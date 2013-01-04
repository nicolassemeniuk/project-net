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

 package net.project.admin;

import java.sql.SQLException;

import net.project.base.directory.AuthenticationContext;
import net.project.base.directory.Directory;
import net.project.base.directory.DirectoryException;
import net.project.base.directory.IDirectoryEntry;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.license.License;
import net.project.license.LicenseException;
import net.project.license.create.LicenseContext;
import net.project.license.create.LicenseCreator;
import net.project.license.system.LicenseProperties;
import net.project.persistence.PersistenceException;
import net.project.resource.IPersonAttributes;
import net.project.security.domain.UserDomain;
import net.project.security.domain.UserDomainManager;
import net.project.security.login.LoginManager;

import org.apache.log4j.Logger;

/**
 * Manages user registration based on information captured in a <code>RegistrationBean</code>.
 * Performs the actual registration of a user and creation of licenses.
 */
public class RegistrationManager implements java.io.Serializable {

    /**
     * The registration bean containing all the registration information
     * for a user.
     */
    private RegistrationBean registrationBean = null;

    /**
     * The current system license properties, used to determine whether
     * licensing is mandator for registration.
     */
    private LicenseProperties licenseProperties = null;

    /**
     * License created during registration.
     */
    private License license;

    /**
     * Private constructor to prevent instantiation.
     * A <code>RegistrationBean</code> is always required.
     * @see #RegistrationManager(RegistrationBean)
     */
    private RegistrationManager() {
        // Do nothing
    }

    /**
     * Creates a new RegistrationManager for the specified registration bean.
     * @param registration the registration bean containing registration information
     */
    protected RegistrationManager(RegistrationBean registration) {
        setRegistrationBean(registration);
    }

    /**
     * Creates a new RegistrationManager for the specified registration bean
     * with the licenseProperties.
     * @param registration the registration bean containing registration information
     * @param licenseProperties the properties that control licensing information
     * within the system
     */
    protected RegistrationManager(RegistrationBean registration, LicenseProperties licenseProperties) {
        setRegistrationBean(registration);
        setLicenseProperties(licenseProperties);
    }


    /**
     * Sets the registration bean containing all the registration information.
     * @param registration the registration bean
     */
    private void setRegistrationBean(RegistrationBean registration) {
        this.registrationBean = registration;
    }


    /**
     * Sets the license properties that are used to make decisions during
     * registration.
     * For example, is licensing required for registration?
     * @param licenseProperties the current license properties
     * @see #getLicenseProperties
     */
    private void setLicenseProperties(LicenseProperties licenseProperties) {
        this.licenseProperties = licenseProperties;
    }


    /**
     * Returns the current license properties.
     * @return the license properties
     * @see #setLicenseProperties
     */
    private LicenseProperties getLicenseProperties() {
        return this.licenseProperties;
    }

    /**
     * Return the license created during the registration process.
     *
     * @return a <code>License</code> object created during the registration
     * process.
     */
    public License getLicense() {
        return license;
    }


    public static void validateAutomaticRegistration(LoginManager loginManager) throws AutomaticRegistrationException {
        RegistrationBean newUser = new RegistrationBean();
        UserDomain domain = new UserDomain (loginManager.getDomainID());

        try {
            domain.load();

            Directory directory = Directory.getInstance(domain.getDirectoryProviderType(), domain.getDirectoryConfiguration());
            directory.setAuthenticationContext(new AuthenticationContext(loginManager.getLoginContext()));

            boolean hasErrors = false;
            StringBuffer errorList = new StringBuffer();

            // next, get an authenticated directory entry for this user.
            // This means that we will once again authenticate this user against the directory -- and assuming success
            // will use the information returned to register them.  Note, the user must have successfully authenticated
            // by this point, so this shouldn't be an issue other than in an exceptional case.
            IDirectoryEntry newUserEntry = directory.getAuthenticatedDirectoryEntry();
            newUser.setDirectoryEntry(newUserEntry);
            newUser.populateFromDirectoryEntry();

            if (newUserEntry.getAttributeValue(IPersonAttributes.FIRSTNAME_ATTRIBUTE) == null) {
                hasErrors = true;
                errorList.append("\nRequired Attribute ");
                errorList.append(IPersonAttributes.FIRSTNAME_ATTRIBUTE);
                errorList.append(" was not found.");
            }

            if (newUserEntry.getAttributeValue(IPersonAttributes.LASTNAME_ATTRIBUTE) == null) {
                hasErrors = true;
                errorList.append("\nRequired Attribute ");
                errorList.append(IPersonAttributes.LASTNAME_ATTRIBUTE);
                errorList.append(" was not found.");
            }

            if (newUserEntry.getAttributeValue(IPersonAttributes.DISPLAYNAME_ATTRIBUTE) == null) {
                hasErrors = true;
                errorList.append("\nRequired Attribute ");
                errorList.append(IPersonAttributes.DISPLAYNAME_ATTRIBUTE);
                errorList.append(" was not found.");
            }

            if (newUserEntry.getAttributeValue(IPersonAttributes.EMAIL_ATTRIBUTE) == null) {
                hasErrors = true;
                errorList.append("\nRequired Attribute ");
                errorList.append(IPersonAttributes.EMAIL_ATTRIBUTE);
                errorList.append(" was not found.");
            }

            if (hasErrors) {
                throw new AutomaticRegistrationException("There was a problem loading this user's Directory Entry.\n" + errorList.toString());
            }

        } catch (PersistenceException pe) {
            throw new AutomaticRegistrationException("There was a problem loading the Domain for this user.", pe);
        } catch (DirectoryException de) {
            throw new AutomaticRegistrationException("There was a problem accessing the DirectoryServiceProvider.");
        }

    }
    

    /**
     * Registers the user using information in the registration bean.
     * This includes license creation if licensing is turned on.
     * It is expected that under normal circumstances this registration
     * process can complete without problems;  any validation of the
     * registration parameters should already have been completed
     * @throws IllegalStateException if there are no current license
     * properties
     * @throws RegistrationException if there is a problem completing
     * registration, for example a license key is missing or a database
     * problem occurred
     * @throws LicenseException if there is a problem getting the license
     * master properties.
     */
    protected void register() throws RegistrationException, LicenseException,
            PersistenceException {
        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            db.openConnection();
            register(db);
            db.commit();
        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException e) {
                Logger.getLogger(RegistrationManager.class).debug("Fatal error trying to register user.  " +
                        "Transaction in register() could not be rolled back");
            }
            throw new PersistenceException("Error thrown while trying to register.  " +
                    sqle.getMessage(),sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Registers the user using information in the registration bean.
     * This includes license creation if licensing is turned on.
     * It is expected that under normal circumstances this registration
     * process can complete without problems;  any validation of the
     * registration parameters should already have been completed
     *
     * @param db a <code>DBBean</code> object which is already in a transaction.
     * This transaction will not be committed or rolled back as part of this
     * method.
     * @throws IllegalStateException if there are no current license
     * properties
     * @throws RegistrationException if there is a problem completing
     * registration, for example a license key is missing or a database
     * problem occurred
     * @throws LicenseException if there is a problem getting the license
     * master properties.
     */
    protected void register(DBBean db) throws RegistrationException, LicenseException,
            SQLException, PersistenceException {

        register(db, true);
    }

    /**
     * Registers the user using information in the registration bean.
     * This includes license creation if licensing is turned on.
     * It is expected that under normal circumstances this registration
     * process can complete without problems;  any validation of the
     * registration parameters should already have been completed
     *
     * @param db a <code>DBBean</code> object which is already in a transaction.
     * This transaction will not be committed or rolled back as part of this
     * method.
     * @param notifyUser a <code>boolean</code> value indicating whether the
     * user should automatically be notified that they have been registered.
     * @throws IllegalStateException if there are no current license
     * properties
     * @throws RegistrationException if there is a problem completing
     * registration, for example a license key is missing or a database
     * problem occurred
     * @throws LicenseException if there is a problem getting the license
     * master properties.
     */
    protected void register(DBBean db, boolean notifyUser) throws RegistrationException, LicenseException,
            SQLException, PersistenceException {

        if (getLicenseProperties() == null) {
            throw new IllegalStateException(PropertyProvider.get("prm.registration.licenseproperties.error.none.message")); // "No license properties."
        }

        //
        // Note:
        // See net.project.license.create.LicenseUpdater also
        // It is very simliar, but not the same
        // Ideally we'd refactor to eliminate 2 locations that depend
        // on the enumeration of selection types
        //
        // Note #2: It is possible that the user that is registering already
        // has a license.  This would be the case if someone invited someone
        // then the appadmin assigned them a license before they registered.
        // In this case, we won't make them a new license.  (It would cause an
        // error if we did.)

        if (getLicenseProperties().isLicenseRequiredAtRegistration() &&
                !registrationBean.isRegisteringUserLicensed()) {
            // We must create a license to complete registration

            try {

                // Get the user-specified license information currently being
                // managed by the registration bean
                LicenseContext licenseContext = this.registrationBean.getLicenseContext();

                // Make a creator containing a new or existing license
                LicenseCreator licenseCreator = LicenseCreator.makeCreator(licenseContext, getLicenseProperties(), registrationBean);

                // At this point the license creator will have successfully
                // built a license or found an existing license
                // This will NOT have been persisted finally to persistent sotre
                // Thus we can dump out of this method at any time to abort
                // registration

                // Perform any tasks that must be completed PRIOR to physical
                // registration occurring
                preRegistration();

                // Now perform the actual registration
                // During this process the user will receive notification that
                // their registration is successful (pending verification)
                this.registrationBean.createPerson(db);

                // Now associate the license with the user being registered
                // This step allows the user to log in
                licenseCreator.commitLicense(db, this.registrationBean, true, false);
                license = licenseCreator.getLicense();

                // Now create the user
                createUser(db, notifyUser);
            } catch (LicenseException e) {
                // A problem occurred while creating the license
                throw new RegistrationException("Register user operation failed: " + e, e);
            }
        } else {
            // Licensing not required at registration time
            preRegistration();

            // Register
            this.registrationBean.createPerson(db);

            // Now create the user
            createUser(db, notifyUser);
        }
    }

    /**
     * Updates registration information.
     * All validation is presumed to have been completed on the
     * registration bean.
     * @param dbean database bean
     * @throws RegistrationException if there is a problem updating
     * registration; for example, the user could not be stored or
     * there is a problem updating in the domain or directory
     */
    protected void updateRegistration(DBBean dbean) throws RegistrationException {

        try {
            // Store the user's details
            this.registrationBean.store(dbean);

            // Now update the user in the domain (which also updates
            // the user in the directory
            net.project.security.domain.UserDomainManager.updateUserInDomain(this.registrationBean, this.registrationBean.getUserDomainID(), this.registrationBean.getDirectoryEntry(), dbean);

        } catch (PersistenceException pe) {
            throw new RegistrationException("Update registration operation failed: " + pe, pe);

        } catch (net.project.security.domain.DomainException e) {
            throw new RegistrationException("Update registration operation failed; " + e, e);
        }

    }

    /**
     * Updates registration information.
     * All validation is presumed to have been completed on the
     * registration bean.
     * @throws RegistrationException if there is a problem updating
     * registration; for example, the user could not be stored or
     * there is a problem updating in the domain or directory
     */
    protected void updateRegistration() throws RegistrationException {

        DBBean dbean = new DBBean();

        try {
            updateRegistration(dbean);
        } finally {
            dbean.release();
        }

    }

    /**
     * Tasks to perform before registration.
     * @throws RegistrationException if there is a problem performing a task
     */
    private void preRegistration() throws RegistrationException {
        // set the users verification code (it will be sent out in email
        this.registrationBean.generateVerificationCode();
    }

    /**
     * Create user tasks.  This includes adding the user to a domain, logging in
     * history and sending a verification email.
     *
     * @throws RegistrationException if there is a problem completing
     * these tasks, such as a problem adding the user to a domain or
     * sending the verification code
     */
    private void createUser() throws RegistrationException {
        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            db.openConnection();

            createUser(db, true);

            db.commit();
        } catch (Exception e) {
            try {
                db.rollback();
            } catch (SQLException e2) {
                Logger.getLogger(RegistrationManager.class).debug("Fatal error: unable to roll back transaction.");
            }

            throw new RegistrationException(e);
        } finally {
            db.release();
        }

    }

    /**
     * Create user tasks.
     * This includes adding the user to a domain, logging in history
     * and sending a verification email
     *
     * @param db a <code>DBBean</code> which is already in a transaction.  This
     * method guarantees not to commit or rollback that transaction.
     * @param notifyUser a <code>boolean</code> value indicating whether we
     * should notify the user.
     * @throws RegistrationException if there is a problem completing
     * these tasks, such as a problem adding the user to a domain or
     * sending the verification code
     * @throws SQLException if an error occurred while trying to create the user.
     */
    private void createUser(DBBean db, boolean notifyUser) throws RegistrationException, SQLException {
        try {
            // Now, assuming the registration process was successful,
            // add the user to the appropriate UserDomain specifying
            // the user , domain to which to add and directory-specific
            // entry information
            UserDomain domain = registrationBean.getUserDomain();

            if (domain != null) {
                registrationBean.getUserDomain().addUser(registrationBean, registrationBean.getDirectoryEntry(), db);
            } else {
                UserDomainManager.addUserToDomain(this.registrationBean, this.registrationBean.getUserDomainID(), this.registrationBean.getDirectoryEntry(), db);
            }


            // Now, log the user's creation, send a verification email and set the isRegistered flag for the user
            // to prevent multiple submissions/registrations.
            this.registrationBean.logUserHistory(db);

            // Only send a verification email if this domain says that
            // verification is required
            if (this.registrationBean.getUserDomain().isVerificationRequired() && notifyUser) {
                this.registrationBean.sendVerificationEmail();
            }

            this.registrationBean.setUserStored();
        } catch (net.project.security.domain.DomainException de) {
            throw new RegistrationException("Registration create user operation failed: the user could not be added to the domain: " + de, de);
        } catch (net.project.notification.NotificationException ne) {
            // In this case, there is no reason to rollback the entire transaction, rather just tell the user that they were
            // successfully registered but need to ask an administrator to resend their verification code.
            throw new RegistrationException("Your user registration was successful, but we were unable to send your email confirmation.." +
                    " Please ask an administrator to resend your Registration Verification Email.", ne);
        } catch (net.project.persistence.PersistenceException pe) {
            throw new RegistrationException("Registration create user operation failed: Unable to load domain: " + pe, pe);
        }
    }

}
