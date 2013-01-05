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

 package net.project.schedule.mvc.handler.workingtime;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.ControllerException;
import net.project.base.mvc.Handler;
import net.project.calendar.workingtime.WorkingTimeCalendarHelper;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.util.Validator;

/**
 * Handles Working times view.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class WorkingTimeViewHandler extends WorkingTimeHandler {

    public WorkingTimeViewHandler(HttpServletRequest request) {
        super(request);
    }

    public String getViewName() {
        return "/schedule/workingtime/View.jsp";
    }

    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {
        AccessVerifier.verifyAccess(module, Action.VIEW, objectID);
    }

    /**
     * Handles the request.
     * @param request
     * @param response
     * @return the model; contains:
     * <ul>
     * <li>calendarHelper ==> <code>WorkingTimeCalendarHelper</code>
     * <li>calendarDefinition ==> <code>WorkingTimeCalendarDefinition</code>
     * </ul>
     * @throws Exception
     */
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map model = new HashMap();

        String calendarID = request.getParameter("calendarID");
        if (Validator.isBlankOrNull(calendarID)) {
            throw new ControllerException("Missing request parameter 'calendarID'");
        }
        model.put("calendarHelper", new WorkingTimeCalendarHelper(request, provider, calendarID));

        return model;
    }

}
