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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.mvc.Controller;
import net.project.base.mvc.Handler;
import net.project.base.mvc.HandlerMapping;
import net.project.base.mvc.IView;
import net.project.security.SecurityProvider;

public class ImportController extends Controller {

	public void controlRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// Requisite objects
		SecurityProvider sp = (SecurityProvider) request.getSession().getAttribute("securityProvider");

		// Handle the request, checking security and doing any operations that
		// are required.
		Handler handler = HandlerMapping.getHandler(request);
		handler.validateSecurity(sp.getCheckedModuleID(), sp.getCheckedActionID(), sp.getCheckedObjectID(), request);
		Map model = handler.handleRequest(request, response);
		// Now render the view
		IView view = handler.getView();
		view.render(model, request, response);

	}
}
