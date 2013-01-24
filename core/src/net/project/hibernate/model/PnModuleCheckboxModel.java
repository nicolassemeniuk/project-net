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

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PnModuleCheckboxModel
 */
public class PnModuleCheckboxModel implements java.io.Serializable {

	/** PnModule field */
	private PnModule module;

	/** isSelected field */
	private boolean isSelected = false;
	
	private String displayName;

	public PnModuleCheckboxModel() {
	}

	public PnModuleCheckboxModel(PnModule module, boolean isSelected) {
		this.module = module;
		this.isSelected = isSelected;
	}

	/**
	 * @return the module
	 */
	public PnModule getModule() {
		return module;
	}

	/**
	 * @param module the module to set
	 */
	public void setModule(PnModule module) {
		this.module = module;
	}

	/**
	 * @return the isSelected
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * @param isSelected the isSelected to set
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String toString() {
		return new ToStringBuilder(this).append("moduleId", module.getModuleId()).toString();
	}

}
