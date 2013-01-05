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

public class Quantity {

    /**
     * The actual value of this quantity.
     */
    private Number value = null;

    /**
     * The unit of measuer of this quantity.
     */
    private UnitOfMeasure uom = null;

    public Quantity() {
        // Do Nothing
    }

    public Quantity(Number value, UnitOfMeasure uom) {
        this.value = value;
        this.uom = uom;
    }

    public Number getValue() {
        return this.value;
    }
    
    public UnitOfMeasure getUnitOfMeasure() {
        return this.uom;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public void setUnitOfMeasure(UnitOfMeasure uom) {
        this.uom = uom;
    }

    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement("Quantity");
	    doc.addElement("Value", this.getValue());

            doc.startElement("UnitOfMeasure");
	    doc.addElement(this.getUnitOfMeasure().getXMLDocument());
	    doc.endElement();
            
            doc.endElement();
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }

    /**
     * Populates this quantity from the xml element.
     * @param element the xml element from which to populate this quantity
     */
    protected void populate(Element element) {
        
        // Iterate over each child element of this Quantity element
        // and handle each one
        Element childElement = null;
        for (Iterator it = element.getChildren().iterator(); it.hasNext(); ) {
            childElement = (Element) it.next();

            if (childElement.getName().equals("Value")) {
                
                this.setValue(new Integer(childElement.getTextTrim()));
            
            } else if (childElement.getName().equals("UnitOfMeasure")) {
                
                this.setUnitOfMeasure(UnitOfMeasure.create(childElement));
            
            } 
	}

    }

    // Static Members

    /**
     * Creates a payment information from the specified payment information element.
     * @param paymentInformationElement the xml element from which to create the
     * paymentInformation
     * @return the payment information
     */
    public static Quantity create(org.jdom.Element quantityElement) {
        Quantity quantity = new Quantity();
        quantity.populate(quantityElement);
        return quantity;
    }
}
