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

/**
 * Defines the configuration of a directory.
 * The configuration is associated with a context and is passed
 * to a directory service provider implementation.
 * Currently a <code>Domain</code> is the only context that maintains
 * <code>DirectoryConfiguration</code> information.
 * It must contain all information needed to authenticate with a particular
 * directory type.
 *
 * @author Tim
 * @since Gecko Update 3
 */
public abstract class DirectoryConfiguration 
        implements net.project.persistence.IJDBCPersistence {

    //
    // Static members
    //

    /**
     * An empty configuration, used if a service provider requires no
     * additional configuration information.
     */
    public static DirectoryConfiguration EMPTY_CONFIGURATION = new EmptyConfiguration();


    //
    // Instance members
    //

    /**
     * The id of the Domain against which this DirectoryConfiguration
     * is stored.
     */
    private String domainID = null;

    /**
     * Sets the domainID  to which this directory 
     * configuration belongs.
     * @param domainID the id of the context; this may be any value
     * that conforms the the standard persistent store object id
     * @see #getID
     */
    public void setDomainID(String domainID) {
        this.domainID = domainID;
    }

    /**
     * Returns the domainID to which this directory 
     * configuration belongs.
     * @return the domainID
     * @see #setID
     */
    public String getDomainID() {
        return this.domainID;
    }

    /**
     * Sets the domaiID.
     * @param id the domainID
     * @see #setDomainID
     */
    public void setID(String id) {
        setDomainID(id);
    }

    public abstract void load() throws net.project.persistence.PersistenceException;

    public abstract void store() throws net.project.persistence.PersistenceException;

    public abstract void remove() throws net.project.persistence.PersistenceException;

    //
    // Nested top-level classes
    //

    /**
     * An empty configuration.
     * All methods perform no operations.
     */
    private static class EmptyConfiguration extends DirectoryConfiguration {
        
        public void setID(String id) {
            // Do nothing
        }

        public void load() throws net.project.persistence.PersistenceException {
            // Do nothing
        }

        public void store() throws net.project.persistence.PersistenceException {
            // Do nothing
        }

        public void remove() throws net.project.persistence.PersistenceException {
            // Do nothing
        }

    }

}
