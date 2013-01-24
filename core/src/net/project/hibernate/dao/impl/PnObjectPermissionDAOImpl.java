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

import net.project.hibernate.dao.IPnObjectPermissionDAO;
import net.project.hibernate.model.PnObjectPermission;
import net.project.hibernate.model.PnObjectPermissionPK;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnObjectPermissionDAOImpl extends
	AbstractHibernateAnnotatedDAO<PnObjectPermission, PnObjectPermissionPK>
	implements IPnObjectPermissionDAO {

    private final static Logger LOG = Logger.getLogger(PnObjectPermissionDAOImpl.class);
    
    public PnObjectPermissionDAOImpl() {
	super(PnObjectPermission.class);
    }

    @SuppressWarnings("unchecked")
    public List<PnObjectPermission> getObjectPermissionsForGroup(Integer groupId) {
	
	if (LOG.isDebugEnabled()) {
	    LOG.debug("ENTRY OK: getObjectPermissionsByGroup");
	}
	
	List<PnObjectPermission> result = new ArrayList();
	try {
	    String sql = "from PnObjectPermission where comp_id.groupId = :adminGroupId ";
	    Query query = getHibernateTemplate().getSessionFactory()
		    .getCurrentSession().createQuery(sql);
	    result = query.setInteger("adminGroupId", groupId).list();
	} catch (Exception e) {
	    if (LOG.isDebugEnabled()) {
		LOG.debug("EXIT FAIL: getObjectPermissionsByGroup");
	    }
	    e.printStackTrace();
	}
	
	if (LOG.isDebugEnabled()) {
	    LOG.debug("EXIT OK: getObjectPermissionsByGroup");
	}
	
	return result;
	
    }
    
    
    @SuppressWarnings("unchecked")
    public List<PnObjectPermission> getObjectPermissionsForObject(Integer objectId) {
	
	if (LOG.isDebugEnabled()) {
	    LOG.debug("ENTRY OK: getObjectPermissionsByObject");
	}
	
	List<PnObjectPermission> result = new ArrayList();
	try {
	    String sql = "from PnObjectPermission where comp_id.objectId = :the_parent ";
	    Query query = getHibernateTemplate().getSessionFactory()
		    .getCurrentSession().createQuery(sql);
	    result = query.setInteger("the_parent", objectId).list();
	} catch (Exception e) {
	    if (LOG.isDebugEnabled()) {
		LOG.debug("EXIT FAIL: getObjectPermissionsByObject");
	    }
	    e.printStackTrace();
	}
	
	if (LOG.isDebugEnabled()) {
	    LOG.debug("EXIT OK: getObjectPermissionsByObject");
	}
	
	return result;
	
    }
}
