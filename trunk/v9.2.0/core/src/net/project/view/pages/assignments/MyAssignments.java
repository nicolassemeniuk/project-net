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
package net.project.view.pages.assignments;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.PnetException;
import net.project.base.finder.NumberComparator;
import net.project.base.finder.TextComparator;
import net.project.base.property.PropertyProvider;
import net.project.business.BusinessSpace;
import net.project.business.BusinessSpaceFinder;
import net.project.channel.ScopeType;
import net.project.hibernate.model.PnAssignment;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.service.IAddTaskService;
import net.project.hibernate.service.IPnAssignmentService;
import net.project.hibernate.service.IPnProjectSpaceService;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentColumn;
import net.project.resource.AssignmentList;
import net.project.resource.AssignmentStoreDataFactory;
import net.project.resource.AssignmentWorkCaptureHelper;
import net.project.resource.PersonProperty;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.PersonalSpaceBean;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.util.DateFormat;
import net.project.util.InvalidDateException;
import net.project.util.JSONUtils;
import net.project.util.Node;
import net.project.util.StringUtils;
import net.project.util.Version;
import net.project.view.pages.base.BasePage;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Cookies;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.util.TextStreamResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

@IncludeJavaScriptLibrary("${tapestry.scriptaculous}/prototype.js")
public class MyAssignments extends BasePage {
	
	private static Logger log = Logger.getLogger(MyAssignments.class);
	
	private String JSPRootURL;
	
	private String versionNumber;
	
	@Inject
	private RequestGlobals requestGlobals;
	
	@Persist
	private DateFormat userDateFormat;
	
	@Persist
	private String validationMessageForBlogEntry;
	
	@Persist
	private String validationMessageForBlogComment;
	
	@Persist
	private Integer moduleId;
	
	@Persist
	private Integer spaceId;
	
	@Persist
	private String assigneeOrAssignorParameter;
	
	@Persist
	private Integer businessId;
	
	@Persist
	private Integer[] projectIds;

	@Persist
	private String[] assignmentTypes;

	@Persist
	private boolean lateAssignment;

	@Persist
	private boolean comingDueDate;

	@Persist
	private boolean shouldHaveStart;

	@Persist
	private boolean inProgress;

	@Persist
	private Date startDate;

	@Persist
	private Date endDate;

	@Persist
	private Double percentComplete;

	@Persist
	private String percentCompleteComparator;

	@Persist
	private String assignmentName;

	@Persist
	private String assignmentNameComparator;
	
	@Persist
	private String businessOptionsString;
	
	@Persist
	private String projectOptionsString;
	
	@Persist
	private String assignmentViewParameter;
	
	@Persist
	private String userId;
	
	private String myAssignmentsColumn;
	
	private String myAssignmentsColumnSize;
	
	@Persist
	private Integer totalFilteredAssignments;
	
	@Persist
	private String defaultPageSize;
	
	@Inject
	private IPnAssignmentService pnAssignmentService;
	
	@Persist
	private String groupingParameter; 
	
	private boolean rightPanelCollapsed = false;
	
	private String rightPanelWidth = "30%";
	
	private final String COLUMN_PROPERTY_CONTEXT = "net.project.column.MyAssignments";
	
	private final String COLUMNSIZE_PROPERTY_CONTEXT = "net.project.columnsize.MyAssignments";
	
	private final String PANELCOLLAPSED_PROPERTY_CONTEXT = "net.project.panelcollapsed.MyAssignments";
	
	private final String PANELWIDTH_PROPERTY_CONTEXT = "net.project.panelwidth.MyAssignments";
	
	private final String GRIDTREESTATE_PROPERTY_CONTEXT = "net.project.gridtreestate.MyAssignments";
	
	private final String PROPERTY = "myassignmentswidget";
	
	private final String GRID_PAGE_OFFSET_PROPERTY = "gridpageOffset";
	
	private boolean actionsIconEnabled;

	private boolean isAgileWorkRemainingEnabled;
	
	@Property
	private AssignmentStoreDataFactory assignmentStoreDataFactory;
	
	@Persist
	private List<PnAssignment> assignments;
	
	private String userDateFormatString;
	
	private String startDisplayFrom;
	
	@Inject
	private Cookies cookies;
	
	@Persist
	private Schedule schedule;
	
	@Persist
	private ScheduleEntry scheduleEntry;
	
	@Persist
	private Space projectSpace;
	
	@Inject
	private IAddTaskService addTaskService;
	
	@Persist
	private String selectedTaskId;
	
	private String userDisplayName;
	
	@Persist
	private Integer numberOfLateAssignments;
	
	@Persist
	private Integer numberOfComingDueAssignments;
	
	@Persist
	private Integer numberOfShouldHaveStartedAssignments;
	
	@Persist
	private Integer numberOfInProgressAssignments;
	
	@Persist
	private Integer numberOfAllAssignments;
	
	@Persist
	private Integer numberOfCompletedAssignments;
    
    @Persist
    private Integer numberOfAllAssignedByMe;
	
	private int offset;
	
	private int range;
	
	private final String FILTER_PROPERTY_CONTEXT = "net.project.filter.MyAssignments";
	
	@Persist
    private PersonProperty personProperty;
    
    private final String [] ALL_ASSIGNMENT_TYPES = {ObjectType.TASK, ObjectType.FORM_DATA, ObjectType.MEETING};
    
    private final String ASSIGNEE = "assignee";
    
    private final String ASSIGNOR = "assignor";
    
    private String noProjectTaskAvailableMsg;
    
    //  Used to iterate node list over tml
    @Property
    private Node node;
    
    @Property
    @Persist
    private List<Node> assignmentList;
    
    @Property
    private List<BusinessSpace> businesses = null;
    
    @Property
    private BusinessSpace bussinessSpace;
    
    @Property 
    private List<PnProjectSpace> projects;
    
    @Property
    private PnProjectSpace project;
    
    @Property
    @InjectPage
    private MyAssignmentListPage assignmentListPage;
    
    @Property
    private String personalSettingsString;
    
    private PersonProperty property;
    
    @Persist
    private AssignmentList assignment;
    
    @Persist
    private boolean isActionAllowed;
    
    @Persist
    private List<Node> allAssignmentsList;
    
    @Persist
    private List<Node> assignedByMeAssignmentList;
    
    @Persist
    private List<Node> completedAssignmentsList;
    
    @Persist
    private List<Node> inProgressAssignmentList;
    
    @Persist
    private List<Node> comingDueAssignmentList;
    
    @Persist
    private List<Node> shouldHaveStartedAssignmentList;
    
    @Persist
    private List<Node> lateAssignmentList;
   
	private enum AssignmentsAction {
		SETFILTERPARAMETER, GETASSIGNMENTSTREEDATA, SETVIEWPARAMETER, REPLACEMYASSIGNMENTCOLUMN, SETGROUPINGPARAMETER, 
		ADDANDASSIGNTASK, GETRESOURCESTOASSIGN, REPLACEMYASSIGNMENTSPANELSTATE, REPLACEMYASSIGNMENTSPANELRESIZESTATE, REPLACEMYASSIGNMENTSCOLUMNSIZE,
		GETTIMELINEDATA, CALCULATETASK, SETASSIGNMENTTYPEPARAMETER, GETLATEASSIGNMENTSTREEDATA, GETCOMINGDUEASSIGNMENTSTREEDATA, 
		GETINPROGRESSASSIGNMENTSTREEDATA, GETSHOULDHAVESTARTEDASSIGNMENTSTREEDATA, GETALLASSIGNMENTSTREEDATA, GETCOMPLETEDASSIGNMENTSTREEDATA, SETASSIGNORORASSIGNEEPARAMETER, 
        GETASSIGNMENTSCOUNT, REPLACEGRIDTREESTATE, GETGRIDTREESTATE, GETALLASSIGNEDBYMETREEDATA, STOREVIEWSETTINGS, SORTCHANGED,GETNEWASSIGNMENTDATA;

        public static AssignmentsAction get( String v ) {
            try {
                return AssignmentsAction.valueOf( v.toUpperCase() );
            } catch( Exception ex ) { }
            return null;
         }
	}
	
	private enum WorkCaptureAction {
		UPDATE_TIME_SHEET_ENTRIES, SUBMIT_TIME_SHEET_ENTRIES, PERCENT_CHANGE;
        
        public static WorkCaptureAction get( String v ) {
            try {
                return WorkCaptureAction.valueOf( v.toUpperCase() );
            } catch( Exception ex ) { }
            return null;
         }
	}
	
	/**
	 * 
	 */
	void initialize() {
		try {
			JSPRootURL = SessionManager.getJSPRootURL();
			//pnAssignmentService = ServiceFactory.getInstance().getPnAssignmentService();
			//addTaskService = ServiceFactory.getInstance().getAddTaskService();
			assignmentStoreDataFactory = new AssignmentStoreDataFactory();
			versionNumber = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
			assignments = new ArrayList<PnAssignment>();
		} catch (Exception e) {
			log.error("Error occured while getting property tokens : " + e.getMessage());
		}
	}
	
	@SetupRender
	void setValues() {
        createUserBusinessOptionsString();
        createUserProjectOptionsString();
        initializeSystemAndUserProperty();
        setDefaultFilterParameter();
		initializeAssignmentsCount();
		assignmentListPage.setAssignmentColumn(new AssignmentColumn());
		assignmentList = getAssignmentTreeData();
		initializeMyAssignmentsWidget();
		assignment = new AssignmentList();
		assignment.setColumnIndex(0);
		assignment.setOrderDescending(true);
		assignmentListPage.setAssignmentList(assignmentListPage.arrangeAssignmentSequence(assignment.sort(assignmentList)));
		property = PersonProperty.getFromSession(getSession());
        property.setScope(ScopeType.SPACE.makeScope(getUser()));
    	property.prefetchForContextPrefix(assignmentListPage.getAssignmentColumn().getColumnPropertyContext());
        personalSettingsString = getPersonalSettingsJSONString(property);
	}
    
	private void setDefaultFilterParameter(){
		//initializing default parameter for getting assignment data.
		if(StringUtils.isEmpty(assigneeOrAssignorParameter)){
            assigneeOrAssignorParameter = getPropertyValue(FILTER_PROPERTY_CONTEXT + "_assignmentOf", null);
            if(StringUtils.isEmpty(assigneeOrAssignorParameter)){
                assigneeOrAssignorParameter = ASSIGNEE;
            }
		}
		if(percentComplete == null){
			percentComplete = 1.00;
		}
		if(StringUtils.isEmpty(percentCompleteComparator)){
			percentCompleteComparator = "lessthan";
		}
		if(StringUtils.isEmpty(assignmentNameComparator)){
			assignmentNameComparator = "contains";
		}
		if(StringUtils.isEmpty(assignmentViewParameter)){
			assignmentViewParameter = "indent";
		}
		if(StringUtils.isEmpty(groupingParameter)){
			groupingParameter = getPropertyValue(FILTER_PROPERTY_CONTEXT + "_groupingField", null);
            if(StringUtils.isEmpty(groupingParameter)){
                groupingParameter = "BusinessProject";
                //No need to grouping by business, if there is no any business for user.
                //getBusinessOptionsString() method returns list (JSON data) of user all buesinesses it will "[]" if user business list is empty.  
                if(getBusinessOptionsString().equals("[]")){
                    groupingParameter = "Project";
                }
            }
		}
		if (assignmentTypes == null) {
            String assignmentTypesCSV = getPropertyValue(FILTER_PROPERTY_CONTEXT + "_assignmentTypes", "");
            if (StringUtils.isEmpty(assignmentTypesCSV)) {
                if (PropertyProvider.getBoolean("prm.personal.assignments.filter.defaultassignmenttypetask.isenabled")) {
                    assignmentTypesCSV += ObjectType.TASK + ",";
                }
                if (PropertyProvider.getBoolean("prm.personal.assignments.filter.defaultassignmenttypemeeting.isenabled")) {
                    assignmentTypesCSV += ObjectType.MEETING + ",";
                }
                if (PropertyProvider.getBoolean("prm.personal.assignments.filter.defaultassignmenttypeform.isenabled")) {
                    assignmentTypesCSV += ObjectType.FORM_DATA + ",";
                }
                // if there are no any assignment filter property found then 'task' will be default assignment type.
                if (StringUtils.isEmpty(assignmentTypesCSV)) {
                    assignmentTypesCSV += ObjectType.TASK + ",";
                }
            }
            assignmentTypes = assignmentTypesCSV.split(",");
        }
        if (businessId == null) {
            businessId = Integer.parseInt(getPropertyValue(FILTER_PROPERTY_CONTEXT + "_business", "0"));
            // Set null if business id is zero
            businessId = businessId == 0 ? null : businessId;
        }
        
        if(projectIds == null){
            projectIds = StringUtils.getIntegerArrayOfCSNString(getPropertyValue(FILTER_PROPERTY_CONTEXT + "_projects", null));
        }
        
        setLateAssignment(getPropertyValue(FILTER_PROPERTY_CONTEXT + "_lateAssignment", "false").equals("true"));
        setComingDueDate(getPropertyValue(FILTER_PROPERTY_CONTEXT + "_comingDueDate", "false").equals("true"));
        setShouldHaveStart(getPropertyValue(FILTER_PROPERTY_CONTEXT + "_shouldHaveStart", "false").equals("true"));
        setInProgress(getPropertyValue(FILTER_PROPERTY_CONTEXT + "_inProgress", "false").equals("true"));
	}
	
	private void initializeSystemAndUserProperty(){
		//Some system property
		validationMessageForBlogEntry = PropertyProvider.get("prm.blog.addweblogentry.validation.message");
		validationMessageForBlogComment = PropertyProvider.get("prm.blog.addweblogentrycomment.validation.message");
		defaultPageSize = PropertyProvider.get("prm.personal.assignments.defaultpagesize.value");
		actionsIconEnabled = PropertyProvider.getBoolean("prm.global.actions.icon.isenabled");
		isAgileWorkRemainingEnabled = PropertyProvider.get("prm.personal.myassignments.blogit.agileworkremaining.isenabled").equals("1");
		noProjectTaskAvailableMsg = PropertyProvider.get("prm.personal.myassignment.taskassignwindow.error.noprojectortaskavailable");
		
		//Some user properties
		personProperty = new PersonProperty();
		personProperty.setScope(ScopeType.GLOBAL.makeScope(SessionManager.getUser()));
		userDateFormat = SessionManager.getUser().getDateFormatter();
		userDateFormatString = SessionManager.getUser().getDateFormatter().getDateFormatExample();
		userId = SessionManager.getUser().getID();
		userDisplayName = SessionManager.getUser().getDisplayName().replaceAll("'", "&acute;");
	}

	Object onActivate() {
		if(checkForUser() != null) {
			return checkForUser();
		}
		initialize();
		User user = SessionManager.getUser();
		//Resetting Personal space if user redirected from other space.
		PersonalSpaceBean personalSpace = (net.project.space.PersonalSpaceBean) requestGlobals.getHTTPServletRequest().getSession().getAttribute("personalSpace");																								
		if(personalSpace == null){
			personalSpace = new PersonalSpaceBean();
			personalSpace.setID(SessionManager.getUser().getID());
			personalSpace.load();
		}
		try {
			user.setCurrentSpace(personalSpace);
		} catch (PnetException e) {
			log.error("Error occured while setting the currentSpace" + e.getMessage());
		}
		requestGlobals.getHTTPServletRequest().setAttribute("user", user.getUserForPerson());
		
		moduleId = Module.getModuleForSpaceType(Space.PERSONAL_SPACE);
		spaceId = Integer.parseInt(SessionManager.getUser().getCurrentSpace().getID());
		return null;
	}

	/**
	 * @param action
	 * @return
	 */
	Object onActivate(String action) {
		if(checkForUser() != null) {
			return checkForUser();
		}
		initialize();
        AssignmentsAction assignmentsAction = AssignmentsAction.get( action );
        WorkCaptureAction workCaptureAction = WorkCaptureAction.get( action );
		moduleId = Module.getModuleForSpaceType(Space.PERSONAL_SPACE);
		spaceId = Integer.parseInt(SessionManager.getUser().getCurrentSpace().getID());
		HttpServletRequest request = requestGlobals.getHTTPServletRequest();
		offset = StringUtils.isNotEmpty(request.getParameter("start")) ? Integer.parseInt(request.getParameter("start")) : 0;
		range = StringUtils.isNotEmpty(request.getParameter("limit")) ? Integer.parseInt(request.getParameter("limit")) : 50;
		AssignmentWorkCaptureHelper workCaptureHelper = new AssignmentWorkCaptureHelper();
		if (action != null) {
			if (assignmentsAction == AssignmentsAction.SETVIEWPARAMETER) {
				setAssignmentViewParameter(StringUtils.isNotEmpty(request.getParameter("view")) ? request.getParameter("view") : "indent");
				return new TextStreamResponse("text/plain", "");
			} else if (assignmentsAction == AssignmentsAction.SETFILTERPARAMETER) {
				setAllFilterParameter(request);
				assignmentListPage.setAssignmentList(getAssignmentTreeData());
				getAssignmentsCount();
				return assignmentListPage;
			} else if (assignmentsAction == AssignmentsAction.GETASSIGNMENTSTREEDATA) {
				assignmentListPage.setAssignmentList(getAssignmentTreeData());
				getAssignmentsCount();
				return assignmentListPage;
			} else if (assignmentsAction == AssignmentsAction.REPLACEMYASSIGNMENTCOLUMN) {
				if (StringUtils.isNotEmpty(request.getParameter("column"))
						&& StringUtils.isNotEmpty(request.getParameter("value"))) {
					replaceMyAssignmentsUserPropetry(COLUMN_PROPERTY_CONTEXT + "_"
							+ request.getParameter("column"), request.getParameter("value"));
				}
				return new TextStreamResponse("text/plain", "");
			} else if (assignmentsAction == AssignmentsAction.SETGROUPINGPARAMETER){
				initializeGroupingParameter(request);
				assignmentListPage.setAssignmentList(getAssignmentTreeData());
				getAssignmentsCount();
				return assignmentListPage;
			} else if (assignmentsAction == AssignmentsAction.GETRESOURCESTOASSIGN){
				return getResourceAssignGridData(request);
			} else if(assignmentsAction == AssignmentsAction.GETNEWASSIGNMENTDATA){
				return getNewAssignmentsData(request);
			} else if (assignmentsAction == AssignmentsAction.ADDANDASSIGNTASK){
				return addAndAssignTask(request);
			} else if (assignmentsAction == AssignmentsAction.REPLACEMYASSIGNMENTSPANELSTATE) {
				replaceMyAssignmentsUserPropetry(PANELCOLLAPSED_PROPERTY_CONTEXT, request.getParameter("value"));
				return new TextStreamResponse("text/plain", "");
			} else if (assignmentsAction == AssignmentsAction.REPLACEMYASSIGNMENTSPANELRESIZESTATE) {
				replaceMyAssignmentsUserPropetry(PANELWIDTH_PROPERTY_CONTEXT, request.getParameter("value"));
				return new TextStreamResponse("text/plain", "");
			} else if (assignmentsAction == AssignmentsAction.REPLACEMYASSIGNMENTSCOLUMNSIZE) {
				replaceMyAssignmentsColumnSize(request);
				return new TextStreamResponse("text/plain", "");
			} else if (assignmentsAction == AssignmentsAction.GETTIMELINEDATA){
				return getTimeLineData();	
			} else if (assignmentsAction == AssignmentsAction.CALCULATETASK){
				return getTaskAfterCalucaton(request);
			} else if (assignmentsAction == AssignmentsAction.SETASSIGNMENTTYPEPARAMETER){
				setAssignmentTypes((StringUtils.isNotEmpty(request.getParameter("assignmentTypes"))) ? request.getParameter("assignmentTypes").split(",") : null );
				//Persist this filter 
				replaceMyAssignmentsUserPropetry(FILTER_PROPERTY_CONTEXT + "_assignmentTypes", request.getParameter("assignmentTypes"));
				assignmentListPage.setAssignmentList(getAssignmentTreeData());
				getAssignmentsCount();
				return assignmentListPage;
			} else if (assignmentsAction == AssignmentsAction.SETASSIGNORORASSIGNEEPARAMETER){
				setAssigneeOrAssignorParameter((StringUtils.isNotEmpty(request.getParameter("assignmentOf"))) ? request.getParameter("assignmentOf") : null);
				//Persist this filter
				replaceMyAssignmentsUserPropetry(FILTER_PROPERTY_CONTEXT + "_assignmentOf", request.getParameter("assignmentOf"));
				assignmentListPage.setAssignmentList(getAssignmentTreeData());
				getAssignmentsCount();
				return assignmentListPage;
			} else if (assignmentsAction == AssignmentsAction.GETLATEASSIGNMENTSTREEDATA){
				assignmentListPage.setAssignmentList(getLateAssignmentsTreeData());
				getAssignmentsCount();
				return assignmentListPage;
			} else if (assignmentsAction == AssignmentsAction.GETCOMINGDUEASSIGNMENTSTREEDATA){
				assignmentListPage.setAssignmentList(getComingDueDateAssignmentsTreeData());
				getAssignmentsCount();
				return assignmentListPage;
			} else if (assignmentsAction == AssignmentsAction.GETALLASSIGNMENTSTREEDATA){
				assignmentListPage.setAssignmentList(getAllAssignmentsTreeData()); 
				getAssignmentsCount();
				return assignmentListPage;
			} else if (assignmentsAction == AssignmentsAction.GETCOMPLETEDASSIGNMENTSTREEDATA){
				assignmentListPage.setAssignmentList(getCompletedAssignmentsTreeData()) ;
				getAssignmentsCount();
				return assignmentListPage;
			} else if (assignmentsAction == AssignmentsAction.GETSHOULDHAVESTARTEDASSIGNMENTSTREEDATA){
				assignmentListPage.setAssignmentList(getShouldHaveStartAssignmentsTreeData());
				getAssignmentsCount();
				return assignmentListPage;
			} else if (assignmentsAction == AssignmentsAction.GETINPROGRESSASSIGNMENTSTREEDATA){
				assignmentListPage.setAssignmentList(getInProgressAssignmentTreeData());
				getAssignmentsCount();
				return assignmentListPage;
			} else if (assignmentsAction == AssignmentsAction.GETALLASSIGNEDBYMETREEDATA){
                 assignmentListPage.setAssignmentList(getAllAssignedByMeTreeData());
                 getAssignmentsCount();
 				return assignmentListPage;
            } else if (assignmentsAction == AssignmentsAction.GETASSIGNMENTSCOUNT){
				 getAssignmentsCount();
			} else if (assignmentsAction == AssignmentsAction.REPLACEGRIDTREESTATE){
				replaceMyAssignmentsUserPropetry(GRIDTREESTATE_PROPERTY_CONTEXT,request.getParameter("value"));
				return new TextStreamResponse("text/plain", "");
			} else if (assignmentsAction == AssignmentsAction.GETGRIDTREESTATE){
				return new TextStreamResponse("text/plain",getPropertyValue(GRIDTREESTATE_PROPERTY_CONTEXT, "0"));
			} else if(assignmentsAction == AssignmentsAction.STOREVIEWSETTINGS){
				if(getParameter("propertyName") != null && getParameter("propertyValue") != null){
					return new TextStreamResponse("text/plain", ""+storeViewSetting(getParameter("propertyName"), getParameter("propertyValue")));
				}
			}else if(assignmentsAction == AssignmentsAction.SORTCHANGED){
				if(getParameter("columnIndex") != null){
					assignment.setColumnIndex(Integer.parseInt(getParameter("columnIndex")));
					assignmentListPage.setAssignmentList(assignmentListPage.arrangeAssignmentSequence(assignment.sort(getAssignmentListForActiveTab(getParameter("activeTab")))));
					getAssignmentsCount();
					return assignmentListPage;
				}
			}
			
			// Action to update the time sheet entries 
			// Return js call : an error message call if error found
			// update time sheet call if no error found	
			else if (workCaptureAction == WorkCaptureAction.UPDATE_TIME_SHEET_ENTRIES) {
				String updatedJSCall = "";
				try {
					updatedJSCall = workCaptureHelper.findUpdatedPercentComplete(request);
				} catch (Exception e) {
					log.error("Error occured while updating the work caputre : " + e.getMessage());
					updatedJSCall = "showErrorDiv('Internal Server Error!');";
				}
				return new TextStreamResponse("text/plain", updatedJSCall);
			}
			 // Action to save the time sheet in database if error free,
			 // otherwise return a js error message call like not a working day.			 
			else if (workCaptureAction == WorkCaptureAction.SUBMIT_TIME_SHEET_ENTRIES) {
				String strResponse = "";
				try {
					strResponse = workCaptureHelper.processUserChanges(request);
				} catch (Exception e) {
					log.error("Error occured while submitting the work caputre : " + e.getMessage());
					strResponse = "showErrorDiv('Internal Server Error!');";
				}
				return new TextStreamResponse("text/plain", strResponse);				
			}			
			//Action to update/save the percent adjustment of work of an assignment
			else if (workCaptureAction == WorkCaptureAction.PERCENT_CHANGE) {
				try {
					return new TextStreamResponse("text/plain", workCaptureHelper.changePC(request));
				} catch (Exception e) {
					log.error("Error occured while setting percentage of work caputre" + e.getMessage());
					return new TextStreamResponse("text/plain", "showErrorDiv1('Invalid input!');");
				}
			}
		}
		return null;
	}
	
	/**
	 * Invoked for getting Filtered assignments treeGrid json string.
	 * @return List<Node>
	 */
	public List<Node> getAssignmentTreeData() {
		assignments = pnAssignmentService.getAssignmentDetailsWithFilters(StringUtils.getIntegerArrayOfCSNString(SessionManager.getUser().getID()),
				assigneeOrAssignorParameter, projectIds, businessId, assignmentTypes, lateAssignment,
				comingDueDate, shouldHaveStart, inProgress, startDate, endDate, null,
				percentComplete, percentCompleteComparator, assignmentName, assignmentNameComparator,
				0, 0, false);
		
		//Set offset as startdDisplayForm
		cookies.removeCookieValue(GRID_PAGE_OFFSET_PROPERTY + "_" + userId);
		cookies.writeCookieValue(GRID_PAGE_OFFSET_PROPERTY + "_" + userId, ""+offset);
				
		assignmentList = assignmentListPage.arrangeAssignmentSequence(assignmentStoreDataFactory.getMyAssignmentGridDataString(assignments, userDateFormat,
						assignmentViewParameter, groupingParameter));
		totalFilteredAssignments = assignments.size();
		return assignmentList;
	}
	
	/**
	 * Invoked for getting Assignments Time Line json string.
	 * @return TextStreamResponse
	 */
	public TextStreamResponse getTimeLineData() {
		return new TextStreamResponse("text/json", assignmentStoreDataFactory.getTimlineDataString(assignments, userDateFormat));
	}
	
	/**
	 * Invoked for getting Late assignments treeGrid json string.
	 * @return List<Node>
	 */
	private List<Node> getLateAssignmentsTreeData(){
		lateAssignmentList = assignmentListPage.arrangeAssignmentSequence(assignmentStoreDataFactory.getMyAssignmentGridDataString(
						pnAssignmentService.getAssignmentDetailsWithFilters(StringUtils.getIntegerArrayOfCSNString(SessionManager.getUser().getID()),
								ASSIGNEE, null, null, ALL_ASSIGNMENT_TYPES, true, false, false, false, null, null, null, null, null, null, null, 0, 0, false), userDateFormat, assignmentViewParameter, groupingParameter));
		numberOfLateAssignments = lateAssignmentList.size();
		return lateAssignmentList;
	}
	
	/**
	 * Invoked for getting Coming Due assignments treeGrid json string.
	 * @return List<Node>
	 */
	private List<Node> getComingDueDateAssignmentsTreeData(){
		comingDueAssignmentList = assignmentListPage.arrangeAssignmentSequence(assignmentStoreDataFactory.getMyAssignmentGridDataString(
						pnAssignmentService.getAssignmentDetailsWithFilters(StringUtils.getIntegerArrayOfCSNString(SessionManager.getUser().getID()),
								ASSIGNEE, null, null, ALL_ASSIGNMENT_TYPES, false, true, false, false, null, null, null, null, null, null, null, 0, 0, false), userDateFormat, assignmentViewParameter, groupingParameter));
		numberOfComingDueAssignments = comingDueAssignmentList.size();
		return comingDueAssignmentList;
	}
	
	/**
	 * Invoked for getting Should Have Started  assignments treeGrid json string.
	 * @return List<Node>
	 */
	private List<Node> getShouldHaveStartAssignmentsTreeData(){
		shouldHaveStartedAssignmentList = assignmentListPage.arrangeAssignmentSequence(assignmentStoreDataFactory.getMyAssignmentGridDataString(
						pnAssignmentService.getAssignmentDetailsWithFilters(StringUtils.getIntegerArrayOfCSNString(SessionManager.getUser().getID()),
								ASSIGNEE, null, null, ALL_ASSIGNMENT_TYPES, false, false, true, false, null, null, null, null, null, null, null, 0, 0, false), userDateFormat, assignmentViewParameter, groupingParameter));
		numberOfShouldHaveStartedAssignments = shouldHaveStartedAssignmentList.size();
		return shouldHaveStartedAssignmentList;
	}
	
	/**
	 * Invoked  for getting In Progress assignments treeGrid json string.
	 * @return TextStreamResponse
	 */
	private List<Node> getInProgressAssignmentTreeData(){
		inProgressAssignmentList =  assignmentListPage.arrangeAssignmentSequence(assignmentStoreDataFactory.getMyAssignmentGridDataString(
						pnAssignmentService.getAssignmentDetailsWithFilters(StringUtils.getIntegerArrayOfCSNString(SessionManager.getUser().getID()),
								ASSIGNEE, null, null, ALL_ASSIGNMENT_TYPES, false, false, false, true, null, null, null, null, null, null, null, 0, 0, false), userDateFormat, assignmentViewParameter, groupingParameter));
		numberOfInProgressAssignments = inProgressAssignmentList.size();
		return inProgressAssignmentList;
	}
	
	/**
	 * Invoked for getting All assignments treeGrid json string.
	 * @return List<Node>
	 */
	private List<Node> getAllAssignmentsTreeData(){
		allAssignmentsList = assignmentListPage.arrangeAssignmentSequence(assignmentStoreDataFactory.getMyAssignmentGridDataString(
				pnAssignmentService.getAssignmentDetailsWithFilters(StringUtils.getIntegerArrayOfCSNString(SessionManager.getUser().getID()),
                        ASSIGNEE, null, null, ALL_ASSIGNMENT_TYPES, false, false, false, false, null, null, null, null, null, null, null, 0, 0, false), userDateFormat, assignmentViewParameter, groupingParameter));
		numberOfAllAssignments = allAssignmentsList.size();
		return allAssignmentsList;
	}
	
	/**
	 * Invoked for getting completed assignments treeGrid json string.
	 * @return List<Node>
	 */
	private List<Node> getCompletedAssignmentsTreeData(){
		completedAssignmentsList = assignmentListPage.arrangeAssignmentSequence(assignmentStoreDataFactory.getMyAssignmentGridDataString(
						pnAssignmentService.getAssignmentDetailsWithFilters(StringUtils.getIntegerArrayOfCSNString(SessionManager.getUser().getID()),
								ASSIGNEE, null, null, ALL_ASSIGNMENT_TYPES, false, false, false, false, null, null, null, .99, "greaterthan", null, null, 0, 0, false), userDateFormat, assignmentViewParameter, groupingParameter));
		numberOfCompletedAssignments = completedAssignmentsList.size();
		return completedAssignmentsList;
	}
    
    /**
     * Invoked for getting all assignment which is assigned by me treeGrid json string.
     * @return List<Node>
     */
    private List<Node> getAllAssignedByMeTreeData(){
    	assignedByMeAssignmentList = assignmentListPage.arrangeAssignmentSequence(assignmentStoreDataFactory.getMyAssignmentGridDataString(
                        pnAssignmentService.getAssignmentDetailsWithFilters(StringUtils.getIntegerArrayOfCSNString(SessionManager.getUser().getID()),
                                ASSIGNOR, null, null, ALL_ASSIGNMENT_TYPES, false, false, false, false, null, null, null, null, null, null, null, 0, 0, false), userDateFormat, assignmentViewParameter, groupingParameter));
    	numberOfAllAssignedByMe = assignedByMeAssignmentList.size();
        return assignedByMeAssignmentList;
    }
	
	
	/**
	 * This method generates business option string for business filter drop down list.
	 */
	private void createUserBusinessOptionsString() {
		//List<BusinessSpace> businesses = null; It is glogal now
		BusinessSpaceFinder bFinder = new BusinessSpaceFinder();
		try {
			businesses = bFinder.findByUser(net.project.security.SessionManager.getUser(), "A");
		} catch (PersistenceException e) {
			log.error("Error occured while generating list values for Businesses model: MyAssignments.createUserBusinessOptionsString()");
		}
		setBusinessOptionsString(assignmentStoreDataFactory.generateUserBusinessOptionsString(businesses));
	}
	
	/**
	 *This method generates project option string for business filter multi select drop down list. 
	 */
	private void createUserProjectOptionsString(){
		IPnProjectSpaceService pnProjectSpaceService = ServiceFactory.getInstance().getPnProjectSpaceService();
		projects = pnProjectSpaceService.getProjectsByUserId(Integer.parseInt(SessionManager.getUser().getID()));
		setProjectOptionsString(assignmentStoreDataFactory.generateUserProjectOptionsString(projects));
	}
	
	/**
	 * This method is invoked for setting all filter parameters.
	 * @param request
	 * @return
	 */
	private void setAllFilterParameter(HttpServletRequest request) {
        businessId = StringUtils.isNotEmpty(request.getParameter("business")) ? Integer.parseInt(request.getParameter("business")): null;
		//Persist this filter 
		replaceMyAssignmentsUserPropetry(FILTER_PROPERTY_CONTEXT + "_business", request.getParameter("business"));

		projectIds = StringUtils.getIntegerArrayOfCSNString(request.getParameter("projects"));
		//Persist this filter 
		replaceMyAssignmentsUserPropetry(FILTER_PROPERTY_CONTEXT + "_projects", request.getParameter("projects"));

        startDate = null;
        if (StringUtils.isNotEmpty(request.getParameter("startDate"))) {
			try {
				startDate = userDateFormat.parseDateString(request.getParameter("startDate"));
			} catch (InvalidDateException pnetEx) {
				log.error("Error occured while parsing dates MyAssignments.setAllFilterParameter(): "+ pnetEx.getMessage());
			}
		} else {
			startDate = null;
		}

		if (StringUtils.isNotEmpty(request.getParameter("endDate"))) {
			try {
				endDate = userDateFormat.parseDateString(request.getParameter("endDate"));
                Calendar cal = Calendar.getInstance();
                cal.setTime(endDate);
                cal.add(Calendar.DATE, 1);
                endDate = cal.getTime();
			} catch (InvalidDateException pnetEx) {
				log.error("Error occured while parsing dates MyAssignments.setAllFilterParameter(): "+ pnetEx.getMessage());
			}
		} else {
			endDate = null;
		}

		if (StringUtils.isNotEmpty(request.getParameter("percentComplete"))
				&& StringUtils.isNotEmpty(request.getParameter("percentCompleteComparator"))) {
			percentComplete = Double.parseDouble(request.getParameter("percentComplete"))/100.0;
			percentCompleteComparator = request.getParameter("percentCompleteComparator");
		} else {
			percentComplete = null;
			percentCompleteComparator = null;
		}

		if (StringUtils.isNotEmpty(request.getParameter("assignmentName"))
				&& StringUtils.isNotEmpty(request.getParameter("assignmentNameComparator"))) {
			assignmentName = request.getParameter("assignmentName");
			assignmentNameComparator = request.getParameter("assignmentNameComparator");
		} else {
			assignmentName = null;
			assignmentNameComparator = null;
		}
		
		setLateAssignment(StringUtils.isNotEmpty(request.getParameter("lateAssignment"))
				&& request.getParameter("lateAssignment").equals("true"));
		//Persist this filter 
		replaceMyAssignmentsUserPropetry(FILTER_PROPERTY_CONTEXT + "_lateAssignment", request.getParameter("lateAssignment"));


		setComingDueDate(StringUtils.isNotEmpty(request.getParameter("comingDueDate"))
				&& request.getParameter("comingDueDate").equals("true"));
		//Persist this filter 
		replaceMyAssignmentsUserPropetry(FILTER_PROPERTY_CONTEXT + "_comingDueDate", request.getParameter("comingDueDate"));


		setShouldHaveStart(StringUtils.isNotEmpty(request.getParameter("shouldHaveStart"))
				&& request.getParameter("shouldHaveStart").equals("true"));
		//Persist this filter 
		replaceMyAssignmentsUserPropetry(FILTER_PROPERTY_CONTEXT + "_shouldHaveStart", request.getParameter("shouldHaveStart"));


		setInProgress(StringUtils.isNotEmpty(request.getParameter("inProgress"))
				&& request.getParameter("inProgress").equals("true"));
		//Persist this filter 
		replaceMyAssignmentsUserPropetry(FILTER_PROPERTY_CONTEXT + "_inProgress", request.getParameter("inProgress"));

	}
	
	/**
	 * This method is invoked for initializing assignmetns counts. 
	 */
	private void initializeAssignmentsCount() {
		numberOfLateAssignments = pnAssignmentService.getTotalAssignmentCountWithFilters(Integer.parseInt(
				SessionManager.getUser().getID()), ASSIGNEE, null, null, ALL_ASSIGNMENT_TYPES, true, false, false, false, null, null, null, null, null, null, null);
		
		numberOfComingDueAssignments = pnAssignmentService.getTotalAssignmentCountWithFilters(Integer.parseInt(
				SessionManager.getUser().getID()), ASSIGNEE, null, null, ALL_ASSIGNMENT_TYPES, false, true, false, false, null, null, null, null, null, null, null); 
		
		numberOfShouldHaveStartedAssignments = pnAssignmentService.getTotalAssignmentCountWithFilters(Integer.parseInt(
				SessionManager.getUser().getID()), ASSIGNEE, null, null, ALL_ASSIGNMENT_TYPES, false, false, true, false, null, null, null, null, null, null, null);
		
		numberOfInProgressAssignments = pnAssignmentService.getTotalAssignmentCountWithFilters(Integer.parseInt(
				SessionManager.getUser().getID()), ASSIGNEE, null, null, ALL_ASSIGNMENT_TYPES, false, false, false, true, null, null, null, null, null, null, null);
		
		numberOfAllAssignments = pnAssignmentService.getTotalAssignmentCountWithFilters(Integer.parseInt(
				SessionManager.getUser().getID()), ASSIGNEE, null, null, ALL_ASSIGNMENT_TYPES, false, false, false, false, null, null, null, null, null, null, null);
		
		numberOfCompletedAssignments = pnAssignmentService.getTotalAssignmentCountWithFilters(Integer.parseInt(
				SessionManager.getUser().getID()), ASSIGNEE, null, null, ALL_ASSIGNMENT_TYPES, false, false, false, false, null, null, null,	.99, "greaterthan", null, null);
        
        numberOfAllAssignedByMe = pnAssignmentService.getTotalAssignmentCountWithFilters(Integer.parseInt(
                        SessionManager.getUser().getID()), ASSIGNOR, null, null, ALL_ASSIGNMENT_TYPES, false, false, false, false, null, null, null, null, null, null, null);
	}
	
	/**
	 * This method is invoked for initializing assignment widget view. 
	 */
	private void initializeMyAssignmentsWidget(){
		String columns[] = {"objectName", "workSpace", "dueDate", "workRemaining", "objectId", "objectType", "assigneeName", "assignorName", "myWork", "myWorkComplete", "myPercentComplete", "startDate", "actualStartDate"};
		myAssignmentsColumn = "[{";
		myAssignmentsColumnSize = "[{";

		for(String column : columns){
			if(!myAssignmentsColumn.equals("[{")){
				myAssignmentsColumn += ",";
				myAssignmentsColumnSize += ",";
			}
			myAssignmentsColumn += "'" + column + "' : "+getPropertyValue(COLUMN_PROPERTY_CONTEXT + "_"+ column, "null");
			myAssignmentsColumnSize += "'" + column + "' : "+getPropertyValue(COLUMNSIZE_PROPERTY_CONTEXT + "_"+ column, "null");
		}
		myAssignmentsColumn += "}]";
		myAssignmentsColumnSize+= "}]";
		
		setRightPanelCollapsed(getPropertyValue(PANELCOLLAPSED_PROPERTY_CONTEXT, "0").equals("1"));
		setRightPanelWidth(getPropertyValue(PANELWIDTH_PROPERTY_CONTEXT, "30%"));
        startDisplayFrom = (StringUtils.isNumeric(cookies.readCookieValue(GRID_PAGE_OFFSET_PROPERTY + "_" + userId)))
                        && (Integer.parseInt(cookies.readCookieValue(GRID_PAGE_OFFSET_PROPERTY + "_" + userId)) < totalFilteredAssignments) ? cookies
                        .readCookieValue(GRID_PAGE_OFFSET_PROPERTY + "_" + userId) : "0";
	}
	
	/**
	 * @param column_context
	 * @param personProperty
	 * @return
	 */
	private String getPropertyValue(String context , String ifNull){
		return (personProperty.get(context, PROPERTY) != null 
				&& personProperty.get(context, PROPERTY).length > 0 
				? personProperty.get(context, PROPERTY)[0] : ifNull); 
	}
	
	/**This method to replace user context value by new value.
	 * @param context
	 * @param property
	 * @param value
	 */
	private void replaceMyAssignmentsUserPropetry(String context, String value) {
        try {
            personProperty.removeAllValues(context, PROPERTY);
            if (StringUtils.isNotEmpty(value))
                personProperty.put(context, PROPERTY, value);
        } catch (PersistenceException pnetEx) {
            log.error("Database error while replacing column property in MyAssignments.replaceMyAssignmentsPanelPropetry(): "+ pnetEx.getMessage());
        }
    }
	
	/**
	 * This method is invoked for persisting user column size setting in database. 
	 * @param request
	 */
	private void replaceMyAssignmentsColumnSize(HttpServletRequest request) {
		if (StringUtils.isNotEmpty(request.getParameter("columnSize"))
				&& StringUtils.isNotEmpty(request.getParameter("columnName"))) {
			String[] columneSizeArray = request.getParameter("columnSize").split(",");
			String[] columnNameArray = request.getParameter("columnName").split(",");
			
			for (int index = 0; index < columneSizeArray.length; index++) {
				try {
					personProperty.removeAllValues(COLUMNSIZE_PROPERTY_CONTEXT + "_" + columnNameArray[index], PROPERTY);
					personProperty.put(COLUMNSIZE_PROPERTY_CONTEXT + "_" + columnNameArray[index], PROPERTY,
							columneSizeArray[index]);
				} catch (PersistenceException pnetEx) {
					log.error("Database error while replacing column property in MyAssignments.replaceMyAssignmentsColumnSize(): "
									+ pnetEx.getMessage());
				}
			}
		}
	}
	
	/**
	 * this method is invoked for setting grouping parameter
	 * @param request
	 */
	private void initializeGroupingParameter(HttpServletRequest request) {
		if (StringUtils.isNotEmpty(request.getParameter("toGroup")) && request.getParameter("toGroup").equals("true")) {
			groupingParameter += StringUtils.isNotEmpty(request.getParameter("groupingField")) ? request
					.getParameter("groupingField") : "";
		} else {
			groupingParameter = groupingParameter.replaceAll(StringUtils.isNotEmpty(request.getParameter("groupingField")) ? request
					.getParameter("groupingField") : "", "");
		}
		
		//Persist this setting
		replaceMyAssignmentsUserPropetry(FILTER_PROPERTY_CONTEXT + "_groupingField", groupingParameter);
	}
	
	/** 
	 * This method is for adding and assigning any task.
	 * @param request
	 * @return TextStreamResponse
	 */
	private TextStreamResponse addAndAssignTask(HttpServletRequest request) {
		String taskAssignmentCreationErrorMessage = "";
		String [] resourceIDs = request.getParameter("resourceIds").split(",");
		String [] percentAssignedDataList = request.getParameter("percentAssigned").split(",");
		try {
			scheduleEntry.setName(request.getParameter("taskName"));
			scheduleEntry.setDescription(request.getParameter("taskDescription"));
		    scheduleEntry.setMilestone(scheduleEntry.getWorkTQ().isZero() && (schedule.isAutocalculateTaskEndpoints() || (scheduleEntry.getStartTime().equals(scheduleEntry.getEndTime()))));
			scheduleEntry.store(schedule.isAutocalculateTaskEndpoints(), schedule);
			schedule.loadAll();
			ServiceFactory.getInstance().getAssignResourceService().assignResourcesToSingleTask(scheduleEntry,
					SessionManager.getUser(), null, schedule, resourceIDs, percentAssignedDataList, null, projectSpace);
			schedule.loadAll();
			addTaskService.indentTaskUnderSelectedTask(scheduleEntry.getID(), selectedTaskId, schedule);

		} catch (Exception e) {
			taskAssignmentCreationErrorMessage = e.getMessage();
			log.error("Error occured while storing and assigning task :" + e.getMessage());
		} 
		return new TextStreamResponse("text/json", taskAssignmentCreationErrorMessage);
	}
	
	/**
	 * This mehod generates project member list and initialize schedule entry.  
	 * @param request
	 * @return TextStreamResponse
	 */
	private TextStreamResponse getResourceAssignGridData(HttpServletRequest request) {
		return new TextStreamResponse("text/json", assignmentStoreDataFactory.getSpaceResourceGridStrng(projectSpace));
	}
	
	/**
	 * This mehod generates project member list and initialize schedule entry.  
	 * @param request
	 * @return TextStreamResponse
	 */
	private TextStreamResponse getNewAssignmentsData(HttpServletRequest request) {
		JSONStringer jsonStringer = new JSONStringer();
		selectedTaskId = request.getParameter("selectedTaskId");
		if (StringUtils.isNotEmpty(request.getParameter("projectId"))) {
			try {
				projectSpace = SpaceFactory.constructSpaceFromID(request.getParameter("projectId"));
				isActionAllowed = SessionManager.getSecurityProvider().isActionAllowed(request.getParameter("projectId"), Module.SCHEDULE, Action.CREATE);
			} catch (PersistenceException pnetEx) {
				log.error("Database Error occured while creating project space by projectId:" + pnetEx.getMessage());
			} 
		}
		
		try {
			jsonStringer.object();
			if (isActionAllowed) { // If create action is allowed for selected project for current user
				schedule = new Schedule();
				schedule.setSpace(projectSpace);
				schedule.loadAll();
				//IAddTaskService addTaskService = ServiceFactory.getInstance().getAddTaskService();
				scheduleEntry = addTaskService.getNewScheduleEntry(schedule, selectedTaskId);
				
				jsonStringer.key("error").value("");
				//jsonStringer.key("resourceGridData").value(assignmentStoreDataFactory.getSpaceResourceGridStrng(projectSpace));
				jsonStringer = populateJsonObjectForScheduleEntry(jsonStringer, scheduleEntry); 
			} else {
				// user is not space adimin for project
				jsonStringer.key("error").value(PropertyProvider.get("prm.personal.assignments.taskassignmentpermission.message"));
			}
		}catch(IllegalArgumentException ex){
			try {
				jsonStringer.key("error").value("Empty working Times.");
			} catch (JSONException pnetEx) {
				log.error("Json error occured while settng value Json Stringer: "+ pnetEx.getMessage());
			}
			log.error("non working day: "+ ex.getMessage());
		}catch (JSONException pnetEx) {
			log.error("Json error occured while settng value Json Stringer: "+ pnetEx.getMessage());
		} catch (PersistenceException pnetEx) {
			log.error("DataBase error occured while creating space and loading schedule: "+ pnetEx.getMessage());
		} catch (SQLException pnetEx) {
			log.error("Sql error occured while creating schedule entry: "+ pnetEx.getMessage());
		} 
		try {
			jsonStringer.endObject();
		} catch (JSONException pnetEx) {
			log.error("Json error occured while settng value Json Stringer: "+ pnetEx.getMessage());
		}
		return new TextStreamResponse("text/json", jsonStringer.toString());
	}
	
	/**
	 * this is method to calculate schedule entry
	 * @param request
	 * @return TextStreamResponse
	 */
	private TextStreamResponse getTaskAfterCalucaton(HttpServletRequest request) {
		String work = request.getParameter("work"); 
		String workUnit = request.getParameter("workUnit"); 
		String calculateBy = request.getParameter("calculateBy");
		String dateString = null;
		
		if(StringUtils.isNotEmpty(calculateBy) && calculateBy.equals("startDate")){
			dateString = request.getParameter("startDate");
		} else{
			dateString = request.getParameter("dueDate");
		}
		JSONStringer jsonStringer = new JSONStringer();
		try {
			jsonStringer.object();
			try {
				Date date = userDateFormat.parseDateString(dateString);
				scheduleEntry = addTaskService.reCalculateScheduleEntry(schedule, scheduleEntry, work, workUnit, date, calculateBy, SessionManager.getUser());
				jsonStringer.key("error").value("");
				jsonStringer = populateJsonObjectForScheduleEntry(jsonStringer, scheduleEntry);
			} catch (ParseException pnetEx) {
				jsonStringer.key("error").value(pnetEx.getMessage());
				log.error("Parsing error occured while reCalculating shedule entry: " + pnetEx.getMessage());
			} catch (InvalidDateException pnetEx) {
				jsonStringer.key("error").value(pnetEx.getMessage());
				log.error("Error occured while parsing date string to date type: " + pnetEx.getMessage());
			} catch (IllegalArgumentException pnetEx) {
				jsonStringer.key("error").value(pnetEx.getMessage());
				log.error("Error occured while calculating task: " + pnetEx.getMessage());
			}
			jsonStringer.endObject();
		} catch (JSONException pnetEx) {
			log.error("Json error occured while settng value Json Stringer: " + pnetEx.getMessage());
		} 
		return new TextStreamResponse("text/json", jsonStringer.toString());
	}
	
	/**
	 * @param jsonStringer
	 * @param scheduleEntry
	 * @return TextStreamResponse
	 */
	private JSONStringer populateJsonObjectForScheduleEntry(JSONStringer jsonStringer, ScheduleEntry scheduleEntry) {
		try {
			jsonStringer.key("startDate").value(scheduleEntry.getStartTimeString());
			jsonStringer.key("dueDate").value(scheduleEntry.getEndTimeString());
			jsonStringer.key("work").value(scheduleEntry.getWorkTQ().getAmount().doubleValue());
			jsonStringer.key("workUnit").value(scheduleEntry.getWorkTQ().getUnits().getUniqueID());
		} catch (JSONException pnetEx) {
			log.error("Json error occured while settng value Json Stringer: " + pnetEx.getMessage());
			pnetEx.printStackTrace();
		}
		return jsonStringer;
	}
	
	/**
	 * To stroe view settings
	 * @param name
	 * @param value
	 */
	private boolean storeViewSetting(String propertyName, String propertyValue){
	 	PersonProperty property = PersonProperty.getFromSession(getHttpServletRequest().getSession());
        property.setScope(ScopeType.SPACE.makeScope(SessionManager.getUser()));
        //Remove any old values
        try {
			property.replace(COLUMN_PROPERTY_CONTEXT, propertyName, propertyValue);
			return true;
		} catch (PersistenceException pnetEx) {
			log.error("Error occurred while storing view settings: " + pnetEx.getMessage());
			return false;
		}
	}
	
	/**
	 * To get stored view settings
	 * @param property
	 * @return
	 */
	public String getPersonalSettingsJSONString(PersonProperty property){
		JSONObject jSONObject = new JSONObject();
		AssignmentColumn assignmentColumn = assignmentListPage.getAssignmentColumn();
		String context = assignmentColumn.getColumnPropertyContext();
		property.prefetchForContextPrefix(context);
		try {
			//is right panel expanded
			jSONObject.put("rightTabsetExpanded", assignmentColumn.getProperty(property, context, "righttabsetexpanded",""));
			//left panel width
			jSONObject.put("assignmentListPanelWidth", assignmentColumn.getProperty(property, context, "tasklistpanelwidth",""));
			jSONObject.put("rightTabSet", assignmentColumn.getProperty(property, context, "rightTabSet",""));
			jSONObject.put("tasklisttablecontainerwidth", assignmentColumn.getProperty(property, context, "tasklisttablecontainerwidth",""));
			return JSONUtils.jsonToObjectLibertal(jSONObject, null);
		} catch (JSONException pnetEx) {
			log.error("Error occured while jsonToObjectLibertal : " + pnetEx.getMessage());
		}
		return null;
	}
	
	/**
	 * This method is to get a json string of filtered, late, coming due, should have started 
	 * and inprogress  assignments count, 
	 * @return TextStreamResponse
	 */
	private void getAssignmentsCount(){
		JSONStringer jsonStringer = new JSONStringer();
		try {
			jsonStringer.object();
			initializeAssignmentsCount();
			jsonStringer.key("totalFilteredAssignments").value(getTotalFilteredAssignments());
			jsonStringer.key("numberOfLateAssignments").value(getNumberOfLateAssignments());
			jsonStringer.key("numberOfComingDueAssignments").value(getNumberOfComingDueAssignments());
			jsonStringer.key("numberOfShouldHaveStartedAssignments").value(getNumberOfShouldHaveStartedAssignments());
			jsonStringer.key("numberOfInProgressAssignments").value(getNumberOfInProgressAssignments());
			jsonStringer.key("numberOfAllAssignments").value(getNumberOfAllAssignments());
			jsonStringer.key("numberOfCompletedAssignments").value(getNumberOfCompletedAssignments());
            jsonStringer.key("numberOfAllAssignedByMe").value(getNumberOfAllAssignedByMe());
			jsonStringer.endObject();
		} catch (JSONException pnetEx) {
			log.error("Json error occured while settng value Json Stringer: " + pnetEx.getMessage());
		}
  	    assignmentListPage.setAssignmentsCountString(jsonStringer.toString());
	}
	
	/**
	 * To get appropriate assignment list eg. late, comming due etc.
	 * @param activeTab
	 * @return
	 */
	private List<Node> getAssignmentListForActiveTab(String activeTab){
		if(activeTab.equalsIgnoreCase("panel_MyAssignments")){
			return assignmentList;
		}else if(activeTab.equalsIgnoreCase("panel_allAssignments")){
			return allAssignmentsList;
		}else if(activeTab.equalsIgnoreCase("panel_assignedByMe")){
			return assignedByMeAssignmentList;
		}else if(activeTab.equalsIgnoreCase("panel_completedAssignments")){
			return completedAssignmentsList;
		}else if(activeTab.equalsIgnoreCase("panel_inProgressAssignments")){
			return inProgressAssignmentList;
		}else if(activeTab.equalsIgnoreCase("panel_comingDue")){
			return comingDueAssignmentList;
		}else if(activeTab.equalsIgnoreCase("panel_shouldHaveStarted")){
			return shouldHaveStartedAssignmentList;
		}else if(activeTab.equalsIgnoreCase("panel_lateAssignments")){
			return lateAssignmentList;
		}
		return null;
	}
	/**
	 * @return the jSPRootURL
	 */
	public String getJSPRootURL() {
		return JSPRootURL;
	}

	/**
	 * @param rootURL
	 *            the jSPRootURL to set
	 */
	public void setJSPRootURL(String rootURL) {
		JSPRootURL = rootURL;
	}

	/**
	 * @return the validationMessageForBlogComment
	 */
	public String getValidationMessageForBlogComment() {
		return validationMessageForBlogComment;
	}

	/**
	 * @return the validationMessageForBlogEntry
	 */
	public String getValidationMessageForBlogEntry() {
		return validationMessageForBlogEntry;
	}

	/**
	 * @return the moduleId
	 */
	public Integer getModuleId() {
		return moduleId;
	}

	/**
	 * @return the spaceId
	 */
	public Integer getSpaceId() {
		return spaceId;
	}

	/**
	 * @return the filterParameter
	 */
	public String getAssigneeOrAssignorParameter() {
		return assigneeOrAssignorParameter;
	}

	/**
	 * @param filterParameter the filterParameter to set
	 */
	public void setAssigneeOrAssignorParameter(String filterParameter) {
		this.assigneeOrAssignorParameter = filterParameter;
	}

	/**
	 * @return the assignmentName
	 */
	public String getAssignmentName() {
		return assignmentName;
	}

	/**
	 * @param assignmentName the assignmentName to set
	 */
	public void setAssignmentName(String assignmentName) {
		this.assignmentName = assignmentName;
	}

	/**
	 * @return the assignmentNameComparator
	 */
	public String getAssignmentNameComparator() {
		return assignmentNameComparator;
	}

	/**
	 * @param assignmentNameComparator the assignmentNameComparator to set
	 */
	public void setAssignmentNameComparator(String assignmentNameComparator) {
		this.assignmentNameComparator = assignmentNameComparator;
	}

	/**
	 * @return the assignmentTypes
	 */
	public String[] getAssignmentTypes() {
		return assignmentTypes;
	}

	/**
	 * @param assignmentTypes the assignmentTypes to set
	 */
	public void setAssignmentTypes(String[] assignmentTypes) {
		this.assignmentTypes = assignmentTypes;
	}

	/**
	 * @return the comingDueDate
	 */
	public boolean isComingDueDate() {
		return comingDueDate;
	}

	/**
	 * @param comingDueDate the comingDueDate to set
	 */
	public void setComingDueDate(boolean comingDueDate) {
		this.comingDueDate = comingDueDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the lateAssignment
	 */
	public boolean isLateAssignment() {
		return lateAssignment;
	}

	/**
	 * @param lateAssignment the lateAssignment to set
	 */
	public void setLateAssignment(boolean lateAssignment) {
		this.lateAssignment = lateAssignment;
	}

	/**
	 * @return the percentComplete
	 */
	public Double getPercentComplete() {
		return percentComplete * 100;
	}

	/**
	 * @param percentComplete the percentComplete to set
	 */
	public void setPercentComplete(Double percentComplete) {
		this.percentComplete = percentComplete;
	}

	/**
	 * @return the projectIds
	 */
	public Integer[] getProjectIds() {
		return projectIds;
	}

	/**
	 * @param projectIds the projectIds to set
	 */
	public void setProjectIds(Integer[] projectIds) {
		this.projectIds = projectIds;
	}

	/**
	 * @return the shouldHaveStart
	 */
	public boolean isShouldHaveStart() {
		return shouldHaveStart;
	}

	/**
	 * @param shouldHaveStart the shouldHaveStart to set
	 */
	public void setShouldHaveStart(boolean shouldHaveStart) {
		this.shouldHaveStart = shouldHaveStart;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @param moduleId the moduleId to set
	 */
	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

	/**
	 * @return the inProgress
	 */
	public boolean isInProgress() {
		return inProgress;
	}

	/**
	 * @param inProgress the inProgress to set
	 */
	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}

	/**
	 * @return the projectOptionsString
	 */
	public String getProjectOptionsString() {
		return projectOptionsString;
	}

	/**
	 * @param projectOptionsString the projectOptionsString to set
	 */
	public void setProjectOptionsString(String projectOptionsString) {
		this.projectOptionsString = projectOptionsString;
	}

	/**
	 * @return the percentCompleteComparator
	 */
	public String getPercentCompleteComparator() {
		return percentCompleteComparator;
	}

	/**
	 * @param percentCompleteComparator the percentCompleteComparator to set
	 */
	public void setPercentCompleteComparator(String percentCompleteComparator) {
		this.percentCompleteComparator = percentCompleteComparator;
	}

	/**
	 * @return the businessOptionsString
	 */
	public String getBusinessOptionsString() {
		return businessOptionsString;
	}

	/**
	 * @param businessOptionsString the businessOptionsString to set
	 */
	public void setBusinessOptionsString(String businessOptionsString) {
		this.businessOptionsString = businessOptionsString;
	}

	/**
	 * @return the treeViewParamerer
	 */
	public String getAssignmentViewParameter() {
		return assignmentViewParameter;
	}

	/**
	 * @param treeViewParamerer the treeViewParamerer to set
	 */
	public void setAssignmentViewParameter(String assignmentViewParameter) {
		this.assignmentViewParameter = assignmentViewParameter;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the myAssignmentsColumn
	 */
	public String getMyAssignmentsColumn() {
		return myAssignmentsColumn;
	}

	/**
	 * @param myAssignmentsColumn the myAssignmentsColumn to set
	 */
	public void setMyAssignmentsColumn(String myAssignmentsColumn) {
		this.myAssignmentsColumn = myAssignmentsColumn;
	}

	/**
	 * @return the defaultPageSize
	 */
	public String getDefaultPageSize() {
		return defaultPageSize;
	}

	/**
	 * @param defaultPageSize the defaultPageSize to set
	 */
	public void setDefaultPageSize(String defaultPageSize) {
		this.defaultPageSize = defaultPageSize;
	}

	/**
	 * @return the totalAssignment
	 */
	public Integer getTotalFilteredAssignments() {
		return totalFilteredAssignments;
	}

	/**
	 * @param totalAssignment the totalAssignment to set
	 */
	public void setTotalFilteredAssignments(Integer totalAssignment) {
		this.totalFilteredAssignments = totalAssignment;
	}

	/**
	 * @return the groupingParameter
	 */
	public String getGroupingParameter() {
		return groupingParameter;
	}

	/**
	 * @param groupingParameter the groupingParameter to set
	 */
	public void setGroupingParameter(String groupingParameter) {
		this.groupingParameter = groupingParameter;
	}

	/**
	 * @return the versionNumber
	 */
	public String getVersionNumber() {
		return versionNumber;
	}

	/**
	 * @return the myAssignmentsColumnSize
	 */
	public String getMyAssignmentsColumnSize() {
		return myAssignmentsColumnSize;
	}

	/**
	 * @param myAssignmentsColumnSize the myAssignmentsColumnSize to set
	 */
	public void setMyAssignmentsColumnSize(String myAssignmentsColumnSize) {
		this.myAssignmentsColumnSize = myAssignmentsColumnSize;
	}

	/**
	 * @return the rightPanelCollapsed
	 */
	public boolean isRightPanelCollapsed() {
		return rightPanelCollapsed;
	}

	/**
	 * @param rightPanelCollapsed the rightPanelCollapsed to set
	 */
	public void setRightPanelCollapsed(boolean rightPanelCollapsed) {
		this.rightPanelCollapsed = rightPanelCollapsed;
	}

	/**
	 * @return the rightPanelWidth
	 */
	public String getRightPanelWidth() {
		return rightPanelWidth;
	}

	/**
	 * @param rightPanelWidth the rightPanelWidth to set
	 */
	public void setRightPanelWidth(String rightPanelWidth) {
		this.rightPanelWidth = rightPanelWidth;
	}

	/**
	 * @return the actionsIconEnabled
	 */
	public boolean isActionsIconEnabled() {
		return actionsIconEnabled;
	}

	/**
	 * @param actionsIconEnabled the actionsIconEnabled to set
	 */
	public void setActionsIconEnabled(boolean actionsIconEnabled) {
		this.actionsIconEnabled = actionsIconEnabled;
	}

	/**
	 * @return the isAgileWorkRemainingEnabled
	 */
	public boolean getIsAgileWorkRemainingEnabled() {
		return isAgileWorkRemainingEnabled;
	}

	/**
	 * @return the userDateForamtString
	 */
	public String getUserDateFormatString() {
		return userDateFormatString;
	}

	/**
	 * @param userDateForamtString the userDateForamtString to set
	 */
	public void setUserDateFormatString(String userDateFormatString) {
		this.userDateFormatString = userDateFormatString;
	}
	/**
	 * @return the startDisplayFrom
	 */
	public String getStartDisplayFrom() {
		return startDisplayFrom;
	}

	/**
	 * @param startDisplayFrom the startDisplayFrom to set
	 */
	public void setStartDisplayFrom(String startDisplayFrom) {
		this.startDisplayFrom = startDisplayFrom;
	}

	/**
	 * @return the userDisplayName
	 */
	public String getUserDisplayName() {
		return userDisplayName;
	}
	
	/**
	 * @return the assignmentTypesCSV
	 */
	public String getAssignmentTypesCSV() {
		String assignmentTypesCSV = "";
		for (String assignmentType : assignmentTypes) {
			if (StringUtils.isNotEmpty(assignmentType))
				assignmentTypesCSV += "'"+assignmentType+"',";
		}
		return assignmentTypesCSV.substring(0, assignmentTypesCSV.length()-1);
	}
	
	/**
	 * @return the projectIdsCSV
	 */
	public String getProjectIdsCSV() {
		String projectIdsCSV = "";
		if (projectIds != null) {
			for (Integer projectId : projectIds) {
				projectIdsCSV += "'" + projectId + "',";
			}
		}else{
            projectIdsCSV += "'0',";
        }
		return projectIdsCSV.length() > 0 ? projectIdsCSV.substring(0, projectIdsCSV.length() - 1): "''";
	}
	
	/**
	 * @return StartDateString
	 */
	public String getStartDateString(){
		return startDate != null ? userDateFormat.formatDate(startDate) : userDateFormat.getDateFormatExample();
	}
	
	/**
	 * @return EndDateString
	 */
	public String getEndDateString(){
		return endDate != null ? userDateFormat.formatDate(endDate) : userDateFormat.getDateFormatExample();
	}

	/**
	 * @return the businessId
	 */
	public Integer getBusinessId() {
		return businessId;
	}

	/**
	 * @return the numberOfComingDueAssignments
	 */
	public Integer getNumberOfComingDueAssignments() {
		return numberOfComingDueAssignments;
	}

	/**
	 * @return the numberOfInProcessAssignments
	 */
	public Integer getNumberOfInProgressAssignments() {
		return numberOfInProgressAssignments;
	}

	/**
	 * @return the numberOfLateAssignments
	 */
	public Integer getNumberOfLateAssignments() {
		return numberOfLateAssignments;
	}

	/**
	 * @return the numberOfShouldHaveStartedAssignments
	 */
	public Integer getNumberOfShouldHaveStartedAssignments() {
		return numberOfShouldHaveStartedAssignments;
	}
	/**
	 * @return the numberofCompletedAssignments
	 */
	public Integer getNumberOfCompletedAssignments() {
		return numberOfCompletedAssignments;
	}

	/**
	 * @return the numberofAllAssignments
	 */
	public Integer getNumberOfAllAssignments() {
		return numberOfAllAssignments;
	}
    /**
     * @return the userLocaleMonthList
     */
    public String getUserLocaleMonthList(){
        return StringUtils.getJsonMonthsString();
    }

    /**
     * @return the numberofAllAssignedByMe
     */
    public Integer getNumberOfAllAssignedByMe() {
        return numberOfAllAssignedByMe;
    }

	/**
	 * @return the noProjectTaskAvailableMsg
	 */
	public String getNoProjectTaskAvailableMsg() {
		return noProjectTaskAvailableMsg;
	}
	/**
	 * @return pnet tab set width.
	 */
	public int getPnetTabSetWidth() {
		return getWindowWidth() - 217;
	}

	/**
	 * @return the sliding panel width.
	 */
	public int getSlidingPanelContentWidth() {
		return getWindowWidth() - 231;
	}
	
	/**
	 * @return sliding panel tool bar width.
	 */
	public int getSlidingPanelToolBarWidth(){
		return getWindowWidth() - 223;
	}
	
	/**
	 * @return the task list header Width
	 */
	public int getTaskListHeaderWidth() {
		return getWindowWidth() - 248;
	}

	/**
	 * @return the bottombar width
	 */
	public int getBottomBarWidth() {
		return getWindowWidth() - 225;
	}

	/**
	 * first checks for personal property if not then get sum of visible column width.
	 * @return the task list container table width
	 */
	public int getAssignmentListTableContainerWidth() {
		String context = assignmentListPage.getAssignmentColumn().getColumnPropertyContext();
		String width = assignmentListPage.getAssignmentColumn().getProperty(property, context, "tasklisttablecontainerwidth", "null");
		return StringUtils.isNumeric(width) ? Integer.valueOf(width) : assignmentListPage.getAssignmentColumn().getVisibleColumnsWidth();
	}
	
	/**
	 * To generate assignment name comparator option list
	 * @return
	 */
	public String getAssignmentNameComparatorModel() {
		return TextComparator.getOptionList(TextComparator.CONTAINS);
	}

	/**
	 * To generate work percent complete comparator option list
	 * @return
	 */
	public String getWorkPercentCompleteComparatorModel() {
		return NumberComparator.getOptionList(NumberComparator.DEFAULT);
	}

	/**
	 * @return the pnet tab set height
	 */
	public int getPnetTabSetHeight() {
		return (getWindowHeight() - 125);
	}

	/**
	 * @return the tab content tab height
	 */
	public int getTabContentHeight() {
		return (getWindowHeight() - 202);
	}

	
	/**
	 * @return the splitter bar height
	 */
	public int getSplitterBarHeight() {
		return (getWindowHeight() - 152);
	}
	
    public String getLeftHeaderMyAssignments() {
    	return PropertyProvider.get("prm.personal.assignments.leftheading.message");
    }
    
    public String getNewAssignmentLabel() {
    	return PropertyProvider.get("prm.personal.assignments.newassignment.label");
    }
    
    public String getClearFilterLabel() {
    	return PropertyProvider.get("prm.personal.assignments.clearfilters.label");
    }
    
    public String getApplyFilterLabel() {
    	return PropertyProvider.get("prm.personal.assignments.applyfilters.label");
    }
    
    /**
     * Column settings title
     * 
     * @return String
     */
    public String getColumnSettingsTitle() {
    	return PropertyProvider.get("prm.personal.assignment.link.columnsettings.label");
    }
    
    /**
     * Expand all title
     * 
     * @return String
     */
    public String getExpandAllTitle(){
    	return PropertyProvider.get("prm.personal.assignment.expandall.label");
    }
    
    /**
     * Collapse all title
     * 
     * @return String
     */
    public String getCollapseAllTitle(){
    	return PropertyProvider.get("prm.personal.assignment.collapseall.label");
    }
}
