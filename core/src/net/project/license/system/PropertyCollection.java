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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.project.xml.XMLUtils;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Provides a collection of license properties that are persisited in an
 * encrypted format.
 * These are critical properties that must not be changed at an installation.
 * They may only be changed by providing an encrypted string.
 */
// MUST be Package access only - this class must be generally unavailable
class PropertyCollection extends ArrayList {
    
    //
    // Static members
    //

    /**
     * The current XML schema major version number for this object structure.
     * This is written into all xml produced to help determine the structure
     */
    private static final int CURRENT_XML_SCHEMA_VERSION_MAJOR = 1;
    
    /**
     * The current XML schema minor version number for this object structure.
     * This is written into all xml produced to help determine the structure
     */
    private static final int CURRENT_XML_SCHEMA_VERSION_MINOR = 0;

    /**
     * An empty collection; all calls to {@link #get} will return <code>null</code>.
     */
    static final PropertyCollection EMPTY_COLLECTION = new EmptyPropertyCollection();

    /**
     * Converts XML representation of property collection.
     * @param propertyCollectionXML the xml
     * @return the unmarshalled object
     */
    static PropertyCollection unmarshal(String propertyCollectionXML) 
            throws net.project.xml.XMLException {

        PropertyCollection properties = null;

        if (propertyCollectionXML == null) {
            throw new NullPointerException("Error creating property collection; missing parameter");
        }

        // Build Document using SAX and JDOM
        try {
            // Build the document
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new java.io.StringReader(propertyCollectionXML));

            // Get the root element
            Element propertyCollectionElement = doc.getRootElement();
            if (propertyCollectionElement == null || !propertyCollectionElement.getName().equals("PropertyCollection")) {
                throw new net.project.xml.XMLException("Expected element PropertyCollection not found");
            }
            
            // Get the version element
            // Note that we currently ignore it; that is, this method
            // handles all versions
            Element versionElement = propertyCollectionElement.getChild("Version");

            properties = new PropertyCollection();

            // Get all properties
            for (Iterator it = propertyCollectionElement.getChildren("Property").iterator(); it.hasNext(); ) {
                properties.add(Property.create((Element) it.next()));
            }

        } catch (NullPointerException e) {
            // JDOM returns null when methods like getChild("Name") do not find
            // matching elements
            // It becomes remarkably tedious coding to check every single possible structure
            // problem
            // I guess using the validating parser would help
            throw new net.project.xml.XMLException("Error creating property collection from xml; invalid document structure: " + e, e);
            

        } catch (org.jdom.JDOMException e) {
            // Problem building document from xml
            throw new net.project.xml.XMLException("Error creating property collection from xml: " + e, e);
        }catch (IOException ioe) {
            // Problem building document from xml
            throw new net.project.xml.XMLException("Error creating property collection from xml", ioe);
        }
        
        return properties;
    }

    //
    // Instance members
    //

    /**
     * Index of properties by property name.
     * Key type is <code>PropertyName</code>.
     * Value type is <code>Property</code>.
     */
    private Map index = new HashMap();

    PropertyCollection() {
        // Do nothing
    }

    /**
     * Creates a new property collection by making a defensive copy of the
     * specified property collection.
     * @param source the source property collection
     */
    PropertyCollection(PropertyCollection source) {
        for (Iterator it = source.iterator(); it.hasNext(); ) {
            Property nextProperty = (Property) it.next();
            add(new Property(nextProperty));
        }
    }

    /**
     * Returns the property with the specified name.
     */
    Property get(PropertyName name) {
        return (Property) this.index.get(name);
    }

    /**
     * Adds a property.
     * @param property the <code>Property</code> to add.
     * @return true if the property was added successfully
     * @see java.util.List#add
     */
    // Must be Package access only
    // Do NOT make protected or public
    // Any requirement to do so must seek an alternative solution
    boolean add(Property property) {
        this.index.put(property.getName(), property);
        return super.add(property);
    }

    /**
     * Not supported by this class.
     * @throws UnsupportedOperationException always
     */ 
    public boolean add(Object o) {
        // Prevent external classes adding new properties
        // We have to do this since it add() is a public method in the super-class
        throw new UnsupportedOperationException("boolean add(Object) not supported");
    }

    /**
     * Constructs the XML format of this property collection suitable for storage.
     * <b>Note:</b> This is UNSECURED.
     * @param xml the stringbuffer to which the XML representation will be
     * appended; this procedural-style of updating a parameter will make any
     * future methods that write to output streams more consistent
     * Technically i should specify a java.io.Reader here
     */
    void marshal(StringBuffer xml) {

        Element rootElement = new Element("PropertyCollection");

        // Add the version number for this XML schema
        rootElement.addContent(XMLUtils.getVersionElement(CURRENT_XML_SCHEMA_VERSION_MAJOR, CURRENT_XML_SCHEMA_VERSION_MINOR));

        // Add the properties
        for (Iterator it = iterator(); it.hasNext();) {
            rootElement.addContent( ((Property) it.next()).getXMLElement() );
        }

        // Build the document and add it to the xml string
        Document doc = new Document(rootElement);
        xml.append(XMLUtils.outputString(doc));

    }

    net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        try {
            doc.startElement("PropertyCollection");

            for (Iterator it = iterator(); it.hasNext(); ) {
                Property nextProperty = (Property) it.next();

                doc.addElement(nextProperty.getXMLDocument());
            }

            doc.endElement();
        
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Empty document

        }

        return doc;
    }

    /**
     * An empty PropertyCollection.
     * Similar to a {@link java.util.Collections#EMPTY_LIST}.
     */
    private static class EmptyPropertyCollection extends PropertyCollection {

        EmptyPropertyCollection() {
            super();
        }

        public int size() {return 0;}

        public boolean contains(Object obj) {return false;}

        public boolean add(Property property) {return false;}
        
        public Object get(int index) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }

    }

}
