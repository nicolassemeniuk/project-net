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
package net.project.license;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;

/**
 * Provides facilities for creating and checking licenses.
 */
public class LicenseManager implements java.io.Serializable {
    /**
     * Creates a new LicenseManager.
     */
    public LicenseManager() {
        // DO nothing
    }


    /**
     * Checks that the license key for the display key is valid and may be
     * used by an additional user.
     * This is useful when a user wishes to acquire a license by a display key;
     * they may do so only if the license can accomodate an additional user
     * @param displayLicenseKey the license key to check
     * @return the status indicating whether the license exists, is available etc.
     * @throws InvalidLicenseKeyException if the license key does not represent
     * a valid key; the absence of this exception does NOT mean that the license
     * key is actually present in the system or available for use (that information
     * is given by the return value)
     * @throws LicenseException if there is a problem loading the license master properties
     */
    public LicenseResult checkLicenseAvailableForUseForDisplayKey(String displayLicenseKey)
            throws InvalidLicenseKeyException, LicenseException, PersistenceException {

        // Construct a license key from the display license key
        // and check that the license exists and is available for use
        return checkLicenseAvailableForUse(LicenseKey.createLicenseKey(displayLicenseKey));
    }

    /**
     * Checks that the license result for the specified person.
     * This is useful for checking if a person may login; it checks that the license
     * is current ; it does not check the usage limits
     * @throws LicenseException
     * @return the status indicating whether the person has a license, is current etc.
     */
    public LicenseResult checkLicenseResult(Person person)
            throws LicenseException, PersistenceException {
        LicenseResult licenseResult = null;
        try {
            // Fetch the license for the person
            License license = new PersonLicense(person).getCurrentLicense();
            licenseResult = checkLicenseIsCurrent(license);

        } catch (PersistenceException pe) {
            licenseResult = new LicenseResult(LicenseResultCode.FAILURE, "A problem occurred fetching a license for a key");

        } catch (LicenseNotFoundException e) {
            licenseResult = new LicenseResult(LicenseResultCode.MISSING, PropertyProvider.get("prm.license.manager.checklicenseresult.missingerror.message"));//"License not found for key"

        } catch (LicenseKeyMismatchException e) {
            licenseResult = new LicenseResult(LicenseResultCode.CERTIFICATE_KEY_MISMATCH, "License found for key was not created with that key");

        }
        return licenseResult;
    }

    /**
     * Checks that the license status for the specified person.
     * This is useful for checking if a person may login; it checks that the license
     * has not been disabled; it does not check the usage limits
     * @return the status indicating whether the person has a license, is not disabled etc.
     * this method seems to return null if there is a problem loading
     * the license
     */
    public LicenseStatus checkLicenseStatus(Person person) {
        LicenseStatus licenseStatus = null;

        try {
            // Fetch the license for the person
            License license = new PersonLicense(person).getCurrentLicense();
            if (license != null) {
                if (license.getStatus() != null) {
                    licenseStatus = license.getStatus();

                } else {
                    licenseStatus = new LicenseStatus(LicenseStatusCode.ENABLED);
                }
            }

        } catch (PersistenceException pe) {
            //licenseStatus = new LicenseResult(LicenseResultCode.FAILURE, "A problem occurred fetching a license for a key");

        } catch (LicenseNotFoundException e) {
            //licenseResult = new LicenseResult(LicenseResultCode.MISSING, "License not found for key");

        } catch (InvalidLicenseCertificateException e) {
            // Do nothing
            // Based on other catch blocks failing to do anything; actual reasons unknown

        } catch (LicenseKeyMismatchException e) {
            //licenseResult = new LicenseResult(LicenseResultCode.CERTIFICATE_KEY_MISMATCH, "License found for key was not created with that key");

        }
        return licenseStatus;
    }

    /**
     * Checks that the license status for the specified License key String.
     *
     * @param displayLicenseKey
     * @return the status indicating whether the License Key has a license, is not disabled etc.
     * @throws PersistenceException if some database error occurs while
     * loading a license
     * @throws LicenseKeyMismatchException if the license found for the key
     * had a different key inside its certificate; the license has been modified
     * by an external mechanism
     * @throws InvalidLicenseKeyException if the key was not recognized as
     * a license key
     * @throws LicenseNotFoundException if no license is found with the
     * specified key
     * @throws InvalidLicenseCertificateException if there is a problem loading
     * or parsing the certificate for the license
     */
    public LicenseStatus checkLicenseStatusForDisplayKey(String displayLicenseKey)
            throws LicenseKeyMismatchException, InvalidLicenseKeyException, PersistenceException, LicenseNotFoundException, InvalidLicenseCertificateException {

        LicenseKey key = LicenseKey.createLicenseKey(displayLicenseKey);
        License license = getLicense(key);
        return license.getStatus();
    }

    /**
     * Checks that the license for the specified person is current.
     * This is useful for checking if a person may login; it checks that the license
     * has not expired; it does not check the usage limits
     * @throws LicenseException
     * @return the status indicating whether the person has a license, is current etc.
     */
    public LicenseResult checkLicenseIsCurrent(Person person)
            throws LicenseException, PersistenceException {
        LicenseResult licenseResult = null;

        try {
            // Fetch the license for the person
            License license = new PersonLicense(person).getCurrentLicense();
            licenseResult = checkLicenseIsCurrent(license);

        } catch (PersistenceException pe) {
            licenseResult = new LicenseResult(LicenseResultCode.FAILURE, "A problem occurred fetching a license for a key");

        } catch (LicenseNotFoundException e) {
            licenseResult = new LicenseResult(LicenseResultCode.MISSING, "License not found for key");

        } catch (LicenseKeyMismatchException e) {
            licenseResult = new LicenseResult(LicenseResultCode.CERTIFICATE_KEY_MISMATCH, "License found for key was not created with that key");

        }

        return licenseResult;
    }

    /**
     * Gets the license for the specified display key.
     * Does not check any constraints.
     * @param displayLicenseKey the display key
     * @throws InvalidLicenseKeyException if there display key is invalid
     * @throws LicenseNotFoundException if no license can be found for the key
     * @throws InvalidLicenseCertificateException if there is a problem loading
     * or parsing the certificate for the license
     * @throws LicenseKeyMismatchException if the loaded license's key doesn't
     * match its certificate key
     */
    public License getLicenseForDisplayKey(String displayLicenseKey)
            throws InvalidLicenseKeyException, LicenseNotFoundException, InvalidLicenseCertificateException, LicenseKeyMismatchException {

        License license = null;

        try {
            // Construct a license key from the display license key
            license = getLicense(LicenseKey.createLicenseKey(displayLicenseKey));

        } catch (PersistenceException pe) {
            throw new LicenseNotFoundException("A problem occurred finding a license");

        }

        return license;
    }

    /**
     * Disassociates the current license from the specified person.
     * @param person the Person
     * @see #disassociateCurrentLicense(DBBean, Person, boolean)
     */
    public void disassociateCurrentLicense(Person person)
            throws PersistenceException {

        DBBean db = new DBBean();

        try {
            db.setAutoCommit(false);
            disassociateCurrentLicense(db, person, true);
            db.commit();

        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("Disassociate license operation failed:" + sqle, sqle);

        } finally {
            try {
                db.rollback();
            } catch (java.sql.SQLException sqle) {
                // Simply release
            }

            db.release();
        }

    }

    /**
     * Disassociates the current license from the specified person.
     * Does NOT commit or rollback.
     * @param db the DBBean in which to perform the transaction
     * @param person the Person for whom to remove their current license;
     * the license is relinquished.  If there is a problem finding
     * their current license or relinquishing it, the person has
     * their current license forcefully removed.
     * @param isNotificationRequired true if a notification should be sent;
     * false if no notification is required
     * @throws PersistenceException if there is a problem disassociating
     * their license in persistent store
     * @see PersonLicense#disassociateCurrentLicense
     */
    public void disassociateCurrentLicense(DBBean db, Person person, boolean isNotificationRequired)
            throws PersistenceException {

        // Delegate to PersonLicense object
        PersonLicense personLicense = new PersonLicense(person);
        personLicense.disassociateCurrentLicense(db, isNotificationRequired);

    }


    /**
     * Indicates whether the license for the specified key is available for use.
     * @param key
     */
    private LicenseResult checkLicenseAvailableForUse(LicenseKey key)
            throws LicenseException, PersistenceException {

        LicenseResult licenseResult = null;

        try {
            License license = getLicense(key);
            licenseResult = checkLicenseAvailableForUse(license);

        } catch (PersistenceException pe) {
            // Some error occurred fetch license for key
            licenseResult = new LicenseResult(LicenseResultCode.FAILURE, "A problem occurred fetching a license for a key");

        } catch (LicenseNotFoundException e) {
            // Couldn't find license for key
            licenseResult = new LicenseResult(LicenseResultCode.MISSING, "License not found for key");

        } catch (LicenseKeyMismatchException e) {
            // Key stored in license mismatches key in certificate
            licenseResult = new LicenseResult(LicenseResultCode.CERTIFICATE_KEY_MISMATCH, "License found for key was not created with that key");
        }

        return licenseResult;
    }


    /**
     * Checks that the license is available for use by an additional person.
     * Checks the license models to ensure that none would be exceeded by
     * an additional use of the license.
     * @param license the license to check
     * @throws LicenseException
     * @return the status indicating the result
     */
    private LicenseResult checkLicenseAvailableForUse(License license)
            throws LicenseException, PersistenceException {

        LicenseResult licenseResult = null;

        CheckStatus checkStatus = license.checkAvailableForUse();
        if (checkStatus.booleanValue()) {
            // It is ok, no constraints met
            licenseResult = new LicenseResult(LicenseResultCode.VALID);

        } else {
            // One or more constraints met, would be exceeded if we were to
            // use the license

            licenseResult = new LicenseResult(LicenseResultCode.CONSTRAINT_EXCEEDED, checkStatus.getMessage());
        }

        return licenseResult;
    }


    /**
     * Checks that the license is current.
     * Checks the license models to ensure that they are currently valid.
     * @param license the license to check
     * @throws LicenseException
     * @return the status code indicating the result
     */
    private LicenseResult checkLicenseIsCurrent(License license)
            throws LicenseException, PersistenceException {

        LicenseResult licenseResult = null;

        CheckStatus checkStatus = license.checkCurrent();
        if (checkStatus.booleanValue()) {
            // OK, no constraints exceeded
            licenseResult = new LicenseResult(LicenseResultCode.VALID);

        } else {
            // Constraints exceeded
            licenseResult = new LicenseResult(LicenseResultCode.CONSTRAINT_EXCEEDED, checkStatus.getMessage());

        }

        return licenseResult;
    }


    /**
     * Gets the license for the specified key.
     * @param key the license key for which to load the license
     * @throws PersistenceException if there is a problem loading the license
     * @throws LicenseNotFoundException if no license can be found for the key
     * @throws InvalidLicenseCertificateException if there is a problem loading
     * or parsing the certificate for the license
     * @throws LicenseKeyMismatchException if the loaded license's key doesn't
     * match its certificate key
     */
    private License getLicense(LicenseKey key)
            throws PersistenceException, LicenseNotFoundException, InvalidLicenseCertificateException, LicenseKeyMismatchException {

        License license = new License();
        license.load(key);
        return license;
    }

    /**
     * Indicates if a user is responsible for atleast a license.
     * @param user the user for which to determine the license responsibility
     * @throws PersistenceException if there is a problem loading the license
     * @return true if the user is responsible for atleast one license, false otherwise
     */
    public static boolean isUserResponsibleForLicense(net.project.security.User user)
            throws PersistenceException {

        String userID = user.getID();
        DBBean db = new DBBean();
        int index = 0;
        String query = "select 1 from pn_license where responsible_user_id = ?";

        try {
            db.prepareStatement(query);
            db.pstmt.setString(++index, userID);
            db.executePrepared();

            if (db.result.next()) {
                return true;
            } else {
                return false;
            }
        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("Is user responsible for license operation failed:" + sqle, sqle);

        } finally {
            try {
                db.rollback();
            } catch (java.sql.SQLException sqle) {
                // Simply release
            }

            db.release();
        }
    }

}
