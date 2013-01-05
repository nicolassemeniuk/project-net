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

 package net.project.security.domain;

import net.project.admin.AutomaticRegistrationException;
import net.project.base.directory.AuthenticationContext;
import net.project.base.directory.Directory;
import net.project.base.directory.DirectoryException;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.security.User;

import org.apache.log4j.Logger;

/**
 * The DomainAuthenticator provides login authentication services.
 */
public class DomainAuthenticator implements java.io.Serializable {

    //
    // Static members
    //

    /**
     * Creates a new DomainAuthenticator for the specified domain.
     * @param domainID the id of the domain for which authentication
     * will be performed
     * @param username the username for authentication
     * @param clearTextPassword the password for authentication
     * @return the DomainAuthenticator
     * @throws DomainException if there is a problem loading the
     * specified domain
     */
    public static DomainAuthenticator getInstance(String domainID, String username, String clearTextPassword) 
            throws DomainException {

        return new DomainAuthenticator(domainID, username, clearTextPassword);
    }


    //
    // Instance members
    //

    private UserDomain domain = null;
    private AuthenticationContext authenticationContext = null;
    private boolean isAuthenticated = false;


    /**
     * Creates a new DomainAuthenticator for the specified domain.
     * @param domainID the id of the domain for which authentication
     * will be performed
     * @param username the username for authentication
     * @param clearTextPassword the password for authentication
     * @throws DomainException if there is a problem loading the
     * specified domain
     */
    private DomainAuthenticator(String domainID, String username, String clearTextPassword) 
            throws DomainException {

        try {
            // Load the domain
            UserDomain domain = new UserDomain();
            domain.setID(domainID);
            domain.load();
            setDomain(domain);

            // Construct the authentication context
            setAuthenticationContext(new AuthenticationContext(domainID, username,clearTextPassword));

        } catch (net.project.persistence.PersistenceException pe) {
            throw new DomainException("Domain Authenticator failed: " + pe, pe);

        }

    }
    
    public void authenticate() throws DirectoryException, DomainException, AutomaticRegistrationException {
    	authenticate(false, false);
    }

    /**
     * Authenticates against the current domain with the current username
     * and password.
     * Also dynamically registers new users of the Domain supports automatic registration.
     * @postcondidition the <code>isAuthenticated</code> flag is set
     * to <code><b>true</b></code> if authentication was successful;
     * <code><b>false</b></code> if authentication was unsuccessful
     * @throws DirectoryException if there was a problem authenticating
     * (other than invalid username or password)
     * @throws DomainException if there was a problem with the domain
     * @see #isAuthenticated
     */
    public void authenticate( boolean shadoLogin, boolean isFromSSOLogin) throws DirectoryException, DomainException, AutomaticRegistrationException {

        setAuthenticated(false);

        try {

            Directory directory = Directory.getInstance(this.domain.getDirectoryProviderType(), this.domain.getDirectoryConfiguration());
            directory.setAuthenticationContext(this.authenticationContext);

            // First check that the username is even associated with
            // this domain
            if (domain.hasUsername(this.authenticationContext.getUsername())) {

                // Create the Directory from the current domain's
                // directory provider type and configuration
                directory.authenticate(shadoLogin, isFromSSOLogin);
                setAuthenticated(directory.isAuthenticated());

            } else if (domain.allowsAutomaticRegistration() || PropertyProvider.getBoolean("prm.domains.samlexternalsso.isenabled") || PropertyProvider.getBoolean("prm.domains.ldapcosignconfiguration.isenabled")) {
                // there is not currently a user registered to this domain
                // If the Domain Supports automatic registration, we will attempt to authenticate the credentials
                // provided against the service provider.
                // Assuming a successful authentication, we will then proceed to register the user behind the scenes

            	if(!PropertyProvider.getBoolean("prm.domains.ldapcosignconfiguration.isenabled")){
	                // Authenticate against the directory
	                directory.authenticate(shadoLogin, isFromSSOLogin);
	                setAuthenticated(directory.isAuthenticated());
            	}

                if (directory.isAuthenticated() || PropertyProvider.getBoolean("prm.domains.ldapcosignconfiguration.isenabled")) {
                    // now, if we were able to succesfully authenticate, we throw a specific exception to hand-off handling elsewhere.
                    // this will throw a domain exception if the service provide doesn't support this, or if there was a problem.
                    throw new AutomaticRegistrationException("The new user has successsfully authenticated against the domain.");
                }

            } else {
                // the user doesn't already exist and the domain doesn't support automatic registration
                // hence, no such user...
                setAuthenticated(false);
            }

        } catch (PersistenceException pe) {
            // Problem getting directory provider type
        	Logger.getLogger(DomainAuthenticator.class).error("Error loading directory provider type: " + pe);
            throw new DomainException("Domain load operation failed: " + pe, pe);
        }

    }

    private void setDomain(UserDomain domain) {
        this.domain = domain;
    }

    private void setAuthenticationContext(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    private void setAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    /**
     * Indicates whether authentication was successful.
     * @return <code><b>true</b></code> if authentication was successful;
     * <code><b>false</b></code> if authentication was not successful or
     * if <code>{@link #authenticate}</code> has not been called
     */
    public boolean isAuthenticated() {
        return this.isAuthenticated;
    }

    /**
     * Returns the <code>User</code> that was authenticated with
     * the specified username and password against the domain.
     * @return the loaded <code>User</code>
     * @throws IllegalStateException if authentication has not been
     * succesful
     * @throws DomainException if there is a problem looking up the
     * user in the domain (for example, the users could not be loaded)
     * @see #isAuthenticated
     */
    public User getAuthenticatedUser() 
            throws DomainException {

        if (!isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }

        return domain.getUser(this.authenticationContext.getUsername());
    }

}
