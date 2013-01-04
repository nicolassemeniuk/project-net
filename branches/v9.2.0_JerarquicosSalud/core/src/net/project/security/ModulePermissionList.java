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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.security;

import java.io.Serializable;
import java.sql.SQLException;

import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.security.group.Group;
import net.project.security.group.GroupException;
import net.project.security.group.GroupProvider;
import net.project.security.group.GroupTypeID;
import net.project.space.Space;

import org.apache.log4j.Logger;

/**
 * A set of actions which are permitted on a Module.
 * Module permissions take precedence over all object permissions for object type owned by the module.
 * For example the Role must have MODIFY permission for the "document" Module in order to modify any
 * document or folder in that module.
 */
public class ModulePermissionList extends java.util.ArrayList implements Serializable {
    /** The project.net Module this permission applies to */
    protected Module m_module = null;

    /** The Space context for this permission */
    protected Space m_space = null;

    protected ObjectType m_objectType = null;

    // Database access bean
    protected DBBean db = new DBBean();

    /** Construct a ModulePermissionList of unknown size.  Grows as needed */
    public ModulePermissionList() {
        super();
    }

    /** Set the Space context for this permission */
    public void setSpace(Space space) {
        m_space = space;
    }

    /** Get the Space context for this permission */
    public Space getSpace() {
        return m_space;
    }

    /**	
        Populates an arraylist of ModulePermissions for a group and a space
    */
    public ModulePermissionList getModulePermissionList() {
        return this;
    }

    /**
     * Populates an arraylist of modules for space
     * @return 
     */
    public ModulePermissionList getModulesBySpace() {
        String query;
        Module module = null;
        ModulePermission mp = null;
        ModulePermissionList modulesBySpace = new ModulePermissionList();

        query = "SELECT distinct shm.module_id, m.name, m.description, m.column_order " +
                "FROM pn_space_has_module shm, pn_module m " +
                "WHERE shm.space_id = " + m_space.getID() + 
                " and m.module_id not in (90, 100, 120, 210) " + // exclude Budget, Project Metrics, Site Weather, Vote from module security selection
                " AND shm.module_id = m.module_id and shm.is_active = '1'" +
                "order by m.column_order ";

        try {
            db.setQuery(query);
            db.executeQuery();
            while (db.result.next()) {
                module = new Module();
                mp = new ModulePermission();

                module.setId(db.result.getString(1));
                module.setName(db.result.getString(2));
                module.setDescription(PropertyProvider.get(db.result.getString(3)));

                modulesBySpace.add(module);
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(ModulePermissionList.class).debug("ModulePermissionList.getModulesBySpace failed: " + sqle);
        } finally {
            db.release();
        }

        return modulesBySpace;
    }


    /**
     * Populates this object with the permission allowed for a module based on a Space ID
     */
    public void load() {
        StringBuffer query = new StringBuffer();
        Module module = null;
        ModulePermission mp = null;

        GroupProvider groupProvider = new GroupProvider();

        query.append("select distinct shm.module_id, m.name, mp.group_id, mp.actions, g.group_type_id ");
        query.append("from pn_space_has_module shm, pn_module m, pn_module_permission mp, pn_group_view g ");
        query.append("where shm.space_id = ? ");
        query.append("and shm.space_id = mp.space_id ");
        query.append("and shm.module_id = m.module_id ");
        query.append("and m.module_id = mp.module_id ");
        query.append("and m.module_id not in (90, 100, 120, 210) "); // exclude Budget, Project Metrics, Site Weather, Vote from module security selection
        query.append("and g.group_id = mp.group_id and shm.is_active = '1' ");

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, m_space.getID());
            db.executePrepared();

            while (db.result.next()) {
                Group group = null;

                try {
                    group = groupProvider.newGroup(GroupTypeID.forID(db.result.getString("group_type_id")));
                    group.setID(db.result.getString("group_id"));

                    module = new Module();
                    mp = new ModulePermission();

                    module.setId(db.result.getString("module_id"));
                    module.setName(db.result.getString("name"));
                    mp.setActionBits(db.result.getInt("actions"));
                    mp.setModule(module);
                    mp.setGroup(group);

                    this.add(mp);

                } catch (GroupException ge) {
                    // Unknown group_type_id

                }
            }
        
        } catch (SQLException sqle) {
        	Logger.getLogger(ModulePermissionList.class).debug("ModulePermissionList.load failed: " + sqle);
        
        } finally {
            db.release();
        
        }

    }

    public void store() {
    }

    public void remove() {
    }

}
