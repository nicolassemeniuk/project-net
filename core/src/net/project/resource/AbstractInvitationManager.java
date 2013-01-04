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
|   $Revision: 19927 $
|       $Date: 2009-09-08 14:20:14 -0300 (mar, 08 sep 2009) $
|     $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.project.base.directory.DirectoryException;
import net.project.base.directory.search.ISearchableDirectory;
import net.project.base.property.PropertyProvider;
import net.project.hibernate.model.PnBusiness;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.space.Space;
import net.project.util.TextFormatter;

/**
 * Performs processing for adding team members to a Space/License.
 */
public abstract class AbstractInvitationManager 
    implements IInvitationManager {

    /**
     * The current user who is inviting people.
     */
    protected User user = null;

    /** 
     * The Space to which the new team members are being added.
     */
    protected Space space = null;

    /**
     * The ID of the selected searchable directory.
     */
    protected String directoryID = null;

    /**
     * The selected searchable directory.
     */
    protected ISearchableDirectory searchableDirectory = null;

    /**
     * The searchable directories.
     */
    protected HashMap searchableDirectories = new HashMap();

    /**
     * Indicates whether we've loaded directories before, to 
     * avoid loading more than once.
     */
    protected boolean hasLoadedDirectories = false;

    /** 
     * The name being searched in the directory.
     */
    protected String searchName = null;

    /**
     * The list of invitees to the current space.
     */
    private final InviteeList inviteeList = new InviteeList();
    
    /**
     * Creates a new, empty AbstractInvitationManager.
     */
    public AbstractInvitationManager() {
        // Nothing
    }

    /**
     * sets the current User context.
     * This is the person inviting new team members.
     * @param user the current user
     * @see #getUser
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Returns the user inviting new team members.
     * @return the current user
     * @see #setUser
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Sets the current Space context.
     * This is the space to which team members are to be added.
     * @param space the current space
     * @see #getSpace
     */
    public void setSpace(Space space) {
        this.space = space;
    }

    /**
     * Returns the space to which team members are to be added.
     * @return the current space
     * @see #setSpace
     */
    public Space getSpace() {
        return this.space;
    }


    //
    // Wizard Search Directory stuff
    //

    /**
     * Loads the searchable directories for the project/business/license.
     * @throws net.project.base.directory.DirectoryException if there is a problem determining
     * the user's domain's directories
     * @throws PersistenceException if there is a problem getting
     * the user's domain
     
    protected abstract void loadSearchableDirectories() 
            throws net.project.base.directory.DirectoryException, PersistenceException;*/
    
    protected abstract void loadSearchableDirectories(boolean isIncludeCurrentBusiness) 
    	throws net.project.base.directory.DirectoryException, PersistenceException;
	//Subclasses should implement it to load the correct directories.
    
    /**
     * Determine whether the inviter has access to directories to search from.
     * It has directories if the inviter has one or more business spaces or
     * the inviter's domain provides directories
     * @return true if there a directories to invite from; false otherwise
     */
    public boolean hasDirectories() {
        
        try {
            if (!this.hasLoadedDirectories) {
                loadSearchableDirectories(false);
                this.hasLoadedDirectories = true;
            }
        
        } catch (PersistenceException e) {
            // Cannot throw as signature does not allow
            // simply accept searchable directories as loaded

        } catch (net.project.base.directory.DirectoryException e) {
            // Cannot throw as signature does not allow
            // simply accept searchable directories as loaded

        }

	//System.out.println("AbstractInvitationManager : searchableDirectories.size() is " + this.searchableDirectories.size());
        // Has directories if the searchable directory map is
        // not empty
        return !this.searchableDirectories.isEmpty();
    }
    

    /**
     * Sets the id of the directory to search in.
     * @param directoryID the directory id
     * @throws InvitationException if the specified ID is not found
     * in the current list of searchable directories
     */
    public void setDirectoryID(String directoryID) throws InvitationException {
        this.directoryID = directoryID;
        
        if (this.directoryID != null) {
            this.searchableDirectory = getSearchableDirectory(directoryID);
        }
    
    }
    
    /**
     * Returns the id of the directory being searched in.
     * @return the id of the directory being searched
     */
    public String getDirectoryID() {
        return this.directoryID;
    }

    /**
     * Returns the searchable directories.
     * Each key is of type String and represents a unique ID.  Each value
     * is of type <code>ISearchableDirectory</code>.
     * return the current searchable directories.
     * @return the searchable directories
     */
    protected java.util.Map getSearchableDirectories() {
        //return null;
	return this.searchableDirectories;
    }

    /**
     * Returns the searchable directory with the specified id.
     * Default implementation returns null; subclasses should override
     * to return the currently searchable directory for the specified id.
     * @throws InvitationException if the invitation wizard does not
     * have the directory specified by the id; this prevents
     * ability to force searching of an different directory by merely
     * specifying an aribitrary id (a potential security hole)
     */
    protected ISearchableDirectory getSearchableDirectory(String directoryID)
            throws net.project.resource.InvitationException {

        return null;
    }
    
    /**
     * Returns the currently selected searchable directory.
     * Assumes {@link #setDirectoryID} has been called.
     * @return the searchable directory or null if none has been
     * specified.
     */
    public ISearchableDirectory getSearchableDirectory() {
        return this.searchableDirectory;
    }

    /**
     * Returns an HTML option list of searchable directories.
     * Provides for selection of a searchable directory.
     * Only returns options if this wizard has directories.
     * The currently selected directory is selected in the option list
     * @return the HTML option list; each value is the String form of the
     * key in <code>getSearchableDirectories</code> and the display text is 
     * the searchable directory name; OR returns an empty string if this
     * wizard has no directories
     * @see #getDirectoryID
     * @see #getSearchableDirectories
     */
    public String getDirectoryOptionList() {
        
        StringBuffer result = new StringBuffer();

        if (hasDirectories()) {
        
            // Grab the ID of the currently selected directory (if any)
            // And the directories for searching
            String currentSelectionID = getDirectoryID();
            java.util.Map searchableDirectories = getSearchableDirectories();

            // Sort the keys (directoryIDs) in numeric order
            List keyList = new ArrayList();
            keyList.addAll(searchableDirectories.keySet());
            Collections.sort(keyList, new DirectoryIDComparator());

            // Generate the option tags for each searchable directory
            // Ensure the current directory is marked as "selected"
            for (Iterator it = keyList.iterator(); it.hasNext(); ) {
                String nextID = (String) it.next();
                ISearchableDirectory nextDirectory = (ISearchableDirectory) searchableDirectories.get(nextID);

                result.append("<option value=\"").append(nextID).append("\"");
                if (currentSelectionID != null && currentSelectionID.equals(nextID)) {
                    result.append(" selected");
                }
                result.append(">");
                result.append(TextFormatter.truncateString(nextDirectory.getSearchableDirectoryName(),35));
                result.append("</option>").append("\n");
            }

        }
        
        return result.toString();
    }
    
    public List<PnBusiness> getDirectoryOptionLists(boolean addCurrentBusiness) throws DirectoryException, PersistenceException {
        
        StringBuffer result = new StringBuffer();
        List<PnBusiness> businessList = new ArrayList<PnBusiness>();
        int i = 0;

        	loadSearchableDirectories(addCurrentBusiness);
        
            // Grab the ID of the currently selected directory (if any)
            // And the directories for searching
            String currentSelectionID = getDirectoryID();
            java.util.Map searchableDirectories = getSearchableDirectories();

            // Sort the keys (directoryIDs) in numeric order
            List keyList = new ArrayList();
            keyList.addAll(searchableDirectories.keySet());
            Collections.sort(keyList, new DirectoryIDComparator());

            // Generate the option tags for each searchable directory
            // Ensure the current directory is marked as "selected"
            for (Iterator it = keyList.iterator(); it.hasNext(); ) {
                String nextID = (String) it.next();
                ISearchableDirectory nextDirectory = (ISearchableDirectory) searchableDirectories.get(nextID);
                //BusinessWrapper businessWrapper = new BusinessWrapper();
                PnBusiness businessWrapper = new PnBusiness();
                if(i == 0 && !addCurrentBusiness){
            	 	PnBusiness businessWrapperSelect = new PnBusiness();
                	businessWrapperSelect.setBusinessId(-1);
                	businessWrapperSelect.setBusinessName(PropertyProvider.get("prm.directory.invitemember.selectbusiness.option"));
                	businessList.add(businessWrapperSelect);
                }
            	businessWrapper.setBusinessId(Integer.parseInt(nextID));
            	businessWrapper.setBusinessName(nextDirectory.getSearchableDirectoryName());
                businessList.add(businessWrapper);
                i++;
            }

        
        
        return businessList;
    }

    /**
     * Sets the search name to search in the directory.
     * @param searchName the name to search
     */
    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }


    /**
     * Returns the name to search in the directory.
     * @return the current search name
     */
    public String getSearchName() {
        return this.searchName;
    }

    /**
     * Adds the specified invitee to the list of invitees.
     * If the invitee has already been added (that is, there is an
     * invitee with the same email address) it is first removed to ensure
     * we don't have duplicate invitees. This method may be deleted after
     * two releases
     *
     * @param invitee a person being invited
     * @deprecated as of Gecko4 Please use {@link #addMember} 
     * @see #addMember(Invitee)
     */
    public void addInvitee(Invitee invitee) {
        // If the list already contains the invitee (by email address)
        // then we remove it before adding again
        // Ensures no duplicates in list but allows for modification of names
        
        try {
            addMember(invitee);
        } catch (InvitationException ie ) {
            // Do nothing as the method is already deprecated
        }
    }

    /**
     * Adds all the specified invitees to the list of invitees.
     * If any invitees already present, they are first removed.
     * This method may be deleted after
     * two releases
     *
     * @param inviteeCollection the collection of <code>Invitee</code>s
     * @deprecated as of Gecko4 Please use {@link #addAllMembers}
     */
    public void addAllInvitees(Collection inviteeCollection) {

        try {
            addAllMembers(inviteeCollection);
        } catch (InvitationException ie ){
            // Do nothing as the method is already deprecated
        }
    }

    /**
     * Adds the specified invitee to the list of invitees.
     * If the invitee has already been added (that is, there is an
     * invitee with the same email address) it is first removed to ensure
     * we don't have duplicate invitees.
     * 
     * @param invitee a person being invited
     * @exception InvitationException  is thrown if the Invitee cannot be added 
     * due to some constraints
     */
    public void addMember(Invitee invitee) throws InvitationException {
        // If the list already contains the invitee (by email address)
        // then we remove it before adding again
        // Ensures no duplicates in list but allows for modification of names

        this.inviteeList.remove(invitee);
        this.inviteeList.add(invitee);
    }

    /**
     * Adds all the specified invitees to the list of invitees.
     * If any invitees already present, they are first removed.
     * 
     * @param inviteeCollection
     *               the collection of <code>Invitee</code>s
     * @exception InvitationException  is thrown if the Invitee cannot be added 
     * due to some constraints
     */
    public void addAllMembers (Collection inviteeCollection) 
        throws InvitationException {

        this.inviteeList.removeAll(inviteeCollection);
        this.inviteeList.addAll(inviteeCollection);
    }

    /**
     * Removes the specified invitee from the list of invitees on the basis of email ID .
     * @param inviteeEmail invitee's email ID
     */
    public void removeInvitee(String inviteeEmail) {
        Iterator itr = this.inviteeList.iterator();
        while (itr.hasNext()) {
	    Invitee invitee = (Invitee) itr.next();
            if (invitee.getEmail() != null && invitee.getEmail().equalsIgnoreCase(inviteeEmail)) {
                this.inviteeList.remove(invitee);
                break;
            }
        }        
    }

    /**
     * Returns the list of invitees currently added to this invitation manager.
     * The returned list should not be modified directly.
     * @return the invitee list which may be empty.  Each element is
     * of the type <code>Invitee</code>.
     */
    public InviteeList getInviteeList() {
        return this.inviteeList;
    }

    /**
     * Returns the last invitee added to the list.
     * This is useful for displaying invitee information.
     * @return the last invitee or an empty Invitee if none have been
     * added yet.
     */
    public Invitee getLastInvitee() {
        Invitee lastInvitee;

        if (!this.inviteeList.isEmpty()) {
            lastInvitee = (Invitee) this.inviteeList.get(this.inviteeList.size() - 1);
        
        } else {
            lastInvitee = new Invitee();
        }

        return lastInvitee;
    }

    /**
     * Clear the cached information from this manager.
     * This does NOT include the current user and space or
     * the invite URLs.
     * Essentially it prepares this manager for use in the wizard.
     */
    public void clear() {
        this.directoryID = null;
        this.searchableDirectory = null;
        this.searchName = null;
        this.inviteeList.clear();
    }
    
    /**
     * Comparator for comparing directory IDs.
     * DirectoryIDs are Strings that contain numbers; this comparator
     * causes comparison by numeric value
     */
    private static class DirectoryIDComparator implements java.util.Comparator {
        
        /**
         * Compares two Strings that represent integers.
         * Comparison based on converting Strings to integers.
         */
        public int compare(Object o1, Object o2) {
            int returnValue;

            int i1 = new Integer((String) o1).intValue();
            int i2 = new Integer((String) o2).intValue();
            
            if ( i1 < i2 ) {
                returnValue = -1;
            
            } else if (i1 > i2) {
                returnValue = 1;
            
            } else {
                returnValue = 0;
            }

            return returnValue;
        }

    }
    
    /**
     * Set hasLoadedDirectories 
     * @param hasLoadedDirectories
     */
    public void setHasLoadedDirectories(boolean hasLoadedDirectories){
    	this.hasLoadedDirectories = hasLoadedDirectories;
    }
}
