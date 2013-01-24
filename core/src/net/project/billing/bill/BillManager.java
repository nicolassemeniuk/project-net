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
package net.project.billing.bill;

import java.sql.SQLException;
import java.util.ArrayList;

import net.project.base.money.Money;
import net.project.base.quantity.Quantity;
import net.project.base.quantity.UnitOfMeasureCollection;
import net.project.billing.bill.category.Category;
import net.project.billing.bill.category.CategoryID;
import net.project.billing.bill.dueness.Dueness;
import net.project.billing.bill.group.BillGroup;
import net.project.billing.bill.group.GroupType;
import net.project.billing.bill.group.GroupTypeID;
import net.project.billing.ledger.LedgerManager;
import net.project.billing.payment.PaymentInformation;
import net.project.database.DBBean;
import net.project.license.License;
import net.project.license.LicenseException;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;


/**
 * Provides operation for creating bills.
 * @author tim
 */
public class BillManager {

    /**
     * Creates a new BillManager.
     */
    public BillManager() {
        // Do nothing
    }

    /**
     * Creates all the bills in the specified bill source collection.
     * @param db the DBBean in which to perform the transaction
     * @param billSourceCollection the collection of bills, where each element
     * is of type <code>IBillSource</code>
     */
    public void createBills(net.project.database.DBBean db, java.util.Collection billSourceCollection)
        throws net.project.persistence.PersistenceException, net.project.license.LicenseException {

        Bill nextBill = null;
        IBillSource nextBillSource = null;
        LedgerManager ledgerManager = new LedgerManager();

        // Iterate over each bill source, creating a bill from it
        for (java.util.Iterator it = billSourceCollection.iterator(); it.hasNext();) {
            nextBillSource = (IBillSource)it.next();

            nextBill = new Bill(nextBillSource);
            nextBill.store(db);
            ledgerManager.createLedgerEntry(db, nextBill);

        }

    }

    /**
     * Gets all the bills that have not been recorded in the ledger yet.
     * @throws PersistenceException if there is problem gettting the bills
     */
    public static ArrayList getAllUnrecordedBills()
        throws PersistenceException {

        DBBean db = new DBBean();
        ArrayList unrecordedBills = new ArrayList();
        int index = 0;

        String query = "Select * from pn_bill where bill_status_id = ? ";

        try {

            db.prepareStatement(query);
            db.pstmt.setString(++index, BillStatusID.UNRECORDED.getID());
            db.executePrepared();

            while (db.result.next()) {

                Bill bill = new Bill();

                bill.setID(db.result.getString("bill_id"));

                BillGroup billGroup = new BillGroup();
                billGroup.setDescription(db.result.getString("group_description"));
                billGroup.setValue(db.result.getString("group_value"));
                billGroup.setGroupTypeID(GroupTypeID.forID(db.result.getString("group_type_id")));
                billGroup.setGroupType(new GroupType(GroupTypeID.forID(db.result.getString("group_type_id"))));
                bill.setBillGroup(billGroup);


                CategoryID categoryID = new CategoryID(db.result.getString("category_id"));
                Category category = new Category();
                category.setID(categoryID);
                bill.setCategory(category);

                Dueness dueness = new Dueness();
                dueness.setDueDate(db.result.getTimestamp("due_datetime"));
                bill.setDueness(dueness);

                bill.setCreationDate(db.result.getTimestamp("creation_datetime"));

                PartDetails partDetails = new PartDetails();
                partDetails.setPartDescription(db.result.getString("part_details_part_description"));
                partDetails.setPartNumber(db.result.getString("part_details_part_number"));
                bill.setPartDetails(partDetails);

                PaymentInformation paymentInformation = new PaymentInformation();
                paymentInformation.setID(db.result.getString("originating_payment_id"));
                paymentInformation.load();
                bill.setPaymentInformation(paymentInformation);

                Quantity quantity = new Quantity();
                quantity.setUnitOfMeasure(UnitOfMeasureCollection.getAll().getForID(db.result.getString("quantity_uom_id")));
                quantity.setValue(new Integer(db.result.getString("quantity_amount")));
                bill.setQuantity(quantity);

                bill.setUnitPrice(new Money(db.result.getString("unit_price_value")));

                License license = new License();
                license.setID(db.result.getString("originating_license_id"));
                bill.setOriginatingLicense(license);

                Person person = new Person(db.result.getString("originating_person_id"));
                person.load();
                bill.setOriginatingPerson(person);

                unrecordedBills.add(bill);

            }

            return unrecordedBills;

        } catch (SQLException sqle) {
            throw new PersistenceException("BillManager.java : Problem loading bill." + sqle.getMessage(), sqle);
        } finally {
            db.release();
        }


    }

    public static void flagBillRecorded(Bill bill, DBBean db)
        throws PersistenceException, LicenseException {

        String query = "update pn_bill set bill_status_id = ? where bill_id = ?";
        int index = 0;

        try {
            db.prepareStatement(query);
            db.pstmt.setString(++index, BillStatusID.RECORDED.getID());
            db.pstmt.setString(++index, bill.getID());
            db.executePrepared();

            // Must create a new bill for a recurring bill
            if (bill.getCategory().getID().equals(CategoryID.LICENSE_MAINTENANCE_TYPE_B)) {

                Bill newBill = new Bill(bill);
                newBill.setCreationDate(new java.util.Date());
                Dueness dueness = new Dueness(bill.getCategory());
                dueness.setCreationDate(newBill.getCreationDate());
                newBill.setDueness(dueness);

                newBill.store(db);
            }
        } catch (SQLException sqle) {
            throw new PersistenceException("BillManager.java : Problem flagging bill as recorded." + sqle.getMessage(), sqle);
        }
    }

}
