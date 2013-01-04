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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 20817 $
|       $Date: 2010-05-08 13:50:42 -0300 (sáb, 08 may 2010) $
|     $Author: avinash $
|
+--------------------------------------------------------------------------------------*/
package net.project.link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.project.base.ObjectType;
import net.project.base.RecordStatus;
import net.project.persistence.PersistenceException;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

/**
 * Provides a collection of linkable objects.
 * Note: Some objects may be deleted, denoted by getRecordStatus() = RecordStatus.DELETED
 *
 * @author BrianConneen 03/00
 * @since Version 1
 */
public class DisplayLinks {

    /**
     * The list of linkable objects.
     */
    private final Collection linkableObjectList = new ArrayList();

    /**
     * All object types, used to resolve their descriptions.
     */
    private final Map allObjectTypes = new HashMap();

    /**
     * Creates new DisplayLinks.
     */
    public DisplayLinks() {

        try {
            // Populate the map of object type ID to object type
            // This is needed to get the descriptions of the object types
            for (Iterator it = ObjectType.getAll().iterator(); it.hasNext(); ) {
                ObjectType nextObjectType = (ObjectType) it.next();
                allObjectTypes.put(nextObjectType.getType(), nextObjectType);
            }
        } catch (PersistenceException e) {
            // There was a problem loading all object types

        }
    }

    public boolean isLinked() {
    	DisplayLink nextDisplayLink;
    	boolean hasNoDeleted = false;
    	for (final Iterator it = linkableObjectList.iterator(); it.hasNext(); ) {    
    		nextDisplayLink = (DisplayLink) it.next();    
            if (nextDisplayLink.linkableObject.getRecordStatus() != null &&
                    !nextDisplayLink.linkableObject.getRecordStatus().equals(RecordStatus.DELETED)) {
            	hasNoDeleted = true;
            	break;
            }
    	}
    	
    	return linkableObjectList.size() > 0 && hasNoDeleted;
    }
    
    /**
     * Returns the XML representation of this List.
     * @return the XML representation of this List
     */
    public String getXML() {
        return getXMLDocument().getXMLBodyString();
    }

    private XMLDocument getXMLDocument() {

        XMLDocument xml = new XMLDocument();

        try {

            xml.startElement("link_list");

            for (Iterator it = linkableObjectList.iterator(); it.hasNext(); ) {
                DisplayLink nextDisplayLink = (DisplayLink) it.next();

                // Customize the XML depending on whether it is dleted
                // 02/06/2003 - Tim
                // This is to solve BFD-283.
                // It is less than ideal since it still requires a loaded object
                // to determine whether it needs displayed; it would be better
                // if we could avoid loading the object entirely

                xml.startElement("linked_object");

                // Note - This condition looks backwards
                // The "to" and "from" are the link type of the _linked_
                // objects.  That is, when producing a "from" list for an object
                // all linked objects are linked _to_ it
                xml.addElement("link_type", (nextDisplayLink.isFrom ? "to" : "from"));

                xml.addElement("object_type", nextDisplayLink.linkableObject.getType());
                xml.addElement("object_type_description", ((ObjectType) this.allObjectTypes.get(nextDisplayLink.linkableObject.getType())).getDescription());
                xml.addElement("object_name", nextDisplayLink.linkableObject.getName());
                xml.addElement("object_id", nextDisplayLink.linkableObject.getID());

                if (nextDisplayLink.linkableObject.getRecordStatus() != null &&
                        !nextDisplayLink.linkableObject.getRecordStatus().equals(RecordStatus.DELETED)) {

                    // Non-deleted objects are clickable
                    xml.addElement(nextDisplayLink.linkableObject.getRecordStatus().getXMLDocument());
                    System.out.println("Object Link URL => "+ nextDisplayLink.linkableObject.getURL());
                    xml.addElement("href", nextDisplayLink.linkableObject.getURL());

                } else {
                    // Deleted objects are not clickable
                    xml.addElement(RecordStatus.DELETED.getXMLDocument());

                }

                xml.endElement();
            }

            xml.endElement();

        } catch (XMLDocumentException e) {
            // Do nothing

        }

        return xml;
    }

    /**
     * Adds a linkable object to this collection.
     * @param linkableObject the object to add
     */
    public void add(ILinkableObject linkableObject, boolean isFrom) {
        linkableObjectList.add(new DisplayLink(linkableObject,  isFrom));
    }

    /**
     * Provides an ILinkableObject with an indication of the direction of
     * the link from the source object.
     */
    private static class DisplayLink {

        final ILinkableObject linkableObject;
        final boolean isFrom;

        private DisplayLink(ILinkableObject object, boolean isFrom) {
            this.linkableObject = object;
            this.isFrom = isFrom;
        }
    }
}