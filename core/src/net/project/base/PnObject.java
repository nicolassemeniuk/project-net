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

 package net.project.base;

import java.io.Serializable;
import java.sql.SQLException;

import net.project.database.DBBean;

import org.apache.log4j.Logger;

/**
 * Provides a mechanism for determining the type of an object
 * for a particular ID.
 */
public class PnObject implements Serializable, Identifiable {

    /** The unique ID of this object. */
    private String objectID = null;

    /** The string representation of the type of this object. */
    private String type = null;

    /**
     * Sets the ID of the object, required before loading.
     * @param objectID the ID of the object to load
     * @see #getID
     */
    public void setID(String objectID) {
        this.objectID = objectID;
    }

    /**
     * Returns the ID of the object.
     * @return the object ID
     * @see #setID
     */
    public String getID() {
        return objectID;
    }

    /**
     * Returns the type of this object as a string.
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the <code>ObjectType</code> for this object.
     * @return the ObjectType loaded from persistence.
     */
    public ObjectType getObjectType() {
        ObjectType oType = new ObjectType();
        oType.setType(getType());
        oType.load();
        return oType;
    }

    /**
     * Loads this object from persistence.
     * ID is required to bet set.
     */
    public void load() {
        String query = "select object_id, object_type, created_by from pn_object where object_id=" + getID();

        DBBean db = new DBBean();

        try {
            db.setQuery(query);
            db.executeQuery();

            if (db.result.next()) {
                this.objectID = db.result.getString("object_id");
                this.type = db.result.getString("object_type");
            }

            getObjectType();
        } catch (SQLException sqle) {
        	Logger.getLogger(PnObject.class).error("PnObject.load(): SQL Exception" + sqle);
        } finally {
            db.release();
        }

    }

    public void store(DBBean db) throws SQLException {
        throw new PnetRuntimeException("This method has not been implemented");
    }

}
