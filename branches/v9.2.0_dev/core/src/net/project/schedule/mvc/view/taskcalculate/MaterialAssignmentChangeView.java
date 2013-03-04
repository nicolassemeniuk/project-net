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

 package net.project.schedule.mvc.view.taskcalculate;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import net.project.base.mvc.AbstractJavaScriptView;
import net.project.base.mvc.ViewException;
import net.project.resource.AssignmentRoster;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.ScheduleEntry;
import net.project.util.DateFormat;
import net.project.util.ErrorReporter;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantity;

import org.apache.log4j.Logger;

/**
 * Provides a view that constructs Javascript calls when adding, removing or modifying a task material assignment.
 * <p>
 * The view includes calls to the following functions:
 * <pre><code>
 * setTaskDuration(<i>durationDisplayValue</i>);
 * setTaskWork(<i>workDisplayValue</i>);
 * setTaskStartDate(<i>startDateDisplayValue</i>);
 * setTaskEndDate(<i>endDateDisplayValue</i>);
 * For each assignment:
 * setAssignmentValues(<i>resourceID</i>, <i>percentageAssigned</i>, <i>workAmount</i>, <i>workUnitsID</i>, <i>workCompleteDisplayValue</i>, <i>maxPercentAssigned</i>, <i>maxPercentAssignedValue</i>);
 * </code></pre>
 * </p>
 */
public class MaterialAssignmentChangeView extends AbstractJavaScriptView {

    private static final Logger LOGGER = Logger.getLogger(MaterialAssignmentChangeView.class);

    private String javascriptText;

    protected void acceptModel(Map model) throws ViewException {
        this.javascriptText = getJavascriptResults(model);
    }

    protected int getContentLength() {
        return this.javascriptText.length();
    }

    protected void writeJavascript(Writer writer) throws IOException {
        writer.write(this.javascriptText);
    }

    private String getJavascriptResults(Map model) throws ViewException {
        ErrorReporter errorReporter = (ErrorReporter) getRequiredModelAttribute("errorReporter", model);
        if (errorReporter.errorsFound()) {
            return AbstractJavaScriptView.getJavaScriptErrors(errorReporter);
        }

        ScheduleEntry scheduleEntry = (ScheduleEntry) getRequiredModelAttribute("scheduleEntry", model);

        String results = "";
        results += "\n" + processAssignments(scheduleEntry);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Assignment Change View Results:\n" + results);
        }

        return results;
    }

    /**
     * Write out all of the DHTML commands to change the values for each assignment.
     * <p>
     * This method includes values for unassigned resources as well as assignmed resources.
     * </p>
     * @param entry the schedule entry from which to get the current assignments
     * @param oldAssignmentPercentages a map where each key is a <code>String</code> resourceID and each
     * value is a <code>BigDecimal</code> representing the percentage decimal for that assignment
     * used to determine the change in percentage allocation for resources that are unassigned
     * @param maxAllocationMap a map where each key is a <code>String</code> resourceID and each
     * value is a <code>BigDecimal</code> containing the current max allocation
     * modification
     * @param assignmentRoster the roster from which to locate all persons; used because assignments
     * may be removed from the schedule entry
     */
    private String processAssignments(ScheduleEntry entry) {

        StringBuffer javascript = new StringBuffer();
        
        //Turn overallocation flag on
        Boolean overAllocationExists = entry.getMaterialAssignments().overAssignationExists();
        javascript.append("overallocationExist(").append(Boolean.valueOf(overAllocationExists).toString()).append(");\n");
        
        return javascript.toString();
    }

    /**
     * Formats the specified time quantity for display purposes as an argument to a
     * javascript function.
     * @param timeQuantity the time quantity to format for display
     * @param isShortString true if the short format is to be used, false for the long format
     * @return the time quantity formatted for the current user's locale, surrounded by double quotes
     */
    private static String formatArgDisplay(TimeQuantity timeQuantity, boolean isShortString) {
        return "\"" + (isShortString ? timeQuantity.toShortString(0, 2) : timeQuantity.toString()) + "\"";
    }

    /**
     * Formats the specified date for display purposes as an argument to a
     * javascript function.
     * @param date the date to format for display
     * @return the formatted date in the form <code>"date"</code> where the date is formatted
     * for the current user's locale
     */
    private static String formatArgDisplay(Date date) {
        return "\"" + DateFormat.getInstance().formatDateTime(date) + "\"";
    }

    /**
     * Formats the specified percentage for edit as an argument to a javascript function.
     * @param percentageDecimal the percentage to display
     * @return the percentage formatted for the user's locale in the form <code>"percentage"</code>
     * where 100 = 100%.
     */
    private static String formatArgDisplay(BigDecimal percentageDecimal) {
        return formatArgEdit(percentageDecimal);
    }

    /**
     * Formats the specified time quantity for edit as an argument to a javascript function.
     * @param timeQuantity the time quantity to be editable
     * @return the timequantity suitable for arguments to a javascript function when it must be editable,
     * for example: <code>"amount", "unitsID"</code>
     */
    private static String formatArgEdit(TimeQuantity timeQuantity) {
        return "\"" + NumberFormat.getInstance().formatNumber(timeQuantity.getAmount().doubleValue(), 0, 2) + "\"" +
                ", " +
                "\"" + timeQuantity.getUnits().getUniqueID() + "\"";
    }


    /**
     * Formats the specified percentage for edit as an argument to a javascript function.
     * @param percentageDecimal the percentage to edit
     * @return the percentage formatted for the user's locale in the form <code>"percentage"</code>
     * where 100 = 100%.
     */
    private static String formatArgEdit(BigDecimal percentageDecimal) {
        return "\"" + NumberFormat.getInstance().formatPercent(percentageDecimal.doubleValue(), 0, 3) + "\"";
    }

}