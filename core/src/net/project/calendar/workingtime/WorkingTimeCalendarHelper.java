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
package net.project.calendar.workingtime;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.project.base.property.PropertyProvider;
import net.project.calendar.TimeBean;
import net.project.persistence.PersistenceException;
import net.project.schedule.Schedule;
import net.project.security.User;
import net.project.util.ErrorReporter;
import net.project.util.Validator;

/**
 * Provides a Helper class for displaying a single working time calendar.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class WorkingTimeCalendarHelper {

    //
    // Static members
    //

    /** The format to use when display the day of week as a day. */
    private static final String DAY_OF_WEEK_FORMAT = "EEEE";

    /** The date format style to use when displaying the date. */
    private static final int DATE_FORMAT_STYLE = DateFormat.MEDIUM;

    private static final String DAY_OF_WEEK_VALUE_USE_DEFAULT = "usedefault";
    private static final String DAY_OF_WEEK_VALUE_WORKING_TIME = "workingtime";
    private static final String DAY_OF_WEEK_VALUE_NONWORKING_TIME = "nonworkingtime";

    /**
     * The prefix applied to HTML input fields that contain a value to
     * indicate "working time" or "non working time".
     */
    private static final String DAY_OF_WEEK_VALUE_INPUT_PREFIX = "dayValue_";

    /**
     * The prefix applied to HTML input fields that provide time values for
     * working times.
     */
    private static final String DAY_OF_WEEK_TIME_INPUT_START_PREFIX = "dayTimeStart";
    private static final String DAY_OF_WEEK_TIME_INPUT_END_PREFIX = "dayTimeEnd";
    private static final String DAY_OF_WEEK_TIME_CONTROL_PREFIX = "dayTimeControl";
    //
    // Instance members
    //

    /**
     * The current user.
     */
    private final User user;

    /**
     * The current schedule.
     */
//    private final Schedule schedule;

    /**
     * The provider used for fetching working time calendar definitions.
     */
    private final IWorkingTimeCalendarProvider provider;

    /**
     * The current working time calendar definition.
     */
    private final WorkingTimeCalendarDefinition calendarDef;

    /**
     * The name of this calendar.
     */
    private String name;

    /**
     * A map of day number to day of week helper.
     */
    private final Map dayOfWeekMap = new HashMap();

    /**
     * Initialize the helper based on the request.
     * <p>
     * Loads the working time calendar definition for the specified calendar ID.
     * Assumes there is a session attribute called <code>user</code> and <code>schedule</code>.
     * </p>
     * @param request the request containing required attributes
     * @param calendarID the ID of the calendar to load; null for the default calendar
     * @throws NullPointerException if calendarID is null
     * @throws IllegalStateException if no user or schedule attribute was found in the session
     * @throws IllegalArgumentException if the calendar with the specified ID cannot be found
     */
    public WorkingTimeCalendarHelper(HttpServletRequest request, IWorkingTimeCalendarProvider provider, String calendarID) {

        if (calendarID == null) {
            throw new NullPointerException("calendarID is required");
        }

        user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            throw new IllegalStateException("Could not find attribute with name 'user' in session");
        }

//        schedule = (Schedule) request.getSession().getAttribute("schedule");
//        if (schedule == null) {
//            throw new IllegalStateException("Could not find attribute with name 'schedule' in session");
//        }

        // Grab the provider for the schedule
//        provider = schedule.getWorkingTimeCalendarProvider();
        
        this.provider = provider;

        WorkingTimeCalendarDefinition calendarDef = provider.get(calendarID);
        if (calendarDef == null) {
            throw new IllegalArgumentException("Working time calendar definition not found with ID " + calendarID);
        }

        this.calendarDef = calendarDef;

        this.name = calendarDef.getDisplayName();

        // Grab all the day of weeks (including inherited ones)
        // and add them to the map
        for (int dayNumber = Calendar.SUNDAY; dayNumber <= Calendar.SATURDAY; dayNumber++) {
            DayOfWeekEntry nextEntry = this.calendarDef.getDayOfWeek(dayNumber);
            dayOfWeekMap.put(new Integer(nextEntry.getDayNumber()), new DayOfWeekHelper(nextEntry));
        }

    }

    /**
     * Returns the ID of the calendar definition.
     * @return the ID
     */
    public String getID() {
        return this.calendarDef.getID();
    }

    /**
     * Indicates whether this is a base calendar.
     * @return true if this is a base calendar; false otherwise
     */
    public boolean isBaseCalendar() {
        return this.calendarDef.isBaseCalendar();
    }

    /**
     * Returns the display name of the parent calendar.
     * @return the display name
     * @throws IllegalStateException if this is a base calendar
     */
    public String getParentCalendarDisplayName() {
        if (isBaseCalendar()) {
            throw new IllegalStateException("A base calendar has no parent calendar");
        }
        return this.provider.get(this.calendarDef.getParentCalendarID()).getDisplayName();
    }

    /**
     * Returns an iterator over the day of week helper entries in the current calendar
     * definition in order starting with the first day of the week as defined
     * by the user's locale.
     * <p>
     * There is one entry per day of week, for a total of 7.  Day Of Weeks
     * that are inherited from a parent calendar are included, but flagged as inherited.
     * </p>
     * @return an iterator where each element is a <code>DayOfWeekHelper</code>.
     */
    public Iterator getDayOfWeekIterator() {
        // Sort in locale order
        List helperEntries = new ArrayList(this.dayOfWeekMap.values());
        Collections.sort(helperEntries, new DayOfWeekComparator(this.user.getLocale()));
        return helperEntries.iterator();
    }

    /**
     * Returns an iterator over the date entries in the current calendar
     * definition.
     * @return an iterator where each element is a <code>WorkingTimeCalendarEntry</code>.
     */
    public Iterator getDateIterator() {

        // Create a sorted list of day of week entries
        final List orderedDateEntries = new ArrayList();
        orderedDateEntries.addAll(this.calendarDef.getDateEntries());
        Collections.sort(orderedDateEntries, new DateComparator());

        // Create an anonymous inner class that is an Iterator that simply
        // wraps an iterator over the date entries
        return new Iterator(){
            // Wrapped iterator
            private final Iterator it = orderedDateEntries.iterator();

            public boolean hasNext() {
                return it.hasNext();
            }

            public Object next() {
                return new DateHelper((DateEntry) it.next());
            }

            public void remove() {
                it.remove();
            }
        };
    }

    /**
     * Returns a collection of options for each working time calendar in
     * the current plan.
     * @return a collection where each element is an <code>IHTMLOption</code>
     */
    public Collection getWorkingTimeCalendarOptions() {
        return WorkingTimeCalendarListHelper.getWorkingTimeCalendarOptions(this.provider.getAll(), this.provider.getDefault().getID());
    }

    /**
     * Indicates whether the name of this calendar is updatable.
     * Base calendar names are; non-base calendars are not
     * @return true if this calendar name is updatable; false otherwise
     */
    public boolean isNameUpdateable() {
        return this.calendarDef.isBaseCalendar();
    }

    /**
     * Sets the name of the working time calendar definition.
     * @param name the name
     * @throws IllegalStateException if this calendar's name is not updatable
     * @see #isNameUpdateable
     */
    private void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the display name of the working time calendar definition.
     * @return the display name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Indicates whether the calendar definition backed by this helper
     * is the default calendar for the context from which it was fetched.
     * @return true if this is the default calendar; false otherwise
     */
    public boolean isDefault() {
        return this.provider.isDefault(this.calendarDef.getID());
    }

    /**
     * Stores the working time calendar definition backed by this helper.
     * @throws PersistenceException if there is a problem storing;
     * also if there is no working day of week entry (when resolving from
     * this calendar and the parent calendar)
     */
    public void store() throws PersistenceException {

        if (isNameUpdateable()) {
            this.calendarDef.setName(name);
        }

        // Update Entries
        for (Iterator it = this.dayOfWeekMap.values().iterator(); it.hasNext();) {
            DayOfWeekHelper nextDay = (DayOfWeekHelper) it.next();

            if (nextDay.isUseDefaultSelected()) {
                // Inerhit the day from the parent calendar
                calendarDef.updateDayOfWeekInherit(nextDay.getDayNumber());

            } else if (nextDay.isWorkingTimeSelected()) {
                // Set it to working time
                calendarDef.updateDayOfWeekWorkingDay(nextDay.getDayNumber(), nextDay.getWorkingTimeEditHelper().getWorkingTimes());

            } else {
                // Set it to non working time
                calendarDef.updateDayOfWeekNonWorkingDay(nextDay.getDayNumber());
            }

        }

        // Delegate to provider so that it is updated
        this.provider.store(this.calendarDef.getID());
    }

    /**
     * Removes the working time calendar definition backed by this helper.
     * @throws PersistenceException if there is a problem removing
     */
    public void remove() throws PersistenceException {
        // Delegate to provider so that it is updated
        this.provider.remove(this.calendarDef.getID());
    }

    /**
     * Returns a date entry helper for creating a new date entry.
     * @return the date entry helper
     */
    public WorkingTimeCalendarDateEntryHelper makeDateEntryHelper(HttpServletRequest request) {
        return new WorkingTimeCalendarDateEntryHelper(request, this.provider, this.calendarDef);
    }

    /**
     * Returns a date entry helper for editing an existing date entry.
     * @param entryID the ID of the entry to edit
     * @return the date entry helper
     */
    public WorkingTimeCalendarDateEntryHelper makeDateEntryHelper(HttpServletRequest request, String entryID) {
        return new WorkingTimeCalendarDateEntryHelper(request, this.provider, this.calendarDef, entryID);
    }

    /**
     * Processes the submission from the edit page, parsing all parameters
     * and validating the state.
     * <p>
     * No store is performed.
     * </p>
     * @param request the request from which to get the parameters
     * @param errorReporter the error reporter to which to add validation
     * errors; if there are any validation errors, at least one error will
     * be added to this.  If everything is sucessful, no errors will be added.
     */
    public void processSubmission(HttpServletRequest request, ErrorReporter errorReporter) {

        // Set the calendar name
        String name = request.getParameter("name");
        if (isNameUpdateable()) {
            if (Validator.isBlankOrNull(name)) {
                errorReporter.addError(PropertyProvider.get("prm.schedule.workingtime.edit.name.isrequired.message"));
            } else {
                setName(name);
            }
        }

        for (Enumeration e = request.getParameterNames(); e.hasMoreElements(); ) {
            String nextName = (String) e.nextElement();
            String value = request.getParameter(nextName);

            if (nextName.startsWith(DAY_OF_WEEK_VALUE_INPUT_PREFIX)) {
                // We found a paramter we are insterested in; it is
                // a value of "working time" "non working" time etc,
                int dayNumber = Integer.valueOf(nextName.substring(DAY_OF_WEEK_VALUE_INPUT_PREFIX.length())).intValue();
                DayOfWeekHelper day = (DayOfWeekHelper) this.dayOfWeekMap.get(new Integer(dayNumber));

                if (value.equals(DAY_OF_WEEK_VALUE_USE_DEFAULT)) {

                    if (!day.hasParent()) {
                        throw new IllegalArgumentException("Cannot update day of week with day number '" + dayNumber + "' to 'use default' on a base calendar");
                    }

                    day.setUseDefaultSelected();

                } else if (value.equals(DAY_OF_WEEK_VALUE_WORKING_TIME)) {
                    day.setWorkingTimeSelected();

                } else if (value.equals(DAY_OF_WEEK_VALUE_NONWORKING_TIME)) {
                    day.setNonWorkingTimeSelected();

                } else {
                    throw new IllegalArgumentException("Unexpected value '" + value + "' when updating day of week");
                }

            } else if (nextName.startsWith(DAY_OF_WEEK_TIME_CONTROL_PREFIX)) {
                // We found a parameter controlling a time of form:
                // <prefix>_<dayNum>_<position>
                // where dayNum 1..7 and position 0..5
                int dayNumber = Integer.valueOf(nextName.substring(nextName.indexOf('_') + 1, nextName.lastIndexOf('_'))).intValue();
                int position = Integer.valueOf(nextName.substring(nextName.lastIndexOf('_') + 1)).intValue();
                DayOfWeekHelper day = (DayOfWeekHelper) this.dayOfWeekMap.get(new Integer(dayNumber));

                // Construct the names of the time fields
                String startTimeName = DAY_OF_WEEK_TIME_INPUT_START_PREFIX + "_" + dayNumber + "_" + position;
                String endTimeName = DAY_OF_WEEK_TIME_INPUT_END_PREFIX + "_" + dayNumber + "_" + position;

                // Parse the start and end; we don't care about the date portion,
                // just the time
                // It is optional; the found time field may be empty
                // We'll get a null date if no values are selected
                day.getWorkingTimeEditHelper().setStartTime(position, TimeBean.parseTime(request, startTimeName, new Date(), true));
                day.getWorkingTimeEditHelper().setEndTime(position, TimeBean.parseTime(request, endTimeName, new Date(), true));

            }
        }

        // Validate inputs
        validate(errorReporter);

    }

    private void validate(ErrorReporter errorReporter) {

        // Validate all days and ensure there is at least one working day
        // defined
        boolean isOneWorkingDayDefined = false;
        for (Iterator it = this.dayOfWeekMap.values().iterator(); it.hasNext();) {
            DayOfWeekHelper nextDay = (DayOfWeekHelper) it.next();
            nextDay.validate(errorReporter);
            // If no working day is defined yet and this is a working day,
            // record that fact
            if (!isOneWorkingDayDefined) {
                isOneWorkingDayDefined = nextDay.isWorkingDay();
            }
        }

        if (!isOneWorkingDayDefined) {
            errorReporter.addError(PropertyProvider.get("prm.schedule.workingtime.edit.noworkingtime.message"));
        }

    }

    /**
     * Updates the current working days and sets their working times according
     * to the setting of the request parameter <code>presetType</code>.
     * <p>
     * <li> <code>standard</code> - 8:00 - 12:00, 13:00 - 17:00
     * <li> <code>nightshift</code> - 0:00 - 3:00, 4:00 - 8:00, 23:00 - 24:00
     * <li> <code>24hour</code> - 0:00 - 24:00
     * </p>
     * <p>
     * No store is performed.
     * </p>
     * @param request the request from which to get the parameters
     * @throws IllegalStateException if no parameter called presetType is found
     * or it is empty or set to an unknown value
     */
    public void processPreset(HttpServletRequest request) {

        String presetType = request.getParameter("presetType");
        if (Validator.isBlankOrNull(presetType)) {
            throw new IllegalStateException("Missing request parameter 'presetType'");
        }

        if (presetType.equals("standard")) {

            for (Iterator it = this.dayOfWeekMap.values().iterator(); it.hasNext();) {
                DayOfWeekHelper nextDay = (DayOfWeekHelper) it.next();

                if (nextDay.getDayNumber() == Calendar.SUNDAY || nextDay.getDayNumber() == Calendar.SATURDAY) {
                    nextDay.setNonWorkingTimeSelected();
                } else {
                    nextDay.setWorkingTimeSelected();
                    nextDay.getWorkingTimeEditHelper().setTimes(0, new Time(8, 0), new Time(12, 0));
                    nextDay.getWorkingTimeEditHelper().setTimes(1, new Time(13, 0), new Time(17, 0));
                    nextDay.getWorkingTimeEditHelper().setTimes(2, null, null);
                    nextDay.getWorkingTimeEditHelper().setTimes(3, null, null);
                    nextDay.getWorkingTimeEditHelper().setTimes(4, null, null);
                }
            }

        } else if (presetType.equals("nightshift")) {

            for (Iterator it = this.dayOfWeekMap.values().iterator(); it.hasNext();) {
                DayOfWeekHelper nextDay = (DayOfWeekHelper) it.next();

                if (nextDay.getDayNumber() == Calendar.SUNDAY) {
                    // Sunday is non working day
                    nextDay.setNonWorkingTimeSelected();
                } else if (nextDay.getDayNumber() == Calendar.MONDAY) {
                    // Monday starts at 23:00
                    nextDay.setWorkingTimeSelected();
                    nextDay.getWorkingTimeEditHelper().setTimes(0, new Time(23, 0), new Time(24, 0));
                    nextDay.getWorkingTimeEditHelper().setTimes(1, null, null);
                    nextDay.getWorkingTimeEditHelper().setTimes(2, null, null);
                    nextDay.getWorkingTimeEditHelper().setTimes(3, null, null);
                    nextDay.getWorkingTimeEditHelper().setTimes(4, null, null);
                } else if (nextDay.getDayNumber() == Calendar.SATURDAY) {
                    // Saturday ends in the morning
                    nextDay.setWorkingTimeSelected();
                    nextDay.getWorkingTimeEditHelper().setTimes(0, new Time(0, 0), new Time(3, 0));
                    nextDay.getWorkingTimeEditHelper().setTimes(1, new Time(4, 0), new Time(8, 0));
                    nextDay.getWorkingTimeEditHelper().setTimes(2, null, null);
                    nextDay.getWorkingTimeEditHelper().setTimes(3, null, null);
                    nextDay.getWorkingTimeEditHelper().setTimes(4, null, null);
                } else {
                    nextDay.setWorkingTimeSelected();
                    nextDay.getWorkingTimeEditHelper().setTimes(0, new Time(0, 0), new Time(3, 0));
                    nextDay.getWorkingTimeEditHelper().setTimes(1, new Time(4, 0), new Time(8, 0));
                    nextDay.getWorkingTimeEditHelper().setTimes(2, new Time(23, 0), new Time(24, 0));
                    nextDay.getWorkingTimeEditHelper().setTimes(3, null, null);
                    nextDay.getWorkingTimeEditHelper().setTimes(4, null, null);
                }
            }

        } else if (presetType.equals("24hour")) {

            for (Iterator it = this.dayOfWeekMap.values().iterator(); it.hasNext();) {
                DayOfWeekHelper nextDay = (DayOfWeekHelper) it.next();
                nextDay.setWorkingTimeSelected();
                nextDay.getWorkingTimeEditHelper().setTimes(0, new Time(0, 0), new Time(24, 0));
                nextDay.getWorkingTimeEditHelper().setTimes(1, null, null);
                nextDay.getWorkingTimeEditHelper().setTimes(2, null, null);
                nextDay.getWorkingTimeEditHelper().setTimes(3, null, null);
                nextDay.getWorkingTimeEditHelper().setTimes(4, null, null);
            }

        } else {
            throw new IllegalStateException("Invalid request parameter 'presetType': " + presetType);
        }

    }

    //
    // Inner classes
    //

    /**
     * Helper class for rendering working time calendar entries that represent
     * day of week entries.
     */
    public class DayOfWeekHelper {

        private static final int USE_DEFAULT = 0;
        private static final int WORKING_TIME = 1;
        private static final int NON_WORKING_TIME = 2;

        /** The day number (Calendar.SUNDAY .. Calendar.SATURDAY). */
        private final int dayNumber;

        /**
         * Indicates whether this entry has a parent entry.
         */
        private final boolean hasParentEntry;

        /**
         * Indicates whether the parent entry (if any) is a working day.
         */
        private final boolean isParentEntryWorkingDay;

        /** The option selected. */
        private int selectedOption = WORKING_TIME;

        /**
         * A helper for editing working times populated from the day entry's
         * working times.
         */
        private final WorkingTimeEditHelper workingTimeEditHelper;

        /**
         * Creates a new DayOfWeekHelper for the specified entry.
         * @param entry the entry
         */
        private DayOfWeekHelper(DayOfWeekEntry entry) {

            this.dayNumber = entry.getDayNumber();

            // A day of week has a parent entry if it is not a base calendar
            this.hasParentEntry = !WorkingTimeCalendarHelper.this.calendarDef.isBaseCalendar();

            // Determine whether the parent is a working day or not
            if (this.hasParentEntry) {
                this.isParentEntryWorkingDay = (WorkingTimeCalendarHelper.this.calendarDef.getParentDayOfWeek(this.dayNumber).isWorkingDay());
            } else {
                this.isParentEntryWorkingDay = false;
            }

            // Determine which option is selected
            if ((this.hasParentEntry && WorkingTimeCalendarHelper.this.calendarDef.isInheritedDayOfWeek(dayNumber))) {
                // We are inheriting the entry
                this.selectedOption = USE_DEFAULT;
            } else {
                // The entry is specified in this calendar
                this.selectedOption = (entry.isWorkingDay() ? WORKING_TIME : NON_WORKING_TIME);
            }

            this.workingTimeEditHelper = new WorkingTimeEditHelper(WorkingTimeCalendarHelper.this.user, entry.getWorkingTimes());
        }

        /**
         * Returns the helper used to edit working times for this entry.
         * @return the helper
         */
        public WorkingTimeEditHelper getWorkingTimeEditHelper() {
            return this.workingTimeEditHelper;
        }

        /**
         * Returns the day of week displayed in the current user's locale.
         * Uses the format <code>EEEE</code>.
         * @return the day of week formatted
         */
        public String getDayOfWeekFormatted() {

            // Create a calendar in the timezone of the user and set the
            // day to bet the current day of week
            Calendar cal = new GregorianCalendar(WorkingTimeCalendarHelper.this.user.getTimeZone());
            cal.set(Calendar.DAY_OF_WEEK, this.dayNumber);

            // Format the day of week as a day
            return WorkingTimeCalendarHelper.this.user.getDateFormatter().formatDate(cal.getTime(), DAY_OF_WEEK_FORMAT);
        }

        /**
         * Returns the day number of this day of week,
         * in the range {@link Calendar#SUNDAY} to {@link Calendar#SATURDAY}.
         * @return the day of week number
         */
        public int getDayNumber() {
            return this.dayNumber;
        }

        public boolean isUseDefaultSelected() {
            return (this.selectedOption == USE_DEFAULT);
        }

        private void setUseDefaultSelected() {
            this.selectedOption = USE_DEFAULT;
        }

        public boolean isNonWorkingTimeSelected() {
            return (this.selectedOption == NON_WORKING_TIME);
        }

        private void setNonWorkingTimeSelected() {
            this.selectedOption = NON_WORKING_TIME;
        }

        public boolean isWorkingTimeSelected() {
            return (this.selectedOption == WORKING_TIME);
        }

        private void setWorkingTimeSelected() {
            this.selectedOption = WORKING_TIME;
        }

        /**
         * Indicates whether the day of week entry has a parent entry.
         * @return true if it has a parent entry; false otherwise
         */
        public boolean hasParent() {
            return this.hasParentEntry;
        }

        /**
         * Indicates whether the day of week entry corresponding to
         * this one in the parent calendar is a working day or not.
         * @return true if the parent calendar's entry is a working day; false otherwise
         * returns false if this is a base calendar
         * @throws IllegalStateException if this entry does not have a parent
         * entry
         * @see #hasParent
         */
        public boolean isParentEntryWorkingDay() {
            if (!hasParent()) {
                throw new IllegalStateException("entry has no parent");
            }
            return this.isParentEntryWorkingDay;
        }

        /**
         * Indicates whether this is a working day.
         * It is a working day if "use default" is selected and the parent
         * entry is a working day; otherwise it is a working day if
         * "working time" is selected
         * @return true if this is a working day; false otherwise
         */
        public boolean isWorkingDay() {
            return (((this.selectedOption == USE_DEFAULT) && hasParent() && isParentEntryWorkingDay()) ||
                    (this.selectedOption == WORKING_TIME));
        }

        /**
         * Validates this day of week.
         * @param errorReporter the reporter to which to add errors; an error
         * will be added if validation fails in at least one place
         */
        private void validate(ErrorReporter errorReporter) {

            if (isWorkingTimeSelected()) {

                getWorkingTimeEditHelper().validate(errorReporter, new WorkingTimeErrorMessageProvider(getDayOfWeekFormatted()));

            } else {
                // Nothing to validate; working time is ignored

            }

        }

    }


    /**
     * Helper class for rendering working time calendar entries that represent
     * date entries.
     */
    public class DateHelper {

        /**
         * The wrapped DateEntry.
         */
        private final DateEntry dateEntry;

        /**
         * Creates a new DateHelper for the specified entry.
         * @param entry the entry
         */
        private DateHelper(DateEntry entry) {
            this.dateEntry = entry;
        }

        /**
         * Returns the ID of the date entry.
         * @return the ID
         */
        public String getEntryID() {
            return this.dateEntry.getEntryID();
        }

        /**
         * Indicates whether this DateHelper represents a date
         * that is a working day.
         * @return true if the date is a working day; false if a non-working day
         */
        public boolean isWorkingDay() {
            return this.dateEntry.isWorkingDay();
        }

        /**
         * Returns the date or dates of this date entry formatted for the
         * current user's locale and timezone.
         * @return the date display
         */
        public String getDateDisplay() {

            String display;

            if (isSingleDate()) {
                display = format(dateEntry.getStartDayOfYear());
            } else {

                display = new StringBuffer(format(dateEntry.getStartDayOfYear()))
                        .append(" - ")
                        .append(format(dateEntry.getEndDayOfYear())).toString();

            }

            return display;
        }

        /**
         * Indicates whether this DateHelper represents a DateEntry
         * on a single date (e.g. Friday August 8, 2003) or a span
         * of dates (e.g. Friday August 8, 2003 - Monday August 11, 2003)
         * @return true if it is a single date; false if a span
         */
        private boolean isSingleDate() {
            return dateEntry.isSingleDate();
        }

        /**
         * Converts the specified day of year to a date for the
         * current user's time zone and formats it for display.
         * @param dayOfYear the day of year to convert
         * @return a formatted date
         */
        private String format(DayOfYear dayOfYear) {
            return WorkingTimeCalendarHelper.this.user.getDateFormatter().formatDate(
                    dayOfYear.toDate(WorkingTimeCalendarHelper.this.user.getTimeZone()), DATE_FORMAT_STYLE
            );
        }
        
        /**
    	 * @return the dateDescription
    	 */
    	public String getDateDescription() {
    		return dateEntry.getDateDescription();
    	}

    }

    //
    // Nested top-level classes
    //

    /**
     * Provides error messages when validating working times.
     */
    private static class WorkingTimeErrorMessageProvider implements WorkingTimeEditHelper.IErrorMessageProvider {

        private final String dayName;

        WorkingTimeErrorMessageProvider(String dayName) {
            this.dayName = dayName;
        }

        public String getStartTimeEndTimeRequired() {
            return PropertyProvider.get("prm.schedule.workingtime.edit.startendtimerequired.message", dayName);
        }

        public String getEndTimeMustBeAfterStartTime() {
            return PropertyProvider.get("prm.schedule.workingtime.edit.endtimeafterstart.message", dayName);
        }

        public String getStartTimeMustBeAfterPreviousEndTime() {
            return PropertyProvider.get("prm.schedule.workingtime.edit.starttimeafterend.message", dayName);
        }

        public String getWorkingTimeRequired() {
            return PropertyProvider.get("prm.schedule.workingtime.edit.workingtimerequired.message", dayName);
        }

    }

    /**
     * Provides a comparator to compare two <code>DayOfWeekEntry</code>s
     * to order for display.
     * See {@link #compare} for details.
     */
    private static class DayOfWeekComparator implements Comparator {

        /** Maintains the first day of the week. */
        private final int firstDayOfWeek;

        /**
         * Creates a comparator based on the specified locale.
         * @param locale the locale that provides the first day of the week
         * for comparison purposes
         */
        private DayOfWeekComparator(Locale locale) {
            Calendar cal = new GregorianCalendar(locale);
            this.firstDayOfWeek = cal.getFirstDayOfWeek();
        }

        /**
         * Compares two <code>DayOfWeekEntry</code>s and indicates whether
         * the first is before, equal to or after the second.
         * <p>
         * The first day of week for the user's locale is considered so that
         * ordering is preserved for locales.
         * </p>
         * @param o1 the first entry
         * @param o2 the second entry
         * @return a negative integer, zero, or positive interger if the first
         * entry is earlier than, equal to, or after the second entry as determined
         * by their day numbers and the locale's first day of week
         */
        public int compare(Object o1, Object o2) {

            DayOfWeekHelper entry1 = (DayOfWeekHelper) o1;
            DayOfWeekHelper entry2 = (DayOfWeekHelper) o2;

            // First, adjust the day number so comparison will
            // work regardless of day of week
            // We add 7 to days of week numerically lower than the first day
            // of the week so that they are moved to the end of the week
            int adjustedDayNumber1 = entry1.getDayNumber();
            if (adjustedDayNumber1 < firstDayOfWeek) {
                adjustedDayNumber1 += 7;
            }

            int adjustedDayNumber2 = entry2.getDayNumber();
            if (adjustedDayNumber2 < firstDayOfWeek) {
                adjustedDayNumber2 += 7;
            }

            // Now determine comparison
            final int result;
            if (adjustedDayNumber1 < adjustedDayNumber2) {
                result = -1;

            } else if (adjustedDayNumber1 == adjustedDayNumber2) {
                result = 0;

            } else {
                result = 1;
            }

            return result;
        }
    }

    /**
     * Provides a comparator of <code>DateEntry</code>s for ordering their
     * display.
     * See {@link #compare} for details.
     */
    private static class DateComparator implements Comparator {

        /**
         * Compares two <code>DateEntry</code>s and indicates whether
         * the first is earlier, equal to or later than the second.
         * <p>
         * A DateEntry is earlier if its start date is earlier or its start date
         * is equal and its end date is earlier.
         * A DateEntry is equal if both its start date and end date are equal.
         * A DateEntry is later if its start date is later or its start date
         * is equal and its end date is later.
         * </p>
         * @param o1 the first entry
         * @param o2 the second entry
         * @return a negative integer, zero, or positive interger if the first
         * entry is earlier than, equal to, or later than the second entry as determined
         * by their start and end dates
         */
        public int compare(Object o1, Object o2) {

            DateEntry entry1 = (DateEntry) o1;
            DateEntry entry2 = (DateEntry) o2;

            final int result;
            if (entry1.getStartDayOfYear().isBefore(entry2.getStartDayOfYear())) {
                // Start date before; earlier
                result = -1;

            } else if (entry1.getStartDayOfYear().equals(entry2.getStartDayOfYear())) {
                // Start date equal; based on end dates
                if (entry1.getEndDayOfYear().isBefore(entry2.getEndDayOfYear())) {
                    result = -1;
                } else if (entry1.getEndDayOfYear().equals(entry2.getEndDayOfYear())) {
                    result = 0;
                } else {
                    result = 1;
                }

            } else {
                // Start date after; later
                result = 1;
            }

            return result;
        }

    }

}
