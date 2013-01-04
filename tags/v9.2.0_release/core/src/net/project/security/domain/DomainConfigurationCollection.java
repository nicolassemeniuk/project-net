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
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
+--------------------------------------------------------------------------------------*/

package net.project.security.domain;

import java.sql.SQLException;

import net.project.configuration.ConfigurationSpace;
import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;

/**
 * This class represents a collection of configurations supported by a UserDomain
 * 
 * @author Philip Dixon
 * @see net.project.security.domain.UserDomain
 * @since Gecko
 */
class DomainConfigurationCollection extends java.util.ArrayList 
implements IXMLPersistence, java.io.Serializable {

    /** ID of the domain this collection of users belongs to */
    String domainID = null;
    /** Database access bean */
    private DBBean db = new DBBean();
    /** isLoaded flag */
    private boolean isLoaded = false;

    private static final String ACTIVE = "active";

    private static final String ALL = "all";



    /* ------------------------------- Constructor(s)  ------------------------------- */

    /**
     * Empty constructor to support bean compliance.
     * @since Gecko
     */
    protected DomainConfigurationCollection() {
        // do nothing
    }


    /**
     * Instantiate a new <code>DomainConfigurationCollection</code> object with the domainID specified
     * 
     * @param domainID The id of the domain in question.
     * @since Gecko
     */
    protected DomainConfigurationCollection (String domainID) {

        setDomainID (domainID);
    }


    /* ------------------------------- Interaction Methods  ------------------------------- */


    /**
     * Boolean indicator of whether or not the specified configuration is suppoted by this domain.
     * Will return false by default if the collection is not loaded.
     * 
     * @param configurationID
     *               ID of the configuration in question
     * @return True if the configuration is supported by the domain, false if not
     */
    protected boolean isSupportedConfiguration (String configurationID) {
        boolean isSupported = false ;

        for (java.util.Iterator it = iterator(); it.hasNext(); ) {
                ConfigurationSpace nextConfiguration = (ConfigurationSpace) it.next();
                
                if (nextConfiguration != null && nextConfiguration.getID().equals(configurationID)) {
                    return true;
                }
        }

        return isSupported;
    }



    /* ------------------------------- Collection Methods  ------------------------------- */

    /**
     * Populate this collection with the IDs of all of the active configurations that are supported by the domain specified by the class member "domainID".
     * This load operation performs a lazy load, bringing in only the IDs of the configurations
     * 
     * @param db     The database bean
     * @exception PersistenceException
     *                   Throws a <code>PersistenceException</code> if the load operation fails or of the domainID is invalid.
     * @see net.project.persistence.PersistenceException
     * @see net.project.resource.Person
     * @see java.util.HashMap
     * @since Gecko
     */

    protected void loadActiveOnly(DBBean db) throws PersistenceException {
        loadConfigurations(DomainConfigurationCollection.ACTIVE , db);
    }

    /**
     * Populate this collection with the IDs of all of the active configurations that are supported by the domain specified by the class member "domainID".
     * This load operation performs a lazy load, bringing in only the IDs of the configurations
     * 
     * @exception PersistenceException
     *                   Throws a <code>PersistenceException</code> if the load operation fails or of the domainID is invalid.
     * @see net.project.persistence.PersistenceException
     * @see net.project.resource.Person
     * @see java.util.HashMap
     * @since Gecko
     */
    protected void loadActiveOnly() throws PersistenceException {
        DBBean db = new DBBean();

        try {
            loadActiveOnly(db);
        } finally {
            db.release();
        }

    }        

    /**
     * Populate this collection with the IDs of all of the configurations that are supported by the domain specified by the class member "domainID".
     * This load operation performs a lazy load, bringing in only the IDs of the configurations
     * 
     * @exception PersistenceException
     *                   Throws a <code>PersistenceException</code> if the load operation fails or of the domainID is invalid.
     * @see net.project.persistence.PersistenceException
     * @see net.project.resource.Person
     * @see java.util.HashMap
     * @since Gecko
     */

    protected void load() throws PersistenceException {
        DBBean db = new DBBean();

        try {
            load(db);
        } finally {
            db.release();
        }

    }


    /**
     * Populate this collection with the IDs of all of the configurations that are supported by the domain specified by the class member "domainID".
     * This load operation performs a lazy load, bringing in only the IDs of the configurations
     * 
     * @param db     The database bean
     * @exception PersistenceException
     *                   Throws a <code>PersistenceException</code> if the load operation fails or of the domainID is invalid.
     * @see net.project.persistence.PersistenceException
     * @see net.project.resource.Person
     * @see java.util.HashMap
     * @since Gecko
     */

    protected void load(DBBean db) throws PersistenceException {
        loadConfigurations(DomainConfigurationCollection.ALL , db);
    }


    /**
     * Populate this collection with the IDs of all of the configurations that are supported by the domain specified by the class member "domainID".
     * This load operation performs a lazy load, bringing in only the IDs of the configurations
     * 
     * @return <code>String</code> Type of Configurations
     * @exception PersistenceException
     *                   Throws a <code>PersistenceException</code> if the load operation fails or of the domainID is invalid.
     * @see net.project.persistence.PersistenceException
     * @see net.project.resource.Person
     * @see java.util.HashMap
     * @since Gecko
     */

    private void loadConfigurations(String type , DBBean db) throws PersistenceException {

        ConfigurationSpace configuration = null;
        StringBuffer query = new StringBuffer();

        query.append("select c.configuration_id, c.configuration_name, c.configuration_desc, ");
        query.append("c.created_by_id, c.created_datetime, c.modified_by_id, c.modified_datetime, ");
        query.append("c.crc, c.record_status, c.brand_id ");
        query.append("from pn_configuration_space c, pn_user_domain_supports_config udsc ");
        query.append("where c.configuration_id = udsc.configuration_id ");
        query.append("and udsc.domain_id = ? ");

        if (type != null && type.equals(ACTIVE)) {
            query.append("and c.record_status = 'A' ");
        }

        if (this.domainID == null) {
            throw new PersistenceException ("DomainConfigurationCollection.load() was unable to load the supported configurations for this domain " +
                                            "because of an invalid (null) domainID");
        }

        // now, first clear the collection so that we don't have weird data issues
        clear();

        try {

            db.prepareStatement(query.toString());
            db.pstmt.setString (1, this.domainID);
            db.executePrepared();

            while (db.result.next()) {

                configuration = new ConfigurationSpace();

                configuration.setID (db.result.getString("configuration_id"));
                configuration.setName (db.result.getString("configuration_name"));
                configuration.setDescription (db.result.getString("configuration_desc"));
                configuration.setCreatedByID (db.result.getString("created_by_id"));
                configuration.setCreatedDatetime (db.result.getTimestamp("created_datetime"));
                configuration.setModifiedByID (db.result.getString("modified_by_id"));
                configuration.setModifiedDatetime (db.result.getTimestamp("modified_datetime"));
                configuration.setCrc (db.result.getTimestamp("crc"));

                this.add (configuration);
            }

            this.isLoaded = true;

        } catch (SQLException sqle) {
            throw new PersistenceException ("DomainConfigurationCollection load operation failed due to an SQLE", sqle);

        } 

    }



    /**
     * Store the supported configurations for this domain.
     * <br>
     * This is implemented by deleting existing domain/configuration mappings and inserting all new ones.
     * In a multi-instance enviornment this may cause contention, but due to the central and administrative nature of
     * the functionality, it shouldn't be a problem.
     * 
     * @exception PersistenceException
     *                   Throws a persistence exception if the store operation fails
     * @since Gecko
     */
    protected void store() throws PersistenceException {

        try {

            // first turn off atocommit
            this.db.setAutoCommit (false);

            // then, store the map
            store (this.db);

            // finally, commit
            this.db.commit();
        }

        catch (SQLException sqle) {

            try {
                this.db.rollback();
            } catch (SQLException sqle2) { /* do nothing */
            }

            throw new PersistenceException ("DomainConfigurationCollection.store() threw an SQLE: ", sqle);
        }

        finally {
            db.release();
        }
    }


    /**
     * Store the supported configurations for this domain.
     * <br>
     * This is implemented by deleting existing domain/configuration mappings and inserting all new ones.
     * In a multi-instance enviornment this may cause contention, but due to the central and administrative nature of
     * the functionality, it shouldn't be a problem.
     * 
     * @param db     An instantiated database access bean.
     * @exception PersistenceException
     *                   Throws a persistence exception if the store operation fails
     * @since Gecko
     */
    protected void store (DBBean db) throws SQLException {

        // first delete exsiting supported configurations
        deleteSupportedConfigurations (db);

        // then store the configuration maps in this collection
        storeSupportedConfigurations (db);
    }


    /**
     * Store any existing relationships to the persistence store
     * 
     * @param db     Instantiated database access bean
     * @exception SQLException
     *                   If the database operation fails
     * @since Gecko
     */
    private void storeSupportedConfigurations (DBBean db) throws SQLException {

        boolean execute = false;
        java.util.Iterator configurations = this.iterator();
        String qstrCreateConfigurationMap = "insert into pn_user_domain_supports_config (domain_id, configuration_id) " +
                                            "values (?, ?)";


        db.prepareStatement (qstrCreateConfigurationMap);

        // if there are any configuration maps iterate and store
        // add the maps to the batch
        while ( configurations.hasNext() ) {

            db.pstmt.setString (1, this.domainID);
            db.pstmt.setString (2, ((ConfigurationSpace) configurations.next()).getID());

            db.pstmt.addBatch();

            execute = true;
        }

        // then, if there were any to add, execute the batch
        if (execute)
            db.executePreparedBatch();
    }


    /**
     * Delete existing configurations for the domainID
     * 
     * @param db     Instantiated database access bean
     * @exception SQLException
     *                   Throws an SQLE if the database operation failed.
     * @since Gecko
     */
    private void deleteSupportedConfigurations (DBBean db) throws SQLException {

        String qstrDeleteConfigurationMap = "delete from pn_user_domain_supports_config where domain_id = ?";

        // first delete existing maps
        db.prepareStatement (qstrDeleteConfigurationMap);

        db.pstmt.setString (1, this.domainID);

        db.executePrepared();
    }



    /* ------------------------------- Getters and Setters  ------------------------------- */


    /**
     * Set the ID of this domain
     * 
     * @param domainID The ID of this domain
     */
    protected void setDomainID (String domainID) {
        this.domainID = domainID;
    }


    /**
     * Get the ID of this domain
     * 
     * @param domainID The ID of this domain
     */
    protected String getDomainID() {
        return this.domainID;
    }


    /**
     * Returns a boolean indicator represented the "loaded" status of this collection.
     * 
     * @return True if the collection is loaded, false if not.
     * @since Gecko
     */
    protected boolean isLoaded() {

        return this.isLoaded;
    }



    /**
     * Clears the array and sets the collection to include those items specified in the collections string array
     * 
     * @param configurations
     *               String array of configuration ID's
     * @since Gecko
     */
    protected void setSupportedConfigurations (String[] configurations) {

        this.clear();

        for (int i = 0; i < configurations.length; i++) {
            ConfigurationSpace cspace = new ConfigurationSpace();
            cspace.setID(configurations[i]);
            this.add(cspace);
        }
    }
    
    /**
     * Adds the following configuration to the collection
     * 
     * @param configuration
     *               The configuration ID
     */
    protected void addSupportedConfigurations (String configuration) {
        ConfigurationSpace cspace = new ConfigurationSpace();
        cspace.setID(configuration);
        this.add(cspace);
    }


    /* ------------------------------- Utility Methods  ------------------------------- */

    public void clear() {

        super.clear();
        this.isLoaded = false;
    }



    /* ------------------------------- XML Methods  ------------------------------- */    


    /**
     * Returns the properties of this collection in XML format.
     * 
     * @return 
     * @since Gecko
     */
    public String getXML() {
        return getXMLDocument().getXMLString();
    }


    /**
     * Returns the properties of this collection in XML format -- without the version string
     * 
     * @return 
     * @since Gecko
     */
    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    protected net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();

        try {
            doc.startElement("DomainConfigurationCollection");
            
            for (java.util.Iterator it = iterator(); it.hasNext(); ) {
                ConfigurationSpace nextConfiguration = (ConfigurationSpace) it.next();
                doc.addXMLString(nextConfiguration.getXMLBody());
            }

            doc.endElement();

        } catch (net.project.xml.document.XMLDocumentException e) {
            // Return empty document

        }

        return doc;
    }

}

