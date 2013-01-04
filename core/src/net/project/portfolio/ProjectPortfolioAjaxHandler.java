/**
 * 
 */
package net.project.portfolio;

import java.sql.SQLException;
import java.util.List;

import net.project.base.finder.FinderIngredients;
import net.project.hibernate.model.PnBusinessHasView;
import net.project.persistence.PersistenceException;
import net.project.portfolio.view.MetaColumn;
import net.project.portfolio.view.PersonalPortfolioFinderIngredients;
import net.project.portfolio.view.PersonalPortfolioViewBuilder;
import net.project.portfolio.view.ViewException;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.tapestry5.util.TextStreamResponse;
import org.slf4j.Logger;

/**
 * To provide functionality of saving customize views
 *
 */
public class ProjectPortfolioAjaxHandler extends BasePage{

	private static Logger log = logger;

	/**
	 * To save column settings 
	 */
	public void saveColumnSetting(PersonalPortfolioViewBuilder viewBuilder, List<MetaColumn> projectColumnList){
		FinderIngredients ingredients = viewBuilder.getFinderIngredients();
	        if (ingredients instanceof PersonalPortfolioFinderIngredients)
	            ((PersonalPortfolioFinderIngredients) ingredients).getMetaColumnList().updateFromProjectPortfolio(projectColumnList);
	}
	
	/**
	 * To create, modify and remove customized views
	 * 
	 * @param viewID
	 * @param theAction
	 * @param viewName
	 * @param viewBuilder
	 * @param projectColumnList
	 * @param defaultScenarioIDValues
	 * @param shareViewToAll
	 * @param bussinessID
	 * @param isSharedView
	 * @param businessHasViewList
	 * @return
	 */
	public Object performCustomizeViewAction(String viewID, String theAction, String viewName, 
			PersonalPortfolioViewBuilder viewBuilder, List<MetaColumn> projectColumnList,
			String[]  defaultScenarioIDValues, boolean shareViewToAll, String[] bussinessID, boolean isSharedView,
			List<PnBusinessHasView> businessHasViewList){
		String response = "failed";
		try{
			if (StringUtils.isNotEmpty(theAction)){
				if(theAction.equals("createportfolio")) {
					FinderIngredients finderIngredients = viewBuilder.getFinderIngredients();
					viewBuilder.createView();
					if(("default").equals(viewID)){
						viewBuilder.setFinderIngredients(finderIngredients);
					}
					viewBuilder.setName(viewName);
					viewBuilder.setDescription("");
					viewBuilder.setViewShared(isSharedView);
					viewBuilder.setVisibleToAll(shareViewToAll);
			    } else if (theAction.equals("modifyportfolio")) {
			    	viewBuilder.setDescription("");
					viewBuilder.setViewShared(isSharedView);
					viewBuilder.setVisibleToAll(shareViewToAll);
			    } else if (theAction.equals("removeportfolio")) {
			    	viewBuilder.removeView(viewID);
			    	viewBuilder.remove();
					return new TextStreamResponse("text/plain", "default");
			    }
			} 			
			saveColumnSetting(viewBuilder, projectColumnList);
			viewBuilder.updateDefaultViewSettings(defaultScenarioIDValues);
			viewBuilder.store();
			Integer viewIdInteger = Integer.parseInt(viewBuilder.getID());

			/*
			 * Remove all entries of current view from PnBusinessHasVie database model.
			 * since, If view is modified and is set unshared with few busuinesses. 
			 */
			if(CollectionUtils.isNotEmpty(businessHasViewList))
				for(PnBusinessHasView businessHasview : businessHasViewList){
					if(businessHasview  != null)
						getPnBusinessHasViewService().delete(businessHasview.getComp_id().getBusinessId(), viewIdInteger);
				}

			for(String business : bussinessID){
				if(StringUtils.isNotEmpty(business))
					getPnBusinessHasViewService().save(Integer.parseInt(business), viewIdInteger);
			}
			response = viewBuilder.getID();
		} catch (PersistenceException pnetEx) {
			log.error("Error occurred while saving portfolio view: " + pnetEx.getMessage());
		} catch (SQLException pnetEx){
			log.error("Error occurred while saving portfolio view: " + pnetEx.getMessage());
		} catch (ViewException pnetEx) {
			log.error("Error occurred while saving portfolio view: " + pnetEx.getMessage());
		}
		return new TextStreamResponse("text/plain", response);
	}
}
