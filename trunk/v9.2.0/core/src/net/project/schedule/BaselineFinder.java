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
package net.project.schedule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.project.base.finder.Finder;
import net.project.database.DatabaseUtils;
import net.project.persistence.PersistenceException;

/**
 * Find baselines.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class BaselineFinder extends Finder {
    private static final String SQL =
        "select " +
        "  b.baseline_id, " +
        "  b.object_id, " +
        "  b.name, " +
        "  b.date_created, " +
        "  b.date_modified, " +
        "  b.record_status, " +
        "  b.description," +
        "  b.is_default_for_object " +
        "from " +
        "  pn_baseline b " +
        "where " +
        "  b.record_status = 'A' ";

    private static final int ID_COL = 1;
    private static final int OBJECT_ID_COL = 2;
    private static final int NAME_COL = 3;
    private static final int DATE_CREATED_COL = 4;
    private static final int DATE_MODIFIED_COL = 5;
    private static final int RECORD_STATUS_COL = 6;
    private static final int DESCRIPTION_COL = 7;
    private static final int IS_DEFAULT_FOR_OBJECT_COL = 8;

    /**
     * Get the SQL statement which without any additional where clauses, group
     * by, or order by statements. <p> The SQL statement will include a
     * <code>SELECT</code> part, a <code>FROM</code> part and the
     * <code>WHERE</code> keyword.  It will include any conditional expressions
     * required to perform joins. All additional conditions will be appended
     * with an <code>AND</code> operator. </p>
     *
     * @return a <code>String</code> value containing the default sql statement
     *         without any additional adornments.
     */
    protected String getBaseSQLStatement() {
        return SQL;
    }

    /**
     * Find all matching baselines.
     *
     * @return a <code>List</code> containing zero or more baseline objects.
     * @throws PersistenceException if there is a problem loading from the
     * database.
     */
    public List findAll() throws PersistenceException {
        return loadFromDB();
    }


    /**
     * Finds a baseline with a given id.
     *
     * @param baselineID a <code>String</code> containing the primary key of the
     * baseline.
     * @return a <code>Baseline</code> that has been populated.
     */
    public Baseline findBaseline(String baselineID) throws PersistenceException {
        addWhereClause("b.baseline_id = "+baselineID);

        Baseline baseline = new Baseline();
        if (loadFromDB(baseline)) {
            return baseline;
        } else {
            return null;
        }
    }

    public List findForObjectID(String objectID) throws PersistenceException {
        addWhereClause("b.object_id = " + objectID);
        return loadFromDB();
    }

    /**
     * Populate a domain object with data specific to the query result.  For
     * example, a task finder would populate a {@link Task}
     * object.  Any class that extends the finder base class needs to implement
     * this method the finder can use its build-in loadFromDB method to load
     * objects.
     *
     * @param rs a <code>ResultSet</code> that provides the data
     * necessary to populate the domain object.  The ResultSet is assumed to be
     * on a current row.
     * @return a <code>Object</code> subclass specific to your finder that has
     *         been populated with data.
     * @throws java.sql.SQLException if an error occurs populating the object.
     */
    protected Object createObjectForResultSetRow(ResultSet rs) throws SQLException {
        return createObjectForResultSetRow(rs, new Baseline());
    }

    /**
     * Populate a domain object with data specific to the query result.  For
     * example, a task finder would populate a {@link Task}
     * object.  This particular version of the createObjectForResultSetRow
     * method is available so the user can fill up an preexisting object with
     * data more easily.
     *
     * @param rs a <code>ResultSet</code> that provides the data
     * necessary to populate the domain object.  The ResultSet is assumed to be
     * on a current row.
     * @param object a preexisting object that needs to be filled with data.
     * @return a <code>Object</code> subclass specific to your finder that has
     *         been populated with data.
     * @throws java.sql.SQLException if an error occurs populating the object.
     */
    protected Object createObjectForResultSetRow(ResultSet rs, Object object) throws SQLException {
        Baseline b = (Baseline)object;

        b.setID(rs.getString(ID_COL));
        b.setBaselinedObjectID(rs.getString(OBJECT_ID_COL));
        b.setName(rs.getString(NAME_COL));
        b.setCreationDate(DatabaseUtils.getTimestamp(rs, DATE_CREATED_COL));
        b.setModifiedDate(DatabaseUtils.getTimestamp(rs, DATE_MODIFIED_COL));
        b.setRecordStatus(rs.getString(RECORD_STATUS_COL));
        b.setDescription(rs.getString(DESCRIPTION_COL));
        b.setDefaultForObject(rs.getBoolean(IS_DEFAULT_FOR_OBJECT_COL));

        return b;
    }
}
