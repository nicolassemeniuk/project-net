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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule.mvc.handler.taskview;

import javax.servlet.http.HttpServletRequest;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.resource.AssignmentManager;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;

/**
 * MVC Handler responsible for creating the objects necessary to render the
 * "Advanced" tab of the TaskView pages.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class TaskAdvancedHandler extends AbstractTaskViewHandler {
    public TaskAdvancedHandler(HttpServletRequest request) {
        super(request);
        setViewName("/schedule/TaskAdvanced.jsp");
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
     * @throws AuthorizationFailedException if the user didn't have the proper
     * credentials to view this page, or if they tried to spoof security.
     * @throws PnetException if any other error occurred.
     */
    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {

        AccessVerifier.verifyAccess(Module.SCHEDULE, Action.VIEW, objectID);

        //This page cannot be view unless the user is in the list of assignees.
        //This irritating artifact occurs because the task assignment page is
        //both a viewing and editing page at the same time.
        AssignmentManager assignmentManager = populateAssignmentManager(objectID);
        if (!assignmentManager.isUserInAssignmentList(SessionManager.getUser().getID())) {
            // Check modify permission if user is not assigned task
            // An exception is thrown if they have no permission
            SessionManager.getSecurityProvider().securityCheck(objectID, String.valueOf(Module.SCHEDULE), Action.MODIFY);
        }

    }
}
