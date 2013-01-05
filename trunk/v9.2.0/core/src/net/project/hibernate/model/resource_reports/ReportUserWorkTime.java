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

import java.util.Date;
import java.util.HashMap;

public class ReportUserWorkTime {

	/**
	 * user identificator
	 */
	private Integer userId = 0;

	/**
	 * user resource full name
	 */
	private String resourceName;

	/**
	 *  wokring hours by day
	 */
	private HashMap<Date, Float> workTimeByDays = new HashMap<Date, Float>();

	public ReportUserWorkTime() {
		super();
	}

	/**
	 * @return Returns the resourceName.
	 */
	public String getResourceName() {
		return resourceName;
	}

	/**
	 * @param resourceName The resourceName to set.
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	/**
	 * @return Returns the userId.
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * @param userId The userId to set.
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * @return Returns the workTimeByDays.
	 */
	public HashMap<Date, Float> getWorkTimeByDays() {
		return workTimeByDays;
	}

	/**
	 * @param workTimeByDays The workTimeByDays to set.
	 */
	public void setWorkTimeByDays(HashMap<Date, Float> workTimeByDays) {
		this.workTimeByDays = workTimeByDays;
	}

}
