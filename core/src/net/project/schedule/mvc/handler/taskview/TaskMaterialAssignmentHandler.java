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
|   $Revision: 19063 $
|       $Date: 2009-04-05 14:27:40 -0300 (dom, 05 abr 2009) $
|     $Author: nilesh $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule.mvc.handler.taskview;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentList;
import net.project.resource.AssignmentRoster;
import net.project.resource.AssignmentType;
import net.project.resource.RosterBean;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.MaterialAssignmentsHelper;
import net.project.schedule.ScheduleEntry;
import net.project.security.User;

/**
 * Class which does all of the logic required to load the TaskMaterials page.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class TaskMaterialAssignmentHandler extends AbstractTaskAssignmentHandler {

    public TaskMaterialAssignmentHandler(HttpServletRequest request) {
        super(request);
        setViewName("/schedule/TaskViewMaterial.jsp");
    }

    /**
     * Add the necessary elements to the model that are required to render a
     * view.  Often this will include things like loading variables that are
     * needed in a page and adding them to the model.
     *
     * The views themselves should not be doing any loading from the database.
     * The whole reason for an mvc architecture is to avoid that.  All loading
     * should occur in the handler.
     *
     * @param request the <code>HttpServletRequest</code> that resulted from the
     * user submitting the page.
     * @param response the <code>HttpServletResponse</code> that will allow us
     * to pass information back to the user.
     * @return a <code>Map</code> which is the updated model.
     * @throws java.lang.Exception if any error occurs.
     */
    @SuppressWarnings("unchecked")
	public Map<String, Object> handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = super.handleRequest(request, response);
        ScheduleEntry scheduleEntry = (ScheduleEntry)model.get("scheduleEntry");

        //HAY QUE PONER UN ROSTER DE MATERIALES materialRoster
        //La bandera de sobreutilizacion no debe estar porque no podemos sobreutilizar un material
        AssignmentRoster assignmentRoster = loadAssignmentRoster(scheduleEntry);
        boolean overAllocatedResourcesExist = getOverallocationFlag(scheduleEntry, assignmentRoster);

        //Put the assignment roster in the session so the worker (which updates
        //the page in real time) doesn't have to do any database access.
        request.getSession().setAttribute("assignmentRoster", assignmentRoster);
        
        //Put some variables in the model so the page can access them.
        model.put("assignments", assignmentManager.getAssignments());
        model.put("assignmentMap", assignmentManager.getAssignmentMap());
        model.put("overallocatedResourcesExist", Boolean.valueOf(overAllocatedResourcesExist));
        model.put("assignmentRoster", assignmentRoster);

        return model;
    }

    @SuppressWarnings("rawtypes")
	private boolean getOverallocationFlag(ScheduleEntry scheduleEntry, AssignmentRoster assignmentRoster) {
        boolean overAllocatedResourcesExist = false;
        AssignmentList al = scheduleEntry.getAssignmentList();
        Map assignmentMap = assignmentRoster.getPersonMap();

        for (Iterator it = al.iterator(); it.hasNext();) {
            ScheduleEntryAssignment assignment = (ScheduleEntryAssignment)it.next();
            AssignmentRoster.Person p =
                (AssignmentRoster.Person)assignmentMap.get(assignment.getPersonID());
            if (p.getMaxAllocatedDecimal().compareTo(new BigDecimal("1.00")) > 0) {
                overAllocatedResourcesExist = true;
                break;
            }
        }
        return overAllocatedResourcesExist;
    }

    private AssignmentRoster loadAssignmentRoster(ScheduleEntry scheduleEntry) throws PersistenceException {
        User user = (User)getSessionVar("user");

        //Determine if there are any over-allocated resources
        AssignmentRoster assignmentRoster = new AssignmentRoster();
        assignmentRoster.setDateStart(scheduleEntry.getStartTime());
        assignmentRoster.setDateEnd(scheduleEntry.getEndTime());
        assignmentRoster.setSpace(user.getCurrentSpace());
        assignmentRoster.setObjectID(scheduleEntry.getID(), AssignmentType.TASK.getObjectType());
        assignmentRoster.load();
        return assignmentRoster;
    }
}
