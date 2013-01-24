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
|   $Revision$
|       $Date$
|     $Author$
|
+--------------------------------------------------------------------------------------*/
package net.project.calendar;

import java.sql.SQLException;

import net.project.base.ObjectType;
import net.project.base.URLFactory;
import net.project.notification.ImmediateNotification;
import net.project.notification.NotificationException;
import net.project.resource.Person;
import net.project.security.User;
import net.project.xml.XMLUtils;

/**
 * Provides a notification for a meeting invitation.
 *
 * @author Phil Dixon
 * @since 05/2001
 */
public class MeetingNotification extends ImmediateNotification {
    /**
     * The stylesheet used for sending the meeting notification, currently
     * <code>/calendar/xsl/MeetingNotification.xsl</code>.
     */
    public static final String MEETING_NOTIFICATION_STYLE_SHEET = "/calendar/xsl/MeetingNotification.xsl";

    /**
     * Creates an empty MeetingNotification.
     */
    public MeetingNotification() {
        super();
    }

    /**
     * Initializes the Notification object.
     *
     * @param attendee the <code>Attendee</code> who is being invited; assumes
     * the current event is a meeting
     * @throws NotificationException if anything goes wrong while sending notifiaction
     * @throws SQLException 
     */
    public void initialize(AttendeeBean attendee) throws NotificationException, SQLException {

        Meeting meeting = (Meeting) attendee.m_event;
        Person host = meeting.getHost();

        StringBuffer xml = new StringBuffer();

        // see if the attendee is a registered user
        boolean registeredUser = net.project.base.DefaultDirectory.isUserRegisteredByID(attendee.getID());
        User customizationUser = null;
        if (registeredUser) {
            // Create a loaded user
            customizationUser = new User(attendee.getID());
        } else {
            // Customization user is current user for unregistered attendees
            customizationUser = net.project.security.SessionManager.getUser();
        }

        setDeliveryTypeID(ImmediateNotification.EMAIL_DELIVERABLE);
        setXSLStylesheetPath(MEETING_NOTIFICATION_STYLE_SHEET);
        setDeliveryAddress(attendee.getAttendeeEmail());
        setCustomizationUserID(customizationUser.getID());

        xml.append("<MeetingInvite>");
        xml.append(meeting.getXMLBody());

        // Once we have finished making the changes to notification to
        // automatically switch configuration and locale to the customization
        // user, it will no longer be necessary to include both externally
        // formatted date and times and ISO datetime; we can simply include
        // ISO datetime and do transforms directly in the notification XSL
        xml.append("<inviteeDate>" + XMLUtils.escape(customizationUser.getDateFormatter().formatDate(meeting.getStartTime())) + "</inviteeDate>");
        xml.append("<inviteeTime>" + XMLUtils.escape(customizationUser.getDateFormatter().formatTime(meeting.getStartTime())) + "</inviteeTime>");
        xml.append("<inviteeZone>" + XMLUtils.escape(customizationUser.getDateFormatter().formatDate(meeting.getStartTime(), "z")) + "</inviteeZone>\n");

        xml.append("<inviteeDateTime>" + XMLUtils.formatISODateTime(meeting.getStartTime()) + "</inviteeDateTime>");

        xml.append(attendee.getXMLBody());
        xml.append("<host>" + host.getXMLBody() + "</host>");
        xml.append("<meetingurl>" + URLFactory.makeURL(meeting.getID(), ObjectType.MEETING) + "&amp;external=1</meetingurl>");
        xml.append("</MeetingInvite>");

        setNotificationXML(xml.toString());
    }
}
