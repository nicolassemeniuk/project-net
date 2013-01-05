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

 package net.project.resource;

import net.project.persistence.PersistenceException;

/**
 * Provides base services for managing assignments for a space.
 */
public class SpaceAssignmentManager {
    /**
     * Creates an empty assignment manager.
     */
    public SpaceAssignmentManager() {
        // do nothing
    }


    /**
      * Responds to invitation by adding user to space if accepting invitation.
      * If accepting, user is added to space for applicationID,
      * added to team member group, added to principle group for space.
      * Invitation is then set to Accpeted.<br>
      * If rejecting, invitation is set to Rejected.<br>
      * @param spaceID the application space id
      * @param personID the userID of the user resonding to the invitation
      * @param response either "Accepted" or "Rejected"
      * @throws PersistenceException if there is a problem responding to the
      * invitation
      * @deprecated  in favour {@link net.project.resource.SpaceAssignmentManager#storeInvitationResponse(String spaceID ,String personID ,String response);}  
      */

    public void storeInvitationResponse (String spaceID, String personID, String inviteeEmail, String response) throws PersistenceException {
        storeInvitationResponse(spaceID ,personID ,response);
    }

    /**
      * Responds to invitation by adding user to space if accepting invitation.
      * If accepting, user is added to space for applicationID,
      * added to team member group, added to principle group for space.
      * Invitation is then set to Accpeted.<br>
      * If rejecting, invitation is set to Rejected.<br>
      * @param spaceID the application space id
      * @param personID the userID of the user resonding to the invitation
      * @param response either "Accepted" or "Rejected"
      * @throws PersistenceException if there is a problem responding to the
      * invitation
      */

    public void storeInvitationResponse (String spaceID, String personID, String response) throws PersistenceException {

        String invitationCode = null;

        invitationCode = SpaceInvitationManager.getInvitationKey (personID, spaceID);

        if (response.equals (SpaceInvitationManager.ACCEPTED_SPACE_STATUS)) {
            SpaceInvitationManager.acceptInvitation (invitationCode, spaceID, personID);
        
        } else if (response.equals (SpaceInvitationManager.REJECTED_SPACE_STATUS)) {
            SpaceInvitationManager.rejectInvitation (invitationCode, spaceID, personID);
        }

    }

}
