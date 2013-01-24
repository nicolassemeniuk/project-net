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

import net.project.base.property.PropertyException;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.security.User;

import org.apache.log4j.Logger;


/**
 * The Postman delivers outstanding notifications.
 * <p>
 * This class is used in two ways.  When an Immediate Notification is posted
 * by {@link ImmediateNotification#post} it asks the Postman to deliver it
 * immediately by calling {@link #deliver}. <br>
 * Alternatively, all outstanding notifications may be delivered by calling
 * {@link #deliverAll}.
 * </p>
 *
 * @author Unascribed
 * @since Version 2
 */
public class Postman implements INotificationDB {

    /**
     * The default notification stylesheet to use, currently <code>/notification/xsl/notification-delivery.xsl</code>.
     */
    public static final String DEFAULT_NOTIFICATION_STYLESHEET = "/notification/xsl/notification-delivery.xsl";

    /**
     * Delivers all notifications that require delivery.
     *
     * @throws NotificationException if there is a problem loading the
     * notifications to be delivered; Note: No error occurs if there is
     * a problem delivering a notification
     */
    public static void deliverAll() throws NotificationException {
        Postman postman = new Postman();
        postman.deliverScheduledNotifications();

    }
    /**
     * Delivers the specified notification switching user context
     * if the notification userID is different from the session user's ID.
     * <p>
     * The order of priority for user context switching is:
     * <br> the recipient's context -> the sender's context -> the system default context
     * <p>
     * This means that if a notification is being sent to the logged-in user,
     * it uses the current configuration and language and ignores the logged-in
     * user's preferred language and configuration.
     * </p>
     *
     * @param notification the notification to deliver
     * @throws DeliveryException if there is a problem delivering the notification
     */
    public static void deliver(INotification notification) throws DeliveryException {

        // Save the current user
        // Note:  The current user may be NULL since notifications are sent
        // prior to logging in
        User customizationUser;
        User currentUser = SessionManager.getUser();
        User sender;
        String customizationUserLastBrandID = null;
        String customizationUserLanguage = null;
        String senderLastBrandID = null;
        String senderLanguage = null;
        // BFD-2004
        // We surround ALL code in this method with try ... finally
        // to ensure we always switch the user property context back
        try {
 
            // first load the sender from the notification
            sender = new User(notification.getSenderID());
        	Logger.getLogger(Postman.class).debug("Postman: attempting to deliver email to " + sender.getEmail());

            // the customization user is used to define which configuration is loaded in order to send the
            // notification using the appropriate configuration for the user.
            // The customization user may be the current user.
            customizationUser = getCustomizationUser(notification.getCustomizationUserID(), sender, currentUser);

            // there is a chance the sender or customization user could be null, we test for that here and assign values for the
            // last brand and language (which may be null -- but that's ok)
            if (customizationUser != null) {

                customizationUserLastBrandID = customizationUser.getLastUsedBrandID();
                customizationUserLanguage = customizationUser.getLanguageCode();
            }

            if (sender != null) {

                senderLastBrandID = sender.getLastUsedBrandID();
                senderLanguage = sender.getLanguageCode();
            }

            try {

                // note, this method will only switch contexts if the new brand is different from the current loaded brand
                PropertyProvider.switchContext(customizationUserLastBrandID, customizationUserLanguage, senderLastBrandID, senderLanguage);

            } catch (PropertyException e) {
                // Unable to switch to user's language, but we'll try to send anyway with the current loaded brand
                Logger.getLogger(Postman.class).error("Postman.deliver could not switch brand context: " + e);
            }

            // Now deliver the deliverable
            IDeliverable deliverable = getDeliverableForNotification(notification);
            deliverable.deliver();

        } catch (Exception e) {
            // Unable to switch to user's language, but we'll try to send anyway with the current loaded brand
            Logger.getLogger(Postman.class).error("Postman.deliver could not customise user and switch brand context: " + e);
        }
        finally {

            // switch brand context back to the current user's context
            try {
				if(currentUser != null)
	                PropertyProvider.switchContext(currentUser.getLastUsedBrandID(), currentUser.getLanguageCode(), PropertyProvider.get("prm.global.brand.defaultbrandid"), PropertyProvider.get("prm.global.brand.defaultlanguagecode"));
            } catch (PropertyException e) {
                //Unable to switch back to user's language
                Logger.getLogger(Postman.class).error("Postman.deliver could not switch back to user language: " + e);
            }
        }
    }

    /**
     * Returns a loaded user based on the specified Customization User ID.
     * <p>
     * The Customization user will be (in this order), the specified user -> the sender -> the current user.
     * <p> If the specified customization user is null, or the same as the current session user
     * the session user is returned.
     * @param customizationUserID the ID of the user to load for customizing
     * the deliverly of the notification
     * @param sender the sender of the notificaiton
     * @param currentUser the current in-session user.
     * @return loaded user to be used to load the appropriate configuration for delivery of the notification.  This method
     * may return null if the customizationUserID, sender and current user are all null or invalid
     */
    private static User getCustomizationUser(String customizationUserID, User sender, User currentUser) {

        User customizationUser = null;

        // if the current user is null, set the customization user to the specified user, or
        // if that user is not available, set the customization user to the sender.
        if (currentUser == null || currentUser.getID() == null) {

            if (customizationUserID != null) {
                customizationUser = new User (customizationUserID);
            }

            // if the customizationUser was not sucessfully loaded based on the specified customizationUserID
            // Try setting the user to the sender.
            if (customizationUser == null || !customizationUser.isLoaded()) {
                customizationUser = sender;
            }

        } else {

            if (customizationUserID != null) {

                // if the customization user id is the same as the current user's id, we'll just go with the current user.
                if (customizationUserID.equals(currentUser.getID())) {
                    customizationUser = currentUser;

                } else {
                    // otherwise, try to load the customization user.
                    customizationUser = new User (customizationUserID);
                }

                // if the customization user isn't loaded based on the specified id, go with the sender
                if (customizationUser == null || !customizationUser.isLoaded()) {
                    customizationUser = sender;
                }

            } else if (sender != null) {

                // if the specified customizaton user id was null, try to load based on the sender's ID
                    customizationUser = sender;
            }

            // Worst case, if neither the customization ID or the sender is a valid user, set customization user to teh current user.
            if (customizationUser == null || !customizationUser.isLoaded()) {
                customizationUser = currentUser;
            }

        }

        return customizationUser;
    }

    /**
     * Returns a populated Deliverable based on the specified notification.
     * The actual type of deliverable depends on the {@link Notification#getDeliveryTypeID}.
     * @param notification the notification from which to create the deliverable
     * @return the deliverable
     * @throws DeliveryException if the notification's delivery type is not supported.
     */
    private static IDeliverable getDeliverableForNotification(INotification notification) throws DeliveryException {
        // Construct a new deliverable based on the notification delivery type
        IDeliverable deliverable = createDeliverable(notification.getDeliveryTypeID());

        // Populate the deliverable from the notification's properties
        deliverable.setDeliveryAddress(notification.getDeliveryAddress());
        deliverable.setDeliveryTypeID(notification.getDeliveryTypeID());
        deliverable.setNotificationXML(notification.getNotificationXML());
        deliverable.setNotificationID(notification.getNotificationID());
        deliverable.setContentType(notification.getContentType());
        
        String fromAddress;

        boolean sendFromDefaultUser = PropertyProvider.getBoolean("prm.notification.sender.sendfromdefaultuser.flag", false);

        if (sendFromDefaultUser) {
            fromAddress = PropertyProvider.get("prm.global.default.email.fromaddress");
        } else if (notification.getFromAddress() != null) {
            fromAddress = notification.getFromAddress();
        } else {
            User sender = new User (notification.getSenderID());
            fromAddress = sender.getEmail();
        }
        deliverable.setFromAddress(fromAddress);

        if (notification.getXSLStylesheetPath() != null)
            deliverable.setMessageStylesheet(notification.getXSLStylesheetPath());
        else
            deliverable.setMessageStylesheet(DEFAULT_NOTIFICATION_STYLESHEET);

        // Add notification attachments to deliverable
        Iterator it = notification.getAttachments().iterator();
        while (it.hasNext()) {
            deliverable.attach((net.project.notification.email.IEmailAttachment) it.next());
        }

        return deliverable;
    }


    /**
     * Creates an empty Deliverable based on the specified delivery type.
     * @param deliveryTypeID the id of the delivery type specifiying what type
     * of deliverable to create.
     * @return an empty deliverable
     * @throws DeliveryException if the delivery type id is not supported
     */
    private static IDeliverable createDeliverable(String deliveryTypeID) throws DeliveryException {
        IDeliverable deliverable;

        if (deliveryTypeID.equals(EMAIL_DELIVERABLE)) {
            deliverable = new EmailDeliverable();
        } else {
            throw new DeliveryException("Postman.createDeliverable: notification has an invalid delivery type");
        }

        return deliverable;
    }


    /**
     * The collection of notifications to deliver.
     */
    private NotificationCollection notificationCollection = null;

    /**
     * The last userID that was used for customization the delivered notification.
     * We use this to avoid reloading a user if the user remains the same.
     * This may be infrequent.
     */

    /**
     * Creates a new, empty Postman.
     */
    private Postman() {
        // Do nothing
    }

    /**
     * Delivers all notifications that require delivery.
     * @throws NotificationException if there is a problem loading the
     * notifications to be delivered; Note: No error occurs if there is
     * a problem delivering a notification
     */
    private void deliverScheduledNotifications() throws NotificationException {

        try {
            loadNotificationCollection();

            for (Iterator it = notificationCollection.iterator(); it.hasNext(); ) {
                INotification nextNotification = (INotification) it.next();

                try {

                    deliver(nextNotification);

                } catch (DeliveryException e) {
                    // Silently log the exception and send the next notification
                	Logger.getLogger(Postman.class).error("Postman.sendDeliverables() threw an NotificationException: " + e);
                }
            }

        } catch (PersistenceException e) {
            throw new NotificationException("Error loading notifications for delivery: " + e, e);
        }
    }

    /**
     * Loads all notifications that require delivery.
     * @throws PersistenceException if there is a problem loading
     */
    private void loadNotificationCollection() throws PersistenceException {
        this.notificationCollection = new NotificationCollection();
        this.notificationCollection.load();
    }

}