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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.InvalidDateException;
import net.project.util.Validator;

/**
 * A presentation bean for use on JSP pages when displaying a time input control.
 * It also provides method for parsing times from the input control.
 * <p>
 * The following example will draw the input elements with calendar-style minute
 * selection (00, 15, 30, 45) prefixing each element name with <i>fieldname</i>.
 * The selection list values will be defaulted to the current time:<pre><code>
 *     TimeBean timeBean = new TimeBean();
 *     timeBean.setTag("fieldname");
 *     timeBean.setDate(new Date());
 *     timeBean.setMinuteStyle(TimeBean.MINUTE_STYLE_CALENDAR);
 *     out.print(timeBean.getPresentation());
 * </code></pre>
 * </p>
 *
 * @author AdamKlatzkin
 * @author Tim Morrow
 * @since Version 1
 */
public class TimeBean {

    //
    // Static Members
    //

    private static final String SELECTED = "SELECTED";
    private static final String EMPTY = "";

    /**
     * Normal minute style; minutes are 00..59
     */
    public static final int MINUTE_STYLE_NORMAL = 0;

    /**
     * Calendar minute style; minutes are 00, 15, 30, 45
     */
    public static final int MINUTE_STYLE_CALENDAR = 1;

    /**
     * Returns the date containing the date and time parsed from the request
     * given the names of the date field and time field.
     * <p>
     * Assumes the date time value is mandatory; throws an exception if it cannot be found
     * in the request.
     * </p>
     * @param request the request from which to get the values
     * @param dateFieldName the name of the date field
     * @param timeFieldName the prefix of the time field as passed to the Time Taglib
     * @return the date constructed from the date value and time value (including time zone)
     * @throws IllegalStateException if no date value can be found
     * @throws InvalidDateException if the date value cannot be parsed
     */
    public static Date parseDateTime(HttpServletRequest request, String dateFieldName, String timeFieldName)
            throws InvalidDateException {
        return parseDateTime(request, dateFieldName, timeFieldName, false);
    }

    /**
     * Returns the date containing the date and time parsed from the request
     * given the names of the date field and time field.
     * <p>
     * The value may be optional; if a date value is found, the date value is returned;
     * if time values are found, they are added to the date value
     * </p>
     * @param request the request from which to get the values
     * @param dateFieldName the name of the date field
     * @param timeFieldName the prefix of the time field as passed to the Time Taglib
     * @param isOptional true if the date time is optional and null may be returned; false
     * if mandatory
     * @return the date constructed from the date value and optionally time value (including time zone)
     * or null if no date or time values were found
     * @throws IllegalStateException if not optional and no date or time values can be found
     * @throws InvalidDateException if the date value cannot be parsed
     */
    public static Date parseDateTime(HttpServletRequest request, String dateFieldName, String timeFieldName, boolean isOptional)
            throws InvalidDateException {

        Date returnDate;

        String dateValue = request.getParameter(dateFieldName);
        String hourValue = request.getParameter(timeFieldName + "_hour");
        String minuteValue = request.getParameter(timeFieldName + "_minute");
        String amPmValue = request.getParameter(timeFieldName + "_ampm");
        String timeZoneID = request.getParameter(timeFieldName + "_timeZoneID");

        if (Validator.isBlankOrNull(dateValue)) {

            if (isOptional) {
                returnDate = null;
            } else {
                throw new IllegalStateException("Unable to locate date value from request with name '" + dateFieldName + "'.");
            }

        } else {
            // We got a date value
            if (hourValue == null || hourValue.trim().length() == 0 ||
                    minuteValue == null || minuteValue.trim().length() == 0 ||
                    timeZoneID == null || timeZoneID.trim().length() == 0) {

                if (isOptional) {
                    // We'll just return the date part only
                    returnDate = parseDate(dateValue, timeZoneID);

                } else {
                    throw new IllegalStateException("Unable to locate hour or minute values from request with names '" +
                            timeFieldName + "_hour" + " and " + timeFieldName + "_minute" + " respectively.");

                }

            } else {
                // We have date, hour and minute values
                returnDate = parseDateTime(dateValue, hourValue, minuteValue, amPmValue, timeZoneID);
            }
        }

        return returnDate;
    }

    /**
     * Returns the date containing the time parsed from the request based on
     * the specified input name, updating the specified date.
     * <p>
     * <b>Note:</b> {@link TimeBean#parseDateTime} is recommended to ensure the
     * date is parsed using the correct time zone.
     * </p>
     * <p>
     * Equivalent to calling <code>parseTime(request, name, date, false)</code>.
     * </p>
     * @param request the request from which to read the time components; it
     * is assumed that up to 3 parameters are present:
     * <ul>
     * <li><code><i>name</i>_hour</code> - the hour value
     * <li><code><i>name</i>_minute</code> - the minute value
     * <li><code><i>name</i>_ampm</code> - (optional) the ampm value
     * </ul>
     * where <code>name</code> is the name specified in the next parameter
     * @param name the name given to the time input components in the request
     * @param date the date to which to add the time; this <b>must</b> have been
     * parsed using the same time zone as submitted by the time fields; otherwise
     * the resultant date will be incorrect
     * @return the parsed date; the value includes the date portion from
     * the specified date and the time as parsed from the request
     * @throws IllegalStateException if the hour or minute values are not found
     * in the request for the specified names
     * @see #parseTime(HttpServletRequest, String, Date, boolean)
     */
    public static Date parseTime(HttpServletRequest request, String name, Date date) {
        return parseTime(request, name, date, false);
    }

    /**
     * Returns the date containing the time parsed from the request based on
     * the specified input name, updating the specified date or null if
     * optional and no time is found.
     * <p>
     * <b>Note:</b> {@link TimeBean#parseDateTime} is recommended to ensure the
     * date is parsed using the correct time zone.
     * </p>
     * <p>
     * It is based on whether the user's locale uses a 12- or 24-hour clock.
     * when a 12-hour clock, the hours are assumed to be numbered 0..11 and an
     * AM/PM value must be available.
     * When a 24-hour clock, the hours are assumed to be numbered 0..23 and
     * no AM/PM value is required.
     * </p>
     * @param request the request from which to read the time components; it
     * is assumed that up to 3 parameters are present:
     * <ul>
     * <li><code><i>name</i>_hour</code> - the hour value
     * <li><code><i>name</i>_minute</code> - the minute value
     * <li><code><i>name</i>_ampm</code> - (optional) the ampm value
     * </ul>
     * where <code>name</code> is the name specified in the next parameter
     * @param name the name given to the time input components in the request
     * @param date the date to which to add the time
     * @return the parsed date; the value includes the date portion from
     * the specified date and the time as parsed from the request; this will
     * return null if no hour and minute value is found and <code>isOptional</code>
     * is true
     * @throws IllegalStateException <code>isOptional</code> is false and
     * if the hour or minute values are not found in the request for the specified names
     */
    public static Date parseTime(HttpServletRequest request, String name, Date date, boolean isOptional) {
        Date returnDate;

        String hourValue = request.getParameter(name + "_hour");
        String minuteValue = request.getParameter(name + "_minute");
        String amPmValue = request.getParameter(name + "_ampm");
        String timeZoneID = request.getParameter(name + "_timeZoneID");

        if (hourValue == null || hourValue.trim().length() == 0 ||
                minuteValue == null || minuteValue.trim().length() == 0 ||
                timeZoneID == null || timeZoneID.trim().length() == 0) {

            if (isOptional) {
                returnDate = null;

            } else {
                throw new IllegalStateException("Unable to locate hour or minute values from request with names '" +
                        name + "_hour" + " and " + name + "_minute" + " respectively.");

            }

        } else {
            // We have hour and minute values
            returnDate = parseTime(hourValue, minuteValue, amPmValue, timeZoneID, date);
        }

        return returnDate;
    }

    /**
     * Returns the date containing the date and time parsed from the component parts.
     * <p>
     * It is based on whether the user's locale uses a 12- or 24-hour clock.
     * when a 12-hour clock, the hours are assumed to be numbered 0..11 and an
     * AM/PM value must be available.
     * When a 24-hour clock, the hours are assumed to be numbered 0..23 and
     * no AM/PM value is required.
     * </p>
     * @param dateValue the value for the date
     * @param hourValue the value for the hour; in the range <code>0..11</code>
     * if 12-hour clock or <code>0..23</code> if 24-hour clock
     * @param minuteValue the value for the minutes; in the range <code>00..59</code>
     * @param amPmValue the AM/PM selection, one of {@link Calendar#AM} or
     * {@link Calendar#PM}; this may be null if the user's
     * locale uses 24-hour clock
     * @param timeZoneID the ID of the time zone that was used to display and enter
     * the time components
     * @return the parsed date; the value includes the date portion from
     * the specified date and the time as parsed from the request
     */
    private static Date parseDateTime(String dateValue, String hourValue, String minuteValue, String amPmValue, String timeZoneID)
            throws InvalidDateException {
        return parseTime(hourValue, minuteValue, amPmValue, timeZoneID, parseDate(dateValue, timeZoneID));
    }

    /**
     * Parses the specified date value with the specified time zone.
     * @param dateValue the date string to parse
     * @param timeZoneID the time zone for which to parse it
     * @return the date
     * @throws InvalidDateException if the date is not valid for the current
     * user's locale
     */
    private static Date parseDate(String dateValue, String timeZoneID)
            throws InvalidDateException {

        // Grab the user from the session
        User user = SessionManager.getUser();
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneID);

        PnCalendar dateCalendar = new PnCalendar(user);
        dateCalendar.setTimeZone(timeZone);
        dateCalendar.setTime(user.getDateFormatter().parseDateString(dateValue, timeZone));
        dateCalendar.set(Calendar.SECOND, 0);
        dateCalendar.set(Calendar.MILLISECOND, 0);

        return dateCalendar.getTime();
    }

    /**
     * Returns the date containing the time parsed from the component parts,
     * updating the specified date.
     * <p>
     * It is based on whether the user's locale uses a 12- or 24-hour clock.
     * when a 12-hour clock, the hours are assumed to be numbered 0..11 and an
     * AM/PM value must be available.
     * When a 24-hour clock, the hours are assumed to be numbered 0..23 and
     * no AM/PM value is required.
     * </p>
     * @param hourValue the value for the hour; in the range <code>0..11</code>
     * if 12-hour clock or <code>0..23</code> if 24-hour clock
     * @param minuteValue the value for the minutes; in the range <code>00..59</code>
     * @param amPmValue the AM/PM selection, one of {@link Calendar#AM} or
     * {@link Calendar#PM}; this may be null if the user's
     * locale uses 24-hour clock
     * @param timeZoneID the ID of the time zone that was used to display and enter
     * the time components
     * @param date the date to which to add the time
     * @return the parsed date; the value includes the date portion from
     * the specified date and the time as parsed from the request
     */
    public static Date parseTime(String hourValue, String minuteValue, String amPmValue, String timeZoneID, Date date) {

        // Grab the user from the session
        User user = SessionManager.getUser();
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneID);

        // Create a calendar for maintaining the date and time
        // We set it to the specified date
        PnCalendar dateTimeCalendar = new PnCalendar(user);
        dateTimeCalendar.setTimeZone(timeZone);
        dateTimeCalendar.setTime(date);

        if (is12HourClock()) {
            // 0..11 hours
            // The pattern excludes the AM/PM component because the AM/PM
            // value is 0 or 1, not "AM" or "PM"
            // We handle the AM/PM value by setting it explicitly in the calendar
            String timePattern = "K:mm";
            String timeString = hourValue + ":" + minuteValue;

            // Create a calendar for maintaining the parsed time
            PnCalendar timeCalendar = new PnCalendar(user);
            timeCalendar.setTimeZone(timeZone);
            timeCalendar.setTime(user.getDateFormatter().parseTime(timeString, timePattern, timeZone));

            // Now update the datetime with the parsed time components
            // 10/21/2003 - Tim
            // There is a Java bug that prevents set(Calendar.AM_PM) from working
            // http://developer.java.sun.com/developer/bugParade/bugs/4846659.html
            // As a result, we convert hour & ampm to hour of day
            dateTimeCalendar.set(Calendar.HOUR_OF_DAY, toHourOfDay(timeCalendar.get(Calendar.HOUR), Integer.valueOf(amPmValue).intValue()));
            dateTimeCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));

        } else {
            // 0..23 hours
            // No AM/PM component is required since the hour is in the range
            // 0..23
            String timePattern = "H:mm";
            String timeString = hourValue + ":" + minuteValue;

            // Create a calendar for maintaining the parsed time
            PnCalendar timeCalendar = new PnCalendar(user);
            timeCalendar.setTimeZone(timeZone);
            timeCalendar.setTime(user.getDateFormatter().parseTime(timeString, timePattern, timeZone));

            // Now update the datetime with the parsed time components
            dateTimeCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            dateTimeCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));

        }


        // Reset second and millisecond since we don't care about those
        dateTimeCalendar.set(Calendar.SECOND, 0);
        dateTimeCalendar.set(Calendar.MILLISECOND, 0);

        // Now return the date
        return dateTimeCalendar.getTime();
    }

    /**
     * Convers an AM/PM hour to an hour of day
     * @param hour the hour to convert in the range 0..11 (where 0 is really 12)
     * @param amPm one of <code>Calendar.AM</code> or <code>Calendar.PM</code>
     * @return the hour of day value in the range 0..23
     * @throws IllegalArgumentException if hour or amPm are not in the correct range
     */
    private static int toHourOfDay(int hour, int amPm) {

        if (hour < 0 || hour > 11) {
            throw new IllegalArgumentException("hour must be in the range 0..11: " + hour);
        }

        if (!(amPm == Calendar.AM || amPm == Calendar.PM)) {
            throw new IllegalArgumentException("amPM must be " + Calendar.AM + " or " + Calendar.PM + ": " + amPm);
        }

        int hourOfDay;

        if (amPm == Calendar.AM) {
            // 0..11 AM is 0..11 24-hour clock
            hourOfDay = hour;

        } else {
            // 0..11 PM is 12..23 24-hour clock
            hourOfDay = hour + 12;
        }

        return hourOfDay;
    }

    /**
     * Indicates whether the locale for the current user uses 12 hour clock.
     * This is determined by checking a an arbitrary formatted date for the
     * presence of 12 hour clock dateformat fields in a value formatted using the
     * SHORT time style.
     * @return true if the user's locale uses 12 hour clock
     */
    private static boolean is12HourClock() {
        net.project.util.DateFormat df = net.project.util.DateFormat.getInstance();
        Date currentDate = new Date();

        // Determine the format of the hour componenent of the date
        boolean is12HourClock = true;
        if (df.isFieldPresent(DateFormat.Field.HOUR0, currentDate, DateFormat.SHORT, DateFormat.SHORT) ||
                df.isFieldPresent(DateFormat.Field.HOUR1, currentDate, DateFormat.SHORT, DateFormat.SHORT)) {

            // Contains hours 0..11 or 1..12
            // This means it uses 12-hour clock
            is12HourClock = true;

        } else if (df.isFieldPresent(DateFormat.Field.HOUR_OF_DAY0, currentDate, DateFormat.SHORT, DateFormat.SHORT) ||
                df.isFieldPresent(DateFormat.Field.HOUR_OF_DAY1, currentDate, DateFormat.SHORT, DateFormat.SHORT)) {

            // Contains 0..23 or 1..24
            // This means it uses 24-hour clock
            is12HourClock = false;

        }
        return is12HourClock;
    }

    /**
     * Returns the timezone of the time field from the request.
     * @param request the request from which to get the time zone
     * @param timeFieldName the name of the time field as passed to the time taglib
     * @return the time zone or null if none was found
     */
    public static TimeZone getTimeZone(HttpServletRequest request, String timeFieldName) {
        TimeZone timeZone;

        String timeZoneID = request.getParameter(timeFieldName + "_timeZoneID");
        if (!Validator.isBlankOrNull(timeZoneID)) {
            timeZone = TimeZone.getTimeZone(timeZoneID);
        } else {
            timeZone = null;
        }

        return timeZone;
    }

    //
    // Instance members
    //

    /**
     * The name of the input field.
     */
    private String name = "";

    /**
     * The ID prefix of the input fields.
     */
    private String id = null;

    /**
     * The current date to set the selection lists to.
     */
    private Date date = null;

    /**
     * The attributes to add to each HTML element.
     */
    private Map attributes = new HashMap();

    /**
     * Specifies the minute style; default is calendar.
     */
    private int minuteStyle = MINUTE_STYLE_CALENDAR;

    /**
     * Indicates whether to include empty hour and minute values.
     */
    private boolean isOptional = false;

    /**
     * Indicates whether the time zone is included in the display.
     */
    private boolean isIncludeTimeZone = false;

    /**
     * The time zone to use.
     */
    private TimeZone timeZone = null;

    /**
     * The current calendar to use for displaying the time.
     */
    private final Calendar calendar;

    /**
     * The date formatter to use when formatting the time.
     */
    private final net.project.util.DateFormat dateFormat;

    /**
     * Creates a new TimeBean based on the current time and current user's
     * locale and time zone.
     */
    public TimeBean() {
        this.calendar = new PnCalendar(SessionManager.getUser());
        this.dateFormat = SessionManager.getUser().getDateFormatter();
    }

    /**
     * Specifies the date from which to extract the time to preset the
     * input field.
     * <p>
     * Initially <code>date</code> is null.  A null date indicates no selection
     * is made if this bean is optional; otherwise a null date results in
     * the current time to be used to select the options in the presentation.
     * </p>
     * @param date the date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Returns the date that is to be used when initializing the presentation
     * selection lists.
     * <p>
     * Returns the current date specified by {@link #setDate} or null.
     * @return the date which may be null if none was specified
     */
    private Date getDate() {
        return this.date;
    }

    /**
     * Specifies the name to use when creating the HTML elements.
     * <p/>
     * This is a required attribute.
     * @param tag prefix the bean should use when generating names for HTML input controls
     * Names will be:
     * <li>&lt;tag&gt;_hour
     * <li>&lt;tag&gt;_minute
     * <li>&lt;tag&gt;_ampm
     * <li>&lt;tag&gt;_timeZoneID
     */
    public void setTag(String tag) {
        this.name = tag;
    }

    /**
     * Specifies the id to use when creating the HTML elements.
     * <p/>
     * The specified ID prefix is used to construct actual IDs in the same manner
     * as {@link #setTag(java.lang.String)}. <br/>
     * This is an optional attribute; when omitted or null, no ID attribute will be added.
     * @param idPrefix the prefix for each input element's ID
     */
    public void setID(String idPrefix) {
        this.id = idPrefix;
    }

    /**
     * Specifies the additional HTML attributes to add to each HTML element.
     * @param attributes the attributes
     */
    public void setAttributes(Map attributes) {
        this.attributes = attributes;
    }

    /**
     * Specifies the minute style to use.
     * The default is calendar, which displays 4 minute selections: 00, 15, 30, 45
     * @param minuteStyle the style of minutes to display.  One of
     * {@link #MINUTE_STYLE_NORMAL} or {@link #MINUTE_STYLE_CALENDAR}
     */
    public void setMinuteStyle(int minuteStyle) {
        this.minuteStyle = minuteStyle;
    }

    /**
     * Specifies whether this time field is optional.
     * Default is <code>false</code>.
     * An optional time field means empty hour and minute values are
     * included in the display.  Those empty values are selected if
     * the date is null.
     * @param isOptional true if it is optional; false otherwise
     * @see #isDateSpecifed
     */
    public void setOptional(boolean isOptional) {
        this.isOptional = isOptional;
    }

    /**
     * Indicates whether this is an optional field or not.
     * @return true if optional; empty hour and minute values are included
     * in the display
     */
    private boolean isOptional() {
        return this.isOptional;
    }

    /**
     * Specifies whether to include a time zone display.
     * @param isIncludeTimeZone true if the time zone should be displayed;
     * false if no time zone should be display
     */
    public void setIncludeTimeZone(boolean isIncludeTimeZone) {
        this.isIncludeTimeZone = isIncludeTimeZone;
    }

    /**
     * Indicates whether the time zone is displayed.
     * @return true if the time zone is displayed; false if no time zone is display
     */
    private boolean isIncludeTimeZone() {
        return this.isIncludeTimeZone;
    }

    /**
     * Specifies an alternative time zone to the current user's time
     * zone when displaying and inputting a time value.
     * @param timeZone the time zone to use
     */
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Returns the currently set time zone.
     * @return the time zone or null if none was specified
     */
    private TimeZone getTimeZone() {
        return this.timeZone;
    }

    /**
     * Indicates whether a date has been specified.
     * When no date is specified and the time is optional, the empty values
     * are selected.  Otherwise, the current date is used.
     * @return true if a specific date was specified; false if no date was
     * specfied
     */
    private boolean isDateSpecifed() {
        return (getDate() != null);
    }

    /**
     * Returns the current calendar, updating its time if the
     * current date is not null and setting the current time
     * zone (if specified).
     * @return the current calendar
     */
    private Calendar getCalendar() {
        if (isDateSpecifed()) {
            this.calendar.setTime(getDate());
        }
        if (getTimeZone() != null) {
            this.calendar.setTimeZone(getTimeZone());
        }

        return this.calendar;
    }

    /**
     * Returns the current date format to use when formatting.
     * @return the date format
     */
    private net.project.util.DateFormat getDateFormat() {
        return this.dateFormat;
    }

    /**
     * Returns the presentation of the input fields.
     * Consists of 2 or 3 selection lists, depending on locale.
     * <ul>
     * <li>hour selection; options depend on 12-hour or 24-hour clock.
     * If this time bean's <code>optional</code> flag is set, then an empty hour
     * selection is included at the top of the list; it is selected only
     * if the specified date is null
     * <li>minute selection
     * If this time bean's <code>optional</code> flag is set, then an empty hour
     * selection is included at the top of the list; it is selected only
     * if the specified date is null
     * <li>am/pm selection - only if 12-hour clock
     * </ul>
     * @return String rendered HTML presentation of the control
     */
    public String getPresentation() {
        StringBuffer sb = new StringBuffer();
        writeHourOptionList(sb);
        sb.append(" : ");
        writeMinuteOptionList(sb);
        writeAMPMOptionList(sb);
        sb.append("&nbsp;");
        writeTimeZone(sb);
        return sb.toString();
    }

    /**
     * Returns the current hour value, which depends on whether the current user's
     * locale uses a 12-hour or 24-hour clock.
     * @return the hour value
     */
    public int getHourSelectionValue() {
        return getCalendar().get((is12HourClock() ? Calendar.HOUR : Calendar.HOUR_OF_DAY));
    }

    /**
     * Returns the current minute value.
     * @return the minute value
     */
    public int getMinuteSelectionValue() {
        return getCalendar().get(Calendar.MINUTE);
    }

    /**
     * Returns the AM/PM value.
     * @return the AM/PM value
     */
    public int getAmPmSelectionValue() {
        return getCalendar().get(Calendar.AM_PM);
    }

    /**
     * Append an hour option list (for a select HTML input type) to a string
     * buffer.
     * @param sb the string buffer to append to
     */
    private void writeHourOptionList(StringBuffer sb) {

        // The hours to display in the appropriate order
        int[] orderedHours;
        // The pattern used for displaying the hour
        String displayHourPattern;
        // The calendar constant used for getting and setting the hour
        int hourConstant;

        boolean is12HourClock = is12HourClock();

        if (is12HourClock) {
            // Hours are numbered 0 thru 11
            // These correspond to the values returned by Calendar.HOUR
            // Displayed using the "h" pattern which displays hours as 1..12
            // We put hour zero at the end so that 12 displays after 11
            orderedHours = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 0};
            displayHourPattern = "h";
            hourConstant = Calendar.HOUR;

        } else {
            // Hours are numbered 0 thru 23
            // These correspond to the values returned by Calendar.HOUR_OF_DAY
            // Displayed using the "H" pattern which displays hours as 0..23
            orderedHours = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                    12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};
            displayHourPattern = "H";
            hourConstant = Calendar.HOUR_OF_DAY;
        }

        Calendar calendar = getCalendar();

        // Get the current hour value; used to select an item in the selection list
        int selectedHour = calendar.get(hourConstant);
        boolean isOptionSelected = false;

        sb.append("<SELECT name=\"" + this.name + "_hour\" ");
        if (this.id != null) {
            sb.append("id=\"" + this.id + "_hour\" ");
        }
        sb.append(addAllAttributes()).append(">");

        if (isOptional()) {
            // Add an empty value; make it selected if no date was specified
            sb.append("<option ");
            if (!isDateSpecifed()) {
                sb.append(" selected");
                isOptionSelected = true;
            }
            sb.append(" value=\"\">");
        }

        for (int i = 0; i < orderedHours.length; i++) {
            int nextHour = orderedHours[i];

            sb.append("<option");

            if (!isOptionSelected && nextHour == selectedHour) {
                // Select this option if the date's hour
                // matches the hour we are presenting
                sb.append(" selected");
                isOptionSelected = true;
            }

            sb.append(" value=\"").append(nextHour).append("\">");

            // Set the current hour to the next hour
            calendar.set(hourConstant, nextHour);

            // Format the hour using the appropriate pattern
            sb.append(getDateFormat().formatDate(calendar.getTime(), displayHourPattern, getTimeZone()));

            sb.append("</option>");

        }

        sb.append("</SELECT>");

    }

    /**
     * Append a minute option list (for a select HTML input type) to a string
     * buffer.
     * @param sb a <code>StringBuffer</code> object to which the minute option
     * list will be appended.
     */
    private void writeMinuteOptionList(StringBuffer sb) {

        Calendar calendar = getCalendar();

        // Grab the current minute value
        int minute = calendar.get(Calendar.MINUTE);
        boolean isOptionSelected = false;

        sb.append("<SELECT name=\"" + this.name + "_minute\" ");
        if (this.id != null) {
            sb.append("id=\"" + this.id + "_minute\" ");
        }
        sb.append(addAllAttributes()).append(">");

        if (isOptional()) {
            // Add an empty value; make it selected if no date was specified
            sb.append("<option ");
            if (!isDateSpecifed()) {
                sb.append(" selected");
                isOptionSelected = true;
            }
            sb.append(" value=\"\">");
        }

        if (this.minuteStyle == MINUTE_STYLE_CALENDAR) {
            // Minutes are 00, 15, 30, 45

            // Add 0 minute
            String temp = EMPTY;
            if (!isOptionSelected && (minute == 0 || minute > 45)) {
                temp = SELECTED;
                isOptionSelected = true;
            }
            calendar.set(Calendar.MINUTE, 0);
            sb.append("<OPTION " + temp + " value=\"00\">" + getDateFormat().formatDate(calendar.getTime(), "mm", getTimeZone()) + "</OPTION>");

            // Add 15 minute
            if (!isOptionSelected && minute <= 15) {
                temp = SELECTED;
                isOptionSelected = true;
            } else {
                temp = EMPTY;
            }
            calendar.set(Calendar.MINUTE, 15);
            sb.append("<OPTION " + temp + " value=\"15\">" + getDateFormat().formatDate(calendar.getTime(), "mm", getTimeZone()) + "</OPTION>");

            // Add 30 minute
            if (!isOptionSelected && minute <= 30) {
                temp = SELECTED;
                isOptionSelected = true;
            } else {
                temp = EMPTY;
            }
            calendar.set(Calendar.MINUTE, 30);
            sb.append("<OPTION " + temp + " value=\"30\">" + getDateFormat().formatDate(calendar.getTime(), "mm", getTimeZone()) + "</OPTION>");

            // Add 45 minute
            if (!isOptionSelected && minute <= 45) {
                temp = SELECTED;
            } else {
                temp = EMPTY;
            }
            calendar.set(Calendar.MINUTE, 45);
            sb.append("<OPTION " + temp + " value=\"45\">" + getDateFormat().formatDate(calendar.getTime(), "mm", getTimeZone()) + "</OPTION>");

        } else {
            // Minutes are 0..59

            for (int i = 0; i <= 59; i++) {

                calendar.set(Calendar.MINUTE, i);

                sb.append("<option");
                if (!isOptionSelected && minute == i) {
                    sb.append(" selected");
                }
                sb.append(" value=\"").append(i).append("\"").append(">");
                sb.append(getDateFormat().formatDate(calendar.getTime(), "mm", getTimeZone()));
                sb.append("</option>");
            }

        }

        sb.append("</SELECT>");
    }

    /**
     * Append an AM/PM option list (for a select HTML input type) to a string
     * buffer only if the user's locale is 12 hour clock.
     * @param sb the string buffer to append to; nothing is appended if the
     * user's locale does not use 12 hour clock
     */
    private void writeAMPMOptionList(StringBuffer sb) {

        if (is12HourClock()) {
            // We included AM/PM is the user's locale specifies a 12 hour clock

            Calendar calendar = getCalendar();

            // Grab the current AMPM value
            int currentAmPm = calendar.get(Calendar.AM_PM);

            sb.append("<SELECT name=\"" + this.name + "_ampm\" ");
            if (this.id != null) {
                sb.append("id=\"" + this.id + "_ampm\" ");
            }
            sb.append(addAllAttributes()).append(">");

            // Add the AM option
            calendar.set(Calendar.HOUR_OF_DAY, 1);
            sb.append("<option");
            if (currentAmPm == Calendar.AM) {
                sb.append(" selected");
            }
            sb.append(" value=\"").append(Calendar.AM).append("\">");
            sb.append(getDateFormat().formatDate(calendar.getTime(), "a", getTimeZone()));
            sb.append("</option>");

            // Add the PM option
            calendar.set(Calendar.HOUR_OF_DAY, 13);
            sb.append("<option");
            if (currentAmPm == Calendar.PM) {
                sb.append(" selected");
            }
            sb.append(" value=\"").append(Calendar.PM).append("\">");
            sb.append(getDateFormat().formatDate(calendar.getTime(), "a", getTimeZone()));
            sb.append("</option>");

            sb.append("</SELECT>");
        }

    }

    /**
     * Writes the current time zone to the specified buffer.
     * <p>
     * The time zone ID is always included as a hidden field.
     * It's name is displayed only if {@link #isIncludeTimeZone} is specified.
     * </p>
     * <p>
     * The currently set time zone is used or the user's current time zone if
     * none was specified.
     * </p>
     * @param sb the buffer to update
     */
    private void writeTimeZone(StringBuffer sb) {

        Calendar displayCalendar = getCalendar();
        TimeZone timeZone = displayCalendar.getTimeZone();

        // We always write a hidden timezone filed
        sb.append("<input type=\"hidden\" name=\"" + this.name + "_timeZoneID\" ");
        if (this.id != null) {
            sb.append("id=\"" + this.id + "_timeZoneID\" ");
        }
        sb.append("value=\"" + timeZone.getID() + "\">");

        if (isIncludeTimeZone()) {
            // We also want to display it
            sb.append(getDateFormat().formatDate(displayCalendar.getTime(), "zzz", getTimeZone()));
        }

    }

    /**
     * Constructs a string containing all attributes in this element.
     * @return the attributes
     */
    private String addAllAttributes() {
        StringBuffer attributeText = new StringBuffer();

        // Iterate over all attributes, adding to HTML element
        for (Iterator it = this.attributes.keySet().iterator(); it.hasNext();) {
            String nextAttribute = (String) it.next();
            Object nextValue = this.attributes.get(nextAttribute);

            if (nextValue instanceof Boolean) {
                // For Boolean attributes, we simply use the attribute name
                // If it is set to true
                // For example: DISABLED or READONLY
                if (((Boolean) nextValue).booleanValue()) {
                    attributeText.append(" ").append(nextAttribute);
                }

            } else {
                // Skip "name" and "id" because we already handled those
                if (!nextAttribute.equals("name") && !nextAttribute.equals("id")) {
                    // All other attributes we use the attribute name and value
                    attributeText.append(" ").append(nextAttribute).append("=\"")
                            .append(nextValue)
                            .append("\"");
                }

            }

        }

        return attributeText.toString();
    }

}
