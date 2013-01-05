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

import net.project.hibernate.dao.IPnDefaultObjectPermissionDAO;
import net.project.hibernate.model.PnDefaultObjectPermission;
import net.project.hibernate.model.PnDefaultObjectPermissionPK;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnDefaultObjectPermissionDAOImpl extends AbstractHibernateAnnotatedDAO<PnDefaultObjectPermission, PnDefaultObjectPermissionPK> implements IPnDefaultObjectPermissionDAO {
    
    	private final static Logger LOG = Logger.getLogger(PnDefaultObjectPermissionDAOImpl.class);

	public PnDefaultObjectPermissionDAOImpl() {
		super(PnDefaultObjectPermission.class);
	}

	
	@SuppressWarnings("unchecked")
	public List<PnDefaultObjectPermission> getDefaultObjectPermisionsBySpaceAndObjectTypeForNonPrincipalGroup(final Integer spaceId, final String objectType) {
	    
	    	if (LOG.isDebugEnabled()) {
		    LOG.debug("ENTRY OK: getDefaultObjectPermisionsBySpaceAndObjectTypeForNonPrincipalGroup");
		}
	    	
		List<PnDefaultObjectPermission> result = new ArrayList();
		try {
			String sql = " select dop " +
					" from PnDefaultObjectPermission dop, PnGroup g " +
					" where dop.comp_id.spaceId = :spaceId and dop.comp_id.objectType = :objectType " +
					" and  g.groupId = dop.comp_id.groupId and  g.isPrincipal <> 1";
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			result = session.createQuery(sql).setInteger("spaceId", spaceId).setString("objectType", objectType).list();
		} catch (Exception e) {
		    	if (LOG.isDebugEnabled()) {
			    LOG.debug("EXIT FAIL: getDefaultObjectPermisionsBySpaceAndObjectTypeForNonPrincipalGroup");
			}
			e.printStackTrace();
		}
		
		if (LOG.isDebugEnabled()) {
		    LOG.debug("EXIT OK: getDefaultObjectPermisionsBySpaceAndObjectTypeForNonPrincipalGroup");
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<PnDefaultObjectPermission> getDefaultObjectPermissionsBySpaceAndObjectTypeForSystemNonPrincipalGroup(final Integer spaceId, final String objectType) {
	    
	    if (LOG.isDebugEnabled()) {
		LOG.debug("ENTRY OK: getDefaultObjectPermissionsBySpaceAndObjectTypeForSystemNonPrincipalGroup");
	    }
	    
		List<PnDefaultObjectPermission> result = new ArrayList();
		try {
			String sql = " select dop " +
					" from PnDefaultObjectPermission dop, PnGroup g " +
					" where dop.comp_id.spaceId = :spaceId and dop.comp_id.objectType = :objectType " +
					" and  g.groupId = dop.comp_id.groupId and " +
					" g.is_system_group = 1 and g.isPrincipal <> 1";
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			result = session.createQuery(sql).setInteger("spaceId", spaceId).setString("objectType", objectType).list();
		} catch (Exception e) {
		    	if (LOG.isDebugEnabled()) {
			    LOG.debug("EXIT FAIL: getDefaultObjectPermissionsBySpaceAndObjectTypeForSystemNonPrincipalGroup");
			}
			e.printStackTrace();
		}
	    
	    if (LOG.isDebugEnabled()) {
		LOG.debug("EXIT OK: getDefaultObjectPermissionsBySpaceAndObjectTypeForSystemNonPrincipalGroup");
	    }
	    
	    return result;
	}
}
