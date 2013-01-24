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

import net.project.hibernate.dao.IPnModuleDAO;
import net.project.hibernate.model.PnModule;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnModuleDAOImpl extends AbstractHibernateAnnotatedDAO<PnModule, Integer> implements IPnModuleDAO {
    
    	private final static Logger LOG = Logger.getLogger(PnModuleDAOImpl.class);

	public PnModuleDAOImpl() {
		super(PnModule.class);
	}

	@SuppressWarnings("unchecked")
	public List<PnModule> getModuleDefaultPermissions(final Integer spaceId) {
		List<PnModule> pnModuleList = new ArrayList();
		try {
			String sql = " select new net.project.hibernate.model.PnModule(m.moduleId, m.defaultPermissionActions) from PnModule m, PnSpaceHasModule shm where "
					+ " shm.comp_id.spaceId = :spaceId and m.moduleId = shm.comp_id.moduleId and shm.isActive = 1";
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			pnModuleList = session.createQuery(sql).setInteger("spaceId", spaceId).list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pnModuleList;
	}

	@SuppressWarnings("unchecked")
	public List<PnModule> getModuleIds() {
		List<PnModule> pnModuleList = new ArrayList();
		try {
			pnModuleList = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery("select new PnModule(p.moduleId) from PnModule p").list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pnModuleList;
	}
	
	@SuppressWarnings("unchecked")
	public List<PnModule> getModulesForSpace(final Integer spaceId) {
	    	if (LOG.isDebugEnabled()) {
		    LOG.debug("ENTRY OK: getModulesForSpace");
		}
	    	
	    	List<PnModule> result = new ArrayList();
	    	
	    	try {
		    String sql = "select m " +
 		    		 "from PnModule m, PnSpaceHasModule shm " +
 		    		 "where shm.comp_id.spaceId = :spaceId " +
 		    		 "	and m.moduleId = shm.comp_id.moduleId " +
 		    		 "	and shm.isActive = 1";
		    Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
		    result = query.setInteger("spaceId", spaceId).list();
		} catch (Exception e) {
		    if (LOG.isDebugEnabled()) {
			LOG.debug("EXIT FAIL: getModulesForSpace");
		    }
		    e.printStackTrace();
		}
		
		if (LOG.isDebugEnabled()) {
		    LOG.debug("EXIT OK: getModulesForSpace");
		}
		return result;
	}

}
