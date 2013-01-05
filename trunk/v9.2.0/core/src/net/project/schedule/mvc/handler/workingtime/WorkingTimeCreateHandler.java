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
import net.project.base.property.PropertyProvider;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.ScheduleWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.WorkingTimeCalendarCreateHelper;
import net.project.calendar.workingtime.WorkingTimeCalendarHelper;
import net.project.resource.ResourceWorkingTimeCalendarProvider;
import net.project.schedule.Schedule;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.User;
import net.project.util.ErrorReporter;
import net.project.util.Validator;

/**
 * Provides a helper class for editing a working time calendar.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class WorkingTimeCreateHandler extends WorkingTimeHandler {

    /** The view to navigate to. */
    private String viewName;

    /** The submitted action to perform. */
    private final String theAction;
    
    public WorkingTimeCreateHandler(HttpServletRequest request) {
        super(request);

        String theAction = request.getParameter("theAction");
        if (!Validator.isBlankOrNull(theAction)) {
            this.theAction = theAction;
        } else {
            this.theAction = null;
        }

    }

    public String getViewName() {
        return this.viewName;
    }

    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {

        AccessVerifier.verifyAccess(module, Action.CREATE, objectID);
    }

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map model = new HashMap();
        ErrorReporter errorReporter = new ErrorReporter();

        if (theAction == null) {
            throw new ControllerException("Missing request parameter 'theAction'");

        } else if (theAction.equals("submit")) {
            // Create a new calendar

            // Use a helper to create the calendar
            WorkingTimeCalendarCreateHelper helper = new WorkingTimeCalendarCreateHelper(request, provider);

            String typeID = request.getParameter("calendarType");
            if (Validator.isBlankOrNull(typeID)) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.workingtime.create.type.isrequired.message"));

            } else {

                helper.setCalendarType(typeID);

                if (helper.getCalendarType().equals(WorkingTimeCalendarCreateHelper.CalendarType.BASE)) {
                    // Base calendar

                    String name = request.getParameter("name");
                    if (Validator.isBlankOrNull(name)) {
                        errorReporter.addError(PropertyProvider.get("prm.schedule.workingtime.create.name.isrequired.message"));
                    } else {
                        helper.setName(name);
                    }

                } else {
                    // Resource calendar

                    String resourceID = request.getParameter("resourceID");
                    if (Validator.isBlankOrNull(resourceID)) {
                        errorReporter.addError(PropertyProvider.get("prm.schedule.workingtime.create.resource.isrequired.message"));
                    } else {
                        helper.setResourceID(resourceID);
                    }

                    String parentCalendarID = request.getParameter("parentCalendarID");
                    if (Validator.isBlankOrNull(parentCalendarID)) {
                        errorReporter.addError(PropertyProvider.get("prm.schedule.workingtime.create.parentcalendar.isrequired.message"));
                    } else {
                        helper.setParentCalendarID(parentCalendarID);
                    }

                }

            }


            if (errorReporter.errorsFound()) {
                // Go back to create view
                model.put("createHelper", helper);
                model.put("action", "" + Action.CREATE);
                this.viewName = "/schedule/workingtime/Create.jsp";

            } else {
                // Continue to store the new calendar
                String calendarID = helper.store();

                // Go to edit view to edit newly created calendar
                WorkingTimeCalendarHelper editHelper = new WorkingTimeCalendarHelper(request, provider, calendarID);
                model.put("editHelper", editHelper);
                model.put("action", "" + Action.MODIFY);
                this.viewName = "/schedule/workingtime/Edit.jsp";
                
            }

        } else {
            throw new ControllerException("Unknown action '" + theAction + "'");
        }


        model.put("errorReporter", errorReporter);
        return model;
    }

}
