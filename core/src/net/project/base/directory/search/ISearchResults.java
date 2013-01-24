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

/**
 * Results of performing a directory search.
 * Search Results are a list; it need not support any modification
 * methods.  Search Results are pageable too.
 */
public interface ISearchResults 
        extends java.util.List, 
                net.project.persistence.IXMLPersistence, 
                net.project.gui.pager.IPageable {

    /**
     * Returns a list containing <code>ISearchResult</code> objects
     * with the specified IDs.
     * The size of the returned list will only be the same as the length of
     * the array if <code>ISearchResult</code> objects were found
     * for each of the IDs.
     * @param resultIDs the ID values for the <code>ISearchResult</code>s
     * to return
     * @return the <code>ISearchResult</code>s with matching IDs
     */
    public java.util.List getForIDs(String[] resultIDs);

}
