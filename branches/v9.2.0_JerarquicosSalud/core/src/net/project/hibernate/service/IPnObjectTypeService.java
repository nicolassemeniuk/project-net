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

import net.project.hibernate.model.PnObjectType;

public interface IPnObjectTypeService {
	
	/**
	 * @param pnObjectTypeId 
	 * @return PnObjectType bean
	 */
	public PnObjectType getObjectType(String pnObjectTypeId);
	
	/**
	 * Saves new PnObjectType
	 * @param PnObjectType object we want to save
	 * @return primary key for saved PnObjectType bean
	 */
	public String saveObjectType(PnObjectType pnObjectType);
	
	/**
	 * Deletes PnObjectType from database
	 * @param PnObjectType object we want to delete
	 */
	public void deleteObjectType(PnObjectType pnObjectType);
	
	/**
	 * Updates PnObjectType
	 * @param PnObjectType object we want to update
	 */
	public void updateObjectType(PnObjectType pnObjectType);
	
	/**
	 * selects all objectType and defaultPermissionActions
	 * @param PnObjectType object we want to update
	 */
	public List<PnObjectType> findObjectTypes();	

    public List<PnObjectType> findAll();

	/**
	 * @param objectId
	 * @return PnObjectType of objectId
	 */
	public PnObjectType getObjectTypeByObjectId(Integer objectId);

}
