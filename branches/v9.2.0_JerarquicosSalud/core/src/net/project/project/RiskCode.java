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
public class RiskCode implements net.project.gui.html.IHTMLOption, Comparable {

    //
    // Static members
    //

    /**
     * The list of all RiskCodes.
     */
    private static List riskCodeList = new ArrayList();

    /**
     * Empty RiskCode.
     */
    public static final RiskCode EMPTY = new EmptyRiskCode();

    /**
     * Risk level one.
     * Display name specified by token <code>prm.global.code.risk.one.name</code>.
     */
    public static final RiskCode ONE = new RiskCode("100", "prm.global.code.risk.one.name");

    /**
     * Risk level two.
     * Display name specified by token <code>prm.global.code.risk.two.name</code>.
     */
    public static final RiskCode TWO = new RiskCode("200", "prm.global.code.risk.two.name");

    /**
     * Risk level three.
     * Display name specified by token <code>prm.global.code.risk.three.name</code>.
     */
    public static final RiskCode THREE = new RiskCode("300", "prm.global.code.risk.three.name");

    /**
     * Risk level four.
     * Display name specified by token <code>prm.global.code.risk.four.name</code>.
     */
    public static final RiskCode FOUR = new RiskCode("400", "prm.global.code.risk.four.name");

    /**
     * Risk level five.
     * Display name specified by token <code>prm.global.code.risk.five.name</code>.
     */
    public static final RiskCode FIVE = new RiskCode("500", "prm.global.code.risk.five.name");

    /**
     * Returns the RiskCode with the specified id.
     * @param id the id of the value to find
     * @return the RiskCode with matching id, or {@link #EMPTY} if no value is
     * found with that id
     */
    public static RiskCode findByID(String id) {
        RiskCode foundRiskCode = EMPTY;
        boolean isFound = false;

        for (Iterator it = RiskCode.riskCodeList.iterator(); it.hasNext() && !isFound;) {
            RiskCode nextRiskCode = (RiskCode) it.next();
            if (nextRiskCode.getID().equals(id)) {
                foundRiskCode = nextRiskCode;
                isFound = true;
            }
        }

        return foundRiskCode;
    }

    /**
     * Returns a list of all RiskCodes.
     * @return the list where each element is a <code>RiskCode</code>
     */
    public static List getAllRiskCodes() {
        return Collections.unmodifiableList(RiskCode.riskCodeList);
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
     * Creates an empty RiskCode.
     */
    private RiskCode() {
        // Do nothing
    }

    /**
     * Creates a new RiskCode.
     * @param id the id
     * @param nameToken the token for the display name
     */
    private RiskCode(String id, String nameToken) {
        this.id = id;
        this.nameToken = nameToken;
        riskCodeList.add(this);
    }

    /**
     * Returns the internal id of this RiskCode.
     * @return the id
     */
    public String getID() {
        return this.id;
    }

    /**
     * Returns the display name of this RiskCode.
     * @return the display name
     */
    public String getName() {
        return PropertyProvider.get(this.nameToken);
    }

    /**
     * Returns the token providing the display name of this RiskCode.
     * @return the token
     */
    public String getNameToken() {
        return this.nameToken;
    }


    /**
     * Indicates whether the specified object is a RiskCode with
     * matching ID.
     * @param o the RiskCode object to compare
     * @return true if the specified RiskCode has a matching id; false otherwise
     */
    public boolean equals(Object o) {
        boolean isEqual = false;

        if (this == o) {

            isEqual = true;

        } else {

            if (o instanceof RiskCode) {
                RiskCode riskCode = (RiskCode) o;
                if (id.equals(riskCode.id)) {
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

        doc.startElement("RiskCode");
        doc.addElement("ID", getID());
        doc.addElement("NameToken", this.nameToken);
        doc.endElement();

        return doc;
    }

    /**
     * Provides an empty RiskCode where all get methods throw
     * an <code>UnsupportedOperationException</code>.
     */
    private static final class EmptyRiskCode extends RiskCode {

        EmptyRiskCode() {
            super();
        }

        public String getID() {
            throw new UnsupportedOperationException("Unsupported Operation");
        }

        public String getName() {
            throw new UnsupportedOperationException("Unsupported Operation");
        }
    }

	public int compareTo(Object to) {
		if( to == null )
			return -1;
		return Integer.decode(this.getID()).compareTo(Integer.decode(( (RiskCode) to ).getID()));
	}
}
