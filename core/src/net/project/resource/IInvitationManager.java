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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.resource;


import java.util.Collection;

import net.project.base.directory.search.ISearchableDirectory;
import net.project.license.LicenseException;
import net.project.notification.NotificationException;
import net.project.persistence.PersistenceException;


/**
 * Specifies methods for adding members to a space/license etc.
 */
public interface IInvitationManager {
    

    /**
     * Commits the invitation.
     */
    public void commit() throws PersistenceException, SpaceInvitationException, NotificationException, LicenseException ; 
            

    /**
     * Returns the list of invitees currently added to this invitation manager. 
     */
    public InviteeList getInviteeList();

    /**
     * Clears out all the instance variables.
     */
    public void clear();

    public ISearchableDirectory getSearchableDirectory();
    
    public void addAllInvitees(Collection inviteeCollection);

    public void addInvitee (Invitee invitee);

    public void addAllMembers(Collection inviteeCollection) throws InvitationException;

    public void addMember (Invitee invitee) throws InvitationException ;

    public String getSearchName();
    
}
								 
