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
package net.project.security.domain;

import net.project.base.directory.search.DirectorySearchException;
import net.project.base.directory.search.ISearchResults;
import net.project.base.directory.search.SearchControls;
import net.project.base.directory.search.SearchFilter;

/**
 * Provides a context for searching UserDomains.
 */
public class UserDomainDirectoryContext implements net.project.base.directory.search.IDirectoryContext {
    
    private UserDomain domain = null;

    protected UserDomainDirectoryContext(UserDomain domain) {
        this.domain = domain;
    }

    /**
     * Searches the directory.
     * @param filter the filter to apply while searching
     * @param controls the parameters affecting the search
     * @return the results of the search
     * @throws DirectorySearchException if there is a problem searching
     */
    public ISearchResults search(SearchFilter filter, SearchControls controls) throws DirectorySearchException {

        // TODO Need to search the current domain based on the specified filter and controls
        // Initially need to support a filter with the key "name"
        // and search first name / last name for that value
        //
        // see Roster.search() for similar functionality
        //
        // We should pass some form of results to UserDomainDirectorySearchResults
        //
        // See net.project.business.BusinessDirectorySearchResults

        return new UserDomainDirectorySearchResults();

    }
}

