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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.project.base.property.PropertyProvider;
import net.project.gui.html.IHTMLOption;

/**
 * Typed Enumeration of statuses that a Task can take.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class TaskStatus implements IHTMLOption {
    private static final Map statuses = new LinkedHashMap();
    public static final TaskStatus STATUS_NOT_STARTED = new TaskStatus("10", "prm.schedule.task.status.notstarted.name");
    public static final TaskStatus STATUS_IN_PROGRESS = new TaskStatus("20", "prm.schedule.task.status.inprocess.name");
    public static final TaskStatus STATUS_COMPLETED = new TaskStatus("30", "prm.schedule.task.status.completed.name");
    public static final TaskStatus STATUS_CANCELLED = new TaskStatus("40", "prm.schedule.task.status.cancelled.name");
    public static final TaskStatus DEFAULT = STATUS_NOT_STARTED;

    /**
     * Returns an unmodifiable collection of <code>TaskStatus</code>s.
     * @return a collection where each element is a <code>TaskStatus</code>
     */
    public static Collection getAll() {
        return Collections.unmodifiableCollection(statuses.values());
    }

    /**
     * Get the TaskStatus that corresponds to this ID.
     *
     * @param id a <code>String</code> value which is the primary key of a
     * <code>TaskStatus</code> we want to find.
     * @return a <code>TaskStatus</code> corresponding to the supplied ID, or
     * the DEFAULT <code>TaskStatus</code> if one cannot be found.
     */
    public static TaskStatus getForID(String id) {
        TaskStatus toReturn = (TaskStatus)statuses.get(id);
        if (toReturn == null) {
            toReturn = DEFAULT;
        }

        return toReturn;
    }

    /**
     * Get the TaskStatus that corresponds to this ID.
     *
     * @param id a <code>int</code> value which is the primary key of a
     * <code>TaskStatus</code> we want to find.
     * @return a <code>TaskStatus</code> corresponding to the supplied ID, or
     * the DEFAULT <code>TaskStatus</code> if one cannot be found.
     */
    public static TaskStatus getForID(int id) {
        return getForID(String.valueOf(id));
    }

    //**************************************************************************
    // Implementation code
    //**************************************************************************
    /** A unique identifier for task statuses. */
    private String id;
    /**
     * A property which can be used to look up a human-readable name for this
     * task status.
     */
    private String displayToken;

    /**
     * Private constructor for task status.
     *
     * @param id a <code>int</code> containing a unique identifier for this
     * task status.
     * @param displayToken a <code>String</code> value containing a property
     * which returns a human-readable value for this Task status.
     */
    private TaskStatus(String id, String displayToken) {
        this.id = id;
        this.displayToken = displayToken;
        statuses.put(id, this);
    }

    /**
     * Get the unique identifier for this task status.
     *
     * @return a <code>String</code> value containing the unique id for this
     * task status.
     */
    public String getID() {
        return id;
    }

    /**
     * Get the human readable form of this task status, which is suitable for
     * display.  This value is looked up through a token that was used to
     * construct this task status, so it should be properly internationalized.
     *
     * The tokens for TaskStatus can be found in the "prm.schedule.task.status."
     * section.
     *
     * @return a <code>String</code> representation of this task status which is
     * properly internationalized.
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
}