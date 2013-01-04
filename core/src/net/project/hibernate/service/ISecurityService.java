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
package net.project.hibernate.service;



public interface ISecurityService {

    public Integer createSpaceAdminGroup(Integer creatorPersonId, Integer spaceId, String description);

    public void createSecurityPermissions(Integer objectId, String objectType, Integer projectId, Integer personId);

    public boolean addPersonToGroup(Integer groupId, Integer personId);

    public boolean storeModulePermission(Integer spaceId, Integer groupId, Integer moduleId, long actions);

    public boolean storeObjectPermission(Integer objectId, Integer groupId, long actions);

    public boolean storeDefaultObjPermission(Integer spaceId, String objectType, Integer groupId, long actions);

    public boolean removeGroupPermission(Integer groupId);

    /**
     * Gets the list of existing security object Id's and remediates the security permissions for the new Adming group
     * @param newGroupId new SpaceAdmin ID
     * @param spaceAdminGroupId old SpaceAdmin ID
     */
    public void applyAdminPermissions(Integer newGroupId, Integer spaceAdminGroupId);

    public Integer storeGroup(String groupName, String groupDesc, Integer isPrincipal, Integer isSystemGroup, Integer groupTypeId, Integer creatorPersonId, Integer spaceId, Integer groupId);

    public Integer createTeamMemberGroup(Integer creatorId, Integer spaceId);

    public Integer createPrincipalGroup(Integer creatorId, Integer spaceId);

    public void applyDocumentPermissions(Integer pObjectId, Integer pParentId, String pObjectType, Integer pSpaceId, Integer pPersonId);

    public Integer createPowerUserGroup(Integer creatorPersonId, Integer spaceId, String description);

    public Integer createParentAdminRole(Integer spaceId, Integer parentSpaceId);

    public void grantModulePermissions(Integer spaceId, Integer groupId, String permission);

    public void setNewObjectPermissions(Integer spaceId, Integer groupId, String permission);

    public void inheritGroup(Integer spaceId, Integer groupId, String permission);

    public void inheritGroupFromSpace(Integer spaceId, Integer srcSpaceId, Integer groupId, String permission);

    public void removeInheritedGroup(final Integer spaceId, Integer groupId);

    public void retrofitSecurityPermissions(final Integer spaceId, Integer groupId, String permission);

    public Integer copyDefPermUserDefGroups(String fromGroupId, String toGroupId, String fromSpaceId, String toSpaceId);

    public Integer copyModPermUserDefGroups(String fromGroupId, String toGroupId, String fromSpaceId, String toSpaceId);

    public Integer copyGroups(String fromSpaceId, String toSpaceId, String createdById);

    public void copyModulePermissions(String fromSpaceId, String toSpaceId, String createdById);

    public void copyDefaultPermissions(String fromSpaceId, String toSpaceId, String createdById);

    public static final Integer GROUP_TYPE_USERDEFINED = new Integer(100);
    public static final Integer GROUP_TYPE_SPACEADMIN = new Integer(200);
    public static final Integer GROUP_TYPE_TEAMMEMBER = new Integer(300);
    public static final Integer GROUP_TYPE_PRINCIPAL = new Integer(400);
    public static final Integer GROUP_TYPE_POWERUSER = new Integer(500);
    public static final Integer GROUP_TYPE_EVERYONE = new Integer(600);

    public static final String PERMISSION_SELECTION_NONE = "none";
    public static final String PERMISSION_SELECTION_VIEW = "view";
    public static final String PERMISSION_SELECTION_DEFAULT = "default";
    public static final String PERMISSION_SELECTION_INHERIT = "inherit";

    public static final long VIEW_PERMISSION_BITMASK = 1;
}
