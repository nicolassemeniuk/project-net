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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import net.project.base.RecordStatus;
import net.project.calendar.PnCalendar;
import net.project.database.DBBean;
import net.project.database.ObjectManager;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.util.Conversion;
import net.project.util.DateUtils;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.xml.document.XMLDocument;

/**
 * Provides the entries in the working time calendar to use to calculate schedule
 * dates.
 * <p>
 * Supports a hierarchy of Working Time Definitions; this allows for calendars
 * to be based on other calendars. Day of week entries describe the default
 * value for each day of the week. A base calendar must have 7 day of week
 * entries for describing Sunday thru Saturday. A non-base calendar is always
 * based on a base calendar. It may specify zero to 7 day of week entries in
 * order to override the base calendar. A calendar can also specify a specific
 * date entry that alters the value for a date. <br>
 * <b>Note:</b> If a non-base calendar (i.e. a sub-calendar) specifies a day of
 * week entry, it will override any date entry in it's parent calendar. <br>
 * The mechanism for determining whether a day is a working day or non working
 * day is based on the following rules: <br>
 * Consider a particular date that we are interested in knowing whether it is
 * a working day or non working day:<br>
 * <li>If a date entry is present in the current definition that matches the date
 * in question, the result is based on that entry.
 * <li>Otherwise, if a day of week entry is present in the current definition
 * that matches the day of week of the date, the result is based on that entry.
 * <li>Otherwise, repeat the same check in the parent calendar.
 * </p>
 * @author Tim Morrow
 * @since Version 7.6.0
 */
public class WorkingTimeCalendarDefinition implements IXMLPersistence, Serializable {

	//
	// Static Members
	//

	/**
	 * Creates a definition with no name with default working time entries.
     * Saturday and Sunday are non working days, Monday thru Friday are
     * working days with default working times.
	 * @return the default calendar definition
	 */
	public static WorkingTimeCalendarDefinition makeDefaultWorkingTimeCalendarDefinition() {
		WorkingTimeCalendarDefinition calendarDefinition = new WorkingTimeCalendarDefinition();
		addDefaultEntries(calendarDefinition);
		return calendarDefinition;
	}

	/**
	 * Creates an empty base calendar definition.
     * @param name the name of the base calendar
	 * @return a base calendar
     * @throws NullPointerException if name is null
	 */
	public static WorkingTimeCalendarDefinition makeBaseCalendar(String name) {
		if (name == null) {
			throw new NullPointerException("name is required");
		}
		WorkingTimeCalendarDefinition calendarDef = new WorkingTimeCalendarDefinition();
		calendarDef.name = name;
		return calendarDef;
	}

	/**
	 * Creates a base calendar definition with default day of week entries.
     * @param name the name of the base calendar
	 * @return a base calendar with default day of week working times
	 */
	public static WorkingTimeCalendarDefinition makeBaseCalendarWithDefaults(String name) {
		WorkingTimeCalendarDefinition calendarDef = makeBaseCalendar(name);
		addDefaultEntries(calendarDef);
		return calendarDef;
	}

	/**
	 * Creates an empty resource calendar definition.
     * @param parentCalendarID the ID of the parent calendar
     * @param personID the ID of the person who is the owning resource
	 * @return the resource calendar definition
	 */
	public static WorkingTimeCalendarDefinition makeResourceCalendar(String parentCalendarID, String personID) {
		WorkingTimeCalendarDefinition calendarDef = new WorkingTimeCalendarDefinition();
		calendarDef.setBaseCalendar(false);
		calendarDef.setParentCalendarID(parentCalendarID);
		calendarDef.setResourcePersonID(personID);
		return calendarDef;
	}

	/**
     * Adds the default day of week entries to the specified calendar definition.
     * @param calendarDefinition the calendar definition to which to add the default entries
	 */
	private static void addDefaultEntries(WorkingTimeCalendarDefinition calendarDefinition) {
        calendarDefinition.addDayOfWeekEntry(WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SUNDAY));
        calendarDefinition.addDayOfWeekEntry(WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.MONDAY));
        calendarDefinition.addDayOfWeekEntry(WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.TUESDAY));
        calendarDefinition.addDayOfWeekEntry(WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.WEDNESDAY));
        calendarDefinition.addDayOfWeekEntry(WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.THURSDAY));
        calendarDefinition.addDayOfWeekEntry(WorkingTimeCalendarEntry.makeDefaultWorkingDayOfWeek(Calendar.FRIDAY));
        calendarDefinition.addDayOfWeekEntry(WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(Calendar.SATURDAY));
	}

	//
	// Instance members
	//

	/**
	 * The ID of this working time calendar definition.
	 */
	private String calendarID = null;

	/**
	 * Indicates whether this is calendar may not inherit from another calendar.
	 * If it is not a base calendar, then the parentCalendarID must be
	 * specified.
	 */
	private boolean isBaseCalendar = true;

	/** The name of this calendar (only valid if isBaseCalendar). */
	private String name = null;

	/** The ID of the parent calendar (only valid if !isBaseCalendar). */
	private String parentCalendarID = null;

	/** The person ID of the owning resource (only valid if !isBaseCalendar). */
	private String resourcePersonID = null;

	/** The display name of the owning resource (only valid if !isBaseCalendar). */
	private String resourceDisplayName = null;

	/**
	 * The record status of this loaded definition.
	 */
	private RecordStatus recordStatus = null;

	/**
     * Provides the entries for day of week working times (Sunday thru Saturday).
     * Each key is an Integer representing the day of the week.  Each value
     * is a DayOfWeekEntry.
	 */
	private final Map dayOfWeekEntries = new HashMap();

	/**
     * Provides the entries for dates.
     * Each key is a <code>DayOfYear</code>.  Each value is a DateEntry.  The same DateEntry value
     * may be in the map for different keys if the DateEntry spans multiple
     * dates.
	 */
	private final Map dateEntries = new HashMap();

	/**
     * The parent working time calendar definitions.
     * Present if this is not a base calendar.
	 */
	private WorkingTimeCalendarDefinition parentCalendarDefinition = null;

	/**
	 * The current plan ID, required for storing.
	 */
	private String planID = null;
	
	
	/**
	 * The date descripton, optional field. 
	 */
	private String dateDescription = null;

	/**
	 * @return the dateDescription
	 */
	public String getDateDescription() {
		return dateDescription;
	}

	/**
	 * @param dateDescription the dateDescription to set
	 */
	public void setDateDescription(String dateDescription) {
		this.dateDescription = dateDescription;
	}

	/**
	 * Creates an empty WorkingTimeCalendarDefinition.
	 */
	public WorkingTimeCalendarDefinition() {
		// Do nothing
	}

	/**
     * Creates an empty WorkingTimeCalendarDefintion with the specified
     * parent calendar definition.
     * @param parentCalendarDef the parent calendar
	 */
    public WorkingTimeCalendarDefinition(WorkingTimeCalendarDefinition parentCalendarDef) {
		this.isBaseCalendar = false;
		this.parentCalendarDefinition = parentCalendarDef;
	}

	/**
	 * Returns the ID of this calendar definition, if loaded or set.
     * @return the ID or null if this calendar definition has not been
     * loaded or stored
	 */
	public String getID() {
		return this.calendarID;
	}

	/**
     * Specifies the ID of this calendar definition, required before loading
     * or removing.
     * @param id the id
	 */
	public void setID(String id) {
		this.calendarID = id;
	}

	/**
	 * Specifies the display name of this calendar.
     * @param name the name
	 */
    public void setName(String name) {
		this.name = name;
	}

	/**
     * Returns the name of this calendar.
     * Only base calendars have an actual name; non-base calendars derive
     * their names from another source
     * @return the name of this calendar or null if this calendar is not
     * a base calendar
	 */
	private String getName() {
		return this.name;
	}

	/**
	 * Returns the display name of this calendar.
	 * <p>
     * The display name depends on whether this calendar is a base calendar.
     * If a base calendar, this returns the calendar name.  If a non-base
     * calendar, this returns the owning resource display name.
	 * </p>
	 * 
	 * @return the display name
	 */
	public String getDisplayName() {
		String displayName;

		if (isBaseCalendar()) {
			displayName = getName();
		} else {
			displayName = getResourceDisplayName();
		}

		return displayName;
	}

	/**
     * Specifies whether this is a base calendar.
     * A base calendar has no parent calendar definition.
     * A non-base calendar MUST have a parent calendar definition.
     * @param isBaseCalendar true if this is a base calendar; false if not
	 */
    public void setBaseCalendar(boolean isBaseCalendar) {
		this.isBaseCalendar = isBaseCalendar;
	}

	/**
     * Indicates whether this calendar definition is a base calendar.
     * A base calendar has no parent calendar definition.
     * A non-base calendar MUST have a parent calendar definition.
	 * @return true if it is a base calendar; false if it is not a base calendar
	 */
	public boolean isBaseCalendar() {
		return this.isBaseCalendar;
	}

	/**
	 * Specifies the parent calendar ID of this non-base calendar.
     * @param parentCalendarID the parent calendar ID
	 */
    public void setParentCalendarID(String parentCalendarID) {
		this.parentCalendarID = parentCalendarID;
	}

	/**
	 * Changed method access from default to public - for Export project functionality
	 * @return
	 */
	public String getParentCalendarID() {
		return this.parentCalendarID;
	}

	/**
	 * Specifies the person ID of the rsource which owns this calendar.
     * @param resourcePersonID the person ID of the owning resource
	 */
    public void setResourcePersonID(String resourcePersonID) {
		this.resourcePersonID = resourcePersonID;
	}

	/**
	 * Returns the personID of the resource which owns this calendar.
	 * @return the ID of the owning resource
	 */
	public String getResourcePersonID() {
		return this.resourcePersonID;
	}

	/**
	 * Specifies the display name of the resource which owns this calendar.
     * @param displayName the display name of the owning resource
	 */
    public void setResourceDisplayName(String displayName) {
		this.resourceDisplayName = displayName;
	}

	/**
	 * Returns the display name of the owning resource.
	 * @return the display name or null if this is a base calendar
	 */
	private String getResourceDisplayName() {
		return this.resourceDisplayName;
	}

	/**
	 * Specifies the current record status.
     * @param recordStatus the record status
	 */
    public void setRecordStatus(RecordStatus recordStatus) {
		this.recordStatus = recordStatus;
	}

	/**
	 * The planID that this definition belongs to.
	 * 
	 * @return a <code>String</code> containing the plan ID that the working
	 *         time calendar definition was created in.
	 */
	public String getPlanID() {
		return planID;
	}

	/**
	 * Specifies the current planID, required for certain operations.
     * @param planID the ID of the plan
	 */
	public void setPlanID(String planID) {
		this.planID = planID;
	}

	/**
     * Sets this non-base calendar's parent calendar definition given
     * a map containing the parent calendar definition.
     * @param calendarDefinitions a map where each key is a <code>String</code> calendarID
     * and each value is a <code>WorkingTimeCalendarDefinition</code> with
     * that ID.
     * @throws IllegalStateException if this is a base calendar or if the
     * parent calendar definition could not be found in the map
	 */
	public void setParentCalendarDefinition(Map calendarDefinitions) {

        WorkingTimeCalendarDefinition calendarDef = (WorkingTimeCalendarDefinition) calendarDefinitions.get(this.parentCalendarID);
		if (calendarDef == null) {
            throw new IllegalStateException("Working time calendar definition with ID '" + getID() + "' could not find parent calendar with ID '" + this.parentCalendarID + "'");
		}

		updateParentCalendar(calendarDef);
	}

	/**
     * Update's this non-base calendar's parent calendar definition to the specified
     * calendar definition.
     * @param parentCalendarDef the parent calendar definition
     * @throws NullPointerException if parentCalendarDef is null
     * @throws IllegalStateException if this is a base calendar
     * @throws IllegalArgumentException if the specified parent calendar definition is
     * not a base calendar
	 */
	public void updateParentCalendar(WorkingTimeCalendarDefinition parentCalendarDef) {

		if (parentCalendarDef == null) {
			throw new NullPointerException("parentCalendarDef is required");
		}

		if (isBaseCalendar()) {
            throw new IllegalStateException("Cannot specify a parent calendar for a base calendar");
		}

		if (!parentCalendarDef.isBaseCalendar()) {
            throw new IllegalArgumentException("A parent calendar definition must be a base calendar");
		}

		this.parentCalendarDefinition = parentCalendarDef;
		setParentCalendarID(parentCalendarDef.getID());
	}

	/**
     * Replaces the entries in this calendar definition with the specified entries.
     * @param entries the entries to add
	 */
	public void setEntries(Collection entries) {
		this.dayOfWeekEntries.clear();
		this.dateEntries.clear();
		for (Iterator it = entries.iterator(); it.hasNext();) {
			WorkingTimeCalendarEntry nextEntry = (WorkingTimeCalendarEntry) it.next();
			addEntry(nextEntry);
		}
	}

	/**
	 * Adds the specified entry to this calendar definition.
     * @param entry the entry to add
	 */
	public void addEntry(WorkingTimeCalendarEntry entry) {

		if (entry instanceof DayOfWeekEntry) {
			addDayOfWeekEntry((DayOfWeekEntry) entry);
		} else {
			addDateEntry((DateEntry) entry);
		}

	}

	/**
     * Adds a day of week entry to the map of day number to entry.
     * Also adds the number of working hours in that day to the running total.
     * @param entry the entry to add
	 */
    public void addDayOfWeekEntry(DayOfWeekEntry entry) {
		dayOfWeekEntries.put(new Integer(entry.getDayNumber()), entry);
	}

	/**
	 * Adds a date entry to this calendar definition.
     * @param entry the entry to add
     * @throws IllegalStateException if the specified entry overlaps
     * with an existing entry; at this point the date entries are in an
     * undefined state.
	 */
    public void addDateEntry(DateEntry entry) {

		// A DateEntry may span multiple dates if its start and end dates
		// are different
		// We add the same entry multiple times into the map, once for
		// each day that the entry spans
		for (Iterator it = entry.getSpanDayOfYear().iterator(); it.hasNext();) {
			DayOfYear nextDayOfYear = (DayOfYear) it.next();

			if (dateEntries.containsKey(nextDayOfYear)) {
                throw new IllegalStateException("Attempted to add overlapping date entry: " + entry);
			}
			dateEntries.put(nextDayOfYear, entry);
		}

	}

	/**
     * Returns the day of week entry for the specified day number.
     * This is the entry from this definition, or if none is specified,
     * the entry from the parent definition.
     * @param dayNumber the day to get the day of week entry for
     * in the range {@link Calendar#SUNDAY} .. {@link Calendar#SATURDAY}
	 * @return the day of week entry for the specified day number
     * @throws IllegalStateException if no day of week entry could be found
     * at any level
     * @throws IllegalArgumentException if the day number is not in the correct range
     * @throws IllegalStateException if no day of week entry is found in this
     * calendar definition and it has no parent calendar definition
	 */
    public DayOfWeekEntry getDayOfWeek(int dayNumber) {

		DayOfWeekEntry.assertValidDayNumber(dayNumber);
		DayOfWeekEntry day = getDayOfWeekNoHierarchy(dayNumber);

		if (day == null) {
			// No day of week entry in this calendar
			if (this.parentCalendarDefinition == null) {
                throw new IllegalStateException("No day of week entry found and no parent calendar definition");
			}
			day = this.parentCalendarDefinition.getDayOfWeek(dayNumber);
		}

		return day;
	}

	/**
     * Returns the day of week entry for the specified day number.
     * This is the entry from this definition.  No hierarchical lookup is
     * performed.
     * @param dayNumber the day to get the day of week entry for
     * in the range {@link Calendar#SUNDAY} .. {@link Calendar#SATURDAY}
     * @return the day of week entry for the specified day number or null
     * if this is a non-base calendar and there is no day of week entry
     * @throws IllegalStateException if this is a base calendar and no day of
     * week entry could be found
     * @throws IllegalArgumentException if the day number is not in the correct range
	 */
	private DayOfWeekEntry getDayOfWeekNoHierarchy(int dayNumber) {

		DayOfWeekEntry.assertValidDayNumber(dayNumber);
		DayOfWeekEntry day = (DayOfWeekEntry) this.dayOfWeekEntries.get(new Integer(dayNumber));

		if (day == null && isBaseCalendar()) {
            throw new IllegalStateException("Illegal day of week number or missing day of week in base calendar: " + dayNumber);
		}

		return day;
	}

	/**
     * Returns the day of week entry for the specified day number
     * from the parent calendar.
     * @param dayNumber the day to get the day of week entry for
     * in the range {@link Calendar#SUNDAY} .. {@link Calendar#SATURDAY}
	 * @return the day of week entry for the specified day number
     * @throws IllegalStateException if this is a base calendar and thus has
     * no parent calendar
     * @throws IllegalArgumentException if the day number is not in the correct range
	 */
    public DayOfWeekEntry getParentDayOfWeek(int dayNumber) {

		if (isBaseCalendar()) {
            throw new IllegalStateException("A base calendar has no parent calendar");
		}

		DayOfWeekEntry.assertValidDayNumber(dayNumber);
		return this.parentCalendarDefinition.getDayOfWeek(dayNumber);
	}

	/**
     * Returns a date entry for the specified calendar's date.
     * The time component is ignored.
     * Only looks in this working time calendar definition; does not look
     * in parent.
     * @param cal the calendar containing the date to get the entry for
	 * @return the date entry or null if there is none for that date
	 */
    public DateEntry getDate(Calendar cal) {
		return (DateEntry) this.dateEntries.get(new DayOfYear(cal));
	}

	/**
	 * Returns the entry for the specified entryID
     * @param entryID the ID of the entry to get
	 * @return the entry for that ID
     * @throws NullPointerException if the entryID is null
     * @throws IllegalStateException if no entry can be found for the specified ID
	 */
    public WorkingTimeCalendarEntry getEntry(String entryID) {

		if (entryID == null) {
			throw new NullPointerException("entryID is required");
		}

		// Look for the entry in both day of weeks and dates
		WorkingTimeCalendarEntry foundEntry;
		foundEntry = findEntryForID(entryID, getDayOfWeekEntries().iterator());
		if (foundEntry == null) {
			foundEntry = findEntryForID(entryID, getDateEntries().iterator());
		}
		if (foundEntry == null) {
			throw new IllegalStateException("No entry found with entryID: " + entryID);
		}

		return foundEntry;
	}

	private WorkingTimeCalendarEntry findEntryForID(String entryID, Iterator it) {

		WorkingTimeCalendarEntry foundEntry = null;

		while (it.hasNext()) {
			WorkingTimeCalendarEntry nextentry = (WorkingTimeCalendarEntry) it.next();
			if (nextentry.getEntryID() != null && nextentry.getEntryID().equals(entryID)) {
				foundEntry = nextentry;
				break;
			}
		}

		return foundEntry;
	}

	/**
	 * Returns the entry for the specified calendar's current date.
	 * <p>
	 * Algorithm as follows:<br>
	 * <li>If a date entry is found, that is returned.
	 * <li>Otherwise, if there is a day of week entry for the same day of week,
	 * that is returned.
	 * <li>Otherwise, if it is a non-base calendar, the entry from the parent
	 * calendar is returned. If this wasn't a base calendar an excpetion is
	 * thrown
	 * </p>
     * @param cal the calendar containing the date to get the entry for
	 * @return the date entry or day of week entry if there is no date entry
     * @throws IllegalStateException if no date or day of week entry is found
	 */
	private WorkingTimeCalendarEntry getEntry(Calendar cal) {

		WorkingTimeCalendarEntry entry = getDate(cal);

		if (entry == null) {
			// There is no date entry
			// We use the day of week entry
			entry = getDayOfWeekNoHierarchy(cal.get(Calendar.DAY_OF_WEEK));

			if (entry == null) {
				// No date entry or no day of week entry

				if (isBaseCalendar()) {
					// A base calendar must have all day of week entries
                    throw new IllegalStateException("Could not find date entry or day of week entry in base calendar for date: " + cal.getTime());

				} else {
					// Non-base calendars look in the parent definition
					entry = this.parentCalendarDefinition.getEntry(cal);

				}

			}

		}

		return entry;
	}

	/**
	 * Changed method access from default to public - for Export project functionality
	 * Returns an unmodifiable collection of the day of week entries in this
	 * calendar definition.
	 * @return a collection where each element is a <code>DayOfWeekEntry</code>
	 */
	public Collection getDayOfWeekEntries() {
		return Collections.unmodifiableCollection(this.dayOfWeekEntries.values());
	}

	/**
	 * Returns an unmodifiable collection of the day of week entries
	 * hierarchically resolved from this and the parent calendar definition/
	 * @return a collection containing seven <code>DayOfWeekEntry</code>s
	 */
	private Collection getDayOfWeekEntriesHierarchical() {

		Collection entries = new ArrayList();

		for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
			entries.add(getDayOfWeek(i));
		}

		return entries;
	}

	/**
	 * Returns an unmodifiable collection of the unique date entries in this
	 * calendar definition.
	 * @return the date entries
	 */
    public Collection getDateEntries() {
		// Create a set from the date entries to eliminate duplicates
		// and return it unmodifiable
		return Collections.unmodifiableCollection(new HashSet(this.dateEntries.values()));
	}

	/**
     * Changes the day of week for the specified day number to inherit
     * from this non-base calendar's parent calendar.
	 * <p>
	 * If it is already inheriting, this method silently succeeds.
	 * </p>
     * @param dayNumber the day number of the day of week to remove
     * @throws IllegalArgumentException if dayNumber is not in the correct range
     * @throws IllegalStateException if this is a base calendar; base calendars
     * cannot inherit day of week entries
	 */
    public void updateDayOfWeekInherit(int dayNumber) {

		if (isBaseCalendar()) {
            throw new IllegalStateException("Base calendars cannot inherit a day of week entry");
		}

		// Now remove it
		removeDayOfWeek(dayNumber);
	}

	/**
	 * Updates the day of week for the specified day number to be a working day,
	 * including the specified working times.
     * @param dayNumber the day number of the day of week to change
     * @param workingTimes the working times of day; each element is a <code>WorkingTime</code>
     * @throws IllegalArgumentException if dayNumber is not in the correct range
     * @throws NullPointerException if workingTimes is null
	 */
    public void updateDayOfWeekWorkingDay(int dayNumber, Collection workingTimes) {

		if (workingTimes == null) {
			throw new NullPointerException("workingTimes is required");
		}
		removeDayOfWeek(dayNumber);
		DayOfWeekEntry dayOfWeek = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(dayNumber);
		dayOfWeek.setWorkingTimes(workingTimes);
		addDayOfWeekEntry(dayOfWeek);
	}

	/**
     * Updates the day of week for the specified day number to be a non working day.
     * @param dayNumber the day number of the day of week to change
     * @throws IllegalArgumentException if dayNumber is not in the correct range
	 */
    public void updateDayOfWeekNonWorkingDay(int dayNumber) {
		removeDayOfWeek(dayNumber);
		addDayOfWeekEntry(WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(dayNumber));
	}

	/**
     * Removes the day of week for the specified day number.
     * Silently succeeds if no day of week entry is present for that day number.
     * @param dayNumber the day number to remove
	 */
	private void removeDayOfWeek(int dayNumber) {
		DayOfWeekEntry.assertValidDayNumber(dayNumber);
		this.dayOfWeekEntries.remove(new Integer(dayNumber));
	}

	/**
     * Indicates whether the day of week for the specified day
     * number is inherited from the parent calendar.
     * @param dayNumber the day number of the day of week to check
     * @return true if it is inherited from the parent calendar;
     * false if specified in this calendar
     * @throws IllegalStateException if this is a base calendar (which
     * cannot inherit day of week entries)
     * @throws IllegalArgumentException if the day number is not in the correct range
	 */
    public boolean isInheritedDayOfWeek(int dayNumber) {

		if (isBaseCalendar()) {
            throw new IllegalStateException("Base calendars do not inherit day of week entries");
		}

		DayOfWeekEntry.assertValidDayNumber(dayNumber);
		return (getDayOfWeekNoHierarchy(dayNumber) == null);
	}

	/**
     * Indicates whether the specified calendar contains a working day.
     * This is based on both day of week and date entries.
     * @param cal the calendar containing the date to check
	 * @return true if it is a working day; false if it is a non-working day
	 */
    public boolean isWorkingDay(Calendar cal) {
		return getEntry(cal).isWorkingDay();
	}

	/**
	 * This method indicates whether or not the given calendar day of week is a
     * working day normally.  This method does not take into account if there
     * are any exceptions for that day.
	 * 
     * @param dayOfWeek a <code>int</code> code for day of week derived from
	 *            {@link java.util.Calendar.DAY_OF_WEEK}.
     * @return a <code>boolean</code> indicating if this particular day of week
     * is normally a working day.
	 */
    public boolean isStandardWorkingDay(int dayOfWeek) {
		return getDayOfWeekNoHierarchy(dayOfWeek).isWorkingDay();
	}

	/**
     * Indicates whether the specified calendar contains a working time.
     * This is based on both day of week and date entries.
     * @param cal the calendar containing the date to check
	 * @return true if the specified date is a working time; false otherwise
	 */
    public boolean isWorkingTime(Calendar cal) {
		return getEntry(cal).isWorkingTime(new Time(cal));
	}

	/**
	 * Indicates whether the specified calendar's current time is a working time
     * on the calendar's date or day of week.
     * If the time is at the very start of a working time boundary, it is not
     * considered to be in working time for an end time since no work can be
     * done in that time.
     * Based on date entries and day of week entries.
	 * <p>
	 * For example, 8:00 AM is normally a working time, but not when considering
     * an end time of a task.  8:01 AM is a working time for an end time (since
     * 1 minute of work can get done).  12:00 PM is not a working time.
     * @param cal the calendar whose date and time to use
	 * @return true if the date is a working day and the time is a working time
     * and is not in the first minute of working time;
     * false if the date is a non-working day or the time is a non-working time
     * on a working day
	 */
    public boolean isWorkingTimeForEnd(Calendar cal) {
		return getEntry(cal).isWorkingTimeForEnd(new Time(cal));
	}

	/**
	 * Returns the number of working hours in a week based on day of week
     * entries.
     * The number is the sum of the working hours in each resolved day of the
     * week; that is, if this definition does not specify any or all days of
     * week, the parent day of week is used.
	 * @return the number of working hours in a week
	 */
    public SimpleTimeQuantity getWorkingHoursInWeek() {

		SimpleTimeQuantity totalHours = new SimpleTimeQuantity(0, 0);
		for (int dayNumber = Calendar.SUNDAY; dayNumber <= Calendar.SATURDAY; dayNumber++) {
			DayOfWeekEntry nextEntry = getDayOfWeek(dayNumber);
			if (nextEntry.isWorkingDay()) {
				totalHours = totalHours.add(nextEntry.getWorkingHours());
			}
		}

		return totalHours;
	}

    public SimpleTimeQuantity getWorkingHoursInDay(Calendar cal) {
		WorkingTimeCalendarEntry entry = getEntry(cal);
		return entry.getWorkingHours();
	}

	/**
	 * Updates the calendar with the date of the next working time on or after
	 * the date and time in the calendar.
     * @param cal the date and time to use as the starting point for finding
     * the next earliest working time; if the date in the calendar is already
     * a working time, the calendar is unchanged
     * @throws NoWorkingTimeException if no working time was found; calendar is not updated
	 */
    public void updateWithStartOfNextWorkingTime(Calendar cal) throws NoWorkingTimeException {
		updateWithWorkingTime(cal, true);
	}

	/**
     * Updates the calendar with the date of the previous working time on or before
     * the date and time in the calendar.
     * @param cal the date and time to use as the starting point for finding
     * the previous latest working time end
     * @throws NoWorkingTimeException if no working time was found; calendar is not updated
	 */
    public void updateWithEndOfPreviousWorkingTime(Calendar cal) throws NoWorkingTimeException {
		updateWithWorkingTime(cal, false);
	}

	/**
	 * Updates the calendar with the date of the next or previous working time.
     * @param cal the calendar to update
     * @param isForward true means the next working time is found; false means
     * the previous working time is found
     * @throws NoWorkingTimeException if no working time was found; calendar is not updated
	 */
	private void updateWithWorkingTime(Calendar cal, boolean isForward) throws NoWorkingTimeException {

		Time currentTime = new Time(cal);

		// Initialize a set containing 7 elements; one for each of the days
		// of the week; we'll stop processing if we discover that even after
		// checking all day of week entries, no next working time was found
		Set remainingDaysOfWeek = new HashSet(7);
		for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
			remainingDaysOfWeek.add(new Integer(i));
		}

		boolean isWorkingTimeFound = false;
		boolean isFirstDay = true;

		// Iterate over calendar starting from current date, looking for
		// the next available day that has a working time
		// We stop when we find a working time or we exhaust all the
		// day of week entries
		while (!isWorkingTimeFound && remainingDaysOfWeek.size() > 0) {

			WorkingTimeCalendarEntry nextEntry;

			if (isFirstDay) {
				// We're on the first day; We'd like to check to see
				// if there is still working time today
				nextEntry = getEntry(cal);

				if (isForward) {

					if (isWorkingTime(cal)) {
						// If it is already working time then we're done
						isWorkingTimeFound = true;

					} else {
						// Not working time; check to see if there is more today

						if (nextEntry.hasNextWorkingTime(currentTime)) {
							// There is another working time today
							nextEntry.updateWithStartOfNextWorkingTime(cal, currentTime);
							isWorkingTimeFound = true;
						}

					}

				} else {

					if (nextEntry.hasPreviousWorkingTime(currentTime)) {
						// There is a previous working time today
						nextEntry.updateWithEndOfPreviousWorkingTime(cal, currentTime);
						isWorkingTimeFound = true;
					}
				}

			} else {
				// Second or subsequent day

				if (isForward) {
					// We add a day then look for the first working time
					// Which is the same as finding the next working time
					// on or after midnight
					cal.add(Calendar.DAY_OF_MONTH, 1);
					nextEntry = getEntry(cal);

					Time boundaryTime = new Time(0, 0);
					if (nextEntry.hasNextWorkingTime(boundaryTime)) {
						nextEntry.updateWithStartOfNextWorkingTime(cal, boundaryTime);
						isWorkingTimeFound = true;
					}

				} else {
					// Subtract a day then look for the last working time
					// which is the same as finiding the previous working
					// time before 11:59 PM
					cal.add(Calendar.DAY_OF_MONTH, -1);
					nextEntry = getEntry(cal);

					Time boundaryTime = new Time(24, 0);

					if (nextEntry.hasPreviousWorkingTime(boundaryTime)) {
						nextEntry.updateWithEndOfPreviousWorkingTime(cal, boundaryTime);
						isWorkingTimeFound = true;
					}
				}

			}

			// If we just processed a DayOfWeekEntry, remove that from the
			// pool. This allows us to know when we've tried all
			// DayOfWeekEntrys;
			// We don't remove the first day since the time may have been beyond
			// the last working time on that day; we'll want to try the
			// first day of week one more time
			if (isFirstDay) {
				// We're done processing the first day
				isFirstDay = false;

			} else {
				if (nextEntry instanceof DayOfWeekEntry) {
					// Remove from set of remaining days of week
					remainingDaysOfWeek.remove(new Integer(((DayOfWeekEntry) nextEntry).getDayNumber()));
				}
			}

		}

		if (!isWorkingTimeFound) {
			// Big problem; we cycled through all days of week entries and
			// there were no working times;
            // We'll perform a final check to see if there are any future date entries
			// that have working time

            throw new NoWorkingTimeException("All day of week entries in working time calendar are non working days.");
		}

	}

	/**
     * Updates the specified calendar to set the time to the end boundary
     * of the latest working time block on the day of the date.
	 * <p>
     * <b>Note:</b> The calendar may actually roll to the next day if the
     * last working time block ends at midnight on the specified day.
     * In that case the calendar will be set to midnight on the next day.
     * </p>
     * @param cal the calendar whose time component to set to the end of
     * working time based on the working times on the calendar's current day
     * @throws IllegalStateException if there is no first working time, for
     * example the day is not a working day
	 */
    public void updateWithEndOfLastWorkingTimeForDay(Calendar cal) {
		getEntry(cal).updateWithEndOfLastWorkingTime(cal);
	}

	/**
     * Updates the specified calendar to set the time to the start boundary
     * of the earlierst working time block on the day of the date.
     * @param cal the calendar whose time component to set to the start of
	 *            working time on the day
     * @throws IllegalStateException if there is no first working time, for
     * example the day is not a working day
	 */
    public void updateWithStartOfFirstWorkingTimeForDay(Calendar cal) {
		getEntry(cal).updateWithStartOfFirstWorkingTime(cal);
	}

	/**
     * Figures out how many working hours are left in the day from the date
     * in the specified calendar, subtracts them from the hours and updates the
	 * calendar to a date by which those hours are consumed.
	 * <p>
     * If the number of working hours left in the day are less than or equal
     * to the number of hours left to allocate, then the date is updated to
     * reflect the correct point in time during the day at which the remaining
     * hours are consumed. <br>
	 * <b>Note:</b> The calendar may be set to the next day at midnight if the
	 * the working time ended at midnight and the working hours left in the day
	 * was equal to the number of hours left to allocate.
	 * </p>
     * @param endDateCal the current date which is updated to be the end date
     * if all remaining hours are consumed or updated to be the next working time
     * if there are still hours to allocate
     * @param hours the hours to allocate
     * @throws NoWorkingTimeException it was not possible to update the
     * calendar due to no working day of week entries; allocation will
     * still have been made
	 */
    public void allocateLaterHoursInDay(Calendar endDateCal, Allocation hours) throws NoWorkingTimeException {

		allocateHoursInDay(endDateCal, hours, true);
	}

	/**
	 * Figures out how many working hours are in the day earlier than the date
	 * in the specified calendar, subtracts them from the hours and updates the
     * date to be the end of the previous working time.
     * If the number of working hours earlier in the day are less than or equal
     * to the number of hours left to allocate, then the date is updated to
     * reflect the correct point in time earlier in the day at which the remaining
     * hours are consumed
     * @param endDateCal the current date which is updated to be the end date
     * if all remaining hours are consumed or updated to be the previous working time end
     * if there are still hours to allocate
     * @param hours the hours to allocate
     * @throws NoWorkingTimeException if no working time was found; calendar is not updated
	 */
    public void allocateEarlierHoursInDay(Calendar endDateCal, Allocation hours) throws NoWorkingTimeException {

		allocateHoursInDay(endDateCal, hours, false);
	}

	/**
	 * Deducts from the allocated hours the number of hours remaining or number
	 * of hours elapsed in the day specified by the calendar.
	 * <p>
	 * Also updates the calendar to advance or retard the time by the number of
	 * hours deducted. Typically only the time portion of the calendar is
	 * affected; however, when the remaining hours in the day is exactly equal
     * to the hours to allocate and the day's working time ends at midnight,
     * the calendar will be set to midnight on the next day.
     * For example, if the day has a 24-hour working time (0:00 - 24:00) and
     * there are 24 hours to allocate, the date will midnight on the next
     * day.
	 * </p>
     * @param endDateCal the calendar set to a date and time; it is updated
     * @param hours the remaining hours to allocate; this is updated if the
	 *            calendar time is changed
     * @param isLater true means later hours in day will be allocated - the
     * calendar time will be advanced; false means earlier hours in day will be
     * allocated - the calendar time will be retarded
     * @throws IllegalArgumentException if the actual hours remaining returned by <code>hours</code>
	 *             is zero; this method shouldn't be called in that case
     * @throws NoWorkingTimeException if some problem occurs while trying
     * to locate a start or end of working time; note allocation will still
     * have been made, endDateCal will not have been advanced
	 */
	private void allocateHoursInDay(Calendar endDateCal, Allocation hours, boolean isLater) throws NoWorkingTimeException {

		// Get the number of actual hours required to complete
		SimpleTimeQuantity actualHoursRemaining = hours.getActualTimeRemaining();
		if (actualHoursRemaining.isZero()) {
			// This is partly in response to BFD-2009 whereby 0.003 remaining
            // work hours attempted to be allocated; that is less than 30 seconds,
            // resulting in zero hours.  If the date calendar is a non-working day,
            // then it attempts to allocate zero hours on a day with zero remaining
			// hours
            throw new IllegalArgumentException("Attempted to allocate zero remaining hours");
		}

		// The time before/after which we want working time
		Time time;

		// The entry providing working times for the date
		WorkingTimeCalendarEntry workingTimeEntry;

		// The calendar set to the actual date for which we want
		// the calendar entry; we use this calendar for all
		// computations
		Calendar actualDateCal = (Calendar) endDateCal.clone();

		// Determine which actual date we want an entry for and the correct Time
		// Special handling is required for backwards calculations when the
        // calendar is set to midnight; this really means the end of the _previous_ day
		time = new Time(endDateCal);

		if (!isLater && time.equals(new Time(0, 0))) {
			// We roll the date back 1 day and convert the Time to 24:00
			time = new Time(24, 0);
			actualDateCal.add(Calendar.DAY_OF_MONTH, -1);
		}

		// Get the entry for the correct date
		workingTimeEntry = getEntry(actualDateCal);

		SimpleTimeQuantity remainingHoursInDay;

		if (isLater) {
			// Calculate the number of hours left in the current day
			remainingHoursInDay = workingTimeEntry.getRemainingWorkingHours(time);

		} else {
			// Calculate the number of hours earlier in the day
			remainingHoursInDay = workingTimeEntry.getEarlierWorkingHours(time);

		}

		if (remainingHoursInDay.compareTo(actualHoursRemaining) >= 0) {
			// All remaining hours can be allocated today

			if (isLater) {
				// Update the end date to a time in the day where all hours are
				// consumed
				workingTimeEntry.advanceHours(actualDateCal, actualHoursRemaining);

			} else {
				// Update the end date to a time in the day where all hours are
				// consumed (moving the end date earlier)

				// Note: We use the original date specified since if it was
                // midnight this method will count back to the previous (correct) day
				actualDateCal.setTime(endDateCal.getTime());
				workingTimeEntry.retardHours(actualDateCal, actualHoursRemaining);

			}

			// Zero out the remaining hours since we allocated them all
			hours.zeroHoursRemaining();

		} else {
			// The day has fewer hours than needed to complete the task
			// Consume the hours
			hours.subtractActualHoursRemaining(remainingHoursInDay.toHour());

			if (isLater) {
				// We'll move the date to first thing tomorrow or the
				// next working time start if tomorrow is not a working day
				actualDateCal.add(Calendar.DAY_OF_MONTH, 1);
				if (isWorkingDay(actualDateCal)) {
					// Following day is a working day
					// We set the calendar to the start of the day
					workingTimeEntry = getEntry(actualDateCal);
					workingTimeEntry.updateWithStartOfFirstWorkingTime(actualDateCal);

				} else {
					// Following day is not a working day
					// Find the next working time start
					updateWithStartOfNextWorkingTime(actualDateCal);
				}

			} else {
				// We'll move the date to last thing yesterday or the
				// previous working time end if yesterday was not a working day

				// Note: We subtract from the date that gave us the entry
				// to be sure to get the previous entry
				actualDateCal.add(Calendar.DAY_OF_MONTH, -1);
				if (isWorkingDay(actualDateCal)) {
					// Previous day is a working day
					// We set the calendar to the end of the day
					workingTimeEntry = getEntry(actualDateCal);
					workingTimeEntry.updateWithEndOfLastWorkingTime(actualDateCal);

				} else {
					// Previous day is not a working day
					// Find the previous working time end
					updateWithEndOfPreviousWorkingTime(actualDateCal);

				}

			}

		}

		// Now update the passed-in date wioth the computed date
		endDateCal.setTime(actualDateCal.getTime());

	}

	/**
     * Calculates the number of hours between the specified dates that are
     * in exception to the normal number of hours in a week using a callback
	 * mechanism to check to see whether the date should be ignored or not.
	 * <p>
	 * Any date entry that says it is a working day but the day of week is not,
	 * is considered to be an exception. Similarly, any date that is a non
	 * working day but its day of week is a working day is an exception.
	 * </p>
	 * <p>
     * There is special handling for any exceptions occurring on the start
     * and end dates:  we only count the number of hours remaining on the
     * start date or the number of hours elapsed on the end date.
	 * </p>
	 * <p>
	 * For example, given a start date of Monday 16th June @ 8:00 AM and an end
     * date of Monday 23rd June @ 8:00 AM there are normally 40 hours.
     * However, if there is a date entry that indicates Tuesday 17th June is a
     * non working day, there will be -8 exceptions hours (normal 8 hours on a
	 * Tuesday are lost).
	 * </p>
     * @param startDate the start date and time value
     * @param endDate the end date and time value
     * @param timeZone the time zone required to figure out what day of the
     * week a particular date is on
     * @return the number of exceptional hours;
     * a negative number means fewer hours were available to work;
     * a positive number means more hours were available to work
	 */
    public SimpleTimeQuantity calculateExceptionHours(Date startDate, Date endDate, TimeZone timeZone) {
		// Calculate the exceptions where no dates are overridden; every date
		// this definition will be used.
		return calculateNonOverriddenExceptionHours(startDate, endDate, timeZone, EMPTY_OVERRIDE_CHECKER);
	}

	/**
     * Calculates the number of hours between the specified dates that are
     * in exception to the normal number of hours in a week using a callback
	 * mechanism to check to see whether the date should be ignored or not.
	 * <p>
	 * See the comment for {@link #calculateExceptionHours} for details. <br>
	 * <b>Note:</b> The time portion of the dates is significant; For those
	 * exceptional dates ocurring on the start date only the exceptional hours
	 * following the start time are counted. For those exceptional dates
	 * occurring on the end date only the exceptional hours prior to the end
	 * time are counted. For example,if end time is set to a midnight value,
     * then the date on which it falls is essentially ignored
     * (since there is no time prior to midnight).
	 * </p>
     * @param startTime the start date and time value
     * @param endTime the end date and time value
     * @param timeZone the time zone required to figure out what day of the
     * week a particular date is on
     * @param overrideChecker the instance used to determine whether a date
     * entry in this definition is overridden; parent working time calendar
	 *            definitions are overridden by their children
	 * @return the number of exceptional hours; a negative number means fewer
     * hours were available to work; a positive number means more hours were
     * available to work
	 */
	private SimpleTimeQuantity calculateNonOverriddenExceptionHours(Date startTime, Date endTime, TimeZone timeZone, IEntryOverrideChecker overrideChecker) {

		// Determine the earliest and latest date
		// This is necessary for simplified date range comparisons
		Date earliestTime;
		Date latestTime;

		if (startTime.before(endTime)) {
			earliestTime = startTime;
			latestTime = endTime;
		} else {
			earliestTime = endTime;
			latestTime = startTime;
		}

		// Now calcualte date-only part and time-only part
		Calendar cal = new GregorianCalendar(timeZone);
		cal.setTime(earliestTime);
		Time earliestTimeTimePart = new Time(cal);
		DateUtils.zeroTime(cal);
		DayOfYear earliestDayOfYear = new DayOfYear(cal);

		cal.setTime(latestTime);
		Time latestTimeTimePart = new Time(cal);
		DateUtils.zeroTime(cal);
		DayOfYear latestDayOfYear = new DayOfYear(cal);

		SimpleTimeQuantity exceptionHourTotal = new SimpleTimeQuantity(0, 0);

		// Iterate over every exceptional date looking for those values
		// between start date and end date inclusive
		// Modifies exceptionHourTotal
		// Note: We iterate over the keys in the map; there is one key for
		// each affected date. DateEntry objects themselves often span dates,
        // meaning the same DateEntry object may be returned for different key dates
		for (Iterator it = this.dateEntries.keySet().iterator(); it.hasNext();) {
			DayOfYear nextDayOfYear = (DayOfYear) it.next();

			// Figure out the day of the week that the current date is on
			Calendar currentEntryCal = new GregorianCalendar(timeZone);
			currentEntryCal.setTime(nextDayOfYear.toDate(timeZone));

			// Only process entries for dates between the dates in question
			// and that are not overridden by a sub-calendar
            if (!nextDayOfYear.isBefore(earliestDayOfYear) && !nextDayOfYear.isAfter(latestDayOfYear) && !overrideChecker.isOverridden(currentEntryCal)) {

				// Grab the date entry for the next date key
				DateEntry nextEntry = (DateEntry) this.dateEntries.get(nextDayOfYear);

				// Grab the day of week entry for that day of the week
				// And check to see if the date exception allows less work or
				// more work
                // We look it up in the hierarchy to resolve the day of week entry
				DayOfWeekEntry dayOfWeekEntry = getDayOfWeek(currentEntryCal.get(Calendar.DAY_OF_WEEK));

				final SimpleTimeQuantity normalWorkingHours;
				final SimpleTimeQuantity overriddenWorkingHours;

				if (!dayOfWeekEntry.isWorkingDay()) {
					// Non working days are equivalent to zero hours
					normalWorkingHours = new SimpleTimeQuantity(0, 0);

				} else {
					// Working day; get the working hours adjusted if
					// the exceptional date falls on the start or end date
					normalWorkingHours = getRelevantHours(nextDayOfYear,earliestDayOfYear, latestDayOfYear, earliestTimeTimePart, latestTimeTimePart, dayOfWeekEntry);

				}

				if (!nextEntry.isWorkingDay()) {
					// Non working days are equivalent to zero hours
					overriddenWorkingHours = new SimpleTimeQuantity(0, 0);

				} else {
					// Working day; get the working hours adjusted if
					// the exceptional date falls on the start or end date
					overriddenWorkingHours = getRelevantHours(nextDayOfYear, earliestDayOfYear, latestDayOfYear, earliestTimeTimePart, latestTimeTimePart, nextEntry);

				}

				// overridden is greater than normal, then positive exception
				// overridden is less than normal, negative exception
				exceptionHourTotal = exceptionHourTotal.add(overriddenWorkingHours.subtract(normalWorkingHours));

			}
		}

		// Now repeat for the parent definition entries
		if (!isBaseCalendar()) {

			// First we construct a checker that indicates which dates
			// and day of week entries are present in this definition
			final IEntryOverrideChecker thisOverrideChecker = new IEntryOverrideChecker() {
				public boolean isOverridden(Calendar cal) {
					// A date is overridden if we have a date entry or a
					// day of week entry
					// This means that lower-level day of week entries can
					// override parent date entries
                    return ((WorkingTimeCalendarDefinition.this.getDate(cal) != null) || (WorkingTimeCalendarDefinition.this.getDayOfWeekNoHierarchy(cal.get(Calendar.DAY_OF_WEEK)) != null));

				}
			};

			// Count the non-overridden exceptions in the parent definition
			// and add to the current exception count
			exceptionHourTotal = exceptionHourTotal.add(this.parentCalendarDefinition.calculateNonOverriddenExceptionHours(startTime, endTime, timeZone, thisOverrideChecker));
		}

		return exceptionHourTotal;
	}

	/**
     * Returns the number of working hours from the specified working time
     * entry with special handling depending on whether the current date
     * is on the start and/or end date.
     * <p>
     * If the current date falls on the start date or the end date then only
     * the working hours after / before the start / end time are counted.
     * If the current date falls on the start date AND the end date (that is,
     * they are the same date) then only the working hours between the
     * start and end time are counted.  Otherwise, all working hours are
     * counted.
     * <p>
     * @param currentDayOfYear the current date
     * @param startDayOfYear the start date
     * @param endDayOfYear the end date
     * @param startTime the time on the start date
     * @param endTime the time on the end date
     * @param workingTimeCalendarEntry the entry from which to get the
     * working hours
	 * @return the total working hours
	 */
	private static SimpleTimeQuantity getRelevantHours(DayOfYear currentDayOfYear, DayOfYear startDayOfYear, DayOfYear endDayOfYear, Time startTime, Time endTime, WorkingTimeCalendarEntry workingTimeCalendarEntry) {

		final SimpleTimeQuantity relevantWorkingHours;

		// The number of hours is generally the
		// number of hours in the day; however we must handle
		// the case where the start date and end dates are part
		// way through the day
		if (currentDayOfYear.equals(startDayOfYear) && currentDayOfYear.equals(endDayOfYear)) {

            // relevant working hours is hours on entry between earliest & latest
			relevantWorkingHours = workingTimeCalendarEntry.getWorkingHoursBetween(startTime, endTime);

		} else if (currentDayOfYear.equals(startDayOfYear)) {
			// Overriding date entry occurs on earliest date
			// in range; we only use the hours later in the date
			relevantWorkingHours = workingTimeCalendarEntry.getRemainingWorkingHours(startTime);

		} else if (currentDayOfYear.equals(endDayOfYear)) {
			// Overriding date entry occurs on latest date
			// in range; we only use the hours earlier in the date
			relevantWorkingHours = workingTimeCalendarEntry.getEarlierWorkingHours(endTime);

		} else {
			// Overriding date somewhere in middle; use all the hours
			relevantWorkingHours = workingTimeCalendarEntry.getWorkingHours();

		}

		return relevantWorkingHours;
	}

	/**
	 * Defines an empty override checker meaning no dates are overridden.
	 */
	private static final IEntryOverrideChecker EMPTY_OVERRIDE_CHECKER = new IEntryOverrideChecker() {
		/**
		 * Returns false always.
         * @param cal the calendar containing a date to checl
		 * @return false always
		 */
		public boolean isOverridden(Calendar cal) {
			return false;
		}
	};

	/**
     * Updates the daysWorked based on the number of hours remaining on the
     * day specified by the calendar and the amount of remaining work.
	 * <p>
	 * At most one full day is added (of course the number of hours in the day
     * specified by the calendar may vary up to 24 hours).
     * The calendar is not modified. <br>
	 * <b>Note:</b> When the allocation is greater than 100%, the remaining
     * work will be reduced by more than the days worked.  For example, if
     * the allocation is 200% then the remaining work will be reduced by twice
     * as much as the days worked. <br>
     * If the allocation is 25% then the remaining work will be reduced by
     * one quarter of the days worked.
     * </p>
     * @param cal the calendar which is set to a working day; this is not
     * modified; the calendar's time zone must be set appropriately so that
     * the current day can be determined and the current working times can
     * be converted to actual dates
     * @param daysWorked the days worked to update; if number of hours in
     * the day is greater than remaining work then those number of hours are
     * added for the day; otherwise, the hours which are worked to complete
     * the remaining work are added
     * @param remainingWork the remaining work; the number of hours
     * allocated are subtracted from remaining work
	 */
    public void updateDaysWorked(DaysWorked daysWorked, Calendar cal, Allocation remainingWork) {

		// Grab the entry for the day / date of the calendar
		WorkingTimeCalendarEntry currentEntry = getEntry(cal);

		// Convert the time of the calendar to something we can work with
		Time startTime = new Time(cal);

		// Determine the times that are worked after the start time to complete
		// the specified work
		AggregatedWorkingTimes timesWorked = currentEntry.getTimesWorkedAfter(startTime, remainingWork.getActualTimeRemaining());

		if (timesWorked.getDuration().isZero()) {
			// No times worked this day; no subtraction to make

		} else {
			// Some times worked

            if (timesWorked.getDuration().compareTo(remainingWork.getActualTimeRemaining()) < 0) {
                // There were not enough hours in the day to complete the remaining
				// work; we simply deduct the number of hours that were worked
				// from the remaining work
				remainingWork.subtractActualHoursRemaining(timesWorked.getDuration().toHour());

			} else {
                // There were enough hours remaining in the day after the start time
				// to complete all the work
				remainingWork.zeroHoursRemaining();
			}

			// Now add the times worked to the days worked
			DayOfYear dayOfYear = new DayOfYear(cal);
			daysWorked.addDay(dayOfYear, timesWorked, cal.getTimeZone());

		}

	}

	/**
	 * Loads the calendar definition with the current ID from persistent store.
     * @throws PersistenceException if there is a problem loading
     * @throws NullPointerException if the current ID is null
	 * @see #setID
	 */
	private void load() throws PersistenceException {

		if (getID() == null) {
			throw new NullPointerException("ID is required before loading");
		}

		new WorkingTimeCalendarFinder().findByID(getID(), this);
	}

	/**
	 * Stores this calendar definition and all the entries in it.
	 * <p>
	 * The time zone must be specified before storing. <br>
     * If this calendar definition has an ID, it will be modified.
     * If this calendar definition does not have an ID, it will be created.
	 * </p>
	 * <p>
     * At least one working day of week is required to be found either in
     * this calendar or its parent calendar.
	 * </p>
     * @throws PersistenceException if there is a problem storing or
     * if this is a resource calendar that is not loaded and the resource
     * already has a calendar (that is, it is an attempt to create an
     * additional resource calendar for the same resource);
     * also if there is no working day of week entry (when resolving from
     * this calendar and the parent calendar)
     * @throws NullPointerException if the current plan ID is null
	 */
	public void store() throws PersistenceException {

		/*if (this.planID == null) {
			throw new NullPointerException("planID is required before storing");
		}*/

		DBBean db = new DBBean();

		try {

			// We're doing a multi-statement transaction
			db.setAutoCommit(false);

			if (getID() != null) {
				// Update existing calendar
				update(db, this.calendarID);

			} else {
				// Insert new calendar

				// First we must ensure that we don't violate the requirement
                // that there can only be one resource calendar per person per plan
				// We can't enforce this in the database because person_id can
				// be null; really would need a separate table for storing
				// resource calendars

				if (!isBaseCalendar()) {

					boolean isExistingResourceCalendar = false;

                    final String query = "select calendar_id from pn_workingtime_calendar wtc " +
                            "where wtc.resource_person_id = ? and wtc.plan_id = ? and wtc.record_status = ? ";

					db.prepareStatement(query);
					int index = 0;
					db.pstmt.setString(++index, this.resourcePersonID);
					db.pstmt.setString(++index, this.planID);
					db.pstmt.setString(++index, RecordStatus.ACTIVE.getID());
					db.executePrepared();

					if (db.result.next()) {
						isExistingResourceCalendar = true;
					}

					if (isExistingResourceCalendar) {
                        throw new PersistenceException("Person with ID " + this.resourcePersonID + " already has a working time calendar in this schedule");
					} else {
						// Does not exist
						insert(db);
					}

				} else {
					// Base calendar; insert it
					insert(db);
				}

			}

			// Now update the entries
			storeEntries(db);

			db.commit();

		} catch (SQLException e) {
            throw new PersistenceException("Error storing calendar definition: " + e, e);

		} finally {
			db.release();
		}

		// Now reload this calendar; certain items are set during load
		// Clear out some items so that load won't duplicate them
		this.dateEntries.clear();
		this.dayOfWeekEntries.clear();
		load();
	}

	/**
	 * Inserts the calendar and updates this definition's ID afterwards.
     * @param db the DBBean in which to perform the transaction
     * @throws SQLException if there is a problem inserting
	 */
	private void insert(DBBean db) throws SQLException {

		// insert the new calendar
        final String query = "insert into pn_workingtime_calendar " +
                "(calendar_id, plan_id, is_base_calendar, name, parent_calendar_id, resource_person_id, record_status) " +
                "values (?, ?, ?, ?, ?, ?, ?) ";

		// Generate a new ID
		String calendarID = ObjectManager.getNewObjectID();
		// Ensure record status is Active
		setRecordStatus(RecordStatus.ACTIVE);

		db.prepareStatement(query);
		int index = 0;
		db.pstmt.setString(++index, calendarID);
		index = bindCommonParameters(db.pstmt, index);
		db.pstmt.setString(++index, this.recordStatus.getID());
		db.executePrepared();

		setID(calendarID);
	}

	/**
	 * Updates the calendar with the specified calendarID.
     * @param db the DBBean in which to perform the transaction
     * @param calendarID the ID of the calendar to update
     * @throws SQLException if there is a problem updating
	 */
	private void update(DBBean db, String calendarID) throws SQLException {

		// insert the new calendar
        final String query = "update pn_workingtime_calendar set " +
                "plan_id = ?, is_base_calendar = ?, name = ?, parent_calendar_id = ?, " +
                "resource_person_id = ?, record_status = ? " +
                "where calendar_id = ? ";

		db.prepareStatement(query);
		int index = 0;
		index = bindCommonParameters(db.pstmt, index);
		db.pstmt.setString(++index, RecordStatus.ACTIVE.getID());
		db.pstmt.setString(++index, calendarID);
		db.executePrepared();

	}

	/**
     * Binds parameters common to inserting and updating to the prepared statement.
     * @param pstmt the prepared statement to which to bind
     * @param index the position of the last parameter bound; it will be increment
	 *            prior to binding the next parameter
     * @return the index of the last parameter bound by this method; it should be
     * incremented prior to binding the next parameter
     * @throws SQLException if ther eis a problem binding any parameters
	 */
    private int bindCommonParameters(PreparedStatement pstmt, int index) throws SQLException {

		pstmt.setString(++index, this.planID);
		pstmt.setBoolean(++index, isBaseCalendar());

		if (isBaseCalendar() && this.planID != null) {
			// Base calendars have a name, but no parent calendar,
			// or resource (as defined by space and person)
			pstmt.setString(++index, getName());
			pstmt.setNull(++index, Types.VARCHAR);
			pstmt.setNull(++index, Types.VARCHAR);

		}else if (isBaseCalendar()) {
			// Personal resource calendars have no name, no parent calendar,
			// but resourceId
			pstmt.setNull(++index, Types.VARCHAR);
			pstmt.setNull(++index, Types.VARCHAR);
			pstmt.setString(++index, this.resourcePersonID);

		} else {
			// Non-base calendars have no name
			pstmt.setNull(++index, Types.VARCHAR);
			pstmt.setString(++index, this.parentCalendarID);
			pstmt.setString(++index, this.resourcePersonID);

		}

		return index;
	}

	/**
	 * Stores the entries for this calendar definition.
	 * <p>
     * Note: Each entry's start and end times are actually stored as date
     * values created by updating the hour and minute components of the current
     * time.  The time is converted to a date using the server's
     * time zone.  The same time zone is used during load. It <i>cannot</i>
     * be changed without breaking any existing stored working times and
     * breaking the loadability of every working time calendar.
	 * </p>
     * @param db the DBBean in which to perform the transaction
     * @throws SQLException if there is a problem storing the entires
     * @throws NullPointerException if the current ID of this calendar definition
     * is null
	 */
	private void storeEntries(DBBean db) throws SQLException {

		if (getID() == null) {
			throw new NullPointerException("ID is required for storing entries");
		}

		StringBuffer query = new StringBuffer();

		// First delete all entries
        query.append("delete from pn_workingtime_calendar_entry ")
                .append("where calendar_id = ? ");

		db.prepareStatement(query.toString());
		db.pstmt.setString(1, getID());
		db.executePrepared();

		// Now insert all entries
		query = new StringBuffer();
        query.append("insert into pn_workingtime_calendar_entry ")
				.append("(calendar_id, entry_id, is_working_day, is_day_of_week, day_number, start_date, end_date, ")
				.append("time1_start, time1_end, time2_start, time2_end, time3_start, time3_end, time4_start, time4_end, time5_start, time5_end, ")
				.append("record_status, description) ")
				.append("values (?, ?, ?, ?, ?, ?, ?, ")
				.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ")
				.append("?, ?) ");

		db.prepareStatement(query.toString());

		// Entry ID is a simple sequential number since the compound
		// primary key is based on calendar_id and entry_id
		int entryIDSequence = 0;

		// Batch up day of week parameters
		for (Iterator it = getDayOfWeekEntries().iterator(); it.hasNext();) {
			DayOfWeekEntry nextEntry = (DayOfWeekEntry) it.next();

			int index = 0;
			db.pstmt.setString(++index, getID());
			db.pstmt.setString(++index, String.valueOf(entryIDSequence++));
			db.pstmt.setInt(++index, Conversion.booleanToInt(nextEntry.isWorkingDay()));
			db.pstmt.setInt(++index, Conversion.booleanToInt(true));
			db.pstmt.setInt(++index, nextEntry.getDayNumber());
			db.pstmt.setNull(++index, Types.TIMESTAMP);
			db.pstmt.setNull(++index, Types.TIMESTAMP);
			index = bindTimes(db.pstmt, index, nextEntry);
			db.pstmt.setString(++index, RecordStatus.ACTIVE.getID());
			db.pstmt.setString(++index, nextEntry.getDateDescription());
			db.pstmt.addBatch();
		}

		// Batch up date parameters
		for (Iterator it = getDateEntries().iterator(); it.hasNext();) {
			DateEntry nextEntry = (DateEntry) it.next();

			int index = 0;
			db.pstmt.setString(++index, getID());
			db.pstmt.setString(++index, String.valueOf(entryIDSequence++));
			db.pstmt.setInt(++index, Conversion.booleanToInt(nextEntry.isWorkingDay()));
			db.pstmt.setInt(++index, Conversion.booleanToInt(false));
			db.pstmt.setNull(++index, Types.NUMERIC);
			db.pstmt.setTimestamp(++index, new Timestamp(DayOfYear.getDateForStore(nextEntry.getStartDayOfYear()).getTime()));
			db.pstmt.setTimestamp(++index, new Timestamp(DayOfYear.getDateForStore(nextEntry.getEndDayOfYear()).getTime()));
			index = bindTimes(db.pstmt, index, nextEntry);
			db.pstmt.setString(++index, RecordStatus.ACTIVE.getID());
			db.pstmt.setString(++index, nextEntry.getDateDescription());
			db.pstmt.addBatch();
		}

		db.executePreparedBatch();

	}

	/**
	 * Binds the 5 start and end times in the specified entry to the specified
	 * PreparedStatement.
	 * <p>
	 * If the entry is not a working day,
     * @param pstmt the prepared statement to which to bind the parameters;
     * assumes there are up to 10 parameters to be bound, in pairs of start time
     * and end times
     * @param lastIndex the last index bound to the prepared statement; this
     * the next position used will be one greater than this value
     * @param nextEntry the entry from which to get times to bind
     * @return the last bound index position; subsequent parameters may
     * be bound at the next position
     * @throws SQLException if there is a problem binding the parameters to
     * the prepared statement
	 */
	private static int bindTimes(PreparedStatement pstmt, int lastIndex, WorkingTimeCalendarEntry nextEntry) throws SQLException {

		// Count the number of times we bound parameters
		int count = 0;

		// Initialize index; it will be pre-incremented before using
		int index = lastIndex;

		if (nextEntry.isWorkingDay()) {
			// Only add times if a working day

			// Create a calendar based on an arbitrary date (its going to be
			// "today") for the GMT timezone
			// We need this to convert hours and minutes of a day (e.g. "13:00")
			// into an actual timestamp
			Calendar cal = new GregorianCalendar(WorkingTime.TIME_CONVERSION_TIMEZONE);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			// Iterate over each working time, converting to a start and end
			// timestamp and bind it to the prepared statement
			for (Iterator it = nextEntry.getWorkingTimes().iterator(); it.hasNext();) {
				WorkingTime nextWorkingTime = (WorkingTime) it.next();

				// Construct start time from hour and minute
				Timestamp startTime = new Timestamp(makeDate(cal, nextWorkingTime.getStartTime()).getTime());

				// Construct end time from hour and minute
				Timestamp endTime = new Timestamp(makeDate(cal, nextWorkingTime.getEndTime()).getTime());

				// Now bind start and end time parameters
				pstmt.setTimestamp(++index, startTime);
				pstmt.setTimestamp(++index, endTime);

				// Increment count of pairs of bound parameters
				count++;
			}

		}

		// Bind the left over parameters with null
		// For non-working days this will be all parameters
		for (int i = 0; i < (5 - count); i++) {
			pstmt.setNull(++index, Types.TIMESTAMP);
			pstmt.setNull(++index, Types.TIMESTAMP);
		}

		return index;
	}

	/**
	 * Makes a date from the specified time using a calendar for the appropriate
	 * timezone.
     * @param cal the calendar constructed for the appropriate timezone; the
	 *            date portion is not changed, only hours and minutes
     * @param time the time whose components to use to construct a date
	 * @return a date based on the date of the calendar with hour and minutes
	 *         set to that of the specified time
	 */
	private static Date makeDate(Calendar cal, Time time) {
		cal.set(Calendar.HOUR_OF_DAY, time.getHour());
		cal.set(Calendar.MINUTE, time.getMinute());
		return cal.getTime();
	}

	/**
     * Removes the calendar definition with the current ID from persistent store.
     * Only an ID is required to remove.
     * @param replacementParentCalendarID the ID of the calendar to use as
     * a replacement for this one, for resource calendars using this
     * one as a parent
     * @throws PersistenceException if there is a problem removing
     * @throws NullPointerException if the current ID is null
	 * @see #setID
	 */
    public void remove(String replacementParentCalendarID) throws PersistenceException {

		if (getID() == null) {
			throw new NullPointerException("ID is required before removing");
		}

		DBBean db = new DBBean();

		try {
			db.setAutoCommit(false);

			updateResourceCalendar(db, replacementParentCalendarID);
			removeEntries(db);
			removeDefinition(db);

			db.commit();

		} catch (SQLException e) {
            throw new PersistenceException("Error removing working time calendar with ID " + getID() + ": " + e, e);

		} finally {
			db.release();

		}

	}

	/**
     * Updates existing resource calendar that have this calendar
     * as their parent to have the specified calendar as their parent.
     * @param db the DBBean in which to perform the transaction
     * @param replacementParentCalendarID the ID of the calendar to use as
     * a replacement for this one, for resource calendars using this
     * one as a parent
     * @throws SQLException if there is a problem updating
     */
    private void updateResourceCalendar(DBBean db, String replacementParentCalendarID) throws SQLException {

        final String query = "update pn_workingtime_calendar " +
                "set parent_calendar_id = ? " +
                "where parent_calendar_id = ? ";

		db.prepareStatement(query);
		int index = 0;
		db.pstmt.setString(++index, replacementParentCalendarID);
		db.pstmt.setString(++index, getID());
		db.executePrepared();

	}

	/**
	 * Removes all the entries.
     * @param db the DBBean in which to perform the transaction
     * @throws SQLException if there is a problem removing
	 */
	private void removeEntries(DBBean db) throws SQLException {
        final String query = "update pn_workingtime_calendar_entry set record_status = '" +
                RecordStatus.DELETED.getID() + "' where calendar_id = ? ";

		db.prepareStatement(query);
		db.pstmt.setString(1, getID());
		db.executePrepared();
	}

	/**
	 * Removes the definition.
     * @param db the DBBean in which to perform the transaction
     * @throws SQLException if there is a problem removing
	 */
	private void removeDefinition(DBBean db) throws SQLException {
        final String query = "update pn_workingtime_calendar set record_status = '" +
                RecordStatus.DELETED.getID() + "' where calendar_id = ? ";

		db.prepareStatement(query);
		db.pstmt.setString(1, getID());
		db.executePrepared();
	}

	public String getXML() {
		return getXMLDocument().getXMLString();
	}

	public String getXMLBody() {
		return getXMLDocument().getXMLBodyString();
	}

	/**
	 * Returns the xml representation of this working time calendar definition.
	 * @return the xml
	 */
    public XMLDocument getXMLDocument() {
		XMLDocument xml = new XMLDocument();
		xml.startElement("WorkingTimeCalendarDefinition");
		xml.addElement("ID", this.calendarID);
		xml.addElement("IsBaseCalendar", Boolean.valueOf(isBaseCalendar()));
		xml.addElement("Name", getName());
		xml.addElement("ParentCalendarID", getParentCalendarID());
		xml.addElement("ResourcePersonID", getResourcePersonID());
		xml.addElement("ResourceDisplayName", getResourceDisplayName());
		xml.addElement(this.recordStatus.getXMLDocument());
		xml.addElement("DisplayName", getDisplayName());
		xml.endElement();
		return xml;
	}

	/**
     * Checks to ensure that there is some working time available
     * after the specified calendar's date (including the date).
	 * <p>
     * It is possible that a situation occurs where there are no
     * available days of week for a calendar (due to hierarchial settings).
     * In that case, working time is solely specified by date entries.
     * It is possible that there are no date entries, or none after
     * the calendar's date that allow the task to complete.
	 * </p>
	 */
    public boolean isWorkingTimeAvailable(Calendar cal) {
		// At least one day of week is a working day of week that has
		// some working time
		// We're guaranteed working time on that day, at least
		// some date in the future
		// OR
		// There is some working time available on a later date
		return (isWorkingDayOfWeekDefined() || isLaterWorkingDateDefined(cal));
	}

	/**
     * Indicates whether there is at least one working day of week that has
     * at least some working time.
	 * <p>
     * No working time withing working days of week is a bad situation; it may be impossible
     * to complete tasks, unless there are sufficient overridden working
     * dates.  It typically means the calendar is not defined correctly.
	 * </p>
     * @return true if there is at least one working day of week that has some working time,
     * after hierarchical lookup; false if there are no days of week with working time
	 */
    public boolean isWorkingDayOfWeekDefined() {

		boolean isDefined = false;

		// Loop over day of week entries, breaking when a working day is found
		for (Iterator it = getDayOfWeekEntriesHierarchical().iterator(); it.hasNext() && !isDefined;) {
			DayOfWeekEntry nextentry = (DayOfWeekEntry) it.next();
			isDefined = (nextentry.isWorkingDay() && !nextentry.getWorkingHours().isZero());
		}

		return isDefined;
	}

	/**
     * Indicates whether there is at least one working date on
     * or after the time of the specified calendar.
     * @param cal the calendar containing the date to check
     * @return true if there is at least one working date entry defined
     * where the date entry is on the specified calendar's date and
     * the some working time is available after the time calendar, or
     * the date entry is after the specified calendar's date
	 */
	private boolean isLaterWorkingDateDefined(Calendar cal) {

		boolean isDefined = false;

        // Loop over date entries, looking for one with working time on or after cal
		for (Iterator it = getDateEntries().iterator(); it.hasNext() && !isDefined;) {
			DateEntry nextentry = (DateEntry) it.next();
			isDefined = nextentry.isWorkingTimeOnOrAfter(cal);
		}

		return isDefined;
	}

	/**
	 * Updates the date entries with the specified entry.
	 * <p>
     * Regardless of whether it is a new or existing entry, all
     * existing entries which which it overlaps are adjusted to
     * eliminate the overlap. <br>
     * The result is that the number of entries may stay the same,
     * or increase by up to three entries, depending on how many
     * overlaps there are.
	 * </p>
     * @param entry the entry to update the entries with
	 */
    public void updateDateEntries(DateEntry entry) {

		Collection newEntries = new ArrayList();

		for (Iterator it = getDateEntries().iterator(); it.hasNext();) {
			DateEntry nextEntry = (DateEntry) it.next();

			if (!hasSameID(nextEntry, entry)) {
				// Entry has no ID or different from next entry
				// We process the nextEntry WRT to this entry to split it
				// if overlap occurs
                // We add the processed entries (which may be unchanged) to the new entries
				newEntries.addAll(processEntry(nextEntry, entry));
			}
		}

		// Now add the specified entry to the collection
		newEntries.add(entry);

		// Update the date entries with the newly formed entries
		// We use the addDateEntry method since the dateEntries map
		// is specially constructed
		this.dateEntries.clear();
		for (Iterator it = newEntries.iterator(); it.hasNext();) {
			DateEntry nextEntry = (DateEntry) it.next();
			addDateEntry(nextEntry);
		}

	}

	/**
	 * Indicates whether the specified entries both have the same ID.
     * @param entry1 the first entry
     * @param entry2 the second entry
     * @return true if both entry1 and entry2 have a non-null ID and they are equal;
     * false if either entry has a null ID or both IDs are not equal
	 */
	private static boolean hasSameID(WorkingTimeCalendarEntry entry1, WorkingTimeCalendarEntry entry2) {
		return (entry1.getEntryID() != null && entry1.getEntryID().equals(entry2.getEntryID()));
	}

	/**
	 * Processes a current entry, checking for overlap with a new entry.
	 * <p>
     * If the new entry overlaps the current entry, the current entry is
     * split into zero or more entries (actually, zero to three) and those
     * new entries are returned. <br>
	 * The effect of this is to return a collection of entries that cover all
	 * the dates specified by the current entry, excluding those dates specified
	 * by the new entry. <br>
     * If there is no overlap at all, the current entry is simply returned unchanged.
	 * </p>
     * @param currentEntry the entry to check for overlap and to split
     * @param newEntry the new entry that takes precedence
	 * @return a collection where each element is a <code>DateEntry</code>
     * where those entries replace the current entry; possibly empty if the new
     * entry completely overrides all dates in the current entry.
     * Note that the new entry is not returned in this collection
	 */
    private static Collection processEntry(DateEntry currentEntry, DateEntry newEntry) {

		Collection processedEntries = new ArrayList();

		if (currentEntry.isOverlappedBy(newEntry)) {
			// Only process the current entry if it really is
			// overlapped by the new entry

			// We convert the currene entry into zero, one or two entries
			// depending on whether it is:
			// (0) - fully replaced by new entry
			// (1) - partially overlapped by new entry
			// (2) - new entry fully within current entry

			if (newEntry.getStartDayOfYear().isOnOrBetween(currentEntry.getStartDayOfYear(), currentEntry.getEndDayOfYear())) {
                // New entry starts between the start and end date of the current entry
                // We must grab the differences between the current entry start and the new start

				if (currentEntry.getStartDayOfYear().isBefore(newEntry.getStartDayOfYear())) {
					// It is at least one day prior
                    // Create an entry from current start to day before new start
					// and add it to the processed entries
					processedEntries.add(currentEntry.copy(currentEntry.getStartDayOfYear(), newEntry.getStartDayOfYear().previousDay()));
				}

			}

			if (newEntry.getEndDayOfYear().isOnOrBetween(currentEntry.getStartDayOfYear(), currentEntry.getEndDayOfYear())) {
                // New entry ends between the start and end date of the current entry
                // We must grab the differences between the current entry start and the new start

				if (newEntry.getEndDayOfYear().isBefore(currentEntry.getEndDayOfYear())) {
                    // New entry end is at least a day earlier then current entry end date
                    // Create an entry from day after new end to current entry end
					// and add it to the processed entries
					processedEntries.add(currentEntry.copy(newEntry.getEndDayOfYear().nextDay(), currentEntry.getEndDayOfYear()));
				}

			}

		} else {
			// Not overlapped; we keep the current entry as is
			processedEntries.add(currentEntry);
		}

		return processedEntries;
	}

	/**
	 * Removes the date entry for the specified entry ID.
     * @param entryID the ID of the entry to remove
     * @throws NullPointerException if entryID is null
     * @throws IllegalArgumentException if there is no date entry with the
     * specified ID
	 */
    public void removeDateEntry(String entryID) {
		if (entryID == null) {
			throw new NullPointerException("entryID is required");
		}

		// Removes the entry from the entries map
		// Note that the same entry can be found in more than one position
		// in the map (for the case of spanning date entries)
		// We must ensure that all values are removed; thus we cannot break
		// after the first entry is found
		boolean isFound = false;
		for (Iterator it = this.dateEntries.values().iterator(); it.hasNext();) {
			DateEntry nextEntry = (DateEntry) it.next();
			if (nextEntry.getEntryID() != null && nextEntry.getEntryID().equals(entryID)) {
				it.remove();
				isFound = true;
			}
		}

		if (!isFound) {
            throw new IllegalArgumentException("No date entry found for ID " + entryID);
		}

	}

	/**
	 * Determine the amount of working time between two dates. If the first date
	 * is after the first date, the amount will be negative.
	 * 
     * @param date1 the starting day and time
     * @param date2 the ending day and time.
     * @return the total amount of working time between the dates in the specified
     * calendars.
	 */
	public TimeQuantity subtractWorkingTimeForDateRange(Date date1, Date date2) {
		Calendar cal1 = new GregorianCalendar(SessionManager.getUser().getTimeZone());
		Calendar cal2 = (Calendar) cal1.clone();
		cal1.setTime(date1);
		cal2.setTime(date2);

		return subtractWorkingTimeForDateRange(cal1, cal2);
	}

	/**
	 * Determine the amount of working time between two dates. If the first date
	 * is after the first date, the amount will be negative.
	 * 
     * @param cal1 the starting day and time
     * @param cal2 the ending day and time.
     * @return the total amount of working time between the dates in the specified
     * calendars.
	 */
	public TimeQuantity subtractWorkingTimeForDateRange(Calendar cal1, Calendar cal2) {
		boolean negate = false;
		if (cal2.before(cal1)) {
			negate = true;
		}

		TimeQuantity amount = new TimeQuantity(getWorkingTimeAmountForDateRange(cal1, cal2).toHour(), TimeQuantityUnit.HOUR);

		if (negate) {
			amount = amount.negate();
		}

		return amount;
	}

	/**
	 * Get the number of working hours that occurred on a date.
	 * 
     * @param date a <code>Date</code> on which you want to know how many working
     * hours there are.  The "time" portion of the date is ignored.
     * @param timeZone timezone in which the the working time is requested
	 * @return a <code>SimpleTimeQuantity</code> this indicates the amount of
	 *         time the user worked on a given day.
	 */
	public TimeQuantity getWorkingTimeAmountForDate(Date date, TimeZone timeZone) {
		Calendar cal = new GregorianCalendar(timeZone);
		cal.setTime(date);

		WorkingTimeCalendarEntry entry = getEntry(cal);
		SimpleTimeQuantity stq = entry.getWorkingHours();
		return new TimeQuantity(stq.toHour(), TimeQuantityUnit.HOUR);
	}

	/**
	 * Get the number of working hours that occurred between two dates.
	 * <p>
	 * The calendars are not modified.
	 * </p>
     * @param cal1 the starting day and time
     * @param cal2 the ending day and time
     * @return the total amount of working time between the dates in the specified calendars
	 */
	public SimpleTimeQuantity getWorkingTimeAmountForDateRange(Calendar cal1, Calendar cal2) {
        //If cal1's time is after cal2's time, we need to reverse them.  Otherwise
		// the algorithm won't work.
		if (cal1.getTime().after(cal2.getTime())) {
			Calendar temp;
			temp = cal1;
			cal1 = cal2;
			cal2 = temp;
		}

		Time time1 = new Time(cal1);
		Time time2 = new Time(cal2);

		SimpleTimeQuantity totalTimeWorked;

		// Determine if the dates are on the same day
        if ((cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) && (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))) {

			// Get the total amount of time between the days
			WorkingTimeCalendarEntry entry = getEntry(cal1);
			totalTimeWorked = entry.getWorkingHoursBetween(time1, time2);

		} else {
            // Both days aren't on the same day, so we need to iterate and return
			// an aggregated amount of time.
			WorkingTimeCalendarEntry entry;

			// Get the amount of time on the first date
			entry = getEntry(cal1);
			totalTimeWorked = entry.getRemainingWorkingHours(time1);

			// Iterate through the days in the middle, adding each

			// Create a calendar at midnight on the following day
			Calendar workingCal = (Calendar) cal1.clone();
			DateUtils.zeroTime(workingCal);
			workingCal.add(Calendar.DATE, 1);

			// Get all the dates that occur before the first date in the date
			// range and the last date in the range.
			Calendar endCal = (Calendar) cal2.clone();
			DateUtils.zeroTime(endCal);
			while (workingCal.before(endCal)) {
				WorkingTimeCalendarEntry middleEntry = getEntry(workingCal);
				totalTimeWorked = totalTimeWorked.add(middleEntry.getWorkingHours());

				workingCal.add(Calendar.DATE, 1);
			}

			// Now get the amount of time on the last date
			entry = getEntry(cal2);
			totalTimeWorked = totalTimeWorked.add(entry.getEarlierWorkingHours(time2));
		}

		return totalTimeWorked;
	}

	/**
	 * Get the number of working hours that occurred between two dates.
	 * <p>
	 * The calendars are not modified.
	 * </p>
     * @param date1 the starting day and time
     * @param date2 the ending day and time
     * @param timeZone the timezone in which working time is to be computed
     * @return the total amount of working time between the dates in the specified calendars
	 */
	public SimpleTimeQuantity getWorkingTimeAmountForDateRange(Date date1, Date date2, TimeZone timeZone) {
		Calendar cal1 = new PnCalendar(timeZone);
		cal1.setTime(date1);
		Calendar cal2 = new PnCalendar(timeZone);
		cal2.setTime(date2);

		return getWorkingTimeAmountForDateRange(cal1, cal2);
	}

	//
	// Nested top-level classes
	//

	/**
     * Implementing classes are used to indicate whether an date is specified
     * by a particular working time calendar definition.
	 */
	private static interface IEntryOverrideChecker {
		/**
         * Indicates that the specified calendar's date is overridden by
         * the implementing class.
         * @param cal the calendar containing the date to check
		 * @return true if the date is overridden by an entry in this calendar;
		 *         false otherwise
		 */
		boolean isOverridden(Calendar cal);
	}

	/**
	 * Get WorkingTimeCalendarBean
	 * @return
	 */
	
	public WorkingTimeCalendarBean getCalendarBean() {
		WorkingTimeCalendarBean calendarBean = new WorkingTimeCalendarBean();
		calendarBean.setBaseCalendar(this.isBaseCalendar);
		calendarBean.setBaseCalendarUID(this.parentCalendarID);
		calendarBean.setName(this.name);
		calendarBean.setWeekDays(getWeekDayBeanList());
		
		return calendarBean;
	}

	/**
	 * Get List of WeekDayBean objects
	 * @return
	 */	
	    
	private List<WeekDayBean> getWeekDayBeanList() {
		List<WeekDayBean> weekDayBeanList = new ArrayList();
		Collection weekDayEntries = getDayOfWeekEntries();
		Iterator iter = weekDayEntries.iterator();
		WeekDayBean weekDayBean = null;
		while(iter.hasNext()) {
			DayOfWeekEntry dayOfWeekEntry = (DayOfWeekEntry) iter.next();
			weekDayBeanList.add(weekDayBean);
		}
		return weekDayBeanList;
	}	
}
