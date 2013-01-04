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

 package net.project.admin;

import java.sql.SQLException;
import java.util.Date;

import net.project.configuration.ConfigurationPortfolio;
import net.project.database.DBBean;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.PersistenceException;
import net.project.space.ISpaceTypes;
import net.project.space.Space;
import net.project.space.SpaceTypes;
import net.project.util.Conversion;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
  * ApplicationSpace encapsulates application-wide functionality.
  */
public class ApplicationSpace 
extends Space 
implements java.io.Serializable, IJDBCPersistence {

    /** The default application space object id. */
    public static final String DEFAULT_APPLICATION_SPACE_ID = "4";

    /** The default application space object. */
    public static final ApplicationSpace DEFAULT_APPLICATION_SPACE = new ApplicationSpace(DEFAULT_APPLICATION_SPACE_ID);

    /*
        Persistent Attributes
     */
    private String createdByID = null;
    private Date createdDatetime = null;
    private Date crc = null;

    private String defaultPortfolioID = null;
    private ConfigurationPortfolio defaultConfigurationPortfolio = null;

    /**
      * Creates a new application space.
      */
    public ApplicationSpace() {
        super();
        setType(ISpaceTypes.APPLICATION_SPACE);
        this.spaceType = SpaceTypes.APPLICATION ;
    }

    /**
      * Creates a new application space for the specified id.
      * @param spaceID the space id of the application space.
      */
    public ApplicationSpace(String spaceID) {
        super(spaceID);
        setType(ISpaceTypes.APPLICATION_SPACE);
        this.spaceType = SpaceTypes.APPLICATION ;
    }

    protected void setCreatedByID(String createdByID) {
        this.createdByID = createdByID;
    }

    public String getCreatedByID() {
        return this.createdByID;
    }

    protected void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public Date getCreatedDatetime() {
        return this.createdDatetime;
    }

    protected void setCrc(Date crc) {
        this.crc = crc;
    }

    public Date getCrc() {
        return this.crc;
    }


    /**
      * Returns the default portfolio id for this application space.
      * @return the default portfolio id
      * @throws PersistenceException if default portfolio id is not found or
      * there is a problem finding it.
      */
    public String getDefaultPortfolioID() throws PersistenceException {
        if (this.defaultPortfolioID == null) {
            loadDefaultPortfolioID();
        }
        return this.defaultPortfolioID;
    }

    /**
      * Returns the default configuraiton portfolio for this application space.
      * It is only loaded from persistent store if not already done so.
      * @throws PersistenceException if there is a problem loading.
      */
    public ConfigurationPortfolio getDefaultConfigurationPortfolio() throws PersistenceException {
        return getDefaultConfigurationPortfolio(false);
    }

    /**
      * Returns the default configuraiton portfolio for this application space.
      * @param doReload true means the portfolio will always be reloaded from
      * persistence store; false means it will only be loaded if not already loaded.
      * @throws PersistenceException if there is a problem loading.
      */
    public ConfigurationPortfolio getDefaultConfigurationPortfolio(boolean doReload)
            throws PersistenceException {

        if (doReload || this.defaultConfigurationPortfolio == null) {
            loadDefaultConfigurationPortfolio();
        }
        
        return this.defaultConfigurationPortfolio;
    }

    private void loadDefaultConfigurationPortfolio() throws PersistenceException {
        String portfolioID = null;
        ConfigurationPortfolio portfolio = null;

        portfolioID = getDefaultPortfolioID();
        portfolio = new ConfigurationPortfolio();
        portfolio.setID(portfolioID);
        portfolio.load();

        this.defaultConfigurationPortfolio = portfolio;
    }

    /**
      * Loads the default portfolioID.<br>
      * this.defauktPortfolioID is set.
      */
    private void loadDefaultPortfolioID() throws PersistenceException {
        StringBuffer query = new StringBuffer();
        String portfolioID = null;

        query.append("select portfolio_id ");
        query.append("from pn_space_has_portfolio shp ");
        query.append("where shp.space_id = ? ");
        query.append("and shp.is_default = 1 ");

        DBBean db = new DBBean();
        try {
            db.prepareStatement(query.toString());
            db.pstmt.setString(1, getID());
            db.executePrepared();

            if (db.result.next()) {
                portfolioID = db.result.getString("portfolio_id");
            
            } else {
            	Logger.getLogger(ApplicationSpace.class).error("ApplicationSpace.getDefaultPortfolioID() failed to find default portfolio for space id " + getID());
                throw new PersistenceException ("No portfolio found for space ID " + getID());

            }

        } catch (SQLException sqle) {
        	Logger.getLogger(ApplicationSpace.class).error("ApplicationSpace.getDefaultPortfolioID() threw an SQL exception: " + sqle);
            throw new PersistenceException ("Application space load operation failed.", sqle);

        } finally {
            db.release();

        } //end try

        this.defaultPortfolioID = portfolioID;
    }

    /**
      * Load this application space from persistent store
      * @throws PersistenceException if there is a problem loading
      */
    public void load() throws PersistenceException {
        StringBuffer query = new StringBuffer();

        query.append("select application_name, application_desc, created_by_id, created_datetime, crc, record_status ");
        query.append("from pn_application_space a ");
        query.append("where a.application_id = ? ");

        DBBean db = new DBBean();
        try {
            db.prepareStatement(query.toString());
            db.pstmt.setString(1, getID());
            db.executePrepared();

            if (db.result.next()) {
                setName(db.result.getString("application_name"));
                setDescription(db.result.getString("application_desc"));
                setCreatedByID(db.result.getString("created_by_id"));
                setCreatedDatetime(db.result.getTimestamp("created_datetime"));
                setCrc(db.result.getTimestamp("crc"));
                setRecordStatus(db.result.getString("record_status"));
                setLoaded(true);

            } else {
            	Logger.getLogger(ApplicationSpace.class).error("ApplicationSpace.load() failed to load row for id " + getID());
                throw new PersistenceException ("No data found for application space with ID " + getID());

            }

        } catch (SQLException sqle) {
        	Logger.getLogger(ApplicationSpace.class).error("ApplicationSpace.load() threw an SQL exception: " + sqle);
            throw new PersistenceException ("Application space load operation failed.", sqle);

        } finally {
            db.release();

        } //end try

    }

    /** store not supported for this class */
    public void store() throws PersistenceException {
        throw new PersistenceException("ApplicationSpace store operation not supported.");
    }


    /** remove not supported for this class */
    public void remove() throws PersistenceException {
        throw new PersistenceException("ApplicationSpace remove operation not supported.");
    }

    /**
      * XML Representation of this object.
      * @return the XML string including the xml version tag.
      */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION +
                "<JSPRootURL>" + XMLUtils.escape(net.project.security.SessionManager.getJSPRootURL()) + "</JSPRootURL>" +
                getXMLBody();
    }    

    /**
      * XML Representation of this object.
      * @return the XML string not including the xml version tag.
      */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<applicationSpace>\n");
        xml.append(getXMLElements());
        xml.append("</applicationSpace>\n");

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
        xml.append("<createdByID>" + XMLUtils.escape(getCreatedByID()) + "</createdByID>");
        xml.append("<createdDatetime>" + XMLUtils.escape(Conversion.dateToString(getCreatedDatetime())) + "</createdDatetime>");
        xml.append("<crc>" + XMLUtils.escape("" + getCrc().getTime()) + "</crc>");
        xml.append("<recordStatus>" + XMLUtils.escape(getRecordStatus()) + "</recordStatus>");

        return xml.toString();
    }

}
