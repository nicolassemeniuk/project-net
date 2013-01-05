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
package net.project.view.pages.project;

import java.io.Serializable;

import net.project.calendar.ICalendarEntry;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.DateFormat;

public class CalendarWrapper implements Serializable {

    private ICalendarEntry entry;
    
    public CalendarWrapper() {
        
    }
    
    public CalendarWrapper(ICalendarEntry entry) {
        this.entry = entry;
    }
    
    public String getName() {
        return entry.getName();
    }
    
    public String getDate() {
    	String date = "";	
        User user = SessionManager.getUser();
        DateFormat userDateFormatter = user.getDateFormatter();
        date = userDateFormatter.formatDate(entry.getStartTime(),"h:mm a");
        date +=", "+userDateFormatter.formatDate(entry.getStartTime());
        date +=" "+userDateFormatter.formatDate(entry.getStartTime(),"(EEE)");
        return date;
    }
    
    public String getUrl() {
        return "/calendar/MeetingManager.jsp?id=" + this.entry.getID() + "&module=70&refLink=/calendar/Main.jsp?module=70&action=1";
    }
    
}
