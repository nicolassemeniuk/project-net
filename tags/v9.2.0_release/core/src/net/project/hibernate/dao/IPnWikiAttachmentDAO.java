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
package net.project.hibernate.dao;

import java.util.List;

import net.project.hibernate.model.PnWikiAttachment;

/**
 * Interface for accessing wiki page database object.
 */
public interface IPnWikiAttachmentDAO extends IDAO<PnWikiAttachment, Integer> {
	
	public PnWikiAttachment getFileIdWithWikiPageAndFileName(Integer wikiPageId, String fileName);
	
	public PnWikiAttachment getRecordWithWikiPageIdAndFileNameWithStatusAorD(Integer wikiPageId, String fileName);
	
	public List<PnWikiAttachment> getAllAttachmentsFromWikiPage(Integer wikiPageId);
	
	public boolean doesAttachmentWithNameExistOnWikiPage(Integer wikiPageId, String attachmentName, char withStatus);

	/**
	 * Method for retrieving attachment information for <b>file</b> with specified wiki file-details page, and record status 
	 */
	public PnWikiAttachment getFileWithDetailsPage(Integer fileDetailsWikiPageId, String recordStatus);
	
	/**
	 * Method for deleting the attachment from wiki
	 * @param wikiPageId wiki page identifier
	 * @param attachmentName name of the attachment
	 */
	public void deleteAttachmentFromWiki(Integer wikiPageId, String attachmentName);
}