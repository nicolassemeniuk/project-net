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
|   $Revision: 19860 $
|       $Date: 2009-08-25 12:45:15 -0300 (mar, 25 ago 2009) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.security;

import java.sql.SQLException;

import net.project.base.ObjectType;
import net.project.database.DBBean;
import net.project.security.group.Group;
import net.project.security.group.GroupException;
import net.project.security.group.GroupProvider;
import net.project.security.group.GroupTypeID;
import net.project.space.Space;
import net.project.space.SpaceTypes;

import org.apache.log4j.Logger;

/**
 * Presents the List that are asscociated with Default Object Permissions
 */
public class DefaultObjectPermissionList extends java.util.ArrayList implements java.io.Serializable {
    /** The project.net ObjectType this permission applies to */
    protected ObjectType m_objectType = null;

    /** The Space context for this permission */
    protected Space m_space = null;

    // Database access bean
    protected DBBean db = new DBBean();

    /** Construct an array list of unknown size for the DefaultObejctPermission.  Grows as needed */
    public DefaultObjectPermissionList() {
        super();
    }

    /** Get the Object context for this permission */
    public ObjectType getObjectType() {
        return m_objectType;
    }

    /** Set the Space context for this permission */
    public void setSpace(Space space) {
        m_space = space;
    }


    /**
     * Populates an arraylist of ObjectTypes for a space based on the DefaultObjectPermission
     * @return 
     */
    public DefaultObjectPermissionList getDefaultObjectsBySpace() {
        StringBuffer query = new StringBuffer();
        DefaultObjectPermissionList defaultObjectBySpace = new DefaultObjectPermissionList();

        query.append("select distinct dop.object_type, ot.object_type_desc ");
        query.append("from pn_default_object_permission dop, pn_object_type ot ");
        query.append("where dop.space_id = ? ");
        query.append("and dop.object_type = ot.object_type ");
        
        if(m_space.isTypeOf(SpaceTypes.PROJECT_SPACE)){
        	query.append(" and ot.is_securable > 0 ");	
        } else {
        	query.append(" and ot.is_securable = 2 ");
        }

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, m_space.getID());
            db.executePrepared();

            while (db.result.next()) {
                ObjectType objectType = new ObjectType();
                objectType.setType( db.result.getString("object_type"));
                objectType.setDescriptionPropertyName(db.result.getString("object_type_desc"));

                defaultObjectBySpace.add(objectType);
            }
        
        } catch (SQLException sqle) {
        	Logger.getLogger(DefaultObjectPermissionList.class).error("DefaultObjectPermissionList.getDefaultObjectsBySpace(): " + sqle);        
        
        } finally {
            db.release();
        
        }
        
        return defaultObjectBySpace;
    }


    /**
        Populates this DefaultObjectPermissionList object based on a space ID	
    */
    public void load() {
        StringBuffer query = new StringBuffer();
        DefaultObjectPermission obp = null;

        GroupProvider groupProvider = new GroupProvider();

        query.append("select dop.group_id, dop.object_type, dop.actions, g.group_type_id ");
        query.append("from pn_default_object_permission dop, pn_group g , pn_object_type po ");
        query.append("where space_id = ? ");
        query.append(" and g.group_id = dop.group_id ");
        
        if(m_space.isTypeOf(SpaceTypes.PROJECT_SPACE)){
        	query.append(" and po.is_securable > 0 ");	
        } else {
        	query.append(" and po.is_securable = 2 ");
        }

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, m_space.getID());
            db.executePrepared();

            while (db.result.next()) {
                Group group = null;
                ObjectType objectType = (ObjectType) new ObjectType();

                obp = new DefaultObjectPermission();

                try {
                    group = groupProvider.newGroup(GroupTypeID.forID(db.result.getString("group_type_id")));
                    group.setID(db.result.getString("group_id"));
                    
                    objectType.setType( db.result.getString("object_type"));
                    obp.setActionBits(db.result.getInt("actions"));
                    obp.setGroup(group);
                    obp.setObjectType(objectType);

                    this.add(obp);
                
                } catch (GroupException ge) {
                    // No object type permission
                
                }
            }
        
        } catch (SQLException sqle) {
        	Logger.getLogger(DefaultObjectPermissionList.class).error("DefaultObjectPermissionList.load() failed " + sqle);
        } finally {
            db.release();
        
        }
    }

}
