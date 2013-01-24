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

import java.util.Set;

import net.project.hibernate.model.PnWeblogComment;


public interface IPnWeblogCommentService {
	
	/**
	 * Persist the given transient instance, first assigning a generated identifier. (Or using the current value
	 * of the identifier property if the assigned generator is used.) 
	 * @param pnWeblogComment a transient instance of a persistent class 
	 * @return the class identifier
	 */
	public java.lang.Integer save(net.project.hibernate.model.PnWeblogComment pnWeblogComment);

	/**
	 * Update the persistent state associated with the given identifier. An exception is thrown if there is a persistent
	 * instance with the same identifier in the current session.
	 * @param pnWeblogComment a transient instance containing updated state
	 */
	public void update(net.project.hibernate.model.PnWeblogComment pnWeblogComment);

	public net.project.hibernate.model.PnWeblogComment get(java.lang.Integer key);

	public java.util.List<net.project.hibernate.model.PnWeblogComment> findAll ();

	/**
	 * Remove a persistent instance from the datastore. The argument may be an instance associated with the receiving
	 * Session or a transient instance with an identifier associated with existing persistent state. 
	 * @param pnWeblogComment the instance to be removed
	 */
	public void delete(net.project.hibernate.model.PnWeblogComment pnWeblogComment);
	
	/**
	 * Method to get weblog comments by weblog entry id
	 * @param weblogEntryId 
	 * @return PnWeblogComment Set
	 */
	public Set<PnWeblogComment> getWeblogCommentsForWeblogEntry(Integer weblogEntryId);
	
	public PnWeblogComment getWeblogCommentByCommentId(Integer commentId);
	
	/**
	 * Get weblog comments by weblog entry id and status
	 * @param weblogEntryId
	 * @param status
	 * @return Set of PnWeblogComment instances
	 */
	public Set<PnWeblogComment> getWeblogCommentsForWeblogEntry(Integer weblogEntryId, String status);
	
	/**
	 * Method to update the comment status by comment id 
	 * @param commentId
	 * @param status
	 * @return Integer
	 */
	public Integer updateCommentStatus(Integer commentId, String status);
}