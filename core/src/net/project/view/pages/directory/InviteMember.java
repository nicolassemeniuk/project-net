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

import net.project.base.Module;
import net.project.base.directory.DirectoryException;
import net.project.base.directory.search.ISearchResults;
import net.project.base.property.PropertyProvider;
import net.project.business.BusinessDirectorySearchResults;
import net.project.calendar.PnCalendar;
import net.project.hibernate.model.PnBusiness;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.service.IPnBusinessSpaceService;
import net.project.hibernate.service.ServiceFactory;
import net.project.license.License;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpaceBean;
import net.project.resource.Invitee;
import net.project.resource.LicenseInvitationManager;
import net.project.resource.Person;
import net.project.resource.Roster;
import net.project.resource.SpaceInvitationManager;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.session.PageContextManager;
import net.project.space.ISpaceTypes;
import net.project.space.Space;
import net.project.space.SpaceTypes;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;
import net.project.view.pages.resource.management.GenericSelectModel;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.json.JSONObject;

/**
 * For Invite member page 
 */
public class InviteMember extends BasePage{ 
	
	private static Logger log;
	
	@Inject
	private PropertyAccess access;
	
	@Property
	@Persist
	private GenericSelectModel<PnBusiness> directoryBeanModel;
	
	@Property
	@Persist
	private BusinessWrapper businessWrapper;
	
	@Property
	@Persist
	private String businessDirectoryName;
	
	@Property
	@Persist
	private String searchUser;
	
	@Property
	@Persist
	private String firstName;
	
	@Property
	@Persist
	private String lastName;
	
	
	@Property	
	@Persist
	private String directoryId;
	
	@Property
	@Persist
	private String emailAddress;
	
	@Property
	@Persist
	private BusinessDirectorySearchResults businessDirectorySearchResults;
	
	@Property
	private Roster teamMembers;
	
	@Property
	private Person person;
	
	@Property
	private int totalParticipants;
	
	@Property
	private int totalInvitee;
	
	@Property
	@Persist
	private String additionalMessage;
	
	@Property
	@Persist
	private boolean sendToAll;
	
	@Property
	@Persist
	private String checkslist;
	
	@Property
	private BusinessDirectorySearchResults.BusinessDirectorySearchResult personWrapper;
	
	@Property
	@Persist
	private List<BusinessDirectorySearchResults.BusinessDirectorySearchResult> personWrappers;
	
	@Property
	private Invitee invitedObject;
	
	@Property
	private ArrayList<Invitee> inviteesList;
	
	@Persist
	private boolean delete; 
	
	
	@Persist
	private List<String> deletedList;
	
	@Property
	@Persist
	private ISearchResults tempResults;
	
	@Property
	@Persist
	private String[] lists;
	
	@Property
	@Persist
	private long startOfMonth;
	
	@Property
	@Persist
	private boolean error;
	
	@Property
	private String errorIterator;
	
	@Property
	private boolean userExistanceError;
	
	@Property
	private String errorReporter;
	
	@Property
	@Persist
	private int divHeight;
	
	@Property
	@Persist
	private String divScroll;
	
	@Property
	@Persist
	private String userListCssStyle;
	
	@Property
	@Persist
	private String height;
	
    @InjectPage
    private AddResponsibility addResponsibility;
    
    @Property
	@Persist
	private String rowsPerPage;
    
    @Property
	private ArrayList<Invitee> importedObjectList;
    
    @Property
    private Teammate teammate;

    @Persist
    private String currentSpaceId;
    
    private Pattern emailPattern;
    
	@Persist
	private boolean isFromImportUser;
	
	@Property
	private String addNewInviteeCaption;
	
	@Property 
	private String defaultValue;
	
	@Property 
	private PnBusiness business;
	
	@Property 
	private String addNewInviteeDetails;
	
	@Property 
	private String sendInvitationDetails;
	
	@Property
	private String deleteUserTooltip;
	
	@Property
	private String importUserTooltip;
	
	@Property
	private Integer moduleId;
	
	@Property
	private Integer actionIdForCreate;
	
	@Property
	private ArrayList<Invitee> membersInInviteesList;
	
	@Property	
	@Persist
	private String businessId;
	
	@Property	
	@Persist
	private boolean showDirectory;
	
	@InjectPage
    private LoadMembers loadMembers;
	
	@Persist
	private int InviteesCount;
	
	/**
	 * Getting directory business list.
	 */
    Object onActivate(){
    	Object isValidAccess;
    	isValidAccess = checkForUser();
		//check permission of user to access 
    	if(isValidAccess == null){
    		isValidAccess = checkAccess(getUser().getCurrentSpace().getID(), net.project.base.Module.DIRECTORY, net.project.security.Action.CREATE);
    	}
		initiliazeValues();
		return isValidAccess;
	}
	
    /**
	 * Initialize the lists 
	 */
	private void initiliazeValues() {
		try {
			log = Logger.getLogger(InviteMember.class);
			error = true;
			userExistanceError = false;
			errorReporter = new String();
			rowsPerPage = "2000";
			emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
			addNewInviteeCaption = PropertyProvider.get("prm.directory.directory.invitemember.addnewinviteebutton.caption");
			deleteUserTooltip = PropertyProvider.get("prm.directory.invitemember.removeuser.tooltip");
			importUserTooltip = PropertyProvider.get("prm.directory.invitemember.importuser.tooltip");
			moduleId = Module.DIRECTORY;
			actionIdForCreate = Action.CREATE;
			businessId = directoryId;
			// check the previous currentSpaceId with latest one
			if(StringUtils.isEmpty(currentSpaceId))
				currentSpaceId = SessionManager.getUser().getCurrentSpace().getID();
			
			getDirectoryList();
			createInvitedMemberList();
			if(getSessionAttribute("setDefaultsForMemberInvitation") != null) {
	    		clearPersistedValues(); // clear list if space id is changed
	    		currentSpaceId = SessionManager.getUser().getCurrentSpace().getID();
	    	}
	    	checkForAllreadyExistMembers();
		} catch (Exception e) {
			log.error("Error occurred while displaying invitees lists" + e.getMessage());
		}
		
	}
	
    /**
     * This method checks and change the status of member searched by business if allready invited or in invitee list 
     */
    public void checkForAllreadyExistMembers() {
    	if(CollectionUtils.isNotEmpty(personWrappers)) {
			for(BusinessDirectorySearchResults.BusinessDirectorySearchResult userList : personWrappers){
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
     * For getting business list
     */
	public void getDirectoryList() {
		SpaceInvitationManager spaceInvitationManager = (SpaceInvitationManager)getSessionAttribute("spaceInvitationWizard");
		Invitee lastInvitee = (Invitee) getSessionAttribute("lastInvitee");
		PageContextManager pageContextManager = (PageContextManager) getSessionAttribute("pageContextManager");
		User user = SessionManager.getUser();
		if (user == null) {
			user = new User();
		}
		lastInvitee = new Invitee();
		setSessionAttribute("lastInvitee", lastInvitee);

		// spaceInvitationManager.clear();
		spaceInvitationManager.setSpace(user.getCurrentSpace());
		spaceInvitationManager.setUser(user);

		// Set invitation URls for each kind of inviation (accept required / auto-accept)
		spaceInvitationManager.setInvitationAcceptRequiredURL(pageContextManager.getProperty("space.invite.acceptrequired.url"));
		spaceInvitationManager.setInvitationAutoAcceptURL(pageContextManager.getProperty("space.invite.autoaccept.url"));
		
		try {
			if(user.getCurrentSpace().isTypeOf(ISpaceTypes.APPLICATION_SPACE)){
				spaceInvitationManager.setHasLoadedDirectories(true);
			}
			showDirectory = spaceInvitationManager.hasDirectories();
			List<PnBusiness> directoryNameOptions = spaceInvitationManager.getDirectoryOptionLists(false);
			
			// Set owner business selected defaultly 
		 	if (StringUtils.isEmpty(directoryId) && CollectionUtils.isNotEmpty(directoryNameOptions)) { 
		 		String owningBusinessName = StringUtils.EMPTY;
	 			IPnBusinessSpaceService businessSpaceService = ServiceFactory.getInstance().getPnBusinessSpaceService();
		 		if(user.getCurrentSpace().isTypeOf(ISpaceTypes.PROJECT_SPACE)) {
		 			PnBusiness pnBusiness = businessSpaceService.getBusinessByProjectId(Integer.valueOf(user.getCurrentSpace().getID()));
		 			if(pnBusiness != null){
	 		 			owningBusinessName = pnBusiness.getBusinessName(); 
		 			}
		 		} else if(user.getCurrentSpace().isTypeOf(ISpaceTypes.BUSINESS_SPACE)) {
		 			if(!businessSpaceService.isRootBusines(Integer.valueOf(user.getCurrentSpace().getID())))
		 				owningBusinessName = businessSpaceService.getParentBusinessByBusinessId(Integer.valueOf(user.getCurrentSpace().getID())).getBusinessName();
		 		}
	 			for (PnBusiness business : directoryNameOptions) { 
	 				if (business.getBusinessName().equals(owningBusinessName)) { 
	 					directoryId = business.getBusinessId().toString(); 
	 				} 
	 			} 
		 	} 
			directoryBeanModel = new GenericSelectModel<PnBusiness>(directoryNameOptions, PnBusiness.class, "businessName","businessId", access);
		} catch (DirectoryException e) {
			log.error("Error occurred while generating directory lists" + e.getMessage());
		} catch (PersistenceException e) {
			log.error("Error occurred while generating directory lists" + e.getMessage());
		}
	}
	
	/**
	 * Load team members details of current project
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
	 * Sending invitation to invited user list users.
	 * @return
	 * @throws PersistenceException
	 */
	void sendInvitation() {
		License license = (License) getSessionAttribute("license");
		if(license == null){
			license = new License();
		}
		LicenseInvitationManager invitationManager = new LicenseInvitationManager();
	    User user =SessionManager.getUser();
	    invitationManager.setSpace(user.getCurrentSpace());
		invitationManager.setUser(user);
	}
	
	/**
	 * Checking is invitee alreadt invited
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
	 * Submit form for sending invitation.
	 * @return
	 */
	Object onSubmitFromSendInvitations(){
		if(StringUtils.isNotEmpty(sendInvitationDetails)){
			JSONObject jSONObject = new JSONObject(sendInvitationDetails);
			if(jSONObject.getString("isSendMail") != null)
				sendToAll = Boolean.valueOf(jSONObject.getString("isSendMail"));
			if(jSONObject.getString("additionalComment") != null)
				additionalMessage = jSONObject.getString("additionalComment");
			sendInvitation();
		}
		return addResponsibility;
	}
	
	/**
	 * @return the actionsIconEnabled
	 */
	public boolean isActionsIconEnabled() {
		return PropertyProvider.getBoolean("prm.global.actions.icon.isenabled");
	}

	/**
	 * @return the blogEnabled
	 */
	public boolean isBlogEnabled() {
		return PropertyProvider.getBoolean("prm.blog.isenabled") && getUser().getCurrentSpace().isTypeOf(SpaceTypes.PERSONAL_SPACE)
				|| getUser().getCurrentSpace().isTypeOf(SpaceTypes.PROJECT_SPACE);
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
	 * @return the deletedList
	 */
	public List<String> getDeletedList() {
		  if(deletedList == null){
              deletedList = new ArrayList<String>();
      } 
		return deletedList;
	}

	/**
	 * @param deletedList the deletedList to set
	 */
	public void setDeletedList(List<String> deletedList) {
		this.deletedList = deletedList;
	}
	
	/**
	 * Get value for send invitation
	 * @return
	 */
	public boolean getsendToAllInvitees() {
		return sendToAll;
	}
	
	/**
	 * Get comment to send with mail
	 * @return
	 */
	public String getAdditionalComment() {
		return additionalMessage;
	}
	
	/**
	 * Clear persisted value
	 */
	public void clearPersistedValues() {
		if(CollectionUtils.isNotEmpty(personWrappers)) {
			personWrappers.clear();
		}
		divHeight = 0;
    	divScroll = "hidden";
    	height = "50";
    	userListCssStyle = "userlisttype";
    	sendToAll = false;
    	additionalMessage = "";
    	searchUser = "";
	}
	/**
	 * Get Invitee Div Height
	 * @return
	 */
	public String getInviteeDivHeight() {
		if(CollectionUtils.isNotEmpty(inviteesList))
			return (inviteesList.size() > 10 ? "225px" : (inviteesList.size() * 25)+5+"px");
		else 
			return "35px";
	}
	
	/**
	 * Get perticipant div height
	 * @return
	 */
	public String getParticipantsDivHeight() {
		if(CollectionUtils.isNotEmpty(teamMembers))
			return (teamMembers.size() > 10 ? "225px" : (teamMembers.size() * 25)+"px");
		else 
			return "25px";
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
	 * To identify current space is project space
	 * @return boolean
	 */
	public boolean isProjectSpace(){
		return getUser().getCurrentSpace().getType().equalsIgnoreCase(Space.PROJECT_SPACE);
	}

	/**
	 * To get ProjectSpace
	 * @return ProjectSpaceBean
	 */
	public ProjectSpaceBean getProjectSpaceBean(){
		return (ProjectSpaceBean) getUser().getCurrentSpace();
	}
	
	@CleanupRender
	public void resetDirectoryId(){
		directoryId = null;	
	} 
}
