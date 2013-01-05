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

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.ControllerException;
import net.project.base.mvc.Handler;
import net.project.schedule.importer.ResourceResolver;
import net.project.schedule.importer.XMLImporter;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;

/**
 * Handles processing after ResolveResources view.
 */
public class ResolveResourcesProcessingHandler extends Handler {

	private String viewName;

	public ResolveResourcesProcessingHandler(HttpServletRequest request) {
		super(request);
	}

	public String getViewName() {
		return this.viewName;
	}

	public void validateSecurity(int module, int action, String objectID, HttpServletRequest request)
			throws AuthorizationFailedException, PnetException {
		AccessVerifier.verifyAccess(Module.SCHEDULE, Action.MODIFY, objectID);
	}

	/**
	 * Process the resource mappings and prepares scheduleImporter for
	 * importing.
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		XMLImporter scheduleImporter = (XMLImporter) request.getSession().getAttribute("scheduleImporter");
		if (scheduleImporter == null) {
			throw new ControllerException("Missing session attribute 'scheduleImporter'");
		}

		ResourceResolver resourceResolver = scheduleImporter.getResourceResolver();

		// Grab all request parameters called "person_XXXXX" where "XXXXX" is
		// a person id; the value of each parameter is a resource UID
		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
			String nextName = (String) e.nextElement();

			if (nextName.startsWith("person_")) {
				// We have a parameter we're interested in
				// Extract the personID, grab the mapped resourceUID
				// and pass to resource resolver
				String nextPersonID = nextName.substring("person_".length());
				String nextResourceUID = request.getParameter(nextName);

				if (!nextResourceUID.equals(ResourceResolver.IGNORE_RESOURCE_OPTION_VALUE)) {
					resourceResolver.resolveMappingToResource(Integer.valueOf(nextPersonID), Integer
							.valueOf(nextResourceUID));
				}

			}
		}

		// We're now ready to count up the resource calendars to import
		scheduleImporter.prepareImport();
		this.viewName = "/schedule/importer/ImportSummary.jsp";

		// No additional model items
		Map model = Collections.EMPTY_MAP;
		return model;
	}

}
