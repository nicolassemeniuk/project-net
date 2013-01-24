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

 package net.project.crossspace;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.persistence.PersistenceException;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.Task;

/**
 * This object describes a permission to share an object from one space to
 * another space.
 *
 * @author Matthew Flower
 * @since Version 8.0.0
 */
public class ExportedObject {
    /** The primary key for the object being shared. */
    private String id;
    /** The human-readable name of the object being shared. */
    private String name;
    /**
     * The objectType of the object being shared.
     * @see net.project.base.ObjectType
     */
    private String objectType;
    /** Indicates the type of permission that the sharer is granting. */
    private TradeAgreement permissionType = TradeAgreement.NO_ACCESS;
    /**
     * If this share is part of a hierarchy of shares, this id is the id of the
     * next item up in the hierarchy.  For example, if this share was of a task,
     * the container id would be the plan id.
     */
    private String containerID;
    /** The id of the space that this object primarily belongs to. */
    private String spaceID;

    /** The space ID's of spaces permitted to see this share. */
    private List permittedSpaces = new LinkedList();
    /** The users ID's of users permitted to see this share. */
    private List permittedUsers = new LinkedList();
    /**
     * This list contains the actions that we allow when another space attempts
     * to create a shared object.
     */
    private AllowableActionCollection allowableActions = new AllowableActionCollection();
    /**
     * Indicates if this is a parent object (e.g. a plan) that the child shares
     * should have this same share.
     */
    private boolean propagateToChildren = false;

    /**
     * List of spaces sharing this exported object.  This is not stored to the
     * database.
     */
    private List sharingSpaces;

    /**
     * Get the primary key of the object being shared.
     *
     * @return a <code>String</code> containing the primary key of the object
     * being shared.
     */
    public String getID() {
        return id;
    }

    /**
     * Set the primary key of the object being shared.
     *
     * @param id a <code>String</code> containing the primary key of the object
     * being shared.
     */
    public void setID(String id) {
        this.id = id;
    }

    /**
     * Get the name of the object being shared.
     *
     * @return a <code>String</code> containing the name of the object being
     * shared.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the object being shared.  Note that this name is never
     * stored to the database.  The name of the object is always looked up from
     * the pn_object table.
     *
     * @param name a <code>String</code> containing the name of the object being
     * shared.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get a string identified the type of object being shared.  These string
     * identifiers are declared in the {@link net.project.base.ObjectType}
     * object.
     *
     * @return a <code>String</code> which uniquely identifies the type of
     * object being shared.
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * Set the type of object being shared.  Note that this is never saved to
     * the database.
     *
     * @param objectType a <code>String</code> containing the object type of the
     * object being shared.
     */
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public TradeAgreement getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(TradeAgreement permissionType) {
        this.permissionType = permissionType;
    }

    /**
     * Get the id of the parent object for this share.  For example, if we are
     * sharing a task, all tasks are in a workplan so the container id will be
     * the id of the workplan.
     *
     * This will be null for a space object.  Generally these space objects
     * aren't directly shared though, there presence in the hierarchy of object
     * is simply to provide an object to display in a tree view of shared
     * objects.
     *
     * @return a <code>String</code> containing the unique identifier for the
     * container holding the shared object.
     */
    public String getContainerID() {
        return containerID;
    }

    /**
     * Set the id of the container which holds this share.
     *
     * @param containerID a <code>String</code> containing the object id of the
     * object which is the parent of the object we are sharing.
     */
    public void setContainerID(String containerID) {
        this.containerID = containerID;
    }

    /**
     * Indicates if this share is only for display purposes, or if it can
     * actually be shared.  Shares that cannot be shared are around for the
     * purpose of building a complete tree of shares that can be navigated.
     *
     * @return a <code>boolean</code> indicating if this share can actually be
     * shared.
     */
    public boolean canBeShared() {
        return permissionType != TradeAgreement.NO_ACCESS && permissionType != TradeAgreement.LEAVE_UNCHANGED;
    }

    /**
     * The id of the space that this object primarily belongs to.
     *
     * @return a <code>String</code> containing the id of the space this object
     * belongs to.
     */
    public String getSpaceID() {
        return spaceID;
    }

    /**
     * Set the space that the object being shared belongs to.  This value isn't
     * stored in the database.
     *
     * @param spaceID a <code>String</code> containing the id of the space that
     * this share belongs to.
     */
    public void setSpaceID(String spaceID) {
        this.spaceID = spaceID;
    }

    /**
     * Store the information in this share to the database.
     *
     * @throws PersistenceException if there is a problem storing the share's
     * information to the database.
     */
    public void store() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            db.prepareStatement("{ call SHARING.STORE_SHARE(?,?,?,?,?,?)}");

            db.pstmt.setString(1, id);
            db.pstmt.setString(2, permissionType.getID());
            db.pstmt.setString(3, containerID);
            db.pstmt.setString(4, spaceID);
            db.pstmt.setBoolean(5, propagateToChildren);
            db.pstmt.setInt(6, allowableActions.getDatabaseID());
            db.executePrepared();

            db.prepareStatement("delete from pn_shareable_permissions where object_id = ?");
            db.pstmt.setString(1, id);
            db.executePrepared();

            db.prepareStatement(
                "insert into pn_shareable_permissions " +
                "(object_id, permitted_object_id, share_type) " +
                "values " +
                "(?,?,?) ");
            for (Iterator it = permittedSpaces.iterator(); it.hasNext();) {
                ExportFinder.PermittedObject space = (ExportFinder.PermittedObject) it.next();
                db.pstmt.setString(1, id);
                db.pstmt.setString(2, space.objectID);
                db.pstmt.setString(3, SharePermissionType.SPACE.getID());
                db.pstmt.addBatch();
            }

            for (Iterator it = permittedUsers.iterator(); it.hasNext();) {
                ExportFinder.PermittedObject user = (ExportFinder.PermittedObject)it.next();

                db.pstmt.setString(1, id);
                db.pstmt.setString(2, user.objectID);
                db.pstmt.setString(3, SharePermissionType.USER.getID());
                db.pstmt.addBatch();
            }

            db.executePreparedBatch();

            if (propagateToChildren) {
                db.prepareStatement("{ call SHARING.INHERIT_SHARE_FROM_PLAN(?)}");
                db.pstmt.setString(1, id);
                db.executePrepared();
            }

            db.commit();
            db.setAutoCommit(true);
        } catch (SQLException sqle) {
            try { db.rollback(); } catch (SQLException e) {}
            throw new PersistenceException("Unable to store share.", sqle);
        } finally {
            db.release();
        }
    }

    public static void remove(String[] idList) throws PersistenceException {
        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            
            db.executeQuery(
                "delete from pn_shareable_permissions where object_id in (" +
                DatabaseUtils.collectionToCSV(Arrays.asList(idList)) + ")"
            );
            db.executeQuery(
                "update pn_shareable set permission_type = 0 where object_id in (" +
                DatabaseUtils.collectionToCSV(Arrays.asList(idList)) + ")"
            );
            String deleteShareSQL = "delete from pn_shared where imported_object_id = ?";
            ImportFinder finder = new ImportFinder();
            List sharingObjects = finder.findByExportedIDs(db, Arrays.asList(idList));
            db.prepareStatement(deleteShareSQL);
            for (Iterator it = sharingObjects.iterator(); it.hasNext();) {
                ImportedObject importedObject = (ImportedObject) it.next();
                Task task = new Task();
                task.setID(importedObject.getImportedObjectID());
                //fix for bug-1440 - task need to be loaded to get its parent while removing.
                task.load();
                task.remove();
                //fix for bfd 5670 - remove this from the pn_shared table too
                //note*: task.remove wont do that because we have not loaded the complete task here, just the reference
                db.pstmt.setString(1, importedObject.getImportedObjectID());
                db.executePrepared();
            }
            
            db.commit();
        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException e) {
                Logger.getLogger(ScheduleEntry.class).debug("Unable to roll back task.");
            }
            throw new PersistenceException("Unable to remove shares.", sqle);
        } finally {
            db.release();
        }
    }

    public void setPermittedSpaces(List permittedSpaces) {
        this.permittedSpaces = permittedSpaces;
    }

    public List getPermittedSpaces() {
        return permittedSpaces;
    }

    public void setPermittedUsers(List permittedUsers) {
        this.permittedUsers = permittedUsers;
    }

    public List getPermittedUsers() {
        return permittedUsers;
    }

    public List getSharingSpaces() {
        return sharingSpaces;
    }

    public void setSharingSpaces(List sharingSpaces) {
        this.sharingSpaces = sharingSpaces;
    }

    public boolean isPropagateToChildren() {
        return propagateToChildren;
    }

    public void setPropagateToChildren(boolean propagateToChildren) {
        this.propagateToChildren = propagateToChildren;
    }

    public AllowableActionCollection getAllowableActions() {
        return allowableActions;
    }

    public void setAllowableActions(AllowableActionCollection allowableActions) {
        this.allowableActions = allowableActions;
    }
}
