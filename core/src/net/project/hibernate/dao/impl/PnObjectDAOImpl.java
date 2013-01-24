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

import java.math.BigDecimal;
import java.util.List;

import net.project.hibernate.dao.IPnObjectDAO;
import net.project.hibernate.model.PnObject;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
@Transactional
@Repository
public class PnObjectDAOImpl extends AbstractHibernateAnnotatedDAO<PnObject, Integer> implements IPnObjectDAO {
	
	private Logger log = Logger.getLogger(PnObjectDAOImpl.class);

	public PnObjectDAOImpl() {
		super(PnObject.class);
	}

	public Integer generateNewId() {
		Integer objectId = null;
		try {
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			List list = session.createSQLQuery(" SELECT PN_OBJECT_SEQUENCE.NEXTVAL FROM DUAL ").list();
			if (list.size() > 0) {
				BigDecimal sequenceValue = (BigDecimal) list.get(0);
				objectId = new Integer(sequenceValue.toString());
			}
			if (log.isDebugEnabled()){
				log.debug("Generated new object id:"+objectId);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return objectId;
	}

	public void insertObject(PnObject object) {
		getHibernateTemplate().save(object);
	}

	public Integer create(PnObject pnObject) {
		Integer objectId = new Integer(0);
		try {
			objectId = generateNewId();
			pnObject.setObjectId(objectId);
			getHibernateTemplate().save(pnObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objectId;
	}
	
	@SuppressWarnings("unchecked")
	public PnObject getObjectByObjectId(Integer objectId) {
		PnObject object = null;
		try {
			String sql = " SELECT ot FROM PnObject ot WHERE ot.objectId = :objectId";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("objectId", objectId);
			List<PnObject> result = query.list();
			if (result.size() > 0) {
				object = result.get(0);
			}
		} catch (Exception e) {
			log.error("Error occured while getting object by object id : " + e.getMessage());
		}
		return object;
	}

}
