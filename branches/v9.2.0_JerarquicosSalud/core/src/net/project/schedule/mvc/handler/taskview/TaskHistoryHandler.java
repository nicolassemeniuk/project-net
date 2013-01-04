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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import antlr.StringUtils;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleEntryHistory;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;

/**
 * Handler class responsible for rendering the "History" tab on the task view.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class TaskHistoryHandler extends AbstractTaskViewHandler {
    public TaskHistoryHandler(HttpServletRequest request) {
        super(request);
        setViewName("/schedule/TaskHistory.jsp");
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
    public void validateSecurity(int module, int action,
        String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {

        if (action == Action.VIEW) {
            AccessVerifier.verifyAccess(Module.SCHEDULE, Action.VIEW, objectID);
        } else {
            AccessVerifier.verifyAccess(Module.SCHEDULE, Action.MODIFY, objectID);
        }
    }

    /**
     * Add the necessary elements to the model that are required to render a
     * view.  Often this will include things like loading variables that are
     * needed in a page and adding them to the model.
     *
     * The views themselves should not be doing any loading from the database.
     * The whole reason for an mvc architecture is to avoid that.  All loading
     * should occur in the handler.
     *
     * @param request the <code>HttpServletRequest</code> that resulted from the
     * user submitting the page.
     * @param response the <code>HttpServletResponse</code> that will allow us
     * to pass information back to the user.
     * @return a <code>Map</code> which is the updated model.
     * @throws Exception if any error occurs.
     */
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = super.handleRequest(request, response);
        ScheduleEntry entry = (ScheduleEntry)model.get("scheduleEntry");
        if(request.getParameter("showBlog")==null){
        //Add the history object to the model
	        ScheduleEntryHistory history = new ScheduleEntryHistory();
	        history.setTaskID(entry.getID());
	        history.load();
	        model.put("history", history);
        }else{
        	setViewName("/schedule/TaskBlogs.jsp");
        }
        return model;
    }
}
