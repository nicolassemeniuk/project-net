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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
|
+--------------------------------------------------------------------------------------*/
package net.project.schedule;

import java.util.Date;

import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.resource.PersonalPropertyMap;
import net.project.util.Node;
import net.project.util.NodeFactory;
import net.project.util.TimeQuantity;

/**
 * Interface that objects must implement to be included on a schedule.
 *
 * @author Roger Bly
 * @since 01/2000
 */
public interface IScheduleEntry extends IJDBCPersistence, IXMLPersistence {
    /**
     * Gets the primary key of this schedule entry.
     *
     * @return a <code>String</code> containing the primary key of this schedule
     * entry.
     */
    public String getID();

    /**
     * Gets the type id of this schedule entry.
     *
     * @return a <code>String</code> containing the type id of this schedule
     * entry.  This should correspond to one of the id's defined in
     * {@link net.project.schedule.TaskType}.
     */
    public String getType();

    /**
     * Gets the human readable name for this schedule entry.
     *
     * @return a <code>String</code> containing the human-readable name for this
     * schedule entry.
     */
    public String getName();

    /**
     * Gets a prose description for this task.
     *
     * @return a <code>String</code> containing a prose description for this
     * schedule entry.
     */
    public String getDescription();

    /**
     * Gets the date and time that this item should first appear on the
     * calendar. For example, the time and date that a 2 day meeting begins.
     *
     * @return a Date with time and date set.
     */
    public Date getStartTime();

    /**
     * Gets the date and time that this item should last appear on the calendar.
     * For example, the time and date that a 2 day meeting ends.
     *
     * @return a Date with time and date set.
     */
    public Date getEndTime();

    /**
     * This method also returns the name of the task, but if the name is longer
     * than 40 characters, it truncates it to 40 characters and appends "...".
     *
     * @return a <code>String</code> containing the task name, unless the task
     * name is longer than 40 characters.
     */
    String getNameMaxLength40();
    
    public void addToJSONStore(final NodeFactory factory, final PersonalPropertyMap propertyMap, final IWorkingTimeCalendarProvider provider, final boolean forFlatView, final boolean childrenVisible);
    
    public String getXMLBody();

    public String getXML();

    /**
     * Get the id of the space the task belongs to. Note: task does not need
     * to be loaded but task id must be set
     *
     * @return a <code>String</code> value containing the id of the space that
     * the task belongs to.
     * @throws PersistenceException if the method was unable to look up the space
     * id in the database.
     * @throws NullPointerException if the task id is null.
     * @since 05/00
     */
    String getSpaceID() throws PersistenceException;

    /**
     * Get the total amount of work required for this task as a time quantity.
     * 
     * @return a <code>TimeQuantity</code> containing the total amount of work
     *         required to complete this task from start to finish.
     */
    TimeQuantity getWorkTQ();
}




