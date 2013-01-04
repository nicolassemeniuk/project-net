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
 |   $Revision: 19160 $
 |       $Date: 2009-05-05 18:45:21 -0300 (mar, 05 may 2009) $
 |     $Author: umesha $
 |
 +--------------------------------------------------------------------------------------*/
package net.project.resource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.project.base.directory.DirectoryException;
import net.project.base.directory.search.ISearchableDirectory;
import net.project.base.property.PropertyProvider;
import net.project.business.BusinessSpace;
import net.project.database.DBBean;
import net.project.license.License;
import net.project.license.LicenseException;
import net.project.license.LicenseNotification;
import net.project.license.PersonLicense;
import net.project.license.model.LicenseModel;
import net.project.license.model.UsageLimit;
import net.project.notification.NotificationException;
import net.project.persistence.PersistenceException;
import net.project.security.domain.UserDomain;
import net.project.space.PersonalSpace;

/**
 * Performs processing for associating/inviting persons to a License.
 */
public class LicenseInvitationManager extends AbstractInvitationManager {

    /**
         * Cached PersonalSpace of the inviter. This is used to determine the
         * inviter's business spaces.
         */
    private PersonalSpace personalSpace = null;

    /**
         * Cached current domain of the inviter. Used to fetch domain
         * directories.
         */
    private UserDomain currentDomain = null;

    /**
         * The license to which persons are being invited/associated.
         */
    private License license = null;

    /**
         * The message to be sent to all invitees.
         */
    private String inviteeMessage = null;

    /**
         * Adds the specified invitee to the list of invitees. If the invitee
         * has already been added (that is, there is an invitee with the same
         * email address) it is first removed to ensure we don't have duplicate
         * invitees.
         * 
         * @param invitee
         *                a person being invited
         * @exception InvitationException
         *                    is thrown if the Invitee cannot be added due to
         *                    some constraints
         */

    public void addAllMembers(Collection inviteeCollection) throws InvitationException {

	if (this.license == null) {
	    throw new NullPointerException("License cannot be null");
	}

	for (Iterator it = license.getModelCollection().iterator(); it.hasNext();) {
	    LicenseModel nextModel = (LicenseModel) it.next();

	    if (nextModel instanceof UsageLimit) {
		UsageLimit usageLimit = (UsageLimit) nextModel;

		if (usageLimit.getMaxUsageCount() - usageLimit.getCurrentUsageCount() - super.getInviteeList().size() - inviteeCollection.size() <= 0) {
		    throw new LicenseInvitationException(" Maximum usage count for this license has been exceeded ");
		}

	    }

	}
	super.addAllMembers(inviteeCollection);

    }

    /**
         * Adds the specified invitee to the list of invitees. If the invitee
         * has already been added (that is, there is an invitee with the same
         * email address) it is first removed to ensure we don't have duplicate
         * invitees.
         * 
         * @param invitee
         *                a person being invited
         * @exception InvitationException
         *                    is thrown if the Invitee cannot be added due to
         *                    some constraints
         */
    public void addMember(Invitee invitee) throws InvitationException {

	if (this.license == null) {
	    throw new NullPointerException("License cannot be null");
	}

	for (Iterator it = license.getModelCollection().iterator(); it.hasNext();) {
	    LicenseModel nextModel = (LicenseModel) it.next();

	    if (nextModel instanceof UsageLimit) {
		UsageLimit usageLimit = (UsageLimit) nextModel;

		int count = usageLimit.getMaxUsageCount() - usageLimit.getCurrentUsageCount() - super.getInviteeList().size();

		if (usageLimit.getMaxUsageCount() - usageLimit.getCurrentUsageCount() - super.getInviteeList().size() <= 0) {
		    throw new LicenseInvitationException(" Maximum usage count for this license has been exceeded ");
		}

	    }

	}
	super.addMember(invitee);
    }

    /**
         * Sets the Invitee's Message
         * 
         * @param message
         *                The message
         */
    public void setInviteeMessage(String message) {
	this.inviteeMessage = message;
    }

    /**
         * Returns the Invitee's Message
         * 
         * @return message The message
         */
    public String getInviteeMessage() {
	return this.inviteeMessage;
    }

    /**
         * 
         */
    public void commit() throws PersistenceException, NotificationException, LicenseException {
	// System.out.println("LicenseInvitationManager.java : Executing
        // commit() ....." );
	if (getInviteeList().isEmpty()) {
	    throw new IllegalStateException(PropertyProvider.get("prm.personal.license.key.inviteusers.error.noinvitees.message")); // No
                                                                                                                                        // invitees
                                                                                                                                        // to
                                                                                                                                        // invite.
	}

	List successfulPersonList = new ArrayList();
	DBBean db = new DBBean();

	Iterator iter = this.getInviteeList().iterator();
	while (iter.hasNext()) {
	    Invitee invitee = (Invitee) iter.next();
	    // Check if the invitee is a registered user and in that case
	    // auto associate to this license, otherwise just send the
                // invitation.
	    if (invitee.isInviteeRegisteredUser()) {
		Person nextPerson = Person.getInstance(invitee.getEmail());
		PersonLicense personLicense = new PersonLicense(nextPerson);
		try {
		    personLicense.associate(db, this.license, false);
		    LicenseNotification notification = new LicenseNotification();
		    notification.setMessage(this.getInviteeMessage());
		    notification.notifyUserOfAssociation(license, nextPerson);
		    db.commit();

		} catch (SQLException sqle) {
		    throw new PersistenceException("License load operation failed: " + sqle, sqle);

		} finally {
		    db.release();

		}
	    } else {
		LicenseNotification notification = new LicenseNotification();
		notification.setMessage(this.getInviteeMessage());
		notification.inviteUserToAssociate(this.license, invitee.getEmail());
	    }
	}
    }

    /**
         * Clear the cached information from this manager. This does NOT include
         * the current user and space or the invite URLs. Essentially it
         * prepares this manager for use in the wizard.
         */
    public void clear() {
	super.clear();
    }

    /**
         * Loads the searchable directories for the inviter. This includes
         * businesses and domains.
         * 
         * @throws DirectoryException
         *                 if there is a problem determining the user's domain's
         *                 directories
         * @throws PersistenceException
         *                 if there is a problem getting the user's domain
         */
    protected void loadSearchableDirectories(boolean isIncludeCurrentBusiness) throws net.project.base.directory.DirectoryException, PersistenceException {

	// create the PersonalSpace if it does not exist.
	if (this.personalSpace == null) {
	    this.personalSpace = new PersonalSpace();
	}

	// Load the PersonalSpace from the database if not already loaded.
	if (!this.personalSpace.isLoaded()) {
	    this.personalSpace.setUser(getUser());
	    this.personalSpace.load();
	}

	// Load the user's current domain
	if (this.currentDomain == null) {
	    this.currentDomain = getUser().getUserDomain();
	}

	int nextID = 0;

	// Get the BusinessSpaces this user can see
	// Add as ISearchableDirectory values to the HashMap
	// Where each BusinessSpace is an ISearchableDirectory
	for (Iterator it = this.personalSpace.getBusinessSpaces().iterator(); it.hasNext();) {
	    System.out.println("LicenseInvitationManager : searching business spaces ");
	    this.searchableDirectories.put(String.valueOf(nextID++), ((BusinessSpace) it.next()).getSearchableDirectory());
	}

	// Get the searchable domain directories
	// Add as ISearchableDirectory values to the HashMap
	if (this.currentDomain.providesSearchableDirectory()) {
	    for (Iterator it = this.currentDomain.getSearchableDirectories().iterator(); it.hasNext();) {
		System.out.println("LicenseInvitationManager : searching domain ");
		this.searchableDirectories.put(String.valueOf(nextID++), (ISearchableDirectory) it.next());
	    }
	}

	System.out.println("LicenseInvitationManager : searchableDirectories.size() is " + this.searchableDirectories.size());

    }

    /**
         * Returns the searchable directory corresponding to the specified id.
         * 
         * @throws InvitationException
         *                 if this member wizard does nto have a searchable
         *                 directory matching that id
         */
    protected ISearchableDirectory getSearchableDirectory(String directoryID) throws InvitationException {

	ISearchableDirectory directory = (ISearchableDirectory) this.searchableDirectories.get(directoryID);

	if (directory == null) {
	    throw new net.project.resource.InvitationException("Selected directory not found in License Invitation wizard");

	}

	return directory;
    }

    public void setLicense(License license) {
	this.license = license;

    }

}
