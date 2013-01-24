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
package net.project.billing.bill.category;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

/**
 * Enumeration of Category IDs.
 * @author tim
 */
public class CategoryID {

    //
    // Static members
    //

    /**
     * Maintains the finite list of ids.
     */
    private static List categoryIDs = new ArrayList();
    

    /**
     * Returns the CategoryID object for the specified string id.
     * @param id for which to get the matching CategoryID
     * @return the object with the specified internal id or null if
     * there one is not found
     */
    public static CategoryID forID(String id) {
        CategoryID categoryID = null;
        boolean isFound = false;
        
        // Loop over all the items, finding the one with matching internal id
        for (Iterator it = CategoryID.categoryIDs.iterator(); it.hasNext() & !isFound; ) {
            categoryID = (CategoryID) it.next();
            if (categoryID.getID().equals(id)) {
                isFound = true;                
            }
        }

        return categoryID;
    }


    //
    // Instance members
    //
    
    /** The internal id of this category id. */
    private String id = null;
    
    /**
     * Creates a new CategoryID.
     * @param id the internal id
     */
    public CategoryID(String id) {
        this.id = id;
        CategoryID.categoryIDs.add(this);
    }

    /**
     * Creates an empty CategoryID.
     */
    public CategoryID() {
        CategoryID.categoryIDs.add(this);
    }

    /**
     * Returns the internal id of this group category id.
     */
    public String getID() {
        return this.id;
    }

    /**
     * Setss the internal id of this group category id.
     */
    public void setID(String id) {
        this.id = id;
    }
    
    public boolean equals(Object obj) {
        if (obj != null &&
            obj instanceof CategoryID &&
            ((CategoryID) obj).getID().equals(this.getID())) {
            
            return true;
        }
        
        return false;
    }

    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement("CategoryID");
            doc.addElement("ID", this.getID());
            doc.endElement();

        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }

    /**
     * Populates this categoryID from the xml element.
     * @param element the xml element from which to populate this categoryID
     */
    protected void populate(Element element) {
        
        // Iterate over each child element of this CategoryID element
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
     * Creates a category ID from the specified category ID element.
     * @param categoryIDElement the xml element from which to create the
     * categoryID
     * @return the category id
     */
    static CategoryID create(org.jdom.Element categoryIDElement) {
        CategoryID categoryID = new CategoryID();
        categoryID.populate(categoryIDElement);
        return categoryID;
    }


    //
    // Static Constants located at end of class to ensure all other
    // static initializations occur before this
    //
    
    /** License Usage bill category, currently <code>100</code>. */
    public static final CategoryID LICENSE_USAGE_TYPE_A = new CategoryID("100");
    
    /** License Maintenance bill category, currently <code>200</code>. */
    //public static final CategoryID LICENSE_MAINTENANCE = new CategoryID("200");

    /** License Maintenance bill category, currently <code>1000</code>. */
    public static final CategoryID LICENSE_MAINTENANCE_TYPE_A = new CategoryID("1000");
    public static final CategoryID LICENSE_MAINTENANCE_TYPE_B = new CategoryID("2000");
    public static final CategoryID LICENSE_MAINTENANCE_TYPE_C = new CategoryID("3000");
    
}