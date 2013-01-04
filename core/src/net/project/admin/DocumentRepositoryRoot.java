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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.admin;

import java.io.File;
import java.io.Serializable;
import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.util.Validator;

/**
 * Represents the root of a path to a document vault share.
 *
 * @author Matthew Flower
 * @since Version 8.0.0
 */
public class DocumentRepositoryRoot implements Serializable {
    /** The unique identifier for this document repository root. */
    private String id;
    /**
     * The path to the root of the repository.  This can be any path string that
     * could be safely used in a {@link java.io.File} object.
     */
    private String path;
    /** Indicates if this root is currently available for use. */
    private boolean active;

    /**
     * Get a unique identifier for this document repository root.
     *
     * @return a <code>String</code> containing a unique id for this document
     * repository root.
     */
    public String getID() {
        return id;
    }

    /**
     * Set a unique id for this document repository root.
     *
     * @param id a <code>String</code> containing a unique id for this document
     * repository root.
     */
    public void setID(String id) {
        this.id = id;
    }

    /**
     * Get the path that can access the server from the server.
     *
     * @return a <code>String</code> containing the path to the server.
     */
    public String getPath() {
        return path;
    }

    /**
     * Set a path to this root of the document repository.  This should be a
     * path that could be passed to a file object.
     *
     * @param path a <code>String</code> containing the path to the repository
     * root.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Indicates if this repository root should be used to store files.
     *
     * @return a <code>boolean</code> indicating if this root should be used to
     * store files.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Indicate if this repository path should be used to store files.
     *
     * @param active a <code>boolean</code> indicating if this root should be
     * used to store files.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean pathExists() {
        File file = new File(path);
        return file.exists();
    }

    public boolean pathWriteable() {
        File file = new File(path);
        return file.exists() && file.canWrite();
    }

    public void store() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            if (Validator.isBlankOrNull(id)) {
                db.executeQuery("select max(repository_id)+1 as new_id from pn_doc_repository_base");
                if (db.result.next()) {
                    id = db.result.getString("new_id");
                } else {
                    id = "101";
                }

                db.prepareStatement(
                    "insert into pn_doc_repository_base " +
                    "  (repository_path, is_active, repository_id) " +
                    "values " +
                    "  (?,?,?) "
                );
            } else {
                db.prepareStatement(
                    "update pn_doc_repository_base " +
                    "set " +
                    "  repository_path = ?, " +
                    "  is_active = ? " +
                    "where " +
                    "  repository_id = ?"
                );
            }

            db.pstmt.setString(1, path);
            db.pstmt.setBoolean(2, active);
            db.pstmt.setString(3, id);

            db.executePrepared();
        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to store document repository root.", sqle);
        } finally {
            db.release();
        }
    }

    public void remove() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            db.prepareStatement(
                "delete from pn_doc_repository_base " +
                "where repository_id = ? "
            );
            db.pstmt.setString(1, id);
            db.executePrepared();
        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to delete document repository root.", sqle);
        } finally {
            db.release();
        }
    }
}
