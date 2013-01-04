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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.calendar;

import java.util.Date;

import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IVCalendarPersistence;
import net.project.persistence.IXMLPersistence;

/**
 * Interface that objects must implement to be included on a calendar.
 *
 * @author AdamKlatzkin    03/00
 */
public interface ICalendarEntry
    extends IVCalendarPersistence, IJDBCPersistence, IXMLPersistence {

    /**
     * Get the ID of the object.  The ID is the object_id of the object in the
     * database.
     *
     * @return the database object_id for this object.
     */
    public String getID();

    /**
     * Get the type of the object as registered in the database. (pn_object
     * table).
     *
     * @return the type string for the object as defined in the database.
     */
    public String getType();

    /**
     * Get the name of the event.
     *
     * @return the event name
     */
    public String getName();

    /**
     * Get the description of the event.
     *
     * @return the event description
     */
    public String getDescription();

    /**
     * Gets the date and time that this item should first appear on the calendar.
     * For example, the time and date that a 2 day meeting begins.
     *
     * @return a Date with time and date set.
     */
    public Date getStartTime();

    /**
     * Gets the date and time that this item should last appear on the calendar.
     * For example, the time and date that a 2 day meeting end.
     *
     * @return a Date with time and date set.
     */
    public Date getEndTime();

}
