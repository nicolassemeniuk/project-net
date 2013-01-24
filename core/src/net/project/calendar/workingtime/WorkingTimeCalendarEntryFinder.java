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

 /*----------------------------------------------------------------------+
|
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.calendar.workingtime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.apache.log4j.Logger;

import net.project.base.RecordStatus;
import net.project.base.finder.Finder;
import net.project.base.finder.FinderListener;
import net.project.base.finder.FinderListenerAdapter;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.persistence.PersistenceException;

/**
 * Provides a Finder for loading <code>WorkingTimeCalendarEntry</code>s.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
class WorkingTimeCalendarEntryFinder extends Finder {

    /** The base SQL statement to load entries. */
    private static final String BASE_SQL_STATEMENT =
            "select calendar_id, entry_id, is_working_day, is_day_of_week, day_number, start_date, end_date, " +
            "time1_start, time1_end, time2_start, time2_end, time3_start, time3_end, time4_start, time4_end, time5_start, time5_end, " +
            "record_status, description " +
            "from pn_workingtime_calendar_entry e " +
            "where (1=1) ";

    /**
     * A SQL statement to return entries by plan.
     * We use an optimizer hint to tell Oracle to use the index on
     * PLAN_ID since that will limit the potential candidates by the greatest
     * amount (there will often be zero results).
     * Without the hint, using the Rules based optimizer Oracle seems to do full
     * table scans.
     * Don't remove without analyzing performance impact on Scheduling module
     * (especially viewing / loading schedule entries).
     */
    private static final String ENTRIES_BY_PLAN_STATEMENT =
            "select /*+ INDEX(c, PN_WORKINGTIME_CLAENDAR_IDX1) */ c.plan_id, e.calendar_id, e.entry_id, e.is_working_day, e.is_day_of_week, e.day_number, e.start_date, e.end_date, " +
            "e.time1_start, e.time1_end, e.time2_start, e.time2_end, e.time3_start, e.time3_end, e.time4_start, e.time4_end, e.time5_start, e.time5_end, " +
            "e.record_status, description " +
            "from pn_workingtime_calendar c, pn_workingtime_calendar_entry e " +
            "where e.calendar_id = c.calendar_id ";

    /**
     * Indicates whether we're finding by planID; determines the appropriate SQL statement
     * to be returned; default is <code>false</code>.
     */
    private boolean isFindByPlan = false;

    /**
     * Returns the base SQL statement.
     * If we're finding by plan then the SQL will provide planID as a
     * column.
     * @return the base SQL statement
     */
    protected String getBaseSQLStatement() {
        if (isFindByPlan) {
            return ENTRIES_BY_PLAN_STATEMENT;
        } else {
            return BASE_SQL_STATEMENT;
        }
    }

    protected Object createObjectForResultSetRow(ResultSet databaseResults) throws SQLException {
        boolean isWorkingDay = databaseResults.getBoolean("is_working_day");
        boolean isDayOfWeek = databaseResults.getBoolean("is_day_of_week");

        WorkingTimeCalendarEntry entry;

        if (isDayOfWeek) {
            // We have a day of week entry, which has a day number
            int dayNumber = databaseResults.getInt("day_number");

            if (isWorkingDay) {
                entry = WorkingTimeCalendarEntry.makeWorkingDayOfWeek(dayNumber);
            } else {
                entry = WorkingTimeCalendarEntry.makeNonWorkingDayOfWeek(dayNumber);
            }

        } else {
            // We have a date entry which has a start and end date
            Date startDate = new Date(databaseResults.getTimestamp("start_date").getTime());
            Date endDate = new Date(databaseResults.getTimestamp("end_date").getTime());

            if (isWorkingDay) {
                entry = WorkingTimeCalendarEntry.makeWorkingDate(DayOfYear.makeFromStoredDate(startDate), DayOfYear.makeFromStoredDate(endDate));
            } else {
                entry = WorkingTimeCalendarEntry.makeNonWorkingDate(DayOfYear.makeFromStoredDate(startDate), DayOfYear.makeFromStoredDate(endDate));
            }
        }

        entry.setCalendarID(databaseResults.getString("calendar_id"));
        entry.setEntryID(databaseResults.getString("entry_id"));
        entry.setDateDescription(databaseResults.getString("description"));

        if (isWorkingDay) {
            // Now add the stored times
            Collection workingTimes = new ArrayList(5);
            addWorkingTime(entry, workingTimes, databaseResults.getTimestamp("time1_start"), databaseResults.getTimestamp("time1_end"));
            addWorkingTime(entry, workingTimes, databaseResults.getTimestamp("time2_start"), databaseResults.getTimestamp("time2_end"));
            addWorkingTime(entry, workingTimes, databaseResults.getTimestamp("time3_start"), databaseResults.getTimestamp("time3_end"));
            addWorkingTime(entry, workingTimes, databaseResults.getTimestamp("time4_start"), databaseResults.getTimestamp("time4_end"));
            addWorkingTime(entry, workingTimes, databaseResults.getTimestamp("time5_start"), databaseResults.getTimestamp("time5_end"));
            entry.setWorkingTimes(workingTimes);
        }

        return entry;
    }

    /**
     * Adds a new working time to the specified collection constructed from
     * the specified dates.
     * <p>
     * This method is convenient in that it adds no working time if either
     * of the specified dates are null. <br>
     * Additionally, it converts the dates to <code>java.util.Date</code> objects
     * by using the millisecond value, making it safe to use with SQL date
     * and timestamp objects.
     * </p>
     * @param entry the entry to which the working times have to be added
     * @param workingTimes the working times to which to add the working time
     * if both dates are non null.
     * @param startDate the start date of the working time
     * @param endDate the end date of the working time
     */
    private void addWorkingTime(WorkingTimeCalendarEntry entry, Collection workingTimes, Date startDate, Date endDate) {
        if (startDate != null && endDate != null) {
            try {
                workingTimes.add(new WorkingTime(new Date(startDate.getTime()), new Date(endDate.getTime())));
            } catch (Exception e) {
                Logger.getLogger(WorkingTimeCalendarEntryFinder.class).error("Error adding entry for calendar:" +
                        entry.getCalendarID() + " with entry id:" + entry.getEntryID() + 
                        " for start time:" + startDate + " and for end time:" + endDate, e);
            }
        }
    }
    /**
     * Finds calendar entries for the specified calendar ID.
     * @param calendarID the ID of the calendar definition for which to find
     * entries
     * @return a list where each element is a <code>WorkingTimeCalendarEntry</code>
     * @throws PersistenceException if there is a problem loading
     */
    Collection findByCalendarID(final String calendarID) throws PersistenceException {
        addWhereClause("e.calendar_id = ? ");

        FinderListener listener = new FinderListenerAdapter() {
            public void preExecute(DBBean db) throws SQLException {
                int index = 0;
                db.pstmt.setString(++index, calendarID);
            }
        };

        addFinderListener(listener);
        return loadFromDB();
    }

    /**
     * Finds all calendar entries for the specified planID.
     * @param planID the ID of the plan for which to find entries
     * @return a collection where each element is a <code>WorkingTimeCalendarEntry</code>;
     * these are in no particular order (specifically, not guaranteed to be grouped
     * by calendarID)
     * @throws PersistenceException if there is a problem loading
     */
    Collection findByPlanID(final String planID) throws PersistenceException {

        // Set flag for appropriate SQL statement
        this.isFindByPlan = true;
        addWhereClause("c.plan_id = ? ");
        addWhereClause("e.record_status = ? ");

        FinderListener listener = new FinderListenerAdapter() {
            public void preExecute(DBBean db) throws SQLException {
                int index = 0;
                db.pstmt.setString(++index, planID);
                db.pstmt.setString(++index, RecordStatus.ACTIVE.getID());
            }
        };
        addFinderListener(listener);

        return loadFromDB();
    }

    Collection findByCalendarIDs(Collection planIDList) throws PersistenceException {
        Collection entries = Collections.EMPTY_LIST;

        if (planIDList.size() > 0) {
            String planIDs = DatabaseUtils.collectionToCSV(planIDList);
            addWhereClause("e.calendar_id in ("+planIDs+") ");
            addWhereClause("e.record_status = '" + RecordStatus.ACTIVE.getID() + "'");

            entries = loadFromDB();
        }

        return entries;
    }

}
