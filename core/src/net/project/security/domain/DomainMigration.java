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


import java.io.Serializable;
import java.sql.SQLException;

import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.database.ObjectManager;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.SecurityException;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * The DomainMigration class represents a migration of user(s) from one domain
 * to another.
 *
 * @author Deepak Pandey
 * @since Gecko Update 2
 */
public class DomainMigration implements IXMLPersistence , IJDBCPersistence, Serializable {

    /**
     * The Domain Migration ID
     */
    private String migrationID = null;
    /**
     * The Source Domain 
     */
    private UserDomain sourceDomain = null;
    /**
     * The target domain  
     */
    private UserDomain targetDomain = null;
    /**
     * The Source Domain ID
     */
    private String sourceDomainID = null;
    /**
     * The target domain  ID
     */
    private String targetDomainID = null;

    /**
     * The Admin Message for the Migration
     */
    private String migrationMessage = null;


    /**
     * Sets the Migration ID
     * 
     * @param migrationID
     *               the migration ID
     */
    public void setID (String migrationID) {
        this.migrationID = migrationID;
    }

    /**
     * Returns the Migration ID
     * 
     * @return the domain migration ID
     */
    public String getID () {
        return this.migrationID;
    }

    /**
     * Returns the domain ID from which the migration is suppose to take place
     * 
     * @return the the domain ID from which the migration is suppose to take place
     */
    public String getSourceDomainID () {
        return this.sourceDomainID;
    }

    /**
     * Sets the domain ID from which the migration is suppose to take place
     * 
     * @param sourceID the domain ID from which the migration is suppose to take place
     */
    public void setSourceDomainID (String sourceID) {
        this.sourceDomainID = sourceID ;
    }

    /**
     * Returns the domain ID to which the migration is suppose to take place
     * 
     * @return the domain ID to which the migration is suppose to take place
     */
    public String getTargetDomainID () {
        return this.targetDomainID;
    }

    /**
     * Sets the domain ID to which the migration is suppose to take place
     * 
     * @param targetID the domain ID to which the migration is suppose to take place
     */
    public void setTargetDomainID (String targetID) {
        this.targetDomainID = targetID ;
    }

    /**
     * Returns the domain  from which the migration is suppose to take place
     * 
     * @return the  domain  from which the migration is suppose to take place
     * @exception DomainMigrationException
     *                   if anything goes wrong while fetching it
     */
    public UserDomain getSourceDomain ()  throws DomainMigrationException {
        
        if (this.sourceDomainID == null) {
            throw new DomainMigrationException (" The Source Domain ID is null .");
        } else {
            try {
                this.sourceDomain = UserDomainFactory.makeDomainForDomainID(this.sourceDomainID) ;
            } catch ( SecurityException se) {
                throw new DomainMigrationException (" "+ se);
            }
        }         
        return this.sourceDomain ;
    }

    /**
     * Sets the domain from which the migration is suppose to take place
     * 
     * @param sourceDomain the domain  from which the migration is suppose to take place
     */
    public void setSourceDomain (UserDomain sourceDomain) {
        this.sourceDomain = sourceDomain ;
    }

    /**
     * Returns the domain  to which the migration is suppose to take place
     * 
     * @return the domain  to which the migration is suppose to take place
     * @exception DomainMigrationException
     *                   if anything goes wrong while fetching it
     */
    public UserDomain getTargetDomain() throws DomainMigrationException {

        if (this.targetDomainID == null) {
            throw new DomainMigrationException (" The Target Domain ID is null .");
        } else {
            try {
                this.targetDomain = UserDomainFactory.makeDomainForDomainID(this.targetDomainID) ;
            } catch ( SecurityException se) {
                throw new DomainMigrationException (" "+ se);
            }
        }       
        return this.targetDomain;
    }

    /**
     * Sets the domain  to which the migration is suppose to take place
     * 
     * @param targetDomain the domain  to which the migration is suppose to take place
     */
    public void setTargetDomain (UserDomain targetDomain) {
        this.targetDomain = targetDomain ;
    }
    /**
     * Return the Migration Message
     * 
     * @return the Migration Message
     */
    public String getMigrationMessage () {
        return this.migrationMessage;
    }

    /**
     * Sets the Migration Message
     * 
     * @param message the Migration Message
     */
    public void setMigrationMessage(String message) {
        this.migrationMessage = message ;
    }

    /**
     * Stores the Domain Migration record to the database.
     * 
     * @exception PersistenceException
     *                   Thrown to indicate a failure loading from the database, a system-level error.
     */
    public void store() throws PersistenceException {

        if (this.migrationID == null || this.migrationID.trim().equals("")) {
            this.migrationID = new ObjectManager().getNewObjectID();
        }


        // Construct insert query
        // If the migration message is null, we insert null
        // Otherwise, we create an empty_clob() suitable for writing to
        StringBuffer insertQuery = new StringBuffer();
        insertQuery.append("insert into pn_domain_migration ")
                .append("(domain_migration_id,  from_domain, to_domain, ")
                .append("admin_message_clob) ")
                .append("values (?, ?, ?, ");
        if (getMigrationMessage() == null) {
            insertQuery.append("NULL ");
        } else {
            insertQuery.append("empty_clob() ");
        }
        insertQuery.append(") ");

        // Construct query to select CLOB for streaming
        StringBuffer selectQuery = new StringBuffer();
        selectQuery.append("select admin_message_clob from pn_domain_migration ")
                .append("where domain_migration_id = ? ");

        DBBean db = new DBBean();

        try {

            db.setAutoCommit(false);
            db.prepareStatement(insertQuery.toString());
            
            int index = 0;
            db.pstmt.setString(++index , getID());
            db.pstmt.setString(++index , getSourceDomainID());
            db.pstmt.setString(++index , getTargetDomainID());
            db.executePrepared();

            if (getMigrationMessage() != null) {
                // Now stream the migration message to the CLOB

                index = 0;
                db.prepareStatement(selectQuery.toString());
                db.pstmt.setString(++index, getID());
                db.executePrepared();

                if (db.result.next()) {
                    ClobHelper.write(db.result.getClob("admin_message_clob"), getMigrationMessage());
                } else {
                    throw new PersistenceException("Error storing domain migration; unable to select newly inserted row");
                }

            }

            db.commit();

        } catch (java.sql.SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException e) {
                // throw original error and release
            }
            throw new PersistenceException("DomainMigration store operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

    }

    /**
     * Load the Domain Migration record from the database.
     * 
     * @exception PersistenceException
     *                   Thrown to indicate a failure loading from the database, a system-level error.
     */
    public void load() throws PersistenceException {

        StringBuffer query = new StringBuffer();
        DBBean dbean = new DBBean(); 
       
        if (getID() == null)
            throw new NullPointerException("DomainMigration.load(): must call setID() before loading.");

        query.append("select from_domain , to_domain , admin_message_clob ");
        query.append(" from pn_domain_migration where domain_migration_id = ?");

        try {
            int index = 0 ;
            dbean.prepareStatement(query.toString());
            dbean.pstmt.setString(++index , getID());

            dbean.executePrepared();

            if (dbean.result.next()) {

                this.sourceDomainID = dbean.result.getString("from_domain");
                this.targetDomainID = dbean.result.getString("to_domain");
                this.migrationMessage = ClobHelper.read(dbean.result.getClob("admin_message_clob"));

            }

        } catch ( SQLException sqle ) {
        	Logger.getLogger(DomainMigration.class).error("Domain Migration load() threw an SQL exception: " + sqle);
            throw new PersistenceException("Domain Migration load() failed: " + sqle, sqle);
        } finally {
            dbean.release();
        } 

    } // end load()


    /**
     * Romoves the Domain Migration record from the Database . Not implemented as yet
     * 
     * @exception PersistenceException
     */
    public void remove() throws PersistenceException {
        // Not implemented
    }

    /**
     * Resets the object
     */
    public void clear() {

        this.migrationID = null ;
        this.sourceDomain = null ;
        this.targetDomain = null ;
        this.sourceDomainID = null ;
        this.targetDomainID = null ;
        this.migrationMessage = null;
    }

    /**
     * Returns the properties of this UserDomain in XML format.
     * @return the XML properties
     */
    public String getXML() {
        return IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
     * Returns the properties of this DomainMigration in XML format
     * @return the XML properties
     */
    public String getXMLBody() {
         
        StringBuffer xml = new StringBuffer();
        xml.append("<DomainMigration>\n");
        xml.append("<domain_migration_id>" + getID() + "</domain_migration_id>\n");
        xml.append("<source_domain_id>" + XMLUtils.escape(getSourceDomainID()) + "</source_domain_id>\n");

        if(this.sourceDomain != null) 
            xml.append("<source_domain_name>" + XMLUtils.escape(this.sourceDomain.getName()) + "</source_domain_name>\n");

        xml.append("<target_domain_id>" + XMLUtils.escape(getTargetDomainID()) + "</target_domain_id>\n");

        if(this.targetDomain != null) 
            xml.append("<target_domain_name>" + XMLUtils.escape(this.targetDomain.getName()) + "</target_domain_name>\n");

        xml.append("<migration_message>" + XMLUtils.escape(getMigrationMessage()) + "</migration_message>\n");
        xml.append("</DomainMigration>\n");
        return xml.toString();
    }

}
