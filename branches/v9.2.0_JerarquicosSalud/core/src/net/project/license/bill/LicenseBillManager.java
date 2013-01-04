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
package net.project.license.bill;

import java.util.ArrayList;
import java.util.List;

import net.project.billing.bill.PartDetails;
import net.project.billing.bill.category.Category;
import net.project.billing.bill.category.CategoryID;
import net.project.billing.bill.dueness.Dueness;
import net.project.billing.bill.group.BillGroup;
import net.project.billing.bill.group.GroupTypeID;
import net.project.billing.payment.PaymentModelTypeID;
import net.project.billing.payment.PaymentModelTypes;
import net.project.license.License;
import net.project.license.cost.LicenseCharge;
import net.project.license.cost.LicenseCost;
import net.project.license.cost.LicenseCostTypeID;
import net.project.resource.Person;


/**
 * Provides operations for creating and managing bills for licenses.
 * Based on UseCaseController pattern, it bridges the gap between licenses
 * and bills.
 * @author tim
 */
public class LicenseBillManager {

    /** Cache of payment model types. */
    private PaymentModelTypes paymentModelTypes = null;

    /**
     * Creates a new LicenseBillManager.
     */
    public LicenseBillManager() {
        // Do Nothing
    }

    /**
     * Creates the bills for a license.
     * @param db the DBBean in which to perform the transaction
     * @param person the person for whom the license is created for
     * @param license the license for which to create the bills
     */
    public void createBills(net.project.database.DBBean db, Person person, License license)
        throws net.project.persistence.PersistenceException, net.project.license.LicenseException {

        List licenseBillList = new ArrayList();

        // Iterate over each cost for the license
        // Create a LicenseBill based on the license and each cost
        // The Number of LicenseBills creates is equal to the number of
        // costs associated with this license
        LicenseCost nextCost = null;
        for (java.util.Iterator it = license.getCostCollection().iterator(); it.hasNext();) {
            nextCost = (LicenseCost)it.next();

            licenseBillList.add(createLicenseBill(person, license, nextCost));
        }

        // Now create the bills for all the license bills
        new net.project.billing.bill.BillManager().createBills(db, licenseBillList);
    }


    /**
     * Creates a LicenseBill for the specified license and license cost.
     * The LicenseBill is used to create the actual Bill.
     * @param person the person to whom the license belongs
     * @param license the license for which to create the bill
     * @param cost the LicenseCost from the license for which to create the bill
     */
    private LicenseBill createLicenseBill(Person person, License license, LicenseCost cost) {
        LicenseBill licenseBill = new LicenseBill();

        // Set all the properties of the license bill

        // The part details are constructed from the person and the license
        licenseBill.setPartDetails(getPartDetails(person, license));

        // The bill group is based on the payment information for the license
        licenseBill.setBillGroup(getBillGroup(license));

        // The bill quantity and unit price are based on the actual charge
        // of the current cost within the context of the current license
        LicenseCharge charge = cost.calculateLicenseCharge(license);
        licenseBill.setQuantity(charge.getQuantity());
        licenseBill.setUnitPrice(charge.getUnitPrice());

        // The billing category is derived from the license cost
        licenseBill.setCategory(getCategory(cost));

        // The dueness is derived from the license master properties and the license category
        licenseBill.setDueness(getDueness(getCategory(cost)));

        // The payment information associated with this license
        licenseBill.setPaymentInformation(license.getPaymentInformation());

        // The person originating the bill
        licenseBill.setOriginatingPerson(person);

        // The license attached to the bill
        // Yes, this completely breaks separation of bills and licenses
        // It is being included to ensure that as this licensing stuff is
        // revised, we have captured all necessary info since day one in
        // the database
        licenseBill.setOriginatingLicense(license);

        return licenseBill;
    }

    /**
     * Gets the PartDetails for the specified license.
     * The part number is the license value and the description is a
     * description of the license and its usage.
     * @param license the license
     * @return the PartDetails from the license
     */
    private PartDetails getPartDetails(Person person, License license) {

        // Construct the part description based on the person's name
        StringBuffer description = new StringBuffer();
        description.append("License for " + person.getDisplayName());

        // Construct the part details
        // Use the license key display string as the part number for the license
        // Use the constructed description
        PartDetails partDetails = new PartDetails();
        partDetails.setPartNumber(license.getKey().toDisplayString());
        partDetails.setPartDescription(description.toString());

        return partDetails;
    }

    /**
     * Returns the BillGroup that this license is based on.
     * The BillGroup for a license is based on its payment information; this
     * allows bills to be grouped by payment information (such as model and value).
     * @param license the license
     * @return the BillGroup to which this license's bills belong
     */
    private BillGroup getBillGroup(License license) {
        BillGroup billGroup = new BillGroup();

        // Get the payment information from the license
        // Used for building the bill group
        net.project.billing.payment.PaymentInformation payment = license.getPaymentInformation();

        // Convert the payment information model type to a group type
        // This means that bills for licenses are grouped by payment model
        billGroup.setGroupTypeID(convertToBillGroupTypeID(payment.getPaymentModel().getPaymentModelTypeID()));

        // Set grouping value is based on a single value from the payment model
        // For example, it charge code or credit card number
        billGroup.setValue(payment.getPaymentModel().getIdentifyingValue());

        // Build the description of the bill group
        // Constructed from the payment model's type description (e.g. Charge Code)
        // This is purely for display purposes
        StringBuffer description = new StringBuffer();
        description.append(getPaymentModelTypes().getPaymentModelType(payment.getPaymentModel().getPaymentModelTypeID()).getDescription());
        description.append(" : ").append(billGroup.getValue());
        billGroup.setDescription(description.toString());

        return billGroup;
    }

    /**
     * Converts a payment model type id to a bill group type id.
     * This allows licenses to be grouped by their payment model types during
     * billing.
     * @param paymentModelTypeID the payment model type id to convert to
     * a bill group type id
     * @return the bill group type id; {@link GroupTypeID#UNSPECIFIED} is returned
     * if a PaymentModelTypeID is not explicitly handled here
     */
    private GroupTypeID convertToBillGroupTypeID(PaymentModelTypeID paymentModelTypeID) {

        // 02/06/2002 - Tim
        // Here I completely break the ability to add new payment model type ids
        // by adding them to a database table and creating a class
        // The correct thing to do is to load this conversion from a table
        // (PN_PAYMENT_MODEL_TYPE_TO_BILL_GROUP_TYPE)
        // That way it is easy to define how a new payment model should be grouped
        // on a license billing report

        GroupTypeID groupTypeID = null;
        if (paymentModelTypeID.equals(PaymentModelTypeID.CHARGE_CODE)) {
            groupTypeID = GroupTypeID.CHARGE_CODE;

        } else if (paymentModelTypeID.equals(PaymentModelTypeID.CREDIT_CARD)) {
            groupTypeID = GroupTypeID.CREDIT_CARD;

        } else if (paymentModelTypeID.equals(PaymentModelTypeID.TRIAL)) {
            groupTypeID = GroupTypeID.TRIAL;

        } else {
            // Unhandled payment model type id
            // Default to a catch-all billing group
            groupTypeID = GroupTypeID.UNSPECIFIED;
        }

        return groupTypeID;
    }

    /**
     * Returns the billing Category that this license cost belongs to.
     * This is used to categorize bills such that billing reports may be
     * based on categories.
     * A bill's category should be determined based on the LicenseCost and the LicenseType.Currently we are
     * assuming that there is only one type of license and that is Enterprise Type. In future we will need to
     * pass licenseType to be able to determine the bill category.
     * @param cost the license cost
     * @return the billing Category to which this cost belongs
     */
    private net.project.billing.bill.category.Category getCategory(LicenseCost cost) {

        CategoryID categoryID = null;

        // Convert license cost to category
        if (cost.getLicenseCostTypeID().equals(LicenseCostTypeID.BASE)) {
            //For an Enterprise type License Base cost belongs to LICENSE_USAGE_TYPE_A bill category
            categoryID = CategoryID.LICENSE_USAGE_TYPE_A;

        } else if (cost.getLicenseCostTypeID().equals(LicenseCostTypeID.MAINTENANCE)) {
            //For an Enterprise type license Maintenance cost belongs to the LICENSE_MAINTENANCE_TYPE_B bill category
            categoryID = CategoryID.LICENSE_MAINTENANCE_TYPE_B;

        } else {
            // With unknown cost, category is license usage
            categoryID = CategoryID.LICENSE_USAGE_TYPE_A;
        }

        // Return the appropriate category for the derived category id
        return net.project.billing.bill.category.CategoryCollection.getAll().getCategory(categoryID);
    }

    /**
     * Returns the billing frequency for this license cost.
     * This is based on the type of license cost.
     * @param category the license cost
     * @return the frequency id for this license cost
     */
    private Dueness getDueness(Category category) {

        Dueness dueness = new Dueness(category);

        return dueness;
    }

    /**
     * Gets the payment model types, loading if necessary.
     * @return the payment model types
     */
    private PaymentModelTypes getPaymentModelTypes() {
        if (this.paymentModelTypes == null) {
            this.paymentModelTypes = PaymentModelTypes.getAll();
        }
        return this.paymentModelTypes;
    }

}