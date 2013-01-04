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
import java.util.ArrayList;
import java.util.Iterator;

import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;

public class DocumentRepositoryCollection extends ArrayList {
    boolean isLoaded = false;

    public void load() throws net.project.persistence.PersistenceException {
        String qstrLoadRepos = "SELECT repository_id, repository_path, is_active FROM PN_DOC_REPOSITORY_BASE";

        DBBean db = new DBBean();
        try {
            db.executeQuery(qstrLoadRepos);
            // going forward one row at a time.
            while (db.result.next()) {
                DocumentRepository dr = new DocumentRepository();
                dr.setID(db.result.getString("repository_id"));
                dr.setPath(db.result.getString("repository_path"));
                dr.setIsActive(net.project.util.Conversion.toBoolean(db.result.getString("is_active")));

                this.add(dr);
            }

            isLoaded = true;
        } catch (SQLException sqle) {
            throw new PersistenceException("DocumentRepositoryCollection.load(): " +
                "threw a persistence exception: " + sqle, sqle);

        } finally {
            db.release();
        }
    }

    /**
     * Converts the object to XML representation This method returns the object
     * as XML text.
     */
    public void add(String id, String path, boolean isActive) throws PersistenceException {
        /* add an new DocumentRepository to the collection */
        DocumentRepository dr = new DocumentRepository();

        try {
            dr.setID(id);
            dr.setPath(path);
            dr.setIsActive(isActive);
            dr.store();
            this.add(dr);
        } catch (Exception e) {
            throw new PersistenceException("DocumentRepositoryCollection.load(): " +
                "threw a persistence exception: " + e, e);
        }
    }


    public String getXML() throws SQLException {
        StringBuffer xml = new StringBuffer();
        xml.append(IXMLPersistence.XML_VERSION);
        xml.append(getXMLBody());
        return xml.toString();
    }

    /**
     * Converts the object to XML node representation without the xml header
     * tag. This method returns the object as XML text.
     *
     * @return XML node representation
     * @throws SQLException 
     */
    public String getXMLBody() throws SQLException {
        IXMLPersistence dr = null;
        StringBuffer xml = new StringBuffer();
        Iterator list = this.iterator();
        xml.append("<document_repository_collection>");

        while (list.hasNext()) {
            dr = (IXMLPersistence) list.next();
            xml.append(dr.getXMLBody());

        }
        xml.append("</document_repository_collection>");
        return xml.toString();
    }
}