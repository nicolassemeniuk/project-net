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
package net.project.calendar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.persistence.IXMLPersistence;
import net.project.xml.XMLUtils;

/**
 * A class which represents the types of Attendee Status
 */
public class AttendeeStatus implements IXMLPersistence {

    private static ArrayList attendeeStatusList = new ArrayList();

    /**
     * Active status
     */
    public static final AttendeeStatus HOST = new AttendeeStatus("Host","@prm.calendar.attendee.status.host.name");
    public static final AttendeeStatus INVITEE = new AttendeeStatus("Invitee","@prm.calendar.attendee.status.invitee.name");
    
    private String id = null;
    private String nameToken = null;

    public AttendeeStatus(String id , String nameToken) {
        this.id = id;
        this.nameToken = nameToken;
        this.attendeeStatusList.add(this);
    }

    /**
     * Get a list of all Attendee Statuses.
     *
     * @return a <code>List</code> value containing all of the possible Attendee Status
     * objects in the system.
     */
    public static List getAttendeeStatusList() {
        return attendeeStatusList;
    }

    /**
     * Get the AttendeeStatus that corresponds to a given ID string.
     *
     * @param id a <code>String</code> value that represents a AttendeeStatus.
     * @return a <code>AttendeeStatus</code> object which corresponds to the
     * id parameter passed to this method.
     */
    public static AttendeeStatus getForID(String id) {
        AttendeeStatus attendeeStatusToReturn = null;
        Iterator it = attendeeStatusList.iterator();
        
        while (it.hasNext()) {
            AttendeeStatus attendeeStatus = (AttendeeStatus)it.next();
            if (attendeeStatus.getID().equals(id)) {
                attendeeStatusToReturn = attendeeStatus;
            }
        }
        
        return attendeeStatusToReturn;
    }
    /**
     * Get the human-readable name of this AttendeeStatus type.  This is based
     * on the token that was passed to the constructor of this object.
     *
     * @return a human-readable string identifying this attendee status.
     */
    public String getName() {
        return PropertyProvider.get(nameToken);
    }

    /**
     * Get the token which is used to load the name of this  AttendeeStatus type.
     *
     * @return a <code>String</code> value used to load the human-readable name
     * of this  AttendeeStatus type.
     * @see #getName
     */
    public String getNameToken() {
        return nameToken;
    }

    /**
     * Get the unique identifier used to identify this AttendeeStatus type.
     *
     * @return a <code>String</code> value which uniquely identifies this AttendeeStatus
     * type.
     */
    public String getID() {
        return id;
    }

     /**
     * Return a string identifying this Attendee Status Type.
     *
     * @return a <code>String</code> value identifying this Attendee Status type.
     */
    public String toString() {
        return getName() + "(" + getID() + ")";
    }

    /**
     * Determine if a given object is equal to the current Attendee Status Type
     * instance.
     *
     * @param o the object to be compared.
     * @return <code>true</code> if the objects are equal, otherwise false.
     */
    public boolean equals(Object o) {
        boolean isEqual = true;
        
        if (!(o instanceof AttendeeStatus)) {
            isEqual = false;
        } else {
            isEqual = (((AttendeeStatus)o).getID().equals(this.getID()));
        }
        
        return isEqual;
    }

    /**
     * Return a valid hash code for this object.  This allows the object to be
     * looked up in a hash table with minimal required hash table buckets.
     *
     * @return a valid hash code.
     */
    public int hashCode() {
        int result;
        result = (nameToken != null ? nameToken.hashCode() : 0);
        result = 31 * result + id.hashCode();
        return result;
    }


    /**
     * Get the HTML for all of the <option> tags which represent that available
     * Attendee Status types.  We do not return the surrounding <select> tags as
     * the HTML author might need to set custom values in that tag.
     *
     * @return a <code>String</code> value containing all of the HTML select
     * values.
     */
    public static String getHTMLOptionList() {
        return getHTMLOptionList();
    }   

    /**
     * Get the HTML for all of the <option> tags which represent that available
     * AttendeeStatus types.  We do not return the surrounding <select> tags as
     * the HTML author might need to set custom values in that tag.
     *
     * @param selectedConstraint a <code>AttendeeStatus</code> object which
     * represents the default value selected in the select list.
     * @return a <code>String</code> value containing all of the HTML select
     * values.
     */
    public static String getHTMLOptionList(AttendeeStatus selectedConstraint) {
        StringBuffer html = new StringBuffer();
        
        Iterator it = attendeeStatusList.iterator();
        while (it.hasNext()) {
            AttendeeStatus is = (AttendeeStatus)it.next();
            html.append("<option value=\"").append(is.getID()).append("\"")
                .append((selectedConstraint.equals(is) ? " SELECTED" : ""))
                .append(">").append(is.getName()).append("</option>\r\n");
        }
        
        return html.toString();
    }

   /**
    * Converts the object to XML representation without the XML version tag.
    * This method returns the object as XML text. 
    *
    * @return XML representation
    */
    public String getXMLBody() {

        StringBuffer xml = new StringBuffer();   
        xml.ensureCapacity(100);
        xml.append("<AttendeeStatus>\n");
        xml.append("<id>" + id + "</id>\n");
        xml.append("<name>" + XMLUtils.escape(getName()) + "</name>\n");
        xml.append("</AttendeeStatus>\n");

        return xml.toString();
    }


   /**
    * Converts the object to XML representation.
    * This method returns the object as XML text. 
    *
    * @return XML representation
    *
    */
    public String getXML() {
        return(IXMLPersistence.XML_VERSION + getXMLBody() );
    }

}
