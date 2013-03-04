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

 package net.project.schedule.mvc.handler.taskedit;

import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.project.base.mvc.Handler;
import net.project.persistence.PersistenceException;
import net.project.schedule.Baseline;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskConstraintType;
import net.project.schedule.TaskFinder;
import net.project.schedule.TaskPriority;
import net.project.schedule.TaskType;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;
import net.project.util.Validator;

/**
 * Class which generically deals with the concept of "let me edit a schedule
 * entry".  This class contains the code that is common between editing and
 * creating tasks.  Previously all of the code for editing or creating a task
 * was in a single place.  This led to lots of nasty if statements.  Now there
 * are proper subclasses of this class to deal with the differences.
 *
 * @author Matthew Flower
 * @since Version 7.6
 */
public abstract class AbstractTaskEditHandler extends Handler {
    /**
     * Creates a new instance of AbstractTaskEditHandler.  (Not really, seems
     * how this is an abstract class.)
     *
     * @param request the request object that was passed to the
     * ScheduleController.
     */
    public AbstractTaskEditHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * Get the name of the view that we should render when we leave this page.
     *
     * @return a <code>String</code> containing the name of a view that we
     * want to render.
     */
    public String getViewName() {
        return "/schedule/TaskEdit.jsp";
    }

    /**
     * Add the appropriate items needed to render the view into a model.
     * Translation - get all the variables.
     *
     * @param scheduleEntry a <code>ScheduleEntry</code> that is going to be
     * edited.  This will be a new entry if this is a new task.
     * @param model
     * @param request
     * @throws java.lang.Exception
     */
    protected void handleRequest(ScheduleEntry scheduleEntry, Map model, HttpServletRequest request) throws Exception {
        if(scheduleEntry ==  null)
            return;
        
        //Current space, which will help load the left hand menu
        Space currentSpace = ((User)getSessionVar("user")).getCurrentSpace();
        model.put("spaceID", currentSpace.getID());

        //Load schedule
        Schedule schedule = (Schedule)getSessionVar("schedule");
        schedule.setSpace(currentSpace);
        schedule.load();

        scheduleEntry.setPlanID(schedule.getID());
        model.put("schedule", schedule);

        //Load scheduleEntry, if necessary
        model.put("scheduleEntry", scheduleEntry);

        //Figure out where to go if the user hits the cancel button
        String refLink = (request.getParameter("refLink") != null ? request.getParameter("refLink") : "");
        if (!refLink.equals("")) {
            refLink = URLEncoder.encode(refLink, SessionManager.getCharacterEncoding());
            model.put("refLink", refLink);
        }

        //If the schedule entry doesn't have a baseline in it, allow the user
        //to set the current default baseline as their baseline.  To do this,
        //we have to know that there really is a baseline right now.
        String baselineID = Baseline.getDefaultBaselineID(schedule.getID());
        model.put("baselineID", (baselineID == null ? "" : baselineID));

        //If we are returning here from an error reporting page, there might be
        //an error object in scope
        passThru(model, "errorReporter");

        //The page will render differently depending on whether we are creating
        //or editing.  We pass through action so the page knows how to render
        passThru(model, "action");
        passThru(model, "dependencies");

        //More environmental variables
        model.put("showStartEndDate", Boolean.valueOf(!schedule.isAutocalculateTaskEndpoints()));
        model.put("showStartEndDateWarnings", Boolean.valueOf(schedule.isAutocalculateTaskEndpoints()));
        model.put("constraintsRequiringDates", TaskConstraintType.getConstraintTypesRequiringDates());
        model.put("isReadOnly", Boolean.valueOf(scheduleEntry.getTaskType().equals(TaskType.SUMMARY) && schedule.isAutocalculateTaskEndpoints()));

        //Add variables containing the option lists that need to be present for the task page
        addOptionLists(scheduleEntry, model, currentSpace);

        //Determine which fields are editable
        model.put("filter", new ReadOnlyState(scheduleEntry, schedule));
    }

    /**
     * Add variables that contain Strings to render all of the option lists that
     * the view will need to render.
     * <p>
     * The option lists are <code>Collection</code> objects where each element
     * is an <code>IHTMLOption</code>.
     * </p>
     * @param scheduleEntry a <code>ScheduleEntry</code> object from which we will
     * get existing selected phases.
     * @param model the location where we are going to store the option lists.
     * @param currentSpace a <code>Space</code> object that we need in order to
     * know which phases to load.
     * @throws net.project.persistence.PersistenceException if an error occurs loading phases.
     */
    private void addOptionLists(ScheduleEntry scheduleEntry, Map model, Space currentSpace) throws PersistenceException {
        model.put("priorityOptions", TaskPriority.getAll());
        model.put("phaseOptions", Helper.getPhaseOptions(currentSpace));
        model.put("workUnitOptions", Helper.getWorkUnitOptions());
        model.put("workCompleteUnitOptions", Helper.getWorkCompleteUnitOptions());
        model.put("durationUnitOptions", Helper.getDurationUnitOptions());

    }

    /**
     * Load the schedule entry with the given id, unless it has been passed in
     * the request.
     *
     * @param request a <code>HttpServletReqeust</code> which contains the
     * information passed to the controller.
     * @param id the primary key of the task entry to load.
     * @return a <code>ScheduleEntry</code> that corresponds to the primary key
     *
     * @throws PersistenceException
     */
    protected ScheduleEntry loadExistingScheduleEntry(HttpServletRequest request, String id) throws PersistenceException {
        ScheduleEntry scheduleEntry;
        //If we are returning to an error page, we already have a scheduleEntry
        //available to us that is loaded with data -- reuse that.
        scheduleEntry = (ScheduleEntry)request.getAttribute("scheduleEntry");
        if (scheduleEntry == null) {
            scheduleEntry = (ScheduleEntry)request.getSession().getAttribute("scheduleEntry");
        }

        //We didn't find an object in the request, load it from the db.
        if (Validator.isBlankOrNull((String)request.getAttribute("donterase"))) {
            //Always reload the task, even though it might already be loaded
            //by the schedule.  This will prevent us from updating the
            //database with stale information.
            scheduleEntry = new TaskFinder().findObjectByID(id, true, true, true);
            request.getSession().setAttribute("scheduleEntry", scheduleEntry);
        } else {
            TaskType type = scheduleEntry.getTaskType();

            //Ensure that the schedule entry is the right type for what we are
            //creating.
            if (type != scheduleEntry.getTaskType()) {
                scheduleEntry = scheduleEntry.as(type);
                request.getSession().setAttribute("scheduleEntry", scheduleEntry);
            }
        }

        return scheduleEntry;
    }

}
