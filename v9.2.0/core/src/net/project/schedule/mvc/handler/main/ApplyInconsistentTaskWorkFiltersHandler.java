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
package net.project.schedule.mvc.handler.main;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.project.base.finder.FinderListener;
import net.project.base.finder.FinderListenerAdapter;
import net.project.database.DBBean;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.ScheduleEntry;
import net.project.util.ErrorReporter;
import net.project.util.TimeQuantity;

public class ApplyInconsistentTaskWorkFiltersHandler extends ApplyInconsistentFiltersHandler {

    public ApplyInconsistentTaskWorkFiltersHandler(HttpServletRequest request) {
        super(request);
    }
    
    @Override
    protected FinderListener appendInconsistentTaskFinderListener(ErrorReporter errorReporter) {

        return new FinderListenerAdapter() {
            private TimeQuantity getWorkLoggedByAssignments(ScheduleEntry entry) {
                TimeQuantity totalWorkComplete = TimeQuantity.O_HOURS;
                Collection assignments = entry.getAssignments();
                Iterator assignmentsIterator = assignments.iterator();
                while (assignmentsIterator.hasNext()) {
                    ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) assignmentsIterator.next();
                    totalWorkComplete = totalWorkComplete.add(assignment.getWorkComplete());
                }

                return totalWorkComplete;
            }

            public void postExecute(DBBean db, List listToRemoveFrom) throws SQLException {
                Iterator listToRemoveFromIterator = listToRemoveFrom.iterator();
                while (listToRemoveFromIterator.hasNext()) {
                    ScheduleEntry entry = (ScheduleEntry) listToRemoveFromIterator.next();
                    TimeQuantity work = entry.getWorkTQ();
                    TimeQuantity workCompelte = entry.getWorkCompleteTQ();
                    TimeQuantity unallocWorkComplete = entry.getUnallocatedWorkComplete();

                    // keep only those tasks whose actual work logged + unalloc work complete != work complete
                    if (unallocWorkComplete.add(getWorkLoggedByAssignments(entry)).compareTo(workCompelte) == 0) {
                        listToRemoveFromIterator.remove();
                    }
                }
            }
        };
    }

}
