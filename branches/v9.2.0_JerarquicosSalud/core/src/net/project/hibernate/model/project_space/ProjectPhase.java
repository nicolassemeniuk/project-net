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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.project.hibernate.model.PnTask;

public class ProjectPhase {

	private Integer phaseId;

	private String phaseName;

	private String phaseDesc;

	private Date startDate;

	private Date endDate;

	private Integer sequence;

	private Integer statusId;

	private String statusCode;
	
	private Integer percentComplete;

	private String progressReportingMethod;

	private String gateName;
	
	private Date gateDate;
	
	private List<PnTask> milestones = new ArrayList<PnTask>();

	public ProjectPhase() {
		super();
	}

	/**
	 * constructor
	 * 
	 * @param phaseId
	 * @param phaseName
	 * @param phaseDesc
	 * @param startDate
	 * @param endDate
	 * @param statusId
	 * @param sequence
	 * @param percentComplete
	 * @param progressReportingMethod
	 */
	public ProjectPhase(Integer phaseId, String phaseName, String phaseDesc,
			Date startDate, Date endDate, Integer statusId, Integer sequence,
			Integer enteredPercentComplete, String progressReportingMethod, String statusCode,
			String gateName, Date gateDate) {
		// TODO Auto-generated constructor stub
		this.phaseId = phaseId;
		this.phaseName = phaseName;
		this.phaseDesc = phaseDesc;
		this.startDate = startDate;
		this.endDate = endDate;
		this.statusId = statusId;
		this.sequence = sequence;
		this.percentComplete = enteredPercentComplete;
		this.progressReportingMethod = progressReportingMethod;
		this.statusCode = statusCode;
		this.gateName = gateName;
		this.gateDate = gateDate;
	}

	/**
	 * @return Returns the endDate.
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate The endDate to set.
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return Returns the percentComplete.
	 */
	public Integer getPercentComplete() {
		return percentComplete;
	}

	/**
	 * @param percentComplete The percentComplete to set.
	 */
	public void setPercentComplete(Integer enteredPercentComplete) {
		this.percentComplete = enteredPercentComplete;
	}

	/**
	 * @return Returns the phaseDesc.
	 */
	public String getPhaseDesc() {
		return phaseDesc;
	}

	/**
	 * @param phaseDesc The phaseDesc to set.
	 */
	public void setPhaseDesc(String phaseDesc) {
		this.phaseDesc = phaseDesc;
	}

	/**
	 * @return Returns the phaseId.
	 */
	public Integer getPhaseId() {
		return phaseId;
	}

	/**
	 * @param phaseId The phaseId to set.
	 */
	public void setPhaseId(Integer phaseId) {
		this.phaseId = phaseId;
	}

	/**
	 * @return Returns the phaseName.
	 */
	public String getPhaseName() {
		return phaseName;
	}

	/**
	 * @param phaseName The phaseName to set.
	 */
	public void setPhaseName(String phaseName) {
		this.phaseName = phaseName;
	}

	/**
	 * @return Returns the progressReportingMethod.
	 */
	public String getProgressReportingMethod() {
		return progressReportingMethod;
	}

	/**
	 * @param progressReportingMethod The progressReportingMethod to set.
	 */
	public void setProgressReportingMethod(String progressReportingMethod) {
		this.progressReportingMethod = progressReportingMethod;
	}

	/**
	 * @return Returns the sequence.
	 */
	public Integer getSequence() {
		return sequence;
	}

	/**
	 * @param sequence The sequence to set.
	 */
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return Returns the startDate.
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate The startDate to set.
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return Returns the statusId.
	 */
	public Integer getStatusId() {
		return statusId;
	}

	/**
	 * @param statusId The statusId to set.
	 */
	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	/**
	 * @return Returns the milestones.
	 */
	public List<PnTask> getMilestones() {
		return milestones;
	}

	/**
	 * @param milestones The milestones to set.
	 */
	public void setMilestones(List<PnTask> milestones) {
		this.milestones = milestones;
	}

	/**
	 * @return Returns the statusCode.
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode The statusCode to set.
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return Returns the gateDate.
	 */
	public Date getGateDate() {
		return gateDate;
	}

	/**
	 * @param gateDate The gateDate to set.
	 */
	public void setGateDate(Date gateDate) {
		this.gateDate = gateDate;
	}

	/**
	 * @return Returns the gateName.
	 */
	public String getGateName() {
		return gateName;
	}

	/**
	 * @param gateName The gateName to set.
	 */
	public void setGateName(String gateName) {
		this.gateName = gateName;
	}

	
	
}
