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

import java.util.ArrayList;

/**
 * Provides a collection of {@link ScheduledSubscription}s where they are all
 * destined for a single recipient.
 * <p>
 * While each ScheduledSubscription may have multiple addresses, the invariant
 * is that each one in this collection has a common address.
 * </p>
 *
 * @author Unascribed
 * @since Version 2
 */
class SingleRecipientScheduledSubscriptionCollection extends ArrayList {

    /**
     * The type of delivery for all scheduled subscriptions.
     */
    private String deliveryTypeID = null;

    /**
     * The id of the user whose customization information(locale + configuration) will be used
     * to customize the notification.
     */
    private String customizationUserID = null;


    /** The id of the user that created the scheduled subscription.
     * this person will be considered to be the "sender" for scheduled subscriptions.
     */
    private String senderID;

    /**
     *  The delivery address of scheduled properties
     */
    private String deliveryAddress = null;

    void setDeliveryTypeID(String type) {
        this.deliveryTypeID = type;
    }

    String getDeliveryTypeID() {
        return this.deliveryTypeID;
    }

    /**
     * Sets the ID of the user that will customize the delivered notification
     * for these scheduled subscriptions.
     * <p>
     * The ID is used for determining the language of the notification and
     * localization information (such as timezone and locale for date and
     * number formatting).
     * </p>
     * <p>
     * The user ID is usually the user that the recipient is derived from;
     * however, in the case that the recipient is not a user of the
     * application (e.g. external recipients) the user ID may be that of
     * the sender.
     * </p>
     *
     * @param customizationUserID the ID of the user to use when delivering
     * the notification
     * @see #getCustomizationUserID
     */
    public void setCustomizationUserID(String customizationUserID) {
        this.customizationUserID = customizationUserID;
    }

    /**
     * Returns the customization user ID for these scheduled subscriptions.
     *
     * @return  the customization user ID
     * @see #setCustomizationUserID
     */
    public String getCustomizationUserID() {
        return customizationUserID;
    }

    /**
     * Returns the ID of the user that created the scheduled subscription.
     * @return the ID of the user that created the scheduled subscription.
     */
    public String getSenderID() {
        return senderID;
    }

    void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    /**
     * Returns the email ID of the user that created the scheduled subscription.
     * @return the email ID of the user that created the scheduled subscription.
     */
    public String getDeliveryAddress() {
        return this.deliveryAddress;
    }

    void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

}
