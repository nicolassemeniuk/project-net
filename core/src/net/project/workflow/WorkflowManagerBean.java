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
import net.project.xml.XMLFormatter;

public class WorkflowManagerBean extends WorkflowManager {

    private XMLFormatter xmlFormatter;
    /** Current workflow ID */
    private String currentWorkflowID;
    /** Current workflow Name */
    private String currentWorkflowName;
    /** Current step ID */
    private String currentStepID;
    /** Current step Name */
    private String currentStepName;
    /** Current Transition ID */
    private String currentTransitionID;
    /** Current Transition name */
    private String currentTransitionName;

    /**
     * Creates a new WorkflowManagerBean
     */
    public WorkflowManagerBean() {
        this.xmlFormatter = new XMLFormatter();
    }

    /**
     * Get current workflow ID
     * @return current workflow ID
     */
    public String getCurrentWorkflowID() {
        return this.currentWorkflowID;
    }

    /**
     * Set current workflow ID
     * This is used when creating steps / transitions to track which workflow
     * they will belong to.
     * @param currentWorkflowID the current workflow ID
     */
    public void setCurrentWorkflowID(String currentWorkflowID) {
        this.currentWorkflowID = currentWorkflowID;
    }

    /**
     * Get current workflow name
     * @return current workflow name
     */
    public String getCurrentWorkflowName() {
        return this.currentWorkflowName;
    }

    /**
     * Set current workflow name
     * @param currentWorkflowName the current workflow name
     */
    public void setCurrentWorkflowName(String currentWorkflowName) {
        this.currentWorkflowName = currentWorkflowName;
    }

    /**
     * Get current step ID
     * @return current step ID
     */
    public String getCurrentStepID() {
        return this.currentStepID;
    }

    /**
     * Set current step ID
     * This is used when creating steps / transitions to track which step
     * they will belong to.
     * @param currentStepID the current step ID
     */
    public void setCurrentStepID(String currentStepID) {
        this.currentStepID = currentStepID;
    }

    /**
     * Get current step name
     * @return current step name
     */
    public String getCurrentStepName() {
        return this.currentStepName;
    }

    /**
     * Set current step name
     * @param currentStepName the current step name
     */
    public void setCurrentStepName(String currentStepName) {
        this.currentStepName = currentStepName;
    }

    /**
     * Get current transition ID
     * @return current transition ID
     */
    public String getCurrentTransitionID() {
        return this.currentTransitionID;
    }

    /**
     * Set current transition ID
     * This is used when creating transitions / transitions to track which transition
     * they will belong to.
     * @param currentTransitionID the current transition ID
     */
    public void setCurrentTransitionID(String currentTransitionID) {
        this.currentTransitionID = currentTransitionID;
    }

    /**
     * Get current transition name
     * @return current transition name
     */
    public String getCurrentTransitionName() {
        return this.currentTransitionName;
    }

    /**
     * Set current transition name
     * @param currentTransitionName the current transition name
     */
    public void setCurrentTransitionName(String currentTransitionName) {
        this.currentTransitionName = currentTransitionName;
    }

    /**
     * Set the stylesheet to use for the menu
     * @param stylesheetFileName the stylesheet path
     */
    public void setStylesheet(String stylesheetFileName) {
        // set the XML formatter stylesheet
        xmlFormatter.setStylesheet(stylesheetFileName);
    }

    /**
     * Get a list of workflows
     * @param includeUnpublished true will return both published and unpublished
     * false returns only published workflows
     * @return list of workflows as XML
     */
    public String getAvailableWorkflowsPresentation(boolean includeUnpublished) throws PersistenceException {
        WorkflowList workflowList = getAvailableWorkflows(includeUnpublished);
        return xmlFormatter.getPresentation(workflowList.getXML());
    }

    /**
     * Get a list of workflows that support a specific type of object
     * @param objectType the object type that the workflow must support
     * @param subTypeID the sub type ID that the workflow must support
     * @return list of workflows as XML
     * @see net.project.workflow.WorkflowList#getXML()
     */
    public String getWorkflowsForTypePresentation(String objectType, String subTypeID) throws PersistenceException {
        return xmlFormatter.getPresentation(getWorkflowsForType(objectType, subTypeID).getXML());
    }

    /**
     * Return transformed XML for list of Steps belonging
     * to specified workflow ID.
     * <br>
     * <b>Preconditions:</b><br>
     * setStylesheet must be called prior to this method.
     *
     * @param workflowID the workflow to get the steps for
     * @return transformed XML string
     * @see #setStylesheet
     */
    public String getStepsPresentation(String workflowID) {
        // Fetch list of steps for workflow
        try {
            StepList stepList = getSteps(workflowID);
            return xmlFormatter.getPresentation(stepList.getXML());
        } catch (PersistenceException pe) {
            return "";
        }
    }

    /**
     * Return transformed XML for list of Steps belonging
     * to specified workflow ID, excluding a particular step.
     * This is used to get the list of "to steps" for a transtion - i.e.
     * the "from step" must be excluded.
     * <br>
     * <b>Preconditions</b><br>
     * setStylesheet() must be called prior to this method.<br>
     * <br>
     * @param workflowID the workflow to get the steps for
     * @param excludeStepID the step to exclude
     * @return transformed XML string
     * @see #setStylesheet
     */
    public String getStepsPresentation(String workflowID, String excludeStepID) {
        // Fetch list of steps for workflow
        try {
            StepList stepList = getSteps(workflowID, excludeStepID);
            // Return transformed XML
            return xmlFormatter.getPresentation(stepList.getXML());
        } catch (PersistenceException pe) {
            return "";
        }
    }

    /**
     * Return transformed XML for list of transitions belonging
     * to specified step -- (where a transitions belongs to a step
     * if its "begin_step_id" matches the specified step.
     * <br>
     * <b>Preconditions</b><br>
     * setStylesheet() must be called prior to this method.<br>
     * <br>
     * @param stepID the step to get the transitions for
     * @return transformed XML string
     * @see #setStylesheet
     */
    public String getStepTransitionsPresentation(String stepID) throws PersistenceException {
        TransitionList transitionList = getStepTransitions(stepID);
        // Return transformed XML
        return xmlFormatter.getPresentation(transitionList.getXML());
    }

    /**
     * Return transformed XML for list of transitions belonging
     * to specified workflow.
     * <br>
     * <b>Preconditions</b><br>
     * setStylesheet() must be called prior to this method.<br>
     * <br>
     * @param workflowID the workflow to get the transitions for
     * @return transformed XML string
     * @see #setStylesheet
     */
    public String getWorkflowTransitionsPresentation(String workflowID) throws PersistenceException {
        TransitionList transitionList = getWorkflowTransitions(workflowID);
        // Return transformed XML
        return xmlFormatter.getPresentation(transitionList.getXML());
    }

    /**
     * Return transformed XML for list of groups belonging to specified
     * step.
     * <br>
     * <b>Preconditions</b><br>
     * setStylesheet() must be called prior to this method.<br>
     * <br>
     * @param stepID the step to get the groups for
     * @return transformed XML string
     * @see #setStylesheet
     */
    public String getStepGroupsPresentation(String stepID) throws PersistenceException {
        StepGroupList stepGroupList = getGroupsForStep(stepID);
        return xmlFormatter.getPresentation(stepGroupList.getXML());
    }

    /**
     * Return transformed XML for list of rules belonging to specified
     * transition.
     * <br>
     * <b>Preconditions</b><br>
     * setStylesheet() must be called prior to this method.<br>
     * <br>
     * @param transitionID the transition to get rules for
     * @return transformed XML string
     * @see #setStylesheet
     */
    public String getRulesPresentation(String transitionID) throws PersistenceException {
        RuleList ruleList = getRulesForTransition(transitionID);
        return xmlFormatter.getPresentation(ruleList.getXML());
    }

}

