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

import net.project.hibernate.model.PnWikiAssignment;

/**
 * Interface for accessing wiki page database object.
 */
public interface IPnWikiAssignmentDAO extends IDAO<PnWikiAssignment, Integer> {
	
	/**
	 * Retrieve the pnWikiAssignment instance for objectId
	 * @param objectId object indentifier
	 * @return pnWikiAssignment instance
	 */
	public PnWikiAssignment getWikiAssignmentByObjectId(Integer objectId);
	
	/**
	 * Delete the pnWikiAssignment instances for wikiPageId
	 * @param wikiPageId wiki page identifier
	 */
	public void deleteWikiAssignmentsByPageId(Integer wikiPageId);
	
}