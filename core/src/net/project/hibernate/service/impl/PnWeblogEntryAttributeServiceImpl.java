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
/**
 * 
 */
package net.project.hibernate.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import net.project.hibernate.dao.IPnWeblogEntryAttributeDAO;
import net.project.hibernate.model.PnWeblogEntryAttribute;
import net.project.hibernate.service.IPnWeblogEntryAttributeService;

/**
 * @author 
 *
 */
@Service(value="pnWeblogEntryAttributeService")
public class PnWeblogEntryAttributeServiceImpl implements IPnWeblogEntryAttributeService {
	
	/**
	 * PnEntryAttribute data access object
	 */
	@Autowired
	private IPnWeblogEntryAttributeDAO pnWeblogEntryAttributeDAO;
	
	/**
	 * sets PnWeblogEntryAttribute data acces object
	 * 
	 * @param pnWeblogEntryAttributeDAO the pnWeblogEntryAttributeDAO to set
	 */
	public void setPnWeblogEntryAttributeDAO(IPnWeblogEntryAttributeDAO pnWeblogEntryAttributeDAO) {
		this.pnWeblogEntryAttributeDAO = pnWeblogEntryAttributeDAO;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnEntryAttributeService#saveOrUpdate(net.project.hibernate.model.PnEntryAttribute)
	 */
	public java.lang.Integer save(PnWeblogEntryAttribute pnWeblogEntryAttribute) {
		return pnWeblogEntryAttributeDAO.create(pnWeblogEntryAttribute);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnEntryAttributeService#update(net.project.hibernate.model.PnEntryAttribute)
	 */
	public void update(PnWeblogEntryAttribute pnWeblogEntryAttribute) {
		pnWeblogEntryAttributeDAO.update(pnWeblogEntryAttribute);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnEntryAttributeService#get(java.lang.Integer)
	 */
	public PnWeblogEntryAttribute get(Integer key) {
		return pnWeblogEntryAttributeDAO.findByPimaryKey(key);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnEntryAttributeService#findAll()
	 */
	public List<PnWeblogEntryAttribute> findAll() {
		return pnWeblogEntryAttributeDAO.findAll();
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnEntryAttributeService#delete(net.project.hibernate.model.PnEntryAttribute)
	 */
	public void delete(PnWeblogEntryAttribute pnWeblogEntryAttribute) {
		pnWeblogEntryAttributeDAO.delete(pnWeblogEntryAttribute);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogEntryAttributeService#getTaskIdsFromTaskBlogEntries(java.lang.Integer)
	 */
	public List getTaskIdsFromTaskBlogEntries(Integer spaceId) {
		return pnWeblogEntryAttributeDAO.getTaskIdsFromTaskBlogEntries(spaceId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogEntryAttributeService#getWeblogEntryAtribute(java.lang.Integer, java.lang.String)
	 */
	public PnWeblogEntryAttribute getWeblogEntryAtribute(Integer weblogEntryId, String name) {
		return pnWeblogEntryAttributeDAO.getWeblogEntryAtribute(weblogEntryId, name);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogEntryAttributeService#getWeblogEntryAtributesByEntryId(java.lang.Integer)
	 */
	public List<PnWeblogEntryAttribute> getWeblogEntryAtributesByEntryId(Integer weblogEntryId) {
		return pnWeblogEntryAttributeDAO.getWeblogEntryAtributesByEntryId(weblogEntryId);
	}

}
