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

 package net.project.schedule.mvc.handler.taskedit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.process.ProcessBean;
import net.project.schedule.ScheduleEntry;
import net.project.space.Space;
import net.project.util.TimeQuantityUnit;

import org.apache.log4j.Logger;

/**
 * Provides helper methods for populating model.
 * <p>
 * Are we helping the model or the view?  Should this be in the view package?
 * </p>
 *
 * @author Tim Morrow
 * @since 7.6.2
 */
public class Helper {

    /**
     * Returns a collection of available phases in the specified space.
     * @param space the space for which to get phases
     * @return a collection where each element is an <code>IHTMLOption</code>
     * or an empty collection if there are none or there is a problem loading
     */
    public static Collection getPhaseOptions(Space space) {
        Collection phaseOptions = new ArrayList();

        try {
            ProcessBean process = new ProcessBean();
            process.loadProcess(space.getID());
            phaseOptions.addAll(process.getPhaseOptions(false));
        } catch (PersistenceException e) {
            // Phase options will be empty
        }

        return phaseOptions;
    }

    /**
     * Returns list of task ids which are in the current branch of tasks.<br>
     * This includes the current task and all its children, their children
     * etc.
     *
     * @param entry a <code>ScheduleEntry</code> object that we are going to
     * show in the view.  We need this so we know which id's to exclude from
     * the branch.
     * @return list of task ids or empty list if this is a new task
     * @throws net.project.persistence.PersistenceException if there is a
     * problem fetching the task ids
     */
    private static ArrayList getCurrentBranch(ScheduleEntry entry) throws PersistenceException {
        ArrayList currentBranchTasks = new ArrayList();
        StringBuffer query = new StringBuffer();

        // If this is a new Task, there are no children
        if (entry.getID() == null) {
            return currentBranchTasks;
        }

        // Lovely hierarchical SQL query, starting at current task and recursing
        // based on parent task id
        query.append("select task_id ");
        query.append("from pn_task ");
        query.append("where record_status <> 'D' ");
        query.append("start with task_id = " + entry.getID() + " ");
        query.append("connect by prior task_id = parent_task_id");

        DBBean db = new DBBean();
        try {
            db.executeQuery(query.toString());

            while (db.result.next()) {
                currentBranchTasks.add(db.result.getString("task_id"));
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(Helper.class).error("Task.getCurrentBranch() threw an SQL exception: " + sqle);
            throw new PersistenceException("Task fetch operation failed.", sqle);
        } finally {
            db.release();
        }

        return currentBranchTasks;
    }

    /**
     * Returns a collection of work units.
     * @return a collection where each element is an <code>IHTMLOption</code>
     * or an empty collection if there are none or there is a problem loading
     */
    public static Collection getWorkUnitOptions() {
        List availableUnits = new ArrayList();
        availableUnits.add(TimeQuantityUnit.HOUR);
        availableUnits.add(TimeQuantityUnit.DAY);
        availableUnits.add(TimeQuantityUnit.WEEK);
        return availableUnits;
    }

    /**
     * Returns a collection of work complete units.
     * @return a collection where each element is an <code>IHTMLOption</code>
     * or an empty collection if there are none or there is a problem loading
     */
    public static Collection getWorkCompleteUnitOptions() {
        return getWorkUnitOptions();
    }

    /**
     * Returns the units available for selection when editing duration.
     * @return a collection where each element is an <code>IHTMLOption</code>
     * or an empty collection if there are none or there is a problem loading
     */
    public static Collection getDurationUnitOptions() {
        List availableUnits = new ArrayList();
        availableUnits.add(TimeQuantityUnit.HOUR);
        availableUnits.add(TimeQuantityUnit.DAY);
        availableUnits.add(TimeQuantityUnit.WEEK);
        return availableUnits;
    }
}
