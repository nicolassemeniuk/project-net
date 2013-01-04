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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.http.HttpSession;

import net.project.base.EventException;
import net.project.base.EventFactory;
import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.property.PropertyProvider;
import net.project.document.Document;
import net.project.document.DocumentManagerBean;
import net.project.events.EventType;
import net.project.hibernate.constants.WikiConstants;
import net.project.hibernate.model.PnObjectType;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnWikiAttachment;
import net.project.hibernate.model.PnWikiPage;
import net.project.hibernate.service.IPnObjectService;
import net.project.hibernate.service.IPnObjectTypeService;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.IPnWikiAttachmentService;
import net.project.hibernate.service.IPnWikiPageService;
import net.project.hibernate.service.IWikiProvider;
import net.project.notification.EventCodes;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.FileUtils;
import net.project.util.StringUtils;
import net.project.wiki.ExtWikiModel;
import net.project.wiki.WikiManager;
import net.project.wiki.WikiURLManager;

import org.apache.log4j.Logger;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
@Service(value="wikiProvider")
public class WikiProviderImpl implements IWikiProvider{
	
	private static Logger log = Logger.getLogger(WikiProviderImpl.class);

//	private IPnWikiPageService pnWikiPageService;
//	
//	private IPnWikiHistoryService pnWikiHistoryService;
//	
//	public void setPnWikiPageService(IPnWikiPageService pnWikiPageService) {
//		this.pnWikiPageService = pnWikiPageService;
//	}
//
//	public void setPnWikiHistoryService(IPnWikiHistoryService pnWikiHistoryService) {
//		this.pnWikiHistoryService = pnWikiHistoryService;
//	}


	/**
	 * Method that finds CamelCase words and converts them in wiki links.
	 * 
	 * @param wikiText
	 * @return wiki markup text with CamelCase like words converted to inner wiki links
	 */
	public String camelCaseRecognition(String wikiText) {
		String camelCaseRegEx = "([A-Z][a-z]+[A-Z][a-z]+([A-Z][a-z]+)?)*[,.:!]?";
		StringBuffer result = new StringBuffer("");

		String[] poList = wikiText.split("\r\n|\r|\n");
		for (int i = 0; i < poList.length; i++) {
			StringTokenizer stLine = new StringTokenizer(poList[i]);
			while (stLine.hasMoreTokens()) {
				String token = stLine.nextToken();
				if (token.matches(camelCaseRegEx)) {
					if (token.endsWith(",") || token.endsWith(".") || token.endsWith(":") || token.endsWith("!")) {
						String newToken = null;
						newToken = token.substring(0, token.length() - 1);
						result.append("[[");
						result.append(newToken);
						result.append("]]");
						result.append(token.charAt(token.length() - 1));
						result.append(" ");
					} else {
						result.append("[[");
						result.append(token);
						result.append("]] ");
					}
				} else {
					result.append(token);
					result.append(" ");
				}
			}
			result.append("\n");
		}
		return result.toString();
	}

	
	/**
	 * Method that actually uploads image to some pnet's object wiki. <br>
	 * 
	 * @param ownerObjectId object whose wiki we are using (e.g. some projectSpace...)
	 */
	public String uploadImageToWiki( Integer objectId, UploadedFile file, String description, String ownerObjectName, User user, HttpSession session ) {
			String imageId = null;
			try {
				DocumentManagerBean docManager = (DocumentManagerBean) session.getAttribute("docManager");
				docManager.setUser(user);
				String tempFilePath = FileUtils.commitUploadedFileToFileSystem(file);
				
				imageId = docManager.addFileToSpace(file.getSize(), file.getFileName(), tempFilePath, file.getContentType(), user.getCurrentSpace().getID(), true);
				
				//now actually preserve image to wiki for specified object
				createImageDetailsPage(objectId, ownerObjectName, file, imageId, description, user);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return imageId;
	}
	
	@Autowired
	private IPnPersonService personService;
	
	@Autowired
	private IPnWikiPageService wikiPageService;
	
	/**
	* Method for creation of <b>image details</b> wiki page - displaying attached image.
	* @param parentPageName the name of wiki page on which we are attaching image.
	* @param imageName the name of uploaded file
	* @param description
	*/
	protected void createImageDetailsPage(Integer objectId, String parentPageName, UploadedFile file, String imageId, String description, User user) {
		String imageName = file.getFileName().replaceAll(" ", "_");
		StringBuffer pageContent = new StringBuffer("");
		String imagePageName = null;
		if(WikiManager.isFileTypeOfImage(file.getFileName())){
			imagePageName = "Image:" + imageName;	
		} else {
			imagePageName = "File:" + imageName;
		}
		
		
		PnPerson person = personService.getPerson(Integer.valueOf(user.getID()));
		
		pageContent.append("== ").append(parentPageName).append(": ").append(imageName).append(" == \n<br>");
		pageContent.append("[[").append(imagePageName).append("]] <br><br>\n");
		pageContent.append(" '''File:''' ").append(imageName).append("<br>\n");
		pageContent.append(" '''Added By:''' ").append(user.getDisplayName()).append("<br>\n");
		pageContent.append(" '''Size:''' ").append(file.getSize()/1024).append(" KB<br>\n");
		pageContent.append(" '''Added On:''' ").append(SessionManager.getUser().getDateFormatter().formatDate(new Date(), "EEE, MMM dd, yyyy. hh:mm:ss")).append(" <br>\n");
		pageContent.append(" '''Description:''' ");

		if (StringUtils.isEmpty(description)) {
			pageContent.append("Not commented");
		} else {
			pageContent.append(description);
		}
		pageContent.append(". <br>\n");
		
		PnWikiPage wikiPage = new PnWikiPage();
		// prepare the object
		wikiPage.setEditedBy(person);
		wikiPage.setEditDate(new Date());		
		wikiPage.setRecordStatus("A");
		wikiPage.setContent(pageContent.toString());
		if (StringUtils.isNotEmpty(description)) {		// populating description field
			wikiPage.setCommentText(description);
		} else {
			wikiPage.setCommentText("Not commented.");
		}
		
		PnWikiPage imageParentPage = null;
		PnWikiPage parentWikiPage = wikiPageService.getWikiPage(objectId);
		if(parentWikiPage != null && parentWikiPage.getParentPageName() != null){
			imageParentPage	= parentWikiPage.getParentPageName();	
		} else {
			imageParentPage = parentWikiPage;
		}
		// if 'image-details' page with given name already exist in specified project space (doesn't matter which status it has)
		if(wikiPageService.doesWikiPageWithGivenNameExist(imagePageName, parentWikiPage.getOwnerObjectId().getObjectId())) {
			// it exists get it and update it
			wikiPage = wikiPageService.getRecordWithWikiPageNameAndProjectSpaceIdWithStatusAorD(imagePageName, parentWikiPage.getOwnerObjectId().getObjectId());
			
			wikiPage.setEditedBy(person);
			wikiPage.setEditDate(new Date());		
			wikiPage.setRecordStatus("A");
			wikiPage.setContent(pageContent.toString());
			
			if (StringUtils.isNotEmpty(description)) {
				wikiPage.setCommentText(description);
			} else {
				wikiPage.setCommentText("Not commented.");
			}
			
			if( imageParentPage != null ) {
				wikiPage.setParentPageName(imageParentPage);
			} else {
				log.error("WikiProviderImpl.createImageDetailsPage(Integer objectId, String parentPageName, UploadedFile file, String imageId, String description, User user) exception: " 
						+ "Could not find parent page for image details page!");	// parent page not found exception - image-details page has parent page - root wiki page of ownerObjectId	
			}
			
			wikiPageService.update(wikiPage);
			
		} else {
			//the page with given name in specified space doesn't exist - save it.
			wikiPage.setCreatedBy(person);
			wikiPage.setCreatedDate(new Date());
			wikiPage.setAccessLevel(WikiConstants.PROJECT_LEVEL_ACCESS);
			wikiPage.setPageName(imagePageName);
			if( imageParentPage != null ) {
				wikiPage.setParentPageName(imageParentPage);
			} else {
				log.error("WikiProviderImpl.createImageDetailsPage(Integer objectId, String parentPageName, UploadedFile file, String imageId, String description, User user) exception: " 
							+ "Could not find parent page for image details page!");	// parent page not found exception - image-details page has parent page - root wiki page of ownerObjectId 
			}
			wikiPage.setOwnerObjectId(imageParentPage.getOwnerObjectId());
			wikiPageService.save(wikiPage);
		}
		
		WikiManager wikiManager = new WikiManager(wikiPage);
		
		if(wikiPage.getParentPageName() != null){
			net.project.wiki.WikiEvent event = new net.project.wiki.WikiEvent();
			event.setSpaceID(SessionManager.getUser().getCurrentSpace().getID());
			event.setTargetObjectID(wikiPage.getParentPageName().getWikiPageId().toString());
			event.setTargetObjectType(EventCodes.WIKI_UPLOAD_IMAGE);
			event.setTargetObjectXML(wikiManager.getXmlBody());
			event.setEventType(EventCodes.WIKI_UPLOAD_IMAGE);
			event.setUser(SessionManager.getUser());
			event.setDescription("Wiki image upload : \"" + wikiPage.getPageName() + "\"");
			try {
				event.store();
			} catch (PersistenceException pnetEx) {
				Logger.getLogger(WikiProviderImpl.class).error("WikiProviderImpl.createImageDetailsPage() :: Wiki Image uploaded Notification Event store Failed!", pnetEx);
			}
		}
		//publishing event to asynchronous queue
		try{	
			//Event for wiki image upload
        	net.project.events.WikiEvent wikiEvent = (net.project.events.WikiEvent) EventFactory.getEvent(ObjectType.WIKI, EventType.IMAGE_UPLOADED);
        	wikiEvent.setObjectType(ObjectType.WIKI);
        	wikiEvent.setObjectID(wikiPage.getWikiPageId().toString());
        	wikiEvent.setObjectRecordStatus("A");
        	if(wikiPage.getParentPageName() != null)
        		wikiEvent.setParentObjectId(wikiPage.getParentPageName().getWikiPageId().toString());
        	wikiEvent.publish();
		} catch (EventException e) {
			Logger.getLogger(WikiProviderImpl.class).error("WikiProviderImpl.createImageDetailsPage() :: Wiki Image uploaded Event Publishing Failed!", e);
		}
				
		// TODO: for saving the attachment to wiki page instead of root page
		// saveOrUpdateWikiAttachment(parentWikiPage, file, imageId, description, person, false);
		
		// now enter record in Wiki_Attachment to attach uploaded image with 'image details' page		
		saveOrUpdateWikiAttachment(wikiPage.getParentPageName(), file, imageId, description, person, false);
	}
	

	@Autowired
	private IPnWikiAttachmentService wikiAttachmentService;
	
	/**
	 * Helper method for entering record in WIKI_ATTACHMENT table.
	 * @param pnWikiPage
	 * @param wikiPageName
	 * @param file
	 * @param imageId
	 * @param description
	 * @param person
	 * @param isRootPage
	 */
	private void saveOrUpdateWikiAttachment(PnWikiPage pnWikiPage, UploadedFile file, String imageId, String description, PnPerson person, boolean isRootPage){
				
		// fill in the wiki-attachment object properties
		PnWikiAttachment pnWikiAttachment = new PnWikiAttachment();
		
		// if wiki_page-attachment record already exists in table - update it (cause it has record_status eather "D" or "A")
		// but on upload page show warning that you are going to update existing file with same name with new one
		if ( wikiAttachmentService.doesAttachmentWithNameExistOnWikiPage(pnWikiPage.getWikiPageId(), file.getFileName().replaceAll(" ", "_"), 'N' )) {
			pnWikiAttachment = wikiAttachmentService.getRecordWithWikiPageIdAndFileNameWithStatusAorD(pnWikiPage.getWikiPageId(), file.getFileName().replaceAll(" ", "_"));
			pnWikiAttachment.setFileId(Integer.valueOf(imageId));
			pnWikiAttachment.setAttachedBy(person);
			pnWikiAttachment.setAttachedOnDate(new Date());
			pnWikiAttachment.setDescription(description);
			pnWikiAttachment.setRecordStatus("A");
			wikiAttachmentService.update(pnWikiAttachment);
		} else { // record doesn't exist - save it!
			pnWikiAttachment.setAttachmentName(file.getFileName().replaceAll(" ", "_"));
			pnWikiAttachment.setFileId(Integer.valueOf(imageId));
			pnWikiAttachment.setAttachedBy(person);
			pnWikiAttachment.setAttachedOnDate(new Date());
			pnWikiAttachment.setDescription(description);
			pnWikiAttachment.setWikiPageId(pnWikiPage);
			pnWikiAttachment.setRecordStatus("A");
			wikiAttachmentService.save(pnWikiAttachment);
		}
	}
		
	//image attaching
	public List getAllAttachedFilesToWiki(Integer ownerObjId, String wikiObjName, String statusRecord){
		List<PnWikiAttachment> allFilesAttached = wikiAttachmentService.getAllImagesFromWiki(ownerObjId);
		List<Map> result = new ArrayList<Map>();
		
		//make new list to return
		Iterator it = allFilesAttached.iterator();
		while ( it.hasNext() ) {
			PnWikiAttachment imageDetails = (PnWikiAttachment) it.next();
			
			String name = imageDetails.getAttachmentName();
			String lastmod = imageDetails.getAttachedOnDate().toString();
			String url = SessionManager.getJSPRootURL() + 
							"/servlet/ViewWikiImage/imageName/" + imageDetails.getAttachmentName() + 
							"/wikiPageName/" + wikiObjName +
							"?module=" + Module.DIRECTORY;

			Map map = new HashMap();
			map.put("name", name);
			map.put("lastmod", lastmod);
			map.put("url", url);
			result.add(map);
		}
		return result; 
	}
	
	// Page Index for Personal Assignment page
	public String wikiPagesIndex(Integer spaceId, Integer objectId, boolean selectableIndex) {
		String resultString = null;
		
		TreeMap <String, List<PnWikiPage>> tMap = new TreeMap<String, List<PnWikiPage>>();
		//get list of all wiki pages in current object space
		List<PnWikiPage> pages = wikiPageService.getWikiPagesByOwnerAndRecordStatus(spaceId, "A");
		
		//filling in the map with wiki pages - indexed
		Iterator it = pages.iterator();
		while ( it.hasNext() ) {
			PnWikiPage currPage = (PnWikiPage) it.next();
			String pageNameFirstLetter = currPage.getPageName().substring(0,1).toUpperCase();
            //if initial letter of current page is contained in map add
			if ( tMap.containsKey(pageNameFirstLetter) ) {
            	List<PnWikiPage> list = tMap.get(pageNameFirstLetter);
            	list.add(currPage);
            	tMap.put(pageNameFirstLetter, list);
            } else {
            	//there is no key for this letter in the map
            	List<PnWikiPage> list = new ArrayList<PnWikiPage>();
            	list.add(currPage);
            	tMap.put(pageNameFirstLetter, list);
            }
		}
		
        //read the map for presenting index
		resultString = renderPageIndex(tMap, objectId, spaceId, selectableIndex);
		return resultString;
	}
	
	private String renderPageIndex(TreeMap pgIndMap, Integer objectId, Integer spaceId, boolean selectableIndex) {
		String result = null;
		
		//read the map for presenting index
		if( pgIndMap != null && pgIndMap.size() != 0 ) {
			StringBuffer indexStr = new StringBuffer("<div id=\"pageIndex\">");
			indexStr.append("<h3 id=\"indexTitle\">Page Index</h3>");
			if( !selectableIndex ) {
				indexStr.append("<h3><span style=\"position:absolute;right:70px;\">[<a href=\"javascript:hidePgIndex();\" style=\"padding: 2px;\">Exit</a>]</span></h3><br/><br/>");
				indexStr.append("<div style=\"background: #EEEEEE none repeat scroll 0%; border: 1px solid #CCCCCC; float: left; margin-right: 15px; padding: 4px; text-align: left; width: 60%; \"> This is an alphabetical list of pages you can read on this wiki space. </div><br/><br/>");
				indexStr.append("<strong><b>");
				indexStr.append("<a href=\"#\">All:&nbsp;</a> ");
				
				//Use an Iterator to traverse the mappings in the TreeMap.  
		        Iterator it1 = pgIndMap.entrySet().iterator();
		        while ( it1.hasNext() ) {
		            Map.Entry entry = (Map.Entry) it1.next();
		            //indexStr.append("<a href=\"#" + entry.getKey() + "\">" + entry.getKey() + "</a> ");
		            indexStr.append("<a href=\"#").append(entry.getKey()).append("\">").append(entry.getKey()).append("</a> ");
		        }
				indexStr.append("</b></strong><br/><br/>");
			}
				
			Iterator it2 = pgIndMap.entrySet().iterator();
			//loop through entire map
	        while( it2.hasNext() ) {
	            Map.Entry entry = (Map.Entry) it2.next();
	        	indexStr.append("<div id=\"").append(entry.getKey()).append("\">");
				indexStr.append("<strong><b>").append(entry.getKey()).append("</b></strong><br/>");
				List<PnWikiPage> pagesForLetter = (List<PnWikiPage>) entry.getValue();
				
				Iterator it3 = pagesForLetter.iterator();
				//loop through all pages with same first letter
				indexStr.append("<div id=\"pagesWithFirstLetter\" style=\"padding-left:15px; padding-top:5px;\">");
				while( it3.hasNext() ) {
					PnWikiPage currPg = (PnWikiPage) it3.next();
					User user = SessionManager.getUser();
					String hrefLink = null;
					try {
						if( selectableIndex ) {
							hrefLink = "javascript:showPageContent(" + currPg.getOwnerObjectId().getObjectId() + ", '" 
										+ currPg.getPageName()
										+ "', " + objectId + ", " + currPg.getWikiPageId() + ")";		// 14.Jan.
						} else {
							hrefLink = SessionManager.getJSPRootURL() + "/wiki/EditWikiPage/" + currPg.getPageName()
							+ "/" + currPg.getParentPageName() + "/"+ URLEncoder.encode(objectId.toString(), SessionManager.getCharacterEncoding())
							+ "/" + URLEncoder.encode(user.getID(), SessionManager.getCharacterEncoding()) + "?module=" 
							+ (selectableIndex ? Module.PERSONAL_SPACE : Module.PROJECT_SPACE);
						}
						indexStr.append("<a href=\"").append(hrefLink).append("\">").append(currPg.getPageName()).append("</a> ");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					
					if( !selectableIndex ) {
						indexStr.append(". . . . . . <b>Last Editor:</b> ").append(currPg.getEditedBy().getDisplayName()).append(", <b>edited on:</b> ").append(SessionManager.getUser().getDateFormatter().formatDate(currPg.getEditDate(), "EEE, MMM dd, yyyy. hh:mm:ss")).append("<br/>");
					} else {
						indexStr.append("<br/>");
					}
				}
				indexStr.append("</div>");
				indexStr.append("</div><br/>");
			}
			
			indexStr.append("</div>");
			result = indexStr.toString();
		} else {
			result = "<div id=\"pageIndex\"><h3>Page Index</h3><br/><b>This wiki doesn't contain any pages!</b><br/></div>";
		}
		
		return result;
	}

	/**
	 * Added for better performance.
	 */
	@Autowired
	private IPnObjectTypeService objectTypeService;
	
	@Autowired
	private IPnObjectService objectService;
	
	public String convertToHtmlNew(String wikiText, String parentPage, String objectId, boolean isPreview) {	 // CUSTOMIZE for assignment objects
		net.project.security.User user = SessionManager.getUser();
		StringBuffer htmlFormat = new StringBuffer();
		ExtWikiModel wikiModel = null;
		
		/** If object is assignmnet (task/form):
		 *  then in each link of it's page set objectId to owning objectId (e.g. projectId).
		 *  This is due to the fact that assignment objects wiki page belongs to projects wiki space, so clicking on the link brigns
		 *  the user to the owning projects wiki.
		 */
		PnObjectType objectType = objectTypeService.getObjectTypeByObjectId(Integer.valueOf(objectId));
		if( objectType.getObjectType().equals("task") || objectType.getObjectType().equals("form_data") ) {
			// get owning object id
			objectId = objectService.getObjectWithAssignedWikiPage(Integer.valueOf(objectId)).getObjectId().toString();
		}
		
		StringBuffer imageLink = new StringBuffer();
		StringBuffer hrefLink = new StringBuffer();
		
		if( !isPreview ) {
			wikiModel = new ExtWikiModel(
					//image uri
					imageLink.append( SessionManager.getJSPRootURL() )
							 .append("/servlet/ViewWikiImage?imageName=${image}")
							 .append("&wikiPageName=").append(parentPage)
							 .append("&ownerObjectId=").append(objectId)
							 .append("&module=").append(Module.DIRECTORY).toString(),
					//link uri
					hrefLink.append(SessionManager.getJSPRootURL())
							.append("/wiki/")
							.append(parentPage)
							.append("/").append("${title}").toString(), objectId, false);
		} else {
			wikiModel = new ExtWikiModel(
					//image uri
					imageLink.append( SessionManager.getJSPRootURL() )
					 .append("/servlet/ViewWikiImage?imageName=${image}")
					 .append("&wikiPageName=").append(parentPage)
					 .append("&ownerObjectId=").append(objectId)
					 .append("&module=").append(Module.DIRECTORY).toString(),
					//link uri
					"#", objectId, true);
		}
			
		//parse wikiText to provide CamelCase support
		wikiText = camelCaseRecognition(wikiText);		
		
		htmlFormat.append(wikiModel.render(wikiText) + "\n");
		//added for categories rendering
		htmlFormat.append(wikiModel.parseCategories("") + "\n");
//		if ( !isPreview ) {			//don't show attachment list in preview mode
//			htmlFormat.append(wikiModel.parseAttachedImages(parentPage, objectId) + "\n");
//		}
		
		return htmlFormat.toString();
	}
	
	/*
	 * Method for converting wiki text to html.
	 * With isRootPage marker for telling ExtWikiModel/ViewWikiServlet to consider is root or non root page. 
	 */
	public String convertToHtmlNew(String wikiText, String parentPage, String objectId, boolean isPreview, boolean isRootPage) {	/* CUSTOMIZE for assignment objects*/
		StringBuffer htmlFormat = new StringBuffer();
		ExtWikiModel wikiModel = null;
		
		/*
		 *  If object is assignmnet (task/form):
		 *  then in each link of it's page set objectId to owning objectId (e.g. projectId).
		 *  This is due to the fact that assignment objects wiki page belongs to projects wiki space, so clicking on the link brigns
		 *  the user to the owning projects wiki.
		 */
		PnObjectType objectType = objectTypeService.getObjectTypeByObjectId(Integer.valueOf(objectId));
		if( objectType.getObjectType().equals("task") || objectType.getObjectType().equals("form_data") ) {
			/* get owning object id */
//			objectId = ServiceFactory.getInstance().getPnAssignmentService()
//										.getAssigmentByAssignmentId(Integer.valueOf(objectId), Integer.valueOf(SessionManager.getUser().getID()) ).getComp_id().getSpaceId().toString();
			objectId = objectService.getObjectWithAssignedWikiPage(Integer.valueOf(objectId)).getObjectId().toString();
		}
		
		StringBuffer imageLink = new StringBuffer();
		StringBuffer hrefLink = new StringBuffer();
		if( !isPreview ) {
			wikiModel = new ExtWikiModel(
					//image uri
					imageLink.append( SessionManager.getJSPRootURL() )
					 .append("/servlet/ViewWikiImage?imageName=${image}")
					 .append("&wikiPageName=").append(parentPage)
					 .append("&ownerObjectId=").append(objectId)
					 .append("&isRootPage=").append(isRootPage)
					 .append("&module=").append(Module.DIRECTORY).toString(),
					//link uri
					hrefLink.append(SessionManager.getJSPRootURL())
					.append("/wiki/")
					.append("${title}")
					.append("/").append(parentPage).toString() , objectId, false, isRootPage);
		} else {
			wikiModel = new ExtWikiModel(
					//image uri
					imageLink.append( SessionManager.getJSPRootURL() )
					 .append("/servlet/ViewWikiImage?imageName=${image}")
					 .append("&wikiPageName=").append(parentPage)
					 .append("&ownerObjectId=").append(objectId)
					 .append("&isRootPage=").append(isRootPage)
					 .append("&module=").append(Module.DIRECTORY).toString(),
					//link uri
					"#", objectId, true, isRootPage);
		}
			
		//parse wikiText to provide CamelCase support
		wikiText = camelCaseRecognition(wikiText);		
		
		htmlFormat.append(wikiModel.render(wikiText) + "\n");
		//added for categories rendering
		htmlFormat.append(wikiModel.parseCategories("") + "\n");
//		if ( !isPreview && !parentPage.startsWith("Image:") ) {						// don't show attachment list in preview mode or on 'Image Details' page
//			htmlFormat.append(wikiModel.parseAttachedImages(parentPage, objectId) + "\n");
//		}
		
		return htmlFormat.toString();
	}
	    
    /** 
     * Method for converting wiki text to html.
     * With isRootPage marker for telling ExtWikiModel/ViewWikiServlet to consider is root or non root page.
     * to use WikiPage content instead of sending it as a parameter
     */
    public String convertToHtmlNew(PnWikiPage wikiPage, boolean isPreview) {
        return convertToHtmlNew(wikiPage, isPreview, null);
    }
    
	/*
	 * Method for converting wiki text to html.
	 * With isRootPage marker for telling ExtWikiModel/ViewWikiServlet to consider is root or non root page. 
	 */
	public String convertToHtmlNew(PnWikiPage wikiPage, boolean isPreview, String pageContent) {
		StringBuffer htmlFormat = new StringBuffer();
		ExtWikiModel wikiModel = null;
		
		// TODO: rename objectId to parentWikiPageId
		Integer objectId = wikiPage.getParentPageName() != null 
							?  wikiPage.getParentPageName().getWikiPageId() 
								: (wikiPage.getWikiPageId() == null ? wikiPage.getOwnerObjectId().getObjectId() : wikiPage.getWikiPageId());
		
		// TODO: set object id to wiki page id to show image on page properly
		// when image is not uploaded to only root page 
		//Integer objectId = wikiPage.getWikiPageId();
		
		String parentPage = null;
		if(wikiPage.getParentPageName() != null){
			parentPage = wikiPage.getParentPageName().getPageName();
		} else {
			parentPage = wikiPage.getPageName();
		}
		
		StringBuffer imageLink = new StringBuffer();
		StringBuffer hrefLink = new StringBuffer();
		if( !isPreview ) {
			wikiModel = new ExtWikiModel(
					// image uri
					imageLink.append( SessionManager.getJSPRootURL() )
					 .append("/servlet/ViewWikiImage?imageName=${image}")
					 .append("&wikiPageName=").append(parentPage)
					 .append("&ownerObjectId=").append(objectId)
					 .append("&isRootPage=").append(wikiPage.getParentPageName() == null)
					 .append("&module=").append(Module.DIRECTORY).toString(),
					// link uri
					hrefLink.append(SessionManager.getJSPRootURL())
					.append("/wiki/")
					.append(parentPage)
					.append("/").append("${title}").toString(), objectId.toString(), false, (wikiPage.getParentPageName() == null));
		} else {
			wikiModel = new ExtWikiModel(
					// image uri
					imageLink.append( SessionManager.getJSPRootURL() )
					 .append("/servlet/ViewWikiImage?imageName=${image}")
					 .append("&wikiPageName=").append(parentPage)
					 .append("&ownerObjectId=").append(objectId)
					 .append("&isRootPage=").append(wikiPage.getParentPageName() == null)
					 .append("&module=").append(Module.DIRECTORY).toString(),
					// link uri
					 hrefLink.append(SessionManager.getJSPRootURL())
						.append("/wiki/")
						.append(parentPage)
						.append("/").append("${title}").toString(), objectId.toString(), true, (wikiPage.getParentPageName() == null));
		}
		
		String wikiText = pageContent != null ? pageContent : wikiPage.getContent();
		// parse wikiText to provide CamelCase support
		wikiText = camelCaseRecognition(wikiText);

		htmlFormat.append(alignContentsBlock(wikiModel.render(wikiText)) + "\n");
		// added for categories rendering
		htmlFormat.append(wikiModel.parseCategories("") + "\n");
		// don't show attachment list in preview mode or on 'Image Details' page
		if(PropertyProvider.getBoolean("prm.wiki.showattachmentsonrootpage.isenabled")){
			if ( !isPreview && !wikiPage.getPageName().startsWith("Image:") ) {
				htmlFormat.append(wikiModel.parseAttachedImages(wikiPage) + "\n");
			}
		}

		// Adding 'Referenced Images' block 
		// unimplemented image reference functionality
		//htmlFormat.append("<br><br>\n\n" + wikiModel.referencedImagesBlock());
		
		return htmlFormat.toString();
	}
	
    /**
     * align the content block of wiki page based on token value
     */
	public String alignContentsBlock(String htmlContent) {       
		if(PropertyProvider.getBoolean("prm.wiki.rightalignedtopcontentblock.isenabled")) {
			htmlContent = htmlContent.replace("<table id=\"toc\" class=\"toc\" summary=\"Contents\">", "<table id=\"toc\" class=\"toc\" summary=\"Contents\" align=\"right\">");
		} else {
			htmlContent = htmlContent.replace("<table id=\"toc\" class=\"toc\" summary=\"Contents\">", "<table id=\"toc\" class=\"toc\" summary=\"Contents\" align=\"center\">");       
		}
        return htmlContent;
    }
	
	/**
	 * Method to retrieve list of pages that contain link to <b>targetedPage</b>.
	 */
	public List<PnWikiPage> whatLinksHere(PnWikiPage targetedPage) {
		ExtWikiModel wikiModel = null;
		Integer objectId = targetedPage.getOwnerObjectId().getObjectId();	// id of object to which wiki this page belongs to
		List<PnWikiPage> wikiSpacePages = new ArrayList<PnWikiPage>();		// holds all active pages from this wiki space
		List<PnWikiPage> foundPages = new ArrayList<PnWikiPage>();			// holds all wiki pages which have links to currentPage
		
		Integer rootPageId = targetedPage.getParentPageName() != null ?  targetedPage.getParentPageName().getWikiPageId() : targetedPage.getWikiPageId();
		//System.out.println("rootPageId: "+rootPageId);
		// for assignment specific wiki - start 
		PnObjectType objectType = objectTypeService.getObjectTypeByObjectId(targetedPage.getOwnerObjectId().getObjectId());
		if( objectType.getObjectType().equals("task") || objectType.getObjectType().equals("form_data") ) {
			// get owning object id 
			objectId = objectService.getObjectWithAssignedWikiPage(Integer.valueOf(objectId)).getObjectId();
		}
		
		wikiSpacePages = wikiPageService.getWikiPagesByOwnerAndRecordStatus(objectId, "A"); 
		Iterator wikiSpacePagesIt = wikiSpacePages.iterator();
		while( wikiSpacePagesIt.hasNext() ) { // loop through all pages from wiki space
			PnWikiPage wikiPage = (PnWikiPage) wikiSpacePagesIt.next();			// grab one page
			String camelCaseConvertedToLinksContent = camelCaseRecognition(wikiPage.getContent());
			
//			wikiModel = getAppropriateWikiModel(wikiPage.getPageName(), objectId);
			wikiModel = getAppropriateWikiModel(wikiPage.getPageName(), rootPageId);
			wikiModel.render(camelCaseConvertedToLinksContent);

			Set<String> links = wikiModel.getLinks();							// links on current page
			List<String> referencedImages = wikiModel.getImages();				// images on current page
			links.addAll(referencedImages);
			Iterator linksIt = links.iterator();
			
			while ( linksIt.hasNext() ) {
				String currPage = (String) linksIt.next();
				currPage = currPage.replaceAll(" ", "_");
				if( targetedPage.getPageName().equals(currPage) 
						&& !targetedPage.getPageName().equals(wikiPage.getPageName()) ) {
					foundPages.add(wikiPage);									// This wiki page links to currentPage
				}
			}
		}
		return foundPages;
	}
	
	/**
	 * Method to retrieve list of existing wiki pages that this page links to (all pages that this page has links to).
	 */
	public List<PnWikiPage> linksOnThisPage(PnWikiPage currentPage) {
		ExtWikiModel wikiModel = null;
		Integer objectId = currentPage.getOwnerObjectId().getObjectId();	// id of object to which wiki this page belongs to
		List<PnWikiPage> foundPages = new ArrayList<PnWikiPage>();			// holds all wiki pages that current page links to
		String camelCaseConvertedToLinksContent = camelCaseRecognition(currentPage.getContent());
		
		Integer rootPageId = currentPage.getParentPageName() != null ?  currentPage.getParentPageName().getWikiPageId() : currentPage.getWikiPageId();
		//System.out.println("rootPageId: "+rootPageId);
		// for assignment specific wiki - start
		PnObjectType objectType = objectTypeService.getObjectTypeByObjectId(currentPage.getOwnerObjectId().getObjectId());
		if( objectType.getObjectType().equals("task") || objectType.getObjectType().equals("form_data") ) {
			// get owning object id 
			objectId = objectService.getObjectWithAssignedWikiPage(Integer.valueOf(objectId)).getObjectId();
		}
		
//		wikiModel = getAppropriateWikiModel(currentPage.getPageName(), objectId);
		wikiModel = getAppropriateWikiModel(currentPage.getPageName(), rootPageId);
		wikiModel.render(camelCaseConvertedToLinksContent);
		
		Iterator linkedPagesIt = wikiModel.getLinks().iterator();		// links on current page
		while( linkedPagesIt.hasNext() ) {								// loop through all page names that currentPage has links to
			String currPageName = (String) linkedPagesIt.next();
			PnWikiPage wikiPage = wikiPageService.getWikiPageWithName(WikiURLManager.converToWikiPageName(currPageName), objectId);
			if( wikiPage != null ) {
				foundPages.add(wikiPage);									// currentPage has inner wiki link to wikiPage
			}
		}
		return foundPages;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IWikiProvider#wikiPagesIndex(java.lang.Integer, java.lang.Boolean)
	 */
	public Map wikiPagesIndex(Integer objectId, Boolean showDocs) {
		
		TreeMap <String, List<PnWikiPage>> tMap = new TreeMap<String, List<PnWikiPage>>();
		// get list of all wiki pages in current object space
		List<PnWikiPage> pages = null;
		
		if(showDocs){
			pages = wikiPageService.getAllDocumentsPagesForWiki(objectId, "A");	
		} else {
			pages = wikiPageService.getWikiPagesByOwnerAndRecordStatus(objectId, "A");
		}
		
		// filling in the map with wiki pages - indexed
		Iterator it = pages.iterator();
		while ( it.hasNext() ) {
			PnWikiPage currPage = (PnWikiPage) it.next();
			String pageName = currPage.getPageName();
			if(showDocs){
				pageName = pageName.replaceFirst("Image:", "");
			}
			String pageNameFirstLetter = pageName.substring(0,1).toUpperCase();
            //if initial letter of current page is contained in map add
			if ( tMap.containsKey(pageNameFirstLetter) ) {
            	List<PnWikiPage> list = tMap.get(pageNameFirstLetter);
            	list.add(currPage);
            	tMap.put(pageNameFirstLetter, list);
            } else {
            	//there is no key for this letter in the map
            	List<PnWikiPage> list = new ArrayList<PnWikiPage>();
            	list.add(currPage);
            	tMap.put(pageNameFirstLetter, list);
            }
		}
		return tMap;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IWikiProvider#recentChanges(java.lang.Integer, int, java.lang.String)
	 */
	@SuppressWarnings("deprecation")
	public Map recentChanges(Integer objectId, int range, String namespace) {
		
		Map <String, List<PnWikiPage>> tMap = new TreeMap<String, List<PnWikiPage>>();
		//get list of all wiki pages in current object space
		List<PnWikiPage> pages = wikiPageService.getRecentChangesForWiki(objectId, range, namespace);
		
		//filling in the map with wiki pages - indexed by date
		Iterator it = pages.iterator();
		while ( it.hasNext() ) {
			PnWikiPage currPage = (PnWikiPage) it.next();
            String date =  SessionManager.getUser().getDateFormatter().formatDate(currPage.getEditDate(), "MMM dd, yyyy");
            
			//if initial letter of current page is contained in map add
			if ( tMap.containsKey(date) ) {
            	List<PnWikiPage> list = tMap.get(date);
            	list.add(currPage);
            	tMap.put(date, list);
            } else {
            	//there is no key for this letter in the map
            	List<PnWikiPage> list = new ArrayList<PnWikiPage>();
            	list.add(currPage);
            	tMap.put(date, list);
            }
		}
		
		return tMap;
	}
	
	// helper method to return appropriate wiki model for methods that need it
	private ExtWikiModel getAppropriateWikiModel(String currentPageName, Integer ownerObjectId) {
		ExtWikiModel wikiModel = null;

		StringBuffer imageLink = new StringBuffer();
		StringBuffer hrefLink = new StringBuffer();
		wikiModel = new ExtWikiModel(
				//image uri
				imageLink.append( SessionManager.getJSPRootURL() )
				 .append("/servlet/ViewWikiImage?imageName=${image}")
				 .append("&wikiPageName=").append(currentPageName)
				 .append("&ownerObjectId=").append(ownerObjectId)
				 .append("&module=").append(Module.DIRECTORY).toString(),
				//link uri
				hrefLink.append(SessionManager.getJSPRootURL()+ "/wiki/" +currentPageName+ "/${title}").toString(), 
				ownerObjectId.toString(),
				false);
		return wikiModel;
	}


	public void setPersonService(IPnPersonService personService) {
		this.personService = personService;
	}


	public void setWikiPageService(IPnWikiPageService wikiPageService) {
		this.wikiPageService = wikiPageService;
	}


	public void setWikiAttachmentService(IPnWikiAttachmentService wikiAttachmentService) {
		this.wikiAttachmentService = wikiAttachmentService;
	}


	public void setObjectTypeService(IPnObjectTypeService objectTypeService) {
		this.objectTypeService = objectTypeService;
	}


	public void setObjectService(IPnObjectService objectService) {
		this.objectService = objectService;
	}
	
	
	
//	/* (non-Javadoc)
//     * @see net.project.hibernate.service.IPnWikiPageService#renameWikiPage(String newName, PnWikiPage wikiPageToRename)
//     */
//    public void renameWikiPage(PnWikiPage newWikiPage, PnWikiPage wikiPageToRename) {
//
//    	Integer generatedId = pnWikiPageService.save(newWikiPage);
//    	log.info("\ngeneratedId: "+generatedId+", wikiPageToRename.ID: "+wikiPageToRename.getWikiPageId());
//
//    	// setup linking page fields
//		setUpLinkingPageProperties(wikiPageToRename, newWikiPage.getEditedBy(), newWikiPage.getPageName());
//    	// update linking page
//    	pnWikiPageService.update(wikiPageToRename);
//
//    	// edit history table records
//    	pnWikiHistoryService.updateWikiPageIds(generatedId, wikiPageToRename.getWikiPageId());
//    	log.info("\nHistory update executed!");
//		
//		// TODO: modify assignment set for renamed page
//
//	}
//    
//    private void setUpLinkingPageProperties(PnWikiPage linkingPage, PnPerson editedBy, String targetPageName) {
//    	// change old wiki page content (redirect tag)
//    	String newContent = "'''This is linking page!''' \n * The page [[" + 
//    						(linkingPage.getParentPageName() == null ? 
//    								linkingPage.getPageName() :
//    								(linkingPage.getParentPageName().getPageName() + "/" + linkingPage.getPageName()))  + 
//    						"]] is renamed and moved to the following location [[" +
//    						(linkingPage.getParentPageName() == null ? 
//    								targetPageName :
//    								(linkingPage.getParentPageName().getPageName() + "/" + targetPageName)) +
//    						"]].";
//    	
//    	// set linking page content
//    	linkingPage.setContent(newContent);
//    	linkingPage.setEditDate(new Date());
//    	linkingPage.setEditedBy(editedBy);		// TODO: set to null or add TYPE column in PN_WIKI_PAGE table indicating page type
//    	linkingPage.setCommentText("Redirect page created. Redirection target: " + targetPageName);
//    }
}
