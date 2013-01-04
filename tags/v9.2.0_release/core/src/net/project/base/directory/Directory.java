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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.project.database.DBBean;

import org.apache.log4j.Logger;

/**
 * This class provides the functionality of a directory service for
 * authentication and lookup.
 * <p>
 * In order to create a Directory object, the application calls the
 * <code>getInstance</code> method and passes the provider type
 * and configuration information to it.
 * </p>
 *
 * @author Tim
 * @since Gecko Update 3
 */
public class Directory implements java.io.Serializable {
    //
    // Static members
    //

    transient Logger logger = Logger.getLogger(Directory.class);

    /**
     * Creates <code>Directory</code> object that provides authentication
     * facilities for the specified provider type.
     * @param providerType the type of directory provider for which
     * services are required
     * @param configuration the configuration information required
     * to connect with the directory service
     * @throws DirectoryException if there is a problem instantiating
     * the service provider for the specified provider type
     */
    public static Directory getInstance(DirectoryProviderType providerType, DirectoryConfiguration configuration) 
            throws DirectoryException {
        
        return new Directory(providerType, configuration);
    }


    //
    // Instance members
    //

    private DirectorySPI serviceProvider = null;

    private boolean isAuthenticated = false;

    /**
     * Creates a Directory.
     * @param providerType the type of directory provider for which
     * services are required
     * @param configuration the configuraiton information required to
     * connect with the directory service
     * @throws DirectoryException if there is a problem instantiating
     * the service provider for the specified provider type
     * @see #getInstance
     */
    protected Directory(DirectoryProviderType providerType, DirectoryConfiguration configuration) 
            throws DirectoryException {
        
        instantiateServiceProvider(providerType, configuration);
    }

    /**
     * Sets the authentication context used for future authentications
     * with this directory.
     * @param authenticationContext the authentication context used
     * for all authentication operations
     */
    public void setAuthenticationContext(AuthenticationContext authenticationContext) {
        this.serviceProvider.setAuthenticationContext(authenticationContext);    
        setAuthenticated(false);
    }

    /**
     * Sets the service provider that provides the core services
     * for accessing this directory implementation.
     * @param serviceProvider the directory service provider implementation
     */
    private void setServiceProvider(DirectorySPI serviceProvider) {
        this.serviceProvider = serviceProvider;
    }


    /**
     * Creates an instance of the service provider for the specified
     * directory provider type and configures it.
     * @postcondition a service provider implementation is available
     * with {@link #getServiceProvider}
     * @param providerType the type of directory provider for which
     * services are required
     * @param configuration the configuraiton information required to
     * connect with the directory service
     * @throws DirectoryException if there is a problem instantiating
     * the service provider for the specified provider type
     */
    private void instantiateServiceProvider(DirectoryProviderType providerType, DirectoryConfiguration configuration) 
            throws DirectoryException {

        // Instantiate a service provider based on the provider type
        DirectorySPI serviceProvider = providerType.newServiceProvider();

        // Configure the service provider
        serviceProvider.setConfiguration(configuration);

        // Store it in this instance
        setServiceProvider(serviceProvider);
    }

    public void authenticate() throws DirectoryException {
    	authenticate(false, false);
    }

    /**
     * Authenticate against the directory with the current authentication
     * information. <br>
     * <b>Precondition:</b> the authentication context has been specified
     * by {@link #setAuthenticationContext} <br>
     * <b>Postcondition:</b> <code>{@link #isAuthenticated}</code> will
     * return true if authentication was successful; false if authentication
     * was not successful
     * @throws IllegalStateException if there is no current authentication
     * context.
     * @throws DirectoryException if there is some problem authenticating
     * (more serious than invalid username or password)
     */
    public void authenticate(boolean shadowLogin, boolean isFromSSOLogin) throws DirectoryException {
        
        if (this.serviceProvider.getAuthenticationContext() == null) {
            throw new IllegalStateException("No authentication context");
        }
        
        setAuthenticated(false);
        
        try {
        	this.serviceProvider.authenticate(shadowLogin, isFromSSOLogin);
            setAuthenticated(true);

            logger.info("Authentication successful for username=" +
                    this.serviceProvider.getAuthenticationContext().getUsername() +
                    ", domainID=" + this.serviceProvider.getAuthenticationContext().getDomainID());

        } catch (AuthenticationFailedException e) {
            // Authentication failed for an expected reason
            // Authentication remains false
            logger.info("Authentication failed for username=" +
                    this.serviceProvider.getAuthenticationContext().getUsername() +
                    ", domainID=" + this.serviceProvider.getAuthenticationContext().getDomainID());

        }
        
    }

    private void setAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    /** 
     * Indicates whether authentication with the current authentication
     * context has been successful.
     * @return <b>true</b> if the <code>authenticate</code> operation has succeeded
     * since the authentication context was last set; <br>
     * <b>false</b> if <code>authenticate</code> has not been called, or was
     * unsuccessful, or if the authentication context has changed since
     * the last call to <code>authenticate</code>
     */
    public boolean isAuthenticated() {
        return this.isAuthenticated;
    }

    /**
     * Returns the directory entry for the user identified by the
     * current authentication context. <br>
     * <b>Precondition:</b> the authentication context has been specified
     * by {@link #setAuthenticationContext} <br>
     * <b>Postcondition:</b> <code>{@link #isAuthenticated}</code> will
     * return true if authentication was successful; false if authentication
     * was not successful <br>
     * @return the directory-specific entry or null if authentication
     * was not successful
     * @throws IllegalStateException if there is no current authentication
     * context.
     * @throws DirectoryException if there is some problem authenticating
     * (more serious than invalid username or password) or a problem
     * reading the entry
     */
    public IDirectoryEntry getAuthenticatedDirectoryEntry() 
            throws IllegalStateException, DirectoryException {

        if (this.serviceProvider.getAuthenticationContext() == null) {
            throw new IllegalStateException("No authentication context");
        }
        
        IDirectoryEntry directoryEntry = null;
        setAuthenticated(false);
        
        try {
            directoryEntry = this.serviceProvider.getAuthenticatedDirectoryEntry();
            setAuthenticated(true);
        
        } catch (AuthenticationFailedException e) {
            // Authentication failed for an expected reason
            // Authentication remains false
        
        }

        return directoryEntry;
    }

    /**
     * Returns the directory entry for the specified user.
     * This does not authenticate; assumes the directory supports
     * non-authenticated access.
     * Does not affect <code>isAuthenticated</code>
     * @throws DirectoryException if there is some problem reading
     * the entry
     */
    public IDirectoryEntry getDirectoryEntry(String username) 
            throws DirectoryException {

        return this.serviceProvider.getDirectoryEntry(username);
    }

    /**
     * Updates a user in the directory.
     * @param user the user to update
     * @param directoryEntry the directory-specific entry details for the user
     * @throws DirectoryException if there is a problem updating
     */
    public void updateUser(net.project.security.User user, IDirectoryEntry directoryEntry) 
            throws DirectoryException {

        this.serviceProvider.updateUser(user, directoryEntry);
    }

    /**
     * Updates a user in the directory.
     * @param user the user to update
     * @param directoryEntry the directory-specific entry details for the user
     * @param dbean the database bean
     * @throws DirectoryException if there is a problem updating
     */
    public void updateUser(net.project.security.User user, IDirectoryEntry directoryEntry, DBBean dbean) 
            throws DirectoryException {

        this.serviceProvider.updateUser(user, directoryEntry, dbean);
    }

    /**
     * Removes the user from the directory.
     * @param user the user to remove
     * @param dbean the Database bean
     * @throws DirectoryException if there is a problem removing
     */
    public void removeUser(net.project.security.User user , DBBean dbean)
            throws DirectoryException {

        this.serviceProvider.removeUser(user, dbean);
    }

    
    /**
     * Removes the user from the directory.
     * @param user the user to remove
     * @throws DirectoryException if there is a problem removing
     */
    public void removeUser(net.project.security.User user)
            throws DirectoryException {

        this.serviceProvider.removeUser(user);
    }

    /**
     * Indicates whether this directory allows searching.
     * Not all directories may provide searching or it may be
     * explicitly configured to prevent it.
     * @return true if this directory permits searching; false otherwise
     */
    public boolean isSearchableForInvitation() {
        return this.serviceProvider.isSearchableForInvitation();
    }
    
    /**
     * Returns the SearchableDirectory for this directory.
     * This is only valid if <code>{@link #isSearchableForInvitation}</code> returns true.
     * @return the Searchable Directory or null if not searchable
     */
    public net.project.base.directory.search.ISearchableDirectory getSearchableDirectory() {
        net.project.base.directory.search.ISearchableDirectory directory = null;

        if (isSearchableForInvitation()) {
            directory = this.serviceProvider.getSearchableDirectory();
        }

        return directory;
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        logger = Logger.getLogger(Directory.class);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }
}
