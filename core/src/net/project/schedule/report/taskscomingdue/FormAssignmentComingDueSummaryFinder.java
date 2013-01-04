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
package net.project.schedule.report.taskscomingdue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.project.base.finder.Finder;
import net.project.base.finder.FinderListener;
import net.project.base.finder.FinderListenerAdapter;
import net.project.calendar.PnCalendar;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;

public class FormAssignmentComingDueSummaryFinder extends Finder {

	  /**
     * Private String that contains the basic report.  Be careful about changing
     * this String, it is used by all of the finder methods below, as well as by
     * the Filtering classes that can be found in the same directory as this file.
     */
    private String BASE_SELECT_STATEMENT =
        "SELECT " +
        "  a.space_id, " +
        "  count(a.object_id) assignment_count, " +
        "  count(tc.completed_assignment_id) completed, " +
        "  count(tomorrow.due_tomorrow_assignment_id) due_tomorrow, " +
        "  count(n7.due_next_7_assignment_id) due_next_7, " +
        "  count(nm.due_next_month_assignment_id) due_next_month, " +
        "  count(uc.uncompleted_assignment_id) uncompleted, " +
        "  count(today.due_today_assignment_id) due_today " +
        "FROM " +
        "  pn_assignment a, " +
        "  pn_object o, " +
        "  (select a2.object_id completed_assignment_id, a2.status_id completed_status_id from pn_assignment a2 where a2.percent_complete >= 1) tc, " +
        "  (select a3.object_id due_tomorrow_assignment_id, a3.status_id tomorrow_status_id from pn_assignment a3 where a3.percent_complete < 1 and end_date >= ? and end_date < ?) tomorrow, " +
        "  (select a4.object_id due_next_7_assignment_id, a4.status_id due_next_7_status_id from pn_assignment a4 where a4.percent_complete < 1 and end_date >= ? and end_date < ?) n7, " +
        "  (select a5.object_id due_next_month_assignment_id, a5.status_id due_next_month_status_id from pn_assignment a5 where a5.percent_complete < 1 and end_date >= ? and end_date < ?) nm, " +
        "  (select a6.object_id uncompleted_assignment_id, a6.status_id uncompleted_status_id from pn_assignment a6 where a6.percent_complete < 1 and end_date >= ?) uc, " +
        "  (select a7.object_id due_today_assignment_id, a7.status_id due_today_status_id from pn_assignment a7 where a7.percent_complete < 1 and end_date >= ? and end_date < ?) today " +
        "WHERE " +
        "  a.object_id = o.object_id " +
        "  and o.object_type = 'form_data'  " +
        "  and o.record_status = 'A'  " +
        "  and tc.completed_assignment_id(+) = a.object_id " +
        "  and tc.completed_status_id(+) = a.status_id " +
        "  and tomorrow.due_tomorrow_assignment_id(+) = a.object_id " +
        "  and tomorrow.tomorrow_status_id(+) = a.status_id " +
        "  and n7.due_next_7_assignment_id(+) = a.object_id " +
        "  and n7.due_next_7_status_id(+) = a.status_id " +
        "  and nm.due_next_month_assignment_id(+) = a.object_id " +
        "  and nm.due_next_month_status_id(+) = a.status_id " +
        "  and uc.uncompleted_assignment_id(+) = a.object_id " +
        "  and uc.uncompleted_status_id(+) = a.status_id " +
        "  and today.due_today_assignment_id(+) = a.object_id " +
        "  and today.due_today_status_id(+) = a.status_id " +
        "  and a.percent_complete != 1 "+
        "  and a.record_status = 'A' ";

    private FinderListener listener = new FinderListenerAdapter() {
        /**
         * This method is called prior to doing anything in the getSQLStatement()
         * method.  No sorters or filters have been gathered, nor has the
         * <code>getBaseSQLStatement</code> been called.
         *
         * This method was created to allow Finders to add "group by" statements
         * that are required for the sql statement to function properly, although
         * it can be used for much more.
         *
         * @param f a <code>Finder</code> that is about to call its own
         * {@link Finder#getSQLStatement} method.
         */
        public void preConstruct(Finder f) {
            if (!groupByClauses.contains("a.space_id")) {
                addGroupByClause("a.space_id");
            }
        }

        /**
         * This method allows a class to alter a finder query just before the
         * executeQuery method is called.
         *
         * @param db a <code>DBBean</code> which will be executed after the
         * execution of this method is completed.
         * @throws SQLException if any error occurs while manipulating the DBBean
         * object.
         */
        public void preExecute(DBBean db) throws SQLException {
            PnCalendar cal = new PnCalendar(SessionManager.getUser());

            //Midnight of today
            cal.setTime(new Date());
            Timestamp today = new Timestamp(cal.startOfDay(cal.getTime()).getTime());

            //Midnight of tomorrow
            cal.setTime(today);
            cal.add(Calendar.DATE, 1);
            Timestamp tomorrow = new Timestamp(cal.getTime().getTime());

            //Midnight of day after tomorrow
            cal.setTime(tomorrow);
            cal.add(Calendar.DATE, 1);
            Timestamp dayAfterTomorrow = new Timestamp(cal.getTime().getTime());

            //Get 7 days ahead
            cal.setTime(today);
            cal.add(Calendar.DATE, 7);
            Timestamp sevenDaysAhead = new Timestamp(cal.getTime().getTime());

            //Get Next Month
            cal.setTime(today);
            cal.setTime(cal.startOfDay());
            cal.add(Calendar.MONTH, 1);
            Timestamp nextMonth = new Timestamp(cal.getTime().getTime());

            //Set the parameters for the query
            db.pstmt.setTimestamp(1, tomorrow);
            db.pstmt.setTimestamp(2, dayAfterTomorrow);
            db.pstmt.setTimestamp(3, today);
            db.pstmt.setTimestamp(4, sevenDaysAhead);
            db.pstmt.setTimestamp(5, today);
            db.pstmt.setTimestamp(6, nextMonth);
            db.pstmt.setTimestamp(7, today);
            db.pstmt.setTimestamp(8, today);
            db.pstmt.setTimestamp(9, tomorrow);
        }
    };

    /**
     * Standard constructor to create a new FormAssignmentComingDueSummaryFinder object.
     */
    public FormAssignmentComingDueSummaryFinder() {
        addFinderListener(listener);
    }

    /**
     * Get the SQL statement without any additional where clauses, group by, or
     * order by statements.
     *
     * @return a <code>String</code> value containing the default sql statement
     * without any additional adornments.
     */
    protected String getBaseSQLStatement() {
        return BASE_SELECT_STATEMENT;
    }

    /**
     * Find the {@link net.project.schedule.report.taskscomingdue.TasksComingDueSummaryData}
     * object that pertains the supplied space id.
     *
     * @param spaceID a <code>String</code> value containing the space id for
     * the space we are looking up a <code>TasksComingDueSummaryData</code>.
     * @return a <code>List</code> object which contains one or more
     * <code>TasksComingDueSummaryData</code> objects.  Currently, this query
     * should really only be returning one object, but the data model actually
     * does support more than one schedule.
     * @throws PersistenceException if there is an error loading data from the
     * database.
     */
    public List findBySpaceID(String spaceID) throws PersistenceException {
        addWhereClause(" a.space_id = " + spaceID);
        return loadFromDB();
    }

    /**
     * Populate a domain object which data specific to the query result.  For
     * example, a task finder would populate a {@link net.project.schedule.Task}
     * object.  Any class that extends the finder base class needs to implement
     * this method the finder can use its build-in loadFromDB method to load
     * objects.
     *
     * @param databaseResults a <code>ResultSet</code> that provides the data
     * necessary to populate the domain object.
     * @return a <code>Object</code> subclass specific to your finder that has
     * been populated with data.
     * @throws SQLException if an error occurs populating the object.
     */
    protected Object createObjectForResultSetRow(ResultSet databaseResults) throws SQLException {
        TasksComingDueSummaryData tcdsd = new TasksComingDueSummaryData();
        tcdsd.setTaskCount(databaseResults.getInt("assignment_count"));
        tcdsd.setCompletedTaskCount(databaseResults.getInt("completed"));
        tcdsd.setDueTodayCount(databaseResults.getInt("due_today"));
        tcdsd.setDueTomorrowCount(databaseResults.getInt("due_tomorrow"));
        tcdsd.setDueNext7Count(databaseResults.getInt("due_next_7"));
        tcdsd.setDueNextMonthCount(databaseResults.getInt("due_next_month"));
        tcdsd.setUncompletedTasks(databaseResults.getInt("uncompleted"));
        return tcdsd;
    }	
	
}
