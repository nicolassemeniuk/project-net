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
package net.project.hibernate.model.project_space;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;

import net.project.hibernate.model.PnAssignment;

/**
 * 
 *  TeamMate class
 * 
 */
public class Teammate {

	/**
	 *  person unique identifier 
	 */
	private Integer personId;

	/**
	 * person first name
	 */
	private String firstName;

	/**
	 * person last name
	 */
	private String lastName;

	/**
	 * person display name
	 */
	private String displayName;

	/**
	 * person email
	 */
	private String email;

	/**
	 * user status 
	 */
	private String userStatus;

	/**
	 * person portfilio identifier
	 */
	private Integer membershipPortfolioId;

	/**
	 * database record status 
	 */
	private String recordStatus;

	/**
	 * person creation date
	 */
	private Date createdDate;

	/**
	 * person assignment list
	 */
	private List<PnAssignment> assignments;

	/**
	 * user assignement status - true if user is overassigned
	 */
	private boolean overassigned;

	/**
	 * user task status - true if user has late tasks 
	 */
	private boolean hasLateTasks;

	/**
	 * user task status - true if user has task due this week
	 */
	private boolean hasTaskDueThisWeek;
	
	/**
	 * user online status
	 */
	private boolean online;

	/**
	 * time of teammate last application action
	 */
	private Date lastAccessTime;
	
	/**
	 * skype to check user skype status 
	 */
	private String skype;
	
	/**
	 * @return Returns the createdDate.
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate The createdDate to set.
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return Returns the displayName.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName The displayName to set.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return Returns the email.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email The email to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

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
	 * @return Returns the membershipPortfolioId.
	 */
	public Integer getMembershipPortfolioId() {
		return membershipPortfolioId;
	}

	/**
	 * @param membershipPortfolioId The membershipPortfolioId to set.
	 */
	public void setMembershipPortfolioId(Integer membershipPortfolioId) {
		this.membershipPortfolioId = membershipPortfolioId;
	}

	/**
	 * @return Returns the personId.
	 */
	public Integer getPersonId() {
		return personId;
	}

	/**
	 * @param personId The personId to set.
	 */
	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	/**
	 * @return Returns the recordStatus.
	 */
	public String getRecordStatus() {
		return recordStatus;
	}

	/**
	 * @param recordStatus The recordStatus to set.
	 */
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	/**
	 * @return Returns the userStatus.
	 */
	public String getUserStatus() {
		return userStatus;
	}

	/**
	 * @param userStatus The userStatus to set.
	 */
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	/**
	 * @return Returns the assignments.
	 */
	public List<PnAssignment> getAssignments() {
		return assignments;
	}

	/**
	 * @param assignments The assignments to set.
	 */
	public void setAssignments(List<PnAssignment> assignments) {
		this.assignments = assignments;
	}

	/**
	 * @return Returns the overassigned.
	 */
	public boolean isOverassigned() {
		return overassigned;
	}

	/**
	 * @param overassigned The overassigned to set.
	 */
	public void setOverassigned(boolean overassigned) {
		this.overassigned = overassigned;
	}

	/**
	 * @return Returns the hasLateTasks.
	 */
	public boolean isHasLateTasks() {
		return hasLateTasks;
	}

	/**
	 * @param hasLateTasks The hasLateTasks to set.
	 */
	public void setHasLateTasks(boolean hasLateTasks) {
		this.hasLateTasks = hasLateTasks;
	}

	/**
	 * @return Returns the hasTaskDueThisWeek.
	 */
	public boolean isHasTaskDueThisWeek() {
		return hasTaskDueThisWeek;
	}

	/**
	 * @param hasTaskDueThisWeek The hasTaskDueThisWeek to set.
	 */
	public void setHasTaskDueThisWeek(boolean hasTaskDueThisWeek) {
		this.hasTaskDueThisWeek = hasTaskDueThisWeek;
	}
	
	/**
	 * @return Returns the online.
	 */
	public boolean isOnline() {
		return online;
	}

	/**
	 * @param online The online to set.
	 */
	public void setOnline(boolean online) {
		this.online = online;
	}
	
	/**
	 * @return Returns the lastAccessTime.
	 */
	public Date getLastAccessTime() {
		return lastAccessTime;
	}

	/**
	 * @param lastAccessTime The lastAccessTime to set.
	 */
	public void setLastAccessTime(Date lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	/**
	 * @return Returns the skype
	 */
	public String getSkype() {
		return skype;
	}

	/**
	 * @param skype
	 */
	public void setSkype(String skype) {
		this.skype = skype;
	}	
	
	public Teammate() {
		super();
	}


	public Teammate(Integer personId, String firstName, String lastName, String displayName, String email, Integer membershipPortfolioId, String userStatus, String recordStatus, Date createdDate) {
		this.personId = personId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.displayName = displayName;
		this.email = email;
		this.membershipPortfolioId = membershipPortfolioId;
		this.userStatus = userStatus;
		this.recordStatus = recordStatus;
		this.createdDate = createdDate;
	}

	public Teammate(Integer personId, String firstName, String lastName, String displayName, String email, Integer membershipPortfolioId) {
		this.personId = personId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.displayName = displayName;
		this.email = email;
		this.membershipPortfolioId = membershipPortfolioId;
		this.assignments = new ArrayList<PnAssignment>();
	}	
	
	public Teammate(Integer personId, String firstName, String lastName, String displayName, Date lastAccessTime) {
		this.personId = personId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.displayName = displayName;
		this.assignments = new ArrayList<PnAssignment>();
		this.lastAccessTime = lastAccessTime; 
	}

	/** 
	 * Used for getting Teammates Without Assignments 
	 */
	public Teammate(Integer personId, String firstName, String lastName, String displayName, String email, Integer membershipPortfolioId, String skype) {
		this.personId = personId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.displayName = displayName;
		this.email = email;
		this.membershipPortfolioId = membershipPortfolioId;
		this.assignments = new ArrayList<PnAssignment>();
		this.skype = skype;
	}	
	
	/** 
	 * Used for getting Teammates Without Assignments with user status
	 */
	public Teammate(Integer personId, String firstName, String lastName, String displayName, String email, Integer membershipPortfolioId, String skype, String userStatus) {
		this.personId = personId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.displayName = displayName;
		this.email = email;
		this.membershipPortfolioId = membershipPortfolioId;
		this.assignments = new ArrayList<PnAssignment>();
		this.skype = skype;
		this.userStatus = userStatus;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof Teammate))
			return false;				
		Teammate castOther = (Teammate)other;
		
		return new EqualsBuilder().append(personId.intValue(), castOther.personId.intValue()).isEquals();
	}
	
}
