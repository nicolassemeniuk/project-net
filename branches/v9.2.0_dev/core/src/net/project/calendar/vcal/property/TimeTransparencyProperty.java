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

/**
 * The TimeTransparencyProperty defines whether the event is transparent to 
 * free time searches.  Its value may be <code>BLOCKING</code> or <code>TRANSPARENT</code>
 * A <code>TRANSPARENT</code> vCalendar entity might display on a Calendar client
 * as "Free" time, while a <code>BLOCKING</code> entity might display as "Busy" 
 */
public class TimeTransparencyProperty extends Property implements net.project.calendar.vcal.ICalendarConstants {

    public static final TimeTransparencyPropertyValue BLOCKING = new TimeTransparencyPropertyValue(0);
    public static final TimeTransparencyPropertyValue TRANSPARENT = new TimeTransparencyPropertyValue(1);

    public TimeTransparencyProperty() {
        super(FIELD_TRANSP);
    }

    public TimeTransparencyProperty(TimeTransparencyPropertyValue value) {
        super(FIELD_TRANSP, value);
    }

    /**
     * Represents the enumeration of property values.
     */
    public static class TimeTransparencyPropertyValue extends PropertyValue {

        int value = 0;

        TimeTransparencyPropertyValue(int value) {
            this.value = value;
        }

        /**
         * Indicates whether this PropertyValue is quoted printable.
         * @return true if the property value is quoted printable
         */
        boolean isQuotedPrintable() {
            return false;
        }
        /**
         * Returns the string rendition of this property value.
         * @return the string rendition
         */
        String getRendition() {
            return Integer.toString(this.value);
        }
    }
}
