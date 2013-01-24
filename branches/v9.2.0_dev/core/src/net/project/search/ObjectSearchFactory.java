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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
|
+--------------------------------------------------------------------------------------*/
package net.project.search;

import net.project.base.ObjectType;

/**
 * Static factory for making search classes for various object types.
 *
 * @author Roger
 * @since 01/2001
 */
public class ObjectSearchFactory {
    /**
     * Get an empty object of the proper type
     *
     * @param searchType the ObjectType of seach object to manufacture.
     * @return the proper type of search object implementing IObjectSearch
     */
    public static IObjectSearch make(String searchType) {
        // Default to DOCUMENT search
        if (searchType == null) {
            searchType = ObjectType.DOCUMENT;
        }

        // Now set appropriate search object based on searchType
        if (searchType.equals(ObjectType.DOCUMENT)) {
            return new DocumentSearch();
        } else if (searchType.equals(ObjectType.POST)) {
            return new PostSearch();
        } else if (searchType.equals(ObjectType.TASK)) {
            return new TaskSearch();
        } else if (searchType.equals(ObjectType.CALENDAR)) {
            return new CalendarSearch();
        } else if (searchType.equals(ObjectType.DELIVERABLE)) {
            return new DeliverableSearch();
        } else if (searchType.equals(ObjectType.FORM_DATA)) {
            return new FormDataSearch();
        } else if (searchType.equals(ObjectType.BLOG)) {
            return new BlogSearch();
        } else if (searchType.equals(ObjectType.WIKI)) {
            return new WikiSearch();
        }
        // No matching object type found
        else
            return null;
    }

}
