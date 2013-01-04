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

import java.io.File;
import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;

import net.project.base.EventException;
import net.project.base.EventFactory;
import net.project.base.ObjectType;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.events.EventType;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.Conversion;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

public class DocumentVersion implements IVersion {
    public String versionID = null;
    public String parentObjectID = null;
    public String parentObjectName = null;

    public int versionNum = 0;
    public String versionLabel = null;
    public java.util.Date lastModified = null;
    public String modifiedBy = null;
    public String notes = null;

    public String OSFilePath = null;
    public String shortFileName = null;
    public String author = null;
    public String authorID = null;
    public boolean isCko = false;
    public String ckoByID = null;
    public String ckoBy = null;
    public java.util.Date ckoDate = null;
    public java.util.Date ckoReturn = null;

    public String numTimesViewed = null;
    public String recordStatus = null;
    public String repositoryID = null;
    public String repositoryPath = null;

    public ContentElementCollection contentElements = null;
    public User user = null;
    public boolean isLoaded = false;
    private boolean listDeleted = false;

    /**
     * **************************************************************************************************************
     * ****                                     CONSTRUCTORS
     *                                                    *****
     * ***************************************************************************************************************
     */

    public DocumentVersion(String versionID, String parentObjectID) {

        this.versionID = versionID;
        this.parentObjectID = parentObjectID;

        load();

    }

    public DocumentVersion() {
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


    /**
     * **************************************************************************************************************
     * ****                                     Implementing INTERFACE
     * IContainerObject                                 *****
     * ***************************************************************************************************************
     */

    public void load() {

        String viewName = listDeleted ? " pn_doc_del_version_view " : " pn_doc_version_view ";
        
        String qstrLoadDocumentVersion = "select version_id, document_id, document_name, doc_version_num, doc_version_label," +
            "date_modified, modified_by, doc_comment, source_file_name, short_file_name, doc_author_id, author, is_checked_out," +
            "checked_out_by_id, cko_by, date_checked_out, cko_due, record_status" +
            " from " + viewName + " where version_id = " + this.versionID + " and document_id = " + this.parentObjectID;

        DBBean db = new DBBean();
        try {

            db.executeQuery(qstrLoadDocumentVersion);

            if (db.result.next()) {

                this.parentObjectName = db.result.getString("document_name");
                this.versionNum = db.result.getInt("doc_version_num");
                this.versionLabel = db.result.getString("doc_version_label");
                this.lastModified = (java.util.Date) db.result.getTimestamp("date_modified");
                this.modifiedBy = db.result.getString("modified_by");
                this.notes = db.result.getString("doc_comment");
                this.OSFilePath = db.result.getString("source_file_name");
                this.shortFileName = db.result.getString("short_file_name");
                this.authorID = db.result.getString("doc_author_id");
                this.author = db.result.getString("doc_author");
                this.isCko = DocumentUtils.stringToBoolean(db.result.getString("is_checked_out"));
                this.ckoBy = db.result.getString("checked_out_by");
                this.ckoByID = db.result.getString("checked_out_by_id");
                this.ckoDate = DocumentUtils.sqlDateToUtilDate(db.result.getDate("date_checked_out"));
                this.ckoReturn = DocumentUtils.sqlDateToUtilDate(db.result.getDate("cko_due"));
                this.recordStatus = db.result.getString("record_status");
//		this.repositoryID = db.result.getString("repository_id");
//		this.repositoryPath = db.result.getString("repository_path");

//		PnDebug.display("SET REPOSITORY PATH IN LOAD TO: " + this.repositoryPath);

                isLoaded = true;

            } // end if
            else {
            	Logger.getLogger(DocumentVersion.class).debug("DocumentVersion.load() could not load document version: " + this.versionID);
            }

        } // end try
        catch (SQLException sqle) {
            isLoaded = false;
            Logger.getLogger(DocumentVersion.class).debug("DocumentVersion.load() threw an SQL exception: " + sqle);
        } // end catch
        finally {
            db.release();
        }

        this.loadContentElements();

    } // end load


    public void loadContentElements() {

        ContentElementCollection elements = new ContentElementCollection();

        elements.setParentObjectID(this.parentObjectID);
        elements.setParentVersionID(this.versionID);

        elements.load();

        this.contentElements = elements;

    } // loadContentElements

    public String getVersionID() {
        return this.versionID;
    }

    public boolean isVersionOf(String parentObjectID) {

        boolean retval = false;

        if (parentObjectID.equals(this.parentObjectID)) {
            retval = true;
        } else {
            retval = false;
        }

        return retval;
    } // end isVersionOf

    public String getParentObjectID() {
        return this.parentObjectID;
    }

    public void getBytes() {
    }

    public void getByteStream() {
    }


    /**
     * **************************************************************************************************************
     * ****                                 Implementing other getter/setter
     * methods                                          *****
     * ***************************************************************************************************************
     */

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public int getVersionNum() {
        return this.versionNum;
    }

    public String getVersionLabel() {
        return this.versionLabel;
    }

    public ContentElement getContentElement() {

        return this.contentElements.getContentElement();
    }

    public String getFileSize() {
        ContentElement element = getContentElement();

        return element.fileSize;
    }

    public String getMimeType() {
        ContentElement element = getContentElement();

        return element.mimeType;
    }

    public String getFileFormat() {

        return getContentElement().format;

    }

    public String getFileHandle() {
        return getContentElement().fileHandle;
    }

    public boolean isLoaded() {
        return this.isLoaded;
    }


    public void download(HttpServletResponse response) throws DocumentException {

        FileManager fileManager = new FileManager();
        DocumentEvent event = new DocumentEvent();

        fileManager.setObjectID(this.parentObjectID);
        fileManager.setFileHandle(getFileHandle());
        fileManager.setContentType(getMimeType());
        fileManager.setFileName(this.shortFileName);
        fileManager.setRepositoryPath(this.repositoryPath);


        event.setSpaceID(this.user.getCurrentSpace().getID());
        event.setTargetObjectID(this.parentObjectID);
        event.setTargetObjectType(net.project.base.ObjectType.DOCUMENT);
        event.setTargetObjectXML(this.getXML());
        event.setCode(EventCodes.VIEW_DOCUMENT_VERSION);
        event.setName(EventCodes.getName(EventCodes.VIEW_DOCUMENT_VERSION));
        event.setUser(this.user);
        event.setNotes(PropertyProvider.get("prm.document.documentversion.download.event.notes", new Object[]{new Integer(this.versionNum), this.parentObjectName}));

        // log this event to the database
        event.store();
        
		// publishing event to asynchronous queue
        try {
        	net.project.events.DocumentEvent documentEvent = (net.project.events.DocumentEvent) EventFactory.getEvent(ObjectType.DOCUMENT, EventType.VIEWED);
        	documentEvent.setObjectID(this.parentObjectID);
        	documentEvent.setObjectType(ObjectType.DOCUMENT);
        	documentEvent.setName(PropertyProvider.get("prm.document.documentversion.download.event.notes", new Object[]{new Integer(this.versionNum), this.parentObjectName}));
        	documentEvent.setObjectRecordStatus("A");
        	documentEvent.publish();
		} catch (EventException e) {
			Logger.getLogger(DocumentVersion.class).error("Document.download() :: Document View Event Publishing Failed!"+ e.getMessage());
		}

        fileManager.download(response);
    } // download
    
    /**
     * This method returns a {@link java.io.File} object for the 
     * document version.
     *
     * @return The abstract file object representing this document version
     * @throws DocumentException if the document has not been loaded
     */
    public File getFileObject() throws DocumentException {

        if (!isLoaded()) {
            throw new DocumentException("Document.getFileObject(): Document must first be loaded");
        }

        FileManager fileManager = new FileManager();

        fileManager.setObjectID(this.parentObjectID);
        fileManager.setFileHandle(getFileHandle());
        fileManager.setContentType(getMimeType());
        fileManager.setFileName(this.shortFileName);
        fileManager.setRepositoryPath(this.repositoryPath);

        return (fileManager.getFileObject());

    } // end getFileObject



    /**
     * **************************************************************************************************************
     * ****                                 Implementing redering methods
     *                                                  *****
     * ***************************************************************************************************************
     */

    public String getXML() {

        StringBuffer xml = new StringBuffer();
        String tab = null;

        xml.append("<jsp_root_url>" + XMLUtils.escape(SessionManager.getJSPRootURL()) + "</jsp_root_url>");
        tab = "\t\t";
        xml.append(tab + "<version_id>" + XMLUtils.escape(this.versionID) + "</version_id>\n");
        xml.append(tab + "<parent_object_id>" + XMLUtils.escape(this.parentObjectID) + "</parent_object_id>\n");
        xml.append(tab + "<parent_object_name>" + XMLUtils.escape(this.parentObjectName) + "</parent_object_name>\n");

        xml.append(tab + "<version_num>" + XMLUtils.escape(Conversion.intToString(this.versionNum)) + "</version_num>\n");
        xml.append(tab + "<version_label>" + XMLUtils.escape(this.versionLabel) + "</version_label>\n");
        xml.append(tab + "<last_modified>" +
            XMLUtils.formatISODateTime(this.lastModified) + "</last_modified>\n");
        xml.append(tab + "<modified_by>" + XMLUtils.escape(this.modifiedBy) + "</modified_by>\n");
        xml.append(tab + "<notes>" + XMLUtils.escape(this.notes) + "</notes>\n");

        xml.append(tab + "<os_file_path>" + XMLUtils.escape(this.OSFilePath) + "</os_file_path>\n");
        xml.append(tab + "<short_file_name>" + XMLUtils.escape(this.shortFileName) + "</short_file_name>\n");
        xml.append(tab + "<author>" + XMLUtils.escape(this.author) + "</author>\n");
        xml.append(tab + "<author_id>" + XMLUtils.escape(this.authorID) + "</author_id>\n");
        xml.append(tab + "<is_cko>" + XMLUtils.format(this.isCko) + "</is_cko>\n");
        xml.append(tab + "<cko_by_id>" + XMLUtils.escape(this.ckoByID) + "</cko_by_id>\n");
        xml.append(tab + "<cko_by>" + XMLUtils.escape(this.ckoBy) + "</cko_by>\n");

        if (this.ckoDate == null) {
            xml.append(tab + "<cko_date/>\n");
        } else {
            xml.append(tab + "<cko_date>" + XMLUtils.formatISODateTime(this.ckoDate) + "</cko_date>\n");
        }

        if (this.ckoDate == null) {
            xml.append(tab + "<cko_return/>\n");
        } else {
            xml.append(tab + "<cko_return>" + XMLUtils.formatISODateTime(this.ckoDate) + "</cko_return>\n");
        }

        xml.append(tab + "<file_size>" + XMLUtils.escape(DocumentUtils.fileSizetoKBytes(getFileSize())) + "</file_size>\n");
        xml.append(tab + "<file_handle>" + XMLUtils.escape(getFileHandle()) + "</file_handle>\n");
        xml.append(tab + "<file_format>" + XMLUtils.escape(getFileFormat()) + "</file_format>\n");

        xml.append(tab + "<num_times_viewed>" + XMLUtils.escape(this.numTimesViewed) + "</num_times_viewed>\n");
        xml.append(tab + "<record_status>" + XMLUtils.escape(this.recordStatus) + "</record_status>\n");

        //PnDebug.dumpString(xml.toString());

        return xml.toString();
    } // end getXML()

} // end class DocumentVersion


