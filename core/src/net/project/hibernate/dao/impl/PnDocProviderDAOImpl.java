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

import net.project.hibernate.dao.IPnDocProviderDAO;
import net.project.hibernate.model.PnDocProvider;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnDocProviderDAOImpl extends AbstractHibernateAnnotatedDAO<PnDocProvider, Integer> implements IPnDocProviderDAO {

	public PnDocProviderDAOImpl() {
		super(PnDocProvider.class);
	}

	@SuppressWarnings("unchecked")
	public List<PnDocProvider> getDocProviderIds() {
		List<PnDocProvider> pnDocProviderList = new ArrayList();
		Iterator pnDocProviderIterator = null;
		try {
			pnDocProviderIterator = (Iterator) getHibernateTemplate().execute(new HibernateCallback() {
				public Iterator doInHibernate(Session session) throws HibernateException {
					Query query = null;
					Iterator it = null;
					try {
						String sql = " SELECT docProviderId FROM PnDocProvider WHERE isDefault = 1 ";
						query = session.createQuery(sql);
						it = query.list().iterator();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return it;
				}
			});
			while (pnDocProviderIterator.hasNext()) {
				PnDocProvider pnDocProvider = new PnDocProvider(((Integer) pnDocProviderIterator.next()).intValue());
				pnDocProviderList.add(pnDocProvider);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pnDocProviderList;
	}

}
