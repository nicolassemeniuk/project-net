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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
+--------------------------------------------------------------------------------------*/
package net.project.calendar;

import java.sql.SQLException;

import net.project.base.property.PropertyProvider;
import net.project.code.TableCodeDomain;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.database.DBExceptionFactory;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;
import net.project.resource.Roster;
import net.project.util.ErrorLogger;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * Provides presentation & persistence for Meeting agenda items.
 *
 * @author AdamKlatzkin
 * @since 03/00
 */
public class AgendaBean implements java.io.Serializable {
    private Meeting m_meeting = null;
    private String m_agenda_id = null;
    private String m_item_name = null;
    private String m_item_desc = null;
    private String m_time_alloted = null;
    private String m_status_id = null;
    private String m_status = null;
    private String m_owner_id = null;
    private String m_owner = null;
    private String m_item_sequence = null;
    private String m_meeting_notes = null;

    /**
     * @param meeting    the meeting the agenda item belongs to
     */
    public void setMeeting(Meeting meeting) {
        m_meeting = meeting;
    }

    /**
     * @return String   the agenda item's id
     */
    public String getId() {
        return m_agenda_id;
    }

    /**
     * @param id     the agenda item's id
     */
    public void setId(String id) {
        m_agenda_id = id;
    }

    /**
     * @return String   the agenda item's name
     */
    public String getName() {
        return m_item_name;
    }

    /**
     * @param name     the agenda item's name
     */
    public void setName(String name) {
        m_item_name = name;
    }

    /**
     * @return String   the agenda item's description
     */
    public String getDescription() {
        return m_item_desc;
    }

    /**
     * @param desc     the agenda item's description
     */
    public void setDescription(String desc) {
        m_item_desc = desc;
    }

    /**
     * @return String   the agenda item's alloted time
     */
    public String getAllotedTime() {
        return m_time_alloted;
    }

    /**
     * @param time   the agenda item's alloted time
     */
    public void setAllotedTime(String time) {
        m_time_alloted = time;
    }

    /**
     * @return String   the agenda item's status id
     */
    public String getStatusId() {
        return m_status_id;
    }

    /**
     * @param status     the agenda item's status id
     */
    public void setStatusId(String status) {
        m_status_id = status;
    }

    /**
     * @return String   the agenda item's status
     */
    public String getStatus() {
        return m_status;
    }

    /**
     * @param status     the agenda item's status
     */
    public void setStatus(String status) {
        m_status = status;
    }

    /**
     * @return String   the agenda item's owner id
     */
    public String getOwnerId() {
        return m_owner_id;
    }

    /**
     * @param ownerid   the agenda item's owner id
     */
    public void setOwnerId(String ownerid) {
        m_owner_id = ownerid;
    }

    /**
     * @return String   the agenda item's owner
     */
    public String getOwner() {
        return m_owner;
    }

    /**
     * @param owner   the agenda item's owner
     */
    public void setOwner(String owner) {
        m_owner = owner;
    }

    /**
     * @return String   the agenda item's sequence number
     */
    public String getItemSequence() {
        return m_item_sequence;
    }

    /**
     * @param seq   the agenda item's sequence number
     */
    public void setItemSequence(String seq) {
        m_item_sequence = seq;
    }

    /**
     * @return String   the agenda item's meeting notes
     */
    public String getMeetingNotes() {
        return m_meeting_notes;
    }

    /**
     * @param notes   the agenda item's meeting notes
     */
    public void setMeetingNotes(String notes) {
        m_meeting_notes = notes;
    }

    /**
     * @return String   the HTML list of options for the agenda status populated
     *                   from the table code domain.  The agenda's current status will be selected.
     */
    public String getAgendaStatusOptionList() {
        TableCodeDomain domain = new TableCodeDomain();

        domain.setTableName("pn_agenda_item");
        domain.setColumnName("status_id");

        domain.load();

        return domain.getOptionList(m_status_id);
    }

    /**
     * @return String   the HTML list of options for the agenda item's owner populated
     *                   from the team roster.  The agenda's current owner will be selected.
     */
    public String getOwnerOptionList() {
        StringBuffer options = new StringBuffer();

        Roster roster = new Roster();
        Person person;

        roster.setSpace(m_meeting.getCalendar().getSpace());
        roster.load();

        for (int i = 0; i < roster.size(); i++) {
            person = (Person)roster.get(i);

            if ((m_owner != null) && (m_owner.equals(person.getDisplayName())))
                options.append("<option SELECTED value=\"" + person.getID() + "\">" + person.getDisplayName() + "</option>");
            else
                options.append("<option value=\"" + person.getID() + "\">" + person.getDisplayName() + "</option>");
        }

        return options.toString();
    }

    /**
     * @return String   the HTML list of optons for agenda sequence numbers.
     *                   The agenda's current sequence number will be selected or the
     *                   greatest one of this is a new agenda item.
     */
    public String getSequenceNumberOptionList() {
        StringBuffer options = new StringBuffer();
        int nextSeq = m_meeting.getNextItemSeq();

        // If we are displaying a sequence list for an agenda item that already exists then the list
        // should not inlude the next available number
        if (m_item_sequence != null)
            nextSeq--;

        for (int i = 1; i <= nextSeq; i++) {
            String val = Integer.toString(i);
            if ((m_item_sequence != null) && (m_item_sequence.equals(val)))
                options.append("<option SELECTED value=\"" + i + "\">" + i + "</option>");
            else if ((m_item_sequence == null) && (i == nextSeq))
                options.append("<option SELECTED value=\"" + i + "\">" + i + "</option>");
            else
                options.append("<option value=\"" + i + "\">" + i + "</option>");

        }

        return options.toString();
    }

    /**
     * @return String   the XML representation of the agenda item
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<agendaItem>\n");
        xml.append("<id>" + m_agenda_id + "</id>\n");
        xml.append("<name>" + XMLUtils.escape(m_item_name) + "</name>\n");
        xml.append("<description>" + XMLUtils.escape(m_item_desc) + "</description>\n");
        xml.append("<allotedTime>" + XMLUtils.escape(m_time_alloted) + "</allotedTime>\n");
        xml.append("<status>" + XMLUtils.escape(m_status) + "</status>\n");
        xml.append("<owner>" + XMLUtils.escape(m_owner) + "</owner>\n");
        xml.append("<sequence>" + m_item_sequence + "</sequence>\n");
        xml.append("<notes>" + XMLUtils.escape(m_meeting_notes) + "</notes>\n");
        xml.append("</agendaItem>\n");

        return xml.toString();
    }

    /***************************************************************************************************
     *  Implementing IJDBCPersistence
     ***************************************************************************************************/

    /**
     * load a persisted agenda item.
     * meeting and id must be set or a PersistenceException will be thrown
     */
    public void load() throws PersistenceException {

        String query = null;

        if ((m_meeting == null) || (m_agenda_id == null)) {
            throw new PersistenceException("Agenda ID must be set before load");
        }

        query = "SELECT ai.item_name, ai.item_desc, ai.TIME_ALLOTED, ai.STATUS_ID, " +
            "gd.code_name as status, ai.owner_id, p.display_name as owner_name, " +
            "ai.ITEM_SEQUENCE, ai.meeting_notes_clob " +
            "FROM PN_AGENDA_ITEM ai, pn_person p, pn_global_domain gd " +
            "WHERE ai.meeting_id = " + m_meeting.getID() + " " +
            "and ai.agenda_item_id = " + m_agenda_id + " " +
            "and ai.record_status = 'A' and p.person_id = ai.owner_id " +
            "and gd.table_name = 'pn_agenda_item' and gd.column_name = 'status_id' " +
            "and gd.code = ai.status_id " +
            "order by ai.item_sequence asc";

        DBBean db = new DBBean();

        try {
            db.setQuery(query);
            db.executeQuery();

            if (db.result.next()) {
                m_item_name = db.result.getString(1);
                m_item_desc = db.result.getString(2);
                m_time_alloted = db.result.getString(3);
                m_status_id = db.result.getString(4);
                m_status = PropertyProvider.get(db.result.getString(5));
                m_owner_id = db.result.getString(6);
                m_owner = db.result.getString(7);
                m_item_sequence = db.result.getString(8);
                m_meeting_notes = ClobHelper.read(db.result.getClob(9));
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(AgendaBean.class).error("AgendaBean.load failed " + sqle);
            throw new PersistenceException("failed to load agenda item: " + sqle, sqle);

        } finally {
            db.release();

        }
    }

    /**
     * Persist an agenda item.
     */
    public void store() throws PersistenceException {

        DBBean db = new DBBean();

        try {
            int index = 0;
            int meetingNotesClobIndex = 0;

            db.setAutoCommit(false);
            db.prepareCall("{call CALENDAR.STORE_AGENDA_ITEM(?,?,?,?,?,?,?,?,?,?, ?)}");
            db.cstmt.setInt(++index, Integer.parseInt(m_owner_id));
            db.cstmt.setInt(++index, Integer.parseInt(m_meeting.getID()));
            if (m_agenda_id == null) {
                db.cstmt.setNull(++index, java.sql.Types.INTEGER);
            } else {
                db.cstmt.setInt(++index, Integer.parseInt(m_agenda_id));
            }

            db.cstmt.setString(++index, m_item_name);
            db.cstmt.setString(++index, m_item_desc);
            db.cstmt.setString(++index, m_time_alloted);
            db.cstmt.setInt(++index, Integer.parseInt(m_status_id));
            db.cstmt.setInt(++index, Integer.parseInt(m_owner_id));
            db.cstmt.setInt(++index, Integer.parseInt(m_item_sequence));
            db.cstmt.setInt(++index, (m_meeting_notes == null ? 1 : 0));
            db.cstmt.registerOutParameter((meetingNotesClobIndex = ++index), java.sql.Types.CLOB);
            db.executeCallable();

            if (m_meeting_notes != null) {
                ClobHelper.write(db.cstmt.getClob(meetingNotesClobIndex), m_meeting_notes);
            }

            db.commit();

        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException e) {
                // Throw original error and release
            }
            throw new PersistenceException("Error storing agenda: " + sqle, sqle);

        } finally {
            db.release();

        }
    }

    /**
     * remove a persisted agenda item.
     * meeting and id must be set or a PersistenceException will be thrown
     */
    public void remove() throws PersistenceException {
        int statusId = 0;

        if ((m_meeting == null) || (m_agenda_id == null)) {
            throw new PersistenceException("Agenda ID must be set before load");
        }

        DBBean db = new DBBean();
        try {
            db.prepareCall("begin CALENDAR.REMOVE_AGENDA_ITEM(?,?,?); end;");
            db.cstmt.setInt(1, Integer.parseInt(m_agenda_id));
            db.cstmt.setInt(2, Integer.parseInt(m_meeting.getID()));
            db.cstmt.registerOutParameter(3, java.sql.Types.INTEGER);
            db.executeCallable();
            statusId = db.cstmt.getInt(3);
        } catch (SQLException sqle) {
            throw new PersistenceException("Error storing agenda.", sqle);
        } catch (NumberFormatException nfe) {
        	Logger.getLogger(AgendaBean.class).error("ParseInt Failed in AgendaBean.store()" + nfe);
            throw new PersistenceException("ParseInt Failed in AgendaBean.store().", ErrorLogger.HIGH);
        } finally {
            try {
                DBExceptionFactory.getException("AgendaBean.remove()", statusId);
            } catch (net.project.base.PnetException pe) {
            	Logger.getLogger(AgendaBean.class).error("AgendaBean.remove() threw an exception;" + pe);
                throw new PersistenceException("AgendaBean.remove() threw an exception; " + pe, pe);
            }
            db.release();
        }
    }

    /**
     * update a persisted agenda item
     * meeting and id must be set or a PersistenceException will be thrown
     *
     * @param setString  set string to use in update query
     *                   ex:  "column_name_1 = value_1, ... , column_name_n = value_n"
     */
    public void update(String setString) throws PersistenceException {
        if ((m_meeting == null) || (m_agenda_id == null)) {
        	Logger.getLogger(AgendaBean.class).error("Agenda ID must be set before load.  Persistence Exception thrown");
            throw new PersistenceException("Agenda ID must be set before load");
        }

        DBBean db = new DBBean();
        try {
            db.prepareStatement("UPDATE pn_agenda_item SET " +
                setString +
                " WHERE meeting_id=? AND agenda_item_id=?");
            db.pstmt.setString(1, m_meeting.getID());
            db.pstmt.setString(2, m_agenda_id);

            db.executePrepared();
        } catch (SQLException sqle) {
            throw new PersistenceException("Error updating agenda item.", sqle);
        } catch (NumberFormatException nfe) {
        	Logger.getLogger(AgendaBean.class).error("ParseInt Failed in AgendaBean.update().  Persistence Exception thrown");
            throw new PersistenceException("ParseInt Failed in AgendaBean.update().", ErrorLogger.HIGH);
        } finally {
            db.release();
        }

    }

    /**
     * reset an AgendaBean instance
     */
    public void reset() {
        m_meeting = null;
        m_agenda_id = null;
        m_item_name = null;
        m_item_desc = null;
        m_time_alloted = null;
        m_status_id = null;
        m_status = null;
        m_owner_id = null;
        m_owner = null;
        m_item_sequence = null;
        m_meeting_notes = null;
    }

} // AgendaBean


