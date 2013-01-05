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
package net.project.schedule.importer.mvc;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.ControllerException;
import net.project.base.mvc.Handler;
import net.project.resource.Roster;
import net.project.resource.RosterBean;
import net.project.schedule.importer.XMLImporter;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.User;
import net.project.util.Validator;

public class MapResourcesHandler extends Handler {

	public MapResourcesHandler(HttpServletRequest request) {
		super(request);
	}

	public String getViewName() {
		return "/schedule/importer/MapResources.jsp";
	}

	public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {
		AccessVerifier.verifyAccess(Module.SCHEDULE, Action.MODIFY, objectID);
	}

	/**
	 * Loads the scheduleImporter in the session.
	 * 
	 * @param request
	 * @param response
	 * @return a map containing the following keys and values: <br>
	 *         <ul>
	 *         <li><code>personOptionList</code> - A <code>String</code>
	 *         containing an HTML option list of persons in the current space
	 *         roster
	 *         <li><code>resources</code> - A <code>Collection</code> where
	 *         each element is an <code>Resource</code> of the resources in
	 *         the XML file
	 *         </ul>
	 * @throws Exception
	 */
	public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map model = new HashMap();

		// First set attributes in the schedule importer
		XMLImporter scheduleImporter = (XMLImporter) request.getSession().getAttribute("scheduleImporter");
		if (scheduleImporter == null) {
			throw new ControllerException("Missing session attribute 'scheduleImporter'");
		}

		String MSProjectID = request.getParameter("MSProjectID");
		String isImportTasks = request.getParameter("importTasks");
		String isImportAssignments = request.getParameter("importAssignments");
		String isImportProjStartAndEnd = request.getParameter("importStartAndEndDates");
		String isImportWorkingTimeCalendars = request.getParameter("importWorkingTimeCalendars");
		String isImportResourceWorkingTimeCalendars = request.getParameter("importResourceWorkingTimeCalendars");
		String isImportStartAndEndDates = request.getParameter("importStartAndEndDates");
		String defaultWorkingTimeCalendar = request.getParameter("defaultWorkingTimeCalendar");

		if (Validator.isBlankOrNull(MSProjectID)) {
			throw new ControllerException("Missing request attribute 'MSProjectID'");
		}

		scheduleImporter.setMSProjectID(Integer.valueOf(MSProjectID).intValue());

		if (!Validator.isBlankOrNull(isImportTasks)) {
			scheduleImporter.setImportTasks(Boolean.valueOf(isImportTasks).booleanValue());
		}
		if (!Validator.isBlankOrNull(isImportAssignments)) {
			scheduleImporter.setImportAssignments(Boolean.valueOf(isImportAssignments).booleanValue());
		}
		if (!Validator.isBlankOrNull(isImportProjStartAndEnd)) {
			scheduleImporter.setImportStartAndEndDates(Boolean.valueOf(isImportProjStartAndEnd).booleanValue());
		}
		if (!Validator.isBlankOrNull(isImportWorkingTimeCalendars)) {
			scheduleImporter.setImportWorkingTimeCalendars(Boolean.valueOf(isImportWorkingTimeCalendars).booleanValue());
		}
		if (!Validator.isBlankOrNull(isImportResourceWorkingTimeCalendars)) {
            scheduleImporter.setImportResourceWorkingTimeCalendars(Boolean.valueOf(isImportResourceWorkingTimeCalendars).booleanValue());
        }
		if (!Validator.isBlankOrNull(defaultWorkingTimeCalendar)) {
			if (defaultWorkingTimeCalendar.equals("update")) {
				scheduleImporter.setUpdateDefaultCalendar(true);
			} else if (defaultWorkingTimeCalendar.equals("add")) {
				scheduleImporter.setUpdateDefaultCalendar(false);
			} else {
				throw new ControllerException("Unexpected value for parameter defaultWorkingTimeCalendar: " + defaultWorkingTimeCalendar);
			}
		}
		if (!Validator.isBlankOrNull(isImportStartAndEndDates)) {
			scheduleImporter.setImportStartAndFinishDates(Boolean.valueOf(isImportStartAndEndDates).booleanValue());
		}

		User user = (User) request.getSession().getAttribute("user");
		scheduleImporter.setCurrentSpace(user.getCurrentSpace());
		scheduleImporter.load();

		Roster roster = RosterBean.getFromSession(request.getSession());
		roster.setSpace(user.getCurrentSpace());
		roster.load();
		scheduleImporter.setRoster(roster);
		request.getSession().setAttribute("roster", roster);

		// Construct an option list of the people in the roster
		// Also place a collection of available resources in the request
		String personOptionList = roster.getSelectionList("");

		model.put("personOptionList", personOptionList);
		model.put("resources", scheduleImporter.getResources());

		return model;
	}

}
