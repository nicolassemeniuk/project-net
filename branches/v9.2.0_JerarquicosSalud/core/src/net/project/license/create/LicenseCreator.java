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

import net.project.billing.payment.ChargeCode;
import net.project.billing.payment.CreditCard;
import net.project.billing.payment.PaymentInformation;
import net.project.billing.payment.Trial;
import net.project.database.DBBean;
import net.project.license.InvalidLicenseCertificateException;
import net.project.license.InvalidLicenseKeyException;
import net.project.license.License;
import net.project.license.LicenseException;
import net.project.license.LicenseKey;
import net.project.license.LicenseKeyException;
import net.project.license.LicenseKeyMismatchException;
import net.project.license.LicenseManager;
import net.project.license.LicenseNotFoundException;
import net.project.license.LicenseStatusCode;
import net.project.license.PersonLicense;
import net.project.license.cost.BaseCost;
import net.project.license.cost.LicenseCostManager;
import net.project.license.cost.MaintenanceCost;
import net.project.license.model.NodeLocked;
import net.project.license.model.TimeLimit;
import net.project.license.model.UsageLimit;
import net.project.license.system.LicenseProperties;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;

/**
 * Provides methods for creating licenses and associating with persons.
 * Based on the <code>UseCaseController</code> pattern, it acts
 * as a coordinator across numerous license objects.
 */
public class LicenseCreator implements java.io.Serializable {

    //
    // Static members
    //

    /**
     * Creates a LicenseCreator based on the specified context, properties and current user.
     * This is typically used having captures information about a license to which to associate
     * a user (for example, when the user is first registering or logging in without a valid
     * license).
     * <p>
     * After calling the license creator will have created or located a license; the person
     * will not have been associated with the license at that point;
     * only <code>{@link LicenseCreator#commitLicense}</code> must be called
     * to complete the process of associatng a user to a license.
     * </p>
     * @param licenseContext the context that provides the type of license to create
     * @param licenseProperties the properties that affects licenses
     * @param creatorPerson (optional) the person creating the license; that
     * person will set as the purchaser if a new license is created; ignored when null or
     * when using an existing license (since it will already have a purchaser)
     * @return the LicenseCreator containing a newly created license
     * @throws LicenseException if there is some problem creating a license; for example the context selection type
     * is unknown
     * @throws PersistenceException if there is a database problem creating a license
     */
    public static LicenseCreator makeCreator(LicenseContext licenseContext, LicenseProperties licenseProperties, Person creatorPerson)
            throws LicenseException, PersistenceException {

        // Get the "kind" of license creation to be performed
        // That is, the user may have entered a charge code, entered
        // a license key or requested a trial license
        LicenseSelectionType selectionType = licenseContext.getSelectionType();

        // Use a creator helper class to perform all the license creation
        // activities
        LicenseCreator licenseCreator = new LicenseCreator();
        licenseCreator.setLicenseProperties(licenseProperties);

        // Begin the license creation process based on the mechanism
        // selected by the user
        // This is the rather awkward shift from a registration-oriented
        // high-level selection to the messy implementation details
        // associated with satisfying that selection
        // The license creator has particular hard-wired methods
        // for proceeding, since each is significantly different
        if (selectionType.equals(LicenseSelectionType.CHARGE_CODE)) {
            // Create a license with default constraints for the specified
            // license code
            licenseCreator.createDefaultLicenseForChargeCode(licenseContext.getChargeCode());
            if (creatorPerson != null) {
                licenseCreator.setPurchaser(creatorPerson);
            }

        } else if (selectionType.equals(LicenseSelectionType.TRIAL)) {
            // Create a trial license with default constraints
            licenseCreator.createDefaultTrialLicense();
            if (creatorPerson != null) {
                licenseCreator.setPurchaser(creatorPerson);
            }

        } else if (selectionType.equals(LicenseSelectionType.ENTERED_LICENSE_KEY)) {
            // Use existing license for key
            // Since it is an existing licensing, we don't change the purchaser
            licenseCreator.useExistingLicenseForKey(licenseContext.getEnteredLicenseKey());

        } else if (selectionType.equals(LicenseSelectionType.DEFAULT_CHARGE_CODE)) {
            // Create a license with default constraints for the system
            // default charge code
            licenseCreator.createDefaultLicenseForChargeCode(licenseContext.getDefaultChargeCode());
            if (creatorPerson != null) {
                licenseCreator.setPurchaser(creatorPerson);
            }

        } else if (selectionType.equals(LicenseSelectionType.DEFAULT_LICENSE_KEY)) {
            // Use license that has default license key
            // Since it is an existing licensing, we don't change the purchaser
            licenseCreator.useExistingLicenseForKey(licenseContext.getDefaultLicenseKey());

        } else if (selectionType.equals(LicenseSelectionType.CREDIT_CARD)) {
            // Create a license while adding the credit card information
            licenseCreator.createCreditCardLicense(licenseContext.getCreditCardLicenseCount(),
                    licenseContext.getCreditCardNumber(),
                    licenseContext.getCreditCardExpirationMonth(),
                    licenseContext.getCreditCardExpirationYear());
            if (creatorPerson != null) {
                licenseCreator.setPurchaser(creatorPerson);
            }

        } else {
            throw new LicenseException("License update operation failed: Unknown selection: " + selectionType.getID());

        }
        return licenseCreator;
    }

    //
    // Instance Members
    //

    /**
     * The current license being created.
     */
    private License currentLicense = null;

    /**
     * The current license properties.
     */
    private LicenseProperties licenseProps = null;

    /**
     * The person who purchased the license.
     */
    private Person purchaser = null;


    /**
     * Creates a new LicenseCreator used for creating licenses.
     * <p>
     * <b>Note:</b> In general prefer the use of {@link #makeCreator}.
     * </p>
     */
    public LicenseCreator() {
        // Do nothing
    }

    /**
     * Sets the license properties to use.
     * @param licenseProps the license properties
     */
    private void setLicenseProperties(LicenseProperties licenseProps) {
        this.licenseProps = licenseProps;
    }

    /**
     * Returns the current license properties
     * @return the license properties
     */
    private LicenseProperties getLicenseProperties() {

        return this.licenseProps;
    }

    /**
     * Set the person who purchased the license.
     * <p>
     * Note that when an existing license is being used, there will likely already
     * be a purchaser.  The purchaser should only be set for new licenses.
     * </p>
     * @param purchaser a <code>Person</code> object which represents the person
     * who purchased the license.  It is anticipated that this Person object has
     * already been stored.
     */
    private void setPurchaser(Person purchaser) {
        this.purchaser = purchaser;
    }

    /**
     * Creates a license with default constraints with billing information
     * as the specified charge code.
     * <b>postcondition:</b> a license has been initiated but not stored
     * @param chargeCode the charge code
     */
    private void createDefaultLicenseForChargeCode(String chargeCode)
            throws LicenseKeyException, LicenseException, PersistenceException {

        // Create payment information
        PaymentInformation payment = new PaymentInformation();
        payment.setPaymentModel(new ChargeCode(chargeCode));

        // Create license
        License license = new License();
        license.setKey(createLicenseKey());
        license.addModel(new UsageLimit(1));
        license.addModel(new NodeLocked(getLicenseProperties().getCurrentNodeID()));
        license.setPaymentInformation(payment);
        license.setPurchaser(purchaser);
        addDefaultCosts(license);

        setCurrentLicense(license);
    }


    /**
     * Creates a trial license with default constraints.
     * <b>postcondition:</b> a license has been initiated but not stored
     * @throws LicenseException if there was a problem getting the license
     * master license properties
     */
    private void createDefaultTrialLicense()
            throws LicenseKeyException, LicenseException, PersistenceException {

        // Create trial payment info
        PaymentInformation payment = new PaymentInformation();
        payment.setPaymentModel(new Trial());

        // Create License
        License license = new License();
        license.setKey(createLicenseKey());
        license.setTrial(true);
        license.addModel(new UsageLimit(1));
        license.addModel(new TimeLimit(getLicenseProperties().getDefaultTrialLicensePeriodDays()));
        license.addModel(new NodeLocked(getLicenseProperties().getCurrentNodeID()));
        license.setPurchaser(purchaser);
        license.setPaymentInformation(payment);
        addTrialCosts(license);

        setCurrentLicense(license);
    }

    /**
     * Create a license as the result of a credit card purchase.
     *
     * @param licenseCount a <code>int</code> value indicating the number of
     * users this license should authorize.
     * @throws LicenseException if there is a problem fetching the master
     * properties.
     * @throws PersistenceException if there is a problem getting the master
     * properties.
     */
    private void createCreditCardLicense(int licenseCount, String cardNumber,
            int expiryMonth, int expiryYear) throws LicenseException, PersistenceException {

        PaymentInformation payment = new PaymentInformation();

        payment.setPaymentModel(new CreditCard(cardNumber, expiryMonth, expiryYear));

        //Create License
        License license = new License();
        license.setKey(createLicenseKey());
        license.setTrial(false);
        license.addModel(new UsageLimit(licenseCount));
        license.addModel(new NodeLocked(getLicenseProperties().getCurrentNodeID()));
        license.setPaymentInformation(payment);
        license.setPurchaser(purchaser);

        addDefaultCosts(license);

        setCurrentLicense(license);
    }


    /**
     * Uses an existing license that has the specified display key.
     * @param displayKey the license key in display format
     * @throws InvalidLicenseKeyException if the display key cannot be processed
     * @throws LicenseNotFoundException if no license can be found for the
     * specified key
     * @throws InvalidLicenseCertificateException if there is a problem loading
     * or parsing the license certificate
     * @throws LicenseKeyMismatchException if the license key of the loaded license
     * doesn't match its certificate
     */
    public void useExistingLicenseForKey(String displayKey)
            throws InvalidLicenseKeyException, LicenseNotFoundException, InvalidLicenseCertificateException, LicenseKeyMismatchException {

        LicenseManager manager = new LicenseManager();
        setCurrentLicense(manager.getLicenseForDisplayKey(displayKey));
    }

    /**
     * Get the license created as part of this process.
     *
     * @return a <code>License</code> object created by this creator.
     */
    public License getLicense() {
        return currentLicense;
    }

    /**
     * Associates the current license with the current person.
     * and ensures any additional items are created.
     * @param db the DBBean in which to perform the transaction
     * @param person the person to associate the license with.  If the license
     * hasn't already been assigned a purchaser or responsible user, the person
     * will be assigned to do either or both of those tasks.
     * @param associateUser a <code>boolean</code> value indicating whether we
     * are going to associate the user with the license.  Even if we don't,
     * they'll still be the responsible user.
     * @param notify if true notify user with license info otherwise keep silence
     * @throws LicenseException if there is a problem acquiring a unit of the
     * license for the person
     * @throws PersistenceException if there is a problem storing the license
     * or bills
     * @throws NullPointerException if the person is null
     * @throws IllegalStateException if there is no current license available
     * by {@link #getCurrentLicense}
     */
    public void commitLicense(DBBean db, Person person, boolean associateUser, boolean notify) throws LicenseException, PersistenceException {
        // Check we have a person
        if (person == null) {
            throw new NullPointerException("Person is null");
        }

        License license = getCurrentLicense();

        // Make sure we have a license to commit!
        if (license == null) {
            throw new IllegalStateException("No license available");
        }

        // Make sure the license is not disabled or cancelled!
        if (!license.getStatus().getCode().equals(LicenseStatusCode.ENABLED)) {
            throw new LicenseException("Can not attach a person to a Cancelled or Disabled license.");
        }

        // Make sure that this license has a responsible person,
        // if not associate the current person(the license creator) as the responsible person
        // We don't send any notification at this stage because it is the creator of the license
        // who is being made the responsible user
        if (license.getResponsiblePerson() == null) {
            license.setResponsiblePerson(person);
        }

        // Make sure the license has a purchaser
        // If not we set the purchaser that was specified earlier
        if (license.getPurchaser() == null) {
            license.setPurchaser(purchaser);
        }

        license.store(db);

        if (associateUser) {
            PersonLicense personLicense = new PersonLicense(person);

            // Associate the license with the current person
            // This disassociates the current license from the person (if any)
            // and also writes history entries
            // The license is modified; a single unit has been acquired
            // The license is stored, since this is necessary to perform the
            // association
            personLicense.associate(db, license, notify);
        }
    }

    /**
     * Associates the current license with the current person.
     * and ensures any additional items are created.
     * @param person the person to associate the license with
     * @throws LicenseException if there is a problem acquiring a unit of the
     * license for the person
     * @throws PersistenceException if there is a problem storing the license
     * or bills
     * @throws NullPointerException if the person is null
     * @throws IllegalStateException if there is no current license available
     * by {@link #getCurrentLicense}
     */
    public void commitLicense(Person person, boolean notify) throws LicenseException, PersistenceException {

        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            commitLicense(db, person, true, notify);
            db.commit();
        } catch (LicenseException le) {
            throw new LicenseException("LicenseCreator.java : " +
                "commitLicense(person) operation failed." + le.getMessage(), le);
        } catch (SQLException sqle) {
            throw new PersistenceException("LicenseCreator.java : " +
                "commitLicense(person) operation failed" + sqle.getMessage(), sqle);
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
     * Sets the current license being created.
     * @param license the license being created
     */
    private void setCurrentLicense(License license) {
        this.currentLicense = license;
    }


    /**
     * Returns the license being created.
     * @return the current license
     */
    private License getCurrentLicense() {
        return this.currentLicense;
    }


    /**
     * Adds default costs to the license.
     * These costs are determined by what?
     * @param license the license to which to add costs
     * @throws LicenseException if there was problem getting
     * the license master properties
     */
    private static void addDefaultCosts(License license) throws LicenseException,
            PersistenceException {

        LicenseCostManager costManager = LicenseCostManager.getInstance();
        license.addCost(new BaseCost(costManager.getBaseCostUnitPrice()));
        license.addCost(new MaintenanceCost(costManager.getMaintenanceCostPercentage()));
    }

    /**
     * Adds trial costs to the license.
     * These costs are determined by what?
     * @param license the license to which to add costs
     * @throws LicenseException
     */
    private static void addTrialCosts(License license) throws LicenseException,
            PersistenceException {

        LicenseCostManager costManager = LicenseCostManager.getInstance();
        license.addCost(new BaseCost(costManager.getTrialCostUnitPrice()));
    }

    /**
     * Creates and sets a unique key for the license under creation.
     */
    private static LicenseKey createLicenseKey()
            throws LicenseKeyException {
        return LicenseKey.createLicenseKey();
    }

}
