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
package net.project.base.finder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.util.VisitException;

/**
 * This object is designed to contain a list of finder groupings for the purpose
 * of displaying them on an HTML form and for the purpose of maintaining their
 * values when the finder groupings are submitted back to the application.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class FinderGroupingList {
    /**
     * This list contains all the {@link net.project.base.finder.FinderGrouping}
     * objects that belong to the list.
     */
    private List groupingList = new ArrayList();

    /**
     * Public constructor.
     */
    public FinderGroupingList() {
        super();
    }

    /**
     * Add a new {@link net.project.base.finder.FinderGrouping} object to the
     * internal list of <code>FinderGrouping</code> objects.
     *
     * @param grouping a <code>FinderGrouping</code> object that should be added
     * to the internal list of <code>FinderGrouping</code> objects.
     */
    public void add(FinderGrouping grouping) {
        groupingList.add(grouping);
    }

    /**
     * Remove any <code>FinderGrouping</code> objects that we are storing from
     * our internal list.
     */
    public void clear() {
        groupingList.clear();
    }

    /**
     * Return a list of {@link net.project.base.finder.FinderGrouping} objects
     * that the user has selected.
     *
     * @return a <code>List</code> of selected groupings.
     */
    public List getSelectedGroupings() {
        ArrayList selectedGroupings = new ArrayList();

        for (Iterator it = groupingList.iterator(); it.hasNext(); ) {
            FinderGrouping fg = (FinderGrouping)it.next();

            if (fg.isSelected()) {
                selectedGroupings.add(fg);
            }
        }

        return selectedGroupings;
    }

    /**
     * Get a list of all {@link net.project.base.finder.FinderGrouping} objects
     * that are currently contained in this object.
     *
     * @return a <code>List</code> object containing all of the
     * <code>FinderGrouping</code> objects currently contained in this object.
     */
    public List getAllGroupings() {
        return groupingList;
    }

    /**
     * Returns the <code>FinderGrouping</code> with matching id.
     * Looks in all groupings.
     * @param groupingID the id of the FinderGrouping to return
     * @return the FinderGrouping whose ID matches the specified id or null
     * if no FinderGrouping has that id
     */
    public FinderGrouping getForID(String groupingID) {
        FinderGrouping foundGrouping = null;

        for (Iterator it = getAllGroupings().iterator(); it.hasNext(); ) {
            FinderGrouping nextGrouping = (FinderGrouping) it.next();
            if (nextGrouping.getID().equals(groupingID)) {
                foundGrouping = nextGrouping;
                break;
            }
        }

        return foundGrouping;
    }

    /**
     * Accept a visitor who will visit every grouping in this list.
     *
     * @param visitor
     */
    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitFinderGroupingList(this);
    }

    /**
     * Get the total number of groupings (selected or not) that appear in this
     * grouping list.
     *
     * @return an <code>int</code> value identifying the number of grouping
     * items in this list.
     */
    public int size() {
        return groupingList.size();
    }

}
