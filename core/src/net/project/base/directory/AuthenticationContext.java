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

 package net.project.base.directory;

import net.project.security.login.LoginContext;

/**
 * An <code>AuthenticationContext</code> represents the information
 * required by any directory provider implementation to authenticate
 * a user with the application.
 *
 * @author Tim
 * @since Gecko Update 3
 */
public class AuthenticationContext implements java.io.Serializable {

    private String username = null;
    private String clearTextPassword = null;
    private String domainID = null;

    /**
     * Creates an <code>AuthenticationContext</code> for the specified
     * username and password.
     * @param domainID the id of the domain against which authentication
     * is occurring; this is necessary as some directory providers
     * may store user profiles for more than one domain in the same
     * underlying directory store; those configured directory providers
     * that maintain users for a single domain only may ignore this parameter
     * @param username the username with which to authenticate; the
     * interpretation of this value is determined by the directory
     * provider implementation
     * @param clearTextPassword the password with which to authenticate;
     */
    public AuthenticationContext(String domainID, String username, String clearTextPassword) {
        setDomainID(domainID);
        setUsername(username);
        setClearTextPassword(clearTextPassword);
    }

    public AuthenticationContext(LoginContext loginContext) {
        setDomainID(loginContext.getDomainID());
        setUsername(loginContext.getUsername());
        setClearTextPassword(loginContext.getClearTextPassword());
    }

    private void setDomainID(String domainID) {
        this.domainID = domainID;
    }

    private void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    private void setClearTextPassword(String clearTextPassword) {
        this.clearTextPassword = clearTextPassword;
    }

    public String getClearTextPassword() {
        return this.clearTextPassword;
    }

    public String getDomainID() {
        return this.domainID;
    }

}
