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

import javax.servlet.http.HttpServletRequest;

import net.project.resource.Timesheet;
import net.project.resource.TimesheetStatus;

/**
 * Handles the data loading and security checks for the CreateTimesheet.jsp
 * page used for creating a timesheet for task selected.
 *
 * @author Sachin Mittal
 * @since Version 8.2.0
 */
public class SubmitTimesheetProcessingHandler extends CreateTimesheetProcessingHandler {
    
    public SubmitTimesheetProcessingHandler(HttpServletRequest request) {
        super(request);
    }
    
    /**
     * @see CreateTimesheetProcessingHandler#setTimesheetStatus(Timesheet, TimesheetStatus)
     */
    protected void setTimesheetStatus(Timesheet timesheet) {
        timesheet.setTimesheetStatus(TimesheetStatus.SUBMITTED);
    }

}
