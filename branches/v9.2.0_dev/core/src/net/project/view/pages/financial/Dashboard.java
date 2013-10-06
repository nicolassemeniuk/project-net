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
package net.project.view.pages.financial;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;
import org.slf4j.Logger;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.business.BusinessSpace;
import net.project.channel.ScopeType;
import net.project.channel.State;
import net.project.financial.FinancialSpaceBean;
import net.project.hibernate.model.PnPersonProperty;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;
import net.project.resource.PersonProperty;
import net.project.resource.UserActivityStatus;
import net.project.security.BuddyListBean;
import net.project.security.SecurityProvider;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;
import net.project.util.HttpUtils;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;
import net.project.portfolio.IPortfolioEntry;
import net.project.portfolio.ProjectPortfolio;
import net.project.project.ProjectSpace;

public class Dashboard extends BasePage
{
	@Inject
	private RequestGlobals requestGlobals;	
	
	private static Logger log = logger;	

	private Integer moduleId;	
	
	private FinancialSpaceBean financialSpace;	
	
	private String financialName;
	
	private String financialDescription;
	
	@Persist
	private String financialId;		
	
	private String logoUrl;
	
	private boolean financialLogo;
	
	private ArrayList<ProjectSpace> projectList;
	
	private ProjectSpace project;		
	
	private boolean hasProjects;
	
	private BuddyListBean buddyList;
	
	private User buddy;
	
	// Left navbar
	private boolean actionsIconEnabled;	
	
	private boolean blogEnabled;
	
	// Variables for minimize maximize functionality
	private String closeTitle;
	
	private String upTitle;
	
	private String downTitle;
	
	// Channel states
	private boolean projectsState;	

	private boolean projectTotalCostsChartState;
	
	private boolean financialTeamState;
	
	private boolean actualCostsTypesOverTotalChartState;
	
	// Channel close state
	private boolean projectsCloseState;
	
	private boolean projectTotalCostsChartCloseState;

	private boolean financialTeamCloseState;
	
	private boolean actualCostsTypesOverTotalChartCloseState;
	
	// Context initialization
	@Persist
	private String spaceName;	
	
	private Integer spaceId;
	
	private Integer personId;	
	
	private final String PROPERTY = "state";
	
	private final String CHANNEL_PROPERTY_CONTEXT = "net.project.channel.";
	
	// Context for save channels states
	private final String FINANCIAL_SPACE_PROJECTS = "FinancialSpace_Projects_";
	
	private final String FINANCIAL_SPACE_PROJECT_TOTAL_COSTS_CHART = "FinancialSpace_ProjectTotalCostsChart_";	

	private final String FINANCIAL_SPACE_FINANCIAL_TEAM = "FinancialSpace_FinancialTeam_";		

	private final String FINANCIAL_SPACE_ACTUAL_COSTS_TYPES_OVER_TOTAL_CHART = "FinancialSpace_ActualCostsTypesOverTotalChart_";	
	
	// Titles for channels 
	private final String PROJECTS_TITLE = PropertyProvider.get("prm.financial.dashboard.projects.channel.title");
	
	private final String PROJECT_TOTAL_COSTS_CHART_TITLE = PropertyProvider.get("prm.financial.dashboard.projecttotalcostschart.channel.title");
	
	private final String FINANCIAL_TEAM_TITLE = PropertyProvider.get("prm.financial.dashboard.financialteam.channel.title");
	
	private final String ACTUAL_COSTS_TYPES_OVER_TOTAL_CHART_TITLE = PropertyProvider.get("prm.financial.dashboard.actualcoststypesovertotalchart.channel.title");
	
	// Tooltips
	@Property
	private String personalizePageTooltip;	

	/**
	 * Method to initialize tokens.
	 */
	public void initializeTokens() {
        actionsIconEnabled = PropertyProvider.getBoolean("prm.global.actions.icon.isenabled");
        blogEnabled = PropertyProvider.getBoolean("prm.blog.isenabled");        		
		
		closeTitle = PropertyProvider.get("all.global.channelbarbutton.title.close");
		upTitle = PropertyProvider.get("all.global.channelbarbutton.title.minimize");
		downTitle = PropertyProvider.get("all.global.channelbarbutton.title.maximize");      
        
		personalizePageTooltip = PropertyProvider.get("prm.financial.main.personalize.button.tooltip");
	}		
	
	/**
	* Method for setting current financial space as user's current space.
	*/
	 private void setUserCurrentSpace(){
		FinancialSpaceBean financial = (FinancialSpaceBean) requestGlobals.getHTTPServletRequest().getSession().getAttribute("financialSpace");
		financialId = requestGlobals.getHTTPServletRequest().getParameter("id");
		try {
			if(financial == null && !financialSpace.getID().equals(financialId)){
				financial = new FinancialSpaceBean();
				financial.setID(financialId);
				financial.load();
			} else {
				financial.load();	
			}
		} catch (PersistenceException pnetEx) {
			log.error("Error occured while loading financial space : "+pnetEx.getMessage());
		}
		try {
			SessionManager.getUser().setCurrentSpace(financial);
		} catch (PnetException pnetEx) {
			log.error("Error occured while setting financial as current space : "+pnetEx.getMessage());
		}
		financialSpace = financial;
	}	
	
	Object onActivate() {
		if(checkForUser() != null) {
			return checkForUser();
		}
		HttpServletRequest request = requestGlobals.getHTTPServletRequest();
		String financialId = request.getParameter("id");
		if (StringUtils.isNotEmpty(financialId)) {
			// Security Check: Is user allowed access to requested space?
			SecurityProvider securityProvider = SessionManager.getSecurityProvider();
			Space checkSpace = new FinancialSpaceBean();
			checkSpace.setID(financialId);
			Space oldSpace = securityProvider.getSpace();
			securityProvider.setSpace(checkSpace);
			if (securityProvider.isActionAllowed(null, Integer.toString(net.project.base.Module.FINANCIAL_SPACE),
			                                     net.project.security.Action.VIEW)) {
			    // Passed Security Check
				initializeFinancialDashboardView();
	        	setUserCurrentSpace();
			} else {
	            // Failed Security Check
	            securityProvider.setSpace(oldSpace);
	            request.setAttribute("exception", new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.project.main.authorizationfailed.message"), checkSpace));
	            try {
					RequestDispatcher requestDispatcher = request.getRequestDispatcher("/AccessDenied.jsp");
					requestDispatcher.forward(request, requestGlobals.getHTTPServletResponse());
				} catch (IOException e) {
					log.error("Error occured while setting financial as current space : "+e.getMessage());
				} catch (ServletException e) {
					log.error("Error Occured while redirecting to error page : "+e.getMessage());
				}
			}
		} else {
			if(SessionManager.getUser().getCurrentSpace() instanceof FinancialSpaceBean){
			     financialSpace = (FinancialSpaceBean) SessionManager.getUser().getCurrentSpace();
			}
			if (StringUtils.isNotEmpty(financialSpace.getID())) {
		        try {
					financialSpace.load();
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
	
	// initializing all contexts	
	private void initializeFinancialDashboardView(){
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
			if (pnPersonProperty.getComp_id().getContext().equals(CHANNEL_PROPERTY_CONTEXT + FINANCIAL_SPACE_PROJECTS + spaceName)){
				projectsState = pnPersonProperty.getComp_id().getValue().equals(State.MINIMIZED.getID());
				projectsCloseState = pnPersonProperty.getComp_id().getValue().equals(State.CLOSED.getID());
			}
			
			if (pnPersonProperty.getComp_id().getContext().equals(CHANNEL_PROPERTY_CONTEXT + FINANCIAL_SPACE_PROJECT_TOTAL_COSTS_CHART + spaceName)){
				projectTotalCostsChartState = pnPersonProperty.getComp_id().getValue().equals(State.MINIMIZED.getID());
				projectTotalCostsChartCloseState = pnPersonProperty.getComp_id().getValue().equals(State.CLOSED.getID());
			}
			
			if (pnPersonProperty.getComp_id().getContext().equals(CHANNEL_PROPERTY_CONTEXT + FINANCIAL_SPACE_FINANCIAL_TEAM + spaceName)){
				financialTeamState = pnPersonProperty.getComp_id().getValue().equals(State.MINIMIZED.getID());
				financialTeamCloseState = pnPersonProperty.getComp_id().getValue().equals(State.CLOSED.getID());
			}
			
			if (pnPersonProperty.getComp_id().getContext().equals(CHANNEL_PROPERTY_CONTEXT + FINANCIAL_SPACE_ACTUAL_COSTS_TYPES_OVER_TOTAL_CHART + spaceName)){
				actualCostsTypesOverTotalChartState = pnPersonProperty.getComp_id().getValue().equals(State.MINIMIZED.getID());
				actualCostsTypesOverTotalChartCloseState = pnPersonProperty.getComp_id().getValue().equals(State.CLOSED.getID());
			}			
		}
	}
	
	/**
	 * Method for saving the channel state in pnPersonProperty table.
	 * @param request
	 */
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
	 * Seting up values before page render.
	 */
	@SetupRender
	void setValues() {
		try {
			initializeTokens();
			String id = requestGlobals.getHTTPServletRequest().getParameter("id");
			if(StringUtils.isNotEmpty(id)){
				financialId = id;
			}
			
			moduleId = Module.FINANCIAL_SPACE;
			financialName = financialSpace.getName();

			financialDescription = financialSpace.getDescription();
			if(financialDescription != null && financialDescription.length() > 100){
				financialDescription = financialDescription.substring(0, 100) + "..";
			}			
			
			projectList = financialSpace.getProjectsList();
			if(projectList.isEmpty())
				hasProjects = false;
			else
				hasProjects = true;			
			
			if (financialSpace.getFinancialLogoID() != null) {
				financialLogo = true;
				logoUrl = getJSPRootURL()+"/servlet/photo?id=" + financialSpace.getID() + "&logoType=flogo&module=" + Module.DOCUMENT;
			} else {
				financialLogo = false;
			}			
			
			buddyList = new BuddyListBean();
			buddyList.setUser(SessionManager.getUser());
		    buddyList.setSpace(financialSpace);
		    buddyList.load();
			
		} catch (Exception e) {
			log.error("Error occurred while getting dash board data: "+e.getMessage());
		}			
	}
	
	public String getFinancialName()
	{
		return financialName;
	}
	
	public String getFinancialDescription()
	{
		return financialDescription;
	}

	public String getReportsUrl()
	{
		return "/report/Main.jsp?module="+Module.REPORT;
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
	            url.append("&name=").append(URLEncoder.encode(FINANCIAL_SPACE_PROJECTS+ financialSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(PROJECTS_TITLE, SessionManager.getCharacterEncoding()));
	            url.append("&name=").append(URLEncoder.encode(FINANCIAL_SPACE_PROJECT_TOTAL_COSTS_CHART+ financialSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(PROJECT_TOTAL_COSTS_CHART_TITLE, SessionManager.getCharacterEncoding()));
	            url.append("&name=").append(URLEncoder.encode(FINANCIAL_SPACE_FINANCIAL_TEAM+ financialSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(FINANCIAL_TEAM_TITLE, SessionManager.getCharacterEncoding()));
	            url.append("&name=").append(URLEncoder.encode(FINANCIAL_SPACE_ACTUAL_COSTS_TYPES_OVER_TOTAL_CHART+ financialSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(ACTUAL_COSTS_TYPES_OVER_TOTAL_CHART_TITLE, SessionManager.getCharacterEncoding()));
            
	        } catch (Exception e) {
	            log.error(e.getMessage()); 
	        }

        return url.toString();
    }
	
    public ArrayList<ProjectSpace> getProjectList()
	{
		return projectList;
	}

	public ProjectSpace getProject()
	{
		return project;
	}

	public void setProject(ProjectSpace project)
	{
		this.project = project;
	}

	public BuddyListBean getBuddyList()
	{
		return buddyList;
	}	
	
	public User getBuddy()
	{
		return buddy;
	}

	public void setBuddy(User buddy)
	{
		this.buddy = buddy;
	}

	public boolean isOnline()
	{	
		UserActivityStatus activeStatus = UserActivityStatus.ACTIVE;
		activeStatus.setStatus(UserActivityStatus.ACTIVE);
		return this.buddy.getUserActivityStatusInstance().equals(activeStatus);
	}
	
	public boolean isHasProjects()
	{
		return hasProjects;
	}

	public String getLogoUrl()
	{
		return logoUrl;
	}	

	public boolean isFinancialLogo()
	{
		return financialLogo;
	}
	
	public boolean isActionsIconEnabled()
	{
		return actionsIconEnabled;
	}
	
	public boolean isBlogEnabled()
	{
		return blogEnabled;
	}

	public String getCloseTitle()
	{
		return closeTitle;
	}

	public String getUpTitle()
	{
		return upTitle;
	}
	
	public String getDownTitle()
	{
		return downTitle;
	}		

	public boolean isProjectsState()
	{
		return projectsState;
	}	
	
	public boolean isProjectTotalCostsChartState()
	{
		return projectTotalCostsChartState;
	}
	
	public boolean isFinancialTeamState()
	{
		return financialTeamState;
	}
	
	public boolean isActualCostsTypesOverTotalChartState()
	{
		return actualCostsTypesOverTotalChartState;
	}

	public boolean isProjectsCloseState()
	{
		return projectsCloseState;
	}	
	
	public boolean isProjectTotalCostsChartCloseState()
	{
		return projectTotalCostsChartCloseState;
	}

	public boolean isFinancialTeamCloseState()
	{
		return financialTeamCloseState;
	}

	public boolean isActualCostsTypesOverTotalChartCloseState()
	{
		return actualCostsTypesOverTotalChartCloseState;
	}
}
