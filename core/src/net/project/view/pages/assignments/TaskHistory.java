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
package net.project.view.pages.assignments;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.project.resource.AssignmentWorkCaptureHelper;
import net.project.schedule.ScheduleEntry;
import net.project.security.SessionManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

/**
 *
 */
public class TaskHistory {

	private static Logger log = Logger.getLogger(TaskHistory.class);

	private String selectedTask;
    
    private String selectedTaskTooltip;

	private List<ScheduleEntry> historyEntries;

	private ScheduleEntry scheduleEntry;

	private String startDate;

	private String endDate;
	
	private String jspRootURL;
    
    private String userTimeZone;

	@Inject
	private RequestGlobals requestGlobals;

	public void onActivate() {
		if (net.project.security.SessionManager.getUser() == null) {
			throw new IllegalStateException("User is null");
		}

		HttpServletRequest request = requestGlobals.getHTTPServletRequest();
		String objectId = request.getParameter("objectId");
		String personId = SessionManager.getUser().getID();
        userTimeZone = SessionManager.getUser().getDateFormatter().formatDate(new Date(),"z");
		jspRootURL = SessionManager.getJSPRootURL();
		if (StringUtils.isNotEmpty(objectId) && StringUtils.isNotEmpty(personId)) {
			try {
				selectedTask = request.getParameter("selectedTask");
                selectedTaskTooltip = request.getParameter("selectedTaskTooltip");
				historyEntries = AssignmentWorkCaptureHelper.getHistory(objectId, personId);
			} catch (Exception e) {
				log.error("Error occurred while getting history for selected task assignment : " + e.getMessage());
			}
		}

		if (historyEntries != null && historyEntries.size() > 0) {
			startDate = ((ScheduleEntry) historyEntries.get(0)).getStartTimeString().toString();
			endDate = ((ScheduleEntry) historyEntries.get(0)).getEndTimeString().toString();
		}
	}

	/**
	 * @return the historyEntries
	 */
	public List<ScheduleEntry> getHistoryEntries() {
		return historyEntries;
	}

	/**
	 * @param historyEntries
	 *            the historyEntries to set
	 */
	public void setHistoryEntries(List<ScheduleEntry> historyEntries) {
		this.historyEntries = historyEntries;
	}

	/**
	 * @return the scheduleEntry
	 */
	public ScheduleEntry getScheduleEntry() {
		return scheduleEntry;
	}

	/**
	 * @param scheduleEntry
	 *            the scheduleEntry to set
	 */
	public void setScheduleEntry(ScheduleEntry scheduleEntry) {
		this.scheduleEntry = scheduleEntry;
	}

	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @return the selectedTask
	 */
	public String getSelectedTask() {
		return selectedTask;
	}

	/**
	 * @return the jspRootURL
	 */
	public String getJspRootURL() {
		return jspRootURL;
	}

    /**
     * @return the userTimeZone
     */
    public String getUserTimeZone() {
        return userTimeZone;
    }

    /**
     * @return the selectedTaskTooltip
     */
    public String getSelectedTaskTooltip() {
        return selectedTaskTooltip;
    }
}
