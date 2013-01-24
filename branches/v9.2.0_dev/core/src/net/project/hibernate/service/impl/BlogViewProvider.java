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
package net.project.hibernate.service.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.PnWebloggerException;
import net.project.base.URLFactory;
import net.project.base.property.PropertyProvider;
import net.project.calendar.Meeting;
import net.project.form.FormData;
import net.project.hibernate.constants.WeblogConstants;
import net.project.hibernate.model.PnDocument;
import net.project.hibernate.model.PnTask;
import net.project.hibernate.model.PnWeblogComment;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.model.PnWeblogEntryAttribute;
import net.project.hibernate.model.PnWikiPage;
import net.project.hibernate.service.IBlogProvider;
import net.project.hibernate.service.IBlogViewProvider;
import net.project.hibernate.service.IPnDocumentService;
import net.project.hibernate.service.IPnTaskService;
import net.project.hibernate.service.IPnWikiPageService;
import net.project.security.SecurityProvider;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;
import net.project.util.DateFormat;
import net.project.util.HTMLUtils;
import net.project.util.InvalidDateException;
import net.project.util.NumberFormat;
import net.project.wiki.WikiManager;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.util.TextStreamResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="blogViewProvider")
public class BlogViewProvider implements IBlogViewProvider {

    private static Logger log = Logger.getLogger(BlogViewProvider.class);

    @Autowired
    private IBlogProvider blogProvider;

    /**
     * @param blogProvider the blogProvider to set
     */
    public void setBlogProvider(IBlogProvider blogProvider) {
        this.blogProvider = blogProvider;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.project.hibernate.service.IBlogViewProvider#getBlogEntries(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.Integer, net.project.util.DateFormat)
     */
    public List<PnWeblogEntry> getBlogEntries(String spaceType, String startDateString, String endDateString,
            String userId, Integer weblogId, DateFormat userDateFormat) {
        List<PnWeblogEntry> userWeblogEntries = null;
        if (startDateString.contains("-") && weblogId != null) {
            Date startDate = null;
            Date endDate = null;
            try {
                startDate = userDateFormat.parseDateString(startDateString, "dd-MM-yyyy");
                endDate = userDateFormat.parseDateString(endDateString, "dd-MM-yyyy");
                // get blog entries of a weblog
                userWeblogEntries = blogProvider.getWeblogEntries(weblogId, null, startDate, endDate, WeblogConstants.STATUS_PUBLISHED, 0, 0);

                if (spaceType.equals(Space.PERSONAL_SPACE)) {
                    // get blog entries from project weblogs
                    if (userWeblogEntries != null && userWeblogEntries.size() > 0) {
                        userWeblogEntries.addAll(blogProvider.getWeblogEntriesFromProjectBlogByPerson(Integer.parseInt(userId), null, startDate, endDate, WeblogConstants.STATUS_PUBLISHED, 0, 0));
                    } else {
                        userWeblogEntries = blogProvider.getWeblogEntriesFromProjectBlogByPerson(Integer.parseInt(userId), null, startDate, endDate, WeblogConstants.STATUS_PUBLISHED, 0, 0);
                    }
                }
            } catch (NumberFormatException pnetEx) {
                log.error(pnetEx.getMessage());
            } catch (PnWebloggerException pnetEx) {
                log.error(pnetEx.getMessage());
            } catch (InvalidDateException pnetEx) {
                log.error(pnetEx.getMessage());
            }
        }
        return userWeblogEntries;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.project.hibernate.service.IBlogViewProvider#getFormattedBlogEntries(java.util.List,
     *      java.lang.String, java.lang.String)
     */
    public List<PnWeblogEntry> getFormattedBlogEntries(List<PnWeblogEntry> userWeblogEntries, String JSPRootURL,
            String spaceType, DateFormat userDateFormat) {
    	NumberFormat numberFormat = NumberFormat.getInstance();
        if (userWeblogEntries != null && userWeblogEntries.size() > 0) {
            try {
                for (PnWeblogEntry entry : userWeblogEntries) {
                    entry.setIsImportantSymbol(entry.getIsImportant() == 1 ? "!" : null);
                    entry.setPubDateString(userDateFormat.formatDate(entry.getPubTime(), "EEEEE, MMM d, yyyy"));
                    entry.setPubTimeString(userDateFormat.formatDate(entry.getPubTime(), "h:mm a"));

                    // Added condition to set UpdateTimeString only if edited
                    // for this blog entry.
                    if (!entry.getPubTime().equals(entry.getUpdateTime())) {
                        entry.setUpdateTimeString(userDateFormat.formatDate(entry.getUpdateTime(), "h:mm a, MMM dd, yyyy"));
                    } else {
                        entry.setUpdateTimeString("");
                    }

                    // setting short title for title view if title length gets
                    // more than 40 characters
                    if (StringUtils.isNotEmpty(entry.getTitle()) && entry.getTitle().length() > 40) {
                        entry.setShortTitle(entry.getTitle().substring(0, 40) + "...");
                    }
                    
                    if ((entry.getPnPerson().getPersonId().toString().equals(SessionManager.getUser().getID()))
                            || (StringUtils.isNotEmpty(spaceType) && spaceType.equals(Space.PROJECT_SPACE)
                                    && SessionManager.getUser().isSpaceAdministrator())) {
                        entry.setIsEditable(true);
                        entry.setIsDeletable(true);
                    }
                    if (entry.getPnWeblogComment() != null && !entry.getPnWeblogComment().isEmpty()) {
                        Iterator iterator = entry.getPnWeblogComment().iterator();
                        while (iterator.hasNext()) {
                            PnWeblogComment comment = (PnWeblogComment) iterator.next();
                            comment.setPostTimeString(userDateFormat.formatDate(comment.getPostTime(), "h:mm a"));
                            comment.setPostDateString(userDateFormat.formatDate(comment.getPostTime(), " MMM d, yyyy "));
                            if(SessionManager.getUser().getID().equals(comment.getPersonId().toString()) || SessionManager.getUser().getID().equals(comment.getEntryByPersonId().toString())
									|| (spaceType.equalsIgnoreCase(Space.PROJECT_SPACE) && SessionManager.getUser().isSpaceAdministrator())){
										comment.setIsDeletable(true);
							}
                        }
                    }
                    
                    boolean isUserDeleted = entry.getPnPerson().getUserStatus()!= null && entry.getPnPerson().getUserStatus().equals("Deleted");
                    boolean isProjectDeleted = entry.getPnProjectSpace() != null && entry.getPnProjectSpace().getRecordStatus().equals("D");
                    
                    // set user flag to false if user is deleted
                    entry.setCheckPerson(!isUserDeleted);
                    
                   	// set project flag to false if project is deleted
                    entry.setCheckProject(!(spaceType.equals(Space.PERSONAL_SPACE) && isProjectDeleted));
                    
                    List<PnWeblogEntryAttribute> pnWeblogEntryAttributes = blogProvider.getWeblogEntryAtributesByEntryId(entry.getWeblogEntryId());
                    if (CollectionUtils.isNotEmpty(pnWeblogEntryAttributes) && pnWeblogEntryAttributes.size() > 0) {
                        String objectId = null;
                        String objectType = null;
                        String objectName = null;
                        String objectAbbr = null;
                        String workSubmitted = null;
                        String changedEstimate = null;
                        String multipleWorkSubmitted = null;
                        
                        for (PnWeblogEntryAttribute attribute : pnWeblogEntryAttributes) {
                            if (StringUtils.isNotEmpty(attribute.getName())
                                    && StringUtils.isNotEmpty(attribute.getValue())) {
                                if (attribute.getName().contains("Id")) {
                                    objectType = attribute.getName().replaceAll("Id", "");
                                    objectId = attribute.getValue();
                                } else if (attribute.getName().equals(ObjectType.DOCUMENT)
                                        || attribute.getName().equals(ObjectType.TASK)
                                        || attribute.getName().contains(ObjectType.FORM + "_")
                                        || attribute.getName().equals(ObjectType.WIKI)) {
                                    objectName = attribute.getValue();
                                    if (attribute.getName().contains(ObjectType.FORM + "_")) {
                                        objectAbbr = StringUtils.capitalize(attribute.getName().replace(ObjectType.FORM + "_", ""));
                                    }
                                } else if (attribute.getName().equals("workSubmitted")) {
                                    workSubmitted = attribute.getValue();
                                } else if (attribute.getName().equals("changedEstimate")) {
                                    changedEstimate = attribute.getValue();
                                } else if(attribute.getName().equals("multipleWorkSubmitted")){
                                	multipleWorkSubmitted = attribute.getValue();
                                }
                            }
                        }
                        entry.setContextUrl("javascript:checkAndRedirect("+ entry.getWeblogEntryId() +", "+ objectId +", '"+ objectType +"', '"+ objectName +"', '"+ isUserDeleted +"', '"+ isProjectDeleted +"')");
                        if (StringUtils.isNotEmpty(objectType) && objectType.equals(ObjectType.DOCUMENT)) {
                            entry.setContextName(StringUtils.capitalize(ObjectType.DOCUMENT));
                        } else if (StringUtils.isNotEmpty(objectType) && objectType.equals(ObjectType.TASK)) {
                            entry.setContextName(StringUtils.capitalize(ObjectType.TASK));
                        } else if (StringUtils.isNotEmpty(objectType) && objectType.equals(ObjectType.FORM + "Data")) {
                            setFormContext(entry, objectId, objectName, spaceType, objectAbbr, isUserDeleted, isProjectDeleted);
                        } else if (StringUtils.isNotEmpty(objectType) && objectType.equals(ObjectType.MEETING)) {
                        	entry.setContextName(StringUtils.capitalize(ObjectType.MEETING));
                        } else if (StringUtils.isNotEmpty(objectType) && objectType.equals(ObjectType.WIKI)) {
                        	entry.setContextName(StringUtils.capitalize(ObjectType.WIKI));
                        }
                        entry.setContextValue(objectName);
                        
                        if (StringUtils.isNotEmpty(workSubmitted)) {
                            entry.setWorkSubmitted(numberFormat.formatNumber(Double.valueOf(workSubmitted), 0, 2));
                        }
                        if (StringUtils.isNotEmpty(changedEstimate)) {
                            entry.setChangedEstimate(changedEstimate);
                        }
                        if(StringUtils.isNotEmpty(multipleWorkSubmitted)) {
                        	entry.setMultipleWorkSubmitted(multipleWorkSubmitted);
                        }
                        // setting short context for title view if context
                        // length gets more than 40 characters
                        if (StringUtils.isNotEmpty(entry.getContextValue()) && entry.getContextValue().length() > 40) {
                            entry.setShortContext(entry.getContextValue().substring(0, 40) + "...");
                        }
                    }
                    
                    // this for displaying title on blogit popup when title
                    // enter by user empty
                    if (StringUtils.isEmpty(entry.getTitle())) {
                        String text = entry.getText().replaceAll("\\<.*?>", "").replaceAll("/&[#]*[\\w|\\d]*;/g", "").replaceAll("&nbsp;", "");
                        entry.setTitle(text.length() > 40 ? text.substring(0, 40) + "..." : text);
                    }
                    entry.setTitle(entry.getTitle().replaceAll(" &amp; ", " & "));
                }
            } catch (Exception e) {
                log.error("Error occurred while setting flags of view blog page : " + e.getMessage());
            }
        }
        return userWeblogEntries;
    }
    
    @Autowired
    private IPnDocumentService documentService;
    
    
    
    /**
     * Set the context link of document
     * @param entry
     * @param objectId
     * @param objectName
     * @param spaceType
     * @param isUserDeleted
     * @param isProjectDeleted
     */
    public String getDocumentContext(String entryId, String objectId, String objectName, String spaceType, boolean isUserDeleted, boolean isProjectDeleted) {
    	String documentURL = StringUtils.EMPTY;
        if (StringUtils.isNotEmpty(objectId)) {
            try {
                
                PnDocument document = documentService.getDocumentDetailsById(Integer.parseInt(objectId));
                if (document != null && StringUtils.isNotEmpty(document.getDocName())) {
                    objectName = document.getDocName();
                    if (!document.getRecordStatus().equals("D") && !isUserDeleted) {
                        if (spaceType.equals(Space.PERSONAL_SPACE) && isProjectDeleted) {
                        	documentURL = "showObjectDeletedError('Project', '" + entryId + "', 'doc');";
                        } else {
                        	documentURL = "self.location = '"+ URLFactory.makeURL(objectId, ObjectType.DOCUMENT, true) +"'";
                        }
                    }

                }
            } catch (Exception e) {
                log.error("Error occurred while fromatting document details by documentId: " + e.getMessage());
            }
        }
        return documentURL;
    }

    @Autowired
    private IPnTaskService taskService;
    
    /**
     * Set the context link of task
     * @param entry
     * @param objectId
     * @param objectName
     * @param spaceType
     * @param isUserDeleted
     * @param isProjectDeleted
     */
    public String getTaskContext(String entryId, String objectId, String objectName, String spaceType, boolean isUserDeleted, boolean isProjectDeleted) {
    	String taskURL = StringUtils.EMPTY;
        if (StringUtils.isNotEmpty(objectId)) {
            try {
                PnTask task = (taskService.getTaskDetailsById(Integer.parseInt(objectId)));
                if (task != null && StringUtils.isNotEmpty(task.getTaskName()) && !isUserDeleted) {
                    if (spaceType.equals(Space.PERSONAL_SPACE) && isProjectDeleted) {
                    	taskURL = "showObjectDeletedError('Project', '" + entryId + "', 'task');";
                    } else {
                        objectName = task.getTaskName();
                        taskURL = "self.location = '"+ URLFactory.makeURL(objectId, ObjectType.TASK, true) +"'";
                    }
                }
            } catch (Exception e) {
                log.error("Error occurred while fromatting task details by taskid: " + e.getMessage());
            }
        }
        return taskURL;
    }

    
    /**
     * Set the context link of form
     * @param entry
     * @param objectId
     * @param objectName
     * @param spaceType
     * @param objectAbbr
     * @param isUserDeleted
     * @param isProjectDeleted
     */
    public void setFormContext(PnWeblogEntry entry, String objectId, String objectName, String spaceType, String objectAbbr, boolean isUserDeleted, boolean isProjectDeleted) {
        int formDataId = 0;
        if (StringUtils.isNotEmpty(objectId)) {
            FormData formData = new FormData();
            try {
                formData.load(objectId);
                if (formData != null && StringUtils.isNotEmpty(formData.getName())) {
                    objectName = formData.getName();
                    formDataId = formData.getSeqNum();
                    if (!formData.getRecordStatus().getID().equals("D")
                            && formData.getForm().getStatusName().equals(PropertyProvider.get("prm.form.designer.activate.form.status.inuse.value"))
                            && !isUserDeleted) {
                    	if (!isAccessAllowed(SessionManager.getUser().getCurrentSpace(), Module.FORM, net.project.security.Action.CREATE, SessionManager.getUser())){
                        	entry.setContextUrl("javascript:showAccessDeniedError('" + entry.getWeblogEntryId() + "');");	
                        }else if (spaceType.equals(Space.PERSONAL_SPACE) && isProjectDeleted) {
                            entry.setContextUrl("javascript:showObjectDeletedError('Project', '" + entry.getWeblogEntryId() + "', 'form');");
                        } else {
                            entry.setContextUrl(URLFactory.makeURL(objectId, ObjectType.FORM_DATA, true));
                        }
                    } else if (isUserDeleted) {
                        entry.setContextUrl("javascript:showObjectDeletedError('Member', '" + entry.getWeblogEntryId() + "', '');");
                    } else {
                    	entry.setContextUrl("javascript:showErrorForFormContextLink('" + entry.getWeblogEntryId() + "');");
                    }
                }
            } catch (Exception Ex) {
                log.error("Exception occurred while loading form data : " + Ex.getMessage());
            }
        }
        entry.setContextName(objectAbbr + "-" + formDataId);
        entry.setContextValue(objectName);
    }
    
    /**
     * Set the context link of meeting
     * @param entry
     * @param objectId
     * @param objectName
     * @param spaceType
     * @param isUserDeleted
     * @param isProjectDeleted
     */
    public String getMeetingContext(String entryId, String objectId, String objectName, String spaceType, boolean isUserDeleted, boolean isProjectDeleted) {
    	String meetingURL = StringUtils.EMPTY;
        if (StringUtils.isNotEmpty(objectId)) {
            Meeting meeting = new Meeting();
            meeting.setID(objectId);
            try {
                meeting.load();
                if (meeting != null && StringUtils.isNotEmpty(meeting.getName())) {
                    objectName = meeting.getName();
                    if (!meeting.getRecordStatus().getID().equals("D") && !isUserDeleted) {
                        if (spaceType.equals(Space.PERSONAL_SPACE) && isProjectDeleted) {
                        	meetingURL = "showObjectDeletedError('Project', '" + entryId + "', 'meeting');";
                        } else {
                        	meetingURL = "self.location = '"+ URLFactory.makeURL(objectId, ObjectType.MEETING, true) +"'";
                        }
                    }
                }
            }catch (Exception Ex) {
                log.error("Exception occurred while loading meeting data : " + Ex.getMessage());
            }
        }
        return meetingURL;
    }
    
    @Autowired
    private IPnWikiPageService wikiPageService;
    
    /**
     * Set the context link of wiki page
     * @param entry
     * @param objectId
     * @param objectName
     * @param spaceType
     * @param isUserDeleted
     * @param isProjectDeleted
     */
    public String getWikiContext(String entryId, String objectId, String objectName, String spaceType, boolean isUserDeleted, boolean isProjectDeleted) {
    	String wikiURL = StringUtils.EMPTY;
        if (StringUtils.isNotEmpty(objectId)) {
            PnWikiPage wikiPage =  wikiPageService.get(Integer.valueOf(objectId));
            try {
                if (wikiPage != null && StringUtils.isNotEmpty(wikiPage.getPageName())) {
                    objectName = wikiPage.getPageName();
                    if (!wikiPage.getRecordStatus().equals("D") && !isUserDeleted) {
                        if (spaceType.equals(Space.PERSONAL_SPACE) && isProjectDeleted) {
                        	wikiURL = "showObjectDeletedError('Project', '" + entryId + "', 'wiki');";
                        } else {
                        	wikiURL = "self.location = '"+ SessionManager.getJSPRootURL()+"/wiki/" + WikiManager.getPagesToCall(wikiPage) +"'";
                        }
                    }
                }
            }catch (Exception Ex) {
                log.error("Exception occurred while loading meeting data : " + Ex.getMessage());
            }
        }
        return wikiURL;
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.service.IBlogViewProvider#checkObjectStatusAndReturnObjectURL(java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
     */
    public TextStreamResponse checkObjectStatusAndReturnObjectURL(String entryId, String objectId, String objectType, String objectName, String spaceType, boolean isUserDeleted, boolean isProjectDeleted){
    	String returnValue = StringUtils.EMPTY;
    	if(StringUtils.isNotEmpty(objectId) && StringUtils.isNotEmpty(objectType) && StringUtils.isNotEmpty(spaceType)){
    		if(objectType.equalsIgnoreCase(ObjectType.DOCUMENT)){
    			returnValue = getDocumentContext(entryId, objectId, objectName, spaceType, isUserDeleted, isProjectDeleted);
    		} else if(objectType.equalsIgnoreCase(ObjectType.TASK)){
    			returnValue = getTaskContext(entryId, objectId, objectName, spaceType, isUserDeleted, isProjectDeleted);
    		} else if(objectType.equalsIgnoreCase(ObjectType.MEETING)){
    			returnValue = getMeetingContext(entryId, objectId, objectName, spaceType, isUserDeleted, isProjectDeleted);
    		} else if(objectType.equalsIgnoreCase(ObjectType.WIKI)){
    			returnValue = getWikiContext(entryId, objectId, objectName, spaceType, isUserDeleted, isProjectDeleted);
    		}
    	}
    	return new TextStreamResponse("text/plain", StringUtils.isNotEmpty(returnValue) ? returnValue : "showErrorForContextLink('"+ entryId +"');");
    }
    
    /**
	 * Check for access allowed for specified module and action in specified space for specified user.
	 * @param spaceId space identifier
	 * @return true or false
	 */
    private boolean isAccessAllowed(Space space, int module, int action, User user){
        SecurityProvider checkSecurityProvider = new SecurityProvider();
        checkSecurityProvider.setUser(user);
        checkSecurityProvider.setSpace(space);
        return checkSecurityProvider.isActionAllowed(null, module, action);
    }

	public void setDocumentService(IPnDocumentService documentService) {
		this.documentService = documentService;
	}

	public void setTaskService(IPnTaskService taskService) {
		this.taskService = taskService;
	}

	public void setWikiPageService(IPnWikiPageService wikiPageService) {
		this.wikiPageService = wikiPageService;
	}
    
    
    
}
