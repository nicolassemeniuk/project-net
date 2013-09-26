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
package net.project.hibernate.service;

import java.util.List;

import net.project.hibernate.model.PnTask;

/**
 * @author
 * 
 */
public interface IPnTaskService {

	public PnTask getTaskById(Integer id);

	public PnTask getTaskDetailsById(Integer id);

	public List<PnTask> getTasksByProjectId(Integer projectId);

	/**
	 * Obtain all completed tasks by Project Id. A task is considered completed
	 * based on a certain percent of completion. This is a property that is set
	 * on the database (prm.global.taskcompletedpercentage).
	 * 
	 * @param projectId
	 *            the id from the project.
	 * @return a list of tasks.
	 */
	public List<PnTask> getCompletedTasksByProjectId(Integer projectId);

	/**
	 * Get all milestones for project
	 * 
	 * @param projectId
	 *            selected project id
	 * @param onlyWithoutPhases
	 *            if true only milestones that does not belong to phase will be
	 *            returned, if false all milestones will be returned
	 * @return list of milestone tasks
	 */
	public List<PnTask> getProjectMilestones(Integer projectId, boolean onlyWithoutPhases);

	public Integer getProjectByTaskId(Integer taskId);

	public PnTask getTaskWithRecordStatus(Integer taskId);

}
