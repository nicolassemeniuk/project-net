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

 package net.project.metric.mvc.handler;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.personal.metric.PersonalAssignmentMetricsGroup;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;
import net.project.security.User;

/**
 * Handler for PersonalAssignmentMetricsGroup(s)
 *
 * @author Philip Dixon
 * @since Version 7.7
 */
public class MetricsPersonalAssignmentHandler extends Handler {
    public MetricsPersonalAssignmentHandler(HttpServletRequest request) {
        super(request);
    }

    public String getViewName() {
        return "/personal/include/personalAssignmentMetrics.jsp";
    }

    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {
        AccessVerifier.verifyAccess(Module.PERSONAL_SPACE, Action.VIEW);
    }

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map model = new HashMap();
        User user = SessionManager.getUser();

        model.put("personalAssignmentMetrics", PersonalAssignmentMetricsGroup.makeLoaded(user));

        return model;
    }
}
