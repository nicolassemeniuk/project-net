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
|   $Revision: 20381 $
|       $Date: 2010-02-05 11:22:10 -0300 (vie, 05 feb 2010) $
|     $Author: umesha $
|
|
+----------------------------------------------------------------------*/
package net.project.notification;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import net.project.base.compatibility.Compatibility;
import net.project.base.compatibility.modern.LocalSessionProvider;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.scheduler.PostmanAgentJob;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import net.project.notification.Subscription;


/**
 * This class provides methods to manage Notifications.  This class is used to
 * create Notifications and send email notifications to users, etc.
 *
 * @author Bern McCarty (01/26/2000)
 */
public class NotificationManager {

    /**
     * temp linked list to store the events
     */
    private static LinkedList notificationEventLinkedList = new LinkedList();
    
    public static class AsynchronousNotificationJob implements StatefulJob  {

        public void execute(JobExecutionContext context) throws JobExecutionException {

        	try {
        		PropertyProvider.loadDefaultContext();
	            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
	            LinkedList<Event> jobEventLinkedList = (LinkedList<Event>) jobDataMap.get("event");
	            //empty the temp linked list and put it in the stateful linked list
	            synchronized (notificationEventLinkedList) {
	                jobEventLinkedList.addAll(notificationEventLinkedList);
	                notificationEventLinkedList.clear();
	            }
	            //subscribe the events and once they are done remove it from
	            //stateful linked list
	            Iterator<Event> iterator = jobEventLinkedList.iterator();
	            while(iterator.hasNext()) {
	                Event event = iterator.next();
	                ArrayList<Boolean> statusList = Subscription.getSubscriptionStatusListByObjectID(event.getTargetObjectID());
	                Iterator<Boolean> statusListiterator = statusList.iterator();
	                
	                while(statusListiterator.hasNext()) {
	                	Boolean status = statusListiterator.next();
	                	if(status){
	                		NotificationManager.createScheduledSubscription(event);	                	
			            }
		            }
	                iterator.remove();
	            }
        	}catch(Exception e){
        		Logger.getLogger(AsynchronousNotificationJob.class).debug("Error executing AsynchronousNotificationJob", e);
        	}
        }
    }


    /**
     * Returns a loaded subscription for the specified ID.
     *
     * @param subscriptionID the ID of the subscription to get
     * @return the loaded subscription
     * @throws NotificationException if there is a problem loading the subscription
     */
    public static Subscription getSubscription(String subscriptionID) throws NotificationException {

        Subscription sub;

        try {
            sub = new Subscription();

            sub.setID(subscriptionID);
            sub.load();
        } catch (PersistenceException pe) {
            throw new NotificationException("NotificationMangager.getSubscription() failed: " + pe);
        }

        return sub;
    }

    /**
     * Delivers the specified notification immediately.
     * This method posts the notification if necessary then delivers it.
     *
     * @param notification the notification to deliver
     * @throws DeliveryException if there is a problem delivering
     * @throws NotificationException if there is a problem posting
     */
    public static void deliverImmediately(INotification notification) throws DeliveryException, NotificationException {
        if (!notification.isPosted()) {
            notification.post();
        }

        Postman.deliver(notification);
    }

    /**
     * Generates and  then persists the change in the Notification System that
     * should occur when a Subscription's target object is changed (viewed,
     * altered, moved etc) in a way that the Subscriber has asked to be
     * notified about.
     * <p>
     * First records the XML representation of the target object, then creates a
     * scheduled subscription for every subscription which is listening for this
     * change on this object.
     * </p>
     * @param notificationEvent the event to notify about
     * @return true if the scheduled subscription was created successfully,
     * false if not
     */
    public static void notify(Event notificationEvent) {
        // just add the events in the temp linked list
        //sachin: note*
        //after every 15 seconds approx these events are emptied
        //from this temp list and copied into a stateful (persisted) list
        //if between that time app server shuts down then we loose the danger
        //of loosing the events created in these 15 seconds
        //other (better) way to handle this would be using JMS
        synchronized (notificationEventLinkedList) {
            notificationEventLinkedList.add(notificationEvent);            
        }
    }

    /**
     * When an Event that the User has subscribed to occurs, the User's
     * Subscription gets scheduled for eventual delivery (a notification is
     * scheduled). 	<p>
     * This is informally referred to as "creating a scheduled subscription" even
     * though there is no java class named ScheduledSubscription and no such
     * Java object is created.<p>
     * In the Notification system,   certain entries are made in db tables- namely
     * pn_scheduled_subscription and pn_notification_clob.<p>
     * This method causes those entries to be made and a "scheduled subscription"
     * to be created.
     *
     * @param notificationEvent a <code>INotificationEvent</code> value which we
     * will use to fetch the XML for making the pn_notification_clob row entry and
     * for creating an entry in pn_scheduled_subscription.
     * @return a <code>boolean</code> value indicating whether or not creating the
     * scheduled subscription was successful.
     */
    private static void createScheduledSubscription(Event notificationEvent) {
        DBBean db = new DBBean();
        try {
            //First, we need to make this all occur in a single transaction.
            db.setAutoCommit(false);
            db.openConnection();

            //Create the clob for this scheduled subscription - this will be used
            //to render the notification email.
            // Note:
            // This method _commits_ the writing of the clob; so, despite the
            // fact that we set autocommit to false, the created clob is
            // visible in other transactions
            String clobID = NotificationClob.createClob(notificationEvent.getTargetObjectXML(), db);

            //Create the scheduled subscription
            // Note: This method does NOT commit
            int subscriptionID = createScheduledSubscriptionEntry(notificationEvent, clobID, db);
            //The scheduled subscription should be created now, commit the transaction
            db.commit();
            //We have success unless there was an exception thrown
            Logger.getLogger(NotificationManager.class).info("NotificationManger.notify sucessfuly scheduled the subscription");

        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException sqle2) {
            	Logger.getLogger(NotificationManager.class).error("Unable to rollback statement in exception: " + sqle2);
            }
            Logger.getLogger(NotificationManager.class).error("NotificationManger.notify couldn't create scheduled subscription.");
            Logger.getLogger(NotificationManager.class).error("NotificationManger.notify threw an SQLException: " + sqle);
        } catch (PersistenceException pe) {
            try {
                db.rollback();
            } catch (SQLException sqle2) {
            	Logger.getLogger(NotificationManager.class).error("Unable to rollback statement in exception: " + sqle2);
            }
            Logger.getLogger(NotificationManager.class).error("NotificationManger.notify couldn't create scheduled subscription.");
            Logger.getLogger(NotificationManager.class).debug("NotificationManger.notify threw an PersistenceException: " + pe);
        } finally {
            db.release();
        }
    }

    /**
     * Creates an entry into pn_scheduled_subscription inside of an existing transaction.
     *
     * @param notificationEvent a <code>INotificationEvent</code> value
     * @param clobID a <code>String</code> value containing the clobID that will
     * be inserted into the pn_scheduled_subscription table.  The clob of the
     * row specified by this id contains the XML that will be used to
     * generate email notifications.
     * @param db a <code>DBBean</code> value already in transaction.  It will
     * not be committed or released in this method.
     * @return a <code>int</code> database id of the scheduled subscription.  Returns 0 if a scheduled subscription was not created.
     */
    private static int createScheduledSubscriptionEntry(INotificationEvent notificationEvent, String clobID, DBBean db) throws PersistenceException {
        int scheduledSubscriptionID = 0;
        try {
            db.prepareCall(INotificationDB.PREPARED_CALL_CREATE_SCHEDULED_SUBSCRIPTION);
            db.cstmt.setString(1, notificationEvent.getEventType());
            db.cstmt.setString(2, notificationEvent.getTargetObjectID());
            db.cstmt.setString(3, notificationEvent.getTargetObjectType());
            db.cstmt.setTimestamp(4, new Timestamp(notificationEvent.getEventTime().getTime()));
            db.cstmt.setString(5, notificationEvent.getDescription());
            db.cstmt.setString(6, clobID);
            db.cstmt.setString(7, notificationEvent.getInitiatorID());
            db.cstmt.setString(8, notificationEvent.getSpaceID());
            db.cstmt.registerOutParameter(9, java.sql.Types.INTEGER);

            db.executeCallable();
            //If the scheduled subscription doesn't have an id, it wasn't created
            scheduledSubscriptionID = db.cstmt.getInt(9);

        } catch (SQLException sqle) {
        	scheduledSubscriptionID = 0;
        	Logger.getLogger(NotificationManager.class).debug("NotificationManager.createScheduledSubscriptionEntry() threw an SQLException: " + sqle);
            throw new PersistenceException("Unable to create scheduled subscription entry.", sqle);
        }

        return scheduledSubscriptionID;
    }
}
