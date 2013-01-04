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
package net.project.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import net.project.calendar.workingtime.DefinitionBasedWorkingTimeCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.ScheduleWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.calendar.workingtime.WorkingTimeCalendarFinder;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.security.User;

/**
 * This class finds all the WorkingTimeCalendarDefinition that a user currently
 * is responsible for.
 *
 * @author Matthew Flower
 * @author Sachin Mittal
 * @since Version 7.7.0
 */
public class ResourceWorkingTimeCalendarProvider implements IWorkingTimeCalendarProvider {
    
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
    public static IWorkingTimeCalendarProvider make(User user) throws PersistenceException {
        return new ResourceWorkingTimeCalendarProvider(user);
    }

    //
    // Instance members
    //
    
    private final User user;

    /**
     * A map where each key is a <code>String</code> planID and each
     * value is a <code>WorkingTimeCalendarDefinition</code>.
     * If a default calendar for the resource is defined than its planID = null
     */
    private final Map planIDToDefinition = new HashMap();
    
    /**
     * A map where each key is a <code>String</code> calendarID and each
     * value is a <code>WorkingTimeCalendarDefinition</code>.
     */
    private final Map calendarsByID = new HashMap();
    
    /**
     * The default calendar definition.
     */
    private WorkingTimeCalendarDefinition defaultCalendarDef;
    
    private ResourceWorkingTimeCalendarProvider(User user) throws PersistenceException {
        this.user = user;
        load();
        
        WorkingTimeCalendarDefinition defaultCalendarDef = (WorkingTimeCalendarDefinition) planIDToDefinition.get(null);
        if (defaultCalendarDef == null) {
            // Resource had no default, or it's no longer with us
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
    private ResourceWorkingTimeCalendarProvider(ResourceWorkingTimeCalendarProvider provider) {
        this.user = provider.user;
        this.calendarsByID.putAll(provider.calendarsByID);
        this.planIDToDefinition.putAll(provider.planIDToDefinition);
        this.defaultCalendarDef = provider.defaultCalendarDef;
    }
    
    private void load() throws PersistenceException {
        // Local variable so we can update specified map somewhat atomically
        Map localPlanIDToDefinition = new HashMap();
        Map localCalendarsByID = new HashMap();

        WorkingTimeCalendarFinder finder = new WorkingTimeCalendarFinder();
        Collection definitions = finder.findByUserID(user.getID());

        for (Iterator it = definitions.iterator(); it.hasNext();) {
            WorkingTimeCalendarDefinition definition = (WorkingTimeCalendarDefinition) it.next();
            localPlanIDToDefinition.put(definition.getPlanID(), definition);
            localCalendarsByID.put(definition.getID(), definition);
        }
        
        // Successfully updated all calendars; update maps
        this.calendarsByID.clear();
        this.calendarsByID.putAll(localCalendarsByID);
        this.planIDToDefinition.clear();
        this.planIDToDefinition.putAll(localPlanIDToDefinition);
    }

    public void changeDefaultCalendar(String defaultCalendarID) throws PersistenceException {
        throw new UnsupportedOperationException("Cannot change the resources default calendar");
    }

    public WorkingTimeCalendarDefinition get(String calendarID) {
        return (WorkingTimeCalendarDefinition) calendarsByID.get(calendarID);
    }

    public Collection getAll() {
        return Collections.unmodifiableCollection(this.calendarsByID.values());
    }

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

    public WorkingTimeCalendarDefinition getDefault() {
        return this.defaultCalendarDef;
    }

    public WorkingTimeCalendarDefinition getForPlanID(String planID) {
        WorkingTimeCalendarDefinition def = get(planID);

        if (def == null) {
            return defaultCalendarDef;
        }

        return def;
    }

    public WorkingTimeCalendarDefinition getForResourceID(String resourcePersonID) {
        return getDefault();
    }

    public boolean isDefault(String calendarID) {
        WorkingTimeCalendarDefinition defaultWorkingTimeCalendarDefinition = getDefault();
        if(defaultWorkingTimeCalendarDefinition.getID() != null)
        {
            return defaultWorkingTimeCalendarDefinition.getID().equals(calendarID);
        }
        return false;
    }
    
    /*
     * This API is used only to create the user base calander in his personal workspcae
     * 
     * For creating Resource calandars in work plan spaces refer <code>ScheduleWorkingTimeCalendarProvider</code>
     * @see net.project.calendar.workingtime.ScheduleWorkingTimeCalendarProvider#create(net.project.calendar.workingtime.WorkingTimeCalendarDefinition)
     */
    public String create(WorkingTimeCalendarDefinition calendarDef) throws PersistenceException {
        calendarDef.setResourcePersonID(this.user.getID());
        calendarDef.setBaseCalendar(true);
        calendarDef.setPlanID(null);
        if (getDefault().getID() != null) {
            throw new IllegalStateException("User " + user.getDisplayName() + " already has a personal calendar with id " + getDefault().getID() + 
                    ". So cannot create one.");
        }

        // Store the calendar, creating it
        calendarDef.store();

        // Update this provider with the newly stored calendar definition
        this.calendarsByID.put(calendarDef.getID(), calendarDef);

        return calendarDef.getID();
    }

    public void remove(String calendarID) throws PersistenceException {
        throw new UnsupportedOperationException("Cannot remove the resources default calendar");
    }

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
    
    public TimeZone getDefaultTimeZone() {
        return user.getTimeZone();
    }
    
    /**
     * Performs a shallow clone of this workingtime calendar provider.
     * <p/>
     * The clone is currently shallow due to no current requirement for a
     * deep clone.
     * @return the cloned instance
     */
    public Object clone() {
        return new ResourceWorkingTimeCalendarProvider(this);
    }
}
