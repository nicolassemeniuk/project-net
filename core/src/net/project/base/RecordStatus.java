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
package net.project.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.project.persistence.IXMLPersistence;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

/**
 * Provides an enumeration of RecordStatuses.
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class RecordStatus implements IXMLPersistence, Serializable {

    //
    // Static members
    //

    /**
     * The collection of all RecordStatus types.
     */
    private static Collection recordStatusCollection = new ArrayList();

    /**
     * Active record status, indicates an object is generally available.
     * ID is <code>A</code>.
     */
    public static final RecordStatus ACTIVE = new RecordStatus("A");

    /**
     * Pending record status indicates an object is in an intermediate state.
     * Limited access is provided to the object.
     * ID is <code>P</code>.
     */
    public static final RecordStatus PENDING = new RecordStatus("P");

    /**
     * Deleted record status indicates an object is not generally available.
     * It may only be seen by functionality specifically dealing with
     * deleted objects.
     * ID is <code>D</code>.
     */
    public static final RecordStatus DELETED = new RecordStatus("D");

    /**
     * Hard Deleted record status indicates an object has been removed from the file system.
     * It may only be seen by functionality specifically dealing with
     * deleted objects.
     * ID is <code>D</code>.
     */
    public static final RecordStatus HARD_DELETED = new RecordStatus("H");

    /**
     * Returns the RecordStatus with the specified id.
     * @param id the id of the value to find
     * @return the RecordStatus with matching id, or null if no value is
     * found with that id
     */
    public static RecordStatus findByID(String id) {
        RecordStatus foundRecordStatus = null;
        boolean isFound = false;

        for (Iterator it = RecordStatus.recordStatusCollection.iterator(); it.hasNext() && !isFound;) {
            RecordStatus nextRecordStatus = (RecordStatus) it.next();
            if (nextRecordStatus.getID().equals(id)) {
                foundRecordStatus = nextRecordStatus;
                isFound = true;
            }
        }

        return foundRecordStatus;
    }

    /**
     * Returns an unmodifiable collection containing all record statuses.
     * @return a collection where each element is a <code>RecordStatus</code>
     */
    public static Collection getAllRecordStatuses() {
        return Collections.unmodifiableCollection(RecordStatus.recordStatusCollection);
    }


    //
    // Instance members
    //

    /**
     * The unique id.
     */
    private String id = null;

    /**
     * Creates a new RecordStatus.
     * @param id the id
     */
    private RecordStatus(String id) {
        this.id = id;
        recordStatusCollection.add(this);
    }

    /**
     * Returns the internal id of this RecordStatus.
     * @return the id
     */
    public String getID() {
        return this.id;
    }

    /**
     * Indicates whether the specified object is a RecordStatus with
     * matching ID.
     * @param o the RecordStatus object to compare
     * @return true if the specified RecordStatus has a matching id; false otherwise
     */
    public boolean equals(Object o) {
        boolean isEqual = false;

        if (this == o) {

            isEqual = true;

        } else {

            if (o instanceof RecordStatus) {
                RecordStatus recordStatus = (RecordStatus) o;
                if (id.equals(recordStatus.id)) {
                    isEqual = true;
                }
            }

        }

        return isEqual;
    }

    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Returns this object's XML representation, including the XML version tag.
     * @return XML representation of this object
     * @see IXMLPersistence#getXMLBody
     * @see IXMLPersistence#XML_VERSION
     */
    public String getXML() {
        return getXMLDocument().getXMLString();
    }

    /**
     * Returns this object's XML representation, without the XML version tag.
     * @return XML representation of this object
     * @see IXMLPersistence#getXML
     */
    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    /**
     * Returns this object's XML representation as an XMLDocument.
     * @return
     */
    public XMLDocument getXMLDocument() {
        XMLDocument xml = new XMLDocument();

        try {
            xml.startElement("RecordStatus");
            xml.addElement("ID", getID());
            xml.endElement();

        } catch (XMLDocumentException e) {
            // Do nothing

        }

        return xml;
    }

}
