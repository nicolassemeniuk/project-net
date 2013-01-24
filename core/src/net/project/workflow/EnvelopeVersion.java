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
|    EnvelopeVersion.java
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.workflow;

import java.io.Serializable;
import java.sql.SQLException;

import net.project.base.property.PropertyProvider;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.util.Conversion;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * An EnvelopeVersion
 */
public class EnvelopeVersion
    implements IJDBCPersistence, IXMLPersistence, Serializable {

    /* Persistent properties */
    private String versionID = null;
    private String envelopeID = null;
    private String stepID = null;
    private String statusID = null;
    private String workflowID = null;
    private String transitionID = null;
    private String priorityID = null;
    private String comments = null;
    private String createdBy;
    private java.util.Date createdDatetime;
    private java.util.Date crc = null;
    private String recordStatus = null;
    /* End of persistent properties */

    /* Denormalized properties */
    private String stepName = null;
    private String stepDescription = null;
    private String stepNotes = null;
    private String statusName = null;
    private String statusDescription = null;
    private String transitionVerb = null;
    private String transitionDescription = null;
    private String priorityName = null;
    private String priorityDescription = null;
    private String createdByFullName = null;

    /** objects for this version */
    private EnvelopeVersionObjectList objectList = null;

    private boolean isLoaded = false;
    private User user = null;
    private DBBean db = null;

    /**
     * Creates new EnvelopeVersion
     */
    public EnvelopeVersion() {
        this.db = new DBBean();
    }

    /**
     * Getter for property envelopeID.
     * @return Value of property envelopeID.
     */
    public String getEnvelopeID() {
        return this.envelopeID;
    }

    /**
     * Setter for property envelopeID.
     * @param envelopeID New value of property envelopeID.
     */
    public void setEnvelopeID(String envelopeID) {
        this.envelopeID = envelopeID;
    }

    /**
     * Getter for property stepID.
     * @return Value of property stepID.
     */
    public String getStepID() {
        return this.stepID;
    }

    /**
     * Setter for property stepID.
     * @param stepID New value of property stepID.
     */
    public void setStepID(String stepID) {
        this.stepID = stepID;
    }

    /**
     * Getter for property statusID.
     * @return Value of property statusID.
     */
    public String getStatusID() {
        return this.statusID;
    }

    /**
     * Setter for property statusID.
     * @param statusID New value of property statusID.
     */
    public void setStatusID(String statusID) {
        this.statusID = statusID;
    }

    /**
     * Getter for property workflowID.
     * @return Value of property workflowID.
     */
    public String getWorkflowID() {
        return this.workflowID;
    }

    /**
     * Setter for property workflowID.
     * @param workflowID New value of property workflowID.
     */
    public void setWorkflowID(String workflowID) {
        this.workflowID = workflowID;
    }

    /**
     * Getter for property transitionID.
     * @return Value of property transitionID.
     */
    public String getTransitionID() {
        return this.transitionID;
    }

    /**
     * Setter for property transitionID.
     * @param transitionID New value of property transitionID.
     */
    public void setTransitionID(String transitionID) {
        this.transitionID = transitionID;
    }

    /**
     * Getter for property priorityID.
     * @return Value of property priorityID.
     */
    public String getPriorityID() {
        return this.priorityID;
    }

    /**
     * Setter for property priorityID.
     * @param priorityID New value of property priorityID.
     */
    public void setPriorityID(String priorityID) {
        this.priorityID = priorityID;
    }

    /**
     * Getter for property comments.
     * @return Value of property comments.
     */
    public String getComments() {
        return this.comments;
    }

    /**
     * Setter for property comments.
     * @param comments New value of property comments.
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public java.util.Date getCreatedDatetime() {
        return this.createdDatetime;
    }

    void setCreatedDatetime(java.util.Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    void setCrc(java.util.Date crc) {
        this.crc = crc;
    }

    void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getStepName() {
        return this.stepName;
    }

    void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getStepDescription() {
        return this.stepDescription;
    }

    void setStepDescription(String stepDescription) {
        this.stepDescription = stepDescription;
    }

    public String getStepNotes() {
        return this.stepNotes;
    }

    void setStepNotes(String stepNotes) {
        this.stepNotes = stepNotes;
    }

    public String getStatusName() {
        return this.statusName;
    }

    void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusDescription() {
        return this.statusDescription;
    }

    void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getTransitionVerb() {
        return this.transitionVerb;
    }

    void setTransitionVerb(String transitionVerb) {
        this.transitionVerb = transitionVerb;
    }

    public String getTransitionDescription() {
        return this.transitionDescription;
    }

    void setTransitionDescription(String transitionDescription) {
        this.transitionDescription = transitionDescription;
    }

    public String getPriorityName() {
        return this.priorityName;
    }

    void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    public String getPriorityDescription() {
        return this.priorityDescription;
    }

    void setPriorityDescription(String priorityDescription) {
        this.priorityDescription = priorityDescription;
    }

    public String getCreatedByFullName() {
        return this.createdByFullName;
    }

    void setCreatedByFullName(String createdByFullName) {
        this.createdByFullName = createdByFullName;
    }

    /**
     * Return object list for this envelope version, loading from database
     * if not already loaded
     * @return list of IWorkflowable objects
     */
    public EnvelopeVersionObjectList getObjectList() {
        if (this.objectList == null) {
            loadObjectList();
        }
        return this.objectList;
    }

    /**
     * Set the object list for this envelope version
     * @param objectList the object list
     */
    void setObjectList(EnvelopeVersionObjectList objectList) {
        this.objectList = objectList;
    }

//     /**
//       * Add an object to the envelope
//       * @param workflowObject the object to add
//       */
//     public void addObject(IWorkflowable workflowObject) {
//     }
//
//     /**
//       * Remove an object from the envelope
//       * @param workflowObject the object to remove
//       */
//     public void removeObject(IWorkflowable workflowObject) {
//     }
//
//     /**
//       * Return a list of objects in this envelope
//       * @return the list of objects
//       */
//     public ArrayList getObjects() {
//         return null;
//     }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return True if instance has been populated from database
     */
    public boolean isLoaded() {
        return this.isLoaded;
    }

    void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    /**
     * Clear all properties
     */
    public void clear() {
        setID(null);
        setEnvelopeID(null);
        setStepID(null);
        setStatusID(null);
        setWorkflowID(null);
        setTransitionID(null);
        setPriorityID(null);
        setComments(null);
        setCreatedBy(null);
        setCreatedDatetime(null);
        setCrc(null);
        setRecordStatus(null);
        setStepName(null);
        setStepDescription(null);
        setStepNotes(null);
        setStatusName(null);
        setStatusDescription(null);
        setTransitionVerb(null);
        setTransitionDescription(null);
        setPriorityName(null);
        setPriorityDescription(null);
        setCreatedByFullName(null);
        setLoaded(false);
    }

    /**
     * Return the envelope version id
     * @return the id of the envelope version
     */
    public String getID() {
        return this.versionID;
    }

    /*
        Implementing IJDBCPersistence
     */
    public void setID(java.lang.String versionID) {
        this.versionID = versionID;
    }

    public void load() throws net.project.persistence.PersistenceException {
        StringBuffer queryBuff = new StringBuffer();

        queryBuff.append("select envelope_id, version_id, workflow_id, step_id, step_name, step_description, step_notes_clob, ");
        queryBuff.append("status_id, status_name, status_description, ");
        queryBuff.append("transition_id, transition_verb, transition_description, ");
        queryBuff.append("priority_id, priority_name, priority_description, ");
        queryBuff.append("comments_clob, created_by_id, created_by_full_name, created_datetime, crc, record_status ");
        queryBuff.append("from pn_envelope_version_view ");
        queryBuff.append("where version_id = " + getID() + " ");

        try {
            // Execute select statement
            db.executeQuery(queryBuff.toString());
            if (db.result.next()) {
                setID(db.result.getString("version_id"));
                setEnvelopeID(db.result.getString("envelope_id"));
                setWorkflowID(db.result.getString("workflow_id"));
                setStepID(db.result.getString("step_id"));
                setStepName(db.result.getString("step_name"));
                setStepDescription(db.result.getString("step_description"));
                setStepNotes(ClobHelper.read(db.result.getClob("step_notes_clob")));
                setStatusID(db.result.getString("status_id"));
                setStatusName(PropertyProvider.get(db.result.getString("status_name")));
                setStatusDescription(db.result.getString("status_description"));
                setTransitionID(db.result.getString("transition_id"));
                setTransitionVerb(db.result.getString("transition_verb"));
                setTransitionDescription(db.result.getString("transition_description"));
                setPriorityID(db.result.getString("priority_id"));
                setPriorityName(PropertyProvider.get(db.result.getString("priority_name")));
                setPriorityDescription(db.result.getString("priority_description"));
                setComments(ClobHelper.read(db.result.getClob("comments_clob")));
                setCreatedBy(db.result.getString("created_by_id"));
                setCreatedByFullName(db.result.getString("created_by_full_name"));
                setCreatedDatetime(db.result.getTimestamp("created_datetime"));
                setCrc(db.result.getTimestamp("crc"));
                setRecordStatus(db.result.getString("record_status"));
                setLoaded(true);
            }

        } catch (SQLException sqle) {
            this.isLoaded = false;
            Logger.getLogger(EnvelopeVersion.class).error("EnvelopeVersion.load() threw an SQL exception: " + sqle);
            throw new PersistenceException("Envelope version load operation failed.", sqle);

        } finally {
            db.release();
        }

    }

    /**
     * Loads the object list for this envelope version
     */
    private void loadObjectList() {

        EnvelopeVersionObject evo = null;
        EnvelopeVersionObjectList objectList = new EnvelopeVersionObjectList();
        StringBuffer queryBuff = new StringBuffer();

        queryBuff.append("select object_id, version_id, envelope_id, ");
        queryBuff.append("object_properties_id, object_version_id, crc, record_status ");
        queryBuff.append("from pn_envelope_version_has_object ");
        queryBuff.append("where version_id = " + getID() + " ");
        queryBuff.append("and envelope_id = " + getEnvelopeID() + " ");

        try {
            db.executeQuery(queryBuff.toString());
            while (db.result.next()) {
                evo = new EnvelopeVersionObject();
                evo.setID(db.result.getString("object_id"));
                evo.setVersionID(db.result.getString("version_id"));
                evo.setEnvelopeID(db.result.getString("envelope_id"));
                evo.setObjectPropertiesID(db.result.getString("object_properties_id"));
                evo.setObjectVersionID(db.result.getString("object_version_id"));
                evo.setCrc(db.result.getTimestamp("crc"));
                evo.setRecordStatus(db.result.getString("record_status"));
                evo.setLoaded(true);
                objectList.add(evo);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(EnvelopeVersion.class).error("EnvelopeVersion.loadObjectList() threw an SQL exception: " + sqle);

        } finally {
            db.release();

        }
        setObjectList(objectList);
    }

    public void store() throws net.project.persistence.PersistenceException {
        throw new PersistenceException("EnvelopeVersion store functionality not available.");
    }

    public void remove() throws net.project.persistence.PersistenceException {
        throw new PersistenceException("EnvelopeVersion remove functionality not available.");
    }

    /*
        Implement IXMLPersistence
     */
    public java.lang.String getXML() {
        return IXMLPersistence.XML_VERSION + getXMLBody();
    }

    public java.lang.String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<envelope_version>");
        xml.append(getXMLElements());
        xml.append("</envelope_version>");
        return xml.toString();
    }

    String getXMLElements() {
        StringBuffer xml = new StringBuffer();
        xml.append("<version_id>" + quote(getID()) + "</version_id>");
        xml.append("<envelope_id>" + quote(getEnvelopeID()) + "</envelope_id>");
        xml.append("<step_id>" + quote(getStepID()) + "</step_id>");
        xml.append("<status_id>" + quote(getStatusID()) + "</status_id>");
        xml.append("<workflow_id>" + quote(getWorkflowID()) + "</workflow_id>");
        xml.append("<transition_id>" + quote(getTransitionID()) + "</transition_id>");
        xml.append("<priority_id>" + quote(getPriorityID()) + "</priority_id>");
        xml.append("<comments>" + quote(getComments()) + "</comments>");
        xml.append("<created_by>" + quote(getCreatedBy()) + "</created_by>");
        xml.append("<created_datetime>" + quote(Conversion.dateToString(getCreatedDatetime())) + "</created_datetime>");
        xml.append("<step_name>" + quote(getStepName()) + "</step_name>");
        xml.append("<step_description>" + quote(getStepDescription()) + "</step_description>");
        xml.append("<step_notes>" + quote(getStepNotes()) + "</step_notes>");
        xml.append("<status_name>" + quote(getStatusName()) + "</status_name>");
        xml.append("<status_description>" + quote(getStatusDescription()) + "</status_description>");
        xml.append("<transition_verb>" + quote(getTransitionVerb()) + "</transition_verb>");
        xml.append("<transition_description>" + quote(getTransitionDescription()) + "</transition_description>");
        xml.append("<priority_name>" + quote(getPriorityName()) + "</priority_name>");
        xml.append("<priority_description>" + quote(getPriorityDescription()) + "</priority_description>");
        xml.append("<created_by_full_name>" + quote(getCreatedByFullName()) + "</created_by_full_name>");
        return xml.toString();
    }

    /**
     * quotes string to HTML, turns null strings into empty strings
     * @param s the string
     * @return the quotes string
     */
    private String quote(String s) {
        return XMLUtils.escape(s);
    }

}
