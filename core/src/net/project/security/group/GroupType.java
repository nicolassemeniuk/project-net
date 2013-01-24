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
package net.project.security.group;

/**
 * A group type represents a type of group in our system.  The actual
 * types are loaded by <code>{@link GroupTypes}</code>.  There is one group
 * type for each ID in <code>{@link GroupTypeID}</code>/
 */
public class GroupType implements java.io.Serializable {

    private GroupTypeID id = null;
    private String className = null;
    private Class groupClass = null;

    protected GroupType() {
        // Nothing
    }

    /**
     * Sets the id of this group type.
     * @param id the group type id
     * @see #getID
     */
    protected void setID(GroupTypeID id) {
        this.id = id;
    }

    /**
     * Returns this type's id.
     * @return the id of this type
     * @see #setID
     */
    public GroupTypeID getID() {
        return this.id;
    }
   
    /**
     * Sets this group type's class name.
     * @param className the class name
     * @see #getClassName
     */
    protected void setClassName(String className) {
        this.className = className;
    }

    /**
     * Returns the java class name that represents a group of this type.
     * @return the class name
     * @see #setClassName
     */
    protected String getClassName() {
        return this.className;
    }

    /**
     * Returns the instantiated java class for this group type's class name.
     * @return the java class
     * @throws ClassNotFoundException if the class cannot be found
     */
    Class getGroupClass() throws ClassNotFoundException {
        if (this.groupClass == null) {
            this.groupClass = Class.forName(getClassName());
        }
        return this.groupClass;
    }

}
