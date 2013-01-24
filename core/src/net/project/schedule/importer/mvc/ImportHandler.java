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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.ControllerException;
import net.project.base.mvc.Handler;
import net.project.base.mvc.IView;
import net.project.base.mvc.JSPView;
import net.project.base.mvc.RedirectingJSPView;
import net.project.schedule.importer.XMLImporter;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.util.ErrorReporter;

public class ImportHandler extends Handler {

	/**
	 * The view for displaying import errors.
	 */
	private static final IView ERRORS_VIEW = new JSPView("/schedule/importer/ImportErrors.jsp");

	/**
	 * The view to navigate to upon success.
	 */
	private static final IView SUCCESS_VIEW = new RedirectingJSPView("/workplan/taskview", "module=" + Module.SCHEDULE + "&action=" + Action.VIEW);

	private IView view;

	public ImportHandler(HttpServletRequest request) {
		super(request);
	}

	/**
	 * This implementation throws an <code>UnsupportedOperationException</code>
	 * always since this class overrides {@link #getView()} to return a view.
	 * 
	 * @return never returns
	 * @throws UnsupportedOperationException
	 *             always
	 */
	public String getViewName() {
		throw new UnsupportedOperationException("ImportHandler cannot return a simple view name.  Use getView() instead.");
	}

	/**
	 * Returns the appropriate view to navigate to upon completion of this
	 * handler.
	 * <p>
	 * When errors occur, an error view is forwarded to. When successful, a
	 * redirection to Schedule Main occurs.
	 * </p>
	 * 
	 * @return
	 */
	public IView getView() {
		return view;
	}

	public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {
		AccessVerifier.verifyAccess(Module.SCHEDULE, Action.MODIFY, objectID);
	}

	public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		XMLImporter scheduleImporter = (XMLImporter) request.getSession().getAttribute("scheduleImporter");
		if (scheduleImporter == null) {
			throw new ControllerException("Missing session attribute 'scheduleImporter'");
		}

		Map model = new HashMap();

		// Perform the importer
		scheduleImporter.importSchedule();

		ErrorReporter errors = scheduleImporter.getErrorReporter();
		if (errors.errorsFound()) {
			// Display errors
			request.getSession().setAttribute("errors", errors);
			model.put("errors", errors);
			this.view = ERRORS_VIEW;

		} else {
			// Return to schedule main
			this.view = SUCCESS_VIEW;
		}
		File tempFile = new File( scheduleImporter.getFileName());
		tempFile.delete();
		
		// free up the session with schedule importer
		// request.getSession().removeAttribute("scheduleImporter");
		return model;
	}
}
