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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 19088 $
|       $Date: 2009-04-14 14:55:15 -0300 (mar, 14 abr 2009) $
|     $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.resource;

import net.project.base.DefaultDirectory;
import net.project.notification.ImmediateNotification;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.space.Space;
import net.project.space.SpaceTypes;
import net.project.util.StringUtils;

import org.apache.log4j.Logger;

/**
 * Notification sent to registered users inviting them to a space.
 * Subclasses may change initialization or override the stylesheet used.
 */
public class SpaceInviteNotification extends ImmediateNotification {

    /** The path to the stylesheet to use for invitation. */
    protected static final String INVITE_NOTIFICATION_STYLE_SHEET = "/roster/xsl/registered-space-invite.xsl";

    /**
     * Initializes this notification.
     * Sets DeliveryTypeID, XSLStylesheetPath, DeliveryAddress, FromAddress and NotificationXML
     * @param inviteSpace the space to which users are being invited
     * @param invitingUser the user performing the invitation
     * @param invitee the Person to be invited
     * @param parameters the parameters of the invitation
     */
    public void initialize(Space inviteSpace, User invitingUser, Person invitee, SpaceInvitationManager.SpaceInvitationParameters parameters) {

        setDeliveryTypeID(invitingUser.getDefaultNotificationDeliveryType());
        setXSLStylesheetPath(getStylesheet());
        setDeliveryAddress(net.project.notification.Email.formatEmailAddress(invitee.getDisplayName(), invitee.getEmail()));
        setFromAddress(net.project.notification.Email.formatEmailAddress(invitingUser.getDisplayName(), invitingUser.getEmail()));

        if (DefaultDirectory.isUserRegisteredByID(invitee.getID())) {
            setCustomizationUserID(invitee.getID());
        } else {
            setCustomizationUserID(invitingUser.getID());
        }


        // Construct Notification XML
        net.project.xml.document.XMLDocument xml = new net.project.xml.document.XMLDocument();

        try {

            xml.startElement("InviteNotification");
            xml.addElement("inviteurlparameters", parameters.getInviteURL());            
           
            //build url link for space 
            String spaceUrl = "/" + (inviteSpace.getSpaceType() == SpaceTypes.PROJECT ? "project" : "business") + "/"
                                  + (inviteSpace.getSpaceType() == SpaceTypes.PROJECT ? "Dashboard" : "Main.jsp") + "?id=" + inviteSpace.getID();
            xml.addElement("spaceUrl", spaceUrl);
            	
            // Legacy purposes we include TeamMemberWizard element
            xml.startElement("TeamMemberWizard");

            // Add all invitee properties
            xml.addElement("inviteeFirstName", invitee.getFirstName());
            xml.addElement("inviteeLastName", invitee.getLastName());
            xml.addElement("inviteeFullName", invitee.getDisplayName());
            xml.addElement("inviteeEmail", invitee.getEmail());
            xml.addElement("inviteeMessage", parameters.getMessage());
            xml.addElement("inviteeResponsibilities", StringUtils.isNotEmpty(invitee.getResponsibilities()) ? invitee.getResponsibilities() : parameters.getResponsibilities());
            xml.addElement("IsAutoAcceptInvite", new Boolean(parameters.isAutoAcceptInvite()));

            // Add the user performing the invite
            if (invitingUser != null) {
                xml.addXMLString(invitingUser.getXMLBody());
            }

            // Add the space information
            if (inviteSpace != null) {
                if (!inviteSpace.isLoaded()) {

                    try {
                        inviteSpace.load();
                    } catch (PersistenceException pe) {
                    	Logger.getLogger(SpaceInviteNotification.class).error("SpaceInvitationManager.getXMLDocument: Unable to load space: " + pe);
                        // We'll add empty properties to the XML
                    }
                }

                xml.addXMLString(inviteSpace.getXMLProperties());
            }

            // A dump of the person being invited
            // This seems to be somewhat extraneous, but is included for
            // legacy reasons
            xml.addXMLString(invitee.getXMLBody());

            xml.endElement();
            xml.endElement();

        } catch (net.project.xml.document.XMLDocumentException e) {
            // Empty xml document

        }
        setNotificationXML(xml.getXMLString());
        System.out.println("space invitation mail:-\n"+xml.getXMLString());
    }

    /**
     * Returns the stylesheet to use for notification.
     * Subclasses should override with the appropriate stylesheet.
     * The default is {@link SpaceInviteNotification#INVITE_NOTIFICATION_STYLE_SHEET}.
     * @return the stylesheet path
     */
    protected String getStylesheet() {
        return INVITE_NOTIFICATION_STYLE_SHEET;
    }

}
