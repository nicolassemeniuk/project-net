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
package net.project.hibernate.dao;

import java.util.List;

import net.project.hibernate.model.PnObjectPermission;
import net.project.hibernate.model.PnObjectPermissionPK;

public interface IPnObjectPermissionDAO extends
	IDAO<PnObjectPermission, PnObjectPermissionPK> {

    /**
     * Returns a <code>List</code> of {@link PnObjectPermission} for a passed groupId.
     * @param groupId
     * @return <code>List</code> of <code>PnObjectPermission</code>
     */
    public List<PnObjectPermission> getObjectPermissionsForGroup(Integer groupId);
    
    /**
     * Returns a <code>List</code> of {@link PnObjectPermission} by a passed objectId.
     * @param objectId
     * @return <code>List</code> of <code>PnObjectPermission</code>
     */
    public List<PnObjectPermission> getObjectPermissionsForObject(Integer objectId);

}
