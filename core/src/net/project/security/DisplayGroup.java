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
package net.project.security;

import net.project.security.group.Group;

/**
 * Provides a Group with presentation information, used when
 * configuring object permissions.
 */
public class DisplayGroup extends Group
        implements java.io.Serializable {

    private Group group = null;
    private boolean isDisplay = false;

    /**
     * Creates a new DisplayGroup based on the specified group.
     */
    public DisplayGroup(Group group) {
        super(group.getGroupTypeID(), group.isSystem());
        super.setID(group.getID());
        super.setName(group.getName());
        super.setDescription(group.getDescription());
        super.setSpaceID(group.getSpaceID());
        super.setMemberCount(group.getMemberCount());
        super.setLoaded(group.isLoaded());
    }

    public void setDisplay(boolean isDisplay) {
        this.isDisplay = isDisplay;
    }

    /**
     * Indicates whether this group should be displayed.
     * @return true if this group should be displayed; false otherwise
     */
    public boolean isDisplay() {
        return this.isDisplay;
    }

}
