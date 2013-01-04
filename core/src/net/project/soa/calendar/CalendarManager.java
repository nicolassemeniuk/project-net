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
package net.project.soa.calendar;

import net.project.calendar.CalendarBean;
import net.project.calendar.ICalendarEntry;
import net.project.calendar.PnCalendar;
/**
 * Webservice Implementation for pnet calendarBean   
 * 
 * @author Avinash Bhamare
 * @since 8.3.0
 */
public class CalendarManager extends CalendarBean implements ICalendarManager {
	
	/**
	 * Returns all calender entries for current year for the logged in user
	 */
	public ICalendarEntry[] getCalendarEntries() throws Exception {
		CalendarBean calendarBean = new CalendarBean();
		calendarBean.setTime(PnCalendar.currentTime());
		
		//	Configure the calendar
		calendarBean.loadIfNeeded();
	    int monthNum = calendarBean.get(CalendarBean.MONTH);
	    int yearNum = calendarBean.get(CalendarBean.YEAR);
	    calendarBean.setStartDate(calendarBean.startOfMonth(monthNum, yearNum));
	    calendarBean.setEndDate(calendarBean.endOfMonth(monthNum, yearNum));
	    // load following type of entries
		String[] entryTypes= {"meeting", "event", "milestone", "task"};
		calendarBean.loadEntries(entryTypes);
		return (ICalendarEntry[]) calendarBean.getEntries().toArray();
		
	}

}
