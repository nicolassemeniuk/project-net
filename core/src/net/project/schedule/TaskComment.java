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
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.schedule;

import java.sql.SQLException;
import java.util.Date;

import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.xml.XMLUtils;

/**
 * A Task comment is a comment associated with a task baseline.
 * It is typically write-one, read many times.
 */
public class TaskComment implements java.io.Serializable {

    /** ID of Task to which comment belongs */
    private String taskID = null;

    /** Date on which comment was created */
    private Date createdDatetime = null;

    /** Person who created task comment */
    private String createdByID = null;
    private String createdByDisplayName = null;

    /** Text of the comment */
    private String text = null;


    private boolean isLoaded = false;

    /**
     * Creates a new, empty TaskComment.
     */
    public TaskComment() {
        // Do nothing
    }

    /**
     * Sets the id of the task to which this comment belongs.
     * @param taskID the task id
     */
    protected void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    /**
     * Returns the id of the task to which this comment belongs.
     * @return the task id
     */
    private String getTaskID() {
        return this.taskID;
    }

    /**
     * Sets the datetime on which this comment was created.
     * @param createdDatetime the date and time
     */
    protected void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    /**
     * Returns the date and time on which this comment was created.
     * @return the datetime on which this comment was created
     */
    public Date getCreatedDatetime() {
        return this.createdDatetime;
    }

    /**
     * Sets the id of the person creating the comment.
     * @param createdByID the id of the person creating the comment
     */
    protected void setCreatedByID(String createdByID) {
        this.createdByID = createdByID;
    }

    /**
     * Returns the id of the person who created the comment.
     * @return the id
     */
    public String getCreatedByID() {
        return this.createdByID;
    }

    /**
     * Sets the display name of the person who created the comment.
     * This is only used for display purposes when the TaskComment has been
     * loaded from the database.
     * @param createdByDisplayName display name
     */
    protected void setCreatedByDisplayName(String createdByDisplayName) {
        this.createdByDisplayName = createdByDisplayName;
    }

    /**
     * Returns the display name of the person who created the comment.
     * @return the display name
     */
    public String getCreatedByDisplayName() {
        return this.createdByDisplayName;
    }

    /**
     * Sets the text of the comment.
     * @param text the text of the comment, limited to 4,000 characters.
     */
    protected void setText(String text) {
        this.text = text;
    }

    /**
     * Returns the text of the comment.
     * @return the text of the comment
     */
    public String getText() {
        return this.text;
    }

    /**
     * Clears the properties of this comment.
     */
    public void clear() {
        setTaskID(null);
        setCreatedByID(null);
        setCreatedByDisplayName(null);
        setCreatedDatetime(null);
        setText(null);
        setLoaded(false);
    }

    /**
     * Sets whether this comment is loaded or not.
     * This should be set to true if all other properties have been loaded.
     * This is normally set to false only when a {@link #clear} is performed.
     */
    protected void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    /**
     * Indicates whether this comment is loaded or not.
     * @return true if the comment is loaded; false otherwise
     */
    public boolean isLoaded() {
        return this.isLoaded;
    }


    protected void store() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            store(db);
            db.commit();
        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException e) {
                //Throw original exception
            }
            throw new PersistenceException("Task Comment store operation failed: " + sqle, sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Stores a task comment.
     * @throws PersistenceException if there is a problem storing the task comment
     */
    protected void store(DBBean db) throws PersistenceException {

        StringBuffer query = new StringBuffer();
        query.append("{call schedule.add_comment(?,?,?,?, ?)}");

        try {
            int index = 0;
            int textClobIndex = 0;

            db.prepareCall(query.toString());
            db.cstmt.setString(++index, getTaskID());
            db.cstmt.setString(++index, getCreatedByID());
            db.cstmt.setTimestamp(++index, new java.sql.Timestamp(getCreatedDatetime().getTime()));
            db.cstmt.setInt(++index, (this.text == null ? 1 : 0));
            db.cstmt.registerOutParameter((textClobIndex = ++index), java.sql.Types.CLOB);
            db.executeCallable();

            if (this.text != null) {
                // We only have a clob locater if the text was not null
                // Write the text to the clob
                ClobHelper.write(db.cstmt.getClob(textClobIndex), this.text);
            }

        } catch (SQLException sqle) {
            throw new PersistenceException("Task Comment store operation failed: " + sqle, sqle);
        }
    }


    //
    // Implementing IXMLPersistence
    //

    /**
     * Returns the TaskComment as XML, including the XML version tag.
     * @return the XML
     */
    public String getXML() {
        return getXMLBody() + net.project.persistence.IXMLPersistence.XML_VERSION;
    }

    /**
     * Returns the TaskComment as XML.
     * @return the XML
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<TaskComment>");
        xml.append(getXMLElements());
        xml.append("</TaskComment>");
        return xml.toString();
    }

    private String getXMLElements() {
        StringBuffer xml = new StringBuffer();
        xml.append("<TaskID>" + XMLUtils.escape(getTaskID()) + "</TaskID>");
        xml.append("<CreatedDatetime>" + XMLUtils.formatISODateTime(getCreatedDatetime()) + "</CreatedDatetime>");
        xml.append("<CreatedByID>" + XMLUtils.escape(getCreatedByID()) + "</CreatedByID>");
        xml.append("<CreatedByDisplayName>" + XMLUtils.escape(getCreatedByDisplayName()) + "</CreatedByDisplayName>");
        xml.append("<Text>" + XMLUtils.escape(getText()) + "</Text>");
        return xml.toString();
    }

    //
    // End IXMLPersistence
    //

}
