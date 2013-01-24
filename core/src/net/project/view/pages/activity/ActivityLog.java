package net.project.view.pages.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import net.project.activity.ActivityLogManager;
import net.project.activity.ActivityRssFeedManager;
import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.URLFactory;
import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.hibernate.model.PnActivityLog;
import net.project.hibernate.model.PnActivityLogMarked;
import net.project.hibernate.model.PnActivityLogMarkedPK;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.service.IPnActivityLogMarkedService;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.InvalidDateException;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.PrimaryKeyEncoder;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.util.TextStreamResponse;

public class ActivityLog extends BasePage{

	@Property
	private List<PnActivityLog> activityLogList;
	
	private Map result;

	@Property
	@Persist
	private List<ActivityLogMap> formattedActivityList;

	@Persist
	private String comboVal;

	@Property
	private PnPerson person;

	@Persist
	private String filterCriteriaValue;

	@Persist
	private Integer teamMember;

	@Property
	private ActivityLogMap currentDate;

	@Persist
	private List<String> filterCriteriaValues;

	@Property
	private String spaceId;

	private Integer range;
	
	@Property
	private String startDateString;
	
	@Property
	private String endDateString;
	
	@Property
	private PnActivityLog pnActivityLog;
	
	@Property
	private int activitiesCounter; 
	
	@Property
	private String encryptedValue;
	
	private String rssFilterValues;
	
	@Persist
	private String encryptedSpaceId;
	
	@Property
	private String activityIdsPerPage;
	
	@Property
	private String unMarkedToken;
	
	@Property
	private String markedToken;
	
	@Property
	private Date continuedDate;
	
	@Property
	private String continuedDateMessage;
	
	@Property
	private String blogActivityIds;
	
	@InjectPage
	@Property
	private View view;
	
	private enum ActivityActions {
			MARKED, CLEAR_ALL, MARKED_BY_PERSON, LOAD_ACTIVITIES, 
			CHECK_BLOG, BLOG_WITH_COMMENT, CHECK_AND_REDIRECT;
			
			public static ActivityActions get( String v ) {
	            try {
	                return ActivityActions.valueOf( v.toUpperCase() );
	            } catch( Exception ex ) { }
	            return null;
	         }
	}
	
	Object onActivate(String action) {
		range = 31;
		unMarkedToken =  PropertyProvider.get("prm.project.activity.unmarked.label");
		markedToken =  PropertyProvider.get("prm.project.activity.marked.label");
		ActivityActions activityAction = ActivityActions.get( action );
		if(StringUtils.isNotEmpty(action)){
			IPnActivityLogMarkedService pnActivityLogMarkedService = ServiceFactory.getInstance().getPnActivityLogMarkedService();
			if (activityAction == ActivityActions.MARKED) {
				try {
					if (StringUtils.isNotEmpty(getRequest().getParameter("isMarked"))
							&& StringUtils.isNotEmpty(getRequest().getParameter("activityId"))
							&& Integer.parseInt(getRequest().getParameter("isMarked")) == 1) {
						PnActivityLogMarked pnActivityLogMarked = new PnActivityLogMarked(new PnActivityLogMarkedPK(
								new Integer(getRequest().getParameter("activityId")), new Integer(SessionManager
										.getUser().getID())), 1);
						pnActivityLogMarkedService.save(pnActivityLogMarked);
					}else {
						PnActivityLogMarked pnActivityLogMarked = new PnActivityLogMarked(new PnActivityLogMarkedPK(
								new Integer(getRequest().getParameter("activityId")), new Integer(SessionManager
										.getUser().getID())), 1);
						pnActivityLogMarkedService.delete(pnActivityLogMarked);
					}
					return new TextStreamResponse("text/plain", "true");
				} catch (Exception e) {
					Logger.getLogger(ActivityLog.class)
							.error("Error while saving or deleting marking of activity " + e);
				}
			} else if (activityAction == ActivityActions.CLEAR_ALL) {
				try {
					if(StringUtils.isNotEmpty(getRequest().getParameter("activityIds"))){
						List<Integer> ids = new ArrayList<Integer>();
						for(String id: getRequest().getParameter("activityIds").split(",")){
							ids.add(new Integer(id));
						}
						pnActivityLogMarkedService.deleteByActivityIds(ids);
					}
					return new TextStreamResponse("text/plain", "true");
				} catch (Exception e) {
					Logger.getLogger(ActivityLog.class).error("Error while deleting all marking of activities " + e);
				}
			} else if (activityAction == ActivityActions.MARKED_BY_PERSON) {
				Date jumpedDate = null;
				try {
					List<Integer> activityIdsPerPage = new ArrayList<Integer>();
					if(StringUtils.isNotEmpty(getRequest().getParameter("activityIdsPerPage"))){
						for(String id: getRequest().getParameter("activityIdsPerPage").split(",")){
							activityIdsPerPage.add(Integer.valueOf(id));
						}
					}
					if (StringUtils.isNotEmpty(getRequest().getParameter("jumpToDate"))){
						jumpedDate = DateFormat.getInstance().parseDateString(getRequest().getParameter("jumpToDate"),"dd/MM/yy");
					}
					return new TextStreamResponse("text/plain", getMarkedActivityIds(jumpedDate, null, activityIdsPerPage));
				} catch (InvalidDateException de) {
					Logger.getLogger(ActivityLog.class).error("parse date exception occured while getting marked activity ids " + de);
				} catch (Exception e) {
					Logger.getLogger(ActivityLog.class).error("Error occurred while getting marked activity ids " + e);
				}				
			} else if (activityAction == ActivityActions.LOAD_ACTIVITIES) {
				Date jumpedDate = null;
				try {
					if (StringUtils.isNotEmpty(getRequest().getParameter("filterValues"))) {
						setFiltervalues(getRequest().getParameter("filterValues"));
					}
					if (StringUtils.isNotEmpty(getRequest().getParameter("jumpToDate")))
						jumpedDate = DateFormat.getInstance().parseDateString(getRequest().getParameter("jumpToDate"),"dd/MM/yy");
					else
						view.setPersistedJumpedDate(null);
					setResult(setActivityList(jumpedDate, null, new Integer(getRequest().getParameter("offset"))));
					formattedActivityList = getActivityLog(getResult());
					encryptedValue = encryptedSpaceId + "/" + rssFilterValues;
				} catch (InvalidDateException de) {
					Logger.getLogger(ActivityLog.class).error("parse date exception occured while getting activities " + de);
				} catch (Exception e) {
					Logger.getLogger(ActivityLog.class).error("Error occurred while getting activities " + e);
				}
			} else if (activityAction == ActivityActions.CHECK_BLOG) {
				try {
					if (StringUtils.isNotEmpty(getRequest().getParameter("weblogEntryId"))) {
						if(ServiceFactory.getInstance().getPnWeblogEntryService().isWeblogEntryDeleted(Integer.parseInt(getRequest().getParameter("weblogEntryId"))))
							return new TextStreamResponse("text/plain", "true");
					}	
				} catch (Exception e) {
					Logger.getLogger(ActivityLog.class).error("Error occurred while checking whether blog entry deleted or not " + e);
				}
			} else if (activityAction == ActivityActions.BLOG_WITH_COMMENT) {
				try {
					List<Integer> activityIds = new ArrayList<Integer>();
					if(StringUtils.isNotEmpty(getRequest().getParameter("blogActivityIds"))){
						for(String id: getRequest().getParameter("blogActivityIds").split(",")){
							activityIds.add(Integer.valueOf(id));
						}
						return new TextStreamResponse("text/plain", getPnActivityLogService().getActivityIdsOfBlogHavingComment(activityIds));
					}
				} catch (Exception e) {
					Logger.getLogger(ActivityLog.class).error("Error occurred while getting activity ids of blog having comments: " + e.getMessage());
					return new TextStreamResponse("text/plain", "false");
				}
			} else if (activityAction == ActivityActions.CHECK_AND_REDIRECT) {
				String objectId = getRequest().getParameter("objectId");
				String objectType = getRequest().getParameter("objectType");
				String objectName = getRequest().getParameter("objectName");
				String parentObjectId = getRequest().getParameter(
						"parentObjectId");
				try {
					if (StringUtils.isNotEmpty(objectName)
							&& StringUtils.isNotEmpty(parentObjectId)
							&& StringUtils.isNotEmpty(objectId)
							&& StringUtils.isNotEmpty(objectType)){
						return new TextStreamResponse("text/plain", showObject(
								objectId, objectType, objectName,
								parentObjectId));
					}
					if (StringUtils.isNotEmpty(objectId)
							&& StringUtils.isNotEmpty(objectType)){
						return new TextStreamResponse("text/plain", showObject(
								objectId, objectType));
					} else{
						return new TextStreamResponse("text/plain", "false");
					}
				} catch (Exception e) {
					Logger.getLogger(ActivityLog.class).error(
							"Error occurred while showing object page: "
									+ e.getMessage());
				}
			}
		}
		return null;
	}
	
	/**
	 * @param value
	 */
	public void setFiltervalues(String value) {
		filterCriteriaValue = value;
		view.setFilterCriteriaValue(filterCriteriaValue);
		String[] criteriaValues = value.split(",");
		rssFilterValues = "";
		filterCriteriaValues = new ArrayList<String>();
		for (String criteriavalue : criteriaValues) {
				criteriavalue = criteriavalue.replace("blog_entry-commented",
				"blog_comment-commented").replace("document-folder_created", "doc_container-folder_created")
				.replace("document-folder_deleted", "doc_container-folder_deleted");
				filterCriteriaValues.add(criteriavalue);
				
				if (criteriavalue.contains("-")) {
					if (!rssFilterValues.contains(criteriavalue.substring(0, criteriavalue.indexOf("-")))) {
						if ((criteriavalue.contains("form_data") || criteriavalue.contains("form"))
								&& (!rssFilterValues.contains("form"))) {
							rssFilterValues += "form,";							
						} else if(!(criteriavalue.contains("form_data")	|| criteriavalue.contains("form"))){
							rssFilterValues += criteriavalue.substring(0, criteriavalue.indexOf("-")) + ",";	
						}						
					}
			}
		}
		rssFilterValues = rssFilterValues.length() > 0 ? rssFilterValues.substring(0,rssFilterValues.length()-1) : rssFilterValues;
		teamMember = new Integer(filterCriteriaValue.substring(0, filterCriteriaValue.indexOf(",")));
		view.setPerson(new PnPerson(teamMember));
	}
	
	public List<ActivityLogMap> getActivityLog(Map result) {
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
				formattedDateList.add(currPg); // add formatted PnActivityLog to formatted list
			}
			formattedActivityList.add(new ActivityLogMap((Date) entry.getKey(), formattedDateList));
		}
		return formattedActivityList;
	}

	/* added for Map looping component */
	private final PrimaryKeyEncoder<Date, ActivityLogMap> mapEncoder = new PrimaryKeyEncoder<Date, ActivityLogMap>() {
		public Date toKey(ActivityLogMap value) {
			return value.getKey();
		}

		public void prepareForKeys(List<Date> keys) {
		}

		public ActivityLogMap toValue(Date key) {
			return activityIndexLoopByKey(key);
		}

		public Class<Date> getKeyType() {
			return null;
		}
	};

	public PrimaryKeyEncoder getMapEncoder() {
		return mapEncoder;
	}

	/* added for List looping component */
	private final PrimaryKeyEncoder<Integer, PnActivityLog> listEncoder = new PrimaryKeyEncoder<Integer, PnActivityLog>() {
		public Integer toKey(PnActivityLog value) {
			return value.getActivityLogId();
		}

		public void prepareForKeys(List<Integer> keys) {
		}

		public PnActivityLog toValue(Integer key) {
			PnActivityLog pnActivityLog = getPnActivityLogService().get(key);
			return pnActivityLog;
		}

		public Class<Integer> getKeyType() {
			return null;
		}
	};

	public PrimaryKeyEncoder getListEncoder() {
		return listEncoder;
	}

	/* helper method for getting List element by key */
	private ActivityLogMap activityIndexLoopByKey(Date key) {
		ActivityLogMap result = null;

		Iterator it = formattedActivityList.iterator(); // use returned index (found above) and iterate through it to
														// find element with specified key
		while (it.hasNext()) {
			ActivityLogMap currentDate = (ActivityLogMap) it.next();
			if (currentDate.getKey().equals(key)) { // if element with specified key is found - store it in result
				result = new ActivityLogMap(currentDate.getKey(), currentDate.getValues());
				break;
			}
		}
		return result;
	}

	public Map setActivityList(Date sDate, Date eDate, Integer offSet) {
		Date date = null;
		Integer currentUserId = Integer.parseInt(SessionManager.getUser().getID());
		offSet = (offSet != 0 && offSet != 30) ? offSet -1 : offSet;
		range =  offSet <= 29 ? range-1 : range;
		boolean isSelectedDatePresent = true;
		Calendar currentDate = Calendar.getInstance();
		Calendar dateToCheck = Calendar.getInstance();
		Map<Date, List<PnActivityLog>> tMap = new TreeMap<Date, List<PnActivityLog>>(new Comparator() {
			public int compare(Object object1, Object object2) {
				Date date1 = (Date) object1;
				Date date2 = (Date) object2;
				return date1.compareTo(date2);
			}
		});
    	activityLogList = new ArrayList<PnActivityLog>();
    	List<PnActivityLog> formListLog = new ArrayList<PnActivityLog>();
    	if (this.teamMember != null && teamMember > 0)
    		person = new PnPerson(this.teamMember);
    	
    	if (StringUtils.isEmpty(filterCriteriaValue)) {
			activityLogList = getPnActivityLogService().getActivityLogBySpaceIdAndDate(
				Integer.parseInt(SessionManager.getUser().getCurrentSpace().getID()),
				null, null, null,
				person != null && person.getPersonId() != 0 ? person.getPersonId() : null, offSet, range, currentUserId);
		} else {
			try {
				activityLogList = filterCriteriaValues != null && filterCriteriaValues.size() > 1 ? getPnActivityLogService()
						.getActivityLogBySpaceIdAndDate(
							Integer.parseInt(SessionManager.getUser().getCurrentSpace().getID()),
							sDate != null ?	sDate
									: ActivityLogManager.getTimeChangedDate(new PnCalendar().getTime(), 11, 59, 59, 1),
							eDate != null ?	ActivityLogManager.getTimeChangedDate(eDate, 11, 59, 59, 1) : null,
							ActivityLogManager.getFormattedCriteria(filterCriteriaValues),
							person != null && person.getPersonId() != 0 ? person.getPersonId() : null, offSet, range, currentUserId)
					: new ArrayList<PnActivityLog>();
			} catch (Exception e) {
				Logger.getLogger(ActivityLog.class).error("Error occured while getting activities using filer criteria" + e);
			}
		}

		if (CollectionUtils.isNotEmpty(activityLogList)) {
			Iterator it = activityLogList.iterator();
			activityIdsPerPage = "";
			blogActivityIds = "";
			continuedDateMessage = "";
			int activityCounter = 0;
			Calendar contDate = null;
			if(sDate != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(sDate);
				cal.add(Calendar.DATE, -1);
				view.setPersistedJumpedDate(cal.getTime());
				sDate = ActivityLogManager.getFormattedDate(null, cal.getTime());
				startDateString = ActivityLogManager.getDisplayDate(sDate);
			}
			while (it.hasNext()) {
				PnActivityLog currLog = (PnActivityLog) it.next();
				date = ActivityLogManager.getFormattedDate(currLog, null);
				activityIdsPerPage += currLog.getActivityLogId() + ",";
				if(currLog.getTargetObjectType().equalsIgnoreCase(ObjectType.BLOG_ENTRY))
					blogActivityIds += currLog.getTargetObjectId() + ",";
				if(offSet > 0 && activityCounter == 0){
					contDate = Calendar.getInstance();
					contDate.setTime(date);
					activityCounter++;
					continue;
				}
				currentDate.setTime(date);
				if (activityCounter == 1 && contDate != null && currentDate.get(Calendar.DATE) == contDate.get(contDate.DATE)
						&& currentDate.get(Calendar.MONTH) == contDate.get(contDate.MONTH)
						&& currentDate.get(Calendar.YEAR) == contDate.get(contDate.YEAR)) {
					continuedDate = contDate.getTime();
				}
				dateToCheck.setTime(sDate != null ? sDate : new PnCalendar().getTime());
				if (currentDate.get(Calendar.DATE) == dateToCheck.get(dateToCheck.DATE)
						&& currentDate.get(Calendar.MONTH) == dateToCheck.get(dateToCheck.MONTH)
						&& currentDate.get(Calendar.YEAR) == dateToCheck.get(dateToCheck.YEAR)) {
					isSelectedDatePresent = false;
				}
				if (tMap.containsKey(date)) {
					List<PnActivityLog> list = tMap.get(date);
					list.add(currLog);
					tMap.put(date, list);
				} else {
					// there is no key for this letter in the map
					List<PnActivityLog> list = new ArrayList<PnActivityLog>();
					list.add(currLog);
					tMap.put(date, list);
				}
				if(activityCounter == range-1){
					dateToCheck.setTime(date);
					if( currentDate.get(Calendar.DATE) == dateToCheck.get(dateToCheck.DATE)
							&& currentDate.get(Calendar.MONTH) == dateToCheck.get(dateToCheck.MONTH)
							&& currentDate.get(Calendar.YEAR) == dateToCheck.get(dateToCheck.YEAR)){ 
						continuedDateMessage = PropertyProvider.get("prm.activity.continueddatemessage.message", ActivityLogManager.getDisplayDate(dateToCheck.getTime()));
					}
				}
				activityCounter++;
			}
			activityIdsPerPage = activityIdsPerPage.length() > 0 ? activityIdsPerPage.substring(0,activityIdsPerPage.length()-1) : activityIdsPerPage;
			if (isSelectedDatePresent && sDate != null && !(offSet >= 29)) {
				List<PnActivityLog> list = new ArrayList<PnActivityLog>();
				list.add(new PnActivityLog());
				tMap.put(sDate, list);
			}
			if(startDateString == null)
				startDateString = ActivityLogManager.getDisplayDate(ActivityLogManager.getFormattedDate(activityLogList.get(0), null));
			endDateString = ActivityLogManager.getDisplayDate(ActivityLogManager.getFormattedDate(activityLogList.get(activityLogList.size()-1), null));
			activitiesCounter = activityLogList.size();
		}
		return tMap;
	}
	
	/**
	 * @return the result
	 */
	public Map getResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(Map result) {
		this.result = result;
	}
	
	/**
	 * @param sDate
	 * @param eDate
	 * @param activityIdsPerPage
	 * @return marked activity ids
	 */
	public String getMarkedActivityIds(Date sDate, Date eDate, List<Integer> activityIdsPerPage) {
		
		IPnActivityLogMarkedService pnActivityLogMarkedService = ServiceFactory.getInstance()
				.getPnActivityLogMarkedService();
		String activityIds = activityIdsPerPage != null && activityIdsPerPage.size() != 0 ? pnActivityLogMarkedService.getMarkedByPersonId(new Integer(SessionManager.getUser().getID()),
															sDate != null ? sDate : ActivityLogManager.getTimeChangedDate(new PnCalendar().getTime(), 11, 59, 59, 1),
															eDate != null ? ActivityLogManager.getTimeChangedDate(eDate, 11, 59, 59, 1) : null, activityIdsPerPage) : "";
														
		return activityIds;
	}

	/**
	 * @return the encryptedSpaceId
	 */
	public String getEncryptedSpaceId() {
		return encryptedSpaceId;
	}

	/**
	 * @param encryptedSpaceId the encryptedSpaceId to set
	 */
	public void setEncryptedSpaceId(String encryptedSpaceId) {
		this.encryptedSpaceId = encryptedSpaceId;
	}
	
	/**
	 * Method for check object is deleted or not , called on actionlink url
	 * @param objectId
	 * @param objectType
	 * @return
	 */
	String showObject(String objectId, String objectType){
		PnActivityLog pnActivityLog = new PnActivityLog();
		pnActivityLog.setTargetObjectId(Integer.parseInt(objectId));
			if(objectType.equalsIgnoreCase(ObjectType.DOCUMENT) && !ActivityRssFeedManager.isDocumentDeleted(Integer.parseInt(objectId))){
				return makeURLString(objectId, objectType);
			} else if(objectType.equalsIgnoreCase(ObjectType.TASK) && !ActivityRssFeedManager.isTaskDeleted(pnActivityLog)) {
				return makeURLString(objectId, objectType);
			} else if(objectType.equalsIgnoreCase(ObjectType.NEWS) && !ActivityRssFeedManager.isNewsDeleted(pnActivityLog)) {
				return makeURLString(objectId, objectType);
			} else if(objectType.equalsIgnoreCase(ObjectType.FORM) && !ActivityRssFeedManager.isFormDeleted(pnActivityLog)) {
				return makeURLString(objectId, objectType);
			} else if(objectType.equalsIgnoreCase(ObjectType.FORM_DATA) && !ActivityRssFeedManager.isFormDataDeleted(pnActivityLog)) {
				return makeURLString(objectId, objectType);
			} else if(objectType.equalsIgnoreCase(ObjectType.BLOG_ENTRY) || objectType.equalsIgnoreCase(ObjectType.BLOG_COMMENT)){
				return ActivityRssFeedManager.checkBlogEntryDeleted(objectId, objectType);
			} else if(objectType.equalsIgnoreCase(ObjectType.CONTAINER) && !ActivityRssFeedManager.isDocumentDeleted(Integer.parseInt(objectId))){
				return makeURLString(objectId, objectType);
			} else if(objectType.equalsIgnoreCase(ObjectType.PROJECT) && !ActivityRssFeedManager.isProjectDeleted(pnActivityLog)){
				return SessionManager.getAppURL() + "/project/Dashboard/?module=" + Module.PROJECT_SPACE+"&amp;id=" + objectId;			
			} else {
				if(objectType.equalsIgnoreCase(ObjectType.FORM) && ActivityRssFeedManager.isFormHidden(pnActivityLog))
					return "hidden";
				else
					return "deleted";
			}
	}
	
	/**
	 * Method for making URL string for object, If user not allowed access then to return accessDenied message 
	 * @param objectId
	 * @param objectType
	 * @return String
	 */
	private String makeURLString(String objectId, String objectType) {
		if((objectType.equalsIgnoreCase(ObjectType.FORM_DATA) || objectType.equalsIgnoreCase(ObjectType.FORM_DATA)) 
			&& !isAccessAllowed(SessionManager.getUser().getCurrentSpace(), Module.FORM, net.project.security.Action.CREATE, SessionManager.getUser()))
			return "accessDenied";
		else
			return SessionManager.getSiteURL()+URLFactory.makeURL(objectId, objectType).replaceAll(URLFactory.AMP, URLFactory.URLAMP);
	}
	/**
	 * Method for check object is deleted or not , called on actionlink url
	 * @param objectId
	 * @param objectType
	 * @param objectName
	 * @param parentObjectId
	 * @return object
	 */
	String showObject(String objectId, String objectType, String objectName, String parentObjectId) {
		PnActivityLog pnActivityLog = new PnActivityLog();
		pnActivityLog.setTargetObjectId(Integer.parseInt(objectId));
		pnActivityLog.setObjectName(objectName);
		if(StringUtils.isNotEmpty(parentObjectId) && !parentObjectId.equals("null"))
			pnActivityLog.setParentObjectId(Integer.parseInt(parentObjectId));
		if(StringUtils.isNotEmpty(objectType) && objectType.equalsIgnoreCase(ObjectType.WIKI))
			return ActivityRssFeedManager.getWikiPageURL(pnActivityLog, "activity");
		 else 
			return "deleted";
	}
}
