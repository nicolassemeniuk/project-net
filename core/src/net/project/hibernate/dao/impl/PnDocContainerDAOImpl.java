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

import net.project.hibernate.dao.IPnDocContainerDAO;
import net.project.hibernate.model.PnDocContainer;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnDocContainerDAOImpl extends AbstractHibernateAnnotatedDAO<PnDocContainer,Integer> implements IPnDocContainerDAO {

	public PnDocContainerDAOImpl() {
		super(PnDocContainer.class);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnDocContainerDAO#getDocContainerWithRecordStatus(java.lang.Integer)
	 */
	public PnDocContainer getDocContainerWithRecordStatus(Integer docContainerId) {
		PnDocContainer pnDocContainer = null;

		String sql = "SELECT new PnDocContainer(dc.docContainerId, dc.containerName, dc.recordStatus) "
				+ " FROM PnDocContainer dc WHERE dc.docContainerId = :docContainerId ";
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("docContainerId", docContainerId);
			pnDocContainer = (PnDocContainer) query.uniqueResult();
		} catch (Exception e) {
			Logger.getLogger(PnDocContainer.class).error(
					"Error occurred while getting doc container details by document id :" + e.getMessage());
		}
		return pnDocContainer;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnDocContainerDAO#getDocContainerWithIsHidden(java.lang.Integer)
	 */
	public PnDocContainer getDocContainerWithIsHidden(Integer documentId) {
		PnDocContainer pnDocContainer = null;

		String sql = "SELECT new PnDocContainer(dc.docContainerId, dc.isHidden) "
				+ " FROM PnDocContainer dc, PnDocContainerHasObject dho "
				+ " WHERE dc.docContainerId = dho.comp_id.docContainerId and dho.comp_id.objectId = :documentId ";
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("documentId", documentId);
			pnDocContainer = (PnDocContainer) query.uniqueResult();
		} catch (Exception e) {
			Logger.getLogger(PnDocContainer.class).error(
					"Error occurred while getting doc container details by document id :" + e.getMessage());
		}
		return pnDocContainer;
	}
}
