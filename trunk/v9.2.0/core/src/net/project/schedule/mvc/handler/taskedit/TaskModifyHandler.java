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
|   $Revision: 19949 $
|       $Date: 2009-09-11 14:44:44 -0300 (vie, 11 sep 2009) $
|     $Author: nilesh $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule.mvc.handler.taskedit;
 
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.PnetRuntimeException;
import net.project.base.property.PropertyProvider;
import net.project.resource.AssignmentManager;
import net.project.schedule.ScheduleEntry;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.security.SecurityProvider;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.Validator;

/**
 * Generate the necessary model to render a "task modify" view.
 *
 * @author Matthew Flower
 * @since Version 7.6
 */
public class TaskModifyHandler extends AbstractTaskEditHandler {
    /**
     * Assignment manager for this space.  We store this globally so we don't
     * have to load it twice.
     */
    private AssignmentManager am;

    /**
     * Standard constructor.
     *
     * @param request a <code>HttpServletRequest</code> object that gives the
     * code access to the request parameters.
     */
    public TaskModifyHandler(HttpServletRequest request) {
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
        return super.getViewName();
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

        //Verify that the user didn't try to sidestep the intended access method
        AccessVerifier.verifyAccess(Module.SCHEDULE, Action.VIEW, objectID);

        //We need the assignees for checking security
        am = new AssignmentManager();
        if (!Validator.isBlankOrNull(objectID)) {
            am.setObjectID(objectID);
            am.loadAssigneesForObject();
        }

        //First check to see if the current user is in the assignment list
        User user = (User)getSessionVar("user");

        if (!am.isUserInAssignmentList(user.getID())) {
            SecurityProvider sp = (SecurityProvider)getSessionVar("securityProvider");

            /*
             * Check modify permission if user is not assigned task. A null value is
             * sent as the ObjectId because a role could modify a task if such
             * role had granted some modify access at module level. So, the module
             * access overrides the object access.
             * An exception is thrown if they have no permission.
            */            
            sp.securityCheck(null, Integer.toString(Module.SCHEDULE), Action.MODIFY);
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
        ErrorReporter errors = (ErrorReporter)getSessionVar("errorReporter");
        if(errors == null)
        	errors = new ErrorReporter();
        
        if(errors.errorsFound()) 
        	model.put("errorReporter", errors);
        
        String id = request.getParameter("id");
        passThru(model, "id");
        
        //sjmittal: this is not needed as we store the refLink in hidden parameter
        //and jsp root url is passed in the jsp page
//        String referer = Validator.isBlankOrNull(request.getParameter("referer")) ? "" : "?referer=" + request.getParameter("referer");
        model.put("handler", /*SessionManager.getJSPRootURL() +*/ "/servlet/ScheduleController/TaskEditProcessing/Modify");

        ScheduleEntry scheduleEntry = loadExistingScheduleEntry(request, id);
        if(scheduleEntry == null) {
            ErrorDescription ed = new ErrorDescription(PropertyProvider.get("prm.schedule.error.message.noscheduleentry", id));
            errors.addError(ed);
            model.put("errorReporter", errors);
            throw new PnetRuntimeException(PropertyProvider.get("prm.schedule.error.message.noscheduleentry", id));
        }
        
        super.handleRequest(scheduleEntry, model, request);

        return model;
    }
}
