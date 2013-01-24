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

import org.apache.log4j.Logger;

/**
 * <code>PnetRuntimeException</code> provides a base class that specifies errors 
 * that are not explicitly trapped by the method declarations of a method.
 *
 * In almost every situation you can think of, errors are a bad idea.  It is
 * almost always better to throw an explicit exception than it is to throw an
 * error.  If you are considering subclassing this method, think twice.
 *
 * So, given this warning, why does PnetError still exist?  There are certain
 * categories of errors that it isn't worthwhile to implement.  These are errors
 * that are so common that they would have to be thrown in just about every
 * method that you write if they were implemented correctly.
 * {@link java.lang.NullPointerException} is a good example of this.  If you
 * were to throw this exception in your application in every place that could
 * access an object that was null, virtually every method would have to throw it.
 *
 * @author Matthew Flower (11/21/2001)
 * @since Gecko
 */
public class PnetRuntimeException extends java.lang.RuntimeException {

    /** Logging category. */
    private static final Logger logger = Logger.getLogger(PnetRuntimeException.class);

    public PnetRuntimeException() {
        super();
        logger.warn("PnetRuntimeException");
    }
    
    public PnetRuntimeException(String message) {
        super(message);
        logger.warn(message);
    }

    public PnetRuntimeException(String message, Throwable throwable) {
        super(message, throwable);
        logger.warn(message, throwable);
    }

    public PnetRuntimeException(Throwable throwable) {
        super(throwable);
        logger.warn("PnetRuntimeException", throwable);
    }

}
