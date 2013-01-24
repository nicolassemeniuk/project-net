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
package net.project.hibernate.model.resource_reports;

import java.util.ArrayList;
import java.util.List;

public class ReportUserProjects {

	/**
	 * project identity
	 */
	private Integer projectId;
	
	/**
	 * project name
	 */
	private String projectName;
	
	/**
	 * 
	 */
	private List<ReportMonth> monthList;
			
	/**
	 * @return Returns the monthList.
	 */
	public List<ReportMonth> getMonthList() {
		return monthList;
	}

	/**
	 * @param monthList The monthList to set.
	 */
	public void setMonthList(List<ReportMonth> monthList) {
		this.monthList = monthList;
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



	public ReportUserProjects() {
		super();
	}

	/**
	 * @param id
	 * @param name
	 */
	public ReportUserProjects(Integer id, String name) {
		projectId = id;
		projectName = name;
		monthList = new ArrayList<ReportMonth>();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((projectId == null) ? 0 : projectId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ReportUserProjects other = (ReportUserProjects) obj;
		if (projectId == null) {
			if (other.projectId != null)
				return false;
		} else if (!projectId.equals(other.projectId))
			return false;
		return true;
	}

	
}
