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
import net.project.gui.html.IHTMLOption;

/**
 * This class contains all of the possible dependency types that a task dependency
 * can have.
 */
public class TaskDependencyType implements Serializable, IHTMLOption {
    //--------------------------------------------------------------------------
    //Static TaskDependencyType implementation
    //--------------------------------------------------------------------------

    //List of all available TaskDependency types
    private static ArrayList taskDependencyList = new ArrayList();

    //All dependencies which do not explicitly indicate a dependency type will
    //have this type.
    /** This type links the end date of a task with the start date of its dependency. */
    public static TaskDependencyType FINISH_TO_START = new TaskDependencyType("1", "prm.schedule.task.dependencytype.finishtostart.name", "prm.schedule.task.dependencytype.finishtostart.abbr");
    /** This type links the start date of a task with the start date of its dependency. */
    public static TaskDependencyType START_TO_START = new TaskDependencyType("2", "prm.schedule.task.dependencytype.starttostart.name", "prm.schedule.task.dependencytype.starttostart.abbr");
    /** This type links the end date of a task with the end date of its dependency. */
    public static TaskDependencyType FINISH_TO_FINISH = new TaskDependencyType("3", "prm.schedule.task.dependencytype.finishtofinish.name", "prm.schedule.task.dependencytype.finishtofinish.abbr");
    /** This type links the start date of a task with the end date of its dependency. */
    public static TaskDependencyType START_TO_FINISH = new TaskDependencyType("4", "prm.schedule.task.dependencytype.starttofinish.name", "prm.schedule.task.dependencytype.starttofinish.abbr");
    /**
     * This is the default task dependency type that will be assigned to any task
     * dependency which does not explicitly define a task dependency type.  This will
     * also be the default selection in a html option list of task dependencies which
     * does not explicitly specify which dependency to select.
     */
    public static TaskDependencyType DEFAULT = FINISH_TO_START;

    /**
     * Get the task dependency that corresponds to the database id supplied as a
     * parameter.
     *
     * @param id The ID of the TaskDependency that we want to look up.
     * @return A TaskDependencyType which corresponds to the ID supplied as a parameter.
     */
    public static TaskDependencyType getForID(String id) {
        TaskDependencyType taskDependencyToReturn = DEFAULT;
        Iterator it = taskDependencyList.iterator();

        while (it.hasNext()) {
            TaskDependencyType testDependencyType = (TaskDependencyType)it.next();

            if (testDependencyType.getID().equals(id)) {
                taskDependencyToReturn = testDependencyType;
            }
        }

        return taskDependencyToReturn;
    }

    public static TaskDependencyType getForMSPID(int id) {
        switch (id) {
            case 0:
                return FINISH_TO_FINISH;
            case 1:
                return FINISH_TO_START;
            case 2:
                return START_TO_FINISH;
            case 3:
                return START_TO_START;
            default:
                throw new RuntimeException("No matching dependency found.");
        }
    }

    public static int convertToMSPID(int id){
        switch (id) {
        case 3:
            return 0;
        case 1:
            return 1;
        case 4:
            return 2;
        case 2:
            return 3;
        default:
            throw new RuntimeException("No matching MSPID found.");
    }
    }
    
    /**
     * Return a List of IHTMLOption objects corresponding to the possible
     * <code>TaskDependencyType</code> objects.
     *
     * @return a <code>List</code> containing <code>IHTMLOption</code> objects.
     */
    public static List getDependencyTypeOptionList() {
        return new ArrayList(taskDependencyList);
    }

    //--------------------------------------------------------------------------
    //Instantiable TaskDependencyType implementation
    //--------------------------------------------------------------------------
    private String id;
    private String nameToken;
    private String abbreviationToken;

    private TaskDependencyType(String taskDependencyID, String taskDependencyNameToken, String abbreviationToken) {
        id = taskDependencyID;
        nameToken = taskDependencyNameToken;
        this.abbreviationToken = abbreviationToken;

        //Add this value to the list of all available task dependencies
        taskDependencyList.add(this);
    }

    /**
     * Gets the ID for this task dependency type.
     *
     * @return The ID for the current task dependency.
     */
    public String getID() {
        return id;
    }

    /**
     * Get the internationalized name of this TaskDependencyType.
     *
     * @return The name of the current task dependency.  For example, "Finish To
     * Start" or "Start to Finish".  This value is internationalized, so it
     * should change according to whatever language you currently have set.
     */
    public String getName() {
        return PropertyProvider.get(nameToken);
    }

    /**
     * Get the internationalized abbreviation for this task dependency type.
     *
     * @return a <code>String</code> containing the abbreviation for this
     * task dependency type.
     */
    public String getAbbreviation() {
        return PropertyProvider.get(abbreviationToken);
    }

    public boolean equals(Object o) {
        boolean isEqual = true;

        if (!(o instanceof TaskDependencyType)) {
            isEqual = false;
        } else {
            isEqual = (((TaskDependencyType)o).getID().equals(this.getID()));
        }

        return isEqual;
    }

    public String getHtmlOptionValue() {
        return getID();
    }

    public String getHtmlOptionDisplay() {
        return getName();
    }
}
