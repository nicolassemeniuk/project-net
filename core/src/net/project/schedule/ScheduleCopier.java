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
|    $Revision: 20949 $
|        $Date: 2010-06-16 10:02:51 -0300 (mié, 16 jun 2010) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.schedule;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import net.project.calendar.workingtime.WorkingTimeCalendarDefinitionCopier;
import net.project.document.DocumentCopier;
import net.project.persistence.PersistenceException;
import net.project.space.SpaceFactory;

/**
 * Provides a mechanism for copying existing schedules from one space to another.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class ScheduleCopier {

    private final String fromSpaceID;
    private final String toSpaceID;

    /**
     * Creates a new Schedule Copier to copy the schedule that exists
     * for one space to a new space.
     * @param fromSpaceID the space from which to copy the schedule
     * @param toSpaceID the space to which to copy the schedule
     */
    public ScheduleCopier(String fromSpaceID, String toSpaceID) {
        this.fromSpaceID = fromSpaceID;
        this.toSpaceID = toSpaceID;
    }

    /**
	 * Copies all the schedule and all tasks and base calendars from one space
	 * to another.
	 * 
	 * @param phaseIDMap
	 *            a <code>HashMap</code> with a map between old phases and new
	 *            phases. This object is needed to get the cross references
	 *            between process and schedule.
	 * 
	 * @throws PersistenceException
	 *             if there is a problem loading or storing
	 */
	public void copyAll(HashMap phaseIDMap) throws PersistenceException {
		// First copy the schedule and all tasks
		Schedule schedule = new Schedule();
		schedule.setSpace(SpaceFactory.constructSpaceFromID(this.fromSpaceID));
		schedule.load();
		schedule.setTaskType(TaskType.ALL);
		schedule.loadEntries(true, true);
		try {
			// Remember the default calendar
			String currentDefaultCalendarID = schedule.getDefaultCalendarID();
			// Copy the schedule to a new plan
			String targetPlanID = schedule.copyToSpace(this.toSpaceID, null, phaseIDMap);

			// Now copy base calendars
			Map calendarIDMap = new WorkingTimeCalendarDefinitionCopier(schedule.getID(), targetPlanID).copyBaseCalendars();
			if (currentDefaultCalendarID != null) {
				// Update default calendar ID
				Schedule targetSchedule = new Schedule();
				targetSchedule.setID(targetPlanID);
				targetSchedule.load();
				targetSchedule.setDefaultCalendarID((String) calendarIDMap.get(currentDefaultCalendarID));
				targetSchedule.store();
			}
		} catch (MissingScheduleException e) {
			//Swallow this exception.  The target space doesn't have a
			//schedule module, so we aren't going to try to copy the
			//schedule.
			Logger.getLogger(ScheduleCopier.class).error("ScheduleCopier.copyAll failed missing schedule : " + e.getMessage());
		} catch (PersistenceException e) {
			Logger.getLogger(ScheduleCopier.class).error("ScheduleCopier.copyAll failed unable to load entries: " + e.getMessage());
		} catch (Exception e) {
			Logger.getLogger(ScheduleCopier.class).error("ScheduleCopier.copyAll failed : " + e.getMessage());
		}
	}
}
