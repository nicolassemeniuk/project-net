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
package net.project.security;

import java.util.Collections;
import java.util.List;

import net.project.base.PnetRuntimeException;
import net.project.space.Space;

/**
 * Adds additional handling for AuthorizationFailedExceptions.
 * The space for which authorization failed may be specified;  this allows
 * for better display of space access errors.
 */
public class AuthorizationFailedException extends PnetRuntimeException {
    
    /** The space for which authorization failed. */
    private Space space;

    /** Id(s) that failed. */
    private List failedIDList = null;


    /**
     * Constructs an empty AuthorizationFailedException.
     */
    public AuthorizationFailedException() {
        super();
    }


    /**
     * Constructs an AuthorizationFailedException with the specified message.
     * @param message the message
     */
    public AuthorizationFailedException(String message) {
        super(message);
    }

    /**
     * Constructs an AuthorizationFailedException with the specified message
     * and causing Throwable.
     * @param message the message
     * @param cause the Throwable causing this exception to be thrown
     */
    public AuthorizationFailedException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }

    /**
     * Constructs an AuthorizationFailedException with the specified causing
     * Throwable.
     * @param cause the Throwable causing this exception to be thrown
     */
    public AuthorizationFailedException(Throwable cause) {
        super();
        initCause(cause);
    }

    /**
     * Constructs an AuthorizationFailedException with the specified message,
     * indicating that authorization failed in the specified space.
     * @param message the message
     * @param space the space in which the error occurred
     */
    public AuthorizationFailedException(String message, Space space) {
        super(message);
        setSpace(space);
    }

    /**
     * Constructs and AuthorizationFailedException with the specified message,
     * also indicating the id(s) that failed.
     *
     * @param message a <code>String</code> containing the message that
     * describes the cause for the failure.
     * @param idsThatFailed a <code>List</code> containing the id's that failed.
     */
    public AuthorizationFailedException(String message, List idsThatFailed) {
        super(message);
        setFailedIDList(idsThatFailed);
    }

    /**
     * Sets the space in which authorization failed.  This is used most often
     * when switching spaces;  the current space is not actually the space
     * in which authorization failed.
     * @param space the space
     * @see #getSpace
     */
    public void setSpace(Space space) {
        this.space = space;
    }


    /**
     * Returns the space in which authorization failed.
     * @return the space
     * @see #setSpace
     */
    public Space getSpace() {
        return this.space;
    }

    /**
     * Get the list of ID's that failed during the security check.  Currently,
     * this only works for multi-item security checks.
     *
     * @return a <code>List</code> containing one or more Strings that are the
     * id's of objects that didn't pass the security check.
     */
    public List getFailedIDList() {
        if (failedIDList == null) {
            return Collections.EMPTY_LIST;
        } else {
            return failedIDList;
        }
    }

    /**
     * Set the list of id's that failed the security check.
     *
     * @param failedIDList a <code>List</code> containing zero or more
     * <code>String</code> objects which are the ID's that failed the security
     * check.
     */
    void setFailedIDList(List failedIDList) {
        this.failedIDList = failedIDList;
    }

}
