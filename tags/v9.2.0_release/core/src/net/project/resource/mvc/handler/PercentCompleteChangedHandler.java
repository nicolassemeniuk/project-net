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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.resource.mvc.handler;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.base.mvc.IView;
import net.project.base.property.PropertyProvider;
import net.project.form.assignment.FormAssignment;
import net.project.resource.ActivityAssignment;
import net.project.resource.Assignment;
import net.project.resource.ScheduleEntryAssignment;
import net.project.resource.mvc.view.PercentCompleteChangedView;
import net.project.security.AuthorizationFailedException;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.util.Validator;

public class PercentCompleteChangedHandler extends Handler {

    public PercentCompleteChangedHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * This implementation throws an <code>UnsupportedOperationException</code>
     * always since this class overrides {@link #getView()} to return a view.
     * @return never returns
     * @throws UnsupportedOperationException always
     */
    public String getViewName() {
        throw new UnsupportedOperationException("PercentCompleteChangedHandler cannot return a simple view name.  Use getView() instead.");
    }

    /**
     * Returns a Javascript view for rendering the model.
     * @return the view
     */
    public IView getView() {
        return new PercentCompleteChangedView();
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
        ErrorReporter errors = new ErrorReporter();
        model.put("errors", errors);

        updateWork(request, model, errors);
        passThru(model, "objectID");

        return model;
    }

    private void updateWork(HttpServletRequest request, Map model, ErrorReporter errors) throws ParseException {
        String objectID = request.getParameter("objectID");
        NumberFormat nf = NumberFormat.getInstance();

        //Get the assignment for this work
        Map assignmentMap = (Map)request.getSession().getAttribute("updateAssignmentsMap");
        Assignment assn = (Assignment)assignmentMap.get(objectID);

        //Figure out the total amount of work that has been completed.
        TimeQuantity workComplete = getWorkComplete(assn);

        for (int i = 0; i < 7; i++) {
            String workReported = request.getParameter("wc"+i);
            if (!Validator.isBlankOrNull(workReported)) {
                workComplete = workComplete.add(new TimeQuantity(nf.parseNumber(workReported), TimeQuantityUnit.HOUR));
            }
        }

        if (workComplete.isZero()) {
            errors.addError(PropertyProvider.get("prm.personal.assignments.cannotsetpercentcomplete.message"));
            model.put("percentComplete", nf.formatPercent(getPercentComplete(assn).doubleValue()));
            return;
        }

        //Now find the new total amount of work
        BigDecimal percentComplete=null;
        try {
            // Try to parse as pecent
            percentComplete = new BigDecimal(nf.parsePercent(request.getParameter("pc")).toString());
    	} catch (ParseException ex) {
    	    // Assume had no '%' sign. Parse as number
    	    percentComplete = new BigDecimal(nf.parseNumber(request.getParameter("pc")).toString());
    	    percentComplete = percentComplete.movePointLeft(2);
    	}
        if(!Validator.isInRange(percentComplete.doubleValue(), 0.00001, 1)) {
            errors.addError(new ErrorDescription(PropertyProvider.get("prm.resource.updatework.error.pencentcomplete.range.message")));
            model.put("percentComplete", nf.formatPercent(getPercentComplete(assn).doubleValue()));
            return;
        }

        TimeQuantity work = workComplete.divide(percentComplete, 3, BigDecimal.ROUND_HALF_UP);
        TimeQuantity workRemaining = work.subtract(workComplete);
        
        model.put("work", work);
        model.put("workRemaining", workRemaining);
    }
    
    private TimeQuantity getWorkComplete(Assignment assn) {
        if(assn instanceof ScheduleEntryAssignment)
            return((ScheduleEntryAssignment) assn).getWorkComplete();
        else if(assn instanceof FormAssignment)
            return ((FormAssignment) assn).getWorkComplete();
        else
            return ((ActivityAssignment) assn).getWork();
    }
    
    private BigDecimal getPercentComplete(Assignment assn) {
        if(assn instanceof ScheduleEntryAssignment)
            return((ScheduleEntryAssignment) assn).getPercentComplete();
        else if(assn instanceof FormAssignment)
            return ((FormAssignment) assn).getPercentComplete();
        else
            return new BigDecimal(1.0);
    }
}