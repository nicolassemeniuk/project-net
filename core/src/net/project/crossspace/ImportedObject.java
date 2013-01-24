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

 package net.project.crossspace;

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

/**
 * A foreign object is an object appears in a tool (e.g. a schedule) but whose
 * native space is a different space.
 */
public class ImportedObject {
    private final String SQL = "{call sharing.add_external(?,?,?,?,?,?)}";

    private String exportedObjectID;
    private String exportContainerID;
    private String exportSpaceID;
    private String importContainerID;
    private String importSpaceID;
    private String importedObjectID;
    private boolean readOnly;

    public String getExportedObjectID() {
        return exportedObjectID;
    }

    public void setExportedObjectID(String exportedObjectID) {
        this.exportedObjectID = exportedObjectID;
    }

    public String getExportContainerID() {
        return exportContainerID;
    }

    public void setExportContainerID(String exportContainerID) {
        this.exportContainerID = exportContainerID;
    }

    public String getImportContainerID() {
        return importContainerID;
    }

    public void setImportContainerID(String importContainerID) {
        this.importContainerID = importContainerID;
    }

    public String getImportSpaceID() {
        return importSpaceID;
    }

    public void setImportSpaceID(String importSpaceID) {
        this.importSpaceID = importSpaceID;
    }

    public String getImportedObjectID() {
        return importedObjectID;
    }

    public void setImportedObjectID(String importedObjectID) {
        this.importedObjectID = importedObjectID;
    }

    public String getExportSpaceID() {
        return exportSpaceID;
    }

    public void setExportSpaceID(String exportSpaceID) {
        this.exportSpaceID = exportSpaceID;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public void store() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            db.prepareStatement(SQL);
            db.pstmt.setString(1, exportedObjectID);
            db.pstmt.setString(2, exportContainerID);
            db.pstmt.setString(3, importContainerID);
            db.pstmt.setString(4, importSpaceID);
            db.pstmt.setString(5, importedObjectID);
            db.pstmt.setBoolean(6, readOnly);
            db.executePrepared();
        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to store imported object", sqle);
        } finally {
            db.release();
        }
    }
}
