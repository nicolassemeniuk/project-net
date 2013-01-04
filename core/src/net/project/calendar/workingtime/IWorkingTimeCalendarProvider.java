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

 package net.project.calendar.workingtime;

import java.io.Serializable;
import java.util.Collection;
import java.util.TimeZone;

import net.project.persistence.PersistenceException;

/**
 * Implementing classes provide working time calendars.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public interface IWorkingTimeCalendarProvider extends Cloneable, Serializable {

    /**
     * Indicates whether the calendar with the specified ID is the
     * default calendar in the current context.
     * @param calendarID the ID of the calendar to check
     * @return true if the calendar is default; false otherwise
     * @throws NullPointerException if calendarID is null
     */
    boolean isDefault(String calendarID);

    /**
     * Returns the default working time calendar definition.
     * @return the working time calendar definition which is the
     * default one in the current context; this method should always
     * return a working time calendar
     */
    WorkingTimeCalendarDefinition getDefault();

    /**
     * Returns the working time calendar definition with the specified ID.
     * @param calendarID the ID of the working time calendar definition
     * to get
     * @return the working time calendar definition with the specified ID
     * or null if no calendar is found with that ID
     * @throws NullPointerException if calendarID is null
     */
    WorkingTimeCalendarDefinition get(String calendarID);

    
    /**
     * Returns the working time calendar definition for the specified work plan ID.
     * @param planID the ID of the work plan for which the calendar is defined
     * @return the plan's calendar or null if none is found for the resource
     * @throws NullPointerException if the specified ID is null
     */
    WorkingTimeCalendarDefinition getForPlanID(String planID);
    
    /**
     * Returns the working time calendar definition for the specified resource person ID.
     * @param resourcePersonID the ID of the person who is the resource that owns
     * the calendar
     * @return the resource's calendar or null if none is found for the resource
     * @throws NullPointerException if the specified ID is null
     */
    WorkingTimeCalendarDefinition getForResourceID(String resourcePersonID);

    /**
     * Returns all working time calendar definitions in the current context.
     * @return a collection where each element is a <code>WorkingTimeCalendarDefinition</code>
     */
    Collection getAll();

    /**
     * Returns all base working time calendar definitions in the current context.
     * @return a collection where each element is a <code>WorkingTimeCalendarDefinition</code>
     * and is a base calendar
     */
    Collection getBaseCalendars();

    /**
     * Removes the working time calendar with the specified ID.
     * @param calendarID the ID of the calendar to remove
     * @throws PersistenceException if there is a problem removing
     */
    void remove(String calendarID) throws PersistenceException;

    /**
     * Stores the working time calendar with the specified ID.
     * @param calendarID the ID of the calendar to store
     * @throws PersistenceException if there is a problem storing
     */
    void store(String calendarID) throws PersistenceException;

    /**
     * Creates the specified calendar by storing it and ensures that the
     * calendar becomes available from this provider.
     * @param calendarDef the calendar definition to create
     * @return the ID of the newly created calendar
     * @throws PersistenceException if there is a problem creating
     */
    String create(WorkingTimeCalendarDefinition calendarDef) throws PersistenceException;

    /**
     * Changes the default calendar for the one with the specified ID.
     * @param defaultCalendarID the ID of the default calendar
     * @throws PersistenceException if there is a problem making the change
     * @throws NullPointerException if the defaultCalendarID is null
     * @throws IllegalArgumentException if no calendar is found with the
     * specified ID
     */
    void changeDefaultCalendar(String defaultCalendarID) throws PersistenceException;

    /**
     * Returns the default time zone to use when no other time zone context is available.
     * @return the default time zone
     */
    TimeZone getDefaultTimeZone();

    Object clone();
}
