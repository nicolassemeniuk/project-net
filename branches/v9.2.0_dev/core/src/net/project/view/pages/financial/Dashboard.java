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
import net.project.channel.ScopeType;
import net.project.channel.State;
import net.project.financial.FinancialSpaceBean;
import net.project.hibernate.model.PnPersonProperty;
import net.project.hibernate.model.project_space.ProjectSchedule;
import net.project.hibernate.service.IPnProjectSpaceService;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpace;
import net.project.project.ProjectSpaceBean;
import net.project.resource.PersonProperty;
import net.project.security.SecurityProvider;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;
import net.project.space.SpaceManager;
import net.project.util.DateFormat;
import net.project.util.HttpUtils;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;

public class Dashboard extends BasePage {
	
	private static Logger log = logger;	
	
	private FinancialSpaceBean financialSpace;	
	
	private String financialName;
	
	private String logoUrl;
	
	private boolean financialLogo;
	
	private boolean actionsIconEnabled;	
	
	private boolean blogEnabled;
	
	private String closeTitle;
	
	private String upTitle;
	
	private String downTitle;		
	
	// variables for minimize maximize functionality
	
	private boolean projectsState;	
	
	private boolean projectsCloseState;	

	@Persist
	private String spaceName;	
	
	private Integer spaceId;
	
	private Integer personId;	
	
	private final String PROPERTY = "state";
	
	private final String CHANNEL_PROPERTY_CONTEXT = "net.project.channel.";
	
	private final String FINANCIAL_SPACE_PROJECTS = "FinancialSpace_Projects_";	
	
	@Persist
	private String financialId;	
	
	// Tooltips
	@Property
	private String blogitTooltip;
	
	@Property
	private String editFinancialTooltip;
	
	@Property
	private String viewPropertiesTooltip;
	
	@Property
	private String personalizePageTooltip;	

	private Integer moduleId = 175;	
	
	@Inject
	private RequestGlobals requestGlobals;	
	
	/**
	 * Method to initialize tokens 
	 */
	public void initializeTokens() {
        actionsIconEnabled = PropertyProvider.getBoolean("prm.global.actions.icon.isenabled");
        blogEnabled = PropertyProvider.getBoolean("prm.blog.isenabled");        		
		
		closeTitle = PropertyProvider.get("all.global.channelbarbutton.title.close");
		upTitle = PropertyProvider.get("all.global.channelbarbutton.title.minimize");
		downTitle = PropertyProvider.get("all.global.channelbarbutton.title.maximize");      
        
		blogitTooltip = PropertyProvider.get("all.global.toolbar.standard.blogit");
		editFinancialTooltip = PropertyProvider.get("prm.financial.main.modify.button.tooltip");
		viewPropertiesTooltip = PropertyProvider.get("prm.financial.main.properties.button.tooltip");
		personalizePageTooltip = PropertyProvider.get("prm.financial.main.personalize.button.tooltip");
	}		
	
	/**
	* Method for setting current financial space as user's current space
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
			log.error("Error occured while loading project space : "+pnetEx.getMessage());
		}
		try {
			SessionManager.getUser().setCurrentSpace(financial);
		} catch (PnetException pnetEx) {
			log.error("Error occured while setting project as current space : "+pnetEx.getMessage());
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
	 * Seting up values before page render
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
			financialName = financialSpace.getFinancialSpaceName();

			if (financialSpace.getFinancialLogoID() != null) {
				financialLogo = true;
				logoUrl = getJSPRootURL()+"/servlet/photo?id=" + /*financialSpace.getID()*/ 6607 + "&logoType=plogo&module=" + Module.DOCUMENT;
			} else {
				financialLogo = false;
			}			
			
		} catch (Exception e) {
			log.error("Error occurred while getting dash board data: "+e.getMessage());
		}			
	}
	 
	public String getReportsUrl() {
		return "/report/Main.jsp?module="+Module.REPORT;
	}
	
	public FinancialSpaceBean getFinancialSpace()
	{
		return financialSpace;
	}

	public void setFinancialSpace(FinancialSpaceBean financialSpace)
	{
		this.financialSpace = financialSpace;
	}

	public String getFinancialName()
	{
		return financialName;
	}

	public String getSpaceName()
	{
		return spaceName;
	}

	public void setSpaceName(String spaceName)
	{
		this.spaceName = spaceName;
	}

	public Integer getSpaceId()
	{
		return spaceId;
	}

	public void setSpaceId(Integer spaceId)
	{
		this.spaceId = spaceId;
	}

	public Integer getPersonId()
	{
		return personId;
	}

	public void setPersonId(Integer personId)
	{
		this.personId = personId;
	}

	public String getFinancialId()
	{
		return financialId;
	}

	public void setFinancialId(String financialId)
	{
		this.financialId = financialId;
	}

	public Integer getModuleId()
	{
		return moduleId;
	}

	public void setModuleId(Integer moduleId)
	{
		this.moduleId = moduleId;
	}

	public RequestGlobals getRequestGlobals()
	{
		return requestGlobals;
	}

	public void setRequestGlobals(RequestGlobals requestGlobals)
	{
		this.requestGlobals = requestGlobals;
	}

	/**
     * Returns the String to insert a "Personalize" link at the bottom of the page.
     * @return HTML proving a link to CustomizeChannels.jsp
     */
    public String getPersonalizeLink() {
    	StringBuffer url = new StringBuffer();
//    	try {
//		    	PersonProperty settings = new PersonProperty();
//		    	settings.setScope(ScopeType.SPACE.makeScope(SessionManager.getUser()));
//		        String qs = HttpUtils.getRedirectParameterString(requestGlobals.getHTTPServletRequest());
//		        
//		        if (qs != null) {
//					qs = "?" + qs;
//				} else {
//					qs = "";
//				}
//        
//            	url.append(SessionManager.getJSPRootURL()).append("/channel/CustomizeChannels.jsp?referer=").append(URLEncoder.encode(getJSPRootURL()+ requestGlobals.getHTTPServletRequest().getServletPath() + qs));
//
//	            // Add the scope so that it is build into personalize link
//	            url.append("&").append(settings.getScope().formatRequestParameters());
//	
//	            // Need to add id and name of every widget
//	            url.append("&name=").append(URLEncoder.encode(PROJECT_SPACE_PIE_CHART+ projectSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(BAR_CHART_TITLE, SessionManager.getCharacterEncoding()));
//	            url.append("&name=").append(URLEncoder.encode(PROJECT_SPACE_PROJECT_COMPLETION + projectSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(PROJECT_COMPLETION_TITLE, SessionManager.getCharacterEncoding()));
//	            url.append("&name=").append(URLEncoder.encode(PROJECT_SPACE_PHASES + projectSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(PHASES_TITLE, SessionManager.getCharacterEncoding()));
//	            url.append("&name=").append(URLEncoder.encode(PROJECT_SPACE_SUBPROJECTS + projectSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(SUBPROJECTS_TITLE, SessionManager.getCharacterEncoding()));
//	            url.append("&name=").append(URLEncoder.encode(PROJECT_SPACE_MEETINGS + projectSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(MEETINGS_TITLE, SessionManager.getCharacterEncoding()));
//	            url.append("&name=").append(URLEncoder.encode(PROJECT_SPACE_PROJECT_NEWS + projectSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(PROJECT_NEWS_TITLE, SessionManager.getCharacterEncoding()));
//	            url.append("&name=").append(URLEncoder.encode(PROJECT_SPACE_MATERIALS + projectSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(MATERIALS_TITLE, SessionManager.getCharacterEncoding()));
//	            url.append("&name=").append(URLEncoder.encode(PROJECT_SPACE_TEAMMATES + projectSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(TEAMMATES_TITLE, SessionManager.getCharacterEncoding()));
//	            url.append("&name=").append(URLEncoder.encode(PROJECT_SPACE_PROJECT_CHANGES + projectSpace.getName(), SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(PROJECT_CHANGES_TITLE, SessionManager.getCharacterEncoding()));
//	            
//	        } catch (Exception e) {
//	            log.error(e.getMessage()); 
//	        }
    	
    	url.append("about:blank");
        return url.toString();
    }

	public String getPROPERTY()
	{
		return PROPERTY;
	}

	public String getCHANNEL_PROPERTY_CONTEXT()
	{
		return CHANNEL_PROPERTY_CONTEXT;
	}

	public String getCloseTitle() {
		return closeTitle;
	}

	public String getUpTitle() {
		return upTitle;
	}
	
	public String getDownTitle() {
		return downTitle;
	}	
	
	public boolean isFinancialLogo()
	{
		return financialLogo;
	}
	
	public String getLogoUrl()
	{
		return logoUrl;
	}

	public boolean isActionsIconEnabled()
	{
		return actionsIconEnabled;
	}
	
	public boolean isBlogEnabled()
	{
		return blogEnabled;
	}

	public boolean isProjectsCloseState()
	{
		return projectsCloseState;
	}

	public boolean isProjectsState()
	{
		return projectsState;
	}
}
