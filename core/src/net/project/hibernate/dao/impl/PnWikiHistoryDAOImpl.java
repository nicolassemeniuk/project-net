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
package net.project.hibernate.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.project.hibernate.model.PnWikiHistory;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * PnWikiHistory database object implementation.
 */
@Transactional 
@Repository 
public class PnWikiHistoryDAOImpl extends AbstractHibernateAnnotatedDAO<PnWikiHistory, Integer> implements net.project.hibernate.dao.IPnWikiHistoryDAO {
	
	private static Logger log = Logger.getLogger(PnWikiPageDAOImpl.class);
	
	public PnWikiHistoryDAOImpl () {
		super(PnWikiHistory.class);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnWikiHistoryDAO#findHistoryWithPageId(java.lang.Integer)
	 */
	public List<PnWikiHistory> findHistoryWithPageId(Integer wikiPageID) {
		List<PnWikiHistory> wikiHistoryList = new ArrayList<PnWikiHistory>();

		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(
					" from PnWikiHistory h where h.wikiPageId = :pageId order by h.editDate asc ");

			query.setInteger("pageId", wikiPageID);
			wikiHistoryList = query.list();
			for (PnWikiHistory pnWikiHistory : wikiHistoryList) {
				initializeEntity(pnWikiHistory.getEditedBy());
			}
		} catch (Exception e) {
			log.error("Error occured in retrieving wiki history contents : " + e.getMessage());
		}
		return wikiHistoryList;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.dao.IPnWikiHistoryDAO#getWikiHistory(java.lang.Integer,
	 *      java.util.Date, java.util.Date)
	 */
	public List<PnWikiHistory> getWikiHistory(Integer projectId,
			Date startDate, Date endDate) {
		List<PnWikiHistory> wikiHistoryList = new ArrayList<PnWikiHistory>();
		String sql = "select wh from PnWikiPage wp, PnWikiHistory wh where wp.ownerObjectId=:projectId and wp.wikiPageId=wh.wikiPageId and wh.editDate between :startDate and :endDate and wp.parentPageName is null";
		try {
			Query query = getHibernateTemplate().getSessionFactory()
					.getCurrentSession().createQuery(sql);
			query.setInteger("projectId", projectId);
			query.setDate("startDate", startDate);
			query.setDate("endDate", endDate);
			wikiHistoryList = query.list();
		} catch (Exception e) {
			log.error("Error occured in retriving wiki history contents :"
					+ e.getMessage());
		}
		return wikiHistoryList;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.dao.IPnWikiHistoryDAO#updateWikiPageIds(Integer newWikiPageId, Integer oldWikiPageId)
	 */
	public void updateWikiPageIds(Integer newWikiPageId, Integer oldWikiPageId) {
		//System.out.println("PnWikiHistory.updateWikiPageIds(Integer "+newWikiPageId+", Integer "+oldWikiPageId+")");
		String sql = "update PnWikiHistory wh set wh.wikiPageId=:newWikiPageId where wh.wikiPageId=:oldWikiPageId ";
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("oldWikiPageId", oldWikiPageId);
			query.setInteger("newWikiPageId", newWikiPageId);
			int rowCount = query.executeUpdate();
			//System.out.println("updated "+rowCount+" rows!");
		} catch (Exception e) {
			log.error("Error occured while updating wiki history wikiPageId values with ID: " + oldWikiPageId + e.getMessage());
			// TODO: Throw exception in order to break transaction in pnWIkiPage service
		}
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.impl.AbstractHibernateAnnotatedDAO#findByPimaryKey(java.io.Serializable)
	 */
	@Override
	public PnWikiHistory findByPimaryKey(Integer key) {
		PnWikiHistory wikiHistory = super.findByPimaryKey(key);
		if(wikiHistory != null) {
			initializeEntity(wikiHistory.getEditedBy());
		}
		return wikiHistory;
	}
	
	
}


