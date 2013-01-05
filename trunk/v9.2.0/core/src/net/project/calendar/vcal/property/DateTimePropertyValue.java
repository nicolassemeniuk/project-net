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
package net.project.calendar.vcal.property;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * TimeZonePropertyValue is a property value for a date and time.
 * The Date and Time value is formatted as a string consistent with the ISO 
 * 8601 representation for combinations of dates and times.  This is of the form:
 * <code>&lt;year&gt;&lt;month&gt;&lt;day&gt;T&lt;hour&gt;&lt;minute&lt;second&gt;&lt;type designator&gt;</code>
 */
public class DateTimePropertyValue extends PropertyValue {
    
    private Date dateTime = null;
    private Locale locale = null;
    private TimeZone timezone = null;

    /**
     * Creates a new DateTimePropertyValue for the specified date and time
     * in the specified locale and time zone
     * @param dateTime the date and time
     * @param locale the locale
     * @param timezone the time zone
     */
    DateTimePropertyValue(Date dateTime, Locale locale, TimeZone timezone) {
        this.dateTime = dateTime;
        this.locale = locale;
        this.timezone = timezone;
    }

    /**
     * Returns this DateTimePropertyValue as UTC time in ISO 8601 representation
     * For example: 6th July 2001 @ 03:30:00 UTC is <code>20010706T033000</code>
     * (i.e. 5th July 2001 @ 20:30 PDT)
     * @return the rendition in ISO 8601 format
     */
    String getRendition() {
        Date utcDateTime = null;
        int utcOffset = 0;
        
        // Construct a calendar based on specified timezone and locale
        // for the specified time since epoch and use it to get the offset from UTC
        // The correct offset depends on whether the dateTime value is
        // in Daylight Savings or not for the specified timezone
        GregorianCalendar calendar = new GregorianCalendar(this.timezone, this.locale);
        calendar.setTime(this.dateTime);
        utcOffset = calendar.get(GregorianCalendar.ZONE_OFFSET);
        if (this.timezone.inDaylightTime(this.dateTime)) {
                utcOffset += calendar.get(GregorianCalendar.DST_OFFSET);
        }

        // UTC time since epoch representing same hours, minutes and seconds
        // is calculated by adding offset from UTC to my time since epoch
        utcDateTime = new Date(calendar.getTime().getTime() - utcOffset);

        // Construct a date formatter based on the ISO 8601 representation for
        // combinations of dates and times and format the date and time 
        // using the formatter
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd'T'HHmmss", this.locale);
        df.setTimeZone(timezone);
        return df.format(utcDateTime) + "Z";
    }

    boolean isQuotedPrintable() {
        return false;
    }

}
