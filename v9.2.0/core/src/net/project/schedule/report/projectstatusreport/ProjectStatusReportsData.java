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
|   $Revision: 15404 $
|       $Date: 2006-08-28 20:20:09 +0530 (Mon, 28 Aug 2006) $
|     $Author: deepak $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule.report.projectstatusreport;

import java.util.Collections;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.DuplicateFilterIDException;
import net.project.base.finder.EmptyFinderFilter;
import net.project.base.finder.EmptyFinderGrouping;
import net.project.base.finder.FinderGrouping;
import net.project.base.finder.FinderSorter;
import net.project.base.finder.RadioButtonFilter;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.report.SummaryDetailReportData;
import net.project.schedule.TaskFinder;

/**
 * Controller object which coordinates querying the database to find the data
 * needed to produce the report.
 *
 * @author K B Deepak
 * @since Version 1.0
 */
class ProjectStatusReportsData extends SummaryDetailReportData {
    /** Token for the label of the "Default Grouping" grouper. */
    private String DEFAULT_GROUPING = PropertyProvider.get("prm.schedule.report.projectstatusreport.grouping.default.name");

    /**
     * Variable to contain all of the data that will be used to construct the
     * summary section of the Project Status Report.
     */
    private ProjectStatusReportsSummaryData summaryData;
    /**
     * Token pointing to: "Unexpected programmer error found while constructing
     * the list of report filters."
     */
    private String FILTER_LIST_CONSTRUCTION_ERROR = "prm.report.errors.filterlistcreationerror.message";

    /**
     * Standard constructor.
     */
    public ProjectStatusReportsData() {
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
        groupingList.add(defaultGrouper);
    }

    /**
     * Populate the list of sorters with all sorters that this report supports.
     */
    private void populateFinderSorterList() {
        for (int i = 1; i < 4; i++) {
            FinderSorter fs = new FinderSorter(String.valueOf(i * 10),
                new ColumnDefinition[]{TaskFinder.MILESTONE_NAME_COLUMN, TaskFinder.DATE_START_COLUMN,
            						   TaskFinder.DATE_FINISH_COLUMN, TaskFinder.WORK_PERCENT_COMPLETE_COLUMN},
            						   TaskFinder.MILESTONE_NAME_COLUMN);
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
            EmptyFinderFilter eff = new EmptyFinderFilter("20", "prm.schedule.report.projectstatusreport.showalltasks.name");
            eff.setSelected(true);
            rbf.add(eff);
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
     * @return a <code>ProjectStatusReportsSummaryData</code> object that contains all
     * of the information needed to construct the summary section of the Project Status
     * Report.
     */
    public ProjectStatusReportsSummaryData getSummaryData() {
        return summaryData;
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
            ProjectStatusReportsSummaryFinder trf = new ProjectStatusReportsSummaryFinder();
            trf.addFinderFilterList(filterList);

            //Load the data required to create the summary section
            java.util.List taskReports = trf.findByProjectID(getSpaceID());

            if (taskReports.size() > 0) {
                summaryData = (ProjectStatusReportsSummaryData)taskReports.get(0);

                //Do the query to find tasks matching the user's criteria
                TaskFinder taskFinder = new TaskFinder();
                taskFinder.addFinderFilterList(filterList);
                taskFinder.addFinderSorterList(sorterList);
                detailedData = taskFinder.findMilestoneTasks(getSpaceID());
            } else {
                //No data was found for this report.  This is probably due to the
                //project not yet having a schedule.
                summaryData = new ProjectStatusReportsSummaryData();
                detailedData = Collections.EMPTY_LIST;
            }

            isLoaded = true;
        }
    }
}
