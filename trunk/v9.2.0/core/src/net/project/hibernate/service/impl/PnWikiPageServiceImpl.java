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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.project.base.ObjectType;
import net.project.hibernate.dao.IPnWikiHistoryDAO;
import net.project.hibernate.dao.IPnWikiPageDAO;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.model.PnObjectName;
import net.project.hibernate.model.PnObjectSpace;
import net.project.hibernate.model.PnObjectSpacePK;
import net.project.hibernate.model.PnObjectType;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnWikiAssignment;
import net.project.hibernate.model.PnWikiPage;
import net.project.hibernate.service.IPnObjectNameService;
import net.project.hibernate.service.IPnObjectService;
import net.project.hibernate.service.IPnObjectSpaceService;
import net.project.hibernate.service.IPnObjectTypeService;
import net.project.hibernate.service.IPnWikiAssignmentService;
import net.project.hibernate.service.IPnWikiAttachmentService;
import net.project.hibernate.service.IPnWikiPageService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 
 *
 */
@Service(value="pnWikiPageService")
public class PnWikiPageServiceImpl implements IPnWikiPageService {

	private static Logger log = Logger.getLogger(PnWikiPageServiceImpl.class);

	/**
	 * PnWikiPage data access object
	 */
	@Autowired
	private IPnWikiPageDAO pnWikiPageDAO;
	
	@Autowired
	private IPnObjectService pnObjectService;
	
	@Autowired
	private IPnObjectSpaceService pnObjectSpaceService;
	
	@Autowired
	private IPnWikiHistoryDAO pnWikiHistoryDAO;
	
	@Autowired
	private IPnObjectNameService pnObjectNameService;

	public void setObjectService(IPnObjectService objectService) {
		this.objectService = objectService;
	}

	public void setObjectTypeService(IPnObjectTypeService objectTypeService) {
		this.objectTypeService = objectTypeService;
	}

	public void setPnWikiAssignmentService(IPnWikiAssignmentService pnWikiAssignmentService) {
		this.pnWikiAssignmentService = pnWikiAssignmentService;
	}

	public void setWikiAttachmentService(IPnWikiAttachmentService wikiAttachmentService) {
		this.wikiAttachmentService = wikiAttachmentService;
	}

	/**
	 * @param pnWikiPageDAO the pnWikiPageDAO to set
	 */
	public void setPnWikiPageDAO(IPnWikiPageDAO pnWikiPageDAO) {
		this.pnWikiPageDAO = pnWikiPageDAO;
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
	
	/**
	 * @param pnWikiHistoryDAO the pnWikiHistoryDAO to set
	 */
	public void setPnWikiHistoryDAO(IPnWikiHistoryDAO pnWikiHistoryDAO) {
		this.pnWikiHistoryDAO = pnWikiHistoryDAO;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiPageService#save(net.project.hibernate.model.PnWikiPage)
	 */
	public Integer save(PnWikiPage pnWikiPage) {													/* CUSTOMIZED for assignment wiki */
		PnObject pnAssignmentObject = pnWikiPage.getOwnerObjectId();								/* get PnObject for pnWikiPage */
		
		/*if ( pnAssignmentObject.getPnObjectType() != null && pnAssignmentObject.getPnObjectType().getObjectType() != null
				&& pnAssignmentObject.getPnObjectType().getObjectType().equals("task") || 
					pnAssignmentObject.getPnObjectType().getObjectType().equals("form_data") ) {			 if object type is assignment(TASK/FORM) 
			
			IPnAssignmentService assignmentServ = ServiceFactory.getInstance().getPnAssignmentService();
			Integer ownerSpaceId = assignmentServ.getAssigmentByAssignmentId(pnWikiPage.getOwnerObjectId().getObjectId(), Integer.valueOf(SessionManager.getUser().getID()) ).getComp_id().getSpaceId();				 get owner object(project) id as argument 
			
			pnWikiPage.setOwnerObjectId(pnObjectService.getObject(ownerSpaceId));						 set object to be owning PROJECT (for wiki page to be stored in that objects wiki space) 
			
			Set<PnObject> assignements = new HashSet<PnObject>();
			assignements.add(pnAssignmentObject);
			pnWikiPage.setAssignements(assignements);
		} */
		
		// create object for wikipage
		PnObject wikipageObject = new PnObject(ObjectType.WIKI, new Integer(1), new Date(System.currentTimeMillis()), "A");
		
		// Save wikipage object in pn_object table
		Integer wikiPageId = pnObjectService.saveObject(wikipageObject);
		pnWikiPage.setWikiPageId(wikiPageId);
		
		// Create object of pn_object_space for wikipage
		PnObjectSpacePK pnObjectSpacePK = new PnObjectSpacePK(pnWikiPage.getWikiPageId(), pnWikiPage.getOwnerObjectId().getObjectId());
		PnObjectSpace pnObjectSpace = new PnObjectSpace(pnObjectSpacePK);
		
		// Save wikipage object id and space id in pn_object_space table
		pnObjectSpaceService.save(pnObjectSpace);

		PnObjectName pnObjectName = new PnObjectName(pnWikiPage.getWikiPageId(),pnWikiPage.getPageName());
		pnObjectNameService.save(pnObjectName);
		
		// Save wiki page
		return pnWikiPageDAO.create(pnWikiPage);
	}
	
	@Autowired
	private IPnObjectService objectService;
	
	public Integer saveAssignment(PnWikiPage pnWikiPage, Integer ownerSpaceId) {													/* CUSTOMIZED for assignment wiki */
		//System.out.println("PnWikiPageServiceImpl.saveAssignment(" + pnWikiPage.getPageName() + ", " + ownerSpaceId + ")");
		PnObject pnAssignmentObject = pnWikiPage.getOwnerObjectId();							/* get PnObject for pnWikiPage */
		
		/* get the owner object for this assignment */
		pnWikiPage.setOwnerObjectId(objectService.getObjectWithAssignedWikiPage(ownerSpaceId));	// set object to be owning PROJECT (for wiki page to be stored in that objects wiki space)
		pnWikiPage.setParentPageName( pnWikiPageDAO.getRootPageForObject(ownerSpaceId) );		// set root page of owning objects wiki as parentPageName value
		
		Set<PnObject> assignements = new HashSet<PnObject>();
		assignements.add(pnAssignmentObject);
		pnWikiPage.setAssignements(assignements);			

		// Save wiki page
		return pnWikiPageDAO.create(pnWikiPage);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiPageService#update(net.project.hibernate.model.PnWikiPage)
	 */
	public void update(PnWikiPage pnWikiPage) {
		
		// When deleting the page: check is it assigned to some object and delete appropriate record if it is.
		if( pnWikiPage.getRecordStatus().equals("D") ) {
			pnWikiAssignmentService.deleteWikiAssignmentsByPageId(pnWikiPage.getWikiPageId());
		}
		
		PnObjectName pnObjectName = new PnObjectName(pnWikiPage.getWikiPageId(), pnWikiPage.getPageName());
		pnObjectNameService.update(pnObjectName);
		
		pnWikiPageDAO.update(pnWikiPage);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiPageService#findAll()
	 */
	public List<PnWikiPage> findAll() {
		return pnWikiPageDAO.findAll();
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiPageService#get(java.lang.Integer)
	 */
	public PnWikiPage get(Integer key) {
		PnWikiPage wikiPage = pnWikiPageDAO.findByPimaryKey(key);
		
		if (wikiPage != null) {
			wikiPage.getEditedBy();
			wikiPage.getOwnerObjectId();
		}
		return wikiPage;
	}
	
	/* Altered delete method to change page record_status to D
	 * 
	 * (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiPageService#delete(net.project.hibernate.model.PnWikiPage)
	 */
	public void delete(PnWikiPage pnWikiPage) {
		pnWikiPageDAO.delete(pnWikiPage);
	}

	@Autowired
	private IPnObjectTypeService objectTypeService;
	
	@Autowired
	private IPnWikiAssignmentService pnWikiAssignmentService;
	
	public PnWikiPage getWikiPageWithName(String pageName, Integer ownerObjectId) {							/* CUSTOMIZED for assignment wiki */
		PnWikiPage result = null;
		PnObjectType objectType = objectTypeService.getObjectTypeByObjectId(ownerObjectId);
		
		if( objectType != null && ownerObjectId != null) {
			PnWikiAssignment wikiAssignment = null;
			if( objectType.getObjectType().equals("task") || objectType.getObjectType().equals("form_data") ) {		/* getting TASK/FORM specific wiki content */
				/* for this object types when calling this method pageName is not considered - so put it to be assignment object name */
				  
				wikiAssignment = pnWikiAssignmentService.getWikiAssignmentByObjectId(ownerObjectId);
			}
			if(wikiAssignment != null && wikiAssignment.getWikiPageId() != null){
				result = getWikiPage(wikiAssignment.getWikiPageId()); 
			} else {
				result = pnWikiPageDAO.getWikiPageWithName(pageName, ownerObjectId);
			}
		}
		return result;
	}
	
	public PnWikiPage getRecordWithWikiPageNameAndProjectSpaceIdWithStatusAorD(String pageName, Integer ownerObjectId) {
		return pnWikiPageDAO.getRecordWithWikiPageNameAndProjectSpaceIdWithStatusAorD(pageName, ownerObjectId);
	}
	
	@Autowired
	private IPnWikiAttachmentService wikiAttachmentService;
	
	/**
	 * Method for deleting wiki page (along with all of its subpages and its subpages...).
	 * <b>It also deletes all attachmnets related to those deleted pages</b>.
	 * @param pnWikiPage wiki page to delete
	 */
	public void deletePageWithSubpages(PnWikiPage pnWikiPage) {
		/*
		 * Error occured while deleting the wiki pages: a different object with the same identifier value was already associated with the session: 
		 * [net.project.hibernate.model.PnWikiPage#117]; nested exception is org.hibernate.NonUniqueObjectException: 
		 * a different object with the same identifier value was already associated with the session: [net.project.hibernate.model.PnWikiPage#117]
		 * */
		List<PnWikiPage> pnWikiSubPages = new ArrayList<PnWikiPage>();
		
		try{
			pnWikiSubPages = pnWikiPageDAO.getActualSubPages(pnWikiPage);
			Iterator result = pnWikiSubPages.iterator();
			while(result.hasNext()){
				PnWikiPage subPage = (PnWikiPage) result.next();
				deletePageWithSubpages(subPage);
			}
			//delete the page
			wikiAttachmentService.deleteAllAttachmentsFromWikiPage(pnWikiPage.getWikiPageId(), pnWikiPage.getOwnerObjectId().getObjectId());
			pnWikiPage.setRecordStatus("D");
			pnWikiPageDAO.update(pnWikiPage);
			
		} catch (Exception e) {
			System.out.println("\nError occured while deleting wiki pages: " + e.getMessage());
		}		
		return;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiPageService#getDeletedWikiPages(java.lang.Integer)
	 */
	public List<PnWikiPage> getWikiPagesByOwnerAndRecordStatus(Integer ownerObjectId, String status) {
		if( ownerObjectId != null ) {
			PnObject object = pnObjectService.getObject(ownerObjectId);					/* get PnObject with ownerObjectId */
			if ( object.getPnObjectType().getObjectType().equals("task") || 
				 object.getPnObjectType().getObjectType().equals("form_data") ) {		/* if object type is assignment(TASK/FORM) */
//				IPnAssignmentService assignmentServ = ServiceFactory.getInstance().getPnAssignmentService();
//				ownerObjectId = assignmentServ.getAssigmentByAssignmentId(ownerObjectId, Integer.valueOf(SessionManager.getUser().getID()) ).getComp_id().getSpaceId();				/* get owner object(project) id as argument */
				ownerObjectId = pnObjectService.getObjectWithAssignedWikiPage(ownerObjectId).getObjectId();
			}
		}
		return pnWikiPageDAO.getWikiPagesByOwnerAndRecordStatus(ownerObjectId, status);

		/* return pnWikiPageDAO.getWikiPagesByOwnerAndRecordStatus(ownerObjectId,status); OBSOLETE - FOR COMMON WIKI FEATURES FOR ALL OBJECT TYPES */
	}

	public boolean doesWikiPageWithGivenNameExist(String wikiPageName, Integer projectSpaceId) {
		return pnWikiPageDAO.doesWikiPageWithGivenNameExist(wikiPageName, projectSpaceId);
	}
	
	//added for retrieving list of images available for attaching to wiki pages of one wiki
	 public List<PnWikiPage> getAllImageDetailPagesForWiki(Integer ownerObjectId, String objectName, String recordStatus){	/* CUSTOMIZE */
		return pnWikiPageDAO.getAllImageDetailPagesForWiki(ownerObjectId, objectName, recordStatus);
	}
	
	public List<PnWikiPage> getAllImageDetailPagesForWiki(Integer ownerObjectId, String recordStatus){	/* CUSTOMIZE */
		return pnWikiPageDAO.getAllImageDetailPagesForWiki(ownerObjectId, recordStatus);
	}

	/**
	 * Method that returns list of all subpages (child pages) of wiki page passed as argument.
	 */
	public List<PnWikiPage> getSubPages(PnWikiPage pnWikiPage) {
		return pnWikiPageDAO.getSubPages(pnWikiPage);
	}
	
	public List<PnWikiPage> getRecentChangesForWiki(Integer spaceId, int rangeNumber, String namespace) {
		//get number of pages from specified wiki space declared by rangeNumber
		return pnWikiPageDAO.getWikiPagesByDate(spaceId, rangeNumber, namespace);
	}
	
	public void assignWikiPageToObject(PnWikiPage selectedWikiPage, Integer objectId) {
		pnWikiAssignmentService.save(new PnWikiAssignment(objectId, selectedWikiPage.getWikiPageId()));
	}	
	
	/*
	 * Customized method for retreiving root page for objects
	 * Retreives root page for any type of object.
	 */
	public PnWikiPage getRootPageForObject(Integer objectId) {
		PnObjectType objectType = objectTypeService.getObjectTypeByObjectId(objectId);
		PnWikiPage result = null;
		
		if( objectType != null && objectId != null) {
			if( objectType.getObjectType().equals("task") || objectType.getObjectType().equals("form_data") ) {		//getting TASK/FORM specific wiki page 
				result = objectService.getObjectWithAssignedWikiPage(objectId).getAssignedWikiPage();

			} else {	 																							//getting OTHER objects wiki content 
				result = pnWikiPageDAO.getRootPageForObject(objectId);			 										//get root page for regular wiki objects (object types that have regular wiki) 
			}
		}
		
		return result;
	}
    
    /* (non-Javadoc)
     * @see net.project.hibernate.service.IPnWikiPageService#getWikiPage(java.lang.Integer)
     */
    public PnWikiPage getWikiPage(Integer wikiPageId){
        return pnWikiPageDAO.getWikiPageByPageId(wikiPageId);
    }

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiPageService#getWikiPageCountForProject(java.lang.Integer, java.util.Date, java.util.Date)
	 */
	public Integer getWikiPageCountForProject(Integer projectId, Date startDate, Date endDate) {
		return pnWikiPageDAO.getWikiPageCountForProject(projectId, startDate, endDate);
	}
    
    
    /* (non-Javadoc)
     * @see net.project.hibernate.service.IPnWikiPageService#getRootPageOfSpace(java.lang.Integer)
     */
    public PnWikiPage getRootPageOfSpace(Integer spaceId) {
        return pnWikiPageDAO.getRootPageForObject(spaceId); 
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.service.IPnWikiPageService#getRootPageByPageName(java.lang.String)
     */
    public PnWikiPage getRootPageByPageName(String pageName) {
        return pnWikiPageDAO.getRootWikiPageByName(pageName);
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.service.IPnWikiPageService#getRootWikiPagesByName(java.lang.String)
     */
    public List<PnWikiPage> getRootWikiPagesByName(String pageName) {
    	return pnWikiPageDAO.getRootWikiPagesByName(pageName);
    }
    /* (non-Javadoc)
     * @see net.project.hibernate.service.IPnWikiPageService#getChildWikiPageByName(java.lang.String, java.lang.Integer)
     */
    public PnWikiPage getChildWikiPageByName(String pageName, Integer parentPageId){
        return pnWikiPageDAO.getChildWikiPageByName(pageName, parentPageId);
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.service.IPnWikiPageService#getWikiPageByObjectId(java.lang.Integer)
     */
    public PnWikiPage getWikiPageByObjectId(Integer objectId) {
        return pnWikiPageDAO.getWikiPageByObjectId(objectId);
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.service.IPnWikiPageService#getAllDocumentsPagesForWiki(java.lang.Integer, java.lang.String)
     */
    public List<PnWikiPage> getAllDocumentsPagesForWiki(Integer ownerObjectId, String recordStatus) {
    	return pnWikiPageDAO.getAllDocumentsPagesForWiki(ownerObjectId, recordStatus);
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.service.IPnWikiPageService#deleteWikiPageByName(java.lang.String, java.lang.Integer)
     */
    public void deleteWikiPageByName(String pageName, Integer ownerObjectId) {
    	pnWikiPageDAO.deleteWikiPageByName(pageName, ownerObjectId);
    }
 
 	public void renameWikiPage(PnWikiPage newWikiPage, PnWikiPage wikiPageToRename) {
    	Integer generatedId = save(newWikiPage);
    	log.info("\ngeneratedId: "+generatedId+", wikiPageToRename.ID: "+wikiPageToRename.getWikiPageId());

    	// setup linking page fields
		setUpLinkingPageProperties(wikiPageToRename, newWikiPage.getEditedBy(), newWikiPage.getPageName());
    	// update linking page
    	pnWikiPageDAO.update(wikiPageToRename);

    	// edit history table records
    	pnWikiHistoryDAO.updateWikiPageIds(generatedId, wikiPageToRename.getWikiPageId());
    	log.info("\nHistory update executed!");
		
		// TODO: modify assignment set for renamed page
 	}
 	
    private void setUpLinkingPageProperties(PnWikiPage linkingPage, PnPerson editedBy, String targetPageName) {
    	// change old wiki page content (redirect tag)
    	String newContent = "'''This is linking page!''' \n * The page [[" + 
    						(linkingPage.getParentPageName() == null ? 
    								linkingPage.getPageName() :
    								(linkingPage.getParentPageName().getPageName() + "/" + linkingPage.getPageName()))  + 
    						"]] is renamed and moved to the following location [[" +
    						(linkingPage.getParentPageName() == null ? 
    								targetPageName :
    								(linkingPage.getParentPageName().getPageName() + "/" + targetPageName)) +
    						"]].";
    	
    	// set linking page content
    	linkingPage.setContent(newContent);
    	linkingPage.setEditDate(new Date());
    	linkingPage.setEditedBy(editedBy);		// TODO: set to null or add TYPE column in PN_WIKI_PAGE table indicating page type
    	linkingPage.setCommentText("Redirect page created. Redirection target: " + targetPageName);
    }

    /* (non-Javadoc)
     * @see net.project.hibernate.service.IPnWikiPageService#getWikiPageWithPageNameAndRecordStatusWithParent(java.lang.Integer)
     */
    public PnWikiPage getWikiPageWithPageNameAndRecordStatusWithParent(Integer wikiPageId){
    	return pnWikiPageDAO.getWikiPageWithPageNameAndRecordStatusWithParent(wikiPageId);
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.service.IPnWikiPageService#getWikiPageWithPageNameAndRecordStatusWithoutParent(java.lang.Integer)
     */
    public PnWikiPage getWikiPageWithPageNameAndRecordStatusWithoutParent(Integer wikiPageId){
    	return pnWikiPageDAO.getWikiPageWithPageNameAndRecordStatusWithoutParent(wikiPageId);
    }
   
    /* (non-Javadoc)
     * @see net.project.hibernate.service.IPnWikiPageService#getWikiPageWithPageNameAndContent(java.lang.Integer)
     */
    public PnWikiPage getWikiPageWithPageNameAndContent(Integer wikiPageId){
    	return pnWikiPageDAO.getWikiPageWithPageNameAndContent(wikiPageId);
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.service.IPnWikiPageService#deleteWikiPageById(java.lang.Integer)
     */
    public void deleteWikiPageById(Integer wikiPageId) {
    	pnWikiPageDAO.deleteWikiPageById(wikiPageId);
    }

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiPageService#getWikiPage(java.lang.String, java.lang.Integer)
	 */
	public PnWikiPage getWikiPage(String wikiPageName, Integer ownerObjectId) {
		return pnWikiPageDAO.getWikiPage(wikiPageName, ownerObjectId);
	}
    
}
