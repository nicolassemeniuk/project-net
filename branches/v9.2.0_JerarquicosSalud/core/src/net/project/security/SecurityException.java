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

 package net.project.security;


/**
 * General Security Exception.  Will be thrown by any security object.
 * 
 * @author Philip Dixon
 * @see net.project.base.PnetException
 * @since Gecko
 */
public class SecurityException extends net.project.base.PnetException {

    /**
        Constructs an SecurityException
    */
    public SecurityException() {
        super();
    }


    /**
        Constructs an SecurityException with the specified detail message
        @param message detailed message
    */
    public SecurityException(String message) {
        super(message);
    }

    /**
     * Creates a new SecurityException with the specified message and
     * causing throwable.
     * @param message detailed message
     * @param cause the causing exception
     */
    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}

