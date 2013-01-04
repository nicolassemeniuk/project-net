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
package net.project.license;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is responsible for translating the Licensing search type to 
 * Licensing status codes.  (The codes in the database.)
 * 
 * Licensing Search Types are "L", "U", or "A", which correspond to
 * "Licensed", "Unlicensed", or "All" respectively.
 */
public class LicenseStatusCodeAdapter {
    String[] licenseStatusCodes = null;
    
    /**
     * Set a list of licenses status codes in the "L", "U", "A" format to the 
     * 100,200,300 format.
     */
    public void setLicenseStatusCodes(String[] licenseStatusCodes) {
        this.licenseStatusCodes = licenseStatusCodes; 
    }
    
    /**
     * Get the list of license status codes in "L", "U", "A" format.
     */
    public String[] getLicenseStatusCodes() {
        return this.licenseStatusCodes;
    }
    
    /**
     * Get the license status code as a list of 100, 200, 300.
     */
    public String[] getDBLicenseStatusCodes() {
        List oldStatusCodeList;

        if (licenseStatusCodes != null) 
            oldStatusCodeList = Arrays.asList(licenseStatusCodes);
        else
            oldStatusCodeList = new ArrayList();

        ArrayList newStatusCodeList = new ArrayList();
        
        if (oldStatusCodeList.contains("A")) {
            newStatusCodeList.add(String.valueOf(LicenseStatusCode.ENABLED.getCodeID()));
            newStatusCodeList.add(String.valueOf(LicenseStatusCode.DISABLED.getCodeID()));
            newStatusCodeList.add(String.valueOf(LicenseStatusCode.CANCELED.getCodeID()));
            newStatusCodeList.add(null);
        } 
        
        if (oldStatusCodeList.contains("U")) {
            newStatusCodeList.add(String.valueOf(LicenseStatusCode.DISABLED.getCodeID()));
            newStatusCodeList.add(String.valueOf(LicenseStatusCode.CANCELED.getCodeID()));
            newStatusCodeList.add(null);
        } 
        
        if (oldStatusCodeList.contains("L")) {
            newStatusCodeList.add(String.valueOf(LicenseStatusCode.ENABLED.getCodeID()));
        } 
        
        String returnStrings[] = new String[]{};
        if (newStatusCodeList.size() > 0 ) {
            returnStrings = (String[])newStatusCodeList.toArray(returnStrings);
        } else {
            returnStrings = new String[0];
        }

        return returnStrings;
    }
    
    public final String UNLICENSED = "U";
    public final String LICENSED = "L";
    public final String ALL = "A";
}
