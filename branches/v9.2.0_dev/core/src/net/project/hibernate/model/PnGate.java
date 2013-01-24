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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PnGate generated by hbm2java
 */
@Entity
@Table(name = "PN_GATE")
public class PnGate implements java.io.Serializable {

	/** identifier field */
	private Integer gateId;

	/** persistent field */
	private String gateName;

	/** nullable persistent field */
	private String gateDesc;

	/** nullable persistent field */
	private Date gateDate;

	/** persistent field */
	private Integer statusId;

	/** persistent field */
	private String recordStatus;

	private PnObject pnObject;

	private PnPhase pnPhase;

	private Set pnProcesses = new HashSet(0);

	public PnGate() {
	}

	public PnGate(Integer gateId, String gateName, Integer statusId, String recordStatus) {
		this.gateId = gateId;
		this.gateName = gateName;
		this.statusId = statusId;
		this.recordStatus = recordStatus;
	}

	public PnGate(Integer gateId, String gateName, String gateDesc, Date gateDate, Integer statusId,
			String recordStatus, PnObject pnObject, PnPhase pnPhase, Set pnProcesses) {
		this.gateId = gateId;
		this.gateName = gateName;
		this.gateDesc = gateDesc;
		this.gateDate = gateDate;
		this.statusId = statusId;
		this.recordStatus = recordStatus;
		this.pnObject = pnObject;
		this.pnPhase = pnPhase;
		this.pnProcesses = pnProcesses;
	}

	/** minimal constructor */
	public PnGate(Integer gateId, String gateName, Integer statusId, String recordStatus,
			net.project.hibernate.model.PnPhase pnPhase, Set pnProcesses) {
		this.gateId = gateId;
		this.gateName = gateName;
		this.statusId = statusId;
		this.recordStatus = recordStatus;
		this.pnPhase = pnPhase;
		this.pnProcesses = pnProcesses;
	}

	@Id
	@Column(name = "GATE_ID", nullable = false)
	public Integer getGateId() {
		return this.gateId;
	}

	public void setGateId(Integer gateId) {
		this.gateId = gateId;
	}

	@Column(name = "GATE_NAME", nullable = false, length = 80)
	public String getGateName() {
		return this.gateName;
	}

	public void setGateName(String gateName) {
		this.gateName = gateName;
	}

	@Column(name = "GATE_DESC", length = 4000)
	public String getGateDesc() {
		return this.gateDesc;
	}

	public void setGateDesc(String gateDesc) {
		this.gateDesc = gateDesc;
	}

	@Column(name = "GATE_DATE", length = 7)
	public Date getGateDate() {
		return this.gateDate;
	}

	public void setGateDate(Date gateDate) {
		this.gateDate = gateDate;
	}

	@Column(name = "STATUS_ID", nullable = false, length = 20)
	public Integer getStatusId() {
		return this.statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	@Column(name = "RECORD_STATUS", nullable = false, length = 1)
	public String getRecordStatus() {
		return this.recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	//@OneToOne(fetch = FetchType.LAZY, targetEntity = PnObject.class)
	//@JoinColumn(name = "GATE_ID")
	@Transient
	public PnObject getPnObject() {
		return this.pnObject;
	}

	public void setPnObject(PnObject pnObject) {
		this.pnObject = pnObject;
	}

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = PnPhase.class)
	@JoinColumn(name = "PHASE_ID")
	public PnPhase getPnPhase() {
		return this.pnPhase;
	}

	public void setPnPhase(PnPhase pnPhase) {
		this.pnPhase = pnPhase;
	}

	//@OneToMany(fetch = FetchType.LAZY, mappedBy = "pnGate", targetEntity = PnProcess.class)
	@Transient
	public Set getPnProcesses() {
		return this.pnProcesses;
	}

	public void setPnProcesses(Set pnProcesses) {
		this.pnProcesses = pnProcesses;
	}

	public String toString() {
		return new ToStringBuilder(this).append("gateId", getGateId()).toString();
	}

}
