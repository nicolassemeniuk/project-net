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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.hibernate.dao.IPnAssignmentDAO;
import net.project.hibernate.model.PnAssignment;
import net.project.hibernate.model.PnAssignmentPK;
import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.model.PnMaterialAssignment;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.model.PnTask;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.model.resource_reports.ReportUser;
import net.project.hibernate.model.resource_reports.ReportUserProjects;
import net.project.material.PnMaterialAssignmentList;
import net.project.material.PnMaterialList;
import net.project.resource.AssignmentStatus;
import net.project.resource.PersonStatus;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class PnAssignmentDAOImpl extends AbstractHibernateAnnotatedDAO<PnAssignment, PnAssignmentPK> implements IPnAssignmentDAO {

	private static Logger log = Logger.getLogger(PnAssignmentDAOImpl.class);

	public PnAssignmentDAOImpl() {
		super(PnAssignment.class);
	}

	@SuppressWarnings("unchecked")
	public List<PnAssignment> getAssigmentsList(Integer[] personIds, Date startDate, Date endDate) {
		List<PnAssignment> assignments = new ArrayList<PnAssignment>();

		try {
			String sql = "SELECT distinct a.percentAllocated, a.workComplete,  t.actualStart, t.actualFinish, a.work ,  "
					+ " t.taskId, t.taskName, t.duration, t.dateStart, t.dateFinish, t.workPercentComplete,"
					+ " p.personId, p.firstName, p.lastName, p.displayName,  " + " ps.projectId, ps.projectName, a.startDate, a.endDate  "
					+ " FROM PnAssignment a,  PnTask t, PnPerson p,  PnProjectSpace ps,  PnSpaceHasSpace shs "
					+ " WHERE a.comp_id.objectId = t.taskId  AND a.comp_id.personId = p.personId AND a.recordStatus = 'A' AND "
					+ " t.recordStatus = 'A' AND ps.recordStatus = 'A' AND a.comp_id.spaceId = shs.comp_id.childSpaceId and "
					+ " shs.comp_id.childSpaceId = ps.projectId ";

			String orderBy = " ORDER BY p.firstName, p.personId, t.dateStart DESC";

			if (personIds != null && personIds.length > 0) {
				sql = sql + " AND p.personId IN (:personsIds) ";
			}
			if (startDate != null && endDate != null) {
				sql = sql + " AND ((a.startDate BETWEEN :startDate AND :endDate " + " OR a.endDate  BETWEEN :startDate AND  :endDate) "
						+ " OR (a.startDate <= :startDate AND a.endDate >= :endDate ))";
			}

			sql = sql + orderBy;

			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			if (personIds != null && personIds.length > 0) {
				query.setParameterList("personsIds", personIds);
			}
			if (startDate != null && endDate != null) {
				query.setDate("startDate", startDate);
				query.setDate("endDate", endDate);
			}
			Iterator results = query.list().iterator();
			while (results.hasNext()) {
				Object[] row = (Object[]) results.next();
				PnAssignment assignment = new PnAssignment((BigDecimal) row[0], (BigDecimal) row[1], (Date) row[17], (Date) row[18], (BigDecimal) row[4]);
				PnTask task = new PnTask((Integer) row[5], (String) row[6], (BigDecimal) row[7], (Date) row[8], (Date) row[9], (BigDecimal) row[10],
						(Date) row[2], (Date) row[3]);
				PnPerson person = new PnPerson((Integer) row[11], (String) row[12], (String) row[13], (String) row[14]);
				PnProjectSpace project = new PnProjectSpace((Integer) row[15], (String) row[16]);
				assignment.setPnTask(task);
				assignment.setPnPerson(person);
				assignment.setPnProjectSpace(project);
				assignments.add(assignment);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return assignments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.project.hibernate.dao.IPnAssignmentDAO#getAssigmentsList(java.lang
	 * .Integer, java.lang.Integer[], java.util.Date)
	 */
	public List<PnAssignment> getCurrentAssigmentsListForProject(Integer projectId, Integer[] personIds, Date date) {
		List<PnAssignment> assignments = new ArrayList<PnAssignment>();

		try {
			String sql = "SELECT a.percentAllocated, a.workComplete,  a.actualStart, a.actualFinish, a.work ,  "
					+ "  t.taskId, t.taskName, t.duration, t.dateStart, t.dateFinish, t.workPercentComplete,"
					+ "  p.personId,p.firstName,p.lastName,p.displayName,  ps.projectId,ps.projectName, a.startDate, a.endDate "
					+ " FROM PnAssignment a,  PnTask t, PnPerson p,  PnProjectSpace ps,  PnSpaceHasSpace shs "
					+ " WHERE a.comp_id.objectId = t.taskId  AND a.comp_id.personId = p.personId AND a.recordStatus = 'A' "
					+ " AND a.percentComplete < 1 AND t.recordStatus = 'A' AND a.comp_id.spaceId = shs.comp_id.childSpaceId AND "
					+ "ps.projectId = :projectId AND shs.comp_id.childSpaceId = ps.projectId ";
			String orderBy = " ORDER BY p.personId";

			if (personIds != null && personIds.length > 0) {
				sql = sql + " and p.personId in (:personsIds) ";
			}
			if (date != null) {
				sql = sql + " and a.startDate <= :date AND a.endDate >= :date ";
			}
			sql = sql + orderBy;

			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			if (personIds != null && personIds.length > 0) {
				query.setParameterList("personsIds", personIds);
			}
			if (date != null) {
				query.setDate("date", date);
			}
			query.setInteger("projectId", projectId);
			Iterator results = query.list().iterator();
			while (results.hasNext()) {
				Object[] row = (Object[]) results.next();
				PnAssignment assignment = new PnAssignment((BigDecimal) row[0], (BigDecimal) row[1], (Date) row[17], (Date) row[18], (BigDecimal) row[4]);
				PnTask task = new PnTask((Integer) row[5], (String) row[6], (BigDecimal) row[7], (Date) row[8], (Date) row[9], (BigDecimal) row[10],
						(Date) row[2], (Date) row[3]);
				PnPerson person = new PnPerson((Integer) row[11], (String) row[12], (String) row[13], (String) row[14]);
				PnProjectSpace project = new PnProjectSpace((Integer) row[15], (String) row[16]);
				assignment.setPnTask(task);
				assignment.setPnPerson(person);
				assignment.setPnProjectSpace(project);
				assignments.add(assignment);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return assignments;
	}

	public List getWorkSumByMonthForUsers(Integer[] personIds, Integer[] projectIds, Date startDate, Date endDate) {
		String sql = "SELECT p.personId, p.firstName,p.lastName, a.percentAllocated, a.work,  " + "	 t.dateStart, t.dateFinish,  ps.projectId,ps.projectName  "
				+ " FROM PnAssignment a,  PnTask t, PnPerson p,  PnProjectSpace ps,  PnSpaceHasSpace shs "
				+ " WHERE a.comp_id.objectId = t.taskId  AND a.comp_id.personId = p.personId AND a.recordStatus = 'A' AND "
				+ " t.recordStatus = 'A' AND ps.recordStatus = 'A' AND a.comp_id.spaceId = shs.comp_id.childSpaceId AND "
				+ " shs.comp_id.childSpaceId = ps.projectId ";
		String orderBy = " ORDER BY p.personId, ps.projectId,  t.dateStart";

		if (personIds != null && personIds.length > 0) {
			sql = sql + " AND p.personId IN (:personsIds) ";
		}
		if (projectIds != null && projectIds.length > 0) {
			sql = sql + " AND ps.projectId IN (:projectIds) ";
		}
		if (startDate != null && endDate != null) {
			sql = sql + " AND ( a.startDate BETWEEN :startDate AND :endDate " + " 		OR a.endDate BETWEEN :startDate AND :endDate ) ";
		}

		sql = sql + orderBy;

		Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
		if (personIds != null && personIds.length > 0) {
			query.setParameterList("personsIds", personIds);
		}
		if (projectIds != null && projectIds.length > 0) {
			query.setParameterList("projectIds", projectIds);
		}
		if (startDate != null && endDate != null) {
			query.setDate("startDate", startDate);
			query.setDate("endDate", endDate);
		}
		List results = query.list();
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.project.hibernate.dao.IPnAssignmentDAO#getWorkSumByMonthForBusiness
	 * (java.lang.Integer, java.util.Date, java.util.Date)
	 */
	public List getWorkSumByMonthForBusiness(Integer resourceId, Integer businessId, Date startDate, Date endDate) {
		String sql = "SELECT distinct p.personId, p.firstName,p.lastName, a.percentAllocated, a.work,  "
				+ "	 t.dateStart, t.dateFinish,  ps.projectId, ps.projectName, a.comp_id.objectId  "
				+ " FROM PnAssignment a,  PnTask t, PnPerson p,  PnProjectSpace ps,  PnSpaceHasPerson pshp "
				+ " WHERE a.comp_id.objectId = t.taskId  AND a.comp_id.personId = p.personId AND a.recordStatus = 'A' AND "
				+ " t.recordStatus = 'A' AND ps.recordStatus = 'A'  AND pshp.comp_id.spaceId = ps.projectId"
				+ " AND a.comp_id.personId = pshp.comp_id.personId ";

		String orderBy = " ORDER BY p.personId, ps.projectId, t.dateStart";

		if (businessId != null) {
			sql += " AND pshp.comp_id.personId " + " IN ( SELECT distinct pshp1.comp_id.personId FROM PnSpaceHasPerson pshp1 "
					+ " WHERE pshp1.comp_id.spaceId = :businessId ) ";
		} else {
			sql += " AND pshp.comp_id.personId  " + " IN ( SELECT distinct pshp1.comp_id.personId FROM PnSpaceHasPerson pshp1, PnBusiness b "
					+ "      WHERE pshp1.comp_id.spaceId " + "      IN ( SELECT pshp2.comp_id.spaceId FROM PnSpaceHasPerson pshp2 "
					+ "           WHERE pshp2.comp_id.personId = :resourceId ) " + "      and b.businessId = pshp1.comp_id.spaceId"
					+ "      and b.recordStatus = 'A' )";
		}

		if (startDate != null && endDate != null) {
			sql = sql + " AND ( a.startDate BETWEEN :startDate AND :endDate " + " 		OR a.endDate BETWEEN :startDate AND :endDate ) ";
		}

		sql = sql + orderBy;
		Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
		if (businessId != null) {
			query.setInteger("businessId", businessId);
		} else {
			query.setParameter("resourceId", resourceId);
		}

		if (startDate != null && endDate != null) {
			query.setDate("startDate", startDate);
			query.setDate("endDate", endDate);
		}
		List results = query.list();
		return results;
	}

	public List getResourceAssignmentSummary(Integer businessId, Integer portfolioId, Date startDate, Date endDate) {
		String sql = "SELECT p.personId, p.firstName,p.lastName, a.percentAllocated, a.work,  " + "	 t.dateStart, t.dateFinish,  ps.projectId,ps.projectName  ";
		if (businessId != null) {
			sql = sql + " FROM PnAssignment a,  PnTask t, PnPerson p,  PnProjectSpace ps,  PnSpaceHasSpace shs, "
					+ "PnPortfolioHasSpace phs, PnSpaceHasPortfolio pshp "
					+ " WHERE ps.projectId = phs.comp_id.spaceId AND phs.comp_id.portfolioId = pshp.comp_id.portfolioId "
					+ " AND pshp.comp_id.spaceId = :businessId AND "
					+ " a.comp_id.objectId = t.taskId  AND a.comp_id.personId = p.personId AND a.recordStatus = 'A' AND "
					+ " t.recordStatus = 'A' AND ps.recordStatus = 'A' AND a.comp_id.spaceId = shs.comp_id.childSpaceId and "
					+ " shs.comp_id.childSpaceId = ps.projectId ";
		} else if (portfolioId != null) {
			sql = sql + " FROM PnAssignment a,  PnTask t, PnPerson p,  PnProjectSpace ps,  PnSpaceHasSpace shs, PnPortfolioHasSpace phs "
					+ " WHERE ps.projectId = phs.comp_id.spaceId AND phs.comp_id.portfolioId = :portfolioId AND  "
					+ " a.comp_id.objectId = t.taskId  AND a.comp_id.personId = p.personId AND a.recordStatus = 'A' AND "
					+ " t.recordStatus = 'A' AND ps.recordStatus = 'A' AND a.comp_id.spaceId = shs.comp_id.childSpaceId and "
					+ " shs.comp_id.childSpaceId = ps.projectId ";
		} else {
			sql = sql + " FROM PnAssignment a,  PnTask t, PnPerson p,  PnProjectSpace ps,  PnSpaceHasSpace shs, PnPortfolioHasSpace phs "
					+ " WHERE ps.projectId = phs.comp_id.spaceId AND  "
					+ " a.comp_id.objectId = t.taskId  AND a.comp_id.personId = p.personId AND a.recordStatus = 'A' AND "
					+ " t.recordStatus = 'A' AND ps.recordStatus = 'A' AND a.comp_id.spaceId = shs.comp_id.childSpaceId and "
					+ " shs.comp_id.childSpaceId = ps.projectId ";
		}

		String orderBy = " ORDER BY p.personId, ps.projectId,  t.dateStart";

		if (startDate != null) {
			sql = sql + " and a.startDate >= :startDate ";
		}
		if (endDate != null) {
			sql = sql + " and a.endDate <= :endDate ";
		}
		sql = sql + orderBy;

		Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
		if (businessId != null) {
			query.setInteger("businessId", businessId);
		}
		if (portfolioId != null) {
			query.setInteger("portfolioId", portfolioId);
		}
		if (startDate != null) {
			query.setDate("startDate", startDate);
		}
		if (endDate != null) {
			query.setDate("endDate", endDate);
		}
		List results = query.list();

		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.dao.IPnAssignmentDAO#
	 * getResourceAssignmentSummaryByBusiness(java.lang.Integer,
	 * java.lang.Integer, java.util.Date, java.util.Date)
	 */
	public List getResourceAssignmentSummaryByBusiness(Integer resourceId, Integer businessId, Date startDate, Date endDate) {
		String select = "  SELECT distinct p.personId, p.firstName,p.lastName, a.percentAllocated, a.work, "
				+ " a.startDate, a.endDate,  ps.projectId,ps.projectName, a.comp_id.objectId ";

		String from = " FROM  PnTask t, PnPlanHasTask pht, PnAssignment a, PnSpaceHasPlan shp, " + " PnPlan pp, PnProjectSpace ps, PnPerson p ";

		String where = " WHERE " + " t.taskId = pht.comp_id.taskId " + " AND t.taskId  = a.comp_id.objectId " + " AND pht.comp_id.planId = pp.planId "
				+ " AND ps.projectId = shp.comp_id.spaceId " + " AND a.comp_id.personId = p.personId " + " AND shp.comp_id.planId = pp.planId "
				+ " AND t.recordStatus = 'A' " + " AND a.recordStatus = 'A' " + " AND ps.recordStatus = 'A' " + " AND p.userStatus != '"
				+ PersonStatus.DELETED.getID() + "'";

		if (businessId != null) {
			from += ", PnSpaceHasPerson bshp ";
			where = where + "  AND bshp.comp_id.spaceId = :businessId  ";
		} else {
			where += " AND p.personId IN " + " (SELECT  distinct pshp.comp_id.personId " + " FROM PnBusinessSpaceView b, PnSpaceHasPerson pshp "
					+ " WHERE b.recordStatus = 'A' AND pshp.comp_id.spaceId = b.businessSpaceId "
					+ " AND (pshp.comp_id.personId = :resourceId OR b.includesEveryone = 1)  " + " AND pshp.recordStatus = 'A' ) ";
		}
		String orderBy = " ORDER BY p.personId, ps.projectId,  a.startDate";

		if (startDate != null && endDate != null) {
			where += " AND ((a.startDate BETWEEN :startDate AND :endDate " + " OR a.endDate BETWEEN :startDate AND :endDate )"
					+ " OR (a.startDate <= :startDate AND a.endDate >= :endDate ))";
		}

		String sql = select + from + where + orderBy;

		Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);

		if (businessId != null) {
			query.setInteger("businessId", businessId);
		} else {
			query.setInteger("resourceId", resourceId);
		}

		if (startDate != null && endDate != null) {
			query.setDate("startDate", startDate);
			query.setDate("endDate", endDate);
		}
		List results = query.list();

		return results;
	}

	public List<PnAssignment> getResourceAssignmentDetails(Integer resourceId, Integer[] projectIds, Date startDate, Date endDate) {
		List<PnAssignment> assignments = new ArrayList<PnAssignment>();
		String sql = "SELECT new PnAssignment (p.displayName, ps.projectId, ps.projectName, "
				+ " t.taskName, t.workPercentComplete, o.pnObjectType.objectType, a.work, a.workUnits,"
				+ " a.workComplete, a.startDate, a.endDate, a.percentAllocated, a.comp_id.objectId) "
				+ " FROM PnAssignment a, PnTask t, PnObject o ,PnProjectSpace ps, PnPerson p " + " WHERE a.comp_id.spaceId = ps.projectId "
				+ " AND a.comp_id.objectId = o.objectId " + " AND o.objectId = t.taskId " + " AND a.comp_id.personId = p.personId "
				+ " AND a.comp_id.personId = :resourceId " + " AND t.recordStatus = 'A' " + " AND a.recordStatus = 'A' " + " AND ps.recordStatus = 'A' ";

		if (projectIds != null) {
			sql += " AND ps.projectId IN( :projectIds )";
		}

		if (startDate != null && endDate != null) {
			if (startDate.equals(endDate)) {
				sql += " AND a.startDate <= :startDate ";
				sql += " AND a.endDate >= :endDate ";
			} else {
				sql += " AND ( a.startDate between :startDate and :endDate ";
				sql += " or a.endDate between :startDate and :endDate ) ";
			}
		}

		sql += " ORDER BY ps.projectName ";

		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("resourceId", resourceId);

			if (projectIds != null) {
				query.setParameterList("projectIds", projectIds);
			}

			if (startDate != null) {
				query.setDate("startDate", startDate);
			}

			if (endDate != null) {
				query.setDate("endDate", endDate);
			}

			assignments = query.list();
		} catch (Exception e) {
			log.error("Error occured while getting resource assignment details " + e.getMessage());
		}
		return assignments;
	}

	public List<PnAssignment> getOverAllocatedResources(Integer userId) {
		List<PnAssignment> assignments = new ArrayList<PnAssignment>();

		String sql = " SELECT a.startDate, SUM (a.percentAllocated), " + " p.personId, p.firstName, p.lastName, p.displayName "
				+ " FROM   PnAssignment a, PnTask t, PnPerson p, PnSpaceHasSpace shs " + " WHERE  t.taskId = a.comp_id.objectId "
				+ " AND    p.personId = a.comp_id.personId  " + " AND    t.recordStatus = 'A' " + " and    a.comp_id.spaceId = shs.comp_id.childSpaceId "
				+ " and    shs.comp_id.parentSpaceId IN " + " ( SELECT b.businessId " + "   FROM PnBusiness b, PnObject o, PnPerson p "
				+ "   WHERE o.objectId = b.businessId " + "   AND p.personId = o.pnPerson.personId " + "   AND o.recordStatus = 'A' "
				+ "   AND p.recordStatus = 'A' " + "   AND o.pnPerson.personId = :userId ) "
				+ " GROUP BY p.personId, p.firstName, p.lastName, p.displayName, a.startDate " + " HAVING   SUM (a.percentAllocated) > 100 "
				+ " ORDER BY p.personId ASC ";

		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("userId", userId);

			Iterator results = query.list().iterator();
			while (results.hasNext()) {
				Object[] row = (Object[]) results.next();
				PnAssignment assignment = new PnAssignment((Date) row[0], (BigDecimal) row[1]);
				PnPerson person = new PnPerson((Integer) row[2], (String) row[3], (String) row[4], (String) row[5]);
				assignment.setPnPerson(person);
				assignments.add(assignment);
			}
		} catch (Exception e) {
			log.error("Error occured while getting Over allocated resources " + e.getMessage());
		}
		return assignments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.project.hibernate.dao.IPnAssignmentDAO#getOverAssignedResources(java
	 * .lang.Integer)
	 */
	public List<PnAssignment> getOverAssignedResources(Integer userId, Date startDate, Date endDate) {
		List<PnAssignment> assignments = new ArrayList<PnAssignment>();

		String sql = "select p.personId, p.firstName, p.lastName, p.displayName, t.taskId, t.taskName, "
				+ " a.startDate, a.endDate, a.percentAllocated , a.work " + " from PnAssignment a, PnPerson p, PnTask t, PnSpaceHasPerson shp  "
				+ "	where a.comp_id.objectId = t.taskId and a.comp_id.personId = p.personId and a.recordStatus = 'A' "
				+ " and t.recordStatus = 'A' and p.personId = shp.comp_id.personId and " + " shp.comp_id.spaceId in "
				+ " (select b.businessSpaceId from PnBusinessSpaceView b, PnSpaceHasPerson shp"
				+ "   where b.recordStatus ='A' and shp.comp_id.spaceId = b.businessSpaceId and "
				+ "  (shp.comp_id.personId = :userId or b.includesEveryone = 1)) ";

		String orderBy = "order by p.personId, shp.comp_id.spaceId, a.startDate, a.endDate";
		if (startDate != null) {
			sql = sql + " and ((a.startDate >= :startDate) or (a.startDate <= :startDate and a.endDate >= :startDate )) ";
		}
		if (endDate != null) {
			sql = sql + " and ((a.endDate <= :endDate ) or (a.startDate <= :endDate and a.endDate >= :endDate) ) ";
		}
		sql = sql + orderBy;
		Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
		query.setInteger("userId", userId);
		if (startDate != null) {
			query.setDate("startDate", startDate);
		}
		if (endDate != null) {
			query.setDate("endDate", endDate);
		}

		Iterator results = query.list().iterator();
		while (results.hasNext()) {
			Object[] row = (Object[]) results.next();
			PnAssignment assignment = new PnAssignment((Date) row[6], (Date) row[7], (BigDecimal) row[8], (BigDecimal) row[9]);
			PnPerson person = new PnPerson((Integer) row[0], (String) row[1], (String) row[2], (String) row[3]);
			assignment.setPnPerson(person);
			assignments.add(assignment);
		}

		return assignments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.dao.IPnAssignmentDAO#
	 * getCurrentOverAssignedResourcesForProject(java.lang.Integer,
	 * java.util.Date)
	 */
	public List<PnAssignment> getCurrentOverAssignedResourcesForProject(Integer projectId, Date date) {
		List<PnAssignment> assignments = new ArrayList<PnAssignment>();

		String sql = "select p.personId, p.firstName, p.lastName, p.displayName, t.taskId, t.taskName, "
				+ " a.startDate, a.endDate, a.percentAllocated , a.work " + " from PnAssignment a, PnPerson p, PnTask t, PnSpaceHasPerson shp  "
				+ "	where a.comp_id.objectId = t.taskId and a.comp_id.personId = p.personId and a.recordStatus = 'A' "
				+ " and t.recordStatus = 'A' and p.personId = shp.comp_id.personId and "
				+ " shp.comp_id.spaceId = :projectId and a.startDate <= :date and a.endDate >= :date "
				+ " order by p.personId, shp.comp_id.spaceId, a.startDate, a.endDate";

		Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
		query.setInteger("projectId", projectId);
		query.setDate("date", date);

		Iterator results = query.list().iterator();
		while (results.hasNext()) {
			Object[] row = (Object[]) results.next();
			PnAssignment assignment = new PnAssignment((Date) row[6], (Date) row[7], (BigDecimal) row[8], (BigDecimal) row[9]);
			PnPerson person = new PnPerson((Integer) row[0], (String) row[1], (String) row[2], (String) row[3]);
			assignment.setPnPerson(person);
			assignments.add(assignment);
		}

		return assignments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.project.hibernate.dao.IPnAssignmentDAO#getAssignmentsByPersonForProject
	 * (java.lang.Integer, java.util.Date, java.util.Date)
	 */
	public List<Teammate> getAssignmentsByPersonForProject(Integer projectId, Date startDate, Date endDate) {
		List<Teammate> teammates = new ArrayList<Teammate>();
		String sql = "SELECT DISTINCT p.personId, p.firstName, p.lastName, p.displayName, p.email, "
				+ " p.membershipPortfolioId, a.startDate, a.endDate, a.percentAllocated,  a.work, a.comp_id.spaceId, "
				+ " a.percentComplete , t.taskId, t.taskName, ppp.skype, p.userStatus " + " FROM PnAssignment a, PnTask t,"
				+ " PnPerson p LEFT JOIN p.pnPersonProfile ppp " + "	WHERE a.comp_id.objectId = t.taskId AND a.comp_id.personId = p.personId "
				+ " AND a.recordStatus = 'A' AND t.recordStatus = 'A' " + " AND p.userStatus IN ('Active', 'Unregistered') "
				+ " AND ((a.comp_id.spaceId = :projectId AND a.startDate <= :endDate) OR "
				+ "      (a.comp_id.spaceId <> :projectId AND (a.startDate BETWEEN :startDate AND :endDate "
				+ "       OR a.endDate BETWEEN :startDate AND :endDate ))) " + " AND p.personId IN (SELECT shp.comp_id.personId FROM PnSpaceHasPerson shp "
				+ " WHERE shp.relationshipPersonToSpace = 'member' AND shp.comp_id.spaceId = :projectId) "
				+ " ORDER BY p.personId, a.comp_id.spaceId, a.startDate, a.endDate";

		Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
		query.setInteger("projectId", projectId);
		query.setDate("startDate", startDate);
		query.setDate("endDate", endDate);

		Iterator results = query.list().iterator();
		int personId = 0;
		Teammate teammate = new Teammate();
		while (results.hasNext()) {
			Object[] row = (Object[]) results.next();
			if (personId != (Integer) row[0]) {
				personId = (Integer) row[0];
				teammate = new Teammate((Integer) row[0], (String) row[1], (String) row[2], (String) row[3], (String) row[4], (Integer) row[5]);
				// setting skype id for teammate
				teammate.setSkype((String) row[14]);
				// setting user status Active or Unregistered
				teammate.setUserStatus((String) row[15]);
				teammates.add(teammate);
			}
			PnAssignment assignment = new PnAssignment((Date) row[6], (Date) row[7], (BigDecimal) row[8], (BigDecimal) row[9], (BigDecimal) row[11]);
			PnPerson person = new PnPerson((Integer) row[0], (String) row[1], (String) row[2], (String) row[3]);
			assignment.setComp_id(new PnAssignmentPK((Integer) row[10], personId, (Integer) row[12]));
			assignment.setPnPerson(person);
			teammate.getAssignments().add(assignment);
		}
		return teammates;
	}

	public List<ReportUser> getAssignedProjectsByBusiness(String userId, Integer businessId, Date startDate, Date endDate) {
		List<ReportUser> projects = new ArrayList<ReportUser>();
		String sql = "SELECT distinct p.personId , p.firstName , p.lastName, ps.projectId, ps.projectName "
				+ " FROM PnAssignment a, PnPerson p, PnProjectSpace ps, PnSpaceHasPerson pshp"
				+ " WHERE p.personId = a.comp_id.personId AND a.comp_id.personId = pshp.comp_id.personId "
				+ " AND a.comp_id.spaceId = ps.projectId AND ps.projectId = pshp.comp_id.spaceId "
				+ " AND ps.recordStatus='A' AND p.userStatus !='Deleted' AND a.recordStatus = 'A' " + " AND ((a.startDate between :startDate AND :endDate"
				+ " OR a.endDate between :startDate AND :endDate )" + " OR (a.startDate <= :startDate AND a.endDate >= :endDate ))";

		if (businessId != null) {
			sql += " AND pshp.comp_id.personId " + " IN ( SELECT distinct pshp1.comp_id.personId FROM PnSpaceHasPerson pshp1 "
					+ " WHERE pshp1.comp_id.spaceId = :businessId ) ";
		} else {
			sql += " AND pshp.comp_id.personId  " + " IN ( SELECT distinct pshp1.comp_id.personId FROM PnSpaceHasPerson pshp1, PnBusiness b "
					+ "      WHERE pshp1.comp_id.spaceId " + "      IN ( SELECT pshp2.comp_id.spaceId FROM PnSpaceHasPerson pshp2 "
					+ "           WHERE pshp2.comp_id.personId = :userId ) " + "      AND b.businessId = pshp1.comp_id.spaceId AND b.recordStatus = 'A' )";
		}
		sql += " ORDER BY p.personId ";
		Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);

		if (businessId != null) {
			query.setInteger("businessId", businessId);
		} else {
			query.setString("userId", userId);
		}
		query.setDate("startDate", startDate);
		query.setDate("endDate", endDate);
		Iterator results = query.list().iterator();
		while (results.hasNext()) {
			Object[] row = (Object[]) results.next();
			ReportUser reportUser = new ReportUser((Integer) row[0], (String) row[1], (String) row[2]);
			ReportUserProjects reportUserProjects = new ReportUserProjects((Integer) row[3], (String) row[4]);
			List<ReportUserProjects> projectList = new ArrayList<ReportUserProjects>();
			projectList.add(reportUserProjects);
			reportUser.setProjektList(projectList);
			projects.add(reportUser);
		}
		return projects;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.project.hibernate.dao.IPnAssignmentDAO#getTeammatesWithoutAssignments
	 * (java.lang.Integer, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	public List<Teammate> getTeammatesWithoutAssignments(Integer projectId, Date date) {
		List<Teammate> teammates = new ArrayList<Teammate>();

		String sql = " SELECT DISTINCT new net.project.hibernate.model.project_space.Teammate(p.personId, p.firstName, "
				+ " p.lastName, p.displayName, p.email, p.membershipPortfolioId, ppp.skype, p.userStatus ) "
				+ " FROM PnPerson p LEFT JOIN p.pnPersonProfile ppp " + " WHERE p.userStatus IN ('Active', 'Unregistered') "
				+ " AND p.personId IN (SELECT shp.comp_id.personId FROM PnSpaceHasPerson shp "
				+ " WHERE shp.relationshipPersonToSpace = 'member' AND shp.comp_id.spaceId = :projectId) " + " AND NOT EXISTS (SELECT a.comp_id.personId "
				+ " FROM PnAssignment a, PnObject o " + " WHERE a.comp_id.personId = p.personId AND a.recordStatus = 'A' AND "
				// + " a.percentComplete < 1 AND"
				+ " a.comp_id.objectId = o.objectId and o.pnObjectType.objectType = 'task' "
				+ " AND a.comp_id.spaceId = :projectId AND a.endDate <= :endDate) ";
		Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
		query.setInteger("projectId", projectId);
		query.setDate("endDate", date);
		teammates = query.list();
		return teammates;
	}

	// public List<Assignment> getAssigmentsByAssignor(Integer assignorId) {
	// List<Assignment> assignments = new ArrayList<Assignment>();
	// String sql =
	// "SELECT a, p1.displayName, p2.displayName, on1.name, on2.name, o1.pnObjectType.objectType, o2.pnObjectType.objectType, pv.timezoneCode "
	// +
	// "FROM PnAssignment a, PnPerson p1, PnPerson p2, PnObjectName on1, PnObjectName on2, PnObject o1, PnObject o2, PnPersonView pv WHERE "
	// +
	// "p1.userStatus = 'Active' AND " +
	// "p1.personId = a.pnAssignor.personId AND " +
	// "a.pnAssignor.personId = :assignorId AND " +
	// "p2.userStatus = 'Active' AND " +
	// "p2.personId = a.comp_id.personId AND " +
	// "pv.personId = p2.personId AND " +
	// "on1.objectId = a.comp_id.objectId AND " +
	// "on2.objectId = a.comp_id.spaceId AND " +
	// "o1.objectId = on2.objectId AND " +
	// "o2.objectId = on1.objectId AND " +
	// "o1.recordStatus = 'A' AND " +
	// "o2.recordStatus = 'A' AND " +
	// "a.recordStatus = 'A'";
	// try {
	// Query query =
	// getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
	// query.setInteger("assignorId", assignorId);
	// Iterator results = query.list().iterator();
	// while (results.hasNext()) {
	// Object[] row = (Object[]) results.next();
	// PnAssignment pnAssignment = (PnAssignment) row[0];
	// String assignorName = (String) row[1];
	// String assigneeName = (String) row[2];
	// String objectName = (String) row[3];
	// String spaceName = (String) row[4];
	// String spaceType = (String) row[5];
	// String objectType = (String) row[6];
	// String timeZoneId = (String) row[7];
	// Assignment assignment = null;
	// AssignmentType assignmentType = AssignmentType.forObjectType(spaceType);
	// if (assignmentType == null) {
	// if (log.isDebugEnabled()){
	// log.debug(" AssignmentType.forObjectType returned null !");
	// }
	// } else {
	// assignment = assignmentType.newAssignment();
	// assignment.populate(pnAssignment, assignorName, assigneeName, objectName,
	// objectType, spaceName, objectType, timeZoneId);
	// assignments.add(assignment);
	// }
	//
	// }
	// } catch (Exception e) {
	// log.error("Error occured while getting assignments of user from project : "
	// + e.getMessage());
	// e.printStackTrace();
	// }
	// return assignments;
	// }
	//
	public List<PnAssignment> getAssignorAssignmentDetails(Integer assignorId) {
		List<PnAssignment> assignments = new ArrayList<PnAssignment>();
		String sql = "SELECT new PnAssignment (p.displayName, ps.projectId, ps.projectName, "
				+ " t.taskName, t.workPercentComplete, o.pnObjectType.objectType, a.work, a.workUnits, "
				+ " a.workComplete, a.startDate, a.endDate, a.percentAllocated, a.comp_id.objectId) "
				+ " FROM PnAssignment a, PnTask t, PnObject o ,PnProjectSpace ps, PnPerson p " + " WHERE a.comp_id.spaceId = ps.projectId "
				+ " AND a.comp_id.objectId = o.objectId " + " AND o.objectId = t.taskId " + " AND a.pnAssignor.personId = p.personId "
				+ " AND a.pnAssignor.personId = :assignorId " + " AND t.recordStatus = 'A' " + " AND a.recordStatus = 'A' " + " AND ps.recordStatus = 'A' ";

		sql += " ORDER BY ps.projectName ";

		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("assignorId", assignorId);
			assignments = query.list();
		} catch (Exception e) {
			log.error("Error occured while getting resource assignment details " + e.getMessage());
		}
		return assignments;
	}

	public List<PnAssignment> getAssignmentDetailsWithFilters(Integer[] personIds, String assigneeORAssignor, Integer[] projectIds, Integer businessId,
			String[] assignmentTypes, boolean lateAssignment, boolean comingDueDate, boolean shouldHaveStart, boolean inProgress, Date startDate, Date endDate,
			Integer statusId, Double percentComplete, String PercentCompleteComparator, String assignmentName, String assignmentNameComparator, int offset,
			int range, boolean isOrderByPerson) {
		List<PnAssignment> assignments = new ArrayList<PnAssignment>();

		String sql = "SELECT new PnAssignment ("
				+ " p.firstName, p.lastName,  assignor.firstName, assignor.lastName,"
				+ " a.comp_id.spaceId, on1.name, o1.pnObjectType.objectType,"
				+ " o2.pnObjectType.objectType, "
				+ " a.work, a.workUnits, a.workComplete, a.workCompleteUnits, a.percentComplete, a.startDate, a.endDate, a.actualStart, a.actualFinish, a.percentAllocated,"
				+ " a.comp_id.objectId, on2.name, task.taskType, task.parentTaskId, ci.classId, p.personId, a.statusId, ci.sequenceNo) "
				+ " FROM PnAssignment a left join  a.pnAssignor assignor " + " left join a.pnTask task left join a.pnClassInstance ci, "
				+ " PnObject o1, PnObject o2, " + " PnObjectName on1, PnObjectName on2, " + " PnPerson p " + " WHERE o1.objectId = a.comp_id.spaceId "
				+ " AND o2.objectId = a.comp_id.objectId " + " AND on1.objectId = a.comp_id.spaceId " + " AND on2.objectId = a.comp_id.objectId "
				+ " AND a.recordStatus = 'A' " + " AND a.statusId != '" + AssignmentStatus.REJECTED.getID() + "' " // FIX
																													// FOR:
																													// bug-775
				+ " AND o1.recordStatus = 'A' " + " AND o2.recordStatus = 'A' " + " AND p.personId = a.comp_id.personId ";

		if (personIds != null) {
			if (assigneeORAssignor != null && assigneeORAssignor.equalsIgnoreCase("assignor")) {// assignor
																								// Assignment
				sql += " AND a.pnAssignor.personId IN (:personIds) ";
			} else if (assigneeORAssignor != null && assigneeORAssignor.equalsIgnoreCase("assignee")) {// resource
																										// Assignment
				sql += " AND a.comp_id.personId IN (:personIds) ";
			} else {
				sql += " AND( a.pnAssignor.personId IN (:personIds) OR a.comp_id.personId IN (:personIds) )";
			}
		}

		// project ids clause
		if (projectIds != null) {
			sql += " AND a.comp_id.spaceId IN( :projectIds )";
		}

		if (businessId != null) {
			sql += " AND( a.comp_id.spaceId IN(" + " select distinct shs.comp_id.childSpaceId from PnSpaceHasSpace shs "
					+ " where shs.parentSpaceType = 'business' " + " and	shs.comp_id.parentSpaceId = :businessId and shs.recordStatus = 'A') "
					+ " OR a.comp_id.spaceId = :businessId )";
		}

		// Assignment type clause
		if (assignmentTypes != null) {
			sql += " AND o2.pnObjectType.objectType IN( :assignmentTypes )";
		}

		if (lateAssignment) {
			sql += " AND ((a.percentComplete is null) OR " + " (a.endDate <  :toDayDate AND " + " (a.percentComplete < 1))) ";
		}

		if (comingDueDate) {
			sql += " AND ((a.endDate <= :toDayDatePlus7 ) AND (a.endDate >= :toDayDate))";
		}

		if (shouldHaveStart) {
			sql += " AND ((a.percentComplete is null) OR " + " (a.startDate <=  :toDayDate AND " + " (a.percentComplete = 0))) ";
		}

		if (inProgress) {
			sql += " AND (a.percentComplete > 0 and a.percentComplete < 1)";
		}

		if (startDate != null && endDate != null) {
			if (startDate.equals(endDate)) {
				sql += " AND a.startDate <= :startDate ";
				sql += " AND a.endDate >= :endDate ";
			} else {
				sql += " AND ( a.startDate between :startDate and :endDate ";
				sql += " OR a.endDate between :startDate and :endDate OR (a.startDate <= :startDate AND a.endDate >= :endDate ) ) ";
			}
		}

		if (statusId != null) {
			sql += " AND a.statusId = :statusId";
		}

		if (percentComplete != null && PercentCompleteComparator != null) {
			if (PercentCompleteComparator.equalsIgnoreCase("equals")) {
				sql += " AND a.percentComplete = :percentComplete ";
			}
			if (PercentCompleteComparator.equalsIgnoreCase("notequals")) {
				sql += " AND a.percentComplete != :percentComplete ";
			}
			if (PercentCompleteComparator.equalsIgnoreCase("lessthan")) {
				sql += " AND a.percentComplete < :percentComplete ";
			}
			if (PercentCompleteComparator.equalsIgnoreCase("greaterthan")) {
				sql += " AND a.percentComplete > :percentComplete ";
			}
		}
		if (assignmentName != null && assignmentNameComparator != null) {
			if (assignmentNameComparator.equalsIgnoreCase("equals")) {
				sql += " AND on2.name = :assignmentName ";
			}
			if (assignmentNameComparator.equalsIgnoreCase("notequals")) {
				sql += " AND on2.name != :assignmentName ";
			}
			if (assignmentNameComparator.equalsIgnoreCase("contains")) {
				sql += " AND upper(on2.name) like upper(:assignmentName) ";
			}
		}
		sql += " ORDER BY ";
		if (isOrderByPerson) {
			sql += " p.lastName, p.firstName, p.personId, ";
		}
		sql += " on1.name, a.comp_id.spaceId, task.seq";

		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			if (personIds != null) {
				query.setParameterList("personIds", personIds);
			}

			if (projectIds != null) {
				query.setParameterList("projectIds", projectIds);
			}

			if (businessId != null) {
				query.setParameter("businessId", businessId);
			}

			if (assignmentTypes != null) {
				query.setParameterList("assignmentTypes", assignmentTypes);
			}

			if (lateAssignment || comingDueDate || shouldHaveStart) {
				query.setDate("toDayDate", new Date());
			}

			if (comingDueDate) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, 8);
				query.setDate("toDayDatePlus7", cal.getTime());
			}

			if (startDate != null) {
				query.setDate("startDate", startDate);
			}

			if (endDate != null) {
				query.setDate("endDate", endDate);
			}

			if (statusId != null) {
				query.setInteger("statusId", statusId);
			}

			if (percentComplete != null && PercentCompleteComparator != null) {
				query.setDouble("percentComplete", percentComplete);
			}

			if (assignmentName != null && assignmentNameComparator != null) {
				if (assignmentNameComparator.equalsIgnoreCase("contains")) {
					query.setParameter("assignmentName", "%" + assignmentName + "%");
				} else {
					query.setParameter("assignmentName", assignmentName);
				}
			}
			if (offset != 0) {
				query.setFirstResult(offset);
			}
			if (range != 0) {
				query.setMaxResults(range);
			}
			assignments = query.list();
		} catch (Exception e) {
			log.error("Error occured while getting person Assignment " + e.getMessage());
		}
		return assignments;
	}

	@SuppressWarnings("unchecked")
	public Integer getTotalAssignmentCountWithFilters(Integer personId, String assigneeORAssignor, Integer[] projectIds, Integer businessId,
			String[] assignmentTypes, boolean lateAssignment, boolean comingDueDate, boolean shouldHaveStart, boolean inProgress, Date startDate, Date endDate,
			Integer statusId, Double percentComplete, String PercentCompleteComparator, String assignmentName, String assignmentNameComparator) {
		Integer nAssignment = 0;
		String sql = " SELECT count(a.comp_id.objectId) " + " FROM PnAssignment a left join  a.pnAssignor assignor " + " left join a.pnTask task, "
				+ " PnObject o1, PnObject o2, " + " PnObjectName on1, PnObjectName on2, " + " PnPerson p " + " WHERE o1.objectId = a.comp_id.spaceId "
				+ " AND o2.objectId = a.comp_id.objectId " + " AND on1.objectId = a.comp_id.spaceId " + " AND on2.objectId = a.comp_id.objectId "
				+ " AND a.recordStatus = 'A' " + " AND o1.recordStatus = 'A' " + " AND o2.recordStatus = 'A' " + " AND p.personId = a.comp_id.personId ";

		if (assigneeORAssignor != null && assigneeORAssignor.equalsIgnoreCase("assignor")) {// assignor
																							// Assignment
			sql += " AND a.pnAssignor.personId = :personId ";

		} else if (assigneeORAssignor != null && assigneeORAssignor.equalsIgnoreCase("assignee")) {// resource
																									// Assignment
			sql += " AND a.comp_id.personId = :personId ";
		} else {
			sql += " AND( a.pnAssignor.personId = :personId OR a.comp_id.personId = :personId )";
		}

		// project ids clause
		if (projectIds != null) {
			sql += " AND a.comp_id.spaceId IN( :projectIds )";
		}

		if (businessId != null) {
			sql += " AND a.comp_id.spaceId IN(" + " select distinct shs.comp_id.childSpaceId from PnSpaceHasSpace shs "
					+ " where shs.parentSpaceType = 'business' " + " and	shs.comp_id.parentSpaceId = :businessId and shs.recordStatus = 'A')";
		}

		// Assignment type clause
		if (assignmentTypes != null) {
			sql += " AND o2.pnObjectType.objectType IN( :assignmentTypes )";
		}

		if (lateAssignment) {
			sql += " AND ((a.percentComplete is null) OR " + " (a.endDate <  :toDayDate AND " + " (a.percentComplete < 1))) ";
		}

		if (comingDueDate) {
			sql += " AND ((a.endDate <= :toDayDatePlus7 ) AND (a.endDate >= :toDayDate))";
		}

		if (shouldHaveStart) {
			sql += " AND ((a.percentComplete is null) OR " + " (a.startDate <=  :toDayDate AND " + " (a.percentComplete = 0))) ";
		}

		if (inProgress) {
			sql += " AND (a.percentComplete > 0 and a.percentComplete < 1)";
		}

		if (startDate != null && endDate != null) {
			if (startDate.equals(endDate)) {
				sql += " AND a.startDate <= :startDate ";
				sql += " AND a.endDate >= :endDate ";
			} else {
				sql += " AND ( a.startDate between :startDate and :endDate ";
				sql += " or a.endDate between :startDate and :endDate ) ";
			}
		}

		if (statusId != null) {
			sql += " AND a.statusId = :statusId";
		}

		if (percentComplete != null && PercentCompleteComparator != null) {
			if (PercentCompleteComparator.equalsIgnoreCase("equals")) {
				sql += " AND a.percentComplete = :percentComplete ";
			}
			if (PercentCompleteComparator.equalsIgnoreCase("notequals")) {
				sql += " AND a.percentComplete != :percentComplete ";
			}
			if (PercentCompleteComparator.equalsIgnoreCase("lessthan")) {
				sql += " AND a.percentComplete < :percentComplete ";
			}
			if (PercentCompleteComparator.equalsIgnoreCase("greaterthan")) {
				sql += " AND a.percentComplete > :percentComplete ";
			}
		}
		if (assignmentName != null && assignmentNameComparator != null) {
			if (assignmentNameComparator.equalsIgnoreCase("equals")) {
				sql += " AND on2.name = :assignmentName ";
			}
			if (assignmentNameComparator.equalsIgnoreCase("notequals")) {
				sql += " AND on2.name != :assignmentName ";
			}
			if (assignmentNameComparator.equalsIgnoreCase("contains")) {
				sql += " AND upper(on2.name) like upper(:assignmentName) ";
			}
		}

		sql += " ORDER BY on1.name , a.comp_id.spaceId ";

		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("personId", personId);

			if (projectIds != null) {
				query.setParameterList("projectIds", projectIds);
			}

			if (businessId != null) {
				query.setParameter("businessId", businessId);
			}

			if (assignmentTypes != null) {
				query.setParameterList("assignmentTypes", assignmentTypes);
			}

			if (lateAssignment || comingDueDate || shouldHaveStart) {
				query.setDate("toDayDate", new Date());
			}

			if (comingDueDate) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, 8);
				query.setDate("toDayDatePlus7", cal.getTime());
			}

			if (startDate != null) {
				query.setDate("startDate", startDate);
			}

			if (endDate != null) {
				query.setDate("endDate", endDate);
			}

			if (statusId != null) {
				query.setInteger("statusId", statusId);
			}

			if (percentComplete != null && PercentCompleteComparator != null) {
				query.setDouble("percentComplete", percentComplete);
			}

			if (assignmentName != null && assignmentNameComparator != null) {
				if (assignmentNameComparator.equalsIgnoreCase("contains")) {
					query.setParameter("assignmentName", "%" + assignmentName + "%");
				} else {
					query.setParameter("assignmentName", assignmentName);
				}
			}
			Object obj = null;
			List<Object> result = query.list();
			if (result.size() > 0) {
				obj = result.get(0);
			}

			nAssignment = Integer.parseInt(obj == null ? "0" : obj.toString());

		} catch (Exception e) {
			log.error("Error occured while getting count of Assignment " + e.getMessage());
		}
		return nAssignment;
	}

	/**
	 * Method to return assignment record for object with given objectId
	 * 
	 * @param objectId
	 * @return
	 */
	public PnAssignment getAssigmentByAssignmentId(Integer objectId, Integer userId) {
		PnAssignment assignment = new PnAssignment();

		try {
			String sql = " FROM PnAssignment a  WHERE a.comp_id.objectId = :objectId AND( a.pnAssignor.personId = :userId OR a.comp_id.personId = :userId ) AND a.recordStatus = 'A' ";

			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("objectId", objectId);
			query.setInteger("userId", userId);
			assignment = (PnAssignment) query.uniqueResult();
		} catch (Exception e) {
			log.error("Error occured while getting Assignment by Assignment ID: " + e.getMessage());
			e.printStackTrace();
		}
		return assignment;
	}

	/**
	 * Get assignment for specific objectId and personId.
	 * 
	 * @param objectId
	 * @param userId
	 * @return PnAssignment.
	 */
	public PnAssignment getPersonAssignmentForObject(Integer objectId, Integer personId) {
		PnAssignment assignment = new PnAssignment();
		try {
			String sql = " FROM PnAssignment a  WHERE a.comp_id.objectId = :objectId " + " AND a.comp_id.personId = :personId " + " AND a.recordStatus = 'A' "
					+ " AND a.statusId != '" + AssignmentStatus.REJECTED.getID() + "'";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("objectId", objectId);
			query.setInteger("personId", personId);
			assignment = (PnAssignment) query.uniqueResult();
		} catch (Exception e) {
			log.error("Error occured while getting Assignment by personid and objectid: " + e.getMessage());
			e.printStackTrace();
		}
		return assignment;
	}

	@Override
	public List<PnAssignment> getAssignmentList(Integer projectId) {
		List<PnAssignment> result = new ArrayList<PnAssignment>();
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			Criteria criteria = session.createCriteria(PnAssignment.class);
			criteria.add(Restrictions.eq("comp_id.spaceId", projectId));
			result = criteria.list();
			session.close();
		} catch (Exception e) {
			log.error("Error occurred while getting the list of assignments " + e.getMessage());
		}
		return result;
	}

	@Override
	public List<PnAssignment> getAssignmentsByObjectId(Integer objectID) {
		List<PnAssignment> result = null;
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			result = session.createCriteria(PnAssignment.class).add(Restrictions.eq("comp_id.objectId", objectID)).list();
			session.close();
		} catch (Exception e) {
			log.error("Error occurred while getting the list of assignments " + e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

}
