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

 package net.project.notification;

import java.util.ArrayList;

import net.project.persistence.PersistenceException;
import net.project.xml.XMLUtils;

/**
 * A scheduled subscription is the instantation of a subscription due to the
 * occurrence of an event.
 * <p>
 * It maintains a collection of subscribers that must be notified about the
 * event and the description of the object to which the event pertains.
 * </p>
 *
 * @author Unascribed
 * @since Version 2
 */
class ScheduledSubscription {
    /** The id of the scheduled subscription record in the pn_scheduled_subscription table **/
    String objectID = null;
    String name = null;
    String description = null;
    String notificationTypeID = null;
    String notificationTypeName = null;
    String notificationTypeDescription = null;
    String notificationTypeDefaultMessage = null;
    String customMessage = null;
    String subscriberBatchID = null;
    String targetObjectID = null;
    String targetObjectType = null;
    String targetObjectXML = null;
    String initiatorID = null;
    String eventTime = null;
    String eventType = null;
    private String createDate = null;
    private String createdByID = null;
    private SubscriberCollection subscribers = new SubscriberCollection();
    private String senderID;

    /** The ID of the space in which the notification event took place. */
    private String spaceID = null;

    /** The name of the space for the space ID. */
    private String spaceName = null;

    /**
     * Specifies the ID of the space in which the event took place.
     * @param spaceID the space ID
     */
    void setSpaceID(String spaceID) {
        this.spaceID = spaceID;
    }

    void setSenderID (String senderID) {
        this.senderID = senderID;
    }

    String getSenderID () {
        return this.senderID;
    }

    /**
     * Specifies the name of the space in which the event took place.
     * @param spaceName the space name
     */
    void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    /* -------------------------------  DB Methods  ------------------------------- */

    public void loadSubscribers(String batchID) {
        this.subscribers.setBatchID(batchID);
        this.subscribers.load();
    }

    public ArrayList getConsolidatedDeliveryAddressList() {
        return this.subscribers.getConsolidatedAddressList();
    }



    /* -------------------------------  util methods  ------------------------------- */

    public String getXML() {

        StringBuffer xml = new StringBuffer();

        xml.append(net.project.xml.IXMLTags.XML_VERSION_STRING);
        xml.append(getXMLBody());

        return xml.toString();
    }

    private String getXMLBody() {

        StringBuffer xml = new StringBuffer();

        xml.append("<scheduled_subscription>");
        xml.append(getXMLProperties());
        xml.append("</scheduled_subscription>");

        return xml.toString();
    }

    public String getXMLProperties() {
        StringBuffer xml = new StringBuffer();
        net.project.resource.Person initiator = new net.project.resource.Person(this.initiatorID);

        xml.append("<scheduled_subscription_id>" + XMLUtils.escape(this.objectID) + "</scheduled_subscription_id>");
        xml.append("<name>" + XMLUtils.escape(this.name) + "</name>");
        xml.append("<description>" + XMLUtils.escape(this.description) + "</description>");
        xml.append("<notification_type>");
        xml.append("<notification_type_id>" + XMLUtils.escape(this.notificationTypeID) + "</notification_type_id>");
        xml.append("<name>" + XMLUtils.escape(this.notificationTypeName) + "</name>");
        xml.append("<description>" + XMLUtils.escape(this.notificationTypeDescription) + "</description>");
        xml.append("<default_message>" + XMLUtils.escape(this.notificationTypeDefaultMessage) + "</default_message>");
        xml.append("<object_type>" + XMLUtils.escape(this.targetObjectType) + "</object_type>");
        xml.append("</notification_type>");
        xml.append("<custom_message>" + XMLUtils.escape(this.customMessage) + "</custom_message>");
        xml.append("<subscriber_batch_id>" + XMLUtils.escape(this.subscriberBatchID) + "</subscriber_batch_id>");
        xml.append("<target_object_id>" + XMLUtils.escape(this.targetObjectID) + "</target_object_id>");
        xml.append("<target_object_type>" + XMLUtils.escape(this.targetObjectType) + "</target_object_type>");
        xml.append("<target_object_xml>" + this.targetObjectXML + "</target_object_xml>");
        xml.append("<sender_id>" + this.senderID + "</sender_id>");

        try {
            initiator.load();

            xml.append("<initiator>");
            xml.append("<initiator_id>" + XMLUtils.escape(this.initiatorID) + "</initiator_id>");
            xml.append("<name>" + XMLUtils.escape(initiator.getDisplayName()) + "</name>");
            xml.append("<email_address>" + XMLUtils.escape(initiator.getEmail()) + "</email_address>");
            xml.append("</initiator>");
        } catch (PersistenceException pe) {
            // do nothing
        }

        xml.append("<event_time>" + XMLUtils.escape(this.eventTime) + "</event_time>");
        xml.append("<event_type>" + XMLUtils.escape(this.eventType) + "</event_type>");
        xml.append("<create_date>" + XMLUtils.escape(this.createDate) + "</create_date>");
        xml.append("<created_by_id>" + XMLUtils.escape(this.createdByID) + "</created_by_id>");

        xml.append("<Space>");
        xml.append("<SpaceID>").append(this.spaceID).append("</SpaceID>");
        xml.append("<SpaceName>").append(this.spaceName).append("</SpaceName>");
        xml.append("</Space>");

        xml.append(this.subscribers.getXMLBody());

        return xml.toString();
    }

}
