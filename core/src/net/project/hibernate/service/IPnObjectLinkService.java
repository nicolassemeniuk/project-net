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

import net.project.hibernate.model.PnObjectLink;
import net.project.hibernate.model.PnObjectLinkPK;
import net.project.hibernate.service.filters.IPnObjectLinkFilter;

public interface IPnObjectLinkService {
	public List<PnObjectLink> findByFilter(IPnObjectLinkFilter filter);

	public List<PnObjectLink> getObjectsById(Integer parentId, Integer contextId);
	
	/**
	 * Saves new PnObjectLink
	 * @param pnObjectLink object we want to save
	 * @return primary key for saved Module
	 */
	public PnObjectLinkPK saveObjectLink(PnObjectLink pnObjectLink);
	
	public void saveOrUpdateObjectLink(PnObjectLink pnObjectLink);

	public List<PnObjectLink> getObjectLinksByParent(Integer fromObjectId);
	
	public List<PnObjectLink> getObjectLinksByChild(Integer toObjectId);
}
