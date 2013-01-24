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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.URLFactory;
import net.project.business.report.AssignmentList;
import net.project.business.report.TimeSubmitalWeek;
import net.project.business.report.TimeSummaryCSVDownLoad;
import net.project.calendar.PnCalendar;
import net.project.channel.ScopeType;
import net.project.hibernate.model.PnAssignment;
import net.project.hibernate.model.PnPerson;
import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentStoreDataFactory;
import net.project.resource.AssignmentWorkCaptureHelper;
import net.project.resource.PersonProperty;
import net.project.resource.mvc.handler.AssignmentDate;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.util.InvalidDateException;
import net.project.util.NumberFormat;
import net.project.util.StringUtils;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.view.pages.base.BasePage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.util.TextStreamResponse;

/**
 * Page for create time summary view
 */
public class TimeSummaryView extends BasePage{
	
	private static Logger log = Logger.getLogger(TimeSummaryView.class);
	
	@Property
	private List<PnAssignment> assignmentList;
		
	@Property
	private PnAssignment assignment;
	
	@Property
	private ArrayList<Integer> dayTotal;
	
	@Property
	private Integer currentDay;
	
	private Map summaryDateValues;
	
	private TimeQuantity oldWorkForDay;
	
	@Property
	private String workHour; 
	
	@Property
	private List<AssignmentList> detailTimeSubmittal;
	
	@Property
	private AssignmentList currentTimeSubmittal;
	
	@Property
	private TimeSubmitalWeek currentWorkHourValue;
	
	@Property
	private List<TimeSubmitalWeek> weekList;
	
	@Property
	private String userName;
	
	@Property
	private long monthStartDate;
	
	@Property
	private List<String> dailyTotalListInFormat; 
		
	@Property
	private String dailyTotal; 
	
	private String personName;
	
	@Property
	private String grantTotalString;
	
	private Double[] resourceSubTotal;
	
	@Property
	private String resourceWorkHour;
	
	@Property
	private String projectWorkHour;
	
	private NumberFormat numberFormat;
	
	@Property
	private Integer columnSize;
	
	@Property
	private boolean assignmentEnabled;
	
	@Property
	private boolean isDailyView;
	
	@Property
	private List<TimeSubmitalWeek> dailyTotalListWeekyView;
	
	@Property
	private String memberListString;
	
	private PersonProperty property;
	
	 /**
	 * Initialize the values
	 */
	void initialize(){
		 summaryDateValues = new HashMap();
	     dayTotal = new ArrayList();
	     dailyTotalListInFormat = new ArrayList<String>();
	     detailTimeSubmittal = new ArrayList<AssignmentList>();
	     numberFormat = NumberFormat.getInstance();
	     assignmentEnabled = false;//PropertyProvider.getBoolean("prm.business.timesubmital.report.showassignment.isenabled");
	     isDailyView = false;
	     property = PersonProperty.getFromSession(getSession());
		 property.setScope(ScopeType.GLOBAL.makeScope(SessionManager.getUser()));
	 }
	 
	/**
	 * @param action
	 * @return page
	 */
	Object onActivate(String action){
		initialize();
		if(action.equals("viewPage")){
			Date convertedDate = PnCalendar.currentTime();
			PnCalendar tempCal = new PnCalendar();
			tempCal.setTime(convertedDate);
			tempCal.add(Calendar.DATE, 1);
			//Dates of starting and ending of current month
			Date startDate = tempCal.getPrevMonth();
		
			Calendar calEnd = Calendar.getInstance();
			calEnd.setTime(convertedDate);
			calEnd.add(Calendar.DATE, 1);
			Date endDate = calEnd.getTime();
			userName = SessionManager.getUser().getDisplayName();
			monthStartDate = startDate.getTime();
			personName = getUser().getDisplayName();
			
			Integer[] userIds = getAllBusinessMembers();
			retrieveMonthList(startDate, endDate, tempCal.getActualMaximum(tempCal.DAY_OF_MONTH), userIds);
		} else if(action.equals("weeklyView")){
			
			if (StringUtils.isNotEmpty(getRequest().getParameter("startDate"))
							&& StringUtils.isNotEmpty(getRequest().getParameter("endDate")) && StringUtils.isNotEmpty(getRequest().getParameter("showDailyView")) &&
									StringUtils.isNotEmpty(getRequest().getParameter("showAssignmentView")) /*&& StringUtils.isNotEmpty(getRequest().getParameter("userIds"))*/) {
				
				Integer[] userIds = null;
				if(StringUtils.isNotEmpty(getRequest().getParameter("userIds"))){
					userIds = StringUtils.getIntegerArrayOfCSNString(getParameter("userIds"));
				} else {
					userIds = getAllBusinessMembers();
				}
				if(getRequest().getParameter("showAssignmentView").equals("true")){
					assignmentEnabled = true;
				}
				if(getRequest().getParameter("showDailyView").equals("true")){
					isDailyView = true;
				} 
				personName = getUser().getDisplayName();
				Date startDate = null;
				Date endDate = null;
				try {
					startDate = getUser().getDateFormatter().parseDateString(getRequest().getParameter("startDate"), "dd/MM/yy");
					endDate = getUser().getDateFormatter().parseDateString(getRequest().getParameter("endDate"), "dd/MM/yy");
				} catch (InvalidDateException pnetEx) {
					pnetEx.printStackTrace();
				}
				
				PnCalendar tempCal = new PnCalendar();
				userName = SessionManager.getUser().getDisplayName();
				monthStartDate = startDate.getTime();
				retrieveMonthList(startDate, endDate, tempCal.getActualMaximum(tempCal.DAY_OF_MONTH), userIds);
			}
		} else if(action.equals("exportCSV")){
			List<AssignmentList> exportCSVassignmentList = (List<AssignmentList>) getSession().getAttribute("detailTimeSubmittal");
			TimeSummaryCSVDownLoad summaryCSVDownLoad = new TimeSummaryCSVDownLoad();
			summaryCSVDownLoad.setAssignMentList(exportCSVassignmentList);
			getRequest().getSession(true).setAttribute("summaryCSVDownLoad", summaryCSVDownLoad);
		} else if(action.equals("saveChanges")){
			PersonProperty property = PersonProperty.getFromSession(getSession());
			property.setScope(ScopeType.GLOBAL.makeScope(SessionManager.getUser()));
			replacePropertyValue();
			
			return new TextStreamResponse("text/plain", "success"); 
		}
		return null;
	}
	
	private Integer[] getAllBusinessMembers() {
		List<PnPerson> persons = getPnPersonService().getPersonsByProjectId(Integer.parseInt(SessionManager.getUser().getCurrentSpace().getID()));
		Integer[] personList = new Integer[persons.size()];	
		if (persons != null && persons.size() > 0) {
				int i = 0;
				for(PnPerson person:  persons){
					personList[i] = person.getPersonId();
					if(i == 0){
						memberListString = personList[i]+",";
					} else {
						memberListString += personList[i]+",";
					}
					i++;
				}	
			}
		return personList;	
	}	
	
	private void replacePropertyValue(){
		try {
			String[] Ids = getParameter("ids").split(",");
			int indexCnt = 0;
			for(String id : Ids){
				String[] idType = id.split("_");
				if(idType.length > 1 && idType[0].equals("member") && indexCnt == 0){
					if(getParameter("value").equals("true")) {
						property.replace("prm.report.timesubmittal", "noderesource_"+idType[1]+"expanded"+getParameter("viewType"), "false");
					} else {
						property.replace("prm.report.timesubmittal", "noderesource_"+idType[1]+"expanded"+getParameter("viewType"), "true");
					}
				} else if(idType.length > 2 && getParameter("showAssignmentView").equals("true") && idType[0].equals("project") && indexCnt == 0){
					if(getParameter("value").equals("true")) {
						property.replace("prm.report.timesubmittal", "nodeprt_"+idType[1]+"_"+idType[2]+"expanded"+getParameter("viewType"), "false");
					} else {
						property.replace("prm.report.timesubmittal", "nodeprt_"+idType[1]+"_"+idType[2]+"expanded"+getParameter("viewType"), "true");
					}
				}
				property.replace("prm.report.timesubmittal", "node"+id+"expanded"+getParameter("viewType"), getParameter("value"));
				indexCnt++;
			}
			property.replace("prm.report.timesubmittal", getParameter("property"), getParameter("value"));
		} catch (PersistenceException e) {
			log.getLogger(TimeSummaryView.class).error("Error occured while saving user settings" + e.getMessage());
		}
	}

	/**
	 * Make List of assignments and work capture for business space 
	 * @param startDate
	 * @param endDate
	 * @param totalDays
	 * @param userId
	 */
	public void retrieveMonthList(Date startDate, Date endDate, Integer totalDays, Integer[] userIds){
			AssignmentList assignmentRowList = null;
			weekList = getUtilService().getWeeksBetween(startDate, endDate);
			PnCalendar cal = new PnCalendar(SessionManager.getUser());
			cal.setTime(startDate);
			cal.add(Calendar.DATE, -1);
			assignmentList = getPnAssignmentService().getAssignmentDetailsWithFilters(userIds , "assignee", null,
					null, null, false, false, false, false, cal.getTime(), endDate, null, null, null, null, null, 0, 0, true);
			AssignmentWorkCaptureHelper workCaptureHelper = new AssignmentWorkCaptureHelper();
			
			summaryDateValues.putAll(workCaptureHelper.getTotalWorkHoursForMonth(cal.getTime(), endDate, userIds, true));
			
			String spaceId = "0";
			String personId = "0";
			Double[] dailyTotalList = new Double[weekList.size()];
			double grantTotal = 0.0;
		    Double tempResourceTotal[] = new Double[weekList.size()];
			resourceSubTotal = new Double[weekList.size()];
			Double resourceTot = 0.0;
			String lastResourceName = null;
			Double[] projectTotal = new Double[weekList.size()];
			Double monthlyTotal = 0.0;
			double monthlyProjectTotal = 0.0;
			String lastProjectName = null;
			double weeklyTotal = 0.0;
			List<TimeSubmitalWeek> monthlyWorkHour = new ArrayList<TimeSubmitalWeek>();
			String assignmentType = null;
			int noOfWeek = 0;
			for (Iterator it = assignmentList.iterator(); it.hasNext();) {
				assignment = (PnAssignment) it.next();
				List<TimeSubmitalWeek> workHourList = new ArrayList<TimeSubmitalWeek>();
				cal.setTime(startDate);
				cal.add(Calendar.DATE, -1);
				monthlyTotal = 0.0;
				int i = 0;
				noOfWeek = 0;
				for(TimeSubmitalWeek week : weekList){
					TimeSubmitalWeek weeklyDateRange = new TimeSubmitalWeek(); 
					if(week != null ){
						oldWorkForDay =(TimeQuantity)summaryDateValues.get(new AssignmentDate( cal.getTime(), ""+assignment.getPnObjectName().getObjectId()+"_"+assignment.getPnPerson().getPersonId()));
						oldWorkForDay = (oldWorkForDay == null ? new TimeQuantity(0, TimeQuantityUnit.HOUR) : oldWorkForDay);
						String workForDay = numberFormat.formatNumber(oldWorkForDay.getAmount().doubleValue(), 0, 2);
						monthlyTotal = monthlyTotal + oldWorkForDay.getAmount().doubleValue();
						weeklyDateRange.setDateValue(cal.getTime());
						cal.add(Calendar.DATE, 1);
						
						weeklyDateRange.setWork(!workForDay.equals("0") ? workForDay : " ");
						weeklyDateRange.setDateString(week.getDateString());
						weeklyDateRange.setEndOfweek(false);
						if(tempResourceTotal[i] == null){
							tempResourceTotal[i] = 0.0;
						}
						
						tempResourceTotal[i] = tempResourceTotal[i] + oldWorkForDay.getAmount().doubleValue() != 0.0 ? oldWorkForDay.getAmount().doubleValue() : 0.0;
						weeklyTotal = weeklyTotal + oldWorkForDay.getAmount().doubleValue();
						workHourList.add(weeklyDateRange);
					} else {
						noOfWeek ++;
						if(tempResourceTotal[i] == null){
							tempResourceTotal[i] = 0.0;
						}
						tempResourceTotal[i] = tempResourceTotal[i] + weeklyTotal;
						TimeSubmitalWeek weekTotal = new TimeSubmitalWeek();
						weekTotal.setWeeklyTotal(!numberFormat.formatNumber(weeklyTotal, 0, 2).equals("0") ? numberFormat.formatNumber(weeklyTotal, 0, 2) : " ");
						weekTotal.setEndOfweek(true);
						weeklyTotal = 0.0;
						workHourList.add(weekTotal);
					}
					i++;
				}
				monthlyWorkHour = workHourList;
				String contextURL = URLFactory.makeURL(assignment.getPnObjectName().getObjectId().toString(), assignment.getPnObjectType().getObjectType(), true);;
				
				assignmentType = (assignment.getPnObjectType().getObjectType().equals(ObjectType.FORM_DATA)) ? "Form data" : assignment.getPnObjectType().getObjectType();
				
				if (assignment.getPnObjectType().getObjectType().equals(ObjectType.FORM_DATA)){
					String form_abbrevation = new AssignmentStoreDataFactory().getFormAbbreviationByClassId(assignment.getPnClassInstance().getClassId());
					contextURL += "&spaceID="+assignment.getSpaceId()+ "&redirectedFromSpace=true&redirectedFromTimeSumaryReport=true";
					assignment.getPnObjectName().setName(form_abbrevation + "-" + assignment.getPnClassInstance().getSequenceNo()+ ": " + assignment.getPnObjectName().getName());
				} else if (assignment.getPnObjectType().getObjectType().equals(ObjectType.TASK)){
					assignment.getPnObjectName().setName(assignment.getPnObjectName().getName());
					contextURL = "";
					try {
						contextURL += SessionManager.getJSPRootURL() + "/project/Main.jsp?id=" + assignment.getSpaceId() + 
							"&page=" +URLEncoder.encode(SessionManager.getJSPRootURL() + "/servlet/AssignmentController/TaskView?"+
							"module="+Module.SCHEDULE+"&action="+Action.VIEW+"&id="+assignment.getPnObjectName().getObjectId() + 
							"&refLink=/business/report/TimeSummary?module="	+ Module.TIME_SUMMARY_REPORT+"&businessId=" + SessionManager.getUser().getCurrentSpace().getID(), SessionManager.getCharacterEncoding());
					} catch (UnsupportedEncodingException e) {
						log.getLogger(TimeSummaryView.class).error("Error occurs while creating context url : " + e.getMessage());
					}
				}
				if((!spaceId.equals("0") && spaceId.equals(assignment.getSpaceId().toString()))){
				   if(personId.equals(assignment.getPnPerson().getPersonId().toString())){
						assignmentRowList = new AssignmentList(null, assignment.getPnObjectName().getName(), monthlyWorkHour, numberFormat.formatNumber(monthlyTotal, 0, 2), null, assignment.getSpaceId(), assignment.getPnPerson().getPersonId(), null, null, null, null, null, assignment.getPnObjectName().getObjectId(), null,assignmentType, assignment.getPnPerson().getFirstName()+" "+assignment.getPnPerson().getLastName(), assignment.getSpaceName(), null, null, null, getVisiblityClass("project_"+assignment.getPnPerson().getPersonId()+"_"+assignment.getSpaceId(), false), null, null, contextURL);
				   } else {
					   assignmentRowList = new AssignmentList(null, null, null, null, null, Integer.parseInt(spaceId), Integer.parseInt(personId), null, null, null, projectTotal, numberFormat.formatNumber(monthlyProjectTotal, 0, 2), assignment.getPnObjectName().getObjectId(), lastProjectName, null, null, null, weekList, null, getVisiblityClass("project_"+personId+"_"+spaceId, false), null, null, getVisiblityClass("prt_"+personId+"_"+spaceId,  false), null);
					   detailTimeSubmittal.add(assignmentRowList);
					   projectTotal = new Double[weekList.size()];
					   monthlyProjectTotal = 0.0;
					   assignmentRowList = new AssignmentList(null, null, null, null, null, Integer.parseInt(spaceId), Integer.parseInt(personId), resourceSubTotal, numberFormat.formatNumber(resourceTot, 0, 2), lastResourceName, null, null, assignment.getPnObjectName().getObjectId(), null, null, null, null, weekList, getVisiblityClass("res_total_"+personId, false), null, null, getVisiblityClass("resource_"+personId, true), null, null);
					   detailTimeSubmittal.add(assignmentRowList);
					   resourceSubTotal = new Double[weekList.size()];
					   resourceTot = 0.0;
					   assignmentRowList = new AssignmentList(assignment.getSpaceName(), assignment.getPnObjectName().getName(), monthlyWorkHour, numberFormat.formatNumber(monthlyTotal, 0, 2), assignment.getPnPerson().getFirstName()+" "+assignment.getPnPerson().getLastName(), assignment.getSpaceId(), assignment.getPnPerson().getPersonId(), null, null, null, null, null, assignment.getPnObjectName().getObjectId(), null, assignmentType, assignment.getPnPerson().getFirstName()+" "+assignment.getPnPerson().getLastName(), assignment.getSpaceName(), null, getVisiblityClass("member_"+assignment.getPnPerson().getPersonId(), false), getVisiblityClass("project_"+assignment.getPnPerson().getPersonId()+"_"+assignment.getSpaceId(), false), getVisiblityClass("project_"+assignment.getPnPerson().getPersonId()+"_"+assignment.getSpaceId(), false), null, null, contextURL);
					   personId = assignment.getPnPerson().getPersonId().toString();
				   }
				} else {
					assignmentRowList = new AssignmentList(null, null, null, null, null, Integer.parseInt(spaceId), Integer.parseInt(personId), null, null, null, projectTotal, numberFormat.formatNumber(monthlyProjectTotal, 0, 2), assignment.getPnObjectName().getObjectId(), lastProjectName, null,  null, null, weekList, null, getVisiblityClass("project_"+personId+"_"+spaceId, false), null, null, getVisiblityClass("prt_"+personId+"_"+spaceId,  false), null);
					detailTimeSubmittal.add(assignmentRowList);
					projectTotal = new Double[weekList.size()];
					monthlyProjectTotal = 0.0;
					if(!personId.equals("0") && personId.equals(assignment.getPnPerson().getPersonId().toString())){
						assignmentRowList = new AssignmentList(assignment.getSpaceName(), assignment.getPnObjectName().getName(), monthlyWorkHour, numberFormat.formatNumber(monthlyTotal, 0, 2), null, assignment.getSpaceId(), assignment.getPnPerson().getPersonId(), null, null, null, null, null, assignment.getPnObjectName().getObjectId(), null, assignmentType, assignment.getPnPerson().getFirstName()+" "+assignment.getPnPerson().getLastName(), assignment.getSpaceName(), null, getVisiblityClass("member_"+assignment.getPnPerson().getPersonId(), false), getVisiblityClass("project_"+assignment.getPnPerson().getPersonId()+"_"+assignment.getSpaceId(), false), getVisiblityClass("project_"+assignment.getPnPerson().getPersonId()+"_"+assignment.getSpaceId(), false), null, null, contextURL);
					} else {
						if(!spaceId.equals("0")){
							assignmentRowList = new AssignmentList(null, null, null, null, null, Integer.parseInt(spaceId), Integer.parseInt(personId), resourceSubTotal, numberFormat.formatNumber(resourceTot, 0, 2), lastResourceName, null, null, assignment.getPnObjectName().getObjectId(), null, null, null, null, weekList, getVisiblityClass("res_total_"+personId, false), null, null, getVisiblityClass("resource_"+personId, true), null, contextURL);
							detailTimeSubmittal.add(assignmentRowList);
							resourceSubTotal = new Double[weekList.size()];
							resourceTot = 0.0;
						}
						assignmentRowList = new AssignmentList(assignment.getSpaceName(), assignment.getPnObjectName().getName(), monthlyWorkHour, numberFormat.formatNumber(monthlyTotal, 0, 2), assignment.getPnPerson().getFirstName()+" "+assignment.getPnPerson().getLastName(), assignment.getSpaceId(), assignment.getPnPerson().getPersonId(), null, null, null, null, null, assignment.getPnObjectName().getObjectId(), null, assignmentType, assignment.getPnPerson().getFirstName()+" "+assignment.getPnPerson().getLastName(), assignment.getSpaceName(), null, getVisiblityClass("member_"+assignment.getPnPerson().getPersonId(), false), getVisiblityClass("project_"+assignment.getPnPerson().getPersonId()+"_"+assignment.getSpaceId(), false), getVisiblityClass("project_"+assignment.getPnPerson().getPersonId()+"_"+assignment.getSpaceId(), false), null, null, contextURL);
						personId = assignment.getPnPerson().getPersonId().toString();
					}
					spaceId = assignment.getSpaceId().toString();
				}
				
				resourceTot = resourceTot + monthlyTotal;
				grantTotal = grantTotal + monthlyTotal;
				monthlyProjectTotal = monthlyProjectTotal + monthlyTotal;
				grantTotalString = numberFormat.formatNumber(grantTotal, 0, 2);
				
				for(int j = 0; j < weekList.size(); j++){
					if(resourceSubTotal[j] == null) {
						resourceSubTotal[j] = 0.0;
					}
					if(projectTotal[j] == null){
						projectTotal[j] = 0.0;
					}
					if(dailyTotalList[j] == null){
						dailyTotalList[j] = 0.0;
					}
					if(tempResourceTotal[j] != null){
						projectTotal[j] = projectTotal[j] + tempResourceTotal[j];
						resourceSubTotal[j] = resourceSubTotal[j] + tempResourceTotal[j];
						dailyTotalList[j] = dailyTotalList[j] + tempResourceTotal[j];
					}
				}	
				tempResourceTotal = new Double[weekList.size()];
				
				lastResourceName = assignment.getPnPerson().getFirstName()+" "+assignment.getPnPerson().getLastName();
				lastProjectName = assignment.getSpaceName();
				detailTimeSubmittal.add(assignmentRowList);
			}
			
			dailyTotalListWeekyView = AssignmentList.convertDoubleToString(dailyTotalList, weekList);
			if(isDailyView){
				columnSize = weekList.size() + 1;
			} else {
				columnSize = noOfWeek + 1;
			}
			if(CollectionUtils.isNotEmpty(assignmentList)) {
				assignmentRowList = new AssignmentList(null, null, null, null, null, Integer.parseInt(spaceId), Integer.parseInt(personId), null, null, null, projectTotal, numberFormat.formatNumber(monthlyProjectTotal, 0, 2), assignment.getPnObjectName().getObjectId(),lastProjectName, null, null, null, weekList, null, getVisiblityClass("project_"+personId+"_"+spaceId, false), null, null, getVisiblityClass("prt_"+personId+"_"+spaceId,  false), null);
				detailTimeSubmittal.add(assignmentRowList);
				assignmentRowList = new AssignmentList(null, null, null, null, null, Integer.parseInt(spaceId), Integer.parseInt(personId), resourceSubTotal, numberFormat.formatNumber(resourceTot, 0, 2), lastResourceName, null, null, assignment.getPnObjectName().getObjectId(), null, null, null, null, weekList, getVisiblityClass("res_total_"+personId, false), null, null, getVisiblityClass("resource_"+personId, true), null, null);
				detailTimeSubmittal.add(assignmentRowList);
			}	
		    getRequest().getSession(true).setAttribute("detailTimeSubmittal", detailTimeSubmittal);
		    getRequest().getSession(true).setAttribute("dailyTotalList", dailyTotalList);
		    getRequest().getSession(true).setAttribute("personName", personName);
		    getRequest().getSession(true).setAttribute("totalDays", totalDays);
		    getRequest().getSession(true).setAttribute("DateString",getUser().getDateFormatter().formatDate(startDate, "MMM-yy"));
		    getRequest().getSession(true).setAttribute("weekList",weekList);
		    getRequest().getSession(true).setAttribute("grantTotal",grantTotalString);
	}
	
	private String getVisiblityClass( String id, boolean collapseNode) {
		String[] expandedProps = null;
		if(assignmentEnabled){
			expandedProps = property.get("prm.report.timesubmittal", "node"+id+"expandedassignmentView", false);
		} else {
			expandedProps = property.get("prm.report.timesubmittal", "node"+id+"expandedprojectView", false);
		}
	    String expandedProp = (expandedProps != null && expandedProps.length > 0 ? expandedProps[0]: null);
	    if(expandedProp == null){
	    	return collapseNode ? " visible": " hidden";
	    }else if(expandedProp.equals("true") && collapseNode){
	    	return " visible";
	    } else if(expandedProp.equals("true") && !collapseNode){
	    	return " visible";
	    } else if(expandedProp.equals("false") && collapseNode){
	    	return " hidden";
	    } else if(expandedProp.equals("false") && !collapseNode){
	    	return " hidden";
	    } 
	    return null;
	}
}
