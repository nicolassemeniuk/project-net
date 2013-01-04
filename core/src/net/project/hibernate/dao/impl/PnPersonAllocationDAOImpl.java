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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.project.hibernate.dao.IPnPersonAllocationDAO;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnPersonAllocation;
import net.project.hibernate.model.PnPersonAllocationPK;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnPersonAllocationDAOImpl extends AbstractHibernateAnnotatedDAO<PnPersonAllocation, PnPersonAllocationPK> implements IPnPersonAllocationDAO {

	private static Logger log = Logger.getLogger(PnPersonAllocationDAOImpl.class);

	public PnPersonAllocationDAOImpl() {
		super(PnPersonAllocation.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.dao.IPnPersonAllocationDAO#getResourceAllocationEntryByPerson(java.lang.Integer,
	 *      java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	public List getResourceAllocationEntryByPerson(Integer businesId, Integer personId, Date startDate, Date endDate) {
		List result = new ArrayList();
		String sql = "SELECT ps.projectId,ps.projectName, pa.comp_id.allocationId, pa.hoursAllocated, pa.allocationDate ";
		try {
			if (businesId != null) {
				sql = sql + " FROM PnPerson p, PnPersonAllocation pa, PnProjectSpace ps, PnSpaceHasSpace shs, PnPortfolioHasSpace phs, PnSpaceHasPortfolio pshp "
						+ " WHERE pa.comp_id.personId = p.personId AND p.personId = :personId AND pa.allocationDate >= :startDate AND "
						+ " pa.allocationDate <= :endDate  AND pa.comp_id.spaceId = ps.projectId AND "
						+ " ps.projectId = phs.comp_id.spaceId AND shs.comp_id.childSpaceId = ps.projectId AND "
						+ " phs.comp_id.portfolioId = pshp.comp_id.portfolioId  AND ps.recordStatus = 'A' AND  p.userStatus != 'Deleted'"
						+ " AND pshp.comp_id.spaceId = :businessId ORDER BY ps.projectId, pa.allocationDate";
			} else {
				sql = sql + " FROM PnPerson p, PnPersonAllocation pa, PnProjectSpace ps"
						+ " WHERE pa.comp_id.personId = p.personId AND p.personId = :personId AND pa.allocationDate >= :startDate AND "
						+ " pa.allocationDate < :endDate  AND pa.comp_id.spaceId = ps.projectId AND " 
						+ "  ps.recordStatus = 'A' AND  p.userStatus != 'Deleted' ORDER BY ps.projectId, pa.allocationDate";
			}
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			if (businesId != null) {
				query.setParameter("businessId", businesId);
			}
			query.setParameter("personId", personId);
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);

			result = query.list();

		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.dao.IPnPersonAllocationDAO#getResourceAllocationEntryByProject(java.lang.Integer,
	 *      java.lang.Integer, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	public List getResourceAllocationEntryByProject(Integer businesId, Integer projectId, Date startDate, Date endDate) {
		List result = new ArrayList();

		try {
			String sql = " SELECT p.personId, p.firstName,p.lastName, pa.comp_id.allocationId, pa.hoursAllocated, pa.allocationDate "
					+ " FROM PnPerson p, PnPersonAllocation pa, PnProjectSpace ps "
					+ " WHERE pa.comp_id.personId = p.personId AND ps.projectId = :projectId AND pa.allocationDate >= :startDate AND "
					+ " pa.allocationDate < :endDate  AND pa.comp_id.spaceId = ps.projectId AND " + " ps.recordStatus = 'A' AND  p.userStatus != 'Deleted'";

			sql = sql + " ORDER BY p.personId, pa.allocationDate ";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);

			query.setParameter("projectId", projectId);
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);

			result = query.list();
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return result;
	}

	public List getResourceAllocationSumary(Integer businesId, Date startDate, Date endDate) {
		List result = new ArrayList();
		String sql = " SELECT p.personId, p.firstName,p.lastName, ps.projectId,ps.projectName, pa.comp_id.allocationId, pa.hoursAllocated, pa.allocationDate ";
		try {
			if (businesId != null) {
				sql = sql + " FROM PnPerson p, PnPersonAllocation pa, PnProjectSpace ps, PnSpaceHasSpace shs, PnPortfolioHasSpace phs, PnSpaceHasPortfolio pshp "
						+ " WHERE pa.comp_id.personId = p.personId AND pa.allocationDate >= :startDate AND  pa.allocationDate <= :endDate  AND "
						+ " pa.comp_id.spaceId = ps.projectId AND  ps.recordStatus = 'A' AND  p.userStatus != 'Deleted' AND  ps.projectId = phs.comp_id.spaceId AND "
						+ " shs.comp_id.childSpaceId = ps.projectId AND phs.comp_id.portfolioId = pshp.comp_id.portfolioId "
						+ " AND pshp.comp_id.spaceId = :businessId  ORDER BY p.personId, ps.projectId, pa.allocationDate ";
			} else {
				sql = sql + " FROM PnPerson p, PnPersonAllocation pa, PnProjectSpace ps "
						+ " WHERE pa.comp_id.personId = p.personId AND pa.allocationDate >= :startDate AND  pa.allocationDate < :endDate  AND "
						+ " pa.comp_id.spaceId = ps.projectId AND  ps.recordStatus = 'A' AND  p.userStatus != 'Deleted' " 
						+ " ORDER BY p.personId, ps.projectId, pa.allocationDate ";
			}

			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			if (businesId != null) {
				query.setParameter("businessId", businesId);
			}
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);

			result = query.list();
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return result;
	}

	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnPersonAllocationDAO#getResourceAllocationSumary(java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date)
	 */
	public List getResourceAllocationSumary(Integer resourceId, Integer businesId, Date startDate, Date endDate) {
		List result = new ArrayList();
		
		String sql = " SELECT distinct p.personId, p.firstName,p.lastName, ps.projectId,ps.projectName, pa.comp_id.allocationId, pa.hoursAllocated, pa.allocationDate "
					+ " FROM PnPerson p, PnPersonAllocation pa, PnProjectSpace ps, PnSpaceHasPerson shp "
					+ " WHERE pa.comp_id.personId = p.personId AND pa.allocationDate >= :startDate AND  pa.allocationDate < :endDate  AND "
					+ " pa.comp_id.spaceId = ps.projectId AND  ps.recordStatus = 'A' AND  p.userStatus != 'Deleted' AND   "
					+ " p.personId = shp.comp_id.personId   ";			
		try {
			if (businesId != null) {
				sql = sql + " AND shp.comp_id.spaceId = :businessId  ";
			} else {
				sql = sql + " AND shp.comp_id.spaceId IN " 
							+ " (SELECT  distinct b.businessId " 
						  		+ " FROM PnBusinessSpaceView b, PnSpaceHasPerson pshp " 
						  		+ " WHERE b.recordStatus = 'A' AND pshp.comp_id.spaceId = b.businessSpaceId " 
						  		+ " AND (pshp.comp_id.personId = :resourceId OR b.includesEveryone = 1)  " 
						  		+ " AND pshp.recordStatus = 'A' ) ";
			}

			String orderBy = " ORDER BY p.personId, ps.projectId, pa.allocationDate ";
			sql = sql + orderBy;
			
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			if (businesId != null) {
				query.setParameter("businessId", businesId);
			} else {
				query.setParameter("resourceId", resourceId);
			}
			
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);

			result = query.list();
		} catch (Exception e) {
			log.error(e.getMessage());
		}		
		
		return result;		
	}

	public void saveResourceAllocations(List<PnPersonAllocation> allocations) {
		for (PnPersonAllocation allocation : allocations) {
			getHibernateTemplate().saveOrUpdate(allocation);
		}
	}

	public PnPersonAllocation getResourceAllocationDetails(Integer resourceId, Integer projectId, Date startDate, Date endDate) {
		PnPersonAllocation allocatedProject = new PnPersonAllocation();
		
		String sql = " SELECT ppa.comp_id.personId, ppa.comp_id.spaceId, ppa.hoursAllocated, ppa.allocationDate " 
				   + " FROM PnPersonAllocation ppa "				   
				   + " WHERE ppa.allocationDate >= :startDate " 
				   + " and ppa.allocationDate < :endDate "
				   + " AND ppa.comp_id.personId = :resourceId " 
				   + " AND ppa.comp_id.spaceId = :projectId ";		
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
			query.setParameter("resourceId", resourceId);
			query.setParameter("projectId", projectId);			
			Object[] result = (Object[]) query.uniqueResult();
			if(result != null ) {			
				PnPerson person = new PnPerson((Integer) result[0]); 
				allocatedProject.setPnPerson(person);
				allocatedProject.setHoursAllocated((BigDecimal) result[2]);
				allocatedProject.setAllocationDate((Date) result[3]);
			}
		} catch (Exception e) {
			log.error("Error occurred while getting resource allocation details "+e.getMessage());			
		}		
		return allocatedProject;
	}

}
