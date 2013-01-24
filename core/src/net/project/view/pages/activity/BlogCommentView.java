/**
 * 
 */
package net.project.view.pages.activity;

import java.util.Date;
import java.util.Set;

import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.property.PropertyProvider;
import net.project.hibernate.constants.WeblogConstants;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnWeblogComment;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class BlogCommentView extends BasePage {

	@Property
	private String activityLogId;

	@Property
	private String personLink;

	@Property
	private Set<PnWeblogComment> comment;

	@Property
	private String fullText;

	@Property
	private PnWeblogComment pnWeblogComment;

	@Property
	private boolean isCommentEntry = false;

	@Property
	private String toPersonLink;
	
	@Property
	private String personImg;
	
	@Property
	private String personName;
	
	@Property
	private String blogTitle;
	
	@Property
	private String objectLink;
	
	@Property
	private boolean isMarked;
	
	@Property
	private String markedUnmarkedToken;
	
	@Property
	private String blogEntryToken;
	
	@Property
	private boolean isBlogEntryNotDeleted;
	
	@Property
	private Integer webBlogEntryId;
	
	@Property
	private String collapse;
	
	@SetupRender
	void initializeSchedule() {
	}

	Object onActivate(String action) {
		blogEntryToken = PropertyProvider.get("prm.project.activity.blogentry.title");
		collapse = PropertyProvider.get("prm.project.activity.collapse.lable");
		int noOfCommentEntries = 0;
		if (action.equals("showFullBlogEntry")) {
			isMarked = StringUtils.isNotEmpty(getRequest().getParameter("isMarked")) && getRequest().getParameter("isMarked").equals("true");
			markedUnmarkedToken =  PropertyProvider.get(isMarked ? "prm.project.activity.marked.label" : "prm.project.activity.unmarked.label");
			if (StringUtils.isNotEmpty(getRequest().getParameter("activityLogId"))) {
				activityLogId = getRequest().getParameter("activityLogId");
			}
			if (StringUtils.isNotEmpty(getRequest().getParameter("activityBy"))) {
				personLink = SessionManager.getJSPRootURL() + "/blog/view/" + getRequest().getParameter("activityBy")
						+ "/" + getRequest().getParameter("activityBy") + "/person/" + Module.PERSONAL_SPACE
						+ "?module=" + Module.PERSONAL_SPACE;
			}
			if (StringUtils.isNotEmpty(getRequest().getParameter("objectId"))) {
				if (StringUtils.isNotEmpty(getRequest().getParameter("actionFor")) && getRequest().getParameter("actionFor").equals("blogEntry")) {
					PnWeblogEntry pnWeblogEntry = ServiceFactory.getInstance().getPnWeblogEntryService().getWeblogEntryDetail(Integer.parseInt(getRequest().getParameter("objectId")));
					comment = ServiceFactory.getInstance().getPnWeblogCommentService().getWeblogCommentsForWeblogEntry(pnWeblogEntry.getWeblogEntryId());
					if (comment != null && !comment.isEmpty()) {
						for (PnWeblogComment pnWeblogComment : comment) {
							String activityTime = DateFormat.getInstance().formatDate(
									new Date(pnWeblogComment.getPostTime().getTime()), "h:mm a").toLowerCase();
							String activityDate = DateFormat.getInstance().formatDate(
									new Date(pnWeblogComment.getPostTime().getTime()), "MMM dd");
							pnWeblogComment.setPostTimeString( activityDate + " <span class=\"gray\"> at " + activityTime+"</span>");
							if(noOfCommentEntries == comment.size()-1)
								pnWeblogComment.setIsLastEntry(true);
							noOfCommentEntries++;	
						}
					}
					fullText = pnWeblogEntry.getText();
					isBlogEntryNotDeleted = !pnWeblogEntry.getStatus().equals(WeblogConstants.STATUS_DELETED);
				} else if (StringUtils.isNotEmpty(getRequest().getParameter("actionFor")) && getRequest().getParameter("actionFor").equals("blogComment")) {
					isCommentEntry = true;
					personImg = getPersonImage(getRequest().getParameter("activityBy"));
					PnWeblogComment pnWeblogComment = ServiceFactory.getInstance().getPnWeblogCommentService().getWeblogCommentByCommentId(Integer.parseInt(getRequest().getParameter("objectId")));
					PnWeblogEntry pnWeblogEntry = null;
					if (pnWeblogComment != null) {
						pnWeblogEntry = pnWeblogComment.getPnWeblogEntry();
						webBlogEntryId = pnWeblogEntry.getWeblogEntryId();
						personName = pnWeblogEntry.getPnPerson().getDisplayName();
					}
					if (pnWeblogEntry != null) {
						pnWeblogEntry = ServiceFactory.getInstance().getPnWeblogEntryService().getWeblogEntryDetail(
								pnWeblogEntry.getWeblogEntryId());
						comment = ServiceFactory.getInstance().getPnWeblogCommentService().getWeblogCommentsForWeblogEntry(pnWeblogEntry.getWeblogEntryId());
						if (comment != null && !comment.isEmpty()) {
							for (PnWeblogComment weblogComment : comment) {
								String activityTime = DateFormat.getInstance().formatDate(
										new Date(weblogComment.getPostTime().getTime()), "h:mm a").toLowerCase();
								String activityDate = DateFormat.getInstance().formatDate(
										new Date(weblogComment.getPostTime().getTime()), "MMM dd");
								weblogComment.setPostTimeString( "<b>"+activityDate + "</b> <span class=\"gray\"> at " + activityTime+"</span>");
								if(getRequest().getParameter("objectId").equalsIgnoreCase(weblogComment.getCommentId().toString()) ){
									weblogComment.setCommentId(null);
								}
								if(noOfCommentEntries == comment.size()-1)
									weblogComment.setIsLastEntry(true);
								noOfCommentEntries++;	
							}
						}
						String important = (StringUtils.isNotEmpty(getRequest().getParameter("isImportantBlog")) && getRequest().getParameter("isImportantBlog").equals("true"))
											? "!" : "";
						objectLink = "javascript:checkAndRedirect('"+ pnWeblogEntry.getWeblogEntryId() + "','" + ObjectType.BLOG_ENTRY + "','" + activityLogId + "');";
						blogTitle = pnWeblogEntry.getTitle();
						blogTitle = "&nbsp;<a id=\"objectLink_"+activityLogId+"\" class=\"activityBodyText\" href="+objectLink+" title="+pnWeblogEntry.getTitle()+"><font color=\"red\"><b>"+important+"</b></font>"+(blogTitle.length() > 40 ? blogTitle.substring(0, 40)+"..." : blogTitle)+"</a> <span " + "class=\"gray\"> at " + getTime(pnWeblogEntry.getUpdateTime())+"</span>";
						fullText = pnWeblogEntry.getText();
						PnPerson pnPerson = pnWeblogEntry.getPnPerson();
						toPersonLink = SessionManager.getJSPRootURL() + "/blog/view/" + pnPerson.getPersonId() + "/"+ pnPerson.getPersonId() + "/person/" + Module.PERSONAL_SPACE + "?module="
								+ Module.PERSONAL_SPACE;
					}
					isBlogEntryNotDeleted = !pnWeblogEntry.getStatus().equals(WeblogConstants.STATUS_DELETED);
				}
			}
		}
		return null;
	}
	
	private String getPersonImage(String activityBy){
		PnPerson pnPerson = ServiceFactory.getInstance().getPnPersonService().getPesronNameAndImageIdByPersonId(Integer.parseInt(activityBy));
		 if(pnPerson.getImageId()!=null)
			 return SessionManager.getJSPRootURL()+"/servlet/photo?id="+pnPerson.getPersonId()+"&amp;size=thumbnail"+Module.PERSONAL_SPACE;
		 else
			 return SessionManager.getJSPRootURL()+"/images/NoPicture.gif";
	 }
	
	private String getTime(Date blogPostDate){
		return DateFormat.getInstance().formatDate(new Date(blogPostDate.getTime()), "h:mm a").toLowerCase();
	 }

}
