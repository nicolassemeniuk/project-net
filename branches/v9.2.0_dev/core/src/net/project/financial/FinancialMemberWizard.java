package net.project.financial;
import java.util.Iterator;

import net.project.base.directory.DirectoryException;
import net.project.base.directory.search.ISearchableDirectory;
import net.project.business.BusinessSpace;
import net.project.persistence.PersistenceException;
import net.project.resource.InvitationException;
import net.project.resource.SpaceInvitationManager;
import net.project.security.domain.UserDomain;
import net.project.space.PersonalSpace;


public class FinancialMemberWizard extends SpaceInvitationManager {

    /**
     * Cached PersonalSpace of the inviter.  This is used to determine the
     * inviter's business spaces.
     */
    private PersonalSpace personalSpace = null;

    /** Cached current domain of the inviter.  Used to fetch domain directories. */
    private UserDomain currentDomain = null;

    /**
     * Creates a new, empty ProjectMemberWizard.
     */
    public FinancialMemberWizard() {
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
     * @throws DirectoryException if there is a problem determining the user's
     * domain's directories
     * @throws PersistenceException if there is a problem getting the user's
     * domain
     */
    protected void loadSearchableDirectories(boolean isIncludeCurrentBusiness)
        throws DirectoryException, PersistenceException {

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
            this.searchableDirectories.put(String.valueOf(nextID++), ((BusinessSpace) it.next()).getSearchableDirectory());
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
     * @throws InvitationException if this member wizard does not have a
     * searchable directory matching that id
     */
    protected ISearchableDirectory getSearchableDirectory(String directoryID)
        throws net.project.resource.InvitationException {

        ISearchableDirectory directory = (ISearchableDirectory) this.searchableDirectories.get(directoryID);

        if (directory == null) {
            throw new InvitationException("Selected directory not found in Project invitation wizard");

        }

        return directory;
    }
    
    //
    // Directory Presentation
    //

    /**
     * Indicates whether this directory supports Participants.
     * 
     * @return true always
     */
    public boolean isParticipantsSupported() {
        return true;
    }

    /**
     * Indicates whether this directory supports Org Chart.
     * 
     * @return true always
     */
    public boolean isOrgChartSupported() {
        return true;
    }

    /**
     * Indicates whether this directory supports Assignments.
     * 
     * @return true always
     */
    public boolean isAssignmentsSupported() {
        return true;
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
