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
package net.project.view.pages.assignments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.calendar.workingtime.DefinitionBasedWorkingTimeCalendar;
import net.project.calendar.workingtime.IWorkingTimeCalendar;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.chargecode.ChargeCodeManager;
import net.project.form.assignment.FormAssignment;
import net.project.hibernate.model.PnChargeCode;
import net.project.resource.ActivityAssignment;
import net.project.resource.Assignment;
import net.project.resource.AssignmentWorkCaptureHelper;
import net.project.resource.ResourceWorkingTimeCalendarProvider;
import net.project.resource.ScheduleEntryAssignment;
import net.project.resource.AssignmentWorkCaptureHelper.DateHeader;
import net.project.resource.AssignmentWorkCaptureHelper.DateLongName;
import net.project.resource.mvc.handler.AssignmentDate;
import net.project.schedule.Schedule;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.HTMLUtils;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.view.pages.base.BasePage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 *
 */
public class Timesheet extends BasePage {

	private static Logger log = Logger.getLogger(Timesheet.class);

	private DateFormat userDateFormat;

	@Property
	private String date;

	@Property
	private List<String> hoursModel;

	@Property
	private List<String> minutesModel;

	@Property
	private String fromHours;

	@Property
	private String toHours;

	@Property
	private String fromMinutes;

	@Property
	private String toMinutes;

    private AssignmentWorkCaptureHelper assignmentWorkCaptureHelper;
    
    @Property
    private String scrollType;
    
    @Property
    private Boolean scrollTypeIsWeek;
    
    @Property
    private String strTDwidth;
    
    @Property
    private String strPercentComplete;
    
    @Property
    private String strWork = TimeQuantity.O_HOURS.toShortString(0, 2);
    
    @Property
    private String strWorkComplete = TimeQuantity.O_HOURS.toShortString(0, 2);
    
    @Property
    private String strWorkRemaining = TimeQuantity.O_HOURS.toShortString(0, 2);
    
    @Property
    private Long scrollBackStartDate;

    @Property
    private Long scrollForwardStartDate;
    
    @Property
    private List dateHeaders;
    
    @Property
    private DateHeader dateHeader;
    
    @Property
    private List dateLongNames;
    
    @Property
    private List<DateLongName> dateLongNamesList;
    
    @Property
    private DateLongName dateLongName;
    
    @Property
    private String assignmentObjectId;

    @Property
	private int moduleId;
	
	@Property
	private String timesValueArray;

	@Property
	private String invalidErrorKey;
	
	@Property
	private boolean assignmentFound;
	
	private int todayToHighlightCount = 0;
	
	private boolean isSetTodayToHighlightCount;
	
	private ResourceWorkingTimeCalendarProvider calendarProvider;
	
	private WorkingTimeCalendarDefinition def;
    
	@Property
    private String currentUserName;
    
    @Property
    private String workCapturedInfoForTask;
    
    @Property
    private String workNotCapturedInfoForTask;
    
    @Property
    private String workCapturedInfoForAllassignments;
    
    @Property
    private String workNotCapturedInfoForAllAssignments;
    
    @Property
    private String decimalSeparator;

    @Property
	private String estimatedTotWorkErrorMessage;

    @Property
	private String workHoursLessThanZeroErrorMessage;
    
    @Property
	private boolean userAssigned;
    
    @Property
    private List<PnChargeCode> chargeCodeList;
    
    @Property
    private PnChargeCode chargeCode;

    public void onActivate() {
		if (net.project.security.SessionManager.getUser() == null) {
			throw new IllegalStateException("User is null");
		}
        workCapturedInfoForTask = PropertyProvider.get("prm.blog.timesheet.workcapturedinfofortask.message");
        workNotCapturedInfoForTask = PropertyProvider.get("prm.blog.timesheet.worknotcapturedinfofortask.message");
        workCapturedInfoForAllassignments = PropertyProvider.get("prm.blog.timesheet.workcapturedinfoforallassignments.message");
        workNotCapturedInfoForAllAssignments = PropertyProvider.get("prm.blog.timesheet.worknotcapturedinfoforallassignments.message");
        estimatedTotWorkErrorMessage = PropertyProvider.get("prm.blog.timesheet.estimatedtotalwork.error.message");
        workHoursLessThanZeroErrorMessage = PropertyProvider.get("prm.blog.timesheet.workhourslessthanzero.error.message");
        
		moduleId = Module.PERSONAL_SPACE;
        currentUserName = HTMLUtils.escape(getUser().getDisplayName().replaceAll("'", "&acute;"));

		userDateFormat = SessionManager.getUser().getDateFormatter();
		date = userDateFormat.formatDate(Calendar.getInstance().getTime(), "EEEEE, MMM dd, yyyy");

		String objectId = getHttpServletRequest().getParameter("objectId");
		scrollType = StringUtils.isEmpty(getHttpServletRequest().getParameter("scrollType")) ? "week" : getHttpServletRequest().getParameter("scrollType");
		
		List assignments = null;
		if(StringUtils.isNotEmpty(objectId)){
			assignmentWorkCaptureHelper = new AssignmentWorkCaptureHelper();
			// Get the assignments that we are displaying
			try {
				assignments = assignmentWorkCaptureHelper.getAssignments(objectId);
			} catch (Exception e) {
				log.error("Error occured while getting assignments:" + e.getMessage());
			}
			if(org.apache.commons.collections.CollectionUtils.isNotEmpty(assignments)){
				boolean dummayAssignment = false;
				Schedule schedule = (Schedule) getSession().getAttribute("schedule");
				boolean unAssignedWorkCapture = schedule != null ? schedule.isUnAssignedWorkcapture() : false;
				
				if(assignments.size() == 1){
					Assignment ass = (Assignment) assignments.get(0);
					dummayAssignment = ass.getAssignorID() == null;
				}
				if(dummayAssignment && !unAssignedWorkCapture){
					assignmentFound = false;
				}else{
					getModifiedTimeSheetEntries(objectId, assignments, getHttpServletRequest());
					assignmentFound = true;
				}
			}
		}
		decimalSeparator = "" + NumberFormat.getInstance().getDecimalSeparator();
		userAssigned = isUserHasAssignment(objectId);
		
		Integer spaceId = Integer.valueOf(getUser().getCurrentSpace().getID());
		chargeCodeList = getPnChargeCodeService().getChargeCodeByProjectId(spaceId);
		chargeCode = getPnChargeCodeService().getChargeCodeApliedOnTask(Integer.valueOf(objectId), spaceId);
		if(chargeCode == null)
			chargeCode = getPnChargeCodeService().getChargeCodeAppliedOnPersonInSpace(Integer.valueOf(SessionManager.getUser().getID()), spaceId);
	}

	/**
	 * Get the Saved/Non Saved timeWorkedDiv for blogit.js for the first time only.
	 * @param request a <code>HttpServletRequest</code> object that we can use
     * to look up what time the user has entered.
     * The date value list has been set according to this paramaeter and pass it to
     * getTimeValueForJS() method to set the date value list.
     * and after that in session.
	 */
    public void getModifiedTimeSheetEntries(String objectId, List assignments, HttpServletRequest request) {
        String dateToStart = request.getParameter("scroll");
        scrollTypeIsWeek = scrollType.equalsIgnoreCase("week");    

        // Get/Set Date values List while scrolling and first Time Laod.
        timesValueArray = assignmentWorkCaptureHelper.getTimeValueForJS(dateToStart, scrollType, objectId);
        invalidErrorKey = PropertyProvider.get("prm.resource.assignments.update.error.invalidwork.message", "0", "24");
        
        strTDwidth = "50px";
        scrollBackStartDate = assignmentWorkCaptureHelper.scrollBackStartDate.getTime();
        scrollForwardStartDate = assignmentWorkCaptureHelper.scrollForwardStartDate.getTime();
        
        dateHeaders = assignmentWorkCaptureHelper.dateHeaders;      
        String personId = SessionManager.getUser().getID();
        assignmentWorkCaptureHelper.planIDMap = assignmentWorkCaptureHelper.getPlanIDMap(personId, assignmentWorkCaptureHelper.assignmentMap);
        try{
            calendarProvider = (ResourceWorkingTimeCalendarProvider) ResourceWorkingTimeCalendarProvider.make(SessionManager.getUser());
            def = calendarProvider
					.getForPlanID((String) assignmentWorkCaptureHelper.planIDMap
							.get(objectId));
        }catch (Exception e) {
        	log.error("Error occured while getting calendarProvider object for user" + e.getMessage());
		}
        for (DateHeader dateHeader : (List<DateHeader>)dateHeaders) {
            if(dateHeader.date.equalsIgnoreCase("Today")) {
            	dateHeader.setIsToday(dateHeader.date.equalsIgnoreCase("Today"));
            } else {            	
            	try {
            		String newDate = userDateFormat.formatDate(Calendar.getInstance().getTime(), "MMM dd");
            		if (!isSetTodayToHighlightCount)// to highlight the text box of current date.
            			todayToHighlightCount++;
            		if (newDate.equals(dateHeader.date)) {
            			dateHeader.setIsTodaysDayInWeek(true);
            			isSetTodayToHighlightCount = true;
            		} else {
            		    dateHeader.setIsTodaysDayInWeek(false);
            		}
            		// To set the non working day flags of user to highlight
            		try{
    	                Date date = new Date(Long.parseLong(dateHeader.getDateHeaderLongname()));
    	                IWorkingTimeCalendar cal = new DefinitionBasedWorkingTimeCalendar(calendarProvider.getDefaultTimeZone(), def);
    	                if(!cal.isWorkingDay(date)){
    	                	dateHeader.setIsNonWorkingDay(true); 
    	                }
                    }catch(Exception e){
                    	log.error("Error occurred while setting non working day flag to highlight" + e.getMessage());
                    }
				} catch (Exception e) {
					log.error("Error occurred while parsing date :" + e.getMessage());
				}
				
            }
        }
        if(!isSetTodayToHighlightCount){
        	todayToHighlightCount = 0;
        }
        isSetTodayToHighlightCount = false;
        double percentComplete = 1.0;
        int forcount = 0;
        
        for (Assignment assignment : (List<Assignment>)assignments) {
            assignmentObjectId = assignment.getObjectID();
            
            if (assignment instanceof ScheduleEntryAssignment) {
                ScheduleEntryAssignment seAssignment = (ScheduleEntryAssignment) assignment;
                percentComplete = seAssignment.getPercentComplete().doubleValue();
                strWork = seAssignment.getWork().toShortString(0, 2);
                strWorkComplete = seAssignment.getWorkComplete().toShortString(0, 2);
                strWorkRemaining = seAssignment.getWorkRemaining().toShortString(0, 2);

            } else if (assignment instanceof ActivityAssignment) {
                ActivityAssignment aAssignment = (ActivityAssignment) assignment;
                strWork = aAssignment.getWork().toShortString(0, 2);
                strWorkComplete = aAssignment.getWork().toShortString(0, 2);

            } else if (assignment instanceof FormAssignment) {
                FormAssignment fAssignment = (FormAssignment) assignment;
                percentComplete = fAssignment.getPercentComplete().doubleValue();
                strWork = fAssignment.getWork().toShortString(0, 2);
                strWorkComplete = fAssignment.getWorkComplete().toShortString(0, 2);
                strWorkRemaining = fAssignment.getWorkRemaining().toShortString(0, 2);
            }
            
            strWork = assignmentWorkCaptureHelper.convertHoursToDays(strWork);	            	
        	strWorkRemaining = assignmentWorkCaptureHelper.convertHoursToDays(strWorkRemaining);
        	dateLongNames = assignmentWorkCaptureHelper.dateLongNames;
        	
        	dateLongNamesList = new ArrayList<DateLongName>();
        	
            for (String longName : (List<String>)dateLongNames) {
            	DateLongName dateLongName =  new DateLongName(); 
                
            	dateLongName.name = longName;
                
                TimeQuantity work = (TimeQuantity) assignmentWorkCaptureHelper.dateValues.get(new AssignmentDate(new Date(Long.parseLong(longName)), assignment.getObjectID()));
                NumberFormat nf = NumberFormat.getInstance();
                work = (work == null ? new TimeQuantity(0, TimeQuantityUnit.HOUR) : work);
                dateLongName.work = nf.formatNumber(work.getAmount().doubleValue(), 0, 2).equals("0") ? 
                						"" :  nf.formatNumber(work.getAmount().doubleValue(), 0, 2) ;
                
                String valueName = "dateupdX" + assignment.getObjectID() + "X" + longName;
                dateLongName.valueName = valueName;
                
                String valueValue = (StringUtils.isEmpty(request.getParameter(valueName)) ? "" : request.getParameter(valueName));
                dateLongName.valueValue = valueValue;

                dateLongName.forcount = forcount++;
                if(todayToHighlightCount == forcount){
                	dateLongName.setIsTodaysDayInWeek(true);
                	//dateLongName.setIsToday(true);
                }
                // To set the non working day flags of user to highlight
                try{
	               Date date = new Date(Long.parseLong(longName));
	                IWorkingTimeCalendar cal = new DefinitionBasedWorkingTimeCalendar(calendarProvider.getDefaultTimeZone(), def);
	                if(!cal.isWorkingDay(date)){
	                	dateLongName.setIsNonWorkingDay(true); 
	                }
                    dateLongName.date = userDateFormat.formatDate(date);
	                //To get total work done by user on particular day 
	                TimeQuantity oldWorkForDay = (TimeQuantity)assignmentWorkCaptureHelper.summaryDateValues.get(date);
	                oldWorkForDay = (oldWorkForDay == null ? new TimeQuantity(0, TimeQuantityUnit.HOUR) : oldWorkForDay);
	    	        dateLongName.setOldWorkForDay(nf.formatNumber(oldWorkForDay.getAmount().doubleValue(), 0, 2));	
                }catch(Exception e){
                	log.error("Error occured while setting non working day flag to highlight" + e.getMessage());
                }
                dateLongNamesList.add(dateLongName);
            }
        }
        
        strPercentComplete = NumberFormat.getInstance().formatPercent(percentComplete, 2);
        // Put some date objects into the session that we'll use repeatedly
        try {
        	assignmentWorkCaptureHelper.putObjectsInSession(request, request.getSession());
        } catch (Exception e) {
            log.error("Error occurred while putting objects in session:" + e.getMessage());
        }        
    }
    
    /**
     * User has assignement for spacified object. 
     * @param objectId
     * @return boolean
     */
    private boolean isUserHasAssignment(String objectId) {
		if (objectId == null)//if object id is null return false.
			return false;
		
		return getPnAssignmentService().getPersonAssignmentForObject(Integer.valueOf(objectId),
				Integer.valueOf(getUser().getID())) != null;
	}
    
    /**
     * To Check weather charge codes available for owning project of this assignment
     * @return boolean
     */
    public boolean isChargeCodeAvailable() {
    	return CollectionUtils.isNotEmpty(chargeCodeList);	
    }
    
    /**
     * To check weather charge code is assigned to this assignment.
     * @return boolean
     */
    public boolean isChargeCodeAssigned() {
    	return chargeCode != null;
    }
    
	/**
     * To check whether charge code is enabled.
     * @return boolean
     */
	public boolean isChargeCodeEnabled() {
		return PropertyProvider.getBoolean("prm.global.business.managechargecode.isenabled");
	}
}
