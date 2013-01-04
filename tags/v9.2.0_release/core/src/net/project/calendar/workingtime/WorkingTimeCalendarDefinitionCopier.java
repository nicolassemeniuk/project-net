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
package net.project.calendar.workingtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.project.persistence.PersistenceException;

/**
 * Provides facilities to copy working time calendar definitions.
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class WorkingTimeCalendarDefinitionCopier {

    private final String fromPlanID;
    private final String toPlanID;

    /**
     * Creates a copier to copy calendars from one plan to another.
     * The target plan is assumed to exist.
     * @param fromPlanID the ID of the plan from which to copy
     * @param toPlanID the ID of the plan to which to copy calendars
     */
    public WorkingTimeCalendarDefinitionCopier(String fromPlanID, String toPlanID) {
        this.fromPlanID = fromPlanID;
        this.toPlanID = toPlanID;
    }

    /**
     * Copies all base calendars from one plan to the other.
     * <p>
     * All calendar entries are copied.
     * </p>
     * @return a map of old calendar ID to new calendar ID
     * @throws PersistenceException if there is a problem copying
     */
    public Map copyBaseCalendars() throws PersistenceException {

        Collection baseCalendars = new ArrayList();

        for (Iterator it = new WorkingTimeCalendarFinder().findByPlanID(fromPlanID).iterator(); it.hasNext();) {
            WorkingTimeCalendarDefinition nextCalendarDef = (WorkingTimeCalendarDefinition) it.next();

            if (nextCalendarDef.isBaseCalendar()) {
                baseCalendars.add(nextCalendarDef);
            }
        }

        // A map of old ID to new ID
        Map calendarIDMap = new HashMap();

        // All calendar planIDs are updated, IDs are null
        // Storing will insert calendars
        for (Iterator it = baseCalendars.iterator(); it.hasNext();) {
            WorkingTimeCalendarDefinition nextCalendarDef = (WorkingTimeCalendarDefinition) it.next();
            String oldCalendarID = nextCalendarDef.getID();
            // Reset the ID so the calendar is inserted during store
            nextCalendarDef.setID(null);
            nextCalendarDef.setPlanID(toPlanID);
            nextCalendarDef.store();
            // Now store the mapping from old to new
            calendarIDMap.put(oldCalendarID, nextCalendarDef.getID());
        }

        return calendarIDMap;
    }

}
