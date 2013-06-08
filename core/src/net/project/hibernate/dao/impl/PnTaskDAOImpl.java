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
/**
 * 
 */
package net.project.hibernate.dao.impl;

import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.hibernate.dao.IPnTaskDAO;
import net.project.hibernate.model.PnFinancialSpace;
import net.project.hibernate.model.PnTask;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author
 *
 */
@Transactional 
@Repository 
public class PnTaskDAOImpl extends AbstractHibernateAnnotatedDAO<PnTask, Integer> implements IPnTaskDAO{
	
	private static Logger log = Logger.getLogger(PnTask.class);
	
	public PnTaskDAOImpl() {
		super(PnTask.class);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnTaskDAO#getTasksByProjectId(java.lang.Integer)
	 */
	public List<PnTask> getTasksByProjectId(Integer projectId){
		List<PnTask> tasks = null;
		
		String sql = " SELECT new PnTask(t.taskId, t.taskName, t.taskDesc, t.duration, t.dateStart, t.dateFinish, t.workPercentComplete) " +
					" FROM PnTask t, PnPlan pp, PnPlanHasTask pht, PnSpaceHasPlan shp, PnProjectSpace ps " +
					" WHERE shp.comp_id.spaceId = ps.projectId AND shp.comp_id.planId = pp.planId " +
					" AND pht.comp_id.taskId = t.taskId AND pp.planId = pht.comp_id.planId " +
					" AND ps.projectId = :projectId AND t.recordStatus = 'A' order by t.seq ";
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("projectId", projectId);
			tasks = query.list();			
		} catch(Exception e){
			log.error("Error occurred while getting tasks by project id : "+e.getMessage());
			e.printStackTrace();
		}
		return tasks;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnTaskDAO#getTaskDetailsById(java.lang.Integer)
	 */
	public PnTask getTaskDetailsById(Integer taskId) {
		PnTask task = null;
		
		String sql = " SELECT new PnTask(t.taskId, t.taskName, t.taskDesc, t.duration, t.dateStart, t.dateFinish, t.workPercentComplete) " +
					 " FROM PnTask t WHERE t.taskId = :taskId AND t.recordStatus = 'A' ";
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("taskId", taskId);
			task = (PnTask) query.uniqueResult();			
		} catch(Exception e){
			log.error("Error occurred while getting task details by task id : "+e.getMessage());
			e.printStackTrace();
		}
		return task;		
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnTaskDAO#getProjectMilestones(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public List<PnTask> getProjectMilestones(Integer projectId, boolean onlyWithoutPhases) {
	List<PnTask> tasks = null;
		
		String sql = " SELECT new PnTask(t.taskId, t.taskName, t.taskDesc, t.duration, t.dateStart, t.dateFinish, t.workPercentComplete) " +
					" FROM PnTask t, PnPlan pp, PnPlanHasTask pht, PnSpaceHasPlan shp, PnProjectSpace ps " +
					" WHERE shp.comp_id.spaceId = ps.projectId AND shp.comp_id.planId = pp.planId " +
					" AND pht.comp_id.taskId = t.taskId AND pp.planId = pht.comp_id.planId " +
					" AND ps.projectId = :projectId AND t.recordStatus = 'A' AND t.isMilestone = 1 ";
		if (onlyWithoutPhases){
			sql = sql + " and not exists (select pht.comp_id.phaseId from PnPhaseHasTask pht where pht.comp_id.taskId = t.taskId) ";
		}
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("projectId", projectId);
			tasks = query.list();			
		} catch(Exception e){
			log.error("Error occurred while getting milestones by project id : "+e.getMessage());
			e.printStackTrace();
		}
		return tasks;
	}

	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnTaskDAO#getProjectByTaskId(java.lang.Integer)
	 */
	public Integer getProjectByTaskId(Integer taskId) {
		Integer projectId = null;
		
		String sql = " SELECT ps.projectId FROM PnPlan pp, PnPlanHasTask pht, PnProjectSpace ps, PnSpaceHasPlan shp "+
					 " WHERE pp.planId = pht.comp_id.planId AND shp.comp_id.planId = pht.comp_id.planId "+
					 " AND shp.comp_id.spaceId = ps.projectId AND pht.comp_id.taskId = :taskId ";
	
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("taskId", taskId);
			projectId = (Integer) query.uniqueResult();
		} catch (Exception e) {
			log.error("Error occurred while getting project id by task id : " + e.getMessage());
			e.printStackTrace();
		}
		return projectId;
	}
	
	/**
	 * Get Task name and record status.
	 * @param taskId
	 * @return PnTask
	 */
	public PnTask getTaskWithRecordStatus(Integer taskId) {
		PnTask task = null;
		
		String sql = " SELECT new PnTask(t.taskId, t.taskName, t.recordStatus) " +
					 " FROM PnTask t WHERE t.taskId = :taskId ";
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("taskId", taskId);
			task = (PnTask) query.uniqueResult();			
		} catch(Exception e){
			log.error("Error occurred while getting task details by task id : "+e.getMessage());
		}
		return task;		
	}

	@Override
	public List<PnTask> getCompletedTasksByProjectId(Integer projectId) {
		List<PnTask> tasks = null;
		Integer percentComplete = PropertyProvider.getInt("prm.global.taskcompletedpercentage");
		
		String sql = " SELECT new PnTask(t.taskId, t.taskName, t.taskDesc, t.duration, t.dateStart, t.dateFinish, t.workPercentComplete) " +
					" FROM PnTask t, PnPlan pp, PnPlanHasTask pht, PnSpaceHasPlan shp, PnProjectSpace ps " +
					" WHERE shp.comp_id.spaceId = ps.projectId AND shp.comp_id.planId = pp.planId " +
					" AND pht.comp_id.taskId = t.taskId AND pp.planId = pht.comp_id.planId " +
					" AND ps.projectId = :projectId AND t.recordStatus = 'A' " +
					" AND t.percentComplete = :percentComplete " +
					" ORDER BY t.seq ";
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("projectId", projectId);
			query.setInteger("percentComplete", percentComplete);
			tasks = query.list();			
		} catch(Exception e){
			log.error("Error occurred while getting tasks by project id : "+e.getMessage());
			e.printStackTrace();
		}
		return tasks;
	}
	
}
