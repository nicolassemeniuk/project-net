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
package net.project.form.report.formitemtimeseries;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.util.DateUtils;

/**
 * A typed enumeration of SQLStatementType classes.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public abstract class SQLStatementType {
    /** This list contains all of the possible types for this typed enumeration. */
    private static List types = new ArrayList();
    /**
     * This is the SQL statement to be used if we wish to group the form count
     * information by day.
     */
    public static SQLStatementType DAILY_TIME_SERIES = new DailyTimeSeries("10");
    /**
     * This is the SQL statement to be used if we wish to group the form count
     * information by week.
     */
    public static SQLStatementType WEEKLY_TIME_SERIES = new WeeklyTimeSeries("20");
    /**
     * This is the SQL statement to be used if we wish to group the form count
     * information by month.
     */
    public static SQLStatementType MONTHLY_TIME_SERIES = new MonthlyTimeSeries("30");
    /**
     * This SQL Statement is returned by getForID if the id parameter does not
     * match the ID indicated.  It also serves the as the time series that
     * should be used if the user hasn't selected one and you don't know which
     * one to choose.
     */
    public static final SQLStatementType DEFAULT = WEEKLY_TIME_SERIES;

    /**
     * Get the SQLStatementType that corresponds to a given ID.
     *
     * @param id a <code>String</code> value which is the primary key of a
     * <code>SQLStatementType</code> we want to find.
     * @return a <code>SQLStatementType</code> corresponding to the supplied ID, or
     * the DEFAULT <code>SQLStatementType</code> if one cannot be found.
     */
    public static SQLStatementType getForID(String id) {
        SQLStatementType toReturn = DEFAULT;

        for (Iterator it = types.iterator(); it.hasNext();) {
            SQLStatementType type = (SQLStatementType)it.next();
            if (type.getID().equals(id)) {
                toReturn = type;
                break;
            }
        }

        return toReturn;
    }

    //**************************************************************************
    // Implementation code
    //**************************************************************************
    /** A unique identifier for this SQLStatementType. */
    private String id;
    /** The token used to find a human-readable name for this SQLStatementType. */
    private String displayToken;
    /**
     * There are a number of things worth noting about this query because it is
     * more complex than it appears:
     *
     * 1. In the inner query, we are selecting the latest version ID because we
     * are trying to determine what was selected at the end of the day.  This is
     * the best bet we have for finding that information.
     * 2. To join the inner query to the outer query, we use the object ID and
     * the version id.  We do not however use the multi_data_seq number.  This
     * is intentional.  In the inner query, we don't want to find the latest
     * version for every multi_data_seq.  If we did that, we wouldn't be able to
     * detect when someone decided to choose 2 columns in a field that previously
     * had three columns selected.  (We detect the 2 columns that the last selected
     * plus the third that they selected earlier.)
     *
     * <b>Expected Results</b>
     * This query will produce a series of rows with three columns.  The columns
     * are cal_date, field_value, and value_count.  For each row, you will see
     * how many times a certain field value appeared on that date.
     */
    private String initialCountSQL =
        "select "+
        "  cal_date, cd.@@@fieldname@@@ AS field_value, COUNT (*) AS value_count "+
        "from "+
        "  @@@tablename@@@ cd, "+
        "  (select "+
        "     @@@startdate@@@ AS cal_date, "+
        "     c.object_id, "+
        "     MAX (c.version_id) AS version_id "+
        "   from "+
        "     @@@tablename@@@ c, "+
        "     pn_class_instance ci "+
        "   where "+
        "     c.date_modified <= @@@startdate@@@ "+
        "     and ci.class_instance_id = c.object_id "+
        "     and ci.space_id = @@@spaceid@@@ " +
        "     and (ci.record_status = 'A' OR (ci.record_status = 'D' and ci.CRC > c.date_modified)) "+
        "   group by "+
        "     @@@startdate@@@, "+
        "     c.object_id) lo "+
        "WHERE "+
        "  lo.object_id = cd.object_id " +
        "  AND lo.version_id = cd.version_id ";

    /**
     * The base SQL statement to produce the positive field value deltas for a
     * given day.
     */
    private String positiveDeltaSQL;
    /**
     * The base SQL statement to produce the negative field value deltas for a
     * given day.
     */
    private String negativeDeltaSQL;
    /**
     * Base SQL statement to produce the number of items that were deleted in a
     * given time period.
     */
    private String dailyDeletedSQL;

    /**
     * Private constructor which creates a new SQLStatementType instance.
     *
     * @param id a <code>String</code> value which is a unique identifier for
     * this typed enumeration.
     * {@link #getInitialCountSQL} method.
     * @param displayToken a <code>String</code> value which contains a token to
     * look up the display name for this type.
     */
    protected SQLStatementType(String id, String positiveDeltaSQL,
        String negativeDeltaSQL, String dailyDeletedSQL, String displayToken) {
        this.id = id;
        this.displayToken = displayToken;
        this.positiveDeltaSQL = positiveDeltaSQL;
        this.negativeDeltaSQL = negativeDeltaSQL;
        this.dailyDeletedSQL = dailyDeletedSQL;
        types.add(this);
    }

    /**
     * Given the name of the form table and the column name of a form field,
     * construct the correct SQL statement to return the count of form items
     * that existing at the end of the "earliestDate" parameter.
     *
     * @param tableName a <code>String</code> containing the master table name
     * for the form.
     * @param fieldName a <code>String</code> containing the column name for the
     * form field.
     * @return a <code>String</code> containing a well-formed SQL statement.
     */
    public String getInitialCountSQL(String tableName, String fieldName, Date earliestDate, String spaceID) {
        String toReturn = initialCountSQL;
        toReturn = toReturn.replaceAll("@@@fieldname@@@", fieldName);
        toReturn = toReturn.replaceAll("@@@tablename@@@", tableName);
        toReturn = toReturn.replaceAll("@@@spaceid@@@", spaceID);
        toReturn = toReturn.replaceAll("@@@startdate@@@",
            DateUtils.getDatabaseDateString(earliestDate));
        return toReturn;
    }

    /**
     * Given some variables, produce the correct SQL statement to return the
     * count of items added during each time period.
     *
     * @param tableName a <code>String</code> value containing the name of the
     * form table we are querying about.
     * @param fieldName a <code>String</code> value containing the name of the
     * field for the form table we are querying about.
     * @param earliestDate a <code>Date</code> value containing the earliest
     * date for which we are going to return information.
     * @param spaceID a <code>String</code> value containing the space id.  All
     * information returned will only be for forms in this particular space id.
     * @return a <code>String</code> containing a SQL statement which will
     * return the count of items added during each time period.
     */
    public String getPositiveDeltaSQL(String tableName, String fieldName, Date earliestDate, String spaceID) {
        String toReturn = positiveDeltaSQL;
        toReturn = toReturn.replaceAll("@@@fieldname@@@", fieldName);
        toReturn = toReturn.replaceAll("@@@tablename@@@", tableName);
        toReturn = toReturn.replaceAll("@@@spaceid@@@", spaceID);
        toReturn = toReturn.replaceAll("@@@startdate@@@",
            DateUtils.getDatabaseDateString(earliestDate));
        return toReturn;
    }

    /**
     * Given some variables, produce the correct SQL statement to return the
     * count of items removed during each time period.
     *
     * @param tableName a <code>String</code> value containing the name of the
     * form table we are querying about.
     * @param fieldName a <code>String</code> value containing the name of the
     * field for the form table we are querying about.
     * @param earliestDate a <code>Date</code> value containing the earliest
     * date for which we are going to return information.
     * @param spaceID a <code>String</code> value containing the space id.  All
     * information returned will only be for forms in this particular space id.
     * @return a <code>String</code> containing a SQL statement which will
     * return the count of items removed during each time period.
     */
    public String getNegativeDeltaSQL(String tableName, String fieldName, Date earliestDate, String spaceID) {
        String toReturn = negativeDeltaSQL;
        toReturn = toReturn.replaceAll("@@@fieldname@@@", fieldName);
        toReturn = toReturn.replaceAll("@@@tablename@@@", tableName);
        toReturn = toReturn.replaceAll("@@@spaceid@@@", spaceID);
        toReturn = toReturn.replaceAll("@@@startdate@@@",
            DateUtils.getDatabaseDateString(earliestDate));
        return toReturn;
    }

    /**
     * Given some variables, produce the correct SQL statement to return the
     * count of items deleted during each time period.
     *
     * @param tableName a <code>String</code> value containing the name of the
     * form table we are querying about.
     * @param fieldName a <code>String</code> value containing the name of the
     * field for the form table we are querying about.
     * @param earliestDate a <code>Date</code> value containing the earliest
     * date for which we are going to return information.
     * @param classID a <code>String</code> value containing the numeric id for
     * this form.
     * @return a <code>String</code> containing a SQL statement which will
     * return the count of items deleted during each time period.
     */
    public String getDailyDeletedSQL(String tableName, String fieldName, Date earliestDate, String classID) {
        String toReturn = dailyDeletedSQL;
        toReturn = toReturn.replaceAll("@@@fieldname@@@", fieldName);
        toReturn = toReturn.replaceAll("@@@tablename@@@", tableName);
        toReturn = toReturn.replaceAll("@@@classid@@@", classID);
        toReturn = toReturn.replaceAll("@@@startdate@@@",
            DateUtils.getDatabaseDateString(earliestDate));
        return toReturn;
    }

    /**
     * How the start date is determined varies with each SQLStatementType.  This
     * method, which is abstract and must be overridden by every SQLStatement
     * type, allows the correct start date to be determined.
     *
     * @param originalDate a <code>Date</code> object indicating the earliest
     * "Date Modified" found in the form.
     * @return a <code>Date</code> which indicates the correct date to start
     * summarizing form data.
     */
    protected abstract Date fixStartDate(Date originalDate);

    /**
     * Get the unique identifier for this type enumeration.
     *
     * @return a <code>String</code> value containing the unique id for this
     * type.
     */
    public String getID() {
        return id;
    }

    /**
     * Return a human-readable display name for this SQLStatementType.
     *
     * @return a <code>String</code> containing a human-readable version of this
     * SQLStatementType.
     */
    public String toString() {
        return PropertyProvider.get(displayToken);
    }
}
