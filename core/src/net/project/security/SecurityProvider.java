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

 package net.project.security;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.project.base.Module;
import net.project.base.ModuleCollection;
import net.project.base.PnetException;
import net.project.base.UnexpectedStaticVariableException;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.DBExceptionFactory;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpace;
import net.project.project.ProjectVisibility;
import net.project.resource.UserActivity;
import net.project.security.cache.SecurityCache;
import net.project.security.group.Group;
import net.project.security.group.GroupCollection;
import net.project.security.group.GroupException;
import net.project.security.group.GroupProvider;
import net.project.security.group.GroupTypeID;
import net.project.space.Space;
import net.project.space.SpaceTypes;

import org.apache.log4j.Logger;

/**
 * SecurityProvider performs security checking and cacheing for ACLs.
 *
 * @author Michael Ariston
 * @since 2/2000
 */
public class SecurityProvider implements Serializable {
    public static final String SECURITY_PROVIDER_SESSION_OBJECT_NAME = "securityProvider";

    /** Application Administrator user id */
    public static final String APPLICATION_ADMINISTRATOR_ID = "1";

    private static final int ACTION_DENIED = 0;
    private static final int ACTION_ALLOWED = 1;
    private static final int ACTION_UNDEFINED = 2;

    /**
     * The maximum number of groups permitted in a sublist when loading
     * security permission by group.
     * <p/>
     * This is to workaround Oracle limitations on expressions, it has a limit of 1,000 expressions
     * per SQL query.
     */
    private static final int MAX_GROUP_SUBLIST = 900;

    /**
     * the space context.
     * @deprecated as of 7.6.2; No replacement. This member will be made private.
     */
    protected Space space = null;

    /**
     * the User context
     * @deprecated as of 7.6.2; No replacement. This member will be made private.
     */
    protected User user = null;

    /** @deprecated as of 7.6.2; No replacement. This member will be made private. */
    protected int m_moduleID = 0;
    /** @deprecated as of 7.6.2; No replacement. This member will be made private. */
    protected int m_actionID = 0;
    /** @deprecated as of 7.6.2; No replacement. This method will be made private. */
    protected String m_objectID = null;

    private SecurityCache m_cache = null;

    /** Collection of Actions for resolving display names. */
    private ActionList actions = null;

    /** Collection of Modules for resolving display names. */
    private ModuleCollection modules = null;

    /**
     * Get the active instance of the securityProvider from the session.
     * If there is no securityProvider currently in the session, make a new one and place it in session
     *
     * @return An instantiated "singleton" instance of the session persisted securityProvider object.
     * @since Gecko
     */
    public static SecurityProvider getInstance() {

        SecurityProvider securityProvider =
            SessionManager.getSecurityProvider();

        if (securityProvider == null) {

            securityProvider = new SecurityProvider();
            SessionManager.setSecurityProvider(securityProvider);
        }

        return securityProvider;
    }

    /**
     * Forces the time out to occur on the Security Cache
     *
     */
    public void forceTimeOutOfSecurityCache() {
        m_cache.forceTimeOut();
    }

    /**
     * Converts the Group ArrayList to a comma seperated list of Group IDs
     * @param groupCollection the collection of <code>Group</code>s
     * @return a comma separated list of group ids
     */
    public static String arrayListToString(Collection groupCollection) {
        Group group;
        StringBuffer buff = new StringBuffer();

        Iterator groupIt = groupCollection.iterator();
        while (groupIt.hasNext()) {
            group = (Group)groupIt.next();

            if (buff.length() > 0) {
                buff.append(", ");
            }

            buff.append(group.getID());
        }

        return buff.toString();
    }

    /**
     * Creates a new, empty SecurityProvider with an empty cache.
     */
    public SecurityProvider() {
        m_cache = new SecurityCache();
    }

    /**
     * Sets the Space context for the SecurityProvider.
     * If the specified space is different from the current space then
     * the security cache is cleared.
     * @param space the new space context
     */
    public void setSpace(Space space) {

        if (this.space == null ||
            this.space.getID() == null ||
            space == null ||
            !this.space.getID().equals(space.getID())) {

            clearCache();
        }
        this.space = space;
        m_cache.setSpace(space);
    }

    /**
     * Gets the Space context for the SecurityProvider.
     * @return the current space context
     */
    public Space getSpace() {
        return this.space;
    }

    /**
     * Sets the new User context for the SecurityProvider.
     * If the specified user is different from the current user then the
     * security cache is cleared.
     * @param user the new user context
     */
    public void setUser(User user) {
        if (this.user == null || this.user.getID() == null || !this.user.getID().equals(user.getID())) {
            clearCache();
        }

        this.user = user;
        m_cache.setUser(user);
    }

    /**
     * Returns the current User context.
     * @return the current user
     */
    protected User getUser() {
        return this.user;
    }

    /**
     * Sets the current Space and user Context
     * @param user the new user context
     * @param space the new space context
     */
    public void setCurrentSpace(User user, Space space) {
        setSpace(space);
        setUser(user);
    }

    /**
     * Checks that the specified action is allowed against the specified
     * module, optionally checking the user's principal group also.
     * If this has previously been checked (that is, is available in the cache)
     * that cached value is returned.<br>
     * <b>Note:</b> When checking, the list of module permissions is loaded from
     * the cache.  This means:
     * <li>If the permissions for a module are modified, the cache will not
     * reflect those until it is reloaded</li>
     * <li>Once a module/action combination has been checked, it is never
     * re-checked</li>
     * <br>
     * The checked value is placed in the cache.
     * @param module the module to check
     * @param action the action to check
     * @param checkPrincipal true means check the user's principal group
     * also, if that group has security settings
     * @return whether the action was allowed or denied:<br>
     * <li>{@link #ACTION_DENIED} Action is not allowed on the module</li>
     * <li>{@link #ACTION_ALLOWED} Action is allowed on the module</li>
     * <li>{@link #ACTION_UNDEFINED} checkPrincipal was true but the user's
     * principal group has no specific security settings for the action and
     * module; the action was neither allowed nor denied.  In this case,
     * the security should be based on calling <code>checkModule</code> with
     * checkPrincipal set to <code>false</code></li>
     */
    private int checkModule(String module, int action, boolean checkPrincipal) {
        int returnValue;
        boolean usePrincipal = false;
        int roleActions = 0;
        int principalActions = 0;
        String principalValue;

        ModulePermission modulePermission;
        String moduleID;
        String spaceID;
        String groupID;


        // User is always allowed to perform any action on their personal space
        if (module.equals(Integer.toString(Module.PERSONAL_SPACE))) {
            return ACTION_ALLOWED;
        }

        // If this check is cached, return the result
        if ((returnValue = m_cache.checkModule(module, action, checkPrincipal)) >= 0) {
            return returnValue;
        }

        try {
            // Loop through all the cached module permissions, building
            // the the Actions bitset from the permissions for each module
            List moduleList = m_cache.getModules();

            // The principalValue is the principal groupID or "NO_ID"
            // if the user has no principal group in the current space
            principalValue = getPrincipalGroup();

            Iterator moduleIt = moduleList.iterator();
            while (moduleIt.hasNext()) {
                modulePermission = (ModulePermission)moduleIt.next();

                moduleID = modulePermission.getModule().getId();
                spaceID = modulePermission.getSpace().getID();
                groupID = modulePermission.getGroup().getID();

                if (moduleID.equals(module) && spaceID.equals(getSpace().getID())) {
                    if (principalValue.equals(groupID)) {
                        usePrincipal = true;
                        principalActions = modulePermission.getActionsBits();

                    } else {
                        roleActions |= modulePermission.getActionsBits();
                    }
                }
            } //end while


            // Now check the permissions based on the action bitset
            // 10/25/2001 - Tim
            // Seems to be a rather poor re-use of ObjectPermission
            // Why not have static methods isActionAllowed(int actionBitSet, int action)
            ObjectPermission permission = new ObjectPermission();
            permission.clearAll();

            if (checkPrincipal) {
                // We are checking the principal group

                if (usePrincipal) {
                    // User's principal group has specific permissions
                    // Check to see if it allows or denies the action
                    permission.setActionBits(principalActions);

                    if (permission.actionAllowed(action)) {
                        returnValue = ACTION_ALLOWED;

                    } else {
                        returnValue = ACTION_DENIED;

                    }

                } else {
                    // Principal group not involved;  neither denied nor allowed
                    returnValue = ACTION_UNDEFINED;

                }

            } else {
                permission.setActionBits(roleActions);

                if (permission.actionAllowed(action)) {
                    returnValue = ACTION_ALLOWED;

                } else {
                    returnValue = ACTION_DENIED;

                }
            }

        } catch (PersistenceException pe) {
        	// Allow view action for global projects
            if(Action.VIEW == action && getSpace().isTypeOf(SpaceTypes.PROJECT_SPACE) 
            		&& ((ProjectSpace) getSpace()).getVisibility().equals(ProjectVisibility.GLOBAL)) {
            	returnValue = ACTION_ALLOWED;
            } else {
            	// Action is denied
                returnValue = ACTION_DENIED;
            }

        }

        // Cache the checked permission
        m_cache.addModuleCheck(module, action, checkPrincipal, returnValue);

        return returnValue;
    }


    /**
     * Checks the permission for the specified action against the specified
     * object.
     * If this has previously been checked (that is, is available in the cache)
     * that cached value is returned.<br>
     * <b>Note:</b> When checking, the list of object permissions is NOT loaded from
     * the cache but the groups that the user is a member of are.  This means:
     * <li>If a group is added to a space, the cache will not reflect that group's
     * permissions, since the list of groups won't be reloaded</li>
     * <li>If the permissions for an object are changed for an existing group,
     * and that object/action combination has never previously been checked,
     * then these new permissions <i>will</i> be checked</li>
     * <li>Once an object has been checked for a certain action, it will never
     * be re-checked (even if permissions change) until the cache is cleared</li>
     * <br>
     * The checked value is placed in the cache.
     * @param objectID the id of the object to check
     * @param action the action to check
     * @param checkPrincipal true means check the user's principal group
     * @param defaultToModuleSecurity - default to module security if object permission not defined
     * also, if that group has security settings
     * @return whether the action was allowed or denied:<br>
     * <li>{@link #ACTION_DENIED} Action is not allowed on the object</li>
     * <li>{@link #ACTION_ALLOWED} Action is allowed on the object</li>
     * <li>{@link #ACTION_UNDEFINED} checkPrincipal was true but the user's
     * principal group has no specific security settings for the action and
     * object; the action was neither allowed nor denied.  In this case,
     * the security should be based on calling <code>checkObject</code> with
     * checkPrincipal set to <code>false</code></li>
     */
    private int checkObject(String objectID, int action, boolean checkPrincipal, boolean defaultToModuleSecurity) {
        int returnValue;
        boolean usePrincipal = false;
        int roleActions = 0;
        int principalActions = 0;
        int actionUnDefined = 0;
        String principalValue;

        GroupProvider groupProvider = new GroupProvider();

        ObjectPermission permission;
        List objectList = new ArrayList();

        // If this check is cached, return the result
        if ((returnValue = m_cache.checkObject(objectID, action, checkPrincipal)) >= 0) {
            return returnValue;
        }

        DBBean db = new DBBean();

        try {
            final List groups = m_cache.getSecurityGroups();

            if (!groups.isEmpty()) {
                // Load the permissions for the specified object ID defined for each
                // of the groups that the current user is in across ALL spaces
                final int groupCount = groups.size();

                // We iterate over sublists of groups since there is an inherent expression limit
                // with Oracle queries
                int nextStartIndex = 0;
                do {
                    // Last index for sublist is lesser of total size and max expressions
                    // Note that the index is _exclusive_ so it is valid to pass in the
                    // list size itself in order to get the last item
                    int endIndex = Math.min(groupCount, (MAX_GROUP_SUBLIST + nextStartIndex));
                    List partialGroups = groups.subList(nextStartIndex, endIndex);

                    StringBuffer query = new StringBuffer();

                    // 2003/06/02 - Tim
                    // This query is deliberately arranged to make the "IN" clause an
                    // inner query linked by an "exists" to ensure that the results are
                    // first filtered on PN_OBJECT_PERMISSION (which will have few rows
                    // for any object ID) before joining to the group IDs. This is
                    // proven to have a very low cost in the database (around 7) and it
                    // is a fixed cost; previous forms of this query had a very high
                    // cost which was proportional to the number of groups in the IN
                    // clause
                    query.append("select op.actions, op.group_id, g.group_type_id ")
                            .append("from pn_object_permission op, pn_group g ")
                            .append("where op.object_id = ? ")
                            .append("and g.group_id = op.group_id ")
                            .append("and exists (select 1 from pn_group g2 ")
                            .append("where g2.group_id = g.group_id ")
                            .append("and g2.group_id in (" + arrayListToString(partialGroups) + ")) ");

                    int index = 0;
                    db.prepareStatement(query.toString());
                    db.pstmt.setString(++index, objectID);
                    db.executePrepared();
                    
                    // If no rows returned - there are no permissions set for this Object
                    if (!(db.result.next()) ) {
                    	if(defaultToModuleSecurity)
                    		actionUnDefined = ACTION_UNDEFINED;
                    	
                    } else {

	                    do {
	                        try {
	                            Group group = groupProvider.newGroup(GroupTypeID.forID(db.result.getString("group_type_id")));
	                            group.setID(db.result.getString("group_id"));
	
	                            permission = new ObjectPermission();
	                            permission.setGroup(group);
	                            permission.setActionBits(Integer.parseInt(db.result.getString("actions")));
	
	                            objectList.add(permission);
	
	                        } catch (GroupException ge) {
	                            // No permission
	                        }
	                    } while (db.result.next());
                    }

                    // Next group to get is the one identified by the endIndex since the
                    // endIndex wasn't included in the sublist
                    nextStartIndex = endIndex;
                } while (nextStartIndex < groupCount);

            }


        } catch (SQLException sqle) {
        	Logger.getLogger(SecurityProvider.class).debug("SecurityProvider.checkObject(): " + sqle);

        } catch (PersistenceException pe) {
            // Problem getting security groups, already logged

        } finally {
            db.release();

        }

        // The principalValue is the principal groupID or "NO_ID"
        // if the user has no principal group in the current space
        principalValue = getPrincipalGroup();

        // Iterate over built list of permissions, building the actions
        // bitset from those permissions
        Iterator objectIt = objectList.iterator();
        while (objectIt.hasNext()) {
            permission = (ObjectPermission)objectIt.next();
            Group group = permission.getGroup();


            if (principalValue != null) {
                // Always true

                if (principalValue.equals(group.getID())) {
                    usePrincipal = true;
                    principalActions = permission.getActionsBits();

                } else {
                    roleActions |= permission.getActionsBits();
                }

            } else {
                // This can never happen.

            }
        }

        permission = new ObjectPermission();

        if (checkPrincipal) {
            // We are checking the principal group

            if (usePrincipal) {
                // User's principal group has specific permissions
                // Check to see if it allows or denies the action
                permission.setActionBits(principalActions);

                if (permission.actionAllowed(action)) {
                    returnValue = ACTION_ALLOWED;

                } else {
                    returnValue = ACTION_DENIED;

                }

            } else {
                // Principal group not involved;  neither denied nor allowed
                returnValue = ACTION_UNDEFINED;

            }

        } else {
            permission.setActionBits(roleActions);

            if (permission.actionAllowed(action)) {
                returnValue = ACTION_ALLOWED;

            } else  if (actionUnDefined == ACTION_UNDEFINED) {
            	returnValue = ACTION_UNDEFINED;
            	
            } else {
                returnValue = ACTION_DENIED;

            }

        }
        
        // Cache those checked permissions
        m_cache.addObjectCheck(objectID, action, checkPrincipal, returnValue);

        return returnValue;
    }

    private int checkObject(String objectID, int action, boolean checkPrincipal) {
    	return checkObject(objectID, action, checkPrincipal, true);
    }

    /**
     * Gets any group that is a Principal for current user within current
     * space context.  Note that this group NEVER returns null.
     * @return the id of the principal group for the current user in current
     * space context; or <code>NO_ID</code> if no principal group is found
     * in the
     */
    private String getPrincipalGroup() {
        Group group;
        String principalID = "NO_ID";

        try {
            GroupCollection groupList = m_cache.getSpaceGroupList();

            Iterator groupIt = groupList.iterator();
            while (groupIt.hasNext()) {
                group = (Group)groupIt.next();

                if (group.isPrincipal()) {
                    principalID = group.getID();
                    break;
                }

            }

        } catch (PersistenceException pe) {
            // Problem getting space group list
            // no principal id
        }

        return principalID;
    }


    /**
     * Converts the string value of the action to the int value
     * @param value the string representation of the action.  One of:
     * <li>view</li>
     * <li>modify</li>
     * <li>create</li>
     * <li>remove</li>
     * <li>list</li>
     * <li>properties</li>
     * <li>modify_permissions</li>
     * @return the int representation of the action
     * @see net.project.security.Action
     * @deprecated as of 7.6.2; Use {@link Action#actionStringToInt} instead.
     * Note that this method accepts the value <code>remove</code> but
     * <code>actionStringToInt</code> accepts the value <code>delete</code>.
     */
    public static int getActionMask(String value) {
        if (value != null && value.equals("remove")) {
            return Action.actionStringToInt(Action.DELETE_STRING);
        } else {
            return Action.actionStringToInt(value);
        }
    }


    /**
     * Indicates whether user is an application administrator.
     * Application Administrators are those users in the "Space Admin" group
     * of the Application Space.
     * <b>Note:</b> This causes current cached space information to be discarded
     * since a space-switch occurs.  As a result this value should be cached
     * itself and performed only when a user logs in.
     * @return true if the user is an application administrator; false otherwise
     */
    boolean isUserApplicationAdministrator() {
        Space currentSpace;
        Space newSpace;
        boolean isApplicationAdministrator;

        // Save current space and switch to application space
        currentSpace = getSpace();
        newSpace = net.project.admin.ApplicationSpace.DEFAULT_APPLICATION_SPACE;
        setSpace(newSpace);

        // Check if user is member of Space Administrator group in Application space
        isApplicationAdministrator = isUserSpaceAdministrator();

        // Revert to saved space
        setSpace(currentSpace);

        return isApplicationAdministrator;
    }

    /**
     * Indicates whether user is a space admin in the current space.
     * Note: If the user is an Application Administrator, this also returns true
     * This is necessary so that code checking for Space Administrator affords
     * App Administrator the same priveleges
     * @return true if the user is a space administrator or application administrator;
     * false otherwise
     */
    public boolean isUserSpaceAdministrator() {
        boolean isUserSpaceAdministrator;

        // User is space administrator if they are in the space administrator group
        isUserSpaceAdministrator = isUserInSpecialGroup(GroupTypeID.SPACE_ADMINISTRATOR);

        // If they are not a Space Administrator yet then if this is
        // not the application space, check to see if they are an Application Administrator
        // (If this is the application space then they would be App Admin if
        //  they are already a Space Admin)

        if (!isUserSpaceAdministrator) {

            if (!getSpace().getID().equals(net.project.admin.ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID)) {
                // The current space is not the application space
                // So check to see if they are application administrator
                isUserSpaceAdministrator |= getUser().isApplicationAdministrator();
            }

        }

        return isUserSpaceAdministrator;
    }

    /**
     * Indicates whether user is a Power User in the current space.
     * @return true if the user is a  Power User;
     * false otherwise
     */
    public boolean isUserPowerUser() {
        boolean isUserPowerUser;

        // User is in the Power User group
        isUserPowerUser = isUserInSpecialGroup(GroupTypeID.POWER_USER);
        return isUserPowerUser;
    }
    
    /**
     * Indicates whether user is a member of the specified group in the current space.
     * @param groupTypeID the group in which we are checking
     * @return true if the user is a specified group;
     * false otherwise
     */
    public boolean isUserGroupUser(GroupTypeID groupTypeID) {
        // User is in the specified User group
    	return isUserInSpecialGroup(groupTypeID);
    }

    /**
     * Indicates whether user is in a group in this space of the specified type. <br>
     * <b>Note:</b> It is important to restrict this to the current space; this
     * disallows an inherited special group (e.g. Space Admin) from being
     * treated as a special group in this space.  However, a user may still
     * be in a special group in this space (e.g. Space Admin) through their
     * membership of a group from another space that has been added to the
     * special group in this space.  This is because the groups for this space
     * are filtered out _after_ loading all groups for all spaces.<br>
     * For example:  In Space 1, user is a member of Group A.  In Space 2,
     * Space 1's Group A is a member of Space 2's Space Admin group.  The user
     * is still a Space Administrator of Space 2 through their membership of
     * Space 1's Group A.
     * @return true if the user is in a group of the specified type
     * in the current space
     */
    private boolean isUserInSpecialGroup(GroupTypeID groupTypeID) {
        boolean isFound = false;

        try {
            GroupCollection groupList = m_cache.getSpaceGroupList();

            // Iterate over all groups in space
            // If group matches specified type (and there should only be one
            // Space Administrator or Team Member Group in a space) then stop
            Iterator groupIt = groupList.iterator();
            while (groupIt.hasNext() && !isFound) {

                if (((Group)groupIt.next()).getGroupTypeID().equals(groupTypeID)) {
                    isFound = true;
                }
            }

        } catch (PersistenceException pe) {
            // Problem getting space group list, already logged
            // User is not found to be in group

        }

        return isFound;
    }

    /**
     * Checks to see if the Action is allowed, based on a Object ID and Action Name
     * @param objectID the object to check the action for
     * @param moduleID the module to check
     * @param actionID the action to check
     * @return boolean true if the action is allowed; false otherwise
     */
    public boolean isActionAllowed(String objectID, int moduleID, int actionID, boolean defaultToModuleSecurity) {
        return isActionAllowed(objectID, Integer.toString(moduleID), actionID, true);
    }

    public boolean isActionAllowed(String objectID, int moduleID, int actionID) {
        return isActionAllowed(objectID, moduleID, actionID, true);
    }
    
    /**
     * Checks to see if the Action is allowed, based on a Object ID and Action Name
     * @param objectID the object to check the action for
     * @param moduleID the module to check
     * @param actionID the action to check
     * @return boolean true if the action is allowed; false otherwise
     */
    public boolean isActionAllowed(String objectID, String moduleID, int actionID, boolean defaultToModuleSecurity) {
        boolean isActionAllowed;

        try {
            securityCheck(objectID, moduleID, actionID, defaultToModuleSecurity);
            isActionAllowed = true;

        } catch (AuthorizationFailedException ex) {
            isActionAllowed = false;

        }

        return isActionAllowed;
    }

    public boolean isActionAllowed(String objectID, String moduleID, int actionID) {
    	return isActionAllowed(objectID, moduleID, actionID, true);
    }   	
    	
    /**
     * Checks to see if the Action is allowed, based on a Object ID and Action
     * @param objectID the object to check the action for
     * @param moduleID the module to check
     * @param action the action to check
     * @return boolean true if the action is allowed; false otherwise
     */
    public boolean isActionAllowed(String objectID, String moduleID, Action action) {
        boolean isActionAllowed;

        try {
            securityCheck(objectID, moduleID, action.getBitMask());
            isActionAllowed = true;

        } catch (AuthorizationFailedException ex) {
            isActionAllowed = false;

        }

        return isActionAllowed;
    }

    /**
     * Check to see if the action is allowed for each object in the object id
     * array.
     *
     * @param objectIDs a <code>String</code> array containing zero or more
     * objects ID's.
     * @param moduleID a <code>String</code> containing the id of the module for
     * which we are checking access.
     * @param action a <code>int</code> containing the Action id for which we
     * are checking access.  These int's are defined in {@link net.project.security.Action}.
     * @return a <code>MultiAccessResults</code> object which indicates if the
     * action is allowed, and if not, which items failed.
     * @throws NullPointerException if the specified objectIDs is null
     * @throws IllegalArgumentException if the specified objectIDs array contains
     * no elements
     */
    public MultiAccessResults isMultiActionAllowed(String[] objectIDs, String moduleID, int action) {
        boolean isActionAllowed;
        List failedIDList = null;

        try {
            multiSecurityCheck(objectIDs, moduleID, action);
            isActionAllowed = true;

        } catch (AuthorizationFailedException ex) {
            isActionAllowed = false;
            failedIDList = ex.getFailedIDList();
        }

        return new MultiAccessResults(failedIDList, isActionAllowed);
    }

    public void refreshGroups(User user) {
        setUser(user);
        // set user will clear the cache if necessary
    }

    /**
     * Clears the current security cache.
     */
    public void clearCache() {
        m_cache.clear();
    }

    /**
     * Sets the Action that is used in the security Checks
     * @param value
     */
    public void setCheckedActionID(int value) {
        m_actionID = value;
    }

    /**
     * Sets the Module that is used in the security Checks
     * @param value
     */
    public void setCheckedModuleID(int value) {
        m_moduleID = value;
    }

    /**
     * Sets the Obejct that is used in the security Checks
     * @param value
     */
    public void setCheckedObjectID(String value) {
        m_objectID = value;
    }

    /**
     * Gets the Action that is used in the security Checks
     * @return
     */
    public int getCheckedActionID() {
        return m_actionID;
    }

    /**
     * Gets the Module that is used in the security Checks
     * @return
     */
    public int getCheckedModuleID() {
        return m_moduleID;
    }

    /**
     * Gets the Object that is used in the security Checks
     * @return
     */
    public String getCheckedObjectID() {
        return m_objectID;
    }

    /**
     * Method that does the actually security check for the application.
     * The behavior of this method is as follows:<br>
     * <li>If no objectID is specified then security is checked for the specified
     * action against the module, for the user's group membership and their
     * principal group
     * <li>If an objectID is specified, then in addition to the previous check,
     * security is checked against the object with that id
     * @param objectID the object for which to check security (optional)
     * @param module the module for which to check security; this should be
     * @param if object security is undefined then default to module security (default true)
     * the module number as a string
     * @param actionValue the action for which to check security
     * @throws net.project.security.AuthorizationFailedException if the user has no security
     * @return a <code>boolean</code> value indicating that the user is always
     * going to be allowed either because they are the application administrator
     * or because they are the space administrator for the current space.
     * @see net.project.base.Module the Module constants
     * @see net.project.security.Action the Action constants
     */
    public SecurityAccess securityCheck(String objectID, String module, int actionValue, boolean defaultToModuleSecurity) throws net.project.security.AuthorizationFailedException {

        SecurityAccess access = securityCheckNoException(objectID, module, actionValue, defaultToModuleSecurity);

        // If access is not granted, throw a security exception
        if (!access.isGranted()) {
            throw new net.project.security.AuthorizationFailedException(access.getMessage());
        }

        return access;
    }

    public SecurityAccess securityCheck(String objectID, String module, int actionValue) throws net.project.security.AuthorizationFailedException {
        return securityCheck(objectID, module, actionValue, true);
    }
    
    /**
     * @param objectID
     * @param module
     * @param actionValue
     * @param defaultToModuleSecurity
     * @return
     * @deprecated As of 7.6.2; Use {@link #securityCheck} instead.  This method
     * will become private
     */
    public SecurityAccess securityCheckNoException(String objectID, String module, int actionValue, boolean defaultToModuleSecurity) {
        // The access given
        SecurityAccess access;

        if (this.user.isApplicationAdministrator()) {
            // Application administrators pass all security checks
            access = SecurityAccess.GRANTED;
        } else {
            // Not an Application Administrator
            // We must check the security

            // Initiate check; this might cause the cache to timeout for
            // subsequent reloading
            m_cache.initiateCheck();

            // Check to see if the current space is active; access is denied
            // to spaces that are not active
            if (!isSpaceAccessAllowed()) {
                access = SecurityAccess.INACTIVE_SPACE;
            } else {
                // Space is active, continue to check security
                if (module == null) {
                    // Module is required
                    access = SecurityAccess.NO_MODULE_SPECIFIED;
                } else {
                    // If user is a Space Administrator, then we just ensure that
                    // the specified action is defined for the object in the module.
                    // Otherwise, we check that the user's principle group has permission
                    // to perform specified action on object in module.
                    if (isUserSpaceAdministrator()) {
                        access = checkAccess(module, actionValue, objectID, defaultToModuleSecurity);
                    } else {
                        access = checkPrincipleAccess(module, actionValue, objectID, defaultToModuleSecurity);
                    }
                }
            }
        }

        UserActivity.log(user);

        return access;
    }

    public SecurityAccess securityCheckNoException(String objectID, String module, int actionValue) {
    	return securityCheckNoException(objectID, module, actionValue, true);
    }
    
    /**
     * This method runs a security check for multiple object ids.  This is
     * necessary for multi-item selects being implemented in the application.
     *
     * @param objectIDs a <code>String</code> array containing one or more
     * object ids.
     * @param module a <code>String</code> containing the module id that we are
     * checking access to.
     * @param actionValue a <code>int</code> indicating which type of access we
     * are checking.  These ints are defined in the
     * {@link net.project.security.Action} object.
     * @throws net.project.security.AuthorizationFailedException if one or more
     * objects are denied access.
     * @throws NullPointerException if the specified objectIDs is null
     * @throws IllegalArgumentException if the specified objectIDs array contains
     * no elements
     */
    public void multiSecurityCheck(String[] objectIDs, String module, int actionValue) throws net.project.security.AuthorizationFailedException {

        if (objectIDs == null) {
            throw new NullPointerException("Object IDs are required");
        }
        if (objectIDs.length == 0) {
            throw new IllegalArgumentException("One or more object IDs are required");
        }

        // The access given
        List failedList = new ArrayList();
        SecurityAccess access;

        if (this.user.isApplicationAdministrator()) {
            // Application administrators pass all security checks
            access = SecurityAccess.GRANTED;

        } else {
            // Not an Application Administrator
            // We must check the security

            // Initiate check; this might cause the cache to timeout for
            // subsequent reloading

            m_cache.initiateCheck();

            // Check to see if the current space is active; access is denied
            // to spaces that are not active
            if (!isSpaceAccessAllowed()) {
                access = SecurityAccess.INACTIVE_SPACE;

            } else {
                // Space is active, continue to check security

                if (module == null) {
                    // Module is required
                    access = SecurityAccess.NO_MODULE_SPECIFIED;

                } else {

                    // If user is a Space Administrator, then we just ensure that
                    // the specified action is defined for the object in the module.
                    // Otherwise, we check that the user's principle group has permission
                    // to perform specified action on object in module.
                    if (isUserSpaceAdministrator()) {
                        // Start with successful security access
                        access = SecurityAccess.GRANTED;

                        for (int i = 0; i < objectIDs.length; i++) {
                            SecurityAccess testAccess = checkAccess(module, actionValue, objectIDs[i]);

                            if (!testAccess.isGranted()) {
                                access = testAccess;
                            }
                        }

                    } else {
                        // Check principle group has permission

                        // Start with successful security access
                        access = SecurityAccess.GRANTED;

                        for (int i = 0; i < objectIDs.length; i++) {
                            SecurityAccess testAccess = checkPrincipleAccess(module, actionValue, objectIDs[i]);

                            if (!testAccess.isGranted()) {
                                failedList.add(objectIDs[i]);
                                access = SecurityAccess.NO_ACCESS_TO_ONE_OR_MORE;
                            }


                        }
                    }

                }
            }

        }

        // If access is not granted, throw a security exception
        if (!access.isGranted()) {
            throw new AuthorizationFailedException(access.getMessage(), failedList);
        }


        UserActivity.log(user);
    }

    /**
     * Checks that the current space allows access.
     * A space may not allow access if it is currently deleted.
     * @return true if the current space allows access; false otherwise
     */
    private boolean isSpaceAccessAllowed() {
        boolean isAccessAllowed = false;

        try {
            // Load the space if necessary
            if (!getSpace().isLoaded()) {
                getSpace().load();
            }

            // Access allowed only if record status is Active, if RecordStatus is null, assume that the
            // space is not implementing record status correctly or is only selecting active records.
            if ((getSpace().getRecordStatus() == null) || (getSpace().getRecordStatus().equals("A"))) {
                isAccessAllowed = true;
            }

        } catch (PersistenceException pe) {
            // Error loading space, access is not allowed

        }

        return isAccessAllowed;
    }

    /**
     * Checks that the user has access to perform the action on specified
     * object in module.  The user has access if they are the member of
     * a group that has access and their principle group does not deny access.
     * @param module the module
     * @param actionValue the action being performed
     * @param objectID the object on which action is being performed; or null
     * if action being performed at module level.
     * @return the access; either Denied or Granted.
     */
    private SecurityAccess checkPrincipleAccess(String module, int actionValue, String objectID, boolean defaultToModuleSecurity) {
        SecurityAccess access;
        int result;

        // Check action against module, checking principle group
        result = checkModule(module, actionValue, true);
        if (result == ACTION_DENIED) {
            // Action is specifically denied to module
            // Old message: Access denied to module for principal
            access = new SecurityAccess.ACCESS_DENIED_TO_MODULE_FOR_PRINCIPAL(
                new Object[] {
                    getActionDisplayName(new Integer(actionValue).toString()),
                    getModuleDisplayName(module)
                }
            );
        } else {
            //Action is either ACTION_ALLOWED or ACTION_UNSPECIFIED.  If action isn't
            //specifically denied to the module, we can check to see if we have permission
            //to the object.
			 if (objectID != null) {
			 result = checkObject(objectID, actionValue, true, defaultToModuleSecurity);
			 }

            //Now we have performed all available security checks that we could possibly
            //need to check for a principle user.  If the results are defined, set the
            //appropriate response.  If the result are undefined, it means that the
            //user probably isn't a principal user.
            if (result == ACTION_DENIED) {
                // Old message: Access denied to object for principal
                access = new SecurityAccess.ACCESS_DENIED_TO_OBJECT_FOR_PRINCIPAL(
                    new Object[]{getActionDisplayName(new Integer(actionValue).toString())}
                );
            } else if (result == ACTION_ALLOWED) {
                // User may perform action on module or object
                access = SecurityAccess.GRANTED;

            } else if (result == ACTION_UNDEFINED) {
                // Check non-principal access to module or object
                access = checkAccess(module, actionValue, objectID);
            } else {
                //Getting to this else implies that none of the ints
                throw new UnexpectedStaticVariableException("Unexpected value of return value from SecurityProvider.checkModule() encountered.");
            }
        }


        return access;
    }

    private SecurityAccess checkPrincipleAccess(String module, int actionValue, String objectID) {
    	return checkPrincipleAccess(module, actionValue, objectID, true);
    }
    
    /**
     * Checks access to perform action on object in module without considering
     * user's group membership.
     * @param module the module
     * @param actionValue the action being performed
     * @param objectID the object on which action is being performed; or null
     * if action being performed at module level.
     * @return the access; either Denied or Granted.
     */
    private SecurityAccess checkAccess(String module, int actionValue, String objectID, boolean defaultToModuleSecurity) {
        SecurityAccess access;
        int result;

        // First check the module/action permission
        result = checkModule(module, actionValue, false);
        if (result == ACTION_DENIED) {
            // Access specifically denied to module
            // Old message: Access denied to module
            access = new SecurityAccess.ACCESS_DENIED_TO_MODULE(
                new Object[]{
                    getActionDisplayName(new Integer(actionValue).toString()),
                    getModuleDisplayName(module)
                }
            );
        } else {
            // Access is granted to module
            // Now check for object permission

            if (objectID == null) {
                // No objectID to check, so they're clear
                access = SecurityAccess.GRANTED;

            } else {
                // Check the object
                result = checkObject(objectID, actionValue, false, defaultToModuleSecurity);
                if (result == ACTION_DENIED) {
                    // Denied specifically to object, so denied overall
                    // Old message: Action denied to object
                    access = new SecurityAccess.ACCESS_DENIED_TO_OBJECT(
                        new Object[]{getActionDisplayName(new Integer(actionValue).toString())}
                    );
                } else {
                    // Granted to module, granted to object = granted overall
                   access = SecurityAccess.GRANTED;
                }
            }
        }

        return access;
    }

    private SecurityAccess checkAccess(String module, int actionValue, String objectID) {
    	return checkAccess(module, actionValue, objectID, true);
    }
    /**
     * Checks that user is a space administrator (or application administrator),
     * throws exception if not, simply returns if is.
     * @param deniedMessage the message to be used if authorization is denied.
     * This message is bundled into the AuthorizationFailedException.  If the
     * message is null, a default message will be used.
     * @throws AuthorizationFailedException if the user is not a space
     * administrator in the current space.
     */
    public void securityCheckSpaceAdministrator(String deniedMessage) throws AuthorizationFailedException {

        if (deniedMessage == null) {
            deniedMessage = PropertyProvider.get("prm.security.securityprovider.accessdenied.space.message");
        }

        if (!isUserSpaceAdministrator() && !getUser().isApplicationAdministrator()) {
            throw new AuthorizationFailedException(deniedMessage);
        }

    }

    /**
     * Gets the display name of an action.
     * @param actionValue the value of the action for which to get the display name;
     * this is its bit mask
     * @return the display name or <code>&lt;Unknown&gt;</code> if there is no action for
     * that value
     */
    private String getActionDisplayName(String actionValue) {
        return getActionDisplayName(actionValue, "<Unknown>");
    }

    /**
     * Gets the display name of an action.
     * @param actionValue the value of the action for which to get the display name;
     * this is its bit mask
     * @param unknownActionName the display name to return if there is no action
     * for that value
     * @return the display name
     */
    private String getActionDisplayName(String actionValue, String unknownActionName) {
        Action action;
        String displayName;

        // Load the actions if necessary
        loadActions();

        // Find the action by its ID
        action = this.actions.getActionForBitmask(actionValue);

        if (action != null) {
            displayName = action.getDescription();
        } else {
            displayName = unknownActionName;
        }

        return displayName;
    }

    /**
     * Gets the display name of a Module.
     * @param moduleID the id of the module for which to get the display name
     * @return the display name or <code>&lt;Unknown&gt;</code> if there is no module for that
     * id
     */
    private String getModuleDisplayName(String moduleID) {
        return getModuleDisplayName(moduleID, "<Unknown>");
    }

    /**
     * Gets the display name of a Module.
     * @param moduleID the id of the module for which to get the display name
     * @param unknownModuleName the display name to return if there is no module
     * for that id
     * @return the display name
     */
    private String getModuleDisplayName(String moduleID, String unknownModuleName) {
        Module module;
        String displayName;

        // Load the modules if necessary
        loadModules();

        // Find the module by its id
        module = this.modules.getModule(moduleID);

        if (module != null) {
            displayName = module.getDescription();
        } else {
            displayName = unknownModuleName;
        }

        return displayName;
    }

    /**
     * Loads the list of actions if necessary.  The load is only performed
     * once.
     */
    private void loadActions() {
        if (this.actions == null) {
            this.actions = new ActionList();
            this.actions.loadAllActions();
        }
    }

    /**
     * Loads the list of modules if necessary.  The load is only performed
     * once.
     */
    private void loadModules() {
        if (this.modules == null) {
            this.modules = new ModuleCollection();

            try {
                this.modules.load();
            } catch (PersistenceException pe) {
                // Empty module list
            }

        }
    }

    /**
     * Copies all security settings from one space to another.
     * @param fromSpaceID the id of the space to copy from
     * @param toSpaceID the id of the space to copy to
     * @throws PnetException if there is a problem copying
     */
    public void copyAllSettings(String fromSpaceID, String toSpaceID) throws PnetException {
        // First copy the roles
        copyRoles(fromSpaceID, toSpaceID);

        // 09/10/2001 - Tim - These were commented out
        // Uncommented since they are now needed by both create template and
        // applymethodology
        copyModulePermissions(fromSpaceID, toSpaceID);
        copyDefaultPermissions(fromSpaceID, toSpaceID);
    }

    /**
     * Copies all roles from one space to another.
     * Uses a stored procedure; currently only copies User-defined, non-inherited
     * groups from the from space.
     * @param fromSpaceID the id of the space to copy from
     * @param toSpaceID the id of the space to copy to
     * @throws PnetException if there is a problem copying
     */
    private void copyRoles(String fromSpaceID, String toSpaceID) throws PnetException {
        int errorCode = 0;

        DBBean db = new DBBean();
        try {
            db.prepareCall("begin SECURITY.COPY_GROUPS (?,?,?,?); end;");

            db.cstmt.setString(1, fromSpaceID);
            db.cstmt.setString(2, toSpaceID);
            db.cstmt.setString(3, SessionManager.getUser().getID());
            db.cstmt.registerOutParameter(4, java.sql.Types.INTEGER);

            db.executeCallable();
            errorCode = db.cstmt.getInt(4);
        } catch (SQLException sqle) {
        	Logger.getLogger(SecurityProvider.class).debug("SecurityProvider.copyGroups():  unable to execute stored procedure: " + sqle);
            throw new PersistenceException("SecurityProvider.copyGroups operation failed! ", sqle);
        } finally {
            db.release();
        }

        DBExceptionFactory.getException("SecurityProvider.copyGroups()", errorCode);

    }


    /**
     * Copies all module permissions from one space to another.
     * Assumes roles have already been copied.
     * @param fromSpaceID the id of the space to copy from
     * @param toSpaceID the id of the space to copy to
     * @throws PnetException if there is a problem copying
     */
    private void copyModulePermissions(String fromSpaceID, String toSpaceID) throws PnetException {
        int errorCode = 0;

        DBBean db = new DBBean();
        try {
            db.prepareCall("begin SECURITY.COPY_MODULE_PERMISSIONS (?,?,?,?); end;");

            db.cstmt.setString(1, fromSpaceID);
            db.cstmt.setString(2, toSpaceID);
            db.cstmt.setString(3, SessionManager.getUser().getID());
            db.cstmt.registerOutParameter(4, java.sql.Types.INTEGER);

            db.executeCallable();
            errorCode = db.cstmt.getInt(4);
        } catch (SQLException sqle) {
        	Logger.getLogger(SecurityProvider.class).debug("SecurityProvider.copyModulePermissions():  unable to execute stored procedure: " + sqle);
            throw new PersistenceException("SecurityProvider.copyModulePermissions operation failed! ", sqle);
        } finally {
            db.release();
        }

        DBExceptionFactory.getException("SecurityProvider.copyModulePermissions()", errorCode);
    }


    /**
     * Copies all default permissions from one space to another.
     * Assumes roles have already been copied.
     * @param fromSpaceID the id of the space to copy from
     * @param toSpaceID the id of the space to copy to
     * @throws PnetException if there is a problem copying
     */
    private void copyDefaultPermissions(String fromSpaceID, String toSpaceID) throws PnetException {
        int errorCode = 0;

        DBBean db = new DBBean();
        try {
            db.prepareCall("begin SECURITY.COPY_DEFAULT_PERMISSIONS (?,?,?,?); end;");

            db.cstmt.setString(1, fromSpaceID);
            db.cstmt.setString(2, toSpaceID);
            db.cstmt.setString(3, SessionManager.getUser().getID());
            db.cstmt.registerOutParameter(4, java.sql.Types.INTEGER);

            db.executeCallable();
            errorCode = db.cstmt.getInt(4);
        } catch (SQLException sqle) {
        	Logger.getLogger(SecurityProvider.class).debug("SecurityProvider.copyDefaultPermissions():  unable to execute stored procedure: " + sqle);
            throw new PersistenceException("SecurityProvider.copyDefaultPermissions operation failed! ", sqle);
        } finally {
            db.release();
        }

        DBExceptionFactory.getException("SecurityProvider.copyDefaultPermissions()", errorCode);
    }

    /**
     * Get a string which explains what was received and what was expected on
     * the last security verification attempt.  This is especially useful for
     * logging statements, which would otherwise have to reconstruct this
     * themselves.
     *
     * @return a <code>String</code> with a string explaining what was received.
     */
    public String getExplanationString() {
        return "Security Provider received \n" +
            "  module: " + getCheckedModuleID() + "\n" +
            "  action: " + getCheckedActionID() + "\n" +
            "  id: " + getCheckedActionID();
    }
}
