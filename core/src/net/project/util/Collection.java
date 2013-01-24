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
package net.project.util;

/**
 * General list management utility.
 *
 * @author phil
 * @since 05/2001
 * @deprecated as of 7.3; See individual methods for alternate solutions
 */
public class Collection {
    /**
     * Standard constructor.
     */
    public Collection() {
        // do nothing
    }

    /**
     * Returns true if key is in String[] list.
     * @param key the key to look for in list
     * @param list the list in which to look for key
     * @return false if list is null or key is null or list does
     * not contain an element that is equal to key
     * @deprecated as of 7.3; use <code>java.util.Arrays.asList(list).contains(key)</code> instead.
     * Note, however that the java version differs:
     * <li>null lists are not permitted
     * <li>a null key will actually return a positive result if the
     * list contains a null entry <br>
     * This method will return false in both those cases.
     */
    public static boolean contains (String key, String[] list) {
        boolean match = false;

        // null list or null key is always a false match in this
        // implementation
        if (list != null && key != null) {
            match = java.util.Arrays.asList(list).contains(key);
        }

        return match;
    }
}
