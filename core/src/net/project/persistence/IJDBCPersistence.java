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

 package net.project.persistence;

import java.sql.SQLException;


/**
 * This interface defines the methods that should be implemented by classes
 * able to represent themselves in JDBC compatible way.
 * Classes may implement the persistance operations directly or make use of 
 * a persistance manager.
 * @author Roger Bly
 * @since The beginning of time
 */
public interface IJDBCPersistence {
    /**
     * Set the unique database ID of the object.
     * This is almost always required to be set to load any object.
     * @param id the unique ID of the object.
     */
    public void setID(String id);

    /**
     * Load an object from persistent store.
     * The store is assumed to be a JDBC accessible data store.
     * @throws PersistenceException if the load operation fails
     * @see #setID
     */
    void load() throws PersistenceException;

    /**
     * Store an object to persistent store.
     * The store is assumed to be a JDBC accessible data store.
     * @throws PersistenceException if the store operation fails
     * @throws SQLException 
     */
    void store() throws PersistenceException, SQLException;

    /**
     * Removes an object from persistent store.
     * The store is assumed to be a JDBC accessible data store.
     * @throws PersistenceException if the remove operation fails
     */
    void remove() throws PersistenceException;

}