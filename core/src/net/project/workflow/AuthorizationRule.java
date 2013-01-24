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
|   $Revision: 20924 $
|       $Date: 2010-06-08 07:43:14 -0300 (mar, 08 jun 2010) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.workflow;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.group.GroupDAO;

import org.apache.log4j.Logger;

/**
 * This rule maintains a list of groups who are authorized to perform a
 * transition.
 *
 * @author Tim
 * @since 09/2000
 */
public class AuthorizationRule extends Rule {
    /*==========================================================================
        Nested classes
    ==========================================================================*/
    /**
     * A group which has authorization.
     */
    class Group implements Serializable {
        // This should be Step Has Group, but with a column called is_selected
        String stepID = null;
        /** A unique identifier for this group. */
        String groupID = null;
        String groupName = null;
        String groupDescription = null;
        boolean isPrincipal = false;
        boolean isSelected = false;

        /**
         * Returns an &lt;option> tag (_not_ an HTML tag but an element called &lt;option>)<br>
         * E.g.<br><code>
         *     &lt;option value="5000" selected="1">Group 5000&lt;/option>
         * </code>
         * @return the xml string
         */
        public String getOptionXML() {
            StringBuffer xml = new StringBuffer();
            // Add option element
            xml.append("<option value=\"" + this.groupID + "\" " +
                (this.isSelected ? "selected=\"1\" " : " ") + ">");

            // Use the name, if present; otherwise use the description
            // Principle Group names are typically null
            if (this.groupName != null) {
                xml.append(this.groupName);
            } else {
                xml.append(this.groupDescription);
            }

            xml.append("</option>");
            return xml.toString();
        }
    }

    /**
     * List of groups
     */
    class GroupTable extends Hashtable {

        GroupTable() {
            super();
        }

        /**
         * Creates new group table.  When created, populates list of groups from
         * step, where the step is the begin step of the transition to which
         * this rule is attached.  Each group is "unselected" be default.
         */
        GroupTable(String transitionID) {
            super();
            WorkflowManager manager = new WorkflowManager();
            Transition transition = null;
            StepGroupList stepGroupList = null;
            StepGroup stepGroup = null;
            Group group = null;

            /* Get the transition to which this rule is attached */
            transition = new Transition();
            transition.setID(transitionID);
            try {
                transition.load();

                /* Get step group list for the begin step of the transition */
                stepGroupList = manager.getGroupsForStep(transition.getBeginStepID());
                /* Build GroupList from step group list */
                for (int i = 0; i < stepGroupList.size(); i++) {
                    stepGroup = (StepGroup)stepGroupList.get(i);

                    group = new Group();
                    group.stepID = stepGroup.getStepID();
                    group.groupID = stepGroup.getGroupID();
                    group.groupName = stepGroup.getName();
                    group.groupDescription = stepGroup.getDescription();
                    group.isPrincipal = stepGroup.isPrincipal();
                    group.isSelected = false;

                    /* Add to myself, keyed by groupID */
                    put(group.groupID, group);
                }
            } catch (PersistenceException pe) {
                /* There was a problem loading the transition... No groups then */
            }
        }

        /**
         * Deselects all groups in table.
         */
        void deselectAll() {
            Enumeration keys = keys();
            while (keys.hasMoreElements()) {
                ((Group)get(keys.nextElement())).isSelected = false;
            }
        }

        String getOptionXML() {
            StringBuffer xml = new StringBuffer();

            Enumeration keys = keys();
            while (keys.hasMoreElements()) {
                xml.append(((Group)get(keys.nextElement())).getOptionXML());
            }
            return xml.toString();
        }

    }
    /*========================================================================*/

    /** The list of groups which have Authorization. */
    private GroupTable groupTable;

    /**
     * Creates a new AuthorizationRule.
     */
    public AuthorizationRule() {
        super();
    }

    /**
     * When a transition is set we need to pre-load the group list.
     *
     * @param transitionID New value of property transitionID.
     */
    public void setTransitionID(String transitionID) {
        super.setTransitionID(transitionID);
        groupTable = new GroupTable(transitionID);
    }

    /**
     * Called when Finish button pressed on RuleWizard
     * For each group id string in the array, sets the "isSelected" flag
     * on the corresponding group record.
     * Group IDs which are not in the groupTable are ignored - this feature is
     * necessary: the HTML form includes one group value which is "0" - this means
     * even if all checkboxes are off, this method will still be called, since at
     * least one group value will be passed in the form POST action.
     * @param groups array, where each element contains a groupID which is
     * the key to groupTable
     */
    public void setGroups(String[] groups) {
        Group group = null;

        /* First deselect all groups */
        groupTable.deselectAll();

        /* Loop through group list, selecting their elements in table */
        for (int i = 0; i < groups.length; i++) {
            group = (Group)groupTable.get(groups[i]);
            if (group != null) {
                group.isSelected = true;
            }
        }
    }

    /**
     * Implementation of rule check.  This ensures that the user in rule context
     * is a member of one of the groups attached to this rule.  This is called
     * from check().  It sets isProblem() and getProblemInfo()
     * @param context the rule context
     * @throws PersistenceException if there is a problem checking the rule
     * @see #check
     */
    void customCheck(Rule.RuleContext context) throws PersistenceException {
        StringBuffer query = new StringBuffer();
        String userID = context.getUser().getID();       // Person performing transition

        // First part of the query looks for groups for this rule
        // and joins with groups that user is a member of
        // Second part returns a row if the special "Envelope Creator" role is
        // at this step and the current user is the person who created the envelope
        query.append("select 1 ");
        query.append("from pn_wf_rule_auth_has_group rulegroup, pn_workflow_step_has_group stepgroup, ");
        query.append("(" + GroupDAO.getQueryFetchAllGroupIDsForPerson() + ") all_distinct_groups ");
        query.append("where rulegroup.rule_id = ? ");
        query.append("and rulegroup.group_id <> " + StepGroup.ENVELOPE_CREATOR_GROUP_ID + " ");
        query.append("and rulegroup.step_id = stepgroup.step_id ");
        query.append("and rulegroup.group_id = stepgroup.group_id ");
        query.append("and rulegroup.workflow_id = stepgroup.workflow_id ");
        query.append("and stepgroup.record_status = 'A' ");
        query.append("and rulegroup.group_id = all_distinct_groups.group_id ");
        query.append("union ");
        query.append("select 1 ");
        query.append("from pn_wf_rule_auth_has_group rulegroup, pn_envelope_has_person_view ehpv ");
        query.append("where ? = ? ");
        query.append("and ehpv.person_id=? and ehpv.envelope_id=?  and rulegroup.group_id = " + StepGroup.ENVELOPE_CREATOR_GROUP_ID + " ");

        DBBean db = new DBBean();
        try {
            int index = 0;
            db.prepareStatement(query.toString());
            // Next two bind variables required by "all_distinct_groups" query
            db.pstmt.setString(++index, userID);
            db.pstmt.setString(++index, userID);
            db.pstmt.setString(++index, getID());
            db.pstmt.setString(++index, userID);
            db.pstmt.setString(++index, context.getEnvelope().getCreatedBy());
            db.pstmt.setString(++index, userID);
            db.pstmt.setString(++index, context.getEnvelope().getID());

            db.executePrepared();
            if (db.result.next()) {
                /* User is ok, we don't care about the data */

            } else {
                /*
                    Person not in group at rule.  Create the problem, flagging it as
                    an error if the rule is mandatory and we have strict rule enforcement
                */
                Rule.RuleProblem problem = new Rule.RuleProblem();
                if (RuleStatus.ENFORCED.equals(RuleStatus.forID(getRuleStatusID())) &&
                    Strictness.STRICT.equals(Strictness.forID(context.getEnvelope().getStrictnessID()))) {
                    /* Strict rule enforcement and an enforced rule */
                    problem.setRule(this);
                    problem.setReason(PropertyProvider.get("prm.workflow.rule.strict.transition.error.message"));
                    problem.setError(true);

                } else {
                    /* Relaxed rule enforcement or a non-enforced rule */
                    problem.setRule(this);
                    problem.setReason(PropertyProvider.get("prm.workflow.rule.relaxed.transition.error.message"));
                    problem.setError(false);
                }

                setProblem(true);
                setProblemInfo(problem);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(AuthorizationRule.class).error("AuthorizationRule.customCheck() threw an SQL exception: " + sqle);
            throw new WorkflowPersistenceException(getName() + " check operation failed");

        } finally {
            db.release();

        }
    }

    /**
     * Clear
     * This should always call super.clear()
     */
    public void clear() {
        super.clear();
        groupTable = null;
    }

    /**
     * Load Rule from database.
     *
     * @throws net.project.persistence.PersistenceException if a problem occurs
     */
    public void load() throws net.project.persistence.PersistenceException {
        Group group = null;

        /* Load standard Rule properties
           This will also load the groupTable with the set of all groups at the step
           due to the overridden setTransitionID() method
         */
        super.load();

        StringBuffer queryBuff = new StringBuffer();
        queryBuff.append("select group_id ");
        queryBuff.append("from pn_wf_rule_auth_has_group ");
        queryBuff.append("where rule_id = " + getID() + " ");

        DBBean db = new DBBean();
        try {
            // Execute select statement
            db.executeQuery(queryBuff.toString());

            /* Set selected flag for corresponding entry in GroupTable */
            String groupID = null;
            while (db.result.next()) {
                groupID = db.result.getString("group_id");
                group = (Group)groupTable.get(groupID);

                // It is possible a select rule group is no longer at the step
                // In that case we ignore it here and it will get deletd on store()
                if (group != null) {
                    group.isSelected = true;
                }
            }

        } catch (SQLException sqle) {
            setLoaded(false);
            Logger.getLogger(AuthorizationRule.class).error("AuthorizationRule.load() threw an SQL exception: " + sqle);
            throw new PersistenceException("AuthorizationRule load operation failed.", sqle);

        } finally {
            db.release();
        }

    }

    /**
     * Store AuthorizationRule.
     */
    public void store() throws net.project.persistence.PersistenceException {
        String storedRuleID;

        /* Store standard Rule properties */
        super.store();

        DBBean db = new DBBean();
        try {
            db.createStatement();

            Logger.getLogger(AuthorizationRule.class).debug("DEBUG [AuthorizationRule.store()] Batching statements...");
            if (isLoaded()) {
                /* Modify existing AuthorizationRule
                   This requires us to delete the existing AuthorizationRule entries
                   from pn_wf_rule_auth and pn_wf_rule_auth_has_group
                 */
                StringBuffer deleteQuery = new StringBuffer();
                StringBuffer deleteGroupsQuery = new StringBuffer();

                // First delete grous for rule
                deleteGroupsQuery.append("delete from pn_wf_rule_auth_has_group authgroup where ");
                deleteGroupsQuery.append("authgroup.workflow_id = " + getWorkflowID() + " ");
                deleteGroupsQuery.append("and authgroup.transition_id = " + getTransitionID() + " ");
                deleteGroupsQuery.append("and authgroup.rule_id = " + getID() + " ");

                // Now delete rule
                // Safety feature : include where in same string as delete to avoid missing where clause
                deleteQuery.append("delete from pn_wf_rule_auth auth where ");
                deleteQuery.append("auth.workflow_id = " + getWorkflowID() + " ");
                deleteQuery.append("and auth.transition_id = " + getTransitionID() + " ");
                deleteQuery.append("and auth.rule_id = " + getID() + " ");


                Logger.getLogger(AuthorizationRule.class).debug("DEBUG [AuthorizationRule.store()] " + deleteGroupsQuery.toString());
                db.stmt.addBatch(deleteGroupsQuery.toString());
                Logger.getLogger(AuthorizationRule.class).debug("DEBUG [AuthorizationRule.store()] " + deleteQuery.toString());
                db.stmt.addBatch(deleteQuery.toString());
            }

            // Now create Authorization Rule
            StringBuffer insertStatement = new StringBuffer();
            insertStatement.append("insert into pn_wf_rule_auth (workflow_id, transition_id, rule_id, crc, record_status) ");
            insertStatement.append("values ( " + getWorkflowID() + " , " + getTransitionID() + " , " + getID() + " , sysdate , 'A' ) ");
            Logger.getLogger(AuthorizationRule.class).debug("DEBUG [AuthorizationRule.store()] " + insertStatement.toString());
            db.stmt.addBatch(insertStatement.toString());

            // Now crete each group for rule
            StringBuffer insertGroupsStatement = null;
            Group group = null;
            Enumeration keys = groupTable.keys();
            while (keys.hasMoreElements()) {
                group = (Group)groupTable.get(keys.nextElement());
                if (group.isSelected) {
                    insertGroupsStatement = new StringBuffer();
                    insertGroupsStatement.append("insert into pn_wf_rule_auth_has_group authgroup ");
                    insertGroupsStatement.append("(rule_id, workflow_id, transition_id, group_id, step_id) ");
                    insertGroupsStatement.append("values ");
                    insertGroupsStatement.append("( " + getID() + " , " + getWorkflowID() + " , " + getTransitionID() + " ");
                    insertGroupsStatement.append(", " + group.groupID + " , " + group.stepID + " ) ");
                    Logger.getLogger(AuthorizationRule.class).debug("DEBUG [AuthorizationRule.store()] " + insertGroupsStatement.toString());
                    db.stmt.addBatch(insertGroupsStatement.toString());
                }
            }

            Logger.getLogger(AuthorizationRule.class).debug("DEBUG [AuthorizationRule.store()] Statement : commit");
            db.stmt.addBatch("commit");

            /* Now db.stmt contains lots of SQL statement */
            Logger.getLogger(AuthorizationRule.class).debug("DEBUG [AuthorizationRule.store()] Executing batch.");
            db.stmt.executeBatch();

            /* If we got here, then everything was fine */
            // Do I need to commit

        } catch (SQLException sqle) {
        	Logger.getLogger(AuthorizationRule.class).error("AuthorizationRule.store() threw an SQL exception: " + sqle);
            throw new PersistenceException("AuthorizationRule store operation failed.", sqle);

        } finally {
            db.release();
        }

        /* Done storing, properties are out of date */
        storedRuleID = getID();
        clear();
        setID(storedRuleID);
    }

    /**
     * Remove AuthorizationRule.
     *
     * @throws PersistenceException if unable to do a soft delete on the
     * authorization rule.
     */
    public void remove() throws net.project.persistence.PersistenceException {
        /* Remove standard rule record */
        super.remove();

        /*
            Note - there is no need to remove any custom rule info, since it
            becomes invisible when the standard rule record is removed.
         */
    }

    /**
     * Return the <custom_property_list> xml structure.
     *
     * @return xml string
     */
    String getCustomPropertiesXML() {
        StringBuffer xml = new StringBuffer();
        xml.append("<custom_property_list>\n");

        // Add groupList property
        xml.append("<property>\n");
        xml.append("<name>groups</name>\n");
        //Avinash:---------------------------------------------------------------------
        xml.append("<label>" + PropertyProvider.get("prm.workflow.transitionedit.rules.roles.label") + "</label>\n");
        xml.append("<name>selectRoles</name>\n");
        xml.append("<labelR>" + PropertyProvider.get("prm.workflow.transitionedit.rules.selectRoles.label") + "</labelR>\n");
        //Avinash:---------------------------------------------------------------------        
        xml.append("<input type=\"checkboxlist\">");
        xml.append(groupTable.getOptionXML());
        xml.append("</input>\n");
        xml.append("</property>\n");
        xml.append("</custom_property_list>\n");

        return xml.toString();
    }

}
