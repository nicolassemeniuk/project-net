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

import java.util.Date;

import net.project.base.finder.EmptyFinderFilter;
import net.project.schedule.TaskFinder;
import net.project.util.DateUtils;

/**
 * This filter shows task that have started in the past and have 0% Work
 * complete (0% + today() > start date).
 * 
 * @author Vlad Melanchyk
 * 
 */
public class TasksThatShouldHaveStartedFilter extends EmptyFinderFilter {

    public TasksThatShouldHaveStartedFilter(final String id) {
	super(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.project.base.finder.EmptyFinderFilter#getWhereClause()
     */
    public final String getWhereClause() {

	return "(((" + TaskFinder.IS_MILESTONE.getColumnName() + " = 1 and "
		+ TaskFinder.WORK_COLUMN.getColumnName() + " = 0 and "
		+ TaskFinder.PERCENT_COMPLETE_COLUMN.getColumnName()
		+ " = 0) or ("
		+ TaskFinder.WORK_PERCENT_COMPLETE_COLUMN.getColumnName()
		+ " = 0))" + " and "
		+ TaskFinder.DATE_START_COLUMN.getColumnName() + " < "
		+ DateUtils.getDatabaseDateString(new Date()) + ")";
    }
}
