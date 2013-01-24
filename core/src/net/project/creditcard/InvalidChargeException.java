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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.creditcard;

import net.project.base.PnetException;

/**
 * Generic message thrown when a credit card is invalid, or the currency being
 * charged to a credit card isn't valid.
 *
 * @author Matthew Flower
 * @since Version 7.5
 */
public class InvalidChargeException extends PnetException {
    /**
     * Creates a new InvalidChargeException with no message, logging the
     * exception.
     */
    public InvalidChargeException() {
    }

    /**
     * Creates a new InvalidChargeException with the specified message, logging
     * the exception.
     *
     * @param message the detailed message
     */
    public InvalidChargeException(String message) {
        super(message);
    }

    /**
     * Creates a new InvalidChargeException with the specified message and
     * specifying the cause of original exception. Logs the exception.
     *
     * @param message the detailed message
     * @param cause the cause exception
     */
    public InvalidChargeException(String message, Throwable cause) {
        super(message, cause);
    }
}
