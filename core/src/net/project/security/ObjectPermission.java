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

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.security;

import java.io.Serializable;
import java.sql.SQLException;

import net.project.base.PnObject;
import net.project.database.DBBean;
import net.project.space.Space;

import org.apache.log4j.Logger;

/**
 * A set of actions which are permitted on an Object.
 *
 * @author Roger Bly
 * @since 2/19/2000
 */
public class ObjectPermission extends Permission implements Serializable {
    /** The project.net Module this permission applies to. */
    private PnObject object = null;

    /** The Space context for this permission. */
    protected Space m_space = null;

    /**
     * Set the Object context for this permission.
     */
    public void setObject(PnObject object) {
        this.object = object;
    }

    /**
     * Get the Object context for this permission.
     */
    public PnObject getObject() {
        return object;
    }


    /**
     * Set the Space context for this permission.
     */
    public void setSpace(Space space) {
        m_space = space;
    }

    /**
     * Get the Space context for this permission.
     */
    public Space getSpace() {
        return m_space;
    }

    /**
     * Returns an XML representation of the object.
     *
     * @return XML representation of the Action.
     */
    public String getXML() {
        StringBuffer xml = new StringBuffer();
        return xml.toString();
    }


    /**
     * Get the default permission for the current object's type in the current
     * Space.
     */
    public String getDefaultPermission() {
        String returnVal = null;
        String query;

        query = "SELECT actions " +
            "FROM pn_default_object_permission " +
            "WHERE space_id = " + m_space.getID() +
            " AND object_type = '" + object.getType() + "'" +
            " AND group_id = " + getGroup().getID();

        DBBean db = new DBBean();
        try {
            db.setQuery(query);
            db.executeQuery();

            if (db.result.next()) {
                returnVal = db.result.getString(1);
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(ObjectPermission.class).error("ObjectPermission.getDefaultPermission() failed " + sqle);
        } finally {
            db.release();
        }

        return returnVal;
    }


    /*
    public void load()
    {
    }
    */


    /**
     * Store the object permission to the database
     */
    public void store() {
        DBBean db = new DBBean();
        try {
            String groupID = null;
            db.prepareStatement("SELECT group_id FROM pn_object_permission WHERE object_id = ? and " + "group_id = ? ");
            db.pstmt.setString(1, object.getID());
            db.pstmt.setString(2, getGroup().getID());
            db.executePrepared();
            while (db.result.next()) {
                groupID = db.result.getString("group_id");
            }
            if (groupID == null) {
                db.prepareStatement("insert into pn_object_permission (object_id, group_id, actions) values(?,?,?)");
                db.pstmt.setString(1, object.getID());
                db.pstmt.setString(2, getGroup().getID());
                db.pstmt.setInt(3, getActionsBits());
                db.executePrepared();
            } else {
                db.prepareStatement("update pn_object_permission set actions = ? where object_id = ? " + " and group_id = ?");
                db.pstmt.setInt(1, getActionsBits());
                db.pstmt.setString(2, object.getID());
                db.pstmt.setString(3, getGroup().getID());
                db.executePrepared();
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(ObjectPermission.class).debug("ObjectPermission.store failed " + sqle);
        } catch (NumberFormatException nfe) {
        	Logger.getLogger(ObjectPermission.class).debug("ObjectPermission.store failed " + nfe);
        } finally {
            db.release();
        }
    }


    /**
     * remove the object permission
     */
    public void remove() {
        DBBean db = new DBBean();
        try {
            db.prepareStatement("Delete FROM pn_object_permission WHERE object_id = ? and " + "group_id = ? ");

            db.pstmt.setString(1, object.getID());
            db.pstmt.setString(2, getGroup().getID());
            db.executePrepared();
        } catch (SQLException sqle) {
        	Logger.getLogger(ObjectPermission.class).debug("ObjectPermission.remove failed " + sqle);

        } catch (NumberFormatException nfe) {
        	Logger.getLogger(ObjectPermission.class).debug("ObjectPermission.remove failed " + nfe);
        } finally {
            db.release();
        }
    }
}

