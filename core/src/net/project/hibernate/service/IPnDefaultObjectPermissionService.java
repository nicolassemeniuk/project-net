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
package net.project.hibernate.service;


import java.util.List;

import net.project.hibernate.model.PnDefaultObjectPermission;
import net.project.hibernate.model.PnDefaultObjectPermissionPK;
import net.project.hibernate.service.filters.IPnDefaultObjectPermissionFilter;

public interface IPnDefaultObjectPermissionService {
	

	/**
	 * @param pnDefaultObjectPermissionId 
	 * @return PnDefaultObjectPermission bean
	 */
	public PnDefaultObjectPermission getDefaultObjectPermission(PnDefaultObjectPermissionPK pnDefaultObjectPermissionId);
	
	/**
	 * Saves new PnDefaultObjectPermission
	 * @param PnDefaultObjectPermission object we want to save
	 * @return primary key for saved PnDefaultObjectPermission bean
	 */
	public PnDefaultObjectPermissionPK saveDefaultObjectPermission(PnDefaultObjectPermission pnDefaultObjectPermission);
	
	/**
	 * Deletes PnDefaultObjectPermission from database
	 * @param PnDefaultObjectPermission object we want to delete
	 */
	public void deleteDefaultObjectPermission(PnDefaultObjectPermission pnDefaultObjectPermission);
	
	/**
	 * Updates PnDefaultObjectPermission
	 * @param PnDefaultObjectPermission object we want to update
	 */
	public void updateDefaultObjectPermission(PnDefaultObjectPermission pnDefaultObjectPermission);

	/**
	 * Gets a <code>List</code> of {@link PnDefaultObjectPermission} by given space Id and object type for non principal group.
	 * @param spaceId
	 * @param objectType
	 * @return <code>List</code> of <code>PnDefaultObjectPermissions</code>
	 */
	public List<PnDefaultObjectPermission> getDefaultObjectPermisionsBySpaceAndObjectTypeForNonPrincipalGroup(Integer spaceId, String objectType);
	
	/**
	 * Gets a <code>List</code> of {@link PnDefaultObjectPermission} by given space Id and object type for system non principal group.
	 * @param spaceId
	 * @param objectType
	 * @return <code>List</code> of <code>PnDefaultObjectPermissions</code>
	 */
	public List<PnDefaultObjectPermission> getDefaultObjectPermissionsBySpaceAndObjectTypeForSystemNonPrincipalGroup(Integer spaceId, String objectType);

	public void deleteDefaultObjectPermisionsByGroupId(Integer groupId);

	public void deleteDefaultObjectPermisionsByGroupIdAndSpaceID(Integer groupId, Integer spaceId);

	public List<PnDefaultObjectPermission> getAll();

	public List<PnDefaultObjectPermission> getByFilter(IPnDefaultObjectPermissionFilter filter);
}
