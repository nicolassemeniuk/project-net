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
package net.project.notification;

import net.project.xml.XMLUtils;


/**
 * Provides a default implementation of <code>Event</code>.
 * @author Unascribed
 * @since Version 2
 */
public class GenericEvent extends Event {

    /**
     * Returns the XML representation of this event.
     * <p>
     * <b>Note:</b> This is substantially different to the XML
     * returned by the default implementation of {@link Event#getXMLBody()}.
     * </p>
     * @return the XML representation
     */
    public String getXMLBody() {
        StringBuffer xmlBody = new StringBuffer();
        xmlBody.append("<subscription>");
        xmlBody.append(getXMLProperties());
        xmlBody.append("</subscription>");
        return xmlBody.toString();
    }

    /**
     * Returns the properties of this IEvent object as an XML String.
     * @return the properties of this object in an XML String
     */
    private String getXMLProperties() {

        StringBuffer xmlProperties = new StringBuffer();
        String tab = "\t\t";

        xmlProperties.append(tab);
        xmlProperties.append(tab + "<id />\n");
        xmlProperties.append(tab + "<initiator_id>" + XMLUtils.escape(getInitiatorID()) + "</initiator_id>\n");
        xmlProperties.append(tab + "<event_time>" + XMLUtils.formatISODateTime(getEventTime()) + "</event_time>\n");
        xmlProperties.append(tab + "<description>" + XMLUtils.escape(getDescription()) + "</description>\n");
        xmlProperties.append(tab + "<target_object> \n");
        xmlProperties.append(tab + "<id>" + XMLUtils.escape(getTargetObjectID()) + "</id>\n");
        xmlProperties.append(tab + "<target_object_xml>" + getTargetObjectXML() + "</target_object_xml>\n");
        xmlProperties.append(tab + "</target_object> \n");
        xmlProperties.append(tab);

        return xmlProperties.toString();
    }

}
