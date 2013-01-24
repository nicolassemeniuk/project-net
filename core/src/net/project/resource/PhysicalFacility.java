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
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * A Physical Facility represents a physical location.
 *
 * @author AdamKlatzkin  03/00
 * @author RogerBly
 * @author Tim Morrow 07/01
 */
public class PhysicalFacility extends Facility {
    /**
     * This facility's name.
     * @deprecated This constant has been deprecated as of Gecko 4 for
     * internationalization.
     */
    public static final String NAME = "Physical";

    protected String roomName = null;
    protected String roomNumber = null;
    protected String building = null;
    protected String floor = null;
    protected String campus = null;
    protected String addressId = null;
    protected String ownerId = null;
    protected Address address = null;

    private DBBean db = new DBBean();

    /**
     * Reset all values stored internally to their defaults.
     */
    public void reset() {
        roomName = null;
        roomNumber = null;
        building = null;
        floor = null;
        campus = null;
        addressId = null;
        address = null;
        ownerId = null;
        super.reset();
    }

    /**
     * Creates a new Physical Facility.  This is a Facility of type
     * {@link FacilityType#PHYSICAL}.
     */
    public PhysicalFacility() {
        super();
        setType(FacilityType.PHYSICAL);
        setName(PropertyProvider.get("prm.directory.resource.physicalfacility.name"));
    }

    /**
     * Sets the id of the facility owner.
     * @param ownerId    person id of the facility owner
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * Gets the id of the facility owner.
     *
     * @return a <code>String</code> containing person id of the facility owner.
     */
    public String getOwnerId() {
        return this.ownerId;
    }

    /** Sets the room name of this facility. */
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    /** Get the room name of this facility. */
    public String getRoomName() {
        return roomName;
    }

    /** Sets the room number for this facility.  */
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    /** Get the room number for this facility.  */
    public String getRoomNumber() {
        return roomNumber;
    }

    /** Sets the building for this facility.  */
    public void setBuilding(String building) {
        this.building = building;
    }

    /** Get the building for this facility.  */
    public String getBuilding() {
        return building;
    }

    /** Sets the floor for this facility.  */
    public void setFloor(String floor) {
        this.floor = floor;
    }

    /** Get the floor for this facility.  */
    public String getFloor() {
        return floor;
    }

    /** Sets the campus for this facility.  */
    public void setCampus(String campus) {
        this.campus = campus;
    }

    /** Get the campus for this facility.  */
    public String getCampus() {
        return campus;
    }

    /**
     * Get the room for this facility.
     *
     * @return the name and number of the room as a concatenated string.   If
     * either the name or the number don't exist, the String is properly
     * formatted.
     */
    public String getRoom() {
        if ((roomName != null) && (roomNumber != null))
            return roomName + " (" + roomNumber + ")";
        else if (roomName != null)
            return roomName;
        else if (roomNumber != null)
            return roomNumber;
        else
            return null;
    }


    /**
     * Get the Address for this facility.
     *
     * @return an Address that has been populated from persistence.
     */
    public Address getAddress() {
        try {
            Address lAddress = new Address();
            lAddress.setID(addressId);
            lAddress.load();
            return lAddress;
        } catch (net.project.persistence.PersistenceException pe) {
            return null;
        }
    }



    /**
     * Converts the object to XML representation without the XML version tag.
     * This method returns the object as XML text.
     *
     * @return XML representation of this object.
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<facility>\n");
        xml.append("<id>" + XMLUtils.escape(id) + "</id>\n");
        xml.append("<name>" + XMLUtils.escape(name) + "</name>\n");
        xml.append("<description>" + XMLUtils.escape(description) + "</description>\n");
        xml.append("<type>" + XMLUtils.escape(type.getID()) + "</type>\n");
        xml.append("<roomName>" + XMLUtils.escape(roomName) + "</roomName>\n");
        xml.append("<roomNumber>" + XMLUtils.escape(roomNumber) + "</roomNumber>\n");
        xml.append("<building>" + XMLUtils.escape(building) + "</building>\n");
        xml.append("<floor>" + XMLUtils.escape(floor) + "</floor>\n");
        xml.append("<campus>" + XMLUtils.escape(campus) + "</campus>\n");
        xml.append("<addressId>" + XMLUtils.escape(addressId) + "</addressId>\n");
        xml.append("</facility>\n");

        return xml.toString();
    }


    /**
     * Converts the object to XML representation.  This method returns the
     * object as XML text.
     *
     * @return XML representation of this object.
     */
    public String getXML() {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }

    /**
     * Load a persisted physical facility from the database.  {@link #setID}
     * must be called prior to calling this method.
     */
    public void load() throws PersistenceException {

        String query = null;

        if (id == null) {
            throw new PersistenceException("facility ID must be set before load");
        }

        query = "SELECT f.name, f.description " +
            "FROM pn_facility f " +
            "WHERE f.facility_id = " + id;

        try {
            db.executeQuery(query);

            if (db.result.next()) {
                name = db.result.getString("name");
                description = db.result.getString("description");
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(PhysicalFacility.class).error("PhysicalFacility.java: SQL Error in load. ");
            throw new PersistenceException("Error loading Physical Facility", sqle);
        } finally {
            db.release();
        }
    }


    /**
     * Removes a persisted physical facility from the database.  This method
     * does an actual delete, not a soft-delete as is performed in many other
     * parts of the code.  {@link #setID} must be called prior to calling this
     * method.
     */
    public void remove() {
        String query = null;

        if (id == null)
            return;

        try {
            query = "DELETE FROM pn_facility WHERE facility_id = " + id + " ";
            db.executeQuery(query);

            query = "DELETE FROM pn_object WHERE object_id = " + id + " ";
            db.executeQuery(query);
        } catch (SQLException sqle) {
        	Logger.getLogger(PhysicalFacility.class).error("PhysicalFacility.java: SQL Error in remove. ");
        } finally {
            db.release();
        }
    }
}
