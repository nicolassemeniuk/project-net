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

 package net.project.base.directory;
 
/**
 * This class defines the <i>Service Provider Interface</i> (<b>SPI</b>)
 * for the <code>Directory</code> class.  All the abstract methods
 * in this class must be implemented by each directory service provider
 * who wishes to supply the implementation of a particular directory type.
 * <p>
 * Implementing classes must provide a no-argument constructor.
 * </p>
 * @author Tim
 * @since Gecko Update 3
 */ 
public abstract class DirectorySPI implements java.io.Serializable {

    private DirectoryConfiguration configuration = null;

    private AuthenticationContext authenticationContext = null;

    /**
     * Sets the configuration to be used for authenticating with this
     * service provider.
     * @param configuration the configuration
     */
    protected void setConfiguration(DirectoryConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Returns the configuration to use for all directory services.
     * @return the configuration
     */
    protected DirectoryConfiguration getConfiguration() {
        return this.configuration;
    }

    /**
     * Sets the authenticationContext to be used for all directory
     * operations.
     * @param authenticationContext the authentication context
     */
    protected void setAuthenticationContext(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    /**
     * Returns the current AuthenticationContext to be used for all
     * directory operations.
     * @return the current AuthenticationContext
     */
    protected AuthenticationContext getAuthenticationContext() {
        return this.authenticationContext;
    }

    /**
     * Authenticates using the current authentication context.
     * Note that all the exceptions thrown by this method are subclasses
     * <code>DirectoryException</code>.
     * @throws AuthenticationFailedException if authentication failed
     * for an expected reason (such as invalid username or password)
     * @throws ConfigurationException if there is a problem that may
     * be caused by a configuration setting
     * @throws TooManyEntriesException if more than one entry found for
     * the current authentication context
     * @throws CommunicationException there is a problem contacting
     * the directory provider
     * @throws DirectoryException if authentication fails for some
     * other reason
     */
    protected abstract void authenticate() 
            throws AuthenticationFailedException, ConfigurationException, 
                TooManyEntriesException, CommunicationException, DirectoryException;
    /**
     * This is similar to authenticate(), but will not percorm real authentication
     * @param shadowLogin
     * @throws AuthenticationFailedException
     * @throws ConfigurationException
     * @throws TooManyEntriesException
     * @throws CommunicationException
     * @throws DirectoryException
     */
    protected abstract void authenticate(boolean shadowLogin, boolean isFromSSOLogin) 
	    throws AuthenticationFailedException, ConfigurationException, 
	        TooManyEntriesException, CommunicationException, DirectoryException;

    /**
     * Authenticates using the current authentication context and
     * returns the directory entry for the authenticated user.
     * Note that all the exceptions thrown by this method are subclasses
     * <code>DirectoryException</code>.
     * @throws AuthenticationFailedException if authentication failed
     * for an expected reason (such as invalid username or password)
     * @throws ConfigurationException if there is a problem that may
     * be caused by a configuration setting
     * @throws TooManyEntriesException if more than one entry found for
     * the current authentication context
     * @throws CommunicationException there is a problem contacting
     * the directory provider
     * @throws DirectoryException if authentication fails for some
     * other reason
     */
    protected abstract net.project.base.directory.IDirectoryEntry getAuthenticatedDirectoryEntry() 
            throws AuthenticationFailedException, ConfigurationException, 
                TooManyEntriesException, CommunicationException, DirectoryException;

    protected abstract net.project.base.directory.IDirectoryEntry getDirectoryEntry(String username ) 
            throws DirectoryException;


    /**
     * Update the user in this directory.
     * Directory Providers that maintain information about the user
     * should write it to the directory here.
     * @param user the user being modified
     * @param directoryEntry the directory-specific entry details
     */
    protected abstract void updateUser(net.project.security.User user, IDirectoryEntry directoryEntry) 
            throws DirectoryException;

    /**
     * Update the user in this directory.
     * Directory Providers that maintain information about the user
     * should write it to the directory here.
     * @param user the user being modified
     * @param directoryEntry the directory-specific entry details
     * @param dbean the Database bean
     */
    protected abstract void updateUser(net.project.security.User user, IDirectoryEntry directoryEntry , net.project.database.DBBean dbean ) 
            throws DirectoryException;

    /**
     * Removes the user from this directory.
     * Directory Providers that maintain information about a registered
     * user should remove that information here.
     * @param user the user to remove
     * @throws DirectoryException if there is a problem removing the user
     */
    protected abstract void removeUser(net.project.security.User user)
            throws DirectoryException;


    /**
     * Removes the user from this directory.
     * Directory Providers that maintain information about a registered
     * user should remove that information here.
     * @param user the user to remove
     * @param dbean the database bean   
     * @throws DirectoryException if there is a problem removing the user
     */
    protected abstract void removeUser(net.project.security.User user , net.project.database.DBBean dbean)
            throws DirectoryException ;
    /**
     * Indicates whether this directory allows searching.
     * Not all directories may provide searching or it may be
     * explicitly configured to prevent it.
     * @return true if this directory permits searching; false otherwise
     */
    public abstract boolean isSearchableForInvitation();


    /**
     * Returns the SearchableDirectory for this directory provider.
     * This is only valid if <code>{@link #isSearchableForInvitation}</code> returns true.
     * @return the Searchable Directory or null if not searchable
     */
    public abstract net.project.base.directory.search.ISearchableDirectory getSearchableDirectory();

}
