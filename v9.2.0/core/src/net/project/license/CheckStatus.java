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
 * This class supplies a boolean value that includes a message.  This can allow
 * a calling function to know why a boolean method result has a certain value.
 *
 * An example of when this class might be used could be when the system attempt
 * to determine if a license is current.  If {@link #booleanValue} returned
 * false, the message might say (for example) that this is a time locked license
 * and that the time period it was valid for has expired.
 *
 * @author Tim Morrow
 * @since Gecko Update 3
 */
public class CheckStatus {
    /**
     * The boolean field which is essentailly a "success" flag for a licensing
     * operation.
     */
    private boolean value = false;
    /**
     * The message which indicates why this <code>CheckStatus</code> has a value.
     * Often, this will only be set if {@link #value} is equal to value.
     */
    private String message = null;

    /**
     * Creates a new CheckStatus with the specified boolean value.
     * @param value the boolean value
     */
    public CheckStatus(boolean value) {
        this.value = value;
    }

    /**
     * Creates a CheckStatus with the specified boolean value and message.
     * @param value the boolean value
     * @param message the message
     */
    public CheckStatus(boolean value, String message) {
        this(value);
        setMessage(message);
    }

    /**
     * Returns the value of this CheckStatus has a boolean primitive.
     * @return the primitive boolean value of this CheckStatus
     */
    public boolean booleanValue() {
        return this.value;
    }

    /**
     * Indicates whether this CheckStatus has a message.
     * @return true if this CheckStatus has a message
     */
    public boolean hasMessage() {
        return this.message != null;
    }

    /**
     * Sets the message for this check.
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
