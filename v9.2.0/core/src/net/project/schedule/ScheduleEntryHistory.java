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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 19063 $
|       $Date: 2009-04-05 14:27:40 -0300 (dom, 05 abr 2009) $
|     $Author: nilesh $
|
|
+--------------------------------------------------------------------------------------*/
package net.project.schedule;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.project.base.RecordStatus;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Task history - This class provides access to the history of changes made to a
 * task object.
 *
 * @author AdamKlatzkin
 * @since 03/00
 */
public class ScheduleEntryHistory {
    /**
     * The id of the schedule entry for which we are looking for the history.
     */
    private String taskID = null;
    /**
     * The type of the schedule entry for which we are looking for the history.
     */
    private String taskType = null;
    /**
     * An array list which contains <code>ScheduleEntry</code> object which
     * represent the history of a <code>ScheduleEntry</code>.
     */
    private List historyEntries = new LinkedList();
    /**
     * A map of schedule entry id's to baseline objects.
     */
    private Map scheduleEntryIDToBaseline = new HashMap();
    private Map baselineIDToBaseline = new HashMap();

    private String baseSQLStatement =
        "select "+
        "  bt.baseline_id, bt.baseline_name, bt.baseline_create_date, " +
        "  t.task_version_id, t.task_name, t.task_desc, t.task_type, t.priority, " +
        "  t.duration, t.duration_units, t.work, t.work_units,   t.work_complete, " +
        "  t.work_complete_units, t.date_start, t.date_finish, t.task_id, " +
        "  t.percent_complete, t.date_created, t.date_modified, t.modified_by, "+
        "  p.display_name, t.parent_task_id, t.record_status, t.critical_path, "+
        "  pt.task_name as parent_task_name, t.constraint_type, t.constraint_date, "+
        "  t.deadline, t.seq, pv.plan_id, t.ignore_times_for_dates, t.early_start, "+
        "  t.late_start, t.early_finish, t.late_finish, t.work_percent_complete, "+
        "  t.is_milestone, bt.work, bt.work_units, bt.duration, bt.duration_units, "+
        "  bt.date_start, bt.date_finish "+
        "from "+
        "  pn_task_version t, "+
        "  pn_task_version pt, "+
        "  pn_plan_version pv, "+
        "  pn_baseline_task bt, "+
        "  pn_person p "+
        "where "+
        "  t.parent_task_version_id = pt.task_version_id(+) "+
        "  and t.plan_version_id = pv.plan_version_id(+) "+
        "  and t.modified_by = p.person_id(+) "+
        "  and t.baseline_id = bt.baseline_id(+) "+
        "  and t.task_id = bt.task_id(+) ";

    private String orderBySQLStatement =
        "order by " +
        "  t.task_version_id ";

    private static int colID = 1;
    private static int BASELINE_ID_COL_ID = colID++;
    private static int BASELINE_NAME_COL_ID = colID++;
    private static int BASELINE_CREATION_COL_ID = colID++;
    private static int TASK_VERSION_COL_ID = colID++;
    private static int TASK_NAME_COL_ID = colID++;
    private static int TASK_DESC_COL_ID = colID++;
    private static int TASK_TYPE_COL_ID = colID++;
    private static int PRIORITY_COL_ID = colID++;
    private static int DURATION_COL_ID = colID++;
    private static int DURATION_UNITS_COL_ID = colID++;
    private static int WORK_COL_ID = colID++;
    private static int WORK_UNITS_COL_ID = colID++;
    private static int WORK_COMPLETE_COL_ID = colID++;
    private static int WORK_COMPLETE_UNITS_COL_ID = colID++;
    private static int DATE_START_COL_ID = colID++;
    private static int DATE_FINISH_COL_ID = colID++;
    private static int TASK_ID_COL_ID = colID++;
    private static int PERCENT_COMPLETE_COL_ID = colID++;
    private static int DATE_CREATED_COL_ID = colID++;
    private static int DATE_MODIFIED_COL_ID = colID++;
    private static int MODIFIED_BY_COL_ID = colID++;
    private static int MODIFIED_BY_DISPLAY_COL_ID = colID++;
    private static int PARENT_TASK_ID_COL_ID = colID++;
    private static int RECORD_STATUS_COL_ID = colID++;
    private static int CRITICAL_PATH_COL_ID = colID++;
    private static int PARENT_TASK_NAME_COL_ID = colID++;
    private static int CONSTRAINT_TYPE_COL_ID = colID++;
    private static int CONSTRAINT_DATE_COL_ID = colID++;
    private static int DEADLINE_COL_ID = colID++;
    private static int SEQ_COL_ID = colID++;
    private static int PLAN_ID_COL_ID = colID++;
    private static int IGNORE_TIMES_FOR_DATES_COL_ID = colID++;
    private static int EARLY_START_COL_ID = colID++;
    private static int LATE_START_COL_ID = colID++;
    private static int EARLY_FINISH_COL_ID = colID++;
    private static int LATE_FINISH_COL_ID = colID++;
    private static int WORK_PERCENT_COMPLETE_COL_ID = colID++;
    private static int IS_MILESTONE_COL_ID = colID++;
    private static int BASELINE_WORK_COL_ID = colID++;
    private static int BASELINE_WORK_UNITS_COL_ID = colID++;
    private static int BASELINE_DURATION_COL_ID = colID++;
    private static int BASELINE_DURATION_UNITS_COL_ID = colID++;
    private static int BASELINE_START_COL_ID = colID++;
    private static int BASELINE_END_COL_ID = colID++;

    /**
     * Get the task id that the history object is currently referencing.
     *
     * @return a <code>String</code> containing the task id.
     */
    public String getTaskID() {
        return taskID;
    }
    
    /**
     * Get the task type that the history object is currently referencing.
     *
     * @return a <code>String</code> containing the task type.
     */
    public String getTaskType() {
        return taskType;
    }

    /**
     * Set the task id of the task whose history you want to obtain.  This
     * method should be called before load().
     *
     * @param id a <code>String</code> containing the task id.
     */
    public void setTaskID(String id) {
        taskID = id;
    }
    
    /**
     * Set the task type of the task whose history you want to obtain.  This
     * method should be called before load().
     *
     * @param id a <code>String</code> containing the task id.
     */
    public void setTaskType(String type) {
        taskType = type;
    }
    
    /**
     * Get the last history object that is currently referencing.
     *
     * @return a <code>ScheduleEntry</code>.
     */
    public ScheduleEntry getLastHistoryEntry() {
        if(historyEntries.size() > 0)
            return (ScheduleEntry) historyEntries.get(historyEntries.size() -1);
        else return null;
    }
    /** 
     * To get the whole hostory list of an assignment
     * @return history list of work capture of assignment 
     */
    public List getAllHistoryEntry() {
        if(historyEntries.size() > 0)
            return historyEntries;
        else return null;
    }
    

    /**
     * Get the XML representation of this schedule.  The XML document will
     * contain the properties of the schedule and nodes for each schedule Task.
     *
     * @return XML representation of the schedule.
     */
    public String getXML() {
        IScheduleEntry entry;
        StringBuffer xml = new StringBuffer();
        Baseline lastBaseline = null;
        Baseline currentBaseline = null;

        xml.append(IXMLPersistence.XML_VERSION);
        if (historyEntries != null && historyEntries.size() > 0) {
            xml.append("<history>\n");

            for (int i = 0; i < historyEntries.size(); i++) {
                currentBaseline = (Baseline)scheduleEntryIDToBaseline.get(((ScheduleEntry)historyEntries.get(i)).getTaskVersionID());
                if (currentBaseline != null && !currentBaseline.equals(lastBaseline)) {
                    lastBaseline = currentBaseline;
                    xml.append(currentBaseline.getXMLBody());
                } else if (currentBaseline == null && i==0) {
                    xml.append("<nobaseline/>");
                }

                entry = (IScheduleEntry)historyEntries.get(i);
                xml.append(entry.getXMLBody());
            }

            xml.append("</history>\n");
        }

        return xml.toString();
    }

    /**
     * Load the history for the set task id.
     *
     * @throws PersistenceException if there is a problem loading any schedule
     * entries from the database.
     */
    public void load() throws PersistenceException {
        historyEntries.clear();

        DBBean db = new DBBean();
        try {
            String query = baseSQLStatement + " and t.task_id = " + taskID + " " + orderBySQLStatement;

            db.executeQuery(query);

            while (db.result.next()) {
                loadTaskEntry(db.result);
            }
        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("could not load baseline tasks", sqle);
        } finally {
            db.release();
        }
    }
    /**
     * Load the history for the set task id and person id.
     * @param personid to filter by personid
     * @throws PersistenceException if there is a problem loading any schedule
     * entries from the database.
     */
    
    public void load(String personId) throws PersistenceException {
        historyEntries.clear();

        DBBean db = new DBBean();
        try {
            String query = baseSQLStatement + " and t.task_id = " + taskID + " and p.person_id = " + personId + " " + orderBySQLStatement;

            db.executeQuery(query);

            while (db.result.next()) {
                loadTaskEntry(db.result);
            }
        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("could not load baseline tasks", sqle);
        } finally {
            db.release();
        }
    }
    
    /**
     * Load the history for the set task id and task type.
     *
     * @throws PersistenceException if there is a problem loading any schedule
     * entries from the database.
     */
    public void loadForTaskType() throws PersistenceException {
        historyEntries.clear();

        DBBean db = new DBBean();
        try {
            String query = baseSQLStatement + " and t.task_id = " + taskID + " " + " and t.task_type = '" + taskType +"'" + orderBySQLStatement;

            db.executeQuery(query);

            while (db.result.next()) {
                loadTaskEntry(db.result);
            }
        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("could not load baseline tasks", sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Load a task enty from the result set.
     *
     * @param result a <code>ResultSet</code> object which is currently pointing
     * to a record for which we are going to populate a schedule entry and save
     * it in the internal list of schedule entries.
     * @throws SQLException if there is a problem populating the task.  This
     * would mostly occur because the wrong name for a column was typed.
     */
    protected void loadTaskEntry(ResultSet result) throws SQLException {
        String taskType = result.getString(TASK_TYPE_COL_ID);

        //Make sure we don't send "milestone" to the factory, that type doesn't
        //exist for normal tasks anymore.
        ScheduleEntry task = ScheduleEntryFactory.createFromType(TaskType.getForID(taskType.equals("milestone") ? "task" : taskType));

        String baselineID = result.getString(BASELINE_ID_COL_ID);
        if (baselineID != null) {
            Baseline b = (Baseline)baselineIDToBaseline.get(baselineID);
            if (b == null) {
                b = new Baseline();
                b.setID(baselineID);
                b.setName(result.getString(BASELINE_NAME_COL_ID));
                b.setCreationDate(DatabaseUtils.getTimestamp(result, BASELINE_CREATION_COL_ID));
            }
            scheduleEntryIDToBaseline.put(result.getString(TASK_VERSION_COL_ID), b);
        }

        //Populate our new tasks will all of the appropriate fields.
        task.setTaskVersionID(result.getString(TASK_VERSION_COL_ID));
        task.setName(result.getString(TASK_NAME_COL_ID));
        task.setDescription(result.getString(TASK_DESC_COL_ID));
        task.setPriority(result.getInt(PRIORITY_COL_ID));
        task.setID(result.getString(TASK_ID_COL_ID));
        task.setWork(new TimeQuantity(result.getBigDecimal(WORK_COL_ID), TimeQuantityUnit.getForID(result.getInt(WORK_UNITS_COL_ID))));
        task.setWorkComplete(new TimeQuantity(result.getBigDecimal(WORK_COMPLETE_COL_ID), TimeQuantityUnit.getForID(result.getInt(WORK_COMPLETE_UNITS_COL_ID))));
        task.startTime = DatabaseUtils.makeDate(result.getTimestamp(DATE_START_COL_ID));
        task.endTime = DatabaseUtils.makeDate(result.getTimestamp(DATE_FINISH_COL_ID));
        task.setIgnoreTimePortionOfDate(result.getBoolean(IGNORE_TIMES_FOR_DATES_COL_ID));

        // Construct the duration as a TimeQuantity
        TimeQuantityUnit durationUnits = TimeQuantityUnit.getForID(result.getString(DURATION_UNITS_COL_ID));
        task.setDuration(new TimeQuantity(result.getBigDecimal(DURATION_COL_ID), durationUnits));

        task.dateCreated = DatabaseUtils.makeDate(result.getTimestamp(DATE_CREATED_COL_ID));
        task.dateModified = DatabaseUtils.makeDate(result.getTimestamp(DATE_MODIFIED_COL_ID));
        task.modifiedBy = result.getString(MODIFIED_BY_DISPLAY_COL_ID);
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
        task.setBaselineStart(DatabaseUtils.makeDate(result.getTimestamp(BASELINE_START_COL_ID)));
        task.setBaselineEnd(DatabaseUtils.makeDate(result.getTimestamp(BASELINE_END_COL_ID)));
        if (result.getString(BASELINE_WORK_COL_ID) != null) {
            task.setBaselineWork(new TimeQuantity(result.getBigDecimal(BASELINE_WORK_COL_ID), TimeQuantityUnit.getForID(result.getInt(BASELINE_WORK_UNITS_COL_ID))));
        }
        if (result.getString(BASELINE_DURATION_COL_ID) != null) {
            task.setBaselineDuration(new TimeQuantity(result.getBigDecimal(BASELINE_DURATION_COL_ID), TimeQuantityUnit.getForID(result.getInt(BASELINE_DURATION_UNITS_COL_ID))));
        }

        //Populate the contained task constraint from the finder
        if (result.getString(CONSTRAINT_TYPE_COL_ID) != null) {
            task.setConstraintDate(DatabaseUtils.makeDate(result.getTimestamp(CONSTRAINT_DATE_COL_ID)));
            task.setDeadline(result.getTimestamp(DEADLINE_COL_ID));
            task.setConstraintType(TaskConstraintType.getForID(result.getString(CONSTRAINT_TYPE_COL_ID)));
        }
        task.setRecordStatus(RecordStatus.findByID(result.getString(RECORD_STATUS_COL_ID)));
        task.isLoaded = true;
        task.isFullXMLBody = false;//sachin: we don't need assignments and predecessors list here

        historyEntries.add(task);
    }
}





