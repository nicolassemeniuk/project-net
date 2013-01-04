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
package net.project.api.handler;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.schedule.ScheduleEntry;
import net.project.xml.XMLUtils;

public class TaskApiXML {
    public static String createXML(List tasks) {
        if (tasks == null || tasks.size() == 0) {
            return "";
        }

        StringBuffer xml = new StringBuffer();
        xml.append("<tasklist>");
        for (Iterator it = tasks.iterator(); it.hasNext();) {
            ScheduleEntry scheduleEntry = (ScheduleEntry) it.next();
            xml.append("<task>");
            xml.append("<id>" + scheduleEntry.getID() + "</id>");
            xml.append("<seq>" + scheduleEntry.getSequenceNumber() + "</seq>");
            xml.append("<name>" + XMLUtils.escape(scheduleEntry.getName()) + "</name>");
            xml.append("<desc>" + XMLUtils.escape(scheduleEntry.getDescription()) + "</desc>");
            xml.append("<type>" + XMLUtils.escape(scheduleEntry.getTaskType().getName()) + "</type>");
            xml.append("<isMilestone>" + (scheduleEntry.isMilestone() ? "1" : "0") + "</isMilestone>");

            Date startTime = scheduleEntry.getStartTime();
            Date endTime = scheduleEntry.getEndTime();
            if (startTime == null) {
                xml.append("<startDateTime/>");
            } else {
                xml.append("<startDateTime>" + XMLUtils.formatISODateTime(startTime) + "</startDateTime>");
            }

            if (endTime == null) {
                xml.append("<endDateTime/>");
            } else {
                xml.append("<endDateTime>" + XMLUtils.formatISODateTime(endTime) + "</endDateTime>");
            }
            xml.append("<duration>" + scheduleEntry.getDurationFormatted() + "</duration>");
            xml.append("<work>" + XMLUtils.escape(scheduleEntry.getWork()) + "</work>");
            xml.append("<workUnits>" + XMLUtils.escape(scheduleEntry.getWorkUnitsString()) + "</workUnits>");
            xml.append("<workComplete>" + XMLUtils.escape(scheduleEntry.getWorkComplete()) + "</workComplete>");
            xml.append("<workCompleteUnits>" + XMLUtils.escape(scheduleEntry.getWorkCompleteUnitsString()) + "</workCompleteUnits>");
            xml.append("<priority>" + scheduleEntry.getPriority().getID() + "</priority>");
            xml.append("<percentComplete>" + scheduleEntry.getPercentComplete() + "</percentComplete>");
            xml.append("<workPercentComplete>" + scheduleEntry.getWorkPercentCompleteDecimal().multiply(new BigDecimal(100)) + "</workPercentComplete>");

            xml.append("</task>");
        }
        xml.append("</tasklist>");

        return xml.toString();
    }
}
