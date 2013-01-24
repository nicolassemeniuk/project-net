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
package net.project.view.pages.portfolio;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.finder.FinderIngredientHTMLConsumer;
import net.project.base.finder.FinderIngredientHTMLProducer;
import net.project.base.finder.FinderSorter;
import net.project.base.finder.FinderSorterList;
import net.project.base.property.PropertyProvider;
import net.project.channel.ScopeType;
import net.project.channel.State;
import net.project.hibernate.model.PnPersonProperty;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.portfolio.ProjectPortfolioBean;
import net.project.portfolio.ProjectPortfolioCSVDownload;
import net.project.portfolio.view.IView;
import net.project.portfolio.view.MetaColumn;
import net.project.portfolio.view.MetaColumnList;
import net.project.portfolio.view.PersonalPortfolioDefaultScenario;
import net.project.portfolio.view.PersonalPortfolioFinderIngredients;
import net.project.portfolio.view.PersonalPortfolioView;
import net.project.portfolio.view.PersonalPortfolioViewBuilder;
import net.project.portfolio.view.PersonalPortfolioViewContext;
import net.project.portfolio.view.PersonalPortfolioViewList;
import net.project.portfolio.view.PersonalPortfolioViewResults;
import net.project.portfolio.view.ResultType;
import net.project.portfolio.view.ViewBuilderFilterPage;
import net.project.portfolio.view.ViewException;
import net.project.project.ProjectSpace;
import net.project.resource.PersonProperty;
import net.project.resource.ProjectColumn;
import net.project.resource.ProjectStoreDataFactory;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.space.PersonalSpaceBean;
import net.project.space.Space;
import net.project.util.HttpUtils;
import net.project.util.ProjectNode;
import net.project.util.StringUtils;
import net.project.util.VisitException;
import net.project.view.pages.base.BasePage;

import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.util.TextStreamResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

/**
 * This class used to display a List of Projects.
 * 
 * @author Ritesh S
 * 
 */
@IncludeJavaScriptLibrary("${tapestry.scriptaculous}/prototype.js")
public class ProjectPortfolio extends BasePage {
	
	private static Logger log = logger;
	
	private final String PROPERTY = "state";	
	
	private final String CHANNEL_PROPERTY_CONTEXT = "net.project.channel.";
	
	private final String TWO_PANE_PORTFOLIO_PROPERTY_CONTEXT = "net.project.portfolio.twopane.";

	private final String PROJECT_STATUS_WIDGET = "portfolio_status";
	
	private final String PROJECT_BUDGET_WIDGET = "portfolio_budget";
	
	private final String PROJECT_STATUS_WIDGET_TITLE = "Project Status";
	
	private final String PROJECT_BUDGET_WIDGET_TITLE = "Project Budget";
	
	private final String RIGHT_PANE_COLLAPSED = "right_pane_collapsed";
	
	private final String RIGHT_PANE_ACTIVE_TAB = "right_pane_active_tab";
	
	private final String PROJECT_LIST_PANEL_WIDTH = "project_list_panel_width";
	
	@Property
	private ProjectNode node;

	@Property
	@InjectPage
	private ProjectListPage projectListPage;

	@Persist
	private ProjectColumn projectColumn;

	@Property
	@Persist
	private List<ProjectNode> projectList;

	@Property
	private Integer moduleId = Module.PERSONAL_SPACE;
	
	@Property
	private Integer spaceId;
	
	@Property
	private int createAction = Action.CREATE;

	@Property
	private int modifyAction = Action.MODIFY;
	
	@Property
	private int viewAction = Action.VIEW;
	
	@Property
	private String objectType = ObjectType.PROJECT;
	
	@Property
	private boolean expanded;
	
	
	@Property
	private boolean portfolioStatusState;
	
	@Property
	private boolean portfolioStatusCloseState;
	
	@Property
	private boolean portfolioBudgetState;
	
	@Property
	private boolean portfolioBudgetCloseState;
	
	@Property
	private String portfolioList;
	
	@Property
	@Persist
	private PersonalPortfolioView view;	

	@Property
	private int currentModule;
	
	@Property
	private boolean rightPanelCollapsed = false;
	
	@Property
	private String rightTabSetActiveTab;
	
	@Property
	private int projectListPanelWidth;
	
	@Property
	private int taskListTableContainerWidth;
	
	@Persist
	private PersonalPortfolioViewResults viewResults;

	@Persist
	private PersonalPortfolioViewContext viewContext;
	
	@Property
	private String sharedViewList;
	
	@Property
	private boolean isSharedView = false;
	
	@Property 
	private List<MetaColumn> colList;
	
	@Property
	private boolean isDefaultTreeView = false;
	
	@Property
	@Persist
	private PersonalPortfolioViewBuilder personalPortfolioViewBuilder;
	
	@Property
	private boolean isUserCreatedView = false;
	
	@Property
	@Persist
	private ProjectPortfolioBean projectPortfolio;
	
	@Property
    private PersonalPortfolioViewList viewList = null;
	
    private enum PortfolioActions {
    	SAVECONTEXTCHANGE,EXPORTCSV,RELOAD,UPDATEPROJECTLIST,SAVECOLUMNORDER,SAVECOLUMNWIDTH,
    	CHANGEVIEW,APPLYFILTERS,APPLYSORT,RELOADPROJECTPORTFOLIOVIEWDROPDOWLIST,RELOADPROJECTPORTFOLIOVIEWFILTER;
		public static PortfolioActions get( String v ) {
            try {
                return PortfolioActions.valueOf( v.toUpperCase() );
            } catch( Exception ex ) { }
            return null;
         }
	}

    /**
	 * Method called on page activation
	 */
	Object onActivate() {
		if(checkForUser() != null) {
			return checkForUser();
		}	
	    Space currentSpace = SessionManager.getUser().getCurrentSpace();
	    spaceId = Integer.parseInt(currentSpace.getID());

	    if (currentSpace.isTypeOf(Space.PERSONAL_SPACE)) {
	        currentModule = Module.PERSONAL_SPACE;
	    } else if (currentSpace.isTypeOf(Space.BUSINESS_SPACE)) {
	        currentModule = Module.BUSINESS_SPACE;
	    } else if (currentSpace.isTypeOf(Space.PROJECT_SPACE)) {
	        currentModule = Module.PROJECT_SPACE;
	    } else {
	    	currentModule = Module.PERSONAL_SPACE;
	    }
	    String viewID = getRequestParameter("viewID");
	    // Grab the view for the viewID or the default view for the PORTFOLIO scenario
	    if (viewID == null)
			viewID = (String) getSessionAttribute("ppviewID");

	    projectPortfolio = new ProjectPortfolioBean();
	    projectPortfolio.clear();
	    projectPortfolio.setID(ProjectPortfolioBean.getUserPortfolioID(SessionManager.getUser().getID()));
	    projectPortfolio.setUser(SessionManager.getUser());
	    try{
		    projectPortfolio.load();
	    } catch (PersistenceException pnetEx) {
	    	log.error("Error occurred while generating personalize page link :" + pnetEx.getMessage()); 
		}
	    initilizeProjectPortfolioViewContext();
	    initializeProjectPortfolioView(viewID, personalPortfolioViewBuilder);
	    initializeProjectPortfolioViewDropDownListModel();
	    initializeProjectPortfolioPersonalizeSettings();
	    colList = projectListPage.getAllColumnList().getAllColumns();
	    setSessionAttribute("projectPortfolio", projectPortfolio);
	    setSessionAttribute("isFromProjectPortfolio", true);
		return null;
	}

	/**
	 * Method called on page activation with single parameter
	 * 
	 * @param action to perform
	 */
	Object onActivate(String action) {		
		PortfolioActions portfolioAction = PortfolioActions.get( action );

		if (portfolioAction.equals(PortfolioActions.SAVECONTEXTCHANGE)) {
			if(replaceContextState(getHttpServletRequest())){
				return new TextStreamResponse("text/plain","success");
			}
		} else if(portfolioAction.equals(PortfolioActions.EXPORTCSV)){
			ProjectPortfolioCSVDownload projectPortfolioCSVDownload  = new ProjectPortfolioCSVDownload(projectList,projectListPage.getProjectColumnList());
			getRequest().getSession(true).setAttribute("projectPortfolioCSVDownload", projectPortfolioCSVDownload);
		} else if (portfolioAction.equals(PortfolioActions.RELOAD)){
			projectListPage.arrangeProjectSequence(projectListPage.getProjectList());
			return projectListPage;
		} else if (portfolioAction.equals(PortfolioActions.UPDATEPROJECTLIST)){
			projectList = projectListPage.arrangeProjectSequence(projectListPage.getProjectList());
		} else if (portfolioAction.equals(PortfolioActions.SAVECOLUMNORDER)){
			projectListPage.handleColumnDragAndDrop(getRequestParameter("draggedColumn"), getRequestParameter("droppedColumn"),
					Integer.parseInt(getRequestParameter("draggedColumnOrder"))+1, Integer.parseInt(getRequestParameter("droppedColumnOrder"))+1);
			projectList = projectListPage.arrangeProjectSequence(projectListPage.getProjectList());
			if(!projectList.isEmpty()){
				return new TextStreamResponse("text/plain","success");
			}
		} else if (portfolioAction.equals(PortfolioActions.SAVECOLUMNWIDTH)){
			projectListPage.saveColumnWidth(getRequestParameter("columnName"), Integer.parseInt(getRequestParameter("width").toString()));
			return new TextStreamResponse("text/plain","success");
		} else if (portfolioAction.equals(PortfolioActions.CHANGEVIEW)){
			initializeProjectPortfolioView(getRequestParameter("viewID").toString(),personalPortfolioViewBuilder);
			setSessionAttribute("isSharedView", getRequestParameter("isSharedView"));
		} else if (portfolioAction.equals(PortfolioActions.APPLYFILTERS)){
			return applyProjectPortfolioViewFilter(getHttpServletRequest());
		} else if (portfolioAction.equals(PortfolioActions.APPLYSORT)){
			return applySortingOnProjectPortfolioView(getRequestParameter("sorterParameterString"), Boolean.parseBoolean(getRequestParameter("multiLevelSort")));
		} else if(portfolioAction.equals(PortfolioActions.RELOADPROJECTPORTFOLIOVIEWDROPDOWLIST)){
			initilizeProjectPortfolioViewContext();
			initializeProjectPortfolioView(getRequestParameter("viewID").toString(),personalPortfolioViewBuilder);
			return new TextStreamResponse("text/html", getProjectPortfolioViewDropDownListModel(getRequestParameter("viewID")));
		} else if(portfolioAction.equals(PortfolioActions.RELOADPROJECTPORTFOLIOVIEWFILTER)){
			return new TextStreamResponse("text/html", getProjectPortfolioViewFilterHtml());
		}
		return null;
	}

	/**
     * Returns the String to insert a Personalize Page link.
     * @return HTML proving a link to CustomizeChannels.jsp
     */
    public String getPersonalizeLink() {
    	StringBuffer url = new StringBuffer();
    	try {
		    PersonProperty settings = new PersonProperty();
		    settings.setScope(ScopeType.GLOBAL.makeScope(SessionManager.getUser()));
		    String qs = HttpUtils.getRedirectParameterString(getHttpServletRequest());

		    qs = StringUtils.isEmpty(qs) ? "" : "?" + qs ;
           	url.append(SessionManager.getJSPRootURL()).append("/channel/CustomizeChannels.jsp?referer=").append(URLEncoder.encode(getJSPRootURL()+ getHttpServletRequest().getServletPath() + qs, SessionManager.getCharacterEncoding()));

           	// Add the scope so that it is build into personalize link
           	url.append("&").append(settings.getScope().formatRequestParameters());
           	
           	// Need to add id and name of every widget
          	url.append("&name=").append(URLEncoder.encode(PROJECT_STATUS_WIDGET, SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(PROJECT_STATUS_WIDGET_TITLE, SessionManager.getCharacterEncoding()));
	        url.append("&name=").append(URLEncoder.encode(PROJECT_BUDGET_WIDGET, SessionManager.getCharacterEncoding())).append("&title=").append(URLEncoder.encode(PROJECT_BUDGET_WIDGET_TITLE, SessionManager.getCharacterEncoding()));
	    } catch (Exception e) {
	    	log.error("Error occurred while generating personalize page link :" + e.getMessage()); 
	    }
        return url.toString();
    }

	/**
	 * Invoked for getting Project treeGrid JSON string.
	 * 
	 * @return List<ProjectNode>
	 */
	public List<ProjectNode> getProjectTreeData(Collection<ProjectSpace> projectSpace, boolean isDeafultTreeView) {
		ProjectStoreDataFactory dataFactory =   new ProjectStoreDataFactory();
		return dataFactory.getProjectGridDataString(projectSpace, isDeafultTreeView);
	}
	
	/**
	 * To get first project Id
	 * @return String
	 */
	public String getFirstProjectId(){
		 if (projectList.size() > 0) {
			 ProjectNode firstNode = (ProjectNode)projectList.get(0);
	         return firstNode.getProject().getProjectId()+"_"+firstNode.getId();
	     } else {
	         return StringUtils.EMPTY;
	     }
	}

	/**
	 * Initializing all personalize settings contexts	
	 */
	private void initializeProjectPortfolioPersonalizeSettings(){
		spaceId = Integer.parseInt(ScopeType.GLOBAL.makeScope(SessionManager.getUser()).getSpaceID());
		Integer personId = Integer.parseInt(SessionManager.getUser().getID());
		List<PnPersonProperty> personProperties = null; 
		
		if (spaceId != null && personId != null) {
			personProperties = ServiceFactory.getInstance()
					.getPnPersonPropertyService().getPersonProperties(spaceId,
							personId, PROPERTY);
		}
		for (PnPersonProperty pnPersonProperty : personProperties) {
			if (pnPersonProperty.getComp_id().getContext().equals(CHANNEL_PROPERTY_CONTEXT + PROJECT_STATUS_WIDGET)) {
				portfolioStatusState = pnPersonProperty.getComp_id().getValue().equals(State.MINIMIZED.getID());
				portfolioStatusCloseState = pnPersonProperty.getComp_id().getValue().equals(State.CLOSED.getID());
			}
			if (pnPersonProperty.getComp_id().getContext().equals(CHANNEL_PROPERTY_CONTEXT + PROJECT_BUDGET_WIDGET)) {
				portfolioBudgetState = pnPersonProperty.getComp_id().getValue().equals(State.MINIMIZED.getID());
				portfolioBudgetCloseState = pnPersonProperty.getComp_id().getValue().equals(State.CLOSED.getID());
			}
			if (pnPersonProperty.getComp_id().getContext().equals(TWO_PANE_PORTFOLIO_PROPERTY_CONTEXT + RIGHT_PANE_COLLAPSED)) {
				rightPanelCollapsed = pnPersonProperty.getComp_id().getValue().equals(State.CLOSED.getID());
			}
			if (pnPersonProperty.getComp_id().getContext().equals(TWO_PANE_PORTFOLIO_PROPERTY_CONTEXT + RIGHT_PANE_ACTIVE_TAB)) {
				rightTabSetActiveTab = StringUtils.isEmpty(pnPersonProperty.getComp_id().getValue()) ? "blog-tab" : pnPersonProperty.getComp_id().getValue();
			}
			if (pnPersonProperty.getComp_id().getContext().equals(TWO_PANE_PORTFOLIO_PROPERTY_CONTEXT + PROJECT_LIST_PANEL_WIDTH)) {
				projectListPanelWidth = Double.valueOf(pnPersonProperty.getComp_id().getValue()).intValue();
			}
		}
	}

	/**
	 * Method for saving personalize settings in pnPersonProperty table
	 * 
	 * @param request
	 * @return boolean
	 */
	private boolean replaceContextState(HttpServletRequest request) {
		String value = request.getParameter("value");
		String contextSuffix = request.getParameter("context");
		String propertyType = request.getParameter("type");
		if (StringUtils.isNotEmpty(value) && StringUtils.isNotEmpty(contextSuffix)) {
			PersonProperty personProperty = new PersonProperty();
			personProperty.setScope(ScopeType.GLOBAL.makeScope(SessionManager.getUser()));
			try {
				if(StringUtils.equals(propertyType, "widget")){
					personProperty.removeAllValues(CHANNEL_PROPERTY_CONTEXT + contextSuffix, PROPERTY);
					personProperty.put(CHANNEL_PROPERTY_CONTEXT + contextSuffix, PROPERTY, value);
				} else {
					personProperty.removeAllValues(TWO_PANE_PORTFOLIO_PROPERTY_CONTEXT + contextSuffix, PROPERTY);
					personProperty.put(TWO_PANE_PORTFOLIO_PROPERTY_CONTEXT + contextSuffix, PROPERTY, value);
				}
				// set this person property in session so that in channelmanager.jsp we can get the recently updated state of each channel
				setSessionAttribute("PR_user_properties", personProperty);
			} catch (PersistenceException pnetEx) {
				log.error("Database error while replacing context property: " + pnetEx.getMessage());
				return false;
			}
		}
		return true;
	}

	
	/**
	 * To initialize projectList according to view selected from  drop down list
	 * using Ajax request
	 */
	private void initializeProjectPortfolioView(String viewID, PersonalPortfolioViewBuilder viewBuilder ){
		try {
			viewList = (PersonalPortfolioViewList) viewContext.makeViewList();
           	view = (PersonalPortfolioView) viewList.getViewOrDefault(viewID, PersonalPortfolioDefaultScenario.PORTFOLIO);
	        if(view == null){
	        	view = (PersonalPortfolioView) viewList.getDefaultViews().get(0);	
	        }
           	viewResults = (PersonalPortfolioViewResults) view.getResults();
			if(!("default").equals(view.getID()) && viewBuilder != null){
				personalPortfolioViewBuilder = viewBuilder;
				personalPortfolioViewBuilder.editView(view.getID());
				personalPortfolioViewBuilder.load();
			}
	    } catch (PersistenceException pnetEx) {
			log.error("Error occurred while retrieving portfolio views: " + pnetEx.getMessage());
		} catch (ViewException e){
			log.error("Error occurred while loading customize view: " + e.getMessage());
		}

		PersonalPortfolioFinderIngredients personalPortfolioFinderIngredients = (PersonalPortfolioFinderIngredients)viewResults.getIngredients();
		boolean isWorkCompletionColumn = false;
		boolean orderDescending = false;
		List<FinderSorter> sorterList = personalPortfolioFinderIngredients.getFinderSorterList().getAllSorters(); 
		for(FinderSorter sorter : sorterList){
				if(sorter.isSelected() && sorter.getSelectedColumn().getColumnName().equalsIgnoreCase("p.percent_complete")){
					isWorkCompletionColumn = true;
					orderDescending = sorter.isDescending();
					break;
				}
		}

		Collection<ProjectSpace> projectSpace = getProjectList(viewResults.getProjectSpaceResultElements(), isWorkCompletionColumn, orderDescending);
		
		if(viewResults.getResultType().equals(ResultType.TREE)){
			projectListPage.setProjectColumnList(personalPortfolioFinderIngredients.getMetaColumnList(), "Tree");
		} else {
			projectListPage.setProjectColumnList(personalPortfolioFinderIngredients.getMetaColumnList(), "Custom");
		}
		
		projectList = projectListPage.arrangeProjectSequence(getProjectTreeData(projectSpace,viewResults.getResultType().equals(ResultType.TREE)));
	    isSharedView = view.isViewShared();
	    isDefaultTreeView = ("default").equals(view.getID());
	    if(!isDefaultTreeView){
		    isUserCreatedView = SessionManager.getUser().getID().equals(view.getCreatedByID()); 
	    }
	    projectListPage.setSorterList(sorterList);
		setSessionAttribute("ppviewID", view.getID());
		setSessionAttribute("viewList", viewList);
		setSessionAttribute("view", view);
		setSessionAttribute("personalPortfolioViewBuilder", personalPortfolioViewBuilder);
		setSessionAttribute("portfolioBudgetChartEntryCollection", projectSpace);
		setSessionAttribute("viewResults", viewResults);
	}

	/**
	 * To apply filters on a view
	 * @param request
	 */
	private Object applyProjectPortfolioViewFilter(HttpServletRequest request){
		String viewID = (String)(getSessionAttribute("ppviewID"));
		PersonalPortfolioViewResults viewResults;
		String responseText = "{success: false}";
			try{
				for (Iterator it = personalPortfolioViewBuilder.getFilterPages().iterator(); it.hasNext();) {
					ViewBuilderFilterPage nextPage = (ViewBuilderFilterPage) it.next();
					FinderIngredientHTMLConsumer consumer = new FinderIngredientHTMLConsumer(request);
					nextPage.getFinderFilterList().accept(consumer);
				}
			    viewResults = (PersonalPortfolioViewResults) view.getFilteredResults(personalPortfolioViewBuilder.getFinderIngredients());
			
				PersonalPortfolioFinderIngredients personalPortfolioFinderIngredients = (PersonalPortfolioFinderIngredients)viewResults.getIngredients();
				Collection<ProjectSpace> projectSpace = viewResults.getProjectSpaceResultElements();				
				projectList = projectListPage.arrangeProjectSequence(getProjectTreeData(projectSpace,viewResults.getResultType().equals(ResultType.TREE)));

			} catch (VisitException pnetEx) {
				log.error("Error occurred while retrieving portfolio view result: " + pnetEx.getMessage());
			}  catch (PersistenceException e) {
				log.error("Error occurred while retrieving portfolio view result: " + e.getMessage());
			}
		return new TextStreamResponse("json",responseText);
	}
	
	/**
	 * To initialize ProjectPortfolioViewContext
	 * for managing project portfolio views
	 */
	private void initilizeProjectPortfolioViewContext(){
		viewContext = new PersonalPortfolioViewContext(projectPortfolio);
		PersonalSpaceBean personalSpace = new PersonalSpaceBean();
		personalPortfolioViewBuilder = new PersonalPortfolioViewBuilder(viewContext);
		personalSpace.setUser(SessionManager.getUser());
		viewContext.setSpace(personalSpace);
	    viewContext.setCurrentUser(SessionManager.getUser());
	}

	/**
	 * To initialize drop-down view list model containing drop-down list items and corresponding values
	 */
	private void initializeProjectPortfolioViewDropDownListModel(){
       	portfolioList = "";
       	sharedViewList = "";
       	List<PersonalPortfolioView> personalPortfolioViewList = viewList.getAllViews(); 
   		for(PersonalPortfolioView personalPortfolioView : personalPortfolioViewList){
            IView nextView = (IView) personalPortfolioView;
            if(nextView.isViewShared()){
            	if(SessionManager.getUser().getID().equals(nextView.getCreatedByID())){ // if view is created by current user add view to portfolio list
            		portfolioList +=  nextView.getID()+"="+nextView.getName()+" "+ getSharedViewNameSuffixValue() +",";
            	} else {	// if view is created by other user add view to shared portfolio list
            		sharedViewList += nextView.getID()+"="+nextView.getName()+"," ;
            	}
            } else {
            	portfolioList += ("default").equals(nextView.getID()) ? nextView.getID()+"=" + getDefaultViewNameLable() + "," : nextView.getID()+"="+nextView.getName()+",";
            }
   		}
   		sharedViewList = sharedViewList.equals("") ? "none="+getNoSharedViewListItem()+"" : sharedViewList ;
	}

	/**
	 * To apply sorting on a view
	 */
	private Object applySortingOnProjectPortfolioView(String sorterParameterString, boolean multiLevelSort){
		PersonalPortfolioViewResults viewResults;
		PersonalPortfolioFinderIngredients finderIngredients;
		FinderSorterList finderSorterList ;
		String responseText = "{success: false}";
		boolean isWorkCompletionColumn = false;
        if(StringUtils.isNotEmpty(sorterParameterString)){
			try{
				finderIngredients = (PersonalPortfolioFinderIngredients)personalPortfolioViewBuilder.getFinderIngredients();
				finderSorterList = finderIngredients.getFinderSorterList();
				List<FinderSorter> sorterList = finderSorterList.getAllSorters();
				List<FinderSorter> sorterAppliedList = new ArrayList<FinderSorter> (); 
				JSONArray jsArray = new JSONArray(sorterParameterString);
				/*
				 * sorterList contains three object of FinderSorter and we can apply sorting on three column in a view. 
				 * When we are applying sorting on a view for first time all FinderSorter are not selected.
				 * While applying sorting we set the selected column in FinderSorter and sorting order.
				 * This loop will execute three times to manage sorting on three columns at a time.
				 */
				if(multiLevelSort) { // when sorting is applied from customize column setting dialog
					int index = 0;
					for(FinderSorter sorter : sorterList){
						if(index < jsArray.length()){
							JSONObject object = jsArray.getJSONObject(index);				
							sorter.setSelectedColumnByName(object.getString("columnName"));
							sorter.setDescending(Boolean.parseBoolean(object.getString("order")));
							sorter.setSelected(true);
						} else {
							sorter.setSelected(false);
						}
						sorterAppliedList.add(sorter);
						index++;
					}
				} else { // when sorting is applied by clicking column header
					JSONObject object = jsArray.getJSONObject(0);				
					boolean sortingApplied = false;
					 for(FinderSorter sorter : sorterList){
						if(!sortingApplied){
							sorter.setSelectedColumnByName(object.getString("columnName"));
							sorter.setDescending(Boolean.parseBoolean(object.getString("order")));
							sorter.setSelected(true);
							sortingApplied = true;
							if(("p.percent_complete").equalsIgnoreCase(object.getString("columnName")))
								isWorkCompletionColumn = true;
						} else {
							sorter.setSelected(false);
						}
						sorterAppliedList.add(sorter);
					}
				}
				projectListPage.setSorterList(sorterAppliedList);
				finderSorterList.clear();
				finderSorterList.addAll(sorterAppliedList);
				finderIngredients.setSorterList(finderSorterList);
				personalPortfolioViewBuilder.setFinderIngredients(finderIngredients);
	
				viewResults = (PersonalPortfolioViewResults) view.getFilteredResults(personalPortfolioViewBuilder.getFinderIngredients());
				PersonalPortfolioFinderIngredients personalPortfolioFinderIngredients = (PersonalPortfolioFinderIngredients)personalPortfolioViewBuilder.getFinderIngredients();
				Collection<ProjectSpace> projectSpace = getProjectList(viewResults.getProjectSpaceResultElements(), isWorkCompletionColumn, false);
				projectList = projectListPage.arrangeProjectSequence(getProjectTreeData(projectSpace,viewResults.getResultType().equals(ResultType.TREE)));
				responseText = "{success: true}";
			} catch (PersistenceException pnetEx) {
				log.error("Error occurred while retrieving portfolio view result: " + pnetEx.getMessage());
			} catch (JSONException pnetEx) {
				log.error("Error occured while jsonToObjectLibertal : " + pnetEx.getMessage());
			}
        }
		return new TextStreamResponse("json",responseText);
	}
	
	/**
	 * To Generating html for project protfolio view drop-down view list containing view name and viewId.
	 * This view list will be updated using ajax call when a new view is created or existing view is modified.
	 * @param projectId
	 * @return portfolioListHtmlString
	 */
	private String getProjectPortfolioViewDropDownListModel(String projectId){
		String portfolioListHtmlString = PropertyProvider.get("prm.project.portfolio.mainpage.selectview.label")+"&nbsp;";
		
		portfolioListHtmlString += "<select id='portfolioList' name='portfolioList' class='fixed-width-form' onchange='changeMyView()'>" +
						"<option value=''></option>";

       	List<PersonalPortfolioView> personalPortfolioViewList = viewList.getAllViews(); 
   		for(PersonalPortfolioView personalPortfolioView : personalPortfolioViewList){

			IView nextView = (IView) personalPortfolioView;
            if(nextView.isViewShared()){
            	if(SessionManager.getUser().getID().equals(nextView.getCreatedByID())){
            		portfolioListHtmlString += projectId.equals(nextView.getID()) ? "<option value=\"" + nextView.getID() + "\" selected=\"selected\">" + nextView.getName()+" "+ getSharedViewNameSuffixValue() + "</option>" : "<option value=\"" + nextView.getID() + "\">" + nextView.getName()+" "+ getSharedViewNameSuffixValue() + "</option>";
            	}
            } else {
            	portfolioListHtmlString += projectId.equals(nextView.getID()) ? (("default").equals(nextView.getID()) ? "<option value=\"" + nextView.getID() + "\" selected=\"selected\">" + getDefaultViewNameLable() + "</option>" : "<option value=\"" + nextView.getID() + "\" selected=\"selected\">" + nextView.getName() + "</option>") : (("default").equals(nextView.getID()) ? "<option value=\"" + nextView.getID() + "\">" + getDefaultViewNameLable() + "</option>" : "<option value=\"" + nextView.getID() + "\">" + nextView.getName() + "</option>");
            }
   		}
   		portfolioListHtmlString += "</select>&nbsp;";
   		return portfolioListHtmlString;
	}
	
	/**
	 * To Generating html for project protfolio view filters.
	 * These filters will be updated using ajax call when view is loaded by selection from portfolio view drop down list.
	 * @return filterHtmlString
	 */
	private String getProjectPortfolioViewFilterHtml(){
		String filterHtmlString = "<form id=\"viewFilterForm\" name=\"viewFilterForm\">";
		for (Iterator it = personalPortfolioViewBuilder.getFilterPages().iterator(); it.hasNext();) {
			ViewBuilderFilterPage nextPage = (ViewBuilderFilterPage) it.next();
			FinderIngredientHTMLProducer filterListProducer = new FinderIngredientHTMLProducer();
			try {
				nextPage.getFinderFilterList().accept(filterListProducer);
			} catch (VisitException e) {
				log.error("Error occurred while generating Project Portfolio Filter: " + e.getMessage());
			}
			filterHtmlString += filterListProducer.getHTML() + "<br/>";
		}
		filterHtmlString += "<table width=\"90%\">";
		filterHtmlString += 	"<tr>";
		filterHtmlString +=		"<td align=\"right\"><input type=\"button\" onclick=\"submitFilters();\" value=\"Apply\"/></td>";
		filterHtmlString +=	"</tr>";
		filterHtmlString += "</table>";
		filterHtmlString += "</form>";
		return filterHtmlString;
	}
	
	/**
	 * Method to get List of projects where current user is member
	 * This method is when sorting is applied on project completion column or
	 * a custom view is loaded with sorting applied on project work complete column. 
	 * if sorting is aplied on project work completion column then 
	 * we need to sort it using a comparator 
	 * since project completion value may differ according to selection while creating a project
	 * so when we sort project by data base query it may result in wrong order
	 * If sorting is not applied on Work completion column then project Space is returned as it is. 
	 * @param projectSpace
	 * @return projectSpace
	 */
	private Collection<ProjectSpace> getProjectList(Collection<ProjectSpace> projectSpace, boolean isWorkCompletionColumn, boolean orderDescending){
		List<ProjectSpace> projectList = new ArrayList<ProjectSpace> (projectSpace);
		if(isWorkCompletionColumn){
			if(orderDescending)
				Collections.sort(projectList, Collections.reverseOrder(new ProjectCompletionComparator()));
			else
				Collections.sort(projectList, new ProjectCompletionComparator());
		}
		return (Collection)projectList;
	}

	/**
     * Comparator for comparing <code>ProjectSpace</code> objects
     */
    protected static class ProjectCompletionComparator implements Comparator {
        public int compare(Object obj1, Object obj2) {
        	Double projectCompletion1 = Double.valueOf(((ProjectSpace) obj1).getPercentComplete());
        	Double projectCompletion2 = Double.valueOf(((ProjectSpace) obj2).getPercentComplete());
        	return projectCompletion1.compareTo(projectCompletion2);
        }
    }
	/**
	 * @return pnet tab set width.
	 */
	public int getPnetTabSetWidth() {
		return (getWindowWidth() - 235);
	}

	/**
	 * @return the pnet tab set height
	 */
	public int getPnetTabSetHeight() {
		return (getWindowHeight() - 125);
	}

	/**
	 * @return sliding panel tool bar width.
	 */
	public int getSlidingPanelToolBarWidth() {
		return (getWindowWidth() - 235);
	}

	/**
	 * @return the sliding panel width.
	 */
	public int getSlidingPanelContentWidth() {
		return (getWindowWidth() - 235);
	}

	/**
	 * @return the tab content tab height
	 */
	public int getTabContentHeight() {
		return ((getWindowHeight() - 205) < 475 ? 475 : (getWindowHeight() - 205));
	}

	/**
	 * @return the splitter bar height
	 */
	public int getSplitterBarHeight() {
		return ((getWindowHeight() - 153) < 527 ? 527 : (getWindowHeight() - 153));
	}

	/**
	 * @return the project list header Width
	 */
	public int getProjectListHeaderWidth() {
		return (getWindowWidth() - 306);
	}

	/**
	 * @return the groupHeaderProject
	 */
	public String getGroupHeaderProject() {
		return PropertyProvider.get("prm.project.portfoliio.groupheader.label");
	}

	/**
	 * First checks for personal property if not then get sum of visible column width.
	 * 
	 * @return the Project list container table width
	 */
	public int getProjectListTableContainerWidth() {
		return projectListPanelWidth > 0 ? projectListPanelWidth : (getWindowWidth() - 235); 
	}

	/**
	 * @return closeTitle
	 */
	public String getCloseTitle() {
		return PropertyProvider.get("all.global.channelbarbutton.title.close");
	}
	
	/**
	 * @return upTitle
	 */
	public String getUpTitle() {
		return PropertyProvider.get("all.global.channelbarbutton.title.minimize");
	}
	
	/**
	 * @return downTitle
	 */
	public String getDownTitle() {
		return PropertyProvider.get("all.global.channelbarbutton.title.maximize");
	}
	
	/**
	 * @return
	 */
	public String getNoSharedViewListItem(){
		return PropertyProvider.get("prm.project.portfolio.mainpage.sharedviewlist.noneavailable.itemtext");
	}
	
	/**
	 * @return
	 */
	public String getDefaultViewNameLable() {
		return PropertyProvider.get("prm.project.portfolio.mainpage.viewlist.defaultview.itemtext");
	}

	/**
	 * @return
	 */
	public String getSharedViewNameSuffixValue () {
		return PropertyProvider.get("prm.project.portfolio.mainpage.viewlist.sharedviewnamesuffix.text");
	}

	/**
	 * To get left side bar toolbox heading
	 * @return String
	 */
	public String getProjectPortfolioToolBoxHeading(){
		return PropertyProvider.get("prm.project.portfolio.toolbox.heading.title");
	}
	
	/**
	 * To get customize column icon tool tip
	 * @return
	 */
	public String getCusomizeColumnTooltip(){
		return PropertyProvider.get("prm.global.twopaneview.cusomizecolumntooltip.message");
	}

	/**
	 * To get expand all nodes icon tool tip
	 * @return
	 */
	public String getExpandAllTooltip(){
		return PropertyProvider.get("prm.global.twopaneview.expandalltooltip.message");
	}
	
	/**
	 * To get collapse all nodes icon tool tip
	 * @return
	 */
	public String getCollapseAllTooltip(){
		return PropertyProvider.get("prm.global.twopaneview.collapsealltooltip.message");
	}
	
	/**
	 * @return size of project list
	 */
	public int getTotalProjects(){
		return projectList.size();
	}
}