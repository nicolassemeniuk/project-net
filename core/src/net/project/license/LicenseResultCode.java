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
 *
 * @author Vishwajeet Lohakarey
 * @since Gecko Update 3
 */
public class LicenseResultCode {

    /** The next ID to assign. */
    private static int nextID = 0;

    /** The id of the status code. */
    private int id = 0;

    /**
     * Creates a LicenseResultCode.
     */
    private LicenseResultCode() {
        this.id = LicenseResultCode.nextID++;
    }
    

    /**
     * Indicates whether specified LicenseResultCode is same as this one.
     */
    public boolean equals(Object obj) {
        if (obj instanceof LicenseResultCode &&
            obj != null &&
            ((LicenseResultCode) obj).id == this.id) {
            return true;
        }

        return false;
    }

    public int hashcode() {
        return this.id;
    }


    /** License is valid. */
    public static LicenseResultCode VALID = new LicenseResultCode();

    /** License is missing. */
    public static LicenseResultCode MISSING = new LicenseResultCode();

    /** License constraint has been exceeded. */
    public static LicenseResultCode CONSTRAINT_EXCEEDED = new LicenseResultCode();

    /** A failure occurred reading a license. */
    public static LicenseResultCode FAILURE = new LicenseResultCode();

    /** License certificate does not match key. */
    public static LicenseResultCode CERTIFICATE_KEY_MISMATCH = new LicenseResultCode();

}
