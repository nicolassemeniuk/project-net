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

import net.project.admin.RegistrationBean;
import net.project.persistence.PersistenceException;

/**
 * Provides facilities for helping with a forgotten login or password.
 * <p>
 * Typical use: <pre><code>
 * LoginPasswordHelper helper = new LoginpasswordHelper();
 * helper.setDomainID(...);
 * helper.loadUserForEmail(...);
 * if (helper.isUserFound) {
 *     user = getUser();
 *     user.getHintPhrase();
 *     ...
 * }
 * <br>
 * if (user.isMatching(...)) {
 *     // proceeed
 * }
 * </code></pre>
 * </p>
 */
public class LoginPasswordHelper {

    /** The current domain. */
    private String domainID = null;

    /**
     * The loaded User for the specified email address.
     */
    private LoginPasswordHelperUser helperUser = null;

    /**
     * Indicates whether a user was found when loaded for an email address.
     */
    private boolean isUserFound = false;

    /**
     * Creates an empty LoginPasswordHelper.
     */
    public LoginPasswordHelper() {
        // Do nothing
    }

    /**
     * Specifies the domain id for the domain against which the
     * user has registered.
     * @param domainID the id of the domain
     */
    public void setDomainID(String domainID) {
        this.domainID = domainID;
    }

    /**
     * Loads a user for the specified email address.
     * <p>
     * <b>Precondition:</b> domainID has been set
     * </p>
     * <p>
     * <b>Postcondition:</b> sets <code>isUserFound</code>; if
     * true, a user is available with <code>getUser</code>; if false
     * no user is available
     * </p>
     * @param emailAddress the email address of the user to load
     */
    public void loadUserForEmail(String emailAddress) 
            throws PersistenceException {

        clearUser();

        RegistrationBean registration = new RegistrationBean();
        registration.loadForEmail(emailAddress);

        if (!(registration.isLoaded() && registration.getUserDomainID().equals(this.domainID))) {
            // User is not loaded (meaning unknown email address)
            // or domain id does not match current domain id
            // Then we didn't find the right user
            this.isUserFound = false;

        } else {
            // User is loaded
            this.helperUser = new LoginPasswordHelperUser(registration);
            this.isUserFound = true;
        }

    }

    /**
     * Clears out existing loaded user.
     */
    private void clearUser() {
        this.isUserFound = false;
        this.helperUser = null;
    }

    /**
     * Returns the loaded user.
     * Only valid if <code>isUserFound</code> is true.
     * @return the user or null if no user was found
     * @see #isUserFound
     */
    public LoginPasswordHelperUser getUser() {
        return this.helperUser;
    }


    /**
     * Indicates whether a user was found with the specified email
     * address.
     * @return true if a user was found; false if the <code>loadUserForEmail</code>
     * method has not been called or no user was found
     * @see #loadUserForEmail
     */
    public boolean isUserFound() {
        return this.isUserFound;
    }


    //
    // Nested top-level classes
    //

    /**
     * Provides a view of a User that is limited to the attributes
     * needed to capture and check forgotten login and password information.
     */
    public static class LoginPasswordHelperUser {

        RegistrationBean registration = null;
        NativeDirectoryEntry entry  = null;

        /**
         * Creates a LoginPasswordHelperUser from the specified registration user.
         * @param registration the user
         */
        public LoginPasswordHelperUser(RegistrationBean registration) {
            this.registration = registration;
            this.entry = (NativeDirectoryEntry) this.registration.getDirectoryEntry();
        }

        /**
         * Returns the registration user that this LoginPasswordHelperUser
         * represents.
         * @return the registration user
         */
        public RegistrationBean getUser() {
            return this.registration;
        }

        /**
         * Returns the user's login name.
         * @return the login name
         */
        public String getLogin() {
            return this.registration.getLogin();
        }

        /**
         * Returns the user's email address.
         * @return the email address
         */
        public String getEmail() {
            return this.registration.getEmail();
        }

        /**
         * Returns the user's first name.
         * @return the first name
         */
        public String getFirstName() {
            return this.registration.getFirstName();
        }

        /**
         * Returns the user's last name.
         * @return the last name
         */
        public String getLastName() {
            return this.registration.getLastName();
        }

        /**
         * Returns the clear text hint phrase.
         * @return the hint phrase
         */
        public String getCleartextHintPhrase() 
                throws net.project.security.EncryptionException {

            return this.entry.getClearTextHintPhrase();
        }

        /**
         * Returns the clear text hint answer.
         * @return the hint answer.
         */
        public String getCleartextHintAnswer()
            throws net.project.security.EncryptionException {

            return this.entry.getClearTextHintAnswer();
        }

        /**
         * Indicates whether the specified first name, last name and hint answer
         * match the current user's values.
         * All comparisons are case insensitive and parameters are <code>trimmed</code> prior
         * to comparing.
         * @param firstName the first name to check
         * @param lastName the last name to check
         * @param hintAnswer the hint answer to check
         * @return true if all the specified parameters match this user's
         * values, ignoring case; false otherwise
         */
        public boolean isMatching(String firstName, String lastName, String hintAnswer) {
            
            boolean isMatching = true;

            try {
                isMatching = isMatching && (firstName != null && getFirstName().trim().equalsIgnoreCase(firstName.trim()));
                isMatching = isMatching && (lastName != null && getLastName().trim().equalsIgnoreCase(lastName.trim()));
                isMatching = isMatching && (hintAnswer != null && getCleartextHintAnswer().trim().equalsIgnoreCase(hintAnswer.trim()));
            
            } catch (net.project.security.EncryptionException e) {
                isMatching = false;

            }

            return isMatching;
        }

        
        /**
         * Indicates whether the specified username and hint answer
         * match the current user's values.
         * All comparisons are case insensitive and parameters are <code>trimmed</code> prior
         * to comparing.
         * @param username the username to check
         * @param hintAnswer the hint answer to check
         * @return true if all the specified parameters match this user's
         * values, ignoring case; false otherwise
         */
        public boolean isMatching(String username, String hintAnswer) {

            boolean isMatching = true;

            try {

                isMatching = isMatching && (username != null && getLogin().equalsIgnoreCase(username));
                isMatching = isMatching && (hintAnswer != null && getCleartextHintAnswer().trim().equalsIgnoreCase(hintAnswer.trim()));
            
            } catch (net.project.security.EncryptionException e) {
                isMatching = false;

            }

            return isMatching;
        }

    }
}
