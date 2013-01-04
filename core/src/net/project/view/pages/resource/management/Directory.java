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
import java.util.Iterator;
import java.util.List;

import net.project.admin.RegistrationBean;
import net.project.admin.RegistrationException;
import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.business.BusinessSpace;
import net.project.business.BusinessSpaceFinder;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.IPnProjectSpaceService;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpace;
import net.project.resource.Person;
import net.project.resource.PersonStatus;
import net.project.resource.Roster;
import net.project.resource.SpaceInvitationException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.security.group.Group;
import net.project.security.group.GroupCollection;
import net.project.space.PersonalSpace;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.util.TextFormatter;
import net.project.util.Version;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.util.TextStreamResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Directory {

	@ApplicationState
	private String jSPRootURL;
	
	@Persist
	private String versionNumber;
	
	private static Logger log;

	private String message;

	private IPnPersonService pnPersonService;

	private DirectoryService directoryService;

	private IPnProjectSpaceService pnProjectSpaceService;

	private BusinessSpaceFinder businessFinder;

	private PnPerson person;

	private String resourceList;

	private String businessComboData;

	private String projectComboData;

	private String businessGridData;

	private String projectGridData;
	
	private String directoryListData;

	@Persist
	private List<BusinessSpace> personBusinessesList;

	@Persist
	private List<PnProjectSpace> personProjectList;
	
	private List<BusinessSpace> allBusinesses;

	private List<PnProjectSpace> allProjects;
	
	private List<BusinessSpace> addedBusinesses;

	private List<PnProjectSpace> addedProjects;
	
	private Integer firstSelectedPersonId;

	@Persist
	private Person rosterPerson;
	
	private static final String FIXED_CONFIG_OF_SPACE_TREE_DATA = "uiProvider : 'col', cls : 'master-task'";

	public Directory() {
		log = Logger.getLogger(Directory.class);
		versionNumber = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
		pnPersonService = ServiceFactory.getInstance().getPnPersonService();
		pnProjectSpaceService = ServiceFactory.getInstance().getPnProjectSpaceService();
		businessFinder = new BusinessSpaceFinder();
		directoryService = new DirectoryService();
		addedBusinesses = new ArrayList<BusinessSpace>();
		addedProjects = new ArrayList<PnProjectSpace>();
	}

	@SetupRender
	void setValues() {
		getResourceTreeData();
		getDirectoryListDataByUser();
		loadPersonSpace(firstSelectedPersonId);
		getPersonDetail(""+firstSelectedPersonId);
	}

	private void getResourceTreeData() {
		resourceList = "[";
		List<PnPerson> persons = pnPersonService.getAllPersonsIds();
		for (PnPerson person : persons) {
			if (!resourceList.equals("[")) {
				resourceList += ",";
			} else{
				firstSelectedPersonId = person.getPersonId();
			}
			resourceList += "['" + person.getPersonId() + "','" + person.getDisplayName().replaceAll("'", "&acute;") + "']";
		}
		resourceList += "]";
	}

	private void getPersonDetail(String personId) {
		rosterPerson = directoryService.getPersonDetail(personId);
	}

	private void getBusinessGridByPersonId(String personId) {
		try {
			personBusinessesList = directoryService.getBusinessesByPersonId(personId, "A");
		} catch (PersistenceException pnetEx) {
			log.error("Error occured while getting person business list.");
		}
		/*businessGridData = "[";
		for (BusinessSpace business : personBusinessesList) {
			if (!businessGridData.equals("[")) {
				businessGridData += ",";
			}
			businessGridData += "['" + business.getID() + "','" + business.getName().replaceAll("'", "&acute;") + "','" 
					+ replaceNull(business.getFlavor(), "")+ "','" + business.getNumProjects() + "','" + business.getNumMembers() + "']";
		}
		businessGridData += "]";
        */
	}

	private void getProjectGridByPersonId(Integer personId) {

		personProjectList = pnProjectSpaceService.getProjectsByUserId(personId);
		/*projectGridData = "[";
		for (PnProjectSpace project : personProjectList) {
			if (!projectGridData.equals("[")) {
				projectGridData += ",";
			}
			projectGridData += "['" + project.getProjectId() + "','" + project.getProjectName().replaceAll("'", "&acute;") + "','"
					+ replaceNull("" + project.getPercentComplete(), "0") + "']";
		}
		projectGridData += "]";*/
	}

	private void getBusinessComboDataByPerson(String personId) {
		List<BusinessSpace> businesses = null;
		try {
			businesses = businessFinder.findAll("A");
		} catch (PersistenceException pnetEx) {
			log.error("Error occured while getting business list.");
		}

		if (personBusinessesList != null && personBusinessesList.size() > 0) {
			for (BusinessSpace personBusiness : personBusinessesList) {
				if (businesses != null && businesses.size() > 0) {
					businesses.remove(personBusiness);
				}
			}
		}

		businessComboData = "[";
		for (BusinessSpace business : businesses) {
			if (!businessComboData.equals("[")) {
				businessComboData += ", ";
			}
			businessComboData += "['" + business.getID() + "','" + business.getName().replaceAll("'", "&acute;") + "']";
		}
		businessComboData += "]";
	}

	private void getProjectComboDataByPerson(Integer personId) {
			
		List<PnProjectSpace> projects = pnProjectSpaceService.getAllProjects();

		if (personProjectList != null && personProjectList.size() > 0) {
			for (PnProjectSpace personProject : personProjectList) {
				if (projects != null && projects.size() > 0) {
					projects.remove(personProject);
				}
			}
		}

		projectComboData = "[";
		for (PnProjectSpace project : projects) {
			if (!projectComboData.equals("[")) {
				projectComboData += ",";
			}
			projectComboData += "['" + project.getProjectId() + "','" + project.getProjectName().replaceAll("'", "&acute;") + "']";
		}
		projectComboData += "]";
	}
	
	private void getDirectoryListDataByUser(){
		List<BusinessSpace> businesses = null;
		
		try {
			businesses = directoryService.getBusinessesByPersonId(net.project.security.SessionManager.getUser().getID(), "A");
		} catch (PersistenceException pnetEx) {
			log.error("Error occured while getting person business list.");
		}
		
		directoryListData = "[ ['0', 'Show all resources..']";
		for (BusinessSpace business : businesses) {
			directoryListData += ", ['"+business.getID()+"', '" + TextFormatter.truncateString(business.getName(), 25).replaceAll("'", "&acute;") +"']";
		}
		directoryListData += "]";
	}
	
	public TextStreamResponse onActivate(String params) {
		String[] param = params.split("&");
		String responseString = "";
		
		if (param[1].equalsIgnoreCase("getResourceProperty")) {
			responseString = getResourceProperty(param[0]);
			
		} else if (param[1].equalsIgnoreCase("addBusiness")) {
			String []id = param[0].split(",");
			
			addBusiness(id[0], id[1]);
			if(message.equals("")){
				getBusinessGridByPersonId(rosterPerson.getID());
				getBusinessComboDataByPerson(rosterPerson.getID());
				responseString = businessGridData + "|&|" + businessComboData;
			}else{
				responseString = message;
			}
			
		} else if (param[1].equalsIgnoreCase("addBusinessAndProject")) {
			String[] id = param[0].split(",");

			addBusiness(id[0], id[1]);
			if (message.equals("")) {
				addProejctsUnderBusiness(id[0], "0");// Resource role will be 'Team Member' for all project
				getBusinessGridByPersonId(rosterPerson.getID());
				getProjectGridByPersonId(Integer.parseInt(rosterPerson.getID()));
				getBusinessComboDataByPerson(rosterPerson.getID());
				getProjectComboDataByPerson(Integer.parseInt(rosterPerson.getID()));

				responseString = businessGridData + "|&|" + businessComboData + "|&|" + projectGridData + "|&|"
						+ projectComboData + "|&|" + message;
			} else {
				responseString = message;
			}
			
		} else if (param[1].equalsIgnoreCase("deleteBusiness")) {
			deleteBusiness(param[0]);
			getBusinessGridByPersonId(rosterPerson.getID());
			getBusinessComboDataByPerson(rosterPerson.getID());
			
			responseString = businessGridData + "|&|" + businessComboData + "|&|" + message;
			
		} else if (param[1].equalsIgnoreCase("addProject")) {
			String []id = param[0].split(",");
			message = "";	
			
			addProject(id[0], id[1]);
			if (message.equals("")) {
				getProjectGridByPersonId(Integer.parseInt(rosterPerson.getID()));
				getProjectComboDataByPerson(Integer.parseInt(rosterPerson.getID()));

				responseString = projectGridData + "|&|" + projectComboData;
			} else {
				responseString = "Sorry! you can not assign reosurce to this project. <br> You are not space admin for this project.";
			}
			
		} else if (param[1].equalsIgnoreCase("deleteProject")) {
			deleteProject(param[0]);
			getProjectGridByPersonId(Integer.parseInt(rosterPerson.getID()));
			getProjectComboDataByPerson(Integer.parseInt(rosterPerson.getID()));
			
			responseString = projectGridData + "|&|" + projectComboData + "|&|" + message;
			
		} else if(param[1].equalsIgnoreCase("getBusinessRoleComboData")){
			responseString = directoryService.getRoleOptionList(new BusinessSpace(param[0]), 
					SessionManager.getUser());
			
		} else if(param[1].equalsIgnoreCase("getProjectRoleComboData")){
			responseString = directoryService.getRoleOptionList(new ProjectSpace(param[0]), 
					SessionManager.getUser());
			
		} else if (param[1].equalsIgnoreCase("updateResourceProperty")){
			updateResourceProperty(param[0]);
			
			if(replaceNull(message, "").equals("")){
				responseString = rosterPerson.getID() + "|&|" + getResourceDetailString(rosterPerson.getID());
			}else{
				responseString = message;	
			}
			
		} else if (param[1].equalsIgnoreCase("addNewResource")){
			String personId = addNewResource(param[0]);
			
			if(personId != null){
				responseString = personId + "|&|" + getResourceProperty(personId);
			}else{
				responseString = message;
			}
		} else if (param[1].equalsIgnoreCase("getDirctoryResource")){
			if(!param[0].equals("0")){
				getResourceTreeDataByDirectory(param[0]);
			} else{
				getResourceTreeData();
			}
			responseString = resourceList;
		} else if (param[1].equalsIgnoreCase("getSpaceData")){
			responseString = getSpaceTreeData();
		} else if (param[1].equalsIgnoreCase("saveSpaceChanges")){
			responseString = saveSpaceChanges(param[0]);
		}

		return new TextStreamResponse("text/plain", responseString);
	}

	private String getResourceProperty(String personId) {

		String resourcePropertyString = getResourceDetailString(personId);
		
		getBusinessGridByPersonId(personId);
		getProjectGridByPersonId(Integer.parseInt(personId));
		
        //Old code no need to concat these data.
        //getBusinessComboDataByPerson(personId);
		//getProjectComboDataByPerson(Integer.parseInt(personId));
		//resourcePropertyString += "|&|" + businessGridData;
		//resourcePropertyString += "|&|" + projectGridData;
		//resourcePropertyString += "|&|" + businessComboData;
		//resourcePropertyString += "|&|" + projectComboData;

		return resourcePropertyString;
	}
	
	private String getResourceDetailString(String personId){
		String resourceDetailString = "";
		getPersonDetail(personId);
		
		if (rosterPerson != null) {
			resourceDetailString = replaceNull(rosterPerson.getDisplayName(),"")
					+ "|&|" + replaceNull(rosterPerson.getFirstName(),"") 
					+ "|&|" + replaceNull(rosterPerson.getLastName(), "")
					+ "|&|" + replaceNull(rosterPerson.getAddress().getOfficePhone(), "") 
					+ "|&|" + replaceNull(rosterPerson.getAddress().getMobilePhone(), "") 
					+ "|&|"	+ replaceNull(rosterPerson.getAddress().getFaxPhone(), "") 
					+ "|&|"	+ replaceNull(rosterPerson.getEmail(), "")
					+ "|&|"	+ replaceNull(rosterPerson.getStatus().getID(), "");
			
		} else {
			resourceDetailString = "  |&|  |&|  |&|  |&|  |&|  |&|  |&|";
		}
		
		return resourceDetailString;
	}

	private void addBusiness(String businessId, String roleId) {
		message = ""; 
		
		if(roleId.equals("0")){
			roleId = null;
		}
		
		Space businessSpace = new BusinessSpace(businessId);
		try {
			SessionManager.getUser().setCurrentSpace(businessSpace);
			if(SessionManager.getUser().isSpaceAdministrator()){
				directoryService.addSpace(businessSpace, rosterPerson, SessionManager.getUser()
						, "" , roleId);
			}else{
				message = "Sorry! you can not assign resource to this business. <br> You do not have permission to administrate this business";
			}

		} catch (SpaceInvitationException pnetEx) {
			log.error("Invitation error occured while inviting user in business space.");
		} catch (PersistenceException pnetEx) {
			log.error("Databse error occured while inviting user in business space.");
		} catch (PnetException pnetEx) {
			log.error("error occured while setting business space as current space to user.");
		} finally {
			try {
				SessionManager.getUser().setCurrentSpace(new PersonalSpace(SessionManager.getUser()));
			} catch (PnetException pnetEx) {
				log.error("error occured while setting personal space.");
			}
		}
	}
	
	private void addProejctsUnderBusiness(String businessId, String roleId){
		message = "";
		
		List<PnProjectSpace> projects = pnProjectSpaceService.getProjectsByBusinessId(Integer.parseInt(businessId));
		if(projects != null && projects.size() >0 ){
			for (PnProjectSpace project : projects) {
				addProject(""+project.getProjectId(), roleId);
			}
		}
		if(!message.equals("")){
			message = "You can not assign resource to project(s): "+ message.substring(0, message.length()-1)
						+". <br> You do not have permission to administrate this project";
		}
	}

	private void deleteBusiness(String strBusinessIds) {
		String[] businessIds = strBusinessIds.split(",");
		message = "";
		String undeletedBusinsess = "";
		Roster roster = null;
		for (String businessId : businessIds ) {
			try {
				Space businessSpace = new BusinessSpace(businessId);
				SessionManager.getUser().setCurrentSpace(businessSpace);
				if (SessionManager.getUser().isSpaceAdministrator()) {
					roster = new Roster(businessSpace);
					roster.removePerson(rosterPerson.getID());
				} else {
					businessSpace.load();
					undeletedBusinsess += businessSpace.getName() + ", ";
				}
				if(!undeletedBusinsess.equals("")){
					message = "Sorry! You can not remove resource from "
						+ undeletedBusinsess.substring(0, undeletedBusinsess.length() - 2)
						+ " businss(s) <br> You do not have permission to administrate this busienss(s)";
				}
				
			} catch (PersistenceException pnetEx) {
				message = pnetEx.getMessage();
				log.error("Error occured while removing user form business space.");
			} catch (PnetException pnetEx) {
				log.error("error occured while setting business space as current space to user.");
			} finally {
				try {
					SessionManager.getUser().setCurrentSpace(new PersonalSpace(SessionManager.getUser()));
				} catch (PnetException pnetEx) {
					log.error("error occured while setting personal space.");
				}
			}
		}

	}

	private void addProject(String projectId, String roleId) {
		if(roleId.equals("0")){
			roleId = null;
		}
		
		Space projectSpace = new ProjectSpace(projectId);
		try {
			SessionManager.getUser().setCurrentSpace(projectSpace);
			if (SessionManager.getUser().isSpaceAdministrator()) {
				directoryService.addSpace(projectSpace, rosterPerson, SessionManager.getUser(), "", roleId);
			} else {
				projectSpace.load();
				message = projectSpace.getName()+", ";	
			}

		} catch (SpaceInvitationException pnetEx) {
			log.error("Invitation error occured while inviting user in project space.");
		} catch (PersistenceException pnetEx) {
			log.error("Database Error occured while inviting user in project space.");
		} catch (PnetException pnetEx) {
			log.error("error occured while setting project space as current space to user.");
		} finally {
			try {
				SessionManager.getUser().setCurrentSpace(new PersonalSpace(SessionManager.getUser()));
			} catch (PnetException pnetEx) {
				log.error("error occured while setting personal space.");
			}
		}
	}

	private void deleteProject(String strProjectIds) {
		String[] projectIds = strProjectIds.split(",");
		message = "";
		String undeletedProject = "";
		Roster roster = null;
		
		for (String projectId : projectIds) {
			try {
				Space proejctSpace = new ProjectSpace(projectId);
				SessionManager.getUser().setCurrentSpace(proejctSpace);
				if(SessionManager.getUser().isSpaceAdministrator()){
					roster = new Roster(proejctSpace);
					roster.removePerson(rosterPerson.getID());
				}else{
					proejctSpace.load();
					undeletedProject += proejctSpace.getName()+", ";
				}
				if(!undeletedProject.equals("")){
					message = "Sorry! You can not remove resource from " 
						+ undeletedProject.substring(0, undeletedProject.length()-2) 
							+" project(s) <br> You do not have permission to administrate this project.";
				}
			} catch (PersistenceException pnetEx) {
				message = pnetEx.getMessage();
				log.error("Error occured while removing user from project space.");
			} catch (PnetException pnetEx) {
				log.error("error occured while setting project space as current space to user.");
			} finally {
				try {
					SessionManager.getUser().setCurrentSpace(new PersonalSpace(SessionManager.getUser()));
				} catch (PnetException pnetEx) {
					log.error("error occured while setting personal space.");
				}
			}
		}
	}
	
	private void updateResourceProperty(String userProperty) {

		try {
			RegistrationBean registration = new RegistrationBean();
			registration.setID(rosterPerson.getID());
			registration.setUpdating(true);
			registration.load();

			JSONArray jsArray = new JSONArray(userProperty);
			JSONObject jsObject = jsArray.getJSONObject(jsArray.length() - 1);

			if (!registration.checkEmailExists(jsObject.getString("email"))) {
				registration.setDisplayName(replaceNull(jsObject.getString("displayName"), ""));
				registration.setFirstName(replaceNull(jsObject.getString("firstName"), ""));
				registration.setLastName(replaceNull(jsObject.getString("lastName"), ""));
				registration.setOfficePhone(replaceNull(jsObject.getString("officePhone"), ""));
				registration.setMobilePhone(replaceNull(jsObject.getString("mobile"), ""));
				registration.setFaxPhone(replaceNull(jsObject.getString("fax"), ""));
				registration.setEmail(replaceNull(jsObject.getString("email"), ""));

				registration.updateInvitedUser();

				User user = SessionManager.getUser();
				if (rosterPerson.getID().equals(user.getID())) {
					user.setEmail(registration.getEmail());
					user.load();
				}
			} else {
				message = "Email address already exist";
			}

		} catch (JSONException pnetEx) {
			log.error("Error occured while parsing JSON object.");
		} catch (RegistrationException pnetEx) {
			log.error("Error occured while updating user.");
		} catch (PersistenceException pnetEx) {
			log.error("Data base error occured while updating user.");
		}
	}
	
	private String addNewResource(String newResourceProperty) {
		String personId = null;
		try {
			JSONArray jsArray = new JSONArray(newResourceProperty);
			JSONObject jsObject = jsArray.getJSONObject(jsArray.length() - 1);
			if (!new RegistrationBean().checkEmailExists(jsObject.getString("email"))) {
				Person newResource = new Person();
				newResource.setFirstName(replaceNull(jsObject.getString("firstName"), ""));
				newResource.setLastName(replaceNull(jsObject.getString("lastName"), ""));
				newResource.setEmail(replaceNull(jsObject.getString("email"), ""));
				
				// this will always be unregistered because no one will create a user
				// unless they are not already registered
				newResource.setStatus(PersonStatus.UNREGISTERED);
				newResource.createStub();
				
				personId = newResource.getID(); 
			} else {
				message = "Email address already exist";
			}
		} catch (PersistenceException pnetEx) {
			log.error("Database error occured creating user.");
		} catch (JSONException pnetEx) {
			log.error("Error occured while parsing JSON object.");
		}
		return personId;
	}
	
	private void getResourceTreeDataByDirectory(String directoryId) {
		resourceList = "[";
		List<PnPerson> persons = pnPersonService.getPersonsByBusinessId(Integer.parseInt(directoryId), "displayName");
		for (PnPerson person : persons) {
			if (!resourceList.equals("[")) {
				resourceList += ",";
			} 
			resourceList += "['" + person.getPersonId() + "','" + person.getDisplayName().replaceAll("'", "&acute;") + "']";
		}
		resourceList += "]";
	}

	private String getSpaceTreeData(){
		String spaceTreeData = "[";
		allBusinesses = null;
		allProjects = null;
		if(addedProjects != null){
			addedProjects.clear();
		}
		
		try {
			if (SessionManager.getUser().isApplicationAdministrator()
					|| PropertyProvider.get("prm.resource.directory.allspacedisplaypermission").equals("1")) {
				allBusinesses = businessFinder.findAll("A");
				allProjects = pnProjectSpaceService.getAllProjects();
			} else {
				allBusinesses = businessFinder.findByUser(SessionManager.getUser(), "A");
				allProjects = pnProjectSpaceService.getProjectsByUserId(Integer.parseInt(SessionManager.getUser()
						.getID()));
			}
		} catch (Exception pnetEx) {
			pnetEx.printStackTrace();
			log.error("Error occured while getting business list Directory:getSpaceTreeData().");
		}
		
		
		for(BusinessSpace business : allBusinesses){
			if (addedBusinesses.indexOf(business) == -1) {
				if (!spaceTreeData.equals("[")) {
					spaceTreeData += ",";
				}
				spaceTreeData += "{" + getBaasicNodeConfig(business.getID(), business.getName());
				spaceTreeData +=  ", iconCls:'business'";
				String subSpaceData = getSubSpace(business.getID());
				if (!subSpaceData.equals("")) {
					spaceTreeData += ", children : [" + subSpaceData + "]";
				} else {
					spaceTreeData += ", leaf : true ";
				}
				spaceTreeData += "}";
			}
		}

		if(addedProjects != null){
			allProjects.removeAll(addedProjects);
			addedProjects.clear();
		}
		
		if (allProjects != null && allProjects.size() > 0) {
			
			if(!spaceTreeData.equals("[")){
				spaceTreeData += ",";
			}
			
			spaceTreeData += "{ spaceId : '', spaceName : '<font color=\"blue\">Other Projects </font>',"
							+ FIXED_CONFIG_OF_SPACE_TREE_DATA + ", children : [";
			for (PnProjectSpace project : allProjects) {
				if (addedProjects.indexOf(project) == -1) {
					spaceTreeData += "{" + getBaasicNodeConfig(""+project.getProjectId(), project.getProjectName());
					spaceTreeData +=  ", iconCls:'project'";
					String subSpaceData = getSubSpace("" + project.getProjectId());
					if (!subSpaceData.equals("")) {
						spaceTreeData += ", children : [" + subSpaceData + "]";
					} else {
						spaceTreeData += ", leaf : true ";
					}
					spaceTreeData += "},";
				}
			}
			spaceTreeData = spaceTreeData.substring(0, spaceTreeData.length() - 1);
			spaceTreeData += "]}";
		}
	
		spaceTreeData += " ]";
		
		return spaceTreeData;

	}
	
	private String getSubSpace(String spaceId ){
		String childData = "";
		List<BusinessSpace> subBusinesses = null;
		List<PnProjectSpace> projects = null;
		List<PnProjectSpace> subProjects = null;
		
		try {
			subBusinesses = directoryService.getSubBusinessByBusinessId(spaceId);
		} catch (PersistenceException pnetEx) {
			log.error("Error occured while getting subbusiness list Directory:getSubSpace().");
		}
		if(subBusinesses != null){
			addedBusinesses.addAll(subBusinesses);
		}
		
		projects = pnProjectSpaceService.getProjectsByBusinessId(Integer.parseInt(spaceId));
		if(projects != null){
			addedProjects.addAll(projects);
		}
		
		subProjects = pnProjectSpaceService.getSubProjectsByProejctId(Integer.parseInt(spaceId));
		if(subProjects!=null){
			addedProjects.addAll(subProjects);
		}
		
		if (subBusinesses != null && subBusinesses.size() > 0) {
			for (BusinessSpace business : subBusinesses) {
				if (allBusinesses.indexOf(business) != -1) {
					if (!childData.equals("")) {
						childData += ",";
					}
					childData += "{" + getBaasicNodeConfig(business.getID(), business.getName());
					childData += ", iconCls:'business'";
					String subSpaceData = getSubSpace(business.getID());
					if (!subSpaceData.equals("")) {
						childData += ", children : [" + subSpaceData + "]";
					} else {
						childData += ", leaf : true ";
					}
					childData += "}";
				}
			}
		}
		
		if (projects != null && projects.size() > 0) {
			for (PnProjectSpace project : projects) {
				if (allProjects.indexOf(project) != -1) {
					if (!childData.equals("")) {
						childData += ",";
					}
					childData += "{" + getBaasicNodeConfig("" + project.getProjectId(), project.getProjectName());
					childData += ", iconCls:'project'";
					String subSpaceData = getSubSpace("" + project.getProjectId());
					if (!subSpaceData.equals("")) {
						childData += " , children : [" + subSpaceData + "]";
					} else {
						childData += " , leaf : true ";
					}
					childData += "}";
				}
			}
		}
		
		if (subProjects != null && subProjects.size() > 0) {
			for (PnProjectSpace project : subProjects) {
				if (allProjects.indexOf(project) != -1) {
					if (!childData.equals("")) {
						childData += ",";
					}
					childData += "{" + getBaasicNodeConfig("" + project.getProjectId(), project.getProjectName());
					childData += ", iconCls:'project'";
					String subSpaceData = getSubSpace("" + project.getProjectId());
					if (!subSpaceData.equals("")) {
						childData += " , children : [" + subSpaceData + "]";
					} else {
						childData += " , leaf : true ";
					}
					childData += "}";
				}
			}
		}
		
		return childData;
	}
	
	private String getBaasicNodeConfig(String spaceId, String spaceName ){
		
		String configString = "spaceId : '" + spaceId
						+"', spaceName : '"+ spaceName.replaceAll("'", "&acute;");
		
		if(personIsMemberOfThisSpace(spaceId)){
			configString += "', role : '"+ getPersonRolesBySpaceID(spaceId)
						+"', checked : true";
		}else{
			configString += "', role : '"
						+"', checked : false";
		}
		
		configString += ", " + FIXED_CONFIG_OF_SPACE_TREE_DATA;
		
		return configString;
	}
	
	private String getPersonRolesBySpaceID(String spaceId) {
		String roleString = "";
		GroupCollection groupList = new GroupCollection();
		try {
			groupList.setSpace(SpaceFactory.constructSpaceFromID(spaceId));
			groupList.loadAll(rosterPerson.getID());
			for (Iterator it = groupList.iterator(); it.hasNext();) {
				Group group = (Group) it.next();
				if(!group.getName().equals(rosterPerson.getDisplayName()) && !roleString.contains(group.getName())){
					roleString += group.getName() + ", ";
				}
			}
		} catch (PersistenceException pnetEx) {
			pnetEx.printStackTrace();
		}
		return (roleString.equals("") ? "" : roleString.substring(0, roleString.length() - 2));
	}
	
	private String saveSpaceChanges(String spaces) {
		JSONArray jsArray;
		Space space;
		String saveMessageString = "";
		String problemWithBusienss = "";
		String problemWithProject = "";
		
		try {
			jsArray = new JSONArray(spaces);
			JSONObject jsObject = jsArray.getJSONObject(jsArray.length() - 1);

			if (spaces.contains("spacesToAdd")) {
				String[] spacesToAdd = jsObject.getString("spacesToAdd").substring(1,
						jsObject.getString("spacesToAdd").length() - 1).split(",");
				// inviting resource to space
				for (String spaceId : spacesToAdd) {
					space = SpaceFactory.constructSpaceFromID(spaceId);
					SessionManager.getUser().setCurrentSpace(space);
					if(SessionManager.getUser().isSpaceAdministrator()){
						directoryService.addSpace(space, rosterPerson, SessionManager.getUser(), "", null);
					} else{
						space.load();
						if(space.getType().equalsIgnoreCase("business")){
							problemWithBusienss += space.getName()+", ";
						}else{
							problemWithProject += space.getName()+", ";
						}
					}
				}
			}

			if (spaces.contains("spacesToRemove")) {
				Roster roster = null;
				String[] spacesToRemove = jsObject.getString("spacesToRemove").substring(1,
						jsObject.getString("spacesToRemove").length() - 1).split(",");
				// removing resoruce from space
				for (String spaceId : spacesToRemove) {
					space = SpaceFactory.constructSpaceFromID(spaceId);
					SessionManager.getUser().setCurrentSpace(space);
					if (SessionManager.getUser().isSpaceAdministrator()) {
						roster = new Roster(space);
						roster.removePerson(rosterPerson.getID());
					} else {
						space.load();
						if(space.getType().equalsIgnoreCase("business")){
							problemWithBusienss += space.getName()+", ";
						}else{
							problemWithProject += space.getName()+", ";
						}	
					}
				}
			}
			
			loadPersonSpace(Integer.parseInt(rosterPerson.getID()));

		} catch (JSONException pnetEx) {
			pnetEx.printStackTrace();
			log.error("Error occured while parsing jason object Directory:saveSpaceChanges().");
		} catch (PersistenceException pnetEx) {
			saveMessageString = pnetEx.getMessage();
			pnetEx.printStackTrace();
			log.error("Error occured while constructing space by id Directory:saveSpaceChanges().");
		} catch (SpaceInvitationException pnetEx) {
			pnetEx.printStackTrace();
			log.error("Databse error occured while inviting/removing user Directory:saveSpaceChanges().");
		} catch (PnetException pnetEx) {
			pnetEx.printStackTrace();
			log.error("error occured while setting space as current space to user Directory:saveSpaceChanges().");
		} finally {
			try {
				SessionManager.getUser().setCurrentSpace(new PersonalSpace(SessionManager.getUser()));
			} catch (PnetException pnetEx) {
				log.error("error occured while setting personal space.");
			}
		}
		if (!problemWithBusienss.equals("")	|| !problemWithProject.equals("")) {
			saveMessageString = "You do not have permission to administrate,";
			if(!problemWithBusienss.equals("")){
				saveMessageString += "<br><b>Business(s):</b> "+problemWithBusienss.substring(0, problemWithBusienss.length()-2) + ".";
			}
			if(!problemWithProject.equals("")){
				saveMessageString += "<br><b>Project(s):</b> "+problemWithProject.substring(0, problemWithProject.length()-2) + ".";
			}
			
		}
		return saveMessageString;
	}
	
	private void loadPersonSpace(Integer personId){
		personProjectList = pnProjectSpaceService.getProjectsByUserId(personId);
		try {
			personBusinessesList = directoryService.getBusinessesByPersonId(""+personId, "A");
		} catch (PersistenceException pnetEx) {
			log.error("Error occured while getting person business list.");
		}
	}

	private String replaceNull(String string, String returnString) {
		return ((string == null || string.equals("null")) ? returnString : string);
	}
	
	private boolean personIsMemberOfThisSpace(String spaceId){
		boolean personInSpace = false;
		if (CollectionUtils.isNotEmpty(personBusinessesList)) {
			for(BusinessSpace business : personBusinessesList){
				personInSpace = business.getID().equals(spaceId);
				if(personInSpace) break;
			}
		}
		if(!personInSpace && CollectionUtils.isNotEmpty(personProjectList)){
			for(PnProjectSpace project : personProjectList){
				personInSpace = project.getProjectId() == Integer.parseInt(spaceId);
				if(personInSpace) break;
			}
		}
		return personInSpace; 
	}

	@CleanupRender
	void cleanValues() {
		message = null;
	}

	/**
	 * @return the resourceList
	 */
	public String getResourceList() {
		return resourceList;
	}

	/**
	 * @param resourceList
	 *            the resourceList to set
	 */
	public void setResourceList(String resourceList) {
		this.resourceList = resourceList;
	}

	/**
	 * @return the jSPRootURL
	 */
	public String getJSPRootURL() {
		return jSPRootURL;
	}

	public void setMessage(String msg) {
		message = msg;
	}

	public String getMessage() {
		return message;
	}

	/**
	 * @return the person
	 */
	public PnPerson getPerson() {
		return person;
	}

	/**
	 * @param person
	 *            the person to set
	 */
	public void setPerson(PnPerson person) {
		this.person = person;
	}

	/**
	 * @return the rosterPerson
	 */
	public net.project.resource.Person getRosterPerson() {
		return rosterPerson;
	}

	/**
	 * @param rosterPerson
	 *            the rosterPerson to set
	 */
	public void setRosterPerson(net.project.resource.Person rosterPerson) {
		this.rosterPerson = rosterPerson;
	}

	/**
	 * @return the businessGridData
	 */
	public String getBusinessGridData() {
		return businessGridData;
	}

	/**
	 * @param businessGridData
	 *            the businessGridData to set
	 */
	public void setBusinessGridData(String businessGridData) {
		this.businessGridData = businessGridData;
	}

	/**
	 * @return the projectGridData
	 */
	public String getProjectGridData() {
		return projectGridData;
	}

	/**
	 * @param projectGridData
	 *            the projectGridData to set
	 */
	public void setProjectGridData(String projectGridData) {
		this.projectGridData = projectGridData;
	}

	/**
	 * @return the businessComboData
	 */
	public String getBusinessComboData() {
		return businessComboData;
	}

	/**
	 * @param businessComboData
	 *            the businessComboData to set
	 */
	public void setBusinessComboData(String businessComboData) {
		this.businessComboData = businessComboData;
	}

	/**
	 * @return the projectComboData
	 */
	public String getProjectComboData() {
		return projectComboData;
	}

	/**
	 * @param projectComboData
	 *            the projectComboData to set
	 */
	public void setProjectComboData(String projectComboData) {
		this.projectComboData = projectComboData;
	}

	/**
	 * @return the versionNumber
	 */
	public String getVersionNumber() {
		return versionNumber;
	}

	/**
	 * @param versionNumber the versionNumber to set
	 */
	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	/**
	 * @return the directoryListData
	 */
	public String getDirectoryListData() {
		return directoryListData;
	}

	/**
	 * @param directoryListData the directoryListData to set
	 */
	public void setDirectoryListData(String directoryListData) {
		this.directoryListData = directoryListData;
	}

}
