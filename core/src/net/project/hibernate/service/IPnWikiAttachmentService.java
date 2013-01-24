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

import java.util.List;

import net.project.hibernate.model.PnWikiAttachment;


public interface IPnWikiAttachmentService {

	/**
	 * Persist the given transient instance, first assigning a generated identifier. (Or using the current value
	 * of the identifier property if the assigned generator is used.) 
	 * @param pnWikiPage a transient instance of a persistent class 
	 * @return the class identifier
	 */
	public java.lang.Integer save(net.project.hibernate.model.PnWikiAttachment pnWikiAttachment);	

	/**
	 * Update the persistent state associated with the given identifier. An exception is thrown if there is a persistent
	 * instance with the same identifier in the current session.
	 * @param pnWeblog a transient instance containing updated state
	 */
	public void update(net.project.hibernate.model.PnWikiAttachment pnWikiAttachment);
	
	public net.project.hibernate.model.PnWikiAttachment get(java.lang.Integer key);
	
	public java.util.List<net.project.hibernate.model.PnWikiAttachment> findAll ();
	
	/**
	 * Remove a persistent instance from the datastore. The argument may be an instance associated with the receiving
	 * Session or a transient instance with an identifier associated with existing persistent state. 
	 * @param pnWeblog the instance to be removed
	 */
	public void delete(net.project.hibernate.model.PnWikiAttachment pnWikiAttachment);
	
	/**
	 * Get PnWikiAttachment object (attachment record) with specified file name and wiki page on which it is located.
	 * And record_status with value "A".
	 * @param wikiPageId
	 * @param fileName
	 * @return
	 */
	public PnWikiAttachment getFileIdWithWikiPageAndFileName(Integer wikiPageId, String fileName);
	
	/**
	 * Get PnWikiAttachment file with specified file name and wiki page on which it is located.
	 * With status eather "A" or "D".
	 * @param wikiPageId
	 * @param fileName
	 * @return
	 */
	public PnWikiAttachment getRecordWithWikiPageIdAndFileNameWithStatusAorD(Integer wikiPageId, String fileName);
	
	public List<PnWikiAttachment> getAllAttachmentsFromWikiPage(Integer wikiPageId);
	
	/**
	 * Checks for existence of file with specified file name on wiki page with given ID.
	 * 
	 * @param pnWikiPage
	 * @return true if it exists, false otherwise.
	 */
	public boolean doesAttachmentWithNameExistOnWikiPage(Integer wikiPageId, String attachmentName, char withStatus);
	
	/**
	 * Mehtod that deletes all attachments on specified wiki page. 
	 */
	public void deleteAllAttachmentsFromWikiPage(Integer wikiPageId, Integer projectSpaceId);

	/**
	 * Mehtod that deletes specified wiki attachment(image) from current wiki space.<br/>
	 * This method is called to delete image uploaded to one wiki space, and also 
	 * delete all attahment records that connect this image with one or more wiki pages from that wiki space.
	 *  
	 * @param wikiPageName the name of page to delete - 'image details' page
	 */
	public void deleteAttachmentFromWikiSpace(String wikiPageName, Integer spaceId);
	
	/**
	 * Method for retrieving all images(records from pn_wiki_attachment table with association <b>file-detail-wiki-page <-> image-name</b>) uploaded to one space, with status "A".
	 * @param ownerObjectId id of the object which wiki we are using
	 */
	public List<PnWikiAttachment> getAllImagesFromWiki(Integer ownerObjectId);
	
	/**
	 * Method for deleting the attachment from wiki
	 * @param wikiPageId wiki page identifier
	 * @param attachmentName name of the attachment
	 */
	public void deleteAttachmentFromWiki(Integer wikiPageId, String attachmentName);
}