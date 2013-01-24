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
import java.util.Iterator;
import java.util.List;

import net.project.hibernate.dao.IPnProjectSpaceMetaValueDAO;
import net.project.hibernate.model.PnProjectSpaceMetaCombo;
import net.project.hibernate.model.PnProjectSpaceMetaProp;
import net.project.hibernate.model.PnProjectSpaceMetaValue;
import net.project.hibernate.model.PnProjectSpaceMetaValuePK;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class PnProjectSpaceMetaValueDAOImpl extends AbstractHibernateAnnotatedDAO<PnProjectSpaceMetaValue, PnProjectSpaceMetaValuePK> implements IPnProjectSpaceMetaValueDAO {
	public PnProjectSpaceMetaValueDAOImpl() {
		super(PnProjectSpaceMetaValue.class);
	}

	@SuppressWarnings("unchecked")
	public List<PnProjectSpaceMetaValue> getMetaValuesByProjectId(final Integer projectId) {
		try {
			String hql = " select p.comp_id.projectId, p.comp_id.propertyId, p.propertyValue, " +
					" p.pnProjectSpaceMetaProp.propertyId, p.pnProjectSpaceMetaProp.propertyName, p.pnProjectSpaceMetaProp.propertyType " +
					" from PnProjectSpaceMetaValue p where p.comp_id.projectId=:projectId ";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(hql);
			query.setInteger("projectId", projectId);
			List metaValues = new ArrayList();
			Iterator result = query.list().iterator();
			while(result.hasNext()){
				Object[] row = (Object[]) result.next();
				PnProjectSpaceMetaValue metaValue = new PnProjectSpaceMetaValue();
				metaValue.setComp_id(new PnProjectSpaceMetaValuePK((Integer) row[0], (Integer) row[1]));
				metaValue.setPropertyValue((String) row[2]);
				PnProjectSpaceMetaProp metaProp = new PnProjectSpaceMetaProp();
				metaProp.setPropertyId((Integer) row[3]);
				metaProp.setPropertyName((String) row[4]);
				metaProp.setPropertyType((Integer) row[5]);
				metaValue.setPnProjectSpaceMetaProp(metaProp);
                metaValues.add(metaValue);
			}
			return metaValues;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public PnProjectSpaceMetaValue getMetaValueByProjectAndPropertyId(Integer projectId, Integer propertyId) {
		PnProjectSpaceMetaValue pnProjectSpaceMetaValue = null;
		String hql = "select new PnProjectSpaceMetaValue(p.comp_id.projectId, p.comp_id.propertyId, p.propertyValue) from PnProjectSpaceMetaValue p where p.comp_id.projectId=:projectId and p.comp_id.propertyId=:propertyId";
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(hql);
			query.setInteger("projectId", projectId);
			query.setInteger("propertyId", propertyId);
			pnProjectSpaceMetaValue = (PnProjectSpaceMetaValue) query.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pnProjectSpaceMetaValue;
	}

	@SuppressWarnings("unchecked")
	public List<PnProjectSpaceMetaCombo> getValuesOptionListForProperty(final Integer propertyId) {
		try {
			String hql = "from PnProjectSpaceMetaCombo where comp_id.propertyId=:propertyId";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(hql);
			List metaValues = query.setInteger("propertyId", propertyId).list();
			return metaValues;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void createOrUpdate(PnProjectSpaceMetaValue object) {
	}
}
