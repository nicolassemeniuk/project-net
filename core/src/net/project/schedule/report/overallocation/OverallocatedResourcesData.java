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

package net.project.schedule.report.overallocation;

import net.project.base.finder.CheckboxFilter;
import net.project.base.finder.DateFilter;
import net.project.base.finder.DuplicateFilterIDException;
import net.project.base.finder.WhereClauseFilter;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.report.ReportAssignmentType;
import net.project.report.SummaryDetailReportData;
import net.project.schedule.report.ResourceFilter;
import net.project.security.SessionManager;

/**
 * Class that provides all of the data required to create the overallocated
 * resources report.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
class OverallocatedResourcesData extends SummaryDetailReportData {
    /** Data required to create the summary section of the report. */
    private OverallocatedResourcesSummaryData summaryData = null;
    /**
     * Token pointing to: "Unexpected programmer error found while constructing
     * the list of report filters."
     */
    private String FILTER_LIST_CONSTRUCTION_ERROR = "prm.report.errors.filterlistcreationerror.message";

    /**
     * Standard constructor.  This constructor populates the filters, sorters,
     * and grouping as well.
     */
    public OverallocatedResourcesData() {
        super();
        populateFilterList();
    }

    /**
     * Populate the list of filters for this object.
     */
    private void populateFilterList() {
        try {
            filterList.add(new CheckboxFilter("10", new ResourceFilter("20",
                SessionManager.getUser().getCurrentSpace())));
            filterList.add(new CheckboxFilter("30", new DateFilter("40",
                OverallocatedResourceFinder.REPORTING_PERIOD,
                "prm.schedule.report.resourceallocation.filters.reportingperiodfilter.name",
                false)));
        } catch (DuplicateFilterIDException e) {
            throw new RuntimeException(PropertyProvider.get(FILTER_LIST_CONSTRUCTION_ERROR), e);
        }
    }

    /**
     * Populate this report data object with data from the database.
     *
     * @throws PersistenceException if there is a difficulty loading the data
     * from the database.
     */
    public void load() throws PersistenceException {    	    
        OverallocatedResourceFinder orf = new OverallocatedResourceFinder();
        orf.addFinderFilterList(filterList);
        orf.addFinderSorterList(sorterList);
        detailedData = orf.findBySpaceID(SessionManager.getUser().getCurrentSpace().getID(), getReportAssignmentType());

        //Get the summary data by examining the detailed data
        summaryData = new OverallocatedResourcesSummaryData(detailedData);
    }


    /**
     * Clear out any data stored in this object and reset.
     */
    public void clear() {
        detailedData = null;
        summaryData = null;
    }

    /**
     * Get the data required to construct the summary section of the
     * overallocated resources report.
     *
     * @return a {@link net.project.schedule.report.overallocation.OverallocatedResourcesSummaryData}
     * object which contains all of the required information to create the
     * summary section of the overallocated resources report.
     */
    public OverallocatedResourcesSummaryData getSummaryData() {
        return summaryData;
    }
}
