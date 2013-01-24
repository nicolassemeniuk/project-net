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
package net.project.view.pages.business.report;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.gui.html.HTMLOptionList;
import net.project.gui.html.IHTMLOption;
import net.project.hibernate.model.PnPerson;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.view.pages.base.BasePage;
import net.project.view.pages.resource.management.GenericSelectModel;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;

/**
 * Class to retrieve time summary data
 */
public class TimeSummary extends BasePage{
	
	@Property
	private String businessName;
	
	@Property
	private boolean actionsIconEnabled;
	
	@Property
	private DateFormat userDateFormat;
	
	@Property
	private ArrayList<String> MONTHS;
	
	@Property
	private ArrayList<String> YEARS;
	
	@Property
	private String monthList;
	
	@Property
	private String yearList;
	
	@Property
	private String monthValue;
	
	@Property 
	private Integer currentYear;
	
	@Property 
	private Integer currentMonth;
	
	@Property
	private Integer moduleId;
	
	private GenericSelectModel<PnPerson> teamBeans;
	
	@Inject
	private PropertyAccess access;
	
	@Property
	private PnPerson person;
	
	@Property
	private boolean isSpaceAdmin;
	
	private String optionList;
	
	@Property
	private String spaceAdminId;
	
	@Property
	private String dateFormat;
	
	@Property
	private String startDateString;
	
	@Property
	private String endDateString;
	
	@Property
	private int timeSummaryDivWidth; 
	
	@Property
	private int assignmentDataDivWidth;
	
	@Property
	private int assignmentDataDivHeight;
	
	@Property
	private int summaryHeaderFooterWidth;
	
	@Property
	private int summaryHeaderFooter_DivWidth;
	
	@Property
	private String blogitTitle;
	
	@Property
	private String exportToExcelTitle;
	
	@Property
	private String exportCSVTitle;
	
	@Property
	private String viewButtonLabel;
	
    /**
     * Initialize the data
     */
    void initialize(){
    	moduleId = Module.BUSINESS_SPACE;
    	userDateFormat = getUser().getDateFormatter();
    	businessName = getUser().getCurrentSpace().getName();
    	actionsIconEnabled = PropertyProvider.getBoolean("prm.global.actions.icon.isenabled");
    	blogitTitle = PropertyProvider.get("all.global.toolbar.standard.blogit");
    	exportToExcelTitle = PropertyProvider.get("prm.resource.timesheet.exporttoexcel.link");
    	exportCSVTitle = PropertyProvider.get("prm.business.timesubmital.report.exportcsv.link");
    	MONTHS = new ArrayList<String>();
    	YEARS = new ArrayList<String>();
    	SetMonthAndYearList();
    	isSpaceAdmin = getUser().isSpaceAdministrator();
    	getOptionList();
    	spaceAdminId = getUser().getID();
    	dateFormat = getUser().getDateFormatter().getInstance().getDateFormatExample();
    	initializeDateString();
    	timeSummaryDivWidth = getWindowWidth() - 217;
    	assignmentDataDivWidth = getWindowWidth() - 228;
    	assignmentDataDivHeight = getWindowWidth() - 335; 
    	summaryHeaderFooterWidth = getWindowWidth() -  228;
    	summaryHeaderFooter_DivWidth = getWindowWidth() - 241;
    	viewButtonLabel = PropertyProvider.get("prm.business.timesubmital.report.viewbutton.label");
    }
    
    Object onActivate(){
    	// security access check
    	URL url = null;
    	if(!isAccessAllowed(getUser().getCurrentSpace().getID(), Module.TIME_SUMMARY_REPORT, Action.VIEW)){
    		// Failed Security Check
            try {
            	getRequest().setAttribute("exception", new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.project.main.authorizationfailed.message"), getUser().getCurrentSpace()));
            	url = new URL(SessionManager.getAppURL() + "/AccessDenied.jsp");
            	return url;
            } catch (Exception ex) {
                Logger.getLogger(TimeSummary.class).error("Error occurred while handling security for time summary: " + ex.getMessage());
            }
    	}
    	initialize();
    	return null;
	}
    
    /**
     * Set the month list and year list  
     */
    void SetMonthAndYearList(){
	    PnCalendar monthCal = new PnCalendar(getUser());
	    currentYear = monthCal.get(Calendar.YEAR);
	    monthValue = userDateFormat.formatDate(monthCal.getTime(), "MMMM");
	    monthCal.set(Calendar.MONTH, Calendar.JANUARY);
	    for (int i=0; i<12; i++) {
	    	MONTHS.add(userDateFormat.formatDate(monthCal.getTime(), "MMMM"));
	        monthCal.roll(Calendar.MONTH, 1);
	    }
	    
	    PnCalendar yearCal = new PnCalendar(getUser());
	    yearCal.roll(Calendar.YEAR, -10);
	    for (int i=0; i<21; i++) {
	        YEARS.add(userDateFormat.formatDate(yearCal.getTime(), "yyyy"));
	        yearCal.roll(Calendar.YEAR, 1);
	    }
    }
    
    public GenericSelectModel<PnPerson> getTeamModel() {
    	return teamBeans;
    }

	/**
	 * @return the optionList
	 */
	public String getOptionList() {
		List personList = new ArrayList<PersonOptions>();
		List<PnPerson> persons = getPnPersonService().getPersonsByProjectId(Integer.parseInt(SessionManager.getUser().getCurrentSpace().getID()));
		if (persons != null && persons.size() > 0) {
			for(PnPerson person:  persons){
				personList.add(new PersonOptions(person.getPersonId(), person.getDisplayName()));
			}
		}
		return HTMLOptionList.makeHtmlOptionList(personList);
	}
	
	// Inner class for create the option list for team members
	public class PersonOptions implements Serializable, IHTMLOption {
		Integer personId;
		String personName;
		
		PersonOptions(Integer personId, String personName){
			this.personId = personId;
			this.personName = personName;
		}
		
	    public String getHtmlOptionValue() {
	        return this.personId.toString();
	    }

	    public String getHtmlOptionDisplay() {
	    	return this.personName;
	    }
	}
	
	private void initializeDateString(){
		Date convertedDate = PnCalendar.currentTime();
		PnCalendar tempCal = new PnCalendar();
		tempCal.setTime(convertedDate);
		
		startDateString = userDateFormat.formatDate(tempCal.getPrevMonth());
		endDateString =  userDateFormat.formatDate(convertedDate);
		
	}
}
