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
package net.project.base;

import net.project.util.RandomGUID;

/**
 * This interface serves to mark those objects that are part of a process that
 * can be uniquely identified by a process.  This interface was written for the
 * purpose of uniquely identifying all registrations that have ever occurred on
 * our system so that if we charge a credit card, we can be relatively certain
 * we haven't already charged for this transaction.
 *
 * Of course, saying that the transaction id is unique is probably a bit too
 * boastful of a claim.  We have made every attempt however, to make it as
 * absolutely unique as possible.  This is typically implemented using a GUID see
 * {@link net.project.util.RandomGUID} for more details.
 *
 * @author Matthew Flower
 * @since Version 7.5
 */
public interface IUniqueTransaction {
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
    public RandomGUID getUniqueTransaction();
}
