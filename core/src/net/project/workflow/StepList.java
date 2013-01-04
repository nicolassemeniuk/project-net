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
| StepList is a simple subclass of ArrayList which provides XML
| facilities.                                                         
+----------------------------------------------------------------------*/
package net.project.workflow;

import java.util.ArrayList;

import net.project.persistence.IXMLPersistence;
import net.project.xml.XMLUtils;

/**
  * StepList provides XML facilities for an ArrayList of
  * Steps
  */
public class StepList extends ArrayList
        implements IXMLPersistence, java.io.Serializable {

    private String selectedStepID = null;
    private boolean isIncludeBeginTransitions = false;

    /**
      * Returns the individual Step elements
      * @return XML elements as string
      */
    private String getXMLElements() {
        StringBuffer xml = new StringBuffer();
        Step step = null;

        // Add each Step XML element to string
        for (int i=0; i < size(); i++) {
            step = (Step) get(i);
            step.setIncludeBeginTransitions(isIncludeBeginTransitions);
            xml.append(step.getXMLBody());
        }
        return xml.toString();
    }

    /*
        Implement IXMLPersistence
     */
    
    /**
      * Return the StepList XML including the XML version tag
      * @return the XML string
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }    
    
    /**
      * Return the StepList XML without the XML version tag
      * @return XML string
      */
    public java.lang.String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<step_list>\n");
        xml.append("<jsp_root_url>" + XMLUtils.escape(net.project.security.SessionManager.getJSPRootURL()) + "</jsp_root_url>");
        xml.append("<selected_step_id>" + XMLUtils.escape(getSelectedStepID()) + "</selected_step_id>");
        xml.append(getXMLElements());
        xml.append("</step_list>\n");
        return xml.toString();
    }

    public void setSelectedStepID(String selectedStepID) {
        this.selectedStepID = selectedStepID;
    }

    public String getSelectedStepID() {
        return this.selectedStepID;
    }

    void setIncludeBeginTransitions(boolean isIncludeBeginTransitions) {
        this.isIncludeBeginTransitions = isIncludeBeginTransitions;
    }

}
