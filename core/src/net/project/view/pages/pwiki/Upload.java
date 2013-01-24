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
package net.project.view.pages.pwiki;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import net.project.base.property.PropertyProvider;
import net.project.hibernate.model.PnWikiPage;
import net.project.hibernate.service.IPnWikiAttachmentService;
import net.project.hibernate.service.IPnWikiPageService;
import net.project.hibernate.service.IWikiProvider;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.util.StringUtils;
import net.project.util.Version;
import net.project.view.pages.Wiki;
import net.project.wiki.WikiManager;
import net.project.wiki.WikiURLManager;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.apache.tapestry5.util.TextStreamResponse;

public class Upload {

	private static Logger log;

	private IWikiProvider wikiProvider;
	
	private String date;

	private String objectName;
 
	private String parentPageName;
	
	private String pageName;

	private String rootPage;

	private Integer spaceId;

	private Integer userId;

	private UploadedFile uploadedFile;
	
	private String description;
	
	private String attachmentId;
	
	private String parentPageLink;
	
	private String wikiForObject;
	
	private Integer wikiPageId;
	
	@Inject
	private RequestGlobals requestGlobals;

	@Inject
	private Request request;
	
	@Inject
	private Response response;
	
	private String jspRootURL;
	
	private String addImageLinkLabel;
	
	private String cancelLinkLabel;
	
	private String selectFileLabel;
	
	private String fileDescriptionLabel;
	
	private IPnWikiAttachmentService wikiAttachmentService;
	
	private IPnWikiPageService wikiPageService;
	
	private String imageName;

	private String versionNumber; 
	
	@Property
	private String invalidFileNameMessage;
	
	/**
	 * To intitialize the page properties
	 */
	private void initialize() {
		try {
			log = Logger.getLogger(Upload.class);
			wikiProvider = ServiceFactory.getInstance().getWikiProvider();
			wikiAttachmentService = ServiceFactory.getInstance().getPnWikiAttachmentService();
			wikiPageService = ServiceFactory.getInstance().getPnWikiPageService();
			jspRootURL = SessionManager.getJSPRootURL();
			addImageLinkLabel = PropertyProvider.get("prm.project.wiki.upload.addimage.link");
			cancelLinkLabel = PropertyProvider.get("prm.project.wiki.upload.cancel.link");
			selectFileLabel = PropertyProvider.get("prm.project.wiki.upload.selectfile.label");
			fileDescriptionLabel = PropertyProvider.get("prm.project.wiki.upload.filedescription.label");
			versionNumber = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
			invalidFileNameMessage = PropertyProvider.get("prm.wiki.upload.invalidfile.message", "~!@#$%^&*()',`/|;+=[]<>{}?\"&");
			if(StringUtils.isNotEmpty(request.getParameter("spaceId"))
					&& StringUtils.isNotEmpty(request.getParameter("userId")) 
					&& StringUtils.isNotEmpty(request.getParameter("wikiPageId"))){
				setSpaceId(Integer.parseInt(request.getParameter("spaceId")));
				setUserId(Integer.parseInt(request.getParameter("userId")));
				setWikiPageId(Integer.parseInt(request.getParameter("wikiPageId")));
				setWikiForObject(request.getParameter("wikiPageName"));
				setParentPageName(request.getParameter("parentPageName"));
			} else {
				setSpaceId(Integer.parseInt(SessionManager.getUser().getCurrentSpace().getID()));
				setUserId(Integer.parseInt(SessionManager.getUser().getID()));
				setWikiPageId(WikiManager.getRootPageForSpace(getSpaceId()).getWikiPageId());
				setWikiForObject(WikiURLManager.getRootWikiPageNameForSpace());
				setParentPageName(getWikiForObject());
			}
		} catch (Exception e) {
			log.error("Error occured while getting property tokens : " + e.getMessage());
		}
	}

	/**
	 * Method call on page activation
	 */
	void onActivate() {
		if (net.project.security.SessionManager.getUser() == null) {
			throw new IllegalStateException("User is null");
		}
		initialize();
	}

	/**
	 * Method to upload a document to wiki page and return to the image page if it is image else top page
	 */
	public void onSuccess(){
		if (net.project.security.SessionManager.getUser() == null) {
			throw new IllegalStateException("User is null");
		}
		try {
			attachmentId = wikiProvider.uploadImageToWiki(getWikiPageId(), getUploadedFile(), getDescription(),
					URLDecoder.decode(getWikiForObject(), SessionManager.getCharacterEncoding()) .replaceAll(" ", "_"), SessionManager.getUser(), requestGlobals
							.getHTTPServletRequest().getSession());

			response.sendRedirect(getJspRootURL() + "/wiki/" 
						+ getParentPageName()
						+ "/" + (isFileTypeSupported(URLEncoder.encode(getUploadedFile().getFileName(), SessionManager.getCharacterEncoding())) 
								? "Image:"+ URLEncoder.encode(getUploadedFile().getFileName().replaceAll(" ", "_"), SessionManager.getCharacterEncoding()) : getWikiForObject()));
			
		} catch (Exception e) {
			log.error("Error Occured while uploading image for wiki" + e.getMessage());
			try {
				response.sendRedirect(getJspRootURL() + "/wiki/" +getParentPageName());
			} catch (IOException pnetEx) {
			}
		}
	}
	
	/**
     * This method check non alphanumeric characters.
     * @param imageName
     * @return true or false
     */
    public boolean invalidFileName(String imageName){
        for(char ch: imageName.toCharArray()){
            if ("~!@#$%^&*()',`/|;+=[]<>{}?\"&".contains(ch+"")){
            	return true;
            }
        }
        return false;
    }
	
	/**
	 * Called on page activation with paremeter
	 * @param action
	 * @return TextStreamResponse object
	 */
	Object onActivate(String action) {
		initialize();
		String imageName = request.getParameter("imageName");
		String wikiPgName = request.getParameter("wikiPgName");
		
		if(action != null){
			if(action.equalsIgnoreCase("checkFileExistence")){
				String returnText = null;
				if(!isFileTypeSupported(imageName)){
					returnText = "FileTypeNotSupported";
				} else if(invalidFileName(imageName)){
					returnText = "InvalidFileName";
				} else {
					returnText = String.valueOf(checkForExistenceOfFileWithSameName(imageName, wikiPgName));
				}
				return new TextStreamResponse("text/plain", returnText);
			}
		}
		return null;
	}
	
	/**
	 * Method to check the file is already exist with the same name or not
	 * 
	 * @param imageName name of the file
	 * @param wikiPageName name of the wiki page
	 * @return true or false
	 */
	public boolean checkForExistenceOfFileWithSameName(String imageName, String wikiPageName) {
		boolean result = false;
		imageName = imageName.substring(imageName.lastIndexOf("\\") + 1);
		
		PnWikiPage pnWikiPage = wikiPageService.getWikiPageWithName(getWikiForObject(), getSpaceId() );
		
		Integer wikiPageId = (pnWikiPage != null && pnWikiPage.getParentPageName() != null)
								? pnWikiPage.getParentPageName().getWikiPageId() 
								: pnWikiPage.getWikiPageId();
	
		result = ( pnWikiPage != null && 
				 ( wikiAttachmentService.getFileIdWithWikiPageAndFileName(wikiPageId, imageName.replaceAll(" ", "_")) != null ));
		
		return result;
	}
	
	/**
	 * To check file type is supported or not 
	 * 
	 * @param file
	 * @return true or false
	 */
	public boolean isFileTypeSupported(String file) {
		boolean result = false;
		if( file.lastIndexOf('.') == -1 ) {
			return result;
		}
		String ext = file.substring(file.lastIndexOf('.') + 1, file.length());
		
		return ( ext.equalsIgnoreCase("gif") || ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpg")
				|| ext.equalsIgnoreCase("jpeg")	|| ext.equalsIgnoreCase("bmp"));
	}
	
	public String getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getParentPageName() {
		return parentPageName;
	}

	public void setParentPageName(String parentPageName) {
		this.parentPageName = parentPageName;
	}

	public Integer getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(Integer objectId) {
		this.spaceId = objectId;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getRootPage() {
		return rootPage;
	}

	public void setRootPage(String rootPage) {
		this.rootPage = rootPage;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getParentPageLink() {
		return parentPageLink;
	}

	public void setParentPageLink(String parentPageLink) {
		this.parentPageLink = parentPageLink;
	}

	public String getWikiForObject() {
		return wikiForObject;
	}

	public void setWikiForObject(String wikiForObject) {
		this.wikiForObject = wikiForObject;
	}

	/**
	 * @return the jspRootURL
	 */
	public String getJspRootURL() {
		return jspRootURL;
	}

	/**
	 * @param jspRootURL
	 *            the jspRootURL to set
	 */
	public void setJspRootURL(String jspRootURL) {
		this.jspRootURL = jspRootURL;
	}

	/**
	 * @return Returns the addImageLinkLabel.
	 */
	public String getAddImageLinkLabel() {
		return addImageLinkLabel;
	}

	/**
	 * @param addImageLinkLabel The addImageLinkLabel to set.
	 */
	public void setAddImageLinkLabel(String addImageLinkLabel) {
		this.addImageLinkLabel = addImageLinkLabel;
	}

	/**
	 * @return Returns the fileDescriptionLabel.
	 */
	public String getFileDescriptionLabel() {
		return fileDescriptionLabel;
	}

	/**
	 * @param fileDescriptionLabel The fileDescriptionLabel to set.
	 */
	public void setFileDescriptionLabel(String fileDescriptionLabel) {
		this.fileDescriptionLabel = fileDescriptionLabel;
	}

	/**
	 * @return Returns the selectFileLabel.
	 */
	public String getSelectFileLabel() {
		return selectFileLabel;
	}

	/**
	 * @param selectFileLabel The selectFileLabel to set.
	 */
	public void setSelectFileLabel(String selectFileLabel) {
		this.selectFileLabel = selectFileLabel;
	}

	/**
	 * @return Returns the cancelLinkLabel.
	 */
	public String getCancelLinkLabel() {
		return cancelLinkLabel;
	}

	/**
	 * @param cancelLinkLabel The cancelLinkLabel to set.
	 */
	public void setCancelLinkLabel(String cancelLinkLabel) {
		this.cancelLinkLabel = cancelLinkLabel;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	/**
	 * @return the uploadFor
	 */
	public String getVersionNumber() {
		return versionNumber;
	}

	/**
	 * @return the addImageButtonCaption
	 */
	public String getAddImageButtonCaption() {
		return PropertyProvider.get("prm.wiki.upload.addimage.button");
	}

	/**
	 * @return the cancelButtonCaption
	 */
	public String getCancelButtonCaption() {
		return PropertyProvider.get("prm.wiki.upload.cancel.button");
	}

	/**
	 * @return the wikiPageId
	 */
	public Integer getWikiPageId() {
		return wikiPageId;
	}

	/**
	 * @param wikiPageId the wikiPageId to set
	 */
	public void setWikiPageId(Integer wikiPageId) {
		this.wikiPageId = wikiPageId;
	}

}
