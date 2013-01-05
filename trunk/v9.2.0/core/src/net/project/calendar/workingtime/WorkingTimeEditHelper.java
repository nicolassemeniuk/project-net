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

 /*----------------------------------------------------------------------+
|                                                                       
|     $RCSfile$
|    $Revision: 20472 $
|        $Date: 2010-02-26 11:51:26 -0300 (vie, 26 feb 2010) $
|      $Author: nilesh $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.calendar.workingtime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.calendar.PnCalendar;
import net.project.security.User;
import net.project.util.ErrorReporter;

/**
 * Provides a helper class for editing working times.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class WorkingTimeEditHelper {

    /**
     * The current user, required for converting dates to times.
     */
    private final User user;

    /**
     * The working times being edited.
     */
    private final List workingTimes = new ArrayList();

    /**
     * Creates a working time edit helper with blank working times.
     * @param user the user providing locale and time zone settings
     */
    WorkingTimeEditHelper(User user) {
        this(user, Collections.EMPTY_LIST);
    }

    /**
     * Creates a working time edit helper for the specified user, initially
     * editing the specified working times.
     * @param user the user providing locale and time zone settings
     * @param workingTimes a collection where each element is a <code>WorkingTime</code>
     */
    WorkingTimeEditHelper(User user, Collection workingTimes) {
        this.user = user;

        // Add all the working times
        for (Iterator it = workingTimes.iterator(); it.hasNext();) {
            WorkingTime nextWorkingTime = (WorkingTime) it.next();
            this.workingTimes.add(new WorkingTimeEditor(nextWorkingTime));
        }

        // Pad the working times with empty ones
        if (this.workingTimes.size() < 5) {
            for (int i = this.workingTimes.size(); i < 5; i++) {
                this.workingTimes.add(new WorkingTimeEditor());
            }
        }

    }

    /**
     * Returns the start time for the working time at the specified postion
     * converted to a date object.
     * @param position the postion for which to get the working time
     * @return the date representing the start time or null if there is none
     */
    public Date getStartTime(int position) {
        Date startTime;

        WorkingTimeEditor workingTime = (WorkingTimeEditor) this.workingTimes.get(position);
        if (workingTime.getStartTime() != null) {
            startTime = makeDate(workingTime.getStartTime());
        } else {
            startTime = null;
        }

        return startTime;
    }

    /**
     * Sets the start time of the working time at the specified position
     * from the time in the specified date.
     * @param position the position to which to set the start time
     * @param startTime the start time as a date, assumed to be created
     * from the time using the current user's time zone; when null
     * the time is set to null
     */
    public void setStartTime(int position, Date startTime) {
        WorkingTimeEditor workingTime = (WorkingTimeEditor) this.workingTimes.get(position);
        workingTime.setStartTime(makeTime(startTime));
    }

    /**
     * Returns the end time for the working time at the specified postion
     * converted to a date object.
     * @param position the postion for which to get the working time
     * @return the date representing the end time or null if there is none
     */
    public Date getEndTime(int position) {
        Date endTime;
        WorkingTimeEditor workingTime = (WorkingTimeEditor) this.workingTimes.get(position);
        if (workingTime.getEndTime() != null) {
            endTime = makeDate(workingTime.getEndTime());
        } else {
            endTime = null;
        }
        return endTime;
    }

    /**
     * Sets the end time of the working time at the specified position
     * from the time in the specified date.
     * <p>
     * If the date is set to midnight, the time is interpreted as "24:00"
     * meaning "midnight at the end of the day".
     * </p>
     * @param position the position to which to set the end time
     * @param endTime the end time as a date, assumed to be created
     * from the time using the current user's time zone; when null
     * the time is set to null
     */
    public void setEndTime(int position, Date endTime) {
        WorkingTimeEditor workingTime = (WorkingTimeEditor) this.workingTimes.get(position);
        Time time = makeTime(endTime);

        // Special handling for midnight on end times; If set to midnight
        // we actually adjust the Time to be "24:00".  It is not logical
        // for an end time to be "0:00" and our time selection doesn't
        // permit the display of "24:00" (and it can't due to localization issues)
        // Anyhow, this is how MSP works
        if (new Time(0, 0).equals(time)) {
            time = new Time(24, 0);
        }

        workingTime.setEndTime(time);
    }

    /**
     * Makes a date from the specified time using the calendar for
     * the current user.
     * @param time the time from which to make the date
     * @return the date set to today's date modified to
     * reflect the specified time
     */
    private Date makeDate(Time time) {
        Calendar cal = new PnCalendar(this.user);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, time.getHour());
        cal.set(Calendar.MINUTE, time.getMinute());
        return cal.getTime();
    }

    /**
     * Makes a time from the specified date using the calendar for the
     * current user.
     * @param date the date from which to make the time; when null, a
     * null time may be returned
     * @return the time created from the hour and minute components of
     * the date or null
     */
    private Time makeTime(Date date) {
        Time time = null;

        if (date != null) {
            Calendar cal = new PnCalendar(this.user);
            cal.setTime(date);
            time = new Time(cal);
        }

        return time;
    }

    /**
     * Sets the start time and end time for the working time at the
     * specifid position;
     * @param position the position to set
     * @param startTime the start time; may be null
     * @param endTime the end time; may be null
     */
    void setTimes(int position, Time startTime, Time endTime) {
        WorkingTimeEditor workingTime = (WorkingTimeEditor) this.workingTimes.get(position);
        workingTime.setStartTime(startTime);
        workingTime.setEndTime(endTime);
    }

    /**
     * Validates that the working times are valid.
     * If any validation errors occur, at least one error is added to the
     * error reporter.
     * @param errorReporter the error reporter to which to add errors.
     * @param errorMessageProvider a provider of error messages
     */
    void validate(ErrorReporter errorReporter, IErrorMessageProvider errorMessageProvider) {
        // Check all the working time
        boolean isOneFound = false;
        Time lastEndTime = null;
        for (Iterator it = this.workingTimes.iterator(); it.hasNext();) {
            WorkingTimeEditor nextworkingTimeHelper = (WorkingTimeEditor) it.next();

            if (nextworkingTimeHelper.getStartTime() == null && nextworkingTimeHelper.getEndTime() == null) {
                // Both null; ignore this working time

            } else {
                // Not ignoring working time
                isOneFound = true;

                if (nextworkingTimeHelper.getStartTime() == null ||
                    nextworkingTimeHelper.getEndTime() == null) {

                    // Start and end time are required
                    errorReporter.addError(errorMessageProvider.getStartTimeEndTimeRequired());

                } else if (!nextworkingTimeHelper.getEndTime().isAfter(nextworkingTimeHelper.getStartTime())) {
                    // End time must be after start time
                    errorReporter.addError(errorMessageProvider.getEndTimeMustBeAfterStartTime());

                } else if (lastEndTime != null && !nextworkingTimeHelper.getStartTime().isAfter(lastEndTime)) {
                    // Start time of a shift must be after last end time
                    errorReporter.addError(errorMessageProvider.getStartTimeMustBeAfterPreviousEndTime());

                }

                lastEndTime = nextworkingTimeHelper.getEndTime();
            }
        }

        if (!isOneFound) {
            // We need at least one working time
            errorReporter.addError(errorMessageProvider.getWorkingTimeRequired());
        }

    }

    /**
     * Returns an ordered collection of working times representing the current
     * working times for this day.
     * <p>
     * Only fully specified working times are returned.
     * </p>
     * @return a collection where each element is a <code>WorkingTime</code>;
     * the elements are ordered from earliest to latest
     */
    Collection getWorkingTimes() {

        Collection workingTimes = new ArrayList();

        for (Iterator it = this.workingTimes.iterator(); it.hasNext();) {
            WorkingTimeEditor nextHelper = (WorkingTimeEditor) it.next();

            // Only create a working time if one is specified
            // The helper may contain empty working times
            if (nextHelper.isSpecified()) {
                workingTimes.add(nextHelper.makeWorkingTime());
            }
        }

        return workingTimes;
    }

    /**
     * Provides a helper for editing a single working time.
     */
    private static class WorkingTimeEditor {

        /** The start time. */
        private Time startTime;

        /** The end time. */
        private Time endTime;

        /**
         * Creates an empty working time editor.
         */
        private WorkingTimeEditor() {
            // Do nothing
        }

        /**
         * Creates a working time editor editing the specified working time.
         * @param workingTime the working time to edit
         */
        private WorkingTimeEditor(WorkingTime workingTime) {
            this.startTime = workingTime.getStartTime();
            this.endTime = workingTime.getEndTime();
        }

        /**
         * Specifies the start time for this editor, overwriting any current
         * start time.
         * @param startTime the start time or null to blank out the start time
         */
        private void setStartTime(Time startTime) {
            this.startTime = startTime;
        }

        /**
         * Returns the start time or null if there is none.
         * @return the start time
         */
        private Time getStartTime() {
            return this.startTime;
        }

        /**
         * Specifies the end time for this editor, overwriting any current
         * end time.
         * @param endTime the end time or null to blank out the end time
         */
        private void setEndTime(Time endTime) {
            this.endTime = endTime;
        }

        /**
         * Returns the end time or null if there is none.
         * @return the end time
         */
        private Time getEndTime() {
            return this.endTime;
        }

        /**
         * Indicates whether both start and end time are specified.
         * @return true if both start and end time are specified; false
         * otherwise
         */
        private boolean isSpecified() {
            return (getStartTime() != null || getEndTime() != null);
        }

        /**
         * Makes a working time from the start time and end time.
         * @return the working time
         * @throws IllegalStateException if start or end time is not specified
         * @see #isSpecified
         */
        private WorkingTime makeWorkingTime() {
            if (!isSpecified()) {
                throw new IllegalStateException("cannot make working time from an empty helper");
            }

            return new WorkingTime(getStartTime(), getEndTime());
        }
    }

    /**
     * Provides an interface for returning custom error messages when
     * validating working times.
     */
    interface IErrorMessageProvider {

        /**
         * Occurs when a start time or end time is missng.
         * @return the message that indicates a start time and end time
         * is required.
         */
        public String getStartTimeEndTimeRequired();

        /**
         * Occurs when an end time is not after a start time.
         * @return the message that indicates an end time must be after
         * a start time
         */
        public String getEndTimeMustBeAfterStartTime();

        /**
         * Occurs when the start time of a shift is not after the end time
         * of the previous shift.
         * @return the message that indicates a start time must be after
         * the previous end time
         */
        public String getStartTimeMustBeAfterPreviousEndTime();

        /**
         * Occurs when no working times are specified.
         * @return the message that indicates no working times are required
         */
        public String getWorkingTimeRequired();

    }
}
