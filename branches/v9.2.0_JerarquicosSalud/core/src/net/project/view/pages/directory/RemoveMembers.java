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

import java.util.List;

import net.project.base.Module;
import net.project.base.directory.DirectoryException;
import net.project.base.property.PropertyProvider;
import net.project.directory.ManageDirectoryHelper;
import net.project.hibernate.model.PnBusiness;
import net.project.persistence.PersistenceException;
import net.project.resource.SpaceInvitationManager;
import net.project.security.SessionManager;
import net.project.space.SpaceTypes;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;
import net.project.view.pages.resource.management.GenericSelectModel;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;

/**
 * This class used to remove members from projects or subbusinesses
 */
public class RemoveMembers extends BasePage{
	
	private static Logger log;
	
	@Persist
    private String currentSpaceId;
	
	@Property
	private GenericSelectModel<PnBusiness> projectBeanModel;
	
	@Inject
	private PropertyAccess access;
	
	@Property	
	@Persist
	private String directoryId;
	
	@Property
	@Persist
	private String searchUser;
	
	@Property 
	private PnBusiness business;
	
	@Property
	private String defaultProjectValue;
	
	@Property 
	private String removeUncheckedMemberCaption;
	
	@Property
	private Integer moduleId;
		
	@Property
	@Persist
	private boolean isSearchMemberFound;
	
	@Property
	@Persist
	private String projectOrSubbusinessId;
	
	@InjectPage
	private ManageDirectoryRemoveMember manageDirectoryRemoveMember;
	
	/**
	 * Getting directory business list.
	 */
    Object onActivate(){
    	Object isValidAccess = checkForUser();
		//check permission of user to access 
    	if(isValidAccess == null){
    		isValidAccess = checkAccess(getUser().getCurrentSpace().getID(), net.project.base.Module.DIRECTORY, net.project.security.Action.DELETE);
    	}
		initiliazeValues();
		return isValidAccess;
	}
    
    /**
	 * Initialize the lists 
	 *
	 */
    public void initiliazeValues(){
    	log = Logger.getLogger(BulkInvitation.class);
    	removeUncheckedMemberCaption = PropertyProvider.get("prm.directory.managedirectory.removemember.submitbutton.label");
    	moduleId = Module.DIRECTORY;
    	isSearchMemberFound = true;
    	if(StringUtils.isEmpty(currentSpaceId))
			currentSpaceId = SessionManager.getUser().getCurrentSpace().getID();
    	if(getSessionAttribute("setDefaultsForRemoveMember") != null) {
    		currentSpaceId = SessionManager.getUser().getCurrentSpace().getID();
    	}
    	getDirectoryList();
    	projectBeanModel = new GenericSelectModel<PnBusiness>(ManageDirectoryHelper.getProjectLists(currentSpaceId, getUser(), net.project.security.Action.DELETE), PnBusiness.class, "businessName", "businessId", access);
    	manageDirectoryRemoveMember.addEqualRowsForAllSpaces(false);
    }
    
	/**
	 * Get all businesses of selected user
	 * 
	 * @throws PersistenceException
	 * @throws DirectoryException
	 */
	public void getDirectoryList() {
		try {
			SpaceInvitationManager spaceInvitationManager = (SpaceInvitationManager) getSessionAttribute("spaceInvitationWizard");
			spaceInvitationManager.setSpace(getUser().getCurrentSpace());
			spaceInvitationManager.setUser(getUser());
			List<PnBusiness> directoryNameOptions = spaceInvitationManager.getDirectoryOptionLists(true);
			if (StringUtils.isEmpty(directoryId)) {
				for (PnBusiness business : directoryNameOptions) {
					if (business.getBusinessName().equals(SessionManager.getUser().getCurrentSpace().getName())) {
						directoryId = business.getBusinessId().toString();
					}
				}
			}
		} catch (DirectoryException e) {
			log.error("Error occurred while getting directory list :" + e.getMessage());
		} catch (PersistenceException e) {
			log.error("Error occurred while getting directory list :" + e.getMessage());
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
				&& getUser().getCurrentSpace().isTypeOf(SpaceTypes.PERSONAL_SPACE)
				|| getUser().getCurrentSpace().isTypeOf(SpaceTypes.PROJECT_SPACE);
	}
	
}
