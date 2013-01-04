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

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.project.hibernate.dao.IPnSpaceAccessHistoryDAO;
import net.project.hibernate.model.PnSpaceAccessHistory;
import net.project.hibernate.model.PnSpaceAccessHistoryPK;
import net.project.hibernate.service.IPnSpaceAccessHistoryService;

/**
 *
 */
@Service(value="pnSpaceAccessHistoryService")
public class PnSpaceAccessHistoryServiceImpl implements IPnSpaceAccessHistoryService {
		
	/**
	 * PnSpaceAccessHistory data access object
	 */
	@Autowired
	private IPnSpaceAccessHistoryDAO pnSpaceAccessHistoryDAO;

	/**
	 * @param pnSpaceAccessHistoryDAO the pnSpaceAccessHistoryDAO to set
	 */
	public void setPnSpaceAccessHistoryDAO(IPnSpaceAccessHistoryDAO pnSpaceAccessHistoryDAO) {
		this.pnSpaceAccessHistoryDAO = pnSpaceAccessHistoryDAO;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnSpaceAccessHistoryService#save(net.project.hibernate.model.PnSpaceAccessHistory)
	 */
	public PnSpaceAccessHistoryPK save(PnSpaceAccessHistory object) {
		return pnSpaceAccessHistoryDAO.create(object);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnSpaceAccessHistoryService#delete(net.project.hibernate.model.PnSpaceAccessHistory)
	 */
	public void delete(PnSpaceAccessHistory object) {
		pnSpaceAccessHistoryDAO.delete(object);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnSpaceAccessHistoryService#findAll()
	 */
	public List<PnSpaceAccessHistory> findAll() {
		return pnSpaceAccessHistoryDAO.findAll();
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnSpaceAccessHistoryService#getBySpaceAndUserId(net.project.hibernate.model.PnSpaceAccessHistoryPK)
	 */
	public PnSpaceAccessHistory getBySpaceAndUserId(PnSpaceAccessHistoryPK key) {
		return pnSpaceAccessHistoryDAO.findByPimaryKey(key);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnSpaceAccessHistoryService#update(net.project.hibernate.model.PnSpaceAccessHistory)
	 */
	public void update(PnSpaceAccessHistory object) {
		pnSpaceAccessHistoryDAO.update(object);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnSpaceAccessHistoryService#getSpaceHistory(java.lang.Integer, java.lang.Integer)
	 */
	public Date getSpaceHistory(Integer userId, Integer spaceId) {
		return pnSpaceAccessHistoryDAO.getSpaceHistory(userId, spaceId);
	}
	
}
