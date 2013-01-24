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
|    $Revision: 15748 $
|    $Date: 2007-03-01 20:20:09 +0530 (Thu, 01 Mar 2007) $
|    $Author: sjmittal $
|
+-----------------------------------------------------------------------------*/
package net.project.resource.mvc.handler;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.resource.Timesheet;
import net.project.resource.TimesheetStatus;
import net.project.util.ErrorReporter;

/**
 * Handles the data loading and security checks for the CreateTimesheet.jsp
 * page used for creating a timesheet for task selected.
 *
 * @author Sachin Mittal
 * @since Version 8.2.0
 */
public class CancelTimesheetProcessingHandler extends CreateTimesheetProcessingHandler {

    public CancelTimesheetProcessingHandler(HttpServletRequest request) {
        super(request);
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
        errors = new ErrorReporter();
        
        //get the timesheet to store
        Timesheet timesheet = getTimesheet(request);
        if (timesheet != null) {
            setTimesheetStatus(timesheet);
            timesheet.store();
        }
        
        if (errors.errorsFound()) {
            errors.populateFromRequest(request);
            passThruAll(model);
            model.put("errorReporter", errors);
        }
        return model;
    }
    
    /**
     * @see CreateTimesheetProcessingHandler#setTimesheetStatus(Timesheet)
     */
    protected void setTimesheetStatus(Timesheet timesheet) {
        timesheet.setTimesheetStatus(TimesheetStatus.CANCELLED);
    }
}
