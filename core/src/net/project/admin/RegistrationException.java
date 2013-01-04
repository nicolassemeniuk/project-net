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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.admin;

import net.project.base.PnetException;

/**
 * New user registration failure.
 *
 * @author Roger Bly
 * @since 01/2000
 */
public class RegistrationException extends PnetException {
    /**
     * Constructs an RegistrationException
     */
    public RegistrationException() {
        super();
    }


    /**
     Constructs an RegistrationException with the specified detail message
     @param message detailed message
     */
    public RegistrationException(String message) {
        super(message);
    }

    /**
     * Constructs a registration message with only the cause of the exception.
     *
     * @param cause a <code>Throwable</code> object which was thrown during
     * registration which needs to be wrapped in a RegistrationException.
     */
    public RegistrationException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new RegistrationException with the specified message and
     * causing throwable.
     * @param message detailed message
     * @param cause the causing exception
     */
    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

}



