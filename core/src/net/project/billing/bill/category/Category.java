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

import java.util.Iterator;

import org.jdom.Element;

/**
 * Each bill is assigned a category that is used to segregate bills.
 * @author tim
 */
public class Category {

    private CategoryID id = null;
    
    /**
     * Creates a new Category.
     */
    public Category() {
        // Do nothing
    }

    /**
     * Sets this category's id.
     * @param id the category id
     * @see #getID
     */
    public void setID(CategoryID id) {
        this.id = id;
    }
    
    /**
     * Returns this category's id.
     * @return the category id
     * @see #setID
     */
    public CategoryID getID() {
        return this.id;
    }

    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement("Category");
            doc.addElement(this.getID().getXMLDocument());
            doc.endElement();

        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }
    
    /**
     * Populates this category from the xml element.
     * @param element the xml element from which to populate this category
     */
    protected void populate(Element element) {
        
        // Iterate over each child element of this Category element
        // and handle each one
        Element childElement = null;
        for (Iterator it = element.getChildren().iterator(); it.hasNext(); ) {
            childElement = (Element) it.next();

            if (childElement.getName().equals("CategoryID")) {
                
                this.setID(CategoryID.create(childElement));
            
            } 
	}

    }

    // Static Members

    /**
     * Creates a responsible party from the specified responsible party element.
     * @param responsiblePartyElement the xml element from which to create the
     * responsibleParty
     * @return the responsible party
     */
    public static Category create(org.jdom.Element categoryElement) {
        Category category = new Category();
        category.populate(categoryElement);
        return category;
    }
}