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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.project.hibernate.dao.IPnProjectSpaceDAO;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.model.project_space.ProjectChanges;
import net.project.hibernate.model.project_space.ProjectPhase;
import net.project.hibernate.model.project_space.ProjectSchedule;
import net.project.hibernate.service.IPnProjectSpaceService;

@Service(value="pnProjectSpaceService")
public class PnProjectSpaceServiceImpl implements IPnProjectSpaceService {

	@Autowired
	private IPnProjectSpaceDAO pnProjectSpaceDAO;

	/**
	 * @param pnProjectSpaceDAO The pnProjectSpaceDAO to set.
	 */
	public void setPnProjectSpaceDAO(IPnProjectSpaceDAO pnProjectSpaceDAO) {
		this.pnProjectSpaceDAO = pnProjectSpaceDAO;
	}      


	public PnProjectSpace getProjectSpace(Integer objectId) {
		return this.pnProjectSpaceDAO.findByPimaryKey(objectId);
	}

	public void saveProjectSpace(PnProjectSpace object) {
		this.pnProjectSpaceDAO.create(object);
	}

	public List<PnProjectSpace> getProjectsByUserId(Integer userId) {
		return this.pnProjectSpaceDAO.getProjectsByMemberId(userId);
	}

	public List<PnProjectSpace> getProjectsByBusinessId(Integer businessId) {
		return this.pnProjectSpaceDAO.getProjectsByBusinessId(businessId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnProjectSpaceService#getProjectsByMemberId(java.lang.Integer)
	 */
	public List<PnProjectSpace> getProjectsByMemberId(Integer userId) {	
		return this.pnProjectSpaceDAO.getProjectsByMemberId(userId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnProjectSpaceService#getProjectsVisbleByUser(java.lang.Integer)
	 */
	public List<PnProjectSpace> getProjectsVisbleByUser(Integer userId) {
		return this.pnProjectSpaceDAO.getProjectsVisbleByUser(userId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnProjectSpaceService#getMemberProjectsNotVisbleByUser(java.lang.Integer, java.lang.Integer)
	 */
	public List<PnProjectSpace> getMemberProjectsNotVisbleByUser(Integer memberId, Integer userId) {
		List<PnProjectSpace> userProjects = this.pnProjectSpaceDAO.getProjectsVisbleByUser(userId);
		
		List<PnProjectSpace> memberProjects = this.pnProjectSpaceDAO.getProjectsVisbleByUser(memberId);
		
		List<PnProjectSpace> result = new ArrayList<PnProjectSpace>();
		
		for (PnProjectSpace project : memberProjects){
			if (! userProjects.contains(project)){
				result.add(project);
			}
		}
		
		return result;
	}

	public void updateProjectSpace(PnProjectSpace object) {
	    this.pnProjectSpaceDAO.update(object);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnProjectSpaceService#getProjectPhasesAndMilestones(java.lang.Integer)
	 */
	public List<ProjectPhase> getProjectPhasesAndMilestones(Integer projectId) {
		
		return pnProjectSpaceDAO.getProjectPhasesAndMilestones(projectId);
	}


	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnProjectSpaceService#getProjectSchedule(java.lang.Integer)
	 */
	public ProjectSchedule getProjectSchedule(Integer projectId) {
		return pnProjectSpaceDAO.getProjectSchedule(projectId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnProjectSpaceService#getProjectChanges(java.lang.Integer, java.lang.Integer)
	 */
	public ProjectChanges getProjectChanges(Integer projectId, Integer numberOfDays) {		
		return pnProjectSpaceDAO.getProjectChanges(projectId, numberOfDays);
	}	

	public List<PnProjectSpace> getAssignedProjectsByResource(Integer businessId, Integer resourceId, Date startDate, Date endDate){
		return pnProjectSpaceDAO.getAssignedProjectsByResource(businessId, resourceId, startDate, endDate);
	}

	public PnProjectSpace getProjectSpaceDetails(Integer projectId) {
		return pnProjectSpaceDAO.getProjectSpaceDetails(projectId);
	}


	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnProjectSpaceService#getAllProjects()
	 */
	public List<PnProjectSpace> getAllProjects() {
		return pnProjectSpaceDAO.getAllProjects();
	}
	
	public List<PnProjectSpace> getSubProjectsByProejctId(Integer projectId) {
		return pnProjectSpaceDAO.getSubProjectsByProejctId(projectId);
	}
	
	public List<PnProjectSpace> getProjectsVisibleToUser(Integer memberId, Integer userId){
		List<PnProjectSpace> visibleProjects = null;
		// Getting list of the user's projects
		List<PnProjectSpace> userProjects = getProjectsByMemberId(memberId);
		
		// Getting projects which current, logged in, user is member of
		List<PnProjectSpace> loggedUserProjects = getProjectsByMemberId(userId);

		// Getting projects which are visible to user logged in
		if (userProjects != null && loggedUserProjects != null) {
			visibleProjects = new ArrayList<PnProjectSpace>();
			for (PnProjectSpace projectSpace : userProjects) {
				if (loggedUserProjects.contains(projectSpace)) {
					visibleProjects.add(projectSpace);
				}
			}
		}
		return visibleProjects;
	}


	public PnProjectSpace getWikiIdByProjectId(Integer projectId) {
		return pnProjectSpaceDAO.getWikiIdByProjectId(projectId);
	}


	public List<PnProjectSpace> getActiveProjectIds() {
		return pnProjectSpaceDAO.getActiveProjectIds();
	}
    
    /* (non-Javadoc)
     * @see net.project.hibernate.service.IPnProjectSpaceService#getProjectDetailsByWeblogEntryId(java.lang.Integer)
     */
    public PnProjectSpace getProjectDetailsByWeblogEntryId(Integer weblogEntryId){
        return pnProjectSpaceDAO.getProjectDetailsByWeblogEntryId(weblogEntryId);
    }

	public boolean doesProjectExist(Integer projectId) {
		return pnProjectSpaceDAO.doesProjectExist(projectId);
	}
	
	/* (non-Javadoc)
     * @see net.project.hibernate.service.IPnProjectSpaceService#getProjectDetailsWithRecordStatus(java.lang.Integer)
     */
    public PnProjectSpace getProjectDetailsWithRecordStatus(Integer projectId){
        return pnProjectSpaceDAO.getProjectDetailsWithRecordStatus(projectId);
    }

    /* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnProjectSpaceService#getProjectsByUserIdWithParentProjectId(java.lang.Integer)
	 */
    public List<PnProjectSpace> getProjectsByUserIdWithParentProjectId(Integer userId){
		return pnProjectSpaceDAO.getProjectsByUserIdWithParentProjectId(userId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnProjectSpaceService#isRootProject(java.lang.Integer)
	 */
	public boolean isRootProject(Integer projectId) {
		return pnProjectSpaceDAO.isRootProject(projectId);
	}


	@Override
	public Float getBudgetedTotalCost(String spaceId) {
		return pnProjectSpaceDAO.getBudgetedTotalCost(Integer.valueOf(spaceId));
	}
	
	@Override
	public String getDefaultCurrency(String spaceId){
		return pnProjectSpaceDAO.getDefaultCurrencyCode(Integer.valueOf(spaceId));
	}
}
