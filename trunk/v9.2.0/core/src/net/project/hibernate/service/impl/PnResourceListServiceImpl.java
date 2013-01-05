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

import net.project.hibernate.dao.IPnResourceListDAO;
import net.project.hibernate.model.PnResourceList;
import net.project.hibernate.service.IPnResourceListService;

@Service(value="resourceListService")
public class PnResourceListServiceImpl implements IPnResourceListService {

	@Autowired
	private IPnResourceListDAO pnResourceListDAO;
		
	/**
	 * @return the pnResourceListDAO
	 */
	public IPnResourceListDAO getPnResourceListDAO() {
		return pnResourceListDAO;
	}

	/**
	 * @param pnResourceDAO The pnResourceDAO to set.
	 */
	public void setPnResourceListDAO(IPnResourceListDAO pnResourceDAO) {
		this.pnResourceListDAO = pnResourceDAO;
	}

	public List<PnResourceList> getResourceList() {
		return pnResourceListDAO.getResourceList();
	}

	public Integer saveResourceList(PnResourceList pnResourceList) {
		return pnResourceListDAO.create(pnResourceList);		
	}
	
	public void deleteResourceList(PnResourceList pnResourceList){
		pnResourceListDAO.delete(pnResourceList);
	}
	
	public void updateResourceList(PnResourceList pnResourceList){
		pnResourceListDAO.update(pnResourceList);
	}
	
	public PnResourceList getResourceListById(Integer id) {
		return pnResourceListDAO.findByPimaryKey(id);
	}
}
