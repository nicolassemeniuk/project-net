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
import java.sql.SQLException;

import net.project.base.RecordStatus;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.DBExceptionFactory;
import net.project.link.ILinkableObject;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.util.Conversion;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

public class Bookmark implements IContainerObject, ILinkableObject, IJDBCPersistence, IXMLPersistence, Serializable {

    /**
     * Object ID
     */
    protected String ID = null;
    /**
     * Bookmark Name
     */
    protected String name = null;
    /**
     * Bookmark Description
     */
    protected String description = null;
    /**
     * Resource URL of this bookmark
     */
    protected String URL = null;
    /**
     * Object Type
     */
    protected String type = null;
    /**
     * ID of the "type" of this document
     */
    protected String typeID = null;
    /**
     * ID of the parent container of this object
     */
    protected String containerID = null;
    /**
     * Name of the parent container of this object
     */
    protected String containerName = null;
    /**
     * Notes / comments about this bookmak
     */
    protected String notes = null;
    /**
     * "Owner" or "author" of this bookmark
     */
    protected String owner = null;
    /**
     * ID of the "Owner" or "author" of this bookmark
     */
    protected String ownerID = null;
    /**
     * ID of this bookmark's status
     */
    protected String statusID = null;
    /**
     * This bookmark's status
     */
    protected String status = null;
    /**
     * Date this bookmark was last modified
     */
    protected java.util.Date lastModified = null;
    /**
     * The userID of the person who last modified this bookmark
     */
    protected String modifiedByID = null;
    /**
     * Checksum
     */
    protected java.util.Date crc = null;
    /**
     * Record status of this Bookmark.
     */
    private RecordStatus recordStatus = null;
    /**
     * True if this bookmark is loaded
     */
    private boolean isLoaded = false;
    /**
     * User context (session) for the user
     */
    private net.project.security.User user = null;

    
    /* -------------------------------  Constructor(s)  ------------------------------- */

    /**
     * Creates an EMPTY bookmark object
     */
    public Bookmark() {

        setType(ContainerObjectType.BOOKMARK_OBJECT_TYPE);
    }


    /* -------------------------------  Getter(s) and Setter(s)  ------------------------------- */


    /**
     * Set the ID of the bookmark
     *
     * @param objectID ID of the bookmark
     */
    public void setID(String objectID) {
        this.ID = objectID;
    }


    /**
     * Return the ID of the bookmark
     *
     * @return String the ID of the bookmark
     */
    public String getID() {
        return this.ID;
    }


    /**
     * Set the type of the bookmark. Will be the type represented in
     * pn_object_type
     *
     * @param objectType Type of the bookmark
     */
    public void setType(String objectType) {
        this.type = objectType;
    }

    /**
     * Return the type of the bookmark
     *
     * @return String the type of the bookmark
     */
    public String getType() {
        return this.type;
    }


    /**
     * Returns true if the objectType of this object is equal to that which is
     * passed in
     *
     * @param objectType ObjectType of the object you are interested in
     * @return boolean True if equal, false if not
     */
    public boolean isTypeOf(String objectType) {

        return (objectType.equalsIgnoreCase(this.type)) ? true : false;
    }


    /**
     * Set the name of the bookmark. This name will be the label displayed on
     * the interface
     *
     * @param name name of the bookmark
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Return the name of the bookmark
     *
     * @return String the name of the bookmark
     */
    public String getName() {
        return this.name;
    }


    /**
     * Set the description of the bookmark. This description will be the label
     * displayed on the interface
     *
     * @param description description of the bookmark
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Return the description of the bookmark
     *
     * @return String the description of the bookmark
     */
    public String getDescription() {
        return this.description;
    }


    /**
     * Set the url of the bookmark. This url will be the label displayed on the
     * interface
     *
     * @param url URL of the bookmark
     */
    public void setURL(String url) {
        this.URL = url;
    }


    /**
     * Returns a well-formed URL for this bookmark
     *
     * @return String full URL for this bookmark
     */
    public String getURL() {
        return this.URL;
    }


    /**
     * Set the comments of the bookmark. This description will be the label
     * displayed on the interface
     *
     * @param notes of the bookmark
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }


    /**
     * Return the notes of the bookmark
     *
     * @return String the notes of the bookmark
     */
    public String getNotes() {
        return this.notes;
    }

    /**
     * Set the status of the bookmark. This description will be the label
     * displayed on the interface
     *
     * @param statusID status of the bookmark
     */
    public void setStatusID(String statusID) {
        this.statusID = statusID;
    }


    /**
     * Return the status of the bookmark
     *
     * @return String the status of the bookmark
     */
    public String getStatusID() {
        return this.statusID;
    }


    /**
     * Set the author/owner of the bookmark. This description will be the label
     * displayed on the interface
     *
     * @param ownerID owner of the bookmark
     */
    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }


    /**
     * Return the owner of the bookmark
     *
     * @return String the owner of the bookmark
     */
    public String getOwnerID() {
        return this.ownerID;
    }


    /**
     * Set's the parent ContainerID of this bookmark Effectively moves this
     * object to a new container
     *
     * @param containerID ID of the parent container of this object
     */
    public void setContainerID(String containerID) {
        this.containerID = containerID;
    }


    /**
     * Return the ID of the parent container of this bookmark
     *
     * @return String The parent containerID of this bookmark
     */
    public String getContainerID() {
        return this.containerID;
    }


    /**
     * Return the name of this object's parent container
     *
     * @return String the name of the container
     */
    public String getContainerName() {
        return this.containerName;
    }

    /**
     * Specifies the record status of this Bookmark.
     *
     * @param recordStatus the current record status
     */
    private void setRecordStatus(RecordStatus recordStatus) {
        this.recordStatus = recordStatus;
    }

    /**
     * Returns the record status of this bookmark.
     *
     * @return the record status
     */
    public RecordStatus getRecordStatus() {
        return this.recordStatus;
    }

    /**
     * Set's the user context into this object.
     *
     * @param user the user context.
     */
    public void setUser(User user) {
        this.user = user;
    }


    /**
     * Loads the properties for this bookmark given an object id
     *
     * @throws PersistenceException if the db operation goes awry
     */
    public void loadProperties() throws PersistenceException {

        // 02/06/2003 - TIm
        // record_status = 'A' is currently redundant since pn_bookmark_view
        // already limits the results to record_status = 'A'
        // However, pn_bookmark view is not correct in doing so, nor is this
        // method.
        // It leads to the unfortunate situation where load() called for an ID
        // of a bookmark that has just been deleted will fail; this is different
        // from all other load methods

        String qstrLoadBookmark = "select bookmark_id, name, description, url, status_id, owner_id, owner, " +
            "status, comments, modified_date, modified_by_id, container_id, record_status, crc " +
            " from pn_bookmark_view where record_status = 'A' and bookmark_id = " + this.ID;

        DBBean db = new DBBean();
        try {
            db.executeQuery(qstrLoadBookmark);

            if (db.result.next()) {

                this.name = db.result.getString("name");
                this.description = db.result.getString("description");
                this.URL = db.result.getString("url");
                this.statusID = db.result.getString("status_id");
                this.status = PropertyProvider.get(db.result.getString("status"));
                this.owner = db.result.getString("owner");
                this.ownerID = db.result.getString("owner_id");
                this.notes = db.result.getString("comments");
                this.lastModified = (java.util.Date) db.result.getTimestamp("modified_date");
                this.modifiedByID = db.result.getString("modified_by_id");
                this.containerID = db.result.getString("container_id");
                setRecordStatus(RecordStatus.findByID(db.result.getString("record_status")));
                this.crc = Conversion.toDate(db.result.getDate("crc"));
            }
        } catch (SQLException sqle) {
            throw new PersistenceException("net.project.Bookmark.loadProperties() " +
                "threw an SQLException: " + sqle, sqle);
        } finally {
            db.release();
        }
    }


    /**
     * Loads the bookmark from the persistent store
     *
     * @throws PersistenceException if the db operation goes awry
     */
    public void load() throws net.project.persistence.PersistenceException {

        // as of now, just implemented to call loadProperties()
        loadProperties();
        this.isLoaded = true;
    }


    /**
     * Returns true if this object is loaded
     *
     * @return boolean True if loaded / False if not.
     */
    public boolean isLoaded() {

        return this.isLoaded;
    }


    /**
     * Persists this object to the database
     *
     * @throws PersistenceException if the db store operation goes awry
     */
    public void store() throws net.project.persistence.PersistenceException {

        int errorCode = -1;

        DBBean db = new DBBean();
        try {
            db.setAutoCommit(true);

            db.prepareCall("begin  DOCUMENT.CREATE_BOOKMARK (?, ?, ?, ?, ?, ?, ?, ?, ?, ?); end;");

            db.cstmt.setString(1, this.name);
            db.cstmt.setString(2, this.description);
            db.cstmt.setString(3, this.URL);
            db.cstmt.setString(4, this.containerID);
            db.cstmt.setString(5, this.statusID);
            db.cstmt.setString(6, this.ownerID);
            db.cstmt.setString(7, this.notes);
            db.cstmt.setString(8, this.user.getID());
            db.cstmt.registerOutParameter(9, java.sql.Types.INTEGER);
            db.cstmt.registerOutParameter(10, java.sql.Types.VARCHAR);

            db.executeCallable();

            errorCode = db.cstmt.getInt(9);
            this.ID = db.cstmt.getString(10);

            // Handle (throw) any database exceptions
            DBExceptionFactory.getException("Bookmark.store()", errorCode);

            // now commit if no problems
            db.commit();

            // reloads the object with current state
            load();
        } catch (SQLException sqle) {
            throw new PersistenceException("net.project.Bookmark.store() threw an SQLException: " + sqle, sqle);
        } catch (net.project.base.PnetException pe) {
        	Logger.getLogger(Bookmark.class).error("net.project.Bookmark.store() threw a PnetException: " + pe);
            throw new PersistenceException("net.project.Bookmark.store() threw a PnetException: " + pe, pe);
        } finally {
            db.release();
        }
    }


    /**
     * Persists this object to the database
     *
     * @throws PersistenceException if the db store operation goes awry
     */
    protected void modify() throws net.project.persistence.PersistenceException {

        int errorCode = -1;

        DBBean db = new DBBean();
        try {

            db.setAutoCommit(true);

            db.prepareCall("begin  DOCUMENT.MODIFY_BOOKMARK (?, ?, ?, ?, ?, ?, ?, ?, ?, ?); end;");

            db.cstmt.setString(1, this.ID);
            db.cstmt.setString(2, this.name);
            db.cstmt.setString(3, this.description);
            db.cstmt.setString(4, this.URL);
            db.cstmt.setString(5, this.containerID);
            db.cstmt.setString(6, this.statusID);
            db.cstmt.setString(7, this.ownerID);
            db.cstmt.setString(8, this.notes);
            db.cstmt.setString(9, this.user.getID());
            db.cstmt.registerOutParameter(10, java.sql.Types.INTEGER);

            db.executeCallable();

            errorCode = db.cstmt.getInt(10);

            // Handle (throw) any database exceptions
            DBExceptionFactory.getException("Bookmark.modify()", errorCode);

            // now commit if no problems
            db.commit();

            // reloads the object with current state
            load();
        } catch (SQLException sqle) {
            throw new PersistenceException("net.project.Bookmark.modify() threw an SQLException: " + sqle, sqle);
        } catch (net.project.base.PnetException pe) {
        	Logger.getLogger(Bookmark.class).error("net.project.Bookmark.modify() threw a PnetException: " + pe);
            throw new PersistenceException("net.project.Bookmark.modify() threw a PnetException: " + pe, pe);
        } finally {
            db.release();
        }
    }


    // remove this object
    public void remove() throws net.project.persistence.PersistenceException {

        String qstrRemoveBookmark = "update pn_bookmark set record_status = 'D' where bookmark_id = " + this.ID;

        DBBean db = new DBBean();
        try {
            db.executeQuery(qstrRemoveBookmark);
        } catch (SQLException sqle) {
            throw new PersistenceException("net.project.Bookmark.remove() threw an SQLException: " + sqle, sqle);
        } finally {
            db.release();
        }
    }


    /**
     * Returns a well-formed XML representation of this object.
     *
     * @return String String representation of this object
     */
    public String getXML() {

        StringBuffer xml = new StringBuffer();

        xml.append(net.project.xml.IXMLTags.XML_VERSION_STRING);
        xml.append(getXMLBody());

        return xml.toString();
    }


    /**
     * Converts the object to XML node representation without the xml header
     * tag. This method returns the object as XML text.
     *
     * @return XML node representation
     */
    public String getXMLBody() {

        StringBuffer xml = new StringBuffer();

        xml.append("<bookmark>");
        xml.append(getXMLProperties());
        xml.append("</bookmark>");

        return xml.toString();
    }


    /**
     * Returns the xml properties of this object
     *
     * @return String String-based XMLrepresentation of this object
     */
    public String getXMLProperties() {

        StringBuffer xml = new StringBuffer();

        xml.append("<id>" + XMLUtils.escape(this.ID) + "</id>");
        xml.append("<name>" + XMLUtils.escape(this.name) + "</name>");
        xml.append("<description>" + XMLUtils.escape(this.description) + "</description>");
        xml.append("<url>" + XMLUtils.escape(this.URL) + "</url>");
        xml.append("<type>" + XMLUtils.escape(this.type) + "</type>");
        xml.append("<containerID>" + XMLUtils.escape(this.containerID) + "</containerID>");
        xml.append("<notes>" + XMLUtils.escape(this.notes) + "</notes>");
        xml.append("<owner>" + XMLUtils.escape(this.owner) + "</owner>");
        xml.append("<ownerID>" + XMLUtils.escape(this.ownerID) + "</ownerID>");
        xml.append("<status>" + XMLUtils.escape(this.status) + "</status>");
        xml.append("<statusID>" + XMLUtils.escape(this.statusID) + "</statusID>");
        xml.append("<lastModified>" +
            XMLUtils.formatISODateTime(this.lastModified) + "</lastModified>\n");
        xml.append("<modifiedByID>" + XMLUtils.escape(this.modifiedByID) + "</modifiedByID>");
        xml.append("<crc>" + XMLUtils.formatISODateTime(this.crc) + "</crc>");
        xml.append("<recordStatus>" + XMLUtils.escape((this.recordStatus == null ? "" : this.recordStatus.getID())) + "</recordStatus>");

        Path path = new Path(this);
        xml.append(path.getXMLBody());

        return xml.toString();
    }


    /**
     * Returns a well-formed XML representation of this object.
     *
     * @return String String representation of this object
     * @deprecated
     */
    public String getAppletXML(String spaceName) {

        return getXML();
    }


} // end class Bookmark

