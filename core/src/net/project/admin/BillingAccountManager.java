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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|    $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.admin;

import java.sql.SQLException;

import net.project.business.BusinessSpace;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.User;

import org.apache.log4j.Logger;

/**
  * Manages billing and relationships between BillingAccounts, users, and master businesses.
  */
public class BillingAccountManager
{

    /**
     *  Get the master businessID for this user.
     *  Currently, a user can have only one master business that he acts as an employee of.
    *   @return the master businessID, null if none found.
    */
    public static String getMasterBusinessIDForUser(User user)
    throws PersistenceException 
    {
        DBBean db = new DBBean();

        try
        {
            db.executeQuery("select business_id from pn_user_has_master_business where person_id = " + user.getID());
            if (db.result.next())
                return db.result.getString("business_id");
            else
                return null;
        }
        catch (SQLException sqle)
        {
        	Logger.getLogger(BillingAccountManager.class).error("BillingAccountManager.getMasterBusinessForUser() threw a SQLException: " + sqle);
            throw new PersistenceException("Error loading master business for user.", sqle);
        }
        finally
        {
            db.release();
        } 
    }



    /**
     *  Get the master business space for this user.
     *  Currently, a user can have only one master business that he acts as an employee of.
     *  @return the master BusinessSpace loaded from the database, null if none found.
    */
    public static BusinessSpace getMasterBusinessSpaceForUser(User user)
    throws PersistenceException 
    {
        String businessID = null;

        if ((businessID = getMasterBusinessIDForUser(user)) != null)
        {
            BusinessSpace businessSpace = new BusinessSpace();
            businessSpace.setID(businessID);
            businessSpace.load();
            return businessSpace;
        }
        else
            return null;
    }



}
