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
import java.util.ArrayList;
import java.util.Iterator;

import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
  * Portfolio of Configuration Spaces.  This is a collection of configuration
  * spaces.
  */
public class ConfigurationPortfolio extends ArrayList implements Serializable, IXMLPersistence {

    /** Portfolio ID */
    private String portfolioID = null;
    private boolean isLoaded = false;

    /**
      * Creates new ConfigurationPortfolio
      */
    public ConfigurationPortfolio() {
        super();
    }

    private void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    public boolean isLoaded() {
        return this.isLoaded;
    }

    /**
      * Clear all attributes
      */
    public void clear() {
        setID(null);
        clearContents();
    }

    /**
      * Clear the contents of this ConfigurationPortfolio.
      * This method does not clear the id or any other attributes used while
      * loading.  It does clear out all the configuration spaces from this collection.
      */
    public void clearContents() {
        super.clear();
        setLoaded(false);
    }

    /**
      * Indicates whether there is at least one configuration for portfolio.
      * If the portfolio is loaded, result is based on presence of loaded portfolio,
      * otherwise result is based on portfolios in database.  No load is performed.
      * @return true if there is at least one configuration for portfolio;
      * false otherwise.
      * @throws PersistenceException if there was a problem determining whether
      * the portfolio has a configuration.
      */
    public boolean hasConfiguration() throws PersistenceException {
        boolean hasConfiguration = false;

        if (isLoaded()) {
            hasConfiguration = (size() > 0);
        
        } else {
            StringBuffer query = new StringBuffer();
            query.append("select count(*) as configuration_count ");
            query.append("from pn_configuration_space c, pn_portfolio_has_space phs ");
            query.append("where c.record_status = 'A' and phs.portfolio_id = ? and c.configuration_id = phs.space_id ");

            DBBean db = new DBBean();
            try {
                db.prepareStatement(query.toString());
                db.pstmt.setString(1, getID());
                db.executePrepared();

                if (db.result.next()) {
                    int count = db.result.getInt("configuration_count");
                    hasConfiguration = (count > 0);
                }

            } catch (SQLException sqle) {
            	Logger.getLogger(ConfigurationPortfolio.class).error("ConfigurationPortfolio.hasConfiguration() " +
                    "threw an SQL exception: " + sqle);
                throw new PersistenceException ("Configuration portfolio load " +
                    "operation failed.", sqle);

            } finally {
                db.release();

            } //end try
        }

        return hasConfiguration;
    }

    /**
      * Set the portfolio id for this ConfigurationPortfolio
      * @param portfolioID the portfolio id
      */
    public void setID(String portfolioID) {
        this.portfolioID = portfolioID;
    }

    /**
      * Returns the portfolio id for this ConfigurationPortfolio
      * @return the portfolio id
      */
    public String getID() {
        return this.portfolioID;
    }

    /**
      * Load this configuration portfolio.
      */
    public void load() throws PersistenceException {
        ConfigurationSpace configSpace = null;
        StringBuffer query = new StringBuffer();
        
        clearContents();        

        query.append("select configuration_id, configuration_name, configuration_desc, brand_id, ");
        query.append("created_by_id, created_datetime, modified_by_id, modified_datetime, ");
        query.append("crc, record_status ");
        query.append("from pn_configuration_space c, pn_portfolio_has_space phs ");
        query.append("where c.record_status = 'A' and phs.portfolio_id = ? and c.configuration_id = phs.space_id ");
        query.append(getLoadOrderBy());

        DBBean db = new DBBean();
        try {
            db.prepareStatement(query.toString());
            db.pstmt.setString(1, getID());
            db.executePrepared();

            while (db.result.next()) {
                configSpace = new ConfigurationSpace(db.result.getString("configuration_id"));
                configSpace.setName(db.result.getString("configuration_name"));
                configSpace.setDescription(db.result.getString("configuration_desc"));
                configSpace.setBrandID(db.result.getString("brand_id"));
                configSpace.setCreatedByID(db.result.getString("created_by_id"));
                configSpace.setCreatedDatetime(db.result.getTimestamp("created_datetime"));
                configSpace.setModifiedByID(db.result.getString("modified_by_id"));
                configSpace.setModifiedDatetime(db.result.getTimestamp("modified_datetime"));
                configSpace.setCrc(db.result.getTimestamp("crc"));
                configSpace.setRecordStatus(db.result.getString("record_status"));
                configSpace.setLoaded(true);
                add(configSpace);
            }

            setLoaded(true);

        } catch (SQLException sqle) {
        	Logger.getLogger(ConfigurationPortfolio.class).error("ConfigurationPortfolio.load() threw an " +
                "SQL exception: " + sqle);
            throw new PersistenceException ("Configuration portfolio load " +
                "operation failed.", sqle);

        } finally {
            db.release();

        } //end try

    }

    /**
      * Construct an order by clause for load query.
      */
    private String getLoadOrderBy() {
        StringBuffer order = new StringBuffer();
        
        order.append("order by ");
        order.append("configuration_name asc ");

        return order.toString();
    }

    public void store() throws PersistenceException {
        throw new PersistenceException("ConfigurationPortfolio store operation not implemented.");
    }

    public void remove() throws PersistenceException {
        throw new PersistenceException("ConfigurationPortfolio remove operation not implemented.");
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
        Iterator it = null;
        ConfigurationSpace configSpace = null;

        xml.append("<configurationPortfolio>\n");
        xml.append("<JSPRootURL>" + XMLUtils.escape(net.project.security.SessionManager.getJSPRootURL()) + "</JSPRootURL>");

        it = iterator();
        while (it.hasNext()) {
            configSpace = (ConfigurationSpace) it.next();
            xml.append(configSpace.getXMLBody());
        }
        xml.append("</configurationPortfolio>\n");

        return xml.toString();
    }

}

