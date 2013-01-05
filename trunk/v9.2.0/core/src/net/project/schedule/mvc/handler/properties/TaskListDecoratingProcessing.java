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

 package net.project.schedule.mvc.handler.properties;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.gui.html.HTMLBackgroundColor;
import net.project.resource.PersonProperty;
import net.project.resource.PersonPropertyGlobalScope;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.Validator;

public class TaskListDecoratingProcessing extends Handler {
    public TaskListDecoratingProcessing(HttpServletRequest request) {
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
        return "/workplan/taskview";
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
        AccessVerifier.verifyAccess(Module.SCHEDULE, Action.VIEW, objectID);
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
     * @throws Exception if any error occurs.
     */
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = SessionManager.getUser();
        setRedirect(true);

        // Properties are stored in the _global_ scope which is irrespective
        // of the current space
        PersonProperty property = new PersonProperty();
        property.setScope(new PersonPropertyGlobalScope(user));

        String[] properties = new String[] {"unassignedTasks", "tasksComingDue",
            "overallocatedResources", "completedTasks", "lateTasks",
            "taskDependenciesExist", "isDateConstrained", "isCriticalPath",
            "afterDeadline", "isExternalTask", "hasAssignment"};

        for (int i = 0; i < properties.length; i++) {
            String propertyName = properties[i];
            String propertyNameValue = request.getParameter(propertyName);

            if (Validator.isBlankOrNull(propertyNameValue) || !Boolean.valueOf(propertyNameValue).booleanValue()) {
                property.removeAllValues("prm.schedule.tasklist", propertyName+"Image");
                property.removeAllValues("prm.schedule.tasklist", propertyName+"Color");
            } else {
                String propertyImageValue = request.getParameter(propertyName+"Image");
                String propertyColorString = request.getParameter(propertyName+"Color");
                String propertyColorValue = (Validator.isBlankOrNull(propertyColorString) ?
                        HTMLBackgroundColor.NO_COLOR.getHTMLClass() :
                        HTMLBackgroundColor.getByID(Integer.parseInt(propertyColorString)).getHTMLClass());

                property.removeAllValues("prm.schedule.tasklist", propertyName+"Image");
                if (!Validator.isBlankOrNull(propertyImageValue)) {
                    property.put("prm.schedule.tasklist", propertyName+"Image", propertyImageValue);
                }
                property.removeAllValues("prm.schedule.tasklist", propertyName+"Color");
                if (!Validator.isBlankOrNull(propertyColorValue)) {
                    property.put("prm.schedule.tasklist", propertyName+"Color", propertyColorValue);
                }
            }
        }

        HashMap model = new HashMap();
        model.put("module", String.valueOf(Module.SCHEDULE));
        model.put("action", String.valueOf(Action.VIEW));

        return model;
    }
}
