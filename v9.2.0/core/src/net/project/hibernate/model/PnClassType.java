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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PnClassType generated by hbm2java
 */
@Entity
@Table(name = "PN_CLASS_TYPE")
public class PnClassType implements java.io.Serializable {

	/** identifier field */
	private Integer classTypeId;

	/** persistent field */
	private String classTypeName;

	/** nullable persistent field */
	private String classTypeDesc;

	/** persistent field */
	private Set pnClasses = new HashSet(0);

	/** persistent field */
	private Set pnClassTypeElements = new HashSet(0);

	public PnClassType() {
	}

	public PnClassType(Integer classTypeId, String classTypeName) {
		this.classTypeId = classTypeId;
		this.classTypeName = classTypeName;
	}

	public PnClassType(Integer classTypeId, String classTypeName, String classTypeDesc, Set pnClasses,
			Set pnClassTypeElements) {
		this.classTypeId = classTypeId;
		this.classTypeName = classTypeName;
		this.classTypeDesc = classTypeDesc;
		this.pnClasses = pnClasses;
		this.pnClassTypeElements = pnClassTypeElements;
	}

	/** minimal constructor */
	public PnClassType(Integer classTypeId, String classTypeName, Set pnClasses, Set pnClassTypeElements) {
		this.classTypeId = classTypeId;
		this.classTypeName = classTypeName;
		this.pnClasses = pnClasses;
		this.pnClassTypeElements = pnClassTypeElements;
	}

	@Id
	@Column(name = "CLASS_TYPE_ID", nullable = false)
	public Integer getClassTypeId() {
		return this.classTypeId;
	}

	public void setClassTypeId(Integer classTypeId) {
		this.classTypeId = classTypeId;
	}

	@Column(name = "CLASS_TYPE_NAME", nullable = false, length = 80)
	public String getClassTypeName() {
		return this.classTypeName;
	}

	public void setClassTypeName(String classTypeName) {
		this.classTypeName = classTypeName;
	}

	@Column(name = "CLASS_TYPE_DESC", length = 500)
	public String getClassTypeDesc() {
		return this.classTypeDesc;
	}

	public void setClassTypeDesc(String classTypeDesc) {
		this.classTypeDesc = classTypeDesc;
	}

	//@OneToMany(fetch = FetchType.LAZY, mappedBy = "pnClassType", targetEntity = PnClass.class)
	@Transient
	public Set getPnClasses() {
		return this.pnClasses;
	}

	public void setPnClasses(Set pnClasses) {
		this.pnClasses = pnClasses;
	}

	//@OneToMany(fetch = FetchType.LAZY, mappedBy = "pnClassType", targetEntity = PnClassTypeElement.class)
	@Transient
	public Set getPnClassTypeElements() {
		return this.pnClassTypeElements;
	}

	public void setPnClassTypeElements(Set pnClassTypeElements) {
		this.pnClassTypeElements = pnClassTypeElements;
	}

	public String toString() {
		return new ToStringBuilder(this).append("classTypeId", getClassTypeId()).toString();
	}

}
