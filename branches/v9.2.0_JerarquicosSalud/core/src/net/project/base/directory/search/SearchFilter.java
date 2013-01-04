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

 /*-------------------------------------------------------------------+
|
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
+-------------------------------------------------------------------*/
package net.project.base.directory.search;

import java.util.HashMap;

/**
 * Provides filtering criteria to a search.
 */
public class SearchFilter {

    private HashMap filter = new HashMap();

    /**
     * Adds a filter term to this search filter.
     * Performs a "like" filter; that is it returns results
     * with values that contain the filterValue somewhere.1
     * @param filterName the thing to filter on
     * @param filterValue the value to filter
     */
    public void add(String filterName, String filterValue) {
        this.filter.put(filterName, filterValue);
    }

    /**
     * Returns the filtered value for the specified filter name.
     * @param filterName the thing being filtered on
     * @return the current value; or null if none was specified
     * for that name
     */
    public String get(String filterName) {
        return (String) this.filter.get(filterName);
    }

}
