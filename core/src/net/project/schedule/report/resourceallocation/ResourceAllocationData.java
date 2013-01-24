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

package net.project.schedule.report.resourceallocation;

import java.util.List;

import net.project.base.finder.CheckboxFilter;
import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.DateFilter;
import net.project.base.finder.DuplicateFilterIDException;
import net.project.base.finder.FinderGrouping;
import net.project.base.finder.FinderSorter;
import net.project.persistence.PersistenceException;
import net.project.report.SummaryDetailReportData;
import net.project.schedule.Schedule;
import net.project.schedule.report.ResourceFilter;
import net.project.security.SessionManager;

import org.apache.log4j.Logger;

/**
 * Class to hold the data required to create the Resource Allocation Report.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
class ResourceAllocationData extends SummaryDetailReportData {
    /**
     * Variable which stores the data required to create the summary section of
     * the resource allocation report.
     */
    private ResourceAllocationSummaryData summaryData;

    /**
     * A code schedule which we will use as the basis for the working times
     * default for the individual resources provided that they don't already
     * have working times specified.
     */
    private Schedule schedule;

    /**
     * Standard constructor.
     */
    public ResourceAllocationData() {
        populateFinderList();
        populateSorterList();
        populateGroupingList();
    }

    private void populateFinderList() {
        try {
            DateFilter startDateFilter = new DateFilter("20",
                ResourceAllocationFinder.DATE_START_COLUMN,
                "prm.schedule.report.resourceallocation.filters.startdate.name",
                false);
            startDateFilter.setTruncateDates(true);
            DateFilter finishDateFilter = new DateFilter("40",
                ResourceAllocationFinder.DATE_FINISH_COLUMN,
                "prm.schedule.report.resourceallocation.filters.enddate.name",
                false);
            finishDateFilter.setTruncateDates(true);

            filterList.add(new CheckboxFilter("10", startDateFilter));
            filterList.add(new CheckboxFilter("30", finishDateFilter));
            filterList.add(new CheckboxFilter("50", new ResourceFilter("60",
                SessionManager.getUser().getCurrentSpace())));
        } catch (DuplicateFilterIDException e) {
        	Logger.getLogger(ResourceAllocationData.class).debug("Unable to add date filter to the " +
                "resource allocation report");
        }
    }

    /**
     * Add all of the FinderGroupers to the internal finder grouping list that
     * are going to be exposed in the interface to run this report.
     */
    private void populateGroupingList() {
        groupingList.add(new DayGrouping("10", true));
        groupingList.add(new WeekGrouping("20", false));
        groupingList.add(new MonthGrouping("30", false));
    }

    /**
     * Populate the list of sorters with all sorters that this report supports.
     */
    private void populateSorterList() {
        //sorterList.add(new FinderSorter(ResourceAllocationFinder.DATE_START_COLUMN, false));
        for (int i = 1; i < 4; i++) {
            FinderSorter fs = new FinderSorter(String.valueOf(i * 10),
                new ColumnDefinition[]{
                    ResourceAllocationFinder.RESOURCE_NAME,
                    ResourceAllocationFinder.NAME_COLUMN,
                    ResourceAllocationFinder.DURATION_COLUMN,
                    ResourceAllocationFinder.DATE_START_COLUMN,
                    ResourceAllocationFinder.DATE_FINISH_COLUMN,
                    ResourceAllocationFinder.PERCENT_ALLOCATED_COLUMN
                },
                ResourceAllocationFinder.RESOURCE_NAME);
            sorterList.add(fs);
        }
    }

    /**
     * Populate this report data object with data from the database.
     *
     * @throws PersistenceException if there is a difficulty loading the data
     * from the database.
     */
    public void load() throws PersistenceException {
        ResourceAllocationFinder raf = new ResourceAllocationFinder();
        raf.addFinderFilterList(getFilterList());
        raf.addFinderSorterList(getSorterList());
        List grouping = this.getGroupingList().getSelectedGroupings();
        FinderGrouping selectedGroup = null;
        if(null!=grouping && grouping.size()>0){
        	selectedGroup = (FinderGrouping) grouping.get(0);
        }
        detailedData = raf.findBySpaceID(getSpaceID(), selectedGroup, schedule);

        summaryData = new ResourceAllocationSummaryData();
        summaryData.populate(detailedData);
    }

    /**
     * Clear out any data stored in this object and reset.
     */
    public void clear() {
        summaryData = null;
        detailedData = null;
    }

    /**
     * Get the data required to create the summary section of the report.
     *
     * @return a <code>ResourceAllocationSummaryData</code> object which contains
     * the data requried to create the summary section of the report.
     */
    public ResourceAllocationSummaryData getSummaryData() {
        return summaryData;
    }

    public void setSchedule(Schedule currentSchedule) {
        this.schedule = currentSchedule;
    }
}
