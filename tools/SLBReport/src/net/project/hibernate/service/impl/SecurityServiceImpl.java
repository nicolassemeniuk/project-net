package net.project.hibernate.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;

import net.project.hibernate.constants.SecurityConstants;
import net.project.hibernate.dao.IPnObjectDAO;
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
import net.project.hibernate.model.PnObjectType;
import net.project.hibernate.model.PnSpaceHasGroup;
import net.project.hibernate.model.PnSpaceHasGroupPK;
import net.project.hibernate.service.IPnDefaultObjectPermissionService;
import net.project.hibernate.service.IPnGroupHasPersonService;
import net.project.hibernate.service.IPnGroupService;
import net.project.hibernate.service.IPnModulePermissionService;
import net.project.hibernate.service.IPnModuleService;
import net.project.hibernate.service.IPnObjectPermissionService;
import net.project.hibernate.service.IPnObjectService;
import net.project.hibernate.service.IPnObjectTypeService;
import net.project.hibernate.service.IPnSpaceHasGroupService;
import net.project.hibernate.service.ISecurityService;
import net.project.hibernate.service.ServiceFactory;

public class SecurityServiceImpl implements ISecurityService {

	private static final int G_ALL_ACTIONS = 65535;

	public BigDecimal createSpaceAdminGroup(BigDecimal creatorPersonId, BigDecimal spaceId, String description) {
		BigDecimal groupObject = new BigDecimal(0);
		try {
			// get Object service
			IPnObjectService objectService = ServiceFactory.getInstance().getPnObjectService();
			// create object
			groupObject = objectService.saveObject(new PnObject("group", creatorPersonId, new Date(System.currentTimeMillis()), "A"));
			// get PnGroup service
			IPnGroupService groupService = ServiceFactory.getInstance().getPnGroupService();
			// save new Group
			groupService.saveGroup(new PnGroup(groupObject, "@prm.security.group.type.spaceadministrator.name", description, 0, 1, "A", SecurityConstants.GROUP_TYPE_SPACEADMIN));
			// get PnGroupHasPerson service
			IPnGroupHasPersonService groupHasPersonService = ServiceFactory.getInstance().getPnGroupHasPersonService();
			// save PnGroupHasPersonService
			groupHasPersonService.saveGroupHasPerson(new PnGroupHasPerson(new PnGroupHasPersonPK(groupObject, creatorPersonId)));
			// get PnSpaceHasGroup service
			IPnSpaceHasGroupService spaceHasGroupService = ServiceFactory.getInstance().getPnSpaceHasGroupService();
			// save PnSpaceHasGroup
			spaceHasGroupService.saveSpaceHasGroup(new PnSpaceHasGroup(new PnSpaceHasGroupPK(spaceId, groupObject), 1));
			// get PnObjectPermission service
			IPnObjectPermissionService objectPermissionService = ServiceFactory.getInstance().getPnObjectPermissionService();
			// save PnObjectPermission
			objectPermissionService.saveObjectPermission(new PnObjectPermission(new PnObjectPermissionPK(groupObject, groupObject), G_ALL_ACTIONS));
			// get PnObjectType service
			IPnObjectTypeService pnObjectTypeService = ServiceFactory.getInstance().getPnObjectTypeService();
			// get list of all PnObjectType objects, only objectType and
			// defaultPermissionActions are selected
			Iterator pnObjectTypeIterator = pnObjectTypeService.findObjectTypes().iterator();
			// get PnDefaultObjectPermission service
			IPnDefaultObjectPermissionService pnDefaultObjectPermissionService = ServiceFactory.getInstance().getPnDefaultObjectPermissionService();
			// saves all PnDefaultObjectPermission objects
			while (pnObjectTypeIterator.hasNext()) {
				PnObjectType pnObjectType = (PnObjectType) pnObjectTypeIterator.next();
				pnDefaultObjectPermissionService.saveDefaultObjectPermission(new PnDefaultObjectPermission(new PnDefaultObjectPermissionPK(spaceId, pnObjectType.getObjectType(), groupObject), G_ALL_ACTIONS));
			}
			// get PnModule service
			IPnModuleService moduleService = ServiceFactory.getInstance().getPnModuleService();
			// get List of moduleIds and defaultPermissionActions from PnModule
			Iterator pnModuleIterator = moduleService.getModuleDefaultPermissions(spaceId).iterator();
			// get PnModulePermission service
			IPnModulePermissionService modulePermissionService = ServiceFactory.getInstance().getPnModulePermissionService();
			// save all PnModulePermission objects
			while (pnModuleIterator.hasNext()) {
				PnModule pnModule = (PnModule) pnModuleIterator.next();
				// saves permissions for all modules
				modulePermissionService.saveModulePermission(new PnModulePermission(new PnModulePermissionPK(spaceId, groupObject, pnModule.getModuleId()), G_ALL_ACTIONS));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupObject;
	}

	public void createSecurityPermissions(BigDecimal objectId, String objectType, BigDecimal spaceId, BigDecimal personId) {
		try {
			// get PnGroup service
			IPnGroupService groupService = ServiceFactory.getInstance().getPnGroupService();
			// get groupId
			BigDecimal groupId = groupService.getGroupId(spaceId,personId);
			if (groupId != null){
				// get PnObjectPermission service
				IPnObjectPermissionService objectPermissionService = ServiceFactory.getInstance().getPnObjectPermissionService();
				// save PnObjectPermission object
				objectPermissionService.saveObjectPermission(new PnObjectPermission(new PnObjectPermissionPK(objectId, groupId), G_ALL_ACTIONS));
			}
			// get PnDefaultObjectPermissions
			IPnDefaultObjectPermissionService defaultObjectPermissionService = ServiceFactory.getInstance().getPnDefaultObjectPermissionService();
			// get Iterator of PnDefaultObjectPermission properties
			Iterator it = defaultObjectPermissionService.getObjectPermisions(spaceId, objectType);
			// get PnObjectPermission service
			IPnObjectPermissionService objectPermissionService = ServiceFactory.getInstance().getPnObjectPermissionService();
			while(it.hasNext()){
				Object[] obj = (Object[])it.next();
				if (obj[0] != null){
					objectPermissionService.saveObjectPermission(new PnObjectPermission(new PnObjectPermissionPK(objectId, (BigDecimal)obj[0]), (Long)obj[1]));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
