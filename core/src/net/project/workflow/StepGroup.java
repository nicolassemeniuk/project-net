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
import java.sql.SQLException;
import java.sql.Timestamp;

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
  * This class represents a step has group database record
  */
public class StepGroup implements IJDBCPersistence, IXMLPersistence, Serializable, ErrorCodes {

    /** Special "Envelope Creator" group, used to indicate that the creator of
      * any envelope from this workflow should be attached to the step.
      * This corresponds with the entry in PN_GROUP/PN_OBJECT to reserve this number.
      */
    public static final String ENVELOPE_CREATOR_GROUP_ID = "100";

    /* Persistent properties */
    String stepID = null;
    String groupID = null;
    String workflowID = null;
    boolean isParticipant = false;
    // Database modifiable only
    String createdBy = null;
    java.util.Date createdDatetime = null;
    String modifiedBy = null;
    java.util.Date modifiedDatetime = null;
    java.util.Date crc = null;
    String recordStatus = null;
    /* End of persistent properties */

    /* Denormalized group properties */
    String name = null;
    String description = null;
    boolean isPrincipal = false;
    String createdByFullName = null;
    String modifiedByFullName = null;

    /** Indicates whether group is subscribed to notification for this step
      * Note - this is not loaded during load(), only loaded by using
      * WorkflowManager methods. */
    private boolean isNotified = false;
    /* End of denormalized properties */

    boolean isLoaded = false;
    /** user currently manipulating workflow */
    private User user = null;
    private DBBean db = null;

    StepGroup() {
        db = new DBBean();
    }

    public void setParticipant(boolean isParticipant) {
        this.isParticipant = isParticipant;
    }

    public boolean isParticipant() {
        return this.isParticipant;
    }

    public String getStepID() {
        return this.stepID;
    }

    public String getWorkflowID() {
        return this.workflowID;
    }

    public String getGroupID() {
        return this.groupID;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isPrincipal() {
        return this.isPrincipal;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setNotified(boolean isNotified) {
        this.isNotified = isNotified;
    }

    public boolean isNotified() {
        return this.isNotified;
    }

    /**********************************************************************************
     *  Implementing IJDBCPersistance
     **********************************************************************************/
    public void setID(java.lang.String id) {
        // Not supported
    }
   
    /**
      * Load the StepGroup from the underlying JDBC object.
      * stepID, workflowID and groupID should be set before calling this
      * @throws PersistanceException Thrown to indicate a failure loading from the database, a system-level error.
      */
    public void load() throws net.project.persistence.PersistenceException {
        StringBuffer queryBuff = new StringBuffer();

        queryBuff.append("select step_id, workflow_id, workflow_name, ");
        queryBuff.append("select step_id, workflow_id, group_id, group_name, ");
        queryBuff.append("group_desc, is_principal, is_participant, ");
        queryBuff.append("created_by_id, created_by_full_name, created_datetime, ");
        queryBuff.append("modified_by_id, modified_by_full_name, modified_datetime, ");
        queryBuff.append("crc, record_status ");
        queryBuff.append("from pn_wf_step_has_group_view ");
        queryBuff.append("WHERE step_id = " + this.stepID + " ");
        queryBuff.append("AND workflow_id = " + this.workflowID + " ");
        queryBuff.append("AND group_id = " + this.groupID + " ");

        try {
            // Execute select statement
            db.executeQuery(queryBuff.toString());

            if (db.result.next()) {
                this.groupID = db.result.getString("group_id");
                this.stepID = db.result.getString("step_id");
                this.workflowID = db.result.getString("workflow_id");
                this.isParticipant = Conversion.toBoolean(db.result.getString("is_participant"));
                this.createdBy = db.result.getString("created_by_id");
                this.createdDatetime = (java.util.Date)db.result.getTimestamp("created_datetime");
                this.modifiedBy = db.result.getString("modified_by_id");
                this.modifiedDatetime = (java.util.Date)db.result.getTimestamp("modified_datetime");
                this.crc = (java.util.Date)db.result.getTimestamp("crc");
                this.recordStatus = db.result.getString("record_status");
                
                this.name = PropertyProvider.get(db.result.getString("group_name"));
                this.description = db.result.getString("description");
                this.isPrincipal = Conversion.toBoolean(db.result.getString("is_principal"));
                this.createdByFullName = db.result.getString("created_by_full_name");
                this.modifiedByFullName = db.result.getString("modified_by_full_name");

                this.isLoaded = true;
            }

        } catch (SQLException sqle)  {
            this.isLoaded = false;
            Logger.getLogger(StepGroup.class).error("StepGroup.load() threw an SQL exception: " + sqle);
            throw new PersistenceException ("Step Group load operation failed.", sqle);

        } finally {
           db.release();
       }

    } // load()

    /**
      * Store the record
      */
    public void store() throws net.project.persistence.PersistenceException {
        int errorCode = -1;

        if (this.isLoaded) {
            // Modify existing step has group
            /*
            procedure modify_step_group (
                i_step_id                in varchar2,
                i_workflow_id            in varchar2,
                i_group_id               in varchar2,
                i_is_participant         in number,
                i_modified_by_id         in varchar2,
                i_crc                    in date,
                o_return_value           out number);
           */
            try {
                db.prepareCall("BEGIN workflow.modify_step_group(?, ?, ?, ?, ?, ?, ?);  END;");
                db.cstmt.setString(1, this.stepID);
                db.cstmt.setString(2, this.workflowID);
                db.cstmt.setString(3, this.groupID);
                db.cstmt.setInt(4, (this.isParticipant ? 1 : 0));
                db.cstmt.setString(5, this.user.getID());
                db.cstmt.setTimestamp(6, new Timestamp(this.crc.getTime()));
                db.cstmt.registerOutParameter(7, java.sql.Types.INTEGER);

                // Execute that sucker
                db.executeCallable();

                // Get error code for later handling
                errorCode = db.cstmt.getInt(7);

            } catch (SQLException sqle) {
            	Logger.getLogger(StepGroup.class).error(
                    "StepGroup.store(): Unable to execute stored procedure: " +
                    sqle);
                throw new PersistenceException("StepGroup store operation failed.", sqle);

            } finally {
                db.release();
            }

            // Handle (throw) any exceptions that were sucked up in PL/SQL
            try {
                DBExceptionFactory.getException("StepGroup.store()", errorCode);
            } catch (net.project.base.UpdateConflictException e) {
                // CRC was different than that in database
                RecordModifiedException rme = 
                        new RecordModifiedException("A record has been modified by another user.  Please try again.");
                rme.setErrorCode(STEP_GROUP_RECORD_MODIFIED);
                throw rme;
            } catch (PnetException e) {
                throw new PersistenceException(e.getMessage(), e);
            }

        } else {
            // Create new step group record
            /*
            procedure create_step_group (
                i_step_id                in varchar2,
                i_workflow_id            in varchar2,
                i_group_id               in varchar2,
                i_is_participant         in number,
                i_created_by_id          in varchar2,
                o_return_value           out number);
            */
            try {
                db.prepareCall("BEGIN workflow.create_step_group(?, ?, ?, ?, ?, ?);  END;");
                db.cstmt.setString(1, this.stepID);
                db.cstmt.setString(2, this.workflowID);
                db.cstmt.setString(3, this.groupID);
                db.cstmt.setInt(4, (this.isParticipant ? 1 : 0));
                db.cstmt.setString(5, this.user.getID());
                db.cstmt.registerOutParameter(6, java.sql.Types.INTEGER);

                // Execute that sucker
                db.executeCallable();

                // Get error code for later handling
                errorCode = db.cstmt.getInt(6);

            } catch (SQLException sqle) {
            	Logger.getLogger(StepGroup.class).error(
                    "StepGroup.store(): Unable to execute stored procedure: " +
                    sqle);
                throw new PersistenceException("StepGroup store operation failed.", sqle);

            } finally {
                db.release();
            }

            // Handle (throw) any exceptions that were sucked up in PL/SQL
            try {
                DBExceptionFactory.getException("StepGroup.store()", errorCode);
            } catch (PnetException e) {
                throw new PersistenceException(e.getMessage(), e);
            }

        } // if (isLoaded)

    }

    public void remove() throws net.project.persistence.PersistenceException {

        int errorCode = -1;

        // Remove existing step has group
        /*
        procedure remove_step_group (
            i_step_id                in varchar2,
            i_workflow_id            in varchar2,
            i_group_id               in varchar2,
            i_modified_by_id         in varchar2,
            i_crc                    in date,
            o_return_value           out number);
       */
        try {
            db.prepareCall("BEGIN workflow.remove_step_group(?, ?, ?, ?, ?, ?);  END;");
            db.cstmt.setString(1, this.stepID);
            db.cstmt.setString(2, this.workflowID);
            db.cstmt.setString(3, this.groupID);
            db.cstmt.setString(4, this.user.getID());
            db.cstmt.setTimestamp(5, new Timestamp(this.crc.getTime()));
            db.cstmt.registerOutParameter(6, java.sql.Types.INTEGER);

            // Execute that sucker
            db.executeCallable();

            // Get error code for later handling
            errorCode = db.cstmt.getInt(6);

        } catch (SQLException sqle) {
        	Logger.getLogger(StepGroup.class).error(
                "StepGroup.remove(): Unable to execute stored procedure: " +
                sqle);
            throw new PersistenceException("StepGroup remove operation failed.", sqle);

        } finally {
            db.release();
        }

        // Handle (throw) any exceptions that were sucked up in PL/SQL
        try {
            DBExceptionFactory.getException("StepGroup.remove()", errorCode);
        } catch (net.project.base.UpdateConflictException e) {
            // CRC was different than that in database
            RecordModifiedException rme = 
                    new RecordModifiedException("A record has been modified by another user.  Please try again.");
            rme.setErrorCode(STEP_GROUP_RECORD_MODIFIED);
            throw rme;
        } catch (PnetException e) {
            throw new PersistenceException(e.getMessage(), e);
        }

    }

    /**
      * Converts the step into an XML string including the XML version
      * tag.
      * @return the XML string
      */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }    

    /**
      * Return XML body for this entry
      * @return XML body string
      */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<step_group>\n");
        xml.append(getXMLElements());
        xml.append("</step_group>\n");
        return xml.toString();
    }

    /**
      * Return this entry's XML elements
      * @return the XML element string
      */
    String getXMLElements() {
        StringBuffer xml = new StringBuffer();
        String type = null;
        
        if (isPrincipal) {
            type = "person";
        } else {
            type = "group";
        }

        xml.append("<group_id>" + XMLUtils.escape(groupID) + "</group_id>");
        xml.append("<step_id>" + XMLUtils.escape(stepID) + "</step_id>");
        xml.append("<workflow_id>" + XMLUtils.escape(workflowID) + "</workflow_id>");
        xml.append("<name>" + XMLUtils.escape(name) + "</name>");
        xml.append("<description>" + XMLUtils.escape(description) + "</description>");
        xml.append("<entry_type>" + XMLUtils.escape(type) + "</entry_type>");
        xml.append("<is_participant>" + XMLUtils.escape((isParticipant ? "1" : "0")) + "</is_participant>");
        xml.append("<is_notified>" + XMLUtils.escape((isNotified ? "1" : "0")) + "</is_notified>");
        xml.append("<created_by_id>" + XMLUtils.escape(createdBy) + "</created_by_id>");
        xml.append("<created_datetime>" + XMLUtils.escape(Conversion.dateToString(createdDatetime)) + "</created_datetime>");
        xml.append("<created_by_full_name>" + XMLUtils.escape(createdByFullName) + "</created_by_full_name>");
        xml.append("<modified_by_id>" + XMLUtils.escape(modifiedBy) + "</modified_by_id>");
        xml.append("<modified_datetime>" + XMLUtils.escape(Conversion.dateToString(modifiedDatetime)) + "</modified_datetime>");
        xml.append("<modified_by_full_name>" + XMLUtils.escape(modifiedByFullName) + "</modified_by_full_name>");
        return xml.toString();
    }

}

