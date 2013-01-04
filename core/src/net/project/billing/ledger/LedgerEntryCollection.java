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

import net.project.billing.bill.category.CategoryID;
import net.project.billing.bill.group.GroupTypeID;
import net.project.billing.invoice.InvoiceStatus;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;

/**
 * Provides facilities for creating and checking licenses.
 */
public class LedgerEntryCollection
    extends ArrayList
    implements net.project.persistence.IXMLPersistence {

    public boolean isLoaded = false;

    private String whereClause = "";
    private String sortField = "";
    private String sortOrder = "";


    /**
     * Loads a list of all entries in the ledger.
     * @throws PersistenceException if there is a problem loading any entry
     */
    public void load()
        throws PersistenceException, net.project.license.LicenseException {

        this.clear();

        DBBean db = new DBBean();

        String query = "Select ledger_id from pn_ledger " + this.whereClause;

        try {

            db.prepareStatement(query);
            db.executePrepared();

            while (db.result.next()) {
                LedgerEntry ledgerEntry = new LedgerEntry();
                ledgerEntry.setID(db.result.getString("ledger_id"));
                ledgerEntry.load();
                this.add(ledgerEntry);
            }
            this.isLoaded = true;

        } catch (SQLException sqle) {
            throw new PersistenceException("LedgerEntry load operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

    }

    /**
     * Loads a list of all ledger entries a party is responsible for.
     * @throws PersistenceException if there is a problem loading any ledger entry
     */
    public void loadResponsibleForPerson(net.project.resource.Person person)
        throws PersistenceException, net.project.license.LicenseException {

        this.clear();

        String personID = person.getID();
        int index = 0;
        DBBean db = new DBBean();

        String query = "Select ledger_id from pn_ledger " + this.whereClause;
        if (this.whereClause.equals("")) {
            query = query + " where responsible_party_id = ?";
        } else {
            query = query + " and responsible_party_id = ?";
        }


        try {

            db.prepareStatement(query);
            db.pstmt.setString(++index, personID);
            db.executePrepared();

            while (db.result.next()) {
                LedgerEntry ledgerEntry = new LedgerEntry();
                ledgerEntry.setID(db.result.getString("ledger_id"));
                ledgerEntry.load();
                this.add(ledgerEntry);
            }
            this.isLoaded = true;

        } catch (SQLException sqle) {
            throw new PersistenceException("LedgerEntry load operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

    }


    public String getXML() {
        return ("<?xml version=\"1.0\" ?>\n" + getXMLBody());
    }

    public String getXMLBody() {

        StringBuffer sb = new StringBuffer();
        sb.append("<LedgerEntryList>");
        sb.append("<JSPRootURL>" + SessionManager.getJSPRootURL() + "</JSPRootURL>");
        sb.append("<TotalAmountNotInvoiced>" + this.getTotalAmountNotInvoiced() + "</TotalAmountNotInvoiced>");
        sb.append(this.getSortOrderXML());
        Iterator iter = this.iterator();

        while (iter.hasNext()) {
            LedgerEntry ledgerEntry = (LedgerEntry)iter.next();
            sb.append(ledgerEntry.getXMLBody());
        }
        sb.append("</LedgerEntryList>");
        //System.out.println(sb.toString());
        return sb.toString();

    }

    /**
     * Sets the filters on this collection to limit loading ledger entries.
     * @param status the entry status (invoiced/not-invoiced) string that will be used to load entries
     * @param category the ledger entry category string that will be used to load ledger entries
     * @param group a ledger entry group string that will be used to load the ledger entries
     */

    public void setLedgerFilters(String status, String category, String group) {

        StringBuffer sb = new StringBuffer();
        sb.append("WHERE ");
        boolean and = false;

        if (status.equals("all") && category.equals("all") && group.equals("")) {

            this.whereClause = "";
        } else {
            if (!status.equals("all")) {
                if (status.equals("invoiced")) {
                    sb.append(" invoice_status_id = " + InvoiceStatus.INVOICED.getID());
                } else {
                    sb.append(" invoice_status_id = " + InvoiceStatus.NOT_INVOICED.getID());
                }
                and = true;
            }
            if (!category.equals("all")) {
                if (and) {
                    sb.append(" AND ");
                }
                if (category.equals("License_Usage_Type_A")) {
                    sb.append(" category_id = '" + CategoryID.LICENSE_USAGE_TYPE_A.getID() + "'");
                } else if (category.equals("License_Maintenance_Type_B")) {
                    sb.append(" category_id = '" + CategoryID.LICENSE_MAINTENANCE_TYPE_B.getID() + "'");
                }
                and = true;
            }

            if (!group.equals("all")) {
                if (and) {
                    sb.append(" AND ");
                }
                if (group.equals("Trial")) {
                    sb.append(" group_type_id = '" + GroupTypeID.TRIAL.getID() + "'");
                } else if (group.equals("Charge_Code")) {
                    sb.append(" group_type_id = '" + GroupTypeID.CHARGE_CODE.getID() + "'");
                } else if (group.equals("Credit_Card")) {
                    sb.append(" group_type_id = '" + GroupTypeID.CREDIT_CARD.getID() + "'");
                }

            }
            this.whereClause = sb.toString();

        }
    }

    /**
     * Sets the payment information and the license key filters on this collection to limit loading ledger entries.
     * @param payInfo the payment information string that will be used to load a ledger entry
     * @param folName first or last name string that will be used to load a ledger entry
     * @param partNumber the license key string that will be used to load a ledger entry
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

    private double getTotalAmountNotInvoiced() {

        double quantity = 0;
        double unitPrice = 0;
        double cost = 0;
        double totalAmountNotInvoiced = 0;

        Iterator iter = this.iterator();
        while (iter.hasNext()) {
            LedgerEntry nextEntry = (LedgerEntry)iter.next();
            if (nextEntry.getInvoiceStatus().getID().equals(InvoiceStatus.NOT_INVOICED.getID())) {
                quantity = nextEntry.getQuantity().getValue().doubleValue();
                unitPrice = nextEntry.getUnitPrice().getValue().doubleValue();
                cost = quantity * unitPrice;
                totalAmountNotInvoiced = totalAmountNotInvoiced + cost;

            }
        }

        return totalAmountNotInvoiced;

    }
}
