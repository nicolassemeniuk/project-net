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

import net.project.hibernate.dao.IPnGroupDAO;
import net.project.hibernate.model.PnGroup;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnGroupDAOImpl extends AbstractHibernateAnnotatedDAO<PnGroup, Integer> implements IPnGroupDAO {
    
    	private final static Logger LOG = Logger.getLogger(PnGroupDAOImpl.class);

	public PnGroupDAOImpl() {
		super(PnGroup.class);
	}

	@SuppressWarnings("unchecked")
	public List<PnGroup> getGroupForSpaceAndGroupType(Integer spaceId, Integer groupTypeId) {
	    	if (LOG.isDebugEnabled()) {
		    LOG.debug("ENTRY OK: getGroupForSpaceAndGroupType");
		}
		List<PnGroup> result = new ArrayList();
		try {
			String sql = "select g " +
					" from PnSpaceHasGroup shg, PnGroup g " +
					" where shg.spaceId = :spaceId and g.groupTypeId = :groupTypeId " +
					"	and shg.groupId = g.groupId ";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			result = query.setInteger("spaceId", spaceId).setInteger("groupTypeId", groupTypeId).list();
		} catch (Exception e) {
		    	if (LOG.isDebugEnabled()) {
			    LOG.debug("EXIT FAIL: getGroupForSpaceAndGroupType");
			}
			e.printStackTrace();
		}
		if (LOG.isDebugEnabled()) {
		    LOG.debug("EXIT OK: getGroupForSpaceAndGroupType");
		}
		return result;
	}
	
	public PnGroup getPrincipalGroupForSpaceAndPerson(final Integer spaceId, final Integer personId) {
	    	if (LOG.isDebugEnabled()) {
		    LOG.debug("ENTRY OK: getPersonPrincipalGroupForSpace");
		}
	    	PnGroup group = null;
	    	try {
			String sql= " select " +
		        "    g " +
		        " from " +
		        "    PnSpaceHasGroup as shg, PnGroup as g, PnGroupHasPerson as ghp " +
		        " where " +
		        "    shg.comp_id.spaceId = :spaceId and " +
		        "    ghp.comp_id.groupId = shg.comp_id.groupId and " +
		        "    ghp.comp_id.personId = :personId and " +
		        "    g.groupId = shg.comp_id.groupId and " +
		        "    g.isPrincipal = 1 ";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("spaceId", spaceId).setInteger("personId", personId);
			group = (PnGroup) query.uniqueResult();
	    	} catch (Exception e) {
	    	    	if (LOG.isDebugEnabled()) {
			    LOG.debug("EXIT FAIL: getPersonPrincipalGroupForSpace");
			}
	    	    	e.printStackTrace();
	    	}
	    	if (LOG.isDebugEnabled()) {
		    LOG.debug("EXIT OK: getPersonPrincipalGroupForSpace");
		}
	    	
	    	return group;
	}
}
