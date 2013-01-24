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

import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;

/**
 * Provides a collection of all the invoices in the system and facilitates
 * filtering and searching based on different criteria.
 */
public class InvoiceCollection extends ArrayList implements IXMLPersistence {

    public boolean isLoaded = false;

    private String whereClause = "";
    private String sortField = "";
    private String sortOrder = "";


    /**
     * Loads a list of all invoices in the system.
     * @throws PersistenceException if there is a problem loading any invoice
     */
    public void load(boolean allInvoices)
        throws PersistenceException, net.project.license.LicenseException {

        this.clear();

        DBBean db = new DBBean();
        String query = null;

        if (allInvoices) {
            query = "Select invoice_id from pn_invoice ";
        } else {
            query = "Select distinct invoice_id from pn_ledger " + this.whereClause;
        }

        try {

            db.prepareStatement(query);
            db.executePrepared();

            while (db.result.next()) {
                if (db.result.getString("invoice_id") != null) {
                    Invoice nextInvoice = new Invoice();
                    nextInvoice.setID(db.result.getString("invoice_id"));
                    nextInvoice.load();
                    this.add(nextInvoice);
                }
            }
            this.isLoaded = true;

        } catch (SQLException sqle) {
            throw new PersistenceException("InvoiceCollection.java :Invoice load operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

    }

    /**
     * Loads a list of all invoices which has a ledger entry a party is responsible for.
     * @throws PersistenceException if there is a problem loading any invoice
     */
    public void loadResponsibleForPerson(net.project.resource.Person person)
        throws PersistenceException, net.project.license.LicenseException {

        this.clear();

        String personID = person.getID();
        int index = 0;
        DBBean db = new DBBean();

        String query = "Select invoice_id from pn_invoice where ledger_id in (select ledger_id from pn_ledger " + this.whereClause;
        if (this.whereClause.equals("")) {
            query = query + " where responsible_party_id = ? )";
        } else {
            query = query + " and responsible_party_id = ? )";
        }


        try {

            db.prepareStatement(query);
            db.pstmt.setString(++index, personID);
            db.executePrepared();

            while (db.result.next()) {
                Invoice nextInvoice = new Invoice();
                nextInvoice.setID(db.result.getString("invoice_id"));
                nextInvoice.load();
                this.add(nextInvoice);
            }
            this.isLoaded = true;

        } catch (SQLException sqle) {
            throw new PersistenceException("InvoiceCollection.java : loadResponsibleForPerson operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

    }


    public String getXML() {
        return ("<?xml version=\"1.0\" ?>\n" + getXMLBody());
    }

    public String getXMLBody() {

        StringBuffer sb = new StringBuffer();
        sb.append("<InvoiceList>");
        sb.append("<JSPRootURL>" + SessionManager.getJSPRootURL() + "</JSPRootURL>");
        sb.append(this.getSortOrderXML());
        Iterator iter = this.iterator();

        while (iter.hasNext()) {
            Invoice nextInvoice = (Invoice)iter.next();
            sb.append(nextInvoice.getXMLBody());
        }
        sb.append("</InvoiceList>");
        //System.out.println(sb.toString());
        return sb.toString();

    }

    /**
     * Sets the payment information and the license key filters on this collection to limit loading invoices.
     * @param payInfo the payment information string that will be used to load an invoice
     * @param folName first or last name string that will be used to load an invoice
     * @param partNumber the license key string that will be used to load an invoice
     */
    public void setSearchCriteria(String payInfo, String folName, String partNumber) {

        StringBuffer sb = new StringBuffer();
        sb.append("WHERE ");
        boolean and = false;

        if (payInfo.equals("") && folName.equals("") && partNumber.equals("")) {

            this.whereClause = "";
        } else {
            if (!payInfo.equals("")) {
                sb.append(" ( ");
                sb.append(" ledger_id in (select ledger_id from pn_ledger " +
                    " where upper (group_value) like UPPER('%" + payInfo + "%')");
                sb.append(" or upper (group_description) like UPPER('%" + payInfo + "%'))");
                sb.append(" ) ");
                and = true;
            }

            if (!folName.equals("")) {
                if (and) {
                    sb.append(" AND ");
                }
                sb.append(" ( ");
                sb.append(" ledger_id in (select ledger_id from pn_ledger " +
                    " where responsible_party_id in (select person_id from pn_person " +
                    " where upper (first_name) like UPPER('%" + folName + "%')" +
                    " or upper (last_name) like UPPER('%" + folName + "%')))");
                sb.append(" ) ");
                and = true;
            }

            if (!partNumber.equals("")) {
                if (and) {
                    sb.append(" AND ");
                }
                sb.append(" ( ");
                sb.append(" ledger_id in (select ledger_id from pn_ledger " +
                    " where upper (part_details_part_number) like UPPER('%" + partNumber + "%'))");
                sb.append(" ) ");

            }

            this.whereClause = sb.toString();

        }
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortField() {

        return this.sortField;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getSortOrder() {

        return this.sortOrder;
    }

    private String getSortOrderXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<SortField>" + this.getSortField() + "</SortField>\n");
        sb.append("<SortOrder>" + this.getSortOrder() + "</SortOrder>");
        return sb.toString();
    }
    /*
    private double getTotalAmountNotInvoiced() {

        double quantity = 0;
        double unitPrice = 0;
        double cost = 0;
        double totalAmountNotInvoiced = 0;

        Iterator iter = this.iterator();
        while(iter.hasNext()) {
        LedgerEntry nextEntry = (LedgerEntry) iter.next();
        if (nextEntry.getInvoiceStatus().getID().equals(InvoiceStatus.NOT_INVOICED.getID())) {
                 quantity = nextEntry.getQuantity().getValue().doubleValue();
         unitPrice = nextEntry.getUnitPrice().getValue().doubleValue();
         cost = quantity * unitPrice;
         totalAmountNotInvoiced = totalAmountNotInvoiced + cost;

        }
        }

        return totalAmountNotInvoiced;

    }	    */
}

