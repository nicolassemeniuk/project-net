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
|
+----------------------------------------------------------------------*/
package net.project.base;

import net.project.license.LicenseStatusCodeAdapter;

/**
 * Used to contain all filter options to be used when loading a list of users from the directory
 * 
 * Used primarily by Directory.loadFiltered() method(s)
 * 
 * @author Phil Dixon
 * @version Gecko 3
 */
public class DirectoryFilter implements java.io.Serializable {

    /** Stores keyword filter(s) */
    private String keywordFilter = null;
    /** Stores any userStatus filter information for loading */
    private String[] userStatusFilter = null;
    /** UserDomain filter.*/
    private String[] userDomainFilter = null;
    /** Store which licensing filters */
    private String[] licenseFilter = new String[] { "A" };

    /**
      * Set an filter into the object for a list of user statuses
      * 
      * @param userStatusFilter
      */
     public void setUserStatusFilter (String[] userStatusFilter) {
         this.userStatusFilter = userStatusFilter;
     }

    /**
     * Set an filter into the object for a list of user statuses
     * 
     * There are two types of license statuses.  The first is the type that 
     * appears in the database.  These are codes 100, 200, and 300, which 
     * correspond to Active, Cancelled, and Disabled respectively.  The second
     * type are the characters "L", "U", or "A" which correspond to "Licensed", 
     * "Unlicensed", and "All" respectively.
     *
     * This method accepts the codes for the second type.
     *
     * @return a <code>String[]</code> value containing the license status filters
     * being applied to the directory.
     */
    public String[] getUserStatusFilter() {
        return this.userStatusFilter;
    }
    
    /**
     * This filter determines whether licensed, unlicensed, or all users will be
     * shown from the user list.
     * 
     * There are two types of license statuses.  The first is the type that 
     * appears in the database.  These are codes 100, 200, and 300, which 
     * correspond to Active, Cancelled, and Disabled respectively.  The second
     * type are the characters "L", "U", or "A" which correspond to "Licensed", 
     * "Unlicensed", and "All" respectively.
     *
     * This method accepts the codes for the second type.
     *
     * @param licenseFilter a <code>String[]</code> value.  Each string will 
     * contain either an "L", "U", or "A".  
     */
     public void setLicenseTypeFilter(String[] licenseFilter) {
         this.licenseFilter = licenseFilter;
     }
     
     /**
      * Get the list of license codes.
      */
    public String[] getLicenseTypeFilter() {
        return this.licenseFilter;
    }
     
     /**
      * This filter determines which license filter to apply.
      *
      * @return a <code>String[]</code> value containing a list of the license
      * status codes that we want to search on.
      */
     public String[] getLicenseStatusCodeFilter() {
         LicenseStatusCodeAdapter lsca = new LicenseStatusCodeAdapter();         
         lsca.setLicenseStatusCodes(licenseFilter);
         
         return lsca.getDBLicenseStatusCodes();
     }

    /**
     * Set a filter into the object for a list of userDomains to load the user by
     * 
     * @param userDomainFilter
     */
     public void setUserDomainFilter (String[] userDomainFilter) {
         this.userDomainFilter = userDomainFilter;
     }

    /**
     * Get the list of userDomains to filter by
     * 
     */
     public String[] getUserDomainFilter () {
         return this.userDomainFilter;
     }

    /**
     * Set the loading filter
     * 
     * @param filter
     */
    public void setKeywordFilter (String filter) {
        this.keywordFilter = filter;
    }

    /**
     * Return the filter
     * 
     * @return 
     */
    public String getKeywordFilter() {
        return this.keywordFilter;
    }

    public void clear() {
        keywordFilter = null;
        userStatusFilter = null;
        userDomainFilter = null;
    }

}
