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
package net.project.billing.invoice;

public final class InvoiceDAO implements java.io.Serializable {

    /**
     * Encrypts the source using the invoice security key.
     * @param source the source to encrypt
     * @throws net.project.security.EncryptionException if there is a problem encrypting
     * @see net.project.security.EncryptionManager#encryptBlowfish
     */
    static String encrypt(String source) throws net.project.security.EncryptionException {
        return net.project.security.EncryptionManager.encryptBlowfish(
            source, net.project.security.crypto.SecretKeyType.INVOICE);
    }


    /**
     * Decrypts the source using the invoice security key.
     * @param source the encrypted source to decrypt
     * @throws net.project.security.EncryptionException if there is a problem decrypting
     * @see net.project.security.EncryptionManager#decryptBlowfish
     */
    static String decrypt(String source) throws net.project.security.EncryptionException {
        return net.project.security.EncryptionManager.decryptBlowfish(
            source, net.project.security.crypto.SecretKeyType.INVOICE);
    }


    /**
     * Returns the select part to load one or more ledger entries.
     * <li><code>PN_LEDGER</code> aliased to <code>PL</code>
     * <li>No <code>WHERE</code> clause
     * @return the select part of a query
     */
    public static String getQueryLoadLedgerEntry() {
        StringBuffer query = new StringBuffer();
        query.append("select pl.ledger_id, pl.bill-id, pl.responsible_party_id, pl.originating_payment_id, " +
		     " pl.due_since_datetime, pl.unit_price_value, pl.quantity_amount, pl.quantity_uom_id, " + 
		     " pl.category_id, pl.part_details_part_number, pl.part_details_part_description, " + 
		     " pl.group_type_id, pl.group_value, pl.group_description, pl.record_status, " +
		     " pl.invoice_id, pl.invoice_date, pl.invoice_status_id ") ;
        query.append("from pn_ledger pl ");
        return query.toString();
    }

    /**
     * Returns the select part to load one or more invoices.
     * <li><code>PN_INVOICE</code> aliased to <code>pi</code>
     * <li>No <code>WHERE</code> caluse
     * @return the select part of the query
     */
    public static String getQueryLoadInvoice() {
        StringBuffer query = new StringBuffer();
        query.append("select pi.invoice_id ");
        query.append("from pn_invoice pi ");
        return query.toString();
    }

    /**
     * Populates a invoice object from a result set.
     * @param result the result set containing data to populate
     * @param invoice the invoice to populate
     * @throws java.sql.SQLException if there is a problem reading columns from the result
     * @throws net.project.persistence.PersistenceException if there is a problem populating the responsible party
     */
    public static void populateInvoice(java.sql.ResultSet result, Invoice invoice)
            throws java.sql.SQLException, net.project.persistence.PersistenceException {

        invoice.setID(result.getString("invoice_id"));
    
    }

}
