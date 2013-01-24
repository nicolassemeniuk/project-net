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

/**
 * Finder to locate TasksComingDueSummary data objects.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
class TasksComingDueSummaryFinder extends Finder {
    /**
     * Private String that contains the basic report.  Be careful about changing
     * this String, it is used by all of the finder methods below, as well as by
     * the Filtering classes that can be found in the same directory as this file.
     */
    private String BASE_SELECT_STATEMENT =
        "SELECT " +
        "  p.plan_id, " +
        "  count(t.task_id) task_count, " +
        "  count(tc.completed_task_id) completed_tasks, " +
        "  count(tomorrow.due_tomorrow_task_id) due_tomorrow, " +
        "  count(n7.due_next_7_task_id) due_next_7, " +
        "  count(nm.due_next_month_task_id) due_next_month, " +
        "  count(uc.uncompleted_task_id) uncompleted_tasks, " +
        "  count(today.due_today_task_id) due_today " +
        "FROM " +
        "  pn_space_has_plan shp, " +
        "  pn_plan p, " +
        "  pn_plan_has_task pht, " +
        "  pn_task t, " +
        "  (select t2.task_id completed_task_id from pn_task t2 where t2.work_percent_complete >= 100) tc, " +
        "  (select t3.task_id due_tomorrow_task_id from pn_task t3 where t3.work_percent_complete < 100 and date_finish >= ? and date_finish < ?) tomorrow, " +
        "  (select t4.task_id due_next_7_task_id from pn_task t4 where t4.work_percent_complete < 100 and date_finish >= ? and date_finish < ?) n7, " +
        "  (select t5.task_id due_next_month_task_id from pn_task t5 where t5.work_percent_complete < 100 and date_finish >= ? and date_finish < ?) nm, " +
        "  (select t6.task_id uncompleted_task_id from pn_task t6 where t6.work_percent_complete < 100 and date_finish >= ?) uc, " +
        "  (select t7.task_id due_today_task_id from pn_task t7 where t7.work_percent_complete < 100 and date_finish >= ? and date_finish < ?) today " +
        "WHERE " +
        "  shp.plan_id = p.plan_id " +
        "  and p.plan_id = pht.plan_id " +
        "  and pht.task_id = t.task_id " +
        "  and tc.completed_task_id(+) = t.task_id " +
        "  and tomorrow.due_tomorrow_task_id(+) = t.task_id " +
        "  and n7.due_next_7_task_id(+) = t.task_id " +
        "  and nm.due_next_month_task_id(+) = t.task_id " +
        "  and uc.uncompleted_task_id(+) = t.task_id " +
        "  and today.due_today_task_id(+) = t.task_id " +
        "  and t.work_percent_complete != 100 "+
        "  and t.record_status = 'A' ";

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
            if (!groupByClauses.contains("p.plan_id")) {
                addGroupByClause("p.plan_id");
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
     * Standard constructor to create a new TasksComingDueSummaryFinder object.
     */
    public TasksComingDueSummaryFinder() {
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
        addWhereClause(" shp.space_id = " + spaceID);
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
        tcdsd.setTaskCount(databaseResults.getInt("task_count"));
        tcdsd.setCompletedTaskCount(databaseResults.getInt("completed_tasks"));
        tcdsd.setDueTodayCount(databaseResults.getInt("due_today"));
        tcdsd.setDueTomorrowCount(databaseResults.getInt("due_tomorrow"));
        tcdsd.setDueNext7Count(databaseResults.getInt("due_next_7"));
        tcdsd.setDueNextMonthCount(databaseResults.getInt("due_next_month"));
        tcdsd.setUncompletedTasks(databaseResults.getInt("uncompleted_tasks"));
        return tcdsd;
    }

}
