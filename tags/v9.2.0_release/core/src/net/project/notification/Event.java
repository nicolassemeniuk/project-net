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
package net.project.notification;

import java.io.Serializable;
import java.util.Date;

import net.project.security.User;
import net.project.xml.XMLUtils;


/**
 * Provides a default implementation of <code>INotificationEvent</code>.
 * 
 * @author deepak
 * @author Tim Morrow
 */
public class Event implements INotificationEvent, Serializable {

    /**
     * The ID for the Event.
     * @deprecated As of 7.6.3; No replacement. Will be removed.
     * This attribute never did and never will have a value.
     */
    protected String eventID = null;

    /**
     * The ID for the Parent Object which is actually
     * the target object ID.
     * @deprecated As of 7.6.3; Use {@link #getTargetObjectID()} instead.
     * This attribute will become private.
     */
    protected String parentObjectID = null;

    /** The XML for the target Object. */
    private String targetObjectXML = null;

    /**
     * The Object Type for the target Object, which is really
     * the event type and is actually the same as <code>code</code>.
     * */
    private String targetObjectType = null;

    /**
     * The Name of the Event.
     * @deprecated As of 7.6.3; no replacement.
     * See {@link #setName(String)} for more details.
     */
    protected String name = null;

    /**
     * The code (type) of the Event.
     * @deprecated As of 7.6.3; Use {@link #getEventType()} instead.
     * This attribute will become private.
     */
    protected String code = null;

    /**
     * The ID of the person performing the action that causes
     * the event.
     */
    private String eventByID = null;

    /**
     * The display name of the person performing the action that
     * causes the event.
     */
    protected String eventBy = null;

    /** The date at which the event occurred. */
    private Date date;

    /**
     * The notes (description) for the Event.
     * @deprecated As of 7.6.3; Use {@link #getDescription()} instead.
     * This attribute will become private.
     */
    protected String notes = null;

    /** The Space ID of the space in which the event occurred. */
    private String spaceID = null;

    /**
     * The user current user for the Event.
     * @deprecated As of 7.6.3; Use {@link #getInitiatorID()} instead.
     * This attribute will become private.
     */
    //transient protected User user = SessionManager.getUser();

    /**
     * Constructs a new object and a date object associated with the event.
     */
    public Event() {
        this.date = new Date();
    }

    /**
     * Specifies the current user that is causing the event.
     * <p>
     * This sets the display name and ID of the causing user.
     * </p>
     * @param user the user causing the event
     */
    public void setUser(User user) {
//        this.user = user;
        this.eventBy = user.getDisplayName();
        this.eventByID = user.getID();

    }

    /**
     * Sets the name for the Event.
     * @param name Event Name
     * @deprecated As of 7.6.3; no replacement.
     * The name is currently stored to the database and rendered
     * in XML, but it is currently only set as an English string
     * based on the event type.  As a result it cannot be displayed
     * to the user as it cannot be internationalized.
     * It is not currently displayed to the user and never will be.
     * An alternate mechanism should be used to provide a descriptive
     * name based on the event type.
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getXML() {
        return ("<?xml version=\"1.0\" ?>\n" + getXMLBody());
    }

    public String getXMLBody() {

        StringBuffer xml = new StringBuffer();
        String tab;

        tab = "\t\t";
        xml.append(tab + "<event_id />\n");
        xml.append(tab + "<parent_object_id>" + XMLUtils.escape(this.parentObjectID) + "</parent_object_id>\n");
        xml.append(tab + "<parent_object_name />\n");

        xml.append(tab + "<code>" + XMLUtils.escape(this.code) + "</code>\n");
        xml.append(tab + "<name>" + XMLUtils.escape(this.name) + "</name>\n");
        xml.append(tab + "<event_by_id>" + XMLUtils.escape(this.eventByID) + "</event_by_id>\n");
        xml.append(tab + "<event_by>" + XMLUtils.escape(this.eventBy) + "</event_by>\n");
        xml.append(tab + "<date>" + XMLUtils.formatISODateTime(this.date) + "</date>\n");
        xml.append(tab + "<notes>" + XMLUtils.escape(this.notes) + "</notes>\n");
        return xml.toString();

    }

    /**
     * Sets the notes (description) for the event.
     * Synonymous with calling <code>setDescription(notes)</code>.
     * @param notes the description
     * @deprecated As of 7.6.3; use {@link #setDescription(String)} instead.
     */
    public void setNotes(String notes) {
        setDescription(notes);
    }

    /**
     * Specifies the description of this event.
     * Synonymous with calling <code>setNotes(description)</code>.
     * @param description the description of the event
     */
    public void setDescription(String description) {
        this.notes = description;
    }

    public String getDescription() {
        return this.notes;
    }

    /**
     * Sets the event type.
     * <p>
     * This should be a value from {@link EventCodes}.
     * Synonymous with calling <code>setEventType(code)</code>.
     * </p>
     * @param code the event type.
     * @deprecated As of 7.6.3; Use {@link #setEventType(String)} instead.
     */
    public void setCode(String code) {
        setEventType(code);
    }

    /**
     * Specifies the type of the event.
     * Synonymous with calling <code>setCode(eventType)</code>.
     * @param eventType one of the values from {@link EventCodes}.
     */
    public void setEventType(String eventType) {
        this.code = eventType;
    }

    public String getEventType() {
        return this.code;
    }

    /**
     * Returns the time at which the event has occurred
     * (which is the time at which this Event was constructed).
     * @return The time at which the event has occurred
     */
    public Date getEventTime() {
        return this.date;
    }

    public String getInitiatorID() {
        return eventByID;
    }

    /**
     * Sets the Parent Object ID which is really the ID of the
     * object that caused the event.
     * Synonymous with calling <code>setTargetObjectID(objectID)</code>.
     * @param objectID the ID of the object causing the event
     * @see #setTargetObjectID
     * @see #getTargetObjectID
     * @deprecated As of 7.6.3; Use {@link #setTargetObjectID(String)} instead.
     */
    public void setParentObjectID(String objectID) {
        setTargetObjectID(objectID);
    }

    /**
     * Specifies the ID of the object that caused the event.
     * Synonymous with calling <code>setParentObjectID(targetObjectID)</code>.
     * @param targetObjectID the id of the object
     */
    public void setTargetObjectID(String targetObjectID) {
        this.parentObjectID = targetObjectID;
    }

    /**
     * Returns the ID of the target Object on which the Event is occurring.
     * @return The ID of the target Object on which the Event is occurring
     * @see #setParentObjectID
     */
    public String getTargetObjectID() {
        return this.parentObjectID;
    }

    /**
     * Specified the type of object causing the event.
     * <p>
     * <b>Note:</b> Usage of this method seems to always set the same value as {@link #setCode}
     * which is actually the <i>event type</i> not the object type.
     * Correct usage is to specify the Object type.
     * </p>
     * <p>
     * The event code appears to be written to event logs and the
     * target object type appears to be written to the notification tables.
     * </p>
     * @param type the object type
     */
    public void setTargetObjectType(String type) {
        this.targetObjectType = type;
    }

    /**
     * Returns event type.
     * <p>
     * <b>Note</b> Due to the confusion between <code>targetObjectType</code>
     * and <code>code</code> this method now simply returns the <code>code</code>.
     * @return the event type
     * @see #setTargetObjectType(String)
     */
    public String getTargetObjectType() {
        return this.targetObjectType;
    }

    /**
     * Specifies the XML of the object that caused the event.
     * @param xml The XML of the target Object
     */
    public void setTargetObjectXML(String xml) {
        this.targetObjectXML = xml;
    }

    public String getTargetObjectXML() {
        return this.targetObjectXML;
    }

    /**
     * Sets the Space ID for the event.
     * @param spaceID The Space ID
     */
    public void setSpaceID(String spaceID) {
        this.spaceID = spaceID;
    }

    public String getSpaceID() {
        return this.spaceID;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }


    /**
     * @param eventByID the eventByID to set
     */
    public void setEventByID(String eventByID) {
        this.eventByID = eventByID;
    }

    /**
     * @return the eventID
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * @param eventID the eventID to set
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     * @return the eventBy
     */
    public String getEventBy() {
        return eventBy;
    }

    /**
     * @param eventBy the eventBy to set
     */
    public void setEventBy(String eventBy) {
        this.eventBy = eventBy;
    }

    /**
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    
}
