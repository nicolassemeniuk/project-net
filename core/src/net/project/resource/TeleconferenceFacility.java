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
 * TeleconferenceFacility
 *
 * @author AdamKlatzkin  03/00
 * @author RogerBly
 * @author Tim Morrow 07/01
 */
public class TeleconferenceFacility extends Facility {

    /**
     * This facility's name.
     *
     * @deprecated This constant has been deprecated as of Gecko 4 for
     * internationalization.
     */
    public static final String NAME = "Teleconference";

    protected boolean isBridge = false;
    protected String phoneNumber = null;
    protected String password = null;
    protected String ownerId = null;
    private DBBean db = new DBBean();

    /**
     * Creates a new TeleconferenceFacility.  This is of type
     * {@link FacilityType#TELECOM}.
     */
    public TeleconferenceFacility() {
        super();
        setName(PropertyProvider.get("prm.directory.resource.teleconferencefacility.name"));
        setType(FacilityType.TELECOM);
    }

    /**
     * Clear the information stored internally in this object regarding the
     * facility.
     */
    public void reset() {
        isBridge = false;
        phoneNumber = null;
        password = null;
    }

    /**
     * Sets the id of the facility owner.
     *
     * @param ownerId    person id of the facility owner
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * Gets the id of the facility owner.
     *
     * @return String person id of the facility owner
     */
    public String getOwnerId() {
        return this.ownerId;
    }

    /** Set whether this is a conference call bridge that all must call into. */
    public void setIsBridge(boolean isBridge) {
        this.isBridge = isBridge;
    }

    /** Get whether this is a conference call bridge that all must call into. */
    public boolean getisBridge() {
        return isBridge;
    }

    /** Sets the room number for this teleconference.  */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /** Get the phone number for this teleconference.  */
    public String getPhoneNumberNumber() {
        return phoneNumber;
    }

    /** Sets the password for this teleconference.  */
    public void setBridgePassword(String password) {
        this.password = password;
    }

    /** Get the password for this teleconference.  */
    public String getBridgePassword() {
        return password;
    }

    /**
     * Converts the object to XML representation without the XML version tag.
     * This method returns the object as XML text.
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
        /* xml.append("<roomName>" + XMLUtils.escape(roomName) + "</roomName>\n");
         xml.append("<roomNumber>" + XMLUtils.escape(roomNumber) + "</roomNumber>\n");
         xml.append("<building>" + XMLUtils.escape(building) + "</building>\n");
         xml.append("<floor>" + XMLUtils.escape(floor) + "</floor>\n");
         xml.append("<campus>" + XMLUtils.escape(campus) + "</campus>\n");
         xml.append("<addressId>" + addressId + "</addressId>\n");*/
        xml.append("</facility>\n");

        return xml.toString();
    }

    /**
     * Converts the object to XML representation.  This method returns the
     * object as XML text.
     *
     * @return XML representation
     */
    public String getXML() {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }

    /**
     * Load a facility from the database.  {@link #setID} must be called prior
     * to calling this method.
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
            throw new PersistenceException("There was an error loading the Teleconference Facility Data", sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Delete a facility from the database.  {@link #setID} must be called prior
     * to calling this method.
     */
    public void remove() {
        String query = null;

        if (id == null)
            return;

        try {
            query = "DELETE FROM pn_facility " +
                "WHERE facility_id = " + id + " ";
            db.executeQuery(query);

            query = "DELETE FROM pn_object " +
                "WHERE object_id = " + id + " ";
            db.executeQuery(query);
        } catch (SQLException sqle) {
        	Logger.getLogger(TeleconferenceFacility.class).error("TeleconferenceFacility.remove() threw an SQL exception: ");
        } finally {
            db.release();
        }
    }
}
