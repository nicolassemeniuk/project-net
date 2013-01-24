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

import java.util.Enumeration;
import java.util.HashMap;
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
 * Handles processing after MapResources view.
 */
public class MapResourcesProcessingHandler extends Handler {

	private String viewName;

	public MapResourcesProcessingHandler(HttpServletRequest request) {
		super(request);
	}

	public String getViewName() {
		return this.viewName;
	}

	public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {
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

		Map model = new HashMap();

		ResourceResolver resourceResolver = scheduleImporter.getResourceResolver();
        Map assignorMapper = scheduleImporter.getAssignorMapper();

		// Process the resource mappings
		final String resourceParameterNamePrefix = "resource";
		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
			String name = (String) e.nextElement();

			if (name.startsWith(resourceParameterNamePrefix)) {
				String value = request.getParameter(name);
				if (!value.equals("ignore")) {
					Integer mspResourceID = new Integer(name.substring(resourceParameterNamePrefix.length()));
					Integer pnetResourceID = new Integer(value);

					resourceResolver.addResourceMapping(mspResourceID, pnetResourceID);
				}
			}

		}
        // Process the assignor mappings
        final String assignorParameterNamePrefix = "assignor";
        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();

            if (name.startsWith(assignorParameterNamePrefix)) {
                String value = request.getParameter(name);
                Integer mspResourceID = new Integer(name.substring(assignorParameterNamePrefix.length()));
                Integer pnetAssignorID = new Integer(value);

                assignorMapper.put(mspResourceID, pnetAssignorID);
            }

        }        

		if (scheduleImporter.isImportResourceWorkingTimeCalendars() && resourceResolver.hasResourceMapping() && resourceResolver.getResourceEntries().size() > 0) {
			// Chosen option to import working time calendars
			// We provide ability to select which to import, and to resolve
			// multiple mappings
			model.put("resourceResolver", resourceResolver);
			this.viewName = "/schedule/importer/ResolveResources.jsp";

		} else {
			// Prepare for import by processing the resource mappings and
			// computing summary information
			scheduleImporter.prepareImport();
			this.viewName = "/schedule/importer/ImportSummary.jsp";

		}

		// No additional model items
		return model;
	}

}
