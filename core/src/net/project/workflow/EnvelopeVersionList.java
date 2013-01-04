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
| EnvelopeVersionList is a simple subclass of ArrayList which provides XML
| facilities.                                                         
+----------------------------------------------------------------------*/
package net.project.workflow;

import java.sql.SQLException;
import java.util.ArrayList;

import net.project.base.property.PropertyProvider;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.NotSupportedException;
import net.project.persistence.PersistenceException;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
  * EnvelopList provides XML facilities for an ArrayList of
  * Envelopes
  */
public class EnvelopeVersionList extends ArrayList
        implements IXMLPersistence, java.io.Serializable,  IJDBCPersistence {

    private String envelopeID;
    private DBBean db = null;

    public EnvelopeVersionList() {
        this.db = new DBBean();
    }

    /**
      * Returns the individual Envelope elements
      * @return XML elements as string
      */
    private String getXMLElements() {
        StringBuffer xml = new StringBuffer();
        // Add each Envelope XML element to string
        for (int i=0; i < size(); i++) {
            xml.append( ((EnvelopeVersion)get(i)).getXMLBody() );
        }
        return xml.toString();
    }

    /*
        Implement IXMLPersistence
     */
    
    /**
      * Return the EnvelopeVersionList XML including the XML version tag
      * @return the XML string
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }    
    
    /**
      * Return the EnvelopeVersionList XML without the XML version tag
      * @return XML string
      */
    public java.lang.String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<envelope_version_list>\n");
        xml.append("<jsp_root_url>" + XMLUtils.escape(net.project.security.SessionManager.getJSPRootURL()) + "</jsp_root_url>");
        xml.append(getXMLElements());
        xml.append("</envelope_version_list>\n");
        return xml.toString();
    }

    String getID() {
        return this.envelopeID;
    }

    public void setID(String id) {
        this.envelopeID = id;
    }
    /**
      * Load the envelope verions list
      * @throws PersistenceException if there is a problem loading
      */
    public void load() throws PersistenceException {
        EnvelopeVersion envelopeVersion = null;
        StringBuffer queryBuff = new StringBuffer();

        queryBuff.append("select envelope_id, version_id, workflow_id, step_id, step_name, step_description, step_notes_clob, ");
        queryBuff.append("status_id, status_name, status_description, ");
        queryBuff.append("transition_id, transition_verb, transition_description, ");
        queryBuff.append("priority_id, priority_name, priority_description, ");
        queryBuff.append("comments_clob, created_by_id, created_by_full_name, created_datetime, crc, record_status ");
        queryBuff.append("from pn_envelope_version_view ");
        queryBuff.append("where envelope_id = " + getID() + " ");
        queryBuff.append("order by version_id asc ");

        try {
            db.executeQuery(queryBuff.toString());
            
            while (db.result.next()) {
                envelopeVersion = new EnvelopeVersion();
                envelopeVersion.setID(db.result.getString("version_id"));
                envelopeVersion.setEnvelopeID(db.result.getString("envelope_id"));
                envelopeVersion.setWorkflowID(db.result.getString("workflow_id"));
                envelopeVersion.setStepID(db.result.getString("step_id"));
                envelopeVersion.setStepName(db.result.getString("step_name"));
                envelopeVersion.setStepDescription(db.result.getString("step_description"));
                envelopeVersion.setStepNotes(ClobHelper.read(db.result.getClob("step_notes_clob")));
                envelopeVersion.setStatusID(db.result.getString("status_id"));
                envelopeVersion.setStatusName(PropertyProvider.get(db.result.getString("status_name")));
                envelopeVersion.setStatusDescription(db.result.getString("status_description"));
                envelopeVersion.setTransitionID(db.result.getString("transition_id"));
                envelopeVersion.setTransitionVerb(db.result.getString("transition_verb"));
                envelopeVersion.setTransitionDescription(db.result.getString("transition_description"));
                envelopeVersion.setPriorityID(db.result.getString("priority_id"));
                envelopeVersion.setPriorityName(PropertyProvider.get(db.result.getString("priority_name")));
                envelopeVersion.setPriorityDescription(db.result.getString("priority_description"));
                envelopeVersion.setComments(ClobHelper.read(db.result.getClob("comments_clob")));
                envelopeVersion.setCreatedBy(db.result.getString("created_by_id"));
                envelopeVersion.setCreatedByFullName(db.result.getString("created_by_full_name"));
                envelopeVersion.setCreatedDatetime(db.result.getTimestamp("created_datetime"));
                envelopeVersion.setCrc(db.result.getTimestamp("crc"));
                envelopeVersion.setRecordStatus(db.result.getString("record_status"));
                envelopeVersion.setLoaded(true);
                add(envelopeVersion);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(EnvelopeVersionList.class).error("EnvelopeVersionList.load() threw an SQL exception: " + sqle);
            throw new PersistenceException("Get envelope versions operation failed.", sqle);

        } finally {
            db.release();

        }
    }

    public void store() throws PersistenceException {
        throw new NotSupportedException("EnvelopeVersionList.store() operation not available.");
    }

    public void remove() throws PersistenceException {
        throw new NotSupportedException("EnvelopeVersionList.remove() operation not available.");
    }

}

