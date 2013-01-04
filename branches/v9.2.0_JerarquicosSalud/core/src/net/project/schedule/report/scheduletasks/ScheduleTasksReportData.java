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
package net.project.schedule.report.scheduletasks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.base.finder.CheckboxFilter;
import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.DateFilter;
import net.project.base.finder.DuplicateFilterIDException;
import net.project.base.finder.EmptyFinderGrouping;
import net.project.base.finder.FinderFilterList;
import net.project.base.finder.FinderGrouping;
import net.project.base.finder.FinderGroupingList;
import net.project.base.finder.FinderSorter;
import net.project.base.finder.FinderSorterList;
import net.project.base.finder.NumberComparator;
import net.project.base.finder.NumberFilter;
import net.project.base.finder.WhereClauseFilter;
import net.project.base.property.PropertyProvider;
import net.project.form.assignment.FormAssignment;
import net.project.persistence.PersistenceException;
import net.project.report.ReportAssignmentType;
import net.project.report.SummaryDetailReportData;
import net.project.resource.AssignmentFinder;
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
import net.project.schedule.report.UserFilter;
import net.project.security.SessionManager;

/**
 * Class which holds (and queries for) the data required to create the scheduled
 * tasks report.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
class ScheduleTasksReportData extends SummaryDetailReportData {
    /** Label for the "Default" (that is no) grouping. */
    private String DEFAULT_GROUPING = PropertyProvider.get("prm.schedule.report.scheduletasks.grouping.default.name");
    /** Label for the "Group by Phase" grouping. */
    private String GROUP_BY_PHASE = PropertyProvider.get("prm.schedule.report.scheduletasks.grouping.byphase.name");
    /** Label for the "Tasks Without Phases" grouping. */
    private String TASKS_WITHOUT_PHASES = PropertyProvider.get("prm.schedule.report.scheduletasks.grouping.byphase.taskswithoutphases.name");
    /**
     * Data necessary to create the summary section of the scheduled tasks
     * report.
     */
    private ScheduleTasksSummaryData summaryData;

    /**
     * Public constructor.
     */
    public ScheduleTasksReportData() {
        populateFinderFilterList();
        populateFinderGroupingList();
        populateFinderSorterList();
    }

    /**
     * Add all of the filters we will need to produce the filters on the
     * parameters. page.
     */
    private void populateFinderFilterList() {
        try {
            filterList.add(new CheckboxFilter("10", new UserFilter("20", SessionManager.getUser().getCurrentSpace())));
            filterList.add(new CheckboxFilter("30", new NumberFilter("40", TaskFinder.WORK_PERCENT_COMPLETE_COLUMN, false)));
            filterList.add(new CheckboxFilter("50", new DateFilter("60", TaskFinder.DATE_START_COLUMN, false, true)));
            filterList.add(new CheckboxFilter("70", new DateFilter("80", TaskFinder.DATE_FINISH_COLUMN, false, true)));
        } catch (DuplicateFilterIDException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            throw new RuntimeException(e);
        }
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

    
    
    
    private List loadFormTasks() throws PersistenceException{
    
    	   FinderFilterList filters = new FinderFilterList();
           
           CheckboxFilter filter = (CheckboxFilter)getFilterList().getAllFilters().get(0);
           if (filter.isSelected()){
           	UserFilter userFilter = (UserFilter)filter.getEnclosedFilter();        	        	
           	String sql = " ";
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
           	WhereClauseFilter whereClauseFilter = new WhereClauseFilter(sql);
           	whereClauseFilter.setSelected(numSelected > 0);        	
           	filters.add(whereClauseFilter);
           }
           
            filter = (CheckboxFilter)getFilterList().getAllFilters().get(1);
           if (filter.isSelected()){
           	NumberFilter numFilter = (NumberFilter)filter.getEnclosedFilter();        	
               NumberFilter percentComplete = new NumberFilter("percent_complete", AssignmentFinder.PERCENT_COMPLETE_COLUMN, false);
               percentComplete.setSelected(true);
               percentComplete.setComparator((NumberComparator)numFilter.getComparator());
               percentComplete.setNumber(numFilter.getNumber().doubleValue() / 100);
               filters.add(percentComplete);        	
           }
           filter = (CheckboxFilter)getFilterList().getAllFilters().get(2);
           if (filter.isSelected()){
           	DateFilter dateFilter = (DateFilter)filter.getEnclosedFilter();        	
           	DateFilter startDate = new DateFilter("start_date", AssignmentFinder.START_DATE_COLUMN, false);
           	startDate.setDateRangeStart(dateFilter.getDateRangeStart());
           	startDate.setDateRangeFinish(dateFilter.getDateRangeFinish());
           	startDate.setTruncateDates(true);
           	startDate.setEmptyOptionSelected(false);
               filters.add(startDate);        	
           }        	
           
           filter = (CheckboxFilter)getFilterList().getAllFilters().get(3);
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
           List formTasks = new ArrayList();
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
    
     
    /**
     * Populate this report data object with data from the database.
     *
     * @throws PersistenceException if there is a difficulty loading the data
     * from the database.
     */
    public void load() throws PersistenceException {
    	
    	detailedData = new ArrayList();
    	
        List tasks = new ArrayList();
        List formTasks = new ArrayList();
        
        if(getReportAssignmentType() == ReportAssignmentType.ALL_ASSIGNMENT_REPORT || getReportAssignmentType() == ReportAssignmentType.TASK_ASSIGNMENT_REPORT){
            TaskFinder tf = new TaskFinder();
            tf.addFinderFilterList(getFilterList());
            tf.addFinderSorterList(getSorterList());
            tasks = tf.findBySpaceID(getSpaceID());              
        }
        if(getReportAssignmentType() == ReportAssignmentType.ALL_ASSIGNMENT_REPORT || getReportAssignmentType() == ReportAssignmentType.FORM_ASSIGNMENT_REPORT){
        	formTasks = loadFormTasks();
        }
        
        detailedData.addAll(tasks);	
        detailedData.addAll(formTasks);
        
        ReportUtils.sortTasks(detailedData, getSorterList().getAllSorters());
        
        summaryData = new ScheduleTasksSummaryData(detailedData);
    }

    /**
     * Clear out any data stored in this object and reset.
     */
    public void clear() {
        detailedData = null;
    }

    /**
     * Return the data required to create the summary section of the scheduled
     * tasks report.
     *
     * @return a <code>ScheduleTasksSummaryData</code> which contains the
     * necessary data to create the summary section.
     */
    public ScheduleTasksSummaryData getSummaryData() {
        return summaryData;
    }
}
