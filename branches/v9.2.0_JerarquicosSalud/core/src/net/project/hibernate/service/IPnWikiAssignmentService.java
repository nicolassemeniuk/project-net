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

import net.project.hibernate.model.PnWikiAssignment;

public interface IPnWikiAssignmentService {

	/**
	 * Persist the given transient instance, first assigning a generated identifier. (Or using the current value
	 * of the identifier property if the assigned generator is used.) 
	 * @param pnWikiPage a transient instance of a persistent class 
	 * @return the class identifier
	 */
	public java.lang.Integer save(PnWikiAssignment pnWikiAssignment);

	/**
	 * Update the persistent state associated with the given identifier. An exception is thrown if there is a persistent
	 * instance with the same identifier in the current session.
	 * @param pnWikiAssignment a transient instance containing updated state
	 */
	public void update(PnWikiAssignment pnWikiAssignment);

	public PnWikiAssignment get(Integer objectID);

	public java.util.List<PnWikiAssignment> findAll();

	/**
	 * Remove a persistent instance from the datastore. The argument may be an instance associated with the receiving
	 * Session or a transient instance with an identifier associated with existing persistent state. 
	 * @param pnWikiAssignment the instance to be removed
	 */
	public void delete(PnWikiAssignment pnWikiAssignment);

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