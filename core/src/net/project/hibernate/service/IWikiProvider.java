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

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.tapestry5.upload.services.UploadedFile;

import net.project.hibernate.model.PnWikiPage;
import net.project.security.User;

/**
 * 
 *
 */
public interface IWikiProvider {
	
	public String camelCaseRecognition(String wikiText);
	
	public String uploadImageToWiki( Integer objectId, UploadedFile file, String description, String ownerObjectName, User user, HttpSession session );
	
	/**
	 * Method for retreiving all images uploaded to one space.
	 * @param ownerObjectId id of the object which wiki we are using
	 * @param wikiObjectName the name of the object which wiki we are using
	 * @param statusRecord the record status of images we are looking for
	 */
	public List getAllAttachedFilesToWiki(Integer ownerObjId, String wikiObjName, String statusRecord);
	
	/**
	 * Method for converting wiki markup text to html with objectID passed in order to
	 * work ok for personal space also.
	 * @param wikiText
	 * @param parentPage
	 * @param objectId
	 * @param isPreview
	 * @return
	 */
	public String convertToHtmlNew(String wikiText, String parentPage, String objectId, boolean isPreview);
	
	public String convertToHtmlNew(String wikiText, String parentPage, String objectId, boolean isPreview, boolean isRootPage);
	
	public String convertToHtmlNew(PnWikiPage wikiPage, boolean isPreview);
    
    public String convertToHtmlNew(PnWikiPage wikiPage, boolean isPreview, String pageContent);
	
	//Assignment Page Index
	public String wikiPagesIndex(Integer spaceId, Integer objectId, boolean selectableIndex);
	
	public List<PnWikiPage> whatLinksHere(PnWikiPage currentPage);
	public List<PnWikiPage> linksOnThisPage(PnWikiPage currentPage);
	
	// chages for Wiki Page Index t5 page
	public Map wikiPagesIndex(Integer objectId, Boolean showDocs);
	public Map recentChanges(Integer objectId, int range, String namespace);

//   /**
//    * Method to rename wiki page name. MediaWiki pattern for renaming is used.
//    * @param newName - new wiki page
//    * @param wikiPageToRename - wiki page to rename
//    */
//	public void renameWikiPage(PnWikiPage newPage, PnWikiPage wikiPageToRename);
}
