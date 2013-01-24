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

 package net.project.schedule.report.overallocation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.Finder;
import net.project.base.finder.FinderListenerAdapter;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.report.ReportAssignmentType;
import net.project.resource.ResourceAssignment;
import net.project.util.DateUtils;

import org.apache.log4j.Logger;

/**
 * This class is designed to find all instances of a {@link
 * net.project.resource.ResourceAssignment} object which match certain
 * criteria. Further information on what criteria is being specified can be
 * found in the finder objects for this class.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
class OverallocatedResourceFinder extends Finder {
    /**
     * Space ID of the resources we are reporting on.
     */
    private String spaceID;
    /**
     * Allocation date column.
     */
    public static ColumnDefinition REPORTING_PERIOD = new ColumnDefinition("eq.allocation_date", PropertyProvider.get("prm.schedule.report.resourceallocation.date.name"));
    
    /**
     * type of assignemetns to include in report
     */
    private ReportAssignmentType reportAssignmentType;
    
    /**
     * Base SQL statement that will be used to construct the statement returned
     * by the {@link #getSQLStatement} method.
     */
    private String BASE_SQL_STATEMENT =
        "select " +
        "  eq.allocation_date, " +
        "  obn.name as task_name, " +
        "  p.display_name as resource_name, " +
        "  p.person_id as resource_id, " +
        "  eq.percent_allocated " +
        "from " +
        //"  pn_task t, " +
        "  pn_assignment a, " +
        "  pn_person p, " +
        //"  pn_space_has_plan shp, " +
        //"  pn_plan_has_task pht, " +
        "  pn_object_name obn, " +
        "  pn_object o, " +
        "  ( " +
        "      SELECT   dt.allocation_date, dt.allocation_date_end, p.person_id, SUM (a.percent_allocated) as percent_allocated " +
        "      FROM     pn_assignment a,  pn_person p, pn_object o ," +
        "               (SELECT @@@startdate@@@ +x AS allocation_date, " +
        "                       @@@startdate@@@ + (x+1) AS allocation_date_end " +
        "                FROM   pn_pivot " +
        "                WHERE  x <= (  @@@finishdate@@@ - @@@startdate@@@)) dt " +
        "      WHERE    dt.allocation_date <= a.end_date " +
        "      AND      dt.allocation_date_end > a.start_date " +
        //"      AND      t.task_id = a.object_id " +
        "      AND      p.person_id = a.person_id " +
        "      AND      a.record_status = 'A' " +
        "      AND      a.percent_complete < 1" +   //1=100% here
        "      AND      a.object_id = o.object_id    " +
        "               @@@assignment_type@@@    "+
        "      GROUP BY dt.allocation_date, dt.allocation_date_end, p.person_id, p.display_name " +
        "      HAVING   SUM (a.percent_allocated) > 100 " +
        "  ) eq " +
        "where " +
        //"  a.object_id = t.task_id " +
        "  a.object_id = o.object_id " +
        "  and a.object_id = obn.object_id " +
        "  and a.person_id = p.person_id " +
        "  and a.person_id = eq.person_id " +
        "  and a.percent_complete < 1 " +
        //"  and shp.plan_id = pht.plan_id " +
        //"  and pht.task_id = t.task_id " +
        "  and a.record_status = 'A' " +
        "  and eq.allocation_date <= a.end_date " +
        "  and eq.allocation_date_end > a.start_date "+
        "  @@@assignment_type@@@";

    /**
     * Standard constructor which prepares the overallocated resource finder for
     * use.
     */
    public OverallocatedResourceFinder() {
        super();
        addFinderListener(new FinderListenerAdapter() {
            public void preExecute(DBBean db) throws SQLException {
                super.preExecute(db);
            }
        });
    }

    /**
     * Get the SQL statement without any additional where clauses, group by, or
     * order by statements.
     *
     * @return a <code>String</code> value containing the default sql statement
     * without any additional adornments.
     */
    protected String getBaseSQLStatement() {
        clearOrderByClauses();
        addOrderByClause("allocation_date");
        addOrderByClause("task_name");
        addOrderByClause("display_name");

        // Determine the earlier and latest tasks, which will be required to
        // produce the correct sql statement
        // Note:  If no start or finish date is found then the current
        // date will be used; this simply means that when the real SQL statement
        // is executed, no results will be returned
        Date startDate = new Date();
        Date finishDate = startDate;

        DBBean db = new DBBean();
        try {
            db.prepareStatement(
                "select " +
                "  min(a.start_date) as start_date, " +
                "  max(a.end_date) as finish_date " +
                "from " +
                "  pn_assignment a, " +
                //"  pn_task t, " +
                "  pn_person p " +
                "where " +
                //"  t.task_id = a.object_id " +
                "  p.person_id = a.person_id " +
                "  and a.space_id = ? ");
            db.pstmt.setString(1, spaceID);
            db.executePrepared();

            if (db.result.next()) {
                // We found a max and min value
                startDate = db.result.getTimestamp("start_date");
                finishDate = db.result.getTimestamp("finish_date");

                // Note: a select that returns an aggregate function will
                // return 1 row with null column values even if no rows
                // match the where clause
                // We must check to make sure that we actually got a value
                // back
                if (startDate == null) {
                    startDate = new Date();
                }

                if (finishDate == null) {
                    finishDate = startDate;
                }

            }

        } catch (SQLException sqle) {
        	Logger.getLogger(OverallocatedResourceFinder.class).error("Unable to find start and end dates for " +
                "overallocated resource report." + sqle);
        } finally {
            db.release();
        }

        String sqlStatement = BASE_SQL_STATEMENT;
        sqlStatement = sqlStatement.replaceAll("@@@startdate@@@",
            DateUtils.getDatabaseDateString(startDate));
        sqlStatement = sqlStatement.replaceAll("@@@finishdate@@@",
            DateUtils.getDatabaseDateString(finishDate));

        if (reportAssignmentType == ReportAssignmentType.ALL_ASSIGNMENT_REPORT){
        	sqlStatement = sqlStatement.replaceAll("@@@assignment_type@@@", "");
        } else if (reportAssignmentType == ReportAssignmentType.TASK_ASSIGNMENT_REPORT){
        	sqlStatement = sqlStatement.replaceAll("@@@assignment_type@@@", " and o.object_type = 'task' ");
        } else if (reportAssignmentType == ReportAssignmentType.FORM_ASSIGNMENT_REPORT){
        	sqlStatement = sqlStatement.replaceAll("@@@assignment_type@@@", " and o.object_type = 'form_data' ");
        }
        
        return sqlStatement;
    }

    /**
     * Find overallocated resources in a given space.
     *
     * @param spaceID
     * @return a <code>java.util.List</code> object containing zero or more
     * ResourceAllocation objects.
     * @throws PersistenceException if a database error occurs while loading the
     * resource allocations.
     */
    public List findBySpaceID(String spaceID, ReportAssignmentType reportAssignmentType) throws PersistenceException {
        this.spaceID = spaceID;
        this.reportAssignmentType = reportAssignmentType;	
        //There can only be one or zero items in the result set because of the
        //way it was written.  If there is one, it will be a list of all resource
        //allocations found.
        addWhereClause(" a.space_id = " + spaceID);
        List toReturn = loadFromDB();
        if (toReturn.size() > 0) {
            toReturn = (List)toReturn.get(0);
        }

        return toReturn;
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
        LinkedHashMap records = new LinkedHashMap();

        Date currentAllocationDate = null;
        int currentResourceID = -1;
        ResourceAssignment resourceAllocation = null;

        do {
            currentAllocationDate = rs.getTimestamp("allocation_date");
            currentResourceID = rs.getInt("resource_id");
            Key key = new Key(currentAllocationDate, currentResourceID);

            if (records.containsKey(key)) {
                resourceAllocation = (ResourceAssignment)records.get(key);
                resourceAllocation.addTask(rs.getString("task_name"));
            } else {
                //Add the information about this allocation
                resourceAllocation = new ResourceAssignment();
                resourceAllocation.setAssignmentDate(currentAllocationDate);
                resourceAllocation.setPercentAssigned(rs.getInt("percent_allocated"));
                resourceAllocation.setResourceName(rs.getString("resource_name"));
                resourceAllocation.setResourceID(rs.getString("resource_id"));
                resourceAllocation.addTask(rs.getString("task_name"));

                records.put(key, resourceAllocation);
            }
        } while (rs.next());

        return new ArrayList(records.values());
    }
}

class Key {
    Date date;
    int id;

    public Key(Date date, int id) {
        this.date = date;
        this.id = id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Key)) {
            return false;
        }

        final Key key = (Key) o;

        if (id != key.id) {
            return false;
        }
        if (date != null ? !date.equals(key.date) : key.date != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = (date != null ? date.hashCode() : 0);
        result = 29 * result + id;
        return result;
    }
}
