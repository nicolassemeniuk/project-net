/**
 * 
 */
package net.project.document;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.service.IBlogProvider;
import net.project.hibernate.service.IBlogViewProvider;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.space.SpaceTypes;
import net.project.util.DateFormat;
import net.project.view.pages.blog.BlogEntries;
import net.project.view.pages.documents.DocumentsDetails;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.util.TextStreamResponse;

public class DocumentsDataFactory {

	private static Logger log = Logger.getLogger(DocumentsDetails.class);
	
	/** 
	 * Method to load blog entries
	 *  
	 * @param docContainerId
	 * @param objectId
	 * @return blogEntries
	 */
	public Object loadBlogEntries(HttpServletRequest request, DocumentManagerBean documentManagerBean,
		IBlogViewProvider blogViewProvider, BlogEntries blogEntries, Integer moduleId, String userId,
			String spaceType) {

		String jSPRootURL = SessionManager.getJSPRootURL();
		String objectId = request.getParameter("objectId");
		String documentObjectType = request.getParameter("documentObjectType");
		String currentSpaceId = SessionManager.getUser().getCurrentSpace().getID();

		documentManagerBean.setCurrentObjectID(objectId);
		List<PnWeblogEntry> entries = null;
		IBlogProvider blogProvider = ServiceFactory.getInstance().getBlogProvider();
		blogViewProvider = ServiceFactory.getInstance().getBlogViewProvider();
		Integer urlModuleId = moduleId;

		try {
			if (StringUtils.isEmpty("" + objectId) || (StringUtils.isNotEmpty("" + objectId) && objectId.equals(0))) {
				blogEntries.setMessage("<br/>Select any object from left pane to see corresponding blog entries.");
				blogEntries.setUserWeblogEntries(null);
				blogEntries.setTotalWorkDone("");
				blogEntries.setIsMoreEntriesToSee(false);
				blogEntries.setMorePostUrl("");
			} else if (documentObjectType.equals(ObjectType.CONTAINER)) {
				return new TextStreamResponse("text", "<br /><div style=\"padding-left: 15px;\">"
						+ "<label style=\"text-align: center; color: #848484; \">"
						+ "Blog is not supported for this object!</label>");
			} else if (objectId != null) {
				entries = blogProvider.getWeblogEntriesByObjectId(objectId);
			}
			if (entries == null || entries.size() <= 0) {
				blogEntries.setMessage("Blog entries not found.");
				blogEntries.setUserWeblogEntries(null);
			} else {
				if (entries.size() > 10) {
					entries = entries.subList(0, 10);
					urlModuleId = Module.PROJECT_SPACE;
					blogEntries.setMessage(entries.size() + " blog entries shown");
					blogEntries.setIsMoreEntriesToSee(true);
					blogEntries.setMorePostUrl(jSPRootURL + "/blog/view/" + currentSpaceId + "/" + userId + "/"
							+ spaceType + "/" + moduleId + "?module=" + moduleId);
				} else {
					blogEntries.setIsMoreEntriesToSee(false);
					blogEntries.setMorePostUrl("");
					blogEntries.setMessage(entries.size() == 1 ? "1 blog entry found" : entries.size()
							+ " blog entries found");
				}

				blogEntries.setJspRootURL(jSPRootURL);
				blogEntries.setUserWeblogEntries(blogViewProvider.getFormattedBlogEntries(entries, jSPRootURL,
						SpaceTypes.PERSONAL_SPACE, DateFormat.getInstance()));
				blogEntries.setLinkToPersonSpace(false);
				blogEntries.setShowEditLink(false);
				blogEntries.setShowExpandCollapseImage(false);
				blogEntries.setShowPersonImage(false);
				blogEntries.setBlogCommentDivClass("");
				blogEntries.setBlogPostDivClass("post-body1");
				blogEntries.setShowCommentLink(true);
			}
			blogEntries.setTotalWorkDone("");
			blogEntries.setIsAssignmentPage(true);
			return blogEntries;
		} catch (Exception e) {
			log.error("Error occured while loading blog entries." + e.getMessage());
		}
		return null;
	}
}
