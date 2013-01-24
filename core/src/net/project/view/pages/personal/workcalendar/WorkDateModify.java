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

public class WorkDateModify {

	private static Logger log = Logger.getLogger(WorkDateModify.class);;
	
	@Persist
	private String jSPRootURL;
    
	@Persist
    private String versionNumber;
	
	@Persist
	private IWorkingTimeCalendarProvider  workingTimeCalendarProvider;
	
	@Persist
	private WorkingTimeCalendarDateEntryHelper dateHelper;
	
	@Persist
	private PersonalCalendarBean personalCalendarBean;
	
	private PersonalCalendarBean.BoxDateBean boxEntry; 
	
	@Persist
	private String calendarID;
	
	@Persist
	private String pageAction;
	
	@InjectPage
	private WorkDateModify workDateModify;
	
	@InjectPage
	private WorkDate workDate;

	@Inject
	private RequestGlobals requestGlobals;
	
	@Persist
	private String errorMessage;
	
	@Persist
	private String entryID;
	
	@Persist
	private Integer moduleId;
	
	private String pageTitle; 

	@Persist
	private String errorAlertTitle;
	
	@Persist
	private String errorSingleDate;
	
	@Persist
	private String dateFormatPattern;
	
	@Persist
	private String errorDateRange;
	
	@SetupRender
	void setValues(){
		if(pageAction != null){
			initializePage();
		}
		setPageTitle();
	}
	
	private void initializePage(){
		try {
			errorAlertTitle = PropertyProvider.get("prm.resource.global.exterroralert.title");
			errorSingleDate = PropertyProvider.get("prm.schedule.workingtime.editdate.singledate.invaliddate.message");
			errorDateRange = PropertyProvider.get("prm.schedule.workingtime.editdate.rangedate.invaliddate.message");
		}catch (Exception ex) {
			log.error("Error occured while getting property values in work date modify page : "+ex.getMessage());			
		}
		
		dateFormatPattern = SessionManager.getUser().getDateFormatter().getDateFormatExample();
		
		try {
			jSPRootURL = SessionManager.getJSPRootURL();
		        versionNumber = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
			moduleId = Module.PERSONAL_SPACE;
			workingTimeCalendarProvider = ResourceWorkingTimeCalendarProvider.make(SessionManager.getUser());
			WorkingTimeCalendarDefinition calendarDef = workingTimeCalendarProvider.getForResourceID(SessionManager
					.getUser().getID());
			calendarID = calendarDef.getID();
			if (calendarID == null) {
				workingTimeCalendarProvider.create(calendarDef);
				calendarID = calendarDef.getID();
			}
			HttpServletRequest request = requestGlobals.getHTTPServletRequest();
			WorkingTimeCalendarHelper helper = new WorkingTimeCalendarHelper(request, workingTimeCalendarProvider,	calendarID);
			
			if (pageAction.equals("createDate")) {
                // Creating a date; no entryID required
                dateHelper = helper.makeDateEntryHelper(request);
                populateDateWorkingTime();

            } else if (pageAction.equals("modifyDate")){
                // Editing a date; entryID required
                dateHelper = helper.makeDateEntryHelper(request, entryID);
                populateDateWorkingTime();
            }
		} catch (PersistenceException pnetEx) {
			log.error("Error occured while getting page history presentation:"+pnetEx);
		}
	}

	
	Object onAction() {
		HttpServletRequest request = requestGlobals.getHTTPServletRequest();
		String theAction = (StringUtils.isNotEmpty(request.getParameter("action")) ? request.getParameter("action") : "");
		
		if (theAction.equals("")) {
			workDateModify.setErrorMessage("");
			return workDate;
		}
		
		if (theAction.equals("submit") || (theAction.equals("add"))) {

			WorkingTimeCalendarHelper helper = new WorkingTimeCalendarHelper(request, workingTimeCalendarProvider, calendarID);
			dateHelper = helper.makeDateEntryHelper(request);
			
			ErrorReporter errorReporter = new ErrorReporter();
			dateHelper.processSubmission(request, errorReporter);
			if (errorReporter.errorsFound()) {
				workDateModify.setPageAction(null);
				workDateModify.setDateHelper(dateHelper);
				workDateModify.setPersonalCalendarBean(personalCalendarBean);
				workDateModify.setErrorMessage(errorReporter.getErrorDescriptions().toString());
				return workDateModify;
			} else {
				// Successfully processed the post
				try {
					dateHelper.store();
				} catch (PersistenceException pnetEx) {
					log.error("database errror while storing  date WorkDateModify.onAction()");
				}

				// Now navigate depending on whether the user chose to
				// submit or add another
				if (theAction.equals("submit")) {
					workDate.setErrorMessage("");
					workDate.setPageAction(null);
					return workDate;
					// Go back to Edit calendar view
				} else {
					// Add another
					dateHelper = helper.makeDateEntryHelper(request);
					populateDateWorkingTime();
					
					workDateModify.setPageAction("add");
					workDateModify.setDateHelper(dateHelper);
					workDateModify.setPersonalCalendarBean(personalCalendarBean);
					return workDateModify;
				}
			}
		}else if (theAction.equals("preset")) {

            // Update working times with a preset (standard, nightshift, 24 hours)

            WorkingTimeCalendarHelper helper = new WorkingTimeCalendarHelper(request, workingTimeCalendarProvider, calendarID);
            
            dateHelper = helper.makeDateEntryHelper(request);
            dateHelper.processPreset(request);
            populateDateWorkingTime();

            // Go back to edit date
            workDateModify.setPageAction("preset");
            workDateModify.setDateHelper(dateHelper);
            workDateModify.setPersonalCalendarBean(personalCalendarBean);
			return workDateModify;

        }
		return workDateModify;

	}
	
	public void populateDateWorkingTime() {
		personalCalendarBean = new PersonalCalendarBean();
		for (int index = 0; index < 5; index++) {
			PersonalCalendarBean.BoxDateBean boxEntry = personalCalendarBean.new BoxDateBean();
			boxEntry.setStartDate(dateHelper.getWorkingTimeEditHelper().getStartTime(index));
			boxEntry.setEndDate(dateHelper.getWorkingTimeEditHelper().getEndTime(index));
			boxEntry.setDisabled(Boolean.valueOf(!dateHelper.isWorkingTimeSelected()));
			boxEntry.setTimeControlName("timeControl_" + index);
			boxEntry.setStartTimeName("timeStart_" + index);
			boxEntry.setEndTimeName("timeEnd_" + index);
			boxEntry.setTimeBoxIndex(index);
			personalCalendarBean.getTimeBoxIndexList().add(boxEntry);
		}

	}
	
	public void setPageTitle() {
		History history = null; // new History();
		HttpSession session = requestGlobals.getHTTPServletRequest().getSession();

		history = (History) session.getAttribute("historyTagHistoryObject");
		if (history != null) {
			PageLevel pageLevel = new PageLevel(1);
			pageLevel.setActive(false);
			if (StringUtils.isNotEmpty(pageAction) && pageAction.equals("modifyDate")) {
				pageLevel.setDisplay("Modify Date");
			} else {
				pageLevel.setDisplay("New Date");
			}
			pageLevel.setJspPage(jSPRootURL + "/personal/workcalendar/WorkDateModify?module=" + moduleId);
			// pageLevel.setQueryString("module='+moduleId" + moduleId);
			pageLevel.setShow(true);
			history.setLevel(pageLevel);

			session.setAttribute("historyTagHistoryObject", history);
			session.setAttribute("user", SessionManager.getUser());
			try {
				this.pageTitle = history.getPresentation().toString();
			} catch (HistoryException pnetEx) {
				log.error("Error occured while setting user's current space andhistory for page : "
						+ pnetEx.getMessage());
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
	 * @return the dateTypeRange
	 */
	
	/**
	 * @return the dateHelper
	 */
	public WorkingTimeCalendarDateEntryHelper getDateHelper() {
		return dateHelper;
	}
	/**
	 * @param dateHelper the dateHelper to set
	 */
	public void setDateHelper(WorkingTimeCalendarDateEntryHelper dateHelper) {
		this.dateHelper = dateHelper;
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
	 * @return the entryID
	 */
	public String getEntryID() {
		return entryID;
	}

	/**
	 * @param entryID the entryID to set
	 */
	public void setEntryID(String entryID) {
		this.entryID = entryID;
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
	 * @return the pageTitle
	 */
	public String getPageTitle() {
		return pageTitle;
	}

    /**
     * @return the versionNumber
     */
    public String getVersionNumber() {
        return versionNumber;
    }
    
    /**
	 * @return the errorAlertTitle
	 */
	public String getErrorAlertTitle() {
		return errorAlertTitle;
	}
	
	/**
	 * @return the errorSingleDate
	 */
	public String getErrorSingleDate() {
		return errorSingleDate;
	}
	
	/**
	 * @return the dateFormatPattern
	 */
	public String getDateFormatPattern() {
		return dateFormatPattern;
	}
	
	/**
	 * @return the errorDateRange
	 */
	public String getErrorDateRange() {
		return errorDateRange;
	}
	
	/**
	 *  Returns group heading for Work Calendar
	 */
	public String getGroupHeadingWorkCalendar() {
		return PropertyProvider.get("prm.groupheading.workcalendar.label");
	}
}
