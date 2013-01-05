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
package net.project.schedule.mvc.handler.taskeditprocessing;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.ScheduleEntryFactory;
import net.project.schedule.TaskType;
import net.project.schedule.mvc.handler.AbstractTaskEditProcessingHandler;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;

/**
 * Handler which deals with the result of a user creating a task.  This handler
 * will save the class and redirect the user to the correct page.
 *
 * @author Matthew Flower
 * @since Version 7.6
 */
public class TaskCreateProcessingHandler extends AbstractTaskEditProcessingHandler {
    /**
     * Standard constructor.
     *
     * @param request a <code>HttpServletRequest</code> object which contains
     * the request parameters.
     */
    public TaskCreateProcessingHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * Gets the name of the view that will be rendered after processing is
     * complete.
     *
     * @return a <code>String</code> containing a name that uniquely identifies
     * a view that we are going to redirect to after processing the request.
     */
    public String getViewName() {
        String viewName;

        if (errorReporter.errorsFound()) {
            viewName = "/servlet/ScheduleController/TaskCreate";
        } else {
            viewName = "/servlet/ScheduleController/TaskView/Assignments";
        }

        return viewName;
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

        AccessVerifier.verifyAccess(Module.SCHEDULE, Action.CREATE);
    }

    /**
     * Add the appropriate information to the model that is required to render
     * the view for this request.
     *
     * @param request a <code>HttpServletRequest</code> that was passed to the
     * controller when it was created.
     * @param response a <code>HttpServletResponse</code> that we will use to
     * render the response.
     * @return a <code>Map</code> which is our "Model".
     * @throws java.lang.Exception if any error occurs.
     */
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();

        ScheduleEntry entry = getScheduleEntry(request);

        //Set some preliminary things that need to be present in a new schedule entry
        Schedule schedule = (Schedule)getSessionVar("schedule");
        entry.setPlanID(schedule.getID());

        //The super class knows how to do things common to creating and editing,
        //such as setting the data from the request and storing the task.
        handleRequest(model, entry, request, response);

        if (hasRefLink()) {
            model.put("refLink", getReferringLinkEncoded());
        }

        //Some additional variables needed for loading the task view assignments
        //page.  If have to go back to "taskEdit.jsp" we aren't going to use
        //these -- they have already been set.
        if (!errorReporter.errorsFound()) {
            model.put("id", entry.getID());
            model.put("module", String.valueOf(Module.SCHEDULE));
            model.put("action", String.valueOf(Action.VIEW));
        }

        return model;
    }

    private ScheduleEntry getScheduleEntry(HttpServletRequest request) {
        ScheduleEntry scheduleEntry = (ScheduleEntry)request.getSession().getAttribute("scheduleEntry");

        if (scheduleEntry == null) {
            scheduleEntry = ScheduleEntryFactory.createFromType(TaskType.TASK);
            request.getSession().setAttribute("scheduleEntry", scheduleEntry);
        } else {
            request.getSession().setAttribute("scheduleEntry", scheduleEntry);
        }


        return scheduleEntry;
    }
}
