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
package net.project.view.components;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.security.SessionManager;
import net.project.view.pages.blog.ViewBlog;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.SetupRender;

/**
 * @author
 *
 */
public class ProjectMenu {
	
	private static Logger log;
	
	@ApplicationState
	private String JSPRootURL;

	private boolean contactIsenabled;
	
	private boolean directoryIsenabled;
	
	private boolean blogIsenabled;
	
	private boolean wikiIsenabled;
		 
	private boolean documentIsenabled;
	
	private boolean discussionIsenabled;
		 
	private boolean processIsenabled;

	private boolean schedulingIsenabled;

	private boolean workflowIsenabled;

	private boolean newsIsenabled;

	private boolean subprojectIsenabled;

	private boolean reportsIsenabled;

	private boolean setupIsenabled;
	
	private boolean isBlogPage;
	
	private Integer projectModule;

	private Integer directoryModule;

	private Integer documentModule;

	private Integer discussionModule;

	private Integer formModule;

	private Integer processModule;

	private Integer calendarModule;

	private Integer scheduleModule;

	private Integer workflowModule;

	private Integer newsModule;

	private Integer reportModule;
	
	private String userCurrentSpaceId;
	
	private String scheduling;
	
	private String userId;
	
	private String toolbar;
	
	private Integer moduleId;
	
	@InjectPage
	private ViewBlog blog;
	
	@SetupRender
	void setValues() {
		if (net.project.security.SessionManager.getUser() == null) {
			throw new IllegalStateException("User is null");
		}
		try {
			log = Logger.getLogger(ProjectMenu.class);
			JSPRootURL = SessionManager.getJSPRootURL();
			if(blog != null){
				isBlogPage = true;
				moduleId = blog.getModuleId();
			} else {
				moduleId = Module.PROJECT_SPACE;
			}
			// conditions for showing links on menu
			contactIsenabled = getBooleanValue(PropertyProvider.get("prm.project.contact.isenabled"));
			directoryIsenabled = getBooleanValue(PropertyProvider.get("prm.project.directory.isenabled"));
			blogIsenabled = getBooleanValue(PropertyProvider.get("prm.blog.isenabled"));
			wikiIsenabled = getBooleanValue(PropertyProvider.get("prm.project.wiki.isenabled"));
			documentIsenabled = getBooleanValue(PropertyProvider.get("prm.project.document.isenabled"));
			discussionIsenabled = getBooleanValue(PropertyProvider.get("prm.project.discussion.isenabled"));
			processIsenabled = getBooleanValue(PropertyProvider.get("prm.project.process.isenabled"));
			schedulingIsenabled = getBooleanValue(PropertyProvider.get("prm.project.scheduling.isenabled"));
			workflowIsenabled = getBooleanValue(PropertyProvider.get("prm.project.workflow.isenabled"));
			newsIsenabled = getBooleanValue(PropertyProvider.get("prm.project.news.isenabled"));
			subprojectIsenabled = getBooleanValue(PropertyProvider.get("prm.project.subproject.isenabled"));
			reportsIsenabled = getBooleanValue(PropertyProvider.get("prm.project.reports.isenabled"));
			setupIsenabled = getBooleanValue(PropertyProvider.get("prm.project.setup.isenabled"));
			// link names for project menu
			scheduling = PropertyProvider.get("prm.project.nav.scheduling");
			// modules in project space
			projectModule = Module.PROJECT_SPACE;
			directoryModule = Module.DIRECTORY;
			documentModule = Module.DOCUMENT;
			discussionModule = Module.DISCUSSION;
			formModule = Module.FORM;
			processModule = Module.PROCESS;
			calendarModule = Module.CALENDAR;
			scheduleModule = Module.SCHEDULE;
			workflowModule = Module.WORKFLOW;
			newsModule = Module.NEWS;
			reportModule = Module.REPORT;
			// user related information
			userCurrentSpaceId = SessionManager.getUser().getCurrentSpace().getID();			
			userId = SessionManager.getUser().getID();
		} catch (Exception e) {
			log.error("Error occured while getting property tokens for project menu : " + e.getMessage());
		}
	}
	
	boolean getBooleanValue(String stringValue){
		return stringValue.equals("1") || stringValue.equals("true") ? true : false;
	}	

	/**
	 * @return the toolbar
	 */
	public String getToolbar() {
		return toolbar;
	}

	/**
	 * @return the contactIsenabled
	 */
	public boolean getContactIsenabled() {
		return contactIsenabled;
	}

	/**
	 * @return the discussionIsenabled
	 */
	public boolean getDiscussionIsenabled() {
		return discussionIsenabled;
	}

	/**
	 * @return the documentIsenabled
	 */
	public boolean getDocumentIsenabled() {
		return documentIsenabled;
	}

	/**
	 * @return the newsIsenabled
	 */
	public boolean getNewsIsenabled() {
		return newsIsenabled;
	}

	/**
	 * @return the processIsenabled
	 */
	public boolean getProcessIsenabled() {
		return processIsenabled;
	}

	/**
	 * @return the reportsIsenabled
	 */
	public boolean getReportsIsenabled() {
		return reportsIsenabled;
	}

	/**
	 * @return the schedulingIsenabled
	 */
	public boolean getSchedulingIsenabled() {
		return schedulingIsenabled;
	}

	/**
	 * @return the setupIsenabled
	 */
	public boolean getSetupIsenabled() {
		return setupIsenabled;
	}

	/**
	 * @return the subprojectIsenabled
	 */
	public boolean getSubprojectIsenabled() {
		return subprojectIsenabled;
	}

	/**
	 * @return the wikiIsenabled
	 */
	public boolean getWikiIsenabled() {
		return wikiIsenabled;
	}

	/**
	 * @return the workflowIsenabled
	 */
	public boolean getWorkflowIsenabled() {
		return workflowIsenabled;
	}

	/**
	 * @return the calendarModule
	 */
	public Integer getCalendarModule() {
		return calendarModule;
	}

	/**
	 * @return the directoryModule
	 */
	public Integer getDirectoryModule() {
		return directoryModule;
	}

	/**
	 * @return the discussionModule
	 */
	public Integer getDiscussionModule() {
		return discussionModule;
	}

	/**
	 * @return the documentModule
	 */
	public Integer getDocumentModule() {
		return documentModule;
	}

	/**
	 * @return the formModule
	 */
	public Integer getFormModule() {
		return formModule;
	}

	/**
	 * @return the newsModule
	 */
	public Integer getNewsModule() {
		return newsModule;
	}

	/**
	 * @return the processModule
	 */
	public Integer getProcessModule() {
		return processModule;
	}

	/**
	 * @return the projectModule
	 */
	public Integer getProjectModule() {
		return projectModule;
	}

	/**
	 * @return the reportModule
	 */
	public Integer getReportModule() {
		return reportModule;
	}

	/**
	 * @return the scheduleModule
	 */
	public Integer getScheduleModule() {
		return scheduleModule;
	}

	/**
	 * @return the workflowModule
	 */
	public Integer getWorkflowModule() {
		return workflowModule;
	}

	/**
	 * @return the userCurrentSpaceId
	 */
	public String getUserCurrentSpaceId() {
		return userCurrentSpaceId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @return the jSPRootURL
	 */
	public String getJSPRootURL() {
		return JSPRootURL;
	}

	/**
	 * @return the blogIsenabled
	 */
	public boolean getBlogIsenabled() {
		return blogIsenabled;
	}

	/**
	 * @return the directoryIsenabled
	 */
	public boolean getDirectoryIsenabled() {
		return directoryIsenabled;
	}

	/**
	 * @return the moduleId
	 */
	public Integer getModuleId() {
		return moduleId;
	}

	/**
	 * @return the isBlogPage
	 */
	public boolean getIsBlogPage() {
		return isBlogPage;
	}

	public String getScheduling() {
		return scheduling;
	}
		
}
