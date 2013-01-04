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
import net.project.resource.AssignmentManager;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;

/**
 * Provides common functionality for TaskAssignment handler & processing handler.
 * @author Tim Morrow
 * @since Version 7.7.1
 */
abstract class AbstractTaskAssignmentHandler extends AbstractTaskViewHandler {

    protected AssignmentManager assignmentManager;

    public AbstractTaskAssignmentHandler(HttpServletRequest request) {
        super(request);
    }

    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {

        //First verify the parameters
        if (action == Action.VIEW) {
            AccessVerifier.verifyAccess(Module.SCHEDULE, Action.VIEW, objectID);
        } else if (action == Action.CREATE) {
            AccessVerifier.verifyAccess(Module.SCHEDULE, Action.CREATE, objectID);
        } else {
            AccessVerifier.verifyAccess(Module.SCHEDULE, Action.MODIFY, objectID);
        }

        //This page cannot be view unless the user is in the list of assignees.
        //This irritating artifact occurs because the task assignment page is
        //both a viewing and editing page at the same time.
        assignmentManager = populateAssignmentManager(objectID);
        //Author: Avinash Bhamare,  Date: 27th Mar 2006
		//bfd-2905 : Team member being allowed to modify resource assignments for a task  
        //if (!assignmentManager.isUserInAssignmentList(SessionManager.getUser().getID())) {
        if (assignmentManager.isUserInAssignmentList(SessionManager.getUser().getID())) {
            // Check modify permission if user is not assigned task
            // An exception is thrown if they have no permission
            SessionManager.getSecurityProvider().securityCheck(objectID, String.valueOf(Module.SCHEDULE), Action.MODIFY);
        }
    }
}