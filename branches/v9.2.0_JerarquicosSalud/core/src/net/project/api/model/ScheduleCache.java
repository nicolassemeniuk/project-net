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
package net.project.api.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import net.project.schedule.Schedule;

/**
 * Provides a synchronized cache of Schedule objects.
 *
 * @author Tim Morrow
 * @since Version 7.6.4
 */
public class ScheduleCache {

    private static final String CACHE_ATTRIBUTE_NAME = "net.project.api.model.ScheduleCache";

    /**
     * Fetches the schedule cache from the servlet context.
     * <p>
     * Initializes if none is found.
     * </p>
     * @param servletContext
     * @return the cache
     */
    public static ScheduleCache get(ServletContext servletContext) {

        ScheduleCache cache = (ScheduleCache) servletContext.getAttribute(CACHE_ATTRIBUTE_NAME);
        if (cache == null) {
            cache = new ScheduleCache();
            servletContext.setAttribute(CACHE_ATTRIBUTE_NAME, cache);
        }

        return cache;
    }

    private final Map entryMap = Collections.synchronizedMap(new HashMap());

    public void clear() {
        this.entryMap.clear();
    }

    public void add(Schedule schedule) {
        this.entryMap.put(schedule.getID(), schedule);
    }

    public Schedule get(String scheduleID) {
        return (Schedule) this.entryMap.get(scheduleID);
    }

    public int size() {
        return this.entryMap.size();
    }

}
