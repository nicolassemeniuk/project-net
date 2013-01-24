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
|   $Revision: 19160 $
|       $Date: 2009-05-05 18:45:21 -0300 (mar, 05 may 2009) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.configuration;

import net.project.base.directory.DirectoryException;
import net.project.persistence.PersistenceException;

/**
  * Performs processing for adding team members to a Configuration Space.
  */
public class ConfigurationMemberWizard extends net.project.resource.SpaceInvitationManager {


    /**
     * Creates an empty ConfigurationMemberWizard.
     */
    public ConfigurationMemberWizard() {
        super();
    }

    /**
     * Indicates whether this directory supports Participants.
     * @return true always
     */
    public boolean isParticipantsSupported(){
        return true;
    }


    /**
     * Indicates whether this directory supports Org Chart.
     * @return false always
     */
    public boolean isOrgChartSupported() {
        return false;
    }

    
    /**
     * Indicates whether this directory supports Assignments.
     * @return false always
     */
    public boolean isAssignmentsSupported() {
        return false;
    }


    /**
     * Indicates whether this directory supports Roles.
     * @return true always
     */
    public boolean isRolesSupported() {
        return true;
    }
    
    /**
     * for loading directory without current space directory
     */
    protected void loadSearchableDirectories(boolean isIncludeCurrentDirectory) throws DirectoryException, PersistenceException {
    	loadSearchableDirectories(false);
    }

}
