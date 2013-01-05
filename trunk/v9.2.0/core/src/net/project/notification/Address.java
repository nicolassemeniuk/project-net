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
 * Provides Addressing information for a subscriber.
 * This includes a string email address, the type of delivery mechanism,
 * the recipient's display name and the ID of the user to be used
 * for language and locale customization of the delivered notification.
 * <p>
 * The Address can be used for any subscriber, whether an application user or
 * external email address.
 * </p>
 *
 * @author Unascribed
 * @since Version 2.0
 */
class Address implements IXMLPersistence {

    /**
     * Textual Representation of the Address
     */
    private final String address;
    /**
     * The Delivery Type ID
     */
    private final String deliveryTypeID;

    /**
     * The name of the recipient
     */
    private final String recipientName;

    /**
     * The ID of the user whose customization info (Language + Locale) would
     * be used to customize the email
     */
    private final String customizationUserID;

    /**
     * Creates a new Address.
     *
     * This constructor should only be used to create an Address that may
     * be stored; it should not be used for creating an Address that will be
     * used to deliver a notification, since it is missing a recipient display
     * name and a customization user ID.
     * @param address the textual representation for the address (often email address)
     * @param deliveryTypeID the ID of the delivery type for the notification
     * sent to this Address
     */
    Address(String address, String deliveryTypeID) {
        this(address, deliveryTypeID, null, null);
    }

    /**
     * Creates a new Address for sending a notification.
     *
     * @param address the email address for this address
     * @param deliveryTypeID the type of deliverable for the subscription to
     * which this address pertains
     * @param recipientName the display name of the recipient to whom this
     * address belongs
     * @param customizationUserID the ID of the user used for customizing the
     * deliverable (e.g. language and locale) created for the subscription to
     * which this address pertains
     */
    Address(String address, String deliveryTypeID, String recipientName, String customizationUserID) {
        this.address = address;
        this.deliveryTypeID = deliveryTypeID;
        this.recipientName = recipientName;
        this.customizationUserID = customizationUserID;
    }

    /**
     * Returns the textual representation for the Address.
     *
     * @return the textual representation for the Address
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Returns the Delivery Type ID
     *
     * @return 	   the Delivery Type ID
     */
    public String getDeliveryTypeID() {
        return this.deliveryTypeID;
    }

    /**
     * Gets the customization user ID of this address.
     * @return  the customization user ID of this address
     */
    public String getCustomizationUserID() {
        return customizationUserID;
    }

    /**
     * Returns the name of the Recipient
     *
     * @return the name of the Recipient
     */
    private String getRecipientName() {
        return this.recipientName;
    }

    /**
     * Indicates whether an Address is equal to this address.
     * Two Address are equal if their address and delivery types are equal.
     * @return true if the specified address is equal to this one; false otherwise
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;

        final Address address1 = (Address) o;

        if (!address.equals(address1.address)) return false;
        if (!deliveryTypeID.equals(address1.deliveryTypeID)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = address.hashCode();
        result = 29 * result + deliveryTypeID.hashCode();
        return result;
    }

    /**
     * Returns a String representing the XML representation of Address.
     *
     * @return String
     */
    public String getXML() {
        StringBuffer xml = new StringBuffer();

        xml.append(net.project.xml.IXMLTags.XML_VERSION_STRING);
        xml.append(getXMLBody());

        return xml.toString();
    }

    /**
     * Returns a String representing the XML representation of Address.
     *
     * @return String the properties of this object in an XML String
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<address>\n");
        xml.append("<address_details>" + XMLUtils.escape(getAddress()) + "</address_details>\n");
        xml.append("<recipient_name>" + XMLUtils.escape(getRecipientName()) + "</recipient_name>\n");
        xml.append("<delivery_type_id>" + XMLUtils.escape(getDeliveryTypeID()) + "</delivery_type_id>\n");
        xml.append("</address>\n");

        return xml.toString();
    }
}
