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

 package net.project.calendar;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import net.project.base.property.PropertyProvider;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.resource.Assignment;
import net.project.resource.FacilityFactory;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.Task;
import net.project.schedule.TaskFinder;
import net.project.schedule.TaskType;
import net.project.schedule.report.TaskTypeFilter;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.util.DateRange;
import net.project.util.DateUtils;
import net.project.xml.XMLUtils;

/**
 * Project.net calendar.  Provides all Gregorian calendar, Fiscal calendar, date methods
 * and date conversion methods.
 * Provides a calendar within the context of a Space.
 * Extends java.util.Calendar to provide Fiscal calendar, date methods, date conversion methods,
 * retrieval, storage, and display of ICalendarEntry items.
 * setSpace() (or setUser()), setStartDate(), setEndDate() must be called before working load(),
 *
 * @author RogerBly
 * @author AdamKlatzkin  03/00
 */
public class PnCalendar extends GregorianCalendar {

    //
    // Static members
    //

    /**
     * The event type ID denoting a meeting which is <code>100</code>.
     */
    public static final String MEETING = "100";

    /**
     * The event type ID denoting an event which is <code>200</code>.
     */
    public static final String EVENT = "200";

    /**
     * The number of milliseconds in a day, used for some calculations.
     */
    private static final int MILLISECONDS_IN_DAY = 1000 * 60 * 60 * 24;

    /**
     * The number of milliseconds in a week, used for some calculations.
     */
    private static final int MILLISECONDS_IN_WEEK = MILLISECONDS_IN_DAY * 7;

    /**
     * Returns a Date object and initialized to the time at which the method call was made,
     * measured to the nearest millisecond.
     * <p>
     * Using <code>new java.util.Date()</code> may be preferable.  Calling this
     * method numerous times in short succession will not return the same date.
     * </p>
     * @return the current time
     */
    public static java.util.Date currentTime() {
        return new java.util.Date();
    }


    //
    // Instance members
    //

    /**
     * The ID of this calendar which is populated by {@link #load}.
     * Used to limit the entries loaded when {@link #loadEntries} is called.
     */
    private String id = null;

    /**
     * The name of the calendar.
     */
    private String name = null;

    /**
     * The description of the calendar.
     */
    private String description = null;

    /**
     * The start date of entries to load.
     */
    private Date startDate = null;

    /**
     * The end date of entries to load.
     */
    private Date endDate = null;

    /**
     * The personal space, business space, project space, etc. context for this calendar.
     * Used to locate the default calendar for the space.
     */
    private Space space = null;

    /**
     * The current user context for this calendar.
     * Provides the timezone and locale settings for the calendar.
     */
    private net.project.security.User user = null;

    /**
     * Factory for creating facilities.
     */
    final FacilityFactory facilityFactory = new FacilityFactory();

    /**
     * A list of objects implementing ICalendarEntry.
     * This is populated when entries are loaded.
     */
    private final List calendarEntries = new ArrayList(25);

    /**
     * The ID of the space that was set before the last call to {@link #load}.
     * This is used to determine whether the space has changed since the last
     * load indicating another load is necessary.
     */
    private String loadedSpaceID = null;

    /**
     * Indicates that the current user is a space administrator.
     * This affects the loading of calendar entries; space administrators may
     * see all entries in a given space regardless of whether they are a
     * meeting attendee.
     */
    private boolean isSpaceAdministrator = false;

    /**
     * The ID of the space that was set before the last call to {@link #isSpaceAdministrator}.
     * This is used to determine whether the space has changed since the last
     * check for space admin priveleges.  We do this to avoid excessive checking
     * of space admin priveleges and setSpace() is only called once during the
     * life of a PnCalendar.
     */
    private String spaceAdminCheckedSpaceID = null;

    /**
     * Construct a calendar setting the timezone and locale properly for the
     * specified user.
     *
     * @param user a user that we will use to find the locale and time zone.
     */
    public PnCalendar(net.project.security.User user) {
        super(user.getTimeZone(), user.getLocale());
        this.user = user;
    }

    /**
     * Construct calendar w/o a user context.
     * This constuctor has a side effect in that it sets {@link #setLenient}
     * to true.  The standard constructor does not.
     * This behavior can go away at any time in the future.  Please
     * setLenient manually.
     */
    public PnCalendar() {
        this(SessionManager.getUser());
        setLenient(true);
    }

    /**
     * Creates a calendar for the specified timeZone.
     * This is useful for calendar arithmetic.
     * @param timeZone the timezone for calendar date conversions
     */
    public PnCalendar(TimeZone timeZone) {
        this();
        setTimeZone(timeZone);
    }

    public PnCalendar(TimeZone timeZone, Locale locale) {
        super(timeZone, locale);
    }

    /**
     * Set the user context for this calendar: provides the timezone and locale settings
     * for the calendar.
     *
     * @deprecated as of Version 7.4.  This method didn't set the locale
     * correctly, which would cause the first day of the week to be wrong after
     * setting a user in a different locale.
     */
    public void setUser(net.project.security.User user) {
        this.user = user;
        setTimeZone(user.getTimeZone());
        this.space = user.getCurrentSpace();
    }

    /**
     * Returns the user context for this calendar.
     * @return the user specified by {@link #setUser} or the current user
     * if none was set
     */
    public User getUser() {
        if (user == null)
            return net.project.security.SessionManager.getUser();
        else
            return user;
    }

    /**
     * Returns the ID of this calendar.
     * @return the ID of the calendar; only available after calling {@link #load}
     */
    public String getID() {
        return id;
    }

    /**
     * Sets the space context for this calendar.
     * Used when loading a calendar; the default calendar for the space is
     * loaded.
     * @param space the current space
     * @see #getSpace
     */
    public void setSpace(Space space) {
        this.space = space;
    }

    /**
     * Returns the current space.
     * @return the current space
     * @see #setSpace
     */
    public Space getSpace() {
        return this.space;
    }

    /**
     * Returns the ID of the current space.
     * @return the space ID
     * @see #setSpace
     */
    public String getSpaceId() {
        return this.space.getID();
    }

    /**
     * Sets the space of this calendar based on the specified ID.
     * This constructs a space of the appropriate type, but does not load it.
     * @param spaceID the ID of the space to set
     */
    public void setSpaceId(String spaceID) {
        try {
    	    Space space = SpaceFactory.constructSpaceFromID(spaceID);
            this.space = space;
            this.space.setID(spaceID);
        } catch (PersistenceException pe) {
            // Catch it
        }
    }

    /**
     * Specifies the start date used when loading calendar entries.
     * @param startDate the date to use when loading entries.
     * @see #getStartDate
     * @see #loadEntries
     */
    public void setStartDate(java.util.Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Returns the current start date.
     * @return the start date or null if none has been set
     * @see #setStartDate
     */
    public java.util.Date getStartDate() {
        return this.startDate;
    }

    /**
     * Specifies the end date used when loading calendar entries.
     * @param endDate the date to use when loading entries.
     * @see #getEndDate
     * @see #loadEntries
     */
    public void setEndDate(java.util.Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Returns the current end date.
     * @return the end date or null if none has been set
     * @see #setEndDate
     */
    public java.util.Date getEndDate() {
        return this.endDate;
    }

    /**
     * Returns the day of the week.
     *
     * @param date the date we want to know to which day of week matches
     * @return day of week
     * @since 8.3.0
     */
    public int getDayOfWeek(long date) {
    	PnCalendar tempCalendar = new PnCalendar(this.user);       
    	tempCalendar.setTimeInMillis(date);
    	return (tempCalendar.get(PnCalendar.DAY_OF_WEEK));
    }
    
    /**
     * Returns a date set to the beginning of the current day (midnight).
     * Seconds and milliseconds are zero.
     * Does not modify this calendar.
     * @return the date set to midnight of today
     */
    public Date startOfDay() {
        return startOfDay(currentTime());
    }

    /**
     * Returns a date set to the beginning of the day (midnight) using the
     * specified date to supply the year, month and day.
     * Does not modify this calendar.
     * @param date the date that supplies the year, month and day
     * @return the date set to midnight on the same year month and day
     */
    public Date startOfDay(Date date) {
        PnCalendar tempCalendar = new PnCalendar(user);
        tempCalendar.setTime(date);
        DateUtils.zeroTime(tempCalendar);

        return tempCalendar.getTime();
    }

    /**
     * Returns a date set to the beginning of the current week.
     * The first day of the week depends on the current user's locale.
     * The time is set to midnight on that day.
     * Seconds and milliseconds are zero.
     * Does not modify this calendar.
     * @return the date set to midnight on the first day of the current week
     */
    public Date startOfWeek() {
        return startOfWeek(currentTime());
    }

    /**
     * Returns a date set to the beginning of the week using the specified date
     * to supply the year month and day from which to determine the start of
     * the week.
     * Does not modify this calendar.
     * @param date the date that supplies the year, month and day from which
     * to determine the first day of the week
     * @return the date set to midnight on the first day of the week
     */
    public Date startOfWeek(Date date) {
        PnCalendar tempCalendar = new PnCalendar(user);
        tempCalendar.setTime(date);
        DateUtils.zeroTime(tempCalendar);

        // set cal to last day of this month.
        tempCalendar.set(DAY_OF_WEEK, getFirstDayOfWeek());

        return tempCalendar.getTime();
    }

    /**
     * Returns a date set to the first day of the current month.
     * The time is set to midnight.
     * Does not modify this calendar.
     * @return the date set to midnight on the first day of this month
     */
    public Date startOfMonth() {
        return startOfMonth(currentTime());
    }

    /**
     * Returns a date set to the first day of the month using the specified
     * date to supply the year and month.
     * Does not modify this calendar.
     * @param date the date used to supply the year and month from which to
     * determine the first day of the month
     * @return the date set to midnight on the first day of the month
     */
    public Date startOfMonth(Date date) {
        PnCalendar tempCalendar = new PnCalendar(user);
        tempCalendar.setTime(date);

        return startOfMonth(tempCalendar.get(MONTH), tempCalendar.get(YEAR));
    }

    /**
     * Returns a date set to the first day of the specified month.
     * Does not modify this calendar.
     * @param month the month for which to get the first day; this is a value
     * from zero (January) to 11 (December)
     * @param year the year used to determine the first day of the month; this
     * should be a 4 digit year
     * @return the date set to midnight on the first day of the month
     */
    public Date startOfMonth(int month, int year) {
        // make sure the calendar is set to today.
        PnCalendar tempCalendar = new PnCalendar(user);
        tempCalendar.setTime(currentTime());
        DateUtils.zeroTime(tempCalendar);

        // set cal to last day of this month.
        tempCalendar.set(MONTH, month);
        tempCalendar.set(YEAR, year);
        tempCalendar.set(DAY_OF_MONTH, 1);

        return tempCalendar.getTime();
    }

    /**
     * Returns a date set to the first day of the first month of the current
     * year.
     * Does not modify this calendar.
     * @return the date set to midnight on the first day of the current year
     */
    public Date startOfYear() {
        PnCalendar tempCalendar = new PnCalendar(user);
        tempCalendar.setTime(currentTime());

        return startOfYear(tempCalendar.get(YEAR));
    }

    /**
     * Returns a date set to the first day of the first month of the specified
     * year.
     * Does not modify this calendar.
     * @param year the year to get the first day of; this is a 4 digit year
     * @return the date set to midnight on the first day of the specified year
     */
    public Date startOfYear(int year) {
        PnCalendar tempCalendar = new PnCalendar(user);
        tempCalendar.setTime(currentTime());
        DateUtils.zeroTime(tempCalendar);

        // set cal to first month and day of the year.
        tempCalendar.set(YEAR, year);
        tempCalendar.set(MONTH, getMinimum(MONTH));
        tempCalendar.set(DAY_OF_MONTH, 1);
        Date returnVal = tempCalendar.getTime();

        return returnVal;
    }

    /**
     * Returns a date that is set to 1 millisecond before midnight of tomorrow.
     * Does not modify this calendar.
     * @return a date based on today with the time portion set to 23:59:59.999
     */
    public Date endOfDay() {
        return endOfDay(getTime());
    }

    /**
     * Returns a date that is set to 1 millisecond before midnight of the
     * day following the day represented by the specified date.
     * Does not modify this calendar.
     * @param date the date for which to determine the end of the day
     * @return a date based on the day of the specified date with the time
     * portion set to 23:59:59.999
     */
    public Date endOfDay(Date date) {
        PnCalendar tempCalendar = new PnCalendar(user);
        tempCalendar.setTime(date);
        DateUtils.setTime(tempCalendar, 23, 59, 59, 999);

        return tempCalendar.getTime();
    }

    /**
     * Returns a date that is set to 1 millisecond before midnight on the
     * last day of the current week.
     * Does not modify this calendar.
     * @return a date set to the last day of the current week with the time
     * set to 23:59:59.999
     */
    public Date endOfWeek() {
        return endOfWeek(currentTime());
    }

    /**
     * Returns a date set to 1 millisecond before midnight on the last day of
     * the week that the specified date represents.
     * Does not modify this calendar.
     * @param date the date that supplies the current week
     * @return a date set to the last day of the week in which the specified
     * date occurs with the time set to 23:59:59.999
     */
    public Date endOfWeek(Date date) {
        PnCalendar tempCalendar = new PnCalendar(user);
        tempCalendar.setTime(date);
        DateUtils.setTime(tempCalendar, 23, 59, 59, 999);

        // set cal to last day of this month.
        tempCalendar.set(DAY_OF_WEEK, getMaximum(DAY_OF_WEEK));

        return tempCalendar.getTime();
    }

    /**
     * Returns a date set to 1 millisecond before midnight on the last day
     * of the current month.
     * Does not modify this calendar.
     * @return a date set to the last day of the current month with the time
     * set to 23:59:59.999
     */
    public Date endOfMonth() {
        return endOfMonth(currentTime());
    }

    /**
     * Returns a date set to 1 millisecond before midnight on the last day of
     * the month that the specified date represents.
     * Does not modify this calendar.
     * @param date a <code>Date</code> object for which we are going to find the
     * last day of the month.
     * @return a <code>Date</code> object set to the last day, hour, minute,
     * second and millisecond of the month of the date passed into this method.
     */
    public Date endOfMonth(Date date) {
        PnCalendar tempCalendar = new PnCalendar(user);
        tempCalendar.setTime(date);

        return endOfMonth(tempCalendar.get(MONTH), tempCalendar.get(YEAR));
    }

    /**
     * Returns a date set to 1 millisecond before midnight on the last day
     * of the specified month and year.
     * Does not modify this calendar.
     * @param month the month for which to get the last day; this is a value
     * from zero (January) to 11 (December)
     * @param year the year used to determine the last day of the month; this
     * should be a 4 digit year
     * @return a date set to the last day of the specified month with the time
     * set to 23:59:59.999
     */
    public Date endOfMonth(int month, int year) {
        PnCalendar tempCalendar = new PnCalendar(user);
        tempCalendar.setTime(currentTime());
        DateUtils.setTime(tempCalendar, 23, 59, 59, 999);

        // set cal to last day of this month.
        tempCalendar.set(MONTH, month);
        tempCalendar.set(YEAR, year);
        tempCalendar.set(DAY_OF_MONTH, getActualMaximum(DAY_OF_MONTH));

        return tempCalendar.getTime();
    }

    /**
     * Returns a date set to 1 millisecond before midnight on the last day of
     * the current year.
     * Does not modify this calendar.
     * @return a date set to the last day of the current year with the time
     * set to 23:59:59.999
     */
    public Date endOfYear() {
        PnCalendar tempCalendar = new PnCalendar(user);
        tempCalendar.setTime(currentTime());

        return endOfYear(tempCalendar.get(YEAR));
    }

    /**
     * Returns a date set to 1 millisecond before midnight on the last day of
     * the specified year.
     * Does not modify this calendar.
     * @param year the year for which to get the last day; this is a 4 digit
     * year
     * @return a date set to the last day of the specified year with the time
     * set to 23:59:59.999
     */
    public Date endOfYear(int year) {
        // make sure the calendar is set to today.
        PnCalendar tempCalendar = new PnCalendar(user);
        tempCalendar.setTime(currentTime());
        DateUtils.setTime(tempCalendar, 23, 59, 59, 999);

        // set cal to last day of this month.
        tempCalendar.set(YEAR, year);
        tempCalendar.set(DAY_OF_YEAR, getActualMaximum(DAY_OF_YEAR));
        java.util.Date returnVal = tempCalendar.getTime();

        return returnVal;
    }

    /**
     * Returns the calendar quarter based on the specified date.
     * @param date the date for which to determine the quarter
     * @return the quarter for the date
     */
    public CalendarQuarter getQuarter(Date date) {
        PnCalendar tempCalendar = new PnCalendar(user);
        tempCalendar.setTime(date);

        byte iquarter;
        if (tempCalendar.get(PnCalendar.MONTH) < 3) {
            iquarter = 0;
        } else if (tempCalendar.get(PnCalendar.MONTH) < 6) {
            iquarter = 1;
        } else if (tempCalendar.get(PnCalendar.MONTH) < 9) {
            iquarter = 2;
        } else {
            iquarter = 3;
        }

        return new CalendarQuarter(iquarter, tempCalendar.get(PnCalendar.YEAR));
    }

    /**
     * Get midnight on day you'd get from {@link #getTime()}.
     *
     * @return a <code>Date</code> containing the current date of the PnCalendar
     * at midnight.
     */
    public Date getMidnight() {
        // make sure the calendar is set to today.
        PnCalendar tempCalendar = new PnCalendar(user);
        tempCalendar.setTime(getTime());
        DateUtils.zeroTime(tempCalendar);

        // set cal to last day of this month.
        java.util.Date returnVal = tempCalendar.getTime();

        return returnVal;
    }

    /**
     * Get midnight on a given day.
     *
     * @param date <code>Date</code> for which we are going to find midnight.
     * @return a <code>Date</code> containing the date parameter at midnight.
     */
    public Date getMidnight(Date date) {
        // make sure the calendar is set to today.
        PnCalendar tempCalendar = new PnCalendar(user);
        tempCalendar.setTime(date);
        DateUtils.zeroTime(tempCalendar);

        // set cal to last day of this month.
        java.util.Date returnVal = tempCalendar.getTime();

        return returnVal;
    }

    /**
     * Returns a date exactly one day prior to this calendar's current date.
     * The time remains the same.
     * Does not modify this calendar.
     * @return a date set to exactly one day prior to this calendar's current date
     */
    public Date getPrevDay() {
        return new java.util.Date(getTime().getTime() - MILLISECONDS_IN_DAY);
    }

    /**
     * Returns a date exactly one day following this calendar's current date.
     * The time remains the same.
     * Does not modify this calendar.
     * @return a date set to exactly one day following this calendar's current date
     */
    public Date getNextDay() {
        return new java.util.Date(getTime().getTime() + MILLISECONDS_IN_DAY);
    }

    /**
     * Returns a date exactly one week prior to this calendar's current date.
     * The time remains the same.
     * Does not modify this calendar.
     * @return a date set to exactly one week prior to this calendar's current date
     */
    public Date getPrevWeek() {
        return new java.util.Date(getTime().getTime() - MILLISECONDS_IN_WEEK);
    }

    /**
     * Returns a date exactly one week following this calendar's current date.
     * The time remains the same.
     * Does not modify this calendar.
     * @return a date set to exactly one week following this calendar's current date
     */
    public Date getNextWeek() {
        return new java.util.Date(getTime().getTime() + MILLISECONDS_IN_WEEK);
    }

    /**
     * Returns a date set to the same day in the previous month based on this
     * calendar's current date.
     * The previous month may be in the previous year.
     * If the previous month does not support the same number of days and the
     * current day exceeds the number of days in the previous month, then the day
     * is set to the last day of the previous month.
     * The time is reset.
     * @return a date set to midnight on the same day of the previous month
     * or the last day of the previous month if there are too few days in the
     * previous month
     */
    public Date getPrevMonth() {
        int iMonth = get(MONTH);
        int iDay = get(DAY_OF_MONTH);
        int iYear = get(YEAR);

        int iPrevMonth = iMonth;
        int iPrevYear = iYear;
        if (iPrevMonth == 0) {
            iPrevMonth = 12;
            iPrevYear--;
        }

        PnCalendar tempCal = new PnCalendar();
        tempCal.clear();
        tempCal.set(MONTH, iPrevMonth - 1);
        int iPrevMonthDay = iDay;
        if (iPrevMonthDay > tempCal.getActualMaximum(DAY_OF_MONTH))
            iPrevMonthDay = tempCal.getActualMaximum(DAY_OF_MONTH);
        tempCal.set(DAY_OF_MONTH, iPrevMonthDay);
        tempCal.set(YEAR, iPrevYear);

        return tempCal.getTime();
    }

    /**
     * Returns a date set to the same day in the next month based on this
     * calendar's current date.
     * The next month may be in next year.
     * If the next month does not support the same number of days and the
     * current day exceeds the number of days in next month then the day
     * is set to the last day of next month.
     * The time is reset.
     * @return a date set to midnight on the same day of the next month
     * or the last day of the next month if there are too few days in the
     * next month
     */
    public Date getNextMonth() {
        int iMonth = get(MONTH);
        int iDay = get(DAY_OF_MONTH);
        int iYear = get(YEAR);

        int iNextMonth = ((iMonth + 1) % 12) + 1;
        int iNextYear = iYear;
        if (iNextMonth == 1)
            iNextYear++;

        PnCalendar tempCal = new PnCalendar();
        tempCal.clear();
        tempCal.set(PnCalendar.MONTH, iNextMonth - 1);
        int iNextMonthDay = iDay;
        if (iNextMonthDay > tempCal.getActualMaximum(PnCalendar.DAY_OF_MONTH))
            iNextMonthDay = tempCal.getActualMaximum(PnCalendar.DAY_OF_MONTH);
        tempCal.set(DAY_OF_MONTH, iNextMonthDay);
        tempCal.set(YEAR, iNextYear);

        return tempCal.getTime();
    }

    /**
     * Returns a date set to the same day and month in the previous year.
     * If the same month last year does not contain the same number of days
     * and the current day exceeds the number of days in the month last year
     * then the day is set to the last day of the month.
     * @return a date set to midnight on the same day of the month last year
     * or the last day of the month if there are too few days in the month
     * last year
     */
    public Date getPrevYear() {
        int iMonth = get(MONTH);
        int iDay = get(DAY_OF_MONTH);
        int iYear = get(YEAR);

        int iPrevYear = iYear - 1;

        PnCalendar tempCal = new PnCalendar();
        tempCal.clear();
        tempCal.set(YEAR, iPrevYear);
        tempCal.set(MONTH, iMonth);
        int iPrevYearDay = iDay;
        if (iPrevYearDay > tempCal.getActualMaximum(DAY_OF_MONTH))
            iPrevYearDay = tempCal.getActualMaximum(DAY_OF_MONTH);
        tempCal.set(DAY_OF_MONTH, iPrevYearDay);

        return tempCal.getTime();
    }

    /**
     * Returns a date set to the same day and month in next year.
     * If the same month next year does not contain the same number of days
     * and the current day exceeds the number of days in the month next year
     * then the day is set to the last day of the month.
     * @return a date set to midnight on the same day of the month next year
     * or the last day of the month if there are too few days in the month
     * next year
     */
    public Date getNextYear() {
        int iMonth = get(MONTH);
        int iDay = get(DAY_OF_MONTH);
        int iYear = get(YEAR);

        int iNextYear = iYear + 1;

        PnCalendar tempCal = new PnCalendar();
        tempCal.clear();
        tempCal.set(YEAR, iNextYear);
        tempCal.set(MONTH, iMonth);
        int iNextYearDay = iDay;
        if (iNextYearDay > tempCal.getActualMaximum(DAY_OF_MONTH))
            iNextYearDay = tempCal.getActualMaximum(DAY_OF_MONTH);
        tempCal.set(DAY_OF_MONTH, iNextYearDay);

        return tempCal.getTime();
    }

    /**
     * Get the XML representation of this calendar.
     * The XML document will contain the properties of the calendar and nodes for each ICalendarEntry.
     *
     * @return XML representation of the calendar.
     * @throws SQLException 
     */
    public String getXML() throws SQLException {
        ICalendarEntry entry;
        StringBuffer xml = new StringBuffer();
        xml.ensureCapacity(1000);

        //if ((calendarEntries == null) || (calendarEntries.size() < 1))
        //    return "empty";

        xml.append(net.project.persistence.IXMLPersistence.XML_VERSION);
        xml.append("<calendar>\n");

        xml.append("<name>");
        xml.append(XMLUtils.escape(name));
        xml.append("</name>\n");

        xml.append("<description>");
        xml.append(XMLUtils.escape(description));
        xml.append("</description>\n");

        xml.append("<start_date>");
        xml.append(XMLUtils.formatISODateTime(startDate));
        xml.append("</start_date>\n");

        xml.append("<end_date>");
        xml.append(XMLUtils.formatISODateTime(endDate));
        xml.append("</end_date>\n");

        for (int i = 0; i < calendarEntries.size(); i++) {
            entry = (ICalendarEntry)calendarEntries.get(i);
            xml.append(entry.getXMLBody());
        }

        xml.append("</calendar>\n");
        return xml.toString();
    }

    /**
     * Loads calendar entries of the specified types.
     * Performance consideration: a single database query is performed for each type.
     * Facilities are loaded by default.
     * @param entryTypes an array of Strings indicating which types of calendar
     * entries should be loaded.
     */
    public void loadEntries(String[] entryTypes) throws PersistenceException {
        loadEntries(entryTypes, true);
    }

    /**
     * Loads calendar entries of the specified types, loading facilities too.
     * <p>
     * When the current space is the personal space, entries are loaded for
     * this calendar and the calendars of all Active spaces the user is a member of.
     * Otherwise, only entries for the this calendar are loaded.
     * </p>
     * @param entryTypes an array of Strings indicating which types of calendar
     * entries should be loaded.
     * @param isFacilityIncluded true if facilities should be loaded; false
     * otherwise.  Current mechanism is to load each facility individually,
     * resulting in a lot of database overhead
     */
    public void loadEntries(String[] entryTypes, boolean isFacilityIncluded) throws PersistenceException {
        // new list about to be extracted.
        calendarEntries.clear();

        ArrayList calendarIdList = new ArrayList();

        if (space.getID().equals(user.getID())) {
            // It is the personal space
            // The user may see all entries in this calendar and also calendars
            // of any space that they are a member of

            // Start with the this calendar (which belongs to the personal space)
            // since the the current space is the personal space
            calendarIdList.add(this.id);

            // Now add all the other calendars from the user's other spaces
            DBBean db = new DBBean();
            try {
                // This query will locate calendar IDs for the spaces in which
                // the user is a member but only for Active spaces
                // The "exists" mechanism (rather than straight join) avoids
                // full table scans of all space tables
                final String allCalendarsQuery =
                        "select shc.calendar_id " +
                        "  from pn_space_has_person shp, pn_space_has_calendar shc " +
                        " where shp.person_id = ? " +
                        "   and shc.space_id = shp.space_id " +
                        "   and exists ( select 1 " +
                        "                  from pn_space_view sv " +
                        "                 where sv.space_id = shp.space_id and sv.record_status = 'A') ";

                db.prepareStatement(allCalendarsQuery);
                db.pstmt.setString(1, user.getID());
                db.executePrepared();

                while (db.result.next()) {
                    String resultCID = db.result.getString(1);
                    calendarIdList.add(resultCID);
                }

            } catch (SQLException sqle) {
                throw new PersistenceException("PnCalendar.loadEntries() threw " +
                    "a SQL exception: " + sqle, sqle);
            } finally {
                db.release();
            }

        } else {
            // Not the personal space; limit the calendars to the current calendar
            calendarIdList.add(this.id);
        }


        for (int i = 0; i < entryTypes.length; i++) {
            if (entryTypes[i].equals("event"))
                loadEventEntries(calendarIdList, isFacilityIncluded);
            if (entryTypes[i].equals("meeting"))
                loadMeetingEntries(calendarIdList, isFacilityIncluded);
            if (entryTypes[i].equals("task"))
                loadTaskEntries();
            if (entryTypes[i].equals("milestone"))
                loadMilestoneEntries();
        }
    }

    /**
     * Load all the event entries within the daterange set by setStartDate() and setEndDate().
     * @param calendarIdList the collection of calendar IDs in which to look
     * for events.
     * @param isFacilityIncluded true if facilities should be loaded; false
     * otherwise.  Current mechanism is to load each facility individually,
     * resulting in a lot of database overhead
     */
    private void loadEventEntries(ArrayList calendarIdList, boolean isFacilityIncluded) throws PersistenceException {

        CalendarEvent event = null;
        StringBuffer query = new StringBuffer();
        int numCalendarIds = 0;
        int calendarIdIndex = 0;

        // get the events on this calendar
        query.append("select e.calendar_event_id, e.event_name, e.event_desc_clob, e.frequency_type_id, ");
        query.append("gd1.code_name as frequency, e.facility_id, e.event_purpose_clob, e.start_date, e.end_date ");

        query.append("from ");

        // Now select only distinct events across all calendars
        // This inner query is named as "distinct_events"
        query.append("(select distinct calendar_event_id from pn_calendar_has_event che ");
        query.append("where ( ");
        while (numCalendarIds < calendarIdList.size()) {
            query.append("che.calendar_id = ? ");
            if (++numCalendarIds != calendarIdList.size())
                query.append(" or ");
        }
        query.append(") ").append(") distinct_events, ");

        // Remaining tables in from clause
        query.append("pn_calendar_event e, pn_global_domain gd1 ");

        query.append("where e.calendar_event_id = distinct_events.calendar_event_id ");
        query.append("and e.event_type_id = ").append(EVENT).append(" ");
        query.append("and e.start_date >= ? and e.start_date <= ? and e.record_status = 'A' ");
        query.append("and gd1.table_name = 'pn_calendar_event' and gd1.column_name = 'frequency_type_id' ");
        query.append("and gd1.code = e.frequency_type_id ");

        query.append("order by e.start_date asc");

        DBBean db = new DBBean();
        try {
            db.prepareStatement(query.toString());
            java.sql.Timestamp sqlStartDate = new Timestamp(startDate.getTime());
            java.sql.Timestamp sqlEndDate = new Timestamp(endDate.getTime());

            for (calendarIdIndex = 0; calendarIdIndex < numCalendarIds; calendarIdIndex++) {
                db.pstmt.setString(calendarIdIndex + 1,
                    (String)calendarIdList.get(calendarIdIndex));
            }

            db.pstmt.setTimestamp(++calendarIdIndex, sqlStartDate);
            db.pstmt.setTimestamp(++calendarIdIndex, sqlEndDate);
            db.executePrepared();

            while (db.result.next()) {
                event = new CalendarEvent(this);

                event.eventId = db.result.getString("calendar_event_id");
                event.name = db.result.getString("event_name");
                event.description = ClobHelper.read(db.result.getClob("event_desc_clob"));
                event.frequencyTypeId = db.result.getString("frequency_type_id");
                event.frequency = PropertyProvider.get(db.result.getString("frequency"));
                if (isFacilityIncluded) {
                    event.facility = facilityFactory.make(db.result.getString("facility_id"));
                }
                event.purpose = ClobHelper.read(db.result.getClob("event_purpose_clob"));
                event.startTime = db.result.getTimestamp("start_date");
                event.endTime = db.result.getTimestamp("end_date");

                calendarEntries.add(event);
            }

        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("could not load events", sqle);

        } finally {
            db.release();
        }

    }


    /**
     * Load all the meeting entries within the daterange set by setStartDate() and setEndDate().
     * @param calendarIdList the collection of calendar IDs in which to look
     * for events.
     * @param isFacilityIncluded true if facilities should be loaded; false
     * otherwise.  Current mechanism is to load each facility individually,
     * resulting in a lot of database overhead
     */
    public void loadMeetingEntries(ArrayList calendarIdList, boolean isFacilityIncluded)
            throws PersistenceException {

        Meeting meeting = null;
        int numCalendarIds = 0;
        int calendarIdIndex = 0;

        StringBuffer query = new StringBuffer();
        query.append("select e.calendar_event_id, m.meeting_id, e.event_name, e.event_desc_clob, ");
        query.append("e.frequency_type_id, gd1.code_name as frequency, ");
        query.append("e.facility_id, e.event_purpose_clob, e.start_date, e.end_date, m.next_agenda_item_seq, ");
        query.append("gd2.code_name as attendee_status_token ");

        query.append("from ");

        // Now select only distinct events across all calendars
        // This inner query is named as "distinct_events"
        query.append("(select distinct calendar_event_id from pn_calendar_has_event che ");
        query.append("where ( ");
        while (numCalendarIds < calendarIdList.size()) {
            query.append("che.calendar_id = ? ");
            if (++numCalendarIds != calendarIdList.size())
                query.append(" or ");
        }
        query.append(") ").append(") distinct_events, ");

        // Remaining tables in from clause
        // We're joining with pn_cal_event_has_attendee to get the current
        // user's status in each event
        // If the current user is not invited (which means the current user
        // is a space admin) the status will be empty
        query.append("pn_calendar_event e, pn_meeting m, pn_global_domain gd1, pn_global_domain gd2, pn_cal_event_has_attendee attendee ");

        // Where clause includes only meetings between current start and end dates
        query.append("where e.calendar_event_id = distinct_events.calendar_event_id ");
        query.append("and e.event_type_id = ").append(MEETING).append(" ")
                .append("and m.calendar_event_id = e.calendar_event_id and e.start_date >= ? ")
                .append("and e.start_date <= ? and e.record_status = 'A' ")
                .append("and gd1.table_name = 'pn_calendar_event' and gd1.column_name = 'frequency_type_id' ")
                .append("and gd1.code = e.frequency_type_id ");
        // Outer join against table since space admins may not be invited
        // to the meetings in question
        query.append("and attendee.calendar_event_id(+) = e.calendar_event_id ")
                .append("and attendee.person_id(+) = ? ")
                .append("and gd2.table_name(+) = 'pn_cal_event_has_attendee' and gd2.column_name(+) = 'status_id' ")
                .append("and gd2.code(+) = attendee.status_id ");

        // Now decide how to limit the meetings
        // If the person is a space admin and the current space is not the
        // personal space, then we show all meetings regardless of attendees
        // This allows space admins to see all meetings in a space
        // If the person is not a space admin, we limit the meetings to those
        // they are attendees of
        // Similarly, for space admins in the personal space, we limit the meetings
        // since there would be far too many items if we choose to display ALL
        // meetings across all spaces
        boolean isLimitedToAttendees = (!isSpaceAdministrator() || space.getID().equals(user.getID()));

        if (isLimitedToAttendees) {
            // Limit to those they are attending
            query.append("and exists (")
                    .append("select 1 from pn_cal_event_has_attendee ceha ")
                    .append("where ceha.calendar_event_id = e.calendar_event_id ")
                    .append("and ceha.status_id != 30 and ceha.person_id = ?)");

        }

        query.append("order by e.start_date asc");

        DBBean db = new DBBean();
        try {
            db.prepareStatement(query.toString());

            for (calendarIdIndex = 0; calendarIdIndex < numCalendarIds; calendarIdIndex++) {
                db.pstmt.setString(calendarIdIndex + 1,
                        (String) calendarIdList.get(calendarIdIndex));
            }
            db.pstmt.setTimestamp(++calendarIdIndex, new Timestamp(startDate.getTime()));
            db.pstmt.setTimestamp(++calendarIdIndex, new Timestamp(endDate.getTime()));
            db.pstmt.setString(++calendarIdIndex, user.getID());

            if (isLimitedToAttendees) {
                // Bind the user's ID to limit to meetings where user is an
                // attendee
                db.pstmt.setString(++calendarIdIndex, user.getID());
            }

            db.executePrepared();

            while (db.result.next()) {
                meeting = new Meeting(this);

                meeting.setEventId(db.result.getString("calendar_event_id"));
                meeting.meetingID = db.result.getString("meeting_id");
                meeting.name = db.result.getString("event_name");
                meeting.description = ClobHelper.read(db.result.getClob("event_desc_clob"));
                meeting.frequencyTypeId = db.result.getString("frequency_type_id");
                meeting.frequency = PropertyProvider.get(db.result.getString("frequency"));
                if (isFacilityIncluded) {
                    meeting.facility = facilityFactory.make(db.result.getString("facility_id"));
                }
                meeting.purpose = ClobHelper.read(db.result.getClob("event_purpose_clob"));
                meeting.startTime = db.result.getTimestamp("start_date");
                meeting.endTime = db.result.getTimestamp("end_date");

                // If the current user is an attendee of the meeting
                // We set their display status
                String currentUserAttendeeStatusToken = db.result.getString("attendee_status_token");
                if (currentUserAttendeeStatusToken != null) {
                    meeting.setCurrentUserAttendeeStatus(PropertyProvider.get(currentUserAttendeeStatusToken));
                }

                calendarEntries.add(meeting);
            }

        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("could not load meetings: " + sqle, sqle);

        } finally {
            db.release();
        }
    }

    /**
     * Load all the task entries within the daterange set by setStartDate() and setEndDate().
     *
     * If in the personal space, display all tasks assigned to you in every project you
     * are a member of, but if in the project space, display all of the tasks that exist
     * within a project.
     */
    private void loadTaskEntries()
        throws PersistenceException {


        // Is the user in their personal space?
        if (space.getID().equals(user.getID())) {

            StringBuffer query = new StringBuffer();

            // Get the task_id's for the users assigned tasks in every project they
            // are a member of
            query.append("SELECT t.task_id ");
            query.append("FROM pn_task t, pn_assignment a, pn_object sp ");
            query.append("WHERE a.object_id = t.task_id ");
            query.append("AND a.person_id = " + user.getID() + " ");
            query.append("AND ( t.date_start >= ? and t.date_start <= ? ) ");
            query.append("AND ((t.task_type = '" + TaskType.SUMMARY.getID() + "') or (t.task_type = '"+TaskType.TASK.getID() + "'))");
            query.append("AND (t.is_milestone = 0) ");
            query.append("AND a.status_id != '" + Assignment.REJECTED + "' ");
            query.append("AND t.record_status = 'A' AND a.record_status = 'A' ");
            query.append("AND sp.object_id = a.space_id ");
            query.append("AND sp.record_status = 'A' ");

            DBBean db = new DBBean();
            try {
                db.prepareStatement(query.toString());

                java.sql.Timestamp sqlStartDate = new Timestamp(startDate.getTime());
                java.sql.Timestamp sqlEndDate = new Timestamp(endDate.getTime());
                db.pstmt.setTimestamp(1, sqlStartDate);
                db.pstmt.setTimestamp(2, sqlEndDate);

                db.executePrepared();

                ArrayList taskEntries = new ArrayList(50);

                while (db.result.next()) {

                    String task_id = db.result.getString(1);
                    ScheduleEntry task = new Task();
                    task.setID(task_id);
                    task.load();

                    taskEntries.add(task);
                }

                calendarEntries.addAll(taskEntries);

            } catch (java.sql.SQLException sqle) {
                throw new PersistenceException("could not load meetings", sqle);

            } finally {
                db.release();

            }
        } else { // Not in personal space
            TaskFinder tf = new TaskFinder();
            DateRange startDateRange = new DateRange(startDate, endDate);
            tf.addFinderFilter(startDateRange.getDateFilter("startDateRange",
                TaskFinder.DATE_START_COLUMN, false));

            //Only load tasks
            TaskTypeFilter ttf = new TaskTypeFilter("ttf");
            ttf.setSelected(true);
            ttf.setLoadMilestones(false);
            ttf.setLoadTasks(true);
            tf.addFinderFilter(ttf);

            calendarEntries.addAll(tf.findBySpaceID(space.getID()));
        }
    }

    /**
     * Load all the milestone entries within the daterange set by setStartDate() and setEndDate().
     */
    private void loadMilestoneEntries() throws PersistenceException {
        // Is the user in their personal space?
        if (space.getID().equals(user.getID())) {

            StringBuffer query = new StringBuffer();

            // Get the task_id's for the users assigned tasks in every project they
            // are a member of
            query.append("SELECT t.task_id ");
            query.append("FROM pn_task t, pn_assignment a, pn_object sp ");
            query.append("WHERE a.object_id = t.task_id ");
            query.append("AND a.person_id = " + user.getID() + " ");
            query.append("AND ( t.date_start >= ? and t.date_start <= ? ) ");
            query.append("AND t.is_milestone = 1");
            query.append("AND t.record_status = 'A' and a.record_status = 'A' ");
            query.append("AND a.space_id = sp.object_id ");
            query.append("AND sp.record_status = 'A' ");

            DBBean db = new DBBean();
            try {
                db.prepareStatement(query.toString());

                java.sql.Timestamp sqlStartDate = new Timestamp(startDate.getTime());
                java.sql.Timestamp sqlEndDate = new Timestamp(endDate.getTime());
                db.pstmt.setTimestamp(1, sqlStartDate);
                db.pstmt.setTimestamp(2, sqlEndDate);

                db.executePrepared();

                ArrayList taskEntries = new ArrayList(50);

                while (db.result.next()) {

                    String task_id = db.result.getString(1);
                    ScheduleEntry task = new Task();
                    task.setID(task_id);
                    task.load();

                    taskEntries.add(task);
                }

                calendarEntries.addAll(taskEntries);

            } catch (java.sql.SQLException sqle) {
                throw new PersistenceException("could not load meetings" + sqle, sqle);

            } finally {
                db.release();

            }


        }
        // Not in personal space
        else {
            TaskFinder tf = new TaskFinder();

            //Only load milestones in a given range
            DateRange startDateRange = new DateRange(startDate, endDate);
            tf.addFinderFilter(startDateRange.getDateFilter("startDateRange",
                TaskFinder.DATE_START_COLUMN, false));

            //Only load milestones
            TaskTypeFilter ttf = new TaskTypeFilter("ttf");
            ttf.setSelected(true);
            ttf.setLoadMilestones(true);
            ttf.setLoadTasks(false);
            tf.addFinderFilter(ttf);

            calendarEntries.addAll(tf.findBySpaceID(space.getID()));
        }
    }

    /**
     * Delete the CalendarEntry with the specified ID from the database.
     */
    public void removeEntry(String Id)
        throws PersistenceException {
        ICalendarEntry entry;
        for (int i = 0; i < calendarEntries.size(); i++) {
            entry = (ICalendarEntry)calendarEntries.get(i);
            if (entry.getID().equals(Id)) {
                // remove from database
                entry.remove();
                // remove from ArrayList (memory)
                calendarEntries.remove(entry);
            }
        }
    }

    /**
     * Returns the current loaded entries.
     * @return a list where each element is an {@link ICalendarEntry}
     * @see #loadEntries
     */
    public List getEntries() {
        return calendarEntries;
    }

    /**
     * Load the calendar properties.
     * Does not load any entries.
     */
    private void load() throws PersistenceException {
        String query = null;

        if ((space == null) || (space.getID() == null))
            throw new NullPointerException("space or space_id was null");

        // get the calendar for this space.
        query = "select c.calendar_id, c.calendar_name, c.calendar_description from pn_space_has_calendar shc, pn_calendar c " +
            "where shc.space_id = " + space.getID() + " and c.calendar_id = shc.calendar_id";

        DBBean db = new DBBean();
        try {
            db.executeQuery(query);
            if (!db.result.next()) {
                throw new PersistenceException("Could not load calendar from database");
            } else {
                loadedSpaceID = space.getID();

                id = db.result.getString(1);
                name = db.result.getString(2);
                description = db.result.getString(3);
            }
        } catch (java.sql.SQLException sqle) {
            sqle.printStackTrace();
            throw new PersistenceException("could not load calendar properties", sqle);
        } finally {
            db.release();
        }

    }


    /**
     * Load the calendar only if it has not yet been loaded, or the last loaded space does
     * not match the current space.
     */
    public void loadIfNeeded()
        throws PersistenceException {
        if ((loadedSpaceID == null) || (!loadedSpaceID.equals(space.getID())))
            load();
    }

    /**
     * Indicates whether the current user is a space administrator in the current
     * space.
     * @return true if the user is a space administrator in the current space;
     * false otherwise
     */
    private boolean isSpaceAdministrator() {

        if (this.spaceAdminCheckedSpaceID == null ||
                !this.spaceAdminCheckedSpaceID.equals(space.getID())) {

            // We haven't checked for space admin priveleges or
            // the space ID has changed since we did

            if (!space.getID().equals(user.getID())) {
                // The space is not the personal space
                // so we check to see if the user is space administrator
                this.isSpaceAdministrator = this.space.isUserSpaceAdministrator(this.user);

            } else {
                // It is the personal space
                // A user is always the space admin of their personal space
                // but we don't use it for that purpose here
                this.isSpaceAdministrator = false;
            }

            // Record the space ID of the space we checked to avoid doing so again
            // until it changes
            this.spaceAdminCheckedSpaceID = this.space.getID();
        }

        return this.isSpaceAdministrator;
    }
}
