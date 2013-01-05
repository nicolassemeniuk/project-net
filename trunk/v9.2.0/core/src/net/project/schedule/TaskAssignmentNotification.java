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
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|    $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.schedule;

import net.project.base.DefaultDirectory;
import net.project.base.ObjectType;
import net.project.base.URLFactory;
import net.project.notification.ImmediateNotification;
import net.project.notification.NotificationException;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;
import net.project.resource.ScheduleEntryAssignment;
import net.project.security.User;
import net.project.xml.XMLUtils;

/**
 * Provides an immediate notification of assignment to a task.
 *
 */
public class TaskAssignmentNotification extends ImmediateNotification {

    public static final String ASSIGNMENT_NOTIFICATION_STYLESHEET = "/schedule/xsl/AssignmentNotification.xsl";

    public TaskAssignmentNotification() {
        super();
    }


    /**
     * Initializes the notification.
     *
     * @param task the task to which a person is being assigned
     * @param assignment the assignment created
     * @param user the user performing the assignment
     * @throws NotificationException if there is a problem initializing
     */
    public void initialize(ScheduleEntry task, ScheduleEntryAssignment assignment, User user) throws NotificationException {

        StringBuffer xml = new StringBuffer();

        try {
            Person assignee = getAssignee(assignment.getPersonID());
            setDeliveryTypeID(user.getDefaultNotificationDeliveryType());
            setXSLStylesheetPath(ASSIGNMENT_NOTIFICATION_STYLESHEET);
            setDeliveryAddress(assignee.getEmail());

            if (DefaultDirectory.isUserRegisteredByID(assignee.getID())) {
                setCustomizationUserID(assignee.getID());
            } else {
                setCustomizationUserID(user.getID());
            }

            xml.append(net.project.xml.IXMLTags.XML_VERSION_STRING);

            xml.append("<assignment-notification>");
            task.isFullXMLBody = false;//sachin: we don't need other assignments and predecessors list here
            xml.append(task.getXMLBody());
            xml.append(assignment.getXMLBody());
            xml.append(user.getXMLBody());

            xml.append("<assigneeName>" + XMLUtils.escape(assignee.getDisplayName()) + "</assigneeName>");
            xml.append("<assigneeStartDate>" + XMLUtils.formatISODateTime(task.getStartTime()) + "</assigneeStartDate>");
            xml.append("<assigneeEndDate>" + XMLUtils.formatISODateTime(task.getEndTime()) + "</assigneeEndDate>");
            xml.append("<taskurl>" + URLFactory.makeURL(task.getID(), ObjectType.TASK) + "&amp;external=1</taskurl>");
            
            //If task asignment from personal space get project name from assignment, 
            //because currnet space name is user name in personal space.
            if(user.getCurrentSpace().getSpaceType().getID().equals(ObjectType.PERSON)){
            	xml.append("<project_name>" + XMLUtils.escape(assignment.getSpaceName()) + "</project_name>");
            } else {
            	xml.append("<project_name>" + XMLUtils.escape(user.getCurrentSpace().getName()) + "</project_name>");
            }
            xml.append("</assignment-notification>");

            setNotificationXML(xml.toString());

        } catch (PersistenceException pe) {
            throw new NotificationException("TaskAssignmentNotificaiton.initialize threw a PE: " + pe);
        }

    }

    private Person getAssignee(String personID) throws PersistenceException {

        Person person = new Person();

        person.setID(personID);
        person.load();

        return person;
    }

}
