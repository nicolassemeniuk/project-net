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

import java.math.BigDecimal;
import java.util.Iterator;

import org.jdom.Element;

/**
 * Provides a representation of a percentage.
 * For example, <code>17%</code> could be represented as <code>new Percentage("17")</code>.
 * For calculation purposes, the decimal value may be used.  For example,
 * <code>new Percentage("17").getDecimalValue()</code> returns the BigDecimal value of
 * <code>0.17</code>.
 */
public class Percentage {

    //
    // Static members
    //

    /**
     * Creates a Percentage object from the specified XML Element.
     * @param element the element that represents the percentage
     * @return the Percentage object, or null if the element is not a Percentage element
     */
    public static Percentage create(Element element) {
        Percentage percentage = null;

        if (element.getName().equals("Percentage")) {
            percentage = new Percentage();
            percentage.populate(element);
        }

        return percentage;
    }

    //
    // Instance members
    //

    /**
     * The actual value of this percentage.
     */
    private BigDecimal value = null;

    /**
     * Creates an empty percentage.
     */
    private Percentage() {
        // Do nothing
    }

    /**
     * Creates a Percentage based on the specified value.
     * Equivalent to calling <code>Percentage(new BigDecimal(value))</code>.
     * @param value the Percentage value
     */
    public Percentage(String value) {
        this(new BigDecimal(value));
    }

    /**
     * Creates a Percentage based on the specified value.
     * @param value the Percentage value
     */
    public Percentage(BigDecimal value) {
        setValue(value);
    }

    /**
     * Sets this percentage value.
     * @param value the value
     */
    private void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * Returns this percentage's value.
     * @return the value
     */
    public BigDecimal getValue() {
        return this.value;
    }

    /**
     * Returns this percentage's value as a decimal value.
     * For example, <code>17.5</code> is returned as <code>0.175</code>.
     * @return this percentage's decimal value
     */
    public BigDecimal getDecimalValue() {
        // Construct a decimal from this percentage divided by 100
        return getValue().movePointLeft(2);
    }
    
    /**
     * Returns this Money as an XML Element.
     * @return the element for this money
     */
    public Element getXMLElement() {
        Element rootElement = new Element("Percentage");
        rootElement.addContent(new Element("Value").addContent(getValue().toString()));
        return rootElement;

    }

    /**
     * Populates this money object from the specified element.
     * @param element the money element
     */
    private void populate(Element element) {
        Element childElement = null;
        for (Iterator it = element.getChildren().iterator(); it.hasNext(); ) {
            childElement = (Element) it.next();

            if (childElement.getName().equals("Value")) {
                // Value contains a string number suitable for passing
                // to BigDecimal
                setValue(new BigDecimal(childElement.getTextTrim()));
            
            }
        }

    }

    /**
     * Provides an XML structure of this Percentage.
     * This structure may be used for presentation purposes.
     */
    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement("Percentage");
            doc.addElement("Value", getValue());
            doc.endElement();
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }

}
