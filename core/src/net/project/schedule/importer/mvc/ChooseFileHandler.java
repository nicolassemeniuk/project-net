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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;

public class ChooseFileHandler extends Handler {

	public ChooseFileHandler(HttpServletRequest request) {
		super(request);
	}

	public String getViewName() {
		return "/schedule/importer/ChooseFile.jsp";
	}

	public void validateSecurity(int module, int action, String objectID, HttpServletRequest request)
			throws AuthorizationFailedException, PnetException {
		AccessVerifier.verifyAccess(Module.SCHEDULE, Action.MODIFY, objectID);
	}

	public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// No additional model items
		Map model = Collections.EMPTY_MAP;
		return model;

	}
}