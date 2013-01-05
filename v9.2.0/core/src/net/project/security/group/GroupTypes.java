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
package net.project.security.group;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * Provides the collection of available group types.
 * This is a singletone collection, loaded once from the database.
 */
public class GroupTypes implements java.io.Serializable {

    /**
     * The singleton loaded instance of GroupTypes.
     */
    private static final GroupTypes GROUP_TYPES = new GroupTypes();

    /**
     * Returns the loaded collection of group types.
     * @return the loaded group types
     */
    public static GroupTypes getAll() {
        return GROUP_TYPES;
    }

    /**
     * The collection of GroupTypes.
     * Each key is a <code>GroupTypeID</code>, each value is a <code>GroupType</code>.
     */
    private final Map groupTypeMap = new HashMap();

    /**
     * Creates a new GroupTypes.
     */
    private GroupTypes() {
        try {
            load();
        } catch (PersistenceException e) {
            // Do nothing; group types are empty
        }
    }

    /**
     * Loads the avaialable group types.
     * @throws PersistenceException if there is a problem loading
     */
    private void load() throws PersistenceException {
        GroupType type = null;
        DBBean db = new DBBean();
        StringBuffer query = new StringBuffer();

        query.append("select gt.group_type_id, gt.class_name ");
        query.append("from pn_group_type gt ");

        try {
            db.prepareStatement(query.toString());
            db.executePrepared();

            while (db.result.next()) {
                type = new GroupType();
                
                type.setID(GroupTypeID.forID(db.result.getString("group_type_id")));
                type.setClassName(db.result.getString("class_name"));
                groupTypeMap.put(type.getID(), type);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(GroupTypes.class).error("GroupTypes.load() threw an SQLException: " + sqle);
            throw new PersistenceException("Group types load operation failed", sqle);
        
        } finally {
            db.release();

        }

    }
    

    /**
     * Returns the group type for the given id.
     * @param groupTypeID the id of the group type to get
     * @return the group type, or null if there is no group type for
     * the specified id
     */
    public GroupType getGroupType(GroupTypeID groupTypeID) {
        return (GroupType) groupTypeMap.get(groupTypeID);
    }

}
