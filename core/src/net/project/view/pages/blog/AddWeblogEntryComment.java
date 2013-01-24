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
package net.project.view.pages.blog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.project.base.EventException;
import net.project.base.EventFactory;
import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.PnWebloggerException;
import net.project.base.property.PropertyProvider;
import net.project.blog.BlogCommentNotification;
import net.project.events.BlogEvent;
import net.project.events.EventType;
import net.project.hibernate.constants.WeblogConstants;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.model.PnWeblogComment;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.service.IBlogProvider;
import net.project.hibernate.service.ServiceFactory;
import net.project.notification.NotificationException;
import net.project.project.ProjectSpace;
import net.project.resource.Person;
import net.project.resource.PersonFinder;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;
import net.project.util.DateFormat;
import net.project.util.InvalidDateException;
import net.project.util.Version;
import net.project.view.pages.base.BasePage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.util.TextStreamResponse;

/**
 * @author
 *
 */
public class AddWeblogEntryComment extends BasePage{
	
	private static Logger log;
	
	@Persist 
	private String versionNumber; 
	
	private String name;
			
	private String subject;
	
	private String content;	
	
	private String date;
	
	private String nameLabel;
	
	private String commentSubjectLabel;
	
	private String commentIsImportantLabel;
	
	private String subjectValidationMessage;
	
	private String contentValidationMessage;
	
	private String validationMessage;
	
	@Persist
	private IBlogProvider blogProvider;
	
	@Persist
	private PnWeblogEntry pnWeblogEntry;
	
	@InjectPage
	private ViewBlog blog;
	
	@Persist
	private String message;
	
	@Persist
	private String weblogDateFormatPattern;
	
	private String addCommentButtonCaption;
	
	private String cancelButtonCaption;
	
	/**
	 * Initialize values
	 */
	void initialize() {
		log = Logger.getLogger(AddWebLogEntry.class);
		blogProvider = ServiceFactory.getInstance().getBlogProvider();
		validationMessage = PropertyProvider.get("prm.blog.addweblogentrycomment.validation.message");
		weblogDateFormatPattern = "EEEEE, MMM dd, yyyy";
		addCommentButtonCaption = PropertyProvider.get("prm.blog.addweblogentrycomment.addcommentbutton.caption");
		cancelButtonCaption = PropertyProvider.get("prm.blog.addweblogentrycomment.cancelbutton.caption");
	}
	
	/**
	 * Initializing values on page render
	 */
	@SetupRender
	private void setValues(){		
		if (net.project.security.SessionManager.getUser() == null) {
			throw new IllegalStateException("User is null");
		}
		date = SessionManager.getUser().getDateFormatter().formatDate(Calendar.getInstance().getTime(), weblogDateFormatPattern);
		versionNumber = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
	}
	
	/**
	 * Method to find weblogEntry by id and set it to weblogEntry object
	 * on page activation and for other actions like save etc.
	 * 
	 * @param weblogEntryId
	 */
	Object onActivate(String action){
		if (net.project.security.SessionManager.getUser() == null) {
			throw new IllegalStateException("User is null");
		}
		initialize();
		String returnText = "false";
		try {			
			if(action != null){
				if(action.contains("Save")) {
					String weblogEntryId = getRequest().getParameter("weblogEntryId");
					if(weblogEntryId != null){
						if(net.project.util.StringUtils.isNotEmpty(getRequest().getParameter("isActivityComment")) 
								          && getRequest().getParameter("isActivityComment").equals("true")){
							setPnWeblogEntry(blogProvider.getWeblogEntryWithSpaceId(Integer.parseInt(weblogEntryId)));
						}
						if(pnWeblogEntry.getStatus().equalsIgnoreCase("deleted")){
							returnText = "deleted";
						} else {	
							setContent(getRequest().getParameter("content"));
							if(net.project.util.StringUtils.isNotEmpty(getRequest().getParameter("content")) && saveComment() != null){
									returnText = "true";
							}
						}
						return new TextStreamResponse("text/plain", returnText);
					}
				}else if(action.contains("Delete")) {
					if (net.project.util.StringUtils.isNotEmpty(getRequest().getParameter("commentId"))) {
						Integer updateStatus = ServiceFactory.getInstance().getPnWeblogCommentService()
								.updateCommentStatus(Integer.parseInt(getRequest().getParameter("commentId")),
										WeblogConstants.COMMENT_DISAPPROVED_STATUS);
						returnText = (updateStatus == 1) ? "true" : "false";
					}
					return new TextStreamResponse("text/plain", returnText);
				} else {
					setPnWeblogEntry(blogProvider.getWeblogEntryWithSpaceId(Integer.parseInt(action)));
				}
			}
		} catch (Exception pnetEx) {
			return new TextStreamResponse("text/plain", returnText);
		}
		return null;
	}
	
	/**
	 * Method called on action from form for saving weblog entry comment 
	 * 
	 * @return instance of ViewBlog to view blog entries
	 */
	Object onAction(){
		if(getContent() != null){
			saveComment();
			blog.setWebblogCommentAdded(true);
			setPnWeblogEntry(null);
			return blog;
		} 
		setMessage(validationMessage);
		return null;
	}
	
	private Integer saveComment(){
		Integer commentId = null;
		User user = SessionManager.getUser();
		PnWeblogComment pnWeblogComment = new PnWeblogComment();
		// creating weblog comment object from data provided from user to store in database
		pnWeblogComment.setName(user.getDisplayName());
		pnWeblogComment.setEmail(user.getEmail());
		pnWeblogComment.setContent(getContent());
		try {
			DateFormat userDateFormat = DateFormat.getInstance();
			pnWeblogComment.setPostTime(userDateFormat.parseDateTimeString(userDateFormat.formatDateTime(Calendar.getInstance().getTime())));
			pnWeblogComment.setPostTimeString(SessionManager.getUser().getDateFormatter().formatDate(
					pnWeblogComment.getPostTime(), "h:mm a, MMM dd, yyyy "));
		} catch (InvalidDateException pnetEx1) {
			log.error("Error occurred while getting current date by users date format "+pnetEx1.getMessage());
		}
		pnWeblogComment.setNotify(WeblogConstants.DONT_NOTIFY);
		pnWeblogComment.setStatus(WeblogConstants.COMMENT_APPROVED_STATUS);
		pnWeblogComment.setContentType(WeblogConstants.CONTENT_TYPE_TEXT_HTML);
		
		try {
			pnWeblogComment.setPnWeblogEntry(pnWeblogEntry);
			pnWeblogComment.setPersonId(Integer.parseInt(SessionManager.getUser().getID()));
			pnWeblogComment.setCommentId(null);
			Set<PnWeblogComment> commentList = ServiceFactory.getInstance().getPnWeblogCommentService().getWeblogCommentsForWeblogEntry(pnWeblogEntry.getWeblogEntryId());
			commentId = blogProvider.saveComment(pnWeblogComment);
			
			// publishing the blog event
            BlogEvent blogCommentEvent = (BlogEvent) EventFactory.getEvent(ObjectType.BLOG_COMMENT, EventType.COMMENTED);
            blogCommentEvent.setBlogEntryTitle(pnWeblogEntry.getTitle());
            blogCommentEvent.setObjectID(commentId.toString());
            blogCommentEvent.setObjectType(ObjectType.BLOG_COMMENT);
            blogCommentEvent.setSpaceID(pnWeblogEntry.getPnWeblog().getSpaceId().toString());
            blogCommentEvent.setParentObjectId(pnWeblogEntry.getWeblogEntryId().toString());
            blogCommentEvent.publish();
			
			if(PropertyProvider.getBoolean("prm.blog.notification.isenabled", true)){
		        String entryUrl = null;
		        String spaceType = getUser().getCurrentSpace().getSpaceType().getName().toLowerCase().equals("personal") ? Space.PERSONAL_SPACE : 
		        				   getUser().getCurrentSpace().getSpaceType().getName().toLowerCase();
		        
		        if(spaceType.equals(Space.PROJECT_SPACE) && !getUser().getCurrentSpace().getID().equals(pnWeblogEntry.getPnWeblog().getSpaceId().toString())){
	        		 spaceType = Space.PERSONAL_SPACE;
		        } 
	        	entryUrl = SessionManager.getAppURL() + "/blog/view/Show_Entry?id="+ pnWeblogEntry.getWeblogEntryId()
					+ "&amp;userId=" + pnWeblogEntry.getPnPerson().getPersonId() + "&amp;spaceType="
					+ spaceType + "&amp;spaceId="
					+ (spaceType.equals(Space.PERSONAL_SPACE) ?  getUser().getID() : getUser().getCurrentSpace().getID()) + "&amp;module="
					+ (spaceType.equals(Space.PERSONAL_SPACE) ? Module.PERSONAL_SPACE : Module.getModuleForSpaceType(spaceType));
	    
		        try { 
					if(!pnWeblogEntry.getPnPerson().getPersonId().toString().equals(user.getID())){
						pnWeblogComment.getPnWeblogEntry().setPnProjectSpace(new PnProjectSpace(pnWeblogEntry.getPnWeblog().getSpaceId()));
						sendCommentEmailNotification(pnWeblogComment, pnWeblogEntry.getPnPerson().getPersonId().toString(), user, entryUrl);
					}
					HashMap<Integer, Object> mailsentList = new HashMap<Integer, Object>();
					for(PnWeblogComment comment:commentList){
						if(comment.getEmail()!=null && !comment.getPersonId().toString().equals(pnWeblogEntry.getPnPerson().getPersonId().toString()) && !comment.getPersonId().toString().equals(user.getID()) ){
							if(mailsentList.get(comment.getPersonId()) == null) {
								pnWeblogComment.getPnWeblogEntry().setPnProjectSpace(new PnProjectSpace(pnWeblogEntry.getPnWeblog().getSpaceId()));
								pnWeblogComment.getPnWeblogEntry().setPnProjectSpace(new PnProjectSpace(pnWeblogEntry.getPnWeblog().getSpaceId()));
								sendCommentEmailNotification(pnWeblogComment,comment.getPersonId().toString(), user, entryUrl);
							}
							mailsentList.put(comment.getPersonId(), comment.getPersonId());
						}
					}
					
				}catch(Exception ex){
					// log and consume the emailing exception for now
					log.error("Error occurred while sending email : " + ex);
				}
			}
		} catch (EventException ex) {
			log.error("AddWebLogEntryComment.saveComment():: Blog Comment Create Event Publishing Failed! "+ ex.getMessage());
		} catch (PnWebloggerException pnetEx) {
			log.error("Error occurred while saving comment: "+pnetEx.getMessage());
		}
		return commentId;
	}
	
	/**
	 * Sends email when comment is added 
	 * @param pnWeblogComment
	 * @param personId
	 * @param personByCmmentPosted
	 */
	private void sendCommentEmailNotification(PnWeblogComment pnWeblogComment,String personId,User personByCmmentPosted, String blogEntryUrl){
		BlogCommentNotification notification=new BlogCommentNotification();
		try {
			notification.initialize(pnWeblogComment, personId, personByCmmentPosted, blogEntryUrl);
			notification.post();
		} catch (NotificationException pnetEx) {
			log.error("Error occurred while sendind comment email: " + pnetEx);
		}
	}
	/**
	 * Cleaning values after rendering 
	 */
	@CleanupRender
	void cleanValuesAfterRender(){
		message = null;		
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the pnWeblogEntry
	 */
	public PnWeblogEntry getPnWeblogEntry() {
		return pnWeblogEntry;
	}

	/**
	 * @param pnWeblogEntry the pnWeblogEntry to set
	 */
	public void setPnWeblogEntry(PnWeblogEntry pnWeblogEntry) {
		this.pnWeblogEntry = pnWeblogEntry;
	}

	/**
	 * @return the commentIsImportantLabel
	 */
	public String getCommentIsImportantLabel() {
		return commentIsImportantLabel;
	}

	/**
	 * @return the commentSubjectLabel
	 */
	public String getCommentSubjectLabel() {
		return commentSubjectLabel;
	}

	/**
	 * @return the contentValidationMessage
	 */
	public String getContentValidationMessage() {
		return contentValidationMessage;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @return the subjectValidationMessage
	 */
	public String getSubjectValidationMessage() {
		return subjectValidationMessage;
	}

	/**
	 * @return the validationMessage
	 */
	public String getValidationMessage() {
		return validationMessage;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the nameLabel
	 */
	public String getNameLabel() {
		return nameLabel;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the versionNumber
	 */
	public String getVersionNumber() {
		return versionNumber;
	}

	/**
	 * @return the addCommentButtonCaption
	 */
	public String getAddCommentButtonCaption() {
		return addCommentButtonCaption;
	}

	/**
	 * @return the cancelButtonCaption
	 */
	public String getCancelButtonCaption() {
		return cancelButtonCaption;
	}
}
