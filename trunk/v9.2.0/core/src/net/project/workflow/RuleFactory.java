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

import net.project.persistence.PersistenceException;

/**
 * RuleFactory provides static methods for creating and loading Rules
 * This is required since Rules must be of a specific sub-class
 */
public class RuleFactory implements java.io.Serializable {

    /** The package to be appended to class names */
    private static final String PACKAGE = "net.project.workflow";

    /**
     * Get the rule for the specified ruleID
     * This returns a Rule object of the correct sub-class that is LOADED
     * or null if there was a problem creating the rule object.
     * @param ruleID the rule id of the rule to get
     * @return the rule
     * @throws PersistenceException if the rule could not be loaded
     * @throws RuleException if there is a problem creating the rule
     */
    public static Rule getRule(String ruleID) throws PersistenceException, RuleException {
        Rule rule = null;

        /* Create Rule object based on type of specified ruleID */
        rule = createRuleForType(Rule.getRuleType(ruleID));
        /* Load it */
        rule.setID(ruleID);
        rule.load();
        return rule;
    }

    /**
     * Gets an empty Rule object of the correct type for the specified ruleID.
     *
     * @param ruleID the rule id
     * @return the rule
     * @throws RuleException if there is a problem creating the rule
     */
    public static Rule createEmptyRule(String ruleID) throws RuleException {
        /* Create Rule object based on type of specified ruleID */
        return createRuleForType(Rule.getRuleType(ruleID));
    }

    /**
     * Create a new rule object of the class determined by the specified rule
     * type id.
     *
     * @param ruleTypeID the rule type id to create rule object
     * @return the rule object
     * @throws RuleException if there is a problem creating the rule
     */
    public static Rule createRule(String ruleTypeID) throws RuleException {
        return createRuleForType(RuleType.forID(ruleTypeID));
    }


    /**
     * Create a Rule object of the correct sub-class for the specified ruleType
     * Returns null if the rule class could not be determined or if there was
     * a problem instantiating it.
     *
     * @param ruleType the rule type
     * @return the Rule object
     * determined.
     */
    public static Rule createRuleForType(RuleType ruleType) throws RuleException {
        Rule rule = null;
        try {
            /* Get the class based on this ruleType */
            Class ruleClass = Class.forName(PACKAGE + "." + RuleType.getRuleClassName(ruleType));

            /* Now create a rule of the correct class, casting to its superclass of Rule */
            rule = (Rule)ruleClass.newInstance();

        } catch (ClassNotFoundException e) {
            /* Couldn't find class */
            throw new RuleException("Unable to create rule: " + e);

        } catch (InstantiationException e) {

            /* Couldn't create rule of appropriate class so return null rule */
            throw new RuleException("Unable to create rule: " + e);

        } catch (IllegalAccessException e) {

            /* Damn! */
            throw new RuleException("Unable to create rule: " + e);

        }
        return rule;
    }
}
