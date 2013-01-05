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
package net.project.project;

import net.project.base.PnetException;


/**
 * Indicates that the requested project space meta property does not exist.
 */
public class NoSuchPropertyException extends PnetException {

    /**
     * Creates a NoSuchPropertyException with the specified message.
     *
     * @param message the message
     */
    public NoSuchPropertyException(String message) {
        super(message);
    }

    /**
     * Creates a NoSuchPropertyException with the specified message
     * indicating the throwable that caused this exception.
     *
     * @param message the message
     * @param cause   the cause of the exception
     */
    public NoSuchPropertyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a NoSuchPropertyException with no message
     * indicating the throwable that caused this exception.
     *
     * @param cause the cause of the exception
     */
    public NoSuchPropertyException(Throwable cause) {
        super(cause);
    }
}
