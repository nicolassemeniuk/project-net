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

 package net.project.util;

import java.io.Serializable;
import java.text.AttributedCharacterIterator;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import net.project.security.SessionManager;
import net.project.security.User;

import org.apache.log4j.Logger;


/**
 * Format and parse dates and times based on the Locale of the User.
 *
 * @author unascribed
 */
public class DateFormat implements java.io.Serializable {

    //
    // Static members
    //

    /**
     * @deprecated as of 7.4; no replacement.
     * Date formats are always user-specific.
     */
    public static final String DEFAULT_DATE_FORMAT = "M/d/y";

    /**
     * @deprecated as of 7.6.3; no replacement.
     * This is an Oracle-specific string that is used to convert string dates
     * to Oracle dates.  It is nothing to do with Java date formatting.
     * The only usage was found in <code>net.project.form.DateField</code> and
     * this constant has been moved there privately.
     */
    public static final String DATABASE_DATETIME_FORMAT = "FMMM/DD/YYYY HH24:MI";

    /**
     * The default date format style, currently <code>java.text.DateFormat.SHORT</code>.
     * This is typically a numeric-only date format (that is, no months are spelled-out or
     * abbreviated).
     */
    public static final int DEFAULT_DATE_STYLE = java.text.DateFormat.SHORT;

    /**
     * The default time format style, currently <code>java.text.DateFormat.LONG</code>.
     * This style includes the time zone, but avoids stupidity of the <code>FULL</code> style.
     * For example, for locale English (United States) both LONG and FULL return dates like: <code>2:22:40 PM PDT</code>
     * but for locale English (United Kingdom) LONG returns <code>14:22:40 PDT</code> and FULL returns <code>14:22:40 o'clock PDT</code>.
     * Clearly LONG is preferred.
     * */
    public static final int DEFAULT_TIME_STYLE = java.text.DateFormat.LONG;

    /**
     * Time format that should be used to display time in the "03:45 PM PST" format.
     * @since Version 7.4
     * @deprecated as of 7.5.1; no replacement
     * A hardcoded time format should never be use.  Use code like
     * <code>user.getDateFormatter().formatTime()</code> instead.
     */
    public static final String CUSTOM_TIME_FORMAT = "hh:mm a z";


    //
    // Instance members
    //

    /**
     * Localization data, resolved from the user or default values.
     * Sometimes a user has not been loaded prior to requiring localization data
     * for formatting (e.g. during registration).
     */
    private final LocalizationData localizationData;

    /**
     * Creates a new DateFormat for the specified user.
     * @param user the user whose locale and timezone all formatting and
     * parsing activities are based
     */
    public DateFormat(User user) {
        this.localizationData = new LocalizationData(user);
    }

    public DateFormat(Locale locale, TimeZone timeZone) {
        this.localizationData = new LocalizationData(locale,  timeZone);
    }

    /**
     * Returns a new DateFormat based on the current user in the session.
     * @return the DateFormat
     */
    public static DateFormat getInstance() {
        User user = SessionManager.getUser();
        return (new DateFormat(user));
    }
                             
    /**
     * Returns a date format for the current user based on the specified style and
     * time zone.
     * @param style the style of the format
     * @param timeZone the time zone to use when formatting
     * @return the formatter; uses the user's locale or the default locale
     * if no user was specified
     */
    private java.text.SimpleDateFormat getDateFormat(int style, TimeZone timeZone) {

        java.text.SimpleDateFormat df;

        try {
            // Get date format based on user's locale or default locale
            df = (SimpleDateFormat) java.text.DateFormat.getDateInstance(style, this.localizationData.getLocale());

        } catch (ClassCastException e) {
            // If the User's locale doesn't return a SimpleDateFormat,
            // we try the default locale
            // If this doesn't work, the error will proprogate to the display
            df = (SimpleDateFormat) java.text.DateFormat.getDateInstance(style);
        }

        if (timeZone != null) {
            df.setTimeZone(timeZone);
        } else {
            // Set timezone to user's timezone or default timezone
            df.setTimeZone(this.localizationData.getTimeZone());
        }

        // Must not be lenient, otherwise "0/0/00" is parsed as "11/30/1999"
        df.setLenient(false);

        return df;
    }


    /**
     * Returns a datetime format for the current user based on the specified style using
     * their locale but the specified time zone.
     * @param dateStyle the style of the date portion
     * @param timeStyle the style of the time portion
     * @param timeZone the time zone to use in the formatter; when null the user's default time zone
     * is used
     * @return the formatter for the user's locale and specified time zone
     */
    private java.text.SimpleDateFormat getDateTimeFormat(int dateStyle, int timeStyle, TimeZone timeZone) {

        java.text.SimpleDateFormat df;

        try {
            // Get date format based on user's locale or default locale
            df = (SimpleDateFormat) java.text.DateFormat.getDateTimeInstance(dateStyle, timeStyle, this.localizationData.getLocale());

        } catch (ClassCastException e) {
            // If the User's locale doesn't return a SimpleDateFormat,
            // we try the default locale
            // If this doesn't work, the error will proprogate to the display
            df = (SimpleDateFormat) java.text.DateFormat.getDateTimeInstance(dateStyle, timeStyle);
        }

        if (timeZone != null) {
            df.setTimeZone(timeZone);
        } else {
            // Set timezone to user's timezone or default timezone
            df.setTimeZone(this.localizationData.getTimeZone());
        }

        // Must not be lenient, otherwise "0/0/00" is parsed as "11/30/1999"
        df.setLenient(false);

        return df;
    }

    /**
     * Returns a time format for the current user based on the specified style.
     * @param timeStyle the style to use for the time
     * @param timeZone the time zone to use in the formatter; when null the user's default time zone
     * is used
     * @return the formatter; uses the user's locale or the default locale
     * if no user was specified
     */
    private java.text.SimpleDateFormat getTimeFormat(int timeStyle, TimeZone timeZone) {

        java.text.SimpleDateFormat df;

        try {
            // Get date format based on user's locale or default locale
            df = (SimpleDateFormat) java.text.DateFormat.getTimeInstance(timeStyle, this.localizationData.getLocale());

        } catch (ClassCastException e) {
            // If the User's locale doesn't return a SimpleDateFormat,
            // we try the default locale
            // If this doesn't work, the error will proprogate to the display
            df = (SimpleDateFormat) java.text.DateFormat.getTimeInstance(timeStyle);
        }

        if (timeZone != null) {
            df.setTimeZone(timeZone);
        } else {
            // Set timezone to user's timezone or default timezone
            df.setTimeZone(this.localizationData.getTimeZone());
        }

        // Must not be lenient, otherwise "0/0/00" is parsed as "11/30/1999"
        df.setLenient(false);

        return df;
    }


    //
    // Formatting methods
    //


    /**
     * Formats the specified date to a time using the {@link #DEFAULT_TIME_STYLE} style.
     * @param time the date to format to a time
     * @return the formatted time or empty string if the specified time was null
     */
    public String formatTime(java.util.Date time) {

        String formattedTime;

        if (time == null) {
            formattedTime = "";

        } else {
            formattedTime = getTimeFormat(DEFAULT_TIME_STYLE, null).format(time);

        }

        return formattedTime;
    }

    /**
     * Formats the specified date to a time using the {@link #DEFAULT_TIME_STYLE} style.
     * @param time the date to format to a time
     * @param timeStyle a <code>int</code> containing a time style.  These are
     * defined in the java.text.DateFormat object.
     * @return the formatted time or empty string if the specified time was null
     */
    public String formatTime(java.util.Date time, int timeStyle) {

        String formattedTime;

        if (time == null) {
            formattedTime = "";

        } else {
            formattedTime = getTimeFormat(timeStyle, null).format(time);

        }

        return formattedTime;
    }

    /**
     * Formats the specified date to a time using the specified pattern.
     * <b>Note:</b> In most cases, {@link #formatTime(java.util.Date)} should
     * be used instead in order to parse dates based on the default pattern
     * for the locale; only in special cases should a pattern be used.
     * @param time the date to format to a time
     * @param pattern the pattern to use; this is a non-localized pattern;
     * Localized patterns should never be used - they won't work with this
     * method and may also contain ambiguous pattern characters
     * @return the formatted time or empty string if the specified time
     * or pattern was null
     */
    public String formatTime(java.util.Date time, String pattern) {

        String formattedTime;

        if (time == null || pattern == null) {
            formattedTime = "";

        } else {
            SimpleDateFormat df = getTimeFormat(DEFAULT_TIME_STYLE, null);
            df.applyPattern(pattern);
            formattedTime = df.format(time);

        }

        return formattedTime;
    }


    /**
     * Formats the specified date to a string using the {@link #DEFAULT_DATE_STYLE} style
     * and the user's locale and time zone
     * @param date the date to format
     * @return the formatted date or empty string if the date is null
     */
    public String formatDate(java.util.Date date) {
        return formatDate(date, DEFAULT_DATE_STYLE, null);
    }

    /**
     * Formats the specified date to a string using the {@link #DEFAULT_DATE_STYLE} style
     * and the specified time zone.
     * @param date the date to format
     * @param timeZone the time zone to use
     * @return the formatted date or empty string if the date is null
     */
    public String formatDate(java.util.Date date, TimeZone timeZone) {
        return formatDate(date, DEFAULT_DATE_STYLE, timeZone);
    }

    /**
     * Formats the specified date to a string using the specified style.
     * @param date the date to format
     * @param style the date format style to use
     * @return the formatted date or empty string if the date is null
     */
    public String formatDate(java.util.Date date, int style) {
        return formatDate(date, style, null);
    }

    /**
     * Formats the specified date to a string using the specified style.
     * @param date the date to format
     * @param style the date format style to use
     * @param timeZone the time zone to use; when null the user's default time zone
     * is used
     * @return the formatted date or empty string if the date is null
     */
    private String formatDate(java.util.Date date, int style, TimeZone timeZone) {

        String formattedDate;

        if (date == null) {
            formattedDate = "";

        } else {
            formattedDate = getDateFormat(style, timeZone).format(date);

        }

        return formattedDate;
    }

    /**
     * Formats the specified date to a string using the specified pattern.
     * <b>Note:</b> In most cases, {@link #formatDate(java.util.Date)} should
     * be used instead in order to parse dates based on the default pattern
     * for the locale; only in special cases should a pattern be used.
     * @param date the date to format
     * @param pattern the pattern to use; this is a non-localized pattern;
     * Localized patterns should never be used - they won't work with this
     * method and may also contain ambiguous pattern characters
     * @return the formatted date or empty string if the date or pattern
     * are null
     */
    public String formatDate(java.util.Date date, String pattern) {
        return formatDate(date, pattern, null);
    }
    
    /**
     * Formats the specified date to a string using the <code>java.text.DateFormat.MEDIUM</code> style
     *  and the user's locale and time zone
     * In case of US Locale it returns some customized date string eg. Today, Yesterday, Tomorrow and 
     *    without year if the date is for current year. 
     * @param date the date to format
     * @return the formatted date or empty string if the date is null
     */
    public String formatDateMedium(java.util.Date date) {
    	if(Locale.US.equals(this.localizationData.getLocale())){
    		if(DateUtils.isToday(date))	
    			return "Today";
    		if(DateUtils.isYesterday(date))
    			return "Yesterday";
    		if(DateUtils.isTomorrow(date))
    			return "Tomorrow";
    		if(DateUtils.isCurrentYear(date))
    			return formatDate(date, "MMM d");
    		else
    			return formatDate(date, "MMM d, yyyy");
    	}
        return formatDate(date, java.text.DateFormat.MEDIUM, null);
    }

    /**
     * Formats the specified date to a string using the specified pattern and time zone.
     * <b>Note:</b> In most cases, {@link #formatDate(java.util.Date)} should
     * be used instead in order to parse dates based on the default pattern
     * for the locale; only in special cases should a pattern be used.
     * @param date the date to format
     * @param pattern the pattern to use; this is a non-localized pattern;
     * Localized patterns should never be used - they won't work with this
     * method and may also contain ambiguous pattern characters
     * @param timeZone the time zone to use; when null the user's default time zone is used
     * @return the formatted date or empty string if the date or pattern
     * are null
     */
    public String formatDate(java.util.Date date, String pattern, TimeZone timeZone) {

        String formattedDate;

        if (date == null || pattern == null) {
            formattedDate = "";

        } else {
            SimpleDateFormat df = getDateFormat(DEFAULT_DATE_STYLE, timeZone);
            df.applyPattern(pattern);
            formattedDate = df.format(date);

        }

        return formattedDate;
    }

    /**
     * Formats the specified date to a date time String using the {@link #DEFAULT_DATE_STYLE}
     * date style and {@link #DEFAULT_TIME_STYLE} time style.
     * @param dateTime the date time to format
     * @return the formatted date time
     */
    public String formatDateTime(java.util.Date dateTime) {
        String formattedDate = "";

        if (dateTime != null) {
            formattedDate = getDateTimeFormat(DEFAULT_DATE_STYLE, DEFAULT_TIME_STYLE, null).format(dateTime);
        }

        return formattedDate; 
    }

    /**
     * Formats the specified date to a date time String using the {@link #DEFAULT_DATE_STYLE}
     * date style and {@link #DEFAULT_TIME_STYLE} time style and
     * using the specified time zone.
     * @param dateTime the date time to format
     * @param timeZone the time zone to use when formatting
     * @return the formatted date time
     */
    public String formatDateTime(Date dateTime, TimeZone timeZone) {
        return getDateTimeFormat(DEFAULT_DATE_STYLE, DEFAULT_TIME_STYLE, timeZone).format(dateTime);
    }

    //
    // Parsing methods
    //

    /**
     * Parses the specified time string using the specified pattern.
     * @param timeString the time to parse
     * @param pattern the pattern to use; this is a non-localized pattern;
     * Localized patterns should never be used - they won't work with this
     * method and may also contain ambiguous pattern characters
     * @return the date based on the specified time string or null
     * if the time string was null; the date is based on today
     */
    public java.util.Date parseTime(String timeString, String pattern) {
        return parseTime(timeString, pattern, null);
    }

    /**
     * Parses the specified time string using the specified pattern assuming
     * the time was entered in the specified time zone
     * @param timeString the time to parse
     * @param pattern the pattern to use; this is a non-localized pattern;
     * Localized patterns should never be used - they won't work with this
     * method and may also contain ambiguous pattern characters
     * @param timeZone the time zone; when null the user's default time zone is used
     * @return the date based on the specified time string or null
     * if the time string was null; the date is based on today
     */
    public java.util.Date parseTime(String timeString, String pattern, TimeZone timeZone) {

        if (timeString == null) {
            return null;
        }

        try {
            SimpleDateFormat df = getTimeFormat(DEFAULT_TIME_STYLE, timeZone);
            df.applyPattern(pattern);
            return df.parse(timeString);

        } catch (ParseException pe) {
        	Logger.getLogger(DateFormat.class).debug("DateFormat.parseTime: ParseException: " + pe);
            return null;

        }

    }

    /**
     * Formats the time from a string using a specific style pattern.
     *
     * @param time a <code>String</code> containing a time we need to parse.
     * @param timeStyle a <code>int</code> containing a time style.  These are
     * defined in the java.text.DateFormat object.
     * @return
     */
    public java.util.Date parseTime(String time, int timeStyle) {
        if (time == null) {
            return null;
        }

        try {
            SimpleDateFormat df = getTimeFormat(timeStyle, null);
            return df.parse(time);

        } catch (ParseException pe) {
        	Logger.getLogger(DateFormat.class).debug("DateFormat.parseTime: ParseException: " + pe);
            return null;

        }
    }
    

    /**
     * Parses the specified date string assuming it matches the {@link #DEFAULT_DATE_STYLE} style
     * using the current user's time zone.
     * @param dateString the date string to be parsed.
     * @return the parsed Date
     * @throws InvalidDateException if the specified date-string contains an invalid date
     */
    public Date parseDateString(String dateString) throws InvalidDateException {
        return parseDateString(dateString, (TimeZone) null);
    }

    /**
     * Parses the specified date string assuming it matches the {@link #DEFAULT_DATE_STYLE} style
     * for the specified time zone
     * @param dateString the date string to be parsed.
     * @param timeZone the time zone to use when parsing; when null the current user's
     * time zone is used
     * @return the parsed Date
     * @throws InvalidDateException if the specified date-string contains an invalid date
     */
    public Date parseDateString(String dateString, TimeZone timeZone) throws InvalidDateException {

        java.util.Date parsedDate;

        try {
            parsedDate = getDateFormat(DEFAULT_DATE_STYLE, timeZone).parse(dateString);

        } catch (ParseException pe) {
            // we suck the exception here since it is going to be rethrown below since we are
            // setting parse date to null here.
            parsedDate = null;
            Logger.getLogger(DateFormat.class).error("DateFormat.parseDate: ParseException: " + pe);

        }

        // The only possible reason why a parsed date could be null is that the date string contains
        // an invalid date
        if (parsedDate == null) {
            InvalidDateException idException = new InvalidDateException("Problem parsing specified date string " + dateString);
            idException.setInvalidDateString(dateString);
            throw idException;
        }

        return parsedDate;

    }


    /**
     * Parses the given date string to a Date using the passed pattern.
     * <b>Note:</b> In general use {@link #parseDateString(String)} instead;
     * this method is not localized.
     * @param dateString the date string to be parsed.
     * @param pattern the pattern string to be used to parse.
     * @return the parsed Date or null if the specified date string is null
     * @throws InvalidDateException if the date-string contains an invalid date.
     */
    public java.util.Date parseDateString(String dateString, String pattern) throws InvalidDateException {

        if (dateString == null) {
            return null;
        }

        java.util.Date parsedDate;

        try {
            SimpleDateFormat df = getDateFormat(DEFAULT_DATE_STYLE, null);
            df.applyPattern(pattern);
            parsedDate = df.parse(dateString);

        } catch (ParseException pe) {
            //we suck the exception here since it is going to be rethrown below since we are
            // setting parse date to null here.
            parsedDate = null;
            Logger.getLogger(DateFormat.class).debug("DateFormat.parseDateString: ParseException: " + pe);

        }

        // The only possible reason why a parsed date could be null is that the date string contains
        // an invalid date
        if (parsedDate == null) {
            InvalidDateException idException = new InvalidDateException("Problem parsing specified date string " + dateString);
            idException.setInvalidDateString(dateString);
            throw idException;
        }

        return parsedDate;
    }

    /**
     * Parses the given date string assuming the date is in the {@link #DEFAULT_DATE_STYLE} format.
     * @param dateString the date string to be parsed.
     * @return the parsed Date
     * @deprecated As of Gecko Update 3;
     * This method may be removed  after two releases. <br>
     * Use parseDateString(String dateString) throws InvalidDateException
     * The parsed date should never be null. Investigation shows that a datestring
     * containing invalid date causes the parsed date to be null.
     * This method does not propagate the cause of a parsed date being null to the caller
     * but just returns a null value. This is generally an undesirable behavior.
     */
    public java.util.Date parseDate(String dateString) {
        if (dateString == null)
            return null;

        try {
            return getDateFormat(DEFAULT_DATE_STYLE, null).parse(dateString);

        } catch (ParseException pe) {
        	Logger.getLogger(DateFormat.class).debug("DateFormat.parseTime: ParseException: " + pe);
            return null;
        }
    }

    /** Parse the given date string to a Date using the passed pattern.
     * @param dateString the date string to be parsed.
     * @param pattern the pattern string to be used to parse.
     * @return the parsed Date
     * @deprecated As of Gecko Update 3;
     * This method may be removed  after two releases. <br>
     * Use parseDateString(String dateString, String pattern) throws InvalidDateException
     * The parsed date should never be null. Investigation shows that a datestring
     * containing invalid date causes the parsed date to be null.
     * This method does not propagate the cause of a parsed date being null to the caller
     * but just returns a null value. This is generally an undesirable behavior.
     */
    public java.util.Date parseDate(String dateString, String pattern) {

        if (dateString == null) {
            return null;
        }

        try {
            SimpleDateFormat df = getDateFormat(DEFAULT_DATE_STYLE, null);
            df.applyPattern(pattern);
            return df.parse(dateString);

        } catch (ParseException pe) {
        	Logger.getLogger(DateFormat.class).debug("DateFormat.parseTime: ParseException: " + pe);
            return null;

        }
    }

    /**
     * Parse the given date string assuming it matches the {@link #DEFAULT_DATE_STYLE and #DEFAULT_TIME_STYLE} style
     * using the current user's time zone.
     * @param dateTimeString the date string to be parsed.
     * @exception InvalidDateException if the date-string contains an invalid date.
     * @return the parsed Date
     */
    public java.util.Date parseDateTimeString(String dateTimeString) throws InvalidDateException {

        if (dateTimeString == null) {
            return null;
        }

        java.util.Date parsedDate;

        try {
        	parsedDate = getDateTimeFormat(DEFAULT_DATE_STYLE, DEFAULT_TIME_STYLE, null).parse(dateTimeString);
        	
        } catch (ParseException pe) {
            //we suck the exception here since it is going to be rethrown below since we are
            // setting parse date to null here.
            parsedDate = null;
            Logger.getLogger(DateFormat.class).debug("DateFormat.parseDateTimeString: ParseException: " + pe);

        }

        // The only possible reason why a parsed date could be null is that the date string contains
        // an invalid date
        if (parsedDate == null) {
            InvalidDateException idException = new InvalidDateException("Problem parsing specified date string " + dateTimeString);
            idException.setInvalidDateString(dateTimeString);
            throw idException;
        }

        return parsedDate;
    }

    /**
     * Parse the given date string to a Date using the passed pattern.
     * It constructs a new DateFormatter for the given pattern and uses it to parse the string.
     * @param dateTimeString the date string to be parsed.
     * @param pattern the pattern string to be used to parse.
     * @exception InvalidDateException if the date-string contains an invalid date.
     * @return the parsed Date
     */
    public java.util.Date parseDateTimeString(String dateTimeString, String pattern) throws InvalidDateException {

        if (dateTimeString == null) {
            return null;
        }

        java.util.Date parsedDate;

        try {
            SimpleDateFormat df = getDateTimeFormat(DEFAULT_DATE_STYLE, DEFAULT_TIME_STYLE, null);
            df.applyPattern(pattern);
            parsedDate = df.parse(dateTimeString);

        } catch (ParseException pe) {
            //we suck the exception here since it is going to be rethrown below since we are
            // setting parse date to null here.
            parsedDate = null;
            Logger.getLogger(DateFormat.class).debug("DateFormat.parseDateTimeString: ParseException: " + pe);

        }

        // The only possible reason why a parsed date could be null is that the date string contains
        // an invalid date
        if (parsedDate == null) {
            InvalidDateException idException = new InvalidDateException("Problem parsing specified date string " + dateTimeString);
            idException.setInvalidDateString(dateTimeString);
            throw idException;
        }

        return parsedDate;
    }

    /**
     * Indicates if the given date string contains a legal date.
     * A legal date is one which will be successfully parsed when
     * {@link #parseDateString(String)} is called.
     * @param dateString the datestring in which to look for legal date.
     * @return boolean, true if the contained date is legal, false otherwise
     */
    public boolean isLegalDate(String dateString) {

        boolean isLegal = false;

        try {
            java.util.Date date = this.parseDateString(dateString);

            if (date != null) {
                // Legal date
                isLegal = true;
            }

        } catch (net.project.util.InvalidDateException ide) {
            // Remains Not legal
        }

        return isLegal;
    }

    /**
     * Returns the localized pattern for {@link #DEFAULT_DATE_STYLE} date
     * style. This is the pattern in which dates should be entered for successfully
     * parsing.
     * <p>
     * <b>Note</b>:  Due to Java bug <a href="http://developer.java.sun.com/developer/bugParade/bugs/4225362.html">http://developer.java.sun.com/developer/bugParade/bugs/4225362.html</a>
     * many locale's localized symbols are completely wrong; this method fixes the
     * following:
     * <ul>
     * <li>All locales with the language "fr"; symbols are <code>GamjkHmsSEDFwWxhKz</code>
     * <li>All locales with the language "de"; symbols are <code>GjMtkHmsSEDFwWahKzZ</code>
     * </ul>
     * </p>
     * <p>
     * <b>Note:</b>
     * The correct localized pattern characters are only available as an example
     * for display; localized patterns should <i>never</i> be used for parsing
     * dates; the problem is that localized pattern characters may be ambiguous.
     * For example, the French pattern character for month ("m") is the same
     * as for minute ("m").
     * </p>
     * @return the localized pattern
     */
    public String getDateFormatExample() {

        // First we must handle the case where the localized
        // pattern characters are total garbage
        // See the Java Bug 4225362 for details
        // The order of locale pattern chars are:
        // GyMdkHmsSEDFwWahKz
        // index positions specified by DateFormat constants

        String alternateSymbolString = null;

        if (this.localizationData.getLocale().getLanguage().equals("fr")) {
            // French Language
            // With Belgium, Canada and Switzerland, problem is month is "n" instead of
            // "m"
            // France and Luxembourg is completely wrong
            // Took French (Belgium) symbols, replaced month with "m"
            alternateSymbolString = "GamjkHmsSEDFwWxhKz";

        } else if (this.localizationData.getLocale().getLanguage().equals("de")) {
            // German Language
            // Austria and Switzerland, problem is year is "u" instead of "j".
            // Germany and Luxembourg is completely wrong
            // Took German (Austria) symbols, replaced year with "j"
            alternateSymbolString = "GjMtkHmsSEDFwWahKzZ";

        }

        SimpleDateFormat df = getDateFormat(DEFAULT_DATE_STYLE, null);

        if (alternateSymbolString != null) {
            // Now Apply alternate symbols to the date format
            DateFormatSymbols symbols = df.getDateFormatSymbols();
            symbols.setLocalPatternChars(alternateSymbolString);
            df.setDateFormatSymbols(symbols);
        }

        return df.toLocalizedPattern();
    }

    /**
     * Gets the current date pattern which this formatter  will use to parse/format dates.
     * @return 		the date pattern string
     * @deprecated As of 7.5; use {@link #getDateFormatExample} instead.
     * This returns the non-localized date format pattern; it should not be
     * used
     */
    public String getDateFormatPattern() {
        return getDateFormat(DEFAULT_DATE_STYLE, null).toPattern();
    }

    /**
     * Gets the current time pattern which this formatter  will use to parse/format time portion of dates.
     * @return 		the time pattern string
     * @deprecated As of 7.5; no replacement
     * This returns the non-localized time format pattern; it should not be
     * used
     */
    public String getTimeFormatPattern() {
        return getTimeFormat(DEFAULT_TIME_STYLE, null).toPattern();
    }

    /**
     * Returns the value for the specified field after formatting the
     * specified date using the date formatter
     * @param field the field value to get
     * @param date the date to format to get the field value
     * @param dateStyle the style to use when formatting the date;
     * should be such that the specified field is going to be found
     * in the formatted value for this style
     * @param timeStyle the style to use when formatting the time;
     * should be such that the specified field is going to be found
     * in the formatted value for this style
     * @return the value of the field or null if the field was not found in
     * the formatted date
     * @see java.text.DateFormat.Field
     */
    private String getFieldValue(java.text.DateFormat.Field field, Date date, int dateStyle, int timeStyle) {

        // Grab a date formatter for the specified style based on the current
        // locale
        java.text.DateFormat df = getDateTimeFormat(dateStyle, timeStyle, null);

        String fieldValue = null;
        boolean isFound = false;

        // The AttributedCharacterIterator is a CharacterIterator over the formatted
        // date that provides information about each character
        // For dates, the information tells us which field the character belongs
        // to and allows to extract the entire field value
        // We iterate over the characters until we hit the end or we find
        // the value for the field
        String formattedDate = df.format(date);
        AttributedCharacterIterator it = df.formatToCharacterIterator(date);
        for (char c = it.first(); c != AttributedCharacterIterator.DONE && !isFound; c = it.next()) {

            if (it.getAttribute(field) != null) {
                // The current character belongs to the field

                // Grab the first character position of all the characters in
                // the field
                int startIndex = it.getRunStart(field);
                // Grab the position _after_ the last character in the field
                int afterEndIndex = it.getRunLimit(field);

                if (startIndex >= 0 && afterEndIndex <= formattedDate.length()) {
                    // Chop out the entire field value based on the start and
                    // end positions
                    // We're done once we have this
                    fieldValue = formattedDate.substring(startIndex, afterEndIndex);
                    isFound = true;
                }

            } else {
                // Current character does not belong to the specified field
                // Continue looping
            }

        }

        return fieldValue;
    }

    /**
     * Indicates whether formatting the specified date would produce a value
     * for the specified field.
     * @param field the field to check for
     * @param date the date to format
     * @param dateStyle the style to use when formatting the date;
     * should be such that the specified field is going to be found
     * in the formatted value for this style
     * @param timeStyle the style to use when formatting the time;
     * should be such that the specified field is going to be found
     * in the formatted value for this style
     * @return true if the formatted date has a value for the specified field;
     * false otherwise
     */
    public boolean isFieldPresent(java.text.DateFormat.Field field, Date date, int dateStyle, int timeStyle) {
        return getFieldValue(field, date, dateStyle, timeStyle) != null;
    }


    //
    // Nested top-level classes
    //

    /**
     * Determines localization data for a user.
     * If the user is null or locale data is null, defaults are used.
     */
    private static class LocalizationData implements Serializable {

        private final Locale locale;
        private final TimeZone timeZone;

        /**
         * Creates locale data based on the specified user.
         * @param user the user; if null or if its locale or timezone are null
         * then default locale and timezone are used
         */
        private LocalizationData(User user) {

            if (user != null && user.getLocale() != null) {
                locale = user.getLocale();
            } else {
                locale = Locale.getDefault();
            }

            if (user != null && user.getTimeZone() != null) {
                timeZone = user.getTimeZone();
            } else {
                timeZone = TimeZone.getDefault();
            }

        }

        private LocalizationData(Locale locale, TimeZone timeZone) {
            this.locale = locale;
            this.timeZone = timeZone;
        }

        /**
         * Returns the locale.
         * @return the user's locale or the system default locale if the user's
         * locale is null
         */
        private Locale getLocale() {
            return this.locale;
        }

        /**
         * Returns the timezone.
         * @return the time zone
         */
        private TimeZone getTimeZone() {
            return this.timeZone;
        }

    }

}
