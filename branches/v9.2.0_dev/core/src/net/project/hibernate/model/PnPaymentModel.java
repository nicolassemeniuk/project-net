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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PnPaymentModel generated by hbm2java
 */
@Entity
@Table(name = "PN_PAYMENT_MODEL")
public class PnPaymentModel implements Serializable {

	private Integer paymentModelId;

	private PnPaymentModelType pnPaymentModelType;

	private PnPaymentModelCharge pnPaymentModelCharge;

	private Set pnPaymentInformations = new HashSet(0);

	public PnPaymentModel() {
	}

	public PnPaymentModel(Integer paymentModelId, net.project.hibernate.model.PnPaymentModelType pnPaymentModelType,
			Set pnPaymentInformations) {
		this.paymentModelId = paymentModelId;
		this.pnPaymentModelType = pnPaymentModelType;
		this.pnPaymentInformations = pnPaymentInformations;
	}

	public PnPaymentModel(Integer paymentModelId,
			net.project.hibernate.model.PnPaymentModelCharge pnPaymentModelCharge,
			net.project.hibernate.model.PnPaymentModelType pnPaymentModelType, Set pnPaymentInformations) {
		this.paymentModelId = paymentModelId;
		this.pnPaymentModelCharge = pnPaymentModelCharge;
		this.pnPaymentModelType = pnPaymentModelType;
		this.pnPaymentInformations = pnPaymentInformations;
	}

	@Id
	@Column(name = "PAYMENT_MODEL_ID", nullable = false)
	public Integer getPaymentModelId() {
		return this.paymentModelId;
	}

	public void setPaymentModelId(Integer paymentModelId) {
		this.paymentModelId = paymentModelId;
	}

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = PnPaymentModelType.class)
	@JoinColumn(name = "MODEL_TYPE_ID")
	public PnPaymentModelType getPnPaymentModelType() {
		return this.pnPaymentModelType;
	}

	public void setPnPaymentModelType(PnPaymentModelType pnPaymentModelType) {
		this.pnPaymentModelType = pnPaymentModelType;
	}

	@OneToOne(fetch = FetchType.LAZY, targetEntity = PnPaymentModelCharge.class)
	public PnPaymentModelCharge getPnPaymentModelCharge() {
		return this.pnPaymentModelCharge;
	}

	public void setPnPaymentModelCharge(PnPaymentModelCharge pnPaymentModelCharge) {
		this.pnPaymentModelCharge = pnPaymentModelCharge;
	}

	//@OneToMany(fetch = FetchType.LAZY, mappedBy = "pnPaymentModel", targetEntity = PnPaymentInformation.class)
	@Transient
	public Set getPnPaymentInformations() {
		return this.pnPaymentInformations;
	}

	public void setPnPaymentInformations(Set pnPaymentInformations) {
		this.pnPaymentInformations = pnPaymentInformations;
	}

	public String toString() {
		return new ToStringBuilder(this).append("paymentModelId", getPaymentModelId()).toString();
	}

}
