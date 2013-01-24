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
package net.project.hibernate.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.base.PnWebloggerException;
import net.project.hibernate.constants.WeblogConstants;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.model.PnWeblog;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.service.IPnProjectSpaceService;
import net.project.hibernate.service.ServiceFactory;
import net.project.util.DateUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Transactional 
@Repository 
public class PnWeblogEntryDAOImpl extends AbstractHibernateAnnotatedDAO<PnWeblogEntry, Integer> implements net.project.hibernate.dao.IPnWeblogEntryDAO {
	
	private static Logger log = Logger.getLogger(PnWeblogEntryDAOImpl.class);
	
	@Autowired
	private IPnProjectSpaceService pnProjectSpaceService; 
	
	public void setPnProjectSpaceService(IPnProjectSpaceService pnProjectSpaceService) {
		this.pnProjectSpaceService = pnProjectSpaceService;
	}

	public PnWeblogEntryDAOImpl () {
		super(PnWeblogEntry.class);
	}
	
	public PnWeblogEntry getWeblogEntry(Integer weblogEntryId){
		PnWeblogEntry entry = findByPimaryKey(weblogEntryId);
		if(entry != null){
			initializeEntity(entry);
		}
		return entry;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnWeblogEntryDAO#getWeblogEntryDetail(java.lang.Integer)
	 */
	public PnWeblogEntry getWeblogEntryDetail(Integer weblogEntryId){
		PnWeblogEntry weblogEntry = new PnWeblogEntry();
		String sql = " select we.weblogEntryId, we.anchor, we.title, we.text, we.pubTime, we.updateTime, "
				+ " we.publishEntry, we.link, we.allowComments, we.commentDays, we.rightToLeft, we.locale, we.status, "
				+ " we.summary, we.contentType, we.contentSrc, we.isImportant, we.pnPerson.personId, "
				+ " we.pnPerson.firstName, we.pnPerson.lastName, we.pnPerson.displayName, we.pnPerson.userStatus " +
				" FROM PnWeblogEntry we WHERE we.weblogEntryId = :weblogEntryId ";
		try {
			Query query = getHibernateTemplate().getSessionFactory()
					.getCurrentSession().createQuery(sql);
			query.setString("weblogEntryId", weblogEntryId.toString());
			Iterator result = query.list().iterator();
			if (result.hasNext()) {
				Object[] row = (Object[]) result.next();
				weblogEntry.setWeblogEntryId((Integer) row[0]);
				weblogEntry.setAnchor(row[1] == null ? "" : (String) row[1]);
				weblogEntry.setTitle(row[2] == null ? "" : (String) row[2]);
				weblogEntry.setText(row[3] == null ? "" : (String) row[3]);
				weblogEntry.setPubTime((Date) row[4]);
				weblogEntry.setUpdateTime((Date) row[5]);
				weblogEntry.setPublishEntry(row[6] == null ? 0
						: (Integer) row[6]);
				weblogEntry.setLink(row[7] == null ? "" : (String) row[7]);
				weblogEntry.setAllowComments(row[8] == null ? 0
						: (Integer) row[8]);
				weblogEntry.setCommentDays(row[9] == null ? 0
						: (Integer) row[9]);
				weblogEntry.setRightToLeft(row[10] == null ? 0
						: (Integer) row[10]);
				weblogEntry.setLocale(row[11] == null ? "" : (String) row[11]);
				weblogEntry.setStatus(row[12] == null ? "" : (String) row[12]);
				weblogEntry.setSummary(row[13] == null ? "" : (String) row[13]);
				weblogEntry.setContentType(row[14] == null ? ""
						: (String) row[14]);
				weblogEntry.setContentSrc(row[15] == null ? ""
						: (String) row[15]);
                weblogEntry.setIsImportant((Integer) row[16]);
                PnPerson person = new PnPerson((Integer) row[17], (String) row[18], (String) row[19], (String) row[20]);
                person.setUserStatus((String) row[21]);
                weblogEntry.setPnPerson(person);
			}
		} catch (Exception e) {
			log.error("Error occured while getting weblog entries : "+e.getMessage());
		}		
		return weblogEntry;
	}

	public PnWeblogEntry getWeblogEntryWithSpaceId(Integer weblogEntryId){
		PnWeblogEntry weblogEntry = new PnWeblogEntry();
		String sql = " select we.weblogEntryId, we.anchor, we.title, we.text, we.pubTime, we.updateTime, "
				+ " we.publishEntry, we.link, we.allowComments, we.commentDays, we.rightToLeft, we.locale, we.status, "
				+ " we.summary, we.contentType, we.contentSrc, we.isImportant, we.pnPerson.personId, we.pnWeblog.weblogId, " 
				+ " we.pnWeblog.spaceId, we.pnPerson.displayName, we.pnPerson.userStatus "
				+ " FROM PnWeblogEntry we WHERE we.weblogEntryId = :weblogEntryId ";
		try {
			Query query = getHibernateTemplate().getSessionFactory()
					.getCurrentSession().createQuery(sql);
			query.setString("weblogEntryId", weblogEntryId.toString());
			Iterator result = query.list().iterator();
			if (result.hasNext()) {
				Object[] row = (Object[]) result.next();
				weblogEntry.setWeblogEntryId((Integer) row[0]);
				weblogEntry.setAnchor(row[1] == null ? "" : (String) row[1]);
				weblogEntry.setTitle(row[2] == null ? "" : (String) row[2]);
				weblogEntry.setText(row[3] == null ? "" : (String) row[3]);
				weblogEntry.setPubTime((Date) row[4]);
				weblogEntry.setUpdateTime((Date) row[5]);
				weblogEntry.setPublishEntry(row[6] == null ? 0
						: (Integer) row[6]);
				weblogEntry.setLink(row[7] == null ? "" : (String) row[7]);
				weblogEntry.setAllowComments(row[8] == null ? 0
						: (Integer) row[8]);
				weblogEntry.setCommentDays(row[9] == null ? 0
						: (Integer) row[9]);
				weblogEntry.setRightToLeft(row[10] == null ? 0
						: (Integer) row[10]);
				weblogEntry.setLocale(row[11] == null ? "" : (String) row[11]);
				weblogEntry.setStatus(row[12] == null ? "" : (String) row[12]);
				weblogEntry.setSummary(row[13] == null ? "" : (String) row[13]);
				weblogEntry.setContentType(row[14] == null ? ""
						: (String) row[14]);
				weblogEntry.setContentSrc(row[15] == null ? ""
						: (String) row[15]);
                weblogEntry.setIsImportant((Integer) row[16]);
                PnPerson person = new PnPerson((Integer) row[17]);
                person.setDisplayName((String) row[20]);
                person.setUserStatus((String) row[21]);
                weblogEntry.setPnPerson(person);
                weblogEntry.setPnWeblog(new PnWeblog(((Integer) row[18]), (Integer) row[19]));
			}
		} catch (Exception e) {
			log.error("Error occured while getting weblog entries : "+e.getMessage());
		}		
		return weblogEntry;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnWeblogEntryDAO#getWeblogEntries(net.project.hibernate.model.PnWeblog, java.lang.Integer, java.util.Date, java.util.Date, java.util.List, java.lang.String, int, int)
	 */
	public List<PnWeblogEntry> getWeblogEntries(Integer weblogId, Integer userId, Date startDate, Date endDate, String status, int offset, int range) throws PnWebloggerException {
		List<PnWeblogEntry> entries = new ArrayList<PnWeblogEntry>();
		String sql = " FROM PnWeblogEntry w WHERE w.status = :status ";
		
		if(weblogId != null)
			sql += " AND w.pnWeblog.weblogId = :weblogId ";
		
		if(userId != null)
			sql += " AND w.pnPerson.personId = :userId ";
		
		if(startDate != null)
			sql += " AND w.pubTime >= :startDate ";
		
		if(endDate != null)
			sql += " AND w.pubTime <= :endDate ";
		
		sql += " ORDER BY w.weblogEntryId DESC ";
		
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setString("status", status);
			
			if(weblogId != null)
				query.setInteger("weblogId", weblogId);
			
			if(userId != null)
				query.setInteger("userId", userId);
			
			if(startDate != null)
				query.setDate("startDate", startDate);
			
			if(endDate != null)
				query.setDate("endDate", endDate);
			
			if(offset != 0)
				query.setFirstResult(offset);
			
			if(range != 0)
				query.setMaxResults(range);
			
			entries = query.list();
			if (entries!= null && entries.size() > 0) {
				for(PnWeblogEntry entry : entries){
					initializeEntity(entry.getPnPerson());
					initializeEntity(entry.getPnWeblogComment());
				}
			}
		} catch (Exception e) {
			log.error("Error occured while getting weblog entries : "+e.getMessage());
		}
		return entries;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnWeblogEntryDAO#getWeblogEntriesByTaskId(java.lang.Integer)
	 */
	public List<PnWeblogEntry> getWeblogEntriesByTaskId(Integer taskId){
		List<PnWeblogEntry> blogEntries = new ArrayList<PnWeblogEntry>();
		
		String sql="SELECT we.weblogEntryId, we.title, we.text, pp.personId, pp.displayName, we.isImportant "+ 
				   " FROM PnWeblogEntryAttribute wea, PnWeblogEntry we, PnPerson pp "+
				   " WHERE wea.name = 'taskId' AND pp.personId = we.pnPerson.personId "+
				   " AND we.weblogEntryId = wea.pnWeblogEntry.weblogEntryId AND wea.value = :taskId ";
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("taskId", taskId);
			Iterator result = query.list().iterator();
			while(result.hasNext()){
				Object[] row = (Object[]) result.next();
				PnWeblogEntry weblogEntry = new PnWeblogEntry();
				weblogEntry.setWeblogEntryId((Integer) row[0]);
				weblogEntry.setTitle((String) row[1]);
				weblogEntry.setText((String) row[2]);
				PnPerson pnPerson = new PnPerson();
				pnPerson.setPersonId((Integer) row[3]);
				pnPerson.setDisplayName((String) row[4]);
				weblogEntry.setPnPerson(pnPerson);
                weblogEntry.setIsImportant((Integer) row[5]);
				blogEntries.add(weblogEntry);
			}
		}catch (Exception e) {
			log.error("Error occured while getting weblog entries : "+e.getMessage());
		}
		return blogEntries;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnWeblogEntryDAO#getWeblogEntriesFromProjectBlogByPerson(java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date, java.lang.String, int, int, java.lang.String, boolean)
	 */
	public List<PnWeblogEntry> getWeblogEntriesFromProjectBlogByPerson(Integer personId, Integer projectId, Date startDate, Date endDate, String status, int offset, int range, String itemType, boolean showImportantBlogEntries) throws PnWebloggerException {
		List<PnWeblogEntry> entries = new ArrayList<PnWeblogEntry>();
        String addTableToQuery = "", addCondition = "";
        if(StringUtils.isNotEmpty(itemType)) {
            addTableToQuery = ", PnWeblogEntryAttribute wea ";
            addCondition = " AND we.weblogEntryId = wea.pnWeblogEntry.weblogEntryId ";
        }
        
        String sql = " SELECT we.weblogEntryId, we.title, we.text, we.pubTime, we.updateTime, we.pnPerson, ps.projectName, ps.projectId, we.isImportant " 
			 	   + " FROM PnWeblogEntry we, PnWeblog w, PnObject o, PnProjectSpace ps" + addTableToQuery 
			 	   + " WHERE w.weblogId = we.pnWeblog.weblogId AND w.spaceId = o.objectId " 
			 	   + " AND ps.projectId = o.objectId AND o.pnObjectType.objectType = 'project' " 
			 	   + " AND we.pnPerson.personId = :personId AND o.recordStatus = 'A' AND we.status = :status " + addCondition;
				   
			
		if(personId != null)
			sql += " AND we.pnPerson.personId = :personId ";
		
		if(projectId != null)
			sql += " AND ps.projectId = :projectId ";
		
		if(startDate != null)
			sql += " AND we.pubTime >= :startDate ";
		
		if(endDate != null)
			sql += " AND we.pubTime <= :endDate ";
		
		if(StringUtils.isNotEmpty(itemType))
			sql += " AND wea.name = :itemType ";
		
		if(showImportantBlogEntries)
			sql += " AND we.isImportant = 1 ";
		
		sql += " ORDER BY we.weblogEntryId DESC ";
		
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setString("status", status);
			
			if(personId != null)
				query.setInteger("personId", personId);
			
			if(projectId != null)
				query.setInteger("projectId", projectId);
			
			if(startDate != null)
				query.setDate("startDate", startDate);
			
			if(endDate != null)
				query.setDate("endDate", endDate);
			
			if(StringUtils.isNotEmpty(itemType))
				query.setString("itemType", itemType);

			if(offset != 0)
				query.setFirstResult(offset);
			
			if(range != 0)
				query.setMaxResults(range);
						
			Iterator result = query.list().iterator();
			while(result.hasNext()){
				Object[] row = (Object[]) result.next();
				PnWeblogEntry weblogEntry = new PnWeblogEntry();
				weblogEntry.setWeblogEntryId((Integer) row[0]);
				weblogEntry.setTitle((String) row[1]);
				weblogEntry.setText((String) row[2]);
				weblogEntry.setPubTime((Date) row[3]);
				weblogEntry.setUpdateTime((Date) row[4]);
				weblogEntry.setPnPerson((PnPerson) row[5]);
				weblogEntry.setProjectEntry(true);
                PnProjectSpace pnProject = new PnProjectSpace();
                pnProject.setProjectId((Integer) row[6]);
                pnProject.setProjectName((String) row[7]);
                weblogEntry.setPnProjectSpace(pnProject);
                weblogEntry.setIsImportant((Integer) row[8]);
				entries.add(weblogEntry);
			}
			if (entries != null && entries.size() > 0) {
				for(PnWeblogEntry entry : entries){
					initializeEntity(entry.getPnWeblogComment());
				}
			}
		} catch (Exception e) {
			log.error("Error occured while getting weblog entries from project blogs by person : "+e.getMessage());
		}
		return entries;
	}
		
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnWeblogEntryDAO#getWeblogEntriesByObjectId(java.lang.String, java.lang.String)
	 */
	public List<PnWeblogEntry> getWeblogEntriesByObjectId(String objectId, String status, Date startDate, Date endDate){
		List<PnWeblogEntry> blogEntries = new ArrayList<PnWeblogEntry>();
		
		String sql=" SELECT we.weblogEntryId, we.title, we.text, we.pubTime, pp.personId, pp.displayName, pp.imageId, we.isImportant "+ 
				   " FROM PnWeblogEntryAttribute wea, PnWeblogEntry we, PnPerson pp "+
				   " WHERE pp.personId = we.pnPerson.personId "+
				   " AND we.weblogEntryId = wea.pnWeblogEntry.weblogEntryId AND wea.value = :objectId ";
		
		if (startDate != null) {
            sql += " AND we.pubTime >= " + DateUtils.toDBDateTime(startDate);
        }

        if (endDate != null) {
            sql += " AND we.pubTime <= " + DateUtils.toDBDateTime(endDate);
        }
		
		if(StringUtils.isNotEmpty(status)){
			sql += "AND we.status = :status ";
		}
		sql += " ORDER BY we.weblogEntryId DESC ";
		
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setString("objectId", objectId);
			
			if(StringUtils.isNotEmpty(status)) {
				query.setString("status", status);
			}
			
			Iterator result = query.list().iterator();
			while(result.hasNext()){
				Object[] row = (Object[]) result.next();
				PnWeblogEntry weblogEntry = new PnWeblogEntry();
				weblogEntry.setWeblogEntryId((Integer) row[0]);
				weblogEntry.setTitle((String) row[1]);
				weblogEntry.setText((String) row[2]);
				weblogEntry.setPubTime((Date) row[3]);
				PnPerson pnPerson = new PnPerson();
				pnPerson.setPersonId((Integer) row[4]);
				pnPerson.setDisplayName((String) row[5]);
				pnPerson.setImageId((Integer) row[6]);
				weblogEntry.setPnPerson(pnPerson);
                weblogEntry.setIsImportant((Integer) row[7]);
				blogEntries.add(weblogEntry);
			}
		} catch (Exception e) {
			log.error("Error occured while getting weblog entries by object id in dao : "+e.getMessage());
		}
		return blogEntries;
	}
	
	/*
	 *(non-Javadoc)
	 * returns a list of project ids visible to logged-in user 
	 *   also includes the logged-in user id
	 */
	private Integer[] getVisibleProjectIdList(Integer userId, Integer currentUserId) {
		Integer[] projectIds = null; 
		List<PnProjectSpace> visibleProjectsList  = pnProjectSpaceService.getProjectsVisibleToUser(userId, currentUserId);
		int count = 0;
		if(CollectionUtils.isNotEmpty(visibleProjectsList)) {
			projectIds = new Integer[visibleProjectsList.size()+1];
			for (PnProjectSpace pnProjectSpace : visibleProjectsList) {
				projectIds[count] = pnProjectSpace.getProjectId();
				count++;
			}
		}
		else
			projectIds = new Integer[1];
		projectIds[count] = userId; // attach the logged-in user id   
		return projectIds;
	}
	
	/* (non-Javadoc)
     * @see net.project.hibernate.dao.IPnWeblogEntryDAO#getFilteredWeblogEntries(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date, java.lang.String, int, int, java.lang.String[], boolean, java.lang.String, boolean, java.lang.Integer)
     */
    public List<PnWeblogEntry> getFilteredWeblogEntries(Integer weblogId, Integer memberId, Integer userId, Integer objectId, Date startDate, Date endDate, String status, int offset, int range, String[] childNodes, boolean showTimeReportedEntries, String itemType, boolean showImportantBlogEntries, Integer currentUserId) throws PnWebloggerException {
    	List<PnWeblogEntry> entries = new ArrayList<PnWeblogEntry>();
    	Integer[] visibleProjectIds = null;
        if(userId != null) { 
        	visibleProjectIds = getVisibleProjectIdList(userId, currentUserId);
        }
        String sql = " SELECT we.weblogEntryId, we.title, we.text, we.pubTime, pp.personId, pp.displayName, "
                + " pp.imageId, we.updateTime, we.isImportant, pp.userStatus , we.locale ";
        
        String fromTable = " FROM PnWeblogEntry we, PnWeblog w, PnObject o, PnPerson pp ";
        
        // visibleProjectIds is a list of visible project ids and logged-in user id
        // this list is used to filter the blog entries of other person (in personal blog)
        String whereClause = " WHERE pp.personId = we.pnPerson.personId AND w.weblogId = we.pnWeblog.weblogId "
                            + (userId != null ? " AND w.spaceId in ( :visibleProjectIds ) " : "" ) + " AND w.spaceId = o.objectId "
                            + " AND we.status = :status ";
        
        // if userId is null assume that loading blog entries for project else load blog entries for person
        if ((userId == null)
                && ((objectId != null && objectId != 0) || (childNodes != null && childNodes.length > 0)
                        || showTimeReportedEntries || StringUtils.isNotEmpty(itemType))) {
        	fromTable += ", PnWeblogEntryAttribute wea ";
            whereClause += " AND we.weblogEntryId = wea.pnWeblogEntry.weblogEntryId ";
        } 
        // to load person's blog entries for project/item type/time reported entries
        else if (userId != null && userId != 0) {
            if (objectId != null && objectId != 0) {
                fromTable += ", PnProjectSpace ps ";
                whereClause += " AND ps.projectId = o.objectId AND o.pnObjectType.objectType = 'project' "
                        + " AND we.pnPerson.personId = :personId AND ps.projectId = :projectId ";
            }
	        if (StringUtils.isNotEmpty(itemType)) {
	            fromTable += ", PnWeblogEntryAttribute wea ";
	            whereClause += " AND we.weblogEntryId = wea.pnWeblogEntry.weblogEntryId ";
	        }
        }
        
        sql += fromTable + whereClause;
        // condition to load blog entries for person
        if (weblogId != null && userId != null && userId != 0) {
            sql += " AND (w.weblogId = :weblogId OR we.pnPerson.personId = :userId) ";
        }
        // condition to load blog entries for project
        else if (weblogId != null) {
            sql += " AND w.weblogId = :weblogId ";
        }
        
        if (memberId != null && memberId != 0) {
            sql += " AND o.pnObjectType.objectType = 'project' AND we.pnPerson.personId = :memberId ";
        }

        if (startDate != null) {
            sql += " AND we.pubTime >= " + DateUtils.toDBDateTime(startDate);
        }

        if (endDate != null) {
            sql += " AND we.pubTime <= " + DateUtils.toDBDateTime(endDate);
        }
        
        if (childNodes != null && childNodes.length > 0)
            sql += " AND wea.value IN ( :childNodes ) ";
        
        if (showTimeReportedEntries) {
            sql += " AND wea.name = 'workSubmitted' ";

            if (StringUtils.isNotEmpty(itemType) || (objectId != null && objectId != 0)) {
                sql += " AND (we.weblogEntryId = "
                        + " (select pwe.weblogEntryId from PnWeblogEntry pwe, PnWeblogEntryAttribute wea "
                        + "   where pwe.weblogEntryId = wea.pnWeblogEntry.weblogEntryId and pwe.weblogEntryId = we.weblogEntryId ";

                if (StringUtils.isNotEmpty(itemType)) {
                    sql += " and wea.name = '" + itemType + "' ";
                }

                if (objectId != null && objectId != 0) {
                    sql += " AND wea.value = '" + objectId + "' ";
                }

                sql += " ))";
            }
        } else if (objectId != null && objectId != 0 && userId == null) {
            sql += " AND wea.value = '" + objectId +"' ";
        } else if (StringUtils.isNotEmpty(itemType)) {
            sql += " AND wea.name = '" + itemType + "'";
        }
        
        if (showImportantBlogEntries)
            sql += " AND we.isImportant = 1 ";
        
        sql += " ORDER BY we.weblogEntryId DESC ";
        
        try {
            Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
            query.setString("status", status);
            
            if (weblogId != null && userId != null && userId != 0) {
                query.setInteger("weblogId", weblogId);
                query.setInteger("userId", userId);
            } else if (weblogId != null) {
                query.setInteger("weblogId", weblogId);
            }

            if (userId != null && userId != 0) {
            	 query.setParameterList("visibleProjectIds", visibleProjectIds, Hibernate.INTEGER);
            	 if(objectId != null && objectId != 0) {
	            	 query.setInteger("personId", userId);
	            	 query.setInteger("projectId", objectId);
            	 }
            } 

            if (memberId != null && memberId != 0)
                query.setInteger("memberId", memberId);

            if (childNodes != null && childNodes.length > 0)
                query.setParameterList("childNodes", childNodes);

            if (offset != 0)
                query.setFirstResult(offset);

            if (range != 0)
                query.setMaxResults(range);
            
            Iterator result = query.list().iterator();
            while (result.hasNext()) {
                Object[] row = (Object[]) result.next();
                PnWeblogEntry weblogEntry = new PnWeblogEntry();
                weblogEntry.setWeblogEntryId((Integer) row[0]);
                weblogEntry.setTitle((String) row[1]);
                weblogEntry.setText((String) row[2]);
                weblogEntry.setPubTime((Date) row[3]);
                PnPerson pnPerson = new PnPerson();
                pnPerson.setPersonId((Integer) row[4]);
                pnPerson.setDisplayName((String) row[5]);
                pnPerson.setImageId((Integer) row[6]);
                weblogEntry.setPnPerson(pnPerson);
                weblogEntry.setUpdateTime((Date) row[7]);
                weblogEntry.setIsImportant((Integer) row[8]);
                pnPerson.setUserStatus((String) row[9]);
                weblogEntry.setLocale((String)row[10]);
                entries.add(weblogEntry);
            }
            
        } catch (Exception e) {
            log.error("Error occured while getting filtered weblog entries in dao : " + e.getMessage());
        }
        return entries;
    }

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnWeblogEntryDAO#getLastBlogEntryOfUser(java.lang.Integer)
	 */
	public PnWeblogEntry getLastBlogEntryOfUser(Integer userId, Integer currentUserId){
		Integer[] visibleProjectIds = null;
        if(userId != null){
        	visibleProjectIds = getVisibleProjectIdList(userId, currentUserId);
        }
		PnWeblogEntry entry = null;
		
		String sql = " SELECT we FROM PnWeblogEntry we, PnWeblog w " +
					"   WHERE w.weblogId = we.pnWeblog.weblogId AND we.status = :status AND we.pnPerson.personId = :userId AND w.spaceId in ( :visibleProjectIds ) " +
					"   ORDER BY we.weblogEntryId DESC ";
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("userId", userId);
			query.setParameterList("visibleProjectIds", visibleProjectIds, Hibernate.INTEGER);
			query.setString("status", WeblogConstants.STATUS_PUBLISHED);
			query.setFirstResult(0);
			query.setMaxResults(1);
			List<PnWeblogEntry> entries =  query.list();
			if(entries != null && entries.size() > 0){
				entry = entries.get(0);				
			}
			if (entry != null && entry.getPnWeblog() != null) {
				initializeEntity(entry.getPnWeblog());
			}
		}catch (Exception e) {
			log.error("Error occured while getting last weblog entry of user in dao : " + e.getMessage());
		}
		return entry;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnWeblogEntryDAO#getLastBlogEntryOfUserByProject(java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public PnWeblogEntry getLastBlogEntryOfUserByProject(Integer userId, Integer projectId) {		
		PnWeblogEntry entry = null;
		String sql = " SELECT we FROM PnWeblogEntry we "
				+ " WHERE we.pnWeblog.spaceId = :projectId AND we.pnPerson.personId = :userId "
				+ "AND we.status = :status ORDER BY we.weblogEntryId DESC ";

		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("userId", userId);
			query.setInteger("projectId", projectId);
			query.setString("status", WeblogConstants.STATUS_PUBLISHED);
			query.setMaxResults(1);
			entry = (PnWeblogEntry) query.uniqueResult();

			if (entry != null && entry.getPnWeblog() != null) {
				initializeEntity(entry.getPnWeblog());
			}
		} catch (Exception e) {
			log.error("Error occured while getting last weblog entry of user in dao : " + e.getMessage());
		}		
		return entry;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnWeblogEntryDAO#getLastBlogEntiesOfAllUsersBySpace(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public List<PnWeblogEntry> getLastBlogEntiesOfAllUsersBySpace(Integer spaceId) {
		List<PnWeblogEntry> entries = new ArrayList<PnWeblogEntry>();
		
		String sql = " SELECT we.pnPerson.personId, max(we.pubTime) as pubTime FROM PnWeblogEntry we, PnWeblog w "
				+ "   WHERE we.status = :status AND we.pnWeblog.weblogId = w.weblogId AND w.spaceId = :spaceId "
				+ "   GROUP BY we.pnPerson.personId ";				

		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("spaceId", spaceId);
			query.setString("status", WeblogConstants.STATUS_PUBLISHED);
			Iterator results = query.iterate();
			while(results.hasNext()){
				Object[] row = (Object []) results.next();
				PnWeblogEntry entry = new PnWeblogEntry();							
				entry.setPubTime((Date) row[1]);
				PnPerson person = new PnPerson((Integer) row[0]);
				entry.setPnPerson(person);
				entries.add(entry);
			}
		} catch (Exception e) {
			log.error("Error occured while getting last weblog entry of user in dao : " + e.getMessage());
		}		
		return entries;
	}
    
    /* (non-Javadoc)
     * @see net.project.hibernate.dao.IPnWeblogEntryDAO#getCountOfBlogEntries(java.lang.Integer, java.lang.Integer, java.lang.String, java.util.Date, java.util.Date)
     */
    public int getCountOfBlogEntries(Integer weblogId, Integer userId, String status, Date startDate, Date endDate) throws PnWebloggerException {
        String sql = " SELECT count(w.weblogEntryId) FROM PnWeblogEntry w WHERE w.status = :status ";
        int totalBlogEntriesCount = 0;
        
        if (weblogId != null)
            sql += " AND w.pnWeblog.weblogId = :weblogId ";
        
        if (userId != null && userId != 0) 
            sql += " AND w.pnPerson.personId = :userId ";
        
        if(startDate != null)
			sql += " AND w.pubTime >= :startDate ";
		
		if(endDate != null)
			sql += " AND w.pubTime <= :endDate ";
        
        try {
            Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
            query.setString("status", status);
            
            if (weblogId != null)
                query.setInteger("weblogId", weblogId);
            
            if (userId != null && userId != 0)
                query.setInteger("userId", userId);
            
            if(startDate != null)
				query.setDate("startDate", startDate);
			
			if(endDate != null)
				query.setDate("endDate", endDate);
            
            totalBlogEntriesCount = Integer.parseInt(query.uniqueResult().toString());

        }catch (Exception e) {
            log.error("Error occurred while getting count of blog entries : "+e.getMessage());
        }
        return totalBlogEntriesCount;
    }
	
    /*(non-Javadoc)
     * @see net.project.hibernate.dao.IPnWeblogEntryDAO#getCountOfBlogEntries(java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date, java.lang.String, int, int, java.lang.String[],boolean, java.lang.String, boolean, java.lang.Integer)
     */
    public int getCountOfBlogEntries(Integer weblogId, Integer memberId, Integer userId, Integer objectId, Date startDate, Date endDate, String status, boolean showTimeReportedEntries, String itemType, boolean showImportantBlogEntries, String[] childNodes, Integer currentUserId) throws PnWebloggerException {
    	Integer[] visibleProjectIds = null;
        if(userId != null){
        	visibleProjectIds = getVisibleProjectIdList(userId, currentUserId);
        }
        
        String sql = " SELECT count(we.weblogEntryId) ";
        
        String fromTable = " FROM PnWeblogEntry we, PnWeblog w, PnObject o, PnPerson pp ";
        
        String whereClause = " WHERE pp.personId = we.pnPerson.personId AND w.weblogId = we.pnWeblog.weblogId "
        					+ (userId != null ? " AND w.spaceId in ( :visibleProjectIds )" : "" ) + " AND w.spaceId = o.objectId  "
                            + " AND we.status = :status ";
        
        // if userId is null assume that loading blog entries for project else load blog entries for person
        if ((userId == null) && ((objectId != null && objectId != 0) || showTimeReportedEntries || StringUtils.isNotEmpty(itemType)) || childNodes != null) {
            fromTable += ", PnWeblogEntryAttribute wea ";
            whereClause += " AND we.weblogEntryId = wea.pnWeblogEntry.weblogEntryId ";
        } 
        // to load person's blog entries for project/item type/time reported entries
        else if (userId != null && userId != 0) {
            if (objectId != null && objectId != 0) {
                fromTable += ", PnProjectSpace ps ";
                whereClause += " AND ps.projectId = o.objectId AND o.pnObjectType.objectType = 'project' "
                        + " AND we.pnPerson.personId = :personId AND ps.projectId = :projectId ";
            }
	        if (StringUtils.isNotEmpty(itemType)) {
                fromTable += ", PnWeblogEntryAttribute wea ";
                whereClause += " AND we.weblogEntryId = wea.pnWeblogEntry.weblogEntryId ";
	        }
        }
        
        if (childNodes != null && childNodes.length > 0)
        	whereClause += " AND wea.value IN ( :childNodes ) ";
        
        sql += fromTable + whereClause;
        // condition to load blog entries for person
        if (weblogId != null && userId != null && userId != 0) {
            sql += " AND (w.weblogId = :weblogId OR we.pnPerson.personId = :userId) ";
        }
        // condition to load blog entries for project
        else if (weblogId != null) {
            sql += " AND w.weblogId = :weblogId ";
        }
        
        if (memberId != null && memberId != 0) {
            sql += " AND o.pnObjectType.objectType = 'project' AND we.pnPerson.personId = :memberId ";
        }

        if (startDate != null) {
            sql += " AND we.pubTime >= " + DateUtils.toDBDateTime(startDate);
        }

        if (endDate != null) {
            sql += " AND we.pubTime <= " + DateUtils.toDBDateTime(endDate);
        }
        
        if (showTimeReportedEntries) {
            sql += " AND wea.name = 'workSubmitted' ";

            if (StringUtils.isNotEmpty(itemType) || (objectId != null && objectId != 0)) {
                sql += " AND (we.weblogEntryId = "
                        + " (select pwe.weblogEntryId from PnWeblogEntry pwe, PnWeblogEntryAttribute wea "
                        + "   where pwe.weblogEntryId = wea.pnWeblogEntry.weblogEntryId and pwe.weblogEntryId = we.weblogEntryId ";

                if (StringUtils.isNotEmpty(itemType)) {
                    sql += " and wea.name = '" + itemType + "' ";
                }

                if (objectId != null && objectId != 0) {
                    sql += " AND wea.value = '" + objectId + "' ";
                }

                sql += " ))";
            }
        } else if (objectId != null && objectId != 0 && userId == null) {
            sql += " AND wea.value = '" + objectId +"' ";
        } else if (StringUtils.isNotEmpty(itemType)) {
            sql += " AND wea.name = '" + itemType + "'";
        }
        
        if (showImportantBlogEntries)
            sql += " AND we.isImportant = 1 ";
        
        sql += " ORDER BY we.weblogEntryId DESC ";
        int totalBlogEntriesCount = 0;
        try {
            Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
            query.setString("status", status);
            
            if (weblogId != null && userId != null && userId != 0) {
                query.setInteger("weblogId", weblogId);
                query.setInteger("userId", userId);
            } else if (weblogId != null) {
                query.setInteger("weblogId", weblogId);
            }

            if (userId != null && userId != 0) {
            	query.setParameterList("visibleProjectIds", visibleProjectIds, Hibernate.INTEGER);
            	if(objectId != null && objectId != 0) {
	            	 query.setInteger("personId", userId);
	            	 query.setInteger("projectId", objectId);
            	}
			} 

            if (memberId != null && memberId != 0)
                query.setInteger("memberId", memberId);
            
            if (childNodes != null && childNodes.length > 0)
                query.setParameterList("childNodes", childNodes);
            
            totalBlogEntriesCount = Integer.parseInt(query.uniqueResult().toString());
            
        } catch (Exception e) {
            log.error("Error occurred while getting count of blog entries : " + e.getMessage() +"  "+sql);
        }
        return totalBlogEntriesCount;
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.dao.IPnWeblogEntryDAO#isWeblogEntryDeleted(java.lang.Integer)
     */
    public boolean isWeblogEntryDeleted(Integer weblogEntryId){
    	boolean isBlogEntryDeleted = false;
    	String sql = " SELECT we.weblogEntryId FROM PnWeblogEntry we "
    				 + " WHERE we.weblogEntryId = :weblogEntryId "
    				 +" AND we.status = :status ";
    	try {
			Query query = getHibernateTemplate().getSessionFactory()
					.getCurrentSession().createQuery(sql);
			query.setInteger("weblogEntryId", weblogEntryId);
			query.setString("status", WeblogConstants.STATUS_DELETED);
			isBlogEntryDeleted = query.uniqueResult() != null;
		} catch (Exception e) {
			log.error("Error occured while checking blog entry deleted or not : "+e.getMessage());
		}		
		return isBlogEntryDeleted;
    }   
}
