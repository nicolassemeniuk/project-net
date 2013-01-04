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

 /*------------------------------------------------------------------------------------------+
|
|    $RCSfile$
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|    $Author: umesha $
|
|
+------------------------------------------------------------------------------------------*/
package net.project.notification;

import net.project.persistence.IXMLPersistence;
import net.project.xml.XMLUtils;

/**
 * Represent a Single Type of Notification Object Type
 * @author deepak
 */

class NotificationObjectType implements IXMLPersistence {
    /**
     * ID of the Space Type
     */
    private String objectType = null;
    /**
     * Display Name of the Space Type
     */
    private String displayName = null;

    /* -------------------------------  Constructors  ------------------------------- */

    public NotificationObjectType(String objectType, String displayName) {
        this.objectType = objectType;
        this.displayName = displayName;
    }

    /**
     * Returns the Displayname for the current Object
     * @return String
     */
    public String getName() {
        return this.displayName;
    }

    /**
     * Returns the ObjectType for the current Object
     * @return String
     */
    public String getObjectType() {
        return this.objectType;
    }

    /**
     *  Converts the object to XML representation of the Notification Object Type.
     *  This method returns the Notification Object Type as XML text.
     *  @return XML representation of the Notification Object Type
     */
    public String getXML() {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }

    /**
     *  Converts the Space Type to XML representation without the XML version tag.
     *  This method returns the Space Type as XML text.
     *  @return XML representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer(100);
        xml.append("<NotificationObjectType>\n");
        xml.append("<ObjectType>" + XMLUtils.escape(getObjectType()) + "</ObjectType>\n");
        xml.append("<name>" + XMLUtils.escape(getName()) + "</name>\n");
        xml.append("</NotificationObjectType>\n");
        return xml.toString();
    }

}
