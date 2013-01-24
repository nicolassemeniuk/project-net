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
package net.project.billing.invoice;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import net.project.base.IDownloadable;
import net.project.base.property.PropertyProvider;
import net.project.billing.bill.category.Category;
import net.project.billing.bill.category.CategoryID;
import net.project.billing.bill.group.BillGroup;
import net.project.billing.bill.group.GroupTypeID;
import net.project.billing.ledger.LedgerEntry;
import net.project.billing.payment.PaymentModelTypeID;
import net.project.database.Clob;
import net.project.database.DBBean;
import net.project.license.LicenseException;
import net.project.license.system.MasterProperties;
import net.project.license.system.MasterPropertiesNotFoundException;
import net.project.license.system.PropertyName;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.xml.document.XMLDocument;

import org.jdom.Element;


/**
 * Provides access to the license models supported by a license.
 */
public class Invoice implements Serializable, IXMLPersistence, IDownloadable {

    //
    // Static members
    //

    /**
     * The table name of the table containing the lob data.
     */
    private static final String INVOICE_LOB_TABLE_NAME = "pn_invoice_lob";

    /**
     * The primary key column name in the table containing the lob data.
     */
    private static final String INVOICE_LOB_PK_NAME = "invoice_id";

    /**
     * The data column name containing the lob data.
     */
    private static final String INVOICE_LOB_DATA_NAME = "invoice_lob_data";


    //
    // Instance members
    //

    /** The id of this invoice. */
    private String invoiceID = null;

    /** The creation date  of this invoice. */
    private Date creationDate = null;

    /** The due date  of this invoice. */
    private Date dueDate = null;

    /** The customer billing info for this invoice. */
    private String customerBillingInfo = null;

    /** Indicates if this invoice is loaded. */
    private boolean isLoaded = false;

    /**
     * The body of the invoice.
     * This is separated from the invoice to make it easier
     * to marshal and unmarshal to/from XML and store in the database.
     * It helps to encapsulate those properties of the invoice that must
     * always be stored in an encrypted fashion.
     * Note that the body is private and is _never_ exposed to a client of
     * the Invoice class.
     */
    private InvoiceBody invoiceBody = new InvoiceBody();

    /**
     * Creates an empty Invoice.
     */
    public Invoice() {
        // Nothing
    }

    /**
     * Creates a new Invoice based on the specified invoice body.
     * @param body the body for this invoice
     */
    Invoice(InvoiceBody body) {
        setBody(body);
    }

    /**
     * Sets this invoice's id.
     * @param invoiceID the id of this invoice
     * @see #getID
     */
    public void setID(String invoiceID) {
        this.invoiceID = invoiceID;
    }

    /**
     * Returns this invoice's id.
     * @return the id of this invoice
     * @see #setID
     */
    public String getID() {
        return this.invoiceID;
    }

    /**
     * Sets this invoice's creation date.
     * @param creationDate the creation date of this invoice
     * @see #getID
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Returns this invoice's creation date.
     * @return the creation date of this invoice
     * @see #setCreationDate
     */
    public Date getCreationDate() {
        if (this.creationDate == null) {
            setCreationDate(new java.util.Date());
        }
        return this.creationDate;
    }

    /**
     * Sets this invoice's due date.
     * @param dueDate the due date of this invoice
     * @see #getDueDate
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Returns this invoice's due date.
     * @return the due date of this invoice
     * @see #setDueDate
     */
    public Date getDueDate() {

        int days = Integer.parseInt(this.getTerm());
        if (this.dueDate == null) {

            setDueDate(net.project.util.DateUtils.addDay(getCreationDate(), days));
        }

        return this.dueDate;
    }

    /**
     * Sets this invoice's billing info.
     * @param billingInfo the billing info of this invoice
     * @see #getCustomerBillingInfo
     */
    public void setCustomerBillingInfo(String billingInfo) {
        this.customerBillingInfo = billingInfo;
    }

    /**
     * Returns this invoice's billing info.
     * @return the billing info of this invoice
     * @see #setCustomerBillingInfo
     */
    public String getCustomerBillingInfo() {

        String billingInfo = null;

        if (this.customerBillingInfo == null) {

            try {
                billingInfo = net.project.license.system.MasterProperties.getInstance().get(PropertyName.LICENSE_BILLING_INVOICE_CUSTOMERINFO).getValue();
                setCustomerBillingInfo(billingInfo);
            } catch (LicenseException le) {
                // Can not do much
            } catch (PersistenceException pe) {
                // Can't do much about it
            }

        }

        return this.customerBillingInfo;
    }

    /**
     * Gets the invoice term for this invoice.
     * The invoice term is obtained from a master property.
     * @return term, String value representing the invoice term
     */
    public String getTerm() {
        String term = null;
        try {
            term = net.project.license.system.MasterProperties.getInstance().get(PropertyName.LICENSE_BILLING_INVOICE_TERM).getValue();
        } catch (LicenseException le) {
            // Can not do much
        } catch (PersistenceException pe) {
            // Can't do much about it
        }
        return term;

    }

    /**
     * Returns value indicating if this invoice is loaded.
     * @return true, if this invoice is loaded
     */
    public boolean isLoaded() {
        return this.isLoaded;
    }

    /**
     * Sets the invoice body for this invoice.
     * @param body the invoice body
     */
    private void setBody(InvoiceBody body) {
        this.invoiceBody = body;
    }

    /**
     * Returns the invoice body.
     * @return the invoice body
     */
    InvoiceBody getBody() {
        return this.invoiceBody;
    }


    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(super.toString()).append("\n");
        result.append(this.invoiceBody).append("\n");
        return result.toString();
    }

    /**
     * Adds a LedgerEntry to this invoice.
     * @param ledgerEntry the ledger entry to add
     */
    void addLedgerEntry(LedgerEntry ledgerEntry) {
        getBody().getLedgerEntryCollection().add(ledgerEntry);
    }

    /**
     * Returns the ledger entries in this invoice.
     * @return the collection of ledger entries; each element is of type
     * <code>LedgerEntry</code>
     * @see net.project.billing.ledger.LedgerEntry
     */
    Collection getLedgerEntryCollection() {
        return getBody().getLedgerEntryCollection();
    }

    /**
     * Loads a certificate.
     * Assumes the current id is set.
     * @throws PersistenceException if there is a problem loading
     * @see #setID
     */
    public void load() throws PersistenceException {
        if (getID() == null) {
            throw new NullPointerException("Missing invoice id in load Invoice");
        }

        // Load the creation date of this invoice
        loadInvoiceDate();

        // Build query to load invoice header row
        StringBuffer loadQuery = new StringBuffer();
        loadQuery.append(InvoiceDAO.getQueryLoadInvoice());
        loadQuery.append("where pi.invoice_id = ? ");

        // Fetch the data
        DBBean db = new DBBean();
        try {
            int index = 0;
            db.prepareStatement(loadQuery.toString());
            db.pstmt.setString(++index, getID());
            db.executePrepared();
            if (db.result.next()) {
                // Got a row; populate it
                InvoiceDAO.populateInvoice(db.result, this);

                // Now load the body data
                // First load the clob data
                Clob invoiceClob = new Clob(db);
                invoiceClob.setTableName(INVOICE_LOB_TABLE_NAME);
                invoiceClob.setIDColumnName(INVOICE_LOB_PK_NAME);
                invoiceClob.setDataColumnName(INVOICE_LOB_DATA_NAME);
                invoiceClob.setID(getID());
                invoiceClob.loadReadOnly();
                //System.out.println("Invoice.java : load() 1. \n" + InvoiceDAO.decrypt(invoiceClob.getData()));
                // Create the invoice body by unmarshalling the decrypted body
                // XML
                setBody(InvoiceBody.unmarshal(InvoiceDAO.decrypt(invoiceClob.getData())));
                //System.out.println("Invoice.java : load() 2. \n" + this.getXML());

            } else {
                // No row
                throw new PersistenceException("Invoice load operation failed: No invoice for id");
            }
            this.isLoaded = true;

        } catch (SQLException sqle) {
            throw new PersistenceException("Invoice load operation failed: " + sqle, sqle);

        } catch (net.project.security.EncryptionException e) {
            // Problem decrypting the data
            throw new PersistenceException("Invoice load operation failed: " + e, e);

        } catch (net.project.xml.XMLException e) {
            // Problem converting xml to an object
            throw new PersistenceException("Invoice load operation failed: " + e, e);

        } finally {
            db.release();

        }

    }

    public void loadInvoiceDate()
        throws PersistenceException {

        String query = "select creation_datetime from pn_invoice where invoice_id = ?";
        int index = 0;
        DBBean db = new DBBean();

        try {

            db.prepareStatement(query);
            db.pstmt.setString(++index, this.getID());
            db.executePrepared();

            while (db.result.next()) {
                this.setCreationDate(db.result.getDate("creation_datetime"));
            }

        } catch (SQLException sqle) {
            throw new PersistenceException("Invoice load operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }
    }

    /**
     * Stores this invoice.
     * No commit/rollback/release is performed.
     * @param db the DBBean in which to perform the transaction
     * @throws PersistenceException if there is a problem storing
     */
    void store(DBBean db) throws PersistenceException {

        if (getID() == null) {
            create(db);
        }
    }

    /**
     * Creates this invoice in the database.
     * @param db the DBBean in which to perform the transaction
     * @throws PersistenceException if there is a problem creating the invoice
     */
    private void create(DBBean db) throws PersistenceException {

        try {
            // Define query to insert header row
            // This row should contain no critical data;  it may hold descriptive
            // information to avoid having to always decode the secure serialized
            // XML data
            StringBuffer insertQuery = new StringBuffer();
            insertQuery.append("insert into pn_invoice ");
            insertQuery.append("(invoice_id, creation_datetime) ");
            insertQuery.append("values (?, ?) ");

            // Generate new invoice id
            String invoiceID = new net.project.database.ObjectManager().getNewObjectID();

            // Generate Certificate Body XML data
            StringBuffer invoiceLobData = new StringBuffer();
            getBody().marshal(invoiceLobData);
            //System.out.println("Invoice.java : the invoicebody is \n" + this.getXMLDocument().getXMLString());
            String secureInvoiceLobData = InvoiceDAO.encrypt(invoiceLobData.toString());

            // Insert certificate row
            int index = 0;
            db.prepareStatement(insertQuery.toString());
            db.pstmt.setString(++index, invoiceID);
            db.pstmt.setTimestamp(++index, new java.sql.Timestamp(getCreationDate().getTime()));
            db.executePrepared();

            // Write certificate XML data to certificate lob
            Clob invoiceClob = new Clob(db);
            invoiceClob.setTableName(INVOICE_LOB_TABLE_NAME);
            invoiceClob.setIDColumnName(INVOICE_LOB_PK_NAME);
            invoiceClob.setDataColumnName(INVOICE_LOB_DATA_NAME);
            invoiceClob.setID(invoiceID);
            invoiceClob.setData(secureInvoiceLobData);
            invoiceClob.store();

            // Set the newly create id
            // This must occur only after all other operations were a succes
            setID(invoiceID);

        } catch (SQLException sqle) {
            throw new PersistenceException("Invoice create operation failed: " + sqle, sqle);

        } catch (net.project.security.EncryptionException e) {
            throw new PersistenceException("Invoice create operation failed: " + e, e);

        }

    }


    /**
     * Returns the xml Element for this Invoice.
     * @return the element
     */
    public Element getXMLElement() {
        Element rootElement = new Element("Invoice");
        rootElement.addContent(new Element("InvoiceID").addContent(getID()));
        rootElement.addContent(getBody().getXMLElement());
        return rootElement;
    }

    public String getXML() {
        return getXMLDocument().getXMLString();
    }

    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    /**
     * Returns the xml representation of this invoice.
     * This xml is for public consumption.
     * @return the xml
     */
    net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();

        try {
            doc.startElement("Invoice");

            doc.startElement("InvoiceSummary");
            doc.addElement("InvoiceID", getID());
            doc.addElement("InvoiceDate", this.getCreationDate());
            doc.addElement("InvoiceTerm", this.getTerm());
            doc.addElement("DueDate", this.getDueDate());
            doc.addElement("ProjectNetInfo", PropertyProvider.get("prm.global.billing.invoice.address"));
            doc.addElement("CustomerBillingInfo", this.getCustomerBillingInfo());
            addLedgerSummary(doc);
            doc.endElement();

            // Add ledger entry information
            doc.startElement("LedgerEntryCollection");
            addLedgerDetails(doc);
            doc.endElement();

        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }

    /*** Helper Methods to complete the Invoice XML document **********/

    private void addLedgerDetails(XMLDocument doc) {

        try {

            doc.startElement("LedgerDetails");

            // Group entries into following three types
            ArrayList chargeCodeEntryList = new ArrayList();
            ArrayList creditCardEntryList = new ArrayList();
            ArrayList trialEntryList = new ArrayList();

            Set uniqueChargeCodeSet = new TreeSet();
            Set uniqueCreditCardSet = new TreeSet();

            for (Iterator it = getLedgerEntryCollection().iterator(); it.hasNext();) {
                LedgerEntry nextEntry = (LedgerEntry)it.next();
                if (nextEntry.getPaymentInformation().getPaymentModel().getPaymentModelTypeID().equals(PaymentModelTypeID.CHARGE_CODE)) {
                    chargeCodeEntryList.add(nextEntry);
                    uniqueChargeCodeSet.add(nextEntry.getPaymentInformation().getPaymentModel().getIdentifyingValue());

                } else if (nextEntry.getPaymentInformation().getPaymentModel().getPaymentModelTypeID().equals(PaymentModelTypeID.CREDIT_CARD)) {
                    creditCardEntryList.add(nextEntry);
                    uniqueCreditCardSet.add(nextEntry.getPaymentInformation().getPaymentModel().getIdentifyingValue());

                } else if (nextEntry.getPaymentInformation().getPaymentModel().getPaymentModelTypeID().equals(PaymentModelTypeID.TRIAL)) {
                    trialEntryList.add(nextEntry);
                }
            }


            getChargeCodeEntries(doc, uniqueChargeCodeSet, chargeCodeEntryList);
            getCreditCardEntries(doc, uniqueCreditCardSet, creditCardEntryList);
            getTrialEntries(doc, trialEntryList);

            doc.endElement();

        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }
    }

    private void getChargeCodeEntries(XMLDocument doc, Set uniqueChargeCodeSet, ArrayList chargeCodeEntryList) {

        // Get the ledger entries and Calculate the subtotal for each charge code
        double quantity = 0;
        double unitPrice = 0;
        double cost = 0;
        try {
            doc.startElement("ChargeCodeEntries");
            for (Iterator iter = uniqueChargeCodeSet.iterator(); iter.hasNext();) {

                String chargeCode = (String)iter.next();
                double chargeCodeSubTotal = 0.0;

                doc.startElement("ChargeCode");
                doc.addElement("ChargeCodeNumber", chargeCode);

                for (Iterator it = chargeCodeEntryList.iterator(); it.hasNext();) {

                    LedgerEntry nextEntry = (LedgerEntry)it.next();
                    if (nextEntry.getPaymentInformation().getPaymentModel().getIdentifyingValue().equals(chargeCode)) {

                        quantity = nextEntry.getQuantity().getValue().doubleValue();
                        unitPrice = nextEntry.getUnitPrice().getValue().doubleValue();
                        cost = quantity * unitPrice;
                        chargeCodeSubTotal = chargeCodeSubTotal + cost;
                        doc.addElement(nextEntry.getXMLDocument());
                        it.remove();
                    }

                }

                doc.addElement("ChargeCodeSubTotal", Double.toString(chargeCodeSubTotal));
                doc.endElement();

            }
            doc.endElement();

        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }
    }

    private void getCreditCardEntries(XMLDocument doc, Set uniqueCreditCardSet, ArrayList creditCardEntryList) {

        // Get the ledger entries and calculate the subtotal for each creditcard
        double quantity = 0;
        double unitPrice = 0;
        double cost = 0;
        try {

            doc.startElement("CreditCardEntries");

            for (Iterator iter = uniqueCreditCardSet.iterator(); iter.hasNext();) {

                String creditCard = (String)iter.next();
                double creditCardSubTotal = 0.0;

                doc.startElement("CreditCard");
                doc.addElement("CreditCardNumber", creditCard);

                for (Iterator it = creditCardEntryList.iterator(); it.hasNext();) {

                    LedgerEntry nextEntry = (LedgerEntry)it.next();
                    if (nextEntry.getPaymentInformation().getPaymentModel().getIdentifyingValue().equals(creditCard)) {

                        quantity = nextEntry.getQuantity().getValue().doubleValue();
                        unitPrice = nextEntry.getUnitPrice().getValue().doubleValue();
                        cost = quantity * unitPrice;
                        creditCardSubTotal = creditCardSubTotal + cost;
                        doc.addElement(nextEntry.getXMLDocument());
                        it.remove();
                    }

                }

                doc.addElement("CreditCardSubTotal", Double.toString(creditCardSubTotal));
                doc.endElement();

            }
            doc.endElement();

        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }
    }

    private void getTrialEntries(XMLDocument doc, ArrayList trialEntryList) {

        // Get the ledger entries and calculate the trial costs
        double quantity = 0;
        double unitPrice = 0;
        double cost = 0;
        double trialSubTotal = 0;


        try {

            doc.startElement("TrialEntries");

            for (Iterator it = trialEntryList.iterator(); it.hasNext();) {

                LedgerEntry nextEntry = (LedgerEntry)it.next();
                quantity = nextEntry.getQuantity().getValue().doubleValue();
                unitPrice = nextEntry.getUnitPrice().getValue().doubleValue();
                cost = quantity * unitPrice;
                trialSubTotal = trialSubTotal + cost;
                doc.addElement(nextEntry.getXMLDocument());
                it.remove();

            }

            doc.addElement("TrialSubTotal", Double.toString(trialSubTotal));
            doc.endElement();

        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }
    }

    private void addLedgerSummary(XMLDocument doc) {

        double totalLicenseUsageCost = 0;
        double totalLicenseMaintenanceCost = 0;
        double totalTrialLicenseCost = 0;

        double totalUsageQuantity = 0;
        double totalMaintenanceQuantity = 0;
        double totalTrialQuantity = 0;

        double quantity = 0;
        double unitPrice = 0;
        double cost = 0;

        Date minDate = null;
        Date maxDate = null;

        for (Iterator it = getLedgerEntryCollection().iterator(); it.hasNext();) {

            LedgerEntry nextEntry = (LedgerEntry)it.next();
//Check and set the min and max date of an entry
            Date dueSinceDate = nextEntry.getDueSinceDate();
            if (minDate != null && maxDate != null) {
                if (minDate.after(dueSinceDate)) {
                    minDate = dueSinceDate;
                }
                if (maxDate.before(dueSinceDate)) {
                    maxDate = dueSinceDate;
                }

            } else {
                minDate = dueSinceDate;
                maxDate = dueSinceDate;
            }

            Category category = nextEntry.getCategory();
            BillGroup billGroup = nextEntry.getBillGroup();
            quantity = nextEntry.getQuantity().getValue().doubleValue();
            unitPrice = nextEntry.getUnitPrice().getValue().doubleValue();
            cost = quantity * unitPrice;

            if (category.getID().equals(CategoryID.LICENSE_USAGE_TYPE_A)) {

                if (billGroup.getGroupTypeID().equals(GroupTypeID.CHARGE_CODE)) {
                    totalLicenseUsageCost = totalLicenseUsageCost + cost;
                    totalUsageQuantity = totalUsageQuantity + quantity;

                } else if (billGroup.getGroupTypeID().equals(GroupTypeID.TRIAL)) {
                    totalTrialLicenseCost = totalTrialLicenseCost + cost;
                    totalTrialQuantity = totalTrialQuantity + quantity;
                }

            } else if (category.getID().equals(CategoryID.LICENSE_MAINTENANCE_TYPE_B)) {
                totalLicenseMaintenanceCost = totalLicenseMaintenanceCost + cost;
                totalMaintenanceQuantity = totalMaintenanceQuantity + quantity;
            }


        }
        try {
            double allTotalCost = totalLicenseUsageCost + totalLicenseMaintenanceCost + totalTrialLicenseCost;
            double usageUnitCost = Double.parseDouble(MasterProperties.getInstance().get(PropertyName.LICENSE_COST_BASE).getValue());
            double maintenanceUnitCost = ((usageUnitCost * Double.parseDouble(MasterProperties.getInstance().get(PropertyName.LICENSE_COST_MAINTENANCE).getValue())) / 100.0);
            double trialUnitCost = Double.parseDouble(MasterProperties.getInstance().get(PropertyName.LICENSE_COST_TRIAL).getValue());


            doc.startElement("LedgerSummary");

            doc.startElement("DateRange");
            doc.addElement("MinDate", minDate);
            doc.addElement("MaxDate", maxDate);
            doc.endElement();

            doc.startElement("LicenseUsage");
            doc.addElement("TotalUsageCost", Double.toString(totalLicenseUsageCost));
            doc.addElement("TotalUsageQuantity", Double.toString(totalUsageQuantity));
            doc.addElement("UsageUnitCost", Double.toString(usageUnitCost));
            doc.endElement();

            doc.startElement("LicenseMaintenance");
            doc.addElement("TotalMaintenanceCost", Double.toString(totalLicenseMaintenanceCost));
            doc.addElement("TotalMaintenanceQuantity", Double.toString(totalMaintenanceQuantity));
            doc.addElement("MaintenanceUnitCost", Double.toString(maintenanceUnitCost));
            doc.endElement();

            doc.startElement("TrialLicense");
            doc.addElement("TotalTrialCost", Double.toString(totalTrialLicenseCost));
            doc.addElement("TotalTrialQuantity", Double.toString(totalTrialQuantity));
            doc.addElement("TrialUnitCost", Double.toString(trialUnitCost));
            doc.endElement();

            doc.addElement("AllTotal", Double.toString(allTotalCost));

            doc.endElement();

        } catch (net.project.xml.document.XMLDocumentException e) {
// Nothing much can be done about this
// Simply return the empty document
        } catch (MasterPropertiesNotFoundException mpnfe) {
            // No master Properties installed

        } catch (net.project.license.LicenseException le) {
            // Very unlikely scenario
            // Simply return the empty document

        } catch (PersistenceException pe) {

        }


    }

    /*******  End Helper methods to complete the invoice xml document ********/

    //*********** Implementing IDownloadable interface *************//
    /**
     * Returns the file name that this object will be saved to.
     * This is the default file name.  Most browsers will
     * allow the user to override it.
     * @return the filename, <Code>Invoice.xml</Code>
     */
    public String getFileName() {
        return "Invoice.xml";
    }

    /**
     * Returns the content type of the invoice data.
     * @return <code>text/xml</code>
     */
    public String getContentType() {
        return "text/xml; charset=" + SessionManager.getCharacterEncoding();
    }

    /**
     * Returns the input stream for this invoice data.
     * @return the stream
     */
    public java.io.InputStream getInputStream()
        throws java.io.IOException {
        try {

            return new java.io.ByteArrayInputStream(getXML().getBytes(SessionManager.getCharacterEncoding()));
        } catch (java.io.UnsupportedEncodingException uee) {
            // No data
            return new java.io.ByteArrayInputStream(new byte[0]);
        }
    }

    /**
     * Returns the length of this data.
     * @return this invoice data's length
     */
    public long getLength() {
        return this.getXML().getBytes().length;
    }

    /**
     * Returns this invoice data as email attachment.
     * @return emailAttachment, this invoice's data as email attachment
     */
    public InvoiceEmailAttachment getEmailAttachment()
        throws java.io.IOException {
        InvoiceEmailAttachment emailAttach = new InvoiceEmailAttachment();
        emailAttach.setDataSource(new net.project.notification.email.ByteArrayDataSource(this.getXML(), "text/xml"));
        return emailAttach;
    }

    /**
     * Provides a Email attachment of this invoice's data.
     */
    private static class InvoiceEmailAttachment
        implements net.project.notification.email.IEmailAttachment {

        private javax.activation.DataSource dataSource = null;
        private String name = "Invoice.xml";

        /**
         * Returns the name of this attachment.
         * @return the name of this attachment
         */
        public String getName() {
            return name;
        }

        /**
         * Returns the data source that represents the data of this attachment.
         * @return the data source for this attachment data
         */
        public javax.activation.DataSource getDataSource() {
            return this.dataSource;

        }

        /**
         * Sets the data source that represents the data of this attachment.
         * @param dataSource the data source for this attachment data
         */
        public void setDataSource(javax.activation.DataSource dataSource) {
            this.dataSource = dataSource;

        }
    }
}
