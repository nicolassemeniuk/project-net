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

 package net.project.security.domain;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;

import net.project.admin.RegistrationBean;
import net.project.admin.RegistrationException;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.User;

import org.apache.log4j.Logger;

/**
 * This class manages the Domain Migration for the Individual User
 */
public class UserDomainMigrationManager implements Serializable {

    /**
     * the user in the session
     */
    private User user = null;

    private DomainMigration domainMigration = new DomainMigration() ;

    /**
     * The last actvity date
     */
    private Date lastActivityDate = null;

    /**
     * the user's migration status
     */
    private int userMigrationStatus = -1;

    /**
     * Sets the Migration ID
     * 
     * @param migrationID
     *               the migration ID
     */
    public void setMigrationID (String migrationID) {
        this.domainMigration.setID(migrationID);
    }

    /**
     * Returns the Migration ID
     * 
     * @return the domain migration ID
     */
    public String getMigrationID () {
        return this.domainMigration.getID();
    }

    /**
     * Returns the domain ID from which the migration is suppose to take place
     * 
     * @return the the domain ID from which the migration is suppose to take place
     */
    public String getSourceDomainID () {
        return this.domainMigration.getSourceDomainID();
    }

    /**
     * Sets the domain ID from which the migration is suppose to take place
     * 
     * @param sourceID the domain ID from which the migration is suppose to take place
     */
    public void setSourceDomainID (String sourceID) {
        this.domainMigration.setSourceDomainID(sourceID) ;
    }

    /**
     * Returns the domain ID to which the migration is suppose to take place
     * 
     * @return the domain ID to which the migration is suppose to take place
     */
    public String getTargetDomainID () {
        return this.domainMigration.getTargetDomainID();
    }

    /**
     * Sets the domain ID to which the migration is suppose to take place
     * 
     * @param targetID the domain ID to which the migration is suppose to take place
     */
    public void setTargetDomainID (String targetID) {
        this.domainMigration.setTargetDomainID(targetID);
    }

    /**
     * Return the Migration Message
     * 
     * @return the Migration Message
     */
    public String getMigrationMessage () {
        return this.domainMigration.getMigrationMessage();
    }

    /**
     * Sets the Migration Message
     * 
     * @param message the Migration Message
     */
    public void setMigrationMessage(String message) {
        this.setMigrationMessage(message);
    }
    
     /**
     * Returns the domain  from which the migration is suppose to take place
     * 
     * @return the  domain  from which the migration is suppose to take place
     * @exception DomainMigrationException
     *                   if anything goes wrong while fetching it
     */
    public UserDomain getSourceDomain ()  throws DomainMigrationException {      
       return this.domainMigration.getSourceDomain();
    }

    /**
     * Sets the domain from which the migration is suppose to take place
     * 
     * @param sourceDomain the domain  from which the migration is suppose to take place
     */
    public void setSourceDomain (UserDomain sourceDomain) {
        this.domainMigration.setSourceDomain(sourceDomain);
    }

    /**
     * Returns the domain  to which the migration is suppose to take place
     * 
     * @return the domain  to which the migration is suppose to take place
     * @exception DomainMigrationException
     *                   if anything goes wrong while fetching it
     */
    public UserDomain getTargetDomain () throws DomainMigrationException {
        return this.domainMigration.getTargetDomain();
    }

    /**
     * Sets the domain  to which the migration is suppose to take place
     * 
     * @param targetDomain the domain  to which the migration is suppose to take place
     */
    public void setTargetDomain (UserDomain targetDomain) {
        this.domainMigration.setTargetDomain(targetDomain); 
    }

    /**
     * Returns the user
     * 
     * @return the current<code>User</code> instance in session
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Sets the user
     * 
     * @param user the current<code>User</code> instance in session
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Returns the Domain Migration Status for the User
     * 
     * @return The Domain Migration Status
     */
    public int getUserMigrationStatus(){
        return this.userMigrationStatus;
    }

    /**
     * Returns the last Activity date for User
     * 
     * @return the last Activity date for User
     *
     */
    public Date getLastActivityDate() {
        return this.lastActivityDate;
    }

    /**
     * Resets the object
     */
    public void clear() {

        user = null;
        domainMigration.clear();
        lastActivityDate = null;
        userMigrationStatus = -1;

    }
    
    /**
     * Returns the Domain Migration Status for the User
     * 
     * @param migrationStatus the Migration status
     * @exception PersistenceException
     *                   is thrown if anything goes wrong while loading the info. from the database
     */
    public void setUserMigrationStatus( int migrationStatus ) throws PersistenceException {
        DBBean dbean = new DBBean();

        try {
            setUserMigrationStatus (migrationStatus , dbean);
        } finally {
            dbean.release();
        }
    }

    /**
     * Returns the Domain Migration Status for the User
     * 
     * @param migrationStatus Migration status
     * @param dbean database bean
     * @exception PersistenceException
     *                   is thrown if anything goes wrong while loading the info. from the database
     */
    public void setUserMigrationStatus(int migrationStatus , DBBean dbean) throws PersistenceException {
    
        if (user == null )
            throw new NullPointerException("UserDomainMigrationManager setUserMigrationStatus(): user should not be null.");
    
        StringBuffer query = new StringBuffer();
      
        query.append("update pn_user_domain_migration set migration_status_id = ? , activity_date = sysdate ");
        query.append(" where user_id = ? and is_current ='1'"); 

        try {
            int index = 0 ;
            dbean.prepareStatement(query.toString());

            dbean.pstmt.setInt(++index , migrationStatus);
            dbean.pstmt.setString(++index , user.getID());

            dbean.executePrepared();

            this.userMigrationStatus = migrationStatus;
            
        } catch ( SQLException sqle ) {
        	Logger.getLogger(UserDomainMigrationManager.class).error("UserDomainMigrationManager setUserMigrationStatus() threw an SQL exception: " + sqle);
              throw new PersistenceException("UserDomainMigrationManager setUserMigrationStatus() threw an SQL exception: " + sqle, sqle);
        } finally {
              dbean.release();
        } 

    }

    /**
     * Returns the Domain Migration Status for the User
     * 
     * @exception PersistenceException
     *                   is thrown if anything goes wrong while loading the info. from the database
     */
    public  void load() throws PersistenceException {
    
        if (user == null )
            throw new NullPointerException("UserDomainMigrationManager.load(): user should not be null.");
    
        DBBean dbean = new DBBean(); 
        StringBuffer query = new StringBuffer();

        query.append(" select pu.migration_status_id , pu.activity_date , p.domain_migration_id , p.from_domain , " );
        query.append(" p.to_domain , p.admin_message_clob from pn_user_domain_migration pu , ");
        query.append(" pn_domain_migration p where user_id = ? and is_current ='1'");
        query.append(" and  pu.domain_migration_id = p.domain_migration_id "); 

        try {
            int index = 0 ;
            dbean.prepareStatement(query.toString());

            dbean.pstmt.setString(++index , user.getID());
            
            dbean.executePrepared();
            
            if(dbean.result.next()){

                this.userMigrationStatus = dbean.result.getInt("migration_status_id");

                java.sql.Timestamp timeStamp = dbean.result.getTimestamp("activity_date");
                this.lastActivityDate = new java.util.Date(timeStamp.getTime());

                this.domainMigration.setID( String.valueOf(dbean.result.getInt("domain_migration_id")));
                this.domainMigration.setSourceDomainID( String.valueOf(dbean.result.getInt("from_domain")));
                this.domainMigration.setTargetDomainID( String.valueOf(dbean.result.getInt("to_domain")));
                this.domainMigration.setMigrationMessage(ClobHelper.read(dbean.result.getClob("admin_message_clob")));

            }
            

        } catch ( SQLException sqle ) {
        	Logger.getLogger(UserDomainMigrationManager.class).error("Domain Migration load() threw an SQL exception: " + sqle);
              throw new PersistenceException("Domain Migration load() failed: " + sqle, sqle);
        } finally {
              dbean.release();
        } 

    }

    /**
     * Returns whether the Domain Migration is supported or not based on the user's current configurations
     * 
     * @return the Domain Migration is supported or not
     */
    public boolean isDomainMigrationSupported() {
        boolean isSupported = false ;
    
        try {

            UserDomain targetDomain = new UserDomain();
            targetDomain.setID(this.getSourceDomainID());
            targetDomain.loadSupportedConfigurations();
            isSupported = targetDomain.isConfigurationSupported(user.getCurrentConfigurationID());

        } catch ( PersistenceException pe) {
        	Logger.getLogger(UserDomainMigrationManager.class).error("UserDomainMigrationManager isDomainMigrationSupported() threw an Persistence exception: " + pe);
        }
        
        return isSupported;
    }

    /**
     * Migrates the user from the Source Domain to Target Domain
     * 
     * @param registration
     *               The registration instance
     * @exception DomainMigrationException
     */
    public void migrateUser(RegistrationBean registration)  
            throws DomainMigrationException {

        if( this.user != null && this.user.isApplicationAdministrator())
            throw new DomainMigrationException(" Application Administrator should not migrate Domains ");

        DBBean dbean = new DBBean();

        try {

            dbean.setAutoCommit(false);
            
            getSourceDomain().removeUser(user,dbean);


            registration.setUserDomain(getTargetDomain().getID());
            getTargetDomain().addUser(registration , registration.getDirectoryEntry() , dbean);

            registration.updateRegistration(dbean);
            registration.updateInvitedUser(dbean);
           
            // Update the user status
            setUserMigrationStatus(DomainMigrationStatus.MIGRATION_COMPLETED , dbean );

            // Commit the transaction 
            dbean.commit();
            
            // Finally notify the user 
            DomainMigrationNotification domainMigrationNotification = new DomainMigrationNotification();
            domainMigrationNotification.notifyUser(this,registration);

        } catch (java.sql.SQLException sqle) {
        	Logger.getLogger(UserDomainMigrationManager.class).error("UserDomainMigrationManager migrateUser() failed:" + sqle);
            throw new DomainMigrationException("UserDomainMigrationManager migrateUser() failed:" + sqle, sqle);
            
        } catch (PersistenceException pe) {
        	Logger.getLogger(UserDomainMigrationManager.class).error("UserDomainMigrationManager migrateUser() failed:" + pe);
            throw new DomainMigrationException("UserDomainMigrationManager migrateUser() failed:" + pe, pe);
            
        } catch (RegistrationException re) {
        	Logger.getLogger(UserDomainMigrationManager.class).error("UserDomainMigrationManager migrateUser() failed:" + re);
            throw new DomainMigrationException("UserDomainMigrationManager migrateUser() failed:" + re, re);

        } catch (DomainException de) {
        	Logger.getLogger(UserDomainMigrationManager.class).error("UserDomainMigrationManager migrateUser() failed:" + de);
            throw new DomainMigrationException("UserDomainMigrationManager migrateUser() failed:" + de, de);
            
        } finally {
            try {
                dbean.rollback();
            } catch (java.sql.SQLException sqle) {
                // Simply release
            }

            dbean.release();
        }
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
        xml.append("<UserDomainMigrationManager>\n");
        xml.append(domainMigration.getXMLBody());
        xml.append("</UserDomainMigrationManager>\n");
        return xml.toString();
    }

}

