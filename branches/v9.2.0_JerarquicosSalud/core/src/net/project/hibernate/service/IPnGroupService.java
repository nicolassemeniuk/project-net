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

import net.project.hibernate.model.PnGroup;

public interface IPnGroupService {
	
	/**
	 * @param groupId for PnGroup we need to select from database
	 * @return PnGroup bean
	 */
	public PnGroup getGroup(Integer groupId);
	
	/**
	 * Saves new Group
	 * @param pnGroup object we want to save
	 * @return primary key for saved Group
	 */
	public Integer saveGroup(PnGroup pnGroup);
	
	/**
	 * Deletes Group from database
	 * @param pnGroup object we want to delete
	 */
	public void deleteGroup(PnGroup pnGroup);
	
	/**
	 * Updates Group
	 * @param pnGroup object we want to update
	 */
	public void updateGroup(PnGroup pnGroup);
	
	/**
	 * Returns a <code>List</code> of {@link PnGroup} for provided spaceId and groupTypeId
	 * @param spaceId
	 * @param groupTypeId
	 * @return <code>List</code> of <code>PnGroup</code>
	 */
	public List<PnGroup> getGroupForSpaceAndGroupType(Integer spaceId, Integer groupTypeId);
	
	/**
	 * Returns principal {@link PnGroup} for space and person.
	 * @param spaceId
	 * @param personId
	 * @return <code>PnGroup</code>
	 */
	public PnGroup getPrincipalGroupForSpaceAndPerson(Integer spaceId, Integer personId);

}
