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

/**
 * Interface used in the application to determine what the response was to a
 * credit card transaction.  Each credit card processing class in project.net
 * which wishes to use any Project.Net credit card functionality must respond
 * with an implementation of this interface.
 *
 * @author Matthew Flower
 * @since Version 7.5
 */
public interface ICreditCardProcessingResults {
    /**
     * Get a {@link net.project.creditcard.CreditCardResultType} object which
     * gives the general category of a credit card response.  There are few of
     * these and they include things like "NETWORK_ERROR" and "SUCCESS".
     *
     * @return a <code>CreditCardResultType</code> object which indicates in
     * general terms if the transaction was successful, and if not why.
     */
    public CreditCardResultType getResultType();
    /**
     * Get any specific messages that the credit card processor returned.  It is
     * anticipated that anyone who implements this message will do what is
     * necessary to make these messages appropriate for a user.
     *
     * @return a <code>String</code> containing a user-centric message
     * explaining an error that might have occurred.
     */
    public String getMessage();
    /**
     * Indicates if the user is attempting to submit the same credit card
     * transaction twice.
     *
     * @return a <code>boolean</code> value indicating if the same transaction
     * is being submitted twice.
     */
    boolean isDuplicateTransaction();
    /**
     * Get the project.net transaction id generated for this transaction.
     *
     * @return a <code>String</code> containing the transaction id.
     */
    public String getTransactionID();
}
