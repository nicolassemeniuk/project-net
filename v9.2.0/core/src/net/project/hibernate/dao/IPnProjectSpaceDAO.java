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

import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.model.project_space.ProjectChanges;
import net.project.hibernate.model.project_space.ProjectPhase;
import net.project.hibernate.model.project_space.ProjectSchedule;


public interface IPnProjectSpaceDAO extends IDAO<PnProjectSpace, Integer> {
	
	public List<PnProjectSpace> getProjectsByUserId(Integer userId);
	
	public List<PnProjectSpace> getProjectsByBusinessId(Integer userId);
	
	public List<PnProjectSpace> getProjectsByMemberId(Integer userId);
	
	public List<PnProjectSpace> getProjectsVisbleByUser(Integer userId);
	
	public List<ProjectPhase> getProjectPhasesAndMilestones(Integer projectId);
	
	public ProjectSchedule getProjectSchedule(Integer projectId);

	public ProjectChanges getProjectChanges(Integer projectId, Integer numberOfDays);
	
	public List<PnProjectSpace> getAssignedProjectsByResource(Integer businessId, Integer resourceId, Date startDate, Date endDate);


	public PnProjectSpace getProjectSpaceDetails(Integer projectId);
	
	public List<PnProjectSpace> getAllProjects();
	
	public List<PnProjectSpace> getSubProjectsByProejctId(Integer projectId);
	
	public PnProjectSpace getWikiIdByProjectId(Integer projectId);
	
	public List<PnProjectSpace> getActiveProjectIds();
    
    /**
     * Get project details of weblog entry
     * @param weblogEntryId
     * @return PnProjectSpace
     */
    public PnProjectSpace getProjectDetailsByWeblogEntryId(Integer weblogEntryId);
    
    public boolean doesProjectExist(Integer projectId);
    
    /**
     * Get project details using projectId
     * @param projectId
     * @return Object of PnProjectSpace
     */
    public PnProjectSpace getProjectDetailsWithRecordStatus(Integer projectId);
    
    /**
     * Get project list using userId
     * @param userId
     * @return List Containing Objects of PnProjectSpace
     */

    public List<PnProjectSpace> getProjectsByUserIdWithParentProjectId(Integer userId);
    	
	/**
	 * To check project is a root project
	 * @param spaceId
	 * @return
	 */
	public boolean isRootProject(Integer spaceId);

}
