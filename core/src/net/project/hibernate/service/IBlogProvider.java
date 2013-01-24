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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.project.base.PnWebloggerException;
import net.project.hibernate.model.PnUser;
import net.project.hibernate.model.PnWeblog;
import net.project.hibernate.model.PnWeblogComment;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.model.PnWeblogEntryAttribute;
import net.project.project.ProjectSpaceBean;
import net.project.security.User;

/**
 * @author
 * Interface to weblog entry, category and comment management.
 *
 */
public interface IBlogProvider {

    public static final String DESCENDING = "DESCENDING";
    public static final String ASCENDING = "ASCENDING";
    
    /**
     * Store a single weblog.
     */
    public java.lang.Integer saveWeblog(PnWeblog data) throws PnWebloggerException;
    
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
     * Update weblog
     */
    public void updateWeblog(PnWeblog data) throws PnWebloggerException;
    
    /**
     * Remove weblog object.
     */
    public void removeWeblog(PnWeblog website) throws PnWebloggerException;
    
    
    /**
     * Get weblog object by userId.
     */
    public PnWeblog getWeblog(Integer userId) throws PnWebloggerException;
    
    /**
     * Get weblog object by userId and spaceId.
     */
    public PnWeblog getWeblogByUserAndSpaceId(Integer userId, Integer spaceId) throws PnWebloggerException;
        
    /**
     * Get weblogs optionally restricted by user, enabled and active status.
     * @param user    Get all websites for this user (or null for all)
     * @param offset  Offset into results (for paging)
     * @param len     Maximum number of results to return (for paging)
     * @param enabled Get all with this enabled state (or null or all)
     * @param active  Get all with this active state (or null or all)
     * @param startDate Restrict to those created after (or null for all)
     * @param endDate Restrict to those created before (or null for all)
     * @returns List of WeblogData objects.
     */
    public List getWeblogs(
            PnUser user,
            Boolean  enabled,
            Boolean  active,
            Date     startDate,
            Date     endDate,
            int      offset,
            int      length)
            throws PnWebloggerException;
    
    
    /**
     * Get weblogs ordered by descending number of comments.
     * @param startDate Restrict to those created after (or null for all)
     * @param endDate Restrict to those created before (or null for all)
     * @param offset    Offset into results (for paging)
     * @param len       Maximum number of results to return (for paging)
     * @returns List of WeblogData objects.
     */
    public List getMostCommentedWeblogs(
            Date startDate,
            Date endDate,
            int  offset,
            int  length)
            throws PnWebloggerException;   
  
       
    /**
     * Save weblog entry.
     */
    public java.lang.Integer saveWeblogEntry(PnWeblogEntry entry) throws PnWebloggerException;
    
    /**
     * Update weblog entry.
     */
    public void updateWeblogEntry(PnWeblogEntry entry) throws PnWebloggerException;
       
    /**
     * Remove weblog entry.
     */
    public void removeWeblogEntry(PnWeblogEntry entry) throws PnWebloggerException;    
    
    /**
     * Get weblog entry by id.
     */
    public PnWeblogEntry getWeblogEntry(Integer id) throws PnWebloggerException;
    
    /**
     * Get Detail weblog entry by id.
     */
    public PnWeblogEntry getWeblogEntryDetail(Integer id, Integer personId) throws PnWebloggerException;

    /**
     * @param id
     * @return
     * @throws PnWebloggerException
     */
    public PnWeblogEntry getWeblogEntryDetail(Integer id) throws PnWebloggerException;
    
    /** 
     * Get weblog entry by anchor. 
     */
    public PnWeblogEntry getWeblogEntryByAnchor(PnWeblog website, String anchor) 
            throws PnWebloggerException;
        
    /**
     * Get WeblogEntries by offset/length as list in reverse chronological order.
     * The range offset and list arguments enable paging through query results.
     * @param weblog    Weblog or null to get for all weblogs.
     * @param userId     UserId or null to get for all users.
     * @param startDate  Start date or null for no start date.
     * @param endDate    End date or null for no end date.    
     * @param status     Status of DRAFT, PENDING, PUBLISHED or null for all     * 
     * @param offset     Offset into results for paging
     * @param length     Max comments to return (or -1 for no limit)
     * @return List of WeblogEntryData objects in reverse chrono order.
     * @throws WebloggerException
     */
    public List getWeblogEntries(
    		Integer weblogId,
            Integer    userId,
            Date        startDate,
            Date        endDate,
            String      status,
            int         offset,
            int         range)
            throws PnWebloggerException;
      
    /**
     * @param personId
     * @param projectId
     * @param startDate
     * @param endDate
     * @param status
     * @param offset
     * @param range
     * @param showTimeReportedEntries
     * @return
     * @throws PnWebloggerException
     */
    public List<PnWeblogEntry> getWeblogEntriesFromProjectBlogByPerson(Integer personId, Integer projectId, Date startDate, Date endDate, String status, int offset, int range) throws PnWebloggerException;
    
    /**
     * @param personId
     * @param projectId
     * @param startDate
     * @param endDate
     * @param status
     * @param offset
     * @param range
     * @param showTimeReportedEntries
     * @param itemType
     * @param showImportantBlogEntries
     * @return
     * @throws PnWebloggerException
     */
    public List<PnWeblogEntry> getWeblogEntriesFromProjectBlogByPerson(Integer personId, Integer projectId, Date startDate, Date endDate, String status, int offset, int range, String itemType, boolean showImportantBlogEntries) throws PnWebloggerException;

    
    /**
	 * Sort blog entries by publication time in descending order
	 * @param entries list of PnWeblogEntry instances to sort
	 * @return List of sorted blog entries
	 */
    public List<PnWeblogEntry> getSortedBlogEntries(List<PnWeblogEntry> entries);
    
    /**
	 * @param weblogEntryId
	 * @param string
	 * @return
	 */
	public PnWeblogEntryAttribute getWeblogEntryAtribute(Integer weblogEntryId, String name);	
	
	/**
	 * @param spaceId
	 * @return
	 */
	public PnWeblog getWeblogBySpaceId(Integer spaceId);
	
	/**
	 * @param spaceId
	 * @param initializePersonObject
	 * @return
	 */
	public PnWeblog getWeblogBySpaceId(Integer spaceId, boolean initializePersonObject);
	
	/**
	 * @param spaceId
	 * @return
	 */
	public List getTaskIdsFromTaskBlogEntries(Integer spaceId);
	
	/**
	 * @param weblogEntryId
	 * @return
	 */
	public List<PnWeblogEntryAttribute> getWeblogEntryAtributesByEntryId(Integer weblogEntryId);
	
	/**
	 * @param taskId
	 * @return
	 */
	public List<PnWeblogEntry> getWeblogEntriesByTaskId(Integer taskId);
	
	/**
	 * Get Weblog Entries by object id
	 * @param objectId object identifier
	 * @return List of PnWeblogEntries
	 */
	public List<PnWeblogEntry> getWeblogEntriesByObjectId(String objectId);
	
	/**
	 * Get Weblog Entries by object id
	 * @param objectId object identifier
	 * @param status for weblog entry
	 * @return List of PnWeblogEntries
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
	 * Get weblog entries by applying filters for person/project blog
	 * @param weblogId weblog identifier
     * @param memberId -to filter entries for selected team member
	 * @param userId -to get entries for person, set for person space only
	 * @param objectId of the object to get entries for projectId(for person space)/objectId
     *        (to filter for selected item in project space)
	 * @param startDate
	 * @param endDate
	 * @param status
	 * @param offset
	 * @param range
	 * @param childNodes[] array of child nodes id's including selected task/project
	 * @param showTimeReportedEntries to filter time reported blog entries only
	 * @param itemType 
	 * @param showImportantBlogEntries
	 * @return List of PnWeblogEntries
	 * @throws PnWebloggerException
	 */
	public List<PnWeblogEntry> getFilteredWeblogEntries(Integer weblogId, Integer memberId, Integer userId, Integer objectId, Date startDate, Date endDate, String status, int offset, int range, String[] childNodes, boolean showTimeReportedEntries, String itemType, boolean showImportantBlogEntries) throws PnWebloggerException;
	
	/**
	 * Get weblog entries by applying filters for person/project blog
	 * @param weblogId weblog identifier
     * @param memberId -to filter entries for selected team member
	 * @param userId -to get entries for person, set for person space only
	 * @param objectId of the object to get entries for projectId(for person space)/objectId
     *        (to filter for selected item in project space)
	 * @param startDate
	 * @param endDate
	 * @param status
	 * @param offset
	 * @param range
	 * @param childNodes[] array of child nodes id's including selected task/project
	 * @param showTimeReportedEntries to filter time reported blog entries only
	 * @param itemType 
	 * @param showImportantBlogEntries
	 * @param currentUserId
	 * @return List of PnWeblogEntries
	 * @throws PnWebloggerException
	 */
	public List<PnWeblogEntry> getFilteredWeblogEntries(Integer weblogId, Integer memberId, Integer userId, Integer objectId, Date startDate, Date endDate, String status, int offset, int range, String[] childNodes, boolean showTimeReportedEntries, String itemType, boolean showImportantBlogEntries, Integer currentUserId) throws PnWebloggerException;
	
	/**
	 * Get Weblog Entries by applying filters
	 * @param weblogId weblog identifier
	 * @param userId id of the to get entries for
	 * @param objectId id of the object to get entries for
	 * @param startDate
	 * @param endDate
	 * @param status
	 * @param offset
	 * @param range
	 * @param childNodes[] array of child nodes id's including selected task/project
	 * @return List of PnWeblogEntries
	 * @throws PnWebloggerException
	 */
	public List<PnWeblogEntry> getFilteredWeblogEntries(Integer weblogId, Integer userId, Integer objectId, Date startDate, Date endDate, String status, int offset, int range, String[] childNodes) throws PnWebloggerException;

	/**
	 * Get last weblog entry of a user 
	 * @param userId user identifier
	 * @return instance of PnWeblogEntry
	 */
	public PnWeblogEntry getLastBlogEntryOfUser(Integer userId);
	
	/**
	 * Get last weblog entry of a user
	 * @param userId
	 * @param currentUserId
	 * @return
	 */
	public PnWeblogEntry getLastBlogEntryOfUser(Integer userId, Integer currentUserId);
	
	/**
     * Get last weblog entry of a user by project id
     * @param userId user identifier
     * @param projectId project identifier
     * @return instance of PnWeblogEntry
     * @throws PnWebloggerException
     */
    public PnWeblogEntry getLastBlogEntryOfUserByProject(Integer userId, Integer projectId) throws PnWebloggerException;
    
	/**
	 * Get last weblog entries of all users by space
	 * @param spaceId Space identifier
	 * @return List of PnWeblogEntry instances
	 */
	public List<PnWeblogEntry> getLastBlogEntiesOfAllUsersBySpace(Integer spaceId);
	
	/**
	 * Method to get weblog comments by weblog entry id
	 * @param weblogEntryId 
	 * @return PnWeblogComment Set
	 */
	public Set<PnWeblogComment> getWeblogCommentsForWeblogEntry(Integer weblogEntryId);
       
    /**
     * Get Weblog Entries grouped by day. This method returns a Map that
     * contains Lists, each List contains WeblogEntryData objects, and the
     * Lists are keyed by Date objects.
     * @param website    Weblog or null to get for all weblogs.
     * @param startDate  Start date or null for no start date.
     * @param endDate    End date or null for no end date.
     * @param catName    Category path or null for all categories.
     * @param status     Status of DRAFT, PENDING, PUBLISHED or null for all
     * @param offset     Offset into results for paging
     * @param length     Max comments to return (or -1 for no limit)
     * @return Map of Lists, keyed by Date, and containing WeblogEntryData.
     * @throws WebloggerException
     */
    public Map getWeblogEntryObjectMap(
            PnWeblog website,
            Date        startDate,
            Date        endDate,
            String      catName,
            List        tags,            
            String      status,
            String      locale,
            int         offset,
            int         range)
            throws PnWebloggerException;
        
    /**
     * Get Weblog Entry date strings grouped by day. This method returns a Map
     * that contains Lists, each List contains YYYYMMDD date strings objects,
     * and the Lists are keyed by Date objects.
     * @param website    Weblog or null to get for all weblogs.
     * @param startDate  Start date or null for no start date.
     * @param endDate    End date or null for no end date.
     * @param catName    Category path or null for all categories.
     * @param status     Status of DRAFT, PENDING, PUBLISHED or null for all
     * @param offset     Offset into results for paging
     * @param length     Max comments to return (or -1 for no limit)
     * @return Map of Lists, keyed by Date, and containing date strings.
     * @throws WebloggerException
     */
    public Map getWeblogEntryStringMap(
            PnWeblog website,
            Date        startDate,
            Date        endDate,
            String      catName,
            List        tags,            
            String      status,
            String      locale,
            int         offset,
            int         range)
            throws PnWebloggerException;    
    
    /** 
     * Get weblog enties ordered by descending number of comments.
     * @param website    Weblog or null to get for all weblogs.
     * @param startDate  Start date or null for no start date.
     * @param endDate    End date or null for no end date.
     * @param offset     Offset into results for paging
     * @param length     Max comments to return (or -1 for no limit)
     * @returns List of WeblogEntryData objects.
     */
    public List getMostCommentedWeblogEntries(
            PnWeblog website,             
            Date        startDate,
            Date        endDate,
            int         offset, 
            int         length)
            throws PnWebloggerException;
    
    /**
     * Get the WeblogEntry following, chronologically, the current entry.
     * Restrict by the Category, if named.
     * @param current The "current" WeblogEntryData
     * @param catName The value of the requested Category Name
     */
    public PnWeblogEntry getNextEntry(PnWeblogEntry current, 
            String catName, String locale) throws PnWebloggerException;    
    
    /**
     * Get the WeblogEntry prior to, chronologically, the current entry.
     * Restrict by the Category, if named.
     * @param current The "current" WeblogEntryData.
     * @param catName The value of the requested Category Name.
     */
    public PnWeblogEntry getPreviousEntry(PnWeblogEntry current, 
            String catName, String locale) throws PnWebloggerException;
      
    
    /**
     * Get specified number of most recent pinned and published Weblog Entries.
     * @param max Maximum number to return.
     * @return Collection of WeblogEntryData objects.
     */
    public List getWeblogEntriesPinnedToMain(Integer max) throws PnWebloggerException;
    
    /**
     * Save attribute with name from given WeblogEntryAttribute Data
     * @param pnWeblogEntryAttribute PnWeblogEntryAttribute object to be saved
     */
    public java.lang.Integer saveWeblogEntryAttribute(PnWeblogEntryAttribute pnWeblogEntryAttribute) throws PnWebloggerException;
    

	/**
	 * @param pnWeblogEntryAttribute
	 */
	public void updateWeblogEntryAttribute(PnWeblogEntryAttribute pnWeblogEntryAttribute) throws PnWebloggerException;


    /**
     * Remove attribute with given name from given WeblogEntryData
     * @param name Name of attribute to be removed
     */
    public void removeWeblogEntryAttribute(PnWeblogEntryAttribute attribute)
            throws PnWebloggerException;

       
               
    /**
     * Save comment.
     */
    public Integer saveComment(PnWeblogComment comment) throws PnWebloggerException;
    
    /**
     * Remove comment.
     */
    public void removeComment(PnWeblogComment comment) throws PnWebloggerException;
   
    /**
     * Get comment by id.
     */
    public PnWeblogComment getComment(String id) throws PnWebloggerException;
       
    /**
     * Generic comments query method.
     * @param website    Website or null for all comments on site
     * @param entry      Entry or null to include all comments
     * @param startDate  Start date or null for no restriction
     * @param endDate    End date or null for no restriction
     * @param status     The status of the comment, or null for any
     * @param reverseChrono True for results in reverse chrono order
     * @param offset     Offset into results for paging
     * @param length     Max comments to return (or -1 for no limit)
     */
    public List getComments(
            
            PnWeblog          website,
            PnWeblogComment     entry,
            String          searchString,
            Date            startDate,
            Date            endDate,
            String          status,
            boolean         reverseChrono,
            int             offset,
            int             length
            
            ) throws PnWebloggerException;

    /**
     * Deletes comments that match paramters.
     * @param website    Website or null for all comments on site
     * @param entry      Entry or null to include all comments
     * @param startDate  Start date or null for no restriction
     * @param endDate    End date or null for no restriction
     * @param status     Status of comment
     * @return Number of comments deleted
     */
    public int removeMatchingComments(
            
            PnWeblog          website,
            PnWeblogComment     entry,
            String          searchString,
            Date            startDate,
            Date            endDate,
            String          status
            
            ) throws PnWebloggerException;
        
   
    
    /**
     * Apply comment default settings from website to all of website's entries.
     */
    public void applyCommentDefaultsToEntries(PnWeblog website) 
        throws PnWebloggerException;
    
    /**
     * Get the count of blog entries
     * @param weblogId
     * @param userId
     * @param status
     * @param startDate
     * @param endDate
     * @return count of total blog entries for project
     * @throws PnWebloggerException
     */
    public int getCountOfBlogEntries(Integer weblogId, Integer userId, String status,Date startDate,Date endDate) throws PnWebloggerException;
    
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
     * @return count of total blog entries found
     * @throws PnWebloggerException
     */
    public int getCountOfBlogEntries(Integer weblogId, Integer memberId, Integer userId, Integer objectId, Date startDate, Date endDate, String status, boolean showTimeReportedEntries, String itemType, boolean showImportantBlogEntries, String[] childNodes) throws PnWebloggerException;

    /**
     * Release all resources held by manager.
     */
    public void release();
    
    /**
     * @param weblogEntryId
     * @param spaceId
     * @return
     */
    public PnWeblogEntry getWeblogEntryWithSpaceId(Integer weblogEntryId);

    /**
     * Get all weblog comments by weblog entry id and status
     * @param weblogEntryId
     * @param status
     * @return Set of all PnWeblogComment instances
     */
    public Set<PnWeblogComment> getWeblogCommentsForWeblogEntry(Integer weblogEntryId, String status);
    
}


