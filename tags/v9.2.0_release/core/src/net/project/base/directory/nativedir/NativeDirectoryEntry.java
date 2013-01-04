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
package net.project.base.directory.nativedir;


/**
 * Provides properties of an entry in the native directory.
 */
public class NativeDirectoryEntry implements net.project.base.directory.IDirectoryEntry {

    /**
     * The login name (username).
     */
    private String login = null;

    /**
     * The encrypted password.
     */
    private String encryptedPassword = null;

    /**
     * The email address.
     */
    private String email = null;

    /**
     * The encrypted hint phrase (question).
     */
    private String encryptedHintPhrase = null;

    /**
     * The encrypted hint answer (resposne).
     */
    private String encryptedHintAnswer = null;


    /**
     * Sets the login name for this directory entry.
     * @param the login name to be stored in the native directory
     * @see #getLogin
     */
    public void setLogin(String login) {
        if (login == null || login.trim().length() == 0) {
            login = null;
        }
        this.login = login;
    }

    /**
     * Returns the login name (username).
     * @return the login name
     * @see #setLogin
     */
    public String getLogin() {
        return this.login;
    }

    //
    // Password stuff
    // Note: Cleartext password facilities not provided since
    //       that is a presentation issue and only needed for
    //       capturing the password for the first time

    /**
     * Sets the encrypted password for this native directory entry.
     * @param encryptedPassword the password using the application
     * standard PBE; null value allowed
     * @see #getEncryptedPassword
     */
    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    /**
     * Returns the current encrypted password.
     * @return the encrypted password
     * @see #setEncryptedPassword
     */
    protected String getEncryptedPassword() {
        return this.encryptedPassword;
    }

    /**
     * Encrypts the specified password using the application standard
     * PBE algorithm.
     * @param clearTextPassword the cleartext password to encrypt;
     * @return the encrypted password
     * @throws NullPointerException if the specified password is null
     * @throws net.project.security.EncryptionException if there is a problem encrypting
     */
    protected String encryptPassword(String clearTextPassword)
            throws net.project.security.EncryptionException {

        if (clearTextPassword == null) {
            // Password is mandatory
            throw new NullPointerException("clearTextPassword is null");
        }

        return net.project.security.EncryptionManager.pbeEncrypt(clearTextPassword);
    }

    //
    // End of password stuff
    //

    /**
     * Sets the email address for this entry.
     * @param email the email address; this should be a regular address
     * of the form <code>name@hostname.com</code>; null values are
     * allowed
     * @see #getEmail
     */
    public void setEmail(String email) {
        if (email == null || email.trim().length() == 0) {
            email = null;
        }
        this.email = email;
    }

    /**
     * Returns the email address for this entry.
     * @return the email address
     * @see #setEmail
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the cleartext hint phrase.
     * This is always maintained in encrypted form since it supports
     * two-way encryption and decryption.
     * @param clearTextHintPhrase the unencrypted hint phrase (question);
     * null values are permitted
     * @throws net.project.security.EncryptionException if there is a problem encrypting
     * the hint phrase
     */
    public void setClearTextHintPhrase(String clearTextHintPhrase)
            throws net.project.security.EncryptionException {
            
        if (clearTextHintPhrase == null || clearTextHintPhrase.trim().length() == 0) {
            // Don't attempt to encrypt null or empty values
            setEncryptedHintPhrase(null);
        
        } else {
            // Use blowfish encryption with standard key
            setEncryptedHintPhrase(net.project.security.EncryptionManager.encryptBlowfish(clearTextHintPhrase.trim()));

        }
    
    }

    /**
     * Returns the cleartext hint phrase.
     * @return the unencrypted hint phrase or null if the encrypted
     * hint phrase is null
     * @throws net.project.security.EncryptionException if there is a problem decrypting
     * the hint phrase
     */
    public String getClearTextHintPhrase()
            throws net.project.security.EncryptionException {
        
        String clearTextHintPhrase = null;

        if (this.encryptedHintPhrase != null) {
            // Use blowfish decruption with standard key
            clearTextHintPhrase = net.project.security.EncryptionManager.decryptBlowfish(getEncryptedHintPhrase());

        }

        return clearTextHintPhrase;
    }

    /**
     * Sets the encrypted hint phrase.
     * @param encryptedHintPhrase the encrypted hint phrase
     * @see #getEncryptedHintPhrase
     */
    void setEncryptedHintPhrase(String encryptedHintPhrase) {
        this.encryptedHintPhrase = encryptedHintPhrase;
    }

    /**
     * Returns the encrypted hint phrase.
     * @return the encrypted hint phrase
     * @see #setEncryptedHintPhrase
     */
    String getEncryptedHintPhrase() {
        return this.encryptedHintPhrase;
    }


    /**
     * Sets the cleartext hint answer.
     * This is always maintained in encrypted form since it supports
     * two-way encryption and decryption.
     * @param clearTextHintAnswer the unencrypted hint answer (response);
     * null values are permitted
     * @throws net.project.security.EncryptionException if there is a problem encrypting
     * the hint answer
     */
    public void setClearTextHintAnswer(String clearTextHintAnswer)
            throws net.project.security.EncryptionException {
            
        if (clearTextHintAnswer == null || clearTextHintAnswer.trim().length() == 0) {
            // Don't attempt to encrypt null or empty values
            setEncryptedHintAnswer(null);
        
        } else {
            // Use blowfish encryption with standard key
            setEncryptedHintAnswer(net.project.security.EncryptionManager.encryptBlowfish(clearTextHintAnswer.trim()));

        }
    
    }

    /**
     * Returns the cleartext hint answer.
     * @return the unencrypted hint answer or null if the encrypted
     * hint answer is null
     * @throws net.project.security.EncryptionException if there is a problem decrypting
     * the hint answer
     */
    public String getClearTextHintAnswer()
            throws net.project.security.EncryptionException {
        
        String clearTextHintAnswer = null;

        if (this.encryptedHintAnswer != null) {
            // Use blowfish decruption with standard key
            clearTextHintAnswer = net.project.security.EncryptionManager.decryptBlowfish(getEncryptedHintAnswer());

        }

        return clearTextHintAnswer;
    }

    /**
     * Sets the encrypted hint answer.
     * @param encryptedHintAnswer the encrypted hint answer
     * @see #getEncryptedHintAnswer
     */
    void setEncryptedHintAnswer(String encryptedHintAnswer) {
        this.encryptedHintAnswer = encryptedHintAnswer;
    }

    /**
     * Returns the encrypted hint answer.
     * @return the encrypted hint answer
     * @see #setEncryptedHintAnswer
     */
    String getEncryptedHintAnswer() {
        return this.encryptedHintAnswer;
    }


    /**
     * Displays contents of this entry.
     * For debugging purposes only.
     * @return all properties
     */
    public String toString() {
        StringBuffer str = new StringBuffer(super.toString()).append("\n");
        str.append("login: ").append(getLogin()).append("\n");
        str.append("email: ").append(getEmail()).append("\n");
        str.append("encryptedPassword: ").append(getEncryptedPassword()).append("\n");
        try {
            str.append("clearTextHintPhrase: ").append(getClearTextHintPhrase()).append("\n");
            str.append("clearTextHintAnswer: ").append(getClearTextHintAnswer());
        } catch (net.project.security.EncryptionException e) {
            // No hint phrase or answer
        }
        return str.toString();
    }

    //
    // Implementing IDirectoryEntry
    //

    /**
     * Indicates whether the profile attribute with the specified
     * ID is provided by this directory entry.
     * Currently returns false always.
     * @param attributeID the attribute to check for
     * @return true if this directory entry provides a value for
     * the specified attribute; false otherwise
     */
    public boolean isAttributeProvided(String attributeID) {
        return false;
    }


    /**
     * Returns this directory entry's value for the specified
     * attribute.
     * Current returns empty string always.
     * @param attributeID the attribute value to get
     * @return the value for that attribute; returns the empty
     * string if this directory entry does not provide the
     * specified attribute
     */
    public String getAttributeValue(String attributeID) {
        return "";
    }

    //
    // End IDirectoryEntry
    //

}
