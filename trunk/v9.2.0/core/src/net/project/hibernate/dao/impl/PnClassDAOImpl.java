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

import net.project.hibernate.dao.IPnClassDAO;
import net.project.hibernate.model.PnClass;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnClassDAOImpl extends AbstractHibernateAnnotatedDAO<PnClass, Integer> implements IPnClassDAO {
	
	private static Logger log = Logger.getLogger(PnClassDAOImpl.class);
	
	public PnClassDAOImpl() {
		super(PnClass.class);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnClassDAO#getFormNamesForSpace(java.lang.String)
	 */
	public List<PnClass> getFormNamesForSpace(String spaceId) {
		List<PnClass> formNames = new ArrayList<PnClass>();
		try {
			String sql = " SELECT c.className, c.classId"
					+ " FROM PnSpaceHasFeaturedMenuitem sfm, PnSpaceHasClass shc, PnClass c "
					+ " WHERE sfm.comp_id.spaceId = :spaceId "
					+ " AND sfm.comp_id.objectId = shc.comp_id.classId "
					+ " AND sfm.comp_id.spaceId = shc.comp_id.spaceId "
					+ " AND shc.comp_id.classId = c.classId "
					+ " AND c.recordStatus = 'A'";

			Query query = getHibernateTemplate().getSessionFactory()
					.getCurrentSession().createQuery(sql);
			query.setString("spaceId", spaceId);
			Iterator results = query.list().iterator();
			while (results.hasNext()) {
				Object[] row = (Object[]) results.next();
				PnClass c = new PnClass();
				c.setClassName((String)row[0]);
				c.setClassId(new Integer((Integer)row[1]));
				formNames.add(c);
			}
		} catch (Exception e) {
			log.error("Error occured while getting form names by space ID: "
					+ e.getMessage());
		}
		return formNames;
	}
	
	/**
	 * Get Form name and recordStatus by form id
	 * @param classId
	 * @return PnClass
	 */
	public PnClass getFormWithRecordStatus(Integer classId) {
		PnClass pnClass = null;
		
		String sql = " SELECT new PnClass(c.classId, c.className, c.recordStatus) " +
					 " FROM PnClass c WHERE c.classId = :classId ";
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("classId", classId);
			pnClass = (PnClass) query.uniqueResult();			
		} catch(Exception e){
			log.error("Error occurred while getting form details by form id : "+e.getMessage());
		}
		return pnClass;
	}
}
