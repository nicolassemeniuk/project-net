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

import java.sql.SQLException;
import java.util.Iterator;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;
import net.project.scheduler.SchedulerSetupServlet;

import org.apache.log4j.Logger;


/**
 * This class is responsible for checking for scheduled notifications who's
 * time has come to be sent.  
 * happened into notifications that will be sent by the Postman class.
 * <p>
 * It processes all ready subscriptions and groups them by unique recipient
 * address.  It then posts a single notification for each unique recipient
 * so that all outstanding scheduled subscriptions for a recipient are batched
 * in a single notification delivery.
 * </p>
 * <p>
 * It also performs some cleanup activity:  It deletes CLOB data that pertains
 * to the scheduled subscriptions just processed where the CLOB data is not
 * referenced by other non-batched scheduled subscriptions.
 * It then deletes the scheduled subscriptions just processed.
 * </p>
 *
 * @author Phil Dixon
 * @since Version 2
 */
public class NotificationScheduler implements INotificationDB {

    //
    // Static members
    //

    private static final String DELIVERY_INFO_OPEN = "<delivery_info>";
    private static final String NOTIFICATION_DELIVERABLE_OPEN = "<notification_deliverable>";
    private static final String NOTIFICATION_COLLECTION_OPEN = "<notification_collection>";
    private static final String NOTIFICATION_OPEN = "<notification>";

    private static final String DELIVERY_INFO_CLOSE = "</delivery_info>";
    private static final String NOTIFICATION_DELIVERABLE_CLOSE = "</notification_deliverable>";
    private static final String NOTIFICATION_COLLECTION_CLOSE = "</notification_collection>";
    private static final String NOTIFICATION_CLOSE = "</notification>";

    //
    // Instance members
    //

    private RecipientScheduledSubscriptionMap ssCollection = null;

    /* -------------------------------  Constructors  ------------------------------- */

    public NotificationScheduler() {
        // Do nothing
    }

    /* -------------------------------  Implementing main schedule methods  ------------------------------- */

    /**
     * Convert any scheduled subscriptions that have already occurred and convert
     * them into notifications so they will be sent by the Postman class.  A bit
     * more depth - when you subscribe for an event and it occurs, one of two
     * things can occur.  The first is that you have chosen to have the messages
     * sent immediately.  An entry will be made in pn_notification and it will be
     * sent.  The other is that you decide to have messages sent at some time
     * interval.  In this case, an entry is made in pn_scheduled_subscription when
     * the event occurs.
     *
     * @exception NotificationException if an error occurs
     */
    public void schedule() throws NotificationException {

        try {
            loadScheduledSubscriptions();
            createNotifications();

        } catch (PersistenceException e) {
            throw new NotificationException("Error loading scheduled subscriptions: " + e, e);
        }

        // Log a check by the scheduler for status purposes
        NotificationSchedulerStatus.logCheck(NotificationSchedulerStatus.DEFAULT_SCHEDULER_ID);
    }

    /**
     * Find any events that have already occurred that have not been batched.
     * @throws PersistenceException if there is a problem loading
     * the scheduled subscription collection
     */
    private void loadScheduledSubscriptions() throws PersistenceException {
        this.ssCollection = new RecipientScheduledSubscriptionMap();
        this.ssCollection.load();
    }

    /**
     * Processes the loaded scheduled subscription map creating a notification
     * for each recipient (the key of the map) based on all the scheduled
     * subscriptions for that recipient.
     *
     * @throws NotificationException if an error occurs
     */
    private void createNotifications() throws NotificationException {
        SingleRecipientScheduledSubscriptionCollection subscriptions;

        //Iterate through the recipients, create notification for each, and
        //erase the schedule subscription.
        for (Iterator it = this.ssCollection.getRecipientList().iterator(); it.hasNext();) {
            // get next email address
            String nextRecipient = (String) it.next();
            Logger.getLogger(NotificationScheduler.class).info("Creating notification for delivery to: " + nextRecipient);
            subscriptions = this.ssCollection.getEntry(nextRecipient);
            createNotificationForRecipient(nextRecipient, subscriptions);
            //Delete recipient notification
            deleteNotificationForRecipient(subscriptions);
        }
    }

    /**
     * Creates a single notification for the specified recipient.
     * The notification contains all the information for all the scheduled subscriptions
     * in the specified subscriptions collection.
     *
     * @param subscriptions a <code>SingleRecipientScheduledSubscriptionCollection</code> value which contains the
     * delivery type and all a arraylist of information to create the delivery
     * information.
     * @exception NotificationException if an error occurs
     */
    private void createNotificationForRecipient(String recipientAddress, SingleRecipientScheduledSubscriptionCollection subscriptions) throws NotificationException {
        Notification notification = new Notification();

        //Figure out where to deliver this notification to (and how)
        //notification.setDeliveryAddress(recipientAddress);
        notification.setDeliveryAddress(subscriptions.getDeliveryAddress());
        notification.setDeliveryTypeID(subscriptions.getDeliveryTypeID());
        notification.setCustomizationUserID(subscriptions.getCustomizationUserID());
        notification.setSenderID(subscriptions.getSenderID());

        //Create the clob that contain the information that we are going to
        //deliver on subscriber.
        //notification.setNotificationXML(createNotificationXML(recipientAddress, subscriptions));
        notification.setNotificationXML(createNotificationXML(subscriptions.getDeliveryAddress(), subscriptions));
        notification.post();
    }

    /**
     * Delete the entries in the PN_SCHEDULED_SUBSCRIPTION and the clobs related
     * to it in PN_NOTIFICATION_CLOB.
     *
     * @param subscriptions a <code>SingleRecipientScheduledSubscriptionCollection</code> value
     */
    private void deleteNotificationForRecipient(SingleRecipientScheduledSubscriptionCollection subscriptions) {
        Iterator iter = subscriptions.iterator();
        StringBuffer scheduledSubscriptionIDList = new StringBuffer();

        while (iter.hasNext()) {
            scheduledSubscriptionIDList.append(",").append(((ScheduledSubscription) iter.next()).objectID);
        }

        Logger.getLogger(NotificationScheduler.class).debug("ID List that I'm about to delete: " + scheduledSubscriptionIDList.toString());

        //Delete the initial comma in the CVS we created above
        if (scheduledSubscriptionIDList.length() > 0)
            scheduledSubscriptionIDList.deleteCharAt(0);

        DBBean db = new DBBean();

        try {
            //Get ready to run multiple database calls in one transaction
            db.setAutoCommit(false);

            //Delete the rows from scheduled subscription for the notification we just created
            StringBuffer deleteQuery = new StringBuffer();
            deleteQuery.append("delete from pn_scheduled_subscription where ");
            deleteQuery.append("  scheduled_subscription_id in (").append(scheduledSubscriptionIDList.toString()).append(") ");
            db.prepareStatement(deleteQuery.toString());
            db.executePrepared();

            db.commit();
            db.setAutoCommit(true);
        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException sqle2) {
            	Logger.getLogger(NotificationScheduler.class).error("Unable to rollback transaction for failed deletion of schedule subscription");
            }
            Logger.getLogger(NotificationScheduler.class).error("Unable to delete scheduled subscription from database: " + sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Constructs the XML from which the notification text will be derived.
     * This contains XML for all the scheduled subscriptions in the specified
     * collection.
     *
     * @param subscriptions a <code>SingleRecipientScheduledSubscriptionCollection</code> value which contains delivery information
     * for this notification.  This object is a subclass of ArrayList, which contains multiple
     * <code>ScheduledSubscription</code> objects.
     * @return a <code>String</code> value containing the XML representing this notification.
     */
    private String createNotificationXML(String recipientAddress, SingleRecipientScheduledSubscriptionCollection subscriptions) {
        StringBuffer xml = new StringBuffer();

        xml.append(net.project.xml.IXMLTags.XML_VERSION_STRING);
        xml.append(NOTIFICATION_DELIVERABLE_OPEN);

        appendDeliveryInfoXML(xml, recipientAddress, subscriptions);
        appendNotificationXML(xml, subscriptions);
        endXMLDocument(xml);

        return xml.toString();
    }

    /**
     * Create an XML snippet for all of the notifications in the subscriptions ArrayList.
     *
     * @param xml a <code>StringBuffer</code> value which already contains the start of an
     * XML document that represents a notification that we are constructing.
     * @param subscriptions a <code>SingleRecipientScheduledSubscriptionCollection</code> value which contains delivery information
     * for this notification.  This object is a subclass of ArrayList, which contains multiple
     * <code>ScheduledSubscription</code> objects.
     */
    private void appendNotificationXML(StringBuffer xml, SingleRecipientScheduledSubscriptionCollection subscriptions) {
        Iterator subs = subscriptions.iterator();

        xml.append(NOTIFICATION_COLLECTION_OPEN);

        while (subs.hasNext()) {
            xml.append(NOTIFICATION_OPEN);
            xml.append(((ScheduledSubscription) subs.next()).getXMLProperties());
            xml.append(NOTIFICATION_CLOSE);
        }

        xml.append(NOTIFICATION_COLLECTION_CLOSE);
    }


    /**
     * Append the closing tag to the XML document.
     *
     * @param xml a <code>StringBuffer</code> value
     */
    private void endXMLDocument(StringBuffer xml) {
        xml.append(NOTIFICATION_DELIVERABLE_CLOSE);
    }

    /**
     * Add information to the notification xml about the location to which we
     * are going to deliver this notification.
     *
     * @param xml a <code>StringBuffer</code> value containing the XML that has
     * already been created for this notification.  We are going to append the
     * delivery information to this.
     * @param subscriptions a <code>SingleRecipientScheduledSubscriptionCollection</code> value that we will use to
     * get the delivery type information.
     */
    private void appendDeliveryInfoXML(StringBuffer xml, String recipientAddress, SingleRecipientScheduledSubscriptionCollection subscriptions) {
        xml.append(DELIVERY_INFO_OPEN);

        xml.append("<recipient_name>").append(Person.getDisplayName(recipientAddress)).append("</recipient_name>");
        xml.append("<delivery_address>").append(recipientAddress).append("</delivery_address>");
        xml.append("<delivery_type_id>").append(subscriptions.getDeliveryTypeID()).append("</delivery_type_id>");

        xml.append(DELIVERY_INFO_CLOSE);
    }

}
