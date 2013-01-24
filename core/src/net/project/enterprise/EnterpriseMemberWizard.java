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
package net.project.enterprise;

import net.project.resource.SpaceInvitationManager;

public class EnterpriseMemberWizard extends SpaceInvitationManager {
    /**
     * Indicates whether this directory supports Participants.
     *
     * @return true if this directory supports participants; false if not
     */
    public boolean isParticipantsSupported() {
        return true;
    }

    /**
     * Indicates whether this directory supports Org Chart.
     *
     * @return true if this directory supports Org Chart; false if not
     */
    public boolean isOrgChartSupported() {
        return false;
    }

    /**
     * Indicates whether this directory supports Assignments.
     *
     * @return true if this directory supports Assignments; false if not
     */
    public boolean isAssignmentsSupported() {
        return false;
    }

    /**
     * Indicates whether this directory supports Roles.
     *
     * @return true if this directory supports Roles; false if not
     */
    public boolean isRolesSupported() {
        return true;
    }
}
