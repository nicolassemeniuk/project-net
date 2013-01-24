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

 /*------------------------------------------------------------------------------------------+
|
|    $RCSfile$
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|    $Author: umesha $
|-----------------------------------------------------------------------------------------*/

package net.project.resource;

import net.project.notification.INotificationDB;
import net.project.notification.ImmediateNotification;
import net.project.security.User;
import net.project.space.Space;

/**
 * This class is responsible for sending Notifications to the Participants
 * when the Participant is being deleted from the Workspace
 * @author deepak
 */
public class ParticipantDeleteNotification extends ImmediateNotification {
    /**
     * Constant for XSL File Path
     */
    public String PARTICIPANT_DELETE_NOTIFICATION_STYLE_SHEET = "/roster/xsl/ParticipantDeleteNotification.xsl";

    /**
     Constructor
     */
    public ParticipantDeleteNotification() {
        super();
    }

    /**
     * Intializes the Notification object
     * @param space the workspace from where the participant is being reoved
     * @param admin Administrator who is removing the Participant
     * @param removedPerson The Particpant who is being removed; this should
     * be a registered, non-deleted user
     */
    public void initialize(Space space, User admin, Person removedPerson) {

        StringBuffer xml = new StringBuffer();
        setDeliveryTypeID(INotificationDB.EMAIL_DELIVERABLE);
        setXSLStylesheetPath(PARTICIPANT_DELETE_NOTIFICATION_STYLE_SHEET);
        setDeliveryAddress(removedPerson.getEmail());
        setFromAddress(admin.getEmail());
        setCustomizationUserID(removedPerson.getID());


        xml.append(net.project.xml.IXMLTags.XML_VERSION_STRING);
        xml.append("<ParticipantDelete>");
        xml.append(space.getXMLProperties());
        xml.append("<Administrator>");
        xml.append(admin.getXMLBody());
        xml.append("</Administrator>");
        xml.append("<DeletedMember>");
        xml.append(removedPerson.getXMLBody());
        xml.append("</DeletedMember>");
        xml.append("</ParticipantDelete>");

        setNotificationXML(xml.toString());
    }

}   //  PartcipantDeleteNotification
