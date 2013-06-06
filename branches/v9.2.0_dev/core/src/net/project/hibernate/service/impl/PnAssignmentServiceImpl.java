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
package net.project.hibernate.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.project.base.ObjectType;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.hibernate.dao.IPnAssignmentDAO;
import net.project.hibernate.model.PnAssignment;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.model.resource_reports.ReportMonth;
import net.project.hibernate.model.resource_reports.ReportUser;
import net.project.hibernate.model.resource_reports.ReportUserProjects;
import net.project.hibernate.model.resource_reports.ReportUserWorkTime;
import net.project.hibernate.service.IPnAssignmentService;
import net.project.hibernate.service.IPnPersonAllocationService;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.IUtilService;
import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentStatus;
import net.project.resource.Person;
import net.project.resource.ResourceWorkingTimeCalendarProvider;
import net.project.security.User;
import net.project.util.DateFormat;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="assignmentService")
public class PnAssignmentServiceImpl implements IPnAssignmentService {

	@Autowired
	private IPnAssignmentDAO pnAssignmentDAO;

	@Autowired
	private IUtilService utilService;
	
	@Autowired
	private IPnPersonAllocationService pnPersonAllocationService;
	
	@Autowired
	private IPnPersonService pnPersonService;
    
    private WorkingTimeCalendarDefinition  workingTimeCalendarDefinition;
    
    private Map<Integer, WorkingTimeCalendarDefinition> workingTimeCalendarByResourceID = new HashMap <Integer, WorkingTimeCalendarDefinition> ();
	
	/**
	 * @param pnAssignmentDAO The pnAssignmentDAO to set.
	 */
	public void setPnAssignmentDAO(IPnAssignmentDAO pnAssignmentDAO) {
		this.pnAssignmentDAO = pnAssignmentDAO;
	}

	/**
	 * @param utilService The utilService to set.
	 */
	public void setUtilService(IUtilService utilService) {
		this.utilService = utilService;
	}	
	
	/**
	 * @param pnPersonAllocationService The pnPersonAllocationService to set.
	 */
	public void setPnPersonAllocationService(IPnPersonAllocationService pnPersonAllocationService) {
		this.pnPersonAllocationService = pnPersonAllocationService;
	}	
			

	/**
	 * @param pnPersonService The pnPersonService to set.
	 */
	public void setPnPersonService(IPnPersonService pnPersonService) {
		this.pnPersonService = pnPersonService;
	}
	
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnAssignmentService#getAssigmentsList(int[], java.util.Date, java.util.Date)
	 */
	public List<PnAssignment> getAssigmentsList(Integer[] personIds, Date startDate, Date endDate) {
		return this.pnAssignmentDAO.getAssigmentsList(personIds, startDate, endDate);
	}
	

	public List<PnAssignment> getAssigmentsListForProject(Integer projectId){
		return this.pnAssignmentDAO.getAssignmentList(projectId);
	}
	
	
	/* (non-Javadoc) 
	 * @see net.project.hibernate.service.IPnAssignmentService#getAssigmentsList(java.lang.Integer, java.lang.Integer[], java.util.Date)
	 */
	public List<PnAssignment> getCurrentAssigmentsListForProject(Integer projectId, Integer[] personIds, Date date) {
		return pnAssignmentDAO.getCurrentAssigmentsListForProject(projectId, personIds, date);	
	}

    /** Initializing WorkingTimeCalendarDefinition as per specified resource.
     * @param spaceID
     * @param resourcePersonID
     */
    private void initializeWorkingTimeCalender(Integer resourcePersonID){
    	//First check the calendar definitation in workingTimeCalendarByResourceID map.
    	workingTimeCalendarDefinition = workingTimeCalendarByResourceID.get(resourcePersonID);
        
    	//If not found in map, initialize person working time calender.
        if(workingTimeCalendarDefinition == null){
	    	try {
	        	workingTimeCalendarDefinition = ResourceWorkingTimeCalendarProvider.make(new User(new Person(resourcePersonID.toString())))
	                            .getForResourceID(resourcePersonID.toString());
	        	//Put this calendar in map so that for this resource we can get it form map without loading it again.
	        	workingTimeCalendarByResourceID.put(resourcePersonID, workingTimeCalendarDefinition);
	        } catch (PersistenceException pnetEx) {
	            Logger.getLogger(PnAssignmentServiceImpl.class).error("Error occured while getting resource's " +
	                    "personal working time calender : "+ pnetEx.getMessage());
			} catch (Exception e) {
				Logger.getLogger(PnAssignmentServiceImpl.class).error("Error occured while getting working time calendar for user "+e.getMessage());
			}
        }
  
    }
    
	/** 
	 * get working hours in specified date interval as per resource working time calendar.
	 * 
	 * @param startDate  interval start date
	 * @param endDate    interval end date
	 * @return  working hourss
	 */	
	private float getWorkingHours(Date startDate, Date endDate) {
		float workingHours = 0;

		if (startDate == null || endDate == null || startDate.after(endDate)) {
			return 0;
		}

		Calendar calFrom = Calendar.getInstance();
		calFrom.setTime(startDate);
		Calendar calTo = Calendar.getInstance();
		calTo.setTime(endDate);
		calTo.add(Calendar.DATE, 1);
		while (calFrom.getTime().before(calTo.getTime())) {
            //if wokring time caendar is defined. 
            if (this.workingTimeCalendarDefinition != null) {
                if (this.workingTimeCalendarDefinition.isWorkingDay(calFrom)) {
                    workingHours += (this.workingTimeCalendarDefinition.getWorkingHoursInDay(calFrom).toHour()).floatValue();
                }
            } else if (calFrom.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
                            && calFrom.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                // else add default work hours of default working days.
                workingHours += 8;
            }
            calFrom.add(Calendar.DATE, 1);
        }
		return workingHours;
	}
	
	/**
	 * 
	 * @param initalMonthList
	 * @param work
	 * @param percent
	 * @param assignFrom
	 * @param assignTo
	 * @return
	 */
	private List<ReportMonth> getWorkByMonthForAssignment(List<ReportMonth> initalMonthList, BigDecimal work, 
                    BigDecimal percent, Date assignFrom, Date assignTo) {
		List<ReportMonth> monthList = new ArrayList<ReportMonth>();
		Calendar cal = Calendar.getInstance();
		cal.set(initalMonthList.get(0).getYear(), initalMonthList.get(0).getMonthInYear(), 1, 0, 0, 0);
		cal.clear(Calendar.MILLISECOND);
		cal.add(Calendar.DATE, -1);
		Date preIntervalDate = cal.getTime();
		
		for (ReportMonth reportMonth : initalMonthList){					
			cal.set(reportMonth.getYear(), reportMonth.getMonthInYear(), 1, 0, 0, 0);
			cal.clear(Calendar.MILLISECOND);
			Date startDate = cal.getTime();
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date endDate = cal.getTime();
			
			float notCalulatedWork = 0.0f;
			if((assignFrom.before(preIntervalDate) || assignFrom.equals(preIntervalDate)) 
					&& (startDate.before(assignTo) || startDate.equals(assignTo))){
				notCalulatedWork =  (float)(getWorkingHours(assignFrom, preIntervalDate) 
						* percent.doubleValue() /100.0);
			}

			float WorkingHours = 0;
			if ((startDate.after(assignFrom) || startDate.equals(assignFrom)) 
					&& (endDate.before(assignTo) || endDate.equals(assignTo))){
                WorkingHours = getWorkingHours(startDate, endDate);
				preIntervalDate = endDate;
			}
			
			if (startDate.before(assignFrom) && (endDate.before(assignTo) || endDate.equals(assignTo))){
                WorkingHours = getWorkingHours(assignFrom, endDate);
				preIntervalDate = endDate;
			}
			
			if ((startDate.after(assignFrom) || startDate.equals(assignFrom)) 
					&& endDate.after(assignTo)){
                WorkingHours = getWorkingHours(startDate, assignTo);
			}			
			
			if (startDate.before(assignFrom) && endDate.after(assignTo)){
                WorkingHours = getWorkingHours(assignFrom, assignTo);
			}				
			
			float monthWork =  (float)( WorkingHours  * percent.doubleValue() /100.0);
			if (notCalulatedWork + monthWork > work.floatValue()){
				if(notCalulatedWork > work.floatValue()){
					notCalulatedWork = work.floatValue();
				}	
				monthWork = work.floatValue() - notCalulatedWork;
			}
			
			ReportMonth month = new ReportMonth();
			month.setMonthInYear(reportMonth.getMonthInYear());
			month.setYear(reportMonth.getYear());
			month.setTotalWork(monthWork);
			
			monthList.add(month);
		}
					
		return monthList;
	}

	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @param results
	 * @return
	 */
	private List<ReportUser> transformUserList(Date startDate, Date endDate, List userList, DateFormat userDateFormatter) {
		List<ReportUser> users = new ArrayList<ReportUser>();				
		List<ReportMonth>initialMonthList = utilService.getMonthsBetween(startDate, endDate);

		Integer userId = 0;
		ReportUser user = new ReportUser();
		Iterator iter = userList.iterator();
		while (iter.hasNext()) {
			Object[] row = (Object[]) iter.next();
			// if row[5](start date) or row[6](end date) is null do not perform calculation 
			if(row[5] == null || row[6] == null){
				continue;
			}
			if (userId.intValue() != ((Integer) row[0]).intValue()) {
				userId = (Integer) row[0];
				user = new ReportUser(userId, (String) row[1], (String) row[2]);
				users.add(user);
			}
            BigDecimal percentAllocated = (BigDecimal) row[3];
			BigDecimal totalWork =  (BigDecimal) row[4];
			Date fromDate = null;
			Date toDate = null;
			SimpleDateFormat formateDate = new SimpleDateFormat("dd/MM/yyyy");
			try {
				fromDate = formateDate.parse(userDateFormatter.formatDate((Date) row[5], "dd/MM/yyyy"));
				toDate = formateDate.parse(userDateFormatter.formatDate((Date) row[6], "dd/MM/yyyy"));
			} catch (Exception e) {
				Logger.getLogger(PnAssignmentServiceImpl.class).error("Error occured while parsing date "+e.getMessage());
			}
			Integer projectId = (Integer) row[7];
			String projectName = (String) row[8];
			
			if(percentAllocated == null){
				percentAllocated = BigDecimal.valueOf(0);
			}
			if(totalWork == null){
				totalWork = BigDecimal.valueOf(0);
			}
			if(!((toDate.before(startDate)) || (fromDate.after(endDate)))){
				//initalize working time calendar for this project and user.
                initializeWorkingTimeCalender(userId);
                
				List<ReportMonth> monthList = getWorkByMonthForAssignment(initialMonthList, totalWork, percentAllocated, fromDate, toDate);
				ReportUserProjects userProject = new ReportUserProjects();	
				boolean found = false;
				for (ReportUserProjects project : user.getProjektList()) {
					if (project.getProjectId().intValue() == projectId.intValue()) {
						found = true;
						userProject = project;
					}
				}
	
				if (found) {
					for (ReportMonth monthReport : monthList) {
						for (ReportMonth monthProject : userProject.getMonthList()){
							if (monthProject.getMonthInYear().intValue() == monthReport.getMonthInYear().intValue() &&
									monthProject.getYear().intValue() == monthReport.getYear().intValue()){
								monthProject.setTotalWork(monthProject.getTotalWork() + monthReport.getTotalWork());
							}
						}
					}
				} else {
					ReportUserProjects project = new ReportUserProjects();
					project.setProjectId(projectId);
					project.setProjectName(projectName);
					project.setMonthList(monthList);
					user.getProjektList().add(project);
				}
			}
		}
		return users;
	}	
	
	
 	public List<ReportUser> getWorkSumByMonthForUsers(Integer[] personIds, Integer[] projectIds, Date startDate, Date endDate){
 		List results = pnAssignmentDAO.getWorkSumByMonthForUsers(personIds, projectIds, startDate, endDate); 
 		//List<ReportUser> users = transformUserList(startDate, endDate, results);
 		return results;
 	}
 	
 	public List<ReportUser> getWorkSumByMonthForUsers(Integer resourceId, Integer businessId, Date startDate, Date endDate, DateFormat userDateFormatter){
 		
 		Calendar cal = Calendar.getInstance();
 		cal.setTime(startDate);
 		cal.add(Calendar.DATE, -1);
 		Date dateFrom = cal.getTime();
 		
 		cal.setTime(endDate);
 		cal.add(Calendar.DATE, 1);
 		Date dateTo = cal.getTime();
 		
 		List results = pnAssignmentDAO.getResourceAssignmentSummaryByBusiness(resourceId, businessId, dateFrom, dateTo); 
 		List<ReportUser> users = transformUserList(startDate, endDate, results, userDateFormatter);
 		return users;
 	}

 	private List<ReportUser> sumAllProjectsWorkButOne(List<ReportUser> users, Integer projectId){
 		List<ReportUser> summary = new ArrayList<ReportUser>();
 		for (ReportUser user : users){
 			ReportUser usr = new ReportUser();
 			usr.setFirstName(user.getFirstName());
 			usr.setLastName(user.getLastName());
 			usr.setUserId(user.getUserId());
 			List<ReportUserProjects> projectList = new ArrayList<ReportUserProjects>();
 			
 			ReportUserProjects otherProject = new ReportUserProjects();
 			otherProject.setProjectId(0);
 			otherProject.setProjectName("Other Projects");
 			List<ReportMonth> otherList = new ArrayList<ReportMonth>();
 			for (ReportUserProjects project : user.getProjektList()){
 				if (project.getProjectId().intValue() == projectId.intValue()){
 					projectList.add(project);
 				}else{
 					for(ReportMonth month : project.getMonthList()){
 						boolean found = false;
 						int idx = 0;
 						while (idx < otherList.size() && !found){
 							if (otherList.get(idx).getMonthInYear().intValue() == month.getMonthInYear().intValue() &&
 								otherList.get(idx).getYear().intValue() == month.getYear().intValue()){
 								found = true;
 								float totalWork = otherList.get(idx).getTotalWork() + month.getTotalWork();
 								otherList.get(idx).setTotalWork(totalWork);
 							}
 						  idx = idx + 1;	
 						}
 						if (!found){
 							otherList.add(month);
 						}
 					}
 				}
 			}
 			otherProject.setMonthList(otherList);
 			projectList.add(otherProject);
 			usr.setProjektList(projectList);
 			summary.add(usr);
 		}
 		return summary;
 	}
 	
 	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnAssignmentService#getResourceAssignmentSummaryByBusiness(java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date)
	 */
	public List getResourceAssignmentSummaryByBusiness(Integer businessId, Integer projectId, Date startDate, Date endDate) {
 		return null;
	}

	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnAssignmentService#getResourceAssignmentSummaryByBusiness(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date)
	 */
	public List getResourceAssignmentSummaryByBusiness(Integer resourceId, Integer businessId, Integer projectId, Date startDate, Date endDate, DateFormat userDateFormatter) {
		Calendar cal = Calendar.getInstance();
 		cal.setTime(startDate);
 		cal.add(Calendar.DATE, -1);
 		Date dateFrom = cal.getTime();
 		
 		cal.setTime(endDate);
 		cal.add(Calendar.DATE, 1);
 		Date dateTo = cal.getTime();
		
 		List results = pnAssignmentDAO.getResourceAssignmentSummaryByBusiness(resourceId, businessId, dateFrom, dateTo); 
 		List<ReportUser> users = transformUserList(startDate, endDate, results, userDateFormatter);
 		return sumAllProjectsWorkButOne(users, projectId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnAssignmentService#getResourceAssignmentSummaryByPortfolio(java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date)
	 */
	public List getResourceAssignmentSummaryByPortfolio(Integer portfolioId, Integer projectId, Date startDate, Date endDate) {
 		return null;
	}
	
	
	public List<PnAssignment> getResourceAssignmentDetails(Integer resourceId, Integer[] projectIds, Date startDate, Date endDate){
 		return this.pnAssignmentDAO.getResourceAssignmentDetails(resourceId, projectIds, startDate, endDate);
 	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnAssignmentService#getOverAllocatedResources(java.lang.Integer)
	 */
	public List<PnAssignment> getOverAllocatedResources(Integer userId) {		
		return this.pnAssignmentDAO.getOverAllocatedResources(userId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnAssignmentService#getAssignmentVsAllocation(java.lang.Integer, java.util.Date, java.util.Date)
	 */
	public List<ReportUser> getAssignmentVsAllocation(Integer resourceId, Integer businessId, Date startDate, Date endDate, DateFormat userDateFormatter) {
		Calendar cal = Calendar.getInstance();
 		cal.setTime(startDate);
 		cal.add(Calendar.DATE, -1);
 		Date dateFrom = cal.getTime();
 		
 		cal.setTime(endDate);
 		cal.add(Calendar.DATE, 1);
 		Date dateTo = cal.getTime();
 		
		List results = pnAssignmentDAO.getResourceAssignmentSummaryByBusiness(resourceId, businessId, dateFrom, dateTo);
		List<ReportUser> users = transformUserList(startDate, endDate, results, userDateFormatter);
		List<ReportUser> usersAllocations = pnPersonAllocationService.getResourceAllocationSumary(resourceId, businessId, startDate, endDate);

		for (ReportUser user : users) {
			boolean userFound = false;
			int idx = 0;
			while (!userFound && idx < usersAllocations.size()) {
				if (user.getUserId().intValue() == usersAllocations.get(idx).getUserId().intValue()) {
					userFound = true;
					for (ReportUserProjects project : user.getProjektList()) {
						boolean projectFound = false;
						List<ReportUserProjects> projectList = usersAllocations.get(idx).getProjektList();
						int prIdx = 0;
						while (!projectFound && prIdx < projectList.size()) {
							if (project.getProjectId().intValue() == projectList.get(prIdx).getProjectId().intValue()) {
								projectFound = true;
								for (ReportMonth month : project.getMonthList()) {
									boolean monthFound = false;
									List<ReportMonth> monthList = projectList.get(prIdx).getMonthList();
									int mthIdx = 0;
									while (!monthFound && mthIdx < monthList.size()) {
										if (month.getMonthInYear().intValue() == monthList.get(mthIdx).getMonthInYear().intValue()
												&& month.getYear().intValue() == monthList.get(mthIdx).getYear().intValue()) {
											monthFound = true;
											month.setAllocation(monthList.get(mthIdx).getAllocation());
										}
										mthIdx = mthIdx + 1;
									}
								}
							}
							prIdx = prIdx + 1;
						}
					}
				}
				idx = idx + 1;
			}
		}
		return users;
	}

	/* (non-Javadoc) 
	 * @see net.project.hibernate.service.IPnAssignmentService#getOverAssignedResources(java.lang.Integer, java.util.Date, java.util.Date)
	 */
	public List<PnAssignment> getOverAssignedResources(Integer userId, Date startDate, Date endDate) {
		List<PnAssignment> assignments = pnAssignmentDAO.getOverAssignedResources(userId, startDate, endDate);		
		return findOverAssignments(assignments);
	}
	
	/**
	 * Calculate user work time sumary by date 
	 * 
	 * @param workTime   - hash map that holds user work time by  day 
	 * @param assignment - user assignment
	 */
	private void getWorkTimeByDays(HashMap<Date, Float> workTime, PnAssignment assignment){
		Date startDate = assignment.getStartDate();
		Date endDate = assignment.getEndDate();
		float percent = assignment.getPercentAllocated().floatValue();
		float assignedWork = assignment.getWork() != null ? assignment.getWork().floatValue() : 0; 
		
		float dayWorkTime = 8 * percent /100;
		
		Calendar calStart = Calendar.getInstance();
		calStart.setTime(startDate);
		Calendar calEnd = Calendar.getInstance();
		calEnd.setTime(endDate);
		clearTimePartOfCalendar(calStart);
		clearTimePartOfCalendar(calEnd);
		Calendar calFrom = (Calendar)calStart.clone();
		Calendar calTo = (Calendar)calEnd.clone();
		calTo.add(Calendar.DATE, 1);
		float workSum = 0;
		do {
			if (calFrom.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calFrom.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				if (workSum + dayWorkTime < assignedWork){
					
					float currentWork = dayWorkTime;
					if (workTime.get(calFrom.getTime()) != null) {
						currentWork = workTime.get(calFrom.getTime()) + dayWorkTime;
						workTime.remove(calFrom.getTime());
					}					
					workTime.put(calFrom.getTime(), currentWork);					
					workSum = workSum + dayWorkTime;
				} else {
					
					if (workSum != assignedWork){
						float currentWork = assignedWork - workSum;
						if (workTime.get(calFrom.getTime()) != null) {
							currentWork = workTime.get(calFrom.getTime()) + dayWorkTime;
							workTime.remove(calFrom.getTime());
						}					
						workTime.put(calFrom.getTime(), currentWork);					
						workSum = workSum + dayWorkTime;						
					}
					
					workSum = assignedWork;
				}
			}
			calFrom.add(Calendar.DATE, 1);
		} while (calFrom.getTimeInMillis() < calTo.getTimeInMillis());		
		
	}	
	
	/**
	 * Set calendar time to midnight  
	 * 
	 * @param cal - calendar  
	 */
	private void clearTimePartOfCalendar(Calendar cal){
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}
	
	/**
	 * Find all assingments where users are overassigned
	 * (work time on some days is > 8hrs) 
	 * 
	 * @param assignments - users assignments in time period
	 * @return            - only assignments for witch users are overassigned    
	 */
	private List<PnAssignment> findOverAssignments(List<PnAssignment> assignments){
		
		ReportUserWorkTime user = new ReportUserWorkTime(); 
		List<ReportUserWorkTime> users = new ArrayList<ReportUserWorkTime>();
		Integer rptUserId = 0;
		//calculate work time for all selected users by days
		for(PnAssignment assignment : assignments){
			if (rptUserId.intValue() != assignment.getPnPerson().getPersonId().intValue()) {
				rptUserId = assignment.getPnPerson().getPersonId().intValue();
				user = new ReportUserWorkTime();
				user.setUserId(rptUserId);
				user.setResourceName(assignment.getPnPerson().getDisplayName());
				users.add(user);
			}
			getWorkTimeByDays(user.getWorkTimeByDays(), assignment);			
		}
		
		//find all days user worked 8hrs or less and remove them 
		for(ReportUserWorkTime rptUser : users){
			Set<Date> dates = rptUser.getWorkTimeByDays().keySet();
			Iterator<Date> iterDate = dates.iterator();
			List<Date> dateToRemove = new ArrayList<Date>();
			while (iterDate.hasNext()) {
				Date locDate = iterDate.next();
				if (rptUser.getWorkTimeByDays().get(locDate) != null
						&& rptUser.getWorkTimeByDays().get(locDate) <= 8) {
					dateToRemove.add((Date)locDate.clone());
				}
			}
			for (Date date : dateToRemove) {
				rptUser.getWorkTimeByDays().remove(date);
			}			
		}
		
		List<PnAssignment> overAllocatedAssignments = new ArrayList<PnAssignment>();
		//select only assignments that when user are overassigned - there are days with  more then 8hrs work 
	    for (PnAssignment assignment : assignments){
	    	boolean userFound = false;
	    	boolean overAllocated = false;
	    	Iterator<ReportUserWorkTime> iterUser = users.iterator();
	    	Calendar locDate = GregorianCalendar.getInstance();
	    	Calendar startDate = GregorianCalendar.getInstance();
	    	Calendar endDate = GregorianCalendar.getInstance();
	    	while (!userFound && iterUser.hasNext()){
	    		ReportUserWorkTime rptUser = iterUser.next();
	    		if (rptUser.getUserId().intValue() == assignment.getPnPerson().getPersonId().intValue()){
	    			userFound = true;
	    			Set<Date> dates = rptUser.getWorkTimeByDays().keySet();
	    			Iterator<Date> iterDates = dates.iterator();
	    			while (!overAllocated && iterDates.hasNext()){
	    				locDate.setTime(iterDates.next());
	    				clearTimePartOfCalendar(locDate);
	    				startDate.setTime(assignment.getStartDate());
	    				clearTimePartOfCalendar(startDate);
	    				endDate.setTime(assignment.getEndDate());
	    				clearTimePartOfCalendar(locDate);
	    				
	    				overAllocated = startDate.getTime().equals(locDate.getTime()) || 
	    				endDate.getTime().equals(locDate.getTime()) ||
	    								(startDate.getTime().before(locDate.getTime()) && endDate.getTime().after(locDate.getTime()));	    				
	    			}
	    		}
	    	}
	    	if (overAllocated){
	    		overAllocatedAssignments.add(assignment);
	    	}
	    }	
				
		return overAllocatedAssignments;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnAssignmentService#getCurrentOverAssignedResourcesForProject(java.lang.Integer, java.util.Date)
	 */
	public List<PnAssignment> getCurrentOverAssignedResourcesForProject(Integer projectId, Date date) {		
		List<PnAssignment> assignments = pnAssignmentDAO.getCurrentOverAssignedResourcesForProject(projectId, date);
		return findOverAssignments(assignments);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnAssignmentService#getAssignmentsByPersonForProject(java.lang.Integer, java.util.Date, java.util.Date)
	 */
	public List<Teammate> getAssignmentsByPersonForProject(Integer projectId, Date startDate, Date endDate) {
		List<Teammate> teammates = pnAssignmentDAO.getAssignmentsByPersonForProject(projectId, startDate, endDate);
		List<Teammate> notAssigned =  pnAssignmentDAO.getTeammatesWithoutAssignments(projectId,  endDate); 
		Calendar endOfWeek = GregorianCalendar.getInstance();
		endOfWeek.setTime(startDate);
		endOfWeek.add(Calendar.DATE, 7);
		
		List<Teammate>onlineTeammates = pnPersonService.getOnlineMembers(projectId);
		for(Teammate teammate : teammates){
			List<PnAssignment> overassgined = findOverAssignments(teammate.getAssignments());
			teammate.setOverassigned(overassgined.size() > 0);
	
			teammate.setOnline(onlineTeammates.contains(teammate));
			
			List<PnAssignment> toRemove = new ArrayList<PnAssignment>();
			for (PnAssignment assignment : teammate.getAssignments()){
				if(assignment.getComp_id().getSpaceId().intValue() != projectId.intValue()){
					toRemove.add(assignment);
				}
			}
			for(PnAssignment assignment : toRemove){
				teammate.getAssignments().remove(assignment);
			}
			
			for (PnAssignment assignment : teammate.getAssignments()){
				if(assignment.getPercentComplete().doubleValue() < 1 &&  assignment.getEndDate().before(startDate)){
					teammate.setHasLateTasks(true);
				}
				if(assignment.getPercentComplete().doubleValue() < 1 &&   (assignment.getEndDate().equals(startDate) || 
						(assignment.getEndDate().after(startDate) && assignment.getEndDate().before(endOfWeek.getTime())))){
					teammate.setHasTaskDueThisWeek(true);
				}
			}							
		}
		for (Teammate teammate : notAssigned){
			teammate.setOnline(onlineTeammates.contains(teammate));
			if(!teammates.contains(teammate)){
				teammates.add(teammate);
			}
		}
		
        Collections.sort(teammates, new Comparator<Teammate>() {
            public int compare(Teammate t1, Teammate t2) {
                return t1.getDisplayName().toLowerCase().compareTo(t2.getDisplayName().toLowerCase());
            }
        });		
		
        
        
		return teammates;
	}
	
	
	public List<ReportUser> getAssignedProjectsByBusiness(String userId, Integer businessId,  Date startDate, Date endDate){
		return pnAssignmentDAO.getAssignedProjectsByBusiness(userId, businessId, startDate, endDate);
	}

//    public List<Assignment> getAssigmentsByAssignor(Integer assignorId) {
//        return pnAssignmentDAO.getAssigmentsByAssignor(assignorId);
//    }
    
    public List<PnAssignment> getAssignorAssignmentDetails(Integer assignorId){
    	return pnAssignmentDAO.getAssignorAssignmentDetails(assignorId);
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.service.IPnAssignmentService#getTotalAssignmentCountWithFilters(java.lang.Integer, java.lang.String, java.lang.Integer[], java.lang.Integer, java.lang.String[], boolean, boolean, boolean, boolean, java.util.Date, java.util.Date, java.lang.Integer, java.lang.Double, java.lang.String, java.lang.String, java.lang.String)
     */
    public Integer getTotalAssignmentCountWithFilters(
    		Integer personId,
    		String assigneeORAssignor,
    		Integer[] projectIds,
    		Integer businessId,
    		String[] assignmentTypes,
    		boolean lateAssignment,
    		boolean comingDueDate,
    		boolean shouldHaveStart,
    		boolean InProgress,
    		Date startDate,
    		Date endDate,
    		Integer statusId,
    		Double percentComplete,
    		String PercentCompleteComparator,
    		String assignmentName,
    		String assignmentNameComparator
    ) {
    	return pnAssignmentDAO.getTotalAssignmentCountWithFilters(personId, assigneeORAssignor, projectIds, businessId,
				assignmentTypes, lateAssignment, comingDueDate, shouldHaveStart, InProgress, startDate, endDate,
				statusId, percentComplete, PercentCompleteComparator, assignmentName, assignmentNameComparator);
	}
    
    
    public List<PnAssignment> getAssignmentDetailsWithFilters (
    		Integer[] personIds,
    		String assigneeORAssignor,
    		Integer[] projectIds,
    		Integer businessId,
    		String[] assignmentTypes,
    		boolean lateAssignment,
    		boolean comingDueDate,
    		boolean shouldHaveStart,
    		boolean InProgress,
    		Date startDate,
    		Date endDate,
    		Integer statusId,
    		Double percentComplete,
    		String PercentCompleteComparator,
    		String assignmentName,
    		String assignmentNameComparator,
    		int offset, 
    		int range,
    		boolean isOrderByPerson
    ) {
    		List<PnAssignment> assignmentList = pnAssignmentDAO.getAssignmentDetailsWithFilters(personIds, assigneeORAssignor,
					projectIds, businessId, assignmentTypes, lateAssignment, comingDueDate, shouldHaveStart,
					InProgress, startDate, endDate, statusId, percentComplete, PercentCompleteComparator,
					assignmentName, assignmentNameComparator, offset, range, isOrderByPerson);
    		
    		if(startDate == null && endDate == null && !isOrderByPerson) {
    			return removeOldMeetings(assignmentList, percentComplete, PercentCompleteComparator, lateAssignment, comingDueDate, shouldHaveStart, InProgress);
    		} else {
	    		return assignmentList;
    		}
	}
    
	public PnAssignment getAssigmentByAssignmentId(Integer objectId, Integer userId) {
		return pnAssignmentDAO.getAssigmentByAssignmentId(objectId, userId);
	}
	
	/**
	 * Get assignment for spacified objectId and personId. 
	 * @param objectId
	 * @param userId
	 * @return PnAssignment.
	 */
	public PnAssignment getPersonAssignmentForObject(Integer objectId, Integer personId){
		return pnAssignmentDAO.getPersonAssignmentForObject(objectId, personId);
	}
	
	/** 
	 *  Remove past meetings from my assignments 
	 * @param list
	 * @param percentComplete
	 * @param PercentCompleteComparator
	 * @return modified assignment list  
	 */
	public List<PnAssignment> removeOldMeetings(List<PnAssignment> list, Double percentComplete, String PercentCompleteComparator,
								boolean lateAssignment, boolean comingDueDate, boolean shouldHaveStart, boolean InProgress) {
		Date todayDate = utilService.clearTimePart(new Date());
		List<PnAssignment> completeList = new ArrayList<PnAssignment>();
		completeList.addAll(list);
		if(CollectionUtils.isNotEmpty(list)) {
			try{
				PnAssignment assignment = null;
				for(int mIndex = 0; mIndex < list.size(); mIndex++) {
					assignment = list.get(mIndex);
					Date startDate = assignment.getStartDate();
					Date endDate = assignment.getEndDate();
					if(assignment.getPnObjectType().getObjectType().equals(ObjectType.MEETING)) {
						if(percentComplete != null && percentComplete != .99 
									&& PercentCompleteComparator != null && !"greaterthan".equals(PercentCompleteComparator)) {
							if(startDate == null || endDate == null) {
								completeList.remove(assignment);
							} else {
								if(utilService.clearTimePart(startDate).before(todayDate))
									completeList.remove(assignment);
								if(assignment.getStatusId() == Integer.parseInt(AssignmentStatus.COMPLETED_PENDING.getID())
										|| assignment.getStatusId() == Integer.parseInt(AssignmentStatus.IN_PROCESS.getID())) {  
									completeList.remove(assignment);
								}
							}
						}
						if((lateAssignment || comingDueDate || shouldHaveStart || InProgress) && assignment.getStatusId() == Integer.parseInt(AssignmentStatus.IN_PROCESS.getID())) {
							completeList.remove(assignment);
						}
					}
				}
			}catch (Exception e) {
				Logger.getLogger(PnAssignmentServiceImpl.class).error("PnAssignmentServiceImpl.removeOldMeetings() failed..."+e.getMessage());
			}
		}
		return completeList;
	}
 }
