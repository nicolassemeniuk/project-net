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
import java.util.List;

import net.project.hibernate.model.PnAssignment;
import net.project.hibernate.model.resource_reports.ReportMonth;
import net.project.hibernate.model.resource_reports.ReportUser;
import net.project.hibernate.model.resource_reports.ReportUserProjects;
import net.project.hibernate.service.IPnAssignmentService;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.util.DateFormat;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Persist;

/**
 * @author
 */
public class ResourceAssignmentDetail {

	private static Logger log;

	private Integer resourceId;

	private Integer projectId;

	private List<PnAssignment> assignments;

	private PnAssignment pnAssignment;

	private String resourceName;

	private String monthYear;

	@Persist
	private List<ReportUser> assignedProjects;

	/**
	 * 
	 */
	public ResourceAssignmentDetail() {
		try {
			log = Logger.getLogger(ResourceAssignmentDetail.class);
			
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
		Integer[] projectIds = null;

		if (projectId != 0) {
			projectIds = new Integer[1];
			projectIds[0] = projectId;
		} else {
			if (assignedProjects != null) {
				for (ReportUser reportUser : assignedProjects) {
					if (reportUser.getUserId().intValue() == resourceId.intValue()) {
						projectIds = new Integer[reportUser.getProjektList().size()];
						int index = 0;
						for (ReportUserProjects project : reportUser.getProjektList()) {
							for (ReportMonth reportMonth : project.getMonthList()) {
								if (reportMonth.getYear().equals(Integer.valueOf(year))
										&& reportMonth.getMonthInYear().equals(Integer.valueOf(month) - 1)
										&& reportMonth.getTotalWork() != 0.0) {
									projectIds[index++] = project.getProjectId();
								} // if
							} // for ReportMonth
						} // for ReportUserProjects
					} // if
				} // for ReportUser
			} // if
		} // else

		IPnAssignmentService assignmentService = ServiceFactory.getInstance().getPnAssignmentService();
		List<PnAssignment> assignmentsData = assignmentService.getResourceAssignmentDetails(resourceId, projectIds,
				startDate, endDate);

		if (projectIds != null) {
			cal.setTime(startDate);
			cal.add(Calendar.DATE, 2);
			
			List<PnAssignment> remainingAssignments = assignmentService.getResourceAssignmentDetails(resourceId,
					projectIds, cal.getTime(), cal.getTime());

			if (assignmentsData != null && remainingAssignments != null) {
				if (assignmentsData.size() > 0) {
					List<PnAssignment> prevAssignments = new ArrayList<PnAssignment>();
					prevAssignments.addAll(assignmentsData);
					for (PnAssignment remAssignment : remainingAssignments) {
						boolean taskInList = false;
						for (PnAssignment prevAssignment : prevAssignments) {
							if (prevAssignment.getPnTask().getTaskId().intValue() == remAssignment.getPnTask()
									.getTaskId().intValue()) {
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
		}
		
		//cacuating actual start/end date
		cal.setTime(startDate);
		cal.add(Calendar.DATE, 1);
		startDate = cal.getTime();
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		endDate = cal.getTime();

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

					float workRemaining = 0f;

					if (assignmentDetail.getPercentComplete() != null && assignmentDetail.getWork() != null) {
						workRemaining = assignmentDetail.getWork().floatValue()
									- (float) ((assignmentDetail.getWork().floatValue() * assignmentDetail
										.getPercentComplete().floatValue()) / 100.0);
					}
					// setting work complete field with work remaining after
					// calculating as below
					// [without inserting new field for work remaining in model
					// class]
					assignmentDetail.setWorkComplete(BigDecimal.valueOf(workRemaining));

					if (assignmentDetail.getWork() != null
							&& assignmentDetail.getWork().floatValue() == assignmentDetail.getWorkComplete().floatValue()) {
						assignmentDetail.setRowClass("#99FFFF");
					} else {
							assignmentDetail.setWork(BigDecimal.ZERO);
							assignmentDetail.setRowClass("#FFFFCC");
						}
					assignments.add(assignmentDetail);
				}
			}
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
	 * @return the assignedProjects
	*/
	public List<ReportUser> getAssignedProjects() {
		return assignedProjects;
	}

	/**
	 * @param assignedProjects
	 *            the assignedProjects to set
	 * */
	 
	public void setAssignedProjects(List<ReportUser> assignedProjects) {
		this.assignedProjects = assignedProjects;
	}
	

}
