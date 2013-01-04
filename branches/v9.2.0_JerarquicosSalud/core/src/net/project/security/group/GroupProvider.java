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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;
import net.project.resource.RosterEntryType;
import net.project.space.Space;
import net.project.space.SpaceFactory;

import org.apache.log4j.Logger;


/**
 * Provides methods for creating new group objects and locating
 * special groups within a space.
 * This group is required to be instantiated to allow it to cache
 * group types.
 */
public class GroupProvider implements java.io.Serializable {

    /**
     * Cached group types used when constructing groups.
     * These are cached to avoid unnecessary roundtrips when loading a series
     * of groups.
     */
    private GroupTypes groupTypes = null;

    /**
     * Creates an empty GroupProvider.
     * The instantiated class maintains a cache of group types, which is
     * used by most of the methods.  It is most efficient to maintain a single
     * instantiation of this class for as long as possible.
     */
    public GroupProvider() {
        // Nothing
    }


    /**
     * Returns a Group object of the correct type for the specified groupID.
     * The Group is not loaded, but has the specified ID set.
     * @param groupID the id of the group
     * @return the group of appropriate type 
     * @throws GroupException if the group is not found, there is a problem
     * determining the type of the group or a problem instantiating
     * the group
     */
    public Group newGroup(String groupID) throws GroupException {
        GroupTypeID groupTypeID;
        Group group;

        try {
            groupTypeID = getGroupTypeID(groupID);
            group = newGroup(groupTypeID);
            group.setID(groupID);
        
        } catch (PersistenceException pe) {
            throw new GroupException("Unable to create a group: " + pe, pe);
        
        }

        return group;
    }


    /**
     * Returns an IGroupMember based on the specified member type with the
     * specified id.
     * @param rosterEntryType the type of member to return
     * @param memberID the id of the member to assign
     * @return the group member, with specified id set
     * @throws GroupException if there is a problem creating the member, for
     * example the memberID is that of a group but the group type cannot
     * be determined
     */
    public IGroupMember newMember(RosterEntryType rosterEntryType, String memberID) throws GroupException {
        IGroupMember member;

        if (rosterEntryType.equals(RosterEntryType.GROUP)) {
            // GROUP type.  Create a new Group object
            member = this.newGroup(memberID);
        
        } else {
            // PERSON type.  Create a new person object
            Person person = new Person();
            person.setID(memberID);
            member = person;

        }

        return member;
    }


    /**
     * Returns the GroupTypeID object for the group with the specified groupID.
     * @param groupID the id of the group for which we want to know the group
     * type
     * @return the GroupTypeID object for that group's type
     * @throws PersistenceException if there is a problem reading from the
     * database
     */
    private GroupTypeID getGroupTypeID(String groupID) 
            throws PersistenceException, GroupException {

        String groupTypeInternalID;
        GroupTypeID groupTypeID = null;
        DBBean db = new DBBean();
        StringBuffer query = new StringBuffer();

        // Query to find group type id (e.g. 100, 200, 300 etc.) from group table
        query.append("select g.group_type_id ");
        query.append("from pn_group g ");
        query.append("where g.group_id = ? ");

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, groupID);
            db.executePrepared();

            if (db.result.next()) {
                // We found the group type internal id
                // Now get the group type id object
                groupTypeInternalID = db.result.getString("group_type_id");
                groupTypeID = GroupTypeID.forID(groupTypeInternalID);
            
            } else {
                // Couldn't find a row for the specified group
                // This is a problem
                throw new GroupException("Group not found with id: " + groupID);
            
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(GroupProvider.class).error("GroupProvider.getGroupTypeID() threw an SQLException: " + sqle);
            throw new PersistenceException("Group get type operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

        return groupTypeID;
    }


    /**
     * Returns a Group object of the correct type for the specified
     * group type id.
     * @param groupTypeID the id of the group type for which to get the new
     * group.
     * @return the group of specified type
     * @throws GroupException if there is a problem constructing the group
     * for the specified type; the class for that type does not exists, 
     * cannot be instantiated
     */
    public Group newGroup(GroupTypeID groupTypeID) throws GroupException {
        GroupType groupType;
        Group group;

        // Get the group type for this type id
        groupType = getGroupTypes().getGroupType(groupTypeID);

        // Create an instance for the appropriate group type
        try {
            group = (Group) groupType.getGroupClass().newInstance();
        
        } catch (ClassNotFoundException cnfe) {
        	Logger.getLogger(GroupProvider.class).error("GroupProvider.newGroup thew a ClassNotFoundException when trying to create class " + 
                    groupType.getClassName() + ": " + cnfe);
            throw new GroupException("Unable to create a Group: " + cnfe, cnfe);

        } catch (InstantiationException ie) {
        	Logger.getLogger(GroupProvider.class).error("GroupProvider.newGroup thew a InstantiationException when trying to create class " + 
                    groupType.getClassName() + ": " + ie);
            throw new GroupException("Unable to create a Group: " + ie, ie);
        
        } catch (IllegalAccessException iae) {
        	Logger.getLogger(GroupProvider.class).error("GroupProvider.newGroup thew a IllegalAccessException when trying to create class " + 
                    groupType.getClassName() + ": " + iae);
            throw new GroupException("Unable to create a Group: " + iae, iae);
        
        }

        return group;
    }


    /**
     * Returns the Space Administrator group for the specified space.
     * Equivalent to calling <code>getSpaceAdminGroup(space.getID())</code>.
     * @param space the space for which to get the Space Admin group
     * @return the space administrator group, with id set but not loaded
     * @throws GroupException if there is a problem finding the group
     * @see #getSpaceAdminGroup(String)
     */
    public Group getSpaceAdminGroup(Space space) 
            throws GroupException {
        
        return getSpaceAdminGroup(space.getID());
    }


    /**
     * Returns the Space Administrator group for the specified space.
     * @param spaceID the space for which to get the space admin group
     * @return the Space Administrator group with id set, but not loaded
     * @throws GroupException if there is a problem locating or creating
     * the Space Admin group
     */
    public Group getSpaceAdminGroup(String spaceID) 
            throws GroupException {

        String spaceAdminID;
        Group spaceAdminGroup = null;
        DBBean db = new DBBean();
        StringBuffer query = new StringBuffer();

        // Query to find id of group in specified space where that
        // group's type is the Space Admin type
        // and the space owns the group
        query.append("select g.group_id ");
        query.append("from pn_space_has_group shg, pn_group g ");
        query.append("where shg.space_id = ? ");
        query.append("and shg.is_owner = 1 ");
        query.append("and g.group_id = shg.group_id ");
        query.append("and g.group_type_id = ? ");

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, spaceID);
            db.pstmt.setString(++index, GroupTypeID.SPACE_ADMINISTRATOR.getID());
            db.executePrepared();

            if (db.result.next()) {
                // We found a space administrator group for the specified
                // space owned by the space
                spaceAdminID = db.result.getString("group_id");
                spaceAdminGroup = newGroup(GroupTypeID.SPACE_ADMINISTRATOR);
                spaceAdminGroup.setID(spaceAdminID);

            } else {
                // No space administrator group found for the specified space
                throw new GroupException("No Space Administrator group found in space: " + spaceID);

            }

        } catch (SQLException sqle) {
        	Logger.getLogger(GroupProvider.class).error("GroupProvider.getSpaceAdminGroup() threw an SQLException: " + sqle);
            throw new GroupException("Group get Space Administrator Group operation failed: " + sqle, sqle);
        
        } finally {
            db.release();

        }

        return spaceAdminGroup;
    }

    /**
     * Indicates whether the specified person has a principal group defined in
     * the specified space.
     * @param spaceID the space for which to check for a principal group
     * @param personID the person for whom to find a principal group (they would be the owner)
     * @return true if that person has a principal group in that space; false otherwise
     * @throws GroupException if there is a problem instantiating the principal group
     * @throws PersistenceException if there is a problem locating the group
     */
    public boolean hasPrincipalGroup(String spaceID, String personID) throws GroupException, PersistenceException {
        return (getPrincipalGroup(spaceID, personID) != null);
    }

    /**
     * Returns the Principal group in the current space owned by the specified person.
     * @param spaceID the space for which to get the principal group
     * @param personID the person owning the principal group
     * @return the principal group with id set, but not loaded; null if the person
     * has no principal group in the space
     * @throws GroupException if there is a problem instantiating the principal group
     * @throws PersistenceException if there is a problem getting the group
     */
    public Group getPrincipalGroup(String spaceID, String personID) throws GroupException, PersistenceException {

        String groupID;
        Group principalGroup = null;
        DBBean db = new DBBean();
        StringBuffer query = new StringBuffer();

        // Query to find id of group in specified space where that
        // group's type is the principal type
        // and the space owns the group
        // and the group's principal owner is that specified
        query.append("select g.group_id ");
        query.append("from pn_space_has_group shg, pn_group g ");
        query.append("where shg.space_id = ? ");
        query.append("and shg.is_owner = 1 ");
        query.append("and g.group_id = shg.group_id ");
        query.append("and g.group_type_id = ? ");
        query.append("and g.principal_owner_id = ? ");

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, spaceID);
            db.pstmt.setString(++index, GroupTypeID.PRINCIPAL.getID());
            db.pstmt.setString(++index, personID);
            db.executePrepared();

            if (db.result.next()) {
                groupID = db.result.getString("group_id");
                principalGroup = newGroup(GroupTypeID.PRINCIPAL);
                principalGroup.setID(groupID);

            } else {
                // No principal group found for the specified space and person
                // Principal group remains null
            }

        } catch (SQLException sqle) {
            throw new PersistenceException("Group get Principal Group operation failed: " + sqle, sqle);
        
        } finally {
            db.release();

        }

        return principalGroup;
    }

    /**
     * Adds the specified member to all the groups whose IDs are specified.
     * @param member the member to add
     * @param groupIDCollection the group IDs of the groups to which to add
     * the member
     * @throws PersistenceException if there is a problem adding member
     * @throws GroupException if there is a problem loading a group for an id
     */
    public void addMemberToGroups(IGroupMember member, Collection groupIDCollection) 
            throws PersistenceException, GroupException {

        DBBean db = new DBBean();
        
        try {
            db.openConnection();
            db.setAutoCommit(false);
            
            addMemberToGroups(member, groupIDCollection, db);
            
            db.commit();
        
        } catch (SQLException sqle) {
        	Logger.getLogger(GroupProvider.class).error("GroupProvider.addMemberToGroups() threw an SQLException: " + sqle);
            throw new PersistenceException("Role add member operation failed", sqle);

        } finally {
            try {
                db.rollback();
            } catch (SQLException sqle2) {
                // Simply release
            }
            db.release();

        }
    }
    

    /**
     * Adds the specified member to all the groups whose IDs are specified.
     * Performs NO commit or rollback on the DBBean.
     * @param member the member to add
     * @param groupIDCollection the group IDs of the groups to which to add
     * the member
     * @param db the DBBean in which to perform the transaction
     * @throws SQLException if there is a problem adding the member
     * @throws GroupException if there is a problem loading a group for an id
     */
    public void addMemberToGroups(IGroupMember member, Collection groupIDCollection, DBBean db) 
            throws SQLException, GroupException {
        
        Iterator it;
        String groupID;
        Group group;

        it = groupIDCollection.iterator();
        while (it.hasNext()) {
            groupID = (String) it.next();

            group = newGroup(groupID);
            group.addMember(member, false, db);
            
        }
    
    }


    /**
     * Updates the specified group with its owning space information.
     * @param group the group to update
     * @throws GroupException if there is a problem determining the owning
     * space for the group
     */
    void updateWithOwningSpace(Group group) throws GroupException {
        ArrayList groups = new ArrayList();
        groups.add(group);
        updateWithOwningSpace(groups);
    }


    /**
     * Updates any groups in the specified collection with their owning
     * space information.  This is not done automatically due to the performance
     * hit involved in loading all the space details; however, each space
     * is loaded only once.
     * @param groups the collection of <code>{@link net.project.security.group.Group}</code>s to update
     * @throws GroupException if there is a problem determining the owning
     * space for a group
     */
    void updateWithOwningSpace(Collection groups) throws GroupException {
        Group group;
        Space owningSpace;
        ArrayList cachedSpaces = new ArrayList();

        try {
            // Iterate over all groups, locating owning space informaion
            Iterator it = groups.iterator();
            while (it.hasNext()) {
                group = (Group) it.next();

                owningSpace = getOwningSpace(group, cachedSpaces);
                group.setOwningSpace(owningSpace);

            }
        
        } catch (PersistenceException pe) {
            // Error getting owning space
            throw new GroupException("Unable to determine owning space for roles", pe);

        }

    }


   /**
     * Returns the owning space for the specified group, maintaing a cache
     * of spaces.
     * @param group the group to get owning space for
     * @param cachedSpaces the cache of spaces, for performance
     * @return the owning space; cachedSpaces may be updated to include that
     * space
     */
    private Space getOwningSpace(Group group, ArrayList cachedSpaces) throws PersistenceException {
        Space owningSpace = null;
        String owningSpaceID = null;
        StringBuffer query = new StringBuffer();
        DBBean db = new DBBean();

        query.append(GroupDAO.getQueryFetchSpaceForGroup());
        query.append("where shg.group_id = ? and shg.is_owner = 1 ");

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, group.getID());
            db.executePrepared();

            if (db.result.next()) {
                owningSpaceID = db.result.getString("space_id");
            }

            // Look for owning space in cached spaces
            Iterator it = cachedSpaces.iterator();
            while (it.hasNext()) {
                Space nextSpace = (Space) it.next();
                if ( nextSpace.getID().equals(owningSpaceID)) {
                    owningSpace = nextSpace;
                    break;
                }
            }

            // If not found yet, load it and add to cached spaces
            if (owningSpace == null) {
                owningSpace = SpaceFactory.constructSpaceFromID(owningSpaceID);
                owningSpace.load();
                cachedSpaces.add(owningSpace);
            }

        } catch (SQLException sqle) {
            throw new PersistenceException("Error determining owning space for role", sqle);
        
        } finally {
            db.release();
        
        }

        return owningSpace;
    }



    /**
     * Returns the available of group types, loading if necessary.
     * @return the group types
     */
    public GroupTypes getGroupTypes() {
        if (this.groupTypes == null) {
            this.groupTypes = GroupTypes.getAll();
        }
    
        return this.groupTypes;
    }


    /**
     * Inherits groups into a space.
     * Note: Already inherited groups are ignored; specifically, permissions
     * are NOT changed for them.
     * @param space the space into which to inherit the groups
     * @param groupIDCollection the collection of group ids to inherit
     * @param permission the permission to apply to all inherited groups
     * @exception PersistenceException if there is a problem inheriting the
     * groups
     */
    public void inheritGroups(Space space, Collection groupIDCollection, PermissionSelection permission) 
            throws PersistenceException {

        StringBuffer query = new StringBuffer();
        DBBean db = new DBBean();

        query.append("{call security.inherit_group(?, ?, ?)}");

        try {
            // Performs update using a batches callable statement
            
            db.prepareCall(query.toString());
            
            // Iterate over each group id, adding as batched parameters
            // to the callable statement
            Iterator it = groupIDCollection.iterator();
            while (it.hasNext()) {
                String groupID = (String) it.next();

                int index = 0;
                db.cstmt.setString(++index, space.getID());
                db.cstmt.setString(++index, groupID);
                db.cstmt.setString(++index, permission.getID());
                db.cstmt.addBatch();

            }

            // Execute batched calls
            db.executeCallableBatch();
        
        } catch (SQLException sqle) {
            throw new PersistenceException("Role inherit operation failed", sqle);
        

        } finally {
            db.release();

        }

    }

    // bfd:3061
    /**
     * Inherits groups from other space into a space
     * Note: Already inherited groups are ignored; specifically, permissions
     * are NOT changed for them.
     * @param space the space into which to inherit the groups
     * @param groupIDCollection the collection of group ids & related space to inherit
     * @param permission the permission to apply to all inherited groups
     * @exception PersistenceException if there is a problem inheriting the
     * groups
     */
    public void inheritGroupsFromSpace(Space space, Collection groupIDCollection, PermissionSelection permission) 
            throws PersistenceException {

        StringBuffer query = new StringBuffer();
        DBBean db = new DBBean();

        query.append("{call security.inherit_group_from_space(?, ?, ?, ?)}");

        try {
            // Performs update using a batches callable statement
            
            db.prepareCall(query.toString());
            
            // Iterate over each group id, adding as batched parameters
            // to the callable statement
            Iterator it = groupIDCollection.iterator();
            while (it.hasNext()) {
                int index = 0;
                String groupStr = (String) it.next();
                String srcSpace = groupStr.substring(0, groupStr.indexOf('-')); 
                String groupID = groupStr.substring(groupStr.indexOf('-')+1, groupStr.length()); 
                
                db.cstmt.setString(++index, space.getID()); // destination space id
                db.cstmt.setString(++index, srcSpace); // source space id of group to be inherited
                db.cstmt.setString(++index, groupID); // group id to be inherited
                db.cstmt.setString(++index, permission.getID()); // permission to be applied after inheritance.
                db.cstmt.addBatch();

            }

            // Execute batched calls
            db.executeCallableBatch();
        
        } catch (SQLException sqle) {
            throw new PersistenceException("Role inherit operation failed", sqle);
        

        } finally {
            db.release();

        }

    }

    /**
     * For a given group, apply security permissions to all existing objects in
     * the indicated space.
     *
     * @param space a <code>Space</code> object in which the group exists.
     * @param group a <code>Group</code> which has permissions
     * @param permissions
     */
    public void retrofitSecurity(DBBean db, Space space, Group group, PermissionSelection permissions) throws SQLException {
        db.prepareCall("{ call security.retrofit_security_permissions(?,?,?)}");
        db.cstmt.setString(1, space.getID());
        db.cstmt.setString(2, group.getID());
        db.cstmt.setString(3, permissions.getID());
        db.executeCallable();
    }

    /**
     * For a given group, apply security permissions at the module level.
     */
    public void grantModulePermissions(DBBean db, Space space, Group group, PermissionSelection permissions) throws SQLException {
        db.prepareCall("{ call security.grant_module_permissions(?,?,?)}");
        db.cstmt.setString(1, space.getID());
        db.cstmt.setString(2, group.getID());
        db.cstmt.setString(3, permissions.getID());
        db.executeCallable();
    }

    /**
     * Set what permissions a group will receive on objects newly created.
     */
    public void setNewObjectPermissions(DBBean db, Space space, Group group, PermissionSelection permissions) throws SQLException {
        db.prepareCall("{ call security.set_newobject_permissions(?,?,?)}");
        db.cstmt.setString(1, space.getID());
        db.cstmt.setString(2, group.getID());
        db.cstmt.setString(3, permissions.getID());
        db.executeCallable();
    }

    /**
     * Provides constants representing the selection of a permission that
     * may be applied to groups inherit from another space.
     */
    public static class PermissionSelection {
        
        /** Collection of all PermissionSelection objects, used for lookups. */
        private static ArrayList permissionSelections = new ArrayList();

        /** This permission selection's id. */
        private String id = null;

        /**
         * Returns the PermissionSelection for the specified id.
         * @param id the id of the permission to get
         * @return the permission selection; or null if none found for specified id
         */
        public static PermissionSelection forID(String id) {
            boolean isFound = false;
            PermissionSelection permissionSelection = null;

            // Loop over all objects, finding one with matching id
            Iterator it = PermissionSelection.permissionSelections.iterator();
            while (it.hasNext() & !isFound) {
                PermissionSelection nextPermission = (PermissionSelection) it.next();

                if (nextPermission.getID().equals(id)) {
                    permissionSelection = nextPermission;
                    isFound = true;
                }
            }

            return permissionSelection;
        }

        /**
         * Creates a new PermissionSelection with the specified id.
         * Private so that only this class may make constants.
         * @param id
         */
        private PermissionSelection(String id) {
            this.id = id;
            permissionSelections.add(this);
        }

        /**
         * Returns the id of this PermissionSelection.
         * @return the id
         */
        public String getID() {
            return this.id;
        }

        /** No permission. */
        public static final PermissionSelection NONE = new PermissionSelection("none");

        /** View-only permission. */
        public static final PermissionSelection VIEW = new PermissionSelection("view");

        /** Default permission. */
        public static final PermissionSelection DEFAULT = new PermissionSelection("default");

        /** inherited permission. */
        public static final PermissionSelection INHERIT = new PermissionSelection("inherit");
        
    }
}
