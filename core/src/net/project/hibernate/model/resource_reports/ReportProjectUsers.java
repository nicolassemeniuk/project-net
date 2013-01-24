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

public class ReportProjectUsers {

	/**
	 * resource identifier
	 */
	private Integer userId = 0;

	/**
	 * first name
	 */
	private String firstName;

	/**
	 * last name
	 */
	private String lastName;

	/**
	 * list of allocations by month
	 */
	private List<ReportMonth> monthList;


	/**
	 * @return Returns the firstName.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName The firstName to set.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return Returns the lastName.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName The lastName to set.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

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

	public ReportProjectUsers() {
		super();
	}

	/**
	 * @param userId
	 * @param firstName
	 * @param lastName
	 */
	public ReportProjectUsers(Integer userId, String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.userId = userId;
		monthList = new ArrayList<ReportMonth>();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((userId == null) ? 0 : userId.hashCode());
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
		final ReportProjectUsers other = (ReportProjectUsers) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}	
	
	
}
