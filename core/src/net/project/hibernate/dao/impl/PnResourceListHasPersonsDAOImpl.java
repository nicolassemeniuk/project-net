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

import net.project.hibernate.dao.IPnResourceListHasPersonsDAO;
import net.project.hibernate.model.PnResourceListHasPersons;
import net.project.hibernate.model.PnResourceListHasPersonsPK;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnResourceListHasPersonsDAOImpl extends AbstractHibernateAnnotatedDAO<PnResourceListHasPersons, PnResourceListHasPersonsPK> 
											 implements IPnResourceListHasPersonsDAO{

	private static Logger log = Logger.getLogger(PnProjectSpaceDAOImpl.class);

	public PnResourceListHasPersonsDAOImpl() {
		super(PnResourceListHasPersons.class);
	}
	
	public void save(Integer resourceListId, String[] personIds) {		
		PnResourceListHasPersons pnResourceListHasPersons;
		PnResourceListHasPersonsPK pnResourceListHasPersonsPK;
		for (int index=0; index < personIds.length; index++){
			pnResourceListHasPersonsPK = new PnResourceListHasPersonsPK(resourceListId, Integer.parseInt(personIds[index]));
			pnResourceListHasPersons = new PnResourceListHasPersons(pnResourceListHasPersonsPK, 0);
			create(pnResourceListHasPersons);
		}
	}

	public PnResourceListHasPersons findByPimaryKey(PnResourceListHasPersonsPK key) {
		// TODO 
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<PnResourceListHasPersons> getResourcesByListId(Integer resourceListId) {
		List<PnResourceListHasPersons> result = new ArrayList<PnResourceListHasPersons>();
		try{
			String sql = "select p from PnResourceListHasPersons p where p.comp_id.resourceListId = :resourceListId";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("resourceListId", resourceListId);
			result = query.list();			
		}catch (Exception e) {
			log.error(e.getMessage());
			//e.printStackTrace();
		}
		if (result.size() > 0){
			return result;
		}
		return result;
	}

	/* 
	 * Method to get count of total resources in a resources list
	 */
	public Integer getResourcesCountByListId(Integer resourceListId) {
		Integer count = 0;		
		try{
			String sql = "select count(p.comp_id.resourceListId) from PnResourceListHasPersons p " 
					   + "where p.comp_id.resourceListId = :resourceListId";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("resourceListId", resourceListId);
			count = (Integer) query.uniqueResult();			
		}catch (Exception e) {
			log.error(e.getMessage());
			//e.printStackTrace();
		}
		return count;
	}
	
}
