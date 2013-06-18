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
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.project.hibernate.dao.IPnSpaceHasPersonDAO;
import net.project.hibernate.model.PnSpaceHasPerson;
import net.project.hibernate.model.PnSpaceHasPersonPK;
import net.project.hibernate.service.IPnSpaceHasPersonService;

@Service(value="pnSpaceHasPersonService")
public class PnSpaceHasPersonServiceImpl implements IPnSpaceHasPersonService {
	
	@Autowired
	private IPnSpaceHasPersonDAO pnSpaceHasPersonDAO;

	public void setPnSpaceHasPersonDAO(IPnSpaceHasPersonDAO pnSpaceHasPersonDAO) {
		this.pnSpaceHasPersonDAO = pnSpaceHasPersonDAO;
	}

	public PnSpaceHasPerson getSpaceHasPerson(PnSpaceHasPersonPK pnSpaceHasPersonId) {
		PnSpaceHasPerson spaceHasPerson = pnSpaceHasPersonDAO.findByPimaryKey(pnSpaceHasPersonId);
		if(spaceHasPerson != null){
			Hibernate.initialize(spaceHasPerson);
		}		
		return spaceHasPerson;
	}

	public PnSpaceHasPersonPK saveSpaceHasPerson(PnSpaceHasPerson pnSpaceHasPerson) {
		return pnSpaceHasPersonDAO.create(pnSpaceHasPerson);
	}

	public void deleteSpaceHasPerson(PnSpaceHasPerson pnSpaceHasPerson) {
		pnSpaceHasPersonDAO.delete(pnSpaceHasPerson);

	}

	public void updateSpaceHasPerson(PnSpaceHasPerson pnSpaceHasPerson) {
		pnSpaceHasPersonDAO.update(pnSpaceHasPerson);

	}
	public PnSpaceHasPerson getPnSpaceHasPersonBySecureKey(final String secureKey) {
		return pnSpaceHasPersonDAO.getPnSpaceHasPersonBySecureKey(secureKey);
	}
	
	public void saveOrUpdateSpaceHasPerson(PnSpaceHasPerson spaceHasPerson) {
		pnSpaceHasPersonDAO.saveOrUpdateSpaceHasPerson(spaceHasPerson);
	}
	
	public List<PnSpaceHasPerson> getSpaceHasPersonByProjectandPerson(Integer[] spaceIds, Integer personId) {
		return pnSpaceHasPersonDAO.getSpaceHasPersonByProjectandPerson(spaceIds, personId);
	}

	public boolean doesPersonExistsInSpace(Integer personId, Integer spaceId) {
		return pnSpaceHasPersonDAO.doesPersonExistsInSpace(personId, spaceId);
	}

	@Override
	public ArrayList<Integer> getSpacesFromPerson(Integer personID) {
		return pnSpaceHasPersonDAO.getSpacesFromPerson(personID);
	}
	
	
}
