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
package net.project.resource;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Provides a type that describes the kind of entry in a roster.
 * @see net.project.resource.IRosterEntry#getRosterEntryType
 */
public class RosterEntryType 
    implements java.io.Serializable, net.project.persistence.IXMLPersistence {

    private static ArrayList rosterEntryTypes = new ArrayList();

    private int id = 0;


    /**
     * Returns the RosterEntryType for specified id.
     * @return the member type or null if no member type with that id is
     * found.
     */
    public static RosterEntryType forID(int id) {
        RosterEntryType rosterEntryType = null;
        boolean isFound = false;
        
        // Iterate over all member types, stopping when found type with id
        Iterator it = RosterEntryType.rosterEntryTypes.iterator();
        while (it.hasNext() & !isFound) {
            rosterEntryType = (RosterEntryType) it.next();
            if (rosterEntryType.getID() == id) {
                isFound = true;
            }
        }
    
        return rosterEntryType;
    }
    

    private RosterEntryType(int id) {
        this.id = id;
        rosterEntryTypes.add(this);
    }

    /**
     * Returns the ID of this RosterEntryType.
     */
    public int getID() {
        return this.id;
    }

    public boolean equals(Object obj) {
        if (obj instanceof RosterEntryType &&
            obj != null &&
            ((RosterEntryType) obj).getID() == this.getID()) {

            return true;
        }
        
        return false;
    }


    /**
     * Returns the id of this member type as a string.
     * @return the id as a string
     * @see #getID
     */
    public String toString() {
        return Integer.toString(getID());
    }


    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }

    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<RosterEntryType>");
        xml.append("<id>" + getID() + "</id>");
        xml.append("</RosterEntryType>");
        return xml.toString();
    }

    /**
     * Group Member Type.
     */
    public static RosterEntryType GROUP = new RosterEntryType(0);
    
    /**
     * Person Member Type.
     */
    public static RosterEntryType PERSON = new RosterEntryType(1);

}

