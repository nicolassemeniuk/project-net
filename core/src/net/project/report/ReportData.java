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
|   $Revision: 20658 $
|       $Date: 2010-04-01 01:53:03 -0300 (jue, 01 abr 2010) $
|     $Author: dpatil $
|
+-----------------------------------------------------------------------------*/
package net.project.report;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import net.project.base.finder.FinderFilterList;
import net.project.base.finder.FinderGroupingList;
import net.project.base.finder.FinderIngredientHTMLConsumer;
import net.project.base.finder.FinderSorterList;
import net.project.base.finder.GroupingIterator;
import net.project.persistence.PersistenceException;
import net.project.util.VisitException;

/**
 * Convenience class to hold reporting data.  There is no direct requirement
 * that this class be implemented, although I have found it to be one that I
 * commonly use.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public abstract class ReportData {
    /** A list of all the available filters that can be applied to a report. */
    protected FinderFilterList filterList = new FinderFilterList();
    /** A list of all the available sorters that can be applied to a report. */
    protected FinderSorterList sorterList = new FinderSorterList();
    /** A list of all the available groupings that can be applied to a report. */
    protected FinderGroupingList groupingList = new FinderGroupingList();
    /** Variable which contains the ID of the space we are reporting on. */
    private String spaceID = null;
    /** Has the data from the database already been loaded into the objects. */
    protected boolean isLoaded = false;
    /** The scope of the data that should be loaded. */
    private ReportScope scope;
    /**Type of assignment to be shown on report: currently task or form assignement  */
    private ReportAssignmentType reportAssignmentType = ReportAssignmentType.TASK_ASSIGNMENT_REPORT;
    
    private boolean hundredPercentComplete = false;

    /**
     * Get a list of all possible filters that can be applied to a report.  The
     * actual report query is generally constructed by calling
     * {@link net.project.base.finder.FinderFilterList#getSelectedFilters} to
     * grab the filters that the user has selected.
     *
     * @return a {@link net.project.base.finder.FinderFilterList} object that
     * contains all of the possible filters that can be applied to a report.
     */
    public FinderFilterList getFilterList() {
        return filterList;
    }

    /**
     * Get a list of all possible sorters that can be applied to a report.  The
     * actual report query is generally constructed by calling
     * {@link net.project.base.finder.FinderSorterList#getSelectedSorters} to
     * grab the filters that the user has selected.
     *
     * @return a {@link net.project.base.finder.FinderSorterList} object that
     * contains all of the possible sorters that can be applied to a report.
     */
    public FinderSorterList getSorterList() {
        return sorterList;
    }

    /**
     * Get a list of all possible <code>FinderGroupings</code> that can be
     * applied to a report.  The actual report query is generally constructed by
     * calling {@link net.project.base.finder.FinderGroupingList#getSelectedGroupings}
     * to grab the groupings that the user has selected.
     *
     * @return a {@link net.project.base.finder.FinderGroupingList} object that
     * contains all of the possible groupings that can be applied to a report.
     */
    public FinderGroupingList getGroupingList() {
        return groupingList;
    }

    /**
     * Get the ID of the space that we are reporting on.
     *
     * @return a <code>String</code> containing the space ID that we are reporting
     * on.
     */
    public String getSpaceID() {
        return spaceID;
    }

    /**
     * Set the ID of the space we are reporting on.
     *
     * @param spaceID a <code>String</code> value containing the ID of the space
     * we are reporting on.
     */
    public void setSpaceID(String spaceID) {
        //Check to see if we have already loaded this data
        isLoaded = ((isLoaded) && (this.spaceID != null) && (this.spaceID.equals(spaceID)));
        this.spaceID = spaceID;
    }

    public ReportScope getScope() {
        return scope;
    }

    public void setScope(ReportScope scope) {
        this.scope = scope;
    }
    
    
    /**
	 * @return Returns the reportAssignmentType.
	 */
	public ReportAssignmentType getReportAssignmentType() {
		return reportAssignmentType;
	}

	/**
	 * @param reportAssignmentType The reportAssignmentType to set.
	 */
	public void setReportAssignmentType(ReportAssignmentType reportAssignmentType) {
		this.reportAssignmentType = reportAssignmentType;
	}

	/**
     * Populate this report data object with data from the database.
     *
     * @throws PersistenceException if there is a difficulty loading the data
     * from the database.
     * @throws SQLException 
     */
    public abstract void load() throws PersistenceException, SQLException;

    /**
     * Update the internal <code>FinderFilterList</code>, <code>FinderSorterList</code>,
     * and <code>FinderGroupingList</code> objects based on the results of a
     * html form post.
     *
     * @param request a <code>HttpServletRequest</code> object that will be used
     * to find the data needed to populate the <code>FinderFilterList</code>,
     * <code>FinderSorterList</code>, and <code>FinderGroupingList</code>
     * objects.
     */
    public void updateParametersFromRequest(HttpServletRequest request) {

        try {
            FinderIngredientHTMLConsumer consumer = new FinderIngredientHTMLConsumer(request);
            filterList.accept(consumer);
            sorterList.accept(consumer);
            groupingList.accept(consumer);
        } catch (VisitException e) {
            // Currently do nothing
            // In this case, a VisitException will almost never be thrown
            // But we really need to change call hierarchy to propogate to surface
        }

        setSpaceID(request.getParameter("objectID"));
    }

    /**
     * Get an iterator that understands how to format the detailed result
     * information into groups.
     *
     * @return a {@link net.project.base.finder.GroupingIterator} object
     * preinitialized with the detailed report information and with the selected
     * {@link net.project.base.finder.FinderGrouping} objects.
     * @throws net.project.persistence.PersistenceException if there is a database exception while
     * trying to construct the grouping iterator.
     */
    public abstract GroupingIterator getGroupingIterator() throws PersistenceException;

    /**
     * Clear out any data stored in this object and reset.
     */
    public abstract void clear();

	/**
	 * @return the hundredPercentComplete
	 */
	public boolean isHundredPercentComplete() {
		return hundredPercentComplete;
	}

	/**
	 * @param hundredPercentComplete the hundredPercentComplete to set
	 */
	public void setHundredPercentComplete(boolean hundredPercentComplete) {
		this.hundredPercentComplete = hundredPercentComplete;
	}
}
