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
 |    $Revision: 15807 $
 |        $Date: 2007-04-09 03:03:58 +0530 (Mon, 09 Apr 2007) $
 |      $Author: avinash $
 |
 +----------------------------------------------------------------------*/
package net.project.schedule.importer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.project.calendar.workingtime.Time;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.calendar.workingtime.WorkingTimeCalendarEntry;
import net.project.soa.schedule.Project;
import net.project.soa.schedule.Project.Calendars.Calendar.WeekDays.WeekDay;
import net.project.soa.schedule.Project.Calendars.Calendar.WeekDays.WeekDay.WorkingTimes.WorkingTime;

/**
 * Provides a collection (small "c") of <code>MSPCalendar</code>s imported
 * from an XML file.
 * 
 * @author Tim Morrow
 * @since 7.6.3
 */
class MSPCalendars {

	//
	// Static Members
	//

	/**
	 * Makes a base WorkingTimeCalendarDefintion from the specified MSPCalendar.
	 * 
	 * @param calendar
	 *            the calendar from which to create the working times
	 * @return the working times; these have not been stored.
	 */
	static WorkingTimeCalendarDefinition makeBaseCalendarDefinition(Project.Calendars.Calendar calendar) {
		WorkingTimeCalendarDefinition calendarDef = WorkingTimeCalendarDefinition.makeBaseCalendar(calendar.getName());
		calendarDef.setEntries(getEntries(calendar));
		return calendarDef;
	}

	/**
	 * Makes a WorkingTimeCalendarDefintion from the specified MSPCalendar.
	 * 
	 * @param calendar
	 *            the calendar from which to create the working times
	 * @param parentCalendarID
	 *            the database ID of the stored parent calendar
	 * @param resourcePersonID
	 *            the ID of the person who is the owning resource
	 * @return the working times; these have not been stored.
	 */
	static WorkingTimeCalendarDefinition makeResourceCalendarDefinition(Project.Calendars.Calendar calendar, String parentCalendarID, String resourcePersonID) {
		WorkingTimeCalendarDefinition calendarDef = WorkingTimeCalendarDefinition.makeResourceCalendar(parentCalendarID, resourcePersonID);
		calendarDef.setEntries(getEntries(calendar));
		return calendarDef;
	}

	/**
	 * Updates the specified working time calendar definition entries from the
	 * entries in the MSPCalendar.
	 * 
	 * @param calendar
	 *            the MSPCalendar from which to get the entries
	 * @param calendarDef
	 *            the calendar definition to update
	 */
	static void updateCalendarDefinitionEntries(Project.Calendars.Calendar calendar, WorkingTimeCalendarDefinition calendarDef) {
		calendarDef.setEntries(getEntries(calendar));
	}

	/**
	 * Returns WorkingTimeCalendarEntries constructed from the MSPCalendar.
	 * 
	 * @param calendar
	 *            the MSP calendar from which to get entries
	 * @return a collection where each element is a
	 *         <code>WorkingTimeCalendarEntry</code>
	 */
	private static Collection getEntries(Project.Calendars.Calendar calendar) {

		Collection<WorkingTimeCalendarEntry> entries = new ArrayList<WorkingTimeCalendarEntry>();

		// Now add all the calendar data
		if (calendar.getWeekDays() != null && calendar.getWeekDays().getWeekDay() != null)
			for (Iterator it = calendar.getWeekDays().getWeekDay().iterator(); it.hasNext();) {
				WeekDay nextCalendarData = (WeekDay) it.next();

                WorkingTimeCalendarEntry entry;
                // xml fine converted using plugin converter for MSP from MS, has day number as '0' - indicates off working day
                if(nextCalendarData.getDayType().intValue() < 1 || nextCalendarData.getDayType().intValue() > 7 )
                {
                    entry = WorkingTimeCalendarEntry.makeNonWorkingDate(
                            new net.project.calendar.workingtime.DayOfYear(nextCalendarData.getTimePeriod().getFromDate().toGregorianCalendar()) ,
                            new net.project.calendar.workingtime.DayOfYear(nextCalendarData.getTimePeriod().getToDate().toGregorianCalendar()));
                } else if (nextCalendarData.isDayWorking()) {
                    entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(nextCalendarData.getDayType().intValue());
                    entry.setWorkingTimes(prepareWorkingTimes(nextCalendarData.getWorkingTimes().getWorkingTime()));
                } else {
                    entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(nextCalendarData.getDayType().intValue());
                }
                entries.add(entry);
            }

		return entries;
	}

	/**
	 * Converts the specified date that was read from an ODBC driver to a
	 * DayOfYear.
	 * <p>
	 * The ODBC driver converts datetimes to a date using the deprecated
	 * {@link java.sql.Timestamp#Timestamp(int, int, int, int, int, int, int)}
	 * method which in turn uses the deprecated
	 * {@link Date#Date(int, int, int, int, int, int)} constructor which uses
	 * the "local" time zone (that is, the default time zone for the JVM). <br>
	 * Thus to convert the date to a day of year we must use the default time
	 * zone.
	 * </p>
	 * 
	 * @param date
	 *            the date to convert
	 * @return the day of year representing the year, month and day of the date
	 */
	/*
	 * private static DayOfYear toDayOfYear(Date date) { // Create a calendar
	 * using the default time zone Calendar cal = Calendar.getInstance();
	 * cal.setTime(date); return new DayOfYear(cal); }
	 */

	//
	// Instance Members
	//
	/**
	 * Each key is an <code>Integer</code> calendar UID; each value is an
	 * <code>MSPCalendar</code>.
	 */
	private final Map<Integer, Project.Calendars.Calendar> calendarMap = new HashMap<Integer, Project.Calendars.Calendar>();

	/**
	 * Adds the specified calendar.
	 * 
	 * @param calendar
	 *            the calendar to add
	 */
	void add(Project.Calendars.Calendar calendar) {
		this.calendarMap.put(new Integer(calendar.getUID().intValue()), calendar);
	}

	/**
	 * Returns the calendar for the specified ID.
	 * 
	 * @param calendarUID
	 *            the ID of the calendar to get
	 * @return the calendar or null if none matches the ID.
	 */
	Project.Calendars.Calendar get(int calendarUID) {
		return this.calendarMap.get(new Integer(calendarUID));
	}

	/**
	 * Returns the default calendar.
	 * <p>
	 * The default calendar is the Project Calendar.
	 * </p>
	 * 
	 * @param project
	 *            the current project; this is used to locate the project
	 *            calendar
	 * @return the default calendar
	 */
	public Project.Calendars.Calendar getDefaultCalendar(Project project) {
		Project.Calendars.Calendar defaultCalendar = null;

		for (Iterator it = this.calendarMap.values().iterator(); it.hasNext();) {
			Project.Calendars.Calendar nextCalendar = (Project.Calendars.Calendar) it.next();

			// Names are null for resource calendars; ignore them
			String name = nextCalendar.getName();
			if (name != null && nextCalendar.isIsBaseCalendar()) {
				defaultCalendar = nextCalendar;
				break;
			}
		}

		return defaultCalendar;
	}

	/**
	 * Indicates whether this is a resource calendar for the resources with the
	 * specified UID.
	 * 
	 * @param resourceUID
	 *            the UID of the resource to check
	 * @return true if there is a resource calendar for that resource
	 */
	/*
	 * boolean isCalendarForResource(int resourceUID) {
	 * 
	 * boolean isFound = false;
	 * 
	 * for (Iterator it = this.calendarMap.values().iterator(); it.hasNext() &&
	 * !isFound;) { Project.Calendars.Calendar nextcalendar =
	 * (Project.Calendars.Calendar) it.next(); if
	 * (!nextcalendar.isIsBaseCalendar() && nextcalendar.getResourceUID() ==
	 * resourceUID) { isFound = true; } }
	 * 
	 * return isFound; }
	 */

	/**
	 * Returns an unmodifiable collection of calendars owned by resources.
	 * <p>
	 * For each element, <code>isResourceCalendar()</code> will return
	 * <code>true</code>
	 * </p>
	 * 
	 * @return a collection where each element is a <code>MSPCalendar</code>
	 */
	Collection getResourceCalendars() {

		List<Project.Calendars.Calendar> resourceCalendars = new ArrayList<Project.Calendars.Calendar>();

		for (Iterator it = this.calendarMap.values().iterator(); it.hasNext();) {
			Project.Calendars.Calendar nextCalendar = (Project.Calendars.Calendar) it.next();

			if (!nextCalendar.isIsBaseCalendar()) {
				resourceCalendars.add(nextCalendar);
			}
		}

		return Collections.unmodifiableCollection(resourceCalendars);
	}

	private static Collection prepareWorkingTimes(List<WorkingTime> xmlWorkingTime) {
		// Calendar MUST use the default locale
		Calendar cal = Calendar.getInstance();

		Collection<net.project.calendar.workingtime.WorkingTime> workingTimes = new ArrayList<net.project.calendar.workingtime.WorkingTime>();
		Iterator<WorkingTime> xmlListItr = xmlWorkingTime.iterator();
		while (xmlListItr.hasNext()) {
			WorkingTime wTime = xmlListItr.next();
			Date fromTime = wTime.getFromTime().toGregorianCalendar().getTime();
			Date toTime = wTime.getToTime().toGregorianCalendar().getTime();
			if (fromTime != null && toTime != null) {
				cal.setTime(fromTime);
				Time start = new Time(cal);
				cal.setTime(toTime);
				Time end = new Time(cal);
				workingTimes.add(new net.project.calendar.workingtime.WorkingTime(start, end));
			}
		}
		return workingTimes;
	}
}
