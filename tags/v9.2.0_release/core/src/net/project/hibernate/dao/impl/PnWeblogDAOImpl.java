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

import net.project.hibernate.model.PnWeblog;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Transactional 
@Repository 
public class PnWeblogDAOImpl extends AbstractHibernateAnnotatedDAO<PnWeblog, Integer> implements net.project.hibernate.dao.IPnWeblogDAO {
	
	private static Logger log = Logger.getLogger(PnWeblogDAOImpl.class);
	
	public PnWeblogDAOImpl () {
		super(PnWeblog.class);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnWeblogDAO#getByUserId(java.lang.Integer)
	 */
	public PnWeblog getByUserId(Integer userId) {
		PnWeblog pnWeblog = null;
		
		String sql = "FROM PnWeblog w WHERE w.pnPerson.personId = :userId";
		
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("userId", userId);
			pnWeblog = (PnWeblog) query.uniqueResult();			
		}catch (Exception e) {
			log.error("Error occured while getting the weblog by user id : "+e.getMessage());
		}		
		return pnWeblog;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnWeblogDAO#getByUserAndSpaceId(java.lang.Integer, java.lang.Integer)
	 */
	public PnWeblog getByUserAndSpaceId(Integer userId, Integer spaceId) {
		PnWeblog pnWeblog = null;		
		String sql = "FROM PnWeblog w WHERE w.pnPerson.personId = :userId AND w.spaceId = :spaceId";		
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("userId", userId);
			query.setInteger("spaceId", spaceId);
			query.setMaxResults(1);
			pnWeblog = (PnWeblog) query.uniqueResult();
			if (pnWeblog != null) {
				initializeEntity(pnWeblog.getPnPerson());
			}
		}catch (Exception e) {
			log.error("Error occured while getting the weblog by user and space id : "+e.getMessage());
		}		
		return pnWeblog;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnWeblogDAO#getBySpaceId(java.lang.Integer)
	 */
	public PnWeblog getBySpaceId(Integer spaceId, boolean initializePersonObject) {
		PnWeblog pnWeblog = null;		
		String sql = " FROM PnWeblog w WHERE w.spaceId = :spaceId ORDER BY w.createdDate ASC ";		
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("spaceId", spaceId);
			query.setMaxResults(1);
			pnWeblog = (PnWeblog) query.uniqueResult();
			
			if(initializePersonObject && pnWeblog != null) {
				initializeEntity(pnWeblog.getPnPerson());
			}
		}catch (Exception e) {
			log.error("Error occured while getting the weblog by space id : "+e.getMessage());
		}		
		return pnWeblog;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnWeblogDAO#getBySpaceId(java.lang.Integer)
	 */
	public PnWeblog getBySpaceId(Integer spaceId) {		
		return getBySpaceId(spaceId, true);
	}
	
	/**
	 * Get PnWebLog by id
	 * @param weblogId
	 * @return
	 */
	public PnWeblog getPnWeblogById(int weblogId){
		PnWeblog pnWeblog = null;
		try{
			String sql = " FROM PnWeblog w WHERE w.weblogId = :weblogId ";
			
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("weblogId", weblogId);
			query.setMaxResults(1);
			
			pnWeblog = (PnWeblog) query.uniqueResult();		
			
		}catch (Exception e) {
			log.error("Error occured while getting the weblog by weblog id : "+e.getMessage());
		}		
		return pnWeblog;
	}
}
