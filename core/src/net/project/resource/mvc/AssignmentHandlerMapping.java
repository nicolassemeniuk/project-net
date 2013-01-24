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
package net.project.resource.mvc;

import javax.servlet.http.HttpServletRequest;

import net.project.base.mvc.ControllerException;
import net.project.base.mvc.Handler;
import net.project.base.mvc.HandlerMapping;
import net.project.resource.mvc.handler.AssignmentViewHandler;
import net.project.resource.mvc.handler.CancelTimesheetProcessingHandler;
import net.project.resource.mvc.handler.CreateTimesheetHandler;
import net.project.resource.mvc.handler.CreateTimesheetProcessingHandler;
import net.project.resource.mvc.handler.PercentCompleteChangedHandler;
import net.project.resource.mvc.handler.PersonalAssignmentsFilterHandler;
import net.project.resource.mvc.handler.PersonalAssignmentsHandler;
import net.project.resource.mvc.handler.SpecifyHoursHandler;
import net.project.resource.mvc.handler.SpecifyHoursProcessingHandler;
import net.project.resource.mvc.handler.SubmitTimesheetProcessingHandler;
import net.project.resource.mvc.handler.UpdateAssignmentsHandler;
import net.project.resource.mvc.handler.UpdateAssignmentsProcessingHandler;
import net.project.resource.mvc.handler.WorkChangeHandler;
import net.project.util.Validator;

/**
 * This class is responsible for finding the correct handler for any request
 * that has been submitted to the AssignmentController.
 *
 * @author Matthew Flower
 * @since Version 7.6
 */
public class AssignmentHandlerMapping extends HandlerMapping {
    protected Handler doGetHandler(HttpServletRequest request) throws ControllerException {
        //Get the path information minus the first slash.
        String pathInfo = request.getPathInfo();
        if (Validator.isBlankOrNull(pathInfo)) {
            pathInfo = request.getParameter("handlerName");
        }
        if (Validator.isBlankOrNull(pathInfo)) {
            pathInfo = request.getParameter("theAction");
        }

        if (pathInfo.equalsIgnoreCase("/PersonalAssignments")) {
            return new PersonalAssignmentsHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/PersonalAssignments/Filter")) {
            return new PersonalAssignmentsFilterHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/CurrentAssignments/Update")) {
            return new UpdateAssignmentsHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/CurrentAssignments/SpecifyHours")) {
            return new SpecifyHoursHandler(request);            
        } else if (pathInfo.equalsIgnoreCase("/CurrentAssignments/SpecifyHoursProcessing")) {
            return new SpecifyHoursProcessingHandler(request);            
        } else if (pathInfo.equalsIgnoreCase("/CurrentAssignments/UpdateProcessing")) {
            return new UpdateAssignmentsProcessingHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/CurrentAssignments/WorkChanged")) {
            return new WorkChangeHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/CurrentAssignments/PCChanged")) {
            return new PercentCompleteChangedHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/View")) {
            return new AssignmentViewHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/CurrentAssignments/CreateTimesheet")) {
            return new CreateTimesheetHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/CurrentAssignments/CreateTimesheetProcessing")) {
            return new CreateTimesheetProcessingHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/CurrentAssignments/CancelTimesheetProcessing")) {
            return new CancelTimesheetProcessingHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/CurrentAssignments/SubmitTimesheetProcessing")) {
            return new SubmitTimesheetProcessingHandler(request);
        } else if (pathInfo.equalsIgnoreCase("/CurrentAssignments/UpdateTimesheetProcessing")) {
            return new CreateTimesheetProcessingHandler(request);
        } else {
            throw new ControllerException("Unrecognized assignments module: " +pathInfo);
        }

    }
}
