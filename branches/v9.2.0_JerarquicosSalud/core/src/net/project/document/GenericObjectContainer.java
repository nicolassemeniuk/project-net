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
|   $Revision: 18995 $
|       $Date: 2009-03-05 08:36:26 -0200 (jue, 05 mar 2009) $
|     $Author: avinash $
|
|
+----------------------------------------------------------------------*/
package net.project.document;

import java.sql.SQLException;

import net.project.base.PnetException;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.User;

import org.apache.log4j.Logger;

/**
 * This class creates an association between any object and a document container,
 * effectively allowing anything that has an object id to have a document vault.
 *
 * This new document vault is created in the space of the object to prevent security
 * issue from occuring.
 * 
 * @author Matthew Flower
 * @since Gecko Update 2
 */
public class GenericObjectContainer {
    private String parentContainerID;
    private String objectIDFolderID;
    private String topFolderID;
    private String objectID;
    private User user;
    private String topFolderName;

    /**
     * Gets the value of containerID
     *
     * @return the value of containerID
     */
    public String getContainerID() {
        return this.topFolderID;
    }

    /**
     * Sets the value of containerID
     *
     * @param argContainerID Value to assign to this.containerID
     */
    public void setContainerID(String argContainerID){
        this.topFolderID = argContainerID;
    }

    /**
     * Gets the value of objectID
     *
     * @return the value of objectID
     */
    public String getObjectID() {
        return this.objectID;
    }

    /**
     * Sets the value of objectID
     *
     * @param argObjectID Value to assign to this.objectID
     */
    public void setObjectID(String argObjectID){
        this.objectID = argObjectID;
    }

    /**
     * Gets the value of parentContainerID
     *
     * @return the value of parentContainerID
     */
    public String getParentContainerID() {
        return this.parentContainerID;
    }

    /**
     * Sets the value of parentContainerID
     *
     * @param argParentContainerID Value to assign to this.parentContainerID
     */
    public void setParentContainerID(String argParentContainerID){
        this.parentContainerID = argParentContainerID;
    }

    /**
     * Get the user that is creating the document container.
     *
     * @return an <code>User</code> value
     */
    public User getUser() {
        return this.user;
    }
    
    /**
     * Set the user that is creating the document container.  
     *
     * @param user an <code>User</code> value
     */
    public void setUser(User user) {
        this.user = user;
    }

  
    /**
     * Set the name of the top folder in document container  
     *
     * @param topFolderName an <code>folder name</code> value
     */    
	public void setTopFolderName(String topFolderName) {
		this.topFolderName = topFolderName;
	}

	/**
     * Create the container for this object id.
     */
    private void createObjectIDContainer() throws PnetException, PersistenceException {
        DBBean db = new DBBean();

        try {
            //First check to see if this folder already exists
            db.prepareStatement("select doc_container_id from pn_doc_container where container_name=?");
            db.pstmt.setString(1, objectID);
            db.executePrepared();

            if (db.result.next()) {
                objectIDFolderID = db.result.getString("doc_container_id");
            } else {
                Container objectIDFolder = new Container();
                objectIDFolder.setUser(user);
                objectIDFolder.setName(objectID);
                objectIDFolder.setContainerID(parentContainerID);
                objectIDFolder.setDescription("System container for this space");
                objectIDFolder.setIsSystem("1");
                objectIDFolderID = objectIDFolder.create();
            }
        
        } catch (SQLException sqle) {
        	Logger.getLogger(GenericObjectContainer.class).error("Unable to create new folder due to an unexpected " +
                                       "SQLException: " + sqle);
            throw new PersistenceException("Unable to create new folder due to an unexpected "+
                                           "error: " + sqle, sqle);

        } finally {
            db.release();

        }


    }

    /**
     * Store a new document container and its relationship to an objectID.
     *
     * @exception PersistenceException if an error occurs while trying to
     * store items in the database.
     */
    public void store() throws PersistenceException {
        DBBean db = new DBBean();

        try {
            //Create a container for this object id.  (This is different from
            //the top folder.
            createObjectIDContainer();

            //Store the top folder
            Container topFolder = new Container();
            topFolder.setUser(user);
            if (topFolderName == null || topFolderName.trim().length() == 0){
            	topFolder.setName("@prm.document.container.topfolder.name");
            } else {
            	topFolder.setName(topFolderName);
            }
           
            topFolder.setContainerID(objectIDFolderID);
            topFolder.setDescription("Top level document folder");
            topFolder.setIsSystem("1");

            //Grab the new containerID and put it back into this object
            topFolderID = topFolder.create();

            //Now store the relationship between the object and container.
            db.prepareStatement("insert into pn_object_has_doc_container "+
                                "  (object_id, container_id) "+
                                "values "+
                                "  (?,?) ");
            db.pstmt.setString(1, objectID);
            db.pstmt.setString(2, topFolderID);
            db.executePrepared();
        } catch (SQLException sqle) {
        	Logger.getLogger(GenericObjectContainer.class).error("Unable to create new folder due to an unexpected "+
                                       "SQLException");
            throw new PersistenceException("Unable to create new folder due to an unexpected "+
                                           "error."+sqle, sqle);
        } catch (PnetException pnete) { 
        	Logger.getLogger(GenericObjectContainer.class).error("PnetException thrown while trying to create a container.");
            throw new PersistenceException("Unable to create new folder due to an unexpected "+
                                           "error.", pnete);
        } finally { 
            db.release();
        }
    }
    
    /**
     * Find the document container id for a given object id.
     *
     * @param objectID a <code>String</code> value containing the object id 
     * @return a <code>String</code> value containing the container id for
     * the object id passed to this method.  If there isn't a container, this
     * value will be null.
     * @exception PersistenceException if an error occurs while trying to find
     * this information in the database.
     */
    public static String findContainerForObjectID(String objectID) throws PersistenceException {
        DBBean db = new DBBean();
        String containerID;

        try {
            db.prepareStatement("select container_id from pn_object_has_doc_container "+
                                "where object_id = ?");
            db.pstmt.setString(1, objectID);
            db.executePrepared();

            if (db.result.next()) {
                containerID = db.result.getString("container_id");
            } else {
                containerID = null;
            }

            return containerID;
        } catch (SQLException sqle) {
        	Logger.getLogger(GenericObjectContainer.class).error(""+sqle);
            throw new PersistenceException(""+sqle, sqle);
        } finally { 
            db.release();
        }
    }
}
