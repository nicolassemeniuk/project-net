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
import java.util.List;

/**
 * @author avinash
 *
 */
public class WorkingTimeCalendarBean implements Serializable{
	private String uid;
	private String name = null;
	private boolean baseCalendar;
	private String baseCalendarUID;
	private List<WeekDayBean> weekDays;
	/**
	 * @return Returns the baseCalendar.
	 */
	public boolean isBaseCalendar() {
		return baseCalendar;
	}
	/**
	 * @param baseCalendar The baseCalendar to set.
	 */
	public void setBaseCalendar(boolean baseCalendar) {
		this.baseCalendar = baseCalendar;
	}
	/**
	 * @return Returns the baseCalendarUID.
	 */
	public String getBaseCalendarUID() {
		return baseCalendarUID;
	}
	/**
	 * @param baseCalendarUID The baseCalendarUID to set.
	 */
	public void setBaseCalendarUID(String baseCalendarUID) {
		this.baseCalendarUID = baseCalendarUID;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the uid.
	 */
	public String getUid() {
		return uid;
	}
	/**
	 * @param uid The uid to set.
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}
	/**
	 * @return Returns the weekDays.
	 */
	public List<WeekDayBean> getWeekDays() {
		return weekDays;
	}
	/**
	 * @param weekDays The weekDays to set.
	 */
	public void setWeekDays(List<WeekDayBean> weekDays) {
		this.weekDays = weekDays;
	}

}
