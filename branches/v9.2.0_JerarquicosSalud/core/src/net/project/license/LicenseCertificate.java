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
import java.util.Collection;
import java.util.Iterator;

import net.project.database.Clob;
import net.project.database.DBBean;
import net.project.license.cost.LicenseCost;
import net.project.license.model.LicenseModel;
import net.project.license.model.UsageLimit;
import net.project.persistence.NoDataFoundException;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;

import org.jdom.Element;

/**
 * Provides access to the license models supported by a license.
 *
 * @author Tim Morrow
 * @since Gecko Update 2
 */
public class LicenseCertificate implements java.io.Serializable {

    //
    // Static members
    //

    /**
     * The table name of the table containing the lob data.
     */
    private static final String CERTIFICATE_LOB_TABLE_NAME = "pn_license_certificate_lob";

    /**
     * The primary key column name in the table containing the lob data.
     */
    private static final String CERTIFICATE_LOB_PK_NAME = "certificate_id";

    /**
     * The data column name containing the lob data.
     */
    private static final String CERTIFICATE_LOB_DATA_NAME = "certificate_lob_data";


    //
    // Instance members
    //

    /** The id of this license certificate. */
    private String certificateID = null;

    /**
     * The body of the license certificate.
     * This is separated from the license certificate to make it easier
     * to marshal and unmarshal to/from XML and store in the database.
     * It helps to encapsulate those properties of the certificate that must
     * always be stored in an encrypted fashion.
     * Note that the body is private and is _never_ exposed to a client of
     * the LicenseCertificate class.
     */
    private LicenseCertificateBody certificateBody = new LicenseCertificateBody();

    /**
     * Creates an empty LicenseCertificate.
     */
    LicenseCertificate() {
        // Nothing
    }

    /**
     * Creates a new LicenseCertificate based on the specified certificate body.
     * @param body the body for this LicenseCertificate
     */
    LicenseCertificate(LicenseCertificateBody body) {
        setBody(body);
    }

    /**
     * Sets this certificate's id.
     * @param certificateID the id of this certificate
     * @see #getID
     */
    protected void setID(String certificateID) {
        this.certificateID = certificateID;
    }

    /**
     * Returns this certificate's id.
     * @return the id of this certificate
     * @see #setID
     */
    public String getID() {
        return this.certificateID;
    }

    /**
     * Sets the license certificate body for this license certificate.
     * @param body the license certificate body
     */
    public void setBody(LicenseCertificateBody body) {
        this.certificateBody = body;
    }

    /**
     * Returns the license certificate body.
     * @return the license certificate body
     */
    private LicenseCertificateBody getBody() {
        return this.certificateBody;
    }


    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(super.toString()).append("\n");
        result.append(this.certificateBody).append("\n");
        return result.toString();
    }


    /**
     * Adds the specified license model to this license certificate.
     * @param model the license model to add
     */
    void addLicenseModel(LicenseModel model) {
        getBody().getLicenseModelCollection().add(model);
    }

    /**
     * Returns the current license models.
     * @return the collection of license models; each element is of type
     * <code>LicenseModel</code>
     * @see net.project.license.model.LicenseModel
     */
    Collection getLicenseModelCollection() {
        return getBody().getLicenseModelCollection();
    }


    /**
     * Adds the specified license cost to this license certificate.
     * @param cost the license cost to add
     */
    void addLicenseCost(LicenseCost cost) {
        getBody().getLicenseCostCollection().add(cost);
    }


    /**
     * Returns the current license costs.
     * @return the collection of license costs; each element is of type
     * <code>LicenseCost</code>
     * @see net.project.license.cost.LicenseCost
     */
    Collection getLicenseCostCollection() {
        return getBody().getLicenseCostCollection();
    }


    /**
     * Specifies the license key that this certificate is associated with.
     * This is to prevent "cut-and-paste" duplication of certificate data
     * in the database; It is now bound to a key.
     *
     * @param key the <code>LicenseKey</code> that this certificate is part of.
     */
    void setAssociatedLicenseKey(LicenseKey key) {
        getBody().setAssociatedLicenseKey(key);
    }

    /**
     * Returns the license key associated with the license that this certificate
     * is part of.
     * @return the key
     */
    private LicenseKey getAssociatedLicenseKey() {
        return getBody().getAssociatedLicenseKey();
    }

    /**
     * Returns the maximum usage count for this license certificate.
     * @return the maximum usage count or <code>0</code> if no usage limit
     * has been added.
     */
    public int getMaximumUsageCount() {
        int maxUsageCount = 0;

        // Iterate over each license model looking for a UsageLimit model
        LicenseModel nextModel = null;
        for (Iterator it = getBody().getLicenseModelCollection().iterator(); it.hasNext();) {
            nextModel = (LicenseModel)it.next();
            if (nextModel instanceof UsageLimit) {
                maxUsageCount = ((UsageLimit)nextModel).getMaxUsageCount();
            }
        }

        return maxUsageCount;
    }

    public void addPurchaser(Person purchaser) {
        getBody().setPurchaser(purchaser);
    }

    public Person getPurchaser() {
        return getBody().getPurchaser();
    }

    /**
     * Indicates whether this license certificate is available, based on
     * its constraints not being met.
     *
     * @return a CheckStatus value of true iff it is true that no model's
     * constraints have been met; false if even a single model's constraints
     * have been met
     * @throws LicenseException if there is problem loading license master properties
     */
    public CheckStatus checkAvailableForUse() throws LicenseException,
        PersistenceException {

        boolean isAnyConstraintMet = false;
        // Default is avaiable
        CheckStatus availableStatus = new CheckStatus(true);

        // Iterate over each license mode only while no constraints have been met
        // Breaks when
        for (Iterator it = getBody().getLicenseModelCollection().iterator(); it.hasNext() && !isAnyConstraintMet;) {
            LicenseModel nextModel = (LicenseModel)it.next();
            CheckStatus status = nextModel.checkConstraintMet();

            // Abort if constraint is met; set the availabile status to false
            // and add the message that caused the constraint to be met
            if (status.booleanValue()) {
                isAnyConstraintMet = true;
                availableStatus = new CheckStatus(false, status.getMessage());
            }
        }

        return availableStatus;
    }

    /**
     * Indicates whether this license is current, based on its constraints
     * not exceeded.
     * @return a CheckStatus value of true iff it is true that no model's
     * constraints have been <b>EXCEEDED</b>; false if even a single model's
     * constraints have been exceeded.
     * @throws LicenseException
     */
    public CheckStatus checkCurrent()
        throws LicenseException, PersistenceException {
        boolean isAnyConstraintExceeded = false;
        // Default is current
        CheckStatus currentStatus = new CheckStatus(true);

        // Iterate over each license mode only while no constraints have been exceeded
        for (Iterator it = getBody().getLicenseModelCollection().iterator(); it.hasNext() && !isAnyConstraintExceeded;) {
            LicenseModel nextModel = (LicenseModel)it.next();
            CheckStatus status = nextModel.checkConstraintExceeded();

            // Abort if constraint is exceeded; set the current status to false
            // and add the message that caused the constraint to be exceeded
            if (status.booleanValue()) {
                isAnyConstraintExceeded = true;
                currentStatus = new CheckStatus(false, status.getMessage());
            }
        }

        return currentStatus;
    }


    /**
     * Acquires a single unit of this license certificate.
     * Updates any license models (e.g. usage limit).
     * @throws LicenseException if there is a problem acquiring the certificate;
     * note that each model is processed even when one error occurs.  Constraints
     * should be checked before acquiring a certificate through
     * {@link #checkAvailableForUse}; this license should not be associated with
     * any user or otherwise used if this exception is thrown
     */
    void acquisitionEvent() throws LicenseException, PersistenceException {

        boolean isError = false;

        LicenseModel nextModel = null;
        // Iterate over each license model and call acquisition event
        for (Iterator it = getBody().getLicenseModelCollection().iterator(); it.hasNext();) {
            nextModel = (LicenseModel)it.next();

            try {
                nextModel.acquisitionEvent();

            } catch (net.project.license.model.LicenseModelAcquisitionException e) {
                // Model has met limit
                isError = true;

            }

        }

        // Handle any error occurred
        if (isError) {
            throw new LicenseException("An error occurred acquiring license certificate");
        }

    }

    /**
     * Relinquishes a single unit of this license certificate.  Updates any
     * license models (e.g. usage limit).
     *
     * @throws LicenseException if there is some problem relinquishing;
     * currently no known problems can occur
     */
    void relinquishEvent() throws LicenseException {
        boolean isError = false;

        // Iterate over each license model and call acquisition event
        for (Iterator it = getBody().getLicenseModelCollection().iterator(); it.hasNext();) {
            LicenseModel nextModel = (LicenseModel)it.next();

            try {
                nextModel.relinquishEvent();
            } catch (net.project.license.model.LicenseModelRelinquishException e) {
                // Some error
                isError = true;
            }
        }

        // Handle any error occurred
        if (isError) {
            throw new LicenseException("An error occurred relinquishing license certificate");
        }
    }

    /**
     * Loads a certificate.  Assumes the current id is set.
     *
     * @throws InvalidLicenseCertificateException if the license certificate
     * could not be found or read; for instance, no certificate CLOB data found, unable
     * to decrypt, unable to parse XML
     * @throws PersistenceException if there is some other database problem loading
     * @see #setID
     */
    protected void load() throws InvalidLicenseCertificateException, PersistenceException {
        if (getID() == null) {
            throw new NullPointerException("Missing certificate id in load License Certificate");
        }

        // Build query to load license certificate header row
        StringBuffer loadQuery = new StringBuffer();
        loadQuery.append(LicenseDAO.getQueryLoadLicenseCertificate());
        loadQuery.append("where lc.certificate_id = ? ");

        // Fetch the data
        DBBean db = new DBBean();
        try {
            int index = 0;
            db.prepareStatement(loadQuery.toString());
            db.pstmt.setString(++index, getID());
            db.executePrepared();
            if (db.result.next()) {
                // Got a row; populate it
                LicenseDAO.populateLicenseCertificate(db.result, this);

                // Now load the body data
                // First load the clob data
                Clob certificateClob = new Clob(db);
                certificateClob.setTableName(CERTIFICATE_LOB_TABLE_NAME);
                certificateClob.setIDColumnName(CERTIFICATE_LOB_PK_NAME);
                certificateClob.setDataColumnName(CERTIFICATE_LOB_DATA_NAME);
                certificateClob.setID(getID());
                certificateClob.loadReadOnly();

                // Create the license body by unmarshalling the decrypted body
                // XML
                setBody(LicenseCertificateBody.unmarshal(LicenseDAO.decrypt(certificateClob.getData())));

            } else {
                // No row
                throw new InvalidLicenseCertificateException("No certificate for id found: " + getID());
            }


        } catch (SQLException sqle) {
            // Error executing certificate load or populate
            throw new PersistenceException("License certificate load operation failed: " + sqle, sqle);
        } catch (NoDataFoundException e) {
            // Unable to load clob data for certificate key
            throw new InvalidLicenseCertificateException("No clob data found for certificate: " + e, e);
        } catch (net.project.security.EncryptionException e) {
            // Problem decrypting the data
            throw new InvalidLicenseCertificateException("Unable to decrypt license certificate: " + e, e);
        } catch (net.project.xml.XMLException e) {
            // Problem converting xml to an object
            throw new InvalidLicenseCertificateException("Unable to read license certificate: " + e, e);
        } finally {
            db.release();
        }
    }

    /**
     * Verifies that the key associated with this license the same as the
     * specified key.
     * @param key the key to verify
     * @throws LicenseKeyMismatchException if the specified key does not match
     * the key associated with this license certificate
     */
    protected void verifyAssociatedKey(LicenseKey key)
        throws LicenseKeyMismatchException {

        // Verification succeeds if the specified license key is equal to
        // the associated license key
        // An Exception is used since this is a significant problem; it must
        // be handled properly by the caller
        if (!getAssociatedLicenseKey().equals(key)) {
            // Not equal.  Big problem.
            throw new LicenseKeyMismatchException("License key does match certificate");

        } else {
            // Everything is OK
            // Silently succed
        }

    }

    /**
     * Stores this license certificate.  No commit/rollback/release is
     * performed.
     *
     * @param db the DBBean in which to perform the transaction
     * @throws PersistenceException if there is a problem storing
     */
    void store(DBBean db) throws PersistenceException {
        if (getID() == null) {
            create(db);
        } else {
            update(db);
        }
    }

    /**
     * Creates this certificate in the database.
     * @param db the DBBean in which to perform the transaction
     * @throws PersistenceException if there is a problem creating the certificate
     */
    private void create(DBBean db) throws PersistenceException {

        try {
            // Define query to insert header row
            // This row should contain no critical data;  it may hold descriptive
            // information to avoid having to always decode the secure serialized
            // XML data
            StringBuffer insertQuery = new StringBuffer();
            insertQuery.append("insert into pn_license_certificate ");
            insertQuery.append("(certificate_id) ");
            insertQuery.append("values (?) ");

            // Generate new certificate id
            String certificateID = new net.project.database.ObjectManager().getNewObjectID();

            // Generate Certificate Body XML data
            StringBuffer certificateLobData = new StringBuffer();
            getBody().marshal(certificateLobData);
            String secureCertificateLobData = LicenseDAO.encrypt(certificateLobData.toString());

            // Insert certificate row
            int index = 0;
            db.prepareStatement(insertQuery.toString());
            db.pstmt.setString(++index, certificateID);
            db.executePrepared();

            // Write certificate XML data to certificate lob
            Clob certificateClob = new Clob(db);
            certificateClob.setTableName(CERTIFICATE_LOB_TABLE_NAME);
            certificateClob.setIDColumnName(CERTIFICATE_LOB_PK_NAME);
            certificateClob.setDataColumnName(CERTIFICATE_LOB_DATA_NAME);
            certificateClob.setID(certificateID);
            certificateClob.setData(secureCertificateLobData);
            certificateClob.store();

            // Set the newly create id
            // This must occur only after all other operations were a succes
            setID(certificateID);

        } catch (SQLException sqle) {
            throw new PersistenceException("License Certificate create operation failed: " + sqle, sqle);

        } catch (net.project.security.EncryptionException e) {
            throw new PersistenceException("License Certificate create operation failed: " + e, e);

        }

    }

    /**
     * Updates this certificate in the database.
     * @param db the DBBean in which to perform the transaction
     * @throws PersistenceException if there is a problem creating the certificate
     */
    private void update(DBBean db) throws PersistenceException {

        try {
            // Generate Certificate Body XML data
            StringBuffer certificateLobData = new StringBuffer();
            getBody().marshal(certificateLobData);
            String secureCertificateLobData = LicenseDAO.encrypt(certificateLobData.toString());

            // Currently, LicenseCertificate has no attributes that must be
            // updated in the pn_license_certificate row

            // Now update certificate body serialized XML
            Clob certificateClob = new Clob(db);
            certificateClob.setTableName(CERTIFICATE_LOB_TABLE_NAME);
            certificateClob.setIDColumnName(CERTIFICATE_LOB_PK_NAME);
            certificateClob.setDataColumnName(CERTIFICATE_LOB_DATA_NAME);
            certificateClob.setID(getID());
            // Load the data specifically for update
            certificateClob.loadReadWrite();
            certificateClob.setData(secureCertificateLobData);
            certificateClob.store();

        } catch (net.project.security.EncryptionException e) {
            throw new PersistenceException("License Certificate create operation failed: " + e, e);

        }

    }

    /**
     * Returns the xml Element for this LicenseCertificate.
     * @return the element
     */
    public Element getXMLElement() {
        Element rootElement = new Element("LicenseCertificate");
        rootElement.addContent(new Element("CertificateID").addContent(getID()));
        rootElement.addContent(getBody().getXMLElement());
        return rootElement;
    }

    /**
     * Returns the xml representation of this license certificate.  This xml is
     * for public consumption.
     *
     * @return the xml
     */
    net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();

        try {
            doc.startElement("LicenseCertificate");

            doc.addElement("CertificateID", getID());

            // Add license model information
            doc.startElement("LicenseModelCollection");
            for (Iterator it = getLicenseModelCollection().iterator(); it.hasNext();) {
                LicenseModel nextModel = (LicenseModel)it.next();
                doc.addElement(nextModel.getXMLDocument());
            }
            doc.endElement();

            // Add license cost information
            doc.startElement("LicenseCostCollection");
            for (Iterator it = getLicenseCostCollection().iterator(); it.hasNext();) {
                LicenseCost nextCost = (LicenseCost)it.next();
                doc.addElement(nextCost.getXMLDocument());
            }

            doc.endElement();

        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }

}
