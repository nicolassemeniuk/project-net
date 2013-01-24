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
package net.project.view.pages.resource.management;

import java.util.ArrayList;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.business.BusinessSpace;
import net.project.business.BusinessSpaceFinder;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Submit;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;

/**
 * @author
 */
public class SelectResource {

	private static Logger log = Logger.getLogger(SelectResource.class);

	@Persist
	private String projectsListLabel;

	@Persist
	private String personColumnLabel;

	@Persist
	private String typeColumnLabel;

	@Persist
	private String primaryRoleColumnLabel;

	@Persist
	private String saveAsResourceListTextLabel;

	@Persist
	private String saveButtonLabel;
	
	@Persist
	private String searchButtonCaption;

	@Persist
	private String businesses;

	@Persist
	private String projects;

	@Component
	private Form viewForm;
	
	@Component
	private Submit search;

	@Persist
	private BusinessSpace businessSpace;

	@Persist
	private PnProjectSpace pnProjectSpace;
	
	@Inject
    private PropertyAccess access;
    
    private GenericSelectModel<BusinessSpace> businessSpaceBeans;

	private String listname;

	@Persist
	private String resourceGridData;
	
	@Persist
	private String message;
	
	@Persist
	private String resourceListNameReqMsg;
	
	@Persist
	private String resourceSelectionReqMsg;

	private PnPerson pnperson;

	private BusinessSpaceFinder bFinder;	

	@InjectPage
	private SelectResource selectResource;	
	
	private List<PnPerson> personList;
	
	private List<BusinessSpace> businessesList;
	
	private BusinessSpace firstBusinessSpace;

	/**
	 * default constructor
	 */	
	public SelectResource() {
		try {
			getPersonData("Default");			
			projectsListLabel = PropertyProvider.get("prm.resource.selectresources.projects.label");
			personColumnLabel = PropertyProvider.get("prm.resource.selectresources.column.person.label");
			typeColumnLabel = PropertyProvider.get("prm.resource.selectresources.column.type.label");
			primaryRoleColumnLabel = PropertyProvider.get("prm.resource.selectresources.column.primaryrole.label");
			saveAsResourceListTextLabel = PropertyProvider.get("prm.resource.selectresources.saveasresourcelist.label");
			saveButtonLabel = PropertyProvider.get("prm.resource.selectresources.save.button.label");
			searchButtonCaption = PropertyProvider.get("prm.resource.selectresources.search.button.label");
			
			// Retrieving validation messages from database 
			resourceListNameReqMsg = PropertyProvider.get("prm.resource.selectresources.resourcelistnamerequired.message");
			resourceSelectionReqMsg =PropertyProvider.get("prm.resource.selectresources.resourceselectionrequired.message");
		} catch (Exception e) {
			log.error("Error occured while getting SelectResource page property values.");
			e.printStackTrace();
		}		
		firstBusinessSpace = new BusinessSpace();
		firstBusinessSpace.setID("0");
		firstBusinessSpace.setName("All");
		businessesList = new ArrayList<BusinessSpace>();		
		bFinder = new BusinessSpaceFinder();		
		initializeValues();		
	}	
	
	@SuppressWarnings("unchecked")
	@SetupRender
	void initializeValues(){
		//	Getting database values for business list		
		if(businessesList != null){
			businessesList.clear();
		}
		businessesList.add(firstBusinessSpace);		
		if (net.project.security.SessionManager.getUser() == null) {
			throw new IllegalStateException("User is null");
		}
		
		try {
			businessesList.addAll(bFinder.findByUser(net.project.security.SessionManager.getUser(), "A"));
		} catch (PersistenceException e) {
			log.error("Error occured while generating list values for Businesses model");
			e.printStackTrace();
		}		
		businessSpaceBeans = new GenericSelectModel<BusinessSpace>(businessesList,BusinessSpace.class,"name","id",access);
	}
	
	/**
	 * Method for getting dropdown list values for Businesses from database
	 * @return GenericSelectModel<BusinessSpace>
	 */	
	public GenericSelectModel<BusinessSpace> getBusinessesModel() {
		return businessSpaceBeans;
	}	
	
	/**
	 * Method to get person data to display in grid
	 */
	public void getPersonData(String condition) {
		personList = new ArrayList<PnPerson>();		
		resourceGridData = "";
		
		IPnPersonService pnPersonService = ServiceFactory.getInstance().getPnPersonService();
		if (condition.equalsIgnoreCase("Default") || getBusinessSpace().getID().equalsIgnoreCase("0")) {			
			personList = pnPersonService.getPersonsByAllBusinesses();
		} else if (!getBusinessSpace().getID().equalsIgnoreCase("0")) {
			personList = pnPersonService.getPersonsByBusinessId(Integer.parseInt(getBusinessSpace().getID()));
		} 
		resourceGridData = "[ ";
		if(personList.size() > 0) {
			int counter = personList.size() - 1;
			for (int index = 0; index < personList.size(); index++) {
				pnperson = (PnPerson) personList.get(index);
				resourceGridData += " [ " + pnperson.getPersonId() + ",'" + pnperson.getDisplayName() + "',"
						+ "'Contractor','QA']";
				if (counter != index) {
					resourceGridData += ", ";
				}
			}
			setMessage(null);
		} else {
			setMessage(PropertyProvider.get("prm.resource.selectresources.resourcesnotfound.message")); 
		}
		resourceGridData += " ]";
	}
	
	/**
	 * Method for action to perform on Search submit button
	 */
	public void onSelectedFromSearch() {
		getPersonData("Criteria");
		selectResource.setResourceGridData(resourceGridData);		
		selectResource.setBusinessSpace(getBusinessSpace());
	}

	/**
	 * Method for calling page on successful submition of action on form.
	 * 
	 * @return Object of class to forward the control
	 */
	Object onSuccessFromViewForm() {
		return selectResource;
	}
	
	@CleanupRender
	public void cleanValues(){
		setMessage(null);
		setBusinessSpace(null);
	}

	/**
	 * @return Returns the listname.
	 */
	public String getListname() {
		return listname;
	}

	/**
	 * @param listname
	 *            The listname to set.
	 */
	public void setListname(String listname) {
		this.listname = listname;
	}

	/**
	 * @return Returns the businesses.
	 */
	public String getBusinesses() {
		return businesses;
	}

	/**
	 * @param businesses
	 *            The businesses to set.
	 */
	public void setBusinesses(String businesses) {
		this.businesses = businesses;
	}

	/**
	 * @return Returns the projects.
	 */
	public String getProjects() {
		return projects;
	}

	/**
	 * @param projects
	 *            The projects to set.
	 */
	public void setProjects(String projects) {
		this.projects = projects;
	}

	/**
	 * @return Returns the resourceGridData.
	 */
	public String getResourceGridData() {
		return resourceGridData;
	}

	/**
	 * @param resourceGridData
	 *            The resourceGridData to set.
	 */
	public void setResourceGridData(String resourceGridData) {
		this.resourceGridData = resourceGridData;
	}

	/**
	 * @return Returns the saveButtonLabel.
	 */
	public String getSaveButtonLabel() {
		return saveButtonLabel;
	}

	/**
	 * @param saveButtonLabel The saveButtonLabel to set.
	 */
	public void setSaveButtonLabel(String saveButtonLabel) {
		this.saveButtonLabel = saveButtonLabel;
	}

	/**
	 * @return Returns the personColumnLabel.
	 */
	public String getPersonColumnLabel() {
		return personColumnLabel;
	}

	/**
	 * @param personColumnLabel
	 *            The personColumnLabel to set.
	 */
	public void setPersonColumnLabel(String personColumnLabel) {
		this.personColumnLabel = personColumnLabel;
	}

	/**
	 * @return Returns the primaryRoleColumnLabel.
	 */
	public String getPrimaryRoleColumnLabel() {
		return primaryRoleColumnLabel;
	}

	/**
	 * @param primaryRoleColumnLabel
	 *            The primaryRoleColumnLabel to set.
	 */
	public void setPrimaryRoleColumnLabel(String primaryRoleColumnLabel) {
		this.primaryRoleColumnLabel = primaryRoleColumnLabel;
	}

	/**
	 * @return Returns the projectsListLabel.
	 */
	public String getProjectsListLabel() {
		return projectsListLabel;
	}

	/**
	 * @param projectsListLabel
	 *            The projectsListLabel to set.
	 */
	public void setProjectsListLabel(String projectsListLabel) {
		this.projectsListLabel = projectsListLabel;
	}

	/**
	 * @return Returns the saveAsResourceListTextLabel.
	 */
	public String getSaveAsResourceListTextLabel() {
		return saveAsResourceListTextLabel;
	}

	/**
	 * @param saveAsResourceListTextLabel
	 *            The saveAsResourceListTextLabel to set.
	 */
	public void setSaveAsResourceListTextLabel(String saveAsResourceListTextLabel) {
		this.saveAsResourceListTextLabel = saveAsResourceListTextLabel;
	}

	/**
	 * @return Returns the typeColumnLabel.
	 */
	public String getTypeColumnLabel() {
		return typeColumnLabel;
	}

	/**
	 * @param typeColumnLabel
	 *            The typeColumnLabel to set.
	 */
	public void setTypeColumnLabel(String typeColumnLabel) {
		this.typeColumnLabel = typeColumnLabel;
	}

	/**
	 * @return Returns the businessSpace.
	 */
	public BusinessSpace getBusinessSpace() {
		return businessSpace;
	}

	/**
	 * @param businessSpace
	 *            The businessSpace to set.
	 */
	public void setBusinessSpace(BusinessSpace businessSpace) {
		this.businessSpace = businessSpace;
	}

	/**
	 * @return Returns the pnProjectSpace.
	 */
	public PnProjectSpace getPnProjectSpace() {
		return pnProjectSpace;
	}

	/**
	 * @param pnProjectSpace
	 *            The pnProjectSpace to set.
	 */
	public void setPnProjectSpace(PnProjectSpace pnProjectSpace) {
		this.pnProjectSpace = pnProjectSpace;
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
	 * @return Returns the resourceListNameReqMsg.
	 */
	public String getResourceListNameReqMsg() {
		return resourceListNameReqMsg;
	}

	/**
	 * @param resourceListNameReqMsg The resourceListNameReqMsg to set.
	 */
	public void setResourceListNameReqMsg(String resourceListNameReqMsg) {
		this.resourceListNameReqMsg = resourceListNameReqMsg;
	}

	/**
	 * @return Returns the resourceSelectionReqMsg.
	 */
	public String getResourceSelectionReqMsg() {
		return resourceSelectionReqMsg;
	}

	/**
	 * @param resourceSelectionReqMsg The resourceSelectionReqMsg to set.
	 */
	public void setResourceSelectionReqMsg(String resourceSelectionReqMsg) {
		this.resourceSelectionReqMsg = resourceSelectionReqMsg;
	}

	/**
	 * @return Returns the searchButtonCaption.
	 */
	public String getSearchButtonCaption() {
		return searchButtonCaption;
	}

	/**
	 * @param searchButtonCaption The searchButtonCaption to set.
	 */
	public void setSearchButtonCaption(String searchButtonCaption) {
		this.searchButtonCaption = searchButtonCaption;
	}
	
}

