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

 package net.project.calendar.metric;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.database.DBBean;
import net.project.metric.MetricsTypeException;
import net.project.metric.QuantityAndWorkMetric;
import net.project.metric.SimpleMetricType;
import net.project.metric.WeekAndMonthMetric;
import net.project.persistence.PersistenceException;
import net.project.personal.metric.PersonalAssignmentMetrics;
import net.project.personal.metric.PersonalAssignmentMetricsType;
import net.project.security.User;
import net.project.util.DateRange;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.xml.document.XMLDocument;

/**
 * Metric representing the quantity and total time for a user's meetings.
 * <p>
 * The metrics include weekly and monthly QuantityAndWorkMetric values
 * <pre>
 * For Example:
 *
 * Item             Week Qty.   Week Work       Month Qty.  Month Work
 * New Assignments     17           43h             64          212h
 * </pre>
 * 
 * @author Philip Dixon
 * @since Version 7.7
 */
public class MeetingAssignmentMetrics extends PersonalAssignmentMetrics {

    /** SQL Query String to load a user's meetings for a specified date range */
    private String SQL = "SELECT " +
        " ce.event_name, " +
        " cea.person_id, " +
        " ce.start_date, " +
        " ce.end_date " +
        "  FROM " +
        " pn_calendar_event ce, " +
        " pn_cal_event_has_attendee cea " +
        " WHERE" +
        " cea.person_id = ? " +
        " and ce.start_date >= ? " +
        " and ce.start_date <= ? " +
        " AND cea.calendar_event_id = ce.calendar_event_id " +
        " AND ce.record_status = 'A'";


    /**
     * Returns a loaded MeetingAssignmentMetrics for specified user.
     *
     * @param user
     * @return MeetingAssignmentMetrics (loaded)
     * @throws PersistenceException if load operation fails.
     */
    public static MeetingAssignmentMetrics makeMeetingMetrics (User user) throws PersistenceException {

        MeetingAssignmentMetrics metrics = new MeetingAssignmentMetrics(user);
        metrics.load();

        return metrics;
    }

    /**
     * Private constructor to create a new Metrics for the specified user
     * <p>
     * Note: This constructor internally sets the Metrics.metricType to <code>MetricsType.PERSONAL_ASSIGNMENT</code.
     *
     * @param user           User for which this Metrics is being created.
     */
    private MeetingAssignmentMetrics (User user) {
        super(user, PersonalAssignmentMetricsType.MEETING);
    }

    /**
     * Loads the weekly and monthly QuantityAndWorkMetrics for a user's meetings.
     * <p>
     * The <code>User</code> is the user specified in the constructor for this object.
     *
     * @throws PersistenceException
     */
    protected void loadMetrics() throws PersistenceException, MetricsTypeException {

        DBBean db = new DBBean();
        PnCalendar calendar = new PnCalendar (getUser());

        try {

            WeekAndMonthMetric metric = new WeekAndMonthMetric();

            metric.setWeek (getMetricByDateRange (db, calendar.startOfWeek(), calendar.endOfWeek(), SimpleMetricType.WEEKLY));
            metric.setMonth (getMetricByDateRange (db, calendar.startOfMonth(), calendar.endOfMonth(), SimpleMetricType.MONTHLY));

            addMetric (metric);

        } catch (SQLException sqle) {
            throw new PersistenceException (sqle);
        } finally {
            db.release();
        }

    }

    /**
     * Return a populated QuantityAndWorkMetric for a user based on the specified date range.
     * This method does not perform a commit.
     *
     * @param db database bean to perform query
     * @param start starting date/time for query
     * @param end ending date/time
     * @param type MetricType
     * @return populated QuantityAndWorkMetric
     * @throws SQLException if db load operation fails
     */
    private QuantityAndWorkMetric getMetricByDateRange (DBBean db, Date start, Date end, SimpleMetricType type) throws SQLException {

        TimeQuantity totalWork = new TimeQuantity(0, TimeQuantityUnit.SECOND);
        int queryIndex = 0;
        int metricQuantity = 0;

        db.prepareStatement(SQL);

        db.pstmt.setString(++queryIndex, getUser().getID());
        db.pstmt.setTimestamp(++queryIndex, new Timestamp(start.getTime()));
        db.pstmt.setTimestamp(++queryIndex, new Timestamp (end.getTime()));

        db.executePrepared();

        for (; db.result.next(); ++metricQuantity) {

            totalWork = totalWork.add (
                calculateTimeDifference(db.result.getTimestamp("end_date"), db.result.getTimestamp("start_date")));
        }

        return (new QuantityAndWorkMetric(metricQuantity, totalWork, type));
    }


    /**
     * Returns the duration of the CalendarEvent.
     * Values calculated by subtracting the end date/time from the start date/time.
     * Note, the calendar event must be loaded.
     * @return <code>TimeQuantity</code> representing the duration in hours.
     */
    private TimeQuantity calculateTimeDifference (Date start, Date end) {

        DateRange dateRange = new DateRange(end, start);
        return (dateRange.getTimeQuantity(TimeQuantityUnit.HOUR, 2));
    }


    /**
     * Add Specific XML properties for this subclass of <code>PersonalAssignmentMetrics</code>
     * @param xml
     */
    protected void addElements (XMLDocument xml) {
        xml.addElement ("DisplayName", PropertyProvider.get("prm.personal.assignmentmetrics.type.meeting"));
    }
}
