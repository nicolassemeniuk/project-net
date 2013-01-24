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
package net.project.schedule;

import java.sql.SQLException;
import java.util.Map;

import net.project.base.Identifiable;
import net.project.crossspace.interdependency.IWorker;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * This class knows what to do if an external object forces a change to a
 * schedule.  Basically, if the schedule is of the "autocalculate" sort, it is
 * going to force a recalculation of the schedule.
 *
 * This object is generally executed asynchronously, so it generally just logs
 * its exceptions to the logger instead of throwing them.
 *
 * @author Matthew Flower
 * @since Version 8.0.0
 */
public class ScheduleChangeWorker implements IWorker {
    private Logger logger = Logger.getLogger(ScheduleChangeWorker.class);

    public void execute(DBBean db, Identifiable changedObject, String scheduleID, String copiedObjectID) {
        try {
            ScheduleFinder sf = new ScheduleFinder();
            Schedule schedule = sf.findByPlanID(scheduleID);

            //Make sure that the copied object in this plan has changed.  We'll
            //do recalculation at the same time.
            updateObject(db, changedObject, copiedObjectID, schedule);
            schedule.setWarnings(1, db);
        } catch (PersistenceException e) {
            logger.error("Unable to load schedule due to unexpected persistence error", e);
        }
    }

    private void updateObject(DBBean db, Identifiable changedObject, String copiedObjectID, Schedule schedule) {

        try {
            Map scheduleEntryMap = schedule.getEntryMap();

            ScheduleEntry copiedObject = (ScheduleEntry)scheduleEntryMap.get(copiedObjectID);

            if (changedObject instanceof ScheduleEntry && ((ScheduleEntry) changedObject).getRecordStatus().equals("D")) {
                //The original object was deleted, so the copied object
                //needs to be too.
                //sjmittal: also note that remove method for schedule is not yet implemented
                //this is why it would be applicable for schedule entry only
                copiedObject.remove();
            } else {

                //Recopy the information from the other space
                if (changedObject instanceof ScheduleEntry)
                    ((ScheduleEntry) changedObject).updateSharingProperties(copiedObject);
                else //this has to be schedule now
                    ((Schedule) changedObject).setFieldsFromSchedule(copiedObject);

                //Restore the object in the other space.
                //but don't calculate that schedules end points
                //this is as per the new design
                copiedObject.setSendNotifications(false);
                copiedObject.store(false, schedule, db);
                copiedObject.setSendNotifications(true);
            }
        } catch (PersistenceException e) {
            logger.error("Unable to update dependent object due to unexpected persistence exception.", e);
        } catch (SQLException e) {
            logger.error("Unable to update dependent object due to unexpected sql exception.", e);
        }
    }
}
