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



import net.project.hibernate.model.PnDirectoryHasPerson;
import net.project.hibernate.model.PnDirectoryHasPersonPK;

public interface IPnDirectoryHasPersonService {
	
	/**
	 * @param directoryHasPersonPK for PnDirectoryHasPerson we need to select from database
	 * @return PnDirectoryHasPerson bean
	 */
	public PnDirectoryHasPerson getDirectoryHasPerson(PnDirectoryHasPersonPK directoryHasPersonPK);
	
	/**
	 * Saves PnDirectoryHasPerson
	 * @param pnDirectoryHasPerson object we want to save
	 * @return primary key for saved DirectoryHasPerson
	 */
	public PnDirectoryHasPersonPK saveDirectoryHasPerson(PnDirectoryHasPerson pnDirectoryHasPerson);
	
	/**
	 * Deletes PnDirectoryHasPerson from database
	 * @param pnDirectoryHasPerson object we want to delete
	 */
	public void deleteDirectoryHasPerson(PnDirectoryHasPerson pnDirectoryHasPerson);
	
	/**
	 * Updates PnDirectoryHasPerson
	 * @param pnDirectoryHasPerson object we want to update
	 */
	public void updateDirectoryHasPerson(PnDirectoryHasPerson pnDirectoryHasPerson);


}
