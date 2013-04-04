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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.project.base.PnetRuntimeException;
import net.project.calendar.PnCalendar;
import net.project.calendar.workingtime.DaysWorked;
import net.project.calendar.workingtime.IDaysWorked;
import net.project.calendar.workingtime.IWorkingTimeCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.NoWorkingTimeException;
import net.project.crossspace.interdependency.ExternalDependencies;
import net.project.database.DBBean;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpace;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.calc.IDateCalculator;
import net.project.util.DateUtils;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


/**
 * This class implements the calculation of the end date of tasks based on an
 * analysis of task dependencies and constraints.
 *
 * @author Matthew Flower
 * @since Version 7.6
 */
public class TaskEndpointCalculation {
    private static final Logger logger = Logger.getLogger(TaskEndpointCalculation.class);

    /** A class that would fetch some useful lists based on the schedule for calculation */ 
    private TaskDependenciesFetcher tdf = null;
    /** Maps primary keys of schedule entries to the schedule entries. */
    private Map tasks = new HashMap();
    /** These are tasks that don't have predecessors or that aren't a child of another task. */
    private final List tasksWithoutPredecessors = new LinkedList();
    /** These are the tasks that have no tasks dependent upon them. */
    private final List tasksWithoutSuccessors = new LinkedList();
    /**
     * Indicates if the topology has already been calculated.  If not, we have
     * to do it manually, which is more expensive.
     */
    private boolean topologyCalculated = false;

    /**
     * This maps id to a structure that contains the information we are
     * calculating, such as earliest start date, earliest end date, etc.
     */
    private final Map<String, TaskInformation> taskInformation = new HashMap<String, TaskInformation>();
    /**
     * This maps id to a structure that contains start and end date information
     * about a schedule.  We store that information here so we can figure out
     * whether the schedule has changed and whether or not it needs to be stored.
     */
    private final Map<String, ScheduleInformation> scheduleInformation = new HashMap<String, ScheduleInformation>();
    /** The schedule that all of these tasks exist in. It would current contain only a single schedule */
    private Map<String, Schedule> scheduleMap = new HashMap<String, Schedule>();
    /** A List of all the summary tasks in this schedule. */
    private final List<SummaryTask> summaryTasks = new LinkedList<SummaryTask>();
    /** A list of all tasks being "pushed". */
    private final List pushedTasks = new LinkedList();
    
    /**
     * This cache will be used to load objects when possible.  If nothing is in
     * the cache, the objects will still have to be loaded from the database
     * ultimately.  This becomes more important in the case when we do cross-
     * space task endpoint calculation, where it is possible that otherwise we
     * might have to load a schedule or task more than once.
     */
    //sjmittal: this is replaced by scheduleMap as now we calculate end points of
   // each schedule in isolation and cross projects are linked via DependencyWorker thread
//    private Cache cache = new Cache();
    /**
     * This is the list of all schedule ids that we will need to load in order to
     * be able to
     */
    private Set scheduleNetwork = null;

    void setCache(Map cache) {
        this.scheduleMap = cache;
    }

    Set getScheduleNetwork() {
        return scheduleNetwork;
    }

    void setScheduleNetwork(Set scheduleNetwork) {
        this.scheduleNetwork = scheduleNetwork;
    }

    private void loadSchedules(String basePlanID) throws SQLException, PersistenceException {
        Set scheduleIDs;
        if (scheduleNetwork == null) {
            scheduleNetwork = new HashSet();
            scheduleNetwork.add(basePlanID);

            scheduleIDs = new HashSet();
            scheduleIDs.add(basePlanID);
        } else {
            scheduleIDs = new HashSet(scheduleNetwork);
        }

        //Now that we have the map, we need to figure out which schedules we
        //really need to load and which ones are already in the cache.
        Collection schedulesToLoad = CollectionUtils.subtract(scheduleIDs, scheduleMap.keySet());

        //Now that we have our list of schedules that aren't loaded, load some
        //schedules.
        if (!schedulesToLoad.isEmpty()) {
            ScheduleFinder finder = new ScheduleFinder();
            finder.setLoadTaskAssignments(true);
            finder.setLoadTaskDependencies(true);
            List scheduleList = finder.findByPlanIDList(new ArrayList(schedulesToLoad));

            //Now add each of our schedules to the cache and the schedule map
            for (Iterator it = scheduleList.iterator(); it.hasNext();) {
                Schedule schedule = (Schedule) it.next();
                scheduleMap.put(schedule.getID(), schedule);
            }
        }

    }


    /**
     * If a schedule hasn't already had a specific date assigned to it, this
     * method will iterate through the schedule entries and determine which is
     * the earliest and is therefore the start date.
     *
     * @param schedule a <code>Schedule</code> object for which we are going to
     * look for the start date.
     * @return a <code>Date</code> which corresponds to a likely start date for
     * this schedule.
     */
    private Date findScheduleStartDate(Schedule schedule) {
        Date startDate = null;
        boolean setFromConstraint = (schedule.getStartConstraint().isDateConstrained() && schedule.getStartConstraintDate() != null);

        if (setFromConstraint) {
            return schedule.getStartConstraintDate();
        } else {
            //First, if there are schedule entries, we will find the earliest of the
            //schedule entries.
            for (Iterator it = schedule.getEntries().iterator(); it.hasNext();) {
                ScheduleEntry scheduleEntry = (ScheduleEntry)it.next();

                startDate = DateUtils.min(startDate, scheduleEntry.getStartTime());

            }

            //See if there is a constrained start date from any schedules
            startDate = DateUtils.min(startDate, findConstrainedScheduleStart(schedule));

            //Second, if the start date is still null, look to see if there is a
            //project start date
            if (startDate == null && schedule.getSpace() instanceof ProjectSpace) {
                ProjectSpace spaceAsProjectSpace = (ProjectSpace)schedule.getSpace();
                if (spaceAsProjectSpace.getStartDate() != null) {
                    startDate = spaceAsProjectSpace.getStartDate();
                }
            }
            // Michael Baranov: Checking scheduleStartDate. 
            // scheduleStartDate seems to be important
            // many test cases set it, but untill now it
            // was not considered in calculations.
            if (startDate == null) {
                startDate = schedule.getScheduleStartDate();
            }

            //Next, if the start date is still null, assign the date to today
            if (startDate == null) {
                startDate = new Date();
            }

            //Make sure that the working day always starts at the beginning of the
            //day
            IWorkingTimeCalendar workingTimeCalendar = schedule.getWorkingTimeCalendar();
            if (workingTimeCalendar.isWorkingDay(startDate)) {
                startDate = workingTimeCalendar.getStartOfWorkingDay(startDate);
            } else {
                startDate = workingTimeCalendar.getStartOfNextWorkingDay(startDate);
            }
        }

        return startDate;
    }

    private Date findConstrainedScheduleStart(Schedule schedule) {
        if (schedule.getStartConstraint().isDateConstrained()) {
            return schedule.getStartConstraintDate();
        }

        Date startDate = null;
        return startDate;
    }

    private Date getScheduleStartDate(ScheduleEntry entry) {
        String planID = entry.getPlanID();
        Date startDate;

        //Start by seeing if this date has already been modified.
        if (scheduleInformation.containsKey(planID)) {
            startDate = (scheduleInformation.get(planID)).getStartDate();
        } else if (scheduleMap.containsKey(planID)) {
            startDate = (scheduleMap.get(planID)).getScheduleStartDate();
        } else {
            throw new RuntimeException("getScheduleStartDate called with plan " +
                "id of " + planID + " from entry ID " + entry.getID() + ".  " +
                "There is no schedule with this plan id!");
        }

        return startDate;
    }

    private Date getScheduleEndDate(ScheduleEntry entry) {
        String planID = entry.getPlanID();
        Date endDate;

        if (scheduleInformation.containsKey(planID)) {
            endDate = (scheduleInformation.get(planID)).getEndDate();
        } else if (scheduleMap.containsKey(planID)) {
            endDate = (scheduleMap.get(planID)).getScheduleEndDate();
        } else {
            throw new RuntimeException("getScheduleEndDate called with " +
                "PlanID of " + planID + " from entry ID " + entry.getID() + ".  " +
                "There is no schedule with this plan id!");
        }

        return endDate;
    }

    private ScheduleInformation getScheduleInformation(String planID) {
        if (!scheduleInformation.containsKey(planID)) {
            Schedule schedule = scheduleMap.get(planID);
            ScheduleInformation info = new ScheduleInformation(schedule);
            scheduleInformation.put(planID, info);
        }

        return scheduleInformation.get(planID);
    }

    private ScheduleInformation getScheduleInformation(ScheduleEntry entry) {
        return getScheduleInformation(entry.getPlanID());
    }

    private void setScheduleEndDate(ScheduleEntry entry, Date newFinishDate) {
        String planID = entry.getPlanID();

        if (!scheduleInformation.containsKey(planID)) {
            Schedule schedule = scheduleMap.get(planID);
            ScheduleInformation info = new ScheduleInformation(schedule);
            scheduleInformation.put(planID, info);
        }

        (scheduleInformation.get(planID)).setEndDate(newFinishDate);
    }

    private void setScheduleEndDate(Schedule schedule, Date newEndDate) {
        String planID = schedule.getID();

        if (scheduleInformation.containsKey(planID)) {
            (scheduleInformation.get(planID)).setEndDate(newEndDate);
        } else {
            Date scheduleEndDate = schedule.getScheduleEndDate();
            if (scheduleEndDate == null || !scheduleEndDate.equals(newEndDate)) {
                ScheduleInformation info = new ScheduleInformation(schedule);
                info.setEndDate(newEndDate);
                scheduleInformation.put(planID, info);
            }
        }
    }

    private void setScheduleStartDate(ScheduleEntry entry, Date newStartDate) {
        String planID = entry.getPlanID();

        if (!scheduleInformation.containsKey(planID)) {
            Schedule schedule = scheduleMap.get(planID);
            ScheduleInformation info = new ScheduleInformation(schedule);
            scheduleInformation.put(planID, info);
        }

        (scheduleInformation.get(planID)).setStartDate(newStartDate);
    }
    
    private void setScheduleStartDate(Schedule schedule, Date startDate) {
        String planID = schedule.getID();

        if (scheduleInformation.containsKey(planID)) {
            (scheduleInformation.get(planID)).setStartDate(startDate);
        } else {
            Date scheduleStartDate = schedule.getScheduleStartDate();
            if (scheduleStartDate == null || !scheduleStartDate.equals(startDate)) {
                ScheduleInformation info = new ScheduleInformation(schedule);
                info.setStartDate(startDate);
                scheduleInformation.put(planID, info);
            }
        }
    }

    private IWorkingTimeCalendarProvider getCalendarProvider(ScheduleEntry entry) {
        String planID = entry.getPlanID();

        if (scheduleMap.containsKey(planID)) {
            return (scheduleMap.get(planID)).getWorkingTimeCalendarProvider();
        } else {
            throw new RuntimeException("getCalendarProvider called with " +
                "PlanID of " + planID + " from entry ID " + entry.getID() + ".  " +
                "There is not schedule with this plan id!");
        }
    }

    /**
     * Recalculate all the task end times in this schedule object without having
     * to first load them from persistent storage.
     *
     * @param schedule a <code>Schedule</code> object that already has its tasks
     * loaded.
     * @param storeTasks a <code>boolean</code> value indicating whether the
     * tasks should be stored after the recalculation has occurred.
     */
    public void recalculateTaskTimes(Schedule schedule, boolean storeTasks) throws PersistenceException {
    	
        DBBean db = new DBBean();
        try {
            loadSchedules(schedule.getID());
            db.setAutoCommit(false);
            recalculateTaskTimes(schedule, storeTasks, db);
            db.commit();
        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException e) {
                Logger.getLogger(ScheduleEntry.class).debug("Unable to roll back schedule.");
            }
            throw new PersistenceException("Unable to recalculate task times.", sqle);
        } finally {
            db.release();
        }
        
    }

    /**
     * Recalculate all the task end times in this schedule object.  All the
     * schedule entries will be reloaded based on a DBBean that is passed in.
     * This means that if there are uncommitted schedule entries, they can be
     * loaded in their dirty state.
     *
     * @param schedule a <code>Schedule</code> object that already has its tasks
     * loaded.
     * @param storeTasks a <code>boolean</code> value indicating whether the
     * tasks should be stored after the recalculation has occurred.
     * @param db a <code>DBBean</code> value which will be used to load schedule
     * entries.
     */
    private void recalculateTaskTimes(Schedule schedule, boolean storeTasks, DBBean db) throws SQLException, PersistenceException {

        if (!scheduleMap.containsKey(schedule.getID())) {
            loadSchedules(schedule.getID());
        }
        recalculate(storeTasks, db);
        //cleanup the current schedule as this has been recalculated 
        schedule.setWarnings(0, db);
        //sjmittal:
        //since the schedule was reloaded and changes in the properties have to be copied over to the original schedule also
        //properties that can change are start date end date etc ..
        Schedule loadedSchedule = scheduleMap.get(schedule.getID());
        if(loadedSchedule.getScheduleStartDate().compareTo(schedule.getScheduleStartDate()) != 0) {
            schedule.setScheduleStartDate(loadedSchedule.getScheduleStartDate());
        }
        if(schedule.getScheduleEndDate() == null || loadedSchedule.getScheduleEndDate().compareTo(schedule.getScheduleEndDate()) != 0) {
            schedule.setScheduleEndDate(loadedSchedule.getScheduleEndDate());
        }
        
    }

    /**
     * Recalculate the schedule without first reloading it from the database.
     * This is a pretty dangerous method to be using.  Do not use it unless you
     * are 100% positive that the schedule is properly loaded from the database
     * and that EVERYTHING is loaded.  (You can't have applied a filter on the
     * schedule that would prevent something from being loaded, for example.)
     * Currently in the application this method is always called with storeTasks 
     * equals to false. 
     *
     * @param schedule a <code>Schedule</code> object that already has its tasks
     * loaded.
     */
    public void recalculateTaskTimesNoLoad(Schedule schedule) throws PersistenceException {
        //put the scheule in map to prevent from re loading
        if (!scheduleMap.containsKey(schedule.getID())) {
            scheduleMap.put(schedule.getID(), schedule);
        }

        //note this db bean is redundant here as we are not going to store anything
        //its created only the called method demands it
        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            loadSchedules(schedule.getID());
            recalculate(false, db);
            db.commit();
        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException e) {
                throw new PersistenceException(e);
            }
            throw new PersistenceException("Unable to recalculate task times.", sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Calculate the start date, end date, and duration for each task in the
     * schedule that a task is associated with.
     *
     * @param storeTasks a <code>boolean</code> indicating that we should store
     * the tasks after we've recalculated them.
     * @throws SQLException if there is an error loading the schedule
     * tasks or schedule properties from the database.
     */
    private void recalculate(boolean storeTasks, DBBean db) throws SQLException, PersistenceException {
        //Add tasks for all schedules in the schedule network to our
        //list of tasks that we are going to process
    	findTasksToProcess();
        
        //Iterate through schedules, make sure all of them have a start date
        ensureScheduleStartAndEndDates();
        
        //Find the topological order of the tasks
        findTopologicalOrder();
        
        //Calculate the earliest start time, and latest start time for each task.
        findESTandEFT(tasksWithoutPredecessors);
        
        logger.debug("#######################################################");
        logger.debug("Begin backwards traversal");
        logger.debug("#######################################################");

        //Find the latest start time and latest finish time for each task.
        findLSTandLFT(tasksWithoutSuccessors);
        
        //Fix a scenario where a task that wants to start "asap" is preceded by
        //one that wants to start "alap".
        fixPushedTasks(pushedTasks);
        
        //Find the parent tasks and update their times from their children
        updateSTDates(summaryTasks);
        

        //Finalize the times for all the tasks and their assignments
        finalizeTimesAndAssignments();
        
        //Find the parent tasks and update their work and work complete from
        //their children.
        updateSTWork(summaryTasks);
        
        //Store new start and finish times for tasks
        storeTasks(tasks.values(), storeTasks, db);
        
        //Update Summary task durations; recursive function that
        //requires all tasks to be updated correctly to work
        updateSTDuration(storeTasks, db);
    }

    /**
     * Make sure that every schedule has a start date and an end date to start
     * out with. 
     * We reset all the dates to the schedule start date.  
     * It will be fixed by the time were done with the end point calculation.
     */
    private void ensureScheduleStartAndEndDates() {
        for (Iterator it = scheduleMap.values().iterator(); it.hasNext();) {
            Schedule schedule = (Schedule) it.next();
            Date scheduleStartDate = findScheduleStartDate(schedule);
            ScheduleInformation si = getScheduleInformation(schedule.getID());

//            if (schedule.getPredecessorList().isEmpty()) {
                si.setEST(scheduleStartDate);
                si.setLST(scheduleStartDate);
                si.setEFT(scheduleStartDate);
                si.setLFT(scheduleStartDate);
//            }

            //Step 1: Preinitialize the date that the project can start
            setScheduleStartDate(schedule, scheduleStartDate);

            //Step 2: Preinitialize the date that the project can stop
            setScheduleEndDate(schedule, scheduleStartDate);
        }
    }

    private void finalizeTimesAndAssignments() {
        for (Iterator it = tasks.values().iterator(); it.hasNext(); ) {
            ScheduleEntry task = (ScheduleEntry)it.next();

            //Find the task information for this task
            TaskInformation ti = taskInformation.get(task.getID());
            if (ti == null) {
            	ti = new TaskInformation();
                taskInformation.put(task.getID(), ti);
            }
            
            TaskConstraintType constraintType = task.getConstraintType();
            Date newStartDate, newEndDate;

            if (constraintType.isLateStarter()) {
                newStartDate = DateUtils.max(task.getLatestStartDate(),task.getEarliestStartDate());
                newEndDate = DateUtils.max(task.getLatestFinishDate(),task.getEarliestFinishDate());
            } else {
                //even if the task doesn't have the "isEarlyStarter" flag set,
                //we are going to use the earliest start time
                newStartDate = DateUtils.min(task.getEarliestStartDate(),task.getLatestStartDate());
                newEndDate = DateUtils.min(task.getEarliestFinishDate(),task.getLatestFinishDate());
            }

            //sjmittal: this step is redundant as its taken care above
//            if (task instanceof SummaryTask) {
//                //For both types, we need to make sure the finish time isn't null.
//                //It will be if there aren't any subtasks.  This shouldn't ever
//                //happen under normal circumstances, but it is happening in the
//                //unit tests.  We might as well fail gracefully.
//                if (constraintType.isLateStarter()) {
//                    newEndDate = (task.getLatestFinishDate() != null ?
//                        task.getLatestFinishDate() : task.getLatestStartDate());
//                } else {
//                    newEndDate = (task.getEarliestFinishDate() != null ?
//                        task.getEarliestFinishDate() : task.getEarliestStartDate());
//                }
//            }

            // Update the assignments (if their dates change)
            // We don't care about the resulting date since we already
            // calculated the end date
            ScheduleEntryDateCalculator dateCalc = new ScheduleEntryDateCalculator(task, getCalendarProvider(task));
            if (newStartDate != null) {
                dateCalc.addWorkAndupdateAssignmentDates(newStartDate);
                dateCalc.updateMaterialAssignmentDates(newStartDate, newEndDate);
            }

            // If any assignment date changes, then we must update the task
            // regardless of whether the task start or end date changed
            // It is possible to modify an assignment or working time
            // calendar such that an individual assignment's start or end
            // date changes but the overall task dates do not
            if (dateCalc.isAssignmentModified()) {
                ti.setModified(true);
            }

            //TODO: We must start checking to make sure that the constraint
            //date is between the earliest finish and latest finish dates.  If
            //it isn't, we have a conflict that needs to be resolved.

            if (task.getStartTime() == null || !task.getStartTime().equals(newStartDate)) {
                task.setStartTimeD(newStartDate);
                ti.setModified(true);
            }
            if (task.getEndTime() == null || !task.getEndTime().equals(newEndDate)) {
                task.setEndTimeD(newEndDate);
                ti.setModified(true);
            }

            boolean estMatchesLST = false;
            if (task.getEarliestStartDate()!= null && task.getLatestStartDate() != null && task.getEarliestStartDate().equals(task.getLatestStartDate()))
            	estMatchesLST = true;
            
            if (task.isCriticalPath() != (estMatchesLST && !task.isComplete())) {
                task.setCriticalPath(estMatchesLST && !task.isComplete());
                ti.setModified(true);
            }

            if (ti.forceStore) {
                ti.setModified(true);
            }

            //Fix the end date of the project, if necessary
            Date projectFinishTime = getScheduleEndDate(task);
            if (task.getEndTime().after(projectFinishTime)) {
                setScheduleEndDate(task, task.getEndTime());
            }
            //Fix the start date of the project, if necessary
            Date projectStartTime = getScheduleStartDate(task);
            if (task.getStartTime().before(projectStartTime)) {
                setScheduleStartDate(task, task.getStartTime());
            }
        }
    }

    /**
     * Finds tasks in all schedules that we are going to process.
     */
    private void findTasksToProcess() {

        for (Iterator it = scheduleNetwork.iterator(); it.hasNext();) {
            String planID = (String) it.next();
            Schedule schedule = scheduleMap.get(planID);
            tasks.putAll(schedule.getEntryMap());
        }
        
        tdf = new TaskDependenciesFetcher(tasks);
    }

    /**
     * Updates the dates for the given list of summary tasks by inspecting the dates of the summary tasks' children tasks.
     * Sets each summary task's EarliestStartDate, LatestStartDate, EarliestFinishDate, LatestFinishDate, ActualStartTimeD, 
     * ActualEndTimeD based on the dates its children.
     * 
     * Implements rules for setting summary task dates based on the dates of it's children tasks.
     * 
     * @param summaryTasks a list of summary tasks to update date for.
     * @throws PersistenceException
     */
    private void updateSTDates(List<SummaryTask> summaryTasks) throws PersistenceException {
        // for each summary task
    	for (Iterator<SummaryTask> it = summaryTasks.iterator(); it.hasNext();) {
            SummaryTask st = it.next();
            TaskInformation ti = taskInformation.get(st.getID());
            Date est = null, lst = null, eft = null, lft = null, actualStart = null, actualEnd = null;

            if (ti == null || !ti.summaryMark) {
                if (ti == null) {
                    ti = new TaskInformation();
                    //sjmittal: please put the task information
                    taskInformation.put(st.getID(), ti);
                }
            }

            // for each of the summary task's children
            for (Iterator it2 = st.getChildren().iterator(); it2.hasNext();) {
                ScheduleEntry child = (ScheduleEntry)it2.next();

                boolean earlyStarter = child.getConstraintType().isEarlyStarter();

                if (est == null || child.getEarliestStartDate().before(est)) {
                    est = (earlyStarter ? child.getEarliestStartDate() : child.getLatestStartDate());
                }
                if (lst == null || child.getLatestStartDate().before(lst)) {
                    lst = child.getLatestStartDate();
                }
                if (eft == null || child.getEarliestFinishDate().after(eft)) {
                    eft = (earlyStarter ? child.getEarliestFinishDate() : child.getLatestFinishDate());
                }
                if (lft == null || child.getLatestFinishDate().after(lft)) {
                    lft = child.getLatestFinishDate();
                }
                if ((actualStart == null && child.getActualStartTime() != null)
                    || (actualStart != null && child.getActualStartTime() != null && child.getActualStartTime().before(actualStart))) {
                    actualStart = child.getActualStartTime();
                }
                if ((actualEnd == null && child.getActualEndTime() != null)
                    || (actualEnd != null && child.getActualEndTime() != null && child.getActualEndTime().after(actualEnd))) {
                    actualEnd = child.getActualEndTime();
                }
            }

            if (est == null) {
                est = getScheduleStartDate(st);
            }
            if (lst == null) {
                lst = getScheduleStartDate(st);
            }
            if (eft == null) {
                eft = getScheduleEndDate(st);
            }
            if (lft == null) {
                lft = getScheduleEndDate(st);
            }

            st.setEarliestStartDate(est);
            st.setLatestStartDate(lst);
            st.setEarliestFinishDate(eft);
            st.setLatestFinishDate(lft);

            if (st.getActualStartTime() != actualStart) {
                ti.forceStore = true;
                st.setActualStartTimeD(actualStart);
            }
            if (st.getActualEndTime() != actualEnd) {
                ti.forceStore = true;
                st.setActualEndTimeD(actualEnd);
            }
        }
    }

    /**
     * Finds the earliest start date, lastest start date, earliest finish date,
     * latest finish date, and work according to the children of this summary
     * task.
     *
     * @param summaryTasks a <code>List</code> of SummaryTask objects that we
     * are going to iterate through and update their information.
     * @throws PersistenceException if the children weren't preloaded (which
     * they will be) and they can't be loaded from the database.  This shouldn't
     * happen under normal circumstances because we ensure the children are
     * prepopulated.
     */
    private void updateSTWork(List<SummaryTask> summaryTasks) throws PersistenceException {
        for (Iterator<SummaryTask> it = summaryTasks.iterator(); it.hasNext();) {
            SummaryTask st = it.next();
            TaskInformation ti = taskInformation.get(st.getID());

            TimeQuantity totalChildWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);
            TimeQuantity totalChildWorkComplete = new TimeQuantity(0, TimeQuantityUnit.HOUR);
//            TimeQuantity totalChildDuration = new TimeQuantity(0, TimeQuantityUnit.HOUR);
//            TimeQuantity totalChildActualDuration = new TimeQuantity(0, TimeQuantityUnit.HOUR);

            //Iterate through all the children to fill in the above values for a
            //summary task.
            if (ti == null || !ti.summaryMark) {
                if (ti == null) {
                    ti = new TaskInformation();
                }

                //Iterate through the children to find the earliest and latest dates
                for (Iterator it2 = st.getChildren().iterator(); it2.hasNext();) {
                    ScheduleEntry child = (ScheduleEntry)it2.next();

                    //if the child is a summary task, they probably haven't be
                    //updated yet.  Run this method on them first.
                    if (child instanceof SummaryTask) {
                        updateSTWork(new LinkedList(Arrays.asList(new ScheduleEntry[] {child})));
                    }

                    //Add work for this child
                    TimeQuantity childWork = ScheduleTimeQuantity.convertToHour(child.getWorkTQ());
                    totalChildWork = totalChildWork.add(childWork);

                    //Add work complete for this child
                    TimeQuantity workComplete = child.getWorkCompleteTQ();
                    workComplete = (workComplete == null ? new TimeQuantity(0, TimeQuantityUnit.HOUR) : workComplete);
                    totalChildWorkComplete = totalChildWorkComplete.add(ScheduleTimeQuantity.convertToHour(workComplete));

                    //Add duration for this child
//                    totalChildDuration = totalChildDuration.add(ScheduleTimeQuantity.convertToHour(child.getDurationTQ()));
//
//                    //Add actual duration for this child
//                    totalChildActualDuration = totalChildActualDuration.add(ScheduleTimeQuantity.convertToHour(child.getActualDuration()));
                }

                //If there are resources assigned to the summary task, we need
                //to account for their work too.
                TimeQuantity resourceAssignmentWork = TimeQuantity.O_HOURS;
                TimeQuantity assignedResourceActualWork = TimeQuantity.O_HOURS;

                IWorkingTimeCalendarProvider provider = getCalendarProvider(st);
                Collection assignments = st.getAssignments();
                for (Iterator it2 = assignments.iterator(); it2.hasNext();) {
                    ScheduleEntryAssignment assn = (ScheduleEntryAssignment)it2.next();
//                    DefinitionBasedWorkingTimeCalendar calendar =  assn.getWorkingTimeCalendar(provider);
//                    TimeQuantity work = new WorkCalculatorHelper(calendar).getWork(st.getStartTime(), st.getEndTime(), assn.getPercentAssignedDecimal());
                    resourceAssignmentWork = resourceAssignmentWork.add(assn.getWork());
                    assignedResourceActualWork = assignedResourceActualWork.add(assn.getWorkComplete());
                    //Update the assignment to make sure it has the right amount of work.
                    
//                    assn.setWork(resourceAssignmentWork);
//                    assn.setWorkComplete(resourceAssignmentWork.multiply(percentComplete));
                }

                //Here we calculate the actual work for the individual resources.
//                TimeQuantity assignedResourceActualWork = resourceAssignmentWork.multiply(percentComplete);

                TimeQuantity totalSTWork = totalChildWork.add(resourceAssignmentWork);
                TimeQuantity totalSTWorkComplete = totalChildWorkComplete.add(assignedResourceActualWork);

                ti.setWork(totalSTWork);
                ti.setWorkComplete(totalSTWorkComplete);

                //We've already finalized other tasks, we update work now, because
                //we would have done that at the time if we had the chance to do so.
                if (st.getWorkTQ().convertToScale(2, BigDecimal.ROUND_HALF_UP).compareTo(ti.getWork().convertToScale(2, BigDecimal.ROUND_HALF_UP)) != 0) {
                    st.setWork(ti.getWork());
                    ti.setModified(true);
                }
                if (st.getWorkCompleteTQ().convertToScale(2, BigDecimal.ROUND_HALF_UP).compareTo(ti.getWorkComplete().convertToScale(2, BigDecimal.ROUND_HALF_UP)) != 0) {
                    st.setWorkComplete(ti.getWorkComplete());
                    ti.setModified(true);
                }

                //Important to set percent complete for summary tasks 
                //Calculation the duration percent complete for this summary task
                BigDecimal percentComplete;
                if (totalSTWork.isZero() || totalSTWorkComplete.isZero()) {
                    percentComplete = new BigDecimal("0.000");
                } else {
                    percentComplete = totalSTWorkComplete.divide(totalSTWork, 3, BigDecimal.ROUND_HALF_UP);
                }                
                st.setPercentComplete(percentComplete);

                ti.summaryMark = true;
                taskInformation.put(st.getID(), ti);
            }
        }
    }

    /**
     * Update the EST and EFT of tasks that are scheduled to start as early as
     * possible but that have an "as late as possible" task preceding them.
     *
     * These tasks have an incorrect est the first time through, because they
     * need the LST of the predecessor task in order to determine the correct
     * EST, but it isn't available yet.
     *
     * We redo calculation for these tasks and their successors in order to fix
     * the confusion.
     */
    private void fixPushedTasks(List itemsToFix) throws PersistenceException {
        List nextItemsToFix = new LinkedList();

        for (Iterator it = itemsToFix.iterator(); it.hasNext();) {
            ScheduleEntry scheduleEntry = (ScheduleEntry)it.next();

            Date newStartTime = findEST(scheduleEntry);
            Date newEndTime = findEFT(newStartTime, scheduleEntry);

            TaskInformation ti = taskInformation.get(scheduleEntry.getID());

            boolean visitChildren = false;
            if (!scheduleEntry.getEarliestStartDate().equals(newStartTime)) {
                visitChildren = true;
                scheduleEntry.setEarliestStartDate(newStartTime);
            }

            if (!scheduleEntry.getEarliestFinishDate().equals(newEndTime)) {
                visitChildren = true;
                scheduleEntry.setEarliestFinishDate(newEndTime);
            }

            if (visitChildren) {
                nextItemsToFix.addAll(findNextTasksToProcess(scheduleEntry));
            }

            updateSummaryTaskESTAndEFT(scheduleEntry, ti, true);
        }

        if (!nextItemsToFix.isEmpty()) {
            fixPushedTasks(nextItemsToFix);
        }
    }

    /**
     * Find the earliest start time (EST) and earliest finish time (EFT) for all
     * schedule entries for the system.  This method is recursive and will call
     * itself repeatedly until all entries are found.
     *
     * @param itemsToFix schedule entries that are in the
     * current "batch" that we are going to process.
     * @throws PersistenceException if there is an error loading predecessors.
     * This should never be called because they should already be loaded.
     */
    private void findESTandEFT(Collection itemsToFix) throws PersistenceException {
        if (logger.isDebugEnabled()) {
            logger.debug("itemsToFix size: " + itemsToFix.size());
        }

        // Use a set to remove duplicate tasks; maintain insertion order
        // Duplicate tasks may be added since next tasks are determined
        // from successors or successors of parent tasks
        // Two tasks may have the same parent and thus the same set of successors
        Set nextTasksToFix = new LinkedHashSet();

        //We need to make sure that all of the predecessors of the items we are
        //going to process have already been processed.  
        List delayProcessingList = findItemsWithUnprocessedPredecessors(itemsToFix);
        if (logger.isDebugEnabled()) {
            logger.debug("--------------------------------------");
            logger.debug("Before Processing");
            logger.debug("itemsToFix: " + itemsToFix);
            logger.debug("delayProcessingList: " + delayProcessingList);
        }
        itemsToFix.removeAll(delayProcessingList);

        for (Iterator it = itemsToFix.iterator(); it.hasNext();) {
            ScheduleEntry task = (ScheduleEntry)it.next();
            TaskInformation ti = taskInformation.get(task.getID());

            if (ti == null) {
                ti = new TaskInformation();
                //Store the information that we've collected about est and eft.
                taskInformation.put(task.getID(), ti);
            }

            //Find the start and finish time of our task by iterating through
            //the dependencies and analyzing them
            task.setEarliestStartDate(findEST(task));
            task.setEarliestFinishDate(findEFT(task.getEarliestStartDate(), task));

            //Update the estimated project finish time, if necessary
            updateScheduleDatesForward(task);

            //Find tasks that might cause this task's start date to be
            //calculated in an unusual way
            findPushers(task, ti);

            //If this is a child task, update the summary task's information
            updateSummaryTaskESTAndEFT(task, ti, false);

            //We are done processing this task.
            ti.forwardMark = true;

            //Find the tasks to look at after we are done with this batch
            nextTasksToFix.addAll(findNextTasksToProcess(task));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("After Processing");
            logger.debug("nextTasksToFix: " + nextTasksToFix.toString());
            logger.debug("--------------------------------------");
        }

        if (nextTasksToFix.size() > 0) {
            findESTandEFT(nextTasksToFix);
        }
    }

    private void updateScheduleDatesForward(ScheduleEntry task) {
        TaskEndpointCalculation.ScheduleInformation scheduleInformation = getScheduleInformation(task);
        IWorkingTimeCalendar taskWorkingTimeCalendar = task.getResourceCalendar(getCalendarProvider(task));

        //First, deal with the EFT -- we'll always be setting that
        Date projectFinishTime = scheduleInformation.getEFT();
        Date taskFinishTime = fixupFinishDate(task.getEarliestFinishDate(), taskWorkingTimeCalendar);
        scheduleInformation.setEFT(DateUtils.max(taskFinishTime, projectFinishTime));
        //sachin: we need this line not sure why ?? (but never remove!!)
        scheduleInformation.setEndDate(scheduleInformation.getEFT());
        
        //We only set the EST if we have a task which is not constraint driven
        Schedule schedule = scheduleMap.get(task.getPlanID());
        if (!schedule.getStartConstraint().isDateConstrained()) {
            Date projectStartTime = scheduleInformation.getEST();
            Date taskStartTime = fixupStartDate(task.getEarliestStartDate(), taskWorkingTimeCalendar);
            scheduleInformation.setEST(DateUtils.min(taskStartTime, projectStartTime));
        }
    }

    private void updateScheduleDatesBackward(ScheduleEntry task) {
        TaskEndpointCalculation.ScheduleInformation scheduleInformation = getScheduleInformation(task);
        IWorkingTimeCalendar taskWorkingTimeCalendar = task.getResourceCalendar(getCalendarProvider(task));

        Date projectFinishTime = scheduleInformation.getLFT();
        Date taskFinishTime = fixupFinishDate(task.getLatestFinishDate(), taskWorkingTimeCalendar);
        scheduleInformation.setLFT(DateUtils.max(projectFinishTime, taskFinishTime));

        Schedule schedule = scheduleMap.get(task.getPlanID());
        if (!schedule.getStartConstraint().isDateConstrained()) {
            Date projectStartTime = scheduleInformation.getLST();
            Date taskStartTime = fixupStartDate(task.getLatestStartDate(), taskWorkingTimeCalendar);
            scheduleInformation.setLST(DateUtils.min(taskStartTime, projectStartTime));
        }
    }

    private void updateSummaryTaskESTAndEFT(ScheduleEntry childTask, TaskInformation childTI, boolean pushersResolved) throws PersistenceException {
        if (!childTask.hasParent() || (!pushersResolved && childTI.hasPushers())) {
            return;
        }

        //Get or create parent task information
        SummaryTask parent = (SummaryTask)tasks.get(childTask.getParentTaskID());
        TaskInformation parentTI = taskInformation.get(parent.getID());
        if (parentTI == null) {
            parentTI = new TaskInformation();
            taskInformation.put(parent.getID(), parentTI);
        }

        Date childEST = childTask.getEarliestStartDate();
        Date childEFT = childTask.getEarliestFinishDate();

        if (parent.getEarliestStartDate() == null || childEST.before(parent.getEarliestStartDate())) {
            parent.setEarliestStartDate(childEST);
        }

        if (parent.getEarliestFinishDate() == null || parent.getEarliestFinishDate().before(childEFT)) {
            parent.setEarliestFinishDate(childEFT);
        }
        
        //If this summary task is a child of another one, make sure the parent is updated
        if (parent.hasParent()) {
            updateSummaryTaskESTAndEFT(parent, parentTI, true);
        }
    }

    /**
     * Find the tasks that we should process after processing a given task.
     * <p>
     * The next tasks to process are defined as the actual predecessor tasks to the task
     * OR, if the task has no predecessor, then the predecessor of the first parent task that has predecessors.
     * That is, if a task has no successors itself but has a parent task (of course, a Summary task)
     * and the parent task has a predecessor (another task is dependent on the Summary)
     * then the next tasks to fix will include that task that is dependent on the Summary.
     * </p>
     * <p>
     * In all cases, if a predecessor is itself a summary task, then it is "exploded"
     * and the next tasks to process will include that summary task's non-summary
     * children without sucessors, recursively.
     * </p>
     * @param task a <code>ScheduleEntry</code> for which we are going to find
     * predecessors.
     * @return an ordered collection of tasks to process next
     * @throws PersistenceException if there is an error communicating with the
     * database.  This shouldn't happen in practice as the predecessors should
     * have been loaded long before getting to this point.
     */
    private Collection findNextTasksToProcessBackwards(ScheduleEntry task) throws PersistenceException {
        Collection nextTasksToFix = new LinkedList();

        //Find the tasks that we should process after we are done processing the current task.
        for (Iterator it2 = tdf.getPredecessors(task).iterator(); it2.hasNext();) {
            TaskDependency td = (TaskDependency)it2.next();
            ScheduleEntry nextTaskToProcess = (ScheduleEntry)tasks.get(td.getDependencyID());
            nextTasksToFix.add(nextTaskToProcess);
        }

        return nextTasksToFix;
    }
    
    /**
     * Find the tasks that we should process after processing a given task.
     * <p>
     * The next tasks to process are defined as the actual successor tasks to the task
     * OR, if the task has no successors, then the successors of the first parent task that has successors.
     * That is, if a task has no successors itself but has a parent task (of course, a Summary task)
     * and the parent task has a successor (another task is dependent on the Summary)
     * then the next tasks to fix will include that task that is dependent on the Summary.
     * </p>
     * <p>
     * In all cases, if a successor is itself a summary task, then it is "exploded"
     * and the next tasks to process will include that summary task's non-summary
     * children without predecessors, recursively.
     * </p>
     * @param task a <code>ScheduleEntry</code> for which we are going to find
     * successors.
     * @return an ordered collection of tasks to process next
     * @throws PersistenceException if there is an error communicating with the
     * database.  This shouldn't happen in practice as the successors should
     * have been loaded long before getting to this point.
     */
    private Collection findNextTasksToProcess(ScheduleEntry task) throws PersistenceException {
        Collection nextTasksToFix = new LinkedList();

        //Find the tasks that we should process after we are done processing the current task.
        for (Iterator it2 = tdf.getSuccessors(task).iterator(); it2.hasNext();) {
            TaskDependency td = (TaskDependency)it2.next();
            ScheduleEntry nextTaskToFix = (ScheduleEntry)tasks.get(td.getTaskID());
            nextTasksToFix.add(nextTaskToFix);
        }

        return nextTasksToFix;
    }

    /**
     * Find situation where a task wants to start as early as possible, but its
     * predecessor wants to start as late as possible.  This is a strange case
     * which I call a "pusher".  When an early starting task has a pusher, it
     * no longer uses its EST to determine its starting time.  Instead it uses
     * information from its pusher to determine the start date.
     *
     * @param task a <code>Task</code> that we are going to check for pushers.
     * @param ti a <code>TaskInformation</code> object in which we are storing
     * information about the task.
     * @throws PersistenceException never.
     */
    private void findPushers(ScheduleEntry task, TaskInformation ti) throws PersistenceException {
        boolean isEarlyStarter = task.getConstraintType().isEarlyStarter();

        if (!isEarlyStarter) {
            return;
        } else {
            //Iterate through predecessors and see if any task is "pushing" this task.
            for (Iterator it = tdf.getPredecessors(task).iterator(); it.hasNext();) {
                TaskDependency td = (TaskDependency)it.next();
                ScheduleEntry predecessorTask = (ScheduleEntry)tasks.get(td.getDependencyID());

                if (predecessorTask.getConstraintType().isLateStarter()) {
                    ti.addPusher(td);
                }
            }

            if (ti.pushers != null && !ti.pushers.isEmpty()) {
                pushedTasks.add(task);
            }
        }

    }

    /**
     * Find the latest time that all of the tasks in the project can start and
     * finish without affecting the end date of the schedule.
     *
     * This method is recursive and will call itself repeatedly to get all of
     * the results.
     *
     * @param itemsToFix a <code>List</code> object which contains the current
     * round of items to analyze for LST and LFT times.
     * @throws PersistenceException never.
     */
    private void findLSTandLFT(Collection itemsToFix) throws PersistenceException {
        if (logger.isDebugEnabled()) {
            logger.debug("itemsToFix size: " + itemsToFix.size());
        }

        // Use a set to remove duplicate tasks; maintain insertion order
        // Duplicate tasks may be added since next tasks are determined
        // from predecessors or predecessors of parent tasks
        // Two tasks may have the same parent and thus the same set of predecessors
        Set nextTasksToFix = new LinkedHashSet();
        
        //We need to make sure that all of the successors of the items we are
        //going to process have already been processed.  It would seem like this
        //would already be done, but it isn't that easy.  In unit tests like
        //SS with ALAP, we find that 1 successor might be done, but maybe not
        //all of them are done.
        List delayProcessingList = findItemsWithUnprocessedSuccessors(itemsToFix);
        if (logger.isDebugEnabled()) {
            logger.debug("--------------------------------------");
            logger.debug("Before Processing");
            logger.debug("itemsToFix: " + itemsToFix);
            logger.debug("delayProcessingList: " + delayProcessingList);
        }
        itemsToFix.removeAll(delayProcessingList);

        for (Iterator it = itemsToFix.iterator(); it.hasNext();) {
            ScheduleEntry task = (ScheduleEntry)it.next();
            
            TaskInformation ti = taskInformation.get(task.getID());
            if (ti == null) {
            	ti = new TaskInformation();
                taskInformation.put(task.getID(), ti);
            }

            //Find the LFT and LST
            task.setLatestFinishDate(findLFT(task));
            task.setLatestStartDate(findLST(task.getLatestFinishDate(), task));

            //Make sure the schedule has the right LFT
            updateScheduleDatesBackward(task);

            //If this is a child task, update the summary task L times.
            updateSummaryTaskLSTAndLFT(task);

            //Mark this task as having been processed.
            ti.backwardsMark = true;
            
            //Find the next tasks to process after we are done with these.
            nextTasksToFix.addAll(findNextTasksToProcessBackwards(task));
        }

        if (logger.isDebugEnabled()) {
            logger.debug("After Processing");
            logger.debug("nextTasksToFix: " + nextTasksToFix.toString());
            logger.debug("--------------------------------------");
        }

        //If we aren't done yet, recurse into this method with the next round of
        //items to fix.
        if (nextTasksToFix.size() > 0) {
            findLSTandLFT(nextTasksToFix);
        }
    }

    private void updateSummaryTaskLSTAndLFT(ScheduleEntry childTask) {
        if (!childTask.hasParent()) {
            return;
        }

        SummaryTask parent = (SummaryTask)tasks.get(childTask.getParentTaskID());
        Date parentLST = parent.getLatestStartDate();
        Date parentLFT = parent.getLatestFinishDate();

        if (parentLST == null || childTask.getLatestStartDate().before(parentLST)) {
            parent.setLatestStartDate(childTask.getLatestStartDate());
        }

        if (parentLFT == null || childTask.getLatestFinishDate().after(parentLFT)) {
            parent.setLatestFinishDate(childTask.getLatestFinishDate());
        }

        if (parent.hasParent()) {
            updateSummaryTaskLSTAndLFT(parent);
        }
    }

    /**
     * Find any items that we can't process yet because all of their successors
     * haven't been processed.
     *
     * @param itemsToFix a <code>List</code> of items that we are considering
     * processing.
     * @return a <code>List</code> of items that we can process because their
     * sucessors have already been processed.
     * @throws PersistenceException never.
     */
    private List findItemsWithUnprocessedSuccessors(Collection itemsToFix) throws PersistenceException {
        List unprocessedSuccessors = new LinkedList();

        for (Iterator it = itemsToFix.iterator(); it.hasNext();) {
            ScheduleEntry task = (ScheduleEntry)it.next();

            for (Iterator it2 = tdf.getSuccessors(task).iterator(); it2.hasNext();) {
                TaskDependency td = (TaskDependency)it2.next();
                ScheduleEntry successor = (ScheduleEntry)tasks.get(td.getTaskID());
                TaskInformation successorTI = taskInformation.get(td.getTaskID());
                
                if (successor instanceof SummaryTask) {
                    continue;
                }

                if (successorTI == null || !successorTI.backwardsMark) {
                    unprocessedSuccessors.add(task);
                    break;
                }
            }
        }

        return unprocessedSuccessors;
    }

    /**
     * Find the items that haven't fully been processed yet.  We are going to
     * avoid processing these items until they are ready.
     *
     * It is fairly easy for this to happen.  It can happen any time a task has
     * two predecessors who aren't at the same "level" of processing.
     *
     * @param itemsToFix a <code>List</code> containing the items that we are
     * currently considering to set the start and finish dates on.
     * @return a <code>List</code> of object that we are going to fix because
     * all of their predecessors have already been processed.
     * @throws PersistenceException never.  Predecessors should have already
     * been loaded.
     */
    private List findItemsWithUnprocessedPredecessors(Collection itemsToFix) throws PersistenceException {
        List unprocessedPredecessors = new LinkedList();

        for (Iterator it = itemsToFix.iterator(); it.hasNext();) {
            ScheduleEntry task = (ScheduleEntry)it.next();

            for (Iterator it2 = tdf.getPredecessors(task).iterator(); it2.hasNext();) {
                TaskDependency td = (TaskDependency)it2.next();
                ScheduleEntry predecessor = (ScheduleEntry)tasks.get(td.getDependencyID());
                if (predecessor instanceof SummaryTask) {
                    continue;
                }
                
                TaskInformation predecessorTI = taskInformation.get(td.getDependencyID());
                if (predecessorTI == null || !predecessorTI.forwardMark) {
                    unprocessedPredecessors.add(task);
                    break;
                }
            }
        }

        return unprocessedPredecessors;
    }

    /**
     * Find the latest possible time at which this task can be completed.
     *
     * @param finishTime a <code>finishTime</code> of this task.
     * @param task a <code>ScheduleEntry</code> that we are going to analyze to
     * find its latest start time.
     * @return a <code>Date</code> containing the latest possible start time for
     * this task.
     * @throws PersistenceException
     */
    private Date findLST(Date finishTime, ScheduleEntry task) throws PersistenceException {
        IWorkingTimeCalendar taskWorkingTimeCalendar = task.getResourceCalendar(getCalendarProvider(task));
        IDateCalculator taskDateCalculator = new ScheduleEntryDateCalculator(task, getCalendarProvider(task));

        Date latestStartTime = null;

        //If the task has already started, the LST is set.
        
        //Roger -- Fixes BFD-3162.  Commented out the following 3 lines.
        //if (task.getActualStartTime() != null) {
        //    return task.getActualStartTime();
        //}

        if (task.getConstraintType().equals(TaskConstraintType.MUST_FINISH_ON)) {
            // Must Finish On constraint dictates that the latest start time
            // is the constraint date minus work
            Date finishDate = task.getConstraintDate();

            // Latest start time is constraint's date working backwards
            latestStartTime = taskDateCalculator.calculateStartDate(finishDate);

            if (!latestStartTime.equals(finishTime)) {
                latestStartTime = fixupStartDate(latestStartTime, taskWorkingTimeCalendar, task);
            }

            return latestStartTime;
        } else if (task.getConstraintType().equals(TaskConstraintType.MUST_START_ON)) {
            // Must Start On Constraint dictates the start time is the constraint
            // time
            latestStartTime = task.getConstraintDate();

            return latestStartTime;
        } else if (task.getConstraintType().equals(TaskConstraintType.START_AND_END_DATES_FIXED)) {
            // Must start On the start date
            // This is for shared tasks in an external schedule 
            return task.getStartTime();

        } else if (task.getConstraintType().equals(TaskConstraintType.FINISH_NO_LATER_THAN)) {
            latestStartTime = taskDateCalculator.calculateStartDate(task.getConstraintDate());
        } else {
            // No constraint affecting start time
            // Determine it by subtracting work from the finish time

            latestStartTime = taskDateCalculator.calculateStartDate(finishTime);
        }

        //Make sure if there is a parent task that it doesn't have an appropriate constraint to apply
        //sjmittal: as per new design parent tasks constraints are not applicable
//        if (task.hasParent()) {
//            SummaryTask parentTask = (SummaryTask)tasks.get(task.getParentTaskID());
//            if (parentTask.getConstraintType().equals(TaskConstraintType.FINISH_NO_LATER_THAN)) {
//                if (latestStartTime == null || (latestStartTime.after(parentTask.getConstraintDate()))) {
//                    latestStartTime = parentTask.getConstraintDate();
//                }
//            }
//        }

        for (Iterator it = tdf.getSuccessors(task).iterator(); it.hasNext();) {
            TaskDependency td = (TaskDependency)it.next();
            ScheduleEntry successor = (ScheduleEntry)tasks.get(td.getTaskID());
            IDateCalculator lagDateCalculator = new ScheduleDateCalculator(td.getLag(), getCalendarProvider(successor));

            Date dependencyDate = null;
            if (td.getDependencyType().equals(TaskDependencyType.START_TO_FINISH)) {
                dependencyDate = lagDateCalculator.calculateStartDate(successor.getLatestFinishDate());
            } else if (td.getDependencyType().equals(TaskDependencyType.START_TO_START)) {
                dependencyDate = lagDateCalculator.calculateStartDate(successor.getLatestStartDate());
            } else if (td.getDependencyType().equals(TaskDependencyType.FINISH_TO_FINISH)) {
                dependencyDate = lagDateCalculator.calculateStartDate(successor.getLatestFinishDate());
                dependencyDate = taskDateCalculator.calculateStartDate(dependencyDate);
            } else if (td.getDependencyType().equals(TaskDependencyType.FINISH_TO_START)) {
                dependencyDate = lagDateCalculator.calculateStartDate(successor.getLatestStartDate());
            }

            latestStartTime = DateUtils.min(latestStartTime, dependencyDate);
        }

        if (!latestStartTime.equals(finishTime)) {
            // Fix up the start time to be appropriate for a start time,
            // taking into consideration the constraint type of the task
            latestStartTime = fixupStartDate(latestStartTime, taskWorkingTimeCalendar, task);
        }


        return latestStartTime;
    }

    /**
     * Find the latest time at which a task can finish and not effect the end
     * date of the project.  This is calculated using the following algorithm:
     *
     * <ol>
     * <li>See if the task has successors.  If it doesn't, it's latest finish
     * date is the project finish date that we calculated when we were finding
     * the earliest finish dates.</li>
     * <li>If the task has successors, find which one (with its lag added in)
     * is the earliest and assign that to the latest finish time.</li>
     * </ol>
     *
     * @param task a <code>Task</code> object for which we will find the
     * latest finish date.
     * @return a <code>Date</code>
     * @throws PersistenceException
     */
    private Date findLFT(ScheduleEntry task) throws PersistenceException {
        IWorkingTimeCalendar taskWorkingTimeCalendar = task.getResourceCalendar(getCalendarProvider(task));
        IDateCalculator taskDateCalculator = new ScheduleEntryDateCalculator(task, getCalendarProvider(task));

        Date latestFinishDate;

        if (task.getConstraintType().equals(TaskConstraintType.FINISH_NO_LATER_THAN)) {
            // Finish No Later constraint dictates that the latest finish time
            // cannot be later than the constraint date
            // However, the latest finish time may be earlier
            latestFinishDate = task.getConstraintDate();

            // Continue to check dependencies

        } else if (task.getConstraintType().equals(TaskConstraintType.MUST_FINISH_ON)) {
            // Must Finish On constraint dictates the finish time
            // We're done because the finish time can't be earlier or later
            return task.getConstraintDate();

        } else if (task.getConstraintType().equals(TaskConstraintType.START_AND_END_DATES_FIXED)) {
            // Must finish On the end date
            // This is for shared tasks in an external schedule 
            return task.getEndTime();

        } else if (task.getConstraintType().equals(TaskConstraintType.MUST_START_ON)) {
            // Must Start On dictates the finish time to be the constraint date
            // plus work
            Date startDate = task.getConstraintDate();

            Date finishTime = taskDateCalculator.calculateFinishDate(startDate);
            if (task.getWorkTQ().getAmount().signum() != 0) {
                finishTime = fixupFinishDate(finishTime, taskWorkingTimeCalendar, task);
            }

            // We're done since the finish time can't be earlier or later
            return finishTime;

        } else if (task.getConstraintType().equals(TaskConstraintType.START_NO_LATER_THAN)) {
            // Start No Later constraint means the latest possible time for
            // the finish time is the constraint date plus work
            // However, the latest finish time may be earlier
            Date startDate = task.getConstraintDate();

            latestFinishDate = taskDateCalculator.calculateFinishDate(startDate);

            // Continue processing to check dependencies

        } else {
            // No constraint affecting finish time, so we'll start with the
            // schedule finish time.  If this is an invited task, we need to
            // check the finish date of all the schedules that have been invited
            // to -- there may be a situation in another schedule that makes the
            // latest start date be due to something external.
            latestFinishDate = getScheduleEndDate(task);
        }

        // Make sure if there is a parent task that it doesn't have an
		// appropriate constraint to apply
        //sjmittal: as per new design parent tasks constraints are not applicable        
//        if (task.hasParent()) {
//            SummaryTask parentTask = (SummaryTask)tasks.get(task.getParentTaskID());
//            if (parentTask.getConstraintType().equals(TaskConstraintType.FINISH_NO_LATER_THAN)) {
//                if (latestFinishDate == null || (latestFinishDate.before(parentTask.getConstraintDate()))) {
//                    latestFinishDate = parentTask.getConstraintDate();
//                }
//            }
//        }

        //Calculate the earliest dependency date as the EFT of this task.
        latestFinishDate = updateLatestFinishDate(task, latestFinishDate);

        //sjmittal: as per new design parent tasks constraints are not applicable        
//        if (task.hasParent()) {
//            SummaryTask parent = (SummaryTask)tasks.get(task.getParentTaskID());
//            if (parent.getConstraintType().equals(TaskConstraintType.FINISH_NO_LATER_THAN)) {
//                if (latestFinishDate.after(parent.getConstraintDate())) {
//                    latestFinishDate = parent.getConstraintDate();
//                }
//            }
//        }

        if (task.getWorkTQ().getAmount().signum() != 0) {
            // Fix up the finish time to be appropriate for an end time,
            // taking into consideration the constraint type of the task
            latestFinishDate = fixupFinishDate(latestFinishDate, taskWorkingTimeCalendar, task);
        }

        return latestFinishDate;
    }

    /**
     * Update the latest finish date to be the earliest finish date of these
     * successors of the current task.
     *
     * @param task a <code>ScheduleEntry</code> whose successors we are going to
     * iterate in order to find the successor that starts the earliest.
     * @param latestFinishDate a <code>Date</code> containing the current latest
     * finish Date
     * @throws PersistenceException if there is an error loading successors.
     * They should have all been loaded prior to calculating endpoints.
     */
    private Date updateLatestFinishDate(ScheduleEntry task, Date latestFinishDate) throws PersistenceException {
        final IDateCalculator taskDateCalculator = new ScheduleEntryDateCalculator(task, getCalendarProvider(task));

        for (Iterator it = tdf.getSuccessors(task).iterator(); it.hasNext(); ) {
            TaskDependency td = (TaskDependency)it.next();
            ScheduleEntry successor = (ScheduleEntry)tasks.get(td.getTaskID());
            //Michael Baranov: ignore successors from schedules other than task's schedule
            //this corrects external pusher behaviour
            //if(!task.getPlanID().equals(successor.getPlanID()))continue;


            // Create a new date calculator based on the schedule's default calendar
            // for calculating the starting date of the lag period
            IDateCalculator lagDateCalculator = new ScheduleDateCalculator(td.getLag(), getCalendarProvider(task));

            Date dependencyDate = null;
            if (td.getDependencyType().equals(TaskDependencyType.FINISH_TO_START)) {
                dependencyDate = lagDateCalculator.calculateStartDate(successor.getLatestStartDate());
            } else if (td.getDependencyType().equals(TaskDependencyType.FINISH_TO_FINISH)) {
                dependencyDate = lagDateCalculator.calculateStartDate(successor.getLatestFinishDate());
            } else if (td.getDependencyType().equals(TaskDependencyType.START_TO_START)) {
                dependencyDate = lagDateCalculator.calculateStartDate(successor.getLatestStartDate());
                dependencyDate = taskDateCalculator.calculateFinishDate(dependencyDate);
            } else if (td.getDependencyType().equals(TaskDependencyType.START_TO_FINISH)) {
                dependencyDate = lagDateCalculator.calculateStartDate(successor.getLatestFinishDate());
                dependencyDate = taskDateCalculator.calculateFinishDate(dependencyDate);
            }

            latestFinishDate = DateUtils.min(dependencyDate, latestFinishDate);
        }

        return latestFinishDate;
    }

    /**
     * Determine the earliest possible finish time for this task taking into
     * account the start time of the schedule, any constraints on this task, and
     * all predecessors of this task.
     *
     * @param startTime a <code>Date</code> containing the earliest start time
     * for this task.
     * @param task a <code>Task</code> object that we are going to examine to
     * find the earliest finish time.
     * @return a <code>Date</code> object containing the earliest finish time
     * for this task.
     * @throws PersistenceException if for some reason the predecessors of this
     * task were not loaded prior to calling this method.
     */
    private Date findEFT(Date startTime, ScheduleEntry task) throws PersistenceException {
        IWorkingTimeCalendar taskWorkingTimeCalendar = task.getResourceCalendar(getCalendarProvider(task));
        IDateCalculator taskDateCalculator = new ScheduleEntryDateCalculator(task, getCalendarProvider(task));

        Date earliestFinishTime;

        //If the task is already done, the end time is set.
//        if (task.getActualEndTime() != null) {
//            return task.getActualEndTime();
//        }

        //Find what the finish date is using the constraints and the schedule.
        //We will look at dependencies afterwards.
        if (task.getConstraintType().equals(TaskConstraintType.FINISH_NO_EARLIER_THAN)) {
            // A Finish No Earlier constraint dictates the earliest bound for
            // the earliest finish time, so we start with the constraint's date

            //We have two options for the earliest finish time at this point.  We
            //can either have the constraint date, or we can have the finish date
            //based on the start date + the duration.  Later, we will also look
            //at dependencies too.
            earliestFinishTime = DateUtils.max(task.getConstraintDate(), taskDateCalculator.calculateFinishDate(startTime));

            // We must continue processing because a dependency may make the
            // earliest finish time be later

        } else if (task.getConstraintType().equals(TaskConstraintType.MUST_FINISH_ON)) {
            // A Must Finish On constraint dictates the finish time, so the
            // constraint's date is always the earliest finish time
            earliestFinishTime = task.getConstraintDate();

            // We're done processing; finish time can't be earlier or later
            return earliestFinishTime;

        } else if (task.getConstraintType().equals(TaskConstraintType.START_AND_END_DATES_FIXED)) {
            // Must finish On the end date
            // This is for shared tasks in an external schedule 
            return task.getEndTime();

        } else if (task.getConstraintType().equals(TaskConstraintType.MUST_START_ON)) {
            // A Must Start On constraint means the earliest finish time is
            // the time calculate from the start on constraint date plus the work

            Date startDate = task.getConstraintDate();
            //sjmittal: I think this is not needed as the constraint dates when set
            //already take care of the working time
//            if (!taskWorkingTimeCalendar.isWorkingDay(startDate)) {
//                startDate = taskWorkingTimeCalendar.getStartOfNextWorkingDay(startDate);
//            }

            // The earliest finish time is the Must Start On date + work
            earliestFinishTime = taskDateCalculator.calculateFinishDate(startDate);

            if (!earliestFinishTime.equals(startTime)) {
                earliestFinishTime = fixupFinishDate(earliestFinishTime, taskWorkingTimeCalendar, task);
            }

            // We're done processing; finish time can't be any earlier and
            // is not going to be any later
            return earliestFinishTime;

        } else {
            // No constraints affecting finish time
            // We'll start by adding the work and continue to check
            // other dependencies
            earliestFinishTime = taskDateCalculator.calculateFinishDate(startTime);
        }

        //Look at dependencies to see if it changes the finish time derived
        //from the schedule or from constraints.
        Date dependencyDate = null;
        for (Iterator it = tdf.getPredecessors(task).iterator(); it.hasNext();) {
            TaskDependency td = (TaskDependency)it.next();
            ScheduleEntry predecessor = (ScheduleEntry)tasks.get(td.getDependencyID());
            IDateCalculator lagDateCalculator = new ScheduleDateCalculator(td.getLag(), getCalendarProvider(task));

            if (td.getDependencyType().equals(TaskDependencyType.START_TO_FINISH)) {
            	// Clear Time part to set the proper finish date according to the earliest start date.   
            	Date predecessorDate = DateUtils.clearTimePart(predecessor.getEarliestStartDate());
            	Date possibleFinishTime = DateUtils.clearTimePart(lagDateCalculator.calculateFinishDate(predecessorDate));
            	if(predecessorDate != null && earliestFinishTime != null && predecessorDate.equals(possibleFinishTime)){
            		PnCalendar pnCalendar = new PnCalendar();
            		do{
            			pnCalendar.setTime(predecessorDate);
            			pnCalendar.add(PnCalendar.DATE, 1);
            			predecessorDate = pnCalendar.getTime();
            		} while(!taskWorkingTimeCalendar.isWorkingDay(predecessorDate));
            	} else {
            		predecessorDate = predecessor.getEarliestStartDate();
            	}
                dependencyDate = lagDateCalculator.calculateFinishDate(predecessorDate);
            } else if (td.getDependencyType().equals(TaskDependencyType.FINISH_TO_FINISH)) {
                dependencyDate = lagDateCalculator.calculateFinishDate(predecessor.getEarliestFinishDate());
            } else if (td.getDependencyType().equals(TaskDependencyType.FINISH_TO_START)) {
                dependencyDate = lagDateCalculator.calculateFinishDate(predecessor.getEarliestFinishDate());
                dependencyDate = taskDateCalculator.calculateFinishDate(dependencyDate);
            } else if (td.getDependencyType().equals(TaskDependencyType.START_TO_START)) {
                dependencyDate = lagDateCalculator.calculateFinishDate(predecessor.getEarliestStartDate());
                dependencyDate = taskDateCalculator.calculateFinishDate(dependencyDate);
            }

            //1. If dependency date is before the "latest constraint end date"
            //     set the earliest finish time to that dependency date
            //   else if dependency date is after the latest constraint end date
            //     set the eft to the latest constrained end date
            //   else
            //     this dependency doesn't effect it.


            earliestFinishTime = DateUtils.max(earliestFinishTime, dependencyDate);
        }

        if (!earliestFinishTime.equals(startTime)) {
            // Fix up the finish time to be appropriate for an end time,
            // taking into consideration the constraint type of the task
            earliestFinishTime = fixupFinishDate(earliestFinishTime, taskWorkingTimeCalendar, task);
        }

        return earliestFinishTime;
    }

    /**
     * Find the earliest start date that this task could possibly have if everything
     * worked out exactly as planned.  We figure this out by iterating through all
     * the predecessors and the parent task (if applicable).  We determine the
     * earliest start time for each of the predecessors as well as the EST of
     * the summary task.  The latest of all of those becomes the earliest start
     * time.
     *
     * @param task a <code>ScheduleEntry</code> object for which you are looking
     * for the earliest start time.
     * @return a <code>Date</code> representing the earliest time at which this
     * schedule entry can be started if everything goes right.
     * @throws PersistenceException never.  This is necessary because
     * predecessors can be lazy loaded.  In the general case though, we will
     * have preloaded them to improve performance.
     */
    private Date findEST(ScheduleEntry task) throws PersistenceException {

        IWorkingTimeCalendar taskWorkingTimeCalendar = task.getResourceCalendar(getCalendarProvider(task));
        IDateCalculator taskDateCalculator = new ScheduleEntryDateCalculator(task, getCalendarProvider(task));
        Schedule schedule = scheduleMap.get(task.getPlanID());

        Date earliestStartTime = null;

        //If the actual start has occurred, est is set in stone.
        
        //Roger -- Fixes BFD-3162.  Commented out the following 3 lines. 
        //if (task.getActualStartTime() != null) {
        //    return task.getActualStartTime();
        //}

        if (task.getConstraintType().equals(TaskConstraintType.MUST_FINISH_ON)) {
            // Must Finish On constraint dictates that the earliest start time
            // is exactly that of the constraint date minus the work
            Date finishDate = task.getConstraintDate();

            // Earliest start time is calculated from the constraint date
            // working backwards
            earliestStartTime = taskDateCalculator.calculateStartDate(finishDate);

            if (task.getWorkTQ().getAmount().signum() != 0) {
                earliestStartTime = fixupStartDate(earliestStartTime, taskWorkingTimeCalendar, task);
            }

            // We're done since the start time cannot be earlier or later
            return earliestStartTime;

        } else if (task.getConstraintType().equals(TaskConstraintType.MUST_START_ON)) {
            // Must Start On constraint dictates that the earliest start time
            // is the constraint date
            // We're done since the start time can't be any different from the
            // constraint time
            return task.getConstraintDate();

        } else if (task.getConstraintType().equals(TaskConstraintType.START_AND_END_DATES_FIXED)) {
            // Must start On the start date
            // This is for shared tasks in an external schedule 
            return task.getStartTime();

        } else if (task.getConstraintType().equals(TaskConstraintType.START_NO_EARLIER_THAN)) {
            // Start No Earlier constraint dictates that the earliest boundary
            // for the start time is the constraint time
            // However, the earliest start time may actually be later if it
            // is affected by dependencies
            earliestStartTime = task.getConstraintDate();

            // Continue to check dependencies

        } else if (task.getConstraintType().equals(TaskConstraintType.FINISH_NO_EARLIER_THAN)) {
            // Finish No Earlier constraint means that the earliest boundary
            // for the start time is the constraint date minues the work
            // However, the earliest start time may actually be later if it is
            // affected by dependencies
            Date finishDate = task.getConstraintDate();

            earliestStartTime = taskDateCalculator.calculateStartDate(finishDate);

            // Continue to check dependencies

        } else {
            // No constraints affecting start time, so we'll try starting at
            // the schedule start time.  We won't actually use it though if the
            // schedule has real predecessors.  To do so would interfere with
            // the processing of which predecessor is the earliest.
            //sjmittal: simplifying the TEC
//            if (schedule.getStartConstraint().isDateConstrained() /*|| schedule.getPredecessorList().isEmpty()*/) {
                earliestStartTime = getScheduleStartDate(task);
//            }
        }

        //Make sure if there is a parent task that it doesn't have an appropriate constraint to apply
        //sjmittal: as per new design parent tasks constraints are not applicable        
//        if (task.hasParent()) {
//            SummaryTask parentTask = (SummaryTask)tasks.get(task.getParentTaskID());
//            if (parentTask.getConstraintType().equals(TaskConstraintType.START_NO_EARLIER_THAN)) {
//                Date potentialStartDate = taskDateCalculator.calculateStartDate(parentTask.getConstraintDate());
//                earliestStartTime = DateUtils.max(earliestStartTime, potentialStartDate);
//            }
//        }

        //Iterate through all of the predecessors to see which one starts the
        //latest.  The latest will be a likely candidate for the earliest start
        //time.
        for (Iterator it2 = tdf.getPredecessors(task).iterator(); it2.hasNext();) {
            TaskDependency td = (TaskDependency)it2.next();
            ScheduleEntry predecessor = (ScheduleEntry)tasks.get(td.getDependencyID());
            TaskConstraintType depConstraintType = predecessor.getConstraintType();
            TaskInformation ti2 = taskInformation.get(td.getDependencyID());
            boolean lateStarter = depConstraintType.isLateStarter();
            boolean lateDependencyInfoPresent = ti2.backwardsMark;
            IDateCalculator lagDateCalculator = new ScheduleDateCalculator(td.getLag(), getCalendarProvider(task));

            Date dependencyTime = null;
            if (td.getDependencyType().equals(TaskDependencyType.FINISH_TO_START)) {
                Date earliestFinishTime;
                if (lateStarter && lateDependencyInfoPresent) {
                    earliestFinishTime = predecessor.getLatestFinishDate();
                } else {
                    earliestFinishTime = predecessor.getEarliestFinishDate();
                }
                // set new actual end time when predecessor task is completed
                if(predecessor != null && predecessor.getActualEndTime() != null 
                			&& predecessor.getWorkPercentComplete().toString().compareTo("100%") == 0) {
                	earliestFinishTime = predecessor.getActualEndTime();
                }
                dependencyTime = lagDateCalculator.calculateFinishDate(earliestFinishTime);
            } else if (td.getDependencyType().equals(TaskDependencyType.START_TO_START)) {
                Date dependencyEarliestStartTime;
                if (lateStarter && lateDependencyInfoPresent) {
                    dependencyEarliestStartTime = predecessor.getLatestStartDate();
                } else {
                    dependencyEarliestStartTime = predecessor.getEarliestStartDate();
                }
                dependencyTime = lagDateCalculator.calculateFinishDate(dependencyEarliestStartTime);
            } else if (td.getDependencyType().equals(TaskDependencyType.FINISH_TO_FINISH)) {
                Date earliestFinishTime;
                if (lateStarter && lateDependencyInfoPresent) {
                    earliestFinishTime = predecessor.getLatestFinishDate();
                } else {
                    earliestFinishTime = predecessor.getEarliestFinishDate();
                }
                dependencyTime = lagDateCalculator.calculateFinishDate(earliestFinishTime);
                dependencyTime = taskDateCalculator.calculateStartDate(dependencyTime);
            } else if (td.getDependencyType().equals(TaskDependencyType.START_TO_FINISH)) {
                Date dependencyEarliestStartTime;
                if (lateStarter && lateDependencyInfoPresent) {
                    dependencyEarliestStartTime = predecessor.getLatestStartDate();
                } else {
                    dependencyEarliestStartTime = predecessor.getEarliestStartDate();
                }
                
                // Clear time part for equal dates and set previous date by working time calendar
                Date predecessorDate = DateUtils.clearTimePart(dependencyEarliestStartTime);
            	Date dependencyStartTime = DateUtils.clearTimePart(lagDateCalculator.calculateFinishDate(dependencyEarliestStartTime));
            	
            	if(predecessorDate != null && dependencyStartTime != null && predecessorDate.equals(dependencyStartTime)){
            		PnCalendar pnCalendar = new PnCalendar();
            		do{
            			pnCalendar.setTime(predecessorDate);
                		pnCalendar.add(PnCalendar.DATE, -1);
                		predecessorDate = pnCalendar.getTime();
            		} while(!taskWorkingTimeCalendar.isWorkingDay(predecessorDate));
            		dependencyTime = lagDateCalculator.calculateFinishDate(predecessorDate);
            	} else {
            		predecessorDate = predecessor.getEarliestStartDate();
            		dependencyTime = lagDateCalculator.calculateFinishDate(predecessorDate);
            		dependencyTime = taskDateCalculator.calculateStartDate(dependencyTime);
            	}
            }
            if(td.getDependencyType().equals(TaskDependencyType.START_TO_FINISH)){
            	earliestStartTime = DateUtils.min(dependencyTime, earliestStartTime);
            } else {
            	earliestStartTime = DateUtils.max(dependencyTime, earliestStartTime);
            }
        }

        //sjmittal: as per new design parent tasks constraints are not applicable
//        if (task.hasParent()) {
//            SummaryTask parent = (SummaryTask)tasks.get(task.getParentTaskID());
//            if (parent.getConstraintType().equals(TaskConstraintType.START_NO_EARLIER_THAN)) {
//                earliestStartTime = DateUtils.max(earliestStartTime, parent.getConstraintDate());
//            }
//        }

        if (task.getWorkTQ().getAmount().signum() != 0) {
            // Fix up the start time to be appropriate for a start time,
            // taking into consideration the constraint type of the task
            earliestStartTime = fixupStartDate(earliestStartTime, taskWorkingTimeCalendar, task);
        }

        return earliestStartTime;
    }

    /**
     * If a start date is at the end of a working day, this method will adjust
     * it to be at the start of the next working day.  We need to do this
     * because some tasks end precisely at the end of their working day
     * (5 o'clock for example.)  The next task would start immediately, which
     * would be at (5 o'clock).  This doesn't look nice though.   No work is
     * going to be done on the task then anyhow.  It will really start on the
     * next morning at 8 o'clock.  This method adjusts the date in that way.
     * <p/>
     * <b>Note:</b> This is intended to be called only from {@link #fixupStartDate(java.util.Date, net.project.calendar.workingtime.IWorkingTimeCalendar, net.project.schedule.ScheduleEntry)};
     * callers wishing to fix up a start date should call that one since it will ensure proper handling
     * of special case constraints.
     * @param startDate a <code>Date</code> on which a task starts.
     * @param workingTimeCalendar a <code>IWorkingTimeCalendar</code> object
     * that we are going to use to determine when the resource working on this
     * task actually is working.
     * @return a <code>Date</code> that is adjusted to on the start of the next
     * working day if the start date is currently pointed at the end of the
     * working day.
     */
    private static Date fixupStartDate(Date startDate, IWorkingTimeCalendar workingTimeCalendar) {

        try {
            startDate = workingTimeCalendar.ensureWorkingTimeStart(startDate);
        } catch (NoWorkingTimeException e) {
            //There isn't a next start time, so don't try to move it
            //forward.
        }

        return startDate;
    }

    /**
     * Fixes the finish date so that if it is not in working time (including
     * being at the start boundary of working time) it is moved back to the
     * end of previous working time.
     * <p/>
     * For example, if the finish date is at 8:00 AM, the returned date will
     * be at 5:00 PM on the previous day.
     * <p/>
     * <b>Note:</b> This is intended to be called only from {@link #fixupFinishDate(java.util.Date, net.project.calendar.workingtime.IWorkingTimeCalendar, net.project.schedule.ScheduleEntry)};
     * callers wishing to fix up a finish date should call that one since it will ensure proper handling
     * of special case constraints.
     * @param finishDate the date to check
     * @param workingTimeCalendar the working time calendar to use
     * @return the same date if already working time or a date set to the
     * end of the previous working time block if the specified date was not
     * in suitable working time for an end time
     */
    private static Date fixupFinishDate(Date finishDate, IWorkingTimeCalendar workingTimeCalendar) {

        try {
            finishDate = workingTimeCalendar.ensureWorkingTimeEnd(finishDate);
        } catch (NoWorkingTimeException e) {
            //There isn't a previous finish date, so we will keep the
            //current one.
        }

        return finishDate;
    }

    /**
     * Fixes up a start time to ensure that it is on an appropriate start time
     * boundary, also considering the constraint type of the schedule entry.
     * <p/>
     * The following constraints currently have special handling:
     * <ul>
     * <li>{@link TaskConstraintType#MUST_START_ON} - fixed-up time is exactly that of the constraint time
     * <li>{@link TaskConstraintType#START_NO_LATER_THAN} - fixed-up time is the earlier of
     * <li>{@link TaskConstraintType#START_NO_EARLIER_THAN} - fixed-up time is the later of
     * the constraint time and the fixed up time; this accounts for constraint dates falling on non-working
     * days
     * @param startDate the date to fix up
     * @param workingTimeCalendar the working time calendar to use when
     * finding the appropriate start time
     * @param scheduleEntry the schedule entry that may affect exactly what
     * start time to use
     * @return the fixed-up start time which may be the same date if it was
     * already appropriate
     * @throws NullPointerException if any parameter is null
     * @throws PnetRuntimeException if there is a problem getting the constraint
     */
    private static Date fixupStartDate(Date startDate, IWorkingTimeCalendar workingTimeCalendar, ScheduleEntry scheduleEntry) {

        if (startDate == null || scheduleEntry == null || workingTimeCalendar == null) {
            throw new NullPointerException("Required parameter missing");
        }

        Date returnDate;

        if (scheduleEntry.getConstraintType().equals(TaskConstraintType.MUST_START_ON)) {
            // A Must Start On Constraint
            // causes the start date to be set to the constraint date
            returnDate = scheduleEntry.getConstraintDate();

        } else if (scheduleEntry.getConstraintType().equals(TaskConstraintType.START_NO_LATER_THAN)) {
            // Starts on earlier of constraint date and fixed-up date
            Date fixedUpDate = fixupStartDate(startDate, workingTimeCalendar);
            Date constraintDate = scheduleEntry.getConstraintDate();
            returnDate = (constraintDate.before(fixedUpDate) ? constraintDate : fixedUpDate);

        } else if (scheduleEntry.getConstraintType().equals(TaskConstraintType.START_NO_EARLIER_THAN)) {
            //sjmittal added this constraint type for fxing bfd 3627
            Date fixedUpDate = fixupStartDate(startDate, workingTimeCalendar);
            Date constraintDate = scheduleEntry.getConstraintDate();
            //return date can never be "fixed-up" after the constraint date
            returnDate = (constraintDate.after(fixedUpDate) ? constraintDate : fixedUpDate);
            
        } else {
            // For all other constraint types, constraint does not affect start date
            // Start Date is fixed up using normal mechanism
            returnDate = fixupStartDate(startDate, workingTimeCalendar);

        }

        return returnDate;
    }

    /**
     * Fixes up a finish time to ensure that it is on an appropriate end time
     * boundary, also considering the constraint type of the schedule entry.
     * <p>
     * Currently <code>{@link TaskConstraintType#MUST_FINISH_ON}</code> ,
     * <code>{@link TaskConstraintType#FINISH_NO_EARLIER_THAN}</code> and 
     * <code>{@link TaskConstraintType#FINISH_NO_LATER_THAN}</code>constraints
     * cause the fixed-up time to be exactly that of the constraint time.
     * </p>
     * @param finishDate the date to fix up
     * @param workingTimeCalendar the working time calendar to use when
     * finding the appropriate finish time
     * @param scheduleEntry the schedule entry that may affect exactly what
     * finish time to use
     * @return the fixed-up finish time which may be the same date if it was
     * already appropriate
     * @throws NullPointerException if any parameter is null
     * @throws PnetRuntimeException if there is a problem getting the constraint
     */
    private static Date fixupFinishDate(Date finishDate, IWorkingTimeCalendar workingTimeCalendar, ScheduleEntry scheduleEntry) {

        if (finishDate == null || scheduleEntry == null || workingTimeCalendar == null) {
            throw new NullPointerException("Required parameter missing");
        }

        Date returnDate;

        if (scheduleEntry.getConstraintType().equals(TaskConstraintType.MUST_FINISH_ON)) {
            // A Must Finish On Constraint
            // causes the finish date to be set to the constraint date
            returnDate = scheduleEntry.getConstraintDate();

        } else if (scheduleEntry.getConstraintType().equals(TaskConstraintType.FINISH_NO_EARLIER_THAN)) {
            // A Finish No Earlier Than constraint requires the finish date to be fixed-up,
            // but it can never be "fixed-up" to a date earlier than the constraint date
            // This scenario can occur if the constraint date falls on a non-working day
            Date fixedUpDate = fixupFinishDate(finishDate, workingTimeCalendar);
            Date constraintDate = scheduleEntry.getConstraintDate();
            returnDate = (constraintDate.after(fixedUpDate) ? constraintDate : fixedUpDate);

        } else if (scheduleEntry.getConstraintType().equals(TaskConstraintType.FINISH_NO_LATER_THAN)) {
            //sjmittal added this constraint type for fxing bfd 2051
            Date fixedUpDate = fixupFinishDate(finishDate, workingTimeCalendar);
            Date constraintDate = scheduleEntry.getConstraintDate();
            //return date can never be "fixed-up" after the constraint date
            returnDate = (constraintDate.before(fixedUpDate) ? constraintDate : fixedUpDate);
            
        } else {
            // For all other constraint types, constraint does not affect finish date
            // Finish Date is fixed up using normal mechanism
            returnDate = fixupFinishDate(finishDate, workingTimeCalendar);

        }

        return returnDate;
    }

    /**
     * Add schedule entries to the {@link #tasksWithoutPredecessors},
     * {@link #tasksWithoutSuccessors} table so we know what order in
     * which to traverse the tasks.
     *
     * @throws PersistenceException if there is any trouble loading dependent
     * task information from the database.  The hope is that by this point, all
     * lazy loaded information will have already been loaded.
     */
    private void findTopologicalOrder() throws PersistenceException {

        //Iterate through each task, trying to find the ones that don't have
        //any FS dependencies
        for (Iterator it = tasks.values().iterator(); it.hasNext();) {
            ScheduleEntry t = (ScheduleEntry)it.next();

            if (t instanceof SummaryTask) {
                summaryTasks.add((SummaryTask)t);

                //We don't want to process summary tasks as part of the normal
                //forward and back search for [el][sf]t.
                continue;
            }

            if (tdf.getPredecessors(t).isEmpty()) {
                tasksWithoutPredecessors.add(t);
            }

            if (tdf.getSuccessors(t).isEmpty()) {
                tasksWithoutSuccessors.add(t);
            }
        }

        topologyCalculated = true;
    }

    /**
     * Determine the start date and finish date for tasks based on their EST,
     * LST, EFT, and LFT.  If requested, also store the tasks.
     *
     * This method is called recursively.
     *
     * @param tasksToSave the current "round" of tasks to save.
     * @param storeTasks a <code>boolean</code> indicating if we should be
     * saving these tasks in the database.
     * @param db a <code>DBBean</code> object which is used to store tasks.
     * @throws SQLException if there is a problem saving a task to the
     * database.
     */
    private void storeTasks(Collection tasksToSave, boolean storeTasks, DBBean db) throws PersistenceException, SQLException {
        //Store the schedule's new finish date, if applicable.
        storeNewScheduleEndDate(storeTasks, db);

        for (Iterator it = tasksToSave.iterator(); it.hasNext();) {
            ScheduleEntry task = (ScheduleEntry)it.next();
            //Find the task information for this task
            TaskInformation ti = taskInformation.get(task.getID());

            if (ti == null) {
                ti = new TaskInformation();
                //Store the information that we've collected about est and eft.
                taskInformation.put(task.getID(), ti);
            }            

            assert ti != null : "Unable to find ti information for task " + task.getID() + " while storing";

            if (ti.isModified() && storeTasks) {
                //We now have real times for the date, make sure the database
                //knows this.
                task.setIgnoreTimePortionOfDate(false);
                task.setSendNotifications(false);
                task.store(false, (scheduleMap.get(task.getPlanID())), db);
                task.setSendNotifications(true);
            }
        }
    }

    /**
     * Update the duration amounts for all summary tasks.
     * @param doStoreTasks true if tasks should be stored (if they change); false to not store tasks
     * @param db the DBBean in which to perform the transaction
     */
    private void updateSTDuration(final boolean doStoreTasks, DBBean db) throws SQLException, PersistenceException {
        // Now update Summary Task durations
        // We have to wait until all tasks are set correctly since duration depends on
        // their children's duration which requires start date, work and assignments
        for (Iterator<SummaryTask> iterator = summaryTasks.iterator(); iterator.hasNext();) {
            SummaryTask summaryTask = iterator.next();
            if (!summaryTask.hasParent()) {
                computeDaysWorked(summaryTask, doStoreTasks, db);
            }
        }
    }

    /**
     * Computes the days worked (duration) for the given summary task, updating it
     * and returning the result so that it may be included its parent's daysworked.
     * @param summaryTask the summary task to update
     * @param doStoreTasks true if tasks should be stored (if they change); false to not store tasks
     * @param db the DBBean in which to perform the transaction
     * @return the days worked for that summary task and its children
     */
    private IDaysWorked computeDaysWorked(final SummaryTask summaryTask, final boolean doStoreTasks, DBBean db) throws SQLException, PersistenceException {

        IDaysWorked daysWorked = new DaysWorked();

        try {
            for (Iterator it = summaryTask.getChildren().iterator(); it.hasNext();) {
                ScheduleEntry nextScheduleEntry = (ScheduleEntry) it.next();

                if (nextScheduleEntry.getTaskType().equals(TaskType.SUMMARY)) {
                    // Recurse into this summary task, updating it then add its days worked to
                    // the current summary task's daysworked value
                    daysWorked.add(computeDaysWorked((SummaryTask) nextScheduleEntry, doStoreTasks, db));

                } else {
                    // Non-summary task simply adds its days worked to this summary task's value
                    daysWorked.add(nextScheduleEntry.getDaysWorked(getCalendarProvider(nextScheduleEntry)));
                }
            }

        } catch (PersistenceException e) {
            // Days worked remains as calculated or zero days

        }

        // Now update this summary task's duration value from days worked
        TimeQuantity computedDuration = new TimeQuantity(daysWorked.getTotalDays(), TimeQuantityUnit.DAY);
        if (!computedDuration.equals(summaryTask.getDurationTQ())) {
            summaryTask.setDuration(computedDuration);

            // Task needs to be stored
            if (doStoreTasks) {
                summaryTask.setIgnoreTimePortionOfDate(false);
                summaryTask.setSendNotifications(false);
                summaryTask.store(false, (scheduleMap.get(summaryTask.getPlanID())), db);
                summaryTask.setSendNotifications(true);
            }
        }

        // Return this summary task's daysworked for inclusion in its parent's
        return daysWorked;
    }

    /**
     * If the "end date" of the schedule has changed, update it in the database.
     *
     * @param storeSchedule a <code>boolean</code> indicating if we are supposed to
     * be storing things.  If we aren't, we won't commit the changes to the
     * database, although we will still alter the schedule object's finish time.
     * @throws SQLException
     */
    private void storeNewScheduleEndDate(boolean storeSchedule, DBBean db) throws SQLException {
        for (Iterator<ScheduleInformation> it = scheduleInformation.values().iterator(); it.hasNext();) {
            ScheduleInformation info =  it.next();
            Schedule schedule = scheduleMap.get(info.id);
            boolean scheduleStored = false;
            if (info.isModified()) {
                schedule.setScheduleStartDate(info.getStartDate());
                schedule.setScheduleEndDate(info.getEndDate());

                if (storeSchedule) {
                    schedule.store(db);
                    scheduleStored = true;
                }
            }
            if(storeSchedule && !scheduleStored) {
                //sjmittal: if schedule is not stored we would still update the 
                //external dependcies to this schedule as other information (other than end date)
                //might have been changed
                ExternalDependencies.update(schedule, db);
            }
        }
    }

    //
    // Nested top-level classes
    //

    /**
     * This object stores intermediate information that we need during processing
     * but that we might not need when we aren't calculating.
     *
     * @author Matthew Flower
     * @since Version 7.6
     */
    private class TaskInformation implements Cloneable {
        /**
         * Pushers are tasks with want to start "as late as possible" when this task
         * want to start as early as possible.  It is a situation in which the
         * schedule calculation is a bit more difficult because the start date of a
         * task is not equal to the est when the task wants to start early.
         */
        private List pushers;
        /**
         * Indicates that we've already visited this task on the way back through
         * the traversal.
         */
        boolean backwardsMark = false;
        /**
         * Indicates that we've visited this task on the way through the traversal.
         */
        boolean forwardMark = false;
        /**
         * Indicates that this summary task has already received its start and end
         * dates.
         */
        boolean summaryMark = false;
        /**
         * Indicates that we have finished working with this task and that it has
         * been stored.  (If necessary)
         */
        boolean finalized = false;
        /**
         * If (and only if) this task is a summary task, it indicates the new
         * work amount of this summary task.
         */
        TimeQuantity work;
        /**
         * If (and only if) this task is a summary task, this field indicates the
         * amount of work that has been completed by subtasks.
         */
        TimeQuantity workComplete;
        /**
         * Force the task to be stored when others are being stored.
         */
        boolean forceStore = false;
        /**
         * Indicates that this task is modified and needs to be stored
         */
        boolean isModified = false;

        public void addPusher(TaskDependency pusher) {
            //We don't create a list until the list is needed
            if (pushers == null) {
                pushers = new LinkedList();
            }

            pushers.add(pusher);
        }

        public boolean hasPushers() {
            return !(pushers == null || pushers.isEmpty());
        }

        /**
         * Get the new amount of work for this task.  This only applies to summary
         * tasks.
         *
         * @return a <code>TimeQuantity</code> of work that the summary task will
         * be performing.
         */
        public TimeQuantity getWork() {
            return work;
        }

        /**
         * Set the new amount of work for this summary task.  This only applies to
         * summary tasks and will be ignored otherwise.
         *
         * @param work a <code>TimeQuantity</code> indicating the amount of work a
         * summary task will do.
         */
        public void setWork(TimeQuantity work) {
            this.work = work;
        }

        public TimeQuantity getWorkComplete() {
            return workComplete;
        }

        public void setWorkComplete(TimeQuantity workComplete) {
            this.workComplete = workComplete;
        }

        public void setModified(boolean modified) {
            this.isModified = modified;
        }

        public boolean isModified() {
            return isModified;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TaskInformation)) return false;

            final TaskInformation taskInformation = (TaskInformation) o;

            if (backwardsMark != taskInformation.backwardsMark) return false;
            if (finalized != taskInformation.finalized) return false;
            if (forceStore != taskInformation.forceStore) return false;
            if (forwardMark != taskInformation.forwardMark) return false;
            if (isModified != taskInformation.isModified) return false;
            if (summaryMark != taskInformation.summaryMark) return false;
            if (pushers != null ? !pushers.equals(taskInformation.pushers) : taskInformation.pushers != null) return false;
            if (work != null ? !work.equals(taskInformation.work) : taskInformation.work != null) return false;
            if (workComplete != null ? !workComplete.equals(taskInformation.workComplete) : taskInformation.workComplete != null) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = (pushers != null ? pushers.hashCode() : 0);
            result = 29 * result + (backwardsMark ? 1 : 0);
            result = 29 * result + (forwardMark ? 1 : 0);
            result = 29 * result + (summaryMark ? 1 : 0);
            result = 29 * result + (finalized ? 1 : 0);
            result = 29 * result + (work != null ? work.hashCode() : 0);
            result = 29 * result + (workComplete != null ? workComplete.hashCode() : 0);
            result = 29 * result + (forceStore ? 1 : 0);
            result = 29 * result + (isModified ? 1 : 0);
            return result;
        }

        protected Object clone() throws CloneNotSupportedException {
            TaskInformation ti = new TaskInformation();
            duplicateInternalFields(ti);
            return ti;
        }

        private void duplicateInternalFields(TaskInformation ti) {
            ti.pushers = this.pushers;
            ti.backwardsMark = this.backwardsMark;
            ti.forwardMark = this.forwardMark;
            ti.summaryMark = this.summaryMark;
            ti.finalized = this.finalized;
            ti.work = this.work;
            ti.workComplete = this.workComplete;
            ti.forceStore = this.forceStore;
            ti.isModified = this.isModified;
        }
    }


    private class ScheduleInformation {
        private final Date originalStartDate;
        private final Date originalEndDate;
        private final String id;
        private Date startDate;
        private Date endDate;
        private Date est;
        private Date eft;
        private Date lst;
        private Date lft;

        public ScheduleInformation(Schedule schedule) {
            originalStartDate = schedule.getScheduleStartDate();
            originalEndDate = schedule.getScheduleEndDate();
            id = schedule.getID();

            startDate = originalStartDate;
            endDate = originalEndDate;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }

        public boolean isModified() {
            boolean needsStoring = !DateUtils.isEqual(originalStartDate, startDate);
            needsStoring = needsStoring || !DateUtils.isEqual(originalEndDate, endDate);

            return needsStoring;
        }

        public Date getEST() {
            return est;
        }

        public void setEST(Date est) {
            this.est = est;
        }

        public Date getEFT() {
            return eft;
        }

        public void setEFT(Date eft) {
            this.eft = eft;
        }

        public Date getLST() {
            return lst;
        }

        public void setLST(Date lst) {
            this.lst = lst;
        }

        public Date getLFT() {
            return lft;
        }

        public void setLFT(Date lft) {
            this.lft = lft;
        }
    }
    
}

