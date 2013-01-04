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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule.report.taskscomingdue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.base.finder.CheckboxFilter;
import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.DateFilter;
import net.project.base.finder.DuplicateFilterIDException;
import net.project.base.finder.EmptyFinderGrouping;
import net.project.base.finder.FinderFilterList;
import net.project.base.finder.FinderSorter;
import net.project.base.finder.WhereClauseFilter;
import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.form.assignment.FormAssignment;
import net.project.persistence.PersistenceException;
import net.project.report.ReportAssignmentType;
import net.project.report.SummaryDetailReportData;
import net.project.resource.AssignmentFinder;
import net.project.resource.AssignmentList;
import net.project.resource.AssignmentManager;
import net.project.resource.AssignmentType;
import net.project.schedule.Task;
import net.project.schedule.TaskFinder;
import net.project.schedule.calc.TaskCalculationType;
import net.project.schedule.report.AssigneeGrouping;
import net.project.schedule.report.FilterUser;
import net.project.schedule.report.ReportUtils;
import net.project.schedule.report.UserFilter;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.util.DateUtils;

/**
 * Class designed to contain the data queried in order to construct the Tasks
 * Coming Due report.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
class TasksComingDueData extends SummaryDetailReportData {
    /** Label for the default grouping. */
    private String DEFAULT_GROUPING = PropertyProvider.get("prm.schedule.report.taskscomingdue.grouping.default.name"); //
    /** Label for the "Show Tasks Between These Dates" filter. */
    private String SHOW_TASKS_BETWEEN_THESE_DATES = PropertyProvider.get("prm.schedule.report.taskscomingdue.showtasksbetweenthesedates.name");  //"Show tasks which finish between these dates";
    /**
     * Token pointing to: "Unexpected programmer error found while constructing
     * the list of report filters."
     */
    private String FILTER_LIST_CONSTRUCTION_ERROR = "prm.report.errors.filterlistcreationerror.message";

    /**
     * Variable to contain all of the data that will be used to construct the
     * summary section of the Tasks Coming Due Report.
     */
    private TasksComingDueSummaryData summaryData = null;

    /**
     * Standard constructor.  Populates internal data.
     */
    public TasksComingDueData() {
        populateFinderFilterList();
        populateFinderSorterList();
        populateFinderGroupingList();
    }

    /**
     * Create the list of <code>FinderGrouping</code> classes that will be used
     * on the HTML page to allow the user to select task grouping.
     */
    private void populateFinderGroupingList() {
        groupingList.add(new EmptyFinderGrouping("10", DEFAULT_GROUPING, true));
        groupingList.add(new AssigneeGrouping("20", false));
    }

    /**
     * Populate the list of sorters with all sorters that this report supports.
     */
    private void populateFinderSorterList() {
        for (int i = 1; i < 4; i++) {
            FinderSorter fs = new FinderSorter(String.valueOf(i * 10),
                new ColumnDefinition[]{TaskFinder.NAME_COLUMN, TaskFinder.TYPE_COLUMN,
                                       TaskFinder.DATE_START_COLUMN, TaskFinder.DATE_FINISH_COLUMN,
                                       TaskFinder.WORK_COMPLETE_COLUMN, TaskFinder.WORK_COLUMN,
                                       TaskFinder.WORK_PERCENT_COMPLETE_COLUMN},
                TaskFinder.NAME_COLUMN);
            sorterList.add(fs);
        }
    }

    /**
     * Populate the list of filters with all filters that are available for this
     * report.
     */
    private void populateFinderFilterList() {
        try {
            //We need to get the space from sessionmanager because this
            //filter is constructed during the construction of the object.
            //The user hasn't had a chance to setSpace() yet.
            Space space = SessionManager.getUser().getCurrentSpace();
            UserFilter userFilter = new UserFilter("10", space);
            CheckboxFilter userFilterCheckbox = new CheckboxFilter("20", userFilter);
            filterList.add(userFilterCheckbox);

            //Add a filter for the date
            DateFilter dateFilter = new DateFilter("40", TaskFinder.DATE_FINISH_COLUMN,
                SHOW_TASKS_BETWEEN_THESE_DATES, false);
            dateFilter.setTruncateDates(true);
            filterList.add(new CheckboxFilter("30", dateFilter));
        } catch (DuplicateFilterIDException e) {
            throw new RuntimeException(
                PropertyProvider.get(FILTER_LIST_CONSTRUCTION_ERROR), e);
        }
    }

    /**
     * Clear out any data stored in this object and reset.
     */
    public void clear() {
        isLoaded = false;
        detailedData = null;
        summaryData = null;
    }

    /**
     * Get the detailed data that will be used to construct the detailed section
     * of the Tasks Coming Due Report.
     *
     * @return a <code>List</code> containing zero or more
     * {@link net.project.schedule.Task} objects.
     */
    public List getDetailedData() {
        return detailedData;
    }

    /**
     * Get the data object required to construct the summary section of the tasks
     * coming due report.
     *
     * @return a <code>TaskComingDueSummaryData</code> object that contains all
     * of the information needed to construct the summary section of the Late
     * Task Report.
     */
    public TasksComingDueSummaryData getSummaryData() {
        return summaryData;
    }

    private List loadFormTasks() throws PersistenceException{
    	List formTasks = new ArrayList();
    	
    	FinderFilterList filters = new FinderFilterList();
    	
    	CheckboxFilter filter = (CheckboxFilter)getFilterList().getAllFilters().get(0);
    	String sql = "";
        if (filter.isSelected()){
           	UserFilter userFilter = (UserFilter)filter.getEnclosedFilter();        	        	
           	int numSelected = 0;
           	for (Iterator its = userFilter.getUserList().iterator(); its.hasNext();) {
           		FilterUser filterUser = (FilterUser)its.next();
           		if (filterUser.isSelected()){
           			if (numSelected > 0) {
           				sql = sql + " OR ";
           			}
           			sql = sql + " ( a.person_Id = "+ filterUser.getID() + " )  ";
           			numSelected = numSelected + 1;
           		}
           	}
           	if (numSelected > 0) {
           		sql = sql + " AND ";
           	}
           }    

        //Get the start of the current day
        PnCalendar cal = new PnCalendar(SessionManager.getUser());
        cal.setTime(new Date());
        String todayString = DateUtils.getDatabaseDateString(cal.startOfDay());
		sql = sql + " a.percent_complete < 1 AND a.end_date >=  " + todayString  ;
		WhereClauseFilter whereClauseFilter = new WhereClauseFilter(sql);
		whereClauseFilter.setSelected(true);
		filters.add(whereClauseFilter);        
                
        filter = (CheckboxFilter)getFilterList().getAllFilters().get(1);
        if (filter.isSelected()){
        	DateFilter dateFilter = (DateFilter)filter.getEnclosedFilter();        	
        	DateFilter endDate = new DateFilter("end_date", AssignmentFinder.END_DATE_COLUMN, false);
        	endDate.setDateRangeStart(dateFilter.getDateRangeStart());
        	endDate.setDateRangeFinish(dateFilter.getDateRangeFinish());
        	endDate.setTruncateDates(true);
        	endDate.setEmptyOptionSelected(false);
            filters.add(endDate);        	
        }    	
    	
        AssignmentManager assignmentManager = new AssignmentManager();
        assignmentManager.setSpaceID(getSpaceID());
        assignmentManager.setAssignmentTypeFilter(AssignmentType.FORM);
        assignmentManager.addFilters(filters);
        assignmentManager.loadAssignments();
        List<FormAssignment> assignments = assignmentManager.getAssignments();
        for (FormAssignment assignment : assignments){
        	Task entry = new Task();;
        	entry.setName(assignment.getObjectName());
        	entry.setWork(assignment.getWork());
        	entry.setWorkComplete(assignment.getWorkComplete());
        	entry.setWorkPercentComplete(assignment.getPercentComplete());
        	entry.setStartTimeD(assignment.getStartTime());
        	entry.setEndTimeD(assignment.getEndTime());
        	entry.setTaskCalculationType(TaskCalculationType.FIXED_WORK);
        	entry.setAssigneesLoaded(true);
        	
        	AssignmentList assignmentList = new AssignmentList();
        	assignmentList.addAssignment(assignment);
        	entry.setAssignmentList(assignmentList);
        	entry.getTaskType();
        	formTasks.add(entry);
        }          
        
    	return formTasks;
    }
    
    private TasksComingDueSummaryData calculateTotalSumary(TasksComingDueSummaryData taskSummary, TasksComingDueSummaryData formSummary){
    	TasksComingDueSummaryData summary = new TasksComingDueSummaryData();
    	
    	summary.setCompletedTaskCount(taskSummary.getCompletedTaskCount() + formSummary.getCompletedTaskCount());
    	summary.setDueNext7Count(taskSummary.getDueNext7Count() + formSummary.getDueNext7Count());
    	summary.setDueNextMonthCount(taskSummary.getDueNextMonthCount() + formSummary.getDueNextMonthCount());
    	summary.setDueTodayCount(taskSummary.getDueTodayCount() + formSummary.getDueTodayCount());
    	summary.setDueTomorrowCount(taskSummary.getDueTomorrowCount() + formSummary.getDueTomorrowCount());
    	summary.setTaskCount(taskSummary.getTaskCount() + formSummary.getTaskCount());    	
    	summary.setUncompletedTasks(taskSummary.getUncompletedTaskCount() + formSummary.getUncompletedTaskCount());	
    	
    	return summary;
    }    
    
      
    /**
     * Load all of the data required to generate a TasksComingDueReport.
     *
     * @throws PersistenceException if there is an error loading this report's
     * data from the database.
     */
    public void load() throws PersistenceException {
        if (!isLoaded) {
            TasksComingDueSummaryFinder finder = new TasksComingDueSummaryFinder();
            finder.addFinderFilterList(filterList);

            FormAssignmentComingDueSummaryFinder formFinder = new FormAssignmentComingDueSummaryFinder();
           // formFinder.addFinderFilterList(filterList);
            
            List taskReports = new ArrayList();
            List formReports = new ArrayList();
            
            //Load the data required to create the summary section
            if(getReportAssignmentType() == ReportAssignmentType.ALL_ASSIGNMENT_REPORT || getReportAssignmentType() == ReportAssignmentType.TASK_ASSIGNMENT_REPORT){
            	taskReports = finder.findBySpaceID(getSpaceID());
            }
            
            if(getReportAssignmentType() == ReportAssignmentType.ALL_ASSIGNMENT_REPORT || getReportAssignmentType() == ReportAssignmentType.FORM_ASSIGNMENT_REPORT){
            	formReports = formFinder.findBySpaceID(getSpaceID());
            }

            if (taskReports.size() > 0 || formReports.size() > 0) {
                //summaryData = (TasksComingDueSummaryData)taskReports.get(0);                
                TasksComingDueSummaryData taskSummary = taskReports.size() > 0 ? (TasksComingDueSummaryData)taskReports.get(0) : new TasksComingDueSummaryData();
                TasksComingDueSummaryData formSummary = formReports.size() > 0 ? (TasksComingDueSummaryData)formReports.get(0) : new TasksComingDueSummaryData();
                summaryData = calculateTotalSumary(taskSummary, formSummary);

                detailedData = new ArrayList();
                List tasks = new ArrayList();
                List formTasks = new ArrayList();                
                if(getReportAssignmentType() == ReportAssignmentType.ALL_ASSIGNMENT_REPORT || getReportAssignmentType() == ReportAssignmentType.TASK_ASSIGNMENT_REPORT){
	                //Do the query to find tasks matching the user's criteria
	                TaskFinder taskFinder = new TaskFinder();
	                taskFinder.addFinderFilterList(filterList);
	                taskFinder.addFinderSorterList(sorterList);                
	                tasks = taskFinder.findComingDueTasks(getSpaceID());
                }
                
                if(getReportAssignmentType() == ReportAssignmentType.ALL_ASSIGNMENT_REPORT || getReportAssignmentType() == ReportAssignmentType.FORM_ASSIGNMENT_REPORT){
                	formTasks = loadFormTasks();
                }
                detailedData.addAll(tasks);	
                detailedData.addAll(formTasks);                
                

                ReportUtils.sortTasks(detailedData, getSorterList().getAllSorters());
                
            } else {
                //No data was found for this report.  This is probably due to the
                //project not yet having a schedule.
                summaryData = new TasksComingDueSummaryData();
                detailedData = Collections.EMPTY_LIST;
            }
        }
    }


}
