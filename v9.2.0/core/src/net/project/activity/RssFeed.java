
package net.project.activity;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.hibernate.model.PnActivityLog;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpaceBean;
import net.project.security.EncryptionException;
import net.project.security.EncryptionManager;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.HTMLUtils;
import net.project.util.StringUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

public class RssFeed extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/xml");
		String spaceId = null;
		String filterCriteria = null;
		List<String> filterCriteriaValues = null;
		String[] params = request.getRequestURI().split("/");
		try {
			String decryptedId  = EncryptionManager.decryptBlowfish(params[params.length-2]);
			spaceId = decryptedId.substring(0, decryptedId.indexOf("++"));
			filterCriteria = params[params.length-1];
			if (StringUtils.isNotEmpty(filterCriteria)) {
				String[] criteriaValues = filterCriteria.split(",");
				filterCriteriaValues = new ArrayList<String>();
				for (String criteriavalue : criteriaValues) {
						filterCriteriaValues.add(criteriavalue);
				}
			}
			if (StringUtils.isNotEmpty(spaceId)) {
				
				String feedXmlString = createActivityListAndXmlString(null, null, spaceId,
						filterCriteria, filterCriteriaValues);
				PrintWriter out = response.getWriter();
				out.print(feedXmlString);
			} else {
				PrintWriter out = response.getWriter();
				out.print(getErrorMessage());
			}
		} catch (EncryptionException exc) {
			Logger.getLogger(RssFeed.class).error("Error occurred while decryption" + exc);
			PrintWriter out = response.getWriter();
			out.print(getErrorMessage());
		}

	}
	
	/**
	 * Create activity log list and make xml string
	 * @param sDate
	 * @param eDate
	 * @param spaceId
	 * @param filterCriteria
	 * @param filterCriteriaValues
	 * @param formListValues
	 * @return XmlString
	 */
	public String createActivityListAndXmlString(Date sDate, Date eDate, String spaceId, String filterCriteria,
													List<String> filterCriteriaValues) {
		Integer currentUserId = null;
		// check if no user has logged in
		if(SessionManager.getUser() != null && StringUtils.isNotEmpty(SessionManager.getUser().getID()))
			currentUserId = Integer.parseInt(SessionManager.getUser().getID());
		List<PnActivityLog> activityLogList = new ArrayList<PnActivityLog>();
		if (StringUtils.isEmpty(filterCriteria)) {
			activityLogList = ServiceFactory.getInstance().getPnActivityLogService().getActivityLogBySpaceIdAndDate(
				Integer.parseInt(spaceId), null,null, null, null, 0, 30, currentUserId);
		} else {
			activityLogList = CollectionUtils.isNotEmpty(filterCriteriaValues) && filterCriteriaValues.size() > 1 ? ServiceFactory
				.getInstance().getPnActivityLogService().getActivityLogBySpaceIdAndDate(Integer.parseInt(spaceId),
						null,null,ActivityLogManager.getFormattedCriteriaForRSS(filterCriteriaValues), null, 0, 30, currentUserId)
				: new ArrayList<PnActivityLog>();
		}
		return feedString(activityLogList, spaceId);
	}

	/**
	 * Create xml string for RSS
	 * @param activityLogList
	 * @param spaceId
	 * @return
	 */
	public String feedString(List<PnActivityLog> activityLogList, String spaceId) {
		String projectName = ServiceFactory.getInstance().getPnObjectNameService().getNameFofObject(Integer.parseInt(spaceId));
		if (SessionManager.getUser() == null || SessionManager.getUser().getID() == null) {
			User user = new User("1");
			ProjectSpaceBean projectSpaceBean = new ProjectSpaceBean();
			projectSpaceBean.setID(spaceId);
			try {
				projectSpaceBean.load();
				user.setCurrentSpace(projectSpaceBean);
			} catch (PersistenceException pnetEx) {
				Logger.getLogger(RssFeed.class).error("Error occured while loading project space" + pnetEx);
			} catch (PnetException pnetEx) {
				Logger.getLogger(RssFeed.class).error("Error occured while set current space" + pnetEx);
			}
			SessionManager.setUser(user);
		}
		String xmlString = "<?xml version=\"1.0\" ?> <rss version=\"2.0\">" + "<channel><title>" + HTMLUtils.escape(projectName)
				+ "</title>" + "<link>" + SessionManager.getAppURL() + "/project/Dashboard/?module="
				+ Module.PROJECT_SPACE + "&amp;id=" + spaceId + "</link>" + "<description>"
				+ PropertyProvider.get("prm.project.activity.rss.projectnametitle") + "</description>";
		if (CollectionUtils.isNotEmpty(activityLogList)) {
			for (PnActivityLog pnActivityLog : activityLogList) {
				if(pnActivityLog.getTargetObjectType().equalsIgnoreCase(ObjectType.BLOG_ENTRY)){
					xmlString += ActivityRssFeedManager.rssFeedForBlogEntry(pnActivityLog); 
				}else if (pnActivityLog.getTargetObjectType().equalsIgnoreCase(ObjectType.BLOG_COMMENT)){
					xmlString += ActivityRssFeedManager.rssFeedForBlogComment(pnActivityLog); 
				}else if (pnActivityLog.getTargetObjectType().equalsIgnoreCase(ObjectType.WIKI)){
					xmlString += ActivityRssFeedManager.rssFeedForWiki(pnActivityLog); 	
				}else if (pnActivityLog.getTargetObjectType().equalsIgnoreCase(ObjectType.FORM_DATA)){
					xmlString += ActivityRssFeedManager.rssFeedForFormData(pnActivityLog);
				}else{
				xmlString += "<item><title>" + HTMLUtils.escape(ActivityRssFeedManager.makeRssTitle(pnActivityLog)) + "</title>" + "<link>"
						+ ActivityRssFeedManager.getObjectLink(pnActivityLog) + "</link>" + "<description>"
						+ HTMLUtils.escape(ActivityRssFeedManager.getDescription(pnActivityLog)) + "</description></item>";
				}
			}
		} else {
			xmlString += "<item><title>" + PropertyProvider.get("prm.project.activity.rss.norssfeeds")
					+ "</title></item>";
		}
		xmlString += "</channel></rss>";
		return xmlString;
	}

	/**
	 * Get error message 
	 * @return xml string
	 */
	public String getErrorMessage() {
		return  "<?xml version=\"1.0\" ?> <rss version=\"2.0\">"
				+ "<channel><title>"+PropertyProvider.get("prm.project.activity.rss.wrongurl")+"</title>"
				+ "<description>"+PropertyProvider.get("prm.project.activity.rss.wrongdescription")+"</description></channel></rss>";
	}
}
