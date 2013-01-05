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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Provides convenient methods for validating an invitee to a space/license.
 */
public class InviteeValidator 
    implements IInviteeValidator {

    /**
     * The current list of invitees to validate.
     */
    protected List inviteeList = new ArrayList();

    /**
     * Indicates whether a validation error occurred.
     */
    protected boolean isInvalid = false;
    
    /**
     * List of erroneous Invitees, only available when <code>isInvalid</code>.
     */
    protected List invalidInviteeList = new ArrayList();

    /**
     * Creates an empty InviteeValidator.
     * All validation methods will return positive results.
     */
    public InviteeValidator() {
        // Do nothing
    }

    /**
     * Clears out all values.
     */
    protected void clear() {
        this.inviteeList.clear();
        this.isInvalid = false;
        this.invalidInviteeList.clear();
    }

    /**
     * Indicates whether the last validation action performed resulted
     * in an error.
     * @return true if there was an error with the last validation;
     * false otherwise
     */
    public boolean isInvalid() {
        return this.isInvalid;
    }

    /**
     * Returns an unmodifable list of the invalid invitees.
     * Only available if <code>isInvalid</code>.
     * @return the invalid invtees; each element is an <code>Invitee</code>
     * @throws IllegalStateException if <code>!isInvalid</code>
     * @see #isInvalid
     */
    public List getInvalidInviteeList() {
        if (!this.isInvalid) {
            throw new IllegalStateException("Invalid invitees not available");
        }
        return java.util.Collections.unmodifiableList(this.invalidInviteeList);
    }
    
    /**
     * Indicates whether all the invitees have valid email addresses.
     * Sets <code>isInvalid</code> to true when returning false from
     * this method.
     * @return true if all invitees have valid email addresses.
     * @see net.project.notification.Email#isValidInternetAddress
     */
    public boolean isValidEmail() {

        this.invalidInviteeList.clear();
        this.isInvalid = false;

        // Iterate over all invitees
        // Check each email address; if one is not valid, add the
        // invitee to the error list
        // Note that we iterate over ALL invitees so we can provide
        // a full report
        for (Iterator it = this.inviteeList.iterator(); it.hasNext(); ) {
            Invitee nextInvitee = (Invitee) it.next();
            
            if (!net.project.notification.Email.isValidInternetAddress(nextInvitee.getEmail())) {
                this.invalidInviteeList.add(nextInvitee);
            }
        
        }
    
        if (!this.invalidInviteeList.isEmpty()) {
            this.isInvalid = true;
        }

        return !this.isInvalid;
    }

    /**
     * Indicates whether the person with the specified email address
     * is considered an active person for invitation purposes.
     * Currently defined as non-deleted users.
     * @param email the email address of the person
     * @return true if the person exists and is not deleted; false
     * otherwise
     */
    protected boolean isActivePerson(String email) {
        boolean isActive = false;

        Person person = Person.getInstance(email);
        if (person != null && !person.hasStatus(PersonStatus.DELETED)) {
            isActive = true;
        }

        return isActive;
    }

    /**
     * Formats the current invalid invitees.
     * Each invitee is displayed like: <code>DisplayName (email address) &lt;br&gt;</code>.
     * @see #getInvalidInviteeList
     */
    public String formatInvalidInvitees() {

        StringBuffer result = new StringBuffer();

        for (Iterator it = getInvalidInviteeList().iterator(); it.hasNext(); ) {
            Invitee nextInvitee = (Invitee) it.next();

            result.append(nextInvitee.getDisplayName());
            result.append(" (").append(nextInvitee.getEmail()).append(")");
            result.append("<br>");

        }

        return result.toString();
    }

    //*** IInviteeValidator Interface ***//

    /**
     * Indicates if the person being invited is already invited. 
     * @return false, this default implementation always returns false.
     * The subclasses must override this method to return the correct flag.
     */
    public boolean isAlreadyInvited() {
	return false;
    }
}
