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

import net.project.admin.ApplicationSpace;
import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.configuration.ConfigurationPortfolio;
import net.project.license.system.LicenseProperties;
import net.project.license.system.MasterProperties;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.view.pages.blog.ViewBlog;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.SetupRender;

/**
 * @author
 *
 */
public class PersonalMenu {
	
	private static Logger log;
	
	@ApplicationState
	private String JSPRootURL;

	private boolean dashboardIsenabled;
	
	private boolean calendarIsenabled;
		 
	private boolean assignmentsIsenabled;
	
	private boolean blogIsenabled;
	
	private boolean documentIsenabled;
	
	private boolean formIsenabled;

	private boolean methodologyIsenabled;

	private boolean setupIsenabled;
	
	private boolean masterPropertiesExist;

	private boolean isLicenseRequiredAtLogin;
	
	private boolean displayApplicationSpace;

	private boolean displayConfigurationSpace;
	
	private boolean isBlogPage;
	
	private String template;
	
	private String licensing;
	
	private String applicationspace;
	
	private String configurations;

	private Integer personalModule;

	private Integer documentModule;

	private Integer formModule;

	private Integer calendarModule;

	private Integer applicationSpaceModule;

	private Integer configurationSpaceModule;
	
	private User user;

	private String userCurrentSpaceId;
	
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
				moduleId = Module.PERSONAL_SPACE;
			}
			user = SessionManager.getUser();
			// conditions for showing links on menu
			dashboardIsenabled = getBooleanValue(PropertyProvider.get("prm.personal.dashboard.isenabled"));
			calendarIsenabled = getBooleanValue(PropertyProvider.get("prm.personal.calendar.isenabled"));
			assignmentsIsenabled = getBooleanValue(PropertyProvider.get("prm.personal.assignments.isenabled"));
			blogIsenabled = getBooleanValue(PropertyProvider.get("prm.blog.isenabled"));
			documentIsenabled = getBooleanValue(PropertyProvider.get("prm.personal.document.isenabled"));
			formIsenabled = getBooleanValue(PropertyProvider.get("prm.personal.form.isenabled"));
			methodologyIsenabled = getBooleanValue(PropertyProvider.get("prm.personal.methodology.isenabled"));
			setupIsenabled = getBooleanValue(PropertyProvider.get("prm.personal.setup.isenabled"));
						
			// Determine whether or not the application space will be displayed.
			displayApplicationSpace = ApplicationSpace.DEFAULT_APPLICATION_SPACE.isUserSpaceMember(user);
			
			// Determine whether or not the configuration space will be displayed.
		    //(The user has to have at least one configuration)
			ConfigurationPortfolio configurationPortfolio =  new ConfigurationPortfolio(); 
		    configurationPortfolio.setID(user.getMembershipPortfolioID());
		    displayConfigurationSpace = configurationPortfolio.hasConfiguration();	
		    
		    template = PropertyProvider.get("prm.personal.nav.template");
		    licensing = PropertyProvider.get("prm.personal.nav.licensing");
		    applicationspace = PropertyProvider.get("prm.personal.nav.applicationspace");					
		   			
			personalModule = Module.PERSONAL_SPACE;
			documentModule = Module.DOCUMENT;
			formModule = Module.FORM;
			calendarModule = Module.CALENDAR;
			applicationSpaceModule = Module.APPLICATION_SPACE;
			configurationSpaceModule = Module.CONFIGURATION_SPACE;
			userCurrentSpaceId = user.getCurrentSpace().getID();			
			userId = user.getID();
			
			masterPropertiesExist = MasterProperties.masterPropertiesExist();
			isLicenseRequiredAtLogin = LicenseProperties.getInstance().isLicenseRequiredAtLogin();
		} catch (Exception e) {
			log.error("Error occured while getting property tokens for project menu : " + e.getMessage());
		}		
	}		
	
	boolean getBooleanValue(String stringValue){
		return stringValue.equals("1") || stringValue.equals("true") ? true : false;
	}

	/**
	 * @return the applicationspace
	 */
	public String getApplicationspace() {
		return applicationspace;
	}

	/**
	 * @return the applicationSpaceModule
	 */
	public Integer getApplicationSpaceModule() {
		return applicationSpaceModule;
	}

	/**
	 * @return the assignmentsIsenabled
	 */
	public boolean getAssignmentsIsenabled() {
		return assignmentsIsenabled;
	}

	/**
	 * @return the blogIsenabled
	 */
	public boolean getBlogIsenabled() {
		return blogIsenabled;
	}

	/**
	 * @return the calendarIsenabled
	 */
	public boolean getCalendarIsenabled() {
		return calendarIsenabled;
	}

	/**
	 * @return the calendarModule
	 */
	public Integer getCalendarModule() {
		return calendarModule;
	}

	/**
	 * @return the configurations
	 */
	public String getConfigurations() {
		return configurations;
	}

	/**
	 * @return the configurationSpaceModule
	 */
	public Integer getConfigurationSpaceModule() {
		return configurationSpaceModule;
	}

	/**
	 * @return the dashboardIsenabled
	 */
	public boolean getDashboardIsenabled() {
		return dashboardIsenabled;
	}

	/**
	 * @return the displayApplicationSpace
	 */
	public boolean getDisplayApplicationSpace() {
		return displayApplicationSpace;
	}

	/**
	 * @return the displayConfigurationSpace
	 */
	public boolean getDisplayConfigurationSpace() {
		return displayConfigurationSpace;
	}

	/**
	 * @return the documentIsenabled
	 */
	public boolean getDocumentIsenabled() {
		return documentIsenabled;
	}

	/**
	 * @return the documentModule
	 */
	public Integer getDocumentModule() {
		return documentModule;
	}

	/**
	 * @return the formIsenabled
	 */
	public boolean getFormIsenabled() {
		return formIsenabled;
	}

	/**
	 * @return the formModule
	 */
	public Integer getFormModule() {
		return formModule;
	}

	/**
	 * @return the isLicenseRequiredAtLogin
	 */
	public boolean getIsLicenseRequiredAtLogin() {
		return isLicenseRequiredAtLogin;
	}

	/**
	 * @return the jSPRootURL
	 */
	public String getJSPRootURL() {
		return JSPRootURL;
	}

	/**
	 * @return the licensing
	 */
	public String getLicensing() {
		return licensing;
	}

	/**
	 * @return the masterPropertiesExist
	 */
	public boolean getMasterPropertiesExist() {
		return masterPropertiesExist;
	}

	/**
	 * @return the methodologyIsenabled
	 */
	public boolean getMethodologyIsenabled() {
		return methodologyIsenabled;
	}

	/**
	 * @return the personalModule
	 */
	public Integer getPersonalModule() {
		return personalModule;
	}

	/**
	 * @return the setupIsenabled
	 */
	public boolean getSetupIsenabled() {
		return setupIsenabled;
	}

	/**
	 * @return the template
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * @return the toolbar
	 */
	public String getToolbar() {
		return toolbar;
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
		
}
