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
package net.project.view.pages.resource.management;

import java.util.ArrayList;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.hibernate.model.PnResourceList;
import net.project.hibernate.model.PnResourceListHasPersons;
import net.project.hibernate.service.IPnResourceListHasPersonsService;
import net.project.hibernate.service.IPnResourceListService;
import net.project.hibernate.service.ServiceFactory;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.SetupRender;

public class ManageResourcesList {
	
	private static Logger log = Logger.getLogger(ManageResourcesList.class);

	@Persist
	private String pageTitle;

	@Persist
	private String listColumnLabel;

	@Persist
	private String resourcesColumnLabel;

	@Persist
	private String manageResourceListGridData;
	
	@Persist
	private String message = "";

	private String list;

	private Integer hiddenResourceListId;

	private String formAction;
	
	@Persist
	private boolean changeTab;	

	@InjectPage
	private ManageResourcesList manageResourcesList;

	@InjectPage
	private SelectResource_V8_5 selectResource;

	/**
	 * default constructor
	 */
	public ManageResourcesList() {
		setMessage(null);		
		setValues();
		changeTab = false;
	}
	
	@SetupRender
	public void setValues() {
		try {
			getGridData();
			pageTitle = PropertyProvider.get("prm.resource.manageresourcelists.pagetitle");
			listColumnLabel = PropertyProvider.get("prm.resource.manageresourcelists.column.list.label");
			resourcesColumnLabel = PropertyProvider.get("prm.resource.manageresourcelists.column.resources.label");			
		} catch (Exception ex) {
			log.error("Error occured while getting ManageResourcesList page property values.");
			ex.printStackTrace();
		}
	}	

	/**
	 * Method to get grid data, from database and fromat as required, to be
	 * displayed in Ext grid
	 */
	public void getGridData() {
		List<PnResourceList> list = new ArrayList<PnResourceList>();
		IPnResourceListService pnResourceListService = ServiceFactory.getInstance().getPnResourceListService();
		IPnResourceListHasPersonsService pnResourceListHasPersonsService = ServiceFactory.getInstance()
				.getPnResourceListHasPersonsService();

		list = pnResourceListService.getResourceList();		
		Integer resourceCount = 0;
		
		manageResourceListGridData = "";
		manageResourceListGridData += "[ ";		
		for(PnResourceList resourceList : list){
			resourceCount = pnResourceListHasPersonsService.getResourcesCountByListId(resourceList.getId());
			manageResourceListGridData += " [" + resourceList.getId() + ",'DeleteLink'," + "'" + resourceList.getName()
					+ "'," + resourceCount + " ],";			
		}
		
		manageResourceListGridData = manageResourceListGridData.substring(0, manageResourceListGridData.length() - 1)+" ]";
	}

	/**
	 * Method to perform action on delete link from page to delete particular
	 * Resource List as well as resources in it if exist.
	 * 
	 * @return Object page to forward
	 */
	public Object onAction() {
		IPnResourceListHasPersonsService pnResourceListHasPersonsService = ServiceFactory.getInstance()
			.getPnResourceListHasPersonsService();
		IPnResourceListService pnResourceListService = ServiceFactory.getInstance().getPnResourceListService();
		List<PnResourceListHasPersons> personsInResourceList = pnResourceListHasPersonsService
			.getResourcesByListId(getHiddenResourceListId());
		
		if (getFormAction().equalsIgnoreCase("delete")) {			
			if (personsInResourceList != null) {
				for (PnResourceListHasPersons pnResourceListHasPersons : personsInResourceList) {
					pnResourceListHasPersonsService.delete(pnResourceListHasPersons);
				}
			}

			PnResourceList pnResourceList = new PnResourceList(getHiddenResourceListId());			
			pnResourceListService.deleteResourceList(pnResourceList);

			getGridData();
			manageResourcesList.setChangeTab(false);
			manageResourcesList.setManageResourceListGridData(manageResourceListGridData);			
			manageResourcesList.setMessage(PropertyProvider.get("prm.resource.manageresourcelists.resourcelistdeleted.message"));
			return manageResourcesList;	
		} else {
			manageResourcesList.setChangeTab(true);
			selectResource.setBusinessSpace(null);
			selectResource.setPnProjectSpace(null);
			selectResource.setResourceListObject(pnResourceListService.getResourceListById(getHiddenResourceListId()));			
			Integer[] personIds = new Integer[personsInResourceList.size()]; 
			int index=0;
			for(PnResourceListHasPersons resourceListHasPerson : personsInResourceList){
				personIds[index++] = resourceListHasPerson.getComp_id().getPersonId();
			}
			selectResource.getPersonData("Default");
			selectResource.setPersonIds(personIds);			
			return manageResourcesList;				
		}		
	}
	
	@CleanupRender
	public void cleanValues(){
		setMessage(null);
		setChangeTab(false);
	}

	/**
	 * @return Returns the listColumnLabel.
	 */
	public String getListColumnLabel() {
		return listColumnLabel;
	}

	/**
	 * @param listColumnLabel
	 *            The listColumnLabel to set.
	 */
	public void setListColumnLabel(String listColumnLabel) {
		this.listColumnLabel = listColumnLabel;
	}

	/**
	 * @return Returns the pageTitle.
	 */
	public String getPageTitle() {
		return pageTitle;
	}

	/**
	 * @param pageTitle
	 *            The pageTitle to set.
	 */
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	/**
	 * @return Returns the resourcesColumnLabel.
	 */
	public String getResourcesColumnLabel() {
		return resourcesColumnLabel;
	}

	/**
	 * @param resourcesColumnLabel
	 *            The resourcesColumnLabel to set.
	 */
	public void setResourcesColumnLabel(String resourcesColumnLabel) {
		this.resourcesColumnLabel = resourcesColumnLabel;
	}

	/**
	 * @return Returns the list.
	 */
	public String getList() {
		return list;
	}

	/**
	 * @param list
	 *            The list to set.
	 */
	public void setList(String list) {
		this.list = list;
	}

	/**
	 * @return Returns the manageResourceListGridData.
	 */
	public String getManageResourceListGridData() {
		return manageResourceListGridData;
	}

	/**
	 * @param manageResourceListGridData
	 *            The manageResourceListGridData to set.
	 */
	public void setManageResourceListGridData(String manageResourceListGridData) {
		this.manageResourceListGridData = manageResourceListGridData;
	}

	/**
	 * @return Returns the hiddenResourceListId.
	 */
	public Integer getHiddenResourceListId() {
		return hiddenResourceListId;
	}

	/**
	 * @param hiddenResourceListId
	 *            The hiddenResourceListId to set.
	 */
	public void setHiddenResourceListId(Integer hiddenResourceListId) {
		this.hiddenResourceListId = hiddenResourceListId;
	}

	/**
	 * @return Returns the formAction.
	 */
	public String getFormAction() {
		return formAction;
	}

	/**
	 * @param formAction
	 *            The formAction to set.
	 */
	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}

	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message The message to set.
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return Returns the changeTab.
	 */
	public boolean getChangeTab() {
		return changeTab;
	}

	/**
	 * @param changeTab The changeTab to set.
	 */
	public void setChangeTab(boolean changeTab) {
		this.changeTab = changeTab;
	}

}
