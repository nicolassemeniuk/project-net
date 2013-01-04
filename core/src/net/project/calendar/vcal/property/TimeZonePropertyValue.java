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

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * The TimeZonePropertyValue is formatted in a manner consistent with ISO 8601. 
 * It is a signed numeric indicating the number of hours and possibly minutes 
 * from UTC. Time zones east of UTC are positive numbers. Time zones west of 
 * UTC are negative numbers. The following are examples of this property:<br>
 * <code><pre>
 * TZ:-05
 * TZ:+05:30
 * </pre></code>
 */
public class TimeZonePropertyValue extends PropertyValue {
    
    private TimeZone timezone = null;

    TimeZonePropertyValue(TimeZone timezone) {
        this.timezone = timezone;
    }

    /**
     * Returns the time zone formatted consistent with ISO 8601.
     * @return the formatted time zone
     */
    String getRendition() {
        StringBuffer rendition = new StringBuffer();
        int millisecondsOffset = 0;
        int minutesOffset = 0;
        int hours = 0;
        int minutes = 0;

        // Get today's date for specified timezone
        GregorianCalendar calendar = new GregorianCalendar(this.timezone);
        
        // Get timezone offset in milliseconds
        millisecondsOffset = timezone.getOffset(calendar.get(Calendar.ERA), calendar.get(Calendar.YEAR), 
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 
                    calendar.get(Calendar.DAY_OF_WEEK), calendar.get(Calendar.MILLISECOND));

        // Convert offset to [+-]HH:MI
        minutesOffset = millisecondsOffset / 1000 / 60;
        hours = minutesOffset / 60;
        minutes = minutesOffset % 60;

        DecimalFormat df = new DecimalFormat("00");
        rendition.append(df.format(hours));
        rendition.append(":");
        rendition.append(df.format(minutes));

        return rendition.toString();
    }

    boolean isQuotedPrintable() {
        return false;
    }

}

