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
import java.util.List;

import net.project.hibernate.dao.IPnSpaceHasPersonDAO;
import net.project.hibernate.model.PnSpaceHasPerson;
import net.project.hibernate.model.PnSpaceHasPersonPK;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnSpaceHasPersonDAOImpl extends AbstractHibernateAnnotatedDAO<PnSpaceHasPerson, PnSpaceHasPersonPK> implements IPnSpaceHasPersonDAO {

	public PnSpaceHasPersonDAOImpl() {
		super(PnSpaceHasPerson.class);
	}

	@SuppressWarnings("unchecked")
	public PnSpaceHasPerson getPnSpaceHasPersonBySecureKey(final String secureKey) {
		try {
			List spacePersons = new ArrayList();
			String hql = "from PnSpaceHasPerson where secureKey=:secureKey";
			spacePersons = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(hql).setString("secureKey", secureKey).list();
			if (spacePersons.size() > 0) {
				return (PnSpaceHasPerson) spacePersons.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public void saveOrUpdateSpaceHasPerson(PnSpaceHasPerson spaceHasPerson) {
		try {
			getHibernateTemplate().getSessionFactory().getCurrentSession().saveOrUpdate(spaceHasPerson);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<PnSpaceHasPerson> getSpaceHasPersonByProjectandPerson(Integer spaceIds[], Integer personId) {
		List<PnSpaceHasPerson> objectList = null;
		String hql = " FROM PnSpaceHasPerson shp WHERE shp.comp_id.spaceId IN (:spaceIds) "
				+ " AND shp.comp_id.personId = :personId ";
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(hql);
			query.setParameterList("spaceIds", spaceIds);
			query.setString("personId", personId.toString());
			objectList = query.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objectList;
	}

	public boolean doesPersonExistsInSpace(Integer personId, Integer spaceId) {
		boolean exists = false;
		try {
			String sql = "select count(*) from PnSpaceHasPerson shp where shp.comp_id.spaceId = :spaceId and shp.comp_id.personId = :personId ";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("spaceId", spaceId).setInteger("personId", personId);
			Integer numberOfPersons = ((Integer) query.iterate().next()).intValue();
			exists = numberOfPersons == 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return exists;
	}
}