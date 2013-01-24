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

import net.project.persistence.IXMLPersistence;
import net.project.xml.XMLUtils;

/**
 * Provides a schedule for deliverint subscribed event notifications.
 *
 * @author Unascribed
 * @since Version 2
 */
public class SubscriptionSchedule implements IXMLPersistence {

    /** the system defined representation of what the recurrent delivery schedule for the  notification associated with this subscription will be*/
    private String deliveryIntervalID;

    /** the system defined representation of what the recurrent delivery schedule for the  notification associated with this subscription will be*/
    private String deliveryIntervalName;


    public SubscriptionSchedule() {
    }

    public String getDeliveryIntervalID() {
        return deliveryIntervalID;
    }

    public void setDeliveryIntervalID(String deliveryIntervalID) {
        this.deliveryIntervalID = deliveryIntervalID;
    }


    private String getDeliveryIntervalName() {
        return deliveryIntervalName;
    }

    public void setDeliveryIntervalName(String deliveryIntervalName) {
        this.deliveryIntervalName = deliveryIntervalName;
    }


    /**
     * returns a well formed XML representation of this <CODE>SubscriptionSchedule</CODE>, including the XML Version string
     *
     * @return a well-formed XML representation of this <code>SubscriptionSchedule</code>
     */
    public String getXML() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(net.project.xml.IXMLTags.XML_VERSION_STRING);
        stringBuffer.append(getXMLBody());
        return stringBuffer.toString();
    }

    /**
     * returns a well formed XML representation of this <CODE>SubscriptionSchedule</CODE>,
     * including the XML Version string
     *
     * @return a well-formed XML representation of this <code>Subscription</code>
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        // For the time- being only consider two properties
        xml.append("<subscription_schedule>\n");
        xml.append("<delivery_interval_id>" + XMLUtils.escape(getDeliveryIntervalID()) + "</delivery_interval_id>\n");
        xml.append("<delivery_interval_name>" + XMLUtils.escape(getDeliveryIntervalName()) + "</delivery_interval_name>\n");
        xml.append("</subscription_schedule>\n");
        return xml.toString();
    }

}
