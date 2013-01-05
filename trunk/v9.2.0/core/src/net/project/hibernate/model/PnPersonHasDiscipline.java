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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * PnPersonHasDiscipline generated by hbm2java
 */
@Entity
@Table(name = "PN_PERSON_HAS_DISCIPLINE")
public class PnPersonHasDiscipline implements Serializable {

	private PnPersonHasDisciplinePK comp_id;

	private String otherDiscipline;

	private PnPerson pnPerson;

	private PnDisciplineLookup pnDisciplineLookup;

	public PnPersonHasDiscipline() {
	}

	public PnPersonHasDiscipline(PnPersonHasDisciplinePK comp_id) {
		this.comp_id = comp_id;
	}

	public PnPersonHasDiscipline(PnPersonHasDisciplinePK comp_id, String otherDiscipline, PnPerson pnPerson,
			PnDisciplineLookup pnDisciplineLookup) {
		this.comp_id = comp_id;
		this.otherDiscipline = otherDiscipline;
		this.pnPerson = pnPerson;
		this.pnDisciplineLookup = pnDisciplineLookup;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "personId", column = @Column(name = "PERSON_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "disciplineCode", column = @Column(name = "DISCIPLINE_CODE", nullable = false, length = 4)) })
	public PnPersonHasDisciplinePK getComp_id() {
		return this.comp_id;
	}

	public void setComp_id(PnPersonHasDisciplinePK comp_id) {
		this.comp_id = comp_id;
	}

	@Column(name = "OTHER_DISCIPLINE", length = 30)
	public String getOtherDiscipline() {
		return this.otherDiscipline;
	}

	public void setOtherDiscipline(String otherDiscipline) {
		this.otherDiscipline = otherDiscipline;
	}

	//@ManyToOne(fetch = FetchType.LAZY, targetEntity = PnPerson.class)
	//@JoinColumn(name = "PERSON_ID", insertable = false, updatable = false)
	@Transient
	public PnPerson getPnPerson() {
		return this.pnPerson;
	}

	public void setPnPerson(PnPerson pnPerson) {
		this.pnPerson = pnPerson;
	}

	//@ManyToOne(fetch = FetchType.LAZY, targetEntity = PnDisciplineLookup.class)
	//@JoinColumn(name = "DISCIPLINE_CODE", insertable = false, updatable = false)
	@Transient
	public PnDisciplineLookup getPnDisciplineLookup() {
		return this.pnDisciplineLookup;
	}

	public void setPnDisciplineLookup(PnDisciplineLookup pnDisciplineLookup) {
		this.pnDisciplineLookup = pnDisciplineLookup;
	}

	public String toString() {
		return new ToStringBuilder(this).append("comp_id", getComp_id()).toString();
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if (!(other instanceof PnPersonHasDiscipline))
			return false;
		PnPersonHasDiscipline castOther = (PnPersonHasDiscipline) other;
		return new EqualsBuilder().append(this.getComp_id(), castOther.getComp_id()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getComp_id()).toHashCode();
	}

}
