/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/

 package net.project.schedule;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import net.project.base.RecordStatus;
import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.Finder;
import net.project.calendar.PnCalendar;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentManager;
import net.project.resource.AssignmentType;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.calc.TaskCalculationType;
import net.project.security.SessionManager;
import net.project.util.DateUtils;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.util.Validator;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.apache.log4j.Logger;

/**
 * The task data access object is responsible for loading information from the
 * database regarding tasks.
 *
 * @author Matthew Flower
 */
public class TaskFinder extends Finder {
    public static final ColumnDefinition NAME_COLUMN = new ColumnDefinition("t.task_name", "prm.schedule.task.columndefs.name");
    public static final ColumnDefinition DESCRIPTION_COLUMN = new ColumnDefinition("t.task_desc", "prm.schedule.task.columndefs.description");
    public static final ColumnDefinition TYPE_COLUMN = new ColumnDefinition("t.task_type", "prm.schedule.task.columndefs.type");
    public static final ColumnDefinition PRIORITY_COLUMN = new ColumnDefinition("t.priority", "prm.schedule.task.columndefs.priority");
    public static final ColumnDefinition DURATION_COLUMN = new ColumnDefinition("t.duration", "prm.schedule.task.columndefs.duration");
    public static final ColumnDefinition DURATION_UNITS_COLUMN = new ColumnDefinition("t.duration_units", "prm.schedule.task.columndefs.durationunits");
    public static final ColumnDefinition WORK_COLUMN = new ColumnDefinition("t.work", "prm.schedule.task.columndefs.work");
    public static final ColumnDefinition WORK_UNITS_COLUMN = new ColumnDefinition("t.work_units", "prm.schedule.task.columndefs.workunits");
    public static final ColumnDefinition WORK_COMPLETE_COLUMN = new ColumnDefinition("t.work_complete", "prm.schedule.task.columndefs.workcomplete");
    public static final ColumnDefinition WORK_COMPLETE_UNITS_COLUMN = new ColumnDefinition("t.work_complete_units", "prm.schedule.task.columndefs.workcompleteunits");
    public static final ColumnDefinition DATE_START_COLUMN = new ColumnDefinition("t.date_start", "prm.schedule.task.columndefs.datestart");
    public static final ColumnDefinition DATE_FINISH_COLUMN = new ColumnDefinition("t.date_finish", "prm.schedule.task.columndefs.datefinish");
    public static final ColumnDefinition TASK_ID_COLUMN = new ColumnDefinition("t.task_id", "prm.schedule.task.columndefs.taskid");
    public static final ColumnDefinition ACTUAL_START_COLUMN = new ColumnDefinition("t.actual_start", "prm.schedule.task.columndefs.actualstart");
    public static final ColumnDefinition ACTUAL_FINISH_COLUMN = new ColumnDefinition("t.actual_finish", "prm.schedule.task.columndefs.actualfinish");
    public static final ColumnDefinition WORK_PERCENT_COMPLETE_COLUMN = new ColumnDefinition("t.work_percent_complete", "prm.schedule.task.columndefs.workpercentcomplete");
    public static final ColumnDefinition DATE_CREATED_COLUMN = new ColumnDefinition("t.date_created", "prm.schedule.task.columndefs.datecreated");
    public static final ColumnDefinition DATE_MODIFIED_COLUMN = new ColumnDefinition("t.date_modified", "prm.schedule.task.columndefs.datemodified");
    public static final ColumnDefinition MODIFIED_BY_COLUMN = new ColumnDefinition("t.modified_by", "prm.schedule.task.columndefs.modifiedby");
    public static final ColumnDefinition PARENT_TASK_ID_COLUMN = new ColumnDefinition("t.parent_task_id", "prm.schedule.task.columndefs.parenttaskid");
    public static final ColumnDefinition PARENT_TASK_NAME_COLUMN = new ColumnDefinition("parent_task_name", "prm.schedule.task.columndefs.parenttaskname");
    public static final ColumnDefinition SEQUENCE_NUMBER_COLUMN = new ColumnDefinition("t.seq", "prm.schedule.task.columndefs.sequencenumber");
    public static final ColumnDefinition PERCENT_COMPLETE_COLUMN = new ColumnDefinition("t.percent_complete", "prm.schedule.task.columndefs.percentcomplete");
    public static final ColumnDefinition CALCULATION_TYPE = new ColumnDefinition("t.calculation_type_id", "prm.schedule.task.columndefs.calculationtype");
    public static final ColumnDefinition PHASE_ID_COLUMN = new ColumnDefinition("ph.phase_id", "");
    public static final ColumnDefinition MILESTONE_NAME_COLUMN = new ColumnDefinition("t.task_name", "prm.schedule.milestone.columndefs.name");
    public static final ColumnDefinition IS_MILESTONE = new ColumnDefinition("t.is_milestone", "");

    /** Indicates whether to pre load dependencies before loading tasks. */
    private boolean preloadDependencies = false;
    /** Map of task ids to predecessor task dependencies. */
    private MultiMap predecessorMap;
    /** Map of task ids to successor task dependencies. */
    private MultiMap successorMap;
    /** Indicates whether to pre load assignments before loading tasks. */
    private boolean preloadAssignments = false;
    /** Indicates whether to pre load material assignments before loading tasks. */
    private boolean preloadMaterialAssignments = false;
    
    /** A multimap of task_id to task assignments. */
    private MultiMap assignmentMap;
    /** The space ID for which items are to be preloaded. */
    private String preloadSpaceID;

    /** Boolean indicating whether we should show records whose status isn't 'A' */
    private boolean showActiveTasksOnly = true;

    /** If set, we should load all tasks as this certain type. */
    private TaskType type = null;


    /**
     * This is the SQL Statement that we generally use to load tasks.
     */
    private String baseSQLStatement =
        "select " +
        "  t.task_name, t.task_desc, t.task_type, t.priority, " +
        "  t.duration, t.duration_units, t.work, t.work_units, t.work_complete, " +
        "  t.work_complete_units, t.date_start, t.date_finish, t.task_id, " +
        "  t.actual_start, t.actual_finish, t.percent_complete, " +
        "  t.date_created, t.date_modified, t.modified_by, " +
        "  t.parent_task_id, t.record_status, t.critical_path, " +
        "  pt.task_name as parent_task_name, t.constraint_type, " +
        "  t.constraint_date, t.deadline, t.seq, pht.plan_id, " +
        "  t.ignore_times_for_dates, t.early_start, t.late_start, " +
        "  t.early_finish, t.late_finish, ph.phase_id, ph.phase_name, ph.sequence," +
        "  t.work_percent_complete, t.is_milestone, shp.space_id," +
        "  shrd.exported_object_id, shrd.read_only, shbl.space_id as exporting_space_id, " +
        "  spon.name as exporting_space_name, ctv.work, ctv.work_units, " +
        "  ctv.duration, ctv.duration_units, ctv.date_start, ctv.date_finish," +
        "  ctv.baseline_id, t.calculation_type_id, t.unallocated_work_complete," +
        "  t.unallocated_work_complete_unit, t.unassigned_work, t.unassigned_work_units, t.wbs, t.wbs_level," +
        "  cc.code_name " +
        "from " +
        "  pn_space_has_plan shp, " +
        "  pn_plan p, " +
        "  pn_plan_has_task pht, " +
        "  pn_task t, " +
        "  pn_task pt, " +
        "  pn_phase_has_task phht, " +
        "  pn_phase ph," +
        "  pn_shared shrd, " +
        "  pn_shareable shbl, " +
        "  pn_object_name spon, " +
        "  pn_current_task_version ctv, " +
        "  pn_charge_code cc, " +
        "  pn_object_has_charge_code ohcc " +
        "where " +
        "  shp.plan_id = p.plan_id " +
        "  and shp.plan_id = pht.plan_id "+
        "  and p.plan_id = pht.plan_id " +
        "  and pht.task_id = t.task_id " +
        "  and t.parent_task_id = pt.task_id(+) " +
        "  and t.task_id = phht.task_id(+) " +
        "  and phht.phase_id = ph.phase_id(+) " +
        "  and ph.record_status(+) = 'A' " +
        "  and t.task_id = shrd.imported_object_id(+) " +
        "  and shrd.exported_object_id = shbl.object_id(+) " +
        "  and shbl.space_id = spon.object_id(+) " +
        "  and t.task_id = ctv.task_id(+) " +
    	"  and cc.code_id (+) = ohcc.code_id " +
    	"  and ohcc.object_id (+)= t.task_id";

    private static int index = 0;
    private static int TASK_NAME_COL_ID = ++index;
    private static int TASK_DESC_COL_ID = ++index;
    private static int TASK_TYPE_COL_ID = ++index;
    private static int PRIORITY_COL_ID = ++index;
    private static int DURATION_COL_ID = ++index;
    private static int DURATION_UNITS_COL_ID = ++index;
    private static int WORK_COL_ID = ++index;
    private static int WORK_UNITS_COL_ID = ++index;
    private static int WORK_COMPLETE_COL_ID = ++index;
    private static int WORK_COMPLETE_UNITS_COL_ID = ++index;
    private static int DATE_START_COL_ID = ++index;
    private static int DATE_FINISH_COL_ID = ++index;
    private static int TASK_ID_COL_ID = ++index;
    private static int ACTUAL_START_COL_ID = ++index;
    private static int ACTUAL_FINISH_COL_ID = ++index;
    private static int PERCENT_COMPLETE_COL_ID = ++index;
    private static int DATE_CREATED_COL_ID = ++index;
    private static int DATE_MODIFIED_COL_ID = ++index;
    private static int MODIFIED_BY_COL_ID = ++index;
    private static int PARENT_TASK_ID_COL_ID = ++index;
    private static int RECORD_STATUS_COL_ID = ++index;
    private static int CRITICAL_PATH_COL_ID = ++index;
    private static int PARENT_TASK_NAME_COL_ID = ++index;
    private static int CONSTRAINT_TYPE_COL_ID = ++index;
    private static int CONSTRAINT_DATE_COL_ID = ++index;
    private static int DEADLINE_COL_ID = ++index;
    private static int SEQ_COL_ID = ++index;
    private static int PLAN_ID_COL_ID = ++index;
    private static int IGNORE_TIMES_FOR_DATES_COL_ID = ++index;
    private static int EARLY_START_COL_ID = ++index;
    private static int LATE_START_COL_ID = ++index;
    private static int EARLY_FINISH_COL_ID = ++index;
    private static int LATE_FINISH_COL_ID = ++index;
    private static int PHASE_ID_COL_ID = ++index;
    private static int PHASE_NAME_COL_ID = ++index;
    private static int PHASE_SEQUENCE_COL_ID = ++index;
    private static int WORK_PERCENT_COMPLETE_COL_ID = ++index;
    private static int IS_MILESTONE_COL_ID = ++index;
    private static int SPACE_ID_COL_ID = ++index;
    private static int EXTERNAL_TASK_ID_COL = ++index;
    private static int IS_READ_ONLY_EXTERNAL_TASK = ++index;
    private static int EXTERNAL_TASK_SPACE_ID_COL_ID = ++index;
    private static int EXTERNAL_TASK_SPACE_NAME_COL_ID = ++index;
    private static int BASELINE_WORK_COL_ID = ++index;
    private static int BASELINE_WORK_UNITS_COL_ID = ++index;
    private static int BASELINE_DURATION_COL_ID = ++index;
    private static int BASELINE_DURATION_UNITS_COL_ID = ++index;
    private static int BASELINE_START_COL_ID = ++index;
    private static int BASELINE_END_COL_ID = ++index;
    private static int BASELINE_ID = ++index;
    private static int CALCULATION_TYPE_ID = ++index;
    private static int UNALLOCATED_WORK_COMPLETE = ++index;
    private static int UNALLOCATED_WORK_COMPLETE_UNIT = ++index;
    private static int UNASSIGNED_WORK = ++index;
    private static int UNASSIGNED_WORK_UNITS = ++index;
    private static int WBS = ++index;
    private static int WBS_LEVEL = ++index;
    private static int CHARGECODE = ++index;

    /**
     * Get the base sql statement to which additional where clauses, group by,
     * and order by clauses will be added.  This sql statement should always have
     * a where clause, even if it is only where 1=1.
     *
     * @return a <code>String</code> value containing a base sql statement for
     * the finder to append to.
     */
    protected String getBaseSQLStatement() {
        if (showActiveTasksOnly) {
            addWhereClause("t.record_status = 'A'");
        }

        return baseSQLStatement;
    }

    /**
     * This method will cause the following finder to preload task
     * dependencies into the task.  Normally task dependencies are lazy loaded,
     * but sometimes lazy loading causes trouble, for example when large list
     * of tasks are shown.
     *
     * @param spaceID a <code>String</code> containing the primary key of a
     * space.  All task dependencies from this space are going to be loaded into
     * the cache.
     */
    public void preloadDependencies(String spaceID) throws PersistenceException {
        preloadDependencies = true;
        preloadSpaceID = spaceID;
        loadDependencies();
    }

    /**
     * This method will cause assignments to be preloaded into a task instead of
     * being loaded individually for each task.  If there is a large list of
     * tasks, this prevents a cascading query problem where each task causes an
     * additional query to be run, slowing down performance.
     *
     * @param spaceID
     * @throws PersistenceException
     */
    public void preloadAssignments(String spaceID) throws PersistenceException {
        preloadAssignments = true;
        preloadSpaceID = spaceID;
        loadAssignments();
    }
    

    public void preloadMaterialAssignments(String spaceID) throws PersistenceException {
        preloadMaterialAssignments = true;
        preloadSpaceID = spaceID;
        loadMaterialAssignments();
    }

	/**
     * Given a list of Tasks just loaded by this task finder, populate all of
     * the children of the summary tasks.  This will only work properly if all
     * of the schedule entries in a space were loaded.
     *
     * @param scheduleEntries a <code>List</code> of scheduleEntries which
     * should represent all of the schedule entries in this space.  We will
     * populate the "children" list of all Summary Tasks in this list.
     */
    public static void populateParents(List scheduleEntries) {
        Map taskMap = new HashMap();

        //First, create a task map from the task list.
        for (Iterator it = scheduleEntries.iterator(); it.hasNext();) {
            ScheduleEntry scheduleEntry = (ScheduleEntry)it.next();
            taskMap.put(scheduleEntry.getID(), scheduleEntry);
        }

        populateParents(taskMap);
    }

    /**
     * Given a Map of schedule entry id to schedule entries, populate all of
     * the children of the summary tasks.  This will only work properly if all
     * of the schedule entries in a space were loaded.
     *
     * @param taskMap a <code>List</code> of scheduleEntries which
     * should represent all of the schedule entries in this space.  We will
     * populate the "children" list of all Summary Tasks in this list.
     */
    public static void populateParents(Map taskMap) {
        for (Iterator it = taskMap.values().iterator(); it.hasNext();) {
            ScheduleEntry child = (ScheduleEntry)it.next();

            if (child instanceof SummaryTask) {
                //Make sure that we have a linked list for the "getChildren"
                //method.  If we don't, every time someone tries to access that
                //method it will try to reload the children which is specifically
                //what we are trying to avoid.
                if (((SummaryTask)child).getChildrenNoLazyLoad() == null) {
                    ((SummaryTask)child).setChildren(new LinkedHashSet());
                }
            }

            if (child.hasParent()) {
                SummaryTask parentTask = (SummaryTask)taskMap.get(child.getParentTaskID());

                //It is unlikely that the parent task is null, but a customer has
                //reported an exception where this is true.
                if (parentTask != null) {
                    if (parentTask.getChildrenNoLazyLoad() == null) {
                        parentTask.setChildren(new LinkedHashSet());
                    }

                    parentTask.getChildrenNoLazyLoad().add(child);
                }
            }
        }
    }

    /**
     * This method loads any tasks for a given space id which were not completed
     * in the specified amount of time.
     *
     * @param spaceID a <code>String</code> value which contains the id of a
     * space which contains a schedule in which we are looking for late tasks.
     */
    public List findLateTasks(String spaceID) throws PersistenceException {
        //Preload dependencies to improve performance on large result sets
        preloadDependencies(spaceID);

        //Get the current date
        PnCalendar cal = new PnCalendar(SessionManager.getUser());
        cal.setTime(new Date());
        cal.add(PnCalendar.DATE, -1);
        String yesterdayString = DateUtils.getDatabaseDateString(cal.startOfDay());

        clearWhereClauses();
        addWhereClause(" t.work_percent_complete < 100 ");
        addWhereClause(" t.date_finish < " + yesterdayString);
        addWhereClause(" shp.space_id = " + spaceID);
        return loadFromDB();
    }

    /**
     * This method finds any tasks for a given space id which are coming due at
     * some point in the future.
     *
     * @param spaceID
     * @return a <code>List</code> object containing one or more tasks that
     * are coming due in the future.
     * @throws PersistenceException if an error occurs while loading the tasks.
     */
    public List findComingDueTasks(String spaceID) throws PersistenceException {
        //Preload dependencies to improve performance on large result sets
        preloadDependencies(spaceID);

        //Get the start of the current day
        PnCalendar cal = new PnCalendar(SessionManager.getUser());
        cal.setTime(new Date());
        String todayString = DateUtils.getDatabaseDateString(cal.startOfDay());

        clearWhereClauses();
        addWhereClause(" t.date_finish >= " + todayString);
        addWhereClause(" t.work_percent_complete < 100");
        addWhereClause(" (t.task_type = 'task' or t.task_type = 'summary') ");
        addWhereClause(" shp.space_id = " + spaceID);
        return loadFromDB();
    }

    /**
     * This method loads any tasks for a given space id which were not completed
     * in the specified amount of time.
     *
     * @param spaceID a <code>String</code> value which contains the id of a
     * space which contains a schedule in which we are looking for late tasks.
     */
    public List findMilestoneTasks(String spaceID) throws PersistenceException {
        //Preload dependencies to improve performance on large result sets
        preloadDependencies(spaceID);

        //Get the current date
        PnCalendar cal = new PnCalendar(SessionManager.getUser());
        cal.setTime(new Date());
        cal.add(PnCalendar.DATE, -1);
        String yesterdayString = DateUtils.getDatabaseDateString(cal.startOfDay());

        clearWhereClauses();
        addWhereClause(" t.is_milestone = 1 ");
        addWhereClause(" t.date_finish < " + yesterdayString);
        addWhereClause(" shp.space_id = " + spaceID);
        return loadFromDB();
    }

    /**
     * Find a certain task with a given ID.
     *
     * @param id a <code>String</code> value which is the primary key of the
     * task you wish to load.
     * @return a <code>List</code> of <code>Task</code> objects that were loaded
     * in response to that id.  (There had better only be one or zero items in
     * the list, otherwise, we have problems.)
     * @throws PersistenceException if there is a problem loading tasks from the
     * database.  This should mostly be of the unexpected error ilk, this shouldn't
     * normally happen.
     */
    public List findByID(String id) throws PersistenceException {
        clearWhereClauses();
        addWhereClause(" t.task_id = " + id);
        return loadFromDB();
    }

    /**
     * Find a certain task with a given ID.
     *
     * @param id a <code>String</code> value which is the primary key of the
     * task you wish to load.
     * @return a <code>List</code> of <code>Task</code> objects that were loaded
     * in response to that id.  (There had better only be one or zero items in
     * the list, otherwise, we have problems.)
     * @throws PersistenceException if there is a problem loading tasks from the
     * database.  This should mostly be of the unexpected error ilk, this shouldn't
     * normally happen.
     */
    public ScheduleEntry findObjectByID(String id) throws PersistenceException {
        List tasks = findByID(id);
        return (ScheduleEntry) getAssertedSingleResult(tasks, "id = " + id);
    }

    /**
     * Find a certain task with a given ID.
     *
     * @param id a <code>String</code> value which is the primary key of the
     * task you wish to load.
     * @param isLoadPredecessors true if predecessors should be loaded
     * @param isLoadAssignments true if assignments should be loaded
     * @return a <code>List</code> of <code>Task</code> objects that were loaded
     * in response to that id.  (There had better only be one or zero items in
     * the list, otherwise, we have problems.)
     * @throws PersistenceException if there is a problem loading tasks from the
     * database.  This should mostly be of the unexpected error ilk, this shouldn't
     * normally happen.
     */
    public ScheduleEntry findObjectByID(String id, boolean isLoadPredecessors, boolean isLoadAssignments, boolean isLoadMaterialAssignments) throws PersistenceException {
        List tasks = findByID(id, isLoadPredecessors, isLoadAssignments, isLoadMaterialAssignments);
        return (ScheduleEntry) getAssertedSingleResult(tasks, "id = " + id);
    }

    /**
     * Asserts that there is at most one result in the given list and returns it.
     * @param results the results to check
     * @param criteria the criteria of the item
     * @return the single result, or null if the results are empty
     * @throws RuntimeException if there is more than one result
     */
    private Object getAssertedSingleResult(List results, String criteria) {
        if (results.size() > 1) {
            throw new RuntimeException("Expected at most one result but found " + results.size() + " with criteria: " + criteria);
        } else if (results.size() == 1) {
            return (ScheduleEntry)results.get(0);
        } else {
            return null;
        }
    }

    /**
     * Find a certain task with a given ID.  Furthermore, load this task as a
     * certain type, even if it isn't that type.  For example, load a task as a
     * milestone, even if it isn't.
     *
     * @param id a <code>String</code> value which is the primary key of the
     * task you wish to load.
     * @return a <code>List</code> of <code>Task</code> objects that were loaded
     * in response to that id.  (There had better only be one or zero items in
     * the list, otherwise, we have problems.)
     * @throws PersistenceException if there is a problem loading tasks from the
     * database.  This should mostly be of the unexpected error ilk, this shouldn't
     * normally happen.
     */
    public List findByID(String id, TaskType type) throws PersistenceException {
        clearWhereClauses();
        addWhereClause(" t.task_id = " + id);
        this.type = type;
        List toReturn = loadFromDB();
        this.type = null;
        return toReturn;
    }

    /**
     * Find a certain task with a given ID.
     *
     * @param id a <code>String</code> value which is the primary key of the
     * task you wish to load.
     * @return a <code>List</code> of <code>Task</code> objects that were loaded
     * in response to that id.  (There had better only be one or zero items in
     * the list, otherwise, we have problems.)
     * @throws PersistenceException if there is a problem loading tasks from the
     * database.  This should mostly be of the unexpected error ilk, this shouldn't
     * normally happen.
     */
    public List findByID(String id, boolean loadPredecessors, boolean loadAssignments, boolean loadMaterialsAssignments) throws PersistenceException {
        List entries = findByID(id);

        //If an entry was found we should preload predecessors and assignments.
        if (!entries.isEmpty()) {
            ScheduleEntry se = (ScheduleEntry)entries.get(0);
            if (loadPredecessors) {
                se.getPredecessors();
            }
            if (loadAssignments) {
                se.loadAssignments();
            }
            if(loadMaterialsAssignments)
            	se.loadMaterialAssignments();
        }

        return entries;
    }

    /**
     * Find all tasks that match a list of task ids
     *
     * @param idList a <code>List</code> object containing one or more strings
     * containing task ids.
     * @return a <code>List</code> object containing zero or more task objects.
     */
    public List findByIDList(List idList) throws PersistenceException {
        addWhereClause(TaskFinder.TASK_ID_COLUMN.getColumnName() + " in (" +
            DatabaseUtils.collectionToCSV(idList) + ")");
        return loadFromDB();
    }

    /**
     * Find a task by its plan ID and sequence number.  Not that this is
     * inherently dangerous -- sequence numbers change.
     *
     * @param planID a <code>String</code> containing the plan id we are
     * searching for.
     * @param sequenceNumber a <code>int</code> containing the sequence number
     * we are searching for.
     * @return a <code>List</code> with zero or more tasks.
     * @throws PersistenceException if there is an error loading the task.
     */
    public List findBySequenceNumber(String planID, int sequenceNumber) throws PersistenceException {
        addWhereClause(" p.plan_id = "+planID+" ");
        addWhereClause(" t.seq = "+sequenceNumber);

        return loadFromDB();
    }

    /**
     * Find all tasks that exist in a certain space ID.
     *
     * @param spaceID a <code>String</code> value in which we are going to look
     * for tasks.
     * @return a <code>List</code> of <code>Task</code> objects that were loaded
     * in response to that id.
     * @throws PersistenceException if there is a problem loading tasks from the
     * database.  This should mostly be of the unexpected error ilk, this shouldn't
     * normally happen.
     */
    public List findBySpaceID(String spaceID) throws PersistenceException {
        //Preload dependencies and assignments to improve performance on large
        //result sets.  The effect should be negligible on small result sets
        preloadDependencies(spaceID);
        preloadAssignments(spaceID);
        preloadMaterialAssignments(spaceID);
        clearWhereClauses();
        addWhereClause(" shp.space_id = " + spaceID);
        return loadFromDB();
    }

    /**
     * Populate a given task based on information found with find by id.
     * @param id a <code>String</code> value containing the primary key of the
     * task id we want to load from the database.
     * @param taskToPopulate a <code>Task</code> object that will be populated
     * with data if the task if found.
     */
    protected boolean findByID(String id, ScheduleEntry taskToPopulate, boolean loadActiveTasksOnly, boolean loadAssignments, boolean loadMaterialAssignments) throws PersistenceException {
        this.showActiveTasksOnly = loadActiveTasksOnly;
        clearWhereClauses();
        addWhereClause(" t.task_id = " + id);
        boolean taskFound = false;
        DBBean db = new DBBean();

        try {
            db.prepareStatement(getSQLStatement());
            db.executePrepared();

            if (db.result.next()) {
                populateTask(db.result, taskToPopulate);
                taskFound = true;
            } else {
                taskFound = false;
            }

            if (loadAssignments) {
                taskToPopulate.loadAssignments();
            }
            if (loadMaterialAssignments){
            	taskToPopulate.loadMaterialAssignments();
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(TaskFinder.class).debug("An unexpected SQL Exception has occurred: " + sqle);
            throw new PersistenceException("Unable to load task", sqle);
        } finally {
            db.release();
        }

        return taskFound;
    }

    /**
     * Finds schedule entries matching the specified parameters.
     * <p>
     * This method actually only expects to load <code>Task</code> entries or
     * entries which are sub-classes of <code>Task</code>.  There are currently
     * no <code>IScheduleEntry</code> objects that are not <code>Task</code>s.
     * </p>
     * @param spaceID the space for which to find schedule entries
     * @param filterActiveItemsOnly when true, cancelled and completed schedule entries
     * are excluded. When false, no entries are excluded.  When this parameter
     * is specified, <code>filterStatusID</code> is typically specified as <code>null</code>
     * @param hierarchyView currently has no effect
     * @param maximumEntries the maximum number of entries to return
     * @param isLoadDependencies true if dependencies should be loaded with
     * each schedule entry; false if no dependencies are to be loaded.  Loading
     * dependencies requires additional database round trips.
     * @param isLoadAssignments true if assignments should be loaded with each
     * schedule entry; false if no assignments are to be loaded.  Loading
     * assignments requires additional database round trips.
     * @param db a <code>DBBean</code> which will be used to load the tasks.
     * @return the list where each element is a <code>Task</code>.
     * @throws SQLException if there is a problem loading the schedule
     * entries.
     */
    public List findScheduleEntries(String spaceID, boolean filterActiveItemsOnly, int hierarchyView, int maximumEntries, boolean isLoadDependencies, boolean isLoadAssignments, DBBean db) throws SQLException {

        clearWhereClauses();

        //Preload dependencies and assignments to improve performance on large
        //result sets.  The effect should be negligible on small result sets
        try {
            if (isLoadDependencies) {
                preloadDependencies(spaceID);
            }

            if (isLoadAssignments) {
                preloadAssignments(spaceID);
            }
        } catch (PersistenceException e) {
            Logger.getLogger(TaskFinder.class).error("Unable to preload assignments or dependencies", e);
            throw new SQLException("Unable to load assignments or dependencies");
        }

        whereClauses.add("shp.space_id = " + spaceID);

        if (filterActiveItemsOnly) {
            whereClauses.add("t.work_percent_complete < 100 ");
        }

        if (maximumEntries >= 0) {
            whereClauses.add("rownum <= " + maximumEntries + " ");
        }

        return loadFromDB(db);
    }

    /**
     * Load all <code>Task</code> objects that match the user's criteria from
     * the database into a <code>List</code> structure.
     *
     * @return a <code>List</code> value contain 0 or more <code>Task</code>
     * objects.
     * @throws PersistenceException when a database error occurs while attempting
     * to load tasks.
     */
    public List findAll() throws PersistenceException {
        clearWhereClauses();
        return loadFromDB();
    }

    /**
     * Populate a task object based on the current row of the recordset.
     *
     * @param result a <code>ResultSet</code> object which is currently pointing
     * to a record in a database result set that contains information about a
     * task.  The result set point will not be modified in any way, only values
     * will be extracted.
     * @param task a task object to fill with data.
     * @throws SQLException when an error occurs while trying to populate
     * <code>Task</code> objects.
     */
    private void populateTask(ResultSet result, ScheduleEntry task) throws SQLException {
        //Populate our new tasks will all of the appropriate fields.
        task.setName(result.getString(TASK_NAME_COL_ID));
        task.setDescription(result.getString(TASK_DESC_COL_ID));
        task.setPriority(result.getInt(PRIORITY_COL_ID));
        task.setID(result.getString(TASK_ID_COL_ID));
        task.setWork(new TimeQuantity(result.getDouble(WORK_COL_ID), TimeQuantityUnit.getForID(result.getInt(WORK_UNITS_COL_ID))));
        task.setWorkComplete(new TimeQuantity(result.getDouble(WORK_COMPLETE_COL_ID), TimeQuantityUnit.getForID(result.getInt(WORK_COMPLETE_UNITS_COL_ID))));
        if (task.getWorkTQ().isZero()) {
            BigDecimal workPercentComplete = result.getBigDecimal(WORK_PERCENT_COMPLETE_COL_ID).divide(new BigDecimal(100), 5, BigDecimal.ROUND_HALF_UP);
            if (workPercentComplete.scale() < 5) {
                workPercentComplete = workPercentComplete.setScale(5);
            }
            task.setWorkPercentComplete(workPercentComplete);
        }

        task.startTime = DatabaseUtils.makeDate(result.getTimestamp(DATE_START_COL_ID));
        task.endTime = DatabaseUtils.makeDate(result.getTimestamp(DATE_FINISH_COL_ID));
        task.setIgnoreTimePortionOfDate(result.getBoolean(IGNORE_TIMES_FOR_DATES_COL_ID));
        task.phaseID = result.getString(PHASE_ID_COL_ID);
        task.phaseName = result.getString(PHASE_NAME_COL_ID);
        task.phaseSequence = result.getInt(PHASE_SEQUENCE_COL_ID);

        // Construct the duration as a TimeQuantity
        TimeQuantityUnit durationUnits = TimeQuantityUnit.getForID(result.getString(DURATION_UNITS_COL_ID));
        task.setDuration(new TimeQuantity(new BigDecimal(result.getString(DURATION_COL_ID)), durationUnits));

        task.actualStart = DatabaseUtils.makeDate(result.getTimestamp(ACTUAL_START_COL_ID));
        task.actualFinish = DatabaseUtils.makeDate(result.getTimestamp(ACTUAL_FINISH_COL_ID));
        task.dateCreated = DatabaseUtils.makeDate(result.getTimestamp(DATE_CREATED_COL_ID));
        task.dateModified = DatabaseUtils.makeDate(result.getTimestamp(DATE_MODIFIED_COL_ID));
        task.modifiedById = result.getInt(MODIFIED_BY_COL_ID);
        task.setParentTaskID(result.getString(PARENT_TASK_ID_COL_ID));
        task.setParentTaskName(result.getString(PARENT_TASK_NAME_COL_ID));
        task.setCriticalPath(result.getBoolean(CRITICAL_PATH_COL_ID));
        task.setSequenceNumber(result.getInt(SEQ_COL_ID));
        task.setPlanID(result.getString(PLAN_ID_COL_ID));
        task.setEarliestStartDate(DatabaseUtils.makeDate(result.getTimestamp(EARLY_START_COL_ID)));
        task.setEarliestFinishDate(DatabaseUtils.makeDate(result.getTimestamp(EARLY_FINISH_COL_ID)));
        task.setLatestStartDate(DatabaseUtils.makeDate(result.getTimestamp(LATE_START_COL_ID)));
        task.setLatestFinishDate(DatabaseUtils.makeDate(result.getTimestamp(LATE_FINISH_COL_ID)));
        if (result.getString(PERCENT_COMPLETE_COL_ID) != null && task.getWorkTQ().getAmount().signum() == 0) {
            task.setPercentCompleteInt(result.getInt(PERCENT_COMPLETE_COL_ID));
        }
        task.setMilestone(result.getBoolean(IS_MILESTONE_COL_ID));
        task.setSpaceID(result.getString(SPACE_ID_COL_ID));
        task.setFromShare(result.getString(EXTERNAL_TASK_ID_COL) != null);
        task.setShareReadOnly(result.getBoolean(IS_READ_ONLY_EXTERNAL_TASK));
        task.setSharedObjectID(result.getString(EXTERNAL_TASK_ID_COL));
        task.setSharingSpaceID(result.getString(EXTERNAL_TASK_SPACE_ID_COL_ID));
        task.setSharingSpaceName(result.getString(EXTERNAL_TASK_SPACE_NAME_COL_ID));
        task.setBaselineID(result.getString(BASELINE_ID));
        task.setBaselineStart(DatabaseUtils.makeDate(result.getTimestamp(BASELINE_START_COL_ID)));
        task.setBaselineEnd(DatabaseUtils.makeDate(result.getTimestamp(BASELINE_END_COL_ID)));
        if (result.getString(BASELINE_WORK_COL_ID) != null) {
            task.setBaselineWork(new TimeQuantity(result.getDouble(BASELINE_WORK_COL_ID), TimeQuantityUnit.getForID(result.getInt(BASELINE_WORK_UNITS_COL_ID))));
        }
        if (result.getString(BASELINE_DURATION_COL_ID) != null) {
            // Construct the baseline duration as a TimeQuantity
            TimeQuantityUnit basseLineDurationUnits = TimeQuantityUnit.getForID(result.getString(BASELINE_DURATION_UNITS_COL_ID));
            task.setBaselineDuration(new TimeQuantity(new BigDecimal(result.getString(BASELINE_DURATION_COL_ID)), basseLineDurationUnits));
        }

        if (!Validator.isBlankOrNull(preloadSpaceID)) {
            task.spaceID = preloadSpaceID;
        }

        //Populate the contained task constraint from the finder
        if (result.getString(CONSTRAINT_TYPE_COL_ID) != null) {
            task.setConstraintDate(DatabaseUtils.makeDate(result.getTimestamp(CONSTRAINT_DATE_COL_ID)));
            task.setDeadline(DatabaseUtils.makeDate(result.getTimestamp(DEADLINE_COL_ID)));
            task.setConstraintType(TaskConstraintType.getForID(result.getString(CONSTRAINT_TYPE_COL_ID)));
        }

        task.setTaskCalculationType(TaskCalculationType.forID(result.getString(CALCULATION_TYPE_ID)));
        task.setUnallocatedWorkComplete(DatabaseUtils.getTimeQuantity(result, UNALLOCATED_WORK_COMPLETE, UNALLOCATED_WORK_COMPLETE_UNIT));
        task.setUnassociatedWorkComplete(DatabaseUtils.getTimeQuantity(result, UNASSIGNED_WORK, UNASSIGNED_WORK_UNITS));

        //Preload the dependencies, if necessary
        if (preloadDependencies) {
            PredecessorList pl = new PredecessorList();
            pl.addAll(findPredecessorsByTaskID(task.getID()));
            pl.setTaskID(task.getID());
            //Tell the dependency list that it is loaded so it won't try to
            //reload itself.
            pl.setLoaded(true);
            pl.setLastSaveState();
            task.setPredecessors(pl);

            SuccessorList sl = new SuccessorList();
            sl.addAll(findSuccessorsByTaskID(task.getID()));
            sl.setLoaded(true);
            task.setSuccessors(sl);
        }

        //Preload the assignments, if necessary
        if (preloadAssignments) {
            task.addAssignments(findAssignmentByTaskID(task.getID()));
            task.setAssigneesLoaded(true);
        }

        task.setRecordStatus(RecordStatus.findByID(result.getString(RECORD_STATUS_COL_ID)));
        task.setWBS(result.getString(WBS));
        task.setWBSLevel(result.getString(WBS_LEVEL));
        task.setChargeCodeName(result.getString(CHARGECODE));
        task.isLoaded = true;
    }

    /**
     * Populate a domain object which data specific to the query result.  For
     * example, a task finder would populate a {@link Task}
     * object.  Any class that extends the finder base class needs to implement
     * this method the finder can use its build-in loadFromDB method to load
     * objects.
     *
     * @param databaseResults a <code>ResultSet</code> that provides the data
     * necessary to populate the domain object.
     * @return a <code>Object</code> subclass specific to your finder that has
     * been populated with data.
     * @throws SQLException if an error occurs populating the object.
     */
    protected Object createObjectForResultSetRow(ResultSet databaseResults) throws SQLException {
        ScheduleEntry scheduleEntry = null;
        if (type == null) {
            scheduleEntry = ScheduleEntryFactory.createFromType(TaskType.getForID(databaseResults.getString(TASK_TYPE_COL_ID)));
        } else {
            scheduleEntry = ScheduleEntryFactory.createFromType(type);
        }

        populateTask(databaseResults, scheduleEntry);
        return scheduleEntry;
    }

    /**
     * This method is called by the {@link #preloadDependencies} method to load
     * dependencies into a cache.
     */
    private void loadDependencies() throws PersistenceException {
        TaskDependencyFinder finder = new TaskDependencyFinder();
        Collection dependencies = finder.findBySpaceID(preloadSpaceID);

        //Create two multimaps, one mapping taskID's to predecessors, the other
        //maps taskID's to successors
        predecessorMap = new MultiHashMap();
        successorMap = new MultiHashMap();

        for (Iterator it = dependencies.iterator(); it.hasNext();) {
            TaskDependency td = (TaskDependency) it.next();
            predecessorMap.put(td.getTaskID(), td);
            successorMap.put(td.getDependencyID(), td);
        }
    }

    /**
     * Find all task dependencies which match a given task ID.  This method is
     * used by the dependencies cache to preload dependencies.
     *
     * @param taskID a <code>String</code> value containing the primary key of
     * a task we are going to load.
     * @return a <code>Collection</code> of the predecessors of the task.
     */
    private Collection findPredecessorsByTaskID(String taskID) {
        Collection toReturn = Collections.EMPTY_LIST;

        if (predecessorMap != null) {
            toReturn = (Collection)predecessorMap.get(taskID);
            if (toReturn == null) {
                toReturn = Collections.EMPTY_LIST;
            }
        }

        return toReturn;
    }

    /**
     * Find all tasks that start after a task with a given task id.  This is
     * used to populate the task with successor while loading.
     *
     * @param taskID a <code>String</code> containing the task id for which we
     * are looking for successors.
     * @return a <code>Collection</code> of the successors for this task.
     */
    private Collection findSuccessorsByTaskID(String taskID) {
        Collection toReturn = Collections.EMPTY_LIST;
        if (successorMap != null) {
            toReturn = (Collection)successorMap.get(taskID);
            if (toReturn == null) {
                toReturn = Collections.EMPTY_LIST;
            }
        }

        return toReturn;
    }

    /**
     * Loads the assignments for the current <code>preloadSpaceID</code> and
     * populates the <code>assignments</code> collection.
     * @throws PersistenceException if there is a problem loading
     */
    private void loadAssignments() throws PersistenceException {
        AssignmentManager assignmentManager = new AssignmentManager();
        assignmentManager.setSpaceID(preloadSpaceID);
        assignmentManager.setAssignmentTypeFilter(AssignmentType.TASK);
        assignmentManager.loadAssignments();

        Collection assignments = assignmentManager.getAssignments();
        assignmentMap = new MultiHashMap(assignments.size());

        for (Iterator it = assignments.iterator(); it.hasNext();) {
            ScheduleEntryAssignment assn = (ScheduleEntryAssignment) it.next();
            assignmentMap.put(assn.getObjectID(), assn);
        }
    }
    
    
    private void loadMaterialAssignments() {
		
	}

    /**
     * Returns a collection of <code>ScheduleEntryAssignment</code>s where
     * each assignment belongs to the task with the specified ID.
     * @param taskID the ID of the task for which assignments are required
     * @return a collection where each element is a <code>ScheduleEntryAssignment</code>;
     * each assignment's objectID is equal to the specified taskID
     */
    private Collection findAssignmentByTaskID(String taskID) {
        Collection toReturn = Collections.EMPTY_LIST;

        if (assignmentMap != null) {
            toReturn = (Collection)assignmentMap.get(taskID);
            if (toReturn == null) {
                toReturn = Collections.EMPTY_LIST;
            }
        }

        return toReturn;
    }
}


