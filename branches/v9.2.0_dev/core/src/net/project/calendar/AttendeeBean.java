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

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

import net.project.base.ObjectType;
import net.project.base.property.PropertyProvider;
import net.project.code.TableCodeDomain;
import net.project.database.DBBean;
import net.project.notification.NotificationException;
import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentStatus;
import net.project.resource.MeetingAssignment;
import net.project.resource.Person;
import net.project.util.ErrorLogger;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * Provides presentation & persistence for event attendees.
 *
 * @author AdamKlatzkin
 * @since 03/00
 */
public class AttendeeBean implements java.io.Serializable {
    public CalendarEvent m_event = null;
    public String m_person_id = null;
    public String m_host_id = null;
    public String m_person_name = null;
    public String m_comment = null;
    public String m_status = null;
    public String m_status_id = null;
    public boolean m_isLoaded = false;
    protected MeetingAssignment assignment = null;
//    private boolean isHost = false;
    private String m_person_email = null;
    private Date m_start_date = null;
    private Date m_end_date = null;

    /**
     * @return boolean  true if the current attendee id is loaded
     */
    public boolean isLoaded() {
        return m_isLoaded;
    }

    /**
     * Return the id of the invited person.
     *
     * @return String   the attendee's person id
     * @deprecated Use {@link #getID} instead.
     */
    public String getPersonId() {
        return m_person_id;
    }

    /**
     * Return the id of the attendee.
     *
     * @return a <code>String</code> value
     */
    public String getID() {
        return m_person_id;
    }
    
    public String getHostID() {
        return m_host_id;
    }

    /**
     * Set the id of the attendee.
     *
     * @param person the attendee's person id
     * @deprecated Use {@link #setID} instead.
     */
    public void setPersonId(String person) {
        if ((m_person_id == null) || (!m_person_id.equals(person))) {
            m_isLoaded = false;
        }

        m_person_id = person;
    }

    /**
     * Set the id of the attendee
     *
     * @param id a <code>String</code> value containing the id of the person
     * that is being invited to attend an event or meeting.
     */
    public void setID(String id) {
        if ((m_person_id == null) || (!m_person_id.equals(id))) {
            m_isLoaded = false;
        }

        m_person_id = id;
    }
    
    public void setHostID(String hostID) {
        this.m_host_id = hostID;
    }

    /**
     *
     * @return String   the attendee's person name
     */
    public String getPersonName() {
        if (m_person_name == null && m_person_id != null) {
            m_person_name = Person.getDisplayNameForID(m_person_id);
        }
        return m_person_name;
    }

    /**
     *
     * @param person  the attendee's person name
     */
    public void setPersonName(String person) {
        m_person_name = person;
    }

    /**
     * Returns the email ID for the Attendee
     *
     * @return email   the attendee's email
     */
    public String getAttendeeEmail() {

        if (m_person_email == null && m_person_id != null) {
            m_person_email = Person.getEmailForID(m_person_id);
        }
        return m_person_email;
    }

    /**
     * Sets the email ID for the Attendee
     *
     * @param email   the attendee's email
     */
    public void setAttendeeEmail(String email) {
        m_person_email = email;
    }

    /**
     *
     * @return String   the id of the calendar event the attendee is assigned to
     */
    public String getEventId() {
        return m_event.getEventId();
    }

    /**
     *
     * @param event the calendar event to assign the attendee to
     */
    public void setEvent(CalendarEvent event) {
        if ((m_event == null) || (m_event != event))
            m_isLoaded = false;

        m_event = event;
    }

    /**
     *
     * @return String   attendee comment
     */
    public String getComment() {
        return m_comment;
    }

    /**
     *
     * @param comment attendee comment
     */
    public void setComment(String comment) {
        m_comment = comment;
    }

    /**
     * Gets the status id of the event attendee.
     *
     * @return String the attendee's status id
     */
    public String getStatusId() {
        return m_status_id;
    }

    /**
     * Gets the status id of the event attendee.
     *
     * @return a <code>String<code> value containing the attendee's status id
     */
    public String getStatusID() {
        return m_status_id;
    }


    /**
     * Set the status of the attendee's invitation.
     *
     * @param id the attendee's status id
     * @deprecated Use {@link #setStatusID}
     */
    public void setStatusId(String id) {
        setStatusID(id);
    }

    /**
     * Set the invitation status of the event attendee.  This is a database
     * identifier that represents values such as "Invited" and "Accepted
     * Invitation".
     *
     * @param id a <code>String</code> value indication the invitation status of
     * this attendee.
     */
    public void setStatusID(String id) {
        m_status_id = id;
    }

    /**
     * Sets the display status for this attendee.
     * This is used only for display purposes.
     * @param status the display status
     */
    void setStatus(String status) {
        m_status = status;
    }

    /**
     * Returns the status of an attendee for display.
     * This display status is not suitable for comparison operations.
     * @return the status of this attendee suitable for display
     */
    public String getStatus() {
        return m_status;
    }

    /**
     * @return String the HTML list of options for the attendee status populated
     * from the table code domain.  The attendee's current status will be selected.
     */
    public String getStatusOptionList() {
        TableCodeDomain domain = new TableCodeDomain();

        domain.setTableName("pn_cal_event_has_attendee");
        domain.setColumnName("status_id");

        domain.load();

        return domain.getOptionList(m_status);
    }

    /**
     * @return String the XML representation of the attendee
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.ensureCapacity(1000);
        xml.append("<attendee>\n");
        xml.append("<personId>" + XMLUtils.escape(m_person_id) + "</personId>\n");
        xml.append("<email>" + XMLUtils.escape(getAttendeeEmail()) + "</email>\n");
        xml.append("<name>" + XMLUtils.escape(getPersonName()) + "</name>\n");
        xml.append("<comment>" + XMLUtils.escape(m_comment) + "</comment>\n");
        xml.append("<status>" + XMLUtils.escape(m_status) + "</status>\n");
        xml.append("<statusId>" + XMLUtils.escape(m_status_id) + "</statusId>\n");
        xml.append("</attendee>\n");

        return xml.toString();
    }

    /**
     * load a persisted attendee.
     * personId and event must be set or a PersistenceException will be thrown
     */
    public void load() throws PersistenceException {
        String query = null;

        m_isLoaded = false;
        if ((m_person_id == null) || (m_event == null)) {
            throw new PersistenceException("Attendee ID must be set before load");
        }
        query = "SELECT at.attendee_comment, at.status_id, " +
            "gd.code_name as status, p.display_name as owner_name , p.email " +
            "FROM pn_cal_event_has_attendee at, pn_person p, pn_global_domain gd " +
            "WHERE at.person_id = " + m_person_id + " " +
            "and at.calendar_event_id = " + m_event.getEventId() + " " +
            "and p.person_id = " + m_person_id + " " +
            "and gd.table_name = 'pn_cal_event_has_attendee' and gd.column_name = 'status_id' " +
            "and gd.code = at.status_id ";

        DBBean db = new DBBean();
        try {
            db.setQuery(query);
            db.executeQuery();

            if (db.result.next()) {
                m_comment = db.result.getString(1);
                m_status_id = db.result.getString(2);
                m_status = PropertyProvider.get(db.result.getString(3));
                m_person_name = db.result.getString(4);
                m_person_email = db.result.getString(5);

                m_isLoaded = true;
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(AgendaBean.class).error("AttendeeBean.load failed " + sqle);
            throw new PersistenceException("failed to load attendee", sqle);
        } finally {
            db.release();
        }

    }

    /**
     * Persist an attendee.  If a user has not been saved previously, this will
     * result in an insert.  If they have been stored, it results in an update.
     */
    public void store() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            if (!m_isLoaded) {
                db.prepareStatement("INSERT INTO pn_cal_event_has_attendee " +
                    "(person_id, calendar_event_id, attendee_comment, status_id) " +
                    "VALUES (?, ?, ?, ?)");
                db.pstmt.setString(1, m_person_id);
                db.pstmt.setString(2, m_event.getEventId());
                db.pstmt.setString(3, m_comment);
                db.pstmt.setString(4, m_status_id);

                // we also want to insert the meeting in the assignment table and mark it as
                // assigned but not accepted.  When it is accepted, the status will be changed
                // in pn_cal_event_has_attendee and the assignment will be removed from pn_assignment
                //sjmittal: we always need to create an assignment entry
//                if (m_status_id.equals("10")) {
                    storeAssignment();
//                }
            } else {
                db.prepareStatement("UPDATE pn_cal_event_has_attendee SET " +
                    "attendee_comment=?,  status_id=? " +
                    "WHERE person_id=? AND calendar_event_id=?");
                db.pstmt.setString(1, m_comment);
                db.pstmt.setString(2, m_status_id);
                db.pstmt.setString(3, m_person_id);
                db.pstmt.setString(4, m_event.getEventId());

                // if the updated status is not invited (10) then remove the meeting entry
                // from the pn_assignment table
                //sjmittal: as per the decoupling of assignments from task making the behaviour
                //similer to that of schedule entry assignment.
                //currently assignment status is simply updated
//                if (!m_status_id.equals("10")) {
                    MeetingAssignment updateAssignment = new MeetingAssignment();
                    updateAssignment.setObjectID(m_event.getID());
                    updateAssignment.setPersonID(m_person_id);
                    if(AssignmentStatus.COMPLETED_PENDING.getID().equals(m_status_id)) {
                    	updateAssignment.updateStatus(m_status_id, BigDecimal.valueOf(1.00));
                    } else {
                    	updateAssignment.updateStatus(m_status_id, BigDecimal.valueOf(0.00));
                    }
//                }

            }
            db.executePrepared();
        } catch (SQLException sqle) {
            throw new PersistenceException("Error storing event attendee", sqle);
        } catch (NumberFormatException nfe) {
            throw new PersistenceException("ParseInt Failed in AttendeeBean.store()", nfe);
        } finally {
            db.release();
        }
    }

    /**
     * Remove a persisted attendee. event and personId must be set or a
     * PersistenceException will be thrown
     */
    public void remove() throws PersistenceException {

        if ((m_event == null) || (m_person_id == null)) {
            throw new PersistenceException("Agenda ID must be set before load");
        }

        DBBean db = new DBBean();
        try {
            db.prepareCall("DELETE FROM pn_cal_event_has_attendee WHERE person_id=? AND calendar_event_id = ?");
            db.cstmt.setInt(1, Integer.parseInt(m_person_id));
            db.cstmt.setInt(2, Integer.parseInt(m_event.getEventId()));
            db.executeCallable();

            // delete the assignment just in case there is one
            MeetingAssignment deleteAssignment = new MeetingAssignment();
            deleteAssignment.setObjectID(m_event.getID());
            deleteAssignment.setPersonID(m_person_id);
            deleteAssignment.delete();
        } catch (SQLException sqle) {
            throw new PersistenceException("Error removing attendee", sqle);
        } catch (NumberFormatException nfe) {
            throw new PersistenceException("ParseInt Failed in AttendeeaBean.remove()", ErrorLogger.HIGH);
        } finally {
            db.release();
        }
    }

    /**
     * update a persisted attendee item personId and event must be set or a
     * PersistenceException will be thrown
     *
     * @param setString set string to use in update query
     * ex:  "column_name_1 = value_1, ... , column_name_n = value_n"
     */
    public void update(String setString) throws PersistenceException {
        if ((m_event == null) || (m_person_id == null)) {
            throw new PersistenceException("Attendee ID must be set before load");
        }

        DBBean db = new DBBean();
        try {
            db.prepareStatement("UPDATE pn_cal_event_has_attendee SET " +
                setString +
                " WHERE person_id=? AND calendar_event_id=?");
            db.pstmt.setString(1, m_person_id);
            db.pstmt.setString(2, m_event.getEventId());
            db.executePrepared();
        } catch (SQLException sqle) {
            throw new PersistenceException("Error updating attendee", sqle);
        } catch (NumberFormatException nfe) {
            throw new PersistenceException("ParseInt Failed in AttendeeBean.update()", nfe);
        } finally {
            db.release();
        }
    }

    /**
     * reset an AgendaBean instance
     *
     * @deprecated Use the {@link #clear} method instead.
     */
    public void reset() {
        clear();
    }

    /**
     * Clear the private member variables of an attendee bean
     *
     * @since Gecko
     */
    public void clear() {
        m_person_id = null;
        m_person_name = null;
        m_event = null;
        m_comment = null;
        m_status = null;
        m_status_id = null;
        m_isLoaded = false;
//        isHost = false;
        this.assignment = null;
    }


    /**
     * Update the status of an attendee.
     */
    public void updateStatus(String status) throws PersistenceException {
//        if (!status.equals(Assignment.ASSIGNED)) {
    		MeetingAssignment updateAssignment = new MeetingAssignment();
    		updateAssignment.setObjectID(m_event.getID());
    		updateAssignment.setPersonID(m_person_id);
    		if(AssignmentStatus.COMPLETED_PENDING.getID().equals(status)) {
    			updateAssignment.updateStatus(status, BigDecimal.valueOf(1.00));
    		} else {
    			updateAssignment.updateStatus(status, BigDecimal.valueOf(0.00));
    		}
//        } else {
//            // So the status has been changed to Assigned
//            // Create a new assignment
//
//            storeAssignment();
//        }

        this.update("status_id=" + status);
    }

    /**
     * stores the (new) assignment
     *
     * @exception PersistenceException
     */
    private void storeAssignment() throws PersistenceException {

        MeetingAssignment assignment = new MeetingAssignment();
        assignment.setPersonID(m_person_id);
        assignment.setObjectID(m_event.getID());
        assignment.setStatus(AssignmentStatus.ASSIGNED);

        assignment.setSpaceID(m_event.getCalendar().getSpace().getID());
        assignment.setObjectType(ObjectType.MEETING);
        assignment.setPersonName(m_person_name);
        
        assignment.setStartTime(m_start_date);
        assignment.setEndTime(m_end_date);
        if (isHost())
            assignment.setPersonRole(AttendeeStatus.HOST.getNameToken());
        else
            assignment.setPersonRole(AttendeeStatus.INVITEE.getNameToken());
        //set the host of the meeting as assignor
        assignment.setAssignorID(m_host_id);
        assignment.setPrimaryOwner(false);
        assignment.store();

        this.assignment = assignment;
    }
    
    private boolean isHost() {
        if(m_person_id != null && m_host_id != null && m_person_id.equals(m_host_id))
            return true;
        return false;
    }

    /**
     * Notifies this attendee of their assignment to an event.
     * If the notification fails, no error is returned.
     * @throws SQLException 
     */
    protected void notifyAttendee() throws SQLException {

        MeetingNotification note = new MeetingNotification();

        try {
            note.initialize(this);

            // Attach VCalendar to notification
            net.project.calendar.vcal.VCalendar vcal = this.m_event.getVCalendar();
            note.attach(new net.project.calendar.vcal.VCalAttachment(vcal));

            // Post notification
            note.post();

        } catch (NotificationException ne) {
        	Logger.getLogger(AgendaBean.class).debug("Notification failed in AttendeeBean.notifyAttendee (Reason " +
                ne.getReasonCode() + "): " + ne);
        }

    }


	/**
	 * @param m_end_date the m_end_date to set
	 */
	public void setMEndDate(Date m_end_date) {
		this.m_end_date = m_end_date;
	}

	/**
	 * @param m_start_date the m_start_date to set
	 */
	public void setMStartDate(Date m_start_date) {
		this.m_start_date = m_start_date;
	}

} // AttendeeBean



