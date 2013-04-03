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

 package net.project.schedule.mvc.handler.taskcalculate;

import java.math.BigDecimal;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.project.base.mvc.ControllerException;
import net.project.hibernate.service.ServiceFactory;
import net.project.material.MaterialAssignment;
import net.project.persistence.PersistenceException;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskEndpointCalculation;
import net.project.schedule.TaskList;
import net.project.schedule.calc.ScheduleEntryCalculator;
import net.project.security.User;
import net.project.util.ErrorReporter;
import net.project.util.Validator;

/**
 * Provides a handler invoked when an material is added or removed from
 * a schedule entry, providing a Javascript view of the model.
 * <p>
 * This is designed to be a synchronous round-trip called from Javascript.
 * </p>
 *
 */
public class MaterialAssignmentAddRemoveHandler extends AbstractMaterialAssignmentChangeHandler {

    public MaterialAssignmentAddRemoveHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * Invokes a task calculation for adding or removing a material assignment.
     * <p>
     * Expects the following request attributes:
     * <ul>
     * <li>mode - one of <code>add</code> or <code>remove</code>
     * <li>materialID - the id of the material being added or removed
     * </ul>
     */
	@Override
	protected void doHandleRequest(HttpServletRequest request, Schedule schedule, ScheduleEntry scheduleEntry, String materialId, ErrorReporter errorReporter)
			throws ControllerException {
        // Determine whether being added or removed
        User user = (User)getSessionVar("user");
        
        
        String mode = request.getParameter("mode");
        
        if (Validator.isBlankOrNull(mode)) {
            throw new ControllerException("Missing request parameter mode");
        }
        boolean isAddAssignmentMaterial = mode.equals("add");

        // Recalculate task
        ScheduleEntryCalculator calc = new ScheduleEntryCalculator(scheduleEntry, schedule.getWorkingTimeCalendarProvider());

        if (isAddAssignmentMaterial) {
        	
        	if(calc.existsMaterialAssignment(materialId))
        	{
        		MaterialAssignment assignment = calc.getMaterialAssignment(materialId);
                assignment.setRecordStatus("A");        		
                assignment.setModifiedBy(user.getID());
                assignment.setModifiedDate(new Date());        		
                assignment.setAssignorId(user.getID());
        	}
        	else
        	{
                // Doesn't exists, create a new assignment
            	MaterialAssignment assignment = new MaterialAssignment();
                assignment.setSpaceId(scheduleEntry.getSpaceID());
                assignment.setObjectId(scheduleEntry.getID());
            	assignment.setMaterialId(materialId);
                assignment.setPercentAssigned(BigDecimal.valueOf(100.00));
                assignment.setRecordStatus("A");
                assignment.setStartDate(scheduleEntry.getStartTime());
                assignment.setEndDate(scheduleEntry.getEndTime());
                assignment.setDateCreated(new Date());
                assignment.setModifiedBy(user.getID());
                assignment.setModifiedDate(new Date());
                assignment.setAssignorId(user.getID());
                
                //Check if this assignment is in conflict with existing assignments.
                assignment.setOverassigned(ServiceFactory.getInstance().getPnMaterialAssignmentService().isOverassigned(assignment.getStartDate(), assignment.getEndDate(), assignment.getSpaceId(), assignment.getMaterialId()));            
                
                calc.assignmentMaterialAdded(assignment);        		
        	}

        } else {                                                
            // When removing, the assignment must be in the list of assignments
        	MaterialAssignment assignment = scheduleEntry.getMaterialAssignments().getAssignedMaterial(materialId);
        	assignment.setRecordStatus("D");
            assignment.setModifiedBy(user.getID());        	
        	assignment.setModifiedDate(new Date());
            //calc.assignmentMaterialRemoved(assignment);
        }

        try {
            //Make a copy of the schedule and replace the build in schedule entry
            //with the one we're modifying.  Then we can do a recalculation to
            //get dates
            Schedule newSchedule = (Schedule)schedule.clone();
            TaskList tl = newSchedule.getTaskList();
            tl.remove(scheduleEntry.getID());
            tl.add(scheduleEntry);

            new TaskEndpointCalculation().recalculateTaskTimesNoLoad(newSchedule);
        } catch (PersistenceException e) {
            // No loading should ever be occurring; the problem is with methods
            // That optionally load, they optionally throw a PersistenceException
            throw (IllegalStateException) new IllegalStateException("PersistenceException occurred when no persistence operations were expected").initCause(e);
        }
		
	}

}
