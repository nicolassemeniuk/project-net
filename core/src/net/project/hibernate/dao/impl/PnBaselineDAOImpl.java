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

import net.project.hibernate.dao.IPnBaselineDAO;
import net.project.hibernate.model.PnBaseline;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnBaselineDAOImpl extends AbstractHibernateAnnotatedDAO<PnBaseline, Integer> implements
	IPnBaselineDAO {
    
    private final static Logger LOG = Logger.getLogger(PnBaselineDAOImpl.class);

    public PnBaselineDAOImpl() {
	super(PnBaseline.class);
    }

    @SuppressWarnings("unchecked")
    public List<Object> getCurrentDefaultBaseline(Integer objectId) {
	if (LOG.isDebugEnabled()) {
	    LOG.debug("ENTRY OK: getCurrentDefaultBaselineAndPlanVersion");
	}
	List<Object> result = null;
	try {
	    String sql = "select new list(b.baseline_id, pv.date_start, pv.date_end) " +
	    		 "from pn_baseline b, pn_baseline_has_plan bhp, pn_plan_version pv " +
	    		 "where " +
	    		 " b.object_id = :plan_id " +
	    		 " and b.is_default_for_object = 1 " +
	    		 " and b.baseline_id = bhp.baseline_id " +
	    		 " and bhp.plan_version_id = pv.plan_version_id ";
	    Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
	    result = (List<Object>) session.createQuery(sql).setInteger("plan_id", objectId).uniqueResult();
	} catch (Exception e) {
	    if (LOG.isDebugEnabled()) {
		LOG.debug("EXIT FAIL: getCurrentDefaultBaselineAndPlanVersion");
	    }
	    e.printStackTrace();
	}
	
	if (LOG.isDebugEnabled()) {
	    LOG.debug("EXIT OK: getCurrentDefaultBaselineAndPlanVersion");
	}
	return result;
    }

}
