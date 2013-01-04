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
|
+----------------------------------------------------------------------*/
package net.project.calendar;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;
import net.project.resource.Roster;
import net.project.xml.XMLUtils;

/**
* Describe class <code>AttendeeList</code> here.
*
* @author Matthew Flower
* @since Gecko
*/
public class UninvitedTeamMemberList extends ArrayList implements IXMLPersistence {
    private CalendarEvent event;
    
    /**
     * Gets the value of event
     *
     * @return the value of event
     */
    public CalendarEvent getEvent() {
        return this.event;
    }

    /**
     * Sets the value of event
     *
     * @param argEvent Value to assign to this.event
     */
    public void setEvent(CalendarEvent argEvent){
        this.event = argEvent;
    }

    /**
     * InvitationList gets the list of all persons who have been invited to 
     * attend the event.  It is used to determine who should be filtered
     * out of the list of people in this space.
     */
    private class InvitationList extends HashSet {
        /**
         * Creates a new <code>InvitationList</code> instance.
         *
         * @param eventID a <code>String</code> value
         * @exception PersistenceException if an error occurs
         */
        InvitationList(String eventID) throws PersistenceException {
            DBBean db = new DBBean();
            try {
                db.prepareStatement("select person_id from pn_cal_event_has_attendee "+
                                    "where calendar_event_id = ?");
                db.pstmt.setString(1, eventID);
                db.executePrepared();

                while (db.result.next()) {
                    this.add((Object)db.result.getString("person_id"));
                }
            } catch(SQLException sqle)  {
                throw new PersistenceException("Unable to get list of people "+
                                               "invited to event " +  eventID,
                                               sqle);
                
            } finally {
                db.release();
            }
        }
    }


    /**
     * Clear out the private member variables of this bean.
     */
    public void clear() {
        super.clear();
        event = null;
    }
    
    /**
     * Describe <code>load</code> method here.
     *
     * @exception PersistenceException if an error occurs
     */
    public void load() throws PersistenceException {
        //First, get all of the list of the people in this space.
        Roster roster = new Roster();
        roster.setSpace(event.getCalendar().getSpace());
        roster.load();

        //Second, get the people the people that have already been invited to
        //the event
        InvitationList invitedList = new InvitationList(event.getEventId());

        //Third, iterate through the list of people in the roster, adding
        //them to the list if they haven't already been invited.
        Iterator it = roster.iterator();
        Person currentPerson;
        while (it.hasNext()) {
            //Get the person
            currentPerson = (Person)it.next();
            //If the id of this person hasn't been invited, invite them
            if (! invitedList.contains(currentPerson.getID())) {
                this.add(currentPerson);
            }
        }        
    }

    /**
     * Return an XML representation of this object, include the XML header
     *
     * @return a <code>String</code> value containing the XML for this object
     */
    public String getXML() {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }

    /**
     * Returns the XML representation of this object, minus the XML header.
     * This allows the XML from this object to be embedded in other objects.
     *
     * @return a <code>String</code> value containing this objects XML
     * representation.
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<optionlist>\n");

        //Iterate through the person objects stored in this object
        Iterator it = this.iterator();
        Person currentPerson;
        while (it.hasNext()) {
            currentPerson = (Person)it.next();

            xml.append("<option>\n");
            xml.append("<optionvalue>").append(currentPerson.getID()).append("</optionvalue>\n");
            xml.append("<optiontext>").append(XMLUtils.escape(currentPerson.getDisplayName())).append("</optiontext>\n");
            xml.append("</option>\n");
        }

        xml.append("</optionlist>\n");

        return xml.toString();
    }
}

