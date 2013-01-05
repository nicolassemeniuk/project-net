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
package net.project.billing.bill;

import java.sql.SQLException;
import java.util.Date;

import net.project.base.money.Money;
import net.project.base.quantity.Quantity;
import net.project.billing.bill.category.Category;
import net.project.billing.bill.dueness.Dueness;
import net.project.billing.bill.group.BillGroup;
import net.project.billing.payment.PaymentInformation;
import net.project.database.DBBean;
import net.project.license.License;
import net.project.license.LicenseException;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;


/**
 * A Bill is a recurring item that has a quantity and unit price.
 * It is the subject of a billing report and used to generate an invoice.
 */
public class Bill
    implements IBillSource {


    /**
     * The database id of this bill.
     */
    private String id = null;

    /**
     * The quantity for this bill.
     */
    private Quantity quantity = null;

    /**
     * The unit price of each item for this bill.
     */
    private Money unitPrice = null;

    /**
     * The Category to which this bill belongs, used for determine which
     * selection of bills to include in a billing report.
     */
    private Category category = null;

    /**
     * Grouping information about this bill, used to group items in a billing
     * report.
     */
    private BillGroup billGroup = null;

    /**
     * The details of the part to which this bill pertains.
     */
    private PartDetails partDetails = null;

    /**
     * The Dueness information for this bill; that is, when it is due.
     */
    private Dueness dueness = null;

    /**
     * Represents this bill's status.
     */
    private BillStatusID statusID = null;

    /**
     * The payment information specified against the original item used
     * to create this bill.
     */
    private PaymentInformation paymentInformation = null;

    /**
     * The person to whom this bill pertains.
     * This is used so any bill can be traced back to a single person.
     */
    private Person originatingPerson = null;

    /**
     * The license to which this bill pertains.
     * This might need to go away if we want to detach bills from licenses.
     * It is included only to ensure in the first iteration that all info
     * is available; we might change our minds as to how bills a reported within
     * a couple of revisions.
     */
    private License originatingLicense = null;

    /**
     * The creation date for this bill.
     */
    private Date creationDate = new java.util.Date();

    /**
     * Creates an empty bill.
     */
    protected Bill() {

    }

    /**
     * Creates a new bill from the specified bill source.
     * @param billSource the bill source
     */
    protected Bill(IBillSource billSource)
        throws LicenseException, PersistenceException {
        initialize();
        populate(billSource);
    }

    /**
     * Initializes this Bill with default settings.
     * <code>
     * <li>statusID - {@link BillStatusID#UNRECORDED}
     *</code>
     */
    private void initialize() {
        setStatusID(BillStatusID.UNRECORDED);
    }

    /**
     * Populates this Bill from the bill source.
     * @param billSource the source of this bill
     */
    private void populate(IBillSource billSource)
        throws LicenseException, PersistenceException {
        setQuantity(billSource.getQuantity());
        setUnitPrice(billSource.getUnitPrice());
        setCategory(billSource.getCategory());
        setBillGroup(billSource.getBillGroup());
        setPartDetails(billSource.getPartDetails());
        setDueness(billSource.getDueness());
        setPaymentInformation(billSource.getPaymentInformation());
        setOriginatingPerson(billSource.getOriginatingPerson());
        setOriginatingLicense(billSource.getOriginatingLicense());
    }

    /**
     * Sets the id of this bill .
     * @param id the id of this bill
     * @see #getID
     */
    public void setID(String id) {
        this.id = id;
    }

    /**
     * Returns the id of this bill.
     * @return the id
     * @see #setID
     */
    public String getID() {
        return this.id;
    }

    /**
     * Sets the quantity of items that this bill is for.
     * @param quantity the quantity
     * @see #getQuantity
     */
    public void setQuantity(Quantity quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns the quantity of items that this bill is for.
     * @return the quantity
     * @see #setQuantity
     */
    public Quantity getQuantity() {
        return this.quantity;
    }

    /**
     * Sets the unit price for each item of this bill.
     * @param unitPrice the unit price
     * @see #getUnitPrice
     */
    public void setUnitPrice(Money unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * Returns the unit price for each item of this bill.
     * @return the unit price
     * @see #setUnitPrice
     */
    public Money getUnitPrice() {
        return this.unitPrice;
    }

    /**
     * Sets this bill's category
     * @param category the category
     * @see #getCategory
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Returns this bill's category
     * @return the category
     * @see #setCategory
     */
    public Category getCategory() {
        return this.category;
    }

    /**
     * Sets this bill's group.
     * @param billGroup the group
     * @see #getBillGroup
     */
    public void setBillGroup(BillGroup billGroup) {
        this.billGroup = billGroup;
    }

    /**
     * Returns this bill's group.
     * @return the group
     * @see #setBillGroup
     */
    public BillGroup getBillGroup() {
        return this.billGroup;
    }

    /**
     * Sets the part details.
     * @param partDetails the part details
     * @see #getPartDetails
     */
    public void setPartDetails(PartDetails partDetails) {
        this.partDetails = partDetails;
    }

    /**
     * Returns the part details.
     * @return the part details
     * @see #setPartDetails
     */
    public PartDetails getPartDetails() {
        return this.partDetails;
    }

    void setStatusID(BillStatusID statusID) {
        this.statusID = statusID;
    }

    BillStatusID getStatusID() {
        return this.statusID;
    }

    protected void setPaymentInformation(PaymentInformation payment) {
        this.paymentInformation = payment;
    }

    public PaymentInformation getPaymentInformation() {
        return this.paymentInformation;
    }

    void setOriginatingPerson(Person person) {
        this.originatingPerson = person;
    }

    public Person getOriginatingPerson() {
        return this.originatingPerson;
    }

    void setOriginatingLicense(License license) {
        this.originatingLicense = license;
    }

    public License getOriginatingLicense() {
        return this.originatingLicense;
    }

    void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    Date getCreationDate() {
        return this.creationDate;
    }

    void setDueness(Dueness dueness) {
        this.dueness = dueness;
    }

    public Dueness getDueness()
        throws LicenseException, PersistenceException {
        return this.dueness;
    }

    /**
     * Stores this bill.
     * @param db the DBBean in which to perform the transaction
     * @throws PersistenceException if there is a problem storing
     */
    protected void store(DBBean db)
        throws PersistenceException, LicenseException {
        try {
            insert(db);
        } catch (SQLException e) {
            throw new PersistenceException("Bill store operation failed: " + e, e);
        }
    }

    /**
     * Inserts this bill.
     * @param db the DBBean in which to perform the transaction.
     * @throws SQLException if there is a problem inserting
     */
    private void insert(DBBean db)
        throws SQLException, LicenseException, PersistenceException {

        StringBuffer insertQuery = new StringBuffer();
        insertQuery.append("insert into pn_bill ");
        insertQuery.append("(bill_id, unit_price_value, quantity_amount, quantity_uom_id, ");
        insertQuery.append("category_id, part_details_part_number, part_details_part_description, ");
        insertQuery.append("group_type_id, group_value, group_description, creation_datetime, ");
        insertQuery.append("due_datetime, ");
        insertQuery.append("bill_status_id, record_status, ");
        insertQuery.append("originating_license_id, originating_person_id, originating_payment_id) ");
        insertQuery.append("values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");

        // Generate new billID
        String billID = new net.project.database.ObjectManager().getNewObjectID();
        this.setID(billID);

        int index = 0;
        db.prepareStatement(insertQuery.toString());
        db.pstmt.setString(++index, billID);
        db.pstmt.setString(++index, getUnitPrice().getValue().toString());
        db.pstmt.setInt(++index, getQuantity().getValue().intValue());
        db.pstmt.setString(++index, getQuantity().getUnitOfMeasure().getID());
        db.pstmt.setString(++index, getCategory().getID().getID());
        db.pstmt.setString(++index, getPartDetails().getPartNumber());
        db.pstmt.setString(++index, getPartDetails().getPartDescription());
        db.pstmt.setString(++index, getBillGroup().getGroupTypeID().getID());
        db.pstmt.setString(++index, getBillGroup().getValue());
        db.pstmt.setString(++index, getBillGroup().getDescription());
        db.pstmt.setTimestamp(++index, new java.sql.Timestamp(getCreationDate().getTime()));
        db.pstmt.setTimestamp(++index, new java.sql.Timestamp(getDueness().getDueDate().getTime()));
        db.pstmt.setString(++index, getStatusID().getID());
        db.pstmt.setString(++index, "A");
        db.pstmt.setString(++index, getOriginatingLicense().getID());
        db.pstmt.setString(++index, getOriginatingPerson().getID());
        db.pstmt.setString(++index, getPaymentInformation().getID());
        db.executePrepared();
    }


}
