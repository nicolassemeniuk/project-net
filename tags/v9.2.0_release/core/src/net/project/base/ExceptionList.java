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

 package net.project.base;

import java.util.List;

/**
 * This class is designed to work list an exception, except that internally, it
 * stores a list of error messages or exceptions.
 *
 * @author Matthew Flower
 * @since Gecko Update 3
 */
public class ExceptionList extends PnetException {
    /** A list of exceptions */
    private List exceptions;

    public ExceptionList() {
        super();
    }

    public ExceptionList(String message) {
        super(message);
    }

    /**
     * Construct an ExceptionList object given a list of exceptions and an error
     * message.
     *
     * @param message a <code>String</code> value containing the error message
     * that summarizes the reason this object is being constructed.
     * @param exceptions a <code>List</code> value containing either a list of
     * error messages or exceptions.
     */
    public ExceptionList(String message, List exceptions) {
        super(message);
        this.exceptions = exceptions;
    }

    /**
     * Get the list of exceptions being stored in this object.
     *
     * @return a <code>List</code> value containing a list of String error messages
     * or exceptions.
     */
    public List getExceptions() {
        return this.exceptions;
    }

    /**
     * Set the list of exceptions for this object
     *
     * @param exceptionList a <code>List</code> value containing either Strings or
     * exception objects.
     */
    public void setExceptions(List exceptionList) {
        this.exceptions = exceptionList;
    }
}
