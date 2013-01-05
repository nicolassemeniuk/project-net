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
 * Provides a view that constructs Javascript calls when adding, removing or modifying a task assignment.
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
 * @author Tim Morrow
 * @since Version 7.7.0
 */
public class AssignmentChangeView extends AbstractJavaScriptView {

    private static final Logger LOGGER = Logger.getLogger(AssignmentChangeView.class);

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
        AssignmentRoster assignmentRoster = (AssignmentRoster) getRequiredModelAttribute("assignmentRoster", model);
        Map oldAssignmentPercentages = (Map) getRequiredModelAttribute("oldAssignmentPercentages", model);
        Map maxAllocationMap = (Map) getRequiredModelAttribute("maxAllocationMap", model);

        String results = "";
        results += "setTaskDuration(" + formatArgDisplay(scheduleEntry.getDurationTQ(), true)  + ");";
        results += "setTaskWork(" + formatArgDisplay(scheduleEntry.getWorkTQ(), true) + ");";
        results += "setTaskStartDate(" + formatArgDisplay(scheduleEntry.getStartTime()) + ");";
        results += "setTaskEndDate(" + formatArgDisplay(scheduleEntry.getEndTime()) + ");";
        results += "setTaskWorkComplete(" + formatArgDisplay(scheduleEntry.getWorkCompleteTQ(), true) + ");";
        results += "\n" + processAssignments(scheduleEntry, oldAssignmentPercentages, maxAllocationMap, assignmentRoster);

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
    private String processAssignments(ScheduleEntry entry, Map oldAssignmentPercentages, Map maxAllocationMap, AssignmentRoster assignmentRoster) {

        StringBuffer javascript = new StringBuffer();
        boolean overAllocationExists = false;

        // Convert current assignments to a map for easy lookup
        Map newAssignmentsMap = entry.getAssignmentList().getAssignmentMap();

        // We loop over all resources in the roster to set values for
        // both assigned and unassigned resources
        for (Iterator it = assignmentRoster.iterator(); it.hasNext();) {
            AssignmentRoster.Person allocation = (AssignmentRoster.Person) it.next();

            BigDecimal maxAllocationDecimal = (BigDecimal) maxAllocationMap.get(allocation.getID());

            ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) newAssignmentsMap.get(allocation.getID());
            boolean isCurrentlyAssigned = (assignment != null);

            BigDecimal assignmentPercent = (assignment == null ? new BigDecimal("0.00") : assignment.getPercentAssignedDecimal());
            BigDecimal oldAssignmentPercent = (BigDecimal) oldAssignmentPercentages.get(allocation.getID());
            boolean isPreviouslyAssigned = (oldAssignmentPercent != null);

            // Calculate new max percentage value
            BigDecimal newPercent;
            if (isPreviouslyAssigned && !isCurrentlyAssigned) {
                // Change from assigned to unassigned
                newPercent = maxAllocationDecimal.subtract(oldAssignmentPercent);

            } else if (!isPreviouslyAssigned && isCurrentlyAssigned) {
                // Change from unassigned to assigned
                newPercent = maxAllocationDecimal.add(assignmentPercent);

            } else if (isPreviouslyAssigned && isCurrentlyAssigned) {
                // Continues to be assigned
                newPercent = maxAllocationDecimal.add(assignmentPercent).subtract(oldAssignmentPercent);

            } else {
                // Remains unassigned
                newPercent = null;
            }

            // Only register an overallocation if there is actually work assigned to the person
            boolean isAssignmentOverallocated = (assignment != null && newPercent.compareTo(new BigDecimal("1.00")) > 0);
            overAllocationExists |= isAssignmentOverallocated;

            // Now format the function call
            String maxPercentDisplay;
            String maxPercentValue;
            String overallocatedValue;
            String percentAssignedDisplay;
            String workArgs;
            String workCompleteDisplay;

            if (newPercent != null) {
                // We calculated a change in max allocation percentage
                maxPercentDisplay = formatArgDisplay(newPercent);
                maxPercentValue = newPercent.toString();
                overallocatedValue = "\"" + Boolean.valueOf(isAssignmentOverallocated).toString() + "\"";
            } else {
                // No change in max allocation percentage
                maxPercentDisplay = "null";
                maxPercentValue = "null";
                overallocatedValue = "null";
            }

            if (assignment != null) {
                // Currently assigned
                percentAssignedDisplay = formatArgEdit(assignment.getPercentAssignedDecimal());
                workArgs = formatArgEdit(assignment.getWork());
                workCompleteDisplay = formatArgDisplay(assignment.getWorkComplete(), true);
            } else {
                // Currently unassigned
                percentAssignedDisplay = "\"\"";
                workArgs = "\"\", \"\"";
                //sjmittal ajax callback should not reset this
                workCompleteDisplay = formatArgDisplay(allocation.getWorkComplete(), true);
            }

            javascript.append("setAssignmentValues(" +
                    "\"" + allocation.getID() + "\", " +
                    percentAssignedDisplay + ", " +
                    workArgs + "," +
                    workCompleteDisplay + "," +
                    maxPercentDisplay + "," +
                    maxPercentValue + "," +
                    overallocatedValue + ");\n");
        }

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