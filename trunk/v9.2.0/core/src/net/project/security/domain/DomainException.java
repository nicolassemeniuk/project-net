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

 package net.project.security.domain;

import net.project.base.PnetException;

/**
    Object persistance failure.
    The underlying persistance system failed, of the object was not in a valid state for the requested persistance operation.
*/
public class DomainException extends PnetException {

    /**
        Constructs an DomainException
    */
    public DomainException() {
        super();
    }


    /**
        Constructs an DomainException with the specified detail message
        @param message detailed message
    */
    public DomainException(String message) {
        super(message);
    }

    /**
     * Creates a new DomainException with the specified message and
     * causing throwable.
     * @param message detailed message
     * @param cause the causing exception
     */
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}



