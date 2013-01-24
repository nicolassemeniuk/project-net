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
import java.util.ArrayList;
import java.util.Iterator;

import net.project.base.property.PropertyProvider;

/**
 * Types of tasks that derive from ScheduleEntry.
 *
 * @author Matthew Flower
 * @since Version 7.4.
 */
public class TaskType implements Serializable {
    /** The list of all known task types. */
    private static ArrayList taskTypes = new ArrayList();
    /** The type for a Summary Task. */
    public static final TaskType SUMMARY = new TaskType("summary", "prm.schedule.task.tasktype.summary.name");
    /** The type for a normal task. */
    public static final TaskType TASK = new TaskType("task", "prm.schedule.task.tasktype.task.name");
    /** The type for a milestone. */
    public static final TaskType MILESTONE = new TaskType("milestone", "prm.schedule.task.tasktype.milestone.name");
    /** An array of all know task types. */
    public static final TaskType[] ALL = new TaskType[]{SUMMARY, TASK, MILESTONE};

    /**
     * Get the task type that corresponds to the supplied id.
     *
     * @param id a <code>String</code> value which contains the unique identifier
     * for the task type that we are trying to find.
     * @return a <code>TaskType</code> which corresponds to the ID passed to this
     * method, or null if one isn't found that matches.
     */
    public static TaskType getForID(String id) {
        TaskType toReturn = null;

        for (Iterator it = taskTypes.iterator(); it.hasNext();) {
            TaskType current = (TaskType)it.next();
            if (current.getID().equals(id)) {
                toReturn = current;
            }
        }

        return toReturn;
    }

    //--------------------------------------------------------------------------
    //Implementation methods
    //--------------------------------------------------------------------------
    /** A unique identifier for this task type. */
    private final String id;
    /** A token pointing to the human-readable name for this task type. */
    private final String nameToken;

    /**
     * Standard constructor for task types.
     *
     * @param id a <code>String</code> value containing a unique identifier for
     * this task type.
     * @param nameToken a <code>String</code> value which contains a token which
     * points to a human readable name for this task type.
     */
    protected TaskType(String id, String nameToken) {
        taskTypes.add(this);
        this.id = id;
        this.nameToken = nameToken;
    }

    /**
     * Get a string which uniquely identifies this task type.
     *
     * @return a <code>String</code> value which uniquely identifies this task
     * type.
     */
    public String getID() {
        return id;
    }

    /**
     * Get a human readable name for this task type.
     *
     * @return a <code>String</code> value which contains a human readable name
     * for this task type.
     */
    public String getName() {
        return PropertyProvider.get(nameToken);
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The <code>toString</code> method for class <code>Object</code>
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `<code>@</code>', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return  a string representation of the object.
     */
    public String toString() {
        return id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskType)) {
            return false;
        }

        final TaskType taskType = (TaskType) o;

        if (!id.equals(taskType.id)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return id.hashCode();
    }
}
