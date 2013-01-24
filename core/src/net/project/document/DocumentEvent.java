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
package net.project.document;

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.notification.NotificationManager;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * Provides an Event caused by an action on a document or a container.
 *
 * @author unascribed
 * @author Tim Morrow
 */
public class DocumentEvent extends net.project.notification.Event {

    public String parentObjectName = null;

    public boolean store() {

        DBBean db = new DBBean();
        boolean isOK = false;

        try {

            db.prepareCall("begin  DOCUMENT.LOG_EVENT (?,?,?,?,?); end;");

            db.cstmt.setString(1, this.parentObjectID);
            db.cstmt.setString(2, getInitiatorID());
            db.cstmt.setString(3, this.code);
            db.cstmt.setString(4, this.name);
            db.cstmt.setString(5, this.notes);


            db.executeCallable();

            isOK = true;

            // finally, register this event to the notification engine.
            NotificationManager.notify(this);

        } catch (SQLException sqle) {
        	Logger.getLogger(DocumentEvent.class).debug("Event.store(): User: " + getInitiatorID() + ", unable to log event: " + sqle);
            isOK = false;

        } finally {
            db.release();

        }

        return isOK;

    } // end store()


    public String getXMLBody() {

        User currentUser = SessionManager.getUser();
        StringBuffer xml = new StringBuffer();
        String tab;

        tab = "\t\t";
        xml.append(tab + "<event_id>" + XMLUtils.escape(this.eventID) + "</event_id>\n");
        xml.append(tab + "<parent_object_id>" + XMLUtils.escape(this.parentObjectID) + "</parent_object_id>\n");
        xml.append(tab + "<parent_object_name>" + XMLUtils.escape(this.parentObjectName) + "</parent_object_name>\n");

        xml.append(tab + "<code>" + XMLUtils.escape(this.code) + "</code>\n");
        xml.append(tab + "<name>" + XMLUtils.escape(this.name) + "</name>\n");
        xml.append(tab + "<event_by_id>" + XMLUtils.escape(getInitiatorID()) + "</event_by_id>\n");
        xml.append(tab + "<event_by>" + XMLUtils.escape(this.eventBy) + "</event_by>\n");
        xml.append(tab + "<date>" +
            XMLUtils.escape(currentUser.getDateFormatter().formatDate(getEventTime())) +
            " at " + currentUser.getDateFormatter().formatTime(getEventTime()) + "</date>\n");
        xml.append(tab + "<dateTime>" + XMLUtils.formatISODateTime(getEventTime()) + "</dateTime>\n");
        xml.append(tab + "<notes>" + XMLUtils.escape(this.notes) + "</notes>\n");

        return xml.toString();

    } // end getXML()


//    public void clear() {
//
//        eventID = null;
//        parentObjectID = null;
//
//        name = null;
//        code = null;
//        eventByID = null;
//        eventBy = null;
//        setEventType() = null;
//        notes = null;
//        setSpaceID(null);
////        user = SessionManager.getUser();
//
//    }

}
