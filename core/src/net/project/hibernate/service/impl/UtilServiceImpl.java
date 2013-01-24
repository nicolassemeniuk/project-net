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

package net.project.hibernate.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.business.report.TimeSubmitalWeek;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.hibernate.model.resource_reports.ReportMonth;
import net.project.hibernate.service.IUtilService;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.DateUtils;

import org.springframework.stereotype.Service;

/**
 * @author Vlad Melanchyk
 *
 */
@Service(value="utilService")
public class UtilServiceImpl implements IUtilService {

    public boolean compareDates(Date date1, Date date2) {
	boolean comparison = false;
	if (date1 == null && date2 == null) {
	    comparison = true;
	} else if ((date1 == null && date2 != null) || (date1 != null && date2 == null)) {
	    comparison = false;
	} else if (date1.compareTo(date2) != 0) {
	    comparison = false;
	} else {
	    comparison = true;
	}
	return comparison;
    }
    
    public boolean compareStrings(String string1, String string2) {
	boolean comparison = false;
	if (string1 == null && string2 == null) {
	    comparison = true;
	} else if ((string1 == null && string2 != null) || (string1 != null && string2 == null)) {
	    comparison = false;
	} else if (string1.compareTo(string2) != 0) {
	    comparison = false;
	} else {
	    comparison = true;
	}
	return comparison;
    }

    /**
	 * get number of months beetwen two dates
	 * 
	 * @param startDate interval start date
	 * @param endDate   interval end date
	 * @return number of months
	 */
	public List<ReportMonth> getMonthsBetween(Date startDate, Date endDate) {
		List<ReportMonth> monthList = new ArrayList<ReportMonth>();
			
		Calendar calFrom = Calendar.getInstance();
		calFrom.setTime(startDate);
		Calendar calTo = Calendar.getInstance();
		calTo.setTime(endDate);        
		int monthTo = calTo.get(Calendar.MONTH) > calFrom.get(Calendar.MONTH) ? calTo.get(Calendar.MONTH) : 12;
		Calendar calStart = (Calendar)calFrom.clone();
		do {
			  ReportMonth month = new ReportMonth();
			  month.setMonthInYear(calStart.get(Calendar.MONTH));
			  month.setYear(calStart.get(Calendar.YEAR));
			  monthList.add(month);
			  calStart.add(Calendar.MONTH, 1);
			  
		} while (calStart.get(Calendar.MONTH) < monthTo && calStart.get(Calendar.YEAR) == calFrom.get(Calendar.YEAR));
		
		if (calTo.get(Calendar.YEAR) != calFrom.get(Calendar.YEAR)){
			int monthStart = 0;	
			do {
				ReportMonth month = new ReportMonth();
				month.setMonthInYear(monthStart);
				month.setYear(calTo.get(Calendar.YEAR));
				monthList.add(month);				
				monthStart += 1;
			} while (monthStart < calTo.get(Calendar.MONTH));				
		}	
        return monthList;
	}	
	
	public int getNumberOfWorkingDays(Date startDate, Date endDate) {
		int numOfWorkDays = 0;
		
		startDate = clearTimePart(startDate);
		endDate = clearTimePart(endDate);
		if (startDate == null || endDate == null || startDate.after(endDate)) {
			return 0;
		}
		
		Calendar calFrom = Calendar.getInstance();
		calFrom.setTime(startDate);
		Calendar calTo = Calendar.getInstance();
		calTo.setTime(endDate);
		calTo.add(Calendar.DATE, 1);
		
		while (calFrom.getTime().before(calTo.getTime())){
			if (calFrom.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calFrom.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				numOfWorkDays += 1;
			}
			calFrom.add(Calendar.DATE, 1);
		}
		return numOfWorkDays;
	}
    
    public float getWorkingHours(Date startDate, Date endDate, WorkingTimeCalendarDefinition resoruceWTCDefinition) {
        float workingHours = 0;
        
        startDate = clearTimePart(startDate);
        endDate = clearTimePart(endDate);
        if (startDate == null || endDate == null || startDate.after(endDate)) {
            return 0;
        }
        
        Calendar calFrom = Calendar.getInstance();
        calFrom.setTime(startDate);
        Calendar calTo = Calendar.getInstance();
        calTo.setTime(endDate);
        calTo.add(Calendar.DATE, 1);
        
        while (calFrom.getTime().before(calTo.getTime())) {
            //if wokring time caendar is defined. 
            if (resoruceWTCDefinition != null) {
                if (resoruceWTCDefinition.isWorkingDay(calFrom)) {
                    workingHours += (resoruceWTCDefinition.getWorkingHoursInDay(calFrom).toHour()).floatValue();
                }
            } else if (calFrom.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
                            && calFrom.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                // else add default work hours of default working days.
                workingHours += 8;
            }
            calFrom.add(Calendar.DATE, 1);
        }
        return workingHours;
    }
	

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IUtilService#clearTimePart(java.util.Date)
	 */
	public Date clearTimePart(Date date){
		 if(date == null) {
          throw new IllegalArgumentException("The argument 'date' cannot be null.");
        }
        // Get an instance of the Calendar.
        Calendar calendar = Calendar.getInstance();
        // Make sure the calendar will not perform automatic correction.
        calendar.setLenient(false);
        // Set the time of the calendar to the given date.
        calendar.setTime(date);
        // Remove the hours, minutes, seconds and milliseconds.
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // Return the date again.
        return calendar.getTime();
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IUtilService#calculateDate(java.util.Date, net.project.util.DateFormat)
	 */
	public String calculateDate(Date dateToCalculate, DateFormat userDateFormatter) {
		Calendar currentDate = Calendar.getInstance();
		Calendar date = Calendar.getInstance();
		date.setTime(dateToCalculate);
		date.set(Calendar.HOUR, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		
		if (date != null) {
			if (currentDate.get(Calendar.DATE) == date.get(date.DATE)
					&& currentDate.get(Calendar.MONTH) == date.get(date.MONTH)
					&& currentDate.get(Calendar.YEAR) == date.get(date.YEAR)) {
				return "Today";
			}
			
			if (currentDate.get(Calendar.DATE) - 1 == date.get(date.DATE)
					&& currentDate.get(Calendar.MONTH) == date.get(date.MONTH)
					&& currentDate.get(Calendar.YEAR) == date.get(date.YEAR)) {
				return "Yesterday";
			}
			
			if( userDateFormatter != null ){
				if (currentDate.get(Calendar.YEAR) == date.get(date.YEAR)) {
					return userDateFormatter.formatDate(date.getTime(), "MMM") + " "
							+ userDateFormatter.formatDate(date.getTime(), "dd");
				} else {
					return userDateFormatter.formatDate(date.getTime(), "MMM") + " "
							+ userDateFormatter.formatDate(date.getTime(), "dd") + ","
							+ userDateFormatter.formatDate(date.getTime(), "yyyy");
				}
			}else {
				return "";
			}
		} else {
			return "";
		}
	}
	
	/**
	 * @param startDate
	 * @param endDate
	 * @return TimeSubmitalWeek list
	 */
	public List<TimeSubmitalWeek> getWeeksBetween(Date startDateValue, Date endDateValue) {
		List<TimeSubmitalWeek> weekList = new ArrayList<TimeSubmitalWeek>();
		DateFormat format = SessionManager.getUser().getDateFormatter();
		Date startDate = null;
		Date endDate = null;
		//try {
		if(startDateValue != null) {
			Calendar calStart = Calendar.getInstance();
			calStart.setTime(startDateValue);
			calStart.add(Calendar.DATE, -1);
			startDate = DateUtils.getLocalizedDate(calStart.getTime());
			
			Calendar calEnd = Calendar.getInstance();
			calEnd.setTime(endDateValue);
			calEnd.add(Calendar.DATE, -1);
			endDate = DateUtils.getLocalizedDate(calEnd.getTime());
		}
		startDate = clearTimePart(startDate);
	    endDate = clearTimePart(endDate);
		
	    Calendar calFrom = Calendar.getInstance();
		calFrom.setTime(startDate);
		Calendar calTo = Calendar.getInstance();
		calTo.setTime(endDate);
		Calendar tempCalForMonth = Calendar.getInstance();
		tempCalForMonth.setTime(startDate);
		Calendar tempCalForYear = Calendar.getInstance();
		tempCalForYear.setTime(startDate);
		int dayIndex = 0;
		String weekFirstDay = null;
		String weekEndDay = null;
		Date dateToCheckMonth = startDate;
		weekFirstDay = new SimpleDateFormat("MMM d-").format(calFrom.getTime());
		while(calFrom.getTime().equals(calTo.getTime()) || calFrom.getTime().before(calTo.getTime())){
			TimeSubmitalWeek week = new TimeSubmitalWeek();
			tempCalForMonth.setTime(dateToCheckMonth);
			dayIndex++;
			if (calFrom.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calFrom.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				week.setEndOfweek(false);
				week.setDateValue(calFrom.getTime());
				week.setDateString(new SimpleDateFormat("E d").format(calFrom.getTime()));
				week.setWeekDays(null);
			} else {
				if(calFrom.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
					weekFirstDay = new SimpleDateFormat("MMM d-").format(calFrom.getTime());
					week.setWeekDays(null);
				}
				if(calFrom.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
					if(calFrom.get(Calendar.MONTH) == tempCalForMonth.get(Calendar.MONTH)){
						weekEndDay = new SimpleDateFormat("d").format(calFrom.getTime()); 
					} else {
						weekEndDay = new SimpleDateFormat("MMM d").format(calFrom.getTime());
						dateToCheckMonth = calFrom.getTime();
					}
					if(!DateUtils.isCurrentYear(calFrom.getTime())){
						weekEndDay += new SimpleDateFormat(", yyyy").format(calFrom.getTime());
					}
					week.setDateRangeString(weekFirstDay+""+weekEndDay);
					week.setWeekDays(dayIndex);
					week.setEndOfweek(true);
					dayIndex = 0;
				}
				week.setDateString(new SimpleDateFormat("E d").format(calFrom.getTime()));
				week.setEndOfweek(false);
			}
			if ((calFrom.get(Calendar.DATE)  == calTo.get(Calendar.DATE)
					&& calFrom.get(Calendar.MONTH) == calTo.get(Calendar.MONTH)
					&& calFrom.get(Calendar.YEAR) == calTo.get(Calendar.YEAR)) && calFrom.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
				
				if(calFrom.get(Calendar.MONTH) == tempCalForMonth.get(Calendar.MONTH)){
					weekEndDay = new SimpleDateFormat("d").format(calFrom.getTime()); 
				} else {
					weekEndDay = new SimpleDateFormat("MMM d").format(calFrom.getTime());
					dateToCheckMonth = calFrom.getTime();
				}
				if(!DateUtils.isCurrentYear(calFrom.getTime())){
					weekEndDay += new SimpleDateFormat(", yyyy").format(calFrom.getTime());
				}
				
				week.setDateRangeString(weekFirstDay+""+weekEndDay);
				week.setWeekDays(dayIndex);
				week.setEndOfweek(true);
			}
			weekList.add(week);
			if (calFrom.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calFrom.equals(calTo)){
				weekList.add(null);
			}
			calFrom.add(Calendar.DATE, 1);
		}
		return weekList;
	}
	
    /* (non-Javadoc)
     * @see net.project.hibernate.service.IUtilService#getDaysBetween(java.util.Date, java.util.Date)
     */
    public String getDaysBetween(Date dateToCheck, Date currentDate) {
    	long noOfDays = DateUtils.daysBetween(dateToCheck, currentDate);
    	if(DateUtils.isToday(dateToCheck))
    		return PropertyProvider.get("prm.global.dayslabel.today");
    	else if(DateUtils.isYesterday(dateToCheck))
    		return PropertyProvider.get("prm.global.dayslabel.yesterday");
    	long overDaysLimit = Long.parseLong(PropertyProvider.get("prm.blog.lastblogit.overdayslimit"));
    	if(noOfDays > overDaysLimit)
    		return PropertyProvider.get("prm.blog.viewblog.overdays.message", "" + overDaysLimit);
    	else
    		return PropertyProvider.get("prm.blog.viewblog.daysago.message", "" + noOfDays);
    }
}
