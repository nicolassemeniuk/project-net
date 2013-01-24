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

import net.project.base.property.PropertyProvider;
import net.project.security.SessionManager;
import net.project.util.Version;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

/**
 * @author
 * 
 */
public class Layout {
	
	private static Logger log = Logger.getLogger(Layout.class);

	@ApplicationState
	private String jSPRootURL;
	
	@Persist 
	private String versionNumber; 

	@Persist
	private String application_title;

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
	private String dataNotFoundalertMessage;
	
	@Persist
	private String errotAlertTitle;
	
	private String userId;
	
	private String currentSpaceId;
	
	@Property
	@Parameter(required = false, defaultPrefix = BindingConstants.LITERAL)
	private String callOnLoad;
	
	@SetupRender
	void setValues() {
		jSPRootURL = SessionManager.getJSPRootURL();
		versionNumber = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
		userId = SessionManager.getUser().getID();
		currentSpaceId = SessionManager.getUser().getCurrentSpace().getID(); 
	}

	public void onActivate() {
		try {
			application_title = PropertyProvider
					.get("prm.global.application.title");
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
			dataNotFoundalertMessage = PropertyProvider
					.get("prm.resource.datanotavailableforselectedperiod.message");
			errotAlertTitle = PropertyProvider
					.get("prm.resource.global.exterroralert.title");
		} catch (Exception ex) {
			log
					.error("Error occured while getting property values in Layout page : "
							+ ex.getMessage());
		}
	}

	/**
	 * @return Returns the application_title.
	 */
	public String getApplication_title() {
		return application_title;
	}

	/**
	 * @param application_title
	 *            The application_title to set.
	 */
	public void setApplication_title(String application_title) {
		this.application_title = application_title;
	}

	/**
	 * @return Returns the personal_off.
	 */
	public String getPersonal_off() {
		return personal_off;
	}

	/**
	 * @param personal_off
	 *            The personal_off to set.
	 */
	public void setPersonal_off(String personal_off) {
		this.personal_off = personal_off;
	}

	/**
	 * @return Returns the personal_on.
	 */
	public String getPersonal_on() {
		return personal_on;
	}

	/**
	 * @param personal_on
	 *            The personal_on to set.
	 */
	public void setPersonal_on(String personal_on) {
		this.personal_on = personal_on;
	}

	/**
	 * @return Returns the business_off.
	 */
	public String getBusiness_off() {
		return business_off;
	}

	/**
	 * @param business_off
	 *            The business_off to set.
	 */
	public void setBusiness_off(String business_off) {
		this.business_off = business_off;
	}

	/**
	 * @return Returns the business_on.
	 */
	public String getBusiness_on() {
		return business_on;
	}

	/**
	 * @param business_on
	 *            The business_on to set.
	 */
	public void setBusiness_on(String business_on) {
		this.business_on = business_on;
	}

	/**
	 * @return Returns the project_off.
	 */
	public String getProject_off() {
		return project_off;
	}

	/**
	 * @param project_off
	 *            The project_off to set.
	 */
	public void setProject_off(String project_off) {
		this.project_off = project_off;
	}

	/**
	 * @return Returns the project_on.
	 */
	public String getProject_on() {
		return project_on;
	}

	/**
	 * @param project_on
	 *            The project_on to set.
	 */
	public void setProject_on(String project_on) {
		this.project_on = project_on;
	}

	/**
	 * @return Returns the resources_off.
	 */
	public String getResources_off() {
		return resources_off;
	}

	/**
	 * @param resources_off
	 *            The resources_off to set.
	 */
	public void setResources_off(String resources_off) {
		this.resources_off = resources_off;
	}

	/**
	 * @return Returns the resources_on.
	 */
	public String getResources_on() {
		return resources_on;
	}

	/**
	 * @param resources_on
	 *            The resources_on to set.
	 */
	public void setResources_on(String resources_on) {
		this.resources_on = resources_on;
	}

	/**
	 * @return the jSPRootURL
	 */
	public String getJSPRootURL() {
		return jSPRootURL;
	}

	/**
	 * @return the dataNotFoundalertMessage
	 */
	public String getDataNotFoundalertMessage() {
		return dataNotFoundalertMessage;
	}

	/**
	 * @return the errotAlertTitle
	 */
	public String getErrotAlertTitle() {
		return errotAlertTitle;
	}

	/**
	 * @return the versionNumber
	 */
	public String getVersionNumber() {
		return versionNumber;
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
