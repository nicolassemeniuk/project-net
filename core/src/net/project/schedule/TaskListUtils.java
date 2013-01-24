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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.project.base.PnetRuntimeException;
import net.project.base.finder.EmptyFinderFilter;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.persistence.PersistenceException;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.calc.ScheduleEntryCalculator;
import net.project.util.Conversion;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.TimeQuantity;
import net.project.util.Validator;

import org.apache.log4j.Logger;

public class TaskListUtils {
    /**
     * Set the percentage complete of multiple schedule entries.
     *
     * @param idList a <code>String[]</code> of ids for which we are going to
     * change percentage completion.
     * @param percentComplete a <code>int</code> containing the new percentage
     * complete for these schedule entries.
     * @throws net.project.persistence.PersistenceException if there is trouble communicating with the
     * database.
     */
    public static ErrorReporter setWorkPercentComplete(String[] idList, double percentComplete, Schedule schedule) throws PersistenceException {
        ErrorReporter er = new ErrorReporter();
        LinkedList summaryTaskNames = new LinkedList();
        LinkedList readOnlyTaskNames = new LinkedList();

        Map entryMap = schedule.getEntryMap();
        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            for (int i = 0; i < idList.length; i++) {
                ScheduleEntry entry = (ScheduleEntry)entryMap.get(idList[i]);

                if (entry instanceof SummaryTask) {
                    summaryTaskNames.add(entry.getName());
                    continue;
                }
                
                if (entry.isFromShare() && entry.isShareReadOnly()) {
					// can't update entries shared read only
					readOnlyTaskNames.add(entry.getName());
					continue;
				}

                if (percentComplete == 0) {
                    entry.setActualStartTimeD(null);
                    entry.setActualEndTimeD(null);
                }

                if (entry.getWorkTQ().isZero()) {
                    //sjmittal: reverting the change to fix bug-427
                    //don't know what this code was commented but somehow got commented between 
                    //changeset 14722 n 8.2 branch
                    entry.setWorkPercentComplete(new BigDecimal(percentComplete/100f));
                    entry.store(false, schedule, db);
//                    er.addError(PropertyProvider.get("prm.schedule.taskedit.error.toomuchworkcomplete.message"));
                } else {
                    TimeQuantity work = entry.getWorkTQ();
                    TimeQuantity workComplete = work.multiply(new BigDecimal(percentComplete/100f));

                    if (!workComplete.equals(entry.getWorkCompleteTQ())) {
                        ScheduleEntryCalculator calc = new ScheduleEntryCalculator(entry, schedule.getWorkingTimeCalendarProvider());
                        calc.workPercentCompleteChanged(new BigDecimal(percentComplete/100f));
                    }

                    entry.store(false, schedule, db);
                }
            }

            db.commit();
        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException e2) {
                // Throw original error
            }
            throw new PersistenceException("Unable to set percentage complete: "+sqle, sqle);
        } finally {
            db.release();
        }

        if (!summaryTaskNames.isEmpty()) {
            er.addError(PropertyProvider.get("prm.schedule.tasklist.percentcompletenotsupported.message", Conversion.toCommaSeparatedString(summaryTaskNames)));
        }
        if (!readOnlyTaskNames.isEmpty()) {
        	er.addError(PropertyProvider.get("prm.schedule.tasklist.percentcompletereadonly.message", Conversion.toCommaSeparatedString(readOnlyTaskNames)));
        }
        if (schedule.isAutocalculateTaskEndpoints()) {
            schedule.recalculateTaskTimes();
        }
        return er;
    }

    /**
     * Set the phase of the schedule entries in the idlist.
     *
     * @param idList a <code>String[]</code> containing the id's of zero or more
     * schedule entries.
     * @param phaseID a <code>String</code> containing the phase id that is going
     * to be assigned to the schedule entries passed into this method.
     * @throws net.project.persistence.PersistenceException if there is a problem storing the new
     * phases.
     */
    public static void setPhase(String[] idList, String phaseID) throws PersistenceException {
        String idCSVList = DatabaseUtils.collectionToCSV(Arrays.asList(idList));

        DBBean db = new DBBean();
        try {
            //First delete any phase entries for these tasks
            db.executeQuery(
                "delete from pn_phase_has_task " +
                "where task_id in ("+idCSVList+")"
            );

            //Now insert new entries for all of the tasks
            if (!Validator.isBlankOrNull(phaseID)) {
                db.executeQuery(
                    "insert into pn_phase_has_task " +
                    "  (phase_id, task_id)" +
                    "  select "+phaseID+", task_id " +
                    "  from pn_task " +
                    "  where task_id in ("+idCSVList+")"
                );
            }
        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to set phase: "+sqle, sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Moves a task upward in a list of schedule entries that are ordered by
     * sequence number.  This method will "swap" schedule entry with the prior
     * schedule entry at the same hierarchical level.
     *
     * @param idList a <code>String[]</code> of tasks that we are going to
     * promote.
     * @throws net.project.persistence.PersistenceException if there is an error promoting tasks.
     */
    public static void promoteTasks(String[] idList, String planID) throws PersistenceException {
        DBBean db = new DBBean();
        try {
            db.prepareCall("begin SCHEDULE.move_task_up(?,?); end;");

            for (int i = 0; i < idList.length; i++) {
                db.cstmt.setString(1, idList[i]);
                db.cstmt.setString(2, planID);
                db.cstmt.addBatch();
            }

            db.executeCallableBatch();
        } catch (SQLException sqle) {
            throw new PersistenceException("Unexpected error while trying to promote task: " + sqle, sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Moves a task downward in a list of schedule entries that are ordered by
     * sequence number.  This method will "swap" the schedule entry with the
     * next schedule entry that is at the same hierarchical level.
     *
     * @param idList a <code>String[]</code> of ids which we are going to
     * demote.
     * @throws net.project.persistence.PersistenceException if there is an error demoting tasks.
     */
    public static void demoteTasks(String[] idList, Schedule schedule) throws PersistenceException {
        DBBean db = new DBBean();
        try {
            db.prepareCall("begin SCHEDULE.move_task_down(?,?); end;");

            for (int i = 0; i < idList.length; i++) {
                db.cstmt.setString(1, idList[i]);
                db.cstmt.setString(2, schedule.getID());
                db.cstmt.addBatch();
            }

            db.executeCallableBatch();
        } catch (SQLException sqle) {
            throw new PersistenceException("Unexpected error while trying to demote task: " + sqle, sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Make all of the schedule entries in the idList have the parent task id of
     * their current parent.
     *
     * @param idList a <code>String[]</code> of the primary key of schedule
     * entries that are no longer going to have their current parent task.
     * @throws net.project.persistence.PersistenceException if there is a problem updating the database
     * to reflect these changes.  This usually occurs because there is an error
     * in the midst of programming, or because there is a problem with the
     * database.
     */
    public static void unindentTasks(final String[] idList, Schedule schedule) throws PersistenceException {
        //Find the tasks to unindent
        final List<String> idsList = Arrays.asList(idList);
        TaskFinder finder = new TaskFinder();
        finder.addFinderFilter(new EmptyFinderFilter("10", true) {
            public String getWhereClause() {
                return "t.task_id in (" + DatabaseUtils.collectionToCSV(idsList) + ") ORDER BY t.seq";
            }
        });
        List taskToUnindent = finder.findBySpaceID(schedule.getSpace().getID());

        //We need to process the list in reverse order to maintain the same order
        //of tasks that occurred inside any indentation level.
        Collections.reverse(taskToUnindent);

        //Find the parents of the tasks we are unindenting
        finder.clearFinderFilter();
        finder.addFinderFilter(new EmptyFinderFilter("10", true) {
            public String getWhereClause() {
                return "t.task_id in (select parent_task_id from pn_task subt where subt.task_id in (" + DatabaseUtils.collectionToCSV(Arrays.asList(idList)) + "))";
            }
        });
        List parentsOfUnindentTasks = finder.findBySpaceID(schedule.getSpace().getID());
        Map parentTaskMap = convertTaskListToMap(parentsOfUnindentTasks);

        for (Iterator it = taskToUnindent.iterator(); it.hasNext();) {
            ScheduleEntry task = (ScheduleEntry)it.next();
            //sjmittal: for fix for bfd 5699 is parent task of this task also have to be indented then we 
            //only unindent the parent one
            if (Validator.isBlankOrNull(task.getParentTaskID()) || idsList.contains(task.getParentTaskID())) {
                continue;
            }

            ScheduleEntry parentTask = (ScheduleEntry)parentTaskMap.get(task.getParentTaskID());
            task.setParentTaskID(parentTask.getParentTaskID());
            task.setUnindentStatus(true);
            task.store(false, schedule);

            //since the child is indented subtract its work and work complete from the parent task
            TimeQuantity resourceAssignmentWork = TimeQuantity.O_HOURS;
            TimeQuantity assignedResourceActualWork = TimeQuantity.O_HOURS;
            //also subtract the assignment work for this one (fix of bfd 5768)
            Collection assignments = parentTask.getAssignments();
            for (Iterator it2 = assignments.iterator(); it2.hasNext();) {
                ScheduleEntryAssignment assn = (ScheduleEntryAssignment)it2.next();
                resourceAssignmentWork = resourceAssignmentWork.add(assn.getWork());
                assignedResourceActualWork = assignedResourceActualWork.add(assn.getWorkComplete());
            }

            TimeQuantity taskWork = ScheduleTimeQuantity.convertToUnit(task.getWorkTQ(), parentTask.getWorkTQ().getUnits(), 5, BigDecimal.ROUND_HALF_UP);
            TimeQuantity newTaskWork = parentTask.getWorkTQ().subtract(taskWork);

            TimeQuantity taskWorkComplete = ScheduleTimeQuantity.convertToUnit(task.getWorkCompleteTQ(), parentTask.getWorkCompleteTQ().getUnits(), 5, BigDecimal.ROUND_HALF_UP);
            TimeQuantity newTaskWorkComplete = parentTask.getWorkCompleteTQ().subtract(taskWorkComplete);
            
            if(ScheduleTimeQuantity.convertToHour(newTaskWork).subtract(resourceAssignmentWork).getAmount().signum() <= 0) {
                //sjmittal: update the parent tasks work and duration back to orignal 
                //ie when it was last time of type task before indenting the child tasks
                //fix for bfd-3040
                ScheduleEntryHistory history = new ScheduleEntryHistory();
                history.setTaskID(parentTask.getID());
                history.setTaskType(TaskType.TASK.getID());
                history.loadForTaskType();
                ScheduleEntry lastTask = history.getLastHistoryEntry();
                
                // if there exist an history item of last task
                if(lastTask != null) {
    	            //update duration and work
    	            TimeQuantity lastTaskworkTQ = lastTask.getWorkTQ();
                    TimeQuantity lastTaskWorkCompleteTQ = lastTask.getWorkCompleteTQ();
                    //If there are resources assigned to the summary task, we need
                    //to account for their work too.
                    //if resource work and work complete is not zero then update theirs else
                    //update the last tasks
                    parentTask.setWork(resourceAssignmentWork.isZero()? lastTaskworkTQ : resourceAssignmentWork);
                    parentTask.setWorkComplete(assignedResourceActualWork.isZero()? lastTaskWorkCompleteTQ: assignedResourceActualWork);
                    //unallocated work complete to be work complete of last task if there is no actual work complete
                    parentTask.setUnallocatedWorkComplete(assignedResourceActualWork.isZero()? lastTaskWorkCompleteTQ: TimeQuantity.O_HOURS);
                    
    	            parentTask.setDuration(lastTask.getDurationTQ());
    	            //set the start date and the end 
    	            parentTask.setStartTimeD(lastTask.getStartTime());
    	            parentTask.setEndTimeD(lastTask.getEndTime());
                    //store these changes
                    parentTask.store(false, schedule);
                }
            } else {
                //just store the new work and work complete
                parentTask.setWork(newTaskWork);
                parentTask.setWorkComplete(newTaskWorkComplete);
                parentTask.store(false, schedule);
            }
        }

        if (taskToUnindent.size() > 0) {
            DBBean db = new DBBean();
            try {
                db.prepareCall("begin SCHEDULE.FIX_SUMMARY_TASK_TYPES(?); end;");
                db.cstmt.setString(1, schedule.getID());
                db.executeCallable();
            } catch (SQLException sqle) {
                throw new PersistenceException("Unexpected SQL Exception while fixing task types: "+sqle, sqle);
            } finally {
                db.release();
            }
            
            if (schedule.isAutocalculateTaskEndpoints()) {
                schedule.recalculateTaskTimes();
            }
        }
    }

    /**
     * Convert a list of tasks into being a map which maps the task id to the
     * schedule entry.
     *
     * @param listOfTasks a <code>List</code> of <code>ScheduleEntry</code>
     * objects that we are going to convert into a map.
     * @return a <code>Map</code> which relates task id to schedule entry.
     */
    private static Map convertTaskListToMap(List listOfTasks) {
        Map taskMap = new HashMap();

        for (Iterator it = listOfTasks.iterator(); it.hasNext();) {
            ScheduleEntry t = (ScheduleEntry)it.next();
            taskMap.put(t.getID(), t);
        }

        return taskMap;
    }

    /**
     * Make all of the tasks passed into this method children of the parent task
     * indicated by the second parameter.
     *
     * @param idList a <code>String[]</code> of the primary key of schedule
     * entries the parent is going to adopt.
     * @param taskAbove a <code>String</code> containing the primary key of the
     * task that is the task directly above this task in a sequenced list.
     * @return a <code>ErrorReporter</code> object which will let the user know
     * if their indenting has caused a cyclic dependency error.
     * @throws net.project.persistence.PersistenceException if there is a problem updating the database
     * to reflect these changes.  This usually occurs because there is an error
     * in the midst of programming, or because there is a problem with the
     * database.
     */
    public static ErrorReporter indentTasks(final String[] idList, String taskAbove, Schedule schedule) throws PersistenceException {
        Logger logger = Logger.getLogger(TaskListUtils.class);
        ErrorReporter er = new ErrorReporter();

        //Step 1, load the tasks that we are going to indent.
        TaskFinder finder = new TaskFinder();
        finder.addFinderFilter(new EmptyFinderFilter("10", true) {
            public String getWhereClause() {
                return "t.task_id in (" + DatabaseUtils.collectionToCSV(Arrays.asList(idList)) + ") ORDER BY t.seq";
            }
        });
        //load one list again from db as this may be stored in db
        List tasks = finder.findBySpaceID(schedule.getSpace().getID());
        //get the next one from the schedules memory as this would be modified internally 
        TaskList taskList = schedule.getTaskList();
        if (tasks.size() > 0) {
            //Step 2, find the new parent task id for these tasks.
            String parentTaskID = null;
            DBBean db = new DBBean();
            try {
                db.prepareStatement("select SCHEDULE.FIND_NEW_TASK_PARENT(?,?) as parent_task_id from dual");
                db.pstmt.setString(1, idList[0]);
                db.pstmt.setString(2, taskAbove);
                db.executePrepared();

                if (db.result.next()) {
                    parentTaskID = db.result.getString("parent_task_id");
                } else {
                    logger.debug("Unable to find parent task id when indenting tasks.");
                }
            } catch (SQLException sqle) {
                logger.debug("Unable to find parent task id when indenting tasks.");
                throw new PersistenceException("Unable to find parent task id when indenting tasks", sqle);
            } finally {
                db.release();
            }

            //Step 2a: Exclude shared tasks to be parent of some other task of the schedule
            //sjmittal: this is because the imported shared task already depends upon the exported 
            //task or schedule
            List sharedTaskNames = new ArrayList();
            ScheduleEntry parentTask = (ScheduleEntry) taskList.get(parentTaskID);
            if (parentTask.isFromShare() /*&& entry.isShareReadOnly()*/) {
            	sharedTaskNames.add(parentTask.getNameMaxLength40());
            }
            if (!sharedTaskNames.isEmpty()) {
                er.addError(PropertyProvider.get("prm.schedule.main.indenttasks.warning.sharingreadonly", Conversion.toCommaSeparatedString(sharedTaskNames)));
                return er;
            }
            //clone the parentTask as summary task
            SummaryTask summaryTask = null;
            if (!(parentTask instanceof SummaryTask)) {
                summaryTask = new SummaryTask();
                parentTask.setFieldsFromScheduleEntry(summaryTask);
                //replace the old parent task from map
                taskList.remove(parentTaskID);
                taskList.add(summaryTask);
            } 

            //Step 2b: Prepare a cyclic dependency detector to make sure that
            //none of the tasks we are making children are already dependent on
            //this "potential parent".
            CyclicDependencyDetectorV2 crd = new CyclicDependencyDetectorV2();

            //Step 3, Change all tasks to have the new parent id.
            for (Iterator it = tasks.iterator(); it.hasNext();) {
                ScheduleEntry t = (ScheduleEntry)it.next();

                //Check to see if adding this parent would create a cyclic dependency
                //a parent child relationship would be updated in memory
                ScheduleEntry newChild = (ScheduleEntry) taskList.get(t.getID());
                newChild.setParentTaskID(parentTaskID);

                boolean hasCycle = false;
                try {
                    hasCycle = crd.hasCycle(newChild, schedule);
                } catch (Exception e) {
                    //restore back!!
                    newChild.setParentTaskID(null);
                    taskList.remove(parentTaskID);
                    taskList.add(parentTask);
                	logger.debug("Unexpected cycle for parent <- child "+ parentTaskID + " <- " + t.getID());
                    throw new PnetRuntimeException("Unexpected cycle in dependency cycle.  No data has been saved.", e);
                }

                if (hasCycle) {
                    //Let the user know that he can't make this task a child
                    ErrorDescription ed = new ErrorDescription(PropertyProvider.get("prm.schedule.main.indenttasks.error.dependencycycle.message", t.getName(), parentTask.getName()));
                    er.addError(ed);
                    //restore back!!
                    newChild.setParentTaskID(null);
                    taskList.remove(parentTaskID);
                    taskList.add(parentTask);
                } else {
                    //If the summary task was a predecessor before, remove that relationship
                    TaskDependency td = t.getPredecessors().findByDependencyID(parentTaskID);
                    if (td != null) {
                        t.getPredecessors().remove(td);
                        logger.debug("Removing predecessor relationship "+parentTaskID+" <- "+t.getID());
                    }

                    t.setParentTaskID(parentTaskID);
                    t.store(false, schedule);
                }
            }

            //Step 4, Make sure that we didn't leave a task without any children as a summary task.
            db = new DBBean();
            try {
                db.prepareCall("begin SCHEDULE.FIX_SUMMARY_TASK_TYPES(?); end;");
                db.cstmt.setString(1, schedule.getID());
                db.executeCallable();
            } catch (SQLException sqle) {
                throw new PersistenceException("Unexpected SQL Exception while fixing task types: "+sqle, sqle);
            } finally {
                db.release();
            }

            //Step 5, make sure that we didn't mess up the start or end dates of
            //any tasks as a result of our changes.
            //sjmittal fix for bfd 2734. We may need such checks at other places also
            //unfortunately this is not checked at every place where we recalculate task time
            //one generic solution could have been to refcator and include
            //call to isAutocalculateTaskEndpoints inside method recalculateTaskTimes itself.
            if (schedule.isAutocalculateTaskEndpoints()) {
                schedule.recalculateTaskTimes();
            }
        }

        return er;
    }
}
