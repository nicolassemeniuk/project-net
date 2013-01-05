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

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;

import org.apache.log4j.Logger;

public class ContentElementCollection implements Serializable {
    public String parentObjectID = null;
    public String parentVersionID = null;

    protected ArrayList collection = null;

    public boolean isLoaded = false;

    public ContentElementCollection() {
    }

    public void load() {
        ContentElement element = null;
        ArrayList listOfElements = new ArrayList();
        String qstrLoadContentElements = "select doc_content_id, document_id, version_id, doc_format_id, doc_format, file_size," +
            "file_handle, record_status, mime_type, app_icon_url " +
            "from pn_doc_content_elements_view where version_id = " + this.parentVersionID +
            " and document_id = " + this.parentObjectID;

        DBBean db = new DBBean();
        try {
            db.executeQuery(qstrLoadContentElements);

            while (db.result.next()) {

                element = new ContentElement();
                element.parentObjectID = this.parentObjectID;
                element.parentVersionID = this.parentVersionID;

                element.elementID = db.result.getString("doc_content_id");

                element.formatID = db.result.getString("doc_format_id");
                element.format = PropertyProvider.get(db.result.getString("doc_format"));
                element.fileSize = db.result.getString("file_size");
                element.fileHandle = db.result.getString("file_handle");
                element.mimeType = db.result.getString("mime_type");
                element.appIconURL = db.result.getString("app_icon_url");

                listOfElements.add(element);

            } // end while

        } catch (SQLException sqle) {
        	Logger.getLogger(ContentElementCollection.class).debug("Contentcollection.load() threw a SQL exception: " + sqle);

        } finally {
            db.release();

        }

        this.collection = listOfElements;
        this.isLoaded = true;

    } // end load()


    public String getXML() {

        StringBuffer xml = new StringBuffer();
        String tab = null;
        Iterator elements = collection.iterator();
        ContentElement element = null;

        tab = "\t";
        xml.append(tab + "<element_collection>\n");

        tab = "\t\t";

        while (elements.hasNext()) {

            element = (ContentElement) elements.next();

            xml.append(tab + "<element>\n");
            xml.append(element.getXML());
            xml.append(tab + "</element>\n");

        }

        tab = "\t";
        xml.append(tab + "</element_collection>\n\n");

        return xml.toString();

    } // end getXML()

    public void setParentObjectID(String parentObjectID) {
        this.parentObjectID = parentObjectID;
    }

    public void setParentVersionID(String parentVersionID) {
        this.parentVersionID = parentVersionID;
    }

    public boolean isCompound() {

        boolean retval = false;

        if (this.isLoaded) {
            if (collection.size() > 1) {
                retval = true;
            }
        }

        return retval;
    } // end isCompound()


    // HACK
    public ContentElement getContentElement() {

        ContentElement element = null;

        if (!isCompound()) {
            element = (ContentElement) this.collection.get(0);
        }

        return element;

    } // get the only element in the collection


    public boolean isLoaded() {
        return this.isLoaded;
    }

    public int size() {

        int size = -1;

        if (this.isLoaded) {
            size = this.collection.size();
        }

        return size;
    }

} // end class ContentElement
