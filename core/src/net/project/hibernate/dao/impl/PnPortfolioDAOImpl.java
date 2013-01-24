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

import net.project.hibernate.dao.IPnPortfolioDAO;
import net.project.hibernate.model.PnPortfolio;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnPortfolioDAOImpl extends AbstractHibernateAnnotatedDAO<PnPortfolio, Integer> implements IPnPortfolioDAO {
	
    	private final static Logger LOG = Logger.getLogger(PnPortfolioDAOImpl.class);
    	
	public PnPortfolioDAOImpl(){
		super(PnPortfolio.class);
	}

	public PnPortfolio getPortfolioForSpace(Integer spaceId) {
	    if (LOG.isDebugEnabled()) {
		LOG.debug("ENTRY OK: getPortfolioForSpace");
	    }
	    PnPortfolio result = null;
	    try {
		String sql = "select p " +
			"from PnSpaceHasPortfolio shp, PnPortfolio p " +
			"where shp.comp_id.spaceId = :spaceId " +
			"	and shp.comp_id.portfolioId = p.portfolioId " +
			"	and p.portfolioName = 'owner'";
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		result = (PnPortfolio) session.createQuery(sql).setInteger("spaceId", spaceId).uniqueResult();
	    } catch (Exception e) {
		if (LOG.isDebugEnabled()) {
		    LOG.debug("EXIT FAIL: getPortfolioForSpace");
		}
		e.printStackTrace();
	    }
	    if (LOG.isDebugEnabled()) {
		LOG.debug("EXIT OK: getPortfolioForSpace");
	    }
	    return result;
	}

}
