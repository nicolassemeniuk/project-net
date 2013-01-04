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
import net.project.schedule.Schedule;
import net.project.schedule.importer.IScheduleImporter;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;

public class ChooseItemsToImportHandler extends Handler {

	public ChooseItemsToImportHandler(HttpServletRequest request) {
		super(request);
	}

	public String getViewName() {
		return "/schedule/importer/ChooseItemsToImport.jsp";
	}

	public void validateSecurity(int module, int action, String objectID, HttpServletRequest request)
			throws AuthorizationFailedException, PnetException {
		AccessVerifier.verifyAccess(Module.SCHEDULE, Action.MODIFY, objectID);
	}

	/**
	 * Prepares for ChooseItemsToImport page.
	 * <p>
	 * Expects to find <code>scheduleImporter</code> and <code>schedule</code>
	 * objects in session. Updates the scheduleImporter with the schedule.
	 * </p>
	 * 
	 * @param request
	 * @param response
	 * @return a map containing items to be placed in the request
	 * @throws Exception
	 */
	public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map model = new HashMap();

		IScheduleImporter scheduleImporter = (IScheduleImporter) request.getSession().getAttribute("scheduleImporter");
		if (scheduleImporter == null) {
			throw new ControllerException("Missing session attribute 'scheduleImporter'");
		}

		Schedule schedule = (Schedule) request.getSession().getAttribute("schedule");
		if (schedule == null) {
			throw new ControllerException("Missing session attribute 'schedule");
		}

		scheduleImporter.setSchedule(schedule);

		// Decide whether or not the default is to import the start and end date
		// of the schedule
		boolean importStartAndEnd = schedule.getScheduleStartDate() == null || schedule.getTaskList().size() == 0;
		model.put("importStartAndEnd", Boolean.valueOf(importStartAndEnd));

		return model;
	}
}
