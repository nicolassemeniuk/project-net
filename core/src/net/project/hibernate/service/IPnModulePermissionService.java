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

import net.project.hibernate.model.PnModulePermission;
import net.project.hibernate.model.PnModulePermissionPK;
import net.project.hibernate.service.filters.IPnModulePermissionFilter;

public interface IPnModulePermissionService {
	

	/**
	 * @param pnModulePermissionId 
	 * @return PnModulePermission bean
	 */
	public PnModulePermission getModulePermission(PnModulePermissionPK pnModulePermissionId);
	
	/**
	 * Saves new PnModulePermission
	 * @param PnModulePermission object we want to save
	 * @return primary key for saved PnModulePermission bean
	 */
	public PnModulePermissionPK saveModulePermission(PnModulePermission pnModulePermission);
	
	/**
	 * Deletes PnModulePermission from database
	 * @param PnModulePermission object we want to delete
	 */
	public void deleteModulePermission(PnModulePermission pnModulePermission);
	
	/**
	 * Updates PnModulePermission
	 * @param PnModulePermission object we want to update
	 */
	public void updateModulePermission(PnModulePermission pnModulePermission);

	public void deleteModulePermissionsByGroupId(Integer groupId);

	public void deleteModulePermissionsBySpaceAndGroupId(Integer spaceId, Integer groupId);

	public List<PnModulePermission> findAll();

	public List<PnModulePermission> findByFilter(IPnModulePermissionFilter filter);
    
	/**
	 * Returns a <code>List</code> of {@link PnModulePermission} with given spaceId and moduleId.
	 * @param spaceId spaceId
	 * @param moduleId moduleId
	 * @return <code>List</code>  of <code>PnModulePermission</code>
	 */
	public List<PnModulePermission> getModulePermissionsBySpaceAndModule(Integer spaceId, Integer moduleId);

}
