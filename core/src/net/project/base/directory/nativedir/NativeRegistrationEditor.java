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

import net.project.base.property.PropertyProvider;
import net.project.security.EncryptionManager;

/**
 * Provides editing and validation of a NativeDirectoryEntry during
 * the registration process.
 * Includes the maintenance of a cleartext password and double-entered
 * passwords and email addresses.
 */
public class NativeRegistrationEditor 
        extends NativeDirectoryEntry
        implements net.project.gui.error.IErrorProvider {

    /**
     * The entered password, in cleartext.
     * We must maintain it in cleartext since it supports one-way
     * encryption only and must be redisplayed on the user interface
     * in the event of any errors.
     */
    private String clearTextPassword = null;
    
    /**
     * The password entered again.
     * It is required to be entered twice to ensure it is not mistyped.
     */
    private String passwordRetype = null;
    
    /**
     * The email entered again.
     * It is required to be entered twice to ensure it is not mistyped.
     */
    private String emailRetype = null;

    /**
     * Creates an empty profile editor.
     */
    public NativeRegistrationEditor() {
        // Do nothing
    }


    public NativeRegistrationEditor(NativeDirectoryEntry entry) {
        this();
        setLogin(entry.getLogin());
        setEmail(entry.getEmail());
        setEncryptedPassword(entry.getEncryptedPassword());
        setEncryptedHintPhrase(entry.getEncryptedHintPhrase());
        setEncryptedHintAnswer(entry.getEncryptedHintAnswer());
    
        // Default the current login name to avoid retyping by user
       // setCurrentLogin(getLogin());
        //try {
        //    setNewClearTextHintPhrase(getClearTextHintPhrase());
         //   setNewClearTextHintAnswer(getClearTextHintAnswer());
        
       // } catch (net.project.security.EncryptionException e) {
            // No hint phrase or answer
        // }
    }


    /**
     * Sets the cleartext password for this directory entry.
     * This also updates the encrypted password.
     * @param clearTextPassword the cleartext password
     * @see #getClearTextPassword
     * @see net.project.base.directory.nativedir.NativeDirectoryEntry#getEncryptedPassword
     * @throws net.project.security.EncryptionException if there is
     * a problem encrypting the password
     */
    public void setClearTextPassword(String clearTextPassword) 
            throws net.project.security.EncryptionException {

        if (clearTextPassword == null || clearTextPassword.trim().length() == 0) {
            this.clearTextPassword = null;
            setEncryptedPassword(null);
        
        } else {
            this.clearTextPassword = clearTextPassword;

            // Now encrypt the password
            String encryptedPassword = null;

            try {
                encryptedPassword = encryptPassword(this.clearTextPassword);

            } catch (net.project.security.InvalidPasswordForEncryptionException e) {
                // The password contained illegal characters
                // We're going to set the encrypted password to null
                // This will prevent the password from being accepted later
                // By the validate routine
            }

            setEncryptedPassword(encryptedPassword);
        }

    }

    /**
     * Returns the cleartext password for this directory entry.
     * @return the cleartext password
     * @see #setClearTextPassword
     */
    public String getClearTextPassword() {
        return this.clearTextPassword;
    }

    /**
     * Sets the retyped password.
     * This is cleartext.
     * @param password entered for the second time
     * @see #getPasswordRetype
     */
    public void setPasswordRetype(String password) {
        if (password == null || password.trim().length() == 0) {
            password = null;
        }
        this.passwordRetype = password;
    }

    /**
     * Returns the retyped password.
     * @return the password that was entered for the second time
     * @see #setPasswordRetype
     */
    public String getPasswordRetype() {
        return this.passwordRetype;
    }

    /**
     * Sets the retyped email address.
     * @param email the email address entered for the second time
     * @see #getEmailRetype
     */
    public void setEmailRetype(String email) {
        if (email == null || email.trim().length() == 0) {
            email = null;
        }
        this.emailRetype = email;
    }


    /**
     * Retunrs the retyped email address.
     * @return the email address entered for the second time
     * @see #setEmailRetype
     */
    public String getEmailRetype() {
        return this.emailRetype;
    }


    //
    // Validation Routines
    //

    /**
     * Validates that all mandatory entry values are present and
     * that passwords and email addresses match retyped values.
     * @postcondition {@link #hasErrors} is true if any validation
     * failed
     */
    public void validate() {
        validateLogin();
        validatePassword();
        validateMatchingPassword();
        validateEmail();
        validateMatchingEmail();
        validateClearTextHintPhrase();
        validateClearTextHintAnswer();

    }

    /**
     * Validates that the login value is present.
     */
    public void validateLogin() {
        if (getLogin() == null) {
            this.validationErrors.put("login", "Login is required");
        }
    }

    /**
     * Validates that the password value is present.
     */
    public void validatePassword() {

        if (getClearTextPassword() == null) {
            this.validationErrors.put("password", "Password is required");

        } else if (!EncryptionManager.isValidPasswordCharactersForPBE(getClearTextPassword())) {
            // The password contains invalid characters
            this.validationErrors.put("password", PropertyProvider.get("prm.registration.authorization.native.error.invalidpassword"));

        } else if (getEncryptedPassword() == null) {
            // Encrypted password was not set, yet the clear text password
            // was and the password contained all valid characters
            // This is a totally unexpected problem
            this.validationErrors.put("password", "The password you entered could not be encrypted.  Please try another password.");

        }


    }

    /**
     * Validates that both passwords match including by case.
     */
    public void validateMatchingPassword() {
        if (getClearTextPassword() != null && !getClearTextPassword().equals(getPasswordRetype())) {
            this.validationErrors.put("password2", "Entered passwords do not match");
        }

    }

    /**
     * Validates that the email address value is present and is
     * valid.
     * @see net.project.notification.Email#isValidInternetAddress
     */
    public void validateEmail() {

        if (getEmail() == null) {
            this.validationErrors.put("email", "Email address is required");
        
        } else if (!net.project.notification.Email.isValidInternetAddress(getEmail())) {
            this.validationErrors.put("email", "Invalid email address");
        
        }

    }
    
    /**
     * Validates that both email address match.
     * Comparison ignores case since email addresses are case insensitive.
     */
    public void validateMatchingEmail() {

        if (getEmail() != null &&
            !getEmail().equalsIgnoreCase(getEmailRetype())) {
            
            this.validationErrors.put("email2", "Entered email addresses do not match");
        }

    }

    /**
     * Validates that the hint phrase is present and decryptable.
     */
    public void validateClearTextHintPhrase() {

        try {
            if (getClearTextHintPhrase() == null) {
                this.validationErrors.put("clearTextHintPhrase", "Jog Question is required");
            }
        
        } catch (net.project.security.EncryptionException e) {
            this.validationErrors.put("clearTextHintPhrase", "Unable to encrypt Jog Question");
        
        }

    }

    /**
     * Validates that the hint answer is present and decryptable.
     */
    public void validateClearTextHintAnswer() {

        try {
            if (getClearTextHintAnswer() == null) {
                this.validationErrors.put("clearTextHintAnswer", "Jog Answer is required");
            }
        
        } catch (net.project.security.EncryptionException e) {
            this.validationErrors.put("clearTextHintAnswer", "Unable to encrypt Jog Answer");

        }

    }


    //
    // Implementing IErrorProvider
    //
 
    private net.project.gui.error.ValidationErrors validationErrors = new net.project.gui.error.ValidationErrors();
 
    /**
     * Clears all errors.
     */
    public void clearErrors() {
        validationErrors.clearErrors();
    }
 
   /**
    * Indicates whether there are any errors
    * @return true if there are errors; false otherwise
    */
    public boolean hasErrors() {
        return validationErrors.hasErrors();
    }
 
    /**
     * Gets the Error Flag for the Field.  This method is used for
     * flagging a field label as having an error.  If an error is present
     * for the field with the specified id, the specified label is returned
     * but formatted to indicate there is an error.  Currently this uses
     * a &lt;span&gt;&lt;/span&gt; tag to specify a CSS class.  If there is no error
     * for the field with the specified id, the label is returned untouched.
     * @param fieldID the id of the field which may have the error
     * @param label the label to modify to indicate there is an error
     * @return the HTML formatted label
     */
    public String getFlagError(String fieldID, String label) {
        return validationErrors.getFlagErrorHTML(fieldID, label);
    }
 
    /**
     * Gets the Error Message for the Field.
     * @param fieldID  the id of the field for which to get the error message
     * @return the HTML formatted error message
     */
    public String getErrorMessage(String fieldID) {
        return validationErrors.getErrorMessageHTML(fieldID);
    }
 
    /**
     * Gets the Error Message for the Field
     * @return HTML formatted error messages
     */
    public String getAllErrorMessages() {
        return validationErrors.getAllErrorMessagesHTML();
    }
 
    //
    // End of IErrorProvider
    //

}
