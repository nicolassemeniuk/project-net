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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import net.project.base.finder.Finder;
import net.project.base.finder.FinderListener;
import net.project.base.finder.FinderListenerAdapter;
import net.project.database.DBBean;

/**
 * Class used to find objects that have been exported from another space.
 *
 * @author Matthew Flower
 * @since 8.2.0
 */
public class ImportFinder extends Finder {
    private static final String SQL_STATEMENT =
        "select " +
        "  s.exported_object_id, " +
        "  s.export_container_id, " +
        "  s.export_space_id, " +
        "  s.imported_object_id, " +
        "  s.import_container_id, " +
        "  s.import_space_id " +
        "from " +
        "  pn_shared s, " +
        "  pn_object o " +
        "where " +
        "  s.import_space_id = o.object_id " +
        "  and o.record_status = 'A'";

    private static final int EXPORTED_OBJECT_ID_COL = 1;
    private static final int EXPORT_CONTAINER_ID_COL = 2;
    private static final int EXPORT_SPACE_ID_COL = 3;
    private static final int IMPORTED_OBJECT_ID_COL = 4;
    private static final int IMPORT_CONTAINER_ID_COL = 5;
    private static final int IMPORT_SPACE_ID_COL = 6;

    protected String getBaseSQLStatement() {
        return SQL_STATEMENT;
    }

    public List findByExportedIDs(DBBean db, List objectIDs) throws SQLException {
        String whereClause = null;
        for (Iterator it = objectIDs.iterator(); it.hasNext();) {
            Object exportID = it.next();

            if (whereClause == null) {
                whereClause = "(";
            }

            whereClause += "s.exported_object_id = " + exportID;

            if (it.hasNext()) {
                whereClause += " or ";
            } else {
                whereClause += ")";
            }
        }
        addWhereClause(whereClause);

        return loadFromDB(db);
    }
    
    
    
    public List findByExportedIDAndNotInPermittedSpace(DBBean db, final String exportID) throws SQLException {
        FinderListener notPermittedSpaceListener = new FinderListenerAdapter() {
            
            public void preConstruct(Finder f) {
                String permittedSpaceQuery = "select permitted_object_id from pn_shareable_permissions where object_id = ? AND share_type = ?";            
                addWhereClause("s.import_space_id not in ( " + permittedSpaceQuery +")");
                addWhereClause("exists ( " + permittedSpaceQuery +")");
            }    
            
            public void preExecute(DBBean db) throws SQLException {
                db.pstmt.setString(1, exportID);
                db.pstmt.setString(2, exportID);
                db.pstmt.setString(3, SharePermissionType.SPACE.getID());
                db.pstmt.setString(4, exportID);
                db.pstmt.setString(5, SharePermissionType.SPACE.getID());
            }
        };
        addFinderListener(notPermittedSpaceListener);
        String whereClause = "s.exported_object_id = ?";
        addWhereClause(whereClause);

        return loadFromDB(db);
    }

    protected Object createObjectForResultSetRow(ResultSet results) throws SQLException {
        ImportedObject imp = new ImportedObject();
        imp.setExportedObjectID(results.getString(EXPORTED_OBJECT_ID_COL));
        imp.setExportContainerID(results.getString(EXPORT_CONTAINER_ID_COL));
        imp.setExportSpaceID(results.getString(EXPORT_SPACE_ID_COL));
        imp.setImportedObjectID(results.getString(IMPORTED_OBJECT_ID_COL));
        imp.setImportContainerID(results.getString(IMPORT_CONTAINER_ID_COL));
        imp.setImportSpaceID(results.getString(IMPORT_SPACE_ID_COL));

        return imp;
    }
}
