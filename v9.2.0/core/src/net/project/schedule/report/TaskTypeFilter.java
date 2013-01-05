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

 package net.project.schedule.report;

import net.project.base.finder.EmptyFinderFilter;
import net.project.schedule.TaskType;
import net.project.util.Validator;

/**
 * This filter allows you to load either tasks are milestones.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class TaskTypeFilter extends EmptyFinderFilter {
    private boolean loadMilestones = true;
    private boolean loadTasks = true;

    /**
     * Standard constructor.
     *
     * @param id a <code>String</code> which contains a unique identifier for
     * this filter.
     */
    public TaskTypeFilter(String id) {
        super(id);
    }

    /**
     * Get the where clause for an empty finder filter, which should always be
     * empty.
     *
     * @return a <code>String</code> value which should always be empty.
     */
    public String getWhereClause() {
        String sql = "";

        if (loadMilestones && loadTasks) {
            sql = "";
        } else if (loadMilestones) {
            sql = "(t.is_milestone = 1)";
        } else if (loadTasks) {
            sql = "(((t.task_type = '"+TaskType.SUMMARY.getID()+"') or" +
                "(t.task_type = '"+TaskType.TASK.getID()+"')) and t.is_milestone=0)";
        }

        return sql;
    }

    /**
     * Indicates if we're going to load milestones.
     *
     * @return a <code>Boolean</code> indicating if we're going to load
     * milestones.
     */
    public boolean isLoadMilestones() {
        return loadMilestones;
    }

    /**
     * Indicates whether we are going to load milestones.
     *
     * @param loadMilestones a <code>boolean</code> indicating whether we are
     * going to load milestones.
     */
    public void setLoadMilestones(boolean loadMilestones) {
        this.loadMilestones = loadMilestones;
    }

    /**
     * Indicates that we are going to load tasks.
     *
     * @return a <code>Boolean</code> indicating if we're going to load tasks.
     */
    public boolean isLoadTasks() {
        return loadTasks;
    }

    /**
     * Indicate if we're going to load tasks.
     *
     * @param loadTasks a <code>boolean</code> indicating if we're going to load
     * tasks.
     */
    public void setLoadTasks(boolean loadTasks) {
        this.loadTasks = loadTasks;
    }

    public String getFilterDescription() {
        String description = "";
        if (loadMilestones) {
            description += "Load Milestones";
        }
        if (loadTasks) {
            if (!Validator.isBlankOrNull(description)) {
                description += " and ";
            }
            description += "tasks";
        }

        return description; 
    }
}
