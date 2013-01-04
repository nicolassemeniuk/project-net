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
package net.project.portfolio.view;

/**
 * An exception that indicates a problem occurred manipulating a view.
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class ViewException extends net.project.base.PnetException {

    /**
     * Creates an empty ViewException.
     */
    public ViewException() {
        super();
    }

    /**
     * Creates an ViewException with the specified message.
     * @param message the message
     */
    public ViewException(String message) {
        super(message);
    }

    /**
     * Creates an ViewException with the specified message
     * indicating the throwable that caused this exception.
     * @param message the message
     * @param cause the cause of the exception
     */
    public ViewException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an ViewException with no message
     * indicating the throwable that caused this exception.
     * @param cause
     */
    public ViewException(Throwable cause) {
        super(cause);
    }

}

