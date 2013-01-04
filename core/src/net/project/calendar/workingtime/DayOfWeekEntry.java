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
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.calendar.workingtime;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Changed access to public which was previously default
 * Provides a WorkingTimeCalendarEntry that represents a day of week
 * (Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday).
 *
 * @author Tim Morrow
 * @since Version 7.6.0
 */
public class DayOfWeekEntry extends WorkingTimeCalendarEntry implements Serializable {

    //
    // Static members
    //

    /**
     * Ensures the specified day number is valid.
     * @param dayNumber a day in the range {@link Calendar#SUNDAY} .. {@link Calendar#SATURDAY}
     * @throws IllegalArgumentException if the day number is not in the correct range
     */
    static void assertValidDayNumber(int dayNumber) {
        if (dayNumber < Calendar.SUNDAY || dayNumber > Calendar.SATURDAY) {
            throw new IllegalArgumentException("Day number must be in the range " + Calendar.SUNDAY + " to " + Calendar.SATURDAY);
        }
    }

    //
    // Instance members
    //

    /**
     * The day of the week.
     * @see Calendar#DAY_OF_WEEK
     */
    private int dayNumber;

    /**
     * Creates a new DayOfWeek entry for the specified day number.
     * If it is a working day, default working times are added.
     * @param dayNumber a day in the range {@link Calendar#SUNDAY} .. {@link Calendar#SATURDAY}
     * @param isWorkingDay true if this is a working day; false if it is a
     * non-working day
     * @param isAddDefaultTimes true if we are to add default working times;
     * false if no working times are to be added
     * @throws IllegalArgumentException if the day number is not in the
     * correct range
     */
    DayOfWeekEntry(int dayNumber, boolean isWorkingDay, boolean isAddDefaultTimes) {
        super(isWorkingDay, isAddDefaultTimes);

        assertValidDayNumber(dayNumber);
        this.dayNumber = dayNumber;
    }

    /**
     * For serialization only.
     */
    private DayOfWeekEntry() {
    }

    /**
     * Returns the day number for this day of week.
     * @return the day number
     * @see Calendar#DAY_OF_WEEK
     */
    int getDayNumber() {
        return this.dayNumber;
    }

    public String toString() {
        return "["+ super.toString() + ", dayNumber=" + dayNumber + "]";
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DayOfWeekEntry)) return false;
        if (!super.equals(o)) return false;

        final DayOfWeekEntry dayOfWeekEntry = (DayOfWeekEntry) o;

        if (dayNumber != dayOfWeekEntry.dayNumber) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + dayNumber;
        return result;
    }
    
    /**
     * Get the WeekDayBean
     * @return
     */
    
    public WeekDayBean getWeekDayBean() {
    	WeekDayBean weekDayBean = new WeekDayBean();
    	weekDayBean.setDayType(String.valueOf(dayNumber));
    	weekDayBean.setDayWorking(isWorkingDay());
    	weekDayBean.setWorkingTimes(getWorkingTimes());
    	return weekDayBean;
    }    
}

