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
package net.project.security;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import net.project.base.ObjectType;
import net.project.database.DBBean;
import net.project.space.Space;

import org.apache.log4j.Logger;

/**
 * A set of actions which are permitted on an DefaultObject.
 */
public class DefaultObjectPermission extends Permission implements java.io.Serializable {
    protected ObjectType m_objectType = null;

    /** The Space context for this permission */
    protected Space m_space = null;

    protected String m_selectedID = null;
    protected String m_previousID = null;

    // Database access bean
    protected DBBean db = new DBBean();


    /**
     * Stores all permissions in specified collection.
     * Efficient since it does this in one round-trip to the database.
     * @param permissions the collection of <code>DefaultObjectPermission</code>s
     */
    public static void storeAll(Collection permissions) {
        DefaultObjectPermission permission = null;
        StringBuffer query = new StringBuffer();
        DBBean db = new DBBean();

        query.append("{call SECURITY.store_default_obj_permission(?,?,?,?,?)}");

        try {
            db.prepareCall(query.toString());

            // Iterate over all permissions, adding to batch prepared statement
            Iterator it = permissions.iterator();
            while (it.hasNext()) {
                int index = 0;
                int status = 0;

                permission = (DefaultObjectPermission) it.next();
                db.cstmt.setString(++index, permission.m_space.getID());
                db.cstmt.setString(++index, permission.m_objectType.getType());
                db.cstmt.setString(++index, permission.getGroup().getID());
                db.cstmt.setInt(++index, permission.getActionsBits());
                // We can't get status back from batched statements, so doesn't
                // matter what we bind here
                db.cstmt.setInt(++index, status);
                db.cstmt.addBatch();
            }

            db.executeCallableBatch();

        } catch (SQLException sqle) {
        	Logger.getLogger(DefaultObjectPermission.class).error("DefaultObjectPermission.storeAll() failed " + sqle);
        
        } finally {
            db.release();
        }

    }


    /**
            Constructs a DefaultObjectPermission Object
    */
    public DefaultObjectPermission() {

    }

    /** Set the Object context for this permission */
    public void setObjectType(ObjectType object) {
        m_objectType = object;
    }

    /** Get the Object context for this permission */
    public ObjectType getObjectType() {
        return m_objectType;
    }

    /** Set the Space context for this permission */
    public void setSpace(Space space) {
        m_space = space;
    }

    /** Get the Space context for this permission */
    public Space getSpace() {
        return m_space;
    }


    /** Set the selected ID this permission */
    public void setSelectedID(String value) {
        m_selectedID = value;
    }

    /** Get the selected ID for this permission */
    public String getSelectedID() {
        return m_selectedID;
    }

    /** Set the previous ID for this permission */
    public void setPreviousID(String value) {
        m_previousID = value;
    }

    /** Get the previous ID for this permission */
    public String getPreviousID() {
        return m_previousID;
    }


    /**
     * Stores this DefaultObjectPermission.
     */
    public void store() {
        StringBuffer query = new StringBuffer();

        query.append("{call SECURITY.store_default_obj_permission(?,?,?,?,?)}");

        try {
            int status = 0;
            int index = 0;
            db.prepareCall(query.toString());
            db.cstmt.setString(++index, m_space.getID());
            db.cstmt.setString(++index, m_objectType.getType());
            db.cstmt.setString(++index, getGroup().getID());
            db.cstmt.setInt(++index, getActionsBits());
            db.cstmt.setInt(++index, status);
            db.executeCallable();

        } catch (SQLException sqle) {
        	Logger.getLogger(DefaultObjectPermission.class).error("DefaultObjectPermission.store failed " + sqle);
        
        } finally {
            db.release();
        
        }
    
    }

    public void remove() {
        try {
            String groupID = null;

            db.prepareStatement("Delete FROM pn_default_object_permission WHERE space_id = ? and group_id = ? ");

            db.pstmt.setString(1, m_space.getID());
            db.pstmt.setString(2, getGroup().getID());

            db.executePrepared();           
        } catch (SQLException sqle) {
        	Logger.getLogger(DefaultObjectPermission.class).error("DefaultObjectPermission.remove failed " + sqle);
        } finally {
            db.release();
        }
    }
}

