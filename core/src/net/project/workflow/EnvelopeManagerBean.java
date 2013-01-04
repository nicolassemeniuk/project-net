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
import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.xml.XMLFormatter;

/**
 * Provies presentation-level routines for EnvelopeManager
 */
public class EnvelopeManagerBean extends EnvelopeManager implements Serializable {

    /**
     * Filter attributes class
     */
    private static class Filter implements java.io.Serializable {
        /** Person to limit envelopes to */
        private String personID = null;
        /** Envelopes belong to this workflowID */
        private String workflowID = null;
        /** Include inactive envelopes also */
        private boolean isInactiveIncluded = false;
        /** priority of envelopes to fetch */
        private String priorityID = null;
    }

    /** For translating xml */
    private XMLFormatter xmlFormatter = null;
    /** Current envelope */
    private Envelope currentEnvelope = null;
    /** Current filter */
    private EnvelopeManagerBean.Filter filter = null;

    /** Ignore warnings when checking rules */
    private boolean isIgnoreWarnings = false;

    /**
     * Create new EnvelopeManagerBean
     */
    public EnvelopeManagerBean() {
        xmlFormatter = new XMLFormatter();
        filter = new Filter();
    }

    /**
     * Set the stylesheet to use for get...Presentation() methods
     * @param stylesheetFileName relative path to stylesheet file
     */
    public void setStylesheet(String stylesheetFileName) {
        xmlFormatter.setStylesheet(stylesheetFileName);
    }

    public void setCurrentEnvelope(Envelope currentEnvelope) {
        this.currentEnvelope = currentEnvelope;
    }

    public Envelope getCurrentEnvelope() {
        return this.currentEnvelope;
    }

    /**
     * Clear the current filter
     */
    public void clearFilter() {
        filter = new Filter();
    }

    /**
     * Set the person to get envelopes for
     * @param personID the person id of the person to filter on
     */
    public void setPersonFilter(String personID) {
        this.filter.personID = personID;
    }

    /**
     * Set the person to get envelopes for.<br>
     * @param user the user who is the person to filter on
     */
    public void setPersonFilter(User user) {
        this.filter.personID = user.getID();
    }

    /**
     * Set the workflow to get envelopes for
     * @param workflowID the workflow id of the workflow to filter on
     */
    public void setWorkflowFilter(String workflowID) {
        this.filter.workflowID = workflowID;
    }

    /**
     * Set whether to include envelopes in an Inactive state
     * @param isInactiveIncluded true means include inactive envelopes,
     * otherwise include only active envelopes
     */
    public void setInactiveIncluded(boolean isInactiveIncluded) {
        this.filter.isInactiveIncluded = isInactiveIncluded;
    }

    /**
     * Set the priority to filter on
     * @param priorityID the priority id of priority to filter on
     */
    public void setPriorityID(String priorityID) {
        this.filter.priorityID = priorityID;
    }

    /**
     * Return transformed XML of workflow list where those workflows
     * are compatible with the current object
     * @return the presentation string
     */
    public String getCompatibleWorkflowListPresentation() {
        try {
            return xmlFormatter.getPresentation(getCompatibleWorkflowList(currentEnvelope.getObjectList()).getXML());
        } catch (NotWorkflowableException e) {
            return "";
        } catch (net.project.persistence.PersistenceException pe) {
            /* Problem getting object list from envelope... get no workflows then */
            return "";
        }
    }

    /**
     * Create the envelope based on the contents of the current envleope
     * @throws EnvelopeException if there is a problem creating the envelope
     * @throws SQLException 
     */
    public void createEnvelope() throws EnvelopeException, SQLException {
        createEnvelope(currentEnvelope);
    }

    /**
     * Return presentation string containing transformed envelope list xml
     * based on preset filter criteria
     *
     * Note that {@link #setStylesheet} must be called prior to calling this
     * method.
     *
     * @return transformed XML string
     * @throws PersistenceException if there is a problem getting the envelopes
     * @see #setStylesheet
     */
    public String getEnvelopeListPresentation() throws PersistenceException {
        if (filter.personID != null) {
            return xmlFormatter.getPresentation(
                getEnvelopesForParticipant(filter.personID, filter.workflowID,
                    filter.isInactiveIncluded, filter.priorityID).getXML());
        } else {
            return xmlFormatter.getPresentation(
                getAllEnvelopes(filter.workflowID, filter.isInactiveIncluded,
                    filter.priorityID).getXML());
        }
    }

    /**
     * Return presentation string containing transformed history list xml
     * based current envelope id
     *
     * Note that {@link #setCurrentEnvelope} and {@link #setStylesheet} must be
     * called prior to calling this method.
     *
     * @return transformed XML string
     * @throws PersistenceException if there is a problem getting the history list
     * @see #setCurrentEnvelope
     * @see #setStylesheet
     */
    public String getHistoryListPresentation() throws PersistenceException {
        return xmlFormatter.getPresentation(getHistoryList(currentEnvelope.getID()).getXML());
    }

    /**
     * Return the envelope content object list
     *
     * @return the list of envelope objects in the envelope's current version
     */
    public EnvelopeVersionObjectList getEnvelopeContentObjects() throws LoadException {
        return super.getEnvelopeContentObjects(currentEnvelope);
    }

    /**
     * Return the presentation for an IWorkflowable object.
     *
     * The presentation is enclosed in &lt;tr>&lt;/tr> tags.
     *
     * @param obj the object implementing the IWorkflowable interface
     * @param stylesheet the stylesheet to use for objects requiring only XML
     * transformation
     * @return the HTML presentation string
     * @throws SQLException 
     */
    public String getPresentation(IWorkflowable obj, String stylesheet) throws SQLException {
        XMLFormatter xmlFormatter = new XMLFormatter();

        StringBuffer presentation = new StringBuffer("");
        presentation.append("<tr><td> ");

        if (obj.isSpecialPresentation()) {
            // Object wishes to use their own presentation
            presentation.append(obj.getPresentation());

        } else {
            // Default presentation is XML tranformation
            xmlFormatter.setStylesheet(stylesheet);
            presentation.append(xmlFormatter.getPresentation(obj.getXML()));

        } // end if

        presentation.append("</td></tr> ");
        presentation.append("<tr class=\"tableLine\"><td class=\"tableLine\" ><img src=\""+ SessionManager.getJSPRootURL() +"/images/spacers/trans.gif\" width=\"1\" height=\"1\" border=\"0\" alt=\"\"/></td></tr> ");
        return presentation.toString();

    } // end ()

    /**
     * Return possible transitions for the current envelope.
     *
     * Please note that {@link #setCurrentEnvelope} and {@link #setStylesheet}
     * need to be called before calling this method.
     *
     * @return transformed xml string
     * @see #setCurrentEnvelope
     * @see #setStylesheet
     */
    public String getAvailableTransitionsPresentation() {
        try {
            return xmlFormatter.getPresentation(getAvailableTransitions(currentEnvelope).getXML());
        } catch (net.project.persistence.PersistenceException pe) {
            // No transitions...
            return "";
        }
    }

    /**
     * Return an visual indiactor of the steps for this workflow, highlighting
     * the current step.
     */
    public String getStepIndicatorPresentation() throws net.project.persistence.PersistenceException {
        return xmlFormatter.getPresentation(getStepsAndTransitions(currentEnvelope).getXML());
    }

    /**
     * Set whether to ignore warnings or not.  Used when rule checking.
     */
    public void setIgnoreWarnings(boolean isIgnoreWarnings) {
        this.isIgnoreWarnings = isIgnoreWarnings;
    }

    private boolean isIgnoreWarnings() {
        return this.isIgnoreWarnings;
    }

    /**
     * Perform transition on current envelope
     * @param transitionID the transition to perform
     * @throws TransitionException if there is a problem performing the transition
     * @throws RuleCheckException if one or more rule checks did not succeed
     * @throws SQLException 
     */
    public void performTransition(String transitionID)
        throws TransitionException, RuleCheckException, SQLException {

        /* Find out if we're ignoring warnings */
        boolean isIgnoreWarnings = isIgnoreWarnings();

        /* Immediately reset it - makes sure we don't ignore warnings on future transition performs */
        setIgnoreWarnings(false);

        /* Do the transition */
        performTransition(this.currentEnvelope, transitionID, isIgnoreWarnings);
    }

    /**
     * Get rule problems presentation.  This is typically called after
     * catching the RuleCheckException thrown by performTransition()
     * @return transformed xml string
     * @see #performTransition must be called before this method
     * @see #setStylesheet must be called before this method
     */
    public String getRuleProblemsPresentation() {
        Iterator it = null;
        Rule.RuleProblem problem = null;
        StringBuffer xml = new StringBuffer();

        xml.append("<rule_problem_list>");
        it = this.ruleProblems.iterator();
        while (it.hasNext()) {
            problem = (Rule.RuleProblem)it.next();
            xml.append(problem.getXMLBody());
        }
        xml.append("</rule_problem_list>");

        return xmlFormatter.getPresentation(net.project.persistence.IXMLPersistence.XML_VERSION + xml);
    }

    /**
     * Get envelope version presentation.
     * @return the transformed xml string
     */
    public String getEnvelopeVersionsPresentation() throws net.project.persistence.PersistenceException {
        return xmlFormatter.getPresentation(getEnvelopeVersions(getCurrentEnvelope()).getXML());
    }

    /**
     * Returns a message regarding the last transition
     * @return the message or empty string if there is no last transition
     */
    public String getLastTransitionMessage() {
        String message = null;
        Transition tran = getLastTransition();
        if (tran != null) {
            message = PropertyProvider.get("prm.workflow.envelopemanagerbean.transition.success.message", new Object[]{tran.getTransitionVerb(), tran.getBeginStepName(), tran.getEndStepName()});
        } else {
            message = "";
        }
        return message;
    }

    /**
     * Returns a message regarding the last transition.  Clears this so that
     * it will return empty string on the next invocation (unless of course
     * another transition occurs
     * @return the message
     * @see #getLastTransitionMessage
     */
    public String consumeLastTransitionMessage() {
        String message = getLastTransitionMessage();
        setLastTransition(null);
        return message;
    }

}
