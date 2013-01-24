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

import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.security.group.Group;
import net.project.security.group.GroupCollection;
import net.project.security.group.GroupTypeID;
import net.project.xml.XMLFormatter;

/**
 * A set of actions which are permitted on a Module for display purposes.
 */
public class DisplayModulePermission 
        extends ModulePermission 
        implements Serializable, net.project.persistence.IXMLPersistence {

    /**
     * Sets the Object Type context
     */
    protected ObjectType m_objectType = null;

    protected ArrayList m_permissionList = null;
    protected ArrayList m_actionList = null;
    protected ArrayList m_modulesBySpace = null;
    protected GroupCollection m_groupList = null;

    /** 
        Sets the status for this permission 
    */
    protected String m_status = null;

    /** Contains XML formatting information and utilities specific to this object **/
    protected XMLFormatter m_formatter = new XMLFormatter();

    /**
        Constructs a DisplayModulePermission Object
    */
    public DisplayModulePermission() {
        m_formatter = new XMLFormatter();
    }

    /**
        sets the Arraylist of ModulePermissions
    */
    public void setModulePermissionList(ArrayList list) {
        m_permissionList = list;
    }

    /** 
        Set the permitted actions for the is module 
    */
    public void setActionList(ActionList actioList) {
        m_actionList = actioList;
    }

    /** Set the Modules for the Space */
    public void setModulesBySpace(ArrayList list) {
        m_modulesBySpace = list;
    }

    /** Get the actions list for this module */
    public ArrayList getActionList() {
        return m_actionList;
    }

    /** Set the grouplist for the given space/module */
    public void setGroupList(GroupCollection groupList) {
        m_groupList = groupList;
    }


    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }
    
    
    /**
     * Returns an XML representation of the object.
     * @return XML representation of the Action.
     */
    public String getXMLBody() {

        StringBuffer xml = new StringBuffer();
        String permissionGroupID = null;
        String groupID = null;

        // This list contains all the groups that the user is a member of
        for (int x = 0; x < m_groupList.size(); x++) {
            if (!(((Group)m_groupList.get(x)).getGroupTypeID()).equals(GroupTypeID.SPACE_ADMINISTRATOR)) {
                groupID = ((Group)m_groupList.get(x)).getID();
                String selected = getSelectedID();

                // Sets the selected value for the module to be viewed. This value is
                // important to the PnSecurityMananger in order for the ModulePermission to 
                // store the proper group and the permissions associated for that module
                if (selected == null)
                    setPreviousID(groupID);
                else
                    setPreviousID(selected);
                // This will display the modules that the group has the ability to 
                // modify the permissions
                if (selected == null || selected.equals(groupID)) {
                    // List contians all the modules that are contained with in a given space	
                    for (int r = 0; r < m_modulesBySpace.size(); r++) {
                        String name = ((Module) m_modulesBySpace.get(r)).getName();
                        String desc = ((Module) m_modulesBySpace.get(r)).getDescription();
                        String id = ((Module) m_modulesBySpace.get(r)).getId();

                        xml.append("<line>\n");
                        xml.append("<objectName>" + name + "</objectName>\n");
                        xml.append("<objectDesc>" + desc + "</objectDesc>\n");
                        xml.append("<id>" + id + "</id>\n");
                        xml.append("<objectPermission>\n");

                        // List contains all the actions that are supported by the Module
                        for (int w = 0; w < m_actionList.size() ; w++) {
                            int action = ((Action)m_actionList.get(w)).getBitMask();
                            String actionName = ((Action)m_actionList.get(w)).getName();

                            boolean check = false;
                            boolean noCheck = false;
                            boolean noBox = false;

                            // List contains the permissions that are allowed for module with in that
                            // particular space. This will determine if the check box should be checked 
                            for (int i = 0; i < m_permissionList.size(); i++) {
                                String permissionGroup = ((ModulePermission) m_permissionList.get(i)).getGroup().getID();
                                String moduleID = (((ModulePermission) m_permissionList.get(i)).getModule()).getId();

                                if (groupID.equals(permissionGroup)) {
                                    setActionBits(((Permission)m_permissionList.get(i)).getActionsBits());

                                    if (id.equals(moduleID)) {
                                        if (actionAllowed(action)) {
                                            check = true;
                                            break;
                                        } else {
                                            noCheck = true;
                                            break;
                                        }
                                    } else {
                                        noBox = true;
                                    }
                                } else {
                                    noBox = true;
                                }
                            }
                            if (check)
                                xml.append("<permission name=\""+ actionName + "\" bitmask=\""+ action +"\">checked</permission>\n");
                            else if (noCheck)
                                xml.append("<permission name=\""+ actionName + "\" bitmask=\""+ action +"\"></permission>\n");
                            else if (noBox)
                                xml.append("<permission name=\""+ actionName + "\" bitmask=\""+ action +"\"></permission>\n");

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
    * Gets the presentation of the component
    * This method will apply the stylesheet to the XML representation of the component and
    * return the resulting text
    * 
    * @return presetation of the component
    */
    public String getPresentation() {
        return m_formatter.getPresentation(getXML());
    }


    /**
     * Sets the stylesheet file name used to render this component.
     * This method accepts the name of the stylesheet used to convert the XML representation of the component
     * to a presentation form.
     *
     * @param styleSheetFileName name of the stylesheet used to render the XML representation of the component
     */
    public void setStylesheet(String styleSheetFileName) {
        m_formatter.setStylesheet(styleSheetFileName);
    }  
}

