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

import net.project.hibernate.dao.IPnWikiAssignmentDAO;
import net.project.hibernate.model.PnWikiAssignment;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * PnWikiPage database object implementation.
 */
@Transactional 
@Repository 
public class PnWikiAssignmentDAOImpl extends AbstractHibernateAnnotatedDAO<PnWikiAssignment, Integer> implements IPnWikiAssignmentDAO {
	
	private static Logger log = Logger.getLogger(PnWikiAssignmentDAOImpl.class);
	
	public PnWikiAssignmentDAOImpl () {
		super(PnWikiAssignment.class);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnWikiAssignmentDAO#getWikiAssignmentByObjectId(java.lang.Integer)
	 */
	public PnWikiAssignment getWikiAssignmentByObjectId(Integer objectId){
		PnWikiAssignment pnWikiAssignment = null;
		
		StringBuffer sql = new StringBuffer(" from PnWikiAssignment wa where wa.objectId = :objectId ");
		
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql.toString());
			query.setInteger("objectId", objectId);
			pnWikiAssignment = (PnWikiAssignment) query.uniqueResult();
		} catch (Exception e) {
			log.error("Error occured while getting the wiki assignment by object id: " + e.getMessage());
		}
		return pnWikiAssignment;		
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnWikiAssignmentDAO#deleteWikiAssignmentsByPageId(java.lang.Integer)
	 */
	public void deleteWikiAssignmentsByPageId(Integer wikiPageId){
		StringBuffer sql = new StringBuffer(" delete from PnWikiAssignment wa where wa.wikiPageId = :wikiPageId ");
		
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql.toString());
			query.setInteger("wikiPageId", wikiPageId);
			int deleted = query.executeUpdate();
		} catch (Exception e) {
			log.error("Error occured while deleting the wiki assignments by wiki page id: " + e.getMessage());
		}
	}

}