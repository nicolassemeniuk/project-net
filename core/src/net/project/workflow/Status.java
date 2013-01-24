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

 /*-------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-------------------------------------------------------------------*/
package net.project.workflow;

import java.io.Serializable;

import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.util.Conversion;
import net.project.xml.XMLUtils;

/**
 * A workflow status.  An envelope will have a particular status.
 */
public class Status implements IJDBCPersistence, IXMLPersistence, Serializable {

    private String statusID = null;
    private String name = null;
    private String description = null;
    private boolean isInactive = false;

    private String createdBy = null;
    private java.util.Date createdDatetime = null;
    private String modifiedBy = null;
    private java.util.Date modifiedDatetime = null;
    private java.util.Date crc = null;
    private String recordStatus = null;

    private boolean isLoaded = false;

    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for property description.
     * @return Value of property description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Setter for property description.
     * @param description New value of property description.
     */
    void setDescription(String description) {
        this.description = description;
    }

    /**
     * Return isInactive property.  A status with this set to true
     * implies that a workflow in that status should be considered inactive.
     * @return true if status implies inactive, false otherwise
     */
    public boolean isInactive() {
        return this.isInactive;
    }

    /**
     * Set inactive property.
     * @param isInactive value for inactive property
     */
    void setInactive(boolean isInactive) {
        this.isInactive = isInactive;
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

    public String getModifiedBy() {
        return this.modifiedBy;
    }

    void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public java.util.Date getModifiedDatetime() {
        return this.modifiedDatetime;
    }

    void setModifiedDatetime(java.util.Date modifiedDatetime) {
        this.modifiedDatetime = modifiedDatetime;
    }

    java.util.Date getCrc() {
        return this.crc;
    }

    void setCrc(java.util.Date crc) {
        this.crc = crc;
    }

    String getRecordStatus() {
        return this.recordStatus;
    }

    void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    /**
     * Getter for property statusID.
     * @return Value of property statusID.
     */
    public String getID() {
        return this.statusID;
    }

    /**
     * Clear all properties
     */
    public void clear() {
        setID(null);
        setName(null);
        setDescription(null);
        setInactive(false);
        setCreatedBy(null);
        setCreatedDatetime(null);
        setModifiedBy(null);
        setModifiedDatetime(null);
        setCrc(null);
        setRecordStatus(null);
        setLoaded(false);
    }

    /**
     * @return True if instance has been populated from database
     */
    public boolean isLoaded() {
        return this.isLoaded;
    }

    /**
     * Set loaded property.
     *
     * @param isLoaded of loaded property
     */
    void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    /*
        Implement IJDBCPersistence
     */

    public void setID(java.lang.String statusID) {
        this.statusID = statusID;
    }

    public void load() throws net.project.persistence.PersistenceException {
        throw new PersistenceException("Status.load() functionality not available.");
    }

    public void store() throws net.project.persistence.PersistenceException {
        throw new PersistenceException("Status.store() functionality not available.");
    }

    public void remove() throws net.project.persistence.PersistenceException {
        throw new PersistenceException("Status.remove() functionality not available.");
    }

    /*
        Implement IXMLPersistence
     */

    /**
     * Return XML representation of Status, includes XML version tag
     * @return XML string
     */
    public java.lang.String getXML() {
        return IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
     * Return XML representation of Status, excludes XML version tag
     * @return XML string
     */
    public java.lang.String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<status>");
        xml.append("<status_id>" + XMLUtils.escape(getID()) + "</status_id>");
        xml.append("<name>" + XMLUtils.escape(getName()) + "</name>");
        xml.append("<description>" + XMLUtils.escape(getDescription()) + "</description>");
        xml.append("<is_inactive>" + XMLUtils.escape((isInactive() ? "1" : "0")) + "</is_inactive>");
        xml.append("<created_by>" + XMLUtils.escape(getCreatedBy()) + "</created_by>");
        xml.append("<created_datetime>" + XMLUtils.escape(Conversion.dateToString(getCreatedDatetime())) + "</created_datetime>");
        xml.append("<modified_by>" + XMLUtils.escape(getModifiedBy()) + "</modified_by>");
        xml.append("<modified_datetime>" + XMLUtils.escape(Conversion.dateToString(getModifiedDatetime())) + "</modified_datetime>");
        xml.append("<crc>" + XMLUtils.escape(Conversion.dateToString(getCrc())) + "</crc>");
        xml.append("<record_status>" + XMLUtils.escape(getRecordStatus()) + "</record_status>");
        xml.append("</status>");
        return xml.toString();
    }

}

