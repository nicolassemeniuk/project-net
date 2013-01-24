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
package net.project.hibernate.model.project_space;

import java.util.Date;

public class ProjectSchedule {

	private Integer projectId;

	private Date plannedStart;

	private Date plannedFinish;

	private Date actualStart;

	private Date actualFinish;	
	
	private Integer numberOfLateTasks = 0;

	private Integer numberOfTaskComingDue = 0;

	private Integer numberOfUnassignedTasks = 0;

	private Integer numberOfCompletedTasks = 0;

	public ProjectSchedule() {
		super();
	}

	/**
	 * @return Returns the numberOfCompletedTasks.
	 */
	public Integer getNumberOfCompletedTasks() {
		return numberOfCompletedTasks;
	}

	/**
	 * @param numberOfCompletedTasks The numberOfCompletedTasks to set.
	 */
	public void setNumberOfCompletedTasks(Integer numberOfCompletedTasks) {
		this.numberOfCompletedTasks = numberOfCompletedTasks;
	}

	/**
	 * @return Returns the numberOfLateTasks.
	 */
	public Integer getNumberOfLateTasks() {
		return numberOfLateTasks;
	}

	/**
	 * @param numberOfLateTasks The numberOfLateTasks to set.
	 */
	public void setNumberOfLateTasks(Integer numberOfLateTasks) {
		this.numberOfLateTasks = numberOfLateTasks;
	}

	/**
	 * @return Returns the numberOfTaskComingDue.
	 */
	public Integer getNumberOfTaskComingDue() {
		return numberOfTaskComingDue;
	}

	/**
	 * @param numberOfTaskComingDue The numberOfTaskComingDue to set.
	 */
	public void setNumberOfTaskComingDue(Integer numberOfTaskComingDue) {
		this.numberOfTaskComingDue = numberOfTaskComingDue;
	}

	/**
	 * @return Returns the numberOfUnassignedTasks.
	 */
	public Integer getNumberOfUnassignedTasks() {
		return numberOfUnassignedTasks;
	}

	/**
	 * @param numberOfUnassignedTasks The numberOfUnassignedTasks to set.
	 */
	public void setNumberOfUnassignedTasks(Integer numberOfUnassignedTasks) {
		this.numberOfUnassignedTasks = numberOfUnassignedTasks;
	}

	/**
	 * @return Returns the plannedFinish.
	 */
	public Date getPlannedFinish() {
		return plannedFinish;
	}

	/**
	 * @param plannedFinish The plannedFinish to set.
	 */
	public void setPlannedFinish(Date plannedFinish) {
		this.plannedFinish = plannedFinish;
	}

	/**
	 * @return Returns the plannedStart.
	 */
	public Date getPlannedStart() {
		return plannedStart;
	}

	/**
	 * @param plannedStart The plannedStart to set.
	 */
	public void setPlannedStart(Date plannedStart) {
		this.plannedStart = plannedStart;
	}

	/**
	 * @return Returns the projectId.
	 */
	public Integer getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId The projectId to set.
	 */
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Date getActualFinish() {
		return actualFinish;
	}

	public void setActualFinish(Date actualFinish) {
		this.actualFinish = actualFinish;
	}

	public Date getActualStart() {
		return actualStart;
	}

	public void setActualStart(Date actualStart) {
		this.actualStart = actualStart;
	}

	
}
