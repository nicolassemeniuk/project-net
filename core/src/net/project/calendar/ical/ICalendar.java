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
package net.project.calendar.ical;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Comment;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Priority;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.model.property.Version;
import net.project.admin.ApplicationSpace;
import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.calendar.AgendaBean;
import net.project.calendar.AttendeeBean;
import net.project.calendar.CalendarEvent;
import net.project.calendar.ICalendarEntry;
import net.project.calendar.Meeting;
import net.project.calendar.PnCalendar;
import net.project.hibernate.model.PnSpaceHasPerson;
import net.project.hibernate.model.PnSpaceHasPersonPK;
import net.project.hibernate.service.IPnSpaceHasPersonService;
import net.project.hibernate.service.ServiceFactory;
import net.project.schedule.ScheduleEntry;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.DateFormat;
import net.project.util.RandomGUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class ICalendar implements Serializable {

	private static final String PROGID = "-//Project.net www.project.net//EN";
	
	// major version of iCalendar
	private static final String ICAL_MAJOR_VERSION = "2";

	// minor version of iCalendar
	private static final String ICAL_MINOR_VERSION = "0";

	/** iCalendar content type */
    private static final String ICAL_CONTENT_TYPE = "text/x-vCalendar";
    
    private static Logger log = Logger.getLogger(ICalendar.class);

	private String _userId = null;

	private String _spaceId = null;

	String[] entryTypes= {"meeting", "event", "milestone", "task"};

	public String getUserId(){
		return _userId;
	}

	public void setUserId(String id){
		_userId = id;
	}

	public String getSpaceId(){
		return _spaceId;
	}

	public void setSpaceId(String id){
		_spaceId = id;
	}

    public String getOutput() throws Exception{
    	return getOutput(_userId, _spaceId);
    }

    public String getOutput(String userId, String spaceId) throws Exception{
    	return getOutput(userId, spaceId, entryTypes);
    }

    public String getContentType(){
		return ICAL_CONTENT_TYPE;
	}
    
    public String getOutput(String secureKey) throws Exception{
		String userId = null;
		String spaceId = null;
		try {
			userId = getUserIdBySecureKey(secureKey);
			spaceId = getSpaceIdBySecureKey(secureKey);
		} catch (Exception ex) {
			Logger.getLogger(ICalendar.class).debug(PropertyProvider.get("prm.icalendar.invalid.key.message") + ": " +  ex.toString());
		}
		if (userId == null || spaceId == null)
			return PropertyProvider.get("prm.icalendar.invalid.key.message");

		return getOutput(userId, spaceId);
    }

    public String getOutput(String userId, String spaceId, String[] entryTypes) throws Exception{
    	User user = null;
    	PnCalendar calendar = null;
    	List<ICalendarEntry> calendarEntries = null;
    	net.fortuna.ical4j.model.Calendar cal = null;
    	VEvent event = null;
    	CalendarOutputter outputter = null;
    	ByteArrayOutputStream  bos = null;

    	// userid is null : assume user already has logged in and trying to export iCal
    	if(null == userId){
    		user = net.project.security.SessionManager.getUser();
			spaceId = user.getCurrentSpace().getID();
    	}else { // else user has subscribed to iCal from ical client
    		user = new User(userId);
    		if (net.project.security.SessionManager.getUser() == null || net.project.security.SessionManager.getUser().getID() == null) {
        		net.project.security.SessionManager.setUser(user);
    		} else {
    			if (userId.equals("1")) {
    				user.setCurrentSpace(new ApplicationSpace(spaceId));
    			}    			
    		}
    	}

		//init calender details
		calendar = new PnCalendar(user);
		calendar.setSpaceId(spaceId);

		// ***** Date
		Date date = PnCalendar.currentTime(); 
		calendar.setTime(date);

		// Configure the calendar
		calendar.loadIfNeeded();
		int yearNum = calendar.get(Calendar.YEAR);
		calendar.setStartDate(calendar.startOfYear(yearNum));
		calendar.setEndDate(calendar.endOfYear(yearNum));

		calendar.loadEntries(entryTypes);

		calendarEntries = calendar.getEntries();
		ICalendarEntry iCalEntry;
		cal = new net.fortuna.ical4j.model.Calendar();
		cal.getProperties().add(new ProdId(PROGID));
		cal.getProperties().add(Version.VERSION_2_0);
		cal.getProperties().add(CalScale.GREGORIAN);

		// load all calender entries into VEvent for iCal
		for (int i = 0; i < calendarEntries.size(); i++) {
			iCalEntry = calendarEntries.get(i);

		    event = new VEvent();
			event.getProperties().add( new Categories(iCalEntry.getType()) );
			event.getProperties().add( new Summary(iCalEntry.getName()) );

			DateFormat df = new DateFormat(user);
			DtStart dtStart = new DtStart(new DateTime( df.formatDate(iCalEntry.getStartTime(), "yyyyMMdd'T'HHmmss''") ));
			event.getProperties().add( dtStart  );

			DtEnd dtEnd = new DtEnd(new DateTime(df.formatDate(iCalEntry.getEndTime(), "yyyyMMdd'T'HHmmss''") ));
			event.getProperties().add( dtEnd  );
			
			event.getProperties().add(new Uid(iCalEntry.getID()) );
			event.getProperties().add(new Description(iCalEntry.getDescription()) );
		    if(iCalEntry instanceof CalendarEvent ){
		    	CalendarEvent entry = (CalendarEvent) iCalEntry;
				event.getProperties().add(new Comment(entry.getPurpose()) );
				event.getProperties().add(new Url(new URI(SessionManager.getAppURL() + entry.getURL())) );
		    }
		    // meeting has more properties like host, attendee
		    if(iCalEntry instanceof Meeting ){
		    	Meeting entry = (Meeting) iCalEntry;
				ArrayList attendees = entry.getAttendees();
				for (int j = 0; j < attendees.size(); j++) {
			            AttendeeBean attendee = (AttendeeBean) attendees.get(j);
			            event.getProperties().add(new Attendee(attendee.getAttendeeEmail()) );
		        }
				ArrayList agendaItems = entry.getAgendaItems();
 			    for (int j = 0; j < agendaItems.size(); j++) {
			            AgendaBean agenda = (AgendaBean) agendaItems.get(j);
			            event.getProperties().add(new Action(agenda.getDescription()) );
			    }
		    }
		    // tasks have some additional properties like priority.
		    if(iCalEntry instanceof ScheduleEntry ){
		    	ScheduleEntry entry = (ScheduleEntry) iCalEntry;
				event.getProperties().add(new Url(new URI(SessionManager.getAppURL() + entry.getURL())) );
				try{event.getProperties().add(new Priority(Integer.parseInt(entry.getPriorityString())) );}catch(Exception ignore){}
				//event.getProperties().add(new Completed(entry.getWorkComplete()) );
		    }
		    cal.getComponents().add(event);
		}
		outputter = new CalendarOutputter(false);

		// write entries to writer
		bos = new ByteArrayOutputStream();
		try{
			outputter.output(cal, bos);
		}catch(Exception ex){
			throw new PnetException("Unable to generate iCal doc :", ex);
		}
		
		return bos.toString();
    }
    
    public String getSecureICalKey(String userId, String spaceID){
    	String key = getSecureICalKey(new Integer(userId), new Integer(spaceID), false);
    	return key;
    }
    
    public String getSecureICalKey(Integer userId, Integer spaceID){
    	return getSecureICalKey(userId, spaceID, false);
    }
    
    public String getSecureICalKey(Integer userId, Integer spaceID, boolean reset){
    	IPnSpaceHasPersonService pnSpaceHasPersonService = ServiceFactory.getInstance().getPnSpaceHasPersonService();
    	String secureKey = null;    	
    	try{
    		PnSpaceHasPerson pnSpaceHasPerson = pnSpaceHasPersonService.getSpaceHasPerson(new PnSpaceHasPersonPK(spaceID, userId));    	
			if( reset || StringUtils.isEmpty(pnSpaceHasPerson.getSecureKey())){
				pnSpaceHasPerson.setSecureKey(new RandomGUID(true).getRawString());
				pnSpaceHasPersonService.updateSpaceHasPerson(pnSpaceHasPerson);
			}
			secureKey = pnSpaceHasPerson.getSecureKey();
    	}catch(Exception e){
    		log.error("Error occurred while getting secure key for project member: "+ e.getMessage());
    	}
    	return secureKey;
    }
    
    public String getUserIdBySecureKey(String secureKey){
    	PnSpaceHasPerson pnSpaceHasPerson = getPnSpaceHasPersonBySecureKey(secureKey);
    	return pnSpaceHasPerson.getComp_id().getPersonId().toString();
    }

    public String getSpaceIdBySecureKey(String secureKey){
    	PnSpaceHasPerson pnSpaceHasPerson = getPnSpaceHasPersonBySecureKey(secureKey);
    	return pnSpaceHasPerson.getComp_id().getSpaceId().toString();
    }
    
    public PnSpaceHasPerson getPnSpaceHasPersonBySecureKey(String secureKey){
    	IPnSpaceHasPersonService pnSpaceHasPersonService = ServiceFactory.getInstance().getPnSpaceHasPersonService();
    	return pnSpaceHasPersonService.getPnSpaceHasPersonBySecureKey(secureKey);
    }
}
