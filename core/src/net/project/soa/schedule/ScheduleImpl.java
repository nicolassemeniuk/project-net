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
package net.project.soa.schedule;

import net.project.base.finder.NumberComparator;
import net.project.base.finder.NumberFilter;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.Task;
import net.project.schedule.TaskFinder;
import net.project.schedule.TaskType;
import net.project.security.SessionManager;


public class ScheduleImpl extends Schedule implements ISchedule {
   
	public ScheduleEntry[] getMilestones() throws Exception{
		Schedule schedule = new Schedule(); 
		schedule.clearFinderFilterList();
	
	    schedule.setSpace(SessionManager.getUser().getCurrentSpace());
	    schedule.setHierarchyView(Schedule.HIERARCHY_VIEW_EXPANDED);
	    // load the schedule
	    schedule.load();
	
	    // sort column by start_date
	    schedule.setOrder("5");
	    schedule.setOrderDirection(0);
	
	    // Load all entries
	    schedule.setMaximumEntries(-1);
	
	    //schedule.setStartDateFilter(cal.getMidnight());
	    NumberFilter percentCompleteFilter = new NumberFilter("percentComplete", TaskFinder.PERCENT_COMPLETE_COLUMN, false);
	    percentCompleteFilter.setSelected(true);
	    percentCompleteFilter.setComparator(NumberComparator.LESS_THAN);
	    percentCompleteFilter.setNumber(100);
	    schedule.addFinderFilter(percentCompleteFilter);
	
	    // load Milestone entries
	    // We avoid loading dependencies and assignments to improve
	    // performance; we don't care about those
	    schedule.setHierarchyView(Schedule.HIERARCHY_VIEW_EXPANDED);
	    schedule.loadEntries(new TaskType[] {TaskType.MILESTONE}, false, false);
	
	    // Reset settings
	    schedule.clearFinderFilterList();
	    schedule.setMaximumEntries(-1);
	    schedule.setOrder("0");
	    
	    Object[] arr =  schedule.getEntries().toArray();
	    Task[] tasks = new Task[arr.length];
	    for(int i=0;i<arr.length;i++){
	    	tasks[i] = (Task)arr[i];
	    }
	    return tasks;
	}
	
	public ScheduleEntry[] getAllTaskEntries() throws Exception{
		Schedule schedule = new Schedule(); 
		schedule.clearFinderFilterList();
	
	    schedule.setSpace(SessionManager.getUser().getCurrentSpace());
	    schedule.setHierarchyView(Schedule.HIERARCHY_VIEW_EXPANDED);
	    try{
		    // load the schedule
	    	schedule.loadEntries(new TaskType[] {TaskType.TASK}, false, false);
	    }catch(Exception ex){
	    	return null;
	    }
	    // sort column by start_date
	    schedule.setOrder("5");
	    schedule.setOrderDirection(0);

	    Object[] arr =  schedule.getEntries().toArray();
	    ScheduleEntry[] tasks = new ScheduleEntry[arr.length];
	    for(int i=0;i<arr.length;i++){
	    	tasks[i] = (ScheduleEntry)arr[i];
	    }
	    return tasks;
	}
	
	public ScheduleEntry[] getAllSummaryTaskEntries() throws Exception{
		Schedule schedule = new Schedule(); 
		schedule.clearFinderFilterList();
	
	    schedule.setSpace(SessionManager.getUser().getCurrentSpace());
	    schedule.setHierarchyView(Schedule.HIERARCHY_VIEW_EXPANDED);
	    try{
		    // load the schedule
	    	schedule.loadEntries(new TaskType[] {TaskType.SUMMARY}, false, false);
	    }catch(Exception ex){
	    	return null;
	    }
	    // sort column by start_date
	    schedule.setOrder("5");
	    schedule.setOrderDirection(0);

	    Object[] arr =  schedule.getEntries().toArray();
	    ScheduleEntry[] summaryTasks = new ScheduleEntry[arr.length];
	    for(int i=0;i<arr.length;i++){
	    	summaryTasks[i] = (ScheduleEntry)arr[i];
	    }
	    return summaryTasks;
	}
}
