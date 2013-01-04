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
|   $Revision: 19181 $
|       $Date: 2009-05-08 14:17:30 -0300 (vie, 08 may 2009) $
|     $Author: nilesh $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule.mvc.handler.workingtime;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.ControllerException;
import net.project.base.mvc.Handler;
import net.project.calendar.workingtime.WorkingTimeCalendarListHelper;
import net.project.schedule.Schedule;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.util.ErrorReporter;
import net.project.util.Validator;

/**
 * Handles Working times list.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class WorkingTimeListHandler extends WorkingTimeHandler {

    /** The view to navigate to. */
    private String viewName;

    /** The current action. */
    private final String theAction;

    public WorkingTimeListHandler(HttpServletRequest request) {
        super(request);
        theAction = request.getParameter("theAction");
    }

    public String getViewName() {
        return this.viewName;
    }

    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {

        if (isChangeDefaultAction()) {
            AccessVerifier.verifyAccess(module, Action.MODIFY, objectID);

        } else {
            // When displaying the list, only view permissions required in schedule
            AccessVerifier.verifyAccess(module, Action.VIEW, objectID);
        }

    }

    private boolean isChangeDefaultAction() {
        return this.theAction != null && this.theAction.equals("changeDefault");
    }

    /**
     * Handles the request.
     * @param request
     * @param response
     * @return the model; contains:
     * <ul>
     * <li>listHelper ==> <code>WorkingTimeCalendarListHelper</code> when returning to the list view
     * <li>editHelper ==> <code>WorkingTimeCalendarHelper</code> when returning to the edit view
     * </ul>
     * @throws Exception
     */
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();

        if (isChangeDefaultAction()) {
            // Change default calendar
            String defaultCalendarID = request.getParameter("defaultCalendarID");
            if (Validator.isBlankOrNull(defaultCalendarID)) {
                throw new ControllerException("Missing request parameter 'defaultCalendarID'");
            }

            WorkingTimeCalendarListHelper helper = new WorkingTimeCalendarListHelper(request, provider);
            helper.changeDefaultCalendar(defaultCalendarID);
            
            //Added to handle auto calculate start and end dates.
            if (request.getSession().getAttribute("schedule") != null) {
            	Schedule schedule = (Schedule)request.getSession().getAttribute("schedule");
            	schedule.recalculateTaskTimes();
            }
            
            prepareList(request, model);

        } else {
            // Simply view list
            prepareList(request, model);
        }

        model.put("errorReporter", new ErrorReporter());
        return model;
    }

    private void prepareList(HttpServletRequest request, Map model) {
        WorkingTimeCalendarListHelper helper = new WorkingTimeCalendarListHelper(request, provider);
        model.put("listHelper", helper);
        model.put("action", "" + Action.VIEW);
        this.viewName = "/schedule/workingtime/List.jsp";
    }

}
