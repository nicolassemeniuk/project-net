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
 * Provides editing and validation of a NativeDirectoryEntry when
 * modifying a user's profile.
 * Modifying the profile requires the current username and password
 * to be entered and correct to allow update.
 */
public class NativeProfileEditor 
        extends NativeDirectoryEntry
        implements net.project.gui.error.IErrorProvider {

    /**
     * Indicates whether the user wishes to change their username.
     */
    private boolean isChangeUsername = false;

    /**
     * Indicates whether the user wishes to change their password.
     */
    private boolean isChangePassword = false;

    /**
     * Indicates whether the user wishes to change their jog question
     * or answer.
     */
    private boolean isChangeHints = false;

    /**
     * The current username; this is required for validation to succeed.
     */
    private String currentUsername = null;

    /**
     * The current encrypted password; this is required for validation
     * to succeed.
     */
     private String currentEncryptedPassword = null;

    /**
     * The new login.
     */
    private String newUsername = null; 

    /**
     * The new password, in cleartext.
     */
    private String newClearTextPassword = null;

    /**
     * The new password entered again.
     * It is required to be entered twice to ensure it is not mistyped.
     */
    private String newPasswordRetype = null;
    
    /**
     * The new jog question.
     */
    private String newClearTextHintPhrase = null;

    /**
     * The new jog answer.
     */
    private String newClearTextHintAnswer = null;

    /**
     * Indicates that an administrator is updating the profile.
     * This affects validation.
     */
    private boolean isAdministrativeUpdate = false;
     

    /**
     * Creates an empty profile editor.
     */
    public NativeProfileEditor() {
        // Do nothing
    }

    /**
     * Creates a profile editor based on the specified directory entry.
     * Copies all values into this object.
     * @param entry the directory entry from which to populate this
     * profile editor
     */
    public NativeProfileEditor(NativeDirectoryEntry entry) {
        this();
        setLogin(entry.getLogin());
        setEmail(entry.getEmail());
        setEncryptedPassword(entry.getEncryptedPassword());
        setEncryptedHintPhrase(entry.getEncryptedHintPhrase());
        setEncryptedHintAnswer(entry.getEncryptedHintAnswer());
    
        // Default the current login name to avoid retyping by user
        setCurrentLogin(getLogin());
        try {
            setNewClearTextHintPhrase(getClearTextHintPhrase());
            setNewClearTextHintAnswer(getClearTextHintAnswer());
        
        } catch (net.project.security.EncryptionException e) {
            // No hint phrase or answer

        }
    }

    /**
     * Specifies whether the user wishes to change their login name.
     * This affects the validity checks.
     * @param isChangeUsername true if the user wishes to change their login name;
     * false if not
     * @see #isChangeLogin
     * @see #validateNewLogin
     */
    public void setChangeLogin(boolean isChangeUsername) {
        this.isChangeUsername = isChangeUsername;
    }

    /**
     * Indicates whether the user wishes to change their login name.
     * @return true if the user wishes to change their login name;
     * false otherwise
     * @see #setChangeLogin
     */
    public boolean isChangeLogin() {
        return this.isChangeUsername;
    }

    /**
     * Specifies whether the user wishes to change their password.
     * This affects the validity checks.
     * @param isChangePassword true if the user wishes to change their password;
     * false if not
     * @see #isChangePassword
     * @see #validateNewPassword
     */
    public void setChangePassword(boolean isChangePassword) {
        this.isChangePassword = isChangePassword;
    }

    /**
     * Indicates whether the user wishes to change their password.
     * @return true if the user wishes to change their password;
     * false otherwise
     * @see #setChangePassword
     */
    public boolean isChangePassword() {
        return this.isChangePassword;
    }

    /**
     * Specifies whether the user wishes to change their jog question or answer.
     * This affects the validity checks.
     * @param isChangeHints true if the user wishes to change their jog question or answer;
     * false if not
     * @see #isChangeHints
     * @see #validateClearTextHintPhrase
     * @see #validateClearTextHintAnswer
     */
    public void setChangeHints(boolean isChangeHints) {
        this.isChangeHints = isChangeHints;
    }

    /**
     * Indicates whether the user wishes to change their jog question or answer.
     * @return true if the user wishes to change their jog question or answer;
     * false otherwise
     * @see #setChangeHints
     */
    public boolean isChangeHints() {
        return this.isChangeHints;
    }

    /**
     * Sets the current login name.
     * This is required to match the directory entry login name.
     * @param currentUsername the current login name
     * @see #getCurrentLogin
     */
    public void setCurrentLogin(String currentUsername) {
        this.currentUsername = currentUsername;
    }

    /**
     * Returns the current login name.
     * @return the current login name
     * @see #setCurrentLogin
     */
    public String getCurrentLogin() {
        return this.currentUsername;
    }


    /**
     * Sets the current cleartext password.
     * This is always stored in encrypted form.  It is required
     * to match the directory entry password.
     * @param currentPassword the current cleartext password
     * @throws net.project.security.EncryptionException if there is
     * a problem encrypting the password
     */
    public void setCurrentClearTextPassword(String currentPassword) 
            throws net.project.security.EncryptionException {

        if (currentPassword == null || currentPassword.trim().length() == 0) {
            setCurrentEncryptedPassword(null);
        
        } else {
            setCurrentEncryptedPassword(encryptPassword(currentPassword));

        }
    }

    /**
     * Sets the current encrypted password.
     * @param encryptedPassword the current encrypted password
     * @see #setCurrentClearTextPassword
     */
    private void setCurrentEncryptedPassword(String encryptedPassword) {
        this.currentEncryptedPassword = encryptedPassword;
    }

    /**
     * Returns the current encrypted password.
     * @return the current encrypted password
     */
    public String getCurrentEncryptedPassword() {
        return this.currentEncryptedPassword;
    }


    /**
     * Sets the new login name to change this directory entry's login
     * name to.
     * @param newUsername the login name to change to
     * @see #getNewLogin
     */
    public void setNewLogin(String newUsername) {
        this.newUsername = newUsername;
    }

    /**
     * Returns the new login name to which the user wants to change.
     * @return the new login name
     * @see #setNewLogin
     */
    public String getNewLogin() {
        return this.newUsername;
    }

    /**
     * Sets the new password.
     * This is cleartext.  Currently we support only the following characters:
     * <code><pre> !"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~</pre></code>
     * @param password the new password
     * @see #getNewPassword
     */
    public void setNewPassword(String password) {
        if (password == null || password.trim().length() == 0) {
            password = null;
        }
        this.newClearTextPassword = password;
    }

    /**
     * Returns the new password.
     * @return the new password that was entered
     * @see #setNewPassword
     */
    public String getNewPassword() {
        return this.newClearTextPassword;
    }

    /**
     * Sets the new retyped password.
     * This is cleartext.
     * @param password the new entered for the second time
     * @see #getNewPasswordRetype
     */
    public void setNewPasswordRetype(String password) {
        if (password == null || password.trim().length() == 0) {
            password = null;
        }
        this.newPasswordRetype = password;
    }

    /**
     * Returns the new retyped password.
     * @return the new password that was entered for the second time
     * @see #setNewPasswordRetype
     */
    public String getNewPasswordRetype() {
        return this.newPasswordRetype;
    }

    /**
     * Sets the new cleartext hint phrase.
     * @param newClearTextHintPhrase the hint phrase
     */
    public void setNewClearTextHintPhrase(String newClearTextHintPhrase) {
        this.newClearTextHintPhrase = newClearTextHintPhrase;
    }

    /**
     * Returns the new cleatext hint phrase.
     * @return the new cleartext hint phrase
     */
    public String getNewClearTextHintPhrase() {
        return this.newClearTextHintPhrase;
    }

    /**
     * Sets the new cleatext hint answer.
     * @param newClearTextHintAnswer the hint answer
     */
    public void setNewClearTextHintAnswer(String newClearTextHintAnswer) {
        this.newClearTextHintAnswer = newClearTextHintAnswer;
    }

    /**
     * Returns the new cleatext hint answer.
     * @return the new cleartext hint answer
     */
    public String getNewClearTextHintAnswer() {
        return this.newClearTextHintAnswer;
    }

    /**
     * Specifies whether the update to profile is by an administrator.
     * This affects validation checks; current login and password
     * are not required.
     * @param isAdministrativeUpdate true if administrator is updating;
     * false if user is updating own profile
     * @see #isAdministrativeUpdate
     */
    public void setAdministrativeUpdate(boolean isAdministrativeUpdate) {
        this.isAdministrativeUpdate = isAdministrativeUpdate;
    }

    /**
     * Indicates whether the update to profile is by an administrator.
     * @return true if administrator is updating; false otheriwse
     * @see #setAdministrativeUpdate
     */
    public boolean isAdministrativeUpdate() {
        return this.isAdministrativeUpdate;
    }

    /**
     * Displays contents of this entry.
     * For debugging purposes only.
     * @return all properties
     */
    public String toString() {
        StringBuffer str = new StringBuffer(super.toString()).append("\n");
        str.append("isChangeLogin: " + isChangeLogin()).append("\n");
        str.append("isChangePassword: " + isChangePassword()).append("\n");
        str.append("isChangeHints: " + isChangeHints()).append("\n");
        str.append("currentLogin: ").append(getCurrentLogin()).append("\n");
        str.append("currentEncryptedPassword: ").append(getCurrentEncryptedPassword()).append("\n");
        str.append("newLogin: ").append(getNewLogin()).append("\n");
        str.append("newPassword: ").append(getNewPassword()).append("\n");
        str.append("newPasswordRetype: ").append(getNewPasswordRetype()).append("\n");
        str.append("newClearTextHintPhrase: ").append(getNewClearTextHintPhrase()).append("\n");
        str.append("newClearTextHintAnswer: ").append(getNewClearTextHintAnswer()).append("\n");
        str.append("isAdministrativeUpdate: " + isAdministrativeUpdate());
        return str.toString();
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
        validateLoginAndPassword();
        
        validateNewLogin();
        validateNewPassword();
        validateNewPasswordRetype();
        validateMatchingNewPassword();
        validateClearTextHintPhrase();
        validateClearTextHintAnswer();

    }

    /**
     * Validates that the current login and password are present
     * and match the existing login name and password.
     * Always succeeds if {@link #isAdministrativeUpdate} is true.
     */
    public void validateLoginAndPassword() {
        
        if (!isAdministrativeUpdate()) {
            // Mandatory for non-admin updates
            // Must have matching login name and password

            if (getCurrentLogin() == null || 
                getCurrentEncryptedPassword() == null ||
                !getCurrentLogin().equalsIgnoreCase(getLogin()) ||
                !getCurrentEncryptedPassword().equalsIgnoreCase(getEncryptedPassword()) ) {
                this.validationErrors.put("login", PropertyProvider.get("prm.directory.provider.type.native.profile.incorrectloginnameorpassword.message"));
            }
        
        }


    }


    /**
     * Validates that the new login value is present if the user wishes
     * to change their login.
     * @see #isChangeLogin
     */
    public void validateNewLogin() {
        
        if (isChangeLogin()) {
            
            if (getNewLogin() == null) {
                this.validationErrors.put("login", "New Login is required");
            }
        
        }
    
    }

    /**
     * Validates that the new password value is present if the user
     * wishes to change their password.
     * @see #isChangePassword
     */
    public void validateNewPassword() {
        
        if (isChangePassword()) {
            // Only validate if user is changing password

            if (getNewPassword() == null) {
                this.validationErrors.put("newPassword", "New Password is required");

            } else if (!EncryptionManager.isValidPasswordCharactersForPBE(getNewPassword())) {
                // The password contains invalid characters
                this.validationErrors.put("newPassword", PropertyProvider.get("prm.directory.provider.type.native.profile.invalidpassword.message"));

            }

        }
    }

    /**
     * Validates that the new password retyped value is present if the user
     * wishes to change their password.
     * @see #isChangePassword
     */
    public void validateNewPasswordRetype() {
        
        if (isChangePassword()) {
            // Only validate if user is changing password

            if (getNewPasswordRetype() == null) {
                this.validationErrors.put("newPassword2", "New Password is required");
            }
        
        }
    }

    /**
     * Validates that both new passwords match including by case if
     * the user wishes to change their password.
     * @see #isChangePassword
     */
    public void validateMatchingNewPassword() {
        
        if (isChangePassword()) {
            
            if (getNewPassword() != null && !getNewPassword().equals(getNewPasswordRetype())) {
                this.validationErrors.put("newPassword2", "Entered passwords do not match");
            }

        }

    }

    /**
     * Validates that the hint phrase is present and decryptable
     * if the user wishes to change their hint phrase or answer.
     * @see #isChangeHints
     */
    public void validateClearTextHintPhrase() {

        if (isChangeHints()) {
            
            if (getNewClearTextHintPhrase() == null) {
                this.validationErrors.put("clearTextHintPhrase", "Jog Question is required");
            }

        
        }

    }

    /**
     * Validates that the hint answer is present and decryptable
     * if the user wishes to change their hint phrase or answer.
     * @see #isChangeHints
     */
    public void validateClearTextHintAnswer() {

        if (isChangeHints()) {

            if (getNewClearTextHintAnswer() == null) {
                this.validationErrors.put("clearTextHintAnswer", "Jog Answer is required");
            }

        }
    }

    /**
     * Updates the entry to new values.
     * Values are updated only if the appropriate flags are set.
     * This is necessary to avoid overwriting the current entry
     * until all validation has succeeded.
     * @throws net.project.security.InvalidPasswordForEncryptionException if
     * the new password contains invalid characters
     * @throws net.project.security.EncryptionException if there is a problem encrypting
     * the new hint phrase or answer
     */
    public void approveChanges() 
            throws net.project.security.InvalidPasswordForEncryptionException, net.project.security.EncryptionException {

        if (isChangeLogin()) {
            setLogin(getNewLogin());
        }

        if (isChangePassword()) {
            setEncryptedPassword(encryptPassword(getNewPassword()));
        }

        if (isChangeHints()) {
            setClearTextHintPhrase(getNewClearTextHintPhrase());
            setClearTextHintAnswer(getNewClearTextHintAnswer());
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
