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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PnInvoice generated by hbm2java
 */
@Entity
@Table(name = "PN_INVOICE")
public class PnInvoice implements java.io.Serializable {

	/** identifier field */
	private Integer invoiceId;

	/** persistent field */
	private Date creationDatetime;

	/** persistent field */
	private Set pnLedgers = new HashSet(0);

	public PnInvoice() {
	}

	public PnInvoice(Integer invoiceId, Date creationDatetime) {
		this.invoiceId = invoiceId;
		this.creationDatetime = creationDatetime;
	}

	public PnInvoice(Integer invoiceId, Date creationDatetime, Set pnLedgers) {
		this.invoiceId = invoiceId;
		this.creationDatetime = creationDatetime;
		this.pnLedgers = pnLedgers;
	}

	@Id
	@Column(name = "INVOICE_ID", nullable = false)
	public Integer getInvoiceId() {
		return this.invoiceId;
	}

	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}

	@Column(name = "CREATION_DATETIME", nullable = false, length = 7)
	public Date getCreationDatetime() {
		return this.creationDatetime;
	}

	public void setCreationDatetime(Date creationDatetime) {
		this.creationDatetime = creationDatetime;
	}

	//@OneToMany(fetch = FetchType.LAZY, mappedBy = "pnInvoice", targetEntity = PnLedger.class)
	@Transient
	public Set getPnLedgers() {
		return this.pnLedgers;
	}

	public void setPnLedgers(Set pnLedgers) {
		this.pnLedgers = pnLedgers;
	}

	public String toString() {
		return new ToStringBuilder(this).append("invoiceId", getInvoiceId()).toString();
	}

}
