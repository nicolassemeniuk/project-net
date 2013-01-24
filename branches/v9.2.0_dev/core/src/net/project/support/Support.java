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

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.support;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;

import net.project.database.DBBean;
import net.project.notification.NotificationSchedulerStatus;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.xml.XMLUtils;

/**
  * Provides appliaction information for support purposes.
  */
public class Support implements java.io.Serializable, IXMLPersistence {

    /** Indicates whether database is responding or not. */
    private boolean isAlive = false;

    /** The current request. */
    private HttpServletRequest request = null;

    /**
     * Creates an empty Support object.
     */
    public Support() {
        // No initialization
    }


    /**
     * Sets the current request so that information be be retrieved from it.
     * @param request the current request
     */
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }


    /**
     * Returns the XML for this Support object, including version tag.
     * @return the XML representation
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }


    /**
     * Returns the XML for this Support object, excluding the version tag.
     * @return the XML representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<ServerInfo>\n");
        
        xml.append(getCurrentDatetimeElement());
        xml.append(getDatabaseResponseElement());
        xml.append(getDatabaseVersionElement());
        xml.append(getRequestHeadersElement());
        xml.append(getProductVersionElement());
        xml.append(getConfigurationElement());
        xml.append(getSchedulerStatusElement());
        
        // Must be placed last to ensure it is set
        xml.append("<Alive>" + XMLUtils.escape( (isAlive ? "true" : "false") ) + "</Alive>");
        xml.append("</ServerInfo>");

        return xml.toString();
    }


    /**
     * Returns the current datetime xml element.
     * For example: <code>&lt;CurrentTime&gt;2001-11-20 10:19:50&lt;/CurrentTime&gt;</code>
     * @return the current date time element
     */
    private String getCurrentDatetimeElement() {
        StringBuffer xml = new StringBuffer();
        
        // Construct the current date and time
        GregorianCalendar now = new GregorianCalendar();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        xml.append("<CurrentTime>" + XMLUtils.escape(df.format(now.getTime())) + "</CurrentTime>");

        return xml.toString();
    }


    /**
     * Returns the database response element.
     * For example: <code><pre>
     * &lt;DatabaseResponse>
     *     &lt;User>PNET_USER&lt;/User> 
     *     &lt;GlobalName>TRUNKDEV.PROJECT.NET&lt;/GlobalName> 
     * &lt;/DatabaseResponse>
     * </pre></code>
     * Sets {@link #isAlive} if the database responds.
     * @return the database response XML ELement
     */
    private String getDatabaseResponseElement() {
        StringBuffer xml = new StringBuffer();
        StringBuffer query = new StringBuffer();
        DBBean db = new DBBean();
        
        xml.append("<DatabaseResponse>\n");

        query.append("select user, global_name ");
        query.append("from global_name ");

        try {
            db.executeQuery(query.toString());

            if (db.result.next()) {
                xml.append("<User>" + XMLUtils.escape(db.result.getString("user")) + "</User>\n");
                xml.append("<GlobalName>" + XMLUtils.escape(db.result.getString("global_name")) + "</GlobalName>\n");
                isAlive = true;
            
            } else {
                xml.append("<Error>" + XMLUtils.escape("Error contacting database: No user or global name found.") + "</Error>\n");
            }

        } catch (SQLException sqle) {
            xml.append("<Error>" + XMLUtils.escape("Error contacting database: " + sqle.getMessage()) + "</Error>\n");
        
        } finally {
            db.release();
        
        }

        xml.append("</DatabaseResponse>\n");

        return xml.toString();
    }


    /**
     * Returns the Database version information.
     * @return database version element
     * @see net.project.util.DBVersion#getXML
     */
    private String getDatabaseVersionElement() {
        StringBuffer xml = new StringBuffer();
        net.project.util.DBVersion dbVersion = new net.project.util.DBVersion();
        
        try {
            dbVersion.load();
            xml.append(dbVersion.getXMLBody());
        
        } catch (PersistenceException pe) {
            xml.append(makeElement("Error", "Error loading database version information"));
        }

        return xml.toString();
    }


    /**
     * Returns the XML representation of the request headers.
     * For example: <code><pre>
     * &lt;RequestHeaders>
     *     &lt;header name="REQUEST_METHOD">GET&lt;/header>
     * &lt;/RequestHeaders>
     * </pre></code>
     * @return the Request Headers XML element
     */
    private String getRequestHeadersElement() {
        String name;
        String value;
        StringBuffer xml = new StringBuffer();

        xml.append("<RequestHeaders>\n");
        
        Enumeration e = request.getHeaderNames();
        while (e.hasMoreElements()) {
            name = (String) e.nextElement();
            value = request.getHeader(name);

            xml.append("<header name=\"" + name + "\">" + XMLUtils.escape(value) + "</header>\n");
        }
        
        xml.append("</RequestHeaders>\n");
        
        return xml.toString();
    }


    /**
     * Returns the product version information from <code>PRODUCT_VERSION</code>
     * table.
     * @return the product version XML element
     */
    private String getProductVersionElement() {
        StringBuffer xml = new StringBuffer();
        StringBuffer query = new StringBuffer();
        DBBean db = new DBBean();

        query.append("select v.product, v.major_version, v.minor_version, v.sub_minor_version, v.build_version, v.timestamp, v.description ");
        query.append("from product_version v ");

        xml.append("<ProductVersion>\n");
        
        try {
            db.executeQuery(query.toString());

            if (db.result.next()) {
                xml.append("<Product>" + XMLUtils.escape(db.result.getString("product")) + "</Product>\n");
                xml.append("<MajorVersion>" + XMLUtils.escape(db.result.getString("major_version")) + "</MajorVersion>\n");
                xml.append("<MinorVersion>" + XMLUtils.escape(db.result.getString("minor_version")) + "</MinorVersion>\n");
                xml.append("<SubMinorVersion>" + XMLUtils.escape(db.result.getString("sub_minor_version")) + "</SubMinorVersion>\n");
                xml.append("<BuildVersion>" + XMLUtils.escape(db.result.getString("build_version")) + "</BuildVersion>\n");
                xml.append("<Timestamp>" + XMLUtils.escape(db.result.getString("timestamp")) + "</Timestamp>\n");
                xml.append("<Description>" + XMLUtils.escape(db.result.getString("description")) + "</Description>\n");
            } else {
                xml.append("<Error>" + XMLUtils.escape("No product version data found.") + "</Error>\n");
            }

        } catch (SQLException sqle) {
            xml.append("<Error>" + XMLUtils.escape("Error reading product version: " + sqle.getMessage()) + "</Error>\n");
        
        } finally {
            db.release();
        
        }

        xml.append("</ProductVersion>\n");

        return xml.toString();
    }



    /**
     * Returns the product configuration element containing configuration file
     * settings.
     * @return the configuration XML element
     */
    private String getConfigurationElement() {
        StringBuffer xml = new StringBuffer();
        
        xml.append("<Configuration>\n");
        xml.append("<Settings>\n");

        xml.append(makeElement("JSPRootURL", SessionManager.getJSPRootURL()));
        xml.append(makeElement("SupportEmail", SessionManager.getSupportEmail()));
        xml.append(makeElement("StartupPage", SessionManager.getStartupPage()));
        xml.append(makeElement("CharacterEncoding", SessionManager.getCharacterEncoding()));
        xml.append(makeElement("EmailCharacterEncoding", SessionManager.getEmailCharacterEncoding()));
        xml.append(makeElement("CustomXSLRootPath", SessionManager.getCustomXSLRootPath()));
        xml.append(makeElement("WebexSite", SessionManager.getWebexSite()));
        xml.append(makeElement("SiteURL", SessionManager.getSiteURL()));
        xml.append(makeElement("AppURL", SessionManager.getAppURL()));
        xml.append(makeElement("ApplicationName", SessionManager.getApplicationName()));
        xml.append("</Settings>\n");
        xml.append("</Configuration>\n");

        return xml.toString();
    }


    /**
     * Returns the notification scheduler status element.
     * @return the notification scheduler status xml
     * @see net.project.notification.NotificationSchedulerStatus#getXML
     */
    private String getSchedulerStatusElement() {
        StringBuffer xml = new StringBuffer();
        NotificationSchedulerStatus status = new NotificationSchedulerStatus();
        
        try {
            status.setSchedulerID(NotificationSchedulerStatus.DEFAULT_SCHEDULER_ID);
            status.load();
            xml.append(status.getXMLBody());
        
        } catch (PersistenceException pe) {
            xml.append("<Error>" + XMLUtils.escape("Error geting scheduler status: " + pe.getMessage()) + "</Error>\n");

        }

        return xml.toString();
    }


    /**
     * Creates an XML element from the name and content.
     * @return the element in the form <code>&lt;<i>name</i>&gt;<i>content</i>&lt;/<i>name</i>&gt;
     */
    private String makeElement(String name, String content) {
        return "<" + name + ">" + XMLUtils.escape(content) + "</" + name + ">";
    }

}
