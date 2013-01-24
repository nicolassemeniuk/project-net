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
import java.util.ArrayList;
import java.util.Iterator;

import net.project.code.TableCodeDomain;
import net.project.persistence.PersistenceException;
import net.project.resource.Roster;
import net.project.space.Space;
import net.project.xml.XMLFormatter;

public class DomainListBean implements Serializable {

    /** Translates XML */
    private XMLFormatter xmlFormatter = null;
    /** Current space */
    private Space space = null;

    /**
     * Creates a new DomainListBean
     */
    public DomainListBean() {
        xmlFormatter = new XMLFormatter();
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
     * Set the current space, to be used by other methods.
     * @param space the space
     */
    public void setSpace(Space space) {
        this.space = space;
    }

    /**
     * Return a list of owner IDs as translated from XML by
     * the specified stylesheet.
     * This list contains all persons in the current space.
     * @return the translated XML list of owner IDs
     */
    public String getOwnerIDList() {

        // Create new roster for space
        Roster r = new Roster(this.space);
        r.load();

        // Return translated XML
        return xmlFormatter.getPresentation(r.getXML());
    }

    /**
     * Return a list of strictnessIDs as translated from XML by
     * the specified stylesheet.
     * @return a translated list of strictness IDs
     */
    public String getStrictnessIDList() {
        WorkflowManager manager = new WorkflowManager();
        ArrayList sList = null;
        StringBuffer xml = new StringBuffer();

        manager.setSpace(this.space);
        sList = manager.getStrictnessList();

        xml.append(net.project.persistence.IXMLPersistence.XML_VERSION);
        xml.append("<strictness_list>\n");
        xml.append("<space_id>" + space.getID() + "</space_id>\n");
        xml.append("<jsp_root_url>" + net.project.security.SessionManager.getJSPRootURL() + "</jsp_root_url>");

        if (sList != null) {
            for (int i = 0; i < sList.size(); i++) {
                xml.append(((Strictness)sList.get(i)).getXMLBody());
            }
        }
        xml.append("</strictness_list>\n");

        return xmlFormatter.getPresentation(xml.toString());
    }

    /**
     * Return a list of rule types as translated from XML by
     * the specified stylesheet.
     * @return a translated list of rule types
     */
    public String getRuleTypeListPresentation() {
        WorkflowManager manager = new WorkflowManager();
        ArrayList ruleTypeList = null;
        StringBuffer xml = new StringBuffer();

        manager.setSpace(this.space);
        ruleTypeList = manager.getRuleTypeList();

        xml.append(net.project.persistence.IXMLPersistence.XML_VERSION);
        xml.append("<rule_type_list>\n");
        xml.append("<space_id>" + space.getID() + "</space_id>\n");
        xml.append("<jsp_root_url>" + net.project.security.SessionManager.getJSPRootURL() + "</jsp_root_url>");

        if (ruleTypeList != null) {
            for (int i = 0; i < ruleTypeList.size(); i++) {
                xml.append(((RuleType)ruleTypeList.get(i)).getXMLBody());
            }
        }
        xml.append("</rule_type_list>\n");

        return xmlFormatter.getPresentation(xml.toString());
    }

    /**
     * Return a list of rule statuses as translated from XML by
     * the specified stylesheet.
     * @return a translated list of rule statuses
     */
    public String getRuleStatusListPresentation() {
        WorkflowManager manager = new WorkflowManager();
        ArrayList ruleStatusList = null;
        StringBuffer xml = new StringBuffer();

        manager.setSpace(this.space);
        ruleStatusList = manager.getRuleStatusList();

        xml.append(net.project.persistence.IXMLPersistence.XML_VERSION);
        xml.append("<rule_status_list>\n");
        xml.append("<space_id>" + space.getID() + "</space_id>\n");
        xml.append("<jsp_root_url>" + net.project.security.SessionManager.getJSPRootURL() + "</jsp_root_url>");

        if (ruleStatusList != null) {
            for (int i = 0; i < ruleStatusList.size(); i++) {
                xml.append(((RuleStatus)ruleStatusList.get(i)).getXMLBody());
            }
        }
        xml.append("</rule_status_list>\n");

        return xmlFormatter.getPresentation(xml.toString());
    }

    /**
     * Return a list of priority values
     * Note: this list defaulted to an option list, therefore no stylesheet
     * is required to be set before calling this.
     * @return list of priority codes in HTML form
     */
    public String getPriorityListPresentation() {
        TableCodeDomain dom = new TableCodeDomain();
        dom.setTableName("pn_envelope_version");
        dom.setColumnName("priority_id");
        dom.load();
        return dom.getOptionList();
    }

    /**
     * Return a list of workflow statuses as translated from XML by
     * the specified stylesheet.
     * @return a translated list of statuses
     * @throws PersistenceException if there is a problem getting the status list
     */
    public String getStatusListPresentation() throws PersistenceException {
        WorkflowManager manager = new WorkflowManager();
        ArrayList statusList = null;
        StringBuffer xml = new StringBuffer();

        manager.setSpace(this.space);
        statusList = manager.getStatusList();

        xml.append(net.project.persistence.IXMLPersistence.XML_VERSION);
        xml.append("<status_list>\n");
        xml.append("<space_id>" + space.getID() + "</space_id>\n");
        xml.append("<jsp_root_url>" + net.project.security.SessionManager.getJSPRootURL() + "</jsp_root_url>");

        if (statusList != null) {
            for (int i = 0; i < statusList.size(); i++) {
                xml.append(((Status)statusList.get(i)).getXMLBody());
            }
        }
        xml.append("</status_list>\n");

        return xmlFormatter.getPresentation(xml.toString());
    }

    /**
     * Return list of workflow object types
     * @return list of workflow object types in HTML form
     */
    public String getWorkflowObjectTypesPresentation() {
        WorkflowManager manager = new WorkflowManager();
        StringBuffer xml = new StringBuffer();
        ArrayList typeList = null;
        Iterator it = null;

        manager.setSpace(this.space);
        try {
            typeList = manager.getWorkflowObjectTypes();
        } catch (net.project.persistence.PersistenceException pe) {
            // Empty list
            typeList = new ArrayList();
        }
        xml.append(net.project.persistence.IXMLPersistence.XML_VERSION);
        xml.append("<object_type_list>");
        xml.append("<jsp_root_url>" + net.project.security.SessionManager.getJSPRootURL() + "</jsp_root_url>");
        if (typeList != null) {
            it = typeList.iterator();
            while (it.hasNext()) {
                xml.append(((WorkflowObjectType)it.next()).getXMLBody());
            }
        }
        xml.append("</object_type_list>");

        return xmlFormatter.getPresentation(xml.toString());
    }

}
