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

 package net.project.security.group;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.DBFormat;
import net.project.gui.error.ValidationErrors;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnSpaceHasPerson;
import net.project.hibernate.model.PnSpaceHasPersonPK;
import net.project.hibernate.service.IPnSpaceHasPersonService;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.resource.IRosterEntry;
import net.project.resource.Person;
import net.project.resource.PersonList;
import net.project.resource.RosterEntryType;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;
import net.project.util.Conversion;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

import org.apache.log4j.Logger;

/**
 * Group is the base for loading and storing security groups.
 * To create a new group object for the purposes of loading a group,
 * use the following code. <pre><code>
 * Group group = null;
 * GroupProvider groupProvider = new GroupProvider();
 * group = groupProvider.newGroup("some id");
 * group.load();
 * </code></pre>
 * This will load a group for the id and the type will be correct for the
 * type of group that ID represents.
 */
public abstract class Group implements IRosterEntry, IGroupMember, IXMLPersistence, IJDBCPersistence, Serializable, Comparable {

    /** The ID of the type of group. */
    private GroupTypeID groupTypeID = null;

    /**
     * The space to which the Group belongs (owned or not).
     * This is an optional attribute; it is only set if Groups were loaded
     * based on a space.
     */
    private String spaceID = null;
    /** Indicates whether that space is the owner of this group or not. */
    private boolean isSpaceOwner = false;

    /**
     * The owning space.
     */
    private Space owningSpace;

    /**
     * The id of this group.
     */
    private String groupID = null; 

    /**
     * The display name.
     */
    private String name = null; 

    /**
     * The display description.
     */
    private String description = null; 

    /**
     * Number of members in group, cached for performance reasons.
     */
    private int memberCount = 0;

    /**
     * Indicates whether this is a system group.
     */
    private boolean isSystemGroup = false;

    /**
     * Indicates whether this group has been loaded from persistence store.
     */
    private boolean isLoaded = false;

    /**
     * The Members of this group.
     * This is cached here for performance.
     */
    private GroupMemberCollection members = null;

    /**
     * All the Members of this group, including members of members.
     * This is cached here for performance.
     */
    private GroupMemberCollection allMembers = null;

    /**
     * the current user context.
     */
    private User currentUser = null;

    /**
     * the current space context.
     */
    private Space currentSpace = null;

    /**
     * A GroupProvider used throughout this class.  Cacheing it here
     * means the group types will not be re-loaded more than once.
     */
    private GroupProvider groupProvider = null;

    /**
     * Creates a new, empty Group of type specified by the id.
     * @param groupTypeID the type of the group
     * @param isSystemGroup true if this group is a system group,
     * false if it is not a system group.
     */
    public Group(GroupTypeID groupTypeID, boolean isSystemGroup) {
        this.groupTypeID = groupTypeID;
        this.isSystemGroup = isSystemGroup;
    }

    public Group() {
        //Do nothing
    }
    
    /**
     * Returns the group type id of this group.
     * @return the group type id
     */
    public GroupTypeID getGroupTypeID() {
        return this.groupTypeID;
    }


    /**
     * Set the User context for the Group.
     * @param user the current user
     * @see #getUser
     */
    public void setUser(User user) {
        this.currentUser = user;
    }


    /**
     * Get the User context for the Group.
     * If a User context has not been explicitly set, the User will be returned from the session.
     * @return the current user
     * @see #setUser
     */
    public User getUser() {
        if (this.currentUser == null) {
            return SessionManager.getUser();
        } else {
            return this.currentUser;
        }
    }


    /**
     * Set the Space context for the Group.
     * This is used when storing a group, or checking for a unique name
     * @param space the current space
     * @see #store
     * @see #isUniqueNameInSpace
     */
    public void setSpace(Space space) {
        this.currentSpace = space;
    }


    /**
     * Get the Space context for the Group.
     * If a Space context has not been explicitly set, the Space will be returned from the user's current space in session.
     * @return the current space
     */
    private Space getSpace() {

        Space space;

        if (this.currentSpace == null) {
            if (getUser() != null) {
                space = getUser().getCurrentSpace();
            } else {
                // this will most commonly be when there is no user context -- for example, when sending notifications
                space = null;
            }
        } else {
            space = this.currentSpace;
        }

        return space;
    }


    /**
     * Set the Space id for the Group.
     * @param id the current space id
     * @see #getSpaceID
     */
    protected void setSpaceID(String id) {
        this.spaceID = id;
    }


    /**
     * Get the Space id for the Group.
     * This is not set simply by loading the group; it must be set through
     * other means
     * @return the current space id
     * @see GroupCollection#loadAll
     * @see #setSpaceID
     */
    public String getSpaceID() {
        return this.spaceID;
    }


    /**
     * Specifies whether the space id owns this group.
     * @param isSpaceOwner true if space is owner of group; false if
     * space has inherited group
     */
    public void setSpaceOwner(boolean isSpaceOwner) {
        this.isSpaceOwner = isSpaceOwner;
    }


    /**
     * Indicates whether the space id owns the group.
     * @return true if space owns the group; false if inherited
     */
    public boolean isSpaceOwner() {
        return this.isSpaceOwner;
    }


    /**
     * Sets the owning space of this group.
     * @param space the owning space
     * @see #getOwningSpace
     */
    protected void setOwningSpace(Space space) {
        this.owningSpace = space;
    }


    /**
     * Returns the owning space of this group.
     * The owning space for a group is not automatically determined; it must
     * be set through other means.
     * @return the owning space of this group; or null if not set
     * @see GroupCollection#updateWithOwningSpace
     * @see #setOwningSpace
     */
    public Space getOwningSpace() {
        return this.owningSpace;
    }


    /**
     * Set this group's id.
     * Must be set before calling persistence operations.
     * @param id the id of this group
     * @see #getID
     */
    public void setID(String id) {
        this.groupID = id;
    }


    /**
     * Returns this group's id.
     * @return the id of this group
     * @see #setID
     */
    public String getID() {
        return this.groupID;
    }


    /**
     * Sets this group's name.
     * @param name this group's name
     * @see #getName
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Returns the name of this group.
     * @return this group's name
     * @see #setName
     */
    public String getName() {
        return PropertyProvider.get(this.name);
    }


    /**
     * Sets this group's description.
     * @param description the description
     * @see #getDescription
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Returns this group's description.
     * @return the description
     * @see #setDescription
     */
    public String getDescription() {
        return PropertyProvider.get(this.description);
    }


    /**
     * Sets the current member count.
     * @param memberCount the number of members in this group
     * @see #getMemberCount
     */
    protected void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    /**
     * the member count.
     * @return the number of members in this group
     * @see #setMemberCount
     */
    public int getMemberCount() {
        return this.memberCount;
    }

    /**
     * Specifies whether this group is loaded.
     * @param isLoaded true if this group has been loaded from persistence store;
     * false if it is a new group
     */
    protected void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }


    /**
     * Indicates whether this Group has been loaded from persistent store.
     * @return true if this Group has been loaded; false otherwise
     */
    public boolean isLoaded() {
        return this.isLoaded;
    }


    /**
     * Clears out the cached members in this group so that a subsequent call to
     * {@link #getMembers} will refresh from the database.
     */
    public void clearMembers() {
        this.members = null;
    }

    /**
     * Indicates whether this is a principal group.
     * @return true if this is a principal group; false otherwise
     */
    public boolean isPrincipal() {
        return this.groupTypeID.equals(GroupTypeID.PRINCIPAL);
    }

    /**
     * Indicates whether this is a system group.
     * @return true if this is a system group; false otherwise
     */
    public boolean isSystem() {
        return this.isSystemGroup;
    }


    /**
     * Returns this roster entry's type.
     * @return the entry type
     * @see net.project.resource.RosterEntryType
     */
    public RosterEntryType getRosterEntryType() {
        return RosterEntryType.GROUP;
    }


    /**
     * Returns the group provider used within this object.
     * @return the GroupProvider
     */
    private GroupProvider getGroupProvider() {
        if (this.groupProvider == null) {
            this.groupProvider = new GroupProvider();
        }

        return this.groupProvider;
    }


    /**
     * Returns the Persons who are members of this group (but not sub-groups).
     * @return collection of people where each element is a <code>Person</code>
     */
    public PersonList getPersonMembers() {
        PersonList persons = new PersonList();
        persons.addAll(getMembers(RosterEntryType.PERSON));
        return persons;
    }


    /**
     * Returns the groups who are members of this group (but not sub-groups).
     * @return collection of groups where each element is a <code>Group</code>
     */
    public GroupCollection getGroupMembers() {
        GroupCollection groups = new GroupCollection();
        groups.addAll(getMembers(RosterEntryType.GROUP));
        return groups;
    }


    /**
     * Returns the members of the specified type.
     * @param rosterEntryType the type of member to get
     * @return a collection of group members of the appropriate type
     */
    private GroupMemberCollection getMembers(RosterEntryType rosterEntryType) {
        GroupMemberCollection members = new GroupMemberCollection();
        IGroupMember member = null;

        try {

            // Iterate over members, adding members of appropriate type
            Iterator it = getMembers().iterator();
            while (it.hasNext()) {
                member = (IGroupMember) it.next();

                if (member.getRosterEntryType().equals(rosterEntryType)) {
                    members.add(member);
                }
            }

        } catch (PersistenceException pe) {
            // No members
        }

        return members;
    }


    /**
     * Returns the groups and persons who are members of this group (but not
     * sub-groups).  The members are loaded if necessary (but only once).
     * @return collection of groups or persons where each element is a
     * <code>IGroupMember</code>
     * @throws PersistenceException if there is a problem loading the members
     */
    public GroupMemberCollection getMembers() throws PersistenceException {
        if (this.members == null) {
            loadMembers();
        }

        return this.members;
    }


    /**
     * Returns the persons who are members of this group (or in groups within
     * this group).
     * @return collection of people where each element is a <code>Person</code>
     */
    public PersonList getAllPersonMembers() {
        PersonList persons = new PersonList();
        persons.addAll(getAllMembers(RosterEntryType.PERSON));
        return persons;
    }


    /**
     * Returns the groups who are members of this group (or in groups within this
     * group).
     * @return collection of groups where each element is a <code>Group</code>
     */
    public GroupCollection getAllGroupMembers() {
        GroupCollection groups = new GroupCollection();
        groups.addAll(getAllMembers(RosterEntryType.GROUP));
        return groups;
    }


    /**
     * Returns the groups and persons who are members of this group
     * (including sub-groups).
     * @return collection of groups or persons where each element is a
     * <code>IGroupMember</code>.
     * @throws PersistenceException if there is a problem loading the members
     */
    public GroupMemberCollection getAllMembers() throws PersistenceException {
        if (this.allMembers == null) {
            loadAllMembers();
        }

        return this.allMembers;
    }


    /**
     * Returns the members of the specified type from all the members of
     * this group (including sub-groups).
     * @param rosterEntryType the type of member to get
     * @return a collection of group members of the appropriate type
     */
    private GroupMemberCollection getAllMembers(RosterEntryType rosterEntryType) {
        GroupMemberCollection members = new GroupMemberCollection();
        IGroupMember member = null;

        try {

            // Iterate over members, adding members of appropriate type
            Iterator it = getAllMembers().iterator();
            while (it.hasNext()) {
                member = (IGroupMember) it.next();

                if (member.getRosterEntryType().equals(rosterEntryType)) {
                    members.add(member);
                }
            }

        } catch (PersistenceException pe) {
            // No members

        }

        return members;
    }


    /**
     * Loads the members of this group.
     * Loaded members may be fetched by {@link #getMembers}.
     * @throws PersistenceException if there is a problem loading
     */
    private void loadMembers() throws PersistenceException {
        GroupMemberCollection members = new GroupMemberCollection();

        fetchMemberGroups(members);
        fetchMemberPersons(members);

        this.members = members;
    }


    /**
     * Loads all the members of this group, including members of members.
     * Loaded members may be returned by {@link #getAllMembers}.
     * @throws PersistenceException if there is a problem loading
     */
    private void loadAllMembers() throws PersistenceException {
        GroupMemberCollection members = new GroupMemberCollection();
        Group group = null;

        // First get all the immediate members of this group
        members.addAll(getMembers());

        // Now get all the members of the groups in this group
        Iterator it = getGroupMembers().iterator();
        while (it.hasNext()) {
            group = (Group) it.next();

            members.addAll(group.getAllMembers());
        }

        this.allMembers = members;
    }


    /**
     * Loads the Persons who are members of this group and adds to the specified
     * collection.
     * @param members the collection updated to include the persons in this group
     * @throws PersistenceException if there is a problem loading
     */
    private void fetchMemberPersons(GroupMemberCollection members) throws PersistenceException {
        Person person = null;
        StringBuffer query = new StringBuffer();

        query.append("select p.person_id, p.display_name, p.first_name, p.last_name, p.email, u.username ");
        query.append("from pn_group_has_person ghp, pn_person_view p, pn_user_view u ");
        query.append("where ghp.group_id = ? ");
        query.append("and p.person_id = ghp.person_id and p.record_status = 'A' ");
        query.append("and u.user_id(+) = p.person_id ");
        query.append("order by p.last_name asc, p.first_name asc ");

        DBBean db = new DBBean();

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, getID());
            db.executePrepared();

            while (db.result.next()) {
                person = new Person();
                person.setID(db.result.getString("person_id"));
                person.setDisplayName(db.result.getString("display_name"));
                person.setFirstName(db.result.getString("first_name"));
                person.setLastName(db.result.getString("last_name"));
                person.setUserName(db.result.getString("username"));
                person.setEmail(db.result.getString("email"));
                members.add(person);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(Group.class).error("Group.fetchMemberPersons() failed: " + sqle);
            throw new PersistenceException("Group load operation failed", sqle);

        } finally {
            db.release();
        }

    }


    /**
     * Loads the Groups who are members of this group and adds to the specified
     * collection.
     * @param members the collection updated to include the groups in this group
     * @throws PersistenceException if there is a problem loading
     */
    private void fetchMemberGroups(GroupMemberCollection members) throws PersistenceException {
        Group group = null;
        StringBuffer query = new StringBuffer();
        GroupProvider groupProvider = getGroupProvider();
        ArrayList bindVariables = new ArrayList();

        if (getSpace() == null) {
            // We have no current space context (for example, we are loading
            // member groups for the purposes of notification)
            // This makes it impossible to flag each member group as being
            // owned or not by any space
            // Therefore, we simply load the groups without any space context
            query.append(GroupDAO.getQueryLoadMemberGroups());
            query.append("and ghg.group_id = ? ");
            bindVariables.add(getID());

        } else {
            // Query to fetch groups that are members of an owning group
            // Indicating whether the current space owns the member groups
            query.append(GroupDAO.getQueryLoadMemberGroupsWithSpace());
            query.append("and ghg.group_id = ? ");
            query.append("and shg.space_id = ? ");
            bindVariables.add(getID());
            bindVariables.add(getSpace().getID());

        }

        DBBean db = new DBBean();

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            Iterator it = bindVariables.iterator();
            while (it.hasNext()) {
                db.pstmt.setString(++index, (String) it.next());
            }
            db.executePrepared();

            while (db.result.next()) {
                group = groupProvider.newGroup(GroupTypeID.forID(db.result.getString("group_type_id")));
                GroupDAO.populateGroup(db.result, group);
                members.add(group);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(Group.class).error("Group.fetchMemberGroups() failed: " + sqle);
            throw new PersistenceException("Group load operation failed", sqle);

        } catch (GroupException ge) {
        	Logger.getLogger(Group.class).error("Group.fetchMemberGroups() failed: " + ge);
            throw new PersistenceException("Group load operation failed", ge);

        } finally {
            db.release();
        }

    }


    /**
     * Indicates whether the specified member is a member of this group.
     * @param potentialMember the member to check
     * @return true if the specified member is a member; false otherwise
     * @throws PersistenceException if there is a problem checking the membership
     */
    public boolean isMember(IGroupMember potentialMember) throws PersistenceException {
        return isMember(potentialMember.getID());
    }


    /**
     * Indicates whether a group member with the specified id is a member of this group.
     * @param memberID the id of the member to check
     * @return true if there is a member with the specified id; false otherwise
     * @exception PersistenceException if there is a problem checking the membership
     */
    public boolean isMember(String memberID) throws PersistenceException {
        IGroupMember member = null;
        boolean isFound = false;

        // Iterate over all members
        // Stop only when a member with matching id is found
        Iterator it = getMembers().iterator();
        while (it.hasNext() & !isFound) {
            member = (IGroupMember) it.next();

            if (member.getID().equals(memberID)) {
                isFound = true;
            }
        }

        return isFound;
    }


    /**
     * Removes the specified member from this Group and clears the member collection.
     * The next call to {@link #getMembers} will re-load and exclude the removed
     * member.
     * @param member the member to remove from the group.
     * @throws PersistenceException if there is a problem removing the member
     */
    public void removeMember(IGroupMember member) throws PersistenceException {
        removeMember(member, true);
    }


    /**
     * Removes all the members from this Group.
     * @param groupIDs the ids of the groups to remove
     * @param personIDs the ids of the persons to remove
     * @throws GroupException if there is a problem removing
     * @throws PersistenceException if there is a problem removing the members
     */
    public void removeMembers(String[] groupIDs, String[] personIDs) 
            throws GroupException, PersistenceException {

        GroupMemberCollection membersToRemove = buildMemberCollection(groupIDs, personIDs);

        Iterator it = membersToRemove.iterator();
        while (it.hasNext()) {
            removeMember((IGroupMember) it.next(), false);
        }

        clearMembers();

    }


    /**
     * Removes the specified member from this Group.
     * @param member the member to remove from the group.
     * @param isClearAfterRemove true means the member collection will be cleared
     * after removing so that the members will be re-loaded on next call to
     * {@link #getMembers}; false means the member collection will not be
     * cleared - subsequent calls to {@link #getMembers} will be stale
     * @throws PersistenceException if there is a problem removing the member
     */
    public void removeMember(IGroupMember member, boolean isClearAfterRemove) throws PersistenceException {
        DBBean db = new DBBean();
        StringBuffer query = new StringBuffer();

        // Final check to make sure there is another Person in Space Admin role
        // This should have been caught by validation
        if (getGroupTypeID().equals(GroupTypeID.SPACE_ADMINISTRATOR) &&
                !isOtherPersonMember(member)) {
            throw new PersistenceException(PropertyProvider.get("prm.security.group.group.removemember.message"));
        }

        // Get appropriate remove query based on member type
        if (member.getRosterEntryType().equals(RosterEntryType.GROUP)) {
            query.append(GroupDAO.getQueryRemoveGroup());

        } else {
            query.append(GroupDAO.getQueryRemovePerson());

        }

        try {
            int index = 0;
            db.prepareStatement(query.toString());    
            db.pstmt.setString(++index, getID());
            db.pstmt.setString(++index, member.getID());
            db.executePrepared();

        } catch (SQLException sqle) {
        	Logger.getLogger(Group.class).error("Group.removeMember() failed: " + sqle);
            throw new PersistenceException("Group remove member operation failed", sqle);

        } finally {
            db.release();

        }

        if (isClearAfterRemove) {
            this.members = null;
        }

    }


    /**
     * Adds the specified member to this Group and clears the member collection.
     * The next call to {@link #getMembers} will re-load and include the added
     * member.
     * @param member the member to add to the group.
     * @throws PersistenceException if there is a problem adding the member
     */
    public void addMember(IGroupMember member) throws PersistenceException {
        addMember(member, true);
    }


    /**
     * Adds all the members to this Group.
     * @param groupIDs the ids of the groups to add
     * @param personIDs the ids of the persons to add
     * @throws GroupException if there is a problem adding
     * @throws PersistenceException if there is a problem adding the members
     */
    public void addMembers(String[] groupIDs, String[] personIDs) 
            throws GroupException, PersistenceException {

        GroupMemberCollection membersToAdd = buildMemberCollection(groupIDs, personIDs);

        Iterator it = membersToAdd.iterator();
        while (it.hasNext()) {
            addMember((IGroupMember) it.next(), false);
        }

        clearMembers();

    }


    /**
     * Adds the specified member to this Group.
     * @param member the member to add to the group.
     * @param isClearAfterAdd true means the member collection will be cleared
     * after adding so that the members will be re-loaded on next call to
     * {@link #getMembers}; false means the member collection will not be
     * cleared - subsequent calls to {@link #getMembers} will be stale
     * @throws PersistenceException if there is a problem adding the member
     */
    protected void addMember(IGroupMember member, boolean isClearAfterAdd) throws PersistenceException {
        DBBean db = new DBBean();

        try {
            addMember(member, isClearAfterAdd, db);

        } catch (SQLException sqle) {
        	Logger.getLogger(Group.class).error("Group.addMember() failed: " + sqle);
            throw new PersistenceException("Group add member operation failed", sqle);

        } finally {
            db.release();

        }

    }


    /**
     * Adds the specified member to this Group using the specified DBBean.
     * @param member the member to add to the group.
     * @param isClearAfterAdd true means the member collection will be cleared
     * after adding so that the members will be re-loaded on next call to
     * {@link #getMembers}; false means the member collection will not be
     * cleared - subsequent calls to {@link #getMembers} will be stale
     * @param db the DBBean to use
     * @throws SQLException if there is a problem adding the member
     */
    protected void addMember(IGroupMember member, boolean isClearAfterAdd, DBBean db) throws SQLException {
        StringBuffer query = new StringBuffer();

        // Get appropriate add query based on member type
        if (member.getRosterEntryType().equals(RosterEntryType.GROUP)) {
            query.append(GroupDAO.getQueryAddGroup());

        } else {
            query.append(GroupDAO.getQueryAddPerson());

        }

        int index = 0;
        db.prepareStatement(query.toString());    
        db.pstmt.setString(++index, getID());
        db.pstmt.setString(++index, member.getID());
        db.executePrepared();

        if (isClearAfterAdd) {
            this.members = null;
        }
    }


    /**
     * Builds a member collection from the specified groupIDs and personIDs.
     * @param groupIDs the array of group IDs
     * @param personIDs the array of personIDs
     * @return the collection of <code>IGroupMember</code>s
     * @throws GroupException if there is a problem building the mbmer collection
     */
    private GroupMemberCollection buildMemberCollection(String[] groupIDs, String[] personIDs) throws GroupException {
        GroupMemberCollection members = new GroupMemberCollection();
        GroupProvider groupProvider = getGroupProvider();

        if (groupIDs != null) {
            // Add all Groups as members to collection
            for (int i = 0; i < groupIDs.length; i++) {
                members.add(groupProvider.newMember(RosterEntryType.GROUP, groupIDs[i]));
            }
        }

        if (personIDs != null) {
            // Add all Persons as members to collection
            for (int i = 0; i < personIDs.length; i++) {
                members.add(groupProvider.newMember(RosterEntryType.PERSON, personIDs[i]));
            }
        }

        return members;
    }


    //
    // Implementing IRecipient
    //


    /**
     * Returns this Group's email recipient name.
     * Used when listing a group as the recipient of an email
     * @return the display name
     */
    public String getRecipientName() {
        String name = null;

        try {
            if (isOwnedBySpace()) {
                name = getName();
            } else {
                name = getOwningSpace().getName() + " : " + getName();
            }
        } catch (GroupException ge) {
            name = getName();
        }

        return name;
    }


    /**
     * Returns this Group's email addresses, including all member groups.
     * @return a collection of email addresses of the form "Display Name <user@domain.com>"
     */
    public java.util.Collection getRecipientEmailAddresses() {
        Collection c = null;

        try {
            c = getMembers().getEmailAddresses();

        } catch (PersistenceException pe) {
            // No email addresses
            c = java.util.Collections.EMPTY_LIST;

        }

        return c;
    }


    //
    // Implementing IXMLPersistence
    //

    /**
     * Get the XML representation of the group.
     * @return the xml, including the version tag
     */
    public String getXML() {
        try {
            return getXMLDocument().getXMLString();
        } catch (XMLDocumentException xde) {
            return "";
        }
    }


    /**
     * Converts the object to XML representation without the XML version tag.
     * This method returns the object as XML text.
     * @return XML representation
     */
    public String getXMLBody() {
        try {
            return getXMLDocument().getXMLBodyString();
        } catch (XMLDocumentException xde) {
            return "";
        }
    }


    /**
     * Converts this Group to an XMLDocument.
     * @return the xml document representing this Group
     */
    private XMLDocument getXMLDocument() throws XMLDocumentException {
        XMLDocument doc = new XMLDocument();

        doc.startElement("group");
        appendXMLAttributes(doc);        
        doc.endElement();

        return doc;
    }


    /**
     * Updates the XMLDocument with the attributes of a Group.
     * @param doc the document to update
     * @throws XMLDocumentException if there is a problem adding to the document
     */
    private void appendXMLAttributes(XMLDocument doc) throws XMLDocumentException {

        if (getSpaceID() != null) {
            doc.startElement("Space");
            doc.addElement("id", getSpaceID());
            doc.addElement("isOwner", Conversion.booleanToInteger(isSpaceOwner()));
            doc.endElement();
        }

        if (getOwningSpace() != null) {
            doc.startElement("OwningSpace");
            doc.addXMLString(getOwningSpace().getXMLProperties());
            doc.endElement();
        }

        doc.addElement("id", getID());
        doc.addElement("group_name", getName());
        doc.addElement("group_desc", getDescription());
        doc.addElement("is_principal", Conversion.booleanToInteger(isPrincipal()));
        doc.addElement("is_system", Conversion.booleanToInteger(isSystem()));
        doc.addElement("size", new Integer(getMemberCount()));

        addClassSpecificXMLAttributes(doc);
        doc.addXMLString(getRosterEntryType().getXMLBody());

    }

    protected void addClassSpecificXMLAttributes(XMLDocument doc) throws XMLDocumentException {
    }

    //
    // Implementing IJBCPersistence
    //


    /**
     * Load the group from  database persistence.
     * @throws PersistenceException if there is a problem loading
     */
    public void load() throws PersistenceException {
        StringBuffer query = new StringBuffer();

        query.append(GroupDAO.getQueryLoadGroup());
        query.append("where group_id = ? ");

        setLoaded(false);

        DBBean db = new DBBean();

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, getID());
            db.executePrepared();

            if (db.result.next()) {
                GroupDAO.populateGroup(db.result, this);

            } else {
            	Logger.getLogger(Group.class).debug("Group.load() failed to find group with id: " + getID());
                throw new PersistenceException("Error loading Group");

            }

        } catch (SQLException sqle) {
        	Logger.getLogger(Group.class).error("Group.load() failed " + sqle);
            throw new PersistenceException("Error loading Group: " + sqle, sqle);

        } finally {
            db.release();

        }
    }


    /**
     * Store any changes to this Group.
     * It creates a new group if the id is currently null, otherwise modifies
     * the existing group for this id.
     * @throws PersistenceException if there is a problem storing
     */
    public void store() throws PersistenceException {
    	  DBBean db = new DBBean();                                                                                                                                                                               
           
	        // Abort if we are creating a non-principal group and the name is not                                                                                                                         
	        // unique within the current space                                                                                                                                                            
	        // This  method should not be called if the validation fails                                                                                                                                  
	        // because of a non-unique name                                                                                                                                                               
	        // @see #validateUniqueName                                                                                                                                                                   
	        if (getID() == null && !isPrincipal() && !isUniqueNameInSpace()) {                                                                                                                            
	            throw new PersistenceException("Cannot create group with same name as existing group.");                                                                                                  
	        }                                                                                                                                                                                             
	                                                                                                                                                                                                      
	        try {                                                                                                                                                                                         
	            int groupIDIndex = 0;                                                                                                                                                                     
	            int index = 0;                                                                                                                                                                            
	            // bfd-4516 fix, if "everyone" global role is added to business                                                                                                                           
	            if ("Everyone".equals(getName())){                                                                                                                                                        
	                List<PnPerson> personList = ServiceFactory.getInstance().getPnPersonService().getAllPersonsIds(Integer.valueOf(getUser().getID()));                                                   
	                if (personList != null){                                                                                                                                                              
	                        for(PnPerson p : personList){                                                                                                                                                 
	                                // saves space_has_person for all users because global visibility is selected                                                                                         
	                                IPnSpaceHasPersonService spaceHasPersonService = ServiceFactory.getInstance().getPnSpaceHasPersonService();                                                           
	                                PnSpaceHasPerson spaceHasPerson = new PnSpaceHasPerson(new PnSpaceHasPersonPK(Integer.valueOf(getSpace().getID()), p.getPersonId()), "member", "A");                  
	                                spaceHasPersonService.saveOrUpdateSpaceHasPerson(spaceHasPerson);                                                                                                             
	                        }                                                                                                                                                                             
	                }                                                                                                                                                                                     
	            }                                                                                                                                                                                         
	            db.prepareCall ("{call SECURITY.STORE_GROUP (?,?,?,?,?,?,?,?)}");                                                                                                                         
	            db.cstmt.setString(++index, getName());                                                                                                                                                   
	            db.cstmt.setString(++index, getDescription());                                                                                                                                            
	            db.cstmt.setString(++index, DBFormat.bool(isPrincipal()));                                                                                                                                
	            db.cstmt.setString(++index, DBFormat.bool(isSystem()));                                                                                                                                   
	            db.cstmt.setString(++index, getGroupTypeID().getID());                                                                                                                                    
	            db.cstmt.setString(++index, getUser().getID());                                                                                                                                           
	            db.cstmt.setString(++index, getSpace().getID());                                                                                                                                          
	                                                                                                                                                                                                      
	            // Specify NULL if id is null                                                                                                                                                             
	            groupIDIndex = ++index;                                                                                                                                                                   
	            if (getID() == null) {                                                                                                                                                                    
	                db.cstmt.setNull(groupIDIndex, java.sql.Types.VARCHAR);                                                                                                                               
	            } else {                                                                                                                                                                                  
	                db.cstmt.setString(groupIDIndex, this.groupID);                                                                                                                                       
	            }                                                                                                                                                                                         
	                                                                                                                                                                                                      
	            db.cstmt.registerOutParameter(groupIDIndex, java.sql.Types.VARCHAR);                                                                                                                      
	            db.executeCallable();                                                                                                                                                                     
	                                                                                                                                                                                                      
	            setID(db.cstmt.getString(groupIDIndex));                                                                                                                                                  
	                                                                                                                                                                                                      
	        } catch (SQLException sqle) {                                                                                                                                                                 
	                Logger.getLogger(Group.class).error("Group.store() operation failed: " + sqle);                                                                                                       
	            throw new PersistenceException("Group store operation failed", sqle);                                                                                                                     
	                                                                                                                                                                                                      
	        } finally {                                                                                                                                                                                   
	            db.release();                                                                                                                                                                             
	                                                                                                                                                                                                      
	        }                                                                                                                                                                                             


    }


    /**
     * Removes this group.
     * @throws PersistenceException if there was a problem removing or
     * the group may not be removed
     * @see #validateRemove
     */
    public void remove() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            if (isRemovable()) {
                int index = 0;
                db.prepareCall ("{call SECURITY.REMOVE_GROUP_PERMISSION(?,?)}");
                db.cstmt.setString(++index, getID());
                db.cstmt.setInt(++index, 0);
                db.executeCallable();
            } else {
                throw new PersistenceException("Role remove operation failed; role may not be removed");
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(Group.class).debug("Group.remove() failed " + sqle);
            throw new PersistenceException("Role remove operation failed", sqle);
        } catch (GroupException ge) {
            throw new PersistenceException("Role remove operation failed", ge);
        } finally {
            db.release();
        }
    }


    /**
     * Removes this group from the current space, where the current space
     * has inherited the group.
     * @throws GroupException if there is a problem determining the owning
     * space of this group or if the group is owned by the current space.
     * @throws PersistenceException if there is a problem removing the inheritance
     */
    public void removeInheritance() throws GroupException, PersistenceException {
        DBBean db = new DBBean();
        StringBuffer query = new StringBuffer();

        if (isOwnedBySpace()) {
            throw new GroupException("Cannot de-inherit a group from the space that owns it");
        }

        // Get query to remove group inheritance from current space
        query.append("{call security.remove_inherited_group(?, ?)}");

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, getSpace().getID());
            db.pstmt.setString(++index, getID());
            db.executePrepared();

        } catch (SQLException sqle) {
            throw new PersistenceException("Group remove inheritance operation failed", sqle);

        } finally {
            db.release();

        }

    }

    /**
     * Indicates whether this group's name is unique within the current space.
     * @return true if there is another group with the same name; false otherwise
     * @throws PersistenceException if there is a problem checking
     */
    private boolean isUniqueNameInSpace() throws PersistenceException {
        boolean isUniqueName = false;
        GroupCollection groups = new GroupCollection();

        groups.setSpace(getSpace());
        groups.loadOwned();

        // Check to see if there is a group with this name already
        // If this group already exists (its id is non-null) then it will
        // be ignored during the comparison
        if (groups.containsGroupWithName(getName(), getID())) {
            isUniqueName = false;
        } else {
            isUniqueName = true;
        }

        return isUniqueName;
    }


    /**
     * Indicates whether this group is owned by the current space context.
     * @return true if the current space context is the owning space for the group;
     * false otherwise (that is, the current space context has probably inherited
     * the group, or the it doesn't even see the group)
     * @throws GroupException if there is a problem determining the owning
     * space for this group.
     */
    public boolean isOwnedBySpace() throws GroupException {
        boolean isOwnedBySpace = false;
        GroupProvider groupProvider = getGroupProvider();

        if (getSpace() == null) {
            throw new GroupException("Current space is null");
        }

        // Update this group with its owning space if we don't already have it
        if (getOwningSpace() == null) {
            groupProvider.updateWithOwningSpace(this);
        }

        // If this group's owning space ID (getOwningSpace()) is equal to the current space
        // context id (getSpace()) then it is owned by the current space
        if (getOwningSpace().getID().equals(getSpace().getID())) {
            isOwnedBySpace = true;
        }

        return isOwnedBySpace;
    }


    //
    // Validation Routines
    //


    /**
     * Validates that all information in this Group is suitable for storing.
     * Checks the name is non-null and is unique within the current space.
     * @throws PersistenceException if there is a problem validating
     */
    public void validate() throws PersistenceException {
        validateName();
        validateUniqueName();
    }


    /**
     * Validates that name is not null or empty
     */
    private void validateName() {
        if (getName() == null || getName().trim().length() == 0) {
            this.validationErrors.put("name", PropertyProvider.get("prm.security.group.group.validatename.message"));
        }
    }


    /**
     * Validates that the name is unique within the space.
     * @exception PersistenceException if there is a problem validating
     */
    private void validateUniqueName() throws PersistenceException {
        if (!isPrincipal() && !isUniqueNameInSpace()) {
            this.validationErrors.put("name", PropertyProvider.get("prm.security.group.group.validateuniquename.message"));
        }
    }


    /**
     * Checks that the specified member may be added to this Group.
     * A member may be added if it is a Person or is a Group where adding
     * that Group will not cause a cyclic relationship.
     * @param member the member being addeed
     * @exception GroupException if there is a problem validating
     */
    public void validateAddMember(IGroupMember member) throws GroupException {

        if (member.getRosterEntryType().equals(RosterEntryType.GROUP)) {

            if (member.getID().equals(this.getID())) {
                this.validationErrors.put("member", PropertyProvider.get("prm.security.group.group.validateaddmember.message1"));

            } else {
                try {
                    Group group = (Group) member;
                    group.load();

                    // Check to see if group being added contains this group
                    // as one of its descendents.  If so, this would result in a 
                    // cyclic relationship (that is, A contains B contains A).
                    if (group.getMembers().containsDescendent(this)) {
                        this.validationErrors.put("member", PropertyProvider.get("prm.security.group.group.validateaddmember.message2", new Object[]{group.getName()}));
                    }

                } catch (PersistenceException pe) {
                    throw new GroupException("Add member validate operation failed", pe);

                } catch (GroupException ge) {
                    throw new GroupException("Add member validate operation failed", ge);

                }

            }

        }

        if (getGroupTypeID().equals(GroupTypeID.TEAM_MEMBER) &&
                member.getRosterEntryType().equals(RosterEntryType.PERSON)) {

            // This is a Team Member group and a Person is being added
            // Cannot add Persons to Team Members

            this.validationErrors.put("member", PropertyProvider.get("prm.security.group.group.validateaddmember.message3"));
        }


    }


    /**
     * Validates that all the groups and all the persons given by the
     * arrays of ids may be added to this group.
     * Sets the first error only.
     * @param groupIDs the array of group IDs
     * @param personIDs the array of personIDs
     * @throws GroupException if there is a problem validating
     */
    public void validateAddMember(String[] groupIDs, String[] personIDs) throws GroupException {
        GroupMemberCollection membersToAdd = buildMemberCollection(groupIDs, personIDs);

        // Iterate over those members and validate each one, stopping when the
        // first error occurs
        Iterator it = membersToAdd.iterator();
        while (it.hasNext() & !hasErrors()) {
            validateAddMember((IGroupMember) it.next());
        }

    }


    /**
     * Checks that the specified member may be removed from this group.
     * If this group is a Space Administrator group, then if the member is
     * a Person, it can only be removed if it is not the last person.
     * For Team Member groups, only non-principals may be added / removed.
     * @param member the member being removed
     */
    public void validateRemoveMember(IGroupMember member) {


        if (getGroupTypeID().equals(GroupTypeID.SPACE_ADMINISTRATOR) &&
                member.getRosterEntryType().equals(RosterEntryType.PERSON)) {

            // If this is a Space Administrator group
            // and a Person is being removed
            // Check that there is at least one other person in the role

            if (!isOtherPersonMember(member)) {
                this.validationErrors.put("member", PropertyProvider.get("prm.security.group.group.validateremovemember.spaceadminrole.message"));
            }

        } else if (getGroupTypeID().equals(GroupTypeID.TEAM_MEMBER) &&
                member.getRosterEntryType().equals(RosterEntryType.PERSON)) {

            // This is a Team Member group and a Person is being removed
            // Cannot remove Persons from Team Members

            this.validationErrors.put("member", PropertyProvider.get("prm.security.group.group.validateremovemember.teammember.message"));
        }

    }


    /**
     * Validates that all the groups and all the persons given by the
     * arrays of ids may be removed from this group.
     * Sets the first error only.
     * @param groupIDs the array of group IDs
     * @param personIDs the array of personIDs
     * @throws GroupException if there is a problem validating
     */
    public void validateRemoveMember(String[] groupIDs, String[] personIDs) throws GroupException {
        GroupMemberCollection membersToRemove = buildMemberCollection(groupIDs, personIDs);

        // Iterate over those members and validate each one, stopping when the
        // first error occurs
        Iterator it = membersToRemove.iterator();
        while (it.hasNext() & !hasErrors()) {
            validateRemoveMember((IGroupMember) it.next());
        }

    }


    /**
     * Validates that this group may be removed.
     * System groups may not be removed.
     */
    public void validateRemove() {
        try {
            if (!isRemovable()) {
                this.validationErrors.put("group", PropertyProvider.get("prm.security.group.group.validateremove.message"));
            }

        } catch (GroupException ge) {
            this.validationErrors.put("group", PropertyProvider.get("prm.security.group.group.validateremove.message"));

        }
    }


    /**
     * Indicates whether there is a Person member in this group that is different
     * to the specified member.
     * @param member 
     * @return true if there is another Person member in this group; false otherwise
     */
    private boolean isOtherPersonMember(IGroupMember member) {
        boolean isOtherPersonMember = false;
        PersonList currentMembers = getPersonMembers();

        // Iterate over current members looking for a person
        // with a different id
        Iterator it = currentMembers.iterator();
        while (it.hasNext() & !isOtherPersonMember) {
            Person person = (Person) it.next();
            if (!person.getID().equals(member.getID())) {
                isOtherPersonMember = true;
            }
        }

        return isOtherPersonMember;
    }


    /**
     * Checks to see if this Group is removable.
     * System groups are not removable.
     * @return true if this group is removable; false if not
     * @exception GroupException if there is a problem determining if it is
     * removable
     */
    public boolean isRemovable() throws GroupException {
        boolean isRemovable = false;

        try {
            // Load this group if necessary
            if (!isLoaded()) {
                load();
            }

            if (!isSystem()) {
                // Only removable if not system group
                isRemovable = true;
            }

        } catch (PersistenceException pe) {
            throw new GroupException("Group may not be removed", pe);

        }

        return isRemovable;
    }


    //
    // Implementing IErrorProvider
    //

    /** Validation errors for current validation series. */
    private ValidationErrors validationErrors = new ValidationErrors();

    /**
     * Clears all errors.
     */
    public void clearErrors() {
        validationErrors.clearErrors();
    }

    /**
     * Indicates whether there are any errors.
     * @return true if there are errors; false otherwise
     */
    public boolean hasErrors() {
        return validationErrors.hasErrors();
    }

    /**
     * Gets the Error Flag for the Field.  This method is used for
     * flagging a field label as having an error.  If an error is present
     * for the field with the specified id, the specified label is returned
     * but formatted to indicate there is an error.  Currently this uses
     * a &lt;span&gt;&lt;/span&gt; tag to specify a CSS class.  If there is no error
     * for the field with the specified id, the label is returned untouched.
     * @param fieldID the id of the field which may have the error
     * @param label the label to modify to indicate there is an error
     * @return the HTML formatted label
     */
    public String getFlagError(String fieldID, String label) {
        return validationErrors.getFlagErrorHTML(fieldID, label);
    }

    /**
     * Gets the Error Message for the Field.
     * @param fieldID  the id of the field for which to get the error message
     * @return the HTML formatted error message
     */
    public String getErrorMessage(String fieldID) {
        return validationErrors.getErrorMessageHTML(fieldID);
    }

    /**
     * Gets the Error Message for the Field.
     * @return HTML formatted error messages
     */
    public String getAllErrorMessages() {
        return validationErrors.getAllErrorMessagesHTML();
    }

    //
    // End of IErrorProvider
    //


    public boolean canAddMembers() {
        return true;
    }
    
    /**
     * Compare the Group object by group name
     * @param  obj the Group object
     * @return int
     */
    public int compareTo(Object obj) {
		Group group =(Group) obj;
		return this.getName().compareToIgnoreCase(group.getName());
	}
}
