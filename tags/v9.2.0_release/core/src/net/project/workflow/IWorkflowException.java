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

 package net.project.workflow;
/*----------------------------------------------------------------------+
|                                                                       
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/

/**
  * Flags something as a workflow exception
  * Defines the set of methods that it must implement.
  */
public interface IWorkflowException {

    /**
      * Set the error code
      * @param errorCode the error code
      */
    public void setErrorCode(ErrorCodes.Code errorCode);

    /**
      * Get the error code
      * @return the error code
      */
    public ErrorCodes.Code getErrorCode();

    /**
      * Return XML string including XML version tag
      * @return the XML string
      */
    public String getXML();
    
    /**
      * Return XML string
      * @return the XML string
      */
    public java.lang.String getXMLBody();

    /**
      * Set the stylesheet to use for the menu
      * @param stylesheet the stylesheet path
      */
    public void setStylesheet(String stylesheetFileName);

    /**
      * Get propreties presentation based on the
      * current stylesheet and workflow properties XML
      * @return properties presentation
      */
    public String getPropertiesPresentation();

}
