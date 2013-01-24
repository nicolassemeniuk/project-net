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
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.util.Version;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

public class AssignmentLayout {

	private static Logger log = Logger.getLogger(BlogLayout.class);

	@ApplicationState
	private String jSPRootURL;

	@Persist
	private String versionNumber;

	@Persist
	private String personal_on;

	@Persist
	private String personal_off;

	@Persist
	private String business_on;

	@Persist
	private String business_off;

	@Persist
	private String project_on;

	@Persist
	private String project_off;

	@Persist
	private String resources_on;

	@Persist
	private String resources_off;

	@Persist
	private String errotAlertTitle;

	@Persist
	private String spaceType;

	@Inject
	private RequestGlobals globals;

	@Persist
	private String module;

	@Persist
	private Integer projectSpace;

	public AssignmentLayout() {
		try {
			projectSpace = Module.PROJECT_SPACE;
			personal_on = PropertyProvider
					.get("prm.global.header.personal.image.on");
			personal_off = PropertyProvider
					.get("prm.global.header.personal.image.off");
			business_on = PropertyProvider
					.get("prm.global.header.business.image.on");
			business_off = PropertyProvider
					.get("prm.global.header.business.image.off");
			project_on = PropertyProvider
					.get("prm.global.header.project.image.on");
			project_off = PropertyProvider
					.get("prm.global.header.project.image.off");
			resources_on = PropertyProvider
					.get("prm.global.header.resource.image.on");
			resources_off = PropertyProvider
					.get("prm.global.header.resource.image.off");
			errotAlertTitle = PropertyProvider
					.get("prm.resource.global.exterroralert.title");
		} catch (Exception ex) {
			log
					.error("Error occured while getting property values in Layout page : "
							+ ex.getMessage());
		}
	}

	@SetupRender
	void setValues() {
		jSPRootURL = SessionManager.getJSPRootURL();
		versionNumber = StringUtils.deleteWhitespace(Version.getInstance()
				.getAppVersion());
		HttpServletRequest request = globals.getHTTPServletRequest();
		module = request.getParameter("module");
		// spaceType = SessionManager.getUser().getCurrentSpace().getType();
		if (module != null && !module.equalsIgnoreCase("null")) {
			if (module.equals("" + Module.PERSONAL_SPACE)) {
				spaceType = Space.PERSONAL_SPACE;
			} else if (module.equals("" + Module.PROJECT_SPACE)) {
				spaceType = Space.PROJECT_SPACE;
			}
		}
	}

	public String getJSPRootURL() {
		return jSPRootURL;
	}

	public void setJSPRootURL(String rootURL) {
		jSPRootURL = rootURL;
	}

	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	public String getPersonal_on() {
		return personal_on;
	}

	public void setPersonal_on(String personal_on) {
		this.personal_on = personal_on;
	}

	public String getPersonal_off() {
		return personal_off;
	}

	public void setPersonal_off(String personal_off) {
		this.personal_off = personal_off;
	}

	public String getBusiness_on() {
		return business_on;
	}

	public void setBusiness_on(String business_on) {
		this.business_on = business_on;
	}

	public String getBusiness_off() {
		return business_off;
	}

	public void setBusiness_off(String business_off) {
		this.business_off = business_off;
	}

	public String getProject_on() {
		return project_on;
	}

	public void setProject_on(String project_on) {
		this.project_on = project_on;
	}

	public String getProject_off() {
		return project_off;
	}

	public void setProject_off(String project_off) {
		this.project_off = project_off;
	}

	public String getResources_on() {
		return resources_on;
	}

	public void setResources_on(String resources_on) {
		this.resources_on = resources_on;
	}

	public String getResources_off() {
		return resources_off;
	}

	public void setResources_off(String resources_off) {
		this.resources_off = resources_off;
	}

	public String getErrotAlertTitle() {
		return errotAlertTitle;
	}

	public void setErrotAlertTitle(String errotAlertTitle) {
		this.errotAlertTitle = errotAlertTitle;
	}

	public String getSpaceType() {
		return spaceType;
	}

	public void setSpaceType(String spaceType) {
		this.spaceType = spaceType;
	}

	public RequestGlobals getGlobals() {
		return globals;
	}

	public void setGlobals(RequestGlobals globals) {
		this.globals = globals;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public Integer getProjectSpace() {
		return projectSpace;
	}

	public void setProjectSpace(Integer projectSpace) {
		this.projectSpace = projectSpace;
	}

}
