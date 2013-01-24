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

import net.project.hibernate.dao.IPnPersonDAO;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.model.project_space.UserActivity;
import net.project.hibernate.model.resource_reports.ReportProjectUsers;
import net.project.hibernate.service.IPnPersonService;

@Service(value="pnPersonService")
public class PnPersonServiceImpl implements IPnPersonService {

	/**
	 * PnPerson data access object
	 */
	@Autowired
	private IPnPersonDAO pnPersonDAO;

	/**
	 * sets PnPerson data acces object
	 * 
	 * @param pnPersonDAO
	 */
	public void setPnPersonDAO(IPnPersonDAO pnPersonDAO) {
		this.pnPersonDAO = pnPersonDAO;
	}

	/**
	 * gets PnPerson object for given id
	 */
	public PnPerson getPerson(Integer personId) {
		return pnPersonDAO.findByPimaryKey(personId);
	}

	/**
	 * saves pnPerson object
	 */
	public Integer savePerson(PnPerson pnPerson) {
		return pnPersonDAO.create(pnPerson);
	}

	/**
	 * deletes pnPerson object
	 */
	public void deletePerson(PnPerson pnPerson) {
		pnPersonDAO.delete(pnPerson);
	}

	/**
	 * updates pnPerson
	 */
	public void updatePerson(PnPerson pnPerson) {
		pnPersonDAO.update(pnPerson);
	}

	/**
	 * gets list of PnPerson beans
	 */
	public List<PnPerson> getUniqueMembersOfBusinessCollection(List<Integer> businessIds) {
		return pnPersonDAO.getUniqueMembersOfBusinessCollection(businessIds);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnPersonService#getAllPersonsIds(java.lang.Integer)
	 */
	public List<PnPerson> getAllPersonsIds(Integer personId) {
		return pnPersonDAO.getAllPersonsIds(personId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnPersonService#getPersonsByBusinessId(java.lang.Integer)
	 */
	public List<PnPerson> getPersonsByBusinessId(Integer businessId) {		
		return pnPersonDAO.getPersonsByBusinessId(businessId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnPersonService#getPersonsByProjectId(java.lang.Integer)
	 */
	public List<PnPerson> getPersonsByProjectId(Integer projectId) {
		return pnPersonDAO.getPersonsByProjectId(projectId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnPersonService#getPersonsByBusinessAndProjectId(java.lang.Integer, java.lang.Integer)
	 */
	public List<PnPerson> getPersonsByBusinessAndProjectId(Integer businessId, Integer projectId) {
		return pnPersonDAO.getPersonsByBusinessAndProjectId(businessId, projectId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnPersonService#getPersonsByAllBusinessesAndProjects()
	 */
	public List<PnPerson> getPersonsByAllBusinessesAndProjects() {		
		return pnPersonDAO.getPersonsByAllBusinessesAndProjects();
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnPersonService#getAllPersonsIds()
	 */
	public List<PnPerson> getAllPersonsIds() {		
		return pnPersonDAO.getAllPersonsIds();
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnPersonService#getPersonsByAllBusinesses()
	 */
	public List<PnPerson> getPersonsByAllBusinesses() {		
		return pnPersonDAO.getPersonsByAllBusinesses();
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnPersonService#getOnlineMembers(java.lang.Integer)
	 */
	public List<Teammate> getOnlineMembers(Integer spaceId) {
		List<Teammate> teammates = pnPersonDAO.getOnlineMembers(spaceId);
		List<Teammate> onlineTeammates = new ArrayList<Teammate>();
		for (Teammate teammate : teammates) {
			if (System.currentTimeMillis() - teammate.getLastAccessTime().getTime() < UserActivity.TIME_OUT_MILLIS.getFActivity()) { 
				onlineTeammates.add(teammate);
			}
		}				
		return onlineTeammates;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnPersonService#getOnlineMembers()
	 */
	public List<Teammate> getOnlineMembers() {
		return getOnlineMembers(null);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnPersonService#getProjectTeammates(java.lang.Integer, java.lang.Integer)
	 */
	public List<PnPerson> getProjectTeammates(Integer projectId, Integer loggedUserId) {
		List<PnPerson> persons = pnPersonDAO.getPersonsByProjectId(projectId);
		for(PnPerson person : persons){
			if(person.getPersonId().intValue() == loggedUserId.intValue()){
				persons.remove(person);
				break;
			}
		}		
		return persons;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnPersonService#getAssignedResourcesByProject(java.lang.Integer, java.util.Date, java.util.Date)
	 */
	public List<ReportProjectUsers> getAssignedResourcesByProject(Integer projectId, Date startDate, Date endDate){
		return pnPersonDAO.getAssignedResourcesByProject(projectId, startDate, endDate);
	}

	public List<PnPerson> getPersonByName(String firstName, String lastName, String email) {
		return pnPersonDAO.getPersonByName(firstName, lastName, email);
	}
	
	public List<PnPerson> getPersonsByBusinessId(Integer businessId, String orderBy){
		return pnPersonDAO.getPersonsByBusinessId(businessId, orderBy);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnPersonService#getPersonNameById(java.lang.Integer)
	 */
	public PnPerson getPersonNameById(Integer personId) {
		return pnPersonDAO.getPersonNameById(personId);
	}
    
    /* (non-Javadoc)
     * @see net.project.hibernate.service.IPnPersonService#getImageIdByPersonId(java.lang.Integer)
     */
    public Integer getImageIdByPersonId(Integer personId){
        return pnPersonDAO.getImageIdByPersonId(personId); 
     }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.service.IPnPersonService#getPesronNameAndImageIdByPersonId(java.lang.Integer)
     */
    public PnPerson getPesronNameAndImageIdByPersonId(Integer personId) {
    	return pnPersonDAO.getPesronNameAndImageIdByPersonId(personId);
    }

    /* (non-Javadoc)
     * @see net.project.hibernate.service.IPnPersonService#getPesronIdByPersonId(java.lang.Integer)
     */
	public PnPerson getPersonById(Integer personId) {
		return pnPersonDAO.getPersonById(personId);
	}
}
