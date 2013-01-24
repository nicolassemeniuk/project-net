package net.project.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.URLFactory;
import net.project.base.property.PropertyProvider;
import net.project.events.EventType;
import net.project.events.ProjectEvent;
import net.project.hibernate.model.PnActivityLog;
import net.project.hibernate.model.PnDocContainer;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnWeblogComment;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.EncryptionException;
import net.project.security.EncryptionManager;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.StringUtils;
import net.project.view.pages.activity.ActivityLogMap;

import org.apache.log4j.Logger;

public class ActivityLogManager {

	/**
	 * Create map for activity log list
	 * @param result
	 * @return
	 */
	public static List<ActivityLogMap> getActivityLog(Map result) {
		List<ActivityLogMap> formattedActivityList = new ArrayList<ActivityLogMap>();

		List list = Arrays.asList(result.entrySet().toArray());
		ListIterator resultIt = list.listIterator(result.size());
		while (resultIt.hasPrevious()) {
			Map.Entry entry = (Map.Entry) resultIt.previous();
			List<PnActivityLog> formattedDateList = new ArrayList<PnActivityLog>();
			List<PnActivityLog> rcList = (List<PnActivityLog>) entry.getValue();
			ListIterator rcValueIt = rcList.listIterator();
			while (rcValueIt.hasNext()) {
				PnActivityLog currPg = (PnActivityLog) rcValueIt.next();
				formattedDateList.add(currPg); // add formatted PnWikiPage to formatted list
			}
			formattedActivityList.add(new ActivityLogMap((Date) entry.getKey(), formattedDateList));
		}
		return formattedActivityList;
	}

	/**
	 * Format the filter criteria value
	 * @param rawCriteria
	 * @return
	 */
	public static String getFormattedCriteria(List<String> rawCriteria) {
		boolean isMyCommentedBlog = rawCriteria.contains(ObjectType.BLOG_ENTRY+"-my_posts_that_are_commented");
		boolean isBlogImp = rawCriteria.contains(ObjectType.BLOG_ENTRY+"-important");
		if(isBlogImp) {
			rawCriteria.remove(rawCriteria.indexOf(ObjectType.BLOG_ENTRY+"-important"));
		}
		if(isMyCommentedBlog) { 
			rawCriteria.remove(rawCriteria.indexOf(ObjectType.BLOG_ENTRY+"-my_posts_that_are_commented"));
		}
		StringBuffer formattedCriteria = new StringBuffer();
		List<String> objects = new ArrayList<String>();
		List<String> events = new ArrayList<String>(0);
		for (String value : rawCriteria) {
			String[] obj_event = value.split("-");
			if (obj_event.length == 2) {
				if (objects.indexOf(obj_event[0]) < 0) {
					objects.add(obj_event[0]);
					events.add("");
				}
				int objectIndex = objects.indexOf(obj_event[0]);
				if (objectIndex >= 0 && !events.get(objectIndex).contains(obj_event[1]))
					events.set(objectIndex, events.get(objectIndex) + "'" + obj_event[1].toLowerCase() + "',");
			}
		}
		for (int index = 0; index < objects.size(); index++) {
			if (StringUtils.isNotEmpty(events.get(index))) {
				if (index > 0) {
					formattedCriteria.append(" OR (");
				} else {
					formattedCriteria.append(" AND ( (");
				}
				formattedCriteria.append(" al.targetObjectType = '");
				if(objects.get(index).startsWith(ObjectType.FORM_DATA)){
					formattedCriteria.append(objects.get(index).substring(0, objects.get(index).lastIndexOf("_")));
				} else {
					formattedCriteria.append(objects.get(index));
				}
				formattedCriteria.append("'");
				formattedCriteria.append(" AND ");
				formattedCriteria.append(" al.activityType IN ( ").append(events.get(index).length() > 0 ? events.get(index).substring(0, events.get(index).length() - 1) : "").append(" ) ");
				if(objects.get(index).startsWith(ObjectType.FORM_DATA)){
					formattedCriteria.append(" AND al.parentObjectId = "+Integer.parseInt(objects.get(index).substring(objects.get(index).lastIndexOf("_")+1))).append(" ");
				}
				if(objects.get(index).equals(ObjectType.BLOG_ENTRY) && isBlogImp) {
					formattedCriteria.append(" AND al.isImportant = 1 ");
				}
				if(objects.get(index).equals(ObjectType.BLOG_ENTRY) && isMyCommentedBlog) {
					formattedCriteria.append(" AND al.targetObjectId IN ");
					formattedCriteria.append(" ( SELECT DISTINCT al.targetObjectId FROM PnActivityLog al, PnActivityLog pal ");
					formattedCriteria.append(" WHERE pal.targetObjectType = 'blog_comment' ");
					formattedCriteria.append(" AND pal.parentObjectId = al.targetObjectId ");
					formattedCriteria.append(" AND al.spaceId = " + SessionManager.getUser().getCurrentSpace().getID() + " ");
					formattedCriteria.append(" AND al.activityBy = " + SessionManager.getUser().getID() + " ) ");
				}
				if(objects.get(index).equals(ObjectType.BLOG_COMMENT) && isBlogImp) {
					formattedCriteria.append(" AND al.parentObjectId IN ( SELECT DISTINCT pal.targetObjectId FROM PnActivityLog pal ");
					formattedCriteria.append(" WHERE  pal.targetObjectId = al.parentObjectId ");
					formattedCriteria.append(" AND pal.targetObjectType = 'blog_entry' ");
					formattedCriteria.append(" AND pal.isImportant = 1 ");
					formattedCriteria.append(" AND pal.spaceId = " + SessionManager.getUser().getCurrentSpace().getID() + " ) ");
				}
							
				formattedCriteria.append(" ) ");
			}
			if (index == objects.size() - 1) {
				formattedCriteria.append(" ) ");
			}
		}
		if(rawCriteria.contains("Marked")){
			formattedCriteria.append("AND al.activityLogId = alm.comp_id.activityLogId ");
		}
		return formattedCriteria.toString();
	}
	
	/**
	 * Format the filter criteria value for RSS
	 * @param criteria
	 * @return String
	 */
	public static String getFormattedCriteriaForRSS(List<String> criteria) {
		StringBuffer formattedCriteria = new StringBuffer();

		for (int index = 0; index < criteria.size(); index++) {
			if (index > 0) {
				formattedCriteria.append(" OR (");
			} else {
				formattedCriteria.append(" AND ( (");
			}
			formattedCriteria.append(" al.targetObjectType = '");
			if (criteria.get(index).equalsIgnoreCase(ObjectType.FORM)) {
				formattedCriteria.append(ObjectType.FORM + "' OR al.targetObjectType = '" + ObjectType.FORM_DATA
						+ "' ");
			} else {
				formattedCriteria.append(criteria.get(index)).append("'");
			}
			formattedCriteria.append(" ) ");
			if (index == criteria.size() - 1) {
				formattedCriteria.append(" ) ");
			}
		}
		return formattedCriteria.toString();
	}

	/**
	 * Set date and time for retireve activity log list
	 * @param date
	 * @param hours
	 * @param minutes
	 * @param seconds
	 * @param am_pm
	 * @return
	 */
	public static Date getTimeChangedDate(Date date, int hours, int minutes, int seconds, int am_pm) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR, hours);
		cal.set(Calendar.MINUTE, minutes);
		cal.set(Calendar.SECOND, seconds);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.AM_PM, am_pm);
		return cal.getTime();
	}
	
	 /**
	 * Encrypt the string parameters for RSS feed link
	 * @param spaceId
	 * @param comboVal
	 * @param filterCriteriaValue
	 * @return
	 */
	public static String encryptString(String spaceId){
		String encryptedValue = null;
		spaceId += "++" + new Random().nextInt(1000);
		try {
			encryptedValue = EncryptionManager.encryptBlowfish(spaceId);
		} catch (EncryptionException e) {
			Logger.getLogger(ActivityLogManager.class).error("Error occurred while encrypting" + e);
		}
		return encryptedValue;
	}
	
	/**
	 * Create document description
	 * @param action
	 * @param recordStatus
	 * @return String
	 */
	public static String documentDescription(EventType action, String recordStatus){
		String htmlStringDescription = null;
			htmlStringDescription =  returnHtmlString(
					recordStatus.equalsIgnoreCase("D"), getDocumentAction(action.getText()));
			return htmlStringDescription;
	}
	
	/**
	 * Create form description
	 * @param action
	 * @param objectType
	 * @param recordStatus
	 * @return String
	 */
	public static String formAndFormDataDescription(EventType action, String objectType, String recordStatus, String formNameWithSequenceNumber, String creatorEmail, boolean isEafForm){
		if(objectType.equalsIgnoreCase(ObjectType.FORM)){
			String htmlStringDescription = null;
				htmlStringDescription =  returnHtmlString( recordStatus.equalsIgnoreCase("D"), 
			              getFormAction(action.getText()));
			return htmlStringDescription;
		} else {
			String htmlStringDescription = null;
				htmlStringDescription =  returnHtmlString(recordStatus.equalsIgnoreCase("D") ,
						 getFormDataAction(action.getText(), null), formNameWithSequenceNumber, creatorEmail, isEafForm);
			return htmlStringDescription;
		}
	}
	
	/**
	 * Create task description
	 * @param action
	 * @param recordStatus
	 * @return String
	 */
	public static String taskDescription(EventType action, String recordStatus){
		String htmlStringDescription = null;
			 htmlStringDescription =  returnHtmlString( recordStatus.equalsIgnoreCase("D"),
		              getTaskAction(action.getText()));
		return htmlStringDescription;
	}
	
	/**
	 * Create project description
	 * @param action
	 * @param recordStatus
	 * @return String
	 */
	public static String projectDescription(EventType action, String recordStatus, int noOfMembers){
		String htmlStringDescription = null;
			 htmlStringDescription =  returnHtmlString( recordStatus.equalsIgnoreCase("D"),
		               getProjectAction(action.getText(), noOfMembers));
		return htmlStringDescription;
	}

	/**
	 * Get project action
	 * @param action
	 * @return
	 */
	public static String getProjectAction(String action, int noOfMembers) {
		String actionTaken = null;
		if (action.equalsIgnoreCase(EventType.NEW.getText())) {
			actionTaken = "{$token$:prm.project.activity.projectcreated.message}";
		} else if(action.equalsIgnoreCase(EventType.EDITED.getText())){
			actionTaken = "{$token$:prm.project.activity.projectupdated.message}";
		} else if(action.equalsIgnoreCase(EventType.MEMBER_ADDED_TO_SPACE.getText())){
			if(noOfMembers == 1)
				actionTaken = ProjectEvent.MEMBER_ADDED + " 1 {$token$:prm.project.activity.invitedmember.message}";
			else
				actionTaken = ProjectEvent.MEMBER_ADDED + " " + noOfMembers + " {$token$:prm.project.activity.invitedmembers.message}";
		} else if(action.equalsIgnoreCase(EventType.MEMBER_DELETED_FROM_SPACE.getText())){
			if(noOfMembers == 1)
				actionTaken = ProjectEvent.MEMBER_REMOVED + " 1 {$token$:prm.project.activity.removedmember.message}";
			else
				actionTaken = ProjectEvent.MEMBER_REMOVED + " " + noOfMembers + " {$token$:prm.project.activity.removedmembers.message}";
		} else if(action.equalsIgnoreCase(EventType.OVERALL_STATUS_CHANGED.getText())){
			actionTaken = "{$token$:prm.project.activity.overallstatuschanged.message}";
		}
		return actionTaken;
	}
	
	/**
	 * Create news description
	 * @param action
	 * @param recordStatus
	 * @return String
	 */
	public static String newsDescription(EventType action, String recordStatus){
		String htmlStringDescription = null;
			 htmlStringDescription =  returnHtmlString( recordStatus.equalsIgnoreCase("D"),
		              getNewsAction(action.getText()));
		return htmlStringDescription;
	}
	
	/**
	 * Create wiki description
	 * @param action
	 * @param wikiPageName
	 * @param recordStatus
	 * @return String
	 */
	public static String wikiDescription(EventType action, String wikiPageName, String recordStatus){
		String htmlStringDescription = null;  
		htmlStringDescription =  returnHtmlString( recordStatus.equalsIgnoreCase("D"),
	              getWikiAction(action.getText()));
		return htmlStringDescription;
	}
	
	/**
	 * Create blog description
	 * @param blogEntryId
	 * @param action
	 * @param activityBy
	 * @param activityTime
	 * @param objectType
	 * @return
	 */
	public static String blogDescription(Integer blogEntryId, EventType action, String objectType){
		String htmlStringDescription = null;
		if(objectType.equalsIgnoreCase(ObjectType.BLOG_ENTRY)){
			PnWeblogEntry pnWeblogEntry = ServiceFactory.getInstance().getPnWeblogEntryService().getWeblogEntryDetail(blogEntryId);
				String entryText = pnWeblogEntry.getText().replaceAll("\\<.*?>", "").replaceAll("/&[#]*[\\w|\\d]*;/g", "").replaceAll("&nbsp;", "");
				entryText = (entryText.length() > 150 ? entryText.substring(0, 150)+"..." : entryText);
				String entryTitle = null;
				if (pnWeblogEntry.getTitle() == null) {
					entryTitle = (entryText.length() > 40 ? entryText.substring(0, 40)+"..." : entryText);
				} else {
					entryTitle = (pnWeblogEntry.getTitle().length() > 40 ? pnWeblogEntry.getTitle().substring(0, 40)+"..." : pnWeblogEntry.getTitle());
				}
				String important = (pnWeblogEntry.getIsImportant() == 1 ? "!" : "");
				htmlStringDescription =  returnBlogEntryHtmlString(pnWeblogEntry.getStatus().equalsIgnoreCase("deleted"),
						entryTitle, SessionManager.getUser().getID(), action, new Date(), entryText, objectType, null,
		                important,  null);
				if(StringUtils.isNotEmpty(htmlStringDescription)){
					return htmlStringDescription;
				}
		}else if(objectType.equalsIgnoreCase(ObjectType.BLOG_COMMENT)){
			PnWeblogComment pnWeblogComment = ServiceFactory.getInstance().getPnWeblogCommentService().getWeblogCommentByCommentId(blogEntryId);
			String shortCommentContent = null;
			PnWeblogEntry pnWeblogEntry = null;
			if(pnWeblogComment != null){
				shortCommentContent = pnWeblogComment.getContent().replaceAll("\\<.*?>", "").replaceAll("/&[#]*[\\w|\\d]*;/g", "").replaceAll("&nbsp;", "");
				shortCommentContent = (shortCommentContent.length() > 150 ? shortCommentContent.substring(0, 150)+"..." : shortCommentContent);
				pnWeblogEntry = pnWeblogComment.getPnWeblogEntry();
			}
			if (pnWeblogEntry != null) {
				pnWeblogEntry = ServiceFactory.getInstance().getPnWeblogEntryService().getWeblogEntryDetail(pnWeblogEntry.getWeblogEntryId());
				PnPerson person = ServiceFactory.getInstance().getPnPersonService().getPersonNameById(pnWeblogEntry.getPnPerson().getPersonId());
				String important = (pnWeblogEntry.getIsImportant() == 1 ? "!" : "");
				htmlStringDescription =  returnBlogEntryHtmlString(
						pnWeblogEntry.getStatus().equalsIgnoreCase("deleted"),
						pnWeblogEntry.getTitle().replaceAll("\\<.*?>", "").replaceAll("/&[#]*[\\w|\\d]*;/g", "").replaceAll("&nbsp;", ""),
						SessionManager.getUser().getID(), action, new Date(), shortCommentContent, objectType, pnWeblogEntry,
		                important, person.getDisplayName());
				if(StringUtils.isNotEmpty(htmlStringDescription)){
					return htmlStringDescription;
				}
			}	
		}
		return null;
	}

	/**
	 * Get blog action
	 * Return blog action
	 * @param pnActivityLog
	 * @return
	 */
	public static String getBlogAction(String action) {
		String actionTaken = null;
		if (action.equalsIgnoreCase(EventType.EDITED.getText())) {
			actionTaken = "{$token$:prm.blog.activity.editedblogpost.message}";
		} else if (action.equalsIgnoreCase(EventType.DELETED.getText())) {
			actionTaken = "{$token$:prm.blog.activity.deletedblogpost.message}";
		} else if (action.equalsIgnoreCase(EventType.NEW.getText())) {
			actionTaken = "{$token$:prm.blog.activity.createdblogpost.message}";
		}
		return actionTaken;
	}
	
	/**
	 * Get wiki action
	 * @param action
	 * @return
	 */
	public static String getWikiAction(String action) {
		String actionTaken = null; 
		if(action.equalsIgnoreCase(EventType.NEW.getText())){
			actionTaken = "{$token$:prm.wiki.activity.createdwikipage.message}";
		}else if(action.equalsIgnoreCase(EventType.EDITED.getText())){
			actionTaken = "{$token$:prm.wiki.activity.modifiedwikipage.message}";
		}else if(action.equalsIgnoreCase(EventType.DELETED.getText())){
			actionTaken = "{$token$:prm.wiki.activity.deletedwikipage.message}";
		}else if(action.equalsIgnoreCase(EventType.IMAGE_UPLOADED.getText())){
			actionTaken = "{$token$:prm.wiki.activity.uploadedwikiimage.message}";
		}
		return actionTaken;
	}
	
	/**
	 * Get form data action
	 * @param action
	 * @return
	 */
	public static String getFormDataAction(String action, String formName) {
		String actionTaken = null;
		if(action.equalsIgnoreCase(EventType.NEW.getText())){
			actionTaken = "{$token$:prm.formrecord.activity.createdformrecord.message} " + (formName != null ? formName+" " : "");
		} else if(action.equalsIgnoreCase(EventType.EDITED.getText())){
			actionTaken = "{$token$:prm.formrecord.activity.editedformrecord.message} " + (formName != null ? formName+" " : "");
		}else if(action.equalsIgnoreCase(EventType.DELETED.getText())){
			actionTaken = "{$token$:prm.formrecord.activity.deletedformrecord.message} " + (formName != null ? formName+" " : "");
		}	
		return actionTaken;
	}
	
	/**
	 * Get News action
	 * Get activity action
	 * @return
	 */
	public static String getNewsAction(String action){
		String actionTaken = null;
		if(action.equalsIgnoreCase(EventType.NEW.getText())) {
			actionTaken = "{$token$:prm.news.activity.postednews.message}";
		}else if (action.equalsIgnoreCase(EventType.EDITED.getText())){
			actionTaken = "{$token$:prm.news.activity.editednews.message}";
		}else if (action.equalsIgnoreCase(EventType.DELETED.getText())){
			actionTaken = "{$token$:prm.news.activity.deletednews.message}";
		}
		return actionTaken;
	}
	
	/**
	 * Get Task action
	 * @param action
	 * @return
	 */
	public static String getTaskAction(String action) {
		String actionTaken = null;
		if (action.equalsIgnoreCase(EventType.NEW.getText())) {
			actionTaken = "{$token$:prm.task.activity.createdtask.message}";
		} else if (action.equalsIgnoreCase(EventType.DELETED.getText())) {
			actionTaken = "{$token$:prm.task.activity.removedtask.message}";
		} else if (action.equalsIgnoreCase(EventType.EDITED.getText())) {
			actionTaken = "{$token$:prm.task.activity.editedtask.message}";
		}
		return actionTaken;
	}
	
	/**
	 * Get Form action
	 * @param action
	 * @return
	 */
	public static String getFormAction(String action){
		String actionTaken = null;
		if (action.equalsIgnoreCase(EventType.NEW.getText())) {
			actionTaken = "{$token$:prm.form.activity.createdform.message}";
		} else if (action.equalsIgnoreCase(EventType.DELETED.getText())) {
			actionTaken = "{$token$:prm.form.activity.removedform.message}";
		} else if (action.equalsIgnoreCase(EventType.EDITED.getText())) {
			actionTaken = "{$token$:prm.form.activity.modifiedform.message}";
		}
		return actionTaken;
	}
	
	/**
	 * Create HTML string 
	 * @param isDisabled
	 * @param action
	 * @return
	 */
	public static String returnHtmlString(boolean isDisabled, String action){
		return returnHtmlString(isDisabled, action, null, null, false);
	}
	
	/**
	 * Create form data string 
	 * @param isDisabled
	 * @param action
	 * @param formNameWithSequenceNumber
	 * @return String
	 */
	public static String returnHtmlString(boolean isDisabled, String action, String formNameWithSequenceNumber, String creatorEmail, boolean isEafForm){
		//TODO: To remove first blank string parameter from all components 
		// and to shift parameter {0} to {1}, {1} to {2} and so on..
		StringBuffer buffer = new StringBuffer(1024);
		if(isEafForm && StringUtils.isNotEmpty(creatorEmail)){
			buffer.append(" <span class=\"{2}\"> <a href=\"mailto:"+creatorEmail+"\">"+creatorEmail+"</a> </span> <span class=\"{8}\">"+action+"</span>");
		}else {
			buffer.append(" <span class=\"{2}\"> <a href=\"{5}\">{6}</a> </span> <span class=\"{8}\">"+action+"</span>");
		}
		if(isDisabled){
			buffer.append(" <a id=\"disabled\" href=\"#\">"+(formNameWithSequenceNumber != null ? formNameWithSequenceNumber+" : " : "")+"{1}</a> ");
		}else{
			buffer.append(" <a id=\"objectLink_{9}\" class=\"{8}\" href=\"{4}\">"+(formNameWithSequenceNumber != null ? formNameWithSequenceNumber+" : " : "")+"{1}</a> ");
		}
		buffer.append("<span class=\"{3}\"> {$token$:prm.activity.at.message} {7} </span>");
		return buffer.toString();
	}
	
	/**
	 * Create html string for blog entry
	 * @param isDisabled
	 * @param entryTitle
	 * @param objectLink
	 * @param activityBy
	 * @param action
	 * @param currentDate
	 * @param entryText
	 * @return
	 */
	public static String returnBlogEntryHtmlString(boolean isDisabled, String entryTitle, String activityBy, EventType action, Date currentDate ,String entryText, String objectType, PnWeblogEntry pnWeblogEntry, String impMark, String blogPostedPersonName){
		//TODO: To remove blank string parameter from all components
		//and to shift parameter {0} to {1}, {1} to {2} and so on..
		StringBuffer buffer = new StringBuffer(1024);
		buffer.append("<img src=\"{0}\" width=\"16\" height=\"14\" title=\"{12}\" alt=\"{12}\"/>");
		buffer.append("<span class=\"{2}\"> <a href=\"{8}\">{9}</a> </span>");
		if(objectType.equalsIgnoreCase(ObjectType.BLOG_ENTRY)){
			buffer.append("<span class=\"{13}\">"+getBlogAction(action.getText())+"</span>");
			if(isDisabled){
				buffer.append("&nbsp;<a id=\"disabled\" href=\"#\"><font color=\"red\"><b>"+impMark+"</b></font>{4}</a> ");
			}else{
				buffer.append("&nbsp;<a id=\"objectLink_{14}\" class=\"{13}\" href=\"{7}\" title=\"{10}\"><font color=\"red\"><b>"+impMark+"</b></font>{4}</a> ");
			}
		}else if(objectType.equalsIgnoreCase(ObjectType.BLOG_COMMENT)){
			buffer.append("&nbsp;<span class=\"{13}\">{$token$:prm.blogcomment.activity.commentonblog.message}"+"</span>");
			if(isDisabled){
				buffer.append("&nbsp;<a id=\"disabled\" href=\"#\" title=\""+pnWeblogEntry.getTitle()+"\"><font color=\"red\"><b>"+impMark+"</b></font>"+(pnWeblogEntry.getTitle().length() > 40 ? pnWeblogEntry.getTitle().substring(0, 40)+"..." : pnWeblogEntry.getTitle())+"</a> ");
			}else{
				buffer.append("&nbsp;<a id=\"objectLink_{14}\" class=\"{13}\" href=\"{7}\" title=\""+pnWeblogEntry.getTitle()+"\"><font color=\"red\"><b>"+impMark+"</b></font>"+(pnWeblogEntry.getTitle().length() > 40 ? pnWeblogEntry.getTitle().substring(0, 40)+"..." : pnWeblogEntry.getTitle())+"</a> ");
			}
		}
		if(objectType.equalsIgnoreCase(ObjectType.BLOG_COMMENT)){
			buffer.append("<span class=\"{13}\">{$token$:prm.activity.by.message}"+"</span><span class=\"{2}\"><a class=\"{13}\" href=\"{10}/blog/view/" + pnWeblogEntry.getPnPerson().getPersonId() + "/" + pnWeblogEntry.getPnPerson().getPersonId() + "/person/"
				+ Module.PERSONAL_SPACE + "?module=" + Module.PERSONAL_SPACE+" \">" + blogPostedPersonName + "</a> </span>");
		}
		buffer.append(" <span class=\"{3}\"> {$token$:prm.activity.at.message} {11} </span></span>");
		buffer.append("<beginOfentryText>"+entryText);
		
		return buffer.toString();
	}
	
	/**
	 * Get document action 
	 * @param action
	 * @return
	 */
	public static String getDocumentAction(String action){
		String actionTaken = null;
		if (action.equalsIgnoreCase(EventType.NEW.getText())) {
			actionTaken = "{$token$:prm.document.activity.uploadeddocument.message}";
		} else if (action.equalsIgnoreCase(EventType.CHECKED_IN.getText())) {
			actionTaken = "{$token$:prm.document.activity.checkedindocument.message}";
		} else if (action.equalsIgnoreCase(EventType.CHECKED_OUT.getText())) {
			actionTaken = "{$token$:prm.document.activity.checkedoutdocument.message}";
		} else if (action.equalsIgnoreCase(EventType.VIEWED.getText())) {
			actionTaken = "{$token$:prm.document.activity.vieweddocument.message}";
		} else if (action.equalsIgnoreCase(EventType.EDITED.getText())) {
			actionTaken = "{$token$:prm.document.activity.modifieddocument.message}";
		} else if (action.equalsIgnoreCase(EventType.DELETED.getText())) {
			actionTaken = "{$token$:prm.document.activity.removeddocument.message}";
		} else if (action.equalsIgnoreCase(EventType.UNDO_CHECKED_OUT.getText())) {
			actionTaken = "{$token$:prm.document.activity.undocheckedout.message}";
		} else if (action.equalsIgnoreCase(EventType.FOLDER_CREATED.getText())) {
			actionTaken = "{$token$:prm.document.activity.createdfolder.message}";
		} else if (action.equalsIgnoreCase(EventType.FOLDER_DELETED.getText())) {
			actionTaken = "{$token$:prm.document.activity.removedfolder.message}";
		}else if (action.equalsIgnoreCase(EventType.MOVED.getText())) {
			actionTaken = "{$token$:prm.document.activity.moveddocument.message}";
		}	
		return actionTaken;
	}
	
	/**
	 * Get object link for description
	 * @param objectId
	 * @param objectType
	 * @return
	 */
	public static String objectLinkForDescription(String objectId, String objectType){
		return URLFactory.makeURL(objectId, objectType);
	}
	
	/**
	 * Get Person Image
	 * @param activityBy
	 * @return
	 */
	public static String getPersonImage(String activityBy){
		PnPerson pnPerson = ServiceFactory.getInstance().getPnPersonService().getPesronNameAndImageIdByPersonId(Integer.parseInt(activityBy));
		 if(pnPerson.getImageId()!=null)
			 return SessionManager.getJSPRootURL()+"/servlet/photo?id="+pnPerson.getPersonId()+"&amp;size=thumbnail"+Module.PERSONAL_SPACE;
		 else
			 return SessionManager.getJSPRootURL()+"/images/NoPicture.gif";
	 }
	
	public static List<PnActivityLog> getSortedActivities(List<PnActivityLog> entries){
		Collections.sort(entries, new Comparator<PnActivityLog>(){
			/**
			 * Implementing compare method for PnActivityLog objects
			 * 
			 * @param entry1
			 * @param entry2
			 * @return integer value 1 or -1
			 */
			public int compare(PnActivityLog entry1, PnActivityLog entry2) {
		        return  - entry1.getActivityLogId().compareTo(entry2.getActivityLogId());
			}
		});
		return entries;
	}

	/**
	 * Get date string 
	 * @param date
	 * @return date string 
	 */
	public static String getDisplayDate(Date date){
		String dateToDisplay = "";
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(getFormattedDate(null, new Date()));
		Calendar dateToCheck = Calendar.getInstance();
		dateToCheck.setTime(date);
		if (currentDate.get(Calendar.DATE) == dateToCheck.get(dateToCheck.DATE)
				&& currentDate.get(Calendar.MONTH) == dateToCheck.get(dateToCheck.MONTH)
				&& currentDate.get(Calendar.YEAR) == dateToCheck.get(dateToCheck.YEAR)) {
			dateToDisplay = PropertyProvider.get("prm.global.dayslabel.today");
			
		} else if (currentDate.get(Calendar.DATE) - 1 == dateToCheck.get(dateToCheck.DATE)
				&& currentDate.get(Calendar.MONTH) == dateToCheck.get(dateToCheck.MONTH)
				&& currentDate.get(Calendar.YEAR) == dateToCheck.get(dateToCheck.YEAR)) {
			dateToDisplay = PropertyProvider.get("prm.global.dayslabel.yesterday");
		} else if (currentDate.get(Calendar.YEAR) == dateToCheck.get(dateToCheck.YEAR)){
			dateToDisplay = new SimpleDateFormat("MMM d").format(date);
		} else {
			dateToDisplay = new SimpleDateFormat("MMM d, yyyy").format(date);
		}
		return dateToDisplay;
	}
	
	/**
	 * Return whether document has system container or not
	 * @param documentId
	 * @return boolean
	 */
	public static boolean isDocumentHasSystemContainer(Integer documentId){
		PnDocContainer pnDocContainer =  ServiceFactory.getInstance().getPnDocContainerService().getDocContainerWithIsHidden(documentId);
		return pnDocContainer != null ? pnDocContainer.getIsHidden() == 1 : false;
	}
	
	/**
	 * Parse date as per user's timezone
	 * @param PnActivityLog activityLog
	 * @param java.util.Date dateToFormat
	 * @return java.util.Date object
	 */
	public static Date getFormattedDate(PnActivityLog activityLog, Date dateToFormat){
		Date date = null;
		try {
			 date = new SimpleDateFormat("dd/MM/yy").parse(DateFormat.getInstance().formatDate(dateToFormat == null ? new Date(activityLog.getActivityOnDate().getTime()) : dateToFormat, "dd/MM/yy"));
		} catch (ParseException e) {
			Logger.getLogger(ActivityLogManager.class).error("Error occurred while formatting date : " + e.getMessage());
		}
		return date;
	}
	
	/**
	 * Get description by replacing token value if token present in description
	 * @param activity description
	 * @return token replaced string
	 */
	public static String getTokenReplacedDescription(String description) {
		if(description.contains("{$token$:")){
			String stringToReplace = description.substring(description.indexOf("{$token$:"), description.indexOf("}", description.indexOf("{$token$:"))+1);
			description = description.replace(stringToReplace, PropertyProvider.get(stringToReplace.substring(stringToReplace.indexOf(":")+1, stringToReplace.indexOf("}"))));
			return getTokenReplacedDescription(description);
		} else {
			return description;
		}
	}
}
