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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentManager;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskFinder;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.Validator;

public abstract class AbstractTaskViewHandler extends Handler {
    private String view;

    public AbstractTaskViewHandler(HttpServletRequest request) {
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
        return view;
    }

    void setViewName(String viewName) {
        this.view = viewName;
    }

    /**
     * Determine if the user is currently in the wrong space.  If they are, we are going to need
     * @param entry
     * @param user
     * @return
     * @throws net.project.persistence.PersistenceException
     */
    protected boolean dealWithSpaceChange(ScheduleEntry entry, User user) throws PersistenceException {
        boolean spaceChangedRequired = false;
        Space currentSpace = user.getCurrentSpace();
        String currentSpaceType = currentSpace.getType();

        // If the user is in their personal space, change context to task's project space
        if (currentSpaceType == null || currentSpaceType.equals(Space.PERSONAL_SPACE) || (!currentSpace.getID().equals(entry.getSpaceID()))) {
            String project_id = entry.getSpaceID();
            try {
                view = "/project/Main.jsp?id=" + project_id
                    + "&page=" + URLEncoder.encode(request.getRequestURI() + "?" +
                    request.getQueryString(), SessionManager.getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                //This can't really happen -- we would have dumped out a long time
                //before getting to this page.
            }
            spaceChangedRequired = true;
        }

        return spaceChangedRequired;
    }

    protected boolean dealWithExternalNotification(HttpServletRequest request, ScheduleEntry entry, String id, Map model) throws PersistenceException, IOException {
        boolean externalNotificationFound = false;

        //Ensure that the user is getting to this page in a "traditional" way.
        //They should be in a frame in the application, if not, redirect them so
        //they will be.  This is probably triggered most frequently if the user
        //attempts to access this page through external notifications.
        if ((request.getParameter("external") != null) && (request.getParameter("inframe") == null)) {
            view = "/NavigationFrameset.jsp";

            //Clear out anything already stored in the model, we are going to be
            //redirecting to somewhere else instead.
            model.clear();

            request.getSession().setAttribute("requestedPage",
                SessionManager.getJSPRootURL() + "/project/Main.jsp?id=" +
                entry.getSpaceID() + "&page=" +
                URLEncoder.encode(request.getRequestURI() + "?id=" +
                id + "&module=" + Module.SCHEDULE + "&action" + Action.VIEW,
                    SessionManager.getCharacterEncoding()));

            externalNotificationFound = true;
        }

        return externalNotificationFound;
    }

    /**
     * Load the schedule entry that the user has requested.
     * <p/>
     * Replaces the current session scheduleEntry with the loaded one.
     * @param request used to update the session schedule entry
     * @param id the id of the schedule entry to load
     * @return the loaded schedule entry
     * @throws net.project.persistence.PersistenceException
     */
    protected ScheduleEntry getScheduleEntry(HttpServletRequest request, String id) throws PersistenceException {
        ScheduleEntry entry;

        //Always reload the task
        entry = new TaskFinder().findObjectByID(id, true, true);
        request.getSession().setAttribute("scheduleEntry", entry);

        return entry;
    }

    protected void setRefLink(HttpServletRequest request, Map model) throws UnsupportedEncodingException {
        String refLink = request.getParameter("refLink");
        String refLinkEncoded = "";
        if (!Validator.isBlankOrNull(refLink)) {
            refLinkEncoded = URLEncoder.encode(refLink, SessionManager.getCharacterEncoding());
        } else {
            refLinkEncoded = URLEncoder.encode("/workplan/taskview?module=" + net.project.base.Module.SCHEDULE, SessionManager.getCharacterEncoding());
        }
        // Can't use passThru; Due to nature of existing code, there may be more than one
        // request parameters of "refLink" (because if there is already a refLink, and you
        // forward to a URL containing ?refLink="..." you get two of them)
        // However, we have no desire to pass thru that array of refLink values;
        // we only want the first one (which is given to us by request.getParameter("refLink"))
        // If we used passThru, that array of refLinks would be passed and the JSP page would fail
        // because request.getAttribute("refLink") would return an array instead of a String
        model.put("refLink", refLink);
        model.put("refLinkEncoded", refLinkEncoded);
    }

    protected String findID(HttpServletRequest request) {
        //Look in both the parameters and the attributes for the id.  Unfortunately,
        //the code copied from TaskViewAssignments.jsp doesn't specify why we
        //need to do this.
        String id = request.getParameter("id");
        if (Validator.isBlankOrNull(id)) {
            id = (String)request.getAttribute("id");
        }
        return id;
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
     * @throws java.lang.Exception if any error occurs.
     */
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();
        ErrorReporter errors = new ErrorReporter();
        String id = (String)getVar("id");
        User user = (User)getSessionVar("user");

        //Get an appropriate schedule entry for this request; puts in the session
        ScheduleEntry entry = getScheduleEntry(request, id);
        if(entry == null) {
            ErrorDescription ed = new ErrorDescription(PropertyProvider.get("prm.schedule.error.message.noscheduleentry", id));
            errors.addError(ed);
            model.put("errorReporter", errors);
            throw new PnetException(PropertyProvider.get("prm.schedule.error.message.noscheduleentry", id));
        } else {
            model.put("scheduleEntry", entry);

            //Put the schedule entry in the session just in case it hasn't made it
            //in there yet.
            request.getSession().setAttribute("scheduleEntry", entry);

            //Need to redirect user if they are requesting an external notification
            if (dealWithExternalNotification(request, entry, id, model)) {
                return model;
            }

            //Need to deal with the situation where the user has tried to view a
            //task, but they are in the wrong space
            if (dealWithSpaceChange(entry, user)) {
                return model;
            } else {
                //It isn't useful to load the schedule unless the space is right
                ensureSchedule(entry, user);
            }
        }
        //Set variables for the referrer, which will be important if we need
        //to redirect to a location other than the default.
        setRefLink(request, model);

        return model;
    }

    /**
     * Make sure there is a schedule in the session.
     */
    private void ensureSchedule(ScheduleEntry entry, User user) throws PersistenceException {
        Schedule schedule = (Schedule)getSessionVar("schedule");
        String spaceID = entry.getSpaceID();
        String scheduleSpaceID = (schedule != null && schedule.getSpaceId() != null ? schedule.getSpaceId() : "");

        if (schedule == null || !spaceID.equals(scheduleSpaceID)) {
            //Reload the space id for the schedule.
            schedule = new Schedule();
            schedule.setSpace(user.getCurrentSpace());
            schedule.loadAll();

            setSessionVar("schedule", schedule);
        }
    }

    /**
     * Populate an assignment manager with the assignees for this object.
     * <p/>
     * Places it in session as <code>assignmentManager</code>
     * @param entryID a <code>String</code> containing the primary key of the
     * schedule entry we want to load.
     * @return a <code>AssignmentManager</code> object loaded with the assignees
     * for the given entry id.
     * @throws PersistenceException if there is an error loading the assignees
     * from the database.
     */
    protected AssignmentManager populateAssignmentManager(String entryID) throws PersistenceException {
        AssignmentManager assignmentManager = new AssignmentManager();
        assignmentManager.reset();
        assignmentManager.setObjectID(entryID);
        assignmentManager.loadAssigneesForObject();

        //Store this assignment manager in the session -- it is going to be used
        //again in the handling page
        request.getSession().setAttribute("assignmentManager", assignmentManager);

        return assignmentManager;
    }
}
