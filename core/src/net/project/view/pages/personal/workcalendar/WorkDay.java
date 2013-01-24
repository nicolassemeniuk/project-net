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
import net.project.gui.history.ModuleLevel;
import net.project.persistence.PersistenceException;
import net.project.resource.ResourceWorkingTimeCalendarProvider;
import net.project.security.SessionManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

/**
 *
 */
public class WorkDay {
	private static Logger log = Logger.getLogger(WorkDay.class);

	private String JSPRootURL;

	private Integer moduleId;
	
	@Inject
	private RequestGlobals requestGlobals;
	
	@InjectPage
	private WorkDay workDay;
	
	@InjectPage
	private WorkWeekModify workWeekModify;
	
	@Persist
	private WorkingTimeCalendarHelper workingTimeCalendarHelper;
	
	private List<PersonalCalendarBean> dayEntries;
	
	private PersonalCalendarBean nextDayEntry;
	
	@Persist
	private boolean defaultCalendar;
	
	private String pageTitle;
	
	@SetupRender
	void setValues() {
		try {
			JSPRootURL = SessionManager.getJSPRootURL();
			moduleId = Module.PERSONAL_SPACE;
		} catch (Exception ex) {
			log.error("Error occured while getting property values in Profile page : " + ex.getMessage());
		}
		setPageTitle();
		initializeWorkDay();
	}
	
	private void initializeWorkDay(){
		try {
			IWorkingTimeCalendarProvider workingTimeCalendarProvider = ResourceWorkingTimeCalendarProvider.make(SessionManager.getUser());
			
			WorkingTimeCalendarDefinition calendarDef = workingTimeCalendarProvider.getForResourceID(SessionManager.getUser().getID());
			
			if (calendarDef.getID() == null) {
				workingTimeCalendarProvider.create(calendarDef);
				setDefaultCalendar(true);
			}

			HttpServletRequest request = requestGlobals.getHTTPServletRequest();
			
			workingTimeCalendarHelper = new WorkingTimeCalendarHelper(request, workingTimeCalendarProvider, calendarDef.getID());
			dayEntries = new ArrayList <PersonalCalendarBean> ();

			for (Iterator it = workingTimeCalendarHelper.getDayOfWeekIterator(); it.hasNext();) {
				WorkingTimeCalendarHelper.DayOfWeekHelper nextEntry = (WorkingTimeCalendarHelper.DayOfWeekHelper) it.next();
				PersonalCalendarBean personalCalendarBean = new PersonalCalendarBean();
				personalCalendarBean.setDayOfWeekFormatted(nextEntry.getDayOfWeekFormatted());
				personalCalendarBean.setWorkingDay(nextEntry.isWorkingDay());
				dayEntries.add(personalCalendarBean);
			}
			
		} catch (PersistenceException pnetEx) {
			log.error("database errror while creating and getting value of personal worktime calendar in WorkDay.initializeWorkDay()"+pnetEx.getMessage());
		} 
	}
	
	Object onAction() {
		HttpServletRequest request = requestGlobals.getHTTPServletRequest();
		String theAction = (StringUtils.isNotEmpty(request.getParameter("action")) ? request.getParameter("action"): "");
		
		//using preset standard for use default Calendar because default calendar is equivalent to standard preset.
		if (theAction.equals("preset")) {
			workingTimeCalendarHelper.processPreset(request);
			try {
				workingTimeCalendarHelper.store();
			} catch (PersistenceException pnetEx) {
				log.error("database errror while preset and saving worktime calendar in WorkDay.onAction()");
			}
			workDay.setDefaultCalendar(true);
			return workDay;
		} else if (theAction.equals("modify")){ 
			workWeekModify.setDefaultCalendar(isDefaultCalendar());
			return workWeekModify;
		}
		return workDay;
	}
	
	/**
	 * @param pageTitle the pageTitle to set
	 */
	public void setPageTitle() {
		History history = null; // new History();
		HttpSession session = requestGlobals.getHTTPServletRequest().getSession();

		history = (History) session.getAttribute("historyTagHistoryObject");
		if (history != null) {
			ModuleLevel moduleLevel = new ModuleLevel();
			moduleLevel.setActive(false);
			moduleLevel.setDisplay("Work Week");
			moduleLevel.setJspPage(JSPRootURL + "/personal/workcalendar/WorkDate?module=" + moduleId);
			moduleLevel.setShow(true);
			history.setLevel(moduleLevel);

			session.setAttribute("historyTagHistoryObject", history);
			session.setAttribute("user", SessionManager.getUser());
			try {
				this.pageTitle = history.getPresentation().toString();
			} catch (HistoryException pnetEx) {
				log.error("Error occured while getting page history presentation:"+pnetEx);
			}
		}
	}
	
	/**
	 * @return the pageTitle
	 */
	public String getPageTitle() {
		return pageTitle;
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
	 *  Returns group heading for Work Calendar
	 */
	public String getGroupHeadingWorkCalendar() {
		return PropertyProvider.get("prm.groupheading.workcalendar.label");
	}
}
