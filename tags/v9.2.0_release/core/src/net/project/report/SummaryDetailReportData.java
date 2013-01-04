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
package net.project.report;

import java.util.ArrayList;
import java.util.List;

import net.project.base.finder.GroupingIterator;
import net.project.persistence.PersistenceException;

/**
 * ReportData subclass which adds some additional methods that are commonly used
 * in summary/detail style reports.  These are methods that probably don't apply
 * to every report, but are commonly used in certain types of reports.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public abstract class SummaryDetailReportData extends ReportData {
    /**
     * Variable to contain all of the tasks that will construct the detailed
     * section of the Tasks Coming Due Report.
     */
    protected List detailedData = new ArrayList();

    /**
     * Get an iterator that understands how to format the detailed result
     * information into groups.
     *
     * @return a {@link net.project.base.finder.GroupingIterator} object
     * preinitialized with the detailed report information and with the selected
     * {@link net.project.base.finder.FinderGrouping} objects.
     * @throws PersistenceException if there is a database exception while
     * trying to construct the grouping iterator.
     */
    public GroupingIterator getGroupingIterator() throws PersistenceException {
        GroupingIterator iterator = new GroupingIterator(detailedData);
        iterator.addFinderGroupingList(groupingList);
        iterator.reset();

        return iterator;
    }

    /**
     * Get the detailed data that will be used to construct the detailed section
     * of the Late Task Report.
     *
     * @return a <code>List</code> containing zero or more
     * {@link net.project.schedule.Task} objects.
     */
    public List getDetailedData() {
        return detailedData;
    }

}
