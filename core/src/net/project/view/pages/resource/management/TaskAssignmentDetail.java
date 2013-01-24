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
package net.project.view.pages.resource.management;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Persist;

/**
 * @author 
 *
 */
public class TaskAssignmentDetail {

	private static Logger log;

	@Persist
	private String taskId;

	@Persist
	private String taskName;

	@Persist
	private String projectName;

	@Persist
	private String description;

	@Persist
	private String startDate;

	@Persist
	private String finishDate;

	@Persist
	private String actualStartDate;

	@Persist
	private String actualFinishDate;

	@Persist
	private String work;

	@Persist
	private String workComplete;

	@Persist
	private String workPercentComplete;

	@Persist
	private String duration;

	@Persist
	private String priority;

	@Persist
	private String calculationType;

	public TaskAssignmentDetail() {
		log = Logger.getLogger(ViewDetails.class);
	}

	void onActivate(String taskDetails) {
		if (taskDetails != null) {			
			String[] details = taskDetails.split("&");
			try {
				taskId = details[0];
				taskName = details[1];
				projectName = details[2];
				actualStartDate = details[3];
				actualFinishDate = details[4];
				workPercentComplete = details[5];
			} catch (Exception e) {
				log.error("Error occured while getting url parameters in TaskAssignmentDetail page" + e.getMessage());
			}
		}
	}

	/**
	 * @return Returns the actualFinishDate.
	 */
	public String getActualFinishDate() {
		return actualFinishDate;
	}

	/**
	 * @param actualFinishDate The actualFinishDate to set.
	 */
	public void setActualFinishDate(String actualFinishDate) {
		this.actualFinishDate = actualFinishDate;
	}

	/**
	 * @return Returns the actualStartDate.
	 */
	public String getActualStartDate() {
		return actualStartDate;
	}

	/**
	 * @param actualStartDate The actualStartDate to set.
	 */
	public void setActualStartDate(String actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	/**
	 * @return Returns the calculationType.
	 */
	public String getCalculationType() {
		return calculationType;
	}

	/**
	 * @param calculationType The calculationType to set.
	 */
	public void setCalculationType(String calculationType) {
		this.calculationType = calculationType;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return Returns the duration.
	 */
	public String getDuration() {
		return duration;
	}

	/**
	 * @param duration The duration to set.
	 */
	public void setDuration(String duration) {
		this.duration = duration;
	}

	/**
	 * @return Returns the finishDate.
	 */
	public String getFinishDate() {
		return finishDate;
	}

	/**
	 * @param finishDate The finishDate to set.
	 */
	public void setFinishDate(String finishDate) {
		this.finishDate = finishDate;
	}

	/**
	 * @return Returns the priority.
	 */
	public String getPriority() {
		return priority;
	}

	/**
	 * @param priority The priority to set.
	 */
	public void setPriority(String priority) {
		this.priority = priority;
	}

	/**
	 * @return Returns the projectName.
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName The projectName to set.
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return Returns the startDate.
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate The startDate to set.
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return Returns the taskId.
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @param taskId The taskId to set.
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * @return Returns the taskName.
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @param taskName The taskName to set.
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 * @return Returns the work.
	 */
	public String getWork() {
		return work;
	}

	/**
	 * @param work The work to set.
	 */
	public void setWork(String work) {
		this.work = work;
	}

	/**
	 * @return Returns the workComplete.
	 */
	public String getWorkComplete() {
		return workComplete;
	}

	/**
	 * @param workComplete The workComplete to set.
	 */
	public void setWorkComplete(String workComplete) {
		this.workComplete = workComplete;
	}

	/**
	 * @return Returns the workPercentComplete.
	 */
	public String getWorkPercentComplete() {
		return workPercentComplete;
	}

	/**
	 * @param workPercentComplete The workPercentComplete to set.
	 */
	public void setWorkPercentComplete(String workPercentComplete) {
		this.workPercentComplete = workPercentComplete;
	}

}
