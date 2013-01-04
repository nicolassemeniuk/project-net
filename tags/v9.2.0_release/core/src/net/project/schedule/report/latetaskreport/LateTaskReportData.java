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
|   $Revision: 18995 $
|       $Date: 2009-03-05 08:36:26 -0200 (jue, 05 mar 2009) $
|     $Author: avinash $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule.report.latetaskreport;

import java.util.ArrayList;
import java.util.Collections;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.DuplicateFilterIDException;
import net.project.base.finder.EmptyFinderFilter;
import net.project.base.finder.EmptyFinderGrouping;
import net.project.base.finder.FinderFilterList;
import net.project.base.finder.FinderGrouping;
import net.project.base.finder.FinderGroupingList;
import net.project.base.finder.FinderSorter;
import net.project.base.finder.FinderSorterList;
import net.project.base.finder.RadioButtonFilter;
import net.project.base.finder.WhereClauseFilter;
import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.form.assignment.FormAssignment;
import net.project.persistence.PersistenceException;
import net.project.report.ReportAssignmentType;
import net.project.report.SummaryDetailReportData;
import net.project.resource.AssignmentList;
import net.project.resource.AssignmentManager;
import net.project.resource.AssignmentType;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.Task;
import net.project.schedule.TaskFinder;
import net.project.schedule.calc.TaskCalculationType;
import net.project.schedule.report.AssigneeGrouping;
import net.project.schedule.report.FilterUser;
import net.project.schedule.report.ReportUtils;
import net.project.schedule.report.TasksAssignedToMeFilter;
import net.project.schedule.report.UserFilter;
import net.project.security.SessionManager;
import net.project.util.DateUtils;

/**
 * Controller object which coordinates querying the database to find the data
 * needed to produce the report.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
class LateTaskReportData extends SummaryDetailReportData {
    /** Token for the label of the "Default Grouping" grouper. */
    private String DEFAULT_GROUPING = PropertyProvider.get("prm.schedule.report.latetaskreport.grouping.default.name");
    /** Token for the label of the "Group by Phase" grouper. */
    private String GROUP_BY_PHASE = PropertyProvider.get("prm.schedule.report.latetaskreport.grouping.byphase.name");
    /** Token for the label of the "Tasks without Phases" grouper. */
    private String TASKS_WITHOUT_PHASES = PropertyProvider.get("prm.schedule.report.latetaskreport.grouping.byphase.blankphasetitle.name"); //"Tasks Without Phases";

    /**
     * Variable to contain all of the data that will be used to construct the
     * summary section of the Late Task Report.
     */
    private LateTaskReportSummaryData summaryData;
    /**
     * Token pointing to: "Unexpected programmer error found while constructing
     * the list of report filters."
     */
    private String FILTER_LIST_CONSTRUCTION_ERROR = "prm.report.errors.filterlistcreationerror.message";

    /**
     * Standard constructor.
     */
    public LateTaskReportData() {
        populateFinderFilterList();
        populateFinderSorterList();
        populateFinderGroupingList();
    }

    /**
     * Clear out any data stored in this object and reset.
     */
    public void clear() {
        isLoaded = false;
        summaryData = null;
        detailedData = null;
    }

    /**
     * Create the list of <code>FinderGrouping</code> classes that will be used
     * on the HTML page to allow the user to select task grouping.
     */
    private void populateFinderGroupingList() {
        FinderGrouping defaultGrouper = new EmptyFinderGrouping("10", DEFAULT_GROUPING, true);
        FinderGrouping groupByUser = new AssigneeGrouping("20", false);

        FinderGrouping groupByPhase = new FinderGrouping("30", GROUP_BY_PHASE, false) {
            public Object getGroupingValue(Object currentObject) throws PersistenceException {
                return (currentObject == null ? null : ((ScheduleEntry)currentObject).getPhaseName());
            }

            public String getGroupName(Object currentObject) throws PersistenceException {
                ScheduleEntry currentTask = (ScheduleEntry)currentObject;
                String toReturn;

                if ((currentTask == null) ||
                    (currentTask.getPhaseName() == null) ||
                    (currentTask.getPhaseName().trim().equals(""))) {
                    toReturn = TASKS_WITHOUT_PHASES;
                } else {
                    toReturn = currentTask.getPhaseName();
                }

                return toReturn;
            }
        };

        groupingList.add(defaultGrouper);
        groupingList.add(groupByUser);
        groupingList.add(groupByPhase);
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
            RadioButtonFilter rbf = new RadioButtonFilter("10");
            EmptyFinderFilter eff = new EmptyFinderFilter("20", "prm.schedule.report.latetaskreport.showalltasks.name");
            eff.setSelected(true);
            rbf.add(eff);
            rbf.add(new TasksAssignedToMeFilter("30"));
            rbf.add(new UserFilter("50", SessionManager.getUser().getCurrentSpace()));

            //rbf.add(new TasksMyTasksAreDependentUponFilter("40", "prm.schedule.report.latetaskreport.tasksmytasksaredependentupon.name"));
            //rbf.add(new TasksDependentUponMyTasks("50", "prm.schedule.report.latetaskreport.tasksothersarewaitingfor.name"));
            filterList.add(rbf);
        } catch (DuplicateFilterIDException e) {
            throw new RuntimeException(
                PropertyProvider.get(FILTER_LIST_CONSTRUCTION_ERROR), e);
        }
    }

    /**
     * Get the data object required to construct the summary section of the late
     * task report.
     *
     * @return a <code>LateTaskReportSummaryData</code> object that contains all
     * of the information needed to construct the summary section of the Late
     * Task Report.
     */
    public LateTaskReportSummaryData getSummaryData() {
        return summaryData;
    }

    private List loadFormTasks() throws PersistenceException{
    	List formTasks = new ArrayList();
    	
    	FinderFilterList filters = new FinderFilterList();
    	RadioButtonFilter radioFilter = (RadioButtonFilter)filterList.getAllFilters().get(0);
    	String sql = "";
    	TasksAssignedToMeFilter filterCurrentUser = (TasksAssignedToMeFilter)radioFilter.getAllFilters().get(1);
    	if(filterCurrentUser.isSelected()){
    		sql = " a.person_id =  "  + SessionManager.getUser().getID() +  " AND ";
    	}
    	
    	UserFilter filterUsers = (UserFilter)radioFilter.getAllFilters().get(2);
    	if(filterUsers.isSelected()){
           	int numSelected = 0;
           	for (Iterator its = filterUsers.getUserList().iterator(); its.hasNext();) {
           		FilterUser filterUser = (FilterUser)its.next();
           		if (filterUser.isSelected()){
           			if (numSelected > 0) {
           				sql = sql + " OR ";
           			}
           			sql = sql + " ( a.person_Id = "+ filterUser.getID() + " )  ";
           			numSelected = numSelected + 1;
           		}
           	}
           	if (numSelected > 0){
           		sql = sql + " AND ";
           	}
    	}
    	
        //Get the current date
        PnCalendar cal = new PnCalendar(SessionManager.getUser());
        cal.setTime(new Date());
        cal.add(PnCalendar.DATE, -1);
        String yesterdayString = DateUtils.getDatabaseDateString(cal.startOfDay());
        
		sql = sql + " a.percent_complete < 1 AND a.end_date <  " + yesterdayString  ;
		WhereClauseFilter whereClauseFilter = new WhereClauseFilter(sql);
		whereClauseFilter.setSelected(true);
		filters.add(whereClauseFilter);

		
	      AssignmentManager assignmentManager = new AssignmentManager();
          assignmentManager.setSpaceID(getSpaceID());
          assignmentManager.setAssignmentTypeFilter(AssignmentType.FORM);
          assignmentManager.addFilters(filters);
          assignmentManager.loadAssignments();
          List<FormAssignment> assignments = assignmentManager.getAssignments();
          for (FormAssignment assignment : assignments){
          	Task entry = new Task();
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
    
    private LateTaskReportSummaryData caclulateTotalSumary(LateTaskReportSummaryData taskSummary, LateTaskReportSummaryData formSummary){
    	LateTaskReportSummaryData summary = new LateTaskReportSummaryData();
    	
    	summary.setCompletedTaskCount(taskSummary.getCompletedTaskCount() + formSummary.getCompletedTaskCount());
    	summary.setOverdueMilestoneCount(taskSummary.getOverdueMilestoneCount() + formSummary.getOverdueMilestoneCount());
    	summary.setOverdueTaskCount(taskSummary.getOverdueTaskCount() + formSummary.getOverdueTaskCount());
    	summary.setTaskCompletedInLast7DaysCount(taskSummary.getTaskCompletedInLast7DaysCount() + formSummary.getTaskCompletedInLast7DaysCount());
    	summary.setTaskCount(taskSummary.getTaskCount() + formSummary.getTaskCount());
    	summary.setTaskDueInNext7DaysCount(taskSummary.getTaskDueInNext7DaysCount() + formSummary.getTaskDueInNext7DaysCount());
    	
    	summary.setPlanID(taskSummary.getPlanID() != 0 ? taskSummary.getPlanID() : formSummary.getPlanID());	
    	
    	return summary;
    }
    
 
    
    /**
     * Populate this report data object with data from the database.
     *
     * @throws PersistenceException if there is a difficulty loading the data
     * from the database.
     */
    public void load() throws PersistenceException {   	    	
    	
        if (!isLoaded) {
            //Set any filters that the user has set
            LateTaskReportSummaryFinder trf = new LateTaskReportSummaryFinder();
            trf.addFinderFilterList(filterList);

            LateFormAssignmentSummaryFinder arf = new LateFormAssignmentSummaryFinder();
          //  arf.addFinderFilterList(filterList);
            
            //Load the data required to create the summary section
            List taskReports = new ArrayList();
            List formReports = new ArrayList();
            if(getReportAssignmentType() == ReportAssignmentType.ALL_ASSIGNMENT_REPORT || getReportAssignmentType() == ReportAssignmentType.TASK_ASSIGNMENT_REPORT){
            	taskReports = trf.findBySpaceID(getSpaceID());
            }
            if(getReportAssignmentType() == ReportAssignmentType.ALL_ASSIGNMENT_REPORT || getReportAssignmentType() == ReportAssignmentType.FORM_ASSIGNMENT_REPORT){
                formReports = arf.findBySpaceID(getSpaceID());
            }

            if (taskReports.size() > 0 || formReports .size() > 0) {
                //summaryData = (LateTaskReportSummaryData)taskReports.get(0);
            	LateTaskReportSummaryData taskSummary = taskReports.size() > 0 ? (LateTaskReportSummaryData)taskReports.get(0) : new LateTaskReportSummaryData();
            	LateTaskReportSummaryData formSummary = formReports.size() > 0 ? (LateTaskReportSummaryData)formReports.get(0) : new LateTaskReportSummaryData();
            	summaryData = caclulateTotalSumary(taskSummary, formSummary);	
            	
                detailedData = new ArrayList();
                List tasks = new ArrayList();
                List formTasks = new ArrayList();
                
                if(getReportAssignmentType() == ReportAssignmentType.ALL_ASSIGNMENT_REPORT || getReportAssignmentType() == ReportAssignmentType.TASK_ASSIGNMENT_REPORT){
                	//Do the query to find tasks matching the user's criteria
                	TaskFinder taskFinder = new TaskFinder();
                	taskFinder.addFinderFilterList(filterList);
                	taskFinder.addFinderSorterList(sorterList);
                	tasks = taskFinder.findLateTasks(getSpaceID());
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
                summaryData = new LateTaskReportSummaryData();
                detailedData = Collections.EMPTY_LIST;
            }

            isLoaded = true;
        }
    }
}
