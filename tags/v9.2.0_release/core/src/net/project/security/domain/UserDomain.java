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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.base.directory.Directory;
import net.project.base.directory.DirectoryConfiguration;
import net.project.base.directory.DirectoryException;
import net.project.base.directory.DirectoryProviderType;
import net.project.base.directory.IDirectoryEntry;
import net.project.base.directory.ldap.LDAPDirectoryConfiguration;
import net.project.base.directory.search.IDirectoryContext;
import net.project.base.directory.search.ISearchableDirectory;
import net.project.base.property.PropertyProvider;
import net.project.configuration.ConfigurationSpace;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.database.ObjectManager;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.util.Conversion;


/**
 * A <code>UserDomain</code> provides a security context for registered users.
 * It maintains a collection of users and defines the type of directory
 * that provides services for this domain.  It maintains the configuration
 * parameters for the directory service for this domain.
 * <p>
 * A User's username must be unique within a domain; the authentication
 * of a user is delegated to the dirctory provider associated with this
 * domain.
 * </p>
 *
 * @author Philip Dixon
 * @author Tim Morrow
 * @since Gecko
 */
public class UserDomain implements IJDBCPersistence, IXMLPersistence, java.io.Serializable {

    //
    // Static members
    //

    /** The pn_object_type object type of this object */
    public static final String DOMAIN_OBJECT_TYPE = "user_domain";

    /**
     * Returns a select query for loading a <code>UserDomain</code>
     * from persistent store.
     * @return the select query
     */
    static String getLoadQuery() {
        StringBuffer loadQuery = new StringBuffer();
        loadQuery.append("select ud.domain_id, ud.name, ud.description, ");
        loadQuery.append("ud.directory_provider_type_id, ud.directory_provider_type_name, ud.user_count, ");
        loadQuery.append("ud.registration_instructions_clob, ud.is_verification_required, ");
        loadQuery.append("ud.supports_credit_card_purchases ");
        loadQuery.append("from pn_user_domain_view ud ");
        return loadQuery.toString();
    }

    /**
     * Returns a select query for loading a <code>UserDomain</code>
     * from persistent store for a particular configuration id.
     * @return the select query
     */
    static String getLoadForConfigurationQuery() {
        StringBuffer loadQuery = new StringBuffer();
        loadQuery.append("select ud.domain_id, ud.name, ud.description, ");
        loadQuery.append("ud.directory_provider_type_id, ud.directory_provider_type_name, ud.user_count, ");
        loadQuery.append("ud.registration_instructions_clob, ud.is_verification_required, ");
        loadQuery.append("ud.supports_credit_card_purchases ");
        loadQuery.append("from pn_user_domain_view ud, pn_user_domain_supports_config dc ");
        loadQuery.append("where dc.domain_id = ud.domain_id and dc.configuration_id = ? ");
        return loadQuery.toString();
    }



    /**
     * Populates the specified UserDomain from the specified
     * ResultSet.
     * @param result the ResultSet from which to read the attributes;
     * assumes the ResultSet is on a current row
     * domain the UserDomain to populate from the ResultSet row
     * @throws java.sql.SQLException if there is a problem reading an
     * attribute from the ResultSet row
     * @throws PersistenceException if there is a problem reading the registration
     * instructions
     */
    static void populate(java.sql.ResultSet result, UserDomain domain)
            throws java.sql.SQLException, PersistenceException {

        domain.setID(result.getString("domain_id"));
        domain.setName(result.getString("name"));
        domain.setDescription(result.getString("description"));
        domain.setRegistrationInstructions(ClobHelper.read(result.getClob("registration_instructions_clob")));
        domain.setVerificationRequired(Conversion.toBoolean(result.getInt("is_verification_required")));
        domain.setDirectoryProviderTypeID(result.getString ("directory_provider_type_id"));
        domain.setDirectoryProviderTypeName(result.getString ("directory_provider_type_name"));
        domain.setUserCount(result.getString ("user_count"));
        domain.setSupportsCreditCardPurchases(result.getBoolean("supports_credit_card_purchases"));
    }


    /**
     * Creates an instantiated <code>UserDomain</code> object for the
     * specified user.
     * @param user the user for which you wish to return an instantiated
     * domain
     * @return an instantiated (and loaded) userDomain object if the
     * user is associated with a domain.
     * @throws DomainException if the specified user is null or there
     * is a problem loading the domain for the
     * specified user.
     * @since Gecko 3
     */
    public static UserDomain getInstance (User user) throws DomainException {

        DBBean db = new DBBean();
        UserDomain domain = null;
        String qstrGetDomainID = "select domain_id from pn_user where user_id = ?";

        if (user == null) {
            throw new DomainException ("UserDomain.getInstance(): Can not instantiate a domain for a NULL user.");
        }

        try {

            int queryIndex = 0;

            db.prepareStatement (qstrGetDomainID);
            db.pstmt.setString (++queryIndex, user.getID());
            db.executePrepared();

            if (db.result.next()) {

                domain = new UserDomain (db.result.getString ("domain_id"));
                domain.load();
            } else {
                throw new DomainException ("UserDomain.getInstance(): No domain was found for user: " + user.getID());
            }

        } catch (SQLException sqle) {
            throw new DomainException ("UserDomain.getInstance() threw an SQL Exception: " + sqle, sqle);

        } catch (PersistenceException pe) {
            throw new DomainException ("UserDomain.getInstance() threw a PersistenceException: " + pe, pe);

        } finally {
            db.release();
        }

        return domain;
    }


    //
    // Instance members
    //

    /** The ID of the instantiated domain */
    private String domainID = null;

    /** The human readable name of the domain. */
    private String name = null;

    /** The description (long form) of the domain */
    private String description = null;

    /** The registration instructions displayed for this domain. */
    private String registrationInstructions = null;

    /** The ID of the directory provider type specified for this domain. */
    private String directoryProviderTypeID = null;

    /**
     * The dereferenced directory provider type name.
     * This is for performance reasons only; it avoids the need to load
     * the directoryProviderType.
     */
    private String directoryProviderTypeName = null;

    /** The loaded DirectoryProviderType for the current directoryProviderTypeID. */
    private DirectoryProviderType directoryProviderType = null;

    /**
     * Specifies whether verification is required after registering against
     * this user domain.
     */
    private boolean isVerificationRequired = true;

    /**
     * Specifies whether this domain allows users to purchase licenses via
     * credit card.
     */
    private boolean supportsCreditCardPurchases = true;

    /** The loaded DirectoryConfiguration. */
    private DirectoryConfiguration directoryConfiguration = null;

    /** The number of users currently "owned" by this domain (calulated at load) */
    private String userCount = null;

    /** A collection of users "owned" by this domain */
    private DomainUserCollection userCollection = new DomainUserCollection();

    /** A collection of configuration (id's) supported by this domain */
    private DomainConfigurationCollection configurationCollection = new DomainConfigurationCollection();

    /** isLoaded flag */
    private boolean isLoaded = false;
    
    private ArrayList newlyAddedConfigCollection = new ArrayList();


    /**
     * Creates an empty UserDomain.
     */
    public UserDomain() {
        // do nothing for now
    }

    /**
     * Returns the properties of this domain.
     * For information only; no structure is defined, no guarantee
     * made to support in future releases.
     * @return the string representation of this domain
     */
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(super.toString()).append("\n");
        result.append("id: ").append(getID()).append("\n");
        result.append("name: ").append(getName()).append("\n");
        result.append("description: ").append(getDescription()).append("\n");
        result.append("registrationInstructions: ").append(getRegistrationInstructions()).append("\n");
        result.append("directoryProviderTypeID: ").append(getDirectoryProviderTypeID()).append("\n");
        result.append("userCount: ").append(getUserCount()).append("\n");
        result.append("isLoaded: ").append(isLoaded()).append("\n");
        return result.toString();
    }

    /**
     * Creates a UserDomain for the specified domainID.
     * The domain is NOT loaded by this methods.
     * @param domainID the id of this user domain
     */
    public UserDomain (String domainID) {

        setID (domainID);
    }

    /**
     * Sets the ID of this domain.
     * @param domainID The ID of this domain
     */
    public void setID (String domainID) {
        this.domainID = domainID;
    }


    /**
     * Returns the ID of this UserDomain.
     * @return Domain ID
     */
    public String getID() {
        return this.domainID;
    }


    /**
     * Sets the name of this domain.
     * @param name the name
     */
    public void setName (String name) {
        this.name = name;
    }


    /**
     * Returns the name of this domain.
     * @return the name
     */
    public String getName() {
        return this.name;
    }


    /**
     * Sets the description of this domain.
     * @param description the description
     */
    public void setDescription (String description) {
        this.description = description;
    }


    /**
     * Returns the description of this domain.
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }


    /**
     * Sets the registration instructions that are displayed when
     * a user is registering and this domain is presented as a selection.
     * These support the inclusion of tokens that are replaced at runtime
     * @param registrationInstructions the registration instruction text
     * @see #getRegistrationInstructions
     */
    public void setRegistrationInstructions(String registrationInstructions) {
        this.registrationInstructions = registrationInstructions;
    }

    /**
     * Returns the registration instructions that should be displayed when
     * a user is registering and this domain is presented as a selection.
     * @return the registration instruction text, or null if none
     * were specified when this domain was created.
     * @see #setRegistrationInstructions
     * @see #getParsedRegistrationInstructions
     */
    public String getRegistrationInstructions() {
        return this.registrationInstructions;
    }

    /**
     * Returns the registration instructions with any token values replaced.
     * @return the parsed registration instructions
     */
    public String getParsedRegistrationInstructions() {
        return net.project.base.property.PropertyProvider.parse(getRegistrationInstructions());
    }

    /**
     * Sets the id of the DirectoryProviderType that determines
     * what kind of directory is authenticated with by this domain.
     * Clears out the loaded DirectoryProviderType if it does not
     * match the specified id.
     * @param directoryProviderTypeID the id of the directory provider type
     * @see net.project.base.directory.DirectoryProviderType#getID
     * @see #getDirectoryProviderTypeID
     */
    public void setDirectoryProviderTypeID(String directoryProviderTypeID) {
        if (this.directoryProviderTypeID != null &&
                !(this.directoryProviderTypeID.equals(directoryProviderTypeID)) ) {

            setDirectoryProviderType(null);
        }
        this.directoryProviderTypeID = directoryProviderTypeID;
    }


    /**
     * Returns the id of the DirectoryProviderType providing directory
     * services for this domain.
     * @return the id of the directory provider type
     * @see #setDirectoryProviderTypeID
     */
    public String getDirectoryProviderTypeID() {
        return this.directoryProviderTypeID;
    }

    /**
     * Sets the name of the DirectoryProviderType.
     * Used to avoid loading the DirectoryProviderType.
     * @param directoryProviderTypeName the name
     * @see #getDirectoryProviderTypeName
     */
    private void setDirectoryProviderTypeName(String directoryProviderTypeName) {
        this.directoryProviderTypeName = directoryProviderTypeName;
    }

    /**
     * Returns the name of the DirectoryProviderType providing directory
     * services for this domain.
     * @return the name
     * @see #setDirectoryProviderTypeName
     */
    public String getDirectoryProviderTypeName() {
        return PropertyProvider.get(this.directoryProviderTypeName);
    }

    /**
     * Sets the DirectoryProviderType for this domain.
     * @param providerType the provider type
     */
    public void setDirectoryProviderType(DirectoryProviderType providerType) {
        this.directoryProviderType = providerType;
    }

    /**
     * Returns the DirectoryProviderType for this domain.
     * It is loaded from persistent store if not already loaded.
     * @return the provider type
     * @throws PersistenceException if the provider type is not yet
     * loaded and there is a problem loading
     */
    public DirectoryProviderType getDirectoryProviderType() throws PersistenceException {
        if (this.directoryProviderType == null) {
            loadDirectoryProviderType();
        }
        return this.directoryProviderType;
    }

    /**
     * Specifies whether verification is required after registering against
     * this domain.
     * @param isVerificationRequired true means a verification email will
     * be sent and a user must complete verification before being to log in,
     * after registering against this domain; false means verification will
     * not be required.
     * @see #isVerificationRequired
     */
    public void setVerificationRequired(boolean isVerificationRequired) {
        this.isVerificationRequired = isVerificationRequired;
    }

    /**
     * Indicates whether verificationis required after registering against
     * this domain.
     * @return true if verification is required; false otherwise
     * @see #setVerificationRequired
     */
    public boolean isVerificationRequired() {
        return this.isVerificationRequired;
    }

    /**
     * Sets the DirectoryConfiguration for this domain.
     * The directory configuration is dependent on the directory
     * provider type for this domain.
     * @param configuration the directory configuration
     * @see #getDirectoryConfiguration
     */
    public void setDirectoryConfiguration(DirectoryConfiguration configuration) {
        this.directoryConfiguration = configuration;
    }

    /**
     * Returns the DirectoryConfiguration for this domain.
     * It is loaded from persistent store if not already loaded.
     * @return the directory configuration
     * @throws PersistenceException if the configuration is not yet
     * loaded and there is a problem loading
     * @throws DirectoryException if the configuration is not yet
     * loaded and there is a problem creating the DirectoryConfiguration
     * @see #setDirectoryConfiguration
     */
    public DirectoryConfiguration getDirectoryConfiguration()
            throws DirectoryException, PersistenceException {

        if (this.directoryConfiguration == null) {
            loadDirectoryConfiguration();
        }
        return this.directoryConfiguration;
    }

    /**
     * Sets the number of users currently supported by this domain.
     *
     * @param count the user count
     */
    void setUserCount (String count) {
        this.userCount = count;
    }


    /**
     * Returns the number of users currently supported by this domain.
     * <br>
     * <b>Precondition:</b>
     * the domain properties have been loaded by {@link #load}<br>
     * @return the number of users
     */
    public String getUserCount() {
        return this.userCount;
    }


    /**
     * Sets the supported configuraitons for this domain from a String array.
     * This will likely be invoked by a JSP page.
     * @param supportedConfigurations an array of configuration ids supported by this domain
     */
    public void setSupportedConfigurations (String[] supportedConfigurations) {

        this.configurationCollection.setSupportedConfigurations (supportedConfigurations);
    }


    /**
     * Adds the supported configuraitons for this domain
     * This will likely be invoked by a JSP page.
     * @param supportedConfigurations the configuration ids to be supported by this domain
     */
    public void addSupportedConfigurations (String supportedConfigurations) {

        this.configurationCollection.addSupportedConfigurations (supportedConfigurations);
        this.newlyAddedConfigCollection.add(supportedConfigurations);

    }

    /**
     * Indicates whether this domain is searchable during invitation.
     * @return true if this domain is searchable during invitation;
     * false if not
     */
    private boolean isSearchableForInvitation() {
        // TODO We should store a property value in the database that
        // TODO indicates whether the domain creator allows searching
        // TODO of the domain for invitiation purposes
        return false;
    }

    /**
     * Indicates whether this domain provides any searchable directories.
     * Initially used during invitation process.
     * @return true if this domain, or its configured directory provider
     * type provides a searchable directory; false if neither provides
     * a searchable directory
     * @throws DirectoryException if there is a problem getting the
     * current directory
     * @throws PersistenceException if there is a problem getting
     * the directory
     */
    public boolean providesSearchableDirectory()
            throws DirectoryException, PersistenceException {

        Directory directory = Directory.getInstance(getDirectoryProviderType(), getDirectoryConfiguration());
        return (isSearchableForInvitation() || directory.isSearchableForInvitation());
    }

    /**
     * Returns the searchable directories.
     * @return the searchable directories; the collection will be empty
     * if neither this domain nor the configured directory provider
     * are searchable.
     * @throws DirectoryException if there is a problem getting the
     * current directory
     * @throws PersistenceException if there is a problem getting
     * the directory
     * @see #providesSearchableDirectory
     */
    public List getSearchableDirectories()
            throws DirectoryException, PersistenceException {

        java.util.List searchableDirectories = new ArrayList();

        if (isSearchableForInvitation()) {
            searchableDirectories.add(getSearchableDirectory());
        }

        // Add directory provider directory if it provides ones
        Directory directory = Directory.getInstance(getDirectoryProviderType(), getDirectoryConfiguration());
        if (directory.isSearchableForInvitation()) {
            searchableDirectories.add(directory.getSearchableDirectory());
        }

        return searchableDirectories;
    }

    /**
     * Returns the SearchableDirectory for this domain.
     * @return the SearchableDirectory
     */
    private ISearchableDirectory getSearchableDirectory() {
        return new DomainSearchableDirectory();
    }

    /**
     * Adds the specified user to the domain.
     * Also invokes the directory provider associated with this domain.
     * @param user the user to be added to the domain; assumes the
     * user is loaded
     * @param directoryEntry the directory-specific entry information
     * about the user
     * @throws DomainException if there is a problem adding the user to
     * the domain or if there is a problem adding the user to the
     * directory
     */
    public void addUser(User user, IDirectoryEntry directoryEntry) throws DomainException {

        DBBean db = new DBBean();

        try {
            addUser(user ,directoryEntry ,db);

        } finally {
            db.release();
        }

    }

    /**
     * Adds the specified user to the domain.
     * Also invokes the directory provider associated with this domain.
     * @param user the user to be added to the domain; assumes the
     * user is loaded
     * @param directoryEntry the directory-specific entry information
     * about the user
     * @param dbean the database bean
     * @throws DomainException if there is a problem adding the user to
     * the domain or if there is a problem adding the user to the
     * directory
     */
    public void addUser(User user, IDirectoryEntry directoryEntry , DBBean dbean) throws DomainException {

        try {

            // First, make sure the domain is loaded
            if (!isLoaded()) {
                load(dbean);
            }

            // then add the user to the domain
            this.userCollection.addUser(user , dbean);

            // Create the Directory from the current domain's
            // directory provider type and configuration
            Directory directory = Directory.getInstance(getDirectoryProviderType(), getDirectoryConfiguration());

            // Update the user in the directory
            // Some directory providers might store the user's username and password (e.g. Native)
            // Others may ignore this information entireyly (e.g. LDAP)
            // That information is encapsulated in the directoryEntry
            directory.updateUser(user, directoryEntry , dbean);


        } catch (PersistenceException pe) {
            throw new DomainException("Domain add user operation failed: " + pe, pe);

        } catch (net.project.base.directory.DirectoryException e) {
            throw new DomainException("Domain add user operation failed: " + e, e);

        }
    }

    /**
     * Updates the specified user in this domain.
     * Also invokes the directory provider associated with this domain.
     * @param user the user to update in the domain; assumes the
     * user is loaded
     * @param directoryEntry the directory-specific entry information
     * about the user
     * @throws DomainException if there is a problem updating the user in
     * the domain or if there is a problem updating the entry in the
     * directory
     */
    public void updateUser(User user, IDirectoryEntry directoryEntry) throws DomainException {
        DBBean db = new DBBean();

        try {
            updateUser(user , directoryEntry , db);

        } finally {
            db.release();
        }

    }

    /**
     * Updates the specified user in this domain.
     * Also invokes the directory provider associated with this domain.
     * @param user the user to update in the domain; assumes the
     * user is loaded
     * @param directoryEntry the directory-specific entry information
     * about the user
     * @param db the database bean
     * @throws DomainException if there is a problem updating the user in
     * the domain or if there is a problem updating the entry in the
     * directory
     */
    public void updateUser(User user, IDirectoryEntry directoryEntry , DBBean db) throws DomainException {

        try {

            // First, make sure the domain is loaded
            if (!isLoaded()) {
                load(db);
            }

            // Create the Directory from the current domain's
            // directory provider type and configuration
            Directory directory = Directory.getInstance(getDirectoryProviderType(), getDirectoryConfiguration());

            // Update the user in the directory
            // Some directory providers might store the user's username and password (e.g. Native)
            // Others may ignore this information entireyly (e.g. LDAP)
            // That information is encapsulated in the directoryEntry
            directory.updateUser(user , directoryEntry , db);


        } catch (PersistenceException pe) {
            throw new DomainException("Domain add user operation failed: " + pe, pe);

        } catch (net.project.base.directory.DirectoryException e) {
            throw new DomainException("Domain add user operation failed: " + e, e);

        }
    }

    /**
     * Removes the specified user from the domain.
     * Also invokes the directory provider associated with this
     * user/domain to remove and directory-specific entries for the
     * specified user's record.
     * @param user the user to be removed from the domain; assumes
     * the user is loaded.
     * @throws DomainException if there is a problem removing the user
     * from the domain.
     * @since Gecko 3
     */
    public void removeUser (User user) throws DomainException {

        DBBean db = new DBBean();

        try {
            removeUser (user, db);

        } finally {
            db.release();
        }
    }

    /**
     * Removes the specified user from the domain.
     * Also invokes the directory provider associated with this user/domain to remove
     * and directory-specific entries for the specified users record.
     * NOTE: this method does NOT commit, rollback or release the DBBean.
     * @param user the user to be removed from the domain; assumes
     * the user is loaded.
     * @param db the DBBean in which to perform the transaction
     * @throws DomainException if there is a problem removing the user
     * from the domain.
     * @since Gecko 3
     */
    public void removeUser (User user, DBBean db) throws DomainException {

        try {

            // Next must delegate to the appropriate directory provider to cleanup any other
            // directory-specific information.
            Directory directory = Directory.getInstance(getDirectoryProviderType(), getDirectoryConfiguration());

            directory.removeUser(user , db);

        } catch (net.project.base.directory.DirectoryException de) {
            throw new DomainException("UserDomain.removeUser() threw a DirectoryException when removing the user: " + de, de);

        } catch (PersistenceException pe) {
            throw new DomainException("UserDomain.removeUser() threw a PersistenceException when removing the user: " + pe, pe);
        }
    }


    /**
     * Indicates whether this domain has a user with the specified
     * username.
     * <br>
     * <b>Preconditions:</b><br>
     * this domain is loaded<br>
     * @param username the username to check
     * @return true if there is a user with that username associated
     * with this domain; false if there is no user with that username
     * @throws DomainException if there was a problem looking-up
     * the users for this domain
     */
    public boolean hasUsername(String username)
            throws DomainException {

        boolean hasUsername = false;

        if (getUser(username) != null) {
            hasUsername = true;
        }

        return hasUsername;
    }

    /**
     * Returns the user belonging to this domain with the specified username.
     * @param username the username of the User to get
     * @return the User with the specified username or null if a
     * user with that username is not found
     * @throws DomainException if the user collection was not loaded
     * and could not be fetched
     */
    public User getUser(String username)
            throws DomainException {

        User foundUser = null;

        try {
            // Make sure that the user collection is loaded
            ensureLoadedUsers();

        } catch (PersistenceException pe) {
            // There was a problem loading
            // We must throw an exception to avoid "false postives"
            // when checking for the presence of users
            throw new DomainException("Get user operation failed: " + pe, pe);
        }

        // Iterate over all users belonging to this domain until
        // we find the one with matching username
        for (Iterator it = this.userCollection.values().iterator(); it.hasNext() && foundUser == null; ) {
            User nextUser = (User) it.next();

            // Critical:  Ignore case since usernames are case
            // insensitive
            if (nextUser.getUserName().equalsIgnoreCase(username)) {
                foundUser = nextUser;
            }
        }

        return foundUser;
    }

    /**
     * Ensures that the users for this domain are loaded.
     * Does not re-load if there are loaded users.
     */
    public void ensureLoadedUsers()
            throws PersistenceException {

        if (this.userCollection == null ||
                !this.userCollection.isLoaded()) {

            loadUsers();
        }

    }

    /* ------------------------------- Persistence Methods  ------------------------------- */

    /**
     * Loads the properties for this domain and the users contained by it.
     *
     * @throws PersistenceException if the load operation (of either properties
     * or user load) is unsuccessful.
     * @see net.project.security.domain.UserDomain#create
     */
    public void load() throws PersistenceException {

        DBBean db = new DBBean();

        try {
            load(db);
        } finally {
            db.release();
        }
    }

    /**
     * Loads the properties for this domain and the users contained by it.
     *
     * @param db     The Database bean
     * @exception PersistenceException
     *                   Throws a <code>PersistenceException</code> if the load operation (of either properties or user load) is unsuccessful.
     * @see net.project.security.domain.UserDomain#create
     */
    public void load(DBBean db) throws PersistenceException {


        // first load the properties of the domain
        loadProperties(db);

        // then load the domain users (id lists only (at first)
        loadUsers(db);

        // then load the supported configurations
        loadSupportedConfigurations(db);
    }



    /**
     * Store the properties for this domain object
     *
     * @throws PersistenceException
     */
    public void store() throws PersistenceException {

        boolean executeCreateOperation = ( this.domainID == null || "null".equals(this.domainID) ) ? true : false;

        if (executeCreateOperation)
            create();
        else
            update();
    }



    /**
     * (Soft) delete this domain object.
     *
     * @throws PersistenceException
     */
    public void remove() throws PersistenceException {
        throw new PersistenceException ("UserDomain.remove(): FEATURE NOT YET IMPLEMENTED");
    }


    /**
     * Clears all properties from this bean.
     * Returned to the same state as an empty instantiation.
     */
    public void clear() {
        setID(null);
        setName(null);
        setDescription(null);
        setRegistrationInstructions(null);
        setVerificationRequired(false);
        setSupportsCreditCardPurchases(true);
        setDirectoryProviderTypeID(null);
        setDirectoryProviderTypeName(null);
        setDirectoryProviderType(null);
        setDirectoryConfiguration(null);
        setUserCount(null);
        setSupportedConfigurations(new String[]{});
        setLoaded(false);
    }

    /**
     * Load the properties for the domain specified by the domainID member
     *
     * @throws PersistenceException
     *                   Throws a <code>PersistenceException</code> if the database load operation fails.
     */
    protected void loadProperties() throws PersistenceException {

        DBBean db = new DBBean ();

        try {
            loadProperties(db);
        } finally {
            db.release();
        }
    }

    /**
     * Load the properties for the domain specified by the domainID member
     *
     * @param db     The database bean
     * @exception PersistenceException
     *                   Throws a <code>PersistenceException</code> if the database load operation fails.
     */
    protected void loadProperties(DBBean db) throws PersistenceException {

        if (this.domainID == null) {
            throw new PersistenceException("UserDomain.loadProperties() can not proceed because the domainID is null");
        }

        StringBuffer loadQuery = new StringBuffer(getLoadQuery());
        loadQuery.append("where domain_id = ? ");

        try {
            int index = 0;
            db.prepareStatement(loadQuery.toString());
            db.pstmt.setString (++index, getID());
            db.executePrepared();

            if (db.result.next()) {                                                               
                populate(db.result, this);
            }

            this.isLoaded = true;

        } catch (SQLException sqle) {
            this.isLoaded = false;
            throw new PersistenceException ("Domain load operation failed: " + sqle, sqle);

        }

    }

    /**
     * Load the users for this domain into a <code>DomainUserCollection</code> class member.
     *
     * @throws PersistenceException
     *                   Throws a <code>PersistenceException of the userCollection load operation is unsuccessful
     * @see net.project.security.domain.DomainUserCollection
     */
    public void loadUsers() throws PersistenceException {
        DBBean db = new DBBean();

        try {
            loadUsers(db);
        } finally {
            db.release();
        }
    }

    /**
     * Load the users for this domain into a <code>DomainUserCollection</code> class member.
     *
     * @throws PersistenceException
     *                   Throws a <code>PersistenceException of the userCollection load operation is unsuccessful
     * @see net.project.security.domain.DomainUserCollection
     */
    public void loadUsers(DBBean db) throws PersistenceException {

        if ( this.domainID == null ) {
            throw new PersistenceException ("UserDomain.loadUsers() can not proceed because the domainID is null");
        }

        if ( this.userCollection == null ) {
            this.userCollection = new DomainUserCollection ();
        }

        this.userCollection.setDomainID (getID());
        this.userCollection.load(db);
    }



    /**
     * Load the supported configurations for this domain into a <code>DomainConfigurationCollection</code> class member.
     *
     * @throws PersistenceException
     *                   Throws a <code>PersistenceException of the configurationCollection load operation is unsuccessful
     * @see net.project.security.domain.DomainConfigurationCollection
     */
    public void loadSupportedConfigurations() throws PersistenceException {
        DBBean db = new DBBean () ;

        try {
            loadSupportedConfigurations(db);
        } finally {
            db.release();
        }
    }


    /**
     * Load the supported configurations for this domain into a <code>DomainConfigurationCollection</code> class member.
     *
     * @throws PersistenceException
     *                   Throws a <code>PersistenceException of the configurationCollection load operation is unsuccessful
     * @see net.project.security.domain.DomainConfigurationCollection
     */
    public void loadSupportedConfigurations(DBBean db) throws PersistenceException {

        if ( this.domainID == null ) {
            throw new PersistenceException ("UserDomain.loadSupportedConfigurations() can not proceed because the domainID is null");
        }

        if ( this.configurationCollection == null ) {
            this.configurationCollection = new DomainConfigurationCollection ();
        }

        this.configurationCollection.clear();
        this.newlyAddedConfigCollection.clear();
        this.configurationCollection.setDomainID (getID());
        this.configurationCollection.loadActiveOnly(db);
    }


    /**
     * Loads the DirectoryProviderType for this domain based on the
     * directoryProviderTypeID.
     * <br>
     * <b>Preconditions</b><br>
     * the id has been set by <code>{@link #setDirectoryProviderTypeID}</code><br>
     * <br>
     * <b>Postconditions</b><br>
     * the loaded provider type is available by
     * <code>{@link #getDirectoryProviderType}</code>
     * @throws PersistenceException if there is a problem loading; the
     * directory provider type is not replaced
     */
    private void loadDirectoryProviderType() throws PersistenceException {
        if (getDirectoryProviderTypeID() == null) {
            throw new PersistenceException("UserDomain.loadDirectoryproviderType() cannot processed because the directoryProviderTypeID is null");
        }

        DirectoryProviderType type = new DirectoryProviderType();
        type.setID(getDirectoryProviderTypeID());
        type.load();
        setDirectoryProviderType(type);
    }


    /**
     * Loads the DirectoryConfiguration for this domain.
     * <br>
     * <b>Postconditions</b><br>
     * the configuration is available by <code>{@link #getDirectoryConfiguration}</code>
     * @throws DirectoryException if there is a problem determining or
     * instantiating the appropriate configuration class based on the
     * directory provider type
     * @throws PersistenceException if there is a problem loading; the
     * directory configuration is not replaced
     * @see net.project.base.directory.DirectoryProviderType#newConfiguration
     */
    private void loadDirectoryConfiguration() throws DirectoryException, PersistenceException {
        DirectoryConfiguration configuration = getDirectoryProviderType().newConfiguration();
        configuration.setDomainID(getID());
        configuration.load();
        setDirectoryConfiguration(configuration);
    }

    /**
     * Identifies whether this domain supports automatic registration.
     * Note, at this time, only LDAP domains supoprt auto registration.
     * @return true if the domain supports automatic registration; false if not.
     */
    public boolean allowsAutomaticRegistration() {

        boolean allowsAutoRegistration = false;

        try {
            if (getDirectoryProviderType().getID().equals(DirectoryProviderType.LDAP_ID)) {
                // if this is an LDAP domain, query the domain configuration to determine
                // if the domain supports auto registration

                LDAPDirectoryConfiguration configuration = (LDAPDirectoryConfiguration) getDirectoryConfiguration();
                allowsAutoRegistration = configuration.allowsAutomaticRegistration();

            } else if (isCosignLoginEnabled()) {
            	// if cosign login enabled
            	allowsAutoRegistration = true;
            } else {
                // if this isn't an LDAP Domain, then we don't currently support auto registration
                allowsAutoRegistration = false;
            }
        } catch (DirectoryException de) {
            // if there is an exception for any reason, assume no auto registration
            allowsAutoRegistration = false;
        } catch (PersistenceException pe) {
            // if there is an exception for any reason, assume no auto registration
            allowsAutoRegistration = false;
        }

        return allowsAutoRegistration;
    }

    /**
     * Creates a new user domain in the databse and stores supported
     * configurations.
     * @throws PersistenceException if the database store operation fails
     */
    protected void create() throws PersistenceException {

        DBBean db = new DBBean();

        try {

            // first turn off autocommit in order to make this operation automic
            db.setAutoCommit(false);

            // then create the domain object itself
            createDomain(db);

            // then store the domain's supported configurations
            storeSupportedConfigurations(db);

            // if the operation was successful, commit
            db.commit();

        } catch (SQLException sqle) {

            // if an exception occurs, rollback
            try {
                db.rollback();
            } catch (SQLException sqle2) {
                // Simply release
            }

            this.domainID = null;
            throw new PersistenceException ("UserDomain.create() Create Domain operation failed due to a database error: " + sqle, sqle);

        } finally {
            db.release();

        }

    }


    /**
     * Updates the user domain in the databse and stores supported configurations.
     * @throws PersistenceException if the database operation fails
     */
    protected void update() throws PersistenceException {

        DBBean db = new DBBean();

        try {

            // first turn off autocommit in order to make this operation automic
            db.setAutoCommit(false);

            // then create the domain object itself
            updateDomainProperties(db);

            // then store the domain's supported configurations
            storeSupportedConfigurations(db);

            // if the operation was successful, commit
            db.commit();

        } catch (SQLException sqle) {

            // if an exception occurs, rollback
            try {
                db.rollback();
            } catch (SQLException sqle2) {
                // Simply release
            }

            throw new PersistenceException ("UserDomain.create() Create Domain operation failed due to a database error: ", sqle);

        } finally {
            db.release();

        }

    }



    /**
     * Store the supported configurations for this domain.
     * <br>
     * This is implemented by deleting existing domain/configuration
     * mappings and inserting all new ones.
     * In a multi-instance enviornment this may cause contention,
     * but due to the central and administrative nature of the
     * functionality, it shouldn't be a problem.
     * @param db an instantiated database access bean.
     * @throws SQLException if there is a problem storing
     */
    private void storeSupportedConfigurations (DBBean db) throws SQLException {

        if (this.configurationCollection != null) {

            this.configurationCollection.setDomainID (this.domainID);
            this.configurationCollection.store (db);
        }
    }


    /**
     * Update the userDomain instance in the database.
     * Note: Does NOT commit or rollback
     * @param db an instantiated database access bean
     * @throws SQLException if the database operation fails.
     * @throws PersistenceException if there is a problem updating the
     * registration instructions
     */
    private void updateDomainProperties(DBBean db) throws SQLException, PersistenceException {

        // First update all properties and clear out the registration instructions
        StringBuffer updateQuery = new StringBuffer();
        updateQuery.append("update pn_user_domain set ");
        updateQuery.append("name = ?, description = ?, directory_provider_type_id = ?, ");
        updateQuery.append("registration_instructions_clob = empty_clob(), is_verification_required = ?, ");
        updateQuery.append("supports_credit_card_purchases = ? ");
        updateQuery.append("where domain_id = ?");

        int index = 0;
        db.prepareStatement(updateQuery.toString());
        db.pstmt.setString(++index, this.name);
        db.pstmt.setString(++index, this.description);
        db.pstmt.setString(++index, this.directoryProviderTypeID);
        db.pstmt.setInt(++index, Conversion.booleanToInt(isVerificationRequired()));
        db.pstmt.setBoolean(++index, this.supportsCreditCardPurchases);
        db.pstmt.setString(++index, this.domainID);
        db.executePrepared();

        // Now reselect the updated row in order to stream registration instructions
        // to the clob
        StringBuffer selectQuery = new StringBuffer();
        selectQuery.append("select registration_instructions_clob from pn_user_domain ");
        selectQuery.append("where domain_id = ? for update nowait ");

        index = 0;
        db.prepareStatement(selectQuery.toString());
        db.pstmt.setString(++index, this.domainID);
        db.executePrepared();

        if (db.result.next()) {
            ClobHelper.write(db.result.getClob("registration_instructions_clob"), getRegistrationInstructions());

        } else {
            throw new PersistenceException("Unable to update registration " +
                    "instructions for domain ID '" + this.domainID + "' Updated row " +
                    "not found.");
        }

    }


    /**
     * Create the userDomain instance in the database.
     * No commit or rollback is performed.
     * @param db An instantiated database access bean
     * @throws SQLException if the database operation fails
     * @throws PersistenceException if there is a problem writing the registration
     * instructions
     */
    private void createDomain(DBBean db) throws SQLException, PersistenceException {

        String activeRecordStatus = "A";

        // First insert the domain with empty instructions
        StringBuffer insertQuery = new StringBuffer();
        insertQuery.append("insert into pn_user_domain ");
        insertQuery.append("(domain_id, name, description, directory_provider_type_id, record_status, ");
        insertQuery.append("registration_instructions_clob, is_verification_required, supports_credit_card_purchases ) ");
        insertQuery.append("values (?, ?, ?, ?, ?, empty_clob(), ?, ?) ");

        this.domainID = new ObjectManager().dbCreateObject (DOMAIN_OBJECT_TYPE, activeRecordStatus);

        int index = 0;
        db.prepareStatement (insertQuery.toString());
        db.pstmt.setString(++index, this.domainID);
        db.pstmt.setString(++index, this.name);
        db.pstmt.setString(++index, this.description);
        db.pstmt.setString(++index, this.directoryProviderTypeID);
        db.pstmt.setString(++index, activeRecordStatus);
        db.pstmt.setInt(++index, Conversion.booleanToInt(isVerificationRequired()));
        db.pstmt.setBoolean(++index, supportsCreditCardPurchases);
        db.executePrepared();

        // Now reselect the inserted row in order to stream registration instructions
        // to the clob
        StringBuffer selectQuery = new StringBuffer();
        selectQuery.append("select registration_instructions_clob from pn_user_domain ");
        selectQuery.append("where domain_id = ? for update nowait ");

        index = 0;
        db.prepareStatement(selectQuery.toString());
        db.pstmt.setString(++index, this.domainID);
        db.executePrepared();

        if (db.result.next()) {
            ClobHelper.write(db.result.getClob("registration_instructions_clob"), getRegistrationInstructions());

        } else {
            throw new PersistenceException("Unable to insert registration " +
                    "instructions for domain ID '" + this.domainID + "' Inserted row not found.");
        }

    }


    /**
     * Indicates whether this UserDomain has been successfully loaded.
     * Set during a successful call to {@link #load}.
     * @return true this UserDomain has been loaded; false otherwise
     */
    protected boolean isLoaded() {
        return this.isLoaded;
    }

    /**
     * Specifies whether this UserDomain has been successfully loaded.
     * @param isLoaded true if this UserDomain has been loaded; false
     * otherwise
     */
    protected void setLoaded (boolean isLoaded) {
        this.isLoaded = isLoaded;
    }



    /**
     * Returns the properties of this UserDomain in XML format.
     * @return the XML properties
     */
    public String getXML() {
        return getXMLDocument().getXMLString();
    }


    /**
     * Returns the properties of this UserDomain in XML format -- without the version string
     * @return the XML properties
     */
    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    /**
     * Returns the XMLDocument representing this UserDomain.
     * @return the XMLDocument
     */
    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        try {
            doc.startElement("UserDomain");
            doc.addElement("id", getID());
            doc.addElement("name", getName());
            doc.addElement("description", getDescription());
            doc.addElement("registrationInstructions", getRegistrationInstructions());
            doc.addElement("parsedRegistrationInstructions", getParsedRegistrationInstructions());
            doc.addElement("isVerificationRequired", Boolean.valueOf(isVerificationRequired()));
            doc.addElement("supportsCreditCards", Boolean.valueOf(supportsCreditCardPurchases()));
            doc.addElement("userCount", getUserCount());
            doc.addElement("directoryProviderTypeID", getDirectoryProviderTypeID());
            doc.addElement("directoryProviderTypeName", getDirectoryProviderTypeName());

            // finally get the configurationCollection's xml
            doc.addElement(getXMLDocumentConfigurationCollection());
            doc.endElement();

        } catch (net.project.xml.document.XMLDocumentException e) {
            // Empty document

        }

        return doc;
    }


    private net.project.xml.document.XMLDocument getXMLDocumentConfigurationCollection() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();

        try {
            if (this.configurationCollection != null && !this.configurationCollection.isLoaded()) {
                loadSupportedConfigurations();
            }

            doc.addElement(this.configurationCollection.getXMLDocument());

        } catch (PersistenceException pe) {
            // Empty document

        } catch (net.project.xml.document.XMLDocumentException e) {
            // Empty document

        }

        return doc;
    }


    /**
     * Returns the a collection of configurations supported by this UserDomain.
     * @return the configurations; the element type depends on how
     * the configuration collection was built: <br>
     * <li>When populated with <code>{@link #setSupportedConfigurations}</code>, each
     * element is a <code>String</code>
     * <li>when populated via the <code>{@link #load}</code> method, each
     * element is a <code>{@link net.project.configuration.ConfigurationSpace}</code>
     * @see DomainConfigurationCollection
     */
    public ArrayList getConfigurationCollection() {
        return this.configurationCollection ;
    }


    /**
     * Returns the a collection of configurations supported by this UserDomain.
     * @return the configurations; the element type depends on how
     * the configuration collection was built: <br>
     * <li>When populated with <code>{@link #setSupportedConfigurations}</code>, each
     * element is a <code>String</code>
     * <li>when populated via the <code>{@link #load}</code> method, each
     * element is a <code>{@link net.project.configuration.ConfigurationSpace}</code>
     * @see DomainConfigurationCollection
     */
    public ArrayList getNewlyAddedConfigurationCollection() {
        return this.newlyAddedConfigCollection ;
    }


    /**
     * Returns whether a given configuration is supported or not
     *
     * @param configurationID
     *               The configuration ID
     * @return true if the configuration is supported
     */
    public boolean isConfigurationSupported(String configurationID) {
        Iterator itr = this.configurationCollection.iterator();

        while(itr.hasNext()){
            ConfigurationSpace cspace = (ConfigurationSpace) itr.next();
            if(cspace != null && cspace.getID() != null && cspace.getID().equals(configurationID))
                return true;

        }
        return false;
    }

    /**
     * Determine if a domain supports purchases by credit card.
     *
     * @return always true, for now.
     */
    public boolean supportsCreditCardPurchases() {
        return supportsCreditCardPurchases;
    }

    public void setSupportsCreditCardPurchases(boolean supportsCCPurchases) {
        supportsCreditCardPurchases = supportsCCPurchases;
    }

    //
    // Inner classes
    //

    /**
     * Provides the SearchableDirectory interface to this UserDomain.
     */
    class DomainSearchableDirectory implements ISearchableDirectory {

        /**
         * Returns the name of this directory.
         * @return the name
         */
        public String getSearchableDirectoryName() {
            return UserDomain.this.getName();
        }

        /**
         * Returns the description of this directory.
         * @return the description
         */
        public String getSearchableDirectoryDescription() {
            return UserDomain.this.getDescription();
        }

        /**
         * Returns the directory context for this directory.
         * The context actually provides the search facilities.
         * @return the directory context
         */
        public IDirectoryContext getDirectoryContext() {
            return new UserDomainDirectoryContext(UserDomain.this);
        }

    }
	/**
	 * @return the isCosignLoginEnabled
	 */
	public boolean isCosignLoginEnabled() {
		return PropertyProvider.getBoolean("prm.domains.ldapcosignconfiguration.isenabled");
	}

}
