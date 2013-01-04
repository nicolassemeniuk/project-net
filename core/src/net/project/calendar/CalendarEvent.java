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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import net.project.base.ObjectType;
import net.project.base.RecordStatus;
import net.project.base.URLFactory;
import net.project.base.property.PropertyProvider;
import net.project.calendar.vcal.VCalendar;
import net.project.calendar.vcal.VEvent;
import net.project.calendar.vcal.parameter.StatusParameter;
import net.project.calendar.vcal.property.AttendeeProperty;
import net.project.calendar.vcal.property.DescriptionProperty;
import net.project.calendar.vcal.property.EndDateTimeProperty;
import net.project.calendar.vcal.property.LocationProperty;
import net.project.calendar.vcal.property.StartDateTimeProperty;
import net.project.calendar.vcal.property.SummaryProperty;
import net.project.calendar.vcal.property.TimeZoneProperty;
import net.project.code.TableCodeDomain;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.link.ILinkableObject;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentManager;
import net.project.resource.FacilityTypeDomain;
import net.project.resource.IFacility;
import net.project.resource.UnknownFacility;
import net.project.security.SessionManager;
import net.project.util.ErrorLogger;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.util.Validator;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * Basic implementation of an object that appears in a calendar.  A
 * <code>CalendarEvent</code> object only contains basic information about an
 * event that occurs.  For something more specific that includes agendas,
 * attendee lists, minutes, etc, try a {@link net.project.calendar.Meeting}
 * object instead.
 *
 * @author Roger Bly
 * @author Adam Klatzkin
 * @since 03/2000
 */
public class CalendarEvent implements ICalendarEntry, ILinkableObject {
    /**
     * The calendar this event belongs to.  Used for proper timezone
     * formatting, etc.
     */
    public PnCalendar calendar = null;
    // From database table pn_calendar_event
    protected String eventId = null;
    protected String name = null;
    protected String description = null;
    protected String purpose = null;
    protected String frequencyTypeId = null;
    protected String frequency = null;

    // Initialize dates to NOW. so we don't get null pointer on setStartTime(), etc.
    protected java.util.Date startTime = initializeDate();
    protected java.util.Date endTime = startTime;

    /**
     * The current record status.
     */
    private RecordStatus recordStatus = null;

    // the facility this event uses
    protected String facilityId = null;
    // We are going to start with a physical facility by default as a placeholder
    // so we can store and fetch information.
    protected IFacility facility = new UnknownFacility();

    // db access bean
    protected boolean isLoaded = false;

    /**
     * A calculated value representing the length of the event.
     */
    private TimeQuantity eventDuration;


    /**
     * A list of meeting attendees.  This list should only contain
     * {@link net.project.calendar.AttendeeBean} objects.
     */
    protected ArrayList attendees = new ArrayList();
    
    /**
     * A space id this event belongs to.
     */
    private String eventSpaceId;
    
    /**
     * A space type this event belongs to.
     */
    private String eventSpaceType;

    /**
     * Construct a calendar event setting its calendar.
     *
     * @see #setCalendar
     */
    public CalendarEvent(PnCalendar calendar) {
        this.calendar = calendar;
    }

    /**
     * Construct a new instance of <code>CalendarEvent</code>.
     */
    public CalendarEvent() {
        calendar = new PnCalendar(SessionManager.getUser());
        // Reset the seconds and milliseconds to Zero since we do not support
        // that level of granularity in the calendar.
        this.calendar.set(Calendar.MILLISECOND, 0);
        this.calendar.set(Calendar.SECOND, 0);
    }

    /**
     * Set the calendar context for this calendar event.  This provides
     * calendar, locale and timezone information used to format times and dates,
     * etc.  If no calendar context is set, the defaults (US, english) for these
     * will be used when formatting dates and times.
     */
    public void setCalendar(PnCalendar newCalendar) {
        this.calendar = newCalendar;
        // Reset the seconds and milliseconds to Zero since we do not support
        // that level of granularity in the calendar.
        this.calendar.set(Calendar.MILLISECOND, 0);
        this.calendar.set(Calendar.SECOND, 0);

        // Set the startTime, then set endTime to one hour later
        // ensuring that seconds are reset to zero
        if (startTime == null)
            startTime = initializeDate(PnCalendar.currentTime());
        if (endTime == null)
            endTime = new java.util.Date(startTime.getTime() + 3600000);

    }

    /**
     * @return PnCalendar the event's calendar context
     */
    public PnCalendar getCalendar() {
        return calendar;
    }


    /**
     * Set the ID of this calendar event.
     */
    public void setID(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Get the ID of this calendar event.
     */
    public String getID() {
        return eventId;
    }

    /**
     * Returns the duration of the CalendarEvent.
     * Values calculated by subtracting the end date/time from the start date/time.
     * Note, the calendar event must be loaded.
     * @return <code>TimeQuantity</code> representing the duration in hours.
     */
    public TimeQuantity getEventDuration() {

        long durationInMillis = getEndTime().getTime() - getStartTime().getTime();
        return (new TimeQuantity(durationInMillis, TimeQuantityUnit.HOUR));
    }

    /**
     * @return String the URL
     */
    public String getURL() {
        return URLFactory.makeURL(eventId, ObjectType.EVENT);
    }

    /**
     * Classes that derive from CalendarEvent (e.g. Meeting) may provide their
     * own id's that are accessed by set and get id, overriding those provided
     * by CalendarEvent.  Therefore we provide an alternate way of accessing the
     * event id that is final.
     *
     * @param eventID the ID of the calendar event
     */
    public final void setEventId(String eventID) {
        this.eventId = eventID;
    }

    /**
     * Gets the id of the calendar event.
     *
     * @return String the id of the calendar event
     */
    public final String getEventId() {
        return eventId;
    }

    /**
     * Return the type of this specific event.  For a plain calendar event, this
     * will be <code>ObjectType.EVENT</code>.  For subclasses, this value may be
     * different.
     *
     * @return String the object type.  The calendar event class always returns
     * <code>ObjectType.EVENT</code>.
     */
    public String getType() {
        return ObjectType.EVENT;
    }

    /**
     * @return String the name of this calendar event
     */
    public String getName() {
        return name;
    }

    /**
     * @param name a <code>String</code> the name of this calendar event
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param description a <code>String</code> value containing the description
     * of this calendar event
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return String the description of this calendar event
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param purpose a <code>String</code> value containing the purpose of this
     * calendar event
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * @return String the purpose of this calendar event
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * Sets the start time from a string.
     *
     * @param time the start time using a time-formatted String; the format
     * is assumed to match the pattern <code>h:mm a</code>
     * @deprecated As of 7.5; Use <code>TimeBean.parseTime(request, <i>name</i>, getStartTime())</code>
     * instead (where <code>name</code> is the name passed to the <input:time .../> taglib).
     * This method assumes a hardcoded format of <code>h:mm a</code> which is
     * not localized.
     */
    public void setStartTimeString(String time) {
        PnCalendar tmpCalendar;

        calendar.setTime(startTime);
        tmpCalendar = (PnCalendar)calendar.clone();

        // Here we are applying a time format of "h:mm a" , because that is the time format of the
        // string ,  send to us . If we don't apply any pattern , then user's default time
        // pattern is applied and the parsing fails

        tmpCalendar.setTime(calendar.getUser().getDateFormatter().parseTime(time, "h:mm a"));
        calendar.set(Calendar.HOUR, tmpCalendar.get(Calendar.HOUR));
        calendar.set(Calendar.MINUTE, tmpCalendar.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, 0);// Since the user can only specify hour, minute and am/pm
        calendar.set(Calendar.AM_PM, tmpCalendar.get(Calendar.AM_PM));

        setStartTime(calendar.getTime());

    }

    /**
     * Sets the start time.
     * @param time the date containing the start time
     */
    public void setStartTime(Date time) {
        this.startTime = time;
    }

    /**
     * Returns the formatted start time.
     *
     * @return a formatted String representation of the start time; it is
     * formatted according to the user's settings
     * @deprecated As of 7.5; use <code>new net.project.util.DateFormat(user).formatTime(getStartTime())</code>
     * instead, to use the default time format for the user's locale
     * This methods uses a hardcoded format of hh:mm a z
     */
    public String getStartTimeFormatted() {
        return calendar.getUser().getDateFormatter().formatTime(startTime,
                    net.project.util.DateFormat.CUSTOM_TIME_FORMAT);
    }

    /**
     * Returns the start time based on the current user's time formatting
     * preferences.
     *
     * @return a formatted <code>String</code> representation of the start time;
     * it is formatted according to the user's settings
     * @deprecated As of Gecko Update 4.  This method is not symmetrically
     * opposite from <code>setStartTimeString(String)</code>.  The start time
     * is formatted based on the user's settings.  This method may be removed
     * in a future release.
     * Use {@link #getStartTimeFormatted} instead.
     */
    public String getStartTimeString() {
        return calendar.getUser().getDateFormatter().formatTime(startTime);
    }

    /**
     * Gets the date and time that this item should first appear on the calendar.
     * For example, the time and date that a 2 day meeting begins.
     *
     * @return a <code>Date</code> with time and date set.
     */
    public java.util.Date getStartTime() {
        return startTime;
    }

    /**
     * Sets the end time from a string.
     *
     * @param time the end time using a time-formatted String; the format
     * is assumed to match the pattern <code>h:mm a</code>
     * @deprecated As of 7.5; Use <code>TimeBean.parseTime(request, <i>name</i>, getStartTime())</code>
     * instead (where <code>name</code> is the name passed to the <input:time .../> taglib).
     * This method assumes a hardcoded format of <code>h:mm a</code> which is
     * not localized.
     */
    public void setEndTimeString(String time) {
        PnCalendar tmpCalendar;
        calendar.setTime(endTime);
        tmpCalendar = (PnCalendar)calendar.clone();

        // Here we are applying a time format of "h:mm a" , because that is the
        // time format of the string ,  send to us . If we don't apply any
        // pattern , then user's default time pattern is applied and the parsing
        // fails.

        tmpCalendar.setTime(calendar.getUser().getDateFormatter().parseTime(time, "h:mm a"));
        calendar.set(Calendar.HOUR, tmpCalendar.get(Calendar.HOUR));
        calendar.set(Calendar.MINUTE, tmpCalendar.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, 0);// Since the user can only specify hour, minute and am/pm
        calendar.set(Calendar.AM_PM, tmpCalendar.get(Calendar.AM_PM));

        setEndTime(calendar.getTime());
    }

    /**
     * Sets the end time.
     * @param time the date containing the end time
     */
    public void setEndTime(Date time) {
        this.endTime = time;
    }

    /**
     * Get a <code>String</code> which contains the end time for this calendar
     * event formatted for the current user's time formatting preferences.
     * @return String a formatted <code>String</code> representation of the end
     * time.
     * @deprecated As of 7.5; use <code>new net.project.util.DateFormat(user).formatTime(getEndTime())</code>
     * instead, to use the default time format for the user's locale
     * This methods uses a hardcoded format of hh:mm a z
     */
    public String getEndTimeFormatted() {
        return calendar.getUser().getDateFormatter().formatTime(endTime,
            net.project.util.DateFormat.CUSTOM_TIME_FORMAT);
    }

    /**
     * @return String a formatted String representation of the end time
     * @deprecated As of Gecko Update 4.  This method is not symmetrically
     * opposite from <code>setEndTimeString(String)</code>.  The end time
     * is formatted based on the user's settings.  This method may be removed
     * in a future release.
     * Use {@link #getEndTimeFormatted} instead.
     */
    public String getEndTimeString() {
        return ((calendar.getUser()).getDateFormatter()).formatTime(endTime);
    }

    /**
     * Gets the date and time that this item should last appear on the calendar.
     * For example, the time and date that a 2 day meeting end.
     *
     * @return a Date with time and date set.
     */
    public java.util.Date getEndTime() {
        return endTime;
    }

    /**
     * Set the date of this calendar event.
     *
     * @param date the date of this event using a date-formatted String
     */
    public void setDate(String date) throws net.project.util.InvalidDateException {

        PnCalendar tmpCalendar;

        calendar.setTime(startTime);
        tmpCalendar = (PnCalendar)calendar.clone();
        tmpCalendar.setTime(calendar.getUser().getDateFormatter().parseDateString(date));
        calendar.set(Calendar.MONTH, tmpCalendar.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, tmpCalendar.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.YEAR, tmpCalendar.get(Calendar.YEAR));
        startTime = calendar.getTime();

        calendar.setTime(endTime);
        calendar.set(Calendar.MONTH, tmpCalendar.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, tmpCalendar.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.YEAR, tmpCalendar.get(Calendar.YEAR));
        endTime = calendar.getTime();
    }

    /**
     * @return a formatted String representation of the event date
     */
    public String getDateString() {
        return calendar.getUser().getDateFormatter().formatDate(startTime);
    }

    /**
     * @param id   the type id of the calendar event's frequency
     */
    public void setFrequencyTypeId(String id) {
        this.frequencyTypeId = id;
    }

    /**
     * @return String   the type id of the calendar event's frequency
     */
    public String getFrequencyTypeId() {
        return frequencyTypeId;
    }

    /**
     * @param frequency   the calendar event's frequency type string
     */
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    /**
     * @return String   the calendar event's frequency type string
     */
    public String getFrequency() {
        return frequency;
    }

    /**
     * @return String the HTML list of options for the calendar event's frequency
     * populated from the table code domain.  The event's current frequency will be
     * selected.
     */
    public String getFrequencyList() {
        TableCodeDomain domain = new TableCodeDomain();

        domain.setTableName("pn_calendar_event");
        domain.setColumnName("frequency_type_id");

        domain.load();

        return domain.getOptionList(frequency);
    }

    /**
     * Set the id of the facility where this calendar event will occur.  This
     * value will be saved in the database as the facility associated with this
     * calendar event UNLESS a valid facility is set in this CalendarEvent using
     * the {@link #setFacility} method.  If that occurs, this facility ID will
     * be overwritten in the {@link #store} method when that facility is stored.
     *
     * Note that the default facility created in this object, which is of type
     * {@link net.project.resource.UnknownFacility} will not trigger this over-
     * writing of the facility id.  Facilities of that type are ignored.
     *
     * @param facilityID the calendar event's facility id
     */
    public void setFacilityId(String facilityID) {
        this.facilityId = facilityID;
        facility = null;
    }

    /**
     * Get the ID of the facility where this meeting will be held.
     *
     * @return String the ID for the facility this meeting will use.  If a
     * facility has not yet been assigned, this value will be null.
     */
    public String getFacilityId() {
        return facilityId;
    }

    /**
     * Sets the internal facility.  If set, this facility will be stored and the
     * value in setFacilityId will be overwritten.  This provides a mechanism to
     * allow this object to save information about a facility while a calendar
     * event is being created.
     *
     * @param facility a <code>IFacility</code> object to be stored internally.
     * @see #setFacilityId
     */
    public void setFacility(IFacility facility) {
        this.facility = facility;
    }

    /**
     * Get the facility where this calendar event is to occur.  If this is a new
     * calendar event that has not yet been saved in the database, this method
     * will always return a facility object stored within.  If it isn't, this
     * method will lazy load the facility from the database if it hsan't already
     * been loaded.
     *
     * @return IFacility the facility for this event
     */
    public IFacility getFacility() {
        boolean isNewEvent = Validator.isBlankOrNull(facility.getId());

        //Is this a new event?
        if (isNewEvent) {
            //This is a new event, don't worry about lazy loading
        } else {
            // Does the host object need to be reloaded?
            if ((Validator.isBlankOrNull(facilityId)) || (!facilityId.equals(facility.getId()))) {
                facility = calendar.facilityFactory.make(facilityId);
            }
        }

        return facility;
    }

    /**
     * This method will return a list of all the available facility types.
     *
     * @return String the HTML list of options for the calendar event's facility
     * populated from the table code domain.  The event's current facility will
     * be selected.
     */
    public String getFacilityList() {
        FacilityTypeDomain domain = new FacilityTypeDomain();
        domain.load();

        String typeID = null;
        if (facility != null) {
            typeID = facility.getType().getID();
        }
        return domain.getOptionList(typeID);
    }

    /**
     * Specifies the record status of this calendar event.
     * @param recordStatus the record status
     */
    protected void setRecordStatus(RecordStatus recordStatus) {
        this.recordStatus = recordStatus;
    }

    /**
     * Returns the current record status.
     * @return the current record status
     */
    public RecordStatus getRecordStatus() {
        return this.recordStatus;
    }

    /**
     * Converts the object to XML representation without the XML version tag.
     * This method returns the object as XML text.
     *
     * @return XML representation
     * @throws SQLException 
     */
    public String getXMLBody() throws SQLException {
        StringBuffer xml = new StringBuffer();
        xml.append("<event>\n");
        xml.append("<id>" + eventId + "</id>\n");
        xml.append("<name>" + XMLUtils.escape(name) + "</name>\n");
        xml.append("<description>" + XMLUtils.escape(description) + "</description>\n");
        xml.append("<purpose>" + XMLUtils.escape(purpose) + "</purpose>\n");
        xml.append("<startDate>" + calendar.getUser().getDateFormatter().formatDate(startTime) + "</startDate>\n");
        xml.append("<startWeekDay>" + calendar.getUser().getDateFormatter().formatDate(startTime, "E") + "</startWeekDay>");
        xml.append("<endDate>" + calendar.getUser().getDateFormatter().formatDate(endTime) + "</endDate>\n");
        xml.append("<startTime>" + calendar.getUser().getDateFormatter().formatTime(startTime).toLowerCase() + "</startTime>\n");
        xml.append("<endTime>" + calendar.getUser().getDateFormatter().formatTime(endTime).toLowerCase() + "</endTime>\n");
        xml.append("<timeZone>" + calendar.getUser().getDateFormatter().formatDate(startTime, " z") + "</timeZone>\n");
        xml.append("<frequencyTypeId>" + frequencyTypeId + "</frequencyTypeId>\n");
        xml.append("<frequency>" + XMLUtils.escape(frequency) + "</frequency>\n");

        // Generating XML as per ISO 8061 Standards for Date-Time
        xml.append("<startDateTime>" + XMLUtils.formatISODateTime(startTime) + "</startDateTime>\n");
        xml.append("<endDateTime>" + XMLUtils.formatISODateTime(endTime) + "</endDateTime>\n");

        if (facility != null)
            xml.append(facility.getXMLBody());

        xml.append("</event>\n");

        return xml.toString();
    }

    /**
     * Converts the object to XML representation.
     * This method returns the object as XML text.
     *
     * @return XML representation
     * @throws SQLException 
     */
    public String getXML() throws SQLException {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }

    /**
     * @return String the XML representation of the event's attendees
     */
    public String getAttendeesXML() {
        loadAttendees();

        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" ?>\n");
        xml.append("<meeting>");
        for (int i = 0; i < attendees.size(); i++) {
            AttendeeBean attendee = (AttendeeBean)attendees.get(i);
            xml.append(attendee.getXMLBody());
        }
        xml.append("</meeting>");
        return xml.toString();
    }

    /**
     * Get an Arraylist of the Attendees of this meeting.  This ArrayList will
     * contain zero or more AttendeeBean objects.
     *
     * @return ArrayList array list of AttendeeBean object instances.
     */
    public ArrayList getAttendees() {
        loadAttendees();
        return attendees;
    }

    /**
     * Build an internal data structure of the event's attendees.
     */
    private void loadAttendees() {
        AttendeeBean attendee = null;
        String query = null;

        this.attendees = new ArrayList();

        query = "SELECT at.person_id, at.attendee_comment, at.status_id, " +
            "gd.code_name as status, p.display_name as owner_name " +
            "FROM pn_cal_event_has_attendee at, pn_person p, pn_global_domain gd " +
            "WHERE at.calendar_event_id = " + eventId + " " +
            "and p.person_id = at.person_id " +
            "and gd.table_name = 'pn_cal_event_has_attendee' and gd.column_name = 'status_id' " +
            "and gd.code = at.status_id ";

        DBBean db = new DBBean();
        try {
            db.setQuery(query);
            db.executeQuery();

            while (db.result.next()) {
                attendee = new AttendeeBean();
                attendee.setID(db.result.getString(1));
                attendee.setComment(db.result.getString(2));
                attendee.setStatusID(db.result.getString(3));
                attendee.setStatus(PropertyProvider.get(db.result.getString(4)));
                attendee.setPersonName(db.result.getString(5));
                attendee.setEvent(this);
                attendee.m_isLoaded = true;
                // add the discussion group object to the ArrayList
                attendees.add(attendee);
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(CalendarEvent.class).error("CalendarEvent.loadAttendees failed " + sqle);
        } finally {
            db.release();
        }

    }

    /**
     * Reset the event data structure.
     */
    public void reset() {
        eventId = null;
        name = null;
        description = null;
        purpose = null;
        frequencyTypeId = null;
        frequency = null;
        startTime = null;
        endTime = null;
        facilityId = null;
        facility = new UnknownFacility();
        recordStatus = null;
        calendar = null;
        isLoaded = false;
        attendees = null;
    }

    /**
     * Indicates whether the event has already been loaded from the database.
     *
     * @return boolean true if the event is loaded
     */
    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Persist the event to the database.
     *
     * @throws PersistenceException if there is an error storing the event in
     * the database.
     */
    public void store() throws PersistenceException {

        DBBean db = new DBBean();

        try {
            db.setAutoCommit(false);

            //First, try to store the facility
            if (!(facility instanceof UnknownFacility)) {
                facility.store(db);
                facilityId = facility.getId();
            }

            // call Stored Procedure to insert or update all the tables involved in storing a meeting.
            int index = 0;
            int descriptionClobIndex = 0;
            int purposeClobIndex = 0;
            int eventIDIndex = 0;

            db.prepareCall("{call CALENDAR.STORE_EVENT(?,?,?,?,?,?,?,?,?, ?,?,?)}");
            db.cstmt.setInt(++index, Integer.parseInt(calendar.getUser().getID()));
            db.cstmt.setInt(++index, Integer.parseInt(calendar.getSpaceId()));
            db.cstmt.setInt(++index, Integer.parseInt(calendar.getID()));

            if ((getID() == null) || (getID().length() == 0))
                db.cstmt.setNull(++index, java.sql.Types.INTEGER);
            else
                db.cstmt.setInt(++index, Integer.parseInt(getID()));

            db.cstmt.setString(++index, name);
            db.cstmt.setString(++index, frequencyTypeId);
            db.cstmt.setInt(++index, Integer.parseInt(facilityId));
            db.cstmt.setTimestamp(++index, new Timestamp(startTime.getTime()));
            db.cstmt.setTimestamp(++index, new Timestamp(endTime.getTime()));
            db.cstmt.registerOutParameter((descriptionClobIndex = ++index), java.sql.Types.CLOB);
            db.cstmt.registerOutParameter((purposeClobIndex = ++index), java.sql.Types.CLOB);
            db.cstmt.registerOutParameter((eventIDIndex = ++index), java.sql.Types.INTEGER);
            db.cstmt.execute();

            eventId = Integer.toString(db.cstmt.getInt(eventIDIndex));

            // Now stream the description and purpose to the clob locaters
            ClobHelper.write(db.cstmt.getClob(descriptionClobIndex), this.description);
            ClobHelper.write(db.cstmt.getClob(purposeClobIndex), this.purpose);

            db.commit();

        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException e) {
            	Logger.getLogger(CalendarEvent.class).debug("Unable to roll back creation of CalendarEvent.");
                //Nothing can be done about this.
            }
            throw new PersistenceException("Error storing event", sqle);

        } catch (NumberFormatException nfe) {
            try {
                db.rollback();
            } catch (SQLException e) {
            	Logger.getLogger(CalendarEvent.class).debug("Unable to roll back creation of CalendarEvent.");
                //Nothing can be done about this.
            }
            throw new PersistenceException("ParseInt Failed in CalendarEvent.store()", ErrorLogger.HIGH);

        } finally {
            db.release();

        }
    }

    /**
     * Get the id of the space the meeting belongs to.
     *
     * Note: meeting does not need to be loaded but meeting id must be set
     */
    public String getSpaceID() throws PersistenceException {
        String spaceId = null;

        if (eventId == null)
            throw new NullPointerException("meetingId is null");

        String query = "select shc.space_id from pn_calendar_has_event che, pn_space_has_calendar shc where " +
            "che.calendar_event_id = " + eventId + " " +
            "and che.calendar_id = shc.calendar_id ";
        DBBean db = new DBBean();
        try {
            db.executeQuery(query);

            if (db.result.next()) {
                spaceId = db.result.getString("space_id");
            }

        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("failed to get event space id from database", sqle);
        } finally {
            db.release();
        }
        return spaceId;
    }

    /**
     * Load a persisted event.
     *
     * event id must be set
     */
    public void load() throws PersistenceException {
        String query = null;

        isLoaded = false;

        if (eventId == null)
            throw new NullPointerException("eventId is null");

        query =
            "select " +
            "  e.event_name, e.event_desc_clob, e.frequency_type_id, " +
            "  gd1.code_name as frequency, e.facility_id, e.event_purpose_clob, " +
            "  e.start_date, e.end_date, e.record_status " +
            "from " +
            "  pn_calendar_event e, pn_global_domain gd1 " +
            "where " +
            "  e.calendar_event_id=" + eventId +
            "  and gd1.table_name = 'pn_calendar_event' " +
            "  and gd1.column_name = 'frequency_type_id' " +
            "  and gd1.code = e.frequency_type_id";
        DBBean db = new DBBean();
        try {
            db.executeQuery(query);

            while (db.result.next()) {
                name = db.result.getString("event_name");
                description = ClobHelper.read(db.result.getClob("event_desc_clob"));
                frequency = db.result.getString("frequency");
                frequencyTypeId = db.result.getString("frequency_type_id");
                facilityId = db.result.getString("facility_id");
                if ((facilityId != null) && (facilityId.length() > 0))
                    facility = calendar.facilityFactory.make(db.result.getString("facility_id"));
                purpose = ClobHelper.read(db.result.getClob("event_purpose_clob"));
                startTime = db.result.getTimestamp("start_date");
                endTime = db.result.getTimestamp("end_date");
                setRecordStatus(RecordStatus.findByID(db.result.getString("record_status")));

                isLoaded = true;
            }
        } catch (SQLException sqle) {
            throw new PersistenceException("failed to load CalendarEvent from database", sqle);
        } finally {
            db.release();
        }
    }

    /**
     * remove a calendar event.
     * Assignments to people invited to this meeting are also removed.
     * eventId must be set before calling this method.
     * 
     */
    public void remove() throws PersistenceException {
        if (eventId == null)
            throw new NullPointerException("eventId is null.  Must be set before calling this method.");

        String query = "update pn_calendar_event set record_status='D' where calendar_event_id=" + eventId;
        DBBean db = new DBBean();
        try {
            db.setQuery(query);
            db.executeQuery();
            AssignmentManager assignmentManager = new AssignmentManager();
            String objectId = ((new Integer(eventId)).intValue()+1)+""; //ENW
            assignmentManager.setObjectID(objectId); //ENW
            //assignmentManager.setObjectID(eventId);	//PNET
            assignmentManager.loadAssigneesForObject(); //ENW
            assignmentManager.hardDeleteAssignmentsForObject();
            isLoaded = false;
            
            // TODO:  Roger -- we need to simplify the concept of invitation vs. assignment vs. attendee.  
            //   We should have a consistent process for issuing Invitations to people and converting
            //   Invitation into Assignments.  People are invited to workspaces, meetings, events, tasks(?)
            //   and upon acceptance, the Invitation is converted to an Assignment.
            //   Attendee to a meeting can be an Assigned to the Meeting.
            
            // calendar assignments are temporary for letting people know they need to accept or
            // reject and invite to a meeting.  The attendee item is a separate object, therefore
            // we can hard delete the assignment.
            
        } catch (SQLException sqle) {
        	Logger.getLogger(CalendarEvent.class).error("CalendarEvent.remove " + sqle);
            throw new PersistenceException("failed to remove calendar event", sqle);
        } finally {
            db.release();
        }
    }


    /**
     * Get Attendee status for this CalendarEvent
     * This object does not need to be loaded for this function to be called, however
     * the eventID must be set first or a NullPointerException will be thrown!
     *
     * Also, if the user is not an attendee of the meeting, the return status string will
     * be null.
     */
    public String getAttendeeStatus(String personID) {
        if (eventId == null)
            throw new NullPointerException("eventId is null");

        StringBuffer query = new StringBuffer();
        String status = null;
        query.append("select gd.code_name from pn_global_domain gd, pn_cal_event_has_attendee ceha ");
        query.append("where gd.table_name='pn_cal_event_has_attendee' ");
        query.append("and gd.column_name='status_id' ");
        query.append("and ceha.calendar_event_id=" + eventId + " and ceha.person_id=" + personID + " and ceha.status_id = gd.code ");

        DBBean db = new DBBean();
        try {
            db.setQuery(query.toString());
            db.executeQuery();
            if (db.result.next()) {
                status = PropertyProvider.get(db.result.getString(1));
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(CalendarEvent.class).error("CalendarEvent.getAttendeeStatus failed " + sqle);
        } finally {
            db.release();
        }

        if (status == null)
            status = PropertyProvider.get("prm.calendar.event.attendee.status.notinvited.name");

        return status;
    }

    /**
     * Returns NOW with seconds and milliseconds set to zero.
     * @return date time of NOW with zero seconds and milliseconds
     */
    private java.util.Date initializeDate() {
        return initializeDate(new java.util.Date());
    }

    /**
     * Returns specified date with seconds and milliseconds set to zero.
     * @return date time with zero seconds and milliseconds
     */
    private java.util.Date initializeDate(java.util.Date date) {
        Calendar calendar = new PnCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    //
    // Implementing IVCalendarPersistence
    //

    /**
     * Returns this CalendarEvent as a vCalendar with a single vEvent entity.
     * @return the VCalendar representation of this CalendarEvent
     */
    public VCalendar getVCalendar() {
        VCalendar vcal = new VCalendar();

        // Give it a name for attachment purposes
        vcal.setName(getName());

        // Specify the timezone property
        vcal.addProperty(new TimeZoneProperty(SessionManager.getUser().getTimeZone()));

        // Add the vEvent to the vCalendar
        vcal.addEntity(getVCalendarEntity());

        return vcal;
    }

    /**
     * Returns this CalendarEvent as a VCalendarEntity
     * @return the VCalendarEntity representaiton of this CalendarEvent
     */
    public net.project.calendar.vcal.VCalendarEntity getVCalendarEntity() {
        VEvent event = new VEvent();
        StringBuffer descriptionProperty = new StringBuffer();

        // Construct description from Purpose and Description
        descriptionProperty.append(getPurpose());
        if (descriptionProperty.length() > 0) {
            descriptionProperty.append('\n');
        }
        descriptionProperty.append(getDescription());

        // Add the appropriate properties to the event
        event.addProperty(new SummaryProperty(getName()));
        event.addProperty(new DescriptionProperty(descriptionProperty.toString()));
        event.addProperty(new LocationProperty(getFacility().getName()));
        event.addProperty(new StartDateTimeProperty(getStartTime(), SessionManager.getUser().getLocale(), SessionManager.getUser().getTimeZone()));
        event.addProperty(new EndDateTimeProperty(getEndTime(), SessionManager.getUser().getLocale(), SessionManager.getUser().getTimeZone()));

        // Add the attendees to the event
        addAttendees(event);

        return event;
    }

    /**
     * Adds this CalendarEvent's attendees to the vCalendar vEvent entity.
     * @param event the vEvent to add attendees to
     */
    private void addAttendees(VEvent event) {
        AttendeeBean attendee = null;
        AttendeeProperty ap = null;

        try {

            // Add all attendees
            Iterator it = getAttendees().iterator();
            while (it.hasNext()) {
                attendee = (AttendeeBean)it.next();

                // Create a new Attendee Property
                // XXX - should be based on email address?
                ap = new AttendeeProperty(attendee.getPersonName());

                // Set the Attendee status
                if (attendee.getStatusId().equals("10")) {
                    // Invited
                    ap.addParameter(new StatusParameter(StatusParameter.SENT));
                } else if (attendee.getStatusId().equals("20")) {
                    // Accepted Invitation
                    ap.addParameter(new StatusParameter(StatusParameter.ACCEPTED));
                } else if (attendee.getStatusId().equals("30")) {
                    // Declined Invitation
                    ap.addParameter(new StatusParameter(StatusParameter.DECLINED));
                } else if (attendee.getStatusId().equals("40")) {
                    // Attended
                    ap.addParameter(new StatusParameter(StatusParameter.COMPLETED));
                } else if (attendee.getStatusId().equals("50")) {
                    // Absent
                    ap.addParameter(new StatusParameter(StatusParameter.SENT));
                } else if (attendee.getStatusId().equals("60")) {
                    // Tardy
                    ap.addParameter(new StatusParameter(StatusParameter.SENT));
                }

                // Add the Attendee Property to the vEvent
                event.addProperty(ap);
            }

        } catch (net.project.calendar.vcal.VCalException vce) {
        	Logger.getLogger(CalendarEvent.class).error("CalendarEvent threw a VCalException: " + vce);

        }
    }
    
    /**
     * Get the id of the space the meeting belongs to.
     *
     * Note: meeting does not need to be loaded but meeting id must be set
     */
    public void getEventSpaceDetails() throws PersistenceException {
        if (eventId == null)
            throw new NullPointerException("meetingId is null");

        String query = "select shc.space_id, o.object_type from pn_calendar_has_event che, pn_space_has_calendar shc, pn_object o where " +
            "che.calendar_event_id = " + eventId + " " +
            "and che.calendar_id = shc.calendar_id and shc.space_id = o.object_id ";
        DBBean db = new DBBean();
        try {
            db.executeQuery(query);

            if (db.result.next()) {
                eventSpaceId = db.result.getString("space_id");
                eventSpaceType = db.result.getString("object_type");
            }

        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("failed to get event space id and space type from database", sqle);
        } finally {
            db.release();
        }
    }

	/**
	 * @return the eventSpaceId
	 */
	public String getEventSpaceId() {
		return eventSpaceId;
	}

	/**
	 * @param eventSpaceId the eventSpaceId to set
	 */
	public void setEventSpaceId(String eventSpaceId) {
		this.eventSpaceId = eventSpaceId;
	}

	/**
	 * @return the eventSpaceType
	 */
	public String getEventSpaceType() {
		return eventSpaceType;
	}

	/**
	 * @param eventSpaceType the eventSpaceType to set
	 */
	public void setEventSpaceType(String eventSpaceType) {
		this.eventSpaceType = eventSpaceType;
	}

    //
    // End of IVCalendarPersistence
    //

}  // CalendarEvent
