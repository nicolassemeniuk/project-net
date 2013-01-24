/**
 * 
 */
package net.project.view.pages.directory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.project.base.EventException;
import net.project.base.EventFactory;
import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.property.PropertyProvider;
import net.project.directory.GroupWrapper;
import net.project.events.EventType;
import net.project.hibernate.model.PnChargeCode;
import net.project.hibernate.model.PnObjectHasChargeCode;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.service.ServiceFactory;
import net.project.project.ProjectSpaceBean;
import net.project.resource.Invitee;
import net.project.resource.SpaceInvitationManager;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.space.SpaceTypes;
import net.project.util.StringUtils;
import net.project.util.Version;
import net.project.view.pages.base.BasePage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;

public class AddResponsibility extends BasePage {

	private static Logger log;
	
	@Property
	private String versionNumber; 
	
	@Property
	@Persist
	private ArrayList<Invitee> inviteesList;
	
	@Property
	@Persist
	private Invitee invitees;
	
	@Property
	@Persist
	private ArrayList<GroupWrapper> userRoles;
	
	@Property
	@Persist
	private GroupWrapper userRole;
	
	@Persist
	private String userRoleCheckList;
	
	@Property
	@Persist
	private String responsibilities;
	
	@Property
	@Persist
	private String message;
	
	@Property
	@Persist
	private String[] roles;
	
	@Property
	@Persist
	private boolean checkgroup; 
	
	@InjectPage
	private InviteMember inviteMember;
	
	@Property
	@InjectPage
	private BasePage page;
	
	@Property
	private int inviteeListSize;
	
	private Integer spaceId;

	@Property
	private List<PnChargeCode> chargeCodeList = null;
		
	private List<PnObjectHasChargeCode> objectHasChargeCodeList = null;
	
	/**
	 * 
	 *
	 */	
	Object onActivate(){
    	Object isValidAccess;
    	isValidAccess = page.checkForUser();
    	// check permission of user to access 
    	if(isValidAccess == null){
    		isValidAccess = page.checkAccess(page.getUser().getCurrentSpace().getID(), net.project.base.Module.DIRECTORY, net.project.security.Action.CREATE);
    	}
		log = Logger.getLogger(AddResponsibility.class);
		versionNumber = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
		userRoles = new ArrayList<GroupWrapper>();
		spaceId = Integer.valueOf(page.getUser().getCurrentSpace().getID());

		if(page.getUser().getCurrentSpace().getSpaceType().equals(SpaceTypes.BUSINESS) && page.getPnBusinessSpaceService().isRootBusines(spaceId)){
			chargeCodeList = page.getPnChargeCodeService().getChargeCodeByBusinessId(spaceId);
		} else if (page.getUser().getCurrentSpace().getSpaceType().equals(SpaceTypes.BUSINESS)){
			chargeCodeList = page.getPnChargeCodeService().getRootBusinessChargeCodeBySubBusinessId(spaceId);
			objectHasChargeCodeList = page.getPnObjectHasChargeCodeService().getChargeCodeAssignedPersonFromParentBusiness(spaceId, "business");
		} else if (page.getUser().getCurrentSpace().getSpaceType().equals(SpaceTypes.PROJECT) && page.getPnProjectSpaceService().isRootProject(spaceId)){
			chargeCodeList = page.getPnChargeCodeService().getChargeCodeByProjectId(spaceId);
			objectHasChargeCodeList = page.getPnObjectHasChargeCodeService().getChargeCodeAssignedPersonFromParentBusiness(spaceId, "project");
		} else{
			chargeCodeList = page.getPnChargeCodeService().getChargeCodeByProjectId(spaceId);
			objectHasChargeCodeList = page.getPnObjectHasChargeCodeService().getChargeCodeAssignedPersonFromParentProject(spaceId);
		}

		displayInvitiesList();
		getAllRoleOptionList();
		return isValidAccess;
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
	 * This method saves the roll and responsibility and sends a mail to invited members    
	 * @return
	 */
	public boolean commitChanges() {
		SpaceInvitationManager spaceInvitationWizard = (SpaceInvitationManager) page.getSessionAttribute("spaceInvitationWizard");
	    if(spaceInvitationWizard != null ) {
			try{ 
		    	Iterator iterator = inviteesList.iterator();
		    	int cnt=0;
				while(iterator.hasNext()) {
			       	try{
			       		Invitee invitee = (Invitee)iterator.next();
			       		if(page.getRequestParameter("resp_"+invitee.getEmail()) != null){
			       			invitee.setResponsibilities((String)page.getRequestParameter("resp_"+invitee.getEmail()));
			       		}
			       		
			       		if(page.getRequestParameter("msg_"+invitee.getEmail()) != null){
			       			invitee.setTitle((String)page.getRequestParameter("msg_"+invitee.getEmail()));
			       		}
			       		if(page.getRequestParameter("userRolescheckListId") != null){
			       			String groupIds = (String)page.getRequestParameter("userRolescheckListId");
			       			String[] role = groupIds.split(",");
			       			groupIds = "";
			       			for(int index = 0; index < role.length; index++) {
			       				if(role[index].contains(invitee.getEmail())) {
			       					groupIds += role[index].split("-")[0]+",";
			       				}
			       			}
			       			if(groupIds != ""){
			       				invitee.setRoles(groupIds.split(","));
			       			}
			       		}
			       		invitee.setSpace(SessionManager.getUser().getCurrentSpace());

			       		String chargeCodeId = page.getRequestParameter("chargecode_"+invitee.getEmail());
			       		if(StringUtils.isNotEmpty(chargeCodeId))
			       			page.getPnObjectHasChargeCodeService().save(Integer.valueOf(invitee.getID()), Integer.valueOf(chargeCodeId), spaceId);
						
			      	}catch(Exception e){
			      		log.error("Error occurred while displaying invitees lists" + e.getMessage());
			      	}
				}
				spaceInvitationWizard.addAllMembers(inviteesList);
				spaceInvitationWizard.setSendNotifications(inviteMember.getsendToAllInvitees());
				spaceInvitationWizard.setInviteeMessage(inviteMember.getAdditionalComment());
				spaceInvitationWizard.commit();
				
				//publishing event to asynchronous queue
		        try {
		        	net.project.events.ProjectEvent projectEvent = (net.project.events.ProjectEvent) EventFactory.getEvent(ObjectType.PROJECT, EventType.MEMBER_ADDED_TO_SPACE);
		        	projectEvent.setObjectID(SessionManager.getUser().getCurrentSpace().getID());
		        	projectEvent.setSpaceID(SessionManager.getUser().getCurrentSpace().getID());
		        	projectEvent.setObjectType(ObjectType.PROJECT);
		        	projectEvent.setName(SessionManager.getUser().getCurrentSpace().getName());
		        	projectEvent.setObjectRecordStatus("A");
		        	projectEvent.setNoOfMembers(inviteesList.size());
		        	projectEvent.publish();
				} catch (EventException e) {
					Logger.getLogger(AddResponsibility.class).error("AddResponsibility.commitChanges() :: Member Added To Project Event Publishing Failed!", e);
				}
				// after sending invitation remove invitee from invitee list.
			} catch(Exception e){
				log.error("Error occurred while setting roles or responsibility or sending mail" + e.getMessage());        
			}
			inviteMember.clearPersistedValues();
			spaceInvitationWizard.clear();
		}
		return true;
	}
	
	/**
	 * Displaying invities list and set the online status.
	 */
	private void displayInvitiesList(){
		SpaceInvitationManager spaceInvitationWizard = (SpaceInvitationManager)page.getSessionAttribute("spaceInvitationWizard");
		if(spaceInvitationWizard != null){
			net.project.resource.InviteeList inviteeList =  spaceInvitationWizard.getInviteeList();
			List<Teammate> onlineTeammates = ServiceFactory.getInstance().getPnPersonService().getOnlineMembers();
			Iterator iterator = inviteeList.iterator();
			inviteesList = new ArrayList<Invitee>();
			while(iterator.hasNext()){
		       	try{
		       		Invitee invitee = (Invitee)iterator.next();
		       		if(invitee.getID() != null) {
			       		for(int index = 0; index < onlineTeammates.size() ;index++ ){
							if(invitee.getID().equals(onlineTeammates.get(index).getPersonId().toString())){
								invitee.setOnline(true);
								break;
							}
						}
			       		invitee.setChargeCodeId(getInviteeDefaultChargeCodeFromParent(invitee.getID()));
		       		}
		       		inviteesList.add(invitee);
		      	}catch(Exception e){
		      		log.error("Error occurred while displaying invitees lists" + e.getMessage());
		      	}
			}
			
			if(CollectionUtils.isNotEmpty(inviteesList)) {
				inviteeListSize = inviteesList.size();
			}
		}
	}
	
	/**
	 * Displaying all role list.
	 */
	private void getAllRoleOptionList(){
		try {
			SpaceInvitationManager spaceInvitationWizard = (SpaceInvitationManager) page.getSessionAttribute("spaceInvitationWizard");
			ArrayList<GroupWrapper> group = new ArrayList<GroupWrapper>();
			group = spaceInvitationWizard.getRoleOptionLists(spaceInvitationWizard.getAssignedRoles());
			Iterator iterator = group.iterator();
			while (iterator.hasNext()) {
				GroupWrapper groupWrapper = (GroupWrapper) iterator.next();
				userRoles.add(new GroupWrapper(groupWrapper.getGroupId(), groupWrapper.getGroupName(), false));
			}
			// Adding default role Team Member in list
			userRoles.add(new GroupWrapper("0", PropertyProvider.get("prm.directory.directorypage.roster.column.teammember"), true));
			Collections.sort(userRoles);
		} catch (Exception e) {
			log.error("Error occurred while displaying role lists" + e.getMessage());
		}
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
	
	public String getInviteesDivHeight() {
		if(CollectionUtils.isNotEmpty(inviteesList))
			return (inviteesList.size() > 12 ) ? "auto" : "350px";
		else 
			return "310px";
	}
	
	/**
	 * To get the invitee's charge code assigned in parent space of current space.
	 * if current space is project and there is not parent project then
	 *  charge code assigned in parent business will be returned.
	 * @return charge code id of invitee
	 */
	public String getInviteeDefaultChargeCodeFromParent(String inviteeId){
		String chargeCodeId = "";
		if(CollectionUtils.isNotEmpty(objectHasChargeCodeList)){
			for(PnObjectHasChargeCode chargeCode : objectHasChargeCodeList){
				if(inviteeId.equals(chargeCode.getComp_id().getObjectId().toString())){
					chargeCodeId = chargeCode.getPnChargeCode().getCodeId().toString();
					break;
				}
			}
		}
		return chargeCodeId;
	}
	
	public boolean isChargeCodeAvailable(){
		return CollectionUtils.isNotEmpty(chargeCodeList);
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

	/**
     * To check whether charge code is enabled.
     * @return boolean
     */
	public boolean isChargeCodeEnabled() {
		return PropertyProvider.getBoolean("prm.global.business.managechargecode.isenabled");
	}
}
