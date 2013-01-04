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

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.project.base.directory.Directory;
import net.project.base.directory.DirectoryException;
import net.project.base.directory.IDirectoryEntry;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.DBFormat;
import net.project.license.License;
import net.project.license.LicenseException;
import net.project.license.create.LicenseContext;
import net.project.license.system.LicenseProperties;
import net.project.notification.Notification;
import net.project.notification.NotificationException;
import net.project.persistence.PersistenceException;
import net.project.resource.Address;
import net.project.resource.AssignmentStatus;
import net.project.resource.IPersonAttributes;
import net.project.resource.PersonStatus;
import net.project.resource.SpaceAssignment;
import net.project.security.Crypto;
import net.project.security.domain.UserDomain;
import net.project.util.LocaleProvider;
import net.project.util.StringUtils;
import net.project.util.Validator;

import org.apache.log4j.Logger;

/**
 * Provides registration process for a new user to the system.
 * Supports a re-entrant (for the browser session life) wizard-based registration process.
 * Note that all
 *
 * @author Roger Bly
 * @author Dan Kelley 4/6/00
 * @author Tim Morrow 4/15/02
 */
public class RegistrationBean extends net.project.security.User implements Serializable {


    /** The default verification code size, currently <code>6</code>. */
    private static final int DEFAULT_VERIFICATION_CODE_SIZE = 6;


    /** The current license context. */
    private LicenseContext licenseContext = null;

    /**
     * Indicates whether the registration process has started.
     * @see #isStarted
     */
    private boolean isStarted = false;

    /**
     * The Directory Entry being created during registration.
     */
    private IDirectoryEntry directoryEntry = null;
    
    private boolean userStored = false;

    /** Signifies whether or not this registration is continuing a stub from a space invite */
    private boolean isStubUser = false;

    private boolean isUserUpdating = false;

    /** identifies whether to verify registration information before proceeding to completeRegistration */
    private boolean validateRegistration = true;

    /**
     * Creates a RegistrationBean with default initialization.
     * @since v1
     */
    public RegistrationBean() {
        init();
    }

    private void init() {
        setAddress(new Address());

        // Initialize the license context
        LicenseContext context = new LicenseContext();
        context.setSelectionType(net.project.license.create.LicenseSelectionType.ENTERED_LICENSE_KEY);
        setLicenseContext(context);
    }

    /**
     * Specifies whether registration is started.
     * This should be set to indicate that redirect to the initial
     * page is NOT required.
     * @param isStarted true means that the registration process
     * has been started
     * @see #isStarted
     */
    public void setStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }

    /**
     * Indicates whether registration is started.
     * If not started, the user has not yet visited the first
     * registration page.  This helps to decide whether the registration
     * wizard should redirect to the first page if some intermediate
     * page is bookmarked.
     * @return true of the registration process has started; false
     * otherwise
     * @see #setStarted
     */
    public boolean isStarted() {
        return this.isStarted;
    }

    public boolean isValidateRegistration() {
        return this.validateRegistration;
    }

    public void setValidateRegistration(boolean validate) {
        this.validateRegistration = validate;
    }

    /**
     * Specifies whether the user record is getting Updated
     * @param isUpdating flag for indicating whether the user record is being Updated
     */
    public void setUpdating(boolean isUpdating) {
        this.isUserUpdating = isUpdating;
    }

    /**
     * Returns whether the user record is getting Updated
     */
    private boolean isUpdating() {
        return this.isUserUpdating;
    }

    /**
     * Sets the directory entry being created or read as part of this
     * registration process.
     * The DirectoryEntry may be read from a directory (when a user
     * is required to be present in a directory) or written to a directory
     * (when a user is required to not exist and is created as part
     * of registration)
     * @param directoryEntry the directory entry
     * @see #getDirectoryEntry
     */
    public void setDirectoryEntry(IDirectoryEntry directoryEntry) {
        this.directoryEntry = directoryEntry;
    }


    /**
     * Returns the directory entry being created or read as part of
     * this registration process.
     * @return the directory entry
     * @see #setDirectoryEntry
     */
    public IDirectoryEntry getDirectoryEntry() {
        return this.directoryEntry;
    }

    /**
     * Loads the directory entry from the current user domain based
     * on the current username.
     * Assumes anonymous directory fetches are supported.
     * After executing, directory entry is available.
     * @see #getDirectoryEntry
     * @throws PersistenceException if there is a problem loading the
     * domain or directory configuration
     * @throws DirectoryException if there is a problem getting the
     * directory entry
     */
    private void loadDirectoryEntry() throws PersistenceException,
            net.project.base.directory.DirectoryException {

        // Initialize a directory for the current provider type
        // and configuration
        Directory directory = Directory.getInstance(getUserDomain().getDirectoryProviderType(),
                getUserDomain().getDirectoryConfiguration());

        // Save the entry for the current username
        setDirectoryEntry(directory.getDirectoryEntry(getLogin()));
    }

    /**
     * Populates this Registration bean from the current directory
     * entry.
     * Only profile properties provided by the directory entry
     * will be modified.
     * @throws NullPointerException if the directory entry has
     * not bee specified
     */
    public void populateFromDirectoryEntry() {

        IDirectoryEntry entry = getDirectoryEntry();
        if (entry == null) {
            throw new NullPointerException("Directory entry is null");
        }


        if (entry.isAttributeProvided(IPersonAttributes.PREFIX_ATTRIBUTE)) {
        	String prefixName = entry.getAttributeValue(IPersonAttributes.PREFIX_ATTRIBUTE);
            this.setNamePrefix(prefixName.length() > 80 ? prefixName.substring(0, 79) : prefixName);
        }

        if (entry.isAttributeProvided(IPersonAttributes.FIRSTNAME_ATTRIBUTE)) {
        	String firstName = entry.getAttributeValue(IPersonAttributes.FIRSTNAME_ATTRIBUTE);
            this.setFirstName(firstName.length() > 40 ? firstName.substring(0, 39) : firstName);
        }

        if (entry.isAttributeProvided(IPersonAttributes.MIDDLENAME_ATTRIBUTE)) {
        	String middleName = entry.getAttributeValue(IPersonAttributes.MIDDLENAME_ATTRIBUTE);
            this.setMiddleName(middleName.length() > 80 ? middleName.substring(0, 79) : middleName);
        }

        if (entry.isAttributeProvided(IPersonAttributes.LASTNAME_ATTRIBUTE)) {
        	String lastName = entry.getAttributeValue(IPersonAttributes.LASTNAME_ATTRIBUTE);
            this.setLastName(lastName.length() > 60 ? lastName.substring(0, 59) : lastName);
        }

        if (entry.isAttributeProvided(IPersonAttributes.SUFFIX_ATTRIBUTE)) {
        	String suffixName = entry.getAttributeValue(IPersonAttributes.SUFFIX_ATTRIBUTE);
            this.setNameSuffix(suffixName.length() > 80 ? suffixName.substring(0, 79) : suffixName);
        }

        if (entry.isAttributeProvided(IPersonAttributes.DISPLAYNAME_ATTRIBUTE)) {
        	String displayName = entry.getAttributeValue(IPersonAttributes.DISPLAYNAME_ATTRIBUTE);
            this.setDisplayName(displayName.length() > 240 ? displayName.substring(0, 239) : displayName);
        }

        if (entry.isAttributeProvided(IPersonAttributes.ADDRESS1_ATTRIBUTE)) {
        	String address1 = entry.getAttributeValue(IPersonAttributes.ADDRESS1_ATTRIBUTE);
            this.setAddress1(address1.length() > 80 ? address1.substring(0, 79) : address1);
        }

        if (entry.isAttributeProvided(IPersonAttributes.ADDRESS2_ATTRIBUTE)) {
        	String address2 = entry.getAttributeValue(IPersonAttributes.ADDRESS2_ATTRIBUTE);
            this.setAddress2(address2.length() > 80 ? address2.substring(0, 79) : address2);
        }

        if (entry.isAttributeProvided(IPersonAttributes.ADDRESS3_ATTRIBUTE)) {
            this.setAddress3(entry.getAttributeValue(IPersonAttributes.ADDRESS3_ATTRIBUTE));
        }

        if (entry.isAttributeProvided(IPersonAttributes.ADDRESS4_ATTRIBUTE)) {
            this.setAddress4(entry.getAttributeValue(IPersonAttributes.ADDRESS4_ATTRIBUTE));
        }

        if (entry.isAttributeProvided(IPersonAttributes.ADDRESS5_ATTRIBUTE)) {
            this.setAddress5(entry.getAttributeValue(IPersonAttributes.ADDRESS5_ATTRIBUTE));
        }

        if (entry.isAttributeProvided(IPersonAttributes.ADDRESS6_ATTRIBUTE)) {
            this.setAddress6(entry.getAttributeValue(IPersonAttributes.ADDRESS6_ATTRIBUTE));
        }

        if (entry.isAttributeProvided(IPersonAttributes.ADDRESS7_ATTRIBUTE)) {
            this.setAddress7(entry.getAttributeValue(IPersonAttributes.ADDRESS7_ATTRIBUTE));
        }

        if (entry.isAttributeProvided(IPersonAttributes.CITY_ATTRIBUTE)) {
        	String city = entry.getAttributeValue(IPersonAttributes.CITY_ATTRIBUTE);
            this.setCity(city.length() > 50 ? city.substring(0, 49) : city);
        }

        if (entry.isAttributeProvided(IPersonAttributes.STATE_ATTRIBUTE)) {
            this.setState(entry.getAttributeValue(IPersonAttributes.STATE_ATTRIBUTE));
        }

        if (entry.isAttributeProvided(IPersonAttributes.ZIPCODE_ATTRIBUTE)) {
        	String zipCode = entry.getAttributeValue(IPersonAttributes.ZIPCODE_ATTRIBUTE);
            this.setZipcode(zipCode.length() > 20 ? zipCode.substring(0, 19) : zipCode);
        }

        if (entry.isAttributeProvided(IPersonAttributes.COUNTRY_ATTRIBUTE)) {
            this.setCountry(entry.getAttributeValue(IPersonAttributes.COUNTRY_ATTRIBUTE));
        }

        if (entry.isAttributeProvided(IPersonAttributes.OFFICE_PHONE_ATTRIBUTE)) {
        	String officePhone = entry.getAttributeValue(IPersonAttributes.OFFICE_PHONE_ATTRIBUTE); 
            this.setOfficePhone(officePhone.length() > 20 ? officePhone.substring(0, 19) : officePhone);
        }

        if (entry.isAttributeProvided(IPersonAttributes.FAX_PHONE_ATTRIBUTE)) {
        	String faxPhone = entry.getAttributeValue(IPersonAttributes.FAX_PHONE_ATTRIBUTE);
            this.setFaxPhone(faxPhone.length() > 20 ? faxPhone.substring(0, 19) : faxPhone);
        }

        if (entry.isAttributeProvided(IPersonAttributes.EMAIL_ATTRIBUTE)) {
            this.setEmail(entry.getAttributeValue(IPersonAttributes.EMAIL_ATTRIBUTE));
        }
    }
    

    /**
     * Populate default locale settings for this user.
     * Locale defaults will be drawn from default settings specified by the current configuration.
     */
    public void populateLocalizationDefaults() {

        this.setLanguageCode(PropertyProvider.get("prm.global.brand.defaultlanguagecode"));
        this.setLocaleCode(PropertyProvider.get("prm.global.brand.defaultlocalecode"));
        this.setTimeZoneCode(PropertyProvider.get("prm.global.brand.defaulttimezone"));
    }


    /**
     * Indicates whether the current email address is a valid internet
     * address.
     * @return true if the email address is a valid internet email address;
     * false otherwise
     * @see net.project.notification.Email#isValidInternetAddress
     * @deprecated as of 7.5.1; no replacement
     * This method is unused and is no longer supported
     */
    public boolean isValidEmailAddress() {
        return net.project.notification.Email.isValidInternetAddress(getEmail());
    }


    /**
     * Checks whether or not a person already exists in the application
     * for the specified email address.
     * <p>
     * An email is defined to exist if there is a person with that
     * email address (primary or alternate), including invited but
     * unregistered persons (stub persons).
     * </p>
     * <p>
     * If this method returns true, use {@link #isUserRegistered} to
     * determine whether the email address belongs to a registered
     * or unregistered user.
     * </p>
     * @param email the email address to check
     * @return true if a person with the email address already exists;
     * false otherwise
     */
    public boolean checkEmailExists(String email)
            throws net.project.persistence.PersistenceException {

        boolean check = true;

        if (this.isUpdating()) {
            check = net.project.base.DefaultDirectory.checkEmailExistsExceptSpecified(email, getID());
        } else {
            check = net.project.base.DefaultDirectory.checkEmailExists(email);
        }
        return check;
    }


    /**
     * Indicates whether there is a registered user using the specified
     * email address.
     * <p>
     * A registered user with the specified email address is defined
     * to exist if there is a person with that email address (primary
     * or alternate).
     * </p>
     * <p>
     * This does not return true if an invited but unregistered person
     * (stub person) has the email address.
     * </p>
     * @param email the email address to check
     * @return true if there is a registered user with that email address;
     * false otherwise
     */
    private boolean isUserRegistered(String email) {
        return net.project.base.DefaultDirectory.isUserRegistered(email);
    }


    /**
     * Specifies that this user has been stored.
     * @see #isUserStored
     */
    public void setUserStored() {
        this.userStored = true;
    }

    /**
     * Indicates whether this user has been stored.
     *
     * @param isStored a <code>boolean</code> variable indicating if this user
     * has been stored.
     */
    public void setUserStored(boolean isStored) {
        this.userStored = isStored;
    }

    /**
     * Indicates whether this user has been stored.
     * @see #setUserStored
     * @see #store
     */
    private boolean isUserStored() {
        return this.userStored;
    }

    /**
     * Indicates whether this user was found to be an unregistered
     * (stub)
     * @return true if the user was found to be a stub user
     */
    private boolean isStubUser() {
        return this.isStubUser;
    }

    /**
     *  the user stub for the current email address.
     * Does not load first name, last name or display name
     * as those will have been set in the interface
     * @precondition an email address is available with {@link #getEmail}
     * @postcondition if the person is found, the current ID is set to
     * the ID of the person matching the specified email address and
     * {@link #isStubUser} returns true
     * @throws IllegalStateException if there is not current email address
     * @throws PersistenceException if there is a problem loading
     */

    private void populateIDForUserStub() throws PersistenceException {

        if (getEmail() == null) {
            throw new IllegalStateException("Cannot load user without email");
        }

        StringBuffer query = new StringBuffer();
        DBBean db = new DBBean();

        query.append("select person_id from pn_person_view ");
        query.append(" where user_status != '" + PersonStatus.DELETED.getID() + "'");
        query.append(" and ( lower(email) = LOWER(?) or lower(alternate_email_1) = LOWER(?) ");
        query.append(" or lower(alternate_email_2) = LOWER(?) or lower(alternate_email_3) = LOWER(?) ) ");

        try {
            int index = 0;
            int count = 0; // Counter for Resultset

            db.prepareStatement(query.toString());

            db.pstmt.setString(++index, getEmail());
            db.pstmt.setString(++index, getEmail());
            db.pstmt.setString(++index, getEmail());
            db.pstmt.setString(++index, getEmail());

            db.executePrepared();

            while (db.result.next()) {

                if (count > 0) {

                    // This means that we have got more than one resultset
                    // which is erroneous condition

                    throw new IllegalStateException("More than one active user is using the same email ID");
                }

                setID(db.result.getString("person_id"));
                this.isStubUser = true;
                count++; // update the counter
            }

        } catch (java.sql.SQLException sqle) {
            this.isStubUser = false;
            Logger.getLogger(RegistrationBean.class).debug("Directory.lookupByEmail() threw an SQLE: " + sqle);
            throw new PersistenceException("RegistrationBean.populateIDForUserStub() unable to populate id.", sqle);
        } finally {
            db.release();
        }

    }


    /**
     * Indicates whether the current username is available for the
     * current domain.
     * Synonymous with calling <code>isUsernameAvailable(getLogin())</code>.
     * @return true if the username is available; false otherwise
     */
    private boolean isUsernameAvailable() {
        return isUsernameAvailable(getLogin());
    }

    /**
     * Indicates whether the specified username is available for the
     * current domain.
     * The username is available if no other user in the domain
     * has the same username (ignoring case).
     * @return true if the username is available; false otherwise
     */
    public boolean isUsernameAvailable(String username) {

        boolean isAvailable = false;

        try {
            UserDomain domain = this.getUserDomain();
            isAvailable = !domain.hasUsername(username);

        } catch (PersistenceException pe) {
            // Problem getting the domain
            isAvailable = false;

        } catch (net.project.security.domain.DomainException e) {
            // Problem checking the username
            isAvailable = false;

        }

        return isAvailable;
    }

    public RegistrationResult completeRegistration(LicenseProperties licenseProps)
            throws RegistrationException, LicenseException, PersistenceException {

        RegistrationResult toReturn = null;

        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            db.openConnection();

            toReturn = completeRegistration(licenseProps, db);

            db.commit();
        } catch (SQLException e) {
            try {
                db.rollback();
            } catch (SQLException e1) {
            	Logger.getLogger(RegistrationBean.class).error("Fatal error in completion of registration.  " +
                        "After a SQLException was thrown, it was not possible to roll back " +
                        "the transaction.");
            }

            throw new PersistenceException("Error completing registration.  "+e.getMessage(), e);
        } finally {
            db.release();
        }

        return toReturn;
    }

    /**
     * Completes the registration process based on the current information.
     * All mandatory information must have been gathered at this point.
     * @param licenseProps the license properties object; used to determine
     * whether a license is required
     * @param db a <code>DBBean</code> object which is already in a transaction.
     * This transaction is guaranteed to not be committed or rolled back as a
     * result of a call to this function.
     * @return the result of registration; success if registration completed,
     * error if any predictable problems occurred during registration (such
     * as missing information, email or login already in use)
     * @throws RegistrationException if there is an error completing the
     * registration, such as a database error or encryption error
     * @see RegistrationResult#isSuccess
     * @throws LicenseException if there is a problem loading the master license
     * properties
     * @throws SQLException if any error has occurred while trying to store
     * information in the database about the registration.
     */
    public RegistrationResult completeRegistration(LicenseProperties licenseProps,
                                                   DBBean db) throws RegistrationException, LicenseException, SQLException, PersistenceException {

        return completeRegistration(licenseProps, db, true);
    }


    /**
     * Completes the registration process based on the current information.
     * All mandatory information must have been gathered at this point.
     * @param licenseProps the license properties object; used to determine
     * whether a license is required
     * @param db a <code>DBBean</code> object which is already in a transaction.
     * This transaction is guaranteed to not be committed or rolled back as a
     * result of a call to this function.
     * @param notifyUser a <code>boolean</code> value indicating whether the user
     * should be notified about their registration.
     * @return the result of registration; success if registration completed,
     * error if any predictable problems occurred during registration (such
     * as missing information, email or login already in use)
     * @throws RegistrationException if there is an error completing the
     * registration, such as a database error or encryption error
     * @see RegistrationResult#isSuccess
     * @throws LicenseException if there is a problem loading the master license
     * properties
     * @throws SQLException if any error has occurred while trying to store
     * information in the database about the registration.
     */
    public RegistrationResult completeRegistration(LicenseProperties licenseProps,
                                                   DBBean db, boolean notifyUser) throws RegistrationException,
            LicenseException, SQLException, PersistenceException {

        RegistrationResult result = new RegistrationResult();

        // Check to see if the user is already registered based on the current
        // email address.  This is to guard against resubmission of a registration page
        if (isUserRegistered(getEmail())) {
            result.addError("ecom_ShipTo_Online_Email", "The email address entered has already been registered.  Email addresses can only be registered once.");

        } else {

            // Now Check to see if the login name is available
            // isAvailable actually checks login name and email address
            // It actually returns the same value as loginExists !?!
            if (!isUsernameAvailable()) {
                result.addError("login", "The Login name entered has already been registered. Login names can only be used once.");
            }

            // now validate the rest of the form submission
            result.addAllErrors(verifyRegistration());
        }


        // At this point either an error has occurred or the registration has
        // been verified
        if (result.isSuccess()) {
            // first, make sure the user hasn't already been stored to the db (with reload or multi-submit)
            if (isUserStored()) {
                result.addError("The registration process has already completed");
            } else {
                // We must finalize the registration
                // Use a RegistrationManager to orchestrate all components
                // of registration
                RegistrationManager registrationManager = new RegistrationManager(this, licenseProps);
                registrationManager.register(db, notifyUser);
                result.setLicense(registrationManager.getLicense());
            }
        }

        return result;
    }

       /*
    * Verify that all required fields are filled in.
    * @return the collection of error messages
    */
    private Collection verifyRegistration() {
        ArrayList errors = new ArrayList();

        if (isValidateRegistration()) {
            // NOTE:
            // these are already being validated by JavaScript,
            // so these will not show up
            if (this.firstName == null) errors.add("First Name is a required field");
            if (this.lastName == null) errors.add("Last Name is a required field");
            if (this.displayName == null) errors.add("Display Name is a required field");
            if (this.email == null) errors.add("Your email must be a valid address, e.g. yourname@domain.com");
            if (this.username == null) errors.add("Login Name is a required field");
            if (this.address.getAddress1() == null) errors.add("Address is a required field");
            if (this.address.getCity() == null) errors.add("City is a required field");
            if (this.address.getState() == null) errors.add("State/Province is a required field");
            if (this.address.getCountry() == null) errors.add("Country is a required field");
            if (this.address.getOfficePhone() == null) errors.add("Work Phone Number is a required field");
            /*if (getLanguageCode() == null) {
            errors.add("Language is a required field");
            } */
            if (getTimeZoneCode() == null) {
                errors.add("Time Zone is a required field");
            }
            if (getLocaleCode() == null) {
                errors.add("Locale is a required field");
            }
            /* if (getDateFormatID() == null) {
            errors.add("Date Format is a required field");
            }
            if (getTimeFormatID() == null) {
            errors.add("Time Format is a required field");
            }
            */
            if (errors.size() > 0) {
                errors.add(1, "You have not filled in all required registration fields");
            }

        }
        return errors;
    }


    /**
     * Generates a secure (non-guessable) verification code in this bean.
     * @see net.project.security.Crypto#generateVerificationCode
     */
    public void generateVerificationCode() {
        setVerificationCode(Crypto.generateVerificationCode(DEFAULT_VERIFICATION_CODE_SIZE));
    }


    /**
     * Register user as an active user of the application.
     * The method first determines whether or not the user is a stub user or a
     * brand new user, then registers the user accordingly.  This method is not
     * sufficient to complete registration.
     * @exception RegistrationException
     *                   If any part of the registration process is not valid or not successful.
     * @since Gecko
     * @see RegistrationManager#register
     */
    void createPerson() throws RegistrationException {

        DBBean db = new DBBean();

        try {

            // first, set autocommit to false so we can have a singletransaction
            db.setAutoCommit(false);
            createPerson(db);
            db.commit();

        } catch (SQLException sqle) {

            try {
                db.rollback();
            } catch (SQLException sqle2) {
            	Logger.getLogger(RegistrationBean.class).debug("RegistrationBean threw an SQLE when trying to rollback the DB connection: " + sqle2);
            }
            throw new RegistrationException("Registration.createPerson(): User registration failed due to a database exception (sqle).  Please try again." + sqle, sqle);

        } catch (PersistenceException pe) {

            try {
                db.rollback();
            } catch (SQLException sqle2) {
            	Logger.getLogger(RegistrationBean.class).debug("RegistrationBean threw an SQLE when trying to rollback the DB connection: " + sqle2);
            }
            throw new RegistrationException("Registration.createPerson(): User registration failed due to a database exception (pe).  Please try again. " + pe, pe);

        } finally {
            db.release();

        }

    }

    /**
     * Registers the user using the current connection in the specified DBBean.
     * <b>Note:</b> No commit/rollback/release is performed.
     * @param db the DBBean in which to perform the transaction
     * @throws SQLException if there is a problem registering in the database
     * @precondition db has an open connection with <code>autoCommit</code> <code>false</code>
     * @postcondition db has uncommited transactions
     */
    void createPerson(DBBean db)
            throws SQLException, PersistenceException {

        // if this is profile only registration...
        // create the stub user
        // otherwise, create the full user
        if (isStubUser()) {
            createPersonProfile(db);

        } else {
            createFullPerson(db);
        }
    }


    /**
     * Creates a complete profile for a user -- can only be used when a stub user does not exist
     * Should probably be swapped with an IProfile instead of RegistrationBean
     * @param db
     * @throws PersistenceException if db operation fails
     * @see net.project.base.DefaultDirectory#createPerson
     * @since EMU
     */
    private void createFullPerson(DBBean db) throws PersistenceException {
        net.project.base.DefaultDirectory.createPerson(this, db);
    }


    /**
     * Creates a profile for a user.
     * Should probably be swapped with an IProfile instead of RegistrationBean
     * @param db
     * @exception PersistenceException if db operation fails
     * @see net.project.base.DefaultDirectory#createPersonProfile
     * @since EMU
     */
    private void createPersonProfile(DBBean db) throws PersistenceException {
        net.project.base.DefaultDirectory.createPersonProfile(this, db);
    }


    /**
     * Send an email message to the registrant containing the validation code and URL to complete the registration process.
     * @throws NotificationException if there is a problem sending the
     * email
     */
    public void sendVerificationEmail() throws NotificationException {

        RegistrationNotification regNote = new RegistrationNotification();

        regNote.initialize(this);

        try {
            regNote.post();

        } catch (NotificationException ne) {

            if (ne.getReasonCode() == Notification.NOTIFICATION_SEND_FAILED_EXCEPTION) {
            	Logger.getLogger(RegistrationBean.class).info("Notification failed: Invalid Address");
            } else if (ne.getReasonCode() == Notification.NOTIFICATION_MESSAGING_EXCEPTION) {
            	Logger.getLogger(RegistrationBean.class).error("Notification failed: Invalid Address.  " +
                        regNote.getNotificationXML());
            }

            // after logging, retrhow the error
            throw ne;
        }
    }
    /**BFD2062 added one function to send varification mail to alternate
     * emailid provided by user
     * Send an email message to the registrant containing the validation code and URL to complete the registration process.
     * @throws NotificationException if there is a problem sending the
     * email
     */
    public void sendVerificationEmailForAlternateEmail(String email) throws NotificationException {

        RegistrationNotification regNote = new RegistrationNotification();
       
        this.generateVerificationCode();
        regNote.initialize(this,email);

        try {
            regNote.post();

        } catch (NotificationException ne) {

            if (ne.getReasonCode() == Notification.NOTIFICATION_SEND_FAILED_EXCEPTION) {
            	Logger.getLogger(RegistrationBean.class).info("Notification failed: Invalid Address");
            } else if (ne.getReasonCode() == Notification.NOTIFICATION_MESSAGING_EXCEPTION) {
            	Logger.getLogger(RegistrationBean.class).error("Notification failed: Invalid Address.  " +
                        regNote.getNotificationXML());
            }

            // after logging, retrhow the error
            throw ne;
        }
    }

    /**
     * Make a single entry in the user login history.
     * An entry is required for all registered users in order for the user to be able to
     * login to the application.
     *
     * @return true if the log entry was successfully created; false otherwise
     */
    public boolean logUserHistory() {
        boolean historyLoggingSuccessful = false;

        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            db.openConnection();

            historyLoggingSuccessful = logUserHistory(db);


            db.commit();
        } catch (SQLException sqle) {
        	Logger.getLogger(RegistrationBean.class).error("SQL exception: " + sqle);
            try {
                db.rollback();
            } catch (SQLException e) {
            	Logger.getLogger(RegistrationBean.class).debug("Fatal error: unable to roll back transaction.");
            }
        } finally {
            db.release();
        }

        return historyLoggingSuccessful;
    }

    /**
     * Make a single entry in the user login history.
     * An entry is required for all registered users in order for the user to be able to
     * login to the application.
     *
     * @return true if the log entry was successfully created; false otherwise
     */
    public boolean logUserHistory(DBBean db) throws SQLException {
        int personStatus = -1;
        String userID = this.getID();

        int index = 0;
        int personStatusIndex = 0;
        db.prepareCall("{call profile.LOG_HISTORY(?,?,?)}");
        db.cstmt.setInt(++index, new Integer(userID).intValue());
        db.cstmt.setString(++index, this.username);
        db.cstmt.registerOutParameter((personStatusIndex = ++index), java.sql.Types.INTEGER);
        db.executeCallable();

        personStatus = db.cstmt.getInt(personStatusIndex);

        // Log successful if status is 0
        return (personStatus == 0);
    }


    /**
     * Indicates whether the user represented by this registration bean
     * is registering as a result of receiving an invitation to a workspace.
     * @return true if the user represented by this registration bean
     * has at least one invitation to any space that has not yet been accepted;
     * false if the user has no invitations that are not yet accepted.
     */
    public boolean isInvited() {

        DBBean db = new DBBean();

        try {
            int index = 0;
            db.prepareStatement("select invited_status from pn_invited_users  where invited_status = 'Invited' and person_id = ?");

            db.pstmt.setString(++index, getID());

            db.executePrepared();

            // if pn_person entry exists and user_status is "Invited", return true
            return (db.result.next());
        } catch (SQLException sqle) {
        	Logger.getLogger(RegistrationBean.class).error("RegistrationBean.java: SQL Exception sqle");
            return false;
        } finally {
            db.release();
        }
    }


    /**
     * Creates assignments for all outstanding invitations to workspaces.
     * This is called after verification of registration to create assignments
     * for all invitations for a user.
     * <p>
     * Assumes email address is available in this registration bean. <br>
     * After calling, zero or more assignemtns will have been created
     * for the user.
     * </p>
     * @param userid the ID of the person who has just registered to whom
     * the assignemtns should belong
     */
    public void checkUserInvitations(String userid) {
        // talk to mike about meeting invitations

        DBBean db = new DBBean();

        try {
            db.setQuery("select space_id, invitor_id from pn_invited_users where person_id =" + DBFormat.varchar2(userid));
            db.executeQuery();
            while (db.result.next()) {
                SpaceAssignment inviteAssignment = new SpaceAssignment();

                // Assignments created for an invitation to a space are
                // listed under the user's personal space
                // They cannot be listed under the space to which the invitation
                // pertains, because the user won't see them until they are
                // a member of that space -- and that is the whole point of
                // this assignment
                inviteAssignment.setSpaceID(userid);
                inviteAssignment.setPersonID(userid);
                // set object id of the workspace; the assignment pertains
                // to the workspace
                inviteAssignment.setObjectID(db.result.getString(1));
                // add the assignor
                inviteAssignment.setAssignorID(db.result.getString(2));
                // hard coded to be status assigned;
                inviteAssignment.setStatus(AssignmentStatus.ASSIGNED);
                inviteAssignment.store();
            }
        } catch (PersistenceException pe) {
        	Logger.getLogger(RegistrationBean.class).error("Unable to store assignment");
        } catch (SQLException sqle) {
        	Logger.getLogger(RegistrationBean.class).error("SQL exception: " + sqle);
        } finally {
            db.release();
        }
    }


    /** clear the properties of this registration. */
    public void clear() {
        super.clear();
        licenseContext = null;
        isStarted = false;
        directoryEntry = null;
        userStored = false;
        isStubUser = false;
    }

    public void reset() {
        clear();
        init();
    }

    /**
     Updated the invited user using the information in this registration.
     Registration data is held held in memory until this method is called.
     All data validation rules must checked before calling this method.
     @throws RegistrationException if a new user can not be updated from the registation information.
     */
    public void updateInvitedUser()
            throws RegistrationException {

        DBBean db = new DBBean();

        try {
            updateInvitedUser(db);
        } finally {
            db.release();
        }
    }

    /**
     * Updated the invited user using the information in this registration.
     * Registration data is held held in memory until this method is called.
     * All data validation rules must checked before calling this method.
     * @param dbean the database bean
     * @exception RegistrationException
     *                   if a new user can not be updated from the registation information.
     */
    public void updateInvitedUser(DBBean dbean) throws RegistrationException {

        try {

            this.address.store(dbean);
            super.store(dbean);

        } catch (PersistenceException pe) {
            throw new RegistrationException("Could not update your registration information in the database. Contact Administrator." + pe);
        }

    }


    /**
     * Update the address record.
     * @deprecated as of 7.5.1; no replacement
     * This method is unused and unsupported. It will be removed in a future
     * release.
     */
    public void storeAddress()
            throws RegistrationException {
        try {
            this.address.store();
        } catch (PersistenceException pe) {
            throw new RegistrationException("Could not update your address information in the database. Contact Administrator.");
        }
    }


    /**
     * Loads this registration bean using the current id.
     * <p>
     * Invokes superclass load() method.
     * Also loads the directory entry from the directory specified by
     * the user's domain.
     * </p>
     * @throws PersistenceException if there is a problem loading
     * @throws NullPointerException if the current ID is null
     */
    public void load() throws PersistenceException {
        if (getID() == null) {
            throw new NullPointerException("No id available to load registration");
        }

        try {
            // Loads all information about this user
            // Actually throws a PersistenceException if the user is
            // not found
            super.load();

            // Now load directory entry for user
            loadDirectoryEntry();
        } catch (DirectoryException e) {
        	Logger.getLogger(RegistrationBean.class).error("RegistrationBean.load: unable to load directory entry for user: " + e);
            throw new PersistenceException("Registration load operation failed: unable to load directory entry", e);
        }
    }

    public void loadLocalInformation() throws PersistenceException {
        super.load();
    }

    /**
     * Load properties from persistent storage based on the specified
     * email address.
     * <p>
     * Invokes superclass load() method.
     * Also loads the directory entry from the directory specified by
     * the user's domain.
     * </p>
     * @throws PersistenceException if there is a problem loading the user
     * @throws NullPointerException if the specified email address is null
     */
    public void loadForEmail(String email) throws PersistenceException {

        if (email == null) {
            throw new NullPointerException("User load operation failed; missing email");
        }

        try {
            // Loads all information about this user
            super.loadForEmail(email);

            if (isLoaded()) {
                // TODO Problem occurs when loaded thing has no PN_USER_RECORD = missing domainID
                // Next method call barfs
                // Trying to determine if this is bad data?
                // Now load directory entry for user
                loadDirectoryEntry();
            }


        } catch (DirectoryException e) {
        	Logger.getLogger(RegistrationBean.class).error("RegistrationBean.load: unable to load directory entry for email " + email + ": " + e);
            throw new PersistenceException("Registration load operation failed: unable to load directory entry", e);

        }

    }

    /**
     * Updates this Registration bean.
     * This stores the changes to the profile and updates the
     * directory-specific entry in the directory.
     * @throws RegistrationException if there is an error completing the
     * registration, such as a database error or encryption error
     */
    public RegistrationResult updateRegistration() throws RegistrationException {
        DBBean dbean = new DBBean();

        try {
            return updateRegistration(dbean);
        } finally {
            dbean.release();
        }
    }


    /**
     * Updates this Registration bean.
     * This stores the changes to the profile and updates the
     * directory-specific entry in the directory.
     * @param dbean the database bean
     * @throws RegistrationException if there is an error completing the
     * registration, such as a database error or encryption error
     */
    public RegistrationResult updateRegistration(DBBean dbean) throws RegistrationException {

        // Currently, result is always success
        RegistrationResult result = new RegistrationResult();

        RegistrationManager registrationManager = new RegistrationManager(this);
        registrationManager.updateRegistration(dbean);

        return result;
    }


    /**
     * Checks that the current login and email address are available.
     * <p>
     * The email address is unavailable if any registered person has
     * it; if an unregistered person has it, that "stub person" is
     * loaded and the email address <i>is available</i>.
     * Deleted persons are ignored.
     * </p>
     * <p>
     * The login is unavailable if any user associated with the current
     * domain already has it.
     * Deleted and unregistered users are ignored.
     * </p>
     * @return the result where <code>isSuccess</code> is false if
     * unavailable, with appropriate error messages specified
     * @throws RegistrationException if there is a problem checking
     * availability; including a problem loading a stub user
     */
    public RegistrationResult checkAvailability()
            throws RegistrationException {

        RegistrationResult result = new RegistrationResult();

        // First check email address

        try {

            if (isUpdating()) {
                // This Registration information is being updated for an existing
                // person, generally through a Profile modification or
                // Domain Migration

                // Check to see if the email address being registered
                // is in use by a person other than the current person
                boolean isEmailExisting = net.project.base.DefaultDirectory.checkEmailExistsExceptSpecified(getEmail(), getID());

                if (isEmailExisting) {

                    // The Email address is in use by a person other than
                    // the current person
                    // Now we check to see if the other person is a fully
                    // Reigstered person, or simply a "stub" person who
                    // has been invited to workspace but never registered

                    if (isUserRegistered(getEmail())) {
                        // Email in use by registered user
                        // NOT available
                        result.addError("email", "The email address has already been registered.  Email addresses can only be registered once.");

                    } else {
                        // The email address is in use by a Stub person
                        // Since we are updating an existing registered user,
                        // we cannot simply load the person stub since that
                        // is essentially a different person
                        // Loading the stub would overwrite the current ID

                        // 09/12/2002 - Tim
                        // Ideally we would delete the person stub since
                        // the current person is now laying claim to that
                        // email address
                        // Since this is a software patch, we will NOT be doing
                        // that at this time
                        // The consideration would be:  What is there to delete
                        // for a stub person, project admins would get confused
                        // as to why their stub persons are deleted; the current
                        // person would have to be re-invited to the project etc.

                        result.addError("email", "The email address is in already in use.  Please contact your Application Administrator for further information.");

                    }

                } else {
                    // The Email address is not in use
                    // No errors are added

                }

            } else {
                // This Registration information is for a new person, generally
                // through the Registration process

                // Check to see if the email address is being used by any
                // person
                boolean isEmailExisting = checkEmailExists(getEmail());

                if (isEmailExisting) {

                    // The Email address is in use by another person
                    // Now we check to see if the other person is a fully
                    // Reigstered person, or simply a "stub" person who
                    // has been invited to workspace but never registered

                    if (isUserRegistered(getEmail())) {
                        // Email in use by registered user
                        // NOT available
                        result.addError("email", PropertyProvider.get("prm.registration.error.emailaddressalreadyregistered.message")); // "The email address has already been registered.  Email addresses can only be registered once."

                    } else {
                        // Email in use by stub person
                        // Since the stub person is not registered we
                        // assume that the current registration is on order
                        // to "become" the stub person
                        // Therefore, we load the stub person's information
                        // This will give the current Registration object
                        // a valid ID
                        populateIDForUserStub();

                    }

                } else {
                    // The Email address is not in use
                    // No errors are added

                }

            }


        } catch (PersistenceException pe) {
        	Logger.getLogger(RegistrationBean.class).error("RegistrationBean.checkAvailability() operation failed: " + pe);
            throw new RegistrationException("There was a problem checking the availability of the email address and username: " + pe, pe);

        }

        // Now check username
        // We perform this check regardless of whether any email errors have
        // occurred
        if (!isUsernameAvailable()) {
            // Username NOT available
            result.addError("login", PropertyProvider.get("prm.registration.error.usernamealreadyregistered.message")); // "The username has already been registered."

        }

        return result;
    }


    //
    // Licensing Stuff
    //

    private void setLicenseContext(LicenseContext context) {
        this.licenseContext = context;
    }

    public LicenseContext getLicenseContext() {
        return this.licenseContext;
    }

    /**
     * Sets the locale code for the user.  Also looks up a locale for the user
     * if one exists that matches the locale code.
     *
     * @param localeCode a <code>String</code> containing the code for the
     * locale that should be associated for this user.
     */
    public void setLocaleCode(String localeCode) {
        super.setLocaleCode(localeCode);

        if (!Validator.isBlankOrNull(localeCode)) {
            try {
                locale = LocaleProvider.getLocale(localeCode);
            } catch (net.project.util.InvalidLocaleException ile) {
                //Do nothing for now.
            }
        }
    }

    /**
     * The validate enforces business rules for usernames in our database.  These
     * rules currently include:
     *
     * 1. The primary email address cannot be blank.
     * 2. No two users in PRM can have the same email address.
     *
     * @see #validate()
     * @throws RegistrationBusinessRuleException if the data in this object does
     * not properly adhere to the business rules.
     * @throws PersistenceException if there is a database error when trying to
     * validate the information supplied in this object.  (For example, if we
     * are trying to verify that someone else in the system doesn't have the same
     * email address.)
     */
    public void validate() throws RegistrationBusinessRuleException, PersistenceException {
        // Determine if the user is an ldap user.
        boolean isLDAPUser = getUserDomain().getDirectoryProviderType().getName().equalsIgnoreCase("LDAP");
        String ruleTokenPrefix = (isLDAPUser ? "prm.global.registration.businessrules.ldap." : "prm.global.registration.businessrules.ldap.native.");

        //Array to store all business rule violations we encounter
        ArrayList businessRuleViolations = new ArrayList();

        //First, check that the user is specifying an email address (it cannot be empty)
        if ((this.getEmail() == null) || (this.getEmail().trim().equals(""))) {
            businessRuleViolations.add(new RegistrationBusinessRuleViolation(
                    PropertyProvider.get(ruleTokenPrefix + "rule1.text"),
                    "ecom_ShipTo_Online_Email"));
        }

        //Now check that none of the email addresses specified already match email
        //addresses being used by other users.
        if ((this.getEmail() != null) && (this.checkEmailExists(this.getEmail()))) {
            businessRuleViolations.add(new RegistrationBusinessRuleViolation(
                    PropertyProvider.get(ruleTokenPrefix + "rule2.text"),
                    "ecom_ShipTo_Online_Email"));
        }
        if ((this.getAlternateEmail1() != null) && (!this.getAlternateEmail1().trim().equals("")) &&
                (this.checkEmailExists(this.getAlternateEmail1()))) {
            businessRuleViolations.add(new RegistrationBusinessRuleViolation(
                    PropertyProvider.get(ruleTokenPrefix + "rule3.text"),
                    "alternateEmail1"));
        }
        if ((this.getAlternateEmail2() != null) && (!this.getAlternateEmail2().trim().equals("")) &&
                (this.checkEmailExists(this.getAlternateEmail2()))) {
            businessRuleViolations.add(new RegistrationBusinessRuleViolation(
                    PropertyProvider.get(ruleTokenPrefix + "rule4.text"),
                    "alternateEmail2"));
        }
        if ((this.getAlternateEmail3() != null) && (!this.getAlternateEmail3().trim().equals("")) &&
                (this.checkEmailExists(this.getAlternateEmail3()))) {
            businessRuleViolations.add(new RegistrationBusinessRuleViolation(
                    PropertyProvider.get(ruleTokenPrefix + "rule5.text"),
                    "alternateEmail3"));
        }

        //If any business rule violations were found, throw an exception
        if (businessRuleViolations.size() > 0) {
            throw new RegistrationBusinessRuleException(businessRuleViolations);
        }
    }

    

    /**
     * This method allows an LDAP user to change their username.  Before calling
     * this method, you must set the ID of the user using the {@link #setID} method.
     *
     * Typically, we only allow this function to application administrators.  Historically,
     * this was primarily for ease-of-use reasons.  If the change of LDAP username
     * was integrated into the login process (without introducing security problems)
     * it might be appropriate to introduce this functionality to users too.
     *
     * @param newUsername a <code>String</code> value containing the username that
     * we are going to assign to this user.
     */
    public void changeLDAPUsername(String newUsername) throws PersistenceException, RegistrationBusinessRuleException, DirectoryException {
        //Store the new username internally
        this.setLogin(newUsername);
        //Make sure that this update won't us to fail any of our business rules,
        //such as there being two users with the same email address
        this.loadDirectoryEntry();
        this.populateFromDirectoryEntry();
        this.setUpdating(true);
        this.validate();

        //Store the new username in the database
        this.store();
    }

    /**
     * Determine if the user with this email address has a license.  This will
     * only occur if a user has been invited and someone (probably in the
     * app space) associated a license with their person stub.
     *
     * @return a <code>boolean</code> value indicating if this user with this
     * email address is already license.  This will only look up users who
     * are currently unregistered
     */
    public boolean isRegisteringUserLicensed() throws SQLException {
        DBBean db = new DBBean();
        boolean isLicensed = false;

        try {
            db.prepareStatement(
                    "select " +
                    "  has_license " +
                    "from " +
                    "  pn_person_view " +
                    "where " +
                    "  email = ? " +
                    "  and record_status = 'A' " +
                    "  and user_status = 'Unregistered' "
            );

            db.pstmt.setString(1, email);
            db.executePrepared();

            if (db.result.next()) {
                isLicensed = db.result.getBoolean("has_license");
            } else {
                isLicensed = false;
            }
        } finally {
            db.release();
        }

        return isLicensed;
    }


    //
    // Nested top-level classes
    //


    /**
     * Provides a collection of error messages and a status.
     * This may be used during any complex validation to indicate
     * errors occurred and provide appropriate error messages.
     */
    public static class RegistrationResult {

        /** The collection of errors. */
        private final java.util.ArrayList errors = new ArrayList();

        /** Whether results are deemed a success. */
        private boolean isSuccess = true;

        /** License created during registration. */
        private License license;

        /**
         * Indicates whether the result of registration is a success.
         * @return true if registration is a success
         */
        public boolean isSuccess() {
            return this.isSuccess;
        }

        /**
         * Flags that an error occurred without any specific message.
         * After this {@link #isSuccess} returns false
         */
        public void flagErrorOccurred() {
            this.isSuccess = false;
        }

        /**
         * Adds an error where the specified field is in error.
         * @param field the name of the field in which the error occurred
         * @param message the error message
         */
        private void addError(String field, String message) {
            this.errors.add(new ErrorEntry(field, message));
            flagErrorOccurred();
        }

        /**
         * Adds an error message without a specific field.
         * @param message the error message
         */
        private void addError(String message) {
            this.errors.add(new ErrorEntry(message));
            flagErrorOccurred();
        }

        /**
         * Adds many error messages.
         * @param messages where each element must be a <code>String</code>
         */
        private void addAllErrors(Collection messages) {
            if (messages != null && messages.size() > 0) {
                Iterator it = messages.iterator();
                while (it.hasNext()) {
                    this.errors.add(new ErrorEntry((String)it.next()));
                }
                flagErrorOccurred();
            }
        }

        /**
         * Returns the name of the first field that is in error, if any.
         * @return the name of the first field, set by {@link #addError(String, String)};
         * or null if there is no field in error
         */
        public String getFirstErrorField() {
            String firstField = null;
            ErrorEntry entry = null;

            // Iterate over all error entries looking for the
            // first one that has a field name
            Iterator it = this.errors.iterator();
            while (it.hasNext()) {
                entry = (ErrorEntry)it.next();
                if (entry.field != null) {
                    firstField = entry.field;
                    break;
                }
            }

            return firstField;
        }

        /**
         * Returns the concatenation of all error messages, separated by a
         * linebreak.
         * @return the messages
         */
        public String getErrorMessages() {
            StringBuffer messages = new StringBuffer();
            ErrorEntry entry = null;

            Iterator it = this.errors.iterator();
            while (it.hasNext()) {
                entry = (ErrorEntry)it.next();
                messages.append(entry.message);
                messages.append("\n");
            }

            return messages.toString();

        }

        /**
         * Returns the formatted error messages, with HTML special characters
         * escaped and linebreaks replaced by HTML &lt;BR&gt; tags.
         * @return the formatted error messages
         */
        public String getErrorMessagesFormatted() {
            return net.project.util.HTMLUtils.formatHtml(getErrorMessages());
        }

        private void setLicense(License license) {
            this.license = license;
        }

        public License getLicense() {
            return license;
        }

        /**
         * Provides a structure for maintaining an error field and message.
         */
        private static class ErrorEntry {
            String field = null;
            String message = null;

            ErrorEntry(String message) {
                this(null, message);
            }

            ErrorEntry(String field, String message) {
                this.field = field;
                this.message = message;
            }
        }
    }
}
