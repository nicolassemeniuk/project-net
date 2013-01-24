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
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.schedule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import net.project.resource.Roster;
import net.project.resource.RosterBean;
import net.project.resource.ScheduleEntryAssignment;
import net.project.security.User;
import net.project.util.DateFormat;

/**
 * Provides a helper class for displaying formatted dates
 * for a Schedule Entry.
 * <p>
 * A Schedule Entry's start and end dates are affected by the
 * schedule working time calendar and the individual resources assigned
 * to it.  Resources may have different time zones also.
 * This helper provides the start and end dates formatted according
 * to each resource's time zone to make it clear exactly when
 * a task is started and stopped.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class ScheduleEntryDateHelper {

    private final User user;
    private final Roster roster;
    private final ScheduleEntry scheduleEntry;
    private final TimeZone scheduleDefaultTimeZone;

    /**
     * Creates a new ScheduleEntryDateHelper for the specified schedule entry ID.
     * @param request the request that provides the session from which to get the
     * current user, schedule and roster
     * @param scheduleEntryID the ID of the schedule entry to provide dates for
     * @throws IllegalStateException if no user or schedule attributes are found in
     * the session
     * @throws IllegalArgumentException if the specified scheduleEntryID cannot be
     * found in the current schedule
     */
    public ScheduleEntryDateHelper(HttpServletRequest request, String scheduleEntryID) {
        this.roster = RosterBean.getFromSession(request.getSession());

        this.user = (User) request.getSession().getAttribute("user");
        if (this.user == null) {
            throw new IllegalStateException("Missing session attribute 'user'");
        }

        Schedule schedule = (Schedule) request.getSession().getAttribute("schedule");
        if (schedule == null) {
            throw new IllegalStateException("Missing session attribute 'schedule'");
        }

        // Grab the schedule entry for the ID
        this.scheduleEntry = (ScheduleEntry) schedule.getEntryMap().get(scheduleEntryID);
        if (this.scheduleEntry == null) {
            throw new IllegalArgumentException("No schedule entry found for id " + scheduleEntryID);
        }

        this.scheduleDefaultTimeZone = schedule.getWorkingTimeCalendarProvider().getDefaultTimeZone();
    }

    /**
     * Returns the start date time and end date time range for the current schedule entry
     * formatted for the schedule's default time zone and the current user's locale.
     * @return the task datetime range
     */
    public IDateRange getScheduleEntryDateTimeRange() {
        return new ScheduleEntryDateRange(this.scheduleEntry, this.scheduleDefaultTimeZone);
    }

    /**
     * Returns the start date time and end date time range for the current schedule entry
     * formatted for the current user's locale and time zone.
     * @return the task datetime range
     */
    public IDateRange getScheduleEntryDateTimeRangeForUser() {
        return new ScheduleEntryDateRangeUser(this.scheduleEntry);
    }

    /**
     * Indicates whether we should display the task times using the current user's
     * time zone.
     * <p>
     * We do this if the user's time zone is different (has different rules; it may
     * not be the exact same ID) from the schedule default time zone
     * and the user is not assigned to the task (and hence won't be listed
     * as a resource time range).
     * </p>
     * @return true if we should display the task times in the user's time zone;
     * false if we don't need to
     */
    public boolean isUserTimeZoneDisplayRequired() {
        return (!this.user.getTimeZone().hasSameRules(scheduleDefaultTimeZone) && !isUserAssigned());
    }

    /**
     * Indicates whether the current user is assigned as a resource.
     * @return true if they are assigned; false otherwise.
     */
    private boolean isUserAssigned() {
        boolean isAssigned = false;
        for (Iterator it = this.scheduleEntry.getAssignments().iterator(); it.hasNext() && !isAssigned;) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) it.next();
            if (nextAssignment.getPersonID().equals(this.user.getID())) {
                isAssigned = true;
            }
        }
        return isAssigned;
    }

    /**
     * Indicates whether the schedule entry has at lease one assignment.
     * @return true if the schedule entry has at least one assignment; false
     * if there are zero assignments
     */
    public boolean hasAssignments() {
        return (this.scheduleEntry.getAssignments().size() > 0);
    }

    /**
     * Returns the start date time and end date time ranges for the current schedule entry's
     * resource assignments.
     * @return an unmodifiable collection where each element is a <code>IDateRange</code>.
     */
    public Collection getAssignmentDateTimeRanges() {
        Collection dateRanges = new ArrayList();

        for (Iterator it = this.scheduleEntry.getAssignments().iterator(); it.hasNext();) {
            ScheduleEntryAssignment nextAssignment = (ScheduleEntryAssignment) it.next();
            dateRanges.add(new ResourceDateRange(nextAssignment, this.scheduleDefaultTimeZone));
        }

        return Collections.unmodifiableCollection(dateRanges);
    }

    //
    // Nested top-level classes
    //

    /**
     * Provides a formatted start date time and end date time.
     */
    public interface IDateRange {

        /**
         * Formats the start date and time.
         * @return the formatted start date and time
         */
        String formatStartDateTime();

        /**
         * Formats the end date and time.
         * @return the formatted end date and time
         */
        String formatEndDateTime();

    }

    /**
     * Provides a resource name in addition to a formatted start
     * date time and end date time.
     */
    public interface IResourceDateRange extends IDateRange {

        /**
         * Returns the resource's display name.
         * @return the resource's display name
         */
        String getDisplayName();

    }

    /**
     * Provides common functionality for formatting a date range.
     */
    private abstract class DateRange implements IDateRange {

        /** The start datetime. */
        private final Date startTime;
        /** The end datetime. */
        private final Date endTime;
        /** The time zone to use when formatting the date range. */
        private final TimeZone timeZone;

        /**
         * Creates a new DateRange for the specified start and end date times
         * and time zone.
         * @param startTime the start date time of the date range
         * @param endTime the end date time of the date range
         * @param timeZone the time zone to use when formatting the times
         */
        private DateRange(Date startTime, Date endTime, TimeZone timeZone) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.timeZone = timeZone;
        }

        public String formatStartDateTime() {
            return formatDateTime(startTime, timeZone);
        }

        public String formatEndDateTime() {
            return formatDateTime(endTime, timeZone);
        }

        /**
         * Formats the specified date time using the current user's locale
         * and the specified time zone.
         * @param dateTime the date time to format
         * @param timeZone the time zone to use when formatting
         * @return formats the date time to display a date and time
         */
        private String formatDateTime(Date dateTime, TimeZone timeZone) {
            DateFormat df = new DateFormat(ScheduleEntryDateHelper.this.user);
            return df.formatDateTime(dateTime, timeZone);
        }

    }

    /**
     * Provides a date range for a schedule entry with date times
     * formatted for the schedule default time zone.
     */
    private class ScheduleEntryDateRange extends DateRange {

        /**
         * Creates a new SchedulEntryDateRange for the specified schedule entry
         * formatting date times using the specified time zone.
         * @param entry the schedule entry who's start and end times to format
         * @param scheduleDefaultTimeZone the schedule default time zone to
         * use when formatting the date times
         */
        private ScheduleEntryDateRange(ScheduleEntry entry, TimeZone scheduleDefaultTimeZone) {
            super(entry.getStartTime(), entry.getEndTime(), scheduleDefaultTimeZone);
        }

    }

    /**
     * Provides a date range for a schedule entry with date times
     * formatted for the current user's time zone.
     */
    private class ScheduleEntryDateRangeUser extends DateRange {

        private ScheduleEntryDateRangeUser(ScheduleEntry entry) {
            super(entry.getStartTime(), entry.getEndTime(), ScheduleEntryDateHelper.this.user.getTimeZone());
        }

    }

    /**
     * Provides a date range for a resource.
     */
    private class ResourceDateRange extends DateRange implements IResourceDateRange {

        /** The ID of the resource. */
        private final String personID;

        /**
         * Creates a new ResourceDateRange for the specified assignment.
         * <p>
         * The the resource has no time zone (for example, they might be an unregistered user)
         * then the specified schedule defailt time zone is used).
         * </p>
         * @param assignment the assignment who's start and end times to format
         * using the assignment's resource's time zone.
         * @param scheduleDefaultTimeZone the schedule default time zone to use if the
         * resource has none
         */
        private ResourceDateRange(ScheduleEntryAssignment assignment, TimeZone scheduleDefaultTimeZone) {
            super(assignment.getStartTime(), assignment.getEndTime(), (assignment.getTimeZone() != null ? assignment.getTimeZone() : scheduleDefaultTimeZone));
            this.personID = assignment.getPersonID();
        }

        public String getDisplayName() {
            return ScheduleEntryDateHelper.this.roster.getAnyPerson(this.personID).getDisplayName();
        }

    }

}
