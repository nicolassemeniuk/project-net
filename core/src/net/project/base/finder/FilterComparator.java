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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.gui.html.IHTMLOption;
import net.project.util.VisitException;

/**
 * Provides the base on which all other datatype-specific comparators are
 * defines.
 * Sub-classes are expected to define the actual compariosn constants since
 * each different data type may present a similar comparator in a different way.
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public abstract class FilterComparator implements IHTMLOption, Serializable {

    //
    // Static Members
    //

    /**
     * Helper method for getting the filter comparator which corresponds to the supplied ID, or
     * the specified default if there is not one that corresponds.
     *
     * @param id a <code>String</code> value corresponding to the id value for
     * a FilterComparator in the specified collection
     * @param comparatorCollection the collection of <code>FilterComparator</code>s
     * to look in
     * @param defaultComparator the default comparator to return if none
     * is found matching the specified id
     * @return a <code>FilterComparator</code> which has the same id as the id
     * parameter the calling method passed in.  Otherwise, the specified default
     * is returned
     */
    protected static FilterComparator getForID(String id, Collection comparatorCollection, FilterComparator defaultComparator) {
        FilterComparator comparator = defaultComparator;
        boolean isFound = false;

        for (Iterator it = comparatorCollection.iterator(); it.hasNext() && !isFound; ) {
            FilterComparator current = (FilterComparator) it.next();
            if (current.getID().equals(id)) {
                comparator = current;
                isFound = true;
            }
        }

        return comparator;
    }

    /**
     * Helper method to ensure the new comparator has a unique ID with respect
     * to the FilterComparators in the specified collection.
     * Returns silently if the new comparator may be added to the collection
     * without violating the uniqueness of the IDs.
     * @param comparatorCollection the collection of comparators in which the
     * new comparator must be unique
     * @param newComparator the new comparator
     * @throws IllegalStateException if the new comparator's ID is found in
     * a comparator in the specified collection
     */
    protected static void assertUniqueID(Collection comparatorCollection, FilterComparator newComparator) {

        for (Iterator it = comparatorCollection.iterator(); it.hasNext(); ) {
            FilterComparator nextComparator = (FilterComparator) it.next();

            if (nextComparator.getID().equals(newComparator.getID())) {
                throw new IllegalStateException("Filter Comparator IDs must be unique");
            }

        }

    }

    //
    // Instance Members
    //

    /** A token that will be displayed in the selection list of comparators. */
    private String displayToken = null;

    /** A unique identifier for this <code>FilterComparator</code>. */
    private String id = null;

    /**
     * Creates a new FilterComparator.
     * @param id a <code>String</code> value which uniquely identifies this
     * FilterComparator among other filter comparators of the same type
     * @param displayToken a <code>String</code> value which contains a property
     * value which points to a string which will used to display this
     * <code>FilterComparator</code>.
     */
    protected FilterComparator(String id, String displayToken) {
        this.id = id;
        this.displayToken = displayToken;
    }

    /**
     * Get a unique identifier for this FilterComparator.
     *
     * @return a <code>String</code> value which uniquely identifies this
     * <code>FilterComparator</code>.
     */
    public String getID() {
        return id;
    }

    /**
     * Returns the value for the <code>value</code> attribute of the HTML
     * option.
     *
     * @return a <code>String</code> which is the content of the <code>value</code>
     * attribute of the <code>&lt;option&gt;</code> tag.
     */
    public String getHtmlOptionValue() {
        return id;
    }

    /**
     * Returns the value for the content part of the HTML option.
     *
     * @return a <code>String</code> value that will be displayed for this
     * html option.
     */
    public String getHtmlOptionDisplay() {
        return getSymbol();
    }

    /**
     * The symbol that will be displayed in the presentation of this filter
     * comparator.
     *
     * @return a <code>String</code> value which should be displayed if the
     * number is rendered for presentation.
     */
    public String getSymbol() {
        return PropertyProvider.get(displayToken);
    }

    /**
     * Creates a where clause based on this comparator for the specified
     * field name and values.
     * @param fieldName the name of the field (column) on which to base the
     * where clause
     * @param fieldValues the values for the where clause
     * @param isNullIncluded true if the null value should be tested in the
     * where clause
     * @return the where clause
     */
    public abstract String createWhereClause(String fieldName, String[] fieldValues, boolean isNullIncluded);

    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitFilterComparator(this);
    }


}

