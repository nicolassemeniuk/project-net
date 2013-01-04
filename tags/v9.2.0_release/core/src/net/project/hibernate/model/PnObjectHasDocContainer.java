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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PnObjectHasDocContainer generated by hbm2java
 */
@Entity
@Table(name = "PN_OBJECT_HAS_DOC_CONTAINER")
public class PnObjectHasDocContainer implements Serializable {

	private PnObjectHasDocContainerPK comp_id;

	public PnObjectHasDocContainer() {
	}

	public PnObjectHasDocContainer(PnObjectHasDocContainerPK comp_id) {
		this.comp_id = comp_id;
	}
	
	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "objectId", column = @Column(name = "OBJECT_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "containerId", column = @Column(name = "CONTAINER_ID", nullable = false, length = 20)) })
	public PnObjectHasDocContainerPK getComp_id() {
		return comp_id;
	}

	public void setComp_id(PnObjectHasDocContainerPK comp_id) {
		this.comp_id = comp_id;
	}

	public String toString() {
		return new ToStringBuilder(this).append("comp_id", getComp_id()).toString();
	}

}
