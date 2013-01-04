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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
+-----------------------------------------------------------------------------*/
package net.project.notification;

import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * Implementation of the <code>IDeliverable</code> interface where the delivery
 * address is an email delivery address. The Deliverable is delivered as an email message.
 *
 * @author Unascribed
 * @since aardvark
 */
public class EmailDeliverable extends Deliverable {

    /**
     * Creates an empty EmailDeliverable.
     */
    public EmailDeliverable() {
        super();
    }


    /* -------------------------------  Message Delivery Methods  ------------------------------- */

    /**
     * Delivers this deliverable via an email message.
     * Currently the notification queue is always cleared so that a message
     * can never be attempted to be resent even on failure
     * @throws DeliveryException if there is a problem creating the
     * message or delivering the email; notification is removed from queue
     */
    public void deliver() throws DeliveryException {

        try {
            Email email = new Email();

            email.setFrom(getFinalFromAddress());
            email.setTo(getDeliveryAddress());

            String subject = (PropertyProvider.isDefined("prm.global.default.email.subject")) ? PropertyProvider.get("prm.global.default.email.subject") : PropertyProvider.get("prm.global.default.email.subject");

            email.setSubject(subject);
            email.setMessageBody(getMessage());
            email.setContentType(getContentType());
            // Add attachments to email
            Iterator it = this.getAttachments().iterator();
            while (it.hasNext()) {
                email.attach((net.project.notification.email.IEmailAttachment) it.next());
            }

            email.send();

        } catch (EmailException e) {
        	Logger.getLogger(EmailDeliverable.class).debug("EmailDeliverable.deliver: could not deliver message id: " + getNotificationID() + " :" + e);
            throw new DeliveryException("Error sending email", e);

        } finally {

            // Email may fail when creating it (getMessage() may throw an exception)
            // Email may fail when sending it
            // Email may successfully be sent

            // We Always attempt to delete from queue
            // * We must do this when the email succeeds to avoid duplicates
            // * We must do it when the email could not be constructed, to avoid
            //   repeated errors that block the queue
            // * Ideally in the case of a send fail we might try to resend if
            //   we knew why it failed.  Some reasons (such as an illegal email address)
            //   can never succeed
            //   At some point it must be removed to avoid a blocked queue

            try {
                NotificationClob.deleteClobsForNotification(getNotificationID());

            } catch (PersistenceException e) {
            	Logger.getLogger(EmailDeliverable.class).debug("EmailDeliverable.deliver: could not log message id: " + getNotificationID() + "  " + e);
                throw new DeliveryException("EmailDeliverable.deliver() could not deliver message id:" + e, e);
            }

        }

    }

    /**
     * Returns the final from address.
     * If a from address has been specified in the deliverable, that is used.
     * Otherwise the default from address is used.
     * @return the final from address
     */
    private String getFinalFromAddress() {
        String finalFromAddress;

        if (getFromAddress() != null && getFromAddress().length() > 0) {
            // If a from address has been specified, return that
            finalFromAddress = getFromAddress();

        } else {
            // Get the default email address
            finalFromAddress = PropertyProvider.get("prm.global.default.email.fromaddress");

        }

        return finalFromAddress;
    }

}
