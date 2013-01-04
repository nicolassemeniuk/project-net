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
package net.project.billing.bill.group;

import java.util.Iterator;

import org.jdom.Element;


/**
 * Provides a type of group.
 */
public class GroupType {

    /** The id of this group type. */
    private GroupTypeID id = null;

    /** The description of this group type. */
    private String description = null;

    /**
     * Creates a new group type.
     */
    protected GroupType() {
        // Nothing
    }

    /**
     * Creates a new group type.
     */
    public GroupType(GroupTypeID groupTypeID) {

        this.setID(groupTypeID);
	        
    }

    /**
     * Sets the id of this group type.
     * @param id the group type id
     * @see #getID
     */
    protected void setID(GroupTypeID id) {
        this.id = id;
    }

    /**
     * Returns this type's id.
     * @return the id of this type
     * @see #setID
     */
    public GroupTypeID getID() {
        return this.id;
    }

    /**
     * Sets this group type's description.
     * @param description the description
     */
    protected void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Returns this group type's description.
     * @return the description
     */
    protected String getDescription() {
        return this.description;
    }

    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement("GroupType");

            doc.addElement(this.getID().getXMLDocument());
            doc.addElement("Description", this.getDescription());
            
            doc.endElement();
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }


    /**
     * Populates this group type from the xml element.
     * @param element the xml element from which to populate this group type
     */
    protected void populate(Element element) {
        
        // Iterate over each child element of this group type element
        // and handle each one
        Element childElement = null;
        for (Iterator it = element.getChildren().iterator(); it.hasNext(); ) {
            childElement = (Element) it.next();

            if (childElement.getName().equals("GroupTypeID")) {
                
                this.setID(GroupTypeID.create(childElement));
            
            } else if (childElement.getName().equals("Description")) {
		this.setDescription(childElement.getTextTrim());
	    }
       }

    }

    // Static Members

    /**
     * Creates a group type from the specified group type element.
     * @param groupTypeElement the xml element from which to create the
     * groupType
     * @return the group type
     */
    public static GroupType create(org.jdom.Element groupTypeElement) {
        GroupType groupType = new GroupType();
        groupType.populate(groupTypeElement);
        return groupType;
    }
  
}