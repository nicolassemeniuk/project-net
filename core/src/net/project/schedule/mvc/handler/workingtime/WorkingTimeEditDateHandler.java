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
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.schedule.mvc.handler.workingtime;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.ControllerException;
import net.project.base.mvc.Handler;
import net.project.calendar.workingtime.WorkingTimeCalendarDateEntryHelper;
import net.project.calendar.workingtime.WorkingTimeCalendarHelper;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.util.ErrorReporter;
import net.project.util.Validator;

/**
 * Handler for editing dates.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class WorkingTimeEditDateHandler extends WorkingTimeHandler {

    /** The view to navigate to. */
    private String viewName;

    /** The submitted action to perform. */
    private final String theAction;

    public WorkingTimeEditDateHandler(HttpServletRequest request) {
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

    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request)
            throws AuthorizationFailedException, PnetException {

        AccessVerifier.verifyAccess(module, Action.MODIFY, objectID);
    }

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map model = new HashMap();
        ErrorReporter errorReporter = new ErrorReporter();

        if (theAction == null) {
            throw new ControllerException("Missing request parameter 'theAction'");

        } else if (theAction.equals("submit") || (theAction.equals("add"))) {

            // Submit the date
            String calendarID = request.getParameter("calendarID");
            if (Validator.isBlankOrNull(calendarID)) {
                throw new ControllerException("Missing request paramater 'calendarID'");
            }

            WorkingTimeCalendarHelper helper = new WorkingTimeCalendarHelper(request, provider, calendarID);
            WorkingTimeCalendarDateEntryHelper dateHelper = helper.makeDateEntryHelper(request);
            dateHelper.processSubmission(request, errorReporter);

            if (errorReporter.errorsFound()) {
                // Go back to edit date view
                model.put("calendarID", calendarID);
                model.put("dateHelper", dateHelper);
                prepareEditDate(model);

            } else {
                // Successfully processed the post
                dateHelper.store();

                // Now navigate depending on whether the user chose to
                // submit or add another
                if (theAction.equals("submit")) {
                    // Go back to Edit calendar view
                    model.put("editHelper", helper);
                    prepareEdit(model);

                } else {
                    // Add another
                    dateHelper = helper.makeDateEntryHelper(request);
                    model.put("calendarID", calendarID);
                    model.put("dateHelper", dateHelper);
                    prepareEditDate(model);

                }

            }


        } else if (theAction.equals("createDate") || theAction.equals("modifyDate")) {

            String calendarID = request.getParameter("calendarID");
            if (Validator.isBlankOrNull(calendarID)) {
                throw new ControllerException("Missing request paramater 'calendarID'");
            }

            WorkingTimeCalendarHelper helper = new WorkingTimeCalendarHelper(request, provider, calendarID);
            WorkingTimeCalendarDateEntryHelper dateHelper;

            if (theAction.equals("createDate")) {
                // Creating a date; no entryID required
                dateHelper = helper.makeDateEntryHelper(request);

            } else {
                // Editing a date; entryID required
                String entryID = request.getParameter("entryID");
                if (Validator.isBlankOrNull(entryID)) {
                    throw new ControllerException("Missing request parameter 'entryID'");
                }

                dateHelper = helper.makeDateEntryHelper(request, entryID);
            }

            model.put("calendarID", calendarID);
            model.put("dateHelper", dateHelper);
            prepareEditDate(model);

        } else if (theAction.equals("removeDate")) {
            // Removing a date

            String calendarID = request.getParameter("calendarID");
            if (Validator.isBlankOrNull(calendarID)) {
                throw new ControllerException("Missing request paramater 'calendarID'");
            }

            WorkingTimeCalendarHelper helper = new WorkingTimeCalendarHelper(request, provider, calendarID);
            WorkingTimeCalendarDateEntryHelper dateHelper;

            // Removing a date; entryID required
            String entryID = request.getParameter("entryID");
            if (Validator.isBlankOrNull(entryID)) {
                throw new ControllerException("Missing request parameter 'entryID'");
            }

            dateHelper = helper.makeDateEntryHelper(request, entryID);
            dateHelper.remove();
            model.put("editHelper", helper);
            prepareEdit(model);

        } else if (theAction.equals("preset")) {

            // Update working times with a preset (standard, nightshift, 24 hours)
            String calendarID = request.getParameter("calendarID");
            if (Validator.isBlankOrNull(calendarID)) {
                throw new ControllerException("Missing request paramater 'calendarID'");
            }

            WorkingTimeCalendarHelper helper = new WorkingTimeCalendarHelper(request, provider, calendarID);
            WorkingTimeCalendarDateEntryHelper dateHelper = helper.makeDateEntryHelper(request);

            dateHelper.processPreset(request);

            // Go back to edit date
            model.put("calendarID", calendarID);
            model.put("dateHelper", dateHelper);
            prepareEditDate(model);

        } else {
            throw new ControllerException("Unknown action '" + theAction + "'");
        }

        model.put("errorReporter", errorReporter);
        return model;
    }

    private void prepareEdit(Map model) {
        model.put("action", "" + Action.MODIFY);
        this.viewName = "/schedule/workingtime/Edit.jsp";
    }

    private void prepareEditDate(Map model) {
        model.put("action", "" + Action.MODIFY);
        this.viewName = "/schedule/workingtime/EditDate.jsp";
    }

}
