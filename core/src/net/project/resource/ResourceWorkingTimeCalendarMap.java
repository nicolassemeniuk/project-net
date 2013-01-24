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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.resource;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.schedule.Schedule;

/**
 * A map of <code>ResourceWorkingTimeCalendar</code> instances to resource ids.
 * If a Calendar hasn't already been created, a new one will be instantiated.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class ResourceWorkingTimeCalendarMap {
    private Map resourceEntries = new HashMap();
    private final IWorkingTimeCalendarProvider provider;
    private final TimeZone timeZone;

    public ResourceWorkingTimeCalendarMap(Schedule schedule, TimeZone timeZone) {
        provider = schedule.getWorkingTimeCalendarProvider();
        this.timeZone = timeZone;
    }

    /**
     * Get a ResourceWorkingTimeCalendar for a given resourceID, creating a new
     * WorkingTimeCalendar if one hasn't already been created.
     *
     * @param resourceID a <code>String</code> containing the unique primary key
     * of a resource (person) for whom we want a working calendar.
     * @return a <code>ResourceWorkingTimeCalendar</code> which corresponds to
     * the given resourceID.
     */
    public ResourceWorkingTimeCalendar get(String resourceID) {
        ResourceWorkingTimeCalendar cal = (ResourceWorkingTimeCalendar)resourceEntries.get(resourceID);

        if (cal == null) {
            WorkingTimeCalendarDefinition def = provider.getForResourceID(resourceID);

            //If the resource doesn't have a specific calendar, the returned
            //value will be null, get the default instead.
            if (def == null) {
                def = provider.getDefault();
            }

            cal = new ResourceWorkingTimeCalendar(timeZone, def);
        }

        return cal;
    }

}
