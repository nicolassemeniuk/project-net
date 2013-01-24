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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
+----------------------------------------------------------------------*/
package net.project.notification;

import java.util.ArrayList;

import net.project.xml.XMLFormatter;

/**
 * Provides base functionality for a deliverable.
 *
 * @author Unascribed
 * @since Version 2
 */
public abstract class Deliverable implements IDeliverable {

    /* -------------------------------  Member Variables  ------------------------------- */

    private String deliveryAddress = null;
    private String notificationXML = null;
    private String notificationID = null;
    private String fromAddress = null;

    private boolean isCustomMessage = false;
    private String message = null;

    private String stylesheetPath = null;

    private String contentType = Notification.TEXT_PLAIN;
    
    /** Attachments for this deliverable */
    private final ArrayList attachments = new ArrayList();

    /* -------------------------------  Implement Setters and Getters  ------------------------------- */


    public void setFromAddress(String address) {
        this.fromAddress = address;
    }

    public String getFromAddress() {
        return this.fromAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryAddress() {
        return this.deliveryAddress;
    }

    public void setDeliveryTypeID(String deliveryTypeID) {
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getNotificationID() {
        return this.notificationID;
    }
    
    public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;		
	}

	/**
     * Returns the formatted message for this deliverable.
     * If {@link #setMessage} was called, the message will be exactly that.
     * Otherwise, the message will be based on the transformation of the XML
     * using the XSL stylesheet.
     *
     * @return the formatted message
     * @throws DeliveryException if there is a problem producing the
     * formatted message
     */
    public String getMessage() throws DeliveryException {
        return getFormattedMessage();
    }

    /**
     * Specifies the plain text message to use when producing the formatted
     * message.
     * Any stylesheet or XML is ignored when formatting the message.
     *
     * @param message the plain message
     */
    public void setMessage(String message) {
        this.isCustomMessage = true;
        this.message = message;
    }


    /**
     * Specifies the XSL stylesheet to use when formatting the message.
     *
     * @param stylesheetPath the XSL stylesheet path
     */
    public void setMessageStylesheet(String stylesheetPath) {
        this.stylesheetPath = stylesheetPath;
    }

    /**
     * Specifies the XML to use when formatting the message.
     *
     * @param xml the XML to use when formatting the message
     */
    public void setNotificationXML(String xml) {
        this.notificationXML = xml;
    }


    /**
     * Return the XML for the deliverable.
     * <p>
     * Currently, this is implemented to only return XML from the notification object itself, but there may be a need in the future
     * to augment this XML with Deliverable-specific XML.
     * @return XML representation of the deliverable.
     */
    public String getXML() {
        return this.notificationXML;
    }

    /**
     * Returns the formatted deliverable message.
     * If this deliverable is a custom message (that is, the message is not produced
     * from XML) then the message text is returned; otherwise the message
     * is formatted from XML.
     *
     * @return the formatted message
     * @throws DeliveryException if there was a problem producing the
     * formatted message, for example the message is produced from XML and
     * the XML was invalid
     */
    private String getFormattedMessage() throws DeliveryException {

        String formattedMessage;

        if (isCustomMessage) {
            formattedMessage = this.message;
        } else {
            formattedMessage = getFormattedXMLMessage();
        }

        return formattedMessage;
    }

    /**
     * Returns the deliverable message formatted based on the deliverable
     * XML and the current stylesheet.
     * <p>
     * If the current stylesheet is null, then the XML is returned untransformed.
     * </p>
     * @return the formatted message from XML
     * @throws DeliveryException if there was a problem producing the XML
     * message, for example the XML was invalid
     */
    private String getFormattedXMLMessage() throws DeliveryException {

        String formattedMessage;
        XMLFormatter xml;

        if (this.stylesheetPath == null)
            formattedMessage = getXML();

        else {
            xml = new XMLFormatter();
            xml.setStylesheet(this.stylesheetPath);

            try {
                formattedMessage = xml.getPresentation(getXML());
            } catch (IllegalStateException e) {
                // There was a problem transforming the XML
                throw new DeliveryException("Error producing formatted message from XML: " + e, e);
            }
        }

        return formattedMessage;
    }


    /**
     * Complete the delivery of this deliverable.
     * This method performs the act of delivering this deliverable to its
     * destination.  After calling successfully, the notification on which
     * this deliverable was based may be deleted since it will no longer be
     * required.
     *
     * @throws DeliveryException if there is a problem delivering
     */
    public abstract void deliver() throws DeliveryException;

    /**
     * Attaches an email attachment to this deliverable.
     * @param attachment the attachment to attach
     */
    public void attach(net.project.notification.email.IEmailAttachment attachment) {
        this.attachments.add(attachment);
    }

    /**
     * Returns the attachments for this deliverable.
     * @return the list of attachments
     */
    public java.util.List getAttachments() {
        return this.attachments;
    }

}
