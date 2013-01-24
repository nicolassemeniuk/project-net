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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.creditcard;

import net.project.base.PnetException;
import net.project.base.money.Money;
import net.project.security.User;

/**
 * Public interface implemented by classes which want to provide the ability
 * to charge money onto credit cards.
 *
 * @author Matthew Flower
 * @since Version 7.5
 */
public interface ICreditCardProcessor {
    /**
     * Charge money to a credit card.
     *
     * @param creditCard a <code>CreditCard</code> object to which we are going
     * to charge money.
     * @param amount a <code>Money</code> object which represents the amount
     * that we are going to charge to the credit card.
     * @param uniqueTransactionID a <code>String</code> containing a globally
     * unique id.  The processor code is going to use this string to ensure
     * that the same transaction isn't submitted twice.
     * @param lineItemDesc a <code>String</code> containing a description of
     * what is being purchased.
     * @param purchasingUser a <code>User</code> object who is doing the
     * purchasing.  This may or may not be the same as the information that
     * appears on the credit card.
     * @return a <code>ICreditCardProcessingResult</code> which indicates
     * whether the transaction was successful, and if not, what happened.
     * @throws PnetException if any error occurs during the credit card
     * processing.
     */
    ICreditCardProcessingResults makePurchase(CreditCard creditCard,
        Money amount, String uniqueTransactionID, String lineItemDesc,
        User purchasingUser) throws PnetException;
}
