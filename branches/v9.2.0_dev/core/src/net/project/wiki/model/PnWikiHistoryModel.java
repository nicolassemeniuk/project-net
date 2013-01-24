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

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Class made as helper class for usage in T5 loop coponent. <br>
 * Represents a Map with key/value pairs. <br>
 * 
 * @author uros
 */
public class PnWikiHistoryModel {
	//	primary key
	private java.lang.Integer wikiHistoryId;
	
	private java.lang.Integer versionNo;

	// fields
	private java.lang.String content;

	private java.lang.String commentText;

	private String editDate;
	
	private java.lang.Integer previousHistoryId;
	
	//many to one
	private net.project.hibernate.model.PnWikiPage wikiPageId;

	private net.project.hibernate.model.PnPerson editedBy;
	
	private boolean isPageNameRename;
	

	public PnWikiHistoryModel() {
	}

	/**
	 * Constructor for primary key
	 */
	public PnWikiHistoryModel(java.lang.Integer wikiHistoryId) {
		this.setWikiHistoryId(wikiHistoryId);
	}

	/**
	 * Constructor for required fields
	 */
	public PnWikiHistoryModel(java.lang.Integer wikiHistoryId, java.lang.String content,
			net.project.hibernate.model.PnPerson editedBy, String editDate, 
			net.project.hibernate.model.PnWikiPage wikiPageId) {

		this.setWikiHistoryId(wikiHistoryId);
		this.setContent(content);
//		this.setCommentText(commentText);
		this.setEditedBy(editedBy);
		this.setEditDate(editDate);
		this.setWikiPageId(wikiPageId);
		initialize();
	}

	protected void initialize() {
	}

	private int hashCode = Integer.MIN_VALUE;

	/**
	 * Return the unique identifier of this class
	 * @hibernate.id
	 *  generator-class="assigned"
	 *  column="WIKI_HISTORY_ID"
	 */
	public java.lang.Integer getWikiHistoryId() {
		return wikiHistoryId;
	}

	/**
	 * Set the unique identifier of this class
	 * @param wikiHistoryId the new ID
	 */
	public void setWikiHistoryId(java.lang.Integer wikiHistoryId) {
		this.wikiHistoryId = wikiHistoryId;
		this.hashCode = Integer.MIN_VALUE;
	}

	/**
	 * Return the value associated with the column: COMMNENT_TEXT
	 */
	public java.lang.String getCommentText() {
		return commentText;
	}

	/**
	 * Set the value related to the column: COMMENT_TEXT
	 * @param name the COMMENT_TEXT value
	 */
	public void setCommentText(java.lang.String comment) {
		this.commentText = comment;
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
	 * Return the value associated with the column: EDIT_DATE
	 */
	public String getEditDate() {
		return editDate;
	}

	/**
	 * Set the value related to the column: EDIT_DATE
	 * @param editDate the EDIT_DATE value
	 */
	public void setEditDate(String editDate) {
		this.editDate = editDate;
	}

	/**
	 * Return the value associated with the column: WIKI_PAGE_ID
	 */
	public net.project.hibernate.model.PnWikiPage getWikiPageId () {
		return wikiPageId;
	}

	/**
	 * Set the value related to the column: WIKI_PAGE_ID
	 * @param spaceId the WIKI_PAGE_ID value
	 */
	public void setWikiPageId (net.project.hibernate.model.PnWikiPage wikiPageId) {
		this.wikiPageId = wikiPageId;
	}

	/**
	 * Return the value associated with the column: EDITED_BY
	 */
	public net.project.hibernate.model.PnPerson getEditedBy() {
		return editedBy;
	}

	/**
	 * Set the value related to the column: EDITED_BY
	 * @param pnUser the EDITED_BY value
	 */
	public void setEditedBy(net.project.hibernate.model.PnPerson editor) {
		this.editedBy = editor;
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj)
			return false;
		if (!(obj instanceof net.project.hibernate.model.PnWikiHistory))
			return false;
		else {
			net.project.hibernate.model.PnWikiHistory pnWikiHistory = (net.project.hibernate.model.PnWikiHistory) obj;
			if (null == this.getWikiHistoryId() || null == pnWikiHistory.getWikiHistoryId())
				return false;
			else
				return (this.getWikiHistoryId().equals(pnWikiHistory.getWikiHistoryId()));
		}
	}

	@Override
	public int hashCode() {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getWikiHistoryId())
				return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getWikiHistoryId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}

	public String toString() {
		return new ToStringBuilder(this)
        .append("wikiHistoryId", getWikiHistoryId())
        .toString();
	}

	/**
	 * @return the versionNo
	 */
	public java.lang.Integer getVersionNo() {
		return versionNo;
	}

	/**
	 * @param versionNo the versionNo to set
	 */
	public void setVersionNo(java.lang.Integer versionNo) {
		this.versionNo = versionNo;
	}

	/**
	 * @return the previousHistoryId
	 */
	public java.lang.Integer getPreviousHistoryId() {
		return previousHistoryId;
	}

	/**
	 * @param previousHistoryId the previousHistoryId to set
	 */
	public void setPreviousHistoryId(java.lang.Integer previousHistoryId) {
		this.previousHistoryId = previousHistoryId;
	}

	/**
	 * @return the isPageNameRename
	 */
	public boolean getPageNameRename() {
		return isPageNameRename;
	}

	/**
	 * @param isPageNameRename the isPageNameRename to set
	 */
	public void setPageNameRename(boolean isPageNameRename) {
		this.isPageNameRename = isPageNameRename;
	}

}
