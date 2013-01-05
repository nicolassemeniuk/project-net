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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.base.finder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.project.util.VisitException;

/**
 * A collection of finder sorters.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class FinderSorterList {
    /**
     * The internal list of sorters.  We don't extend the finder sorter list
     * directly from ArrayList so we can perform additional handling as needed.
     */
    private final ArrayList sorterList = new ArrayList();

    /**
     * Add the specified <code>FinderSorter</code> to the internal list of
     * <code>FinderSorter</code> objects.
     *
     * @param sorter a <code>FinderSorter</code> object which will be added to
     * the internal list of values.
     * @return a <code>boolean</code> value indicating whether or not this method
     * caused the internal list to change.
     */
    public boolean add(FinderSorter sorter) {
        return sorterList.add(sorter);
    }

    /**
     * Add every <code>FinderSorter</code> included in the collection into our
     * internal list of finder sorters.  Every object in the <code>Collection</code>
     * must be a <code>FinderSorter</code> object.
     *
     * @param finderSorters a <code>Collection</code> of FinderSorter objects.
     * @return a <code>boolean</code> value indicating whether or not the internal
     * list changed as a result of this method.
     */
    public boolean addAll(Collection finderSorters) {
        return sorterList.addAll(finderSorters);
    }

    /**
     * Remove any reference to sorters that is being stored internally.
     */
    public void clear() {
        sorterList.clear();
    }

    /**
     * Return all FinderSorter objects currently stored internally in this object.
     *
     * @return a <code>List</code> of {@link net.project.base.finder.FinderSorter}
     * objects.
     */
    public List getAllSorters() {
        return sorterList;
    }

    /**
     * Return a list of any sorters stored internally that are currently
     * selected.
     *
     * @return a <code>List</code> of {@link net.project.base.finder.FinderSorter}
     * objects that are currently selected.
     */
    public List getSelectedSorters() {
        ArrayList selectedList = new ArrayList();

        for (Iterator it = sorterList.iterator(); it.hasNext(); ) {
            FinderSorter currentSorter = (FinderSorter)it.next();

            if (currentSorter.isSelected()) {
                selectedList.add(currentSorter);
            }
        }

        return selectedList;
    }

    /**
     * Returns the <code>FinderSorter</code> with matching id.
     * Looks in all sorters.
     * @param sorterID the id of the FinderSorter to return
     * @return the FinderSorter whose ID matches the specified id or null
     * if no FinderSorter has that id
     */
    public FinderSorter getForID(String sorterID) {
        FinderSorter foundSorter = null;

        for (Iterator it = getAllSorters().iterator(); it.hasNext(); ) {
            FinderSorter nextSorter = (FinderSorter) it.next();
            if (nextSorter.getID().equals(sorterID)) {
                foundSorter = nextSorter;
                break;
            }
        }

        return foundSorter;
    }

    /**
     * Accept a visitor.
     *
     * @param visitor
     */
    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitFinderSorterList(this);
    }

    /**
     * Return the total number of sorters (selected or not) that appear in this
     * filter sorter list.
     *
     * @return an <code>int</code> value indicating the number of sorters that
     * appear in this list.
     */
    public int size() {
        return sorterList.size();
    }

}
