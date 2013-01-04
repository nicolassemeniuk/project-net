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

 package net.project.configuration;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import net.project.base.DBErrorCodes;
import net.project.base.ExceptionList;
import net.project.base.PnetException;
import net.project.brand.Brand;
import net.project.database.DBBean;
import net.project.database.DBExceptionFactory;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.security.domain.UserDomain;
import net.project.security.domain.UserDomainManager;
import net.project.space.ISpaceTypes;
import net.project.space.Space;
import net.project.space.SpaceTypes;
import net.project.util.Conversion;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
  * ConfigurationSpace encapsulates configuration-specific functionality such
  * as branding.
  */
public class ConfigurationSpace extends Space implements Serializable, IJDBCPersistence, IXMLPersistence {

    /*
        Persistent attributes
     */
    private String createdByID = null;
    private Date createdDatetime = null;
    private String modifiedByID = null;
    private Date modifiedDatetime = null;
    private Date crc = null;
    private String brandID = null;
    private boolean isCreateDefaultForms = true;

    // The brand
    private Brand brand = null;
    
    private User user = null;
    /** Portfolios that Configuration Space must be added */
    private ArrayList portfolioMembership = null;

    /**
      * Creates a new configuration space.
      */
    public ConfigurationSpace() {
        super();
        setType(ISpaceTypes.CONFIGURATION_SPACE);
        setPortfolioMembership(new ArrayList());
        setBrand(new Brand());
    }

    /**
      * Creates a new configuration space for the specified id.
      * @param spaceID the space id of the configuration space.
      */
    public ConfigurationSpace(String spaceID) {
        super(spaceID);
        setType(ISpaceTypes.CONFIGURATION_SPACE);
        setPortfolioMembership(new ArrayList());
        this.spaceType = SpaceTypes.CONFIGURATION ;
        setBrand(new Brand());
    }


    /**
     * Copies this configuration space and returns the new copy.  This copies
     * portfolio membership and copies default forms for the space.
     * @return the new configuration space
     */
    public ConfigurationSpace copy() {
        return copy(true, true);
    }

    /**
     * Copies this configuration space and returns the new copy.
     * @param doCopyPortfolioMembership true means the portfolio memberhsip
     * will be copied.  False means configuration space will not be a member
     * of any portfolios.
     * @return the new configuration space
     */
    public ConfigurationSpace copy(boolean doCopyPortfolioMembership, boolean isCreateDefaultForms) {
        ConfigurationSpace newSpace = new ConfigurationSpace();

        try {
            // Specify whether to create default forms when space is stored
            newSpace.setCreateDefaultForms(isCreateDefaultForms);
            newSpace.setBrand(getBrand().copy());
            newSpace.setName(getName());
            newSpace.setDescription(getDescription());
            newSpace.setUser(getUser());

            if (doCopyPortfolioMembership) {
                newSpace.setPortfolioMembership(getPortfolioMembership());
            }

        } catch (PnetException pe) {
        	Logger.getLogger(ConfigurationSpace.class).error("ConfigurationSpace.copy() threw a PnetException: " + pe);
        
        }

        return newSpace;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private User getUser() {
        return this.user;
    }

    public void setName(String configurationName) {
        super.setName(configurationName);
        brand.setName(configurationName);
    }

    public void setDescription(String configurationDesc) {
        super.setDescription(configurationDesc);
        brand.setDescription(configurationDesc);
    }

    public void setCreatedByID(String createdByID) {
        this.createdByID = createdByID;
    }

    public String getCreatedByID() {
        return this.createdByID;
    }

    public void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public Date getCreatedDatetime() {
        return this.createdDatetime;
    }

    public void setModifiedByID(String modifiedByID) {
        this.modifiedByID = modifiedByID;
    }

    public String getModifiedByID() {
        return this.modifiedByID;
    }

    public void setModifiedDatetime(Date modifiedDatetime) {
        this.modifiedDatetime = modifiedDatetime;
    }

    public Date getModifiedDatetime() {
        return this.modifiedDatetime;
    }

    public void setCrc(Date crc) {
        this.crc = crc;
    }

    public Date getCrc() {
        return this.crc;
    }


    /**
     * Sets whether to create default forms when the configuration space
     * is being created for the first time.  This value has no effect if
     * this is an existing configuration space.
     * @param isCreateDefaultForms True means default forms will be 
     * created; this is the default.  False means default forms will not be
     * created; this should be specified when the configuration is being
     * copied from another - the forms belonging to the other will be added
     * to this configuration space (and are assumed to include the default
     * forms).
     */
    void setCreateDefaultForms(boolean isCreateDefaultForms) {
        this.isCreateDefaultForms = isCreateDefaultForms;
    }

    /**
     * Indicates whether the default forms will be created when this configuration
     * space is first created.
     * @return true if the default forms will be created; false otherwise
     */
    private boolean isCreateDefaultForms() {
        return this.isCreateDefaultForms;
    }

    /**
     * Sets the brandID for this configuration space.
     * Calling getBrand() after this method will return the brand for this ID.
     * @param brandID the brand id
     * @see #getBrand
     */
    protected void setBrandID(String brandID) {
        this.brandID = brandID;
        
        // If specified brand ID is null or it doesn't match currently loaded
        // brand's id then clear out currently loaded brand such that it will
        // be reloaded on next call to getBrand()
        if (brandID == null ||
            (this.brand != null && !brandID.equals(this.brand.getID())) ) {
            setBrand(new Brand());
        }
    }

    public String getBrandID() {
        return this.brandID;
    }

    void setPortfolioMembership(Collection portfolioMembership) {
        this.portfolioMembership = new ArrayList(portfolioMembership);
    }

    public void addPortfolioMemberID(String portfolioMemberID) {
        this.portfolioMembership.add(portfolioMemberID);
    }

    public Collection getPortfolioMembership() throws PnetException {
        if (this.portfolioMembership == null || this.portfolioMembership.size() == 0) {
            loadPortfolioMembership();
        }
        return this.portfolioMembership;
    }

    /**
     * Loads portfolio ids of portfolios to which this configuration space belongs
     * Adds to the portolio membership collection.
     * @throws PersistenceException if there is a problem loading the portfolio ids
     * @see #getPortfolioMembership()
     */
    private void loadPortfolioMembership() throws PersistenceException {
        StringBuffer query = new StringBuffer();

        query.append("select portfolio_id from pn_portfolio_has_space phs ");
        query.append("where phs.space_id = ? ");

        DBBean db = new DBBean();
        try {
            db.prepareStatement(query.toString());
            int index = 0;
            db.pstmt.setString(++index, getID());
            db.executePrepared();

            while (db.result.next()) {
                this.addPortfolioMemberID(db.result.getString("portfolio_id"));
            }

        } catch (SQLException sqle) {
            throw new PersistenceException("Configuration space load portfolio " +
                "membership operation failed.", sqle);

        } finally {
            db.release();

        }

    }


    /**
      * Indicates whether this space matches specified space id and is loaded.
      * @param space_id the space id to check
      * @return true if this space is loaded and has the same space id;
      * false otherwise
    public boolean isLoaded(String space_id) {
        if (space_id != null &&
            space_id.equals(getID()) &&
            super.m_is_loaded) {

            return true;
        }

        return false;
    }
           */



    /**
     * Returns the brand that this configuration space manages.  The brand
     * is only loaded once while this configuration space's brand id does not
     * change.
     * @return the brand
     * @throws PersistenceException if there is a problem loading the brand
     */
    public Brand getBrand() throws PersistenceException {
        // Load the brand if it is currently null OR it is not null but not loaded
        // AND this configuration space has a brand id
        if (brand == null || !brand.isLoaded()) {
            loadBrand();
        }
        return this.brand;
    }

    /**
     * Sets the configuration's brand to be the specified brand.
     * If the brand is null, the configuration's brand id is also set to null
     * @param brand the brand to set
     */
    void setBrand(Brand brand) {
        this.brand = brand;
    }

    /**
     * Loads the brand that belongs to this configuration space.
     */
    private void loadBrand() throws PersistenceException {
        // Load this space if it is not loaded and we have an id
        if (!this.isLoaded() && getID() != null) {
            load();
        }
        // Load the brand for this space's brand id
        if (getBrandID() != null) {
            brand.setID(getBrandID());
            brand.load();
        }
    }

    /**
     * Indicates whether this configuration space is a system configuration space.
     * A system configuration space will typically prohibit deletion etc.
     * @return true if this space is a system configuration; false otherwise
     */
    public boolean isSystemConfiguration() {
        boolean isSystemConfiguration = true;
        //
        // This flag should really be based on a column (e.g. "is_system_configuration")
        // and be populated during initial create in database script.
        // However, in the interests cutting corners, we'll base it on the
        // extremely poor logic of:
        //   If this configuration's brand is a default brand, then it is
        //   a system configuration.
        // This achieves our short-term goal of preventing deletion of the
        // default configuration
        try {
            Brand brand = getBrand();
            isSystemConfiguration = brand.isDefaultBrand();
        } catch (PersistenceException pe) {
        	Logger.getLogger(ConfigurationSpace.class).debug("ConfigurationSpace.isSystemConfiguration unable to get brand.");
            // Simply continue to return isSystemConfiguration
            // This ensures that even during an error, configuration has special treatment
        }

        return isSystemConfiguration;
    }


        /**
     * Store any existing relationships to the persistence store
     * 
     * @param db     Instantiated database access bean
     * @exception SQLException
     *                   If the database operation fails
     * @since Gecko
     */
    public void addSupportedUserDomain (UserDomain domain, DBBean db) throws SQLException {

        String qstrCreateConfigurationMap = "insert into pn_user_domain_supports_config (domain_id, configuration_id) " +
                                            "values (?, ?)";

        db.prepareStatement (qstrCreateConfigurationMap);

        db.pstmt.setString (1, domain.getID());
        db.pstmt.setString (2, this.getID());

        db.executePrepared();
    }



    /** clear all the properties of this object */
    public void clear() {
        super.clear();
	setType(CONFIGURATION_SPACE);
        setName(null);
        setDescription(null);
        setCreatedByID(null);
        setCreatedDatetime(null);
        setModifiedByID(null);
        setModifiedDatetime(null);
        setCrc(null);
        setRecordStatus(null);
        setLoaded(false);
        setPortfolioMembership(new ArrayList());
        setCreateDefaultForms(true);
	this.brand.clear();
	this.brandID = null;
    }


    /**
      * Load space from persistent store.
      */
    public void load() throws PersistenceException {
        StringBuffer query = new StringBuffer();

        query.append("select configuration_name, configuration_desc, ");
        query.append("created_by_id, created_datetime, modified_by_id, modified_datetime, ");
        query.append("crc, record_status, brand_id ");
        query.append("from pn_configuration_space c ");
        query.append("where c.configuration_id = ? ");

        DBBean db = new DBBean();
        try {
            db.prepareStatement(query.toString());
            db.pstmt.setString(1, getID());
            db.executePrepared();

            if (db.result.next()) {
                setName(db.result.getString("configuration_name"));
                setDescription(db.result.getString("configuration_desc"));
                setCreatedByID(db.result.getString("created_by_id"));
                setCreatedDatetime(db.result.getTimestamp("created_datetime"));
                setModifiedByID(db.result.getString("modified_by_id"));
                setModifiedDatetime(db.result.getTimestamp("modified_datetime"));
                setCrc(db.result.getTimestamp("crc"));
                setRecordStatus(db.result.getString("record_status"));
                setBrandID(db.result.getString("brand_id"));
                setLoaded(true);

            } else {
            	Logger.getLogger(ConfigurationSpace.class).error("ConfigurationSpace.load() failed to load row for id " + getID());
                throw new PersistenceException ("Configuration space load operation failed -- No data found");

            }

        } catch (SQLException sqle) {
        	Logger.getLogger(ConfigurationSpace.class).error("ConfigurationSpace.load() threw an SQL exception: " + sqle);
            throw new PersistenceException ("Configuration space load " +
                "operation failed.", sqle);

        } finally {
            db.release();

        } //end try

    }

    public void store() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            if (isLoaded()) {
                modifyConfiguration(db);
            } else {
                createConfiguration(db);
            }
        } finally {
            db.release();
        }
    }

    /**
      * Creates a new configutation space
      */
    private void createConfiguration(DBBean db) throws PersistenceException {
        int errorCode = -1;
        String configurationID = null;
        StringBuffer query = new StringBuffer();

        query.append("{call configuration.create_configuration(?, ?, ?, ?, ?, ?)}");

        try {
            db.setAutoCommit(false);

            // First create the brand.  This sets brandID.
            createBrand(db);

            int index = 0;
            int configurationIDIndex = 0;
            int errorCodeIndex = 0;
            db.prepareCall(query.toString());
            db.cstmt.setString(++index, getName());
            db.cstmt.setString(++index, getDescription());
            db.cstmt.setString(++index, getBrandID());
            db.cstmt.setString(++index, getUser().getID());
            db.cstmt.registerOutParameter((configurationIDIndex = ++index), java.sql.Types.INTEGER);
            db.cstmt.registerOutParameter((errorCodeIndex = ++index), java.sql.Types.INTEGER);
            db.executeCallable();
            
            configurationID = db.cstmt.getString(configurationIDIndex);
            setID (configurationID);
            errorCode = db.cstmt.getInt(errorCodeIndex);
            
            if (errorCode == DBErrorCodes.OPERATION_SUCCESSFUL) {
                // Now store portfolios that this configuration space belongs to
                storePortfolioMembership(db, configurationID);
                // Now add support for the domains selected by the user.  
                // NOTE WELL:  FOR GECKO, THIS WILL DEFAULT TO THE DEFAULT USER DOMAIN
                addSupportedUserDomain ( UserDomainManager.getDefaultUserDomain(), db );

                // finally, if all that worked, commit the transaction
                db.commit();

                try {
                    // Copy the default forms from the application space if required
                    // This is not required when the new configuration is being
                    // copied from another configuration
                    if (isCreateDefaultForms()) {
                        copyDefaultForms (db, getUser().getApplicationSpaceID(),configurationID );
                    }
                }

                catch (SQLException sqle) {
                	Logger.getLogger(ConfigurationSpace.class).debug("ConfigurationSpace.store() had no forms to copy");
                }

            } else {
                db.rollback();
            
            }
        
        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException sqle2) {
                // just release later
            }
            Logger.getLogger(ConfigurationSpace.class).error("ConfigurationSpace.store() threw an SQL exception: " + sqle);
            throw new PersistenceException ("Configuration space store operation failed.", sqle);

        } catch (net.project.security.SecurityException se) {
            try {
                db.rollback();
            } catch (SQLException sqle) {
                // just release later
            }
            Logger.getLogger(ConfigurationSpace.class).error("ConfigurationSpace.store() threw a SecurityException while storing the user domains: " + se);
            throw new PersistenceException ("Configuration space store operation failed because the user domains could not be stored.", se);


        } finally {
            db.release();

        } //end try

        // Handle (throw) any exceptions that were sucked up in PL/SQL
        try {
            DBExceptionFactory.getException("ConfigurationSpace.store()", errorCode);

        } catch (PnetException e) {
            throw new PersistenceException(e.getMessage(), e);
        }

        clear();
        setID(configurationID);
    }

    /**
      * Modifies a configutation space
      */
    private void modifyConfiguration(DBBean db) throws PersistenceException {
        int errorCode = -1;
        StringBuffer query = new StringBuffer();

        query.append("{call configuration.modify_configuration(?, ?, ?, ?, ?, ?, ?)}");

        try {
            db.setAutoCommit(false);

            // First modify the brand.
            modifyBrand(db);

            int index = 0;
            int errorCodeIndex = 0;
            db.prepareCall(query.toString());
            db.cstmt.setString(++index, getID());
            db.cstmt.setString(++index, getName());
            db.cstmt.setString(++index, getDescription());
            db.cstmt.setString(++index, getBrandID());
            db.cstmt.setString(++index, getUser().getID());
            db.cstmt.setTimestamp(++index, new Timestamp(getCrc().getTime()));
            db.cstmt.registerOutParameter((errorCodeIndex = ++index), java.sql.Types.INTEGER);
            db.executeCallable();
            
            errorCode = db.cstmt.getInt(errorCodeIndex);
            
            if (errorCode == DBErrorCodes.OPERATION_SUCCESSFUL) {
                db.commit();
            
            } else {
                db.rollback();
            
            }
        
        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException sqle2) {
                // just release later
            }
            Logger.getLogger(ConfigurationSpace.class).error("ConfigurationSpace.modifyConfiguration() threw an SQL exception: " + sqle);
            throw new PersistenceException ("Configuration space store " +
                "operation failed.", sqle);

        } finally {
            db.release();

        } //end try

        // Handle (throw) any exceptions that were sucked up in PL/SQL
        try {
            DBExceptionFactory.getException("ConfigurationSpace.store()", errorCode);

        } catch (PnetException e) {
            throw new PersistenceException(e.getMessage(), e);
        }

        String configurationID = getID();
        clear();
        setID(configurationID);
    }

    /**
      * Creates a brand based on this configuration.<br>
      * Sets brandID.
      */
    private void createBrand(DBBean db) throws SQLException {
        brand.store(db);
        setBrandID(brand.getID());
    }

    /**
      * Modifies the brand for this configuration.
      */
    private void modifyBrand(DBBean db) throws SQLException {
        brand.store(db);
    }


    protected void copyDefaultForms (String fromID, String toID) {
	DBBean db = new DBBean();

	try { 
	    copyDefaultForms (db, fromID, toID);
	}

	catch (SQLException sqle) {
		Logger.getLogger(ConfigurationSpace.class).debug("ConfigurationSpace.copyDefaultForms() threw an SQLException: " + sqle);
	}

	finally {
	    db.release();
	}
    }

    protected void copyDefaultForms (DBBean db, String fromID, String toID) throws SQLException {
        db.prepareCall ("begin FORMS.COPY_ALL (?,?,?,?); end;");

        db.cstmt.setString (1, fromID);
        db.cstmt.setString (2, toID);
        db.cstmt.setString (3, getUser().getID());
        db.cstmt.registerOutParameter (4, java.sql.Types.INTEGER);

        db.executeCallable();

        try {
            // Activate the copied forms (only activate ACTIVE forms in old space)
            net.project.form.FormMenu.activateAllActiveForSpace(toID, getUser());
        } catch (ExceptionList el) {            
            throw new SQLException ("ConfigurationSpace.coypDefaultForms() threw a PersistenceException: " + el);
        } catch (PersistenceException pe) {
            throw new SQLException ("ConfigurationSpace.coypDefaultForms() threw a PersistenceException: " + pe);
        }
    }


    /**
      * Store the portfolios to which this configuration space belongs.<br>
      * Note - this is for adding to a portfolio only... No checks are in place
      * for determining whether this space belongs to a portfolio already.
      * @param db the DBBean to use for the transaction
      * @param configurationID the id of the space to be added to the portfolios
      * @throws SQLException if there is a problem adding to a portfolio
      */
    private void storePortfolioMembership(DBBean db, String configurationID) throws SQLException {
        StringBuffer query = new StringBuffer();
        String portfolioID = null;
        Iterator it = null;
        boolean doExecute = false;
        int index = 0;

        query.append("insert into pn_portfolio_has_space ");
        query.append("(portfolio_id, space_id) ");
        query.append("values (?, ?) ");

        db.prepareStatement(query.toString());

        it = this.portfolioMembership.iterator();
        while (it.hasNext()) {
            portfolioID = (String) it.next();
            index = 0;

            db.pstmt.setString(++index, portfolioID);
            db.pstmt.setString(++index, configurationID);
            db.pstmt.addBatch();
            doExecute = true;
        }

        if (doExecute) {
            db.executePreparedBatch();
        }

    }

    /**
     * Removes this configuration.
     */
    public void remove() throws PersistenceException {
        
        // Quietly don't remove if this is a sytem configuration.
        if (isSystemConfiguration()) {
            return;
        }

        int errorCode = -1;
        StringBuffer query = new StringBuffer();

        query.append("{call configuration.remove_configuration(?, ?, ?, ?)}");

        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);

            int index = 0;
            int errorCodeIndex = 0;
            db.prepareCall(query.toString());
            db.cstmt.setString(++index, getID());
            db.cstmt.setString(++index, getUser().getID());
            db.cstmt.setTimestamp(++index, new Timestamp(getCrc().getTime()));
            db.cstmt.registerOutParameter((errorCodeIndex = ++index), java.sql.Types.INTEGER);
            db.executeCallable();
                        
            errorCode = db.cstmt.getInt(errorCodeIndex);
            
            if (errorCode == DBErrorCodes.OPERATION_SUCCESSFUL) {
                db.commit();
            
            } else {
                db.rollback();
            
            }
        
        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException sqle2) {
                // just release later
            }
            Logger.getLogger(ConfigurationSpace.class).error("ConfigurationSpace.removeConfiguration() threw an SQL exception: " + sqle);
            throw new PersistenceException ("Configuration space remove operation failed.", sqle);

        } finally {
            db.release();

        } //end try

        // Handle (throw) any exceptions that were sucked up in PL/SQL
        try {
            DBExceptionFactory.getException("ConfigurationSpace.remove()", errorCode);

        } catch (PnetException e) {
            throw new PersistenceException(e.getMessage(), e);
        }

        clear();
    }

    /**
      * XML Representation of this object.
      * @return the XML string including the xml version tag.
      */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }    

    /**
      * XML Representation of this object.
      * @return the XML string not including the xml version tag.
      */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<configurationSpace>\n");
        xml.append("<JSPRootURL>" + XMLUtils.escape(net.project.security.SessionManager.getJSPRootURL()) + "</JSPRootURL>");
        xml.append(getXMLElements());
        xml.append("</configurationSpace>\n");

        return xml.toString();
    }

    /**
      * Returns the basic xml elements of this object.
      * @return XML elements as string
      */
    private String getXMLElements() {
        StringBuffer xml = new StringBuffer();
        xml.append("<id>" + XMLUtils.escape(getID()) + "</id>");
        xml.append("<name>" + XMLUtils.escape(getName()) + "</name>");
        xml.append("<description>" + XMLUtils.escape(getDescription()) + "</description>");
        xml.append("<brandID>" + XMLUtils.escape(getBrandID()) + "</brandID>");
        xml.append("<createdByID>" + XMLUtils.escape(getCreatedByID()) + "</createdByID>");
        xml.append("<createdDatetime>" + XMLUtils.escape(Conversion.dateToString(getCreatedDatetime())) + "</createdDatetime>");
        xml.append("<modifiedByID>" + XMLUtils.escape(getModifiedByID()) + "</modifiedByID>");
        xml.append("<modifiedDatetime>" + XMLUtils.escape(Conversion.dateToString(getModifiedDatetime())) + "</modifiedDatetime>");
        xml.append("<crc>" + XMLUtils.escape("" + getCrc().getTime()) + "</crc>");
        xml.append("<recordStatus>" + XMLUtils.escape(getRecordStatus()) + "</recordStatus>");
        return xml.toString();
    }

}

