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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 19514 $
|       $Date: 2009-07-13 11:21:45 -0300 (lun, 13 jul 2009) $
|     $Author: nilesh $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.gui.html.IHTMLOption;

/**
 * A typed enumeration of TaskPriority classes.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class TaskPriority implements IHTMLOption, Serializable {
    /** This list contains all of the possible types for this typed enumeration. */
    private static List types = new ArrayList();
    public static final TaskPriority PRIORITY_LOW = new TaskPriority("10", "prm.schedule.task.priority.low.name");
    public static final TaskPriority PRIORITY_NORMAL = new TaskPriority("20", "prm.schedule.task.priority.normal.name");
    public static final TaskPriority PRIORITY_HIGH = new TaskPriority("30", "prm.schedule.task.priority.high.name");
    public static final TaskPriority DEFAULT = PRIORITY_LOW;

    /**
     * Returns an unmodifiable collection of all <code>TaskPriority</code>s.
     * @return a collection where each element is a <code>TaskPriority</code>
     */
    public static Collection getAll() {
        return Collections.unmodifiableCollection(types);
    }

    /**
     * Get the TaskPriority that corresponds to a given ID.
     *
     * @param id a <code>String</code> value which is the primary key of a
     * <code>TaskPriority</code> we want to find.
     * @return a <code>TaskPriority</code> corresponding to the supplied ID, or
     * the DEFAULT <code>TaskPriority</code> if one cannot be found.
     */
    public static TaskPriority getForID(String id) {
        TaskPriority toReturn = DEFAULT;

        for (Iterator it = types.iterator(); it.hasNext();) {
            TaskPriority type = (TaskPriority)it.next();
            if (type.getID().equals(id)) {
                toReturn = type;
                break;
            }
        }

        return toReturn;
    }

    /**
     * Get the TaskPriority that corresponds to a given ID.
     *
     * @param id a <code>int</code> value which is the primary key of a
     * <code>TaskPriority</code> we want to find.
     * @return a <code>TaskPriority</code> corresponding to the supplied ID, or
     * the DEFAULT <code>TaskPriority</code> if one cannot be found.
     */
    public static TaskPriority getForID(int id) {
        return getForID(String.valueOf(id));
    }

    public static TaskPriority getForMSPID(int mspID) {
        if (mspID > 500) {
            return TaskPriority.PRIORITY_HIGH;
        } else if (mspID == 500) {
            return TaskPriority.PRIORITY_NORMAL;
        } else {
            return TaskPriority.PRIORITY_LOW;
        }
    }

    //**************************************************************************
    // Implementation code
    //**************************************************************************
    private String id;
    private String displayToken;

    /**
     * Private constructor which creates a new TaskPriority instance.
     *
     * @param id a <code>String</code> value which is a unique identifier for
     * this typed enumeration.
     * @param displayToken a <code>String</code> value which contains a token to
     * look up the display name for this type.
     */
    private TaskPriority(String id, String displayToken) {
        this.id = id;
        this.displayToken = displayToken;
        types.add(this);
    }

    /**
     * Get the unique identifier for this type enumeration.
     *
     * @return a <code>String</code> value containing the unique id for this
     * type.
     */
    public String getID() {
        return id;
    }

    /**
     * Return a human-readable display name for this TaskPriority.
     *
     * @return a <code>String</code> containing a human-readable version of this
     * TaskPriority.
     */
    public String toString() {
        return PropertyProvider.get(displayToken);
    }

    /**
     * Returns the value for the <code>value</code> attribute of the HTML
     * option.
     *
     * @return a <code>String</code> value which will become the value="?" part
     * of the option tag.
     */
    public String getHtmlOptionValue() {
        return id;
    }

    /**
     * Returns the value for the content part of the HTML option.
     *
     * @return a <code>String</code> value that will be displayed for this
     * html option.
     */
    public String getHtmlOptionDisplay() {
        return toString();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskPriority)) return false;

        final TaskPriority taskPriority = (TaskPriority) o;

        if (id != null ? !id.equals(taskPriority.id) : taskPriority.id != null) return false;

        return true;
    }
    
    /**
     * Compare this priority by parameter priority.
     * @param taskPriority
     * @return int
     */
    public int compareTo(TaskPriority taskPriority){
    	// priority IDs are numeric.
        // ID value are in same order(ascending) with priority so we can compare it simply by subtracting it.
    	return Integer.valueOf(this.id) - Integer.valueOf(taskPriority.id);
    }
    
    
    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }
}
