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

 package net.project.schedule;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.persistence.NotSupportedException;
import net.project.persistence.PersistenceException;

/**
 * Provides a working time calendar provider that is based on a sepcific
 * calendar definition.
 * <p>
 * This is intended for testing purposes only.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class TestWorkingTimeCalendarProvider implements IWorkingTimeCalendarProvider, Serializable {

    //
    // Static members
    //

    /**
     * Creates a new <code>CustomWorkingTimeCalendarProvider</code> that returns
     * the specified calendar definition as the default calendar.
     * @param calendarDef the calendar definition that is the default calendar
     */
    public static TestWorkingTimeCalendarProvider make(WorkingTimeCalendarDefinition calendarDef) {
        return new TestWorkingTimeCalendarProvider(calendarDef);
    }

    //
    // Instance members
    //

    private final String id;

    /** The default calendar definition. */
    private final WorkingTimeCalendarDefinition calendarDef;

    /** The calendar definitions for resources. */
    private final Map resourceCalendars = new HashMap();

    private final TimeZone defaultTimeZone;

    /**
     * Creates a working time calendar provider with the default
     * working time calendar definition as the default calendar
     * and a default time zone of <code>America/Los_Angeles</code>.
     */
    public TestWorkingTimeCalendarProvider() {
        this((String) null);
    }

    /**
     * Creates a working time calendar with the specified id, used for testing clones.
     * @param id the id of this calendar
     */
    TestWorkingTimeCalendarProvider(String id) {
        this(id, WorkingTimeCalendarDefinition.makeDefaultWorkingTimeCalendarDefinition());
    }

    /**
     * Creates a working time calendar provider with the specified
     * calendar definition as the default calendar and a default time zone of <code>America/Los_Angeles</code>.
     * @param calendarDef the calendar definition that is the default calendar
     */
    TestWorkingTimeCalendarProvider(WorkingTimeCalendarDefinition calendarDef) {
        this(null, calendarDef);
    }

    public TestWorkingTimeCalendarProvider(String id, WorkingTimeCalendarDefinition calendarDef) {
        this.id = id;
        this.calendarDef = calendarDef;
        this.defaultTimeZone = TimeZone.getTimeZone("America/Los_Angeles");
    }

    /**
     * Returns the ID of this test calendar provider, or null if none was specified
     * in the constructor.
     * @return the id
     */
    String getID() {
        return this.id;
    }

    /**
     * Adds a resource calendar definition for the specified resource ID.
     * @param resourceID the ID of the person for which to add the calendar definition
     * @param calendarDef the calendar definition to add
     */
    public void addResourceCalendarDefintion(String resourceID, WorkingTimeCalendarDefinition calendarDef) {
        if (resourceID == null || calendarDef == null) {
            throw new NullPointerException("resourceID and calendarDef are required");
        }
        this.resourceCalendars.put(resourceID, calendarDef);
    }

    /**
     * Indicates whether the specified calendarID is default.
     * @param calendarID the ID of the calendar
     * @return returns <code>true</code> always
     */
    public boolean isDefault(String calendarID) {
        return true;
    }

    /**
     * Returns the default calendar definition, which is the one
     * specified when this provider was created.
     * @return the default calendar definition
     */
    public WorkingTimeCalendarDefinition getDefault() {
        return this.calendarDef;
    }

    /**
     * Returns <code>null</code> always.
     * @param calendarID
     * @return null
     */
    public WorkingTimeCalendarDefinition get(String calendarID) {
        return null;
    }
    
    public WorkingTimeCalendarDefinition getForPlanID(String planID) {
        return null;
    }

    /**
     * Returns the calendar definition for the specified resource or
     * null if none was added for that resource.
     * @param resourcePersonID the person ID of the resource who's calendar definition to get
     * @return the calendar definition
     */
    public WorkingTimeCalendarDefinition getForResourceID(String resourcePersonID) {
        return (WorkingTimeCalendarDefinition) this.resourceCalendars.get(resourcePersonID);
    }

    public Collection getAll() {
        throw new IllegalStateException("method not supported");
    }

    public Collection getBaseCalendars() {
        throw new IllegalStateException("method not supported");
    }

    public void store(String calendarID) throws PersistenceException {
        throw new NotSupportedException("Store not supported");
    }

    public String create(WorkingTimeCalendarDefinition calendarDef) throws PersistenceException {
        throw new NotSupportedException("Create not supported");
    }

    public void remove(String calendarID) throws PersistenceException {
        throw new NotSupportedException("Remove not supported");
    }

    public void changeDefaultCalendar(String defaultCalendarID) throws PersistenceException {
        throw new NotSupportedException("changeDefaultCalendar not supported");
    }

    /**
     * Returns the default time zone.
     * @return the default time zone
     */
    public TimeZone getDefaultTimeZone() {
        return this.defaultTimeZone;
    }

    /**
     * Performs a shallow clone of this test calendar provider.
     * <p/>
     * WorkingTimeCalendarDefinitions are not cloned; this is only due to the fact that
     * such deep cloning has not yet been necessary.
     * @return the cloned instance
     */
    public Object clone() {
        TestWorkingTimeCalendarProvider clone = new TestWorkingTimeCalendarProvider(this.id, this.calendarDef);
        clone.resourceCalendars.clear();
        clone.resourceCalendars.putAll(this.resourceCalendars);
        return clone;
    }

}
