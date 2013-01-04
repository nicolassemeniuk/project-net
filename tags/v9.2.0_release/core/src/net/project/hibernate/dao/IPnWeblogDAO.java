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

import net.project.hibernate.model.PnWeblog;

public interface IPnWeblogDAO extends IDAO<PnWeblog, Integer> {
	
	public PnWeblog getByUserId(Integer userId);

	/**
	 * @param userId
	 * @param spaceId
	 * @return
	 */
	public PnWeblog getByUserAndSpaceId(Integer userId, Integer spaceId);

	/**
	 * @param spaceId
	 * @return
	 */
	public PnWeblog getBySpaceId(Integer spaceId);
	
	/**
	 * Method for getting weblog by space id
	 *
	 * @param spaceId
	 * @param initializePersonObject
	 * @return PnWeblog object 
	 */
	public PnWeblog getBySpaceId(Integer spaceId, boolean initializePersonObject);
	
	/**
	 * Get PnWeblog by id
	 * @param weblogId
	 * @return
	 */
	public PnWeblog getPnWeblogById(int weblogId);
	
}