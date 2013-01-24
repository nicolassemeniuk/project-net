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

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.base.finder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.project.util.VisitException;

/**
 * Provides an ordered list of <code>FinderColumn</code>s.
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class FinderColumnList {

    /**
     * The list of columns.
     */
    private List columnList = new ArrayList();

    /**
     * Adds a new column to the list of columns.
     *
     * @param column a <code>FinderColumn</code> object which should be added to
     * the internal list of columns.
     */
    public void add(FinderColumn column) {
        this.columnList.add(column);
    }

    /**
     * Remove all <code>FinderColumn</code> objects from the internal list of
     * finder columns.
     * After calling, the list is empty.
     */
    public void clear() {
        this.columnList.clear();
    }

    /**
     * Returns all columns in this column list.
     *
     * @return an unmodifiable list where each element is a <code>FinderColumn</code>.
     * The list may be empty if no columns have been added or it has been cleared
     */
    public List getAllColumns() {
        return Collections.unmodifiableList(this.columnList);
    }

    /**
     * Returns all of the columns that have the "selected" property set to true.
     * This normally corresponds to the columns that the user has selected in a
     * query screen.
     *
     * @return a list where each element is a <code>FinderColumn</code> that
     * has been selected. The list may be empty if no columns have been selected or
     * the list of columns has been cleared
     */
    public List getSelectedColumns() {
        ArrayList selectedColumns = new ArrayList();

        for (Iterator it = getAllColumns().iterator(); it.hasNext(); ) {
            FinderColumn currentColumn = (FinderColumn) it.next();
            if (currentColumn.isSelected()) {
                selectedColumns.add(currentColumn);
            }
        }

        return Collections.unmodifiableList(selectedColumns);
    }

    /**
     * Returns all of the columns that do not have the "selected" property set.
     *
     * @return a list where each element is a <code>FinderColumn</code> that
     * has not been selected. The list may be empty if all columns have been
     * selected
     */
    public List getUnselectedColumns() {

        ArrayList unselectedColumns = new ArrayList();

        for (Iterator it = getAllColumns().iterator(); it.hasNext(); ) {
            FinderColumn currentColumn = (FinderColumn) it.next();
            if (!currentColumn.isSelected()) {
                unselectedColumns.add(currentColumn);
            }
        }

        return Collections.unmodifiableList(unselectedColumns);
    }

    /**
     * Returns the <code>FinderColumn</code> with matching id.
     * Looks in all columns.
     *
     * @param columnID the id of the FinderColumn to return
     * @return the FinderColumn whose ID matches the specified id or <code>null</code>
     * if no FinderColumn has that id
     */
    public FinderColumn getForID(String columnID) {
        FinderColumn foundColumn = null;

        for (Iterator it = getAllColumns().iterator(); it.hasNext(); ) {
            FinderColumn nextColumn = (FinderColumn) it.next();
            if (nextColumn.getID().equals(columnID)) {
                foundColumn = nextColumn;
                break;
            }
        }

        return foundColumn;
    }

    /**
     * Returns the total number of items (selected or not) that appear in this
     * column list.
     *
     * @return an <code>int</code> value containing the number of items that
     * appear in this column list.
     */
    public int size() {
        return getAllColumns().size();
    }

    /**
     * Accepts an {@link IFinderIngredientVisitor} and invokes the
     * <code>visitFinderColumnList</code> method.
     * @param visitor the IFinderIngredientVisitor
     * @throws net.project.util.VisitException if there is a problem while visiting this
     * FinderColumnList
     * @throws ClassCastException if the visitor is not an IFinderIngredientVisitor
     */
    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitFinderColumnList(this);
    }

}
