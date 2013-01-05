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

import net.project.persistence.PersistenceException;
import net.project.resource.PersonList;
import net.project.resource.RosterEntryType;
import net.project.security.User;
import net.project.space.Space;

/**
 * A collection of <code>{@link IGroupMember}</code>s.
 */
public class GroupMemberCollection 
        extends ArrayList
        implements net.project.persistence.IXMLPersistence  {

    /** the space context */
    private Space currentSpace = null;
    
    /** the User context */
    private User currentUser = null;

    private boolean isLoaded = false;


    /**
     * Creates an empty GroupMemberCollection.
     */
    public GroupMemberCollection() {
        super();
    }
    
    /**
     * Set the Space context.
     * This clears the collection.
     * @param space the current space
     * @see #getSpace
     */
    public void setSpace(Space space) {
        // new context; we need to clear the list.
        this.clear();
        this.currentSpace = space;
        this.isLoaded = false;
    }


    /**
     * Returns the current space context.
     * @return the current space context
     * @see #setSpace
     */
    protected Space getSpace() {
        return this.currentSpace;
    }


    /**
     * Set the User context.
     * This clears the collection.
     * @param user the current user context
     */
    public void setUser(User user) {
        this.clear();
        this.currentUser = user;
        this.isLoaded = false;
    }


    /**
     * Is the list loaded from persistence.
     * @return true if load, false otherwise.
     */
    public boolean isLoaded() {
        return this.isLoaded;
    }


    /**
     * Returns the Person members.
     * @return collection of IGroupMember
     */
    public PersonList getPersons() {
        PersonList persons = new PersonList();
        
        Iterator it = iterator();
        while (it.hasNext()) {
            IGroupMember member = (IGroupMember) it.next();
            if (member.getRosterEntryType().equals(RosterEntryType.PERSON)) {
                persons.add(member);
            }
        }
        
        return persons;
    }


    /**
     * Returns the Group members.
     * @return collection of IGroupMember
     */
    public GroupCollection getGroups() {
        GroupCollection groups = new GroupCollection();
        
        Iterator it = iterator();
        while (it.hasNext()) {
            IGroupMember member = (IGroupMember) it.next();
            if (member.getRosterEntryType().equals(RosterEntryType.GROUP)) {
                groups.add(member);
            }
        }
        
        return groups;
    }


    /**
     * Indicates whether this collection contains the specified member without
     * checking any descendent roles within this collection.
     * Containment checked by equality of {@link IGroupMember#getID}.
     * @param member the member to look for
     * @return true if the specified member is in this collection; false otherwise
     */
    public boolean contains(IGroupMember member) {
        boolean isFound = false;

        Iterator it = iterator();
        while (it.hasNext() & !isFound) {
            if ( ((IGroupMember) it.next()).getID().equals(member.getID()) ) {
                isFound = true;
            }
        }

        return isFound;
    }


    /**
     * Indicates whether this collection contains the specified member, checking
     * all descendent roles within this collection.
     * Containment checked by equality of {@link IGroupMember#getID}.
     * @param member the member to look for
     * @return true if the specified member is in this collection or a
     * descendent role of this collection; false otherwise
     * @throws GroupException if there is a problem loading any descendents
     * of any groups in this collection
     */
    public boolean containsDescendent(IGroupMember member) throws GroupException {
        boolean isFound = false;

        if (contains(member)) {
            // If this collection contains the member then stop checking
            isFound = true;
        
        } else {
            // Check all groups in this collection

            try {
                // Iterate over all members, finding groups
                // If that group has the specified member as a descendent
                // Then stop processing
                Iterator it = iterator();
                while (it.hasNext() & !isFound) {
                    IGroupMember nextMember = (IGroupMember) it.next();

                    // If member is a group, then check its descendents
                    if (nextMember.getRosterEntryType().equals(RosterEntryType.GROUP)) {
                        if ( ((Group) nextMember).getMembers().containsDescendent(member) ) {
                            isFound = true;
                        }
                    }

                }
            } catch (PersistenceException pe) {
                // Problem getting group members
                // We must abort
                throw new GroupException("Group collection descendent check operation failed");
            
            }
        }

        return isFound;
    }


    /**
     * Returns all the email addresses of all group memebers and each of
     * their members.
     * Each element of the collection is a string.
     * @return the collection of email addresses
     * @see net.project.notification.email.IRecipient#getRecipientEmailAddresses
     */
    public Collection getEmailAddresses() {
        ArrayList emailAddresses = new ArrayList();
        Iterator it = null;

        // First get all Person member email addresses
        it = iterator();
        while (it.hasNext()) {
            IGroupMember nextMember = (IGroupMember) it.next();

            if (nextMember.getRosterEntryType().equals(RosterEntryType.PERSON)) {
                emailAddresses.addAll(nextMember.getRecipientEmailAddresses());
            }

        }

        // Next descend each Group member and get their email addresses
        it = iterator();
        while (it.hasNext()) {
            IGroupMember nextMember = (IGroupMember) it.next();

            if (nextMember.getRosterEntryType().equals(RosterEntryType.GROUP)) {
                emailAddresses.addAll(nextMember.getRecipientEmailAddresses());
            }

        }

        return emailAddresses;
    }


    /**
     * Returns the XML for the groups in this collection, including the
     * version tag.
     * @return the XML representing these groups
     * @throws SQLException 
     */
    public String getXML() throws SQLException {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }


    /**
     * Returns the XML for the group members in this collection, exlcuding the
     * version tag.
     * @return XML representing these groups
     * @throws SQLException 
     */
    public String getXMLBody() throws SQLException {
        StringBuffer xml = new StringBuffer();

        xml.append("<GroupMembers>");

        Iterator it = iterator();
        while (it.hasNext()) {
            xml.append( ((IGroupMember)it.next()).getXMLBody() );
        }

        xml.append("</GroupMembers>\n");
        
        return xml.toString();
    }

}
