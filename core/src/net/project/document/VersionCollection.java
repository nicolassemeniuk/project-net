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

public class VersionCollection implements Serializable {

    public String parentObjectID = null;
    public String currentVersionID = null;
    public ArrayList collection = null;
    
    public User user = null;

    public boolean isLoaded = false;
    private boolean listDeleted = false;
    private DBBean db = new DBBean();

           

    /*****************************************************************************************************************
    *****                                     CONSTRUCTORS                                                                        *****
    *****************************************************************************************************************/

    public VersionCollection (String parentObjectID, String currentVersionID) {

	this.parentObjectID = parentObjectID;
	this.currentVersionID = currentVersionID;
	
	load();

    }

    public VersionCollection () {
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

    public void load () {

	ArrayList listOfVersions = new ArrayList ();
	String viewName = listDeleted ? " pn_doc_del_version_view " : " pn_doc_version_view ";
	DocumentVersion version = null;
	
	String qstrLoadDocumentVersion = "select version_id, document_id, document_name, doc_version_num, doc_version_label," +
	    "date_modified, modified_by, doc_comment, source_file_name, short_file_name, doc_author_id, author, is_checked_out," +
	    "checked_out_by_id, cko_by, date_checked_out, cko_due, record_status,repository_id, repository_path" +
	    " from " + viewName + " where document_id = " + this.parentObjectID + " order by doc_version_num desc";

	try {

	    db.executeQuery (qstrLoadDocumentVersion);

	    while (db.result.next()) {

		version = new DocumentVersion();
		version.setUser(this.user);

		version.versionID = db.result.getString("version_id");
		version.parentObjectID = this.parentObjectID;
		version.parentObjectName = db.result.getString("document_name");
		version.versionNum = db.result.getInt("doc_version_num");
		version.versionLabel = db.result.getString("doc_version_label");
		version.lastModified = (java.util.Date) db.result.getTimestamp("date_modified");
		version.modifiedBy = db.result.getString("modified_by");
		version.notes = db.result.getString("doc_comment");
		version.OSFilePath = db.result.getString("source_file_name");
		version.shortFileName = db.result.getString("short_file_name");
		version.authorID = db.result.getString("doc_author_id");
		version.author = db.result.getString("author");
		version.isCko = DocumentUtils.stringToBoolean( db.result.getString("is_checked_out") );
		version.ckoBy = db.result.getString("cko_by");
		version.ckoByID = db.result.getString("checked_out_by_id");
		version.ckoDate = DocumentUtils.sqlDateToUtilDate( db.result.getDate("date_checked_out"));
		version.ckoReturn = DocumentUtils.sqlDateToUtilDate( db.result.getDate("cko_due"));
                version.recordStatus = db.result.getString("record_status");
		version.repositoryID = db.result.getString("repository_id");
		version.repositoryPath = db.result.getString("repository_path");

		version.loadContentElements();
		version.isLoaded = true;

		listOfVersions.add (version);

	    } // end while


	} catch (SQLException sqle) {
	    System.out.println (sqle);
	    Logger.getLogger(VersionCollection.class).debug("Document.VersionCollection.load() threw an SQL exception: " + sqle);
	
        } finally {
	    db.release();

        }

	this.collection = listOfVersions;
	this.isLoaded = true;


    } // end load


    /*****************************************************************************************************************
    *****                                 Implementing getter/setter methods                                                  *****
    *****************************************************************************************************************/
    
    public void setUser (User user) {
	this.user = user;
    }

    public void setCurrentVersionID (String currentVersionID) {
	this.currentVersionID = currentVersionID;
    }

    public void setParentObjectID (String parentObjectID) {
	this.parentObjectID = parentObjectID;
    }

    public IVersion getVersion (int versionNum) {

	Iterator versionList = this.collection.iterator();
	IVersion version = null;
	IVersion retval = null;

	while (versionList.hasNext()) {

	    version = (IVersion) versionList.next();

	    if (versionNum == version.getVersionNum())
		retval = version;

	} // end while

	return retval;

    } // end getVersion

    public IVersion getVersion (String versionID) {

	Iterator versionList = this.collection.iterator();
	IVersion tmp = null;
	IVersion version = null;

	while (versionList.hasNext()) {

	    tmp = (IVersion) versionList.next();

	    if (versionID.equals ( tmp.getVersionID() )) 
		version = tmp;

	} // end while

	return version;

    } //end getVersion

    public IVersion getCurrentVersion() {

	Iterator versionList = this.collection.iterator();
	IVersion currentVersion = null;
	IVersion tmp = null;

	while (versionList.hasNext()) {

	    tmp = (IVersion) versionList.next();

	    Logger.getLogger(VersionCollection.class).debug("CurrentVersion: " + this.currentVersionID + " -- TMP Version ID: " + tmp.getVersionID());

	    if ( this.currentVersionID.equals ( tmp.getVersionID() ) ) {
		currentVersion = tmp;
		break;
	    }


	} // end while

	return currentVersion;
    } // end getCurrentVersion






    /*****************************************************************************************************************
    *****                                 Implementing redering methods                                                         *****
    *****************************************************************************************************************/

    public String getXML() {

	StringBuffer xml = new StringBuffer();
	String tab = null;
	Iterator versions = collection.iterator();
	IVersion version = null;

	tab = "\t";
	xml.append (tab + "<version_collection>\n");

	tab = "\t\t";

	while (versions.hasNext()) {

	    version = (IVersion) versions.next();

	    xml.append(tab + "<version>\n");
	    xml.append( version.getXML() );
	    xml.append(tab + "</version>\n");

	}

	tab = "\t";
	xml.append (tab + "</version_collection>\n\n");

	return xml.toString();

    } // end getXML()




    public boolean isLoaded() {
	return this.isLoaded;
    }

    public int size() {

	int size = -1;

	if (this.isLoaded)
	    size = this.collection.size();

	return size;
    }



}
