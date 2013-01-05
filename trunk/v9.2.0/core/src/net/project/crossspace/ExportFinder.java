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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.project.base.finder.Finder;
import net.project.database.DatabaseUtils;
import net.project.persistence.PersistenceException;

/**
 * A class used to find exported objects.
 *
 * @author Matthew Flower
 * @since Version 8.2.0
 */
public class ExportFinder extends Finder {
    private String BASE_SQL_STATEMENT =
        "select "+
        "  s.object_id as exported_object_id, "+
        "  o.object_type as exported_object_type, "+
        "  pon.name as exported_object_name, "+
        "  s.permission_type, "+
        "  s.propagate_to_children, " +
        "  s.allowable_actions, " +
        "  spacename.object_id exported_object_space_id, "+
        "  spacename.name exported_object_space_name, "+
        "  sp.permitted_object_id as permitted_object_id , "+
        "  permitted_names.name as permitted_object_name, "+
        "  permitted_object.object_type as permitted_object_type "+
        "from "+
        "  pn_shareable s, "+
        "  pn_object_name pon, "+
        "  pn_object o, "+
        "  pn_shareable_permissions sp, "+
        "  pn_object_name permitted_names, "+
        "  pn_object permitted_object, "+
        "  pn_object_name spacename "+
        "where "+
        "  s.object_id = pon.object_id "+
        "  and pon.object_id = o.object_id "+
        "  and o.record_status = 'A' "+
        "  and s.space_id = spacename.object_id(+) "+
        "  and s.object_id = sp.object_id(+) "+
        "  and sp.permitted_object_id = permitted_object.object_id(+) "+
        "  and sp.permitted_object_id = permitted_names.object_id(+) ";

    private int EXPORTED_OBJECT_ID_COL = 1;
    private int EXPORTED_OBJECT_TYPE_COL = 2;
    private int EXPORTED_OBJECT_NAME_COL = 3;
    private int PERMISSION_TYPE_COL = 4;
    private int PROPAGATE_TO_CHILDREN_COL = 5;
    private int ALLOWABLE_ACTIONS_COL = 6;
    private int EXPORTED_OBJECT_SPACE_ID_COL = 7;
    private int EXPORTED_OBJECT_SPACE_NAME_COL = 8;
    private int PERMITTED_OBJECT_ID = 9;
    private int PERMITTED_OBJECT_NAME = 10;
    private int PERMITTED_OBJECT_TYPE = 11;

    /**
     * The nature of our SQL Statement is such that each new row won't necessarily
     * result in a new unique object.  We keep a local copy of objects to make it
     * easier to update them if a non-unique row comes up.
     */
    private Map localEntriesCopy = new HashMap();

    /**
     * Get the SQL statement which without any additional where clauses, group by, or
     * order by statements.
     * <p/>
     * The SQL statement will include a <code>SELECT</code> part, a <code>FROM</code>
     * part and the <code>WHERE</code> keyword.  It will include any conditional
     * expressions required to perform joins. All additional conditions will
     * be appended with an <code>AND</code> operator.
     * </p>
     *
     * @return a <code>String</code> value containing the default sql statement
     *         without any additional adornments.
     */
    protected String getBaseSQLStatement() {
        return BASE_SQL_STATEMENT;
    }

    /**
     * Populate a domain object with data specific to the query result.  For
     * example, a task finder would populate a {@link net.project.schedule.Task}
     * object.  Any class that extends the finder base class needs to implement
     * this method the finder can use its build-in loadFromDB method to load
     * objects.
     *
     * @param result a <code>ResultSet</code> that provides the data
     *               necessary to populate the domain object.  The ResultSet is assumed
     *               to be on a current row.
     * @return a <code>Object</code> subclass specific to your finder that has
     *         been populated with data.
     * @throws java.sql.SQLException if an error occurs populating the object.
     */
    protected Object createObjectForResultSetRow(ResultSet result) throws SQLException {
        String shareID = result.getString(EXPORTED_OBJECT_ID_COL);
        ExportedObject currentExportInfo;
        Object toReturn;

        /*
        Determine if an extry has already been created for this share id.  If
        not create one.  If so, get a local copy.
        */
        if (localEntriesCopy.containsKey(shareID)) {
            currentExportInfo = (ExportedObject) localEntriesCopy.get(shareID);
            toReturn = this.NON_UNIQUE_ROW;
        } else {
            currentExportInfo = new ExportedObject();
            currentExportInfo.setID(shareID);
            currentExportInfo.setName(result.getString(EXPORTED_OBJECT_NAME_COL));
            currentExportInfo.setObjectType(result.getString(EXPORTED_OBJECT_TYPE_COL));
            currentExportInfo.setPermissionType(TradeAgreement.getForID(result.getString(PERMISSION_TYPE_COL)));
            currentExportInfo.setPropagateToChildren(result.getBoolean(PROPAGATE_TO_CHILDREN_COL));
            currentExportInfo.setAllowableActions(AllowableActionCollection.construct(result.getInt(ALLOWABLE_ACTIONS_COL)));
            localEntriesCopy.put(shareID, currentExportInfo);

            toReturn = currentExportInfo;
        }

        //Now get the name of the space that is sharing this share, if
        //one exists.
        String sharingSpaceID = result.getString(EXPORTED_OBJECT_SPACE_ID_COL);
        if (sharingSpaceID != null) {
            ExportingSpace exportingSpace = new ExportingSpace();
            exportingSpace.id = sharingSpaceID;
            exportingSpace.name = result.getString(EXPORTED_OBJECT_SPACE_NAME_COL);

            //Add the sharing space to the list of sharing spaces
            if (currentExportInfo.getSharingSpaces() == null) {
                currentExportInfo.setSharingSpaces(new ArrayList());
            }
            currentExportInfo.getSharingSpaces().add(exportingSpace);
        }

        String permittedObjectType = result.getString(PERMITTED_OBJECT_TYPE);
        if (permittedObjectType != null) {
            String permittedObjectID = result.getString(PERMITTED_OBJECT_ID);
            String permittedObjectName = result.getString(PERMITTED_OBJECT_NAME);
            PermittedObject po = new PermittedObject(permittedObjectID,
                    permittedObjectType, permittedObjectName);

            if (permittedObjectType.equals("person")) {
                currentExportInfo.getPermittedUsers().add(po);
            } else {
                currentExportInfo.getPermittedSpaces().add(po);
            }
        }

        return toReturn;
    }

    /**
     * Find all exported objects that match the given id.
     */
    public List findByID(List id) throws PersistenceException {
        String csvString = DatabaseUtils.collectionToCSV(id);
        addWhereClause("(s.object_id in ("+csvString+"))");

        return loadFromDB();
    }

    public static class PermittedObject {
        String objectID;
        String objectType;
        String objectName;

        public PermittedObject(String objectID, String objectType, String objectName) {
            this.objectID = objectID;
            this.objectType = objectType;
            this.objectName = objectName;
        }

        public String getObjectID() {
            return objectID;
        }

        public String getObjectType() {
            return objectType;
        }

        public String getObjectName() {
            return objectName;
        }
        
		public boolean equals(Object obj) {
			try {
				return objectID.equals(((PermittedObject) obj).objectID);
			} catch (RuntimeException stupidJavaException) {
				return false;
			}
		}
    }
}
