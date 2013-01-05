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
import java.util.List;

import net.project.base.property.PropertyProvider;

/**
 * A class which represents the types of Task Constraints that are supported by
 * the advanced tab in Task.
 *
 * This class uses the Typesafe enumeration pattern.
 *
 * @author Matthew Flower.
 */
public class TaskConstraintType implements Serializable {
    //--------------------------------------------------------------------------
    //Static Part of Task Constraints
    //--------------------------------------------------------------------------
    private static ArrayList taskConstraintList = new ArrayList();

    //Task constraint constants -- these are the only type of tasks that will
    //ever exist because we have defined the constructor as private
    public static TaskConstraintType AS_SOON_AS_POSSIBLE = new TaskConstraintType("10", "prm.schedule.task.constraint.assoonaspossible.name", false, true, false);
    public static TaskConstraintType AS_LATE_AS_POSSIBLE = new TaskConstraintType("20", "prm.schedule.task.constraint.aslateaspossible.name", false, false, true);
    public static TaskConstraintType FINISH_NO_LATER_THAN = new TaskConstraintType("30", "prm.schedule.task.constraint.finishnolaterthan.name", true, true, false);
    public static TaskConstraintType FINISH_NO_EARLIER_THAN = new TaskConstraintType("40", "prm.schedule.task.constraint.finishnoearlierthan.name", true, true, false);
    public static TaskConstraintType MUST_START_ON = new TaskConstraintType("50", "prm.schedule.task.constraint.muststarton.name", true, false, false);
    public static TaskConstraintType MUST_FINISH_ON = new TaskConstraintType("60", "prm.schedule.task.constraint.mustfinishon.name", true, false, false);
    public static TaskConstraintType START_NO_EARLIER_THAN = new TaskConstraintType("70", "prm.schedule.task.constraint.startnoearlierthan.name", true, true, false);
    public static TaskConstraintType START_NO_LATER_THAN = new TaskConstraintType("80", "prm.schedule.task.constraint.startnolaterthan.name", true, true, false);
    
    //sjmittal: this constraint is issued only for tasks that are created from shared tasks
    //MSP does not have this constraint. the idea of this constraint is that not makes the external tasks shared as readonly
    //whose start date and and date match the original tasks and keeping this constraint TEC does not alter it in any way
    public static TaskConstraintType START_AND_END_DATES_FIXED = new TaskConstraintType("90", "prm.schedule.task.constraint.startandenddatesfixed.name", false, false, false);

    public static TaskConstraintType DEFAULT_TASK_CONSTRAINT = AS_SOON_AS_POSSIBLE;

    /**
     * Get the TaskConstraintType that corresponds to a given ID string.
     *
     * @param id a <code>String</code> value that represents a TaskConstraintType.
     * @return a <code>TaskConstraintType</code> object which corresponds to the
     * id parameter passed to this method.
     */
    public static TaskConstraintType getForID(String id) {
        TaskConstraintType taskConstraintToReturn = null;
        Iterator it = taskConstraintList.iterator();

        while (it.hasNext()) {
            TaskConstraintType testConstraint = (TaskConstraintType)it.next();
            if (testConstraint.getID().equals(id)) {
                taskConstraintToReturn = testConstraint;
            }
        }

        return taskConstraintToReturn;
    }

    public static TaskConstraintType getForMSPID(int id) {
        switch (id) {
            case 0:
                return AS_SOON_AS_POSSIBLE;
            case 1:
                return AS_LATE_AS_POSSIBLE;
            case 2:
                return MUST_START_ON;
            case 3:
                return MUST_FINISH_ON;
            case 4:
                return START_NO_EARLIER_THAN;
            case 5:
                return START_NO_LATER_THAN;
            case 6:
                return FINISH_NO_EARLIER_THAN;
            case 7:
                return FINISH_NO_LATER_THAN;
            default:
                throw new RuntimeException("Invalid constraint type.");
        }
    }

    /**
     * Get the HTML for all of the <option> tags which represent that available
     * task constraint types.  We do not return the surrounding <select> tags as
     * the HTML author might need to set custom values in that tag.
     *
     * @return a <code>String</code> value containing all of the HTML select
     * values.
     */
    public static String getHTMLOptionList() {
        return getHTMLOptionList(DEFAULT_TASK_CONSTRAINT, false);
    }

    /**
     * Get the HTML for all of the <option> tags which represent that available
     * task constraint types.  We do not return the surrounding <select> tags as
     * the HTML author might need to set custom values in that tag.
     *
     * @param selectedConstraint a <code>TaskConstraintType</code> object which
     * represents the default value selected in the select list.
     * @return a <code>String</code> value containing all of the HTML select
     * values.
     */
    public static String getHTMLOptionList(TaskConstraintType selectedConstraint, boolean isSummaryTask) {
        StringBuffer html = new StringBuffer();

        Iterator it = taskConstraintList.iterator();
        while (it.hasNext()) {
            TaskConstraintType tc = (TaskConstraintType)it.next();
            if(START_AND_END_DATES_FIXED.equals(tc))
                continue;
            if (!isSummaryTask || DEFAULT_TASK_CONSTRAINT.equals(tc)) {
                html.append("<option value=\"").append(tc.getID()).append("\"")
                    .append((selectedConstraint.equals(tc) ? " SELECTED" : ""))
                    .append(">").append(tc.getName()).append("</option>\r\n");
            }
        }

        return html.toString();
    }


    /**
     * Get a list of all available task constraints.
     *
     * @return a <code>List</code> value containing all of the possible TaskConstraintList
     * objects in the system.
     */
    public static List getTaskConstraintList() {
        return taskConstraintList;
    }

    /**
     * This method returns a space separate string containing the ids of the
     * task constraints that require dates.  This is provided primarily so
     * JavaScript can identify if the user has entered all the required data.
     *
     * @return a <code>String</code> containing the task constraint ids
     * separated by spaces.
     */
    public static String getConstraintTypesRequiringDates() {
        StringBuffer constraintTypesRequiringDates = new StringBuffer();
        Iterator it2 = TaskConstraintType.getTaskConstraintList().iterator();
        while (it2.hasNext()) {
            TaskConstraintType tct = (TaskConstraintType)it2.next();
            if (tct.isDateConstrained()) {
                constraintTypesRequiringDates.append(tct.getID() + " ");
            }
        }

        return constraintTypesRequiringDates.toString();
    }

    //--------------------------------------------------------------------------
    //Implementation of the instantiable part of Task Constraints
    //--------------------------------------------------------------------------
    private final String nameToken;
    private final String id;
    private final boolean isDateConstrained;
    private final boolean earlyStarter;
    private final boolean lateStarter;
    //sjmittal: I checked the MS project documentation and there is no such rule that
    //following tasks constratints cannot be applicable to summary task
    //further in our TEC we also never make use of this field
//    private final boolean supportsSummaryTasks;

    /**
     * Standard constructor.
     *
     * @param id a <code>String</code> value which uniquely identifies this task
     * constraint type.  All <code>TaskConstraintType</code> objects need to have
     * a unique id.
     * @param nameToken a <code>String</code> value containing the token that
     * should be used to look up a display name for this type.
     * @param isDateConstrained a <code>boolean</code> value indicating whether
     * a date is required to completed describe this date type.  For example,
     * a "Finish No Later Than" constraint requires a date to fully describe
     * itself.
     * @param earlyStarter
     * @param lateStarter
     */
    private TaskConstraintType(String id, String nameToken, boolean isDateConstrained, boolean earlyStarter, boolean lateStarter) {
        this.id = id;
        this.nameToken = nameToken;
        this.isDateConstrained = isDateConstrained;
        this.earlyStarter = earlyStarter;
        this.lateStarter = lateStarter;
//        this.supportsSummaryTasks = supportsSummaryTasks;

        //Add this value to the list of all available task constraints
        taskConstraintList.add(this);
    }

    /**
     * Get the human-readable name of this task constraint type.  This is based
     * on the token that was passed to the constructor of this object.
     *
     * @return a human-readable string identifying this task constraint.
     */
    public String getName() {
        return PropertyProvider.get(nameToken);
    }

    /**
     * Get the token which is used to load the name of this task constraint type.
     *
     * @return a <code>String</code> value used to load the human-readable name
     * of this task constraint type.
     * @see #getName
     */
    public String getNameToken() {
        return nameToken;
    }

    /**
     * Get the unique identifier used to identify this task constraint type.
     *
     * @return a <code>String</code> value which uniquely identifies this task
     * constraint type.
     */
    public String getID() {
        return id;
    }

    /**
     * This method indicates whether a date is required to completed describe this
     * task constraint.  For example, a "Finish No Later Than" constraint
     * requires a date to fully describe itself.
     * @return a <code>boolean</code> value indicating whether a date is needed
     * to fully describe the date constraint.
     */
    public boolean isDateConstrained() {
        return isDateConstrained;
    }

    /**
     * Return a string identifying this Task Constraint Type.
     *
     * @return a <code>String</code> value identifying this task constraint type.
     */
    public String toString() {
        return getName() + "(" + getID() + ")";
    }

    /**
     * This method indicates whether tasks with this constraint type prefer to
     * start as early as possible.
     *
     * @return a <code>boolean</code> value indicating if this type of task
     * constraint wants to start as early as possible.
     */
    public boolean isEarlyStarter() {
        return earlyStarter;
    }

    /**
     * This method indicates whether tasks with this constraint type prefer to
     * start as late as possible.
     *
     * @return a <code>boolean</code> value indicating if this type of task
     * constraint wants to start as late as possible;
     */
    public boolean isLateStarter() {
        return lateStarter;
    }

    /**
     * Determine if a given object is equal to the current Task Constraint Type
     * instance.
     *
     * @param o the object to be compared.
     * @return <code>true</code> if the objects are equal, otherwise false.
     */
    public boolean equals(Object o) {
        boolean isEqual = true;

        if (!(o instanceof TaskConstraintType)) {
            isEqual = false;
        } else {
            isEqual = (((TaskConstraintType)o).getID().equals(this.getID()));
        }

        return isEqual;
    }

    /**
     * Return a valid hash code for this object.  This allows the object to be
     * looked up in a hash table with minimal required hash table buckets.
     *
     * @return a valid hash code.
     */
    public int hashCode() {
        int result;
        result = (nameToken != null ? nameToken.hashCode() : 0);
        result = 29 * result + id.hashCode();
        result = 29 * result + (isDateConstrained ? 1 : 0);
        return result;
    }
}

