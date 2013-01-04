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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.project.base.property.PropertyProvider;

/**
 * Provides an Enumeration of Filter Operators.
 * These are used when producing a where clause for a filter list.
 *
 * @author Tim Morrow
 * @since Version 7,4
 */
public class FilterOperator implements Serializable {

    //
    // Static members
    //

    private static List operatorList = new ArrayList();

    /**
     * AND operator.
     */
    public static final FilterOperator AND = new FilterOperator("and", "AND");

    /**
     * OR operator.
     */
    public static final FilterOperator OR = new FilterOperator("or", "OR");

    /**
     * Returns the FilterOperator with the specified id.
     * @param id the id of the value to find
     * @return the FilterOperator with matching id, or null if no value is
     * found with that id
     */
    public static FilterOperator findByID(String id) {
        FilterOperator foundFilterOperator = null;
        boolean isFound = false;

        for (Iterator it = FilterOperator.operatorList.iterator(); it.hasNext() && !isFound;) {
            FilterOperator nextFilterOperator = (FilterOperator) it.next();
            if (nextFilterOperator.getID().equals(id)) {
                foundFilterOperator = nextFilterOperator;
                isFound = true;
            }
        }

        return foundFilterOperator;
    }

    public static List getAllFilterOperators() {
        return Collections.unmodifiableList(FilterOperator.operatorList);
    }


    //
    // Instance members
    //

    /**
     * The unique id.
     */
    private String id = null;

    /**
     * The SQL operator represented by this FilterOperator.
     */
    private String sqlOperator = null;

    /**
     * Creates a new FilterOperator.
     * @param id the id
     * @param sqlOperator the SQL operator represented by this FilterOperator
     */
    private FilterOperator(String id, String sqlOperator) {
        this.id = id;
        this.sqlOperator = sqlOperator;
        operatorList.add(this);
    }

    /**
     * Returns the internal id of this FilterOperator.
     * @return the id
     */
    public String getID() {
        return this.id;
    }

    /**
     * Returns the SQL operator.
     * @return the SQL operator
     */
    public String getSQLOperator() {
        return PropertyProvider.get(this.sqlOperator);
    }

    /**
     * Indicates whether the specified object is a FilterOperator with
     * matching ID.
     * @param o the FilterOperator object to compare
     * @return true if the specified FilterOperator has a matching id; false otherwise
     */
    public boolean equals(Object o) {
        boolean isEqual = false;

        if (this == o) {

            isEqual = true;

        } else {

            if (o instanceof FilterOperator) {
                FilterOperator operatorList = (FilterOperator) o;
                if (id.equals(operatorList.id)) {
                    isEqual = true;
                }
            }

        }

        return isEqual;
    }

    public int hashCode() {
        return id.hashCode();
    }

}
