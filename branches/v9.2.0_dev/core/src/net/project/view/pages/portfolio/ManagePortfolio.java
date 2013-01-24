/**
 * 
 */
package net.project.view.pages.portfolio;

import java.util.List;

import net.project.base.Module;
import net.project.base.PnetException;
import net.project.business.BusinessSpace;
import net.project.hibernate.model.PnBusinessHasView;
import net.project.hibernate.model.PnView;
import net.project.persistence.PersistenceException;
import net.project.portfolio.view.ViewException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;
import net.project.view.pages.base.BasePage;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.util.TextStreamResponse;
import org.slf4j.Logger;

/**
 * This class is used to manage shared views of current business 
 * @author Ritesh S
 *
 */
public class ManagePortfolio extends BasePage{

	private static Logger log = logger;

	@Property
	private User user;
	
	@Property
	private String businessName;

	@Persist
	@Property
	private Integer businessId;
	
	@Property
	private List<PnBusinessHasView> viewList;
	
	@Property
	private PnBusinessHasView businessHasView;
	
	@Property
	private PnView view;
	
	@Property
	private int totalViews;
	
	@Property
	private int moduleId;
	
	@Property
	private String JSPRootURL;
	
	Object onActivate(){
		if(checkForUser() != null) {
			return checkForUser();
		}	
		user = SessionManager.getUser();
		businessName = user.getCurrentSpace().getName();
		businessId = Integer.parseInt(user.getCurrentSpace().getID());
		JSPRootURL = SessionManager.getJSPRootURL();
		viewList = getPnBusinessHasViewService().getSharedViewByBusiness(businessId);
		totalViews = viewList.size();
		moduleId = Module.BUSINESS_SPACE;
	    return null;
	}
	
	/**
	 * Method called on page activation with single parameter
	 * To handel Ajax request for deleting shared views of current business
	 * @param action
	 * @return
	 */
	Object onActivate(String action){
		String viewId = getRequestParameter("viewId");
		String createdById = getRequestParameter("createdBy");
		String response = "success";
		Space businessSpace = new BusinessSpace(businessId.toString());
		try{
			SessionManager.getUser().setCurrentSpace(businessSpace);
			
			/* 
			 * only space administartor of current business or creater of the view has previleges to delete a view.
			 * the view is only deleted from current bussiness
			 * i.e only entry for selected view and current business is removed from PnBusinessHasView database model.
			 */
			if(SessionManager.getUser().isSpaceAdministrator() || createdById.equals(SessionManager.getUser().getID()) ){
				getPnBusinessHasViewService().delete(businessId, Integer.parseInt(viewId));
			}else{
				response = "notsufficientprevilages";
			}
		} catch (ViewException pnetEx) {
	    	log.error("Error occurred while removing project portfolio view :" + pnetEx.getMessage());
	    	response = "failed";
		} catch (PersistenceException pnetEx) {
	    	log.error("Error occurred while removing project portfolio view :" + pnetEx.getMessage());
	    	response = "failed";
		} catch (PnetException pnetEx) {
	    	log.error("Error occurred while removing project portfolio view :" + pnetEx.getMessage());
	    	response = "failed";
		}
		return new TextStreamResponse("text/plain",response);
	}
	
	/**
	 * To get url of current business main page 
	 * @return String
	 */
	public String getBusinessPageLink(){
		return (SessionManager.getJSPRootURL() + "/business/Main.jsp?id=" + user.getCurrentSpace().getID());
	}
	
	/**
	 * To get url of current business setup page
	 * @return String 
	 */
	public String getSetupPageLink(){
		return (SessionManager.getJSPRootURL() + "/business/Setup.jsp?module=" + Module.BUSINESS_SPACE + "&redirectedFromSpace=true");
	}
}
