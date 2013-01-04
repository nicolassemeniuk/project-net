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

import java.util.Date;
import java.util.List;

import net.project.base.PnWebloggerException;
import net.project.hibernate.model.PnWeblogEntry;

public interface IPnWeblogEntryService {

	/**
	 * Persist the given transient instance, first assigning a generated identifier. (Or using the current value
	 * of the identifier property if the assigned generator is used.) 
	 * @param pnWeblogEntry a transient instance of a persistent class 
	 * @return the class identifier
	 */
	public java.lang.Integer save(PnWeblogEntry pnWeblogEntry);	

	/**
	 * Update the persistent state associated with the given identifier. An exception is thrown if there is a persistent
	 * instance with the same identifier in the current session.
	 * @param pnWeblogEntry a transient instance containing updated state
	 */
	public void update(PnWeblogEntry pnWeblogEntry);	
	
	public PnWeblogEntry getWeblogEntry(java.lang.Integer key);
	
	public PnWeblogEntry getWeblogEntryDetail(java.lang.Integer key);

	public java.util.List<PnWeblogEntry> findAll ();

	/**
	 * Remove a persistent instance from the datastore. The argument may be an instance associated with the receiving
	 * Session or a transient instance with an identifier associated with existing persistent state. 
	 * @param pnWeblogEntry the instance to be removed
	 */
	public void delete(PnWeblogEntry pnWeblogEntry);
	
	/**
	 * Get weblog entries by
	 * @param weblogId weblog identifier
	 * @param userId user identifier
	 * @param startDate 
	 * @param endDate
	 * @param status weblog entry status
	 * @param offset entry offset
	 * @param range for entries
	 * @return List of PnWeblogEntry instances
	 * @throws PnWebloggerException
	 */
	public List<PnWeblogEntry> getWeblogEntries(Integer weblogId, Integer userId, Date startDate, Date endDate, String status, int offset, int range) throws PnWebloggerException;

	/**
	 * Get webog entries by task id
	 * @param taskId task identifier
	 * @return List of PnWeblogEntry instances
	 */
	public List<PnWeblogEntry> getWeblogEntriesByTaskId(Integer taskId);
	
	/**
	 * Get weblog entries by object id
	 * @param objectId object identifier
	 * @return List of PnWeblogEntry instances
	 */
	public List<PnWeblogEntry> getWeblogEntriesByObjectId(String objectId);
	
	/**
	 * Get weblog entries by object id
	 * @param objectId object identifier
	 * @param status weblog entry status
	 * @return List of PnWeblogEntry instances
	 */
	public List<PnWeblogEntry> getWeblogEntriesByObjectId(String objectId, String status);
	
	/**
	 * Get weblog entries by object id
	 * @param objectId object identifier
	 * @param status weblog entry status
	 * @param startDate start date
	 * @param endDate end date
	 * @return List of PnWeblogEntry instances
	 */
	public List<PnWeblogEntry> getWeblogEntriesByObjectId(String objectId, String status, Date startDate, Date endDate);
	
	/**
	 * Get filtered weblog entries
     * @param weblogId weblog identifier
     * @param memberId -to filter entries for selected team member
     * @param userId -to get entries for person, set for person space only
     * @param objectId of the object to get entries for projectId(for person space)/objectId
     *        (to filter for selected item in project space)
	 * @param startDate
	 * @param endDate
	 * @param status weblog entry status
	 * @param offset entry offset
	 * @param range for entries
	 * @param childNodes
	 * @param showTimeReportedEntries
	 * @param itemType
	 * @param showImportantBlogEntries
	 * @param currentUserId
	 * @return List of PnWeblogEntry instances
	 * @throws PnWebloggerException
	 */
	public List<PnWeblogEntry> getFilteredWeblogEntries(Integer weblogId, Integer memberId, Integer userId, Integer objectId, Date startDate, Date endDate, String status, int offset, int range, String[] childNodes, boolean showTimeReportedEntries, String itemType, boolean showImportantBlogEntries, Integer currentUserId) throws PnWebloggerException;
	
	/**
	 * Sort blog entries by publication time in descending order
	 * @param entries list of PnWeblogEntry instances to sort
	 * @return List of sorted blog entries
	 */
	public List<PnWeblogEntry> getSortedBlogEntries(List<PnWeblogEntry> entries);
	
	/**
	 * Get last weblog entry of a user
	 * @param userId user identifier
	 * @param currentUserId
	 * @return instance of PnWeblogEntry
	 */
	public PnWeblogEntry getLastBlogEntryOfUser(Integer userId, Integer currentUserId);
	
	/**
	 * Get last blog entry of user by project
	 * @param userId user identifier
	 * @param projectId project identifier
	 * @return PnWeblogEntry instance
	 */
	public PnWeblogEntry getLastBlogEntryOfUserByProject(Integer userId, Integer projectId);
	
	/**
	 * Get last weblog entries of all users by space
	 * @param spaceId Space identifier
	 * @return List of PnWeblogEntry instances
	 */
	public List<PnWeblogEntry> getLastBlogEntiesOfAllUsersBySpace(Integer spaceId);

	/**
	 * Get weblog entries from project blog by person
	 * @param personId person identifier
	 * @param projectId project identifier
	 * @param startDate
	 * @param endDate
	 * @param status weblog entry status
	 * @param offset entry offset
	 * @param range for entries
	 * @param itemType type of item
	 * @param showImportantBlogEntries
	 * @return List of PnWeblogEntry instances
	 * @throws PnWebloggerException
	 */
	public List<PnWeblogEntry> getWeblogEntriesFromProjectBlogByPerson(Integer personId, Integer projectId, Date startDate, Date endDate, String status, int offset, int range, String itemType, boolean showImportantBlogEntries) throws PnWebloggerException;
    
    /**
     * Get the count of blog entries 
     * @param weblogId
     * @param userId
     * @param status
     * @param startDate
     * @param endDate
     * @return count of total blog entries found
     * @throws PnWebloggerException
     */
    public int getCountOfBlogEntries(Integer weblogId, Integer userId, String status,Date startDate, Date endDate) throws PnWebloggerException;
   
    /**
     * Get the count of blog entries 
     * @param weblogId weblog identifier
     * @param memberId to filter entries for selected team member
     * @param userId id of the to get entries for person, set for person space only
     * @param objectId
     * @param startDate
     * @param endDate
     * @param status
     * @param showTimeReportedEntries
     * @param itemType
     * @param showImportantBlogEntries
     * @param childNodes
     * @param currentUserId
     * @return count of total blog entries found
     * @throws PnWebloggerException
     */
    public int getCountOfBlogEntries(Integer weblogId, Integer memberId, Integer userId, Integer objectId, Date startDate, Date endDate, String status, boolean showTimeReportedEntries, String itemType, boolean showImportantBlogEntries, String[] childNodes, Integer currentUserId) throws PnWebloggerException;
    
    /**
     * @param weblogEntryId
     * @param spaceId
     * @return
     */
    public PnWeblogEntry getWeblogEntryWithSpaceId(Integer weblogEntryId);
    
    /**
     * To check blog entry deleted or not 
     * @param weblogEntryId
     * @returns true if deleted else false
     */
    public boolean isWeblogEntryDeleted(Integer weblogEntryId);
}
