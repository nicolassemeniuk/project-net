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

import net.project.hibernate.model.PnModule;

public interface IPnModuleService {
	
	/**
	 * @param moduleId for Module we need to select from database
	 * @return PnModule bean
	 */
	public PnModule getModule(Integer moduleId);
	
	/**
	 * Saves new PnModule
	 * @param pnModule object we want to save
	 * @return primary key for saved Module
	 */
	public Integer saveModule(PnModule pnModule);
	
	/**
	 * Deletes Module from database
	 * @param pnModule object we want to delete
	 */
	public void deleteModule(PnModule pnModule);
	
	/**
	 * Updates Module
	 * @param pnModule object we want to update
	 */
	public void updateModule(PnModule pnModule);
	
	/**
	 * select all moduleIds from PnModule
	 * @return List of module IDs
	 */
	public List<PnModule> getModuleIds();
	
	/**
	 * select moduleId and defaultPermissionActions from PnModule
	 * @param spaceId 
	 * @return List of PnModule beans
	 */
	public List<PnModule> getModuleDefaultPermissions(Integer spaceId);


	public List<PnModule> getAll();
	
	/**
	 * Gets a <code>List</code> of {@link PnModule} for given spaceId.
	 * @param spaceId
	 * @return <code>List</code> of <code>PnModule</code>
	 */
	public List<PnModule> getModulesForSpace(Integer spaceId);

}
