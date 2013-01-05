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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PnDocSpace generated by hbm2java
 */
@Entity
@Table(name = "PN_DOC_SPACE")
public class PnDocSpace implements java.io.Serializable {

	/** identifier field */
	private Integer docSpaceId;

	/** nullable persistent field */
	private String docSpaceName;

	/** persistent field */
	private Date crc;

	/** persistent field */
	private String recordStatus;

	/** nullable persistent field */
	private PnObject pnObject;

	/** persistent field */
	private Set pnDocProviderHasDocSpaces = new HashSet(0);

	/** persistent field */
	private Set pnSpaceHasDocSpaces = new HashSet(0);

	/** persistent field */
	private Set pnDocSpaceHasContainers = new HashSet(0);

	public PnDocSpace() {
	}

	public PnDocSpace(Integer docSpaceId, Date crc, String recordStatus) {
		this.docSpaceId = docSpaceId;
		this.crc = crc;
		this.recordStatus = recordStatus;
	}

	public PnDocSpace(Integer docSpaceId, String docSpaceName, Date crc, String recordStatus, PnObject pnObject,
			Set pnDocProviderHasDocSpaces, Set pnSpaceHasDocSpaces, Set pnDocSpaceHasContainers) {
		this.docSpaceId = docSpaceId;
		this.docSpaceName = docSpaceName;
		this.crc = crc;
		this.recordStatus = recordStatus;
		this.pnObject = pnObject;
		this.pnDocProviderHasDocSpaces = pnDocProviderHasDocSpaces;
		this.pnSpaceHasDocSpaces = pnSpaceHasDocSpaces;
		this.pnDocSpaceHasContainers = pnDocSpaceHasContainers;
	}

	public PnDocSpace(Integer docSpaceId, String docSpaceName, Date crc, String recordStatus) {
		this.docSpaceId = docSpaceId;
		this.docSpaceName = docSpaceName;
		this.crc = crc;
		this.recordStatus = recordStatus;
	}

	/** minimal constructor */
	public PnDocSpace(Integer docSpaceId, Date crc, String recordStatus, Set pnDocProviderHasDocSpaces,
			Set pnSpaceHasDocSpaces, Set pnDocSpaceHasContainers) {
		this.docSpaceId = docSpaceId;
		this.crc = crc;
		this.recordStatus = recordStatus;
		this.pnDocProviderHasDocSpaces = pnDocProviderHasDocSpaces;
		this.pnSpaceHasDocSpaces = pnSpaceHasDocSpaces;
		this.pnDocSpaceHasContainers = pnDocSpaceHasContainers;
	}

	@Id
	@Column(name = "DOC_SPACE_ID", nullable = false)
	public Integer getDocSpaceId() {
		return this.docSpaceId;
	}

	public void setDocSpaceId(Integer docSpaceId) {
		this.docSpaceId = docSpaceId;
	}

	@Column(name = "DOC_SPACE_NAME", length = 80)
	public String getDocSpaceName() {
		return this.docSpaceName;
	}

	public void setDocSpaceName(String docSpaceName) {
		this.docSpaceName = docSpaceName;
	}

	@Column(name = "CRC", nullable = false, length = 7)
	public Date getCrc() {
		return this.crc;
	}

	public void setCrc(Date crc) {
		this.crc = crc;
	}

	@Column(name = "RECORD_STATUS", nullable = false, length = 1)
	public String getRecordStatus() {
		return this.recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	//@OneToOne(fetch = FetchType.LAZY, targetEntity = PnObject.class)
	//@JoinColumn(name="DOC_SPACE_ID")
	@Transient
	public PnObject getPnObject() {
		return this.pnObject;
	}

	public void setPnObject(PnObject pnObject) {
		this.pnObject = pnObject;
	}

	//@OneToMany(fetch = FetchType.LAZY, mappedBy = "pnDocSpace", targetEntity = PnDocProviderHasDocSpace.class)
	@Transient
	public Set getPnDocProviderHasDocSpaces() {
		return this.pnDocProviderHasDocSpaces;
	}

	public void setPnDocProviderHasDocSpaces(Set pnDocProviderHasDocSpaces) {
		this.pnDocProviderHasDocSpaces = pnDocProviderHasDocSpaces;
	}

	//@OneToMany(fetch = FetchType.LAZY, mappedBy = "pnDocSpace", targetEntity = PnSpaceHasDocSpace.class)
	@Transient
	public Set getPnSpaceHasDocSpaces() {
		return this.pnSpaceHasDocSpaces;
	}

	public void setPnSpaceHasDocSpaces(Set pnSpaceHasDocSpaces) {
		this.pnSpaceHasDocSpaces = pnSpaceHasDocSpaces;
	}

	//@OneToMany(fetch = FetchType.LAZY, mappedBy = "pnDocSpace", targetEntity = PnDocSpaceHasContainer.class)
	@Transient
	public Set getPnDocSpaceHasContainers() {
		return this.pnDocSpaceHasContainers;
	}

	public void setPnDocSpaceHasContainers(Set pnDocSpaceHasContainers) {
		this.pnDocSpaceHasContainers = pnDocSpaceHasContainers;
	}

	public String toString() {
		return new ToStringBuilder(this).append("docSpaceId", getDocSpaceId()).toString();
	}

}
