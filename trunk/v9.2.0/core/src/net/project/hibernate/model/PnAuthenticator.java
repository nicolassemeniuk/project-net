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

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * PnAuthenticator generated by hbm2java
 */
@Entity
@Table(name = "PN_AUTHENTICATOR")
public class PnAuthenticator implements java.io.Serializable {

	private Integer authenticatorId;

	private String name;

	private String description;

	private String recordStatus;

	private Set pnPersonAuthenticators = new HashSet(0);

	private PnAuthenticatorType pnAuthenticatorType;

	public PnAuthenticator() {
	}

	public PnAuthenticator(Integer authenticatorId, String recordStatus) {
		this.authenticatorId = authenticatorId;
		this.recordStatus = recordStatus;
	}

	public PnAuthenticator(Integer authenticatorId, String name, String description, String recordStatus,
			Set pnPersonAuthenticators, PnAuthenticatorType pnAuthenticatorType) {
		this.authenticatorId = authenticatorId;
		this.name = name;
		this.description = description;
		this.recordStatus = recordStatus;
		this.pnPersonAuthenticators = pnPersonAuthenticators;
		this.pnAuthenticatorType = pnAuthenticatorType;
	}

	/** minimal constructor */
	public PnAuthenticator(Integer authenticatorId, String recordStatus,
			net.project.hibernate.model.PnAuthenticatorType pnAuthenticatorType, Set pnPersonAuthenticators) {
		this.authenticatorId = authenticatorId;
		this.recordStatus = recordStatus;
		this.pnAuthenticatorType = pnAuthenticatorType;
		this.pnPersonAuthenticators = pnPersonAuthenticators;
	}

	@Id
	@Column(name = "AUTHENTICATOR_ID", nullable = false)
	public Integer getAuthenticatorId() {
		return this.authenticatorId;
	}

	public void setAuthenticatorId(Integer authenticatorId) {
		this.authenticatorId = authenticatorId;
	}

	@Column(name = "NAME", length = 80)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "DESCRIPTION", length = 240)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "RECORD_STATUS", nullable = false, length = 1)
	public String getRecordStatus() {
		return this.recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	//@OneToMany(fetch = FetchType.LAZY, mappedBy = "pnAuthenticator", targetEntity = PnPersonAuthenticator.class)
	@Transient
	public Set getPnPersonAuthenticators() {
		return this.pnPersonAuthenticators;
	}

	public void setPnPersonAuthenticators(Set pnPersonAuthenticators) {
		this.pnPersonAuthenticators = pnPersonAuthenticators;
	}

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = PnAuthenticatorType.class)
	@JoinColumn(name = "AUTHENTICATOR_TYPE")
	public PnAuthenticatorType getPnAuthenticatorType() {
		return this.pnAuthenticatorType;
	}

	public void setPnAuthenticatorType(PnAuthenticatorType pnAuthenticatorType) {
		this.pnAuthenticatorType = pnAuthenticatorType;
	}

}
