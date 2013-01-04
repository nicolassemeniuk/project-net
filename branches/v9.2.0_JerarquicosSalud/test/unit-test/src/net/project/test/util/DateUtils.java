package net.project.test.util;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.DateFormat;
import java.text.ParseException;
import net.project.util.*;

/**
 * Provides utility methods for manipulating dates.
 * @author Tim Morrow
 * @since Version 7.7.0
 */
public class DateUtils {

    /**
     * Parses a date and time in the short format for US locale assuming PST/PDT
     * timezone.
     * <p/>
     * An example date time: <code>06/04/04 8:00 AM</code>
     * @param dateTimeString the date and time to parse
     * @return the parsed date
     * @throws IllegalArgumentException if the specified date time could not be parsed
     */
    public static Date parseDateTime(String dateTimeString) {

        Date date;

        try {
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
            df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            date = df.parse(dateTimeString);
        } catch (ParseException e) {
            throw (IllegalArgumentException) new IllegalArgumentException("Unable to parse date time " + dateTimeString).initCause(e);
        }

        return date;
    }

    /**
     * Parses a date in the short format for US locale assuming PST/PDT
     * timezone.
     * <p/>
     * An example date time: <code>06/04/04</code>
     * @param dateString the date to parse
     * @return the parsed date
     * @throws IllegalArgumentException if the specified date could not be parsed
     */
    public static Date parseDate(String dateString) {

        Date date;

        try {
            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
            df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            date = df.parse(dateString);
        } catch (ParseException e) {
            throw (IllegalArgumentException) new IllegalArgumentException("Unable to parse date " + dateString).initCause(e);
        }

        return date;
    }

    /**
     * Parses a date in the short format for US locale assuming PST/PDT
     * timezone.
     * @param dateString the date to parse
     * @return the parsed date or null if there was a problem parsing; the
     * date's time components are set to midnight
     */
    public static Date makeDate(String dateString) {

        TimeZone timeZone = TimeZone.getTimeZone("America/Los_angeles");

        Date date;

        try {
            Calendar cal = new GregorianCalendar(timeZone);
            net.project.util.DateFormat df = new net.project.util.DateFormat(Locale.US, timeZone);

            //Clear out the components of the calendar to make sure that if they
            //aren't in the parsed string, they will be set to zero.
            net.project.util.DateUtils.zeroTime(cal);

            // Set the calendar to the parsed date string
            cal.setTime(df.parseDateTimeString(dateString, "MM/DD/yy hh:mm a"));

            date = cal.getTime();
        } catch (InvalidDateException e) {
            date = null;
        }

        return date;
    }
}