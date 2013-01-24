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

 package net.project.base.mvc;

import net.project.base.PnetException;

/**
 * Provides an exception indicating that some problem occurred in producing
 * a view.
 *
 * @author Tim Morrow
 * @since Version 7.7.0
 */
public class ViewException extends PnetException {

    /**
     * Creates a new ViewException with the specified message.
     * @param message the message indicating the problem that occurred
     */
    public ViewException(String message) {
        super(message);
    }

    /**
     * Creates a new ViewException with the specified message and
     * causing throwable
     * @param message the message indicating the problem that occurred
     * @param cause the causing throwable
     */
    public ViewException(String message, Throwable cause) {
        super(message, cause);
    }

}
