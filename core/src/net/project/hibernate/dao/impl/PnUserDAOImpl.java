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
package net.project.hibernate.dao.impl;

import java.util.List;

import net.project.hibernate.dao.IPnUserDAO;
import net.project.hibernate.model.PnUser;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
@Transactional 
@Repository 
public class PnUserDAOImpl extends AbstractHibernateAnnotatedDAO<PnUser, Integer> implements IPnUserDAO {

	private static Logger log = Logger.getLogger(PnUserDAOImpl.class);

	public PnUserDAOImpl() {
		super(PnUser.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.dao.IPnUserDAO#isOnline(java.lang.Integer)
	 */
	public Boolean isOnline(Integer userId) {
		Integer isOnline = 0;
		String sql = "SELECT u.isLogin FROM PnUser u WHERE u.userId = :userId";
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("userId", userId);
			isOnline = (Integer) query.uniqueResult();
		} catch (Exception e) {
			log.error("Error occured while getting the online status of user : " + e.getMessage());
		}
		return isOnline == 1 ? true : false;
	}
	
	/**
	 * returns number of all active users in the system 
	 * @return number of users
	 */
	public Integer getUsersCount(){
		Integer numberOfUsers = 0;
		try {
			String sql = "SELECT count(*) as users FROM PnUser u WHERE u.recordStatus='A' ";
			List result = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql).list();
			if (result != null && result.size() > 0){
				numberOfUsers = Integer.valueOf(result.get(0).toString());
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return numberOfUsers;
	}

}
