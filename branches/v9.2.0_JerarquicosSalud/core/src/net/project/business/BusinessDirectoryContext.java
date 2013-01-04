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
package net.project.business;

import net.project.base.directory.search.DirectorySearchException;
import net.project.base.directory.search.ISearchResults;
import net.project.base.directory.search.SearchControls;
import net.project.base.directory.search.SearchFilter;
import net.project.resource.Roster;

/**
 * Provides a context for searching Businesses.
 */
public class BusinessDirectoryContext implements net.project.base.directory.search.IDirectoryContext {
    
    private BusinessSpace businessSpace = null;

    protected BusinessDirectoryContext(BusinessSpace businessSpace) {
        this.businessSpace = businessSpace;
    }

    /**
     * Searches the directory.
     * @param filter the filter to apply while searching
     * @param controls the parameters affecting the search
     * @return the results of the search
     * @throws DirectorySearchException if there is a problem searching
     */
    public ISearchResults search(SearchFilter filter, SearchControls controls) throws DirectorySearchException {

        Roster roster = new Roster();
        roster.setSpace(this.businessSpace);
        
        String name = filter.get("name");

        roster.search(name);

        return new BusinessDirectorySearchResults(roster);

    }
}

