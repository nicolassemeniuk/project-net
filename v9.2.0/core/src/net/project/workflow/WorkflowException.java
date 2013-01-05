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
  * Abstract implementation of IWorkflowException to facilitate the creation
  * of other exceptions.
  */
public abstract class WorkflowException extends net.project.base.PnetException
        implements IWorkflowException, ErrorCodes {

    /** Workflow error code */
    private ErrorCodes.Code errorCode;
    private XMLFormatter xmlFormatter = new XMLFormatter();
    
    /**
      * Creates WorkflowException
      */
    public WorkflowException() {
        super();
    }

    /**
      * Creates WorkflowException specifying a message
      */
    public WorkflowException(String message) {
        super(message);
    }

    /**
     * Creates a new WorkflowException with the specified message and
     * causing throwable.
     * @param message detailed message
     * @param cause the causing exception
     */
    public WorkflowException(String message, Throwable cause) {
        super(message, cause);
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
      * Return XML string including XML version tag
      * @return the XML string
      */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }    
    
    /**
      * Return XML string
      * @return the XML string
      */
    public java.lang.String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<workflow_exception>\n");
        if (this.errorCode != null) {
            xml.append("<error_code>" + quote(this.errorCode.toString()) + "</error_code>");
        } else {
            xml.append("<error_code/>");
        }
        xml.append("<default_message>" + quote(getMessage()) + "</default_message>");
        xml.append("</workflow_exception>\n");

        return xml.toString();
    }


    /**
      * Set the stylesheet to use for the menu
      * @param stylesheet the stylesheet path
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

    /**
      * quotes string to HTML, turns null strings into empty strings
      * @param s the string
      * @return the quotes string
      */
    private String quote(String s) {
        return XMLUtils.escape(s);
    }

}

