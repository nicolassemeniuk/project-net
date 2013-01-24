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
package net.project.view.pages.personal.workcalendar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.PersonalCalendarBean;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.calendar.workingtime.WorkingTimeCalendarHelper;
import net.project.gui.history.History;
import net.project.gui.history.HistoryException;
import net.project.gui.history.PageLevel;
import net.project.persistence.PersistenceException;
import net.project.resource.ResourceWorkingTimeCalendarProvider;
import net.project.security.SessionManager;
import net.project.util.ErrorReporter;
import net.project.util.Version;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

/**
 *
 */
public class WorkWeekModify {
	private static Logger log = Logger.getLogger(WorkWeekModify.class);

	private String JSPRootURL;
    
    private String versionNumber;

	private Integer moduleId;
	
	@Inject
	private RequestGlobals requestGlobals;
	
	@Persist
	private IWorkingTimeCalendarProvider  workingTimeCalendarProvider;
	
	@Persist
	private String calendarId;
	
	@Persist
	private List<PersonalCalendarBean> dayEntries;
	
	private PersonalCalendarBean nextDayEntry;
	
	private PersonalCalendarBean.BoxDateBean boxEntry; 
	
	@Persist
	private String pageAction;
	
	@InjectPage
	private WorkDay workDay;
	
	@InjectPage
	private WorkWeekModify workWeekModify;
	
	private String presetConfirmMessage;
	
	@Persist
	private String errorMessage;
	
	@Persist
	private boolean defaultCalendar;
	
	private String pageTitle;
	
	/**
	 * @return the pageTitle
	 */
	public String getPageTitle() {
		return pageTitle;
	}

	@SetupRender
	void setValues() {
		try {
			JSPRootURL = SessionManager.getJSPRootURL();
            versionNumber = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
			moduleId = Module.PERSONAL_SPACE;
			presetConfirmMessage = PropertyProvider.get("prm.schedule.workingtime.edit.preset.message");
		} catch (Exception ex) {
			log.error("Error occured while getting property values in Profile page : " + ex.getMessage());
		}
		setPageTitle();
		if(getPageAction()== null){
			initializeEditPage();
		}
	}
	
	private void initializeEditPage(){
		try {
			workingTimeCalendarProvider = ResourceWorkingTimeCalendarProvider.make(SessionManager.getUser());
			WorkingTimeCalendarDefinition calendarDef = workingTimeCalendarProvider.getForResourceID(SessionManager.getUser().getID());
			calendarId = calendarDef.getID();
			if (calendarId == null) {
				workingTimeCalendarProvider.create(calendarDef);
				calendarId = calendarDef.getID();
			}
			HttpServletRequest request = requestGlobals.getHTTPServletRequest();
			populateDayEntry(new WorkingTimeCalendarHelper(request, workingTimeCalendarProvider, calendarId));
			
		} catch (PersistenceException pnetEx) {
			log.error("database errror while initializing personal worktime calendar in WorkModify.initializeEditPage()");
		} 
	}
	
	Object onAction() {
		HttpServletRequest request = requestGlobals.getHTTPServletRequest();
		String theAction = (StringUtils.isNotEmpty(request.getParameter("action")) ? request.getParameter("action")
				: "");
		if (theAction.equals("")) {
			workWeekModify.setErrorMessage("");
			return workWeekModify;
		}
		if (theAction.equals("submit") || theAction.equals("preset")) {
			return processDayAction(request, theAction);
		} 
		return workWeekModify;
	}
	
	private void populateDayEntry(WorkingTimeCalendarHelper workingTimeCalendarHelper){
		dayEntries = new ArrayList <PersonalCalendarBean> ();
		for (Iterator it = workingTimeCalendarHelper.getDayOfWeekIterator(); it.hasNext();) {
			WorkingTimeCalendarHelper.DayOfWeekHelper nextEntry = (WorkingTimeCalendarHelper.DayOfWeekHelper) it.next();
			
			PersonalCalendarBean personalCalendarBean = new PersonalCalendarBean();
			personalCalendarBean.setDayNumber(nextEntry.getDayNumber());
			personalCalendarBean.setDayOfWeekFormatted(nextEntry.getDayOfWeekFormatted());
			personalCalendarBean.setWorkingDay(nextEntry.isWorkingDay());
			personalCalendarBean.setWorkingTimeChecked((nextEntry.isWorkingTimeSelected() ? "checked" : ""));
			personalCalendarBean.setNonWorkingTimeChecked((nextEntry.isNonWorkingTimeSelected() ? "checked" : ""));
			personalCalendarBean.setHtmlClass((nextEntry.isWorkingDay() ? "workingDay" : "nonWorkingDay"));
			personalCalendarBean.setRowChange(!(nextEntry.getDayNumber()%2 == 0));
			
			for (int index = 0; index < 5; index++) {
				PersonalCalendarBean.BoxDateBean boxEntry = personalCalendarBean.new BoxDateBean();
				boxEntry.setStartDate(nextEntry.getWorkingTimeEditHelper().getStartTime(index));
				boxEntry.setEndDate(nextEntry.getWorkingTimeEditHelper().getEndTime(index));
				boxEntry.setDisabled(Boolean.valueOf(!nextEntry.isWorkingTimeSelected()));
				boxEntry.setStartTimeName("dayTimeStart_" + nextEntry.getDayNumber() + "_" + index);
				boxEntry.setEndTimeName("dayTimeEnd_" + nextEntry.getDayNumber() + "_" + index);
				boxEntry.setTimeBoxIndex(index);
				personalCalendarBean.getTimeBoxIndexList().add(boxEntry);
			}
			dayEntries.add(personalCalendarBean);
		} 
	}
	
	private Object processDayAction(HttpServletRequest request, String theActiont) {
		try {
			
			WorkingTimeCalendarHelper helper = new WorkingTimeCalendarHelper(request, workingTimeCalendarProvider, calendarId);
			if (theActiont.equals("submit")) {

				// Make a helper for the edited calendar
				// The helper translates presentation values into actions on the calendar definition
				// ErrorReporter errorReporter = new ErrorReporter();
				ErrorReporter errorReporter = new ErrorReporter();
				helper.processSubmission(request, errorReporter);
				// Now store the backed calendar
				if (!errorReporter.errorsFound()) {
					helper.store();
					workWeekModify.setErrorMessage("");
					workWeekModify.setPageAction(null);
					workWeekModify.setDayEntries(null);
					workDay.setDefaultCalendar(false);
					return workDay;
				} else {
					workWeekModify.setErrorMessage(errorReporter.getErrorDescriptions().toString());
					return workWeekModify;
				}

			} else if (theActiont.equals("preset")) {
				helper.processPreset(request);
				populateDayEntry(helper);
				workWeekModify.setErrorMessage("");
				workWeekModify.setPageAction("preset");
				workWeekModify.setDayEntries(dayEntries);
				return workWeekModify;
			}
		} catch (PersistenceException pnetEx) {
			log.error("database errror while mofiying or preset personal worktime calendar in WorkModify.onAction()");
		}

		return null;
	}
	
	public void setPageTitle() {
		History history = null; // new History();
		HttpSession session = requestGlobals.getHTTPServletRequest().getSession();

		history = (History) session.getAttribute("historyTagHistoryObject");
		if (history != null) {
			PageLevel pageLevel = new PageLevel(0);
			pageLevel.setActive(false);
			pageLevel.setDisplay("Work Week Edit");
			pageLevel.setJspPage(JSPRootURL + "/personal/workcalendar/WorkWeekModfy?module=" + moduleId);
			// pageLevel.setQueryString("module='+moduleId"+moduleId);
			pageLevel.setShow(true);
			history.setLevel(pageLevel);

			session.setAttribute("historyTagHistoryObject", history);
			session.setAttribute("user", SessionManager.getUser());
			try {
				this.pageTitle = history.getPresentation().toString();
			} catch (HistoryException pnetEx) {
				log.error("Error occured while getting page history presentation:"+pnetEx);
			}
		}
	}
	
	@CleanupRender
	void clearValue(){
		setErrorMessage("");
	}
	
	/**
	 * @return the jSPRootURL
	 */
	public String getJSPRootURL() {
		return JSPRootURL;
	}

	/**
	 * @return the moduleId
	 */
	public Integer getModuleId() {
		return moduleId;
	}

	/**
	 * @return the dayEntries
	 */
	public List<PersonalCalendarBean> getDayEntries() {
		return dayEntries;
	}

	/**
	 * @param dayEntries the dayEntries to set
	 */
	public void setDayEntries(List<PersonalCalendarBean> dayEntries) {
		this.dayEntries = dayEntries;
	}

	/**
	 * @return the nextDayEntry
	 */
	public PersonalCalendarBean getNextDayEntry() {
		return nextDayEntry;
	}

	/**
	 * @param nextDayEntry the nextDayEntry to set
	 */
	public void setNextDayEntry(PersonalCalendarBean nextDayEntry) {
		this.nextDayEntry = nextDayEntry;
	}

	/**
	 * @return the boxEntry
	 */
	public PersonalCalendarBean.BoxDateBean getBoxEntry() {
		return boxEntry;
	}

	/**
	 * @param boxEntry the boxEntry to set
	 */
	public void setBoxEntry(PersonalCalendarBean.BoxDateBean boxEntry) {
		this.boxEntry = boxEntry;
	}

	/**
	 * @return the pageAction
	 */
	public String getPageAction() {
		return pageAction;
	}

	/**
	 * @param pageAction the pageAction to set
	 */
	public void setPageAction(String pageAction) {
		this.pageAction = pageAction;
	}

	/**
	 * @return the presetConfirmMessage
	 */
	public String getPresetConfirmMessage() {
		return presetConfirmMessage;
	}

	/**
	 * @param presetConfirmMessage the presetConfirmMessage to set
	 */
	public void setPresetConfirmMessage(String presetConfirmMessage) {
		this.presetConfirmMessage = presetConfirmMessage;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the defaultCalendar
	 */
	public boolean isDefaultCalendar() {
		return defaultCalendar;
	}

	/**
	 * @param defaultCalendar the defaultCalendar to set
	 */
	public void setDefaultCalendar(boolean defaultCalendar) {
		this.defaultCalendar = defaultCalendar;
	}

    /**
     * @return the versionNumber
     */
    public String getVersionNumber() {
        return versionNumber;
    }
    
    /**
	 *  Returns group heading for Work Calendar
	 */
	public String getGroupHeadingWorkCalendar() {
		return PropertyProvider.get("prm.groupheading.workcalendar.label");
	}
}
