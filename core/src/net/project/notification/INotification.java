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


/**
 * A notification is something that may be posted to a delivery address.
 */
public interface INotification {

    public static final int NOTIFICATION_MESSAGING_EXCEPTION = -1;
    public static final int NOTIFICATION_SEND_FAILED_EXCEPTION = -2;
    
    // notification content types
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_HTML = "text/html";
    
    public String getNotificationID();

    public void setFromAddress(String fromAddress);

    public String getFromAddress();

    public void setNotificationXML(String xml);

    public String getNotificationXML();

    public void setXSLStylesheetPath(String path);

    public String getXSLStylesheetPath();

    public void setDeliveryTypeID(String deliveryTypeID);

    public String getDeliveryTypeID();

    public void setCustomizationUserID(String customizationUserID);

    public String getCustomizationUserID();

    public void setDeliveryAddress(String deliveryAddress);

    public String getDeliveryAddress();

    public void post() throws NotificationException;

    public void clear();

    public boolean isPosted();

    public String getSenderID();

    public void setSenderID(String senderID);

    /**
     * Attaches an email attachment to this notification.
     * @param attachment the attachment to attach
     */
    public void attach(net.project.notification.email.IEmailAttachment attachment);

    /**
     * Returns the attachments for this notification.
     * @return the list of attachments
     */
    public java.util.List getAttachments();

    public String getContentType();
    
    public void setContentType(String contentType);

} // end interface
