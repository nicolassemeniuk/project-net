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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.database.DBBean;
import net.project.security.group.GroupCollection;
import net.project.space.Space;

import org.apache.log4j.Logger;

/**
    A set of actions which are permitted on a Module.
    Module permissions take precedence over all object permissions for object type owned by the module.
    For example the Role must have MODIFY permission for the "document" Module in order to modify any
    document or folder in that module.
*/
public class ModulePermission extends Permission implements java.io.Serializable {
    /** The project.net Module this permission applies to */
    protected Module m_module = null;

    /** The Space context for this permission */
    protected Space m_space = null;

    protected ObjectType m_objectType = null;

    protected ArrayList m_permissionList = null;
    protected ArrayList m_actionList = null;
    protected ArrayList m_defaultObjectPermissionList = null;
    protected GroupCollection m_groupList = null;

    protected String m_spaceID = null;
    protected String m_moduleID = null;
    protected String m_groupID = null;
    protected String m_selectedID = null;
    protected String m_previousID = null;

    /**
     * Stores all permissions in specified collection.
     * Efficient since it does this in one round-trip to the database.
     * @param permissions the collection of <code>ModulePermission</code>s
     */
    public static void storeAll(Collection permissions) {
        ModulePermission permission = null;
        StringBuffer query = new StringBuffer();
        DBBean db = new DBBean();

        query.append("{call SECURITY.STORE_MODULE_PERMISSION(?,?,?,?,?)}");

        try {
            db.prepareCall(query.toString());

            // Iterate over all permissions, adding to batch prepared statement
            Iterator it = permissions.iterator();
            while (it.hasNext()) {
                int index = 0;
                int status = 0;

                permission = (ModulePermission) it.next();
                db.cstmt.setString(++index, permission.m_space.getID());
                db.cstmt.setString(++index, permission.getGroup().getID());
                db.cstmt.setString(++index, permission.m_module.getId());
                db.cstmt.setInt(++index, permission.getActionsBits());
                // Copying original code from store() method:
                // This does not seem right - status is an OUT parameter
                db.cstmt.setInt(++index, status);
                db.cstmt.addBatch();
            }

            db.executeCallableBatch();

        } catch (SQLException sqle) {
        	Logger.getLogger(ModulePermission.class).error("ModulePermission.store() failed " + sqle);
        
        } finally {
            db.release();
        }

    }


    /** Set the Module context for this permission */
    public void setModule(Module module) {
        m_module = module;
    }

    /** Get the Module context for this permission */
    public Module getModule() {
        return m_module;
    }

    /** Set the Module ID context for this permission */
    public void setModuleID(String value) {
        m_moduleID = value;
    }


    /** Set the Space context for this permission */
    public void setSpace(Space space) {
        m_space = space;
    }

    /** Set the Space context for this permission */
    public void setSpaceID(String value) {
        m_spaceID = value;
    }

    /** Set the Group ID context for this permission */
    public void setGroupID(String value) {
        m_groupID = value;
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
        Returns an XML representation of the object.    
        @return XML representation of the Action.
    */
    public String getXML() {

        StringBuffer xml = new StringBuffer();
        return xml.toString();
    }

    /**
        Saves the Module Permissions to the database
    */
    public void store() {

        DBBean db = new DBBean();

        try {
            int status = 0;

            // call Stored Procedure to insert or update all the tables involved in storing a discussion group.
            db.prepareCall("begin  SECURITY.STORE_MODULE_PERMISSION(?,?,?,?,?); end;");            
            db.cstmt.setInt(1, Integer.parseInt(m_space.getID()));
            db.cstmt.setInt(2, Integer.parseInt(getGroup().getID()));
            db.cstmt.setInt(3, Integer.parseInt(m_module.getId()));
            db.cstmt.setInt(4, getActionsBits());
            db.cstmt.setInt(5, status);

            db.executeCallable();                  
        } catch (SQLException sqle) {
        	Logger.getLogger(ModulePermission.class).error("ModulePermission.store() failed " + sqle);
        } catch (NumberFormatException nfe) {
        	Logger.getLogger(ModulePermission.class).error("ModulePermission.store() failed " + nfe);
        } finally {
            db.release();
        }
    }

    /**
        Removes a permission from the Module Persmission table
    */
    public void remove() {

        DBBean db = new DBBean();

        try {

            db.prepareStatement("Delete FROM pn_module_permission WHERE space_id = ? and group_id = ? ");

            db.pstmt.setString(1, m_space.getID());
            db.pstmt.setString(2, getGroup().getID());

            db.executePrepared();            
        } catch (SQLException sqle) {
        	Logger.getLogger(ModulePermission.class).error("ModulePermission.remove() failed " + sqle);
        } catch (NumberFormatException nfe) {
        	Logger.getLogger(ModulePermission.class).error("ModulePermission.remove() failed " + nfe);
        } finally {
            db.release();
        }
    }
}


