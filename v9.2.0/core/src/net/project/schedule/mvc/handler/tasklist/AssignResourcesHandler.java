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

 package net.project.schedule.mvc.handler.tasklist;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.base.property.PropertyProvider;
import net.project.database.DatabaseUtils;
import net.project.resource.AssignmentRoster;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.calc.TaskCalculationType;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.User;
import net.project.util.ErrorReporter;

/**
 * This page is the handler implementation for the page that comes up when you
 * click on the resources toolbar button on the workplan.
 *
 * @author Matthew Flower
 * @since Version 8.2.0
 */
public class AssignResourcesHandler extends Handler {
    private static final String SUCCESS_VIEW = "/schedule/AssignResourcesDialog.jsp";
    private static final String ERROR_VIEW = "/schedule/include/AssignResourcesDialogCancel.jsp";
    private String viewName = SUCCESS_VIEW;
    public AssignResourcesHandler(HttpServletRequest request) {
        super(request);
    }

    protected String getViewName() {
        return viewName;
    }

    public void validateSecurity(int module, int action,
                                 String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException, ParseException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Map handleRequest(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        Map model = new HashMap();
        Schedule schedule = (Schedule)getSessionVar("schedule");
        Map scheduleEntries = schedule.getEntryMap();

        String[] ids = request.getParameterValues("selected");
        
        if(null == ids){
            ids = StringUtils.split((String) request.getParameter("selectedIds"), ',');
        }

        /* Keep track of any tasks that are shared, we aren't going to be able
           to assign resources to them because it isn't allowed. */
        List sharedTasks = new ArrayList();
        List newIDList = new ArrayList();
        for (int i = 0; i < ids.length; i++) {
            ScheduleEntry entry = (ScheduleEntry)scheduleEntries.get(ids[i]);
            if (entry.isFromShare()) {
                sharedTasks.add(entry.getNameMaxLength40());
            } else {
                newIDList.add(ids[i]);
            }
        }

        if (sharedTasks.size() == ids.length) {
            //This is an error condition, we cannot assign to shared tasks
            model.put("errorMessage", PropertyProvider.get("prm.schedule.sharedtasks.allresources"));
            model.put("module", String.valueOf(Module.SCHEDULE));
            model.put("action", String.valueOf(Action.VIEW));

            viewName = ERROR_VIEW;
        } else {
            if (sharedTasks.size() > 0) {
                ErrorReporter errorReporter = new ErrorReporter();
                errorReporter.addWarning(PropertyProvider.get("prm.schedule.sharedtasks.resources.noassign", sharedTasks));
                model.put("errorReporter", errorReporter);
            }

            passThru(model, "module");
            passThru(model, "action");
            model.put("idList", DatabaseUtils.collectionToCSV(newIDList));
        }


        User user = (User)getSessionVar("user");

        // We'll record whether any task is fixed duration, effort driven, since
        // in those cases we will ignore the percentage for any replaced or added
        // assignment.  This behavior is implemented in the processing handler
        // we simply display a message to the user
        boolean isAnyTaskFixedDurationEffortDriven = false;

        //Find the date range we should be loading the maximum allocations of the
        //persons in the roster.
        Date startDate = null, endDate = null;
        for (int i = 0; i < ids.length; i++) {
            ScheduleEntry entry = (ScheduleEntry)scheduleEntries.get(ids[i]);
            if (startDate == null || entry.getStartTime().before(startDate)) {
                startDate = entry.getStartTime();
            }
            if (endDate == null || entry.getEndTime().after(endDate)) {
                endDate = entry.getEndTime();
            }
            isAnyTaskFixedDurationEffortDriven |= entry.getTaskCalculationType().equals(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN);
        }

        AssignmentRoster assignmentRoster = new AssignmentRoster();
        assignmentRoster.setDateStart(startDate);
        assignmentRoster.setDateEnd(endDate);
        assignmentRoster.setSpace(user.getCurrentSpace());
        assignmentRoster.load();

        model.put("assignmentRoster", assignmentRoster);
        model.put("isFixedDurationEffortDrivenSelected", Boolean.valueOf(isAnyTaskFixedDurationEffortDriven).toString());

        return model;
    }
}
