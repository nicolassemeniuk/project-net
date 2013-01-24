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
|   $Revision: 20541 $
|       $Date: 2010-03-08 12:24:35 -0300 (lun, 08 mar 2010) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.workflow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.notification.NotificationException;
import net.project.notification.NotificationManager;
import net.project.notification.Subscription;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.security.group.Group;
import net.project.security.group.GroupCollection;
import net.project.security.group.GroupException;
import net.project.security.group.GroupProvider;
import net.project.space.Space;
import net.project.xml.XMLFormatter;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * SelectListBean maintains the display for a group select
 * and provides methods to select and deselect members.
 *
 * @author Tim
 */
public class SelectListBean implements Serializable {

    private XMLFormatter xmlFormatter;
    private WorkflowManager manager;

    /** Current space */
    private Space space = null;
    /** Current user */
    private User user = null;

    /** Current workflow id */
    private String currentWorkflowID = null;
    /** Current workflow */
    private Workflow currentWorkflow = null;

    /** Current step id */
    private String currentStepID = null;
    /** Current step */
    private Step currentStep = null;

    // Table maintains all entries
    private Hashtable entryTable = null;

    private boolean isLoaded = false;

    /**
     * The Entry class stores properties
     * it extends the StepGroup class
     */
    private static class Entry extends StepGroup {
        /** Indicates whether the notified flag selected by user */
        boolean isNotifiedSelected = false;
        /** Indicates whether the entry is selected or not */
        boolean isSelected = false;

        Entry() {
        }

        /**
         * Return XML body for this entry
         * @return XML body string
         */
        public String getXMLBody() {
            StringBuffer xml = new StringBuffer();
            xml.append("<entry>\n");
            xml.append(getXMLElements());
            xml.append("<is_selected>" + XMLUtils.escape((isSelected ? "1" : "0")) + "</is_selected>");
            xml.append("<is_notified_selected>" + XMLUtils.escape((isNotifiedSelected ? "1" : "0")) + "</is_notified_selected>");
            xml.append("</entry>\n");
            return xml.toString();
        }
    }

    /**
     * Creates a new select list
     */
    public SelectListBean() {
        this.xmlFormatter = new XMLFormatter();
        this.manager = new WorkflowManager();
    }

    /**
     * Load the SelectListBean.
     * This loads the groups at the current step.
     * Also sets notification preference based on existing subscriptions.
     * Then loads all groups and constructs a hashtable maintaining the entire
     * set.
     * @throws PersistenceException if there is a problem loading groups
     */
    public void load() throws PersistenceException {
        entryTable = new Hashtable();

        // Load selected groups for this step
        loadSelectedGroups();

        // Load all groups for space and update and add to entry table
        loadGroups();

        // I'm loaded
        this.isLoaded = true;
    }

    /**
     * Loads the groups stored at this step.
     * <br>
     * <b>Preconditions:</b><br>
     * <li>None</li><br>
     * <b>Postconditions:</b><br>
     * <li><code>entryTable</code> contains all selected group entries - WITH notification settings</li><br>
     */
    private void loadSelectedGroups() throws PersistenceException {
        Iterator it = null;
        Entry entry = null;
        StepGroup stepGroup = null;

        StepGroupList stepGroupList = manager.getGroupsForStep(this.currentStepID);
        if (stepGroupList != null) {
            it = stepGroupList.iterator();
            while (it.hasNext()) {
                stepGroup = (StepGroup)it.next();

                // Create new entry
                entry = new Entry();
                entry.groupID = stepGroup.groupID;
                entry.stepID = stepGroup.stepID;
                entry.workflowID = stepGroup.workflowID;
                entry.isParticipant = stepGroup.isParticipant;
                entry.crc = stepGroup.crc;
                entry.isLoaded = true;
                entry.isSelected = true;
                entry.setNotified(stepGroup.isNotified());
                entry.isNotifiedSelected = stepGroup.isNotified();
                entryTable.put(entry.groupID, entry);
            } //end for
        } //end if
    }

    /**
     * Loads the groups for the current space.  For those groups which are
     * already in the <code>entryTable</code> (with <code>isSelected</code> set),
     * their names and descriptions are updated.  For those groups not already
     * in <code>entryTable</code>, they are added with isSelected set to
     * <code>false</code>.
     * <br>
     * <b>Preconditions:</b><br>
     * <li><code>entryTable</code> contains selected groups</li><br>
     * <b>Postconditions:</b><br>
     * <li><code>entryTable</code> contains selected and unselected groups</li></br>
     */
    private void loadGroups() throws PersistenceException {
        Entry entry = null;

        // Load all group for space
        Group group = null;
        GroupProvider groupProvider = new GroupProvider();
        GroupCollection groupList = new GroupCollection();
        groupList.setSpace(this.space);
        groupList.loadOwned();

        // Load special group
        try {
            group = groupProvider.newGroup(StepGroup.ENVELOPE_CREATOR_GROUP_ID);
            group.load();
            groupList.add(group);

        } catch (GroupException ge) {
            // No Envelope Creator group

        }

        // Loop through groups, updating entries that are already there
        // and adding new entries
        for (int i = 0; i < groupList.size(); i++) {
            group = (Group)groupList.get(i);

            if (entryTable.containsKey(group.getID())) {
                // This group already in entry table
                entry = (Entry)entryTable.get(group.getID());
            } else {
                // Group not in step, so add to entry table
                entry = new Entry();
                entry.groupID = group.getID();
                entry.stepID = this.currentStepID;
                entry.workflowID = this.currentWorkflowID;
                // Set some Participant and Notified flags to Yes
                entry.setParticipant(true);
                entry.isNotifiedSelected = true;
                entryTable.put(entry.groupID, entry);
            }

            // Now update the entry
            if (group.isPrincipal()) {
                entry.isPrincipal = true;
            } else {
                entry.isPrincipal = false;
            }
            entry.name = group.getName();
            entry.description = group.getDescription();


        } //end for
    }

    /**
     * Load the workflow to which this select list belongs
     * Sets attribute currentWorkflow
     * @throws PersistenceException if there is a problem loading the workflow
     * @see this.currentWorkflow
     */
    private void loadCurrentWorkflow() throws PersistenceException {
        Workflow workflow = new Workflow();
        workflow.setID(currentWorkflowID);
        workflow.load();
        setCurrentWorkflow(workflow);
    }

    /**
     * Load the step to which this select list belongs
     * Sets attribute currentStep
     * @throws PersistenceException if there is a problem loading the step
     */
    private void loadCurrentStep() throws PersistenceException {
        Step step = new Step();
        step.setUser(this.user);
        step.setID(currentStepID);
        step.load();
        setCurrentStep(step);
    }

    public void clear() {
        this.space = null;
        this.user = null;
        this.entryTable = null;
        this.isLoaded = false;
        this.currentWorkflowID = null;
        this.currentWorkflow = null;
        this.currentStepID = null;
        this.currentStep = null;
    }

    /**
     * Store the select list.  This involves a number of steps:<br>
     * <li>De-selected entries are removed from step and subscriptions to step removed</li>
     * <li>Selected entries are added to step and subscriptions created (if isNotifiedSelected is set)
     * or subscriptions removed (if isNotifiedSelected not set and they were previously being notified)</li>
     * @throws PersistenceException if there is a problem storing
     */
    public void store() throws PersistenceException {
        Entry entry = null;
        boolean doAddSubscription = false;
        boolean doRemoveSubscription = false;
        Enumeration keys = entryTable.keys();

        ArrayList removeSubscriptions = new ArrayList();
        ArrayList addSubscriptions = new ArrayList();

        while (keys.hasMoreElements()) {
            entry = (Entry)entryTable.get(keys.nextElement());
            entry.setUser(this.user);

            // Must add subscription if the entry is selected and isNotifiedSelected is true
            // AND it is an existing entry not previously being notified OR it is a new entry
            doAddSubscription = entry.isSelected && entry.isNotifiedSelected &&
                ((entry.isLoaded && !entry.isNotified()) || !entry.isLoaded);

            // Must remove subscription if the entry was previously being notified
            // AND is no longer selected or is still selected, but isNotifiedSelected is false
            doRemoveSubscription = entry.isNotified() &&
                (!entry.isSelected || (entry.isSelected && !entry.isNotifiedSelected));

            if (doAddSubscription) {
                addSubscriptions.add(entry.getGroupID());
            }
            if (doRemoveSubscription) {
                removeSubscriptions.add(entry.getGroupID());
            }

            if (entry.isSelected) {
                // Store it (either modify or create)
                entry.store();
            } else if (!entry.isSelected && entry.isLoaded) {
                // Remove it
                entry.remove();
            }

        } //end while


        // Now update subscriptions
        storeSubscriptions(addSubscriptions, removeSubscriptions);

    }

    /**
     * Store the subscriptions for this select list.  This will first try and load
     * the subscription object for the current step.  Groups are added and removed
     * from the subscription.
     * @param addSubscriptions the collection of group ids to be added
     * @param removeSubscriptions the collection of group ids to be removed
     * @throws PersistenceException if there is a problem storing the subscriptions
     */
    private void storeSubscriptions(Collection addSubscriptions, Collection removeSubscriptions) throws PersistenceException {
        Step step = null;
        Workflow workflow = null;
        String groupID = null;
        String subscriptionID = null;
        Iterator it = null;
        Subscription subscription = null;

        // No subscription is created / modified if there are no subscribers to add or remove
        if (addSubscriptions.size() == 0 && removeSubscriptions.size() == 0) {
            return;
        }

        // Get the current workflow and step and load the subscription for the current
        // step if there is one.
        workflow = getCurrentWorkflow();
        step = getCurrentStep();

        try {
            subscriptionID = step.getSubscriptionID();
            if (subscriptionID != null) {
                subscription = NotificationManager.getSubscription(subscriptionID);

            } else {
                // Create new subscription object
                subscription = new Subscription();
                subscription.setCreatedByID(this.user.getID());
                subscription.setName(PropertyProvider.get("prm.workflow.selectlist.subscription.name.message", new Object[]{step.getName(), workflow.getName()}));
                subscription.setDescription(PropertyProvider.get("prm.workflow.selectlist.subscription.description.message", new Object[]{step.getName(), workflow.getName()}));
                subscription.setCustomMessage(PropertyProvider.get("prm.workflow.selectlist.subscription.custom.message", new Object[]{step.getName(), workflow.getName()}));
                // Notification sent as soon as event occurs
                subscription.setDeliveryIntervalID(net.project.notification.IDeliverable.IMMEDIATE_DELIVERY_INTERVAL);
                // Subscription is not based on an object type - it is based on a specific object (Step for given step_id)
                subscription.setIsTypeSubscription(false);
                subscription.setTargetObjectID(this.currentStepID);
                subscription.addNotificationType(Step.ENTER_EVENT_ID);
                subscription.setStatus(true);
            }
            subscription.setUser(this.user);
            subscription.setSpaceID(this.space.getID());

            // Add all groups in the addSubscriptions collection to the subscription
            it = addSubscriptions.iterator();
            while (it.hasNext()) {
                groupID = (String)it.next();
                subscription.addSubscriber(groupID, net.project.notification.IDeliverable.EMAIL_DELIVERABLE);
            }

            // Remove all groups in the removeSubscriptions collection from the subscription
            it = removeSubscriptions.iterator();
            while (it.hasNext()) {
                groupID = (String)it.next();
                subscription.removeSubscriber(groupID);
            }

            subscription.store();

        } catch (NotificationException ne) {
        	Logger.getLogger(SelectListBean.class).error("Error storing subscriptions in SelectListBean " + ne);
            throw new PersistenceException("Error occurred storing notification subscriptions.", ne);
        }

        // If this is a new subscription, update the step to record the subscription id
        if (subscriptionID == null) {
            step.setSubscriptionID(subscription.getID());
            step.store();
        }

    }

    /**
     * Set the current space
     * This must be done before load() is called
     * @param space the current space
     */
    public void setSpace(Space space) {
        this.space = space;
        this.manager.setSpace(space);
    }

    /**
     * Set the current user
     * This must be done before load() is called
     * @param user the current user
     */
    public void setUser(User user) {
        this.user = user;
        this.manager.setUser(user);
    }

    /**
     * Set the current step ID
     * This must be done before load() is called
     * @param stepID the step ID
     */
    public void setCurrentStepID(String stepID) {
        this.currentStepID = stepID;
    }

    /**
     * Set the current workflowID
     * This must be done before load() is called
     * @param workflowID the workflow ID
     */
    public void setCurrentWorkflowID(String workflowID) {
        this.currentWorkflowID = workflowID;
    }

    /**
     * Indicate whether select lists have been loaded
     * @return true if the select list has been loaded
     */
    public boolean isLoaded() {
        return this.isLoaded;
    }

    /**
     * Set the isLoaded property
     * @param isLoaded property
     */
    public void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    /**
     * Return the workflow to which this select list
     * @return the workflow
     */
    public Workflow getCurrentWorkflow() throws PersistenceException {
        if (this.currentWorkflow == null) {
            loadCurrentWorkflow();
        }
        return this.currentWorkflow;
    }

    void setCurrentWorkflow(Workflow currentWorkflow) {
        this.currentWorkflow = currentWorkflow;
    }

    /**
     * Return the step to which this select list belongs
     * @return the step
     */
    public Step getCurrentStep() throws PersistenceException {
        if (this.currentStep == null) {
            loadCurrentStep();
        }
        return this.currentStep;
    }

    void setCurrentStep(Step currentStep) {
        this.currentStep = currentStep;
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
     * Return the transformed entry XML
     * @return the transformed XML as a string
     */
    public String getEntryListPresentation() {
        return xmlFormatter.getPresentation(getXML());
    }

    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }

    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<entry_list>\n");
        xml.append("<jsp_root_url>" + XMLUtils.escape(net.project.security.SessionManager.getJSPRootURL()) + "</jsp_root_url>");
        Enumeration keys = entryTable.keys();
        while (keys.hasMoreElements()) {
            xml.append(((Entry)entryTable.get(keys.nextElement())).getXMLBody());
        }
        xml.append("</entry_list>\n");
        return xml.toString();
    }

    public void select(String id) {
        ((Entry)entryTable.get(id)).isSelected = true;
    }

    public void deselect(String id) {
        ((Entry)entryTable.get(id)).isSelected = false;
    }

    public void setParticipation(String participation) {
        // Split participation at ":"
        String id = participation.substring(0, participation.indexOf(":"));
        String value = participation.substring(participation.indexOf(":") + 1);
        // Set participation for person
        Entry e = (Entry)entryTable.get(id);
        e.isParticipant = (value.equals("1") ? true : false);
    }

    public void setNotification(String notification) {
        // Split at ":"
        String id = notification.substring(0, notification.indexOf(":"));
        String value = notification.substring(notification.indexOf(":") + 1);
        // Set notification for person
        Entry e = (Entry)entryTable.get(id);
        e.isNotifiedSelected = (value.equals("1") ? true : false);
    }

    /*=================================================================================================
       Error stuff
       ================================================================================================*/

    private ValidationErrors errors = new ValidationErrors();

    public void clearErrors() {
        errors.clearErrors();
    }

    /**
     * Validate the contents of the SelectList
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
     * Generates an error if the workflow to which the step to which this list belongs is published
     * @throws PersistenceException if there was a problem determining the workflow
     */
    public void validateUnpublished() throws PersistenceException {
        Workflow workflow = null;
        workflow = getCurrentWorkflow();
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
