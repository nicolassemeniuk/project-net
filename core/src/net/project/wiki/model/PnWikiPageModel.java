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
package net.project.wiki.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;

import net.project.hibernate.model.PnObject;
import net.project.hibernate.model.PnWikiPage;
import net.project.security.SessionManager;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

/**
 * This is an object that contains data related to the PN_WIKI_PAGE table.
 * This object is made as alternative to PnWikiPage.java model class in order
 * to format editDate field with user local specific formatters.
 */
public class PnWikiPageModel {
	
	//	primary key
	private java.lang.Integer wikiPageId;

	// fields
	private java.lang.String pageName;

	private java.lang.String content;

	private java.lang.String editDate;
	
	private PnWikiPage parentPageName;			// NC

	private java.lang.String recordStatus;
	
	private String commentText;
	
	private String pageNameForLink;
	
	/**
	 * Links between this wiki page and other objects.
	 */
	private Set<PnObject> assignements;

	// many to one
	private net.project.hibernate.model.PnPerson editedBy;

	private net.project.hibernate.model.PnObject ownerObjectId;
	
	private String deleteAttachmentLink;
	
	// constructors
	public PnWikiPageModel() {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public PnWikiPageModel(java.lang.Integer wikiPageId) {
		this.setWikiPageId(wikiPageId);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public PnWikiPageModel(java.lang.Integer wikiPageId, java.lang.String pageName, java.lang.String content, PnWikiPage parentPageName,
			java.lang.String editDate, net.project.hibernate.model.PnPerson editedBy, net.project.hibernate.model.PnObject ownerObjectId) {

		this.setWikiPageId(wikiPageId);
		this.setPageName(pageName);
		this.setContent(content);
		this.setParentPageName(parentPageName);
		this.setEditDate(editDate);
		this.setEditedBy(editedBy);
		this.setOwnerObjectId(ownerObjectId);
		initialize();
	}

	protected void initialize() {
	}

	private int hashCode = Integer.MIN_VALUE;
	
	/**
	 * Return the unique identifier of this class
	 * @hibernate.id
	 *  generator-class="assigned"
	 *  column="WIKI_PAGE_ID"
	 */
	public java.lang.Integer getWikiPageId() {
		return wikiPageId;
	}

	/**
	 * Set the unique identifier of this class
	 * @param wikiPageId the new ID
	 */
	public void setWikiPageId(java.lang.Integer wikiPageId) {
		this.wikiPageId = wikiPageId;
		this.hashCode = Integer.MIN_VALUE;
	}

	/**
	 * Return the value associated with the column: PAGE_NAME
	 */
	public java.lang.String getPageName() {
		return pageName;
	}

	/**
	 * Set the value related to the column: PAGE_NAME
	 * @param name the PAGE_NAME value
	 */
	public void setPageName(java.lang.String pageName) {
		this.pageName = pageName;
	}

	/**
	 * Return the value associated with the column: CONTENT
	 */
	public java.lang.String getContent() {
		return content;
	}

	/**
	 * Set the value related to the column: CONTENT
	 * @param description the CONTENT value
	 */
	public void setContent(java.lang.String content) {
		this.content = content;
	}

	/**
	 * Return the value associated with the column: PARENT_PAGE_NAME
	 */
	public PnWikiPage getParentPageName() {								// NC
		return parentPageName;
	}

	/**
	 * Set the value related to the column: PARENT_PAGE_NAME
	 * @param name the PARENT_PAGE_NAME value
	 */
	public void setParentPageName(PnWikiPage parentPageName) {	// NC
		this.parentPageName = parentPageName;
	}
	
	/**
	 * Return the value associated with the column: EDITED_BY
	 */
	public net.project.hibernate.model.PnPerson getEditedBy() {
		return editedBy;
	}

	/**
	 * Set the value related to the column: EDITED_BY
	 * @param createdBy the EDITED_BY value
	 */
	public void setEditedBy(net.project.hibernate.model.PnPerson editedBy) {
		this.editedBy = editedBy;
	}

	/**
	 * Return the value associated with the column: EDIT_DATE
	 */
	public java.lang.String getEditDate() {
		return editDate;
	}

	/**
	 * Set the value related to the column: EDIT_DATE
	 * @param creationDate the EDIT_DATE value
	 */
	public void setEditDate(java.lang.String editDate) {
		this.editDate = editDate;
	}

	/**
	 * Return the value associated with the column: OWNER_OBJECT_ID
	 */
	public net.project.hibernate.model.PnObject getOwnerObjectId () {
		return ownerObjectId;
	}

	/**
	 * Set the value related to the column: OWNER_OBJECT_ID
	 * @param spaceId the OWNER_OBJECT_ID value
	 */
	public void setOwnerObjectId (net.project.hibernate.model.PnObject ownerObjectId) {
		this.ownerObjectId = ownerObjectId;
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj)
			return false;
		if (!(obj instanceof net.project.hibernate.model.PnWikiPage))
			return false;
		else {
			net.project.hibernate.model.PnWikiPage pnWikiPage = (net.project.hibernate.model.PnWikiPage) obj;
			if (null == this.getWikiPageId() || null == pnWikiPage.getWikiPageId())
				return false;
			else
				return (this.getWikiPageId().equals(pnWikiPage.getWikiPageId()));
		}
	}

	@Override
	public int hashCode() {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getWikiPageId())
				return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getWikiPageId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}

	public String toString() {
		return new ToStringBuilder(this)
        .append("wikiPageId", getWikiPageId())
        .toString();
	}

	/**
	 * @return the recordStatus
	 */
	public java.lang.String getRecordStatus() {
		return recordStatus;
	}

	/**
	 * @param recordStatus
	 *            the recordStatus to set
	 */
	public void setRecordStatus(java.lang.String recordStatus) {
		this.recordStatus = recordStatus;
	}

	/**
	 * @return the commentText
	 */
	public String getCommentText() {
		return commentText;
	}

	/**
	 * @param commentText the text for describing edits
	 */
	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}

	/**
	 * @return Returns the assignements.
	 */
	public Set<PnObject> getAssignements() {
		return assignements;
	}

	/**
	 * @param assignements The assignements to set.
	 */
	public void setAssignements(Set<PnObject> assignements) {
		this.assignements = assignements;
	}	
     
	/**
	 * TODO: add download servlet url for other than image file attachment urls
	 * @return current open page encoded url
	 */
	public String getOpenPage() {
        return "/wiki/" + ( getParentPageName() == null ? getPageNameForLink() : getParentPageName().getPageName() + "/" + getPageNameForLink());
    }

	/**
	 * @return the deleteAttachmentLink
	 */
	public String getDeleteAttachmentLink() {
		return deleteAttachmentLink;
	}

	/**
	 * @param deleteAttachmentLink the deleteAttachmentLink to set
	 */
	public void setDeleteAttachmentLink(String deleteAttachmentLink) {
		this.deleteAttachmentLink = deleteAttachmentLink;
	}

	/**
	 * @return the pageNameForLink
	 */
	public String getPageNameForLink() {
		return pageNameForLink;
	}

	/**
	 * @param pageNameForLink the pageNameForLink to set
	 */
	public void setPageNameForLink(String pageNameForLink) {
		this.pageNameForLink = pageNameForLink;
	}
	
}
