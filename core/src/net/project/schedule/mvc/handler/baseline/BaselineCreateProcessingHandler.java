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
|   $Revision: 20348 $
|       $Date: 2010-01-29 11:23:05 -0300 (vie, 29 ene 2010) $
|     $Author: nilesh $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule.mvc.handler.baseline;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.schedule.Baseline;
import net.project.schedule.Schedule;
import net.project.security.AccessVerifier;
import net.project.security.Action;
import net.project.security.AuthorizationFailedException;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.Validator;

public class BaselineCreateProcessingHandler extends Handler {
    /** Stores the errors that we find during processing. */
    private ErrorReporter errorReporter = new ErrorReporter();
    
    private String errorViewName;
    
    public BaselineCreateProcessingHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * Gets the name of the view that will be rendered after processing is
     * complete.
     *
     * @return a <code>String</code> containing a name that uniquely identifies
     *         a view that we are going to redirect to after processing the
     *         request.
     */
    public String getViewName() {
        if (errorReporter.errorsFound()) {
          return errorViewName != null ? errorViewName : "/servlet/ScheduleController/Baseline/List";
        } else {
            return "/servlet/ScheduleController/Baseline/List";
        }
    }

    /**
     * Ensure that the requester has proper rights to access this object.  For
     * objects that aren't excluded from security checks, this will just consist
     * of verifying that the parameters that were used to access this page were
     * correct (that is, that the requester didn't try to "spoof it" by using a
     * module and action they have permission to.)
     *
     * @param module the <code>int</code> value representing the module that was
     * passed to security.
     * @param action the <code>int</code> value that was passed through security
     * for the action.
     * @param objectID the <code>String</code> value for objectID that was
     * passed through security.
     * @param request the entire request that was submitted to the schedule
     * controller.
     * @throws net.project.security.AuthorizationFailedException if the user
     * didn't have the proper credentials to view this page, or if they tried to
     * spoof security.
     * @throws net.project.base.PnetException if any other error occurred.
     */
    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {
        if (action == Action.CREATE) {
            errorViewName = "/servlet/ScheduleController/Baseline/Create";
            AccessVerifier.verifyAccess(Module.SCHEDULE, Action.CREATE);
        } else {
            errorViewName = "/servlet/ScheduleController/Baseline/Modify";
            AccessVerifier.verifyAccess(Module.SCHEDULE, Action.MODIFY);
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
     * @throws Exception if any error occurs.
     */
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();

        String baselineID = storeBaseline(model, request);
        // If the baseline is new then add current schedule to baseline. 
        // if old baseline then update baseline only
        if (Validator.isBlankOrNull(request.getParameter("baselineID")) && baselineID != null) {
            addCurrentScheduleToBaseline(baselineID);
        }

        return model;
    }

    private void addCurrentScheduleToBaseline(String baselineID) throws PersistenceException {
        Schedule schedule = (Schedule)getSessionVar("schedule");
        Baseline.baselinePlan(schedule.getID(), baselineID);
    }

    private String storeBaseline(Map model, HttpServletRequest request) throws PersistenceException {
        Baseline baseline = (Baseline)getSessionVar("baseline");
        String defaultForObject = request.getParameter("isDefaultForObject");

        String id = request.getParameter("baselineID");
        if (!Validator.isBlankOrNull(id)) {
            baseline.setID(id);                          
        } 

        Schedule schedule = (Schedule)getSessionVar("schedule");
        baseline.setBaselinedObjectID(schedule.getID());
        
        String name = request.getParameter("name");
        if (!Validator.isBlankOrNull(name)) {
            baseline.setName(name);                        
        } else {
            errorReporter.addError(new ErrorDescription("baseline name", PropertyProvider.get("prm.schedule.baseline.create.name.required.message")));
        }

        baseline.setDescription(request.getParameter("description"));
        baseline.setRecordStatus("A");
        baseline.setDefaultForObject(defaultForObject != null && defaultForObject.equals("true"));
        
        if (errorReporter.errorsFound()) {
            model.put("errorReporter", errorReporter);
            passThru(model, "module");
            passThru(model, "action");
            passThru(model, "isDefaultForObject");
            passThru(model, "baselineID");
            passThru(model, "name");
            passThru(model, "description");
            
            return null;
        } else {
            baseline.store();
            return baseline.getID();
        }

    }
}
