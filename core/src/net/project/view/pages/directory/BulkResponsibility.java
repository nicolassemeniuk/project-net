package net.project.view.pages.directory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import net.project.base.EventException;
import net.project.base.EventFactory;
import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.property.PropertyProvider;
import net.project.directory.GroupWrapper;
import net.project.directory.ProjectListWrapper;
import net.project.events.EventType;
import net.project.resource.Invitee;
import net.project.resource.SpaceInvitationManager;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.space.SpaceTypes;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;

public class BulkResponsibility {

	private static Logger log;
	
	@Property
	@Persist
	private ArrayList<ProjectListWrapper> selectedProjects;
	
	@Property
	private ProjectListWrapper projectListWrapper;
	
	@Property
	@InjectPage
	private ManageDirectoryInviteMember manageDirectoryInviteMember;
	
	@Property
	@InjectPage
	private BulkInvitation bulkInvitation;
	
	@Property
	@InjectPage
	private BasePage page;
	
	@Property
	private GroupWrapper groupWrapper;
	
	@Property
	private Invitee invitee;
	
	@Property
	private String manageTabWidth;
	
	@Property
	private Integer moduleId;
	
	private ArrayList<GroupWrapper> userRoleList;
	
	Object onActivate(){
    	Object isValidAccess;
    	isValidAccess = page.checkForUser();
    	if(isValidAccess != null){
    		return isValidAccess;
    	}
		log = Logger.getLogger(BulkResponsibility.class);
		moduleId = Module.DIRECTORY;
		selectedProjects = manageDirectoryInviteMember.getSelectedProjectsList();
		if(selectedProjects.size() < 4){
			for(ProjectListWrapper listWrapper : selectedProjects){
				if(listWrapper.getProjectName().length() < 15){
					listWrapper.setManageTabWidth("10%");
				}else if(listWrapper.getProjectName().length() > 15 && listWrapper.getProjectName().length() < 20){
					listWrapper.setManageTabWidth("20%");
				}else if(listWrapper.getProjectName().length() > 20 && listWrapper.getProjectName().length() < 25){
					listWrapper.setManageTabWidth("30%");
				}else if(listWrapper.getProjectName().length() > 25 && listWrapper.getProjectName().length() < 30){
					listWrapper.setManageTabWidth("40%");
				}
			}
		}else if(selectedProjects.size() > 4){
			for(ProjectListWrapper listWrapper : selectedProjects){
				listWrapper.setManageTabWidth( "10%");
			}
		}
		return null;
	}
	
	Object onAction() {
		commitChanges();
		URL url = null;
		try {
			url = new URL(SessionManager.getAppURL() + "/project/DirectorySetup.jsp?module=" + Module.DIRECTORY);
		} catch (MalformedURLException e) {
			log.error("Error occurred in onAction : " + e.getMessage());
		} catch (Exception e) {
			log.error("Error occurred in onAction : " + e.getMessage());
		}
		return url;
	}
	
	/**
	 * This method saves the roles and responsibility and sends a mail to invite member
	 */
	private void commitChanges(){
		SpaceInvitationManager spaceInvitationWizard = (SpaceInvitationManager) page.getSessionAttribute("spaceInvitationWizard");
		Iterator projectList = selectedProjects.iterator();
		while (projectList.hasNext()) {
			try {
				projectListWrapper = (ProjectListWrapper) projectList.next();
				Space space = SpaceFactory.constructSpaceFromID(projectListWrapper.getProjectId());
				spaceInvitationWizard.setSpace(space);
				spaceInvitationWizard.setUser(SessionManager.getUser());
				Iterator iterator = projectListWrapper.getInviteesList().iterator();
				while (iterator.hasNext()) {
					Invitee invitee = (Invitee) iterator.next();
					if (page.getRequestParameter(projectListWrapper.getProjectId() + "resp_" + invitee.getEmail()) != null) {
						invitee.setResponsibilities((String) page.getRequestParameter(projectListWrapper.getProjectId()
								+ "resp_" + invitee.getEmail()));
					}
					if (page.getRequestParameter(projectListWrapper.getProjectId() + "msg_" + invitee.getEmail()) != null) {
						invitee.setTitle((String) page.getRequestParameter(projectListWrapper.getProjectId() + "msg_"
								+ invitee.getEmail()));
					}
					if (page.getRequestParameter(projectListWrapper.getProjectId() + "_userRolescheckListId") != null) {
						String groupIds = (String) page.getRequestParameter(projectListWrapper.getProjectId()
								+ "_userRolescheckListId");
						String[] role = groupIds.split(",");
						groupIds = "";
							for (int index = 0; index < role.length; index++) {
								if (role[index].contains(invitee.getEmail())) {
									groupIds += role[index].split("-")[0] + ",";
								}
							}
							String[] roleIds = groupIds.split(",");
							ArrayList<String> roleList = new ArrayList<String>();
								for (int index = 0; index < roleIds.length ; index++) {
									if (StringUtils.isNotEmpty(roleIds[index]) && roleIds[index].substring(0, roleIds[index].indexOf("_")).equals(projectListWrapper.getProjectId())) {
										roleList.add(roleIds[index].substring(roleIds[index].indexOf("_") + 1, roleIds[index].length()));
									}
								}
							String[] onlyRoles = new String[roleList.size()];
							if(CollectionUtils.isNotEmpty(roleList)){
								onlyRoles = roleList.toArray(onlyRoles);
							}
							invitee.setRoles(onlyRoles);
						}
						invitee.setSpace(space);
				}
				spaceInvitationWizard.addAllMembers(projectListWrapper.getInviteesList());
				spaceInvitationWizard.setSendNotifications(bulkInvitation.getsendToAllInvitees());
				spaceInvitationWizard.setInviteeMessage(bulkInvitation.getAdditionalComment());
				spaceInvitationWizard.commit();
				
				//publishing event to asynchronous queue
		        try {
		        	net.project.events.ProjectEvent projectEvent = (net.project.events.ProjectEvent) EventFactory.getEvent(ObjectType.PROJECT, EventType.MEMBER_ADDED_TO_SPACE);
		        	projectEvent.setObjectID(space.getID());
		        	projectEvent.setSpaceID(space.getID());
		        	projectEvent.setObjectType(ObjectType.PROJECT);
		        	projectEvent.setName(SessionManager.getUser().getCurrentSpace().getName());
		        	projectEvent.setObjectRecordStatus("A");
		        	projectEvent.setNoOfMembers(projectListWrapper.getInviteesList().size());
		        	projectEvent.publish();
				} catch (EventException e) {
					Logger.getLogger(BulkResponsibility.class).error("BulkResponsibility.commitChanges() :: Member Added To Project Event Publishing Failed!", e);
				}
			} catch (Exception e) {
				log.error("Error occurred while displaying invitees lists" + e.getMessage());
			}
		}
		// after sending invitation remove invitee from invitee list.
		bulkInvitation.clearPersistedValues();
		spaceInvitationWizard.clear();
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
				&& page.getUser().getCurrentSpace().isTypeOf(SpaceTypes.PERSONAL_SPACE)
				|| page.getUser().getCurrentSpace().isTypeOf(SpaceTypes.PROJECT_SPACE);
	}

	/**
	 * @return the userRoleList
	 */
	public ArrayList<GroupWrapper> getUserRoleList() {
		return userRoleList;
	}

	/**
	 * @param userRoleList the userRoleList to set
	 */
	public void setUserRoleList(ArrayList<GroupWrapper> userRoleList) {
		this.userRoleList = userRoleList;
	}

}
