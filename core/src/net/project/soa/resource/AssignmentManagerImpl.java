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
package net.project.soa.resource;

import net.project.resource.AssignmentManager;
import net.project.resource.AssignmentStatus;
import net.project.resource.ScheduleEntryAssignment;
import net.project.resource.filters.assignments.CurrentAssignmentsFilter;
import net.project.security.SessionManager;

public class AssignmentManagerImpl extends AssignmentManager implements IAssignmentManager {

	public ScheduleEntryAssignment[] getCurrentAssignments() throws Exception {
		    AssignmentManager assignmentManager = new AssignmentManager();
		    assignmentManager.setPersonID(SessionManager.getUser().getID());
		    assignmentManager.addFilter(new CurrentAssignmentsFilter("currentAssignments"));
		    Object[] arr = assignmentManager.getAssignments().toArray();
		    ScheduleEntryAssignment[] assignments = new ScheduleEntryAssignment[arr.length];
		    for(int i=0;i<arr.length;i++){
		    	if( arr[i] instanceof ScheduleEntryAssignment)
		    		assignments[i] = (ScheduleEntryAssignment)arr[i];
		    }
		    return assignments;
	}

	public ScheduleEntryAssignment[] getAssignedAssignments() throws Exception {
	    AssignmentManager assignmentManager = new AssignmentManager();
	    assignmentManager.setStatusFilter(AssignmentStatus.ASSIGNED);
	    assignmentManager.setPersonID(SessionManager.getUser().getID());
	    assignmentManager.loadAssignments();
	    Object[] arr = assignmentManager.getAssignments().toArray();
	    ScheduleEntryAssignment[] assignments = new ScheduleEntryAssignment[arr.length];
	    for(int i=0;i<arr.length;i++){
	    	if( arr[i] instanceof ScheduleEntryAssignment)
	    		assignments[i] = (ScheduleEntryAssignment)arr[i];
	    }
	    return assignments;
	}	
}
