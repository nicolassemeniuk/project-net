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

public class LateFormAssignmentSummaryFinder extends Finder{

    /**
     * Private String that contains the basic report.  Be careful about changing
     * this String, it is used by all of the finder methods below, as well as by
     * the Filtering classes that can be found in the same directory as this file.
     */
    private String BASE_SELECT_STATEMENT =
        "SELECT  " +
        "  a.space_id, " +
        "  count(a.object_id) assignment_count, " +
        "  count(tc.completed_assignment_id) completed, " +
        "  count(ot.overdue_assignment_id) overdue, " +       
        "  count(c7.completed_last_7_assignment_id) completed_last_7, " +
        "  count(n7.due_next_7_assignment_id) due_next_7 " +
        "FROM " +
        "  pn_assignment a, " +
        "  pn_object o, " +
        "  (select a2.object_id completed_assignment_id, a2.STATUS_ID completed_status_id from pn_assignment a2 where a2.percent_complete >= 1 ) tc, " +
        "  (select a3.object_id overdue_assignment_id, a3.STATUS_ID over_due_status_id  from pn_assignment a3 where a3.percent_complete < 1 and a3.end_date < ?  )  ot, " +       
        "  (select a4.object_id completed_last_7_assignment_id, a4.STATUS_ID completed_last_7_status_id from pn_assignment a4 where a4.percent_complete >= 1 and a4.end_date > ?  ) c7, " +
        "  (select a5.object_id due_next_7_assignment_id, a5.STATUS_ID due_next_7_status_id from pn_assignment a5 where a5.percent_complete < 1 and a5.end_date > ? and a5.end_date < ?  ) n7 " +
        "WHERE " +
        "  a.object_id = o.object_id " +
        "  and o.object_type = 'form_data'  " +
        "  and o.record_status = 'A'  " +
        "  and tc.completed_assignment_id(+) = a.object_id " +
        "  and tc.completed_status_id(+) = a.STATUS_ID " +
        "  and ot.overdue_assignment_id(+) = a.object_id " +
        "  and ot.over_due_status_id(+) = a.STATUS_ID " +
        "  and c7.completed_last_7_assignment_id(+) = a.object_id " +
        "  and c7.completed_last_7_status_id(+) = a.STATUS_ID " +
        "  and n7.due_next_7_assignment_id(+) = a.object_id " +
        "  and n7.due_next_7_status_id(+) = a.STATUS_ID " +
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
          * {@link net.project.base.finder.Finder#getSQLStatement} method.
          */
         public void preConstruct(Finder f) {
             groupByClauses.add(0, "a.space_id");
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
             db.pstmt.setTimestamp(2, sevenDaysAgo);
             db.pstmt.setTimestamp(3, todayTS);
             db.pstmt.setTimestamp(4, sevenDaysAhead);
         }
     };         
    	
     /**
      * Standard constructor.
      */
     public LateFormAssignmentSummaryFinder() {
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
         LateTaskReportSummaryData newObject = new LateTaskReportSummaryData();
         newObject.setPlanID(databaseResults.getInt("space_id"));
         newObject.setTaskCount(databaseResults.getInt("assignment_count"));
         newObject.setCompletedTaskCount(databaseResults.getInt("completed"));
         newObject.setOverdueTaskCount(databaseResults.getInt("overdue"));
         newObject.setOverdueMilestoneCount(0);//no milestones 
         newObject.setTaskCompletedInLast7DaysCount(databaseResults.getInt("completed_last_7"));
         newObject.setTaskDueInNext7DaysCount(databaseResults.getInt("due_next_7"));
         return newObject;
     }     
}
