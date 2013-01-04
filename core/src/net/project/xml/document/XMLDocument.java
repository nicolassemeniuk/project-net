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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.xml.document;

import java.io.IOException;
import java.util.Stack;

import net.project.xml.XMLUtils;

import org.jdom.Attribute;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;


/**
 * An XMLDocument represents an entire document.
 * It provides conveniences methods for adding attributes and elements
 * to the document while escaping and formatting values as appropriate.
 */
public class XMLDocument {

    /**
     * The JDOM Document.
     */
    private Document document = null;

    /**
     * A stack for manipulating the current node.
     */
    private Stack nodeStack = new Stack();

    /**
     * Indicates whether to output in "pretty" format; that is, with idents
     * and newlines.
     */
    private boolean isPrettyFormat = false;


    /**
     * Creates a new XMLDocument.
     * Default output format is without any indentation or newlines.
     */
    public XMLDocument() {
        super();
    }

    /**
     * Returns the root element of this document as a JDOM element.
     * It is detached from the JDOM Document, so may be added as content
     * elsewhere.
     * @return the detached root element
     */
    public Element getRootElement() {
        return (Element) this.document.getRootElement().detach();
    }

    /**
     * Adds a new element with the specified name and value.
     * @param name the element name
     * @param value the value of the element
     * @throws XMLDocumentException if there is a problem adding the element
     */
    public void addElement(String name, Object value) {
        
        startElement(name);
        addValue(value);
        endElement();

    }

    /**
     * Adds the root element of the specified document as an element in this
     * document.
     * @param document the document who's root element to add
     * @throws XMLDocumentException if the specified document doesn't have
     * a root element
     */
    public void addElement(XMLDocument document) {
        Element rootElement = document.getRootElement();
        if (rootElement == null) {
            throw new XMLDocumentException("XMLDocument Error: No root element in document");
        }

        addElement(rootElement);
    }

    /**
     * Adds the specified element to this document.
     * @param element the element to add
     * @throws XMLDocumentException if there is a problem adding the element
     */
    private void addElement(Element element) {
        startElement(element);
        endElement();
    }

    /**
     * Starts the specified element.
     * This is added to the document, and is left as the "current node".
     * Subsequent values, elements and attributes are added to this element.
     * @param element the element to add
     * @throws XMLDocumentException if there is a problem starting the element;
     * this can occur if the current document already has a root element (and
     * it is not current) or the current node is not an element
     */
    private void startElement(Element element) {
        
        if (nodeStack.isEmpty()) {
            
            // Ensure document object is created
            if (this.document == null) {
                this.document = new Document(element);
            
            } else {
                throw new XMLDocumentException("XMLDocument Error: Unable to add top level element - current document already has a root element");
            
            }

        } else {
            // We have a current node; check it is an element
            checkInElement("XMLDocument Error: Unable to start element - current node is not an element.  Elements may only be added to other elements.");
        
            // Add the new element content to the current element
            ((Element) nodeStack.peek()).addContent(element);
        
        }

        // Save the new element on the stack; it is now the current element
        nodeStack.push(element);

    }

    /**
     * Starts a new element with the specified name.
     * @param name the name of the element
     * @throws XMLDocumentException if there is a problem starting the element
     */
    public void startElement(String name) {

        startElement(new Element(name));
    }


    /**
     * Ends the current element.
     * @throws XMLDocumentException if there is a problem ending the element
     */
    public void endElement() {

        checkInElement("XMLDocument Error: Unable to end element - current node is not an element");
        nodeStack.pop();
    }


    /**
     * Adds a new attribute to the current element with the specified
     * name and value.
     * @param name the name of the attribute
     * @param value the attribute's value
     * @throws XMLDocumentException if there is no current element to which to add
     * the attribute
     */
    public void addAttribute(String name, Object value) {

        startAttribute(name);
        addValue(value);
        endAttribute();
    }


    /**
     * Starts an attribute and adds to the current element.
     * @param name the name of the attribute
     * @throws XMLDocumentException if the current node is not an element
     */
    private void startAttribute(String name) {

        // Check to make sure there is a current element
        checkInElement("XMLDocument Error: Unable to start attribute - current node is not an element");

        // Create new attribute, add to current element, then add attribute to
        // last position in stack
        Attribute newAttribute = new Attribute(name, "");
        ((Element) nodeStack.peek()).setAttribute(newAttribute);
        nodeStack.push(newAttribute);

    }


    /**
     * Ends the current attribute.
     * @throws XMLDocumentException if there is no current attribute
     */
    private void endAttribute() {
        
        checkInAttribute("XMLDocument Error: Unable to end attribute - current node is not an attribute");
        
        // Remove the current node from the stack
        nodeStack.pop();
    }


    /**
     * Adds a comment to the current element.
     * A comment may only be added to an existing element.
     * @param comment the text of the comment
     * @throws XMLDocumentException if there is a problem adding the comment;
     * for example, there is no current node or it is not an element
     */
    public void addComment(String comment) {

        Comment newComment = new Comment(comment);

        if (nodeStack.isEmpty()) {
            throw new XMLDocumentException("XMLDocument Error: Unable to add comment as root node");

        } else {
            // We have a current node; it must be an element in order to add the
            // comment to it
            checkInElement("XMLDocument Error: Unable to add comment - current node is not an element");

            ((Element) nodeStack.peek()).addContent(newComment);
        }
        
    }


    /**
     * Adds a value to the current node.
     * <p>
     * Booleans and dates are converted to appropriate XML formats.
     * Invalid XML characters are removed from strings.
     * </p>
     * @param value the value to add to the current node
     * @throws XMLDocumentException if there is a problem adding the value;
     * for example, there is no current node or the current node already has
     * a value
     * @see XMLUtils#format(java.lang.Object)
     */
    public void addValue(Object value) {

        checkInElementOrAttribute("XMLDocument Error: Unable to add value - there is no current element or attribute");

        // Get the current node from the stack
        Element currentElement = null;
        Attribute currentAttribute = null;
        Object current = nodeStack.peek();
        if (current instanceof Element) {
            currentElement = (Element) current;
            currentElement.addContent(XMLUtils.format(value));
        } else {
            currentAttribute = (Attribute) current;
            currentAttribute.setValue(XMLUtils.format(value));
        }

    }


    /**
     * Adds a string value that represents an XML structure itself.
     * An XML String may only be added to an element. The XML structure in the
     * string must be a well-formed XML document.
     * @param value the XML string value
     * @throws XMLDocumentException if there is a problem adding the XML string;
     * for example, there is no current element or the current node is not an element
     */
    public void addXMLString(String value) {

        if (nodeStack.isEmpty()) {
            // No current node
            throw new XMLDocumentException("XMLDocument Error: Unable to add XML string as root element");

        } else {
            // We have a current node; check it is an element
            checkInElement("XMLDocument Error: Unable to add XML string - current node is not an element");
        
            // Add the content to the current element
            ((Element) nodeStack.peek()).addContent(parseXMLString(value));

        }
    
    }

    /**
     * Checks to make sure there is a current element.
     * @param message the message to display if there is no current element
     * @throws XMLDocumentException if there is no current element; uses the
     * specified message
     */
    private void checkInElement(String message) {
        
        // If nodes are empty or the current node is not an XMLElement
        // Then there is no current element
        if (nodeStack.isEmpty() ||
                !(nodeStack.peek() instanceof Element) ) {

            throw new XMLDocumentException(message);
        }

    }


    /**
     * Checks to make sure there is a current attribute.
     * @param message the message to display if there is no current attribute
     * @throws XMLDocumentException if there is no current attribute; uses the
     * specified message
     */
    private void checkInAttribute(String message) {
        
        // If nodes are empty or the current node is not an XMLAttribute
        // Then there is no current attribute
        if (nodeStack.isEmpty() ||
                !(nodeStack.peek() instanceof Attribute) ) {

            throw new XMLDocumentException(message);
        }

    }


    /**
     * Checks to make sure there is a current element or attribute.
     * @param message the message to display if there is no current element
     * or attribute
     * @throws XMLDocumentException if there is no current element or attribute;
     * uses the specified message
     */
    private void checkInElementOrAttribute(String message) {
        
        if (nodeStack.isEmpty() ||
            !(nodeStack.peek() instanceof Element || 
              nodeStack.peek() instanceof Attribute) ) {
            
            throw new XMLDocumentException(message);
        }

    }

    /**
     * Indicates whether to format the output to include indents and newlines.
     * @param isPrettyFormat true means the output will be made "pretty";
     * false means no additional indents or newlines will be added
     */
    public void setPrettyFormat(boolean isPrettyFormat) {
        this.isPrettyFormat = isPrettyFormat;
    }

    /**
     * Returns this XMLDocument as a string, including the XML version tag.
     * @return the string version of this XMLDocument
     */
    public String getXMLString() {
        return net.project.xml.XMLUtils.outputString(this.document, false, this.isPrettyFormat);
    }

    /**
     * Returns this XMLDocument as a string, without the XML version tag.
     * @return the string version of this XMLDocument
     */
    public String getXMLBodyString() {
        return net.project.xml.XMLUtils.outputString(this.document, true, this.isPrettyFormat);
    }

    /**
     * Parses the xml string and returns its root element.
     * @param xml the string to parse
     * @return the element that was the root element; it has no parent so it
     * may be added to an existing element
     * @throws XMLDocumentException if there is a problem parsing
     */
    private Element parseXMLString(String xml) {

        // Build Document using SAX and JDOM
        try {
            // Build the document
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new java.io.StringReader(xml));

            // Return the root element, detaching it from the root in the process
            return (Element) doc.getRootElement().detach();

        } catch (org.jdom.JDOMException e) {
            // Problem building document from xml
            throw new XMLDocumentException("XMLDocument Error: Unable to parse XML string: " + e);
        }catch (IOException ioe) {
            // Problem building document from xml
            throw new XMLDocumentException("XMLDocument Error: Unable to read XML string: " + ioe);
        }

    }
}
