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

 package net.project.schedule;

import java.math.BigDecimal;

import net.project.calendar.workingtime.AbstractWorkingTimeCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;

/**
 * Provides a WorkingTimeCalendar for a schedule.
 * <p>
 * It behaves as a ScheduleEntryWorkingTimeCalendar with a single 100% assignment
 * using the schedule working time calendar definitions.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.6.2
 */
public class ScheduleWorkingTimeCalendar extends AbstractWorkingTimeCalendar {

    //
    // Instance members
    //

    /**
     * Creates a ScheduleWorkingTimeCalendar based on the specified provider's
     * default working time calendar definition and default time zone.
     * @param provider the provider from which to get the default calendar definition
     */
    ScheduleWorkingTimeCalendar(IWorkingTimeCalendarProvider provider) {
        super(provider.getDefaultTimeZone(), provider.getDefault());
    }

    /**
     * Returns an allocated resource factor of "1.00" always since a schedule
     * calendar behaves as if 1 resource is assigned 100%.
     * @return an allocated resource factor of <code>1.00</code>
     */
    protected BigDecimal calculateAllocatedResourceFactor() {
        return new BigDecimal("1.00");
    }
}
