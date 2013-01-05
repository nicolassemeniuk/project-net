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
package net.project.workflow;

import java.io.Serializable;

import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.xml.XMLUtils;

public class HistoryAction
    implements Serializable, IJDBCPersistence, IXMLPersistence {

    /** Enter step history action */
    public static final HistoryAction ENTERED_STEP = new HistoryAction("100");
    public static final HistoryAction PERFORMED_TRANSITION = new HistoryAction("200");
    public static final HistoryAction SENT_NOTIFICATION = new HistoryAction("300");

    private String historyActionID = null;

    private HistoryAction(String historyActionID) {
        setID(historyActionID);
    }

    public String getID() {
        return this.historyActionID;
    }

    /**
     * Set the database ID of the object
     * @param id the id
     */
    public void setID(String id) {
        this.historyActionID = id;
    }

    /**
     * Load the history action
     * @throws net.project.persistence.PersistenceException if a problem occurs
     */
    public void load() throws net.project.persistence.PersistenceException {
        throw new PersistenceException("HistoryAction.load() functionality not implemented.");
    }

    /**
     * Store not supported
     * @throws PersistenceException if it is called
     */
    public void store() throws net.project.persistence.PersistenceException {
        throw new PersistenceException("HistoryAction.store(): Store capability not permitted");
    }

    /**
     * Remove not supported
     * @throws PersistenceException if it is called
     */
    public void remove() throws net.project.persistence.PersistenceException {
        throw new PersistenceException("HistoryAction.remove(): Remove capability not permitted");
    }

    /**
     Converts the object to XML representation
     This method returns the object as XML text.
     @return XML representation of the object
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
     Converts the object to XML node representation without the xml header tag.
     This method returns the object as XML text.
     @return XML node representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<history_action>\n");
        xml.append(getXMLElements());
        xml.append("</history_action>\n");
        return xml.toString();
    }

    /**
     * Return XML elements without rule_type tag
     * @return xml string
     */
    private String getXMLElements() {
        StringBuffer xml = new StringBuffer();
        xml.append("<history_action_id>" + XMLUtils.escape(getID()) + "</history_action_id>");
        return xml.toString();
    }

}
