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

import java.util.ArrayList;
import java.util.Iterator;

import net.project.persistence.IXMLPersistence;
import net.project.xml.XMLUtils;

/**
 * An object type that may be workflowed
 */
public class WorkflowObjectType implements java.io.Serializable, IXMLPersistence {

    /**
     * Stores sub type information
     */
    private class SubType implements java.io.Serializable {
        private String subTypeID = null;
        private String description = null;

        SubType(String subTypeID, String description) {
            this.subTypeID = subTypeID;
            this.description = description;
        }

        String getID() {
            return this.subTypeID;
        }

        String getDescription() {
            return this.description;
        }
    }

    private String name = null;
    private String description = null;
    private ArrayList subTypes = null;

    /**
     * Creates new WorkflowObjectType
     */
    public WorkflowObjectType() {
        subTypes = new ArrayList();
    }

    void setName(String name) {
        this.name = name;
    }

    void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    /**
     * Add a new sub type to the object type
     * @param id the id of the subtype
     * @param description the description of the sub type
     */
    public void addSubType(String id, String description) {
        subTypes.add(new SubType(id, description));
    }

    public String getXML() {
        return IXMLPersistence.XML_VERSION + getXMLBody();
    }

    public String getXMLBody() {
        Iterator it = null;
        SubType subType = null;
        StringBuffer xml = new StringBuffer();
        xml.append("<object_type>");
        xml.append("<name>" + XMLUtils.escape(getName()) + "</name>");
        xml.append("<description>" + XMLUtils.escape(getDescription()) + "</description>");
        it = subTypes.iterator();
        while (it.hasNext()) {
            subType = (SubType)it.next();
            xml.append("<sub_type>");
            xml.append("<sub_type_id>" + XMLUtils.escape(subType.getID()) + "</sub_type_id>");
            xml.append("<description>" + XMLUtils.escape(subType.getDescription()) + "</description>");
            xml.append("</sub_type>");
        }
        xml.append("</object_type>");
        return xml.toString();
    }
}
