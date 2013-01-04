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

 /*-------------------------------------------------------------------+
|
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
+-------------------------------------------------------------------*/
package net.project.security.domain;


import java.sql.SQLException;

import net.project.database.DBBean;

import org.apache.log4j.Logger;

/**
 * This class manages the Domain Migration
 */
public class DomainMigrationManager {

    /**
     * Inserts migration records for all active users in the domain
     * 
     * @param domain_migration_id
     *               The domain migration ID
     * @param source_domain_id
     *               The domain from which the migration is going to take place
     * @exception DomainMigrationException
     *                   is thrown if anything goes wrong while creating records in the database
     */
    public void insertMigrationRecords ( String domain_migration_id , String source_domain_id ) 
        throws DomainMigrationException {
            
        if (domain_migration_id == null || source_domain_id == null )
            throw new DomainMigrationException("DomainMigrationManager.insertMigrationRecords(): migration id and source domain id should not be null.");
            
        DBBean dbean = new DBBean(); 
       
        try {
            int index = 0 ;
            dbean.prepareCall("{call USER_DOMAIN.CREATE_MIGRATION_RECORD (?, ?, ? )}");

            dbean.cstmt.setString(++index , domain_migration_id);
            dbean.cstmt.setString(++index , source_domain_id);
            dbean.cstmt.setInt(++index , DomainMigrationStatus.NEVER_STARTED);

            dbean.executeCallable();
            dbean.closeCStatement();
    
        } catch ( SQLException sqle ) {
        	Logger.getLogger(DomainMigrationManager.class).error("DomainMigrationManger insertMigrationRecords() threw an SQL exception: " + sqle);
              throw new DomainMigrationException("DomainMigrationManger insertMigrationRecords() failed: " + sqle, sqle);
        } finally {
              dbean.release();
        } 
    
    }

}

