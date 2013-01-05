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

import java.io.OutputStream;
import java.sql.SQLException;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.security.User;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

public class ContentElement implements IContentElement {
    public String elementID = null;
    public String parentObjectID = null;
    public String parentVersionID = null;
    public String formatID = null;
    public String format = null;
    public String fileSize = null;
    public String fileHandle = null;
    public String displaySequence = null;
    public String mimeType = null;
    public String appIconURL = null;
    public User user = null;

    public ContentElement() {
    }

    public void load() {

        String qstrLoadContentElement = "select doc_content_id, document_id, version_id, doc_format_id, doc_format, file_size," +
            "file_handle, record_status, mime_type, app_icon_url " +
            "from pn_doc_content_elements_view where doc_content_id = " + this.elementID;

        DBBean db = new DBBean();
        try {
            db.executeQuery(qstrLoadContentElement);

            if (db.result.next()) {

                this.parentObjectID = db.result.getString("document_id");
                this.parentVersionID = db.result.getString("version_id");
                this.formatID = db.result.getString("doc_format_id");
                this.format = PropertyProvider.get(db.result.getString("doc_format"));
                this.fileSize = db.result.getString("file_size");
                this.fileHandle = db.result.getString("file_handle");
                this.mimeType = db.result.getString("mime_type");
                this.appIconURL = db.result.getString("app_icon_url");

            }


        } catch (SQLException sqle) {
        	Logger.getLogger(ContentElement.class).debug("ContentElement.load() threw a SQL exception: " + sqle);

        } finally {
            db.release();

        }
    } // end load()

    public void setUser(User user) {
        this.user = user;
    }

    public void setElementID(String elementID) {
        this.elementID = elementID;
    }

    public String getElementID() {
        return this.elementID;
    }

    public String getParentObjectID() {
        return this.parentObjectID;
    }

    public String getParentVersionID() {
        return this.parentVersionID;
    }


    public String getXML() {
        StringBuffer xml = new StringBuffer();
        String tab = null;

        tab = "\t\t";
        xml.append(tab + "<element_id>" + XMLUtils.escape(this.elementID) + "</element_id>\n");
        xml.append(tab + "<parent_object_id>" + XMLUtils.escape(this.parentObjectID) + "</parent_object_id>\n");
        xml.append(tab + "<parent_version_id>" + XMLUtils.escape(this.parentVersionID) + "</parent_version_id>\n");
        xml.append(tab + "<format_id>" + XMLUtils.escape(this.formatID) + "</format_id>\n");
        xml.append(tab + "<format>" + XMLUtils.escape(this.format) + "</format>\n");
        xml.append(tab + "<file_size>" + XMLUtils.escape(this.fileSize) + "</file_size>\n");
        xml.append(tab + "<file_handle>" + XMLUtils.escape(this.fileHandle) + "</file_handle>\n");
        xml.append(tab + "<mime_type>" + XMLUtils.escape(this.mimeType) + "</mime_type>\n");
        xml.append(tab + "<app_icon_url>" + XMLUtils.escape(this.appIconURL) + "</app_icon_url>\n");

        return xml.toString();
    }


    public String getFileHandle() {
        return this.fileHandle;
    }

    public OutputStream getByteStream() {

        return null;
    }


} // end class ContentElement
