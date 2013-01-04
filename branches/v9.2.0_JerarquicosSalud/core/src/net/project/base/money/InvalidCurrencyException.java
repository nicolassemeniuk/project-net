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
package net.project.base.money;

/**
 * An exception that indicates a currency is invalid.
 * <p>
 * A currency code must be a three-character ISO 4217 currency code that
 * is also supported by Java.  Currently, the {@link java.util.Currency} class
 * does not provide a method to enumerate all supported currencies.
 * </p>
 * @author Tim
 * @since 7.4
 */
public class InvalidCurrencyException extends net.project.base.PnetException {

    /**
     * Creates an empty InvalidCurrencyException.
     */
    public InvalidCurrencyException() {
        super();
    }

    /**
     * Creates an InvalidCurrencyException with the specified message.
     * @param message the message
     */
    public InvalidCurrencyException(String message) {
        super(message);
    }

    /**
     * Creates an InvalidCurrencyException with the specified message
     * indicating the throwable that caused this exception.
     * @param message the message
     * @param cause the cause of the exception
     */
    public InvalidCurrencyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an InvalidCurrencyException with no message
     * indicating the throwable that caused this exception.
     * @param cause
     */
    public InvalidCurrencyException(Throwable cause) {
        super(cause);
    }

}

