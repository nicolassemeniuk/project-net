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

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.RecordStatus;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.resource.Timesheet;
import net.project.resource.TimesheetFinder;
import net.project.resource.TimesheetStatus;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.ErrorReporter;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.util.Validator;

/**
 * Handles the data loading and security checks for the CreateTimesheet.jsp
 * page used for creating a timesheet for task selected.
 *
 * @author Sachin Mittal
 * @since Version 8.2.0
 */
public class CreateTimesheetHandler extends UpdateAssignmentsHandler {

	
    public CreateTimesheetHandler(HttpServletRequest request) {
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
        return "/resource/CreateTimesheet.jsp";
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
	    Map model = super.handleRequest(request, response);

	    //get the timesheet header details
	    model.put("id", "");

        Timesheet timesheet = getTimesheet(request, model);

        model.put("timesheet", timesheet);

	    //If we are returning here from an error reporting page, there might be
	    //an error object in scope
	    passThru(model, "errorReporter");

	    //The page will render differently depending on whether we are creating
	    //or editing.  We pass through action so the page knows how to render
	    passThru(model, "action");

	    return model;
	}
	
    private Timesheet getTimesheet(HttpServletRequest request, Map model) throws PersistenceException, SQLException {
        Timesheet timesheet = (Timesheet) request.getSession().getAttribute("timsheet");
        User user = SessionManager.getUser();
        //If we haven't found a timsheet, we will need to create one.
        if (timesheet == null) {
            timesheet = new Timesheet(); 
            request.getSession().setAttribute("timesheet", timesheet);
        } else {
            timesheet.clear();
        }
        
        ErrorReporter errorReporter = (ErrorReporter) model.get("errorReporter");
        String action = request.getParameter("action");
        String timesheetId = request.getParameter("timesheetId");
        
        if(Validator.isBlankOrNull(action) || !Validator.isNumeric(action)) {
            
            errorReporter.addError(PropertyProvider.get("prm.security.servletsecurity.accessdenied.invalidaction.message"));
            return timesheet;
        }
        
        if(Action.MODIFY ==  Integer.parseInt(action)) {
            
            if(Validator.isBlankOrNull(timesheetId) || !Validator.isNumeric(timesheetId)) {
                errorReporter.addError(PropertyProvider.get("prm.resource.timesheet.notfound.error"));
                
            } else {
                TimesheetFinder timesheetFinder = new TimesheetFinder();
                boolean timesheetFound = timesheetFinder.findByID(timesheetId, timesheet);
                
                if(timesheetFound) {
                    //get the total work logged till now
                    Map summaryDateValues = (Map) model.get("summaryDateValues");
                    Iterator timeQuantityValues = summaryDateValues.values().iterator();
                    TimeQuantity totalWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);
                    while(timeQuantityValues.hasNext()) {
                        TimeQuantity timeQuantity = (TimeQuantity) timeQuantityValues.next();
                        totalWork.add(timeQuantity);
                    }
                    timesheet.setWork(totalWork);

                    //get the assignments
                    List assignments = (List) model.get("assignments");
                    timesheet.setAssignmentList(assignments);
                    if(timesheet.getTimesheetStatus().equals(TimesheetStatus.CANCELLED) || timesheet.getTimesheetStatus().equals(TimesheetStatus.REJECTED)) {
                        List list = Arrays.asList(new String[]{user.getDisplayName(), timesheet.getStartDateString(), timesheet.getTimesheetStatus().getDisplayName()});
                        errorReporter.addError(PropertyProvider.get("prm.resource.timesheet.error.foundandnotmodify", list));
                        model.put("readOnly", new Boolean(true));
                    }
                } else {
                    errorReporter.addError(PropertyProvider.get("prm.resource.timesheet.notfound.error"));
                }
            }
            
        } else if(Action.CREATE ==  Integer.parseInt(action)) {
            List dateLongNames = (List) model.get("dateLongNames");

            // Set up default values
            timesheet.setStartDate(new Date(Long.parseLong((String)dateLongNames.get(0))));
            timesheet.setEndDate(new Date(Long.parseLong((String)dateLongNames.get(dateLongNames.size() - 1))));

            timesheet.setPersonId(user.getID());
            timesheet.setTimesheetStatus(TimesheetStatus.DRAFT);
            timesheet.setRecordStatus(RecordStatus.ACTIVE);

            //get the total work logged till now
            Map summaryDateValues = (Map) model.get("summaryDateValues");
            Iterator timeQuantityValues = summaryDateValues.values().iterator();
            TimeQuantity totalWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);
            while(timeQuantityValues.hasNext()) {
                TimeQuantity timeQuantity = (TimeQuantity) timeQuantityValues.next();
                totalWork.add(timeQuantity);
            }
            timesheet.setWork(totalWork);

            //get the assignments
            List assignments = (List) model.get("assignments");
            timesheet.setAssignmentList(assignments);
            
            //now check if any timesheet is active for the date range we plan to create
            TimesheetFinder finder = new TimesheetFinder();
            List timesheetsFound = finder.findActiveTimesheetByPersonIdAndDate(timesheet.getPersonId(), timesheet.getStartDate());
            if(timesheetsFound.size() > 0) {
                errorReporter.addError(PropertyProvider.get("prm.resource.timesheet.error.foundandnotcreate" , user.getDisplayName(), timesheet.getStartDateString()));
                model.put("readOnly", new Boolean(true));
            }
            
        } else {
            errorReporter.addError(PropertyProvider.get("prm.security.servletsecurity.accessdenied.invalidaction.message"));
            
        }
        
        return timesheet;
    }

}
