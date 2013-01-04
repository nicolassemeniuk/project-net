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
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.notification;

import java.util.Date;

import net.project.persistence.IXMLPersistence;

/**
 * Provides an event about which a person may wish to be notified.
 * <p>
 * Events typically occur as the result of a user's action.
 * Any users subscribed to an event type on a particular object or class of
 * objects will be notified when that event occurs on that object or
 * class of objects.
 * </p>
 * 
 * @author unascribed
 * @author Tim Morrow
 * @since Version 2
 */
public interface INotificationEvent extends IXMLPersistence {

    /**
     * Returns the type of the event.
     * <p>
     * This should be one of constants defined in {@link EventCodes}.
     * </p>
     * @return the event type
     */
    public String getEventType();

    /**
     * Returns the date time at which the event occurred.
     * @return the date time
     */
    public Date getEventTime();

    /**
     * Returns the description of the event.
     * @return the description
     */
    public String getDescription();

    /**
     * Returns the XML representation of the object that caused
     * the event.
     * @return the XML representation
     */
    public String getTargetObjectXML();

    /**
     * Returns the object type of the object that caused the event.
     * @return the object type
     */
    public String getTargetObjectType();

    /**
     * Returns the ID of the object that caused the event.
     * @return the ID of the object
     */
    public String getTargetObjectID();

    /**
     * Returns the ID of the person performing the action to
     * cause the event.
     * @return the person ID
     */
    public String getInitiatorID();

    /**
     * Returns the ID of the space in which the event occurred.
     * @return the space ID
     */
    public String getSpaceID();
}
