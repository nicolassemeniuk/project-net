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

import javax.servlet.http.HttpServletRequest;

import net.project.persistence.PersistenceException;
import net.project.resource.Person;
import net.project.resource.Roster;
import net.project.schedule.Schedule;
import net.project.security.User;

/**
 * Provides a helper for creating a working time calendar.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class WorkingTimeCalendarCreateHelper {

    /**
     * The current schedule.
     */
//    private final Schedule schedule;

    /**
     * The provider used for fetching working time calendar definitions.
     */
    private final IWorkingTimeCalendarProvider provider;

    /**
     * The current roster.
     */
    private final Roster roster;

    /**
     * The type of calendar being created.
     */
    private CalendarType type = null;

    /**
     * The name of the base calendar.
     */
    private String name = null;

    /**
     * The ID of the resource when creating a resource calendar.
     */
    private String resourceID = null;

    /**
     * The ID of the parent calendar when creating a resource calendar.
     */
    private String parentCalendarID = null;

    /**
     * Creates a new WorkingTimeCalendarCreateHelper from the specified request.
     * Expects to find <code>schedule</code> and <code>user</code> in the session.
     * @param request the request
     */
    public WorkingTimeCalendarCreateHelper(HttpServletRequest request, IWorkingTimeCalendarProvider provider) {

//        schedule = (Schedule) request.getSession().getAttribute("schedule");
//        if (schedule == null) {
//            throw new IllegalStateException("Could not find attribute with name 'schedule' in session");
//        }

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            throw new IllegalStateException("Could not find attribute with name 'user' in session");
        } else {
            roster = user.getCurrentSpace().getRoster();
        }

        // Grab the provider for the schedule
//        provider = schedule.getWorkingTimeCalendarProvider();
        this.provider = provider;
    }

    /**
     * Specifies the type of calendar being created.
     * @param id the id of the calendar type
     * @throws IllegalArgumentException if the ID is invalid
     * @see CalendarType
     */
    public void setCalendarType(String id) {
        this.type = CalendarType.forID(id);
    }

    /**
     * Returns the current calendar type.
     * @return the type of calendar being created; or null if none has been set
     */
    public CalendarType getCalendarType() {
        return this.type;
    }

    /**
     * Indicates whether we're creating a base calendar.
     * @return true if the current type is a base calendar type;
     * false otherwise
     * @throws IllegalStateException if no type has been specified yet
     */
    private boolean isBaseCalendar() {
        if (this.type == null) {
            throw new IllegalStateException("No type has been specified");
        }

        return this.type.equals(CalendarType.BASE);
    }

    /**
     * Specifies the name of a base calendar.
     * @param name the name of the base calendar
     * @throws IllegalStateException if a non-base calendar is being created
     */
    public void setName(String name) {
        if (!isBaseCalendar()) {
            throw new IllegalStateException("Cannot specify a name for a non-base calendar");
        }
        this.name = name;
    }

    /**
     * Returns the name of the base calendar being created.
     * @return the name or null if none has been set
     */
    public String getName() {
        return this.name;
    }


    /**
     * Specifies the ID of the resource for which a resource calendar
     * is being created.
     * @param resourceID the ID of the resource
     * @throws IllegalStateException if a base calendar is being created
     */
    public void setResourceID(String resourceID) {
        if (isBaseCalendar()) {
            throw new IllegalStateException("Cannot specify a resource ID for a base calendar");
        }
        this.resourceID = resourceID;
    }

    /**
     * Specifies the ID of the parent calendar on which a resource calendar
     * is based.
     * @param parentCalendarID the ID of the parent calendar
     * @throws IllegalStateException if a base calendar is being created
     */
    public void setParentCalendarID(String parentCalendarID) {
        if (isBaseCalendar()) {
            throw new IllegalStateException("Cannot specify a parent calendar ID for a base calendar");
        }
        this.parentCalendarID = parentCalendarID;
    }

    /**
     * Returns the resource options of available resources who do
     * not yet have resource calendars.
     * @return a collection where each element is a <code>IHTMLOption</code>;
     * possibly empty
     */
    public Collection getAvailableResourceOptions() {

        Collection resources = new ArrayList();

        // Look for members of the roster without working time calendars
        for (Iterator it = roster.iterator(); it.hasNext();) {
            Person nextPerson = (Person) it.next();

            WorkingTimeCalendarDefinition resourceCal = this.provider.getForResourceID(nextPerson.getID());
            if (resourceCal == null) {
                // No resource calendar for this person
                // Add them to the list
                resources.add(nextPerson);
            }
        }

        return Collections.unmodifiableCollection(resources);
    }

    /**
     * Returns the base calendar options of base calendars that
     * can be selected when creating a resource calendar.
     * @return a collection where each element is a <code>IHTMLOption</code>
     * where the value is the ID of a base calendar and the display is
     * the name of the calendar; the schedule default calendar is selected
     * by default
     */
    public Collection getBaseCalendarOptions() {
        return WorkingTimeCalendarListHelper.getWorkingTimeCalendarOptions(this.provider.getBaseCalendars(), this.provider.getDefault().getID());
    }

    /**
     * Stores the new calendar created by this helper and returns its ID.
     * Updates the provider so that the calendar is available in the schedule.
     * @throws PersistenceException if there is a problem storing
     */
    public String store() throws PersistenceException {

        WorkingTimeCalendarDefinition calendarDef;

        if (this.type.equals(CalendarType.BASE)) {
            // create base calendar with default entries
            calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendarWithDefaults(this.name);

        } else {
            // create resource calendar
            calendarDef = WorkingTimeCalendarDefinition.makeResourceCalendar(this.parentCalendarID, this.resourceID);
        }

        // Delegate to the provider to create since it requires updating
        return this.provider.create(calendarDef);
    }

    /**
     * Provides the type of calendar that can be created.
     */
    public static class CalendarType {

        private static final Map map = new HashMap();

        public static final CalendarType BASE = new CalendarType("1");
        public static final CalendarType RESOURCE = new CalendarType("2");

        private static CalendarType forID(String id) {
            CalendarType type = (CalendarType) map.get(id);
            if (type == null) {
                throw new IllegalArgumentException("Invalid calendar type ID: " + id);
            }
            return type;
        }

        private final String id;

        private CalendarType(String id) {
            this.id = id;
            map.put(id, this);
        }

        public String getID() {
            return this.id;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CalendarType)) return false;

            final CalendarType calendarType = (CalendarType) o;

            if (!id.equals(calendarType.id)) return false;

            return true;
        }

        public int hashCode() {
            return id.hashCode();
        }
    }
}
