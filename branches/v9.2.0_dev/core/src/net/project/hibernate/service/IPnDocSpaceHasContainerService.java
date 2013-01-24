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

import net.project.hibernate.model.PnDocSpaceHasContainer;
import net.project.hibernate.model.PnDocSpaceHasContainerPK;
import net.project.hibernate.service.filters.IPnDocSpaceHasContainerFilter;

public interface IPnDocSpaceHasContainerService {
	

	/**
	 * @param pnDocSpaceHasContainerId 
	 * @return PnDocSpaceHasContainer bean
	 */
	public PnDocSpaceHasContainer getDocSpaceHasContainer(PnDocSpaceHasContainerPK pnDocSpaceHasContainerId);
	
	/**
	 * Saves new PnDocSpaceHasContainer
	 * @param PnDocSpaceHasContainer object we want to save
	 * @return primary key for saved PnDocSpaceHasContainer bean
	 */
	public PnDocSpaceHasContainerPK saveDocSpaceHasContainer(PnDocSpaceHasContainer pnDocSpaceHasContainer);
	
	/**
	 * Deletes PnDocSpaceHasContainer from database
	 * @param PnDocSpaceHasContainer object we want to delete
	 */
	public void deleteDocSpaceHasContainer(PnDocSpaceHasContainer pnDocSpaceHasContainer);
	
	/**
	 * Updates PnDocSpaceHasContainer
	 * @param PnDocSpaceHasContainer object we want to update
	 */
	public void updateDocSpaceHasContainer(PnDocSpaceHasContainer pnDocSpaceHasContainer);

    public List<PnDocSpaceHasContainer> findByFilter(IPnDocSpaceHasContainerFilter filter);

}
