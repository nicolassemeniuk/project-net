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
|   $Revision: 19582 $
|       $Date: 2009-07-27 12:04:13 -0300 (lun, 27 jul 2009) $
|     $Author: ritesh $
|
| Facilities will not be fully implemented in V1 but the structure will be set forth
| for an easy V2 implementation
+--------------------------------------------------------------------------------------*/
package net.project.resource;

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.database.ObjectManager;
import net.project.persistence.PersistenceException;
import net.project.util.Validator;

/**
 * Abstract class for facilities.
 * Facilities my be physical places such as meeting rooms or
 * virtual place such as ATT phone bridge conference number and password or a WebEx meeting.
 *
 * @author RogerBly
 * @author AdamKlatzkin  03/00
 * @author Tim Morrow
 */
public abstract class Facility implements java.io.Serializable, IFacility {
    protected String name = null;
    protected String description = null;
    protected FacilityType type = null;
    protected String id = null;

    /**
     * Reset the event data structure.
     */
    public void reset() {
        name = null;
        description = null;
        type = null;
        id = null;
    }

    /**
     * Sets the database id of this facility.
     *
     * @deprecated please use {@link #setID} instead.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the database id of this facility.
     *
     * @deprecated Please use {@link #getID} instead.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the database id of this facility.
     */
    public void setID(String id) {
        this.id = id;
    }

    /**
     * Gets the database id of this facility.
     */
    public String getID() {
        return id;
    }

    /**
     * Sets the name of this facility.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the name of this facility.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the description of this facility.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the description of this facility.
     */
    public String getDescription() {
        return description == null ? "" : description;
    }


    /**
     * Sets the type of this facility.
     */
    public void setType(FacilityType type) {
        this.type = type;
    }


    /**
     * Gets the type of this facility.
     */
    public FacilityType getType() {
        return type;
    }

    /**
     * Persist the facility to the database.
     */
    public void store() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            store(db);
        } catch (SQLException sqle) {
            throw new PersistenceException("Unexpected error while storing facility.", sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Transaction safe version of the store method.  This method ensures that
     * any transaction in progress at the time this method is called will be
     * preserved, and will not be committed or rolled back.
     *
     * @param db a <code>DBBean</code> object which we will use to store this
     * facility.
     */
    public void store(DBBean db) throws SQLException {
        if (Validator.isBlankOrNull(id)) {
            ObjectManager om = new ObjectManager();
            id = om.dbCreateObject(db, "facility", "A");

            db.prepareStatement(
                "insert into pn_facility " +
                "  (facility_id, name, description, facility_type) " +
                "values " +
                "  (?,?,?,?)");
            db.pstmt.setString(1, id);
            db.pstmt.setString(2, getName());
            db.pstmt.setString(3, getDescription());
            db.pstmt.setString(4, getType().getID());
            db.executePrepared();
        } else {
            db.prepareStatement(
                "update pn_facility " +
                "set " +
                "  name = ?, " +
                "  description = ?, " +
                "  facility_type = ? " +
                "where" +
                "  facility_id = ?"
            );
            db.pstmt.setString(1, getName());
            db.pstmt.setString(2, getDescription());
            db.pstmt.setString(3, getType().getID());
            db.pstmt.setString(4, id);
            db.executePrepared();
        }
    }
}
