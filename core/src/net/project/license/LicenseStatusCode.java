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
package net.project.license;

/**
 * Enumeration of status codes used when checking a license.
 */
public class LicenseStatusCode {

    /** The id of the status code. */
    private int id = 0;
    
    /**
     * Creates a LicenseResultCode.
     */
    public LicenseStatusCode() {
        
    }

    /**
     * Creates a LicenseResultCode.
     */
    public LicenseStatusCode(int statusID) {
        this.id = statusID;
    }

    /**
     * Indicates whether specified LicenseStatusCode is same as this one.
     */
    public boolean equals(Object obj) {
        if (obj instanceof LicenseStatusCode &&
            obj != null &&
            ((LicenseStatusCode) obj).id == this.id) {
            return true;
        }

        return false;
    }

    public int hashcode() {
        return this.id;
    }

    public int getCodeID() {
        return this.id;
    }

    /** License is Enabled. */
    public static final LicenseStatusCode ENABLED = new LicenseStatusCode(100);

    /** License is Disabled. */
    public static final LicenseStatusCode DISABLED = new LicenseStatusCode(200);

    /** License is Canceled. */
    public static final LicenseStatusCode CANCELED = new LicenseStatusCode(300);

}
