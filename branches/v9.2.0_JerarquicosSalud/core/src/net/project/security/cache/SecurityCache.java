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

 /*----------------------------------------------------------------------+
|                                                                       
|     $RCSfile$
|    $Revision: 18995 $
|        $Date: 2009-03-05 08:36:26 -0200 (jue, 05 mar 2009) $
|      $Author: avinash $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.security.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.project.base.Module;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.ModulePermission;
import net.project.security.SecurityProvider;
import net.project.security.User;
import net.project.security.group.Group;
import net.project.security.group.GroupCollection;
import net.project.security.group.GroupException;
import net.project.security.group.GroupProvider;
import net.project.security.group.GroupTypeID;
import net.project.space.Space;
import net.project.util.StringUtils;

import org.apache.log4j.Logger;


/**
 * Provides caching of security settings.
 * The methods {@link #initiateCheck} must <b>always</b> be called prior to
 * accessing any data from this cache.
 * This provides a hook for the cache to timeout and reload.
 */
public class SecurityCache implements java.io.Serializable {

    /** Default timeout in milliseconds, currently <code>120000</code> (2 minutes). */
    private static long DEFAULT_TIMEOUT = 120000;

    /** Date on which SecurityCache last initialized. */
    private Date initializeDate = null;

    /** Period after which cache should be re-loaded. */
    private long timeout = DEFAULT_TIMEOUT;

    /** Collection of all groups that user is a member of. */
    private GroupCollection allGroups = null;

    /** Collection of groups that current user is a meber of for current space only. */
    private GroupCollection  spaceGroups = null;
    
    /** Collection of module permissions for each group that the user is a
     * member of in the current space. */
    private List modulePermissions = null;
    
    private HashMap checkedModules = new HashMap();
    private HashMap checkedObjects = new HashMap();

    private Space currentSpace = null;
    private User currentUser = null;
    private DBBean db = new DBBean();
    private boolean isForcedTimeOut = false;


    public SecurityCache() {
        initialize();
    }

    /**
     * Sets the current space context.
     * @param space the current space
     */
    public void setSpace(Space space) {
        this.currentSpace = space;
    }


    /**
     * Returns the current space context.
     * @return the current space
     */
    private Space getSpace() {
        return this.currentSpace;
    }


    /**
     * Sets the current user context.
     * @param user the current user
     */
    public void setUser(User user) {
        this.currentUser = user;
    }


    /**
     * Returns the current user context.
     * @return the current user context
     */
    private User getUser() {
        return this.currentUser;
    }


    /**
     * Initializes the cache.
     * Clears out cache and resets initialization date.
     */
    private void initialize() {
        clear();
        setInitializeDate(new Date());
    }
    
    
    /**
     * Sets the initialize date.  This is used when calculating timeouts.
     * @param date the initialization date
     */
    private void setInitializeDate(Date date) {
        this.initializeDate = date;
    }


    /**
     * Returns the initialization date.
     * @return the initialization date
     */
    private Date getInitializeDate() {
        return this.initializeDate;
    }

    
    /**
     * Sets the cache timeout period.  The cache will be re-initialized after
     * this period (and will therefore only be re-loaded on a subsequent cache
     * access).
     * @param period the timeout period in milliseconds.
     * @see #getTimeout
     */
    public void setTimeout(long period) {
        this.timeout = period;
    }

    
    /**
     * Indicates the timeout period.
     * @return the timeout period in milliseconds
     * @see #setTimeout
     */
    public long getTimeout() {
        return this.timeout;
    }


    /**
     * Gets ready for checking the cache.  This may cause the cache to be
     * cleared if it has timed out. This must be called prior to any cache access.
     */
    public void initiateCheck() {
        if (isTimedOut()) {
            initialize();        
        }
    }


    /**
     * Indicates whether the time since last initialize is greater than the
     * timeout period.
     * @return true if the cache has timed out; false otherwise
     */
    private boolean isTimedOut() {
        boolean isTimedOut = false;
           
        if(this.isForcedTimeOut)
            return true;

        Date currentDate = new Date();
        if ((currentDate.getTime() - getInitializeDate().getTime()) > getTimeout()) {
            isTimedOut = true;
        }

        return isTimedOut;
    }

    /**
     * Forces the time out to occur on the Security Cache.
     */
    public void forceTimeOut() {
        this.isForcedTimeOut = true;
    }

    /**
     * Returns all the groups that the current user is a member of across
     * all spaces. Cached after first load.
     * @return the collection where each element is a <code>{@link net.project.security.group.Group}</code>
     * @throws PersistenceException if there is a problem loading the groups
     */
    public synchronized GroupCollection getSecurityGroups() throws PersistenceException {
        if (this.allGroups == null) {
            this.allGroups = querySecurityGroups();
        }
        return this.allGroups;
    }


    /**
     * Returns the ModulePermissions for all modules in the current space
     * for groups that the current user is a member of.  Cached after first load.
     * @return the list where each element is a <code>{@link net.project.security.ModulePermission}</code>
     * @throws PersistenceException if there is a problem loading the modules
     */
    public synchronized List getModules() throws PersistenceException {
        if (this.modulePermissions == null) {
            this.modulePermissions = queryModules();
        }
        return this.modulePermissions;
    }


    /**
     * Returns all the groups that the current user is a member of for the
     * current space only. Cached after first load.
     * @return the collection where each element is a <code>{@link net.project.security.group.Group}</code>
     * @throws PersistenceException if there is a problem loading the groups
     */
    public synchronized GroupCollection getSpaceGroupList() throws PersistenceException {
        Group group;

        if (this.spaceGroups == null) {
            this.spaceGroups = new GroupCollection();
            
            Iterator it = getSecurityGroups().iterator();
            while (it.hasNext()) {
                group = (Group) it.next();
                
                // Add group if its space id matches current space id
                if (StringUtils.isNotEmpty(getSpace().getID()) && getSpace().getID().equals(group.getSpaceID())) {
                    this.spaceGroups.add(group);
                }

            }
        }
        
        return this.spaceGroups;
    }


    /**
     * Caches the security check value for the specified parameters.
     * @param module the module checked
     * @param action the action performed
     * @param checkPrincipal true if the permission was checked for the principal group
     * @param permissionValue the actual permission value
     */
    public void addModuleCheck(String module, int action, boolean checkPrincipal, int permissionValue) {
        this.checkedModules.put(getCacheID(module, action, checkPrincipal), new Integer(permissionValue));
    }


    /**
     * Caches the security check value for the specified parameters.
     * @param objectId the id of the object checked
     * @param action the action performed
     * @param checkPrincipal true if the permission was checked for the principal group
     * @param permissionValue the actual permission value
     */
    public void addObjectCheck(String objectId, int action, boolean checkPrincipal, int permissionValue) {
        this.checkedObjects.put(getCacheID(objectId, action, checkPrincipal), new Integer(permissionValue));
    }


    /**
     * Checks the permission on the specified module for the specified action
     * from the cache.
     * @param module the module to check
     * @param action the action performed
     * @param checkPrincipal true if the permission is to be checked for the principal
     * group
     * @return the permission value or -1 if that permission is not in the
     * cache.
     */
    public int checkModule(String module, int action, boolean checkPrincipal) {
        Integer val = (Integer) this.checkedModules.get(getCacheID(module, action, checkPrincipal));
        
        if (val != null) {
            return val.intValue();
        
        } else {
            return -1;
        }
    
    }

    
    /**
     * Checks the permission on the specified object for the specified action
     * from the cache.
     * @param objectId the id of the object to check
     * @param action the action peformed
     * @param checkPrincipal true if the permission is to be checked for the principal
     * group
     * @return the permission value or -1 if that permission is not in the
     * cache.
     */
    public int checkObject(String objectId, int action, boolean checkPrincipal) {
        Integer val = (Integer) this.checkedObjects.get(getCacheID(objectId, action, checkPrincipal));
        
        if (val != null) {
            return val.intValue();
        
        } else {
            return -1;
        
        }
    }


    /**
     * Contructs a cache ID.
     * @param checkedItem the object id or module that was checked
     * @param action the action performed
     * @param checkPrincipal true if thepermission was checked for the principal group
     * @return the cache id constructed from specified values
     */
    private String getCacheID(String checkedItem, int action, boolean checkPrincipal) {
        return checkedItem + "_" + action + "_" + checkPrincipal;
    }


    /**
     * Clears all security settings.
     */
    public synchronized void clear() {
        this.allGroups = null;
        this.spaceGroups = null;
        this.modulePermissions = null;
        this.checkedModules = new HashMap();
        this.checkedObjects = new HashMap();
    }


    /**
     * Gets the collection of groups that the current user is a member of across
     * all spaces.
     * @return all groups the user is a member of within all spaces.
     * @throws PersistenceException if there is a problem loading
     */
    private GroupCollection querySecurityGroups() throws PersistenceException {
        GroupCollection securityGroups = new GroupCollection();

        try {
            securityGroups.loadAllSpacesAllGroups(getUser().getID());

        } catch (PersistenceException pe) {
        	Logger.getLogger(SecurityCache.class).error("SecurityCache.querySecurityGroups threw an Persistence exception: " + pe);
            throw new PersistenceException("Security load groups operation failed", pe);
        
        }

        return securityGroups;
    }


    /**
     * Gets the collection of ModulePermissions for the current space and
     * the groups that the user is a member of.
     * @return the collection where each element is a <code>{@link net.project.security.ModulePermission}</code>
     * @throws PersistenceException if there is a problem loading
     */
    private List queryModules() throws PersistenceException {
        ArrayList moduleList = new ArrayList();
        StringBuffer query = new StringBuffer();
        GroupProvider groupProvider = new GroupProvider();

        // 10/08/2003 - Tim
        // Eliminated "pn_group_view" in favor of "pn_group" since the view
        // contains additional irrelevant joins
        // However, having done so, the query becomes very costly due to Oracle's mechanism
        // of performing the IN clause.  (This was predicted but only seen once the cost
        // of selecting "pn_group_view" was eliminated).
        // Query deliberately re-written with "exists" clause to force filtering based on
        // space ID before limiting against the values of the IN clause
        // Similar technique to SecurityProvider.checkObject
        query.append("select mp.module_id, mp.group_id, mp.actions, g.group_type_id ");
        query.append("from pn_module_permission mp, pn_group_view g ");
        query.append("where mp.space_id = ? ");
        query.append("and g.group_id = mp.group_id ");
        query.append("and exists ( select 1 from pn_group g2 ");
        query.append("where g2.group_id = g.group_id ");
        query.append("and g2.group_id in (" + SecurityProvider.arrayListToString(getSpaceGroupList()) + ")) ");

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, getSpace().getID());
            db.executePrepared();

            while (db.result.next()) {
                Group group;
                try {
                    group = groupProvider.newGroup(GroupTypeID.forID(db.result.getString("group_type_id")));
                    group.setID(db.result.getString("group_id"));

                    ModulePermission modulePermission = new ModulePermission();
                    Module module = new Module();

                    module.setId(db.result.getString("module_id"));
                    modulePermission.setActionBits(Integer.parseInt(db.result.getString("actions")));
                    modulePermission.setModule(module);
                    modulePermission.setSpace(getSpace());
                    modulePermission.setGroup(group);

                    moduleList.add(modulePermission);

                } catch (GroupException ge) {
                    throw new PersistenceException("Module load operation failed", ge);

                }
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(SecurityCache.class).error("SecurityCache.querySecurityGroups threw an SQL exception: " + sqle);
            throw new PersistenceException("Module load operation failed", sqle);

        } finally {
            db.release();

        }

        return moduleList;
    }

}
