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
|
+--------------------------------------------------------------------------------------*/
package net.project.methodology;

import java.util.Iterator;

import net.project.base.directory.DirectoryException;
import net.project.base.directory.search.ISearchableDirectory;
import net.project.business.BusinessSpace;
import net.project.persistence.PersistenceException;
import net.project.resource.InvitationException;
import net.project.security.domain.UserDomain;
import net.project.space.PersonalSpace;

/**
 * Provides directory facilities for a Methodology Directory
 */
public class MethodologyDirectory extends net.project.resource.SpaceInvitationManager {

	/**
	 * Cached PersonalSpace of the inviter. This is used to determine the
	 * inviter's business spaces.
	 */
	private PersonalSpace personalSpace = null;

	/**
	 * Cached current domain of the inviter. Used to fetch domain directories.
	 */
	private UserDomain currentDomain = null;

	/**
	 * Creates an empty Methodology Directory
	 */
	public MethodologyDirectory() {
		super();
	}

	/**
	 * clear the cached information from this wizard.
	 */
	public void clear() {
		super.clear();
		this.searchableDirectories.clear();
		this.hasLoadedDirectories = false;
	}

	/**
	 * Loads the searchable directories for the inviter. This includes
	 * businesses and domains.
	 * 
	 * @throws DirectoryException
	 *             if there is a problem determining the user's domain's
	 *             directories
	 * @throws PersistenceException
	 *             if there is a problem getting the user's domain
	 */
	protected void loadSearchableDirectories(boolean isIncludeCurrentBusiness) throws DirectoryException, PersistenceException {

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
			// We exclude any directories in the current business space
			BusinessSpace bSpace = (BusinessSpace) it.next();
			if (!bSpace.getID().equals(getUser().getCurrentSpace().getID())) {
				this.searchableDirectories.put(String.valueOf(nextID++), (bSpace).getSearchableDirectory());
			}
		}

		// Get the searchable domain directories
		// Add as ISearchableDirectory values to the HashMap
		if (this.currentDomain.providesSearchableDirectory()) {
			for (Iterator it = this.currentDomain.getSearchableDirectories().iterator(); it.hasNext();) {
				this.searchableDirectories.put(String.valueOf(nextID++), (ISearchableDirectory) it.next());
			}
		}

	}

	/**
	 * Returns the searchable directory corresponding to the specified id.
	 * 
	 * @throws InvitationException
	 *             if this member wizard does nto have a searchable directory
	 *             matching that id
	 */
	protected ISearchableDirectory getSearchableDirectory(String directoryID) throws InvitationException {

		ISearchableDirectory directory = (ISearchableDirectory) this.searchableDirectories.get(directoryID);

		if (directory == null) {
			throw new net.project.resource.InvitationException(
					"Selected directory not found in Project invitation wizard");

		}

		return directory;
	}

	/**
	 * Indicates whether this directory supports Participants.
	 * 
	 * @return false always
	 */
	public boolean isParticipantsSupported() {
		return true;
	}

	/**
	 * Indicates whether this directory supports Org Chart.
	 * 
	 * @return false always
	 */
	public boolean isOrgChartSupported() {
		return false;
	}

	/**
	 * Indicates whether this directory supports Assignments.
	 * 
	 * @return false always
	 */
	public boolean isAssignmentsSupported() {
		return false;
	}

	/**
	 * Indicates whether this directory supports Roles.
	 * 
	 * @return true always
	 */
	public boolean isRolesSupported() {
		return true;
	}
}
