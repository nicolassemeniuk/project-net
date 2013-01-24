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
import java.util.Iterator;
import java.util.List;

import net.project.hibernate.model.PnWikiAttachment;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * PnWikiAttachment database object implementation.
 */
@Transactional 
@Repository 
public class PnWikiAttachmentDAOImpl extends AbstractHibernateAnnotatedDAO<PnWikiAttachment, Integer> implements net.project.hibernate.dao.IPnWikiAttachmentDAO {
	
	private static Logger log = Logger.getLogger(PnWikiAttachmentDAOImpl.class);
	
	public PnWikiAttachmentDAOImpl () {
		super(PnWikiAttachment.class);
	}
	
	@SuppressWarnings("unchecked")
	public PnWikiAttachment getFileIdWithWikiPageAndFileName(Integer wikiPageId, String fileName) {
		PnWikiAttachment pnWikiAttachment = null;

		StringBuffer sql = new StringBuffer(" from PnWikiAttachment wa ");
		sql.append(" where wa.wikiPageId = :wikiPageId ");
		sql.append(" and wa.attachmentName = :fileName ");
		sql.append(" and wa.recordStatus = 'A' order by wa.wikiAttachmentId desc ");

		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql.toString());
			query.setInteger("wikiPageId", wikiPageId);
			query.setString("fileName", fileName);
			List<PnWikiAttachment> result = query.list();
			if (result.size() > 0) {
				pnWikiAttachment = result.get(0);
			}
			if (pnWikiAttachment != null) {
				Hibernate.initialize(pnWikiAttachment.getAttachedBy());
			}
		} catch (Exception e) {
			log.error("Error occured while getting the wiki attachment by file name and wiki page ID: " + e.getMessage());
		}
		return pnWikiAttachment;
	}
	
	public PnWikiAttachment getRecordWithWikiPageIdAndFileNameWithStatusAorD(Integer wikiPageId, String fileName) {
		PnWikiAttachment pnWikiAttachment = null;
		
		StringBuffer sql = new StringBuffer(" from PnWikiAttachment wa where wa.wikiPageId = :wikiPageId ");
		sql.append(" and wa.attachmentName = :fileName ");
		
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql.toString());
			query.setInteger("wikiPageId", wikiPageId);
			query.setString("fileName", fileName);
			pnWikiAttachment = (PnWikiAttachment) query.uniqueResult();
			if(pnWikiAttachment != null){
				Hibernate.initialize(pnWikiAttachment.getAttachedBy());
			}
		} catch (Exception e) {
			log.error("Error occured while getting the wiki attachment by file name and wiki page ID: " + e.getMessage());
		}		
		return pnWikiAttachment;
	}
	
	public List<PnWikiAttachment> getAllAttachmentsFromWikiPage(Integer wikiPageId) {
		List<PnWikiAttachment> pnWikiAttachmentList = new ArrayList<PnWikiAttachment>();
		
		StringBuffer sql = new StringBuffer(" from PnWikiAttachment wa where wa.wikiPageId = :wikiPageId ");
		sql.append(" and wa.recordStatus = 'A' ");
		
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql.toString());
			query.setInteger("wikiPageId", wikiPageId);
			Iterator result = query.list().iterator();
			while( result.hasNext() ) {
				PnWikiAttachment pnWikiAttachment = (PnWikiAttachment) result.next();
				pnWikiAttachmentList.add(pnWikiAttachment);
			}
			
		} catch (Exception e) {
			log.error("Error occured while getting the wiki attachments from wiki page with ID: " + e.getMessage());
		}		
		return pnWikiAttachmentList;
	}

	/**
	 * Checks for existence of attachment with specified attachment name on wiki page with given ID,
	 * with specified status(A/D/N).
	 * 
	 * @param pnWikiPage
	 * @param withStaus parameter which determines which status should that attachment have:
	 * <ul>
	 * 	<li><b>A</b> active - this attachment exists on this page and is active.
	 *  <li><b>D</b> deleted - this attachment existed on this page but is now deleted.
	 *  <li><b>N</b> or whichever char, check does attachment with specified name exists on specified wiki page, 
	 *  doesn't matter if status_record field has A or D status.
	 * </ul>
	 * @return true if it exists, false otherwise.
	 */
	public boolean doesAttachmentWithNameExistOnWikiPage(Integer wikiPageId, String attachmentName, char withStatus) {
		boolean result = false;
		
		StringBuffer sqlIsAllreadyAttached = new StringBuffer(" from PnWikiAttachment wa where wa.wikiPageId = :wikiPageId "); 
		sqlIsAllreadyAttached.append(" and wa.attachmentName = :attachmentName ");

		if( withStatus == 'A') {
			sqlIsAllreadyAttached.append(" and wa.recordStatus = 'A' ");
		} else if ( withStatus == 'D') {
			sqlIsAllreadyAttached.append(" and wa.recordStatus = 'D' ");
		}
		
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sqlIsAllreadyAttached.toString());
			query.setInteger("wikiPageId", wikiPageId);
			query.setString("attachmentName", attachmentName);
			Iterator resultIt = query.list().iterator();
			if(resultIt.hasNext()){
				if( resultIt.next() != null )
					result = true;
			}
		} catch (Exception e) {
			log.error("Error occured while determining does wiki page has attachment with specified name: " + e.getMessage());
		}	
		
		return result;
	}
	
	//added
	/**
	 * Method for retrieving attachment information for <b>file</b> with specified wiki file-details page 
	 */
	public PnWikiAttachment getFileWithDetailsPage(Integer fileDetailsWikiPageId, String recordStatus) {
		PnWikiAttachment pnWikiAttachment = null;
		
		StringBuffer sql = new StringBuffer(" from PnWikiAttachment wa where wa.wikiPageId = :wikiPageId ");
		sql.append(" and wa.recordStatus =  :recordStatus");
		
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql.toString());
			query.setInteger("wikiPageId", fileDetailsWikiPageId);
			query.setString("recordStatus", recordStatus);
			pnWikiAttachment = (PnWikiAttachment) query.uniqueResult();
			if(pnWikiAttachment != null){
				Hibernate.initialize(pnWikiAttachment.getAttachedBy());
			}
		} catch (Exception e) {
			log.error("Error occured while getting the wiki attachment by file name and wiki page ID: " + e.getMessage());
		}		
		return pnWikiAttachment;
	}
		
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnWikiAttachmentDAO#deleteAttachmentFromWiki(java.lang.Integer, java.lang.String)
	 */
	public void deleteAttachmentFromWiki(Integer wikiPageId, String attachmentName){
		String sql = " DELETE FROM PnWikiAttachment wa WHERE wa.wikiPageId = :wikiPageId " +
				" AND wa.attachmentName = :attachmentName ";
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("wikiPageId", wikiPageId);
			query.setString("attachmentName", attachmentName);
			int deleted = query.executeUpdate();			
		} catch (Exception e) {
			log.error("Error occured while getting the wiki attachments from wiki page with ID: " + e.getMessage());
		}	
	}
}