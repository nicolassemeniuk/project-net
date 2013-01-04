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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import net.project.billing.ledger.LedgerEntry;
import net.project.billing.ledger.LedgerManager;
import net.project.database.DBBean;
import net.project.license.LicenseException;
import net.project.notification.NotificationException;
import net.project.persistence.PersistenceException;

/**
 * Provides operation for creating invoices.
 *
 * @author Vishwajeet
 */
public class InvoiceManager {

    /**
     * Creates a new InvoiceManager.
     */
    public InvoiceManager() {
        // Do nothing
    }

    /**
     * Stores an invoice .
     */
    public void storeInvoice(Invoice invoice) throws PersistenceException, NotificationException {
        DBBean db = new DBBean();
        try {
            invoice.store(db);
            //Must do this operation after doing a store, because we need
            //invoice_id which is generated in store.
            LedgerManager.flagEntriesInvoiced(invoice.getBody().getLedgerEntryIDList(), invoice.getID(), invoice.getCreationDate(), db);
            db.commit();
            // Must send this invoice to Project.net, if the notification fails
            //the invoice must not be generated ?
            //Currently the invoice would be generated even if Project Net is not notified.
            InvoiceNotification invoiceNotification = new InvoiceNotification();
            invoiceNotification.notifyProjectNet(invoice);
        } catch (SQLException sqle) {
            throw new PersistenceException("Invoice.java : Problem storing Invoice. " + sqle.getMessage(), sqle);
        } finally {
            try {
                db.rollback();
            } catch (java.sql.SQLException sqle) {
                System.out.println("InvoiceManager.storeInvoice(): Exception thrown when trying to rollback" + sqle.getMessage());
                // Simply release
            }

            db.release();
        }
    }

    /**
     * Creates an invoice to preview for the transactions in a specified time period.
     * @return invoice, the invoice newly created.
     */
    public Invoice createInvoicePreview()
        throws PersistenceException, LicenseException {


        InvoiceBody invoiceBody = createInvoiceBody();
        Invoice invoice = new Invoice(invoiceBody);
        return invoice;

    }

    /**
     * Creates an invoice body for the transactions in a specified time period.
     */
    public InvoiceBody createInvoiceBody() throws PersistenceException, LicenseException {

        ArrayList ledgerEntryList = LedgerManager.getAllNonInvoicedLedgerEntries();
        InvoiceBody invoiceBody = new InvoiceBody();

        Iterator iter = ledgerEntryList.iterator();
        while (iter.hasNext()) {
            LedgerEntry nextEntry = (LedgerEntry)iter.next();
            invoiceBody.addLedgerEntry(nextEntry);

        }

        return invoiceBody;

    }

    /**
     * Gets the invoice ID of the last invoice.
     */
    public static String getLastInvoiceID() throws PersistenceException {

        String id = null;
        String query = "select invoice_id from pn_invoice " +
            " where creation_datetime = (select max(creation_datetime) from pn_invoice)";
        DBBean db = new DBBean();
        try {
            db.setQuery(query);
            db.executeQuery();
            while (db.result.next()) {

                id = db.result.getString("invoice_id");

            }
            //System.out.println("InvoiceManager.java : the invoice id is : " + id);
            return id;

        } catch (SQLException sqle) {
            throw new PersistenceException("InvoiceManager.java : Problem creating Invoice. " + sqle.getMessage(), sqle);
        } finally {

            db.release();

        }

    }

    /**
     * Gets the html option list of all invoices.
     */
    public static String getInvoiceOptionList() throws PersistenceException {

        StringBuffer sb = new StringBuffer();
        sb.append("<select name=invoiceID>");
        String invoiceID = null;
        String creationDate = null;
        String query = "select invoice_id, creation_datetime from pn_invoice order by creation_datetime desc";

        DBBean db = new DBBean();
        try {
            db.setQuery(query);
            db.executeQuery();
            while (db.result.next()) {
                invoiceID = db.result.getString("invoice_id");
                creationDate = db.result.getString("creation_datetime");
                sb.append("<option value=\"" + invoiceID + "\">" + creationDate + "</option>");
            }
            sb.append("</select>");
            return sb.toString();

        } catch (SQLException sqle) {
            throw new PersistenceException("InvoiceManager.java : Problem getting Invoice Option List. " + sqle.getMessage(), sqle);
        } finally {

            db.release();

        }

    }


}
