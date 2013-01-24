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

import java.sql.SQLException;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.license.LicenseResult;
import net.project.license.LicenseResultCode;
import net.project.license.LicenseStatus;
import net.project.license.LicenseStatusCode;
import net.project.license.create.LicenseUpdater;
import net.project.license.system.LicenseProperties;
import net.project.persistence.PersistenceException;
import net.project.resource.PersonStatus;
import net.project.security.User;
import net.project.security.domain.DomainAuthenticator;

/**
 * Provides methods to complete the login process.
 * Authenticates user, checks licenses etc.
 */
public class LoginManager implements java.io.Serializable {

    //
    // Static members
    //

    /**
     * Logs the last brand the User logged in under.
     * @param userID the ID of the user currently attempting to log in.
     * @throws PersistenceException if an unexpected database problem occurs.
     */
    private static void logLastBrand (String userID) throws PersistenceException {

        DBBean db = new DBBean();
        String updateQuery = "update pn_user set last_brand_id = ? where user_id = ?";
        int index = 0;

        try {
            db.prepareStatement(updateQuery);

            db.pstmt.setString(++index, PropertyProvider.getActiveBrandID());
            db.pstmt.setString(++index, userID);

            db.executePrepared();

        } catch (SQLException e) {
            throw new PersistenceException("Log last brand operation failed: " + e, e);

        } finally {
            db.release();
        }

    }

    //
    // Instance members
    //

    /** The login context used to complete login. */
    private LoginContext loginContext = null;

    /** The license properties. */
    private LicenseProperties licenseProperties = null;

    /** The license check result. */
    private LicenseResult licenseResult = null;

    /** The license check status. */
    private LicenseStatus licenseStatus = null;

    /** Any error message that occurred during login. */
    private String errorMessageHTML = null;

    /** The authenticated user, created during login. */
    private User authenticatedUser = null;

    /**
     * Creates a new LoginManager.
     */
    public LoginManager() {
        // Do nothing
    }

    /**
     * Sets the license properties.  These are used to determine whether a
     * license is required during login
     * @param licenseProperties
     */
    public void setLicenseProperties(LicenseProperties licenseProperties) {
        this.licenseProperties = licenseProperties;
    }

    /**
     * Creates a login context.
     * @param username the username specified for login
     * @param clearTextPassword the clear text password specified for login
     * @param domainID the domainID specified for login
     */
    public void createLoginContext(String username, String clearTextPassword, String domainID) {
        setLoginContext(new LoginContext(username, clearTextPassword, domainID));
    }

    private void setLoginContext(LoginContext loginContext) {
        this.loginContext = loginContext;
    }

    public LoginContext getLoginContext() {
        return this.loginContext;
    }

    /**
     * Returns the domainID from the login Context
     * @return
     */
    public String getDomainID() {
        return this.loginContext.getDomainID();
    }

    public LoginStatusCode completeLogin() throws LoginException, PersistenceException {
    	return completeLogin(false);
    }
    
    /**
     * Attempts to log into the application using the current login context.
     * The login is recorded in login history if successfully completed.
     * @return the status of completing login; the postconditions depend on the
     * status returned:
     * <li><code>LoginStatusCode.INVALID_LICENSE</code> user has been authenticated successfully
     * but their license is invalid.  Login MUST NOT proceed.  However, the
     * authenticated user may be used to acquire a new license
     * <li><code>LoginStatusCode.SUCCESS</code> user has been authenticated successfully
     * is returned, the authenticated user is available via <code>{@link #getAuthenticatedUser}</code>
     * <li>Any other status means the user was not even authenticated successfully
     * @see #createLoginContext
     * @throws LoginException if there is a problem completing the login
     */
    public LoginStatusCode completeLogin( boolean shadowLogin, boolean isFromSSOLogin) throws LoginException, PersistenceException {

        LoginStatusCode statusCode = null;

        // First validate that login context parameters are all present
        // Usernamed, password and domain ID must be specified

        if (!this.loginContext.validateMandatoryParameters()) {
            // Some problem with parameters
            statusCode = LoginStatusCode.MISSING_PARAMETER;
            setErrorMessageHTML(PropertyProvider.get("prm.global.login.message.missingparameter"));

        } else {
            // All login context parameters are present
            // Try and authenticate the user; that is, check the username
            // and password

            DomainAuthenticator domainAuthenticator = null;

            try {
                domainAuthenticator = DomainAuthenticator.getInstance(
                        this.loginContext.getDomainID(), this.loginContext.getUsername(), this.loginContext.getClearTextPassword());

                domainAuthenticator.authenticate(shadowLogin, isFromSSOLogin);
                // if no exceptions are thrown, the login was a success
                statusCode = LoginStatusCode.SUCCESS;

            } catch (net.project.security.domain.DomainException e) {
                // Problem creating the domain authenticator
                throw new LoginException("Unable to complete login: " + e, e);

            } catch (net.project.base.directory.DirectoryException e) {
                // Problem authenticating
                throw new LoginException("Unable to complete login: " + e, e);

            } catch (net.project.admin.AutomaticRegistrationException e) {
                // we will catch this exception and set an error code (since that is the going approach in this section)
                statusCode = LoginStatusCode.AUTOMATIC_REGISTRATION;
                return statusCode;
            }


            if (!domainAuthenticator.isAuthenticated()) {
                // Not authenticated successfully
                statusCode = LoginStatusCode.AUTHENTICATION_ERROR;
                setErrorMessageHTML(PropertyProvider.get("prm.global.login.message.authenticationerror"));

            } else {
                // Authenticated successfully

                boolean isUserActive = true ;
                User user = null ;

                try {
                    // Load the authenticated user
                    // This occurs prior to validating the license since
                    // the validation routines require a valid user

                    // Note: Authenticated user is not yet loaded
                    // Currently, when a domain authenticates a user it only
                    // loads the userID and username
                    user = domainAuthenticator.getAuthenticatedUser();
                    user.load();

                    logLastBrand(user.getID());

                    // Prevent Login of Inactive Users

                    if(user.getStatus() != null && user.getStatus().equals(PersonStatus.UNCONFIRMED)) {

                        statusCode = LoginStatusCode.UNCONFIRMED_USER ;
                        setErrorMessageHTML(PropertyProvider.get("prm.global.login.message.unconfirmed"));
                        isUserActive = false ;

                    } else if (user.getStatus() != null && !user.getStatus().equals(PersonStatus.ACTIVE)) {

                        statusCode = LoginStatusCode.INACTIVE_USER ;
                        setErrorMessageHTML(PropertyProvider.get("prm.global.login.message.inactive"));
                        isUserActive = false ;

                    }

                } catch (net.project.persistence.PersistenceException pe) {
                    // Problem loading user
                    throw new LoginException("Error completing login: " + pe, pe);

                } catch (net.project.security.domain.DomainException e) {
                    // Problem getting authenticated user
                    throw new LoginException("Error completing login: " + e, e);

                }

                if (isUserActive) {

                    user.setAuthenticated (true);
                    LoginHistory.logVisit(user);
                    setAuthenticatedUser(user);


                    // Validate license, if necessary
                    // A property is used to determine whether a license is
                    // required for login
                    // Additionally, Application Administrator users do not
                    // require licenses to login
                    boolean isLicenseError = false;
                    boolean isLicenseRequired = true;
                    try {
                        if (getAuthenticatedUser().isApplicationAdministrator()){
                            isLicenseRequired = false;
                        } else {
                            isLicenseRequired = this.licenseProperties.isLicenseRequiredAtLogin();
                        }
                    } catch (net.project.license.LicenseException le) {
                        // Problem loading license master properties
                        throw new LoginException("Error completing login: " + le, le);

                    }catch (PersistenceException pe) {
                        // Problem loading license master properties. set error message
                    	setErrorMessageHTML(pe.getMessage());
                        throw new LoginException("Error completing login: " + pe, pe);
                    }

                    if (isLicenseRequired) {
                        LicenseStatus licenseStatus = null;
                        LicenseResult licenseResult = null;

                        LicenseUpdater licenseUpdater = new LicenseUpdater();
                        licenseUpdater.setUser(getAuthenticatedUser());

                        licenseStatus = licenseUpdater.checkLicenseStatus();
                        try {
                            licenseResult = licenseUpdater.checkLicenseResult();
                        } catch (net.project.license.LicenseException le) {
                            // Problem loading license master properties
                            throw new LoginException("Error completing login: " + le, le);

                        }catch (PersistenceException pe) {
                            // Problem loading license master properties.  set error message
                        	setErrorMessageHTML(pe.getMessage());
                            throw new LoginException("Error completing login: " + pe, pe);

                        }

                        if (licenseStatus != null && !licenseStatus.getCode().equals(LicenseStatusCode.ENABLED)) {
                            // License not valid
                            isLicenseError = true;
                            setErrorMessageHTML( (licenseStatus.hasMessage() ? licenseStatus.getMessage() : PropertyProvider.get("prm.global.login.message.invalidlicense")) );


                        } else {
                            if (!licenseResult.getCode().equals(LicenseResultCode.VALID)) {
                                // License not valid
                                isLicenseError = true;
                                setErrorMessageHTML( (licenseResult.hasMessage() ? licenseResult.getMessage() : PropertyProvider.get("prm.global.login.message.invalidlicense")) );
                            }

                            setLicenseResult(licenseResult);
                        }

                        setLicenseStatus(licenseStatus);

                    } else {
                        // License not required, automatically valid
                        setLicenseResult(new LicenseResult(LicenseResultCode.VALID));

                    }

                    if (isLicenseError) {
                        // License required and not valid
                        // HTML error message expected to have been set
                        statusCode = LoginStatusCode.INVALID_LICENSE;

                    } else {
                        // License not required or no error
                        statusCode = LoginStatusCode.SUCCESS;

                    }

                }
            }

        }
        return statusCode;

    }

    /**
     * Attempts to log into the application using the current login context.
     * The login is recorded in login history if successfully completed.
     * @return the status of completing login; the postconditions depend on the
     * status returned:
     * <li><code>LoginStatusCode.INVALID_LICENSE</code> user has been authenticated successfully
     * but their license is invalid.  Login MUST NOT proceed.  However, the
     * authenticated user may be used to acquire a new license
     * <li><code>LoginStatusCode.SUCCESS</code> user has been authenticated successfully
     * is returned, the authenticated user is available via <code>{@link #getAuthenticatedUser}</code>
     * <li>Any other status means the user was not even authenticated successfully
     * @see #createLoginContext
     * @throws LoginException if there is a problem completing the login
     */
    public LoginStatusCode completeLogin( boolean shadowLogin) throws LoginException, PersistenceException {
    	return completeLogin(shadowLogin, false);
    }

    /**
     * Sets the HTML-formatted login error message.
     * @param errorMessage the error message
     */
    private void setErrorMessageHTML(String errorMessage) {
        this.errorMessageHTML = errorMessage;
    }

    /**
     * Returns the HTML-formatted login error message.
     * @return the error message
     */
    public String getErrorMessageHTML() {
        return this.errorMessageHTML;
    }

    /**
     * Sets the authenticated user.
     * @param user the user that has been authenticated
     */
    private void setAuthenticatedUser(User user) {
        this.authenticatedUser = user;
    }


    /**
     * Returns the authenticated user.
     * {@link #completeLogin} should be called first to authenticate the user.
     * @return the user
     * @throws IllegalStateException if the user has not yet been authenticated
     */
    public User getAuthenticatedUser() {
        if (this.authenticatedUser == null) {
            throw new IllegalStateException("User not authenticated yet");
        }

        return this.authenticatedUser;
    }

    /**
     * Sets the license result generated when completing the login process.
     * @param licenseResult the result of license check
     */
    private void setLicenseResult(LicenseResult licenseResult) {
        this.licenseResult = licenseResult;
    }

    /**
     * Returns the license result generated when completing login.
     * @return the license check result
     */
    public LicenseResult getLicenseResult() {
        return this.licenseResult;
    }

    /**
     * Sets the license status generated when completing the login process.
     * @param licenseStatus the status of license check
     */
    private void setLicenseStatus(LicenseStatus licenseStatus) {
        this.licenseStatus = licenseStatus;
    }

    /**
     * Returns the license status generated when completing login.
     * @return the license check status
     */
    public LicenseStatus getLicenseStatus() {
        return this.licenseStatus;
    }

}
