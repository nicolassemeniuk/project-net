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
package net.project.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

/**
 * Provides an Enumeration of values.
 */
public class PriorityCode implements net.project.gui.html.IHTMLOption, Comparable {

    //
    // Static members
    //

    /**
     * The list of all PriorityCodes.
     */
    private static List priorityCodeList = new ArrayList();

    /**
     * Empty PriorityCode.
     */
    public static final PriorityCode EMPTY = new EmptyPriorityCode();

    /**
     * Priority one.
     * Display name specified by token <code>prm.global.code.priority.one.name</code>.
     */
    public static final PriorityCode ONE = new PriorityCode("100", "prm.global.code.priority.one.name");

    /**
     * Priority two.
     * Display name specified by token <code>prm.global.code.priority.two.name</code>.
     */
    public static final PriorityCode TWO = new PriorityCode("200", "prm.global.code.priority.two.name");

    /**
     * Priority three.
     * Display name specified by token <code>prm.global.code.priority.three.name</code>.
     */
    public static final PriorityCode THREE = new PriorityCode("300", "prm.global.code.priority.three.name");

    /**
     * Priority four.
     * Display name specified by token <code>prm.global.code.priority.four.name</code>.
     */
    public static final PriorityCode FOUR = new PriorityCode("400", "prm.global.code.priority.four.name");

    /**
     * Priority five.
     * Display name specified by token <code>prm.global.code.priority.five.name</code>.
     */
    public static final PriorityCode FIVE = new PriorityCode("500", "prm.global.code.priority.five.name");

    /**
     * Returns the PriorityCode with the specified id.
     * @param id the id of the value to find
     * @return the PriorityCode with matching id, or {@link #EMPTY} if no value is
     * found with that id
     */
    public static PriorityCode findByID(String id) {
        PriorityCode foundPriorityCode = EMPTY;
        boolean isFound = false;

        for (Iterator it = PriorityCode.priorityCodeList.iterator(); it.hasNext() && !isFound;) {
            PriorityCode nextPriorityCode = (PriorityCode) it.next();
            if (nextPriorityCode.getID().equals(id)) {
                foundPriorityCode = nextPriorityCode;
                isFound = true;
            }
        }

        return foundPriorityCode;
    }

    /**
     * Returns all priority codes.
     * @return the list where each element is a <code>PriorityCode</code>
     */
    public static List getAllPriorityCodes() {
        return Collections.unmodifiableList(PriorityCode.priorityCodeList);
    }


    //
    // Instance members
    //

    /**
     * The unique id.
     */
    private String id = null;

    /**
     * The token which provides the display name.
     */
    private String nameToken = null;

    /**
     * Creates an empty PriorityCode.
     */
    private PriorityCode() {
        // Do nothing
    }

    /**
     * Creates a new PriorityCode.
     * @param id the id
     * @param nameToken the token for the display name
     */
    private PriorityCode(String id, String nameToken) {
        this.id = id;
        this.nameToken = nameToken;
        priorityCodeList.add(this);
    }

    /**
     * Returns the internal id of this PriorityCode.
     * @return the id
     */
    public String getID() {
        return this.id;
    }

    /**
     * Returns the display name of this PriorityCode.
     * @return the display name
     */
    public String getName() {
        return PropertyProvider.get(this.nameToken);
    }

    /**
     * Returns the token providing the display name of this PriorityCode.
     * @return the token
     */
    public String getNameToken() {
        return this.nameToken;
    }

    /**
     * Indicates whether the specified object is a PriorityCode with
     * matching ID.
     * @param o the PriorityCode object to compare
     * @return true if the specified PriorityCode has a matching id; false otherwise
     */
    public boolean equals(Object o) {
        boolean isEqual = false;

        if (this == o) {

            isEqual = true;

        } else {

            if (o instanceof PriorityCode) {
                PriorityCode priorityCode = (PriorityCode) o;
                if (id.equals(priorityCode.id)) {
                    isEqual = true;
                }
            }

        }

        return isEqual;
    }

    public int hashCode() {
        return id.hashCode();
    }

    //
    // Implementing IHTMLOption
    //

    /**
     * Returns the value for the <code>value</code> attribute of the HTML
     * option.
     * @return the id
     */
    public String getHtmlOptionValue() {
        return getID();
    }

    /**
     * Returns the value for the content part of the HTML option.
     * @return the display name
     */
    public String getHtmlOptionDisplay() {
        return getName();
    }

    //
    // End IHTMLOption
    //

    /**
     * Creates an XML document.
     * @return the XML document
     * @throws net.project.xml.document.XMLDocumentException if there is a problem creating
     */
    public XMLDocument getXMLDocument() throws XMLDocumentException {
        XMLDocument doc = new XMLDocument();

        doc.startElement("PriorityCode");
        doc.addElement("ID", getID());
        doc.addElement("NameToken", this.nameToken);
        doc.endElement();

        return doc;
    }

    /**
     * All PriorityCode methods are overridden to throw a <code>RuntimeException</code>.
     */
    private static final class EmptyPriorityCode extends PriorityCode {

        EmptyPriorityCode() {
            super();
        }
        public String getID() {
            throw new UnsupportedOperationException("Unsupported operation");
        }

        public String getName() {
            throw new UnsupportedOperationException("Unsupported operation");
        }
    }

	public int compareTo(Object to) {
		if( to == null )
			return -1;
		return Integer.decode(this.getID()).compareTo(Integer.decode(( (PriorityCode) to ).getID()));
	}
    
}
