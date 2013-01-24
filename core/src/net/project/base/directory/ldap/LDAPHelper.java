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

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import net.project.base.directory.AuthenticationFailedException;
import net.project.base.directory.CommunicationException;
import net.project.base.directory.ConfigurationException;
import net.project.base.directory.DirectoryException;
import net.project.base.directory.TooManyEntriesException;

/**
 * Provides helper methods for accessing LDAP Directories.
 * <p>
 * Typical usage involves retrieving a <code>DirContext</code>
 * that represents a connection to an LDAP server and a search base
 * DN: <pre><code>
 *     getBaseContext(config);
 * </code></pre>
 * </p>
 * <p>
 * Next a UserDN is located that represents a user record in the LDAP
 * server.  This is always fetched anonymously.  It is necessary
 * to perform this anonymously since a search must be performed to
 * locate the user record.  The <code>UserDN</code> returned specifies
 * both the full DN of the user record (suitable for authenticating with)
 * and the DN relative to the search base DN of the <code>DirContext</code>. <br>
 * <code>findUserDN(context, config, username)</code> <br>
 * </p>
 * <p>
 * Finally the attributes can be fetched either by authenticating or
 * anonymously. <br>
 * <code>fetchAuthenticatedAttributes(context, userDN, clearTextPassword);</code> <br>
 * <code>fetchAttributes(context, userDN);</code> <br>
 * </p>
 * @since Gecko 3
 * @author Tim
 */
class LDAPHelper {

    /**
     * The LDAP Protocol prefix, currently <code>ldap://</code>.
     */
    private static final String LDAP_PROTOCOL = "ldap://";
    
    /**
     * Returns a DirContext representing the search base, performed
     * without authentication by a real user.
     * <p>
     * Performed by anonymous authentication or by the specific username
     * and password specified by the configuration.
     * The context may be used for subsequent searches from the search
     * base.
     * </p>
     * <p>
     * This context is provided by the first available server; it represents
     * the base DN - any search results will be relative to that
     * DN.  It does <b>NOT</b> include any search subtree RDNs.
     * </p>
     * <p>
     * For example, if the first server is <code>ldap1.company.com</code>
     * and the base DN is <code>dc=company, dc=com</code> then the
     * DirContext represents the following: <br>
     * <code>ldap://ldap1.company.com/dc=company, dc=com</code>
     * </p>
     * @param config the LDAPConfiguration providing connection information
     * @return the DirContext
     * @throws CommunicationException if no hosts can be found
     * to connect to
     * @throws ConfigurationException if the search base cannot
     * be found or no hostnames were found in the configuration
     * @throws DirectoryException if any other problem occurs initializing
     * the context
     * @return the DirContext
     */
    DirContext getBaseContext(LDAPDirectoryConfiguration config) 
            throws CommunicationException, ConfigurationException, DirectoryException {

        DirContext context = null;

        // Get the appropriate set of URLs based on whether to use SSL or not
        List hostnames = config.getHostEntryListForConnection();

        // If we didn't find any hostnames, it is a configuration error
        if (hostnames.isEmpty()) {
            throw new ConfigurationException("No LDAP servers found in configuration");
        }


        try {
            // Construct the provider URL which includes multiple LDAP URLs
            // The Java 2 SE 1.4.1 JNDI API now supports a space separated list
            // of LDAP URLs
            // Use that Provider URL to get the base context
            context = getBaseContext(buildProviderURL(hostnames, config), config);

        } catch (InvalidPortException e) {
            // This is a configuration problem
            throw new CommunicationException("Invalid port found in LDAP server list: " + e, e);

        }

        return context;
    }


    /**
     * Returns a DirContext for representing a search base from a host specified
     * in the provider URL with anonymous authentication
     * (or using the specific userDN and password in the configuration).
     * <p>
     * Note: If the configuration specifies SSL to be used, the port
     * specified in each LDAP URL in the provider URL <b>must</b> be an SSL port;
     * otherwise, the connection will hang.  This is due to the nature of the SSL protocol.
     * </p>
     * <p>
     * Uses the first LDAP URL in the list that successfully opens a connection.
     * </p>
     * @param providerURL the list of space-separated LDAP URLs to use for
     * connection
     * @param config the configuration
     * @return the DirContext representing the base DN
     * @throws ConfigurationException if there is a problem finding
     * the search Base DN
     * @throws DirectoryException if some other problem occurs initializing
     * the DirContext
     */
    private DirContext getBaseContext(String providerURL, LDAPDirectoryConfiguration config)
            throws ConfigurationException, DirectoryException {

        // Construct the environment for an anonymous bind
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, providerURL);

        // This is required to eliminate "PartialResultException" on result.hasMore() with Active Directory
        env.put(Context.REFERRAL, "follow");

        // Determine how to authenticate
        // Some LDAP servers may not support anonymous authentication
        if (config.getNonAuthenticatedAccessType().equals(NonAuthenticatedAccessType.SPECIFIC_USER)) {
            // Nonauthenticated access actually performed by authenticating
            // as a special user
            UserDN specificUserDN = new UserDN(config.getSearchBaseDN(), config.getSpecificUserDN());

            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, specificUserDN.getAbsoluteUserDN());
            env.put(Context.SECURITY_CREDENTIALS, config.getSpecificUserPassword());

        } else {
            // Anonymous binding
            env.put(Context.SECURITY_AUTHENTICATION, "none");
        }

        // Determine protocol
        if (config.isUseSSL()) {
            // SSL communication
            env.put(Context.SECURITY_PROTOCOL, "ssl");
        
        } else {
            // No protocol needs specified
        
        }

        DirContext context = null;

        try {
            // Create the initial context
            // This represents the searchBaseDN entry (since we
            // specified that during connection)
            context = new InitialDirContext(env);

        } catch (javax.naming.AuthenticationException e) {
            // Problem authenticating with a specific user for
            // anonymous lookups
            // This is a configuration issue
            throw new ConfigurationException("Error performing non-authenticated access; check specific user DN in configuration: " + e, e);

        } catch (javax.naming.NameNotFoundException e) {
            // The search base was wrong; this is a fatal error
            // In practice, found this exception _not_ to be thrown at this
            // point;
            // Construct detail package
            // provider_url, security_auth, security_protocol
            
            throw new ConfigurationException("Error fetching search context; check search base DN in configuration: " + e, e);

        } catch (javax.naming.NamingException e) {
            // Some other fatal error
            throw new DirectoryException("Error fetching search context: " + e, e);

        }

        return context;
    }


    /**
     * Returns the fully specified user DN after looking up the
     * entry with the matching username in the specified context.
     * The context is assumed to refer to the searchBaseDN as specified
     * in the configuration so that the full user DN can be constructed.
     * <p>
     * For example, if the DirContext represents this search base: <br>
     * <code>ldap://ldap1.company.com/dc=company, dc=com</code> <br>
     * the username attribute is <code>uid</code>, the configuration
     * specifies to search subtrees and there is an entry at
     * <code>uid=foo, ou=People, dc=company, dc=com</code>, 
     * then the following call: <br>
     * <code>findUserDN(context, config, "foo")</code> <br>
     * would return a UserDN object with relative iser DN of
     * <code>uid=foo, ou=People</code> and an absolute DN of
     * <code>uid=foo, ou=People, dc=company, dc=com</code>.
     * </p>
     * @param context the context in which to perform the search
     * @param config the configuration
     * @param username the username to locate
     * @return the user DN information
     * @throws AuthenticationFailedException if the username cannot be found
     * @throws ConfigurationException if there is a problem with the
     * configuration, such as a bad search subtree
     * @throws TooManyEntriesException if more than one entry found for
     * the specified username
     * @throws DirectoryException if some other problem occurs
     */
    UserDN findUserDN(DirContext context, LDAPDirectoryConfiguration config, String username) 
            throws AuthenticationFailedException, ConfigurationException, 
                TooManyEntriesException, DirectoryException {

        //
        // Do the search
        //

        // Figure out where to search from
        // If a subtree has been specified, then we limit our
        // search to that
        // Otherwise, the search is in the entire directory from
        // the base DN
        String searchFrom = null;
        
        if (config.getSearchType().equals(SearchType.LIMIT_SUBTREES)) {
            searchFrom = config.getSearchSubtrees();
        
        } else {
            searchFrom = "";
        
        }

        // Build the filter from the username attribute and its value
        // For example, it might look like "(&(objectClass=person)(username=foo))"
        String searchFilter = buildSearchFilter(config.getSearchFilterExpression(), config.getUsernameAttribute(), username);
        
        // Build additional search parameters
        // All searches are based on a subtree
        // Return just enough results to detect a duplicate entry
        // This is absolutely CRITICAL; if the user enteres a wildcard in
        // the username (for example '*') our search will locate multiple records
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setCountLimit(1);

        UserDN foundUserDN = null;
        boolean foundUser = false;

        try {
            
            // Search
            
            // Parse the search name to ensure it is valid for
            // this context
            NameParser nameParser = context.getNameParser("");
            Name searchFromName = nameParser.parse(searchFrom);
            NamingEnumeration results = context.search(searchFromName, searchFilter, controls);

            if (results.hasMore()) {
                SearchResult result = (SearchResult) results.next();

                // Fetch the DN of this result
                String userDN = result.getName();
                String relativeUserDN = null;
                
                // Now make absolute user DN
                // If we searched a specific subtree, then the DN
                // we have is relative _from below_ that subtree
                // Otherwise, it is relative from the search base
                if (searchFrom != null && searchFrom.length() > 0) {
                    relativeUserDN = userDN + ", " + searchFrom;

                } else {
                    relativeUserDN = userDN;

                }

                // Record information in UserDN structure
                foundUserDN = new UserDN(config.getSearchBaseDN(), relativeUserDN);
                foundUser = true;
            
                // VERY IMPORTANT CODE
                // A SizeLimitExceededException is only thrown when attempting
                // to access a result beyond the specified size limit
                // Therefore, we must attempt to get a result beyond our limit of 1
                // To cause an exception to occur
                // If we call this method and it returns true, the appropriate exception
                // will be thrown
                // Check duplicate user entries if username contains *
                if(username.indexOf("*") > -1){
                	results.hasMore();
                }

            }


            results.close();

        } catch (javax.naming.InvalidNameException e) {
            // Possibly subtree RDN was not correct
            throw new ConfigurationException("Error authenticating user; check LDAP search subtree configuration: " + e, e);
        
        } catch (javax.naming.NameNotFoundException e) {
            // The search base was wrong; this is a fatal error
            throw new ConfigurationException("Error authenticating user; check LDAP search base DN: " + e, e);

        } catch (javax.naming.SizeLimitExceededException e) {
            // Duplicate matching values
            // Could happen if someone enters a wildcard in the
            // username field
            throw new TooManyEntriesException("Error authenticating user; too many entries found: " + e, e);

        } catch (javax.naming.directory.InvalidSearchFilterException e) {
            throw new ConfigurationException("Error authenticating user; check LDAP object limit filter: " + e, e);
        
        } catch (javax.naming.NamingException e) {
            throw new DirectoryException("Error authenticating user: " + e, e);
        
        }

        // At this point we should have some results

        if (!foundUser) {
            // Not found; could mean bad attribute name, bad username
            throw new AuthenticationFailedException("Invalid username or password");
        
        } else {
            // One result
            // Simply returns the found DN

        }

        return foundUserDN;
    }


    /**
     * Returns an LDAP Directory Entry for the specified user attributes.
     * @param attributeMap the attribute mapping specifying which
     * ldap attributes correspond to profile attributes
     * @param attributes the LDAP attribute values to build the
     * directory entry from
     * @return the directory entry
     * @throws DirectoryException if there is a problem reading any
     * attributes
     */
    public LDAPDirectoryEntry makeDirectoryEntry(LDAPAttributeMap attributeMap, Attributes attributes) 
            throws DirectoryException {
        
        LDAPDirectoryEntry entry = new LDAPDirectoryEntry();

        try {
            
            // The attribute map that we recieve is of the form
            //     profileAttribute --> ldapAttributeDefinition
            //
            // where profileAttribute consists of an ID and
            // ldapAttributeDefinition consists of a name and
            // other properties
            // 
            // The attributes from LDAP are effectively of the form
            //     ldapAttributeName --> ldapAttributeValues
            //
            // where ldapAttributeValues is actually a collection of value objects


            // We build the final map of the form
            //    personAttribute --> ldapAttributeValueObject
            //
            // by iterating through the first map, looking up
            // attribute values using the attribute name
            // and selecting an appropriate value object

            // This maps profileAttributes (keys from the attributeMap)
            // to LDAP entry attribute value objects
            HashMap attributeToValueMap = new HashMap(attributeMap.size());
            
            // Iterate over attribute keys (which are of type IAttribute)
            for (Iterator it = attributeMap.keySet().iterator(); it.hasNext(); ) {
                
                // Grab the next profile attribute
                // and get the attribute definition corresponding
                // to that profile attribute
                net.project.base.attribute.IAttribute nextProfileAttribute = (net.project.base.attribute.IAttribute) it.next();
                LDAPAttributeMap.LDAPAttributeDefinition attributeDefinition = 
                        (LDAPAttributeMap.LDAPAttributeDefinition) attributeMap.get(nextProfileAttribute);
                
                // Armed with the ldap Attribute name, we can get
                // the actual JNDI Attribute
                Attribute attribute = attributes.get(attributeDefinition.getName());

                if (attribute == null) {
                    // No attribute found in LDAP record
                    // with that name
                    // Quietly continue
                
                } else {
                    // LDAP attributes can be multi-valued
                    // our attribute definition specifies whether
                    // we are interested in getting a specific value
                    // or a random value
                    Object value = null;
                    if (attributeDefinition.isValueIndexSpecified()) {
                        // We care which value to get in a multi-valued
                        // attribute
                        value = attribute.get(attributeDefinition.getValueIndex());

                    } else {
                        // We don't care which value to get (or maybe
                        // there is only one in this attribute)
                        value = attribute.get();

                    }

                    // Now we can add the selected value into the
                    // profile attribute id map
                    attributeToValueMap.put(nextProfileAttribute, value);
                
                }

            }


            // Given the profile attribute ID to value map, we can
            // populate the directory entry
            entry.populate(attributeToValueMap);

        } catch (javax.naming.NamingException e) {
            throw new DirectoryException("Error reading LDAP attributes: " + e, e);
        
        }

        return entry;
    }


    /**
     * Fetches the attributes of a user, authenticating as that user.
     * @param context the context used to access record; it is assumed
     * that this represents the same baseDN as recorded in the UserDN
     * @param userDN the userDN to authenticate as and fetch attributes for
     * @param clearTextPassword the password to authenticate with
     * @return the Attributes
     * @throws AuthenticationFailedException if the specified password is null
     * or the empty string; or if the specified DN cannot be authenticated with
     * that password; or if an {@link javax.naming.OperationNotSupportedException}
     * occurs, which may imply the account is inactive
     * @throws DirectoryException if a general problem occurs authenticating
     */
    Attributes fetchAuthenticatedAttributes(DirContext context, UserDN userDN, String clearTextPassword)
             throws AuthenticationFailedException, DirectoryException {
        
        Attributes userAttributes = null;

        // THE MOST CRITICAL CODE IN THIS WHOLE PACKAGE
        // If the password is empty, LDAP automatically defaults to authentication
        // type of "none".  That is, it uses ANONYMOUS authentication.
        // If we don't catch this, users would be able to authenticate by entering
        // a blank password
        if (clearTextPassword == null || clearTextPassword.trim().length() == 0) {
            throw new AuthenticationFailedException("Password is required for authentication");
        }

        try {
            // Simple authentication
            context.removeFromEnvironment(Context.SECURITY_AUTHENTICATION);
            context.addToEnvironment(Context.SECURITY_AUTHENTICATION, "simple");
            context.addToEnvironment(Context.SECURITY_PRINCIPAL, userDN.getAbsoluteUserDN());
            context.addToEnvironment(Context.SECURITY_CREDENTIALS, clearTextPassword);
            
            userAttributes = context.getAttributes(userDN.getRelativeUserDN());

        } catch (javax.naming.AuthenticationException e) {
            // A problem authenticating
            throw new AuthenticationFailedException("Invalid username or password", e);
        
        } catch (javax.naming.OperationNotSupportedException e) {
            // There are realtively few causes of this exception, especially
            // when the only thing we are doing is authenticating and
            // fetching attributes
            // Most likely the user account has been inactivated on
            // the ldap server; we will assume this
            throw new AuthenticationFailedException("Invalid username or password; account may be inactive", e);

        } catch (javax.naming.NamingException e) {
            throw new DirectoryException("Authentication failed: " + e, e);

        }

        return userAttributes;
    }

    /**
     * Fetches the attributes of a user from an existing context.
     * @param context the context used to access record; it is assumed
     * that this represents the same baseDN as recorded in the UserDN
     * @param userDN the userDN to fetch attributes for
     * @return the Attributes
     * @throws DirectoryException if a problem occurs fetching the
     * attributes for the specified DN
     */
    Attributes fetchAttributes(DirContext context, UserDN userDN)
             throws DirectoryException {
        
        Attributes userAttributes = null;

        try {
           
            userAttributes = context.getAttributes(userDN.getRelativeUserDN());

        } catch (javax.naming.NamingException e) {
            throw new DirectoryException("Authentication failed: " + e, e);

        }

        return userAttributes;
    }


    /**
     * Searches using the specified search context.
     * @param context the context from which to perform the search
     * @param config the configuration providing all the parameters for the
     * search
     * @param filter the filter to apply while searching; if <code>null</code> or
     * empty, assumes that the search is for all objects and uses a filter of
     * <code>(objectclass=*)</code>
     * @param controls the parameters affecting the search
     * @return the enumeration of the results
     * @throws DirectoryException if there is a problem searching
     */
    public NamingEnumeration search(DirContext context, LDAPDirectoryConfiguration config, String filter, net.project.base.directory.search.SearchControls controls)
            throws DirectoryException {

        NamingEnumeration results = null;

        // Figure out where to search from
        // If a subtree has been specified, then we limit our
        // search to that
        // Otherwise, the search is in the entire directory from
        // the base DN
        String searchFrom = null;
        
        if (config.getSearchType().equals(SearchType.LIMIT_SUBTREES)) {
            searchFrom = config.getSearchSubtrees();
        
        } else {
            searchFrom = "";
        
        }

        // Build additional search parameters
        // All searches are based on a subtree
        SearchControls ldapSearchControls = new SearchControls();
        ldapSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        // It is illegal to have an empty filter;
        // In our scenario, an empty filter generally means to filter criteria was entered
        // In this case we default to all objects
        if (filter == null || filter.trim().length() == 0) {
            filter = "(objectclass=*)";
        }

        try {
            
            // Search
            
            // Parse the search name to ensure it is valid for
            // this context
            NameParser nameParser = context.getNameParser("");
            Name searchFromName = nameParser.parse(searchFrom);
            
            results = context.search(searchFromName, filter, ldapSearchControls);

        } catch (javax.naming.InvalidNameException e) {
            // Possibly subtree RDN was not correct
            throw new ConfigurationException("Error searching; check LDAP search subtree configuration: " + e, e);
        
        } catch (javax.naming.NameNotFoundException e) {
            // The search base was wrong; this is a fatal error
            throw new ConfigurationException("Error searching; check LDAP search base DN: " + e, e);

        } catch (javax.naming.directory.InvalidSearchFilterException e) {
            // Problem with search filter; possibly empty
            throw new DirectoryException("Error searching; invalid search filter.  Check LDAP filter configuration or invalid characters entered in search: " + e, e);
        
        } catch (javax.naming.NamingException e) {
            throw new DirectoryException("Error searching: " + e, e);
        
        }

        return results;
    }

    /**
     * Constructs a Java 2 SE 1.4.1 compatible provider URL.
     * This is a list of space-separated LDAP URls built from the specified
     * collection of host entries and the search base DN from the specified
     * configuration
     * @param hostEntryCollection the collection of <code>HostEntry</code>s,
     * each of which provides a hostname and port for a single LDAP URL
     * @param config the LDAP configuration which provides the search base DN
     * to add to the LDAP URL
     * @return a space-separated list of LDAP URLs where each URL conforms
     * to RFC 2555; suitable for use as a Java 2 SE 1.4.1 provider URL
     * @throws InvalidPortException if a host entry contains an invalid port
     */
    private static String buildProviderURL(Collection hostEntryCollection, LDAPDirectoryConfiguration config)
            throws InvalidPortException {

        // Will contain the list of space-separated LDAP URLs
        StringBuffer providerURL = new StringBuffer();

        // Iterate over each host entry, appending the complete LDAP URL
        // to the provider URL
        for (Iterator it = hostEntryCollection.iterator(); it.hasNext(); ) {
            LDAPDirectoryConfiguration.HostEntry nextHost = (LDAPDirectoryConfiguration.HostEntry) it.next();

            // Add a space if this is the second or subsequent one
            if (providerURL.length() > 0) {
                providerURL.append(" ");
            }

            // Add the built LDAP URL
            providerURL.append(buildLDAPURL(nextHost.getHostname(), nextHost.getRealizedPort(), config.getSearchBaseDN()));

        }

        return providerURL.toString();
    }


    /**
     * Constructs the correct URL for connecting to the ldap hostname.
     * This conforms to RFC 2555 LDAP URL specification.  Spaces are escaped
     * to the triplet <code>%20</code>.
     * @param hostname the hostname; this is a required value
     * @param port the port; this is a required value
     * @param searchBaseDN; if specified, it is included in the URL
     * @return the URL; for example <code>ldap://ldap.bigfoot.com:389</code>
     * or <code>ldap://directory.netscape.com/dc=netscape,%20dc=com</code>
     * @throws InvalidPortException if the port is specified and is not a number
     */
    private static String buildLDAPURL(String hostname, String port, String searchBaseDN)
            throws InvalidPortException {

        // First check that we have the required hostname and port
        if (hostname == null || hostname.trim().length() == 0 ||
            port == null || port.trim().length() == 0) {

            throw new IllegalArgumentException("Missing hostname or port");
        }

        try {
            // Ensure port is a number
            Integer.valueOf(port);

        } catch (NumberFormatException e) {
            throw new InvalidPortException("Port is not a number: " + port);

        }

        // Construct url, something like ldap://host:port/searchbasedn
        StringBuffer url = new StringBuffer();
        url.append(LDAP_PROTOCOL).append(hostname).append(":").append(port);

        if (searchBaseDN != null && searchBaseDN.trim().length() > 0) {
            url.append("/").append(searchBaseDN.trim());
        }

        // Now escape spaces to %20 as defined by RFC 2555
        // Java 2 SE 1.4.1 introduced a new feature that causes unescaped
        // spaces to cause errors
        return url.toString().replaceAll(" ", "%20");
    }


    /**
     * Returns a search filter based on the specified attribute and value.
     * Search filter is of the form: <code>(attribute=value)</code>.
     * Adds in any additional filter expression stored in the configuration.
     * This filter is suitable for search based on a single attribute value.
     * @param additionalFilterExpression the additional filter expression to <b>AND</b>
     * with the attribute/value expression
     * @param attribute the attribute name
     * @param value the value
     * @return the search filter
     */
    private static String buildSearchFilter(String additionalFilterExpression, String attribute, String value) {

        // Construct filter like (attribute=value)
        StringBuffer attributeValueFilter = new StringBuffer();
        attributeValueFilter.append("(").append(attribute).append("=").append(value).append(")");

        StringBuffer filter = new StringBuffer();

        if (additionalFilterExpression != null && additionalFilterExpression.trim().length() > 0) {
            // Add the additional filter expression, ANDing it with the
            // attribute value expression
            filter.append("(&");
            filter.append(additionalFilterExpression);
            filter.append(attributeValueFilter);
            filter.append(")");
        } else {
            // Simply use the attribute value expression
            filter.append(attributeValueFilter.toString());
        }

        return filter.toString();
    }

    //
    // Nested top-level classes
    //

    /**
     * Indicates a port is invalid (not a number).
     */
    private static class InvalidPortException extends Exception {
        
        private InvalidPortException(String message) {
            super(message);
        }
    
    }

    /**
     * Represents the DNs to access a user's record.
     */
    static class UserDN {

        private String baseDN = null;
        private String relativeUserDN = null;

        /**
         * Creates a new UserDN specifing the absolute DN and
         * the DN relative to the base DN.
         * @param baseDN the DN of the search base; the relativeUserDN
         * is relative to this DN
         * @param relativeUserDN the DN to access the user record
         * from the baseDN
         */
        private UserDN(String baseDN, String relativeUserDN) {
            this.baseDN = baseDN;
            this.relativeUserDN = relativeUserDN;
        }

        /**
         * Returns the relaitve user DN property.
         * @return the DN relative to the base DN
         */
        private String getRelativeUserDN() {
            return this.relativeUserDN;
        }

        /**
         * Returns the absolute DN for the user which includes
         * both the relativeDN and the baseDN.
         * @return the fully specified userDN
         */
        private String getAbsoluteUserDN() {
            return new StringBuffer(this.relativeUserDN).append(", ").append(baseDN).toString();
        }

    }

}
