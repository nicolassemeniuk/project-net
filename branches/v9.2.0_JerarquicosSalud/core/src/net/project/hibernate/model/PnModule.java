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
 * PnModule generated by hbm2java
 */
@Entity
@Table(name = "PN_MODULE")
public class PnModule implements java.io.Serializable {

	/** identifier field */
	private Integer moduleId;

	/** persistent field */
	private String name;

	/** nullable persistent field */
	private String description;

	/** persistent field */
	private long defaultPermissionActions;

	/** persistent field */
	private Set pnModuleHasObjectTypes = new HashSet(0);

	private Set pnSpaceHasModules = new HashSet(0);

	public PnModule() {
	}

	public PnModule(Integer moduleId, String name, long defaultPermissionActions) {
		this.moduleId = moduleId;
		this.name = name;
		this.defaultPermissionActions = defaultPermissionActions;
	}

	public PnModule(Integer moduleId, String name, String description, long defaultPermissionActions,
			Set pnModuleHasObjectTypes, Set pnSpaceHasModules) {
		this.moduleId = moduleId;
		this.name = name;
		this.description = description;
		this.defaultPermissionActions = defaultPermissionActions;
		this.pnModuleHasObjectTypes = pnModuleHasObjectTypes;
		this.pnSpaceHasModules = pnSpaceHasModules;
	}

	/** minimal constructor */
	public PnModule(Integer moduleId, String name, long defaultPermissionActions, Set pnModuleHasObjectTypes,
			Set pnSpaceHasModules) {
		this.moduleId = moduleId;
		this.name = name;
		this.defaultPermissionActions = defaultPermissionActions;
		this.pnModuleHasObjectTypes = pnModuleHasObjectTypes;
		this.pnSpaceHasModules = pnSpaceHasModules;
	}

	public PnModule(Integer moduleId, long defaultPermissionActions) {
		this.moduleId = moduleId;
		this.defaultPermissionActions = defaultPermissionActions;
	}

	public PnModule(Integer moduleId) {
		this.moduleId = moduleId;
	}

	@Id
	@Column(name = "MODULE_ID", nullable = false)
	public Integer getModuleId() {
		return this.moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

	@Column(name = "NAME", nullable = false, length = 80)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "DESCRIPTION", length = 500)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "DEFAULT_PERMISSION_ACTIONS", nullable = false, length = 10)
	public long getDefaultPermissionActions() {
		return this.defaultPermissionActions;
	}

	public void setDefaultPermissionActions(long defaultPermissionActions) {
		this.defaultPermissionActions = defaultPermissionActions;
	}

	//@OneToMany(fetch = FetchType.LAZY, mappedBy = "pnModule", targetEntity = PnModuleHasObjectType.class)
	@Transient
	public Set getPnModuleHasObjectTypes() {
		return this.pnModuleHasObjectTypes;
	}

	public void setPnModuleHasObjectTypes(Set pnModuleHasObjectTypes) {
		this.pnModuleHasObjectTypes = pnModuleHasObjectTypes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pnModule", targetEntity = PnSpaceHasModule.class)
	public Set getPnSpaceHasModules() {
		return this.pnSpaceHasModules;
	}

	public void setPnSpaceHasModules(Set pnSpaceHasModules) {
		this.pnSpaceHasModules = pnSpaceHasModules;
	}

	public String toString() {
		return new ToStringBuilder(this).append("moduleId", getModuleId()).toString();
	}

}
