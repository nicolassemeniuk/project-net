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

 package net.project.schedule.report.latetaskreport;

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
 * Class designed to load <code>LateTaskReportSummaryData</code> objects from the database.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
class LateTaskReportSummaryFinder extends Finder {
    /**
     * Private String that contains the basic report.  Be careful about changing
     * this String, it is used by all of the finder methods below, as well as by
     * the Filtering classes that can be found in the same directory as this file.
     */
    private String BASE_SELECT_STATEMENT =
        "SELECT  " +
        "  p.plan_id, " +
        "  count(t.task_id) task_count, " +
        "  count(tc.completed_task_id) completed_tasks, " +
        "  count(ot.overdue_task_id) overdue_tasks, " +
        "  count(om.overdue_milestone_id) overdue_milestones, " +
        "  count(c7.completed_last_7_task_id) completed_last_7, " +
        "  count(n7.due_next_7_task_id) due_next_7 " +
        "FROM " +
        "  pn_space_has_plan shp, " +
        "  pn_plan p, " +
        "  pn_plan_has_task pht, " +
        "  pn_task t, " +
        "  (select t2.task_id completed_task_id from pn_task t2 where t2.work_percent_complete >= 100) tc, " +
        "  (select t3.task_id overdue_task_id from pn_task t3 where t3.work_percent_complete < 100 and t3.date_finish < ? and ((t3.task_type='task' or t3.task_type='summary') and is_milestone=0)) ot, " +
        "  (select t4.task_id overdue_milestone_id from pn_task t4 where t4.work_percent_complete < 100 and t4.date_finish < ? and t4.is_milestone=1) om, " +
        //MAFTODO: slight problem, this will report a finished task from when it is done until 7 days
        //after the finish date.  What if the user finishes really early?  Unfortunately, we cannot really
        //solve this until getActualFinish is implemented.
        "  (select t5.task_id completed_last_7_task_id from pn_task t5 where t5.work_percent_complete >= 100 and date_finish > ?) c7, " +
        "  (select t6.task_id due_next_7_task_id from pn_task t6 where t6.work_percent_complete < 100 and date_finish > ? and date_finish < ?) n7 " +
        "WHERE " +
        "  shp.plan_id = p.plan_id " +
        "  and p.plan_id = pht.plan_id " +
        "  and pht.task_id = t.task_id " +
        "  and tc.completed_task_id(+) = t.task_id " +
        "  and ot.overdue_task_id(+) = t.task_id " +
        "  and om.overdue_milestone_id(+) = t.task_id " +
        "  and c7.completed_last_7_task_id(+) = t.task_id " +
        "  and n7.due_next_7_task_id(+) = t.task_id " +
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
         * {@link net.project.base.finder.Finder#getSQLStatement} method.
         */
        public void preConstruct(Finder f) {
            groupByClauses.add(0, "p.plan_id");
        }

        /**
         * This method is called just before running <code>executePrepared</code> on
         * the sql statement constructed by calling <code>getSQLStatement</code>.
         * This allows a user to set parameters that are required to execute a
         * prepared statement.
         *
         * @param db a {@link net.project.database.DBBean} that is just about to
         * call {@link net.project.database.DBBean#executePrepared}.
         * @throws SQLException if an error occurs while modifying the
         * <code>DBBean</code>.
         */
        public void preExecute(DBBean db) throws SQLException {
            //Get the current date
            Date currentDate = new Date();
            PnCalendar cal = new PnCalendar(SessionManager.getUser());

            //Midnight of today
            cal.setTime(currentDate);
            Timestamp todayTS = new Timestamp(cal.startOfDay(cal.getTime()).getTime());

            //Get 7 days ago
            cal.setTime(currentDate);
            cal.add(Calendar.DATE, -7);
            Timestamp sevenDaysAgo = new Timestamp(cal.startOfDay(cal.getTime()).getTime());

            //Get 7 days in the future
            cal.setTime(currentDate);
            cal.add(Calendar.DATE, 7);
            Timestamp sevenDaysAhead = new Timestamp(cal.startOfDay(cal.getTime()).getTime());

            db.pstmt.setTimestamp(1, todayTS);
            db.pstmt.setTimestamp(2, todayTS);
            db.pstmt.setTimestamp(3, sevenDaysAgo);
            db.pstmt.setTimestamp(4, todayTS);
            db.pstmt.setTimestamp(5, sevenDaysAhead);
        }
    };

    /**
     * Standard constructor.
     */
    public LateTaskReportSummaryFinder() {
        super();
        addFinderListener(listener);
    }

    /**
     * Return the basic SQL Statement that we will build our select statement
     * upon.
     *
     * @return a <code>String</code> which contains the base select statement.
     */
    protected String getBaseSQLStatement() {
        return BASE_SELECT_STATEMENT;
    }

    /**
     * Find a LateTaskReportSummaryData object that corresponds to the primary key passed
     * in through the planID variable.
     *
     * @param planID a <code>String</code> containing the primary key of the
     * schedule (plan) that you want to report about.
     * @return a <code>List</code> which should contain 1 or zero
     * {@link net.project.schedule.report.latetaskreport.LateTaskReportSummaryData} objects.
     * @throws net.project.persistence.PersistenceException if there is an error loading task report
     * information from the database.
     */
    public List findByPlanID(String planID) throws PersistenceException {
        addWhereClause(" p.plan_id = " + planID);
        return loadFromDB();
    }

    /**
     * Find a LateTaskReportSummaryData object that corresponds to the space id passed
     * in the spaceID parameter.
     *
     * @param spaceID a <code>String</code> variable containing the id of a space
     * which contains a schedule.
     * @return a <code>List</code> which should contain 1 or zero
     * {@link net.project.schedule.report.latetaskreport.LateTaskReportSummaryData} objects.
     * @throws net.project.persistence.PersistenceException if there is an error loading task report
     * information from the database, or if no data is returned from the database.
     */
    public List findBySpaceID(String spaceID) throws PersistenceException {
        addWhereClause(" shp.space_id = " + spaceID);
        return loadFromDB();
    }


    /**
     * Find all task report data objects that exist in the database.
     *
     * @return a <code>LateTaskReportSummaryData</code> array of objects which correspond
     * to the SQL Statement that we've constructed.
     * @throws net.project.persistence.PersistenceException if an error occurs while trying to load data
     * into task objects.
     */
    public List findAll() throws PersistenceException {
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
        LateTaskReportSummaryData newObject = new LateTaskReportSummaryData();
        newObject.setPlanID(databaseResults.getInt("plan_id"));
        newObject.setTaskCount(databaseResults.getInt("task_count"));
        newObject.setCompletedTaskCount(databaseResults.getInt("completed_tasks"));
        newObject.setOverdueTaskCount(databaseResults.getInt("overdue_tasks"));
        newObject.setOverdueMilestoneCount(databaseResults.getInt("overdue_milestones"));
        newObject.setTaskCompletedInLast7DaysCount(databaseResults.getInt("completed_last_7"));
        newObject.setTaskDueInNext7DaysCount(databaseResults.getInt("due_next_7"));
        return newObject;
    }
}
