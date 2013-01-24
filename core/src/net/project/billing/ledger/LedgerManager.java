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
package net.project.billing.ledger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import net.project.billing.bill.Bill;
import net.project.billing.bill.BillManager;
import net.project.billing.invoice.InvoiceStatus;
import net.project.database.DBBean;
import net.project.license.LicenseException;
import net.project.persistence.PersistenceException;

/**
 * Provides operation for creating ledger entries.
 * @author Vishwajeet
 */
public class LedgerManager {

    /**
     * Creates a new LedgerManager.
     */
    public LedgerManager() {
        // Do nothing
    }

    /**
     * Creates a ledger entry for the specified bill if it is overdue.
     * @param bill the bill for which to create a ledger entry
     */
    public void createLedgerEntry(net.project.billing.bill.Bill bill)
        throws net.project.persistence.PersistenceException, net.project.license.LicenseException {

        DBBean db = new DBBean();
        try {
            createLedgerEntry(db, bill);
        } finally {
            db.release();
        }
    }

    /**
     * Creates a ledger entry for the specified bill if it is overdue.
     * @param db the DBBean in which to perform the transaction
     * @param bill the bill for which to create a ledger entry
     */
    public void createLedgerEntry(net.project.database.DBBean db, net.project.billing.bill.Bill bill)
        throws net.project.persistence.PersistenceException, net.project.license.LicenseException {

        net.project.billing.bill.dueness.Dueness billDueness = bill.getDueness();
        java.util.Date dueDate = billDueness.getDueDate();

        // Create a ledger entry only if the bill is overdue.
        if ((dueDate.compareTo(new java.util.Date()) <= 0)) {
            LedgerEntry ledgerEntry = new LedgerEntry(bill, db);
            ledgerEntry.store(db);
            BillManager.flagBillRecorded(bill, db);
        }
    }

    /**
     * Gets all ledger entries which have not been invoiced for.
     */
    public static ArrayList getAllNonInvoicedLedgerEntries()
        throws PersistenceException, LicenseException {

        DBBean db = new DBBean();

        updateLedgerToDate();

        ArrayList entryList = new ArrayList();
        int index = 0;
        String query = "select ledger_id from pn_ledger where invoice_status_id = ?";

        try {

            db.prepareStatement(query);
            db.pstmt.setString(++index, InvoiceStatus.NOT_INVOICED.getID());
            db.executePrepared();

            while (db.result.next()) {
                LedgerEntry nextEntry = new LedgerEntry();
                nextEntry.setID(db.result.getString("ledger_id"));
                nextEntry.load();
                entryList.add(nextEntry);
            }

        } catch (SQLException sqle) {
            throw new PersistenceException("LedgerManager.java : Problem " +
                "creating Invoice. " + sqle.getMessage(), sqle);
        } catch (PersistenceException pe) {
            throw new PersistenceException("LedgerManager.java : Problem " +
                "creating Invoice. " + pe.getMessage(), pe);
        } finally {
            try {
                db.rollback();
            } catch (java.sql.SQLException sqle) {
                // Simply release
            }

            db.release();

        }

        return entryList;
    }

    /**
     * Flags the specified ledger entries as invoiced.
     * @param entryIDList a list of the ledger entries to flag as invoiced
     * @param db the DBBean in which to perform the transaction
     */
    public static void flagEntriesInvoiced(ArrayList entryIDList, String invoiceID,
        java.util.Date invoiceDate, DBBean db) throws PersistenceException {

        // Check if there are any entries to flag
        if (entryIDList.size() <= 0) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("update pn_ledger set invoice_status_id = ? ,");
        sb.append(" invoice_id = ? ,");
        sb.append(" invoice_date = ?");
        sb.append(" where ledger_id in ( ");
        boolean comma = false;

        int listSize = entryIDList.size();
        for (listSize = entryIDList.size(); listSize > 0; --listSize) {
            if (comma) {
                sb.append(", ");
            }
            sb.append("?");
            comma = true;

        }
        sb.append(")");
        int index = 0;
        try {
            db.prepareStatement(sb.toString());
            db.pstmt.setString(++index, InvoiceStatus.INVOICED.getID());
            db.pstmt.setString(++index, invoiceID);
            db.pstmt.setTimestamp(++index, new java.sql.Timestamp(invoiceDate.getTime()));

            String id = null;
            java.util.Iterator it = entryIDList.iterator();
            while (it.hasNext()) {
                id = (String)it.next();
                db.pstmt.setString(++index, id);
            }
            db.executePrepared();

        } catch (SQLException sqle) {
            throw new PersistenceException("LedgerManager.java : Problem " +
                "flagging ledger entries as invoiced ." + sqle.getMessage(), sqle);
        }

    }

    /**
     * Brings the ledger up-to-date, i.e. records all transactions that are overdue.
     * @throws PersistenceException if there is any problem updating the ledger.
     */
    public static void updateLedgerToDate()
        throws PersistenceException, LicenseException {

        ArrayList unrecordedBills = BillManager.getAllUnrecordedBills();

        Iterator iter = unrecordedBills.iterator();
        while (iter.hasNext()) {
            Bill nextBill = (Bill)iter.next();

            LedgerManager ledgerManager = new LedgerManager();
            ledgerManager.createLedgerEntry(nextBill);

        }


    }

}
