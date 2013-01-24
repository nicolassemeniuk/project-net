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

import net.project.hibernate.model.PnBusiness;
import net.project.hibernate.model.PnBusinessSpace;


public interface IPnBusinessSpaceDAO extends IDAO<PnBusinessSpace, Integer> {
	
	public PnBusiness getBusinessByProjectId(Integer projectId);
	
	public PnBusinessSpace getBusinessSpaceById(Integer id);

	/**
	 * To check business is a root business
	 * @param spaceId
	 * @return boolean
	 */
	public boolean isRootBusines(Integer spaceId);
	
	/**
	 * To get parent business of business id
	 * @param businessId
	 * @return PnBusiness
	 */
	public PnBusiness getParentBusinessByBusinessId(Integer businessId);
}
