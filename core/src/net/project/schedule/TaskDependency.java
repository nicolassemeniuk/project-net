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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import net.project.base.PnetRuntimeException;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.TextFormatter;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

import org.apache.log4j.Logger;

/**
 * This class stores information that relates the start or end date of one task
 * with the start or end date of another task.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class TaskDependency implements IXMLPersistence, Cloneable, Serializable {

    /**
     * The scale to round lag to when storing.
     * Currently 3.
     * This number was chosen arbitrarily.
     */
    private static final int LAG_AMOUNT_SCALE = 3;

    /** The task id of the task which has this dependency. */
    private String taskID;
    /** The task that a task has dependency upon. */
    private ScheduleEntry dependency = new Task();
    /** The dependency type for this dependency. */
    private TaskDependencyType dependencyType = TaskDependencyType.DEFAULT;
    /** The scalar amount of lag time between these two tasks. */
    private TimeQuantity lag = new TimeQuantity(0, TimeQuantityUnit.DAY);
    /**
     * Task name of the dependent task.  This is for display purposes only,
     * it isn't saved on store().
     */
    private String taskName;
    /**
     * Sequence number of the dependent task.  This is for display purposes only,
     * it isn't saved on store.
     */
    private int sequenceNumber;
    /** Start date of the dependent task.  This is for display purposes only, it
     * isn't saved on store(). */
    private Date startDate;
    /** Finish date of the dependent task.  This is for display purposes only,
     * it isn't saved on store(). */
    private Date finishDate;

    /**
     * This copy of the TaskDependency object represents its state when it was
     * loaded from the database.  The TaskDependency object will only be saved
     * if it differs from this representation.
     */
    private TaskDependency lastSavedState;

    public TaskDependency() {
    }

    public TaskDependency(ScheduleEntry task, ScheduleEntry dependentTask, TaskDependencyType type, TimeQuantity lag) {
        this.taskID = task.getID();
        this.dependency = dependentTask;
        this.dependencyType = type;
        this.lag = lag;
    }

    public TaskDependency(String taskID, ScheduleEntry dependentTask, TaskDependencyType type, TimeQuantity lag) {
        this.taskID = taskID;
        this.dependency = dependentTask;
        this.dependencyType = type;
        this.lag = lag;
    }

    /**
     * Set the ID for the task that has this dependency.
     *
     * @see #getTaskID
     * @param taskID a <code>String</code> value containing the ID of the task
     * that has the dependency.
     */
    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    /**
     * Get the id of the task that has this dependency.
     *
     * @see #setTaskID
     * @return a <code>String</code> value containing the ID of the task that has
     * this dependency.
     */
    public String getTaskID() {
        return taskID;
    }

    /**
     * Set the relationship between this task and its dependency task.
     *
     * @param dependencyType a <code>TaskDependencyType</code> which identifies
     * the relationship between this task and its dependent task.
     */
    public void setDependencyType(TaskDependencyType dependencyType) {
        this.dependencyType = dependencyType;
    }

    /**
     * Get the relationship between the task and the dependent task.
     *
     * @return a {@link net.project.schedule.TaskDependencyType} object which
     * describes the relationship between the task and the dependent task.
     */
    public TaskDependencyType getDependencyType() {
        return dependencyType;
    }


    /**
     * Set the ID of the task that a task is dependent upon.
     *
     * @see #getDependencyID
     * @param dependencyID a <code>String</code> value containing the primary
     * key of the task that a task is dependent upon.
     */
    public void setDependencyID(String dependencyID) {
        this.dependency.setID(dependencyID);
    }

    /**
     * Get the ID of the task that another task is dependent upon.
     *
     * @see #setDependencyID
     * @return a <code>String</code> value containing the id for a task that
     * another task is dependent upon.
     */
    public String getDependencyID() {
        return this.dependency.getID();
    }

    /**
     * Set the task that this task depends upon.  It is not a requirement that
     * this task be loaded, though its task id should be set.
     *
     * @param dependency a <code>Task</code> object which the task identified
     * by <code>getTask</code> is dependent upon.
     */
    private void setDependency(ScheduleEntry dependency) {
        this.dependency = dependency;
    }

    /**
     * Set the type of this dependency.  Types for dependency are START_TO_START
     * START_TO_FINISH, FINISH_TO_START, AND FINISH_TO_FINISH.  Constants
     * corresponding to these types can be found as static members of this class.
     *
     * @see #getDependencyTypeID
     * @param dependencyTypeID a <code>String</code> value containing the ID that
     * identifies a dependency type that pertains to this TaskDependency.
     */
    public void setDependencyTypeID(String dependencyTypeID) {
        this.dependencyType = TaskDependencyType.getForID(dependencyTypeID);
    }

    /**
     * Identifies which type of dependency relationship that the task has to its
     * dependent task.
     *
     * @see #setDependencyTypeID
     * @return a <code>String</code> value containing a constant for the
     * dependency type.
     */
    public String getDependencyTypeID() {
        return dependencyType.getID();
    }

    /**
     * Set the lag time for this task dependency.
     *
     * @param lag a <code>BigDecimal</code> value containing a scalar number of lag
     * units.  (e.g. 5)
     * @param lagUnitID a <code>String</code> value containing the id that corresponds
     * to a <code>TimeQuantityUnit</code> database id.
     * @see #getLag
     */
    public void setLag(Number lag, String lagUnitID) {
        setLag(new TimeQuantity(lag, TimeQuantityUnit.getForID(Integer.parseInt(lagUnitID))));
    }

    /**
     * The amount of time between the dependency task and this task.
     *
     * @param timeQuantity a <code>TimeQuantity</code> describing the amount
     * of time between the dependency task and this task.
     */
    public void setLag(TimeQuantity timeQuantity) {
        this.lag = timeQuantity;
    }

    /**
     * Get the amount of lag time related to this task dependency.
     *
     * @return a <code>String</code> value containing the amount of time that
     * this task dependency identifies as lag.
     */
    public String getLagString() {
        return lag.toString();
    }

    /**
     * Get the amount of time between the dependent task and this task.
     *
     * @return a <code>TimeQuantity</code> object which indicates the amount of
     * lag time.
     */
    public TimeQuantity getLag() {
        return lag;
    }

    /**
     * Get the name of task which has a dependent task.
     *
     * @return a <code>String</code> value which is the name of the task.
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * This method also returns the name of the task, but if the name is longer
     * than 40 characters, it truncates it to 40 characters and appends "...".
     *
     * @return a <code>String</code> containing the task name, unless the task
     * name is longer than 40 characters.
     */
    public String getTaskNameMaxLength40() {
        return TextFormatter.truncateString(taskName, 40);
    }

    /**
     * Set the name of the task which has the task dependency.
     *
     * @param taskName a <code>String</code> value containing the name of the
     * task.
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * Get the start date of the dependent task formatted as a String specific
     * to the current user's locale.
     *
     * @return a <code>String</code> value which contains the start date of the
     * dependent task formatted for the current user's locale.
     */
    public String getStartDateString() {
        return SessionManager.getUser().getDateFormatter().formatDate(startDate);
    }

    /**
     * Set the start date of the dependent task.  This value is only used for
     * display purposes, it is not saved when the store method is called.
     *
     * @param startDate a <code>Date</code> value identifying the start date of
     * the dependent task.
     */
    void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Get the finish date of the dependent task formatted for the current user's
     * preferences.
     *
     * @return a <code>String</code> value which contains the finish date for
     * the current user's preferences.
     */
    public String getFinishDateString() {
        return SessionManager.getUser().getDateFormatter().formatDate(finishDate);
    }

    /**
     * Get the finish date of the dependent task.  Note that this value is used
     * only for display purposes.  It is not stored when the {@link #store} method
     * is called.
     *
     * @param finishDate a <code>Date</code> value which identifies the finish
     * date of the dependent task.
     */
    void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    /**
     * Store this task dependency in the database.
     *
     * @throws PersistenceException if an error occurs storing this dependency
     * in the database.
     */
    private void store(boolean updateIfExists) throws PersistenceException {
        //Only store this object if it has changed.
        if (this.equals(lastSavedState)) {
            return;
        }

        DBBean db = new DBBean();

        try {
            db.setAutoCommit(false);
            store(db, updateIfExists);
            db.commit();
        } catch (SQLException sqle) {
        	Logger.getLogger(TaskDependency.class).error("Unexpected SQL Exception occurred while" +
                "storing task dependency: " + sqle);
            throw new PersistenceException("Unexpected SQLException thrown.", sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Store the task dependency in the database using the transaction that is
     * currently in progress in the DBBean.  This method is guaranteed to not
     * commit or rollback the transaction -- if there is a problem, a SQL
     * exception will be thrown to the calling method.
     *
     * @param db a {@link net.project.database.DBBean} object which this method
     * will use to store itself in the database.
     * @throws SQLException if any error occurs while storing this task
     * dependency in the database.
     */
    public void store(DBBean db) throws SQLException {
        store(db, true);
    }

    /**
     * Store the task dependency in the database using the transaction that is
     * currently in progress in the DBBean.  This method is guaranteed to not
     * commit or rollback the transaction -- if there is a problem, a SQL
     * exception will be thrown to the calling method.
     *
     * @param db a {@link net.project.database.DBBean} object which this method
     * will use to store itself in the database.
     * @throws SQLException if any error occurs while storing this task
     * dependency in the database.
     */
    public void store(DBBean db, boolean updateIfExists) throws SQLException {
        //Only store this object if it has changed.
        if (this.equals(lastSavedState)) {
            return;
        }

        prepareStoreStatement(db);
        setStoreParameters(db.cstmt, updateIfExists);
        db.executeCallable();

        setLastSaveState();
    }

    public static void prepareStoreStatement(DBBean db) throws SQLException {
        db.prepareCall("{ call SCHEDULE.STORE_TASK_DEPENDENCY(?,?,?,?,?,?) }");
    }

    public void setStoreParameters(PreparedStatement stmt, boolean updateIfExists) throws SQLException {
        stmt.setString(1, taskID);
        stmt.setString(2, dependencyType.getID());
        stmt.setBigDecimal(3, lag.getAmount());
        stmt.setInt(4, lag.getUnits().getUniqueID());
        stmt.setString(5, dependency.getID());
        stmt.setBoolean(6, updateIfExists);
    }

    /**
     * Converts the object to XML representation. This method returns the
     * object as XML text.
     *
     * @return XML representation
     */
    public String getXML() {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }

    /**
     * Converts the object to XML representation without the XML version tag.
     * This method returns the object as XML text.
     *
     * @return XML representation
     */
    public String getXMLBody() {
        final StringBuffer xml = new StringBuffer();
        xml.append("<TaskDependency>");
        xml.append("  <taskID>").append(getTaskID()).append("</taskID>");
        xml.append("  <dependencyID>").append(getDependencyID()).append("</dependencyID>");
        xml.append("  <typeID>").append(getDependencyTypeID()).append("</typeID>");
        xml.append("  <lag>").append(getLag()).append("</lag>");
        xml.append("</TaskDependency>");

        return xml.toString();
    }

    /**
     * Links the tasks with the specified IDs.
     * <p>
     * Tasks are linked in pairs in sequential order.  For example, if idList contains <code>1, 2, 3</code>
     * the resulting dependencies are <code>(2, 1)</code> and <code>(3, 2)</code>.
     * </p>
     * @param idList the IDs of the tasks to links
     * @param schedule the current schedule
     * @return an error reporter that may contain zero or more errors depending on
     * whether any problems occurred
     * @throws PersistenceException if there is a problem loading any tasks
     */
    public static ErrorReporter linkTasks(String[] idList, Schedule schedule) throws PersistenceException {
        ErrorReporter er = new ErrorReporter();
        Logger logger = Logger.getLogger(TaskDependency.class);
        CyclicDependencyDetectorV2 crd = new CyclicDependencyDetectorV2();
        Map tasks = schedule.getEntryMap();

        //We intentionally skip the first task
        String lastID = idList[0];
        for (int i = 1; i < idList.length; i++) {
            String id = idList[i];

            ScheduleEntry linker = (ScheduleEntry)tasks.get(id);
            ScheduleEntry linkee = (ScheduleEntry)tasks.get(lastID);

            //Create the new task dependency
            TaskDependency td = new TaskDependency();
            td.setTaskID(id);
            td.setDependency(linkee);
            td.setDependencyID(lastID);
            td.setDependencyType(TaskDependencyType.FINISH_TO_START);

            //Now check to make sure that we aren't creating a cyclic dependency
            ScheduleEntry se = (ScheduleEntry)tasks.get(id);
            se.getPredecessors().add(td);

            //If there are any dependency cycles, we cannot store this
            //dependency.  We will append the error to the error reporter so
            //the user will know about it.
            boolean hasCycle = false;
            try {
                hasCycle = crd.hasCycle(se, schedule);
            } catch (Exception e) {
                se.getPredecessors().remove(td);
                logger.debug("Unexpected cycle for task <- dependency "+ id + " <- " + lastID);
                throw new PnetRuntimeException("Unexpected cycle in dependency cycle.  No data has been saved.", e);
            }
            if (hasCycle) {
                //Add an error with these dependencies
                ErrorDescription ed = new ErrorDescription(PropertyProvider.get("prm.schedule.main.linktasks.error.dependencycycle.message", linkee.getName(), linker.getName()));
                er.addError(ed);
                //Remove the dependency
                se.getPredecessors().remove(td);
            } else {
                //We tell the task dependencies to not overwrite if there is an
                //existing link so we can prevent overwriting any custom lag
                //the user might have indicated previously.
                td.store(false);
            }

            lastID = id;
        }
        if (schedule.isAutocalculateTaskEndpoints()) {
            schedule.recalculateTaskTimes();
        }
        return er;
    }

    /**
     * This method will remove any links between any of the schedule entries
     * identified in the idList.
     *
     * @param idList a <code>String[]</code> of ids of Schedule entries that we
     * would like to remove the dependencies between.
     * @param schedule a <code>Schedule</code> that contains the tasks we'd like
     * to unlink.  We use this to recalculate task times.
     * @throws PersistenceException if there is an error unlinking these tasks.
     */
    public static void unlinkTasks(String[] idList, Schedule schedule) throws PersistenceException {
        String idCSV = DatabaseUtils.collectionToCSV(Arrays.asList(idList));

        //Remove them from the database
        DBBean db = new DBBean();
        try {
            db.prepareStatement("delete from pn_task_dependency where task_id in ("+idCSV+") and dependency_id in ("+idCSV+")");
            db.executePrepared();
        } catch (SQLException sqle) {
            throw new PersistenceException("Unexpected error occurred while unlinking tasks.", sqle);
        } finally {
            db.release();
        }
        if (schedule.isAutocalculateTaskEndpoints()) {
            schedule.recalculateTaskTimes();
        }
    }

    /**
     * Indicates if this <code>TaskDependency</code> object is identical to
     * to the object supplied by the parameter.  This equality is based on the
     * value of the private member variables of both TaskDependencies.
     *
     * @param o a <code>Object</code> to compare to this Task CyclicDependencyDetector.
     * @return a <code>boolean</code> indicating if these task dependency
     * objects are identical.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskDependency)) return false;

        final TaskDependency taskDependency = (TaskDependency)o;

        if (dependency != null ? !dependency.getID().equals(taskDependency.dependency.getID()) : taskDependency.dependency.getID() != null) return false;
        if (dependencyType != null ? !dependencyType.equals(taskDependency.dependencyType) : taskDependency.dependencyType != null) return false;
        if (lag != null ? !lag.equals(taskDependency.lag) : taskDependency.lag != null) return false;
        if (taskID != null ? !taskID.equals(taskDependency.taskID) : taskDependency.taskID != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (taskID != null ? taskID.hashCode() : 0);
        result = 29 * result + (dependency != null ? dependency.hashCode() : 0);
        result = 29 * result + (dependencyType != null ? dependencyType.hashCode() : 0);
        result = 29 * result + (lag != null ? lag.hashCode() : 0);
        return result;
    }

    /**
     * Performs a "deep clone" of this task dependency.
     * <p>
     * The contained CyclicDependencyDetector task is cloned.
     * </p>
     * @return
     */
    protected Object clone() {
        TaskDependency clone = new TaskDependency();

        // Deep clone the dependency task so that modifying its
        // ID later won't affect original object
        clone.dependency = (ScheduleEntry) this.dependency.clone();

        // Remaining properties are immutable
        clone.dependencyType = this.dependencyType;
        clone.lag = this.lag;
        clone.taskID = this.taskID;

        return clone;
    }

    /**
     * This method indicates that the current state that the object is in is the
     * state that is currently saved in the database for this object.  The
     * object should only be saved if it changes from this state.
     */
    protected void setLastSaveState() {
        lastSavedState = (TaskDependency)this.clone();
    }

    /**
     * Get the sequence number of the dependent task.
     *
     * @return a <code>int</code> corresponding to the sequence number of the
     * dependent task.
     */
    public int getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Set the sequence number of the dependent task.
     *
     * @param sequenceNumber a <code>int</code> corresponding to the sequence
     * number of the dependent task.
     */
    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Indicates if the task dependency has been modified since the last time
     * the TaskDependency was stored.
     *
     * @return a <code>boolean</code> indicating if the task dependency has been
     * modified since the last time it was stored.
     */
    public boolean isModified() {
        return !this.equals(lastSavedState);
    }
}
