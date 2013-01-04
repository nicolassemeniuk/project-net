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
package net.project.base.directory.ldap;

import javax.naming.directory.DirContext;

import net.project.base.directory.AuthenticationFailedException;
import net.project.base.directory.DirectoryException;
import net.project.base.directory.search.IDirectoryContext;
import net.project.base.directory.search.ISearchableDirectory;
import net.project.database.DBBean;


/**
 * The LDAPDirectoryProvider provides authentication and directory
 * services based on an LDAP directory.
 */
public class LDAPDirectoryProvider extends net.project.base.directory.DirectorySPI {
    
    /**
     * Authenticates against the LDAP directory using the current
     * authentication context.
     * @throws AuthenticationFailedException if there is a problem authenticating
     * due to invalid username or password
     * @throws DirectoryException if there is a problem authenticating,
     * for example unable to contact any LDAP server or a configuration problem
     * (such as bad DN)
     */
    protected void authenticate(boolean shadowLogin, boolean isFromSSOLogin) throws AuthenticationFailedException, DirectoryException {
                                                   
        // Delegate authentication and ignore returned attributes
        fetchAuthenticatedAttributes(shadowLogin);

    }

    protected void authenticate() throws AuthenticationFailedException, DirectoryException {
    	authenticate(false, false);
    }
    /**
     * Returns the directory entry from the LDAP directory for the
     * user represented by the current authentication context.
     * @return the directory entry
     * @throws AuthenticationFailedException if there is a problem authenticating
     * due to invalid username or password
     * @throws DirectoryException if there is a problem authenticating,
     * for example unable to contact any LDAP server or a configuration problem
     * (such as bad DN)
     */
    protected net.project.base.directory.IDirectoryEntry getAuthenticatedDirectoryEntry()
            throws AuthenticationFailedException, DirectoryException {

        LDAPDirectoryConfiguration config = (LDAPDirectoryConfiguration) getConfiguration();

        // Make the directory entry from the configured attribute
        // map and attributes for the authenticated user
        return new LDAPHelper().makeDirectoryEntry(config.getAttributeMap(), fetchAuthenticatedAttributes());
    }

    /**
     * Returns the directory entry from the LDAP directory for the user
     * with the specified username.
     * No authentication is performed.
     * @param username the username to get directory entry for
     * @return the directory entry
     * @throws DirectoryException if there is a problem authenticating
     */
    protected net.project.base.directory.IDirectoryEntry getDirectoryEntry(String username )
            throws DirectoryException {

        LDAPDirectoryConfiguration config = (LDAPDirectoryConfiguration) getConfiguration();
        
        // Make the directory entry from the configured attribute
        // map and attributes for the specified user
        return new LDAPHelper().makeDirectoryEntry(config.getAttributeMap(), fetchAttributes(username));
    }

    /**
     * Sets the LDAP Directory Configuration.
     * Simply calls superclass method; provided to allow access to
     * other classes in LDAP package.
     * @param configuration the LDAP Directory Configuration
     */
    protected void setConfiguration(LDAPDirectoryConfiguration configuration) {
        super.setConfiguration(configuration);
    }

    /**
     * Returns the LDAP Directory Configuration.
     * Simply calls superclass method; provided to allow access to
     * other classes in LDAP package.
     * @return the directory configuration
     */
    protected net.project.base.directory.DirectoryConfiguration getConfiguration() {
        return super.getConfiguration();
    }

    private javax.naming.directory.Attributes fetchAuthenticatedAttributes()
    throws AuthenticationFailedException, DirectoryException {
    	return fetchAuthenticatedAttributes(false);
    }
    
    /**
     * Returns the attributes for a user after authenticating using
     * the current authentication context.
     * @return the Attributes for the authenticated user
     * @throws AuthenticationFailedException if there is a problem authenticating
     * due to invalid username or password
     * @throws DirectoryException if there is a problem authenticating,
     * for example unable to contact any LDAP server or a configuration problem
     * (such as bad DN)
     */
    private javax.naming.directory.Attributes fetchAuthenticatedAttributes( boolean shadowLogin, boolean isFromSSO)
             throws AuthenticationFailedException, DirectoryException {

        LDAPDirectoryConfiguration config = (LDAPDirectoryConfiguration) getConfiguration();
        LDAPHelper ldap = new LDAPHelper();

        // Get a context to the search base
        // By using the same context we potentially maintain
        // a single connection with the LDAP server
        // (This statement yet to be proven)
        DirContext context = ldap.getBaseContext(config);
        // Search for the user entry that contains the username; returns an absolute and relative DN
        LDAPHelper.UserDN userDN = ldap.findUserDN(context, config, getAuthenticationContext().getUsername());

        if(shadowLogin || isFromSSO){
        	// in case of shadow login, simply fetch the atrributes for user without authentication.
        	return ldap.fetchAttributes(context, userDN);
        } else {
	        // Authenticate against that userDN with the password
	        // by fetching the user's attributes
	        return ldap.fetchAuthenticatedAttributes(context, userDN, getAuthenticationContext().getClearTextPassword());
        }
    }
    
    private javax.naming.directory.Attributes fetchAuthenticatedAttributes( boolean shadowLogin)
    		 throws AuthenticationFailedException, DirectoryException {
    	 return fetchAuthenticatedAttributes(shadowLogin, false);
    }


    /**
     * Returns the attributes for the user with the specified username
     * without authenticating.
     * @return the Attributes for the user
     * @throws DirectoryException if there is a problem authenticating,
     * for example unable to contact any LDAP server or a configuration problem
     * (such as bad DN)
     */
    private javax.naming.directory.Attributes fetchAttributes(String username)
             throws DirectoryException {

        LDAPDirectoryConfiguration config = (LDAPDirectoryConfiguration) getConfiguration();
        LDAPHelper ldap = new LDAPHelper();

        // Get a context to the search base
        // By using the same context we potentially maintain
        // a single connection with the LDAP server
        // (This statement yet to be proven)
        DirContext context = ldap.getBaseContext(config);
        
        // Search for the user entry that contains the username; returns an absolute and relative DN
        LDAPHelper.UserDN userDN = ldap.findUserDN(context, config, username);

        // Fetch the attributes for the user with anonymous connection
        return ldap.fetchAttributes(context, userDN);
    }



    /**
     * Updates the user in the LDAP directory.
     * Currently has no action.
     * @param directoryEntry the directory information about the user
     * @throws DirectoryException never
     */
    protected void updateUser(net.project.security.User user, net.project.base.directory.IDirectoryEntry directoryEntry)
            throws DirectoryException {
            
        // Pass control to the overloaded method

        DBBean dbean = new DBBean();

        try {
            updateUser(user, directoryEntry , dbean);
        } finally {
            dbean.release();
        }

    }


    /**
     * Updates the user in the LDAP directory.
     * Currently has no action.
     * @param directoryEntry the directory information about the user
     * @throws DirectoryException never
     */
    protected void updateUser(net.project.security.User user, net.project.base.directory.IDirectoryEntry directoryEntry , DBBean dbean )
            throws DirectoryException {

        // Performs no action
    }

    /**
     * Removes the user from this directory.
     * Currently has no action.
     * @param user the user to remove
     * @throws DirectoryException never
     */
    protected void removeUser(net.project.security.User user)
            throws DirectoryException {

        // Pass control to the overloaded method

        DBBean dbean = new DBBean();

        try {
            removeUser(user,dbean);
        } finally {
            dbean.release();
        }

    }

    /**
     * Removes the user from this directory.
     * Currently has no action.
     * @param user the user to remove
     * @param dbean the database bean
     * @throws DirectoryException never
     */
    protected void removeUser(net.project.security.User user , DBBean dbean)
            throws DirectoryException {

        // Performs no action
    }

    /**
     * Indicates whether this LDAP directory allows searching.
     * LDAP directories to allow searching, but it may be configured
     * to prevent it.
     * @return true if this directory permits searching; false otherwise
     */
    public boolean isSearchableForInvitation() {
        LDAPDirectoryConfiguration config = (LDAPDirectoryConfiguration) getConfiguration();
        return config.isAvailableForDirectorySearch();
    }

    /**
     * Returns the SearchableDirectory for this LDAP Directory.
     * This is only valid if <code>{@link #isSearchableForInvitation}</code> returns true.
     * @return the Searchable Directory or null if none is provided
     */
    public ISearchableDirectory getSearchableDirectory() {
        ISearchableDirectory directory = null;

        if (isSearchableForInvitation()) {
            directory = new LDAPSearchableDirectory();
        }
    
        return directory;
    }


    //
    // Inner classes
    //

    /**
     * Provides the SearchableDirectory interface to this LDAP Directory.
     */
    class LDAPSearchableDirectory implements ISearchableDirectory {

        /**
         * Returns the name of this directory.
         * @return the name
         */
        public String getSearchableDirectoryName() {
            // Name is specified by the configuration
            return ((LDAPDirectoryConfiguration) LDAPDirectoryProvider.this.getConfiguration()).getDirectorySearchDisplayName();
        }

        /**
         * Returns the description of this directory.
         * @return the description
         */
        public String getSearchableDirectoryDescription() {
            // Currently the configuration doesn't maintain a separate
            // description
            return getSearchableDirectoryName();
        }

        /**
         * Returns the directory context for this directory.
         * The context actually provides the search facilities.
         * @return the directory context
         */
        public IDirectoryContext getDirectoryContext() {
            return new LDAPDirectoryContext(LDAPDirectoryProvider.this);
        }

    }



    //
    // Unit test
    //

    /**
     * Usage:
     * LDAPDirectoryProvider [username [password]]
     *     username  -  the username to connect as; default is "tim"
     *     password  -  the password to authenticate with; default is "tim"
     */
    public static void main(String[] args) {

        String username = null;
        String password = null;

        for (int i = 0; i < args.length; i++) {
            switch (i) {
            case 0:
                username = args[i];
				break;
            case 1:
                password = args[i];
            }
        }

        if (username == null) {
            username = "tim";
        }

        if (password == null) {
            password = "tim";
        }

        try {
            LDAPDirectoryConfiguration config = new LDAPDirectoryConfiguration();
            config.setHostnameValues("hippo.project.net");
            config.setSecureHostnameValues("hippo.project.net");
            config.setSearchBaseDN("dc=project,dc=net");
//            config.setSearchType(SearchType.ALL_SUBTREES);
            config.setSearchType(SearchType.LIMIT_SUBTREES);
            config.setSearchSubtrees("ou=People");
            config.setUsernameAttribute("uid");
            config.setUseSSL(false);
            config.setNonAuthenticatedAccessType(NonAuthenticatedAccessType.ANONYMOUS);
            
            // Make an attribute map
            LDAPAttributeMap map = new LDAPAttributeMap();
            map.put(new net.project.base.attribute.TextAttribute("person.email","Email"), new LDAPAttributeMap.LDAPAttributeDefinition("mail"));
            map.put(new net.project.base.attribute.TextAttribute("person.lastname", "Last Name"), new LDAPAttributeMap.LDAPAttributeDefinition("sn"));
            map.put(new net.project.base.attribute.TextAttribute("person.firstname", "First Name"), new LDAPAttributeMap.LDAPAttributeDefinition("givenName", 0));
            map.put(new net.project.base.attribute.TextAttribute("person.displayname", "Display Name"), new LDAPAttributeMap.LDAPAttributeDefinition("cn", 0));
            map.put(new net.project.base.attribute.TextAttribute("address.address1", "Address 1"), new LDAPAttributeMap.LDAPAttributeDefinition("postaladdress", 0));
            map.put(new net.project.base.attribute.TextAttribute("address.city", "City"), new LDAPAttributeMap.LDAPAttributeDefinition("postaladdress", 1));
            map.put(new net.project.base.attribute.TextAttribute("address.zipcode", "Zipcode"), new LDAPAttributeMap.LDAPAttributeDefinition("postaladdress", 2));
            config.setAttributeMap(map);


            net.project.base.directory.AuthenticationContext authenticationContext = new net.project.base.directory.AuthenticationContext("", username, password);

            LDAPDirectoryProvider provider = new LDAPDirectoryProvider();
            provider.setConfiguration(config);
            provider.setAuthenticationContext(authenticationContext);

            System.out.println("Configuration: " + config);

            // With authentication / anonymous access
            System.out.println("WITH Authentication...");
            System.out.println("Username: " + username + ", password: " + password);
            System.out.println("Directory entry:");
            System.out.println(provider.getAuthenticatedDirectoryEntry().toString());
            System.out.println("Done");
        
            // Without authentication / anonymous access
            System.out.println("WITHOUT Authentication...");
            System.out.println("Username: " + username);
            System.out.println("Directory entry:");
            System.out.println(provider.getDirectoryEntry(username).toString());
            System.out.println("Done");

            // Without authentication - special user access
            config.setNonAuthenticatedAccessType(NonAuthenticatedAccessType.SPECIFIC_USER);
            config.setSpecificUserDN("uid=tim, ou=People");
            config.setSpecificUserPassword("tim");
            System.out.println("WITHOUT Authentication (Specific user)...");
            System.out.println("Username: " + username);
            System.out.println("Directory entry:");
            System.out.println(provider.getDirectoryEntry(username).toString());
            System.out.println("Done");


        } catch (Exception e) {
            e.printStackTrace();

        }

    }

}
