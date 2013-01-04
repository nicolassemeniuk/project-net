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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|
+--------------------------------------------------------------------------------------*/
package net.project.resource;

import java.util.Iterator;

/**
 * Provides a collection of <code>Invitee</code>s.
 */
public class InviteeList 
        extends java.util.ArrayList 
        implements net.project.persistence.IXMLPersistence {


    /**
     * Returns the XML representation of this InviteeList, including
     * the XML version tag.
     * @return the XML
     */
    public String getXML() {
        return getXMLDocument().getXMLString();

    }

    /**
     * Returns the XML representation of this InviteeList, excluding the
     * XML version tag.
     * @return the XML
     */
    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();

    }

    /**
     * Returns the XML structure of this InviteeList.
     * @return the XMLDocument structure
     */
    private net.project.xml.document.XMLDocument getXMLDocument() {
        
        net.project.xml.document.XMLDocument xml = new net.project.xml.document.XMLDocument();

        try {

            xml.startElement("InviteeList");

            // Add all Invitees
            for (Iterator it = iterator(); it.hasNext(); ) {
                Invitee nextInvitee = (Invitee) it.next();

                xml.addElement(nextInvitee.getXMLDocument());
            }

            xml.endElement();


        } catch (net.project.xml.document.XMLDocumentException e) {
            // Returns an empty xml document
        
        }

        return xml;
    }

}
