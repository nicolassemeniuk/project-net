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

 package net.project.resource.report.workcompleted;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.project.base.PnetRuntimeException;
import net.project.base.finder.CheckboxFilter;
import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.DateFilter;
import net.project.base.finder.EmptyFinderGrouping;
import net.project.base.finder.FinderSorter;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.report.ReportAssignmentType;
import net.project.report.ReportCompositeComparator;
import net.project.report.ReportScope;
import net.project.report.SummaryDetailReportData;
import net.project.resource.AssignmentWorkLogEntry;
import net.project.resource.AssignmentWorkLogFinder;
import net.project.resource.filters.assignments.SpaceFilter;
import net.project.schedule.report.ResourceAssignmentFilter;
import net.project.schedule.report.UserFilter;
import net.project.security.SessionManager;
import net.project.space.ISpaceTypes;
import net.project.util.CollectionUtils;

/**
 * The class loads the data necessary to create the WorkCompleted report.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class WorkCompletedData extends SummaryDetailReportData {
    /** Label for the default grouping. */
    private String DEFAULT_GROUPING = PropertyProvider.get("prm.resource.report.workcompleted.nogrouping.name");
    private String WORKING_USER_GROUPING = PropertyProvider.get("prm.resource.report.workcompleted.groupbyuser.name");
    private String DATE_LOGGED_GROUPING = PropertyProvider.get("prm.resource.report.workcompleted.groupbydatelogged.name");
    private String ASSIGNMENT_GROUPING = PropertyProvider.get("prm.resource.report.workcompleted.groupbyassignment.name");

    private String DATE_LOGGED_FILTER = PropertyProvider.get("prm.resource.report.workcompleted.dateloggedfilter.name");
    private String WORKSPACE_NAME_FILTER = PropertyProvider.get("prm.resource.report.workcompleted.workspacesfilter.name");

    public WorkCompletedData(ReportScope scope) {
        setScope(scope);
        populateFinderFilterList();
        populateFinderSorterList();
        populateFinderGroupingList();
    }

    private void populateFinderFilterList() {
        //Filter for a list of user
        boolean loadAllUsers = getScope() != null && getScope().equals(ReportScope.GLOBAL);
        UserFilter assignmentFilter = new ResourceAssignmentFilter("10", SessionManager.getUser().getCurrentSpace(), !loadAllUsers);
        CheckboxFilter userCheckbox = new CheckboxFilter("20", assignmentFilter);
        filterList.add(userCheckbox);

        //Filter for date logged
        DateFilter dateLoggedFilter = new DateFilter("30", AssignmentWorkLogFinder.LOG_DATE_COL, DATE_LOGGED_FILTER, false);
        CheckboxFilter dateLoggedCheckbox = new CheckboxFilter("40", dateLoggedFilter);
        filterList.add(dateLoggedCheckbox);

        if (getScope() != null && getScope().equals(ReportScope.GLOBAL)) {
            SpaceFilter spaceFilter = new SpaceFilter("50", WORKSPACE_NAME_FILTER, AssignmentWorkLogFinder.SPACE_ID_COL);
            CheckboxFilter spaceFilterCheckbox = new CheckboxFilter("60", spaceFilter);
            try {
                spaceFilter.loadSpaces(new String[] { ISpaceTypes.PROJECT_SPACE});
            } catch (PersistenceException e) {
                throw new PnetRuntimeException(e);
            }
            filterList.add(spaceFilterCheckbox);
        }
    }

    private void populateFinderSorterList() {
        for (int i = 0; i < 3; i++) {
            sorterList.add(
                new FinderSorter(
                    String.valueOf((i+1)*10),
                    new ColumnDefinition[] {AssignmentWorkLogFinder.LOG_DATE_COL,
                                            AssignmentWorkLogFinder.ASSIGNMENT_NAME_COL,
                                            AssignmentWorkLogFinder.PERCENT_COMPLETE_COL,
                                            AssignmentWorkLogFinder.WORK_START_COL,
                                            AssignmentWorkLogFinder.WORK_END_COL},
                    AssignmentWorkLogFinder.LOG_DATE_COL
                )
            );
        }
    }

    private void populateFinderGroupingList() {
        groupingList.add(new EmptyFinderGrouping("10", DEFAULT_GROUPING, true));
        groupingList.add(new WorkingUserGrouping("20", WORKING_USER_GROUPING, false));
        groupingList.add(new WorkLoggedDateGrouping("30", DATE_LOGGED_GROUPING, false));
        groupingList.add(new AssignmentGrouping("40", ASSIGNMENT_GROUPING, false));
    }

    /**
     * Populate this report data object with data from the database.
     *
     * @throws net.project.persistence.PersistenceException if there is a
     * difficulty loading the data from the database.
     * @throws SQLException 
     */
    public void load() throws PersistenceException, SQLException {
        AssignmentWorkLogFinder finder = new AssignmentWorkLogFinder();
        finder.addFinderFilterList(getFilterList());
        finder.addFinderSorterList(getSorterList());
        
        if (getScope().equals(ReportScope.SPACE)) {
            SpaceFilter spaceFilter = new SpaceFilter("sp10", "none", AssignmentWorkLogFinder.SPACE_ID_COL);
            spaceFilter.setSelectedSpaces(CollectionUtils.createCollection(SessionManager.getUser().getCurrentSpace().getID()));
            spaceFilter.setSelected(true);

            finder.addFinderFilter(spaceFilter);
        }

        List taskAssignments = new ArrayList();
        List formAssignments = new ArrayList();
        
        if(getReportAssignmentType() == ReportAssignmentType.ALL_ASSIGNMENT_REPORT || getReportAssignmentType() == ReportAssignmentType.TASK_ASSIGNMENT_REPORT){
        	taskAssignments = isHundredPercentComplete()?  finder.loadTaskWorkComp100PercentFromDB() : finder.loadTaskWorkCompFromDB();
        }
        if(getReportAssignmentType() == ReportAssignmentType.ALL_ASSIGNMENT_REPORT || getReportAssignmentType() == ReportAssignmentType.FORM_ASSIGNMENT_REPORT){
        	formAssignments = finder.loadFormsWorkCompFromDB();
        }
                
        detailedData = new ArrayList();
        detailedData.addAll(taskAssignments);
        detailedData.addAll(formAssignments);
        
        List<Comparator> comparatorList = new ArrayList<Comparator>();        
        List<FinderSorter> sorterList = getSorterList().getAllSorters();        
        for (FinderSorter sorter : sorterList){         
          if (sorter.isSelected()){
        	  final int direction = sorter.isDescending() ? -1 : 1;
        	  
        	  Comparator comparator = null;
        	  if (sorter.getSelectedColumn() == AssignmentWorkLogFinder.LOG_DATE_COL) {
        		  comparator = new Comparator<AssignmentWorkLogEntry>(){
        			  public int compare(AssignmentWorkLogEntry o1, AssignmentWorkLogEntry o2){        				  
        				  return direction * o1.getLogDate().compareTo(o2.getLogDate());        				  
        			  }
        		  };
        	  } else if (sorter.getSelectedColumn() == AssignmentWorkLogFinder.ASSIGNMENT_NAME_COL){
        		  comparator = new Comparator<AssignmentWorkLogEntry>(){
        			  public int compare(AssignmentWorkLogEntry o1, AssignmentWorkLogEntry o2){
        				  return direction * o1.getObjectName().toLowerCase().compareTo(o2.getObjectName().toLowerCase());
        			  }
        		  };        		  
        	  } else if (sorter.getSelectedColumn() == AssignmentWorkLogFinder.PERCENT_COMPLETE_COL){
        		  comparator = new Comparator<AssignmentWorkLogEntry>(){
        			  public int compare(AssignmentWorkLogEntry o1, AssignmentWorkLogEntry o2){
        				  return direction * o1.getPercentComplete().compareTo(o2.getPercentComplete());
        			  }
        		  };        		  
        	  } else if (sorter.getSelectedColumn() == AssignmentWorkLogFinder.WORK_START_COL) {
        		  comparator = new Comparator<AssignmentWorkLogEntry>(){
        			  public int compare(AssignmentWorkLogEntry o1, AssignmentWorkLogEntry o2){
        				  return direction * o1.getDatesWorked().getRangeStart().compareTo(o2.getDatesWorked().getRangeStart());
        			  }
        		  };        		  
        	  } else if (sorter.getSelectedColumn() == AssignmentWorkLogFinder.WORK_END_COL) {
        		  comparator = new Comparator<AssignmentWorkLogEntry>(){
        			  public int compare(AssignmentWorkLogEntry o1, AssignmentWorkLogEntry o2){
        				  return direction * o1.getDatesWorked().getRangeEnd().compareTo(o2.getDatesWorked().getRangeEnd());
        			  }
        		  };        		  
        	  }
        	  comparatorList.add(comparator);
          }
        }
	     if (comparatorList.size() > 0){        
	    	 Comparator composite = new ReportCompositeComparator(comparatorList);
	        Collections.sort(detailedData, composite);
	     }
    }

    /**
     * Clear out any data stored in this object and reset.
     */
    public void clear() {
        detailedData = Collections.EMPTY_LIST;
    }
}
