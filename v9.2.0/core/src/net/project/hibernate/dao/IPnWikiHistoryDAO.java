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

import net.project.hibernate.model.PnWikiHistory;

/**
 * Interface for accessing wiki history database object.
 */
public interface IPnWikiHistoryDAO extends IDAO<PnWikiHistory, Integer> {	
	
	public List<PnWikiHistory> findHistoryWithPageId(Integer wikiPageID);
	
	/**
	 * Get history of the project wiki page
	 * @param projectId project identifier
	 * @param startDate 
	 * @param endDate
	 * @return List of PnWikiHistory instances
	 */
	public List<PnWikiHistory> getWikiHistory(Integer projectId, Date startDate, Date endDate);
	
	/**
	 * Update all history records with wikiPageId field set to oldWikiPageId to new newWikiPageId value.
	 * @param newWikiPageId
	 * @param oldWikiPageId
	 */
	public void updateWikiPageIds(Integer newWikiPageId, Integer oldWikiPageId);
	
}