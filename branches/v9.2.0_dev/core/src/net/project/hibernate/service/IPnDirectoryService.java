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


import java.util.List;

import net.project.hibernate.model.PnDirectory;

public interface IPnDirectoryService {
	
	/**
	 * @param directoryId for Directory we need to select from database
	 * @return PnDirectory bean
	 */
	public PnDirectory getDirectory(Integer directoryId);
	
	/**
	 * Saves new Directory
	 * @param pnDirectory object we want to save
	 * @return primary key for saved Directory
	 */
	public Integer saveDirectory(PnDirectory pnDirectory);
	
	/**
	 * Deletes Directory from database
	 * @param pnDirectory object we want to delete
	 */
	public void deleteDirectory(PnDirectory pnDirectory);
	
	/**
	 * Updates Directory
	 * @param pnDirectory object we want to update
	 */
	public void updateDirectory(PnDirectory pnDirectory);
	
	/**
	 * Get list of default directories
	 * @return list of default directories
	 */
	public List<PnDirectory> getDefaultDirectory();

}
