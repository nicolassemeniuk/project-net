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
package net.project.license;

import net.project.base.DefaultDirectory;
import net.project.notification.ImmediateNotification;
import net.project.notification.NotificationException;
import net.project.persistence.IXMLPersistence;
import net.project.resource.Person;
import net.project.security.SessionManager;
import net.project.util.HTMLUtils;

/**
 * Provides the ability to send notification about certain license events,
 * such as them gaining responsibility for a license or them being invited to a
 * license.
 *
 * @author Vishwajeet Lohakarey
 * @since Gecko Update 3
 */
public class LicenseNotification extends ImmediateNotification {
    public static final String RESPONSIBLE_USER_NOTIFICATION_STYLE_SHEET = "/admin/license/xsl/responsible-user-notification.xsl";
    public static final String INVITE_USER_NOTIFICATION_STYLE_SHEET = "/admin/license/xsl/invite-user-notification.xsl";
    public static final String RESPONSIBLE_USER_ASSOCIATION_NOTIFICATION_STYLE_SHEET = "/admin/license/xsl/responsible-user-association-notification.xsl";
    public static final String DISSOCIATE_USER_NOTIFICATION_STYLE_SHEET = "/admin/license/xsl/dissociate-user-notification.xsl";
    public static final String ASSOCIATE_USER_NOTIFICATION_STYLE_SHEET = "/admin/license/xsl/associate-user-notification.xsl";

    String message = null;

    /**
     * Creates a new LicenseNotification.
     */
    public LicenseNotification() {
        super();
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    /**
     * Notifies user of a license's responsibility.
     * @param license the license to which the user is responsible for.
     * @param person the responsible user for the license.
     */
    public void notifyUserOfResponsibility(License license, Person person) {

        setDeliveryTypeID(ImmediateNotification.EMAIL_DELIVERABLE);
        setXSLStylesheetPath(RESPONSIBLE_USER_NOTIFICATION_STYLE_SHEET);
        setDeliveryAddress(person.getEmail());
        setCustomizationUserID(person.getID());

        StringBuffer xml = new StringBuffer();
        xml.append(IXMLPersistence.XML_VERSION);
        xml.append("<LicenseNotification>");
        xml.append("<ResponsibleUser>" + person.getDisplayName() + "</ResponsibleUser>");
        xml.append(license.getXMLBody());
        xml.append("<Message>" + HTMLUtils.escape(this.message) + "</Message>");
        xml.append("</LicenseNotification>");
        //System.out.println("LicenseNotification.java .... \n" + xml.toString());
        setNotificationXML(xml.toString());
        try {
            this.post();
        } catch (NotificationException ne) {
            // We don't want to fail the assigning of reponsibility just for notification failure.
            //System.out.println("LicenseNotification.java : NotifyUserOfResponsibility operation failed : Notification exception thrown " + ne.getMessage());
        }

    }

    /**
     * Notifies user inviting him/her to join a license.
     * @param license the license to which the user is to be invited.
     * @param emailID the invitee's email address.
     * @throws NotificationException if there is a problem sending out the notification.
     */
    public void inviteUserToAssociate(License license, String emailID)
            throws NotificationException {

        setDeliveryTypeID(ImmediateNotification.EMAIL_DELIVERABLE);
        setXSLStylesheetPath(INVITE_USER_NOTIFICATION_STYLE_SHEET);
        setDeliveryAddress(emailID);

        String userID = DefaultDirectory.getUserIDForEmail(emailID);
        if (userID != null) {
            setCustomizationUserID(userID);
        } else {
            setCustomizationUserID(SessionManager.getUser().getID());
        }

        StringBuffer xml = new StringBuffer();
        xml.append(IXMLPersistence.XML_VERSION);
        xml.append("<LicenseNotification>");
        xml.append(license.getXMLBody());
        xml.append("<Message>" + HTMLUtils.escape(this.message) + "</Message>");
        xml.append("</LicenseNotification>");
        setNotificationXML(xml.toString());
        this.post();
    }

    /**
     * Notifies the responsible user of a license about association of a new user.
     * @param license the license to which the user has been associated.
     * @param person the newly associated user.
     * @throws NotificationException if there is problem sending the notification
     */
    public void notifyResponsibleUserOfAssociation(License license, Person person)
            throws NotificationException {

        // Send this notification only if the person being associated is
        //different from the responsible person	for this license. This is to avoid too many
        // notifications being sent to a newly registering user.
        if (license.getResponsiblePerson().getID().equals(person.getID())) {
            return;
        }
        setDeliveryTypeID(ImmediateNotification.EMAIL_DELIVERABLE);
        setXSLStylesheetPath(RESPONSIBLE_USER_ASSOCIATION_NOTIFICATION_STYLE_SHEET);
        setDeliveryAddress(license.getResponsiblePerson().getEmail());
        setCustomizationUserID(license.getResponsiblePerson().getID());

        StringBuffer xml = new StringBuffer();
        xml.append(IXMLPersistence.XML_VERSION);
        xml.append("<LicenseNotification>");
        xml.append(person.getXMLBody());
        xml.append(license.getXMLBody());
        xml.append("<Message>" + HTMLUtils.escape(this.message) + "</Message>");
        xml.append("</LicenseNotification>");
        //System.out.println("LicenseNotification.java .... \n" + xml.toString());
        setNotificationXML(xml.toString());
        this.post();

    }

    /**
     * Notifies the user of dissociation from a license.
     * @param license the license from which the user has been dissociated.
     * @param person the dissociated user.
     */
    public void notifyUserOfDissociation(License license, Person person) {
        setDeliveryTypeID(ImmediateNotification.EMAIL_DELIVERABLE);
        setXSLStylesheetPath(DISSOCIATE_USER_NOTIFICATION_STYLE_SHEET);
        setDeliveryAddress(person.getEmail());
        if (DefaultDirectory.isUserRegisteredByID(person.getID())) {
            setCustomizationUserID(person.getID());
        } else {
            setCustomizationUserID(SessionManager.getUser().getID());
        }

        StringBuffer xml = new StringBuffer();
        xml.append(IXMLPersistence.XML_VERSION);
        xml.append("<LicenseNotification>");
        xml.append("<DissociatedPersonName>" + person.getDisplayName() + "</DissociatedPersonName>");
        xml.append(person.getXMLBody());
        xml.append(license.getXMLBody());
        xml.append("<Message>" + HTMLUtils.escape(this.message) + "</Message>");
        xml.append("</LicenseNotification>");
        //System.out.println("LicenseNotification.java .... \n" + xml.toString());
        setNotificationXML(xml.toString());
        try {
            this.post();
        } catch (NotificationException ne) {
            // We don't want to fail the dissociation just for notification failure.
            //System.out.println("LicenseNotification.java : NotifyUserOfDissociation operation failed : Notification exception thrown " + ne.getMessage());
        }
    }

    /**
     * Notifies the user of association to a license.
     * @param license the license to which the user has been associated.
     * @param person the associated user.
     */
    public void notifyUserOfAssociation(License license, Person person) {
        setDeliveryTypeID(ImmediateNotification.EMAIL_DELIVERABLE);
        setXSLStylesheetPath(ASSOCIATE_USER_NOTIFICATION_STYLE_SHEET);
        setDeliveryAddress(person.getEmail());
        if (DefaultDirectory.isUserRegisteredByID(person.getID())) {
            setCustomizationUserID(person.getID());
        } else {
            setCustomizationUserID(SessionManager.getUser().getID());
        }

        StringBuffer xml = new StringBuffer();
        xml.append(IXMLPersistence.XML_VERSION);
        xml.append("<LicenseNotification>");
        xml.append(person.getXMLBody());
        xml.append(license.getXMLBody());
        xml.append("<Message>" + HTMLUtils.escape(this.message) + "</Message>");
        xml.append("</LicenseNotification>");
        //System.out.println("LicenseNotification.java .... \n" + xml.toString());
        setNotificationXML(xml.toString());
        try {
            this.post();
        } catch (NotificationException ne) {
            // We don't want to fail the dissociation just for notification failure.
            //System.out.println("LicenseNotification.java : NotifyUserOfAssociation operation failed : Notification exception thrown " + ne.getMessage());
        }
    }

}
