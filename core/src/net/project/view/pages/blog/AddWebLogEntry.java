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
/**
 * 
 */
package net.project.view.pages.blog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.project.base.EventException;
import net.project.base.EventFactory;
import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.PnWebloggerException;
import net.project.base.property.PropertyProvider;
import net.project.calendar.Meeting;
import net.project.events.BlogEvent;
import net.project.events.EventType;
import net.project.form.FormData;
import net.project.hibernate.constants.WeblogConstants;
import net.project.hibernate.model.PnDocument;
import net.project.hibernate.model.PnObjectType;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.model.PnTask;
import net.project.hibernate.model.PnWeblog;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.model.PnWeblogEntryAttribute;
import net.project.hibernate.model.PnWikiPage;
import net.project.hibernate.service.IBlogProvider;
import net.project.hibernate.service.IBlogViewProvider;
import net.project.hibernate.service.IPnDocumentService;
import net.project.hibernate.service.IPnObjectTypeService;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.IPnProjectSpaceService;
import net.project.hibernate.service.IPnTaskService;
import net.project.hibernate.service.IPnWikiPageService;
import net.project.hibernate.service.ServiceFactory;
import net.project.notification.EventCodes;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpaceBean;
import net.project.security.SecurityProvider;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.space.SpaceTypes;
import net.project.util.DateFormat;
import net.project.util.HTMLUtils;
import net.project.util.InvalidDateException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.util.TextStreamResponse;
import org.json.JSONException;
import org.json.JSONStringer;

/**
 * @author
 *
 */
public class AddWebLogEntry {
	
	private static Logger log;
	
	@Persist
	private Integer weblogId;
	
	private Integer weblogEntryId;
	
	private String title;
	
	private String content;
	
	private boolean isImportant = false;
	
	private String workSubmitted;
	
	private String changedEstimate;
	
	private String date;
	
	private String titleValidationMessage;
	
	private String contentValidationMessage;
	
	private String validationMessage;
	
	@Inject
	private IBlogProvider blogProvider;
	
	@Inject
	private IPnPersonService personService;
	
	@Persist
	private PnWeblogEntry pnWeblogEntry;
	
	private String jspRootURL;	
	
	@InjectPage
	private ViewBlog blog;
	
	@Persist
	private PnWeblog weblog;
	
	private PnTask task;
	
	private String weblogDateFormatPattern;
	
	private String[] objectDetails;
	
	@Inject
	private RequestGlobals requestGlobals;
	
	@Inject
	private IPnObjectTypeService objectTypeService;
	
	private Integer timelogId = null;
	
	@InjectPage
	private BlogEntry blogEntry;
	
	private String submitEntryButtonCaption;
	
	private String cancelButtonCaption;
	
	private String securityValidationMsg;
	
	/**
	 * Initialize values
	 */
	public void initializeTokens() {
		try {
			log = Logger.getLogger(AddWebLogEntry.class);
			//blogProvider = ServiceFactory.getInstance().getBlogProvider();
			//personService = ServiceFactory.getInstance().getPnPersonService();
			validationMessage = PropertyProvider.get("prm.blog.addweblogentry.validation.message");
			submitEntryButtonCaption = PropertyProvider.get("prm.blog.addweblogentry.postbutton.caption");
			cancelButtonCaption = PropertyProvider.get("prm.blog.addweblogentry.cancelbutton.caption");
			securityValidationMsg = PropertyProvider.get("prm.blog.addweblogentry.securityvalidation.message");
			weblogDateFormatPattern = "EEEEE, MMM dd, yyyy";
			//objectTypeService = ServiceFactory.getInstance().getPnObjectTypeService();
            date = SessionManager.getUser().getDateFormatter().formatDate(Calendar.getInstance().getTime(), weblogDateFormatPattern);
            jspRootURL = SessionManager.getJSPRootURL();
		}catch (Exception e) {
			log.error("Error occured while getting property tokens : "+ e.getMessage());
		}
	}
	
	/**
	 * Method called on page activate generating assignments list
	 * for project blog
	 */
	void onActivate(){
		if (net.project.security.SessionManager.getUser() == null) {
			throw new IllegalStateException("User is null");
		}
        initializeTokens();
	}
	
	/**
	 * Method gets called on page activation with single parameter
	 *   
	 * @param action to be performed
	 * @return page instance
	 */
	Object onActivate(String action) {
		initializeTokens();
		PnObjectType objectType = null;
		if (action != null) {
			HttpServletRequest request = requestGlobals.getHTTPServletRequest();
			if (action.contains("SaveBlogEntry")) {
				// saving time log entry from blogIt popup
				String objectId = request.getParameter("objectId");
				objectType = objectTypeService.getObjectTypeByObjectId(Integer.parseInt(objectId));
				isImportant = Boolean.parseBoolean(request.getParameter("isImportant") == null ? "false" : 
													request.getParameter("isImportant").toString());
				Integer blogEntryId = null;
				// saving blog entry from blogIt popup
				String title = request.getParameter("subject") != null ? request.getParameter("subject") : "";
				String content = request.getParameter("content");
				String spaceId = request.getParameter("spaceId");
				workSubmitted = request.getParameter("workSubmitted");
				changedEstimate = request.getParameter("changedEstimate");
				if(objectType.getObjectType().equals(ObjectType.BUSINESS) || objectType.getObjectType().equals(ObjectType.APPLICATION) || 
                        objectType.getObjectType().equals(ObjectType.METHODOLOGY) || objectType.getObjectType().equals(ObjectType.CONFIGURATION)){
					objectId = SessionManager.getUser().getID();
				}
				if (title != null && content != null && spaceId != null && objectId != null) {
					blogEntryId = saveBlogEntryForObject(title, content, Integer.parseInt(spaceId), Integer
							.parseInt(objectId),isImportant);
				}
				return new TextStreamResponse("text/plain", (blogEntryId != null ? "true" : "false"));
			} else if(action.contains("GetBlogEntries")) {
				// getting number of blog entries by object id
				return getBlogEntryDetails(request.getParameter("objectId"));
			} else if(action.equalsIgnoreCase("SaveUpdateBlogEntry")){
				setTitle(request.getParameter("subject"));
				setContent(request.getParameter("content"));
				if(StringUtils.isNotEmpty(request.getParameter("isImportant"))) {
					isImportant = request.getParameter("isImportant").equalsIgnoreCase("true"); 
				}
				if(request.getParameter("weblogentryId") != null){
					weblogEntryId = Integer.valueOf(request.getParameter("weblogentryId"));
				}
				
				if(getTitle() != null && getContent() != null){
					try {
						if (weblogEntryId == null) {
							saveBlogEntry();
                            blog.setTotalBlogCount(blog.getTotalBlogCount() + 1); // increase the count of total blog entries by 1
							return new TextStreamResponse("text/plain", "true");
						} else if (pnWeblogEntry != null) {
							updateBlogEntry();
							return getUpdatedBlogEntry();
						}
                        return new TextStreamResponse("text/plain", "false");
					} catch (Exception e) {
						log.error("Error occurred while action save/update blog entry : "+e.getMessage());
					} finally {
						pnWeblogEntry = null;
					}
				}
			}else if(action.equalsIgnoreCase("delete_blog_entry")){
				if(StringUtils.isNotEmpty(request.getParameter("entryId"))){
					int weblogEntryId = Integer.parseInt(request.getParameter("entryId"));
					return deleteBlogEntry(weblogEntryId);
				}
				return new TextStreamResponse("text/plain", "false");
			}
		}
		return null;
	}
	
	/**
	 * Method called on page activation with two parameters
	 * 
	 * @param weblogEntryId
	 * @param weblogEntryImportantSymbol
	 */
	void onActivate(String weblogEntryId, String weblogEntryImportantSymbol) {
		editBlogEntry(weblogEntryId, weblogEntryImportantSymbol);
	}
	
	/**
	 * Method to get object details by object id
	 * 
	 * @param objectId object identifier
	 */
	private void getObjectDetails(Integer objectId) {
		PnObjectType objectType = objectTypeService.getObjectTypeByObjectId(objectId);
		if (objectType != null) {
			if (objectType.getObjectType().equalsIgnoreCase(ObjectType.TASK)) {
				IPnTaskService pnTaskService = ServiceFactory.getInstance().getPnTaskService();
				setTask(pnTaskService.getTaskDetailsById(objectId));
				if (getTask() != null && getTask().getTaskId() != 0) {
					if (getTimelogId() != null && getTimelogId() != 0) {
						// String[] details = {"timelogId", getTimelogId().toString(), getTask().getTaskName()};
						String[] details = { "timelogId", getTask().getTaskId().toString(), getTask().getTaskName(), objectType.getObjectType() };
						setObjectDetails(details);
					} else {
						String[] details = { "taskId", getTask().getTaskId().toString(), getTask().getTaskName(), objectType.getObjectType() };
						setObjectDetails(details);
					}
				}
			} else if (objectType.getObjectType().equalsIgnoreCase(ObjectType.DOCUMENT)) {
				IPnDocumentService documentService = ServiceFactory.getInstance().getPnDocumentService();
				PnDocument document = documentService.getDocumentDetailsById(objectId);
				if (document != null && document.getDocId() != 0) {
					String[] details = { "documentId", document.getDocId().toString(), document.getDocName(), objectType.getObjectType() };
					setObjectDetails(details);
				}
			} else if (objectType.getObjectType().equalsIgnoreCase(ObjectType.FORM_DATA)) {
				FormData formData = new FormData();
				try {
					formData.load(objectId.toString());
				} catch (PersistenceException pnetEx) {
                    log.error("Error occurred while loading form data " + pnetEx.getMessage());
				}
				if (formData != null) {
					String[] details = { "formDataId", objectId.toString(), formData.getName(), ObjectType.FORM+"_"+formData.getForm().getName()  };
					setObjectDetails(details);
				}
			}else if (objectType.getObjectType().equalsIgnoreCase(ObjectType.MEETING)) {
				Meeting meeting = new Meeting();
				meeting.setID(objectId.toString());
				try {
					meeting.load();
				} catch (PersistenceException pnetEx) {
                    log.error("Error occurred while loading meeting data " + pnetEx.getMessage());
				}	
				if(meeting != null && meeting.getMeetingID() != null){
					String[] details = { "meetingId", meeting.getMeetingID().toString(), meeting.getName(), objectType.getObjectType() };
					setObjectDetails(details);	
				}
			} else if (objectType.getObjectType().equalsIgnoreCase(ObjectType.PROJECT)) {
				setObjectDetails(null);
			} else if (objectType.getObjectType().equalsIgnoreCase(ObjectType.WIKI)) {
                IPnWikiPageService wikiPageService = ServiceFactory.getInstance().getPnWikiPageService();
                PnWikiPage wikiPage =  wikiPageService.get(Integer.valueOf(objectId));
                if (wikiPage != null) {
                    String[] details = { "wikiId", wikiPage.getWikiPageId().toString(), wikiPage.getPageName(), objectType.getObjectType() };
                    setObjectDetails(details);
                }
            } 
		}
	}
	
	/**
	 * Method for saving blog entry for selected object from work plan, doc vault etc.
	 * 
	 * @param title of the blog entry
	 * @param content of the blog entry
	 * @param spaceId
	 * @param objectId
	 */
	private Integer saveBlogEntryForObject(String title, String content, Integer spaceId, Integer objectId, boolean isImportant) {
		Integer blogEntryId = null;
		try {
			if(objectId != null && objectId != 0){				
				getObjectDetails(objectId);
			}
			if(getObjectDetails() == null){
				setWeblog(blogProvider.getWeblogBySpaceId(objectId));
			} else {
				if (getObjectDetails()[0].equalsIgnoreCase("taskId")) {
					try {
						IPnTaskService taskService = ServiceFactory.getInstance().getPnTaskService();
						spaceId = taskService.getProjectByTaskId(objectId);
					} catch (Exception e) {
						log.error("Error occurred while getting project id by task id " + e.getMessage());
					}
				} else if (getObjectDetails()[0].equalsIgnoreCase("formDataId")) {
					FormData formData = new FormData();
					try {
						formData.load(objectId.toString());
						if (formData != null){
							spaceId = Integer.parseInt(formData.getForm().getOwningSpaceID());
							
							// Set space id as current space id if the current space is project
							// as for the shared forms the owning space is business which doesn't have blog
							// and blog entry for form assignments should be saved in project blog
							if(SessionManager.getUser().getCurrentSpace().isTypeOf(SpaceTypes.PROJECT_SPACE) && 
									spaceId != Integer.valueOf(SessionManager.getUser().getCurrentSpace().getID())){
								spaceId = Integer.valueOf(SessionManager.getUser().getCurrentSpace().getID());
							} 
						}
					} catch (PersistenceException pnetEx) {
						log.error("Error occurred while getting project id by form data id " + pnetEx.getMessage());
					}
				}else if (getObjectDetails()[0].equalsIgnoreCase("meetingId")) {
					Meeting meeting = new Meeting();
                    meeting.setID(objectId.toString());
					try {
						spaceId = Integer.valueOf(meeting.getSpaceID());
					}catch(PersistenceException pnetEx){
						log.error("Error occurred while getting project id by meeting id " + pnetEx.getMessage());
					}
				}
				setWeblog(blogProvider.getWeblogBySpaceId(spaceId));
			}
			setTitle(title);
			setContent(content);
			setIsImportant(isImportant);
			blogEntryId = saveBlogEntry();
			setTask(null);
			pnWeblogEntry = null;			
		} catch (Exception e) {
			log.error("Error occurred while saving blog entry for object : "+e.getMessage());
		}
		return blogEntryId;
	}

	/**
	 * Method to find task by id and set it to task object
	 * on page activation while editing weblog entry
	 * 
	 * @param weblogEntryId id of the blog entry
	 * @param weblogEntryImportantSymbol important flag symbol
	 */
	void editBlogEntry(String weblogEntryId, String weblogEntryImportantSymbol){
		try {
			this.weblogEntryId  = new Integer(weblogEntryId);
			pnWeblogEntry = blogProvider.getWeblogEntryWithSpaceId(Integer.parseInt(weblogEntryId));
			setTitle(pnWeblogEntry.getTitle());
			setContent(pnWeblogEntry.getText());
			
			if(weblogEntryImportantSymbol != null && weblogEntryImportantSymbol.equals("!")){
				setIsImportant(true);
			} else {
				setIsImportant(false);
			}
		} catch (Exception pnetEx) {
			log.error("Error occurred while editing blog entry : "+pnetEx.getMessage());
		}
	}
	
    Object getBlogEntryDetails(String objectId){
        String spaceType = SessionManager.getUser().getCurrentSpace().getSpaceType().getName().toLowerCase();               
        HttpServletRequest request = requestGlobals.getHTTPServletRequest();
        try {
            if(StringUtils.isNotEmpty(objectId)){
                PnObjectType objectType = objectTypeService.getObjectTypeByObjectId(Integer.parseInt(objectId));
                String entryDetailsForObjectType = objectType.getObjectType(); 
                JSONStringer jsonObject = new JSONStringer();
                jsonObject.object();
                int blogCount = 0;
                if(objectType != null){
                    if(objectType.getObjectType().equalsIgnoreCase(ObjectType.PROJECT)){
                        spaceType = ObjectType.PROJECT;
                        IPnProjectSpaceService pnProjectSpaceService = ServiceFactory.getInstance().getPnProjectSpaceService();
                        PnProjectSpace pnProjectSpace = pnProjectSpaceService.getProjectSpaceDetails(Integer.parseInt(objectId));
                        if(pnProjectSpace != null){
                            jsonObject.key("blogSpaceName").value(HTMLUtils.escape(pnProjectSpace.getProjectName().replaceAll("'", "&acute;")));
                            jsonObject.key("blogSpaceType").value(spaceType);
                            jsonObject.key("blogSpaceNameTooltip").value(pnProjectSpace.getProjectName().replaceAll("'", "&acute;"));
                            jsonObject.key("blogSpaceTypeTooltip").value(spaceType);
                        }
                        if(blogProvider.getWeblogBySpaceId(Integer.parseInt(objectId)) != null){
                            blogCount = blogProvider.getCountOfBlogEntries(null, null, null, Integer.valueOf(objectId), 
                                    null, null, WeblogConstants.STATUS_PUBLISHED, false, null, false, null);
                        } else {
                            jsonObject = getBlogActivationDetails(jsonObject, ObjectType.PROJECT, Integer.valueOf(objectId));
                        }
                    } else if(objectType.getObjectType().equalsIgnoreCase(ObjectType.TASK)){
                    	if(!spaceType.equalsIgnoreCase(SpaceTypes.METHODOLOGY_SPACE)){
	                    	IPnTaskService taskService = null;
	                        Integer projectId = null;
	                        try {
	                            taskService = ServiceFactory.getInstance().getPnTaskService();
	                            projectId = taskService.getProjectByTaskId(Integer.parseInt(objectId));
	                            PnTask task = taskService.getTaskDetailsById(Integer.parseInt(objectId));
	                            if(task != null){
	                                jsonObject = getBlogContextDetails(jsonObject, objectType.getObjectType().toLowerCase(), task.getTaskName());
	                            }
	                            ProjectSpaceBean project = (ProjectSpaceBean) request.getSession().getAttribute("projectSpace");
	                            if (project != null) {
	                                jsonObject.key("blogSpaceName").value(HTMLUtils.escape(project.getName().replaceAll("'", "&acute;")));
	                                jsonObject.key("blogSpaceNameTooltip").value(project.getName().replaceAll("'", "&acute;"));
	                            }
	                        } catch (Exception e) {
	                            log.error("Error occurred while getting project id and task details by task id " + e.getMessage());
	                        }
	                        if(blogProvider.getWeblogBySpaceId(projectId) != null){
	                            blogCount = blogProvider.getCountOfBlogEntries(null, null, null, Integer.valueOf(objectId), 
	                                    null, null, WeblogConstants.STATUS_PUBLISHED, false, null, false, null);
	                        } else {
	                        	jsonObject = getBlogActivationDetails(jsonObject, ObjectType.PROJECT, projectId);
	                        } 
                    	} else{
                    		objectId = SessionManager.getUser().getID();
                            objectType = objectTypeService.getObjectTypeByObjectId(Integer.parseInt(objectId));
                            spaceType = Space.PERSONAL_SPACE;
                            if(blogProvider.getWeblogBySpaceId(Integer.parseInt(objectId)) != null){
                                blogCount = blogProvider.getCountOfBlogEntries(null, null, null, Integer.valueOf(objectId), 
                                        null, null, WeblogConstants.STATUS_PUBLISHED, false, null, false, null);
                            } else {
                                jsonObject = getBlogActivationDetails(jsonObject, spaceType, Integer.valueOf(objectId));
                            }
                    	}
					} else if(objectType.getObjectType().equalsIgnoreCase(ObjectType.MEETING)){
						Meeting meeting = new Meeting();
                        meeting.setID(objectId.toString());
                        Integer spaceId = null;
						try {
                            meeting.load();
                            spaceId = Integer.valueOf(meeting.getSpaceID());
                            objectType = objectTypeService.getObjectTypeByObjectId(spaceId);
                            if(meeting != null){
                                jsonObject = getBlogContextDetails(jsonObject, ObjectType.MEETING, meeting.getName());
                            }
                        } catch (PersistenceException pnetEx) {
                            log.error("Error occurred while loading meeting data " + pnetEx.getMessage());
                        } catch (Exception e) {
                            log.error("Error occurred while getting space id or loading meeting by meeting id " + e.getMessage());
                        } 
                        if (spaceId != null && blogProvider.getWeblogBySpaceId(spaceId) != null) {
                            blogCount = blogProvider.getCountOfBlogEntries(null, null, null, Integer
                                    .valueOf(objectId), null, null, WeblogConstants.STATUS_PUBLISHED, false, null, false, null);
                        } else if (objectType.getObjectType().equalsIgnoreCase(ObjectType.PROJECT)) {
                            jsonObject = getBlogActivationDetails(jsonObject, ObjectType.PROJECT, spaceId);
                        } else {
                            jsonObject = getBlogActivationDetails(jsonObject, spaceType, spaceId);
                        }
					} else if (objectType.getObjectType().equalsIgnoreCase(ObjectType.FORM_DATA)) {
                        Integer spaceId = null;
                        FormData formData = new FormData();
                        try {
                            formData.load(objectId.toString());
                            spaceId = Integer.valueOf(formData.getForm().getOwningSpaceID());
                            objectType = objectTypeService.getObjectTypeByObjectId(spaceId);
                            if (objectType.getObjectType().equalsIgnoreCase(ObjectType.BUSINESS)){
                            	spaceId = Integer.valueOf(SessionManager.getUser().getCurrentSpace().getID());
                            	spaceType = SessionManager.getUser().getCurrentSpace().getType();
                            } 
                            jsonObject = getBlogContextDetails(jsonObject, formData.getForm().getName()+"-"+formData.getSeqNum(), formData.getName());
                        } catch (PersistenceException pnetEx) {
                            log.error("Error occurred while loading form data " + pnetEx.getMessage());
                        }
                        if (spaceId != null && blogProvider.getWeblogBySpaceId(spaceId) != null) {
                            blogCount = blogProvider.getCountOfBlogEntries(null, null, null, Integer
                                    .valueOf(objectId), null, null, WeblogConstants.STATUS_PUBLISHED, false, null, false, null);
                        } else if (objectType.getObjectType().equalsIgnoreCase(ObjectType.PROJECT)) {
                            jsonObject = getBlogActivationDetails(jsonObject, ObjectType.PROJECT, spaceId);
                        } else {
                            jsonObject = getBlogActivationDetails(jsonObject, spaceType, spaceId);
                        }
                    } else if(objectType.getObjectType().equalsIgnoreCase(ObjectType.DOCUMENT)){
                        IPnDocumentService documentService = ServiceFactory.getInstance().getPnDocumentService();
                        PnDocument document = documentService.getDocumentDetailsById(Integer.parseInt(objectId));
                        
                        if (document != null ) {
                            jsonObject = getBlogContextDetails(jsonObject, objectType.getObjectType(), document.getDocName()); 
                        }
                        if(blogProvider.getWeblogBySpaceId(Integer.parseInt(SessionManager.getUser().getCurrentSpace().getID())) != null){
                            blogCount = blogProvider.getCountOfBlogEntries(null, null, null, Integer.valueOf(objectId), 
                                    null, null, WeblogConstants.STATUS_PUBLISHED, false, null, false, null);
                        } else {
                            jsonObject = getBlogActivationDetails(jsonObject, spaceType, Integer.valueOf(SessionManager.getUser().getCurrentSpace().getID()));
                        }
                    } else if(objectType.getObjectType().equalsIgnoreCase(ObjectType.WIKI)){
                        IPnWikiPageService wikiPageService = ServiceFactory.getInstance().getPnWikiPageService();
                        PnWikiPage wikiPage =  wikiPageService.get(Integer.valueOf(objectId));
                        if (wikiPage != null) {
                            jsonObject = getBlogContextDetails(jsonObject, objectType.getObjectType(), wikiPage.getPageName()); 
                        }
                        if(blogProvider.getWeblogBySpaceId(Integer.parseInt(SessionManager.getUser().getCurrentSpace().getID())) != null){
                            blogCount = blogProvider.getCountOfBlogEntries(null, null, null, Integer.valueOf(objectId), 
                                    null, null, WeblogConstants.STATUS_PUBLISHED, false, null, false, null);
                        } else {
                            jsonObject = getBlogActivationDetails(jsonObject, spaceType, Integer.valueOf(SessionManager.getUser().getCurrentSpace().getID()));
                        }
                    } else {
                        if(spaceType.equalsIgnoreCase(Space.BUSINESS_SPACE) || spaceType.equalsIgnoreCase(Space.APPLICATION_SPACE) || 
                                spaceType.equalsIgnoreCase(Space.METHODOLOGY_SPACE) || spaceType.equalsIgnoreCase(ObjectType.CONFIGURATION)){
                            objectId = SessionManager.getUser().getID();
                            objectType = objectTypeService.getObjectTypeByObjectId(Integer.parseInt(objectId));
                            spaceType = Space.PERSONAL_SPACE;
                            if(blogProvider.getWeblogBySpaceId(Integer.parseInt(objectId)) != null){
                                blogCount = blogProvider.getCountOfBlogEntries(null, null, null, Integer.valueOf(objectId), 
                                        null, null, WeblogConstants.STATUS_PUBLISHED, false, null, false, null);
                            } else {
                                jsonObject = getBlogActivationDetails(jsonObject, spaceType, Integer.valueOf(objectId));
                            }
                        } else {
                            if(blogProvider.getWeblogBySpaceId(Integer.parseInt(SessionManager.getUser().getCurrentSpace().getID())) != null){
                                blogCount = blogProvider.getCountOfBlogEntries(null, null, null, Integer.valueOf(objectId), 
                                        null, null, WeblogConstants.STATUS_PUBLISHED, false, null, false, null);
                            } else {
                                jsonObject = getBlogActivationDetails(jsonObject, spaceType, Integer.valueOf(objectId));
                            }
                        }
                        jsonObject.key("blogContextType").value(objectType.getObjectType().toLowerCase());
                    }
                    jsonObject.key("blogCount").value(blogCount);
                } else {
                    jsonObject.key("blogNotSupported").value(true);
                }
                jsonObject.key("userDate").value(date);
                jsonObject.key("moduleId").value(Module.getModuleForSpaceType(spaceType));
                jsonObject.key("recentTabActive").value(PropertyProvider.getBoolean("prm.blogit.defaulttab.recentblogstab"));
                SecurityProvider securityProvider = SessionManager.getSecurityProvider();
            	if(ObjectType.PROJECT.equalsIgnoreCase(entryDetailsForObjectType)){
					securityProvider.setSpace(SpaceFactory.constructSpaceFromID(objectId));
				}else{
					// Security Check: Is user allowed access to blog module?
					securityProvider.setSpace(SessionManager.getUser().getCurrentSpace());
				}
				if ((securityProvider.getSpace().isTypeOf(SpaceTypes.PERSONAL_SPACE) && objectType.getObjectType().toLowerCase().equals(ObjectType.PROJECT))
						|| securityProvider.isActionAllowed(null, net.project.base.Module.BLOG, net.project.security.Action.CREATE)) {
					jsonObject.key("accessAllowed").value(true);
				} else if(securityProvider.getSpace().isTypeOf(SpaceTypes.PERSONAL_SPACE)
						&& !objectType.getObjectType().toLowerCase().equals("project")
						|| securityProvider.getSpace().isTypeOf(SpaceTypes.METHODOLOGY_SPACE)){
					jsonObject.key("accessAllowed").value(true);
				} else{
					jsonObject.key("accessAllowed").value(false);
					jsonObject.key("securityValidationMsg").value(securityValidationMsg);
				}
                jsonObject.endObject();
                return new TextStreamResponse("text/json", jsonObject.toString());
            }
        } catch (Exception e) {
            log.error("Error occurred while getting no. of blog entries for object : "+e.getMessage());
        }
        return new TextStreamResponse("text/plain", "false");
    }
    
    // sets the context details to json object 
    public JSONStringer getBlogContextDetails(JSONStringer jsonObject, String contextType, String contextName) {
        try {
            jsonObject.key("blogContextType").value(contextType);
            jsonObject.key("blogContextName").value(HTMLUtils.escape(contextName.replaceAll("'", "&acute;")));
            jsonObject.key("blogContextTypeTooltip").value(contextType);
            jsonObject.key("blogContextNameTooltip").value(contextName.replaceAll("'", "&acute;"));
        } catch (JSONException e) {
            log.error("Error occurred while setting context details for object: " + e.getMessage());
        }
        return jsonObject;
    }
    
    // sets the blog activation details to json object 
    public JSONStringer getBlogActivationDetails(JSONStringer jsonObject, String blogActivationType, Integer blogActivateFor) {
        try {
            jsonObject.key("BlogNotExist").value(blogActivationType+"Blog");
            jsonObject.key("activateBlogFor").value(blogActivateFor);
        } catch (JSONException e) {
            log.error("Error occurred while setting context details for object: " + e.getMessage());
        }
        return jsonObject;
    }
    
	/**
	 * Getting content text with object name if entry posted for a selected object
	 * 
	 * @return string with object name
	 */
	String getContentTextForObject(){
		String objectName = "";
		if(getObjectDetails() != null && getObjectDetails().length == 3){
			objectName = "<font color=\"blue\" face=\"Arial\"><b>RE : "+ getObjectDetails()[2].replaceAll("'", "&acute;") +"</b></font> <br />";			
		}
		if(getTask() != null && getTask().getTaskId() != 0){
			objectName = "<font color=\"blue\" face=\"Arial\"><b>RE : "+ getTask().getTaskName().replaceAll("'", "&acute;") +"</b></font> <br />";			
		}
		return objectName;
	}
	
	/**
	 * Method to save blog entry
	 */
	Integer saveBlogEntry(){	
		if(getTitle() != null && getContent() != null){
			try {
				DateFormat userDateFormat = SessionManager.getUser().getDateFormatter();
				Date currentDate = null;
				try {
					currentDate = userDateFormat.parseDateTimeString(userDateFormat.formatDateTime(Calendar.getInstance().getTime()));
				} catch (InvalidDateException e) {
					log.error("Error occured while formatting current date : "+e.getMessage());
				}
				pnWeblogEntry = new PnWeblogEntry();
				// loading person object
				PnPerson person = personService.getPerson(Integer.parseInt(SessionManager.getUser().getID()));	
				// creating weblog entry object from data provided from user to store in database 
				pnWeblogEntry.setPnPerson(person);
				// checking if title is not set then set anchor as blank.
				if (StringUtils.isNotEmpty(getTitle())) {					
					pnWeblogEntry.setAnchor(getTitle().trim().replaceAll(" ","_"));
				}
				pnWeblogEntry.setTitle(getTitle().trim());
				pnWeblogEntry.setText(getContent());
				pnWeblogEntry.setUpdateTime(currentDate);
				pnWeblogEntry.setPnWeblog(weblog);
				pnWeblogEntry.setPublishEntry(WeblogConstants.YES_PUBLISH_ENTRY);
				pnWeblogEntry.setAllowComments(WeblogConstants.YES_ALLOW_COMMENTS);
				pnWeblogEntry.setCommentDays(WeblogConstants.DEFAULT_COMMENT_DAYS);
				pnWeblogEntry.setRightToLeft(WeblogConstants.NOT_RIGHT_TO_LEFT);
				pnWeblogEntry.setLocale(SessionManager.getUser().getLocaleCode());		
				pnWeblogEntry.setStatus(WeblogConstants.STATUS_PUBLISHED);
				pnWeblogEntry.setPubTime(currentDate);
                if(getIsImportant()){
                    pnWeblogEntry.setIsImportant(WeblogConstants.YES_IMPORTANT);
                } else {
                    pnWeblogEntry.setIsImportant(WeblogConstants.NOT_IMPORTANT);
                }
				// saving blog entry
				pnWeblogEntry.setWeblogEntryId(null);
				weblogEntryId = blogProvider.saveWeblogEntry(pnWeblogEntry);
				pnWeblogEntry.setWeblogEntryId(weblogEntryId);
				
				// saving object id as entry attribute if any object (assignment/task, document) is selected
				if(getObjectDetails() != null && getObjectDetails().length == 4){				
					saveEntryAttribute(getObjectDetails()[0], getObjectDetails()[1]);
					// saving context_type and context for entry
					saveEntryAttribute(getObjectDetails()[3], getObjectDetails()[2]);
				} else {
					// saving space id as entry attribute
					saveEntryAttribute("spaceId", weblog.getSpaceId().toString());
				}
				if (StringUtils.isNotEmpty(workSubmitted) && !workSubmitted.equals("0")) {
					saveEntryAttribute("workSubmitted", workSubmitted);
				}
				if (StringUtils.isNotEmpty(getChangedEstimate()) && !getChangedEstimate().equals("0")) {
					saveEntryAttribute("changedEstimate", getChangedEstimate());
				}

				// Create notificaiton
				net.project.blog.BlogEvent event = new net.project.blog.BlogEvent();
    			event.setSpaceID(pnWeblogEntry.getPnWeblog().getSpaceId().toString());
    			event.setTargetObjectID(pnWeblogEntry.getPnWeblog().getWeblogId().toString());
    			event.setTargetObjectType(EventCodes.CREATE_BLOG_ENTRY);
    			event.setTargetObjectXML(blog.getXMLBody(pnWeblogEntry));
    			event.setEventType(EventCodes.CREATE_BLOG_ENTRY);
    			event.setUser(SessionManager.getUser());
    			event.setDescription("Blog created : \"" + weblog.getDescription() + "\"");
    			
    			try {
    				event.store();
    			} catch (PersistenceException pnetEx) {
    				log.error("Error occurred while creating notification : "+pnetEx.getMessage());
    			}
    			
				// publishing the blog event
                BlogEvent blogEvent = (BlogEvent) EventFactory.getEvent(ObjectType.BLOG_ENTRY, EventType.NEW);
                blogEvent.setBlogEntryTitle(pnWeblogEntry.getTitle());
                blogEvent.setObjectID(pnWeblogEntry.getWeblogEntryId().toString());
                blogEvent.setObjectType(ObjectType.BLOG_ENTRY);
                blogEvent.setSpaceID(pnWeblogEntry.getPnWeblog().getSpaceId().toString());
                blogEvent.setIsImportant(pnWeblogEntry.getIsImportant());
    			blogEvent.publish();

				return weblogEntryId;
			} catch (EventException ex) {
				log.error("AddWebLogEntry.saveBlogEntry():: Blog entry Create Event Publishing Failed!"+ ex.getMessage());
			} catch (PnWebloggerException pnetEx) {
				pnWeblogEntry = null;
				log.error("Error occurred while saving blog entry : "+pnetEx.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * Method to update blog entry 
	 */
	void updateBlogEntry(){
		try {
			pnWeblogEntry.setAnchor(getTitle().trim().replaceAll(" ", "_"));
			pnWeblogEntry.setTitle(getTitle().trim());
            if(getIsImportant()){
                pnWeblogEntry.setIsImportant(WeblogConstants.YES_IMPORTANT);
            } else {
                pnWeblogEntry.setIsImportant(WeblogConstants.NOT_IMPORTANT);
            }
            
			pnWeblogEntry.setText(getContent()); //pnWeblogEntry.setText(getContentTextForObject() + contentAfterEdit);
			
			try {
				pnWeblogEntry.setUpdateTime(DateFormat.getInstance().parseDateTimeString(
						DateFormat.getInstance().formatDateTime(Calendar.getInstance().getTime())));
			} catch (InvalidDateException e) {
				log.error("Error occured while formatting current date : " + e.getMessage());
			}
			
			blogProvider.updateWeblogEntry(pnWeblogEntry);
			
			// Create notification
			net.project.blog.BlogEvent event = new net.project.blog.BlogEvent();
			event.setSpaceID(pnWeblogEntry.getPnWeblog().getSpaceId().toString());
			event.setTargetObjectID(pnWeblogEntry.getPnWeblog().getWeblogId().toString());
			event.setTargetObjectType(EventCodes.MODIFY_BLOG_ENTRY);
			event.setTargetObjectXML(blog.getXMLBody(pnWeblogEntry));
			event.setEventType(EventCodes.MODIFY_BLOG_ENTRY);
			event.setUser(SessionManager.getUser());
			event.setDescription("Blog updated : \"" + weblog.getDescription() + "\"");
			try {
				event.store();
			} catch (PersistenceException e) {
				log.error("Error occurred while creating notification : "+ e.getMessage());
			}
			
			// publishing the blog event
            BlogEvent blogEvent = (BlogEvent) EventFactory.getEvent(ObjectType.BLOG_ENTRY, EventType.EDITED);
            blogEvent.setBlogEntryTitle(pnWeblogEntry.getTitle());
            blogEvent.setObjectID(pnWeblogEntry.getWeblogEntryId().toString());
            blogEvent.setObjectType(ObjectType.BLOG_ENTRY);
            blogEvent.setSpaceID(pnWeblogEntry.getPnWeblog().getSpaceId().toString());
            blogEvent.setIsImportant(pnWeblogEntry.getIsImportant());
			blogEvent.publish();
		} catch (EventException ex) {
			log.error("AddWebLogEntry.updateBlogEntry():: Blog entry Update Event Publishing Failed!"+ ex.getMessage());
		} catch (PnWebloggerException e) { 
			log.error("Error occurred while updating blog entry : "+ e.getMessage());
		}
	}
	
	/**
	 * Method to save weblog entry attribute
	 * 
	 * @param name of the entry attribute to save
	 * @param value of the entry attribute to save
	 */
	void saveEntryAttribute(String name, String value){
		// creating weblog entry attribute object to save		
		PnWeblogEntryAttribute pnWeblogEntryAttribute = new PnWeblogEntryAttribute();		
		pnWeblogEntryAttribute.setName(name);
		pnWeblogEntryAttribute.setValue(value.length() > 240 ? value.substring(0, 237) + "..." : value);
		pnWeblogEntryAttribute.setPnWeblogEntry(pnWeblogEntry);
		try {
			pnWeblogEntryAttribute.setWeblogEntryAttributeId(null);
			blogProvider.saveWeblogEntryAttribute(pnWeblogEntryAttribute);
		} catch (PnWebloggerException ex) {
			log.error("Error occured while saving blog entry attribute : "+ex.getMessage());
		}
	}
	
	/**
	 * Method to update weblog entry attribute
	 * 
	 * @param object of the entry attribute to update 
	 */
	void updateEntryAttribute(PnWeblogEntryAttribute pnWeblogEntryAttribute){		
		try {
			blogProvider.updateWeblogEntryAttribute(pnWeblogEntryAttribute);
		} catch (PnWebloggerException ex) {
			log.error("Error occured while updating blog entry attribute : "+ex.getMessage());
		}
	}
	
	/**
	 * Method to get updated blog entry
	 * @return instance of BlogEntry
	 */
	Object getUpdatedBlogEntry(){
		try {
			blogEntry.setShowEntry(true);
			blogEntry.setShowComment(false);
			blogEntry.setLinkToPersonSpace(blog.getSpaceType().equals(Space.PERSONAL_SPACE));
			blogEntry.setShowEditLink(true);
			blogEntry.setShowExpandCollapseImage(true);
			pnWeblogEntry = blogProvider.getWeblogEntryDetail(weblogEntryId, pnWeblogEntry.getPnPerson().getPersonId());
			List<PnWeblogEntry> userWeblogEntry = new ArrayList<PnWeblogEntry>();
			userWeblogEntry.add(pnWeblogEntry);
            IBlogViewProvider blogViewProvider = ServiceFactory.getInstance().getBlogViewProvider();
			userWeblogEntry = blogViewProvider.getFormattedBlogEntries(userWeblogEntry, getJspRootURL(), blog.getSpaceType(), DateFormat.getInstance());
			if(userWeblogEntry != null && userWeblogEntry.size() > 0){
				pnWeblogEntry = userWeblogEntry.get(0);
				if(pnWeblogEntry != null)
					blogEntry.setPnWeblogEntry(pnWeblogEntry);
			}			
			return blogEntry;
			} catch (PnWebloggerException ex) {
				log.error("Error occured while getting updated blog entry : "+ex.getMessage());
			}
		return null;
	}
	
	/**
	 * Method to delete blog entry by space administrator
	 * 
	 * @param weblogEntryId weblog entry identifier
	 * @return Blog page instance
	 */
	Object deleteBlogEntry(int weblogEntryId) {		
		try {
			PnWeblogEntry entry = blogProvider.getWeblogEntryWithSpaceId(weblogEntryId);			
			entry.setStatus(WeblogConstants.STATUS_DELETED);
			if (entry.getPnPerson().getPersonId() == Integer.parseInt(SessionManager.getUser().getID())
                    || (blog.getSpaceType().equalsIgnoreCase(Space.PROJECT_SPACE) && SessionManager.getUser().isSpaceAdministrator())) {
    			blogProvider.updateWeblogEntry(entry);
                blog.setTotalBlogCount(blog.getTotalBlogCount() > 0 ? blog.getTotalBlogCount()- 1 : blog.getTotalBlogCount()); // decrease the count of total blog entries by 1
                
                // Create notificaiton
                net.project.blog.BlogEvent event = new net.project.blog.BlogEvent();
    			event.setSpaceID(entry.getPnWeblog().getSpaceId().toString());
    			event.setTargetObjectID(entry.getPnWeblog().getWeblogId().toString());
    			event.setTargetObjectType(EventCodes.DELETE_BLOG_ENTRY);
    			event.setTargetObjectXML(blog.getXMLBody(entry));
    			event.setEventType(EventCodes.DELETE_BLOG_ENTRY);
    			event.setUser(SessionManager.getUser());
    			event.setDescription("Blog deleted : \"" + weblog.getDescription() + "\"");
    			
    			try {
    				event.store();
    			} catch (PersistenceException pnetEx) {
    				log.error("Error occurred while creating notification : "+ pnetEx.getMessage());
    			}
                // publishing the blog event
                BlogEvent blogEvent = (BlogEvent) EventFactory.getEvent(ObjectType.BLOG_ENTRY, EventType.DELETED);
                blogEvent.setBlogEntryTitle(entry.getTitle());
                blogEvent.setObjectID(entry.getWeblogEntryId().toString());
                blogEvent.setObjectType(ObjectType.BLOG_ENTRY);
                blogEvent.setSpaceID(entry.getPnWeblog().getSpaceId().toString());
                blogEvent.setIsImportant(entry.getIsImportant());
    			blogEvent.publish();
    			
    			return new TextStreamResponse("text/plain", "true");
			} else {
				return new TextStreamResponse("text/plain", "unauthorized");
			}			
		} catch (EventException ex) {
			log.error("AddWebLogEntry.deleteBlogEntry():: Blog entry Remove Event Publishing Failed!"+ ex.getMessage());
		} catch (PnWebloggerException e) {
			log.error("Error occured while deleting blog entry : "+ e.getMessage());
		}
		return new TextStreamResponse("text/plain", "false");
	}
	
	/**
	 * Cleaning values which are not required after rendering
	 */
	@CleanupRender
	void cleanValuesAfterRender(){
        
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}


	/**
	 * @return the weblogId
	 */
	public Integer getWeblogId() {
		return weblogId;
	}


	/**
	 * @param weblogId the weblogId to set
	 */
	public void setWeblogId(Integer weblogId) {
		this.weblogId = weblogId;
	}

	/**
	 * @return the jspRootURL
	 */
	public String getJspRootURL() {
		return jspRootURL;
	}

	/**
	 * @return the isImportant
	 */
	public boolean getIsImportant() {
		return isImportant;
	}

	/**
	 * @param isImportant the isImportant to set
	 */
	public void setIsImportant(boolean isImportant) {
		this.isImportant = isImportant;
	}

	/**
	 * @return the weblog
	 */
	public PnWeblog getWeblog() {
		return weblog;
	}

	/**
	 * @param weblog the weblog to set
	 */
	public void setWeblog(PnWeblog weblog) {
		this.weblog = weblog;
	}

	/**
	 * @return the task
	 */
	public PnTask getTask() {
		return task;
	}

	/**
	 * @param task the task to set
	 */
	public void setTask(PnTask task) {
		this.task = task;
	}

	/**
	 * @return the contentValidationMessage
	 */
	public String getContentValidationMessage() {
		return contentValidationMessage;
	}

	/**
	 * @return the titleValidationMessage
	 */
	public String getTitleValidationMessage() {
		return titleValidationMessage;
	}

	/**
	 * @return the validationMessage
	 */
	public String getValidationMessage() {
		return validationMessage;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @return the objectDetails
	 */
	public String[] getObjectDetails() {
		return objectDetails;
	}

	/**
	 * @param objectDetails the objectDetails to set
	 */
	public void setObjectDetails(String[] objectDetails) {
		this.objectDetails = objectDetails;
	}

	/**
	 * @return the timelogId
	 */
	public Integer getTimelogId() {
		return timelogId;
	}

	/**
	 * @return the weblogEntryId
	 */
	public Integer getWeblogEntryId() {
		return weblogEntryId;
	}

	/**
	 * @return the changedEstimate
	 */
	public String getChangedEstimate() {
		return changedEstimate;
	}

	/**
	 * @return the cancelButtonCaption
	 */
	public String getCancelButtonCaption() {
		return cancelButtonCaption;
	}

	/**
	 * @return the submitEntryButtonCaption
	 */
	public String getSubmitEntryButtonCaption() {
		return submitEntryButtonCaption;
	}	
	
}
