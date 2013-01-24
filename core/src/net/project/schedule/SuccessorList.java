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

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * Dependent tasks is a list of all tasks that depend on a given task.  It is
 * effectively the opposite of a PredecessorList, which shows a list of tasks
 * that a given task is dependent upon.
 *
 * @author Matthew Flower
 */
public class SuccessorList implements Cloneable, Serializable {
    private ArrayList successorTasks = new ArrayList();
    private String taskID;
    private boolean isLoaded = false;

    /**
     * Set the id of the task that all tasks in the list will be dependent upon.
     *
     * @param taskID a <code>String</code> value containing a task id.
     */
    public void setTaskID(String taskID) {
        if ((this.taskID != null) && (this.taskID.equals(taskID))) {
            //The id hasn't changed, so don't change the load status.
        } else {
            isLoaded = false;
        }
        this.taskID = taskID;
    }

    /**
     * Get the id of the task that all of the <code>TaskDependency</code> objects
     * in the list are dependent upon.
     */
    public String getTaskID() {
        return taskID;
    }

    /**
     * Populate the list with tasks that are dependent upon the task id indicated
     * by {@see #setTaskID}.
     *
     * @exception PersistenceException if an error occurs while trying to load
     * the tasks from the database.
     */
    public void load() throws PersistenceException {
        if (!((taskID != null) && (!taskID.trim().equals("")))) {
            return;
        }

        DBBean db = new DBBean();

        try {
            db.prepareStatement("select td.task_id, td.dependency_id, " +
                "    td.dependency_type_id, td.lag, td.lag_units, " +
                "    t.task_name, t.date_start, t.date_finish " +
                "from " +
                "    pn_task_dependency td, " +
                "    pn_task t " +
                "where " +
                "    td.task_id = t.task_id and " +
                "    t.record_status = 'A' and " +
                "    td.dependency_id = ?");
            db.pstmt.setString(1, taskID);
            db.executePrepared();

            while (db.result.next()) {
                TaskDependency td = new TaskDependency();
                td.setTaskID(db.result.getString("task_id"));
                td.setDependencyID(db.result.getString("dependency_id"));
                td.setDependencyTypeID(db.result.getString("dependency_type_id"));
                td.setLag(db.result.getBigDecimal("lag"), db.result.getString("lag_units"));
                td.setTaskName(db.result.getString("task_name"));
                td.setStartDate(db.result.getTimestamp("date_start"));
                td.setFinishDate(db.result.getTimestamp("date_finish"));

                this.add(td);
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(SuccessorList.class).error("Unexpected SQL Exception thrown:" + sqle);
            throw new PersistenceException("Unexpected SQL Exception thrown.", sqle);
        } finally {
            db.release();
        }

        isLoaded = true;
    }

    /**
     * Determine if the SucessorList has already been loaded from the database.
     *
     * @return a <code>boolean</code> value indicating whether the list has
     * already been loaded.
     */
    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Sets the internal state of the object to indicate if it has already been
     * populated with data.
     *
     * @param loaded a <code>boolean</code> value indicating whether the list
     * has already been populated with data.
     */
    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }


    //Forwaring methods
    public int size() {
        return successorTasks.size();
    }

    public void clear() {
        successorTasks.clear();
        isLoaded = false;
        taskID = null;
    }

    public void add(TaskDependency td) {
        successorTasks.add(td);
    }

    public Iterator iterator() {
        return successorTasks.iterator();
    }

    public void addAll(Collection successors) {
        successorTasks.addAll(successors);
    }

    public boolean isEmpty() {
        return successorTasks.isEmpty();
    }

    List getInternalList() {
        return successorTasks;
    }

    /**
     * Create a clone of this SuccessorList.
     *
     * @return a clone of this instance.
     * @see Cloneable
     */
    protected Object clone() {
        SuccessorList sl = new SuccessorList();
        for (Iterator it = successorTasks.iterator(); it.hasNext();) {
            TaskDependency dependency = (TaskDependency)it.next();
            sl.successorTasks.add(dependency.clone());
        }
        sl.taskID = taskID;
        sl.isLoaded = isLoaded;

        return sl;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SuccessorList)) return false;

        final SuccessorList successorList = (SuccessorList)o;

        if (isLoaded != successorList.isLoaded) return false;
        if (successorTasks != null ? !successorTasks.equals(successorList.successorTasks) : successorList.successorTasks != null) return false;
        if (taskID != null ? !taskID.equals(successorList.taskID) : successorList.taskID != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (successorTasks != null ? successorTasks.hashCode() : 0);
        result = 29 * result + (taskID != null ? taskID.hashCode() : 0);
        result = 29 * result + (isLoaded ? 1 : 0);
        return result;
    }
}

