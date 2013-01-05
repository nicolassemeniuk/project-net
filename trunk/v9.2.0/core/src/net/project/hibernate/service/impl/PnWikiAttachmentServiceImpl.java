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
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.project.hibernate.dao.IPnWikiAttachmentDAO;
import net.project.hibernate.dao.IPnWikiPageDAO;
import net.project.hibernate.model.PnWikiAttachment;
import net.project.hibernate.model.PnWikiPage;
import net.project.hibernate.service.IPnWikiAttachmentService;
import net.project.hibernate.service.IPnWikiPageService;
import net.project.security.SessionManager;

/**
 * @author 
 *
 */
@Service(value="pnWikiAttachmentService")
public class PnWikiAttachmentServiceImpl implements IPnWikiAttachmentService {

	/**
	 * PnWikiAttachment data access object
	 */
	@Autowired
	private IPnWikiAttachmentDAO pnWikiAttachmentDAO;
	
	/**
	 * PnWikiAttachment data access object
	 */
	@Autowired
	private IPnWikiPageDAO pnWikiPageDAO;

	/**
	 * @param pnWikiAttachmentDAO the pnWikiAttachmentDAO to set
	 */
	public void setPnWikiAttachmentDAO(IPnWikiAttachmentDAO pnWikiAttachmentDAO) {
		this.pnWikiAttachmentDAO = pnWikiAttachmentDAO;
	}
	
	public void setPnWikiPageDAO(IPnWikiPageDAO pnWikiPageDAO) {
		this.pnWikiPageDAO = pnWikiPageDAO;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiAttachmentService#save(net.project.hibernate.model.PnWikiAttachment)
	 */
	public Integer save(PnWikiAttachment pnWikiAttachment) {
		return pnWikiAttachmentDAO.create(pnWikiAttachment);
	}	

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiPageService#update(net.project.hibernate.model.PnWikiPage)
	 */
	public void update(PnWikiAttachment pnWikiAttachment) {
		pnWikiAttachmentDAO.update(pnWikiAttachment);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiAttachmentService#findAll()
	 */
	public List<PnWikiAttachment> findAll() {
		return pnWikiAttachmentDAO.findAll();
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiAttachmentService#get(java.lang.Integer)
	 */
	public PnWikiAttachment get(Integer key) {
		return pnWikiAttachmentDAO.findByPimaryKey(key);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiAttachmentService#delete(net.project.hibernate.model.PnWikiHistory)
	 */
	public void delete(PnWikiAttachment pnWikiAttachment) {
		pnWikiAttachmentDAO.delete(pnWikiAttachment);
	}
	
	public PnWikiAttachment getFileIdWithWikiPageAndFileName(Integer wikiPageId, String fileName) {
		return pnWikiAttachmentDAO.getFileIdWithWikiPageAndFileName(wikiPageId, fileName);
	}

	public List<PnWikiAttachment> getAllAttachmentsFromWikiPage(Integer wikiPageId) {
		return pnWikiAttachmentDAO.getAllAttachmentsFromWikiPage(wikiPageId);
	}
	
	public boolean doesAttachmentWithNameExistOnWikiPage(Integer wikiPageId, String attachmentName, char withStatus){
		return pnWikiAttachmentDAO.doesAttachmentWithNameExistOnWikiPage(wikiPageId, attachmentName, withStatus);
	}
	
	public PnWikiAttachment getRecordWithWikiPageIdAndFileNameWithStatusAorD(Integer wikiPageId, String fileName) {
		return pnWikiAttachmentDAO.getRecordWithWikiPageIdAndFileNameWithStatusAorD(wikiPageId, fileName);
	}
	
	@Autowired
	private IPnWikiPageService wikiPageService;
	
	public void setWikiPageService(IPnWikiPageService wikiPageService) {
		this.wikiPageService = wikiPageService;
	}

	/**
	 * Mehtod that deletes all attachments on specified wiki page. Leaving wiki page without any image attached to it.
	 */
	public void deleteAllAttachmentsFromWikiPage(Integer wikiPageId, Integer projectSpaceId) {
		List<PnWikiAttachment> attachmentList = pnWikiAttachmentDAO.getAllAttachmentsFromWikiPage(wikiPageId);
		
		Iterator resultIt = attachmentList.iterator();
		while( resultIt.hasNext() ){
			PnWikiAttachment currRecord = (PnWikiAttachment) resultIt.next();
			System.out.println("   *** Deleting attachment record (wikiPage<->ImageName): " + currRecord.getWikiPageId().getPageName() + " " + currRecord.getAttachmentName());
			String imagePageName = "Image:" +  currRecord.getAttachmentName();
			//get the 'image-details' wiki page for current image
			PnWikiPage pnImgDetailsWikiPage = wikiPageService.getWikiPageWithName(imagePageName, projectSpaceId );
			
			//1. delete 'image-details-wiki-page'-'image-name' record from pn_from wiki_attachment table	OK
			PnWikiAttachment imgDetailRec = pnWikiAttachmentDAO.getFileIdWithWikiPageAndFileName(pnImgDetailsWikiPage.getWikiPageId(), currRecord.getAttachmentName());
			imgDetailRec.setRecordStatus("D");
			pnWikiAttachmentDAO.update(imgDetailRec);
			
			//2. delete 'image-details-wiki-page' record from pn_wiki_page table							OK
			pnImgDetailsWikiPage.setRecordStatus("D");
			wikiPageService.update(pnImgDetailsWikiPage);
			
			//3. delete 'wiki-page'-'image-name' record from pn_from wiki_attachment table					OK
			//all wiki pages are deleted, but associations between pages and pictures (on that pages) are not
			currRecord.setRecordStatus("D");
			pnWikiAttachmentDAO.update(currRecord);
		}
	}
	
	/**
	 * Mehtod that deletes specified wiki attachment(image) from current wiki space.<br/>
	 * This method is called to delete image uploaded to one wiki space, and also 
	 * delete all attahment records that connect this image with one or more wiki pages from that wiki space.
	 *  
	 * @param wikiPageName the name of page to delete - 'image details' page
	 * @param spaceId the id of space from which we want image to be deleted
	 */
	public void deleteAttachmentFromWikiSpace(String wikiPageName, Integer spaceId) {
		//extract image name
		String imageName = wikiPageName.substring(wikiPageName.indexOf(":") + 1, wikiPageName.length());
		
		//get wiki page (image-details page) with specified name
		PnWikiPage imgDetailsWikiPage = wikiPageService.getWikiPageWithName(wikiPageName, Integer.valueOf(SessionManager.getUser().getCurrentSpace().getID()) );
		//get root page for wiki space we are in
		
		// 1. get all pages from current wiki space
		List<PnWikiPage> allPagesList = wikiPageService.getWikiPagesByOwnerAndRecordStatus(spaceId, "A");
		
		// 2. iterate through all and check is image attached to wiki page, and if it is set that record as deleted
		Iterator allPagesIt = allPagesList.iterator();
		//for all wiki pages in wiki space - check if image is attached to them 
		while( allPagesIt.hasNext() ) {
			PnWikiPage child = (PnWikiPage) allPagesIt.next();
			PnWikiAttachment attachRec = pnWikiAttachmentDAO.getFileIdWithWikiPageAndFileName(child.getWikiPageId(), imageName);
			if( attachRec != null ){
				//found attachment for some wiki page, child of this wiki space - delete it
				attachRec.setRecordStatus("D");
				pnWikiAttachmentDAO.update(attachRec);
			}
		}

		
		//	3. delete the ImageDetails wiki page 
		if ( imgDetailsWikiPage != null ) {
			imgDetailsWikiPage.setRecordStatus("D");
			wikiPageService.update(imgDetailsWikiPage);
		}

	}
	
	public List<PnWikiAttachment> getAllImagesFromWiki(Integer ownerObjectId) {
		List <PnWikiAttachment> wikiAttachedFiles = new ArrayList<PnWikiAttachment>();
		PnWikiPage rootPage = pnWikiPageDAO.getRootPageForObject(ownerObjectId); 
		if( rootPage != null )
			wikiAttachedFiles = pnWikiAttachmentDAO.getAllAttachmentsFromWikiPage( rootPage.getWikiPageId() );
		
		return wikiAttachedFiles;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnWikiAttachmentService#deleteAttachmentFromWiki(java.lang.Integer, java.lang.String)
	 */
	public void deleteAttachmentFromWiki(Integer wikiPageId, String attachmentName) {
		pnWikiAttachmentDAO.deleteAttachmentFromWiki(wikiPageId, attachmentName);
	}

}
