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
package net.project.view.pages.personal;

import javax.servlet.http.HttpServletRequest;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.service.IPnPersonProfileService;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.util.StringUtils;
import net.project.util.Version;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.apache.tapestry5.util.TextStreamResponse;

public class UploadDocument {

	private static Logger log = Logger.getLogger(UploadDocument.class);

	private UploadedFile file;

	private IPnPersonProfileService personProfileService;

	@Inject
	private RequestGlobals requestGlobals;

	private String selectImageToUploadLabel;
	
	private String JSPRootURL;
	
	private String versionNumber; 

	@InjectPage
	private Profile profile;

	@Persist
	private String uploadFor;
	
	private String uploadbutton;
	
	private String cancelbutton;

	@Persist
	private String weblogEntryId;
	
	@Persist
	private boolean isValidImage;
	
	private String errorAlertTitle;
	
	private String imageTypeNotSupportedMessage;
	
	private String selectImageToUploadMessage;
	
	private String selectedInvalidImageMessage;
	
	@Property
	@Persist
	private String documentId;
	
	@Property
	private String uploadButtonCaption;
	
	@Property
	private String cancelButtonCaption;
	
	void initializeValues() {
		try {
			JSPRootURL = SessionManager.getJSPRootURL();
			versionNumber = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());	
			errorAlertTitle = PropertyProvider.get("prm.resource.global.exterroralert.title");
			imageTypeNotSupportedMessage = PropertyProvider.get("prm.personal.personalimageupload.imagetypenotsupported.message");
			selectImageToUploadMessage = PropertyProvider.get("prm.personal.personalimageupload.selectimagetoupload.message");
			selectedInvalidImageMessage = PropertyProvider.get("prm.personal.imageinvalid.message");
			selectImageToUploadLabel = PropertyProvider.get("prm.personal.personalimageupload.selectimagetoupload.label");
			uploadButtonCaption = PropertyProvider.get("prm.personal.personalimageupload.upload.caption");
			cancelButtonCaption = PropertyProvider.get("prm.personal.personalimageupload.cancel.caption");
		}catch (Exception ex) {
			log.error("Error occured while getting property values in upload document page : "+ex.getMessage());			
		}
	}
	
	@CleanupRender
	void clearValues() {
		isValidImage = true;
	}

	/**
	 * To perform image upload on uploading the image from popup on profile page
	 * 
	 * @return current page
	 */
	public Object onAction() {
		initializeValues();
		documentId = null;
		personProfileService = ServiceFactory.getInstance()
				.getPnPersonProfileService();
		//Uploading Document
		documentId = personProfileService.uploadDocument(
				this.getFile(), SessionManager.getUser(),
				requestGlobals.getHTTPServletRequest().getSession(),
				Module.DIRECTORY);
		
		if (getUploadFor().equals("profilePage")) {
			try {
				profile.getUser().load();
				if(profile.getUser().getID().equals(SessionManager.getUser().getID())){
					SessionManager.getUser().load();
				}
			} catch (PersistenceException pnetEx) {
				log.error("Error occured while loading the user object : "
						+ pnetEx.getMessage());
			}
			// Setting image path of profile page if uploads successfully else
			// NoPicture path
			if (StringUtils.isEmpty(documentId)) {
				profile.setImagePath(SessionManager.getJSPRootURL()
						+ "/servlet/ViewDocument?id=" + documentId + "&module="
						+ Module.PERSONAL_SPACE);
				profile.setImageAvailable(true);
			}
		} else if (getUploadFor().equals("setupProfilePage") || getUploadFor().equals("memberview")) {
			try {
				SessionManager.getUser().load();
			} catch (PersistenceException pnetEx) {
				log.error("Error occured while loading the user object : "
						+ pnetEx.getMessage());
			}
		}
		setIsValidImage(StringUtils.isNotEmpty(documentId));
		return null;
	}

	/**
	 * Method to remove person's uploaded image
	 * 
	 * @param personId
	 *            person identifier
	 * @return TextStreamResponse
	 */
	public TextStreamResponse onActivate(Integer personId) {
		initializeValues();
		String responseText = "false";
		try {
			if (personId != null) {
				IPnPersonService personService = ServiceFactory.getInstance()
						.getPnPersonService();
				PnPerson person = personService.getPersonById(personId);
				person.setImageId(null);
				personService.updatePerson(person);
				SessionManager.getUser().load();
				responseText = "true";
				profile.setImageAvailable(false);
			}
		} catch (Exception e) {
			log.error("Error occured while removing the person image : "
					+ e.getMessage());
		}
		return new TextStreamResponse("text/plain", responseText);
	}

	public void onActivate() {
		initializeValues();
		HttpServletRequest request = requestGlobals.getHTTPServletRequest();
		if (request.getParameter("uploadFor") != null) {
			setUploadFor(request.getParameter("uploadFor"));
			setWeblogEntryId(request.getParameter("weblogEntryId"));
		}

	}

	/**
	 * @return the weblogEntryId
	 */
	public String getWeblogEntryId() {
		return weblogEntryId;
	}

	/**
	 * @param weblogEntryId
	 *            the weblogEntryId to set
	 */
	public void setWeblogEntryId(String weblogEntryId) {
		this.weblogEntryId = weblogEntryId;
	}

	/**
	 * @return the file
	 */
	public UploadedFile getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(UploadedFile file) {
		this.file = file;
	}

	/**
	 * @return the selectImageToUploadLabel
	 */
	public String getSelectImageToUploadLabel() {
		return selectImageToUploadLabel;
	}

	/**
	 * @return the uploadFor
	 */
	public String getUploadFor() {
		return uploadFor;
	}

	/**
	 * @param uploadFor
	 *            the uploadFor to set
	 */
	public void setUploadFor(String uploadFor) {
		this.uploadFor = uploadFor;
	}
	
	public String getCancelbutton() {
		return cancelbutton;
	}

	public void setCancelbutton(String cancelbutton) {
		this.cancelbutton = cancelbutton;
	}

	public String getUploadbutton() {
		return uploadbutton;
	}

	public void setUploadbutton(String uploadbutton) {
		this.uploadbutton = uploadbutton;
	}
	
	/**
	 * @return the isValidImage
	 */
	public boolean getIsValidImage() {
		return isValidImage;
	}

	/**
	 * @param isValidImage
	 */
	public void setIsValidImage(boolean isValideImage) {
		this.isValidImage = isValideImage;
	}
	
	/**
	 * @return the imageTypeNotSupportedMessage
	 */
	public String getImageTypeNotSupportedMessage() {
		return imageTypeNotSupportedMessage;
	}

	/**
	 * @return the selectImageToUploadMessage
	 */
	public String getSelectImageToUploadMessage() {
		return selectImageToUploadMessage;
	}
	
	/**
	 * @return the selectedInvalidImageMessage
	 */
	public String getSelectedInvalidImageMessage() {
		return selectedInvalidImageMessage;
	}

	/**
	 * @return the versionNumber
	 */
	public String getVersionNumber() {
		return versionNumber;
	}
	
	/**
	 * @return the JSPRootURL
	 */
	public String getJSPRootURL() {
		return JSPRootURL;
	}

	/**
	 * @return the errorAlertTitle
	 */
	public String getErrorAlertTitle() {
		return errorAlertTitle;
	}
}
