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

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.security.Action;
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

public class DashboardLayout {

	private static Logger log = Logger.getLogger(DashboardLayout.class);

	@ApplicationState
	private String jSPRootURL;

	@Persist
	private String versionNumber;

	@Persist
	private String application_title;

	@Inject
	private RequestGlobals globals;

	@Persist
	private int projectSpace;

	@Persist
	private int modifyAction;

	@Persist
	private int viewAction;

	@Persist
	private String spaceType;

	private String userId;
	
	private String currentSpaceId;
	
	@SetupRender
	void setValues() {
		jSPRootURL = SessionManager.getJSPRootURL();
		versionNumber = StringUtils.deleteWhitespace(Version.getInstance()
				.getAppVersion());
		HttpServletRequest request = globals.getHTTPServletRequest();

		String module = request.getParameter("module");
		userId = SessionManager.getUser().getID();
		currentSpaceId = SessionManager.getUser().getCurrentSpace().getID(); 
		// spaceType = SessionManager.getUser().getCurrentSpace().getType();
		if (module != null && !module.equalsIgnoreCase("null")) {
			if (module.equals("" + Module.PERSONAL_SPACE)) {
				spaceType = Space.PERSONAL_SPACE;
			} else if (module.equals("" + Module.PROJECT_SPACE)) {
				spaceType = Space.PROJECT_SPACE;
			}
		}

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

	public String getSpaceType() {
		return spaceType;
	}

	public String getActualDate() {
		Date actual = new Date();
		return SessionManager.getUser().getDateFormatter().formatDate(actual);
	}

	public String getWeekLaterDate() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 7);
		return SessionManager.getUser().getDateFormatter().formatDate(c.getTime());
	}
	
    /**
     * @return End date after three months from current date used for teamate tasks report
     */
    public String getAssignmentReportEndDate() {
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 3);
        return SessionManager.getUser().getDateFormatter().formatDate(endDate.getTime());
    }
    
	public String getSpaceId() {
		return SessionManager.getUser().getCurrentSpace().getID();
	}

	public int getProcessModule() {
		return Module.PROCESS;
	}

	public int getActionView() {
		return Action.VIEW;
	}

	public long getStartOfMonth() {
		PnCalendar calendar = new PnCalendar();
		return calendar.startOfMonth(new Date()).getTime();
	}

	public String getViewPhaseParams() {
		return "&module=" + Module.PROCESS + "&action=" + Action.VIEW;
	}

	public String getShowResourceAllocationParams() {
		return "&module=140&startDate=" + this.getStartOfMonth();
	}

	public int getProjectSpace() {
		return Module.PROJECT_SPACE;
	}

	public int getModifyAction() {
		return Action.MODIFY;
	}

	public int getViewAction() {
		return Action.VIEW;
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
