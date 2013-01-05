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

import net.project.base.money.Money;
import net.project.base.quantity.Quantity;
import net.project.billing.bill.PartDetails;
import net.project.billing.bill.category.Category;
import net.project.billing.bill.dueness.Dueness;
import net.project.billing.bill.group.BillGroup;
import net.project.billing.payment.PaymentInformation;
import net.project.license.License;
import net.project.resource.Person;

/**
 * Provides the source for creating a bill from a license.
 * This acts as a bridge from licensing to billing.
 *
 * @author tim
 * @since Gecko Update 2
 */
public class LicenseBill implements net.project.billing.bill.IBillSource  {
    
    private BillGroup billGroup = null;
    private Money unitPrice = null;
    private Quantity quantity = null;
    private Category category = null;
    private Dueness dueness = null;
    private PartDetails partDetails = null;
    private PaymentInformation paymentInformation = null;
    private Person originatingPerson = null;
    private License originatingLicense = null;
    
    /**
     * Creates an empty LicenseBill.
     */
    public LicenseBill() {
        // Do nothing
    }
    
    /**
     * Sets the bill group for this bill source.
     * @param billGroup the billing group
     * @see #getBillGroup
     */
    public void setBillGroup(BillGroup billGroup) {
        this.billGroup = billGroup;
    }
    
    /**
     * Sets the unit price for this bill source.
     * @param unitPrice the unit price for this bill source
     * @see #getUnitPrice
     */
    public void setUnitPrice(Money unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    /**
     * Sets the quantity of items for this bill source.
     * Combined with the <code>unitPrice</code> this affects the bill's total
     * @param quantity the quantity
     * @see #getQuantity
     */
    public void setQuantity(Quantity quantity) {
        this.quantity = quantity;
    }
    
    /**
     * Sets the category for this bill source.
     * This affects when a bill might be included in a report.
     * @param category the category
     * @see #getCategory
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Sets the dueness for this bill source.
     * This affects when a bill might be included in a report.
     * @param dueness the dueness
     * @see #getDueness
     */
    public void setDueness(Dueness dueness) {
        this.dueness = dueness;
    }
    
    /**
     * Sets the part details for this bill source.
     * @param partDetails the part details
     * @see #getPartDetails
     */
    public void setPartDetails(PartDetails partDetails) {
        this.partDetails = partDetails;
    }
    
    /**
     * Sets the payment information for this bill source.
     * @param payment the payment information
     * @see #getPaymentInformation
     */
    public void setPaymentInformation(PaymentInformation payment) {
        this.paymentInformation = payment;
    }

    /**
     * Sets the person who originated this bill source.
     * @param person the originating person
     * @see #getOriginatingPerson
     */
    public void setOriginatingPerson(Person person) {
        this.originatingPerson = person;
    }

    /**
     * Sets the license that originated this bill source.
     * This may disappear if license belong less attached to bills
     * @param license the license
     */
    public void setOriginatingLicense(License license) {
        this.originatingLicense = license;
    }

    //
    // Satisfy IBillSource
    //
    
    public BillGroup getBillGroup() {
        return this.billGroup;
    }
    
    public Money getUnitPrice() {
        return this.unitPrice;
    }
    
    public Category getCategory() {
        return this.category;
    }
    
    public Quantity getQuantity() {
        return this.quantity;
    }
    
    public Dueness getDueness() {
        return this.dueness;
    }
    
    public PartDetails getPartDetails() {
        return this.partDetails;
    }
    
    public net.project.billing.payment.PaymentInformation getPaymentInformation() {
        return this.paymentInformation;
    }
    
    public net.project.resource.Person getOriginatingPerson() {
        return this.originatingPerson;
    }

    public net.project.license.License getOriginatingLicense() {
        return this.originatingLicense;
    }

}