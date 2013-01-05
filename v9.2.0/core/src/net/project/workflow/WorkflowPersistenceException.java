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

import net.project.xml.XMLFormatter;
import net.project.xml.XMLUtils;

/**
 * Contians a workflow error code and methods to get the XML structure.
 * This can be used in a stylesheet to output any kind of error message.
 *
 * @author Tim
 * @since 09/2000
 */
public class WorkflowPersistenceException extends net.project.persistence.PersistenceException
    implements IWorkflowException, ErrorCodes {

    /** Workflow error code. */
    private ErrorCodes.Code errorCode;
    /** Class to create a HTML representation of this exception. */
    private XMLFormatter xmlFormatter = new XMLFormatter();

    /**
     * Creates WorkflowPersustenceException.
     */
    public WorkflowPersistenceException() {
        super();
    }

    /**
     * Creates WorkflowPersistenceException specifying a message
     *
     * @param message a <code>String</code> containing the text of the exception.
     */
    public WorkflowPersistenceException(String message) {
        super(message);
    }

    /**
     * Creates WorkflowPersistenceException specifying a message and severity.
     *
     * @param message a <code>String</code> containing the text of the exception.
     * @param severity how severe the exception is, or more specifically, how
     * important it is to respond to this exception.
     */
    public WorkflowPersistenceException(String message, String severity) {
        super(message, severity);
    }

    /**
     * Set the error code
     * @param errorCode the error code
     */
    public void setErrorCode(ErrorCodes.Code errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Get the error code
     * @return the error code
     */
    public ErrorCodes.Code getErrorCode() {
        return this.errorCode;
    }

    /**
     * Return XML string including XML version tag.
     *
     * @return the XML string
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
     * Return XML string.
     *
     * @return the XML string
     */
    public java.lang.String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<workflow_exception>\n");
        xml.append("<error_code>" + XMLUtils.escape(this.errorCode.toString()) + "</error_code>");
        xml.append("<default_message>" + XMLUtils.escape(super.getMessage()) + "</default_message>");
        xml.append("</workflow_exception>\n");

        return xml.toString();
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
     * Get propreties presentation based on the
     * current stylesheet and workflow properties XML
     * @return properties presentation
     */
    public String getPropertiesPresentation() {
        return xmlFormatter.getPresentation(getXML());
    }

}
