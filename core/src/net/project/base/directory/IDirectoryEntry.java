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
 * Specifies the common properties of a Directory Entry.
 */
public interface IDirectoryEntry {

    /**
     * Indicates whether the profile attribute with the specified
     * ID is provided by this directory entry.
     * @param attributeID the attribute to check for
     * @return true if this directory entry provides a value for
     * the specified attribute; false otherwise
     */
    public boolean isAttributeProvided(String attributeID);


    /**
     * Returns this directory entry's value for the specified
     * attribute.
     * @param attributeID the attribute value to get
     * @return the value for that attribute; returns the empty
     * string if this directory entry does not provide the
     * specified attribute
     */
    public String getAttributeValue(String attributeID);

}
