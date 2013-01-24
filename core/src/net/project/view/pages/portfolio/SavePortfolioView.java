/**
 * 
 */
package net.project.view.pages.portfolio;

import java.util.Collection;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.hibernate.model.PnBusinessHasView;
import net.project.hibernate.model.PnBusinessSpaceView;
import net.project.persistence.PersistenceException;
import net.project.portfolio.ProjectPortfolioAjaxHandler;
import net.project.portfolio.view.DefaultViewSetting;
import net.project.portfolio.view.IView;
import net.project.portfolio.view.MetaColumn;
import net.project.portfolio.view.PersonalPortfolioDefaultScenario;
import net.project.portfolio.view.PersonalPortfolioView;
import net.project.portfolio.view.PersonalPortfolioViewBuilder;
import net.project.portfolio.view.PersonalPortfolioViewList;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.slf4j.Logger;

/**
 * To save current two pane view setting in a view
 *
 */
public class SavePortfolioView extends BasePage {
	
	private PersonalPortfolioViewList viewList;

	@Property
	private String viewID;
	
	@Property
	private String viewName;
	
	@Property
	@InjectPage
	private ProjectListPage projectListPage;
	
	private PersonalPortfolioViewBuilder viewBuilder;
	
	private static Logger log = logger;
	
	@Property
	private String personalDashboardScenarioID;

	@Property
	private String portfolioScenarioID;

	@Property
	private boolean defaultOnPersonalDashboard = false;

	@Property
	private boolean defaultOnPortfolio = false;

	@Property
	private User user;
	
	@Property
	private String businessID;
	
	@Property
	private List<PnBusinessSpaceView> userBusinessList;
	
	@Inject
	private PropertyAccess access;
	
	@Property
	private PnBusinessSpaceView business;
	
	@Property
	@Persist
	private boolean isSharedView;
	
	@Property
	private boolean isCreatedByUser;
	
	@Property
	@Persist
	private boolean isVisibleToAll;
	
	@Persist
	private List<PnBusinessHasView> businessHasViewList; 
	
	void onActivate(){
		viewList =(PersonalPortfolioViewList) getSessionAttribute("viewList");
		viewID = (String)(getSessionAttribute("ppviewID"));
		user = SessionManager.getUser();
		userBusinessList = getBusinessSpaceService().findByUser(user, "A");
		viewBuilder = (PersonalPortfolioViewBuilder) getSessionAttribute("personalPortfolioViewBuilder");

		isCreatedByUser = true;
		if( StringUtils.isEmpty(viewID) || ("default").equals(viewID)){
			viewName = "";
		} else {
			businessHasViewList = getPnBusinessHasViewService().getBusinessByView(Integer.parseInt(viewID));
           	List<PersonalPortfolioView> personalPortfolioViewList = viewList.getAllViews(); 
       		for(PersonalPortfolioView personalPortfolioView : personalPortfolioViewList){
   	            IView nextView = (IView) personalPortfolioView;
	            if((viewID).equals(nextView.getID())){
	            	viewName =  nextView.getName();
	            	isSharedView = nextView.isViewShared();
	            	isCreatedByUser = user.getID().equals(nextView.getCreatedByID()) ? true : false;
	            	isVisibleToAll = nextView.isVisibleToAll();
	            	break;
	            }
       		}
			try{
				Collection<DefaultViewSetting> collectionViewSetting = viewBuilder.getDefaultViewSettings();	
				for(DefaultViewSetting viewSetting : collectionViewSetting){
					if(viewSetting.getScenario().getID() == PersonalPortfolioDefaultScenario.PERSONALSPACE_DASHBOARD.getID()){
						personalDashboardScenarioID = viewSetting.getScenario().getID();
						if(viewSetting.isDefault(viewID)){
							defaultOnPersonalDashboard = true;
						}
					} else if(viewSetting.getScenario().getID() == PersonalPortfolioDefaultScenario.PORTFOLIO.getID()){
						portfolioScenarioID = viewSetting.getScenario().getID();
						if(viewSetting.isDefault(viewID)){
							defaultOnPortfolio = true;
						}
					}
				}
			} catch (PersistenceException pnetEx) {
				log.error("Error while while getting default View setting in ProjectPortfolio: " + pnetEx.getMessage());
			}
		}
	}
	
	/**
	 * to handle ajax request
	 * @param action
	 * @return
	 */
	Object onActivate(String action){
		ProjectPortfolioAjaxHandler projectPortfolioAjaxHandler = new ProjectPortfolioAjaxHandler();
		String viewNameEntered = (String) getRequestParameter("viewName");
		viewID = (String)(getSessionAttribute("ppviewID"));
		viewBuilder = (PersonalPortfolioViewBuilder) getSessionAttribute("personalPortfolioViewBuilder");

		if(("removeportfolio").equals(action)){
			return projectPortfolioAjaxHandler.performCustomizeViewAction(viewID, action, viewNameEntered, viewBuilder, null, null, false , null, false, businessHasViewList);
		}

		boolean share = (Boolean.parseBoolean(getRequestParameter("makeShared"))); 
		boolean shareViewToAll = (Boolean.parseBoolean(getRequestParameter("shareAllUsers"))); 
		String[] bussinessID = getRequestParameter("bussinessID").split(",");
		
		/* 
		 * if global view visibility is disabled then user will not be able to mark his choise
         * then set the shareViewToAll value with previously stored value. 
         */		
		if(!getViewVisibilityEnabled())
			shareViewToAll = isVisibleToAll;
		
		List<MetaColumn> projectColumnList = projectListPage.getAllColumnList().getAllColumns();
		PersonalPortfolioView view = (PersonalPortfolioView) getSessionAttribute("view");
		
		String[] defaultScenarioSettingValue  = getRequestParameter("defaultScenarioID").split(",");
		return projectPortfolioAjaxHandler.performCustomizeViewAction(viewID, action, viewNameEntered, viewBuilder, projectColumnList, defaultScenarioSettingValue, shareViewToAll, bussinessID, share, businessHasViewList);
	}

	/**
	 * To get business name multi select html list
	 * busuinesses with whom current view is made shared will be displayed selected.
	 * @return
	 */
	public String getBusinessMultiSelectList() {
		String businessListHtmlString = "<select id=\"businessList\" name=\"businessList\"  size=\"3\" multiple=\"true\" >";
		if(CollectionUtils.isEmpty(businessHasViewList)){
			for(PnBusinessSpaceView userbusiness : userBusinessList){
				businessListHtmlString += "<option value=\"" + userbusiness.getBusinessId() + "\" >" + userbusiness.getBusinessName() +"</option>";
			}
		} else {
			for(PnBusinessSpaceView userbusiness : userBusinessList){
				boolean selectedBusiness = false;
				for(PnBusinessHasView businessHasView : businessHasViewList){
					if(userbusiness.getBusinessId().equals(businessHasView.getPnBusiness().getBusinessId())){
						selectedBusiness = true;
						break;
					}
				}
				businessListHtmlString += selectedBusiness ? "<option value=\"" + userbusiness.getBusinessId() + "\" selected=\"selected\">" + userbusiness.getBusinessName() + "</option>" : "<option value=\"" + userbusiness.getBusinessId() + "\" >" + userbusiness.getBusinessName() +"</option>";				
			}
		}
		businessListHtmlString += "</select>";
		return businessListHtmlString;
	}

	/**
	 * To get value of view visibility token of view shared to all users.
	 * @return boolean
	 */
	public boolean getViewVisibilityEnabled(){  
		return PropertyProvider.getBoolean("prm.global.view.visibility.isenabled");
	}
}
