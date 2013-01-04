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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * QrtzCronTrigger generated by hbm2java
 */
@Entity
@Table(name = "QRTZ_CRON_TRIGGERS")
public class QrtzCronTrigger implements Serializable {

	private QrtzCronTriggerPK comp_id;

	private String cronExpression;

	private String timeZoneId;

	private QrtzTrigger qrtzTrigger;

	public QrtzCronTrigger() {
	}

	public QrtzCronTrigger(QrtzCronTriggerPK comp_id, String cronExpression) {
		this.comp_id = comp_id;
		this.cronExpression = cronExpression;
	}

	public QrtzCronTrigger(QrtzCronTriggerPK comp_id, String cronExpression, String timeZoneId, QrtzTrigger qrtzTrigger) {
		this.comp_id = comp_id;
		this.cronExpression = cronExpression;
		this.timeZoneId = timeZoneId;
		this.qrtzTrigger = qrtzTrigger;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "triggerName", column = @Column(name = "TRIGGER_NAME", nullable = false, length = 80)),
			@AttributeOverride(name = "triggerGroup", column = @Column(name = "TRIGGER_GROUP", nullable = false, length = 80)) })
	public QrtzCronTriggerPK getComp_id() {
		return this.comp_id;
	}

	public void setComp_id(QrtzCronTriggerPK comp_id) {
		this.comp_id = comp_id;
	}

	@Column(name = "CRON_EXPRESSION", nullable = false, length = 80)
	public String getCronExpression() {
		return this.cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	@Column(name = "TIME_ZONE_ID", length = 80)
	public String getTimeZoneId() {
		return this.timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	//@OneToOne(fetch = FetchType.LAZY, targetEntity = QrtzTrigger.class)
	@Transient
	public QrtzTrigger getQrtzTrigger() {
		return this.qrtzTrigger;
	}

	public void setQrtzTrigger(QrtzTrigger qrtzTrigger) {
		this.qrtzTrigger = qrtzTrigger;
	}

	public String toString() {
		return new ToStringBuilder(this).append("comp_id", getComp_id()).toString();
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if (!(other instanceof QrtzCronTrigger))
			return false;
		QrtzCronTrigger castOther = (QrtzCronTrigger) other;
		return new EqualsBuilder().append(this.getComp_id(), castOther.getComp_id()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getComp_id()).toHashCode();
	}

}
