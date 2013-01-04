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
|    $Revision: 18888 $
|    $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|    $Author: avinash $
|
+----------------------------------------------------------------------*/
package net.project.notification;

import java.sql.SQLException;
import java.util.ArrayList;

import net.project.base.PnetException;
import net.project.database.DBBean;
import net.project.database.DBExceptionFactory;
import net.project.util.Conversion;

import org.apache.log4j.Logger;

/**
 * The Notification class represents an object which may be delivered to
 * a person via any number of delivery mechanisms.
 * The mechanism of sending a notification is defined by the {@link Postman} class.
 * The delivery mechanisms is defined by the {@link Deliverable} class.
 */
public class Notification implements INotification, INotificationDB {
    private Logger logger = Logger.getLogger(Notification.class);
    private String notificationID = null;
    private String notificationXML = null;
    private String deliveryTypeID = null;
    private String deliveryAddress = null;
    private String fromAddress = null;
    private String xslStylesheetPath = null;
    private String customizationUserID = null;
    /** populated by the sender of the notification */
    private String senderID;

    /** The attachments */
    private ArrayList attachments = new ArrayList();

    /**
     * Indicates that this Notification will be sent immediately.
     * Implies it must NOT be picked up by the scheduler since that will be
     * unable to transform the included XML object.
     */
    private boolean isImmediate = false;

    private boolean isPosted = false;

    // default content type set to plain text (all existing notifications are text/plain till 8.4.0)
    private String contentType = TEXT_PLAIN;
    /* -------------------------------  Constructors  ------------------------------- */

    /**
     * Creates a new, empty Notification.
     */
    public Notification() {
        // do nothing
    }

    /**
     * Creates a new Notification indicating it will be delivered immediately.
     * This ensures that the Notification will be ignored by non-immediate
     * Notification queues processors.
     * @param isImmediate true if the Notificaiton will be delivered immediately;
     * false otherwise
     */
    Notification(boolean isImmediate) {
        setImmediate(isImmediate);
    }

    /* -------------------------------  Implementing Main  ------------------------------- */

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getNotificationID() {
        return this.notificationID;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getFromAddress() {
        return this.fromAddress;
    }

    public void setNotificationXML(String xml) {
        this.notificationXML = xml;
    }

    public String getNotificationXML() {
        return this.notificationXML;
    }

    public void setXSLStylesheetPath(String path) {
        this.xslStylesheetPath = path;
    }

    public String getXSLStylesheetPath() {
        return this.xslStylesheetPath;
    }

    public void setDeliveryTypeID(String deliveryTypeID) {
        this.deliveryTypeID = deliveryTypeID;
    }

    public String getDeliveryTypeID() {
        return this.deliveryTypeID;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryAddress() {
        return this.deliveryAddress;
    }

    public void setCustomizationUserID(String customizationUserID) {
        this.customizationUserID = customizationUserID;
    }

    public String getCustomizationUserID() {
        return this.customizationUserID;
    }

    /**
     * Returns the user ID of the notification sender.
     * @return the user ID of the notification sender.
     */
    public String getSenderID() {
        return this.senderID;
    }

    /**
     * Sets the ID of the user sending the notification.
     * @param senderID
     */
    public void setSenderID (String senderID) {
        this.senderID = senderID;
    }

    /**
     * Posts the Notification.  This creates the notification such that it may
     * be picked up for delivery by any delivery mechanism.
     *
     * @throws NotificationException if there is a problem posting the notification
     */
    public void post() throws NotificationException {

        String clobID = null;
        int errorCode = -1;
        int index = 0;

        DBBean db = new DBBean();
        try {
            int notificationIDIndex = 0;
            int errorCodeIndex = 0;

            //Step 0: make sure that both of these statements run in a single transaction
            db.setAutoCommit(false);
            db.openConnection();

            // First create clob
            clobID = NotificationClob.createClob(this.notificationXML, db);

            // now create actual notification
            db.prepareCall("begin  NOTIFICATION.CREATE_NOTIFICATION (?,?,?,?,?,?,?,?,?); end;");

            db.cstmt.setString(++index, this.deliveryAddress);
            db.cstmt.setString(++index, clobID);
            db.cstmt.setString(++index, this.deliveryTypeID);
            db.cstmt.setString(++index, this.fromAddress);
            db.cstmt.setString(++index, this.customizationUserID);
            db.cstmt.setString(++index, getSenderID());
            db.cstmt.setInt(++index, Conversion.booleanToInt(isImmediate()));
            db.cstmt.registerOutParameter((notificationIDIndex = ++index), java.sql.Types.VARCHAR);
            db.cstmt.registerOutParameter((errorCodeIndex = ++index), java.sql.Types.INTEGER);

            db.executeCallable();

            this.notificationID = db.cstmt.getString(notificationIDIndex);
            errorCode = db.cstmt.getInt(errorCodeIndex);

            // Handle (throw) any database exceptions
            DBExceptionFactory.getException("Notification.post()", errorCode);

            isPosted = true;

            //Now that we have finished with all the statements, commit the connection
            db.commit();
            db.setAutoCommit(true);
        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException sqle2) {
                logger.debug("Unable to rollback statement in exception: " + sqle2, sqle2);
            }
            logger.debug("Unexpected error in notification.post()", sqle);
            throw new NotificationException("Notification.post() threw a SQLException: " + sqle, sqle);
        } catch (PnetException pe) {
            try {
                db.rollback();
            } catch (SQLException sqle3) {
                logger.debug("Unable to rollback statement in exception", sqle3);
            }
            logger.debug("Notification.post() threw a pnet exception", pe);
            throw new NotificationException("Notification.post() threw a PnetException: " + pe, pe);
        } finally {
            db.release();
        }
    }


    public boolean isPosted() {
        return isPosted;
    }

    public void clear() {
        notificationID = null;
        notificationXML = null;
        deliveryTypeID = null;
        deliveryAddress = null;
        this.attachments.clear();
        this.isImmediate = false;
    }

    /**
     * Attaches an email attachment to this notification. <br>
     * <b>Note: </b>Attachments are not persisted.  Therefore
     * they will be lost if a Notification is not delivered immediately
     * after posting.
     * @param attachment the attachment to attach
     */
    public void attach(net.project.notification.email.IEmailAttachment attachment) {
        this.attachments.add(attachment);
    }

    /**
     * Returns the attachments for this notification.
     * @return the list of attachments
     */
    public java.util.List getAttachments() {
        return this.attachments;
    }

    /**
     * Sets the notification to immediate delivery.
     * This is to prevent the notification being picked up by the scheduler
     * also.
     */
    private void setImmediate(boolean isImmediate) {
        this.isImmediate = isImmediate;
    }

    /**
     * Indicates whether this is an Immediate Notification or not.
     * An immediate notification is delivered immediately after posting
     * @return true if this is an immediate notification; false otherwise
     */
    private boolean isImmediate() {
        return this.isImmediate;
    }

	public String getContentType() {
		return contentType;
	}
    
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
