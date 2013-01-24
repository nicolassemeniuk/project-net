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
|    History.java
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.workflow;

import java.io.Serializable;
import java.sql.SQLException;

import net.project.base.DBErrorCodes;
import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.DBExceptionFactory;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.util.Conversion;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * History is attached to a specific envelope.
 *
 * @author  Tim Morrow
 */
public class History implements IJDBCPersistence, IXMLPersistence, Serializable {

    /* Persistent properties */
    private String historyID = null;
    private String envelopeID = null;
    private String historyActionID = null;
    private String actionByID = null;
    private java.util.Date actionDatetime = null;
    private String historyMessageID = null;
    private String historyMessage = null;
    private java.util.Date crc = null;
    private String recordStatus = null;
    /* End of persistent properties */

    private String envelopeName = null;
    private String envelopeDescription = null;
    private String actionByFullName = null;
    private String actionName = null;
    private String actionDescription = null;

    private boolean isLoaded = false;
    private User user = null;
    private DBBean db = null;

    private static final String HISTORY_CLOB_TABLE_NAME = "pn_envelope_history_clob";

    /**
     * Creates new History
     */
    public History() {
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
     * Getter for property historyActionID.
     * @return Value of property historyActionID.
     */
    public String getHistoryActionID() {
        return this.historyActionID;
    }

    /**
     * Setter for property historyActionID.
     * @param historyActionID New value of property historyActionID.
     */
    public void setHistoryActionID(String historyActionID) {
        this.historyActionID = historyActionID;
    }

    /**
     * Set history action
     * @param historyAction the historyAction to set
     */
    public void setHistoryAction(HistoryAction historyAction) {
        this.historyActionID = historyAction.getID();
    }

    /**
     * Getter for property actionByID.
     * @return Value of property actionByID.
     */
    public String getActionByID() {
        return this.actionByID;
    }

    /**
     * Setter for property actionByID.
     * @param actionByID New value of property actionByID.
     */
    public void setActionByID(String actionByID) {
        this.actionByID = actionByID;
    }

    /** Getter for property actionDatetime.
     * @return Value of property actionDatetime.
     */
    public java.util.Date getActionDatetime() {
        return this.actionDatetime;
    }

    /**
     * Setter for property actionDatetime.
     * @param actionDatetime New value of property actionDatetime.
     */
    public void setActionDatetime(java.util.Date actionDatetime) {
        this.actionDatetime = actionDatetime;
    }

    /**
     * Return the history message id for the history message associated with
     * this History object.
     * @return the history message id
     */
    public String getHistoryMessageID() {
        return this.historyMessageID;
    }

    /**
     * Set the history message id for the history message associated with
     * this History object.
     * @param historyMessageID the history message id
     */
    public void setHistoryMessageID(String historyMessageID) {
        this.historyMessageID = historyMessageID;
    }

    /**
     * Getter for property historyMessage.
     * @return Value of property historyMessage (loading it if necessary)
     * @throws PersistenceException if there is a problem loading the message
     */
    public String getHistoryMessage() throws PersistenceException {
        if (this.historyMessage == null) {
            loadHistoryMessage();
        }
        return this.historyMessage;
    }

    /**
     * Append a message to the history message
     * This allows the history message to be built from individual pieces
     * @param historyMessage the message to append to the history
     */
    void appendHistoryMessage(String historyMessage) {
        if (this.historyMessage == null) {
            this.historyMessage = "";
        }
        this.historyMessage += historyMessage;
    }

    /**
     * Setter for property historyMessage.
     * @param historyMessage New value of property historyMessage.
     */
    void setHistoryMessage(String historyMessage) {
        this.historyMessage = historyMessage;
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

    public String getEnvelopeName() {
        return this.envelopeName;
    }

    void setEnvelopeName(String envelopeName) {
        this.envelopeName = envelopeName;
    }

    public String getEnvelopeDescription() {
        return this.envelopeDescription;
    }

    void setEnvelopeDescription(String envelopeDescription) {
        this.envelopeDescription = envelopeDescription;
    }

    public String getActionByFullName() {
        return this.actionByFullName;
    }

    void setActionByFullName(String actionByFullName) {
        this.actionByFullName = actionByFullName;
    }

    public String getActionName() {
        return this.actionName;
    }

    void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getActionDescription() {
        return this.actionDescription;
    }

    void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }

    /**
     * Clear all properties
     */
    public void clear() {
        setID(null);
        setEnvelopeID(null);
        setHistoryActionID(null);
        setActionByID(null);
        setActionDatetime(null);
        setHistoryMessageID(null);
        setHistoryMessage(null);
        setCrc(null);
        setRecordStatus(null);

        setEnvelopeName(null);
        setEnvelopeDescription(null);
        setActionByFullName(null);
        setActionName(null);
        setActionDescription(null);

        setLoaded(false);
        setUser(null);
    }

    /**
     * @return true if instance has been populated from database
     */
    public boolean isLoaded() {
        return this.isLoaded;
    }

    void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Return the History id
     * @return the id of the history entry
     */
    public String getID() {
        return this.historyID;
    }

    /*
        Implementing IJDBCPersistence
     */
    public void setID(java.lang.String historyID) {
        this.historyID = historyID;
    }

    public void load() throws net.project.persistence.PersistenceException {
        StringBuffer queryBuff = new StringBuffer();

        queryBuff.append("select history_id, envelope_id, envelope_name, envelope_description, ");
        queryBuff.append("action_by_id, action_by_full_name, history_action_id, ");
        queryBuff.append("action_name, action_description, action_datetime, ");
        queryBuff.append("history_message, crc, record_status ");
        queryBuff.append("from pn_envelope_history_view ");
        queryBuff.append("WHERE history_id = " + getID() + " ");

        try {
            // Execute select statement
            db.executeQuery(queryBuff.toString());
            if (db.result.next()) {
                setID(db.result.getString("history_id"));
                setEnvelopeID(db.result.getString("envelope_id"));
                setHistoryActionID(db.result.getString("history_action_id"));
                setActionByID(db.result.getString("action_by_id"));
                setActionDatetime(db.result.getTimestamp("action_datetime"));
                setHistoryMessage(db.result.getString("history_message"));
                setCrc(db.result.getTimestamp("crc"));
                setRecordStatus(db.result.getString("record_status"));
                setEnvelopeName(db.result.getString("envelope_name"));
                setEnvelopeDescription(db.result.getString("envelope_description"));
                setActionByFullName(db.result.getString("action_by_full_name"));
                setActionName(PropertyProvider.get(db.result.getString("action_name")));
                setActionDescription(db.result.getString("action_description"));
                setLoaded(true);
            }

        } catch (SQLException sqle) {
            setLoaded(false);
            Logger.getLogger(History.class).error("History.load() threw an SQL exception: " + sqle);
            throw new PersistenceException("History load operation failed.", sqle);

        } finally {
            db.release();
        }
    }

    /**
     * Loads the history message from the appropriate clob object for this
     * history object.
     * @throws PersistenceException if there is a problem loading the data
     */
    private void loadHistoryMessage() throws PersistenceException {
        try {
            db.setClobTableName(History.HISTORY_CLOB_TABLE_NAME);
            /*
                Set the history message by getting the clob for the current
                history message id and grabbing its data
             */
            setHistoryMessage(db.getClob(getHistoryMessageID()).getData());
        } finally {
            // Release must be performed since db.getClob sets auto-commit
            db.release();
        }
    }

    /**
     * Store history record
     * @throws PersistenceException if there is a problem storing
     */
    public void store() throws net.project.persistence.PersistenceException {
        int errorCode = -1;
        String storedHistoryID;

        if (isLoaded()) {
            throw new PersistenceException("History store cannot store an existing history record.");

        } else {
            // Create new history
            /*
            procedure create_history
              ( i_envelope_id       IN varchar2,
                i_history_action_id IN varchar2,
                i_action_by_id      IN varchar2,
                i_action_datetime   IN date,
                i_history_message_id IN varchar2,
                o_history_id        OUT varchar2,
                o_return_value      OUT number);
            */
            net.project.database.Clob clob = null;
            try {
                db.openConnection();
                db.connection.setAutoCommit(false);

                // First store the history message in a CLOB row
                db.setClobTableName(HISTORY_CLOB_TABLE_NAME);
                clob = db.createClob(getHistoryMessage());
                clob.store();

                // Now create the history row, pointing to the new CLOB row
                db.prepareCall("{call workflow.create_history(?, ?, ?, ?, ?, ?, ?)}");
                db.cstmt.setString(1, getEnvelopeID());
                db.cstmt.setString(2, getHistoryActionID());
                db.cstmt.setString(3, getActionByID());
                db.cstmt.setTimestamp(4, new java.sql.Timestamp(getActionDatetime().getTime()));
                db.cstmt.setString(5, clob.getID());
                db.cstmt.registerOutParameter(6, java.sql.Types.VARCHAR);
                db.cstmt.registerOutParameter(7, java.sql.Types.INTEGER);

                // Execute that sucker
                db.executeCallable();

                // Get newly created stepID
                storedHistoryID = db.cstmt.getString(6);
                // Get error code for later handling
                errorCode = db.cstmt.getInt(7);

                if (errorCode != DBErrorCodes.OPERATION_SUCCESSFUL) {
                    /* Rollback if anything went wrong
                       An exception is thrown later based on this errorCode */
                    db.connection.rollback();

                } else {
                    /* COMMIT all previous updates */
                    db.connection.commit();

                }


            } catch (SQLException sqle) {
            	Logger.getLogger(History.class).error(
                    "History.store(): User: " + this.user.getID() + ", unable to execute stored procedure: " +
                    sqle);
                throw new PersistenceException("History store operation failed.", sqle);

            } finally {
                if (db.connection != null) {
                    try {
                        db.connection.rollback();
                    } catch (SQLException sqle) {
                        /* Nothing we can do here except release everything */
                    }
                }
                db.release();
            }

            // Handle (throw) any exceptions that were sucked up in PL/SQL
            try {
                DBExceptionFactory.getException("History.store()", errorCode);
            } catch (PnetException e) {
                throw new PersistenceException(e.getMessage(), e);
            }

        } // if (isLoaded)

        /*
            Since the current properties are out of date (the stored procedure will
            have inserted values for modified date time etc.) I will clear the
            object then set the historyID such that it can be loaded later.
        */
        clear();
        setID(storedHistoryID);

    }

    public void remove() throws net.project.persistence.PersistenceException {
        throw new PersistenceException("History.remove() functionality not available.");
    }

    /*
        Implement IXMLPersistence
     */
    public java.lang.String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }

    public java.lang.String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        String historyMessage = null;

        /* Get the history message */
        try {
            historyMessage = getHistoryMessage();
        } catch (PersistenceException pe) {
            historyMessage = "";
        }
        xml.append("<history>");
        xml.append("<history_id>" + XMLUtils.escape(getID()) + "</history_id>");
        xml.append("<envelope_id>" + XMLUtils.escape(getEnvelopeID()) + "</envelope_id>");
        xml.append("<envelope_name>" + XMLUtils.escape(getEnvelopeName()) + "</envelope_name>");
        xml.append("<envelope_description>" + XMLUtils.escape(getEnvelopeDescription()) + "</envelope_description>");
        xml.append("<history_action_id>" + XMLUtils.escape(getHistoryActionID()) + "</history_action_id>");
        xml.append("<action_by_id>" + XMLUtils.escape(getActionByID()) + "</action_by_id>");
        xml.append("<action_by_full_name>" + XMLUtils.escape(getActionByFullName()) + "</action_by_full_name>");
        xml.append("<action_name>" + XMLUtils.escape(getActionName()) + "</action_name>");
        xml.append("<action_description>" + XMLUtils.escape(getActionDescription()) + "</action_description>");
        xml.append("<action_datetime>" + XMLUtils.escape(Conversion.dateToString(getActionDatetime())) + "</action_datetime>");
        xml.append("<history_message>" + historyMessage + "</history_message>");
        xml.append("<crc>" + XMLUtils.escape(Conversion.dateToString(getCrc())) + "</crc>");
        xml.append("<record_status>" + XMLUtils.escape(getRecordStatus()) + "</record_status>");
        xml.append("</history>");
        return xml.toString();
    }

}
