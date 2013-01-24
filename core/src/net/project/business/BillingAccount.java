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

 package net.project.business;

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.security.User;

/**
 * This object will generate a description for the object type specified
 */
public class BillingAccount {
    // user needing object description
    private String billingAccountID = null;
    protected User user = null;

    protected String billingMethodID = null;
    protected String accountStatusID = null;
    protected boolean isLoaded = false;

    // constructor
    public BillingAccount() {
        // do nothing
    }


    public String getAccountStatusID() {
        return accountStatusID;
    }


    public void setAccountStatusID(String id) {
        this.accountStatusID = id;
    }

    public String getBillingAccountID() {
        return billingAccountID;
    }


    public void setBillingAccountID(String id) {
        this.billingAccountID = id;
    }


    public String getBillingMethodID() {
        return this.billingMethodID;
    }

    public void setBillingMethodID(String id) {
        this.billingMethodID = id;
    }

    public void store() {
        String query = "insert into pn_billing_account(billing_account_id,billing_method_id,account_status_id)" +
            "values(" + billingAccountID + "," + billingMethodID + "," + accountStatusID + ")";

        DBBean db = new DBBean();
        try {
            //System.out.println("query is"+query);
            db.setQuery(query);
            db.executeQuery();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            db.release();
        }
    }

    /**
     * is an object descrption loaded
     */
    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Load the description for an object
     */
    public void load() {
        String query = "select b.billing_account_id, b.billing_method_id," +
            " b.account_status_id from pn_business b" +
            " where b.business_id = " + "'" + billingAccountID + "'";

        DBBean db = new DBBean();
        try {
            db.setQuery(query);
            db.executeQuery();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            db.release();
        }
    }
}



