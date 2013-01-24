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

 package net.project.calendar.workingtime;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Provides days and times worked which results in duration.
 *
 * @author Tim Morrow
 * @since Version 7.7
 */
public interface IDaysWorked {

    /**
     * Returns the duration in days based on the days and times worked.
     * <p>
     * Duration is defined as the sum of the union of the hours and minutes worked
     * by many people.
     * </p>
     * @return the duration in days; has a scale of 10
     */
    BigDecimal getTotalDays();

    /**
     * Returns the duration in minutes based on the days and times worked.
     * <p>
     * Duration is defined as the sum of the union of the hours and minutes worked
     * by many people.
     * </p>
     * @return the duration in minutes; has a scale of 0
     */
    BigDecimal getTotalMinutes();

    /**
     * Returns the map of days to times.
     * @return an unmodifiable map where each key is a <code>DayOfYear</code>
     * and each value is a <code>AggregatedWorkingTimes</code>
     */
    Map getDaysTimesMap();

    /**
     * Adds all the days worked in the specified <code>DaysWorked</code>
     * to this one.
     * @param daysWorked the days worked to add to this
     */
    void add(IDaysWorked daysWorked);


    /**
     * Returns a list of the unique time ranges worked.
     * @return a list where each element is a {@link TimeRangeWorked}
     */
    List getTimeRangesWorked();

    /**
     * Gets the earliest time range in this map.
     */
    Date getEarliestWorkingTime();
}