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
import org.springframework.stereotype.Service;

import net.project.hibernate.dao.IPnTimelogDAO;
import net.project.hibernate.model.PnTimelog;
import net.project.hibernate.service.IPnTimelogService;

@Service(value="pnTimelogService")
public class PnTimelogServiceImpl implements IPnTimelogService {

	@Autowired
	private IPnTimelogDAO pnTimelogDAO;

	public void setPnTimelogDAO(IPnTimelogDAO pnTimelogDAO) {
		this.pnTimelogDAO = pnTimelogDAO;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnTimelogService#delete(net.project.hibernate.model.PnTimelog)
	 */
	public void delete(PnTimelog pnTimelog) {
		pnTimelogDAO.delete(pnTimelog);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnTimelogService#findAll()
	 */
	public List<PnTimelog> findAll() {
		return pnTimelogDAO.findAll();
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnTimelogService#saveOrUpdate(net.project.hibernate.model.PnTimelog)
	 */
	public Integer save(PnTimelog pnTimelog) {
		return pnTimelogDAO.create(pnTimelog);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnTimelogService#update(net.project.hibernate.model.PnTimelog)
	 */
	public void update(PnTimelog pnTimelog) {
		pnTimelogDAO.update(pnTimelog);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnTimelogService#get(net.project.hibernate.model.PnTimelogPK)
	 */
	public net.project.hibernate.model.PnTimelog get(java.lang.Integer timelogId) {
		return pnTimelogDAO.findByPimaryKey(timelogId);
	}
	
}
