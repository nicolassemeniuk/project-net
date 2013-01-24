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

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;

/**
 * Provides convenient methods for manipulating the license associated with
 * a person.
 */
public class PersonLicense {

    /**
     * The Person who's license we are considering.
     */
    private Person person = null;

    /**
     * Creates a new PersonLicense for the current person.
     */
    public PersonLicense(Person person) {
        this.person = person;
    }

    /**
     * Returns the current person.
     * @return the current person
     */
    private Person getPerson() {
        return this.person;
    }

    /**
     * Associates the license with the current person.
     * Disassociates any existing license.  Stores the specified license
     * @param db the DBBean in which to perform the transaction
     * @param license the license to associate with the current person
     * @throws LicenseException if there is a problem acquiring a unit of
     * the license or storing the license
     * @throws PersistenceException if there is a problem disassociating current license,
     * associating the license or storing the license
     * @throws LicenseAlreadyAssociatedException if the license to which being associated
     * is same as the license to which the person is already associated
     */
    public void associate(DBBean db, License license, boolean notify)
        throws LicenseException, PersistenceException, LicenseAlreadyAssociatedException {

        try {

            // Check if the license to which the person is going to be associated
            // is not the same as the license the person currently is associated with.
            // If that is the case then throw an exception.
            //Ignore if the person is not associated to any license currently
            try {
                if (this.getCurrentLicense() != null &&
                    license.getID() != null &&
                    license.getID().equals(this.getCurrentLicense().getID())) {

                    throw new LicenseAlreadyAssociatedException("PersonLicense.java : Problem associating license: can not associate a person to the currently associated license.");
                }
            } catch (LicenseNotFoundException lnfe) {
                //Do nothing
            }
            // 1. Disassociate existing license from person
            disassociateCurrentLicense(db, notify);

            // 2. Associate new license with person

            // Acquire a single unit of the license
            license.acquire();
            // Store the modified license
            license.store(db);
            // Physically associate the new license
            associateLicense(db, license, notify);

            // Create history entry for new license
            net.project.license.LicenseHistory.createEntry(db, license, person);

        } catch (net.project.notification.NotificationException ne) {
            throw new LicenseException("PersonLicense.java : NotificationException thrown. " + ne.getMessage());

        }

    }

    /**
     * Disassociates the current person's license.
     * This will disassociate the license, relinquishing any usage
     * counts. If the there is some problem doing this, the
     * license will physically be disassociated from the person.
     * @param db the DBBean in which to perform the transaction
     * @throws PersistenceException if there is a problem disassociating
     * the current license in persistent store
     */
    public void disassociateCurrentLicense(DBBean db, boolean notify) throws PersistenceException {

        License currentLicense = null;

        // Note: In the event of a problem getting current license
        // we DO NOT abort the process.  By continuing the process, this
        // method can be used to assicate a license with a user who's existing
        // license is broken
        // The worst case scenario is that the usage counts of a broken license
        // are not updated correctly; not a significant problem
        // Additionally, no current license will be found for user's acquiring
        // their first license
        try {
            // Find the current license for the current person
            // and relinquish one unit of usage
            currentLicense = getCurrentLicense();
            currentLicense.relinquish();
            currentLicense.store(db);

        } catch (LicenseNotFoundException e) {
            // User does not have license
            // Do not abort

        } catch (LicenseKeyMismatchException e) {
            // Current license is broken
            // Do Not abort

        } catch (LicenseException e) {
            // Problem relinquishing
            // Do not abort

        }

        // Note that if this causes a problem, we cannot suppress it;
        // A problem will occur later when the new license is associated
        // Technically both operations can be accomplished by the second
        // method (that is, current license context is not needed).
        // May not hold true in the future.
        if (currentLicense != null) {
            // Physically remove their current license
            disassociateLicense(db, currentLicense);
            if (notify) {
                new LicenseNotification().notifyUserOfDissociation(currentLicense, this.getPerson());
            }

        } else {
            // Forcefully remove their current license, since we
            // couldn't find one
            disassociateLicenseForce(db);

        }

    }


    /**
     * Physically removes the specified license from the current person.
     * @param db the DBBean in which to perform the transaction
     * @param license the license to disassociate from the person
     * @throws PersistenceException if there is a problem dissassociating the license
     */
    private void disassociateLicense(DBBean db, License license) throws PersistenceException {

        try {
            StringBuffer disassociateQuery = new StringBuffer();
            disassociateQuery.append("delete from pn_person_has_license ");
            disassociateQuery.append("where person_id = ? and license_id = ? ");
            int index = 0;
            db.prepareStatement(disassociateQuery.toString());
            db.pstmt.setString(++index, getPerson().getID());
            db.pstmt.setString(++index, license.getID());
            db.executePrepared();

        } catch (SQLException sqle) {
            throw new PersistenceException("Disassociate license from person operation failed: " + sqle, sqle);

        }

    }

    /**
     * Physically removes the person's current license.
     * This is provided to force the removal of the current license, even
     * when it is not available.
     * @param db the DBBean in which to perform the transaction
     * @throws PersistenceException if there is a problem dissassociating the license
     */
    private void disassociateLicenseForce(DBBean db) throws PersistenceException {

        try {
            StringBuffer disassociateQuery = new StringBuffer();
            disassociateQuery.append("delete from pn_person_has_license ");
            disassociateQuery.append("where person_id = ? ");
            int index = 0;
            db.prepareStatement(disassociateQuery.toString());
            db.pstmt.setString(++index, getPerson().getID());
            db.executePrepared();

        } catch (SQLException sqle) {
            throw new PersistenceException("Disassociate license from person operation failed: " + sqle, sqle);

        }

    }

    /**
     * Physically associates the specified license with the current person.
     * This does not check the license models; it is purely a persistence
     * operation.
     * @param db the DBBean in which to perform the transaction
     * @param license the license to associate with the current person
     * @throws PersistenceException if there is a problem associating the license
     */
    private void associateLicense(DBBean db, License license, boolean notify)
        throws PersistenceException, net.project.notification.NotificationException {

        try {
            // Associate new license with person
            StringBuffer associateQuery = new StringBuffer();
            associateQuery.append("insert into pn_person_has_license ");
            associateQuery.append("(person_id, license_id) ");
            associateQuery.append("values (?, ?) ");
            int index = 0;
            db.prepareStatement(associateQuery.toString());
            db.pstmt.setString(++index, getPerson().getID());
            db.pstmt.setString(++index, license.getID());
            db.executePrepared();

            if (notify) {
                new LicenseNotification().notifyResponsibleUserOfAssociation(license, getPerson());
                new LicenseNotification().notifyUserOfAssociation(license, getPerson());
            }
        } catch (SQLException sqle) {
            System.out.println("PersonLicense.java : PersistenceException thrown : " + sqle.getMessage());
            throw new PersistenceException("Associate license with person operation failed: " + sqle, sqle);
        }
    }

    /**
     * Gets the license for the current person.
     * @return the license for the current person
     * @throws PersistenceException if there is a problem loading the license
     * @throws LicenseNotFoundException if license for the person could not be found
     * @throws InvalidLicenseCertificateException if the license certificate
     * could not be loaded or parsed
     * @throws LicenseKeyMismatchException if the loaded license's key doesn't
     * match its certificate key
     */
    public License getCurrentLicense()
        throws PersistenceException, LicenseNotFoundException, InvalidLicenseCertificateException, LicenseKeyMismatchException {

        License license = null;

        // Build query to get the license id for person
        StringBuffer selectQuery = new StringBuffer();
        selectQuery.append("select phl.license_id ");
        selectQuery.append("from pn_person_has_license phl ");
        selectQuery.append("where phl.person_id = ? ");

        DBBean db = new DBBean();
        try {
            int index = 0;
            db.prepareStatement(selectQuery.toString());
            db.pstmt.setString(++index, person.getID());
            db.executePrepared();

            if (db.result.next()) {
                // We found a licenseID for the person
                String licenseID = db.result.getString("license_id");

                // Now load the license
                license = new License();
                license.setID(licenseID);
                license.load();

            } else {
                // No license for person
                throw new LicenseNotFoundException("License not found for person");

            }

        } catch (SQLException sqle) {
            throw new PersistenceException("License load operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

        return license;
    }

}
