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
package net.project.blog;

import java.util.List;

import net.project.base.DefaultDirectory;
import net.project.business.BusinessDeleteWizard;
import net.project.hibernate.model.PnWeblogComment;
import net.project.notification.ImmediateNotification;
import net.project.notification.NotificationException;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpace;
import net.project.project.ProjectSpaceFinder;
import net.project.resource.Person;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.xml.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class BlogCommentNotification extends ImmediateNotification {

	private static Logger logger = Logger.getLogger(BusinessDeleteWizard.class);

	public static final String BLOGCOMMENT_NOTIFICATION_STYLESHEET = "/blog/BlogComment.xsl";

	private StringBuffer xml = new StringBuffer();

	public BlogCommentNotification() {
		super();
	}

	public void initialize(PnWeblogComment comment, String personId, User personByCmmentPosted, String blogEntryUrl) throws NotificationException {
		Person personToDeliver = null;
		boolean isBlogOwner = false;

		try {
			personToDeliver = loadCommenter(personId);
			String projectName = loadProjectName(comment.getPnWeblogEntry().getPnProjectSpace().getProjectId());
			isBlogOwner = isCurrentPersonBlogOwner(personId, comment.getPnWeblogEntry().getPnPerson().getPersonId().toString());

			setDeliveryTypeID(SessionManager.getUser().getDefaultNotificationDeliveryType());
			setXSLStylesheetPath(BLOGCOMMENT_NOTIFICATION_STYLESHEET);
			setDeliveryAddress(personToDeliver.getEmail().trim());
			setContentType(TEXT_HTML);
			// customisation user is set as "receiver"
			if (DefaultDirectory.isUserRegisteredByID(personToDeliver.getID().toString())) {
				setCustomizationUserID(personToDeliver.getID().toString());
			} else {
				setCustomizationUserID(SessionManager.getUser().getID());
			}
			xml.append(net.project.xml.IXMLTags.XML_VERSION_STRING);
			xml.append("<CommentNotification>");
			xml.append("<personName>" + XMLUtils.escape(personToDeliver.getDisplayName()) + "</personName>");
			xml.append("<blogProject><![CDATA[" + projectName + "]]></blogProject>");
			xml.append("<commentPosted><![CDATA[" + comment.getContent() + "]]></commentPosted>");
			xml.append("<blogEntryTitle><![CDATA[" + comment.getPnWeblogEntry().getTitle() + "]]></blogEntryTitle>");
			xml.append("<personByCommentPosted>" + XMLUtils.escape(personByCmmentPosted.getDisplayName()) + "</personByCommentPosted>");
			xml.append("<blogOwner>" + isBlogOwner + "</blogOwner>");
			xml.append("<blogContent><![CDATA[" + comment.getPnWeblogEntry().getText() + "]]></blogContent>");
			xml.append("<blogEntryUrl>"+ blogEntryUrl + "</blogEntryUrl>");
			xml.append("<pubTime>" + XMLUtils.escape(comment.getPostTimeString()) + "</pubTime>");
			xml.append("</CommentNotification>");
			setNotificationXML(xml.toString());
		} catch (Exception e) {
			// We'll log this as a problem
			logger.warn("Unable to load commenter with ID" + personId, e);
			throw new NotificationException("Unable to load commenter with ID: " + e, e);
		}
	}
	
	private String loadProjectName( Integer projectID ) {
		ProjectSpaceFinder projectFinder = new ProjectSpaceFinder();
		List<ProjectSpace> projects;
		try {
			projects = projectFinder.findByID(projectID.toString());
		} catch (PersistenceException e) {
			return StringUtils.EMPTY;
		}
		
		if (projects != null && !projects.isEmpty()) {
			return projects.get(0).getName();
		} else{
			return StringUtils.EMPTY;
		}
	}

	private Person loadCommenter(String personID) throws PersistenceException {

		Person person = new Person();
		person.setID(personID);
		person.load();

		return person;
	}

	private boolean isCurrentPersonBlogOwner(String personId, String currentUserId) {
		return personId.equals(currentUserId);
	}

}