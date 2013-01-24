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
package net.project.form.report.formitemtimeseries;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.Finder;
import net.project.base.finder.FinderFilterList;
import net.project.base.finder.GenericFinder;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.util.DateFormat;

/**
 * Object which creates an object which can tell you how many items of a given
 * field value were contained in a form on a given day.  This is the basic
 * information for the form item time series report.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class FormItemTimeSeriesFinder extends Finder {
    /**
     * Token pointing to: "Unable to find table name and column name for form."
     */
    private String UNABLE_TO_FIND_TABLE_AND_COLUMN_ERROR_TOKEN = "prm.form.report.common.unabletofindtableandcolumnerror.message";
    /**
     * The column definition for the "cal_date" column in the query.  To the
     * user, this column is exposed as the "reporting period".
     */
    public static ColumnDefinition REPORTING_PERIOD = new ColumnDefinition("cal_date", "prm.form.report.formitemtimeseries.columndef.reportingperiod.name");

    /**
     * Find the data object required to construct the FormItemTimeSeries report.
     * Given a form id and a field id, the object that will be constructed can
     * tell you how many times each of its unique values appeared on a single
     * day.
     *
     * @param spaceID a <code>String</code> value containing the space id in
     * which this form appears.  Forms were designed with the theoretical
     * ability to appear in more than one space, so this value is important for
     * finding the correct data.
     * @param formID
     * @param fieldID
     * @param type
     * @return
     * @throws PersistenceException
     */
    public FormItemTimeSeriesData findTimeSeries(String spaceID, String formID, String fieldID, SQLStatementType type) throws PersistenceException {
        //1: Find the tablename and column name for this form id and field id
        FormNames fn = findDataStructureNames(spaceID, formID, fieldID);

        //2: Find the earliest date for form items with the given criteria
        Date earliestStartDate = findEarliestFormItemDate(fn.tableName, type);

        //3: Get the initial form item values counts on the earliest date.
        FormItemTimeSeriesData fitsd = getInitialCounts(fn, type, earliestStartDate, spaceID);

        //4: Apply the positive deltas
        getPositiveDeltas(fn, type, earliestStartDate, fitsd, spaceID);

        //5: Apply the negative deltas
        getNegativeDeltas(fn, type, earliestStartDate, fitsd, spaceID);

        //6: Account for items that were deleted
        applyDeletions(fn, type, earliestStartDate, fitsd, formID);

        return fitsd;
    }

    /**
     * Add deltas to the <code>FormItemTimeSeriesData</code> object for every
     * time an item was deleted from from a form.
     *
     * @param formNames an object which contains the form name and field name
     * that we are querying about.
     * @param sqlStatementType a <code>SQLStatementType</code> which is specific
     * the type of time series we are producing (daily, weekly, or monthly.)
     * @param earliestStartDate a <code>Date</code> value containing the
     * earliest date for which we should report information.
     * @param data a <code>FormItemTimeSeriesData</code> object that we are going
     * to store our deltas in.
     * @param formID a <code>String</code> value containing the numeric form id
     * that we are reporting on.
     */
    private void applyDeletions(FormNames formNames, SQLStatementType sqlStatementType,
        Date earliestStartDate, FormItemTimeSeriesData data, String formID) {

        String sqlStatement = sqlStatementType.getDailyDeletedSQL(formNames.tableName,
            formNames.fieldName, earliestStartDate, formID);
        GenericFinder gf = new GenericFinder(sqlStatement);

        DBBean db = new DBBean();
        try {
            gf.addFinderFilterList(new FinderFilterList(finderFilters,  true));
            gf.addGroupByClause("lo.cal_date");
            gf.addGroupByClause("field_value");
            db = gf.find(db);

            while (db.result.next()) {
                if (db.result.getTimestamp("cal_date").after(earliestStartDate)) {
                    data.addDelta(db.result.getTimestamp("cal_date"),
                        db.result.getString("field_value"),
                        new Integer(db.result.getInt("delta")));
                }
            }
        } catch (SQLException sqle) {
        } finally {
            db.release();
        }
    }

    /**
     * Add deltas to the <code>FormItemTimeSeriesData</code> object for every
     * time an item was no longer selected in a form.
     *
     * @param formNames an object which contains the form name and field name
     * that we are querying about.
     * @param sqlStatementType a <code>SQLStatementType</code> which is specific
     * the type of time series we are producing (daily, weekly, or monthly.)
     * @param earliestStartDate a <code>Date</code> value containing the
     * earliest date for which we should report information.
     * @param data a <code>FormItemTimeSeriesData</code> object that we are going
     * to store our deltas in.
     * @param spaceID a <code>String</code> value containing the space id that
     * we are reporting on.  Only form items that were created in this space
     * will be counted.
     */
    private void getNegativeDeltas(FormNames formNames, SQLStatementType sqlStatementType,
        Date earliestStartDate, FormItemTimeSeriesData data, String spaceID) {

        String sqlStatement = sqlStatementType.getNegativeDeltaSQL(formNames.tableName,
            formNames.fieldName, earliestStartDate, spaceID);
        GenericFinder gf = new GenericFinder(sqlStatement);

        DBBean db = new DBBean();
        try {
            gf.addFinderFilterList(new FinderFilterList(finderFilters, true));
            gf.addGroupByClause("cal_date");
            gf.addGroupByClause("cd."+formNames.fieldName);
            db = gf.find(db);

            while (db.result.next()) {
                if (db.result.getTimestamp("cal_date").after(earliestStartDate)) {
                    data.addDelta(db.result.getTimestamp("cal_date"), db.result.getString("field_value"),
                        new Integer(db.result.getInt("value_count")));
                }
            }
        } catch (SQLException sqle) {
        } finally {
            db.release();
        }
    }

    /**
     * Add deltas to the <code>FormItemTimeSeriesData</code> object for every
     * time an item became selected in a form.
     *
     * @param formNames an object which contains the form name and field name
     * that we are querying about.
     * @param sqlStatementType a <code>SQLStatementType</code> which is specific
     * the type of time series we are producing (daily, weekly, or monthly.)
     * @param earliestStartDate a <code>Date</code> value containing the
     * earliest date for which we should report information.
     * @param data a <code>FormItemTimeSeriesData</code> object that we are going
     * to store our deltas in.
     * @param spaceID a <code>String</code> value containing the space id that
     * we are reporting on.  Only form items that were created in this space
     * will be counted.
     */
    private void getPositiveDeltas(FormNames formNames, SQLStatementType sqlStatementType,
        Date earliestStartDate, FormItemTimeSeriesData data, String spaceID) {

        String sqlStatement = sqlStatementType.getPositiveDeltaSQL(formNames.tableName,
            formNames.fieldName, earliestStartDate, spaceID);
        GenericFinder gf = new GenericFinder(sqlStatement);
        DBBean db = new DBBean();
        try {
            gf.addFinderFilterList(new FinderFilterList(finderFilters, true));
            gf.addGroupByClause("cal_date");
            gf.addGroupByClause("cd."+formNames.fieldName);
            db = gf.find(db);

            while (db.result.next()) {
                if (db.result.getTimestamp("cal_date").after(earliestStartDate)) {
                    data.addDelta(db.result.getTimestamp("cal_date"), db.result.getString("field_value"),
                        new Integer(db.result.getInt("value_count")));
                }
            }
        } catch (SQLException sqle) {
        } finally {
            db.release();
        }
    }

    /**
     * Gets the initial count of form items for a given date.
     *
     * @param formNames a <code>FormNames</code> object which contains the name
     * of the form and field we are reporting on.
     * @param sqlStatementType a <code>SQLStatementType</code> object specific
     * to the type of time series (daily, weekly, or monthly) that we are
     * creating.  This object will be used to fetch an appropriate SQL statement.
     * @param dateToReportOn a <code>Date</code> value for which we are going to
     * fetch the counts.
     * @param spaceID a <code>String</code> value containing the numeric id for
     * the space we are reporting on.  Only form data from this space will be
     * shown in the report.
     * @return a <code>FormItemTimeSeriesData</code> object which has been
     * prepopulated with data for the date passed into the <code>dateToReportOn</code>
     * parameter.
     */
    private FormItemTimeSeriesData getInitialCounts(FormNames formNames,
        SQLStatementType sqlStatementType, Date dateToReportOn, String spaceID) {

        FormItemTimeSeriesData data = new FormItemTimeSeriesData();
        DateFormat df = SessionManager.getUser().getDateFormatter();

        String sqlStatement = sqlStatementType.getInitialCountSQL(formNames.tableName, formNames.fieldName, dateToReportOn, spaceID);
        GenericFinder gf = new GenericFinder(sqlStatement);
        DBBean db = new DBBean();
        try {
            gf.addFinderFilterList(new FinderFilterList(finderFilters, true));
            gf.addGroupByClause("cal_date");
            gf.addGroupByClause("cd."+formNames.fieldName);
            db = gf.find(db);
            while (db.result.next()) {
                int fieldValueType = db.result.getMetaData().getColumnType(2);

                String formattedValue;
                Integer valueCount = new Integer(db.result.getInt("value_count"));

                if (fieldValueType == Types.DATE) {
                    //We need to convert to a certain date format so the
                    //forms module's "DateField" object will recognize it.
                    formattedValue = df.formatDate(
                        new Date(db.result.getTimestamp("field_value").getTime()),
                        "M/d/yyyy H:mm");
                } else {
                    formattedValue = db.result.getString("field_value");
                }

                data.addInitialValue(dateToReportOn, formattedValue, valueCount);
            }
        } catch (SQLException sqle) {
        } finally {
            db.release();
        }
        return data;
    }

    /**
     * Finds the earliest date in which a form item appears.
     *
     * @param tableName a <code>String</code> containing the table name for
     * which we are going to look for the earliest form item.
     * @param type a <code>SQLStatementType</code> object which will convert the
     * date from being absolutely correct to being correct for the type of time
     * series (daily, weekly, monthly) that we are running.
     * @return a <code>Date</code> value which contains the earliest time series
     * period in which a form item appeared.
     * @throws PersistenceException if any error occurred which trying to query
     * for the earliest form item.
     */
    public Date findEarliestFormItemDate(String tableName, SQLStatementType type) throws PersistenceException {
        Date earliestDate = type.fixStartDate(new Date());
        //In the next SQL statement, we added an alias for a cal_date column
        //because there the reporting period date filter uses this column name.
        String sqlStatement = "select min(date_modified) as earliest_date from " +
            "(select cd.*, date_modified cal_date from "+tableName+" cd) where 1=1";
        GenericFinder gf = new GenericFinder(sqlStatement);

        DBBean db = new DBBean();
        try {
            gf.addFinderFilterList(new FinderFilterList(finderFilters, true));
            db = gf.find(db);
            if ((db.result.next()) && (db.result.getTimestamp("earliest_date") != null)) {
                earliestDate = type.fixStartDate(db.result.getTimestamp("earliest_date"));
            }
        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to determine earliest form item " +
                "date", sqle);
        } finally {
            db.release();
        }

        return earliestDate;
    }

    /**
     * Find the name of the master table name and the column that we are going
     * to collect and summarize information for.  This is required to create a
     * base SQL statement.
     *
     * @see #getBaseSQLStatement
     */
    private FormNames findDataStructureNames(String spaceID, String formID, String fieldID) {
        FormNames formNames = new FormNames();
        DBBean db = new DBBean();
        try {
            String query =
                "select data_table_name, data_column_name "+
                "  from pn_class_field "+
                //" where space_id = ? and class_id = ? and field_id = ? ";
                " where class_id = ? and field_id = ? ";
            db.prepareStatement(query);
            //db.pstmt.setString(1, spaceID);
            db.pstmt.setString(1, formID);
            db.pstmt.setString(2, fieldID);
            db.executePrepared();

            if (db.result.next()) {
                formNames.tableName = db.result.getString("data_table_name");
                formNames.fieldName = db.result.getString("data_column_name");
            } else {
                throw new RuntimeException(
                    PropertyProvider.get(UNABLE_TO_FIND_TABLE_AND_COLUMN_ERROR_TOKEN));
            }
        } catch (SQLException sqle) {
            throw new RuntimeException(
                PropertyProvider.get(UNABLE_TO_FIND_TABLE_AND_COLUMN_ERROR_TOKEN), sqle);
        } finally {
            db.release();
        }

        return formNames;
    }

    //Unused
    protected String getBaseSQLStatement() { return null; }
    protected Object createObjectForResultSetRow(ResultSet databaseResults) throws SQLException { return null; }
}

class FormNames {
    public String tableName;
    public String fieldName;
}