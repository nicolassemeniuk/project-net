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
import net.project.schedule.TaskFinder;

/**
 * @author Vlad Melanchyk
 * 
 */
public class TasksStartedAfterPlannedStartFilter extends EmptyFinderFilter {

    public TasksStartedAfterPlannedStartFilter(String id) {
	super(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.project.base.finder.EmptyFinderFilter#getWhereClause()
     */
    public String getWhereClause() {
	return "(" + TaskFinder.ACTUAL_START_COLUMN.getColumnName() + " > "
		+ TaskFinder.DATE_START_COLUMN.getColumnName() + ")";
    }

}
