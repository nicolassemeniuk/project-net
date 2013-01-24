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

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.directory;

import java.sql.SQLException;

import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;

/**
 * A <code>DirectoryProviderType</code> defines a directory mechanism
 * for authentication and directory lookup.
 * Each one is coupled with a directory service provider class
 * that performs the core directory services for a particular directory type.
 *
 * @author Tim
 * @since Gecko Update 3
 */
public class DirectoryProviderType {

    //
    // Static members
    //

    /**
     * The id of the Native DirectoryProviderType.
     * This should never be used in any kind of decision logic; it is expected
     * that additional DirectoryProviderTypes will be added later
     * without the need for defining any constants.
     */
    public static final String NATIVE_ID = "1";
    
    /**
     * The id of the LDAP DirectoryProviderType.
     * This should never be used in any kind of decision logic; it is expected
     * that additional DirectoryProviderTypes will be added later
     * without the need for defining any constants.
     */
    public static final String LDAP_ID = "2";

    /**
     * Returns a select query used to load a DirectoryProviderType from
     * persistent store.
     * @return the query
     */
    static String getLoadQuery() {
        StringBuffer loadQuery = new StringBuffer();
        loadQuery.append("select dpt.provider_type_id, dpt.name, dpt.description, ");
        loadQuery.append("dpt.service_provider_class_name, dpt.configurator_class_name, ");
        loadQuery.append("dpt.configuration_class_name ");
        loadQuery.append("from pn_directory_provider_type dpt ");
        return loadQuery.toString();
    }

    /**
     * Populates the specified DirectoryProviderType from the specified
     * ResultSet.
     * @param result the ResultSet, the result of executing an appropriate
     * load query
     * @param providerType the DirectoryProviderType object to populate
     * from the current row in the ResultSet
     * @throws SQLException if there is a prolem fetching a value from the
     * ResultSet
     * @see #getLoadQuery
     * @see java.sql.ResultSet#getString
     */
    static void populate(java.sql.ResultSet result, DirectoryProviderType providerType) 
            throws java.sql.SQLException {

        providerType.setID(result.getString("provider_type_id"));
        providerType.setName(PropertyProvider.get(result.getString("name")));
        providerType.setDescription(result.getString("description"));
        providerType.setServiceProviderClassName(result.getString("service_provider_class_name"));
        providerType.setConfiguratorClassName(result.getString("configurator_class_name"));
        providerType.setConfigurationClassName(result.getString("configuration_class_name"));
    }


    //
    // Instance members
    //

    /** The unique ID of this provider type. */
    private String id = null;

    /** The display name. */
    private String name = null;

    /** Short description. */
    private String description = null;

    /** 
     * The name of the class of type <code>DirectorySPI</code>
     * that provides directory services.
     */
    private String serviceProviderClassName = null;

    /**
     * The name of the class of type <code>DirectoryConfigurator</code>
     * that allows configuration of a directory provider.
     */
    private String configuratorClassName = null;

    /**
     * The name of the class of type <code>DirectoryConfiguration</code>
     * that stores the configuration for access to the directory.
     */
    private String configurationClassName = null;


    /**
     * Creates an empty <code>DirectoryProviderType</code>.
     */
    public DirectoryProviderType() {
        // Do nothing
    }

    /**
     * Sets the id of this provider type.
     * @param id the id
     * @see #getID
     */
    public void setID(String id) {
        this.id = id;
    }

    /**
     * Returns the id of this provider type.
     * @return the id
     * @see #setID
     */
    public String getID() {
        return this.id;
    }

    /**
     * Sets the name of this provider type.
     * @param name the display name
     * @see #getName
     */
    void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the display name of this provider type.
     * @return the display name
     * @see #setName
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the description of this provider type.
     * @param description the description
     * @see #getDescription
     */
    void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the description of this provider type.
     * @return the description
     * @see #setDescription
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the name of the class of type <code>DirectorySPI</code>
     * that provides an implementation of the directory that this
     * provider type represents.
     * @param serviceProviderClassName the name of the class fully
     * referenced by its package name; for example <code>net.project.base.directory.ldap.LDAPDirectoryProvider</code>
     * @see #getServiceProviderClassName
     */
    void setServiceProviderClassName(String serviceProviderClassName) {
        this.serviceProviderClassName = serviceProviderClassName;
    }

    /**
     * Returns the name of the class that provides directory services.
     * @return the class name
     * @see #setServiceProviderClassName
     */
    private String getServiceProviderClassName() {
        return this.serviceProviderClassName;
    }

    /**
     * Sets the name of the class of type <code>DirectoryConfigurator</code>
     * that provides for configuring the parameters needed to authenticate
     * with a directory of the type represented by this provider.
     * @param configuratorClassName the name of the class fully
     * referenced by its packagte name; for example <code>net.project.base.directory.ldap.LDAPConfigurator</code>
     * @see #getConfiguratorClassName
     */
    void setConfiguratorClassName(String configuratorClassName) {
        this.configuratorClassName = configuratorClassName;
    }

    /**
     * Returns the name of the class that provides configuration services
     * @return the class name
     * @see #setConfiguratorClassName
     */
    private String getConfiguratorClassName() {
        return this.configuratorClassName;
    }

    /**
     * Sets the name of the class of type <code>DirectoryConfiguration</code>
     * that stores the directory configuration.
     * This configuration is stored with the <code>UserDomain</code>; it
     * is populated by the Configurator.
     * @param configurationClassName the name of the class fully
     * referenced by its packagte name; for example <code>net.project.base.directory.ldap.LDAPConfiguration</code>
     * @see #getConfigurationClassName
     */
    void setConfigurationClassName(String configurationClassName) {
        this.configurationClassName = configurationClassName;
    }

    /**
     * Returns the name of the class that provides configuration services
     * @return the class name
     * @see #setConfigurationClassName
     */
    private String getConfigurationClassName() {
        return this.configurationClassName;
    }

    /**
     * Returns an instantiated <code>DirectorySPI</code> for this
     * provider type's service provider class name.
     * @return the instantied directory service provider
     * @throws DirectoryException if there is a problem instantiating
     * the service provider, for example the class cannot be found,
     * is an abstract class or interface, or if it does not have
     * a public no-arg constructor.
     */
    public DirectorySPI newServiceProvider() throws DirectoryException {
        return (DirectorySPI) newInstance(getServiceProviderClassName());
    }

    /**
     * Returns an instantiated <code>DirectoryConfigurator</code> for
     * this provider type's configurator class name.
     * @return the instantiated directory configurator
     * @throws DirectoryException if there is a problem instantiating
     * the configurator, for example the class cannot be found,
     * is an abstract class or interface, or if it does not have
     * a public no-arg constructor.
     */
    public DirectoryConfigurator newConfigurator() throws DirectoryException {
        return (DirectoryConfigurator) newInstance(getConfiguratorClassName());
    }

    /**
     * Returns an instantiated <code>DirectoryConfiguration</code> for
     * this provider type's configuration class name.
     * @return the instantiated directory configuration
     * @throws DirectoryException if there is a problem instantiating
     * the configuration, for example the class cannot be found,
     * is an abstract class or interface, or if it does not have
     * a public no-arg constructor.
     */
    public DirectoryConfiguration newConfiguration() throws DirectoryException {
        return (DirectoryConfiguration) newInstance(getConfigurationClassName());
    }


    /**
     * Instantiates the class with the specified name.
     * @param className the name of the class to instantiate
     * @return the instance of the class
     * @throws DirectoryException if there is a problem instantiating
     * the class
     */
    private Object newInstance(String className) throws DirectoryException {
        Object instance = null;

        try {
            instance = Class.forName(className).newInstance();

        } catch (java.lang.ClassNotFoundException e) {
            // Class cannot be located
            throw new DirectoryException("Directory class not found: " + e, e);

        } catch (java.lang.InstantiationException e) {
            // Class cannot be instantiated
            throw new DirectoryException("Directory class " + className + " cannot be instantiated: " + e, e);
        
        } catch (java.lang.IllegalAccessException e) {
            // Class or initializer not accessible
            throw new DirectoryException("Directory class " + className + " cannot be instantiated: " + e, e);
        
        }
        
        return instance;
    }

    /**
     * Loads this DirectoryProviderType for the current id.
     * @throws PersistenceException if there is a problem loading
     */
    public void load() throws net.project.persistence.PersistenceException {
        
        // Get query and add appropriate where clause to it
        StringBuffer loadQuery = new StringBuffer(DirectoryProviderType.getLoadQuery());
        loadQuery.append("where dpt.provider_type_id = ? ");

        net.project.database.DBBean db = new net.project.database.DBBean();

        try {
            int index = 0;
            db.prepareStatement(loadQuery.toString());
            db.pstmt.setString(++index, getID());
            db.executePrepared();

            if (db.result.next()) {
                // Populate this object from the result set
                DirectoryProviderType.populate(db.result, this);
            }
        
        } catch (java.sql.SQLException sqle) {
            throw new net.project.persistence.PersistenceException("DirectoryProviderType load operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

    }

}
