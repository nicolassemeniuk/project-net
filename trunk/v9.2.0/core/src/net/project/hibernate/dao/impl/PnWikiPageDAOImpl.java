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

import net.project.hibernate.model.PnWikiPage;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * PnWikiPage database object implementation.
 */
@Transactional 
@Repository 
public class PnWikiPageDAOImpl extends AbstractHibernateAnnotatedDAO<PnWikiPage, Integer> implements net.project.hibernate.dao.IPnWikiPageDAO {
	
	private static Logger log = Logger.getLogger(PnWikiPageDAOImpl.class);
	
	public PnWikiPageDAOImpl () {
		super(PnWikiPage.class);
	}

	/**
	 * Method for returning page in wiki table by page name and object that owns that page (e.g. 'ProjectA')
	 */
	@SuppressWarnings("unchecked")
	public PnWikiPage getWikiPageWithName(String pageName, Integer ownerObjectId) {
		PnWikiPage pnWikiPage = null;
		
		if( pageName.startsWith("image:") ) {
			pageName = "I" + pageName.substring(1, pageName.length());
		}
		
		StringBuffer sql = new StringBuffer(" from PnWikiPage w where w.pageName = :pageName ");
		sql.append(" and w.ownerObjectId = :ownerObjectId ");
		sql.append(" and w.recordStatus = 'A' ");
		
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql.toString());
			query.setString("pageName", pageName);
			query.setInteger("ownerObjectId", ownerObjectId);
			List<PnWikiPage> result =  query.list();
			if (result.size() > 0){
				pnWikiPage = result.get(0);
			}				
			if(pnWikiPage != null){
				initializeEntity(pnWikiPage.getEditedBy());
				initializeEntity(pnWikiPage.getParentPageName());
			}
		} catch (Exception e) {
			log.error("Error occured while getting the wiki page by page name: " + e.getMessage());
		}		
		return pnWikiPage;
	}
	
	public PnWikiPage getRecordWithWikiPageNameAndProjectSpaceIdWithStatusAorD(String pageName, Integer ownerObjectId) {
		PnWikiPage pnWikiPage = null;
		
		StringBuffer sql = new StringBuffer(" from PnWikiPage w where w.pageName = :pageName ");
		sql.append(" and w.ownerObjectId = :ownerObjectId ");
		
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql.toString());
			query.setString("pageName", pageName);
			query.setInteger("ownerObjectId", ownerObjectId);			
			pnWikiPage = (PnWikiPage) query.uniqueResult();
			if(pnWikiPage != null){
				initializeEntity(pnWikiPage.getEditedBy());
				initializeEntity(pnWikiPage.getParentPageName());
			}
		} catch (Exception e) {
			log.error("Error occured while getting the wiki page by page name: " + e.getMessage());
		}		
		return pnWikiPage;
	}
	
	/**
	 * Method that returns list of all subpages (child pages) of wiki page passed as argument.
	 */
	public List<PnWikiPage> getSubPages(PnWikiPage pnWikiPage) {
		List<PnWikiPage> pnWikiSubPages = new ArrayList<PnWikiPage>();
		
		StringBuffer sqlGetSubPages = new StringBuffer(" from PnWikiPage wp where wp.parentPageName = :parentPageName "); 
		sqlGetSubPages.append(" and wp.ownerObjectId = :ownerObjectId ");
		
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sqlGetSubPages.toString());
			query.setString("parentPageName", pnWikiPage.getPageName());
			query.setInteger("ownerObjectId", pnWikiPage.getOwnerObjectId().getObjectId());
			Iterator result = query.list().iterator();
			while(result.hasNext()){
				PnWikiPage subPage = (PnWikiPage) result.next();
				pnWikiSubPages.add(subPage);
			}
		} catch (Exception e) {
			log.error("Error occured while getting subpages of the wiki page: " + e.getMessage());
		}	
		return pnWikiSubPages;
	}
	
	/**
	 * Method which returns list of <i>actual subpages</i> of one wiki page.<br>
	 * <b>Actual subpages</b> are wiki pages that are subpages of the given wiki page, 
	 * but all image-details wiki pages that are children of this page are not considred to be 
	 * acutal subpages.<br>
	 * So 'image-details' pages are excluded from returned list of pages.
	 * @param pnWikiPage
	 * @return
	 */
	public List<PnWikiPage> getActualSubPages(PnWikiPage pnWikiPage) {
		List<PnWikiPage> pnWikiSubPages = new ArrayList<PnWikiPage>();
		
		StringBuffer sqlGetSubPages = new StringBuffer(" from PnWikiPage wp where wp.parentPageName = :parentPageName "); 
		sqlGetSubPages.append(" and wp.pageName NOT LIKE 'Image:%' ");
		sqlGetSubPages.append(" and wp.ownerObjectId = :ownerObjectId ");
		
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sqlGetSubPages.toString());
			query.setString("parentPageName", pnWikiPage.getPageName());
			query.setInteger("ownerObjectId", pnWikiPage.getOwnerObjectId().getObjectId());
			Iterator result = query.list().iterator();
			while(result.hasNext()){
				PnWikiPage subPage = (PnWikiPage) result.next();
				pnWikiSubPages.add(subPage);
			}
		} catch (Exception e) {
			log.error("Error occured while getting subpages of the wiki page: " + e.getMessage());
		}
		return pnWikiSubPages;
	}
	
	public boolean hasSubPages(PnWikiPage pnWikiPage) {
		List<PnWikiPage> pnWikiSubPages = new ArrayList<PnWikiPage>();
		boolean result = false;
		
		StringBuffer sqlHasSubPages = new StringBuffer(" from PnWikiPage wp where wp.parentPageName = :parentPageName "); 
		sqlHasSubPages.append(" and wp.ownerObjectId = :ownerObjectId ");
		
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sqlHasSubPages.toString());
			query.setString("parentPageName", pnWikiPage.getPageName());
			query.setInteger("ownerObjectId", pnWikiPage.getOwnerObjectId().getObjectId());
			Iterator resultIt = query.list().iterator();
			if(resultIt.hasNext()){
				if( resultIt.next() != null )
					result = true;
			}
		} catch (Exception e) {
			log.error("Error occured while determining if wiki page has subpages: " + e.getMessage());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.dao.IPnWikiPageDAO#getDeletedWikiPages(java.lang.Integer)
	 */
	public List<PnWikiPage> getWikiPagesByOwnerAndRecordStatus(Integer ownerObjectId, String recordStatus) {
		List<PnWikiPage> list = new ArrayList<PnWikiPage>();
		String sql = " from PnWikiPage wp where wp.ownerObjectId = :pageId " 
				   + " and wp.recordStatus = :recordStatus order by UPPER(wp.pageName)";
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("pageId", ownerObjectId);
			query.setString("recordStatus",recordStatus);
			Iterator result = query.list().iterator();
			while(result.hasNext()){
				PnWikiPage subPage = (PnWikiPage) result.next();
				if( subPage != null ) {
					initializeEntity(subPage.getEditedBy());
					initializeEntity(subPage.getParentPageName());
				}
				list.add(subPage);
			}
		} catch (Exception e) {
			log.error("Error occured while retriving the wikipage contents with recordStatus: " + recordStatus
					+ ", and id: " + ownerObjectId + ". "+ e.getMessage());
		}
		return list;
	}
		
	/**
	 * Checks for existence of wiki page with specified name for given project id (in specified project space).
	 * Doesn't matter which status is "D" or "A".
	 * 
	 * @param pnWikiPage
	 * @param projectSpaceId
	 * @return true if it exists, false otherwise.
	 */
	public boolean doesWikiPageWithGivenNameExist(String wikiPageName, Integer projectSpaceId) {
		boolean result = false;
		
		StringBuffer sqlDoesAllreadyExist = new StringBuffer(" from PnWikiPage wp where wp.pageName = :wikiPageName "); 
		sqlDoesAllreadyExist.append(" and wp.ownerObjectId = :projectSpaceId ");
		
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sqlDoesAllreadyExist.toString());
			query.setString("wikiPageName", wikiPageName);
			query.setInteger("projectSpaceId", projectSpaceId);
			Iterator resultIt = query.list().iterator();
			if(resultIt.hasNext()){
				if( resultIt.next() != null )
					result = true;
			}
		} catch (Exception e) {
			log.error("Error occured while determining does wiki page with given name exists in project space: " + e.getMessage());
		}	
		
		return result;
	}
	
	//added for retrieving list of images available for attaching to wiki pages of one wiki
	/**
	 * Method that retrieves images-detail wiki pages for all images uploaded to wiki for specified object.<br>
	 * TODO: remove this method. This method is obsolete use same method with 2 parameters instead
	 * @param ownerObjectId id of object which wiki we are currently using
	 * @param objectName name of object which wiki we are currently using
	 * @param recordStatus
	 */
	public List<PnWikiPage> getAllImageDetailPagesForWiki(Integer ownerObjectId, String objectName, String recordStatus) {
		List<PnWikiPage> resultList = new ArrayList<PnWikiPage>();
		String sql = " from PnWikiPage wp where wp.ownerObjectId = :ownerObjectId " 
						+ " and wp.pageName LIKE 'Image:%'"
						+ " and wp.parentPageName = :parentPageName"
						+ " and wp.recordStatus = :recordStatus order by wikiPageId ";

		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("ownerObjectId", ownerObjectId);
			query.setString("parentPageName", objectName);
			query.setString("recordStatus",recordStatus);
			Iterator result = query.list().iterator();
			while(result.hasNext()){
				PnWikiPage imagePage = (PnWikiPage) result.next();
				resultList.add(imagePage);
			}
		} catch (Exception e) {
			log.error("Error occured while retriving all image-details pages for specified wiki!\n " + e.getMessage());
		}
		return resultList;
	}
	
	public List<PnWikiPage> getAllImageDetailPagesForWiki(Integer ownerObjectId, String recordStatus) {
		List<PnWikiPage> resultList = new ArrayList<PnWikiPage>();
		String sql = " from PnWikiPage wp where wp.ownerObjectId = :ownerObjectId " 
						+ " and wp.pageName LIKE 'Image:%'"
						+ " and wp.recordStatus = :recordStatus order by wikiPageId ";

		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("ownerObjectId", ownerObjectId);
			query.setString("recordStatus",recordStatus);
			Iterator result = query.list().iterator();
			while(result.hasNext()){
				PnWikiPage imagePage = (PnWikiPage) result.next();
				resultList.add(imagePage);
			}
		} catch (Exception e) {
			log.error("Error occured while retriving all image-details pages for specified wiki!\n " + e.getMessage());
		}
		return resultList;
	}

	public List<PnWikiPage> getWikiPagesByDate(Integer spaceId, int numberOfPages, String namespace) {
		List<PnWikiPage> resultList = new ArrayList<PnWikiPage>();
		StringBuffer sql = new StringBuffer(" from PnWikiPage wp where wp.ownerObjectId = :ownerObjectId ");
		sql.append(" and wp.recordStatus = :recordStatus  ");
		if( namespace != null && !namespace.equals("") ) {
			sql.append(" and wp.pageName LIKE :namespace");
		}
		sql.append(" order by editDate desc ");

		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql.toString());
			query.setInteger("ownerObjectId", spaceId);
			query.setString("recordStatus", "A");
			if( namespace != null && !namespace.equals("") ) {
				query.setString("namespace", namespace + ":%");
			}
			
			Iterator result = query.list().iterator();
			int i = 0;
			while( result.hasNext() && (i < numberOfPages) ){
				PnWikiPage wikiPage = (PnWikiPage) result.next();
				if( wikiPage != null ) {
					initializeEntity(wikiPage.getEditedBy());
				}
				resultList.add(wikiPage);
				i++;
			}
		} catch (Exception e) {
			log.error("Error occured while retriving wiki pages by date for specified wiki!\n " + e.getMessage());
		}
		return resultList;
	}
	
	public PnWikiPage getRootPageForObject(Integer objectId) {
		PnWikiPage rootPage = null;
		
		//getting all page with parentPageName set to null (root pages for specified object)
		StringBuffer sqlMaxId = new StringBuffer("from PnWikiPage t ");
		sqlMaxId.append(" where t.parentPageName is null and t.recordStatus = 'A' and t.ownerObjectId = :ownerObjectId ");
		sqlMaxId.append(" order by wikiPageId desc ");
		
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sqlMaxId.toString());
			query.setInteger("ownerObjectId", objectId);			
			Iterator result = query.list().iterator();
			if( result.hasNext() ) {
				rootPage = (PnWikiPage) result.next();
			}
			if( rootPage != null ){
				initializeEntity(rootPage.getEditedBy());
			}
		} catch (Exception e) {
			log.error("Error occured while getting the root wiki page: " + e.getMessage());
		}
		
		return rootPage;
	}
    
    /* (non-Javadoc)
     * @see net.project.hibernate.dao.IPnWikiPageDAO#getWikiPageByPageId(java.lang.Integer)
     */
    public PnWikiPage getWikiPageByPageId(Integer pageId) {
        PnWikiPage wikiPage = null;
        StringBuffer sqlMaxId = new StringBuffer("from PnWikiPage wp ");
        sqlMaxId.append(" where wp.wikiPageId = :wikiPageId");
        try {
            Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(
                            sqlMaxId.toString());
            query.setInteger("wikiPageId", pageId);

            wikiPage = (PnWikiPage) query.uniqueResult();
            if (wikiPage != null) {
            	initializeEntity(wikiPage.getEditedBy());
                if (wikiPage.getParentPageName() != null) {
                	initializeEntity(wikiPage.getParentPageName());
                }
            }
        } catch (Exception e) {
            log.error("Error occured while getting the wiki page: " + e.getMessage());
        }
        return wikiPage;
    }

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnWikiPageDAO#getWikiPageCountForProject(java.lang.Integer, java.util.Date, java.util.Date)
	 */
	public Integer getWikiPageCountForProject(Integer projectId, Date startDate, Date endDate) {
		Integer wikiPageCount = null;
		String sql = "select count(*) from PnWikiPage wp " 
				+ " where wp.ownerObjectId = :projectId and wp.recordStatus = 'A' " 
			    + " and editDate between :startDate and :endDate ";
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			if (projectId != null) {
				query.setInteger("projectId", projectId);
			}
			
			if (startDate != null) {
				query.setDate("startDate", startDate);
			}
			
			if (endDate != null) {
				query.setDate("endDate", endDate);
			}
			
			wikiPageCount = Integer.parseInt(query.uniqueResult().toString());
		} catch (Exception e) {
			log.error("Error occured while getting the wiki page count for project: " + e.getMessage());
		}
		return wikiPageCount;
	}
    
    /* (non-Javadoc)
     * @see net.project.hibernate.dao.IPnWikiPageDAO#getRootWikiPageByName(java.lang.String)
     */
    public PnWikiPage getRootWikiPageByName(String pageName) {
        PnWikiPage pnWikiPage = null;
        try {
            StringBuffer sql = new StringBuffer(" from PnWikiPage wp where wp.pageName = :pageName ");
            sql.append(" and wp.parentPageName = NULL ");
            sql.append(" and wp.recordStatus = 'A' ");
            Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql.toString());
            query.setString("pageName", pageName);
            pnWikiPage = (PnWikiPage) query.uniqueResult();
        } catch (Exception e) {
            log.error("Error occured while getting the wiki page by name: " + e.getMessage());
        }
        return pnWikiPage;
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.dao.IPnWikiPageDAO#getRootWikiPageByName(java.lang.String)
     */
    public List<PnWikiPage> getRootWikiPagesByName(String pageName) {
        List<PnWikiPage> pnWikiPageList = new ArrayList<PnWikiPage>();
        try {
            StringBuffer sql = new StringBuffer(" from PnWikiPage wp where wp.pageName like :pageName ");
            sql.append(" and wp.parentPageName = NULL ");
            sql.append(" and wp.recordStatus = 'A' ");
            Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql.toString());
            query.setString("pageName", pageName+"%");
            pnWikiPageList = query.list();
        } catch (Exception e) {
            log.error("Error occured while getting the wiki page by name: " + e.getMessage());
        }
        return pnWikiPageList;
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.dao.IPnWikiPageDAO#getChildWikiPageByName(java.lang.String, java.lang.Integer)
     */
    public PnWikiPage getChildWikiPageByName(String pageName, Integer parentPageId) {
        if( pageName.startsWith("image:") ) {
            pageName = "I" + pageName.substring(1, pageName.length());
        }
        
        PnWikiPage pnWikiPage = null;
        try {
            StringBuffer sql = new StringBuffer(" from PnWikiPage wp where wp.pageName = :pageName ");
            sql.append(" and wp.parentPageName.wikiPageId =  :parentPageId");
            Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql.toString());
            query.setString("pageName", pageName);
            query.setInteger("parentPageId", parentPageId);
            pnWikiPage = (PnWikiPage) query.uniqueResult();
        } catch (Exception e) {
            log.error("Error occured while getting the child wiki page by name: " + e.getMessage());
        }
        return pnWikiPage;
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.dao.IPnWikiPageDAO#getWikiPageByObjectId(java.lang.Integer)
     */
    public PnWikiPage getWikiPageByObjectId(Integer objectId) {
        PnWikiPage pnWikiPage = null;
        try {
            StringBuffer sql = new StringBuffer(" from PnWikiPage wp, PnWikiAssignment pwa where wp.wikiPageId = pwa.comp_id.wikiPageId");
            sql.append(" and pwa.comp_id.objectId =  :objectId");
            sql.append(" and wp.recordStatus = 'A' ");
            Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql.toString());
            query.setInteger("objectId", objectId);
            pnWikiPage = (PnWikiPage) query.uniqueResult();
        } catch (Exception e) {
            log.error("Error occured while getting the wiki page by objectId: " + e.getMessage());
        }
        return pnWikiPage;
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.dao.IPnWikiPageDAO#getAllDocumentsPagesForWiki(java.lang.Integer, java.lang.String)
     */
    public List<PnWikiPage> getAllDocumentsPagesForWiki(Integer ownerObjectId, String recordStatus) {
		List<PnWikiPage> resultList = new ArrayList<PnWikiPage>();
		String sql = " from PnWikiPage wp where wp.ownerObjectId = :ownerObjectId " 
						+ " and (wp.pageName LIKE 'Image:%' or wp.pageName LIKE 'File:%') "
						+ " and wp.recordStatus = :recordStatus order by wikiPageId ";

		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("ownerObjectId", ownerObjectId);
			query.setString("recordStatus", recordStatus);
			Iterator result = query.list().iterator();
			while(result.hasNext()){
				PnWikiPage imagePage = (PnWikiPage) result.next();
				resultList.add(imagePage);
				if(imagePage != null){
					initializeEntity(imagePage.getEditedBy());
					if(imagePage.getParentPageName() != null){
						initializeEntity(imagePage.getParentPageName());
					}
				}
			}
		} catch (Exception e) {
			log.error("Error occured while retriving all image-details pages for specified wiki!\n " + e.getMessage());
		}
		return resultList;
	}
    
    /* (non-Javadoc)
     * @see net.project.hibernate.dao.IPnWikiPageDAO#deleteWikiPageByName(java.lang.String, java.lang.Integer)
     */
    public void deleteWikiPageByName(String pageName, Integer ownerObjectId){
		String sql = " DELETE FROM PnWikiPage wp WHERE wp.ownerObjectId = :ownerObjectId " 
					 + " AND wp.pageName = :pageName ";

		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("ownerObjectId", ownerObjectId);
			query.setString("pageName", pageName);
			int deleted = query.executeUpdate();
		} catch (Exception e) {
			log.error("Error occured while delting the wiki page by name : " + e.getMessage());
		}
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.dao.IPnWikiPageDAO#getWikiPageWithPageNameAndRecordStatusWithParent(java.lang.Integer)
     */
    public PnWikiPage getWikiPageWithPageNameAndRecordStatusWithParent(Integer wikiPageId){
    	PnWikiPage pnWikiPage = null;
    	String sql = null;
    	try{
    	sql = " select new PnWikiPage(wikiPageId, pageName, recordStatus, parentPageName.wikiPageId, parentPageName.pageName)" +
			" from PnWikiPage pw WHERE wikiPageId = :wikiPageId ";
    	
	    	Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("wikiPageId", wikiPageId);
			pnWikiPage = (PnWikiPage) query.uniqueResult();
    	} catch (Exception e) {
    		log.error("Error occured while getting the wiki page with parentId by wikiPageId: " + e.getMessage());
    	}
    return pnWikiPage;
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.dao.IPnWikiPageDAO#getWikiPageWithPageNameAndRecordStatusWithoutParent(java.lang.Integer)
     */
    public PnWikiPage getWikiPageWithPageNameAndRecordStatusWithoutParent(Integer wikiPageId){
    	PnWikiPage pnWikiPage = null;
    	String sql = null;
    	try{
    	sql = " select new PnWikiPage(wikiPageId, pageName, recordStatus)" +
			" from PnWikiPage pw WHERE wikiPageId = :wikiPageId ";
    	
	    	Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("wikiPageId", wikiPageId);
			pnWikiPage = (PnWikiPage) query.uniqueResult();
    	} catch (Exception e) {
    		log.error("Error occured while getting the wiki page without parentId by wikiPageId: " + e.getMessage());
    	}
    return pnWikiPage;
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.dao.IPnWikiPageDAO#getWikiPageWithPageNameAndContent(java.lang.String, java.lang.Integer)
     */
    public PnWikiPage getWikiPageWithPageNameAndContent(Integer wikiPageId) {
		PnWikiPage pnWikiPage = null;
		String sql = null;
		try {
			sql = " select new PnWikiPage(wikiPageId, pageName, editedBy.displayName, editDate, content)"
					+ " from PnWikiPage pw WHERE wikiPageId = :wikiPageId ";

			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("wikiPageId", wikiPageId);
			pnWikiPage = (PnWikiPage) query.uniqueResult();
		} catch (Exception e) {
			log.error("Error occured while getting the wiki page by wikiPageId: " + e.getMessage());
		}
		return pnWikiPage;
	}
    
    /* (non-Javadoc)
     * @see net.project.hibernate.dao.IPnWikiPageDAO#deleteWikiPageById(java.lang.Integer)
     */
    public void deleteWikiPageById(Integer wikiPageId){
		String sql = " DELETE FROM PnWikiPage wp WHERE wp.wikiPageId = :wikiPageId ";

		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("wikiPageId", wikiPageId);
			int deleted = query.executeUpdate();
		} catch (Exception e) {
			log.error("Error occured while delting the wiki page by id : " + e.getMessage());
		}
    }

	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnWikiPageDAO#getWikiPage(java.lang.String, java.lang.Integer)
	 */
	public PnWikiPage getWikiPage(String pageName, Integer ownerObjectId) {
		PnWikiPage pnWikiPage = null;
		
		if( pageName.startsWith("image:") ) {
			pageName = "I" + pageName.substring(1, pageName.length());
		}
		
		StringBuffer sql = new StringBuffer(" from PnWikiPage w where w.pageName = :pageName ");
		sql.append(" and w.ownerObjectId = :ownerObjectId ");
		
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql.toString());
			query.setString("pageName", pageName);
			query.setInteger("ownerObjectId", ownerObjectId);
			List<PnWikiPage> result =  query.list();
			if (result.size() > 0){
				pnWikiPage = result.get(0);
			}				
			if(pnWikiPage != null){
				initializeEntity(pnWikiPage.getEditedBy());
				initializeEntity(pnWikiPage.getParentPageName());
			}
		} catch (Exception e) {
			log.error("Error occured while getting the wiki page by page name and ownerObjectId: " + e.getMessage());
		}		
		return pnWikiPage;
	}
    
}
