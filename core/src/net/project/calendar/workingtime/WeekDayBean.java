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

import java.util.List;

/**
 * @author avinash
 *
 */
public class WeekDayBean {
	private String dayType;
	private boolean dayWorking;
	private List timePeriods;
	private List<WorkingTime> workingTimes;
	
	/**
	 * @return Returns the dayType.
	 */
	public String getDayType() {
		return dayType;
	}
	/**
	 * @param dayType The dayType to set.
	 */
	public void setDayType(String dayType) {
		this.dayType = dayType;
	}
	/**
	 * @return Returns the dayWorking.
	 */
	public boolean isDayWorking() {
		return dayWorking;
	}
	/**
	 * @param dayWorking The dayWorking to set.
	 */
	public void setDayWorking(boolean dayWorking) {
		this.dayWorking = dayWorking;
	}
	/**
	 * @return Returns the timePeriods.
	 */
	public List getTimePeriods() {
		return timePeriods;
	}
	/**
	 * @param timePeriods The timePeriods to set.
	 */
	public void setTimePeriods(List timePeriods) {
		this.timePeriods = timePeriods;
	}
	/**
	 * @return Returns the workingTimes.
	 */
	public List<WorkingTime> getWorkingTimes() {
		return workingTimes;
	}
	/**
	 * @param workingTimes The workingTimes to set.
	 */
	public void setWorkingTimes(List<WorkingTime> workingTimes) {
		this.workingTimes = workingTimes;
	}
}
