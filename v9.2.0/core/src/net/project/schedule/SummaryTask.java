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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import net.project.base.finder.NumberComparator;
import net.project.base.finder.NumberFilter;
import net.project.persistence.PersistenceException;
import net.project.schedule.calc.TaskCalculationType;
import net.project.util.TimeQuantity;

/**
 * Class representing a summary task on a schedule.
 *
 * @author Matthew Flower
 * @since Version 7.6
 */
public class SummaryTask extends ScheduleEntry {
    /**
     * This is a list of this summary task's "children".
     */
    private Set children = null;
    /**
     * The children that don't have any successor dependencies.
     */
    private Set childrenWithoutSuccessors = null;
    /**
     * The child tasks that don't have any predecessor dependencies.
     */
    private Set childrenWithoutPredecessors = null;

    /**
     * Reset the state of the task object's internal variables to their
     * default values.
     */
    public void clear() {
        super.clear();
        children = null;
    }

    /**
     * Get the type of task.
     *
     * @return a <code>TaskType</code> object which represents the type of
     * schedule entry this object is.  Until we implement summary tasks
     * correctly, this will always be equal to
     * {@link net.project.schedule.TaskType#TASK}.
     */
    public TaskType getTaskType() {
        return TaskType.SUMMARY;
    }

    /**
     * This overrides the getConstraintType of {@link ScheduleEntry}.
     * This is an expermentational method, as from 8.3 onwards we are not setting
     * any constraints for SummaryTask since its values are derived from the children tasks.
     * 
     *  @see ScheduleEntry#getTaskCalculationType()
     */
    public TaskConstraintType getConstraintType() {
        return TaskConstraintType.DEFAULT_TASK_CONSTRAINT;
    }
    
    /**
     * Get the id of the constraint type for this task.  
     * This overrides the getConstraintType of {@link ScheduleEntry}.
     * This is an expermentational method, as from 8.3 onwards we are not setting
     * any constraints for SummaryTask since its values are derived from the children tasks.
     *
     * @return a <code>String</code> value containing the constraint type id.
     */
    public String getConstraintTypeID() throws PersistenceException {
        return TaskConstraintType.DEFAULT_TASK_CONSTRAINT.getID();
    }
    
    /**
     * The overridden version of getTaskCalculationType only returns
     * fixed duration non-effort driven because this is the only task type that
     * a summary task can take.
     *
     * @return {@link TaskCalculationType#FIXED_DURATION_NON_EFFORT_DRIVEN}
     */
    public TaskCalculationType getTaskCalculationType() {
        return TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN;
    }

    //sjmittal: fix for bfd 3927 we don't change the base TaskCalculationType
    //as this summary task may become normal task at a later date.
    //as we also look above that for others like TaskConstraintType we have only
    //overridden the getter methods.
//    /**
//     * This overridden version of setTaskCalculationType ignores the type that
//     * you send to it, because SummaryTasks can only be
//     * {@link TaskCalculationType#FIXED_DURATION_NON_EFFORT_DRIVEN}.  If
//     * assertions are enabled, an assertion will be raised if a task calculation
//     * type other than {@link TaskCalculationType#FIXED_DURATION_NON_EFFORT_DRIVEN}
//     * is passed in.
//     *
//     * @param calculationType a <code>TaskCalculationType</code> which corresponds
//     * to this summary task.
//     */
//    public void setTaskCalculationType(TaskCalculationType calculationType) {
//        super.setTaskCalculationType(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN);
//    }

    /**
     * Get the actual duration for this summary task.  This is computed somewhat
     * differently than it is with tasks.  Normally, we can use % work complete
     * multiplied by the duration.  This is possible for summary tasks because
     * if a resource is assigned to the summary task itself, it contributes
     * additional work.
     *
     * @return a <code>TimeQuantity</code> referring to the actual duration of
     * this summary task.
     */
    public TimeQuantity getActualDuration() {
        return getDurationTQ().multiply(getPercentCompleteDecimal());
    }

    /**
     * Gets the <code>ScheduleEntry</code> objects which call this SummaryTask
     * their "parent task".
     * <p>
     * The children are lazy-loaded the first time this method is called.
     * </p>
     * @return a <code>Set</code> containing one or more <code>ScheduleEntry</code>
     * subclasses
     * @throws PersistenceException if there is an error loading the subclasses.
     */
    public Set getChildren() throws PersistenceException {
        if (children == null) {
            //Use the task finder to find the children of this object.
            TaskFinder tf = new TaskFinder();

            //If the task doesn't have an id, its children won't be in the
            //database yet.
            if (getID() != null) {
                //Add a filter so we only return this tasks children
                NumberFilter parentTaskIDFilter =
                    new NumberFilter("10", TaskFinder.PARENT_TASK_ID_COLUMN, false);
                parentTaskIDFilter.setNumber(Integer.parseInt(getID()));
                parentTaskIDFilter.setComparator(NumberComparator.EQUALS);
                parentTaskIDFilter.setSelected(true);
                tf.addFinderFilter(parentTaskIDFilter);
                children = new LinkedHashSet(tf.findBySpaceID(getSpaceID()));
            } else {
                children = new LinkedHashSet();
            }
        }

        return children;
    }

    /**
     * Get the children of this parent task, but don't load them from the
     * database if they haven't been loaded.
     *
     * @return a <code>List</code> containing children, if they've already been
     * loaded or null if {@link #getChildren} has never been called.
     */
    public Set getChildrenNoLazyLoad() {
        return children;
    }

    /**
     * Get the set of all children that don't have a successor task.
     *
     * @return a <code>Set</code> of all children that don't have a successor
     * task.
     * @throws PersistenceException if there is an error loading children from
     * the database.  Note that they won't be reloaded if they have already
     * been loaded.
     */
    public Set getChildrenWithoutSuccessors() throws PersistenceException {
        if (childrenWithoutSuccessors == null) {
            //We use a bit of magic here to duplicate the Set.  The Collections
            //utility class doesn't have a way to duplicate a set.
            childrenWithoutSuccessors = new LinkedHashSet(Arrays.asList(getChildren().toArray()));

            for (Iterator it = childrenWithoutSuccessors.iterator(); it.hasNext();) {
                ScheduleEntry scheduleEntry = (ScheduleEntry)it.next();
                if (!scheduleEntry.getSuccessors().isEmpty()) {
                    it.remove();
                }
            }
        }

        return childrenWithoutSuccessors;
    }

    /**
     * Set the list of children for this parent task.
     *
     * @param children a <code>List</code> containing zero or more children of
     * this task.
     */
    void setChildren(Set children) {
        this.children = children;
    }

    public Collection getChildrenWithoutPredecessors() throws PersistenceException {
        if (childrenWithoutPredecessors == null) {
            childrenWithoutPredecessors = new LinkedHashSet(Arrays.asList(getChildren().toArray()));

            for (Iterator it = childrenWithoutPredecessors.iterator(); it.hasNext(); ) {
                ScheduleEntry scheduleEntry = (ScheduleEntry)it.next();
                if (!scheduleEntry.getPredecessors().isEmpty()) {
                    it.remove();
                }
            }
        }

        return childrenWithoutPredecessors;
    }

    public Object clone() {
        SummaryTask clone = (SummaryTask)super.clone();

        if (clone.children != null) {
            clone.children = new HashSet();
            clone.children.addAll(children);
        }
        if (clone.childrenWithoutPredecessors != null) {
            clone.childrenWithoutPredecessors = new HashSet();
            clone.childrenWithoutPredecessors.addAll(childrenWithoutPredecessors);
        }
        if (clone.childrenWithoutSuccessors != null) {
            clone.childrenWithoutSuccessors = new HashSet();
            clone.childrenWithoutSuccessors.addAll(childrenWithoutSuccessors);
        }

        return clone;
    }
}
