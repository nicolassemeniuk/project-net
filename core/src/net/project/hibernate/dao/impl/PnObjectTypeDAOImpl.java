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

import java.util.List;

import net.project.hibernate.dao.IPnObjectTypeDAO;
import net.project.hibernate.model.PnObjectType;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnObjectTypeDAOImpl extends AbstractHibernateAnnotatedDAO<PnObjectType, String> implements IPnObjectTypeDAO {
	
	private static Logger log = Logger.getLogger(PnObjectTypeDAOImpl.class);

	public PnObjectTypeDAOImpl() {
		super(PnObjectType.class);
	}

	@SuppressWarnings("unchecked")
	public List<PnObjectType> findObjectTypes() {
		List<PnObjectType> pnObjectTypeList = null;
		try {
			pnObjectTypeList = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(" select new PnObjectType(p.objectType, p.defaultPermissionActions) from PnObjectType p").list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pnObjectTypeList;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnObjectTypeDAO#getObjectTypeByObjectId(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public PnObjectType getObjectTypeByObjectId(Integer objectId) {
		PnObjectType objectType = null;
		try {
			String sql = " SELECT ot FROM PnObjectType ot, PnObject o WHERE ot.objectType = o.pnObjectType.objectType AND o.objectId = :objectId";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("objectId", objectId);
			List<PnObjectType> result = query.list();
			if (result.size() > 0) {
				objectType = result.get(0);
			}
		} catch (Exception e) {
			log.error("Error occured while getting object type by object id : " + e.getMessage());
		}
		return objectType;
	}

}
