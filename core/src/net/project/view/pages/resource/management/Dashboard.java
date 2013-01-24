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
/**
 * 
 */
package net.project.view.pages.resource.management;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.document.DocumentManagerBean;
import net.project.hibernate.model.PnAssignment;
import net.project.hibernate.service.IPnAssignmentService;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SecurityProvider;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.ResourcesSpace;
import net.project.space.Space;
import net.project.util.DateFormat;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

/**
 * @author
 */
public class Dashboard {
	
	private static Logger log; 
	
	private DateFormat dateFormat;
	
	@Persist
	private String pageTitle;
	
	@Persist
	private String gridtitle;
	
	@Persist
	private String resourceIdColumnLabel;
	
	@Persist
	private String resourceColumnLabel;
	
	@Persist
	private String percentAllocatedMaxColumnLabel;
	
	@Persist
	private String firstOverAllocationColumnLabel;
		
	@Persist
	private String message;

	@Inject
	private RequestGlobals requestGlobals;

	@Persist
	private String overAllocatedResourcesData;
	
	@ApplicationState
	private String jSPRootURL;

	public Dashboard() {
		try {
			log = Logger.getLogger(ViewDetails.class);
			pageTitle = PropertyProvider.get("prm.resource.dashboard.pagetitle");
			gridtitle = PropertyProvider.get("prm.resource.dashboard.gridtitle");
			resourceIdColumnLabel = PropertyProvider.get("prm.resource.dashboard.column.resourceid.label");
			resourceColumnLabel = PropertyProvider.get("prm.resource.dashboard.column.resource.label");
			percentAllocatedMaxColumnLabel = PropertyProvider.get("prm.resource.dashboard.column.percentallocatedmax.label");
			firstOverAllocationColumnLabel = PropertyProvider.get("prm.resource.dashboard.column.firstoverallocation.label");			
			dateFormat = SessionManager.getUser().getDateFormatter();
			message = null;
		}catch (Exception e) {
			log.error("Error occured while getting Dahboard page property values.");			
		}
		this.overAllocatedResourcesData = "[ ]";
	}

	@SetupRender
	void initialzeValues() {
		HttpSession session = requestGlobals.getHTTPServletRequest().getSession();
		User user = SessionManager.getUser();
		SecurityProvider securityProvider = (SecurityProvider) session.getAttribute("securityProvider");
		Space resourcesSpace = new ResourcesSpace();
		resourcesSpace = securityProvider.getSpace();
		securityProvider.setSpace(resourcesSpace);
		DocumentManagerBean docManager = (DocumentManagerBean) session.getAttribute("docManager");
		try {
			user.setCurrentSpace(resourcesSpace);
		} catch (PnetException pnetEx) {
			pnetEx.printStackTrace();
		}
		user.getCurrentSpace().getRoster().reload();
		docManager.getNavigator().put("TopContainerReturnTo", SessionManager.getJSPRootURL() + "/resource/management/Dashboard");

		HttpServletRequest request = requestGlobals.getHTTPServletRequest();
		request.setAttribute("module", Integer.toString(net.project.base.Module.RESOURCES_SPACE));
		session.setAttribute("resourcesSpace", resourcesSpace);
		message = null;	
		
		// generating grid data from database
		getGridDataValues();
	}

	public void getGridDataValues() {
		List<PnAssignment> assignementList = new ArrayList<PnAssignment>();
		IPnAssignmentService assignmentService = ServiceFactory.getInstance().getPnAssignmentService();
		assignementList = assignmentService.getOverAllocatedResources(Integer
				.parseInt(SessionManager.getUser().getID()));
		/*assignementList = assignmentService.getOverAssignedResources(Integer
				.parseInt(SessionManager.getUser().getID()), null, null);*/
		Integer personId = 0;
		
		DateFormat dateFormat = new DateFormat(SessionManager.getUser());
		
		overAllocatedResourcesData = "";
		overAllocatedResourcesData = "[ ";
		if (assignementList != null) {
			for (PnAssignment assignment : assignementList) {
				if (personId.intValue() != assignment.getPnPerson().getPersonId().intValue()) {
					overAllocatedResourcesData += " [";
					overAllocatedResourcesData += assignment.getPnPerson().getPersonId() + ", ";
					overAllocatedResourcesData += "'" + assignment.getPnPerson().getDisplayName() + "', ";
					overAllocatedResourcesData += assignment.getPercentAllocated() + ", ";
					overAllocatedResourcesData += " '" + dateFormat.formatDate(assignment.getStartDate(),"MMM yyyy") + "'";
					overAllocatedResourcesData += " ],";
					personId = assignment.getPnPerson().getPersonId().intValue();
				} else {
					break;
				}
			}
		}
		overAllocatedResourcesData = overAllocatedResourcesData.substring(0, overAllocatedResourcesData.length() - 1)
				+ " ]";		
		if(overAllocatedResourcesData.equals("[ ]")){
			setMessage(PropertyProvider.get("prm.resource.dashboard.message.nooverallocatedresources"));
		}
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the overAllocatedResourcesData
	 */
	public String getOverAllocatedResourcesData() {
		return overAllocatedResourcesData;
	}

	/**
	 * @param overAllocatedResourcesData
	 *            the overAllocatedResourcesData to set
	 */
	public void setOverAllocatedResourcesData(String overAllocatedResourcesData) {
		this.overAllocatedResourcesData = overAllocatedResourcesData;
	}

	/**
	 * @return the firstOverAllocationColumnLabel
	 */
	public String getFirstOverAllocationColumnLabel() {
		return firstOverAllocationColumnLabel;
	}

	/**
	 * @return the gridtitle
	 */
	public String getGridtitle() {
		return gridtitle;
	}

	/**
	 * @return the pageTitle
	 */
	public String getPageTitle() {
		return pageTitle;
	}
	
	/**
	 * @return the percentAllocatedMaxColumnLabel
	 */
	public String getPercentAllocatedMaxColumnLabel() {
		return percentAllocatedMaxColumnLabel;
	}

	/**
	 * @return the resourceColumnLabel
	 */
	public String getResourceColumnLabel() {
		return resourceColumnLabel;
	}

	/**
	 * @return the resourceIdColumnLabel
	 */
	public String getResourceIdColumnLabel() {
		return resourceIdColumnLabel;
	}
	/**
	 * @return the jSPRootURL
	 */
	public String getJSPRootURL() {
		return jSPRootURL;
	}

}
