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
package net.project.view.pages.project;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.finder.NumberComparator;
import net.project.base.finder.NumberFilter;
import net.project.base.property.PropertyProvider;
import net.project.calendar.CalendarBean;
import net.project.calendar.ICalendarEntry;
import net.project.calendar.MyMeetings;
import net.project.channel.ScopeType;
import net.project.channel.State;
import net.project.gui.history.BusinessLevel;
import net.project.gui.history.History;
import net.project.gui.history.HistoryException;
import net.project.gui.history.ProjectLevel;
import net.project.hibernate.constants.WeblogConstants;
import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.model.PnPersonProperty;
import net.project.hibernate.model.PnWeblog;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.model.project_space.ObjectChanged;
import net.project.hibernate.model.project_space.ProjectChanges;
import net.project.hibernate.model.project_space.ProjectPhase;
import net.project.hibernate.model.project_space.ProjectSchedule;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.service.IBlogProvider;
import net.project.hibernate.service.IPnAssignmentService;
import net.project.hibernate.service.IPnProjectSpaceService;
import net.project.hibernate.service.ServiceFactory;
import net.project.material.MaterialAssignment;
import net.project.material.MaterialAssignmentList;
import net.project.material.MaterialBean;
import net.project.material.MaterialBeanList;
import net.project.material.PnMaterialList;
import net.project.news.News;
import net.project.news.NewsList;
import net.project.news.NewsManager;
import net.project.persistence.PersistenceException;
import net.project.portfolio.PortfolioManager;
import net.project.portfolio.ProjectPortfolio;
import net.project.project.ProjectSpace;
import net.project.project.ProjectSpaceBean;
import net.project.resource.PersonProperty;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskFinder;
import net.project.schedule.TaskType;
import net.project.security.Action;
import net.project.security.SecurityProvider;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;
import net.project.space.SpaceList;
import net.project.space.SpaceManager;
import net.project.util.DateFormat;
import net.project.util.HttpUtils;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;
import net.project.wiki.WikiURLManager;

import org.apache.commons.collections.CollectionUtils;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;
import org.slf4j.Logger;

public class Dashboard extends BasePage {

	private static Logger log = logger;

	private ProjectSpaceBean projectSpace;

	private String projectName;

	private String projectDescription;

	private String projectManager;

	private String projectStatus;

	private String percent;

	private int peopleAssigned;

	private int numberLateTasks;

	private int numberComingDueTasks;

	private int numberUnassignedTasks;

	private int numberCompletedTasks;

	@Persist
	private String projectId;

	private String startDate;

	private String startActualDate;

	private String finishDate;

	private String finishActualDate;

	@Persist
	private List<ObjectChangedWrapper> documents;

	private ObjectChangedWrapper document;

	@Persist
	private List<ObjectChangedWrapper> forms;

	private ObjectChangedWrapper form;

	@Persist
	private List<ObjectChangedWrapper> discussions;

	private ObjectChangedWrapper discussion;

	@Persist
	private List<TeammateWrapper> teammates;
	
	private TeammateWrapper teammate;
	
	@Persist
	private List<ProjectPhaseWrapper> phases;

	private ProjectPhaseWrapper phase;

	@Persist
	private List<PnTaskWrapper> milestones;

	private boolean hasMilestones;

	private PnTaskWrapper milestone;

	@Persist
	private List<CalendarWrapper> meetings;

	private CalendarWrapper meeting;

	@Persist
	private List<NewsWrapper> newsList;
	
	private NewsWrapper news;

	private boolean hasNews;
	
	private MaterialBeanList materialsList;
	
	private MaterialBean material;		
	
	private boolean hasMaterials;

	@Persist
	private List<ProjectSpaceWrapper> subprojects;

	private ProjectSpaceWrapper subproject;

	private boolean hasSubprojects;

	@Inject
	private RequestGlobals requestGlobals;

	private boolean hasDocumentsChanges;

	private boolean hasMeetings;

	private boolean hasFormChanges;

	private boolean hasDiscussionsChanges;

	// labels
	private String projectLabel;

	private String descriptionLabel;

	private String managerLabel;

	private String statusLabel;

	private String completionLabel;

	private String projectNewsTitle;

	private String lastChangesTitle;

	private String upcomingMeetingTitle;

	private String scheduleLabel;

	private String startLabel;

	private String finishLabel;

	private String plannedLabel;

	private String actualLabel;

	private String lateTasksLink;

	private String taskComingDueThisWeekLink;

	private String unassignedTasksLink;

	private String tasksCompletedLink;

	private String phasesMilestonesTitle;

	private String nameColumnLabel;

	private String endDateColumnLabel;

	private String statusColumnLabel;

	private String progressColumnLabel;

	private String subprojectsTitle;

	private String projectColumnLabel;

	private String overalStatusColumnLabel;

	private String financialStatusColumnLabel;

	private String scheduleStatusColumnLabel;

	private String resourceStatusColumnLabel;

	private String completionPercentageColumnLabel;

	private String resourcesLabel;

	private String peopleAssignedText;

	private String teammatesLink;

	private String nameTeammateColumnLabel;

	private String assignmentsTeammateColumnLabel;
	
	@Persist
	private String pageTitle;	
	
	private Integer moduleId;
	
	@Persist
	private HttpServletRequest request;
	
	@Persist
	private CalendarBean calendar;
	
	// variables for minimize maximize functionality
	@Persist
	private String spaceName;
	
	private final String PROPERTY = "state";	
	
	private boolean meetingsState;
	
	private boolean phasesState;
	
	private boolean subprojectsState;
	
	private boolean teammatesState;
	
	private boolean projectNewsState;
	
	private boolean materialsState;	
	
	private boolean projectSheduleState;
	
	private boolean projectLastChangesState;
	
	private boolean projectDetailState;
	
	private boolean projectPieChartState;
	
	private boolean projectFinancialChartState;	
	
	private boolean projectCompletionState;
	
	private Integer spaceId;
	
	private Integer personId;
	
	private final String PROJECT_SPACE_MEETINGS = "ProjectSpace_Meetings_";
	
	private final String PROJECT_SPACE_PHASES = "ProjectSpace_phases_";

	private final String PROJECT_SPACE_MILESTONES = "ProjectSpace_Milestones_";

	private final String PROJECT_SPACE_SUBPROJECTS = "ProjectSpace_subprojects_";

	private final String PROJECT_SPACE_TEAMMATES = "ProjectSpace_TeamMembers_";

	private final String PROJECT_SPACE_PROJECT_NEWS = "ProjectSpace_News_";
	
	private final String PROJECT_SPACE_MATERIALS = "ProjectSpace_Materials_";	

	private final String PROJECT_SPACE_PROJECT_SCHEDULE = "ProjectSpace_Shedule_";

	private final String PROJECT_SPACE_PROJECT_CHANGES = "ProjectSpace_LastChanges_";

	private final String PROJECT_SPACE_PROJECT_PROJECT_DETAILS = "ProjectSpace_ProjectDetails_";

	private final String CHANNEL_PROPERTY_CONTEXT = "net.project.channel.";
	
	private final String PROJECT_SPACE_PIE_CHART = "ProjectSpace_PieChart_";
	
	private final String PROJECT_SPACE_FINANCIAL_CHART = "ProjectSpace_FinancialChart_";	
	
	private final String PROJECT_SPACE_PROJECT_COMPLETION = "ProjectSpace_ProjectCompletion_";
	
	private Integer totalEntriesCount;
	
	private Integer wikiEntryCount;
	
	private boolean showSkypeStatus;
	
	private boolean showTeammateOnlineStatus;
	
	private Integer personalModuleId;
	
	private String personalSpaceType;
	
	private String blogCountMessage;
	
	private String wikiCountMessage;
	
	private String formsCountMessage;
	
	private String documentsCountMessage;
	
	private String discussionsCountMessage;
	
	private boolean newsCloseState;
	
	private boolean materialsCloseState;	

	private boolean teamatesCloseState;

	private boolean meetingsCloseState;

	private boolean lastChangesCloseState;

	private boolean phasesCloseState;

	private boolean subProjectCloseState;

	private boolean pieChartCloseState;
	
	private boolean financialChartCloseState;	

	private boolean projectCompletionCloseState;

	private final String MEETINGS_TITLE = "Upcoming Meetings";

	private final String PHASES_TITLE = "Phases And Milestones";

	private final String SUBPROJECTS_TITLE = "Subprojects";

	private final String TEAMMATES_TITLE = "Teammates Online";

	private final String PROJECT_NEWS_TITLE = "Project News";
	
	private final String MATERIALS_TITLE = "Project Materials";	

	private final String PROJECT_CHANGES_TITLE = "Last Changes Within 5 Days";

	private final String BAR_CHART_TITLE = "Bar Chart";
	
	private final String FINANCIAL_CHART_TITLE = "Project Financial";	

	private final String PROJECT_COMPLETION_TITLE = "Project Completion";
	
	private String logoUrl;
	
	private boolean projectLogo;
	
	private boolean actionsIconEnabled;
	
	private boolean blogEnabled;
	
	private String subProjectName; 
    
	private String parentURL;
	
	private String overAssignedTitle;
	
	private String assignedTitle;
	
	private String closeTitle;
	
	private String upTitle;
	
	private String downTitle;
	
	private boolean moreMessage = false;
	
	@Property
	private String blogitTooltip;
	
	@Property
	private String editProjectTooltip;
	
	@Property
	private String viewPropertiesTooltip;
	
	@Property
	private String personalizePageTooltip;
	
	@Property
	private String projectTeamChannel;
	
	@Property
	private String projectTeamAssignmentTooltip;
	
	@Property
	private String skypeStatusTooltip;
	
	/**
	 * Method to initialize tokens 
	 */
	public void initializeTokens() {
		projectLabel = PropertyProvider
				.get("prm.project.viewproject.project.label");
		descriptionLabel = PropertyProvider
				.get("prm.project.viewproject.description.label");
		managerLabel = PropertyProvider
				.get("prm.project.viewproject.mananger.label");
		statusLabel = PropertyProvider
				.get("prm.project.viewproject.status.label");
		completionLabel = PropertyProvider
				.get("prm.project.viewproject.completion.label");
		projectNewsTitle = PropertyProvider
				.get("prm.project.viewproject.projectnews.title");
		lastChangesTitle = PropertyProvider
				.get("prm.project.viewproject.lastchanges.title");
		upcomingMeetingTitle = PropertyProvider
				.get("prm.project.viewproject.upcomingmeeting.title");
		scheduleLabel = PropertyProvider
				.get("prm.project.viewproject.schedule.label");
		startLabel = PropertyProvider
				.get("prm.project.viewproject.start.label");
		finishLabel = PropertyProvider
				.get("prm.project.viewproject.finish.label");
		plannedLabel = PropertyProvider
				.get("prm.project.viewproject.planned.label");
		actualLabel = PropertyProvider
				.get("prm.project.viewproject.actual.label");
		lateTasksLink = PropertyProvider
				.get("prm.project.viewproject.latetasks.link");
		taskComingDueThisWeekLink = PropertyProvider
				.get("prm.project.viewproject.taskcomingduethisweek.link");
		unassignedTasksLink = PropertyProvider
				.get("prm.project.viewproject.unassignedtasks.link");
		tasksCompletedLink = PropertyProvider
				.get("prm.project.viewproject.taskscompleted.link");
		phasesMilestonesTitle = PropertyProvider
				.get("prm.project.viewproject.phasesmilestones.title");
		nameColumnLabel = PropertyProvider
				.get("prm.project.viewproject.namecolumn.label");
		endDateColumnLabel = PropertyProvider
				.get("prm.project.viewproject.enddate.label");
		statusColumnLabel = PropertyProvider
				.get("prm.project.viewproject.statuscolumn.label");
		progressColumnLabel = PropertyProvider
				.get("prm.project.viewproject.progresscolumn.label");
		subprojectsTitle = PropertyProvider
				.get("prm.project.viewproject.subprojects.title");
		projectColumnLabel = PropertyProvider
				.get("prm.project.viewproject.projectcolumn.label");
		overalStatusColumnLabel = PropertyProvider
				.get("prm.project.viewproject.overalstatuscolumn.label");
		financialStatusColumnLabel = PropertyProvider
				.get("prm.project.viewproject.financialstatuscolumn.label");
		scheduleStatusColumnLabel = PropertyProvider
				.get("prm.project.viewproject.schedulestatuscolumn.label");
		resourceStatusColumnLabel = PropertyProvider
				.get("prm.project.viewproject.resourcestatuscolumn.label");
		completionPercentageColumnLabel = PropertyProvider
				.get("prm.project.viewproject.completionpercentagecolumn.label");
		resourcesLabel = PropertyProvider
				.get("prm.project.viewproject.resources.label");
		peopleAssignedText = PropertyProvider
				.get("prm.project.viewproject.peopleassigned.text");
		teammatesLink = PropertyProvider
				.get("prm.project.viewproject.teammates.link");
		nameTeammateColumnLabel = PropertyProvider
				.get("prm.project.viewproject.nameteammatecolumn.label");
		assignmentsTeammateColumnLabel = PropertyProvider
				.get("prm.project.viewproject.assignmentsteammatecolumn.label");
		showSkypeStatus = PropertyProvider.getBoolean("prm.global.skype.isenabled");
		showTeammateOnlineStatus = PropertyProvider.getBoolean("prm.project.viewproject.showteammateonlinestatus.isenabled");
        actionsIconEnabled = PropertyProvider.getBoolean("prm.global.actions.icon.isenabled");
        blogEnabled = PropertyProvider.getBoolean("prm.blog.isenabled");
        overAssignedTitle = PropertyProvider.get("prm.project.dashboard.overassigned.title");
		assignedTitle = PropertyProvider.get("prm.project.dashboard.assigned.title");
		closeTitle = PropertyProvider.get("all.global.channelbarbutton.title.close");
		upTitle = PropertyProvider.get("all.global.channelbarbutton.title.minimize");
		downTitle = PropertyProvider.get("all.global.channelbarbutton.title.maximize");
		blogitTooltip = PropertyProvider.get("all.global.toolbar.standard.blogit");
		editProjectTooltip = PropertyProvider.get("prm.project.main.modify.button.tooltip");
		viewPropertiesTooltip = PropertyProvider.get("prm.project.main.properties.button.tooltip");
		personalizePageTooltip = PropertyProvider.get("prm.project.main.personalize.button.tooltip");
		projectTeamAssignmentTooltip = PropertyProvider.get("prm.project.dashboard.projectTeam.assignments.tooltip");
		skypeStatusTooltip = PropertyProvider.get("prm.project.dashboard.projectTeam.skypestatus.tooltip");
	}	
	
	/**
	* Method for setting current project space as user's current space
	*/
	 private void setUserCurrentSpace(){
		ProjectSpaceBean project = (ProjectSpaceBean) requestGlobals.getHTTPServletRequest().getSession().getAttribute("projectSpace");
		projectId = requestGlobals.getHTTPServletRequest().getParameter("id");
		try {
			if(project == null && !projectSpace.getID().equals(projectId)){
				project = new ProjectSpaceBean();
				project.setID(projectId);
				project.load();
			} else {
				project.load();	
			}
		} catch (PersistenceException pnetEx) {
			log.error("Error occured while loading project space : "+pnetEx.getMessage());
		}
		try {
			SessionManager.getUser().setCurrentSpace(project);
		} catch (PnetException pnetEx) {
			log.error("Error occured while setting project as current space : "+pnetEx.getMessage());
		}
		projectSpace = project;
	}
	 
	Object onActivate() {
		if(checkForUser() != null) {
			return checkForUser();
		}
		HttpServletRequest request = requestGlobals.getHTTPServletRequest();
		String projectId = request.getParameter("id");
		if (StringUtils.isNotEmpty(projectId)) {
			// Security Check: Is user allowed access to requested space?
			SecurityProvider securityProvider = SessionManager.getSecurityProvider();
			Space checkSpace = new ProjectSpaceBean();
			checkSpace.setID(projectId);
			Space oldSpace = securityProvider.getSpace();
			securityProvider.setSpace(checkSpace);
			if (securityProvider.isActionAllowed(null, Integer.toString(net.project.base.Module.PROJECT_SPACE),
			                                     net.project.security.Action.VIEW)) {
			    // Passed Security Check
				initializeProjectDashboardView();
	        	setUserCurrentSpace();
			} else {
	            // Failed Security Check
	            securityProvider.setSpace(oldSpace);
	            request.setAttribute("exception", new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.project.main.authorizationfailed.message"), checkSpace));
	            try {
					RequestDispatcher requestDispatcher = request.getRequestDispatcher("/AccessDenied.jsp");
					requestDispatcher.forward(request, requestGlobals.getHTTPServletResponse());
				} catch (IOException e) {
					log.error("Error occured while setting project as current space : "+e.getMessage());
				} catch (ServletException e) {
					log.error("Error Occured while redirecting to error page : "+e.getMessage());
				}
			}
		} else {
			if(SessionManager.getUser().getCurrentSpace() instanceof ProjectSpaceBean){
			     projectSpace = (ProjectSpaceBean)SessionManager.getUser().getCurrentSpace();
			}
			if (StringUtils.isNotEmpty(projectSpace.getID())) {
		        try {
					projectSpace.load();
				} catch (PersistenceException e) {
					log.error("Error occured while loading current space." + e.getMessage());
				}
			}
	    }
		return null;
	} 

	/**
	 * Method called on page activation with single parameter
	 * 
	 * @param action
	 *            to perform
	 */
	void onActivate(String action) {		
		HttpServletRequest request = requestGlobals.getHTTPServletRequest();
		if (action.equalsIgnoreCase("saveContextChange")) {
			replaceContextState(request);
		}	
	}

	/**
	 * Seting up values before page render
	 */
	@SetupRender
	void setValues() {
		try {
			initializeTokens();
			User user = SessionManager.getUser();
			DateFormat userDateFormatter = user.getDateFormatter();
			String id = requestGlobals.getHTTPServletRequest().getParameter("id");
			if(StringUtils.isNotEmpty(id)){
				projectId = id;
			}
			//Get Sub Project Name
			if (projectSpace.isSubproject()) {
				ProjectSpace parentProject = (ProjectSpace) SpaceManager.getSuperProject(projectSpace);
				if ((parentProject != null) && (parentProject.getID() != null)) {
					parentProject.load();
					if (parentProject.getRecordStatus() != null && parentProject.getRecordStatus().equals("A")) {
			        	parentURL = SessionManager.getJSPRootURL()+"/project/Dashboard?id="+parentProject.getID();
						subProjectName = parentProject.getName();
			        }
				}
			}
			moduleId = Module.PROJECT_SPACE;
			personalModuleId = Module.PERSONAL_SPACE;
			personalSpaceType = Space.PERSONAL_SPACE;
			requestGlobals.getHTTPServletRequest().getSession().setAttribute("projectSpace", projectSpace);
			projectName = projectSpace.getName();
			projectDescription = projectSpace.getDescription();
			if(projectDescription != null && projectDescription.length() > 100){
				projectDescription = projectDescription.substring(0, 100) + "..";
			}
			projectManager = projectSpace.getMetaData().getProperty("ProjectManager");
			projectStatus = projectSpace.getStatus();
			percent = projectSpace.getPercentComplete();
			projectSpace.getProjectLogoID();
			peopleAssigned = ServiceFactory.getInstance().getPnPersonService().getPersonsByProjectId(Integer.parseInt(projectSpace.getID())).size();
			projectTeamChannel = PropertyProvider.get("prm.project.dashboard.projectTeam.channel.title", "" + peopleAssigned);
			IPnProjectSpaceService projectSpaceService = ServiceFactory.getInstance().getPnProjectSpaceService();
			ProjectSchedule projectSchedule = projectSpaceService.getProjectSchedule(Integer.parseInt(projectSpace.getID()));

			numberComingDueTasks = projectSchedule.getNumberOfTaskComingDue();
			numberCompletedTasks = projectSchedule.getNumberOfCompletedTasks();
			numberLateTasks = projectSchedule.getNumberOfLateTasks();
			numberUnassignedTasks = projectSchedule.getNumberOfUnassignedTasks();

			if (projectSchedule.getPlannedStart() != null) {
				startDate = userDateFormatter.formatDateMedium(projectSchedule.getPlannedStart());
			} else {
				startDate = "";
			}

			if (projectSchedule.getActualStart() != null) {
				startActualDate = userDateFormatter.formatDateMedium(projectSchedule.getActualStart());
			} else {
				startActualDate = "";
			}

			if (projectSchedule.getPlannedFinish() != null) {
				finishDate = userDateFormatter.formatDateMedium(projectSchedule.getPlannedFinish());
			} else {
				finishDate = "";
			}

			if (projectSchedule.getActualFinish() != null) {
				finishActualDate = userDateFormatter.formatDateMedium(projectSchedule.getActualFinish());
			} else {
				finishActualDate = "";
			}

			ProjectChanges changes = projectSpaceService.getProjectChanges(Integer.parseInt(projectSpace.getID()), 5);
			List<ObjectChanged> documentsAux = changes.getDocuments();
			documents = new ArrayList<ObjectChangedWrapper>();
			if (documentsAux != null) {
				hasDocumentsChanges = documentsAux.size() > 0;
				Iterator<ObjectChanged> iDocuments = documentsAux.iterator();
				while (iDocuments.hasNext()) {
					documents.add(new ObjectChangedWrapper(iDocuments.next()));
				}
			}
			
			if (CollectionUtils.isNotEmpty(documents)) {
				documentsCountMessage = documents.size() + " " + PropertyProvider.get((documents.size() > 1 ? "prm.project.dashboard.changeswithindays.newentries.message" : "prm.project.dashboard.changeswithindays.newentry.message")); 
			}

			List<ObjectChanged> formsAux = changes.getForms();
			forms = new ArrayList<ObjectChangedWrapper>();
			if (formsAux != null) {
				hasFormChanges = formsAux.size() > 0;
				Iterator<ObjectChanged> iForms = formsAux.iterator();
				while (iForms.hasNext()) {
					forms.add(new ObjectChangedWrapper(iForms.next()));
				}
			}
			
			if (CollectionUtils.isNotEmpty(forms)) {
				formsCountMessage = forms.size() + " " + PropertyProvider.get((forms.size() > 1 ? "prm.project.dashboard.changeswithindays.newentries.message" : "prm.project.dashboard.changeswithindays.newentry.message"));				
			}
			
			List<ObjectChanged> discussionsAux = changes.getDiscussions();
			discussions = new ArrayList<ObjectChangedWrapper>();
			int postsCount = 0;
			if (discussionsAux != null) {
				hasDiscussionsChanges = discussionsAux.size() > 0;
				Iterator<ObjectChanged> iDiscussions = discussionsAux.iterator();
				while (iDiscussions.hasNext()) {
					ObjectChangedWrapper objectChangedWrapper = new ObjectChangedWrapper(iDiscussions.next());
					discussions.add(objectChangedWrapper);
					postsCount += objectChangedWrapper.getObjectChanged().getNumberOfChanges();
				}
			}
			
			if (CollectionUtils.isNotEmpty(discussions)) {
				discussionsCountMessage = postsCount + " " + PropertyProvider.get((postsCount > 1 ? "prm.project.dashboard.changeswithindays.newentries.message" : "prm.project.dashboard.changeswithindays.newentry.message"));
			}

			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, 1);
			Date start = c.getTime();
			c.add(Calendar.MONTH, 3);
			Date finish = c.getTime();

			IBlogProvider blogProvider = ServiceFactory.getInstance().getBlogProvider();
			IPnAssignmentService assignmentService = ServiceFactory.getInstance().getPnAssignmentService();
			List<Teammate> auxTeammates = assignmentService.getAssignmentsByPersonForProject(Integer.parseInt(projectSpace.getID()), start, finish);
			Iterator<Teammate> iTeammates = auxTeammates.iterator();
			teammates = new ArrayList<TeammateWrapper>();
			while (iTeammates.hasNext()) {
					Teammate teammate = iTeammates.next();
					PnWeblogEntry weblogEntry = null;
					teammates.add(new TeammateWrapper(teammate, weblogEntry));
			}
			Calendar calendar = Calendar.getInstance(); 
			calendar.add(Calendar.DATE, -5);
			Date blogEntryStartDate = calendar.getTime();
			
			// get project blog
			PnWeblog projectWeblog = blogProvider.getWeblogBySpaceId(Integer.parseInt(projectId), false);
			
			if(projectWeblog != null){
				// getting last five days entries in project blog
				totalEntriesCount = blogProvider.getCountOfBlogEntries(projectWeblog.getWeblogId(), null, WeblogConstants.STATUS_PUBLISHED, blogEntryStartDate, start);
				if (totalEntriesCount != null && totalEntriesCount > 0) {
					blogCountMessage = totalEntriesCount + " " + PropertyProvider.get((totalEntriesCount > 1 ? "prm.project.dashboard.changeswithindays.newentries.message" : "prm.project.dashboard.changeswithindays.newentry.message"));
				}
			}
			
			// getting last five days entries in project wiki 
			wikiEntryCount = ServiceFactory.getInstance().getPnWikiPageService().getWikiPageCountForProject(Integer.parseInt(projectId), blogEntryStartDate, start);
			wikiCountMessage = wikiEntryCount + " " + PropertyProvider.get((wikiEntryCount > 1 ? "prm.project.dashboard.changeswithindays.newentries.message" : "prm.project.dashboard.changeswithindays.newentry.message"));

			List<ProjectPhase> phasesAux = projectSpaceService.getProjectPhasesAndMilestones(Integer.parseInt(projectSpace.getID()));
			phases = new ArrayList<ProjectPhaseWrapper>();
			Iterator<ProjectPhase> iPhases = phasesAux.iterator();
			while (iPhases.hasNext()) {
				ProjectPhase phaseAux = iPhases.next();

				ProjectPhaseWrapper projectPhaseWrapper = new ProjectPhaseWrapper(phaseAux);
				if(projectPhaseWrapper.getPhase().getPhaseName().length()>40){
					projectPhaseWrapper.getPhase().setPhaseName(projectPhaseWrapper.getPhase().getPhaseName().substring(0, 40).concat("..."));
				}
				phases.add(projectPhaseWrapper);
			}
				
			// loading milestone
			milestones = new ArrayList<PnTaskWrapper>();
			Schedule schedule = milestoneValues();
			if (schedule != null) {
				hasMilestones = true;
				Iterator<ScheduleEntry> taskIterator = schedule.getTaskList()
						.iterator();
				if (taskIterator == null) {
					taskIterator = schedule.getTaskList().iterator();
				}
				if(taskIterator != null){
					while (taskIterator.hasNext()) {
						PnTaskWrapper taskWrapper = new PnTaskWrapper(taskIterator
								.next());
						if (taskWrapper.getScheduleEntry().getName().length() > 40) {
							taskWrapper.getScheduleEntry().setName(
									taskWrapper.getScheduleEntry().getName()
											.substring(0, 40).concat("..."));
						}
						milestones.add(taskWrapper);
					}
				}
			}
			
			MyMeetings myMeetings = new MyMeetings();
			myMeetings.setUser(user);
			myMeetings.loadEntries();

			Collection meetingsAux = myMeetings.getMeetingList();
			meetings = new ArrayList<CalendarWrapper>();
			Iterator i = meetingsAux.iterator();
			while (i.hasNext()) {
				ICalendarEntry entry = (ICalendarEntry) i.next();
				CalendarWrapper calendarWrapper = new CalendarWrapper(entry);
				if(calendarWrapper.getName().length() > 15)
					calendarWrapper.getName().substring(0,15).concat("...");
				meetings.add(calendarWrapper);
			}

			hasMeetings = meetings.size() > 0;

			// load news
			NewsManager newsManager = new NewsManager();
			newsManager.setSpace(user.getCurrentSpace());
			newsManager.setTruncatedPresentableMessageLength(390);
			newsManager.setTruncatedPresentableMessageMaxParagraphs(5);
			NewsList newsListAux = newsManager.getAllNewsItems();
			Iterator<News> iNews = newsListAux.iterator();
			newsList = new ArrayList<NewsWrapper>();
			if (newsListAux.size() > 0) {
				hasNews = true;
				while (iNews.hasNext()) {
					NewsWrapper newsWrapper = new NewsWrapper(iNews.next());
					if(newsWrapper.getNews().getTopic().length()>35){
						newsWrapper.getNews().setTopic(newsWrapper.getNews().getTopic().substring(0,30).concat("..."));
					}
					if(newsWrapper.getNews().getMessage().length()>50){
						newsWrapper.getNews().setMessage(newsWrapper.getNews().getMessage().substring(0,45).concat("..."));
						moreMessage = true;
					}
					newsList.add(newsWrapper);
				}
			}
			
			// load materials
			materialsList = new MaterialBeanList();
			materialsList.setSpaceID(projectId);
			materialsList.load();
			
			if(materialsList.isEmpty())
				hasMaterials = false;
			else
				hasMaterials = true;

			// load subprojects
			SpaceList spaceList = SpaceManager.getSubprojects(projectSpace);
			ProjectPortfolio projectPortfolio = (ProjectPortfolio) PortfolioManager.makePortfolioFromSpaceList(spaceList);
			projectPortfolio.setUser(user);
			projectPortfolio.load();
			subprojects = new ArrayList<ProjectSpaceWrapper>();
			if (projectPortfolio.size() > 0) {
				hasSubprojects = true;
				Iterator<ProjectSpace> iProjectSpace = projectPortfolio.iterator();
				while (iProjectSpace.hasNext()) {
					ProjectSpaceWrapper projectSpaceWrapper = new ProjectSpaceWrapper(iProjectSpace.next());
					if( projectSpaceWrapper.getProjectSpace().getName().length() > 40 ){
						projectSpaceWrapper.getProjectSpace().setName(projectSpaceWrapper.getProjectSpace().getName().substring(0, 40).concat("..."));
					}
					subprojects.add(projectSpaceWrapper);
				}
			}
			
			setPageTitle();
			
			if (projectSpace.getProjectLogoID() != null) {
				projectLogo = true;
				logoUrl = getJSPRootURL()+"/servlet/photo?id=" + projectSpace.getID() + "&logoType=plogo&module=" + Module.DOCUMENT;
			} else {
				projectLogo = false;
			}
			
		} catch (Exception e) {
			log.error("Error occurred while getting dash board data: "+e.getMessage());
		}
	}

	/**
	 * @param pageTitle the pageTitle to set
	 */
	public void setPageTitle() {
		History history = null;
		HttpSession session = requestGlobals.getHTTPServletRequest().getSession();
		history = (History) session.getAttribute("historyTagHistoryObject");
		
		String bussinessId =  projectSpace.getParentBusinessID();
		BusinessLevel businessLevel = new BusinessLevel();
		businessLevel.setActive(false);
		if (bussinessId != null) {
			businessLevel.setDisplay(projectSpace.getParentBusinessName());
			businessLevel.setJspPage(getJSPRootURL() + "/business/Main.jsp?id="
					+ bussinessId);
			businessLevel.setShow(true);
		} else {
			businessLevel.setShow(false);
		}
		if(history == null){
			history = new History();	
			history.setLevel(businessLevel);
		} else {
			ProjectLevel projectLevel = new ProjectLevel();
			projectLevel.setActive(false);
			projectLevel.setDisplay(projectName);
			projectLevel.setJspPage(getJSPRootURL()+"/project/Dashboard?id="+projectId);
			projectLevel.setShow(true);
			history.setLevel(projectLevel);
		}
		session.setAttribute("historyTagHistoryObject", history);
		session.setAttribute("user", SessionManager.getUser());
		try {
			this.pageTitle = history.getPresentation().toString();
		} catch (HistoryException pnetEx) {
			log.error("Error occured while setting user's current space and history for page : " + pnetEx.getMessage());
		}
	}
	
	// initializing all contexts	
	private void initializeProjectDashboardView(){
		spaceId = Integer.parseInt(SessionManager.getUser().getCurrentSpace().getID());
		spaceName = SessionManager.getUser().getCurrentSpace().getName();
		personId = Integer.parseInt(SessionManager.getUser().getID());
		List<PnPersonProperty> personProperties = null; 
		
		if (spaceId != null && personId != null) {
			personProperties = ServiceFactory.getInstance()
					.getPnPersonPropertyService().getPersonProperties(spaceId,
							personId, PROPERTY);
		}
		
		for (PnPersonProperty pnPersonProperty : personProperties) {
			if (pnPersonProperty.getComp_id().getContext().equals(CHANNEL_PROPERTY_CONTEXT + PROJECT_SPACE_MEETINGS + spaceName)) {
				meetingsState = pnPersonProperty.getComp_id().getValue().equals(State.MINIMIZED.getID());
				meetingsCloseState = pnPersonProperty.getComp_id().getValue().equals(State.CLOSED.getID());
			}
			if (pnPersonProperty.getComp_id().getContext().equals(CHANNEL_PROPERTY_CONTEXT + PROJECT_SPACE_PHASES + spaceName)
				|| pnPersonProperty.getComp_id().getContext().equals(CHANNEL_PROPERTY_CONTEXT + PROJECT_SPACE_MILESTONES + spaceName)) {
				phasesState = pnPersonProperty.getComp_id().getValue().equals(State.MINIMIZED.getID());
				phasesCloseState = pnPersonProperty.getComp_id().getValue().equals(State.CLOSED.getID());
			}
			if (pnPersonProperty.getComp_id().getContext().equals(CHANNEL_PROPERTY_CONTEXT + PROJECT_SPACE_SUBPROJECTS+ spaceName)) {
				subprojectsState = pnPersonProperty.getComp_id().getValue().equals(State.MINIMIZED.getID());
				subProjectCloseState = pnPersonProperty.getComp_id().getValue().equals(State.CLOSED.getID());
			}
			if (pnPersonProperty.getComp_id().getContext().equals(CHANNEL_PROPERTY_CONTEXT + PROJECT_SPACE_TEAMMATES + spaceName)) {
				teammatesState = pnPersonProperty.getComp_id().getValue().equals(State.MINIMIZED.getID());
				teamatesCloseState = pnPersonProperty.getComp_id().getValue().equals(State.CLOSED.getID());
			}
			if (pnPersonProperty.getComp_id().getContext().equals(CHANNEL_PROPERTY_CONTEXT + PROJECT_SPACE_PROJECT_NEWS + spaceName)) {
				projectNewsState = pnPersonProperty.getComp_id().getValue().equals(State.MINIMIZED.getID());
				newsCloseState = pnPersonProperty.getComp_id().getValue().equals(State.CLOSED.getID());
			}
			if (pnPersonProperty.getComp_id().getContext().equals(CHANNEL_PROPERTY_CONTEXT + PROJECT_SPACE_MATERIALS + spaceName)) {
				materialsState = pnPersonProperty.getComp_id().getValue().equals(State.MINIMIZED.getID());
				materialsCloseState = pnPersonProperty.getComp_id().getValue().equals(State.CLOSED.getID());
			}
			if (pnPersonProperty.getComp_id().getContext().equals(CHANNEL_PROPERTY_CONTEXT + PROJECT_SPACE_PROJECT_SCHEDULE + spaceName)) {
				projectSheduleState = pnPersonProperty.getComp_id().getValue().equals(State.MINIMIZED.getID());
			}
			if (pnPersonProperty.getComp_id().getContext().equals(CHANNEL_PROPERTY_CONTEXT + PROJECT_SPACE_PROJECT_CHANGES + spaceName)) {
				projectLastChangesState = pnPersonProperty.getComp_id().getValue().equals(State.MINIMIZED.getID());
				lastChangesCloseState = pnPersonProperty.getComp_id().getValue().equals(State.CLOSED.getID());
			}
			if (pnPersonProperty.getComp_id().getContext().equals(CHANNEL_PROPERTY_CONTEXT+ PROJECT_SPACE_PROJECT_PROJECT_DETAILS+ spaceName)) {
				projectDetailState = pnPersonProperty.getComp_id().getValue().equals(State.MINIMIZED.getID());
			}
			if (pnPersonProperty.getComp_id().getContext().equals(CHANNEL_PROPERTY_CONTEXT + PROJECT_SPACE_PIE_CHART + spaceName)) {
				projectPieChartState = pnPersonProperty.getComp_id().getValue().equals(State.MINIMIZED.getID());
				pieChartCloseState = pnPersonProperty.getComp_id().getValue().equals(State.CLOSED.getID());
			}
			if (pnPersonProperty.getComp_id().getContext().equals(CHANNEL_PROPERTY_CONTEXT + PROJECT_SPACE_FINANCIAL_CHART + spaceName)) {
				projectFinancialChartState = pnPersonProperty.getComp_id().getValue().equals(State.MINIMIZED.getID());
				financialChartCloseState = pnPersonProperty.getComp_id().getValue().equals(State.CLOSED.getID());
			}			
			if (pnPersonProperty.getComp_id().getContext().equals(CHANNEL_PROPERTY_CONTEXT + PROJECT_SPACE_PROJECT_COMPLETION + spaceName)){
				projectCompletionState = pnPersonProperty.getComp_id().getValue().equals(State.MINIMIZED.getID());
				projectCompletionCloseState = pnPersonProperty.getComp_id().getValue().equals(State.CLOSED.getID());
			}					
		}
	}
	
	//Method for saving the channel state in pnPersonProperty table
	private void replaceContextState(HttpServletRequest request) {
		String value = request.getParameter("value");
		String contextSuffix = request.getParameter("context");
		spaceName = SessionManager.getUser().getCurrentSpace().getName(); 
		if (StringUtils.isNotEmpty(value) && StringUtils.isNotEmpty(contextSuffix)) {
			PersonProperty personProperty = new PersonProperty();
			personProperty.setScope(ScopeType.SPACE.makeScope(SessionManager.getUser()));
			try {
				personProperty.removeAllValues(CHANNEL_PROPERTY_CONTEXT + contextSuffix + spaceName, PROPERTY);
				personProperty.put(CHANNEL_PROPERTY_CONTEXT + contextSuffix + spaceName, PROPERTY, value);
				// setting session attribute to update channels states while customizing in CustomizeChannels.jsp 
				request.getSession().setAttribute("PR_user_properties", personProperty);
			} catch (PersistenceException pnetEx) {
				log.error("Database error while replacing context property: " + pnetEx.getMessage());
			}
		}
	}
	
	/** 
	 * Method to load Schedule for milestone
	 * @return Schedule
	 */
	private Schedule milestoneValues(){
		Schedule schedule = new Schedule();
		try{
			schedule.clearFinderFilterList();
		    schedule.setSpace(SessionManager.getUser().getCurrentSpace());
		    schedule.setHierarchyView(Schedule.HIERARCHY_VIEW_EXPANDED);
		    schedule.load();
		    
		    // sort column by start_date
		    schedule.setOrder("5");
		    schedule.setOrderDirection(0);
		    
		    // Load all entries
		    schedule.setMaximumEntries(-1);
		   
		    // load Milestone entries
		    // We avoid loading dependencies and assignments to improve
		    // performance; we don't care about those
		    schedule.setHierarchyView(Schedule.HIERARCHY_VIEW_EXPANDED);
		    schedule.loadEntries(new TaskType[] {TaskType.MILESTONE}, false, false, false);
		    
		    // Reset settings
		    schedule.clearFinderFilterList();
		    schedule.setMaximumEntries(-1);
		    schedule.setOrder("0");
	    }catch(Exception e){
	    	log.error("Error occured while loading schedule entries" + e.getMessage());
	    }
		return schedule;
	}
	
	
	/**
     * Returns the String to insert a "Personalize" link at the bottom of the page.
     * @return HTML proving a link to CustomizeChannels.jsp
     */
    public String getPersonalizeLink() {
    	StringBuffer url = new StringBuffer();
    	try {
		    	PersonProperty settings = new PersonProperty();
		    	settings.setScope(ScopeType.SPACE.makeScope(SessionManager.getUser()));
		        String qs = HttpUtils.getRedirectParameterString(requestGlobals.getHTTPServletRequest());
		        
		        if (qs != null) {
					qs = "?" + qs;
				} else {
					qs = "";
				}
        
            	url.append(SessionManager.getJSPRootURL()).append("/channel/CustomizeChannels.jsp?referer=").append(URLEncoder.encode(getJSPRootURL()+ requestGlobals.getHTTPServletRequest().getServletPath() + qs));

	            // Add the scope so that it is build into personalize link
	            url.append("&").append(settings.getScope().formatRequestParameters());
	
	            // Need to add id and name of every widget
	            url.append("&name=").append(URLEncoder.encode(PROJECT_SPACE_PIE_CHART+ projectSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(BAR_CHART_TITLE, SessionManager.getCharacterEncoding()));
	            url.append("&name=").append(URLEncoder.encode(PROJECT_SPACE_FINANCIAL_CHART+ projectSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(FINANCIAL_CHART_TITLE, SessionManager.getCharacterEncoding()));
	            url.append("&name=").append(URLEncoder.encode(PROJECT_SPACE_PROJECT_COMPLETION + projectSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(PROJECT_COMPLETION_TITLE, SessionManager.getCharacterEncoding()));
	            url.append("&name=").append(URLEncoder.encode(PROJECT_SPACE_PHASES + projectSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(PHASES_TITLE, SessionManager.getCharacterEncoding()));
	            url.append("&name=").append(URLEncoder.encode(PROJECT_SPACE_SUBPROJECTS + projectSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(SUBPROJECTS_TITLE, SessionManager.getCharacterEncoding()));
	            url.append("&name=").append(URLEncoder.encode(PROJECT_SPACE_MEETINGS + projectSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(MEETINGS_TITLE, SessionManager.getCharacterEncoding()));
	            url.append("&name=").append(URLEncoder.encode(PROJECT_SPACE_PROJECT_NEWS + projectSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(PROJECT_NEWS_TITLE, SessionManager.getCharacterEncoding()));
	            url.append("&name=").append(URLEncoder.encode(PROJECT_SPACE_MATERIALS + projectSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(MATERIALS_TITLE, SessionManager.getCharacterEncoding()));
	            url.append("&name=").append(URLEncoder.encode(PROJECT_SPACE_TEAMMATES + projectSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(TEAMMATES_TITLE, SessionManager.getCharacterEncoding()));
	            url.append("&name=").append(URLEncoder.encode(PROJECT_SPACE_PROJECT_CHANGES + projectSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(PROJECT_CHANGES_TITLE, SessionManager.getCharacterEncoding()));
	            
	        } catch (Exception e) {
	            log.error(e.getMessage()); 
	        }
        return url.toString();
    }
	
    public RequestGlobals getRequestGlobals() {
		return requestGlobals;
	}

	public void setRequestGlobals(RequestGlobals rg) {
		requestGlobals = rg;
	}
    
	public int getModuleReport() {
		return Module.REPORT;
	}

	public ProjectSpaceBean getProjectSpace() {
		return projectSpace;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getProjectDescription() {
		return projectDescription;
	}

	public String getProjectManager() {
		return projectManager;
	}

	public String getProjectStatus() {
		return projectStatus;
	}

	public String getPercent() {
		if(projectSpace !=null){
			String p = projectSpace.getPercentComplete();
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(2);
			percent = nf.format(Double.parseDouble(p));
		}
		return percent;
	}

	public int getPeopleAssigned() {
		return peopleAssigned;
	}

	public int getProjectSpaceModule() {
		return Module.PROJECT_SPACE;
	}

	public int getNumberLateTasks() {
		return numberLateTasks;
	}

	public int getNumberComingDueTasks() {
		return numberComingDueTasks;
	}

	public int getNumberUnassignedTasks() {
		return numberUnassignedTasks;
	}

	public int getNumberCompletedTasks() {
		return numberCompletedTasks;
	}

	public String getProjectId() {
		return projectId;
	}

	public String getStartDate() {
		return startDate;
	}

	public String getStartActualDate() {
		return startActualDate;
	}

	public String getFinishDate() {
		return finishDate;
	}

	public String getFinishActualDate() {
		return finishActualDate;
	}

	public String getPieChartUrl() {
		return getJSPRootURL() + "/servlet/PieChartServlet?project="
				+ this.projectId + "&module=" + Module.PROJECT_SPACE + 
				"&numberComingDueTasks=" + numberComingDueTasks +
				"&numberLateTasks=" + numberLateTasks +
				"&numberCompletedTasks=" + numberCompletedTasks +
				"&numberUnassignedTasks=" + numberUnassignedTasks;
	}

	public List<ObjectChangedWrapper> getDocuments() {
		return documents;
	}

	public void setDocuments(List<ObjectChangedWrapper> documents) {
		this.documents = documents;
	}

	public ObjectChangedWrapper getDocument() {
		return document;
	}

	public void setDocument(ObjectChangedWrapper document) {
		this.document = document;
	}

	public List<TeammateWrapper> getTeammates() {
		return teammates;
	}

	public void setTeammates(List<TeammateWrapper> teammates) {
		this.teammates = teammates;
	}

	public TeammateWrapper getTeammate() {
		return teammate;
	}

	public void setTeammate(TeammateWrapper teammate) {
		this.teammate = teammate;
	}

	public List<ProjectPhaseWrapper> getPhases() {
		return phases;
	}

	public void setPhases(List<ProjectPhaseWrapper> phases) {
		this.phases = phases;
	}

	public ProjectPhaseWrapper getPhase() {
		return phase;
	}

	public void setPhase(ProjectPhaseWrapper phase) {
		this.phase = phase;
	}

	public PnTaskWrapper getMilestone() {
		return milestone;
	}

	public void setMilestone(PnTaskWrapper milestone) {
		this.milestone = milestone;
	}

	public boolean getImprovementCode() {
		return projectSpace.getImprovementCode() != null;
	}

	public String getImprovementCodeUrl() {
		return projectSpace.getImprovementCode().getImageURL(
				projectSpace.getColorCode());
	}

	public boolean getFinancialStatusCode() {
		return projectSpace.getFinancialStatusColorCode() != null;
	}

	public String getFinancialStatusCodeUrl() {
		return projectSpace.getFinancialStatusImprovementCode().getImageURL(
				projectSpace.getFinancialStatusColorCode());
	}

	public boolean getScheduleStatusCode() {
		return projectSpace.getScheduleStatusColorCode() != null;
	}

	public String getScheduleStatusCodeUrl() {
		return projectSpace.getScheduleStatusImprovementCode().getImageURL(
				projectSpace.getScheduleStatusColorCode());
	}

	public boolean getResourceStatusCode() {
		return projectSpace.getResourceStatusColorCode() != null;
	}

	public String getResourceStatusCodeUrl() {
		return projectSpace.getResourceStatusImprovementCode().getImageURL(
				projectSpace.getResourceStatusColorCode());
	}

	public List<PnTaskWrapper> getMilestones() {
		return milestones;
	}

	public void setMilestones(List<PnTaskWrapper> milestones) {
		this.milestones = milestones;
	}

	public List<CalendarWrapper> getMeetings() {
		return meetings;
	}

	public void setMeetings(List<CalendarWrapper> meetings) {
		this.meetings = meetings;
	}

	public CalendarWrapper getMeeting() {
		return meeting;
	}

	public void setMeeting(CalendarWrapper meeting) {
		this.meeting = meeting;
	}

	public boolean getHasFormChanges() {
		return this.hasFormChanges;
	}

	public boolean getHasDocumentsChanges() {
		return hasDocumentsChanges;
	}

	public boolean getHasDiscussionsChanges() {
		return hasDiscussionsChanges;
	}

	public List<ObjectChangedWrapper> getForms() {
		return forms;
	}

	public void setForms(List<ObjectChangedWrapper> forms) {
		this.forms = forms;
	}

	public ObjectChangedWrapper getForm() {
		return form;
	}

	public void setForm(ObjectChangedWrapper form) {
		this.form = form;
	}

	public List<ObjectChangedWrapper> getDiscussions() {
		return discussions;
	}

	public void setDiscussions(List<ObjectChangedWrapper> discussions) {
		this.discussions = discussions;
	}

	public ObjectChangedWrapper getDiscussion() {
		return discussion;
	}

	public void setDiscussion(ObjectChangedWrapper discussion) {
		this.discussion = discussion;
	}

	public List<NewsWrapper> getNewsList() {
		return newsList;
	}
	
	public void setNewsList(List<NewsWrapper> newsList) {
		this.newsList = newsList;
	}

	public NewsWrapper getNews() {
		return news;
	}

	public void setNews(NewsWrapper news) {
		this.news = news;
	}

	public boolean getHasNews() {
		return hasNews;
	}
	
	public MaterialBeanList getMaterialsList()
	{
		return materialsList;
	}

	public void setMaterialsList(MaterialBeanList materialsList)
	{
		this.materialsList = materialsList;
	}

	public MaterialBean getMaterial()
	{
		return material;
	}

	public void setMaterial(MaterialBean material)
	{
		this.material = material;
	}

	public boolean getHasMaterials() {
		return hasMaterials;
	}	

	public List<ProjectSpaceWrapper> getSubprojects() {
		return subprojects;
	}

	public void setSubprojects(List<ProjectSpaceWrapper> subprojects) {
		this.subprojects = subprojects;
	}

	public ProjectSpaceWrapper getSubproject() {
		return subproject;
	}

	public void setSubproject(ProjectSpaceWrapper subproject) {
		this.subproject = subproject;
	}

	public boolean getHasSubprojects() {
		return hasSubprojects;
	}

	public void setHasSubprojects(boolean hasSubprojects) {
		this.hasSubprojects = hasSubprojects;
	}

	public String getModifyUrl() {
		return "javascript:modify();";
	}

	public boolean getHasMilestones() {
		return hasMilestones;
	}

	public String getProjectLabel() {
		return projectLabel;
	}

	public String getDescriptionLabel() {
		return descriptionLabel;
	}

	public String getManagerLabel() {
		return managerLabel;
	}

	public String getStatusLabel() {
		return statusLabel;
	}

	public String getCompletionLabel() {
		return completionLabel;
	}

	public String getProjectNewsTitle() {
		return projectNewsTitle;
	}

	public String getLastChangesTitle() {
		return lastChangesTitle;
	}

	public String getUpcomingMeetingTitle() {
		return upcomingMeetingTitle;
	}

	public String getScheduleLabel() {
		return scheduleLabel;
	}

	public String getStartLabel() {
		return startLabel;
	}

	public String getFinishLabel() {
		return finishLabel;
	}

	public String getPlannedLabel() {
		return plannedLabel;
	}

	public String getActualLabel() {
		return actualLabel;
	}

	public String getLateTasksLink() {
		return lateTasksLink;
	}

	public String getTaskComingDueThisWeekLink() {
		return taskComingDueThisWeekLink;
	}

	public String getUnassignedTasksLink() {
		return unassignedTasksLink;
	}

	public String getTasksCompletedLink() {
		return tasksCompletedLink;
	}

	public String getPhasesMilestonesTitle() {
		return phasesMilestonesTitle;
	}

	public String getNameColumnLabel() {
		return nameColumnLabel;
	}

	public String getEndDateColumnLabel() {
		return endDateColumnLabel;
	}

	public String getStatusColumnLabel() {
		return statusColumnLabel;
	}

	public String getProgressColumnLabel() {
		return progressColumnLabel;
	}

	public String getSubprojectsTitle() {
		return subprojectsTitle;
	}

	public String getProjectColumnLabel() {
		return projectColumnLabel;
	}

	public String getOveralStatusColumnLabel() {
		return overalStatusColumnLabel;
	}

	public String getFinancialStatusColumnLabel() {
		return financialStatusColumnLabel;
	}

	public String getScheduleStatusColumnLabel() {
		return scheduleStatusColumnLabel;
	}

	public String getResourceStatusColumnLabel() {
		return resourceStatusColumnLabel;
	}

	public String getCompletionPercentageColumnLabel() {
		return completionPercentageColumnLabel;
	}

	public String getResourcesLabel() {
		return resourcesLabel;
	}

	public String getPeopleAssignedText() {
		return peopleAssignedText;
	}

	public String getTeammatesLink() {
		return teammatesLink;
	}

	public String getNameTeammateColumnLabel() {
		return nameTeammateColumnLabel;
	}

	public String getAssignmentsTeammateColumnLabel() {
		return assignmentsTeammateColumnLabel;
	}

	public boolean isHasMeetings() {
		return hasMeetings;
	}

	public void setHasMeetings(boolean hasMeetings) {
		this.hasMeetings = hasMeetings;
	}
	
	public int getTeammateCount() {
		if(this.teammates != null) {
			return this.teammates.size();
		} else {
			return 0;
		}
	}
	
	public String getCreateMeetingUrl() {
		return "/calendar/Main.jsp?module="+Module.CALENDAR;
	}
	
	public String getSubprojectsUrl() {
		return "/project/subproject/Main.jsp?module="+Module.PROJECT_SPACE;
	}
	
	public String getReportsUrl() {
		return "/report/Main.jsp?module="+Module.REPORT;
	}
	
	public String getNewsUrl() {
		return "/news/Main.jsp?module="+Module.NEWS;
	}

	public String getPageTitle() {
		return pageTitle;
	}
	
	public String getFormUrl(){
		return "/form/Main.jsp?module="+ Module.FORM;
	}
	
	public String getBlogUrl(){
		return "/blog/view/"
				+ SessionManager.getUser().getCurrentSpace().getID() + "/"
				+ personId + "/" + Space.PROJECT_SPACE + "/"
				+ Module.PROJECT_SPACE + "?module=" + Module.PROJECT_SPACE;
	}
	
	public String getWikiLink(){
		return "/wiki/"+WikiURLManager.getRootWikiPageNameForSpace()+"?op=recent_changes";  
	}
	
	public String getDocuemntUrl(){
		return "/document/Main.jsp?module="+ Module.DOCUMENT;
	}
	
	public String getDiscussionUrl(){
		return "/discussion/Main.jsp?module="+Module.DISCUSSION;
	}
	
	public String getSubProjectUrl(){
		return "/project/ProjectCreate.jsp?module=" + Module.PROJECT_SPACE + "&action=" + Action.CREATE + "&parent=" + projectSpace.getID() + 
		       (projectSpace.getParentBusinessID() == null ? "" : "&business=" + projectSpace.getParentBusinessID());
	}
	
	public String getPhaseUrl(){
		return "/process/Main.jsp?module="+Module.PROCESS;
	}
	
	public Integer getModuleId() {
		return moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

	public boolean isMeetingsState() {
		return meetingsState;
	}

	public boolean isPhasesState() {
		return phasesState;
	}

	public boolean isSubprojectsState() {
		return subprojectsState;
	}

	public boolean isTeammatesState() {
		return teammatesState;
	}

	public boolean isProjectNewsState() {
		return projectNewsState;
	}

	public boolean isMaterialsState() {
		return materialsState;
	}	
	
	public boolean isProjectSheduleState() {
		return projectSheduleState;
	}

	public boolean isProjectLastChangesState() {
		return projectLastChangesState;
	}

	public boolean isProjectDetailState() {
		return projectDetailState;
	}
	
	public boolean isProjectFinancialChartState()
	{
		return projectFinancialChartState;
	}

	public Integer getTotalEntriesCount() {
		return totalEntriesCount;
	}

	public Integer getWikiEntryCount() {
		return wikiEntryCount;
	}

	public boolean getshowSkypeStatus() {
		return showSkypeStatus;
	}

	public boolean getshowTeammateOnlineStatus() {
		return showTeammateOnlineStatus;
	}

	public Integer getPersonalModuleId() {
		return personalModuleId;
	}

	public String getPersonalSpaceType() {
		return personalSpaceType;
	}

	public String getBlogCountMessage() {
		return blogCountMessage;
	}

	public String getWikiCountMessage() {
		return wikiCountMessage;
	}

	public String getFormsCountMessage() {
		return formsCountMessage;
	}

	public String getDocumentsCountMessage() {
		return documentsCountMessage;
	}

	public String getDiscussionsCountMessage() {
		return discussionsCountMessage;
	}

	public boolean getProjectPieChartState() {
		return projectPieChartState;
	}

	public boolean getProjectCompletionState() {
		return projectCompletionState;
	}

	public boolean getNewsCloseState() {
		return newsCloseState;
	}
	
	public boolean getMaterialsCloseState() {
		return materialsCloseState;
	}	

	public boolean getTeamatesCloseState() {
		return teamatesCloseState;
	}

	public boolean getMeetingsCloseState() {
		return meetingsCloseState;
	}

	public boolean getLastChangesCloseState() {
		return lastChangesCloseState;
	}

	public boolean getPhasesCloseState() {
		return phasesCloseState;
	}

	public boolean getSubProjectCloseState() {
		return subProjectCloseState;
	}

	public boolean getPieChartCloseState() {
		return pieChartCloseState;
	}

	public boolean getFinancialChartCloseState()
	{
		return financialChartCloseState;
	}

	public boolean getProjectCompletionCloseState() {
		return projectCompletionCloseState;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public boolean getProjectLogo() {
		return projectLogo;
	}
	
	/**
	 * Method returns the Improvement Title 
	 * @return String
	 */
	public String getImprovementTitle() {
		return PropertyProvider.get(projectSpace.getImprovementCode().getNameToken())+ " / "
				+ PropertyProvider.get(projectSpace.getColorCode().getNameToken());
	}

	/**
	 * Method returns the Finanacial Status Title
	 * @return String
	 */
	public String getFinancialStatusTitle() {
		return PropertyProvider.get(projectSpace.getFinancialStatusImprovementCode().getNameToken())+ " / "
				+ PropertyProvider.get(projectSpace.getFinancialStatusColorCode().getNameToken());
	}

	/**
	 * Method returns Schedule Status Title
	 * @return String
	 */
	public String getScheduleStatusTitle() {
		return PropertyProvider.get(projectSpace.getScheduleStatusImprovementCode().getNameToken())+ " / "
				+ PropertyProvider.get(projectSpace.getScheduleStatusColorCode().getNameToken());
	}
	
	/**
	 * Method returns Resource Status Title 
	 * @return String
	 */
	public String getResourceStatusTitle() {
		return PropertyProvider.get(projectSpace.getResourceStatusImprovementCode().getNameToken())+ " / "
				+ PropertyProvider.get(projectSpace.getResourceStatusColorCode().getNameToken());
	}
	
	/**
	 * @return the actionsIconEnabled
	 */
	public boolean isActionsIconEnabled() {
		return actionsIconEnabled;
	}

	/**
	 * @return the blogEnabled
	 */
	public boolean isBlogEnabled() {
		return blogEnabled;
	}
	
	/**
	 * @return the subProjectName
	 */
	public String getSubProjectName() {
		return subProjectName;
	}

	/**
	 * @return the parentURL
	 */
	public String getParentURL() {
		return parentURL;
	}

	/**
	 * @return the overAssignedTitle
	 */
	public String getOverAssignedTitle() {
		return overAssignedTitle;
	}

	/**
	 * @return the assignedTitle
	 */
	public String getAssignedTitle() {
		return assignedTitle;
	}

	/**
	 * @return the closeTitle
	 */
	public String getCloseTitle() {
		return closeTitle;
	}

	/**
	 * @return upTitle
	 */
	public String getUpTitle() {
		return upTitle;
	}

	/**
	 * @return downTitle
	 */
	public String getDownTitle() {
		return downTitle;
	}
	
	/**
	 * @return the moreMessage
	 */
	public boolean getMoreMessage() {
		return moreMessage;
	}
	
	/**
	 * Method returns create news page link
	 * @return String 
	 */
	public String getCreateNewsUrl(){
		return "/news/NewsEdit.jsp?action=" + Action.CREATE + "&module=" + Module.NEWS + "&mode=create"; 
	}
	
	/**
	 * Method returns create material page link
	 * @return String 
	 */
	public String getCreateMaterialsUrl(){
		return "/material/MaterialDirectory.jsp?action=" + Action.CREATE + "&module=" + Module.MATERIAL; 
	}	
}
