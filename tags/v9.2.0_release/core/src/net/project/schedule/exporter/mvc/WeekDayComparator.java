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
/**
 * 
 */
package net.project.schedule.exporter.mvc;

import java.util.Comparator;

import net.project.calendar.workingtime.WeekDayBean;

/**
 * @author avibha
 *
 */
public class WeekDayComparator implements Comparator {

	/**
	 * compare two WeekDayBean type of objects on the basic of their weekdayType
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public int compare(Object obj1, Object obj2) {
		if(!(obj1 instanceof WeekDayBean) || !(obj2 instanceof WeekDayBean))
			throw new ClassCastException("Can not compare objects which are not instances of Type: WeekDayBean");
		int dayType1 = Integer.parseInt(((WeekDayBean)obj1).getDayType());
		int dayType2 = Integer.parseInt(((WeekDayBean)obj2).getDayType());
		
		return dayType1 - dayType2;
	}
}
