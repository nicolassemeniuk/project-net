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

import net.project.hibernate.model.PnDocContainer;
import net.project.hibernate.service.filters.IPnDocContainerFilter;

public interface IPnDocContainerService {

	/**
	 * @param docContainerId for DocContainer we need to select from database
	 * @return PnDocContainer bean
	 */
	public PnDocContainer getDocContainer(Integer docContainerId);
	
	/**
	 * Saves new DocContainer
	 * @param pnDocContainer object we want to save
	 * @return primary key for saved DocContainer
	 */
	public Integer saveDocContainer(PnDocContainer pnDocContainer);
	
	/**
	 * Deletes DocContainer from database
	 * @param pnDocContainer object we want to delete
	 */
	public void deleteDocContainer(PnDocContainer pnDocContainer);
	
	/**
	 * Updates DocContainer
	 * @param pnDocContainer object we want to update
	 */
	public void updateDocContainer(PnDocContainer pnDocContainer);

    public List<PnDocContainer> findByFilter(IPnDocContainerFilter filter); 
    
    /**
	 * Get doc container details with record status
	 * @param docContainerId
	 * @return Object PnDocContainer
	 */
	public PnDocContainer getDocContainerWithRecordStatus(Integer docContainerId);
	
	/**
	 * Get doc container details with is hidden
	 * @param documentId
	 * @return Object PnDocContainer
	 */
	public PnDocContainer getDocContainerWithIsHidden(Integer documentId);
}
