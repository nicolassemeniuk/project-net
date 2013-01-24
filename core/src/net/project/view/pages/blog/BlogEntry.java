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
package net.project.view.pages.blog;

import java.util.ArrayList;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.project.base.property.PropertyProvider;
import net.project.hibernate.constants.WeblogConstants;
import net.project.hibernate.model.PnWeblogComment;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.service.IBlogProvider;
import net.project.hibernate.service.IBlogViewProvider;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.space.SpaceTypes;
import net.project.view.pages.base.BasePage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

public class BlogEntry extends BasePage{

	private static Logger log;
	
	@Persist
	private PnWeblogEntry pnWeblogEntry;

	@Persist
	private boolean linkToPersonSpace;
	
	private String entryPostedByLabel;

	private String commentOnLabel;

	private PnWeblogComment pnWeblogComment;

	private String fromLabel;

	private String commentsLabel;

	private String addACommentLink;

	private String editLink;

	private String importantSymbolTooltip;

	@Persist
	private String message;

	@Inject
	private RequestGlobals requestGlobals;
	
	@Persist
	private boolean showEditLink;
	
	@Persist
	private boolean showPersonImage;
	
	@Persist
	private boolean showExpandCollapseImage;
	
	@Persist
	private Set<PnWeblogComment> comments;
	
	@Persist
	private boolean showEntry;
	
	@Persist
	private boolean showComment;

	private boolean showLinks = true;

	private boolean showEntryTooltip = false;

	@Persist
	private boolean isAssignmentPage;

	private enum BlogAction {
		SHOW_BLOG_ENTRY, SHOW_LAST_BLOG_ENTRY
	}

	@Persist
	private String blogCommentDivClass;

	@Persist
	private String blogPostDivClass;
	
	@Property
	private String collapseIconTooltip;
	
	@Property
	private String expandIconTooltip;
	
	@Property
	private Integer blogEntryId;
	
	void onActivate() {
		if (net.project.security.SessionManager.getUser() == null) {
			throw new IllegalStateException("User is null");
		}
		try {
			log = Logger.getLogger(BlogEntries.class);
			importantSymbolTooltip = PropertyProvider.get("prm.blog.viewblog.importantsymbol.tooltip");
			entryPostedByLabel = PropertyProvider.get("prm.blog.viewblog.entrypostedby.label");
			commentOnLabel = PropertyProvider.get("prm.blog.viewblog.commenton.label");
			fromLabel = PropertyProvider.get("prm.blog.viewblog.from.label");
			commentsLabel = PropertyProvider.get("prm.blog.viewblog.comments.label");
			addACommentLink = PropertyProvider.get("prm.blog.viewblog.addacomment.link");
			editLink = PropertyProvider.get("prm.blog.viewblog.edit.link");
			expandIconTooltip = PropertyProvider.get("prm.blog.viewblog.expand.tooltip");
			collapseIconTooltip = PropertyProvider.get("prm.blog.viewblog.collapse.tooltip");
		} catch (Exception e) {
			log.error("Error occured while getting property tokens for blog entry page : "+ e.getMessage());
		}		
	}

	/**
	 * @param action
	 * @return
	 */
	Object onActivate(String action) {
		if (net.project.security.SessionManager.getUser() == null) {
			throw new IllegalStateException("User is null");
		}
		IBlogProvider blogProvider = ServiceFactory.getInstance().getBlogProvider();
		
		blogEntryId = null;
		if (action.equalsIgnoreCase(BlogAction.SHOW_BLOG_ENTRY.toString())) {
			if(StringUtils.isNotEmpty(getRequest().getParameter("weblogEntryId"))){
				blogEntryId = Integer.valueOf(getRequest().getParameter("weblogEntryId"));
			}
			String isCommentClassRequired = StringUtils.isEmpty(getRequest().getParameter("isCommentClassRequired")) ? "false" : getRequest()
					.getParameter("isCommentClassRequired");
			try {
				if (blogEntryId != null) {
					comments = blogProvider.getWeblogCommentsForWeblogEntry(blogEntryId, WeblogConstants.COMMENT_APPROVED_STATUS);
					if (CollectionUtils.isNotEmpty(comments)) {
						for (PnWeblogComment comment : comments) {
							comment.setPostTimeString(SessionManager.getUser().getDateFormatter().formatDate(
									comment.getPostTime(), "h:mm a"));
							comment.setPostDateString(SessionManager.getUser().getDateFormatter().formatDate(
									comment.getPostTime(), "MMM d, yyyy "));
							if(getUser().getID().equals(comment.getPersonId().toString()) || getUser().getID().equals(comment.getEntryByPersonId().toString())
									|| (getUser().getCurrentSpace().getSpaceType().getName().equalsIgnoreCase(Space.PROJECT_SPACE) && getUser().isSpaceAdministrator())){
										comment.setIsDeletable(true);
							}
						}
					}
					// the comment-entry css class is not required on my assignment page (used for alignment purpose)
					if (isCommentClassRequired.equals("false")) {
						setBlogCommentDivClass("");
						setBlogPostDivClass("post-body1");
					} else {
						setBlogCommentDivClass("comment-entry");
						setBlogPostDivClass("post-body");
					}
					setIsAssignmentPage(false);
					setShowEntry(false);
					setShowComment(true);
				}
			} catch (Exception e) {
				log.error("Error occured while loading comments for a blog entry : " + e.getMessage());
			}
		} else if (action.equalsIgnoreCase(BlogAction.SHOW_LAST_BLOG_ENTRY.toString())) {
			Integer personId = null;
			java.util.List<PnWeblogEntry> userWeblogEntries = new ArrayList<PnWeblogEntry>();
			java.util.List<PnWeblogEntry> formatedEntry = new ArrayList<PnWeblogEntry>();
			IBlogViewProvider blogViewProvider = ServiceFactory.getInstance()
					.getBlogViewProvider();

			if (StringUtils.isNotEmpty(getRequest().getParameter("weblogEntryId"))) {
				blogEntryId = Integer.valueOf(getRequest()
						.getParameter("weblogEntryId"));
			}

			if (StringUtils.isNotEmpty(getRequest().getParameter("personId"))) {
				personId = Integer.valueOf(getRequest().getParameter("personId"));
			}
			try {
				if (blogEntryId != null) {
					setPnWeblogEntry(blogProvider.getWeblogEntryDetail(blogEntryId, personId));
					if (pnWeblogEntry != null) {
						userWeblogEntries.add(pnWeblogEntry);
						if (CollectionUtils.isNotEmpty(userWeblogEntries)) {
							formatedEntry = blogViewProvider.getFormattedBlogEntries(userWeblogEntries,
											getJSPRootURL(), SpaceTypes.PROJECT_SPACE, SessionManager.getUser().getDateFormatter());
							if (CollectionUtils.isNotEmpty(formatedEntry)) {
								setPnWeblogEntry(formatedEntry.get(0));
							}
						}
					}
				}
				setShowComment(false);
				setShowLinks(false);
				setShowEntryTooltip(true);
				setShowEntry(false);
			} catch (Exception e) {
				log.error("Error occured while loading last blog entry : " + e.getMessage());
			}
		}
		
		return null;
	}

	public PnWeblogEntry getPnWeblogEntry() {
		return pnWeblogEntry;
	}

	public void setPnWeblogEntry(PnWeblogEntry pnWeblogEntry) {
		this.pnWeblogEntry = pnWeblogEntry;
	}

	public boolean getLinkToPersonSpace() {
		return linkToPersonSpace;
	}

	public void setLinkToPersonSpace(boolean linkToPersonSpace) {
		this.linkToPersonSpace = linkToPersonSpace;
	}

	public String getEntryPostedByLabel() {
		return entryPostedByLabel;
	}

	public void setEntryPostedByLabel(String entryPostedByLabel) {
		this.entryPostedByLabel = entryPostedByLabel;
	}

	public String getCommentOnLabel() {
		return commentOnLabel;
	}

	public void setCommentOnLabel(String commentOnLabel) {
		this.commentOnLabel = commentOnLabel;
	}

	public PnWeblogComment getPnWeblogComment() {
		return pnWeblogComment;
	}

	public void setPnWeblogComment(PnWeblogComment pnWeblogComment) {
		this.pnWeblogComment = pnWeblogComment;
	}

	public String getFromLabel() {
		return fromLabel;
	}

	public void setFromLabel(String fromLabel) {
		this.fromLabel = fromLabel;
	}

	public String getAddACommentLink() {
		return addACommentLink;
	}

	public void setAddACommentLink(String addACommentLink) {
		this.addACommentLink = addACommentLink;
	}

	public String getCommentsLabel() {
		return commentsLabel;
	}

	public void setCommentsLabel(String commentsLabel) {
		this.commentsLabel = commentsLabel;
	}

	public String getEditLink() {
		return editLink;
	}

	public void setEditLink(String editLink) {
		this.editLink = editLink;
	}

	public String getImportantSymbolTooltip() {
		return importantSymbolTooltip;
	}

	public void setImportantSymbolTooltip(String importantSymbolTooltip) {
		this.importantSymbolTooltip = importantSymbolTooltip;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	public boolean getShowEditLink() {
		return showEditLink;
	}

	public void setShowEditLink(boolean showEditLink) {
		this.showEditLink = showEditLink;
	}

	public boolean getShowExpandCollapseImage() {
		return showExpandCollapseImage;
	}

	public void setShowExpandCollapseImage(boolean showExpandCollapseImage) {
		this.showExpandCollapseImage = showExpandCollapseImage;
	}

	public boolean getShowPersonImage() {
		return showPersonImage;
	}

	public void setShowPersonImage(boolean showPersonImage) {
		this.showPersonImage = showPersonImage;
	}

	public Set<PnWeblogComment> getComments() {
		return comments;
	}

	public void setComments(Set<PnWeblogComment> comments) {
		this.comments = comments;
	}

	public boolean getShowComment() {
		return showComment;
	}

	public void setShowComment(boolean showComment) {
		this.showComment = showComment;
	}

	public boolean getShowEntry() {
		return showEntry;
	}

	public void setShowEntry(boolean showEntry) {
		this.showEntry = showEntry;
	}

	public String getBlogCommentDivClass() {
		return blogCommentDivClass;
	}

	public void setBlogCommentDivClass(String blogCommentDivClass) {
		this.blogCommentDivClass = blogCommentDivClass;
	}

	public String getBlogPostDivClass() {
		return blogPostDivClass;
	}

	public void setBlogPostDivClass(String blogPostDivClass) {
		this.blogPostDivClass = blogPostDivClass;
	}
	
	public boolean isShowLinks() {
		return showLinks;
	}

	public void setShowLinks(boolean showLinks) {
		this.showLinks = showLinks;
	}
	/**
	 * @return the isAssignmentPage
	 */
	public boolean getIsAssignmentPage() {
		return isAssignmentPage;
	}

	public boolean isShowEntryTooltip() {
		return showEntryTooltip;
	}
	
	public void setShowEntryTooltip(boolean showEntryTooltip) {
		this.showEntryTooltip = showEntryTooltip;
	}
	
	/**
	 * @param isAssignmentPage the isAssignmentPage to set
	 */
	public void setIsAssignmentPage(boolean isAssignmentPage) {
		this.isAssignmentPage = isAssignmentPage;
	}
}
