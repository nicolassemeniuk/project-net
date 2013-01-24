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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.billing.ledger;

import java.util.Date;
import java.util.Iterator;

import net.project.base.money.Money;
import net.project.base.quantity.Quantity;
import net.project.base.quantity.UnitOfMeasureCollection;
import net.project.billing.bill.Bill;
import net.project.billing.bill.PartDetails;
import net.project.billing.bill.ResponsibleParty;
import net.project.billing.bill.category.Category;
import net.project.billing.bill.category.CategoryID;
import net.project.billing.bill.group.BillGroup;
import net.project.billing.bill.group.GroupType;
import net.project.billing.bill.group.GroupTypeID;
import net.project.billing.invoice.InvoiceStatus;
import net.project.billing.payment.PaymentInformation;
import net.project.database.DBBean;
import net.project.database.ObjectManager;
import net.project.license.LicenseException;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.xml.XMLUtils;

import org.jdom.Element;


/**
 * A LedgerEntry is a one time permanent entry in the ledger.
 * It is the basis of generating an invoice.
 */
public class LedgerEntry extends Bill implements IXMLPersistence {


    /**
     * The id of this entry made in the ledger.
     */
    private String id = null;

    /**
     *  The party that will be responsible for payment indicated in this entry.
     */
    private ResponsibleParty resParty = null;

    /**
     * The id of the bill for which this entry has been made in the ledger.
     */
    private String billID = null;

    /**
     * The id of the invoice in which this entry is included.
     */
    private String invoiceID = null;

    /**
     * The status of this entry indicating whether it is included in an invoice.
     */
    private InvoiceStatus invoiceStatus = null;

    /**
     * The date since this entry is due
     */
    private Date dueSinceDate = null;

    /**
     * The date this entry was invoiced
     */
    private Date invoiceDate = null;


    /**
     * Constructs an empty ledger entry.
     */
    public LedgerEntry() {
        super();

    }

    /**
     * Constructs a ledger entry.
     * @param bill the bill from which to initialize this ledger entry
     * @param db the database bean in which to perform the transaction. We intentionally
     * use the db bean from the caller to ensure that the data written out to the database
     * in this transaction is available for use.
     */
    public LedgerEntry(Bill bill, DBBean db)
        throws PersistenceException, LicenseException {
        initialize(bill, db);

    }

    /**
     * Sets the id of this entry.
     *
     * @param id a <code>String</code> containing the id
     */
    public void setID(String id) {

        this.id = id;
    }

    /**
     * Gets the id corresponding to this entry.
     * @return String id
     */
    public String getID() {

        return this.id;
    }

    /**
     * Sets the responsible party of this entry.
     * @param resParty the ResponsibleParty
     */
    public void setResponsibleParty(ResponsibleParty resParty) {

        this.resParty = resParty;
    }

    /**
     * Gets the responsible party of this entry.
     * @return ResponsibleParty
     */
    public ResponsibleParty getResponsibleParty() {

        return this.resParty;
    }

    /**
     * Sets the bill id of this entry.
     * @param billID a <code>String</code> containing the billID
     */
    public void setBillID(String billID) {

        this.billID = billID;
    }

    /**
     * Gets the bill id corresponding to this entry.
     * @return String billID
     */
    public String getBillID() {

        return this.billID;
    }

    /**
     * Sets the invoice id of this entry.
     * @param invoiceID a <code>String</code> containing the invoiceID
     */
    public void setInvoiceID(String invoiceID) {

        this.invoiceID = invoiceID;
    }

    /**
     * Gets the invoice id of this entry.
     * @return String invoiceID
     */
    public String getInvoiceID() {

        return this.invoiceID;
    }

    /**
     * Sets the invoice status of this entry.
     * @param invoiceStatus this entry's invoice status
     */
    public void setInvoiceStatus(InvoiceStatus invoiceStatus) {

        this.invoiceStatus = invoiceStatus;
    }

    /**
     * Gets the invoice status of this entry.
     * @return invoiceStatus, the entry's invoice status
     */
    public InvoiceStatus getInvoiceStatus() {

        return this.invoiceStatus;
    }

    /**
     * Sets the invoice date of this entry.
     * @param invoiceDate this entry's invoice date
     */
    public void setInvoiceDate(Date invoiceDate) {

        this.invoiceDate = invoiceDate;
    }

    /**
     * Gets the invoice date of this entry if it is invoiced.
     * @return invoiceDate, the entry's invoice date
     */
    public Date getInvoiceDate() {

        return this.invoiceDate;
    }

    /**
     * Sets the date since this entry has become due.
     *
     * @param dueSinceDate a <code>Date</code> value on which this entry became
     * due.
     */
    public void setDueSinceDate(java.util.Date dueSinceDate) {
        this.dueSinceDate = dueSinceDate;
    }

    /**
     * Gets the due date since this entry has become due.
     * @return Date, the due date
     */
    public java.util.Date getDueSinceDate() {

        return this.dueSinceDate;
    }

    /**
     * Returns the xml Element for this LedgerEntry.
     * @return the element
     */
    public org.jdom.Element getXMLElement() {
        return getXMLDocument().getRootElement();
    }

    /**
     * Initializes this ledger entry from the given bill.
     * @param bill the bill from which to initialize this entry.
     */
    private void initialize(Bill bill, DBBean db)
        throws PersistenceException, LicenseException {
        this.setBillID(bill.getID());
        this.setBillGroup(bill.getBillGroup());
        this.setCategory(bill.getCategory());
        this.setDueSinceDate(bill.getDueness().getDueDate());
        this.setPartDetails(bill.getPartDetails());
        this.setPaymentInformation(bill.getPaymentInformation());
        this.setQuantity(bill.getQuantity());
        this.setResponsibleParty(new ResponsibleParty(bill.getID(), db));
        this.setUnitPrice(bill.getUnitPrice());

    }

    public void store(net.project.database.DBBean db)
        throws net.project.persistence.PersistenceException, net.project.license.LicenseException {

        // Generate new ledgerID
        String ledgerID = new ObjectManager().getNewObjectID();
        int index = 0;
        StringBuffer query = new StringBuffer();

        query.append("insert into pn_ledger (ledger_id, bill_id, responsible_party_id, due_since_datetime, ");
        query.append(" unit_price_value, quantity_amount, quantity_uom_id, category_id, part_details_part_number, ");
        query.append(" part_details_part_description, group_type_id, group_value, group_description, ");
        query.append(" originating_payment_id, record_status, invoice_status_id ) ");
        query.append(" values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)  ");

        try {

            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, ledgerID);
            db.pstmt.setString(++index, this.getBillID());
            db.pstmt.setString(++index, this.getResponsibleParty().getID());
            db.pstmt.setTimestamp(++index, new java.sql.Timestamp(this.getDueSinceDate().getTime()));
            db.pstmt.setString(++index, this.getUnitPrice().getValue().toString());
            db.pstmt.setString(++index, this.getQuantity().getValue().toString());
            db.pstmt.setString(++index, this.getQuantity().getUnitOfMeasure().getID());
            db.pstmt.setString(++index, this.getCategory().getID().getID());
            db.pstmt.setString(++index, this.getPartDetails().getPartNumber());
            db.pstmt.setString(++index, this.getPartDetails().getPartDescription());
            db.pstmt.setString(++index, this.getBillGroup().getGroupTypeID().getID());
            db.pstmt.setString(++index, this.getBillGroup().getValue());
            db.pstmt.setString(++index, this.getBillGroup().getDescription());
            db.pstmt.setString(++index, this.getPaymentInformation().getID());
            db.pstmt.setString(++index, "A");
            db.pstmt.setString(++index, InvoiceStatus.NOT_INVOICED.getID());

            db.executePrepared();

        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("LedgerEntry.java : Problem storing " +
                "LedgerEntry, SQLEXception thrown :" + sqle.getMessage(), sqle);
        }
    }

    public void load()
        throws net.project.persistence.PersistenceException, net.project.license.LicenseException {

        DBBean db = new DBBean();
        try {
            load(db);
        } finally {
            db.release();

        }
    }

    public void load(net.project.database.DBBean db)
        throws net.project.persistence.PersistenceException, net.project.license.LicenseException {

        // Assumes that the id is set for this entry.
        if (this.getID() == null) {
            throw new NullPointerException("LedgerEntry load operation failed : The ledgerID for this entry is not set.");
        }
        int index = 0;

        String query = "select * from pn_ledger where ledger_id = ?";

        try {

            db.prepareStatement(query);
            db.pstmt.setString(++index, this.getID());
            db.executePrepared();

            while (db.result.next()) {
                BillGroup billGroup = new BillGroup();
                billGroup.setDescription(db.result.getString("group_description"));
                billGroup.setValue(db.result.getString("group_value"));
                billGroup.setGroupType(new GroupType(GroupTypeID.forID(db.result.getString("group_type_id"))));
                billGroup.setGroupTypeID(GroupTypeID.forID(db.result.getString("group_type_id")));
                this.setBillGroup(billGroup);

                this.setBillID(db.result.getString("bill_id"));

                CategoryID categoryID = new CategoryID(db.result.getString("category_id"));
                Category category = new Category();
                category.setID(categoryID);
                this.setCategory(category);

                this.setDueSinceDate(db.result.getTimestamp("due_since_datetime"));
                this.setInvoiceID(db.result.getString("invoice_id"));
                this.setInvoiceStatus(new InvoiceStatus(db.result.getString("invoice_status_id")));
                this.setInvoiceDate(db.result.getTimestamp("invoice_date"));

                PartDetails partDetails = new PartDetails();
                partDetails.setPartDescription(db.result.getString("part_details_part_description"));
                partDetails.setPartNumber(db.result.getString("part_details_part_number"));
                this.setPartDetails(partDetails);

                PaymentInformation paymentInformation = new PaymentInformation();
                paymentInformation.setID(db.result.getString("originating_payment_id"));
                paymentInformation.load();
                this.setPaymentInformation(paymentInformation);

                Quantity quantity = new Quantity();
                quantity.setUnitOfMeasure(UnitOfMeasureCollection.getAll().getForID(db.result.getString("quantity_uom_id")));
                quantity.setValue(new Integer(db.result.getString("quantity_amount")));
                this.setQuantity(quantity);

                ResponsibleParty responsibleParty = new ResponsibleParty();
                responsibleParty.setID(db.result.getString("responsible_party_id"));
                responsibleParty.load();
                this.setResponsibleParty(responsibleParty);

                this.setUnitPrice(new Money(db.result.getString("unit_price_value")));

            }

        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("LedgerEntry.java : Problem loading " +
                "LedgerEntry, SQLEXception thrown :" + sqle.getMessage(), sqle);
        }
    }

    /**
     Converts the object to XML representation.
     This method returns the object as XML text.
     @return XML representation
     */
    public String getXML() {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }

    /**
     Converts the object to XML representation without the XML version tag.
     This method returns the object as XML text.
     @return XML representation
     */
    public String getXMLBody() {

        StringBuffer xml = new StringBuffer();

        xml.append(getXMLDocument().getXMLBodyString());

        return xml.toString();
    }

    /**
     * Returns this ledger entry as xml
     * @return the xml for this ledger entry
     */
    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();

        try {
            doc.startElement("LedgerEntry");
            doc.addElement("LedgerID", getID());
            doc.addElement("BillID", getBillID());

            doc.addElement(this.getResponsibleParty().getXMLDocument());

            doc.addElement("DueSinceDate", this.getDueSinceDate());
            doc.addElement("InvoiceID", this.getInvoiceID());
            doc.addElement("InvoiceDate", this.getInvoiceDate());
            doc.addElement("TotalCost", Double.toString(this.getTotalCost()));

            doc.addElement(this.getInvoiceStatus().getXMLDocument());

            doc.addElement(this.getBillGroup().getXMLDocument());

            doc.addElement(this.getCategory().getXMLDocument());

            doc.addElement(this.getPartDetails().getXMLDocument());

            doc.addElement(this.getPaymentInformation().getXMLDocument());

            doc.addElement(this.getQuantity().getXMLDocument());

            doc.addElement(this.getUnitPrice().getXMLDocument());

            doc.endElement();

        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }


    /**
     * Populates this ledger entry from the xml element.
     * @param element the xml element from which to populate this ledger entry
     */
    protected void populate(Element element)
        throws net.project.xml.XMLException {

        // Iterate over each child element and handle each one
        Element childElement = null;
        for (Iterator it = element.getChildren().iterator(); it.hasNext();) {
            childElement = (Element)it.next();

            if (childElement.getName().equals("LedgerID")) {
                this.setID(childElement.getTextTrim());

            } else if (childElement.getName().equals("BillID")) {
                this.setBillID(childElement.getTextTrim());

            } else if (childElement.getName().equals("ResponsibleParty")) {
                setResponsibleParty(ResponsibleParty.create(childElement));

            } else if (childElement.getName().equals("DueSinceDate")) {
                // DueSinceDate is a date
                setDueSinceDate(XMLUtils.parseDateFromXML(childElement.getTextTrim()));

            } else if (childElement.getName().equals("InvoiceID")) {
                setInvoiceID(childElement.getTextTrim());

            } else if (childElement.getName().equals("InvoiceStatus")) {
                setInvoiceStatus(InvoiceStatus.create(childElement));

            } else if (childElement.getName().equals("BillGroup")) {
                setBillGroup(BillGroup.create(childElement));

            } else if (childElement.getName().equals("Category")) {
                setCategory(Category.create(childElement));

            } else if (childElement.getName().equals("PartDetails")) {
                setPartDetails(PartDetails.create(childElement));

            } else if (childElement.getName().equals("PaymentInformation")) {
                setPaymentInformation(PaymentInformation.create(childElement));

            } else if (childElement.getName().equals("Quantity")) {
                setQuantity(Quantity.create(childElement));

            } else if (childElement.getName().equals("Money")) {
                setUnitPrice(Money.create(childElement));

            }
        }

    }

    // Static Members

    /**
     * Creates a ledger entry from the specified ledger entry element.
     * The ledger entry is fully populated from the element.
     * @param ledgerEntryElement the xml element from which to create the
     * ledger entry
     * @return the ledger entry  for the ledgerEntryElement
     */
    public static LedgerEntry create(org.jdom.Element ledgerEntryElement)
        throws net.project.xml.XMLException {

        LedgerEntry ledgerEntry = new LedgerEntry();
        ledgerEntry.populate(ledgerEntryElement);

        return ledgerEntry;
    }

    // Helper method to calculate total cost
    private double getTotalCost() {

        double quantity = this.getQuantity().getValue().doubleValue();
        double unitPrice = this.getUnitPrice().getValue().doubleValue();

        double totalCost = quantity * unitPrice;
        return totalCost;
    }

}

