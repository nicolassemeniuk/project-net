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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.project.base.ObjectType;
import net.project.base.finder.NumberComparator;
import net.project.base.finder.NumberFilter;
import net.project.base.finder.TextComparator;
import net.project.base.finder.TextFilter;
import net.project.billing.payment.PaymentInformation;
import net.project.database.DBBean;
import net.project.license.bill.LicenseBillManager;
import net.project.license.cost.LicenseCost;
import net.project.license.model.LicenseModel;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;

import org.apache.log4j.Logger;

/**
 * A License represents the ability to utilize some aspect of the product.
 *
 * @author Tim Morrow
 * @since Gecko Update 2
 */
public class License implements java.io.Serializable, net.project.persistence.IXMLPersistence {

    //
    // Instance members
    //

    /** The id of this license. */
    private String id = null;

    /** The license key. */
    private LicenseKey licenseKey = null;

    /** The license certificate. */
    private LicenseCertificate licenseCertificate = null;

    /** The payment information. */
    private PaymentInformation payment = null;

    /** Indicates whether this license is of the special type Trial.*/
    private boolean isTrial = false;

    /** Indicates this license's status. */
    private LicenseStatus status = new LicenseStatus(LicenseStatusCode.ENABLED);

    /**
     * The person responsible for administering the users associated with a
     * license.  This person does not have to be a user on the license.
     */
    private Person responsiblePerson = null;

    /**
     * Creates an empty License.
     */
    public License() {
        // Nothing
    }

    /**
     * Returns a basic string representation of this license, suitable for debugging.
     * @return the license as a string
     */
    public String toString() {
        StringBuffer stringBuff = new StringBuffer();
        stringBuff.append(super.toString()).append("\n");
        if (getKey() != null) {
            stringBuff.append(getKey()).append("\n");
        }
        if (this.licenseCertificate != null) {
            stringBuff.append(this.licenseCertificate);
        }
        stringBuff.append(this.payment);
        return stringBuff.toString();
    }

    /**
     * Sets this license's id, required to load a license.
     * @param id the license id
     * @see #getID
     */
    public void setID(String id) {
        this.id = id;
    }


    /**
     * Returns this license's id.
     * @return the license id
     * @see #setID
     */
    public String getID() {
        return this.id;
    }

    /**
     * Sets this License's license key.
     * @param licenseKey the license key
     */
    public void setKey(LicenseKey licenseKey) {
        this.licenseKey = licenseKey;
    }

    /**
     * Returns this License's license key.
     * @return the license key
     */
    public LicenseKey getKey() {
        return this.licenseKey;
    }


    /**
     * Adds another model to this license.
     * @param model the license model to add
     */
    public void addModel(LicenseModel model) {
        try {
            ensureLicenseCertificate();
            this.licenseCertificate.addLicenseModel(model);
        } catch (LicenseException le) {
            // No model added
        }
    }

    /**
     * Returns the license models that have been added to this license.
     * @return the collection of license models where each element is a
     * <code>LicenseModel</code>
     */
    public Collection getModelCollection() {
        return this.licenseCertificate.getLicenseModelCollection();
    }

    /**
     * Adds another cost to this license.
     * @param cost the license cost to add
     */
    public void addCost(LicenseCost cost) {
        try {
            ensureLicenseCertificate();
            this.licenseCertificate.addLicenseCost(cost);
        } catch (LicenseException le) {
            // No cost added
        }
    }

    /**
     * Returns the license costs that have been added to this license.
     * @return the collection of license costs where each element is a
     * <code>LicenseCost</code>
     */
    public Collection getCostCollection() {
        return this.licenseCertificate.getLicenseCostCollection();
    }

    /**
     * Sets the payment information for this license.
     * @param payment the payment information
     * @see #getPaymentInformation
     */
    public void setPaymentInformation(PaymentInformation payment) {
        this.payment = payment;
    }


    /**
     * Returns the payment information for this license.
     * @return the payment information
     * @see #setPaymentInformation
     */
    public PaymentInformation getPaymentInformation() {
        return this.payment;
    }

    /**
     * Returns the maximum usage count for this license.
     * @return the maximum usage count or <code>0</code> if no usage limit
     * has been added.
     */
    public int getMaximumUsageCount() {
        return this.licenseCertificate.getMaximumUsageCount();
    }

    /**
     * Sets whether this license is a trial license.
     * @param isTrial true means the license is a trial license; false means
     * it is not.  Currently no special handling occurs for trial licenses.
     */
    public void setTrial(boolean isTrial) {
        this.isTrial = isTrial;
    }

    /**
     * Indicates whether this license is a trial license.
     * This facilitates any special handling associate with a trial license
     * (for example, a User may use at most One trial license).
     * @return true if this is a trial license, false otherwise.
     */
    public boolean isTrial() {
        return this.isTrial;
    }

    /**
     * Sets this License's certificate.
     * Used when constructing a license from an external source.
     * @param licenseCertificate the certificate
     */
    void setLicenseCertificate(LicenseCertificate licenseCertificate) {
        this.licenseCertificate = licenseCertificate;
    }

    /**
     * Returns the current license certificate.
     * @return the license certificate
     */
    LicenseCertificate getLicenseCertificate() {
        return this.licenseCertificate;
    }

    /**
     * Indicates whether this license may be used.
     * Its availability is determined by the license models attached to this
     * license.  For example, it may not be available if a <code>TimeLimit</code>
     * has expired.  It is not available for use if its usage limit is maxed out.
     * @return CheckStatus value of true if this license is available for use; false otherwie
     * @throws LicenseException if there is problem loading license master properties
     */
    public CheckStatus checkAvailableForUse()
        throws LicenseException, PersistenceException {
        return this.licenseCertificate.checkAvailableForUse();
    }

    /**
     * Indicates whether this license is current.
     * Its currency is determined by the license models attached to this license.
     * For example, it is not current if a <code>TimeLimit</code> has expired.
     * It is still current if its usage limit is at its allowed maximum.
     * @throws LicenseException if there is problem loading license master properties
     * @return CheckStatus value of true if this license is current; false otherwise
     */
    public CheckStatus checkCurrent()
        throws LicenseException, PersistenceException {
        return this.licenseCertificate.checkCurrent();
    }

    /**
     * Acquires a single unit of this license.
     * Increments usage counts and rechecks license is available for use.
     * @throws LicenseException if the license cannot be acquired
     */
    public void acquire() throws LicenseException, PersistenceException {
        this.licenseCertificate.acquisitionEvent();
    }

    /**
     * Relinquishes a single unit of this license.
     * Decrements usage counts
     * @throws LicenseException if there is a problem
     */
    public void relinquish() throws LicenseException {
    	Logger.getLogger(License.class).debug("About to relinquish license");
        this.licenseCertificate.relinquishEvent();
        Logger.getLogger(License.class).debug("License relinquished");
    }

    /**
     * Loads a license based on it id.
     * Checks that the license key defined in the license is valid for the
     * certificate.
     * @throws PersistenceException if there is a problem loading the license
     * @throws InvalidLicenseCertificateException if there is a problem loading
     * or parsing the certificate for the license
     * @throws LicenseKeyMismatchException if the license was found but its key
     * does not match the certificate
     */
    public void load() throws PersistenceException, InvalidLicenseCertificateException, LicenseKeyMismatchException {
        if (getID() == null) {
            throw new NullPointerException("License id null in License.load");
        }

        // Build query to load license
        LicenseFinder finder = new LicenseFinder();

        //Add a filter for the license ID.
        NumberFilter licenseIDFilter = new NumberFilter("licenseID", LicenseFinder.LICENSE_ID_COLUMN, false);
        licenseIDFilter.setComparator((NumberComparator)NumberComparator.EQUALS);
        licenseIDFilter.setNumber(new Integer(getID()));
        licenseIDFilter.setSelected(true);
        finder.addFinderFilter(licenseIDFilter);

        if (!finder.find(this)) {
            throw new PersistenceException("License load operation failed: License not found for id");
        }

        // Now that the license is loaded, verify the key and certificate match
        // The only way they might not match is if the certificate was modified,
        // for example by manipulating the database
        this.licenseCertificate.verifyAssociatedKey(getKey());
    }

    /**
     * Loads this license for the specified key.
     *
     * @param key the license key to fetch the license for
     * @throws PersistenceException if there is a database problem fetching
     * the license
     * @throws LicenseNotFoundException if there is no license for the specified
     * key
     * @throws LicenseKeyMismatchException if the license was found but its key
     * does not match the certificate
     * @throws InvalidLicenseCertificateException if there is a problem loading
     * or parsing the certificate for the license
     */
    public void load(LicenseKey key)
        throws PersistenceException, LicenseNotFoundException, LicenseKeyMismatchException, InvalidLicenseCertificateException {

        LicenseFinder finder = new LicenseFinder();

        TextFilter licenseKeyValueFilter = new TextFilter("10",
            LicenseFinder.LICENSE_KEY_COLUMN, false);
        licenseKeyValueFilter.setSelected(true);
        licenseKeyValueFilter.setComparator((TextComparator)TextComparator.EQUALS);
        licenseKeyValueFilter.setValue(key.getValue());

        finder.addFinderFilter(licenseKeyValueFilter);

        if (!finder.find(this)) {
            // We didn't find a license for the key
            throw new LicenseNotFoundException("License not found for key");
        }

        // Now that the license is loaded, verify the key and certificate match
        // The only way they might not match is if the certificate was modified,
        // for example by manipulating the database
        this.licenseCertificate.verifyAssociatedKey(getKey());
    }


    /**
     * Loads this license's certificate based on the id.
     * @param certificateID the id of this license's certificate
     * @throws InvalidLicenseCertificateException if the certificate could
     * not be found, loaded or parsed
     * @throws PersistenceException if there is a problem loading the certificate
     */
    protected void loadCertificate(String certificateID) throws InvalidLicenseCertificateException, PersistenceException {
        LicenseCertificate certificate = new LicenseCertificate();
        certificate.setID(certificateID);
        certificate.load();
        this.licenseCertificate = certificate;
    }

    /**
     * Loads this license's payment information based on the id.
     * @param paymentID the id of the payment information for this license
     * @throws PersistenceException if there is a problem loading the payment information
     */
    protected void loadPaymentInformation(String paymentID) throws PersistenceException {
        PaymentInformation payment = new PaymentInformation();
        payment.setID(paymentID);
        payment.load();
        this.payment = payment;
    }

    /**
     * Creates a key for this license based on the key value.
     * @param keyValue the value of the key to create
     */
    protected void createKeyForValue(String keyValue) {
        this.licenseKey = LicenseKey.createLicenseKeyForValue(keyValue);
    }

    /**
     * Stores this license to persitent store.
     *
     * @throws LicenseException if there is a problem creating any component
     * of the license, such as the license key
     * @throws PersistenceException if there is a problem storing
     */
    public void store() throws LicenseException, PersistenceException {
        DBBean db = new DBBean();

        try {
            db.openConnection();
            db.setAutoCommit(false);

            store(db);

            db.commit();

        } catch (SQLException sqle) {
            throw new PersistenceException("License store operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

    }


    /**
     * Stores this license to persistence store.  No commit/rollback/release is
     * performed.  Creates the license if it is new, otherwise updates the
     * license.  When a new license is created, a license key is generated if
     * necessary.  Assumes the license certificate has already been created.
     *
     * @param db a <code>DBBean</code> object which is already in transaction.
     * This method is guaranteed not to commit or rollback that transaction.
     * @throws LicenseException if there is a problem creating any component
     * of the license, such as the license key
     * @throws PersistenceException if there is a problem storing
     */
    public void store(DBBean db) throws LicenseException, PersistenceException {
        if (getID() == null) {
            create(db);
        } else {
            update(db);
        }
    }


    /**
     * Creates this license in persistent store.  No commit/rollback/release is
     * performed.  A license key is generated.  Assumes the license certificate
     * has already been created.
     *
     * @param db the DBBean in which to perform the transaction
     * @throws LicenseException if there is a problem creating any component
     * of the license, such as the license key
     * @throws PersistenceException if there is a problem storing
     */
    private void create(DBBean db) throws LicenseException, PersistenceException {
        String licenseID = null;
        String certificateID = null;
        String paymentID = null;

        StringBuffer insertQuery = new StringBuffer();
        insertQuery.append("insert into pn_license ");
        insertQuery.append("(license_id, license_key_value, certificate_id, payment_id, is_trial, license_status, responsible_user_id) ");
        insertQuery.append("values (?, ?, ?, ?, ?, ?, ?) ");

        try {
            // Make sure we have a license key and it is unique i.e. not already installed in the system.
            ensureLicenseKey();
            //ensureLicenseKeyIsUnique();

            // Check we have a license certificate
            if (this.licenseCertificate == null) {
                throw new LicenseException("No license certificate has been generated for the license");
            }

            // The license certificate's associated key must match this license's key
            this.licenseCertificate.setAssociatedLicenseKey(getKey());

            // Generate license id
            licenseID = new net.project.database.ObjectManager().dbCreateObject(db, ObjectType.LICENSE, "A");

            // Store the certificate
            this.licenseCertificate.store(db);
            certificateID = this.licenseCertificate.getID();

            // Store payment information
            getPaymentInformation().store(db);
            paymentID = getPaymentInformation().getID();

            // Store license
            int index = 0;
            db.prepareStatement(insertQuery.toString());
            db.pstmt.setString(++index, licenseID);
            db.pstmt.setString(++index, getKey().getValue());
            db.pstmt.setString(++index, certificateID);
            db.pstmt.setString(++index, paymentID);
            db.pstmt.setString(++index, String.valueOf(net.project.util.Conversion.booleanToInt(isTrial())));
            db.pstmt.setInt(++index, this.getStatus().getCode().getCodeID());
            db.pstmt.setString(++index, this.getResponsiblePerson().getID());
            db.executePrepared();


            // Store the purchaser
            if (getPurchaser() != null && getPurchaser().getID() != null) {
                db.prepareStatement(
                    "insert into pn_license_purchaser " +
                    "  (license_id, person_id) " +
                    "values" +
                    "  (?,?) ");
                db.pstmt.setString(1, licenseID);
                db.pstmt.setString(2, getPurchaser().getID());
                db.executePrepared();
            }
        } catch (SQLException sqle) {
            throw new PersistenceException("License create operation failed: " + sqle, sqle);

        }

        // Now set the license id
        // This must occur at the end to ensure it is only set in the
        // event of a successful insert
        setID(licenseID);
        // We must create a bill for this newly created license.

        new LicenseBillManager().createBills(db, responsiblePerson, this);

    }


    /**
     * Updates this license.
     * No commit/rollback/release is performed.
     * @param db the DBBean in which to perform the transaction
     * @throws PersistenceException if there is a problem updating
     */
    private void update(DBBean db) throws PersistenceException {

        try {
            StringBuffer updateQuery = new StringBuffer();
            updateQuery.append("update pn_license ");
            updateQuery.append("set is_trial = ? ");
            updateQuery.append(", license_status = ? ");
            updateQuery.append("where license_id = ? ");

            // Store the certificate
            this.licenseCertificate.store(db);

            // Store the payment information
            getPaymentInformation().store(db);

            // Store license
            int index = 0;
            db.prepareStatement(updateQuery.toString());
            db.pstmt.setString(++index, String.valueOf(net.project.util.Conversion.booleanToInt(isTrial())));
            db.pstmt.setInt(++index, this.getStatus().getCode().getCodeID());
            db.pstmt.setString(++index, getID());
            db.executePrepared();

        } catch (SQLException sqle) {
            throw new PersistenceException("License update operation failed: " + sqle, sqle);

        }

        // NO COMMIT

    }

    /**
     * Ensures that a license certificate has been created.
     * A side-effect is ensuring that a license key has been created.
     *
     * @postcondition this license has a license certificate
     * @throws LicenseException if there is a problem creating a license key
     */
    public void ensureLicenseCertificate() throws LicenseException {
        if (this.licenseCertificate == null) {
            this.licenseCertificate = new LicenseCertificate();
            ensureLicenseKey();
            //ensureLicenseKeyIsUnique();
            this.licenseCertificate.setAssociatedLicenseKey(getKey());
        }
    }


    /**
     * Ensures that a license key has been created.
     * @postcondition this license has a license key
     * @throws LicenseException if there is a problem creating a license key
     */
    private void ensureLicenseKey() throws LicenseException {
        if (this.licenseKey == null) {
            // this.licenseKey = LicenseKey.createLicenseKey();
            throw new LicenseException("No license-key has been generated for this license.");
        }
    }

    /**
     * Ensures that a license key is unique.
     *
     * @param licenseKey a <code>String</code> value containing the license key
     * that we are testing for uniqueness.
     * @throws LicenseKeyUniquenessException if this license key is already
     * present in the system.
     */
    protected static void ensureLicenseKeyIsUnique(String licenseKey)
        throws LicenseKeyUniquenessException, PersistenceException {
        DBBean db = new DBBean();
        try {
            db.prepareStatement("select * from pn_license where license_key_value = ?");

            db.pstmt.setString(1, licenseKey);

            db.executePrepared();

            if (db.result.next()) {
                throw new LicenseKeyUniquenessException("The License you are trying to install is already installed in the system.");
            }
        } catch (SQLException sqle) {
            throw new PersistenceException("ensureUniqueLicenseKey operation failed: " + sqle, sqle);

        } finally {
            db.release();
        }

        return;
    }

    public String getXML() {
        return getXMLDocument().getXMLString();
    }

    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    /**
     * Get an XML document which describes this license.
     *
     * @return a <code>XMLDocument</code> object which describes this license.
     */
    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        try {
            doc.startElement("License");
            if (getResponsiblePerson() != null) {
                doc.addElement("ResponsibleUser", this.getResponsiblePerson().getDisplayName());
                doc.addElement("ResponsibleUserEmail", this.getResponsiblePerson().getEmail());
            }
            doc.addElement("LicenseID", getID());
            doc.addElement("IsTrial", new Boolean(isTrial()));
            doc.addElement("LicenseStatus", String.valueOf(getStatus().getCode().getCodeID()));
            if (getKey() != null) {
                doc.addElement(getKey().getXMLDocument());
            }
            doc.addElement(this.licenseCertificate.getXMLDocument());
            doc.addElement(this.payment.getXMLDocument());

            if (getPurchaser() != null) {
                doc.startElement("Purchaser");
                doc.addXMLString(getPurchaser().getXMLBody());
                doc.endElement();
            }

            doc.endElement();
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }
        return doc;
    }

    /**
     * Gets all the users of a license with the current license ID.  The current
     * license ID is available by calling {@link #getID}.
     *
     * @return ArrayList, a list of all the users of this license.
     * @throws PersistenceException if there is an unexpected database error
     * while trying to fetch the users.
     */
    public ArrayList getUsers() throws PersistenceException {
        return getUsersForLicenseID(this.getID());
    }

    /**
     * Gets all the users of a license with given licenseID.
     *
     * @param licenseID the id of this license.
     * @return ArrayList, a list of all the users of this license.
     * @throws PersistenceException if there is an unexpected error loading
     * users from the database.
     */
    private ArrayList getUsersForLicenseID(String licenseID)
        throws PersistenceException {
        ArrayList userList = new ArrayList();
        DBBean db = new DBBean();
        String query = "select person_id from pn_person_has_license where license_id = ?";

        try {

            db.prepareStatement(query);
            db.pstmt.setString(1, licenseID);
            db.executePrepared();

            while (db.result.next()) {
                String person_id = db.result.getString("person_id");
                net.project.resource.Person user = new net.project.resource.Person(person_id);
                user.load();
                userList.add(user);
            }

            return userList;

        } catch (SQLException sqle) {
            throw new PersistenceException("getUsersForLicenseID operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

    }

    /**
     * Gets XML representation of all the users of this license.
     *
     * @return xml String, as a list of all users of this license.
     * @throws PersistenceException if there is an unexpected error loading the
     * license users from the database.
     */
    public String getLicenseUsersXML() throws PersistenceException {
        ArrayList userList = getUsersForLicenseID(this.getID());
        Iterator iter = userList.iterator();
        StringBuffer sbuff = new StringBuffer();
        sbuff.append("<person_list>");
        while (iter.hasNext()) {
            net.project.resource.Person person = (net.project.resource.Person)iter.next();
            sbuff.append(person.getXMLBody());
        }
        sbuff.append("</person_list>");
        return sbuff.toString();
    }

    /**
     * Sets the status of this license.
     *
     * @param statusCode <code>int</code> indicating thew new status of the
     * license.
     * @param reasonCode <code>int</code> indicating the reason for this new
     * code.
     * @throws PersistenceException if there is an unexpected error loading the
     * license reason code from the database.
     */
    public void setStatus(int statusCode, int reasonCode) throws PersistenceException {
        this.status = new LicenseStatus(statusCode, reasonCode);
    }

    /**
     * Gets the disabled status of this license.
     * @return boolean, true if the license status is disabled.
     */
    public LicenseStatus getStatus() {
        return this.status;
    }

    /**
     * Clears out all parameters of this license.
     */
    public void setClear() {
        this.id = null;
        this.licenseKey = null;
        this.licenseCertificate = null;
        this.payment = null;
        this.isTrial = false;
        this.responsiblePerson = null;

    }

    /**
     * Modifies the status the status of this license.
     *
     * @param statusCode the code to set this license's status to.
     * @param reasonCode the code for this license status's reason.
     * @throws LicenseException if this license has already been cancelled.
     * @throws PersistenceException if an unexpected error occurs while
     * accessing the database.
     */
    public void modifyStatus(int statusCode, int reasonCode)
        throws PersistenceException, LicenseException {

        if (this.getStatus().getCode().equals(LicenseStatusCode.CANCELED)) {
            throw new LicenseException("This license has been cancelled." +
                " You can not change the status of a cancelled license.");
        }

        DBBean db = new DBBean();
        int index = 0;

        try {
            if (statusCode == LicenseStatusCode.ENABLED.getCodeID()) {
                String updateQuery = "UPDATE pn_license set license_status = ?, status_reason_code=? where license_id = ?";
                db.prepareStatement(updateQuery);
                db.pstmt.setInt(++index, statusCode);
                db.pstmt.setString(++index, "");
                db.pstmt.setString(++index, this.getID());
                db.executePrepared();

            } else {
                String updateQuery = "UPDATE pn_license set license_status = ?, status_reason_code=? where license_id = ?";
                db.prepareStatement(updateQuery);
                db.pstmt.setInt(++index, statusCode);
                db.pstmt.setInt(++index, reasonCode);
                db.pstmt.setString(++index, this.getID());
                db.executePrepared();

            }

        } catch (SQLException sqle) {
            throw new PersistenceException("Modify License Status operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }
    }

    /**
     * Assigns the "responsible" person for this license.  This person object
     * is assumed to already be loaded.
     *
     * @param responsiblePerson the person responsible for this license.
     * @see #getResponsiblePerson
     */
    public void setResponsiblePerson(Person responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    /**
     * Assigns the "responsible" person for this license.  Loads the person for
     * the specified ID.
     *
     * @param personID the ID of the responsible person; when null,
     * the responsiblePerson is set to null
     * @see #getResponsiblePerson
     * @throws PersistenceException if there is an unexpected database error
     * while trying to load the responsible person.
     */
    public void setResponsiblePersonID(String personID)
        throws PersistenceException {
        if (personID != null) {
            Person person = new Person(personID);
            person.load();
            this.responsiblePerson = person;

        } else {
            this.responsiblePerson = null;
        }
    }

    /**
     * Gets a person object which points to the person who purchased this
     * license.
     *
     * @return a <code>Person</code> object who originally purchased this
     * license.
     */
    public Person getPurchaser() {
        return licenseCertificate.getPurchaser();
    }

    /**
     * Indicates the person who originally purchased this license.  This value
     * could be null.  (There were a number of licenses which were created
     * before we had the concept of "purchaser".)
     *
     * @param purchaser a <code>Person</code> object who originally purchased
     * this license.
     */
    public void setPurchaser(Person purchaser) {
        licenseCertificate.addPurchaser(purchaser);
    }

    /**
     * Updates the "responsible" person for this license.
     *
     * @param person the responsible person
     * @throws PersistenceException if there is an unexpected error while
     * updating the responsible user in the database.
     */
    public void updateResponsiblePerson(Person person)
        throws PersistenceException {

        this.responsiblePerson = person;
        // Also store the information in the database.
        String licenseID = this.getID();
        String userID = person.getID();
        int index = 0;
        DBBean db = new DBBean();
        String query = "update pn_license set responsible_user_id = ? where license_id = ? ";

        try {
            db.prepareStatement(query);
            db.pstmt.setString(++index, userID);
            db.pstmt.setString(++index, licenseID);

            db.pstmt.executeQuery();
            db.commit();

            new LicenseNotification().notifyUserOfResponsibility(this, person);

        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("Set responsible user for license operation failed:" + sqle, sqle);

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
     * Gets the "responsible" person for this license.
     * @return the responsible person
     */
    public Person getResponsiblePerson() {
        return this.responsiblePerson;
    }


}
