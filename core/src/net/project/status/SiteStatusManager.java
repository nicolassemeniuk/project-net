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
package net.project.status;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * Manages the Site Status
 */
public class SiteStatusManager implements IXMLPersistence, Serializable {
    private ArrayList collection = null;

    public SiteStatusManager() {
    }


    /**
     * Loads the Active Message about the Site Status from the database
     *
     * @throws PersistenceException thrown in case anything goes wrong
     */
    public void loadActive() throws PersistenceException {
        loadSiteStatus(true);
    }

    /**
     * Loads the all Messages about the Site Status from the database
     *
     * @throws PersistenceException thrown in case anything goes wrong
     */
    public void loadAll() throws PersistenceException {
        loadSiteStatus(false);
    }

    /**
     * Loads the Messages about the Site Status from the database based on the
     * parameter being sent
     *
     * @param loadActiveOnly The flag which indicates whether to load Active Message or
     * all Messages
     */
    private void loadSiteStatus(boolean loadActiveOnly) {

        ArrayList lCollection = new ArrayList();
        DBBean db = new DBBean();
        StringBuffer query = new StringBuffer();

        query.append("select MESSAGE_ID,MESSAGE,TITLE, ACTIVE_INDICATOR from STATUS_MESSAGES ");

        if (loadActiveOnly) {
            query.append("where ACTIVE_INDICATOR='A' ");
        }

        query.append(" order by TITLE desc, MESSAGE");

        try {

            db.prepareStatement(query.toString());
            db.executePrepared();

            while (db.result.next()) {

                StatusMessage statusMessage = new StatusMessage();
                statusMessage.setMessageText(db.result.getString("MESSAGE"));
                statusMessage.setMessageTitle(db.result.getString("TITLE"));
                statusMessage.setMessageStatus(db.result.getString("ACTIVE_INDICATOR"));
                statusMessage.setID(db.result.getString("MESSAGE_ID"));
                lCollection.add(statusMessage);

            } // end while

        } catch (SQLException sqle) {
        	Logger.getLogger(SiteStatusManager.class).debug("SiteStatusManager:loadSiteStatus() " + sqle);
        } finally {
            db.release();
        }

        this.collection = lCollection;

    } // end load()

    /**
     * Syncronizes the state of the object by storing it to the underlying JDBC
     * object.
     *
     * @throws PersistenceException Thrown to indicate a failure storing to the
     * database, a system-level error.
     */
    public void store() throws PersistenceException {
        // Not implemented 
    }

    /**
     * Removes the EJB object that is currently associated with the instance.
     *
     * @throws PersistenceException Thrown to indicate a failure storing to the
     * database, a system-level error.
     */
    public void remove() throws PersistenceException {
    }

    /**
     * Set the database ID of the object
     */
    public void setID(String id) {
    }

    /**
     * Converts the object to XML representation This method returns the object
     * as XML text.
     *
     * @return XML representation of the object
     */
    public String getXML() {

        StringBuffer xml = new StringBuffer();

        xml.append(IXMLPersistence.XML_VERSION);
        xml.append(getXMLBody());

        return xml.toString();

    } // end getXML()

    /**
     * Converts the object to XML node representation without the xml header
     * tag. This method returns the object as XML text.
     *
     * @return XML node representation
     */
    public String getXMLBody() {

        StringBuffer xml = new StringBuffer();

        xml.append("<SiteStatusManager>\n");

        xml.append("<StatusMessageCollection>\n");
        for (int i = 0; i < collection.size(); i++) {

            StatusMessage sm = (StatusMessage) collection.get(i);
            xml.append(sm.getXMLBody());
        }
        xml.append("</StatusMessageCollection>\n");
        xml.append("</SiteStatusManager>\n");

        return xml.toString();

    } // end getXMLBody()

} // end class SiteStatus

