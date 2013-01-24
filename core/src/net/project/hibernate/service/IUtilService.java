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

package net.project.hibernate.service;

import java.util.Date;
import java.util.List;

import net.project.business.report.TimeSubmitalWeek;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.hibernate.model.resource_reports.ReportMonth;
import net.project.util.DateFormat;

/**
 * @author Vlad Melanchyk
 *
 */
public interface IUtilService {
    
    /**
     * Compares two dates. 
     * @param date1
     * @param date2
     * @return false if either Date is null and other is not null, or if dates are not equal, true otherwise
     */
    public boolean compareDates(Date date1, Date date2);
    
    /**
     * Compares two strings.
     * @param string1
     * @param string2
     * @return false if either String is null and other is not null, or if strings are not equal, true otherwise
     */
    public boolean compareStrings(String string1, String string2);

    /**
	 * get number of months beetwen two dates
	 * 
	 * @param startDate interval start date
	 * @param endDate   interval end date
	 * @return number of months
	 */    
    public List<ReportMonth> getMonthsBetween(Date startDate, Date endDate);
    
    /** 
	 * get number of working days in specified date interval
	 * 
	 * @param startDate  interval start date
	 * @param endDate    interval end date
	 * @return number of working days
	 */
    public int getNumberOfWorkingDays(Date startDate, Date endDate);
    
    /** 
     * get number of working hours in specified date interval as per resource defined working time calendar.
     * 
     * @param startDate  interval start date
     * @param endDate    interval end date
     * @param resoruceWTCDefinition
     * @return number of working days
     */
    public float getWorkingHours(Date startDate, Date endDate, WorkingTimeCalendarDefinition resoruceWTCDefinition);
    
    /** 
	 * get date after cleaning time part
	 * @param date  
	 * @return date 
	 */
    public Date clearTimePart(Date date);
    
    /**
     * Method to display date in user friendly date format
     * @param date
     * @param dateFormat
     * @return String
     */
    public String calculateDate(Date dateToCalculate,DateFormat userDateFormatter);
    
    /**
     * Method to display date in weekly format
     * @param startDate
     * @param endDate
     * @return
     */
    public List<TimeSubmitalWeek> getWeeksBetween(Date startDate, Date endDate);
    
    /**
     * Method to get days between two days in string
     * E.g. :
     *      If user posted blog is over days limit then return as "over {dayslimit} days"
     *      If user posted blog is below over days limit 
     *      		then return as "{days between last blog and current date} days ago"
     *      For Today and Yesterday it returns "Today" and "Yesterday" respectively
     *       
     * @param dateToCheck
     * @param currentDate
     * @return String
     */
    public String getDaysBetween(Date dateToCheck, Date currentDate);
}
