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

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.model.PnSpaceHasPerson;
import net.project.hibernate.model.PnSpaceHasPersonPK;

public interface IPnSpaceHasPersonService {
	
	/**
	 * @param pnSpaceHasPersonId 
	 * @return PnSpaceHasPerson bean
	 */
	public PnSpaceHasPerson getSpaceHasPerson(PnSpaceHasPersonPK pnSpaceHasPersonId);
	
	/**
	 * Saves new PnSpaceHasPerson
	 * @param PnSpaceHasPerson object we want to save
	 * @return primary key for saved space and person
	 */
	public PnSpaceHasPersonPK saveSpaceHasPerson(PnSpaceHasPerson pnSpaceHasPerson);
	
	/**
	 * Deletes PnSpaceHasPerson from database
	 * @param PnSpaceHasPerson object we want to delete
	 */
	public void deleteSpaceHasPerson(PnSpaceHasPerson pnSpaceHasPerson);
	
	/**
	 * Updates PnSpaceHasPerson
	 * @param PnSpaceHasPerson object we want to update
	 */
	public void updateSpaceHasPerson(PnSpaceHasPerson pnSpaceHasPerson);

	/**
	 * Get Object by secureKey (one of the parameter)
	 * @param secureKey
	 * @return PnSpaceHasPerson object we want to use
	 */
	public PnSpaceHasPerson getPnSpaceHasPersonBySecureKey(final String secureKey);
	
	public void saveOrUpdateSpaceHasPerson(PnSpaceHasPerson spaceHasPerson);
	
	public List<PnSpaceHasPerson> getSpaceHasPersonByProjectandPerson(Integer spaceIds[], Integer personId);
	
	public boolean doesPersonExistsInSpace(Integer personId, Integer spaceId);

	public List<Integer> getSpacesFromPerson(Integer personID);
}
