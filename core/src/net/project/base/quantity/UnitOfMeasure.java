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
package net.project.base.quantity;

import java.util.Iterator;

import org.jdom.Element;

/**
 * Represents a unit of measure.
 */
public class UnitOfMeasure {

    /**
     * The id of this uom.
     */
    private String id = null;

    /**
     * The code for this unit of measure.
     */
    private String code = null;

     /**
     * The display name of this uom.
     */
    private String name = null;
    

    /**
     * Creates a UnitOfMeasure with the specified id and name.
     */
    public UnitOfMeasure(String id, String code, String name) {
        setID(id);
        setCode(code);
        setName(name);
    }

    /**
     * Creates an empty UnitOfMeasure .
     */
    public UnitOfMeasure() {
        
    }

    private void setID(String id) {
        this.id = id;
    }

    public String getID() {
        return this.id;
    }

    private void setCode(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return this.code;
    }
    
    
    private void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }


    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement("UnitOfMeasure");
	    doc.addElement("ID", this.getID());
	    doc.addElement("Code", this.getCode());
	    doc.addElement("Name", this.getName());
            doc.endElement();
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }

    /**
     * Populates this unit of measure from the xml element.
     * @param element the xml element from which to populate this unit of measure
     */
    protected void populate(Element element) {
        
        // Iterate over each child element of this unit of measure element
        // and handle each one
        Element childElement = null;
        for (Iterator it = element.getChildren().iterator(); it.hasNext(); ) {
            childElement = (Element) it.next();

            if (childElement.getName().equals("ID")) {
                
                this.setID(childElement.getTextTrim());
            
            } else if (childElement.getName().equals("code")) {
                
                this.setCode(childElement.getTextTrim());
            
            } else if (childElement.getName().equals("Name")) {
                
                this.setName(childElement.getTextTrim());
            
            }  
	}

    }

    // Static Members

    /**
     * Creates a unit of measure from the specified unit of measure element.
     * @param unitOfMeasureElement the xml element from which to create the
     * unitOfMeasure
     * @return the unit of measure
     */
    static UnitOfMeasure create(org.jdom.Element unitOfMeasureElement) {
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.populate(unitOfMeasureElement);
        return uom;
    }
    
    public static final UnitOfMeasure EA = new UnitOfMeasure("100", "ea", "Each");
    
}
