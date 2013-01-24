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

import net.project.hibernate.model.PnWeblogEntryAttribute;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Transactional 
@Repository 
public class PnWeblogEntryAttributeDAOImpl extends AbstractHibernateAnnotatedDAO<PnWeblogEntryAttribute, Integer> implements net.project.hibernate.dao.IPnWeblogEntryAttributeDAO {

	private static Logger log = Logger.getLogger(PnWeblogEntryAttributeDAOImpl.class);
	
	public PnWeblogEntryAttributeDAOImpl() {
		super(PnWeblogEntryAttribute.class);
	}

	public List getTaskIdsFromTaskBlogEntries(Integer spaceId){
		List<PnWeblogEntryAttribute> result = new ArrayList<PnWeblogEntryAttribute>();
		String sql ="SELECT DISTINCT wea.value " +
					"FROM PnWeblogEntryAttribute wea " +
					"WHERE wea.name = 'taskId' " +
					"AND wea.value IN ( " +
					"    SELECT t.taskId  " +
					"    FROM PnTask t, PnPlan pp, PnPlanHasTask pht, PnSpaceHasPlan shp, PnProjectSpace ps " +
					"    WHERE t.taskId = pht.comp_id.taskId AND pht.comp_id.planId = pp.planId " +
					"    AND pp.planId = shp.comp_id.planId AND shp.comp_id.spaceId = ps.projectId " +
					"    AND ps.projectId = :spaceId )";
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("spaceId", spaceId);
			Iterator results = query.list().iterator();
			while(results.hasNext()){
				Object row = (Object) results.next();
				PnWeblogEntryAttribute entryAttribute = new PnWeblogEntryAttribute();
				entryAttribute.setValue((String) row);
				result.add(entryAttribute);
			}			
		}catch (Exception e) {
			log.error(e.getMessage());
		}		
		return result;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnWeblogEntryAttributeDAO#getWeblogEntryAtribute(java.lang.Integer, java.lang.String)
	 */
	public PnWeblogEntryAttribute getWeblogEntryAtribute(Integer weblogEntryId, String name) {
		PnWeblogEntryAttribute entryAttribute = null;
		String sql = " FROM PnWeblogEntryAttribute wea WHERE wea.pnWeblogEntry.weblogEntryId = :weblogEntryId " +
	     			 " AND wea.name = :name ";
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("weblogEntryId", weblogEntryId);
			query.setString("name", name);
			entryAttribute = (PnWeblogEntryAttribute) query.uniqueResult();	
		}catch (Exception e) {
			log.error(e.getMessage());
		}		
		return entryAttribute;
	}
	
	public List<PnWeblogEntryAttribute> getWeblogEntryAtributesByEntryId(Integer weblogEntryId) {
		List<PnWeblogEntryAttribute> result = new ArrayList<PnWeblogEntryAttribute>();
		String sql = " FROM PnWeblogEntryAttribute wea WHERE wea.pnWeblogEntry.weblogEntryId = :weblogEntryId ";
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("weblogEntryId", weblogEntryId);
			result = query.list();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return result;
	}
	
}