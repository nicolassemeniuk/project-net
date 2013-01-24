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

import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

/**
 * The everyone group is a group whose members are defined as all authenticated
 * users in the system.
 *
 * To give an example of what this means, when the Everyone group is added to a
 * project, it implies that everyone in the system now has the ability to access
 * a given project.
 *
 * @author Matthew Flower
 * @since Version 8.1.2
 */
public class EveryoneGroup extends SystemGroup {
    /**
     * Creates a new, empty Group of type specified by the id.
     */
    public EveryoneGroup() {
        super(GroupTypeID.EVERYONE);
    }

    /**
     * Returns the name of this group.
     *
     * @return this group's name
     * @see #setName
     */
    public String getName() {
        return PropertyProvider.get("prm.security.group.type.everyone.name");
    }

    public String getDescription() {
        return PropertyProvider.get("prm.security.group.type.everyone.description");
    }

    /**
     * Indicates whether the specified member is a member of this group.
     *
     * @param potentialMember the member to check
     * @return true if the specified member is a member; false otherwise
     * @throws net.project.persistence.PersistenceException if there is a
     * problem checking the membership
     */
    public boolean isMember(IGroupMember potentialMember) throws PersistenceException {
        return true;
    }

    /**
     * Indicates whether a group member with the specified id is a member of
     * this group.
     *
     * @param memberID the id of the member to check
     * @return true if there is a member with the specified id; false otherwise
     * @throws net.project.persistence.PersistenceException if there is a
     * problem checking the membership
     */
    public boolean isMember(String memberID) throws PersistenceException {
        return true;
    }

    protected void addClassSpecificXMLAttributes(XMLDocument doc) throws XMLDocumentException {
        doc.addAttribute("hideSize", "1");
        doc.addAttribute("hideSendMail", "1");
    }

    /**
     * Checks to see if this Group is removable. System groups are not
     * removable.
     *
     * @return true if this group is removable; false if not
     * @throws GroupException if there is a problem
     * determining if it is removable
     */
    public boolean isRemovable() throws GroupException {
        return true;
    }

    public boolean canAddMembers() {
        return false;
    }
}
