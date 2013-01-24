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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|
+--------------------------------------------------------------------------------------*/
package net.project.link;

import java.io.Serializable;

import net.project.base.RecordStatus;

/**
 * This is the interface for for classes that can be linked using the LinkManager.
 *
 * @author Brian Conneen
 * @since Version 1
 */
public interface ILinkableObject extends Serializable {


    /**
     * Defines a context value.
     * This is the only supported context.
     */
    public static final int GENERAL = 10;

    /**
     * Get the ID of the object.  The ID is the object_id of the object in the database.
     * @return the database object_id for this object.
     */
    public String getID();

    /**
     * Set the ID of the object.  The ID is the object_id of the object in the database.
     *
     * @param objectID the ID of the object in the database.
     *
     *
     */
    public void setID(String objectID);

    /**
     * Get the name of the object.  This is a String reprensation of the object, it could
     * be the file name for a Document, or the Title for a post, etc.
     *
     * @return the database object_id for this object.
     */
    public String getName();

    /**
     * Get the type of the object as registered in the database. (pn_object table).
     *
     * @return the type string for the object as defined in the database.
     */
    public String getType();

    /**
     * Returns the record status of this linkable object.
     * Deleted objects should not be displayed or be linkable.
     * @return the record status
     */
    public RecordStatus getRecordStatus();

    /**
     * Load an object from the persistable store.
     * @throws net.project.persistence.PersistenceException if the load operation fails.
     */
    void load() throws net.project.persistence.PersistenceException;

    /**
     * Gets the URL for linking to a view of an object.
     * @return a string representing the correct HREF
     */
    String getURL();

}