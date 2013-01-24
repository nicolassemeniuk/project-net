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

import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.model.resource_reports.ReportProjectUsers;

public interface IPnPersonDAO extends IDAO<PnPerson, Integer> {
	
	public List<PnPerson> getUniqueMembersOfBusinessCollection(List<Integer> businessIds);

    public List<PnPerson> getAllPersonsIds(Integer personId);
    
    /**
     * get list of all project members
     * 
     * @param projectId - project identificator
     * @return          - list of project members 
     */
    public List<PnPerson> getPersonsByProjectId(Integer projectId);
    
    public List<PnPerson> getPersonsByBusinessId(Integer businessId);
    
    public List<PnPerson> getPersonsByBusinessAndProjectId(Integer businessId, Integer projectId);
    
    public List<PnPerson> getPersonsByAllBusinessesAndProjects();
    
    public List<PnPerson> getAllPersonsIds();
    
    public List<PnPerson> getPersonsByAllBusinesses();
    
    /**
     * Get all currently online space member
     * 
     * @param spaceId - space identificator
     * @return        - list of online persons
     */
    public List<Teammate> getOnlineMembers(Integer spaceId);
    
    public List<ReportProjectUsers> getAssignedResourcesByProject(Integer projectId, Date startDate, Date endDate);
    
    public List<PnPerson> getPersonByName(String firstName, String lastName, String email);
    
    public List<PnPerson> getPersonsByBusinessId(Integer businessId, String orderBy);
    
    /**
     * Get person name by id
     * @param personId  person identifier
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
     * Get persons by person id
     * @param personId
     * @return
     */
    public PnPerson getPersonById(Integer personId);    
}
