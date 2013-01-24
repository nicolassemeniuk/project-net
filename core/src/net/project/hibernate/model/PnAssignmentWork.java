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
package net.project.hibernate.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Contains history of all work that users have logged against a task.
 */
@Entity
@Table(name = "PN_ASSIGNMENT_WORK")
public class PnAssignmentWork implements java.io.Serializable {

	private Integer assignmentWorkId;

	private Integer objectId;

	private Integer personId;

	private Timestamp workStart;

	private Timestamp workEnd;

	private BigDecimal work;

	private Integer workUnits;

	private BigDecimal workRemaining;

	private Integer workRemainingUnits;

	private BigDecimal percentComplete;

	private Date logDate;

	private Integer modifiedBy;

	private BigDecimal scheduledWork;

	private Integer scheduledWorkUnits;

	private String comments;

	private String objectName;

	private String workStartDate;

	public PnAssignmentWork() {
	}

	public PnAssignmentWork(Integer assignmentWorkId, Integer objectId, Integer personId) {
		this.assignmentWorkId = assignmentWorkId;
		this.objectId = objectId;
		this.personId = personId;
	}

	public PnAssignmentWork(Integer assignmentWorkId, Integer objectId, Integer personId, Timestamp workStart,
			Timestamp workEnd, BigDecimal work, Integer workUnits, BigDecimal workRemaining,
			Integer workRemainingUnits, BigDecimal percentComplete, Date logDate, Integer modifiedBy,
			BigDecimal scheduledWork, Integer scheduledWorkUnits, String comments) {
		this.assignmentWorkId = assignmentWorkId;
		this.objectId = objectId;
		this.personId = personId;
		this.workStart = workStart;
		this.workEnd = workEnd;
		this.work = work;
		this.workUnits = workUnits;
		this.workRemaining = workRemaining;
		this.workRemainingUnits = workRemainingUnits;
		this.percentComplete = percentComplete;
		this.logDate = logDate;
		this.modifiedBy = modifiedBy;
		this.scheduledWork = scheduledWork;
		this.scheduledWorkUnits = scheduledWorkUnits;
		this.comments = comments;
	}

	@Id
	@Column(name = "ASSIGNMENT_WORK_ID", nullable = false)
	public Integer getAssignmentWorkId() {
		return this.assignmentWorkId;
	}

	public void setAssignmentWorkId(Integer assignmentWorkId) {
		this.assignmentWorkId = assignmentWorkId;
	}

	@Column(name = "OBJECT_ID", nullable = false, length = 20)
	public Integer getObjectId() {
		return this.objectId;
	}

	public void setObjectId(Integer objectId) {
		this.objectId = objectId;
	}

	@Column(name = "PERSON_ID", nullable = false, length = 20)
	public Integer getPersonId() {
		return this.personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	@Column(name = "WORK_START")
	public Timestamp getWorkStart() {
		return this.workStart;
	}

	public void setWorkStart(Timestamp workStart) {
		this.workStart = workStart;
	}

	@Column(name = "WORK_END")
	public Timestamp getWorkEnd() {
		return this.workEnd;
	}

	public void setWorkEnd(Timestamp workEnd) {
		this.workEnd = workEnd;
	}

	@Column(name = "WORK", length = 22)
	public BigDecimal getWork() {
		return this.work;
	}

	public void setWork(BigDecimal work) {
		this.work = work;
	}

	@Column(name = "WORK_UNITS", length = 22)
	public Integer getWorkUnits() {
		return this.workUnits;
	}

	public void setWorkUnits(Integer workUnits) {
		this.workUnits = workUnits;
	}

	@Column(name = "WORK_REMAINING", length = 22)
	public BigDecimal getWorkRemaining() {
		return this.workRemaining;
	}

	public void setWorkRemaining(BigDecimal workRemaining) {
		this.workRemaining = workRemaining;
	}

	@Column(name = "WORK_REMAINING_UNITS", length = 22)
	public Integer getWorkRemainingUnits() {
		return this.workRemainingUnits;
	}

	public void setWorkRemainingUnits(Integer workRemainingUnits) {
		this.workRemainingUnits = workRemainingUnits;
	}

	@Column(name = "PERCENT_COMPLETE", length = 22)
	public BigDecimal getPercentComplete() {
		return this.percentComplete;
	}

	public void setPercentComplete(BigDecimal percentComplete) {
		this.percentComplete = percentComplete;
	}

	@Column(name = "LOG_DATE", length = 7)
	public Date getLogDate() {
		return this.logDate;
	}

	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}

	@Column(name = "MODIFIED_BY", length = 22)
	public Integer getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	@Column(name = "SCHEDULED_WORK", length = 22)
	public BigDecimal getScheduledWork() {
		return this.scheduledWork;
	}

	public void setScheduledWork(BigDecimal scheduledWork) {
		this.scheduledWork = scheduledWork;
	}

	@Column(name = "SCHEDULED_WORK_UNITS", length = 22)
	public Integer getScheduledWorkUnits() {
		return this.scheduledWorkUnits;
	}

	public void setScheduledWorkUnits(Integer scheduledWorkUnits) {
		this.scheduledWorkUnits = scheduledWorkUnits;
	}

	@Column(name = "COMMENTS", length = 4000)
	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @return the objectName
	 */
	@Transient
	public String getObjectName() {
		return objectName;
	}

	/**
	 * @param objectName the objectName to set
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	/**
	 * @return the workStartDate
	 */
	@Transient
	public String getWorkStartDate() {
		return workStartDate;
	}

	/**
	 * @param workStartDate the workStartDate to set
	 */
	public void setWorkStartDate(String workStartDate) {
		this.workStartDate = workStartDate;
	}

}
