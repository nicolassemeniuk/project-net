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

 package net.project.schedule.mvc.handler.taskeditprocessing;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentManager;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskFinder;
import net.project.schedule.mvc.handler.AbstractTaskEditProcessingHandler;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SecurityProvider;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.ErrorDescription;
import net.project.util.Validator;

/**
 * Class which handles the user's request to "modify this schedule entry to have
 * these values" which is effectively the submit of the TaskEdit.jsp page.
 *
 * @see net.project.schedule.mvc.handler.taskeditprocessing.TaskCreateProcessingHandler
 * @author Matthew Flower
 * @since Version 7.6
 */
public class TaskModifyProcessingHandler extends AbstractTaskEditProcessingHandler {
    /**
     * Standard constructor.
     *
     * @param request a <code>HttpServletRequest</code> object which contains
     * the request parameters.
     */
    public TaskModifyProcessingHandler(HttpServletRequest request) {
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
            viewName = "/servlet/ScheduleController/TaskEdit?module="+Module.SCHEDULE+"&id="+(String)getVar("id")+"&action="+Action.VIEW;
            setSessionVar("errorReporter", errorReporter);
        } else if (hasRefLink()) {
            viewName = getReferringLink();
        } else {
            viewName = "/workplan/taskview";
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
    public void validateSecurity(int module, int action, String objectID,
        HttpServletRequest request) throws AuthorizationFailedException, PnetException {

        String id = objectID;
        SecurityProvider sp = SessionManager.getSecurityProvider();

        //Check to make sure we got here with the right parameters
        AccessVerifier.verifyAccess(Module.SCHEDULE, Action.VIEW);

        //Load the assignees for this task -- they will be allowed to do modifications
        User user = (User)getSessionVar("user");
        AssignmentManager am = new AssignmentManager();
        if (!Validator.isBlankOrNull(objectID)) {
            am.setObjectID(objectID);
            am.loadAssigneesForObject();
        }

        //Check to see if the user is in the assignment list.  If not, they will
        //have to pass a stardard security check.
        if (!am.isUserInAssignmentList(user.getID())) {
            sp.securityCheck(id, Integer.toString(Module.SCHEDULE), Action.MODIFY);
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
     * @throws java.lang.Exception if any error occurs.
     */
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();
        setRedirect(true);
        String id = (String)getVar("id");

        ScheduleEntry entry = getScheduleEntry(id);
        if(entry == null) {
            ErrorDescription ed = new ErrorDescription(PropertyProvider.get("prm.schedule.error.message.noscheduleentry", id));
            errorReporter.addError(ed);
            model.put("errorReporter", errorReporter);
        }
        
        handleRequest(model, entry, request, response);

        if (!hasRefLink()) {
            passThru(model, "module");
            model.put("action", String.valueOf(Action.VIEW));
        }

        //Reload the task -- it might have changed through the task
        //endpoint calculation and this wouldn't have been reflected
        //in this schedule entry.
        if(!errorReporter.errorsFound()) {
            entry.clear();
            entry.setID(id);
            entry.load();
        }
        
        return model;
    }

    /**
     * Given an id, find a schedule entry that corresponds to it.
     *
     * @param id a <code>String</code> which contains the primary key for the
     * schedule entry.
     * @return a <code>ScheduleEntry</code> corresponding to that id.
     * @throws net.project.persistence.PersistenceException if any error occurs while loading the task.
     */
    protected ScheduleEntry getScheduleEntry(String id) throws PersistenceException {
        ScheduleEntry scheduleEntry = (ScheduleEntry)request.getSession().getAttribute("scheduleEntry");

        //We didn't find an object in the request, load it from the db.
        if (scheduleEntry == null) {
            //Always reload the task, even though it might already be loaded
            //by the schedule.  This will prevent us from updating the
            //database with stale information.
            scheduleEntry = new TaskFinder().findObjectByID(id, true, true);
            request.getSession().setAttribute("scheduleEntry", scheduleEntry);
        } else {
            request.getSession().setAttribute("scheduleEntry", scheduleEntry);
        }

        return scheduleEntry;
    }
}
