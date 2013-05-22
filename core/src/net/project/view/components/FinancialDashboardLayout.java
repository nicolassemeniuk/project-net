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

import net.project.base.property.PropertyProvider;
import net.project.security.SessionManager;
import net.project.util.Version;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

public class FinancialDashboardLayout {

	private static Logger log = Logger.getLogger(FinancialDashboardLayout.class);

	@ApplicationState
	private String jSPRootURL;

	@Persist
	private String versionNumber;

	@Persist
	private String application_title;

	@Inject
	private RequestGlobals globals;

	private String userId;
	
	private String currentSpaceId;
	
	@SetupRender
	void setValues() {
		jSPRootURL = SessionManager.getJSPRootURL();
		versionNumber = StringUtils.deleteWhitespace(Version.getInstance()
				.getAppVersion());
		HttpServletRequest request = globals.getHTTPServletRequest();

		userId = SessionManager.getUser().getID();
		currentSpaceId = SessionManager.getUser().getCurrentSpace().getID(); 

		log.info("REQUEST: " + request);
	}

	/**
	 * @return Returns the application_title.
	 */
	public String getApplication_title() {
		return PropertyProvider.get("prm.global.application.title");
	}

	/**
	 * @param application_title
	 *            The application_title to set.
	 */
	public void setApplication_title(String application_title) {
		this.application_title = application_title;
	}

	/**
	 * @return the versionNumber
	 */
	public String getVersionNumber() {
		return versionNumber;
	}

	public String getJSPRootURL() {
		return jSPRootURL;
	}

	/**
	 * @return the currentSpaceId
	 */
	public String getCurrentSpaceId() {
		return currentSpaceId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
}
