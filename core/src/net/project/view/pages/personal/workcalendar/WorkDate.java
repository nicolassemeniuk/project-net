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
import net.project.calendar.workingtime.WorkingTimeCalendarDateEntryHelper;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.calendar.workingtime.WorkingTimeCalendarHelper;
import net.project.gui.history.History;
import net.project.gui.history.HistoryException;
import net.project.gui.history.PageLevel;
import net.project.persistence.PersistenceException;
import net.project.resource.ResourceWorkingTimeCalendarProvider;
import net.project.security.SessionManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

public class WorkDate {

	private static Logger log = Logger.getLogger(WorkDate.class);;
	
	private String jSPRootURL;
	
	@Persist
	private IWorkingTimeCalendarProvider  workingTimeCalendarProvider;
	
	@Persist
	private PersonalCalendarBean personalCalendarBean;
	
	@Persist
	private List<PersonalCalendarBean> dateEntries;
	
	private PersonalCalendarBean nextDateEntry;
	
	@Persist
	private String calendarId;
	
	@Persist
	private String pageAction;
	
	@InjectPage
	private WorkDate WorkDate;
	
	@InjectPage
	private WorkDateModify WorkDateModify;

	@Inject
	private RequestGlobals requestGlobals;
	
	@Persist
	private String errorMessage;
	
	@Persist
	private Integer moduleId;
	
	private boolean datesEntryFound;
	
	private String pageTitle;
	
	
	@SetupRender
	void setValues(){
		initializePage();
		setPageTitle();
		setDatesEntryFound(dateEntries.size() > 0); 
	}
	
	private void initializePage(){
		try {
			jSPRootURL = SessionManager.getJSPRootURL();
			moduleId = Module.PERSONAL_SPACE;
			workingTimeCalendarProvider = ResourceWorkingTimeCalendarProvider.make(SessionManager.getUser());
			WorkingTimeCalendarDefinition calendarDef = workingTimeCalendarProvider.getForResourceID(SessionManager.getUser().getID());
			calendarId = calendarDef.getID();
			if (calendarId == null) {
				workingTimeCalendarProvider.create(calendarDef);
				calendarId = calendarDef.getID();
			}
			HttpServletRequest request = requestGlobals.getHTTPServletRequest();
			WorkingTimeCalendarHelper workingTimeCalendarHelper = new WorkingTimeCalendarHelper(request, workingTimeCalendarProvider, calendarId);
			
			dateEntries = new ArrayList<PersonalCalendarBean>();
			for (Iterator it = workingTimeCalendarHelper.getDateIterator(); it.hasNext();) {
				WorkingTimeCalendarHelper.DateHelper nextEntry = (WorkingTimeCalendarHelper.DateHelper) it.next();
				PersonalCalendarBean personalCalendarBean = new PersonalCalendarBean();
				personalCalendarBean.setEntryId(nextEntry.getEntryID());
				personalCalendarBean.setDateDisplay(nextEntry.getDateDisplay());
				personalCalendarBean.setWorkingDay(nextEntry.isWorkingDay());
				personalCalendarBean.setDateDescription(nextEntry.getDateDescription());
				dateEntries.add(personalCalendarBean);
			}
			
		} catch (PersistenceException pnetEx) {
			log.error("database errror while initializing personal worktime calendar in WorkModify.initializeEditPage()");
		}
	}

	
	Object onAction() {
		HttpServletRequest request = requestGlobals.getHTTPServletRequest();
		String theAction = (StringUtils.isNotEmpty(request.getParameter("action")) ? request.getParameter("action") : "");
		return processDateAction(request, theAction);
	}
	
	private Object processDateAction(HttpServletRequest request, String theAction){
		 if (theAction.equals("createDate") || theAction.equals("modifyDate")) {
	            WorkDateModify.setEntryID(request.getParameter("entryID"));
	            WorkDateModify.setPageAction(theAction);
	            return WorkDateModify;

	        } else if (theAction.equals("removeDate")) {
	            // Removing a date
	            WorkingTimeCalendarHelper helper = new WorkingTimeCalendarHelper(request, workingTimeCalendarProvider, calendarId);
	            WorkingTimeCalendarDateEntryHelper dateHelper;

	            // Removing a date; entryID required
	            String entryID = request.getParameter("entryID");
	            dateHelper = helper.makeDateEntryHelper(request, entryID);
	            try {
					dateHelper.remove();
				} catch (PersistenceException pnetEx) {
					pnetEx.printStackTrace();
				}
				WorkDate.setErrorMessage("");
				WorkDate.setPageAction(null);
				return WorkDate;
	        }
		return WorkDate;
	}
	
	public void setPageTitle() {
		History history = null; // new History();
		HttpSession session = requestGlobals.getHTTPServletRequest().getSession();

		history = (History) session.getAttribute("historyTagHistoryObject");
		if (history != null) {
			PageLevel pageLevel = new PageLevel(0);
			pageLevel.setActive(false);
			pageLevel.setDisplay("Work Date");
			pageLevel.setJspPage(jSPRootURL + "/personal/workcalendar/WorkDate?module=" + moduleId);
			// pageLevel.setQueryString("module='+moduleId"+moduleId);
			pageLevel.setShow(true);
			history.setLevel(pageLevel);

			session.setAttribute("historyTagHistoryObject", history);
			session.setAttribute("user", SessionManager.getUser());
		}
		try {
			this.pageTitle = history.getPresentation().toString();
		} catch (HistoryException pnetEx) {
			log.error("Error occured while getting page history presentation : " + pnetEx.getMessage());
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
		return jSPRootURL;
	}

	/**
	 * @param rootURL
	 *            the jSPRootURL to set
	 */
	public void setJSPRootURL(String rootURL) {
		jSPRootURL = rootURL;
	}

	/**
	 * @return the personalCalendarBean
	 */
	public PersonalCalendarBean getPersonalCalendarBean() {
		return personalCalendarBean;
	}

	/**
	 * @param personalCalendarBean the personalCalendarBean to set
	 */
	public void setPersonalCalendarBean(PersonalCalendarBean personalCalendarBean) {
		this.personalCalendarBean = personalCalendarBean;
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
	 * @return the moduleId
	 */
	public Integer getModuleId() {
		return moduleId;
	}

	/**
	 * @param moduleId the moduleId to set
	 */
	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

	/**
	 * @return the dateEntries
	 */
	public List<PersonalCalendarBean> getDateEntries() {
		return dateEntries;
	}

	/**
	 * @param dateEntries the dateEntries to set
	 */
	public void setDateEntries(List<PersonalCalendarBean> dateEntries) {
		this.dateEntries = dateEntries;
	}

	/**
	 * @return the nextDateEntry
	 */
	public PersonalCalendarBean getNextDateEntry() {
		return nextDateEntry;
	}

	/**
	 * @param nextDateEntry the nextDateEntry to set
	 */
	public void setNextDateEntry(PersonalCalendarBean nextDateEntry) {
		this.nextDateEntry = nextDateEntry;
	}
	
	/**
	 * @return the datesEntryFound
	 */
	public boolean isDatesEntryFound() {
		return datesEntryFound;
	}

	/**
	 * @param datesEntryFound the datesEntryFound to set
	 */
	public void setDatesEntryFound(boolean datesEntryFound) {
		this.datesEntryFound = datesEntryFound;
	}
	
	/**
	 * @return the pageTitle
	 */
	public String getPageTitle() {
		return pageTitle;
	}

	
	/**
	 *  Returns group heading for Work Calendar
	 */
	public String getGroupHeadingWorkCalendar() {
		return PropertyProvider.get("prm.groupheading.workcalendar.label");
	}
	
}
