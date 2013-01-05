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
|
+----------------------------------------------------------------------*/
package net.project.gui.navbar;

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * <code>FeaturedItemsAssociation</code> is reponsible for creating the database association
 * that will add or remove an individual instance of a tool (such as a form) from displaying
 * in the toolbar (left navigation bar).
 *
 * @author Matthew Flower
 * @since Gecko
 */
public class FeaturedItemsAssociation {
    protected DBBean db = new DBBean();
    private String spaceID;
    private String objectID;
    private boolean displayInToolsMenu;

    /**
     * Gets the value of displayInToolsMenu
     *
     * @return the value of displayInToolsMenu
     */
    public boolean getDisplayInToolsMenu() {
        return this.displayInToolsMenu;
    }

    /**
     * Sets the value of displayInToolsMenu
     *
     * @param argDisplayInToolsMenu Value to assign to this.displayInToolsMenu
     */
    public void setDisplayInToolsMenu(boolean argDisplayInToolsMenu){
        this.displayInToolsMenu = argDisplayInToolsMenu;
    }

    /**
     * Set the spaceID which is going to contain a featured item.
     *
     * @param spaceID a <code>String</code> value that is the id of the space that will be
     * displaying a featured item in the left navigation bar.
     */
    public void setSpaceID(String spaceID) {
        this.spaceID = spaceID;
    }

    /**
     * Get the spaceID which is going to display a featured item in the left navigation bar.
     *
     * @return a <code>String</code> value that is the the id of the space that will be
     * displaying a featured item in the left navigation bar.
     */
    public String getSpaceID() {
        return spaceID;
    }

    /**
     * Sets the objectID that will be displaying in the left navigation bar.
     *
     * @param objectID a <code>String</code> value containing the id of a tools that is
     * going to be displaying in the left navigation bar.  (Think something like a form id.)
     */
    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    /**
     * Get the objectID of an item that will be displayed in the left navigation bar.
     *
     * @return a <code>String</code> value that is the id of an item that will be displaying
     * in the left nav bar.
     */
    public String getObjectID() {
        return objectID;
    }

    /**
     * Load information about this association from the database.  This method assumes that
     * <code>setObjectID()</code> and <code>setSpaceID()</code> have already been called.
     */
    public void load() throws PersistenceException {
        if (spaceID == null) {
        	Logger.getLogger(FeaturedItemsAssociation.class).error("spaceID is null.  This field is required before you " +
                                       "can call load()");
        }
        if (objectID == null) {
        	Logger.getLogger(FeaturedItemsAssociation.class).error("objectID is null.  This field is required before you " +
                                       "can call load()");
        }
                                           
        
        try {
            db.prepareStatement("select object_id from pn_space_has_featured_menuitem where "+
                                "space_id = ? and object_id = ?");
            db.pstmt.setString(1, spaceID);
            db.pstmt.setString(2, objectID);
            db.executePrepared();

            //If there is a row, we already have this association, exit out.
            if (db.result.next()) {
                setDisplayInToolsMenu(true);
            } else {
                setDisplayInToolsMenu(false);
            }
            
        } catch (SQLException sqle) {
        	Logger.getLogger(FeaturedItemsAssociation.class).error("An unexpected error occurred trying to add object_id " +
               objectID + " to the left navbar of space " + spaceID + ": "+
               sqle);
            throw new PersistenceException("An unexpected error occured while trying to expose "+
               "this object on the tools menu", sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Creates the association for a given spaceID and objectID in the database.  The result
     * of this association is that the object should show up in the left navigation bar for
     * the given space.
     */
    public void store() throws PersistenceException
    {
        try {
            //Call store with the DBBean already created for this class
            store(db);
        } catch (SQLException sqle) {
        	Logger.getLogger(FeaturedItemsAssociation.class).error("An unexpected error occurred trying to add object_id " +
               objectID + " to the left navbar of space " + spaceID + ": "+
               sqle);
            throw new PersistenceException("An unexpected error occured while trying to expose "+
               "this object on the tools menu", sqle);
        } finally {
            db.release();
        }
    }
    
    /**
     * Creates the association for a given spaceID and objectID in the database.  The result
     * of this association is that the object should show up in the left navigation bar for
     * the given space.
     *
     * @param db a <code>DBBean</code> that is used to interface with the database.
     */
    public void store(DBBean db) throws SQLException {
        //------------------------------------------------------------------
        //First, check to see if the association is already in the database
        //------------------------------------------------------------------
        db.prepareStatement("select object_id from pn_space_has_featured_menuitem where "+
                            "space_id = ? and object_id = ?");
        db.pstmt.setString(1, spaceID);
        db.pstmt.setString(2, objectID);
        db.executePrepared();

        //If there is a row, we already have this association, exit out.
        if (db.result.next())
            return;

        //------------------------------------------------------------------
        //Second, create the association
        //------------------------------------------------------------------
        db.prepareStatement("insert into pn_space_has_featured_menuitem "+
                            " (space_id, object_id) values (?, ?)");
        db.pstmt.setString(1, spaceID);
        db.pstmt.setString(2, objectID);

        db.executePrepared();

        //Make sure the object knows that there is a relationship
        setDisplayInToolsMenu(true);
    }


    /**
     * Used to remove a tool from the left navigation bar for a given space.
     *
     * @throws PersistenceException if an error occurs while updating this
     * record in the database.
     */
    public void remove() throws PersistenceException {
        try {
            remove(db);
        } catch (SQLException sqle) {
        	Logger.getLogger(FeaturedItemsAssociation.class).error("An unexpected error occurred trying to remove object id " +
               objectID + " from the left nav bar of space " + spaceID +
               " : " + sqle);
            throw new PersistenceException("An unexpected error occurred while trying to remove "+
               "this object from the tools menu", sqle);
        } finally {
            db.release();
        }
    }
    
    /**
     * Used to remove a tool from the left navigation bar for a given space.
     * 
     * @throws SQLException if an error occurs while updating the database.
     */
    public void remove(DBBean db) throws SQLException {
        db.prepareStatement("delete from pn_space_has_featured_menuitem where "+
                            "space_id = ? and object_id = ?");
        System.out.println("SpaceID="+spaceID);
        System.out.println("ObjectID="+objectID);
        db.pstmt.setString(1, spaceID);
        db.pstmt.setString(2, objectID);

        db.executePrepared();
    }

    /**
     * Clear out private member variables to return this object to it's creation state.
     */
    public void clear() {
        spaceID = null;
        objectID = null;
    }
}
