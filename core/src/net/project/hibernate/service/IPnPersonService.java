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

import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.model.resource_reports.ReportProjectUsers;

public interface IPnPersonService {
	
	/**
	 * @param personId for person we need to select from database
	 * @return PnPerson bean
	 */
	public PnPerson getPerson(Integer personId);
	
	/**
	 * Saves new person
	 * @param pnPerson object we want to save
	 * @return primary key for saved person
	 */
	public Integer savePerson(PnPerson pnPerson);
	
	/**
	 * Deletes person from database
	 * @param pnPerson object we want to delete
	 */
	public void deletePerson(PnPerson pnPerson);
	
	/**
	 * Updates person
	 * @param pnPerson object we want to update
	 */
	public void updatePerson(PnPerson pnPerson);
	
	/**
	 * @param businessIds list of business identifiers
	 * @return list of PnPerson beans
	 */
	public List<PnPerson> getUniqueMembersOfBusinessCollection(List<Integer> businessIds);

	/** 
 	 * userId user identifier we need to omit in selection 
 	 * @return list of PnPerson objects with IDs only 
 	*/ 	
	public List<PnPerson> getAllPersonsIds(Integer userId); 
	
	/**
	 * return all project members
	 * 
	 * @param projectId
	 * @return list of PnPerson beans
	 */
    public List<PnPerson> getPersonsByProjectId(Integer projectId);
    
    /**
     * return all business memebers 
     * 
     * @param businessId
     * @return list of PnPerson beans
     */
    public List<PnPerson> getPersonsByBusinessId(Integer businessId);
    
    /**
     * rerurn all persons that are member of specified business and project
     * 
     * @param businessId
     * @param projectId
     * @return
     */
    public List<PnPerson> getPersonsByBusinessAndProjectId(Integer businessId, Integer projectId);
    
    /**
     * rerurn all persons that are member of all businesses and all projects
     * 
     * @return list of PnPerson beans
     */
    public List<PnPerson> getPersonsByAllBusinessesAndProjects();
    
    /**
     * return all persons without omitting user
     * @return list of PnPerson objects with IDs only
     */
    public List<PnPerson> getAllPersonsIds();
    
    
    /**
     * return all persons that are member of all businesses
     * 
     * @return list of PnPerson beans 
     */
    public List<PnPerson> getPersonsByAllBusinesses();
    
    /**
     * Get all currently online space member
     * 
     * @param spaceId - space identificator
     * @return        - list of online persons
     */
    public List<Teammate> getOnlineMembers(Integer spaceId);    
    
    /**
     * Get all currently online space member
     *  
     * @return        - list of online persons
     */
    public List<Teammate> getOnlineMembers();
    
    /**
     * return all the teammates of the project except the logged in person
     * 
     * @param projectId
     * @param loggedUserId
     * @return
     */
    public List<PnPerson> getProjectTeammates(Integer projectId, Integer loggedUserId);
    
    /**
     * @param projectId
     * @param startDate
     * @param endDate
     * @return
     */
    public List<ReportProjectUsers> getAssignedResourcesByProject(Integer projectId, Date startDate, Date endDate);
    
    /**
     * searchs for persons coresponding to any of input parameters
     * @param firstName
     * @param lastName
     * @param email
     * @return list of PnPersons
     */
    public List<PnPerson> getPersonByName(String firstName, String lastName, String email);
    
    /**
     * return all business memebers 
     * 
     * @param businessId
     * @param orderby
     * @return list of PnPerson beans
     */
    public List<PnPerson> getPersonsByBusinessId(Integer businessId, String orderBy);
    
    /**
     * Get person name by id
     * @param personId person identifier
     * @return PnPerson object
     */
    public PnPerson getPersonNameById(Integer personId);
    
    
    /**
     * Get person's image id by person id
     * @param personId
     * @return image id
     */
    public Integer getImageIdByPersonId(Integer personId);
    
    /**
     * Get person's name and image id by person id
     * @param personId
     * @return instance of PnPerson
     */
    public PnPerson getPesronNameAndImageIdByPersonId(Integer personId);
    
    /**
     * Get person by person id
     * @param personId
     * @return
     */
    public PnPerson getPersonById(Integer personId);    
}
