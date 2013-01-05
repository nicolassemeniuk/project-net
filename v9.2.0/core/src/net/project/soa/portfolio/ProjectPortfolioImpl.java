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
package net.project.soa.portfolio;

import net.project.business.BusinessSpace;
import net.project.portfolio.PortfolioManager;
import net.project.portfolio.ProjectPortfolio;
import net.project.portfolio.ProjectPortfolioBean;
import net.project.project.ProjectSpace;
import net.project.security.SessionManager;
import net.project.space.SpaceList;
import net.project.space.SpaceManager;

public class ProjectPortfolioImpl extends ProjectPortfolioBean implements IProjectPortfolio {
	
	public ProjectSpace[] getProjectSpaces() throws Exception {
	    ProjectPortfolio projectPortfolio = new ProjectPortfolio(); 
	    projectPortfolio.clear();
	    projectPortfolio.setID(ProjectPortfolio.getUserPortfolioID(SessionManager.getUser().getID()));
	    projectPortfolio.setUser(SessionManager.getUser());
	    projectPortfolio.load();
	    Object[] arr = projectPortfolio.toArray();
	    ProjectSpace[] projectSpaces = new ProjectSpace[arr.length];
	    for(int i=0;i<arr.length;i++){
	    	projectSpaces[i] = (ProjectSpace)arr[i];
	    }
	    return projectSpaces;
	}
	
	public ProjectSpace[] getProjectSpacesForBusiness() throws Exception {
	    ProjectPortfolio projectPortfolio = new ProjectPortfolio(); 
	    projectPortfolio.clear();
	    projectPortfolio.setID(((BusinessSpace)SessionManager.getUser().getCurrentSpace()).getProjectPortfolioID("owner"));
	    projectPortfolio.load();
	    Object[] arr = projectPortfolio.toArray();
	    ProjectSpace[] projectSpaces = new ProjectSpace[arr.length];
	    for(int i=0;i<arr.length;i++){
	    	projectSpaces[i] = (ProjectSpace)arr[i];
	    }
	    return projectSpaces;
	}
	
	public ProjectSpace[] getSubProjects() throws Exception {
	    ProjectPortfolio projectPortfolio = new ProjectPortfolio(); 
	    projectPortfolio.clear();
	    SpaceList spaceList = SpaceManager.getSubprojects(((ProjectSpace)SessionManager.getUser().getCurrentSpace()));
	    projectPortfolio = (ProjectPortfolio)PortfolioManager.makePortfolioFromSpaceList(spaceList);
	    projectPortfolio.setUser(SessionManager.getUser());
	    projectPortfolio.load();
	    Object[] arr = projectPortfolio.toArray();
	    ProjectSpace[] projectSpaces = new ProjectSpace[arr.length];
	    for(int i=0;i<arr.length;i++){
	    	projectSpaces[i] = (ProjectSpace)arr[i];
	    }
	    return projectSpaces;
	}	

}
