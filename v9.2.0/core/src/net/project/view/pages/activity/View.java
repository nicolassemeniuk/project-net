/**
 * 
 */
package net.project.view.pages.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.project.activity.ActivityLogManager;
import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.property.PropertyProvider;
import net.project.events.EventType;
import net.project.hibernate.model.PnActivityLog;
import net.project.hibernate.model.PnClass;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.service.IPnClassService;
import net.project.hibernate.service.ServiceFactory;
import net.project.project.ProjectSpaceBean;
import net.project.security.SessionManager;
import net.project.space.SpaceTypes;
import net.project.util.DateFormat;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;
import net.project.view.pages.resource.management.GenericSelectModel;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;

/**
 *
 */
public class View extends BasePage {

	private static Logger log;

	@Persist
	@Property
	private List<Integer> dateNumList;

	@Property
	private List<PnActivityLog> activityLogList;

	@Property
	private PnActivityLog pnActivityLog;

	@Property
	private List<ActivityObject> filterObjects;

	@Property
	private ActivityObject activityObj;

	@Property
	private String filterCriteria;

	@Property
	private String filterValues;

	@Property
	private ActivityType activityType;

	@Property
	private List<ActivityLogMap> formattedActivityList;

	@Property
	private ActivityLogMap currentDate;

	@Persist
	private String comboVal;

	@Persist
	private PnPerson person;

	@Property
	private String personValue;

	@Inject
	private PropertyAccess access;

	private GenericSelectModel<PnPerson> teamBeans;

	@Persist
	private Integer teamMember;

	@Persist
	private Calendar lastDate;

	@Persist
	private Calendar firstDate;

	@Persist
	private Date lDate;

	@Persist
	private Date fDate;

	@Property
	private String startDateString;

	@Property
	private String projectName;

	@Property
	private String endDateString;

	private DateFormat userDateFormat;

	private Date currentday;

	@Property
	private boolean blogEnabled;

	@Property
	private boolean actionsIconEnabled;

	@Property
	private boolean isPrevious = false;

	@Property
	private boolean isNext = false;

	@Persist
	private boolean isAllow;

	@Persist
	private boolean setDate;

	@Persist
	private Date tempDate;

	private String activitiesFromToDate;

	@Property
	private String selectOneMessage;

	@Property
	private String loadingMessage;

	private IPnClassService pnClassService;

	@Property
	private List<ActivityObject> formListObjects;

	@Persist
	private List<String> formListValues;

	@Persist
	private List<String> filterCriteriaValues;

	@Property
	private Integer spaceId;

	private String encSpaceId;
	
	@Property
	private String encryptdays;
	
	@Property
	private String encryptFilfilterCriteriaValues;
	
	@Property
	private Integer blogModuleId;
	
	@Property
	private String rssValue;
	
	@Persist
	private Integer offSet;
	
	private final static Integer range = 30;
	
	@Property
	private String jumpToDate;
	
	@Property
	private String dateFormat;
	
	@Persist
	private boolean flag;
	
	@Property
	private String rssMouseOverImagePath;
	
	@Property
	private String rssMouseOutImagePath;
	
	@InjectPage
	@Property
	private ActivityLog activityLog;

	@Property
	private String rssToolTipMessage;
	
	@Property
	private ArrayList<ActivityObject> blogObjectList;

	@Property
	private ArrayList<ActivityObject> wikiAndDocumentObjectList;

	@Property
	private ArrayList<ActivityObject> formObjectList;

	@Property
	private ArrayList<ActivityObject> workplanAndNewsObjectList;
	
	@Persist
	private String filterCriteriaValue;
	
	private String jumpedDate;
	
	@Persist
	private Date persistedJumpedDate;
	
	@Persist
	private Integer oldSpaceId;
	
	@Property
	private String applyButton;
	
	public enum ActivityObjects {

		BLOG("blog_entry"), WIKI("wiki"), DOCUMENTS("document"), DISCUSSIONS("discussion"), FORMS("form"), WORKPLAN(
				"task"), NEWS("news"), MARKED("marked");

		private ActivityObjects(String value) {
			this.value = value;
		}

		private String value;

		public static List<ActivityObjects> getActivityObjects() {
			List<ActivityObjects> objects = new ArrayList<ActivityObjects>();
			objects.add(BLOG);
			objects.add(WIKI);
			objects.add(DOCUMENTS);
			// objects.add(DISCUSSIONS);
			objects.add(FORMS);
			objects.add(WORKPLAN);
			objects.add(NEWS);
			objects.add(MARKED);
			return objects;
		}

		@Override
		public String toString() {
			// only capitalize the first letter
			return StringUtils.capitalize(super.toString().toLowerCase());
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	Object onActivate() {
		if(checkForUser() != null) {
			return checkForUser();
		}	
		if (StringUtils.isNotEmpty(getRequest().getParameter("id"))) {
			spaceId = Integer.valueOf(getRequest().getParameter("id"));
		} else {
			spaceId = Integer.valueOf(SessionManager.getUser().getCurrentSpace().getID());
		}
		if(oldSpaceId == null && spaceId != null) {
			oldSpaceId = spaceId;
		}	
		setJumpedDate(getPersistedJumpedDate() != null ? DateFormat.getInstance().formatDate(getPersistedJumpedDate()) : "");
		if(oldSpaceId != null && spaceId != null && !oldSpaceId.equals(spaceId)) {
			oldSpaceId = spaceId;
			setJumpedDate("");
			setFilterCriteriaValue("");
			setPerson(new PnPerson(0));
		}
		String filterCriteriaForRSS = "";
		Object isValidAccess;
		isValidAccess = checkForUser();
		if (isValidAccess != null) {
			return isValidAccess;
		}
		if(offSet == null){
			offSet = 0;
		}
		dateFormat = DateFormat.getInstance().getDateFormatExample();
		log = Logger.getLogger(View.class);
		actionsIconEnabled = PropertyProvider.getBoolean("prm.global.actions.icon.isenabled");
        blogEnabled = PropertyProvider.getBoolean("prm.blog.isenabled")
				&& getUser().getCurrentSpace().isTypeOf(SpaceTypes.PERSONAL_SPACE)
				|| getUser().getCurrentSpace().isTypeOf(SpaceTypes.PROJECT_SPACE);
		selectOneMessage = PropertyProvider.get("prm.project.activity.select.message");
		loadingMessage = PropertyProvider.get("prm.project.activity.loading.message");
		rssToolTipMessage = PropertyProvider.get("prm.project.activity.rsstitle");
		rssMouseOverImagePath = getJSPRootURL()+""+PropertyProvider.get("prm.project.activity.rssmouseoverimagepath");
		rssMouseOutImagePath = getJSPRootURL()+""+PropertyProvider.get("prm.project.activity.rssmouseoutimagepath");
		applyButton = PropertyProvider.get("prm.activity.button.apply.message");
		userDateFormat = SessionManager.getUser().getDateFormatter();
		projectName = SessionManager.getUser().getCurrentSpace().getName();
		blogModuleId = Module.BLOG;
		filterObjects = new ArrayList<ActivityObject>();
		blogObjectList =  new ArrayList<ActivityObject>();
		wikiAndDocumentObjectList = new ArrayList<ActivityObject>();
		formObjectList = new ArrayList<ActivityObject>();
		workplanAndNewsObjectList = new ArrayList<ActivityObject>();
		for (ActivityObjects activityObj : ActivityObjects.getActivityObjects()) {
			boolean isForm = (activityObj == activityObj.FORMS);
			if(activityObj == ActivityObjects.BLOG){
				blogObjectList.add(new ActivityObject(activityObj, getActivityActions(activityObj), isForm));
			} else if(activityObj == ActivityObjects.DOCUMENTS ||activityObj == ActivityObjects.WIKI ) {
				wikiAndDocumentObjectList.add(new ActivityObject(activityObj, getActivityActions(activityObj), isForm));
			} else if(activityObj == activityObj.FORMS){
				formObjectList.add(new ActivityObject(activityObj, getActivityActions(activityObj), isForm));
			} else if(activityObj == ActivityObjects.WORKPLAN || activityObj == ActivityObjects.NEWS){
				workplanAndNewsObjectList.add(new ActivityObject(activityObj, getActivityActions(activityObj), isForm));
			}
			filterObjects.add(new ActivityObject(activityObj, getActivityActions(activityObj), isForm));
			if(activityObj == activityObj.BLOG){
				filterCriteriaForRSS += activityObj.getValue() + "," +ObjectType.BLOG_COMMENT.toString() + ",";
			} else if(activityObj == activityObj.DOCUMENTS){
				filterCriteriaForRSS += activityObj.getValue() + "," +ObjectType.CONTAINER.toString() + ",";
			} else if(!(activityObj == activityObj.MARKED)){
				filterCriteriaForRSS += activityObj.getValue() + ",";
			}
		}
		filterCriteriaForRSS += ObjectType.PROJECT;
		encSpaceId = ActivityLogManager.encryptString(SessionManager.getUser().getCurrentSpace().getID());
		activityLog.setEncryptedSpaceId(encSpaceId);
		rssValue = encSpaceId + "/" + filterCriteriaForRSS;
		// displaying form list for filter check box value
		formListObjects = new ArrayList<ActivityObject>();
		pnClassService = ServiceFactory.getInstance().getPnClassService();
		List<PnClass> formList = pnClassService.getFormNamesForSpace(spaceId.toString());
		if (CollectionUtils.isNotEmpty(formList)) {
			for (PnClass pnClass : formList) {
				formListObjects.add(new ActivityObject(pnClass.getClassName().length() > 15 ? pnClass.getClassName().substring(0, 15).toString()+"..." : pnClass.getClassName().toString(), pnClass.getClassId(),
						getActivityActions(null)));
			}
		}
		List personList = new ArrayList<PnPerson>();
		try {
			List<PnPerson> persons = getPnPersonService().getPersonsByProjectId(spaceId);

			PnPerson firstOption = new PnPerson();
			firstOption.setPersonId(0);
			firstOption.setDisplayName(PropertyProvider.get("prm.project.activity.allteam"));
			personList.add(firstOption);

			if (persons != null && persons.size() > 0) {
				personList.addAll(persons);
			}
		} catch (Exception e) {
			log.error("Error occured while getting team members : " + e.getMessage());
		}
		teamBeans = new GenericSelectModel<PnPerson>(personList, PnPerson.class, "displayName", "personId", access);
		//encryptedValue = encryptString();
		return null;
	}
	
	/**
	 * Method for getting dropdown list values for Team members from database
	 * 
	 * @return GenericSelectModel<BlogTeam>
	 */
	public GenericSelectModel<PnPerson> getTeamModel() {
		return teamBeans;
	}

	private List getActivityActions(ActivityObjects activityObj) {
		List actions = new ArrayList();
		if (activityObj == null) {
			actions.add(new ActivityType("New", EventType.NEW));
			actions.add(new ActivityType("Edited", EventType.EDITED));
			actions.add(new ActivityType("Deleted", EventType.DELETED));
		} else {
			switch (activityObj) {
			case DOCUMENTS:
				actions.add(new ActivityType("Imported", EventType.NEW));
				actions.add(new ActivityType("Properties Edited", EventType.EDITED));
				actions.add(new ActivityType("Removed", EventType.DELETED));
				actions.add(new ActivityType("Checked In", EventType.CHECKED_IN));
				actions.add(new ActivityType("Checked Out", EventType.CHECKED_OUT));
				actions.add(new ActivityType("Undo Checked Out", EventType.UNDO_CHECKED_OUT));
				actions.add(new ActivityType("Folder Created", EventType.FOLDER_CREATED));
				actions.add(new ActivityType("Folder Deleted", EventType.FOLDER_DELETED));
				actions.add(new ActivityType("Moved", EventType.MOVED));
				//actions.add(new ActivityType("Properties Viewed", EventType.VIEWED));
				break;
			case FORMS:
				actions.add(new ActivityType("New", EventType.NEW));
				actions.add(new ActivityType("Edited", EventType.EDITED));
				actions.add(new ActivityType("Deleted", EventType.DELETED));
				break;
			/*
			 * case DISCUSSIONS: actions.add(new ActivityType("New", EventType.NEW)); actions.add(new
			 * ActivityType("Edited", EventType.EDITED)); actions.add(new ActivityType("Deleted", EventType.DELETED));
			 * actions.add(new ActivityType("Post Created",
			 * net.project.notification.EventCodes.CREATE_DISCUSSION_POST)); actions.add(new ActivityType("Discussion
			 * Post Deleted", net.project.notification.EventCodes.REMOVE_DISCUSSION_POST)); actions.add(new
			 * ActivityType("Post Deleted", net.project.notification.EventCodes.REMOVE_POST)); actions.add(new
			 * ActivityType("Replied", net.project.notification.EventCodes.CREATE_REPLY)); break;
			 */
			case NEWS:
				actions.add(new ActivityType("New", EventType.NEW));
				actions.add(new ActivityType("Edited", EventType.EDITED));
				actions.add(new ActivityType("Deleted", EventType.DELETED));
				break;
			case WORKPLAN:
				actions.add(new ActivityType("New Task", EventType.NEW));
				actions.add(new ActivityType("Task Properties Edited", EventType.EDITED));
				actions.add(new ActivityType("Deleted Task", EventType.DELETED));
				break;
			case BLOG:
				actions.add(new ActivityType("New", EventType.NEW));
				actions.add(new ActivityType("Edited", EventType.EDITED));
				actions.add(new ActivityType("Deleted", EventType.DELETED));
				actions.add(new ActivityType("Commented", EventType.COMMENTED));
				actions.add(new ActivityType(PropertyProvider.get("prm.project.activity.blog.mycommentedblog"), "my_posts_that_are_commented"));
				actions.add(new ActivityType(PropertyProvider.get("prm.project.activity.blog.important"),"important"));
				break;
			case WIKI:
				actions.add(new ActivityType("New", EventType.NEW));
				actions.add(new ActivityType("Edited", EventType.EDITED));
				actions.add(new ActivityType("Deleted", EventType.DELETED));
				actions.add(new ActivityType("Image Uploaded", EventType.IMAGE_UPLOADED));
				break;
			case MARKED:
				break;
			}
		}
		return actions;
	}

	/**
	 * Inner class for activity type
	 */
	public class ActivityType implements Serializable {

		private String name;

		private String value;

		/**
		 * 
		 */
		public ActivityType() {
		}

		/**
		 * 
		 */
		public ActivityType(String name, EventType value) {
			this.name = name;
			this.value = value.getText();
		}
		
		/**
		 * 
		 */
		public ActivityType(String name, String value) {
			this.name = name;
			this.value = value;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * @param value
		 *            the value to set
		 */
		public void setValue(String value) {
			this.value = value;
		}

	}

	/**
	 * @return the encSpaceId
	 */
	public String getEncSpaceId() {
		return encSpaceId;
	}

	/**
	 * @param encSpaceId the encSpaceId to set
	 */
	public void setEncSpaceId(String encSpaceId) {
		this.encSpaceId = encSpaceId;
	}

	/**
	 * @return String
	 */
	public String getFilterCriteriaValue() {
		return filterCriteriaValue;
	}

	/**
	 * @param filterCriteriaValue
	 */
	public void setFilterCriteriaValue(String filterCriteriaValue) {
		this.filterCriteriaValue = filterCriteriaValue;
	}

	/**
	 * @return PnPerson
	 */
	public PnPerson getPerson() {
		return person;
	}

	/**
	 * @param PnPerson person
	 */
	public void setPerson(PnPerson person) {
		this.person = person;
	}

	/**
	 * @return String
	 */
	public String getJumpedDate() {
		return jumpedDate;
	}

	/**
	 * @param String jumpedDate
	 */
	public void setJumpedDate(String jumpedDate) {
		this.jumpedDate = jumpedDate;
	}

	/**
	 * @return Date
	 */
	public Date getPersistedJumpedDate() {
		return persistedJumpedDate;
	}

	/**
	 * @param Date persistedJumpedDate
	 */
	public void setPersistedJumpedDate(Date persistedJumpedDate) {
		this.persistedJumpedDate = persistedJumpedDate;
	}

	/**
	 * To get ProjectSpace
	 * @return ProjectSpaceBean
	 */
	public ProjectSpaceBean getProjectSpace(){
		return (ProjectSpaceBean) getUser().getCurrentSpace();
	}}
