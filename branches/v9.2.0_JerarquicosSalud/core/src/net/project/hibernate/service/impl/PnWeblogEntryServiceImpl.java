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

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.project.base.ObjectType;
import net.project.base.PnWebloggerException;
import net.project.hibernate.dao.IPnWeblogEntryDAO;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.model.PnObjectName;
import net.project.hibernate.model.PnObjectSpace;
import net.project.hibernate.model.PnObjectSpacePK;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.service.IPnObjectNameService;
import net.project.hibernate.service.IPnObjectService;
import net.project.hibernate.service.IPnObjectSpaceService;
import net.project.hibernate.service.IPnWeblogEntryService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author
 *
 */
@Service(value="pnWeblogEntryService")
public class PnWeblogEntryServiceImpl implements IPnWeblogEntryService {
	
	private static Logger log = Logger.getLogger(PnWeblogEntryServiceImpl.class);

	/**
	 * PnWeblogEntry data access object 
	 */
	@Autowired
	private IPnWeblogEntryDAO pnWeblogEntryDAO;
	
	@Autowired
	private IPnObjectService pnObjectService;
	
	@Autowired
	private IPnObjectSpaceService pnObjectSpaceService;
	
	@Autowired
	private IPnObjectNameService pnObjectNameService;

	/**
	 * @param pnWeblogEntryDAO the pnWeblogEntryDAO to set
	 */
	public void setPnWeblogEntryDAO(IPnWeblogEntryDAO pnWeblogEntryDAO) {
		this.pnWeblogEntryDAO = pnWeblogEntryDAO;
	}
	
	/**
	 * @param pnObjectService the pnObjectService to set
	 */
	public void setPnObjectService(IPnObjectService pnObjectService) {
		this.pnObjectService = pnObjectService;
	}

	/**
	 * @param pnObjectSpaceService the pnObjectSpaceService to set
	 */
	public void setPnObjectSpaceService(IPnObjectSpaceService pnObjectSpaceService) {
		this.pnObjectSpaceService = pnObjectSpaceService;
	}

	/**
	 * @param pnObjectNameService the pnObjectNameService to set
	 */
	public void setPnObjectNameService(IPnObjectNameService pnObjectNameService) {
		this.pnObjectNameService = pnObjectNameService;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogEntryService#save(net.project.hibernate.model.PnWeblogEntry)
	 */
	public Integer save(PnWeblogEntry pnWeblogEntry) {
		// create object for weblog entry
		PnObject weblogEntryObject = new PnObject(ObjectType.BLOG_ENTRY, new Integer(1), new Date(System.currentTimeMillis()), "A");
		
		// save weblog entry object in pn_object table
		Integer weblogEntryObjectId = pnObjectService.saveObject(weblogEntryObject);
		pnWeblogEntry.setWeblogEntryId(weblogEntryObjectId);		
		
		// create object of pn_object_space for weblog entry
		PnObjectSpacePK pnObjectSpacePK = new PnObjectSpacePK(weblogEntryObjectId, pnWeblogEntry.getPnWeblog().getSpaceId());
		PnObjectSpace pnObjectSpace = new PnObjectSpace(pnObjectSpacePK);
		
		// save weblog entry object id and space id in pn_object_space table
		pnObjectSpaceService.save(pnObjectSpace);
		
		//save weblog entry object id and name in pn_object_name table
		if (StringUtils.isEmpty(pnWeblogEntry.getTitle())) {
            String text = pnWeblogEntry.getText().replaceAll("\\<.*?>", "").replaceAll("/&[#]*[\\w|\\d]*;/g", "").replaceAll("&nbsp;", "").trim();
            pnWeblogEntry.setTitle(text.length() > 40 ? text.substring(0, 40) + "..." : text);
        } else {
        	pnWeblogEntry.setTitle(pnWeblogEntry.getTitle());
        }
		
		PnObjectName pnObjectName = new PnObjectName(weblogEntryObjectId,pnWeblogEntry.getTitle());
		pnObjectNameService.save(pnObjectName);
		
		// save weblog entry in pn_weblog_entry table
		return pnWeblogEntryDAO.create(pnWeblogEntry);
	}	

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogEntryService#update(net.project.hibernate.model.PnWeblogEntry)
	 */
	public void update(PnWeblogEntry pnWeblogEntry) {
		if (StringUtils.isEmpty(pnWeblogEntry.getTitle())) {
            String text = pnWeblogEntry.getText().replaceAll("\\<.*?>", "").replaceAll("/&[#]*[\\w|\\d]*;/g", "").replaceAll("&nbsp;", "").trim();
            pnWeblogEntry.setTitle(text.length() > 40 ? text.substring(0, 40) + "..." : text);
        } else {
        	pnWeblogEntry.setTitle(pnWeblogEntry.getTitle());
        }
		PnObjectName pnObjectName = new PnObjectName(pnWeblogEntry.getWeblogEntryId(), pnWeblogEntry.getTitle());
		pnObjectNameService.update(pnObjectName);
		pnWeblogEntryDAO.update(pnWeblogEntry);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogEntryService#get(java.lang.Integer)
	 */
	public PnWeblogEntry getWeblogEntry(Integer key) {
		return pnWeblogEntryDAO.getWeblogEntry(key);
	}		
	
	public PnWeblogEntry findById(Integer key) {
		return pnWeblogEntryDAO.findByPimaryKey(key);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogEntryService#get(java.lang.Integer)
	 */
	public PnWeblogEntry getWeblogEntryDetail(Integer key) {
		return pnWeblogEntryDAO.getWeblogEntryDetail(key);
	}		

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogEntryService#findAll()
	 */
	public List<PnWeblogEntry> findAll() {
		return pnWeblogEntryDAO.findAll();
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogEntryService#delete(net.project.hibernate.model.PnWeblogEntry)
	 */
	public void delete(PnWeblogEntry pnWeblogEntry) {
		pnWeblogEntryDAO.delete(pnWeblogEntry);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogEntryService#getWeblogEntries(java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date, java.lang.String, int, int)
	 */
	public List<PnWeblogEntry> getWeblogEntries(Integer weblogId, Integer userId, Date startDate, Date endDate, String status, int offset, int range) throws PnWebloggerException {
		return pnWeblogEntryDAO.getWeblogEntries(weblogId, userId, startDate, endDate, status, offset, range);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogEntryService#getWeblogEntriesByTaskId(java.lang.Integer)
	 */
	public List<PnWeblogEntry> getWeblogEntriesByTaskId(Integer taskId) {
		return pnWeblogEntryDAO.getWeblogEntriesByTaskId(taskId);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogEntryService#getWeblogEntriesFromProjectBlogByPerson(java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date, java.lang.String, int, int)
	 */
	public List<PnWeblogEntry> getWeblogEntriesFromProjectBlogByPerson(Integer personId, Integer projectId, Date startDate, Date endDate, String status, int offset, int range) throws PnWebloggerException {
		return getWeblogEntriesFromProjectBlogByPerson(personId, projectId, startDate, endDate, status, offset, range, null, false);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogEntryService#getWeblogEntriesFromProjectBlogByPerson(java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date, java.lang.String, int, int, java.lang.String, boolean)
	 */
	public List<PnWeblogEntry> getWeblogEntriesFromProjectBlogByPerson(Integer personId, Integer projectId, Date startDate, Date endDate, String status, int offset, int range,String itemType, boolean showImportantBlogEntries) throws PnWebloggerException {
		return pnWeblogEntryDAO.getWeblogEntriesFromProjectBlogByPerson(personId, projectId, startDate, endDate, status, offset, range, itemType, showImportantBlogEntries);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogEntryService#getWeblogEntriesByObjectId(java.lang.String)
	 */
	public List<PnWeblogEntry> getWeblogEntriesByObjectId(String objectId) {
		return getWeblogEntriesByObjectId(objectId, null);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogEntryService#getWeblogEntriesByObjectId(java.lang.String, java.lang.String)
	 */
	public List<PnWeblogEntry> getWeblogEntriesByObjectId(String objectId, String status) {
		return getWeblogEntriesByObjectId(objectId, status, null, null);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogEntryService#getWeblogEntriesByObjectId(java.lang.String, java.lang.String, java.util.Date, java.util.Date)
	 */
	public List<PnWeblogEntry> getWeblogEntriesByObjectId(String objectId, String status, Date startDate, Date endDate) {
		return pnWeblogEntryDAO.getWeblogEntriesByObjectId(objectId, status, startDate, endDate);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogEntryService#getFilteredWeblogEntries(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date, java.lang.String, int, int, java.lang.String[], boolean, java.lang.String, boolean, java.lang.Integer)
	 */
	public List<PnWeblogEntry> getFilteredWeblogEntries(Integer weblogId, Integer memberId, Integer userId, Integer objectId, Date startDate, Date endDate, String status, int offset, int range, String[] childNodes, boolean showTimeReportedEntries, String itemType, boolean showImportantBlogEntries, Integer currentUserId) throws PnWebloggerException {
		return pnWeblogEntryDAO.getFilteredWeblogEntries(weblogId, memberId, userId, objectId, startDate, endDate, status, offset, range, childNodes, showTimeReportedEntries, itemType, showImportantBlogEntries, currentUserId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogEntryService#getSortedBlogEntries(java.util.List)
	 */
	public List<PnWeblogEntry> getSortedBlogEntries(List<PnWeblogEntry> entries){
		
		Collections.sort(entries, new Comparator<PnWeblogEntry>(){
			
			/**
			 * Implementing compare method for PnWeblogEntry objects
			 * 
			 * @param entry1
			 * @param entry2
			 * @return integer value 1 or -1
			 */
			public int compare(PnWeblogEntry entry1, PnWeblogEntry entry2) {
		        long pubTime1 = entry1.getPubTime().getTime();
		        long pubTime2 = entry2.getPubTime().getTime();

		        if (pubTime1 > pubTime2) {
		            return -1;
		        } else if (pubTime1 < pubTime2) {
		            return 1;
		        }

		        // if pubTimes are the same, return
		        // results of String.compareTo() on WeblogEntryId
		        return entry1.getWeblogEntryId().compareTo(entry2.getWeblogEntryId());
			}
		});
		
		return entries;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogEntryService#getLastBlogEntryOfUser(java.lang.Integer, java.lang.Integer)
	 */
	public PnWeblogEntry getLastBlogEntryOfUser(Integer userId, Integer currentUserId) {
		return pnWeblogEntryDAO.getLastBlogEntryOfUser(userId, currentUserId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogEntryService#getLastBlogEntryOfUserByProject(java.lang.Integer, java.lang.Integer)
	 */
	public PnWeblogEntry getLastBlogEntryOfUserByProject(Integer userId, Integer projectId) {		
		return pnWeblogEntryDAO.getLastBlogEntryOfUserByProject(userId, projectId);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWeblogEntryService#getLastBlogEntiesOfAllUsersBySpace(java.lang.Integer)
	 */
	public List<PnWeblogEntry> getLastBlogEntiesOfAllUsersBySpace(Integer spaceId) {
		return pnWeblogEntryDAO.getLastBlogEntiesOfAllUsersBySpace(spaceId);
	}
    
    /*(non-Javadoc)
     * @see net.project.hibernate.dao.IPnWeblogEntryDAO#getCountOfBlogEntries(java.lang.Integer, java.lang.Integer, java.lang.String, Date startDate, Date endDate)
     */
    public int getCountOfBlogEntries(Integer weblogId, Integer userId, String status,Date startDate, Date endDate) throws PnWebloggerException {
        return pnWeblogEntryDAO.getCountOfBlogEntries(weblogId, userId, status, startDate,  endDate);
    }
    
    /*(non-Javadoc)
     * @see net.project.hibernate.dao.IPnWeblogEntryDAO#getCountOfBlogEntries(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date, java.lang.String, boolean, java.lang.String, boolean, java.lang.String[], java.lang.Integer)
     */
    public int getCountOfBlogEntries(Integer weblogId, Integer memberId, Integer userId, Integer objectId, Date startDate, Date endDate, String status, boolean showTimeReportedEntries, String itemType, boolean showImportantBlogEntries, String[] childNodes, Integer currentUserId) throws PnWebloggerException {
        return pnWeblogEntryDAO.getCountOfBlogEntries(weblogId, memberId, userId, objectId, startDate, endDate, status, showTimeReportedEntries, itemType, showImportantBlogEntries, childNodes, currentUserId);
    }
    
    public PnWeblogEntry getWeblogEntryWithSpaceId(Integer weblogEntryId){
    	return pnWeblogEntryDAO.getWeblogEntryWithSpaceId(weblogEntryId);
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.service.IPnWeblogEntryService#isWeblogEntryDeleted(java.lang.Integer)
     */
    public boolean isWeblogEntryDeleted(Integer weblogEntryId){
    	return pnWeblogEntryDAO.isWeblogEntryDeleted(weblogEntryId);
    }
}
