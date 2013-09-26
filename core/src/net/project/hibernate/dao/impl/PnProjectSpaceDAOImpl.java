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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import net.project.hibernate.dao.IPnProjectSpaceDAO;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.model.PnSpaceHasSpace;
import net.project.hibernate.model.PnTask;
import net.project.hibernate.model.project_space.ObjectChanged;
import net.project.hibernate.model.project_space.ProjectChanges;
import net.project.hibernate.model.project_space.ProjectPhase;
import net.project.hibernate.model.project_space.ProjectSchedule;
import net.project.project.PercentCalculationMethod;
import net.project.schedule.TaskType;
import net.project.space.SpaceRelationship;
import net.project.util.NumberUtils;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnProjectSpaceDAOImpl extends AbstractHibernateAnnotatedDAO<PnProjectSpace, Integer> implements IPnProjectSpaceDAO {

	private static Logger log = Logger.getLogger(PnProjectSpaceDAOImpl.class);

	public PnProjectSpaceDAOImpl() {
		super(PnProjectSpace.class);
	}

	@SuppressWarnings("unchecked")
	public List<PnProjectSpace> getProjectsByUserId(Integer userId) {
		List<PnProjectSpace> projectList = new ArrayList<PnProjectSpace>();
		try {

			// those three fields are excluded from this query, gd is
			// pn_global_domain table
			// gd2.code_name as status, gd3.code_name as color, gd3.code_url as
			// color_image_url,

			String sql = "select new PnProjectSpace(p.projectId, p.projectDesc, "
					+ " p.projectName, p.statusCodeId, p.colorCodeId, p.isSubproject, p.percentComplete, p.startDate, p.endDate, p.recordStatus, "
					+ " p.defaultCurrencyCode, p.sponsorDesc, p.improvementCodeId, p.currentStatusDescription, p.financialStatusColorCodeId, "
					+ " p.financialStatusImpCodeId, p.budgetedTotalCostValue, p.budgetedTotalCostCc, p.currentEstTotalCostValue, p.currentEstTotalCostCc, "
					+ " p.actualToDateCostValue, p.actualToDateCostCc, p.estimatedRoiCostValue, p.estimatedRoiCostCc, p.costCenter, p.scheduleStatusColorCodeId, "
					+ " p.scheduleStatusImpCodeId, p.resourceStatusColorCodeId, p.resourceStatusImpCodeId, p.priorityCodeId, p.riskRatingCodeId, p.visibilityId, "
					+ " p.percentCalculationMethod) FROM PnProjectSpace p, PnSpaceHasSpace shs WHERE "
					+ " shs.comp_id.parentSpaceId = :userId and shs.comp_id.childSpaceId = p.projectId and  shs.relationshipChildToParent = :childToParent  and "
					+ " shs.relationshipParentToChild = :parentToChild and  p.recordStatus = :recordStatus  order by p.projectName";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql).setInteger("userId", userId);
			query.setString("childToParent", "owned_by");
			query.setString("parentToChild", "owns");
			query.setString("recordStatus", "A");
			projectList = query.list();

		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return projectList;
	}

	@SuppressWarnings("unchecked")
	public List<PnProjectSpace> getProjectsByMemberId(Integer userId) {
		List<PnProjectSpace> result = new ArrayList<PnProjectSpace>();
		try {

			// those three fields are excluded from this query, gd is
			// pn_global_domain table
			// gd2.code_name as status, gd3.code_name as color, gd3.code_url as
			// color_image_url,

			String sql = "select new PnProjectSpace(p.projectId, p.projectDesc, "
					+ " p.projectName, p.statusCodeId, p.colorCodeId, p.isSubproject, p.percentComplete, p.startDate, p.endDate, p.recordStatus, "
					+ " p.defaultCurrencyCode, p.sponsorDesc, p.improvementCodeId, p.currentStatusDescription, p.financialStatusColorCodeId, "
					+ " p.financialStatusImpCodeId, p.budgetedTotalCostValue, p.budgetedTotalCostCc, p.currentEstTotalCostValue, p.currentEstTotalCostCc, "
					+ " p.actualToDateCostValue, p.actualToDateCostCc, p.estimatedRoiCostValue, p.estimatedRoiCostCc, p.costCenter, p.scheduleStatusColorCodeId, "
					+ " p.scheduleStatusImpCodeId, p.resourceStatusColorCodeId, p.resourceStatusImpCodeId, p.priorityCodeId, p.riskRatingCodeId, p.visibilityId, "
					+ " p.percentCalculationMethod) " + " FROM PnProjectSpace p, PnPortfolioHasSpace phs, PnPerson pp "
					+ " WHERE  p.recordStatus = :recordStatus and  p.projectId = phs.comp_id.spaceId and "
					+ " phs.comp_id.portfolioId = pp.membershipPortfolioId and pp.personId = :userId  order by p.projectName";

			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("userId", userId);
			query.setString("recordStatus", "A");
			result = query.list();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<PnProjectSpace> getProjectsByBusinessId(Integer businessId) {
		List<PnProjectSpace> result = new ArrayList<PnProjectSpace>();
		try {
			String sql = "select new PnProjectSpace(p.projectId, p.projectDesc, "
					+ " p.projectName, p.statusCodeId, p.colorCodeId, p.isSubproject, p.percentComplete, p.startDate, p.endDate, p.recordStatus, "
					+ " p.defaultCurrencyCode, p.sponsorDesc, p.improvementCodeId, p.currentStatusDescription, p.financialStatusColorCodeId, "
					+ " p.financialStatusImpCodeId, p.budgetedTotalCostValue, p.budgetedTotalCostCc, p.currentEstTotalCostValue, p.currentEstTotalCostCc, "
					+ " p.actualToDateCostValue, p.actualToDateCostCc, p.estimatedRoiCostValue, p.estimatedRoiCostCc, p.costCenter, p.scheduleStatusColorCodeId, "
					+ " p.scheduleStatusImpCodeId, p.resourceStatusColorCodeId, p.resourceStatusImpCodeId, p.priorityCodeId, p.riskRatingCodeId, p.visibilityId, "
					+ " p.percentCalculationMethod) " + " FROM PnProjectSpace p, PnPortfolioHasSpace phs, PnSpaceHasPortfolio pshp"
					+ " WHERE  p.recordStatus = :recordStatus and p.projectId = phs.comp_id.spaceId and " + " phs.comp_id.portfolioId = pshp.comp_id.portfolioId and"
					+ " pshp.comp_id.spaceId = :businessId order by p.projectName";

			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("businessId", businessId);
			query.setString("recordStatus", "A");
			result = query.list();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<PnProjectSpace> getProjectsVisbleByUser(Integer userId) {
		List<PnProjectSpace> projectList = new ArrayList<PnProjectSpace>();

		/**
		 * get project where user is member of project space and project with
		 * visibility set to global (include everyone set to 1)
		 */
		String sql = "select new PnProjectSpace(p.projectId, p.projectDesc, "
				+ " p.projectName, p.statusCodeId, p.colorCodeId, p.isSubproject, p.percentComplete, p.startDate, p.endDate, p.recordStatus, "
				+ " p.defaultCurrencyCode, p.sponsorDesc, p.improvementCodeId, p.currentStatusDescription, p.financialStatusColorCodeId, "
				+ " p.financialStatusImpCodeId, p.budgetedTotalCostValue, p.budgetedTotalCostCc, p.currentEstTotalCostValue, p.currentEstTotalCostCc, "
				+ " p.actualToDateCostValue, p.actualToDateCostCc, p.estimatedRoiCostValue, p.estimatedRoiCostCc, p.costCenter, p.scheduleStatusColorCodeId, "
				+ " p.scheduleStatusImpCodeId, p.resourceStatusColorCodeId, p.resourceStatusImpCodeId, p.priorityCodeId, p.riskRatingCodeId, p.visibilityId, "
				+ " p.percentCalculationMethod) " + " FROM PnProjectSpace p, PnPortfolioHasSpace phs, PnPerson pp "
				+ " WHERE  p.recordStatus = :recordStatus and  p.projectId = phs.comp_id.spaceId and "
				+ " phs.comp_id.portfolioId = pp.membershipPortfolioId and ((pp.person_id = :userId) or (p.includesEveryone = 1)) order by p.projectName";

		Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
		query.setInteger("userId", userId);
		query.setString("recordStatus", "A");
		projectList = query.list();

		/**
		 * project having "visible to member of the owning business" set and the
		 * user being a member of that business
		 */
		sql = "select new PnProjectSpace(p.projectId, p.projectDesc, "
				+ " p.projectName, p.statusCodeId, p.colorCodeId, p.isSubproject, p.percentComplete, p.startDate, p.endDate, p.recordStatus, "
				+ " p.defaultCurrencyCode, p.sponsorDesc, p.improvementCodeId, p.currentStatusDescription, p.financialStatusColorCodeId, "
				+ " p.financialStatusImpCodeId, p.budgetedTotalCostValue, p.budgetedTotalCostCc, p.currentEstTotalCostValue, p.currentEstTotalCostCc, "
				+ " p.actualToDateCostValue, p.actualToDateCostCc, p.estimatedRoiCostValue, p.estimatedRoiCostCc, p.costCenter, p.scheduleStatusColorCodeId, "
				+ " p.scheduleStatusImpCodeId, p.resourceStatusColorCodeId, p.resourceStatusImpCodeId, p.priorityCodeId, p.riskRatingCodeId, p.visibilityId, "
				+ " p.percentCalculationMethod) " + " from PnBusiness b, PnSpaceHasPerson shp, PnSpaceHasPortfolio pshp, " + " PnPortfolioHasSpace phs, PnProjectSpace p "
				+ " where b.businessId = shp.comp_id.spaceId  and b.recordStatus = :recordStatus and "
				+ " shp.comp_id.personId = :userId  and b.businessId = pshp.comp_id.spaceId  and phs.comp_id.portfolioId = pshp.comp_id.portfolioId and "
				+ " p.projectId = phs.comp_id.spaceId and p.recordStatus = :recordStatus and p.visibilityId = 200 order by p.projectName";

		Query queryBiznisProjects = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
		queryBiznisProjects.setInteger("userId", userId);
		queryBiznisProjects.setString("recordStatus", "A");
		List<PnProjectSpace> biznisProjectList = queryBiznisProjects.list();

		/**
		 * TODO include projects that belongs to business where user is space
		 * admin if needed
		 */

		/**
		 * create union of this two list
		 */
		for (PnProjectSpace project : biznisProjectList) {
			if (!projectList.contains(project)) {
				projectList.add(project);
			}
		}

		return projectList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.project.hibernate.dao.IPnProjectSpaceDAO#getProjectPhasesAndMilestones
	 * (java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public List getProjectPhasesAndMilestones(Integer projectId) {
		Connection connection = null;
		PreparedStatement stmt = null;
		List<ProjectPhase> phases = new ArrayList<ProjectPhase>();
		try {
			// used clasic jdbc query beacause hiberante poor support for join
			// hibernate does not support ON keyword in JOIN so only posible
			// join
			// between tables is one specified in mapping file
			connection = getHibernateTemplate().getSessionFactory().getCurrentSession().connection();
			String sql = " SELECT ph.phase_id ,ph.phase_name, ph.phase_desc, ph.start_date, ph.end_date, ph.status_id, ph.sequence, "
					+ " ph.entered_percent_complete, ph.progress_reporting_method, gd.code_name, g.gate_name, g.gate_date,"
					+ " tt.task_id, tt.task_name, tt.duration, tt.date_start, tt.date_finish, tt.work_percent_complete, tt.work_ms, " + " tt.work_complete_ms, tt.is_milestone "
					+ " FROM pn_space_has_process shp, pn_process p, pn_global_domain gd, pn_phase ph  "
					+ " LEFT JOIN (SELECT t.task_id, t.task_name, t.duration, t.date_start, t.date_finish, t.work_percent_complete, t.work_ms, "
					+ "           t.work_complete_ms, t.is_milestone, pht.phase_id "
					+ "           FROM pn_phase_has_task pht, pn_task t, pn_plan_has_task plht, pn_space_has_plan shp "
					+ "           where pht.task_id = t.task_id AND plht.task_id = t.task_id " + "            AND shp.plan_id = plht.plan_id AND shp.space_id = ? "
					+ "            and  t.record_status = 'A') tt ON ph.phase_id = tt.phase_id " + " LEFT JOIN pn_gate g ON ph.phase_id = g.phase_id "
					+ " WHERE shp.space_Id = ? AND shp.process_Id = p.process_Id " + " AND p.process_Id = ph.process_Id AND ph.record_Status = 'A' AND p.record_Status = 'A' AND "
					+ " ph.status_id = gd.code AND gd.table_name = 'pn_phase' AND gd.column_name = 'status_id'" + " ORDER BY ph.phase_id";

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, projectId);
			stmt.setInt(2, projectId);
			ResultSet rs = stmt.executeQuery();
			if (rs != null) {
				int phaseId = 0;
				ProjectPhase projectPhase = null;
				Date minStartDate = null;
				Date maxFinishDate = null;
				Long workSum = 0L;
				Long workCompleteSum = 0L;
				while (rs.next()) {
					if (phaseId != rs.getInt("phase_id")) {
						if (projectPhase != null && projectPhase.getProgressReportingMethod().equals(PercentCalculationMethod.SCHEDULE.getID())) {
							projectPhase.setStartDate(minStartDate);
							projectPhase.setEndDate(maxFinishDate);
							if (workSum == 0) {
								projectPhase.setPercentComplete(0);
							} else {
								projectPhase.setPercentComplete(Integer.valueOf(NumberUtils.round(((double)workCompleteSum * 100 / workSum), 0)));
							}
						}
						phaseId = rs.getInt(1);
						minStartDate = null;
						maxFinishDate = null;
						workSum = 0L;
						workCompleteSum = 0L;
						projectPhase = new ProjectPhase(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getTimestamp(4), rs.getTimestamp(5), rs.getInt(6), rs.getInt(7), rs
								.getInt(8), rs.getString(9), rs.getString(10), rs.getString(11), rs.getTimestamp(12));
						minStartDate =	projectPhase.getStartDate();
						maxFinishDate = projectPhase.getEndDate();
						phases.add(projectPhase);
					}
					if (rs.getObject("task_id") != null) {
						workSum = workSum + rs.getLong(19) / 1000;
						workCompleteSum = workCompleteSum + rs.getLong(20) / 1000;
						if (minStartDate != null) {
							minStartDate = minStartDate.before(rs.getTimestamp(16)) ? minStartDate : rs.getTimestamp(16);
						} else {
							minStartDate = rs.getTimestamp(16);
						}
						if (maxFinishDate != null) {
							maxFinishDate = maxFinishDate.after(rs.getTimestamp(17)) ? maxFinishDate : rs.getTimestamp(17);
						} else {
							maxFinishDate = rs.getTimestamp(17);
						}
						if (rs.getInt(21) == 1) {
							PnTask task = new PnTask(rs.getInt(13), rs.getString(14), rs.getBigDecimal(15), rs.getTimestamp(16), rs.getTimestamp(17), rs.getBigDecimal(18));
							projectPhase.getMilestones().add(task);
						}
						projectPhase.setStartDate(minStartDate);
						projectPhase.setEndDate(maxFinishDate);
						if (projectPhase != null && !projectPhase.getProgressReportingMethod().equals(PercentCalculationMethod.MANUAL.getID())){
							if (workSum == 0) {
								projectPhase.setPercentComplete(0);
							} else {
								projectPhase.setPercentComplete(Integer.valueOf(NumberUtils.round((workCompleteSum * 100 / (double)workSum), 0)));
							}
						}
					}
				}	
				if (PercentCalculationMethod.SCHEDULE.getID().equals(projectPhase.getProgressReportingMethod())) {
					if (workSum == 0 && projectPhase.getPercentComplete() == null) {
						projectPhase.setPercentComplete(0);
					} else {
						projectPhase.setPercentComplete(Integer.valueOf(NumberUtils.round((workCompleteSum * 100 / (double)workSum), 0)));
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (connection != null) {
					connection.close();
					connection = null;
				}
			} catch (SQLException e) {
				log.error(e.getMessage());
			}
		}

		return phases;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.project.hibernate.dao.IPnProjectSpaceDAO#getProjectSchedule(java.
	 * lang.Integer)
	 */
	public ProjectSchedule getProjectSchedule(Integer projectId) {
		ProjectSchedule projectSchedule = new ProjectSchedule();
		try {
			Calendar cal = GregorianCalendar.getInstance();
			Date today = cal.getTime();
			cal.add(Calendar.DATE, 7);
			Date dateNextWeek = cal.getTime();
			cal.setTime(today);
			cal.add(Calendar.DATE, -7);
			// Date dateLastWeek = cal.getTime();
			cal.setTime(today);
			cal.add(Calendar.DATE, -1);
			Date yesterday = cal.getTime();

			String sql = " SELECT COUNT(t.taskId)  FROM PnSpaceHasPlan shp, PnPlan p, PnPlanHasTask pht, PnTask t "
					+ "	WHERE shp.comp_id.spaceId = :projectId AND shp.comp_id.planId = p.planId AND p.planId = pht.comp_id.planId"
					+ " AND pht.comp_id.taskId = t.taskId AND  t.workPercentComplete < 100 and t.dateFinish < :yesterday " + " AND t.recordStatus = 'A'";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("projectId", projectId);
			query.setDate("yesterday", yesterday);
			int numOfLateTasks = ((Long) query.iterate().next()).intValue();

			cal.add(Calendar.DATE, 8);
			sql = " SELECT COUNT(*) FROM PnSpaceHasPlan shp, PnPlan p, PnPlanHasTask pht, PnTask t "
					+ "	WHERE shp.comp_id.spaceId = :projectId AND shp.comp_id.planId = p.planId AND p.planId = pht.comp_id.planId"
					+ " AND pht.comp_id.taskId = t.taskId AND  t.workPercentComplete < 100 and t.dateFinish < :date AND t.dateFinish > :today" + " AND t.recordStatus = 'A'";
			query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("projectId", projectId);
			query.setDate("today", today);
			query.setDate("date", dateNextWeek);
			int numOfTasksCommingDue = ((Long) query.iterate().next()).intValue();

			cal.add(Calendar.DATE, 8);
			sql = " SELECT COUNT(*) FROM PnSpaceHasPlan shp, PnPlan p, PnPlanHasTask pht, PnTask t "
					+ "	WHERE shp.comp_id.spaceId = :projectId AND shp.comp_id.planId = p.planId AND p.planId = pht.comp_id.planId"
					+ " AND pht.comp_id.taskId = t.taskId AND  t.workPercentComplete = 100 " + " AND t.recordStatus = 'A'";
			query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("projectId", projectId);
			int numOfTasksCompleted = ((Long) query.iterate().next()).intValue();

			sql = " SELECT COUNT(*) FROM PnSpaceHasPlan shp, PnPlan p, PnPlanHasTask pht, PnTask t "
					+ "	WHERE shp.comp_id.spaceId = :projectId AND shp.comp_id.planId = p.planId AND p.planId = pht.comp_id.planId" + " AND pht.comp_id.taskId = t.taskId " 
					+ " AND t.taskType != '" + TaskType.SUMMARY.getID() + "' "
					// + " AND t.workPercentComplete < 100 " - temporary
					// disabled to include count of finshed unassigned tasks
					+ " AND NOT EXISTS  (SELECT a.comp_id.objectId FROM PnAssignment a WHERE a.comp_id.objectId = t.taskId AND a.recordStatus = 'A')" + " AND t.recordStatus = 'A'";
			query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("projectId", projectId);
			int numOfUnassignedTasks = ((Long) query.iterate().next()).intValue();

			sql = " SELECT ps.startDate, ps.endDate, p.dateStart, p.dateEnd " + " FROM  PnSpaceHasPlan shp, PnPlan p, PnProjectSpace ps"
					+ " WHERE ps.projectId = :projectId AND ps.projectId = shp.comp_id.spaceId AND shp.comp_id.planId = p.planId ";
			query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("projectId", projectId);
			Iterator results = query.list().iterator();
			while (results.hasNext()) {
				Object[] row = (Object[]) results.next();
				projectSchedule.setPlannedStart((Date) row[0]);
				projectSchedule.setPlannedFinish((Date) row[1]);
				projectSchedule.setActualStart((Date) row[2]);
				projectSchedule.setActualFinish((Date) row[3]);
			}

			projectSchedule.setNumberOfLateTasks(numOfLateTasks);
			projectSchedule.setNumberOfTaskComingDue(numOfTasksCommingDue);
			projectSchedule.setNumberOfCompletedTasks(numOfTasksCompleted);
			projectSchedule.setNumberOfUnassignedTasks(numOfUnassignedTasks);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return projectSchedule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.project.hibernate.dao.IPnProjectSpaceDAO#getProjectChanges(java.lang
	 * .Integer)
	 */
	@SuppressWarnings("unchecked")
	public ProjectChanges getProjectChanges(Integer projectId, Integer numberOfDays) {
		ProjectChanges projectChanges = new ProjectChanges();		
		Connection connection = null;
		try {
			Calendar cal = GregorianCalendar.getInstance();
			cal.add(Calendar.DATE, -1 * numberOfDays);

			String sql = " SELECT new net.project.hibernate.model.project_space.ObjectChanged(dg.discussionGroupId, dg.discussionGroupName, COUNT(p.comp_id.postId)) "
					+ " FROM PnDiscussionGroup dg, PnObjectHasDiscussion ohd, PnPost p"
					+ " WHERE ohd.comp_id.objectId = :projectId AND ohd.comp_id.discussionGroupId = dg.discussionGroupId"
					+ " AND p.comp_id.discussionGroupId = dg.discussionGroupId AND dg.recordStatus = 'A' " + " AND p.recordStatus = 'A' AND p.datePosted > :date "
					+ " GROUP BY dg.discussionGroupId, dg.discussionGroupName ";

			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("projectId", projectId);
			query.setDate("date", cal.getTime());

			List<ObjectChanged> discussionList = query.list();

			sql = " SELECT new net.project.hibernate.model.project_space.ObjectChanged(d.docId, d.docName, dv.dateModified) "
					+ " FROM PnDocSpace ds, PnDocSpaceHasContainer dshc, PnDocContainerHasObject dcho, PnSpaceHasDocSpace shds, "
					+ " PnDocVersion dv, PnDocument d, PnDocContainer dc"
					+ " WHERE shds.comp_id.spaceId = :projectId AND dv.dateModified > :date AND d.currentVersionId = dv.docVersionId AND d.recordStatus = 'A'"
					+ " AND dcho.comp_id.objectId = d.docId AND dcho.comp_id.docContainerId = dshc.comp_id.docContainerId "
					+ " AND dshc.comp_id.docSpaceId = ds.docSpaceId AND ds.docSpaceId = shds.comp_id.docSpaceId AND dc.docContainerId = dcho.comp_id.docContainerId AND dc.isHidden = 0"
					+ " ORDER BY dv.dateModified desc ";
			query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("projectId", projectId);
			query.setDate("date", cal.getTime());
			List<ObjectChanged> documentList = query.list();

			sql = " SELECT c.classId, c.className , c.masterTableName, c.classAbbreviation, sfv.childIds "  
	        + " FROM PnClassType ct , PnClass c, PnSpaceHasClass shc LEFT JOIN shc.pnSharedFormsVisibility sfv  "
			+ " WHERE shc.comp_id.spaceId = :projectId AND (shc.isOwner = 1 OR shc.visible = 1) AND c.classId = shc.comp_id.classId "
			+ " AND ct.classTypeId = c.pnClassType.classTypeId AND ct.classTypeName = 'form' AND c.recordStatus = 'A' AND c.isSystemClass = 0 "; 						
			query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("projectId", projectId);
			Iterator formsItr = query.list().iterator();
			List<ObjectChanged> formsList = new ArrayList<ObjectChanged>();
			connection = getHibernateTemplate().getSessionFactory().getCurrentSession().connection();

			StringBuffer dateString = new StringBuffer();
			dateString.append("TO_DATE('");
			dateString.append(new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US).format(cal.getTime()));
			dateString.append("', 'MM/DD/YYYY HH24:MI')");

			while (formsItr.hasNext()) {
				Object[] row = (Object[]) formsItr.next();
				String formAbbreviation = (String) row[3];
				String childIds = (String) row[4];

				sql = " SELECT crc from pn_class_instance where pn_class_instance.class_id=" + row[0] + " and crc >= " + dateString.toString() + " and record_status='A' ";
				if (childIds != null && childIds.length() > 0)
			 	 sql = sql + " AND space_id IN (" + childIds+")";
				PreparedStatement stmt = connection.prepareStatement(sql);

				ResultSet rs = stmt.executeQuery();
				if (rs != null) {
					while (rs.next()) {
						ObjectChanged objChanged = new ObjectChanged((Integer) row[0], formAbbreviation, rs.getDate(1));
						formsList.add(objChanged);
					}
				}
			}

			projectChanges.setDiscussions(discussionList);
			projectChanges.setDocuments(documentList);
			projectChanges.setForms(formsList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
					connection = null;
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return projectChanges;
	}

	public List<PnProjectSpace> getAssignedProjectsByResource(Integer businessId, Integer resourceId, Date startDate, Date endDate) {
		List<PnProjectSpace> projects = new ArrayList<PnProjectSpace>();

		String sql = " select distinct ps.projectId, ps.projectName " + " from  PnAssignment a, PnProjectSpace ps, PnSpaceHasSpace pshs "
				+ " where a.comp_id.spaceId = ps.projectId and pshs.comp_id.childSpaceId = ps.projectId and " + " a.recordStatus = 'A' and ps.recordStatus = 'A' "
				+ " and a.comp_id.personId = :personId" + " and (( a.startDate between :startDate and :endDate " + " or a.endDate between :startDate and :endDate ) "
				+ " OR (a.startDate <= :startDate AND a.endDate >= :endDate ))";

		if (businessId != null) {
			sql += " and pshs.comp_id.parentSpaceId = :businessId ";
		} else {
			sql += " and pshs.comp_id.childSpaceId  " + " IN ( select pshp.comp_id.spaceId from  PnSpaceHasPerson pshp  where " + " pshp.comp_id.personId = :personId ) ";
		}

		Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
		if (businessId != null) {
			query.setInteger("businessId", businessId);
		}
		query.setInteger("personId", resourceId);
		query.setDate("startDate", startDate);
		query.setDate("endDate", endDate);

		Iterator results = query.list().iterator();
		while (results.hasNext()) {
			Object[] row = (Object[]) results.next();
			PnProjectSpace project = new PnProjectSpace((Integer) row[0], (String) row[1]);
			projects.add(project);
		}
		return projects;
	}

	@SuppressWarnings("unchecked")
	public PnProjectSpace getProjectSpaceDetails(Integer projectId) {
		PnProjectSpace pnProjectSpace = null;
		try {
			String sql = "from PnProjectSpace p where p.projectId=:objectId";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("objectId", projectId);
			List<PnProjectSpace> result = query.list();
			if (result.size() > 0) {
				pnProjectSpace = result.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pnProjectSpace;
	}

	@SuppressWarnings("unchecked")
	public List<PnProjectSpace> getAllProjects() {
		List<PnProjectSpace> projectList = new ArrayList<PnProjectSpace>();
		try {

			// those three fields are excluded from this query, gd is
			// pn_global_domain table
			// gd2.code_name as status, gd3.code_name as color, gd3.code_url as
			// color_image_url,

			String sql = "select new PnProjectSpace(p.projectId, p.projectDesc, "
					+ " p.projectName, p.statusCodeId, p.colorCodeId, p.isSubproject, p.percentComplete, p.startDate, p.endDate, p.recordStatus, "
					+ " p.defaultCurrencyCode, p.sponsorDesc, p.improvementCodeId, p.currentStatusDescription, p.financialStatusColorCodeId, "
					+ " p.financialStatusImpCodeId, p.budgetedTotalCostValue, p.budgetedTotalCostCc, p.currentEstTotalCostValue, p.currentEstTotalCostCc, "
					+ " p.actualToDateCostValue, p.actualToDateCostCc, p.estimatedRoiCostValue, p.estimatedRoiCostCc, p.costCenter, p.scheduleStatusColorCodeId, "
					+ " p.scheduleStatusImpCodeId, p.resourceStatusColorCodeId, p.resourceStatusImpCodeId, p.priorityCodeId, p.riskRatingCodeId, p.visibilityId, "
					+ " p.percentCalculationMethod) FROM PnProjectSpace p, PnSpaceHasSpace shs WHERE "
					+ " shs.comp_id.childSpaceId = p.projectId and  shs.relationshipChildToParent = :childToParent  and "
					+ " shs.relationshipParentToChild = :parentToChild and  p.recordStatus = :recordStatus  order by p.projectName";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql).setString("recordStatus", "A");
			query.setString("childToParent", "owned_by");
			query.setString("parentToChild", "owns");
			projectList = query.list();

		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return projectList;
	}

	@SuppressWarnings("unchecked")
	public List<PnProjectSpace> getSubProjectsByProejctId(Integer projectId) {
		List<PnProjectSpace> result = new ArrayList<PnProjectSpace>();
		try {
			String sql = "select new PnProjectSpace(p.projectId, p.projectDesc, "
					+ " p.projectName, p.statusCodeId, p.colorCodeId, p.isSubproject, p.percentComplete, p.startDate, p.endDate, p.recordStatus, "
					+ " p.defaultCurrencyCode, p.sponsorDesc, p.improvementCodeId, p.currentStatusDescription, p.financialStatusColorCodeId, "
					+ " p.financialStatusImpCodeId, p.budgetedTotalCostValue, p.budgetedTotalCostCc, p.currentEstTotalCostValue, p.currentEstTotalCostCc, "
					+ " p.actualToDateCostValue, p.actualToDateCostCc, p.estimatedRoiCostValue, p.estimatedRoiCostCc, p.costCenter, p.scheduleStatusColorCodeId, "
					+ " p.scheduleStatusImpCodeId, p.resourceStatusColorCodeId, p.resourceStatusImpCodeId, p.priorityCodeId, p.riskRatingCodeId, p.visibilityId, "
					+ " p.percentCalculationMethod) FROM PnProjectSpace p " + " WHERE  p.projectId in (  select distinct shs.comp_id.childSpaceId from PnSpaceHasSpace shs "
					+ " where shs.parentSpaceType = :parentSpaceType and shs.comp_id.parentSpaceId = :projectId and shs.recordStatus = :recordStatus)";

			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("projectId", projectId);
			query.setString("recordStatus", "A");
			query.setString("parentSpaceType", "project");
			result = query.list();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public PnProjectSpace getWikiIdByProjectId(Integer projectId) {
		PnProjectSpace pn = null;
		try {
			if (log.isDebugEnabled()) {
				log.debug(" selecting wiki id for projectId:" + projectId);
			}
			String sql = "select new PnProjectSpace(p.projectId, p.wikiId) from PnProjectSpace p where p.projectId = :projectId";
			List result = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql).setInteger("projectId", projectId).list();
			if (result != null && result.size() > 0) {
				pn = (PnProjectSpace) result.get(0);
				if (log.isDebugEnabled()) {
					log.debug(" selected wikiId:" + pn.getWikiId());
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return pn;
	}

	@SuppressWarnings("unchecked")
	public List<PnProjectSpace> getActiveProjectIds() {
		try {
			String sql = "select new PnProjectSpace(p.projectId) from PnProjectSpace p where p.recordStatus = 'A' ";
			List<PnProjectSpace> result = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql).list();
			return result;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.project.hibernate.dao.IPnProjectSpaceDAO#getProjectDetailsByWeblogEntryId
	 * (java.lang.Integer)
	 */
	public PnProjectSpace getProjectDetailsByWeblogEntryId(Integer weblogEntryId) {
		PnProjectSpace pnProject = null;
		try {
			String sql = "select new PnProjectSpace(ps.projectId, ps.projectName, ps.recordStatus) " + " from PnProjectSpace ps, PnWeblogEntry we, PnWeblog w "
					+ " where we.weblogEntryId = :weblogEntryId AND we.pnWeblog.weblogId = w.weblogId AND w.spaceId = ps.projectId";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("weblogEntryId", weblogEntryId);
			pnProject = (PnProjectSpace) query.uniqueResult();
		} catch (Exception e) {
			log.error("Error occurred while getting project details by weblog entry id in PnProjectSpaceDAO: " + e.getMessage());
		}
		return pnProject;
	}

	public boolean doesProjectExist(Integer projectId) {
		boolean projectExists = false;
		try {
			String sql = "select count(*) from PnProjectSpace ps where ps.projectId = :projectId";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("projectId", projectId);
			Integer numberOfProjects = ((Long) query.iterate().next()).intValue();
			projectExists = numberOfProjects == 1;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return projectExists;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnProjectSpaceDAO#getProjectDetailsWithRecordStatus(java.lang.Integer)
	 */
	public PnProjectSpace getProjectDetailsWithRecordStatus(Integer projectId) {
		PnProjectSpace pnProject = null;
		try {
			String sql = " select new PnProjectSpace(ps.projectId, ps.projectName, ps.recordStatus) " + 
						 " from PnProjectSpace ps " +
					     " where ps.projectId = :projectId ";
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("projectId", projectId);
			pnProject = (PnProjectSpace) query.uniqueResult();
		} catch (Exception e) {
			log.error("Error occurred while getting project details by projectId in PnProjectSpaceDAO: " + e.getMessage());
		}
		return pnProject;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnProjectSpaceDAO#getProjectsByUserIdWithParentProjectId(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public List<PnProjectSpace> getProjectsByUserIdWithParentProjectId(Integer userId){
		List<PnProjectSpace> result = new ArrayList<PnProjectSpace>();
		try{
			String sql = "select new PnProjectSpace(p.projectId, p.projectDesc, "
					+ " p.projectName, p.statusCodeId, p.colorCodeId, p.isSubproject, p.percentComplete, "
					+ " p.startDate, p.endDate, p.dateModified, p.recordStatus, p.defaultCurrencyCode, " 
		            + " p.sponsorDesc, p.improvementCodeId, p.currentStatusDescription, " 
		            + " p.financialStatusColorCodeId, p.financialStatusImpCodeId, p.budgetedTotalCostValue, " 
		            + " p.budgetedTotalCostCc, p.currentEstTotalCostValue, p.currentEstTotalCostCc, " 
		            + " p.actualToDateCostValue, p.actualToDateCostCc, " 
		            + " p.estimatedRoiCostValue, p.estimatedRoiCostCc, " 
		            + " p.costCenter, p.scheduleStatusColorCodeId, " 
		            + " p.scheduleStatusImpCodeId, p.resourceStatusColorCodeId, p.resourceStatusImpCodeId, " 
		            + " p.priorityCodeId, p.riskRatingCodeId, " 
		            + " p.visibilityId, p.percentCalculationMethod, " 
		            + " superspace.comp_id.parentSpaceId, superspace.parentSpaceType)" +
					"	from PnSpaceHasSpace ss, " +
					"	PnProjectSpace p, PnObject obj left join obj.pnSpaceHasSpace superspace " +
					"	with superspace.relationshipChildToParent in (:relationSuperSpace) " +
					"	where obj.objectId = p.projectId "+
					"	and ss.comp_id.childSpaceId = p.projectId " +
					"	and ss.relationshipChildToParent = :relationOwnedBy"+
					" 	and	p.projectId in " +
					"			(select pv.comp_id.projectId from PnPortfolioView pv, PnPerson ps " +
					"				where pv.comp_id.portfolioId = ps.membershipPortfolioId and ps.personId= :userId) order by p.projectName";
		
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("userId", userId);
			query.setString("relationOwnedBy", "owned_by");
			query.setString("relationSuperSpace", "subspace");
			result = query.list();
		}
		catch (Exception e) {
			log.error("Error occurred while getting project  by portfolioid in PnProjectSpaceDAO: " + e.getMessage());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnProjectSpaceDAO#isRootProject(java.lang.Integer)
	 */
	public boolean isRootProject(Integer spaceId) {
		List<PnSpaceHasSpace> result = null;
		try {
			String sql = " FROM PnSpaceHasSpace shs "
					   + " WHERE shs.comp_id.childSpaceId = :childSpaceId"
					   + " AND shs.parentSpaceType = :parentSpaceType";
			
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("childSpaceId", spaceId);
			query.setString("parentSpaceType", "project");
			result = query.list();
			
		} catch (Exception e) {
			log.error("Error occured while getting business "+e.getMessage());
			e.printStackTrace();
		}
		return result.isEmpty();
	}

	@Override
	public Float getBudgetedTotalCost(Integer spaceID) {
		Double budgetedTotalCost = new Double(0.0);
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			Criteria criteria = session.createCriteria(PnProjectSpace.class);
			criteria.add(Restrictions.eq("projectId", spaceID));
			criteria.add(Restrictions.ne("recordStatus", "D"));			
			criteria.setProjection(Property.forName("budgetedTotalCostValue"));
			budgetedTotalCost = (Double) criteria.uniqueResult();
			session.close();
		} catch (HibernateException e) {
			log.error(e.getMessage());
		}

		return budgetedTotalCost.floatValue();
	}

	@Override
	public String getDefaultCurrencyCode(Integer spaceId) {
		String defaultCurrencyCode = new String();
		
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			Criteria criteria = session.createCriteria(PnProjectSpace.class);
			criteria.add(Restrictions.eq("projectId", spaceId));
			criteria.add(Restrictions.ne("recordStatus", "D"));			
			criteria.setProjection(Property.forName("defaultCurrencyCode"));
			defaultCurrencyCode = (String) criteria.uniqueResult();
			session.close();
		} catch (HibernateException e) {
			log.error(e.getMessage());
		}
		
		return defaultCurrencyCode;
	}
}
