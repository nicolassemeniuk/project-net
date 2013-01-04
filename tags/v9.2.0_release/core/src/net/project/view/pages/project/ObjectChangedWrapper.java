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
package net.project.view.pages.project;

import java.io.Serializable;

import net.project.base.Module;
import net.project.hibernate.model.project_space.ObjectChanged;
import net.project.security.SessionManager;

public class ObjectChangedWrapper implements Serializable {
    
    private ObjectChanged objectChanged;
    
    public ObjectChangedWrapper() {
        
    }
    
    public ObjectChangedWrapper(ObjectChanged objectChanged) {
        this.objectChanged = objectChanged;
    }
    
    public String getName() {
        return this.objectChanged.getObjectName();
    }
    
    public String getDate() {
        String date = "";
        if(objectChanged.getDateOfChange() != null) {
            date = SessionManager.getUser().getDateFormatter().formatDate(objectChanged.getDateOfChange(), "h:ma, MM/dd/yyyy");
        }
        return date;
    }
    
    public String getDocumentUrl() {
        return "/document/GoToDocument.jsp?id=" + this.objectChanged.getObjectId() + "&module=" + Module.DOCUMENT;
    }
    
    public String getDiscussionUrl() {
        return "/discussion/GroupView.jsp?module=" + Module.DISCUSSION + "&action=1&id=" + objectChanged.getObjectId();
    }

	/**
	 * @return the objectChanged
	 */
	public ObjectChanged getObjectChanged() {
		return objectChanged;
	}

	/**
	 * @param objectChanged the objectChanged to set
	 */
	public void setObjectChanged(ObjectChanged objectChanged) {
		this.objectChanged = objectChanged;
	}
}
