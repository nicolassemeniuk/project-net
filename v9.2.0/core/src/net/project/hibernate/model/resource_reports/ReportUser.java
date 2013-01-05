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



public class ReportUser {

	private Integer userId = 0;
	
	private String firstName;
	
	private String lastName;
	
	private List<ReportUserProjects> projektList;
	
	
	
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
	 * @return Returns the projektList.
	 */
	public List<ReportUserProjects> getProjektList() {
		return projektList;
	}



	/**
	 * @param projektList The projektList to set.
	 */
	public void setProjektList(List<ReportUserProjects> projektList) {
		this.projektList = projektList;
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



	public ReportUser() {
		super();
		// TODO Auto-generated constructor stub
	}



	/**
	 * @param name
	 * @param name2
	 * @param id
	 */
	public ReportUser(Integer personId, String firstName, String lastName ) {
		
		this.firstName = firstName; 
		this.lastName = lastName;
		this.userId = personId;
		this.projektList = new ArrayList<ReportUserProjects>();
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((projektList == null) ? 0 : projektList.hashCode());
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
		final ReportUser other = (ReportUser) obj;
		if (projektList == null) {
			if (other.projektList != null)
				return false;
		} else if (!projektList.equals(other.projektList))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	
}
