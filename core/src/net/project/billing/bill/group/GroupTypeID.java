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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;



/**
 * Enumeration of ids that may be used to group bills.
 * The actual names of these are arbitrary.  However, when bills are listed
 * they may be grouped by the type of their bill group.  Hence bills that
 * may be grouped together should share the same group type id.
 * @author tim
 */
public class GroupTypeID {

    //
    // Static members
    //

    /**
     * Maintains the finite list of ids.
     */
    private static List typeIDs = new ArrayList();
    

    /**
     * Returns the GroupTypeID object for the specified string id.
     * @param id for which to get the matching GroupTypeID
     * @return the object with the specified internal id or null if
     * there one is not found
     */
    public static GroupTypeID forID(String id) {
        GroupTypeID groupTypeID = null;
        boolean isFound = false;
        
        // Loop over all the items, finding the one with matching internal id
        for (Iterator it = GroupTypeID.typeIDs.iterator(); it.hasNext() & !isFound; ) {
            groupTypeID = (GroupTypeID) it.next();
            if (groupTypeID.getID().equals(id)) {
                isFound = true;                
            }
        }

        return groupTypeID;
    }


    //
    // Instance members
    //
    
    /** The internal id of this group type id. */
    private String id = null;
    
    /**
     * Creates a new GroupTypeID.
     * @param id the internal id
     */
    private GroupTypeID(String id) {
        this.id = id;
        GroupTypeID.typeIDs.add(this);
    }

    /**
     * Creates an empty GroupTypeID.
     */
    private GroupTypeID() {
	GroupTypeID.typeIDs.add(this);    
    }

    /**
     * Sets the internal id of this group type id.
     */
    public void setID(String id) {
        this.id = id;
    }

    /**
     * Returns the internal id of this group type id.
     */
    public String getID() {
        return this.id;
    }
    
    public boolean equals(Object obj) {
        if (obj != null &&
            obj instanceof GroupTypeID &&
            ((GroupTypeID) obj).getID().equals(this.getID())) {
            
            return true;
        }
        
        return false;
    }

    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement("GroupTypeID");
            doc.addElement("ID", this.getID());
            doc.endElement();

        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }

    /**
     * Populates this group Type ID from the xml element.
     * @param element the xml element from which to populate this group type ID
     */
    protected void populate(Element element) {
        
        // Iterate over each child element of this groupTypeID element
        // and handle each one
        Element childElement = null;
        for (Iterator it = element.getChildren().iterator(); it.hasNext(); ) {
            childElement = (Element) it.next();

            if (childElement.getName().equals("ID")) {
                
                this.setID(childElement.getTextTrim());
            
            } 
	}

    }

    // Static Members

    /**
     * Creates a group type ID from the specified group type id element.
     * @param groupTypeIDElement the xml element from which to create the
     * groupTypeID
     * @return the  groupTypeID
     */
    static GroupTypeID create(org.jdom.Element groupTypeIDElement) {
        GroupTypeID groupTypeID = new GroupTypeID();
        groupTypeID.populate(groupTypeIDElement);
        return groupTypeID;
    }
    //
    // Static Constants located at end of class to ensure all other
    // static initializations occur before this
    //
    
    /** For grouping by charge code bills. */
    public static final GroupTypeID CHARGE_CODE = new GroupTypeID("100");
    
    /** For grouping by credit card bills. */
    public static final GroupTypeID CREDIT_CARD = new GroupTypeID("200");
    
    /** For grouping by trial license bills. */
    public static final GroupTypeID TRIAL = new GroupTypeID("300");
    
    /** An unspecified grouping, used if no particular grouping is required. */
    public static final GroupTypeID UNSPECIFIED = new GroupTypeID("1000");

}