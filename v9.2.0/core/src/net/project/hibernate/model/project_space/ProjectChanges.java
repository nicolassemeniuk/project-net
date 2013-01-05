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

import java.util.ArrayList;
import java.util.List;

public class ProjectChanges {

	private List<ObjectChanged> discussions = new ArrayList<ObjectChanged>();
	
	private List<ObjectChanged> documents = new ArrayList<ObjectChanged>();
	
	private List<ObjectChanged> forms = new ArrayList<ObjectChanged>();
	
	public ProjectChanges() {
		super();
	}

	/**
	 * @return Returns the discussions.
	 */
	public List<ObjectChanged> getDiscussions() {
		return discussions;
	}

	/**
	 * @param discussions The discussions to set.
	 */
	public void setDiscussions(List<ObjectChanged> discussions) {
		this.discussions = discussions;
	}

	/**
	 * @return Returns the documents.
	 */
	public List<ObjectChanged> getDocuments() {
		return documents;
	}

	/**
	 * @param documents The documents to set.
	 */
	public void setDocuments(List<ObjectChanged> documents) {
		this.documents = documents;
	}

	/**
	 * @return Returns the forms.
	 */
	public List<ObjectChanged> getForms() {
		return forms;
	}

	/**
	 * @param forms The forms to set.
	 */
	public void setForms(List<ObjectChanged> forms) {
		this.forms = forms;
	}

	
	
}
