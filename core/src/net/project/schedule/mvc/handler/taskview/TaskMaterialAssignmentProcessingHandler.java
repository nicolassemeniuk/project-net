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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.material.MaterialAssignment;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.ErrorReporter;

/**
 * Provides a handler for processing task material assignments.
 */
public class TaskMaterialAssignmentProcessingHandler extends AbstractTaskAssignmentHandler {

    public TaskMaterialAssignmentProcessingHandler(HttpServletRequest request) {
        super(request);
    }

    public Map<String, ErrorReporter> handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // We specifically do not call the super.handleRequest() method since it reloads the
        // schedule entry
        Map<String, ErrorReporter> model = new HashMap<String, ErrorReporter>();
        ErrorReporter errorReporter = new ErrorReporter();
        model.put("errorReporter", errorReporter);

        Schedule schedule = (Schedule) getSessionVar("schedule");
        ScheduleEntry scheduleEntry = (ScheduleEntry) getSessionVar("scheduleEntry");
        User user = (User) getSessionVar("user");
        String theAction = request.getParameter("theAction");
                
        if (theAction.equals("submit") || theAction.equals("update") || theAction.equals("overallocation")) {
            String assignorId = request.getParameter("assignorUser");
            if(assignorId == null)
                assignorId = user.getID();

            for(Iterator<MaterialAssignment> it = scheduleEntry.getMaterialAssignmentsList().getIterator(); it.hasNext();)
            	it.next().setAssignorId(assignorId);

            if (!errorReporter.errorsFound()) {
                // We have to store the schedule entry, since work, dates and duration
                // May have changed due to modified assignments
                // This will store the assignments too.
                scheduleEntry.store(true, schedule);
                //Reload the schedule entry to make sure it gets the latest stuff
                String id = scheduleEntry.getID();
                scheduleEntry.clear();
                scheduleEntry.setID(id);
                scheduleEntry.load();

            }

        }

        // Now decide where to go
        String refLink = request.getParameter("refLink");
        String viewAssignmentsUrl = "/servlet/ScheduleController/TaskView/Assignments?module" + Module.SCHEDULE + "&action=" + Action.VIEW;

        if (errorReporter.errorsFound()) {
            setViewName(viewAssignmentsUrl);

        } else if (theAction != null && theAction.equals("overallocation")) {
            setViewName("/servlet/ScheduleController/TaskView/FixOverallocations");

        } else if (theAction != null && (theAction.equals("update") || theAction.equals("deleteAssignment"))) {
            // In the case of an "update" go back to assignments
            setViewName(viewAssignmentsUrl);

        } else if (refLink != null && ! refLink.equals("")) {
            String decodedRefererLink = java.net.URLDecoder.decode(refLink, SessionManager.getCharacterEncoding());
            setViewName(decodedRefererLink);
            setRedirect(true);

        } else {
            // In the case of all other actions, and without a refLink, we go back to workplan main
            setViewName("/workplan/taskview?module=" + Module.SCHEDULE + "&action=" + Action.VIEW);
        }

        return model;
    }

  
}