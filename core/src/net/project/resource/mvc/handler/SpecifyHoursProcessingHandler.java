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

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.security.AuthorizationFailedException;
import net.project.util.DateFormat;
import net.project.util.DateRange;
import net.project.util.ErrorReporter;
import net.project.util.HTMLUtils;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.util.Validator;

/**
 * This class makes sure that the user has entered the hour ranges correctly.
 * As long as they have, they are passed through to the update assignment page
 * to be stored in hidden fields until the user has submitted the page.
 *
 * If the hours were not entered correctly, the "Hours Worked On" page will
 * remain visible, and the user will be asked to fix their errors.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class SpecifyHoursProcessingHandler extends Handler {
    private static String ERROR_VIEW = "/servlet/AssignmentController/CurrentAssignments/SpecifyHours";
    private static String SUCCESSFUL_VIEW = "/resource/UpdateAssignmentHoursProcessing.jsp";
    private String viewName = SUCCESSFUL_VIEW;

    public SpecifyHoursProcessingHandler(HttpServletRequest request) {
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
        return viewName;
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
        List timeRanges = new LinkedList();
        
        //Parse the times that the user has entered and Figure out the total amount of work given the hours entered 
        TimeQuantity totalHoursWorked = parseTimeRanges(request, errors, timeRanges);
        model.put("timeRanges", timeRanges);
        model.put("totalHoursWorked", NumberFormat.getInstance().formatNumber(totalHoursWorked.getAmount().doubleValue()));


        if (errors.errorsFound()) {
            model.put("errorReporter", errors);
            viewName = ERROR_VIEW;
            passThruAll(model);
        } else {
            passThru(model, "dateID");
            passThru(model, "objectID");
            passThru(model, "showFilterPane");
        }

        return model;
    }

    private TimeQuantity parseTimeRanges(HttpServletRequest request, ErrorReporter errors, List timeRanges) {
        List dateRanges = new LinkedList();
        String[][] timeInfo = new String[4][3];
        
        //Parse the time values
        int year, month, date;
        Date dateID = new Date(Long.parseLong(request.getParameter("dateID")));
        PnCalendar cal = new PnCalendar();
        cal.setTime(dateID);
        year = cal.get(PnCalendar.YEAR);
        month = cal.get(PnCalendar.MONTH);
        date = cal.get(PnCalendar.DATE);
        DateFormat df = DateFormat.getInstance();
        Date midnight = cal.getMidnight();
        String errorExample = df.formatTime(midnight, java.text.DateFormat.SHORT);


        //Get the previous time log entries from request
        List previousLogs = new LinkedList();
        String[] valuesStart = request.getParameterValues("previousTimeSpanStart");
        String[] valuesEnd = request.getParameterValues("previousTimeSpanEnd");
        for (int i = 0; valuesStart != null && i < valuesStart.length; i++) {
            String startText = fixupTimeText(valuesStart[i]);
            String endText = fixupTimeText(valuesEnd[i]);
            Date startDate = df.parseTime(startText, java.text.DateFormat.SHORT);
            Date endDate = df.parseTime(endText, java.text.DateFormat.SHORT);
            
            cal.setTime(startDate);
            cal.set(PnCalendar.YEAR, year);
            cal.set(PnCalendar.MONTH, month);
            cal.set(PnCalendar.DATE,  date);
            startDate = cal.getTime();

            cal.setTime(endDate);
            cal.set(PnCalendar.YEAR, year);
            cal.set(PnCalendar.MONTH, month);
            cal.set(PnCalendar.DATE,  date);
            endDate = cal.getTime();
            
            DateRange dateRange = new DateRange(startDate, endDate);
            previousLogs.add(dateRange);
        }
        
        //First, get the string values
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            if (!paramName.startsWith("timeSpan")) {
                continue;
            }

            int row, col;
            row = Integer.parseInt(paramName.substring(8, 9))-1;
            col = (paramName.substring(9).equals("Start") ? 0 : paramName.substring(9).equals("End") ? 1 : 2);

            timeInfo[row][col] = request.getParameter(paramName);
        }

        for (int i = 0; i < 4; i++) {
            if (!Validator.isBlankOrNull(timeInfo[i][0]) || !Validator.isBlankOrNull(timeInfo[i][1])) {
                String startText = fixupTimeText(timeInfo[i][0]);
                String endText = fixupTimeText(timeInfo[i][1]);
                String comments = timeInfo[i][2];

                Date startDate = df.parseTime(startText, java.text.DateFormat.SHORT);
                Date endDate = df.parseTime(endText, java.text.DateFormat.SHORT);

                if (startDate == null) {
                    errors.addError(PropertyProvider.get("prm.assignments.update.timeparseerror.message", startText, errorExample));
                    continue;
                }
                
                if (endDate == null) {
                    errors.addError(PropertyProvider.get("prm.assignments.update.timeparseerror.message", endText, errorExample));
                    continue;
                }

                cal.setTime(startDate);
                cal.set(PnCalendar.YEAR, year);
                cal.set(PnCalendar.MONTH, month);
                cal.set(PnCalendar.DATE,  date);
                startDate = cal.getTime();

                cal.setTime(endDate);
                cal.set(PnCalendar.YEAR, year);
                cal.set(PnCalendar.MONTH, month);
                cal.set(PnCalendar.DATE,  date);
                endDate = cal.getTime();

                DateRange dateRange = new DateRange(startDate, endDate);
                if(startDate.compareTo(endDate) >=0 ) {
                    errors.addError(PropertyProvider.get("prm.schedule.workingtime.editdate.endtimeafterstart.message"));
                    continue;
                }
                LogEntry entry = new LogEntry(String.valueOf(startDate.getTime()), String.valueOf(endDate.getTime()), HTMLUtils.jsEscape(comments));
                //check if this entry falls under any previous times
                //ie start and end time of this entry should not fall between start and end of any previous entries
                Iterator iter = previousLogs.iterator();
                boolean flag = true;
                while(iter.hasNext()) {
                    DateRange previousEntry = (DateRange) iter.next();
                    boolean startDateInRange = (startDate.compareTo(previousEntry.getRangeEnd()) < 0);
                    boolean endDateInRange = (endDate.compareTo(previousEntry.getRangeStart()) > 0);
                    if (startDateInRange && endDateInRange) {
                        errors.addError(PropertyProvider.get("prm.assignments.update.timerangeerror.message", dateRange.toString(), previousEntry.toString()));
                        flag = false;
                        break;
                    }
                }
                if(flag) {
                    dateRanges.add(dateRange);
                    timeRanges.add(entry);
                }
            }
        }

        return findTotalHoursWorked(dateRanges);
    }

    private String fixupTimeText(String timeText) {
        timeText = timeText.toLowerCase();

        if (timeText.endsWith("am") && !timeText.endsWith(" am")) {
            timeText = timeText.substring(0, timeText.length()-2) + " am";
        }
        if (timeText.endsWith("pm") && !timeText.endsWith(" pm")) {
            timeText = timeText.substring(0, timeText.length()-2) + " pm";
        }

        return timeText;
    }

    /**
     * Using the time ranges that have already been found, figure out the total
     * amount of work that the user has worked.  We will use this to populate
     * the work field for that day on the main "Update Assignment" page.
     *
     * @param dateRanges
     * @return
     */
    private TimeQuantity findTotalHoursWorked(List dateRanges) {
        TimeQuantity totalWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);

        for (Iterator it = dateRanges.iterator(); it.hasNext();) {
            DateRange dateRange = (DateRange)it.next();
            totalWork = totalWork.add(dateRange.getTimeQuantity(TimeQuantityUnit.HOUR, 2));
        }

        return totalWork;
    }
}
