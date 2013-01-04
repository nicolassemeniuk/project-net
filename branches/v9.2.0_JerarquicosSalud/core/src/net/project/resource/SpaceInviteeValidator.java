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
|
+--------------------------------------------------------------------------------------*/
package net.project.resource;

import java.util.Collection;
import java.util.Iterator;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.space.Space;

import org.apache.log4j.Logger;

/**
 * Provides convenient methods for validating an invitee to a space.
 */
public class SpaceInviteeValidator 
    extends InviteeValidator {

    /** 
     * The current space.
     * Affects checking invitation within space.
     */
    private Space space = null;

    /**
     * Indicates whether there is a status available.
     */
    private boolean isInviteeStatusAvailable = false;

    /**
     * The current invitee's status in the current space (if available).
     * Only when validating a single invitee.
     */
    private String inviteeStatus = null;

    
    /**
     * Creates an empty InviteeValidator.
     * All validation methods will return positive results.
     */
    public SpaceInviteeValidator() {
        // Do nothing
    }

    /**
     * Creates an InviteeValidator for a single invitee.
     * @param space the current space
     * @param invitee the invitee to validate
     */
    public SpaceInviteeValidator(Space space, Invitee invitee) {
        this.space = space;
        this.inviteeList.add(invitee);
    }

    /**
     * Creates an InviteeValidator for multiple invitees.
     * @param space the current space
     * @param invitees the invitees to validate
     */
    public SpaceInviteeValidator(Space space, Collection invitees) {
        this.space = space;
        this.inviteeList.addAll(invitees);
    }

    /**
     * Clears out all values.
     */
    protected void clear() {
        super.clear();
	this.space = null;
       // this.inviteeList.clear();
        this.isInviteeStatusAvailable = false;
        this.inviteeStatus = null;
       // this.isInvalid = false;
       // this.invalidInviteeList.clear();

    }

    /**
     * Indicates whether any of the current invitees are already invited.
     * When there is only a single invitee, after calling, invitee status is available.
     * Sets <code>isInvalid</code> to true when returning false from
     * this method.
     * @return true if at least one invitee is already invited to the specified
     * space.
     * @see #getInviteeStatus
     */
    public boolean isAlreadyInvited() {
        
        this.invalidInviteeList.clear();
        this.isInvalid = false;
        String lastInviteeInvitedStatus = null;

        // Iterate over all invitees
        // Check each for presence; if one is already valid, add the
        // invitee to the error list
        // Note that we iterate over ALL invitees so we can provide
        // a full report
        for (Iterator it = this.inviteeList.iterator(); it.hasNext(); ) {
            Invitee nextInvitee = (Invitee) it.next();
        
            boolean isAlreadyInvited = false;
            String invitedStatus = null ;

            nextInvitee.resolvePersonIDForInvitee();

            if (nextInvitee.isInviteeRegisteredPerson()) {

                // Grab their current status from the Invited Users table
		try {
		    invitedStatus = getInvitedStatus(this.space.getID(), nextInvitee.getID());        
		} catch (net.project.persistence.PersistenceException pe) {
		    // Log the exception and let the invited status to remain null, this case gets handled below.
			Logger.getLogger(SpaceInviteeValidator.class).debug("SpaceInviteeValidator.java : isAlreadyInvited() threw PersistenceException.");
                    invitedStatus = null;
		}
                // A person is already invited as follows:
                // their status in pn_invited_user is Accepted or Invited
                // AND they are a real person who is not deleted
                isAlreadyInvited = ( invitedStatus != null &&
                                     (invitedStatus.equals(SpaceInvitationManager.INVITED_SPACE_STATUS) ||
                                      invitedStatus.equals(SpaceInvitationManager.ACCEPTED_SPACE_STATUS)) &&
                                     isActivePerson(nextInvitee.getEmail()) );

                // If they didn't have an invited status check to see if
                // they are an existing person who is a space member
                // It seems that a space creator never receives
                // a PN_INVITED_USER record
                // We assume Space Members are simply "Accepted"
                if (invitedStatus == null) {
    
                    Person existingPerson = net.project.base.DefaultDirectory.lookupByEmail(nextInvitee.getEmail());
    
                    if (existingPerson != null &&
                        this.space.isUserSpaceMember(existingPerson)) {
    
                        invitedStatus = SpaceInvitationManager.ACCEPTED_SPACE_STATUS;
                        isAlreadyInvited = true;
                    }
                }   

            } else {
               isAlreadyInvited = false ;
            }


            if (isAlreadyInvited) {
                this.invalidInviteeList.add(nextInvitee);
                lastInviteeInvitedStatus = invitedStatus;
            }

        }

        // If we have an invited status from the last invitee
        // then we store it in the instance and make it available
        // this is a convenience for the case where a single invitee
        // is being checked
        if (lastInviteeInvitedStatus != null) {
            this.inviteeStatus = lastInviteeInvitedStatus;
            this.isInviteeStatusAvailable = true;
        }

        // If we have an erroneous invitee
        // then return that fact
        if (!this.invalidInviteeList.isEmpty()) {
            this.isInvalid = true;
        }
        
        return this.isInvalid;
    }


    /**
     * Returns the current status of the invitee.
     * The status is only available after calling <code>isAlreadyInvited</code>.
     * @see #isAlreadyInvited
     * @throws IllegalStateException if the invitee status is not
     * available.
     */
    public String getInviteeStatus() {

        if (!this.isInviteeStatusAvailable) {
            throw new IllegalStateException("Invitee status not available");
        }

        return this.inviteeStatus;
    }

    /**
     * Returns the invited status of the specified email address
     * in the specified space.
     * @return the status or null if the email is not invited to
     * the specified space
     * @throws PersistenceException if there is a problem checking
     * the status
     */
    private String getInvitedStatus(String spaceID, String personID) 
            throws PersistenceException {

        String invitedStatus = null;

        StringBuffer query = new StringBuffer();
        query.append("select invited_status ");
        query.append("from pn_invited_users ");
        query.append("where space_id = ? and  person_id = ? ");
        
        DBBean db = new DBBean();

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, spaceID);
            db.pstmt.setString(++index, personID);
            db.executePrepared();
            
            if (db.result.next()) {
                // Grab the status of the invited user
                invitedStatus = db.result.getString("invited_status");
            }
        
        } catch (java.sql.SQLException sqle) {
        	Logger.getLogger(SpaceInviteeValidator.class).error("InviteeValidator.getInvitedStatus() threw an SQLException: " + sqle);
            throw new PersistenceException("Check invitation status operation failed: " + sqle, sqle);
        
        } finally {
            db.release();
        
        }

        return invitedStatus;
    }

}
