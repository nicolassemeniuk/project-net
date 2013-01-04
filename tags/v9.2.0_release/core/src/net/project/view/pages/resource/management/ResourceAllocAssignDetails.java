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
package net.project.view.pages.resource.management;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.project.calendar.workingtime.IWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.ScheduleWorkingTimeCalendarProvider;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.hibernate.model.PnAssignment;
import net.project.hibernate.model.PnPersonAllocation;
import net.project.hibernate.service.IPnAssignmentService;
import net.project.hibernate.service.IPnPersonAllocationService;
import net.project.hibernate.service.IUtilService;
import net.project.hibernate.service.ServiceFactory;
import net.project.hibernate.service.impl.PnAssignmentServiceImpl;
import net.project.project.ProjectSpace;
import net.project.schedule.Schedule;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.NumberFormat;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Persist;

/**
 * @author
 */
public class ResourceAllocAssignDetails {

	private static Logger log;

	private Integer resourceId;

	private Integer projectId;

	private List<PnAssignment> assignments;

	private PnAssignment pnAssignment;

	private String resourceName;

	private String monthYear;

	private BigDecimal totalWorkAssigned;

	private BigDecimal hoursAllocated;
    
	private WorkingTimeCalendarDefinition  workingTimeCalendarDefinition;
    
    @Persist
    private Map<Integer, IWorkingTimeCalendarProvider> workingTimeCalendarProviderByProjectID = new HashMap <Integer, IWorkingTimeCalendarProvider> ();

	public ResourceAllocAssignDetails() {
		try {
			log = Logger.getLogger(ResourceAllocAssignDetails.class);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}

	public void onActivate(String parameters) {
		String[] urlParams = parameters.split("&");
		String month = urlParams[2];
		String year = urlParams[3];
		Date startDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		String startDateString = "01/" + (month.length() == 2 ? month : "0" + month) + "/" + year;

		DateFormat userDateFormat = SessionManager.getUser().getDateFormatter();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {
			startDate = simpleDateFormat.parse(startDateString);
		} catch (Exception e) {
			log.error("Error occured while parsing the data " + e.getMessage());
		}

		setMonthYear(new SimpleDateFormat("MMMM").format(startDate) + ' ' + year);

		cal.setTime(startDate);
		cal.add(Calendar.DATE, -1);
		startDate = cal.getTime();
		cal.add(Calendar.DATE, 2);
		cal.add(Calendar.MONTH, 1);
		endDate = cal.getTime();

		this.resourceId = Integer.parseInt(urlParams[0]);
		this.projectId = Integer.parseInt(urlParams[1]);

		Integer[] projectIds = new Integer[1];
		projectIds[0] = projectId;

		IPnPersonAllocationService allocationService = ServiceFactory.getInstance().getPnPersonAllocationService();
		PnPersonAllocation personAllocation = allocationService.getResourceAllocationDetails(resourceId, projectId,
				startDate, endDate);

		hoursAllocated = personAllocation.getHoursAllocated();

		IPnAssignmentService assignmentService = ServiceFactory.getInstance().getPnAssignmentService();
		List<PnAssignment> assignmentsData = assignmentService.getResourceAssignmentDetails(resourceId, projectIds,
				startDate, endDate);

		cal.setTime(startDate);
		cal.add(Calendar.DATE, 2);
		List<PnAssignment> remainingAssignments = assignmentService.getResourceAssignmentDetails(resourceId,
				projectIds, cal.getTime(), cal.getTime());
        
        //initialize working time calendar for poroject and resource.
        initializeWorkingTimeCalender(projectId, resourceId);

		if (assignmentsData != null && remainingAssignments != null) {
			if (assignmentsData.size() > 0) {
				List<PnAssignment> prevAssignments = new ArrayList<PnAssignment>();
				prevAssignments.addAll(assignmentsData);
				for (PnAssignment remAssignment : remainingAssignments) {
					boolean taskInList = false;
					for (PnAssignment prevAssignment : prevAssignments) {
						if (prevAssignment.getPnTask().getTaskId().intValue() == remAssignment.getPnTask().getTaskId()
								.intValue()) {
							taskInList = true;
							break;
						}
					}
					if (!taskInList) {
						assignmentsData.add(remAssignment);
						taskInList = false;
					}
				}
			} else {
				assignmentsData.addAll(remainingAssignments);
			}
		}

		// calculating actual sart/end date
		cal.setTime(startDate);
		cal.add(Calendar.DATE, 1);
		startDate = cal.getTime();
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		endDate = cal.getTime();

		totalWorkAssigned = BigDecimal.valueOf(0);

		if (assignmentsData != null) {
			assignments = new ArrayList<PnAssignment>();
			for (PnAssignment assignmentDetail : assignmentsData) {
				Date assignFrom = null;
				Date assignTo = null;
				try {
					assignFrom = simpleDateFormat.parse(userDateFormat.formatDate(assignmentDetail.getStartDate(),
							"dd/MM/yyyy"));
					assignTo = simpleDateFormat.parse(userDateFormat.formatDate(assignmentDetail.getEndDate(),
							"dd/MM/yyyy"));
				} catch (Exception e) {
					log.error("Error occured while parsing dates in allocAssignDetail:" + e.getMessage());
				}

				if (!(assignTo.before(startDate) || assignFrom.after(endDate))) {// if in selected date criteria
					if (getResourceName() == null) {
						setResourceName(assignmentDetail.getPersonName());
					}
					assignmentDetail.setStartDateString(userDateFormat.formatDate(assignmentDetail.getStartDate()));
					assignmentDetail.setEndDateString(userDateFormat.formatDate(assignmentDetail.getEndDate()));

					IUtilService utilService = ServiceFactory.getInstance().getUtilService();

					if (assignmentDetail.getWork() != null) {
						cal.setTime(startDate);
						cal.add(Calendar.DATE, -1);// before one day of start date
						float notCalulatedWork = 0f;

						if (assignFrom.before(cal.getTime()) || assignFrom.equals(cal.getTime())) {
							notCalulatedWork = (float) (utilService.getWorkingHours(assignFrom, cal.getTime(), this.workingTimeCalendarDefinition)
                                            * assignmentDetail.getPercentAllocated().doubleValue() / 100.0);
						}
						float workignHours = 0;
						if ((startDate.after(assignFrom) || startDate.equals(assignFrom))
								&& (endDate.before(assignTo) || endDate.equals(assignTo))) {
                            workignHours = utilService.getWorkingHours(startDate, endDate, this.workingTimeCalendarDefinition);
						}
						if (startDate.before(assignFrom) && (endDate.before(assignTo) || endDate.equals(assignTo))) {
                            workignHours = utilService.getWorkingHours(assignFrom, endDate, this.workingTimeCalendarDefinition);
						}
						if ((startDate.after(assignFrom) || startDate.equals(assignFrom)) && endDate.after(assignTo)) {
                            workignHours = utilService.getWorkingHours(startDate, assignTo, this.workingTimeCalendarDefinition);
						}
						if (startDate.before(assignFrom) && endDate.after(assignTo)) {
                            workignHours = utilService.getWorkingHours(assignFrom, assignTo, this.workingTimeCalendarDefinition);
						}
						float work = (float) (workignHours * assignmentDetail.getPercentAllocated().doubleValue() / 100.0);

						if (assignmentDetail.getWork().floatValue() < notCalulatedWork + work) {
							if (notCalulatedWork > assignmentDetail.getWork().floatValue()) {
								notCalulatedWork = assignmentDetail.getWork().floatValue();
							}
							totalWorkAssigned = totalWorkAssigned.add(BigDecimal.valueOf(assignmentDetail.getWork().floatValue() - notCalulatedWork));
						} else {
							totalWorkAssigned = totalWorkAssigned.add(BigDecimal.valueOf(work));
						}
					}

					float workRemaining = 0f;
					if (assignmentDetail.getPercentComplete() != null && assignmentDetail.getWork() != null) {
						workRemaining = assignmentDetail.getWork().floatValue()
								- (float)((assignmentDetail.getWork().floatValue() * assignmentDetail.getPercentComplete().floatValue()) / 100.0);
					}
					// setting work complete field with work remaining after
					// calculating as below
					// [without inserting new field for work remaining in model
					// class]
					if(workRemaining == Math.round(workRemaining)){
						assignmentDetail.setWorkComplete(BigDecimal.valueOf(Math.round(workRemaining)));
					} else {
						assignmentDetail.setWorkComplete(BigDecimal.valueOf(Double.parseDouble(NumberFormat.getInstance().formatNumber(workRemaining, 0, 2))));
					}

					if (assignmentDetail.getWork() != null
							&& assignmentDetail.getWork().floatValue() == assignmentDetail.getWorkComplete().floatValue()) {
						assignmentDetail.setRowClass("#99FFFF");
					} else {
						assignmentDetail.setRowClass("#FFFFCC");
					}
					assignments.add(assignmentDetail);
				}
			}
		}
	}
    
    /** Initializing WorkingTimeCalendarDefinition as per project and resource.
     * @param spaceID
     * @param resourcePersonID
     */
    private void initializeWorkingTimeCalender(Integer projectId, Integer resourceID){
        //Here the WorkingTimeCalendarDefinition can be initialized multiple time for same project, therefore we are using a map to avoid multiple Initialization.
        //Initialzing ScheduleWorkingTimeCalendarProvider is a very heavy process so if it is already initialized for particular project do not initalize again get it from map. 
        IWorkingTimeCalendarProvider workingTimeCalendarProvider = (IWorkingTimeCalendarProvider)this.workingTimeCalendarProviderByProjectID.get(projectId);
        
        //If ScheduleWorkingTimeCalendarProvider is null, Make it and put it in to the map.   
        if (workingTimeCalendarProvider == null) {
            Schedule schedule = new Schedule();
            schedule.setSpace(new ProjectSpace(projectId.toString()));
            try {
                schedule.loadAll();
                workingTimeCalendarProvider = ScheduleWorkingTimeCalendarProvider.make(schedule);
                this.workingTimeCalendarProviderByProjectID.put(projectId, workingTimeCalendarProvider);
            } catch (Exception pnetEx) {
                Logger.getLogger(PnAssignmentServiceImpl.class).error("Error occured while loading schedule or initializing workingTimeCalendarProvider "+pnetEx.getMessage());
            }
        }
        // get workingTimeCalendarDefinition for resourcePersonID.
        if (workingTimeCalendarProvider != null) {
            this.workingTimeCalendarDefinition = workingTimeCalendarProvider.getForResourceID(resourceID.toString());
        }
    }
    
	/**
	 * @return the assignments
	 */
	public List<PnAssignment> getAssignments() {
		return assignments;
	}

	/**
	 * @param assignments
	 *            the assignments to set
	 */
	public void setAssignments(List<PnAssignment> assignments) {
		this.assignments = assignments;
	}

	/**
	 * @return the pnAssignment
	 */
	public PnAssignment getPnAssignment() {
		return pnAssignment;
	}

	/**
	 * @param pnAssignment
	 *            the pnAssignment to set
	 */
	public void setPnAssignment(PnAssignment pnAssignment) {
		this.pnAssignment = pnAssignment;
	}

	/**
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}

	/**
	 * @param resourceName
	 *            the resourceName to set
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	/**
	 * @return the monthYear
	 */
	public String getMonthYear() {
		return monthYear;
	}

	/**
	 * @param monthYear
	 *            the monthYear to set
	 */
	public void setMonthYear(String monthYear) {
		this.monthYear = monthYear;
	}

	/**
	 * @return the hoursAllocated
	 */
	public BigDecimal getHoursAllocated() {
		return hoursAllocated;
	}

    /**
     * @return the hoursAllocated
     */
    public String getHoursAllocatedString() {
        return NumberFormat.getInstance().formatPercent(this.hoursAllocated.floatValue(), "###.##");
    }

	/**
	 * @return the totalWorkAssigned
	 */
	public BigDecimal getTotalWorkAssigned() {
		return totalWorkAssigned;
	}
    
    /**
     * @return the totalWorkAssigned
     */
    public String getTotalWorkAssignedString() {
        return NumberFormat.getInstance().formatPercent(this.totalWorkAssigned.floatValue(), "###.##");
    }


}
