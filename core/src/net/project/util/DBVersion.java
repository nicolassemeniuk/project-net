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

 package net.project.util;

import java.io.Serializable;
import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * Get Database Version.
 *
 * @author ChuckKirschman
 * @author Matthew Flower
 * @author Tim Morrow
 * @since V1
 */
public class DBVersion implements Serializable, net.project.persistence.IXMLPersistence {
    private DBBean db = new DBBean();
    protected String dbVersion;
    protected String clientDBVersion;
    protected String clientName;

    /**
     * Creates an empty DBVersion.
     *
     * @since 05/00
     */
    public DBVersion() {
        // Nothing to initialize
    }

    /**
     * Load the version information from the database.
     *
     * @since falcon
     */
    public void load() throws PersistenceException {
        try {
            //Call stored procedure to grab the database and client database versions
            db.prepareCall("{call product.get_database_version(?,?,?)}");
            db.cstmt.registerOutParameter(1, java.sql.Types.VARCHAR);
            db.cstmt.registerOutParameter(2, java.sql.Types.VARCHAR);
            db.cstmt.registerOutParameter(3, java.sql.Types.VARCHAR);

            db.executeCallable();
    
            //Grab the results and store them for later
            dbVersion = db.cstmt.getString(1);
            clientDBVersion = db.cstmt.getString(2);
            clientName = db.cstmt.getString(3);
        } catch (SQLException sqle) {
        	Logger.getLogger(DBVersion.class).info("DBVersion.load() threw an unexpected SQL Exception: " + sqle);
            throw new PersistenceException("Unable to load version information from the database.", sqle);
        } finally {
            db.release();
        }
    }


    /**
     * Get database version
     *
     * @since 05/00
     */
    public String getDBVersion() {
        return dbVersion;
    }

    /**
     * Convenience method so we only have 1 import in JSP's.
     *
     * @since 05/00
     */
    public String getAppVersion()
    {
        return net.project.util.Version.getInstance().getAppVersion();
    }

    public String getProductVersionCodename()
    {
        return net.project.util.Version.getInstance().getProductVersionCodename();
    }
    
    public String getClientAppVersion()
    {
        return net.project.util.Version.getInstance().getClientAppVersion();
    }

    public String getAppBuildDate()
    {
       return net.project.util.Version.getInstance().getBuildDate();
    }

    public boolean isBuildDatePresent() {
        String buildDate = getAppBuildDate();
        return !((buildDate == null) || (buildDate.trim().equals("")));
    }

    public String getClientName() {
        return clientName;
    }

    public boolean isClientAppBuild() {
        String clientAppVersion = getClientAppVersion();
        return !((clientAppVersion == null) || (clientAppVersion.trim().equals("")));
    }

    public boolean isClientDBBuild() {
        return !((clientDBVersion == null) || (clientDBVersion.trim().equals("")));
    }

    public String getClientDBVersion() {
        return clientDBVersion;
    }


    /**
     * Returns the XML representation for this object, including the version
     * tag.
     *
     * @return the XML representation
     * @since gecko
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION +
            getXMLBody();
    }

    /**
     * Returns the XML representation of this object, excluding the version
     * tag.
     *
     * @return the XML representation
     * @since gecko
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<DBVersion>");

        xml.append(makeElement("DBVersion", getDBVersion()));
        xml.append(makeElement("AppVersion", getAppVersion()));
        xml.append(makeElement("ClientName", getClientName()));
        if (isClientAppBuild()) {
            xml.append(makeElement("ClientAppVersion", getClientAppVersion()));
        }
        if (isClientDBBuild()) {
            xml.append(makeElement("ClientDBVersion", getClientDBVersion()));
        }

        xml.append("</DBVersion>");

        return xml.toString();
    }


    /**
     * Creates an XML element from the name and content.
     *
     * @return the element in the form <code>&lt;<i>name</i>&gt;<i>content</i>&lt;/<i>name</i>&gt;
     * @since gecko
     */
    private String makeElement(String name, String content) {
        return "<" + name + ">" + net.project.xml.XMLUtils.escape(content) + "</" + name + ">";
    }

} //Version



