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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import net.project.base.ObjectType;
import net.project.base.property.PropertyProvider;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.notification.INotificationEvent;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.space.Space;
import net.project.util.Conversion;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class EnvelopeManager implements java.io.Serializable, ErrorCodes {
    /** Current user **/
    private User user = null;
    /** Current space **/
    private Space space = null;
    private DBBean db;

    /** Any problems that occurred when checking a rule */
    ArrayList ruleProblems = null;

    /** Last transition performed - so that we can give feedback to user */
    Transition lastTransition = null;

    public EnvelopeManager() {
        this.db = new DBBean();
    }

    /**
     * Set the current user
     * @param user the current user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Set the current space
     * @param space the current space
     */
    public void setSpace(Space space) {
        this.space = space;
    }


    /**
     * Set the last transition that occurred
     */
    void setLastTransition(Transition lastTransition) {
        this.lastTransition = lastTransition;
    }

    /**
     * Get the last transition that occurred
     * @return the last transition
     */
    Transition getLastTransition() {
        return this.lastTransition;
    }

    /**
     * Return envelopes based on criteria for a specified person
     * @param personID the person to get envelopes for.  A person "belongs" to
     * an envelope if that person is the member of a group which participates at a step
     * and the envelope is at that step
     * @param workflowID workflow to filter on. A value of null excludes this criteria.
     * @param isInactiveIncluded true means include inactive envelopes too
     * @param priorityID priority to filter on. A value of null excludes this criteria.
     * @return the envelopes
     * @throws PersistenceException if there is a problem getting the envelopes
     */
    public EnvelopeList getEnvelopesForParticipant(
        String personID, String workflowID, boolean isInactiveIncluded, String priorityID)
        throws PersistenceException {

        return getEnvelopes("pn_envelope_has_person_view",
            getEnvelopeWhereClause(personID, workflowID, isInactiveIncluded, priorityID, null));
    }

    /**
     * Return envelopes based on criteria for a specified object
     * @param objectID the object to get envelopes for.  An object belongs to an
     * envelope if it exists in that envelope for any version of the envelope.
     * A value of null excludes this criteria.
     * @param workflowID workflow to filter on. A value of null excludes this criteria.
     * @param isInactiveIncluded true means include inactive envelopes too
     * @param priorityID priority to filter on. A value of null excludes this criteria.
     * @return the envelopes
     * @throws PersistenceException if there is a problem getting the envelopes
     */
    public EnvelopeList getEnvelopesForObject(String objectID, String workflowID,
        boolean isInactiveIncluded, String priorityID)
        throws PersistenceException {

        return getEnvelopes("pn_envelope_has_object_view",
            getEnvelopeWhereClause(null, workflowID, isInactiveIncluded, priorityID, objectID));
    }


    /**
     * Return envelopes based on criteria.
     *
     * @param workflowID workflow to filter on. A value of null excludes this criteria.
     * @param isInactiveIncluded true means include inactive envelopes too
     * @param priorityID priority to filter on. A value of null excludes this criteria.
     * @return the envelopes
     * @throws PersistenceException if there is a problem getting the envelopes
     */
    public EnvelopeList getAllEnvelopes(String workflowID, boolean isInactiveIncluded, String priorityID)
        throws PersistenceException {
        return getEnvelopes("pn_workflow_envelope_view",
            getEnvelopeWhereClause(null, workflowID, isInactiveIncluded, priorityID, null));
    }


    /**
     * Construct where clause (not including "WHERE" statement) based on criteria.<br>
     * Note - It is up to the calling routine to specify values of null for criteria
     * which will not be valid against the table / view this where clause will be
     * used against.
     * @param personID the person to get envelopes for.  A value of null excludes this criteria.
     * @param workflowID workflow to filter on.  A value of null excludes this criteria.
     * @param isInactiveIncluded true means include inactive envelopes too<br>
     * false means include only active envelopes
     * @param priorityID priority to filter on. A value of null excludes this criteria.
     * @param objectID object to filter on.  A value of null excludes this criteria.
     * @return the where clause (EXCLUDING "WHERE" statement)
     */
    private String getEnvelopeWhereClause(
        String personID, String workflowID, boolean isInactiveIncluded,
        String priorityID, String objectID) {

        StringBuffer whereClause = new StringBuffer("");
        if (personID != null) {
            if (!whereClause.toString().equals("")) whereClause.append("AND ");
            whereClause.append("person_id = '" + personID + "' ");
        }
        if (workflowID != null) {
            if (!whereClause.toString().equals("")) whereClause.append("AND ");
            whereClause.append("workflow_id = '" + workflowID + "' ");
        }
        if (!isInactiveIncluded) {
            if (!whereClause.toString().equals("")) whereClause.append("AND ");
            whereClause.append("is_active = 1 ");
        }
        if (StringUtils.isNotEmpty(priorityID)) {
            if (!whereClause.toString().equals("")) whereClause.append("AND ");
            whereClause.append("current_priority_id = '" + priorityID + "' ");
        }
        if (objectID != null) {
            if (!whereClause.toString().equals("")) whereClause.append("AND ");
            whereClause.append("object_id = '" + objectID + "' ");
        }
        return whereClause.toString();
    }

    /**
     * Return a list of Envelopes based on the specified criteria
     * @param tableName the table to fetch from (or view)
     * @param whereClause the where clause to fetch on (not including "WHERE" statement)
     * @return the envelopes
     * @throws PersistenceException if there is a problem getting the envelopes
     */
    private EnvelopeList getEnvelopes(String tableName, String whereClause) throws PersistenceException {
        // List of Envelope objects
        EnvelopeList envelopeList = new EnvelopeList();
        // Temporary Envelope object
        Envelope env = null;
        // Temporary EnvelopeVersion object
        EnvelopeVersion envelopeVersion = null;

        StringBuffer queryBuff = new StringBuffer();

        // Build generic select on Workflow Envelope table
        queryBuff.append("SELECT envelope_id, workflow_id, strictness_id, strictness_name, ");
        queryBuff.append("strictness_description, current_version_id, envelope_name, envelope_description, ");
        queryBuff.append("created_by_id, created_by_full_name, created_datetime, modified_by_id, ");
        queryBuff.append("modified_by_full_name, modified_datetime, crc, record_status, ");
        queryBuff.append("is_active, current_step_id, current_step_name, ");
        queryBuff.append("current_step_description, current_step_notes_clob, current_status_id, current_status_name, ");
        queryBuff.append("current_status_description, last_transition_id, last_transition_verb, ");
        queryBuff.append("last_transition_description, current_priority_id, current_priority_name, ");
        queryBuff.append("current_priority_description, current_comments_clob, ");
        queryBuff.append("current_created_by_id, current_created_by_full_name, current_created_datetime, ");
        queryBuff.append("current_version_crc, current_version_record_status, ");
        queryBuff.append("space_id ");
        queryBuff.append("FROM " + tableName + " ");

        // Build customized WHERE clause
        queryBuff.append("WHERE record_status = 'A' ");
        if (whereClause != null && !whereClause.equals("")) {
            queryBuff.append("AND " + whereClause);
        }

        // Execute the query
        try {
            db.executeQuery(queryBuff.toString());

            while (db.result.next()) {
                // Create and populate workflow envelope
                env = new Envelope();
                env.setID(db.result.getString("envelope_id"));
                env.setWorkflowIDNoLoad(db.result.getString("workflow_id"));
                env.setStrictnessID(db.result.getString("strictness_id"));
                env.setStrictnessName(PropertyProvider.get(db.result.getString("strictness_name")));
                env.setStrictnessDescription(db.result.getString("strictness_description"));
                env.setCurrentVersionID(db.result.getString("current_version_id"));
                env.setName(db.result.getString("envelope_name"));
                env.setDescription(db.result.getString("envelope_description"));
                env.setCreatedBy(db.result.getString("created_by_id"));
                env.setCreatedByFullName(db.result.getString("created_by_full_name"));
                env.setCreatedDatetime(db.result.getTimestamp("created_datetime"));
                env.setModifiedBy(db.result.getString("modified_by_id"));
                env.setModifiedByFullName(db.result.getString("modified_by_full_name"));
                env.setModifiedDatetime(db.result.getTimestamp("modified_datetime"));
                env.setActive(Conversion.toBoolean(db.result.getString("is_active")));

                /* Now add a "current version" to the envelope */
                envelopeVersion = new EnvelopeVersion();
                envelopeVersion.setID(db.result.getString("current_version_id"));
                envelopeVersion.setEnvelopeID(db.result.getString("envelope_id"));
                envelopeVersion.setWorkflowID(db.result.getString("workflow_id"));
                envelopeVersion.setStepID(db.result.getString("current_step_id"));
                envelopeVersion.setStepName(db.result.getString("current_step_name"));
                envelopeVersion.setStepDescription(db.result.getString("current_step_description"));
                envelopeVersion.setStepNotes(ClobHelper.read(db.result.getClob("current_step_notes_clob")));
                envelopeVersion.setStatusID(db.result.getString("current_status_id"));
                envelopeVersion.setStatusName(PropertyProvider.get(db.result.getString("current_status_name")));
                envelopeVersion.setStatusDescription(db.result.getString("current_status_description"));
                envelopeVersion.setTransitionID(db.result.getString("last_transition_id"));
                envelopeVersion.setTransitionVerb(db.result.getString("last_transition_verb"));
                envelopeVersion.setTransitionDescription(db.result.getString("last_transition_description"));
                envelopeVersion.setPriorityID(db.result.getString("current_priority_id"));
                envelopeVersion.setPriorityName(db.result.getString("current_priority_name"));
                envelopeVersion.setPriorityDescription(db.result.getString("current_priority_description"));
                envelopeVersion.setComments(ClobHelper.read(db.result.getClob("current_comments_clob")));
                envelopeVersion.setCreatedBy(db.result.getString("current_created_by_id"));
                envelopeVersion.setCreatedByFullName(db.result.getString("current_created_by_full_name"));
                envelopeVersion.setCreatedDatetime(db.result.getTimestamp("current_created_datetime"));
                envelopeVersion.setCrc(db.result.getTimestamp("current_version_crc"));
                envelopeVersion.setRecordStatus(db.result.getString("current_version_record_status"));
                envelopeVersion.setLoaded(true);
                env.setCurrentVersion(envelopeVersion);

                env.setLoaded(true);
                envelopeList.add(env);

            }
        } catch (SQLException sqle) {
        	Logger.getLogger(EnvelopeManager.class).error("EnvelopeManager.getEnvelopes() threw an SQL exception: " + sqle);
            throw new PersistenceException("Envelope manager get envelopes operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

        return envelopeList;
    }

    /**
     * Indicates whether an object for the specified objectID is in an
     * active envelope
     * @param objectID the object id
     * @return true if the object is in an active envelope, false otherwise
     * @throws PersistenceException if there is a problem determining this
     */
    public boolean isObjectInActiveEnvelope(String objectID) throws PersistenceException {
        boolean isInEnvelope = false;   // Indicates object is in an active envelope

        StringBuffer queryBuff = new StringBuffer();
        queryBuff.append("SELECT envelope_id, object_id ");
        queryBuff.append("FROM pn_envelope_has_object_view ");
        queryBuff.append("WHERE record_status = 'A' ");
        queryBuff.append("AND object_id = '" + objectID + "' ");
        queryBuff.append("AND is_active = 1 ");

        try {
            db.executeQuery(queryBuff.toString());
            if (db.result.next()) {
                isInEnvelope = true;
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(EnvelopeManager.class).error("EnvelopeManager.isObjectInEnvelope() threw an SQL exception: " + sqle);
            throw new PersistenceException("Envelope manager isObjectInEnvelope operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

        return isInEnvelope;
    }

    /**
     * Indicates whether a user is at the current step of an envelope.  This would
     * mean they can view the envelope.
     * @param envelopeID the id of the envelope in question
     * @param personID the id of ther person
     * @return true if the user is at the envelope's current step, false otherwise
     * @throws PersistenceException if there is a problem determining this
     */
    public boolean isUserAtCurrentStep(String envelopeID, String personID) throws PersistenceException {
        boolean isUserAtCurrentStep = false;
        StringBuffer queryBuff = new StringBuffer();

        queryBuff.append("SELECT is_active ");
        queryBuff.append("FROM pn_envelope_has_person_view ");
        queryBuff.append("WHERE record_status = 'A' ");
        queryBuff.append("AND person_id = '" + personID + "' ");
        queryBuff.append("AND envelope_id = '" + envelopeID + "' ");

        // Execute the query
        try {
            db.executeQuery(queryBuff.toString());

            if (db.result.next()) {
                isUserAtCurrentStep = true;
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(EnvelopeManager.class).error("EnvelopeManager.isUserAtCurrentStep() threw an SQL exception: " + sqle);
            throw new PersistenceException("Envelope manager envelope read failed: " + sqle, sqle);

        } finally {
            db.release();

        }
        return isUserAtCurrentStep;
    }

    /**
     * Return list of published workflows, each of which permits the inclusion of
     * the types of all the objects in the table
     * @param objectList the hashtable of objects to be included in a workflow
     * if the table is empty then all published workflows are returned.
     * @return list of workflows
     * @throws NotWorkflowableException if one the objects is determined to be not workflowable
     */
    public WorkflowList getCompatibleWorkflowList(EnvelopeObjectList objectList)
        throws NotWorkflowableException {
        /* select all workflows
           where exists a workflow_has_object_type where the object_type is in
           list of object types deduced from objectTable
         */

        WorkflowList workflowList = new WorkflowList();
        Workflow wf = null;
        IWorkflowable workflowObj = null;
        Iterator it = null;
        String objectType = null;
        String subType = null;

        StringBuffer queryBuff = new StringBuffer();
        StringBuffer whereClause = new StringBuffer();
        StringBuffer objectTypeClause = null;

        queryBuff.append("SELECT wf.workflow_id, wf.workflow_name, wf.strictness_id, wf.workflow_description, wf.created_by_id, ");
        queryBuff.append("wf.notes, wf.created_datetime, wf.modified_by_id, wf.owner_id, wf.modified_datetime, wf.is_published, ");
        queryBuff.append("wf.is_generic, wf.crc, wf.record_status, ");
        queryBuff.append("wf.strictness_name, wf.owner_full_name, wf.created_by_full_name, wf.modified_by_full_name, ");
        queryBuff.append("wf.active_envelope_count, wf.space_id ");
        queryBuff.append("FROM pn_workflow_view wf ");

        whereClause.append("WHERE wf.record_status = 'A' ");
        whereClause.append("AND wf.space_id = " + this.space.getID() + " ");
        whereClause.append("AND wf.is_published = 1 ");

        /*
            Now add the part of the where clause which limits the workflows to
            the object types for each of the specified objects.
            For example
                AND (wf.is_generic = 1 OR
                    exits (select 1 from pn_workflow_has_object_type wft
                           where
                               wft.workflow_id = wf.workflow_id and
                               ( (wft.object_type = 'document')
                                  OR (wft.object_type = 'form' AND wft.sub_type_id = '123')
                               )
                          )
                    )
         */
        whereClause.append("AND (wf.is_generic = 1 ");

        it = objectList.iterator();
        while (it.hasNext()) {
            /* Get the workflow object then grab its type and sub type */
            workflowObj = ((EnvelopeObject)it.next()).getRealObject();
            objectType = workflowObj.getObjectType();
            subType = workflowObj.getSubType();

            /* Append to the clause */
            if (objectTypeClause == null) {
                objectTypeClause = new StringBuffer("");
            } else {
                objectTypeClause.append("OR ");
            }
            objectTypeClause.append("(wft.object_type = '" + objectType + "' ");
            if (subType != null && !subType.equals("")) {
                objectTypeClause.append("AND wft.sub_type_id = '" + subType + "' ");
            }
            objectTypeClause.append(") ");

        }
        whereClause.append("OR exists ( ");
        whereClause.append("select 1 ");
        whereClause.append("from pn_workflow_has_object_type wft ");
        whereClause.append("where wft.workflow_id = wf.workflow_id and ( ");
        whereClause.append(objectTypeClause);
        whereClause.append(") ) )");

        // Add WHERE clause to Query
        queryBuff.append(whereClause);

        // Execute the query
        try {
            db.executeQuery(queryBuff.toString());

            while (db.result.next()) {
                // Create and populate workflow
                wf = new Workflow();
                wf.setID(db.result.getString("workflow_id"));
                wf.setName(db.result.getString("workflow_name"));
                wf.setDescription(db.result.getString("workflow_description"));
                wf.setStrictnessID(db.result.getString("strictness_id"));
                wf.setNotes(db.result.getString("notes"));
                wf.setPublished(Conversion.toBoolean(db.result.getString("is_published")));
                wf.setOwnerID(db.result.getString("owner_id"));
                wf.setCreatedBy(db.result.getString("created_by_id"));
                wf.setCreatedDatetime(db.result.getTimestamp("created_datetime"));
                wf.setModifiedBy(db.result.getString("modified_by_id"));
                wf.setModifiedDatetime(db.result.getTimestamp("modified_datetime"));

                wf.setStrictnessName(PropertyProvider.get(db.result.getString("strictness_name")));
                wf.setOwnerFullName(db.result.getString("owner_full_name"));
                wf.setCreatedByFullName(db.result.getString("created_by_full_name"));
                wf.setModifiedByFullName(db.result.getString("modified_by_full_name"));
                wf.setActiveEnvelopeCount(db.result.getInt("active_envelope_count"));
                wf.setSpaceID(db.result.getString("space_id"));
                wf.setGeneric(Conversion.toBoolean(db.result.getString("is_generic")));

                // Add to our list
                wf.setLoaded(true);
                workflowList.add(wf);
            }


        } catch (SQLException sqle) {
        	Logger.getLogger(EnvelopeManager.class).error("EnvelopeManager.getCompatibleWorkflowList() threw an SQL exception: " + sqle);

        } finally {
            db.release();

        }

        return workflowList;
    }

    /**
     * Creates an envelope in the database based on the specified values.
     * Also writes history.
     * @param objectID the id of an object to add to the envelope
     * @param workflowID the workflow id of the workflow for which the envelope
     * is to be created.
     * @param name (optional) the name (title) of the envelope
     * @param description (optional) the description of the envelope
     * @param strictness the rule enforcement
     * @param priority the priority
     * @param status (optional) the initial status (may be overridden by the initial step)
     * @param comments (optional) the initial comments
     * @throws EnvelopeException if there is a problem creating the envelope
     * @throws SQLException 
     * @see net.project.workflow.Strictness
     * @see net.project.workflow.EnvelopePriority
     * @see net.project.workflow.Status
     */
    public void createEnvelope(String objectID, String workflowID, String name,
        String description, Strictness strictness, EnvelopePriority priority,
        Status status, String comments) throws EnvelopeException, SQLException {

        Envelope envelope = new Envelope();
        try {
            envelope.setUser(this.user);
            envelope.addObject(objectID);
            envelope.setWorkflowID(workflowID);
            if (name != null) envelope.setName(name);
            if (description != null) envelope.setDescription(description);
            envelope.setStrictnessID(strictness.getID());
            envelope.setPriorityID(priority.getID());
            if (status != null) envelope.setStatusID(status.getID());
            if (comments != null) envelope.setComments(comments);
            createEnvelope(envelope);

        } catch (NotWorkflowableException ne) {
            // thrown by addObject
            throw new EnvelopeException("Object with id '" + objectID + "' may not be added to a workflow", ne);

        }
    }

    /**
     * Creates an envelope in the database based on the envelope object.
     * This routine also writes the history.
     * @throws EnvelopeException if there is a problem storing the envelope
     * @throws SQLException 
     */
    public void createEnvelope(Envelope envelope) throws EnvelopeException, SQLException {

        try {
            /* Store envelope */
            envelope.store();
            /* Reload stored record to get additional properties (like created datetime) */
            envelope.load();
            /* Write the history for the step that the envelope has entered */
            writeStepHistory(envelope, envelope.getCreatedDatetime());

        } catch (PersistenceException pe) {
            // thrown by createEnvelope
            throw new EnvelopeException("Error creating envelope: " + pe, pe);
        }

        try {
            /* Now throw an event for notification purposes */
            enterStepEvents(envelope);

        } catch (PersistenceException pe) {
            throw new EnvelopeException("Error with notification for envelope: " + pe, pe);
        }

    }

    /**
     * Return list of history objects
     * @param envelopeID envelope to get history for
     * @return list of history
     * @throws PersistenceException if there is a problem reading the history
     */
    public HistoryList getHistoryList(String envelopeID) throws PersistenceException {
        HistoryList historyList = new HistoryList();
        History hist = null;

        StringBuffer queryBuff = new StringBuffer();

        queryBuff.append("select history_id, envelope_id, envelope_name, envelope_description, ");
        queryBuff.append("action_by_id, action_by_full_name, history_action_id, action_name, ");
        queryBuff.append("action_description, action_datetime, history_message_id, ");
        queryBuff.append("crc, record_status ");
        queryBuff.append("from pn_envelope_history_view ");
        queryBuff.append("WHERE record_status = 'A' ");
        queryBuff.append("AND envelope_id = " + envelopeID + " ");
        queryBuff.append("ORDER BY history_id ");

        // Execute the query
        try {
            db.executeQuery(queryBuff.toString());

            while (db.result.next()) {
                hist = new History();
                hist.setID(db.result.getString("history_id"));
                hist.setEnvelopeID(db.result.getString("envelope_id"));
                hist.setEnvelopeName(db.result.getString("envelope_name"));
                hist.setEnvelopeDescription(db.result.getString("envelope_description"));
                hist.setActionByID(db.result.getString("action_by_id"));
                hist.setActionByFullName(db.result.getString("action_by_full_name"));
                hist.setHistoryActionID(db.result.getString("history_action_id"));
                hist.setActionName(PropertyProvider.get(db.result.getString("action_name")));
                hist.setActionDescription(db.result.getString("action_description"));
                hist.setActionDatetime(db.result.getTimestamp("action_datetime"));
                hist.setHistoryMessageID(db.result.getString("history_message_id"));
                hist.setCrc(db.result.getTimestamp("crc"));
                hist.setRecordStatus(db.result.getString("record_status"));
                hist.setLoaded(true);
                // Force loading of history message now
                hist.getHistoryMessage();
                historyList.add(hist);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(EnvelopeManager.class).error("EnvelopeManager.getHistoryList() threw an SQL exception: " + sqle);
            throw new PersistenceException("Envelope manager get history list operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

        return historyList;
    }

    /**
     * Return list of available transitions for envelope.<br />
     * This returns the list of transitions for the envelope's workflow
     * that have a "begin_step" equal to the envelope's current step
     *
     * @param envelope the envelope
     * @return list of transitions
     * @throws PersistenceException if an error occurs while loading the transitions
     * from the database.
     */
    TransitionList getAvailableTransitions(Envelope envelope) throws PersistenceException {
        String currentStepID = null;
        String workflowID = null;
        Transition tran = null;
        TransitionList transitionList = new TransitionList();
        StringBuffer queryBuff = new StringBuffer();

        workflowID = envelope.getWorkflowID();
        try {
            currentStepID = envelope.getCurrentVersion().getStepID();
        } catch (LoadException le) {
            throw new PersistenceException("Get available transitions operation failed: " + le, le);
        }

        queryBuff.append("select transition_id, workflow_id, workflow_name, ");
        queryBuff.append("transition_verb, transition_description, begin_step_id, ");
        queryBuff.append("begin_step_name, end_step_id, end_step_name, ");
        queryBuff.append("created_by_id, created_by_full_name, created_datetime, ");
        queryBuff.append("modified_by_id, modified_by_full_name, modified_datetime, ");
        queryBuff.append("crc, record_status ");
        queryBuff.append("from pn_workflow_transition_view ");
        queryBuff.append("where record_status = 'A' ");
        queryBuff.append("and workflow_id = " + workflowID + " ");
        queryBuff.append("and begin_step_id = " + currentStepID + " ");

        try {
            db.executeQuery(queryBuff.toString());
            while (db.result.next()) {
                tran = new Transition();
                tran.setID(db.result.getString("transition_id"));
                tran.setWorkflowID(db.result.getString("workflow_id"));
                tran.setTransitionVerb(db.result.getString("transition_verb"));
                tran.setDescription(db.result.getString("transition_description"));
                tran.setBeginStepID(db.result.getString("begin_step_id"));
                tran.setEndStepID(db.result.getString("end_step_id"));
                tran.setCreatedBy(db.result.getString("created_by_id"));
                tran.setCreatedDatetime(db.result.getTimestamp("created_datetime"));
                tran.setModifiedBy(db.result.getString("modified_by_id"));
                tran.setModifiedDatetime(db.result.getTimestamp("modified_datetime"));
                tran.setCrc(db.result.getTimestamp("crc"));
                tran.setRecordStatus(db.result.getString("record_status"));

                tran.setWorkflowName(db.result.getString("workflow_name"));
                tran.setBeginStepName(db.result.getString("begin_step_name"));
                tran.setEndStepName(db.result.getString("end_step_name"));
                tran.setCreatedByFullName(db.result.getString("created_by_full_name"));
                tran.setModifiedByFullName(db.result.getString("modified_by_full_name"));

                tran.setLoaded(true);
                transitionList.add(tran);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(EnvelopeManager.class).error("EnvelopeManager.getAvailableTransitions() threw an SQL exception: " + sqle);
            throw new PersistenceException("Get available transitions operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

        return transitionList;
    }

    /**
     * Perform a transition on the envelope
     * @param envelope the envelope
     * @param transitionID the transition to perform
     * @param isIgnoreWarnings indicates that we must ignore warnings.  This means
     * a RuleCheckException will only be thrown if errors occur
     * @throws TransitionException if there is a problem performing the transition
     * @throws RuleCheckException if one or more rule checks fail
     * @throws SQLException 
     */
    public void performTransition(Envelope envelope, String transitionID, boolean isIgnoreWarnings)
        throws TransitionException, RuleCheckException, SQLException {
        Transition tran = null;         // Transition to be performed

        // Get the transition from the envelope
        tran = getTransition(envelope, transitionID);

        // Check the rules for the transition.  An exception is thrown if there are any problems
        checkRules(envelope, tran, isIgnoreWarnings);
        // If this line is reached, there are no problems

        // Set the step of the envelope to the end step of the transition to complete transition
        makeTransition(envelope, tran);

        try {
            // Now reload the evnvelopeto ensure that all cached properties are reloaded
            // For example, envelope versions list
            envelope.reload();

        } catch (PersistenceException pe) {
            TransitionException te = new TransitionException("Error performing transition: " + pe);
            te.setErrorCode(TRANSITION_ENVELOPE_LOAD_ERROR);
            throw te;

        }

        try {
            // Write history for transition occurring
            writeTransitionHistory(envelope, tran);
            // Write history for step that has been entered
            writeStepHistory(envelope, envelope.getModifiedDatetime());

        } catch (PersistenceException pe) {
            TransitionException te = new TransitionException("Error performing transition: " + pe);
            te.setErrorCode(TRANSITION_HISTORY_STORE_ERROR);
            throw te;
        }

        try {
            // Now throw an event for notification purposes
            enterStepEvents(envelope);

        } catch (PersistenceException pe) {
            TransitionException te = new TransitionException("Error performing transition: " + pe);
            te.setErrorCode(TRANSITION_NOTIFICATION_ERROR);
            throw te;
        }

        // Remember the transition for user feedback
        setLastTransition(tran);
    }

    /**
     * Get a transition object from an envelope based on the transitionID
     * @param envelope the envelope which has the transition
     * @param transitionID the transition ID for the transition to get
     * @throws TransitionException if there is a problem getting the transition
     */
    private Transition getTransition(Envelope envelope, String transitionID) throws TransitionException {
        Transition tran = null;
        /*
            Get transition from envelope's list of next transitions
         */
        try {
            tran = getAvailableTransitions(envelope).get(transitionID);
        } catch (PersistenceException pe) {
            TransitionException te = new TransitionException(
                "Unable to determine available transitions for envelope '" + envelope.getName() + "': " + pe);
            te.setErrorCode(TRANSITION_UNKNOWN);
            throw te;
        }
        if (tran == null) {
            TransitionException te = new TransitionException(
                "Transition with id '" + transitionID + "' not available for envelope '" + envelope.getName() + "'");
            te.setErrorCode(TRANSITION_NOT_AVAILABLE);
            throw te;
        }
        return tran;
    }

    /**
     * Set the envelope's step id to the end step of the transition, sets the envelope's
     * last transition id to that of the transition.
     * We then reload the envelope in order to populate fields such as ModifiedDatetime
     * which are required by the History.
     * @param envelope the envelope to set the step for
     * @param transition the transition to get the end step from
     * @throws TransitionException if there is a problem setting the step
     */
    private void makeTransition(Envelope envelope, Transition transition) throws TransitionException {
        envelope.setStepID(transition.endStepID);
        envelope.setTransitionID(transition.getID());
        try {
            envelope.store();
            envelope.load();

        } catch (PersistenceException pe) {
            TransitionException te = new TransitionException("Unable to perform transition: " + pe);
            te.setErrorCode(TRANSITION_ENVELOPE_STORE_ERROR);
            throw te;

        }
    }

    /**
     * Check the rules for the transition.
     * Sets this.ruleProblems if there were any problems
     * @param envelope the envelope
     * @param transition the transition whose rules we are checking
     * @param isIgnoreWarnings indicates that we must ignore warnings.  This means
     * a RuleCheckException will only be thrown if errors occur
     * @throws TransitionException if there is a fatal problem checking the rules
     * @throws RuleCheckException if one or more rule checks has problems.
     * this.ruleProblems will contain a list of those problems
     */
    private void checkRules(Envelope envelope, Transition transition, boolean isIgnoreWarnings)
        throws TransitionException, RuleCheckException {

        Rule.RuleContext context = null;        // The information passed to the rule
        boolean hasRuleProblems = false;        // There are rule problems
        RuleList ruleList = null;               // The list of rules to check
        Rule rule = null;                       // A rule to check
        Iterator it = null;

        this.ruleProblems = new ArrayList();
        /*
            Get and check the rules at this transition
         */
        try {
            ruleList = transition.getRuleList();
        } catch (PersistenceException pe) {
            TransitionException te = new TransitionException(
                "Unable to perform transition: " + pe);
            te.setErrorCode(TRANSITION_RULE_ERROR);
            throw te;
        }

        /* Create the context to pass to each rule */
        context = new Rule.RuleContext();
        context.setUser(this.user);
        context.setEnvelope(envelope);
        context.setTransition(transition);

        /* Loop over each rule and check it */
        it = ruleList.iterator();
        while (it.hasNext()) {
            rule = (Rule)it.next();

            /* Check the rule */
            try {
                rule.check(context);

            } catch (RuleException re) {
                TransitionException te = new TransitionException(
                    "Unable to perform transition: " + re);
                te.setErrorCode(TRANSITION_RULE_ERROR);
                throw te;
            }

            if (rule.isProblem()) {
                this.ruleProblems.add(rule.getProblemInfo());
                hasRuleProblems = true;
            }
        }

        /*
            Now determine whether to throw a RuleCheckException
            This happens if there is at least one problem.
            However, if we are ignoring warnings and there are no errors, no exception is thrown
         */
        if (hasRuleProblems) {
            if (isIgnoreWarnings) {
                /* Throw an exception at the first error we come across */
                Rule.RuleProblem problem = null;
                it = this.ruleProblems.iterator();
                while (it.hasNext()) {
                    problem = (Rule.RuleProblem)it.next();
                    if (problem.isError()) {
                        throw new RuleCheckException("A problem occurred while checking rules");
                    }
                }
            } else {
                /* We got errors, throw an exception */
                throw new RuleCheckException("A problem occurred while checking rules");
            }
        }

        /* Done checking rules */
    }

    /**
     * Write the history for the transition
     * @param envelope the envelope for which the transition is occurring
     * @param transition the transition which is occurring
     * @throws PersistenceException if there is a problem writing the history
     */
    private void writeTransitionHistory(Envelope envelope, Transition transition) throws PersistenceException {
        /*
            Write history for transition
          */
        History history = new History();
        history.setEnvelopeID(envelope.getID());
        history.setHistoryAction(HistoryAction.PERFORMED_TRANSITION);
        history.setActionByID(this.user.getID());
        history.setActionDatetime(envelope.getModifiedDatetime());
        history.appendHistoryMessage(transition.getXMLBody());
        history.setUser(this.user);
        history.store();
    }

    /**
     * Write the history for entering a step.  The history is written based
     * on the current step of the envelope.
     * @param envelope the envelope which has entered a step
     * @param actionDatetime the action date and time
     * @throws PersistenceException if there is a problem writing the history
     */
    private void writeStepHistory(Envelope envelope, java.util.Date actionDatetime) throws PersistenceException {
        /* Load current step for envelope */
        Step step = new Step();
        step.setID(envelope.getStepID());
        step.load();

        History history = new History();
        history.setEnvelopeID(envelope.getID());
        history.setHistoryAction(HistoryAction.ENTERED_STEP);
        history.setActionByID(this.user.getID());
        history.setActionDatetime(actionDatetime);
        history.appendHistoryMessage(step.getXMLBody());
        history.setUser(this.user);
        history.store();
    }

    /**
     * Write the history for the notification
     * @param envelope the envelope on which notification was based
     * @param event the event which was created for the purposes of notification
     * @param actionDatetime the action date and time
     * @throws PersistenceException if there is a problem writing the history
     * @throws SQLException 
     */
    private void writeNotificationHistory(Envelope envelope, INotificationEvent event, java.util.Date actionDatetime) throws PersistenceException, SQLException {
        History history = new History();
        history.setEnvelopeID(envelope.getID());
        history.setHistoryAction(HistoryAction.SENT_NOTIFICATION);
        history.setActionByID(this.user.getID());
        history.setActionDatetime(actionDatetime);
        history.appendHistoryMessage(event.getXMLBody());
        history.setUser(this.user);
        history.store();
    }

    /**
     * Throws an event indicating an envelope has entered a step.
     * @param envelope the envelope which has entered the step.
     * @throws PersistenceException if there is a problem writing event (specifically the notification history)
     * @throws SQLException 
     */
    private void enterStepEvents(Envelope envelope) throws PersistenceException, SQLException {

        // First throw the step event, to notify those subscribing to the step
        // for all envelopes for a specific workflow.
        net.project.notification.GenericEvent event = new net.project.notification.GenericEvent();
        event.setUser(this.user);
        event.setEventType(Step.ENTER_EVENT_TYPE);
        // Event is based on the step : groups are subscribed to that step
        event.setTargetObjectID(envelope.getStepID());
        event.setTargetObjectType(ObjectType.WORKFLOW_STEP);
        // However, interesting information is in envelope object
        event.setTargetObjectXML(envelope.getXMLBody(Envelope.XML_VERBOSE));
        // Description doesn't appear to be used.
        event.setDescription(PropertyProvider.get(Step.ENTER_EVENT_DESCRIPTION_PROPERTY));
        event.setSpaceID(envelope.getSpaceID());

        net.project.notification.NotificationManager.notify(event);
//        if (!isNotificationSuccessful) {
//        	Logger.getLogger(EnvelopeManager.class).error("Error performing notification in EnvelopeManager.enterStepEvents()");
//            throw new PersistenceException("Error occurred with notification for envelope.");
//        }

        writeNotificationHistory(envelope, event, event.getEventTime());
    }

    /**
     * Return a list of envelope versions
     * @return list of envelope versions
     */
    public EnvelopeVersionList getEnvelopeVersions(Envelope envelope) throws PersistenceException {
        return envelope.getEnvelopeVersions();
    }

    /**
     * Return the list of envelope version objects in this envelope
     * @return the list of envelope version objects
     * @throws LoadException if there is a problem getting the envelope object list
     */
    protected EnvelopeVersionObjectList getEnvelopeContentObjects(Envelope envelope)
        throws LoadException {
        return envelope.getCurrentVersion().getObjectList();
    }

    /**
     * Return list of steps for envelope's workflow with current step specified.<br />
     * Each step includes the transitions for the step.
     * @param envelope the envelope
     * @return list of steps
     */
    public StepList getStepsAndTransitions(Envelope envelope) throws PersistenceException {
        StepList steps = null;
        String workflowID = null;
        WorkflowManager wfManager = null;

        workflowID = envelope.getWorkflowID();
        wfManager = new WorkflowManager();
        wfManager.setSpace(this.space);
        wfManager.setUser(this.user);
        steps = wfManager.getSteps(workflowID);

        steps.setSelectedStepID(envelope.getStepID());
        steps.setIncludeBeginTransitions(true);

        return steps;
    }
    
    public void abortTransition(String envelopeId) throws PersistenceException {
		String query = "update pn_envelope_version set transition_id = '', status_id = '500' where envelope_id = " +envelopeId;
		//Execute the query
		try {
			db.executeQuery(query);
			db.commit();
		} catch (SQLException sqle) {
			Logger.getLogger(EnvelopeManager.class).error(
					"EnvelopeManager.abortTransition() threw an SQL exception: " + sqle);
			throw new PersistenceException("Envelope manager envelope read failed: " + sqle, sqle);
		} finally {
			db.release();
		}
	}
}
