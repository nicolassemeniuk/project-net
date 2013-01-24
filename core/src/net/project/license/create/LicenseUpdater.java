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
package net.project.license.create;

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.license.License;
import net.project.license.LicenseException;
import net.project.license.LicenseManager;
import net.project.license.LicenseResult;
import net.project.license.LicenseStatus;
import net.project.license.system.LicenseProperties;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;
import net.project.security.User;

import org.apache.log4j.Logger;

/**
 * Provides methods for checking and updating licenses.
 */
public class LicenseUpdater {
    /** The current user who's license to check. */
    private User currentUser = null;

    /** The last license check Result. */
    private LicenseResult licenseResult = null;

    /** The last license check status. */
    private LicenseStatus licenseStatus = null;

    /** The license context used to complete licensing. */
    private LicenseContext licenseContext = null;

    /**
     * The current license properties.
     */
    private LicenseProperties licenseProps = null;

    /**
     * If we are going to create a new license, this is the person who will
     * have purchased that license.
     */
    private Person purchaser;

    /**
     * Get the license created by this process.
     */
    private License license;

    /**
     * Creates an empty LicenseUpdater.
     */
    public LicenseUpdater() {
        // Do nothing
    }

    /**
     * Sets the license properties to use.
     * @param licenseProps the license properties
     */
    public void setLicenseProperties(LicenseProperties licenseProps) {
        this.licenseProps = licenseProps;
    }

    /**
     * Sets the current user.
     * @param user the user who's license to check
     * @see #getUser
     */
    public void setUser(User user) {
        this.currentUser = user;
    }

    /**
     * Returns the current user who's license is being checked.
     * @return the current user
     * @see #setUser
     */
    public User getUser() {
        return this.currentUser;
    }

    /**
     * Get the person who purchased the license.
     *
     * @return a <code>Person</code> object which represents the person who
     * purchased the license.
     */
    public Person getPurchaser() {
        return purchaser;
    }

    /**
     * Set the person who purchased the license.
     *
     * @param purchaser a <code>Person</code> object which represents the person
     * who purchased the license.
     */
    public void setPurchaser(Person purchaser) {
        this.purchaser = purchaser;
    }

    public void setResult(LicenseResult result) {
        this.licenseResult = result;
    }

    public LicenseResult getResult() {
        return this.licenseResult;
    }

    public void setStatus(LicenseStatus status) {
        this.licenseStatus = status;
    }

    public LicenseStatus getStatus() {
        return this.licenseStatus;
    }

    /**
     * Get the new license that was created during the license update process
     * for this user.
     *
     * @return a <code>License</code> object which was created when {@link #updateLicense}
     * was called.
     */
    public License getLicense() {
        return license;
    }

    /**
     * Sets the license context to use to update the user's license.
     * @param licenseContext the license context
     */
    public void setLicenseContext(LicenseContext licenseContext) {
        this.licenseContext = licenseContext;
    }

    /**
     * Checks the license for the current user is current.
     * @throws LicenseException
     * @return the result of checking the license
     * @see net.project.license.LicenseManager#checkLicenseIsCurrent
     */
    public LicenseResult checkLicenseResult()
        throws LicenseException, PersistenceException {
        return new LicenseManager().checkLicenseResult(getUser());

    }

    /**
     * Checks the license for the current user is enabled.
     * @return the status of checking the license
     * @see net.project.license.LicenseManager#checkLicenseIsCurrent
     */
    public LicenseStatus checkLicenseStatus() {
        return new LicenseManager().checkLicenseStatus(getUser());

    }

    /**
     * Updates the user's license based on the license context.
     */
    public void updateLicense() throws LicenseException, PersistenceException {
        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            db.openConnection();

            updateLicense(db);

            db.commit();
        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException e) {
            	Logger.getLogger(LicenseUpdater.class).debug("Fatal error: unable to roll back transaction.");
            }
            throw new LicenseException("License update operation failed", sqle);
        } finally {
            db.release();
        }

    }

    /**
     * Updates the user's license based on the license context.
     *
     * @param db a <code>DBBean</code> object which is already in transaction.
     * This method will not commit or rollback that transaction.
     */
    public void updateLicense(DBBean db)
        throws LicenseException, PersistenceException {

        updateLicense(db, true);
    }

    /**
     * Updates the user's license based on the license context.
     *
     * @param db a <code>DBBean</code> object which is already in transaction.
     * This method will not commit or rollback that transaction.
     * @param notifyUser a <code>boolean</code> value which indicates if we
     * should notify the user about their new license.
     */
    public void updateLicense(DBBean db, boolean notifyUser) throws LicenseException, PersistenceException {

        // Make a creator containing a new or existing license
        LicenseCreator licenseCreator = LicenseCreator.makeCreator(licenseContext, licenseProps, purchaser);

        // At this point the we will have successfully
        // built a license or found an existing license
        // This will NOT have been persisted finally to persistent store
        // Thus we can dump out of this method at any time to abort
        // login license updates

        // Now associate the new license with the user
        // Now associate the license with the user
        licenseCreator.commitLicense(db, getUser(), true, notifyUser);
        license = licenseCreator.getLicense();
    }

}
