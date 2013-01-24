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
import java.util.ArrayList;

import net.project.base.ObjectType;
import net.project.persistence.IXMLPersistence;
import net.project.security.group.Group;
import net.project.security.group.GroupCollection;
import net.project.security.group.GroupTypeID;
import net.project.xml.XMLFormatter;

/**
 * A set of actions which are permitted on an Object.
 *
 * @author Michael Ariston
 * @since 3/31/00
 */
public class DisplayDefaultObjectPermission extends DefaultObjectPermission implements Serializable, IXMLPersistence {
    private ObjectType objectType = null;
    private ArrayList permissionList = null;
    private ActionList supportedActionList = null;
    private ArrayList defaultObjectBySpace = null;
    // this is a system wide action list, therefore it can be static
    private static ActionList actionList = new ActionList();

    static {
        actionList.loadAllActions();
    }

    private GroupCollection groupList = null;

    /** Converts XSL + XML to HTML. */
    private XMLFormatter formatter = null;

    /**
     * Constructs a DisplayDefaultObjectPermission Object
     */
    public DisplayDefaultObjectPermission() {
        formatter = new XMLFormatter();
    }

    /**
     * sets the Arraylist of DefaultObjectPermissions
     */
    public void setPermissionList(ArrayList list) {
        permissionList = list;
    }


    /**
     * Get the actions list for this Object
     */
    public ArrayList getActionList() {
        return actionList;
    }

    /**
     * Set the DefaultObject for the Space
     */
    public void setDefaultObjectBySpace(ArrayList list) {
        defaultObjectBySpace = list;
    }

    /**
     * Set the grouplist for the given space/module
     */
    public void setGroupList(GroupCollection groupList) {
        this.groupList = groupList;
    }

    /**
     * Set the selected ID for this permission
     */
    public void setSelectedID(String value) {
        m_selectedID = value;
    }

    /**
     * Get the selected ID for this permission
     */
    public String getSelectedID() {
        return m_selectedID;
    }


    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }


    /**
     * Returns an XML representation of the object.
     *
     * @return XML representation of the Action.
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        String groupID = null;
        String objectType = null;
        String objectDesc = null;

        boolean newGroup = false;
        boolean check = false;
        boolean noCheck = false;
        boolean noBox = false;
        boolean noGroupCheck = false;
        boolean noGroupBox = false;

        supportedActionList = new ActionList();

        for (int x = 0; x < groupList.size(); x++) {
            if (!(((Group) groupList.get(x)).getGroupTypeID()).equals(GroupTypeID.SPACE_ADMINISTRATOR)) {
                groupID = ((Group) groupList.get(x)).getID();
                String selected = getSelectedID();

                // Sets the selected value for the module to be viewed. This value is
                // important to the PnSecurityMananger in order for the ModulePermission to
                // store the proper group and the permissions associated for that module
                if (selected == null) {
                    setPreviousID(groupID);
                } else {
                    setPreviousID(selected);
                }

                if (selected == null || selected.equals(groupID)) {
                    for (int r = 0; r < defaultObjectBySpace.size(); r++) {
                        objectType = ((ObjectType) defaultObjectBySpace.get(r)).getType();
                        objectDesc = ((ObjectType) defaultObjectBySpace.get(r)).getDescription();

                        xml.append("<line>\n");
                        xml.append("<objectDesc>" + objectDesc + "</objectDesc>\n");
                        xml.append("<objectName>" + objectType + "</objectName>\n");
                        xml.append("<objectPermission>\n");

                        ObjectType object = new ObjectType();
                        object.setType(objectType);
                        supportedActionList.setObjectType(object);
                        supportedActionList.load();

                        // This m_actionList will hold all the Actions that are part of the application
                        // Will help determine if a checkbox is to be added to the XML and if it is to be checked
                        for (int w = 0; w < actionList.size(); w++) {
                            int action = ((Action) actionList.get(w)).getBitMask();
                            String actionName = ((Action) actionList.get(w)).getName();

                            newGroup = false;
                            check = false;
                            noCheck = false;
                            noBox = false;
                            noGroupCheck = false;
                            noGroupBox = false;

                            // Contains all the Permissions that are allowed for a particular Object
                            for (int i = 0; i < permissionList.size(); i++) {
                                String permissionGroup = ((DefaultObjectPermission) permissionList.get(i)).getGroup().getID();
                                this.objectType = ((DefaultObjectPermission) permissionList.get(i)).getObjectType();

                                if (groupID.equals(permissionGroup)) {
                                    if (objectType.equals(this.objectType.getType())) {
                                        // Contains a list of the Supported Actions for a particular object
                                        for (int y = 0; y < supportedActionList.size(); y++) {
                                            int supportedAction = ((Action) supportedActionList.get(y)).getBitMask();
                                            if (action == supportedAction) {
                                                setActionBits(((Permission) permissionList.get(i)).getActionsBits());
                                                if (actionAllowed(action)) {
                                                    check = true;
                                                    noBox = false;
                                                    newGroup = false;
                                                    break;
                                                } else {
                                                    noCheck = true;
                                                    noBox = false;
                                                    newGroup = false;
                                                    break;
                                                }
                                            } else {
                                                noBox = true;
                                                newGroup = false;
                                            }
                                        }
                                        break;
                                    }
                                } else {
                                    newGroup = true;
                                }
                            }

                            // New group represents a group that is part of the Group List but has yet had
                            // any sort of permissions assigned to them
                            if (newGroup) {
                                for (int y = 0; y < supportedActionList.size(); y++) {
                                    int supportedAction = ((Action) supportedActionList.get(y)).getBitMask();

                                    if (action == supportedAction) {
                                        noGroupCheck = true;
                                        break;
                                    } else {
                                        noGroupBox = true;
                                    }
                                }
                                if (noGroupCheck) {
                                    xml.append("<permission name=\"" + actionName + "\" bitmask=\"" + action + "\" box=\"1\"></permission>\n");
                                } else if (noGroupBox) {
                                    xml.append("<permission name=\"" + actionName + "\" bitmask=\"" + action + "\" box=\"2\"></permission>\n");
                                }
                            } else {
                                if (check) {
                                    xml.append("<permission name=\"" + actionName + "\" bitmask=\"" + action + "\" box=\"1\">checked</permission>\n");
                                } else if (noCheck) {
                                    xml.append("<permission name=\"" + actionName + "\" bitmask=\"" + action + "\" box=\"1\"></permission>\n");
                                } else if (noBox) {
                                    xml.append("<permission name=\"" + actionName + "\" bitmask=\"" + action + "\" box=\"2\"></permission>\n");
                                }
                            }
                        }

                        xml.append("</objectPermission>\n");
                        xml.append("</line>\n");
                    }
                    break;
                }
            }
        }
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
}
