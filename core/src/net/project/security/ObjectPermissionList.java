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

import net.project.base.ObjectType;
import net.project.base.PnObject;
import net.project.database.DBBean;
import net.project.security.group.GroupException;
import net.project.security.group.GroupProvider;
import net.project.security.group.GroupTypeID;
import net.project.space.Space;

import org.apache.log4j.Logger;

/**
 * A set of actions which are permitted on an Object.
 */
public class ObjectPermissionList 
        extends java.util.ArrayList 
        implements Serializable {
    
    /** The project.net Module this permission applies to */
    private PnObject m_object = null;

    protected ObjectType m_objectType = null;

    /** The Space context for this permission */
    protected Space m_space = null;

    // Database access bean
    protected DBBean db = new DBBean();

    /**
     * Set the Object context for this permission
     * @param object 
     */
    public void setObject(PnObject object) {
        m_object = object;
        this.clear();
    }

    /**
     * Get the Object context for this permission
     * @return 
     */
    public PnObject getObject() {
        return m_object;
    }


    /**
     * Set the Space context for this permission
     * @param space 
     */
    public void setSpace(Space space) {
        m_space = space;
    }

    /**
     * Get the Space context for this permission
     * @return 
     */
    public Space getSpace() {
        return m_space;
    }

    /**
     * The load of this object which will get all the permission for the object.
     */
    public void load() {
        StringBuffer query = new StringBuffer();
        ObjectPermission obp = null;

        GroupProvider groupProvider = new GroupProvider();

        query.append("select op.group_id, op.actions, g.group_name, g.group_type_id, g.principal_owner_display_name ");
        query.append("from pn_object_permission op, pn_group_view g ");
        query.append("where op.object_id = ? ");
        query.append("and g.group_id = op.group_id ");

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, m_object.getID());
            db.executePrepared();

            while (db.result.next()) {
                obp = new ObjectPermission();

                try {
                    // Has to be a DisplayGroup for ObjectPermissions
                    DisplayGroup group = null;
                    group = new DisplayGroup(groupProvider.newGroup(GroupTypeID.forID(db.result.getString("group_type_id"))));
                    group.setID(db.result.getString("group_id"));
                    group.setName(db.result.getString("group_name"));
                    
                    // Overrides the principal group name with the display name of
                    // the owner
                    if (group.getGroupTypeID().equals(GroupTypeID.PRINCIPAL)) {
                        group.setName(db.result.getString("principal_owner_display_name"));
                    }

                    obp.setActionBits(db.result.getInt("actions"));
                    obp.setStatus(Permission.EXIST);
                    obp.setGroup(group);
                
                    this.add(obp);

                } catch (GroupException ge) {
                    // No object type permission
                
                }

            }
        } catch (SQLException sqle) {
        	Logger.getLogger(ObjectPermissionList.class).debug("ObjectPermissionList.load() failed " + sqle);

        } finally {
            db.release();
        
        }

    }

}
