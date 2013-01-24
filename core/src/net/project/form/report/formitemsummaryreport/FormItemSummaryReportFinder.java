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
|   $Revision: 19065 $
|       $Date: 2009-04-05 16:20:18 -0300 (dom, 05 abr 2009) $
|     $Author: vivana $
|
+-----------------------------------------------------------------------------*/
package net.project.form.report.formitemsummaryreport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.project.base.finder.Finder;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.form.FormElement;
import net.project.persistence.PersistenceException;
import net.project.schedule.Task;
import net.project.security.SessionManager;
import net.project.util.DateFormat;

/**
 * Class to look up data from the database that is used to create the form item
 * summary report.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
class FormItemSummaryReportFinder extends Finder {
    /** ID of the space where the form and field exist and where we are calling the report from. */
    private String spaceID;
    /** ID of the form where the field we are reporting on resides. */
    private String formID;
    /** ID of the field that we are reporting on. */
    private String fieldID;
    /**
     * Name of the table where data for the form identified by {@link #formID}
     * is stored.
     */
    private String masterTableName;
    /**
     * Name of the column in {@link #masterTableName} where the data for
     * {@link #fieldID} is being stored.
     */
    private String columnName;
    /**
     * This SQL statement will be used to construct the SQL statement we will
     * return to the finder architecture.  We will be replacing the fieldname
     * and tablename tokens prior to returning it.
     */
    private String BASE_SQL_STATEMENT =
        "select "+
        "  @@@fieldname@@@ as field_value, "+
        "  count(*) as value_count "+
        "from "+
        "  @@@tablename@@@ cd, "+
        "  pn_class_instance ci "+
        "where "+
        "  cd.is_current = 1 "+
        "  and cd.object_id = ci.class_instance_id " +
        "  and ci.space_id = @@@spaceid@@@ "+
        "  and ci.record_status = 'A' ";

    /**
     * Token pointing to: "Unable to find table name and column name for form."
     */
    private String UNABLE_TO_FIND_TABLE_AND_COLUMN_ERROR_TOKEN = "prm.form.report.common.unabletofindtableandcolumnerror.message";

    /**
     * Get the SQL statement without any additional where clauses, group by, or
     * order by statements.
     *
     * @return a <code>String</code> value containing the default sql statement
     * without any additional adornments.
     */
    protected String getBaseSQLStatement() {
        //First, we are going to need to do a query to find the table name and
        //column name based on the ID's that we have been provided.
        findDataStructureNames();

        String sqlStatement = BASE_SQL_STATEMENT;
        try {
            sqlStatement = sqlStatement.replaceAll("@@@fieldname@@@", columnName);
            sqlStatement = sqlStatement.replaceAll("@@@tablename@@@", masterTableName);
            sqlStatement = sqlStatement.replaceAll("@@@spaceid@@@", spaceID);
            addGroupByClause(columnName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sqlStatement;
    }

    /**
     * Find the name of the master table name and the column that we are going
     * to collect and summarize information for.  This is required to create a
     * base SQL statement.
     *
     * @see #getBaseSQLStatement
     */
    private void findDataStructureNames() {
        DBBean db = new DBBean();
        try {
            String query =
                "select "+
                "  data_table_name, "+
                "  data_column_name "+
                "from "+
                "  pn_class_field "+
                "where "+
                //"  space_id = ? and "+
                "  class_id = ? "+
                "  and field_id = ? ";
            db.prepareStatement(query);
            //db.pstmt.setString(1, spaceID);
            db.pstmt.setString(1, formID);
            db.pstmt.setString(2, fieldID);
            db.executePrepared();
            if (db.result.next()) {
                masterTableName = db.result.getString("data_table_name");
                columnName = db.result.getString("data_column_name");
            } else {
                throw new RuntimeException(
                    PropertyProvider.get(UNABLE_TO_FIND_TABLE_AND_COLUMN_ERROR_TOKEN));
            }
        } catch (SQLException sqle) {
            throw new RuntimeException(
                PropertyProvider.get(UNABLE_TO_FIND_TABLE_AND_COLUMN_ERROR_TOKEN)
                , sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Find summary counts indicating every possible value of the field that is
     * currently active and how many times that field is used.
     *
     * @param spaceID a <code>String</code> value indicating the primary key of
     * the space that the form resides in.
     * @param formID a <code>String</code> value indicating the form where the
     * field value resides that we are reporting on.
     * @param fieldID a <code>String</code> value indicating the primary key of
     * the field that we are going to report the unique values of.
     * @return a <code>java.util.List</code> object containing zero or more
     * {@link net.project.form.report.formitemsummaryreport.SummarizedFieldData}
     * objects.  In reality, if there is more than one, we are probably in
     * trouble, as this method shouldn't be able to return two fields for one
     * field id.
     * @throws PersistenceException if a database error occurs while loading the
     * data from the database.
     */
    public List findFieldSummary(String spaceID, String formID, String fieldID) throws PersistenceException {
        textFieldHandling(fieldID);

        this.spaceID = spaceID;
        this.formID = formID;
        this.fieldID = fieldID;
        return loadFromDB();
    }

    /**
     * Determine if this is a text field we are looking at.  If so, we must
     * only look at the first row.  (The others are just additional text which
     * we aren't going to compare)
     *
     * @param fieldID a <code>String</code> containing the fieldID of the field
     * we are summarizing.
     */
    private void textFieldHandling(String fieldID) throws PersistenceException {
        DBBean db = new DBBean();
        try {
            db.prepareStatement("select element_id from pn_class_field where field_id = ?");
            db.pstmt.setString(1, fieldID);
            db.executePrepared();

            if (db.result.next()) {
                int fieldType = db.result.getInt("element_id");

                if (fieldType == FormElement.TEXT_ELEMENT || fieldType == FormElement.TEXTAREA_ELEMENT) {
                    addWhereClause("cd.multi_data_seq = 0");
                }
            }
        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to determine field type " +
                "for field.", sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Populate a domain object which data specific to the query result.  For
     * example, a task finder would populate a {@link Task}
     * object.  Any class that extends the finder base class needs to implement
     * this method the finder can use its build-in loadFromDB method to load
     * objects.
     *
     * @param rs a <code>ResultSet</code> that provides the data
     * necessary to populate the domain object.
     * @return a <code>Object</code> subclass specific to your finder that has
     * been populated with data.
     * @throws SQLException if an error occurs populating the object.
     */
    protected Object createObjectForResultSetRow(ResultSet rs) throws SQLException {
        SummarizedFieldData sfd = new SummarizedFieldData();
        Map map = sfd.getFieldValues();

        //Iterate through all of the values that the query returned -- they are
        //all going into one object.
        do {
            String formattedValue;
            Integer valueCount;
            DateFormat df = SessionManager.getUser().getDateFormatter();
            int columnType = rs.getMetaData().getColumnType(1);

            valueCount = new Integer(rs.getInt("value_count"));
            if (columnType == Types.DATE && rs.getString("field_value") != null) {
                formattedValue = df.formatDate(new Date(rs.getTimestamp("field_value").getTime()), "M/d/yyyy H:mm");
            } else {
                formattedValue = rs.getString("field_value");
            }

            map.put(formattedValue, valueCount);
        } while (rs.next());

        return sfd;
    }
}
