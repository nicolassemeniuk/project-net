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
package net.project.util;

import net.project.base.IUniqueTransaction;

/**
 * An object which serves to uniquely identify a process that occurs in Project.Net.
 * For example, this object could be used to uniquely identify a registration.
 *
 * The primary use of this object is to prevent a credit card transaction from
 * being submitted twice.  We submit the GUID from this object to VeriSign and
 * they won't charge the account twice if the same guid comes through.  (This is
 * quite handy.)
 */
public class UniqueTransaction implements IUniqueTransaction {
    private RandomGUID guid;

    /**
     * Constructs a new transaction.  We use the client IP address to introduce
     * a tad more "randomness" into the guid.
     *
     * @param clientIPAddress a <code>String</code> which is one of a few factors
     * used to seed the randomness of the transaction id.
     */
    public UniqueTransaction(String clientIPAddress) {
        guid = new RandomGUID(true, clientIPAddress);
    }

    /**
     * Get a 32 character String which unique identifies this transaction across
     * all transactions on all instances of the Project.Net software.  This is
     * done using a GUID.
     *
     * This transaction ID is primarily used to prevent duplication transactions
     * being applied to a user's credit card.
     *
     * @return a <code>RandomGUID</code> object which contains a unique 32
     * character identifier.
     */
    public RandomGUID getUniqueTransaction() {
        return guid;
    }


}
