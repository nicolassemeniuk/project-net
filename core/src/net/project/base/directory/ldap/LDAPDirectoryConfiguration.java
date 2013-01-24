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

import java.util.List;
import java.util.StringTokenizer;

import net.project.database.DBBean;
import net.project.persistence.NotSupportedException;
import net.project.persistence.PersistenceException;
import net.project.util.Conversion;

/**
 * Maintains the LDAP Directory configuration information for
 * connecting to an LDAP Directory and authenticating against that
 * directory.
 * <p>
 * Two sets of hostname:port values are kept; one for regular communications
 * and one for secure communications. Each set is of the form <br>
 * <code>hostname[:port]{,hostname[:port]}</code> <br>
 * If the <code>port</code> is not specified for any hostname, the
 * default port appropriate for the type of communication is assumed.
 * </p>
 * 
 */
public class LDAPDirectoryConfiguration extends net.project.base.directory.DirectoryConfiguration {
    
    //
    // Static members
    //

    private static final String HOSTNAME_SEPARATOR = ",";
    private static final String HOSTNAME_PORT_SEPARATOR = ":";

    /** LDAP default port number, currently <code>389</code>. */
    public static final String LDAP_PORT = "389";

    /** LDAP default SSL port number, currently <code>636</code>. */
    public static final String LDAP_PORT_SSL = "636";

    /**
     * Populates the specified configuration object from the specified
     * ResultSet.
     * @param result the ResultSet to populate; must include the appropriate
     * columns
     * @param config the LDAPDirectoryConfiguraiton to populate
     * @throws java.sql.SQLException if there is a problem reading
     * from the ResultSet, for example a required column value is missing
     */
    static void populate(java.sql.ResultSet result, LDAPDirectoryConfiguration config) 
            throws java.sql.SQLException {

        config.setHostnameValues(result.getString("hostname_values"));
        config.setSecureHostnameValues(result.getString("secure_hostname_values"));
        config.setUseSSL(Conversion.toBoolean(result.getString("is_use_ssl")));
        config.setSearchBaseDN(result.getString("search_base_dn"));
        config.setSearchTypeID(result.getString("search_type_id"));
        config.setSearchSubtrees(result.getString("search_subtrees"));
        config.setUsernameAttribute(result.getString("username_attribute_name"));
        config.setNonAuthenticatedAccessTypeID(result.getString("non_auth_access_type_id"));
        config.setSpecificUserDN(result.getString("specific_user_relative_dn"));
        config.setSpecificUserPassword(result.getString("specific_user_password"));
        config.setAvailableForDirectorySearch(Conversion.toBoolean(result.getString("is_available_directory_search")));
        config.setDirectorySearchDisplayName(result.getString("directory_search_display_name"));
        config.setSearchFilterExpression(result.getString("search_filter_expression"));
        config.setAllowsAutomaticRegistration(Conversion.toBoolean(result.getString("allows_automatic_registration")));
    }


    //
    // Instance members
    //

    /** The comma separated list of hostname:port values. */
    private String hostnameValues = null;

    /** The HostEntry list, derived from the <code>hostnameValues</code>. */
    private List hostEntryList = new java.util.ArrayList();
    
    /** 
     * The comma separated list of hostname:port values used for
     * secure communications.
     */
    private String secureHostnameValues = null;

    /** The secure HostEntry list, derived from the <code>secureHostnameValues</code> */
    private List secureHostEntryList = new java.util.ArrayList();
    
    /** Indicates whether to use SSL secure communications. */
    private boolean isUseSSL = false;
    
    /** The base DN used for searching. */
    private String searchBaseDN = null;

    /** The type of search, whether all subtrees or specific subtrees. */
    private SearchType searchType = null;

    /** The specific subtrees to search (for specific subtree search type). */
    private String searchSubtrees = null;

    /** The attribute that contains the unique username value. */
    private String usernameAttribute = null;

    /** The type of non-authenticated access. */
    private NonAuthenticatedAccessType nonAuthenticatedAccessType = null;

    /** The specific user required for that kind of nonauthenticated access. */
    private NonAuthenticatedSpecificUser specificUser = new NonAuthenticatedSpecificUser();

    /** Indicates if this LDAP Directory is available for directory searches. */
    private boolean isAvailableForDirectorySearch = false;

    /** Indicates if this LDAP Domain allows automatic registration for new users at login */
    private boolean allowsAutomaticRegistration = false;

    /** The display name for this directory for directory searches. */
    private String directorySearchDisplayName = null;

    /** The search filter expression to AND with all searches. */
    private String searchFilterExpression = null;

    /** The attribute map defined in this configuration. */
    private LDAPAttributeMap attributeMap = null;

    /**
     * Indicates whether the current configuration has been loaded
     * from the database; used to determine whether to insert or update
     * when storing.
     */
    private boolean isLoaded = false;


    /**
     * Sets the comma separated list of hostname:port values.
     * These are used in a round-robin fashion when trying to
     * connect to an LDAP server when connection to a particular
     * hostname and port fails.
     * @param hostnameValues the hostname:port values
     */
    public void setHostnameValues(String hostnameValues) {
        this.hostnameValues = hostnameValues;
        convertHostnames(this.hostnameValues, this.hostEntryList, LDAP_PORT);
    }


    /**
     * Returns the comma separated list of hostname:port values.
     * @return the values
     */
    public String getHostnameValues() {
        return this.hostnameValues;
    }


    /**
     * Returns the hostnames as a list of <code>HostEntry</code>s.
     * @return the list
     */
    private List getHostEntryList() {
        return this.hostEntryList;
    }

    /**
     * Sets the comma separated list of hostname:port values for
     * secure LDAP communication.
     * These are maintained separately as a convenience since different 
     * ports are required for secure communications; by keeping a separate
     * list, secure communication can be toggled on and off without
     * re-entering all the hostnames and ports.
     * @param secureHostnameValues the hostname:port values
     */
    public void setSecureHostnameValues(String secureHostnameValues) {
        this.secureHostnameValues = secureHostnameValues;
        convertHostnames(this.secureHostnameValues, this.secureHostEntryList, LDAP_PORT_SSL);
    }

    /**
     * Returns the comma separated list of hostname:port values for
     * secure LDAP communication.
     * @return the values
     */
    public String getSecureHostnameValues() {
        return this.secureHostnameValues;
    }

    /**
     * Returns the secure hostnames as a list of <code>HostEntry</code>s.
     * @return the list
     */
    private List getSecureHostEntryList() {
        return this.secureHostEntryList;
    }

    /**
     * Returns the list of <code>HostEntry</code>; this will either be
     * the regular or secure list, depending on {@link #isUseSSL}. <br>
     * <b>Note:</b> If <code>isUseSSL</code> is true, then the hostname
     * entries will be specifying SSL ports; if a non-SSL connection
     * is opened to an SSL port, then the connection will hang.
     * @return the HostEntry list
     */
    protected List getHostEntryListForConnection() {
        
        List list = null;

        if (isUseSSL()) {
            // SSL communication, use the secure hostnames and ports
            list = getSecureHostEntryList();
        
        } else {
            // Non-SSL communication, use the regular hostnames and ports
            list = getHostEntryList();
        
        }

        return list;
    }

    /**
     * Specifies whether to use SSL for connection.
     * This affects the HostEntry list returned by {@link #getHostEntryListForConnection}.
     * @param isUseSSL true means to use SSL for connection; false
     * means to use non-SSL connection
     * @see #isUseSSL
     */
    public void setUseSSL(boolean isUseSSL) {
        this.isUseSSL = isUseSSL;
    }

    /**
     * Indicates whether to use SSL for connection.
     * @return true if SSL must be used for connection; false if
     * to use regular connection
     * @see #setUseSSL
     */
    public boolean isUseSSL() {
        return this.isUseSSL;
    }

    /**
     * Sets the DN for the search base.
     * All searches will be performed in the subtrees below this DN.
     * @param searchBaseDN the base DN below which to search
     * @see #getSearchBaseDN
     */
    public void setSearchBaseDN(String searchBaseDN) {
        this.searchBaseDN = searchBaseDN;
    }

    /**
     * Returns the DN below which searches occur.
     * @return the search base DN
     * @see #setSearchBaseDN
     */
    public String getSearchBaseDN() {
        return this.searchBaseDN;
    }

    /**
     * Sets how to perform a search.
     * Specifies the internal ID of a search type.
     * @see #setSearchType
     */
    public void setSearchTypeID(String searchTypeID) {
        setSearchType(SearchType.forID(searchTypeID));
    }

    /**
     * Returns the internal id of the current search type.
     * @return the id
     * @see #getSearchType
     */
    public String getSearchTypeID() {
        return (getSearchType() == null ? null : getSearchType().getID());
    }

    /**
     * Sets how to perform a search.
     * A value of <code>{@link SearchType#ALL_SUBTREES}</code> means
     * that all branches below the search base DN will be searched;
     * A value of <code>{@link SearchType#LIMIT_SUBTREES}</code> means
     * that the <code>SearchSubtrees</code> value will be used to
     * limit the search to specific subtree(s).
     * @param searchType the search type
     * @see #getSearchType
     */
    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }

    /**
     * Indicates the type of search to be performed.
     * @return the search type
     * @see #setSearchType
     */
    public SearchType getSearchType() {
        return this.searchType;
    }

    /**
     * Sets the RDN of subtree below which to search.
     * For example, <code>ou=People</code> would limit searches
     * to that organizational unit. <br>
     * <b>Note:</b> Currently only ONE subtree is supported.
     * @param searchSubtrees the subtrees to limit search to
     * @see #getSearchSubtrees
     */
    public void setSearchSubtrees(String searchSubtrees) {
        this.searchSubtrees = searchSubtrees;
    }

    /**
     * Indicates the subtrees to which searching will be limited.
     * @return the subtree RDN
     * @see #setSearchSubtrees
     */
    public String getSearchSubtrees() {
        return this.searchSubtrees;
    }

    /**
     * Specifies the attribute name that contains the unique username.
     * This is matched with the authentication username during a search 
     * to fetch the DN of the record, which is then used to perform
     * authentication.  For example, <code>alias</code>.
     * @param usernameAttribute the name of the attribute containing
     * the username
     * @see #getUsernameAttribute
     */
    public void setUsernameAttribute(String usernameAttribute) {
        this.usernameAttribute = usernameAttribute;
    }

    /**
     * Returns the username attribute name.
     * @return the attribute name that contains the username
     * @see #setUsernameAttribute
     */
    public String getUsernameAttribute() {
        return this.usernameAttribute;
    }


    /**
     * Sets the NonAuthenticatedAccessType for the specified id.
     * @param nonAuthenticatedAccessTypeID the id
     */
    public void setNonAuthenticatedAccessTypeID(String nonAuthenticatedAccessTypeID) {
        setNonAuthenticatedAccessType(NonAuthenticatedAccessType.forID(nonAuthenticatedAccessTypeID));
    }

    /**
     * Returns the NonAuthenticatedAccessType id.
     * @return the id
     */
    public String getNonAuthenticatedAccessTypeID() {
        return (getNonAuthenticatedAccessType() == null ? null : getNonAuthenticatedAccessType().getID());
    }

    /**
     * Sets the NonAuthenticatedAccessType.
     * @param type the type
     */
    public void setNonAuthenticatedAccessType(NonAuthenticatedAccessType type) {
        this.nonAuthenticatedAccessType = type;
    }

    /**
     * Returns the NonAuthenticatedAccessType.
     * @return the nonAuthenticatedAccessType
     */
    public NonAuthenticatedAccessType getNonAuthenticatedAccessType() {
        return this.nonAuthenticatedAccessType;
    }

    /**
     * Sets the nonauthenticated access specific user's userDN.
     * This specific user is used when anonymous access is not supported.
     * @param userDN the username
     */
    public void setSpecificUserDN(String userDN) {
        this.specificUser.userDN = userDN;
    }

    /**
     * Returns the current userDN for the nonauthenticated access
     * specific user.
     * @return the username
     */
    public String getSpecificUserDN() {
        return this.specificUser.userDN;
    }

    /**
     * Sets the nonauthenticated access specific user's password.
     * @param password the password
     */
    public void setSpecificUserPassword(String password) {
        this.specificUser.password = password;
    }

    /**
     * Returns the current password for the nonauthenticated access
     * specific user.
     * @return the password
     */
    public String getSpecificUserPassword() {
        return this.specificUser.password;
    }

    
    /**
     * Specifies whether this LDAP Directory is available for directory search.
     * @param isAvailableForDirectorySearch true if the directory
     * is available for searching; false if not
     * @see #isAvailableForDirectorySearch
     */
    public void setAvailableForDirectorySearch(boolean isAvailableForDirectorySearch) {
        this.isAvailableForDirectorySearch = isAvailableForDirectorySearch;
    }

    /**
     * Indicates whether this LDAP Directory is available for directory search.
     * Currently, workspace invitation allows searching of these
     * directories.
     * @return isAvailableForDirectorySearch true if the directory
     * is available for searching; false if not
     * @see #setAvailableForDirectorySearch
     */
    public boolean isAvailableForDirectorySearch() {
        return this.isAvailableForDirectorySearch;
    }

    /** Indicates whether this UserDomain allows automatic registration of new users.
     * If the value is true, then new users of this domain will not have to register, rather they
     * can simply login to the domain.  At first login, and upon successful authentication, the registration
     * process will be completed behind the scenes.
     * @return allowsAutomaticRegistration true if the domain allows automatic registration for new users; false if not.
     */
    public boolean allowsAutomaticRegistration() {
        return this.allowsAutomaticRegistration;
    }

    /** Specifies whether this UserDomain will allow automatic registration of new users.
     * @param autoRegistration true if the domain allows auto registration; false if not
     * @see #allowsAutomaticRegistration
     */
    public void setAllowsAutomaticRegistration(boolean autoRegistration) {
        this.allowsAutomaticRegistration = autoRegistration;
    }

    /** Specifies whether this UserDomain will allow automatic registration of new users.
     * @param autoRegistration true if the domain allows auto registration; false if not
     * @see #allowsAutomaticRegistration
     */
    public void setAllowsAutomaticRegistration (String autoRegistration) {
        setAllowsAutomaticRegistration(Conversion.toBoolean(autoRegistration));
    }

    /**
     * Specifies the name to display for chosing this directory
     * to search in.
     * Only used if <code>isAvailableForDirectorySearch</code>.
     * @param directorySearchDisplayName the display name for this
     * directory
     * @see #getDirectorySearchDisplayName
     */
    public void setDirectorySearchDisplayName(String directorySearchDisplayName) {
        this.directorySearchDisplayName = directorySearchDisplayName;
    }

    /**
     * Returns the name displayed when chosing this directory to search
     * in.
     * @return the display name
     * @see #setDirectorySearchDisplayName
     */
    public String getDirectorySearchDisplayName() {
        return this.directorySearchDisplayName;
    }

    /**
     * Sets the additional search filter expression to apply to
     * all search operations.
     * <p>
     * This can be used to limit searches
     * to objects of a certain class, or objects with a particular
     * attribute.  For example: <code>(objectClass=person)</code> or
     * <code>(&(objectClass=person)(mail=*))</code>.
     * </p>
     * @param searchFilterExpression the LDAP search filter expression
     * @see #getSearchFilterExpression
     */
    public void setSearchFilterExpression(String searchFilterExpression) {
        this.searchFilterExpression = searchFilterExpression;
    }

    /**
     * Returns the current search filter expression.
     * @return the search filter expression
     * @see #setSearchFilterExpression
     */
    public String getSearchFilterExpression() {
        return this.searchFilterExpression;
    }

    /**
     * Specifies the attribute map for this configuration.
     * @param attributeMap the attribute map for this configuration
     * @see #getAttributeMap
     */
    protected void setAttributeMap(LDAPAttributeMap attributeMap) {
        this.attributeMap = attributeMap;
    }

    /**
     * Returns the attribute map for this configuration.
     * @return the attribute map
     */
    public LDAPAttributeMap getAttributeMap() {
        return this.attributeMap;
    }

    /**
     * Specifies whether this configuration has been loaded from
     * persistent store.
     * This affects whether an insert or update is performed during
     * a store operation.
     * @param isLoaded a boolean containing true if this configuration has been
     * loaded; false otherwise
     */
    private void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    /**
     * Indicates whether this configuration has been loaded from
     * persistent store.
     * @return true if this configuraiton has been loaded; false
     * otherwise
     */
    private boolean isLoaded() {
        return this.isLoaded;
    }

    /**
     * Loads the LDAPDirectoryConfiguration stored against the current
     * contextID.
     * Also loads the attribute map for the configuration.
     * @throws NullPointerException if the current domainID is null
     * @throws PersistenceException if there is
     * a problem loading
     * @see #setDomainID(String)
     * @see #getAttributeMap
     */
    public void load() throws PersistenceException {
        
        if (getDomainID() == null) {
            throw new NullPointerException("Current domainID is null");
        }

        StringBuffer loadQuery = new StringBuffer();
        loadQuery.append("select ldc.hostname_values, ldc.secure_hostname_values, ");
        loadQuery.append("ldc.is_use_ssl, ldc.search_base_dn, ldc.search_type_id, ldc.search_subtrees, ");
        loadQuery.append("ldc.username_attribute_name, ldc.allows_automatic_registration, ");
        loadQuery.append("ldc.non_auth_access_type_id, ldc.specific_user_relative_dn, ldc.specific_user_password, ");
        loadQuery.append("ldc.is_available_directory_search, ldc.directory_search_display_name, ldc.search_filter_expression ");
        loadQuery.append("from pn_ldap_directory_config ldc ");
        loadQuery.append("where ldc.context_id = ? ");

        DBBean db = new DBBean();

        try {
            int index = 0;
            db.prepareStatement(loadQuery.toString());
            db.pstmt.setString(++index, getDomainID());
            db.executePrepared();

            if (db.result.next()) {
                LDAPDirectoryConfiguration.populate(db.result, this);
            
                setLoaded(true);

            } else {
                // No stored configuration yet
                // This is not an error; the configuration has yet to
                // be created

            }

            // Now load the attribute map for this configuration
            // Note: THis load is performed regardless of the presence of data in
            // the configuration
            // This ensures that the attribute map is at least initialized
            // to be empty
            loadAttributeMap();

            
        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("LDAP Directory Configuration load operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

    }

    /**
     * Loads the attribute map for the current contextID.
     * After calling, attribute map will be available.
     * @throws PersistenceException if there is a problem loading
     * @see LDAPAttributeMap#load
     */
    private void loadAttributeMap() throws PersistenceException {
        
        // Load attribute map
        LDAPAttributeMap map = new LDAPAttributeMap();
        map.setDomainID(getDomainID());
        map.load();
        
        // Save in this instance only upon successful load
        setAttributeMap(map);
    }


    /**
     * Stores this configuration information.
     * Also stores the attribute map.
     * @throws PersistenceException if there is a problem storing
     */
    public void store() throws PersistenceException {

        DBBean db = new DBBean();

        try {
            
            db.setAutoCommit(false);

            if (isLoaded()) {
                update(db);

            } else {
                insert(db);

            }

            // Now store the attribute map
            getAttributeMap().store(db);

            db.commit();
        
            // Now that it is stored successfully, it can be regarded
            // as having been freshly loaded
            setLoaded(true);

        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("LDAP Directory Configuration store operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

    }

    /**
     * Updates existing configuration settings in the database.
     * Note: No commit or rollback is performed
     * @param db the DBBean in which to perform the transaction
     * @throws PersistenceException if there is a problem updating
     */
    private void update(DBBean db) throws PersistenceException {

        StringBuffer updateStatement = new StringBuffer();
        updateStatement.append("update pn_ldap_directory_config ");
        updateStatement.append("set hostname_values = ?, secure_hostname_values = ? ");
        updateStatement.append(", is_use_ssl = ?, search_base_dn = ?, search_type_id = ? ");
        updateStatement.append(", search_subtrees = ?, username_attribute_name = ? ");
        updateStatement.append(", non_auth_access_type_id = ?, specific_user_relative_dn = ?, specific_user_password = ? ");
        updateStatement.append(", is_available_directory_search = ?, directory_search_display_name = ? ");
        updateStatement.append(", search_filter_expression = ? , allows_automatic_registration = ?");

        updateStatement.append("where context_id = ? ");

        try {
            int index = 0;
            db.prepareStatement(updateStatement.toString());
            db.pstmt.setString(++index, getHostnameValues());
            db.pstmt.setString(++index, getSecureHostnameValues());
            db.pstmt.setInt(++index, Conversion.booleanToInt(isUseSSL()));
            db.pstmt.setString(++index, getSearchBaseDN());
            db.pstmt.setString(++index, getSearchTypeID());
            db.pstmt.setString(++index, getSearchSubtrees());
            db.pstmt.setString(++index, getUsernameAttribute());
            db.pstmt.setString(++index, getNonAuthenticatedAccessTypeID());
            db.pstmt.setString(++index, getSpecificUserDN());
            db.pstmt.setString(++index, getSpecificUserPassword());
            db.pstmt.setInt(++index, Conversion.booleanToInt(isAvailableForDirectorySearch()));
            db.pstmt.setString(++index, getDirectorySearchDisplayName());
            db.pstmt.setString(++index, getSearchFilterExpression());
            db.pstmt.setInt(++index, Conversion.booleanToInt(allowsAutomaticRegistration()));

            db.pstmt.setString(++index, getDomainID());

            db.executePrepared();
        
        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("LDAP Directory Configuration store operation failed: " + sqle, sqle);
        
        }
    
    }

    /**
     * Inserts existing configuration settings into the database.
     * Note: No commit or rollback is performed
     * @param db the DBBean in which to perform the transaction
     * @throws PersistenceException if there is a problem inserting
     */
    private void insert(DBBean db) throws PersistenceException {

        StringBuffer insertStatement = new StringBuffer();
        insertStatement.append("insert into pn_ldap_directory_config ");
        insertStatement.append("(context_id, hostname_values, secure_hostname_values, ");
        insertStatement.append("is_use_ssl, search_base_dn, search_type_id, search_subtrees, ");
        insertStatement.append("username_attribute_name, ");
        insertStatement.append("non_auth_access_type_id, specific_user_relative_dn, specific_user_password, ");
        insertStatement.append("is_available_directory_search, directory_search_display_name, ");
        insertStatement.append("search_filter_expression, allows_automatic_registration) ");
        insertStatement.append("values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");

        try {
            
            int index = 0;
            db.prepareStatement(insertStatement.toString());
            db.pstmt.setString(++index, getDomainID());
            db.pstmt.setString(++index, getHostnameValues());
            db.pstmt.setString(++index, getSecureHostnameValues());
            db.pstmt.setInt(++index, Conversion.booleanToInt(isUseSSL()));
            db.pstmt.setString(++index, getSearchBaseDN());
            db.pstmt.setString(++index, getSearchTypeID());
            db.pstmt.setString(++index, getSearchSubtrees());
            db.pstmt.setString(++index, getUsernameAttribute());
            db.pstmt.setString(++index, getNonAuthenticatedAccessTypeID());
            db.pstmt.setString(++index, getSpecificUserDN());
            db.pstmt.setString(++index, getSpecificUserPassword());
            db.pstmt.setInt(++index, Conversion.booleanToInt(isAvailableForDirectorySearch));
            db.pstmt.setString(++index, getDirectorySearchDisplayName());
            db.pstmt.setString(++index, getSearchFilterExpression());
            db.pstmt.setInt(++index, Conversion.booleanToInt(allowsAutomaticRegistration()));
            db.executePrepared();
        
        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("LDAP Directory Configuration store operation failed: " + sqle, sqle);
        
        }
    
    }

    public void remove() throws PersistenceException {
        throw new NotSupportedException("LDAP Directory Configuration remove not supported yet");
    }

    /**
     * Converts the specified values into <code>HostEntry</code> elements
     * and adds to the specified list.
     * The hostname values are of the form <br>
     * <code>hostname[:port]{,hostname[:port]}*</code> <br>
     * For any entries where no <code>port</code> is specified, 
     * the specified <code>defaultPort</code> is used.
     * @param values the host values, comma separated; passing a <code>null</code>
     * values parameter simply causes the hostEntryList to be emptied
     * @param hostEntryList the list to update with <code>HostEntry</code>
     * values, one for each host in the values string
     * @param defaultPort the default port to use in the absence of
     * any other port value
     */
    private void convertHostnames(String values, List hostEntryList, String defaultPort) {

        hostEntryList.clear();

        if (values != null) {
            // We're looking for values separated by commas
            StringTokenizer tok = new StringTokenizer(values, HOSTNAME_SEPARATOR);

            while (tok.hasMoreElements()) {
                String nextToken = (String) tok.nextElement();

                // Add the parsed HostEntry to the list
                hostEntryList.add(new HostEntry(nextToken, defaultPort));

            }
        }

    }

    /**
     * For debug purposes only.
     */
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(super.toString()).append("\n");
        result.append("  hostnameValues: ").append(getHostnameValues()).append("\n");
        result.append("  secureHostnameValues: ").append(getSecureHostnameValues()).append("\n");
        result.append("  isUseSSL: ").append(isUseSSL()).append("\n");
        result.append("  searchBaseDN: ").append(getSearchBaseDN()).append("\n");
        result.append("  searchType: ").append(getSearchType()).append("\n");
        result.append("  searchSubtrees: ").append(getSearchSubtrees()).append("\n");
        result.append("  usernameAttribute: ").append(getUsernameAttribute()).append("\n");
        result.append("  nonAuthenticatedAccessType: ").append(getNonAuthenticatedAccessType()).append("\n");
        result.append("  specificUserDN: ").append(getSpecificUserDN()).append("\n");
        result.append("  specificUserPassword: ").append(getSpecificUserPassword()).append("\n");
        result.append("  isAvailableForDirectorySearch: ").append(isAvailableForDirectorySearch()).append("\n");
        result.append("  directorySearchDisplayName: ").append(getDirectorySearchDisplayName());
        return result.toString();
    }

    //
    // Nested top-level classes
    //

    /**
     * Represents a hostname, port combination.
     */
    static class HostEntry {
        
        private String hostname = null;
        private String port = null;
        private boolean isPortSpecified = false;

        /**
         * Creates a new HostEntry from the specified entry string.
         * The host entry string is of the form <code>hostname[:port]</code>
         * where if the <code>port</code> is not specified, the 
         * <code>defaultPort</code> is used
         * @param hostEntryString the host entry string
         * @param defaultPort the default port to use when none is
         * specified in the hostEntryString
         */
        public HostEntry(String hostEntryString, String defaultPort) {
            
            // Look for a port separator
            int colonPos = hostEntryString.indexOf(HOSTNAME_PORT_SEPARATOR);
            
            if (colonPos >= 0) {
                
                // We found a host and port
                this.hostname = hostEntryString.substring(0, colonPos).trim();
                this.port = hostEntryString.substring(colonPos + 1).trim();
            
            } else {
                
                // The whole string is the host
                this.hostname = hostEntryString.trim();
            
            }

            if ((this.port == null || this.port.length() == 0) && defaultPort != null) {
                // port is null or empty so simply use the default port
                this.port = defaultPort.trim();
                
                // We remember that the port wasn't specified to maintain
                // fidelity when converting back to a string
                isPortSpecified = false;
            
            } else {
                // We remember that the port has been specified
                isPortSpecified = true;
            }

        }

        /**
         * Returns the hostname component.
         * All leading and trailing whitespace is removed.
         * @return the hostname
         */
        public String getHostname() {
            return this.hostname;
        }

        /**
         * Returns the port component.
         * All leading and trailing whitespace is removed.
         * @return the port that was specified or the default
         * port if none was specified
         */
        public String getRealizedPort() {
            return this.port;
        }
    
        /**
         * Returns this HostEntry as a string.
         * This will only include a port part if one was originally
         * specified.  Thus it maintains fidelity with the original
         * value.  Any whitespace will have been trimmed around all
         * components of the host entry. For example, with this
         * constructor: <br>
         * <code>HostEntry(" wombat : 700 ", "389")</code> <br>
         * this will be returned: <br>
         * <code>"wombat:700"</code> <br>
         * With this constructor: <br>
         * <code>HostEntry(" wombat", "389")</code> <br>
         * this will be returned: <br>
         * <code>"wombat"</code>
         * since the default port was used.
         * @return the string form of the HostEntry with close fidelity
         * to the original specification
         */
        public String toString() {
            StringBuffer value = new StringBuffer(getHostname());
            
            if (this.isPortSpecified) {
                value.append(HOSTNAME_PORT_SEPARATOR).append(getRealizedPort());
            }
            
            return value.toString();
        }
    
    }

    /**
     * The DN and passsword required for non-authenticated access
     * when a specific user is required.
     */
    private static class NonAuthenticatedSpecificUser {

        String userDN = null;
        String password = null;

    }

}
