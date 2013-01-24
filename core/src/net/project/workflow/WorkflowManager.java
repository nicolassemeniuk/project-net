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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.form.FormMenu;
import net.project.form.FormMenuEntry;
import net.project.notification.NotificationException;
import net.project.notification.NotificationManager;
import net.project.notification.Subscriber;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.space.Space;
import net.project.util.Conversion;

import org.apache.log4j.Logger;

/**
 * Provides all the helper methods for manipulating workflows and workflow
 * envelopes.
 *
 * @author Tim Morrow
 */
public class WorkflowManager implements java.io.Serializable {
    /** Current user */
    private User user;
    /** Current space */
    private Space space;

    /**
     * Set the current space, to be used by other methods.
     * @param space the space
     */
    public void setSpace(Space space) {
        this.space = space;
    }

    /**
     * Set the current user, to be used by other methods.
     * @param user the user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Return a list of workflows
     * @param includeUnpublished True fetches all workflows
     * False fetches only published workflows
     * @return list of workflows
     */
    public WorkflowList getAvailableWorkflows(boolean includeUnpublished) throws PersistenceException {
        return getWorkflows(getWorkflowWhereClause(includeUnpublished, null, null));
    }

    /**
     * Get a list of workflows that support a specific type of object
     * @param objectType the object type that the workflow must support
     * @param subTypeID the sub type ID that the workflow must support
     * @return workflow list
     */
    public WorkflowList getWorkflowsForType(String objectType, String subTypeID) throws PersistenceException {
        return getWorkflows(getWorkflowWhereClause(false, objectType, subTypeID));
    }


    public void copyAll(String fromSpaceID, String toSpaceID) throws net.project.base.PnetException {
        int errorCode = 0;
        boolean doCopyGroups = false;

        if (fromSpaceID.equals(toSpaceID)) {
            doCopyGroups = true;
        }

        DBBean db = new DBBean();
        try {
            db.prepareCall("begin WORKFLOW.COPY_ALL  (?,?,?,?,?); end;");

            db.cstmt.setString(1, fromSpaceID);
            db.cstmt.setString(2, toSpaceID);
            db.cstmt.setString(3, net.project.security.SessionManager.getUser().getID());
            db.cstmt.setInt(4, 1);
            db.cstmt.registerOutParameter(5, java.sql.Types.INTEGER);

            db.executeCallable();
            errorCode = db.cstmt.getInt(5);
        } // end try
        catch (SQLException sqle) {
        	Logger.getLogger(WorkflowManager.class).debug("WorkflowManager.copyAll():  unable to execute stored procedure: " + sqle);
            throw new PersistenceException("WorkflowManager.copyAll operation failed! ", sqle);
        } finally {
            db.release();
        }

        net.project.database.DBExceptionFactory.getException("WorkflowManager.copyAll()", errorCode);

    }


    /**
     * Constructs a SQL where clause (EXCLUDING "WHERE" statement) based on specified
     * workflow criteria.
     *
     * <b>Note</b><br>
     * Assumes the workflow table will have an alias of "wf"
     * @param includeUnpublished true means the workflow list will include unpublished
     * workflows, false means only published workflows will be returned
     * @param objectType the workflow must support this object type.  A value of null
     * means the criteria will be ignored.
     * @param subTypeID the workflow must support this sub type.  A value of null
     * means the criteria will be ignored.
     * @return the where clause
     */
    private String getWorkflowWhereClause(boolean includeUnpublished, String objectType, String subTypeID) {
        StringBuffer whereClause = new StringBuffer("");
        whereClause.append("wf.record_status = 'A' ");
        whereClause.append("AND wf.space_id = " + this.space.getID() + " ");
        if (!includeUnpublished) {
            // Published only
            whereClause.append("AND wf.is_published = 1 ");
        }
        if (objectType != null) {
            /*
                Now add part that limits workflows to
                For example
                    AND (wf.is_generic = 1 OR
                        exits (select 1 from pn_workflow_has_object_type wft
                               where
                                   wft.workflow_id = wf.workflow_id and
                                   (wft.object_type = 'form' AND wft.sub_type_id = '123')
                              )
                        )
             */
            whereClause.append("AND (wf.is_generic = 1 ");
            whereClause.append("OR exists ( ");
            whereClause.append("select 1 ");
            whereClause.append("from pn_workflow_has_object_type wft ");
            whereClause.append("where wft.workflow_id = wf.workflow_id and ");
            whereClause.append("(wft.object_type = '" + objectType + "' ");
            if (subTypeID != null && !subTypeID.equals("")) {
                whereClause.append("AND wft.sub_type_id = '" + subTypeID + "' ");
            }
            whereClause.append(") ) ) ");

        } // end if

        return whereClause.toString();
    }

    /**
     * Return a list of workflows based on the specified where clause
     * @param whereClause the SQL where clause EXCLUDING "WHERE" statement
     * @return the workflow list
     */
    private WorkflowList getWorkflows(String whereClause) throws PersistenceException {
        // List of workflow objects
        WorkflowList workflowList = new WorkflowList();
        // workflow container variable
        Workflow wf = null;

        StringBuffer queryBuff = new StringBuffer();

        queryBuff.append("SELECT wf.workflow_id, wf.workflow_name, wf.strictness_id, wf.workflow_description, wf.created_by_id, ");
        queryBuff.append("wf.notes, wf.created_datetime, wf.modified_by_id, wf.owner_id, wf.modified_datetime, wf.is_published, ");
        queryBuff.append("wf.is_generic, wf.crc, wf.record_status, ");
        queryBuff.append("wf.strictness_name, wf.owner_full_name, wf.created_by_full_name, wf.modified_by_full_name, ");
        queryBuff.append("wf.active_envelope_count, wf.space_id ");
        queryBuff.append("FROM pn_workflow_view wf ");

        if (whereClause != null && !whereClause.equals("")) {
            queryBuff.append("WHERE " + whereClause);
        }

        // Execute the query
        DBBean db = new DBBean();
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
        	Logger.getLogger(WorkflowManager.class).error("WorkflowManager.getWorkflows() threw an SQL exception: " + sqle);
            throw new PersistenceException("Workflow manager get workflows operation failed.", sqle);
        } finally {
            db.release();
        }

        return workflowList;
    }

    /**
     * Return the complete list of strictness values
     * @return list of strictness objects
     */
    public ArrayList getStrictnessList() {
        ArrayList strictnessList = new ArrayList();

        try {
            // Load the data for each Strictness
            Strictness.RELAXED.load();
            Strictness.STRICT.load();

            // Add to list
            strictnessList.add(Strictness.RELAXED);
            strictnessList.add(Strictness.STRICT);

        } catch (PersistenceException pe) {
            // Hmmm... now we have to strictness values
        }

        return strictnessList;
    }

    /**
     * Return a list of steps for a workflow.
     * @param workflowID the workflow to get the steps for
     * @return the steps
     * @throws PersistenceException if there is a problem loading the steps
     */
    public StepList getSteps(String workflowID) throws PersistenceException {
        return getSteps(workflowID, null);
    }

    /**
     * Return a list of steps for a workflow.
     * @param workflowID the workflow to get the steps for
     * @param excludeStepID a step to be excluded from the list
     *    null implies a wildcard
     * @return the steps
     * @throws PersistenceException if there is a problem loading the steps
     */
    public StepList getSteps(String workflowID, String excludeStepID) throws PersistenceException {
        StepList stepList = new StepList();
        Step step = null;

        StringBuffer queryBuff = new StringBuffer();
        StringBuffer whereClause = new StringBuffer();

        // Build generic select on Workflow Step view
        queryBuff.append("SELECT step_id, step_sequence, workflow_id, workflow_name, ");
        queryBuff.append("step_name, step_description, notes_clob, is_initial_step, ");
        queryBuff.append("is_final_step, created_by_id, created_datetime, ");
        queryBuff.append("created_by_full_name, modified_by_id, modified_datetime, ");
        queryBuff.append("modified_by_full_name, crc, record_status, ");
        queryBuff.append("begin_transition_count, end_transition_count ");
        queryBuff.append("FROM pn_workflow_step_view ");

        // Build customized WHERE clause
        whereClause.append("WHERE record_status = 'A' ");
        whereClause.append("AND workflow_id = " + workflowID + " ");
        if (excludeStepID != null) {
            whereClause.append("AND step_id != " + excludeStepID + " ");
        }
        queryBuff.append(whereClause);

        /* ORDERY BY clause
           This order by clause sorts Initial steps to the top, final steps to the bottom
        */
        queryBuff.append("ORDER BY step_sequence ");

        // Execute the query
        DBBean db = new DBBean();
        try {
            db.executeQuery(queryBuff.toString());

            while (db.result.next()) {
                // Create and populate workflow envelope
                step = new Step();
                step.setID(db.result.getString("step_id"));
                step.setWorkflowID(db.result.getString("workflow_id"));
                step.setWorkflowName(db.result.getString("workflow_name"));
                step.setName(db.result.getString("step_name"));
                step.setSequence(db.result.getString("step_sequence"));
                step.setDescription(db.result.getString("step_description"));
                step.setNotes(ClobHelper.read(db.result.getClob("notes_clob")));
                step.setInitialStep(Conversion.toBoolean(db.result.getString("is_initial_step")));
                step.setFinalStep(Conversion.toBoolean(db.result.getString("is_final_step")));
                step.setCreatedBy(db.result.getString("created_by_id"));
                step.setCreatedDatetime(db.result.getTimestamp("created_datetime"));
                step.setCreatedByFullName(db.result.getString("created_by_full_name"));
                step.setModifiedBy(db.result.getString("modified_by_id"));
                step.setModifiedDatetime(db.result.getTimestamp("modified_datetime"));
                step.setModifiedByFullName(db.result.getString("modified_by_full_name"));
                step.setCrc(db.result.getTimestamp("crc"));
                step.setRecordStatus(db.result.getString("record_status"));
                step.setBeginTransitionCount(db.result.getInt("begin_transition_count"));
                step.setEndTransitionCount(db.result.getInt("end_transition_count"));

                // Add to our list
                step.setLoaded(true);
                stepList.add(step);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(WorkflowManager.class).error("WorkflowManager.getSteps() threw an SQL exception: " + sqle);
            throw new PersistenceException("Workflow manager get steps operation failed.", sqle);
        } finally {
            db.release();

        }

        return stepList;
    }

    /**
     * Return a list of transitions belonging to a specific workflow
     * @param workflowID the workflow to get transitions for
     * @return TransitionList the list of transitions
     */
    public TransitionList getWorkflowTransitions(String workflowID) throws PersistenceException {
        return getTransitions(workflowID, null);
    }

    /**
     * Return a list of transitions where each transitions has the specified
     * step as its "beginStepID"
     * @param stepID the step to get transitions for
     * @return the list of transitions
     */
    public TransitionList getStepTransitions(String stepID) throws PersistenceException {
        return getTransitions(null, stepID);
    }

    /**
     * Return a list of groups belonging to a step.
     * These are read from the pn_workflow_step_has_group table.  This method also
     * loads the subscription (notification) settings for each group at the step.
     * @param stepID the step
     * @return list of groups of type StepGroup
     */
    public StepGroupList getGroupsForStep(String stepID) throws PersistenceException {
        HashMap groupMap = new HashMap();               // For mainting groups by groupID
        StepGroupList groupList = new StepGroupList();
        StepGroup group = null;

        StringBuffer queryBuff = new StringBuffer();
        StringBuffer whereClause = new StringBuffer();

        queryBuff.append("select step_id, workflow_id, group_id, group_name, ");
        queryBuff.append("group_desc, is_principal, is_participant, ");
        queryBuff.append("created_by_id, created_by_full_name, created_datetime, ");
        queryBuff.append("modified_by_id, modified_by_full_name, modified_datetime, ");
        queryBuff.append("crc, record_status ");
        queryBuff.append("from pn_wf_step_has_group_view ");

        // Build customized WHERE clause
        whereClause.append("WHERE record_status = 'A' ");
        whereClause.append("AND step_id = " + stepID + " ");
        queryBuff.append(whereClause);

        // Execute the query
        DBBean db = new DBBean();
        try {
            db.executeQuery(queryBuff.toString());

            while (db.result.next()) {
                // Create and populate workflow envelope
                group = new StepGroup();

                group.groupID = db.result.getString("group_id");
                group.stepID = db.result.getString("step_id");
                group.workflowID = db.result.getString("workflow_id");
                group.isParticipant = Conversion.toBoolean(db.result.getString("is_participant"));
                group.createdBy = db.result.getString("created_by_id");
                group.createdDatetime = db.result.getTimestamp("created_datetime");
                group.modifiedBy = db.result.getString("modified_by_id");
                group.modifiedDatetime = db.result.getTimestamp("modified_datetime");
                group.crc = db.result.getTimestamp("crc");
                group.recordStatus = db.result.getString("record_status");

                group.name = PropertyProvider.get(db.result.getString("group_name"));
                group.description = db.result.getString("group_desc");
                group.isPrincipal = Conversion.toBoolean(db.result.getString("is_principal"));
                group.createdByFullName = db.result.getString("created_by_full_name");
                group.modifiedByFullName = db.result.getString("modified_by_full_name");

                group.isLoaded = true;
                // Add to our list
                groupList.add(group);
                // Also add to map for subscriptions later
                groupMap.put(group.getGroupID(), group);
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(WorkflowManager.class).error("WorkflowManager.getGroupsForStep() threw an SQL exception: " + sqle);
            throw new PersistenceException("Workflow manager get groups operation failed.", sqle);
        } finally {
            db.release();
        }

        //  Now load notification settings
        loadNotificationSettings(stepID, groupMap);

        return groupList;
    }

    /**
     * Loads the subscriptions for the groups at a step and updates a hashmap.
     * <b>Preconditions:</b><br>
     * <li><code>entryTable</code> contains selected groups</li><br>
     * <b>Postconditions:</b><br>
     * <li><code>entryTable</code> contains selected groups with correct notification settings</li></br>
     * @param stepID the step for which subscriptions must be loaded.
     * @param groupMap a hashmap of groups by groupID for which Notification must be added
     * @throws PersistenceException if there is a problem loading the settings
     */
    private void loadNotificationSettings(String stepID, HashMap groupMap) throws PersistenceException {
        Iterator it = null;
        StepGroup group = null;
        String groupID = null;
        Step step = null;

        // Load the step to locate the subscription ID stored at the step
        step = new Step();
        step.setID(stepID);
        step.load();

        // If there is no subscription id, then no-one is subscribed and nothing
        // is updated.  If there is a subscription id then iterate over all the
        // subscribers for that subscription and update the corresponding entry
        // in the groupMap.
        if (step.getSubscriptionID() != null) {
            try {
                it = NotificationManager.getSubscription(step.getSubscriptionID()).getSubscriberCollection().iterator();
                while (it.hasNext()) {
                    groupID = ((Subscriber)it.next()).getSubscriberGroupID();
                    group = (StepGroup)groupMap.get(groupID);

                    // Now update notification value
                    if (group != null) {
                        group.setNotified(true);
                    }

                } //end while
            } catch (NotificationException ne) {
                throw new PersistenceException("Error getting subscription for step.", ne);
            }

        }

    }

    /**
     * Return a list of rule types
     * @return list of rule types
     */
    public ArrayList getRuleTypeList() {
        ArrayList ruleTypeList = new ArrayList();

        try {
            // Load the data for each rule type
            RuleType.AUTHORIZATION_RULE_TYPE.load();

            // Add to list
            ruleTypeList.add(RuleType.AUTHORIZATION_RULE_TYPE);

        } catch (PersistenceException pe) {
            // Hmmm... now we have no rule Types... return empty list
        }

        return ruleTypeList;
    }

    /**
     * Return a list of rule statuses
     * @return list of rule statuses
     */
    public ArrayList getRuleStatusList() {
        ArrayList ruleStatusList = new ArrayList();

        try {
            // Load the data for each rule status
            RuleStatus.ENFORCED.load();
            RuleStatus.DISABLED.load();

            // Add to list
            ruleStatusList.add(RuleStatus.ENFORCED);
            ruleStatusList.add(RuleStatus.DISABLED);

        } catch (PersistenceException pe) {
            // Hmmm... now we have no rule Statuses... return empty list
        }

        return ruleStatusList;
    }

    /**
     * Get list of workflow statuses
     * @return array list of status objects
     */
    public ArrayList getStatusList() throws PersistenceException {
        ArrayList statusList = new ArrayList();
        Status status = null;

        StringBuffer queryBuff = new StringBuffer();
        queryBuff.append("select status_id, status_name, status_description, ");
        queryBuff.append("is_inactive, created_by_id, created_datetime, ");
        queryBuff.append("modified_by_id, modified_datetime, crc, record_status ");
        queryBuff.append("from pn_workflow_status ");
        queryBuff.append("where record_status = 'A' ");

        // Execute the query
        DBBean db = new DBBean();
        try {
            db.executeQuery(queryBuff.toString());

            while (db.result.next()) {
                // Create and populate workflow envelope
                status = new Status();
                status.setID(db.result.getString("status_id"));
                status.setName(PropertyProvider.get(db.result.getString("status_name")));
                status.setDescription(db.result.getString("status_description"));
                status.setInactive(Conversion.toBoolean(db.result.getString("is_inactive")));
                status.setCreatedBy(db.result.getString("created_by_id"));
                status.setCreatedDatetime(db.result.getTimestamp("created_datetime"));
                status.setModifiedBy(db.result.getString("modified_by_id"));
                status.setModifiedDatetime(db.result.getTimestamp("modified_datetime"));
                status.setCrc(db.result.getTimestamp("crc"));
                status.setRecordStatus(db.result.getString("record_status"));
                status.setLoaded(true);

                // Add to our list
                statusList.add(status);
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(WorkflowManager.class).error("WorkflowManager.getStatusList() threw an SQL exception: " + sqle);
            throw new PersistenceException("Workflow manager get status list operation failed.", sqle);
        } finally {
            db.release();
        }

        return statusList;
    }

    /**
     * Returns a list of rules belonging to specified transition
     * The RuleList maintains Rule objects of specialized classes
     * @param transitionID the transition ID to get rules for
     * @return the list of rules
     */
    public RuleList getRulesForTransition(String transitionID) throws PersistenceException {
        RuleList ruleList = new RuleList();
        GenericRule rule = null;
        String ruleTypeID = null;

        StringBuffer queryBuff = new StringBuffer();
        StringBuffer whereClause = new StringBuffer();

        queryBuff.append("select rule_id, transition_id, workflow_id, ");
        queryBuff.append("rule_type_id, rule_type_name, rule_status_id, rule_status_name, ");
        queryBuff.append("rule_name, rule_description, notes, created_by_id, created_by_full_name, created_datetime, ");
        queryBuff.append("modified_by_id, modified_by_full_name, modified_datetime, ");
        queryBuff.append("crc, record_status ");
        queryBuff.append("from pn_workflow_rule_view ");

        whereClause.append("where record_status = 'A' ");
        whereClause.append("AND transition_id = " + transitionID + " ");
        queryBuff.append(whereClause);

        // Execute the query
        DBBean db = new DBBean();
        try {
            db.executeQuery(queryBuff.toString());

            while (db.result.next()) {
                rule = new GenericRule();

                // Update properties
                rule.setID(db.result.getString("rule_id"));
                rule.setTransitionID(db.result.getString("transition_id"));
                rule.setWorkflowID(db.result.getString("workflow_id"));
                rule.setRuleTypeID(ruleTypeID);
                rule.setRuleTypeName(db.result.getString("rule_type_name"));
                rule.setRuleStatusID(db.result.getString("rule_status_id"));
                rule.setRuleStatusName(db.result.getString("rule_status_name"));
                rule.setName(db.result.getString("rule_name"));
                rule.setDescription(db.result.getString("rule_description"));
                rule.setNotes(db.result.getString("notes"));
                rule.setCreatedBy(db.result.getString("created_by_id"));
                rule.setCreatedByFullName(db.result.getString("created_by_full_name"));
                rule.setCreatedDatetime(db.result.getTimestamp("created_datetime"));
                rule.setModifiedBy(db.result.getString("modified_by_id"));
                rule.setModifiedByFullName(db.result.getString("modified_by_full_name"));
                rule.setModifiedDatetime(db.result.getTimestamp("modified_datetime"));
                rule.setCrc(db.result.getTimestamp("crc"));
                rule.setRecordStatus(db.result.getString("record_status"));

                rule.setLoaded(true);
                ruleList.add(rule);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(WorkflowManager.class).error("WorkflowManager.getRulesForTransition() threw an SQL exception: " + sqle);
            throw new PersistenceException("Workflow manager get rules operation failed.", sqle);

        } finally {
            db.release();

        }
        return ruleList;
    }

    /**
     * Return a list of transitions
     * @param workflowID the workflow (null implies a wildcard)
     * @param stepID the step to which the transition belongs
     * this is actually the begin_step_id (null implies a wildcard)
     * @return the list of transitions
     */
    private TransitionList getTransitions(String workflowID, String stepID) throws PersistenceException {
        TransitionList transitionList = new TransitionList();
        Transition tran = null;

        StringBuffer queryBuff = new StringBuffer();
        StringBuffer whereClause = new StringBuffer();

        queryBuff.append("select transition_id, workflow_id, workflow_name, ");
        queryBuff.append("transition_verb, transition_description, begin_step_id, ");
        queryBuff.append("begin_step_name, end_step_id, end_step_name, ");
        queryBuff.append("created_by_id, created_by_full_name, created_datetime, ");
        queryBuff.append("modified_by_id, modified_by_full_name, modified_datetime, ");
        queryBuff.append("crc, record_status ");
        queryBuff.append("from pn_workflow_transition_view ");

        whereClause.append("WHERE record_status = 'A' ");
        if (workflowID != null) {
            whereClause.append("AND workflow_id = " + workflowID + " ");
        }
        if (stepID != null) {
            whereClause.append("AND begin_step_id = " + stepID + " ");
        }
        queryBuff.append(whereClause);

        // Execute the query
        DBBean db = new DBBean();
        try {
            db.executeQuery(queryBuff.toString());

            while (db.result.next()) {
                // Create and populate workflow envelope
                tran = new Transition();

                // Update properties
                tran.transitionID = db.result.getString("transition_id");
                tran.workflowID = db.result.getString("workflow_id");
                tran.transitionVerb = db.result.getString("transition_verb");
                tran.description = db.result.getString("transition_description");
                tran.beginStepID = db.result.getString("begin_step_id");
                tran.endStepID = db.result.getString("end_step_id");

                tran.createdBy = db.result.getString("created_by_id");
                tran.createdDatetime = db.result.getTimestamp("created_datetime");
                tran.modifiedBy = db.result.getString("modified_by_id");
                tran.modifiedDatetime = db.result.getTimestamp("modified_datetime");
                tran.crc = db.result.getTimestamp("crc");
                tran.recordStatus = db.result.getString("record_status");

                tran.workflowName = db.result.getString("workflow_name");
                tran.beginStepName = db.result.getString("begin_step_name");
                tran.endStepName = db.result.getString("end_step_name");
                tran.createdByFullName = db.result.getString("created_by_full_name");
                tran.modifiedByFullName = db.result.getString("modified_by_full_name");

                tran.isLoaded = true;
                transitionList.add(tran);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(WorkflowManager.class).error("WorkflowManager.getTransitions() threw an SQL exception: " + sqle);
            throw new PersistenceException("Workflow manager get transitions operation failed.", sqle);

        } finally {
            db.release();

        }

        return transitionList;
    }

    /**
     * Returns a list of workflow object types.  These are object types
     * which are workflowable.  They are used to restrict the types of objects
     * which may be workflowed by a particular workflow.
     * @return list of object types
     */
    public ArrayList getWorkflowObjectTypes() throws PersistenceException {
        ArrayList list = new ArrayList();
        WorkflowObjectType objectType = null;
        StringBuffer queryBuff = new StringBuffer();

        queryBuff.append("select object_type, master_table_name, object_type_desc, ");
        queryBuff.append("parent_object_type, default_permission_actions, is_securable ");
        queryBuff.append("from pn_object_type ");
        queryBuff.append("where is_workflowable = 1 ");

        DBBean db = new DBBean();
        try {
            // Indicate whether to ignore the addition of the object type to the list
            boolean isIgnored = false;
            db.executeQuery(queryBuff.toString());

            while (db.result.next()) {
                // Initially, don't ignore object type
                isIgnored = false;
                objectType = new WorkflowObjectType();
                objectType.setName(db.result.getString("object_type"));
                objectType.setDescription(PropertyProvider.get(db.result.getString("object_type_desc")));

                // Custom code to add object sub types based on object type
                if (objectType.getName().equals(net.project.base.ObjectType.FORM_DATA)) {
                    try {
                        addFormSubTypes(objectType);
                    } catch (PersistenceException pe) {
                        // Occurs if there is an error or if there are no forms in the space
                        // Forms require at least on subtype
                        isIgnored = true;
                    }
                } // add other custom objectType codes

                // Only add the object type if there were no problems with it
                if (!isIgnored) {
                    list.add(objectType);
                }
            } // end while

        } catch (SQLException sqle) {
        	Logger.getLogger(WorkflowManager.class).error("WorkflowManager.getWorkflowObjectTypes() threw an SQL exception: " + sqle);
            throw new PersistenceException("Workflow Manager get object types operation failed.", sqle);

        } finally {
            db.release();

        }
        return list;
    }

    /**
     * Add the sub types for a form to the workflow object type
     * @param objectType the object type (which should have type 'form')
     * @throws PersistenceException if there is a problem loading the form types
     */
    private void addFormSubTypes(WorkflowObjectType objectType) throws PersistenceException {
        FormMenuEntry entry = null;
        Iterator it = null;

        try {

            FormMenu menu = new FormMenu();
            menu.setSpace(this.space);
            menu.load();

            it = menu.m_formTypes.iterator();

            while (it.hasNext()) {
                entry = (FormMenuEntry)it.next();
                objectType.addSubType(entry.getID(), entry.getName());
            }
        } catch (net.project.form.FormException fe) {
            throw new PersistenceException("WorkflowManager.addFormSubTypes() threw a FormException: " + fe, fe);
        }
    }

}
