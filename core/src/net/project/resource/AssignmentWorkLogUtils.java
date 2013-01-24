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

 package net.project.resource;

import java.util.Iterator;
import java.util.List;

import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Utilities to help the processing of a List of AssignmentWorkLog objects.
 *
 * @author Matthew Flower
 * @since Version 8.1.2
 */
public class AssignmentWorkLogUtils {
    public static TimeQuantity getWorkLoggedForAssignee(List assignmentWorkLogEntries, String personID) {
        //Assertions are only used in development time.
        assert personID != null : "Parameter personID cannot be null";
        TimeQuantity totalWork = new TimeQuantity(0, TimeQuantityUnit.HOUR);

        for (Iterator it = assignmentWorkLogEntries.iterator(); it.hasNext();) {
            AssignmentWorkLogEntry entry = (AssignmentWorkLogEntry) it.next();

            if (personID.equals(entry.getAssigneeID())) {
                totalWork = totalWork.add(entry.getWork());
            }
        }

        return totalWork;
    }
}
