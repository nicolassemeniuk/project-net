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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.resource;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.project.persistence.IXMLPersistence;
import net.project.xml.document.XMLDocument;

/**
 * Provides a collection of <code>AssignmentWorkLogEntry</code>s.
 *
 * @author Tim Morrow
 * @since Version 7.7.1
 */
public class AssignmentWorkLog implements IXMLPersistence {

    /**
     * The work log entries.
     * Each element is an <code>AssignmentWorkLogEntry</code>.
     */
    private final List entries;

    /**
     * Creates an AssignmentWorkLog containing the specified entries.
     * @param workLogEntries the entries
     */
    public AssignmentWorkLog(List workLogEntries) {
        this.entries = new LinkedList(workLogEntries);
    }

    public String getXML() {
        return getXMLDocument().getXMLString();
    }

    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    private XMLDocument getXMLDocument() {
        XMLDocument xml = new XMLDocument();
        xml.startElement("AssignmentWorkLog");

        for (Iterator iterator = entries.iterator(); iterator.hasNext();) {
            AssignmentWorkLogEntry nextEntry = (AssignmentWorkLogEntry) iterator.next();
            xml.addElement(nextEntry.getXMLDocument());
        }

        xml.endElement();
        return xml;
    }

}
