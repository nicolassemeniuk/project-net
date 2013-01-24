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

 package net.project.resource;

import java.util.ArrayList;
import java.util.Date;

import net.project.calendar.PnCalendar;


/**  
    Provides an interface for resource objects managed by the ResourceManager.      
*/
public interface IResource
{


     /**
        Returns the calendar for the resource.  The calendar will contain the list of specific non-working days.
        @return a PnCalendar for this resource.  
    */
    public PnCalendar getCalendar(boolean is_fiscal);


     /**
        Returns the list of time periods that this resource is typically available or working.
        @return an ArrayList of TimeCell objects for a calendar week.  
    */
    public ArrayList getAvailableTimes();


    /**
        Returns the list days that this resource in not available on a recurring weekly basis.
        @return an ArrayList of Date objects with the DAY field set to the non-working day.  
    */
    public ArrayList getNonAvailableDays();


    /**
        Returns the list dates that this resource is not available in the specified time period.
        @param start_date the inclusive date used to check for non-working dates.
        @param end_date the inclusive date usded to check for non-working dates.
        @return an ArrayList of Date objects for each non-working date.  
    */
    public ArrayList getNonAvailableDates(Date start_date, Date end_date);


}

