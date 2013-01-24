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
|    Transition.java
|    $RCSfile$
|   $Revision: 19724 $
|       $Date: 2009-08-12 09:14:00 -0300 (mié, 12 ago 2009) $
|     $Author: uroslates $
|
| A workflow transition.
+----------------------------------------------------------------------*/
package net.project.workflow;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;

import net.project.base.DBErrorCodes;
import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.DBExceptionFactory;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.util.DateFormat;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;


/**
 * A workflow transition.
 * A transition is defined between two workflow steps.  A transition has
 * numerous rules.
 */
public class Transition
    implements IJDBCPersistence, IXMLPersistence, Serializable, ErrorCodes {

    protected String transitionID = null;
    protected String workflowID = null;
    protected String beginStepID = null;
    protected String endStepID = null;
    protected String transitionVerb = null;
    protected String description = null;
    // Database modifiable only
    protected String createdBy = null;
    protected java.util.Date createdDatetime = null;
    protected String modifiedBy = null;
    protected java.util.Date modifiedDatetime = null;
    protected java.util.Date crc = null;
    protected String recordStatus = null;

    /** Workflow to which this transition belongs */
    private Workflow workflow = null;
    /** Rules belonging to this transition */
    private RuleList ruleList = null;

    protected String workflowName = null;
    protected String beginStepName = null;
    protected String endStepName = null;
    protected String createdByFullName = null;
    protected String modifiedByFullName = null;
    protected boolean isLoaded = false;
    /** Indicates whether the transition may be removed.  This is set to true only
     * when the appropriate checks have been performed */
    private boolean isRemovePermitted = false;
    /** Indicates whether the transition removal was successful or not. */
    private boolean isRemoveSuccessful = false;


    /** user currently manipulating workflow */
    private User user = null;
    private DBBean db = new DBBean();


    /**
     * Creates new Transitions
     */
    public Transition() {
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
     * Set the beginning step id for the transition
     * @param stepID the beginning step id
     */
    public void setBeginStepID(String stepID) {
        this.beginStepID = stepID;
    }

    /**
     * Return the beginning step id of this transition
     * @return the beginning step id
     */
    public String getBeginStepID() {
        return this.beginStepID;
    }

    /**
     * Set the ending step id for the transition
     * @param stepID the ending step id
     */
    public void setEndStepID(String stepID) {
        this.endStepID = stepID;
    }

    /**
     * Return the ending step id of this transition
     * @return the ending step id
     */
    public String getEndStepID() {
        return this.endStepID;
    }

    /** Getter for property transitionVerb.
     * @return Value of property transitionVerb.
     */
    public String getTransitionVerb() {
        return this.transitionVerb;
    }

    /** Setter for property transitionVerb.
     * @param transitionVerb New value of property transitionVerb.
     */
    public void setTransitionVerb(String transitionVerb) {
        this.transitionVerb = (transitionVerb == null)? null : transitionVerb.trim();
    }

    /** Getter for property description.
     * @return Value of property description.
     */
    public String getDescription() {
        return this.description;
    }

    /** Setter for property description.
     * @param description New value of property description.
     */
    public void setDescription(String description) {
        this.description = description;
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

    public String getWorkflowName() {
        return this.workflowName;
    }

    void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getBeginStepName() {
        return this.beginStepName;
    }

    void setBeginStepName(String beginStepName) {
        this.beginStepName = beginStepName;
    }

    public String getEndStepName() {
        return this.endStepName;
    }

    void setEndStepName(String endStepName) {
        this.endStepName = endStepName;
    }

    public String getCreatedByFullName() {
        return this.createdByFullName;
    }

    void setCreatedByFullName(String createdByFullName) {
        this.createdByFullName = createdByFullName;
    }

    public String getModifiedByFullName() {
        return this.modifiedByFullName;
    }

    void setModifiedByFullName(String modifiedByFullName) {
        this.modifiedByFullName = modifiedByFullName;
    }

    /**
     * Return the workflow to which this transition belongs
     * @return the workflow
     */
    public Workflow getWorkflow() throws PersistenceException {
        if (this.workflow == null) {
            loadWorkflow();
        }
        return this.workflow;
    }

    void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    /**
     * Return the rules for the transition.  Each rule in the rule list
     * is of the correct class.  The rules are loaded if necessary.
     * @throws PersistenceException if there is a problem loading the rules
     */
    public RuleList getRuleList() throws PersistenceException {
        if (this.ruleList == null) {
            loadRuleList();
        }
        return this.ruleList;
    }

    /**
     * Set the rule list
     * @param ruleList the rule list
     */
    void setRuleList(RuleList ruleList) {
        this.ruleList = ruleList;
    }

    /**
     * Clear all properties
     */
    public void clear() {
        this.transitionID = null;
        this.workflowID = null;
        this.transitionVerb = null;
        this.description = null;
        this.beginStepID = null;
        this.endStepID = null;

        this.createdBy = null;
        this.createdDatetime = null;
        this.modifiedBy = null;
        this.modifiedDatetime = null;
        this.crc = null;
        this.recordStatus = null;

        setWorkflow(null);
        setRuleList(null);
        this.workflowName = null;
        this.beginStepName = null;
        this.endStepName = null;
        this.createdByFullName = null;
        this.modifiedByFullName = null;

        this.isLoaded = false;
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
     * Set the current user.  This is required before manipulating.
     * It is used to stamp the creation/modification userid in the database.
     * @param user the user manipulating the transition.
     */
    public void setUser(User user) {
        this.user = user;
    }

    User getUser() {
        return this.user;
    }

    /**
     * Return Transition's id
     * @return id of transition
     */
    public String getID() {
        return this.transitionID;
    }

    /*
        Implement IJDBCPersistence
    */
    public void setID(java.lang.String transitionID) {
        this.transitionID = transitionID;
    }
    
    
    //Avinash:- setter for transactionID---------------------
    public void setTransitionID(java.lang.String transitionID) {
        this.transitionID = transitionID;
    }
    //Avinash:-----------------------------------------------

    /**
     * Load the Transition from the underlying JDBC object.
     * @throws PersistenceException Thrown to indicate a failure loading from the database, a system-level error.
     */
    public void load() throws net.project.persistence.PersistenceException {
        StringBuffer queryBuff = new StringBuffer();

        queryBuff.append("select transition_id, workflow_id, workflow_name, ");
        queryBuff.append("transition_verb, transition_description, begin_step_id, ");
        queryBuff.append("begin_step_name, end_step_id, end_step_name, ");
        queryBuff.append("created_by_id, created_by_full_name, created_datetime, ");
        queryBuff.append("modified_by_id, modified_by_full_name, modified_datetime, ");
        queryBuff.append("crc, record_status ");
        queryBuff.append("from pn_workflow_transition_view ");
        queryBuff.append("WHERE transition_id = " + this.transitionID + " ");

        try {
            // Execute select statement
            db.executeQuery(queryBuff.toString());

            if (db.result.next()) {
                this.transitionID = db.result.getString("transition_id");
                this.workflowID = db.result.getString("workflow_id");
                this.transitionVerb = db.result.getString("transition_verb");
                this.description = db.result.getString("transition_description");
                this.beginStepID = db.result.getString("begin_step_id");
                this.endStepID = db.result.getString("end_step_id");

                this.createdBy = db.result.getString("created_by_id");
                this.createdDatetime = db.result.getTimestamp("created_datetime");
                this.modifiedBy = db.result.getString("modified_by_id");
                this.modifiedDatetime = db.result.getTimestamp("modified_datetime");
                this.crc = db.result.getTimestamp("crc");
                this.recordStatus = db.result.getString("record_status");

                this.workflowName = db.result.getString("workflow_name");
                this.beginStepName = db.result.getString("begin_step_name");
                this.endStepName = db.result.getString("end_step_name");
                this.createdByFullName = db.result.getString("created_by_full_name");
                this.modifiedByFullName = db.result.getString("modified_by_full_name");

                this.isLoaded = true;
            }

        } catch (SQLException sqle) {
            this.isLoaded = false;
            Logger.getLogger(Transition.class).error("Transition.load() threw an SQL exception: " + sqle);
            throw new PersistenceException("Transition load operation failed.", sqle);

        } finally {
            db.release();
        }

    } // load()

    /**
     * Load the workflow to which this transition belongs
     * Sets attribute workflow
     * @throws PersistenceException if there is a problem loading the workflow
     * @see workflow
     */
    private void loadWorkflow() throws PersistenceException {
        Workflow workflow = new Workflow();
        workflow.setID(getWorkflowID());
        workflow.load();
        setWorkflow(workflow);
    }

    /**
     * This requrires (NumberOfRules + 1) SQL statements
     * since we must invoke the load() operation of each rule independently beacuse
     * a rule has subclass-specific information in its load() method
     * @throws PersistenceException if there is a problem loading the rules
     */
    private void loadRuleList() throws PersistenceException {
        RuleList ruleList = new RuleList();
        Rule rule = null;
        StringBuffer queryBuff = new StringBuffer();

        queryBuff.append("select rule_id, rule_type_id ");
        queryBuff.append("from pn_workflow_rule_view ");
        queryBuff.append("WHERE transition_id = " + getID() + " ");

        try {
            String ruleID = null;
            String ruleTypeID = null;

            db.executeQuery(queryBuff.toString());
            if (db.result.next()) {
                ruleID = db.result.getString("rule_id");
                ruleTypeID = db.result.getString("rule_type_id");

                /* Create rule object based on rule type */
                try {
                    rule = RuleFactory.createRule(ruleTypeID);
                } catch (RuleException re) {
                	Logger.getLogger(Transition.class).error("Transition.getRules() could not create a rule for type: " + ruleTypeID +
                        ": " + re);
                }
                rule.setID(ruleID);
                rule.load();
                ruleList.add(rule);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(Transition.class).error("Transition.getRules() threw an SQL exception: " + sqle);
            throw new PersistenceException("Transition get rules operation failed.", sqle);

        } finally {
            db.release();

        }

        setRuleList(ruleList);
    }

    /**
     * Save the Transition to the underlying JDBC object.
     * Note: after a successful store this object's properties are cleared
     * and the transitionID is set such that the load() method may be called.
     *
     * @throws PersistenceException Thrown to indicate a failure storing to the
     * database, a system-level error.
     */
    public void store() throws net.project.persistence.PersistenceException {

        int errorCode = -1;
        String storedTransitionID;

        if (this.isLoaded) {
            // Modify existing transition
            /*
            procedure modify_transition (
                i_transition_id          in varchar2,
                i_transition_verb        in varchar2,
                i_transition_description in varchar2,
                i_begin_step_id          in varchar2,
                i_end_step_id            in varchar2,
                i_modified_by_id         in varchar2,
                i_crc                    in date,
                o_return_value           out number);
            */
            try {
                db.prepareCall("BEGIN workflow.modify_transition(?, ?, ?, ?, ?, ?, ?, ?);  END;");
                db.cstmt.setString(1, this.transitionID);
                db.cstmt.setString(2, this.transitionVerb);
                db.cstmt.setString(3, this.description);
                db.cstmt.setString(4, this.beginStepID);
                db.cstmt.setString(5, this.endStepID);
                db.cstmt.setString(6, this.user.getID());
                db.cstmt.setTimestamp(7, new Timestamp(this.crc.getTime()));
                db.cstmt.registerOutParameter(8, java.sql.Types.INTEGER);

                // Execute that sucker
                db.executeCallable();

                // Get error code for later handling
                errorCode = db.cstmt.getInt(8);
                storedTransitionID = this.transitionID;

            } catch (SQLException sqle) {
            	Logger.getLogger(Transition.class).error(
                    "Transition.store(): User: " + this.user.getID() + ", unable to execute stored procedure: " +
                    sqle);
                throw new PersistenceException("Transition store operation failed.", sqle);

            } finally {
                db.release();
            }

            // Handle (throw) any exceptions that were sucked up in PL/SQL
            try {
                DBExceptionFactory.getException("Transition.store()", errorCode);
            } catch (net.project.base.UpdateConflictException e) {
                // CRC was different than that in database
                RecordModifiedException rme =
                    new RecordModifiedException("The transition has been modified by another user.  Please try again.");
                rme.setErrorCode(TRANSITION_RECORD_MODIFIED);
                throw rme;
            } catch (PnetException e) {
                throw new PersistenceException(e.getMessage(), e);
            }

        } else {
            // Create new transition
            /*
            procedure create_transition
              ( i_workflow_id            in varchar2,
                i_transition_verb        in varchar2,
                i_transition_description in varchar2,
                i_begin_step_id          in varchar2,
                i_end_step_id            in varchar2,
                i_created_by_id          in varchar2,
                o_transition_id          out varchar2,
                o_return_value           out number);
            */
            try {
                db.prepareCall("BEGIN workflow.create_transition(?, ?, ?, ?, ?, ?, ?, ?);  END;");
                db.cstmt.setString(1, this.workflowID);
                db.cstmt.setString(2, this.transitionVerb);
                db.cstmt.setString(3, this.description);
                db.cstmt.setString(4, this.beginStepID);
                db.cstmt.setString(5, this.endStepID);
                db.cstmt.setString(6, this.user.getID());
                db.cstmt.registerOutParameter(7, java.sql.Types.VARCHAR);
                db.cstmt.registerOutParameter(8, java.sql.Types.INTEGER);

                // Execute that sucker
                db.executeCallable();

                // Get newly created transitionID
                storedTransitionID = db.cstmt.getString(7);
                // Get error code for later handling
                errorCode = db.cstmt.getInt(8);

            } catch (SQLException sqle) {
            	Logger.getLogger(Transition.class).error(
                    "Transition.store(): User: " + this.user.getID() + ", unable to execute stored procedure: " +
                    sqle);
                throw new PersistenceException("Transition store operation failed.", sqle);

            } finally {
                db.release();
            }

            // Handle (throw) any exceptions that were sucked up in PL/SQL
            try {
                DBExceptionFactory.getException("Transition.store()", errorCode);
            } catch (PnetException e) {
                throw new PersistenceException(e.getMessage(), e);
            }

        } // if (isLoaded)

        /*
            Since the current properties are out of date (the stored procedure will
            have inserted values for modified date time etc.) I will clear the
            object then set the transitionID such that it can be loaded later.
        */
        this.clear();
        this.transitionID = storedTransitionID;
    }

    /**
     * Prepare the workflow to be removed:  Check it is not published and that there are
     * no active envelopes.
     * This routine populates the errorTable.
     */
    public void prepareRemove() {
        Workflow workflow = null;
        boolean isMetCriteria = true;

        /* Default to not permitted */
        setRemovePermitted(false);

        /* Check to see if Workflow is published */
        workflow = new Workflow();
        workflow.setID(getWorkflowID());
        workflow.setUser(getUser());
        try {
            workflow.load();
            if (workflow.isPublished()) {
                errors.put("remove_is_published", null, "A transition may not be removed while the workflow to which is belongs is Published.");
                isMetCriteria &= false;
            }

        } catch (PersistenceException pe) {
            errors.put("remove_is_published", null, "There was a problem locating the workflow to which this transition belongs.");
            isMetCriteria &= false;
        }

        setRemovePermitted(isMetCriteria);
    }

    String getPrepareRemoveErrorsPresentation() {
        return errors.getAllErrorsRemovePresentation();
    }

    String getRemoveResultPresentation() {
        return errors.getAllErrorsRemovePresentation();
    }

    public boolean isRemovePermitted() {
        return this.isRemovePermitted;
    }

    void setRemovePermitted(boolean isRemovePermitted) {
        this.isRemovePermitted = isRemovePermitted;
    }

    public boolean isRemoveSuccessful() {
        return this.isRemoveSuccessful;
    }

    void setRemoveSuccessful(boolean isRemoveSuccessful) {
        this.isRemoveSuccessful = isRemoveSuccessful;
    }

    /**
     * Delete the Transition from the database.
     * @throws PersistenceException Thrown to indicate a failure storing to the database, a system-level error.
     */
    public void remove() throws net.project.persistence.PersistenceException {
        /* Default to a failed removal */
        setRemoveSuccessful(false);

        /* Repeat the checks that should have been done prior to this method call */
        prepareRemove();

        if (!isRemovePermitted()) {
            /*
                Remove is not permitted
                Message will have been created and will be displayed to user
             */

        } else {
            /*
                Remove is permitted.
                Do it.
             */
            removeTransition();
            setRemoveSuccessful(true);
        }
    }

    /**
     * Performs the actual remove
     * @throws PersistenceException if there is a problem performing the remove
     */
    private void removeTransition() throws PersistenceException {
        /* errorCode will be returned from stored procedures */
        int errorCode = -1;

        /*
            procedure remove_transition (
                i_transition_id  in varchar2,
                i_modified_by_id in varchar2,
                i_crc            in date,
                o_return_value   out number);
        */
        try {
            db.prepareCall("{call workflow.remove_transition(?, ?, ?, ?)}");
            db.connection.setAutoCommit(false);
            db.cstmt.setString(1, getID());
            db.cstmt.setString(2, getUser().getID());
            db.cstmt.setTimestamp(3, new Timestamp(getCrc().getTime()));
            db.cstmt.registerOutParameter(4, java.sql.Types.INTEGER);
            db.executeCallable();
            errorCode = db.cstmt.getInt(4);
            db.closeCStatement();
            if (errorCode != DBErrorCodes.OPERATION_SUCCESSFUL) {
                /* Rollback if anything went wrong
                   An exception is thrown later based on this errorCode */
                db.connection.rollback();

            } else {
                /*
                   Successfully removed the transition
                 */
                db.connection.commit();
                errorCode = DBErrorCodes.OPERATION_SUCCESSFUL;
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(Transition.class).error(
                "Transition.remove(): User: " + getUser().getID() + ", unable to execute stored procedure: " +
                sqle);
            throw new PersistenceException("Transition remove operation failed.", sqle);

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
            DBExceptionFactory.getException("Transition.remove()", errorCode);

        } catch (net.project.base.UpdateConflictException e) {
            RecordModifiedException rme =
                new RecordModifiedException("The transition has been modified by another user.  Please try again.");
            rme.setErrorCode(TRANSITION_RECORD_MODIFIED);
            throw rme;
        } catch (net.project.persistence.RecordLockedException e) {
            WorkflowPersistenceException wpe =
                new WorkflowPersistenceException("The transition is currently locked by another user.  Please try again.");
            wpe.setErrorCode(TRANSITION_RECORD_LOCKED);
            throw wpe;

        } catch (PnetException e) {
            throw new PersistenceException(e.getMessage(), e);
        }

    }

    /*
        Implement IXMLPersistence
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }

    public java.lang.String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<transition>\n");
        xml.append(getXMLElements());
        xml.append("</transition>\n");

        return xml.toString();
    }

    /**
     * Returns the basic transition XML elements
     * @return XML elements as string
     */
    private String getXMLElements() {
	StringBuffer xml = new StringBuffer();
	DateFormat dt = new DateFormat(this.user);

	xml.append("<transition_id>" + quote(this.transitionID)
		+ "</transition_id>\n");
	xml.append("<workflow_id>" + quote(this.workflowID)
		+ "</workflow_id>\n");
	xml.append("<workflow_name>" + quote(this.workflowName)
		+ "</workflow_name>\n");
	xml.append("<transition_verb>" + quote(this.transitionVerb)
		+ "</transition_verb>\n");
	xml.append("<description>" + quote(this.description)
		+ "</description>\n");
	xml.append("<begin_step_id>" + quote(this.beginStepID)
		+ "</begin_step_id>\n");
	xml.append("<begin_step_name>" + quote(this.beginStepName)
		+ "</begin_step_name>\n");
	xml
		.append("<end_step_id>" + quote(this.endStepID)
			+ "</end_step_id>\n");
	xml.append("<end_step_name>" + quote(this.endStepName)
		+ "</end_step_name>\n");
	xml.append("<created_by_id>" + quote(this.createdBy)
		+ "</created_by_id>\n");
	xml.append("<created_by_full_name>" + quote(this.createdByFullName)
		+ "</created_by_full_name>\n");
	xml.append("<created_datetime>"
		+ quote(dt.formatDate(this.createdDatetime))
		+ "</created_datetime>\n");
	xml.append("<modified_by_id>" + quote(this.modifiedBy)
		+ "</modified_by_id>\n");
	xml.append("<modified_by_full_name>" + quote(this.modifiedByFullName)
		+ "</modified_by_full_name>\n");
	xml.append("<modified_datetime>"
		+ quote(dt.formatDate(this.modifiedDatetime))
		+ "</modified_datetime>\n");
	xml.append("<crc>" + quote(dt.formatDate(this.crc)) + "</crc>\n");
	xml.append("<record_status>" + quote(this.recordStatus)
		+ "</record_status>\n");
	return xml.toString();
    }

    /**
         * quotes string to HTML, turns null strings into empty strings
         * 
         * @param s
         *                the string
         * @return the quotes string
         */
    private String quote(String s) {
        return XMLUtils.escape(s);
    }

    /*=================================================================================================
      Error stuff
      ================================================================================================*/

    private ValidationErrors errors = new ValidationErrors();
    
    /** 
     * Validation Errors getter
     * @return validation errors object
     */
    public ValidationErrors getErrors() {
    	return errors;
    }

    public void clearErrors() {
        errors.clearErrors();
    }

    /**
     * Validates that all information in this Transition is suitable for storing.
     * 
     * @since 8.2.0
     */
    public void validate(){
	validateTransitionVerb();
	validateDescription();
	validateBeginStepID();
	validateEndStepID();
    }

    
    /**
     * Validate the contents of the Envelope
     */
    public void validateAll() {
    }

    /**
     * Indicate whether there are any errors
     * @return true if there are errors, false otherwise
     */
    public boolean hasErrors() {
        return errors.hasErrors();
    }

    public String getErrorsTable() {
        return errors.getAllErrorsDefaultPresentation();
    }

    public String getFlagError(String fieldID, String label) {
        return errors.flagError(fieldID, label);
    }

    /*
        Validation Routines
     */

    /**
     * Name must be non-null
     */
    private void validateTransitionVerb() {
        if (getTransitionVerb() == null || getTransitionVerb().equals("")) {
            errors.put("transition_verb", PropertyProvider.get("prm.workflow.transitionedit.name.label"), PropertyProvider.get("prm.workflow.requiredfield.validation.message"));
        }
    }

    private void validateDescription() {
        if (getDescription() != null && getDescription().length() > 500) {
            errors.put("description", PropertyProvider.get("prm.workflow.transitionedit.description.label"), PropertyProvider.get("prm.workflow.description.toomanychars.validation.message"));
        }
    }

    private void validateBeginStepID() {
        if (getBeginStepID() == null || getBeginStepID().equals("")) {
            errors.put("begin_step_id", PropertyProvider.get("prm.workflow.transitionedit.fromstep.label"), PropertyProvider.get("prm.workflow.requiredfield.validation.message"));
        }
    }

    private void validateEndStepID() {
        if (getEndStepID() == null || getEndStepID().equals("")) {
            errors.put("end_step_id", PropertyProvider.get("prm.workflow.transitionedit.tostep.label"), PropertyProvider.get("prm.workflow.requiredfield.validation.message"));
        }
    }

    /**
     * Generates an error if the workflow to which this transition belongs is published
     * @throws PersistenceException if there was a problem determining the workflow
     */
    public void validateUnpublished() throws PersistenceException {
        Workflow workflow = null;
        // load workflow - if get from session issues may occure in getting its status
        loadWorkflow();
        workflow = getWorkflow();
        if (workflow.isPublished()) {
            errors.put("is_published", null, PropertyProvider.get("prm.workflow.ispublished.change.validation.message"));
        }
    }

    /*
        End of validation routines
     */

    /*=================================================================================================
      End of Error stuff
      ================================================================================================*/

}
