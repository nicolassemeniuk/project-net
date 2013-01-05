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
package net.project.resource.report.newuserreport;

import net.project.base.finder.CheckboxFilter;
import net.project.base.finder.DateFilter;
import net.project.base.finder.DuplicateFilterIDException;
import net.project.persistence.PersistenceException;
import net.project.report.SummaryDetailReportData;
import net.project.security.SessionManager;

/**
 * Class to manage the query and distribution of data for the New User Report.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class NewUserReportData extends SummaryDetailReportData {
    /** Data required to create the summary section of the report. */
    private NewUserReportSummaryData summaryData;

    /**
     * Creates a <code>NewUserReportData</code> object.
     */
    public NewUserReportData() {
        populateFinderFilterList();
    }

    /**
     * Add any filters that should appear on the parameters page.
     */
    private void populateFinderFilterList() {
        try {
            filterList.add(new CheckboxFilter("10",
                new DateFilter("20", NewUserReportDataFinder.DATE_INVITED_COLUMN, false)));
            filterList.add(new CheckboxFilter("30",
                new DateFilter("40", NewUserReportDataFinder.DATE_RESPONDED_COLUMN, false)));
        } catch (DuplicateFilterIDException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populate this report data object with data from the database.
     *
     * @throws PersistenceException if there is a difficulty loading the data
     * from the database.
     */
    public void load() throws PersistenceException {
        NewUserReportDataFinder finder = new NewUserReportDataFinder();
        finder.addFinderFilter(new SpaceIDFilter(NewUserReportDataFinder.SPACE_ID_COLUMN,
            SessionManager.getUser().getCurrentSpace().getID()));
        finder.addFinderFilterList(this.getFilterList());
        finder.addFinderSorterList(this.getSorterList());

        this.detailedData = finder.findAllMatches();
        this.summaryData = new NewUserReportSummaryData(this.detailedData);
    }

    /**
     * Clear out any data stored in this object and reset.
     */
    public void clear() {
        detailedData.clear();
    }

    /**
     * Get the summary data required to create the summary section of the new
     * user report.
     *
     * @return a <code>NewUserReportSummaryData</code> object which contains the
     * data necessary to create the summary section of the new user report.
     */
    public NewUserReportSummaryData getSummaryData() {
        return summaryData;
    }
}
