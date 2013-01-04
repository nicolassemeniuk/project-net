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

 package net.project.schedule.report.resourceallocation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.Finder;
import net.project.base.finder.FinderGrouping;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.schedule.Schedule;
import net.project.schedule.TaskFinder;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * This class finds all resource allocations with certain parameters.
 *
 * @author Matthew FLower
 * @since Version 7.4
 */
class ResourceAllocationFinder extends Finder {
    /** Reference to the NAME column, which is column used in SQL statement of this finder. */
    public static final ColumnDefinition NAME_COLUMN = TaskFinder.NAME_COLUMN;
    /** Reference to the DURATION column, which is column used in SQL statement of this finder. */
    public static final ColumnDefinition DURATION_COLUMN = TaskFinder.DURATION_COLUMN;
    /** Reference to the DATE_START column, which is column used in SQL statement of this finder. */
    public static final ColumnDefinition DATE_START_COLUMN = TaskFinder.DATE_START_COLUMN;
    /** Reference to the DATE_FINISH column, which is column used in SQL statement of this finder. */
    public static final ColumnDefinition DATE_FINISH_COLUMN = TaskFinder.DATE_FINISH_COLUMN;
    /** Reference to the TASK_ID column, which is column used in SQL statement of this finder. */
    public static final ColumnDefinition TASK_ID_COLUMN = TaskFinder.TASK_ID_COLUMN;
    /** Reference to the PERCENT_ALLOCATED column, which is column used in SQL statement of this finder. */
    public static final ColumnDefinition PERCENT_ALLOCATED_COLUMN = new ColumnDefinition("percent_allocated", "prm.schedule.report.resourceallocation.columndefs.percentallocated.name");
    /** Reference to the RESOURCE_NAME column, which is column used in SQL statement of this finder. */
    public static final ColumnDefinition RESOURCE_NAME = new ColumnDefinition("display_name", "prm.schedule.report.resourceallocation.columndefs.resourcename.name");

    private ReportingPeriodType reportingPeriodType = ReportingPeriodType.DEFAULT;

    /**
     * Indicates whether we should restrict tasks to those with record status 'A".
     */
    private boolean showActiveTasksOnly = true;

    /**
     * Provides a helper to determine the number of hours worked between
     * dates for a resource.
     */
    private WorkCalculator resourceCalendarMap;

    /**
     * Maps a task_id/person_id key to running total amount of work left to do
     * in the assignment.
     */
    private HashMap workRemaining = new HashMap();

    /**
     * Get the SQL statement without any additional where clauses, group by, or
     * order by statements.
     *
     * @return a <code>String</code> value containing the default sql statement
     * without any additional adornments.
     */
    protected String getBaseSQLStatement() {
        Date startDate = new Date();
        Date finishDate = new Date();
        DBBean db = new DBBean();

        try {
            String sqlStatement =
                "select " +
                "  min(t.date_start) as start_date, " +
                "  max(t.date_finish) as finish_date " +
                "from " +
                "  pn_assignment a, " +
                "  pn_task t, " +
                "  pn_person p, " +
                "  pn_space_view sv " +
                "where " +
                "  1=1 " +
                "  and a.object_id = t.task_id " +
                "  and a.person_id = p.person_id " +
                "  and a.space_id = sv.space_id "+
                "  and a.record_status = 'A' " +
                "  and t.record_status = 'A' " +
                "  and sv.record_status = 'A' " +
                getWhereClause();
            
            db.executeQuery(sqlStatement);
            if (db.result.next()) {
                startDate = db.result.getTimestamp("start_date");
                finishDate = db.result.getTimestamp("finish_date");
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } finally {
            db.release();
        }


        return reportingPeriodType.getSQLStatement(startDate, finishDate);
    }

    /**
     * Find all {@link net.project.schedule.report.resourceallocation.TaskResourceAllocation}
     * objects that correspond to the provided space id.
     * <p>
     * Loads <i>Active</i> tasks only.
     * </p>
     *
     * @param spaceID a <code>String</code> value which is the primary key of
     * the space in which we are going to look for Task Resource Allocations.
     * @param grouping which specifies how we are going to group resources --
     * by day, week, or month.
     * @return a <code>java.util.List</code> object containing zero or more
     * {@link net.project.schedule.report.resourceallocation.TaskResourceAllocation}
     * objects.
     * @throws PersistenceException if an error occurs loading the data from the
     * database.
     */
    public List findBySpaceID(String spaceID, FinderGrouping grouping, Schedule schedule) throws PersistenceException {
        resourceCalendarMap = new WorkCalculator(schedule.getWorkingTimeCalendarProvider());

        //MAFTODO switch to using a parameter page instead of hacking groups.
        if (grouping instanceof DayGrouping) {
			reportingPeriodType = ReportingPeriodType.DAILY_REPORT;
		} else if (grouping instanceof WeekGrouping) {
			reportingPeriodType = ReportingPeriodType.WEEKLY_REPORT;
		} else if (grouping instanceof MonthGrouping) {
			reportingPeriodType = ReportingPeriodType.MONTHLY_REPORT;
		} else {
			this.reportingPeriodType = ReportingPeriodType.DAILY_REPORT;
		}

        addWhereClause(" a.space_id = " + spaceID);

        // Ensure we restrict tasks to Active tasks if necessary
        if (showActiveTasksOnly) {
            addWhereClause("t.record_status = 'A'");
        }       
        return loadFromDB();
    }

    /**
     * Populate a domain object which data specific to the query result.  For
     * example, a task finder would populate a {@link net.project.schedule.Task}
     * object.  Any class that extends the finder base class needs to implement
     * this method the finder can use its build-in loadFromDB method to load
     * objects.
     *
     * @param rs a <code>ResultSet</code> that provides the data
     * necessary to populate the domain object.
     * @return a <code>Object</code> subclass specific to your finder that has
     * been populated with data.
     * @throws SQLException if an error occurs populating the object.
     */
    protected Object createObjectForResultSetRow(ResultSet rs) throws SQLException {
        TaskResourceAllocation tra = new TaskResourceAllocation();
        tra.setReportingPeriodStart(rs.getTimestamp("cal_date"));
        tra.setAllocationStartDate(rs.getTimestamp("allocation_start_date"));
        tra.setAllocationFinishDate(rs.getTimestamp("allocation_finish_date"));
        tra.setTaskID(rs.getString("task_id"));
        tra.setTaskName(rs.getString("task_name"));
        tra.setTaskDuration(new TimeQuantity(rs.getLong("duration"), TimeQuantityUnit.DAY));
        tra.setTotalTaskWork(new TimeQuantity(rs.getLong("work"), TimeQuantityUnit.HOUR));
        tra.setStartDate(rs.getTimestamp("date_start"));
        tra.setFinishDate(rs.getTimestamp("date_finish"));
        tra.setResourceID(rs.getString("resource_id"));
        tra.setResourceName(rs.getString("display_name"));
        tra.setPercentAllocated(rs.getInt("percent_allocated"));

        //Figure out the amount of time being worked.
        tra.setAllocatedWork(resourceCalendarMap.getHoursWorkedBetweenDates(tra.getResourceID(), tra.getAllocationStartDate(), tra.getAllocationFinishDate(), tra.getPercentAllocated()));

        //Make sure we aren't
        AllocationKey key = new AllocationKey(tra.getTaskID(), tra.getResourceID());
        TimeQuantity tq = (TimeQuantity)workRemaining.get(key);
        if (tq == null) {
            tq = tra.getTotalTaskWork();
        }
        TimeQuantity allocatedWork = tra.getAllocatedWork();
        tq = tq.subtract(allocatedWork);
        if (tq.getAmount().signum() < 0) {
            tra.setAllocatedWork(allocatedWork.add(tq));
            tq = new TimeQuantity(0, TimeQuantityUnit.HOUR);
        }
        workRemaining.put(key, tq);

        return tra;
    }


    private class AllocationKey {
        private String taskID;
        private String personID;

        public AllocationKey(String taskID, String personID) {
            this.taskID = taskID;
            this.personID = personID;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AllocationKey)) return false;

            final AllocationKey allocationKey = (AllocationKey) o;

            if (!personID.equals(allocationKey.personID)) return false;
            if (!taskID.equals(allocationKey.taskID)) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = taskID.hashCode();
            result = 29 * result + personID.hashCode();
            return result;
        }
    }

}
