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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import net.project.persistence.PersistenceException;
import net.project.schedule.Schedule;

/**
 * Provides capability for loading and maintaining working time calendars.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class ScheduleWorkingTimeCalendarProvider implements IWorkingTimeCalendarProvider {

    //
    // Static Members
    //

    /**
     * Returns a provider containing working time calendars for the specified plan.
     * @param schedule the schedule for which to load calendars; provides
     * the default calendar ID and the plan ID
     * @return a provider that provides working time calendars for the
     * specified plan
     * @throws PersistenceException if there is a problem loading
     */
    public static IWorkingTimeCalendarProvider make(Schedule schedule) throws PersistenceException {
        return new ScheduleWorkingTimeCalendarProvider(schedule);
    }

    //
    // Instance members
    //

    /**
     * Handle on current schedule.
     */
    private final Schedule schedule;

    /** The time zone in which times are displayed and stored in the schedule. */
    private final TimeZone timeZone;

    /**
     * A map where each key is a <code>String</code> calendarID and each
     * value is a <code>WorkingTimeCalendarDefinition</code>.
     */
    private final Map calendarsByID = new HashMap();

    /**
     * a map where each key is a <code>String</code> resourceID and each
     * value is a <code>WorkingTimeCalendarDefinition</code>.
     */
    private final Map resourceCalendarByResourceID = new HashMap();

    /**
     * The default calendar definition.
     */
    private WorkingTimeCalendarDefinition defaultCalendarDef;


    /**
     * Creates a provider for the specified schedule.
     * @param schedule the schedule for which to create a provider
     * @throws PersistenceException if there is a problem loading
     */
    private ScheduleWorkingTimeCalendarProvider(Schedule schedule) throws PersistenceException {
        this.schedule = schedule;
        this.timeZone = schedule.getTimeZone();
        loadAll();

        WorkingTimeCalendarDefinition defaultCalendarDef = null;

        if (this.schedule.getDefaultCalendarID() != null) {
            defaultCalendarDef = (WorkingTimeCalendarDefinition) this.calendarsByID.get(this.schedule.getDefaultCalendarID());
        }

        if (defaultCalendarDef == null) {
            // Schedule had no default, or it's no longer with us
            // We'll construct a system default one
            // This is used in working time calendar calculations
            defaultCalendarDef = WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition();
        }

        // Save it as the default calendar
        this.defaultCalendarDef = defaultCalendarDef;

    }

    /**
     * Creates a provider as a clone of the specified one.
     * @param provider the provider to clone
     */
    private ScheduleWorkingTimeCalendarProvider(ScheduleWorkingTimeCalendarProvider provider) {
        this.schedule = provider.schedule;
        this.calendarsByID.putAll(provider.calendarsByID);
        this.resourceCalendarByResourceID.putAll(provider.resourceCalendarByResourceID);
        this.defaultCalendarDef = provider.defaultCalendarDef;
        this.timeZone = provider.timeZone;
    }

    /**
     * Loads all working time calendar definitions for the current plan.
     * <p>
     * Updates the {@link #calendarsByID} map and {@link #resourceCalendarByResourceID} map.
     * </p>
     * @throws PersistenceException if there is a problem loading; the maps will not have been
     * modified in this case
     */
    private void loadAll() throws PersistenceException {

        // Local variable so we can update specified map somewhat atomically
        Map localCalendarDefMap = new HashMap();
        Map localResourceCalendarDefMap = new HashMap();

        // First place all calendar definitions in the map
        for (Iterator it = new WorkingTimeCalendarFinder().findByPlanID(this.schedule.getID()).iterator(); it.hasNext();) {
            WorkingTimeCalendarDefinition nextCalendarDef = (WorkingTimeCalendarDefinition) it.next();
            localCalendarDefMap.put(nextCalendarDef.getID(), nextCalendarDef);
        }

        // Now update resource calendars to inherit from their base calendars
        // We do this after loading so that we know all base calendars are definitely loaded
        for (Iterator it = localCalendarDefMap.values().iterator(); it.hasNext();) {
            WorkingTimeCalendarDefinition nextCalendarDef = (WorkingTimeCalendarDefinition) it.next();

            if (!nextCalendarDef.isBaseCalendar()) {
                // Set the parent calendar definition
                nextCalendarDef.setParentCalendarDefinition(localCalendarDefMap);

                // Add to resource calendar map
                localResourceCalendarDefMap.put(nextCalendarDef.getResourcePersonID(), nextCalendarDef);
            }
        }

        // Successfully updated all calendars; update maps
        this.calendarsByID.clear();
        this.calendarsByID.putAll(localCalendarDefMap);
        this.resourceCalendarByResourceID.clear();
        this.resourceCalendarByResourceID.putAll(localResourceCalendarDefMap);
    }


    /**
     * Indicates whether the calendar with the specified ID is the
     * default calendar for the current plan.
     * @param calendarID the ID of the calendar to check
     * @return true if the calendar is default; false otherwise
     * @throws NullPointerException if calendarID is null
     */
    public boolean isDefault(String calendarID) {

        if (calendarID == null) {
            throw new NullPointerException("calendarID is required");
        }

        return (calendarID.equals(this.schedule.getDefaultCalendarID()));
    }

    /**
     * Returns the default working time calendar definition.
     * @return the working time calendar definition which is the
     * default one for the plan; if there is none, a system-default
     * working time calendar is returned
     */
    public WorkingTimeCalendarDefinition getDefault() {
        return this.defaultCalendarDef;
    }

    /**
     * Returns the working time calendar definition with the specified ID.
     * @param calendarID the ID of the working time calendar definition
     * to get
     * @return the working time calendar definition with the specified ID
     * or null if no calendar is found with that ID
     * @throws NullPointerException if calendarID is null
     */
    public WorkingTimeCalendarDefinition get(String calendarID) {

        if (calendarID == null) {
            throw new NullPointerException("calendarID is required");
        }

        return (WorkingTimeCalendarDefinition) this.calendarsByID.get(calendarID);
    }

    public WorkingTimeCalendarDefinition getForPlanID(String planID) {
        return getDefault();
    }

    /**
     * Returns the working time calendar definition for the specified resource person ID.
     * @param resourcePersonID the ID of the person who is the resource that owns
     * the calendar
     * @return the resource's calendar or the default calendar if none is found
     * for that resource or the specified resource person ID is null
     */
    public WorkingTimeCalendarDefinition getForResourceID(String resourcePersonID) {

        if (resourcePersonID == null) {
            throw new NullPointerException("resourcePersonID is required");
        }

        return (WorkingTimeCalendarDefinition) this.resourceCalendarByResourceID.get(resourcePersonID);
    }

    /**
     * Returns all working time calendar definitions in the current context.
     * @return a collection where each element is a <code>WorkingTimeCalendarDefinition</code>
     */
    public Collection getAll() {
        return Collections.unmodifiableCollection(this.calendarsByID.values());
    }

    /**
     * Returns all base working time calendar definitions in the current context.
     * @return a collection where each element is a <code>WorkingTimeCalendarDefinition</code>
     * and is a base calendar
     */
    public Collection getBaseCalendars() {
        Collection baseCalendars = new ArrayList();
        for (Iterator it = getAll().iterator(); it.hasNext();) {
            WorkingTimeCalendarDefinition nextDefinition = (WorkingTimeCalendarDefinition) it.next();
            if (nextDefinition.isBaseCalendar()) {
                baseCalendars.add(nextDefinition);
            }
        }
        return Collections.unmodifiableCollection(baseCalendars);
    }

    /**
     * Stores the working time calendar with the specified ID.
     * @param calendarID the ID of the calendar to store
     * @throws PersistenceException if there is a problem storing;
     * also if there is no working day of week entry (when resolving from
     * this calendar and the parent calendar)
     * @throws NullPointerException if calendarID is null
     * @throws IllegalArgumentException if no calendar for the specified ID is found
     */
    public void store(String calendarID) throws PersistenceException {

        if (calendarID == null) {
            throw new NullPointerException("calendarID is required");
        }

        WorkingTimeCalendarDefinition calendarDef = (WorkingTimeCalendarDefinition) this.calendarsByID.get(calendarID);
        if (calendarDef == null) {
            throw new IllegalArgumentException("No working time calendar found for ID " + calendarID);
        }

        // Now store it
        calendarDef.store();
    }

    /**
     * Stores the working time calendar and updates this provider.
     * @param calendarDef the calendar definition to store
     * @return the ID of the new calendar
     * @throws PersistenceException
     */
    public String create(WorkingTimeCalendarDefinition calendarDef) throws PersistenceException {
        calendarDef.setPlanID(this.schedule.getID());
        if (!calendarDef.isBaseCalendar()) {
            // Ensure the calendar's parent calendar is updated; we need this prior
            // to storing to ensure days of week entries are defined correctly
            calendarDef.setParentCalendarDefinition(this.calendarsByID);
        }

        // Store the calendar, creating it
        calendarDef.store();

        // Update this provider with the newly stored calendar definition
        this.calendarsByID.put(calendarDef.getID(), calendarDef);
        if (!calendarDef.isBaseCalendar()) {
            this.resourceCalendarByResourceID.put(calendarDef.getResourcePersonID(), calendarDef);
        }

        return calendarDef.getID();
    }

    /**
     * Removes the working time calendar with the specified ID.
     * <p>
     * The default calendar may not be removed. <br>
     * All calendars are reloaded since removing a calendar may cause other calendars
     * to change (removing a resource calendar's parent calendar causes the resource
     * calendar to be changed to using the plan default calendar).
     * </p>
     * @param calendarID the ID of the calendar to remove
     * @throws PersistenceException if there is a problem removing or the default calendar
     * is attempted to be removed
     * @throws NullPointerException if calendarID is null
     * @throws IllegalArgumentException if no calendar for the specified ID is found
     */
    public void remove(String calendarID) throws PersistenceException {

        if (calendarID == null) {
            throw new NullPointerException("calendarID is required");
        }

        WorkingTimeCalendarDefinition calendarDef = (WorkingTimeCalendarDefinition) this.calendarsByID.get(calendarID);
        if (calendarDef == null) {
            throw new IllegalArgumentException("No working time calendar found for ID " + calendarID);
        }

        if (isDefault(calendarID)) {
            throw new PersistenceException("You may not remove the default calendar");
        }

        // Now remove the calendar definition specifying the default
        // calendar as the replacement
        calendarDef.remove(getDefault().getID());

        // At this point, it was successfully removed
        // Now we remove it from our cache
        removeFromMap(calendarDef);

        // As a result of removing, it is possible that resource calendars
        // have changed (their parent calendar may have been removed)
        // We need to reload
        loadAll();

    }

    /**
     * Removes the specified working time calendar definition from all maps.
     * @param calendarDef the calendar definition to remove
     */
    private void removeFromMap(WorkingTimeCalendarDefinition calendarDef) {

        this.calendarsByID.remove(calendarDef.getID());

        for (Iterator it = this.resourceCalendarByResourceID.values().iterator(); it.hasNext();) {
            WorkingTimeCalendarDefinition nextcalendarDefinition = (WorkingTimeCalendarDefinition) it.next();
            if (nextcalendarDefinition.getID().equals(calendarDef.getID())) {
                this.resourceCalendarByResourceID.remove(calendarDef.getID());
                break;
            }
        }
    }

    /**
     * Changes the default calendar to the calendar with the specified
     * ID and updates the schedule.
     * @param defaultCalendarID
     * @throws PersistenceException
     */
    public void changeDefaultCalendar(String defaultCalendarID) throws PersistenceException {

        if (defaultCalendarID == null) {
            throw new NullPointerException("defaultCalendarID is required");
        }

        if (!this.calendarsByID.containsKey(defaultCalendarID)) {
            throw new IllegalArgumentException("No working time calendar found for ID " + defaultCalendarID);
        }

        // First update the schedule
        this.schedule.setDefaultCalendarID(defaultCalendarID);
        this.schedule.store();

        // Now replace our default calednar
        this.defaultCalendarDef = get(defaultCalendarID);
    }

    /**
     * Returns the schedule default time zone to use for working times when
     * no other time zone context is available.
     * @return the schedule default time zone
     */
    public TimeZone getDefaultTimeZone() {
        return this.schedule.getTimeZone();
    }

    /**
     * Performs a shallow clone of this workingtime calendar provider.
     * <p/>
     * The clone is currently shallow due to no current requirement for a
     * deep clone.
     * @return the cloned instance
     */
    public Object clone() {
        return new ScheduleWorkingTimeCalendarProvider(this);
    }

}
