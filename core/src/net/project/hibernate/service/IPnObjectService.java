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



import net.project.hibernate.model.PnObject;

public interface IPnObjectService {
	
	/** 
	 * @param primaryKey unique is id for PnObject bean 
	 * @return PnObject bean
	 */
	public PnObject getObject(Integer primaryKey);
	
	/** 
	 * @param primaryKey unique is id for PnObject bean 
	 * @return PnObject bean
	 */
	public PnObject getObjectWithProjectSpace(Integer primaryKey);
	
	/**
	 * Stores new PnObject on database
	 * @param pnObject is PnObject bean that we need to save in database
	 * @return saved object id, pnObject have sequence so we don't need to set primary key value
	 */
	public Integer saveObject(PnObject pnObject);

	public Integer generateNewId();
		
	/** 
	 * @param primaryKey unique is id for PnObject bean 
	 * @return PnObject bean
	 */
	public PnObject getObjectWithAssignedWikiPage(Integer primaryKey);
	
	public PnObject getObjectByObjectId(Integer objectId);
}
