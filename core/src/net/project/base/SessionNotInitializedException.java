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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base;

/**
  * This exception is thrown when a user is accessing a page that does NOT
  * require authentication but properites have not been loaded.
  * This is used to redirect to an initialization page.
  */
public class SessionNotInitializedException extends net.project.security.AuthenticationRequiredException {

    /**
     * Constructs an empty SessionNotInitializedException.
     */
    public SessionNotInitializedException() {
        super();
    }

    /**
     * Constructs an SessionNotInitializedException with the specified message.
     * @param message the message
     */
    public SessionNotInitializedException(String message) {
        super(message);
    }

    /**
     * Constructs an SessionNotInitializedException with the specified message
     * and causing Throwable.
     * @param message the message
     * @param cause the Throwable causing this exception to be thrown
     */
    public SessionNotInitializedException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }

    /**
     * Constructs an SessionNotInitializedException with the specified causing
     * Throwable.
     * @param cause the Throwable causing this exception to be thrown
     */
    public SessionNotInitializedException(Throwable cause) {
        super();
        initCause(cause);
    }


    /**
     * Suppresses any attempt to fillInStackTrace.
     * This Exception is often expected.  Suppressing the stack trace reduces
     * the likelihood of this excpetion's presence in a log file being
     * misinterpreted as a genuine exception.
     * Since only the stack trace is suppressed, the {@link #printStackTrace}
     * method may still print the detail message in the exception.
     */
    public Throwable fillInStackTrace() {
        // Do nothing
        return this;
    }
    
}
