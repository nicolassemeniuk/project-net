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
package net.project.schedule;

import java.util.TimeZone;

import net.project.util.TimeQuantityUnit;

/**
 * A helper for rendering assignments.
 * <p>
 * In general, all values are formatted for display purposes; empty strings
 * are returned when no value is available.
 * </p>
 */
public interface IAssignmentHelper {

    /**
     * Returns the ID of the person.
     * @return the person ID
     */
    String getPersonID();

    /**
     * Returns the display name of the person.
     * @return the display name
     */
    String getDisplayName();

    /**
     * Indicates whether the person is assigned.
     * <p>
     * Unassigned persons return emptry string for most values.
     * </p>
     * @return true if they are assigned; false otherwise
     */
    boolean isAssigned();

    /**
     * Returns <code>checked</code> if the current person is assigned.
     * @return <code>checked</code> or empty string if the person is not
     * assigned
     */
    String getIsAssignedCheckedAttribute();

    /**
     * Returns the assignment percentage (where 100 is 100%)
     * formatted for the current user's locale.
     * <p>
     * This value does not contain a % sign so may be used for modification.
     * </p>
     * @return the percentage is assigned or empty string if the
     * person is not assigned
     */
    String getPercentAssigned();

    /**
     * Returns the assignment work amount formatted for display using
     * the current user's locale.
     * @return the formatted assignment work or empty string if the
     * person is not assigned
     */
    String getWorkAmountFormatted();

    /**
     * Returns an HTML option list of the work units available for selection.
     * @return HTML options
     */
    String getWorkUnitOptions();
    
    /**
     * Returns the TimeQuantityUnit to select in the option list.
     * @return the selected time quantity unit
     */
    public TimeQuantityUnit getSelectedWorkUnit();

    /**
     * Returns the number of units of work that the assignee has already
     * completed.
     */
    String getWorkComplete();
    
    /**
     * Returns the number of hours of work that the assignee has already
     * completed.
     */
    String getWorkCompleteInHours();

    /**
     * Returns the maximum allocation percentage formatted for
     * the current user's locale.
     * @return the formatted maximum allocation % or empty string if the
     * person is not assigned
     */
    String getMaximumAllocation();

    /**
     * Returns the maximum allocation percentage unformatted.
     * @return the maximum allocation value
     */
    String getMaximumAllocationDecimal();

    /**
     * Returns the time for which to show resource allocation
     * which is the start time for the assignee (if there is one)
     * or the current time if the person is not assigned or has not
     * start time.
     * @return the time, in milliseconds, for which to show resource allocation
     */
    String getResourceAllocationTime();

    /**
     * Returns <code>checked</code> if the current person is flagged
     * as the owner of the task.
     * @return <code>checked</code> or empty string if the person is not
     * assigned or is not the owner
     */
    String getIsOwnerCheckedAttribute();

    /**
     * Returns the HTML options for assignment statuses with the currently
     * selected status for the assignment selected (if assigned).
     * When not assigned, the option list is returned with no selection.
     * @return the HTML option elements
     */
    String getAssignmentStatusOptions();

    /**
     * Returns the current assignee's role, if any.
     * @return the role or empty string if there is none or the person
     * is not assigned
     */
    String getRole();

    /**
     * Returns the start date for the assignment formatted for
     * the current user's locale and the assignee's time zone.
     * @return the start date formatted or
     * empty string if the person is not assigned
     */
    String getStartDateTime();

    /**
     * Returns the end date for the assignment formatted for
     * the current user's locale and the assignee's time zone.
     * @return the end date formatted or
     * empty string if the person is not assigned
     */
    String getEndDateTime();

    /**
     * Indicates whether we should display the start and end date times
     * using the current user's time zone.
     * <p>
     * This will be necessary of if assignee's time zone has different rules
     * from the current user's time zone.
     * </p>
     * @return
     */
    boolean isUserTimeZoneDisplayRequired();

    /**
     * Returns the start date for the assignment formatted for
     * the current user's locale and the current user's time zone.
     * <p>
     * This will only return a differnt date and time if {@link #isUserTimeZoneDisplayRequired}
     * </p>
     * @return the start date formatted or
     * empty string if the person is not assigned
     */
    String getStartDateTimeForUser();

    /**
     * Returns the end date for the assignment formatted for
     * the current user's locale and the current user's time zone.
     * <p>
     * This will only return a differnt date and time if {@link #isUserTimeZoneDisplayRequired}
     * </p>
     * @return the end date formatted or
     * empty string if the person is not assigned
     */
    String getEndDateTimeForUser();

    /**
     * Returns the time zone for the assignment.
     * If the user does not have any time zone defined then the schedules default time zone
     * would be returned.
     * @return the applicable time zone for the user
     */
    TimeZone getTimeZone();
}
