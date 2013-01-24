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

import net.project.hibernate.dao.IPnPersonPropertyDAO;
import net.project.hibernate.model.PnPersonProperty;
import net.project.hibernate.model.PnPersonPropertyPK;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnPersonPropertyDAOImpl extends AbstractHibernateAnnotatedDAO<PnPersonPropertyPK, Integer> implements IPnPersonPropertyDAO  {
	
	private static Logger log  = Logger.getLogger(PnPersonPropertyDAOImpl.class);
	
	public PnPersonPropertyDAOImpl() {
		super(PnPersonProperty.class);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnPersonPropertyDAO#getPersonPropertyBySpaceId(java.lang.Integer, java.lang.Integer, java.lang.String)
	 */
	public List<PnPersonProperty> getPersonProperties(Integer spaceId, Integer personId, String property) {
		List<PnPersonProperty> result = new ArrayList<PnPersonProperty>();
		try {
			String sql = " SELECT distinct new PnPersonProperty(p.comp_id) "
					+ " FROM PnPersonProperty p WHERE p.comp_id.personId = :personId "
					+ " AND p.comp_id.spaceId = :spaceId AND p.comp_id.property = :property ";

			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setParameter("spaceId", spaceId);
			query.setParameter("personId", personId);
			query.setParameter("property", property);
			result = query.list();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return result;
	}
	
}
