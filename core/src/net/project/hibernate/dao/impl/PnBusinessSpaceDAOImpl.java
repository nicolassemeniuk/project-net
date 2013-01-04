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

import net.project.hibernate.dao.IPnBusinessSpaceDAO;
import net.project.hibernate.model.PnBusiness;
import net.project.hibernate.model.PnBusinessSpace;
import net.project.hibernate.model.PnSpaceHasSpace;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by IntelliJ IDEA.
 * User: Oleg
 * Date: 14.05.2007
 * Time: 21:25:26
 * To change this template use File | Settings | File Templates.
 */
@Transactional 
@Repository 
public class PnBusinessSpaceDAOImpl extends AbstractHibernateAnnotatedDAO<PnBusinessSpace, Integer> implements IPnBusinessSpaceDAO {
	private static Logger log = Logger.getLogger(PnBusinessSpaceDAOImpl.class);
	
	public PnBusinessSpaceDAOImpl() {
        super(PnBusinessSpace.class);
    }
    
	@SuppressWarnings("unchecked")
    public PnBusiness getBusinessByProjectId(Integer projectId){
    	PnBusiness pnBusiness = null ;
    	String sql = "SELECT new PnBusiness(b.businessId, b.businessName) FROM PnBusiness b, PnSpaceHasSpace shs "
    		+" WHERE  b.recordStatus = 'A' AND b.businessId = shs.comp_id.parentSpaceId" 
    		+" AND shs.comp_id.childSpaceId = :projectId";
    	try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("projectId", projectId);
			List<PnBusiness> result = query.list();
			if (result.size() > 0) {
				pnBusiness = result.get(0);
			}
		} catch (Exception e) {
			log.error("Error occured while getting business "+e.getMessage());	
		}
    	return pnBusiness;
    }
	
	/**
	 * 
	 */
	public PnBusinessSpace getBusinessSpaceById(Integer id){
		PnBusinessSpace pnBusinessSpace = null;
		
		try {
			String sql = " FROM PnBusinessSpace shs WHERE shs.businessSpaceId = :id";
			
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("id", id);
			
			List<PnBusinessSpace> result = query.list();
			
			if (result.size() > 0) {
				pnBusinessSpace = result.get(0);
			}
			
		} catch (Exception e) {
			log.error("Error occured while getting business "+e.getMessage());
			e.printStackTrace();
		}
    	return pnBusinessSpace;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnBusinessSpaceDAO#isRootBusines(java.lang.Integer)
	 */
	public boolean isRootBusines(Integer spaceId) {
		List<PnSpaceHasSpace> result = null;
		try {
			String sql = " FROM PnSpaceHasSpace shs "
					   + " WHERE shs.comp_id.childSpaceId = :childSpaceId"
					   + " AND shs.parentSpaceType = :parentSpaceType";
			
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("childSpaceId", spaceId);
			query.setString("parentSpaceType", "business");
			result = query.list();
			
		} catch (Exception e) {
			log.error("Error occured while getting business "+e.getMessage());
			e.printStackTrace();
		}
		return result.isEmpty();
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnBusinessSpaceDAO#getParentBusinessByBusinessId(java.lang.Integer)
	 */
	public PnBusiness getParentBusinessByBusinessId(Integer businessId) {
		PnBusiness pnBusiness = null;
		
		try {
			String sql = "SELECT new PnBusiness(b.businessId, b.businessName) FROM PnBusiness b, PnSpaceHasSpace shs "
	    		+" WHERE  b.recordStatus = 'A' AND b.businessId = shs.comp_id.parentSpaceId" 
	    		+" AND shs.comp_id.childSpaceId = :businessId";
			
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("businessId", businessId);
			
			List<PnBusiness> result = query.list();
			
			if (result.size() > 0) {
				pnBusiness = result.get(0);
			}
			
		} catch (Exception e) {
			log.error("Error occured while getting parent business "+e.getMessage());
		}
		return pnBusiness;
	}
}
