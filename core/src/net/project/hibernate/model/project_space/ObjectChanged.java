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
package net.project.hibernate.model.project_space;

import java.util.Date;

public class ObjectChanged {

	private Integer objectId;
	
	private String objectName;
	
	private Date dateOfChange; 
	
	private Integer numberOfChanges;
	
	
	
	/**
	 * @return Returns the dateOfChange.
	 */
	public Date getDateOfChange() {
		return dateOfChange;
	}



	/**
	 * @param dateOfChange The dateOfChange to set.
	 */
	public void setDateOfChange(Date dateOfChange) {
		this.dateOfChange = dateOfChange;
	}



	/**
	 * @return Returns the numberOfChanges.
	 */
	public Integer getNumberOfChanges() {
		return numberOfChanges;
	}



	/**
	 * @param numberOfChanges The numberOfChanges to set.
	 */
	public void setNumberOfChanges(Integer numberOfChanges) {
		this.numberOfChanges = numberOfChanges;
	}



	/**
	 * @return Returns the objectId.
	 */
	public Integer getObjectId() {
		return objectId;
	}



	/**
	 * @param objectId The objectId to set.
	 */
	public void setObjectId(Integer objectId) {
		this.objectId = objectId;
	}



	/**
	 * @return Returns the objectName.
	 */
	public String getObjectName() {
		return objectName;
	}



	/**
	 * @param objectName The objectName to set.
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}



	public ObjectChanged() {
		super();
	}



	/**
	 * @param id
	 * @param name
	 * @param change
	 */
	public ObjectChanged(Integer objectId, String objectName, Date dateOfChange) {
		// TODO Auto-generated constructor stub
		this.objectId = objectId;
		this.objectName = objectName;
		this.dateOfChange = dateOfChange;
	}



	/**
	 * @param id
	 * @param name
	 * @param changes
	 */
	public ObjectChanged(Integer objectId, String objectName, Long numberOfChanges) {
		// TODO Auto-generated constructor stub
		this.objectId = objectId;
		this.objectName = objectName;
		this.numberOfChanges = numberOfChanges.intValue();
	}

	
	
}
