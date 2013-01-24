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

 package net.project.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import net.project.gui.html.HTMLOptionList;
import net.project.resource.AssignmentRoster;
import net.project.resource.AssignmentStatus;
import net.project.resource.ScheduleEntryAssignment;
import net.project.security.User;
import net.project.util.DateFormat;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantityUnit;

/**
 * Provides a helper for displaying schedule entry assignments.
 * <p>
 * This is a "view" helper; it may contain HTML code.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.6.4
 */
public class AssignmentsHelper {

    private User user;
    private AssignmentRoster assignmentRoster;
    private Map assignmentsMap;
    private TimeZone scheduleDefaultTimeZone;

    /**
     * Creates an empty AssignmentsHelper.
     */
    public AssignmentsHelper() {
        // Do Nothing
    }

    /**
     * Initializes the helper from the request.
     * @param request the request
     * @param assignmentRoster the assignments
     */
    public void init(HttpServletRequest request, AssignmentRoster assignmentRoster, Map assignmentsMap) {

        Schedule schedule = (Schedule) request.getSession().getAttribute("schedule");
        if (schedule == null) {
            throw new IllegalStateException("Missing session attribute 'schedule'");
        }
        this.scheduleDefaultTimeZone = schedule.getTimeZone();

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            throw new IllegalStateException("Missing session attribute 'user'");
        }
        this.user = user;

        this.assignmentRoster = assignmentRoster;
        this.assignmentsMap = assignmentsMap;
    }

    /**
     * Returns a collection of assignment helpers for presenting
     * assignments information.  There is one entry for each person in the roster.
     * @return an unmodifiable collection where each element is an <code>IAssignmentHelper</code>
     */
    public Collection getAssignments() {
        Collection assignmentHelpers = new ArrayList();

        for (Iterator it = assignmentRoster.iterator(); it.hasNext();) {
            AssignmentRoster.Person nextPerson = (AssignmentRoster.Person) it.next();

            // Add the appropriate type of helper depending on whether the
            // person is assigned or not
            ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) assignmentsMap.get(nextPerson.getID());
            if (assignment == null) {
                assignmentHelpers.add(new UnAssignedResource(nextPerson));
            } else {
                assignmentHelpers.add(new AssignedResource(nextPerson, assignment));
            }
        }

        return Collections.unmodifiableCollection(assignmentHelpers);
    }

    /**
     * Provides common methods for assignment helpers, both assigned and unassigned.
     * <p>
     * Note: This class must remain public so that Bluestone can access the
     * methods via its bean getter (despite the fact that the class isn't referenced)
     * </p>>
     */
    public abstract class AssignmentHelper implements IAssignmentHelper {

        final AssignmentRoster.Person rosterPerson;
        final NumberFormat numberFormat;

        private AssignmentHelper(AssignmentRoster.Person rosterPerson) {
            this.rosterPerson = rosterPerson;
            this.numberFormat = new NumberFormat(AssignmentsHelper.this.user);
        }

        public String getPersonID() {
            return rosterPerson.getID();
        }

        public String getDisplayName() {
            return rosterPerson.getDisplayName();
        }

        public String getWorkUnitOptions() {
            // Both assigned and unassigned provide the same option list
            // But with a different unit selection
            return HTMLOptionList.makeHtmlOptionList(Arrays.asList(new TimeQuantityUnit[]{TimeQuantityUnit.HOUR, TimeQuantityUnit.DAY, TimeQuantityUnit.WEEK}), getSelectedWorkUnit());
        }

        /**
         * Returns the TimeQuantityUnit to select in the option list.
         * <p>
         * Assigned resources should return the unit of their current work value.
         * Unassigned resources should return the default unit.
         * </p>
         * @return the selected time quantity unit
         */
        public abstract TimeQuantityUnit getSelectedWorkUnit();

        public String getMaximumAllocation() {
            return numberFormat.formatPercent(rosterPerson.getMaxAllocatedDecimal().doubleValue());
        }

        public String getMaximumAllocationDecimal() {
            return rosterPerson.getMaxAllocatedDecimal().toString();
        }
    }

    /**
     * Provides an assigned resource.
     * <p>
     * Note: This class must remain public so that Bluestone can access the
     * methods via its bean getter (despite the fact that the class isn't referenced)
     * </p>>
     */
    public class AssignedResource extends AssignmentHelper implements IAssignmentHelper {

        private final ScheduleEntryAssignment assignment;
        private final TimeZone timeZone;

        /**
         * Creates an assigned resource for the specified person and assignment.
         * @param rosterPerson the person who is assigned
         * @param assignment the assignment for that person
         */
        private AssignedResource(AssignmentRoster.Person rosterPerson, ScheduleEntryAssignment assignment) {
            super(rosterPerson);
            this.assignment = assignment;
            this.timeZone = (assignment.getTimeZone() != null ? assignment.getTimeZone() : AssignmentsHelper.this.scheduleDefaultTimeZone);
        }

        /**
         * Returns true always.
         * @return true
         */
        public boolean isAssigned() {
            return true;
        }

        /**
         * Returns <code>checked</code> always.
         * @return <code>checked</code>
         */
        public String getIsAssignedCheckedAttribute() {
            return "checked";
        }

        public String getPercentAssigned() {
            // Format as a number so as not to ge a % assign
            // this value is required for update
            return numberFormat.formatNumber(assignment.getPercentAssignedInt());
        }

        public String getWorkAmountFormatted() {
            return assignment.getWork().formatAmount();
        }

        /**
         * Returns the current unit of this assignment's work.
         * @return the current work unit
         */
        public TimeQuantityUnit getSelectedWorkUnit() {
            return assignment.getWork().getUnits();
        }

        public String getWorkComplete() {
            return assignment.getWorkComplete().toShortString(0,2);
        }
        
        public String getWorkCompleteInHours() {
            return assignment.getWorkComplete().convertTo(TimeQuantityUnit.HOUR).formatAmount();
        }

        public String getResourceAllocationTime() {
            return String.valueOf((assignment.getStartTime() == null ? new Date() : assignment.getStartTime()).getTime());
        }

        public String getIsOwnerCheckedAttribute() {
            return (assignment.isPrimaryOwner() ? "checked" : "");
        }

        public String getAssignmentStatusOptions() {
            return HTMLOptionList.makeHtmlOptionList(AssignmentStatus.getValidStatuses(), assignment.getStatus());
        }

        public String getRole() {
            return (assignment.getPersonRole() == null ? "" : assignment.getPersonRole());
        }

        public String getStartDateTime() {
            if (assignment.getStartTime() != null) {
                return formatDateTime(assignment.getStartTime(), timeZone);
            } else {
                return "";
            }
        }

        public String getEndDateTime() {
            if (assignment.getEndTime() != null) {
                return formatDateTime(assignment.getEndTime(), timeZone);
            } else {
                return "";
            }
        }

        public boolean isUserTimeZoneDisplayRequired() {
            return (!AssignmentsHelper.this.user.getTimeZone().hasSameRules(timeZone));
        }

        public String getStartDateTimeForUser() {
            return formatDateTime(assignment.getStartTime(), AssignmentsHelper.this.user.getTimeZone());
        }

        public String getEndDateTimeForUser() {
            return formatDateTime(assignment.getEndTime(), AssignmentsHelper.this.user.getTimeZone());
        }

        /**
         * Formats the specified date time using the current user's locale
         * and the specified time zone.
         * @param dateTime the date time to format
         * @param timeZone the time zone to use when formatting
         * @return formats the date time to display a date and time
         */
        private String formatDateTime(Date dateTime, TimeZone timeZone) {
            DateFormat df = new DateFormat(AssignmentsHelper.this.user);
            return df.formatDateTime(dateTime, timeZone);
        }

        /**
         * @return the applicable time zone
         */
        public TimeZone getTimeZone() {
            return this.timeZone;
        }

    }

    /**
     * Provides an unassigned resource.
     * <p>
     * Note: This class must remain public so that Bluestone can access the
     * methods via its bean getter (despite the fact that the class isn't referenced)
     * </p>>
     */
    public class UnAssignedResource extends AssignmentHelper implements IAssignmentHelper {

        private final TimeZone timeZone;
        /**
         * Creates a new UnAssignedResource for the specified person.
         * @param rosterPerson the person from the roster
         */
        private UnAssignedResource(AssignmentRoster.Person rosterPerson) {
            super(rosterPerson);
            this.timeZone = (rosterPerson.getTimeZone() != null ? rosterPerson.getTimeZone() : AssignmentsHelper.this.scheduleDefaultTimeZone);
        }

        /**
         * Returns false always.
         * @return false
         */
        public boolean isAssigned() {
            return false;
        }

        public String getIsAssignedCheckedAttribute() {
            return "";
        }

        public String getPercentAssigned() {
            return "";
        }

        public String getWorkAmountFormatted() {
            return "";
        }

        /**
         * Returns the default work unit selection for an unassigned resource.
         * @return {@link TimeQuantityUnit#HOUR} always
         */
        public TimeQuantityUnit getSelectedWorkUnit() {
            return TimeQuantityUnit.HOUR;
        }

        public String getWorkComplete() {
            return rosterPerson.getWorkComplete().toShortString(0,2);
        }
        
        public String getWorkCompleteInHours() {
            return rosterPerson.getWorkComplete().convertTo(TimeQuantityUnit.HOUR).formatAmount();
        }

        public String getResourceAllocationTime() {
            return "";
        }

        public String getIsOwnerCheckedAttribute() {
            return "";
        }

        public String getAssignmentStatusOptions() {
            return HTMLOptionList.makeHtmlOptionList(AssignmentStatus.getValidStatuses());
        }

        public String getRole() {
            return "";
        }

        public String getStartDateTime() {
            return "";
        }

        public String getEndDateTime() {
            return "";
        }

        public boolean isUserTimeZoneDisplayRequired() {
            return false;
        }

        public String getStartDateTimeForUser() {
            return "";
        }

        public String getEndDateTimeForUser() {
            return "";
        }

        public TimeZone getTimeZone() {
            return this.timeZone;
        }
    }

}

//
// Nested top-level classes
//

