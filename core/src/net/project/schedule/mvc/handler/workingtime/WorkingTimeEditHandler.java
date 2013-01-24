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

 /*----------------------------------------------------------------------+
|                                                                       
|     $RCSfile$
|    $Revision: 20523 $
|        $Date: 2010-03-04 10:18:56 -0300 (jue, 04 mar 2010) $     $Author: nilesh $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.schedule.mvc.handler.workingtime;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.PnetException;
import net.project.base.mvc.ControllerException;
import net.project.base.property.PropertyProvider;
import net.project.calendar.workingtime.WorkingTimeCalendarCreateHelper;
import net.project.calendar.workingtime.WorkingTimeCalendarHelper;
import net.project.calendar.workingtime.WorkingTimeCalendarListHelper;
import net.project.schedule.Schedule;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.util.ErrorReporter;
import net.project.util.Validator;

/**
 * Provides a helper class for editing a working time calendar.
 * <p>
 * Handles all aspects of editing: create, modify, remove.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class WorkingTimeEditHandler extends WorkingTimeHandler {

    /** The view to navigate to. */
    private String viewName;

    /** The submitted action to perform. */
    private final String theAction;
    
    public WorkingTimeEditHandler(HttpServletRequest request) {
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

        AccessVerifier.verifyAccess(module, Action.MODIFY, objectID);
    }

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map model = new HashMap();
        ErrorReporter errorReporter = new ErrorReporter();

        if (theAction == null || theAction.equals("modify")) {

            if (theAction != null && theAction.equals("modify")) {
                // Came from the list screen
                String selected = request.getParameter("selected");
                if (Validator.isBlankOrNull(selected)) {
                    throw new ControllerException("Missing request parameter 'selected' for action 'modify'");
                }

                // Now edit it
                prepareEdit(request, model, selected);

            } else {
                // Navigate from some other place
                String calendarID = request.getParameter("calendarID");
                if (Validator.isBlankOrNull(calendarID)) {
                    throw new ControllerException("Missing request parameter 'calendarID'");
                }

                // Now edit it
                prepareEdit(request, model, calendarID);
            }

        } else if (theAction.equals("create")) {
            // Create a new calendar
            prepareCreate(request, model);

        } else if (theAction.equals("remove")) {
            // Remove a calendar
            String selected = request.getParameter("selected");
            if (Validator.isBlankOrNull(selected)) {
                throw new ControllerException("Missing request parameter 'selected' for action 'remove'");

            } else {
                // Do the remove

                Schedule schedule = (Schedule) request.getSession().getAttribute("schedule");
                if (schedule == null) {
                    throw new ControllerException("Missing session attribute 'schedule'");
                }

                WorkingTimeCalendarHelper helper = new WorkingTimeCalendarHelper(request, provider, selected);
                WorkingTimeCalendarListHelper calendarList = new WorkingTimeCalendarListHelper(request, provider);
                if (helper.isDefault()) {
                    // Cannot remove the default calendar; remove will barf
                    errorReporter.addError(PropertyProvider.get("prm.schedule.workingtime.list.cannotremovedefault.message"));
                } else if(schedule.getDefaultCalendarID() == null && calendarList.hasDependentCalendars(helper.getID())) {
                    // sjmittal: No default calendar set; so removed one cannot be replaced by new parent
                    errorReporter.addError(PropertyProvider.get("prm.schedule.workingtime.list.cannotremove.message"));
                } else {
                    helper.remove();
                }

            }

            prepareList(request, model);

        } else if (theAction.equals("submit")) {

            // Update Day of Week

            String calendarID = request.getParameter("calendarID");
            if (Validator.isBlankOrNull(calendarID)) {
                throw new ControllerException("Missing request paramater 'calendarID'");
            }

            // Make a helper for the edited calendar
            // The helper translates presentation values into actions on the calendar definition
            WorkingTimeCalendarHelper helper = new WorkingTimeCalendarHelper(request, provider, calendarID);

            helper.processSubmission(request, errorReporter);

            if (errorReporter.errorsFound()) {
                // Go back to edit view
                prepareEdit(model, helper);

            } else {

                // Now store the backed calendar
                helper.store();

                // Go back to list view
                prepareList(request, model);

            }
            
            //Added to handle auto calculate start and end dates.
            if (request.getSession().getAttribute("schedule") != null) {
	            Schedule schedule = (Schedule)request.getSession().getAttribute("schedule");
	            schedule.recalculateTaskTimes();
            }

        } else if (theAction.equals("preset")) {
            // User selected a preset calendar type

            String calendarID = request.getParameter("calendarID");
            if (Validator.isBlankOrNull(calendarID)) {
                throw new ControllerException("Missing request paramater 'calendarID'");
            }

            WorkingTimeCalendarHelper helper = new WorkingTimeCalendarHelper(request, provider, calendarID);
            helper.processPreset(request);
            prepareEdit(model, helper);

        }else if (theAction.equals("workingtimes")) {
            // User selected a preset calendar type

        	Schedule schedule = (Schedule) request.getSession().getAttribute("schedule");
            if (schedule == null) {
                throw new ControllerException("Missing session attribute 'schedule'");
            }
            if(!Validator.isBlankOrNull(request.getParameter("hoursPerDay"))
            	&& !Validator.isBlankOrNull(request.getParameter("hoursPerDay"))
            	&& !Validator.isBlankOrNull(request.getParameter("hoursPerDay"))){
            	schedule.setHoursPerDay(BigDecimal.valueOf(Double.parseDouble(request.getParameter("hoursPerDay"))));
                schedule.setHoursPerWeek(BigDecimal.valueOf(Double.parseDouble(request.getParameter("hoursPerWeek"))));
                schedule.setDaysPerMonth(BigDecimal.valueOf(Double.parseDouble(request.getParameter("daysPerMonth"))));
                schedule.store();
            }else{
            	errorReporter.addError(PropertyProvider.get("prm.schedule.properties.workingtimes.required.message"));
            }
            prepareList(request, model);
        } else {
            throw new ControllerException("Unknown action '" + theAction + "'");
        }


        model.put("errorReporter", errorReporter);
        return model;
    }

    private void prepareList(HttpServletRequest request, Map model) {
        WorkingTimeCalendarListHelper helper = new WorkingTimeCalendarListHelper(request, provider);
        model.put("listHelper", helper);
        model.put("action", "" + Action.VIEW);
        this.viewName = "/schedule/workingtime/List.jsp";
    }

    private void prepareCreate(HttpServletRequest request, Map model) {
        WorkingTimeCalendarCreateHelper helper = new WorkingTimeCalendarCreateHelper(request, provider);
        model.put("createHelper", helper);
        model.put("action", "" + Action.CREATE);
        this.viewName = "/schedule/workingtime/Create.jsp";
    }

    private void prepareEdit(HttpServletRequest request, Map model, String calendarID) {
        prepareEdit(model, new WorkingTimeCalendarHelper(request, provider, calendarID));
    }

    private void prepareEdit(Map model, WorkingTimeCalendarHelper helper) {
        model.put("editHelper", helper);
        model.put("action", "" + Action.MODIFY);
        this.viewName = "/schedule/workingtime/Edit.jsp";
    }
}