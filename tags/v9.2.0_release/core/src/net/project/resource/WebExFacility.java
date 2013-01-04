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
| Facilities will not be fully implemented in V1 but the structure will be set forth
| for an easy V2 implementation
+--------------------------------------------------------------------------------------*/
package net.project.resource;

import java.sql.SQLException;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.ObjectManager;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * WebExFacility
 *
 * @author AdamKlatzkin  03/00
 * @author Tim Morrow 07/01
 */
public class WebExFacility
    extends TeleconferenceFacility {

    /** This facility's name. */
    public static final String NAME = "WebEx";
    public static final String SECURE = "secure";
    protected String roomNumber = null;
    protected String roomName = null;
    protected String wxPassword = null;
    protected String ownerId = null;
    protected DBBean m_db = new DBBean();

    /**
     * Creates a new WebExFacility. This is of type {@link FacilityType#WEBEX}.
     */
    public WebExFacility() {
        super();
        setName(PropertyProvider.get("prm.directory.resource.webexfacility.name"));
        setType(FacilityType.WEBEX);
    }

    /**
     * Reset the facilty
     */
    public void reset() {
        roomNumber = null;
        wxPassword = null;
        ownerId = null;
        super.reset();
    }

    /**
     * Sets the id of the facility owner.
     *
     * @param ownerId person id of the facility owner
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * Gets the id of the facility owner.
     *
     * @return String    person id of the facility owner
     */
    public String getOwnerId() {
        return this.ownerId;
    }

    /**
     * Sets WebEx room number for this meeting.
     *
     * @param webExRoomNumber the room number
     */
    public void setRoomNumber(String webExRoomNumber) {
        this.roomNumber = webExRoomNumber;
    }

    /**
     * getRoomNumber Gets WebEx room number for this meeting.
     *
     * @return String    the room number
     */
    public String getRoomNumber() {
        return roomNumber;
    }

    /**
     * Sets WebEx room name for this meeting. - really used to set the meeting
     * to secure or not secure.
     *
     * @param webExRoomName the room name
     */
    public void setRoomName(String webExRoomName) {
        this.roomName = webExRoomName;
    }

    /**
     * Gets WebEx room name for this meeting.- this field is really used to see
     * if the meeting is secure or not.
     *
     * @return String    the room number
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * Gets WebEx room name for this meeting.- this field is really used to see
     * if the meeting is secure or not.
     *
     * @return Boolean - is the room secure or not
     */
    public boolean isSecure() {

        boolean retvalue = false;
        if (roomName != null) {

            if (roomName.equals(SECURE)) {
                retvalue = true;
            }
        }
        return retvalue;
    }
    
    /**
     * Sets the password for this WebEx meeting.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.wxPassword = password;
    }


    /**
     * Get the password for this WebEx meeting.
     *
     * @return String    the room number
     */
    public String getPassword() {
        return wxPassword;
    }

    /**
     * Converts the object to XML representation without the XML
     * version tag. This method returns the object as XML text.
     *
     * @return XML representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<facility>\n");
        xml.append("<id>" + id + "</id>\n");
        xml.append("<name>" + XMLUtils.escape(getName()) + "</name>\n");
        xml.append("<description>" + XMLUtils.escape(getDescription()) + "</description>\n");
        xml.append("<type>" + XMLUtils.escape(getType().getID()) + "</type>\n");
        xml.append("<roomNumber>" + XMLUtils.escape(roomNumber) + "</roomNumber>\n");
        xml.append("<roomName>" + XMLUtils.escape(roomName) + "</roomName>\n");
        xml.append("<roomPassword>" + XMLUtils.escape(wxPassword) + "</roomPassword>\n");
        xml.append("<host>" + XMLUtils.escape(ownerId) + "</host>");
        xml.append("</facility>\n");

        return xml.toString();
    }


    /**
     * getXML Converts the object to XML representation. This method returns the
     * object as XML text.
     *
     * @return XML representation
     */
    public String getXML() {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }

    /**
     * Persist the facility.
     */
    public void store() throws PersistenceException {

        try {

            if (id == null) {
                id = ObjectManager.dbCreateObject("facility", "A");

                m_db.prepareStatement("INSERT INTO pn_facility " +
                    "(facility_id, name, description, facility_type, room_number, password) " +
                    "VALUES (?, ?, ?, ?, ?, ?)");
                m_db.pstmt.setString(1, id);
                m_db.pstmt.setString(2, getName());
                m_db.pstmt.setString(3, getDescription());
                m_db.pstmt.setInt(4, Integer.parseInt(getType().getID()));
                m_db.pstmt.setString(5, roomNumber);
                m_db.pstmt.setString(6, wxPassword);
            } else {
                m_db.prepareStatement("UPDATE pn_facility " +
                    "SET name=?, description=?, facility_type=?, room_number=?, password=?, room_name=? " +
                    "WHERE facility_id=?");

                m_db.pstmt.setString(1, getName());
                m_db.pstmt.setString(2, getDescription());
                m_db.pstmt.setInt(3, Integer.parseInt(getType().getID()));
                m_db.pstmt.setString(4, roomNumber);
                m_db.pstmt.setString(5, wxPassword);
                m_db.pstmt.setString(6, roomName);
                m_db.pstmt.setString(7, id);

            }

            m_db.executePrepared();
        } catch (SQLException sqle) {
            throw new PersistenceException("Error storing webex facility", sqle);
        } catch (NumberFormatException nfe) {
            throw new PersistenceException("ParseInt Failed in WebExFacility.store()", nfe);
        } finally {
            m_db.release();
        }
    }

    /**
     * load load a persisted facility. id must be set
     */
    public void load() throws PersistenceException {

        String query = null;

        if (id == null) {
            throw new PersistenceException("facility ID must be set before load");
        }

        query = "SELECT f.name, f.description, " +
            "f.room_number, f.password, o.created_by, f.room_name " +
            "FROM pn_facility f, pn_object o " +
            "WHERE f.facility_id = " + id + " " +
            "and o.object_id = " + id + " ";

        try {
            m_db.setQuery(query);
            m_db.executeQuery();

            if (m_db.result.next()) {

                name = m_db.result.getString(1);
                description = m_db.result.getString(2);
                roomNumber = m_db.result.getString(3);
                wxPassword = m_db.result.getString(4);
                ownerId = m_db.result.getString(5);
                roomName = m_db.result.getString(6);
            }
        } catch (SQLException sqle) {
            throw new PersistenceException("Failed to load webex facility: " + sqle);
        } finally {
            m_db.release();
        }
    }

    /**
     * Remove a persisted facility. id must be set.
     */
    public void remove() {

        String query = null;

        if (id == null) {
            return;
        }

        query = "DELETE FROM pn_facility " +
            "WHERE facility_id = " + id + " ";
        try {
            m_db.setQuery(query);
            m_db.executeQuery();
        } catch (SQLException sqle) {
        	Logger.getLogger(WebExFacility.class).debug("Failed to remove webex facility: " + sqle);
        } finally {
            m_db.release();
        }

        query = "DELETE FROM pn_object " +
            "WHERE object_id = " + id + " ";
        try {
            m_db.setQuery(query);
            m_db.executeQuery();
        } catch (SQLException sqle) {
        	Logger.getLogger(WebExFacility.class).debug("Failed to remove webex facility: " + sqle);
        } finally {
            m_db.release();
        }

    }

}  // WebExFacility
