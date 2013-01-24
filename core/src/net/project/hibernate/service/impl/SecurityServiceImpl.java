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
package net.project.hibernate.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.project.base.RecordStatus;
import net.project.hibernate.constants.SecurityConstants;
import net.project.hibernate.model.PnBusinessSpace;
import net.project.hibernate.model.PnDefaultObjectPermission;
import net.project.hibernate.model.PnDefaultObjectPermissionPK;
import net.project.hibernate.model.PnGroup;
import net.project.hibernate.model.PnGroupHasPerson;
import net.project.hibernate.model.PnGroupHasPersonPK;
import net.project.hibernate.model.PnModule;
import net.project.hibernate.model.PnModulePermission;
import net.project.hibernate.model.PnModulePermissionPK;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.model.PnObjectPermission;
import net.project.hibernate.model.PnObjectPermissionPK;
import net.project.hibernate.model.PnObjectSpace;
import net.project.hibernate.model.PnObjectType;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.model.PnSpaceHasGroup;
import net.project.hibernate.model.PnSpaceHasGroupPK;
import net.project.hibernate.model.PnSpaceHasModule;
import net.project.hibernate.model.PnSpaceHasSpace;
import net.project.hibernate.service.IPnBusinessSpaceService;
import net.project.hibernate.service.IPnDefaultObjectPermissionService;
import net.project.hibernate.service.IPnGroupHasPersonService;
import net.project.hibernate.service.IPnGroupService;
import net.project.hibernate.service.IPnModulePermissionService;
import net.project.hibernate.service.IPnModuleService;
import net.project.hibernate.service.IPnObjectPermissionService;
import net.project.hibernate.service.IPnObjectTypeService;
import net.project.hibernate.service.IPnProjectSpaceService;
import net.project.hibernate.service.IPnSpaceHasGroupService;
import net.project.hibernate.service.ISecurityService;
import net.project.hibernate.service.ServiceFactory;
import net.project.hibernate.service.filters.IPnDefaultObjectPermissionFilter;
import net.project.hibernate.service.filters.IPnModulePermissionFilter;
import net.project.hibernate.service.filters.IPnObjectSpaceServiceFilter;
import net.project.hibernate.service.filters.IPnSpaceHasGroupServiceFilter;
import net.project.hibernate.service.filters.IPnSpaceHasSpaceFilter;
import net.project.hibernate.service.impl.utilities.DebugMethodUtilites;
import net.project.hibernate.util.InsertObjectUtils;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service(value="securityService")
public class SecurityServiceImpl implements ISecurityService {

	@Deprecated
	private Logger logger;
	
	private final static Logger LOG = Logger.getLogger(SecurityServiceImpl.class);

	@Deprecated
	public Logger getLogger() {
		if (logger == null)
			logger = Logger.getLogger(getClass());
		return logger;
	}

	// Vlad fixed that method
	public Integer createSpaceAdminGroup(Integer creatorPersonId, Integer spaceId, String description) {
	    	if (LOG.isDebugEnabled()) {
		    LOG.debug("ENTRY OK: createSpaceAdminGroup");
		}
		Integer groupId = null;

		try {
			groupId = ServiceFactory.getInstance().getBaseService().createObject("group", creatorPersonId, "A");
			
			IPnGroupService groupService = ServiceFactory.getInstance().getPnGroupService();
			groupService.saveGroup(new PnGroup(groupId, "@prm.security.group.type.spaceadministrator.name",
				description, 0, 1, "A", SecurityConstants.GROUP_TYPE_SPACEADMIN));
			
			// Add the project creator (person) to the group
			IPnGroupHasPersonService groupHasPersonService = ServiceFactory.getInstance().getPnGroupHasPersonService();
			groupHasPersonService.saveGroupHasPerson(new PnGroupHasPerson(new PnGroupHasPersonPK(groupId, creatorPersonId)));
			
			// Add the group to the project space
			IPnSpaceHasGroupService spaceHasGroupService = ServiceFactory.getInstance().getPnSpaceHasGroupService();
			spaceHasGroupService.saveSpaceHasGroup(new PnSpaceHasGroup(new PnSpaceHasGroupPK(spaceId, groupId), 1));
			
			// Copies the System Group into the Object Permissions Table
			IPnObjectPermissionService objectPermissionService = ServiceFactory.getInstance().getPnObjectPermissionService();
			objectPermissionService.saveObjectPermission(new PnObjectPermission(
				new PnObjectPermissionPK(groupId, groupId), SecurityConstants.ALL_ACTIONS));
			
			IPnObjectTypeService pnObjectTypeService = ServiceFactory.getInstance().getPnObjectTypeService();
			// get list of all PnObjectType objects, only objectType and
			// defaultPermissionActions are selected
			Iterator pnObjectTypeIterator = pnObjectTypeService.findObjectTypes().iterator();
			IPnDefaultObjectPermissionService pnDefaultObjectPermissionService = ServiceFactory.getInstance().getPnDefaultObjectPermissionService();
			// saves all PnDefaultObjectPermission objects
			while (pnObjectTypeIterator.hasNext()) {
				PnObjectType pnObjectType = (PnObjectType) pnObjectTypeIterator.next();
				pnDefaultObjectPermissionService.saveDefaultObjectPermission(new PnDefaultObjectPermission(
					new PnDefaultObjectPermissionPK(spaceId, pnObjectType.getObjectType(), 
					groupId), SecurityConstants.ALL_ACTIONS));
			}
			
			IPnModuleService moduleService = ServiceFactory.getInstance().getPnModuleService();
			// get List of moduleIds and defaultPermissionActions from PnModule
			Iterator pnModuleIterator = moduleService.getModuleDefaultPermissions(spaceId).iterator();
			IPnModulePermissionService modulePermissionService = ServiceFactory.getInstance().getPnModulePermissionService();
			// save all PnModulePermission objects
			while (pnModuleIterator.hasNext()) {
				PnModule pnModule = (PnModule) pnModuleIterator.next();
				// saves permissions for all modules
				modulePermissionService.saveModulePermission(new PnModulePermission(
					new PnModulePermissionPK(spaceId, groupId, pnModule.getModuleId()), SecurityConstants.ALL_ACTIONS));
			}
		} catch (Exception e) {
		    	if (LOG.isDebugEnabled()) {
			    LOG.debug("EXIT FAIL: createSpaceAdminGroup");
			}
			e.printStackTrace();
		}
		if (LOG.isDebugEnabled()) {
		    LOG.debug("EXIT OK: createSpaceAdminGroup");
		}
		return groupId;
	}

	// Vlad fixed that method
	public void createSecurityPermissions(Integer objectId, String objectType, Integer spaceId, Integer personId) {
	    	if (LOG.isDebugEnabled()) {
		    LOG.debug("ENTRY OK: createSecurityPermissions");
		}
	    	
		try {
		    	// Get the object creator's (person) principal group_id for this space.
		    	// The object creator gets all permissions on the object.
			IPnGroupService groupService = ServiceFactory.getInstance().getPnGroupService();
			PnGroup pnGroup = groupService.getPrincipalGroupForSpaceAndPerson(spaceId, personId);
			Integer creatorPrincipalGroupId = null;
			if (pnGroup != null) {
			    creatorPrincipalGroupId = pnGroup.getGroupId();
			}
			
			// For this group is the creator's principal group,
			// give them all permissions on the object they are creating.
			if (creatorPrincipalGroupId != null) {
				IPnObjectPermissionService objectPermissionService = ServiceFactory.getInstance().getPnObjectPermissionService();
				objectPermissionService.saveObjectPermission(new PnObjectPermission(
					new PnObjectPermissionPK(objectId, creatorPrincipalGroupId), SecurityConstants.ALL_ACTIONS));
			}
			
			// Copies all object permissions and all of the groups (except principal groups)
			// for this project to the object_permission table
			IPnDefaultObjectPermissionService defaultObjectPermissionService = ServiceFactory.getInstance().
					getPnDefaultObjectPermissionService();
			List<PnDefaultObjectPermission> pnDefaultObjectPermissions = defaultObjectPermissionService.
					getDefaultObjectPermisionsBySpaceAndObjectTypeForNonPrincipalGroup(spaceId, objectType);
			
			IPnObjectPermissionService objectPermissionService = ServiceFactory.getInstance().getPnObjectPermissionService();
			
			for (PnDefaultObjectPermission pnDefaultObjectPermission : pnDefaultObjectPermissions) {
			    Integer pnDefaultObjectPermissionGroupId = pnDefaultObjectPermission.getComp_id().getGroupId();
			    long pnDefaultObjectPermissionActions = pnDefaultObjectPermission.getActions();
			    PnObjectPermission pnObjectPermission = new PnObjectPermission(
				    new PnObjectPermissionPK(objectId, pnDefaultObjectPermissionGroupId),pnDefaultObjectPermissionActions);
			    objectPermissionService.saveObjectPermission(pnObjectPermission);
			}
			
		} catch (Exception e) {
		    	if (LOG.isDebugEnabled()) {
			    LOG.debug("EXIT FAIL: createSecurityPermissions");
			}
			e.printStackTrace();
		}
		if (LOG.isDebugEnabled()) {
		    LOG.debug("EXIT OK: createSecurityPermissions");
		}
	}

	// Vlad fixed that method
	public boolean addPersonToGroup(Integer groupId, Integer personId) {
		if (LOG.isDebugEnabled()) {
		    LOG.debug("ENTRY OK: addPersonToGroup");
		}
		boolean result = false;
		try {
			IPnGroupHasPersonService service = ServiceFactory.getInstance().getPnGroupHasPersonService();
			PnGroupHasPerson object = new PnGroupHasPerson();
			object.setComp_id(new PnGroupHasPersonPK(groupId, personId));
			service.saveGroupHasPerson(object);
			result = true;
		} catch (Exception e) {
		    	if (LOG.isDebugEnabled()) {
		    	    LOG.debug("EXIT FAIL: addPersonToGroup");
		    	}
			e.printStackTrace();
		}
		if (LOG.isDebugEnabled()) {
		    LOG.debug("EXIT OK: addPersonToGroup");
		}
		return result;
	}

	public boolean storeModulePermission(Integer spaceId, Integer groupId, Integer moduleId, long actions) {
		boolean result = false;
		PnModulePermission object = null;
		String methodName = "storeModulePermission";
		String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
		String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
		String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
		long startTime = System.currentTimeMillis();
		getLogger().debug(methodIsStart);
		try {
			PnModulePermissionPK pk = new PnModulePermissionPK(spaceId, groupId, moduleId);
			object = new PnModulePermission(pk, actions);
			ServiceFactory.getInstance().getPnModulePermissionService().saveModulePermission(object);
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(methodIsFinished);
			result = true;
		} catch (Exception e) {
			try {
				ServiceFactory.getInstance().getPnModulePermissionService().updateModulePermission(object);
				long endTime = System.currentTimeMillis();
				long timeExecution = endTime - startTime;
				String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
				getLogger().debug(messageTimeExecution);
				getLogger().debug(exceptionInMethod);
				result = true;
			} catch (Exception e1) {
				long endTime = System.currentTimeMillis();
				long timeExecution = endTime - startTime;
				String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
				getLogger().debug(messageTimeExecution);
				getLogger().debug(exceptionInMethod);
				e1.printStackTrace();
			}
		}
		return result;
	}

	public boolean storeObjectPermission(Integer objectId, Integer groupId, long actions) {
		boolean result = false;
		PnObjectPermission object = null;
		String methodName = "storeObjectPermission";
		String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
		String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
		String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
		long startTime = System.currentTimeMillis();
		getLogger().debug(methodIsStart);
		try {
			PnObjectPermissionPK pk = new PnObjectPermissionPK(objectId, groupId);
			object = new PnObjectPermission(pk, actions);
			ServiceFactory.getInstance().getPnObjectPermissionService().saveObjectPermission(object);
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(methodIsFinished);
			result = true;
		} catch (Exception e) {
			try {
				ServiceFactory.getInstance().getPnObjectPermissionService().updateObjectPermission(object);
				long endTime = System.currentTimeMillis();
				long timeExecution = endTime - startTime;
				String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
				getLogger().debug(messageTimeExecution);
				getLogger().debug(exceptionInMethod);
				result = true;
			} catch (Exception e1) {
				long endTime = System.currentTimeMillis();
				long timeExecution = endTime - startTime;
				String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
				getLogger().debug(messageTimeExecution);
				getLogger().debug(exceptionInMethod);
				e1.printStackTrace();
			}
		}
		return result;
	}

	public boolean storeDefaultObjPermission(Integer spaceId, String objectType, Integer groupId, long actions) {
		boolean result = false;
		PnDefaultObjectPermission object = null;
		String methodName = "storeDefaultObjPermission";
		String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
		String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
		String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
		long startTime = System.currentTimeMillis();
		getLogger().debug(methodIsStart);
		try {
			PnDefaultObjectPermissionPK pk = new PnDefaultObjectPermissionPK(spaceId, objectType, groupId);
			object = new PnDefaultObjectPermission(pk, actions);
			ServiceFactory.getInstance().getPnDefaultObjectPermissionService().saveDefaultObjectPermission(object);
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(methodIsFinished);
			result = true;
		} catch (Exception e) {
			try {
				ServiceFactory.getInstance().getPnDefaultObjectPermissionService().updateDefaultObjectPermission(object);
				result = true;
				long endTime = System.currentTimeMillis();
				long timeExecution = endTime - startTime;
				String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
				getLogger().debug(messageTimeExecution);
				getLogger().debug(exceptionInMethod);
			} catch (Exception e1) {
				long endTime = System.currentTimeMillis();
				long timeExecution = endTime - startTime;
				String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
				getLogger().debug(messageTimeExecution);
				getLogger().debug(exceptionInMethod);
				e1.printStackTrace();
			}
		}
		return result;
	}

	public boolean removeGroupPermission(Integer groupId) {
		/*
		 * select group_type_id, o.object_type, o.object_id into v_group_type_id, v_space_type, v_space_id from pn_group g, pn_space_has_group shg, pn_object o where g.group_id =
		 * groupId and g.group_id = shg.group_id and shg.is_owner = 1 and shg.space_id = o.object_id(+);
		 */
		boolean result = false;
		String methodName = "removeGroupPermission";
		String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
		String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
		String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
		long startTime = System.currentTimeMillis();
		getLogger().debug(methodIsStart);
		try {
			PnGroup object = ServiceFactory.getInstance().getPnGroupService().getGroup(groupId);
			if (object != null) {
				Set groups = object.getPnSpaceHasGroups();
				if (groups != null) {
					Iterator groupsIterator = groups.iterator();
					while (groupsIterator.hasNext()) {
						PnSpaceHasGroup spaceHasGroup = (PnSpaceHasGroup) groupsIterator.next();
						if (spaceHasGroup.getIsOwner() == 1) {
							// if (v_group_type_id = 600 and v_space_type = 'business') then
							if (object.getPnGroupType().getGroupTypeId().equals(new Integer(600))
									&& (object.getPnObject().getPnObjectType().getObjectType().equalsIgnoreCase("business"))) {
								/*
								 * ToDo *update pn_business_space set includes_everyone = 0 where business_space_id = v_space_id;
								 */
								IPnBusinessSpaceService bsService = ServiceFactory.getInstance().getPnBusinessSpaceService();
								PnBusinessSpace bsObject = bsService.getBusinessSpace(object.getPnObject().getObjectId());
								bsObject.setIncludesEveryone(0);
								bsService.saveObject(bsObject);
							}
							/*
							 * elsif (v_group_type_id = 600 and v_space_type = 'project') then update pn_project_space set includes_everyone = 0 where project_id = v_space_id; end
							 * if;
							 */
							else {
								if (object.getPnGroupType().getGroupTypeId().equals(new Integer(600))
										&& (object.getPnObject().getPnObjectType().getObjectType().equalsIgnoreCase("project"))) {
									IPnProjectSpaceService psService = ServiceFactory.getInstance().getPnProjectSpaceService();
									PnProjectSpace psObject = psService.getProjectSpace(object.getPnObject().getObjectId());
									psObject.setIncludesEveryone(0);
									psService.saveProjectSpace(psObject);
								}
							}
						}
					}
				}
				/*-- hard delete from permission tables
				 DELETE FROM pn_default_object_permission
				 WHERE group_id = groupId;
				 */
				ServiceFactory.getInstance().getPnDefaultObjectPermissionService().deleteDefaultObjectPermisionsByGroupId(groupId);
				/*
				 * DELETE FROM pn_module_permission WHERE group_id = groupId;
				 */
				ServiceFactory.getInstance().getPnModulePermissionService().deleteModulePermissionsByGroupId(groupId);
				/*
				 * DELETE FROM pn_object_permission WHERE group_id = groupId;
				 */
				ServiceFactory.getInstance().getPnObjectPermissionService().deleteObjectPermissionsForGroup(groupId);
				/*
				 * DELETE FROM pn_page_permission WHERE group_id = groupId;
				 */
				ServiceFactory.getInstance().getPnPagePermissionService().deletePermissionByGroupId(groupId);
				/*
				 * -- remove the group from the space(s) it belongs to DELETE FROM pn_space_has_group WHERE group_id = groupId;
				 */
				ServiceFactory.getInstance().getPnSpaceHasGroupService().deleteByGroupId(groupId);
				/*
				 * -- remove group membership where other groups are a member of this -- one or this group is a member of other groups delete from pn_group_has_group where group_id =
				 * groupId or member_group_id = groupId;
				 */
				ServiceFactory.getInstance().getPnGroupHasGroupService().deleteByGroupId(groupId);
				/*
				 * -- soft delete from group table -- This is necessary because other parts of the system (Workflow) -- may break if the group is gone UPDATE pn_group SET
				 * record_status = 'D' WHERE group_id = groupId;
				 */
				object.setRecordStatus(RecordStatus.DELETED.getID());
				ServiceFactory.getInstance().getPnGroupService().saveGroup(object);

			}
			result = true;
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(methodIsFinished);
		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(exceptionInMethod);
			e.printStackTrace();
		}
		return result;
	}

	// Vlad fixed that method
	public void applyAdminPermissions(Integer newGroupId, Integer spaceAdminGroupId) {
	    	if (LOG.isDebugEnabled()) {
		    LOG.debug("EXIT OK: applyAdminPermissions");
		}
		try {
			IPnObjectPermissionService pnService = ServiceFactory.getInstance().getPnObjectPermissionService();
			List<PnObjectPermission> pnObjectPermissions = pnService.getObjectPermissionsForGroup(spaceAdminGroupId);
			for (PnObjectPermission pnObjectPermissionIter : pnObjectPermissions) {
			    PnObjectPermission pnObjectPermission = new PnObjectPermission(
				    new PnObjectPermissionPK(pnObjectPermissionIter.getComp_id().getObjectId(), newGroupId),
				    SecurityConstants.ALL_ACTIONS);
			    pnService.saveObjectPermission(pnObjectPermission);
			}
		} catch (Exception e) {
		    if (LOG.isDebugEnabled()) {
			LOG.debug("EXIT FAIL: applyAdminPermissions");
		    }
		    e.printStackTrace();
		}
		if (LOG.isDebugEnabled()) {
		    LOG.debug("EXIT OK: applyAdminPermissions");
		}
	}

	// Vlad fixed this method
	public Integer storeGroup(String groupName, String groupDesc, Integer isPrincipal, Integer isSystemGroup, Integer groupTypeId, Integer creatorPersonId, Integer spaceId, Integer groupId) {
	    	if (LOG.isDebugEnabled()) {
		    LOG.debug("ENTRY OK: storeGroup");
		}
		try {
		    // null groupId means we are storing the group for the first time
		    if (groupId == null) {
			// Principal groups have the owner's id stameped on them
			Integer principalOwnerId = null;
			if (isPrincipal == 1) {
				principalOwnerId = creatorPersonId;
			}

			// Create new ID for the group and set default security permissions
			groupId = ServiceFactory.getInstance().getBaseService().createObject("group", creatorPersonId, "A");
			// Insert into the group table
			PnGroup pnGroup = new PnGroup();
			pnGroup.setGroupId(groupId);
			pnGroup.setGroupName(groupName);
			pnGroup.setGroupDesc(groupDesc);
			pnGroup.setIsPrincipal(isPrincipal);
			pnGroup.setIsSystemGroup(isSystemGroup);
			pnGroup.setRecordStatus("A");
			pnGroup.setPnGroupType(ServiceFactory.getInstance().getPnGroupTypeService().getByObjectId(groupTypeId));
			pnGroup.setPnPerson(ServiceFactory.getInstance().getPnPersonService().getPerson(principalOwnerId));
			ServiceFactory.getInstance().getPnGroupService().saveGroup(pnGroup);
			
			// Insert the group into the space
			PnSpaceHasGroupPK pk = new PnSpaceHasGroupPK();
			pk.setSpaceId(spaceId);
			pk.setGroupId(groupId);
			PnSpaceHasGroup pnSpaceHasGroup = new PnSpaceHasGroup();
			pnSpaceHasGroup.setComp_id(pk);
			pnSpaceHasGroup.setIsOwner(1);
			ServiceFactory.getInstance().getPnSpaceHasGroupService().saveSpaceHasGroup(pnSpaceHasGroup);

			// Principal groups don't get default permissions
			if (isPrincipal != 1) {
			    	// Apply module_permissions to the new group
			    	// Groups are special objects that need module permissions for security checks
			    	List<PnModule> modules = ServiceFactory.getInstance().getPnModuleService().getModulesForSpace(spaceId);
			    	for (PnModule module : modules) {
			    	    PnModulePermission modulePermission = new PnModulePermission(new PnModulePermissionPK(
			    		    spaceId, groupId, module.getModuleId()), module.getDefaultPermissionActions());
				    ServiceFactory.getInstance().getPnModulePermissionService().saveModulePermission(modulePermission);
				}
			    	
			    	// Add pn_default_object_permissions for group
			    	// Make entries in default_object_permissions table for each module for the new group
				List<PnObjectType> types = ServiceFactory.getInstance().getPnObjectTypeService().findObjectTypes();
				for (PnObjectType objectType : types) {
					   PnDefaultObjectPermission defaultObjectPermission = new PnDefaultObjectPermission(
						   new PnDefaultObjectPermissionPK(spaceId, objectType.getObjectType(), groupId),
						   objectType.getDefaultPermissionActions());
					    ServiceFactory.getInstance().getPnDefaultObjectPermissionService().
					    	saveDefaultObjectPermission(defaultObjectPermission);
					}
				createSecurityPermissions(groupId, "group", spaceId, creatorPersonId);
				}
			
			} else {
			    	// Caller passed a group_id, do an update of the group's properties
				PnGroup objGroup = ServiceFactory.getInstance().getPnGroupService().getGroup(groupId);
				if (objGroup != null) {
				    objGroup.setGroupName(groupName);
				    objGroup.setGroupDesc(groupDesc);
				    objGroup.setIsPrincipal(isPrincipal);
				    objGroup.setIsSystemGroup(isSystemGroup);
				    ServiceFactory.getInstance().getPnGroupService().updateGroup(objGroup);
				}
			}
		    
		    	// We're going to do some denormalization here so that project spaces or business
		    	// spaces don't have to look into the groups to see if the everyone group has been
		    	// invited. This way, they have an easy way to generate a portfolio.
			if (groupTypeId == 600) {
				PnObject obj = ServiceFactory.getInstance().getPnObjectService().getObject(spaceId);
				if (obj != null) {
					if (obj.getPnObjectType().getObjectType().equalsIgnoreCase("project")) {
						PnProjectSpace projectSpace = ServiceFactory.getInstance().getPnProjectSpaceService().getProjectSpace(spaceId);
						if (projectSpace != null) {
							projectSpace.setIncludesEveryone(1);
							ServiceFactory.getInstance().getPnProjectSpaceService().updateProjectSpace(projectSpace);
						}
					} else {
						if (obj.getPnObjectType().getObjectType().equalsIgnoreCase("business")) {
							PnBusinessSpace businessSpace = ServiceFactory.getInstance().getPnBusinessSpaceService().getBusinessSpace(spaceId);
							if (businessSpace != null) {
								businessSpace.setIncludesEveryone(1);
								ServiceFactory.getInstance().getPnBusinessSpaceService().updateBusinessSpace(businessSpace);
							}
						}
					}
				}
			}
		} catch (Exception e) {
		    	if (LOG.isDebugEnabled()) {
			    LOG.debug("EXIT FAIL: storeGroup");
			}
			e.printStackTrace();
		}
		if (LOG.isDebugEnabled()) {
		    LOG.debug("EXIT OK: storeGroup");
		}
		return groupId;
	}

	public Integer createTeamMemberGroup(Integer creatorId, Integer spaceId) {
		/*
		 * security.storeGroup ('@prm.security.group.type.teammember.name', '@prm.security.group.type.teammember.description', 0, 1, security.group_type_teammember, creatorId,
		 * spaceId, v_group_id );
		 */
		return storeGroup("@prm.security.group.type.teammember.name", "@prm.security.group.type.teammember.description", new Integer(0), new Integer(1), GROUP_TYPE_TEAMMEMBER,
				creatorId, spaceId, null);
	}

	public Integer createPrincipalGroup(Integer creatorId, Integer spaceId) {
		/*
		 * security.storeGroup ('@prm.security.group.type.principal.name', '@prm.security.group.type.principal.decription', 1, 1, security.group_type_principal, creatorId, spaceId,
		 * v_group_id );
		 */

		return storeGroup("@prm.security.group.type.principal.name", "@prm.security.group.type.principal.decription", new Integer(1), new Integer(1), GROUP_TYPE_PRINCIPAL,
				creatorId, spaceId, null);
	}

	// Vlad fixed that method
	public void applyDocumentPermissions(Integer objectId, Integer parentId, String objectType, Integer spaceId, Integer personId) {
	    
	    	if (LOG.isDebugEnabled()) {
	    	    LOG.debug("ENTRY OK: applyDocumentPermissions");
	    	}
	    	
		try {
			/*
			 * -- remove the objects current security settings DELETE FROM pn_object_permission WHERE object_id = objectId;
			 */
			IPnObjectPermissionService pnObjectPermissionService = ServiceFactory.getInstance().getPnObjectPermissionService();
			pnObjectPermissionService.deleteObjectPermissionsForObject(objectId);

			if (parentId != null) {
			    
			    	// The object has a parent, therefore inherit the parents security settings
			    	List<PnObjectPermission> objectPermissionsList = pnObjectPermissionService.getObjectPermissionsForObject(parentId);
			    	for (PnObjectPermission pnObjectPermission : objectPermissionsList) {
					PnObjectPermission objectPermission = new PnObjectPermission();
					PnObjectPermissionPK pk = new PnObjectPermissionPK();
					pk.setObjectId(objectId);
					pk.setGroupId(pnObjectPermission.getPnGroup().getGroupId());
					objectPermission.setActions(pnObjectPermission.getActions());
					pnObjectPermissionService.saveObjectPermission(objectPermission);
			    	    }

				if (personId != null) {
				    
				    	// Get the object creator's (person) principal group_id for this space.
				    	// The object creator gets all permissions on the object.
				    	PnGroup creatorPrincipalGroup = ServiceFactory.getInstance().getPnGroupService().
				    		getPrincipalGroupForSpaceAndPerson(spaceId, personId);
				    	Integer vCreatorPrincipalGroupId = null;
				    	if (creatorPrincipalGroup != null) {
				    	     vCreatorPrincipalGroupId = creatorPrincipalGroup.getGroupId();
				    	     // For this group is the creator's principal group,
				    	     // give them all permissions on the object the they are creating
					    PnObjectPermission pnObjectPermission = ServiceFactory.getInstance().getPnObjectPermissionService()
					    		.getObjectPermission(new PnObjectPermissionPK(objectId, vCreatorPrincipalGroupId));
					    pnObjectPermission.setActions(SecurityConstants.ALL_ACTIONS);
					    ServiceFactory.getInstance().getPnObjectPermissionService().updateObjectPermission(pnObjectPermission);
					}
				}
					
			} else {
			    
				// The object does not have a parent therefore system groups get full permissions
			    	List<PnDefaultObjectPermission> pnGroups = ServiceFactory.getInstance().getPnDefaultObjectPermissionService().
			    		getDefaultObjectPermissionsBySpaceAndObjectTypeForSystemNonPrincipalGroup(spaceId, objectType);
			    	    for (PnDefaultObjectPermission pnDefaultObjectPermission : pnGroups) {
			    		Integer pnGroupId = pnDefaultObjectPermission.getPnGroup().getGroupId();
			    		PnObjectPermission pnObjectPermission = new PnObjectPermission();
			    		PnObjectPermissionPK pk = new PnObjectPermissionPK();
			    		pnObjectPermission.setComp_id(pk);
			    		pk.setObjectId(objectId);
			    		pk.setGroupId(pnGroupId);
			    		pnObjectPermission.setActions(SecurityConstants.ALL_ACTIONS);
			    		ServiceFactory.getInstance().getPnObjectPermissionService().saveObjectPermission(pnObjectPermission);
			    	    }
			}
			
		} catch (Exception e) {
		    	if (LOG.isDebugEnabled()) {
		    	    LOG.debug("EXIT FAIL: applyDocumentPermissions");
		    	}
			e.printStackTrace();
		}
		
		if (LOG.isDebugEnabled()) {
		    LOG.debug("EXIT OK: applyDocumentPermissions");
		}
		
	}

	// Ivana fixed this method
	public Integer createPowerUserGroup(Integer creatorPersonId, Integer spaceId, String description) {
	    	if (LOG.isDebugEnabled()) {
		    LOG.debug("ENTRY OK: createPowerUserGroup");
		}
		Integer groupId = null;
		try {
			int permissibleActions = 135;
			groupId = ServiceFactory.getInstance().getBaseService().createObject("group", creatorPersonId, "A");
			// create new group
			PnGroup pnGroup = new PnGroup(groupId, "@prm.security.group.type.poweruser.name", description, 0, 1, "A", GROUP_TYPE_POWERUSER);
			ServiceFactory.getInstance().getPnGroupService().saveGroup(pnGroup);

			// SECURITY.CREATE_SECURITY_PERMISSIONS(v_group_id, 'group', spaceId, creatorPersonId);

			createSecurityPermissions(groupId, "group", spaceId, creatorPersonId);

			// Add the group to the project_space
			PnSpaceHasGroup pnSpaceHasGroup = new PnSpaceHasGroup(new PnSpaceHasGroupPK(spaceId, groupId), 1);
			ServiceFactory.getInstance().getPnSpaceHasGroupService().saveSpaceHasGroup(pnSpaceHasGroup);

			// Add permissions for all object types in the system
			List<PnObjectType> pnObjectTypes = ServiceFactory.getInstance().getPnObjectTypeService().findObjectTypes();
			for (int i = 0; i < pnObjectTypes.size(); i++) {
				PnObjectType currentObjectType = pnObjectTypes.get(i);
				PnDefaultObjectPermission newDefaultObjectPermission = new PnDefaultObjectPermission(new PnDefaultObjectPermissionPK(spaceId, currentObjectType.getObjectType(),
						groupId), permissibleActions);
				ServiceFactory.getInstance().getPnDefaultObjectPermissionService().saveDefaultObjectPermission(newDefaultObjectPermission);
			}

			// Default Module permissions for Power User Group
			List<PnModule> modulesList = ServiceFactory.getInstance().getPnModuleService().getModuleDefaultPermissions(spaceId);
			for (int i = 0; i < modulesList.size(); i++) {
				PnModule pnModule = (PnModule) modulesList.get(i);
				PnModulePermission pnModulePermission = new PnModulePermission(new PnModulePermissionPK(spaceId, groupId, pnModule.getModuleId()), permissibleActions);
				ServiceFactory.getInstance().getPnModulePermissionService().saveModulePermission(pnModulePermission);
			}
		} catch (Exception e) {
		    	if (LOG.isDebugEnabled()) {
			    LOG.debug("EXIT FAIL: createPowerUserGroup");
			}
			e.printStackTrace();
		}
		if (LOG.isDebugEnabled()) {
		    LOG.debug("EXIT OK: createPowerUserGroup");
		}
		return groupId;
	}

	// Vlad fixed this method
	public Integer createParentAdminRole(Integer spaceId, Integer parentSpaceId) {
	    	if (LOG.isDebugEnabled()) {
		    LOG.debug("ENTRY OK: createParentAdminRole");
		}
		Integer groupId = null;
		try {
			// get the existing Space Admin Group for remediation of existing objects
			Integer currentAdminGroup = ServiceFactory.getInstance().getPnGroupService().
				getGroupForSpaceAndGroupType(spaceId, SecurityConstants.GROUP_TYPE_SPACEADMIN).get(0).getGroupId();
			// get the ids for groups for parent space
			List<PnGroup> groups = ServiceFactory.getInstance().getPnGroupService().
				getGroupForSpaceAndGroupType(parentSpaceId, SecurityConstants.GROUP_TYPE_SPACEADMIN);
			
			// Get the Space Administrator role from the parent space
			for (PnGroup group : groups){
				//ALL Space Administrator Roles from the parent space
			    	groupId = group.getGroupId();
				// Add the group to the project_space
				PnSpaceHasGroup spaceHasGroup = new PnSpaceHasGroup(new PnSpaceHasGroupPK(spaceId, groupId), 0);
				ServiceFactory.getInstance().getPnSpaceHasGroupService().saveSpaceHasGroup(spaceHasGroup);
				
				// Set all object_type permissions
				// for administrator the actions are set to ALL_ACTIONS or (2^16)-1
				//PnObjectTypeServiceImpl		        
				List<PnObjectType> objectTypes = ServiceFactory.getInstance().getPnObjectTypeService().findObjectTypes();
				// ALL OBJECT TYPES IN THE SYSTEM
				for(PnObjectType objectType : objectTypes){
				    PnDefaultObjectPermission defaultObjectPermission = new PnDefaultObjectPermission(
					    new PnDefaultObjectPermissionPK(spaceId, objectType.getObjectType(), groupId),
					    SecurityConstants.ALL_ACTIONS);
				    ServiceFactory.getInstance().getPnDefaultObjectPermissionService().
				    	saveDefaultObjectPermission(defaultObjectPermission);
				}
				
				// Default Module permissions for Space Administrator
				List<PnModule> pnModules = ServiceFactory.getInstance().getPnModuleService().getModulesForSpace(spaceId);
				for (PnModule pnModule : pnModules){
				    PnModulePermission modulePermission = new PnModulePermission(new PnModulePermissionPK(
					    spaceId, groupId, pnModule.getModuleId()), SecurityConstants.ALL_ACTIONS);
				    // Must follow the group creation for Space Administrator above
				    ServiceFactory.getInstance().getPnModulePermissionService().saveModulePermission(modulePermission);
				}	
				applyAdminPermissions(groupId, currentAdminGroup);
			}
		} catch (Exception e) {
		    	if (LOG.isDebugEnabled()) {
			    LOG.debug("EXIT FAIL: createParentAdminRole");
			}
			e.printStackTrace();
		}
		if (LOG.isDebugEnabled()) {
		    LOG.debug("EXIT OK: createParentAdminRole");
		}
		return groupId;
		
		/*
		
		Integer result = null;
		String methodName = "createParentAdminRole";
		String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
		String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
		String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
		long startTime = System.currentTimeMillis();
		getLogger().debug(methodIsStart);
		try{
			PnGroup currentAdminGroup = null;
			List<PnSpaceHasGroup> pnSpaceList = ServiceFactory.getInstance().getPnSpaceHasGroupService().getAll();
			for (int i = 0, n = pnSpaceList.size(); i < n; i++) {
				PnSpaceHasGroup pnSpaceGroup = pnSpaceList.get(i);
				if (pnSpaceGroup.getComp_id().getSpaceId().equals(spaceId)) {
					PnGroup currentGroup = pnSpaceGroup.getPnGroup();
					if (currentGroup.getPnGroupType().getGroupTypeId().equals(GROUP_TYPE_SPACEADMIN)) {
						currentAdminGroup = currentGroup;
						break;
					}
				}
			}
			// CURSOR c_group_id IS select g.group_id from pn_space_has_group shg, pn_group g where shg.space_id = parentSpaceId and g.group_type_id =
			// SECURITY.GROUP_TYPE_SPACEADMIN and shg.group_id = g.group_id;
			
			pnSpaceList = ServiceFactory.getInstance().getPnSpaceHasGroupService().getAll();
			for (int i = 0, n = pnSpaceList.size(); i < n; i++) {
				PnSpaceHasGroup pnSpaceGroup = pnSpaceList.get(i);
				if (pnSpaceGroup.getComp_id().getSpaceId().equals(parentSpaceId)) {
					PnGroup currentGroup = pnSpaceGroup.getPnGroup();
					result = currentGroup.getGroupId();
					if (currentGroup.getPnGroupType().getGroupTypeId().equals(GROUP_TYPE_SPACEADMIN)) {
						// -- Add the group to the project_space insert into pn_space_has_group (space_id, group_id, is_owner) values (spaceId, v_group_id, 0);
						
						PnSpaceHasGroup newPnSpaceHasGroup = new PnSpaceHasGroup();
						PnSpaceHasGroupPK pnSpaceHasGroupPK = new PnSpaceHasGroupPK();
						newPnSpaceHasGroup.setComp_id(pnSpaceHasGroupPK);
						pnSpaceHasGroupPK.setGroupId(currentGroup.getGroupId());
						pnSpaceHasGroupPK.setSpaceId(spaceId);
						newPnSpaceHasGroup.setIsOwner(0);
						ServiceFactory.getInstance().getPnSpaceHasGroupService().saveSpaceHasGroup(newPnSpaceHasGroup);

						List<PnObjectType> listPnObjectTypes = ServiceFactory.getInstance().getPnObjectTypeService().findAll();
						for (int j = 0, k = listPnObjectTypes.size(); j < k; j++) {
							PnObjectType currentPnObjectType = listPnObjectTypes.get(j);
							//INSERT INTO pn_default_object_permission ( space_id, object_type, group_id, actions) VALUES ( spaceId, v_object_type, v_group_id, ALL_ACTIONS); --
							 // space administrators get all actions for all types
							 
							PnDefaultObjectPermission newDefaultObjectPermission = new PnDefaultObjectPermission();
							PnDefaultObjectPermissionPK pkDefaultObjectPermissionPK = new PnDefaultObjectPermissionPK();
							newDefaultObjectPermission.setComp_id(pkDefaultObjectPermissionPK);
							pkDefaultObjectPermissionPK.setSpaceId(spaceId);
							pkDefaultObjectPermissionPK.setObjectType(currentPnObjectType.getObjectType());
							pkDefaultObjectPermissionPK.setGroupId(currentGroup.getGroupId());
							newDefaultObjectPermission.setActions(ALL_ACTIONS);
							ServiceFactory.getInstance().getPnDefaultObjectPermissionService().saveDefaultObjectPermission(newDefaultObjectPermission);
						}

						List<PnModule> listPnModule = ServiceFactory.getInstance().getPnModuleService().getAll();
						for (int j = 0, k = listPnModule.size(); j < k; j++) {
							PnModule currentModule = listPnModule.get(j);
							Set pnSpaceHasModules = currentModule.getPnSpaceHasModules();
							if (pnSpaceHasModules != null) {
								Iterator<PnSpaceHasModule> pnSpaceIterator = pnSpaceHasModules.iterator();
								while (pnSpaceIterator.hasNext()) {
									PnSpaceHasModule spaceHasModule = pnSpaceIterator.next();
									if (spaceHasModule.getComp_id().getSpaceId().equals(spaceId) && (spaceHasModule.getIsActive() == 1)) {
										PnModulePermission newPnModulePermission = new PnModulePermission();
										PnModulePermissionPK newPnModulePermissionPK = new PnModulePermissionPK();
										newPnModulePermission.setComp_id(newPnModulePermissionPK);
										newPnModulePermissionPK.setModuleId(currentModule.getModuleId());
										newPnModulePermissionPK.setSpaceId(spaceId);
										newPnModulePermissionPK.setGroupId(currentGroup.getGroupId());
										newPnModulePermission.setActions(ALL_ACTIONS);
										ServiceFactory.getInstance().getPnModulePermissionService().saveModulePermission(newPnModulePermission);
									}
								}
							}
						}
					}
					// applyAdminPermissions (v_group_id, v_current_admin_group);
					applyAdminPermissions(result, currentAdminGroup.getGroupId());
				}
			}
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(methodIsFinished);
		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(exceptionInMethod);
			e.printStackTrace();
		}
		return result;
		*/
	}

	public void grantModulePermissions(Integer spaceId, Integer groupId, String permission) {
		String methodName = "grantModulePermissions";
		String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
		String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
		String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
		long startTime = System.currentTimeMillis();
		getLogger().debug(methodIsStart);
		try {
			if (permission.equalsIgnoreCase(PERMISSION_SELECTION_NONE)) {
				/*
				 * -- No permissions are to be available. Remove existing ones. delete from pn_module_permission where space_id = spaceId and group_id = groupId;
				 */
				ServiceFactory.getInstance().getPnModulePermissionService().deleteModulePermissionsBySpaceAndGroupId(spaceId, groupId);
			}

			if (permission.equalsIgnoreCase(PERMISSION_SELECTION_VIEW)) {
				ServiceFactory.getInstance().getPnModulePermissionService().deleteModulePermissionsBySpaceAndGroupId(spaceId, groupId);
				List<PnSpaceHasModule> listPnSpaceHasModules = ServiceFactory.getInstance().getPnSpaceHasModuleService().findAll();
				for (int i = 0, n = listPnSpaceHasModules.size(); i < n; i++) {
					PnSpaceHasModule currentPnSpaceHasModule = listPnSpaceHasModules.get(i);
					if (currentPnSpaceHasModule.getComp_id().getSpaceId().equals(spaceId)) {
						/*
						 * INSERT INTO pn_module_permission (module_id, space_id, group_id, actions) VALUES (module_rec.module_id, module_rec.space_id, groupId,
						 * SECURITY.VIEW_PERMISSION_BITMASK);
						 */
						PnModulePermission newPnModulePermission = new PnModulePermission();
						PnModulePermissionPK pk = new PnModulePermissionPK();
						newPnModulePermission.setComp_id(pk);
						pk.setModuleId(currentPnSpaceHasModule.getPnModule().getModuleId());
						pk.setSpaceId(currentPnSpaceHasModule.getComp_id().getSpaceId());
						pk.setGroupId(groupId);
						newPnModulePermission.setActions(VIEW_PERMISSION_BITMASK);
						ServiceFactory.getInstance().getPnModulePermissionService().saveModulePermission(newPnModulePermission);
					}
				}
			} else {
				ServiceFactory.getInstance().getPnModulePermissionService().deleteModulePermissionsBySpaceAndGroupId(spaceId, groupId);
				List<PnSpaceHasModule> listPnSpaceHasModules = ServiceFactory.getInstance().getPnSpaceHasModuleService().findAll();
				for (int i = 0, n = listPnSpaceHasModules.size(); i < n; i++) {
					PnSpaceHasModule currentPnSpaceHasModule = listPnSpaceHasModules.get(i);
					if (currentPnSpaceHasModule.getComp_id().getSpaceId().equals(spaceId)) {
						/*
						 * INSERT INTO pn_module_permission (module_id, space_id, group_id, actions) VALUES (module_rec.module_id, module_rec.space_id, groupId,
						 * SECURITY.VIEW_PERMISSION_BITMASK);
						 */
						PnModulePermission newPnModulePermission = new PnModulePermission();
						PnModulePermissionPK pk = new PnModulePermissionPK();
						newPnModulePermission.setComp_id(pk);
						pk.setModuleId(currentPnSpaceHasModule.getPnModule().getModuleId());
						pk.setSpaceId(currentPnSpaceHasModule.getComp_id().getSpaceId());
						pk.setGroupId(groupId);
						newPnModulePermission.setActions(currentPnSpaceHasModule.getPnModule().getDefaultPermissionActions());
						ServiceFactory.getInstance().getPnModulePermissionService().saveModulePermission(newPnModulePermission);
					}
				}
			}
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(methodIsFinished);
		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(exceptionInMethod);
			e.printStackTrace();
		}
	}

	public void setNewObjectPermissions(Integer spaceId, Integer groupId, String permission) {
		String methodName = "setNewObjectPermissions";
		String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
		String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
		String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
		long startTime = System.currentTimeMillis();
		getLogger().debug(methodIsStart);
		try {
			if (permission.equalsIgnoreCase(PERMISSION_SELECTION_NONE)) {
				ServiceFactory.getInstance().getPnDefaultObjectPermissionService().deleteDefaultObjectPermisionsByGroupId(groupId);
			}
			if (permission.equalsIgnoreCase(PERMISSION_SELECTION_VIEW)) {
				ServiceFactory.getInstance().getPnDefaultObjectPermissionService().deleteDefaultObjectPermisionsByGroupId(groupId);
				List<PnObjectType> listPnObjectTypes = ServiceFactory.getInstance().getPnObjectTypeService().findAll();
				for (int i = 0, n = listPnObjectTypes.size(); i < n; i++) {
					PnObjectType currentPnObjectType = listPnObjectTypes.get(i);
					/*
					 * NSERT INTO pn_default_object_permission (space_id, object_type, group_id, actions) VALUES (spaceId, object_type_rec.object_type, groupId,
					 * SECURITY.VIEW_PERMISSION_BITMASK);
					 */
					PnDefaultObjectPermission newDefaultObjectPermission = new PnDefaultObjectPermission();
					PnDefaultObjectPermissionPK pnDefaultObjectPermissionPK = new PnDefaultObjectPermissionPK();
					newDefaultObjectPermission.setComp_id(pnDefaultObjectPermissionPK);
					pnDefaultObjectPermissionPK.setSpaceId(spaceId);
					pnDefaultObjectPermissionPK.setObjectType(currentPnObjectType.getObjectType());
					pnDefaultObjectPermissionPK.setGroupId(groupId);
					newDefaultObjectPermission.setActions(VIEW_PERMISSION_BITMASK);
					ServiceFactory.getInstance().getPnDefaultObjectPermissionService().saveDefaultObjectPermission(newDefaultObjectPermission);
				}
			} else {
				ServiceFactory.getInstance().getPnDefaultObjectPermissionService().deleteDefaultObjectPermisionsByGroupId(groupId);
				List<PnObjectType> listPnObjectTypes = ServiceFactory.getInstance().getPnObjectTypeService().findAll();
				for (int i = 0, n = listPnObjectTypes.size(); i < n; i++) {
					PnObjectType currentPnObjectType = listPnObjectTypes.get(i);
					/*
					 * INSERT INTO pn_default_object_permission (space_id, object_type, group_id, actions) VALUES (spaceId, object_type_rec.object_type, groupId,
					 * SECURITY.VIEW_PERMISSION_BITMASK);
					 */
					PnDefaultObjectPermission newDefaultObjectPermission = new PnDefaultObjectPermission();
					PnDefaultObjectPermissionPK pnDefaultObjectPermissionPK = new PnDefaultObjectPermissionPK();
					newDefaultObjectPermission.setComp_id(pnDefaultObjectPermissionPK);
					pnDefaultObjectPermissionPK.setSpaceId(spaceId);
					pnDefaultObjectPermissionPK.setObjectType(currentPnObjectType.getObjectType());
					pnDefaultObjectPermissionPK.setGroupId(groupId);
					newDefaultObjectPermission.setActions(currentPnObjectType.getDefaultPermissionActions());
					ServiceFactory.getInstance().getPnDefaultObjectPermissionService().saveDefaultObjectPermission(newDefaultObjectPermission);
				}

			}
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(methodIsFinished);
		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(exceptionInMethod);
			e.printStackTrace();
		}

	}

	private void inheritGroupDetails(Integer spaceId, Integer groupId, String permission, InheritGroupInterface inheritGroupInterface) {
		String methodName = "inheritGroupDetails";
		String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
		String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
		String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
		long startTime = System.currentTimeMillis();
		getLogger().debug(methodIsStart);
		try {
			List<PnModule> modules = ServiceFactory.getInstance().getPnModuleService().getAll();
			for (int i = 0, n = modules.size(); i < n; i++) {
				PnModule currentModule = modules.get(i);
				Set pnSpaceHasModules = currentModule.getPnSpaceHasModules();
				if (pnSpaceHasModules != null) {
					Iterator spaceIterator = pnSpaceHasModules.iterator();
					while (spaceIterator.hasNext()) {
						PnSpaceHasModule currentPnSpaceHasModule = (PnSpaceHasModule) spaceIterator.next();
						if (((currentPnSpaceHasModule.getComp_id().getSpaceId().equals(spaceId)) && (currentPnSpaceHasModule.getIsActive() == 1))) {
							InsertObjectUtils.savePnModulePermission(currentModule.getModuleId(), currentPnSpaceHasModule.getComp_id().getSpaceId(), groupId, inheritGroupInterface
									.getActionsForPnModule(currentModule));
						}
					}
				}
			}
			List<PnObjectType> pnObjectTypes = ServiceFactory.getInstance().getPnObjectTypeService().findAll();
			for (int i = 0, n = pnObjectTypes.size(); i < n; i++) {
				PnObjectType currentPnObjectType = pnObjectTypes.get(i);
				/*
				 * INSERT INTO pn_default_object_permission (space_id, object_type, group_id, actions) VALUES (spaceId, object_type_rec.object_type, groupId,
				 * SECURITY.VIEW_PERMISSION_BITMASK);
				 */
				InsertObjectUtils.savePnDefaultObjectPermission(spaceId, currentPnObjectType.getObjectType(), groupId, inheritGroupInterface
						.getActionsForPnObjectType(currentPnObjectType));
			}
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(methodIsFinished);
		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(exceptionInMethod);
			e.printStackTrace();
		}
	}

	public void inheritGroup(Integer spaceId, Integer groupId, String permission) {
		PnSpaceHasGroup spaceHasGroup = null;
		String methodName = "inheritGroup";
		String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
		String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
		String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
		long startTime = System.currentTimeMillis();
		getLogger().debug(methodIsStart);
		try {
			List<PnSpaceHasGroup> listSpaceHasGroup = ServiceFactory.getInstance().getPnSpaceHasGroupService().getAll();
			for (int i = 0, n = listSpaceHasGroup.size(); i < n; i++) {
				PnSpaceHasGroup currentSpaceHasGroup = listSpaceHasGroup.get(i);
				if ((currentSpaceHasGroup.getComp_id().getSpaceId().equals(spaceId)) && (currentSpaceHasGroup.getComp_id().getGroupId().equals(groupId))
						&& (currentSpaceHasGroup.getIsOwner() == 0)) {
					spaceHasGroup = currentSpaceHasGroup;
					break;
				}
			}
			if (spaceHasGroup == null) {
				/*
				 * INSERT INTO pn_space_has_group (space_id, GROUP_ID, is_owner ) VALUES (spaceId, groupId, 0 );
				 */
				PnSpaceHasGroup newPnSpaceHasGroup = new PnSpaceHasGroup();
				PnSpaceHasGroupPK pkPnSpaceHasGroupPK = new PnSpaceHasGroupPK();
				newPnSpaceHasGroup.setComp_id(pkPnSpaceHasGroupPK);
				pkPnSpaceHasGroupPK.setGroupId(groupId);
				pkPnSpaceHasGroupPK.setSpaceId(spaceId);
				newPnSpaceHasGroup.setIsOwner(0);
				ServiceFactory.getInstance().getPnSpaceHasGroupService().saveSpaceHasGroup(newPnSpaceHasGroup);
				if (permission.equalsIgnoreCase(PERMISSION_SELECTION_NONE)) {

				} else {
					if (permission.equalsIgnoreCase(PERMISSION_SELECTION_VIEW)) {
						inheritGroupDetails(spaceId, groupId, permission, new InheritGroupInterface() {
							public long getActionsForPnModule(PnModule object) {
								return VIEW_PERMISSION_BITMASK;
							}

							public long getActionsForPnObjectType(PnObjectType object) {
								return VIEW_PERMISSION_BITMASK;
							}

						});
					} else {
						inheritGroupDetails(spaceId, groupId, permission, new InheritGroupInterface() {
							public long getActionsForPnModule(PnModule object) {
								return object.getDefaultPermissionActions();
							}

							public long getActionsForPnObjectType(PnObjectType object) {
								return object.getDefaultPermissionActions();
							}

						});
					}
				}
			}
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(methodIsFinished);
		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(exceptionInMethod);
			e.printStackTrace();
		}
	}

	public void inheritGroupFromSpace(Integer spaceId, Integer srcSpaceId, Integer groupId, String permission) {
		PnSpaceHasGroup spaceHasGroup = null;
		String methodName = "inheritGroupFromSpace";
		String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
		String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
		String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
		long startTime = System.currentTimeMillis();
		getLogger().debug(methodIsStart);
		try {
			List<PnSpaceHasGroup> listSpaceHasGroup = ServiceFactory.getInstance().getPnSpaceHasGroupService().getAll();
			for (int i = 0, n = listSpaceHasGroup.size(); i < n; i++) {
				PnSpaceHasGroup currentSpaceHasGroup = listSpaceHasGroup.get(i);
				if ((currentSpaceHasGroup.getComp_id().getSpaceId().equals(spaceId)) && (currentSpaceHasGroup.getComp_id().getGroupId().equals(groupId))
						&& (currentSpaceHasGroup.getIsOwner() == 0)) {
					spaceHasGroup = currentSpaceHasGroup;
					break;
				}
			}
			if (spaceHasGroup == null) {
				/*
				 * INSERT INTO pn_space_has_group (space_id, GROUP_ID, is_owner ) VALUES (spaceId, groupId, 0 );
				 */
				PnSpaceHasGroup newPnSpaceHasGroup = new PnSpaceHasGroup();
				PnSpaceHasGroupPK pkPnSpaceHasGroupPK = new PnSpaceHasGroupPK();
				newPnSpaceHasGroup.setComp_id(pkPnSpaceHasGroupPK);
				pkPnSpaceHasGroupPK.setGroupId(groupId);
				pkPnSpaceHasGroupPK.setSpaceId(spaceId);
				newPnSpaceHasGroup.setIsOwner(0);
				ServiceFactory.getInstance().getPnSpaceHasGroupService().saveSpaceHasGroup(newPnSpaceHasGroup);
				if (permission.equalsIgnoreCase(PERMISSION_SELECTION_NONE)) {

				} else {
					if (permission.equalsIgnoreCase(PERMISSION_SELECTION_VIEW)) {
						inheritGroupDetails(spaceId, groupId, permission, new InheritGroupInterface() {
							public long getActionsForPnModule(PnModule object) {
								return VIEW_PERMISSION_BITMASK;
							}

							public long getActionsForPnObjectType(PnObjectType object) {
								return VIEW_PERMISSION_BITMASK;
							}

						});
					} else {
						if (permission.equalsIgnoreCase(PERMISSION_SELECTION_INHERIT)) {
							List<PnSpaceHasModule> listPnSpaceHasModules = ServiceFactory.getInstance().getPnSpaceHasModuleService().findBySpaceId(spaceId);
							for (int i = 0, n = listPnSpaceHasModules.size(); i < n; i++) {
								PnSpaceHasModule currentPnSpaceHasModule = listPnSpaceHasModules.get(i);
								if (currentPnSpaceHasModule.getIsActive() == 1) {
									Set setModule = currentPnSpaceHasModule.getPnModulePermissions();
									if (setModule != null) {
										Iterator modulePermissionIterator = setModule.iterator();
										while (modulePermissionIterator.hasNext()) {
											PnModulePermission currentModulePermission = (PnModulePermission) modulePermissionIterator.next();
											if ((currentModulePermission.getComp_id().getGroupId().equals(groupId))
													&& (currentModulePermission.getComp_id().getSpaceId().equals(srcSpaceId))) {
												/*
												 * INSERT INTO pn_module_permission (module_id, space_id, group_id, actions) VALUES (module_rec.module_id, spaceId, groupId,
												 * module_rec.actions);
												 */
												InsertObjectUtils.savePnModulePermission(currentPnSpaceHasModule.getPnModule().getModuleId(), spaceId, groupId,
														currentModulePermission.getActions());
											}
										}
									}
								}
							}
							List<PnObjectType> pnObjectTypes = ServiceFactory.getInstance().getPnObjectTypeService().findAll();
							for (int i = 0, n = pnObjectTypes.size(); i < n; i++) {
								PnObjectType currentPnObjectType = pnObjectTypes.get(i);
								/*
								 * INSERT INTO pn_default_object_permission (space_id, object_type, group_id, actions) VALUES (spaceId, object_type_rec.object_type, groupId,
								 * SECURITY.VIEW_PERMISSION_BITMASK);
								 */
								InsertObjectUtils.savePnDefaultObjectPermission(spaceId, currentPnObjectType.getObjectType(), groupId, currentPnObjectType
										.getDefaultPermissionActions());
							}

						} else {
							inheritGroupDetails(spaceId, groupId, permission, new InheritGroupInterface() {
								public long getActionsForPnModule(PnModule object) {
									return object.getDefaultPermissionActions();
								}

								public long getActionsForPnObjectType(PnObjectType object) {
									return object.getDefaultPermissionActions();
								}

							});
						}
					}
				}
			}
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(methodIsFinished);
		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(exceptionInMethod);
			e.printStackTrace();
		}
	}

	public void removeInheritedGroup(final Integer spaceId, Integer groupId) {
		String methodName = "removeInheritedGroup";
		String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
		String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
		String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
		long startTime = System.currentTimeMillis();
		getLogger().debug(methodIsStart);
		try {
			ServiceFactory.getInstance().getPnSpaceHasGroupService().deleteByGroupIdAndOwner(groupId, 0);
			ServiceFactory.getInstance().getPnDefaultObjectPermissionService().deleteDefaultObjectPermisionsByGroupIdAndSpaceID(spaceId, groupId);
			ServiceFactory.getInstance().getPnModulePermissionService().deleteModulePermissionsBySpaceAndGroupId(spaceId, groupId);
			ServiceFactory.getInstance().getPnPagePermissionService().deletePermissionByGroupIdAndSpaceID(groupId, spaceId);
			List<PnSpaceHasGroup> list = ServiceFactory.getInstance().getPnSpaceHasGroupService().getAll();
			for (int i = 0, n = list.size(); i < n; i++) {
				final PnSpaceHasGroup currentPnSpaceHasGroup = list.get(i);
				if (currentPnSpaceHasGroup.getComp_id().getGroupId().equals(groupId)) {
					List<PnSpaceHasGroup> testList = ServiceFactory.getInstance().getPnSpaceHasGroupService().getByFilter(new IPnSpaceHasGroupServiceFilter() {
						public boolean isSuitable(PnSpaceHasGroup object) {
							return ((object.getComp_id().getSpaceId().equals(spaceId)) && (object.getComp_id().getGroupId()
									.equals(currentPnSpaceHasGroup.getComp_id().getGroupId())));
						}
					});
					if (testList.size() > 0) {
						ServiceFactory.getInstance().getPnSpaceHasGroupService().deleteSpaceHasGroup(currentPnSpaceHasGroup);
					}
				}
			}
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(methodIsFinished);
		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(exceptionInMethod);
			e.printStackTrace();
		}
	}

	public void retrofitSecurityPermissionsDetails(final Integer spaceId, Integer groupId, String permission, RetrofitSecurityPermissionsInterface getActionInterface) {
		String methodName = "retrofitSecurityPermissionsDetails";
		String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
		String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
		String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
		long startTime = System.currentTimeMillis();
		getLogger().debug(methodIsStart);
		try {
			List<PnObjectSpace> spaces = ServiceFactory.getInstance().getPnObjectSpaceService().getByFilter(new IPnObjectSpaceServiceFilter() {
				public boolean isSuitable(PnObjectSpace object) {
					return object.getComp_id().getSpaceId().equals(spaceId);
				}
			});
			for (int i = 0, n = spaces.size(); i < n; i++) {
				PnObjectSpace currentSpace = spaces.get(i);
				PnObject currentObject = ServiceFactory.getInstance().getPnObjectService().getObject(currentSpace.getComp_id().getObjectId());
				if (currentObject.getPnObjectType().getIsSecurable() == 1) {
					if (!currentObject.getObjectId().equals(groupId)) {
						InsertObjectUtils.insertPnObjectPermission(currentObject.getObjectId(), groupId, getActionInterface.getActionsForObject(currentObject));
					}
				}
			}
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(methodIsFinished);
		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(exceptionInMethod);
			e.printStackTrace();
		}
	}

	public void retrofitSecurityPermissions(final Integer spaceId, Integer groupId, String permission) {
		String methodName = "retrofitSecurityPermissions";
		String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
		String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
		String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
		long startTime = System.currentTimeMillis();
		getLogger().debug(methodIsStart);
		try {
			if (PERMISSION_SELECTION_VIEW.equalsIgnoreCase(permission)) {
				retrofitSecurityPermissionsDetails(spaceId, groupId, permission, new RetrofitSecurityPermissionsInterface() {
					public long getActionsForObject(PnObject object) {
						return VIEW_PERMISSION_BITMASK;
					}
				});
			} else {
				retrofitSecurityPermissionsDetails(spaceId, groupId, permission, new RetrofitSecurityPermissionsInterface() {
					public long getActionsForObject(PnObject object) {
						return object.getPnObjectType().getDefaultPermissionActions();
					}
				});
			}
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(methodIsFinished);
		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(exceptionInMethod);
			e.printStackTrace();
		}
	}

	public Integer copyDefPermUserDefGroups(final String fromGroupId, final String toGroupId, final String fromSpaceId, final String toSpaceId) {
		Integer result = new Integer(0);
		String methodName = "copyDefPermUserDefGroups";
		String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
		String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
		String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
		long startTime = System.currentTimeMillis();
		getLogger().debug(methodIsStart);
		try {
			List<PnDefaultObjectPermission> cursor = ServiceFactory.getInstance().getPnDefaultObjectPermissionService().getByFilter(new IPnDefaultObjectPermissionFilter() {
				public boolean isSuitable(PnDefaultObjectPermission object) {
					return ((object.getComp_id().getSpaceId().equals(new Integer(fromSpaceId))) && (object.getComp_id().getGroupId().equals(fromGroupId)) && (object.getPnGroup()
							.getPnGroupType().getGroupTypeId().equals(GROUP_TYPE_USERDEFINED)));
				}
			});
			for (int i = 0, n = cursor.size(); i < n; i++) {
				final PnDefaultObjectPermission currentPnDefaultObjectPermission = cursor.get(i);
				List<PnDefaultObjectPermission> updateableValues = ServiceFactory.getInstance().getPnDefaultObjectPermissionService().getByFilter(
						new IPnDefaultObjectPermissionFilter() {
							public boolean isSuitable(PnDefaultObjectPermission object) {
								return ((object.getComp_id().getSpaceId().equals(new Integer(toSpaceId))) && (object.getComp_id().getGroupId().equals(new Integer(toGroupId))) && (object
										.getPnObjectType().getObjectType().equalsIgnoreCase(currentPnDefaultObjectPermission.getPnObjectType().getObjectType())));
							}
						});
				for (int j = 0, k = updateableValues.size(); j < k; j++) {
					PnDefaultObjectPermission updateValue = updateableValues.get(j);
					updateValue.setActions(currentPnDefaultObjectPermission.getActions());
					ServiceFactory.getInstance().getPnDefaultObjectPermissionService().updateDefaultObjectPermission(updateValue);
				}
			}
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(methodIsFinished);
		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(exceptionInMethod);
			e.printStackTrace();
		}
		return result;
	}

	public Integer copyModPermUserDefGroups(final String fromGroupId, final String toGroupId, final String fromSpaceId, final String toSpaceId) {
		Integer result = new Integer(0);
		String methodName = "copyModPermUserDefGroups";
		String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
		String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
		String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
		long startTime = System.currentTimeMillis();
		getLogger().debug(methodIsStart);
		try {
			List<PnModulePermission> cursor = ServiceFactory.getInstance().getPnModulePermissionService().findByFilter(new IPnModulePermissionFilter() {
				public boolean isSuitable(PnModulePermission object) {
					return ((object.getComp_id().getSpaceId().equals(new Integer(fromSpaceId))) && (object.getComp_id().getGroupId().equals(fromGroupId)) && (object.getPnGroup()
							.getPnGroupType().getGroupTypeId().equals(GROUP_TYPE_USERDEFINED)));
				}
			});
			for (int i = 0, n = cursor.size(); i < n; i++) {
				final PnModulePermission currentPnModulePermission = cursor.get(i);
				List<PnModulePermission> updateableValues = ServiceFactory.getInstance().getPnModulePermissionService().findByFilter(new IPnModulePermissionFilter() {
					public boolean isSuitable(PnModulePermission object) {
						return ((object.getComp_id().getSpaceId().equals(new Integer(toSpaceId))) && (object.getComp_id().getGroupId().equals(new Integer(toGroupId))) && (object
								.getComp_id().getModuleId().equals(currentPnModulePermission.getComp_id().getModuleId())));
					}
				});
				for (int j = 0, k = updateableValues.size(); j < k; j++) {
					PnModulePermission updateValue = updateableValues.get(j);
					updateValue.setActions(currentPnModulePermission.getActions());
					ServiceFactory.getInstance().getPnModulePermissionService().updateModulePermission(updateValue);
				}
			}
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(methodIsFinished);
		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(exceptionInMethod);
			e.printStackTrace();
		}
		return result;
	}

	public Integer copyGroups(final String fromSpaceId, final String toSpaceId, final String createdById) {
		Integer result = new Integer(0);
		String methodName = "copyGroups";
		String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
		String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
		String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
		long startTime = System.currentTimeMillis();
		getLogger().debug(methodIsStart);
		try {
			List<PnSpaceHasGroup> groupCur = ServiceFactory.getInstance().getPnSpaceHasGroupService().getByFilter(new IPnSpaceHasGroupServiceFilter() {
				public boolean isSuitable(PnSpaceHasGroup object) {
					PnGroup currentGroup = object.getPnGroup();
					return ((object.getIsOwner() == 1) && (object.getComp_id().getSpaceId().equals(new Integer(fromSpaceId))) && (currentGroup.getIsSystemGroup() == 0 || currentGroup
							.getIsSystemGroup() == 600));
				}
			});
			for (int i = 0, n = groupCur.size(); i < n; i++) {
				PnSpaceHasGroup currentSpaceHasGroup = groupCur.get(i);
				Integer vGroupId = storeGroup(currentSpaceHasGroup.getPnGroup().getGroupName(), currentSpaceHasGroup.getPnGroup().getGroupDesc(), new Integer(0), new Integer(0),
						currentSpaceHasGroup.getPnGroup().getPnGroupType().getGroupTypeId(), new Integer(createdById), new Integer(toSpaceId), null);
				/*
				 * copyModPermUserDefGroups ( group_rec.group_id , vGroupId , fromSpaceId , toSpaceId , o_return_value ) ;
				 */
				copyModPermUserDefGroups(currentSpaceHasGroup.getPnGroup().getGroupId().toString(), vGroupId.toString(), fromSpaceId, toSpaceId);
				/*
				 * copyDefPermUserDefGroups ( group_rec.group_id , vGroupId , fromSpaceId , toSpaceId , o_return_value ) ;
				 */
				copyDefPermUserDefGroups(currentSpaceHasGroup.getPnGroup().getGroupId().toString(), vGroupId.toString(), fromSpaceId, toSpaceId);

			}
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(methodIsFinished);
		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(exceptionInMethod);
			e.printStackTrace();
		}
		return result;
	}

	private Integer getBusinessAdminId(final String fromSpaceId, final String toSpaceId, final String createdById) {
		Integer bussinesAdminIdBuff = null;
		String methodName = "getBusinessAdminId";
		String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
		String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
		String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
		long startTime = System.currentTimeMillis();
		getLogger().debug(methodIsStart);
		try {
			List<PnSpaceHasSpace> allHasSpaces = ServiceFactory.getInstance().getPnSpaceHasSpaceService().findByFilter(new IPnSpaceHasSpaceFilter() {
				public boolean isSuitable(PnSpaceHasSpace object) {
					return ((object.getChildSpaceType().equalsIgnoreCase(toSpaceId)) && (object.getRelationshipParentToChild().equalsIgnoreCase("owns")));
				}
			});
			boolean needExit = false;
			for (int i = 0, n = allHasSpaces.size(); i < n; i++) {
				if (needExit)
					break;
				final PnSpaceHasSpace currentPnSpaceHasSpace = allHasSpaces.get(i);
				List<PnSpaceHasGroup> listSpaceHasGroup = ServiceFactory.getInstance().getPnSpaceHasGroupService().getByFilter(new IPnSpaceHasGroupServiceFilter() {
					public boolean isSuitable(PnSpaceHasGroup object) {
						return ((object.getComp_id().getSpaceId().equals(currentPnSpaceHasSpace.getComp_id().getParentSpaceId()))
								&& (object.getPnGroup().getPnGroupType().getGroupTypeId().equals(GROUP_TYPE_SPACEADMIN)) && (object.getIsOwner() == 1));
					}
				});
				for (int j = 0, k = listSpaceHasGroup.size(); j < k; j++) {
					PnSpaceHasGroup currentPnSpaceHasGroup = listSpaceHasGroup.get(j);
					needExit = true;
					bussinesAdminIdBuff = currentPnSpaceHasGroup.getPnGroup().getGroupId();
					break;
				}
			}
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(methodIsFinished);
		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(exceptionInMethod);
			e.printStackTrace();
		}
		return bussinesAdminIdBuff;

	}

	public void copyModulePermissions(final String fromSpaceId, final String toSpaceId, final String createdById) {
		Integer bussinesAdminIdBuff = getBusinessAdminId(fromSpaceId, toSpaceId, createdById);
		Integer groupIdBuff = null;
		String methodName = "copyModulePermissions";
		String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
		String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
		String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
		long startTime = System.currentTimeMillis();
		getLogger().debug(methodIsStart);
		try {
			List<PnModulePermission> modulePermissions = ServiceFactory.getInstance().getPnModulePermissionService().findByFilter(new IPnModulePermissionFilter() {
				public boolean isSuitable(PnModulePermission object) {
					return ((object.getComp_id().getSpaceId().equals(new Integer(fromSpaceId)))
							&& (!object.getPnGroup().getPnGroupType().getGroupTypeId().equals(GROUP_TYPE_USERDEFINED))
							&& (!object.getPnGroup().getPnGroupType().getGroupTypeId().equals(GROUP_TYPE_EVERYONE)) && (object.getPnGroup().getIsPrincipal() != 1));
				}
			});
			for (int i = 0, n = modulePermissions.size(); i < n; i++) {
				final PnModulePermission currentModulePermission = modulePermissions.get(i);
				final Integer bussinesAdminId = bussinesAdminIdBuff;
				if (bussinesAdminId != null) {
					List<PnSpaceHasGroup> listSpaceHasGroup = ServiceFactory.getInstance().getPnSpaceHasGroupService().getByFilter(new IPnSpaceHasGroupServiceFilter() {
						public boolean isSuitable(PnSpaceHasGroup object) {
							return ((object.getComp_id().getSpaceId().equals(new Integer(toSpaceId)))
									&& (object.getPnGroup().getPnGroupType().getGroupTypeId().equals(currentModulePermission.getPnGroup().getPnGroupType().getGroupTypeId())) && (!object
									.getPnGroup().getGroupId().equals(bussinesAdminId)));
						}
					});
					if (listSpaceHasGroup.size() > 0) {
						groupIdBuff = listSpaceHasGroup.get(0).getPnGroup().getGroupId();
					}
				} else {
					List<PnSpaceHasGroup> listSpaceHasGroup = ServiceFactory.getInstance().getPnSpaceHasGroupService().getByFilter(new IPnSpaceHasGroupServiceFilter() {
						public boolean isSuitable(PnSpaceHasGroup object) {
							return ((object.getComp_id().getSpaceId().equals(new Integer(toSpaceId))) && (object.getPnGroup().getPnGroupType().getGroupTypeId()
									.equals(currentModulePermission.getPnGroup().getPnGroupType().getGroupTypeId())));
						}
					});
					if (listSpaceHasGroup.size() > 0) {
						groupIdBuff = listSpaceHasGroup.get(0).getPnGroup().getGroupId();
					}
				}
				final Integer groupId = groupIdBuff;
				List<PnModulePermission> listPnModulePermissions = ServiceFactory.getInstance().getPnModulePermissionService().findByFilter(new IPnModulePermissionFilter() {
					public boolean isSuitable(PnModulePermission object) {
						return ((object.getComp_id().getSpaceId().equals(new Integer(toSpaceId))) && (object.getPnGroup().getGroupId().equals(groupId)) && (object.getComp_id()
								.getModuleId().equals(currentModulePermission.getComp_id().getModuleId())));
					}
				});
				for (int j = 0, k = listPnModulePermissions.size(); j < k; j++) {
					PnModulePermission currentUpdatableModulePermission = listPnModulePermissions.get(j);
					currentUpdatableModulePermission.setActions(currentModulePermission.getActions());
					ServiceFactory.getInstance().getPnModulePermissionService().updateModulePermission(currentUpdatableModulePermission);
				}
			}
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(methodIsFinished);
		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(exceptionInMethod);
			e.printStackTrace();
		}

	}

	public void copyDefaultPermissions(final String fromSpaceId, final String toSpaceId, final String createdById) {
		Integer bussinesAdminIdBuff = getBusinessAdminId(fromSpaceId, toSpaceId, createdById);
		Integer vGroupIdBuff = null;
		String methodName = "copyDefaultPermissions";
		String methodIsFinished = DebugMethodUtilites.getMethodIsFinishedValue(methodName);
		String methodIsStart = DebugMethodUtilites.getStartMethodValue(methodName);
		String exceptionInMethod = DebugMethodUtilites.getExceptionMethodValue(methodName);
		long startTime = System.currentTimeMillis();
		getLogger().debug(methodIsStart);
		try {
			List<PnDefaultObjectPermission> modulePermissions = ServiceFactory.getInstance().getPnDefaultObjectPermissionService().getByFilter(
					new IPnDefaultObjectPermissionFilter() {
						public boolean isSuitable(PnDefaultObjectPermission object) {
							return ((object.getComp_id().getSpaceId().equals(new Integer(fromSpaceId)))
									&& (!object.getPnGroup().getPnGroupType().getGroupTypeId().equals(GROUP_TYPE_USERDEFINED))
									&& (!object.getPnGroup().getPnGroupType().getGroupTypeId().equals(GROUP_TYPE_EVERYONE)) && (object.getPnGroup().getIsPrincipal() != 1));
						}
					});
			for (int i = 0, n = modulePermissions.size(); i < n; i++) {
				final PnDefaultObjectPermission currentDefaultObjectPermission = modulePermissions.get(i);
				final Integer vBussinesAdminId = bussinesAdminIdBuff;
				if (vBussinesAdminId != null) {
					List<PnSpaceHasGroup> listSpaceHasGroup = ServiceFactory.getInstance().getPnSpaceHasGroupService().getByFilter(new IPnSpaceHasGroupServiceFilter() {
						public boolean isSuitable(PnSpaceHasGroup object) {
							return ((object.getComp_id().getSpaceId().equals(new Integer(toSpaceId)))
									&& (object.getPnGroup().getPnGroupType().getGroupTypeId().equals(currentDefaultObjectPermission.getPnGroup().getPnGroupType().getGroupTypeId())) && (!object
									.getPnGroup().getGroupId().equals(vBussinesAdminId)));
						}
					});
					if (listSpaceHasGroup.size() > 0) {
						vGroupIdBuff = listSpaceHasGroup.get(0).getPnGroup().getGroupId();
					}
				} else {
					final Integer vToGroupId = vGroupIdBuff;
					List<PnDefaultObjectPermission> listUpdateObjectPermissions = ServiceFactory.getInstance().getPnDefaultObjectPermissionService().getByFilter(
							new IPnDefaultObjectPermissionFilter() {
								public boolean isSuitable(PnDefaultObjectPermission object) {
									return ((object.getComp_id().getSpaceId().equals(new Integer(toSpaceId))) && (object.getPnGroup().getGroupId().equals(vToGroupId)) && (object
											.getPnObjectType().getObjectType().equalsIgnoreCase(currentDefaultObjectPermission.getPnObjectType().getObjectType())));
								}
							});
					for (int j = 0, k = listUpdateObjectPermissions.size(); j < k; j++) {
						PnDefaultObjectPermission currentUpdatableModulePermission = listUpdateObjectPermissions.get(j);
						currentUpdatableModulePermission.setActions(currentDefaultObjectPermission.getActions());
						ServiceFactory.getInstance().getPnDefaultObjectPermissionService().updateDefaultObjectPermission(currentUpdatableModulePermission);
					}
				}
			}
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(methodIsFinished);
		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long timeExecution = endTime - startTime;
			String messageTimeExecution = DebugMethodUtilites.getTimeExecution(methodName, timeExecution);
			getLogger().debug(messageTimeExecution);
			getLogger().debug(exceptionInMethod);
			e.printStackTrace();
		}
	}
}

interface InheritGroupInterface {
	public long getActionsForPnModule(PnModule object);

	public long getActionsForPnObjectType(PnObjectType object);
}

interface RetrofitSecurityPermissionsInterface {
	public long getActionsForObject(PnObject object);
}