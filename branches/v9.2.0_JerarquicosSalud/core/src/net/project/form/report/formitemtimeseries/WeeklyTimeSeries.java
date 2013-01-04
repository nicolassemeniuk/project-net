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

import java.util.Date;

import net.project.calendar.PnCalendar;
import net.project.security.SessionManager;

/**
 * This object can create the proper SQL to create a Form Item Time Series report
 * with output for each week.  This object shouldn't be instantiated directly.
 * Instead use the variable {@link SQLStatementType#WEEKLY_TIME_SERIES}.
 *
 * This object doesn't really do too much by itself (except for calculating the
 * end of a reporting period.)  It originally was an anonymous inner class
 * created inside of SQLStatement type.  Unfortunately, the SQL got a bit too
 * large and it finally needed to be moved to its own class.
 *
 * @see net.project.form.report.formitemtimeseries.DailyTimeSeries
 * @see net.project.form.report.formitemtimeseries.MonthlyTimeSeries
 * @see net.project.form.report.formitemtimeseries.SQLStatementType
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
class WeeklyTimeSeries extends SQLStatementType {
    /**
     * This SQL is derived from the
     * {@link net.project.form.report.formitemtimeseries.DailyTimeSeries#POSITIVE_DELTA_SQL}
     * variables.  Any changes there should be reflected here.  Full documentation
     * for this query exists there.
     */
    private static String POSITIVE_DELTA_SQL =
        "SELECT "+
        "  cal.cal_date, @@@fieldname@@@ as field_value, count(*) as value_count "+
        "FROM "+
        "  @@@tablename@@@ cd," +
        "  pn_class_instance ci, "+
        "  (SELECT "+
        "     @@@startdate@@@ + (pv.x*7) cal_date "+
        "   FROM "+
        "     pn_pivot pv "+
        "   WHERE "+
        "     pv.x < ((SELECT LEAST (MAX (date_modified) - MIN (date_modified), 10000) "+
        "              FROM @@@tablename@@@))) cal "+
        "WHERE "+
        "  cd.date_modified <= cal.cal_date "+
        "  AND cd.date_modified > cal.cal_date-7 " +
        "  and cd.object_id = ci.class_instance_id " +
        "  and ci.space_id = @@@spaceid@@@ ";
    /**
     * This SQL is derived from the
     * {@link net.project.form.report.formitemtimeseries.DailyTimeSeries#NEGATIVE_DELTA_SQL}
     * variables.  Any changes there should be reflected here.  Full documentation
     * for this query exists there.
     */
    private static String NEGATIVE_DELTA_SQL =
        "SELECT    "+
        "  cal_date, cd.@@@fieldname@@@ AS field_value, -COUNT (*) AS value_count "+
        "FROM      "+
        "  @@@tablename@@@ cd, "+
        "  (SELECT    "+
        "     cal.cal_date, c.object_id, c.previous_version_id, MIN (multi_data_seq) "+
        "   FROM      "+
        "     @@@tablename@@@ c, "+
        "     pn_class_instance ci, "+
        "     (SELECT    "+
        "        @@@startdate@@@ + (pv.x*7) cal_date "+
        "      FROM    "+
        "        pn_pivot pv "+
        "      WHERE   "+
        "        pv.x < ((SELECT LEAST (MAX (date_modified) - MIN (date_modified), 10000) "+
        "                   FROM @@@tablename@@@))) cal "+
        "   WHERE     "+
        "     c.date_modified <= cal.cal_date "+
        "     AND c.date_modified > cal.cal_date - 7 "+
        "     and ci.class_instance_id = c.object_id " +
        "     and ci.space_id = @@@spaceid@@@ " +
        "   GROUP BY  "+
        "     cal.cal_date,  "+
        "     c.object_id,  "+
        "     c.previous_version_id) lo "+
        "WHERE     "+
        "  lo.object_id = cd.object_id  "+
        "  AND lo.previous_version_id = cd.version_id ";
    /**
     * This SQL is derived from the
     * {@link net.project.form.report.formitemtimeseries.DailyTimeSeries#DAILY_DELETED_SQL}
     * variables.  Any changes there should be reflected here.  Full documentation
     * for this query exists there.
     */
    private static String WEEKLY_DELETED_SQL =
        "select "+
        "  cal_date, field_value, -count(*) as delta "+
        "from "+
        "  @@@tablename@@@ cd, "+
        "  (SELECT    "+
        "     cal.cal_date, cd.object_id, cd.@@@fieldname@@@ as field_value,  "+
        "     MAX (cd.multi_data_seq) as multi_data_seq, " +
        "     MAX (cd.version_id) as version_id "+
        "   FROM      "+
        "     pn_class_instance ci, @@@tablename@@@ cd, "+
        "     (SELECT    "+
        "        @@@startdate@@@ + (pv.x*7) cal_date "+
        "      FROM    "+
        "        pn_pivot pv "+
        "      WHERE   "+
        "        pv.x < ((SELECT  "+
        "                   LEAST(MAX(date_modified) - MIN(date_modified),10000) "+
        "                 FROM    "+
        "                   @@@tablename@@@))) cal "+
        "   WHERE     "+
        "     ci.class_instance_id = cd.object_id AND cd.is_current = 1 "+
        "     AND ci.crc < cal.cal_date AND ci.crc >=  cal.cal_date-7 "+
        "     AND class_id = @@@classid@@@ AND record_status = 'D' "+
        "   GROUP BY  "+
        "     cal.cal_date, cd.object_id, cd.@@@fieldname@@@) lo " +
        "where " +
        "  lo.object_id = cd.object_id " +
        "  and lo.version_id = cd.version_id " +
        "  and lo.multi_data_seq = cd.multi_data_seq ";
    /**
     * This field contains the token needed to render the human-readable name
     * for this type of time series.
     */
    private static String DISPLAY_TOKEN = "prm.form.report.formitemtimeseries.parameter.weeklytimeseries.description";

    /**
     * Public constructor to create a new WeeklyTimeSeries object.
     *
     * @param id a <code>int</code> value which uniquely identifies this
     * SQLStatementType among all existing SQLStatementType objects.
     */
    public WeeklyTimeSeries(String id) {
        super(id, POSITIVE_DELTA_SQL, NEGATIVE_DELTA_SQL,
            WEEKLY_DELETED_SQL, DISPLAY_TOKEN);
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
    protected Date fixStartDate(Date originalDate) {
        PnCalendar cal = new PnCalendar(SessionManager.getUser());
        return cal.startOfWeek(originalDate);
    }


}
