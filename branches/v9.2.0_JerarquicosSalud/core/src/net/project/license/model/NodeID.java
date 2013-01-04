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
package net.project.license.model;

import java.util.Iterator;

import org.jdom.Element;

/**
 * A NodeID is the id of a node to which a license may be locked.
 */
public class NodeID {

    //
    // Static members
    //

    /**
     * Creates a node ID based on the specified display node ID.
     * @return the node ID
     */
    public static NodeID createNodeID(String displayID) {
        return createNodeIDForValue(parseFromDisplay(displayID));
    }

    /**
     * Creates a node ID based on the specified value.
     * @return the node ID
     */
    static NodeID createNodeIDForValue(String nodeIDValue) {
        NodeID nodeID = new NodeID();
        nodeID.setValue(nodeIDValue);
        return nodeID;
    }

    /**
     * Creates a node ID from the specified node ID element.
     * @param nodeIDElement the xml element from which to create the
     * node ID
     * @return the node ID
     */
    static NodeID create(org.jdom.Element nodeIDElement) {
        NodeID nodeID = new NodeID();
        nodeID.populate(nodeIDElement);
        return nodeID;
    }

    /**
     * Parses a display node ID and returns an internal value.
     * @param displayNodeID the display node ID to parse
     * @return the internal value for the display node ID
     */
    private static String parseFromDisplay(String displayNodeID) {
        char[] source = displayNodeID.toCharArray();
        StringBuffer result = new StringBuffer();
        
        // Iterate over source and ignore '-' characters
        for (int i = 0; i < source.length; i++) {
            if (source[i] != '-') {
                result.append(source[i]);
            }
        }

        return result.toString();
    }


    /**
     * Formats the specified internal value for display purposes.
     * This is formatted as <code>XXXXX-XXXXX</code>.
     * @return the formatted value
     */
    private static String formatForDisplay(String value) {
        char[] source = value.toCharArray();
        StringBuffer result = new StringBuffer();

        int length = source.length;
        int sourceIndex = 0;
        int groupCounter = 0;
        final int groupSize = 5;

        // Iterate over source characters, adding to result
        // Breaking groups up with '-'
        while (sourceIndex < length) {
            // Add the next character to the result, incrementing pointer
            result.append(source[sourceIndex++]);

            // If loop condition still holds (that is, we're not done yet)
            // AND we've counted up 5 positions
            // Add '-'
            if ((sourceIndex < length) && (++groupCounter == groupSize)) {
                result.append('-');
                groupCounter = 0;
            }
        }
        
        return result.toString();
    }


    //
    // Instance attributes and methods
    //

    /** The value of this node ID. */
    private String value = null;

    /**
     * Creates an empty NodeID.
     */
    NodeID() {
        // Do Nothing
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(super.toString()).append("\n");
        result.append("value: ").append(getValue()).append("\n");
        return result.toString();
    }

    /**
     * Indicates whether specified object is equal to this node ID.
     * @param obj the node ID to compare
     * @return true if the specified node ID's value is equal to this
     * node ID's value
     */
    public boolean equals(Object obj) {
        if ((obj == this) ||
                (obj instanceof NodeID &&
                 obj != null &&
                 ((NodeID) obj).getValue().equals(getValue())) ) {
            
            return true;
        }

        return false;
    }

    public int hashCode() {
        // Since we equality is based on equal values, the hashcode should
        // be based on the hashcode's value
        // That is - two equal node IDs must return equal hashcodes
        return getValue().hashCode();
    }


    /**
     * Sets this node ID's value.
     * @param value the internal value for this node ID
     * @see #getValue
     */
    private void setValue(String value) {
        this.value = value;
    }


    /**
     * Returns this node ID's internal value.
     * @return the value
     * @see #setValue
     */
    String getValue() {
        return this.value;
    }


    /**
     * Returns the display representation of this key.
     * This is formatted as <code>XXXXX-XXXXX</code>.
     * @return the node ID formatted for display
    */
    public String toDisplayString() {
        return NodeID.formatForDisplay(getValue());
    }


    /**
     * Returns the XML format of this node ID suitable for storage.
     * @return the storage XML
     */
    String serializeXMLBody() {
        String xml = null;

        try {
            net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
            doc.startElement("NodeID");
            doc.addElement("Value", getValue());
            doc.addElement("DisplayString", toDisplayString());
            doc.endElement();
            xml = doc.getXMLBodyString();
        
        } catch (net.project.xml.document.XMLDocumentException xde) {
            // Suck it up
            xml = "";
        
        }

        return xml;
    }

   /**
     * Returns the xml Element for this NodeID.
     * @return the element
     */
    public Element getXMLElement() {
        Element rootElement = new Element("NodeID");
        rootElement.addContent(new Element("Value").addContent(getValue()));
        rootElement.addContent(new Element("DisplayString").addContent(toDisplayString()));;
        return rootElement;
    }

    /**
     * Populates this license model from the xml element.
     * The element can be assumed to be of the correct type for the license model.
     * @param element the xml element from which to populate this license model
     */
    protected void populate(Element element) {
        
        // Iterate over each child element of this NodeID element
        // and handle each one
        Element childElement = null;
        for (Iterator it = element.getChildren().iterator(); it.hasNext(); ) {
            childElement = (Element) it.next();

            if (childElement.getName().equals("Value")) {
                // Value contains a string
                setValue(childElement.getTextTrim());
            
            } else if (childElement.getName().equals("DisplayString")) {
                // Ignored, this is read-only and is always
                // constructed from the value

            }
        }

    }

    /**
     * Returns this node ID as xml.
     * @return the xml for this node ID
     */
    net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement("NodeID");
            doc.addElement("Value", getValue());
            doc.addElement("DisplayString", toDisplayString());
            doc.endElement();
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }

}
