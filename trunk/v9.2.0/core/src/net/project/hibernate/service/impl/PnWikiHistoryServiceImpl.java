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

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.project.hibernate.dao.IPnWikiHistoryDAO;
import net.project.hibernate.model.PnWikiHistory;
import net.project.hibernate.service.IPnWikiHistoryService;

/**
 * @author 
 *
 */

@Service(value="pnWikiHistoryService")
public class PnWikiHistoryServiceImpl implements IPnWikiHistoryService {

	/**
	 * PnWikiPage data access object
	 */
	@Autowired
	private IPnWikiHistoryDAO pnWikiHistoryDAO;

	/**
	 * @param pnWikiHistoryDAO the pnWikiHistoryDAO to set
	 */
	public void setPnWikiHistoryDAO(IPnWikiHistoryDAO pnWikiHistoryDAO) {
		this.pnWikiHistoryDAO = pnWikiHistoryDAO;
	}	
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiHistoryService#save(net.project.hibernate.model.PnWikiHistory)
	 */
	public Integer save(PnWikiHistory pnWikiHistory) {
		return pnWikiHistoryDAO.create(pnWikiHistory);
	}	

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiPageService#update(net.project.hibernate.model.PnWikiPage)
	 */
	public void update(PnWikiHistory pnWikiHistory) {
		pnWikiHistoryDAO.update(pnWikiHistory);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiHistoryService#getWikiHistory(java.lang.Integer, java.util.Date, java.util.Date)
	 */
	public List<PnWikiHistory> getWikiHistory(Integer projectId, Date startDate, Date endDate) {
		return pnWikiHistoryDAO.getWikiHistory(projectId, startDate, endDate);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiHistoryService#findAll()
	 */
	public List<PnWikiHistory> findAll() {
		return pnWikiHistoryDAO.findAll();
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiHistoryService#get(java.lang.Integer)
	 */
	public PnWikiHistory get(Integer key) {
		PnWikiHistory history = pnWikiHistoryDAO.findByPimaryKey(key);
		return history;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiHistoryService#delete(net.project.hibernate.model.PnWikiHistory)
	 */
	public void delete(PnWikiHistory pnWikiHistory) {
		pnWikiHistoryDAO.delete(pnWikiHistory);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiHistoryService#findHistoryWithPageId(java.lang.Integer)
	 */
	public List<PnWikiHistory> findHistoryWithPageId(Integer wikiPageID) {
		return pnWikiHistoryDAO.findHistoryWithPageId(wikiPageID);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiHistoryService#updateWikiPageIds(Integer newWikiPageId, Integer oldWikiPageId)
	 */
	public void updateWikiPageIds(Integer newWikiPageId, Integer oldWikiPageId) {
		pnWikiHistoryDAO.updateWikiPageIds(newWikiPageId, oldWikiPageId);
	}

}
