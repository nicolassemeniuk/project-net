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

import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;

/**
 * IWorkflowable indicates that an object supports the ability to participate
 * in a workflow.  It extends IXMLPersitence since the workflow requires the
 * ability to get an objects properties via XML.
 * It extends IJDBCPersistence so that it can load and store objects.
 */
public interface IWorkflowable extends IXMLPersistence, IJDBCPersistence {

    /**
     * Return id property.  The ID is used to load the workflow object from
     * peristent store.  The id returned will be used to construct and load
     * an object of the correct type using ObjectFactory
     * @return the id property
     * @see net.project.base.ObjectFactory#make
     */
    public String getID();

    /**
     * Return name property
     * @return the name property
     */
    public String getName();

    /**
     * Return objectType property.  This is the object type in the database.
     * @return the objectType property
     * @see net.project.base.ObjectType
     */
    public String getObjectType();

    /**
     * Returns subtype.  This is a type-specific value.<br>
     * Return null if there is no subtype
     * @return the subtype property
     */
    public String getSubType();

    /**
     * Return the version ID property
     * @return the version ID property
     */
    public String getVersionID();

    /**
     * Indicates whether an object will provide its own presentation or
     * simply XML
     * @return true means the getPresentation() method will return HTML presentation<br>
     * false means getXML() should be called
     * @see net.project.persistence.IXMLPersistence#getXML()
     * @see net.project.workflow.IWorkflowable#getPresentation()
     */
    public boolean isSpecialPresentation();

    /**
     * Return HTML presentation or null if the object is to be rendered using XML<br>
     * This method will only be called if isSpecialPresentation() return true.
     * @return the HTML presentation
     * @see net.project.workflow.IWorkflowable#isSpecialPresentation()
     */
    public String getPresentation();

}
