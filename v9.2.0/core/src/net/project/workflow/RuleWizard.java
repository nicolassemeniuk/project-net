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

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.space.Space;
import net.project.xml.XMLFormatter;

import org.apache.log4j.Logger;

/**
 * A RuleWizard.  This is used to track the current rule type and provide
 * methods for determining the appropriate Rule sub-class to instantiate.
 *
 * @author Tim
 */
public class RuleWizard implements java.io.Serializable {
    /** Translates XML */
    private XMLFormatter xmlFormatter = null;
    /** Current space */
    private Space space = null;

    /** The current rule type */
    private RuleType ruleType = null;
    /** The current mode (e.g. Create or Edit) */
    private String mode = null;
    /** The current rule */
    private Rule rule = null;

    /**
     * Create a new RuleWizard
     */
    public RuleWizard() {
        xmlFormatter = new XMLFormatter();
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    /**
     * Set the stylesheet to use
     * @param stylesheetFileName the stylesheet path
     */
    public void setStylesheet(String stylesheetFileName) {
        // set the XML formatter stylesheet
        xmlFormatter.setStylesheet(stylesheetFileName);
    }


    /**
     * Return the rule type id
     * This returns the ID of the current rule type
     * @return the rule type id
     */
    public String getRuleTypeID() {
        if (this.ruleType == null) {
            return null;
        }
        return this.ruleType.getID();
    }

    /**
     * Return the rule type name
     * @return the rule type name
     */
    public String getRuleTypeName() {
        if (this.ruleType == null) {
            return null;
        }
        return this.ruleType.getName();
    }

    /**
     * Return the rule type description
     * @return the rule type description
     */
    public String getRuleTypeDescription() {
        if (this.ruleType == null) {
            return null;
        }
        return this.ruleType.getDescription();
    }

    /**
     * Return the rule type notes
     * @return the rule type notes
     */
    public String getRuleTypeNotes() {
        if (this.ruleType == null) {
            return null;
        }
        return this.ruleType.getNotes();
    }

    /**
     * Set the wizard mode
     * This may be used to determine appropriate buttons to highlight
     * or affect which pages may be navigated to.
     * @param mode the mode
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * Gets the wizard mode
     * @return the mode
     */
    public String getMode() {
        return this.mode;
    }

    /**
     * Return the current rule ID
     * @return the rule ID
     */
    public String getRuleID() {
        return this.rule.getID();
    }

    /**
     * Set the rule being created / edited
     * This allows the RuleWizard to directly access the rule
     * @param rule the rule
     */
    public void setRule(Rule rule) {
        this.rule = rule;
        try {
            loadRuleType(rule.getRuleTypeID());
        } catch (RuleException re) {
            // Can't do anything
        } catch (PersistenceException pe) {
            // Can't do anything
        }
    }

    /**
     * Get the rule being created / edit
     * @return the rule
     */
    public Rule getRule() {
        return this.rule;
    }

    /**
     * Clear rule type, rule ID and rule
     * Note the Mode is NOT reset
     */
    public void clearRuleInfo() {
        rule = null;
        ruleType = null;

    }

    /**
     * Return the Rule properties presentation.  setStylesheet() must be called
     * before this method.
     *
     * @return the XML string
     * @see #setStylesheet
     */
    public String getRulePropertiesEditPresentation() {
        return xmlFormatter.getPresentation(this.rule.getXML());
    }

    /**
     * Load the rule type
     * This instantiates a rule of the appropriate type
     * @param ruleTypeID the rule type id to load the rule type for
     */
    private void loadRuleType(String ruleTypeID) throws RuleException, PersistenceException {

        /* Get ruleType based on id */
        this.ruleType = RuleType.forID(ruleTypeID);

        /* Load it */
        this.ruleType.load();
    }

    /**
     * Check to see if the workflow can be removed
     * and return the results
     * @return the HTML results
     */
    public String getPrepareRemovePresentation() {
        Rule rule = getRule();
        rule.clearErrors();
        rule.prepareRemove();
        return rule.getPrepareRemoveErrorsPresentation();
    }

    public String getRemoveResultPresentation() {
        Rule rule = getRule();
        return rule.getRemoveResultPresentation();
    }

    /**
     * Attempts to load a rule for the specified type for this transition.
     * If there is more than one rule of that type, it only loads the first one.
     * This method is used by the Special Rule Edit JSP page - which provides
     * a custom interface permitting the creation of one rule of a specific type.<br>
     * This method also returns a boolean indicating if a rule was successfully loaded.
     * This combined function reduces the number of SQL statements required to determine
     * if a rule is defined and to load it.<br>
     * <b>Preconditions</b>
     * <li>None</li>
     * <br>
     * <b>Postconditions</b>
     * <li>return value of true indicates a rule has been loaded and may be
     * fetched using getRule()</li>
     * @param transition the transition for which the rule is to be loaded
     * @param ruleType the type of rule which is to be loaded
     * @return true if a rule was loaded, false otherwise
     * @see net.project.workflow.RuleWizard#getRule
     */
    public boolean isRuleDefined(Transition transition, RuleType ruleType) throws PersistenceException {
        DBBean db = new DBBean();
        boolean isRuleLoaded = false;
        StringBuffer queryBuff = new StringBuffer();

        queryBuff.append("select rule_id ");
        queryBuff.append("from pn_workflow_rule ");
        queryBuff.append("where transition_id = '" + transition.getID() + "' ");
        queryBuff.append("and workflow_id = '" + transition.getWorkflowID() + "' ");
        queryBuff.append("and rule_type_id = '" + ruleType.getID() + "' ");
        queryBuff.append("and record_status = 'A'");

        try {
            db.executeQuery(queryBuff.toString());

            if (db.result.next()) {
                setRule(RuleFactory.getRule(db.result.getString("rule_id")));
                isRuleLoaded = true;
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(RuleWizard.class).error("RuleWizard.isRuleDefined() threw an SQL exception: " + sqle);
            throw new PersistenceException("Error looking for rules for transition.", sqle);

        } catch (RuleException re) {
        	Logger.getLogger(RuleWizard.class).error("RuleWizard.isRuleDefined() threw a RuleException: " + re);
            throw new PersistenceException("Error loading rule for transition.", re);

        } finally {
            db.release();
        }

        return isRuleLoaded;
    }

    /*=================================================================================================
      Error stuff
      ================================================================================================*/

    /*
        Note : The RuleWizard requires error handling because it must be capable of validating a rule type
        Ah ha!  So why can a rule not validate its own type?  Because a rule object is created from a class
        which is derived from the type: hence, a rule cannot be created without a valid type.
     */

    private ValidationErrors errors = new ValidationErrors();

    public void clearErrors() {
        errors.clearErrors();
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

    public void validateRuleTypeID() {
        if (getRuleTypeID() == null || getRuleTypeID().equals("")) {
            errors.put("rule_type_id", "Rule Type", PropertyProvider.get("prm.workflow.requiredfield.validation.message"));
        }
    }

    /*
        End of validation routines
     */

    /*=================================================================================================
      End of Error stuff
      ================================================================================================*/

}
