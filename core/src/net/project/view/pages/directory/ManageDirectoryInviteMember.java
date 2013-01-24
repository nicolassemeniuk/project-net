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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.project.base.Module;
import net.project.base.directory.search.AbstractSearchResult;
import net.project.base.directory.search.ISearchResults;
import net.project.base.property.PropertyProvider;
import net.project.directory.GroupWrapper;
import net.project.directory.ManageDirectoryHelper;
import net.project.directory.ProjectListWrapper;
import net.project.gui.html.HTMLOptionList;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.project.DomainListBean;
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
import org.apache.tapestry5.json.JSONObject;

/**
 * For Invite member page in manage directory
 */
public class ManageDirectoryInviteMember extends BasePage{
	
	private static Logger log;
	
	@Property
	@Persist
	private String projectOrSubbusinessId;
	
	@Property
	@Persist
	private String searchUser;
	
	@Property	
	@Persist
	private String directoryId;
	
	@Property
	private AbstractSearchResult personWrapper;
	
	@Property
	@Persist
	private List<AbstractSearchResult> personWrappers;
	
	@Property
	@Persist
	private String selectedChkBoxIds;
	
	@Property
	@Persist
	private String selectedRoleIds;
	
	@Property
	@Persist
	private String totalMembersSelctedAsPerProject;
	
	@Property
	private boolean isLoadMember = false;
	
	@Property
	@Persist
	private ArrayList<ProjectListWrapper> selectedProjects;
	
	@Property
	private ProjectListWrapper projectListWrapper;
	
	@Property
	private String deleteProjectTooltip;
	
	@Property
	private String deleteBusinessTooltip;
	
	@Property
	private Integer moduleId;
	
	@Persist
    private String currentSpaceId;
	
	@Property
	private String defaultProjectValue;
	
	@Property 
	private String removeProjectId;
	
	@Property
	@Persist
	private boolean sendToAll;
	
	@Property
	@Persist
	private String additionalMessage;
	
	/**
	 * Actions for ManageDirectory Invite Member
	 */
	private enum ManageDirectoryInviteMemberActions {
	    LOAD_MEMBER, ADD_PROJECT, REMOVE_PROJECT, SEND_ALL_SPACES_FOR_INVITATION ;
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
		moduleId = Module.DIRECTORY;
		if(getSessionAttribute("setDefaultsForMemberInvitation") != null) {
    		clearPersistedValues(); // clear list if space id is changed
    		currentSpaceId = SessionManager.getUser().getCurrentSpace().getID();
    		setSessionAttribute("setDefaultsForMemberInvitation", null);
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
	           		isLoadMember = true;
	           		directoryId = getRequest().getParameter("directoryId");
	             	searchUser = getRequest().getParameter("searchUser");
	             	setPreviousSelectedValues(getRequest().getParameter("selectedRecords"));
	        		if(!StringUtils.isNotEmpty(searchUser) || searchUser.equals(PropertyProvider.get("prm.directory.invite.memberaddition.partialfirstorlastname.defaultmessage"))){
	        			searchUser = "";
	        		}
		   			if(StringUtils.isNotEmpty(directoryId) && !directoryId.equals("-1")){
		   				SpaceInvitationManager spaceInvitationManager = (SpaceInvitationManager) getSessionAttribute("spaceInvitationWizard");
		   							
		   		        ISearchResults searchResults = ManageDirectoryHelper.loadBusinessMembers(directoryId, searchUser, spaceInvitationManager);
		   		        Iterator iterator = searchResults.iterator();
		   		        personWrappers = new ArrayList<AbstractSearchResult>();
		   		        //loop to iterate user info to display in grid
		   		        while(iterator.hasNext()){
		   		       		try{
			   		       		AbstractSearchResult searchedDirectoryResult = (AbstractSearchResult)iterator.next();
		    	        		if(searchedDirectoryResult != null && searchedDirectoryResult.getEmail()!= null && 
		    	        				!searchedDirectoryResult.getEmail().endsWith("(Deleted)")){
		    	        			personWrappers.add(searchedDirectoryResult);
		    	        		}
		   		        	}catch(Exception e){
		   		        		log.error("Error occurred while displaying users list" + e.getMessage());  
		   		        	}
		   		        }
		   		        displayInvitiesList();
		   			} else{
		   				personWrappers.clear();
		   			}
            } else if(loadMemberAction == ManageDirectoryInviteMemberActions.ADD_PROJECT){
            	isLoadMember = true;
            	addProjectorSubbusiness(getRequest().getParameter("projectId"), getRequest().getParameter("selectedRecords"));
            }else if(loadMemberAction == ManageDirectoryInviteMemberActions.REMOVE_PROJECT){
            	isLoadMember = true;
            	removeProjectName(getRequest().getParameter("projectId"), getRequest().getParameter("selectedRecords"));
            }
    	 }    	
    	 return null;
    }
  
	/**
	 * Set previous selected values.
	 */
	public void setPreviousSelectedValues(String defaultProjectValue){
		if(StringUtils.isNotEmpty(defaultProjectValue)){
			JSONObject obj = new JSONObject(defaultProjectValue);
			projectOrSubbusinessId = obj.getString("projectOrSubbusinessId");
			selectedChkBoxIds = obj.getString("selectedChkBoxIds") != null ? obj.getString("selectedChkBoxIds").toString() : "";
			selectedRoleIds = obj.getString("selectedRoleIds") != null ?  obj.getString("selectedRoleIds").toString() : "";
			totalMembersSelctedAsPerProject = obj.getString("countAsPerProject") != null ?  obj.getString("countAsPerProject").toString() : "";
		}
	}
	
	/**
	 * Displaying invities list and set the online status.
	 */
	private void displayInvitiesList(){
		List<Teammate> onlineTeammates = ServiceFactory.getInstance().getPnPersonService().getOnlineMembers();
		if(personWrappers != null){
			Iterator iterator = personWrappers.iterator();
			while(iterator.hasNext()){
		       	try{
		       		AbstractSearchResult member = (AbstractSearchResult)iterator.next();
		       		if(member.getID() != null) {
			       		for(int index = 0; index < onlineTeammates.size() ;index++ ){
							if(member.getPersonId().equals(onlineTeammates.get(index).getPersonId().toString())){
								member.setOnline(true);
								break;
							}
						}
		       		}
		      	}catch(Exception e){
		      		log.error("Error occurred while displaying invitees lists" + e.getMessage());
		      	}
			}
		}
	}
	
	/**
	 * Call to check project is already added.
	 * if already added return true otherwise false.
	 * @param project
	 * @return
	 */
	public  boolean isAllreadyAdded(String project) {
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
	 * Call when we add project by selecting project.
	 * after adding project zone refreshes.
	 */
	void addProjectorSubbusiness(String projectOrSubbusinessId, String selectedRecords){
		try{
			setPreviousSelectedValues(selectedRecords);
			String projectName ="";
			if(selectedProjects == null){
				selectedProjects =  new ArrayList<ProjectListWrapper>();
			}
			if(StringUtils.isNotEmpty(projectOrSubbusinessId) && !projectOrSubbusinessId.equals("-1")){
				Space space =  SpaceFactory.constructSpaceFromID(projectOrSubbusinessId);
				if(space.isTypeOf(SpaceTypes.PROJECT_SPACE)){
					projectName = DomainListBean.getProjectName(projectOrSubbusinessId);
				}if(space.isTypeOf(SpaceTypes.BUSINESS_SPACE)){
					 projectName = DomainListBean.getBusinessName(projectOrSubbusinessId);
				}
				if(!isAllreadyAdded(projectOrSubbusinessId)){
					ProjectListWrapper listWrapper;
					listWrapper = new ProjectListWrapper();
					listWrapper.setProjectName(projectName);
					listWrapper.setProjectId(projectOrSubbusinessId);
					listWrapper.setCntforprojectAndSubbusinesses(selectedProjects.size());
					selectedProjects.add(getAllRoleOptionList(listWrapper));
				}
			}
		}catch(PersistenceException e){
			log.error("Error occurred while adding project : " + e.getMessage());
		}catch(Exception e){
			log.error("Error occurred while adding project : " + e.getMessage());
		}
	}
	
	/**
	 * Displaying all role list.
	 */
	private ProjectListWrapper getAllRoleOptionList(ProjectListWrapper projectListWrapper){
		try {
			SpaceInvitationManager spaceInvitationWizard = (SpaceInvitationManager) getSessionAttribute("spaceInvitationWizard");
			ArrayList group = new ArrayList();
			Space space =  SpaceFactory.constructSpaceFromID(projectListWrapper.getProjectId());
			if(space.isTypeOf(SpaceTypes.PROJECT_SPACE)){
				projectListWrapper.setSpaceNameBusiness(false);
			}
			if(space.isTypeOf(SpaceTypes.BUSINESS_SPACE)){
				projectListWrapper.setSpaceNameBusiness(true);
			}
			group = spaceInvitationWizard.getRoleOptionLists(space,spaceInvitationWizard.getAssignedRoles());
			Iterator iterator = group.iterator();
			ArrayList<GroupWrapper> userRoles = new ArrayList<GroupWrapper>();
			while (iterator.hasNext()) {
				GroupWrapper groupWrapper = (GroupWrapper) iterator.next();
				userRoles.add(new GroupWrapper(groupWrapper.getGroupId(), groupWrapper.getGroupName(), false));
			}
			userRoles.add(new GroupWrapper("0", PropertyProvider.get("prm.directory.directorypage.roster.column.teammember"), true));
			Collections.sort(userRoles);
			projectListWrapper.setUserRoles(userRoles);
			projectListWrapper.setRoleOptionList(HTMLOptionList.makeHtmlOptionList(userRoles));
			// for disabling already invited user.
			Roster teamMembers = new Roster(space);
			teamMembers.load();
			Iterator teamMembersiterator = teamMembers.iterator();
			String invitedParticipant = new String();
			while(teamMembersiterator.hasNext()){
				Person person = (Person)teamMembersiterator.next();
				invitedParticipant += person.getEmail() + ",";
			}
			projectListWrapper.setParticipantList(invitedParticipant);
			projectListWrapper.setParticipantListCount(CollectionUtils.isNotEmpty(teamMembers)?teamMembers.size():0);
		} catch (Exception e) {
			log.error("Error occurred while displaying role lists" + e.getMessage());
		}
		return projectListWrapper;
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
			for (AbstractSearchResult personWrapper : personWrappers) {
				memberIds += personWrapper.getPersonId() + ",";
			}
		}
		return memberIds;
	}
	
	/**
	 * @return comma separated invited member email id's
	 */
	public String getInviteMemberEmailIds() {
		String inviteMemberEmailIds = "";
		if(CollectionUtils.isNotEmpty(personWrappers)) {
			for (AbstractSearchResult personWrapper : personWrappers) {
				inviteMemberEmailIds += personWrapper.getEmail().replaceAll("'", "`") + ",";
			}
		}
		return inviteMemberEmailIds;
	}

	/**
	 * Call when we remove project or subbusiness that 
	 * are added.
	 * @param project
	 * @return
	 */
	void removeProjectName(String project, String selectedRecords){
		// add for saving previous selection.
		setPreviousSelectedValues(selectedRecords);
		if(CollectionUtils.isNotEmpty(selectedProjects)){
			ArrayList<ProjectListWrapper> copyOfProjectList = (ArrayList<ProjectListWrapper>) selectedProjects.clone();
			for(ProjectListWrapper listWrapper : copyOfProjectList) {
				if(listWrapper.getProjectId().equals(project)) {
					selectedProjects.remove(listWrapper);
				}
			}
		}
	}
	
	/**
	 * Clear the list on space changes or invitation completion
	 */
	public void clearPersistedValues(){
		if(CollectionUtils.isNotEmpty(selectedProjects))
			selectedProjects.clear();
		directoryId = "";
		searchUser = "";
		projectOrSubbusinessId = "";
		selectedChkBoxIds = "";
		selectedRoleIds =  "";
		totalMembersSelctedAsPerProject = "";
	}
	
	/**
	 * @return size of member list
	 */
	public int getBusinessMembersCount() {
		return personWrappers != null ? personWrappers.size() : 0; 
	}
	
	/**
	 * @return the selectedProjects
	 */
	public ArrayList<ProjectListWrapper> getSelectedProjectsList() {
		return selectedProjects;
	}
	
	/**
	 * @return member list
	 */
	public List<AbstractSearchResult> getMemberLists() {
		return personWrappers;
	}
}
