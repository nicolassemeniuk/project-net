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
/**
 * 
 */
package net.project.hibernate.service.impl;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import net.project.calendar.workingtime.IWorkingTimeCalendar;
import net.project.channel.ScopeType;
import net.project.hibernate.service.IAddTaskService;
import net.project.persistence.PersistenceException;
import net.project.resource.PersonProperty;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleEntryFactory;
import net.project.schedule.SummaryTask;
import net.project.schedule.TaskConstraintType;
import net.project.schedule.TaskFinder;
import net.project.schedule.TaskListUtils;
import net.project.schedule.TaskPriority;
import net.project.schedule.TaskType;
import net.project.schedule.calc.ScheduleEntryCalculator;
import net.project.schedule.mvc.handler.main.QuickAddHandler;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.util.Validator;

/**
 *
 */
@Service(value="addTaskService")
public class AddTaskServiceImpl implements IAddTaskService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IAddTaskService#quickAdd(java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String, java.lang.String[], net.project.schedule.Schedule,
	 *      java.lang.String)
	 */
	public String quickAdd(String taskName, String taskDescription, String work, String workUnits, String startTimeString,
			String endTimeString, String[] selected, Schedule schedule)
			throws Exception {
		TimeQuantity newWork = TimeQuantity.parse(work, workUnits);

		// Construct the schedule entry for the specified dates (if specified)
		ScheduleEntry scheduleEntry = QuickAddHandler.createScheduleEntry(startTimeString, endTimeString, newWork, schedule);
		
		// Determine where in the schedule we want to insert this new task
		if (selected != null && selected.length > 0 && StringUtils.isNotEmpty(selected[0])) {
            ScheduleEntry neighborAbove = (ScheduleEntry)schedule.getEntryMap().get(selected[0]);
            PersonProperty property = new PersonProperty();
            property.setScope(ScopeType.SPACE.makeScope(SessionManager.getUser()));
            property.prefetchForContextPrefix("prm.schedule.main");
            String[] expanded = property.get("prm.schedule.main", "node" + neighborAbove.getID() + "expanded");
            boolean isNeighborAboveExpanded = true;
            if(expanded != null && expanded.length > 0)
                isNeighborAboveExpanded = Boolean.parseBoolean(expanded[0]);
            QuickAddHandler.addScheduleEntryBelow(schedule, scheduleEntry, neighborAbove, isNeighborAboveExpanded);
        }
		
		scheduleEntry.setName(taskName);
		scheduleEntry.setDescription(taskDescription);
		scheduleEntry.setPlanID(schedule.getID());
		scheduleEntry.setPriority(TaskPriority.PRIORITY_NORMAL.getID());
		scheduleEntry.setConstraintType(TaskConstraintType.AS_SOON_AS_POSSIBLE);
		scheduleEntry.setIgnoreTimePortionOfDate(true);
		scheduleEntry.setTaskCalculationType(schedule.getDefaultTaskCalculationType());

		// Update work and recalculate duration as appropriate
		if (schedule.isAutocalculateTaskEndpoints()) {
			//For autocalculate, duration is based on the work
			ScheduleEntryCalculator calc = new ScheduleEntryCalculator(scheduleEntry, schedule.getWorkingTimeCalendarProvider());
			calc.workChanged(newWork);
		} else {
			// For non-autocalculate, duration is always 1 day
			scheduleEntry.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));
		}
		
		scheduleEntry.store(true, schedule);
		return scheduleEntry.getID();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IAddTaskService#addASubTaskUnderSelectedTask(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 *      net.project.schedule.Schedule, java.lang.String)
	 */
	public String quickAddASubTaskUnderSelectedTask(String taskName, String taskDescription, String work, String workUnits,
			String startTimeString, String endTimeString, String selectedTaskId, Schedule schedule, String nodeExpanded)
			throws Exception {
			
		String[] selectedTaskIds = {selectedTaskId}; 
		if(selectedTaskId == null){
			selectedTaskIds = null;
		}
		
		String newTaskId = quickAdd(taskName, taskDescription, work, workUnits, startTimeString, endTimeString,
				selectedTaskIds, schedule);
		
		// Getting ScheduleEntry of selectedTask to determine selectedTask is summaryTask or not.
		// If selectedTask is not summayTask then indent newTask to right to convert selectedTask to 
		// summaytask of new created task 
		if (selectedTaskId != null) {
			ScheduleEntry parnetTask = (ScheduleEntry) schedule.getEntryMap().get(selectedTaskId);

			if (!(parnetTask instanceof SummaryTask)) {
				schedule.loadAll();
				TaskListUtils.indentTasks(newTaskId.split(","), selectedTaskId, schedule);
			}
		}
		return newTaskId;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IAddTaskService#calculateScheduleEntry(net.project.schedule.Schedule, java.lang.String, java.lang.String, java.util.Date, java.util.Date)
	 */
	public ScheduleEntry reCalculateScheduleEntry(Schedule schedule,ScheduleEntry scheduleEntry, String work, String workUnit, Date date,  String calculateBy, User user) throws ParseException {
		//Recalculate task
		Schedule cloneSchedule = null;
		try {
			cloneSchedule = createClonedSchedule(schedule, scheduleEntry);
		} catch (PersistenceException pnetEx) {
		}
		ScheduleEntryCalculator calc = new ScheduleEntryCalculator(scheduleEntry, cloneSchedule.getWorkingTimeCalendarProvider());
		
		if(calculateBy.equals("work") || calculateBy.equals("workUnit")){
			//Converting work in timeQuantity
			TimeQuantity newWork = TimeQuantity.parse(work, workUnit);
			calc.workChanged(newWork);
		}else if (calculateBy.equals("startDate")) {
            calc.startDateChanged(cloneSchedule, date, user.getTimeZone());
        } else if (calculateBy.equals("dueDate")) {
            calc.endDateChanged(cloneSchedule, date, user.getTimeZone());
        }
		return scheduleEntry;
	}
	
	public ScheduleEntry getNewScheduleEntry(Schedule schedule, String createAfterID) throws PersistenceException, SQLException {
        //Check to make sure that there isn't a scheduleEntry attribute in the
        //request
        ScheduleEntry scheduleEntry = ScheduleEntryFactory.createFromType(TaskType.TASK);
            
        // Set up default values
        IWorkingTimeCalendar workingTimeCalendar = schedule.getWorkingTimeCalendar();
        
        //note startTime would never be null
        Date startTime = schedule.getScheduleStartDate();
        if (workingTimeCalendar.isWorkingDay(startTime)) {
            scheduleEntry.setStartTimeD(workingTimeCalendar.getStartOfWorkingDay(startTime));
        } else {
            scheduleEntry.setStartTimeD(workingTimeCalendar.getStartOfNextWorkingDay(startTime));
        }

        Date endTime = scheduleEntry.getStartTime();
        scheduleEntry.setEndTimeD(workingTimeCalendar.getEndOfWorkingDay(endTime));
        
        scheduleEntry.setDuration(new TimeQuantity(1, TimeQuantityUnit.DAY));
        scheduleEntry.setWork(new TimeQuantity(8, TimeQuantityUnit.HOUR));

        //If the user asked that this task be created after a given sequence number
        //make sure we put it there
        if (!Validator.isBlankOrNull(createAfterID)) {

            ScheduleEntry neighborAbove = (ScheduleEntry)schedule.getEntryMap().get(createAfterID);
            scheduleEntry.setSequenceNumber(neighborAbove.getSequenceNumber()+1);
            
            //Figure out what the parent task id of the task below is
            TaskFinder tf = new TaskFinder();
            List entries = tf.findBySequenceNumber(schedule.getID(), scheduleEntry.getSequenceNumber());
            ScheduleEntry neighborBelow = (entries.size() != 0 ? (ScheduleEntry)entries.get(0) : null);

            //Now we have to figure out which schedule entry has a greater
            //indent level, the one above or the one below.  We will place
            //our entry at the location with the greater depth.
            //  Task 1            Task 1           Task 1
            //    [Insert]        [Insert]         [Insert]
            //    Task 2        Task 2             Task 2
            //
            //This information won't be available if the user didn't load
            //the schedule in "Indented Hierarchical Format"
            //
            int levelAbove = QuickAddHandler.getScheduleEntryHierarchyDepth(neighborAbove);
            int levelBelow = QuickAddHandler.getScheduleEntryHierarchyDepth(neighborBelow);

            //Set the parent to be the parent of whichever task has a greater
            //hierarchy depth.
            if (levelAbove > levelBelow || levelAbove == levelBelow) {
                scheduleEntry.setParentTaskID(neighborAbove.getParentTaskID());
            } else {
                scheduleEntry.setParentTaskID(neighborBelow.getParentTaskID());
            }
        }
        
        scheduleEntry.setPlanID(schedule.getID());
        scheduleEntry.setTaskCalculationType(schedule.getDefaultTaskCalculationType());
        Schedule cloneSchedule = createClonedSchedule(schedule, scheduleEntry);
        
        //First Schedule entry start date will be schedule date and than it is to set today as start date and recalculate shedule Entry.
        ScheduleEntryCalculator calc = new ScheduleEntryCalculator(scheduleEntry, cloneSchedule.getWorkingTimeCalendarProvider());
        calc.startDateChanged(cloneSchedule, new Date(), SessionManager.getUser().getTimeZone());
        
        return scheduleEntry;
    }
	
	public void indentTaskUnderSelectedTask(String newTaskId, String selectedTaskId, Schedule schedule) throws PersistenceException{
		if (StringUtils.isNotEmpty(selectedTaskId)) {
			ScheduleEntry parnetTask = (ScheduleEntry) schedule.getEntryMap().get(selectedTaskId);
			//indent new task under selected task if slected task is not a summary task
			if (!(parnetTask instanceof SummaryTask)) {
				//schedule.loadAll();
				TaskListUtils.indentTasks(newTaskId.split(","), selectedTaskId, schedule);
			}
		}
	}
	
	 /**
     * Creates a clone of the schedule to permit endpoint calculations without affecting
     * the session schedule.
     * <p/>
     * The specified scheduleEntry is added or replaced in the clone such that recalculating
     * the schedule will automatically see the modified scheduleEntry.
     * <p/>
     * Tasks being created will have null task IDs and will be placed in the schedule with a null ID.
     * @param schedule the schedule to clone
     * @param scheduleEntry the scheduleEntry to ensure is present in the clone
     * @return the cloned schedule
     */
    private Schedule createClonedSchedule(Schedule schedule, ScheduleEntry scheduleEntry) throws PersistenceException {
        Schedule calcSchedule = null;

        if (schedule.isFiltered()) {
            //Make sure our current calculation schedule isn't null.
            if (calcSchedule == null || calcSchedule.getID() != schedule.getID() ||
                calcSchedule.getLoadTime().before(schedule.getLoadTime())) {

                calcSchedule = (Schedule)schedule.clone();
                calcSchedule.clearFinderFilterList();
                calcSchedule.loadAll();
            }
            //If we get this far, there is a calculation schedule and it is up to date.
        } else {
            //The schedule isn't filtered -- it could probably be used for calculation
            calcSchedule = (Schedule)schedule.clone();
        }
        //setSessionVar("calcSchedule", calcSchedule);

        //Now we have a proper calculation schedule.  We need to not modify this
        //schedule directory -- always modify a clone.  Loading is expensive,
        //cloning is relatively cheap.  We always need the calc schedule to be
        //pristene.  By making a copy of the calc schedule it means that the
        //next time we do this calculation we won't have to load it from
        //scratch again.
        Schedule clonedSchedule = (Schedule)calcSchedule.clone();
        Map currentTasks = new HashMap(clonedSchedule.getEntryMap());
        currentTasks.put(scheduleEntry.getID(), scheduleEntry);
        clonedSchedule.setEntryMap(currentTasks);

        return clonedSchedule;
    }
}
