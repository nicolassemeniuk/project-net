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
package net.project.hibernate.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.project.base.PnWebloggerException;
import net.project.hibernate.constants.WeblogConstants;
import net.project.hibernate.model.PnUser;
import net.project.hibernate.model.PnWeblog;
import net.project.hibernate.model.PnWeblogComment;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.model.PnWeblogEntryAttribute;
import net.project.hibernate.service.IBlogProvider;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.IPnProjectSpaceService;
import net.project.hibernate.service.IPnWeblogCommentService;
import net.project.hibernate.service.IPnWeblogEntryAttributeService;
import net.project.hibernate.service.IPnWeblogEntryService;
import net.project.hibernate.service.IPnWeblogService;
import net.project.project.ProjectSpaceBean;
import net.project.security.User;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author
 */
@Service(value="rollerBlogProvider")
public class RollerBlogProvider implements IBlogProvider {

	@Autowired
	private IPnPersonService personService;
	
	@Autowired
	private IPnWeblogService pnWeblogService;
	
	@Autowired
	private IPnWeblogEntryService pnWeblogEntryService;
	
	@Autowired
	private IPnWeblogEntryAttributeService pnWeblogEntryAttributeService;
	
	@Autowired
	private IPnWeblogCommentService pnWeblogCommentService;
    
	@Autowired
    private IPnProjectSpaceService pnProjectSpaceService;
    
	@Autowired
    private IPnPersonService pnPersonService;

	
	public void setPersonService(IPnPersonService personService) {
		this.personService = personService;
	}

	/**
	 * @param pnWeblogCommentService the pnWeblogCommentService to set
	 */
	public void setPnWeblogCommentService(IPnWeblogCommentService pnWeblogCommentService) {
		this.pnWeblogCommentService = pnWeblogCommentService;
	}

	/**
	 * @param pnWeblogEntryService the pnWeblogEntryService to set
	 */
	public void setPnWeblogEntryService(IPnWeblogEntryService pnWeblogEntryService) {
		this.pnWeblogEntryService = pnWeblogEntryService;
	}

	/**
	 * @param pnWeblogEntryAttributeService the pnWeblogEntryAttributeService to set
	 */
	public void setPnWeblogEntryAttributeService(IPnWeblogEntryAttributeService pnWeblogEntryAttributeService) {
		this.pnWeblogEntryAttributeService = pnWeblogEntryAttributeService;
	}

	/**
	 * @param pnWeblogService the pnWeblogService to set
	 */
	public void setPnWeblogService(IPnWeblogService pnWeblogService) {
		this.pnWeblogService = pnWeblogService;
	}

     /**
     * @param pnProjectSpaceService the pnProjectSpaceService to set
     */
    public void setPnProjectSpaceService(IPnProjectSpaceService pnProjectSpaceService) {
        this.pnProjectSpaceService = pnProjectSpaceService;
    }
    
    /**
	 * @param pnPersonService the pnPersonService to set
	 */
	public void setPnPersonService(IPnPersonService pnPersonService) {
		this.pnPersonService = pnPersonService;
	}
    
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#saveWebsite(net.project.hibernate.model.PnWeblog)
	 */
	public java.lang.Integer saveWeblog(PnWeblog data) throws PnWebloggerException {		
		return pnWeblogService.save(data);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#createBlog(net.project.security.User, java.lang.String, java.lang.Integer, net.project.project.ProjectSpaceBean)
	 */
	public PnWeblog createBlog(User user, String spaceType, Integer spaceId, ProjectSpaceBean project) {
		return pnWeblogService.createBlog(user, spaceType, spaceId, project);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#updateWeblog(net.project.hibernate.model.PnWeblog)
	 */
	public void updateWeblog(PnWeblog data) throws PnWebloggerException {		
		pnWeblogService.update(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#saveWeblogEntry(net.project.hibernate.model.PnWeblogEntry)
	 */
	public java.lang.Integer saveWeblogEntry(PnWeblogEntry entry) throws PnWebloggerException {		
		return pnWeblogEntryService.save(entry);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#updateWeblogEntry(net.project.hibernate.model.PnWeblogEntry)
	 */
	public void updateWeblogEntry(PnWeblogEntry entry) throws PnWebloggerException {		
		pnWeblogEntryService.update(entry);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#saveComment(net.project.hibernate.model.PnWeblogComment)
	 */
	public Integer saveComment(PnWeblogComment comment) throws PnWebloggerException {		
		return pnWeblogCommentService.save(comment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#getWebsite(java.lang.String)
	 */
	public PnWeblog getWeblog(Integer id) throws PnWebloggerException {
		return pnWeblogService.getByUserId(id);		 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#removeWebsite(net.project.hibernate.model.PnWeblog)
	 */
	public void removeWeblog(PnWeblog pnWeblog) throws PnWebloggerException {
		pnWeblogService.delete(pnWeblog);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#getWebsite(java.lang.String)
	 */
	public PnWeblog getWeblogByUserAndSpaceId(Integer userId, Integer spaceId) throws PnWebloggerException {
		PnWeblog weblog = pnWeblogService.getByUserAndSpaceId(userId, spaceId);
		if(weblog != null && weblog.getPnPerson() != null && weblog.getPnPerson().getPersonId() != null)
			weblog.setPnPerson(pnPersonService.getPerson(weblog.getPnPerson().getPersonId()));
		return weblog;		 
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getWeblogBySpaceId(java.lang.Integer)
	 */
	public PnWeblog getWeblogBySpaceId(Integer spaceId) {
		return pnWeblogService.getBySpaceId(spaceId);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getBySpaceId(java.lang.Integer, boolean)
	 */
	public PnWeblog getWeblogBySpaceId(Integer spaceId, boolean initializePersonObject) {
		return pnWeblogService.getBySpaceId(spaceId, initializePersonObject);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#getWeblogEntries(net.project.hibernate.model.PnWeblog,
	 *      net.project.hibernate.model.PnUser, java.util.Date, java.util.Date, java.lang.String, java.util.List,
	 *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, int)
	 */
	public List getWeblogEntries(Integer weblogId, Integer userId, Date startDate, Date endDate, String status, int offset, int range)
			throws PnWebloggerException {
		return pnWeblogEntryService.getWeblogEntries(weblogId, userId, startDate, endDate, status, offset, range);
	}
		
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getWeblogEntriesFromProjectBlogByPerson(java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date, java.lang.String, int, int)
	 */
	public List<PnWeblogEntry> getWeblogEntriesFromProjectBlogByPerson(Integer personId, Integer projectId, Date startDate, Date endDate, String status, int offset, int range) throws PnWebloggerException {
		return getWeblogEntriesFromProjectBlogByPerson(personId, projectId, startDate, endDate, status, offset, range, null, false);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getWeblogEntriesFromProjectBlogByPerson(java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date, java.lang.String, int, int, java.lang.String, boolean)
	 */
	public List<PnWeblogEntry> getWeblogEntriesFromProjectBlogByPerson(Integer personId, Integer projectId, Date startDate, Date endDate, String status, int offset, int range, String itemType, boolean showImportantBlogEntries) throws PnWebloggerException {
		List<PnWeblogEntry> weblogEntries = pnWeblogEntryService.getWeblogEntriesFromProjectBlogByPerson(personId, projectId, startDate, endDate, status, offset, range, itemType, showImportantBlogEntries);
		if(CollectionUtils.isNotEmpty(weblogEntries)){
			for(PnWeblogEntry entry : weblogEntries){
				entry.setPnWeblogComment(pnWeblogCommentService.getWeblogCommentsForWeblogEntry(entry.getWeblogEntryId()));
			}
		}
		return weblogEntries;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getSortedBlogEntries(java.util.List)
	 */
	public List<PnWeblogEntry> getSortedBlogEntries(List<PnWeblogEntry> entries) {
		return pnWeblogEntryService.getSortedBlogEntries(entries);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#removeWeblogEntry(net.project.hibernate.model.PnWeblogEntry)
	 */
	public void removeWeblogEntry(PnWeblogEntry entry) throws PnWebloggerException {
		pnWeblogEntryService.delete(entry);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#saveWeblogEntryAttribute(net.project.hibernate.model.PnWeblogEntryAttribute)
	 */
	public java.lang.Integer saveWeblogEntryAttribute(PnWeblogEntryAttribute pnWeblogEntryAttribute) throws PnWebloggerException {
		return pnWeblogEntryAttributeService.save(pnWeblogEntryAttribute);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#updateWeblogEntryAttribute(net.project.hibernate.model.PnWeblogEntryAttribute)
	 */
	public void updateWeblogEntryAttribute(PnWeblogEntryAttribute pnWeblogEntryAttribute) throws PnWebloggerException {
		pnWeblogEntryAttributeService.update(pnWeblogEntryAttribute);	
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getTaskIdsFromTaskBlogEntries(java.lang.Integer)
	 */
	public List getTaskIdsFromTaskBlogEntries(Integer spaceId) {
		return pnWeblogEntryAttributeService.getTaskIdsFromTaskBlogEntries(spaceId);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getWeblogEntryAtribute(java.lang.Integer, java.lang.String)
	 */
	public PnWeblogEntryAttribute getWeblogEntryAtribute(Integer weblogEntryId, String name) {
		return pnWeblogEntryAttributeService.getWeblogEntryAtribute(weblogEntryId, name);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getWeblogEntryAtribute(java.lang.Integer, java.lang.String)
	 */
	public List<PnWeblogEntryAttribute> getWeblogEntryAtributesByEntryId(Integer weblogEntryId) {
		return pnWeblogEntryAttributeService.getWeblogEntryAtributesByEntryId(weblogEntryId);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getWeblogEntriesByTaskId(java.lang.Integer)
	 */
	public List<PnWeblogEntry> getWeblogEntriesByTaskId(Integer taskId) {
		List<PnWeblogEntry> weblogEntries = pnWeblogEntryService.getWeblogEntriesByTaskId(taskId);
		if(weblogEntries != null && weblogEntries.size() > 0){
			for(PnWeblogEntry entry : weblogEntries){
				entry.setPnWeblogComment(pnWeblogCommentService.getWeblogCommentsForWeblogEntry(entry.getWeblogEntryId()));
			}
		}
		return weblogEntries;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getWeblogEntriesByObjectId(java.lang.String)
	 */
	public List<PnWeblogEntry> getWeblogEntriesByObjectId(String objectId) {		
		return getWeblogEntriesByObjectId(objectId, null);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getWeblogEntriesByObjectId(java.lang.String, java.lang.String)
	 */
	public List<PnWeblogEntry> getWeblogEntriesByObjectId(String objectId, String status) {
		return getWeblogEntriesByObjectId(objectId, status, null, null);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getWeblogEntriesByObjectId(java.lang.String, java.lang.String, java.util.Date, java.util.Date)
	 */
	public List<PnWeblogEntry> getWeblogEntriesByObjectId(String objectId, String status, Date startDate, Date endDate) {
		List<PnWeblogEntry> weblogEntries = pnWeblogEntryService.getWeblogEntriesByObjectId(objectId, status, startDate, endDate);
		if(weblogEntries != null && weblogEntries.size() > 0){
			for(PnWeblogEntry entry : weblogEntries){
				entry.setPnWeblogComment(pnWeblogCommentService.getWeblogCommentsForWeblogEntry(entry.getWeblogEntryId(), WeblogConstants.COMMENT_APPROVED_STATUS));
			}
		}
		return weblogEntries;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getFilteredWeblogEntries(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date, java.lang.String, int, int, java.lang.String[], boolean, java.lang.String, boolean)
	 */
	public List<PnWeblogEntry> getFilteredWeblogEntries(Integer weblogId, Integer memberId, Integer userId, Integer objectId, Date startDate, Date endDate, String status, int offset, int range, String[] childNodes, boolean showTimeReportedEntries, String itemType, boolean showImportantBlogEntries, Integer currentUserId) throws PnWebloggerException {
		List<PnWeblogEntry> weblogEntries = pnWeblogEntryService.getFilteredWeblogEntries(weblogId, memberId, userId, objectId, startDate, endDate, status, offset, range, childNodes, showTimeReportedEntries, itemType, showImportantBlogEntries, currentUserId); 
		if(weblogEntries != null && weblogEntries.size() > 0){
			for(PnWeblogEntry entry : weblogEntries){
				entry.setPnWeblogComment(pnWeblogCommentService.getWeblogCommentsForWeblogEntry(entry.getWeblogEntryId(), WeblogConstants.COMMENT_APPROVED_STATUS));
                if (userId != null) {
                    entry.setPnProjectSpace(pnProjectSpaceService.getProjectDetailsByWeblogEntryId(entry.getWeblogEntryId()));
                }
			}
		}
		return weblogEntries;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getFilteredWeblogEntries(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date, java.lang.String, int, int, java.lang.String[], boolean, java.lang.String, boolean)
	 */
	public List<PnWeblogEntry> getFilteredWeblogEntries(Integer weblogId, Integer memberId, Integer userId, Integer objectId, Date startDate, Date endDate, String status, int offset, int range, String[] childNodes, boolean showTimeReportedEntries, String itemType, boolean showImportantBlogEntries) throws PnWebloggerException {
		return getFilteredWeblogEntries(weblogId, userId, null, objectId, startDate, endDate, status, offset, range, childNodes, false, null, false, null);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getFilteredWeblogEntries(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date, java.lang.String, int, int, java.lang.String[],boolean)
	 */
	public List<PnWeblogEntry> getFilteredWeblogEntries(Integer weblogId, Integer userId, Integer objectId, Date startDate, Date endDate, String status, int offset, int range, String[] childNodes) throws PnWebloggerException {
		return getFilteredWeblogEntries(weblogId, userId, null, objectId, startDate, endDate, status, offset, range, childNodes, false, null, false); 
		
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getLastBlogEntryOfUser(java.lang.Integer)
	 */
	public PnWeblogEntry getLastBlogEntryOfUser(Integer userId) {
		return pnWeblogEntryService.getLastBlogEntryOfUser(userId, null);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getLastBlogEntryOfUser(java.lang.Integer, java.lang.Integer)
	 */
	public PnWeblogEntry getLastBlogEntryOfUser(Integer userId, Integer currentUserId) {
		return pnWeblogEntryService.getLastBlogEntryOfUser(userId, currentUserId);
	}

	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getLastBlogEntryOfUserByProject(java.lang.Integer, java.lang.Integer)
	 */
	public PnWeblogEntry getLastBlogEntryOfUserByProject(Integer userId, Integer projectId) throws PnWebloggerException {
		return pnWeblogEntryService.getLastBlogEntryOfUserByProject(userId, projectId);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getLastBlogEntiesOfAllUsersBySpace(java.lang.Integer)
	 */
	public List<PnWeblogEntry> getLastBlogEntiesOfAllUsersBySpace(Integer spaceId) {
		return pnWeblogEntryService.getLastBlogEntiesOfAllUsersBySpace(spaceId);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getWeblogEntry(java.lang.Integer)
	 */
	public PnWeblogEntry getWeblogEntry(Integer id) throws PnWebloggerException {
		return pnWeblogEntryService.getWeblogEntry(id);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getWeblogEntryDetail(java.lang.Integer,java.lang.Integer)
	 */
	public PnWeblogEntry getWeblogEntryDetail(Integer id, Integer personId) throws PnWebloggerException {
		PnWeblogEntry pnWeblogEntry  = pnWeblogEntryService.getWeblogEntryDetail(id);
		pnWeblogEntry.setPnWeblogComment(pnWeblogCommentService.getWeblogCommentsForWeblogEntry(pnWeblogEntry.getWeblogEntryId()));
        pnWeblogEntry.setPnProjectSpace(pnProjectSpaceService.getProjectDetailsByWeblogEntryId(id));
		return pnWeblogEntry;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getWeblogEntryDetail(java.lang.Integer,java.lang.Integer)
	 */
	public PnWeblogEntry getWeblogEntryDetail(Integer id) throws PnWebloggerException {
		PnWeblogEntry pnWeblogEntry  = pnWeblogEntryService.getWeblogEntryDetail(id);
		pnWeblogEntry.setPnWeblogComment(pnWeblogCommentService.getWeblogCommentsForWeblogEntry(pnWeblogEntry.getWeblogEntryId()));
		pnWeblogEntry.setPnPerson(personService.getPersonById(pnWeblogEntry.getPnPerson().getPersonId()));
		return pnWeblogEntry;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#removeWeblogEntryAttribute(java.lang.String,
	 *      net.project.hibernate.model.PnWeblogEntry)
	 */
	public void removeWeblogEntryAttribute(PnWeblogEntryAttribute attribute) throws PnWebloggerException {
		pnWeblogEntryAttributeService.delete(attribute);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getWeblogCommentsForWeblogEntry(java.lang.Integer)
	 */
	public Set<PnWeblogComment> getWeblogCommentsForWeblogEntry(Integer weblogEntryId){
		return pnWeblogCommentService.getWeblogCommentsForWeblogEntry(weblogEntryId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#applyCommentDefaultsToEntries(net.project.hibernate.model.PnWeblog)
	 */
	public void applyCommentDefaultsToEntries(PnWeblog pnWeblog) throws PnWebloggerException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#getComment(java.lang.String)
	 */
	public PnWeblogComment getComment(String id) throws PnWebloggerException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#getComments(net.project.hibernate.model.PnWeblog,
	 *      net.project.hibernate.model.PnWeblogComment, java.lang.String, java.util.Date, java.util.Date,
	 *      java.lang.String, boolean, int, int)
	 */
	public List getComments(PnWeblog website, PnWeblogComment entry, String searchString, Date startDate, Date endDate,
			String status, boolean reverseChrono, int offset, int length) throws PnWebloggerException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#getMostCommentedWeblogEntries(net.project.hibernate.model.PnWeblog,
	 *      java.util.Date, java.util.Date, int, int)
	 */
	public List getMostCommentedWeblogEntries(PnWeblog website, Date startDate, Date endDate, int offset, int length)
			throws PnWebloggerException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#getMostCommentedWebsites(java.util.Date, java.util.Date, int,
	 *      int)
	 */
	public List getMostCommentedWeblogs(Date startDate, Date endDate, int offset, int length)
			throws PnWebloggerException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#getNextEntry(net.project.hibernate.model.PnWeblogEntry,
	 *      java.lang.String, java.lang.String)
	 */
	public PnWeblogEntry getNextEntry(PnWeblogEntry current, String catName, String locale) throws PnWebloggerException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#getPreviousEntry(net.project.hibernate.model.PnWeblogEntry,
	 *      java.lang.String, java.lang.String)
	 */
	public PnWeblogEntry getPreviousEntry(PnWeblogEntry current, String catName, String locale)
			throws PnWebloggerException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#getWeblogEntriesPinnedToMain(java.lang.Integer)
	 */
	public List getWeblogEntriesPinnedToMain(Integer max) throws PnWebloggerException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#getWeblogEntryByAnchor(net.project.hibernate.model.PnWeblog,
	 *      java.lang.String)
	 */
	public PnWeblogEntry getWeblogEntryByAnchor(PnWeblog website, String anchor) throws PnWebloggerException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#getWeblogEntryObjectMap(net.project.hibernate.model.PnWeblog,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.List, java.lang.String, java.lang.String, int,
	 *      int)
	 */
	public Map getWeblogEntryObjectMap(PnWeblog website, Date startDate, Date endDate, String catName, List tags,
			String status, String locale, int offset, int range) throws PnWebloggerException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#getWeblogEntryStringMap(net.project.hibernate.model.PnWeblog,
	 *      java.util.Date, java.util.Date, java.lang.String, java.util.List, java.lang.String, java.lang.String, int,
	 *      int)
	 */
	public Map getWeblogEntryStringMap(PnWeblog website, Date startDate, Date endDate, String catName, List tags,
			String status, String locale, int offset, int range) throws PnWebloggerException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#getWebsites(net.project.hibernate.model.PnUser,
	 *      java.lang.Boolean, java.lang.Boolean, java.util.Date, java.util.Date, int, int)
	 */
	public List getWeblogs(PnUser user, Boolean enabled, Boolean active, Date startDate, Date endDate, int offset,
			int length) throws PnWebloggerException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#release()
	 */
	public void release() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#removeComment(net.project.hibernate.model.PnWeblogComment)
	 */
	public void removeComment(PnWeblogComment comment) throws PnWebloggerException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.IBlogProvider#removeMatchingComments(net.project.hibernate.model.PnWeblog,
	 *      net.project.hibernate.model.PnWeblogComment, java.lang.String, java.util.Date, java.util.Date,
	 *      java.lang.String)
	 */
	public int removeMatchingComments(PnWeblog website, PnWeblogComment entry, String searchString, Date startDate,
			Date endDate, String status) throws PnWebloggerException {
		// TODO Auto-generated method stub
		return 0;
	}
	
    /*(non-Javadoc)
     * @see net.project.hibernate.dao.IPnWeblogEntryDAO#getCountOfBlogEntries(java.lang.Integer, java.lang.Integer, java.lang.String, java.util.Date, java.util.Date)
     */
    public int getCountOfBlogEntries(Integer weblogId, Integer userId, String status, Date startDate, Date endDate) throws PnWebloggerException {
        return pnWeblogEntryService.getCountOfBlogEntries(weblogId, userId, status, startDate, endDate);
    }
    
    /*(non-Javadoc)
     * @see net.project.hibernate.dao.IPnWeblogEntryDAO#getCountOfBlogEntries(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date, java.lang.String, boolean, java.lang.String, boolean, java.lang.String[])
     */
    public int getCountOfBlogEntries(Integer weblogId, Integer memberId, Integer userId, Integer objectId, Date startDate, Date endDate, String status, boolean showTimeReportedEntries, String itemType, boolean showImportantBlogEntries, String[] childNodes) throws PnWebloggerException {
        return getCountOfBlogEntries(weblogId, memberId, userId, objectId, startDate, endDate, status, showTimeReportedEntries, itemType, showImportantBlogEntries, childNodes, null);
    }
    
    /*(non-Javadoc)
     * @see net.project.hibernate.dao.IPnWeblogEntryDAO#getCountOfBlogEntries(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date, java.lang.String, boolean, java.lang.String, boolean, java.lang.String[], java.lang.Integer)
     */
    public int getCountOfBlogEntries(Integer weblogId, Integer memberId, Integer userId, Integer objectId, Date startDate, Date endDate, String status, boolean showTimeReportedEntries, String itemType, boolean showImportantBlogEntries, String[] childNodes, Integer currentUserId) throws PnWebloggerException {
        return pnWeblogEntryService.getCountOfBlogEntries(weblogId, memberId, userId, objectId, startDate, endDate, status, showTimeReportedEntries, itemType, showImportantBlogEntries, childNodes, currentUserId);
    }
    
    public PnWeblogEntry getWeblogEntryWithSpaceId(Integer weblogEntryId){
    	return pnWeblogEntryService.getWeblogEntryWithSpaceId(weblogEntryId);
    }
    
    /* (non-Javadoc)
	 * @see net.project.hibernate.service.IBlogProvider#getWeblogCommentsForWeblogEntry(java.lang.Integer, java.lang.String)
	 */
	public Set<PnWeblogComment> getWeblogCommentsForWeblogEntry(Integer weblogEntryId, String status){
		return pnWeblogCommentService.getWeblogCommentsForWeblogEntry(weblogEntryId, status);
	}
}
