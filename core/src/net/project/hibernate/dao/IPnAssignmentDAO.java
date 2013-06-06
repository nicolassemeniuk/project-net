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
package net.project.hibernate.dao;

import java.util.Date;
import java.util.List;

import net.project.hibernate.model.PnAssignment;
import net.project.hibernate.model.PnAssignmentPK;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.model.resource_reports.ReportUser;


public interface IPnAssignmentDAO extends IDAO<PnAssignment, PnAssignmentPK> {	

	public List<PnAssignment> getAssigmentsList(Integer[] personIds, Date startDate, Date endDate);	
	
	public List<PnAssignment> getCurrentAssigmentsListForProject(Integer projectId, Integer[] personIds, Date date);

	public List getWorkSumByMonthForUsers(Integer[] personIds, Integer[] projectIds, 
			Date startDate, Date endDate);
	
	@Deprecated
	public List getResourceAssignmentSummary(Integer businessId, Integer portfolioId, Date startDate, Date endDate);
	
	public List getResourceAssignmentSummaryByBusiness(Integer resourceId, Integer businessId, Date startDate, Date endDate);
	
	public List<PnAssignment> getResourceAssignmentDetails(Integer resourceId, Integer[] projectIds, Date startDate, Date endDate);
	
	public List<PnAssignment> getOverAllocatedResources(Integer userId);
	
	public List getWorkSumByMonthForBusiness(Integer resourceId, Integer businessId, Date startDate, Date endDate);
	
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
	
	/**
	 * Get all teammates in project without any active assignment until
	 * specigfied date 
	 * 
	 * @param projectId - project identity 
	 * @param date   - end date
	 * @return          - return list of teammates 
	 */
	public List<Teammate> getTeammatesWithoutAssignments(Integer projectId, Date date);

//    public List<Assignment> getAssigmentsByAssignor(Integer assignorId);
    
    public List<PnAssignment> getAssignorAssignmentDetails(Integer assignorId);
    
    /**
     * Get all assignment accorging to filter criteria
     * @param personIds
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
     * @return Assignments list
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
    
    public Integer getTotalAssignmentCountWithFilters(
    		Integer personId,
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
    		String assignmentNameComparator
    );

	public PnAssignment getAssigmentByAssignmentId(Integer objectId, Integer userId);
	
	/**
	 * Get assignment for spacified objectId and personId. 
	 * @param objectId
	 * @param userId
	 * @return PnAssignment.
	 */
	public PnAssignment getPersonAssignmentForObject(Integer objectId, Integer personId);

	public List<PnAssignment> getAssignmentList(Integer projectId);
}

