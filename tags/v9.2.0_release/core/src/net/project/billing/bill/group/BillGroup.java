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
 * A BillGroup provides a grouping mechanism for bills.
 * Each bill belongs to a group.
 * @author tim
 */
public class BillGroup {

    /** 
     * The type of group.  Every group must have a well defined type for
     * ordering purposes and to provide a "namespace" against which different
     * values may be grouped.
     * Values are simply strings without semantic meaning.
     */
    private GroupType groupType = null;

    private GroupTypeID groupTypeID = null;
    
    /** The value used for grouping. */
    private String value = null;

    /** The description for this group. */
    private String description = null;
    
    /**
     * Creates an empty BillGroup.
     */
    public BillGroup() {
        // Do Nothing
    }

    public void setGroupType(GroupType groupType) {
        this.groupType = groupType;
    }
    
    public GroupType getGroupType() {
        return this.groupType;
    }

    public void setGroupTypeID(GroupTypeID groupTypeID) {
        this.groupTypeID = groupTypeID;
    }

    public GroupTypeID getGroupTypeID() {
        return this.groupTypeID;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return this.description;
    }

    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement("BillGroup");

            doc.addElement(this.getGroupType().getXMLDocument());

            doc.addElement(this.getGroupTypeID().getXMLDocument());

            doc.addElement("Value", this.getValue());
	    doc.addElement("Description", this.getDescription());
            
            doc.endElement();
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }

    /**
     * Populates this bill group from the xml element.
     * @param element the xml element from which to populate this bill group
     */
    protected void populate(Element element) {
        
        // Iterate over each child element of this BillGroup element
        // and handle each one
        Element childElement = null;
        for (Iterator it = element.getChildren().iterator(); it.hasNext(); ) {
            childElement = (Element) it.next();

            if (childElement.getName().equals("GroupType")) {
                
                this.setGroupType(GroupType.create(childElement));
            
            } else if (childElement.getName().equals("GroupTypeID")) {
                
                this.setGroupTypeID(GroupTypeID.create(childElement));
            
            } else if (childElement.getName().equals("Value")) {
                this.setValue(childElement.getTextTrim());
                
            } else if (childElement.getName().equals("Description")) {
		this.setDescription(childElement.getTextTrim());
	    }
       }

    }

    // Static Members

    /**
     * Creates a bill group from the specified bill group element.
     * @param billGroupElement the xml element from which to create the
     * billGroup
     * @return the bill group
     */
    public static BillGroup create(org.jdom.Element billGroupElement) {
        BillGroup billGroup = new BillGroup();
        billGroup.populate(billGroupElement);
        return billGroup;
    }
    
}