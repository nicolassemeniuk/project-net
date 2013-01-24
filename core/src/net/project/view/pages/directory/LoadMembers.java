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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import net.project.base.directory.search.AbstractSearchResult;
import net.project.base.directory.search.DirectorySearchException;
import net.project.base.directory.search.IDirectoryContext;
import net.project.base.directory.search.ISearchResults;
import net.project.base.directory.search.ISearchableDirectory;
import net.project.base.directory.search.SearchControls;
import net.project.base.directory.search.SearchFilter;
import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.hibernate.model.PnBusiness;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.service.ServiceFactory;
import net.project.resource.InvitationException;
import net.project.resource.Invitee;
import net.project.resource.Person;
import net.project.resource.Roster;
import net.project.resource.SpaceInvitationManager;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.json.JSONObject;
import org.slf4j.Logger;

/**
 * For invite member page
 */
public class LoadMembers extends BasePage{
	
	private static Logger log = logger;
	
	@Property
	private AbstractSearchResult personWrapper;
	
	@Property
	@Persist
	private List<AbstractSearchResult> personWrappers;
	
	@Property	
	@Persist
	private String directoryId;
	
	@Property 
	private PnBusiness business;
	
	@Property
	@Persist
	private String searchUser;
	
	@Property
	private ArrayList<Invitee> inviteesList;
	
	@Property
	private Invitee invitedObject;
	
	@Property
	private Roster teamMembers;
	
	@Property
	private boolean userExistanceError;
	
	@Property
	private String errorIterator;
	
	@Persist
	private boolean delete; 
	
	@Persist
	private List<String> deletedList;
	
	@Property
	@Persist
	private String firstName;
	
	@Property
	@Persist
	private String lastName;
	
	@Property
	@Persist
	private String emailAddress;
	
	@Property
	private String errorReporter;
	
	private Pattern emailPattern;
	
	@Property
	@Persist
	private boolean error;
	
	@Property
	private int totalInvitee;
	
	@Persist
	private boolean isFromImportUser;
	
	@Property
	private String deleteUserTooltip;
	
	@Property
	private String importUserTooltip;
		
	@Property
	private String noMembersFoundMsg;
	
	@Property
	@Persist
	private String[] lists;
	
	@Property
	private int totalParticipants;
	
	@Property
	@Persist
	private long startOfMonth;
	
	@InjectPage
	private InviteMember inviteMember;
	
	@Property
	private boolean isLoadMember = false;
	
	@Property
	private boolean isInviteMember = false;
	
	@Persist
    private String currentSpaceId;
	
	/**
	 * Actions for Invite Member
	 */
	 private enum LoadMembersActions {
        LOAD_USER, ADD_MEMBER_TO_INVITEELIST, MOVE_MEMBER_TO_INVITEELIST, REMOVE_MEMBER, LOAD_INVITEE;
        public static LoadMembersActions get( String v ) {
            try {
                return LoadMembersActions.valueOf( v.toUpperCase() );
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
    	error = true;
		userExistanceError = false;
		errorReporter = new String();
		emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
		deleteUserTooltip = PropertyProvider.get("prm.directory.invitemember.removeuser.tooltip");
		importUserTooltip = PropertyProvider.get("prm.directory.invitemember.importuser.tooltip");
		noMembersFoundMsg = PropertyProvider.get("prm.directory.loadmembers.nomemberfound.error.message");
		createInvitedMemberList();
		initializeInviteeList();
		if(getSessionAttribute("setDefaultsForMemberInvitation") != null) {
    		clearPersistedValues(); // clear list if space id is changed
    		currentSpaceId = SessionManager.getUser().getCurrentSpace().getID();
    		setSessionAttribute("setDefaultsForMemberInvitation", null);
    	}
    }

    /**
     * Called when page activate with single parameter as action 
     * @param action
     * @return
     */
  Object onActivate(String action) {
	  initialize();
	  if (StringUtils.isNotEmpty(action)) {
		  LoadMembersActions loadMemberAction = LoadMembersActions.get( action );
		  if (loadMemberAction == LoadMembersActions.LOAD_USER) {
				isLoadMember = true;
				directoryId = getRequest().getParameter("directoryId");
				searchUser = getRequest().getParameter("searchUser");
	          	if(!StringUtils.isNotEmpty(searchUser) || searchUser.equals(PropertyProvider.get("prm.directory.invite.memberaddition.partialfirstorlastname.defaultmessage"))){
	          		searchUser = "";
	          	}
	    		if(StringUtils.isNotEmpty(directoryId) && !directoryId.equals("-1")){
	    			try{
		    			SpaceInvitationManager spaceInvitationManager = (SpaceInvitationManager) getSessionAttribute("spaceInvitationWizard");
		    			spaceInvitationManager.setDirectoryID(directoryId);
		    			spaceInvitationManager.setSearchName(searchUser);
		        		ISearchableDirectory searchDirectory = spaceInvitationManager.getSearchableDirectory();
		    	        IDirectoryContext directoryContext = searchDirectory.getDirectoryContext();
		    	        // Construct search filter
		    	        SearchFilter searchFilter = new SearchFilter();
		    	        searchFilter.add("name", spaceInvitationManager.getSearchName());
		    	        // Build search control
		    	        SearchControls searchControls = new SearchControls();
		    	        // Perform search
		    	        ISearchResults searchResults = directoryContext.search(searchFilter, searchControls);
		    	        Iterator iterator = searchResults.iterator();
		    	        personWrappers = new ArrayList<AbstractSearchResult>();
		    	        //loop to iterate user info to display in grid
		    	        while(iterator.hasNext()){
		    	        	try{
		    	        		AbstractSearchResult searchedDirectoryResult = (AbstractSearchResult)iterator.next();
		    	        		if(searchedDirectoryResult != null && searchedDirectoryResult.getEmail() != null && 
		    	        				!searchedDirectoryResult.getEmail().endsWith("(Deleted)")){
		    	        				searchedDirectoryResult.setEmail(searchedDirectoryResult.getEmail().replaceAll("'", ""));
		    	        				searchedDirectoryResult.setDisplayName(searchedDirectoryResult.getDisplayName().replaceAll("'", ""));
		    	        			personWrappers.add(searchedDirectoryResult);
		    	        		}
		    	        	}catch(Exception e){
		    	        		log.error("Error occurred while displaying users list" + e.getMessage());  
		    	        	}
		    	        }
		    	        if(CollectionUtils.isNotEmpty(inviteesList) && CollectionUtils.isNotEmpty(personWrappers)){
		        			for(AbstractSearchResult userList : personWrappers){
		        				for(Invitee list : inviteesList){
		        					if(userList.getEmail().equals(list.getEmail()) || isAllreadyInvited(userList.getEmail())){
		        						userList.setDisable(true);
		        					}
		        				}
		        			}
		        		}
		    	        checkForAllreadyExistMembers();
		    		}catch(InvitationException e){
		    			log.error("Error occurred inviting user : " + e.getMessage()); 	
		    		}catch(DirectorySearchException e){
		    			log.error("Error occurred while searching user : " + e.getMessage()); 	
		    		}
	    		}else {
	    			clearPersistedValues();
	    	    }
	    		if(CollectionUtils.isEmpty(personWrappers)){
	    			userExistanceError = true;
		        	errorIterator = noMembersFoundMsg;
	    		}
            } else if(loadMemberAction == LoadMembersActions.ADD_MEMBER_TO_INVITEELIST){
            	if(StringUtils.isNotEmpty(getRequest().getParameter("newInviteeJsonObject"))){
        			JSONObject jSONObject = new JSONObject(getRequest().getParameter("newInviteeJsonObject"));
        			if(jSONObject.getString("fname") != null)
        				firstName = jSONObject.getString("fname");
        			if(jSONObject.getString("lname") != null)
        				lastName = jSONObject.getString("lname");
        			if(jSONObject.getString("email") != null)
        				emailAddress = jSONObject.getString("email");
        			addDirectUser();
        		}
			} else if(loadMemberAction == LoadMembersActions.MOVE_MEMBER_TO_INVITEELIST){
				SpaceInvitationManager spaceInvitationManager = (SpaceInvitationManager) getSessionAttribute("spaceInvitationWizard");
				User user = SessionManager.getUser();
				spaceInvitationManager.setSpace(user.getCurrentSpace());
				spaceInvitationManager.setUser(user);
				String checkslist = getRequest().getParameter("checkslist");
				if (inviteesList == null) {
					inviteesList = new ArrayList<Invitee>();
				}
				if (StringUtils.isNotEmpty(checkslist)) {
					lists = checkslist.split(",");
					if (CollectionUtils.isNotEmpty(personWrappers)) {
						for (int i = 0; i < personWrappers.size(); i++) {
							for (int j = 0; j < lists.length; j++) {
								if (personWrappers.get(i).getEmail().equals(lists[j])) {
									personWrappers.get(i).setDisable(true);
									try {
										createAndAddInvitee(personWrappers.get(i).getID(), personWrappers.get(i).getFirstName(), personWrappers.get(i).getLastName(), personWrappers.get(i).getDisplayName(), personWrappers.get(i).getEmail());
									} catch (InvitationException e) {
										log.error("Error occurred while adding user : " + e.getMessage()); 	
									}
									checkslist = "";
								}
							}
						}
					} 
				} 
			}else if(loadMemberAction == LoadMembersActions.REMOVE_MEMBER){
				SpaceInvitationManager spaceInvitationWizard = (SpaceInvitationManager) getSessionAttribute("spaceInvitationWizard");
				if (inviteesList != null) {
					// Remove old for loop
					for (int i = 0; i < inviteesList.size(); i++) {
						if (inviteesList.get(i).getEmail().equals(getRequest().getParameter("emailId"))) {
							// after deleting user from invited list he become visible in user list.
							spaceInvitationWizard.removeInvitee(inviteesList.get(i).getEmail());
							inviteesList.remove(i);
						}
					}
				}
			}
		  	isInviteMember = (loadMemberAction == LoadMembersActions.REMOVE_MEMBER) 
		  						|| (loadMemberAction == LoadMembersActions.MOVE_MEMBER_TO_INVITEELIST)
		  						|| (loadMemberAction == LoadMembersActions.ADD_MEMBER_TO_INVITEELIST) 
		  						|| (loadMemberAction == LoadMembersActions.LOAD_INVITEE);
		  }
	  return null;
  }
	  
  /**
   * @param email
   * @return
   */
  public  boolean isAllreadyInvited(String email) {
		boolean invitted = false;
		if(CollectionUtils.isNotEmpty(teamMembers)){
			for (int i = 0; i < teamMembers.size(); i++) {
				if((((Person) teamMembers.get(i)).getEmail().equals(email))) {
					invitted = true;
				}
	        }
		}
		return invitted;
	}
  
  	/**
     * This method checks and change the status of member searched by business if allready invited or in invitee list 
     */
    public void checkForAllreadyExistMembers() {
    	if(CollectionUtils.isNotEmpty(personWrappers)) {
			for(AbstractSearchResult userList : personWrappers){
				if(CollectionUtils.isNotEmpty(inviteesList)){
					for(Invitee list : inviteesList){
						if(userList.getEmail().equals(list.getEmail()) || isAllreadyInvited(list.getEmail())) {
							userList.setDisable(true);
							break;
						}
					}
				} else {
					userList.setDisable(isAllreadyInvited(userList.getEmail()));
				}
			}
		}
    }
    
    /**
	 * load team members details of current project
	 */
	public void createInvitedMemberList() {
		List<Teammate> onlineTeammates = ServiceFactory.getInstance().getPnPersonService().getOnlineMembers(Integer.parseInt(getUser().getCurrentSpace().getID()));
		teamMembers = new Roster(SessionManager.getUser().getCurrentSpace());
		teamMembers.load();
		Iterator iterator = teamMembers.iterator();
		while(iterator.hasNext()){
			Person person = (Person)iterator.next();
			for(int index = 0; index < onlineTeammates.size() ;index++ ){
				if(person.getID().equals(onlineTeammates.get(index).getPersonId().toString())){
					person.setOnline(true);
					break;
				}
			}
		}
		totalParticipants = teamMembers.size();
		PnCalendar calendar = new PnCalendar();
		startOfMonth = calendar.startOfMonth(new Date()).getTime();
	}
    
	/**
	 * Clear persisted values 
	 */
    public void clearPersistedValues() {
    	if(CollectionUtils.isNotEmpty(personWrappers)) {
			personWrappers.clear();
		}
		directoryId = null;
    	searchUser = "";
	}
    
	/**
	 * @return the delete
	 */
	public boolean isDelete() {
		return delete;
	}

	/**
	 * @param delete the delete to set
	 */
	public void setDelete(boolean delete) {
		if(delete){
			getDeletedList().add(personWrapper.getID());
		}
		this.delete = delete;
	}
	
	/**
	 * @param deletedList the deletedList to set
	 */
	public void setDeletedList(List<String> deletedList) {
		this.deletedList = deletedList;
	}
	
	
	/**
	 * @return the deletedList
	 */
	public List<String> getDeletedList() {
		  if(deletedList == null){
              deletedList = new ArrayList<String>();
      } 
		return deletedList;
	}
	
	/**
	 * adding user directly from outside.
	 */
	void addDirectUser(){
		if(!getFromImportUser()){
			String displayName = "";
			if(StringUtils.isEmpty(firstName)) {
				errorReporter = getText("prm.directory.invite.memberaddition.firstnamevalidation.message");
			} else if(StringUtils.isEmpty(lastName)) {
				errorReporter = getText("prm.directory.invite.memberaddition.lastnamevalidation.message");
			} else if(StringUtils.isEmpty(emailAddress)) {
				errorReporter = getText("prm.directory.invite.memberaddition.emailaddressvalidation.message");
			} else {
				displayName += firstName+" "+lastName;
				try {
					createAndAddInvitee("", firstName, lastName, displayName, emailAddress);
				} catch (InvitationException e) {
					log.error("Error occurred while adding direct user : " + e.getMessage()); 	
				}
			}
			firstName = "";
			lastName = "";
			emailAddress = "";
		}else{
			setFromImportUser(false);
		}
	}
	
	/**
	 * For adding invitee into space invitation wizard
	 * @param personId
	 * @param fName
	 * @param lName
	 * @param dName
	 * @param email
	 * @return
	 * @throws InvitationException
	 */
	public boolean createAndAddInvitee(String personId, String fName,String lName,String dName, String email) throws InvitationException{
		if (inviteesList == null) {
			inviteesList = new ArrayList<Invitee>();
		}
		SpaceInvitationManager spaceInvitationManager = (SpaceInvitationManager) getSessionAttribute("spaceInvitationWizard");
	
		Invitee lastInvitee = new Invitee();
		boolean flag = false;
		// for setting id as a last element.
		if (CollectionUtils.isNotEmpty(inviteesList)) {
			for (int i = 0; i < inviteesList.size(); i++) {
				if (StringUtils.isNotEmpty(email)) {
					if (inviteesList.get(i).getEmail().contains(email) && isAllreadyInvited(email)) {
						flag = true;
						break;
					}
				} else {
					errorReporter = "Please enter email address.";
				}
			}
		}
		
		lastInvitee.setFirstName(fName);
		lastInvitee.setLastName(lName);
		lastInvitee.setDisplayName(dName);
		lastInvitee.setEmail(email);
		lastInvitee.setInvite("");
		if(!personId.equals(""))
			lastInvitee.setID(personId);
		User user = SessionManager.getUser();
		spaceInvitationManager.setSpace(user.getCurrentSpace());
		spaceInvitationManager.setUser(user);
		
		if (lastInvitee.getEmail() == null || lastInvitee.getEmail().trim().length() == 0) {
			// We need an email address
			errorReporter = "E-mail address is required";
		} else {
			// We have an email address
			net.project.resource.SpaceInviteeValidator validator = new net.project.resource.SpaceInviteeValidator(user.getCurrentSpace(), lastInvitee);

			// Check to make sure the entered email address is valid
			if (!validator.isValidEmail()) {
				// Invalid email address
				// Go back to previous page with error message
				errorReporter = "Invalid emailAddress";
			} else if (validator.isAlreadyInvited()) {
				// User already invited
				// Go back to previous page with error message

				errorReporter = "User Already Invited";
				error = false;
			} else {
				// All successful
				// Add the validated invitee to the space invitation wizard
				if (!flag) {
					spaceInvitationManager.addMember(lastInvitee);
					inviteesList.remove(lastInvitee);
					inviteesList.add(lastInvitee);
					error = true;
				}
			}
		}
		if (inviteesList != null) {
			totalInvitee = inviteesList.size();
		}
		return true;
	}	
	
	
	 /**
     * Initilize invitee list
     */
    public void initializeInviteeList(){
    	SpaceInvitationManager spaceInvitationWizard = (SpaceInvitationManager) getSessionAttribute("spaceInvitationWizard");
		if (spaceInvitationWizard != null) {
			inviteesList = spaceInvitationWizard.getInviteeList();
			if (inviteesList != null) {
				Iterator iterator = inviteesList.iterator();
				inviteesList = new ArrayList<Invitee>();
				while (iterator.hasNext()) {
					try {
						Invitee invitee = (Invitee) iterator.next();
						inviteesList.add(invitee);
					} catch (Exception e) {
						log.error("Error occurred while displaying invitees lists" + e.getMessage());
					}
				}
			} else {
				if (personWrappers != null && personWrappers.size() > 0) {
					for (AbstractSearchResult userList : personWrappers) {
						userList.setDisable(false);
					}
				}
			}
		} else {
			inviteesList = null;
		}
    }
    
    /**
	 * Get invitee list count
	 * @return
	 */
	public int getInviteeCount(){
		return inviteesList != null ? inviteesList.size() : 0; 
	}
	
    
    /**
	 * Get Invitee email address with comma separated string
	 * @return
	 */
	public String getInviteeEmail(){
		String emailIds = "";
		if(CollectionUtils.isNotEmpty(inviteesList)) {
			for (Invitee invitee : inviteesList) {
				emailIds += invitee.getEmail()  + "," ;
			}
		}
		return emailIds;
	}
	
	/**
	 * @return the isFromImportUser
	 */
	public boolean getFromImportUser() {
		return isFromImportUser;
	}
	
	/**
	 * @param isFromImportUser the isFromImportUser to set
	 */
	public void setFromImportUser(boolean isFromImportUser) {
		this.isFromImportUser = isFromImportUser;
	}

	/**
	 * 
	 * @return
	 */
	public int getBusinessMembersCount() {
		return personWrappers != null ? personWrappers.size() : 0; 
	}
	
	/**
	 * Get invitee count
	 * @return
	 */
	public int getInviteesCount() {
		return inviteesList != null ? inviteesList.size() : 0; 
	}
}
