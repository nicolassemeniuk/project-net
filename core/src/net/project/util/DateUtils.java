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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import net.project.calendar.PnCalendar;
import net.project.security.SessionManager;

/**
 * The Utility class for date related functions.
 *
 * @author Deepak Pandey
 * @since 03/2002
 */
public class DateUtils {
    /** The number of milliseconds in one hour. */
    static final long ONE_HOUR = 60 * 60 * 1000L;

    /**
     * Adds years to the date as specified by the integer parameter . Conversely will also do
     * substraction if the integer parameter is having negative value
     *
     * @param date   <code>Date</code> the specified date
     * @param i      the amount by which the date is to be reset
     * @return <code>Date</code>The reseted date object
     */
    public static Date addYear (Date date, int i) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
        cal.add(Calendar.YEAR , i);
        return cal.getTime();
    }

    /**
     * Adds months to the date as specified by the integer parameter .
     * Conversely will also do substraction if the integer parameter is having
     * negative value.
     *
     * @param date <code>Date</code> the specified date
     * @param i the amount by which the date is to be reset
     * @return <code>Date</code>The reseted date object
     */
    public static Date addMonth (Date date, int i) {
        Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
        cal.add(Calendar.MONTH , i);
        return cal.getTime();

    }

    /**
     * Adds days to the date as specified by the integer parameter . Conversely
     * will also do substraction if the integer parameter is having negative
     * value.
     *
     * @param date <code>Date</code> the specified date
     * @param i the amount by which the date is to be reset
     * @return <code>Date</code>The reseted date object
     */
    public static Date addDay (Date date, int i) {
        Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR , i);
        return cal.getTime();
    }

    /**
     * Returns the no. of days betweens two dates.  This value should always be
     * positive. (If there were a negative,  it would be subtraction and the name 
     * wouldn't be "days between" it would be "subtract".) Partial days are counted 
     * as full days.  If there are more than 24 hours between two dates, it means that 
     * we really have 2 days. 
     *
     * @param d1 <code>java.util.Date</code> instance
     * @param d2 <code>java.util.Date</code> instance
     * @return the no. of days betweens two dates
     */
    public static long daysBetween(Date d1, Date d2){
        return daysBetween(new PnCalendar(), d1, d2);
    }

    /**
     * Returns the no. of days betweens two dates.  This value should always be
     * positive. (If there were a negative,  it would be subtraction and the name 
     * wouldn't be "days between" it would be "subtract".) Partial days are counted 
     * as full days.  If there are more than 24 hours between two dates, it means that 
     * we really have 2 days. 
     *
     * @param cal <code>net.project.calendar.PnCalendar</code> instance
     * @param d1 <code>java.util.Date</code> instance
     * @param d2 <code>java.util.Date</code> instance
     * @return the no. of days betweens two dates
     */
    public static long daysBetween(Calendar cal, Date d1, Date d2) {
    	return daysBetween(cal, d1, d2, false);
    }
    
    /**
     * Returns the no. of days betweens two dates.  This value should always be
     * positive. (If there were a negative,  it would be subtraction and the name 
     * wouldn't be "days between" it would be "subtract".) Partial days are ignored,
     * if <b>ignoringPartialDays</b> is set to be <b>true</b>. It means, if there 
     * are more than 24 hours but less than 48 hours between two dates, we really have 1 day. 

     *
     * @param d1 <code>java.util.Date</code> instance
     * @param d2 <code>java.util.Date</code> instance
     * @param ignoringPartialDays boolean value
     * @return the no. of days betweens two dates
     */
    public static long daysBetween(Date d1, Date d2, boolean ignoringPartialDays) {
    	return daysBetween(new PnCalendar(), d1, d2, ignoringPartialDays);
    }
    
    /**
     * Returns the no. of days in decimal between two dates.  This value should always be
     * positive. (If there were a negative,  it would be subtraction and the name 
     * wouldn't be "days between" it would be "subtract".) Partial days are converted,
     * into decimals since the lowest granularity is hours.  
     *
     * @param cal <code>net.project.calendar.PnCalendar</code> instance
     * @param d1 <code>java.util.Date</code> instance
     * @param d2 <code>java.util.Date</code> instance
     * @return the no. of days betweens two dates
     */
    public static double daysBetweenGantt(Calendar cal, Date d1, Date d2) {
        //This algorithm only works if d2 is after d1.  Otherwise we return zero.
        if (d1.after(d2)) {
            Date dtemp = d2;
            d2 = d1;
            d1 = dtemp;
        }

        cal.setTime(d1);
        int d1Year = cal.get(Calendar.YEAR);
        int d1DayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        long d1TimeSeconds = (cal.get(Calendar.HOUR_OF_DAY) * 3600) + (cal.get(Calendar.MINUTE) * 60) + (cal.get(Calendar.SECOND));

        cal.setTime(d2);
        int d2Year = cal.get(Calendar.YEAR);
        int d2DayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        long d2TimeSeconds = (cal.get(Calendar.HOUR_OF_DAY) * 3600) + (cal.get(Calendar.MINUTE) * 60) + (cal.get(Calendar.SECOND));

        double daysBetween = 0;

        if (d1Year == d2Year) {
            //This is the simple case -- everybody is in the same year
            daysBetween = d2DayOfYear - d1DayOfYear;
        } else {
            cal.setTime(d1);
            daysBetween += cal.getActualMaximum(Calendar.DAY_OF_YEAR) - d1DayOfYear + 1;

            //Iterate on the years between these two years to find the number of
            //days in each of them.  (This helps account for leap years.)

            //Set a nice safe month that can't roll over into the next year
            cal.set(Calendar.MONTH, 5);
            for (int i = 0; i < d2Year-d1Year-1; i++) {
                cal.add(Calendar.YEAR, 1);
                daysBetween += cal.getActualMaximum(Calendar.DAY_OF_YEAR) - cal.getActualMinimum(Calendar.DAY_OF_YEAR) + 1;
            }

            //Now finish off with the days in the final year
            cal.setTime(d2);
            daysBetween += (d2DayOfYear - cal.getActualMinimum(Calendar.DAY_OF_YEAR));
        }

        if (d2TimeSeconds > d1TimeSeconds) {
            // this means days between is daysBetween + deltax
            daysBetween += (d2TimeSeconds - d1TimeSeconds)/(24 * 3600.0);
        } else {
            // this means days between is daysBetween - deltax
            daysBetween -= (d1TimeSeconds - d2TimeSeconds)/(24 * 3600.0);
        }


        return daysBetween;
    }
    
    /**
     * Returns the no. of days betweens two dates.  This value should always be
     * positive. (If there were a negative,  it would be subtraction and the name 
     * wouldn't be "days between" it would be "subtract".) Partial days are ignored,
     * if <b>ignoringPartialDays</b> is set to be <b>true</b>. It means, if there 
     * are more than 24 hours but less than 48 hours between two dates, we really have 1 day. 
     *
     * @param cal <code>net.project.calendar.PnCalendar</code> instance
     * @param d1 <code>java.util.Date</code> instance
     * @param d2 <code>java.util.Date</code> instance
     * @param ignoringPartialDays boolean value
     * @return the no. of days betweens two dates
     */
    public static long daysBetween(Calendar cal, Date d1, Date d2, boolean ignoringPartialDays) {
        //This algorithm only works if d2 is after d1.  Otherwise we return zero.
        if (d1.after(d2)) {
            Date dtemp = d2;
            d2 = d1;
            d1 = dtemp;
        }

        cal.setTime(d1);
        int d1Year = cal.get(Calendar.YEAR);
        int d1DayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        long d1TimeSeconds = (cal.get(Calendar.HOUR_OF_DAY) * 3600) + (cal.get(Calendar.MINUTE) * 60) + (cal.get(Calendar.SECOND));

        cal.setTime(d2);
        int d2Year = cal.get(Calendar.YEAR);
        int d2DayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        long d2TimeSeconds = (cal.get(Calendar.HOUR_OF_DAY) * 3600) + (cal.get(Calendar.MINUTE) * 60) + (cal.get(Calendar.SECOND));

        long daysBetween = 0;

        if (d1Year == d2Year) {
            //This is the simple case -- everybody is in the same year
            daysBetween = d2DayOfYear - d1DayOfYear;
        } else {
            cal.setTime(d1);
            daysBetween += cal.getActualMaximum(Calendar.DAY_OF_YEAR) - d1DayOfYear + 1;

            //Iterate on the years between these two years to find the number of
            //days in each of them.  (This helps account for leap years.)

            //Set a nice safe month that can't roll over into the next year
            cal.set(Calendar.MONTH, 5);
            for (int i = 0; i < d2Year-d1Year-1; i++) {
                cal.add(Calendar.YEAR, 1);
                daysBetween += cal.getActualMaximum(Calendar.DAY_OF_YEAR) - cal.getActualMinimum(Calendar.DAY_OF_YEAR) + 1;
            }

            //Now finish off with the days in the final year
            cal.setTime(d2);
            daysBetween += (d2DayOfYear - cal.getActualMinimum(Calendar.DAY_OF_YEAR));
        }

        if(!ignoringPartialDays) {
                
            //Partial days are counted as full days.  If there are more than 24 hours
            //between two dates, it means that we really have 2 days.  We have to be
            //careful about how we check this though -- daylight savings time can
            //really screw stuff up.
            if (d2TimeSeconds > d1TimeSeconds) {
                daysBetween++;
            }
        }

        return daysBetween;
    }

    public static long monthsBetween(Date d1, Date d2) {
        //This algorithm only works if d2 is after d1.  Otherwise we return zero.
        if (d1.after(d2)) {
            Date dtemp = d2;
            d2 = d1;
            d1 = dtemp;
        }

        PnCalendar cal = new PnCalendar();
        cal.setTime(d1);
        int d1Day = cal.get(PnCalendar.DAY_OF_MONTH);
        int d1Month = cal.get(PnCalendar.MONTH);
        int d1Year = cal.get(PnCalendar.YEAR);
        int d1Hour = cal.get(PnCalendar.HOUR_OF_DAY);
        int d1Minute = cal.get(PnCalendar.MINUTE);
        int d1Second = cal.get(PnCalendar.SECOND);

        cal.setTime(d2);
        int d2Day = cal.get(PnCalendar.DAY_OF_MONTH);
        int d2Month = cal.get(PnCalendar.MONTH);
        int d2Year = cal.get(PnCalendar.YEAR);
        int d2Hour = cal.get(PnCalendar.HOUR_OF_DAY);
        int d2Minute = cal.get(PnCalendar.MINUTE);
        int d2Second = cal.get(PnCalendar.SECOND);

        long monthsBetween = 0;

        if (d2Day > d1Day) {
            monthsBetween++;
        } else if (d2Day == d1Day && d2Hour > d1Hour) {
            monthsBetween++;
        } else if (d2Day == d1Day && d2Hour == d1Hour && d2Minute > d1Minute) {
            monthsBetween++;
        } else if (d2Day == d1Day && d2Hour == d1Hour && d2Minute == d1Minute && d2Second > d1Second) {
            monthsBetween++;
        }

        monthsBetween += (d2Year - d1Year)*12;
        monthsBetween += (d2Month - d1Month);

        return monthsBetween;
    }

    /**
     * Indicates the number of quarters in between two dates.
     *
     * @param d1 a <code>Date</code> object indicating the beginning of the
     * time period for which we will count quarters.
     * @param d2 a <code>Date</code> object indicating the end of the time
     * period for which we will count quarters.
     * @return a <code>long</code> indicating the number of quarters between the
     * two dates, including the quarters that the dates are in.
     */
    public static long quartersBetween(Date d1, Date d2) {
        //This algorithm only works if d2 is after d1.  Otherwise we return zero.
        if (d1.after(d2)) {
            Date dtemp = d2;
            d2 = d1;
            d1 = dtemp;
        }

        PnCalendar cal = new PnCalendar();
        long quarters = 0;

        cal.setTime(d1);
        int d1Day = cal.get(PnCalendar.DAY_OF_MONTH);
        int d1Month = cal.get(PnCalendar.MONTH);
        int d1Year = cal.get(PnCalendar.YEAR);

        cal.setTime(d2);
        int d2Day = cal.get(PnCalendar.DAY_OF_MONTH);
        int d2Month = cal.get(PnCalendar.MONTH);
        int d2Year = cal.get(PnCalendar.YEAR);

        //First, deal with the year differences
        quarters += (d2Year-d1Year)*4;

        //Then deal with the month differences
        int d1Q = d1Month / 3;
        int d2Q = d2Month / 3;
        quarters += (d2Q-d1Q+1);

        return quarters;
    }

    public static long yearsBetween(Date d1, Date d2) {
        //This algorithm only works if d2 is after d1.  Otherwise we return zero.
        if (d1.after(d2)) {
            Date dtemp = d2;
            d2 = d1;
            d1 = dtemp;
        }

        PnCalendar cal = new PnCalendar();

        cal.setTime(d1);
        int d1Day = cal.get(PnCalendar.DAY_OF_MONTH);
        int d1Month = cal.get(PnCalendar.MONTH);
        int d1Year = cal.get(PnCalendar.YEAR);
        int d1Hour = cal.get(PnCalendar.HOUR_OF_DAY);
        int d1Minute = cal.get(PnCalendar.MINUTE);
        int d1Second = cal.get(PnCalendar.SECOND);

        cal.setTime(d2);
        int d2Day = cal.get(PnCalendar.DAY_OF_MONTH);
        int d2Month = cal.get(PnCalendar.MONTH);
        int d2Year = cal.get(PnCalendar.YEAR);
        int d2Hour = cal.get(PnCalendar.HOUR_OF_DAY);
        int d2Minute = cal.get(PnCalendar.MINUTE);
        int d2Second = cal.get(PnCalendar.SECOND);

        int yearsBetween = 0;

        if (d2Month > d1Month) {
            yearsBetween++;
        } else if (d2Month == d1Month && d2Day > d1Day) {
            yearsBetween++;
        } else if (d2Month == d1Month && d2Day == d1Day && d2Hour > d1Hour) {
            yearsBetween++;
        } else if (d2Month == d1Month && d2Day == d1Day && d2Hour == d1Hour && d2Minute > d1Minute) {
            yearsBetween++;
        } else if (d2Month == d1Month && d2Day == d1Day && d2Hour == d1Hour && d2Minute == d1Minute && d2Second > d1Second) {
            yearsBetween++;
        }

        yearsBetween += (d2Year - d1Year);

        return yearsBetween;
    }

    public static Date startOfQuarter(Date date) {
        PnCalendar cal = new PnCalendar();
        cal.setTime(date);

        int month = cal.get(PnCalendar.MONTH);

        //Integer division will truncate any month that isn't evenly divisible by 3
        month = (month / 3)*3;

        cal.set(PnCalendar.MONTH, month);
        cal.set(PnCalendar.DAY_OF_MONTH, 1);

        return cal.getTime();
    }

    public static Date startOfHalf(Date date) {
        PnCalendar cal = new PnCalendar();
        cal.setTime(date);

        int month = cal.get(PnCalendar.MONTH);

        //Integer division will truncate any month that isn't evenly divisible by 3
        month = (month / 6)*6;

        cal.set(PnCalendar.MONTH, month);
        cal.set(PnCalendar.DAY_OF_MONTH, 1);

        return cal.getTime();
    }

    public static Date startOfYear(Date date) {
        PnCalendar cal = new PnCalendar();
        cal.setTime(date);

        cal.set(PnCalendar.MONTH, 0);

        return cal.getTime();
    }

    public static Date endOfYear(Date date) {
        PnCalendar cal = new PnCalendar();
        cal.setTime(date);

        cal.set(PnCalendar.MONTH, 11);

        return cal.getTime();
    }

    /**
     * Given a date value, return a string that a database query would interpret
     * as that date.  In Oracle, you can use a string like '01/01/2000' to
     * indicate a date, but you cannot include a time with this method.
     *
     * This method will return a string such as
     * <code>TO_DATE('01/01/2000 04:00', 'MM/DD/YYYY HH24:MI')</code> to get
     * around that shortcoming in situations where you really need a date in
     * string format.
     *
     * Note that under normal circumstance, you probably want to use a prepared
     * statement instead and use the <code>setTimestamp</code> method to set the
     * date value.
     *
     * @param dateToConvert a <code>Date</code> value that is to translated.
     * @return a <code>String</code> value with an embedded date.  This date is
     * formatted in a way that it can be provided to an oracle database.
     * @see net.project.database.DBFormat#dateTime - this is very similar;
     * your choice as to which one to use
     * @throws NullPointerException if the specified date is null
     */
    public static String getDatabaseDateString(Date dateToConvert) {

        if (dateToConvert == null) {
            throw new NullPointerException("dateToConvert is required");
        }

        StringBuffer dateString = new StringBuffer();
        dateString.append("TO_DATE('");
        dateString.append(new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US).format(dateToConvert));
        dateString.append("', 'MM/DD/YYYY HH24:MI')");

        return dateString.toString();
    }

    public static Date addTimeQuantityToDate(Date date, TimeQuantity tq) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);

        List strata = tq.stratify();
        for (Iterator it = strata.iterator(); it.hasNext();) {
            TimeQuantity timeQuantity = (TimeQuantity)it.next();
            cal.add(timeQuantity.getUnits().getGregorianEquivalent(), timeQuantity.getAmount().intValue());
        }

        return cal.getTime();
    }

    /**
     * Zeros the time components of the specified date, setting it to midnight
     * on the current day given the specified timezone.
     * @param date the date to zero the time components from
     * @param timeZone the time zone to use to decide what day the date is on
     * @return the date at midnight
     */
    public static Date zeroTime(Date date, TimeZone timeZone) {
        Calendar cal = new GregorianCalendar(timeZone);
        cal.setTime(date);
        zeroTime(cal);
        return cal.getTime();
    }

    /**
     * Zeros all time components from hour and lower.
     * @param cal the calendar to modify
     */
    public static void zeroTime(Calendar cal) {
        setTime(cal, 0, 0, 0, 0);
    }

    /**
     * Sets the hour, minute, second, millisecond parts of the calendar to the
     * specified values.
     * @param cal the calendar to modify
     * @param hour the hour of day (0 to 23)
     * @param minute the minute (0 to 59)
     * @param second the seconds (0 to 59)
     * @param millisecond the milliseconds (0 to 999)
     */
    public static void setTime(Calendar cal, int hour, int minute, int second, int millisecond) {
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
    }

    /**
     * Indicates whether two calendars represent the same day.
     * @param cal1 the first calendar
     * @param cal2 the second calendar
     * @return true if both calendars represent the same year, month and day
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {

        if (cal1 == null || cal2 == null) {
            throw new NullPointerException("cal1 and cal2 are required");
        }

        return ((cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) &&
                (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)) &&
                (cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)));

    }

    /**
     * Indicates whether the first calendar is at least one day earlier than the second
     * calendar.
     * @param cal1 the first calendar
     * @param cal2 the second calendar
     * @return true if the first calendar represents a time that is
     * on a preceding day than the second calendar; false otherwise.
     * Note that it doesn't matter by how much, the calendars could be 1 second
     * apart and sill be on different days (11:59 PM -> 12:00 AM)
     */
    public static boolean isEarlierDay(Calendar cal1, Calendar cal2) {

        if (cal1 == null || cal2 == null) {
            throw new NullPointerException("cal1 and cal2 are required");
        }

        boolean isEarlierDay = false;
        if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)) {
            // Earlier year, then it is definitely on an earlier day
            isEarlierDay = true;

        } else if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
            // Same year; continue to test smaller components

            if (cal1.get(Calendar.MONTH) < cal2.get(Calendar.MONTH)) {
                // Earlier month, then it is definitely on an earlier day
                isEarlierDay = true;

            } else if (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)) {
                // Same month; continue to test smaller components

                if (cal1.get(Calendar.DAY_OF_MONTH) < cal2.get(Calendar.DAY_OF_MONTH)) {
                    // Earlier day
                    isEarlierDay = true;
                }

            }

        }

        return isEarlierDay;
    }

    /**
     * Indicates whether the specified date is on or between the specified
     * start and end dates.
     * @param date the date to check
     * @param startDate the start date
     * @param endDate the end date
     * @return true if the date falls on or between the start and end dates
     */
    public static boolean isOnOrBetween(Date date, Date startDate, Date endDate) {
        return (!date.before(startDate) && !date.after(endDate));
    }

    /**
     * Return the earliest of the two dates.
     */ 
    public static Date min(Date date1, Date date2) {
        if (date1 == null && date2 != null) {
            return date2;
        } else if (date1 != null && date2 == null) {
            return date1;
        } else if (date1 == null && date2 == null) {
            return null;
        } else {
            return (date1.before(date2) ? date1 : date2);
        }
    }

    /**
     * Return the latest of two Dates.
     */
    public static Date max(Date date1, Date date2) {
        if (date1 == null && date2 != null) {
            return date2;
        } else if (date1 != null && date2 == null) {
            return date1;
        } else if (date1 == null && date2 == null) {
            return null;
        } else {
            return (date1.after(date2) ? date1 : date2);
        }
    }

    public static boolean isEqual(Date date1, Date date2) {
        //5 cases:
        // 1 == null 2 == null
        // 1 == null 2 != null
        // 1 != null 2 == null
        // 1 != null 2 !== null same
        // 1 != null 2 != null different

        return (date1 == null ? date2 == null : date1.equals(date2));
    }

    public static Date makeDate(TimeZone timeZone, String dateString) {
        Date date;

        try {
            Calendar cal = new GregorianCalendar(timeZone);
            DateFormat df = new DateFormat(Locale.US, timeZone);

            //Clear out the components of the calendar to make sure that if they
            //aren't in the parsed string, they will be set to zero.
            zeroTime(cal);

            // Set the calendar to the parsed date string
            cal.setTime(df.parseDateTimeString(dateString, "MM/DD/yy hh:mm a"));

            date = cal.getTime();
        } catch (InvalidDateException e) {
            date = null;
        }
        return date;
    }
    
    /**
     * To get the date and time in database format to match Dates
     * no locale specification
     * @param date
     * @return formatted date String
     */
    public static String toDBDateTime(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        StringBuffer sb = new StringBuffer();
        sb.append("TO_DATE('");
        sb.append(cal.get(Calendar.DATE));
        sb.append("/");
        sb.append(cal.get(Calendar.MONTH) + 1);
        sb.append("/");
        sb.append(cal.get(Calendar.YEAR));
        sb.append(" ");
        sb.append(cal.get(Calendar.HOUR_OF_DAY));
        sb.append(":");
        sb.append(cal.get(Calendar.MINUTE));
        sb.append(":");
        sb.append(cal.get(Calendar.SECOND));
        sb.append("', 'DD/MM/YYYY HH24:MI:SS')");
        return sb.toString();
    }
    
    /**
     * Convert date to user local spacific date.
     * The time part of cnverted date will be zero.
     * @param date
     * @return Date
     */
    public static Date getLocalizedDate(Date date) {
		try {
			return new SimpleDateFormat("MM/dd/yyyy").parse(SessionManager.getUser().getDateFormatter().formatDate(date, "MM/dd/yyyy"));
		} catch (ParseException pnetEx) {
			return null;
		}
	}

	/**
	 * If the date is for today	
	 * checks condition by considering user time zone. 
	 * @param date
	 * @return boolean
	 */
	public static boolean isToday(Date date){
    	if (date == null) 
    		return false;
    	Calendar today_cal = Calendar.getInstance();
		Calendar new_cal = Calendar.getInstance();
		today_cal.setTime(getLocalizedDate(new Date()));
		new_cal.setTime(getLocalizedDate(date));
		
		return new_cal.getTimeInMillis() == today_cal.getTimeInMillis();
    }

	/**
	 * If the date is for yesterday	
	 * checks condition by considering user time zone. 
	 * @param date
	 * @return boolean
	 */
	public static boolean isYesterday(Date date){
    	if (date == null) 
    		return false;
    	Calendar today_cal = Calendar.getInstance();
		Calendar new_cal = Calendar.getInstance();
		today_cal.setTime(getLocalizedDate(new Date()));
		new_cal.setTime(getLocalizedDate(date));
		today_cal.add(Calendar.DATE, -1);
		return new_cal.getTimeInMillis() == today_cal.getTimeInMillis();
    }

	/**
	 * If the date is for tomorrow	
	 * checks condition by considering user time zone. 
	 * @param date
	 * @return boolean
	 */
	public static boolean isTomorrow(Date date){
    	if (date == null) 
    		return false;
    	Calendar today_cal = Calendar.getInstance();
		Calendar new_cal = Calendar.getInstance();
		today_cal.setTime(getLocalizedDate(new Date()));
		new_cal.setTime(getLocalizedDate(date));
		today_cal.add(Calendar.DATE, 1);
		return new_cal.getTimeInMillis() == today_cal.getTimeInMillis();
    	
    }

	/**
	 * If the date year is current year.	
	 * checks condition by considering user time zone. 
	 * @param date
	 * @return boolean
	 */
	public static boolean isCurrentYear(Date date) {
		if (date == null)
			return false;
		Calendar today_cal = Calendar.getInstance();
		Calendar new_cal = Calendar.getInstance();
		today_cal.setTime(getLocalizedDate(new Date()));
		new_cal.setTime(getLocalizedDate(date));
		return new_cal.get(Calendar.YEAR) == today_cal.get(Calendar.YEAR);
	}
	
	
    /** 
	 * get date after cleaning time part
	 * @param date  
	 * @return date 
	 */
	public static Date clearTimePart(Date date){
		 if(date == null) {
          throw new IllegalArgumentException("The argument 'date' cannot be null.");
        }
        // Get an instance of the Calendar.
        Calendar calendar = Calendar.getInstance();
        // Make sure the calendar will not perform automatic correction.
        calendar.setLenient(false);
        // Set the time of the calendar to the given date.
        calendar.setTime(date);
        // Remove the hours, minutes, seconds and milliseconds.
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // Return the date again.
        return calendar.getTime();
	}
    
}


