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
import java.util.List;

import net.project.base.Module;
import net.project.base.directory.DirectoryException;
import net.project.base.directory.search.AbstractSearchResult;
import net.project.base.property.PropertyProvider;
import net.project.business.BusinessDirectorySearchResults;
import net.project.directory.GroupWrapper;
import net.project.directory.ManageDirectoryHelper;
import net.project.directory.ProjectListWrapper;
import net.project.hibernate.model.PnBusiness;
import net.project.license.License;
import net.project.persistence.PersistenceException;
import net.project.resource.Invitee;
import net.project.resource.LicenseInvitationManager;
import net.project.resource.SpaceInvitationManager;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.session.PageContextManager;
import net.project.space.SpaceTypes;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;
import net.project.view.pages.resource.management.GenericSelectModel;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

/**
 * For invite member in Manage Directories
 */
public class BulkInvitation extends BasePage{
	
	private static Logger log;
	
	@Inject
	private PropertyAccess access;
	
	@Property
	@Persist
	private GenericSelectModel<PnBusiness> directoryBeanModel;
	
	@Property
	private GenericSelectModel<PnBusiness> projectBeanModel;
	
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
	@Persist
	private String roleId;
	
	@Property
	private AbstractSearchResult personWrapper;
	
	@Persist
    private String currentSpaceId;
	
	@Persist
	private boolean delete; 
	
	@InjectPage
	private BulkResponsibility bulkResponsibility;
	
	@InjectPage
	private ManageDirectoryInviteMember manageDirectoryInviteMember;
	
	@Persist
	private List<String> deletedList;
	
	@Property
	@Persist
	private boolean sendToAll;
	
	@Property
	@Persist
	private String additionalMessage;
	
	@Property
	@Persist
	private String selectedChkBoxIds;
	
	@Property
	@Persist
	private String selectedRoleIds;
	
	@Property
	private String addNewInviteeCaption;
	
	@Property
	private String errorReporter;
	
	@Property
	@Persist
	private ArrayList<ProjectListWrapper> selectedProjects;
	
	@Property 
	private PnBusiness business;
	
	@Property
	private GroupWrapper userRole;
	
	@Property
	private String defaultProjectValue;
	
	@Property 
	private String sendInvitationDetails;
	
	@Property 
	private String selectedProjectList;
	
	@Property 
	private String removeProjectId;
	
	@Property
	private Integer moduleId;
	
	@Property
	@Persist
	private String totalMembersSelctedAsPerProject;
	
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
			log = Logger.getLogger(BulkInvitation.class);
			addNewInviteeCaption = PropertyProvider.get("prm.directory.bulkinvitation.submitbuttontitle.header");
			moduleId = Module.DIRECTORY;
			// check the previous currentSpaceId with latest one
			if (StringUtils.isEmpty(currentSpaceId))
				currentSpaceId = SessionManager.getUser().getCurrentSpace().getID();

			if (getSessionAttribute("setDefaultsForMemberInvitation") != null) {
				clearPersistedValues(); // clear list if space id is changed
				currentSpaceId = SessionManager.getUser().getCurrentSpace().getID();
			}
			getDirectoryList();
			getProjectLists();
		} catch (Exception e) {
			log.error("Error occurred while displaying invitees lists" + e.getMessage());
		}
	}
	
	/**
	 * Get all business of selected user.
	 * 
	 * @throws PersistenceException
	 * @throws DirectoryException
	 */
	public void getDirectoryList(){
		try {
			SpaceInvitationManager spaceInvitationManager = (SpaceInvitationManager) getSessionAttribute("spaceInvitationWizard");
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
			spaceInvitationManager.setInvitationAcceptRequiredURL(pageContextManager
					.getProperty("space.invite.acceptrequired.url"));
			spaceInvitationManager.setInvitationAutoAcceptURL(pageContextManager
					.getProperty("space.invite.autoaccept.url"));
			List<PnBusiness> directoryNameOptions = spaceInvitationManager.getDirectoryOptionLists(true);
			if (StringUtils.isEmpty(directoryId)) {
				for (PnBusiness business : directoryNameOptions) {
					if (business.getBusinessName().equals(SessionManager.getUser().getCurrentSpace().getName())) {
						directoryId = business.getBusinessId().toString();
					}
				}
			}
			directoryBeanModel = new GenericSelectModel<PnBusiness>(directoryNameOptions, PnBusiness.class,
					"businessName", "businessId", access);
		} catch (DirectoryException e) {
			log.error("Error occurred while getting directory list :" + e.getMessage());
		} catch (PersistenceException e) {
			log.error("Error occurred while getting directory list :" + e.getMessage());
		}
	}
	
	/**
	 * Get list of project in current selected business.
	 */
	private void getProjectLists() {
		projectBeanModel = new GenericSelectModel<PnBusiness>(ManageDirectoryHelper.getProjectLists(currentSpaceId, getUser(), net.project.security.Action.CREATE), PnBusiness.class, "businessName","businessId", access);
	}
	
	/**
	 * Submit for send invitation
	 * @return
	 */
	Object onSubmitFromSendInvitations() {
		if (StringUtils.isNotEmpty(sendInvitationDetails)) {
			JSONObject jSONObject = new JSONObject(sendInvitationDetails);
			if (jSONObject.getString("isSendMail") != null)
				sendToAll = Boolean.valueOf(jSONObject.getString("isSendMail"));
			if (jSONObject.getString("additionalComment") != null)
				additionalMessage = jSONObject.getString("additionalComment");
			selectedProjectsList(selectedProjectList);
			sendInvitation();
		}
		return bulkResponsibility;
	}
				
	/**
	 * Sending invitation to invited user list users.
	 * @return bulk resposibility page.
	 * @throws PersistenceException
	 */
	void sendInvitation() {
		License license = (License) getSessionAttribute("license");
		if (license == null) {
			license = new License();
		}
		LicenseInvitationManager invitationManager = new LicenseInvitationManager();
		User user = SessionManager.getUser();
		invitationManager.setSpace(user.getCurrentSpace());
		invitationManager.setUser(user);

		if (CollectionUtils.isNotEmpty(manageDirectoryInviteMember.getSelectedProjectsList())) {
			for (int index = 0; index < manageDirectoryInviteMember.getSelectedProjectsList().size(); index++) {
				if (CollectionUtils.isNotEmpty(manageDirectoryInviteMember.getSelectedProjectsList().get(index)
						.getUserRoles())) {
					ArrayList<GroupWrapper> list = new ArrayList<GroupWrapper>();
					list = manageDirectoryInviteMember.getSelectedProjectsList().get(index).getUserRoles();
					if (list.get(0).getGroupName().equals("Team Member")) {
						list.remove(0);
					}
					bulkResponsibility.setUserRoleList(list);
				}
			}
		}
	}
	
	/**
	 * For each invitee, add roles and others information.
	 * @param projectWrapper
	 * @param roleId
	 * @param memberID
	 */
	public void addMemberWithRole(ProjectListWrapper projectWrapper, String roleId, String memberID) {
		Invitee invitee = new Invitee();
		if (CollectionUtils.isNotEmpty(manageDirectoryInviteMember.getMemberLists())) {
			for (AbstractSearchResult personWrapper : manageDirectoryInviteMember
					.getMemberLists()) {
				if (personWrapper.getEmail().equals(memberID)) {
					invitee.setID(personWrapper.getPersonId());
					invitee.setFirstName(personWrapper.getFirstName());
					invitee.setLastName(personWrapper.getLastName());
					invitee.setEmail(personWrapper.getEmail());
					invitee.setDisplayName(personWrapper.getFirstName() + "" + personWrapper.getLastName());
					invitee.setOnline(personWrapper.getOnline());
					if (roleId != "0") {
						invitee.setRoles(roleId.split(","));
					}
					projectWrapper.setInviteesList(invitee);
					break;
				}
			}
		}
	}
	
	/**
	 * Set roles,selected members for each project
	 * from json object.
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
		if (CollectionUtils.isNotEmpty(manageDirectoryInviteMember.getSelectedProjectsList())) {
			for (ProjectListWrapper projectWrapper : manageDirectoryInviteMember.getSelectedProjectsList()) {
				if (jSONObject != null && jSONObject.has(projectWrapper.getProjectId())) {
					JSONArray array = jSONObject.getJSONArray(projectWrapper.getProjectId());
					for (int index = 0; index < array.length(); index++) {
						JSONObject jobject = array.getJSONObject(index);
						addMemberWithRole(projectWrapper, jobject.getString("selectedrole"), jobject
								.getString("memberId"));
					}
				}
			}
		}
	}
	
	/**
	 * Clear the list on space changes or invitation completion
	 */
	public void clearPersistedValues(){
		additionalMessage = "";
		sendToAll = false;
		projectOrSubbusinessId = "";
	}
	
	/**
	 * Set previous selected values.
	 */
	public void setPreviousSelectedValues(){
		if(StringUtils.isNotEmpty(defaultProjectValue)){
			JSONObject obj = new JSONObject(defaultProjectValue);
			projectOrSubbusinessId = obj.getString("projectOrSubbusinessId");
			selectedChkBoxIds = obj.getString("selectedChkBoxIds") != null ? obj.getString("selectedChkBoxIds").toString() : "";
			selectedRoleIds = obj.getString("selectedRoleIds") != null ?  obj.getString("selectedRoleIds").toString() : "";
			totalMembersSelctedAsPerProject = obj.getString("countAsPerProject") != null ?  obj.getString("countAsPerProject").toString() : "";
		}
	}
	
	/**
	 * @return comma separated project id's
	 */
	public String getProjectIds(){
		String projectIds = "";
		if(CollectionUtils.isNotEmpty(manageDirectoryInviteMember.getSelectedProjectsList())) {
			for (ProjectListWrapper projectWrapper : manageDirectoryInviteMember.getSelectedProjectsList()) {
				projectIds += projectWrapper.getProjectId() + ",";
			}
		}
		return projectIds;
	}
	
	/**
	 * @return check for sending invitees
	 */
	public boolean getsendToAllInvitees() {
		return sendToAll;
	}
	
	/**
	 * @return check for additional comment
	 */
	public String getAdditionalComment() {
		return additionalMessage;
	}
	
	/**
	 * Get added space count
	 * @return
	 */
	public int getAddedObjectCount() {
		return CollectionUtils.isNotEmpty(manageDirectoryInviteMember.getSelectedProjectsList()) ? manageDirectoryInviteMember.getSelectedProjectsList().size() : 0;
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
		return PropertyProvider.getBoolean("prm.blog.isenabled")
				&& getUser().getCurrentSpace().isTypeOf(SpaceTypes.PERSONAL_SPACE)
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
}