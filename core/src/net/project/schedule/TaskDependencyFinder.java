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

 package net.project.schedule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.project.base.finder.Finder;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

/**
 * This finder finds dependencies between task to return one or more
 * {@link net.project.schedule.TaskDependency} objects.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class TaskDependencyFinder extends Finder {
    private String BASE_SQL_STATEMENT =
        "select " +
        "  td.task_id, td.dependency_id, " +
        "  td.dependency_type_id, td.lag, td.lag_units, " +
        "  t.task_name, t.date_start, t.date_finish, t.seq " +
        "from " +
        "  pn_space_has_plan shp, " +
        "  pn_plan_has_task pht, " +
        "  pn_task t, " +
        "  pn_task dep, " +
        "  pn_task_dependency td " +
        "where " +
        "  shp.plan_id = pht.plan_id " +
        "  and pht.task_id = t.task_id " +
        "  and td.dependency_id = t.task_id " +
        "  and td.task_id = dep.task_id " +
        "  and dep.record_status = 'A' " +
        "  and t.record_status = 'A' ";

    private int TASK_ID_COL_ID = 1;
    private int DEPENDENCY_ID_COL_ID = 2;
    private int DEPENDENCY_TYPE_ID_COL_ID = 3;
    private int LAG_COL_ID = 4;
    private int LAG_UNITS_COL_ID = 5;
    private int TASK_NAME_COL_ID = 6;
    private int DATE_START_COL_ID = 7;
    private int DATE_FINISH_COL_ID = 8;
    private int SEQ_COL_ID = 9;
    private String taskID;

    /**
     * Get the SQL statement without any additional where clauses, group by, or
     * order by statements.
     *
     * @return a <code>String</code> value containing the default sql statement
     * without any additional adornments.
     */
    protected String getBaseSQLStatement() {
        return BASE_SQL_STATEMENT;
    }

    /**
     * Find all {@link net.project.schedule.TaskDependency} objects which are
     * the dependencies of the <code>Task</code> identified by the
     * <code>taskID</code> parameter.
     *
     * @param taskID a <code>String</code> containing the primary key of the
     * task for which we are trying to find dependencies.
     * @return a <code>java.util.List</code> containing zero or more
     * {@link net.project.schedule.TaskDependency} objects.
     * @throws PersistenceException if there is an error loading the
     * TaskDependency objects from the database.
     */
    public List findByTaskID(String taskID) throws PersistenceException {
        clearWhereClauses();
        addWhereClause("td.task_id = " + taskID);

        return loadFromDB();
    }

    /**
     * Find all {@link net.project.schedule.TaskDependency} objects which are
     * the dependencies of the <code>Task</code> identified by the
     * <code>taskID</code> parameter.
     *
     * @param taskID a <code>String</code> containing the primary key of the
     * task for which we are trying to find dependencies.
     * @return a <code>java.util.List</code> containing zero or more
     * {@link net.project.schedule.TaskDependency} objects.
     * @throws SQLException if there is an error loading the
     * TaskDependency objects from the database.
     */
    public List findByTaskID(DBBean db, String taskID) throws SQLException {
        clearWhereClauses();
        addWhereClause("td.task_id = " + taskID);

        return loadFromDB(db);
    }

    public List findByDependencyID(DBBean db, String dependencyID) throws SQLException {
        clearWhereClauses();
        addWhereClause("td.dependency_id = " + dependencyID);

        return loadFromDB(db);
    }

    /**
     * Find all {@link net.project.schedule.TaskDependency} objects which belong
     * to tasks which belong to the space identified by the <code>spaceID</code>
     * parameter.
     *
     * @param spaceID a <code>String</code> value containing the primary key of
     * a space in which we are going to look for Task objects.
     * @return a <code>java.util.List</code> containing zero or more
     * {@link net.project.schedule.TaskDependency} objects.
     * @throws PersistenceException if there is an error loading the
     * TaskDependency objects from the database.
     */
    public List findBySpaceID(String spaceID) throws PersistenceException {
        clearWhereClauses();
        addWhereClause("shp.space_id = " + spaceID);

        return loadFromDB();
    }

    /**
     * Populate a domain object which data specific to the query result.  For
     * example, a task finder would populate a {@link Task}
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
        TaskDependency td = new TaskDependency();
        td.setSequenceNumber(databaseResults.getInt(SEQ_COL_ID));
        td.setTaskID(databaseResults.getString(TASK_ID_COL_ID));
        td.setDependencyID(databaseResults.getString(DEPENDENCY_ID_COL_ID));
        td.setDependencyTypeID(databaseResults.getString(DEPENDENCY_TYPE_ID_COL_ID));
        td.setLag(databaseResults.getBigDecimal(LAG_COL_ID), databaseResults.getString(LAG_UNITS_COL_ID));
        td.setTaskName(databaseResults.getString(TASK_NAME_COL_ID));
        td.setStartDate(databaseResults.getTimestamp(DATE_START_COL_ID));
        td.setFinishDate(databaseResults.getTimestamp(DATE_FINISH_COL_ID));
        td.setLastSaveState();

        return td;
    }
}
