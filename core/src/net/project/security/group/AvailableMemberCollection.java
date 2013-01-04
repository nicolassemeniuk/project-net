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
|   $Revision: 20760 $
|       $Date: 2010-04-27 11:31:27 -0300 (mar, 27 abr 2010) $
|     $Author: dpatil $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.security.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.project.persistence.PersistenceException;
import net.project.resource.Roster;
import net.project.util.StringUtils;

/**
 * Provides a collection of <code>{@link IGroupMember}</code>s where
 * those members are available to be added to a particular group in
 * a particular space.  This collection excludes members of the space who
 * are already members of the group specified by {@link #setGroup}.
 */
public class AvailableMemberCollection
        extends GroupMemberCollection {

    /** 
     * The group for which members of this collection are available to be
     * added. That is, any members of this group are not in this collection.
     */
    private Group group = null;

    /**
     * Creates an empty AvailableMemberCollection.
     */
    public AvailableMemberCollection() {
        super();
    }
    

    /**
     * Sets the current group.
     * This group is used to get its current members, which are filtered
     * out of the available members.
     * @param group the group
     * @see #getGroup
     */
    public void setGroup(Group group) {
        this.group = group;
    }


    /**
     * Returns the current group.
     * @return the current group
     * @see #setGroup
     */
    public Group getGroup() {
        return this.group;
    }


    /**
     * Loads the available members by locating all potential members in the
     * current space and filtering out the members of the group.
     * @throws PersistenceException if there is a problem loading
     * @see AvailableMemberCollection#setGroup
     * @see GroupMemberCollection#setSpace
     */
    public void load() throws PersistenceException {
        GroupMemberCollection groupMembers = new GroupMemberCollection();
        GroupMemberCollection spaceMembers = new GroupMemberCollection();
        GroupMemberCollection availableMembers = new GroupMemberCollection();
        List<Group> roles = new ArrayList<Group>();
        IGroupMember spaceMember = null;

        // Clear this collection before loading
        clear();

        // Load all groups and persons in current space
        fetchGroups(spaceMembers);
        fetchPersons(spaceMembers);

        // Get members of group
        // Existing group members are excluded from the available list
        groupMembers.addAll(getGroup().getMembers());
        
        // Add in the group itself so that it is also removed
        // (don't want user to add a group to itself!)
        // (Validation catches this scenario though)
        groupMembers.add(getGroup());

        // Iterate over all space members
        // Add those not in the group to the list of available members
        Iterator spaceMemberIt = spaceMembers.iterator();
        while (spaceMemberIt.hasNext()) {
            spaceMember = (IGroupMember) spaceMemberIt.next();

            // If group does not contain space member
            // then add it to our list of available members
            if (!groupMembers.contains(spaceMember)) {
                availableMembers.add(spaceMember);
            }
        }

        // create a collection of roles from availableMember collection
        for(Object object : availableMembers){
        	Group group;
        	if (object instanceof Group) {
        		group = (Group) object;				
	        	if(!StringUtils.equals(group.getGroupTypeID().getID(), GroupTypeID.PRINCIPAL.getID())
	        			&& !StringUtils.equals(group.getGroupTypeID().getID(), GroupTypeID.EVERYONE.getID()))
	        		roles.add(group);
	        }
        }
        // sort the roles collection in alphabatical order of their name
        Collections.sort(roles);
        // remove all unsorted roles from availableMember collection
        availableMembers.removeAll(roles);
        // add sorted roles to availableMember collection
        availableMembers.addAll(roles);
        
        // Add all available members to this structure
        // Performed at end to ensure structure is empty if any problems
        // occur
        this.addAll(availableMembers);
    }


    /**
     * Loads all Persons in the current space and updates specified collection.
     * @param members the collection to update with <code>Person</code>s in
     * current space.
     * @throws PersistenceException if there is a problem loading
     */
    private void fetchPersons(Collection members) throws PersistenceException {
        Roster roster = new Roster();
        roster.setSpace(getSpace());
        // Load all persons in space
        roster.load();
        members.addAll(roster);
    }


    /**
     * Loads all Groups in the current space and updates specified collection.
     * @param members the collection to update with <code>Group</code>s in
     * current space
     * @throws PersistenceException if there is a problem loading
     */
    private void fetchGroups(Collection members) throws PersistenceException {
        GroupCollection groups = new GroupCollection();
        groups.setSpace(getSpace());
        // Load all available groups in space
        groups.loadAll();
        members.addAll(groups);
    }
}
