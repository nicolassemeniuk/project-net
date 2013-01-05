package net.project.activity;

import java.math.BigDecimal;
import java.util.Date;

import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.RecordStatus;
import net.project.base.URLFactory;
import net.project.base.property.PropertyProvider;
import net.project.events.EventType;
import net.project.events.ProjectEvent;
import net.project.form.FormData;
import net.project.hibernate.model.PnActivityLog;
import net.project.hibernate.model.PnClass;
import net.project.hibernate.model.PnDocContainer;
import net.project.hibernate.model.PnDocument;
import net.project.hibernate.model.PnNews;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.model.PnTask;
import net.project.hibernate.model.PnWeblogComment;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.model.PnWikiPage;
import net.project.hibernate.service.IPnWikiPageService;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.HTMLUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class ActivityRssFeedManager {
	
	private static String FORM_NAME_WITH_SEQ_NUM;
	
	/**
	 * Return blog action
	 * @param pnActivityLog
	 * @return
	 */
	public static String getBlogActionForRss(PnActivityLog pnActivityLog) {
		String action = null;
		if (pnActivityLog.getActivityType().equalsIgnoreCase(EventType.EDITED.getText())) {
			action = PropertyProvider.get("prm.blog.activity.editedblogpost.message");
		} else if (pnActivityLog.getActivityType().equalsIgnoreCase(EventType.DELETED.getText())) {
			action = PropertyProvider.get("prm.blog.activity.deletedblogpost.message");
		} else if (pnActivityLog.getActivityType().equalsIgnoreCase(EventType.NEW.getText())){
			action = PropertyProvider.get("prm.blog.activity.createdblogpost.message");
		}
		return action;
	}

	/**
	 * Make title for each object
	 * 
	 * @param pnActivityLog
	 * @return
	 */
	public static String makeRssTitle(PnActivityLog pnActivityLog) {
		String title = null;
		 if (pnActivityLog.getTargetObjectType().equalsIgnoreCase(ObjectType.DOCUMENT)) {
			title = pnActivityLog.getPersonName() + " " + ActivityLogManager.getTokenReplacedDescription(ActivityLogManager.getDocumentAction(pnActivityLog.getActivityType()));
		} else if (pnActivityLog.getTargetObjectType().equalsIgnoreCase(ObjectType.FORM)) {
			title = pnActivityLog.getPersonName() + " " + ActivityLogManager.getTokenReplacedDescription(ActivityLogManager.getFormAction(pnActivityLog.getActivityType())) + " " + pnActivityLog.getObjectName();
		} else if (pnActivityLog.getTargetObjectType().equalsIgnoreCase(ObjectType.NEWS)) {
			title = pnActivityLog.getPersonName() + " " + ActivityLogManager.getTokenReplacedDescription(ActivityLogManager.getNewsAction(pnActivityLog.getActivityType())) + " " + pnActivityLog.getObjectName();
		} else if (pnActivityLog.getTargetObjectType().equalsIgnoreCase(ObjectType.TASK)) {
			title = pnActivityLog.getPersonName() + " " + ActivityLogManager.getTokenReplacedDescription(ActivityLogManager.getTaskAction(pnActivityLog.getActivityType())) + " " + pnActivityLog.getObjectName();
		} else if (pnActivityLog.getTargetObjectType().equalsIgnoreCase(ObjectType.PROJECT)) {
			title = pnActivityLog.getPersonName() + " " 
					+ ActivityLogManager.getTokenReplacedDescription(ActivityLogManager.getProjectAction(pnActivityLog.getActivityType(), getNoOfMembers(ActivityLogManager.getTokenReplacedDescription(pnActivityLog.getDescription())).intValue())) 
					+ " " + pnActivityLog.getObjectName();
		} else if (pnActivityLog.getTargetObjectType().equalsIgnoreCase("doc_container")) {
			title = pnActivityLog.getPersonName() + " " + ActivityLogManager.getTokenReplacedDescription(ActivityLogManager.getDocumentAction(pnActivityLog.getActivityType())) + " " + pnActivityLog.getObjectName();
		}
		return title;
	}

	/**
	 * Return link of object
	 * @param pnActivityLog
	 * @return
	 */
	public static String getObjectLink(PnActivityLog pnActivityLog) {
		String objectLink = null;
		String activityLink = SessionManager.getAppURL() + "/activity/view/?id=" + pnActivityLog.getSpaceId()
				+ "&amp;module=" + Module.PROJECT_SPACE;
		if (pnActivityLog.getTargetObjectType().equalsIgnoreCase(ObjectType.TASK)) {
			if (isTaskDeleted(pnActivityLog)) {
				objectLink = activityLink;
			} else {
				objectLink = SessionManager.getSiteURL() + ""
						+ URLFactory.makeURL(pnActivityLog.getTargetObjectId().toString(), ObjectType.TASK);
			}
		} else if (pnActivityLog.getTargetObjectType().equalsIgnoreCase(ObjectType.DOCUMENT)) {
			if (isDocumentDeleted(pnActivityLog.getTargetObjectId())) {
				objectLink = activityLink;
			} else {
				objectLink = SessionManager.getSiteURL() + ""
						+ URLFactory.makeURL(pnActivityLog.getTargetObjectId().toString(), ObjectType.DOCUMENT);
			}
		} else if (pnActivityLog.getTargetObjectType().equalsIgnoreCase(ObjectType.FORM)) {
			if (isFormDeleted(pnActivityLog)) {
				objectLink = activityLink;
			} else {
				objectLink = SessionManager.getSiteURL() + ""
						+ URLFactory.makeURL(pnActivityLog.getTargetObjectId().toString(), ObjectType.FORM);
			}
		} else if (pnActivityLog.getTargetObjectType().equalsIgnoreCase(ObjectType.FORM_DATA)) {
			if (isFormDataDeleted(pnActivityLog)) {
				objectLink = activityLink;
			} else {
				objectLink = SessionManager.getSiteURL() + ""
						+ URLFactory.makeURL(pnActivityLog.getTargetObjectId().toString(), ObjectType.FORM_DATA);
			}
		} else if (pnActivityLog.getTargetObjectType().equalsIgnoreCase(ObjectType.NEWS)) {
			if (isNewsDeleted(pnActivityLog)) {
				objectLink = activityLink;
			} else {
				objectLink = SessionManager.getSiteURL() + ""
					  + URLFactory.makeURL(pnActivityLog.getTargetObjectId().toString(), ObjectType.NEWS);
			}
		} else if (pnActivityLog.getTargetObjectType().equalsIgnoreCase(ObjectType.PROJECT)) {
			if (isProjectDeleted(pnActivityLog)) {
				objectLink = activityLink;
			} else {
				objectLink = SessionManager.getAppURL() + "/project/Dashboard/?module=" + Module.PROJECT_SPACE
						+ "&amp;id=" + pnActivityLog.getTargetObjectId();
			}
		}  else if (pnActivityLog.getTargetObjectType().equalsIgnoreCase("doc_container")) {
			if (isDocumentDeleted(pnActivityLog.getTargetObjectId())) {
				objectLink = activityLink;
			} else {
				objectLink = SessionManager.getSiteURL() + ""
						+ URLFactory.makeURL(pnActivityLog.getTargetObjectId().toString(), ObjectType.CONTAINER);
			}
		}
		return objectLink;
	}

	/**
	 * Create and return description of object
	 * @param pnActivityLog
	 * @return
	 */ 
	public static String getDescription(PnActivityLog pnActivityLog) {
		String descriptionText = null;
		if (pnActivityLog.getTargetObjectType().equalsIgnoreCase(ObjectType.NEWS)) {
			descriptionText = pnActivityLog.getPersonName() +" "+  ActivityLogManager.getTokenReplacedDescription(ActivityLogManager.getNewsAction(pnActivityLog.getActivityType())) + " " + pnActivityLog.getObjectName() + " "+ getTimeAndDate(pnActivityLog); 
		} else if (pnActivityLog.getTargetObjectType().equalsIgnoreCase(ObjectType.TASK)) {
			descriptionText = pnActivityLog.getPersonName() +" "+  ActivityLogManager.getTokenReplacedDescription(ActivityLogManager.getTaskAction(pnActivityLog.getActivityType())) + " " + pnActivityLog.getObjectName() + " "+ getTimeAndDate(pnActivityLog);
		} else if (pnActivityLog.getTargetObjectType().equalsIgnoreCase(ObjectType.FORM)) {
			descriptionText = pnActivityLog.getPersonName() +" "+  ActivityLogManager.getTokenReplacedDescription(ActivityLogManager.getFormAction(pnActivityLog.getActivityType())) + " " + pnActivityLog.getObjectName()  + " "+ getTimeAndDate(pnActivityLog);
		} else if (pnActivityLog.getTargetObjectType().equalsIgnoreCase(ObjectType.PROJECT)) {
			descriptionText = pnActivityLog.getPersonName() +" "+  ActivityLogManager.getTokenReplacedDescription(ActivityLogManager.getProjectAction(pnActivityLog.getActivityType(), getNoOfMembers(ActivityLogManager.getTokenReplacedDescription(pnActivityLog.getDescription())).intValue())) 
								+ " " + pnActivityLog.getObjectName() + " "+ getTimeAndDate(pnActivityLog);
		} else if (pnActivityLog.getTargetObjectType().equalsIgnoreCase(ObjectType.DOCUMENT)) {
			descriptionText = pnActivityLog.getPersonName() +" "+  ActivityLogManager.getTokenReplacedDescription(ActivityLogManager.getDocumentAction(pnActivityLog.getActivityType())) + " " + pnActivityLog.getObjectName() + " "+ getTimeAndDate(pnActivityLog);
		} else if (pnActivityLog.getTargetObjectType().equalsIgnoreCase("doc_container")) {
			descriptionText = pnActivityLog.getPersonName() +" "+  ActivityLogManager.getTokenReplacedDescription(ActivityLogManager.getDocumentAction(pnActivityLog.getActivityType())) + " " + pnActivityLog.getObjectName() + " "+ getTimeAndDate(pnActivityLog);
		}
		return descriptionText;
	}
	
	/**
	 * Check whether task deleted or not
	 * @param pnActivityLog
	 * @return
	 */
	public static boolean isTaskDeleted(PnActivityLog pnActivityLog){
		PnTask pnTask= ServiceFactory.getInstance().getPnTaskService().getTaskWithRecordStatus(pnActivityLog.getTargetObjectId());
		return (pnTask != null && pnTask.getRecordStatus().equalsIgnoreCase("D"));
	}
	
	/**
	 * Check whether form deleted or not
	 * @param pnActivityLog
	 * @return
	 */
	public static boolean isFormDeleted(PnActivityLog pnActivityLog){
		PnClass pnClass = ServiceFactory.getInstance().getPnClassService().getFormWithRecordStatus(pnActivityLog.getTargetObjectId());
		return (pnClass != null && (pnClass.getRecordStatus().equalsIgnoreCase("D") || pnClass.getRecordStatus().equalsIgnoreCase("P")));
	}
	
	/**
	 * Check whether form hidden or not
	 * @param pnActivityLog
	 * @return boolean
	 */
	public static boolean isFormHidden(PnActivityLog pnActivityLog){
		PnClass pnClass = ServiceFactory.getInstance().getPnClassService().getFormWithRecordStatus(pnActivityLog.getTargetObjectId());
		return (pnClass != null && pnClass.getRecordStatus().equalsIgnoreCase("P"));
	}
	
	/**
	 * Check whether form data deleted or not
	 * @param pnActivityLog
	 * @return
	 */
	public static boolean isFormDataDeleted(PnActivityLog pnActivityLog){
		FormData formData = new FormData();
		try {
			formData.load(pnActivityLog.getTargetObjectId().toString());
		} catch (PersistenceException pnetEx) {
			Logger.getLogger(ActivityRssFeedManager.class).error("Error occured while loading FormData" + pnetEx);
		}
		RecordStatus recordStatus = formData.getRecordStatus();
		FORM_NAME_WITH_SEQ_NUM = formData.getForm().getName()+"-"+formData.getSeqNum();
		return recordStatus != null ? recordStatus.getID().equalsIgnoreCase("D") : false;
	}
	
	/**
	 *Check whether news is deleted or not
	 * @param pnActivityLog
	 * @return
	 */
	public static boolean isNewsDeleted(PnActivityLog pnActivityLog){
		PnNews pnNews = ServiceFactory.getInstance().getPnNewsService().getNewsWithRecordStatus(new BigDecimal(pnActivityLog.getTargetObjectId()));
		return (pnNews != null && pnNews.getRecordStatus().equalsIgnoreCase("D"));
	}
	
	/**
	 * @param pnActivityLog
	 * @return
	 */
	public static boolean isDocumentDeleted(Integer objectId){
		PnDocument pnDocument = ServiceFactory.getInstance().getPnDocumentService().getDocumentDetailsById(objectId);
		PnDocContainer pnDocContainer =  ServiceFactory.getInstance().getPnDocContainerService().getDocContainerWithRecordStatus(objectId);
		return pnDocument != null ? pnDocument.getRecordStatus().equalsIgnoreCase("D")
				|| pnDocument.getRecordStatus().equalsIgnoreCase("H") 
			: (pnDocContainer != null ? pnDocContainer.getRecordStatus().equalsIgnoreCase("D")
				|| pnDocContainer.getRecordStatus().equalsIgnoreCase("H") : false);
	}
	
	/**
	 * @param pnActivityLog
	 * @return
	 */
	public static boolean isProjectDeleted(PnActivityLog pnActivityLog){
		PnProjectSpace pnProjectSpace = ServiceFactory.getInstance().getPnProjectSpaceService().getProjectDetailsWithRecordStatus(pnActivityLog.getTargetObjectId());
		return (pnProjectSpace != null && pnProjectSpace.getRecordStatus().equalsIgnoreCase("D"));
	}

	/**
	 * @param pnActivityLog
	 * @return
	 */
	public static String rssFeedForWiki(PnActivityLog pnActivityLog){
			String wikiAction = ActivityLogManager.getTokenReplacedDescription(ActivityLogManager.getWikiAction(pnActivityLog.getActivityType()));
			return "<item><title>" + HTMLUtils.escape(pnActivityLog.getPersonName()+" " + wikiAction) + "</title>" + "<link>"
			+ getWikiPageURL(pnActivityLog, "rssFeed") + "</link>" + "<description>"
			+ HTMLUtils.escape(pnActivityLog.getPersonName()+ " " + wikiAction + " " +getTimeAndDate(pnActivityLog) ) + "</description></item>";
	}
	
	/**
	 * @param pnActivityLog
	 * @return
	 */
	public static String rssFeedForFormData(PnActivityLog pnActivityLog){
			String url = getObjectLink(pnActivityLog);
			String formDataAction = ActivityLogManager.getTokenReplacedDescription(ActivityLogManager.getFormDataAction(pnActivityLog.getActivityType(), FORM_NAME_WITH_SEQ_NUM));
			return "<item><title>" + HTMLUtils.escape(pnActivityLog.getPersonName()+ " " + formDataAction) +" "+pnActivityLog.getObjectName()+"</title>" + "<link>"
			+ url + "</link>" + "<description>"
			+ HTMLUtils.escape(pnActivityLog.getPersonName()+ " " + formDataAction +" "+pnActivityLog.getObjectName()+ " " +getTimeAndDate(pnActivityLog) ) + "</description></item>";
	}
	
	/**
	 * @param pnActivityLog
	 * @return
	 */
	public static String getBlogEntryContent(PnActivityLog pnActivityLog){
		PnWeblogEntry pnWeblogEntry = ServiceFactory.getInstance().getPnWeblogEntryService().getWeblogEntryDetail(pnActivityLog.getTargetObjectId());
		return pnWeblogEntry.getText().replaceAll("\\<.*?>", "").replaceAll("/&[#]*[\\w|\\d]*;/g", "").replaceAll("&nbsp;", "");
	}
	
	/**
	 * Create description for blog entry  rssfeed 
	 * @param pnActivityLog
	 * @return
	 */
	public static String rssFeedForBlogEntry(PnActivityLog pnActivityLog){
		PnWeblogEntry pnWeblogEntry = ServiceFactory.getInstance().getPnWeblogEntryService().getWeblogEntryDetail(pnActivityLog.getTargetObjectId());
		if(pnWeblogEntry != null) {
			if (StringUtils.isEmpty(pnWeblogEntry.getTitle())) {
		        String text = pnWeblogEntry.getText().replaceAll("\\<.*?>", "").replaceAll("/&[#]*[\\w|\\d]*;/g", "").replaceAll("&nbsp;", "");
		        pnWeblogEntry.setTitle(text.length() > 40 ? text.substring(0, 40) + "..." : text);
		    } else {
		    	pnWeblogEntry.setTitle(pnWeblogEntry.getTitle().length() > 40 ? pnWeblogEntry.getTitle().substring(0, 40) + "..." : pnWeblogEntry.getTitle());
		    	pnWeblogEntry.setTitle(HTMLUtils.escape(pnWeblogEntry.getTitle()));
		    }
			String rssDescription = null;
			String rssLink = null;
			String rssTitle = pnActivityLog.getPersonName()  + " "+getBlogActionForRss(pnActivityLog)+" "+pnWeblogEntry.getTitle();
			if(pnWeblogEntry.getStatus().equalsIgnoreCase("deleted")){
				rssLink = SessionManager.getAppURL() + "/activity/view/?id=" + pnActivityLog.getSpaceId()+ "&amp;module=" + Module.PROJECT_SPACE;
			} else{
				rssLink = SessionManager.getAppURL() + "/blog/view/Show_Entry?id="+ pnWeblogEntry.getWeblogEntryId() + "&amp;module=" + Module.PROJECT_SPACE;
			}
			rssDescription =  pnActivityLog.getPersonName() + " "+getBlogActionForRss(pnActivityLog)+" "+pnWeblogEntry.getText().replaceAll("\\<.*?>", "").replaceAll("/&[#]*[\\w|\\d]*;/g", "").replaceAll("&nbsp;", "")
								+" "+getTimeAndDate(pnActivityLog);
			
			 return "<item><title>" + HTMLUtils.escape(rssTitle) + "</title>" + "<link>"
				+ rssLink + "</link>" + "<description>"
				+ HTMLUtils.escape(rssDescription) + "</description></item>";
		}	 
		return null;
	}
	
	
	/**
	 * Create description for blog entry commentrss feed 
	 * @param pnActivityLog
	 * @return
	 */
	public static String rssFeedForBlogComment(PnActivityLog pnActivityLog){
			PnWeblogComment pnWeblogComment = ServiceFactory.getInstance().getPnWeblogCommentService().getWeblogCommentByCommentId(pnActivityLog.getTargetObjectId());
			String fullComment = null;
			PnWeblogEntry pnWeblogEntry = new PnWeblogEntry();
			if(pnWeblogComment != null){
				fullComment = pnWeblogComment.getContent();
				pnWeblogEntry = pnWeblogComment.getPnWeblogEntry();
			}
			if (pnWeblogEntry != null) {
				PnWeblogEntry weblogEntry = ServiceFactory.getInstance().getPnWeblogEntryService().getWeblogEntryDetail(pnWeblogEntry.getWeblogEntryId());
				if (StringUtils.isEmpty(weblogEntry.getTitle())) {
		            String text = weblogEntry.getText().replaceAll("\\<.*?>", "").replaceAll("/&[#]*[\\w|\\d]*;/g", "").replaceAll("&nbsp;", "");
		            weblogEntry.setTitle(text.length() > 40 ? text.substring(0, 40) + "..." : text);
		        } else {
		        	weblogEntry.setTitle(weblogEntry.getTitle().length() > 40 ? weblogEntry.getTitle().substring(0, 40) + "..." : weblogEntry.getTitle());
		        	weblogEntry.setTitle(HTMLUtils.escape(weblogEntry.getTitle()));
		        }
				String rssDescription = null;
				String rssLink = null;
				String rssTitle = pnActivityLog.getPersonName() + " " + PropertyProvider.get("prm.blogcomment.activity.commentonblog.message") + " " + weblogEntry.getTitle();
				if(weblogEntry.getStatus().equalsIgnoreCase("deleted")){
					rssLink = SessionManager.getAppURL() + "/activity/view/?id=" + pnActivityLog.getSpaceId()+ "&amp;module=" + Module.PROJECT_SPACE;
				} else{
					rssLink = SessionManager.getAppURL() + "/blog/view/Show_Entry?id="+ weblogEntry.getWeblogEntryId() + "&amp;module=" + Module.PROJECT_SPACE;
				}
				rssDescription =  pnActivityLog.getPersonName() + " " + PropertyProvider.get("prm.blogcomment.activity.rssfeed.commentis.message") + " " +fullComment + " "+getTimeAndDate(pnActivityLog);
				
				 return "<item><title>" + HTMLUtils.escape(rssTitle) + "</title>" + "<link>"
					+ rssLink + "</link>" + "<description>"
					+ HTMLUtils.escape(rssDescription) + "</description></item>";
			}	
			return null;
	}
	
	/**
	 * Return person name
	 * @param pnActivityLog
	 * @return
	 */
	public static String getPersonName(Integer activityBy) {
		PnPerson pnPerson = new PnPerson();
		pnPerson = ServiceFactory.getInstance().getPnPersonService().getPesronNameAndImageIdByPersonId(
				activityBy);
		return pnPerson.getDisplayName();
	}

	/**
	 * Create sting of date and time and return it
	 * @param pnActivityLog
	 * @return
	 */
	public static String getTimeAndDate(PnActivityLog pnActivityLog) {
		String activityTime = DateFormat.getInstance().formatDate (
				new Date(pnActivityLog.getActivityOnDate().getTime()), "h:mm a").toLowerCase();
		String activityDate = DateFormat.getInstance().formatDate (
				new Date(pnActivityLog.getActivityOnDate().getTime()), "MMM dd");
		return " " + PropertyProvider.get("prm.activity.dateon.message") + " " + activityDate + " " + PropertyProvider.get("prm.activity.at.message") + " " + activityTime;
	}
	
	
	/**
	 * Check if blog entry is deleted or not
	 * @param objectId
	 * @param objectType
	 * @return
	 */
	public static String checkBlogEntryDeleted(String objectId, String objectType) {
		PnWeblogEntry pnWeblogEntry = new PnWeblogEntry();
		 if(objectType.equalsIgnoreCase(ObjectType.BLOG_COMMENT)){
			PnWeblogComment pnWeblogComment = ServiceFactory.getInstance().getPnWeblogCommentService().getWeblogCommentByCommentId(Integer.parseInt(objectId));
			PnWeblogEntry weblogEntry = new PnWeblogEntry();
			if(pnWeblogComment != null){
				weblogEntry = pnWeblogComment.getPnWeblogEntry();
			}
			if (pnWeblogEntry != null) {
				pnWeblogEntry = ServiceFactory.getInstance().getPnWeblogEntryService().getWeblogEntryDetail(weblogEntry.getWeblogEntryId());
			}
		} else if(objectType.equalsIgnoreCase(ObjectType.BLOG_ENTRY)) {
			 pnWeblogEntry = ServiceFactory.getInstance().getPnWeblogEntryService().getWeblogEntryDetail(Integer.parseInt(objectId));
		}	
		if (pnWeblogEntry.getStatus().equalsIgnoreCase("deleted")) {
			return "deleted";
		} else {
			return SessionManager.getSiteURL()+URLFactory.makeURL(pnWeblogEntry.getWeblogEntryId().toString(), ObjectType.BLOG).replaceAll(URLFactory.AMP, URLFactory.URLAMP);
		}
	}
	
	/**
	 * Check whether wiki page is deleted or not
	 * @param pnActivityLog
	 * @param urlFor
	 * @return URL String
	 */
	public static String getWikiPageURL(PnActivityLog pnActivityLog, String urlFor) {
		String wikiPageUrl = null;
		IPnWikiPageService pnWikiPageService = ServiceFactory.getInstance().getPnWikiPageService();
		PnWikiPage pnWikiPage = pnActivityLog.getParentObjectId() != null ? 
										pnWikiPageService.getWikiPageWithPageNameAndRecordStatusWithParent(pnActivityLog.getTargetObjectId())
										: pnWikiPageService.getWikiPageWithPageNameAndRecordStatusWithoutParent(pnActivityLog.getTargetObjectId());
			if (pnWikiPage != null) {
				if (pnWikiPage.getRecordStatus().equalsIgnoreCase("D")) {
					wikiPageUrl = urlFor.equals("rssFeed") ? SessionManager.getAppURL() + "/activity/view/?id=" + pnActivityLog.getSpaceId()+ "&amp;module=" + Module.PROJECT_SPACE	: "deleted";
				} else {
					wikiPageUrl = SessionManager.getAppURL()
							+ URLFactory.SLASH
							+ URLFactory.WIKI_ROOT_PATH
							+ URLFactory.SLASH
							+ (pnWikiPage.getParentPageName() == null ? pnWikiPage
									.getPageName() : pnWikiPage.getParentPageName()
									.getPageName()
									+ URLFactory.SLASH + pnWikiPage.getPageName());
				}
			} else if (pnActivityLog.getObjectName().startsWith("Image:")) {
				wikiPageUrl = urlFor.equals("rssFeed") ? SessionManager.getAppURL() + "/activity/view/?id=" + pnActivityLog.getSpaceId()+ "&amp;module=" + Module.PROJECT_SPACE	: "deleted";
			}
		return wikiPageUrl;
	}
	
	/**
	 * Get no. of members invited or deleted from space
	 * @param description
	 * @return Integer
	 */
	private static Integer getNoOfMembers(String description) {
		if(!(description.contains(ProjectEvent.MEMBER_ADDED) || description.contains(ProjectEvent.MEMBER_REMOVED)))
			return 0;
		String inviteMember = description.contains(ProjectEvent.MEMBER_ADDED) && description.contains(PropertyProvider.get("prm.project.activity.invitedmembers.message")) ? 
								PropertyProvider.get("prm.project.activity.invitedmembers.message") : PropertyProvider.get("prm.project.activity.invitedmember.message");
		String removeMember = description.contains(ProjectEvent.MEMBER_REMOVED) && description.contains(PropertyProvider.get("prm.project.activity.removedmembers.message")) ?
								PropertyProvider.get("prm.project.activity.removedmembers.message") : PropertyProvider.get("prm.project.activity.removedmember.message");
		String partialDescription = description.substring(description.indexOf(description.contains(ProjectEvent.MEMBER_ADDED) ? ProjectEvent.MEMBER_ADDED : ProjectEvent.MEMBER_REMOVED));
		return (Integer.parseInt(partialDescription.substring(partialDescription.indexOf(" "), partialDescription.indexOf(partialDescription.contains(ProjectEvent.MEMBER_ADDED) ? inviteMember.substring(0, inviteMember.indexOf(" ")) : removeMember.substring(0, removeMember.indexOf(" ")))).trim()));
	}
}