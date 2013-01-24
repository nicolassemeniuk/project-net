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
package net.project.portfolio.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.project.persistence.IXMLPersistence;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

/**
 * Provides an Enumeration of Personal Portfolio View Result Types.
 * The Results may require different presentation mechansisms.
 */
public class ResultType implements IXMLPersistence {

    //
    // Static members
    //

    private static final List resultTypeListList = new ArrayList();

    /**
     * Default PersonalPortfolioViewResults.
     */
    public static final ResultType DEFAULT = new ResultType("default");

    /**
     * Tree results; requires special presentation.
     */
    public static final ResultType TREE = new ResultType("tree");

    /**
     * Returns the ResultType with the specified id.
     * @param id the id of the value to find
     * @return the ResultType with matching id, or null if no value is
     * found with that id
     */
    public static ResultType findByID(String id) {
        ResultType foundResultType = null;
        boolean isFound = false;

        for (Iterator it = ResultType.resultTypeListList.iterator(); it.hasNext() && !isFound;) {
            ResultType nextResultType = (ResultType) it.next();
            if (nextResultType.getID().equals(id)) {
                foundResultType = nextResultType;
                isFound = true;
            }
        }

        return foundResultType;
    }

    /**
     * Returns an unmodifiable list of all defined <code>ResultType</code>s.
     * @return
     */
    public static List getAllResultTypes() {
        return Collections.unmodifiableList(ResultType.resultTypeListList);
    }


    //
    // Instance members
    //

    /**
     * The unique id.
     */
    private String id = null;

    /**
     * Creates a new ResultType.
     * @param id the id
     */
    private ResultType(String id) {
        this.id = id;
        resultTypeListList.add(this);
    }

    /**
     * Returns the internal id of this ResultType.
     * @return the id
     */
    public String getID() {
        return this.id;
    }

    /**
     * Indicates whether the specified object is a ResultType with
     * matching ID.
     * @param o the ResultType object to compare
     * @return true if the specified ResultType has a matching id; false otherwise
     */
    public boolean equals(Object o) {
        boolean isEqual = false;

        if (this == o) {

            isEqual = true;

        } else {

            if (o instanceof ResultType) {
                ResultType resultTypeList = (ResultType) o;
                if (id.equals(resultTypeList.id)) {
                    isEqual = true;
                }
            }

        }

        return isEqual;
    }

    public int hashCode() {
        return id.hashCode();
    }

    public String getXML() {
        return getXMLDocument().getXMLString();
    }

    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    XMLDocument getXMLDocument() {
        XMLDocument doc = new XMLDocument();

        try {
            doc.startElement("ResultType");
            doc.addElement("ID", getID());
            doc.endElement();

        } catch (XMLDocumentException e) {
            // Return partially built document

        }

        return doc;

    }
}
