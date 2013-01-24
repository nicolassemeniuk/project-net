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
/**
 * 
 */
package net.project.hibernate.service;

import java.util.List;

import org.apache.tapestry5.util.TextStreamResponse;

import net.project.hibernate.model.PnWeblogEntry;
import net.project.util.DateFormat;

/**
 *
 */
public interface IBlogViewProvider {

	/**
	 * Method to get blog entries according to following parameters
	 *  
	 * @param spaceType type of space for getting corresponding entries
	 * @param startDate start date
	 * @param endDate end date
	 * @param userId id of the user
	 * @param weblogId weblog identifier
	 * @param userDateFormat date format of current user
	 * @return List of PnWeblogEntry instances
	 */
	public List<PnWeblogEntry> getBlogEntries(String spaceType, String startDate, String endDate, String userId, Integer weblogId, DateFormat userDateFormat);
	
	/** 
	 * Method to get formatted blog entries
	 * 
	 * @param userWeblogEntries list of blog entries
	 * @param JSPRootURL application jsp root url
	 * @param spaceType space type
	 * @return List of formatted blog entries
	 */
	public List<PnWeblogEntry> getFormattedBlogEntries(List<PnWeblogEntry> userWeblogEntries, String JSPRootURL, String spaceType, DateFormat userDateFormat);
	
	/**
	 * Method for check object is deleted or not and if not then redirect to object page
	 * @param entryId
	 * @param objectId
	 * @param objectName
	 * @param spaceType
	 * @param isUserDeleted
	 * @param isProjectDeleted
	 * @return
	 */
	public TextStreamResponse checkObjectStatusAndReturnObjectURL(String entryId, String objectId, String objectType, String objectName, String spaceType, boolean isUserDeleted, boolean isProjectDeleted);
}
