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

import java.util.ArrayList;
import java.util.List;

import net.project.persistence.IXMLPersistence;
import net.project.security.group.GroupTypeID;
import net.project.xml.XMLFormatter;

/**
 * A set of actions which are permitted on an Object.
 *
 * @author Michael Ariston
 * @since 3/6/2000
 */
public class DisplayObjectPermission extends ObjectPermission
    implements java.io.Serializable, IXMLPersistence {

    private List permissionList = null;
    private ArrayList actionList = null;

    /** Used to transform XML and XSL into html. */
    protected XMLFormatter m_formatter = null;

    /**
     * Constructs a ObjectPermission Object.
     */
    public DisplayObjectPermission() {
        m_formatter = new XMLFormatter();
    }

    /**
     * Gets the Arraylist of objectPermissions.
     */
    public void setObjectPermissionList(ArrayList list) {
        permissionList = list;
    }

    /**
     * Set the Space context for this permission.
     */
    public void setActionList(ActionList actioList) {
        actionList = actioList;
    }

    /**
     * Get the Space context for this permission.
     */
    public ArrayList getActionList() {
        return actionList;
    }

    /**
     * Returns an XML representation of the object.
     *
     * @return XML representation of the Action.
     */
    public String getXML() {
        return IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
     * Returns this object's XML representation, without the XML version tag.
     *
     * @return XML representation of this object
     * @see net.project.persistence.IXMLPersistence#getXML
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        for (int i = 0; i < permissionList.size(); i++) {
            Permission permission = (Permission) permissionList.get(i);

            if (permission.getStatus().equals(ObjectPermission.EXIST) ||
                permission.getStatus().equals(ObjectPermission.NEW)) {

                if (permission.getGroup().getGroupTypeID().equals(GroupTypeID.SPACE_ADMINISTRATOR)) {
                    // Do nothing

                } else {
                    xml.append("<line>\n");
                    xml.append(permission.getGroup().getXMLBody());
                    xml.append("<objectPermission>\n");
                    setActionBits(((ObjectPermission) permissionList.get(i)).getActionsBits());
                    for (int w = 0; w < actionList.size(); w++) {
                        int action = ((Action) actionList.get(w)).getBitMask();
                        String actionName = ((Action) actionList.get(w)).getName();
                        if (actionAllowed(action)) {
                            xml.append("<permission name=\"" + actionName + "\" bitmask=\"" + action + "\">checked</permission>\n");
                        } else {
                            xml.append("<permission name=\"" + actionName + "\" bitmask=\"" + action + "\"></permission>\n");
                        }

                    }
                    xml.append("</objectPermission>\n");
                    xml.append("</line>\n");
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
     * @return presentation of the component
     */
    public String getPresentation() {
        return m_formatter.getPresentation(getXML());
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
        m_formatter.setStylesheet(styleSheetFileName);
    }
}

