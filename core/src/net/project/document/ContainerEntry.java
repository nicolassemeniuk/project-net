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

 package net.project.document;

import java.io.Serializable;
import java.util.Date;

import net.project.security.SessionManager;
import net.project.util.Conversion;
import net.project.xml.XMLUtils;

public class ContainerEntry implements IContainerEntry, Serializable {

    public String objectID = null;
    public String objectType = null;
    public String containerID = null;
    public String name = null;
    //sjmittal implemented only for trashcan view
    public String parentName = null;
    public String format = null;
    public String appIconURL = null;
    public String version = null;
    public boolean isRoot = false;
    public String isCko = null;
    public String ckoByID = null;
    public String ckoBy = null;
    public String status = null;
    public String author = null;

    /**
     * Sets the last modified Date
     *
     * @see #getLastModifiedDate
     * @see #setLastModifiedDate
     * @deprecated As of release 7.4.  Please use getLastModifiedDate() and
     *             setLastModifiedDate() instead.
     */
    public String lastModified = null;
    public String size = null;
    public String shortFileName = null;
    public String mimeType = null;
    public String hasLinks = null;
    public String hasDiscussions = null;
    public String hasWorkflows = null;
    public String url = null;
    public String lastModifiedBy = null;

    private java.util.Date lastModifiedDate = null;
    String description = null;
    String comments = null;

    public ContainerEntry() {
    }


    public boolean isTypeOf(String objectType) {

        boolean typeMatch = false;

        if (objectType.equals(ContainerObjectType.CONTAINER_OBJECT_TYPE)) {
            typeMatch = true;
        }

        return typeMatch;
    }

    public void setID(String objectID) {
        this.objectID = objectID;
    }

    public void setHasLinks(boolean hasLinks) {
        if (hasLinks) {
            this.hasLinks = "1";
        } else {
            this.hasLinks = "0";
        }
    }

    public String getHasLinks() {
        return this.hasLinks;
    }

    public void setType(String objectType) {
        this.objectType = objectType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIsRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    public boolean isRoot() {
        return this.isRoot;
    }

    public void setContainerID(String containerID) {
        this.containerID = containerID;
    }

    public String getID() {
        return this.objectID;
    }

    public String getType() {
        return this.objectType;
    }

    public String getContainerID() {
        return this.containerID;
    }

    public String getName() {
        return this.name;
    }


    /**
     * Returns the last modified Date for the container
     *
     * @return the last modified Date for the container
     */
    public Date getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    /**
     * Sets the last modified Date for the container
     *
     * @param date the last modified Date for the container
     */
    public void setLastModifiedDate(java.util.Date date) {
        this.lastModifiedDate = date;
    }

    public String getXML() {

        StringBuffer xml = new StringBuffer();
        String siteURL = SessionManager.getSiteURL();
        String tab = null;

        tab = "\t";
        xml.append(tab + "<entry type=\"" + this.getType() + "\">\n");

        tab = "\t\t";
        xml.append(tab + "<object_id>" + XMLUtils.escape(this.objectID) + "</object_id>\n");
        xml.append(tab + "<object_type>" + XMLUtils.escape(this.objectType) + "</object_type>\n");
        xml.append(tab + "<container_id>" + XMLUtils.escape(this.containerID) + "</container_id>\n");
        xml.append(tab + "<name>" + XMLUtils.escape(this.name) + "</name>\n");
        xml.append(tab + "<parent_name>" + XMLUtils.escape(this.parentName) + "</parent_name>\n");
        xml.append(tab + "<format>" + XMLUtils.escape(this.format) + "</format>\n");

        xml.append(tab + "<url>" + XMLUtils.escape(this.url) + "</url>\n");

        xml.append(tab + "<app_icon_url>");
        xml.append(XMLUtils.escape(this.appIconURL));
        xml.append("</app_icon_url>\n");

        xml.append(tab + "<version>" + XMLUtils.escape(this.version) + "</version>\n");
        xml.append(tab + "<cko_by>" + XMLUtils.escape(this.ckoBy) + "</cko_by>\n");
        xml.append(tab + "<cko_by_id>" + XMLUtils.escape(this.ckoByID) + "</cko_by_id>\n");
        xml.append(tab + "<is_root>" + XMLUtils.escape(Conversion.booleanToString(this.isRoot)) + "</is_root>\n");
        if (SessionManager.getUser() != null) {
            xml.append(tab + "<is_cko>" + XMLUtils.format(DocumentUtils.stringToBoolean(this.isCko)) + "</is_cko>\n");
            xml.append(tab + "<user_id>" + XMLUtils.escape(SessionManager.getUser().getID()) + "</user_id>\n");
            xml.append(tab + "<cko_image_str>" +
                DocumentUtils.getCkoImage(Conversion.toBoolean(this.isCko), this.ckoBy, this.ckoByID) + "</cko_image_str>\n");
        }
        xml.append(tab + "<status>" + XMLUtils.escape(this.status) + "</status>\n");
        xml.append(tab + "<author>" + XMLUtils.escape(this.author) + "</author>\n");

        if (this.lastModifiedDate != null) {
            xml.append(tab + "<last_modified>" + XMLUtils.formatISODateTime(this.lastModifiedDate) + "</last_modified>\n");
        } else {
            xml.append(tab + "<last_modified/>\n");
        }
        
        if (this.lastModifiedBy != null) {
            xml.append(tab + "<last_modified_by>" + XMLUtils.escape(this.lastModifiedBy) + "</last_modified_by>\n");
        } else {
            xml.append(tab + "<last_modified_by/>\n");
        }

        xml.append(tab + "<file_size>" + XMLUtils.escape(DocumentUtils.fileSizetoKBytes(this.size)) + "</file_size>\n");
        xml.append(tab + "<file_size_bytes>" + XMLUtils.escape(this.size) + "</file_size_bytes>\n");
        xml.append(tab + "<short_file_name>" + XMLUtils.escape(this.shortFileName) + "</short_file_name>\n");
        xml.append(tab + "<has_links>" + XMLUtils.escape(this.hasLinks) + "</has_links>\n");
        xml.append(tab + "<has_workflows>" + XMLUtils.escape(this.hasWorkflows) + "</has_workflows>\n");
        xml.append(tab + "<has_discussions>" + XMLUtils.escape(this.hasDiscussions) + "</has_discussions>\n");
        xml.append(tab + "<description>" + XMLUtils.escape(this.description) + "</description>\n");
        xml.append(tab + "<comments>" + XMLUtils.escape(this.comments) + "</comments>\n");

        tab = "\t";
        xml.append(tab + "</entry>\n");

        return xml.toString();

    } // getXML()


    // A reduced set of the XML for transmission to the applet
    public String getAppletXML(String spaceName) {

        StringBuffer xml = new StringBuffer();
        String tab = null;

        tab = "\t";
        xml.append(tab + "<" + this.getType() + ">\n");

        tab = "\t\t";
        // These are required
        xml.append(tab + "<object_id>" + XMLUtils.escape(this.objectID) + "</object_id>\n");
        xml.append(tab + "<container_id>" + XMLUtils.escape(this.containerID) + "</container_id>\n");
        // Name of root container is the space name
        if (this.isRoot && spaceName != null) {
            xml.append(tab + "<name>" + XMLUtils.escape(spaceName) + "</name>\n");
        } else {
            xml.append(tab + "<name>" + XMLUtils.escape(this.name) + "</name>\n");
        }

        // Send these only if they are available
        if (this.format != null) {
            xml.append(tab + "<format>" + XMLUtils.escape(this.format) + "</format>\n");
        }
        if (this.appIconURL != null) {
            xml.append(tab + "<app_icon_url>" + XMLUtils.escape(this.appIconURL) + "</app_icon_url>\n");
        }
        if (this.version != null) {
            xml.append(tab + "<version>" + XMLUtils.escape(this.version) + "</version>\n");
        }
        if (this.ckoBy != null) {
            xml.append(tab + "<cko_by>" + XMLUtils.escape(this.ckoBy) + "</cko_by>\n");
        }
        if (this.ckoByID != null) {
            xml.append(tab + "<cko_by_id>" + XMLUtils.escape(this.ckoByID) + "</cko_by_id>\n");
        }
        if (this.status != null) {
            xml.append(tab + "<status>" + XMLUtils.escape(this.status) + "</status>\n");
        }
        if (this.author != null) {
            xml.append(tab + "<author>" + XMLUtils.escape(this.author) + "</author>\n");
        }
        if (this.lastModified != null) {
            xml.append(tab + "<last_modified>" + XMLUtils.escape(this.lastModified) + "</last_modified>\n");
        }
        if (this.size != null) {
            xml.append(tab + "<file_size_bytes>" + XMLUtils.escape(this.size) + "</file_size_bytes>\n");
        }

        tab = "\t";
        xml.append(tab + "</" + this.getType() + ">\n\n");

        return xml.toString();

    } // getXML()


} // end ContainerEntry
