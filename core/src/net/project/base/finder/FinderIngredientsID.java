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

import java.util.Collection;
import java.util.Iterator;

/**
 * Provides a base class for defining an enumeration of FinderIngredientsIDs.
 * IDs for the individual components of a FinderIngredients object must be
 * explicitly defined and be constant across product versions.  This is to allow
 * persistence of ingredients.
 * <p>
 * It is recommended that each persistent ID is defined as a <code>FinderIngredientsID</code>
 * and named.  This ensures that two locations referring to the same ID always
 * use the same value and that there is no temptation to modify the ID value
 * in locations that use it.
 * </p>
 * <p>
 * This class also ensures that ID values of two IDs in the same collection
 * are unique.  This is important to ensure that each component in a
 * <code>FinderIngredients</code> object is uniquely identifiable.
 * </p>
 * <p>
 * Example usage:
 * <pre><code>
 *    class MyIngredientsID extends FinderIngredientsID {
 *        private static final idList = new ArrayList();
 *
 *        static final FinderIngredientsID MY_FILTER_1 = new FinderIngredientsID("10");
 *        static final FinderIngredientsID MY_FILTER_2 = new FinderIngredientsID("20");
 *        static final FinderIngredientsID MY_COLUMN_3 = new FinderIngredientsID("30");
 *
 *        private MyIngredientsID(String id) {
 *            super(idList, id);
 *        }
 *
 *    }
 *
 *    ...
 *    ingredients.getFinderFilters().add(new NumberFilter(MY_FILTER_1.getID()));
 *    ingredients.getFinderFilters().add(new TextFilter(MY_FILTER_2.getID()));
 *    ...
 *
 * </code></pre>
 * </p>
 */
public abstract class FinderIngredientsID {

    //
    // Static members
    //

    /**
     * Helper method to return the FinderIngredientsID with the specified id.
     *
     * @param ingredientsIDCollection the collection of <code>FinderIngredientsID</code>s
     * to look in
     * @param id the id of the value to find
     * @return the FinderIngredientsID with matching id, or null if no value is
     * found with that id
     */
    protected static FinderIngredientsID findByID(Collection ingredientsIDCollection, String id) {
        FinderIngredientsID foundFinderIngredientsID = null;
        boolean isFound = false;

        for (Iterator it = ingredientsIDCollection.iterator(); it.hasNext() && !isFound;) {
            FinderIngredientsID nextFinderIngredientsID = (FinderIngredientsID) it.next();
            if (nextFinderIngredientsID.getID().equals(id)) {
                foundFinderIngredientsID = nextFinderIngredientsID;
                isFound = true;
            }
        }

        return foundFinderIngredientsID;
    }

    //
    // Instance members
    //

    /**
     * The unique id.
     */
    private String id = null;

    /**
     * Creates a new FinderIngredientsID.
     * @param finderIngredientsIDCollection the collection of <code>FinderIngredientsID</code>s
     * to which to add this new <code>FinderIngredientsID</code> and in which
     * to check for uniqueness
     * @param id the internal id to assign to the new FinderIngredientsID
     * @throws IllegalStateException if the specified id is not unique
     * with respect to the other elements in the specified collection; in this
     * case the collection is not modified
     */
    protected FinderIngredientsID(Collection finderIngredientsIDCollection, String id) {
        this.id = id;
        assertUnique(finderIngredientsIDCollection, this);
        finderIngredientsIDCollection.add(this);
    }

    /**
     * Returns the internal id of this FinderIngredientsID.
     * @return the id
     */
    public String getID() {
        return this.id;
    }

    /**
     * Indicates whether the specified object is a FinderIngredientsID with
     * matching ID.
     * @param o the FinderIngredientsID object to compare
     * @return true if the specified FinderIngredientsID has a matching id; false otherwise
     */
    public boolean equals(Object o) {
        boolean isEqual = false;

        if (this == o) {

            isEqual = true;

        } else {

            if (o instanceof FinderIngredientsID) {
                FinderIngredientsID ingredientsIDList = (FinderIngredientsID) o;
                if (id.equals(ingredientsIDList.id)) {
                    isEqual = true;
                }
            }

        }

        return isEqual;
    }

    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Asserts that the specified ingredientsID is unique within the specified
     * collection.
     * It is assumed that the the ingredientsID has not yet been added
     * to the collection.
     * @param finderIngredientsIDCollection the collection of <code>FinderIngredientsID</code>s
     * to check
     * @param ingredientsID the ingredientsID to check
     * @throws IllegalStateException if the ID is not unique
     */
    private void assertUnique(Collection finderIngredientsIDCollection, FinderIngredientsID ingredientsID) {
        if (FinderIngredientsID.findByID(finderIngredientsIDCollection, ingredientsID.getID()) != null) {
            throw new IllegalStateException("ID is not unique");
        }
    }

}
