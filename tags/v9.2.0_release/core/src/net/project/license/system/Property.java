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
package net.project.license.system;

import java.util.Iterator;

import org.jdom.Element;

/**
 * Represents a simple property that has a name and a value.
 * Provides mechanisms for converting to and from an XML structure.
 */
public class Property {

    //
    // Static members
    //

    /**
     * Creates a Property object from the specified XML Element.
     * @param element the element that represents the property
     * @return the Property object, or null if the element is not a Property element
     */
    public static Property create(Element element) {
        Property property = null;

        if (element.getName().equals("Property")) {
            property = new Property();
            property.populate(element);
        }

        return property;
    }

    //
    // Instance members
    //

    private PropertyName name = null;

    private String value = null;

    /**
     * Creates an empty Property.
     */
    private Property() {
        // DO nothing
    }

    /**
     * Creates a new property by making a defensive copy of the specified property.
     * @param source the source property
     */
    Property(Property source) {
        this.name = source.name;
        this.value = source.value;
    }

    /**
     * Creates a property with the specified name.
     */
    Property(PropertyName name, String value) {
        this();
        setName(name);
        this.value = value;
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append(super.toString()).append(" - ");
        s.append("[Name: ").append(getName().getName()).append("] "); 
        s.append("[Value: ").append(getValue()).append("] "); 
        return s.toString();
    }

    void setName(PropertyName name) {
        this.name = name;
    }

    public PropertyName getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    /**
     * Returns this Property as an XML Element.
     * @return the element for this property
     */
    public Element getXMLElement() {
        Element rootElement = new Element("Property");
        rootElement.addContent(getName().getXMLElement());
        rootElement.addContent(new Element("Value").addContent(getValue()));
        return rootElement;

    }

    /**
     * Populates this property object from the specified element.
     * @param element the property element
     */
    private void populate(Element element) {
        Element childElement = null;
        for (Iterator it = element.getChildren().iterator(); it.hasNext(); ) {
            childElement = (Element) it.next();

            if (childElement.getName().equals("PropertyName")) {
                setName(PropertyName.create(childElement));
            
            } else if (childElement.getName().equals("Value")) {
                this.value = childElement.getTextTrim();

            }
        }

    }

    /**
     * Provides an XML structure of this Property.
     * This structure may be used for presentation purposes.
     */
    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement("Property");
            doc.addElement(getName().getXMLDocument());
            doc.addElement("Value", getValue());
            doc.endElement();
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }

}
