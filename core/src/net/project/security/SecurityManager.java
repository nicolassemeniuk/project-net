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
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.security;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.PnObject;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.group.Group;
import net.project.security.group.GroupCollection;
import net.project.security.group.GroupException;
import net.project.security.group.GroupProvider;
import net.project.security.group.GroupTypeID;
import net.project.space.Space;
import net.project.xml.XMLFormatter;


/**
 * Manages security settings for a Space.  This includes capturing and storing
 * permission settings for Module Permissions, New Object Permissions and
 * individual Object permissions.
 *
 * @author Michael Ariston
 * @since Aardvark
 */
public class SecurityManager implements Serializable, IXMLPersistence {
    public static final String PERSON = "person";
    public static final String GROUP = "group";

    public static final String OBJECT = "object";
    public static final String MODULE = "module";
    public static final String PAGE = "page";
    public static final String DEFAULT_OBJECT = "default_object";

    /** The space context */
    private Space space = null;
    private User user = null;
    /** the pnobject context */
    private PnObject pnObject = null;
    /** the Groups List that are valid for the current Space and User context */
    private GroupCollection groups = null;
    /** the security that is being manipulated...Page, Module, or Object */
    private String securityType = null;

    private ArrayList permissionList = null;
    private ActionList supportedActionsList = null;
    private ArrayList moduleBySpace = null;
    private ArrayList defaultObjectBySpace = null;
    private DisplayGroupList groupList = null;
    private DisplayGroupList personList = null;
    private DisplayObjectPermission displayObjectPersmission = null;
    private DisplayModulePermission displayModulePersmission = null;
    private DisplayDefaultObjectPermission displayDefaultObjectPersmission = null;
    private String actions;
    private XMLFormatter formatter = new XMLFormatter();

    private String selectedID = null;
    private String previousID = null;

    /**
     * Constructor
     */
    public SecurityManager() {
        formatter = new XMLFormatter();
    }


    /**
     * Set the Space context for the SecurityManager.
     */
    public void setSpace(Space space) {
        this.space = space;

        // new context; we need to clear the old m_groups and m_roles lists.
        if (groups != null) {
            groups.clear();
        }
    }

    /**
     * Set the Space context for the SecurityManager.
     */
    public void setUser(User user) {
        this.user = user;

        // new context; we need to clear the old m_groups and m_roles lists.
        if (groups != null) {
            groups.clear();
        }
    }


    /**
     * Set the groups that this SecurityManager will manage.
     *
     * @param list
     */
    public void setGroups(GroupCollection list) {
        groups = list;
    }


    /**
     * Set the Space context for the SecurityManager.
     */
    public void setPnObject(PnObject pnobject) {
        pnObject = pnobject;
    }


    /**
     * Get the Principal GroupCollection for the current Space and User
     * context.
     */
    public DisplayGroupList getPrincipalGroups() {
        return personList;
    }

    /**
     * Get the Non-principal groups for the current Space and User context.
     */
    public DisplayGroupList getNonPrincipalGroups() {
        return groupList;
    }

    /**
     * Set the GroupCollection for the current Space and User context.
     */
    public void setGroupList(GroupCollection list) {
        groups = list;
    }

    /**
     * Resets the ObjectPermission List
     */
    public void clearObjectPermission() {
        permissionList = null;
    }

    /**
     * Resets the Module Permission List
     */
    public void clearModulePermission() {
        permissionList = null;
        selectedID = null;
    }

    /**
     * Resets the Default Permission List
     */
    public void clearDefaultPermission() {
        permissionList = null;
        selectedID = null;
    }

    /**
     * Gets the Seleceted ID for the Module group permissions
     */
    public String getSelectedID() {
        return selectedID;
    }

    /**
     * Sets the Seleceted ID for the Module group permissions
     */
    public void setSelectedID(String value) {
        selectedID = value;
    }

    /**
     * Sets the Previous ID for the Module group permissions
     */
    public void setPreviousID(String value) {
        previousID = value;
    }

    /**
     * Sets the GroupCollection to the proper values based on the Is_Principal
     * value.
     */
    public void setArrayListForGroupPeople() {
        DisplayGroup group = null;
        GroupCollection allGroups = new GroupCollection();
        groupList = new DisplayGroupList();
        personList = new DisplayGroupList();

        if (space == null) {
            throw new NullPointerException("m_space is null.  The Space context must be set before calling load()");
        }

        try {
            // Load all groups for space
            allGroups.setSpace(space);
            allGroups.loadAll();

            // Iterate over all groups, ignoring Space Administrators
            // Adding Principal groups to m_PersonList and others to m_GroupList
            Iterator it = allGroups.iterator();
            while (it.hasNext()) {
                group = new DisplayGroup((Group) it.next());
                group.setDisplay(true);

                if (!group.getGroupTypeID().equals(GroupTypeID.SPACE_ADMINISTRATOR)) {

                    if (group.getGroupTypeID().equals(GroupTypeID.PRINCIPAL)) {
                        personList.add(group);

                    } else {
                        groupList.add(group);

                    }

                }
            }

        } catch (PersistenceException pe) {
            // No groups

        }

    }


    /**
     * Gets all visible groups in the specified space. That is, all groups for
     * which security may be customized. This includes groups that are not owned
     * by this space.  It excludes Space Administrator type groups.
     *
     * @return the groups
     * @throws PersistenceException if there is a problem loading the groups
     */
    public GroupCollection getSecurityGroups(Space space) throws PersistenceException {
        Group group = null;
        GroupCollection allGroups = new GroupCollection();
        GroupCollection requiredGroups = new GroupCollection();

        allGroups.setSpace(space);
        allGroups.loadAll();

        Iterator it = allGroups.iterator();
        while (it.hasNext()) {
            group = (Group) it.next();

            // Only add Space Admins
            if (!group.getGroupTypeID().equals(GroupTypeID.SPACE_ADMINISTRATOR)) {
                requiredGroups.add(group);
            }
        }


        /**
         * Now sort the groups by name.
         */
        java.util.Collections.sort(requiredGroups, new GroupOrderComparator());
        return requiredGroups;
    }


    //
    // Security Console Methods
    //


    /**
     * Loads the object security settings for the current space and object.
     * After loading, security settings may be rendered as XML.
     *
     * @see #setPnObject
     * @see #getXML
     */
    public void makeSecurityConsole() {
        securityType = OBJECT;

        displayObjectPersmission = new DisplayObjectPermission();
        supportedActionsList = new ActionList();

        // Set the ArrayList that stores the Actions assigned to the object
        // Loading the PnObject by id
        pnObject.load();

        // Creates the Principal/NonPrincipal groups
        setArrayListForGroupPeople();

        // Sets the permitted actions for the ObjectType
        supportedActionsList.setObjectType(pnObject.getObjectType());
        supportedActionsList.load();

        // Grabs the system wide default actions based on the Type 
        actions = supportedActionsList.getDefaultActions();

        // Configure the permission list for the object
        if (permissionList == null) {
            permissionList = new ObjectPermissionList();
            ((ObjectPermissionList) permissionList).setObject(pnObject);
            ((ObjectPermissionList) permissionList).load();
        }

        // Sets the action that are allowed by the object type
        displayObjectPersmission.setActionList(supportedActionsList);
        displayObjectPersmission.setObjectPermissionList(permissionList);

        // Call to method to make sure that the Names in m_objectPermission
        // are not the same in the Group/Person List, will be driven by
        // the m_objectPermission
        checkNames();
    }


    /**
     * Loads the Module security settings for all modules and the current space.
     * After loading, security settings may be rendered as XML.
     *
     * @see #getXML
     */
    public void makeModuleSecurityConsole() {
        securityType = MODULE;

        ModulePermissionList modulePermissionList = new ModulePermissionList();
        displayModulePersmission = new DisplayModulePermission();
        supportedActionsList = new ActionList();
        moduleBySpace = new ArrayList();

        // construct an action list of actions supported by modules
        buildAvailableActions(supportedActionsList);

        // load the module permission list for this space
        modulePermissionList.setSpace(space);
        modulePermissionList.load();

        moduleBySpace = modulePermissionList.getModulesBySpace();
        permissionList = modulePermissionList;

        displayModulePersmission.setModulesBySpace(moduleBySpace);
        displayModulePersmission.setSelectedID(selectedID);
        displayModulePersmission.setGroupList(groups);
        displayModulePersmission.setActionList(supportedActionsList);
        displayModulePersmission.setModulePermissionList(permissionList);

    }


    /**
     * Loads the New Object security permissions for the current space. After
     * loading, security settings may be rendered as XML.
     *
     * @see #getXML
     */
    public void makeDefaultObjectPermissionSecurityConsole() {
        securityType = DEFAULT_OBJECT;

        DefaultObjectPermission defaultObjectPermission = new DefaultObjectPermission();
        DefaultObjectPermissionList defaultObjectPermissionList = new DefaultObjectPermissionList();
        displayDefaultObjectPersmission = new DisplayDefaultObjectPermission();
        supportedActionsList = new ActionList();
        defaultObjectBySpace = new ArrayList();

        // Sets the Space and Object properties for the ObjectPermission		
        defaultObjectPermission.setSpace(space);
        defaultObjectPermissionList.setSpace(space);
        defaultObjectBySpace = defaultObjectPermissionList.getDefaultObjectsBySpace();
        buildAvailableActions(supportedActionsList);

        defaultObjectPermissionList.load();
        permissionList = defaultObjectPermissionList;

        // Sets the action that are allowed by the object type
        displayDefaultObjectPersmission.setSelectedID(selectedID);
        displayDefaultObjectPersmission.setDefaultObjectBySpace(defaultObjectBySpace);
        displayDefaultObjectPersmission.setGroupList(groups);
        displayDefaultObjectPersmission.setPermissionList(permissionList);

    }


    //
    // End Security Console Methods
    //


    /**
     * Adds security permissions for a group (principal or otherwise) to the
     * permissions for the current object.
     */
    public void addGroupToObjectACL(javax.servlet.ServletRequest request) {

        // Scan through the objects current permissions updating with the request parameters
        // m_permissionList was populated because makeSecurityConsole must be called prior to this method
        for (int d = 0; d < permissionList.size(); d++) {
            Permission permission = (Permission) permissionList.get(d);
            Action action = new Action();
            
            // Ignore Space Administrator groups
            if (!permission.getGroup().getGroupTypeID().equals(GroupTypeID.SPACE_ADMINISTRATOR)) {

                // Does the request contain values for the current indexed groups permissions
                // on this object
                String[] checkedValues = request.getParameterValues(permission.getGroup().getID());
                if (checkedValues != null) {
                    permission.clearAll(); // clear the current actions
                    for (int h = 0; h < checkedValues.length; h++) {
                        action.setBitMask(Integer.parseInt(checkedValues[h]));
                        permission.grant(action);
                    }
                }
            }
        }

        // we will be adding either groups or people (principal groups) to
        // the object acl, determine which one
        String[] requestList = request.getParameterValues("Roles");
        ArrayList theGroupList = groupList;
        if (requestList == null) {
            requestList = request.getParameterValues("Persons");
            theGroupList = personList;
        }


        // add groups in request to the object ACL with their default permissions
        int num_values = 0;
        if ((requestList != null) && ((num_values = (requestList.length)) != 0)) {

            for (int z = 0; z < num_values; z++) {  // for each group in request
                String valueAdd = requestList[z];
                Permission existingPermission = checkForIDToAdd(permissionList, valueAdd);

                if (existingPermission == null) {   // requestList[z] is not in the objects permission list
                    
                    // search through all groups for the current space looking for
                    // the group id
                    for (int x = 0; x < theGroupList.size(); x++) {
                        DisplayGroup group = (DisplayGroup) theGroupList.get(x);

                        if (valueAdd.equals(group.getID())) {
                            group.setDisplay(false);

                            ObjectPermission objectPermission = new ObjectPermission();
                            objectPermission.setStatus(ObjectPermission.NEW);
                            objectPermission.setGroup(group);
                            objectPermission.setSpace(space);
                            objectPermission.setObject(pnObject);
                            // If this group has a default permission for the object type then use it
                            // otherwise use the system wide default permission
                            String groupDefaultAction = objectPermission.getDefaultPermission();
                            if (groupDefaultAction == null) {
                                objectPermission.setActionBits(Integer.parseInt(actions));
                            } else {
                                objectPermission.setActionBits(Integer.parseInt(groupDefaultAction));
                            }
                            permissionList.add(objectPermission);
                        }
                    }

                } else {  // requestList[z] is already in the objects permission list
                    
                    // switch the permission status to exist
                    if (existingPermission.getStatus().equals(ObjectPermission.DELETED)) {
                        existingPermission.setStatus(ObjectPermission.EXIST);

                        for (int x = 0; x < theGroupList.size(); x++) {
                            DisplayGroup group = (DisplayGroup) theGroupList.get(x);
                            if (valueAdd.equals(group.getID())) {
                                group.setDisplay(false);
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Removed a group from the Object permission screen.
     *
     * @param request
     */
    public void getHtmlRemovePost(javax.servlet.ServletRequest request) {
        int num_values = 0;
        String removeValue = null;

        String[] m_values = request.getParameterValues("groupID");

        if (m_values != null) {
            num_values = m_values.length;

            if (num_values != 0) {

                for (int g = 0; g < num_values; g++) {
                    removeValue = m_values[g];
                    
                    // Iterate over all permissions, getting the action bits
                    // for the group that that permission represents
                    Iterator permissionIt = permissionList.iterator();
                    while (permissionIt.hasNext()) {
                        Permission permission = (Permission) permissionIt.next();

                        if (!permission.getGroup().getGroupTypeID().equals(GroupTypeID.SPACE_ADMINISTRATOR)) {
                            permission.clearAll();

                            String[] checkedValues = request.getParameterValues(permission.getGroup().getID());
                            if (checkedValues != null) {
                                for (int q = 0; q < checkedValues.length; q++) {
                                    Action action = new Action();
                                    action.setBitMask(Integer.parseInt(checkedValues[q]));
                                    permission.grant(action);
                                }
                            }

                        }

                        if (permission.getStatus().equals(ObjectPermission.EXIST)) {
                            // If it is an existing permission, then flag as deleted
                            // so that it will be removed upon storage
                            if (removeValue.equals(permission.getGroup().getID())) {
                                ((DisplayGroup) permission.getGroup()).setDisplay(true);
                                permission.setStatus(ObjectPermission.DELETED);
                            }

                        } else if (permission.getStatus().equals(ObjectPermission.NEW)) {
                            // It is a new permission added, simply remove it
                            if (removeValue.equals(permission.getGroup().getID())) {
                                ((DisplayGroup) permission.getGroup()).setDisplay(true);
                                permissionIt.remove();
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Get the data from a HTML form post and save in the SecurityManager
     * object. This contains many request parameters of the form:<br>
     * <code>&lt;moduleID&gt;=&lt;selectedAction&gt;</code><br> where
     * &lt;moduleID&gt; is the actual module number and &lt;selectedAction&gt;
     * is the number representing an action that was checked for that
     * module.<br> This method reads the parameters and stored the module
     * permissions against the currently selected group
     *
     * @param request the request containing security settings
     * @throws GroupException if there is a problem instantiating the group
     */
    public void getModuleApplyHtmlPost(javax.servlet.ServletRequest request) throws GroupException {

        Group group = null;
        ModulePermission modulePermission = null;

        GroupProvider groupProvider = new GroupProvider();
        group = groupProvider.newGroup(previousID);

        String moduleActive = request.getParameter("modulePermissionActive");
        if (moduleActive == null) {
            // Module Permissions not active.  Remove all permissions
            modulePermission = new ModulePermission();
            modulePermission.setGroup(group);
            modulePermission.setSpace(space);
            modulePermission.remove();

        } else {
            ArrayList permissionsToStore = new ArrayList();

            // Iterate over all the modules in the current space
            Iterator moduleIt = moduleBySpace.iterator();
            while (moduleIt.hasNext()) {
                modulePermission = new ModulePermission();

                // Get the module id
                String id = ((Module) moduleIt.next()).getId();

                Module module = new Module();
                module.setId(id);
                modulePermission.setGroup(group);
                modulePermission.setModule(module);
                modulePermission.setSpace(space);

                // Get all the checked values for module id
                String[] checkedValues = request.getParameterValues(id);
                if (checkedValues != null) {
                    // Some action boxes are checked for module
                    
                    // For each checked action, grant that permission
                    for (int q = 0; q < checkedValues.length; q++) {
                        Action action = new Action();
                        action.setBitMask(Integer.parseInt(checkedValues[q]));
                        modulePermission.grant(action);
                    }

                } else {
                    // No actions checked for module
                    // Set permission to zero (none)
                    modulePermission.setActionBits(0);

                }

                // Add permission to list to store later
                permissionsToStore.add(modulePermission);

            } //end while

            // Now store all permission
            ModulePermission.storeAll(permissionsToStore);
        }
    }


    /**
     * Get the data from a HTML form post and save in the SecurityManager
     * object.
     *
     * @param request
     * @throws GroupException
     */
    public void getDefaultApplyHtmlPost(javax.servlet.ServletRequest request) throws GroupException {

        Group group = null;
        DefaultObjectPermission defaultObjectPermission = null;


        GroupProvider groupProvider = new GroupProvider();
        group = groupProvider.newGroup(previousID);

        String moduleActive = request.getParameter("modulePermissionActive");
        if (moduleActive == null) {
            // Object permissions not active. Remove all permissions
            defaultObjectPermission = new DefaultObjectPermission();
            defaultObjectPermission.setGroup(group);
            defaultObjectPermission.setSpace(space);
            defaultObjectPermission.remove();

        } else {
            // Module permissions are active.  Set them.
            ArrayList permissionsToStore = new ArrayList();

            // Iterate over all the object types in the current space
            Iterator objectTypeIt = defaultObjectBySpace.iterator();
            while (objectTypeIt.hasNext()) {
                defaultObjectPermission = new DefaultObjectPermission();

                // Get the object type
                String type = ((ObjectType) objectTypeIt.next()).getType();

                ObjectType object = new ObjectType();
                object.setType(type);
                defaultObjectPermission.setGroup(group);
                defaultObjectPermission.setObjectType(object);
                defaultObjectPermission.setSpace(space);

                // Get all the checked value for object type
                String[] checkedValues = request.getParameterValues(type);
                if (checkedValues != null) {
                    // Some boxes are checked for object type

                    // For each checked permission, grant that permission
                    for (int q = 0; q < checkedValues.length; q++) {
                        Action action = new Action();
                        action.setBitMask(Integer.parseInt(checkedValues[q]));
                        defaultObjectPermission.grant(action);
                    }

                } else {
                    // No actions are checked for object type
                    // Set permission to zero (none)
                    defaultObjectPermission.setActionBits(0);

                }

                permissionsToStore.add(defaultObjectPermission);

            } //end while

            DefaultObjectPermission.storeAll(permissionsToStore);
        }
    }


    /**
     * Get the data from a HTML form post and save in the SecurityManager
     * object.
     *
     * @param request
     */
    public void getHtmlApplyPost(javax.servlet.ServletRequest request) {
        ObjectPermission objectPermission = null;

        // most form fields have only one value, but multi-selection lists have multiple values.
        for (int val = 0; val < permissionList.size(); val++) {
            objectPermission = new ObjectPermission();
            Permission permission = (Permission) permissionList.get(val);

            if (permission.getStatus().equals(ObjectPermission.EXIST) || permission.getStatus().equals(ObjectPermission.NEW)) {
                // Only deal with group permissions that are currently selected
                
                if (permission.getGroup().getGroupTypeID().equals(GroupTypeID.SPACE_ADMINISTRATOR)) {
                    // Do nothing

                } else {

                    String[] checkedValues = request.getParameterValues(permission.getGroup().getID());
                    if (checkedValues != null) {
                        permission.clearAll();

                        for (int q = 0; q < checkedValues.length; q++) {
                            Action action = new Action();
                            action.setBitMask(Integer.parseInt(checkedValues[q]));
                            objectPermission.grant(action);
                        }

                    } else {
                        objectPermission.setActionBits(0);
                    }

                    objectPermission.setGroup(permission.getGroup());
                    objectPermission.setObject(pnObject);
                    objectPermission.store();
                    objectPermission.clearAll();
                }

            } else if (permission.getStatus().equals(ObjectPermission.DELETED)) {
                // Permision is deleted, remove all permissions from it
                objectPermission.setGroup(permission.getGroup());
                objectPermission.setObject(pnObject);
                objectPermission.remove();
            }

        }

        permissionList = null;
    }


    /**
     * Checks to see if the Group/Person ID being added to Object Persmissions
     * List exist Returns a string value that represents a group id
     *
     * @param list the collection of <code>Permission</code>s to check
     * @param value the group id
     * @return the permission corresponding to the group id
     */
    public Permission checkForIDToAdd(ArrayList list, String value) {
        Permission permission = null;

        for (int t = 0; t < list.size(); t++) {
            Permission temp = (Permission) list.get(t);
            if (value.equals(temp.getGroup().getID())) {
                permission = temp;
            }
        }
        return permission;
    }


    /**
     * This sets the checkbox for the Security Interface and determines if the
     * permissions have been applied to the group.
     *
     * @return
     */
    public String getChecked() {
        String groupID = null;
        String selected = null;
        String returnValue = null;

        // 10/29/2001 - Tim - Bizarre code alert.
        // I think the purpose of this code is to simply set "selected" equal
        // "m_selectedID" or if that is null, the first non-Space Admin group id
        // In other words: if no group has been selected yet, select the first
        // group.  Note that it does NOT set "m_selected" afterwards.
        for (int x = 0; x < groups.size(); x++) {
            if (!((Group) groups.get(x)).getGroupTypeID().equals(GroupTypeID.SPACE_ADMINISTRATOR)) {
                groupID = ((Group) groups.get(x)).getID();
                selected = selectedID;

                if (selected == null) {
                    selected = groupID;
                    break;
                }
            }
        }

        // Iterates over permissions, looking for the permission that corresponds
        // to the currently selected group id.  If it finds it, then it
        // returns checked=""
        for (int i = 0; i < permissionList.size(); i++) {
            String permissionGroup = ((Permission) permissionList.get(i)).getGroup().getID();

            if (permissionGroup.equals(selected)) {
                returnValue = "checked=\"\"";
                break;
            } else {
                returnValue = "";
            }
        }

        return returnValue;
    }


    /**
     * Checks the GroupID's for the ObjectPermissions against the Group and
     * Person List to determine which should be shown based.
     */
    public void checkNames() {
        String obValue = null;
        String obDisplay = null;
        String personValue = null;
        String groupValue = null;
        boolean bContinue = true;


        for (int s = 0; s < permissionList.size(); s++) {
            bContinue = true;
            obValue = (((Permission) permissionList.get(s)).getGroup()).getID();
            // D - Value represents a value that can be displayed in either group/person list
            // A - Value represents a value that should not be displayed in either group/person list
            obDisplay = ((Permission) permissionList.get(s)).getStatus();
            for (int x = 0; x < personList.size(); x++) {
                personValue = ((Group) personList.get(x)).getID();
                //If the Object Permission value is set to D
                if (obValue.equals(personValue)) {
                    if (obDisplay.equals(ObjectPermission.EXIST) || obDisplay.equals(ObjectPermission.NEW)) {
                        bContinue = false;
                        ((DisplayGroup) personList.get(x)).setDisplay(false);
                    }
                } else {
                    bContinue = true;
                }

            }

            if (bContinue) {
                for (int h = 0; h < groupList.size(); h++) {
                    groupValue = ((Group) groupList.get(h)).getID();
                    //If the Object Permission value is set to D
                    if (obValue.equals(groupValue)) {
                        if (obDisplay.equals(ObjectPermission.EXIST) || obDisplay.equals(ObjectPermission.NEW)) {
                            ((DisplayGroup) groupList.get(h)).setDisplay(false);
                        }
                    }

                }
            }
        }
    }


    public void copyPermissions(String srcID, String destID) throws PersistenceException {

        DBBean db = new DBBean();
        try {
            // call Stored Procedure to insert or update all the tables involved in storing a meeting.
            db.prepareCall("begin SECURITY.APPLY_DOCUMENT_PERMISSIONS(?,?,?,?,?); end;");
            db.cstmt.setInt(1, Integer.parseInt(destID));
            db.cstmt.setInt(2, Integer.parseInt(srcID));
            db.cstmt.setNull(3, java.sql.Types.INTEGER);
            db.cstmt.setInt(4, Integer.parseInt(space.getID()));
            db.cstmt.setNull(5, java.sql.Types.INTEGER);
            db.executeCallable();
        } catch (SQLException sqle) {
            throw new PersistenceException("Error copying permissions", sqle);
        } catch (NumberFormatException nfe) {
            throw new PersistenceException("ParseInt Failed in " +
                "SecurityManager.copyPermissions()", nfe);
        } finally {
            db.release();
        }

    }


    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
     * Converts the object to XML representation without the XML version tag.
     * This method returns the object as XML text. The XML returned depends on
     * which security console was "made"
     *
     * @return XML representation
     * @see #makeSecurityConsole
     * @see #makeModuleSecurityConsole
     * @see #makeDefaultObjectPermissionSecurityConsole
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<PermissionList>\n");
        xml.append(supportedActionsList.getXMLBody());

        if (securityType.equals(OBJECT)) {
            xml.append(displayObjectPersmission.getXMLBody());

        } else if (securityType.equals(MODULE)) {
            xml.append(displayModulePersmission.getXMLBody());
            setPreviousID(displayModulePersmission.getPreviousID());

        } else if (securityType.equals(DEFAULT_OBJECT)) {
            xml.append(displayDefaultObjectPersmission.getXMLBody());
            setPreviousID(displayDefaultObjectPersmission.getPreviousID());

        }

        xml.append("</PermissionList>\n");

        return xml.toString();
    }


    /**
     * Gets the presentation of the component This method will apply the
     * stylesheet to the XML representation of the component and return the
     * resulting text
     *
     * @return presetation of the component
     */
    public String getPresentation() {
        return formatter.getPresentation(getXML());
    }


    /**
     * Sets the stylesheet file name used to render this component. This method
     * accepts the name of the stylesheet used to convert the XML representation
     * of the component to a presentation form.
     *
     * @param styleSheetFileName name of the stylesheet used to render the XML
     * representation of the component
     */
    public void setStylesheet(String styleSheetFileName) {
        formatter.setStylesheet(styleSheetFileName);
    }


    /**
     * Updates the specified action list, adding Actions to be displayed on the
     * security screen.
     *
     * @param list the action list to which to add actions
     */
    private void buildAvailableActions(ActionList list) {
        Action action = new Action();

        action.setName("view");
        action.setDescription(PropertyProvider.get("prm.security.action.view.description"));
        action.setBitMask(Action.VIEW);
        list.add(action);

        action = new Action();
        action.setName("modify");
        action.setDescription(PropertyProvider.get("prm.security.action.modify.description"));
        action.setBitMask(Action.MODIFY);
        list.add(action);

        action = new Action();
        action.setName("create");
        action.setDescription(PropertyProvider.get("prm.security.action.create.description"));
        action.setBitMask(Action.CREATE);
        list.add(action);

        action = new Action();
        action.setName("delete");
        action.setDescription(PropertyProvider.get("prm.security.action.delete.description"));
        action.setBitMask(Action.DELETE);
        list.add(action);

        action = new Action();
        action.setName("modify permission");
        action.setDescription(PropertyProvider.get("prm.security.action.modifypermission.description"));
        action.setBitMask(Action.MODIFY_PERMISSIONS);
        list.add(action);

        action = new Action();
        action.setName("share");
        action.setDescription(PropertyProvider.get("prm.security.action.share.description"));
        action.setBitMask(Action.SHARE);
        list.add(action);
    }


    /**
     * Provides a Comparator that orders groups based on their type and name.
     * Non-principal groups are lower than principal groups.  Within this
     * classification, groups are ordered by name, ignoring case.
     */
    private static class GroupOrderComparator implements java.util.Comparator {

        /**
         * Compares two <code>Group</code>s.
         *
         * @param o1 first group
         * @param o2 second group
         * @return negative number if first group is lower than second; zero if
         *         both groups are equal; positive number if first group is
         *         higher than second group
         */
        public int compare(Object o1, Object o2) {
            int returnValue = 0;

            Group group1 = (Group) o1;
            Group group2 = (Group) o2;

            if (!group1.getGroupTypeID().equals(group2.getGroupTypeID())) {
                // Not of same type.  Special handling.
                // Principal groups are sorted later than other types

                if (group2.getGroupTypeID().equals(GroupTypeID.PRINCIPAL)) {
                    // If group2 is a principal group, group1 is less
                    returnValue = -1;

                } else if (group1.getGroupTypeID().equals(GroupTypeID.PRINCIPAL)) {
                    // if group 1 is a principal group, group1 is greater
                    returnValue = 1;
                }
            }


            // If we haven't been able to figure out that one is less than
            // the other, then base the comparison on their names
            if (returnValue == 0) {
                // Returns -ve if group1's name is less than group2's name
                returnValue = group1.getName().compareToIgnoreCase(group2.getName());
            }

            return returnValue;
        }

    }

}
