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
package net.project.view.components;

import net.project.base.property.PropertyProvider;
import net.project.view.pages.resource.management.AssignmentSummary;
import net.project.view.pages.resource.management.Dashboard;
import net.project.view.pages.resource.management.Remaining;
import net.project.view.pages.resource.management.ViewDetails;
import net.project.view.pages.resource.management.ViewDetails_V8_5;
import net.project.view.pages.resource.management.ViewSummary;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.SetupRender;

/**
 * @author
 * 
 */
public class NavBar {

	private static Logger log = Logger.getLogger(NavBar.class);

	@InjectPage
	private Dashboard dashboard;

	@InjectPage
	private ViewDetails view_details;

	@InjectPage
	private ViewDetails_V8_5 view_details_V8_5;

	@InjectPage
	private AssignmentSummary assignment_summary; // For Single Project
													// Assignment Summary
													// (currently no link
													// provided)

	@InjectPage
	private ViewSummary view_summary;

	@InjectPage
	private Remaining remaining;

	@Persist
	private String dashboard_link;

	@Persist
	private String view_by_project_link;

	@Persist
	private String people_title;

	@Persist
	private String list_link;

	@Persist
	private String pools_link;

	@Persist
	private String skills_link;

	@Persist
	private String equipment_link;

	@Persist
	private String resource_requests_link;

	@Persist
	private String setup_link;

	@ApplicationState
	private String jSPRootURL;

	@Persist
	private boolean allocationsEnabled;
	@Persist
	private boolean directoryEnabled;

	/**
	 *Method for initializing token values 
	 */
    @SetupRender
	public void initializeTokens() {
		try {
			people_title = PropertyProvider.get("prm.resource.nav.people");
			list_link = PropertyProvider.get("prm.resource.nav.list");
			pools_link = PropertyProvider.get("prm.resource.nav.pools");
			skills_link = PropertyProvider.get("prm.resource.nav.skills");
			equipment_link = PropertyProvider.get("prm.resource.nav.equipment");
			resource_requests_link = PropertyProvider.get("prm.resource.nav.resource_requests");
			setup_link = PropertyProvider.get("prm.resource.nav.setup");
			allocationsEnabled = PropertyProvider.getBoolean("prm.resource.allocations.isenabled");
			directoryEnabled = PropertyProvider.getBoolean("prm.resource.directory.isenabled");
		} catch (Exception ex) {
			log.error("Error occured while getting NavBar component property values : "+ex.getMessage());
		}
	}

	/**
	* @return Returns the people_title.
	*/
	public String getPeople_title() {
		return people_title;
	}

	/**
	 * @param people_title
	 *            The people_title to set.
	 */
	public void setPeople_title(String people_title) {
		this.people_title = people_title;
	}
	
	/**
	 * @return Returns the dashboard_link.
	 */
	public String getDashboard_link() {
		return dashboard_link;
	}

	/**
	 * @param dashboard_link
	 *            The dashboard_link to set.
	 */
	public void setDashboard_link(String dashboard_link) {
		this.dashboard_link = dashboard_link;
	}

	/**
	 * @return Returns the equipment_link.
	 */
	public String getEquipment_link() {
		return equipment_link;
	}

	/**
	 * @param equipment_link
	 *            The equipment_link to set.
	 */
	public void setEquipment_link(String equipment_link) {
		this.equipment_link = equipment_link;
	}

	/**
	 * @return Returns the list_link.
	 */
	public String getList_link() {
		return list_link;
	}

	/**
	 * @param list_link
	 *            The list_link to set.
	 */
	public void setList_link(String list_link) {
		this.list_link = list_link;
	}

	/**
	 * @return Returns the pools_link.
	 */
	public String getPools_link() {
		return pools_link;
	}

	/**
	 * @param pools_link
	 *            The pools_link to set.
	 */
	public void setPools_link(String pools_link) {
		this.pools_link = pools_link;
	}

	/**
	 * @return Returns the resource_requests_link.
	 */
	public String getResource_requests_link() {
		return resource_requests_link;
	}

	/**
	 * @param resource_requests_link
	 *            The resource_requests_link to set.
	 */
	public void setResource_requests_link(String resource_requests_link) {
		this.resource_requests_link = resource_requests_link;
	}

	/**
	 * @return Returns the setup_link.
	 */
	public String getSetup_link() {
		return setup_link;
	}

	/**
	 * @param setup_link
	 *            The setup_link to set.
	 */
	public void setSetup_link(String setup_link) {
		this.setup_link = setup_link;
	}

	/**
	 * @return Returns the skills_link.
	 */
	public String getSkills_link() {
		return skills_link;
	}

	/**
	 * @param skills_link
	 *            The skills_link to set.
	 */
	public void setSkills_link(String skills_link) {
		this.skills_link = skills_link;
	}

	/**
	 * @return Returns the view_by_project_link.
	 */
	public String getView_by_project_link() {
		return view_by_project_link;
	}

	/**
	 * @param view_by_project_link
	 *            The view_by_project_link to set.
	 */
	public void setView_by_project_link(String view_by_project_link) {
		this.view_by_project_link = view_by_project_link;
	}

	public Object onActionFromDashboard() {
		dashboard.setMessage("Dashboard page");
		return dashboard;
	}

	public Object onActionFromAllocate_by_resource() {
		remaining
				.setMessage("Allocate_by_resource page ... Work in progress...");
		return remaining;
	}

	public Object onActionFromAllocate_by_project() {
		remaining
				.setMessage("Allocate_by_project page ... Work in progress...");
		return remaining;
	}

	public Object onActionFromView_by_resource() {
		remaining.setMessage("View_by_resource page ... Work in progress...");
		return remaining;
	}

	public Object onActionFromView_by_project() {
		remaining.setMessage("View_by_project page ... Work in progress...");
		return remaining;
	}

	public Object onActionFromView_vs_assignments() {
		remaining
				.setMessage("View_vs_assignments page ... Work in progress...");
		return remaining;
	}

	public Object onActionFromView_details() {
		view_details.setFromDate(null);
		view_details.setToDate(null);
		view_details.setDateCriteria("");
		view_details.getAssignmentDetails(null, null, null);
		return view_details;
	}

	public Object onActionFromView_details_V8_5() {
		view_details_V8_5.setStartDate(null);
		view_details_V8_5.setEndDate(null);
		view_details_V8_5.setDateCriteria("");
		view_details_V8_5.getAssignmentDetails(null, null, null);
		return view_details_V8_5;
	}

	public Object onActionFromView_summary() {
		return view_summary;
	}

	/*
	 * public Object onActionFromView_summary() { return assignment_summary; }
	 */

	public Object onActionFromLists() {
		remaining.setMessage("Lists page... Work in progress...");
		return remaining;
	}

	public Object onActionFromPools() {
		remaining.setMessage("Pools page ... Work in progress...");
		return remaining;
	}

	public Object onActionFromSkills() {
		remaining.setMessage("Skills page ... Work in progress...");
		return remaining;
	}

	public Object onActionFromEquipment() {
		remaining.setMessage("Equipment page ... Work in progress...");
		return remaining;
	}

	public Object onActionFromResource_requests() {
		remaining.setMessage("Resource requests page ... Work in progress...");
		return remaining;
	}

	public Object onActionFromSetup() {
		remaining.setMessage("Setup page... Work in progress...");
		return remaining;
	}

	/**
	 * @return the jSPRootURL
	 */
	public String getJSPRootURL() {
		return jSPRootURL;
	}

	/**
	 * @return the isAllocationsEnabled
	 */
	public boolean isAllocationsEnabled() {
		return allocationsEnabled;
	}


	/**
	 * @return the isResourceDirectory
	 */
	public boolean getdirectoryEnabled() {
		return directoryEnabled;
	}

}
