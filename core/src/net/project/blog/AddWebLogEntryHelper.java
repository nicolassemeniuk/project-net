package net.project.blog;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.project.base.ObjectType;
import net.project.base.PnWebloggerException;
import net.project.hibernate.constants.WeblogConstants;
import net.project.hibernate.model.PnObjectType;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnTask;
import net.project.hibernate.model.PnWeblog;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.model.PnWeblogEntryAttribute;
import net.project.hibernate.service.IBlogProvider;
import net.project.hibernate.service.IPnObjectTypeService;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.IPnTaskService;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.InvalidDateException;
import net.project.util.StringUtils;

import org.apache.log4j.Logger;

public class AddWebLogEntryHelper {
	
	private static Logger log = Logger.getLogger(AddWebLogEntryHelper.class);

	private static IPnObjectTypeService objectTypeService = ServiceFactory.getInstance().getPnObjectTypeService();
	
	private static IBlogProvider blogProvider = ServiceFactory.getInstance().getBlogProvider();
	
	private static String[] objectDetails;
	
	/**
	 * default constructor 
	 */
	public AddWebLogEntryHelper(){
	}
	
	/** 
	 * Method for adding multiple work submitted blog entry
	 *  
	 * @param request
	 * @param commentArray
	 */
	public static void saveMultpleWrokSubmittedBlogEntry(String objectId, String subject, String isImportantStr, String spaceId, String[] commentArray, String taskIdArray, String spaceIdArray, List assignmentWorkId, String inputContent) {
		boolean isImportant = Boolean.parseBoolean(isImportantStr == null ? "false" : isImportantStr);
		Integer blogEntryId = null;
		
		// saving blog entry from blogIt window
		String title = StringUtils.isNotEmpty(subject) ? subject : "";
		
		String taskId[] = null;
		if(StringUtils.isNotEmpty(taskIdArray)){
			taskId = taskIdArray.split(",");
		}
		
		// space ids (project ids) of all tasks in which blog entry should be posted
		String spaceIds[] = null;
		if(StringUtils.isNotEmpty(spaceIdArray)){
			spaceIds = spaceIdArray.split(",");
		}
		
		Integer[] assignmentWorkIdArray = new Integer[assignmentWorkId.size()];
		String multipleWorkSubmitted = "";
		for(int index = 0; index < assignmentWorkId.size(); index++){
			assignmentWorkIdArray = (Integer[])assignmentWorkId.get(index);
			for(int arrayIndex = 0; arrayIndex < assignmentWorkIdArray.length; arrayIndex++) {
				multipleWorkSubmitted += assignmentWorkIdArray[arrayIndex] + "," + taskId[index] + ",";
			}
		}
		
		if(StringUtils.isNotEmpty(multipleWorkSubmitted)){
			multipleWorkSubmitted = multipleWorkSubmitted.substring(0, (multipleWorkSubmitted.length()-1));
		}

		if (StringUtils.isNotEmpty(title) && StringUtils.isNotEmpty(inputContent) 
				&& StringUtils.isNotEmpty(spaceId) && StringUtils.isNotEmpty(objectId)) {
		
			// saving the blog entry in all project blogs
			for(String projectId : spaceIds){
				blogEntryId = saveBlogEntryForObject(title, inputContent, Integer.parseInt(projectId), 
						Integer.parseInt(projectId), isImportant, multipleWorkSubmitted);			
			}
		}
		
	}
	
	/**
	 * Method for saving blog entry for selected object from work plan
	 * 
	 * @param title of the blog entry
	 * @param content of the blog entry
	 * @param spaceId
	 * @param objectId
	 * @param isImportant
	 * @param multipleWorkSubmitted
	 * @return
	 */
	public static Integer saveBlogEntryForObject(String title, String content, Integer spaceId, Integer objectId, boolean isImportant, String multipleWorkSubmitted) {
		Integer blogEntryId = null;
		PnWeblog weblog = null;
		try {
			if(objectId != null && objectId != 0){				
				getObjectDetails(objectId);
			}
			if(getObjectDetails() == null){
				weblog = blogProvider.getWeblogBySpaceId(objectId);
			} else {
				if (getObjectDetails()[0].equalsIgnoreCase("taskId")) {
					try {
						IPnTaskService taskService = ServiceFactory.getInstance().getPnTaskService();
						spaceId = taskService.getProjectByTaskId(objectId);
					} catch (Exception e) {
						log.error("Error occurred while getting project id by task id " + e.getMessage());
					}
				}
				weblog = blogProvider.getWeblogBySpaceId(spaceId);
			}
			blogEntryId = saveBlogEntry(title, content, multipleWorkSubmitted, isImportant, weblog);
		} catch (Exception e) {
			log.error("Error occurred while saving blog entry for object : "+e.getMessage());
		}
		return blogEntryId;
	}
	
	/**
	 * Method to get object details by object id
	 * 
	 * @param objectId object identifier
	 */
	private static void getObjectDetails(Integer objectId) {
		PnObjectType objectType = objectTypeService.getObjectTypeByObjectId(objectId);
		if (objectType != null) {
			if (objectType.getObjectType().equalsIgnoreCase(ObjectType.TASK)) {
				IPnTaskService pnTaskService = ServiceFactory.getInstance().getPnTaskService();
				PnTask task = pnTaskService.getTaskDetailsById(objectId);
				if (task != null && task.getTaskId() != 0) {
					String[] details = { "taskId", task.getTaskId().toString(), task.getTaskName(), objectType.getObjectType() };
					setObjectDetails(details);
				}
			} else if (objectType.getObjectType().equalsIgnoreCase(ObjectType.PROJECT)) {
				setObjectDetails(null);
			} 
		}
	}

	/**
	 * Method to save blog entry
	 * 
	 * @param title
	 * @param content
	 * @param multipleWorkSubmitted
	 * @param isImportant
	 * @param weblog
	 * @return saved blog entry identifier 
	 */
	public static Integer saveBlogEntry(String title, String content, String multipleWorkSubmitted, boolean isImportant, PnWeblog weblog){	
		if(title != null && content != null){
			try {
				DateFormat userDateFormat = SessionManager.getUser().getDateFormatter();
				IPnPersonService personService = ServiceFactory.getInstance().getPnPersonService();
				Date currentDate = null;
				try {
					currentDate = userDateFormat.parseDateTimeString(userDateFormat.formatDateTime(Calendar.getInstance().getTime()));
				} catch (InvalidDateException e) {
					log.error("Error occured while formatting current date : "+e.getMessage());
				}
				PnWeblogEntry pnWeblogEntry = new PnWeblogEntry();
				// loading person object
				PnPerson person = personService.getPerson(Integer.parseInt(SessionManager.getUser().getID()));	
				// creating weblog entry object from data provided from user to store in database 
				pnWeblogEntry.setPnPerson(person);
				// checking if title is not set then set anchor as blank.
				if (StringUtils.isNotEmpty(title)) {					
					pnWeblogEntry.setAnchor(title.trim().replaceAll(" ","_"));
				}
				pnWeblogEntry.setTitle(title.trim());
				pnWeblogEntry.setText(content);
				pnWeblogEntry.setUpdateTime(currentDate);
				pnWeblogEntry.setPnWeblog(weblog);
				pnWeblogEntry.setPublishEntry(WeblogConstants.YES_PUBLISH_ENTRY);
				pnWeblogEntry.setAllowComments(WeblogConstants.YES_ALLOW_COMMENTS);
				pnWeblogEntry.setCommentDays(WeblogConstants.DEFAULT_COMMENT_DAYS);
				pnWeblogEntry.setRightToLeft(WeblogConstants.NOT_RIGHT_TO_LEFT);
				pnWeblogEntry.setLocale(SessionManager.getUser().getLocaleCode());		
				pnWeblogEntry.setStatus(WeblogConstants.STATUS_PUBLISHED);
				pnWeblogEntry.setPubTime(currentDate);
                if(isImportant){
                    pnWeblogEntry.setIsImportant(WeblogConstants.YES_IMPORTANT);
                } else {
                    pnWeblogEntry.setIsImportant(WeblogConstants.NOT_IMPORTANT);
                }
				// saving blog entry
				pnWeblogEntry.setWeblogEntryId(null);
				Integer weblogEntryId = blogProvider.saveWeblogEntry(pnWeblogEntry);
				pnWeblogEntry.setWeblogEntryId(weblogEntryId);
				// saving object id as entry attribute if any object (assignment/task, document) is selected
				if(getObjectDetails() != null && getObjectDetails().length == 4){				
					saveEntryAttribute(getObjectDetails()[0], getObjectDetails()[1], pnWeblogEntry);
					// saving context_type and context for entry
					saveEntryAttribute(getObjectDetails()[3], getObjectDetails()[2], pnWeblogEntry);
				} else {
					// saving space id as entry attribute
					saveEntryAttribute("spaceId", weblog.getSpaceId().toString(), pnWeblogEntry);
				}
				
				// saving the multiple work captured values as entry attribute
				if (StringUtils.isNotEmpty(multipleWorkSubmitted) && !multipleWorkSubmitted.equals("0")) {
					saveEntryAttribute("multipleWorkSubmitted", multipleWorkSubmitted, pnWeblogEntry);
				}
				return weblogEntryId;
			} catch (PnWebloggerException pnetEx) {
				log.error("Error occurred while saving blog entry : "+pnetEx.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * Method to save weblog entry attribute
	 * 
	 * @param name of the entry attribute to save
	 * @param value of the entry attribute to save
	 * @param pnWeblogEntry instance of the entry 
	 */
	public static void saveEntryAttribute(String name, String value, PnWeblogEntry pnWeblogEntry){
		// creating weblog entry attribute object to save		
		PnWeblogEntryAttribute pnWeblogEntryAttribute = new PnWeblogEntryAttribute();		
		pnWeblogEntryAttribute.setName(name);
		pnWeblogEntryAttribute.setValue(value);
		pnWeblogEntryAttribute.setPnWeblogEntry(pnWeblogEntry);
		try {
			pnWeblogEntryAttribute.setWeblogEntryAttributeId(null);
			blogProvider.saveWeblogEntryAttribute(pnWeblogEntryAttribute);
		} catch (PnWebloggerException ex) {
			log.error("Error occured while saving blog entry attribute : "+ex.getMessage());
		}
	}
	
	/**
	 * @return the objectDetails
	 */
	public static String[] getObjectDetails() {
		return objectDetails;
	}

	/**
	 * @param objectDetails the objectDetails to set
	 */
	public static void setObjectDetails(String[] objDetails) {
		objectDetails = objDetails;
	}

}
