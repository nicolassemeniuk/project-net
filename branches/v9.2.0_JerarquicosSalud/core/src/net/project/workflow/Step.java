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

 package net.project.workflow;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Iterator;

import net.project.base.DBErrorCodes;
import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.database.DBExceptionFactory;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.util.Conversion;
import net.project.util.DateFormat;
import net.project.util.NumberFormat;
import net.project.util.Validator;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;


/**
 * A workflow step.  This describes a state that the workflow can enter.
 *
 * @author Tim
 * @since 08/2000
 */
public class Step implements IJDBCPersistence, IXMLPersistence, Serializable, ErrorCodes {

    /** Event name for envelope entering step */
    public static final String ENTER_EVENT_ID = "2300";
    public static final String ENTER_EVENT_TYPE = "envelope_step_enter";

    /**
     * Defines the property that provides the description for the "Enter step"
     * event, currently <code>prm.workflow.event.stepenter.description</code>.
     */
    public static final String ENTER_EVENT_DESCRIPTION_PROPERTY = "prm.workflow.event.stepenter.description";

    private String stepID = null;
    private String workflowID = null;
    private String name = null;
    private String description = null;
    private String notes = null;
    private boolean isFinalStep = false;
    private boolean isInitialStep = false;
    private String entryStatusID = null;
    private String subscriptionID = null;
    private String sequence = null;

    // Database modifiable only
    private String createdBy = null;
    private java.util.Date createdDatetime = null;
    private String modifiedBy = null;
    private java.util.Date modifiedDatetime = null;
    private java.util.Date crc = null;
    private String recordStatus = null;

    private String workflowName = null;
    private String entryStatusName = null;
    private String createdByFullName = null;
    private String modifiedByFullName = null;
    private int activeEnvelopeCount = 0;
    private int beginTransitionCount = 0;
    private int endTransitionCount = 0;

    /** Workflow that this step belongs to */
    private Workflow workflow = null;
    /** List of transitions which begin at this step */
    private TransitionList beginTransitions = null;
    /** List of transitions which end at this step */
    private TransitionList endTransitions = null;
    /** Include begin transitions in XML output */
    private boolean isIncludeBeginTransitions = false;

    private boolean isLoaded = false;
    /** user currently manipulating workflow */
    private User user = null;
    private DBBean db = new DBBean();

    /** Indicates whether the step may be removed.  This is set to true only
     * when the appropriate checks have been performed */
    private boolean isRemovePermitted = false;
    /** Indicates whether the step removal was successful or not. */
    private boolean isRemoveSuccessful = false;

    /**
     * Creates a new workflow Step
     */
    public Step() {
    }

    /**
     * Return the step id
     * @return the step id
     */
    public String getID() {
        return this.stepID;
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
    public void setName(String name) {
        this.name = (name == null)? null : name.trim();
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
    public void setDescription(String description) {
        this.description = description;
    }


    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }



    /**
     * Getter for property notes.
     * @return Value of property notes.
     */
    public String getNotes() {
        return this.notes;
    }

    /**
     * Setter for property notes.
     * @param notes New value of property notes.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Getter for property finalStep.
     * @return Value of property finalStep.
     */
    public boolean isFinalStep() {
        return this.isFinalStep;
    }

    /**
     * Setter for property finalStep.
     * @param isFinalStep New value of property finalStep.
     */
    public void setFinalStep(boolean isFinalStep) {
        this.isFinalStep = isFinalStep;
    }

    /**
     * Getter for property initialStep.
     * @return Value of property initialStep.
     */
    public boolean isInitialStep() {
        return this.isInitialStep;
    }

    /**
     * Setter for property initialStep.
     * @param isInitialStep New value of property initialStep.
     */
    public void setInitialStep(boolean isInitialStep) {
        this.isInitialStep = isInitialStep;
    }

    public String getEntryStatusID() {
        return this.entryStatusID;
    }

    public void setEntryStatusID(String entryStatusID) {
        this.entryStatusID = entryStatusID;
    }

    public String getSubscriptionID() {
        return this.subscriptionID;
    }

    public void setSubscriptionID(String subscriptionID) {
        this.subscriptionID = subscriptionID;
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

    public String getEntryStatusName() {
        return this.entryStatusName;
    }

    void setEntryStatusName(String entryStatusName) {
        this.entryStatusName = entryStatusName;
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

    public int getActiveEnvelopeCount() {
        return this.activeEnvelopeCount;
    }

    void setActiveEnvelopeCount(int activeEnvelopeCount) {
        this.activeEnvelopeCount = activeEnvelopeCount;
    }

    public int getBeginTransitionCount() {
        return this.beginTransitionCount;
    }

    void setBeginTransitionCount(int beginTransitionCount) {
        this.beginTransitionCount = beginTransitionCount;
    }

    public int getEndTransitionCount() {
        return this.endTransitionCount;
    }

    void setEndTransitionCount(int endTransitionCount) {
        this.endTransitionCount = endTransitionCount;
    }

    /**
     * Return the workflow to which this step belongs
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
     * Return the transitions that begin at this step
     * @return the list of transitions
     * @throws PersistenceException if there is a problem loading the transitions
     */
    public TransitionList getBeginTransitions() throws PersistenceException {
        if (this.beginTransitions == null) {
            loadBeginTransitions();
        }
        return this.beginTransitions;
    }

    void setBeginTransitions(TransitionList beginTransitions) {
        this.beginTransitions = beginTransitions;
    }

    /**
     * Return the transitions that end at this step
     * @return the list of transitions
     * @throws PersistenceException if there is a problem loading the transitions
     */
    public TransitionList getEndTransitions() throws PersistenceException {
        if (this.endTransitions == null) {
            loadEndTransitions();
        }
        return this.endTransitions;
    }

    void setEndTransitions(TransitionList endTransitions) {
        this.endTransitions = endTransitions;
    }

    /**
     * Indicates whether begin transitions should be included in XML output.
     */
    public boolean getIncludeBeginTransitions() {
        return this.isIncludeBeginTransitions;
    }

    void setIncludeBeginTransitions(boolean isIncludeBeginTransitions) {
        this.isIncludeBeginTransitions = isIncludeBeginTransitions;
    }


    /**
     * Clear all properties.
     */
    public void clear() {
        setID(null);
        setWorkflowID(null);
        setName(null);
        setSequence(null);
        setDescription(null);
        setNotes(null);
        setFinalStep(false);
        setInitialStep(false);
        setEntryStatusID(null);
        setSubscriptionID(null);

        setCreatedBy(null);
        setCreatedDatetime(null);
        setModifiedBy(null);
        setModifiedDatetime(null);
        setCrc(null);
        setRecordStatus(null);

        setWorkflowName(null);
        setEntryStatusName(null);
        setCreatedByFullName(null);
        setModifiedByFullName(null);
        setActiveEnvelopeCount(0);
        setBeginTransitionCount(0);
        setEndTransitionCount(0);

        setWorkflow(null);
        setBeginTransitions(null);
        setEndTransitions(null);
        setIncludeBeginTransitions(false);

        setLoaded(false);
    }

    /**
     * Set the current user.  This is required before manipulating workflow.
     * It is used to stamp the creation/modification userid in the database.
     * @param user the user manipulating the workflow.
     */
    public void setUser(User user) {
        this.user = user;
    }

    User getUser() {
        return this.user;
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

    /**********************************************************************************
     *  Implementing IJDBCPersistance
     **********************************************************************************/
    public void setID(java.lang.String stepID) {
        this.stepID = stepID;
    }
    
    //Avinash: -- setter for stepID-------------------------------------------
    	public void setStepID(java.lang.String stepID) {
    		this.stepID = stepID;
    	}
    //Avinash-----------------------------------------------------------------

    /**
     * Load the Step from the underlying JDBC object.
     *
     * @throws PersistenceException Thrown to indicate a failure loading from
     * the database, a system-level error.
     */
    public void load() throws net.project.persistence.PersistenceException {
        StringBuffer queryBuff = new StringBuffer();

        queryBuff.append("select step_id, workflow_id, workflow_name, ");
        queryBuff.append("step_name, step_description, notes_clob, is_initial_step, ");
        queryBuff.append("is_final_step, entry_status_id, entry_status_name, subscription_id, created_by_id, created_datetime, ");
        queryBuff.append("created_by_full_name, modified_by_id, modified_datetime, ");
        queryBuff.append("modified_by_full_name, crc, record_status, step_sequence, ");
        queryBuff.append("begin_transition_count, end_transition_count ");
        queryBuff.append("from pn_workflow_step_view ");
        queryBuff.append("WHERE step_id = " + getID() + " ");

        try {
            // Execute select statement
            db.executeQuery(queryBuff.toString());

            if (db.result.next()) {
                setID(db.result.getString("step_id"));
                setWorkflowID(db.result.getString("workflow_id"));
                setWorkflowName(db.result.getString("workflow_name"));
                setName(db.result.getString("step_name"));
                setSequence(db.result.getString("step_sequence"));
                setDescription(db.result.getString("step_description"));
                setNotes(ClobHelper.read(db.result.getClob("notes_clob")));
                setInitialStep(Conversion.toBoolean(db.result.getString("is_initial_step")));
                setFinalStep(Conversion.toBoolean(db.result.getString("is_final_step")));
                setEntryStatusID(db.result.getString("entry_status_id"));
                setEntryStatusName(db.result.getString("entry_status_name"));
                setSubscriptionID(db.result.getString("subscription_id"));
                setCreatedBy(db.result.getString("created_by_id"));
                setCreatedDatetime(db.result.getTimestamp("created_datetime"));
                setCreatedByFullName(db.result.getString("created_by_full_name"));
                setModifiedBy(db.result.getString("modified_by_id"));
                setModifiedDatetime(db.result.getTimestamp("modified_datetime"));
                setModifiedByFullName(db.result.getString("modified_by_full_name"));
                setCrc(db.result.getTimestamp("crc"));
                setRecordStatus(db.result.getString("record_status"));
                setBeginTransitionCount(db.result.getInt("begin_transition_count"));
                setEndTransitionCount(db.result.getInt("end_transition_count"));
                setLoaded(true);
            }

        } catch (SQLException sqle) {
            setLoaded(false);
            Logger.getLogger(Step.class).error("Step.load() threw an SQL exception: " + sqle);
            throw new PersistenceException("Step load operation failed.", sqle);

        } finally {
            db.release();
        }

    } // load()

    /**
     * Load the workflow to which this step belongs
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
     * Load the transitions for which this step is the begin step
     * Sets attribute beginTransitions
     * @throws PersistenceException if there is a problem loading the transitions
     * @see #beginTransitions
     */
    private void loadBeginTransitions() throws PersistenceException {
        TransitionList transitions = null;
        StringBuffer whereClause = new StringBuffer();
        whereClause.append("begin_step_id = " + getID());
        transitions = getTransitions(whereClause.toString());
        setBeginTransitions(transitions);
    }

    /**
     * Load the transitions for which this step is the end step
     * Sets attribute endTransitions
     * @throws PersistenceException if there is a problem loading the transitions
     * @see #endTransitions
     */
    private void loadEndTransitions() throws PersistenceException {
        TransitionList transitions = null;
        StringBuffer whereClause = new StringBuffer();
        whereClause.append("end_step_id = " + getID());
        transitions = getTransitions(whereClause.toString());
        setEndTransitions(transitions);
    }

    /**
     * Get the transitions
     * @param whereClause where clause (excluding AND etc.) limiting transitions
     * to the appropriate begin or end step id
     * Uses its own DBBean so it may be called without affecting another transaction
     * @return list of transitions
     * @throws PersistenceException if there is a problem loading the transitions
     */
    private TransitionList getTransitions(String whereClause) throws PersistenceException {
        DBBean myDb = new DBBean();

        TransitionList transitionList = new TransitionList();
        Transition tran = null;

        StringBuffer queryBuff = new StringBuffer();
        queryBuff.append("select transition_id, workflow_id, workflow_name, ");
        queryBuff.append("transition_verb, transition_description, begin_step_id, ");
        queryBuff.append("begin_step_name, end_step_id, end_step_name, ");
        queryBuff.append("created_by_id, created_by_full_name, created_datetime, ");
        queryBuff.append("modified_by_id, modified_by_full_name, modified_datetime, ");
        queryBuff.append("crc, record_status ");
        queryBuff.append("from pn_workflow_transition_view ");
        queryBuff.append("WHERE record_status = 'A' ");
        queryBuff.append("AND workflow_id = " + getWorkflowID() + " ");
        queryBuff.append("AND " + whereClause + " ");
        try {
            myDb.executeQuery(queryBuff.toString());
            while (myDb.result.next()) {
                tran = new Transition();
                tran.setID(myDb.result.getString("transition_id"));
                tran.setWorkflowID(myDb.result.getString("workflow_id"));
                tran.setTransitionVerb(myDb.result.getString("transition_verb"));
                tran.setDescription(myDb.result.getString("transition_description"));
                tran.setBeginStepID(myDb.result.getString("begin_step_id"));
                tran.setEndStepID(myDb.result.getString("end_step_id"));
                tran.setCreatedBy(myDb.result.getString("created_by_id"));
                tran.setCreatedDatetime(myDb.result.getTimestamp("created_datetime"));
                tran.setModifiedBy(myDb.result.getString("modified_by_id"));
                tran.setModifiedDatetime(myDb.result.getTimestamp("modified_datetime"));
                tran.setCrc(myDb.result.getTimestamp("crc"));
                tran.setRecordStatus(myDb.result.getString("record_status"));
                tran.setWorkflowName(myDb.result.getString("workflow_name"));
                tran.setBeginStepName(myDb.result.getString("begin_step_name"));
                tran.setEndStepName(myDb.result.getString("end_step_name"));
                tran.setCreatedByFullName(myDb.result.getString("created_by_full_name"));
                tran.setModifiedByFullName(myDb.result.getString("modified_by_full_name"));
                tran.setLoaded(true);
                transitionList.add(tran);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(Step.class).error("WorkflowManager.loadTransitions() threw an SQL exception: " + sqle);
            throw new PersistenceException("WorkflowManager load transitions operation failed.", sqle);
        } finally {
            myDb.release();
        }

        return transitionList;
    }

    /**
     * Save the Step to the underlying JDBC object.
     * Note: after a successful store this object's properties are cleared
     * and the stepID is set such that the load() method may be called.
     * @throws PersistenceException Thrown to indicate a failure storing to the
     * database, a system-level error.
     */
    public void store() throws net.project.persistence.PersistenceException {

        String storedStepID;

        if (isLoaded()) {
            // Modify existing step

            DBBean db = new DBBean();

            try {
                int index = 0;
                int notesClobIndex = 0;

                db.setAutoCommit(false);

                db.prepareCall("{call workflow.modify_step(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
                db.cstmt.setString(++index, getID());
                db.cstmt.setString(++index, getName());
                db.cstmt.setString(++index, getSequence());
                db.cstmt.setString(++index, getDescription());
                db.cstmt.setInt(++index, (isInitialStep() ? 1 : 0));
                db.cstmt.setInt(++index, (isFinalStep() ? 1 : 0));
                db.cstmt.setString(++index, getEntryStatusID());
                db.cstmt.setString(++index, getSubscriptionID());
                db.cstmt.setString(++index, getUser().getID());
                db.cstmt.setTimestamp(++index, new Timestamp(getCrc().getTime()));
                db.cstmt.setInt(++index, (getNotes() == null ? 1 : 0));
                db.cstmt.registerOutParameter((notesClobIndex = ++index), java.sql.Types.CLOB);

                // Execute that sucker
                db.executeCallable();

                if (getNotes() != null) {
                    // If we had notes, then stream them to the returned clob locater
                    ClobHelper.write(db.cstmt.getClob(notesClobIndex), getNotes());
                }

                storedStepID = getID();

                db.commit();

            } catch (SQLException sqle) {
                try {
                    db.rollback();
                } catch (SQLException e) {
                    // Throw original error then release
                }
                throw new PersistenceException("Step store operation failed: " + sqle, sqle);
            }
            finally {
                db.release();
            }

        } else {
            // Create new step
            try {

                int index = 0;
                int notesClobIndex = 0;
                int stepIDIndex = 0;

                db.setAutoCommit(false);

                db.prepareCall("{call workflow.create_step(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
                db.cstmt.setString(++index, getWorkflowID());
                db.cstmt.setString(++index, getName());
                db.cstmt.setString(++index, getSequence());
                db.cstmt.setString(++index, getDescription());
                db.cstmt.setInt(++index, (isInitialStep() ? 1 : 0));
                db.cstmt.setInt(++index, (isFinalStep() ? 1 : 0));
                db.cstmt.setString(++index, getEntryStatusID());
                db.cstmt.setString(++index, getSubscriptionID());
                db.cstmt.setString(++index, getUser().getID());
                db.cstmt.setInt(++index, (getNotes() == null ? 1 : 0));
                db.cstmt.registerOutParameter((notesClobIndex = ++index), java.sql.Types.CLOB);
                db.cstmt.registerOutParameter((stepIDIndex = ++index), java.sql.Types.INTEGER);

                // Execute that sucker
                db.executeCallable();

                if (getNotes() != null) {
                    // If we had notes, then stream them to the returned clob locater
                    ClobHelper.write(db.cstmt.getClob(notesClobIndex), getNotes());
                }

                // Get newly created stepID
                storedStepID = db.cstmt.getString(stepIDIndex);

                db.commit();

            } catch (SQLException sqle) {
                try {
                    db.rollback();
                } catch (SQLException e) {
                    // Throw original error then release
                }
                throw new PersistenceException("Step store operation failed: " + sqle, sqle);

            } finally {
                db.release();
            }

        } // if (isLoaded)

        /*
            Since the current properties are out of date (the stored procedure will
            have inserted values for modified date time etc.) I will clear the
            object then ste the stepID such that it can be loaded later.
        */
        clear();
        setID(storedStepID);
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
                errors.put("remove_is_published", null, "A step may not be removed while the workflow to which is belongs is Published.");
                isMetCriteria &= false;
            }

        } catch (PersistenceException pe) {
            errors.put("remove_is_published", null, "There was a problem locating the workflow to which this step belongs.");
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
     * Delete the Step from the database.
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
            removeStep();
            setRemoveSuccessful(true);
        }
    }

    /**
     * Performs the actual remove
     * @throws PersistenceException if there is a problem performing the remove
     */
    private void removeStep() throws PersistenceException {
        /* errorCode will be returned from stored procedures */
        int errorCode = -1;

        /*
            procedure remove_step (
                i_step_id        in varchar2,
                i_modified_by_id in varchar2,
                i_crc            in date,
                o_return_value   out number);
        */
        try {
            db.prepareCall("{call workflow.remove_step(?, ?, ?, ?)}");
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
                   Successfully removed the step
                   Now remove the transitions for which this step is the end step
                 */
                removeEndTransitions();

                db.connection.commit();
                errorCode = DBErrorCodes.OPERATION_SUCCESSFUL;
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(Step.class).error(
                "Step.remove(): User: " + getUser().getID() + ", unable to execute stored procedure: " +
                sqle);
            throw new PersistenceException("Step remove operation failed.", sqle);

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
            DBExceptionFactory.getException("Step.remove()", errorCode);

        } catch (net.project.base.UpdateConflictException e) {
            RecordModifiedException rme =
                new RecordModifiedException("The step has been modified by another user.  Please try again.");
            rme.setErrorCode(STEP_RECORD_MODIFIED);
            throw rme;
        } catch (net.project.persistence.RecordLockedException e) {
            WorkflowPersistenceException wpe =
                new WorkflowPersistenceException("The step is currently locked by another user.  Please try again.");
            wpe.setErrorCode(STEP_RECORD_LOCKED);
            throw wpe;

        } catch (PnetException e) {
            throw new PersistenceException(e.getMessage(), e);
        }

    }

    /**
     * Remove the transitions for which this step is the end step
     * This batches up calls to workflow.remove_transition based on the
     * transitions in this.endTransitions<br>
     * DOES NOT COMMIT OR ROLLBACK
     * @throws SQLException if there is a problem
     * @throws PersistenceException if there was a problem getting the endTransitions
     * @see #endTransitions
     */
    private void removeEndTransitions() throws SQLException, PersistenceException {
        Iterator it = null;
        Transition tran = null;
        TransitionList transitions = getEndTransitions();

        /*
            procedure remove_transition (
                i_transition_id  in varchar2,
                i_modified_by_id in varchar2,
                i_crc            in date);
        */
        try {
            db.prepareCall("{call workflow.remove_transition(?, ?, ?)}");
            it = transitions.iterator();
            while (it.hasNext()) {
                tran = (Transition)it.next();
                db.cstmt.setString(1, tran.getID());
                db.cstmt.setString(2, getUser().getID());
                db.cstmt.setTimestamp(3, new Timestamp(tran.getCrc().getTime()));
                db.cstmt.addBatch();
            }
            db.cstmt.executeBatch();

        } finally {
            /* Close the statement we opened */
            db.closeCStatement();
        }

    }

    /*
        Implement IXMLPersistence
     */

    /**
     * Converts the step into an XML string including the XML version
     * tag.
     * @return the XML string
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
     * Returns step XML body without XML version tag.
     * @return XML body as string
     */
    public java.lang.String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<step>\n");
        xml.append(getXMLElements());
        xml.append("</step>\n");

        return xml.toString();
    }

    /**
     * Returns the basic step XML elements
     * @return XML elements as string
     */
    private String getXMLElements() {
	StringBuffer xml = new StringBuffer();
	DateFormat dt = new DateFormat(this.user);

	xml.append("<step_id>" + XMLUtils.escape(this.stepID) + "</step_id>\n");
	xml.append("<step_sequence>" + XMLUtils.escape(this.sequence)
		+ "</step_sequence>\n");
	xml.append("<workflow_id>" + XMLUtils.escape(this.workflowID)
		+ "</workflow_id>\n");
	xml.append("<workflow_name>" + XMLUtils.escape(this.workflowName)
		+ "</workflow_name>\n");
	xml.append("<name>" + XMLUtils.escape(this.name) + "</name>\n");
	xml.append("<description>" + XMLUtils.escape(this.description)
		+ "</description>\n");
	xml.append("<notes>" + XMLUtils.escape(this.notes) + "</notes>\n");
	xml.append("<is_initial_step>"
		+ XMLUtils.escape((this.isInitialStep ? "1" : "0"))
		+ "</is_initial_step>\n");
	xml.append("<is_final_step>"
		+ XMLUtils.escape((this.isFinalStep ? "1" : "0"))
		+ "</is_final_step>\n");
	xml.append("<entry_status_id>" + XMLUtils.escape(this.entryStatusID)
		+ "</entry_status_id>");
	xml.append("<entry_status_name>"
		+ XMLUtils.escape(this.entryStatusName)
		+ "</entry_status_name>");
	xml.append("<subscription_id>" + XMLUtils.escape(this.subscriptionID)
		+ "</subscription_id>");
	xml.append("<created_by_id>" + XMLUtils.escape(this.createdBy)
		+ "</created_by_id>\n");
	xml.append("<created_datetime>"
		+ XMLUtils.escape(dt.formatDate(this.createdDatetime))
		+ "</created_datetime>\n");
	xml.append("<created_by_full_name>"
		+ XMLUtils.escape(this.createdByFullName)
		+ "</created_by_full_name>\n");
	xml.append("<modified_by_id>" + XMLUtils.escape(this.modifiedBy)
		+ "</modified_by_id>\n");
	xml.append("<modified_datetime>"
		+ XMLUtils.escape(dt.formatDate(this.modifiedDatetime))
		+ "</modified_datetime>\n");
	xml.append("<modified_by_full_name>"
		+ XMLUtils.escape(this.modifiedByFullName)
		+ "</modified_by_full_name>\n");
	xml.append("<crc>" + XMLUtils.escape(dt.formatDate(this.crc))
		+ "</crc>");
	xml.append("<record_status>" + XMLUtils.escape(this.recordStatus)
		+ "</record_status>");
	xml.append("<begin_transition_count>"
		+ XMLUtils.escape("" + this.beginTransitionCount)
		+ "</begin_transition_count>\n");
	xml.append("<end_transition_count>"
		+ XMLUtils.escape("" + this.endTransitionCount)
		+ "</end_transition_count>\n");

	if (getIncludeBeginTransitions()) {
	    try {
		TransitionList transitions = getBeginTransitions();
		xml.append(transitions.getXMLBody());
	    } catch (PersistenceException pe) {
	    	Logger.getLogger(Step.class).error(
			"Error getting transitions for Step XML: " + pe);
	    }
	}

	return xml.toString();
    }

    /*
         * =================================================================================================
         * Error stuff
         * ================================================================================================
         */

    private ValidationErrors errors = new ValidationErrors();

    public void clearErrors() {
        errors.clearErrors();
    }
    
    /**
     * Validates that all information in this Step is suitable for storing.
     * 
     * @since 8.2.0
     */
    public void validate(){
	validateName();
	validateDescription();
	validateSequence();
    }

    /**
     * Validate the contents of the Envelope
     */
    public void validateAll(){
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
    private void validateName() {
        if (getName() == null || getName().equals("")) {
            errors.put("name", PropertyProvider.get("prm.workflow.stepedit.name.label"), PropertyProvider.get("prm.workflow.requiredfield.validation.message"));
        }
    }

    private void validateSequence() {
        // Blank
        if (Validator.isBlankOrNull(getSequence())) {
            errors.put("sequence", PropertyProvider.get("prm.workflow.stepedit.sequence.label"), PropertyProvider.get("prm.workflow.requiredfield.validation.message"));
        }
        else {
            // make sure the sequence is a valid integer.  User's Locale used.
                //TODO: Perform proper interger validation.  This needs to be locale-based, however
                //TODO: the NumberFormat classes do not provide a parser that will throw exceptions for text such as "123a".
        	try{
                Number number = NumberFormat.getInstance().parseInteger(getSequence());
                Logger logger = Logger.getLogger(Step.class);
                logger.log(Priority.DEBUG, "WORKFLOW STEP: Sequence string is: " + getSequence() + " conversion is: " + (number !=null ? ""+number.intValue() : null));
                // Sequence must be a positve integer; zero allowed.
                if (number == null || number.intValue() < 0)
                    errors.put("sequence", PropertyProvider.get("prm.workflow.stepedit.sequence.label"), PropertyProvider.get("prm.workflow.step.sequence.integerrequired.validation.message"));
                else
                	this.sequence=""+number.intValue();
            }catch(ParseException ee){
            	errors.put("sequence", PropertyProvider.get("prm.workflow.stepedit.sequence.label"), PropertyProvider.get(ee.getMessage())+" '"+getSequence()+"'");
            }
        }
      }

    private void validateDescription() {
        if (getDescription() != null && getDescription().length() > 500) {
            errors.put("description", PropertyProvider.get("prm.workflow.stepedit.description.label"), PropertyProvider.get("prm.workflow.description.toomanychars.validation.message"));
        }
    }

    private void validateEntryStatusID() {
        /* No validation required */
    }

    private void validateNotes() {
        // No validation required
    }

    /**
     * Generates an error if the workflow to which this step belongs is published
     * @throws PersistenceException if there was a problem determining the workflow
     */
    public void validateUnpublished() throws PersistenceException {
        Workflow workflow = null;
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
