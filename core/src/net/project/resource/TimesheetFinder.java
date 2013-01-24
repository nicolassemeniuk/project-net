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
|    $Revision: 15748 $
|    $Date: 2007-03-01 20:20:09 +0530 (Thu, 01 Mar 2007) $
|    $Author: sjmittal $
|
+-----------------------------------------------------------------------------*/
package net.project.resource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import net.project.base.RecordStatus;
import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.DateFilter;
import net.project.base.finder.Finder;
import net.project.base.finder.StringDomainFilter;
import net.project.base.finder.TextComparator;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * Finds timesheets using the finder architecture.  This gives calling classes
 * the ability to request timesheets with a certain criteria without having to
 * know details like the name of database fields.
 *
 * @author Sachin Mittal
 * @since Version 8.2.0
 */

public class TimesheetFinder extends Finder {

    public static ColumnDefinition PERSON_ID_COLUMN =  new ColumnDefinition("ts.person_id", "");
    public static ColumnDefinition OBJECT_ID_COLUMN =  new ColumnDefinition("ts.object_id", "");
    public static ColumnDefinition STATUS_ID_COLUMN =  new ColumnDefinition("ts.status_id", "");
    public static ColumnDefinition START_DATE_ID_COLUMN =  new ColumnDefinition("ts.start_date", "");
    public static ColumnDefinition END_DATE_TYPE_COLUMN =  new ColumnDefinition("ts.end_date", "");
    

    /**
     * This is the base of the SQL statement that we will use to query for
     * assignments.  In the best case scenario, this will be the only SQL
     * statement that queries for timesheets.  For this reason, we need to be
     * careful with performance.
     */
    private static final String SQL =
        "select " +
        "  ts.object_id, "+
        "  ts.person_id, "+
        "  ts.status_id, "+
        "  ts.start_date, "+
        "  ts.end_date, "+
        "  ts.work, "+
        "  ts.work_units, "+
        "  ts.date_submitted, " +
        "  ts.date_approve_reject, "+
        "  ts.approve_reject_by_id, "+
        "  ts.comments, "+
        "  ts.record_status "+
        "from "+
        "  pn_timesheet ts "+
        "  where ts.record_status = 'A'";
    
    private static int index = 0;
    private static int TIMESHEET_COL_ID = ++index;
    private static int PERSON_COL_ID = ++index;
    private static int STATUS_COL_ID = ++index;
    private static int START_DATE_COL_ID = ++index;
    private static int END_DATE_COL_ID = ++index;
    private static int WORK_COL_ID = ++index;
    private static int WORK_UNITS_COL_ID = ++index;
    private static int DATE_SUBMITTED_COL_ID = ++index;
    private static int DATE_APPROVE_REJECT_COL_ID = ++index;
    private static int APPROVE_REJECT_BY_COL_ID = ++index;
    private static int COMMENTS_COL_ID = ++index;
    private static int RECORD_STATUS_COL_ID = ++index;

    
    protected Object createObjectForResultSetRow(ResultSet databaseResults) throws SQLException {
        Timesheet timesheet = new Timesheet();
        populateTimesheet(databaseResults, timesheet);
        return timesheet;
    }

    protected String getBaseSQLStatement() {

        return SQL;
    }
    
    /**
     * Returns a list of timesheet beloning to the person indicated by the personId
     * @param personId <code>String</code> the id of the person whose timesheets are to be loaded
     * @return a <code>List</code> of timesheets for that person
     * @throws PersistenceException
     */
    public List findByPersonId(String personId) throws PersistenceException {
        clearWhereClauses();
        addWhereClause(" ts.person_id = " + personId);
        return loadFromDB();
    }
    
    /**
     * Returns a list of timesheet that have been completed i.e. whose <code>TimesheetStatus</code> is
     * either submitted or approved; beloning to the person indicated by the personId and for the week
     * indicated by the startDate.
     * @param personId <code>String</code> the id of the person whose timesheets are to be loaded
     * @param startDate <code>Date<code> the date when the timesheets were started
     * @return a <code>List</code> of timesheets for that person
     * @throws PersistenceException
     * @see TimesheetStatus#SUBMITTED
     * @see TimesheetStatus#APPROVED
     */
    public List findCompletedTimesheetByPersonIdAndDate(String personId, Date startDate) throws PersistenceException {
        clearWhereClauses();
        addWhereClause(" ts.person_id = " + personId);
        
        StringDomainFilter statusFilter = new StringDomainFilter("10", "", STATUS_ID_COLUMN, (TextComparator) TextComparator.EQUALS);
        statusFilter.setSelected(true);
        statusFilter.setSelectedValues(new String[] {TimesheetStatus.APPROVED.getID(), TimesheetStatus.SUBMITTED.getID()});
        
        DateFilter dateFilter = new DateFilter("20", START_DATE_ID_COLUMN, false);
        dateFilter.setSelected(true);
        dateFilter.setDateRangeStart(startDate, false);
        
        addFinderFilter(statusFilter);
        addFinderFilter(dateFilter);
        
        return loadFromDB();
    }
    
    /**
     * Returns a list of timesheet that are active i.e. whose <code>TimesheetStatus</code> is
     * not canceled; beloning to the person indicated by the personId and for the week
     * indicated by the startDate.
     * @param personId <code>String</code> the id of the person whose timesheets are to be loaded
     * @param startDate <code>Date<code> the date when the timesheets were started
     * @return a <code>List</code> of timesheets for that person
     * @throws PersistenceException
     * @see TimesheetStatus#CANCELLED
     */
    public List findActiveTimesheetByPersonIdAndDate(String personId, Date startDate) throws PersistenceException {
        clearWhereClauses();
        addWhereClause(" ts.person_id = " + personId);
        
        StringDomainFilter statusFilter = new StringDomainFilter("10", "", STATUS_ID_COLUMN, (TextComparator) TextComparator.NOT_EQUAL);
        statusFilter.setSelected(true);
        statusFilter.setSelectedValues(new String[] {TimesheetStatus.CANCELLED.getID()});
        
        DateFilter dateFilter = new DateFilter("20", START_DATE_ID_COLUMN, false);
        dateFilter.setSelected(true);
        dateFilter.setDateRangeStart(startDate, false);
        
        addFinderFilter(statusFilter);
        addFinderFilter(dateFilter);
        
        return loadFromDB();
    }
    
    /**
     * Populate a given timesheet based on information found with activity id.
     * @param id a <code>String</code> value containing the activity associated with
     * timesheet we want to load from the database.
     * @param timesheetToPopulate a <code>Timesheet</code> object that will be populated
     * @return a <code>boolean</code> indicating that timesheet with that id was found
     */
    public boolean findByActivityID(String id, Timesheet timesheetToPopulate) throws PersistenceException {
        clearWhereClauses();
        addWhereClause(" ts.object_id in (select timesheet_id from pn_assignment_timesheet where object_id = " + id + ")");
        boolean timesheetFound = false;
        DBBean db = new DBBean();

        try {
            db.prepareStatement(getSQLStatement());
            db.executePrepared();

            if (db.result.next()) {
                populateTimesheet(db.result, timesheetToPopulate);
                timesheetFound = true;
            } else {
                timesheetFound = false;
            }

        } catch (SQLException sqle) {
            Logger.getLogger(TimesheetFinder.class).debug("An unexpected SQL Exception has occurred: " + sqle);
            throw new PersistenceException("Unable to load timesheet", sqle);
        } finally {
            db.release();
        }

        return timesheetFound;
    }
    
    /**
     * Populate a given timesheet based on information found with find by id.
     * @param id a <code>String</code> value containing the primary key of the
     * timesheet id we want to load from the database.
     * @param timesheetToPopulate a <code>Timesheet</code> object that will be populated
     * @return a <code>boolean</code> indicating that timesheet with that id was found
     */
    public boolean findByID(String id, Timesheet timesheetToPopulate) throws PersistenceException {
        clearWhereClauses();
        addWhereClause(" ts.object_id = " + id);
        boolean timesheetFound = false;
        DBBean db = new DBBean();

        try {
            db.prepareStatement(getSQLStatement());
            db.executePrepared();

            if (db.result.next()) {
                populateTimesheet(db.result, timesheetToPopulate);
                timesheetFound = true;
            } else {
                timesheetFound = false;
            }

        } catch (SQLException sqle) {
            Logger.getLogger(TimesheetFinder.class).debug("An unexpected SQL Exception has occurred: " + sqle);
            throw new PersistenceException("Unable to load timesheet", sqle);
        } finally {
            db.release();
        }

        return timesheetFound;
    }
    
    /**
     * Populate a timesheet object based on the current row of the recordset.
     *
     * @param result a <code>ResultSet</code> object which is currently pointing
     * to a record in a database result set that contains information about a
     * timesheet.  The result set point will not be modified in any way, only values
     * will be extracted.
     * @param timesheet a timesheet object to fill with data.
     * @throws SQLException when an error occurs while trying to populate
     * <code>Timesheet</code> objects.
     */
    private void populateTimesheet(ResultSet result, Timesheet timesheet) throws SQLException {
        //Populate our new timesheet with all of the appropriate fields.

        timesheet.setID(result.getString(TIMESHEET_COL_ID));
        timesheet.setStartDate(DatabaseUtils.makeDate(result.getTimestamp(START_DATE_COL_ID)));
        timesheet.setEndDate(DatabaseUtils.makeDate(result.getTimestamp(END_DATE_COL_ID)));
        timesheet.setWork(DatabaseUtils.getTimeQuantity(result, WORK_COL_ID, WORK_UNITS_COL_ID));
        timesheet.setDateSubmitted(DatabaseUtils.makeDate(result.getTimestamp(DATE_SUBMITTED_COL_ID)));
        timesheet.setDateApproveReject(DatabaseUtils.makeDate(result.getTimestamp(DATE_APPROVE_REJECT_COL_ID)));
        timesheet.setComments(result.getString(COMMENTS_COL_ID));
        timesheet.setPersonId(result.getString(PERSON_COL_ID));
        timesheet.setApproveRejectById(result.getString(APPROVE_REJECT_BY_COL_ID));
        timesheet.setStatusId(result.getString(STATUS_COL_ID));
        timesheet.setRecordStatus(RecordStatus.findByID(result.getString(RECORD_STATUS_COL_ID)));
        
    }


}
