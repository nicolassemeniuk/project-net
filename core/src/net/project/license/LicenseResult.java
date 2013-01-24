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
 * Provides the status of checking a license for availability.
 *
 * @author Vishwajeet Lohakarey
 * @since Gecko Update 3
 */
public class LicenseResult {

    private LicenseResultCode code = null;
    private String message = null;

    /**
     * Creates a new LicenseResult with the specified code.
     * @param code the status code
     */
    public LicenseResult(LicenseResultCode code) {
        this.code = code;
    }

    /**
     * Creates a LicenseResult with the specified code and message.
     * @param code the status code
     * @param message the message
     */
    public LicenseResult(LicenseResultCode code, String message) {
        this(code);
        setMessage(message);
    }

    /**
     * Returns the status code.
     * @return the status code
     */
    public LicenseResultCode getCode() {
        return this.code;
    }

    /**
     * Indicates whether this LicenseResult has a message.
     * @return true if this LicenseResult has a message
     */
    public boolean hasMessage() {
        return this.message != null;
    }

    /**
     * Sets the message for this status.
     * @param message the message
     */
    private void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the current message.
     * @return the current message or <code>null</code> if there isn't one
     * @see #hasMessage
     */
    public String getMessage() {
        return this.message;
    }

}

