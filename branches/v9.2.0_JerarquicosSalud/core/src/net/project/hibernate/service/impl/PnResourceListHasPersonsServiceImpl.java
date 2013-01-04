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
package net.project.hibernate.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.project.hibernate.dao.IPnResourceListHasPersonsDAO;
import net.project.hibernate.model.PnResourceListHasPersons;
import net.project.hibernate.service.IPnResourceListHasPersonsService;

@Service(value="resourceListHasPersonsService")
public class PnResourceListHasPersonsServiceImpl implements IPnResourceListHasPersonsService {

	@Autowired
	private IPnResourceListHasPersonsDAO pnResourceListHasPersonsDAO ;	

	/**
	 * sets PnResourceListHasPersonsDAO data access object
	 * 
	 * @param pnResourceListHasPersonsDAO The pnResourceListHasPersonsDAO to set.
	 */
	public void setPnResourceListHasPersonsDAO(IPnResourceListHasPersonsDAO pnResourceListHasPersonsDAO) {
		this.pnResourceListHasPersonsDAO = pnResourceListHasPersonsDAO;
	}	
	
	/**
	 * saves selected resources
	 */	
	public void save(Integer resourceListId, String[] personIds){
		pnResourceListHasPersonsDAO.save(resourceListId, personIds);
	}

	public void delete(PnResourceListHasPersons pnResourceListHasPersons) {		
		pnResourceListHasPersonsDAO.delete(pnResourceListHasPersons);
	}
	
	public List<PnResourceListHasPersons> getResourcesByListId(Integer resourceListId){
		return pnResourceListHasPersonsDAO.getResourcesByListId(resourceListId);
	}

	public Integer getResourcesCountByListId(Integer resourceListId) {		
		return pnResourceListHasPersonsDAO.getResourcesCountByListId(resourceListId);
	}	
	
}
