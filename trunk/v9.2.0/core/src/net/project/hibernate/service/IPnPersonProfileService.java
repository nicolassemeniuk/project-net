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



import javax.servlet.http.HttpSession;

import org.apache.tapestry5.upload.services.UploadedFile;

import net.project.hibernate.model.PnPersonProfile;
import net.project.security.User;

public interface IPnPersonProfileService {
	
	/**
	 * @param personProfileId for PersonProfile we need to select from database
	 * @return PnPersonProfile bean
	 */
	public PnPersonProfile getPersonProfile(Integer personProfileId);
	
	/**
	 * Saves new PersonProfile
	 * @param pnPersonProfile object we want to save
	 * @return primary key for saved PersonProfile
	 */
	public Integer savePersonProfile(PnPersonProfile pnPersonProfile);
	
	/**
	 * Deletes PersonProfile from database
	 * @param pnPersonProfile object we want to delete
	 */
	public void deletePersonProfile(PnPersonProfile pnPersonProfile);
	
	/**
	 * Updates PersonProfile
	 * @param pnPersonProfile object we want to update
	 */
	public void updatePersonProfile(PnPersonProfile pnPersonProfile);
	
	/**
	 * Uploads Documents
	 * @param file
	 * @param user
	 * @param session
	 * @param module
	 * @return Document id as String
	 */
	public String uploadDocument(UploadedFile file, User user, HttpSession session , int module);

}
