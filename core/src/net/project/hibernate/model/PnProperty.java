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
import java.sql.Clob;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *        For brand name terminology lookups for 
 *     
 */
@Entity
@Table(name = "PN_PROPERTY")
public class PnProperty implements Serializable {

	private PnPropertyPK comp_id;

	private String propertyType;

	private String propertyValue;

	private String recordStatus;

	private int isSystemProperty;

	private int isTranslatableProperty;

	private Clob propertyValueClob;

	private PnLanguage pnLanguage;

	public PnProperty() {
	}

	public PnProperty(PnPropertyPK comp_id, String propertyType, int isSystemProperty, int isTranslatableProperty) {
		this.comp_id = comp_id;
		this.propertyType = propertyType;
		this.isSystemProperty = isSystemProperty;
		this.isTranslatableProperty = isTranslatableProperty;
	}

	public PnProperty(PnPropertyPK comp_id, String propertyType, String propertyValue, String recordStatus,
			int isSystemProperty, int isTranslatableProperty, Clob propertyValueClob, PnLanguage pnLanguage) {
		this.comp_id = comp_id;
		this.propertyType = propertyType;
		this.propertyValue = propertyValue;
		this.recordStatus = recordStatus;
		this.isSystemProperty = isSystemProperty;
		this.isTranslatableProperty = isTranslatableProperty;
		this.propertyValueClob = propertyValueClob;
		this.pnLanguage = pnLanguage;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "contextId", column = @Column(name = "CONTEXT_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "language", column = @Column(name = "LANGUAGE", nullable = false, length = 2)),
			@AttributeOverride(name = "property", column = @Column(name = "PROPERTY", nullable = false, length = 500)) })
	public PnPropertyPK getComp_id() {
		return this.comp_id;
	}

	public void setComp_id(PnPropertyPK comp_id) {
		this.comp_id = comp_id;
	}

	@Column(name = "PROPERTY_TYPE", nullable = false, length = 40)
	public String getPropertyType() {
		return this.propertyType;
	}

	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	@Column(name = "PROPERTY_VALUE", length = 4000)
	public String getPropertyValue() {
		return this.propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	@Column(name = "RECORD_STATUS", length = 1)
	public String getRecordStatus() {
		return this.recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	@Column(name = "IS_SYSTEM_PROPERTY", nullable = false, length = 1)
	public int getIsSystemProperty() {
		return this.isSystemProperty;
	}

	public void setIsSystemProperty(int isSystemProperty) {
		this.isSystemProperty = isSystemProperty;
	}

	@Column(name = "IS_TRANSLATABLE_PROPERTY", nullable = false, length = 1)
	public int getIsTranslatableProperty() {
		return this.isTranslatableProperty;
	}

	public void setIsTranslatableProperty(int isTranslatableProperty) {
		this.isTranslatableProperty = isTranslatableProperty;
	}

	@Column(name = "PROPERTY_VALUE_CLOB", length = 4000)
	public Clob getPropertyValueClob() {
		return this.propertyValueClob;
	}

	public void setPropertyValueClob(Clob propertyValueClob) {
		this.propertyValueClob = propertyValueClob;
	}

	//@ManyToOne(fetch = FetchType.LAZY, targetEntity = PnLanguage.class)
	//@JoinColumn(name = "LANGUAGE", insertable = false, updatable = false)
	@Transient
	public PnLanguage getPnLanguage() {
		return this.pnLanguage;
	}

	public void setPnLanguage(PnLanguage pnLanguage) {
		this.pnLanguage = pnLanguage;
	}

	public String toString() {
		return new ToStringBuilder(this).append("comp_id", getComp_id()).toString();
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if (!(other instanceof PnProperty))
			return false;
		PnProperty castOther = (PnProperty) other;
		return new EqualsBuilder().append(this.getComp_id(), castOther.getComp_id()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getComp_id()).toHashCode();
	}

}
