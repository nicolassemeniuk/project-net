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

 package net.project.database;

import java.sql.SQLException;

import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;

import org.apache.log4j.Logger;


/**
 * Manages the creation, maintence and default permissions on database objects. 
 */
public class ObjectManager implements java.io.Serializable {
    /**
     * Get the next object id from the database.  
     *
     * @deprecated As of Gecko Update 4.  The naming convention is slightly
     * incorrect - ID should be capitalized.  This method is replaced by 
     * {@link #getNewObjectID()}.
     * @return a object_id from the database sequence generator
     */
    public static String getNewObjectId() {
        return getNewObjectID();
    }
    
    /**
     * Get the next object id from the database.  
     *
     * @return a object_id from the database sequence generator
     */
    public static String getNewObjectID() {
        String objectID = null;
        DBBean db = new DBBean();

        try {
            db.executeQuery("select pn_object_sequence.nextval as object_id from dual");

            if (db.result.next())
                objectID =  db.result.getString("object_id");
        } catch (SQLException sqle) {
            Logger.getLogger(ObjectManager.class).error("Could not get object_id from sequence generator");
        } finally {
            db.release();
        }

        return objectID;
    }

    /**
     * Assign a new object_id and register the object_in the in the database pn_object table.
     *
     * @param object_type the type of this object (i.e. form, project, post, doc, etc.)
     * @param record_status the status of the object to be created (A, P, D, etc.)
     * @return a object_id of the pn_object that was newly created.    
     */
    public static String dbCreateObject(String object_type, String record_status) {
        return dbCreateObject(object_type,  record_status, null);
    }

    /**
     * Assign a new object_id and register the object_in the in the database pn_object table.
     *
     * @param db a <code>DBBean</code> object.  This object will be used to create
     * the object and is guaranteed not to be committed or rolled back by this method.
     * @param object_type the type of this object (i.e. form, project, post, doc, etc.)
     * @param record_status the status of the object to be created (A, P, D, etc.)
     * @return a object_id of the pn_object that was newly created.
     */
    public static String dbCreateObject(DBBean db, String object_type, String record_status) throws SQLException {
        return dbCreateObject(db, object_type,  record_status, null);
    }

    /** 
     * Assign a new objectID, register the object in the in the database
     * pn_object table, and assign default permissions to the object for the
     * specified space.
     *
     * @param object_type the type of this object (i.e. form, project, post, doc, etc.)
     * @param record_status the status of the object to be created (A, P, D, etc.)
     * @param space the Space context for setting default security permissions on the object.
     * @return a object_id of the pn_object that was newly created.
     * @param userID The user that is the default owner/creator of the object.
     */
    public static String dbCreateObjectWithPermissions(String object_type, String record_status, Space space, String userID) {
        String objectID = null;

        objectID =  dbCreateObject(object_type,  record_status, null);
        createDefaultPermissions(objectID, object_type, space, userID);
        return objectID;
    }




    /** Assign a new objectID, register the object in the in the database
     * pn_object table, and assign default permissions to the object for the
     * specified space..
     * @param object_type the type of this object (i.e. form, project, post, doc, etc.)
     * @param record_status the status of the object to be created (A, P, D, etc.)
     * @param spaceID the Space context for setting default security permissions on the object.
     * @return a object_id of the pn_object that was newly created.
     * @param userID The user that is the default owner/creator of the object.
     */
    public static String dbCreateObjectWithPermissions(String object_type, String record_status, String spaceID, String userID) {
        String objectID = null;

        objectID =  dbCreateObject(object_type,  record_status, null);
        createDefaultPermissions(objectID, object_type, spaceID, userID);
        return objectID;
    }



    /**
     * Assign a new object_id and register the object_in the in the database 
     * pn_object table.
     * 
     * @param object_type the type of this object (i.e. form, project, post, doc, etc.)
     * @param record_status the status of the object to be created (A, P, D, etc.)
     * @param user the User that is the creator of this object.
     * @return a object_id of the pn_object that was newly created.    
     */
    public static String dbCreateObject(String object_type, String record_status, User user) {
        DBBean db = new DBBean();
        String objectID = null;
        try {
            objectID = dbCreateObject(db, object_type, record_status, user);
        } catch (SQLException sqle) {
        	Logger.getLogger(ObjectManager.class).error("Could not get object_id from sequence generator");
        } finally {
            db.release();
        }

        return objectID;
    }

    public static String dbCreateObject(DBBean db, String object_type, String record_status, User user) throws SQLException {
        String objectID = null;
        String userId = null;
        String spaceId = null;

        // Get the user object from session cache
        if (user != null)
            userId = user.getID();
        else if (SessionManager.getUser() != null)
            userId = SessionManager.getUser().getID();

        // if userId is still null, set to system user, so insert won't fail.
        if (userId == null)
            userId = "1";

        if ((objectID = getNewObjectID()) == null )
            return null;

        db.executeQuery("insert into pn_object (object_id, date_created, object_type, created_by, record_status) " +
                        "values (" + objectID + ", SYSDATE, " + DBFormat.varchar2(object_type) + ", " + userId + ", " + DBFormat.varchar2(record_status) + ")");

        return objectID;
    }

    /**
     * Create default security permissions for the specified object.  Uses the 
     * default security permissions defined for the SpaceID and the ObjectType.
     *
     * @param objectID the objects database ID
     * @param objectType the String object type name (i.e. "form", "document", "post").
     * @param spaceID the project space, business space, etc. context for this permission.
     * @param userID the person_id of the creator
     */
    public static void createDefaultPermissions(String objectID, String objectType, String spaceID, String userID) {
        DBBean db = new DBBean();
        
        try {
            db.prepareCall ("begin SECURITY.CREATE_SECURITY_PERMISSIONS (?,?,?,?); end;");
            db.cstmt.setString(1, objectID);
            db.cstmt.setString(2, objectType);
            db.cstmt.setString(3, spaceID);
            db.cstmt.setString(4, userID);
            db.executeCallable();
        } catch (SQLException sqle) {
        	Logger.getLogger(ObjectManager.class).debug("ObjectManager: Unable to create default permission on object=" + 
                           objectID + ", objectType=" + objectType + ", spaceID=" + spaceID + 
                           " , userID= " + userID);
        } finally {
            db.release();
        }
    }

   /**
    * Create default security premissions for the specified object.  Uses the
    * default security permissions defined for the Space and the ObjectType.
     *
     * @param objectID the objects database ID
     * @param objectType the String object type name (i.e. "form", "document", "post").
     * @param space the project space, business space, etc. context for this permission.
     * @param userID the person_id of the creator
    */
    public static void createDefaultPermissions(String objectID, String objectType, Space space, String userID) {
        createDefaultPermissions(objectID, objectType, space.getID(), userID);
    }
}

