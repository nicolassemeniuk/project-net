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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import oracle.jdbc.OracleTypes;

/**
 * Provides a map where each key is a String email address and each value
 * is of type {@link SingleRecipientScheduledSubscriptionCollection} which is a collection of {@link ScheduledSubscription}s
 * destined to a common address.
 * <p>
 * The {@link #load} method loads all ready scheduled subscription entries
 * and builds the map.
 * </p>
 *
 * @author Unascribed
 * @since Version 2
 */
class RecipientScheduledSubscriptionMap extends HashMap implements INotificationDB {

    /**
     * Loads all ready scheduled subscriptions and adds to this map in multiple
     * positions.
     * The subscription will be added for each address in the subscription.
     * @throws PersistenceException if there is a problem loading the
     * ready scheduled subscriptions
     */
    public void load() throws PersistenceException {

        DBBean db = new DBBean();

        try {

            db.prepareCall(PREPARED_CALL_GET_READY_SUBSCRIPTIONS);
            db.cstmt.registerOutParameter(1, OracleTypes.CURSOR);

            db.executeCallable();

            db.result = (ResultSet) db.cstmt.getObject(1);

            while (db.result.next()) {
                ScheduledSubscription ss = new ScheduledSubscription();

                ss.objectID = db.result.getString(SCHEDULED_SUBSCRIPTION_ID);
                ss.name = db.result.getString(NAME);
                ss.description = db.result.getString(DESCRIPTION);
                ss.notificationTypeID = db.result.getString(NOTIFICATION_TYPE_ID);
                ss.notificationTypeName = db.result.getString("nt_name");
                ss.notificationTypeDescription = db.result.getString("nt_description");
                ss.notificationTypeDefaultMessage = db.result.getString("nt_default_message");
                ss.customMessage = ClobHelper.read(db.result.getClob("custom_message_clob"));
                ss.targetObjectID = db.result.getString(TARGET_OBJECT_ID);
                ss.subscriberBatchID = db.result.getString(SUBSCRIBER_BATCH_ID);
                ss.targetObjectType = db.result.getString(TARGET_OBJECT_TYPE);
                ss.targetObjectXML = NotificationClob.getClobData(db.result.getString(CLOB_ID));
                ss.initiatorID = db.result.getString(INITIATOR_ID);
                ss.eventTime = db.result.getString(EVENT_TIME);
                ss.eventType = db.result.getString(EVENT_TYPE);
                ss.setSpaceID(db.result.getString("space_id"));
                ss.setSpaceName(db.result.getString("space_name"));
                // note, the sender ID is actually the "created_by_id" for the scheduled subscription
                ss.setSenderID(db.result.getString("sender_id"));

                ss.loadSubscribers(db.result.getString(SUBSCRIBER_BATCH_ID));

                addEntry(ss);
            }

        } catch (SQLException sqle) {
            throw new PersistenceException("Error loading scheduled subscriptions: " + sqle, sqle);

        } finally {
            db.release();
        }

    }


    /* -------------------------------  Implementing util methods  ------------------------------- */

    /**
     * Returns an unmodifiable collection of unique recipients.
     * This is the key set of this map.
     *
     * @return a collection where each element is a String email address
     */
    public Collection getRecipientList() {
        return Collections.unmodifiableCollection(keySet());
    }

    /**
     * Returns a single SingleRecipientScheduledSubscriptionCollection for a given recipient address.
     * An <code>SingleRecipientScheduledSubscriptionCollection</code> is itself a collection of <code>ScheduledSubscription</code>s
     * for a single recipient.
     *
     * @param key the recipienet address for which to get the scheduled subscriptions
     * @return the collection of scheduled subscriptions for the recipient address
     */
    public SingleRecipientScheduledSubscriptionCollection getEntry(String key) {
        return ((SingleRecipientScheduledSubscriptionCollection) this.get(key));
    }

    /**
     * Adds a scheduled subscription to this map.
     * The scheduled subscription is added potentially more than once;
     * A scheduled subscription has many addresses.  This map maintains a lookup
     * from address to a collection of scheduled subscriptions destined to
     * the same address.  Thus, the specified scheduled subscription will be
     * added to every value (a value being of type <code>SingleRecipientScheduledSubscriptionCollection</code>) that
     * is referenced by each address in the scheduled subscription.
     *
     * @param scheduledSubscription the scheduled subscription to add to this map.
     */
    private void addEntry(ScheduledSubscription scheduledSubscription) {

        SingleRecipientScheduledSubscriptionCollection entries = null;
        Iterator addresses = scheduledSubscription.getConsolidatedDeliveryAddressList().iterator();
        Address address = null;
        String key = null;

        while (addresses.hasNext()) {
            address = (Address) addresses.next();
            key = address.getAddress();
            //key = scheduledSubscription.objectID;

            if (this.containsKey(key)) {
                entries = getEntry(key);

            } else {

                entries = new SingleRecipientScheduledSubscriptionCollection();
                entries.setDeliveryTypeID(address.getDeliveryTypeID());
                entries.setCustomizationUserID(address.getCustomizationUserID());
                entries.setSenderID(scheduledSubscription.getSenderID());
                entries.setDeliveryAddress(address.getAddress());

            }

            entries.add(scheduledSubscription);
            this.put(key, entries);

        }
    }

}
