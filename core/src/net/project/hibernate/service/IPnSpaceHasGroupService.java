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

import net.project.hibernate.model.PnSpaceHasGroup;
import net.project.hibernate.model.PnSpaceHasGroupPK;
import net.project.hibernate.service.filters.IPnSpaceHasGroupServiceFilter;

public interface IPnSpaceHasGroupService {	

	/**
	 * @param pnGroupHasPersonId 
	 * @return PnGroupHasPerson bean
	 */
	public PnSpaceHasGroup getSpaceHasGroup(PnSpaceHasGroupPK pnSpaceHasGroupId);
	
	/**
	 * Saves new PnSpaceHasGroup
	 * @param PnSpaceHasGroup object we want to save
	 * @return primary key for saved space and Portfolio
	 */
	public PnSpaceHasGroupPK saveSpaceHasGroup(PnSpaceHasGroup pnSpaceHasGroup);
	
	/**
	 * Deletes PnSpaceHasGroup from database
	 * @param PnSpaceHasGroup object we want to delete
	 */
	public void deleteSpaceHasGroup(PnSpaceHasGroup pnSpaceHasGroup);
	
	/**
	 * Updates PnSpaceHasGroup
	 * @param PnSpaceHasGroup object we want to update
	 */
	public void updateSpaceHasGroup(PnSpaceHasGroup pnSpaceHasGroup);

    public void deleteByGroupId(Integer groupId);

    public void deleteByGroupIdAndOwner(Integer groupId, long isOwner);

    public List<PnSpaceHasGroup> getAll();

    public List<PnSpaceHasGroup> getByFilter(IPnSpaceHasGroupServiceFilter filter); 
}
