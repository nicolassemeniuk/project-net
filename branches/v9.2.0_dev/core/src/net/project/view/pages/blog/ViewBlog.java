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

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.PnWebloggerException;
import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.gui.history.BusinessLevel;
import net.project.gui.history.History;
import net.project.gui.history.ProjectLevel;
import net.project.hibernate.constants.WeblogConstants;
import net.project.hibernate.model.PnAddress;
import net.project.hibernate.model.PnClass;
import net.project.hibernate.model.PnObjectType;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.model.PnWeblog;
import net.project.hibernate.model.PnWeblogComment;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.service.IBlogProvider;
import net.project.hibernate.service.IBlogViewProvider;
import net.project.hibernate.service.IPnAddressService;
import net.project.hibernate.service.IPnClassService;
import net.project.hibernate.service.IPnObjectTypeService;
import net.project.hibernate.service.IPnProjectSpaceService;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpace;
import net.project.project.ProjectSpaceBean;
import net.project.security.SecurityProvider;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.PersonalSpaceBean;
import net.project.space.Space;
import net.project.space.SpaceManager;
import net.project.space.SpaceTypes;
import net.project.util.DateFormat;
import net.project.util.InvalidDateException;
import net.project.util.StringUtils;
import net.project.util.TextFormatter;
import net.project.view.pages.base.BasePage;
import net.project.view.pages.resource.management.GenericSelectModel;
import net.project.xml.XMLUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.runtime.ComponentEventException;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.util.TextStreamResponse;
import org.json.JSONStringer;
import org.slf4j.Logger;

/**
 * @author
 */
public class ViewBlog extends BasePage implements Serializable {

	private static Logger log = logger;

	private String blogName;
	
	private String linkHrefValue;

	private String dashboard;

	private String postNewEntryLink;

	private String importantSymbolTooltip;

	private String taskListLabel;

	private String taskListFirstOption;

	private String tasksNotExistOption;

	private String entryPostedAt;

	private String entryPopupTitle;
	
	private String editEntryPopupTitle;

	private String commentPopupTitle;

	private String entryPopupCloseLabel;
	
	@Inject
	private IBlogProvider blogProvider;
	
	@Inject
	private IBlogViewProvider blogViewProvider;

	@Persist
	private PnWeblog userWeblog;

	private PnWeblogEntry pnWeblogEntry;

	private PnWeblogComment pnWeblogComment;
	
	@Persist
	private List<PnWeblogEntry> userWeblogEntries;

	private boolean entriesMoreThanLimit = false;

	@InjectPage
	private AddWebLogEntry addWeblogEntry;

	@Inject
	private RequestGlobals requestGlobals;

	@Persist
	private Integer spaceId;
	
	@Persist
	private Integer otherUsersSpaceId;

	@Persist
	private Integer userId;

	@Persist
	private Integer moduleId;

	@Persist
	private String spaceType;

	@Property
	@Persist
	private ProjectSpaceBean project;

	@Persist
	private boolean linkToPersonSpace;

	@Persist
	private boolean webblogEntryAdded;

	@Persist
	private boolean webblogCommentAdded;

	@Persist
	private String date;
	
	private String weblogDateFormatPattern;

	private String startEndDateFormatPattern;
	
	private DateFormat userDateFormat;
	
	@Persist
	private boolean moreFlag;

	@Persist
	private String fromDate;

	@Persist
	private String toDate;

	private Date currentDate;
	
	private Date previousDate;

	private boolean blogEntriesExist = false;
	
	private boolean blogEntriesLoadedForObject = false;
	
	private String validationMessageForBlogEntry;
	
	private String validationMessageForBlogHtmlTagEntry;
	
	private String validationMessageForBlogComment;
	
	private String message;
	
	private boolean getBlogEntriesForTeamMember = false;
	
	private String filterResultMessage;

	private boolean isSpaceAdmin = false;

	@Persist
	private String imagePath;

	@Persist
	private PnAddress pnAddress;
	
	@InjectPage
	private BlogEntries blogEntries;
	
	private String blogEntryIds;
	
	@Inject
	private PropertyAccess access;
	
	private GenericSelectModel<PnProjectSpace> projectBeans;
	
	private List<PnProjectSpace> visibleProjectsList;
	
	private PnProjectSpace visibleProjectSpace;
	
	private String blogHeaderStyle;
	
	private String itemTypeList;
	
	private String teamMemberId;
	
	@Inject
	private IPnClassService pnClassService;
	
	private String confirmDeleteBlogEntryMessage;  
	
	private boolean showSkypeStatus; 
	
    @Persist
    private int totalBlogCount;
    
    private boolean blogEnabled;
    
    private String blogPopupUserDate;
    
	private boolean actionsIconEnabled;
    
    private String blogItImageOn;
    
    private String blogItImageOver;
    
    private String showTitlesOnlyImageOn;
    
    private String showTitlesOnlyImageOver;
    
    private String myProfileImageOn;
    
    private String myProfileImageOver;
    
    private String showPictureOn;
    
    private String showPictureOver;
    
    private HttpServletRequest request;
    
    private Boolean projectLogo;
    
    private String parentProjectURL;
    
    private String weblogEntryId;
    
	private String taskID;
    
	private String objectType;

    @Property
	private String userEmail;
    
    private enum BlogActions {
		TEAMMORE, PROJECTBLOG, PERSONALBLOG, SHOW_BLOG_ENTRIES_FOR_OBJECT, 
		FILTER_BLOG_ENTRIES, GET_LAST_BLOG_ENTRY, LOAD_BLOG_ENTRIES, 
		GET_MODULEID_FROM_SPACE, GET_LAST_BLOGIT_DATE_LINK, SHOW_ENTRY, LOADBLOGENTRIES, CHECK_AND_REDIRECT;
		
		public static BlogActions get( String v ) {
            try {
                return BlogActions.valueOf( v.toUpperCase() );
            } catch( Exception ex ) { }
            return null;
         }
	}

	/**
	 * Initialize values
	 */
	public void initializeTokens() {
		try {
			dashboard = PropertyProvider.get("prm.global.tool.dashboard.name");
			postNewEntryLink = PropertyProvider.get("prm.blog.viewblog.postnewentry.link");
			importantSymbolTooltip = PropertyProvider.get("prm.blog.viewblog.importantsymbol.tooltip");
			taskListLabel = PropertyProvider.get("prm.blog.viewblog.dropdownselecttaskshere.label");
			taskListFirstOption = PropertyProvider.get("prm.blog.viewblog.dropdownselecttaskshere.firstoptionvalue");
			tasksNotExistOption = PropertyProvider
					.get("prm.blog.viewblog.dropdownselecttaskshere.tasksnotexistoptionvalue");
			entryPostedAt = PropertyProvider.get("prm.blog.viewblog.entrypostedat.label");
			entryPopupTitle = PropertyProvider.get("prm.blog.viewblog.entrypopup.title");
			editEntryPopupTitle = PropertyProvider.get("prm.blog.viewblog.editentrypopup.title");
			commentPopupTitle = PropertyProvider.get("prm.blog.viewblog.commentpopup.title");
			entryPopupCloseLabel = PropertyProvider.get("prm.blog.viewblog.entrypopup.closelabel");
			weblogDateFormatPattern = "EEE, MMM dd, yyyy";
			startEndDateFormatPattern = "dd-MM-yyyy";
			userDateFormat = SessionManager.getUser().getDateFormatter();
			validationMessageForBlogEntry = PropertyProvider.get("prm.blog.addweblogentry.validation.message");
			validationMessageForBlogHtmlTagEntry = PropertyProvider.get("prm.blog.addweblogentry.htmlvalidation.message");
			validationMessageForBlogComment = PropertyProvider.get("prm.blog.addweblogentrycomment.validation.message");
			confirmDeleteBlogEntryMessage = PropertyProvider.get("prm.blog.viewblog.confirmdeleteblogentry.message");
			showSkypeStatus = PropertyProvider.getBoolean("prm.global.skype.isenabled");
			Space currentSpace = SessionManager.getUser().getCurrentSpace();
			blogEnabled = PropertyProvider.getBoolean("prm.blog.isenabled")
					&& currentSpace.isTypeOf(SpaceTypes.PERSONAL_SPACE)
					|| currentSpace.isTypeOf(SpaceTypes.PROJECT_SPACE);
            blogPopupUserDate = userDateFormat.formatDate(Calendar.getInstance().getTime(), "EEEEE, MMM dd, yyyy");
            actionsIconEnabled = PropertyProvider.getBoolean("prm.global.actions.icon.isenabled");
            blogItImageOn = PropertyProvider.get("all.global.toolbar.standard.blogit.image.on");
            blogItImageOver = PropertyProvider.get("all.global.toolbar.standard.blogit.image.over");
            showTitlesOnlyImageOn = PropertyProvider.get("all.global.toolbar.standard.showtitles.image.on");
            showTitlesOnlyImageOver = PropertyProvider.get("all.global.toolbar.standard.showtitles.image.over");
            myProfileImageOn = PropertyProvider.get("all.global.toolbar.standard.myprofile.image.on");
            myProfileImageOver = PropertyProvider.get("all.global.toolbar.standard.myprofile.image.over");
            showPictureOn = PropertyProvider.get("all.global.toolbar.standard.showpicture.image.on");
            showPictureOver = PropertyProvider.get("all.global.toolbar.standard.showpicture.image.over");
            request = requestGlobals.getHTTPServletRequest();
		} catch (Exception e) {
			log.error("Error occured while getting property tokens : " + e.getMessage());
		}
	}

	/**
	 * Method called on page activation
	 */
	void onActivate() {
		if (net.project.security.SessionManager.getUser() == null) {
			throw new IllegalStateException("User is null");
		}
        initializeTokens();
		Calendar cal = Calendar.getInstance();
		// initialize date variable with current date & format with weblog date format
		date = userDateFormat.formatDate(cal.getTime(), weblogDateFormatPattern);		
		cal.add(Calendar.DATE, 1);
		currentDate = cal.getTime();
		cal.add(Calendar.DATE, -(WeblogConstants.DATE_RANGE_FOR_ARCHIVES + 1));
		previousDate = cal.getTime();
		
		// setting style class for blog header depending current users space
		if(SessionManager.getUser().getCurrentSpace().getType().equalsIgnoreCase(Space.PERSONAL_SPACE)) {
			blogHeaderStyle = "leftheading-" + Space.PERSONAL_SPACE;	
		} else if(SessionManager.getUser().getCurrentSpace().getType().equalsIgnoreCase(Space.PROJECT_SPACE)) {		
			blogHeaderStyle = "leftheading-" + Space.PROJECT_SPACE;
		} else if(SessionManager.getUser().getCurrentSpace().getType().equalsIgnoreCase(Space.BUSINESS_SPACE)) {
			blogHeaderStyle = "leftheading-" + Space.BUSINESS_SPACE;
		} else if(SessionManager.getUser().getCurrentSpace().getType().equalsIgnoreCase(Space.FINANCIAL_SPACE)) {
			blogHeaderStyle = "leftheading-" + Space.FINANCIAL_SPACE;
		}		
		
		// if page called after adding weblog entry or weblog entry comment
		// then call method with parameters spaceId, userId, spaceType and moduleId
		if (webblogEntryAdded || webblogCommentAdded) {
			webblogEntryAdded = false;
			webblogCommentAdded = false;
			getBlogForSpace(spaceId, userId, spaceType, moduleId);
		}
		if(userWeblog != null){
			setBlogName(userWeblog.getName());
		}
	}
	
	/**
	 * Method called on page activation with parameters
	 */
	void onActivate(String weblogEntryId, String taskId, String taskType) {
		if(StringUtils.isNotEmpty(taskType) && taskType.equals(ObjectType.TASK)){
			initializeTokens();
			setWeblogEntryId(weblogEntryId);
			setTaskID(taskId);
			setObjectType(taskType);
			setBlogParameters();
			getBlogForSpace(getSpaceId(), getUserId(), getSpaceType(), getModuleId());
		}
	}

	/**
	 * Setting blog parameters using session values
	 */
	void setBlogParameters(){
		setUserId(Integer.parseInt(SessionManager.getUser().getID()));
		setSpaceId(Integer.parseInt(SessionManager.getUser().getCurrentSpace().getID()));
		setSpaceType(SessionManager.getUser().getCurrentSpace().getSpaceType().getName().toLowerCase());
		setSpaceType(getSpaceType().equalsIgnoreCase("personal") ? Space.PERSONAL_SPACE : getSpaceType());
		setModuleId(Module.getModuleForSpaceType(getSpaceType()));
	}
	
	/**
	 * Method called on page activation with single parameter
	 * @param action to perform
	 */
	Object onActivate(String action){
		initializeTokens();
		//setBlogParameters();
		BlogActions blogAction = BlogActions.get( action );
		if (StringUtils.isNotEmpty(action)) {
			if (blogAction == BlogActions.TEAMMORE) {
				setMoreFlag(true);
				getBlogForSpace(getSpaceId(), getUserId(), getSpaceType(), getModuleId());
			} else if (blogAction == BlogActions.PROJECTBLOG) {				
				return createProjectBlog();
			} else if (blogAction == BlogActions.PERSONALBLOG) {
				if (blogProvider.createBlog(new User(SessionManager.getUser().getID()), getSpaceType(), getSpaceId(), null) != null) {
					return new TextStreamResponse("text/plain", "true");
				}
				return new TextStreamResponse("text/plain", "false");
			} else if (blogAction == BlogActions.SHOW_BLOG_ENTRIES_FOR_OBJECT) {
				try {
					String objectType = null; //request.getParameter("objectType");
					String objectId = request.getParameter("objectId");
					setUserId(Integer.parseInt(SessionManager.getUser().getID()));
					if (StringUtils.isNotEmpty(objectId)) {
						PnObjectType objType = ServiceFactory.getInstance().getPnObjectTypeService().getObjectTypeByObjectId(Integer.parseInt(objectId));
						objectType = objType.getObjectType();
						if (objectType.equalsIgnoreCase(ObjectType.PROJECT)) {
							setSpaceId(Integer.parseInt(objectId));
							setSpaceType(Space.PROJECT_SPACE);
							setModuleId(Module.getModuleForSpaceType(getSpaceType()));
						}else if(objectType.equals(ObjectType.BUSINESS) || objectType.equals(ObjectType.APPLICATION) || 
                                objectType.equals(ObjectType.METHODOLOGY) || objectType.equals(ObjectType.CONFIGURATION)){
							objectId = SessionManager.getUser().getID();
							setSpaceId(Integer.parseInt(objectId));
							setSpaceType(Space.PERSONAL_SPACE);
							setModuleId(Module.getModuleForSpaceType(getSpaceType()));
						} else {
							setSpaceId(Integer.parseInt(SessionManager.getUser().getCurrentSpace().getID()));
							setSpaceType(SessionManager.getUser().getCurrentSpace().getSpaceType().getName().toLowerCase());
							setSpaceType(getSpaceType().equalsIgnoreCase("personal") ? Space.PERSONAL_SPACE : getSpaceType());
							setModuleId(Module.getModuleForSpaceType(getSpaceType()));
						}
						setUserWeblogEntries(blogProvider.getFilteredWeblogEntries(null, null, null, Integer.valueOf(objectId), null, null, WeblogConstants.STATUS_PUBLISHED, 0, 21, null, false, null, false));
						if(StringUtils.isNotEmpty(request.getParameter("entriesForPopup")) && request.getParameter("entriesForPopup").equals("true")){
                            if(userWeblogEntries != null && userWeblogEntries.size() > 0){
                                if (userWeblogEntries.size() > 20){
                                    userWeblogEntries = userWeblogEntries.subList(0, 20);
                                    blogEntries.setMessage(userWeblogEntries.size() + " blog entries shown");
                                    blogEntries.setIsMoreEntriesToSee(true);
                                    blogEntries.setMorePostUrl(getJSPRootURL()+"/blog/view/"+getSpaceId()+"/"+SessionManager.getUser().getID()+"/"+getSpaceType()+"/"+getModuleId()+"?module="+getModuleId());
                                } else {
                                    blogEntries.setMessage("");
                                    blogEntries.setIsMoreEntriesToSee(false);
                                    blogEntries.setMorePostUrl("");
                                }
                                blogEntries.setTotalNoOfBlogEntry(userWeblogEntries.size());
                                blogEntries.setUserWeblogEntries(blogViewProvider.getFormattedBlogEntries(userWeblogEntries, getJSPRootURL(), spaceType, userDateFormat));
								blogEntries.setLinkToPersonSpace(objectType.equalsIgnoreCase(ObjectType.PERSON));	
								// setting blog entry flags
								blogEntries.setShowCommentLink(true);
								blogEntries.setShowEditLink(false);
								blogEntries.setShowExpandCollapseImage(false);
								blogEntries.setShowPersonImage(false);
								blogEntries.setBlogCommentDivClass("comment-entry");
								blogEntries.setBlogPostDivClass("post-body");
								blogEntries.setIsAssignmentPage(false);
                                blogEntries.setPagingEnabled(false);
                                blogEntries.setWeblogEntryContentsClass("");
                            } else {
                                blogEntries.setTotalNoOfBlogEntry(0);
                                blogEntries.setMessage("Blog entries not found.");
                                blogEntries.setUserWeblogEntries(null);
                                blogEntries.setIsMoreEntriesToSee(false);
                                blogEntries.setMorePostUrl("");
                            }
							return blogEntries;
						}
						//setUserWeblog(blogProvider.getWeblogBySpaceId(getSpaceId()));
						setBlogEntriesLoadedForObject(true);					
						getBlogForSpace(getSpaceId(), getUserId(), getSpaceType(), getModuleId());	
					}	
				}catch (Exception e) {
					log.error("Error occurred while getting blog entries by object id in view blog page : "
							+ e.getMessage());
				}						
			} else if (blogAction == BlogActions.FILTER_BLOG_ENTRIES) {
				 blogEntriesLoadedForObject = Boolean.parseBoolean(request.getParameter("blogEntriesLoadedForObject"));
					if (blogEntriesLoadedForObject){
						if (CollectionUtils.isNotEmpty(userWeblogEntries)) {
							blogEntries.setUserWeblogEntries(userWeblogEntries);
							blogEntries.setLinkToPersonSpace(getLinkToPersonSpace());
							blogEntries.setShowEditLink(true);
							blogEntries.setShowExpandCollapseImage(true);
							blogEntries.setShowPersonImage(true);
							blogEntries.setBlogCommentDivClass("comment-entry");
							blogEntries.setBlogPostDivClass("post-body");
							blogEntries.setBlogEntriesLoadedForObject(false);							
							blogEntries.setMessage(userWeblogEntries.size() == 1 ? "1 blog entry found" : userWeblogEntries.size()
									+ " blog entries found");
							blogEntries.setShowCommentLink(true);
							blogEntries.setIsAssignmentPage(false);
                            blogEntries.setWeblogEntryContentsClass("");
							return blogEntries;
						}
					} else {
						String spaceType = "";
						if(StringUtils.isNotEmpty(request.getParameter("spaceType"))){
							spaceType = request.getParameter("spaceType");
							if(!this.spaceType.equals(spaceType) && spaceType.equals("project")){
								setUserWeblog(blogProvider.getWeblogBySpaceId(getSpaceId()));
							}
						}
						String memberId = request.getParameter("memberId");
						String objectId = request.getParameter("objectId");
						String startDateString = request.getParameter("startDate");
						String endDateString = request.getParameter("endDate");
						String itemType =  request.getParameter("itemType");
						boolean showTimeReportedEntries = StringUtils.isEmpty(request.getParameter("showTimeReportedEntries")) ? false : Boolean.parseBoolean(request.getParameter("showTimeReportedEntries"));
						boolean showImportantBlogEntries = StringUtils.isEmpty(request.getParameter("showImportantBlogEntries")) ? false : Boolean.parseBoolean(request.getParameter("showImportantBlogEntries"));
						int post = StringUtils.isEmpty(request.getParameter("posts")) ? 20 : Integer.parseInt(request.getParameter("posts"));
						int offset = StringUtils.isEmpty(request.getParameter("offset")) ? 0 : Integer.parseInt(request.getParameter("offset"));
						Date startDate = null;
						Date endDate = null;
						double totalWorkDone = 0.0;
                        if(userWeblog != null){
						try {
                            if(StringUtils.isNotEmpty(startDateString) && StringUtils.isNotEmpty(endDateString)){
								startDate = userDateFormat.parseDateString(startDateString, "dd/MM/yyyy");
								endDate = userDateFormat.parseDateString(endDateString, "dd/MM/yyyy");
							}
                            
                            // to get the count of blog entries according to filter
                            // so this method should be changed in parallel to filter method if any new filter added
                            totalBlogCount = blogProvider.getCountOfBlogEntries(userWeblog.getWeblogId(),
                                    StringUtils.isNotEmpty(memberId) ? new Integer(memberId) : null,
                                    spaceType.equals(Space.PERSONAL_SPACE) ? userId : null, // pass userId only for person space otherwise set to null
                                    StringUtils.isNotEmpty(objectId) ? new Integer(objectId) : null, 
                                    startDate, endDate, WeblogConstants.STATUS_PUBLISHED, showTimeReportedEntries ,itemType, showImportantBlogEntries, null, Integer.valueOf(getUser().getID()));
                            // to get the filtered entries
							setUserWeblogEntries(blogProvider.getFilteredWeblogEntries(userWeblog.getWeblogId(), 
									StringUtils.isNotEmpty(memberId) ? new Integer(memberId) : null, 
                                    spaceType.equals(Space.PERSONAL_SPACE) ? userId : null, // pass userId only for person space otherwise set to null
									StringUtils.isNotEmpty(objectId) ? new Integer(objectId) : null, 
									startDate, endDate, WeblogConstants.STATUS_PUBLISHED, offset, post, null, showTimeReportedEntries ,itemType, showImportantBlogEntries, Integer.valueOf(getUser().getID())));
                            
                            blogEntries.setLinkToPersonSpace(spaceType.equals(Space.PERSONAL_SPACE));
                            blogEntries.setShowPrevLink(offset != 0);
                            blogEntries.setPagingEnabled(totalBlogCount > 0);
                            blogEntries.setPosts(post);
                            
                            if (totalBlogCount > 0) {
                                blogEntries.setTotalNoOfBlogEntry(totalBlogCount);
                                if(offset + 1 > totalBlogCount) {
                                    blogEntries.setOffsetForDisplay(totalBlogCount);
                                    blogEntries.setShowNextLink(false);
                                } else {
                                    blogEntries.setOffsetForDisplay(offset + 1);
                                    blogEntries.setShowNextLink(true);
                                }
                                if(offset + post >= totalBlogCount) {
                                    blogEntries.setRangeForDisplay(totalBlogCount);
                                    blogEntries.setShowNextLink(false);
                                } else {
                                    blogEntries.setRangeForDisplay(offset + post);
                                    blogEntries.setShowNextLink(true);
                                    blogEntries.setNextEntriesCount((totalBlogCount - (offset + post)) > post ? post : (totalBlogCount - (offset + post)));
                                }
                            }
                            
						} catch (NumberFormatException pnetEx1) {
							log.error("Error occurred while filtering blog entries by given criteria view blog page : "
									+ pnetEx1.getMessage());					
						} catch (PnWebloggerException pnetEx2) {
							log.error("Error occurred while filtering blog entries by given criteria view blog page : "
									+ pnetEx2.getMessage());
						} catch (InvalidDateException pnetEx3) {
							log.error("Error occurred while filtering blog entries by given criteria view blog page : "
									+ pnetEx3.getMessage());
						} catch(ComponentEventException e){
							log.error("Error occurred while filtering blog entries by given criteria view blog page : "
									+ e.getMessage());
						}
						if (CollectionUtils.isEmpty(userWeblogEntries)) {
							setMessage("Blog entries not found");
						} else {
							setUserWeblogEntries(blogProvider.getSortedBlogEntries(userWeblogEntries));
							//setMessage((blogEntries.getTotalNoOfBlogEntry() == 1 ? "1 blog entry found" : blogEntries.getTotalNoOfBlogEntry() + " blog entries found"));
						}
						// Blog Entries Content setting
						blogEntries.setMessage(getMessage());
						String memberName = "";
						blogEntries.setUserWeblogEntries(blogViewProvider.getFormattedBlogEntries(userWeblogEntries, getJSPRootURL(), getSpaceType(), DateFormat.getInstance()));
						if(!spaceType.equals(Space.PERSONAL_SPACE) && CollectionUtils.isNotEmpty(userWeblogEntries) && showTimeReportedEntries){
							for (PnWeblogEntry entry : getUserWeblogEntries()) {
								if (StringUtils.isNotEmpty(entry.getWorkSubmitted())) {
									memberName = entry.getPnPerson().getDisplayName().toString();
									try {
										totalWorkDone += Double.parseDouble(entry.getWorkSubmitted());
									} catch (Exception e) {
										log.error("Error occurred while calculating total work done  : " + e.getMessage());
									}
								}
							}
							if (totalWorkDone > 0) {
								if (StringUtils.isEmpty(memberId)) {
									memberName = "All members";
								}
                                
								Object[] arguments = { memberName, totalWorkDone,
										getUserWeblogEntries().get(getUserWeblogEntries().size() - 1).getPubTimeString(),
										getUserWeblogEntries().get(0).getPubTimeString() };
                                blogEntries.setTotalWorkDone(PropertyProvider.get("prm.blog.viewblog.totalworkdonebymemberson.message", arguments));

							} else {
								blogEntries.setTotalWorkDone("");
							}
						} else {
							blogEntries.setTotalWorkDone("");
						}
						
						blogEntries.setShowEditLink(true);
						blogEntries.setShowExpandCollapseImage(true);
						blogEntries.setShowPersonImage(true);
						blogEntries.setBlogCommentDivClass("comment-entry");
						blogEntries.setBlogPostDivClass("post-body");
						blogEntries.setShowCommentLink(true);
						blogEntries.setIsAssignmentPage(false);
                        blogEntries.setWeblogEntryContentsClass("weblogEntryContentsClass");
                        } else {
                            blogEntries.setMessage("Blog entries not found");
                        }
						return blogEntries;
					}
			} else if (blogAction == BlogActions.GET_LAST_BLOG_ENTRY) {
				String userId = request.getParameter("userId");
				if(StringUtils.isNotEmpty(userId)){
					try {
						PnWeblogEntry lastBlogEntry = blogProvider.getLastBlogEntryOfUser(Integer.parseInt(userId), Integer.valueOf(getUser().getID()));
						String blogEntryHTMLString = null;
						if (lastBlogEntry != null) {
							try {
								lastBlogEntry.setPubTimeString(userDateFormat.formatDate(lastBlogEntry.getPubTime(),
										WeblogConstants.WEBLOG_DATE_FORMAT_PATTERN));

                                lastBlogEntry.setIsImportantSymbol(lastBlogEntry.getIsImportant() == 1 ? "!" : null);

								blogEntryHTMLString = "<div class=\"blogdate\">"+ lastBlogEntry.getPubTimeString() + "</div>" 
									+ "<div class=\"blogentry-title\"> <label style=\"color: red; font-weight: bolder; font-size: x-large;\" title=\""
									+ importantSymbolTooltip + "\">"+ (lastBlogEntry.getIsImportant() == 0 ? "" : "!") 
									+ "</label><b>";
									if(StringUtils.isNotEmpty(lastBlogEntry.getTitle())){
										blogEntryHTMLString += lastBlogEntry.getTitle();
									} else {
										String text = lastBlogEntry.getText().replaceAll("\\<.*?>", "")
														.replaceAll("/&[#]*[\\w|\\d]*;/g", "").replaceAll("&nbsp;", "");
										blogEntryHTMLString += text.length() > 40 ? text.substring(0, 40)+"..." : text;
									}									 
									blogEntryHTMLString += "</b></div>"
									+ "<div class=\"blogentry-content\"><div id=\"weblogEntryContents\">" + lastBlogEntry.getText() + "</div></div>";
							} catch (Exception e) {
								log.error("Error occurred while generating html text for blog entries : " + e.getMessage());
							}
							return new TextStreamResponse("text/html", blogEntryHTMLString);
						}
					} catch (Exception e) {
						log.error("Error occurred while getting last blog entry : " + e.getMessage());
						return new TextStreamResponse("text/plain", "Error occurred while getting last blog entry");
					}
				}
			} else if(blogAction == BlogActions.GET_MODULEID_FROM_SPACE){
                try {
                    JSONStringer stringer = new JSONStringer();
                    stringer.object();
                    
                    if(StringUtils.isNotEmpty(request.getParameter("spaceType"))){
                        stringer.key("moduleId").value(String.valueOf(Module.getModuleForSpaceType(request.getParameter("spaceType"))));
                        stringer.key("userDate").value(blogPopupUserDate);
    				}else{
    					return new TextStreamResponse("text/plain","");
    				}
                    stringer.endObject();
                    return new TextStreamResponse("text/json",stringer.toString());
                } catch (Exception pnetEx) {
                    log.error("Error occurred while getting module id for space type "+pnetEx.getMessage());
                }
			} else if(blogAction == BlogActions.GET_LAST_BLOGIT_DATE_LINK){
				return getLastBlogEntryDetailsBySpace(request.getParameter("spaceId"));							
			} else if (blogAction == BlogActions.SHOW_ENTRY) {
				if (StringUtils.isNotEmpty(request.getParameter("id"))) {
					PnWeblogEntry entry = null;
					try {
						entry = blogProvider.getWeblogEntryDetail(Integer.parseInt(request.getParameter("id")));
					
						if (entry != null) {
							userWeblogEntries = new ArrayList<PnWeblogEntry>();
							userWeblogEntries.add(entry);
							userWeblogEntries = blogViewProvider.getFormattedBlogEntries(userWeblogEntries,
									getJSPRootURL(), getSpaceType(), DateFormat.getInstance());
							blogEntriesLoadedForObject = true;
							if(StringUtils.isNotEmpty(request.getParameter("spaceId")) && StringUtils.isNotEmpty(request.getParameter("userId"))) {
								setUserId(Integer.parseInt(request.getParameter("userId")));
								setSpaceId(Integer.parseInt(request.getParameter("spaceId")));
								setSpaceType(request.getParameter("spaceType"));
								setModuleId(Module.getModuleForSpaceType(getSpaceType()));
							} else {
								setBlogParameters();
							}
							getBlogForSpace(getSpaceId(), getUserId(), getSpaceType(), getModuleId());
						}
					} catch (NumberFormatException pnetEx) {
						log.error("Error occurred while parsing entry id "+pnetEx.getMessage());
						pnetEx.printStackTrace();
					} catch (PnWebloggerException pnetEx) {
						log.error("Error occurred while formatting blog entry details "+pnetEx.getMessage());
						pnetEx.printStackTrace();
					}
				}
			} else if (blogAction == BlogActions.LOADBLOGENTRIES) {
                String childObjectsList = StringUtils.isEmpty(request.getParameter("childObjectsList")) ? "" : request.getParameter("childObjectsList");
                int post = StringUtils.isEmpty(request.getParameter("posts")) ? 10 : Integer.parseInt(request.getParameter("posts"));
				int offset = StringUtils.isEmpty(request.getParameter("offset")) ? 0 : Integer.parseInt(request.getParameter("offset"));
				boolean isProjectPortfolio = Boolean.parseBoolean(request.getParameter("isProjectPortfolio"));
                if (StringUtils.isNotEmpty(request.getParameter("forObjectId"))) {
                    return loadBlogEntries(Integer.parseInt(request.getParameter("forObjectId")), childObjectsList, request.getParameter("workSpaceId"), request.getParameter("startDate"), request.getParameter("endDate"), offset, post, isProjectPortfolio);
                } else {
                    blogEntries.setMessage("Error occured while loading blog entries. ");
                    blogEntries.setUserWeblogEntries(null);
                    return blogEntries;
                }               
			} else if (blogAction == BlogActions.CHECK_AND_REDIRECT) {
				setSpaceType(SessionManager.getUser().getCurrentSpace().getSpaceType().getName().toLowerCase());
				setSpaceType(getSpaceType().equalsIgnoreCase("personal") ? Space.PERSONAL_SPACE : getSpaceType());				
                return checkAndRedirect(request.getParameter("weblogEntryId"), request.getParameter("objectId"), request.getParameter("objectType"), request.getParameter("objectName"), request.getParameter("isUserDeleted"), request.getParameter("isProjectDeleted"));
			}
		}
		return null;
	}
	
	/**
	 * Method which will be called on page activation with four parameters
	 * @param spaceId
	 * @param userId
	 * @param spaceType
	 * @param moduleId
	 */
	void onActivate(Integer spaceId, Integer userId, String spaceType, Integer moduleId){
		initializeTokens();
		getBlogForSpace(spaceId, userId, spaceType, moduleId);
	}
	
	/**
	 * Method to initialize page values before displaying on page activation with parameters
	 * 
	 * @param spaceId current space id
	 * @param userId user's id
	 * @param spaceType current space type e.g person, project
	 * @param moduleId id of the current module
	 */	
	void getBlogForSpace(Integer spaceId, Integer userId, String spaceType, Integer moduleId) {		
		setUserId(userId);
		setModuleId(moduleId);
		setSpaceType(spaceType);
		setSpaceId(spaceId);
		setTeamMemberId(request.getParameter("teamMemberId"));
		if(currentDate == null && previousDate == null){
			onActivate();
		}
		// user object from session
		User user = SessionManager.getUser();
		// for history
		BusinessLevel businessLevel = new BusinessLevel();
		businessLevel.setShow(false);
		ProjectLevel projectLevel = null;
		History history = new History();
		try {
			// getting weblog of a user or project as per space type
			if (StringUtils.isNotEmpty(spaceType)) {
				if (spaceType.equals(Space.PERSONAL_SPACE)) {
					linkToPersonSpace = true;
					setOtherUsersSpaceId(spaceId);
					userWeblog = blogProvider.getWeblogByUserAndSpaceId(userId, spaceId);					
					// set personal space as curent space					
					try {
						PersonalSpaceBean personalSpace = (net.project.space.PersonalSpaceBean) request.getSession().getAttribute("personalSpace");																								
						if(personalSpace == null){
							personalSpace = new PersonalSpaceBean();
							personalSpace.setID(SessionManager.getUser().getID());
							personalSpace.load();
						}
						if(userWeblog != null && !userWeblog.getPnPerson().getDisplayName().equalsIgnoreCase(userWeblog.getName())){
							userWeblog.setName(userWeblog.getPnPerson().getDisplayName());
							blogProvider.updateWeblog(userWeblog);
							if(userWeblog.getPnPerson().getEmail().length() > 17) {
								userEmail = userWeblog.getPnPerson().getEmail().substring(0, 17)+"...";
							}
						}						
						// setting history for personal space
						projectLevel = new ProjectLevel();
						projectLevel.setDisplay(personalSpace.getName());
						projectLevel.setJspPage(SessionManager.getJSPRootURL() + "/personal/Main.jsp");
						if(SessionManager.getUser().getCurrentSpace().getType().equalsIgnoreCase(Space.PERSONAL_SPACE))
							user.setCurrentSpace(personalSpace);
					} catch (PnetException pnetEx1) {
						log.error("Error occured while setting user's current space and history for page : "+pnetEx1.getMessage());
					}
				} else if (spaceType.equals(Space.PROJECT_SPACE)) {
					linkToPersonSpace = false;
                    blogHeaderStyle = "leftheading-" + Space.PROJECT_SPACE;
					setOtherUsersSpaceId(null);
					String isTeamMember = request.getParameter("teamMember");
					if(StringUtils.isNotEmpty(isTeamMember) && isTeamMember.equalsIgnoreCase("true")){
						setGetBlogEntriesForTeamMember(true);
					}
					// getting ProjectSpaceBean from session for project details
					project = (ProjectSpaceBean) request.getSession().getAttribute("projectSpace");
					if(project == null || (StringUtils.isNotEmpty(project.getID()) && !project.getID().equalsIgnoreCase(spaceId.toString()))){
						project = new ProjectSpaceBean();
						project.setID(spaceId.toString());
						try {
							project.load();
						} catch (PersistenceException pnetEx) {
							log.error("Error occured while loading project space : "+pnetEx.getMessage());
						}
					}
					// security access check
					Space oldSpace = SessionManager.getSecurityProvider().getSpace();
					if(!isAccessAllowed(project.getID())) {
						// Failed Security Check
						SessionManager.getSecurityProvider().setSpace(oldSpace);
						Space testSpace = new ProjectSpaceBean();
				        testSpace.setID(project.getID());
                        try {
                            request.setAttribute("exception", new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.project.main.authorizationfailed.message"), testSpace));
                            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/AccessDenied.jsp");
                            requestDispatcher.forward(request, requestGlobals.getHTTPServletResponse());
                        } catch (Exception ex) {
                            log.error("Error occurred while handling security for blog: " + ex.getMessage());
                        }
					}
					// setting history for project space
					projectLevel = new ProjectLevel();
					projectLevel.setDisplay(project.getName());
					projectLevel.setJspPage(SessionManager.getJSPRootURL() + "/project/Dashboard?module="+Module.PROJECT_SPACE+"&id="+spaceId);					
					try {
						user.setCurrentSpace(project);
					} catch (PnetException pnetEx) {
						log.error("Error occured while setting project as current space : "+pnetEx.getMessage());
					}					
					userWeblog = blogProvider.getWeblogBySpaceId(spaceId);
					boolean weblogChanged = false;
					if (project != null && userWeblog != null) {
                        if (project.getName() != null && userWeblog.getName() != null && !project.getName().equalsIgnoreCase(userWeblog.getName())) {
                            userWeblog.setName(project.getName());
                            weblogChanged = true;
                        }
                        if (project.getDescription() != null && userWeblog.getDescription() != null && !project.getDescription().equalsIgnoreCase(userWeblog.getDescription())) {
                            if (userWeblog.getDescription().contains(" Project Blog")
                                    && userWeblog.getDescription().replace(" Project Blog", "").equalsIgnoreCase(project.getName())) {
                                userWeblog.setDescription(project.getDescription() != null ? project.getDescription() : project.getName() + " Project Blog");
                            }
                            weblogChanged = true;
                        }
                        if (weblogChanged) {
                            blogProvider.updateWeblog(userWeblog);
                        }
                    }
				}
				// setting history levels and setting as session attribute
				projectLevel.setShow(true);
				history.setLevel(businessLevel);
				history.setLevel(projectLevel);
				request.getSession().setAttribute("historyTagHistoryObject", history);
				request.getSession().setAttribute("user", user);
			}
		} catch (NumberFormatException pnetEx) {
			log.error(pnetEx.getMessage());
		} catch (PnWebloggerException pnetEx) {
			log.error(pnetEx.getMessage());
		}
		// if blog not exist then creating a blog
		if (userWeblog == null) {
			userWeblog = blogProvider.createBlog(new User(userId.toString()), spaceType, spaceId, project);			
		}
		addWeblogEntry.setWeblogId(userWeblog.getWeblogId());
		addWeblogEntry.setWeblog(userWeblog);
		
		if(userWeblog != null){
			setBlogName(userWeblog.getName());
		}
		
	}
	
	/**
	 * Check for blog module view access for current space
	 * @param spaceId space identifier
	 * @return true or false
	 */
	private boolean isAccessAllowed(String spaceId) {
        boolean accessAllowed = false;
        if (StringUtils.isNotEmpty(spaceId)) {            
            SecurityProvider securityProvider = SessionManager.getSecurityProvider();
            
            // Security Check: Is user allowed access to requested module?
            Space testSpace = new ProjectSpaceBean();
            testSpace.setID(spaceId);
            securityProvider.setSpace(testSpace);
            if (securityProvider.isActionAllowed(null, Integer.toString(net.project.base.Module.BLOG), net.project.security.Action.VIEW)) {
                accessAllowed = true;
            }
        }
        return accessAllowed;
    }
	
	/**
	 * Method to create a blog for project
	 * 
	 * @return true text on successful blog creation else false
	 */
	Object createProjectBlog(){
		try {
			// getting ProjectSpaceBean from request for project details
            String objectId = request.getParameter("objectId");
			project = (ProjectSpaceBean) request.getSession().getAttribute("projectSpace");
			if (project == null || (project != null && !objectId.equals(project.getID()))) {
				if (objectId != null) {
					IPnProjectSpaceService pnProjectSpaceService = ServiceFactory.getInstance()
							.getPnProjectSpaceService();
					PnProjectSpace pnProjectSpace = pnProjectSpaceService.getProjectSpaceDetails(Integer
							.parseInt(objectId));
					project = new ProjectSpaceBean();
					project.setID(pnProjectSpace.getProjectId().toString());
					project.setName(pnProjectSpace.getProjectName());
					project.setDescription(pnProjectSpace.getProjectDesc() != null ? pnProjectSpace
							.getProjectDesc() : null);
				}
			}
			if (project != null) {
				setSpaceId(Integer.parseInt(project.getID()));
				setSpaceType(Space.PROJECT_SPACE);
				setModuleId(Module.getModuleForSpaceType(getSpaceType()));
				if (blogProvider.createBlog(new User(SessionManager.getUser().getID()), getSpaceType(), getSpaceId(), project) != null) {
					return new TextStreamResponse("text/plain", "true");
				}
			}
		} catch (Exception e) {
			log.error("Error occured while creating project blog : " + e.getMessage());
		}
		return new TextStreamResponse("text/plain", "false");
	}
    
	/**
	 * Seting up values before page render
	 */
	@SetupRender
	void setValues() {
		setLinkHrefValue(SessionManager.getJSPRootURL() + "/blog/view/" + userDateFormat.formatDate(previousDate, startEndDateFormatPattern).replace("/", "-") + "/"
				+ userDateFormat.formatDate(currentDate, startEndDateFormatPattern).replace("/", "-")
				+ "/" + (getOtherUsersSpaceId() != null ? getOtherUsersSpaceId() : getSpaceId()) + "?module=" + getModuleId());
		
		setSpaceId(Integer.parseInt(SessionManager.getUser().getCurrentSpace().getID()));
		
		
		setSpaceAdmin(StringUtils.isNotEmpty(spaceType) && spaceType.equalsIgnoreCase(Space.PROJECT_SPACE));
		
		// Getting user's image path and getting contact information to display on page
		if (StringUtils.isNotEmpty(spaceType) && spaceType.equals(Space.PERSONAL_SPACE)) {
			try {
				// Getting visble project list for filter
				IPnProjectSpaceService projectSpaceService = ServiceFactory.getInstance().getPnProjectSpaceService();
				visibleProjectsList = projectSpaceService.getProjectsVisibleToUser(userId, Integer.parseInt(SessionManager.getUser().getID()));
				if(visibleProjectsList != null && visibleProjectsList.size() > 0){
					visibleProjectSpace = new PnProjectSpace();
					visibleProjectSpace.setProjectId(0);
					visibleProjectSpace.setProjectName("All projects");
					visibleProjectsList.add(0, visibleProjectSpace);
				} else {
					visibleProjectSpace = new PnProjectSpace();
					visibleProjectSpace.setProjectId(0);
					visibleProjectSpace.setProjectName("No Projects");
					visibleProjectsList = new ArrayList<PnProjectSpace>();
					visibleProjectsList.add(visibleProjectSpace);
				}
				projectBeans = new GenericSelectModel<PnProjectSpace>(visibleProjectsList, PnProjectSpace.class, "projectName", "projectId", access);
				
				if (userWeblog != null && userWeblog.getPnPerson() != null) {
                    setSpaceAdmin(userWeblog.getPnPerson().getPersonId().intValue() == Integer.valueOf(SessionManager.getUser().getID()).intValue());
					if (userWeblog.getPnPerson().getImageId() != null && userWeblog.getPnPerson().getImageId() != 0) {
						imagePath = SessionManager.getJSPRootURL() + "/servlet/photo?id="
								+ userWeblog.getPnPerson().getPersonId()+ "&size=medium&module=" + Module.PERSONAL_SPACE;
					} else {
						imagePath = SessionManager.getJSPRootURL() + "/images/NoPicture.gif";
					}
					if (userWeblog.getPnPerson().getPnPersonProfile() != null
							&& userWeblog.getPnPerson().getPnPersonProfile().getAddressId() != null
							&& userWeblog.getPnPerson().getPnPersonProfile().getAddressId() != 0) {
						IPnAddressService pnAddressService = ServiceFactory.getInstance().getPnAddressService();
						pnAddress = pnAddressService.getAddress(userWeblog.getPnPerson().getPnPersonProfile()
								.getAddressId());
					}
				}
			} catch (Exception e) {
				log.error("Error occurred while getting image path and person's contact details : " + e.getMessage());
			}
		}
		
		try {
			// Getting Item Type list for filter
			//pnClassService = ServiceFactory.getInstance().getPnClassService();
			itemTypeList = "0=All item type,";
			itemTypeList += "taskId=Task,";
			itemTypeList += "documentId=Document";
			List<PnClass> formList = pnClassService.getFormNamesForSpace(""+spaceId);
			if(CollectionUtils.isNotEmpty(formList)){
				for(PnClass pnClass : formList){
					itemTypeList += ", "+ObjectType.FORM+"_"+pnClass.getClassName()+"="+pnClass.getClassName();
				}
			}
			
		} catch (Exception e) {
			log.error("Error occurred while generating form list for space : " + e.getMessage());
		}
		
		// setting flag for important symbol, edit link and format for entry published time
		// of weblog entry for displaying on page
		userWeblogEntries = blogViewProvider.getFormattedBlogEntries(userWeblogEntries, getJSPRootURL(), getSpaceType(), DateFormat.getInstance());
		
		if(CollectionUtils.isNotEmpty(userWeblogEntries)) {
			blogEntryIds = "";
			for(PnWeblogEntry entry : userWeblogEntries){
				blogEntryIds += entry.getWeblogEntryId()+",";
			}
		}
	}
	
	/**
	 * Method to get last blog entry details of all members of a space 
	 * in json array format
	 * 
	 * @param spaceId Space identifier
	 * @return TextStreamResponse instance containing json array string, 
	 * if entries found else false as string.
	 */
	private TextStreamResponse getLastBlogEntryDetailsBySpace(String spaceId){
		String spaceType = SessionManager.getUser().getCurrentSpace().getSpaceType().getName().toLowerCase();
		spaceType = spaceType.equalsIgnoreCase("personal") ? Space.PERSONAL_SPACE : spaceType;
		Integer moduleId = Module.getModuleForSpaceType(spaceType);
		if(StringUtils.isNotEmpty(spaceId)){
			List<PnWeblogEntry> lastBlogEntries = null;
			try {
				JSONStringer stringer = new JSONStringer();
				lastBlogEntries = blogProvider.getLastBlogEntiesOfAllUsersBySpace(Integer.parseInt(spaceId));
				if(CollectionUtils.isNotEmpty(lastBlogEntries)){
					stringer.array();
					for(PnWeblogEntry lastEntry : lastBlogEntries){
						stringer.object();
	                    stringer.key("spaceId").value(spaceId);
	                    stringer.key("userId").value(lastEntry.getPnPerson().getPersonId());
	                    stringer.key("moduleId").value(moduleId);
	                    if(lastEntry != null){
	                    	stringer.key("date").value(ServiceFactory.getInstance().getUtilService().getDaysBetween(lastEntry.getPubTime(), new PnCalendar().getTime()));
	                    	stringer.key("webLogEntryId").value(lastEntry.getWeblogEntryId());
	                    } else {
	                     	stringer.key("date").value("");
	                     	stringer.key("webLogEntryId").value("");
	                    }   
	                    stringer.endObject();
					}
					stringer.endArray();
                    return new TextStreamResponse("text/json", stringer.toString());
				}
			} catch (Exception pnetEx) {
				log.error("Error occurred while getting last blog entry link details "+pnetEx.getMessage());
			}
		}
		return new TextStreamResponse("text/plain", "false");
	}
    
    /**
     * Method to get blog entries for selected object
     * @param objectId object indentifier
     * @return Blog entries as Text Stream Response
     */
    public Object loadBlogEntries(Integer objectId, String childTaskList, String workSpaceId, String startDateString, String endDateString, int offset, int post) {
    	return loadBlogEntries(objectId, childTaskList, workSpaceId, startDateString, endDateString, offset, post, false);
    }
    	
	/**
     * Method to get blog entries for selected object
     * @param objectId object indentifier
     * @return Blog entries as Text Stream Response
     */
    public Object loadBlogEntries(Integer objectId, String childTaskList, String workSpaceId, String startDateString, String endDateString, int offset, int post, boolean isProjectPortfolio){
        List<PnWeblogEntry> entries = null;
        IPnObjectTypeService objectTypeService = ServiceFactory.getInstance().getPnObjectTypeService();
        PnObjectType objType = objectTypeService.getObjectTypeByObjectId(objectId);
        String spaceType = ObjectType.PERSON;
        int moduleId = 160;
        String userId = SessionManager.getUser().getID();
        String JSPRootURL = SessionManager.getJSPRootURL();
        
		Date endDate =  null;
		
		if(StringUtils.isNotEmpty(startDateString) && StringUtils.isNotEmpty(endDateString)){
			endDate =  new Date(Long.parseLong(endDateString));
			PnCalendar calendar = new PnCalendar();
			calendar.setTime(endDate);
			calendar.add(Calendar.DATE, 1);
			endDate = calendar.getTime();
		}
		
        try {
            if(StringUtils.isEmpty(""+objectId) || (StringUtils.isNotEmpty(""+objectId) && objectId.equals(0))){    //FIX for clearing wiki tab on refreshing assignments tab.
                blogEntries.setMessage(PropertyProvider.get("prm.blog.tab.selectobject.message"));
                blogEntries.setUserWeblogEntries(null);
                blogEntries.setTotalWorkDone("");
                blogEntries.setIsMoreEntriesToSee(false);
                blogEntries.setMorePostUrl("");
            } else if (childTaskList.equals("-1")) {
                blogEntries.setMessage(PropertyProvider.get("prm.blog.tab.noblogentriesobject.message"));
                blogEntries.setUserWeblogEntries(null);
                blogEntries.setTotalWorkDone("");
                blogEntries.setIsMoreEntriesToSee(false);
                blogEntries.setMorePostUrl("");
            } else {
                if (StringUtils.isNotEmpty(workSpaceId)) {
                    spaceType = objectTypeService.getObjectTypeByObjectId(Integer.parseInt(workSpaceId)).getObjectType();
                    moduleId = Module.getModuleForSpaceType(spaceType);
                }
                if (objectId != null) {
                    try {
                        String[] childNodes = null;
                        if (StringUtils.isNotBlank(childTaskList)) {
                            childNodes = childTaskList.split(",");
                        }
                        if(objType.getObjectType().equalsIgnoreCase(ObjectType.PERSON)){
                            entries = blogProvider.getFilteredWeblogEntries(null, null, Integer.valueOf(userId), null, null, WeblogConstants.STATUS_PUBLISHED, offset, post, null);
                            totalBlogCount = blogProvider.getCountOfBlogEntries(null, null, null, Integer.valueOf(userId), null, null, WeblogConstants.STATUS_PUBLISHED, false, null, false, childNodes);
                        } else if(objType.getObjectType().equalsIgnoreCase(ObjectType.PROJECT) && isProjectPortfolio) {
                        	setUserWeblog(blogProvider.getWeblogBySpaceId(objectId));
                        	setUserWeblogEntries(blogProvider.getFilteredWeblogEntries( userWeblog.getWeblogId(), 
                        					null, null, null, null, null, WeblogConstants.STATUS_PUBLISHED, offset, post, 
                        					null, false, null, false, Integer.valueOf(getUser().getID())));
                        	entries = getUserWeblogEntries();
                        	blogEntries.setTotalNoOfBlogEntry(entries.size());
                        } else {
                            entries = blogProvider.getFilteredWeblogEntries(null, null, null, null, endDate, WeblogConstants.STATUS_PUBLISHED, offset, post, childNodes);
                            totalBlogCount = blogProvider.getCountOfBlogEntries(null, null, null, null, null, endDate, WeblogConstants.STATUS_PUBLISHED, false, null, false, childNodes);
                        }
                    } catch (Exception e) {
                        log.error("Error ocurred while loading weblog entries for selected object " + e.getMessage());
                    }
                }
                if (entries == null || entries.size() <= 0) {
                    blogEntries.setMessage(PropertyProvider.get("prm.blog.tab.noblogsfound.message"));
                    blogEntries.setUserWeblogEntries(null);                 
                } else {
                    if(totalBlogCount > 10){
                    	blogEntries.setLinkToPersonSpace(spaceType.equals(Space.PERSONAL_SPACE));
                    	blogEntries.setShowPrevLink(offset != 0);
                    	blogEntries.setShowTwoPanePaging(totalBlogCount > 0);
                        blogEntries.setPosts(post);
                        
                        if (totalBlogCount > 0) {
                            blogEntries.setTotalNoOfBlogEntry(totalBlogCount);
                            if(offset + 1 > totalBlogCount) {
                               blogEntries.setOffsetForDisplay(totalBlogCount);
                               blogEntries.setShowNextLink(false);
                            	
                            } else {
                               blogEntries.setOffsetForDisplay(offset + 1);
                               blogEntries.setShowNextLink(true);
                            	
                            }
                            if(offset + post >= totalBlogCount) {
                               blogEntries.setRangeForDisplay(totalBlogCount);
                               blogEntries.setShowNextLink(false);
                            	
                            } else {
                               blogEntries.setRangeForDisplay(offset + post);
                               blogEntries.setShowNextLink(true);
                               blogEntries.setNextEntriesCount((totalBlogCount - (offset + post)) > post ? post : (totalBlogCount - (offset + post)));
                               blogEntries.setMessage(totalBlogCount == 1 ? PropertyProvider.get("prm.blog.tab.oneblogentryfound.message") : PropertyProvider.get("prm.blog.tab.blogentriesfound.message", String.valueOf(totalBlogCount)));
                            }
                        }
                    }else{
                        blogEntries.setIsMoreEntriesToSee(false);
                        blogEntries.setMorePostUrl("");
                        blogEntries.setMessage(totalBlogCount == 1 ? PropertyProvider.get("prm.blog.tab.oneblogentryfound.message") : PropertyProvider.get("prm.blog.tab.blogentriesfound.message", String.valueOf(totalBlogCount)));
                    }
                    
                    blogEntries.setJspRootURL(JSPRootURL);
                    blogEntries.setUserWeblogEntries(blogViewProvider.getFormattedBlogEntries(entries, JSPRootURL, spaceType, DateFormat.getInstance()));
                    blogEntries.setLinkToPersonSpace(false);
                    blogEntries.setShowEditLink(false);
                    blogEntries.setShowExpandCollapseImage(true);
                    blogEntries.setShowPersonImage(false);
                    blogEntries.setBlogCommentDivClass("");
                    blogEntries.setBlogPostDivClass("post-body1");
                }
                blogEntries.setTotalWorkDone("");
                blogEntries.setIsAssignmentPage(true);
            }
            return blogEntries;
        } catch (Exception e) {
            log.error("Error occurred while loading blog entries for selected object : "+e.getMessage());
        }
        return null;
    }
    
    /**
	 * Method for check object is deleted or not and if not then redirect to object page
	 * @param objectId
	 * @param objectType
	 * @param objectName
	 * @param parentObjectId
	 * @return object
	 */
	Object checkAndRedirect(String weblogEntryId, String objectId, String objectType, String objectName, String isUserDeleted, String isProjectDeleted){
		return blogViewProvider.checkObjectStatusAndReturnObjectURL(weblogEntryId, objectId, objectType, objectName, getSpaceType(), Boolean.parseBoolean(isUserDeleted), Boolean.parseBoolean(isProjectDeleted));
	}
    
	@CleanupRender
	void cleanValues(){
		setMessage(null);
	}
	
	/**
	 * Method to get visible project beans to be shown for filtering
	 * 
	 * @return GenericSelectModel<PnProjectSpace>
	 */
	public GenericSelectModel<PnProjectSpace> getVisibleProjectsModel() {
		return projectBeans;
	}

	/**
	 * @return the blogName
	 */
	public String getBlogName() {
		return blogName;
	}

	/**
	 * @param blogName
	 *            the blogName to set
	 */
	public void setBlogName(String blogName) {
		this.blogName = blogName;
	}

	/**
	 * @return the userWeblog
	 */
	public PnWeblog getUserWeblog() {
		return userWeblog;
	}

	/**
	 * @return the userWeblogEntries
	 */
	public List<PnWeblogEntry> getUserWeblogEntries() {
		return userWeblogEntries;
	}

	/**
	 * @return the pnWeblogEntry
	 */
	public PnWeblogEntry getPnWeblogEntry() {
		return pnWeblogEntry;
	}

	/**
	 * @param pnWeblogEntry
	 *            the pnWeblogEntry to set
	 */
	public void setPnWeblogEntry(PnWeblogEntry pnWeblogEntry) {
		this.pnWeblogEntry = pnWeblogEntry;
	}

	/**
	 * @param userWeblogEntries
	 *            the userWeblogEntries to set
	 */
	public void setUserWeblogEntries(List<PnWeblogEntry> userWeblogEntries) {
		this.userWeblogEntries = userWeblogEntries;
	}

	/**
	 * @param userWeblog
	 *            the userWeblog to set
	 */
	public void setUserWeblog(PnWeblog userWeblog) {
		this.userWeblog = userWeblog;
	}

	/**
	 * @return the entriesMoreThanLimit
	 */
	public boolean getEntriesMoreThanLimit() {
		return entriesMoreThanLimit;
	}

	/**
	 * @return the linkToPersonSpace
	 */
	public boolean getLinkToPersonSpace() {
		return linkToPersonSpace;
	}

	/**
	 * @param linkToPersonSpace
	 *            the linkToPersonSpace to set
	 */
	public void setLinkToPersonSpace(boolean linkToPersonSpace) {
		this.linkToPersonSpace = linkToPersonSpace;
	}

	/**
	 * @return the moduleId
	 */
	public Integer getModuleId() {
		return moduleId;
	}

	/**
	 * @param moduleId
	 *            the moduleId to set
	 */
	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

	/**
	 * @return the spaceType
	 */
	public String getSpaceType() {
		return spaceType;
	}

	/**
	 * @param spaceType
	 *            the spaceType to set
	 */
	public void setSpaceType(String spaceType) {
		this.spaceType = spaceType;
	}

	/**
	 * @return the webblogEntryAdded
	 */
	public boolean isWebblogEntryAdded() {
		return webblogEntryAdded;
	}

	/**
	 * @param webblogEntryAdded
	 *            the webblogEntryAdded to set
	 */
	public void setWebblogEntryAdded(boolean webblogEntryAdded) {
		this.webblogEntryAdded = webblogEntryAdded;
	}

	/**
	 * @return the dashboard
	 */
	public String getDashboard() {
		return dashboard;
	}

	/**
	 * @return the entryPopupCloseLabel
	 */
	public String getEntryPopupCloseLabel() {
		return entryPopupCloseLabel;
	}

	/**
	 * @return the entryPopupTitle
	 */
	public String getEntryPopupTitle() {
		return entryPopupTitle;
	}
	
	/**
	 * @return the editEntryPopupTitle
	 */
	public String getEditEntryPopupTitle() {
		return editEntryPopupTitle;
	}

	/**
	 * @return the entryPostedAt
	 */
	public String getEntryPostedAt() {
		return entryPostedAt;
	}

	
	/**
	 * @return the importantSymbolTooltip
	 */
	public String getImportantSymbolTooltip() {
		return importantSymbolTooltip;
	}

	/**
	 * @return the postNewEntryLink
	 */
	public String getPostNewEntryLink() {
		return postNewEntryLink;
	}

	/**
	 * @return the taskListFirstOption
	 */
	public String getTaskListFirstOption() {
		return taskListFirstOption;
	}

	/**
	 * @return the taskListLabel
	 */
	public String getTaskListLabel() {
		return taskListLabel;
	}

	/**
	 * @return the tasksNotExistOption
	 */
	public String getTasksNotExistOption() {
		return tasksNotExistOption;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @return the pnWeblogComment
	 */
	public PnWeblogComment getPnWeblogComment() {
		return pnWeblogComment;
	}

	/**
	 * @param pnWeblogComment
	 *            the pnWeblogComment to set
	 */
	public void setPnWeblogComment(PnWeblogComment pnWeblogComment) {
		this.pnWeblogComment = pnWeblogComment;
	}

	/**
	 * @return the webblogCommentAdded
	 */
	public boolean isWebblogCommentAdded() {
		return webblogCommentAdded;
	}

	/**
	 * @param webblogCommentAdded
	 *            the webblogCommentAdded to set
	 */
	public void setWebblogCommentAdded(boolean webblogCommentAdded) {
		this.webblogCommentAdded = webblogCommentAdded;
	}

	/**
	 * @return the spaceId
	 */
	public Integer getSpaceId() {
		return spaceId;
	}

	/**
	 * @param spaceId
	 *            the spaceId to set
	 */
	public void setSpaceId(Integer spaceId) {
		this.spaceId = spaceId;
	}

	/**
	 * @return the userId
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * @return the commentPopupTitle
	 */
	public String getCommentPopupTitle() {
		return commentPopupTitle;
	}

	/**
	 * @param commentPopupTitle
	 *            the commentPopupTitle to set
	 */
	public void setCommentPopupTitle(String commentPopupTitle) {
		this.commentPopupTitle = commentPopupTitle;
	}

	/**
	 * @return the moreFlag
	 */
	public boolean isMoreFlag() {
		return moreFlag;
	}

	/**
	 * @param moreFlag
	 *            the moreFlag to set
	 */
	public void setMoreFlag(boolean moreFlag) {
		this.moreFlag = moreFlag;
	}

	/**
	 * @return the fromDate
	 */
	public String getFromDate() {
		return fromDate;
	}

	/**
	 * @param fromDate
	 *            the fromDate to set
	 */
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * @return the toDate
	 */
	public String getToDate() {
		return toDate;
	}

	/**
	 * @param toDate
	 *            the toDate to set
	 */
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	/**
	 * @return the linkHrefValue
	 */
	public String getLinkHrefValue() {
		return linkHrefValue;
	}

	/**
	 * @param linkHrefValue
	 *            the linkHrefValue to set
	 */
	public void setLinkHrefValue(String linkHrefValue) {
		this.linkHrefValue = linkHrefValue;
	}

	/**
	 * @return the blogEntriesLoadedForObject
	 */
	public boolean getBlogEntriesLoadedForObject() {
		return blogEntriesLoadedForObject;
	}

	/**
	 * @param blogEntriesLoadedForObject the blogEntriesLoadedForObject to set
	 */
	public void setBlogEntriesLoadedForObject(boolean blogEntriesLoadedForObject) {
		this.blogEntriesLoadedForObject = blogEntriesLoadedForObject;
	}

	/**
	 * @return the validationMessageForBlogComment
	 */
	public String getValidationMessageForBlogComment() {
		return validationMessageForBlogComment;
	}

	/**
	 * @param validationMessageForBlogComment the validationMessageForBlogComment to set
	 */
	public void setValidationMessageForBlogComment(String validationMessageForBlogComment) {
		this.validationMessageForBlogComment = validationMessageForBlogComment;
	}

	/**
	 * @return the validationMessageForBlogHtmlTagEntry
	 */
	public String getValidationMessageForBlogHtmlTagEntry() {
		return validationMessageForBlogHtmlTagEntry;
	}
	
	/**
	 * @return the validationMessageForBlogEntry
	 */
	public String getValidationMessageForBlogEntry() {
		return validationMessageForBlogEntry;
	}

	/**
	 * @param validationMessageForBlogEntry the validationMessageForBlogEntry to set
	 */
	public void setValidationMessageForBlogEntry(String validationMessageForBlogEntry) {
		this.validationMessageForBlogEntry = validationMessageForBlogEntry;
	}

	/**
	 * @return the otherUsersSpaceId
	 */
	public Integer getOtherUsersSpaceId() {
		return otherUsersSpaceId;
	}

	/**
	 * @param otherUsersSpaceId the otherUsersSpaceId to set
	 */
	public void setOtherUsersSpaceId(Integer otherUsersSpaceId) {
		this.otherUsersSpaceId = otherUsersSpaceId;
	}

	/**
	 * @return the getBlogEntriesForTeamMember
	 */
	public boolean isGetBlogEntriesForTeamMember() {
		return getBlogEntriesForTeamMember;
	}

	/**
	 * @param getBlogEntriesForTeamMember the getBlogEntriesForTeamMember to set
	 */
	public void setGetBlogEntriesForTeamMember(boolean getBlogEntriesForTeamMember) {
		this.getBlogEntriesForTeamMember = getBlogEntriesForTeamMember;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}	

	/**
	 * @return the filterResultMessage
	 */
	public String getFilterResultMessage() {
		return filterResultMessage;
	}

	/**
	 * @param filterResultMessage the filterResultMessage to set
	 */
	public void setFilterResultMessage(String filterResultMessage) {
		this.filterResultMessage = filterResultMessage;
	}

	/**
	 * @return the isSpaceAdmin
	 */
	public boolean getIsSpaceAdmin() {
		return isSpaceAdmin;
	}

	/**
	 * @param isSpaceAdmin the isSpaceAdmin to set
	 */
	public void setSpaceAdmin(boolean isSpaceAdmin) {
		this.isSpaceAdmin = isSpaceAdmin;
	}

	/**
	 * @return the imagePath
	 */
	public String getImagePath() {
		return imagePath;
	}

	/**
	 * @return the pnAddress
	 */
	public PnAddress getPnAddress() {
		return pnAddress;
	}

	/**
	 * @return the blogEntryIds
	 */
	public String getBlogEntryIds() {
		return blogEntryIds;
	}

	/**
	 * @return the visibleProjectSpace
	 */
	public PnProjectSpace getVisibleProjectSpace() {
		return visibleProjectSpace;
	}

	/**
	 * @param visibleProjectSpace the visibleProjectSpace to set
	 */
	public void setVisibleProjectSpace(PnProjectSpace visibleProjectSpace) {
		this.visibleProjectSpace = visibleProjectSpace;
	}

	/**
	 * @return the blogHeaderStyle
	 */
	public String getBlogHeaderStyle() {
		return blogHeaderStyle;
	}

	/**
	 * @return the itemTypeList
	 */
	public String getItemTypeList() {
		return itemTypeList;
	}

	/**
	 * @param itemTypeList the itemTypeList to set
	 */
	public void setItemTypeList(String itemTypeList) {
		this.itemTypeList = itemTypeList;
	}

	public String getTeamMemberId() {
		return teamMemberId;
	}

	public void setTeamMemberId(String teamMemberId) {
		this.teamMemberId = teamMemberId;
	}

	/**
	 * @return the confirmDeleteBlogEntryMessage
	 */
	public String getConfirmDeleteBlogEntryMessage() {
		return confirmDeleteBlogEntryMessage;
	}

	/**
	 * @return the showSkypeStatus
	 */
	public boolean getShowSkypeStatus() {
		return showSkypeStatus;
	}

    /**
     * @param totalBlogCount the totalBlogCount to set
     */
    public void setTotalBlogCount(int totalBlogCount) {
        this.totalBlogCount = totalBlogCount;
    }

    /**
     * @return the totalBlogCount
     */
    public int getTotalBlogCount() {
        return totalBlogCount;
    }

    /**
     * @return the blogEnabled
     */
    public boolean isBlogEnabled() {
        return blogEnabled;
    }

    /**
     * @return the blogPopupUserDate
     */
    public String getBlogPopupUserDate() {
        return blogPopupUserDate;
    }

	/**
	 * @return the actionsIconEnabled
	 */
	public boolean getActionsIconEnabled() {
		return actionsIconEnabled;
	}

	/**
	 * @return the blogItImageOn
	 */
	public String getBlogItImageOn() {
		return blogItImageOn;
	}

	/**
	 * @return the blogItImageOver
	 */
	public String getBlogItImageOver() {
		return blogItImageOver;
	}

	/**
	 * @return the showTitlesOnlyImageOn
	 */
	public String getShowTitlesOnlyImageOn() {
		return showTitlesOnlyImageOn;
	}

	/**
	 * @return the showTitlesOnlyImageOver
	 */
	public String getShowTitlesOnlyImageOver() {
		return showTitlesOnlyImageOver;
	}

	/**
	 * @return the myProfileImageOn
	 */
	public String getMyProfileImageOn() {
		return myProfileImageOn;
	}

	/**
	 * @return the myProfileImageOver
	 */
	public String getMyProfileImageOver() {
		return myProfileImageOver;
	}

	/**
	 * @return the showPictureOn
	 */
	public String getShowPictureOn() {
		return showPictureOn;
	}

	/**
	 * @return the showPictureOver
	 */
	public String getShowPictureOver() {
		return showPictureOver;
	}

	/**
	 * @return the projectLogo
	 */
	public Boolean getProjectLogo() {
		return projectLogo;
	}

	/**
	 * @return the parentProjectURL
	 */
	public String getParentProjectURL() {
		return parentProjectURL;
	}
	
	   /**
	 * @return the weblogEntryId
	 */
	public String getWeblogEntryId() {
		return weblogEntryId;
	}

	/**
	 * @param weblogEntryId the weblogEntryId to set
	 */
	public void setWeblogEntryId(String weblogEntryId) {
		this.weblogEntryId = weblogEntryId;
	}
	
	/**
	 * @return the taskID
	 */
	public String getTaskID() {
		return taskID;
	}

	/**
	 * @param taskID the taskID to set
	 */
	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}

	/**
	 * @return the objectType
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * @param objectType the objectType to set
	 */
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	
     /**
      * Converts the ViewBlog to XML representation without the XML version tag.
      * This method returns the blog entry title and blog entry url as XML text.
      *
      * @param pnWeblogEntry
      * @return XML representation
      */
    public String getXMLBody(PnWeblogEntry pnWeblogEntry) {
    	StringBuffer sb = new StringBuffer();
    	String space = StringUtils.EMPTY;
    	int module = 0;
    	
    	if(SessionManager.getUser().getID().equals(SessionManager.getUser().getCurrentSpace().getID())){
    		space = Space.PERSONAL_SPACE;
    		module = Module.PERSONAL_SPACE;
    	} else {
    		space = Space.PROJECT_SPACE;
    		module = Module.PROJECT_SPACE;
    	}

    	String title = StringUtils.isNotEmpty(pnWeblogEntry.getTitle()) ? pnWeblogEntry.getTitle() 
    					: (pnWeblogEntry.getText().length() > 40 ? pnWeblogEntry.getText().substring(0, 40) : pnWeblogEntry.getText());
    	String blogEntryUrl = SessionManager.getJSPRootURL() + "/blog/view/" + SessionManager.getUser().getCurrentSpace().getID() + "/"
								+ pnWeblogEntry.getPnPerson().getPersonId() + "/" + space + "/"
								+ module + "?module=" + module ;
    	
        sb.append("<Blog>\n");
        sb.append("<BlogName>" + XMLUtils.escape(title) + "</BlogName>\n");
        sb.append("<BlogUrl>" + XMLUtils.escape(blogEntryUrl) + "</BlogUrl>\n");
        sb.append("</Blog>");
        return sb.toString();
    }

}
