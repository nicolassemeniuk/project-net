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

import net.project.calendar.Meeting;
import net.project.calendar.MyMeetings;
import net.project.security.SessionManager;
/**
 * Implementation of Meetings service
 * 
 * @author Avinash Bhamare
 * @since 8.3.0
 */
public class MyMeetingsImpl extends MyMeetings implements IMyMeetings {
	/*
	 * Returns the meetings for current user and space
	 * @see net.project.soa.calendar.IMyMeetings#getMeetings()
	 */
	public Meeting[] getMeetings() throws Exception{
		MyMeetings myMeetings = new MyMeetings(); 
	    myMeetings.setUser(SessionManager.getUser());
	    myMeetings.loadEntries();
	    Object[] arr = myMeetings.getMeetingList().toArray();
	    Meeting[] meetings = new Meeting[arr.length];
	    for(int i=0;i<arr.length;i++){
	    	meetings[i] = (Meeting)arr[i];
	    }
	    return meetings;
	}
}
