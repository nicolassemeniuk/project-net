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
import java.util.Date;
import java.util.Map;

import net.project.base.mvc.AbstractJavaScriptView;
import net.project.base.mvc.ViewException;
import net.project.calendar.TimeBean;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskConstraintType;
import net.project.util.DateFormat;
import net.project.util.ErrorReporter;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantity;

import org.apache.log4j.Logger;

/**
 * Provides a Javscript view that invokes functions after a schedule entry
 * changes.
 * @author Tim Morrow
 * @since Version 7.7
 */
public class ScheduleEntryChangeView extends AbstractJavaScriptView {

    private static final Logger LOGGER = Logger.getLogger(ScheduleEntryChangeView.class);

    private final DateFormat dateFormat;
    private final NumberFormat numberFormat;

    /** The text of the Javascript. */
    private String javascriptText;

    public ScheduleEntryChangeView() {
        this.dateFormat = DateFormat.getInstance();
        this.numberFormat = NumberFormat.getInstance();
    }

    protected void acceptModel(Map model) throws ViewException {
        this.javascriptText = getJavascriptResults(model);
    }

    protected int getContentLength() {
        return this.javascriptText.length();
    }

    protected void writeJavascript(Writer writer) throws IOException {
        LOGGER.debug("View Content:\n" + javascriptText);
        writer.write(javascriptText);
    }

    /**
     * Returns the results of a schedule entry calculation in the form of
     * Javascript functions, suitable for <code>eval</code>.
     * <p>
     * The results contain semi-colon separated function calls as follows:
     * <pre><code>
     * setDuration(<i>durationAmount</i>, <i>durationUnitsID</i>);
     * setWork(<i>workAmount</i>, <i>workUnitsID</i>);
     * setStartDate(<i>dateFormatted</i>);
     * setEndDate(<i>dateFormatted</i>);
     * setConstraint(<i>constraintTypeID</i>, <i>constraintDateFormatted</i>, <i>constraintTimeHourValue</i>, <i>constraintTimeMinuteValue</i>, <i>constraintTimeAMPMValue</i>);
     * </code></pre>
     * </p>
     * <p>
     * If the model contains an ErrorReporter with errors, then the view is
     * not rendered as above, instead only the errors are rendered.
     * </p> 
     * @param model the model from which to get the schedule entry to produce the
     * javascript
     * @return the javascript results
     * @throws ViewException if the attribute <code>scheduleEntry</code> is missing
     * from the model
     */
    private String getJavascriptResults(Map model) throws ViewException {

        ErrorReporter errorReporter = (ErrorReporter) getRequiredModelAttribute("errorReporter", model);
        if (errorReporter.errorsFound()) {
            return AbstractJavaScriptView.getJavaScriptErrors(errorReporter);
        }

        ScheduleEntry scheduleEntry = (ScheduleEntry) model.get("scheduleEntry");
        if (scheduleEntry == null) {
            throw new ViewException("Missing model attribute 'scheduleEntry'");
        }

        String results = "";
        results += "setDuration(" + formatArgs(scheduleEntry.getDurationTQ()) + ");";
        results += "setWork(" + formatArgs(scheduleEntry.getWorkTQ()) + ");";
        results += "setWorkComplete(" + formatArgs(scheduleEntry.getWorkCompleteTQ()) + ");";
        results += "setStartDate(" + formatArgs(scheduleEntry.getStartTime()) + ");";
        results += "setEndDate(" + formatArgs(scheduleEntry.getEndTime()) + ");";
        results += "setConstraint(" + formatArgs(scheduleEntry.getConstraintType(), scheduleEntry.getConstraintDate()) + ");";
        results += "setWorkPercentComplete(\'" + scheduleEntry.getWorkPercentComplete() + "\');";
        return results;
    }

    /**
     * Formats the specified time quantity as arguments.
     * @param timeQuantity the time quantity to format
     * @return the arguments in the form <code>'amount', 'unitsID'</code> where the amount
     * is formatted for the current user's locale
     */
    private String formatArgs(TimeQuantity timeQuantity) {
        return formatJavascriptString(this.numberFormat.formatNumber(timeQuantity.getAmount().doubleValue(), 0, 2)) +
                ", " +
                formatJavascriptString(String.valueOf(timeQuantity.getUnits().getUniqueID()));
    }

    /**
     * Formats the specified date as arguments.
     * @param date the date to format as an argument
     * @return the formatted date in the form <code>'date'</code> where the date is formatted
     * for the current user's locale
     */
    private String formatArgs(Date date) {
        return formatJavascriptString(this.dateFormat.formatDate(date));
    }

    /**
     * Formats the specified constraint and date as arguments.
     * @param constraintType the constraint type
     * @param constraintDate the constraint date
     * @return the arguments in the form <code>'constraintTypeID', 'constraintDateFormatted', 'constraintTimeHourValue', 'constraintTimeMinuteValue', 'constraintTimeAMPMValue'</code>
     * where the date is formatted for the current user's locale and the hour, minute and AMPM values
     * are suitable for setting selection lists formatted by {@link net.project.calendar.TimeBean}
     */
    private String formatArgs(TaskConstraintType constraintType, Date constraintDate) {
        String result = formatJavascriptString(constraintType.getID()) + ", ";
        if (constraintDate == null) {
            result += formatJavascriptString("") + ", " +
                    formatJavascriptString("") + ", " +
                    formatJavascriptString("") + ", " +
                    formatJavascriptString("");
        } else {
            TimeBean timeBean = new TimeBean();
            timeBean.setDate(constraintDate);
            result += formatJavascriptString(this.dateFormat.formatDate(constraintDate)) + ", " +
            formatJavascriptString(String.valueOf(timeBean.getHourSelectionValue())) + ", " +
            formatJavascriptString(String.valueOf(timeBean.getMinuteSelectionValue())) + ", " +
            formatJavascriptString(String.valueOf(timeBean.getAmPmSelectionValue()));
        }
        return result;
    }
}