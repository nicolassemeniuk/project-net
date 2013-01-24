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

import net.project.hibernate.dao.IPnModulePermissionDAO;
import net.project.hibernate.model.PnModulePermission;
import net.project.hibernate.model.PnModulePermissionPK;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnModulePermissionDAOImpl extends AbstractHibernateAnnotatedDAO<PnModulePermission, PnModulePermissionPK> implements IPnModulePermissionDAO {

    	private final static Logger LOG = Logger.getLogger(PnModulePermissionDAOImpl.class);
    	
	public PnModulePermissionDAOImpl() {
		super(PnModulePermission.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<PnModulePermission> getModulePermissionsBySpaceAndModule(Integer spaceId, Integer moduleId) {
	    if (LOG.isDebugEnabled()) {
		LOG.debug("EXIT OK: updateActionsBySpaceAndModuleId");
	    }
	    
	    List<PnModulePermission> result = new ArrayList();
	    try {
		 String sql = "from PnModulePermission where comp_id.space_id = :spaceId and comp_id.module_id = :moduleId";
		 Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
		 result = query.setInteger("spaceId", spaceId).setInteger("moduleId", moduleId).list();
	    } catch (Exception e) {
		if (LOG.isDebugEnabled()) {
		    LOG.debug("EXIT FAIL: updateActionsBySpaceAndModuleId");
		}
		e.printStackTrace();
	    }
	    
	    if (LOG.isDebugEnabled()) {
		LOG.debug("EXIT OK: updateActionsBySpaceAndModuleId");
	    }
	    return result;
	}

}
