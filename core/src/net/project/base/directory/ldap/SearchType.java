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
|   $Revision: 14743 $
|       $Date: 2006-02-06 22:26:39 +0530 (Mon, 06 Feb 2006) $
|     $Author: andrewr $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.directory.ldap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Provides an enumeration of <code>SearchType</code> selections.
 */
public class SearchType {

    //
    // Static members
    //

    /** 
     * All search types, provided for easy lookup of search types by
     * id.
     */
    private static List allSearchTypes = new ArrayList();
    
    /**
     * Returns the SearchType for an internal id.
     * @param searchTypeID the internal id of the search type to get
     * @return the <code>SearchType</code> with mathing internal id, 
     * or null if none matched the id
     */
    public static SearchType forID(String searchTypeID) {
        SearchType foundType = null;

        for (Iterator it = SearchType.allSearchTypes.iterator(); it.hasNext() && foundType == null; ) {
            
            SearchType nextType = (SearchType) it.next();
            if (nextType.id.equals(searchTypeID)) {
                foundType = nextType;
            }
        
        }

        return foundType;
    }


    //
    // Instance members
    //

    /** The internal ID of the SearchType. */
    private String id = null;

    /** 
     * Creates a SearchType with the specified internal id.
     * @param id the internal id for the search type
     */
    private SearchType(String id) {
        this.id = id;
        allSearchTypes.add(this);
    }

    /**
     * Returns the internal id of this SearchType.
     * @return the id
     */
    public String getID() {
        return this.id;
    }

    /** 
     * Indicates whether the specified object is equal to this SearchType.
     * @param obj the SearchType object to compare
     * @return true if the specified object is of type <code>SearchType</code>
     * and has a matching internal id
     */
    public boolean equals(Object obj) {
        boolean isEqual = false;

        if (obj == this) {
            isEqual = true;
        
        } else {
            
            if (obj != null &&
                obj instanceof SearchType &&
                ((SearchType) obj).getID().equals(this.id)) {

                isEqual = true;
            }
        
        }

        return isEqual;
    }

    public int hashCode() {
        return getID().hashCode();
    }

    public String toString() {
        return getID();
    }

    //
    // Constants
    //

    /**
     * Search all subtrees below search base DN.
     */
    public static SearchType ALL_SUBTREES = new SearchType("all_subtrees");

    /**
     * Search specific subtree(s) below search base DN.
     */
    public static SearchType LIMIT_SUBTREES = new SearchType("limit_subtrees");

}
