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
import java.util.Date;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.business.BusinessSpace;
import net.project.business.BusinessSpaceFinder;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.model.PnResourceList;
import net.project.hibernate.model.PnResourceListHasPersons;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.IPnProjectSpaceService;
import net.project.hibernate.service.IPnResourceListHasPersonsService;
import net.project.hibernate.service.IPnResourceListService;
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
public class SelectResource_V8_5 {

	private static Logger log = Logger.getLogger(SelectResource_V8_5.class);

	@Persist
	private String pageTitle;

	@Persist
	private String businessesListLabel;

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
	private Submit search;

	@Component
	private Submit save;

	@Component
	private Form viewForm;

	@Persist
	private BusinessSpace businessSpace;

	@Persist
	private PnProjectSpace pnProjectSpace;
	
	@Inject
    private PropertyAccess access;
    
    private GenericSelectModel<BusinessSpace> businessSpaceBeans;
    
    private GenericSelectModel<PnProjectSpace> pnProjectSpaceBeans;

	private String listname;
	
	@Persist
	private PnResourceList resourceListObject;

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
	private SelectResource_V8_5 selectResource;	

	private String hiddenPersonIds;
	
	@Persist	
	private String personIdsInList; 
	
	@Persist
	private Integer[] personIds;
	
	private Integer resourceListId;
	
	private List<PnPerson> personList;
	
	private IPnProjectSpaceService pnProjectSpaceService;
	
	private List<BusinessSpace> businessesList;
	private List<PnProjectSpace> projectsList;
	
	private BusinessSpace firstBusinessSpace;
	private PnProjectSpace firstPnProjectSpace;	

	/**
	 * default constructor
	 */	
	public SelectResource_V8_5() {
		try {
			getPersonData("Default");			
			pageTitle = PropertyProvider.get("prm.resource.selectresources.pagetitle");
			businessesListLabel = PropertyProvider.get("prm.resource.selectresources.businesses.label");
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
			setPersonIdsInList("null");
		} catch (Exception e) {
			log.error("Error occured while getting SelectResource page property values.");
			e.printStackTrace();
		}		
		
		setMessage(null);		
		setResourceListObject(null);
		
		pnProjectSpaceService = ServiceFactory.getInstance().getPnProjectSpaceService();
		firstBusinessSpace = new BusinessSpace();
		firstBusinessSpace.setID("0");
		firstBusinessSpace.setName("All");		
		firstPnProjectSpace = new PnProjectSpace();
		firstPnProjectSpace.setProjectId(0);
		firstPnProjectSpace.setProjectName("All");
		businessesList = new ArrayList<BusinessSpace>();		
		bFinder = new BusinessSpaceFinder();
		projectsList = new ArrayList<PnProjectSpace>();
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
		
		// Getting database values for project list
		if(projectsList != null){
			projectsList.clear();
		}
		projectsList.add(firstPnProjectSpace);
		projectsList.addAll(pnProjectSpaceService.getProjectsByUserId(Integer.parseInt(net.project.security.SessionManager
				.getUser().getID())));		
		pnProjectSpaceBeans =  new GenericSelectModel<PnProjectSpace>(projectsList,PnProjectSpace.class,"projectName","projectId",access);
		
		if(getResourceListObject() != null){ 
			if(personList != null && getPersonIds() != null){
				String rowIndexes = "[ ";			
				
				for(PnPerson person : personList){					
					for(int counter=0; counter<getPersonIds().length; counter++){
						if(getPersonIds()[counter].equals(person.getPersonId())){
							rowIndexes += " ["+personList.indexOf(person)+"],";
							break;
						}
					}					
				}
				rowIndexes = rowIndexes.substring(0, rowIndexes.length() - 1) + " ]";
				
				if (!rowIndexes.equals("[  ]")){
					setPersonIdsInList(rowIndexes);					
				} else {
					setPersonIdsInList("null");
				}
			}			
			setListname(getResourceListObject().getName());			
		}		
	}
	
	/**
	 * Method for getting dropdown list values for project from database	 
	 * @return GenericSelectModel<PnProjectSpace>
	 */
	public GenericSelectModel<PnProjectSpace> getProjectsModel()  {
		return pnProjectSpaceBeans;
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
		pnperson = new PnPerson();
		pnperson.getPersonId();
		resourceGridData = "";
		IPnPersonService pnPersonService = ServiceFactory.getInstance().getPnPersonService();
		if (condition.equalsIgnoreCase("Default")) {			
			personList = pnPersonService.getAllPersonsIds();
		} else if (!getBusinessSpace().getID().equalsIgnoreCase("0") && getPnProjectSpace().getProjectId() != 0) {
			personList = pnPersonService.getPersonsByBusinessAndProjectId(Integer.parseInt(getBusinessSpace().getID()),
					getPnProjectSpace().getProjectId());
		} else if (!getBusinessSpace().getID().equalsIgnoreCase("0") && getPnProjectSpace().getProjectId() == 0) {
			personList = pnPersonService.getPersonsByBusinessId(Integer.parseInt(getBusinessSpace().getID()));
		} else if (getBusinessSpace().getID().equalsIgnoreCase("0") && getPnProjectSpace().getProjectId() != 0) {
			personList = pnPersonService.getPersonsByProjectId(getPnProjectSpace().getProjectId());
		} else if (getBusinessSpace().getID().equalsIgnoreCase("0") && getPnProjectSpace().getProjectId() == 0) {
			personList = pnPersonService.getPersonsByAllBusinessesAndProjects();
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
		if(getBusinessSpace() != null && getPnProjectSpace() != null){
			getPersonData("Criteria");
			selectResource.setResourceGridData(resourceGridData);		
			selectResource.setBusinessSpace(getBusinessSpace());
			selectResource.setPnProjectSpace(getPnProjectSpace());
		}
		if(getResourceListObject() != null){
			selectResource.setResourceListObject(getResourceListObject());
			selectResource.setPersonIdsInList(null);
		}
	}

	/**
	 * Method for action to perform on continue submit button 
	 * which saves new or updates edited resource list
	 */
	public void onSelectedFromSave() {
		IPnResourceListService pnResourceListService = ServiceFactory.getInstance().getPnResourceListService();
		IPnResourceListHasPersonsService pnResourceListHasPersonsService = ServiceFactory.getInstance()
			.getPnResourceListHasPersonsService();		
		
		
		// checking to update edited or save the new resource list
		if (getResourceListObject() == null){
			// Saving the resource list when not editing and returning generated resource list id
			PnResourceList pnResourceList = new PnResourceList(getListname(), Integer
					.parseInt(net.project.security.SessionManager.getUser().getID()), "A", new Date(System
					.currentTimeMillis()));
			
			resourceListId = pnResourceListService.saveResourceList(pnResourceList);
			selectResource.setMessage(PropertyProvider.get("prm.resource.selectresources.resourcelistsaved.message"));
		} else {
			// getting id of edited resource list
			resourceListId = getResourceListObject().getId();
			
			// fetching all resources from edited resource list to delete
			List<PnResourceListHasPersons> personsInResourceList = pnResourceListHasPersonsService
				.getResourcesByListId(resourceListId);
			
			// deleting resources from edited resource list
			if (personsInResourceList != null) {
				for (PnResourceListHasPersons pnResourceListHasPersons : personsInResourceList) {
					pnResourceListHasPersonsService.delete(pnResourceListHasPersons);
				}
			}
			selectResource.setMessage(PropertyProvider.get("prm.resource.selectresources.resourcelistupdated.message"));
		}
		
		// Saving the selected resources with the above generated resource list id
		// or the resources of edited resource list
		if (getHiddenPersonIds() != null) {			
			pnResourceListHasPersonsService.save(resourceListId, getHiddenPersonIds().split(","));
		}
		selectResource.setBusinessSpace(null);
		selectResource.setPnProjectSpace(null);
		selectResource.setResourceListObject(null);
		selectResource.setPersonIdsInList(null);
	}

	/**
	 * Method for calling page on successful submition of any 
	 * actions on form.
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
		setPnProjectSpace(null);
		setListname(null);
		setPersonIds(null);
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
	 * @return Returns the businessesListLabel.
	 */
	public String getBusinessesListLabel() {
		return businessesListLabel;
	}

	/**
	 * @param businessesListLabel
	 *            The businessesListLabel to set.
	 */
	public void setBusinessesListLabel(String businessesListLabel) {
		this.businessesListLabel = businessesListLabel;
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
	 * @return Returns the hiddenPersonIds.
	 */
	public String getHiddenPersonIds() {
		return hiddenPersonIds;
	}

	/**
	 * @param hiddenPersonIds
	 *            The hiddenPersonIds to set.
	 */
	public void setHiddenPersonIds(String hiddenPersonIds) {
		this.hiddenPersonIds = hiddenPersonIds;
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
	 * @return Returns the resourceListObject.
	 */
	public PnResourceList getResourceListObject() {
		return resourceListObject;
	}

	/**
	 * @param resourceListObject The resourceListObject to set.
	 */
	public void setResourceListObject(PnResourceList resourceListObject) {
		this.resourceListObject = resourceListObject;
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

	/**
	 * @return Returns the personIdsInList.
	 */
	public String getPersonIdsInList() {
		return personIdsInList;
	}

	/**
	 * @param personIdsInList The personIdsInList to set.
	 */
	public void setPersonIdsInList(String personIdsInList) {
		this.personIdsInList = personIdsInList;
	}

	/**
	 * @return Returns the personIds.
	 */
	public Integer[] getPersonIds() {
		return personIds;
	}

	/**
	 * @param personIds The personIds to set.
	 */
	public void setPersonIds(Integer[] personIds) {
		this.personIds = personIds;
	}
	
}

