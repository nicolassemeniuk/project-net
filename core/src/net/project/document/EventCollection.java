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

 package net.project.document;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import net.project.database.DBBean;
import net.project.security.User;

import org.apache.log4j.Logger;


public class EventCollection implements Serializable {

    public String parentObjectID = null;
    public ArrayList collection = null;
    public User user = null;

    private boolean isLoaded = false;
    private boolean listDeleted = false;

    private DBBean db = new DBBean();

    public EventCollection() {
    }
    
    public void setListDeleted() {
        this.listDeleted = true;
    }
    
    public void unSetListDeleted() {
        this.listDeleted = false;
    }
    
    public boolean isDeleted() {
        return this.listDeleted;
    }

    public void setParentObjectID(String parentObjectID) {
	this.parentObjectID = parentObjectID;
    }

    public void setUser (User user) {
	this.user = user;
    }


    public void load() {

	ArrayList list = new ArrayList();
    DocumentEvent event = null;
	String viewName = listDeleted ? " pn_doc_del_history_view " : " pn_doc_history_view ";
	
	String qstrLoadEvent = "select document_id, document_name, doc_history_id, action, action_name, action_by_id," +
	    "action_by, action_date, action_comment" + 
	    " from " + viewName + " where document_id = " + this.parentObjectID + " order by action_date desc";

	try {

	    db.executeQuery(qstrLoadEvent);

	    while (db.result.next()) {

		event = new DocumentEvent();
		event.setParentObjectID(this.parentObjectID);
		// Commented out by PHIL per Tim 10/30/00
		// event.setUser (this.user);

		event.setEventID(db.result.getString("doc_history_id"));
		event.parentObjectName = db.result.getString("document_name");
		event.setCode(db.result.getString("action"));
		event.setName(db.result.getString("action_name"));
		event.setEventByID(db.result.getString("action_by_id"));
		event.setEventBy(db.result.getString("action_by"));
		event.setDate((java.util.Date) db.result.getTimestamp("action_date"));
		event.setNotes(db.result.getString("action_comment"));

		list.add (event);

	    } // end while


	} catch (SQLException sqle) {
		Logger.getLogger(EventCollection.class).debug("EventCollection.load() threw a SQL exception: " + sqle);
	
        } finally {
	    db.release();

        }

	this.collection = list;
	this.isLoaded = true;

    } // end load();



    public String getXML() {

	StringBuffer xml = new StringBuffer();
	String tab = null;
	Iterator events = collection.iterator();
    DocumentEvent event = null;

	tab = "\t";
	xml.append (tab + "<event_collection>\n");

	tab = "\t\t";

	while (events.hasNext()) {

	    event = (DocumentEvent) events.next();

	    xml.append(tab + "<event>\n");
	    xml.append( event.getXMLBody() );
	    xml.append(tab + "</event>\n");

	}

	tab = "\t";
	xml.append (tab + "</event_collection>\n\n");

	return xml.toString();

    } // end getXML()


    public boolean isLoaded() {
	return this.isLoaded();
    }










} // end class DBBean 
