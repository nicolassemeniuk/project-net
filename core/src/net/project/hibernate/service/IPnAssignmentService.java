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
package net.project.hibernate.service;

import java.util.Date;
import java.util.List;

import net.project.hibernate.model.PnAssignment;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.model.resource_reports.ReportUser;
import net.project.util.DateFormat;


public interface IPnAssignmentService {
	

	public List<PnAssignment> getAssigmentsList(Integer[] personIds, Date startDate, Date endDate);
	
	public List<PnAssignment> getCurrentAssigmentsListForProject(Integer projectId, Integer[] personIds, Date date);
	
	/**
	 * Obtain the list of all assignments from a project.
	 * @param projectId the id from the project.
	 * @return a list of assignments.
	 */
	public List<PnAssignment> getAssigmentsListForProject(Integer projectId);
	
	@Deprecated
	public List<ReportUser> getWorkSumByMonthForUsers(Integer[] personIds, Integer[] projectIds, Date startDate, Date endDate);
	
	public List<ReportUser> getWorkSumByMonthForUsers(Integer resourceId, Integer businessId, Date startDate, Date endDate, DateFormat userDateFormatter);
	
	public List<PnAssignment> getResourceAssignmentDetails(Integer resourceId, Integer[] projectIds, Date startDate, Date endDate);
	
	public List getResourceAssignmentSummaryByBusiness(Integer resourceId, Integer businessId, Integer projectId, Date startDate, Date endDate, DateFormat userDateFormatter);
	
	public List<PnAssignment> getOverAllocatedResources(Integer userId);
		
	public List<ReportUser> getAssignmentVsAllocation(Integer resourceId, Integer businessId, Date startDate, Date endDate, DateFormat userDateFormatter);
	
	public List<PnAssignment> getOverAssignedResources(Integer userId, Date startDate, Date endDate);
	
	public List<ReportUser> getAssignedProjectsByBusiness(String userId, Integer businessId,  Date startDate, Date endDate);
	
	public List<PnAssignment> getCurrentOverAssignedResourcesForProject(Integer projectId, Date date);
	
	/**
	 * For selected project returns list of all persons assigned for
	 * project with all assignments including assignment from all other projects
	 * assignments from other project are needed to find overassigned persons  
	 * 
	 * @param projectId - project identity 
	 * @param startDate - start date
	 * @param endDate   - end date
	 * @return          - return list of teammates with associated assignments
	 */
	public List<Teammate> getAssignmentsByPersonForProject(Integer projectId, Date startDate, Date endDate);	

//    public List<Assignment> getAssigmentsByAssignor(Integer assignorId);
    
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
    );
    
    /**
     *  Get Assignment details for selected user ids.
     * @param personId
     * @param assigneeORAssignor
     * @param projectIds
     * @param businessId
     * @param assignmentTypes
     * @param lateAssignment
     * @param comingDueDate
     * @param shouldHaveStart
     * @param inProgress
     * @param startDate
     * @param endDate
     * @param statusId
     * @param percentComplete
     * @param PercentCompleteComparator
     * @param assignmentName
     * @param assignmentNameComparator
     * @param offset
     * @param range
     * @param isOrderByPerson
     * @return list of teammates with associated assignments
     */
    public List<PnAssignment> getAssignmentDetailsWithFilters(
    		Integer[] personIds,
    		String assigneeORAssignor,
    		Integer[] projectIds,
    		Integer businessId,
    		String[] assignmentTypes,
    		boolean lateAssignment,
    		boolean comingDueDate,
    		boolean shouldHaveStart,
    		boolean inProgress,
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
    );

	public PnAssignment getAssigmentByAssignmentId(Integer objectId, Integer userId);
	
	/**
	 * Get assignment for spacified objectId and personId. 
	 * @param objectId
	 * @param userId
	 * @return PnAssignment.
	 */
	public PnAssignment getPersonAssignmentForObject(Integer objectId, Integer personId);

	
	/**
	 * Get all the person assignments for a certain object.
	 * @param objectID the id of the object.
	 * @return a list of Assignments.
	 */
	public List<PnAssignment> getAssigmentsByObjectId(Integer objectID);
}
