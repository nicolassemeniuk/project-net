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
|   $Revision: 19068 $
|       $Date: 2009-04-06 10:34:11 -0300 (lun, 06 abr 2009) $
|     $Author: avinash $
|
+-----------------------------------------------------------------------------*/
package net.project.resource.filters.assignments;

import java.math.BigDecimal;
import java.util.Date;

import net.project.base.finder.DateFilter;
import net.project.base.finder.EmptyFinderFilter;
import net.project.calendar.PnCalendar;
import net.project.calendar.workingtime.DateCalculatorHelper;
import net.project.calendar.workingtime.DefinitionBasedWorkingTimeCalendar;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.resource.AssignmentFinder;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Only show Assignments which are "current".  These are assignments who should
 * have started but have not finished.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class CurrentAssignmentsFilter extends EmptyFinderFilter {
    public CurrentAssignmentsFilter(String id) {
        super(id, true);
    }

    /**
     * Get the where clause for an empty finder filter, which should always be
     * empty.
     *
     * @return a <code>String</code> value which should always be empty.
     */
    public String getWhereClause() {
    	String sql = null;
    	if(this.getID().equalsIgnoreCase("currentAssignments")){
	        DateFilter startDateFilter = new DateFilter("startDate", AssignmentFinder.START_DATE_COLUMN, false);
	        User user = SessionManager.getUser();
	        WorkingTimeCalendarDefinition def = WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition();
	        DateCalculatorHelper dateHelper = new DateCalculatorHelper(new DefinitionBasedWorkingTimeCalendar(user.getTimeZone(), def));
	        Date twoDays = dateHelper.calculateDate(new Date(), new TimeQuantity(2, TimeQuantityUnit.DAY), new BigDecimal(1));
	
	        PnCalendar cal = new PnCalendar();
	        cal.setTime(twoDays);
	        startDateFilter.setDateRangeFinish(cal.endOfDay());
	
	        //Allow assignments that match the following criteria:
	        //(not complete && (percentComplete > 0 or startDate < today+3)
	        String isCompleteCol = AssignmentFinder.IS_COMPLETE_COLUMN.getColumnName();
	        String percentCompleteCol = AssignmentFinder.PERCENT_COMPLETE_COLUMN.getColumnName();
	        sql = "(("+isCompleteCol + " = 0) and " +
	            "(("+percentCompleteCol + ">0 or "+startDateFilter.getWhereClause()+") and ("+percentCompleteCol+"<1)))";
    	} else if(this.getID().equalsIgnoreCase("timesheetCurrentAssignments")){
    		String isCompleteCol = AssignmentFinder.IS_COMPLETE_COLUMN.getColumnName();
	        String percentCompleteCol = AssignmentFinder.PERCENT_COMPLETE_COLUMN.getColumnName();
	        sql = "(("+isCompleteCol + " = 0) and " +
	            "(("+percentCompleteCol + ">0 ) and ("+percentCompleteCol+"<1)))";
    	}

        return sql;
    }
}
