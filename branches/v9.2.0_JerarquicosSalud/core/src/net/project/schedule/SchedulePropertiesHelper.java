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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import net.project.base.property.PropertyProvider;
import net.project.calendar.workingtime.IWorkingTimeCalendar;
import net.project.calendar.workingtime.NoWorkingTimeException;
import net.project.gui.html.HTMLOptionList;
import net.project.gui.html.IHTMLOption;
import net.project.schedule.calc.TaskCalculationType;
import net.project.security.User;
import net.project.util.DateFormat;
import net.project.util.TimeZoneList;

/**
 * Provides a helper class for viewing and editing schedule properties.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class SchedulePropertiesHelper {

    public static final String PERSONAL_RESOURCE_CALENDAR = "PERSONAL";
    public static final String SCHEDULE_RESOURCE_CALENDAR = "SCHEDULE";
	private User user;
    private Date startDate;
    private Date endDate;
    /** The schedule default time zone (or current user's time zone if none). */
    private TimeZone timeZone;
    private boolean isAutoCalculateTaskEndpoints;
    private TaskCalculationType defaultTaskCalculationType;
    private List scheduleDependencies = new ArrayList();
    private boolean editingWarning;
    private boolean unAssignedWorkcapture;
    private String resourceCalendar;

    /**
     * Creates an empty SchedulePropertiesHelper.
     */
    public SchedulePropertiesHelper() {
        // Do nothing
    }

    /**
     * Initializes the helper from the request.
     * <p>
     * Expects to find a schedule in the session.
     * </p>
     * @param request
     * @throws IllegalStateException if there is no schedule or user objects found in
     * the session
     */
    public void init(HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            throw new IllegalStateException("Missing session attribute 'user'");
        }

        Schedule schedule = (Schedule) request.getSession().getAttribute("schedule");
        if (schedule == null) {
            throw new IllegalStateException("Missing session attribute 'schedule'");
        }

        this.user = user;
        this.startDate = schedule.getScheduleStartDate();

        //a fix to get the schedule properties start date equal to the start working time
        IWorkingTimeCalendar cal = schedule.getWorkingTimeCalendar();
        try {
            startDate = cal.ensureWorkingTimeStart(startDate);
        } catch (NoWorkingTimeException e) {
        }
        
        this.endDate = findScheduleEndDate(schedule);
        this.timeZone = schedule.getTimeZone();
        this.isAutoCalculateTaskEndpoints = schedule.isAutocalculateTaskEndpoints();
        this.defaultTaskCalculationType = schedule.getDefaultTaskCalculationType();
        this.editingWarning = schedule.isEditingWarning();
        this.unAssignedWorkcapture = schedule.isUnAssignedWorkcapture();
        this.resourceCalendar = schedule.getResourceCalendar();
    }

    /**
     * Finds the schedule end date from the specified schedule.
     * <p>
     * The end date is the actual stored end date (if there is one) or
     * the latest end date from all the schedule's tasks (if any).
     * </p>
     * @param schedule the schedule from which to get the end date
     * @return the end date or earliest entry end time or null if
     * there is no end date and there are no schedule entries
     */
    private Date findScheduleEndDate(Schedule schedule) {
        Date scheduleEndDate = schedule.getScheduleEndDate();

        if (scheduleEndDate == null) {
            // Iterate through each of the entries, choosing the earliest start date.
            for (Iterator it = schedule.getEntries().iterator(); it.hasNext();) {
                IScheduleEntry scheduleEntry = (IScheduleEntry) it.next();

                if (scheduleEndDate == null ||
                    scheduleEntry.getEndTime().after(scheduleEndDate)) {
                    // We have not found an end date or
                    // the current entry ends after the current end date
                    // Use the current entry's end date
                    scheduleEndDate = scheduleEntry.getStartTime();
                }
            }
        }

        return scheduleEndDate;
    }

    /**
     * Returns the schedule start date which may be null.
     * @return the start date or null if there is none and none can be computed
     */
    public Date getStartDate() {
        return this.startDate;
    }

    /**
     * Returns the formatted schedule start date.
     * @return the start date formatted for the current user or empty string
     * if there is no start date and there are no schedule entries
     */
    public String getStartDateFormatted() {
        return (this.startDate == null ? "" : formatDate(this.startDate));
    }

    /**
     * Returns the formatted schedule end date.
     * @return the end date formatted for the schedule default time zone or the current user's time zone
     * or text indicating there is none if there is no end date and there are no schedule entries
     */
    public String getEndDateFormatted() {
        return (this.endDate == null ? PropertyProvider.get("prm.schedule.properties.enddate.scheduleempty.message") : formatDateTime(this.endDate));
    }

    /**
     * Formats the specified date to a date and time format
     * using the schedule default time zone or the current user's time zone.
     * @param dateTime the date to format
     * @return the formatted date and time
     */
    private String formatDateTime(Date dateTime) {
        DateFormat dateFormat = user.getDateFormatter();
        return dateFormat.formatDateTime(dateTime, getDefaultTimeZone());
    }

    /**
     * Formats the specified date to a date format
     * using the schedule default time zone or the current user's time zone.
     * @param date the date to format
     * @return the formatted date only
     */
    private String formatDate(Date date) {
        DateFormat dateFormat = user.getDateFormatter();
        return dateFormat.formatDate(date, getDefaultTimeZone());
    }

    /**
     * Returns the "checked" attribute based on the autocalculation setting
     * @return the string "checked" if auto calculate is on;
     * otherwise empty string
     */
    public String getCheckedAutocalculateTaskEndpoints() {
        return (this.isAutoCalculateTaskEndpoints ? "checked" : "");
    }

    /**
     * Returns an HTML option list of time zones.
     * @return the option list of time zones that may be selected,
     * with the currently chosen time zone selected or the current
     * user's time zone selected if there is none
     */
    public String getTimeZoneOptions() {
        return TimeZoneList.getHtmlOptionList(this.timeZone.getID());
    }

    /**
     * Returns the default time zone to use when entering times.
     * @return the time zone which may be a specified time zone or the current user's time zone
     */
    public TimeZone getDefaultTimeZone() {
        return this.timeZone;
    }

    /**
     * Returns an HTML option list of task calculation types.
     * @return the option lists of task calculation types that may be selected,
     * with the current selected task calculation type selected
     */
    public String getTaskCalculationTypeOptions() {
        return HTMLOptionList.makeHtmlOptionList(
                TaskCalculationType.getFixedElementHTMLOptions(),
                this.defaultTaskCalculationType.getFixedElementHTMLOption());
    }

    /**
     * Returns the default task calculation type's fixed element as an <code>IHTMLOption</code>.
     * <p>
     * This is the default selected option in the list.
     * </p>
     * @return the default task calculation type fixed element
     */
    public IHTMLOption getDefaultTaskCalculationTypeFixedElement() {
        return this.defaultTaskCalculationType.getFixedElementHTMLOption();
    }

    /**
     * Returns the "checked" attribute based on default task calculation type effort driven.
     * @return "checked" if default task calculation type is effort driven;
     * otherwise, empty string
     */
    public String getCheckedNewTasksEffortDriven() {
        return (this.defaultTaskCalculationType.isEffortDriven() ? "checked" : "");
    }

    /**
     * Returns the "disabled" attribute for the new tasks effort driven checkbox
     * based on the task calculation type.
     * <p>
     * Fixed Work tasks can only be effort driven.
     * </p>
     * @return "disabled" if task calculation type is Fixed Work;
     * otherwise, empty string
     */
    public String getDisabledNewTasksEffortDriven() {
        return (this.defaultTaskCalculationType.equals(TaskCalculationType.FIXED_WORK) ? "disabled" : "");
    }

    public List getScheduleDependencies() {
        return scheduleDependencies;
    }
    
    /**
     * Returns the "checked" attribute based on the editingWarning setting
     * @return the string "checked" if Inline Editing Warning is on;
     * otherwise empty string
     */
    public String getCheckedEditingWarning() {
        return (this.editingWarning ? "checked" : "");
    }
    
    /**
     * Returns the "checked" attribute based on the unAssignedWorkcapture setting
     * @return the string "checked" if Un Assigned Task Work Capture is on;
     * otherwise empty string
     */
    public String getCheckedUnAssignedWorkcapture() {
        return (this.unAssignedWorkcapture ? "checked" : "");
    }
    
    /**
     * Returns the "checked" attribute based on the resourceCalendar setting
     * @return the string "checked" if auto Personal Resource Calendar is checked ;
     * otherwise empty string
     */
    public String getCheckedPersonalResourceCalendar(){
    	return (PERSONAL_RESOURCE_CALENDAR.equals(this.resourceCalendar) ? "checked" : "");
    }
    
    /**
     * Returns the "checked" attribute based on the resourceCalendar setting
     * @return the string "checked" if auto Schedule Resource Calendar is checked ;
     * otherwise empty string
     */
    public String getCheckedScheduleResourceCalendar(){
    	return (SCHEDULE_RESOURCE_CALENDAR.equals(this.resourceCalendar) ? "checked" : "");
    }

}
