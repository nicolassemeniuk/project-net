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

 package net.project.security.login;

/**
 * Provides status code for logging into application.
 */
public class LoginStatusCode implements java.io.Serializable {

    /** The unique id of this status code. */
    private String id = null;

    /**
     * Creates a new LoginStatusCode with the specified id.
     * The id of the status code.
     */
    private LoginStatusCode(String id) {
        this.id = id;
    }

    /**
     * Returns the internal id of this status code.
     * @return the id
     */
    private String getID() {
        return this.id;
    }

    /**
     * Returns the internal id of this status code, but is not guaranteed to
     * do so.
     * @return the string representation of this status code
     */
    public String toString() {
        return getID();
    }

    /**
     * Two status codes are equal if their internal ids are equal.
     * @return true if obj is equal to this status code
     */
    public boolean equals(Object obj) {
        boolean isEqual = false;
        
        if (this == obj) {
            isEqual = true;
        
        } else {
            
            if (obj instanceof LoginStatusCode &&
                ((LoginStatusCode) obj).getID().equals(getID())) {
                
                isEqual = true;
            }
        }

        return isEqual;
    }

    public int hashCode() {
        return getID().hashCode();
    }

    //
    // Constants
    //

    /** Successful login. */
    public static final LoginStatusCode SUCCESS = new LoginStatusCode("100");

    /** Missing parameter for login (such as username or password). */
    public static final LoginStatusCode MISSING_PARAMETER = new LoginStatusCode("200");

    /** Error authenticating user. */
    public static final LoginStatusCode AUTHENTICATION_ERROR = new LoginStatusCode("300");

    /** Error checking license. */
    public static final LoginStatusCode INVALID_LICENSE = new LoginStatusCode("400");

     /** Error Inactive User. */
    public static final LoginStatusCode INACTIVE_USER = new LoginStatusCode("500");

    /** Error Unconfirmed User. */
    public static final LoginStatusCode UNCONFIRMED_USER = new LoginStatusCode("600");

    /** Error Automatic Registration. */
    public static final LoginStatusCode AUTOMATIC_REGISTRATION = new LoginStatusCode("700");


}
