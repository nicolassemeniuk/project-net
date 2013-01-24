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

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;

public class DocumentRepository implements IXMLPersistence {
    String objectID = null;
    String path = null;
    boolean isActive = false;

    public DocumentRepository(){
    }

    public DocumentRepository( String id, String path, boolean isActive ){
        setID(id);
    }

    public String getID(){
        return this.objectID;
    }

    public String getPath(){
        return this.path;
    }

    public boolean getIsActive(){
        return this.isActive;
    }

    public void setID( String newObjectID ){
        this.objectID = newObjectID;
    }

    public void setPath( String newPath ){
        this.path = newPath;
    }

    public void setIsActive( boolean newIsActive ){
        this.isActive = newIsActive;
    }

    public void store() throws net.project.persistence.PersistenceException {
        /** 
            Stores the object's state in the database.
        */

        String qstrSQL = "";
        String tmpID = "";
        String tmpPath = "";
        boolean tmpIsActive = false;

        tmpID = this.getID();
        tmpPath = this.getPath();
        tmpIsActive = this.getIsActive();

        if (this.getID() == null) {
            // this is an add

            qstrSQL = "INSERT INTO PN_DOC_REPOSITORY_BASE(repository_path, is_active) VALUES(\"";
            qstrSQL = qstrSQL + tmpPath + "\", \"" + tmpIsActive + "\")";

        } else {
            // update
            qstrSQL = "UPDATE PN_DOC_REPOSITORY_BASE SET repository_path = \"" + tmpPath;
            qstrSQL = qstrSQL + "\", is_active = \"" + net.project.util.Conversion.booleanToString(tmpIsActive); 
            qstrSQL = qstrSQL + "\" WHERE id = " + tmpID;
        }

        DBBean db = new DBBean();
        try {
            db.executeQuery(qstrSQL);

        } catch (SQLException sqle) {
            throw new PersistenceException("DocumentRepository.store(): threw " +
                "a persistence exception: " + sqle, sqle);

        } finally {
            db.release();
        }
    }
    /**
         Converts the object to XML representation
         This method returns the object as XML text.
         @return XML representation of the object
     */
    public String getXML() {
        StringBuffer xml = new StringBuffer();
        xml.append( XML_VERSION  );
        xml.append(getXMLBody());
        return xml.toString();
    }
    /**
        Converts the object to XML node representation without the xml header tag.
        This method returns the object as XML text.
         @return XML node representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<document_repository>");
        xml.append("<repository_id>" + this.objectID +  "</repository_id>");
        xml.append("<repository_path>" + this.path +  "</repository_path>");
        xml.append("<is_active>" + net.project.util.Conversion.booleanToString(this.isActive) +  "</is_active>");

        return xml.toString();
    }
}

