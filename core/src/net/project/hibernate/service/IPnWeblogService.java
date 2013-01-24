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

import net.project.hibernate.model.PnWeblog;
import net.project.project.ProjectSpaceBean;
import net.project.security.User;


public interface IPnWeblogService {

	/**
	 * Persist the given transient instance, first assigning a generated identifier. (Or using the current value
	 * of the identifier property if the assigned generator is used.) 
	 * @param pnWeblog a transient instance of a persistent class 
	 * @return the class identifier
	 */
	public java.lang.Integer save(net.project.hibernate.model.PnWeblog pnWeblog);	

	/**
	 * Update the persistent state associated with the given identifier. An exception is thrown if there is a persistent
	 * instance with the same identifier in the current session.
	 * @param pnWeblog a transient instance containing updated state
	 */
	public void update(net.project.hibernate.model.PnWeblog pnWeblog);
	
	public net.project.hibernate.model.PnWeblog get(java.lang.Integer key);
	
	public net.project.hibernate.model.PnWeblog getByUserId(java.lang.Integer userId);
	
	/**
	 * @param userId
	 * @param spaceId
	 * @return the PnWeblog object retrieved
	 */
	public PnWeblog getByUserAndSpaceId(Integer userId, Integer spaceId);
	
	/**
	 * @param spaceId
	 * @return
	 */
	public PnWeblog getBySpaceId(Integer spaceId);
	
	/**
	 * Method for getting weblog by space id
	 * 
	 * @param spaceId
	 * @param initializePersonObject
	 * @return the PnWeblog object retrieved
	 */
	public PnWeblog getBySpaceId(Integer spaceId, boolean initializePersonObject);

	public java.util.List<net.project.hibernate.model.PnWeblog> findAll ();
	
	/**
	 * Remove a persistent instance from the datastore. The argument may be an instance associated with the receiving
	 * Session or a transient instance with an identifier associated with existing persistent state. 
	 * @param pnWeblog the instance to be removed
	 */
	public void delete(net.project.hibernate.model.PnWeblog pnWeblog);
	
	/**
	 * Method to create a weblog
	 * 
	 * @param user current user's instance
	 * @param spaceType type of the space of which blog is being created
	 * @param spaceId space identifier
	 * @param project instance of ProjectSpaceBean for project details  
	 * @return instance of a weblog created, null on error
	 */
	public PnWeblog createBlog(User user, String spaceType, Integer spaceId, ProjectSpaceBean project);

	/**
	 * Get PnWebLog by id
	 * @param weblogId
	 * @return
	 */
	public PnWeblog getPnWeblogById(int weblogId);
}