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
package net.project.form.soa;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GetImport")
public class GetImport {

	private int  userId;
	
	private int spaceId;

	private DataHandler formDefinition;
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(int spaceId) {
		this.spaceId = spaceId;
	}

	public DataHandler getFormDefinition() {
		return formDefinition;
	}

	public void setFormDefinition(DataHandler formDefinition) {
		this.formDefinition = formDefinition;
	}
	
	
	
}
