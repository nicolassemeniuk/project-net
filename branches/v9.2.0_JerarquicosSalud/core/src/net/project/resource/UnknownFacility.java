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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.resource;

import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;

/**
 * This is a facility that has yet to be created.  It is used as a placeholder
 * when the user hasn't selected a facility or when one cannot be loaded from
 * the database.
 *
 * @author Matthew Flower
 */
public final class UnknownFacility extends Facility {
    public UnknownFacility() {
        this.name = "";
        this.description = "";
    }

    /** Sets the id of the facilities owner. */
    public void setOwnerId(String id) {
        throw new UnsupportedOperationException("This facility type does not support an owner.");
    }

    /** Gets the id of the facilities owner. */
    public String getOwnerId() {
        return null;
    }

    /**
     * Gets the type of this facility.
     */
    public FacilityType getType() {
        return FacilityType.UNKNOWN;
    }

    /**
     * Load an object from the persistable store.
     * IJDBCPersistence, the store is assumed to be a JDBC accessible data store,
     * however, this method makes no assumptions (in an of itself) where the object
     * will be loaded from.
     *
     * @exception PersistenceException If the load operation fails.
     * @since The beginning of time
     */
    public void load() throws PersistenceException {
        throw new UnsupportedOperationException("This facility type cannot be loaded from persistent storage");
    }

    /**
     * Store an object to the persistable store.
     * IJDBCPersistence, the store is assumed to be a JDBC accessible data store,
     * however, this method makes no assumptions (in an of itself) where the object
     * will be stored to.
     *
     * @exception PersistenceException If the store operation fails.
     * @since The beginning of time
     */
    public void store() throws PersistenceException {
        throw new UnsupportedOperationException("This facility type cannot be stored.");
    }

    /**
     * Remove an object from the persistable store.
     * IJDBCPersistence, the store is assumed to be a JDBC accessible data store,
     * however, this method makes no assumptions (in an of itself) where the object
     * will be remove from.
     *
     * @exception PersistenceException If the remove operation fails.
     * @since The beginning of time
     */

    public void remove() throws PersistenceException {
        throw new UnsupportedOperationException("This facility type cannot be removed.");
    }

    /**
     * Returns this object's XML representation, including the XML version tag.
     *
     * @return XML representation of this object
     * @see IXMLPersistence#getXMLBody
     * @see IXMLPersistence#XML_VERSION
     */
    public String getXML() {
        return null;
    }

    /**
     * Returns this object's XML representation, without the XML version tag.
     * @return XML representation of this object
     * @see IXMLPersistence#getXML
     */
    public String getXMLBody() {
        return null;
    }
}
