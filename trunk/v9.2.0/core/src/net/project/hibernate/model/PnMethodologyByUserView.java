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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PnMethodologyByUserView generated by hbm2java
 */
@Entity
@Table(name = "PN_METHODOLOGY_BY_USER_VIEW")
public class PnMethodologyByUserView implements java.io.Serializable {

	private PnMethodologyByUserViewPK comp_id;

	public PnMethodologyByUserView() {
	}

	public PnMethodologyByUserView(PnMethodologyByUserViewPK comp_id) {
		this.comp_id = comp_id;
	}
	
	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "methodologyId", column = @Column(name = "METHODOLOGY_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "methodologyName", column = @Column(name = "METHODOLOGY_NAME", nullable = false, length = 80)),
			@AttributeOverride(name = "methodologyDesc", column = @Column(name = "METHODOLOGY_DESC", nullable = false, length = 100)),
			@AttributeOverride(name = "isGlobal", column = @Column(name = "IS_GLOBAL", nullable = false, length = 1)),
			@AttributeOverride(name = "parentSpaceId", column = @Column(name = "PARENT_SPACE_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "childSpaceId", column = @Column(name = "CHILD_SPACE_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "personId", column = @Column(name = "PERSON_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "person", column = @Column(name = "PERSON", nullable = false, length = 4000)),
			@AttributeOverride(name = "recordStatus", column = @Column(name = "RECORD_STATUS", nullable = false, length = 1)) })
	public PnMethodologyByUserViewPK getComp_id() {
		return comp_id;
	}

	public void setComp_id(PnMethodologyByUserViewPK comp_id) {
		this.comp_id = comp_id;
	}

	public String toString() {
		return new ToStringBuilder(this).append("").toString();
	}

}
