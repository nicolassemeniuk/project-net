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

import net.project.util.ParseString;

import org.apache.log4j.Logger;

/**
 * Provides a structure for capturing the required parameters for login
 * and validating them.
 */
public class LoginContext implements java.io.Serializable {

    /** The username used to login. */
    private String username = null;

    /** The clearText password. */
    private String clearTextPassword = null;

    /** The userDomain used to login. */
    private String domainID = null;

    /**
     * Creates a new LoginContext from the specified login parameters.
     * The password is always stored in an encrypted fashion.
     * @param username the username
     * @param clearTextPassword the password, in cleartext
     * @param domainID the id of the authentication domain
     */
    LoginContext(String username, String clearTextPassword, String domainID) {
        setUsername(username);
        setClearTextPassword(clearTextPassword);
        setDomainID(domainID);
    }

    /**
     * Sets the username for login purposes.
     * @param username the username
     * @see #getUsername
     */
    private void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the username for login purposes.
     * @return the username
     * @see #setUsername
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets the clear text password for login purposes.
     * The password must be maintained in clear text since it
     * must be provided to most Directory service providers in
     * clear text; each directory service may utilize a different
     * encryption algorithm.
     * @param clearTextPassword the clear text password
     */
    private void setClearTextPassword(String clearTextPassword) {
        this.clearTextPassword = clearTextPassword;
    
    }

    /**
     * Returns the clearText password specified for login.
     * @return the clearText password
     */
    public String getClearTextPassword() {
        return this.clearTextPassword;
    }

    /**
     * Returns the encrypted password for login purposes.
     * @return the encrypted password or null if the the password was not
     * encrypted successfully
     * @see #setClearTextPassword
     */
    public String getEncryptedPassword() {
        String encryptedPassword = null;

        try {
            encryptedPassword = net.project.security.EncryptionManager.pbeEncrypt(getClearTextPassword());
        
        } catch (net.project.security.EncryptionException ee) {
        	Logger.getLogger(LoginContext.class).error("LoginContext.setPassword(): Unable to encrypt password: " + ee);
            encryptedPassword = null;
        }

        return encryptedPassword;
    }

    /**
     * Sets the domainID for login purposes.
     * @param domainID the domainID
     * @see #getDomainID
     */
    private void setDomainID(String domainID) {
        this.domainID = domainID;
    }

    /**
     * Returns the domainID for login purposes.
     * @return the domainID
     * @see #setDomainID
     */
    public String getDomainID() {
        return this.domainID;
    }

    /**
     * Checks that mandatory parameters have been specified.
     * @return true if mandatory parameters are all specified; false if any
     * are missing
     */
    boolean validateMandatoryParameters() {
        boolean isValid = false;

        if (ParseString.isEmpty(getUsername()) || 
            ParseString.isEmpty(getClearTextPassword()) ||
            ParseString.isEmpty(getDomainID()) ) {
        
            isValid = false;
        
        } else {
            isValid = true;
        }
    
        return isValid;
    }

}
