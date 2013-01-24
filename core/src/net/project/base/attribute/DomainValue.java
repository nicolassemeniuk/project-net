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
package net.project.base.attribute;

public class DomainValue implements IAttributeValue {
    private String displayName = null;
    private String id = null;
    private IAttribute iAttribute = null;

    /**
     * Creates a new DomainValue with the specified values.
     * @param id the id of the domain value
     * @param displayName the display name
     * @param iAttribute the attribute to which this domain value belongs
     */
    public DomainValue(String id, String displayName, IAttribute iAttribute) {
        this.displayName = displayName;
        this.id = id;
        this.iAttribute = iAttribute;
    }

    /**
     * Returns the Displayname for the current Object
     * @return String
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Returns the ID for the current Object
     * @return String
     */
    public String getID() {
        return this.id;
    }

    public IAttribute getAttribute() {
        return iAttribute;
    }
}

