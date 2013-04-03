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
package net.project.view.components;

import net.project.base.property.PropertyProvider;
import net.project.gui.toolbar.Band;
import net.project.gui.toolbar.ButtonType;
import net.project.gui.toolbar.Toolbar;
import net.project.gui.toolbar.ToolbarException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * This component can be used same as current toolbar tag on jsp pages. 
 * Internally this component uses Toolbar class to get the html presentation.
 * Currently this can be used to show standard toolbar items only. 
 * To use this following parameters should be specified -
 * @param buttons - comma separated list of button names as string
 * @param groupTitle - group heading for action box
 * @param showAll - (optional) whether to show all buttons or not, by default: false
 */
public class ActionBox {

	/* Comma separated Button names */
	@Parameter(required = false, defaultPrefix = "literal")
	private String buttons;

	/* Group title for action box */
	@Parameter(required = true, defaultPrefix = "literal")
	private String groupTitle;
	
	/* Space title for action box */
	@Parameter(required = false, defaultPrefix = "literal")
	private String subTitle;

	/* Show all flag wheter to show all buttons or not */
	@Parameter
	private boolean showAll = false;
	
	@Parameter(required = false, defaultPrefix = "literal")
	private String caption;
	
	@Parameter(required = false, defaultPrefix = "literal")
	private String typeTitle;
	
	@Parameter(required = false, defaultPrefix = "literal")
	private String type;

	@Parameter(required = false, defaultPrefix = "literal")
	private Boolean isProjectListPage;

	@Parameter(required = false, defaultPrefix = "literal")
	private Boolean showSpaceDetails;

	@Inject
	private ComponentResources resources;

	private String htmlString;

	private static Logger log;

	/**
	 * Initializing action box by adding buttons specified in buttons parameter,
	 * setting groupTitle parameter and setting flag showAll. 
	 */
	@SetupRender
	void initializeActionBox() {
		log = Logger.getLogger(ActionBox.class);
		Toolbar toolbar = new Toolbar();
		boolean buttonsHasCaption = false;
		try {
			toolbar.setStyle("tool");
			toolbar.setShowAll(showAll);
			toolbar.setGroupTitle(PropertyProvider.get(groupTitle));
			toolbar.setSubTitle(subTitle);
			if(isProjectListPage != null){
				toolbar.setProjectListPage(isProjectListPage);
			}
			if(showSpaceDetails == null){
				toolbar.setShowSpaceDetails(true);
			} else{
				toolbar.setShowSpaceDetails(showSpaceDetails);
			}
			Band toolBand = toolbar.addBand("standard");
			if (StringUtils.isNotEmpty(buttons)) {
				String[] buttonArray = buttons.split(",");
				
				//Code for adding caption of buttons 
				String[] buttonCaptionArray = null;
				if(StringUtils.isNotEmpty(caption)){
					buttonCaptionArray = caption.split(",");
					if(buttonArray.length == buttonCaptionArray.length){
						buttonsHasCaption = true;
					}
				}
				
				for (int index = 0; index < buttonArray.length; index++) {
					if(buttonsHasCaption && StringUtils.isNotBlank(buttonCaptionArray[index])){
						 toolBand.addButton(buttonArray[index].trim()).setLabel(PropertyProvider.get(buttonCaptionArray[index].trim()));
					 }else{
						 toolBand.addButton(buttonArray[index].trim());
					 }
				}
			}
			if(StringUtils.isNotEmpty(type)){
				if(type.equals("schedule")){
					String[] scheduleActions = {ButtonType.TASK_UP.toString(),ButtonType.TASK_DOWN.toString(),
							ButtonType.TASK_LEFT.toString(),ButtonType.TASK_RIGHT.toString(),
							ButtonType.RESOURCES.toString(),ButtonType.MATERIALS.toString(),
							ButtonType.LINK_TASKS.toString(),ButtonType.UNLINK_TASKS.toString(),
							ButtonType.RECALCULATE.toString(),ButtonType.PROPERTIES.toString(),
							ButtonType.EXPORT_PDF.toString()};
					Band scheduletoolBand = toolbar.addBand(type);
					if(StringUtils.isNotEmpty(typeTitle)){
						scheduletoolBand.setGroupHeading(PropertyProvider.get(typeTitle));
					}
					for (String scheduleAction : scheduleActions) {
						scheduletoolBand.addButton(scheduleAction);
					}
					//if assign to phase is enable for schedule toolbar add phase action in schedule.
					if(PropertyProvider.getBoolean("prm.schedule.toolbar.phase.isenabled", false)){
						scheduletoolBand.addButton(ButtonType.CHOOSE_PHASE.toString());
					}
					
				}else if(type.equals("portfolio")){
					String[] portfolioActions = {ButtonType.SAVE_CURRENT_SETTINGS.toString(),
							ButtonType.DELETE_SAVED_VIEWS.toString(),ButtonType.EXPORT_PDF.toString(),
							ButtonType.EXPORT_EXCEL.toString(),ButtonType.EXPORT_CSV.toString()};
					Band portfoliotoolBand = toolbar.addBand(type);
					if(StringUtils.isNotEmpty(typeTitle)){
						portfoliotoolBand.setGroupHeading(PropertyProvider.get(typeTitle));
					}
					for (String portfolioAction : portfolioActions) {
						portfoliotoolBand.addButton(portfolioAction);
					}
				}
			}
			htmlString = toolbar.getPresentation();
		} catch (ToolbarException e) {
			log.error("Error ocurred while getting toolbar " + e.getMessage());
		}
	}
	
	/**
	 * Getting html string generated in setup render phase
	 * @return the toolbar's presentation string
	 */
	public String getHtmlString() {
		return htmlString;
	}
}
