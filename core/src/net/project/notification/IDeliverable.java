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




public interface IDeliverable {

    public static final int DELIVERY_STATUS_SUCCESS = 0;
    public static final String EMAIL_DELIVERABLE = "100";
    public static final String IMMEDIATE_DELIVERY_INTERVAL = "100";


    /* -------------------------------  Implement Setters and Getters  ------------------------------- */


    public void setDeliveryAddress(String deliveryAddress);

    public String getDeliveryAddress();

    public void setFromAddress(String address);

    public String getFromAddress();

    public void setDeliveryTypeID(String deliveryTypeID);

    public void setNotificationID(String notificationID);

    public String getNotificationID();

    /**
     * Returns the message to be delivered.
     * @return the messaged, formatted for the appropriate delivery type
     * @throws NotificationException if there is a problem producing the
     * message
     */
    public String getMessage() throws NotificationException;

    public void setMessage(String message);

    public void setMessageStylesheet(String stylesheetPath);

    public void setNotificationXML(String xml);

    public void deliver() throws DeliveryException;

    public void setContentType(String contentType);
    public String getContentType();
    
    /**
     * Attaches an email attachment to this deliverable.
     * @param attachment the attachment to attach
     */
    public void attach(net.project.notification.email.IEmailAttachment attachment);

    /**
     * Returns the attachments for this deliverable.
     * @return the list of attachments
     */
    public java.util.List getAttachments();

} // end class
