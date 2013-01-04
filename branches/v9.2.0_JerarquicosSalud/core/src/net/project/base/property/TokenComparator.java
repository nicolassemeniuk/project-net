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
| Singleton PropertyProvider
+----------------------------------------------------------------------*/
package net.project.base.property;

/**
 * A comparator which compares tokens based on name.
 */
class TokenComparator implements java.util.Comparator {

    /**
     * Compares two tokens based on name, ignoring case.
     * @see String#compareToIgnoreCase
     */
    public int compare(Object field1, Object field2) {

        String t1 = ((Token) field1).getName();
        String t2 = ((Token) field2).getName();

        return (t1.compareToIgnoreCase(t2));
    }
}
