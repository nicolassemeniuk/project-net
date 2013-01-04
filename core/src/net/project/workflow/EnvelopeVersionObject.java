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

import net.project.base.ObjectFactory;
import net.project.database.DBBean;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.NotSupportedException;
import net.project.persistence.PersistenceException;
import net.project.xml.XMLUtils;

/**
 * An EnvelopeVersionObject
 * This represents an object attached to a particular envelop version
 */
public class EnvelopeVersionObject
    implements IJDBCPersistence, IXMLPersistence, Serializable {

    /* Persistent properties */
    private String objectID = null;
    private String versionID = null;
    private String envelopeID = null;
    private String objectType = null;
    private String objectVersionID = null;
    private String objectPropertiesID = null;
    private String objectProperties = null;
    private java.util.Date crc = null;
    private String recordStatus = null;
    /* End of persistent properties */
    private boolean isLoaded = false;

    private DBBean db = null;

    /**
     * Creates new EnvelopeObject
     */
    public EnvelopeVersionObject() {
        db = new DBBean();
    }

    /**
     * Getter for property versionID.
     * @return Value of property versionID.
     */
    public String getVersionID() {
        return this.versionID;
    }

    /**
     * Setter for property versionID.
     * @param versionID New value of property versionID.
     */
    void setVersionID(String versionID) {
        this.versionID = versionID;
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
    void setEnvelopeID(String envelopeID) {
        this.envelopeID = envelopeID;
    }

    /**
     * Setter for property objectType.
     * @param objectType New value of property objectType.
     */
    void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    /**
     * Getter for property objectType.
     * @return Value of property objectType.
     */
    String getObjectType() {
        return this.objectType;
    }

    /**
     * Getter for property objectVersionID.
     * @return Value of property objectVersionID.
     */
    public String getObjectVersionID() {
        return this.objectVersionID;
    }

    /**
     * Setter for property objectVersionID.
     * @param objectVersionID New value of property objectVersionID.
     */
    void setObjectVersionID(String objectVersionID) {
        this.objectVersionID = objectVersionID;
    }

    /**
     * Getter for property objectPropertiesID.
     * @return Value of property objectPropertiesID.
     */
    public String getObjectPropertiesID() {
        return this.objectPropertiesID;
    }

    /**
     * Setter for property objectPropertiesID.
     * @param objectPropertiesID New value of property objectPropertiesID.
     */
    void setObjectPropertiesID(String objectPropertiesID) {
        this.objectPropertiesID = objectPropertiesID;
    }

    /**
     * Return the object properties for this EnvelopeVersionObject
     * They are loaded if necessary
     * @return the object properties as a string
     * @throws PersistenceException if there is a problem loading the object
     * properties
     */
    public String getObjectProperties() throws PersistenceException {
        if (this.objectProperties == null) {
            loadObjectProperties();
        }
        return this.objectProperties;
    }

    void setObjectProperties(String objectProperties) {
        this.objectProperties = objectProperties;
    }

    void setCrc(java.util.Date crc) {
        this.crc = crc;
    }

    void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
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
     * Return this object as an IWorkflowable object <br>
     * This will instantiate the current object id
     */
    public IWorkflowable getWorkflowObject() throws NotWorkflowableException, PersistenceException {
        IWorkflowable workflowObject = null;
        Object obj = null;
        ObjectFactory objectFactory = new ObjectFactory();

        obj = objectFactory.make(getID());
        if (obj == null) {
            throw new NotWorkflowableException("Problem locating object.");
        } else {
            if (obj instanceof IWorkflowable) {
                workflowObject = (IWorkflowable)obj;
                workflowObject.setID(getID());
                workflowObject.load();
            } else {
                throw new NotWorkflowableException("Object not workflowable but expected to be so.");
            }
        }

        return workflowObject;
    }

    /**
     * Clear all properties
     */
    public void clear() {
    }

    /**
     * Return the  id
     * @return the envelope version object id
     */
    public String getID() {
        return this.objectID;
    }

    /*
        Implementing IJDBCPersistence
     */
    public void setID(java.lang.String id) {
        this.objectID = id;
    }

    public void load() throws net.project.persistence.PersistenceException {
        throw new NotSupportedException("EnvelopeVersionObject.load() not supported");
    }

    /**
     * Loads the object properties from the appropriate clob object for this
     * envelope version object
     * @throws PersistenceException if there is a problem loading the data
     */
    private void loadObjectProperties() throws PersistenceException {
        try {
            db.setClobTableName(Envelope.OBJECT_PROPERTIES_CLOB_TABLE_NAME);
            /*
                Set the object properties by getting the clob for the current
                object properties id and grabbing its data
             */
            setObjectProperties(db.getClob(getObjectPropertiesID()).getData());
        } finally {
            // Release must be performed since db.getClob sets auto-commit
            db.release();
        }
    }

    public void store() throws net.project.persistence.PersistenceException {
        throw new NotSupportedException("EnvelopeVersionObject.store() not supported");
    }

    public void remove() throws net.project.persistence.PersistenceException {
        throw new NotSupportedException("EnvelopeVersionObject.remove() not supported");
    }

    /*
        Implement IXMLPersistence
     */
    public java.lang.String getXML() {
        return IXMLPersistence.XML_VERSION + getXMLBody();
    }

    public java.lang.String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        String objectProperties = null;
        /* Get the object properties */
        try {
            objectProperties = getObjectProperties();
        } catch (PersistenceException pe) {
            objectProperties = "";
        }
        xml.append("<envelope_version_object>\n");
        xml.append("<object_id>" + XMLUtils.escape(getID()) + "</object_id>");
        xml.append("<envelope_id>" + XMLUtils.escape(getEnvelopeID()) + "</envelope_id>");
        xml.append("<version_id>" + XMLUtils.escape(getVersionID()) + "</version_id>");
        xml.append("<object_type>" + XMLUtils.escape(getObjectType()) + "</object_type>");
        xml.append("<object_version_id>" + XMLUtils.escape(getObjectVersionID()) + "</object_version_id>");
        xml.append("<object_properties_id>" + XMLUtils.escape(getObjectPropertiesID()) + "</object_properties_id>");
        xml.append("<object_properties>" + objectProperties + "</object_properties>");
        xml.append("</envelope_version_object>");
        return xml.toString();
    }

}
