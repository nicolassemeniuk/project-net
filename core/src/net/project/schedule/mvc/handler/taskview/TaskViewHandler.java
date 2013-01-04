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

 package net.project.schedule.mvc.handler.taskview;

import javax.servlet.http.HttpServletRequest;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.util.Validator;

/**
 * This handler is designed to replace direct access to the "TaskView.jsp" page.
 * Originally, this was necessary because we moved from just having a single
 * task object to having a hierarchy of objects.  We were having problems with
 * things in the session.  Although we could have solved it in other ways, the
 * MVC method turns out to be a good way to solve the problem for TaskEdit, so
 * I extended it to TaskView.jsp too.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class TaskViewHandler extends AbstractTaskViewHandler {

    /**
     * Standard constructor.
     *
     * @param request a <code>HttpServletRequest</code> object that gives the
     * code access to the request parameters.
     */
    public TaskViewHandler(HttpServletRequest request) {
        super(request);
        setViewName("/schedule/TaskView.jsp");
    }

    /**
     * Ensure that the requester has proper rights to access this object.  For
     * objects that aren't excluded from security checks, this will just
     * consist of verifying that the parameters that were used to access this
     * page were correct (that is, that the requester didn't try to "spoof it"
     * by using a module and action they have permission to.)
     *
     * @param module the <code>int</code> value representing the module that
     * was passed to security.
     * @param action the <code>int</code> value that was passed through security
     * for the action.
     * @param objectID the <code>String</code> value for objectID that was
     * passed through security.
     * @param request the entire request that was submitted to the schedule
     * controller.
     * @throws net.project.security.AuthorizationFailedException if the user didn't have the proper
     * credentials to view this page, or if they tried to spoof security.
     * @throws net.project.base.PnetException if any other error occurred.
     */
    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {
        //According to the original documentation for the TaskView.jsp page,
        //TaskView.jsp can be reached with either view or modify permissions.  I
        //haven't figured out where that is occuring yet -- I'll just have to
        //take TaskView.jsp's word on it for now.
        if (action == Action.VIEW) {
            AccessVerifier.verifyAccess(Module.SCHEDULE, Action.VIEW, objectID);
        } else {
            AccessVerifier.verifyAccess(Module.SCHEDULE, Action.MODIFY, objectID);
        }

        if (Validator.isBlankOrNull(objectID)) {
            throw new AuthorizationFailedException("task id parameter missing");
        }
    }
}
