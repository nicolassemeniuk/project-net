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
+--------------------------------------------------------------------------------------*/
package net.project.resource;

import java.sql.SQLException;
import java.util.ArrayList;

import net.project.database.DBBean;

import org.apache.log4j.Logger;


/**
 * FacilityFactory
 * Create facility objects from object id's
 *
 * @author AdamKlatzkin  03/00                                                      
 * @author Tim Morrow 07/01
 */
public class FacilityFactory
        implements java.io.Serializable {
    
    // *************************************************************
    // PRIVATE members
    // *************************************************************
    private String              facilityId              = null;
    private String              addressId               = null;
    private String              name                    = null;
    private String              description             = null;
    private FacilityType        facilityType            = null;
    private String              roomName                = null;
    private String              roomNumber              = null;
    private String              building                = null;
    private String              floor                   = null;
    private String              campus                  = null;
    private String              phoneNumber             = null;
    private String              password                = null;
    private String              ownerId                 = null;
    private boolean             isBridge                = false;

    /**
     * Creates a new facility object corresponding to the specified type.
     * @param facilityType the facility type
     */
    public static IFacility createNew(FacilityType facilityType) {
        if (facilityType.equals(FacilityType.PHYSICAL)) {
            return new PhysicalFacility();
        } else if (facilityType.equals(FacilityType.TELECOM)) {
            return new TeleconferenceFacility();
        } else if (facilityType.equals(FacilityType.WEBEX)) {
            return new WebExFacility();
        }
        return null;
    }

    /**
     * Creates a new facility object corresponding to the type with the
     * specified id.
     * Convenience method which is the same as calling
     * <code>createNew(FacilityType.forID(facilityTypeID))</code>
     * @param facilityTypeID the id of the facility type
     */
    public static IFacility createNew(String facilityTypeID) {
        return FacilityFactory.createNew(FacilityType.forID(facilityTypeID));
    }

    /*---------------------------------------------------------------------------------**//**
    * make
    * Returns a new facility of the proper type for the given facilityId.  
    * The new facility is restored from the persistence store.
    * @return a PhyscialFacility, TeleconferenceFacility, WebExFacility, etc.*
    +---------------+---------------+---------------+---------------+---------------+------*/
    public synchronized IFacility make(String facilityId) {
        String query;

        if (facilityId == null)
            return null;

        query =  "select created_by, address_id, name, description, facility_type, room_name, room_number, building, floor, campus, is_bridge, phone_number, password, facility_url " +
                 "from pn_facility, pn_object where facility_id=" + facilityId + " and object_id=" + facilityId;

        DBBean db = new DBBean();
        try {
            db.setQuery(query);
            db.executeQuery();

            if (!db.result.next())
                return null;

            this.facilityId = facilityId;
            ownerId = db.result.getString(1);
            addressId = db.result.getString(2);
            name = db.result.getString(3);
            description = db.result.getString(4);
            facilityType = FacilityType.forID(db.result.getString(5));
            roomName = db.result.getString(6);
            roomNumber = db.result.getString(7);
            building = db.result.getString(8);
            floor = db.result.getString(9);
            campus = db.result.getString(10);
            isBridge = db.result.getBoolean(11);
            phoneNumber = db.result.getString(12);
            password = db.result.getString(13);
        } catch (SQLException sqle) {
        	Logger.getLogger(FacilityFactory.class).error("FacilityFactory.make failed " + sqle);
        } finally {
            db.release();
        }

        if (facilityType.equals(FacilityType.PHYSICAL))
            return makePhysicalFacility();
        else if (facilityType.equals(FacilityType.TELECOM))
            return makeTeleconferenceFacility();
        else if (facilityType.equals(FacilityType.WEBEX))
            return makeWebExFacility();
        else
            return null;
    }

    /*---------------------------------------------------------------------------------**//**
    * getFacilities
    * get the facilities assigned to a specified space
    *
    * @param spaceId    the space id
    * @return ArrayList contains IFacility objects assigned to the space
    +---------------+---------------+---------------+---------------+---------------+------*/
    public ArrayList getFacilities(String spaceId) {
        String query;

        query =  "select shf.facility_id, f.facility_name " +
                 "from pn_facility f, pn_space_has_facility shf " +
                 "where shf.space_id=" + spaceId + " " +
                 "AND shf.display_in_menu = 0 " +
                 "AND shf.facility_id+=f.facility_id " +
                 "AND f.facility_type = 10"; // restrict to physical facilities

        ArrayList facilities = new ArrayList();
        DBBean db = new DBBean();
        try {
            db.setQuery(query);
            db.executeQuery();      

            while (db.result.next()) {
                PhysicalFacility pf = new PhysicalFacility();
                pf.id = db.result.getString(1);
                pf.name = db.result.getString(2);
                facilities.add(pf);
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(FacilityFactory.class).error("FacilityFactory.getFacilities failed " + sqle);
        } finally {
            db.release();
        }

        return facilities;
    }

    /*---------------------------------------------------------------------------------**//**
    * makePhysicalFacility
    +---------------+---------------+---------------+---------------+---------------+------*/
    private PhysicalFacility makePhysicalFacility() {
        PhysicalFacility facility = new PhysicalFacility();
        facility.id = this.facilityId;
        facility.addressId = this.addressId ;
        facility.name = this.name;
        facility.description = this.description;
        facility.roomName = this.roomName;
        facility.roomNumber = this.roomNumber;
        facility.building = this.building;
        facility.floor = this.floor;
        facility.campus = this.campus;

        return facility;
    }

    /*---------------------------------------------------------------------------------**//**
    * makeTeleconferenceFacility
    +---------------+---------------+---------------+---------------+---------------+------*/
    private TeleconferenceFacility makeTeleconferenceFacility() {
        TeleconferenceFacility facility = new TeleconferenceFacility();
        facility.id = this.facilityId;
        facility.name = this.name;
        facility.description = this.description;
        facility.isBridge = this.isBridge;
        facility.phoneNumber = this.phoneNumber;
        facility.password = this.password;

        return facility;
    }

    /*---------------------------------------------------------------------------------**//**
    * makeWebExFacility
    +---------------+---------------+---------------+---------------+---------------+------*/
    private WebExFacility makeWebExFacility() {
        WebExFacility facility = new WebExFacility();
        facility.id = this.facilityId;
        facility.name = this.name;
        facility.description = this.description;
        facility.wxPassword = this.password;
        facility.roomNumber = this.roomNumber;
        facility.ownerId = this.ownerId;

        return facility;
    }

}  // FacilityFactory
