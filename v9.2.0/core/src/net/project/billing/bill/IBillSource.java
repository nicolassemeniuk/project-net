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

import net.project.license.LicenseException;
import net.project.persistence.PersistenceException;

/**
 * Used by the billing system to create bills.
 * Any class implementing <code>IBillSource</code> may be created as a bill.
 * @author tim
 */
public interface IBillSource {

    /**
     * Returns the quantity of items associated with this bill.
     * @return the quantity
     */
    public net.project.base.quantity.Quantity getQuantity();
    
    /**
     * Returns the unit price of each item associated with this bill.
     * @return the unit price
     */
    public net.project.base.money.Money getUnitPrice();
    
    /**
     * Returns the part details of the part associated with this bill.
     * @return the part details
     */
    public net.project.billing.bill.PartDetails getPartDetails();
    
    /**
     * Returns the category to which this bill should belong.
     * @return the category
     */
    public net.project.billing.bill.category.Category getCategory();
    
    /**
     * Returns the frequency of recurrence for this bill.
     * @return the frequency
     */
  //  public net.project.billing.bill.recurrence.FrequencyID getFrequencyID();
    /**
     * Returns the dueness of  this bill.
     * @return the dueness
     */
    public net.project.billing.bill.dueness.Dueness getDueness() throws PersistenceException, LicenseException;
    
    /**
     * Returns the group to which this bill belongs.
     * @return the group
     */
    public net.project.billing.bill.group.BillGroup getBillGroup();

    /**
     * Returns the payment information associated with this bill.
     * This is used for deciding who pays for the bill
     * @return the originating payment information
     */
    public net.project.billing.payment.PaymentInformation getPaymentInformation();

    /**
     * Returns the responsible person (or person who originated the bill).
     * This is used in the case that some dispute arises over for whom the
     * bill was created.
     * @return the originating person
     */
    public net.project.resource.Person getOriginatingPerson();

    /**
     * Returns the originating license that was used to create this bill.
     * This method might go away if bills become more general than simply
     * applying to licenses.  It is present as a safety measure to ensure
     * we can always figure out what causes this bill.
     * Note that all other objects (which may be derived from the license) are
     * still used for reporting and invoicing;  this is to ensure that a bill
     * is stamped with a "snapshot" of the license information at creation time;
     * it is immunte from any future changes in the license (unlikely, since
     * license are currently not modifiable.
     * For example, frequency, part details, unit price, category etc. are
     * used for reporting and invoicing.
     * @return the originating license
     */
    public net.project.license.License getOriginatingLicense();

}