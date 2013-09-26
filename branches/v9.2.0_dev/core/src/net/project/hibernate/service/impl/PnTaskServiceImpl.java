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
package net.project.hibernate.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.project.hibernate.dao.IPnTaskDAO;
import net.project.hibernate.model.PnTask;
import net.project.hibernate.service.IPnTaskService;

/**
 * @author
 *
 */
@Service(value="pnTaskService")
public class PnTaskServiceImpl implements IPnTaskService {

	@Autowired
	private IPnTaskDAO pnTaskDAO;

	/**
	 * @param pnTaskDAO the pnTaskDAO to set
	 */
	public void setPnTaskDAO(IPnTaskDAO pnTaskDAO) {
		this.pnTaskDAO = pnTaskDAO;
	}
	
	public PnTask getTaskById(Integer id){
//		return pnTaskDAO.findByPimaryKey(id);
		return pnTaskDAO.getTaskById(id);
	}
	
	public List<PnTask> getTasksByProjectId(Integer projectId){
		return pnTaskDAO.getTasksByProjectId(projectId);
	}
	
	@Override
	public List<PnTask> getCompletedTasksByProjectId(Integer projectId) {
		return pnTaskDAO.getCompletedTasksByProjectId(projectId);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnTaskService#getTaskDetailsById(java.lang.Integer)
	 */
	public PnTask getTaskDetailsById(Integer id) {
		return pnTaskDAO.getTaskDetailsById(id);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnTaskService#getProjectMilestones(java.lang.Integer, boolean)
	 */
	public List<PnTask> getProjectMilestones(Integer projectId, boolean onlyWithoutPhases) {
		return pnTaskDAO.getProjectMilestones(projectId, onlyWithoutPhases);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnTaskService#getProjectByTaskId(java.lang.Integer)
	 */
	public Integer getProjectByTaskId(Integer taskId) {
		return pnTaskDAO.getProjectByTaskId(taskId);
	}
	
	public PnTask getTaskWithRecordStatus(Integer taskId){
		return pnTaskDAO.getTaskWithRecordStatus(taskId);
	}


	
}
