/**
 * 
 */
package net.project.hibernate.service.impl;

import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import net.project.base.property.PropertyProvider;
import net.project.calendar.TimeBean;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.WorkingTimeCalendarCreateHelper;
import net.project.calendar.workingtime.WorkingTimeCalendarDateEntryHelper;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.calendar.workingtime.WorkingTimeCalendarHelper;
import net.project.calendar.workingtime.WorkingTimeCalendarListHelper;
import net.project.hibernate.service.IDateChangeHandler;
import net.project.hibernate.service.IUtilService;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;
import net.project.resource.ResourceWorkingTimeCalendarProvider;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.calc.ScheduleEntryCalculator;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.DateFormat;
import net.project.util.InvalidDateException;
import net.project.util.StringUtils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="workplanDateChangeHandler")
public class DateChangeHandlerImpl implements IDateChangeHandler{
	
	@Autowired
	private IUtilService utilService;
	
	/**
	 * @param utilService The utilService to set.
	 */
	public void setUtilService(IUtilService utilService) {
		this.utilService = utilService;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IDateChangeHandler#changeEndDate(java.lang.String, java.lang.String, net.project.schedule.Schedule)
	 */
	public String changeEndDate(String taskId, String newDateTochange, Schedule schedule, boolean makeWoringDay, HttpServletRequest request) {
		// Check parameters are not null
		if (StringUtils.isEmpty(taskId) || StringUtils.isEmpty(newDateTochange) || schedule == null) {
			throw new IllegalArgumentException("parmeters must not be be null");
		}

		ScheduleEntry scheduleEntry = schedule.getEntry(taskId);
		Date date = null;
		
		try {
			date = new DateFormat(SessionManager.getUser()).parseDateString(newDateTochange);
			if(makeWoringDay){
				makeThisDateAsWorkingDay(request, scheduleEntry, schedule, date);
			}
			ScheduleEntryCalculator calc = new ScheduleEntryCalculator(scheduleEntry, schedule.getWorkingTimeCalendarProvider());
			
			calc.endDateChanged(schedule, date, SessionManager.getUser().getTimeZone());
		} catch (InvalidDateException e) {
			// Problem parsing the end date
			return  PropertyProvider.get("prm.schedule.taskedit.error.enddate.message", newDateTochange);
		}
		
		String currentDateString = ((SessionManager.getUser()).getDateFormatter()).formatDateMedium(date);
		if(!scheduleEntry.getEndTimeStringFormatted().equalsIgnoreCase(currentDateString)){
			return "{\"dateType\":\"end\",\"editedDate\":\""+newDateTochange+"\",\"currentDate\":\""+ currentDateString +"\",\"nextDate\":\""+ scheduleEntry.getEndTimeStringFormatted()+"\",\"name\":\""+scheduleEntry.getName()+"\"}";
		}
		//EndDate change handled successfully.
		return StringUtils.EMPTY;
	}


	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IDateChangeHandler#changeStartDate(java.lang.String, java.lang.String, net.project.schedule.Schedule)
	 */
	public String changeStartDate(String taskId, String newDateTochange, Schedule schedule, boolean makeWoringDay, HttpServletRequest request) {
		//Check parameters are not null
		if (StringUtils.isEmpty(taskId) || StringUtils.isEmpty(newDateTochange) || schedule == null) {
			throw new IllegalArgumentException("parmeters must not be be null");
		}
		ScheduleEntry scheduleEntry = schedule.getEntry(taskId);
		Date date = null;
		try {
			date = new DateFormat(SessionManager.getUser()).parseDateString(newDateTochange);
			if(makeWoringDay){
				makeThisDateAsWorkingDay(request, scheduleEntry, schedule, date);
			}

			ScheduleEntryCalculator calc = new ScheduleEntryCalculator(scheduleEntry, schedule.getWorkingTimeCalendarProvider());
			calc.startDateChanged(schedule, date, SessionManager.getUser().getTimeZone());
		} catch (InvalidDateException e) {
			// Problem parsing the start date
			return  PropertyProvider.get("prm.schedule.taskedit.error.startdate.message", newDateTochange);
		}
		if(!utilService.clearTimePart(scheduleEntry.getStartTime()).equals(utilService.clearTimePart(date))){
			return "{\"dateType\":\"start\",\"editedDate\":\""+newDateTochange+"\",\"currentDate\":\""+ ((SessionManager.getUser()).getDateFormatter()).formatDateMedium(date) +"\",\"nextDate\":\""+ scheduleEntry.getEndTimeStringFormatted()+"\",\"name\":\""+scheduleEntry.getName()+"\"}";
		}
		//StartDate change handled successfully.
		return StringUtils.EMPTY;
	}
	
	/**
	 * make specified date as working in schedule/resource personal calender. 
	 * @param request
	 * @param scheduleEntry
	 * @param schedule
	 * @param date
	 */
	private void makeThisDateAsWorkingDay(HttpServletRequest request, ScheduleEntry scheduleEntry, Schedule schedule, Date date){
		//get default calendar of schedule.
		IWorkingTimeCalendarProvider provider = schedule.getWorkingTimeCalendarProvider();
		
		//If task has any assignment we need to make working day for assigned resources. 
		if(scheduleEntry.isHasAssignments()){
			for (Iterator it = scheduleEntry.getAssignmentList().iterator(); it.hasNext();) {
				ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) it.next();
				WorkingTimeCalendarDefinition def = provider.getForResourceID(assignment.getPersonID());
	            
	            //If schedule calender is not defined for resource get personal Working time calendr of resource.
	            if (def == null) {
	                try {
	                	provider = ResourceWorkingTimeCalendarProvider.make(new User(new Person(assignment.getPersonID())));
	                    def = provider.getForResourceID(assignment.getPersonID());
	                } catch (PersistenceException pnetEx) {
	                    Logger.getLogger(ScheduleEntryAssignment.class).error("Error occured while getting resource's " +
	                            "personal working time calender : "+ pnetEx.getMessage());
	                }
	            }
	            storeAsWorkingDate(request, provider, def.getID(), assignment.getTimeZone().getID(), date);
			}
		}else{
			String calendarID = provider.getDefault().getID();
			//If Calendar Id is null that means no calendar is defind here creat a clendar and set it as default.
			if (calendarID == null) {
				WorkingTimeCalendarCreateHelper helper = new WorkingTimeCalendarCreateHelper(request, provider);
				helper.setCalendarType(WorkingTimeCalendarCreateHelper.CalendarType.BASE.getID());
				helper.setName("Base Calendar");
				try {
					calendarID = helper.store();
					WorkingTimeCalendarListHelper listHelper = new WorkingTimeCalendarListHelper(request, provider);
		            listHelper.changeDefaultCalendar(calendarID);
				} catch (PersistenceException pnetEx) {
					Logger.getLogger(ScheduleEntryAssignment.class).error("Error occured while creating default schedule " +
                            "working time calender: "+ pnetEx.getMessage());
				}
			}
			
			storeAsWorkingDate(request, provider, calendarID, schedule.getTimeZone().getID(), date);
		}
	}
	
	/** 
	 * Store specified date as working day
	 * Working time will be considered as regular 8 hours.
	 *      8 am to 12 pm and 1 pm to 5 pm
	 * @param request
	 * @param provider
	 * @param calId
	 * @param timeZoneId
	 * @param date
	 */ 
	private void storeAsWorkingDate(HttpServletRequest request, IWorkingTimeCalendarProvider provider, String calId, String timeZoneId, Date date){
		//First caeate a WorkingTimeCalendarDateEntryHelper object and set all parameter which are reqired to create any date for working.
		WorkingTimeCalendarHelper helper = new WorkingTimeCalendarHelper(request, provider, calId);
		WorkingTimeCalendarDateEntryHelper dateHelper = helper.makeDateEntryHelper(request);
		
		dateHelper.setSingleDate(true);
		dateHelper.setSingleDate(date);
		dateHelper.setWorkingTimeSelected();
		//Add time slot
		dateHelper.getWorkingTimeEditHelper().setStartTime(0, TimeBean.parseTime("8", "00", "0", timeZoneId, date));
		dateHelper.getWorkingTimeEditHelper().setEndTime(0, TimeBean.parseTime("0", "00", "1", timeZoneId, date));
		dateHelper.getWorkingTimeEditHelper().setStartTime(1, TimeBean.parseTime("1", "00", "1", timeZoneId, date));
		dateHelper.getWorkingTimeEditHelper().setEndTime(1, TimeBean.parseTime("5", "00", "1", timeZoneId, date));
		try {
			dateHelper.store();
		} catch (PersistenceException pnetEx) {
			Logger.getLogger(DateChangeHandlerImpl.class).error("error occurred while storing date helper " + pnetEx.getMessage());;
		}
	}
	
	
}
