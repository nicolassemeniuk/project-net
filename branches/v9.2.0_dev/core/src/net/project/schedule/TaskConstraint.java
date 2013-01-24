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
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|
+----------------------------------------------------------------------*/
package net.project.schedule;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * The task constraint class is used to record information about a date or time
 * constraints that might apply to a class.
 *
 * @see TaskConstraintType
 * @author Matthew Flower
 * @since Version 7.4
 */
public class TaskConstraint implements IXMLPersistence, Cloneable {
    /** Identify which type of constraint this task has.  Eventually, this will
     be important for computing the task start and end date.  For now, it
     just displays a pretty string. */
    private TaskConstraintType type = TaskConstraintType.DEFAULT_TASK_CONSTRAINT;
    /** Some task constraints require a date, such as TaskConstraintType.FINISH_NO_LATER_THAN.
     This field gives a place to hold that information. */
    private Date constraintDate;
    /** Separate date that can apply an additional constraint. */
    private Date deadline;
    /** Task ID that this constraint is associated to. */
    private String taskID;
    /**
     * This is a "copy" of the task constraint from when the task constraint was
     * originally loaded.  If there are any differences between the "live" copy
     * and this backup, the task constraint will be saved when necessary.
     */
    private TaskConstraint lastSavedState = null;

    /**
     * Clear out any values sotred in this object.
     */
    public void clear() {
        type = TaskConstraintType.DEFAULT_TASK_CONSTRAINT;
        constraintDate = null;
        deadline = null;
        taskID = null;
    }

    public TaskConstraint() {
    }

    public TaskConstraint(TaskConstraintType type) {
        this.type = type;
    }

    /**
     * Get a date that corresponds to the current constraint type.  For example,
     * a TaskConstraint.FINISH_NO_LATER_THAN task constraint requires a date.
     *
     * @see #setConstraintDate(Date)
     * @return a <code>Date</code> value that corresponds to the current task
     * constraint type.
     */
    public Date getConstraintDate() {
        return constraintDate;
    }

    /**
     * Get a date (formatted for the current user) that corresponds to the current
     * constraint type.  For example, a TaskConstraint.FINISH_NO_LATER_THAN task
     * constraint requires a date.
     *
     * @see #getConstraintDate()
     * @return a <code>String</code> value containing the constratint date formatted
     * for the current user.
     */
    public String getConstraintDateString() {
        return SessionManager.getUser().getDateFormatter().formatDate(constraintDate);
    }

    /**
     * Set the date that corresponds to the current constraint type.
     *
     * @see #getConstraintDate()
     * @param constraintDate a date that is used with the constraint type to
     * determine start and end date of a task for certain constraint types.
     */
    public void setConstraintDate(Date constraintDate) {
        this.constraintDate = constraintDate;
    }

    /**
     * Get a date that the task must be completed by, regardless of constraint.
     *
     * @return a <code>Date</code> value that indicates the latest time a task
     * can be completed.
     * @see #setDeadline(Date)
     */
    public Date getDeadline() {
        return deadline;
    }

    /**
     * Get the deadline string formatted for the current user's locale.
     *
     * @return a <code>String</code> value formatted correctly for the current
     * user's locale.
     * @see #getDeadline()
     */
    public String getDeadlineString() {
        return SessionManager.getUser().getDateFormatter().formatDate(deadline);
    }

    /**
     * Set a date that the task must be completed by, regardless of constraint.
     *
     * @param deadline a <code>Date</code> value identifying a date that the
     * task must be completed by.
     * @see #getDeadline()
     */
    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    /**
     * Get the constraint type for this task constraint.  The valid values for
     * type are static variables in the {@link net.project.schedule.TaskConstraintType}
     * class.
     *
     * @return a <code>TaskConstraintType</code> object indicating the type of
     * constraint.
     * @see #setType(TaskConstraintType)
     */
    public TaskConstraintType getType() {
        return type;
    }

    /**
     * Set the constraint type for this task constraint.  This value values for
     * type are static variables in the {@link net.project.schedule.TaskConstraintType}
     * class.
     *
     * @param type a <code>TaskConstraintType</code> object indicating the type
     * of constraint.
     * @see #getType()
     */
    public void setType(TaskConstraintType type) {
        this.type = type;
    }

    /**
     * Set the task ID associated with this constraint.
     */
    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    /**
     * Get the task ID associated with this constraint.
     */
    public String getTaskID() {
        return taskID;
    }

    /**
     * Load information about this task constraint into the object based on the
     * task id provided by {@link #setTaskID}.
     *
     * @throws PersistenceException if the task constraint can't be loaded from
     * the database.
     */
    public void load() throws PersistenceException {
        DBBean db = new DBBean();

        try {
            db.prepareStatement("select task_id, constraint_type, constraint_date, " +
                "deadline from pn_task where task_id = ?");
            db.pstmt.setString(1, taskID);
            db.executePrepared();

            //Grab the values returned from the database
            if (db.result.next()) {
                setType(TaskConstraintType.getForID(db.result.getString("constraint_type")));
                setConstraintDate(db.result.getTimestamp("constraint_date"));
                setDeadline(db.result.getTimestamp("deadline"));
            } else {
                clear();
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(TaskConstraint.class).error(sqle.toString());
            throw new PersistenceException("Unexpected SQL exception thrown " +
                " while loading task constraint.", sqle);
        } finally {
            db.release();
        }

        setLastSaveState();
    }

    public void insert(DBBean db) throws SQLException {
        //Run the appropriate answer depending on whether the record exists
        db.prepareStatement("insert into pn_task_has_constraint (constraint_type," +
            "constraint_date,deadline,task_id) values (?,?,?,?)");
        setParameters(db);
        db.executePrepared();

        //Make sure this object knows how it looked when it was last saved, this
        //will prevent unnecessary saves
        setLastSaveState();
    }

    public void update(DBBean db) throws SQLException {
        if (this.equals(lastSavedState)) {
            return;
        }

        db.prepareStatement("update pn_task_has_constraint set constraint_type = ?, " +
            "constraint_date=?, deadline=? where task_id = ?");
        setParameters(db);
        db.executePrepared();

        //Make sure this object knows how it looked when it was last saved, this
        //will prevent unnecessary saves
        setLastSaveState();
    }

    public void setParameters(DBBean db) throws SQLException {
        db.pstmt.setString(1, type.getID());

        //Set the constraint date, if one is available
        if (constraintDate != null) {
            db.pstmt.setTimestamp(2, new Timestamp(constraintDate.getTime()));
        } else {
            db.pstmt.setNull(2, java.sql.Types.TIMESTAMP);
        }

        //Set the deadline, if one is available
        if (deadline != null) {
            db.pstmt.setTimestamp(3, new Timestamp(deadline.getTime()));
        } else {
            db.pstmt.setNull(3, java.sql.Types.TIMESTAMP);
        }

        db.pstmt.setString(4, taskID);
    }

    /**
     * Store this task constraint in the database.
     */
    public void store() throws PersistenceException {
        //If the task constraint hasn't changed since it was last changed, don't
        //store it.
        if (this.equals(lastSavedState)) {
            return;
        }

        DBBean db = new DBBean();

        try {
            //Check to see if the constraint has been stored already.  This is
            //an additional round trip that can be avoided if insert() or
            //update() is called directly instead.
            db.prepareStatement("select task_id from pn_task_has_constraint where task_id = ?");
            db.pstmt.setString(1, taskID);
            db.executePrepared();
            boolean recordExists = db.result.next();

            //Run the appropriate answer depending on whether the record exists
            if (recordExists) {
                update(db);
            } else {
                insert(db);
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(TaskConstraint.class).error(sqle.toString());
            throw new PersistenceException("Unexpected SQL exception thrown " +
                "while storing task constraint.", sqle.toString());
        } finally {
            db.release();
        }
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
        StringBuffer xml = new StringBuffer();
        xml.append("<TaskConstraint>");

        xml.append("  <tooltip>").append(PropertyProvider.get("prm.schedule.workplan.calendargraphic.taskcontsraint.tooltip") + "&#13;").append(getType().getName()).append((getType().isDateConstrained() ? " (" + getConstraintDateString() + ")" : "")).append("</tooltip>");
        xml.append("  <type>").append(getType().getName()).append("</type>");
        if (getType().isDateConstrained()) {
            xml.append("  <isDateConstrained/>");
        }
        if (getConstraintDate() == null) {
            xml.append("  <constraintDate/>\n");
        } else {
            xml.append("  <constraintDate>").append(XMLUtils.formatISODateTime(getConstraintDate())).append("</constraintDate>");
        }

        xml.append("  <deadline>").append(getDeadlineString()).append("</deadline>");
        xml.append("</TaskConstraint>");

        return xml.toString();
    }

    /**
     * Creates a copy of this object.
     *
     * @return     a clone of this instance.
     * @see Cloneable
     */
    protected Object clone() {
        TaskConstraint c = new TaskConstraint();
        c.type = type;
        c.constraintDate = constraintDate;
        c.deadline = deadline;
        c.taskID = taskID;

        return c;
    }

    /**
     * This method indicates that the current state that the object is in is the
     * state that is currently saved in the database for this object.  The
     * object should only be saved if it changes from this state.
     */
    protected void setLastSaveState() {
        lastSavedState = (TaskConstraint)this.clone();
    }

    /**
     * Compares two objects to determine if they are effectively equal.  This
     * equality is determined based on the equality of all "important" private
     * member variables.
     *
     * @param o a <code>TaskConstraint</code> object to compare to this one.
     * @return a <code>boolean</code> variable indicating whether the parameter
     * is equal to this object.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskConstraint)) return false;

        final TaskConstraint taskConstraint = (TaskConstraint)o;

        if (constraintDate != null ? !constraintDate.equals(taskConstraint.constraintDate) : taskConstraint.constraintDate != null) return false;
        if (deadline != null ? !deadline.equals(taskConstraint.deadline) : taskConstraint.deadline != null) return false;
        if (taskID != null ? !taskID.equals(taskConstraint.taskID) : taskConstraint.taskID != null) return false;
        if (type != null ? !type.equals(taskConstraint.type) : taskConstraint.type != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (type != null ? type.hashCode() : 0);
        result = 29 * result + (constraintDate != null ? constraintDate.hashCode() : 0);
        result = 29 * result + (deadline != null ? deadline.hashCode() : 0);
        result = 29 * result + (taskID != null ? taskID.hashCode() : 0);
        return result;
    }
}
