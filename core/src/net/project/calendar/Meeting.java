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

 package net.project.calendar;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletContext;

import net.project.base.ObjectType;
import net.project.base.RecordStatus;
import net.project.base.URLFactory;
import net.project.base.property.PropertyProvider;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.link.ILinkableObject;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.resource.FacilityFactory;
import net.project.resource.Person;
import net.project.resource.UnknownFacility;
import net.project.security.SessionManager;
import net.project.space.SpaceFactory;
import net.project.util.ErrorLogger;
import net.project.util.TemplateFormatter;
import net.project.util.Validator;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * A subclass of event that can be stored as a calendar entry.
 * Meetings have an agenda, attendees, minutes, etc.
 *
 * @author AdamKlatzkin
 * @author Roger Bly
 * @since 03/00
 */
public class Meeting extends CalendarEvent implements ILinkableObject, java.io.Serializable {

    protected ArrayList agendaItems = null;
    protected String meetingID = null;
    protected String hostId = null;
    protected Person host = null;

    /**
     * The current user's attendance display status.
     * This is populated when calendar events are loaded.
     * It is set to the display status for the user who is viewing the calendar
     * events, or is null if the current user is not an attendee to an event.
     * This can occur when a space administrator sees meetings to which they
     * have not been invited.
     */
    private String currentUserAttendeeStatus = null;

    protected FacilityFactory facilityFactory = new FacilityFactory();
    private ArrayList newAttendeesList = new ArrayList();

    /**
     * Construct a meeting setting it's calendar
     */
    public Meeting(PnCalendar calendar) {
        super(calendar);
    }

    /**
     * Constructs a Meeting without a calendar context.
     */
    public Meeting() {
    }

    /**
     * set the Id of this meeting
     *
     */
    public void setID(String meetingId) {
        this.meetingID = meetingId;
    }

    /**
     * get the Id of this meeting
     *
     */
    public String getID() {
        return meetingID;
    }
    //Avinash:---------------------------------------------------------
    /**
     * set the Id of this meeting
     *
     */
    public void setMeetingID(String meetingId) {
        this.meetingID = meetingId;
    }

    /**
     * get the Id of this meeting
     *
     */
    public String getMeetingID() {
        return meetingID;
    }
    //Avinash:---------------------------------------------------------
   
    /**
     * override the getType provided by CalendarEvent
     *
     */
    public String getType() {
        return ObjectType.MEETING;
    }

    /**
     * create a url to get to the current meeting
     *
     */
    public String getURL() {
        return URLFactory.makeURL(meetingID, ObjectType.MEETING);
    }



    /**
     * set the ID of the meetings host
     * this is especially important for webex meetings since the host will be the only
     * user authorized to start the meeting.
     * @param hostId     the host's person id
     *
     */
    public void setHostID(String hostId) {
        host = null;

        if (this.hostId != null && !this.hostId.equals(hostId)) {
            this.hostId = hostId;
            addHostToNewAttendeesList(hostId);
        }

        this.hostId = hostId;

    }

    /**
     * get the ID of the meetings host
     * @return String    the host's person id
     *
     */
    public String getHostID() {
        return hostId;
    }

    /**
     * get the host's person object
     * @return Person    the host's person object
     *
     */
    public Person getHost() {

        if ((hostId == null) || (hostId.length() == 0))
            return null;

        try {
            // Does the host object need to be reloaded?
            if ((host == null) || (!hostId.equals(host.getID()))) {
                host = new Person(hostId);
                host.load();
            }
        } catch (net.project.persistence.PersistenceException pe) {
            return null;
        }

        return host;
    }

    /**
     * Specifies the current user's attendance status to this event.
     * @param currentUserAttendeeStatus the display status
     */
    void setCurrentUserAttendeeStatus(String currentUserAttendeeStatus) {
        this.currentUserAttendeeStatus = currentUserAttendeeStatus;
    }

    /**
     * Returns the current user's status to this event.
     * @return the display status or null if the current user is not an
     * attendee of this event (for example, the current user is a Space Admin
     * who is seeing events to which they have not been invited)
     * @see #setCurrentUserAttendeeStatus
     */
    String getCurrentUserAttendeeStatus() {
        return this.currentUserAttendeeStatus;
    }

    /**
     * Returns the next available agenda item sequence number
     *
     * @return int  the next available agenda item sequence number
     */
    public int getNextItemSeq() {

        if (meetingID != null) {

            String query = null;

            query = "SELECT next_agenda_item_seq " +
                    "FROM PN_MEETING  " +
                    "WHERE meeting_id = " + meetingID;

            DBBean db = new DBBean();
            try {
                db.setQuery(query);
                db.executeQuery();

                if (db.result.next())
                    return db.result.getInt(1);

            } catch (SQLException sqle) {
            	Logger.getLogger(Meeting.class).error("Meeting.getNextItemSeq failed " + sqle);
            } finally {
                db.release();
            }

            return 1;
        } else
            return 1;
    }

    /**
     * Converts the object to XML representation without the XML version tag.
     * This method returns the object as XML text.
     *
     * @return XML representation
     * @throws SQLException 
     */
    public String getXMLBody() throws SQLException {

        StringBuffer attendeesCSV = new StringBuffer();
        StringBuffer xml = new StringBuffer();

        xml.append("<meeting>\n");
        xml.append("<id>" + meetingID + "</id>\n");
        xml.append("<eventId>" + eventId + "</eventId>\n");
        xml.append("<name>" + XMLUtils.escape(name) + "</name>\n");
        xml.append("<description>" + XMLUtils.escape(description) + "</description>\n");
        xml.append("<purpose>" + XMLUtils.escape(purpose) + "</purpose>\n");
        xml.append("<startWeekDay>" + calendar.getUser().getDateFormatter().formatDate(startTime, "E") + "</startWeekDay>");
        xml.append("<startDate>" + SessionManager.getUser().getDateFormatter().formatDate(startTime) + "</startDate>\n");
        xml.append("<endDate>" + SessionManager.getUser().getDateFormatter().formatDate(endTime) + "</endDate>\n");
        xml.append("<startTime>" + SessionManager.getUser().getDateFormatter().formatTime(startTime).toLowerCase() + "</startTime>\n");
        xml.append("<endTime>" + SessionManager.getUser().getDateFormatter().formatTime(endTime).toLowerCase() + "</endTime>\n");
        xml.append("<timeZone>" + SessionManager.getUser().getDateFormatter().formatDate(startTime, " z") + "</timeZone>\n");
        xml.append("<frequencyTypeId>" + frequencyTypeId + "</frequencyTypeId>\n");
        xml.append("<frequency>" + XMLUtils.escape(frequency) + "</frequency>\n");

        // Generating XML as per ISO 8061 Standards for Date-Time
        xml.append("<startDateTime>" + XMLUtils.formatISODateTime(startTime) + "</startDateTime>\n");
        xml.append("<endDateTime>" + XMLUtils.formatISODateTime(endTime) + "</endDateTime>\n");

        if (facility != null)
            xml.append(facility.getXMLBody());

        xml.append("<AttendeesList>\n");

        for (int i = 0; i < getAttendees().size(); i++) {
            AttendeeBean attendee = (AttendeeBean) this.attendees.get(i);
            xml.append(attendee.getXMLBody());

            if (i == 0)
                attendeesCSV.append(" " + attendee.getPersonName());
            else
                attendeesCSV.append(" , " + attendee.getPersonName());
        }

        xml.append("<attendeesCSV>" + XMLUtils.escape(attendeesCSV.toString()) + "</attendeesCSV>\n");
        xml.append("</AttendeesList>\n");

        xml.append("</meeting>\n");

        return xml.toString();
    }

    /**
     * Converts the object to XML representation.
     * This method returns the object as XML text.
     *
     * @return XML representation
     * @throws SQLException 
     *
     */
    public String getXML() throws SQLException {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }

    /**
     * @return String   xml representation of the meetings agenda itmes
     *
     */
    public String getAgendaXML() {

        loadAgendaItems();

        StringBuffer xml = new StringBuffer();
        xml.append(IXMLPersistence.XML_VERSION);
        xml.append("<meeting>");

        for (int i = 0; i < agendaItems.size(); i++) {
            AgendaBean agenda = (AgendaBean) agendaItems.get(i);
            xml.append(agenda.getXMLBody());
        }

        xml.append("</meeting>");

        return xml.toString();
    }

    /**
     * @return ArrayList    the meetings agenda items - ArrayList of AgendaBean
     *
     */
    public ArrayList getAgendaItems() {
        loadAgendaItems();
        return agendaItems;
    }

    /**
     * get the id of the space the meeting belongs to.
     * Note: meeting does not need to be loaded but meeting id must be set
     *
     * @return the space ID
     * @exception PersistenceException
     *                   If anything goes wrong while loading from the database
     */
    public String getSpaceID()
            throws PersistenceException {

        String spaceId = null;

        if (meetingID == null)
            throw new NullPointerException("meetingId is null");

        String query = "select shc.space_id from pn_meeting m, pn_calendar_has_event che, pn_space_has_calendar shc where " +
                "m.meeting_id = " + meetingID + " " +
                "and m.calendar_event_id = che.calendar_event_id " +
                "and che.calendar_id = shc.calendar_id ";

        DBBean db = new DBBean();
        try {
            db.executeQuery(query);

            if (db.result.next()) {
                spaceId = db.result.getString("space_id");
            }

        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("failed to get meeting space id " +
                "from database", sqle);
        } finally {
            db.release();
        }
        return spaceId;
    }
    
    /**
     * get the type of the space the meeting belongs to.
     *
     * @return the space type
     * @exception PersistenceException
     *                   If anything goes wrong while loading from the database
     */
    public String getSpaceType() {
    	try {
			return SpaceFactory.constructSpaceFromID(getSpaceID()).getType();
		} catch (PersistenceException e) {
			Logger.getLogger(Meeting.class).error("Meeting.getSpaceType exception: " + e);
		}
		return null;
    }


    /**
     * build an internal data structure of the meeting's agenda items
     *
     */
    private void loadAgendaItems() {

        AgendaBean agenda = null;
        String query = null;

        query = "SELECT ai.AGENDA_ITEM_ID, ai.item_name, ai.TIME_ALLOTED, ai.STATUS_ID, " +
                "gd.code_name as status, p.display_name as owner_name, ai.ITEM_SEQUENCE " +
                "FROM PN_AGENDA_ITEM ai, pn_person p, pn_global_domain gd " +
                "WHERE ai.meeting_id = " + meetingID + " " +
                "and ai.record_status = 'A' and p.person_id = ai.owner_id " +
                "and gd.table_name = 'pn_agenda_item' and gd.column_name = 'status_id' " +
                "and gd.code = ai.status_id " +
                "order by ai.item_sequence asc";

        DBBean db = new DBBean();
        try {
            db.setQuery(query);
            db.executeQuery();

            agendaItems = new ArrayList();

            while (db.result.next()) {

                // populate a DiscussionGroup object from the current result row
                agenda = new AgendaBean();
                agenda.setId(db.result.getString(1));
                agenda.setName(db.result.getString(2));
                agenda.setAllotedTime(db.result.getString(3));
                agenda.setStatusId(db.result.getString(4));
                agenda.setStatus(PropertyProvider.get(db.result.getString(5)));
                agenda.setOwner(db.result.getString(6));
                agenda.setItemSequence(db.result.getString(7));
                // add the discussion group object to the ArrayList
                agendaItems.add(agenda);

            }

        } catch (SQLException sqle) {
        	Logger.getLogger(Meeting.class).error("Meeting.loadAgendaItems failed " + sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Persist the meeting
     *
     */
    public void store() throws PersistenceException {

        boolean isNewMeeting = Validator.isBlankOrNull(meetingID);

        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);

            // First, try to store a facility for this meeting, if possible
            if (!(facility instanceof UnknownFacility)) {
                facility.store(db);
                facilityId = facility.getId();
            }

            // call Stored Procedure to insert or update all the tables involved in storing a meeting.
            int index = 0;
            int descriptionClobIndex = 0;
            int purposeClobIndex = 0;
            int meetingIDIndex = 0;
            int eventIDIndex = 0;

            db.prepareCall("{call CALENDAR.STORE_MEETING(?,?,?,?,?,?,?,?,?,?,?, ?,?,?,?)}");
            db.cstmt.setInt(++index, Integer.parseInt(calendar.getUser().getID()));
            db.cstmt.setInt(++index, Integer.parseInt(calendar.getSpaceId()));
            db.cstmt.setInt(++index, Integer.parseInt(calendar.getID()));

            if (super.getID() == null)
                db.cstmt.setNull(++index, java.sql.Types.INTEGER);
            else
                db.cstmt.setInt(++index, Integer.parseInt(super.getID()));

            if ((meetingID == null) || (meetingID.length() == 0))
                db.cstmt.setNull(++index, java.sql.Types.INTEGER);
            else
                db.cstmt.setInt(++index, Integer.parseInt(meetingID));

            db.cstmt.setInt(++index, Integer.parseInt(hostId));
            db.cstmt.setString(++index, name);
            db.cstmt.setString(++index, frequencyTypeId);
            db.cstmt.setInt(++index, Integer.parseInt(facilityId));
            db.cstmt.setTimestamp(++index, new Timestamp(startTime.getTime()));
            db.cstmt.setTimestamp(++index, new Timestamp(endTime.getTime()));
            db.cstmt.registerOutParameter((descriptionClobIndex = ++index), java.sql.Types.CLOB);
            db.cstmt.registerOutParameter((purposeClobIndex = ++index), java.sql.Types.CLOB);
            db.cstmt.registerOutParameter((meetingIDIndex = ++index), java.sql.Types.INTEGER);
            db.cstmt.registerOutParameter((eventIDIndex = ++index), java.sql.Types.INTEGER);
            db.cstmt.execute();

            meetingID = Integer.toString(db.cstmt.getInt(meetingIDIndex));
            eventId = Integer.toString(db.cstmt.getInt(eventIDIndex));

            // Now stream the description and purpose to the clob locaters
            ClobHelper.write(db.cstmt.getClob(descriptionClobIndex), this.description);
            ClobHelper.write(db.cstmt.getClob(purposeClobIndex), this.purpose);


            if (isNewMeeting) {
                addHostToNewAttendeesList(hostId);
            }

            db.commit();

            //ENW: removed by PNET in 7.7-8.2, but restored in ENW 4.1.1. It will be reported to PNET as a bug
	        try {
                //Update pn_object_name table with event id and name for load assignments needs
	        	Logger.getLogger(ObjectType.class).debug("isNewMeeting="+isNewMeeting);
                if (isNewMeeting) {
                    db.prepareStatement("insert into pn_object_name (object_id, name)  values (?, ?)");
                    db.pstmt.setString(1, meetingID);
                    db.pstmt.setString(2, getName() );
                    db.executePrepared();
                } else {
                    db.prepareStatement("update pn_object_name set name=? where object_id=?");
                    db.pstmt.setString(1, getName() );
                    db.pstmt.setString(2, meetingID);
                    db.executePrepared();
                }
	        } catch (SQLException sqle) {
	        	Logger.getLogger(Meeting.class).debug("ERROR: meeting: Unable to update pn_object_name for meetingId:"+meetingID+" meeting name="+getName() );
			     System.out.println("Unable to insert into/update pn_object_name for meetingId:"+meetingID+" meeting name="+getName());
	        }
	        //End of ENW
	        
        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException e) {
            	Logger.getLogger(Meeting.class).debug("Unable to roll back creation of Meeting.");
                //Nothing can be done about this.
            }
            throw new PersistenceException("Error storing meeting", sqle);

        } catch (NumberFormatException nfe) {
            try {
                db.rollback();
            } catch (SQLException e) {
            	Logger.getLogger(Meeting.class).debug("Unable to roll back creation of Meeting.");
                //Nothing can be done about this.
            }
            throw new PersistenceException("ParseInt Failed in Meeting.store()", ErrorLogger.HIGH);

        } finally {
            db.release();

        }
    }

    /**
     * load a persisted meeting.
     * meeting id must be set
     *
     */
    public void load()
            throws PersistenceException {

        String query;

        isLoaded = false;

        if (meetingID == null)
            throw new NullPointerException("meetingId is null");

        query = "select e.calendar_event_id, e.event_name, e.event_desc_clob, e.frequency_type_id,  gd.code_name as frequency, e.event_type_id, " +
                "e.facility_id, e.event_purpose_clob, e.start_date, e.end_date, e.record_status, m.host_id " +
                "from pn_meeting m, pn_calendar_event e,  pn_global_domain gd where m.meeting_id=" + meetingID + " " +
                " and e.calendar_event_id = m.calendar_event_id and gd.table_name = 'pn_calendar_event' and gd.column_name = 'frequency_type_id' and gd.code = e.frequency_type_id";

        DBBean db = new DBBean();
        try {
            db.executeQuery(query);

            while (db.result.next()) {

                eventId = db.result.getString("calendar_event_id");
                name = db.result.getString("event_name");
                description = ClobHelper.read(db.result.getClob("event_desc_clob"));
                frequencyTypeId = db.result.getString("frequency_type_id");
                frequency = PropertyProvider.get(db.result.getString("frequency"));
                facilityId = db.result.getString("facility_id");

                if ((facilityId != null) && (facilityId.length() > 0))
                    facility = facilityFactory.make(db.result.getString("facility_id"));

                purpose = ClobHelper.read(db.result.getClob("event_purpose_clob"));
                startTime = db.result.getTimestamp("start_date");
                endTime = db.result.getTimestamp("end_date");
                setRecordStatus(RecordStatus.findByID(db.result.getString("record_status")));
                hostId = db.result.getString("host_id");
            }

            this.attendees = getAttendees();

            isLoaded = true;
        } catch (java.sql.SQLException sqle) {
            isLoaded = false;
            throw new PersistenceException("failed to load CalendarEvent from " +
                "database", sqle);
        } finally {
            db.release();
        }
    }

    /**
     * remove a persisted meeting.
     * event id must be set
     *
     * @exception PersistenceException
     *                   is thrown if anything goes wrong
     */
    public void remove()
            throws PersistenceException {

        if (meetingID == null)
            throw new NullPointerException("meetingId is null");

        super.remove();
        isLoaded = false;
    }

    /**
     * reset the meeting data structure
     *
     * @since 03/00
     */
    public void reset() {
        super.reset();
        clearNewAttendeesList();
        agendaItems = null;
        meetingID = null;
        hostId = null;
        host = null;
    }

    /**
     * Adds a new Attendee to the List
     *
     * @param attendee <code>Attendee</code> for the meeting
     */
    public void addNewAttendees(AttendeeBean attendee) {
        this.newAttendeesList.add(attendee);
    }
    
    /**
     * Remove a new attendee.
     * 
     * @param attendeeID
     */public void removeNewAttendees(String attendeeID) {
    	for(int i = 0;  i < this.newAttendeesList.size();  ++i)
    	{
    		AttendeeBean ab = (AttendeeBean)this.newAttendeesList.get(i);
    		if( attendeeID.equals( ab.getID() ) )
        		this.newAttendeesList.remove(i);
        }
    }

    /**
     * Notify individual attendees
     * @throws SQLException 
     */
    public void notifyNewAttendees() throws SQLException {
        Iterator itr = this.newAttendeesList.iterator();

        while (itr.hasNext()) {
            AttendeeBean attendee = (AttendeeBean) itr.next();
            //com.enw.util.DBUtils.setAssignmentRecordStatus( getID(), attendee.getID(), 'A');
            attendee.notifyAttendee();
        }
    }

    /**
     * Get list of new Attendees.
     * 
     * @return ArrayList of new attendees.
     */
    public ArrayList getNewAttendees() {
			return newAttendeesList;
    }
    
    /**
     * Clears the new Attendees List
     */
    public void clearNewAttendeesList() {
        this.newAttendeesList.clear();
    }

    /**
     * Adds host to the New Attendees List so that he can be notified too
     *
     * @param hostID the host ID
     */
    private void addHostToNewAttendeesList(String hostID) {

        AttendeeBean attendee = new AttendeeBean();
        attendee.setEvent(this);
        attendee.setStatusID("20");
        attendee.setID(hostID);
        attendee.setHostID(hostID);

        addNewAttendees(attendee);

    }
    
    public boolean hasAgendaItems(){
    	return getAgendaItems().isEmpty();
    }
    
    /**
	 * details of this Meeting object in html format.
	 * By usnig a template meeting-detail.tml
	 * @param servletContext
	 * @return <code>String</code>HTML of detaill for this object. 
	 */
	public String getDetails(ServletContext servletContext){
		return new TemplateFormatter(servletContext, "/details/template/meeting-detail.tml").transForm(this);
	}

}
