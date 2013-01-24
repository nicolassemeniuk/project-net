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

import javax.servlet.http.HttpServletRequest;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.enterprise.EnterpriseSpace;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;
import net.project.util.Version;
import net.project.view.pages.resource.management.Dashboard;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

public class SpaceMainMenu {

	private static Logger log = Logger.getLogger(SpaceMainMenu.class);

	private String jSPRootURL;
	
	private Integer module;
	
	@InjectPage
	private Dashboard dashboard;

	@Persist
	private static String personalURL;

	@Persist
	private static String businessURL;

	@Persist
	private static String projectsURL;

	@Persist
	private static String enterpriseURL;

	@Persist
	private static String resourcesURL;

	@Persist
	private String username;

	@Persist
	private String personal_off;

	@Persist
	private String business_off;

	@Persist
	private String project_off;

	@Persist
	private String resources_off;

	@Persist
	private String personal_height;

	@Persist
	private String business_height;

	@Persist
	private String projeect_height;

	@Persist
	private String resources_height;

	@Persist
	private String personal_width;

	@Persist
	private String business_width;

	@Persist
	private String project_width;

	@Persist
	private String resources_width;
	
	@Persist
	private boolean personalSpaceIsEnabled;
		
	@Persist
	private boolean bussinessSpaceIsEnabled;
	
	@Persist
	private boolean projectSpaceIsEnabled;
	
	@Persist
	private boolean enterpriseSpaceIsEnabled;
	
	@Persist
	private boolean resourceSpaceIsEnabled;
	
	@Inject
	private RequestGlobals requestGlobals;

	private boolean isUserMemberOfEnterpriseSpace;

	private boolean headerLeftLinkBarIsEnabled;

	private boolean link1Isenabled;

	private String link1Href;

	private boolean link2Isenabled;

	private String link2Href;

	private boolean link3Isenabled;

	private String link3Href;

	private boolean link4Isenabled;

	private String link4Href;

	private boolean showappversion;

	private String productVersionCodename;

	private boolean homeIsenabled;

	private String homeHref;

	private String headerLinksCss;

	private boolean supportIsenabled;

	private String headerSupportHref;
	
	private String headerSupportCss;

	private boolean logoutIsenabled;

	private String headerLogoutHref;

	private boolean helpIsenabled;

	private String headerHelpHref;

	public SpaceMainMenu() {
	}

	@SetupRender
	void initializeValues() {
		if (net.project.security.SessionManager.getUser() == null) {
			throw new IllegalStateException("User is null");
		}
		try {
			User user =SessionManager.getUser();
			username = user.getDisplayName();
			jSPRootURL = SessionManager.getJSPRootURL();

			String spaceType = user.getCurrentSpace().getSpaceType().getName().toLowerCase();
			module = Module.getModuleForSpaceType(spaceType.equalsIgnoreCase("personal") ? Space.PERSONAL_SPACE : spaceType);
			
			personal_off = PropertyProvider.get("prm.global.header.personal.image.off");
			business_off = PropertyProvider.get("prm.global.header.business.image.off");
			project_off = PropertyProvider.get("prm.global.header.project.image.off");
			resources_off = PropertyProvider.get("prm.global.header.resource.image.off");
			personal_height = PropertyProvider.get("prm.global.header.personal.image.height");
			personal_width = PropertyProvider.get("prm.global.header.personal.image.width");
			business_height = PropertyProvider.get("prm.global.header.business.image.height");
			business_width = PropertyProvider.get("prm.global.header.business.image.width");
			projeect_height = PropertyProvider.get("prm.global.header.project.image.height");
			project_width = PropertyProvider.get("prm.global.header.project.image.width");
			resources_height = PropertyProvider.get("prm.global.header.resource.image.height");
			resources_width = PropertyProvider.get("prm.global.header.resource.image.width");
			
			// space related tokens
			personalSpaceIsEnabled = getBooleanValue(PropertyProvider.get("prm.personal.isenabled"));
			bussinessSpaceIsEnabled = getBooleanValue(PropertyProvider.get("prm.business.isenabled"));
			projectSpaceIsEnabled = getBooleanValue(PropertyProvider.get("prm.project.isenabled"));
			enterpriseSpaceIsEnabled = getBooleanValue(PropertyProvider.get("prm.enterprise.isenabled"));
			resourceSpaceIsEnabled = getBooleanValue(PropertyProvider.get("prm.resource.isenabled"));
			
			HttpServletRequest request = requestGlobals.getHTTPServletRequest();
			boolean isBusinessMenuSelected = false;
			if ( request.getRequestURI().indexOf("BusinessPortfolio.jsp") >=0 )
				isBusinessMenuSelected = true;
			boolean isProjectMenuSelected = false;
			if ( request.getRequestURI().indexOf("PersonalPortfolio.jsp") >=0 )
				isProjectMenuSelected = true;
			
			if (request.getRequestURI().indexOf("resource") < 0 && user.getCurrentSpace().isTypeOf(net.project.space.Space.PERSONAL_SPACE) && !isBusinessMenuSelected && !isProjectMenuSelected) {
				personalURL = "<a href=\""+SessionManager.getJSPRootURL()+"/personal/Main.jsp\" class=\"menu-one-selected\">Personal</a>";
			} else { 
				personalURL = "<a href=\""+SessionManager.getJSPRootURL()+"/personal/Main.jsp\" class=\"menu-personal\">Personal</a>";
		    }
			
			if (user.getCurrentSpace().isTypeOf(net.project.space.Space.BUSINESS_SPACE) && !isProjectMenuSelected || isBusinessMenuSelected ) { 
				businessURL = "<a href=\""+SessionManager.getJSPRootURL()+"/business/BusinessPortfolio.jsp?module="+Module.PERSONAL_SPACE+"&portfolio=true\" class=\"menu-one-selected\">Business</a>";
			} else {  
				businessURL = "<a href=\""+SessionManager.getJSPRootURL()+"/business/BusinessPortfolio.jsp?module="+Module.PERSONAL_SPACE+"&portfolio=true\" class=\"menu-business\">Business</a>";
			}

			if (user.getCurrentSpace().isTypeOf(net.project.space.Space.PROJECT_SPACE) && !isBusinessMenuSelected || isProjectMenuSelected) { 
				projectsURL = "<a href=\""+SessionManager.getJSPRootURL()+"/portfolio/PersonalPortfolio.jsp?module="+Module.PERSONAL_SPACE+"&portfolio=true\" class=\"menu-one-selected\">Projects</a>";
			} else {  
				projectsURL = "<a href=\""+SessionManager.getJSPRootURL()+"/portfolio/PersonalPortfolio.jsp?module="+Module.PERSONAL_SPACE+"&portfolio=true\" class=\"menu-projects\">Projects</a>";
			}
			
			isUserMemberOfEnterpriseSpace = new EnterpriseSpace().isUserSpaceMember(SessionManager.getUser()); 
			
			if (user.getCurrentSpace().isTypeOf(net.project.space.Space.ENTERPRISE_SPACE) ) { 
				enterpriseURL = "<a href=\""+SessionManager.getJSPRootURL()+"/enterprise/Main.jsp?module="+Module.ENTERPRISE_SPACE+"\" class=\"menu-one-selected\">Enterprise</a>";
			} else {  
				enterpriseURL = "<a href=\""+SessionManager.getJSPRootURL()+"/enterprise/Main.jsp?module="+Module.ENTERPRISE_SPACE+"\" class=\"menu-projects\">Enterprise</a>";
			}

			if (request.getRequestURI().indexOf("resource") >= 0 || user.getCurrentSpace().isTypeOf(net.project.space.Space.RESOURCES_SPACE) ) { 
				resourcesURL = "<a href=\""+SessionManager.getJSPRootURL()+"/personal/Main.jsp?page="+SessionManager.getJSPRootURL()+"/resource/management/viewsummary?module="+Module.RESOURCES_SPACE+"\" class=\"menu-one-selected\">Resource</a>";
			} else {  
				resourcesURL = "<a href=\""+SessionManager.getJSPRootURL()+"/personal/Main.jsp?page="+SessionManager.getJSPRootURL()+"/resource/management/viewsummary?module="+Module.RESOURCES_SPACE+"\" class=\"menu-projects\">Resource</a>";
			}
			
			headerLeftLinkBarIsEnabled = getBooleanValue(PropertyProvider.get("prm.global.header.leftlinkbar.isenabled"));
			link1Isenabled = getBooleanValue(PropertyProvider.get("prm.global.header.leftlinkbar.link1.isenabled"));
			link1Href = PropertyProvider.get("@prm.global.header.leftlinkbar.link1.href");
			link2Isenabled = getBooleanValue(PropertyProvider.get("prm.global.header.leftlinkbar.link2.isenabled"));
			link2Href = PropertyProvider.get("@prm.global.header.leftlinkbar.link2.href");
			link3Isenabled = getBooleanValue(PropertyProvider.get("prm.global.header.leftlinkbar.link3.isenabled"));
			link3Href = PropertyProvider.get("@prm.global.header.leftlinkbar.link3.href");
			link4Isenabled = getBooleanValue(PropertyProvider.get("prm.global.header.leftlinkbar.link4.isenabled"));
			link4Href = PropertyProvider.get("@prm.global.header.leftlinkbar.link4.href");
			showappversion = getBooleanValue(PropertyProvider.get("prm.global.application.debug.showappversion"));
			productVersionCodename = Version.getInstance().getProductVersionCodename();
			homeIsenabled = getBooleanValue(PropertyProvider.get("prm.global.header.home.isenabled"));
			homeHref = PropertyProvider.get("@prm.global.header.home.href");
			headerLinksCss = PropertyProvider.get("@prm.global.header.links.css");
			supportIsenabled = getBooleanValue(PropertyProvider.get("prm.global.header.support.isenabled"));
			headerSupportHref = PropertyProvider.get("@prm.global.header.support.href");
			headerSupportCss = PropertyProvider.get("@prm.global.header.links.css");
			logoutIsenabled = getBooleanValue(PropertyProvider.get("prm.global.header.logout.isenabled"));
			headerLogoutHref = PropertyProvider.get("@prm.global.header.logout.href");
			helpIsenabled = getBooleanValue(PropertyProvider.get("prm.global.header.help.isenabled"));
			headerHelpHref = PropertyProvider.get("@prm.global.header.help.href");
		} catch (Exception ex) {
			log.error("Error occured while getting property values in SpaceMainMenu page : " + ex.getMessage());
		}
	}
	
	boolean getBooleanValue(String stringValue){
		return stringValue.equals("1") ? true : false;
	}

	public Object onActionFromresources_link() {
		dashboard.setMessage("Resources Tab clicked");
		return dashboard;
	}

	@CleanupRender
	void cleanValues() {
		username = null;
		personalURL = null;
		businessURL = null;
		projectsURL = null;
		enterpriseURL = null;
		resourcesURL = null;
	}

	/**
	 * @return Returns the businessURL.
	 */
	public String getBusinessURL() {
		return businessURL;
	}

	/**
	 * @param businessURL
	 *            The businessURL to set.
	 */
	public void setBusinessURL(String businessURL) {
		this.businessURL = businessURL;
	}

	/**
	 * @return Returns the enterpriseURL.
	 */
	public String getEnterpriseURL() {
		return enterpriseURL;
	}

	/**
	 * @param enterpriseURL
	 *            The enterpriseURL to set.
	 */
	public void setEnterpriseURL(String enterpriseURL) {
		this.enterpriseURL = enterpriseURL;
	}

	/**
	 * @return Returns the personalURL.
	 */
	public String getPersonalURL() {
		return personalURL;
	}

	/**
	 * @param personalURL
	 *            The personalURL to set.
	 */
	public void setPersonalURL(String personalURL) {
		this.personalURL = personalURL;

	}

	/**
	 * @return Returns the projectsURL.
	 */
	public String getProjectsURL() {
		return projectsURL;
	}

	/**
	 * @param projectsURL
	 *            The projectsURL to set.
	 */
	public void setProjectsURL(String projectsURL) {
		this.projectsURL = projectsURL;

	}

	/**
	 * @return Returns the username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the resourcesURL
	 */
	public String getResourcesURL() {
		return resourcesURL;
	}

	/**
	 * @param resourcesURL
	 *            the resourcesURL to set
	 */
	public void setResourcesURL(String resourcesURL) {
		this.resourcesURL = resourcesURL;
	}

	/**
	 * @return the jSPRootURL
	 */
	public String getJSPRootURL() {
		return jSPRootURL;
	}

	/**
	 * @return the business_height
	 */
	public String getBusiness_height() {
		return business_height;
	}

	/**
	 * @return the business_off
	 */
	public String getBusiness_off() {
		return business_off;
	}

	/**
	 * @return the business_width
	 */
	public String getBusiness_width() {
		return business_width;
	}

	/**
	 * @return the dashboard
	 */
	public Dashboard getDashboard() {
		return dashboard;
	}

	/**
	 * @return the personal_height
	 */
	public String getPersonal_height() {
		return personal_height;
	}

	/**
	 * @return the personal_off
	 */
	public String getPersonal_off() {
		return personal_off;
	}

	/**
	 * @return the personal_width
	 */
	public String getPersonal_width() {
		return personal_width;
	}

	/**
	 * @return the project_off
	 */
	public String getProject_off() {
		return project_off;
	}

	/**
	 * @return the project_width
	 */
	public String getProject_width() {
		return project_width;
	}

	/**
	 * @return the projeect_height
	 */
	public String getProjeect_height() {
		return projeect_height;
	}

	/**
	 * @return the resources_height
	 */
	public String getResources_height() {
		return resources_height;
	}

	/**
	 * @return the resources_off
	 */
	public String getResources_off() {
		return resources_off;
	}

	/**
	 * @return the resources_width
	 */
	public String getResources_width() {
		return resources_width;
	}

	public boolean getBussinessSpaceIsEnabled() {
		return bussinessSpaceIsEnabled;
	}

	public boolean getEnterpriseSpaceIsEnabled() {
		return enterpriseSpaceIsEnabled;
	}

	public boolean getIsUserMemberOfEnterpriseSpace() {
		return isUserMemberOfEnterpriseSpace;
	}

	public boolean getPersonalSpaceIsEnabled() {
		return personalSpaceIsEnabled;
	}

	public boolean getProjectSpaceIsEnabled() {
		return projectSpaceIsEnabled;
	}

	public boolean getResourceSpaceIsEnabled() {
		return resourceSpaceIsEnabled;
	}

	/**
	 * @return the headerHelpHref
	 */
	public String getHeaderHelpHref() {
		return headerHelpHref;
	}

	/**
	 * @return the headerLeftLinkBarIsEnabled
	 */
	public boolean getHeaderLeftLinkBarIsEnabled() {
		return headerLeftLinkBarIsEnabled;
	}

	/**
	 * @return the headerLinksCss
	 */
	public String getHeaderLinksCss() {
		return headerLinksCss;
	}

	/**
	 * @return the headerLogoutHref
	 */
	public String getHeaderLogoutHref() {
		return headerLogoutHref;
	}

	/**
	 * @return the headerSupportHref
	 */
	public String getHeaderSupportHref() {
		return headerSupportHref;
	}

	/**
	 * @return the helpIsenabled
	 */
	public boolean getHelpIsenabled() {
		return helpIsenabled;
	}

	/**
	 * @return the homeHref
	 */
	public String getHomeHref() {
		return homeHref;
	}

	/**
	 * @return the homeIsenabled
	 */
	public boolean getHomeIsenabled() {
		return homeIsenabled;
	}

	/**
	 * @return the link1Href
	 */
	public String getLink1Href() {
		return link1Href;
	}

	/**
	 * @return the link1Isenabled
	 */
	public boolean getLink1Isenabled() {
		return link1Isenabled;
	}

	/**
	 * @return the link2Href
	 */
	public String getLink2Href() {
		return link2Href;
	}

	/**
	 * @return the link2Isenabled
	 */
	public boolean getLink2Isenabled() {
		return link2Isenabled;
	}

	/**
	 * @return the link3Href
	 */
	public String getLink3Href() {
		return link3Href;
	}

	/**
	 * @return the link3Isenabled
	 */
	public boolean getLink3Isenabled() {
		return link3Isenabled;
	}

	/**
	 * @return the link4Href
	 */
	public String getLink4Href() {
		return link4Href;
	}

	/**
	 * @return the link4Isenabled
	 */
	public boolean getLink4Isenabled() {
		return link4Isenabled;
	}

	/**
	 * @return the logoutIsenabled
	 */
	public boolean getLogoutIsenabled() {
		return logoutIsenabled;
	}

	/**
	 * @return the showappversion
	 */
	public boolean getShowappversion() {
		return showappversion;
	}

	/**
	 * @return the supportIsenabled
	 */
	public boolean getSupportIsenabled() {
		return supportIsenabled;
	}

	/**
	 * @return the productVersionCodename
	 */
	public String getProductVersionCodename() {
		return productVersionCodename;
	}

	/**
	 * @return the headerSupportCss
	 */
	public String getHeaderSupportCss() {
		return headerSupportCss;
	}

	/**
	 * @return the module
	 */
	public Integer getModule() {
		return module;
	}

}
