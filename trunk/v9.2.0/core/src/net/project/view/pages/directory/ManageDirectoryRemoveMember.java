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
package net.project.view.pages.directory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.base.EventException;
import net.project.base.EventFactory;
import net.project.base.ObjectType;
import net.project.base.directory.search.ISearchResults;
import net.project.base.property.PropertyProvider;
import net.project.business.BusinessDirectorySearchResults;
import net.project.directory.ManageDirectoryHelper;
import net.project.directory.ProjectListWrapper;
import net.project.events.EventType;
import net.project.hibernate.model.PnBusiness;
import net.project.persistence.PersistenceException;
import net.project.project.DomainListBean;
import net.project.project.ProjectSpace;
import net.project.resource.Person;
import net.project.resource.Roster;
import net.project.resource.SpaceInvitationManager;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.space.SpaceTypes;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

/**
 * For remove member page in manage directory
 */
public class ManageDirectoryRemoveMember extends BasePage {
	
	private static Logger log;
	
	@Persist
	@Property 
	private String errorMessage;
	
	@Property
	@Persist
	private boolean isSearchMemberFound;
	
	@Property
	@Persist
	private ArrayList<ProjectListWrapper> selectedProjects;
	
	@Property
	private ProjectListWrapper projectListWrapper;
	
	@Property
	private BusinessDirectorySearchResults.BusinessDirectorySearchResult personWrapper;
	
	@Property
	private BusinessDirectorySearchResults.BusinessDirectorySearchResult memberWrapper;
	
	@Property
	@Persist
	private List<BusinessDirectorySearchResults.BusinessDirectorySearchResult> personWrappers;
	
	@Property
	private String deleteProjectTooltip;
	
	@Property
	private String deleteBusinessTooltip;
	
	@Persist
    private String currentSpaceId;
	
	@Property	
	@Persist
	private String directoryId;
	
	@Property
	@Persist
	private String searchUser;
	
	@Property
	private Roster teamMembers;
	
	@Property
	private String defaultProjectValue;
	
	@Property 
	private String removeProjectId;
	
	@Property
	@Persist
	private String projectOrSubbusinessId;
	
	@Property
	@Persist
	private String selectedChkBoxIds;
	
	@Persist
	private String projectAndMembersToHighlight;
	
	/**
	 * Actions for ManageDirectory Invite Member
	 */
	private enum ManageDirectoryInviteMemberActions {
        LOAD_MEMBER, ADD_PROJECT, REMOVE_PROJECT, SUBMIT_SPACES_FOR_REMOVE ;
        public static ManageDirectoryInviteMemberActions get( String v ) {
            try {
                return ManageDirectoryInviteMemberActions.valueOf( v.toUpperCase() );
            } catch( Exception ex ) {
                log.error("Unspecified action : " + ex.getMessage());
            }
            return null;
         }
    };
	
    /**
	 * Initializing tokens
	 */
    public void initialize(){
    	log = Logger.getLogger(ManageDirectoryInviteMember.class);
    	deleteProjectTooltip = PropertyProvider.get("prm.directory.bulkinvitation.deleteproject.tooltip");
    	deleteBusinessTooltip = PropertyProvider.get("prm.directory.bulkinvitation.deletebusiness.tooltip");
    	if(StringUtils.isEmpty(projectAndMembersToHighlight))
    		projectAndMembersToHighlight = "";
       	if(StringUtils.isEmpty(currentSpaceId))
			currentSpaceId = SessionManager.getUser().getCurrentSpace().getID();
    	if(getSessionAttribute("setDefaultsForRemoveMember") != null) {
    		clearPersistedValues(); // clear list if space id is changed
    		currentSpaceId = SessionManager.getUser().getCurrentSpace().getID();
    		setSessionAttribute("setDefaultsForRemoveMember", null);
    	}
    }
	
    /**
     * Called when page activate
     * @return
     */
    Object onActivate(){
    	if(checkForUser() != null){
    		return checkForUser();
    	}
		initialize();
		return null;
	}
    
	/**
	 * Called when page activate with single parameter as action
	 * @return
	 */	
    Object onActivate(String action) {
    	  initialize();
		  if (StringUtils.isNotEmpty(action)) {
			 ManageDirectoryInviteMemberActions loadMemberAction = ManageDirectoryInviteMemberActions.get( action );
	         if (loadMemberAction == ManageDirectoryInviteMemberActions.LOAD_MEMBER) {
	        	 clearPersistedValues();
				 directoryId = getRequest().getParameter("directoryId");
				 searchUser = getRequest().getParameter("searchUser");
				 loadBusinessMember(directoryId, searchUser);
				 addProjectCorrespondingToSearch(getRequest().getParameter("searchUser"));
				 addEqualRowsForAllSpaces(false);
	         }else if(loadMemberAction == ManageDirectoryInviteMemberActions.ADD_PROJECT){
	        	isSearchMemberFound = Boolean.parseBoolean(getRequest().getParameter("requestFor")); 
				setPreviousSelectedValues(getRequest().getParameter("selectedRecords"));
				loadBusinessMember(null, null);
				addProjectOrSubbusiness(getRequest().getParameter("projectId"), true);
				addEqualRowsForAllSpaces(false);
				clearPersistedValue();
	         }else if(loadMemberAction == ManageDirectoryInviteMemberActions.REMOVE_PROJECT){
				 removeProjectName(getRequest().getParameter("projectId"));
				 setPreviousSelectedValues(getRequest().getParameter("selectedRecords"));
				 addEqualRowsForAllSpaces(true);
				 clearPersistedValue();
		     }else if(loadMemberAction == ManageDirectoryInviteMemberActions.SUBMIT_SPACES_FOR_REMOVE){
		 		selectedProjectsList(getRequest().getParameter("selectedProjectsList"));
		 		addEqualRowsForAllSpaces(true);
		     }
		  }
	  return null;
  }
	  
    /**
	 * Load business members
	 */
	private void loadBusinessMember(String directoryId, String searchUser) {
		if (!StringUtils.isNotEmpty(searchUser)
				|| searchUser.equals(PropertyProvider
						.get("prm.directory.invite.memberaddition.partialfirstorlastname.defaultmessage"))) {
			searchUser = "";
		}
		personWrappers = new ArrayList<BusinessDirectorySearchResults.BusinessDirectorySearchResult>();
		if (StringUtils.isNotEmpty(directoryId) && !directoryId.equals("-1")) {
			SpaceInvitationManager spaceInvitationManager = (SpaceInvitationManager) getSessionAttribute("spaceInvitationWizard");
			ISearchResults searchResults = ManageDirectoryHelper.loadBusinessMembers(directoryId, searchUser,
					spaceInvitationManager);
			Iterator iterator = searchResults.iterator();
			// loop to iterate user info to display in grid
			while (iterator.hasNext()) {
				try {
					BusinessDirectorySearchResults.BusinessDirectorySearchResult businessDirectorySearchResults = (BusinessDirectorySearchResults.BusinessDirectorySearchResult) iterator
							.next();
					if (!businessDirectorySearchResults.getEmail().endsWith("(Deleted)")) {
						personWrappers.add(businessDirectorySearchResults);
					}
				} catch (Exception e) {
					log.error("Error occurred in while loading business member " + e.getMessage());
				}
			}
		} else {
			personWrappers.clear();
		}
	}
  
	/**
	 * Adding project or sub business members
	 * @param listWrapper
	 * @param projectOrSubbusinessId
	 * @return
	 */
	public ProjectListWrapper addProjectOrSubbusinessMember(ProjectListWrapper listWrapper, String projectOrSubbusinessId) {
		// adding project members to person list.
		addProjectMembers(projectOrSubbusinessId, "");
		ArrayList<BusinessDirectorySearchResults.BusinessDirectorySearchResult> list = new ArrayList<BusinessDirectorySearchResults.BusinessDirectorySearchResult>();
		if (CollectionUtils.isNotEmpty(personWrappers)) {
			for (BusinessDirectorySearchResults.BusinessDirectorySearchResult businessDirectorySearchResult : personWrappers) {
				list.add(businessDirectorySearchResult);
			}
		}
		listWrapper.setProjectMemberLists(list);
		return listWrapper;
	}
	
	/**
	 * Call to check project is already added.
	 * if already added return true otherwise false.
	 * @param project
	 * @return
	 */
	public boolean isAllreadyAdded(String project) {
		boolean added = false;
		if(selectedProjects != null){
			for (int index = 0; index < selectedProjects.size(); index++) {
				if((((ProjectListWrapper) selectedProjects.get(index)).getProjectId().equals(project))) { 
					added = true;
				}
	        } 
		}
		return added;
	}
		
	/**
	 * Add same search member for all project or subbusiness
	 * @param listWrapper
	 * @return
	 */
	public ProjectListWrapper addSearchMemberForAllProjectOrSubbusiness(ProjectListWrapper listWrapper){
		ArrayList <BusinessDirectorySearchResults.BusinessDirectorySearchResult> list = new ArrayList<BusinessDirectorySearchResults.BusinessDirectorySearchResult>();
		for(BusinessDirectorySearchResults.BusinessDirectorySearchResult businessDirectorySearchResult : personWrappers){
			list.add(businessDirectorySearchResult);
		}
		listWrapper.setProjectMemberLists(list);
		return listWrapper;
	}
  
	/**
	 * Call when we add project by selecting project
	 * after adding project zone refreshes.
	 * @param projectOrSubbusinessId
	 * @param addDirectProject
	 */
	void addProjectOrSubbusiness(String projectOrSubbusinessId, boolean addDirectProject) {
		try {
			String projectName = "";
			ProjectListWrapper listWrapper = new ProjectListWrapper();
			if (selectedProjects == null) {
				selectedProjects = new ArrayList<ProjectListWrapper>();
			}
			if (StringUtils.isNotEmpty(projectOrSubbusinessId) && !projectOrSubbusinessId.equals("-1")) {
				Space space = SpaceFactory.constructSpaceFromID(projectOrSubbusinessId);
				if (space.isTypeOf(SpaceTypes.PROJECT_SPACE)) {
					projectName = DomainListBean.getProjectName(projectOrSubbusinessId);
					listWrapper.setSpaceNameBusiness(false);
				}
				if (space.isTypeOf(SpaceTypes.BUSINESS_SPACE)) {
					projectName = DomainListBean.getBusinessName(projectOrSubbusinessId);
					listWrapper.setSpaceNameBusiness(true);
				}
				if (!isAllreadyAdded(projectOrSubbusinessId)) {
					listWrapper.setProjectName(projectName);
					listWrapper.setProjectId(projectOrSubbusinessId);
					listWrapper.setCntforprojectAndSubbusinesses(selectedProjects.size());

					// addDirectProject is true when user add project and is false when user search for project
					if (addDirectProject) {
						selectedProjects.add(addProjectOrSubbusinessMember(listWrapper, projectOrSubbusinessId));
					} else if(CollectionUtils.isNotEmpty(personWrappers)){
						selectedProjects.add(addSearchMemberForAllProjectOrSubbusiness(listWrapper));
					}
				}
			}
		} catch (PersistenceException e) {
			log.error("Error occurred while adding project : " + e.getMessage());
		} catch (Exception e) {
			log.error("Error occurred while adding project : " + e.getMessage());
		}
	}
	
	/**
	 * For remove member after performing delete action
	 * @param projectWrapper
	 * @param memberToRemove
	 */
	public void removeMemberByProject(ProjectListWrapper projectWrapper, String memberToRemove){
		ArrayList<BusinessDirectorySearchResults.BusinessDirectorySearchResult> copyMembers = new ArrayList<BusinessDirectorySearchResults.BusinessDirectorySearchResult>();
		if (CollectionUtils.isNotEmpty(projectWrapper.getProjectMemberLists())) {
			copyMembers.addAll(projectWrapper.getProjectMemberLists());
		}
		for (BusinessDirectorySearchResults.BusinessDirectorySearchResult businessDirectorySearchResult : projectWrapper
				.getProjectMemberLists()) {
			if (businessDirectorySearchResult.getPersonId() != null) {
				if (businessDirectorySearchResult.getPersonId().equals(memberToRemove)) {
					copyMembers.remove(businessDirectorySearchResult);
				}
			}
		}
		projectWrapper.setProjectMemberLists(copyMembers);
	}
	
	/**
	 * Set selected members for each project from json object
	 * @param selectedProjectList
	 */
	public void selectedProjectsList(String selectedProjectList) {
		JSONObject jSONObject = null;
		if (StringUtils.isNotEmpty(selectedProjectList)) {
			try {
				jSONObject = new JSONObject(selectedProjectList);
			} catch (Exception e) {
				log.error("Error occurred while getting json Object :" + e.getMessage());
			}
		}
		if (CollectionUtils.isNotEmpty(selectedProjects)) {
			for (ProjectListWrapper projectWrapper : selectedProjects) {
				if (jSONObject != null && jSONObject.has(projectWrapper.getProjectId())) {
					JSONArray array = jSONObject.getJSONArray(projectWrapper.getProjectId());
					int noOfMembers = 0;
					for (int index = 0; index < array.length(); index++) {
						JSONObject jobject = array.getJSONObject(index);
						try {
							noOfMembers++;	
							Roster roster = new Roster(SpaceFactory.constructSpaceFromID(projectWrapper.getProjectId()));
							roster.removePerson(jobject.getString("memberId"));
							removeMemberByProject(projectWrapper, jobject.getString("memberId"));
						} catch (PersistenceException e) {
							noOfMembers--;
							setProjectAndMembersToHighlight(projectWrapper.getProjectId()+"_"+jobject.getString("memberId"));
							errorMessage = PropertyProvider.get("prm.directory.managedirectory.removemember.errormessage.label");
							log.error("Error occurred while removing selectedProjectsList : " + e.getMessage());
						}
					}
					if(noOfMembers != 0) {
						//publishing event to asynchronous queue
				        try {
				        	net.project.events.ProjectEvent projectEvent = (net.project.events.ProjectEvent) EventFactory.getEvent(ObjectType.PROJECT, EventType.MEMBER_DELETED_FROM_SPACE);
				        	projectEvent.setObjectID(projectWrapper.getProjectId());
				        	projectEvent.setSpaceID(projectWrapper.getProjectId());
				        	projectEvent.setObjectType(ObjectType.PROJECT);
				        	projectEvent.setName(SessionManager.getUser().getCurrentSpace().getName());
				        	projectEvent.setObjectRecordStatus("A");
				        	projectEvent.setNoOfMembers(noOfMembers);
				        	projectEvent.publish();
						} catch (EventException e) {
							Logger.getLogger(ManageDirectoryRemoveMember.class).error("ManageDirectoryRemoveMember.selectedProjectsList() :: Member Removed From Project Event Publishing Failed!", e);
						}
					}	
				}
			}
		}
	}
	
	/**
	 * Add project members 
	 * @param projectorSubbusinessId
	 * @param searchUser
	 * @return
	 */
	public boolean addProjectMembers(String projectorSubbusinessId, String searchUser){
		try {
			if(!projectorSubbusinessId.equals("-1")){
				Space space = SpaceFactory.constructSpaceFromID(projectorSubbusinessId);
				teamMembers = new Roster(space);
				teamMembers.load();
				Iterator iterator = teamMembers.iterator();
				if (CollectionUtils.isNotEmpty(personWrappers)) {
					personWrappers.clear();
					personWrappers = new ArrayList<BusinessDirectorySearchResults.BusinessDirectorySearchResult>();
				}
				while (iterator.hasNext()) {
					Person person = (Person) iterator.next();
					// for those user which are in project member but not in business. we need to add into person list.
					if(StringUtils.isEmpty(searchUser)){
						BusinessDirectorySearchResults businessDirectorySearchResults = new BusinessDirectorySearchResults();
						BusinessDirectorySearchResults.BusinessDirectorySearchResult businessDirectorySearchResult = businessDirectorySearchResults.new BusinessDirectorySearchResult(person);
						personWrappers.add(businessDirectorySearchResult);
					}else if(person.getFirstName().toUpperCase().startsWith(searchUser.toUpperCase()) || person.getLastName().toUpperCase().startsWith(searchUser.toUpperCase())){
						BusinessDirectorySearchResults businessDirectorySearchResults = new BusinessDirectorySearchResults();
						BusinessDirectorySearchResults.BusinessDirectorySearchResult businessDirectorySearchResult = businessDirectorySearchResults.new BusinessDirectorySearchResult(person);
						personWrappers.add(businessDirectorySearchResult);
					}
				}
				if(StringUtils.isNotEmpty(searchUser)){
					addProjectOrSubbusiness(projectorSubbusinessId, false);
				}
				isSearchMemberFound = CollectionUtils.isNotEmpty(personWrappers);
			}
		} catch (PersistenceException e) {
			log.error("Error occurred while adding project members: " + e.getMessage());
		}
		return false;
	}
  
	/**
	 * Add project as per user performed search criteria
	 * @param searchUser
	 */
	public void addProjectCorrespondingToSearch(String searchUser){
		List <PnBusiness> projectList = new ArrayList<PnBusiness>();
		projectList = ManageDirectoryHelper.getProjectLists(SessionManager.getUser().getCurrentSpace().getID(), getUser(), net.project.security.Action.DELETE);
		
		if(CollectionUtils.isNotEmpty(projectList)){
			for (PnBusiness projectName : projectList) {
				if(!projectName.getBusinessId().equals("-1")){
					addProjectMembers(projectName.getBusinessId().toString(), searchUser);
				}
			}
		}
	}
	
	/**
	 * Call when we remove project or subbusiness that are added 
	 * @param project
	 * @return
	 */
	void removeProjectName(String project){
		// Add for saving previous selection.
		if(selectedProjects.size() > 0){
			for(int index = 0; index < selectedProjects.size(); index++) {
				if(selectedProjects.get(index).getProjectId().equals(project)) {
					selectedProjects.remove(index);
				}
			}
		}
	}
	
	/**
	 * Managing all rows with same height
	 * @param isRemove
	 */
	 public void addEqualRowsForAllSpaces(boolean isRemoveBlankRows){
	    int memberSize = 0;
		if (CollectionUtils.isNotEmpty(selectedProjects)) {
			for (ProjectListWrapper listWrapper : selectedProjects) {
				if (CollectionUtils.isNotEmpty(listWrapper.getProjectMemberLists())) {
					if (memberSize < listWrapper.getProjectMemberLists().size()) {
						memberSize = listWrapper.getProjectMemberLists().size();
					}
				}
			}
		}
		if (CollectionUtils.isNotEmpty(selectedProjects)) {
			if(!isRemoveBlankRows){
				for (ProjectListWrapper listWrapper : selectedProjects) {
					if (CollectionUtils.isNotEmpty(listWrapper.getProjectMemberLists())) {
						for (int index = listWrapper.getProjectMemberLists().size(); index < memberSize; index++) {
							BusinessDirectorySearchResults businessDirectorySearchResults = new BusinessDirectorySearchResults();
							listWrapper.getProjectMemberLists().add(businessDirectorySearchResults.new BusinessDirectorySearchResult(new Person()));
						}
					}
				}
			}else{
				for (ProjectListWrapper listWrapper : selectedProjects) {
					ArrayList<BusinessDirectorySearchResults.BusinessDirectorySearchResult> copyMembers = new ArrayList<BusinessDirectorySearchResults.BusinessDirectorySearchResult>();
					if (CollectionUtils.isNotEmpty(listWrapper.getProjectMemberLists())) {
						copyMembers.addAll(listWrapper.getProjectMemberLists());
					}
					if (CollectionUtils.isNotEmpty(listWrapper.getProjectMemberLists())) {
						for (BusinessDirectorySearchResults.BusinessDirectorySearchResult member : listWrapper
								.getProjectMemberLists()) {
							if (StringUtils.isEmpty(member.getPersonId())) {
								copyMembers.remove(member);
							}
						}
						listWrapper.setProjectMemberLists(copyMembers);
					}
				}
			}
		}
	  }
	  
 	/**
	 * @return the selectedProjects
	 */
	public ArrayList<ProjectListWrapper> getSelectedProjectsList() {
		return selectedProjects;
	}
	
	/**
	 * Get member list 
	 * @return
	 */
	public List<BusinessDirectorySearchResults.BusinessDirectorySearchResult> getMemberLists() {
		return personWrappers;
	}
	
	/**
	 * @return comma separated project id's
	 */
	public String getProjectIds(){
		String projectIds = "";
		if(CollectionUtils.isNotEmpty(selectedProjects)) {
			for (ProjectListWrapper projectWrapper : selectedProjects) {
				projectIds += projectWrapper.getProjectId() + ",";
			}
		}
		return projectIds;
	}
	
	/**
	 * @return comma separated member id's
	 */
	public String getMemberIds() {
		String memberIds = "";
		if(CollectionUtils.isNotEmpty(personWrappers)) {
			for (BusinessDirectorySearchResults.BusinessDirectorySearchResult personWrapper : personWrappers) {
				memberIds += personWrapper.getPersonId() + ",";
			}
		}
		return memberIds;
	}
  
	/**
	 * Clear persisted values
	 */
	public void clearPersistedValues(){
		if(CollectionUtils.isNotEmpty(personWrappers))
			personWrappers.clear();
		if(CollectionUtils.isNotEmpty(selectedProjects))
			selectedProjects.clear();
		errorMessage = "";
		projectAndMembersToHighlight = "";
		selectedChkBoxIds = "";
		projectOrSubbusinessId = "";
	}
	
	/**
	 * Set previous selected values.
	 */
	public void setPreviousSelectedValues(String defaultProjectValue){
		if(StringUtils.isNotEmpty(defaultProjectValue)){
			JSONObject obj = new JSONObject(defaultProjectValue);
			projectOrSubbusinessId = obj.getString("projectOrSubbusinessId");
			selectedChkBoxIds = obj.getString("selectedChkBoxIds") != null ? obj.getString("selectedChkBoxIds").toString() : "";
		}
	}
	
	/**
	 * @return comma separated member id's for all added project
	 */
	public String getMemberIdList(){
		String memberIds = "";
		if(CollectionUtils.isNotEmpty(selectedProjects)){
			for (ProjectListWrapper listWrapper : selectedProjects) {
				if (CollectionUtils.isNotEmpty(listWrapper.getProjectMemberLists())) {
					for (BusinessDirectorySearchResults.BusinessDirectorySearchResult member : listWrapper.getProjectMemberLists()) {
						memberIds += member.getPersonId() + ",";
					}
				}
			}
		}
		return memberIds;
	}
	
	/**
	 * Clear persisted value
	 */
	public void clearPersistedValue(){
		errorMessage = "";
	    projectAndMembersToHighlight = "";
	}
	
	/**
	 * @return the projectAndMembersToHighlight
	 */
	public String getProjectAndMembersToHighlight() {
		return projectAndMembersToHighlight;
	}

	/**
	 * @param projectAndMembersToHighlight the projectAndMembersToHighlight to set
	 */
	public void setProjectAndMembersToHighlight(String projectAndMembersToHighlight) {
		this.projectAndMembersToHighlight += projectAndMembersToHighlight + ",";
	}
}
